package com.github.projectrake.dbm;

import com.zaxxer.hikari.HikariConfig;
import org.jooq.Configuration;
import org.jooq.SQLDialect;

/**
 * Created on 17.11.2017.
 */
public class DBMConfiguration {
    private boolean enableMetrics;
    private SQLDialect sqlDialect;
    private HikariConfig hikariConfig;

    public DBMConfiguration() {
    }

    public DBMConfiguration(boolean enableMetrics, HikariConfig hikariConfig) {
        this.enableMetrics = enableMetrics;
        this.hikariConfig = hikariConfig;
    }

    public SQLDialect getSqlDialect() {
        return sqlDialect;
    }

    public void setSqlDialect(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    public boolean isEnableMetrics() {
        return enableMetrics;
    }
    public boolean getEnabledMetrics() {
        return enableMetrics;
    }

    public void setEnableMetrics(boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

    public void setHikariConfig(HikariConfig hikariConfig) {
        this.hikariConfig = hikariConfig;
    }

    @Override
    public String toString() {
        return "DBMConfiguration{" +
                "enableMetrics=" + enableMetrics +
                ", hikariConfig=" + hikariConfig +
                '}';
    }
}
