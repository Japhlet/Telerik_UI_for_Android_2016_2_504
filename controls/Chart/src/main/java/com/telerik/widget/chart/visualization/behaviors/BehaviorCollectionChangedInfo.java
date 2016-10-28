package com.telerik.widget.chart.visualization.behaviors;

import com.telerik.android.common.CollectionChangeAction;

/**
 * Stores info about a change that occurred in a collection of behaviours.
 */
public class BehaviorCollectionChangedInfo {

    private ChartBehavior addedBehavior;
    private ChartBehavior removedBehavior;
    private CollectionChangeAction action;

    /**
     * Creates a new instance of the {@link BehaviorCollectionChangedInfo} class.
     *
     * @param added   the behaviour that has been added.
     * @param removed the behaviour that has been removed.
     * @param action  the action that was performed on the collection.
     */
    public BehaviorCollectionChangedInfo(ChartBehavior added, ChartBehavior removed, CollectionChangeAction action) {
        this.addedBehavior = added;
        this.removedBehavior = removed;
        this.action = action;
    }

    /**
     * Gets the current behaviour that was added.
     *
     * @return the current added behaviour.
     */
    public ChartBehavior getAddedBehavior() {
        return addedBehavior;
    }

    /**
     * Sets the current behaviour that was added.
     *
     * @param addedBehavior the new added behaviour.
     */
    public void setAddedBehavior(ChartBehavior addedBehavior) {
        this.addedBehavior = addedBehavior;
    }

    /**
     * Gets the current behaviour that was removed.
     *
     * @return the current removed behaviour.
     */
    public ChartBehavior getRemovedBehavior() {
        return removedBehavior;
    }

    /**
     * Sets the current behaviour that was removed.
     *
     * @param removedBehavior the new removed behaviour.
     */
    public void setRemovedBehavior(ChartBehavior removedBehavior) {
        this.removedBehavior = removedBehavior;
    }

    /**
     * Gets the current action that was performed on the collection.
     *
     * @return the current action.
     */
    public CollectionChangeAction getAction() {
        return action;
    }

    /**
     * Sets the current action that was performed on the collection.
     *
     * @param action the new action.
     */
    public void setAction(CollectionChangeAction action) {
        this.action = action;
    }
}
