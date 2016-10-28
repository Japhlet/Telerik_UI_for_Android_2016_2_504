package com.telerik.android.primitives.widget.tooltip.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.primitives.R;
import com.telerik.android.primitives.widget.tooltip.contracts.DrawListener;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipAdapter;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipContentAdapter;

/**
 * Base class for all tooltip presenters providing the core logic for displaying and managing a tooltip instance.
 */
public abstract class TooltipPresenterBase extends View implements DrawListener {

    private boolean isOpen;

    /**
     * The ViewGroup that holds the tooltip.
     */
    protected ViewGroup tooltipContentContainer;

    /**
     * The ViewGroup that holds the content of the tooltip.
     */
    protected ViewGroup targetContentContainer;

    /**
     * The {@link PopupWindow} that holds the tooltip container.
     */
    protected PopupWindow popupWindow;

    /**
     * The adapter providing the information needed for managing the tooltip.
     */
    protected TooltipAdapter tooltipAdapter;

    /**
     * The currently selected target from which information will be extracted regarding position and content info.
     */
    protected Point targetPoint;

    /**
     * The calculated bounds of the tooltip for the currently selected target.
     */
    protected RadRect tooltipBounds;

    /**
     * Creates a new instance of the {@link TooltipPresenterBase} class.
     *
     * @param context        the context for this tooltip instance.
     * @param tooltipAdapter the adapter to be used when managing the tooltip.
     * @param tooltipLayout  the layout resource to be used.
     */
    public TooltipPresenterBase(Context context, TooltipAdapter tooltipAdapter, int tooltipLayout) {
        super(context);

        this.tooltipAdapter = tooltipAdapter;

        this.tooltipContentContainer = Util.createViewFromXML(tooltipLayout, ViewGroup.class, context);
        this.targetContentContainer = Util.getLayoutPart(this.tooltipContentContainer, R.id.chart_data_point_content_container, ViewGroup.class);

        this.tooltipContentContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        this.popupWindow = new PopupWindow(this.tooltipContentContainer, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.popupWindow.setClippingEnabled(false);
        this.popupWindow.setTouchable(false);
        this.popupWindow.setAnimationStyle(0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LicensingProvider.verify(this.getContext());
    }

    /**
     * Gets the current content adapter.
     *
     * @return the current content adapter.
     * @see TooltipContentAdapter
     */
    public TooltipContentAdapter getContentAdapter() {
        return this.tooltipAdapter.contentAdapter();
    }

    /**
     * Sets the current content adapter.
     *
     * @param contentAdapter the new content adapter.
     * @see TooltipContentAdapter
     */
    public void setContentAdapter(TooltipContentAdapter contentAdapter) {
        if (contentAdapter == null) {
            throw new NullPointerException("contentAdapter");
        }

        this.tooltipAdapter.setContentAdapter(contentAdapter);
    }

    /**
     * Gets the current touchable state of the popup window.
     *
     * @return the current touchable state.
     */
    public boolean getIsTouchable() {
        return this.popupWindow.isTouchable();
    }

    /**
     * Sets the touchable state of the popup window.
     *
     * @param touchable <code>true</code> will make to popup touchable, <code>false</code> will make it untouchable.
     */
    public void setTouchable(boolean touchable) {
        this.popupWindow.setTouchable(touchable);
    }

    /**
     * Gets the current tooltip animation style.
     *
     * @return the current animation style resource id.
     */
    public int getTooltipAnimationStyle() {
        return this.popupWindow.getAnimationStyle();
    }

    /**
     * Sets the current tooltip animation style.
     *
     * @param style the new animation style resource id.
     */
    public void setTooltipAnimationStyle(int style) {
        this.popupWindow.setAnimationStyle(style);
    }

    /**
     * Gets a value stating whether the tooltip is currently opened.
     *
     * @return <code>true</code> if the tooltip is currently oppened, <code>false</code> otherwise.
     */
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * Closes the tooltip.
     *
     * @return <code>true</code> if the tooltip was opened and needed closing, <code>false</code> otherwise.
     * &nbsp;In both cases the tooltip is closed after invoking this method.
     */
    public boolean close() {
        if (!this.isOpen)
            return false;

        this.isOpen = false;
        this.popupWindow.dismiss();
        this.targetContentContainer.removeAllViews();

        return true;
    }

    /**
     * Used to update the content of the current tooltip instance using the provided content adapter.
     *
     * @param context context from which the content for the currently selected target will be extracted.
     * @return <code>true</code> if the tooltip content was successfully updated and it is ready to be displayed, <code>false</code> if
     * content update failed and it should not be displayed.
     */
    public boolean updateTooltipContent(Object context) {
        Object[] data = this.tooltipAdapter.getTooltipData(context);

        if (data == null)
            return false;

        View pointTemplate = this.getContentAdapter().getView(data);

        if (pointTemplate == null)
            return false;

        this.targetContentContainer.removeAllViews();
        this.targetContentContainer.addView(pointTemplate);

        return true;
    }

    @Override
    public void notifyDraw(Canvas canvas) {
        this.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.isOpen)
            this.onDrawCore(canvas);
        else
            this.close();
    }

    /**
     * Applies logic specific to the current implementation instance that should occur when onDraw being is invoked.
     *
     * @param canvas the canvas on which any additional drawing logic will be applied.
     */
    protected void onDrawCore(Canvas canvas) {
    }

    /**
     * States whether or not the location point of the selected target can be overlapped by the tooltip.
     *
     * @return <code>true</code> if overlapping should be avoided, <code>false</code> otherwise.
     */
    protected boolean shouldPreventPointOverlap() {
        return true;
    }

    /**
     * Adjusts the position of the tooltip bounds so that it doesn't go outside the available plot area and makes sure that the
     * target location point is not being overlapped if not advised by the {@link #shouldPreventPointOverlap()} logic.
     *
     * @param tooltipBounds  the calculated bounds of the current tooltip.
     * @param targetLocation the location of the currently selected target.
     */
    protected RadRect adjustPosition(RadRect tooltipBounds, Point targetLocation) {
        Rect layoutLocal = this.tooltipAdapter.availableLayoutSlot();

        boolean wasMovedUp = false;
        boolean wasMovedRight = false;

        // PREVENTING TOOLTIP FROM GOING OUTSIDE THE AVAILABLE PLOTTING AREA

        int padding = 0;
        double x = tooltipBounds.getX();
        double y = tooltipBounds.getY();

        // Move Right
        if (x + padding < 0) {
            x = -padding;
            wasMovedRight = true;
        }

        // Move Left
        if (tooltipBounds.getRight() - padding > layoutLocal.width())
            x = layoutLocal.width() + padding - tooltipBounds.getWidth();

        // Move Down
        if (y + padding < 0)
            y = -padding;

        // Move Up
        if (tooltipBounds.getBottom() - padding > layoutLocal.height()) {
            y = layoutLocal.height() + padding - tooltipBounds.getHeight();
            wasMovedUp = true;
        }


        // PREVENTING DATA POINT OVERLAPPING

        if (!this.shouldPreventPointOverlap()) {
            return null;
        }

        if (this.tooltipAdapter.alignTooltipVertically()) {
            // Moving down
            if (!wasMovedUp && isPointInRectangle(targetLocation, tooltipBounds))
                y = targetLocation.y;
        } else {
            // Move left
            if (!wasMovedRight && isPointInRectangle(targetLocation, tooltipBounds))
                x = targetLocation.x - (tooltipBounds.getWidth());
        }

        return new RadRect(x, y, tooltipBounds.getWidth(), tooltipBounds.getHeight());
    }

    /**
     * Calculates the bounds of the tooltip before visualizing it so that its position can be adjusted
     * according to the {@link #adjustPosition(RadRect, Point)} logic.
     *
     * @param location the location point of the currently selected target.
     * @return the calculated bounds of the tooltip according to the current tooltip content.
     */
    protected RadRect calculateTooltipBounds(Point location) {
        int left;
        int top;

        if (this.tooltipAdapter.alignTooltipVertically()) {
            left = location.x - (this.tooltipContentContainer.getMeasuredWidth() / 2);
            top = location.y - (this.tooltipContentContainer.getMeasuredHeight());
        } else {
            left = location.x;
            top = location.y - (this.tooltipContentContainer.getMeasuredHeight() / 2);
        }

        return new RadRect(left, top, this.tooltipContentContainer.getMeasuredWidth(), this.tooltipContentContainer.getMeasuredHeight());
    }

    /**
     * Opens the tooltip for given target location after measuring and adjusting the tooltip size and position
     * according to the content of the target and the constraints of the available plot area.
     *
     * @param desiredPopupLocation the location of the selected target.
     */
    public void open(Point desiredPopupLocation) {
        if (desiredPopupLocation == null) {
            throw new NullPointerException("desiredPopupLocation");
        }

        this.isOpen = true;
        this.targetPoint = desiredPopupLocation;

        this.openCore(desiredPopupLocation);

        this.invalidate();
    }

    /**
     * Opens the popup for a given location.
     *
     * @param dataPointLocation location.
     */
    protected void openCore(Point dataPointLocation) {
        int measureSpec = View.MeasureSpec.UNSPECIFIED;
        this.tooltipContentContainer.measure(measureSpec, measureSpec);

        tooltipBounds = this.calculateTooltipBounds(dataPointLocation);
        RadRect adjustedPosition = this.adjustPosition(tooltipBounds, dataPointLocation);
        if(adjustedPosition != null) {
            tooltipBounds = adjustedPosition;
        }

        if (!this.popupWindow.isShowing()) {
            this.popupWindow.showAtLocation(this.tooltipContentContainer, Gravity.NO_GRAVITY, 0, 0);
        }

        Point rawOffset = this.tooltipAdapter.rawOffset();

        this.popupWindow.update((int) tooltipBounds.getX() + rawOffset.x, (int) tooltipBounds.getY() + rawOffset.y, tooltipContentContainer.getMeasuredWidth(), tooltipContentContainer.getMeasuredHeight(), false);
        this.tooltipContentContainer.invalidate();
    }

    private boolean isPointInRectangle(Point point, RadRect rectangle) {
        return point.x > rectangle.getX() && point.x < rectangle.getRight() &&
                point.y > rectangle.getY() && point.y < rectangle.getBottom();
    }
}