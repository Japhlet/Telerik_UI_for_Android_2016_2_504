package com.telerik.widget.dataform.engine;

import java.util.ArrayList;

public abstract class EntityCore implements Entity {

    private ArrayList<EntityPropertyCommitListener> commitListeners = new ArrayList<>();

    public void addCommitListener(EntityPropertyCommitListener listener) {
        this.commitListeners.add(listener);
    }

    public void removeCommitListener(EntityPropertyCommitListener listener) {
        this.commitListeners.remove(listener);
    }

    @Override
    public boolean notifyCommitListenersBefore(EntityProperty property) {
        boolean result = false;
        for(EntityPropertyCommitListener listener : this.commitListeners) {
            if (listener.onBeforeCommit(property)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public void notifyCommitListenersAfter(EntityProperty property) {
        for(EntityPropertyCommitListener listener : this.commitListeners) {
            listener.onAfterCommit(property);
        }
    }
}
