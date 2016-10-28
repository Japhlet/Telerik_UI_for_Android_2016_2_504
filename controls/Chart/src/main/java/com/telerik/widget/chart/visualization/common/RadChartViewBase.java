package com.telerik.widget.chart.visualization.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telerik.android.common.Function2;
import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.android.primitives.widget.tooltip.contracts.DrawListener;
import com.telerik.widget.chart.R;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.view.ChartView;
import com.telerik.widget.chart.visualization.behaviors.BehaviorCollectionChangedInfo;
import com.telerik.widget.chart.visualization.behaviors.ChartBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartBehaviorCollection;
import com.telerik.widget.chart.visualization.behaviors.ChartDataContext;
import com.telerik.widget.chart.visualization.behaviors.ChartScaleGestureDetector;
import com.telerik.widget.chart.visualization.behaviors.DataPointInfo;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.ChartPalettes;
import com.telerik.widget.primitives.legend.LegendInfoProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for all chart controls that are categorized mainly by the
 * coordinate system used to plot their points.
 */
public abstract class RadChartViewBase<T extends ChartSeries> extends View implements ChartView, LegendInfoProvider, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ChartScaleGestureDetector.OnScaleGestureListener {
    private List<ChartElementPresenter> presenters = new ArrayList<>();
    private RadSize maxZoom = new RadSize(100, 100);
    private RadSize zoom = new RadSize(1, 1);
    private RadPoint panOffset = new RadPoint();
    private RadPoint deferredPanOffset = null;
    private RadSize deferredZoom = null;
    private ChartAreaModel chartArea;
    private RadSize availableSize = RadSize.getEmpty();
    private Object emptyContent;
    private ChartBehaviorCollection behaviorsCache = new ChartBehaviorCollection(this);
    private ChartDataContext lastDataContext;
    private GestureDetector gestureDetector;
    private ChartScaleGestureDetector scaleDetector;
    private List<DrawListener> onDrawListeners = new ArrayList<>();
    private ChartPalette selectionPalette;
    private StackedSeriesContext stackedSeriesContext;
    private ChartPalette palette;
    private TextView emptyContentPresenter;
    private boolean arrangeRequested = false;

    protected boolean updateSuspended;
    protected boolean renderSuspended;
    protected float paddingLeft;
    protected float paddingRight;
    protected float paddingTop;
    protected float paddingBottom;

    CountDownTimer timer;
    private boolean moveOccurred = false;
    private boolean holdCancelled = false;
    private boolean isInHold = false;
    private MotionEvent holdEvent;
    private int milliseconds = 500;

    private PresenterCollection<T> series;

    /**
     * States if the context should be clipped to the given bounds.
     */
    protected boolean clipToBounds = false;

    /**
     * Holds the old width of the chart.
     */
    protected int oldWidth = 0;

    /**
     * Holds the old height of the chart.
     */
    protected int oldHeight = 0;

    ChartPresenterImpl presenterImpl;

    /**
     * Initializes a new instance of the {@link RadChartViewBase} class with passed
     * {@link Context} as argument.
     *
     * @param context context to be used.
     */
    protected RadChartViewBase(Context context) {
        this(context, null);
    }

    /**
     * Initializes a new instance of the {@link RadChartViewBase} class with passed {@link Context} and
     * attribute set as arguments.
     *
     * @param context context to be used.
     * @param attrs   attributes to be used.
     */
    public RadChartViewBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initializes a new instance of the {@link RadChartViewBase} class with passed {@link Context},
     * attribute set and def style as arguments.
     *
     * @param context  context to be used.
     * @param attrs    attributes to be used.
     * @param defStyle def style to be used.
     */
    public RadChartViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        this.emptyContentPresenter = new TextView(context);
        this.emptyContentPresenter.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        presenterImpl = new ChartPresenterImpl(this);

        this.setPalette(ChartPalettes.light(context));
        this.setSelectionPalette(ChartPalettes.lightSelected(context));

        float defaultPadding = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5);
        paddingLeft = defaultPadding;
        paddingTop = defaultPadding;
        paddingRight = defaultPadding;
        paddingBottom = defaultPadding;

        this.behaviorsCache = new ChartBehaviorCollection(this);
        this.gestureDetector = new GestureDetector(context, this);
        this.gestureDetector.setIsLongpressEnabled(false);
        this.scaleDetector = new ChartScaleGestureDetector(context, this);

        this.clipToBounds = true;

        this.chartArea = this.createChartAreaModel();
        this.stackedSeriesContext = new StackedSeriesContext();

        if (attrs == null) {
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, com.telerik.widget.chart.R.styleable.RadChartViewBase,
                defStyle, com.telerik.widget.chart.R.style.RadChartBaseStyle);

        this.initFromXML(array);
    }

    @SuppressWarnings("unused")
    public int getHoldDelay() {
        return this.milliseconds;
    }

    @SuppressWarnings("unused")
    public void setHoldDelay(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Gets the collection containing all the series presented by this instance.
     *
     * @return the collection of series for the current selection.
     */
    public PresenterCollection<T> getSeries() {
        return this.series;
    }

    /**
     * Sets the collection containing all the series presented by this instance.
     *
     * @param newSeries the new collection of series.
     */
    public void setSeries(PresenterCollection<T> newSeries) {
        RadChartViewBase chart = this;

        PresenterCollection<T> oldSeries = this.series;
        if (oldSeries != null) {
            for (ChartSeries series : oldSeries) {
                chart.onPresenterRemoved(series);
            }
            oldSeries.reset();
        }

        if (newSeries != null && newSeries.owner() != chart) {
            newSeries.init(chart);
            for (ChartSeries series : newSeries) {
                chart.onPresenterAdded(series);
            }
        }

        this.series = newSeries;

        chart.requestInvalidateArrange();
    }

    public void addOnDrawListener(DrawListener listener) {
        this.onDrawListeners.add(listener);
    }

    public void removeOnDrawListener(DrawListener listener) {
        this.onDrawListeners.remove(listener);
    }

    public ChartElementPresenter presenterImpl() {
        return this.presenterImpl;
    }

    public List<? extends ChartElementPresenter> presenters() {
        return this.presenters;
    }

    /**
     * Gets the stacked series context.
     */
    public StackedSeriesContext stackedSeriesContext() {
        return this.stackedSeriesContext;
    }

    /**
     * Makes sure the current chart instance supports a specific behavior.
     *
     * @param behavior the behaviour to be checked.
     */
    public void validateBehaviourSupport(ChartBehavior behavior) {
    }

    /**
     * Used to redraw the chart elements. Can be called from any thread.
     */
    public void requestRender() {
        if (renderSuspended || updateSuspended || arrangeRequested)
            return;

        this.renderSuspended = true;
        this.post(new Runnable() {
            @Override
            public void run() {
                RadChartViewBase.this.invalidate();
                RadChartViewBase.this.renderSuspended = false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(this.emptyContentPresenter.getLeft(), this.emptyContentPresenter.getTop());
        this.emptyContentPresenter.draw(canvas);
        canvas.restore();

        for (ChartElementPresenter presenter : this.presenters) {
            if (presenter.isVisible()) {
                presenter.render(canvas);
            }
        }

        for (ChartElementPresenter presenter : this.presenters) {
            if (presenter.isVisible()) {
                presenter.postRender(canvas);
            }
        }

        for (DrawListener listener : this.onDrawListeners) {
            listener.notifyDraw(canvas);
        }
    }

    private RadSize getZoomValue(String attribute) {
        String[] value = attribute.split(",");

        if (value.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid XML attribute value. The zoom or maxZoom attribute accepts a string of two numbers delimited by a comma.");
        }

        return new RadSize(Double.parseDouble(value[0]), Double.parseDouble(value[1]));
    }

    private ChartPalette parsePalette(String attribute) {
        if (attribute.toLowerCase().equals("light")) {
            return ChartPalettes.light(getContext());
        } else if (attribute.toLowerCase().equals("dark")) {
            return ChartPalettes.dark(getContext());
        }

        return null;
    }

    private void onClipToBoundsPropertyChanged(boolean newValue) {
        this.clipToBounds = newValue;
        this.requestRender();
    }

    private RadSize normalizeAvailableSize(RadSize availableSize) {
        double width = availableSize.getWidth();

        View parent = (View) this.getParent();

        if (Double.isInfinite(width)) {
            width = parent.getWidth();
        }

        double height = availableSize.getHeight();
        if (Double.isInfinite(height)) {
            height = parent.getHeight();
        }

        return new RadSize(width, height);
    }

    private void applyDeferredPanZoomActions() {
        if (this.deferredZoom != null) {
            RadSize zoom = new RadSize(this.deferredZoom.getWidth(), this.deferredZoom.getHeight());
            this.deferredZoom = null;
            this.setZoom(zoom);
        }

        if (this.deferredPanOffset != null) {
            RadPoint offset = new RadPoint(this.deferredPanOffset.getX(), this.deferredPanOffset.getY());
            this.deferredPanOffset = null;
            this.setPanOffset(offset);
        }
    }

    private void onBehaviorReplaced(ChartBehavior newBehavior, ChartBehavior oldBehavior) {
        oldBehavior.detach();
        newBehavior.attach(this);
    }

    private void onBehaviorsReset() {
        for (ChartBehavior behavior : this.behaviorsCache) {
            behavior.detach();
        }
    }

    private void onBehaviorRemoved(ChartBehavior removed) {
        removed.detach();
    }

    private void onBehaviorAdded(ChartBehavior added) {
        added.attach(this);
    }

    private Pair<List<DataPointInfo>, DataPointInfo> findClosestPoints(Point tapLocation) {
        List<DataPointInfo> closestPoints = new ArrayList<>();
        double totalMinDistance = Double.POSITIVE_INFINITY;

        DataPointInfo closestPoint = null;

        for (ChartSeriesModel series : this.chartArea.getSeries()) {
            ChartSeries visualSeries = (ChartSeries) series.getPresenter();
            DataPointInfo currentClosestDataPoint = visualSeries.findClosestPoint(tapLocation);

            if (currentClosestDataPoint != null) {
                double distance = currentClosestDataPoint.getDistanceToTouchLocation();

                if (distance < totalMinDistance) {
                    totalMinDistance = distance;
                    closestPoint = currentClosestDataPoint;
                    if (closestPoint.getDataPoint()
                            .getLayoutSlot().contains(tapLocation.x, tapLocation.y)) {
                        closestPoint.setContainsTouchLocation(true);
                    } else if (visualSeries.hitTestDataPoint(new PointF(tapLocation.x, tapLocation.y)) == closestPoint.getDataPoint()) {
                        closestPoint.setContainsTouchLocation(true);
                    }
                }

                closestPoints.add(currentClosestDataPoint);
            }
        }

        Collections.sort(closestPoints, new Comparator<DataPointInfo>() {
            @Override
            public int compare(DataPointInfo lhs, DataPointInfo rhs) {
                double left = lhs.getDistanceToTouchLocation();
                double right = rhs.getDistanceToTouchLocation();
                if (left < right || lhs.getContainsTouchLocation()) {
                    return -1;
                } else if (left > right) {
                    return 1;
                }

                return 0;
            }
        });

        return new Pair<>(closestPoints, closestPoint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        for (PresenterBase presenter : this.presenters)
            presenter.onLoaded();

        LicensingProvider.verify(this.getContext());

        this.presenterImpl.onLoaded();
        this.sortPresenters();
    }

    protected void processPaletteChanged() {
        for (ChartElementPresenter presenter : this.presenters) {
            presenter.processPaletteChanged();
        }
    }

    @Override
    public double getViewportWidth() {
        return this.availableSize.getWidth();
    }

    @Override
    public double getViewportHeight() {
        return this.availableSize.getHeight();
    }

    /**
     * Makes arrangements that precede an invalidation.
     */
    public void requestInvalidateArrange() {
        if (this.updateSuspended || this.arrangeRequested) {
            return;
        }

        this.arrangeRequested = true;
        this.post(new Runnable() {
            @Override
            public void run() {
                RadChartViewBase.this.updateEngine();
                RadChartViewBase.this.arrangeRequested = false;
            }
        });
    }

    /**
     * Marks the presenter as not loaded.
     */
    protected void onUnloaded() {
        for (PresenterBase presenter : this.presenters) {
            presenter.onUnloaded();
        }

        this.presenterImpl.onUnloaded();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        if (w != this.oldWidth || h != this.oldHeight) {
            this.oldWidth = w;
            this.oldHeight = h;
        }

        this.setMeasuredDimension(w, h);
        this.arrangeOverride(w, h);

        this.arrangeRequested = false;
    }
    
    private void updateEmptyContent(int width, int height) {
        this.emptyContentPresenter.setText(String.valueOf(this.getEmptyContent()));
        this.emptyContentPresenter.measure(width, height);
        this.layoutEmptyContent();
    }

    private void layoutEmptyContent() {
        int middleX = this.getMeasuredWidth() / 2;
        int middleY = this.getMeasuredHeight() / 2;

        int measuredWidth = this.emptyContentPresenter.getMeasuredWidth();
        int measuredHeight = this.emptyContentPresenter.getMeasuredHeight();
        int left = middleX - measuredWidth / 2;
        int right = left + measuredWidth;
        int top = middleY - measuredHeight / 2;
        int bottom = top + measuredHeight;
        this.emptyContentPresenter.layout(left, top, right, bottom);
    }

    private void updateEngine() {
        this.arrangeOverride(this.getMeasuredWidth(), this.getMeasuredHeight());
        this.invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        this.onUnloaded();

        this.resetBehaviors();
    }

    @Override
    public double getZoomWidth() {
        return this.zoom.getWidth();
    }

    @Override
    public double getZoomHeight() {
        return this.zoom.getHeight();
    }

    @Override
    public double getPanOffsetX() {
        return this.panOffset.getX();
    }

    @Override
    public double getPanOffsetY() {
        return this.panOffset.getY();
    }

    @Override
    public RadRect getPlotAreaClip() {
        return new RadRect(0, 0, this.availableSize.getWidth(), this.availableSize.getHeight());
    }

    /**
     * Used to update the current size, to trigger the UI update function and to mark that the
     * arrange has passed.
     *
     * @param width  the new final width.
     * @param height the new final height.
     * @return the new final size.
     */
    protected RadSize arrangeOverride(int width, int height) {
        this.updateEmptyContent(width, height);
        RadSize finalSize = new RadSize(width, height);
        this.availableSize = this.normalizeAvailableSize(finalSize);
        this.updateChartArea();
        ChartLayoutContext context = new ChartLayoutContext(this.availableSize,
                this.zoom, new RadPoint(this.panOffset.getX(), this.panOffset.getY()), this.getPlotAreaClip());

        this.presenterImpl.updateUI(context);

        this.applyDeferredPanZoomActions();

        return finalSize;
    }

    /**
     * Used to handle the addition of a new presenter.
     *
     * @param presenter the new presenter to be added.
     */
    public void onPresenterAdded(ChartElementPresenter presenter) {
        this.presenters.add(presenter);
        Collections.sort(this.presenters, new Comparator<ChartElementPresenter>() {
            @Override
            public int compare(ChartElementPresenter lhs, ChartElementPresenter rhs) {
                Integer leftIndex = lhs.getCollectionIndex();
                return leftIndex.compareTo(rhs.getCollectionIndex());
            }
        });

        presenter.attach(this);
        this.resetBehaviors();

        this.requestInvalidateArrange();
    }

    /**
     * Gets the current chart area.
     *
     * @return the current chart area.
     */
    public ChartAreaModel getChartArea() {
        return this.chartArea;
    }

    /**
     * Initializes from xml using the styleable resources of the chart.
     *
     * @param array collection of index values to be red from the xml.
     */
    protected void initFromXML(TypedArray array) {
        String attribute;

        if (array.hasValue(com.telerik.widget.chart.R.styleable.RadChartViewBase_pan)) {
            attribute = array.getString(com.telerik.widget.chart.R.styleable.RadChartViewBase_pan);
            if (attribute == null) {
                attribute = "";
            }

            String[] value = attribute.split(",");
            if (value.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid XML attribute value. The pan attribute accepts a string of two numbers delimited by a comma.");
            }

            double x = Integer.parseInt(value[0].trim());
            double y = Integer.parseInt(value[1].trim());
            this.setPanOffset(new RadPoint(x, y));
        }

        if (array.hasValue(R.styleable.RadChartViewBase_chartZoom)) {
            attribute = array.getString(R.styleable.RadChartViewBase_chartZoom);
            RadSize zoom = this.getZoomValue(attribute);

            this.setZoom(zoom);
        }

        if (array.hasValue(com.telerik.widget.chart.R.styleable.RadChartViewBase_maxZoom)) {
            attribute = array.getString(com.telerik.widget.chart.R.styleable.RadChartViewBase_maxZoom);

            this.setMaxZoom(this.getZoomValue(attribute));
        }

        if (array.hasValue(com.telerik.widget.chart.R.styleable.RadChartViewBase_emptyContent)) {
            attribute = array.getString(com.telerik.widget.chart.R.styleable.RadChartViewBase_emptyContent);

            this.setEmptyContent(attribute);
        }

        if (array.hasValue(com.telerik.widget.chart.R.styleable.RadChartViewBase_palette)) {
            attribute = array.getString(com.telerik.widget.chart.R.styleable.RadChartViewBase_palette);

            this.palette = this.parsePalette(attribute);
        }

        if (array.hasValue(com.telerik.widget.chart.R.styleable.RadChartViewBase_clipToBounds)) {
            Boolean clipToBounds = array.getBoolean(com.telerik.widget.chart.R.styleable.RadChartViewBase_clipToBounds, this.clipToBounds);

            this.setClipToBounds(clipToBounds);
        }
    }

    public ChartPalette getSelectionPalette() {
        return this.selectionPalette;
    }

    public void setSelectionPalette(ChartPalette value) {
        if (this.selectionPalette != null && this.selectionPalette == value) {
            return;
        }
        this.selectionPalette = value;
        this.processPaletteChanged();
    }

    /**
     * Used to arrange all views according to their Z-Index and then rearrange them on the
     * render surface.
     */
    public void sortPresenters() {
        int childCount = this.presenters.size();
        ChartElementPresenter[] sortedPresenters = new ChartElementPresenter[childCount];
        for (int i = 0; i < childCount; ++i) {
            sortedPresenters[i] = this.presenters.get(i);
        }

        Arrays.sort(sortedPresenters, new Comparator<ChartElementPresenter>() {
            @Override
            public int compare(ChartElementPresenter lhs, ChartElementPresenter rhs) {
                Integer leftIndex = lhs.getZIndex();
                Integer rightIndex = rhs.getZIndex();

                return leftIndex.compareTo(rightIndex);
            }
        });

        this.presenters.clear();
        Collections.addAll(this.presenters, sortedPresenters);
    }

    /**
     * Gets the current chart area.
     *
     * @return the current chart area.
     */
    public ChartAreaModel chartAreaModel() {
        return this.chartArea;
    }

    /**
     * Gets the value indicating whether the chart content will be clipped to the control's bounds.
     */
    public boolean isClipToBounds() {
        return this.clipToBounds;
    }

    /**
     * Sets the value indicating whether the chart content will be clipped to the control's bounds.
     *
     * @param value the new value for the clip to bounds.
     */
    public void setClipToBounds(boolean value) {
        if (this.clipToBounds == value) {
            return;
        }

        this.onClipToBoundsPropertyChanged(value);
    }

    /**
     * Begins an update block during which the chart will not automatically update itself. The {@link #endUpdate()} method is
     * used to end this update block and causes the chart to redraw itself once to reflect the changes made during the update block.
     * The beginUpdate() endUpdate() block should be used as a performance improvement if the chart has to be updated several times in a row.
     */
    public void beginUpdate() {
        this.updateSuspended = true;
    }

    /**
     * Ends the update block and forces one redraw of the chart either synchronously or asynchronously.
     */
    public void endUpdate(boolean asynchronous) {
        this.updateSuspended = false;

        if(asynchronous) {
            this.requestInvalidateArrange();
        } else {
            this.updateEngine();
        }
    }

    /**
     * Ends the update block and forces one redraw of the chart asynchronously.
     */
    public void endUpdate() {
        this.endUpdate(true);
    }

    /**
     * Gets the content to be displayed when the chart is either not properly initialized or if
     * it's missing data.
     *
     * @return the empty content.
     */
    public Object getEmptyContent() {
        if (this.emptyContent != null) {
            return this.emptyContent;
        }

        return this.generateEmptyContent();
    }

    /**
     * Sets the content to be displayed when the chart is either not properly initialized or if
     * it's missing data.
     *
     * @param value the new empty content.
     */
    public void setEmptyContent(Object value) {
        if (this.emptyContent == value) {
            return;
        }

        this.emptyContent = value;
        this.requestLayout();
    }

    /**
     * Used to set the padding of every side of the chart.
     *
     * @param left   padding on the left side.
     * @param top    padding on the top side.
     * @param right  padding on the right side.
     * @param bottom padding on the bottom side.
     */
    public void setChartPadding(float left, float top, float right, float bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
    }

    /**
     * Sets uniform padding on the chart.
     *
     * @param padding The padding to set.
     */
    public void setChartPadding(float padding) {
        this.setChartPadding(padding, padding, padding, padding);
    }

    /**
     * Used to remove and detach a given presenter from the chart.
     *
     * @param presenter presenter to be removed.
     */
    public void onPresenterRemoved(ChartElementPresenter presenter) {
        this.presenters.remove(presenter);
        presenter.detach();
        this.resetBehaviors();

        this.requestInvalidateArrange();
    }

    /**
     * Generates a string holding information about the load state of the chart area.&nbsp;
     * It either returns the reasons if a chart area failed to load or the state of the series
     * and the data inside the plot area.
     *
     * @return the generated string.
     */
    public String generateEmptyContent() {
        StringBuilder builder = new StringBuilder();

        for (String notLoadedReason : this.chartArea.getNotLoadedReasons()) {
            builder.append(notLoadedReason);
            builder.append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    /**
     * Creates the model of the plot area.
     *
     * @return the created model.
     */
    public abstract ChartAreaModel createChartAreaModel();

    /**
     * Prepares the plot area model so that it may be visualized.
     */
    protected void updateChartArea() {
        // prepare plot area
        if (!this.chartArea.isTreeLoaded()) {
            this.chartArea.loadElementTree(this);
        }
        this.chartArea.arrange(new RadRect(this.paddingLeft,
                this.paddingTop,
                this.getViewportWidth() - (this.paddingLeft + this.paddingRight),
                this.getViewportHeight() - (this.paddingTop + this.paddingBottom)));
    }

    /**
     * Gets the current zoom (scale) of the chart.
     *
     * @return the current chart zoom.
     */
    public RadSize getZoom() {
        return this.zoom;
    }

    /**
     * Gets the maximum allowed zoom for this instance.
     *
     * @return the maximum zoom.
     */
    public RadSize getMaxZoom() {
        return this.maxZoom;
    }

    /**
     * Gets the maximum allowed zoom for this instance.
     *
     * @param value the new maximum zoom.
     */
    public void setMaxZoom(RadSize value) {
        RadChartViewBase chart = this;

        if (value.getWidth() < 1 || value.getHeight() < 1) {
            throw new IllegalArgumentException("The values for both width and height must be greater than or equal to 1.");
        }

        chart.maxZoom = value;
        chart.setZoom(chart.zoom);
    }

    /**
     * Gets the current pan offset.
     *
     * @return the current pan offset.
     */
    public RadPoint getPanOffset() {
        return this.panOffset;
    }

    /**
     * Sets the current pan offset.
     *
     * @param value the new pan offset.
     */
    public void setPanOffset(RadPoint value) {
        this.setPanOffset(value.getX(), value.getY());
    }

    public void setPanOffset(double x, double y) {
        RadPoint newOffset = new RadPoint(x, y);

        newOffset = this.clampTranslate(newOffset, this.zoom);

        if (this.panOffset.equals(newOffset)) {
            return;
        }

        this.panOffset = newOffset;
        this.onPanOffsetChanged();

        requestInvalidateArrange();
    }

    /**
     * Called when the zoom factor of the chart changes.
     * Triggers the {@link ChartAreaModel#onZoomChanged()} so that the chart area can respond to
     * the zoom change event.
     */
    protected void onZoomChanged() {
        this.chartArea.onZoomChanged();
    }

    /**
     * Called when the pan offset of the chart changes.
     * Triggers the {@link ChartAreaModel#onPanOffsetChanged()} ()} so that the chart area can respond to
     * the change in the pan offset.
     */
    protected void onPanOffsetChanged() {
        this.chartArea.onPanOffsetChanged();
    }

    /**
     * Used to translate a given {@link Point} according to a given zoom value presented as a {@link RadSize}
     *
     * @param translate the point to be translated.
     * @param newZoom   the new zoom to be used when translating the point.
     * @return the translated point.
     */
    public RadPoint clampTranslate(RadPoint translate, RadSize newZoom) {

        RadPoint newTranslate = new RadPoint(translate.getX(), translate.getY());

        double x = newTranslate.getX();
        if (x >= 0) {
            x = 0;
        } else {
            double zoomWidth = newZoom.getWidth() * this.chartArea.getPlotArea().getLayoutSlot().getWidth();
            if (zoomWidth + x < this.chartArea.getPlotArea().getLayoutSlot().getWidth()) {
                x = Math.round(this.chartArea.getPlotArea().getLayoutSlot().getWidth() - zoomWidth);
            }
        }

        double y = newTranslate.getY();
        if (y >= 0) {
            y = 0;
        } else {
            double zoomHeight = newZoom.getHeight() * this.chartArea.getPlotArea().getLayoutSlot().getHeight();
            if (zoomHeight + y < this.chartArea.getPlotArea().getLayoutSlot().getHeight()) {
                y = (int) Math.round(this.chartArea.getPlotArea().getLayoutSlot().getHeight() - zoomHeight);
            }
        }

        return new RadPoint(x, y);
    }

    /**
     * Used to set the current zoom and to trigger the {@link #onZoomChanged()} method of the
     * current instance.
     *
     * @param newZoom the new zoom.
     */
    public void setZoom(RadSize newZoom) {
        this.setZoom(newZoom.getWidth(), newZoom.getHeight());
    }

    public void setZoom(double width, double height) {
        if (width < 1 || height < 1)
            throw new IllegalArgumentException("The values for both width and height must be greater than or equal to 1.");

        RadSize clampedZoom = clampZoom(width, height);

        if (clampedZoom.getWidth() == this.zoom.getWidth() && clampedZoom.getHeight() == this.zoom.getHeight()) {
            return;
        }

        this.zoom = clampedZoom;

        this.onZoomChanged();
        requestInvalidateArrange();
    }

    public RadSize clampZoom(double width, double height) {
        width = Math.min(this.maxZoom.getWidth(), width);
        height = Math.min(this.maxZoom.getHeight(), height);
        return new RadSize(width, height);
    }

    /**
     * Gets a collection of chart behaviors.&nbsp;For example a ChartToolTipBehavior can
     * be added to this collection which will enable tooltips on certain gestures.
     *
     * @return the collection of chart behaviours.
     */
    public ChartBehaviorCollection getBehaviors() {
        return this.behaviorsCache;
    }

    /**
     * Gets the data context that corresponds to the given physical point.
     *
     * @param physicalOrigin point to be used in determining the data context.
     * @return the corresponding data context.
     */
    public ChartDataContext getDataContext(Point physicalOrigin) {
        // The tool tip location has to be translated because the data points are laid out in a larger layout slot if there is a zoom factor.
        // Also, the absolute value of the pan offset is used in the transformation because the pan offset is applied as a negative value to
        // the visualization of the plot area in order to simulate pan behavior.

        if (this.lastDataContext != null &&
                this.lastDataContext.getTouchLocation().equals(physicalOrigin)) {

            return this.lastDataContext;
        }

        Pair<List<DataPointInfo>, DataPointInfo> closestPoints = this.findClosestPoints(physicalOrigin);

        ArrayList<DataPoint> dataPoints = new ArrayList<>(closestPoints.first.size());
        for (DataPointInfo info : closestPoints.first) {
            dataPoints.add(info.getDataPoint());
        }

        this.lastDataContext = new ChartDataContext(this, closestPoints.first, dataPoints, closestPoints.second);
        this.lastDataContext.setTouchLocation(physicalOrigin);

        return this.lastDataContext;
    }

    public ChartDataContext getDataContext(DataPoint point) {
        DataPointInfo info = new DataPointInfo();
        info.setDataPoint(point);
        return new ChartDataContext(this, null, null, info);
    }

    /**
     * Used to respond to a change in the collection of behaviours.
     *
     * @param info instance holding the type of action that occurred in the collection.
     */
    public void onBehaviorsCollectionChanging(BehaviorCollectionChangedInfo info) {
        switch (info.getAction()) {
            case ADD:
                this.onBehaviorAdded(info.getAddedBehavior());
                break;
            case REMOVE:
                this.onBehaviorRemoved(info.getRemovedBehavior());
                break;
            case REPLACE:
                this.onBehaviorReplaced(info.getAddedBehavior(), info.getRemovedBehavior());
                break;
            case RESET:
                this.onBehaviorsReset();
                break;
        }
    }

    public void resetBehaviors() {
        if(this.updateSuspended) {
            return;
        }

        for (ChartBehavior behavior : this.getBehaviors()) {
            behavior.reset();
        }
    }

    private  CountDownTimer createTimer(int milliseconds) {
        return new CountDownTimer(milliseconds, milliseconds) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                onTimerFinished();
            }
        };
    }

    @Override
    public boolean onDown(MotionEvent e) {
        boolean result = this.applyGesture(new Function2<ChartBehavior, MotionEvent, Boolean>() {
            @Override
            public Boolean apply(ChartBehavior argument1, MotionEvent argument2) {
                return argument1.onDown(argument2);
            }
        }, e);

        holdEvent = MotionEvent.obtain(e); // copy the event because it changes between down and hold for some inexplicable reason. Hail Android!

        timer = createTimer(milliseconds);
        timer.start();
        return result;
    }

    private void onTimerFinished() {
        if (this.moveOccurred || this.holdCancelled) {
            holdEvent = null;
            holdCancelled = false;
            return;
        }


        this.applyGesture(new Function2<ChartBehavior, MotionEvent, Boolean>() {
            @Override
            public Boolean apply(ChartBehavior argument1, MotionEvent argument2) {
                return argument1.onHold(holdEvent);
            }
        }, null);

        this.isInHold = true;
        holdEvent = null;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        boolean result = this.applyGesture(new Function2<ChartBehavior, MotionEvent, Boolean>() {
            @Override
            public Boolean apply(ChartBehavior argument1, MotionEvent argument2) {

                boolean result = argument1.onUp(argument2);

                if (!moveOccurred) {
                    result = result || argument1.onTap(argument2);
                }

                return result;
            }
        }, e);

        this.timer.cancel();
        moveOccurred = false;

        return result;
    }

    @Override
    public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
        this.moveOccurred = true;

        this.applyGesture(new Function2<ChartBehavior, MotionEvent, Boolean>() {
            @Override
            public Boolean apply(ChartBehavior argument1, MotionEvent argument2) {
                return argument1.onDrag(e1, e2, new RadSize(distanceX, distanceY), isInHold);
            }
        }, e1);

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        this.holdCancelled = true;
        return this.applyGesture(new Function2<ChartBehavior, MotionEvent, Boolean>() {
            @Override
            public Boolean apply(ChartBehavior argument1, MotionEvent argument2) {
                return argument1.onDoubleTap(argument2);
            }
        }, e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!this.isClickable()) {
            return super.onTouchEvent(event);
        }

        this.gestureDetector.onTouchEvent(event);
        this.scaleDetector.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            this.moveOccurred = false;
            if (this.isInHold) {
                this.onSingleTapUp(event);
                this.isInHold = false;
            }

            this.timer.cancel();
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onScale(final ChartScaleGestureDetector detector) {
        if (this.isInHold) {
            return false;
        }

        return this.applyGesture(new Function2<ChartBehavior, MotionEvent, Boolean>() {
            @Override
            public Boolean apply(ChartBehavior argument1, MotionEvent argument2) {
                return argument1.onPinch(detector, argument2);
            }
        }, null);
    }

    @Override
    public boolean onScaleBegin(ChartScaleGestureDetector detector) {
        return !this.isInHold;
    }

    @Override
    public void onScaleEnd(ChartScaleGestureDetector detector) {
        if (this.isInHold) {
            return;
        }

        this.applyGesture(new Function2<ChartBehavior, MotionEvent, Boolean>() {
            @Override
            public Boolean apply(ChartBehavior argument1, MotionEvent argument2) {
                argument1.onPinchComplete();
                return true;
            }
        }, null);
    }

    private boolean applyGesture(Function2<ChartBehavior, MotionEvent, Boolean> gesture, MotionEvent event) {
        boolean result = false;

        for (ChartBehavior behavior : this.behaviorsCache) {
            if (gesture.apply(behavior, event)) {
                result = true;
            }
        }

        return result;
    }

    public ChartPalette getPalette() {
        return this.palette;
    }

    public void setPalette(ChartPalette value) {
        if (this.palette == value) {
            return;
        }

        this.palette = value;
        this.presenterImpl.processPaletteChanged();
    }

    @Override
    public void refreshNode(ChartNode node) {
        this.presenterImpl.refreshNode(node);
    }

    @Override
    public RadSize measureContent(ChartNode owner, Object content) {
        return this.presenterImpl.measureContent(owner, content);
    }

    @Override
    public void invalidatePalette() {
        this.presenterImpl.invalidatePalette();
    }

    @Override
    public int getCollectionIndex() {
        return this.presenterImpl.getCollectionIndex();
    }
}
