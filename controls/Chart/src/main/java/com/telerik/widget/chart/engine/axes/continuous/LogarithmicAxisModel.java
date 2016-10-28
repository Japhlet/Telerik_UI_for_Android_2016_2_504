package com.telerik.widget.chart.engine.axes.continuous;

import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.Ohlc;

/**
 * A numerical axis that calculates its values with the Log function.
 */
public class LogarithmicAxisModel extends LinearAxisModel {

    private double logBase;

    static final int LOGARITHM_BASE_PROPERTY_KEY = PropertyKeys.register(LogarithmicAxisModel.class, "LogarithmBase", ChartAreaInvalidateFlags.ALL);

    /**
     * Creates a new instance of the {@link LogarithmicAxisModel} class.
     */
    public LogarithmicAxisModel() {
        this.logBase = 10.0;
    }

    /**
     * Gets the the logarithm base used for normalizing data points' values.
     *
     * @return The log base.
     */
    public double getLogarithmBase() {
        return this.logBase;
    }

    /**
     * Sets the super of the logarithm used for normalizing data points' values.
     *
     * @param value The log base.
     */
    public void setLogarithmBase(double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException("LogarithmBase must be greater than 0.");
        }

        this.setValue(LOGARITHM_BASE_PROPERTY_KEY, value);
    }

    @Override
    double calculateAutoStep(final ValueRange<Double> range) {
        if (this.isLocalValue(DESIRED_TICK_COUNT_PROPERTY_KEY)) {
            double step = (range.maximum - range.minimum) / (this.getDesiredTickCount() - 1);
            return this.normalizeStep(step);
        }

        return 1.0;
    }

    @Override
    double transformValue(double value) {
        if (value <= 0) {
            return 0.0;
        }

        if (this.logBase == 10) {
            // Log10 gives higher precision
            return Math.log10(value);
        }

        return this.logOfBase(value, this.logBase);
    }

    @Override
    Ohlc transformValue(final Ohlc value) {
        return new Ohlc(this.transformValue(value.high()), this.transformValue(value.low()),
                this.transformValue(value.open()), this.transformValue(value.close()));
    }

    @Override
    double reverseTransformValue(double value) {
        return Math.pow(this.logBase, value);
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed).
        if (e.getKey() == LOGARITHM_BASE_PROPERTY_KEY) {
            this.logBase = ((Number) e.newValue()).doubleValue();
        }

        super.onPropertyChanged(e);
    }

    private double logOfBase(double num, double base) {
        return Math.log(num) / Math.log(base);
    }
}

