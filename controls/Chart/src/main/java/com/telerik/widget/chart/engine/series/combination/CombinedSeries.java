package com.telerik.widget.chart.engine.series.combination;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Immutable class that stores information for a collection of combined series.
 */
public class CombinedSeries {

    private Type seriesType;
    private ChartSeriesCombineMode combineMode;
    private final ArrayList<ChartSeriesModel> series = new ArrayList<ChartSeriesModel>();
    private final ArrayList<CombineGroup> groups = new ArrayList<CombineGroup>();
    private int combineIndex;
    private AxisModel stackAxis;
    private AxisModel stackValueAxis;

    /**
     * Creates a new instance of the {@link CombinedSeries} class.
     *
     * @param seriesType     The type of the combined series.
     * @param combineMode    The combine mode of the series.
     * @param combineIndex   The combine index.
     * @param stackAxis      The stack axis.
     * @param stackValueAxis The stack value Axis.
     */
    public CombinedSeries(Type seriesType, ChartSeriesCombineMode combineMode, int combineIndex, AxisModel stackAxis, AxisModel stackValueAxis) {
        this.seriesType = seriesType;
        this.combineMode = combineMode;
        this.combineIndex = combineIndex;
        this.stackAxis = stackAxis;
        this.stackValueAxis = stackValueAxis;
    }

    /**
     * Gets the combined series.
     */
    public ArrayList<ChartSeriesModel> series() {
        return this.series;
    }

    /**
     * Gets the combine groups.
     */
    public ArrayList<CombineGroup> groups() {
        return this.groups;
    }

    /**
     * Gets the combine index.
     */
    public int combineIndex() {
        return this.combineIndex;
    }

    /**
     * Gets the combine mode.
     */
    public ChartSeriesCombineMode combineMode() {
        return this.combineMode;
    }

    /**
     * Gets the series type.
     */
    public Type seriesType() {
        return this.seriesType;
    }

    /**
     * Gets the stack axis.
     */
    public AxisModel stackAxis() {
        return this.stackAxis;
    }

    /**
     * Gets the stack value axis.
     */
    public AxisModel stackValueAxis() {
        return this.stackValueAxis;
    }
}

