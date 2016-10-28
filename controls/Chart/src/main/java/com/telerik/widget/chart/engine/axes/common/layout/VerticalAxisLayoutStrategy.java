package com.telerik.widget.chart.engine.axes.common.layout;

import com.telerik.android.common.RadThickness;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelLayoutMode;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation;
import com.telerik.widget.chart.engine.axes.common.AxisLastLabelVisibility;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.continuous.ValueRange;

/**
 * Vertical layout algorithm for {@link com.telerik.widget.chart.engine.axes.AxisModel}.
 */
public class VerticalAxisLayoutStrategy extends AxisModelLayoutStrategy {

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.engine.axes.common.layout.VerticalAxisLayoutStrategy} class.
     *
     * @param owner The layout algorithm owner.
     */
    public VerticalAxisLayoutStrategy(AxisModel owner) {
        super(owner);
    }

    @Override
    public AxisLastLabelVisibility getDefaultLastLabelVisibility() {
        return AxisLastLabelVisibility.VISIBLE;
    }

    @Override
    public double getZoom() {
        return this.owner.chartArea().getView().getZoomHeight();
    }

    @Override
    public void applyLayoutRounding() {
        // fit first and last ticks within axis layout slot
        AxisTickModel firstTick = this.owner.getFirstTick();
        AxisTickModel lastTick = this.owner.getLastTick();

        double thickness = this.owner.getTickThickness();
        int thicknessOffset = (int) (thickness / 2.0);

        // ensure that the first and last ticks are within axis' layout slot
        if (firstTick != null && RadMath.isZero(firstTick.normalizedValue())) {
            RadRect firstLayoutSlot = firstTick.getLayoutSlot();

            double zoomHeight = this.owner.getLayoutSlot().getHeight() * this.owner.chartArea().getView().getZoomHeight();
            double y = this.owner.getLayoutSlot().getY() + zoomHeight - thicknessOffset;
            // remove one additional pixel at bottom (rendering along the y-axis goes from top to bottom)
            y -= 1;

            firstTick.arrange(new RadRect(firstLayoutSlot.getX(), y, firstLayoutSlot.getWidth(), firstLayoutSlot.getHeight()));
        }

        if (lastTick != null && RadMath.isOne(lastTick.normalizedValue())) {
            RadRect lastLayoutSlot = lastTick.getLayoutSlot();
            double y = this.owner.getLayoutSlot().getY() - thicknessOffset;

            lastTick.arrange(new RadRect(lastLayoutSlot.getX(), y, lastLayoutSlot.getWidth(), lastLayoutSlot.getHeight()));
        }
    }

    @Override
    public void updateTicksVisibility(final RadRect clipRect) {
        AxisPlotMode plotMode = this.owner.getActualPlotMode();

        for (AxisTickModel tick : this.owner.ticks()) {
            boolean visible = tick.getLayoutSlot().getCenter().getY() >= clipRect.getY() - this.owner.chartArea().getView().getPanOffsetY() &&
                    tick.getLayoutSlot().getCenter().getY() <= clipRect.getBottom() - this.owner.chartArea().getView().getPanOffsetY();
            tick.setIsVisible(visible);
            if (tick.associatedLabel() != null) {
                if (plotMode == AxisPlotMode.ON_TICKS) {
                    tick.associatedLabel().setIsVisible(visible);
                } else {
                    tick.associatedLabel().setIsVisible(tick.associatedLabel().getLayoutSlot().getY() >= clipRect.getY() - this.owner.chartArea().getView().getPanOffsetY() &&
                            tick.associatedLabel().getLayoutSlot().getBottom() <= clipRect.getBottom() - this.owner.chartArea().getView().getPanOffsetY());
                }
            }
        }
    }

    @Override
    public void arrange(final RadRect rect) {

        RadRect availableRect = rect;

        // arrange title
        double titleLeft;
        if (this.owner.getHorizontalLocation() == AxisHorizontalLocation.LEFT) {
            titleLeft = availableRect.getX();
        } else {
            titleLeft = availableRect.getRight() - this.owner.title().desiredSize.getHeight();
        }
        this.owner.title().arrange(new RadRect(
                titleLeft,
                availableRect.getY() + ((availableRect.getHeight() - this.owner.title().desiredSize.halfHeight())),
                this.owner.title().desiredSize.getWidth(),
                this.owner.title().desiredSize.getHeight()));

        // scale by the zoom factor
        double height = availableRect.getHeight() * this.owner.chartArea().getView().getZoomHeight();
        availableRect = new RadRect(availableRect.getX(), availableRect.getY(), availableRect.getWidth(), height);

        // arrange ticks
        double thickness = this.owner.getTickThickness();
        double thicknessOffset = thickness / 2.0;
        for (AxisTickModel tick : this.owner.ticks()) {
            double y;
            if (tick.normalizedValue() == 0d) {
                y = availableRect.getBottom() - thicknessOffset;
            } else if (tick.normalizedValue() == 1.0) {
                y = availableRect.getY();
            } else {
                y = availableRect.getBottom() - (tick.normalizedValue() * availableRect.getHeight()) - thicknessOffset;
            }

            double x;
            double width = this.owner.getMajorTickLength();
            if (this.owner.getHorizontalLocation() == AxisHorizontalLocation.LEFT) {
                x = availableRect.getRight() - (width + this.owner.getLineThickness());
            } else {
                x = availableRect.getX();
            }
            tick.arrange(new RadRect(x, y, width, thickness));
        }

        // arrange labels
        RadRect labelRect;
        double majorTickLength = this.owner.getMajorTickLength();

        for (AxisLabelModel label : this.owner.getLabels()) {
            double x;
            AxisLabelLayoutMode layoutMode = this.owner.getLabelLayoutMode();
            if (this.owner.getHorizontalLocation() == AxisHorizontalLocation.LEFT) {
                if(layoutMode == AxisLabelLayoutMode.OUTER) {
                    x = availableRect.getRight() - majorTickLength - label.desiredSize.getWidth() - this.owner.getLabelMargin() - this.owner.getLineThickness();
                } else {
                    x = availableRect.getRight() + this.owner.getLabelMargin() + this.owner.getLineThickness();
                }
            } else {
                if(layoutMode == AxisLabelLayoutMode.OUTER) {
                    x = availableRect.getX() + majorTickLength + this.owner.getLabelMargin();
                } else {
                    x = availableRect.getX() - this.owner.getLabelMargin() - label.desiredSize.getWidth();
                }
            }

            double y = availableRect.getBottom() - label.normalizedPosition() * availableRect.getHeight();
            labelRect = new RadRect(
                    x,
                    y - label.desiredSize.halfHeight(),
                    label.desiredSize.getWidth(),
                    label.desiredSize.getHeight());
            label.arrange(labelRect);
        }
    }

    @Override
    public ValueRange<Double> getVisibleRange(final RadSize availableSize) {
        RadRect plotAreaClip = this.owner.chartArea().getPlotArea().getLayoutSlot();
        double zoomFactor = this.owner.chartArea().getView().getZoomHeight();
        if (zoomFactor == 1.0) {
            return new ValueRange<Double>(0.0, 1.0);
        }

        double visibleLength = 1.0 / zoomFactor;

        double zoomedHeight = Math.round(plotAreaClip.getHeight() * zoomFactor);
        double offsetBottom = zoomedHeight - plotAreaClip.getHeight() + this.owner.chartArea().getView().getPanOffsetY();
        double offsetY = offsetBottom / zoomedHeight;

        return new ValueRange<Double>(offsetY, offsetY + visibleLength);
    }

    @Override
    public RadThickness getDesiredMargin(final RadSize availableSize) {
        RadThickness margin = new RadThickness();
        if (this.maxLabelHeight == 0 || this.owner.getLastLabelVisibility() != AxisLastLabelVisibility.VISIBLE) {
            return margin;
        }

        double labelOffset;
        if (this.owner.getActualPlotMode() == AxisPlotMode.ON_TICKS) {
            labelOffset = this.maxLabelHeight / 2.0;
            margin.top = labelOffset;
            margin.bottom = this.owner.getLabels().get(0).desiredSize.halfHeight();
        }

        return margin;
    }

    @Override
    public RadSize getDesiredSize(final RadSize availableSize) {
        if(this.owner.getWidth() >= 0) {
            return new RadSize(this.owner.getWidth(), 0);
        }

        this.maxLabelHeight = 0;
        this.maxLabelWidth = 0;
        for (AxisLabelModel label : this.owner.getLabels()) {
            if (!label.isVisible()) {
                continue;
            }

            if (label.desiredSize.getWidth() > this.maxLabelWidth) {
                this.maxLabelWidth = label.desiredSize.getWidth();
            }
            if (label.desiredSize.getHeight() > this.maxLabelHeight) {
                this.maxLabelHeight = label.desiredSize.getHeight();
            }
        }

        double width = this.owner.getMajorTickLength() + this.owner.getLineThickness();
        if(this.owner.getLabelLayoutMode() == AxisLabelLayoutMode.OUTER) {
            width += this.maxLabelWidth;
            width += this.owner.getLabelMargin();
        }

        // We are adding the height of the title since we will rotate it.
        width += this.owner.title().desiredSize.getHeight();

        return new RadSize(width, 0);
    }

    @Override
    public void arrangeLabelMultiline(AxisLabelModel label, final RadRect rect) {
    }

    @Override
    public void arrangeLabelNone(AxisLabelModel label, final RadRect rect) {
    }
}

