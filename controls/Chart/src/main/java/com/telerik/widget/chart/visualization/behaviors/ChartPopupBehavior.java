package com.telerik.widget.chart.visualization.behaviors;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;

import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.primitives.widget.tooltip.contracts.DrawListener;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipAdapter;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipContentAdapter;
import com.telerik.android.primitives.widget.tooltip.views.TooltipPresenterBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.propertyStore.ValueExtractor;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;
import com.telerik.widget.chart.visualization.cartesianChart.axes.CategoricalAxis;
import com.telerik.widget.chart.visualization.cartesianChart.axes.DateTimeContinuousAxis;
import com.telerik.widget.chart.visualization.common.Axis;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.palettes.ChartPalette;

/**
 * Chart behaviour that adds tooltip functionality to the chart instance so that individual data points can be observed in more detail following the user's gestures.
 */
public abstract class ChartPopupBehavior extends ChartBehavior implements TooltipAdapter {
    private TooltipContextNeededListener contextNeededListener;
    private RadRect plotAreaClip;
    private ChartDataContext lastChartContext;
    private Point rawOffset = new Point();

    /**
     * Adapter providing the content view for the popup.
     */
    protected TooltipContentAdapter tooltipContentAdapter;

    /**
     * The range in which a data point will be considered as selected after performing a triggering gesture.
     */
    protected float maxTouchDistanceTolerance;

    /**
     * The current context.
     */
    protected Context context;

    /**
     * The visible part of the tooltip.
     */
    protected TooltipPresenterBase popupPresenter;

    /**
     * The currently selected data point.
     */
    protected DataPoint selectedDataPoint;

    /**
     * Creates an instance of {@link ChartPopupBehavior}.
     *
     * @param context The app context.
     */
    public ChartPopupBehavior(Context context) {
        this.context = context;
        this.popupPresenter = this.createPresenter(context);
        if (this.popupPresenter == null) {
            throw new IllegalStateException("The tooltip presenter can not be null.");
        }

        this.maxTouchDistanceTolerance = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 60);
    }

    /**
     * Gets the layout slot in which the popup will be arranged.
     */
    public Rect availableLayoutSlot() {
        RadRect slot = chart.getChartArea().getLayoutSlot();

        return new Rect((int) slot.getX(), (int) slot.getY(), (int) slot.getRight(), (int) slot.getBottom());
    }

    @Override
    protected DrawListener getDrawListener() {
        return this.getPopupPresenter();
    }

    @Override
    public boolean alignTooltipVertically() {
        if (!(this.chart instanceof RadCartesianChartView)) {
            return false;
        }

        RadCartesianChartView chart = (RadCartesianChartView) this.chart;

        Axis horizontalAxis = chart.getHorizontalAxis();

        return horizontalAxis instanceof CategoricalAxis || horizontalAxis instanceof DateTimeContinuousAxis;
    }

    /**
     * Creates the presenter that will render this behavior.
     *
     * @param context The app context.
     */
    protected abstract TooltipPresenterBase createPresenter(Context context);

    /**
     * Gets the previous chart data context if there is one. If there is no previous context returns null.
     */
    public ChartDataContext getLastChartContext() {
        return this.lastChartContext;
    }

    /**
     * Gets the max touch target distance of the behavior. If the user touches farther than the touch target from a data point, nothing will be displayed.
     */
    public float getMaxTouchDistanceTolerance() {
        return maxTouchDistanceTolerance;
    }

    /**
     * Gets the max touch target distance of the behavior. If the user touches farther than the touch target from a data point, nothing will be displayed.
     *
     * @param value The new max touch distance.
     */
    public void setMaxTouchDistanceTolerance(float value) {
        if (value <= 0)
            throw new IllegalArgumentException("value must be above zero");

        this.maxTouchDistanceTolerance = value;
    }

    /**
     * Gets the presenter that renders this behavior.
     */
    public TooltipPresenterBase getPopupPresenter() {
        return this.popupPresenter;
    }

    /**
     * Sets the presenter that renders this behavior.
     *
     * @param popupPresenter The new popup presenter.
     */
    public void setPopupPresenter(TooltipPresenterBase popupPresenter) {
        if (popupPresenter == null)
            throw new NullPointerException("popupPresenter");

        this.popupPresenter = popupPresenter;
    }

    /**
     * Gets a value stating whether the tooltip is currently being displayed.
     *
     * @return <code>true</code> if the tooltip is visible, <code>false</code> otherwise.
     */
    public boolean isTooltipDisplayed() {
        return this.popupPresenter.isOpen();
    }

    /**
     * Gets the current listener responsible for providing context to the tooltip.
     *
     * @return the current content needed listener.
     */
    public TooltipContextNeededListener getContextNeededListener() {
        return this.contextNeededListener;
    }

    /**
     * Sets the current listener responsible for providing context to the tooltip.
     *
     * @param listener the new listener.
     */
    public void setTooltipContextNeededListener(TooltipContextNeededListener listener) {
        if (listener == null)
            throw new NullPointerException("listener");

        this.contextNeededListener = listener;
    }

    @Override
    public Point rawOffset() {
        return rawOffset;
    }

    @Override
    public Object[] getTooltipData(Object context) {
        ChartDataContext chartContext = (ChartDataContext) context;
        if (chartContext.getDataPoints() == null) {
            return new Object[]{chartContext.getClosestDataPoint().getDataPoint()};
        } else {
            return chartContext.getDataPoints().toArray();
        }
    }

    /**
     * Attempts to open the popup and returns a boolean stating the result of this attempt.
     *
     * @param location    the location at which the gesture was recorded.
     * @param rawLocation the location of the gesture in world coordinates.
     * @return <code>true</code> if the open attempt was successful, <code>false</code> otherwise.
     */
    public boolean open(Point location, Point rawLocation) {
        if (!this.chart.getPlotAreaClip().contains(location.x, location.y)) {
            return false;
        }

        this.lastChartContext = extractTooltipContext(location);
        if (this.lastChartContext == null) {
            this.onOpenFailed();
            return true;
        }

        if (this.lastChartContext.getClosestDataPoint() == null)
            return false;

        this.rawOffset.x = rawLocation.x - location.x;
        this.rawOffset.y = rawLocation.y - location.y;

        this.selectedDataPoint = this.lastChartContext.getClosestDataPoint().getDataPoint();
        ChartSeries series = (ChartSeries)this.selectedDataPoint.getParent().getPresenter();
        if(!series.isVisible()) {
            return false;
        }

        // The palette is applied every tiem (this is not a typo, this is Doge) on open because currently there is no way
        // to get notified when the palette changes.
        if (this.contentAdapter().getIsApplyDefaultStyles()) {
            applyPalette(this.chart.getPalette());
        }

        if (!this.popupPresenter.updateTooltipContent(this.lastChartContext))
            return false;

        this.popupPresenter.open(desiredPopupLocation(this.selectedDataPoint));

        this.chart.invalidate();
        return true;
    }

    /**
     * Attempts to open the popup and returns a boolean stating the result of this attempt.
     *
     * @param dataPoint the data point for which the tooltip will be opened.
     * @return <code>true</code> if the open attempt was successful, <code>false</code> otherwise.
     */
    public boolean open(DataPoint dataPoint) {
        if (dataPoint == null)
            throw new NullPointerException("dataPoint");

        int[] chartLocation = new int[2];
        this.chart.getLocationOnScreen(chartLocation);

        Point location = dataPoint.getCenter();

        return open(location, new Point(location.x + chartLocation[0], location.y + chartLocation[1]));
    }

    /**
     * Extracts context data for a given location.
     *
     * @param location the location for which data context will be extracted.
     * @return the extracted data or null if no data was extracted.
     */
    public ChartDataContext extractTooltipContext(Point location) {
        ChartDataContext defaultContext = this.chart.getDataContext(location);
        ValueExtractor<ChartDataContext> extractor = new ValueExtractor<ChartDataContext>();
        this.onContextNeeded(defaultContext, extractor);

        if (!this.validateDataContext(extractor.value)) {
            return null;
        }

        return extractor.value;
    }

    /**
     * Extracts data context for a given data point using a {@link ValueExtractor}
     *
     * @param point the data point from which data will be extracted.
     * @return the extracted data.
     */
    public ChartDataContext extractTooltipContext(DataPoint point) {
        ChartDataContext defaultContext = this.chart.getDataContext(point);
        ValueExtractor<ChartDataContext> extractor = new ValueExtractor<ChartDataContext>();
        this.onContextNeeded(defaultContext, extractor);

        return extractor.value;
    }

    @Override
    public RadRect getPlotAreaClip() {
        if (this.plotAreaClip == null)
            this.plotAreaClip = chart.getPlotAreaClip();

        return this.plotAreaClip;
    }

    /**
     * Gets the current popup palette family name.
     *
     * @return the family name.
     */
    protected String getPaletteFamilyName() {
        return "";
    }

    /**
     * Used to apply the passed palette to the popup.
     *
     * @param palette the palette to be applied.
     */
    protected abstract void applyPalette(ChartPalette palette);

    /**
     * Validates whether the data context is valid and can be displayed.
     *
     * @param dataContext the data context to be validated.
     * @return <code>true</code> if the context is valid, <code>false</code> if it is not.
     */
    protected boolean validateDataContext(ChartDataContext dataContext) {
        DataPointInfo firstInfo = dataContext.getClosestDataPoint();

        if (firstInfo == null) {
            return false;
        }

        Point firstPoint = dataContext.getTouchLocation();
        Point secondPoint = firstInfo.getDataPoint().getCenter();
        return RadMath.getPointDistance(firstPoint.x, secondPoint.x, firstPoint.y, secondPoint.y) < this.maxTouchDistanceTolerance || firstInfo.getContainsTouchLocation();
    }

    /**
     * Gets invoked when an attempt to open the popup has failed.
     */
    protected void onOpenFailed() {
    }

    /**
     * Closes the popup if needed.
     */
    public boolean close() {
        this.chart.requestRender();
        return this.popupPresenter.close();
    }

    /**
     * Obtains the location at which it is desired for the popup to appear.
     *
     * @param selectedDataPoint the currently selected data point.
     * @return the desired location.
     */
    protected abstract Point desiredPopupLocation(DataPoint selectedDataPoint);

    private boolean onContextNeeded(ChartDataContext defaultContext, ValueExtractor<ChartDataContext> extractor) {
        TooltipContextNeededListener listener = this.getContextNeededListener();
        if (listener == null) {
            extractor.value = defaultContext;
            return false;
        }

        TooltipContextNeededEventArgs args = new TooltipContextNeededEventArgs(defaultContext);
        listener.onContextNeeded(args);

        extractor.value = args.getContext() != null ? args.getContext() : defaultContext;
        return false;
    }

    /**
     * Gets a value determining whether to apply the default styles or not.
     *
     * @return <code>true</code> will result in application of the default styles, <code>false</code> will prevent it.
     */
    public boolean getApplyDefaultStyles() {
        return contentAdapter().getIsApplyDefaultStyles();
    }

    /**
     * Sets a value determining whether to apply the default styles or not.
     *
     * @param applyDefaultStyles the value.
     */
    public void setApplyDefaultStyles(boolean applyDefaultStyles) {
        this.contentAdapter().setApplyDefaultStyles(applyDefaultStyles);
    }
}
