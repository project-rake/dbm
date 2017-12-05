package com.github.projectrake.dbm.api.v1;

import java.util.Objects;

import static com.github.projectrake.dbm.api.v1.PersistentObjectState.*;

/**
 * Class representing a persisted object. This class supports transition mechanics.
 */
public abstract class PersistentObject {
    private PersistentObjectState state = NEW;
    private PersistentObjectState preDeletionState = UNDEFINED;

    public void setStateDirty() {
        assertNotDeleted();
        assertNotNew(DIRTY);
        setState(DIRTY);
    }

    public void setStateClean() {
        assertNotDeleted();
        setState(CLEAN);
    }

    public void setStateDeleted() {
        assertNotNew(DELETED);
        setState(DELETED);
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
