package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.financial.HighLowCloseIndicatorDataSourceBase;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class StochasticIndicatorDataSourceBase extends HighLowCloseIndicatorDataSourceBase {

    private int mainPeriod;
    private int signalPeriod;

    /**
     * Creates a new instance of the {@link StochasticIndicatorDataSourceBase} class.
     *
     * @param owner the current context.
     */
    public StochasticIndicatorDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    /**
     * Gets the main or fast period.
     *
     * @return the main period.
     */
    public int getMainPeriod() {
        return this.mainPeriod;
    }

    /**
     * Sets the main or fast period.
     *
     * @param value the new fast period.
     */
    public void setMainPeriod(int value) {
        if (this.mainPeriod == value)
            return;

        this.mainPeriod = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    /**
     * Gets the signal or slow period.
     *
     * @return the current signal period.
     */
    public int getSignalPeriod() {
        return this.signalPeriod;
    }

    /**
     * Sets the signal or slow period.
     *
     * @param value the new signal period.
     */
    public void setSignalPeriod(int value) {
        if (this.signalPeriod == value)
            return;

        this.signalPeriod = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }
}
