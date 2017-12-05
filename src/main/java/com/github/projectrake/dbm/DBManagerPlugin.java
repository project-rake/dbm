package com.github.projectrake.dbm;

import com.codahale.metrics.MetricRegistry;
import com.github.projectrake.dbm.api.v1.DBMPlugin;
import com.github.projectrake.dbm.api.v1.DatabaseInterface;
import com.github.projectrake.dbm.api.v1.SQLScript;
import com.github.projectrake.dbm.jooq.Tables;
import com.github.projectrake.dbm.jooq.tables.records.DbmVersionsRecord;
import com.github.projectrake.dbm.updatescripts.UpdateGraph;
import com.github.projectrake.dbm.updatescripts.UpdateGraphFactory;
import com.github.projectrake.dbm.updatescripts.UpdateScript;
import com.zaxxer.hikari.pool.HikariPool;
import org.bukkit.plugin.java.JavaPlugin;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultConfiguration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created on 17.11.2017.
 * <p>
 * Database manager bukkit/spigot plugin.
 */
public class DBManagerPlugin extends JavaPlugin implements DBMPlugin {
    private static DBManagerPlugin instance;
    private DBMConfiguration config;
    private MetricRegistry metrics;
    private ExecutorService executorService;
    private HikariPool pool;
    private Configuration jooqConfiguration;
    private DatabaseInterface dbi;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        CustomClassLoaderConstructor constr = new CustomClassLoaderConstructor(getClassLoader());
        Yaml yaml = new Yaml(constr);

        config = yaml.loadAs(getConfig().saveToString(), DBMConfiguration.class);
        executorService = Executors.newWorkStealingPool(config.getHikariConfig().getMaximumPoolSize());
        pool = new HikariPool(config.getHikariConfig());
        jooqConfiguration = prepareJooqConfiguration(config, pool);

        if (config.isEnableMetrics()) {
            getLogger().info("Metrics enabled.");
            metrics = new MetricRegistry();
            config.getHikariConfig().setMetricRegistry(metrics);
            pool.setMetricRegistry(metrics);
            dbi = new TrackedDatabaseInterface(executorService, jooqConfiguration, metrics, "dbm");
        } else {
            dbi = new UntrackedDatabaseInterface(executorService, jooqConfiguration);
        }

        register(this);
    }

    private static Configuration prepareJooqConfiguration(DBMConfiguration config, HikariPool pool) {
        DefaultConfiguration conf = new DefaultConfiguration();
        conf.setConnectionProvider(new ConnectionProvider() {
            @Override
            public Connection acquire() throws DataAccessException {
                try {
                    return pool.getConnection();
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage(), e);
                }
            }

            @Override
            public void release(Connection connection) throws DataAccessException {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage(), e);
                }
            }
        });

        conf.setSQLDialect(config.getSqlDialect());
        Settings settings = new Settings();
        settings.setRenderNameStyle(RenderNameStyle.UPPER);
        settings.setRenderSchema(false);
        conf.setSettings(settings);

        return conf;
    }

    @Override
    public void onDisable() {
        executorService.shutdown();
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            getLogger().warning("HikariPool shutdown threw " + e.getMessage());
        }
        instance = null;
    }

    /**
     * Registers a new plugin with the database manager, resolves the update script dependencies, runs the dbm
     * for the plugin and sets the plugin database interface. If any of these steps fail, a {@link Throwable} is thrown.
     * All database operations are all-or-nothing to prevent corruption.
     *
     * @param plugin Plugin to register.
     * @param <T>    Type constraint for the plugins to register.
     */
    public <T extends JavaPlugin & DBMPlugin> void register(T plugin) {
        registerWithoutContext(plugin, jooqConfiguration, metrics, executorService, getLogger(), config, dbi, pool);
    }

    private static <T extends JavaPlugin & DBMPlugin> void registerWithoutContext(T plugin, Configuration jooqConfiguration, MetricRegistry metrics, ExecutorService executorService, Logger logger, DBMConfiguration config, DatabaseInterface dbi, HikariPool pool) {
        final String pluginName = plugin.getName();
        Objects.requireNonNull(pluginName);
        logger.info("Registering plugin for database access: " + pluginName);

        processUpdates(plugin, dbi, logger, pool, config.getSqlDialect());

        if (config.isEnableMetrics()) {
            plugin.setDatabaseInterface(new TrackedDatabaseInterface(executorService, jooqConfiguration, metrics, pluginName));
        } else {
            plugin.setDatabaseInterface(new UntrackedDatabaseInterface(executorService, jooqConfiguration));
        }
    }

    private static <T extends JavaPlugin & DBMPlugin> void processUpdates(T plugin, DatabaseInterface dbi, Logger log, HikariPool pool, SQLDialect dialect) {
        List<SQLScript> scripts = plugin.getScripts();
        List<UpdateScript> uscripts = resolveScripts(scripts, Objects.requireNonNull(dialect));
        int maxVersion = uscripts.stream().flatMap(s -> s.getHeader().getTo().stream()).mapToInt(s -> s.intValue()).max()
                .orElseThrow(() -> new IllegalStateException("Cannot find any versions to upgrade to."));

        int currentVersion = getCurrentPluginVersion(plugin, dbi);
        if (currentVersion != maxVersion) {
            log.info("Searching path from " + currentVersion + " to " + maxVersion);

            UpdateGraph graph = new UpdateGraphFactory().constructDependencyGraph(uscripts);

            if (!graph.hasPath(currentVersion, maxVersion)) {
                throw new IllegalStateException("Unable to construct update path from " + currentVersion + " to " + maxVersion + ".");
            } else {
                log.info("Constructed update path for " + plugin.getName());
                String script = graph.getUpdateScript(currentVersion, maxVersion);
                String SEPERATOR = IntStream.range(0, 80).mapToObj(v -> "-").reduce("", (a, b) -> a + b);
                System.out.println(
                        "\n" +
                                SEPERATOR + "\n"
                                + "-- CONSTRUCTED UPDATE SCRIPT\n"
                                + SEPERATOR + "\n"
                                + script + "\n"
                                + SEPERATOR + "\n"
                                + SEPERATOR + "\n"
                                + "\n"
                                + "-- Stop the server now if you don't want to run this."
                );

                try {
                    Thread.sleep(5000);
                    String fname = plugin.getName() + ".dbm_update_script.latest.sql";
                    try (FileOutputStream out = new FileOutputStream(fname)) {
                        out.write(script.getBytes("UTF-8"));
                    }
                    System.out.println("Dumped to \"" + fname + "\"");
                    try (Connection con = pool.getConnection()) {
                        new ScriptRunner(con, false, true).runScript(new StringReader(script));
                        con.commit();
                        System.out.println("Update script run.");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            dbi.rqd(ctx -> {
                if (currentVersion == -1) {
                    DbmVersionsRecord rec = new DbmVersionsRecord(plugin.getName(), maxVersion, new Timestamp(System.currentTimeMillis()));
                    ctx.insertInto(Tables.DBM_VERSIONS).set(rec).execute();
                } else {
                    ctx.update(Tables.DBM_VERSIONS).set(Tables.DBM_VERSIONS.VERSION, maxVersion)
                            .where(Tables.DBM_VERSIONS.PLUGIN_NAME.eq(plugin.getName()))
                            .execute();
                }
            });
        }
    }

    private static <T extends JavaPlugin & DBMPlugin> int getCurrentPluginVersion(T plugin, DatabaseInterface dbi) {
        try {
            return dbi.cqd(ctx -> ctx.select(Tables.DBM_VERSIONS.VERSION).from(Tables.DBM_VERSIONS).where(Tables.DBM_VERSIONS.PLUGIN_NAME.eq(plugin.getName())).fetchOne().value1());
        } catch (Throwable t) {
            t.printStackTrace();
            return -1;
        }
    }

    /**
     * Returns the instance of this plugin. If this plugin has not been initialized, this will return null.
     *
     * @return The instance of this plugin.
     */
    public static DBManagerPlugin getInstance() {
        return instance;
    }

    /**
     * Returns the instance of this plugin. If this plugin has not been initialized, this will return null.
     *
     * @return The instance of this plugin.
     */
    public static DBManagerPlugin getInstanceChecked() {
        return Objects.requireNonNull(instance, "Instance of DBManagerPlugin NOT INITIALIZED.");
    }

    @Override
    public void setDatabaseInterface(DatabaseInterface dbinterface) {
        dbi = dbinterface;
    }

    public static List<UpdateScript> resolveScripts(List<SQLScript> scripts, SQLDialect dialect) {
        return scripts.stream().map(s -> {
            try {
                return new UpdateScript(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
                .filter(s -> s.getHeader().getDialect().stream().anyMatch(q -> dialect.getNameLC().equalsIgnoreCase(q)))
                .collect(Collectors.toList());
    }
}
