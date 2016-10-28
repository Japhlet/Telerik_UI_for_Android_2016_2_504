package com.telerik.widget.chart.visualization.behaviors;

import android.graphics.Point;
import android.view.MotionEvent;

import com.telerik.android.common.PropertyManager;
import com.telerik.android.common.math.RadSize;
import com.telerik.android.primitives.widget.tooltip.contracts.DrawListener;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

/**
 * Base class for all chart behaviors.
 */
public abstract class ChartBehavior extends PropertyManager {
    /**
     * The chart that owns this behavior.
     */
    protected RadChartViewBase chart;

    /**
     * Gets the chart to which this behavior is attached.
     */
    public RadChartViewBase chart() {
        return this.chart;
    }

    /**
     * Invoked when a hold gesture occurs on the owner chart.
     *
     * @param e An object that contains gesture information.
     * @return Returns true if the gesture is handled.
     */
    public boolean onHold(MotionEvent e) {
        return false;
    }

    /**
     * Invoked when the user touches the chart.
     *
     * @param e An object that contains gesture information.
     * @return Returns true if the gesture is handled.
     */
    public boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * Invoked when the user lifts a finger from the chart.
     *
     * @param e An object that contains gesture information.
     * @return Returns true if the gesture is handled.
     */
    public boolean onUp(MotionEvent e) {
        return false;
    }

    /**
     * Invoked when a tap gesture occurs on the owner chart.
     *
     * @param e An object that contains gesture information.
     * @return Returns true if the gesture is handled.
     */
    public boolean onTap(MotionEvent e) {
        return false;
    }

    /**
     * Invoked when a drag gesture occurs on the owner chart.
     *
     * @param initialEvent Information for the event that started the gesture.
     * @param currentEvent Information for the event that caused the current drag gesture
     * @param distance     The distance travelled since the previous gesture. This is NOT the distance between initialEvent and currentEvent.
     * @return Returns true if the gesture is handled.
     */
    public boolean onDrag(MotionEvent initialEvent, MotionEvent currentEvent, RadSize distance, boolean isInHold) {
        return false;
    }

    /**
     * Invoked when a double tap gesture occurs on the owner chart.
     *
     * @param e An object that contains gesture information.
     * @return Returns true if the gesture is handled.
     */
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    /**
     * Invoked when a pinch gesture occurs on the owner chart.
     *
     * @param detector   The scale detector that detected this gesture.
     * @param pinchEvent The MotionEvent that represents this pinch gesture.
     * @return Returns true if the gesture is handled.
     */
    public boolean onPinch(ChartScaleGestureDetector detector, MotionEvent pinchEvent) {
        return false;
    }

    /**
     * Called when the pinch gesture finishes.
     */
    public void onPinchComplete() {
    }

    protected DrawListener getDrawListener() {
        return null;
    }

    /**
     * Used to attach the behaviour to e specified chart instance.
     *
     * @param chart the chart to be attached.
     */
    public void attach(RadChartViewBase chart) {
        if (chart == null) {
            throw new NullPointerException("chart");
        }

        this.chart = chart;
        this.chart.setClickable(true);
        this.chart.setFocusable(true);
        DrawListener listener = this.getDrawListener();
        if (listener != null) {
            this.chart.addOnDrawListener(listener);
        }
        this.onAttached();
    }

    /**
     * Used to detach the current chart instance.
     */
    public void detach() {
        this.onDetached();
        this.chart.setClickable(false);
        this.chart.setFocusable(false);
        DrawListener listener = this.getDrawListener();
        if (listener != null) {
            this.chart.removeOnDrawListener(listener);
        }
        this.chart = null;
    }

    /**
     * Resets the behavior. This occurs when the chart has major changes like new series or different axes.
     */
    public void reset() {
    }

    /**
     * Gets the {@link ChartDataContext} object associated with the specified physical lastLocation.
     *
     * @param location the specified physical lastLocation.
     * @return the associated context.
     * @see Point
     * @see ChartDataContext
     */
    protected ChartDataContext getContext(Point location) {
        if (this.chart == null || !this.chart.presenterImpl().isLoaded()) {
            return null;
        }

        return this.chart.getDataContext(location);
    }

    /**
     * Called when the behavior is added to the chart control.
     */
    protected void onAttached() {
    }

    /**
     * Called when the behavior is removed from the chart control.
     */
    protected void onDetached() {
    }
}
