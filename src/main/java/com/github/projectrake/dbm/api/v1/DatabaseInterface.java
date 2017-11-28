package com.github.projectrake.dbm.api.v1;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.TransactionalCallable;
import org.jooq.TransactionalRunnable;
import org.jooq.impl.DSL;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

/**
 * Created on 17.11.2017.
 * <p>
 * Database interface abstraction class.
 * <p>
 * Do not use the asynchronous methods to do big calculations while holding a connection.
 */
public abstract class DatabaseInterface {
    /**
     * Asynchronous query to database with return. This submits the query to the execution service for asynchronous execution.
     * Handling of submitted tasks may be out of order.
     *
     * @param transactionalCallable Transaction to run.
     * @param <T>                   Return type.
     * @return Returns a completion stage for the provided transaction.
     * @ Thrown on error.
     */
    public <T> CompletionStage<T> asyncCallQuery(TransactionalCallable<T> transactionalCallable) {
        return getDSLContext().transactionResultAsync(getExecutorService(), transactionalCallable);
    }

    /**
     * Query database with return.
     *
     * @param transactionalCallable This submits the query synchronously and waits for termination.
     * @param <T>                   Return type.
     * @return Returns an object of type T.
     * @ Thrown on error.
     */
    public <T> T callQuery(TransactionalCallable<T> transactionalCallable) {
        return getDSLContext().transactionResult(transactionalCallable);
    }

    /**
     * Query database without return.
     *
     * @param runnable Runnable to execute.
     * @ Thrown on error.
     */
    public void runQuery(TransactionalRunnable runnable) {
        getDSLContext().transaction(runnable);
    }

    /**
     * Asynchronous query to database with return. This submits the query to the execution service for asynchronous execution.
     * Handling of submitted tasks may be out of order.
     *
     * @param runnable
     * @
     */
    public void asyncRunQuery(TransactionalRunnable runnable) {
        getDSLContext().transactionAsync(getExecutorService(), runnable);
    }

    //Short hands

    /**
     * {@link #asyncCallQuery(TransactionalCallable)}
     */
    public <T> CompletionStage<T> acq(TransactionalCallable<T> transactionalCallable) {
        return asyncCallQuery(transactionalCallable);
    }

    /**
     * {@link #callQuery(TransactionalCallable)}
     */
    public <T> T cq(TransactionalCallable<T> transactionalCallable) {
        return callQuery(transactionalCallable);
    }

    /**
     * {@link #runQuery(TransactionalRunnable)}
     */
    public void rq(TransactionalRunnable runnable) {
        runQuery(runnable);
    }

    /**
     * {@link #asyncRunQuery(TransactionalRunnable)}
     */
    public void arq(TransactionalRunnable runnable) {
        asyncRunQuery(runnable);
    }

    /**
     * {@link #asyncCallQuery(TransactionalCallable)} with DSLContext passed instead of a configuration
     */
    public <T> CompletionStage<T> acqd(DSLTransactionCallable<T> transactionalCallable) {
        return asyncCallQuery((conf) -> transactionalCallable.run(DSL.using(conf)));
    }

    /**
     * {@link #callQuery(TransactionalCallable)} with DSLContext passed instead of a configuration
     */
    public <T> T cqd(DSLTransactionCallable<T> transactionalCallable) {
        return callQuery((conf) -> transactionalCallable.run(DSL.using(conf)));
    }

    /**
     * {@link #runQuery(TransactionalRunnable)} with DSLContext passed instead of a configuration
     */
    public void rqd(DSLTransactionRunnable runnable) {
        runQuery((conf) -> runnable.run(DSL.using(conf)));
    }

    /**
     * {@link #asyncRunQuery(TransactionalRunnable)} with DSLContext passed instead of a configuration
     */
    public void arqd(DSLTransactionRunnable runnable) {
        asyncRunQuery((conf) -> runnable.run(DSL.using(conf)));
    }

    /**
     * Constructs a new {@link DSLContext}.
     *
     * @return {@link DSLContext}
     */
    protected abstract DSLContext getDSLContext();

    /**
     * Returns the executor service to be used by this interface.
     *
     * @return Executor service to be used.
     */
    protected abstract ExecutorService getExecutorService();

    /**
     * Returns the current configuration to be used.
     *
     * @return Configuration to be used.
     */
    protected abstract Configuration getJooqConfiguration();
}
