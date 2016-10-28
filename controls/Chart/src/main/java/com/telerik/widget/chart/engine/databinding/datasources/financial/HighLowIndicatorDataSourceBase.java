package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSource;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class HighLowIndicatorDataSourceBase extends CategoricalSeriesDataSource {
    public HighLowIndicatorDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    protected DataPointBinding highBinding;
    protected DataPointBinding lowBinding;

    public DataPointBinding getHighBinding() {
        return this.highBinding;
    }

    public void setHighBinding(DataPointBinding value) {
        if (this.highBinding == value) {
            return;
        }

        this.highBinding = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }

    public DataPointBinding getLowBinding() {
        return this.lowBinding;
    }

    public void setLowBinding(DataPointBinding value) {
        if (this.lowBinding == value) {
            return;
        }

        this.lowBinding = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }
}
