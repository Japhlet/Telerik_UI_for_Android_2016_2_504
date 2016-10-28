package com.telerik.widget.chart.engine.databinding.datasources;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;
import com.telerik.widget.chart.engine.series.PieSeriesModel;

/**
 * Represents an instance of the {@link SingleValuePointDataSource} class used for storing data
 * sources for the pie charts family.
 */
public class PieSeriesDataSource extends SingleValuePointDataSource {

    /**
     * Initializes a new instance of the {@link PieSeriesDataSource} for a given chart series owner.
     *
     * @param owner the chart series this data source belongs to.
     */
    public PieSeriesDataSource(PieSeriesModel owner) {
        super(owner);
    }

    @Override
    protected DataPoint createDataPoint() {
        return new PieDataPoint();
    }
}

