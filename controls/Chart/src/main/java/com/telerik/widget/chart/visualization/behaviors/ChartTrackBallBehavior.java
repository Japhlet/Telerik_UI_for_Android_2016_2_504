package com.telerik.widget.chart.visualization.behaviors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipContentAdapter;
import com.telerik.android.primitives.widget.tooltip.views.TooltipPresenterBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.behaviors.views.TrackballPresenter;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;
import com.telerik.widget.chart.visualization.cartesianChart.axes.CategoricalAxis;
import com.telerik.widget.chart.visualization.cartesianChart.axes.DateTimeContinuousAxis;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a behavior that adds two lines in {@link com.telerik.widget.chart.visualization.common.RadChartViewBase}'s
 * render surface. The two lines intersect at the center of the closest data point found.
 */
public class ChartTrackBallBehavior extends ChartPopupBehavior {

    private static final String PALETTE_FAMILY_NAME = "Tooltip";

    public static final int BACKGROUND_PROPERTY_KEY = registerProperty(Color.WHITE, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ChartTrackBallBehavior behavior = (ChartTrackBallBehavior)sender;
            ChartTrackballContentAdapter adapter = (ChartTrackballContentAdapter)behavior.contentAdapter();
            adapter.setBackgroundColor(behavior.getBackgroundColor());
        }
    });

    public static final int PADDING_PROPERTY_KEY = registerProperty(0, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ChartTrackBallBehavior behavior = (ChartTrackBallBehavior)sender;
            ChartTrackballContentAdapter adapter = (ChartTrackballContentAdapter)behavior.contentAdapter();

            adapter.setPadding(behavior.getPadding());
        }
    });

    public static final int CATEGORY_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ChartTrackBallBehavior behavior = (ChartTrackBallBehavior)sender;
            ChartTrackballContentAdapter adapter = (ChartTrackballContentAdapter)behavior.contentAdapter();

            adapter.setCategoryTextColor(behavior.getCategoryTextColor());
        }
    });

    public static final int VALUE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ChartTrackBallBehavior behavior = (ChartTrackBallBehavior)sender;
            ChartTrackballContentAdapter adapter = (ChartTrackballContentAdapter)behavior.contentAdapter();

            adapter.setValueTextColor(behavior.getValueTextColor());
        }
    });

    public static final int CATEGORY_SIZE_PROPERTY_KEY = registerProperty(15.0f, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ChartTrackBallBehavior behavior = (ChartTrackBallBehavior)sender;
            ChartTrackballContentAdapter adapter = (ChartTrackballContentAdapter)behavior.contentAdapter();

            adapter.setCategoryTextSize(behavior.getCategoryTextSize());
        }
    });

    public static final int VALUE_SIZE_PROPERTY_KEY = registerProperty(10.0f, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ChartTrackBallBehavior behavior = (ChartTrackBallBehavior)sender;
            ChartTrackballContentAdapter adapter = (ChartTrackballContentAdapter)behavior.contentAdapter();

            adapter.setValueTextSize(behavior.getValueTextSize());
        }
    });

    private TrackBallSnapMode snapMode;
    private boolean showTrackInfo = true;
    private boolean showIntersectionPoints;
    private List<DataPoint> relatedPoints = new ArrayList<>();

    /**
     * Initializes a new instance of the {@link ChartTrackBallBehavior} class.
     */
    public ChartTrackBallBehavior(Context context) {
        super(context);
        this.snapMode = TrackBallSnapMode.CLOSEST_POINT;
    }

    public int getBackgroundColor() {
        return (int)this.getValue(BACKGROUND_PROPERTY_KEY);
    }

    public void setBackgroundColor(int color) {
        this.setValue(BACKGROUND_PROPERTY_KEY, color);
    }

    public int getPadding() {
        return (int)this.getValue(PADDING_PROPERTY_KEY);
    }

    public void setPadding(int value) {
        this.setValue(PADDING_PROPERTY_KEY, value);
    }

    public int getCategoryTextColor() {
        return (int)this.getValue(CATEGORY_COLOR_PROPERTY_KEY);
    }

    @SuppressWarnings("unused")
    public void setCategoryTextColor(int value) {
        this.setValue(CATEGORY_COLOR_PROPERTY_KEY, value);
    }

    public float getCategoryTextSize() {
        return (float)this.getValue(CATEGORY_SIZE_PROPERTY_KEY);
    }

    @SuppressWarnings("unused")
    public void setCategoryTextSize(float value) {
        this.setValue(CATEGORY_SIZE_PROPERTY_KEY, value);
    }

    public float getValueTextSize() {
        return (float)this.getValue(VALUE_SIZE_PROPERTY_KEY);
    }

    @SuppressWarnings("unused")
    public void setValueTextSize(float value) {
        this.setValue(VALUE_SIZE_PROPERTY_KEY, value);
    }

    public int getValueTextColor() {
        return (int) this.getValue(VALUE_COLOR_PROPERTY_KEY);
    }

    @SuppressWarnings("unused")
    public void setValueTextColor(int color) {
        this.setValue(VALUE_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets a value determining whether visual information for all the closest data points will be displayed.
     *
     * @return <code>true</code> if visual information will be displayed, <code>false</code> otherwise.
     */
    public boolean getShowTrackInfo() {
        return this.showTrackInfo;
    }

    /**
     * Sets a value determining whether visual information for all the closest data points will be displayed.
     *
     * @param value the new value.
     */
    @SuppressWarnings("unused")
    public void setShowTrackInfo(boolean value) {
        this.showTrackInfo = value;
    }

    /**
     * Gets a value determining whether visual information for all the closest data points will be displayed.
     *
     * @return <code>true</code> if the information will be displayed, <code>false</code> otherwise.
     */
    public boolean getShowIntersectionPoints() {
        return this.showIntersectionPoints;
    }

    /**
     * Sets a value determining whether visual information for all the closest data points will be displayed.
     *
     * @param value the new value.
     */
    @SuppressWarnings("unused")
    public void setShowIntersectionPoints(boolean value) {
        this.showIntersectionPoints = value;
    }

    /**
     * Gets a list of data points related to the currently tracked one.
     *
     * @return the list of related data points.
     */
    public List<DataPoint> getRelatedDataPoints() {
        return this.relatedPoints;
    }

    /**
     * Gets a value determining how the track ball behaviour will snap to the data points closest to a physical lastLocation.
     *
     * @return the current snap mode.
     */
    @SuppressWarnings("unused")
    public TrackBallSnapMode getSnapMode() {
        return this.snapMode;
    }

    /**
     * Sets a value determining how the track ball behaviour will snap to the data points closest to a physical lastLocation.
     *
     * @param mode the new snap mode.
     */
    @SuppressWarnings("unused")
    public void setSnapMode(TrackBallSnapMode mode) {
        if (this.snapMode == mode)
            return;

        this.snapMode = mode;
    }

    @Override
    public TooltipContentAdapter contentAdapter() {
        if (this.tooltipContentAdapter == null) {
            this.tooltipContentAdapter = new ChartTrackballContentAdapter(this.context);
        }

        return this.tooltipContentAdapter;
    }

    @Override
    public void setContentAdapter(TooltipContentAdapter adapter) {
        this.tooltipContentAdapter = adapter;
    }

    @Override
    public boolean onHold(MotionEvent e) {
        return open(new Point(Math.round(e.getX()), Math.round(e.getY())), new Point(Math.round(e.getRawX()), Math.round(e.getRawY())));
    }

    @Override
    public boolean onDrag(MotionEvent event, MotionEvent event2, RadSize distance, boolean isInHold) {
        if (!this.popupPresenter.isOpen()) {
            return false;
        }

        open(new Point(Math.round(event2.getX()), Math.round(event2.getY())), new Point(Math.round(event2.getRawX()), Math.round(event2.getRawY())));

        return true;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        return this.close();
    }

    @Override
    protected TooltipPresenterBase createPresenter(Context context) {
        return new TrackballPresenter(context, this);
    }

    @Override
    protected Point desiredPopupLocation(DataPoint selectedDataPoint) {
        DataPoint point = this.relatedPoints.get(0);
        if (this.alignTooltipVertically()) {
            return new Point((int) point.getCenterX(), 0);
        } else {
            return new Point(0, (int) point.getCenterY());
        }
    }

    @Override
    protected boolean validateDataContext(ChartDataContext dataContext) {
        return true;
    }

    @Override
    public Object[] getTooltipData(Object context) {
        ChartDataContext chartDataContext = (ChartDataContext) context;

        RadCartesianChartView chart = (RadCartesianChartView) chartDataContext.chart();

        CartesianAxis horizontalAxis = chart.getHorizontalAxis();
        CartesianAxis verticalAxis = chart.getVerticalAxis();

        if (horizontalAxis == null && verticalAxis == null) {
            return null;
        }

        double coordinate;
        Object closestCategory;

        CartesianAxis firstAxis;
        boolean isChartVertical = true;
        if (horizontalAxis instanceof CategoricalAxis || horizontalAxis instanceof DateTimeContinuousAxis) {
            coordinate = chartDataContext.getTouchLocation().x;
            firstAxis = horizontalAxis;
        } else if (verticalAxis instanceof CategoricalAxis) {
            coordinate = chartDataContext.getTouchLocation().y;
            firstAxis = verticalAxis;
            isChartVertical = false;
        } else {
            return this.relatedPoints.toArray();
        }

        closestCategory = firstAxis.getModel().convertPhysicalUnitsToData(coordinate);
        if (closestCategory == null) {
            return this.relatedPoints.toArray();
        }

        this.relatedPoints = firstAxis.getDataPointsForValue(closestCategory);
        if (this.relatedPoints.size() > 0) {
            DataPoint point = this.relatedPoints.get(0);

            RadRect plotAreaClip = this.chart.getPlotAreaClip();
            double start, end, center;
            if (isChartVertical) {
                start = plotAreaClip.getX();
                end = plotAreaClip.getRight();
                center = point.getCenterX();
            } else {
                start = plotAreaClip.getY();
                end = plotAreaClip.getBottom();
                center = point.getCenterY();
            }

            if (center < start || center > end) {
                return null;
            }
        }

        boolean atLeastOnePointWithValueIsPresent = false;
        RadRect slot;
        for (DataPoint point : relatedPoints) {
            slot = point.getLayoutSlot();
            if (slot.getX() != 0 || slot.getY() != 0 ||
                    slot.getWidth() > 0 || slot.getHeight() > 0) {
                atLeastOnePointWithValueIsPresent = true;
                break;
            }
        }

        if (atLeastOnePointWithValueIsPresent)
            return this.relatedPoints.toArray();

        return null;
    }

    @Override
    protected String getPaletteFamilyName() {
        return PALETTE_FAMILY_NAME;
    }

    @Override
    protected void applyPalette(ChartPalette palette) {
        if (palette == null)
            return;

        PaletteEntry entry = palette.getEntry(getPaletteFamilyName(), 0);

        if (entry == null)
            return;

        this.setValue(BACKGROUND_PROPERTY_KEY, PALETTE_VALUE, Color.parseColor(entry.getCustomValue("ContentFill")));
        this.setValue(PADDING_PROPERTY_KEY, PALETTE_VALUE, Integer.parseInt(entry.getCustomValue("Padding")));
        this.setValue(CATEGORY_COLOR_PROPERTY_KEY, PALETTE_VALUE, Color.parseColor(entry.getCustomValue("CategoryTextColor")));
        this.setValue(VALUE_COLOR_PROPERTY_KEY, PALETTE_VALUE, Color.parseColor(entry.getCustomValue("ValueTextColor")));
        this.setValue(CATEGORY_SIZE_PROPERTY_KEY, PALETTE_VALUE, Float.parseFloat(entry.getCustomValue("CategoryTextSize")));
        this.setValue(VALUE_SIZE_PROPERTY_KEY, PALETTE_VALUE, Float.parseFloat(entry.getCustomValue("ValueTextSize")));
    }
}
