package com.telerik.widget.chart.engine.decorations;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.chartAreas.CartesianChartAreaModel;

import java.util.ArrayList;

/**
 * This class calculates the layout information for the chart grid lines.
 */
public class CartesianChartGridModel extends ChartGridModel {

    public final ArrayList<GridStripe> xStripes;
    public final ArrayList<GridStripe> yStripes;
    public final ArrayList<GridLine> xLines;
    public final ArrayList<GridLine> yLines;
    private AxisModel primaryAxis;
    private AxisModel secondaryAxis;

    /**
     * Creates a new instance of the {@link CartesianChartGridModel} class.
     */
    public CartesianChartGridModel() {
        this.xStripes = new ArrayList<GridStripe>();
        this.yStripes = new ArrayList<GridStripe>();
        this.xLines = new ArrayList<GridLine>();
        this.yLines = new ArrayList<GridLine>();
    }

    public void setPrimaryAxis(AxisModel axis) {
        this.primaryAxis = axis;
    }

    public AxisModel getPrimaryAxis() {
        return this.primaryAxis;
    }

    public AxisModel getSecondaryAxis() {
        return this.secondaryAxis;
    }

    public void setSecondaryAxis(AxisModel axis) {
        this.secondaryAxis = axis;
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        this.clear();

        CartesianChartAreaModel plotArea = (CartesianChartAreaModel) this.chartArea;
        AxisModel primaryAxis = this.primaryAxis != null ? this.primaryAxis : plotArea.primaryFirstAxis;
        if (primaryAxis != null) {
            CartesianChartGridContext context = new CartesianChartGridContext(rect, this.chartArea.getView(), primaryAxis);
            this.buildXStripesAndLines(context);
        }

        AxisModel secondaryAxis = this.secondaryAxis != null ? this.secondaryAxis : plotArea.primarySecondAxis;
        if (secondaryAxis != null) {
            CartesianChartGridContext context = new CartesianChartGridContext(rect, this.chartArea.getView(), secondaryAxis);
            this.buildYStripesAndLines(context);
        }

        return rect;
    }

    private void buildXStripesAndLines(CartesianChartGridContext context) {
        double width;
        double thickness = context.tickThickness();
        double thicknessOffset = (int) (thickness / 2);

        for (AxisTickModel tick : context.majorTicks()) {
            double x = tick.getLayoutSlot().getX() + context.view().getPanOffsetX() + thicknessOffset;

            if (tick.isVisible()) {
                GridLine line = new GridLine();
                line.axisTickModel = tick;
                line.point1 = new RadPoint(x, context.availableRect().getY());
                line.point2 = new RadPoint(x, context.availableRect().getY() + context.availableRect().getHeight());
                this.xLines.add(line);
            }

            AxisTickModel nextMajor = tick.getNextMajorTick();
            if (nextMajor == null) {
                break;
            }

            width = nextMajor.getLayoutSlot().getX() - tick.getLayoutSlot().getX();

            GridStripe stripe = new GridStripe();
            stripe.fillRect = new RadRect(x, context.availableRect().getY(), width, context.availableRect().getHeight());
            stripe.startTick = tick;
            stripe.endTick = nextMajor;

            this.xStripes.add(stripe);
        }
    }

    private void buildYStripesAndLines(CartesianChartGridContext context) {
        double height;
        double thickness = context.tickThickness();
        double thicknessOffset = (int) (thickness / 2);

        for (AxisTickModel tick : context.majorTicks()) {
            double y = tick.getLayoutSlot().getY() + context.view().getPanOffsetY() + thicknessOffset;

            if (tick.isVisible()) {
                GridLine line = new GridLine();
                line.axisTickModel = tick;
                line.point1 = new RadPoint(context.availableRect().getX(), y);
                line.point2 = new RadPoint(context.availableRect().getX() + context.availableRect().getWidth(), y);
                this.yLines.add(line);
            }

            AxisTickModel nextMajor = tick.getNextMajorTick();
            if (nextMajor == null) {
                break;
            }

            height = Math.max(tick.getLayoutSlot().getY() - nextMajor.getLayoutSlot().getY(), 0);

            GridStripe stripe = new GridStripe();
            stripe.fillRect = new RadRect(context.availableRect().getX(), tick.getLayoutSlot().getY() + context.view().getPanOffsetY() - height + thicknessOffset, context.availableRect().getWidth(), height);
            stripe.startTick = tick;
            stripe.endTick = nextMajor;

            this.yStripes.add(stripe);
        }
    }

    private void clear() {
        this.xStripes.clear();
        this.xLines.clear();
        this.yStripes.clear();
        this.yLines.clear();
    }
}

