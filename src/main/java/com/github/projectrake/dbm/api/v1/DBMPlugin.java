package com.github.projectrake.dbm.api.v1;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created on 17.11.2017.
 * <p>
 * Interface implementing the minimal set of methods needed by the dbm plugin to handle plugins.
 */
public interface DBMPlugin {
    /**
     * Returns the name of the plugin.
     *
     * @return Name of the plugin.
     */
    String getName();

    /**
     * Set the database interface for this plugin.
     *
     * @param dbinterface A newly constructed {@link DatabaseInterface} for this plugin to use.
     */
    void setDatabaseInterface(DatabaseInterface dbinterface);

    /**
     * Returns a list of SQLScripts following the dbm format for construction of update / init scripts.
     *
     * @return List of SQLScripts following the dbm format for construction of update / init scripts.
     */
    default List<SQLScript> getScripts() {
        String file = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        if (!file.isEmpty()) {
            try (JarFile jar = new JarFile(file)) {
                LinkedList<SQLScript> list = new LinkedList<>();

                Enumeration<JarEntry> it = jar.entries();

                while (it.hasMoreElements()) {
                    JarEntry entry = it.nextElement();

                    if (entry.getName().endsWith(".sql")) {
                        list.add(new SQLScript(file, entry.getName()));
                    }
                }
                return list;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return Collections.emptyList();
    }
}
