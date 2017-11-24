package com.github.projectrake.dbm;

import com.github.projectrake.dbm.api.v1.DatabaseInterface;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Created on 18.11.2017.
 */
class UntrackedDatabaseInterface extends DatabaseInterface {
    private ExecutorService executorService;
    private Configuration jooqConfiguration;

    public UntrackedDatabaseInterface(ExecutorService executorService, Configuration jooqConfiguration) {
        this.executorService = Objects.requireNonNull(executorService);
        this.jooqConfiguration = Objects.requireNonNull(jooqConfiguration);
    }

    @Override
    protected DSLContext getDSLContext() {
        return DSL.using(getJooqConfiguration());
    }

    @Override
    protected ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public Configuration getJooqConfiguration() {
        return jooqConfiguration;
    }
}
