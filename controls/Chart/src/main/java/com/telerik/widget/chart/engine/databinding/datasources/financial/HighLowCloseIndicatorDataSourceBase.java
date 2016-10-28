package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class HighLowCloseIndicatorDataSourceBase extends HighLowIndicatorDataSourceBase {
    public HighLowCloseIndicatorDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    protected DataPointBinding closeBinding;

    public DataPointBinding getCloseBinding() {
        return this.closeBinding;
    }

    public void setCloseBinding(DataPointBinding value) {
        if (this.closeBinding == value) {
            return;
        }

        this.closeBinding = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }
}
