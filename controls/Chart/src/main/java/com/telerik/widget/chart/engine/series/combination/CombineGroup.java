package com.telerik.widget.chart.engine.series.combination;

import java.util.ArrayList;

/**
 * Stores one or more data points of combined chart series within a group.
 */
public class CombineGroup {

    private final ArrayList<CombineStack> stacks = new ArrayList<CombineStack>();

    /**
     * Creates a new instance of the {@link CombineGroup} class.
     */
    public CombineGroup() {
    }

    /**
     * Gets the {@link CombineStack} for the given series.
     *
     * @param series A series object that implements {@link SupportCombineMode}.
     */
    public CombineStack getStack(SupportCombineMode series) {
        if (series.getCombineMode() == ChartSeriesCombineMode.CLUSTER) {
            return this.createNewStack(series);
        }

        Object stackKey = series.getStackGroupKey();
        for (CombineStack stack : this.stacks) {
            if (stack.key == stackKey ||
                    (stack.key != null && stack.key.equals(stackKey))) {
                return stack;
            }
        }

        return this.createNewStack(series);
    }

    /**
     * Gets all combine stacks.
     */
    public ArrayList<CombineStack> stacks() {
        return this.stacks;
    }

    private CombineStack createNewStack(SupportCombineMode series) {
        CombineStack newStack = new CombineStack();
        newStack.key = series.getStackGroupKey();
        this.stacks.add(newStack);

        return newStack;
    }
}

