package com.telerik.widget.chart.visualization.common;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation;
import com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.visualization.common.renderers.CartesianAxisLabelRenderer;
import com.telerik.widget.chart.visualization.common.renderers.ChartLabelRenderer;

/**
 * This class is a base for all types of axes used in a {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 * This class is abstract and should not be used directly in your application.
 */
public abstract class CartesianAxis extends LineAxis {
    public int linkedSeriesCount;

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.common.CartesianAxis} class.
     */
    protected CartesianAxis() {
    }

    /**
     * Gets a value from the {@link com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation}
     * enum determining the horizontal location of the current {@link com.telerik.widget.chart.visualization.common.CartesianAxis} instance.
     *
     * @return the {@link com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation} instance.
     */
    public AxisHorizontalLocation getHorizontalLocation() {
        return this.getModel().getHorizontalLocation();
    }

    /**
     * Sets a value from the {@link com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation}
     * enum determining the horizontal location of the current {@link com.telerik.widget.chart.visualization.common.CartesianAxis} instance.
     *
     * @param value the {@link com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation} instance.
     */
    public void setHorizontalLocation(AxisHorizontalLocation value) {
        Axis presenter = this;
        presenter.getModel().setHorizontalLocation(value);
    }

    /**
     * Gets a value from the {@link com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation}
     * enum determining the vertical location of the current {@link com.telerik.widget.chart.visualization.common.CartesianAxis} instance.
     *
     * @return the {@link com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation} instance.
     */
    public AxisVerticalLocation getVerticalLocation() {
        return this.getModel().getVerticalLocation();
    }

    /**
     * Sets a value from the {@link com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation}
     * enum determining the vertical location of the current {@link com.telerik.widget.chart.visualization.common.CartesianAxis} instance.
     *
     * @param value the {@link com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation} instance.
     */
    public void setVerticalLocation(AxisVerticalLocation value) {
        Axis presenter = this;
        presenter.getModel().setVerticalLocation(value);
    }

    @Override
    protected RadSize measureNodeOverride(ChartNode node, Object content) {
        if (node instanceof AxisLabelModel) {
            CartesianAxisLabelRenderer renderer = (CartesianAxisLabelRenderer) this.resolveLabelRenderer();
            return renderer.measureLabel((AxisLabelModel) node, content);
        }

        return super.measureNodeOverride(node, content);
    }

    @Override
    protected ChartLabelRenderer createDefaultLabelRenderer() {
        return new CartesianAxisLabelRenderer(this);
    }
}
