package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.ScatterBubbleSeriesDataSource;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.BubblePointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;

public class ScatterBubbleSeries extends ScatterPointSeries {
    private DataPointBinding bubbleSizeBinding;
    private float bubbleScale = 1.0f;

    public ScatterBubbleSeries() {
        this.getLabelRenderer().setLabelFormat("X: %.2f, Y: %.2f, Area: %.2f");
    }

    public float getBubbleScale() {
        return this.bubbleScale;
    }

    public void setBubbleScale(float value) {
        this.bubbleScale = value;
    }

    public DataPointBinding getBubbleSizeBinding() {
        return this.bubbleSizeBinding;
    }

    public void setBubbleSizeBinding(DataPointBinding value) {
        if (this.bubbleSizeBinding == value) {
            return;
        }

        this.bubbleSizeBinding = value;

        this.onBubbleSizeBindingChanged(value);
    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        this.scatterPointRenderer = new BubblePointRenderer(this);
        return this.scatterPointRenderer;
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new ScatterBubbleSeriesDataSource(this.model());
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new BubbleSeriesLabelRenderer(this);
    }

    protected void onBubbleSizeBindingChanged(DataPointBinding value) {
        ScatterBubbleSeriesDataSource dataSource = (ScatterBubbleSeriesDataSource) this.dataSource();
        dataSource.setBubbleSizeBinding(value);
    }
}
