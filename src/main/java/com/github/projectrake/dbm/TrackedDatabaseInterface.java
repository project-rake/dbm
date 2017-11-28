package com.github.projectrake.dbm;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.TransactionalCallable;
import org.jooq.TransactionalRunnable;

import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

/**
 * Created on 18.11.2017.
 */
class TrackedDatabaseInterface extends UntrackedDatabaseInterface {
    private MetricRegistry metrics;
    private String name;

    private Counter runQueryCounter;
    private Counter callQueryCounter;
    private Timer runQueryTimer;
    private Timer callQueryTimer;

    private Counter asyncRunQueryCounter;
    private Counter asyncCallQueryCounter;
    private Timer asyncRunQueryTimer;
    private Timer asyncCallQueryTimer;
    private Timer asyncRunQueryTimerToExec;
    private Timer asyncCallQueryTimerToExec;


    public TrackedDatabaseInterface(ExecutorService executorService, Configuration jooqConfiguration, MetricRegistry metrics, String name) {
        super(executorService, jooqConfiguration);
        this.metrics = Objects.requireNonNull(metrics);
        this.name = Objects.requireNonNull(name);

        runQueryCounter = metrics.counter("dbm#" + name + "#runQueryCounter");
        callQueryCounter = metrics.counter("dbm#" + name + "#callQueryCounter");
        runQueryTimer = metrics.timer("dbm#" + name + "#runQueryTimer");
        callQueryTimer = metrics.timer("dbm#" + name + "#callQueryTimer");

        asyncRunQueryCounter = metrics.counter("dbm#" + name + "#asyncRunQueryCounter");
        asyncCallQueryCounter = metrics.counter("dbm#" + name + "#asyncCallQueryCounter");
        asyncRunQueryTimer = metrics.timer("dbm#" + name + "#asyncRunQueryTimer");
        asyncCallQueryTimer = metrics.timer("dbm#" + name + "#asyncCallQueryTimer");

        asyncRunQueryTimerToExec = metrics.timer("dbm#" + name + "#asyncRunQueryTimerToExec");
        asyncCallQueryTimerToExec = metrics.timer("dbm#" + name + "#asyncCallQueryTimerToExec");
    }

    @Override
    public void runQuery(TransactionalRunnable runnable)  {
        runQueryCounter.inc();
        Timer.Context ctx = runQueryTimer.time();
        try {
            super.runQuery(runnable);
        } finally {
            ctx.stop();
        }
    }

    @Override
    public <T> T callQuery(TransactionalCallable<T> transactionalCallable)  {
        callQueryCounter.inc();
        Timer.Context ctx = callQueryTimer.time();
        T value;
        try {
            value = super.callQuery(transactionalCallable);
        } finally {
            ctx.stop();
        }

        return value;
    }

    @Override
    public void asyncRunQuery(TransactionalRunnable runnable)  {
        asyncRunQueryCounter.inc();
        Timer.Context rctx = asyncRunQueryTimerToExec.time();
        super.asyncRunQuery((conf) -> {
            rctx.stop();
            Timer.Context ctx = asyncRunQueryTimer.time();
            try {
                runnable.run(conf);
            } finally {
                ctx.stop();
            }
        });
    }

    @Override
    public <T> CompletionStage<T> asyncCallQuery(TransactionalCallable<T> transactionalCallable)  {
        asyncCallQueryCounter.inc();
        Timer.Context rctx = asyncCallQueryTimerToExec.time();
        return super.asyncCallQuery((conf) -> {
            rctx.stop();
            Timer.Context ctx = asyncCallQueryTimer.time();
            T value;
            try {
                value = transactionalCallable.run(conf);
            } finally {
                ctx.stop();
            }

            return value;
        });
    }

    @Override
    protected DSLContext getDSLContext() {
        metrics.counter("dbm#" + name + "#DSL-Constructed").inc();
        return super.getDSLContext();
    }

    @Override
    protected ExecutorService getExecutorService() {
        metrics.counter("dbm#" + name + "#Executors-Used").inc();
        return super.getExecutorService();
    }
}
