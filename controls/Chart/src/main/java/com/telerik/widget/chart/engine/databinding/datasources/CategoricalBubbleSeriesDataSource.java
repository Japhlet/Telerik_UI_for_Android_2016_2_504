package com.telerik.widget.chart.engine.databinding.datasources;

import com.telerik.widget.chart.engine.dataPoints.CategoricalBubbleDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class CategoricalBubbleSeriesDataSource extends CategoricalSeriesDataSource {
    private DataPointBinding bubbleSizeBinding;

    /**
     * Initializes a new instance of the {@link com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSource} with a given chart
     * series owner.
     *
     * @param owner the chart series this data source belongs to.
     */
    public CategoricalBubbleSeriesDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public DataPointBinding getBubbleSizeBinding() {
        return this.bubbleSizeBinding;
    }

    public void setBubbleSizeBinding(DataPointBinding value) {
        if(this.bubbleSizeBinding == value) {
            return;
        }

        this.bubbleSizeBinding = value;

        if(this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected DataPoint createDataPoint() {
        return new CategoricalBubbleDataPoint();
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        super.initializeBinding(binding);

        CategoricalBubbleDataPoint point = (CategoricalBubbleDataPoint) binding.getDataPoint();
        if(this.bubbleSizeBinding != null) {
            Object sizeValue = this.bubbleSizeBinding.getValue(binding.getDataItem());
            if(sizeValue instanceof Number) {
                point.setSize(((Number) sizeValue).doubleValue());
            }
        }
    }
}
