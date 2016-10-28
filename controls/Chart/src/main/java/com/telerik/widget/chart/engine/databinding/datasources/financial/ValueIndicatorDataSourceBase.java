package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.databinding.PropertyNameDataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSource;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.ValueIndicatorBase;

import java.beans.PropertyChangeEvent;

public abstract class ValueIndicatorDataSourceBase extends CategoricalSeriesDataSource {
    public ValueIndicatorDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        ValueIndicatorBase owner = (ValueIndicatorBase) this.getOwner().getPresenter();

        PropertyNameDataPointBinding propertyNameValueBinding = (PropertyNameDataPointBinding) owner.getValueBinding();
        PropertyNameDataPointBinding propertyNameCategoryBinding = (PropertyNameDataPointBinding) owner.getCategoryBinding();

        String propertyName = e.getPropertyName();

        if (propertyNameValueBinding != null &&
                propertyNameCategoryBinding != null &&
                !propertyName.equals(propertyNameValueBinding.getPropertyName()) &&
                !propertyName.equals(propertyNameCategoryBinding.getPropertyName())) {
            return;
        }

        super.propertyChange(e);
    }
}
