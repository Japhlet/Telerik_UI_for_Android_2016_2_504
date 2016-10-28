package com.telerik.widget.chart.engine.axes;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.elementTree.ContentNode;
import com.telerik.widget.chart.visualization.common.LabelSizeInfo;

/**
 * Model for all labels part of an {@link com.telerik.widget.chart.engine.axes.AxisModel} in a chart.
 */
public class AxisLabelModel extends ContentNode {

    private LabelSizeInfo labelSizeInfo = new LabelSizeInfo();
    private double normalizedPosition;

    /**
     * Creates an instance of the {@link AxisLabelModel} class with specified parameters.
     *
     * @param normalizedPosition       the normalized label position on the axis.
     * @param transformOffset          the transform offset.
     * @param untransformedDesiredSize the original desired size of the label.
     */
    public AxisLabelModel(double normalizedPosition, RadPoint transformOffset, RadSize untransformedDesiredSize) {
        this.normalizedPosition = normalizedPosition;
        labelSizeInfo.transformOffset = transformOffset;
        labelSizeInfo.untransformedSize = untransformedDesiredSize;
    }

    /**
     * Updates the current {@link com.telerik.widget.chart.engine.axes.AxisLabelModel}
     * with the information provided in the specified {@link com.telerik.widget.chart.visualization.common.LabelSizeInfo} argument.
     *
     * @param info an instance of the {@link com.telerik.widget.chart.visualization.common.LabelSizeInfo} class containing the update information.
     */
    public void update(LabelSizeInfo info) {
        this.labelSizeInfo = info;
    }

    public LabelSizeInfo getLabelSizeInfo() {
        return this.labelSizeInfo;
    }

    /**
     * Gets the normalized position of the {@link com.telerik.widget.chart.engine.axes.AxisLabelModel}.
     *
     * @return the normalized position.
     */
    public double normalizedPosition() {
        return this.normalizedPosition;
    }

    /**
     * Gets the transform offset.
     *
     * @return an instance of the {@link com.telerik.android.common.math.RadPoint} class holding the transform offset.
     */
    public RadPoint transformOffset() {
        return this.labelSizeInfo.transformOffset;
    }

    /**
     * Gets the untransformed desired size of the current label.
     *
     * @return an instance of the {@link RadSize} class representing the desired size.
     */
    public RadSize untransformedDesiredSize() {
        return this.labelSizeInfo.untransformedSize;
    }
}

