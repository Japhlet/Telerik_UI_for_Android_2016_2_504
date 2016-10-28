package com.telerik.widget.chart.visualization.behaviors;

import android.view.MotionEvent;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadSize;
import com.telerik.android.primitives.widget.tooltip.contracts.DrawListener;
import com.telerik.widget.chart.visualization.behaviors.views.DeferredZoomPresenter;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Represents a chart behavior that handles pinch and drag gestures and manipulates the zoom and
 * pan properties of the associated {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView} instance.
 */
public class ChartPanAndZoomBehavior extends ChartBehavior {
    private int zoomMode = ChartPanZoomMode.BOTH;
    private int panMode = ChartPanZoomMode.BOTH;
    private ChartZoomStrategy zoomStrategy = ChartZoomStrategy.IMMEDIATE;
    private boolean handleDoubleTap = true;
    private DeferredZoomPresenter deferredZoomPresenter;
    private ArrayList<PanZoomListener> panZoomListeners = new ArrayList<>();

    private boolean isPinching;
    private boolean isDragging;

    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior} class.
     */
    public ChartPanAndZoomBehavior() {
        this.deferredZoomPresenter = new DeferredZoomPresenter();
        this.deferredZoomPresenter.setOwner(this);
    }


    public void addPanZoomListener(PanZoomListener listener) {
        panZoomListeners.add(listener);
    }

    public void removePanZoomListener(PanZoomListener listener) {
        panZoomListeners.remove(listener);
    }

    /**
     * Gets the current {@link com.telerik.widget.chart.visualization.behaviors.ChartZoomStrategy}.
     */
    public ChartZoomStrategy getZoomStrategy() {
        return this.zoomStrategy;
    }

    /**
     * Sets the {@link com.telerik.widget.chart.visualization.behaviors.ChartZoomStrategy}.
     */
    public void setZoomStrategy(ChartZoomStrategy value) {
        this.zoomStrategy = value;
    }

    /**
     * Gets whether a double-tap gesture will be handled by the behavior to reset the
     * current Zoom and PanOffset values.
     *
     * @return whether a double tab gesture is handled.
     */
    public boolean getHandleDoubleTap() {
        return this.handleDoubleTap;
    }

    /**
     * Sets whether a double-tap gesture will be handled by the behavior to reset the
     * current Zoom and PanOffset values.
     *
     * @param value the new handle state.
     */
    public void setHandleDoubleTap(boolean value) {
        this.handleDoubleTap = value;
    }

    /**
     * Gets the {@link ChartPanZoomMode} value that specifies how the chart will respond to
     * a zoom gesture.
     *
     * @return the current zoom mode.
     */
    public int getZoomMode() {
        return this.zoomMode;
    }

    /**
     * Sets the {@link ChartPanZoomMode} value that specifies how the chart will respond to
     * a zoom gesture.
     *
     * @param value the new zoom mode.
     */
    public void setZoomMode(int value) {
        this.zoomMode = value;
    }

    /**
     * Gets the {@link ChartPanZoomMode} value that specifies how the chart will respond
     * to a pan gesture.
     *
     * @return the current pan mode.
     */
    public int getPanMode() {
        return this.panMode;
    }

    /**
     * Sets the {@link ChartPanZoomMode} value that specifies how the chart will respond
     * to a pan gesture.
     *
     * @param value the new pan mode.
     */
    public void setPanMode(int value) {
        this.panMode = value;
    }

    /**
     * Gets value that indicates whether the behavior is processing a pinch gesture.
     */
    public boolean isPinching() {
        return this.isPinching;
    }

    /**
     * Gets the {@link com.telerik.widget.chart.visualization.behaviors.views.DeferredZoomPresenter}.
     */
    public DeferredZoomPresenter getDeferredZoomPresenter() {
        return this.deferredZoomPresenter;
    }

    /**
     * Sets the {@link com.telerik.widget.chart.visualization.behaviors.views.DeferredZoomPresenter}.
     */
    public void setDeferredZoomPresenter(DeferredZoomPresenter value) {
        if(value == null) {
            throw new IllegalArgumentException("value cannot be null.");
        }

        this.deferredZoomPresenter.setOwner(null);
        this.deferredZoomPresenter = value;
        this.deferredZoomPresenter.setOwner(this);
    }

    @Override
    public void onPinchComplete() {
        this.isPinching = false;
        this.isDragging = false;

        if(this.getZoomMode() == ChartPanZoomMode.NONE) {
            return;
        }

        if(this.zoomStrategy == ChartZoomStrategy.DEFERRED) {
            double zoomX = this.deferredZoomPresenter.getZoomX();
            double zoomY = this.deferredZoomPresenter.getZoomY();
            double panX = this.deferredZoomPresenter.getPanX();
            double panY = this.deferredZoomPresenter.getPanY();
            this.chart.setZoom(zoomX, zoomY);
            this.chart.setPanOffset(panX, panY);

            notifyListenersOnZoom(zoomX, zoomY);
            notifyListenersOnPan(panX, panY);
        }
    }

    protected void notifyListenersOnZoom(double zoomX, double zoomY) {
        ArrayList<PanZoomListener> listeners = copyPanZoomListeners();
        for(PanZoomListener listener : listeners) {
            listener.onZoom(zoomX, zoomY);
        }
    }

    protected void notifyListenersOnPan(double panX, double panY) {
        ArrayList<PanZoomListener> listeners = copyPanZoomListeners();
        for(PanZoomListener listener : listeners) {
            listener.onPan(panX, panY);
        }
    }

    private ArrayList<PanZoomListener> copyPanZoomListeners() {
        ArrayList<PanZoomListener> copy = new ArrayList<>();
        for(PanZoomListener listener : panZoomListeners) {
            copy.add(listener);
        }

        return copy;
    }

    @Override
    public boolean onDrag(MotionEvent event, MotionEvent event2, RadSize distance, boolean isInHold) {
        if (isInHold || this.isPinching) {
            return false;
        }

        if (this.panMode == ChartPanZoomMode.NONE) {
            return false;
        }

        if (!this.isDragging) {
            this.isDragging = true;
            return true;
        }

        double offsetX = -1 * distance.getWidth();
        double offsetY = -1 * distance.getHeight();

        setPanOffsetToChart(offsetX, offsetY);

        return offsetX != 0 || offsetY != 0;
    }

    @Override
    public void reset() {
        super.reset();

        if (this.chart == null) {
            return;
        }

        this.setZoomToChart(0, 0, 0);
        this.setPanOffsetToChart(0, 0);
    }

    public boolean isZoomed() {
        return this.chart.getZoomWidth() != 1 || this.chart.getZoomHeight() != 1;
    }

    public void setPanOffsetToChart(double offsetX, double offsetY) {
        if(!this.isZoomed()) {
            return;
        }

        double x = this.chart.getPanOffsetX();
        double y = this.chart.getPanOffsetY();
        if ((this.panMode & ChartPanZoomMode.HORIZONTAL) == ChartPanZoomMode.HORIZONTAL) {
            x += offsetX;
        }

        if ((this.panMode & ChartPanZoomMode.VERTICAL) == ChartPanZoomMode.VERTICAL) {
            y += offsetY;
        }

        this.chart.setPanOffset(x, y);

        notifyListenersOnPan(x, y);
    }

    public void setZoomToChart(double scale, double centerX, double centerY) {
        this.setZoomToChart(scale, scale, centerX, centerY);
    }

    public void setZoomToChart(double scaleX, double scaleY, double centerX, double centerY) {
        double newZoomX = this.chart.getZoom().getWidth();
        if ((this.zoomMode & ChartPanZoomMode.HORIZONTAL) == ChartPanZoomMode.HORIZONTAL ||
                this.zoomMode == ChartPanZoomMode.BOTH) {
            newZoomX = Math.max(newZoomX * scaleX, 1);
        }

        double newZoomY = this.chart.getZoom().getHeight();
        if ((this.zoomMode & ChartPanZoomMode.VERTICAL) == ChartPanZoomMode.VERTICAL ||
                this.zoomMode == ChartPanZoomMode.BOTH) {
            newZoomY = Math.max(newZoomY * scaleY, 1);
        }

        RadPoint oldPan = this.chart.getPanOffset();
        RadSize oldZoom = this.chart.getZoom();

        RadSize newZoom = this.chart.clampZoom(newZoomX, newZoomY);
        this.chart.setZoom(newZoomX, newZoomY);
        notifyListenersOnZoom(newZoomX, newZoomY);

        if(oldZoom.equals(newZoom)) {
            return;
        }

        // The rest of this method works flawlessly thanks to Rositsa Topchiyska.
        double newPanX = -newZoom.getWidth() * (centerX - oldPan.getX()) / oldZoom.getWidth() + centerX;
        double newPanY = -newZoom.getHeight() * (centerY - oldPan.getY()) / oldZoom.getHeight() + centerY;

        RadPoint newPan = new RadPoint(newPanX, newPanY);
        newPan = this.chart.clampTranslate(newPan, newZoom);
        this.chart.setPanOffset(newPan.getX(), newPan.getY());
        notifyListenersOnPan(newPanX, newPanY);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        if (!this.handleDoubleTap) {
            return false;
        }

        reset();

        return true;
    }

    @Override
    public boolean onPinch(ChartScaleGestureDetector detector, MotionEvent pinchEvent) {
        double scale = detector.getScaleFactor();
        double centerX = detector.getFocusX();
        double centerY = detector.getFocusY();
        if (this.zoomMode == ChartPanZoomMode.NONE) {
            return false;
        }

        if (!this.isPinching) {
            this.isPinching = true;
            return true;
        }

        if(this.zoomStrategy == ChartZoomStrategy.IMMEDIATE) {
            setZoomToChart(scale, centerX, centerY);
        } else {
            double halfSpanX = detector.getCurrentSpanX() / 2.0;
            double halfSpanY = detector.getCurrentSpanY() / 2.0;
            this.deferredZoomPresenter.setPinchPoints(new RadPoint(centerX - halfSpanX, centerY - halfSpanY),
                    new RadPoint(centerX + halfSpanX, centerY + halfSpanY));
            this.deferredZoomPresenter.applyPalette(this.chart.getPalette());
            this.chart.requestRender();
        }

        return true;
    }

    @Override
    protected DrawListener getDrawListener() {
        return this.deferredZoomPresenter;
    }
}
