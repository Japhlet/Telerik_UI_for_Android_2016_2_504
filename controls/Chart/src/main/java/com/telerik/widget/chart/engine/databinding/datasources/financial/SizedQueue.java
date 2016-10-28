package com.telerik.widget.chart.engine.databinding.datasources.financial;

import java.util.ArrayDeque;

public class SizedQueue extends ArrayDeque<Double> {

    public double runningSum;
    public int size;
    public int currentItemsCount;

    public double enqueueItem(double item) {
        double result = 0;
        if (this.size <= this.currentItemsCount && this.size > 0) {
            result = this.removeFirst();
            this.runningSum -= result;
            this.currentItemsCount--;
        }

        if (this.size > this.currentItemsCount) {
            this.add(item);
            this.runningSum += item;
            this.currentItemsCount++;
        }

        return result;
    }

    public double dequeueItem() {
        double result = removeFirst();
        this.runningSum -= result;
        this.currentItemsCount--;
        return result;
    }

    public double min() {
        double min = Double.POSITIVE_INFINITY;
        for (double value : this)
            if (value < min) {
                min = value;
            }

        return min;
    }

    public double max() {
        double max = Double.NEGATIVE_INFINITY;
        for (double value : this)
            if (value > max) {
                max = value;
            }

        return max;
    }
}
