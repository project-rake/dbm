package com.github.projectrake.dbm.api.v1;

import java.util.*;

import static com.github.projectrake.dbm.api.v1.PersistentObjectState.NEW;
import static com.github.projectrake.dbm.api.v1.PersistentObjectState.UNDEFINED;

/**
 * Class representing a persisted object. This class supports transition mechanics.
 * Added listener support, so you can add event hooks on object transitions (post transition).
 */
public abstract class SubscribeablePersistentObject extends PersistentObject {
    private PersistentObjectState state = NEW;
    private PersistentObjectState preDeletionState = UNDEFINED;
    private Map<PersistentObjectState, List<Runnable>> listeners = new HashMap<>();
    private List<Runnable> resurrectionListeners = new LinkedList<>();

    public void setStateDirty() {
        super.setStateDirty();
        callListeners(getState());
    }

    public void setStateClean() {
        super.setStateClean();
        callListeners(getState());
    }

    public void setStateDeleted() {
        super.setStateDeleted();
        callListeners(getState());
    }

    public void resurrect() {
        super.resurrect();
        resurrectionListeners.forEach(Runnable::run);
    }

    public Runnable addListener(final PersistentObjectState state, final Runnable run) {
        listeners.computeIfAbsent(Objects.requireNonNull(state), s -> new LinkedList<>()).add(Objects.requireNonNull(run));
        return run;
    }

    public void removeListener(final Runnable run) {
        for (PersistentObjectState state : PersistentObjectState.values()) {
            removeListener(state, run);
        }
    }

    protected void removeListener(final PersistentObjectState state, final Runnable run) {
        listeners.getOrDefault(state, Collections.emptyList()).remove(run);
    }

    private void callListeners(final PersistentObjectState state) {
        listeners.getOrDefault(state, Collections.emptyList()).forEach(Runnable::run);
    }
}
