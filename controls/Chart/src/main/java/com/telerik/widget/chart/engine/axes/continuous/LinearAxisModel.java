package com.telerik.widget.chart.engine.axes.continuous;

import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.MajorTickModel;

import java.util.ArrayList;

public class LinearAxisModel extends NumericalAxisModel {

    /**
     * Creates a new instance of the {@link LinearAxisModel} class.
     */
    public LinearAxisModel() {
    }

    @Override
    public int majorTickCount() {
        double delta = this.getActualRange().maximum - this.getActualRange().minimum;
        return (int)Math.round(delta / calculateTickStep());
    }

    protected double calculateTickStep() {
        double scale = this.getLayoutStrategy().getZoom();
        double step = this.majorStep;
        if (scale != 1.0) {
            step = this.normalizeStep(step / scale);
        }

        return step;
    }

    @Override
    protected Iterable<AxisTickModel> generateTicks(final ValueRange<Double> range) {
        ValueRange<Double> visibleRange = range.clone();
        double delta = this.getActualRange().maximum - this.getActualRange().minimum;
        ArrayList<AxisTickModel> list = new ArrayList<>();

        if (delta == 0.0) {
            return list;
        }

        double tickStep = this.calculateTickStep();
        double normalizedTickStep = tickStep / delta;

        if (RadMath.areClose(visibleRange.minimum, 0.0)) {
            visibleRange.minimum = 0.0;
        } else {
            visibleRange.minimum += normalizedTickStep - (visibleRange.minimum % normalizedTickStep);
        }

        if (RadMath.areClose(visibleRange.maximum, 1.0)) {
            visibleRange.maximum = 1.0;
        } else {
            visibleRange.maximum -= (visibleRange.maximum % normalizedTickStep);
        }

        double startTick = Math.max(0.0, visibleRange.getMinimum());
        double endTick = Math.min(1.0, visibleRange.getMaximum());
        double currentTick = startTick;
        double value = this.getActualRange().minimum + (tickStep * (currentTick / normalizedTickStep));

        int virtualIndex = (int) ((value - this.getActualRange().minimum) / tickStep);

        while (currentTick < endTick || RadMath.areClose(currentTick, endTick)) {
            AxisTickModel tick = new MajorTickModel(
                    this.reverseTransformValue(value),
                    currentTick,
                    virtualIndex);

            currentTick += normalizedTickStep;
            value += tickStep;
            virtualIndex++;
            list.add(tick);
        }

        return list;
    }

    public AxisLabelModel generateLastLabel() {
        AxisLabelModel result = new AxisLabelModel(1, RadPoint.getEmpty(), RadSize.getEmpty());

        result.setContent(getLabelContent(new MajorTickModel(this.reverseTransformValue(getMaximum()), 1, 0)));
        result.desiredSize = this.getPresenter().measureContent(result, result.getContent());
        return result;
    }
}

