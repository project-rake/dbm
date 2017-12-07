package com.github.projectrake.dbm.api.v1;

import java.util.Objects;

import static com.github.projectrake.dbm.api.v1.PersistentObjectState.*;

/**
 * Class representing a persisted object. This class supports transition mechanics.
 */
public abstract class PersistentObject {
    private PersistentObjectState state = NEW;
    private PersistentObjectState preDeletionState = UNDEFINED;

    /**
     * Sets state to dirty, and throws an exception if this is not possible from the current state.
     */
    public void setStateDirty() {
        assertNotDeleted();
        assertNotNew(DIRTY);
        setState(DIRTY);
    }

    /**
     * Sets state to dirty, and ignores the call if this is not possible from the current state.
     */
    public void trySetStateDirty() {
        if (state != DELETED && state != NEW) {
            setStateDirty();
        }
    }

    /**
     * Sets state to clean, and throws an exception if this is not possible from the current state.
     */
    public void setStateClean() {
        assertNotDeleted();
        setState(CLEAN);
    }

    /**
     * Sets state to clean, and ignores the call if this is not possible from the current state.
     */
    public void trySetStateClean() {
        if (state != DELETED) {
            setStateClean();
        }
    }

    /**
     * Sets state to deleted, and throws an exception if this is not possible from the current state.
     */
    public void setStateDeleted() {
        assertNotNew(DELETED);
        preDeletionState = state;
        setState(DELETED);
    }

    /**
     * Sets state to deleted, and ignores the call if this is not possible from the current state.
     */
    public void trySetStateDeleted() {
        if (state != NEW) {
            setStateDeleted();
        }
    }

    public boolean isNew() {
        return getState() == NEW;
    }

    public boolean isClean() {
        return getState() == CLEAN;
    }

    public boolean isDirty() {
        return getState() == DIRTY;
    }

    public boolean isDeleted() {
        return getState() == DELETED;
    }

    public boolean isNotDeleted() {
        return getState() == DELETED;
    }

    /**
     * Checks for deletion state. If this object is in deletion state, this will throw an {@link IllegalStateException}
     */
    public void checkNotDeleted() {
        if (isDeleted()) {
            throw new IllegalStateException("Object already deleted. This might be a bug.");
        }
    }

    public void resurrect() {
        assertDeleted();
        assertPreDeletionState();
        setState(preDeletionState);
        preDeletionState = null;
    }

    public PersistentObjectState getState() {
        return state;
    }

    private void assertNotDeleted() {
        if (state == DELETED) {
            throw new IllegalStateException("Trying to modify deleted object. This might be a bug. Use resurrect() first.");
        }
    }

    private void assertNotNew(final PersistentObjectState newstate) {
        if (state == NEW) {
            throw new IllegalStateException("Trying to set a " + state + " object to " + newstate + ". This might be a bug. Use persist first.");
        }
    }

    private void assertDeleted() {
        if (state != DELETED) {
            throw new IllegalStateException("Trying to resurrect an object that isn't in state deleted. This might be a bug.");
        }
    }

    private void assertPreDeletionState() {
        if (preDeletionState == UNDEFINED || preDeletionState == null) {
            throw new IllegalStateException("Trying to resurrect an object that doesn't have a pre-deletion state. This might be a bug.");
        }
    }

    protected void setState(final PersistentObjectState state) {
        assertNotUndefined(state);
        this.state = Objects.requireNonNull(state, "State is not nullable.");
    }

    private void assertNotUndefined(final PersistentObjectState state) {
        if (state == UNDEFINED) {
            throw new IllegalStateException("Trying to set object state to UNDEFINED, this is not allowed.");
        }
    }
}
