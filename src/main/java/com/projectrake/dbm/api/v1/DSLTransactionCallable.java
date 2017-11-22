package com.projectrake.dbm.api.v1;

import org.jooq.DSLContext;

/**
 * Created on 20.11.2017.
 * <p>
 * Interface for lambdas / objects that require a DSLContext.
 *
 * @param <T> Return type of the {@link DSLTransactionCallable#run(DSLContext)} method.
 */

public interface DSLTransactionCallable<T> {
    /**
     * Called to execute the query.
     *
     * @param ctx {@link DSLContext} to use.
     * @return Object of type T.
     */
    T run(DSLContext ctx);
}
