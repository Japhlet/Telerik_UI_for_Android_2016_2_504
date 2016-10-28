package com.telerik.widget.chart.engine.series;

import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineMode;
import com.telerik.widget.chart.engine.series.combination.SupportCombineMode;

/**
 * Base class for all categorical series models.
 */
public abstract class CategoricalSeriesModel extends SeriesModelWithAxes<CategoricalDataPointBase> implements SupportCombineMode {

    public static final int COMBINE_MODE_PROPERTY_KEY = PropertyKeys.register(CategoricalSeriesModel.class, "CombineMode", ChartAreaInvalidateFlags.ALL);
    public static final int STACK_GROUP_KEY_PROPERTY_KEY = PropertyKeys.register(CategoricalSeriesModel.class, "StackGroupKey", ChartAreaInvalidateFlags.ALL);

    /**
     * Creates a new instance of the {@link CategoricalSeriesModel} class.
     */
    public CategoricalSeriesModel() {
        this.trackPropertyChanged = true;
    }

    /**
     * Gets the {@link ChartSeriesCombineMode} value that specifies whether this instance should be combined with other instances of same type.
     */
    @Override
    public ChartSeriesCombineMode getCombineMode() {
        return this.getTypedValue(COMBINE_MODE_PROPERTY_KEY,
                ChartSeriesCombineMode.NONE);
    }

    /**
     * Sets the {@link ChartSeriesCombineMode} value that specifies whether this instance should be combined with other instances of same type.
     */
    public void setCombineMode(ChartSeriesCombineMode value) {
        this.setValue(COMBINE_MODE_PROPERTY_KEY, value);
    }

    /**
     * Gets the key that identifies the stack this instance should be put into.
     */
    @Override
    public Object getStackGroupKey() {
        return this.getValue(STACK_GROUP_KEY_PROPERTY_KEY);
    }

    /**
     * Sets the key that identifies the stack this instance should be put into.
     */
    public void setStackGroupKey(Object value) {
        this.setValue(STACK_GROUP_KEY_PROPERTY_KEY, value);
    }

    @Override
    public AxisPlotMode getDefaultPlotMode() {
        return AxisPlotMode.BETWEEN_TICKS;
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child instanceof CategoricalDataPoint) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }
}
