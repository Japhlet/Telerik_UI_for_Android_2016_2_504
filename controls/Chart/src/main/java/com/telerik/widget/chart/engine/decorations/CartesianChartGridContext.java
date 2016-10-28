package com.telerik.widget.chart.engine.decorations;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.view.ChartView;

/**
 * This class holds related information for the cartesian chart grid.
 */
public class CartesianChartGridContext {

    private AxisModel axis;
    private RadRect availableRect;
    private ChartView view;
    private Iterable<AxisTickModel> majorTicks;
    private int majorTicksCount;
    private float tickThickness;

    /**
     * Creates a new instance of the {@link CartesianChartGridContext} class.
     *
     * @param availableRect The layout rect of the grid.
     * @param view          The chart of the grid.
     * @param axis          The grid axis on which the grid will plot grid lines.
     */
    public CartesianChartGridContext(final RadRect availableRect, ChartView view, AxisModel axis) {
        this.availableRect = availableRect;
        this.view = view;
        this.axis = axis;
        this.majorTicks = axis.getMajorTicks();
        this.majorTicksCount = axis.majorTickCount();
        this.tickThickness = axis.getTickThickness();
    }

    /**
     * Gets the axis.
     */
    public AxisModel axis() {
        return this.axis;
    }

    /**
     * Gets the available rect.
     */
    public RadRect availableRect() {
        return this.availableRect;
    }

    /**
     * Gets the chart view.
     */
    public ChartView view() {
        return this.view;
    }

    /**
     * Gets the major ticks.
     */
    public Iterable<AxisTickModel> majorTicks() {
        return this.majorTicks;
    }

    /**
     * Gets the major tick count.
     */
    public int majorTicksCount() {
        return this.majorTicksCount;
    }

    /**
     * Gets the tick thickness.
     */
    public float tickThickness() {
        return this.tickThickness;
    }
}

