package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.StochasticFastIndicator;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.StochasticIndicatorDataSourceBase;

public class StochasticFastIndicatorDataSource extends StochasticIndicatorDataSourceBase {

    public StochasticFastIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    protected void bindCore() {
        int currentIndex = 0;

        StochasticFastIndicator owner = (StochasticFastIndicator) this.owner.getPresenter();
        ChartSeriesModel signalModel = owner.signalModel();

        int mainPeriod = owner.getMainPeriod();
        int signalPeriod = owner.getSignalPeriod();

        SizedQueue highValues = new SizedQueue();
        highValues.size = mainPeriod;

        SizedQueue lowValues = new SizedQueue();
        lowValues.size = mainPeriod;

        SizedQueue stochValues = new SizedQueue();
        stochValues.size = signalPeriod;

        for (Object item : this.itemsSource) {
            double high = ((Number) this.highBinding.getValue(item)).doubleValue();
            double low = ((Number) this.lowBinding.getValue(item)).doubleValue();
            double close = ((Number) this.closeBinding.getValue(item)).doubleValue();

            double mainValue = calculateMainValue(highValues, lowValues, high, low, close);

            stochValues.enqueueItem(mainValue);

            double signalValue = MovingAverageIndicatorDataSource.calculateCurrentValue(stochValues);

            CategoricalDataPoint point, point2;
            DataPointCollection dataPoints = this.owner.dataPoints();
            if (dataPoints.size() > currentIndex) {
                point = (CategoricalDataPoint) dataPoints.get(currentIndex);
                point.setValue(mainValue);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(mainValue);
                dataPoints.add(point);
            }

            dataPoints = signalModel.dataPoints();
            if (dataPoints.size() > currentIndex) {
                point2 = (CategoricalDataPoint) dataPoints.get(currentIndex);
                point2.setValue(signalValue);
            } else {
                point2 = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point2.setValue(signalValue);
                dataPoints.add(point2);
            }

            currentIndex++;
        }
    }

    private static double calculateMainValue(SizedQueue highValues, SizedQueue lowValues, double high, double low, double close) {
        highValues.enqueueItem(high);
        lowValues.enqueueItem(low);
        double max = highValues.max();
        double min = lowValues.min();

        return (close - min) / (max - min) * 100;
    }

    @Override
    protected void unbind() {
        super.unbind();

        ((StochasticFastIndicator) this.owner.getPresenter()).signalModel().dataPoints().clear();
    }
}
