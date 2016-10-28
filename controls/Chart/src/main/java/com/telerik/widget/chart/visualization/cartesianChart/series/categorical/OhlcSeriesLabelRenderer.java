package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public class OhlcSeriesLabelRenderer extends CategoricalSeriesLabelRenderer {

    protected static final String LABEL_FORMAT = "O: %s\nH: %s\nL: %s\nC: %s";

    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.series.categorical.OhlcSeriesLabelRenderer} class.
     *
     * @param owner the chart series owning this renderer instance.
     */
    public OhlcSeriesLabelRenderer(ChartSeries owner) {
        super(owner);
    }

    @Override
    protected String getLabelText(DataPoint dataPoint) {
        OhlcDataPoint ohlcPoint = (OhlcDataPoint)dataPoint;

        return String.format(this.getLabelFormat(), ohlcPoint.getOpen(), ohlcPoint.getHigh(), ohlcPoint.getLow(), ohlcPoint.getClose());
    }

    @Override
    public String getLabelFormat() {
        return LABEL_FORMAT;
    }
}
