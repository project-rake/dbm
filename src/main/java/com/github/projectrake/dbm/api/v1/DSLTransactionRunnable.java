package com.github.projectrake.dbm.api.v1;

import org.jooq.DSLContext;

/**
 * Created on 20.11.2017.
 * <p>
 * Interface for lambdas / objects that require a DSLContext.
 */
public interface DSLTransactionRunnable {
    /**
     * Called to execute the query.
     *
     * @param ctx {@link DSLContext} to use.
     */
    void run(DSLContext ctx);
}
