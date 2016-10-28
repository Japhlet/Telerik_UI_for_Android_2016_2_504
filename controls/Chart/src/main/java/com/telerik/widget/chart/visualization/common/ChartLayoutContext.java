package com.telerik.widget.chart.visualization.common;

import android.graphics.Point;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;

/**
 * Simple class used to store information data about a chart layout.
 */
public class ChartLayoutContext {

    private final RadSize availableSize;
    private final RadSize scale;
    private final RadPoint panOffset;
    private final RadRect clipRect;

    public ChartLayoutContext() {
        this.availableSize = new RadSize();
        this.scale = new RadSize(1, 1);
        this.panOffset = new RadPoint();
        this.clipRect = new RadRect();
    }

    /**
     * Creates a new instance of the {@link ChartLayoutContext} class.
     *
     * @param availableSize the current available size for the chart layout.
     * @param scale         the current scale of the chart layout.
     * @param pan           the current pan of the chart layout.
     * @param clip          the current clip of the chart layout.
     */
    public ChartLayoutContext(RadSize availableSize, RadSize scale, RadPoint pan, final RadRect clip) {
        this.availableSize = new RadSize(availableSize.getWidth(), availableSize.getHeight());
        this.scale = new RadSize(scale.getWidth(), scale.getHeight());
        this.panOffset = new RadPoint(pan.getX(), pan.getY());
        this.clipRect = clip;
    }

    /**
     * Gets the current available size for the chart layout as {@link RadSize}.
     *
     * @return current available size.
     * @see RadSize
     */
    public RadSize getAvailableSize() {
        return availableSize;
    }

    /**
     * Gets the current chart layout scale as {@link RadSize}.
     *
     * @return the current chart layout scale.
     * @see RadSize
     */
    public RadSize scale() {
        return scale;
    }

    /**
     * Gets the current pan offset for the chart layout as {@link Point}.
     *
     * @return the current pan offset.
     * @see Point
     */
    public RadPoint panOffset() {
        return new RadPoint(this.panOffset.getX(), this.panOffset.getY());
    }

    /**
     * Gets the current clip for the chart layout as {@link RadRect}.
     *
     * @return the current layout clip.
     * @see RadRect
     */
    public RadRect clipRect() {
        return clipRect;
    }
}
