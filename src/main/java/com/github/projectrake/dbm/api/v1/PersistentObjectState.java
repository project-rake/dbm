package com.github.projectrake.dbm.api.v1;

/**
 * States representing a persisted/persistable object.
 */
public enum PersistentObjectState {
    NEW,
    CLEAN,
    DIRTY,
    DELETED,
    UNDEFINED
}
