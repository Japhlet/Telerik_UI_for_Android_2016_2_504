package com.telerik.widget.chart.engine.axes.common.layout;

import com.telerik.android.common.RadThickness;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelLayoutMode;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.axes.common.AxisLastLabelVisibility;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation;
import com.telerik.widget.chart.engine.axes.continuous.ValueRange;

/**
 * Encapsulates the horizontal axis layout algorithm.
 */
public class HorizontalAxisLayoutStrategy extends AxisModelLayoutStrategy {

    private double labelBottom;
    private double totalLabelHeight;

    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.engine.axes.common.layout.HorizontalAxisLayoutStrategy} class.
     *
     * @param owner The owner of this layout algorithm.
     */
    public HorizontalAxisLayoutStrategy(AxisModel owner) {
        super(owner);
    }

    @Override
    public AxisLastLabelVisibility getDefaultLastLabelVisibility() {
        return AxisLastLabelVisibility.VISIBLE;
    }

    @Override
    public double getZoom() {
        return this.owner.chartArea().getView().getZoomWidth();
    }

    @Override
    public void applyLayoutRounding() {
        // fit first and last ticks within axis layout slot
        AxisTickModel firstTick = this.owner.getFirstTick();
        AxisTickModel lastTick = this.owner.getLastTick();

        double thickness = this.owner.getTickThickness();
        double thicknessOffset = thickness / 2.0;

        if (firstTick != null && RadMath.isZero(firstTick.normalizedValue())) {
            double x = this.owner.getLayoutSlot().getX() - thicknessOffset;
            RadRect slot = firstTick.getLayoutSlot();
            firstTick.arrange(new RadRect(x, slot.getY(), slot.getWidth(), slot.getHeight()));
        }
        if (lastTick != null && RadMath.isOne(lastTick.normalizedValue())) {
            double zoomWidth = this.owner.getLayoutSlot().getWidth() * this.owner.chartArea().getView().getZoomWidth();
            RadRect slot = lastTick.getLayoutSlot();
            double x = this.owner.getLayoutSlot().getX() + zoomWidth - thicknessOffset;

            // remove one additional pixel on the right (rendering along the x-axis goes from left to right)
            x -= 1;
            lastTick.arrange(new RadRect(x, slot.getY(), slot.getWidth(), slot.getHeight()));
        }
    }

    @Override
    public void updateTicksVisibility(final RadRect clipRect) {
        AxisPlotMode plotMode = this.owner.getActualPlotMode();

        for (AxisTickModel tick : this.owner.ticks()) {
            boolean visible = tick.getLayoutSlot().getCenter().getX() >= clipRect.getX() - this.owner.chartArea().getView().getPanOffsetX() &&
                    tick.getLayoutSlot().getCenter().getX() <= clipRect.getRight() - this.owner.chartArea().getView().getPanOffsetX();
            tick.setIsVisible(visible);
            if (tick.associatedLabel() != null && tick.associatedLabel().isVisible()) {
                if (plotMode == AxisPlotMode.ON_TICKS) {
                    tick.associatedLabel().setIsVisible(visible);
                } else if (!visible) {
                    tick.associatedLabel().setIsVisible(tick.associatedLabel().getLayoutSlot().getX() >= clipRect.getX() - this.owner.chartArea().getView().getPanOffsetX() &&
                            tick.associatedLabel().getLayoutSlot().getRight() <= clipRect.getRight() - this.owner.chartArea().getView().getPanOffsetX());
                }
            }
        }
    }

    @Override
    public void arrange(final RadRect availableRect) {
        RadRect modifiedAvailableRect = availableRect;
        double lineThickness = owner.getLineThickness();

        // arrange title
        double titleTop;
        if (this.owner.getVerticalLocation() == AxisVerticalLocation.BOTTOM) {
            titleTop = modifiedAvailableRect.getBottom() - this.owner.title().desiredSize.getHeight();
        } else {
            titleTop = modifiedAvailableRect.getY();
            modifiedAvailableRect = new RadRect(modifiedAvailableRect.getX(), modifiedAvailableRect.getY() + this.owner.title().desiredSize.getHeight(), modifiedAvailableRect.getWidth(), modifiedAvailableRect.getHeight());
        }

        this.owner.title().arrange(new RadRect(
                modifiedAvailableRect.getX() + ((modifiedAvailableRect.getWidth() - this.owner.title().desiredSize.getWidth()) / 2),
                titleTop,
                this.owner.title().desiredSize.getWidth(),
                this.owner.title().desiredSize.getHeight()));

        // scale by the zoom factor
        modifiedAvailableRect = new RadRect(modifiedAvailableRect.getX(), modifiedAvailableRect.getY(), modifiedAvailableRect.getWidth() * this.owner.chartArea().getView().getZoomWidth(), modifiedAvailableRect.getHeight());

        // arrange ticks
        double x;
        double y;
        double tickLength = this.owner.getMajorTickLength();
        if (this.owner.getVerticalLocation() == AxisVerticalLocation.BOTTOM) {
            y = modifiedAvailableRect.getY() + lineThickness;
        } else {
            y = modifiedAvailableRect.getBottom() - lineThickness - tickLength;
        }
        double thickness = this.owner.getTickThickness();
        double thicknessOffset = (int) (thickness / 2);

        for (AxisTickModel tick : this.owner.ticks()) {
            if (tick.normalizedValue() == 0.0) {
                x = modifiedAvailableRect.getX() - thicknessOffset;
            } else if (tick.normalizedValue() == 1.0) {
                x = modifiedAvailableRect.getX() + modifiedAvailableRect.getWidth();
            } else {
                x = modifiedAvailableRect.getX() + (tick.normalizedValue() * modifiedAvailableRect.getWidth()) - thicknessOffset;
            }

            tick.arrange(new RadRect(x, y, thickness, tickLength));
        }

        AxisLabelLayoutMode layoutMode = this.owner.getLabelLayoutMode();

        // arrange labels
        if (this.owner.getVerticalLocation() == AxisVerticalLocation.BOTTOM) {
            if (layoutMode == AxisLabelLayoutMode.OUTER) {
                this.labelBottom = modifiedAvailableRect.getY() + this.owner.getMajorTickLength() + this.owner.getLabelMargin() + this.totalLabelHeight;
            } else {
                this.labelBottom = modifiedAvailableRect.getY() - this.owner.getLabelMargin();
            }
        } else {
            if (layoutMode == AxisLabelLayoutMode.OUTER) {
                this.labelBottom = ((modifiedAvailableRect.getBottom() - this.owner.getMajorTickLength()) - this.owner.getLabelMargin());
            } else {
                this.labelBottom = modifiedAvailableRect.getBottom() + this.totalLabelHeight + this.owner.getLabelMargin();
            }
        }

        AxisLabelFitMode fitMode = this.owner().getLabelFitMode();
        if (fitMode == AxisLabelFitMode.MULTI_LINE) {
            for (AxisLabelModel label : this.owner.getLabels()) {
                this.arrangeLabelMultiline(label, modifiedAvailableRect);
            }
        } else if (fitMode == AxisLabelFitMode.NONE) {
            for (AxisLabelModel label : this.owner.getLabels()) {
                this.arrangeLabelNone(label, modifiedAvailableRect);
            }
        } else {
            for (AxisLabelModel label : this.owner.getLabels()) {
                this.arrangeLabelRotate(label, modifiedAvailableRect);
            }
        }
    }

    @Override
    public ValueRange<Double> getVisibleRange(final RadSize availableSize) {
        double zoomFactor = this.owner.chartArea().getView().getZoomWidth();
        if (zoomFactor == 1.0) {
            return new ValueRange<Double>(0.0, 1.0);
        }

        RadRect plotAreaClip = this.owner.chartArea().getPlotArea().getLayoutSlot();

        double visibleLength = 1.0 / zoomFactor;
        double zoomedWidth = Math.round(zoomFactor * plotAreaClip.getWidth());
        double panOffset = this.owner.chartArea().getView().getPanOffsetX();
        double offsetX = Math.abs(panOffset / zoomedWidth);

        // Avoids a floating point error. offsetX + visibleLength almost never amounts to exactly 1 which
        // results in the last tick and label not being drawn.
        double end = 1;
        if(zoomedWidth - plotAreaClip.getWidth() + panOffset != 0) {
            end = offsetX + visibleLength;
        }

        return new ValueRange<Double>(offsetX, end);
    }

    @Override
    public RadThickness getDesiredMargin(final RadSize availableSize) {
        RadThickness margin = new RadThickness();
        if (this.maxLabelWidth == 0 || this.owner.getLastLabelVisibility() != AxisLastLabelVisibility.VISIBLE) {
            return margin;
        }

        double lastLabelHReach;
        AxisLabelModel lastLabel = this.owner.generateLastLabel();
        if (this.owner.getLabelFitMode() != AxisLabelFitMode.ROTATE) {
            lastLabelHReach = lastLabel.desiredSize.halfWidth();
        } else {
            if (this.owner.getNormalizedLabelRotationAngle() < 180) {
                lastLabelHReach = lastLabel.desiredSize.getWidth() + lastLabel.transformOffset().getX();
            } else if (this.owner.getNormalizedLabelRotationAngle() < 270) {
                lastLabelHReach = lastLabel.desiredSize.getWidth();
            } else {
                lastLabelHReach = 0.0;
            }
        }

        AxisLabelModel firstLabel = this.owner.getLabels().get(0);
        if (this.owner.getActualPlotMode() == AxisPlotMode.ON_TICKS) {
            margin.right = Math.max((int) (lastLabel.desiredSize.getWidth() / 2), lastLabelHReach);
            margin.left = firstLabel.desiredSize.halfWidth();
        } else {
            int slotWidth = (int) ((availableSize.getWidth() / this.owner.majorTickCount()) * getZoom());

            if(lastLabel.desiredSize.getWidth() > slotWidth) {
                margin.right = Math.max(0, lastLabelHReach - (slotWidth / 2));
            } else {
                margin.right = 0;
            }
        }

        return margin;
    }

    @Override
    public RadSize getDesiredSize(final RadSize availableSize) {
        this.maxLabelWidth = 0;
        this.maxLabelHeight = 0;
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

        this.updateTotalLabelHeight(availableSize);

        double height = this.owner.getLineThickness() + this.owner.getMajorTickLength();

        if (this.owner.getLabelLayoutMode() == AxisLabelLayoutMode.OUTER) {
            height += this.totalLabelHeight;
            height += this.owner.getLabelMargin();
        }

        height += this.owner.title().desiredSize.getHeight();

        return new RadSize(0, height);
    }

    @Override
    public void arrangeLabelMultiline(AxisLabelModel label, final RadRect rect) {
        double center = rect.getX() + label.normalizedPosition() * rect.getWidth();
        double stackShift = label.collectionIndex() % this.totalLabelWidthToAvailableWidth;

        RadRect labelRect = new RadRect(center - label.desiredSize.halfWidth(),
                (this.labelBottom - label.desiredSize.getHeight()) - stackShift * this.maxLabelHeight,
                label.desiredSize.getWidth(),
                label.desiredSize.getHeight());
        label.arrange(labelRect);
    }

    @Override
    public void arrangeLabelNone(AxisLabelModel label, final RadRect rect) {
        double center = rect.getX() + label.normalizedPosition() * rect.getWidth();

        RadRect labelRect = new RadRect(center - label.desiredSize.halfWidth(), this.labelBottom - label.desiredSize.getHeight(), label.desiredSize.getWidth(), label.desiredSize.getHeight());
        label.arrange(labelRect);
    }

    public void arrangeLabelRotate(AxisLabelModel label, final RadRect rect) {
        double center = rect.getX() + label.normalizedPosition() * rect.getWidth();

        RadRect labelRect;
        if (this.owner.getVerticalLocation() == AxisVerticalLocation.BOTTOM) {
            labelRect = new RadRect(center - label.desiredSize.halfWidth(), rect.getY() + this.owner().getMajorTickLength() + this.owner.getLabelMargin(), label.desiredSize.getWidth(), label.desiredSize.getHeight());
        } else {
            labelRect = new RadRect(center - label.desiredSize.halfWidth(), rect.getBottom() - label.desiredSize.getHeight() - this.owner().getMajorTickLength() - this.owner.getLabelMargin(), label.desiredSize.getWidth(), label.desiredSize.getHeight());
        }
        label.arrange(labelRect);
    }

    private void updateTotalLabelHeight(final RadSize availableSize) {
        // we assume that all labels have almost same width and we take the maximum of all
        // TODO: This is not always true, we need a more extended algorithm which will build lines dynamically
        double totalLabelWidth = this.owner.getLabels().size() * this.maxLabelWidth;
        this.totalLabelWidthToAvailableWidth = (int) (totalLabelWidth / availableSize.getWidth()) + 1;
        if (this.totalLabelWidthToAvailableWidth > this.owner.getLabels().size()) {
            this.totalLabelWidthToAvailableWidth = this.owner.getLabels().size();
        }

        this.totalLabelHeight = this.maxLabelHeight;
        if (this.owner.getLabelFitMode() == AxisLabelFitMode.MULTI_LINE) {

            this.totalLabelHeight *= this.totalLabelWidthToAvailableWidth;
        }
    }
}
