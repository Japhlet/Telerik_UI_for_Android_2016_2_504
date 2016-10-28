package com.telerik.widget.chart.engine.axes.categorical;

import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.AxisUpdateContext;
import com.telerik.widget.chart.engine.axes.MajorTickModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.continuous.ValueRange;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.view.ChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the internal logic of the categorical axis in RadChartView.
 */
public class CategoricalAxisModel extends AxisModel implements AxisSupportsCombinedSeriesPlot {
    private static final int GAP_LENGTH_PROPERTY_KEY = PropertyKeys.register(CategoricalAxisModel.class, "GapLength", ChartAreaInvalidateFlags.ALL);
    private static final int PLOT_MODE_PROPERTY_KEY = PropertyKeys.register(CategoricalAxisModel.class, "PlotMode", ChartAreaInvalidateFlags.ALL);
    private static final int MAJOR_TICK_INTERVAL_PROPERTY_KEY = PropertyKeys.register(CategoricalAxisModel.class, "MajorTickInterval", ChartAreaInvalidateFlags.ALL);

    private AxisPlotMode plotMode;
    private AxisPlotMode actualPlotMode;

    protected ArrayList<AxisCategory> categories;

    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel} class.
     */
    public CategoricalAxisModel() {
        this.categories = new ArrayList<AxisCategory>();
        this.plotMode = AxisPlotMode.BETWEEN_TICKS;
    }

    /**
     * Gets the step at which ticks are positioned.
     *
     * @return The major tick interval.
     */
    public int getMajorTickInterval() {
        return this.getTypedValue(MAJOR_TICK_INTERVAL_PROPERTY_KEY, 1);
    }

    /**
     * Sets the step at which ticks are positioned.
     *
     * @param value The major tick interval.
     */
    public void setMajorTickInterval(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("value cannot be lesser than 1");
        }

        this.setValue(MAJOR_TICK_INTERVAL_PROPERTY_KEY, value);
    }

    /**
     * Gets or sets the plot mode used to position points along the axis.
     *
     * @return The {@link com.telerik.widget.chart.engine.axes.common.AxisPlotMode}.
     */
    public AxisPlotMode getPlotMode() {
        return this.plotMode;
    }

    public void setPlotMode(AxisPlotMode value) {
        this.setValue(PLOT_MODE_PROPERTY_KEY, value);
    }

    /**
     * Gets or sets the length of the gap to be applied for each category.
     *
     * @return The gap length.
     */
    public float getGapLength() {
        return this.getTypedValue(GAP_LENGTH_PROPERTY_KEY, 0.3F);
    }

    @Override
    public int majorTickCount() {
        int categoryCount = this.categories.size();
        return this.actualPlotMode == AxisPlotMode.ON_TICKS ? categoryCount : categoryCount + 1;
    }

    /**
     * Gets or sets the length of the gap to be applied for each category.
     *
     * @param value The new gap length.
     */
    public void setGapLength(float value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("value must not be negative or greater than 1");
        }

        this.setValue(GAP_LENGTH_PROPERTY_KEY, value);
    }

    @Override
    public boolean isDataReady() {
        return this.categories.size() != 0 && super.isDataReady();
    }

    @Override
    public AxisPlotMode getActualPlotMode() {
        return this.actualPlotMode;
    }

    @Override
    public Object getCombineGroupKey(DataPoint point) {
        Object value = point.getValueForAxis(this);
        return this.getCategoryKey(point, value);
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == PLOT_MODE_PROPERTY_KEY) {
            this.plotMode = (AxisPlotMode) e.newValue();
        }

        super.onPropertyChanged(e);
    }

    @Override
    protected void updateCore(AxisUpdateContext context) {
        this.updateActualPlotMode(context.series());
        this.buildCategories(context);
    }

    @Override
    protected void plotCore(AxisUpdateContext context) {
        int count = this.categories.size();
        if (count == 0) {
            return;
        }

        double step = this.calculateRelativeStep(count);

        double value = 0.0;
        double gap = this.getGapLength() * step;

        double position;
        double length = step - gap;

        for (AxisCategory category : this.categories) {
            position = this.actualPlotMode == AxisPlotMode.ON_TICKS ? value - (length / 2) : value + (gap / 2);

            for (DataPoint point : category.points) {
                CategoricalAxisPlotInfo info = CategoricalAxisPlotInfo.create(this, value, step);
                info.categoryKey = category.keySource;
                info.position = position;
                info.length = length;

                point.setValueFromAxis(this, info);
            }

            value += step;
        }
    }

    @Override
    protected Iterable<AxisTickModel> generateTicks(final ValueRange<Double> range) {
        ValueRange<Double> visibleRange = range.clone();

        ArrayList<AxisTickModel> localTicks = new ArrayList<AxisTickModel>();

        // use the BigDecimal type for higher accuracy; see the XML comments on the getVisibleRange method
        int categoryCount = this.categories.size();
        if (categoryCount == 0) {
            return localTicks;
        }
        int tickInterval = this.calculateMajorTickInterval();
        int emptyTickCount = 0;

        int tickCount = this.majorTickCount();
        double tickStep = tickCount == 1 ? 1.0 : 1.0 / (tickCount - 1);
        visibleRange.setMinimum(visibleRange.getMinimum() - (visibleRange.getMinimum() % tickStep));
        visibleRange.setMaximum(visibleRange.getMaximum() + tickStep - (visibleRange.getMaximum() % tickStep));

        double startTick = Math.max(0.0, visibleRange.getMinimum());

        double endTick;
        if (this.actualPlotMode == AxisPlotMode.BETWEEN_TICKS) {
            endTick = Math.min(1, visibleRange.getMaximum());
        } else {
            endTick = Math.min(tickCount == 1 ? 0 : 1, visibleRange.getMaximum());
        }

        int virtualIndex = (int) Math.round(startTick / tickStep);
        if (this.actualPlotMode == AxisPlotMode.ON_TICKS_PADDED) {
            startTick += tickStep / 2.0;
        }
        double currentTick = startTick;
        while (currentTick < endTick || RadMath.areClose(currentTick, endTick)) {
            if (emptyTickCount == 0) {
                AxisTickModel tick = new MajorTickModel(currentTick, currentTick, virtualIndex);
                emptyTickCount = tickInterval - 1;
                localTicks.add(tick);

            } else {
                emptyTickCount--;
            }

            currentTick = currentTick + tickStep;
            virtualIndex++;
        }
        return localTicks;
    }

    @Override
    public AxisLabelModel generateLastLabel() {
        AxisLabelModel lastLabel = new AxisLabelModel(1.0, RadPoint.getEmpty(), RadSize.getEmpty());

        Object content = "";

        if(this.categories.size() > 0) {
            content = this.categories.get(this.categories.size() - 1).key;
        }

        lastLabel.setContent(content);

        lastLabel.desiredSize = this.getPresenter().measureContent(lastLabel, lastLabel.getContent());

        return lastLabel;
    }

    @Override
    protected Object getLabelContentCore(AxisTickModel tick) {
        if (tick.virtualIndex() < this.categories.size()) {
            return this.categories.get(tick.virtualIndex()).key;
        }

        return "";
    }

    void updateActualPlotMode(Iterable<ChartSeriesModel> seriesModels) {
        if (this.isLocalValue(CategoricalAxisModel.PLOT_MODE_PROPERTY_KEY)) {
            this.actualPlotMode = this.plotMode;
        } else if (seriesModels != null) {
            this.actualPlotMode = ChartSeriesModel.selectPlotMode(seriesModels);
        }
    }

    Object getCategoryKey(DataPoint point, Object value) {
        if (value != null) {
            return value;
        }

        return point.collectionIndex() + 1;
    }

    int calculateMajorTickInterval() {
        // reduce the interval proportionally to the zoom factor
        double scale = this.getLayoutStrategy().getZoom();
        return Math.max(1, this.getMajorTickInterval() / (int) (scale + 0.5));
    }

    @Override
    public AxisPlotInfo createPlotInfo(Object value) {
        for (int index = 0; index < this.categories.size(); index++) {
            AxisCategory category = this.categories.get(index);
            if (category.keySource.equals(value)) {
                double step = this.calculateRelativeStep(this.categories.size());
                double gap = this.getGapLength() * step;
                double length = step - gap;
                double valueLength = index * step;

                CategoricalAxisPlotInfo info = CategoricalAxisPlotInfo.create(this, valueLength, step);
                info.categoryKey = value;
                info.position = this.actualPlotMode == AxisPlotMode.ON_TICKS ? valueLength - (length / 2.0) : valueLength + (gap / 2.0);
                info.length = length;

                return info;
            }
        }

        return super.createPlotInfo(value);
    }

    @Override
    public Object convertPhysicalUnitsToData(double coordinate) {
        if (!this.isUpdated()) {
            return super.convertPhysicalUnitsToData(coordinate);
        }

        int count = this.categories.size();
        if (count == 0) {
            return super.convertPhysicalUnitsToData(coordinate);
        }

        RadRect plotArea = this.chartArea.getPlotArea().getLayoutSlot();
        ChartView view = this.chartArea.getView();
        RadRect plotAreaVirtualSize = new RadRect(plotArea.getX(), plotArea.getY(), plotArea.getWidth() * view.getZoomWidth(), plotArea.getHeight() * view.getZoomHeight());
        double position;

        CategoricalDataPointBase firstPoint;
        CategoricalDataPointBase lastPoint;
        CategoricalAxisPlotInfo firstPlot = null;
        CategoricalAxisPlotInfo secondPlot = null;
        if (this.plotMode == AxisPlotMode.BETWEEN_TICKS) {
            firstPoint = (CategoricalDataPointBase) this.categories.get(0).points.get(0);
            lastPoint = (CategoricalDataPointBase) this.categories.get(this.categories.size() - 1).points.get(0);

            if (firstPoint == null || lastPoint == null) {
                return null;
            }

            firstPlot = firstPoint.categoricalPlot;
            secondPlot = lastPoint.categoricalPlot;
        }

        if (this.getType() == AxisType.FIRST) {
            coordinate += Math.abs(view.getPanOffsetX());

            if (this.plotMode == AxisPlotMode.BETWEEN_TICKS) {
                double firstX = firstPlot.centerX(plotAreaVirtualSize);
                position = (coordinate - firstX) / (secondPlot.centerX(plotAreaVirtualSize) - firstX);
            } else {
                position = (coordinate - plotAreaVirtualSize.getX()) / plotAreaVirtualSize.getWidth();
            }
        } else {
            coordinate += Math.abs(view.getPanOffsetY());

            if (this.plotMode == AxisPlotMode.BETWEEN_TICKS) {
                double firstY = firstPlot.centerY(plotAreaVirtualSize);
                position = (coordinate - firstY) / (secondPlot.centerY(plotAreaVirtualSize) - firstY);
            } else {
                position = (coordinate - plotAreaVirtualSize.getY()) / plotAreaVirtualSize.getHeight();
            }
        }

        double step = 1.0 / (count - 1.0);
        int categoryIndex = (int) Math.round(position / step);

        if (categoryIndex < 0 || categoryIndex > count - 1) {
            return super.convertPhysicalUnitsToData(coordinate);
        }

        AxisCategory category = this.categories.get(categoryIndex);
        return category.keySource;
    }

    protected double calculateRelativeStep(int count) {
        double step;
        if (this.actualPlotMode == AxisPlotMode.BETWEEN_TICKS || this.actualPlotMode == AxisPlotMode.ON_TICKS_PADDED) {
            step = 1.0 / count;
        } else {
            step = count == 1 ? 1 : 1.0 / (count - 1.0);
        }
        return step;
    }

    private void buildCategories(AxisUpdateContext context) {
        if (context.series() == null) {
            return;
        }

        this.categories.clear();
        Map<Object, AxisCategory> categoriesByKey = new HashMap<Object, AxisCategory>(8);
        AxisPlotDirection direction = this.getType() == AxisType.FIRST ? AxisPlotDirection.VERTICAL : AxisPlotDirection.HORIZONTAL;

        for (ChartSeriesModel series : context.series()) {
            // tell each series what is the plot direction
            series.setValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, direction);

            for (Object point : series.visibleDataPoints()) {
                DataPoint dataPoint = (DataPoint) point;
                Object value = dataPoint.getValueForAxis(this);
                Object categoryKey = this.getCategoryKey(dataPoint, value);
                if (categoryKey == null) {
                    continue;
                }

                AxisCategory category = categoriesByKey.get(categoryKey);
                if (category == null) {
                    category = new AxisCategory();
                    category.key = categoryKey;
                    category.keySource = value;
                    categoriesByKey.put(categoryKey, category);
                    this.categories.add(category);
                }

                category.points.add(dataPoint);
            }
        }
    }
}

