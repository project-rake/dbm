package com.github.projectrake.dbm.api.v1;

/**
 * Created on 01.12.2017.
 */
public class UnsupportedTransitionException extends RuntimeException {
    public UnsupportedTransitionException(String s) {
        super(s);
    }

    public UnsupportedTransitionException(PersistentObjectState state, PersistentObjectState clean) {
        super("Cannot go from " + state + " to " + clean);
    }
}
