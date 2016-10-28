package com.telerik.widget.chart.visualization.behaviors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.android.primitives.widget.tooltip.RadTooltipView;
import com.telerik.android.primitives.widget.tooltip.containers.PointerLayout;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipContentAdapter;
import com.telerik.android.primitives.widget.tooltip.views.TooltipPresenterBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;
import com.telerik.widget.chart.visualization.pieChart.PieSeries;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Tooltip behavior for chart instances.
 */
public class ChartTooltipBehavior extends ChartPopupBehavior {

    private static final String PALETTE_FAMILY_NAME = "Tooltip";

    public static final int BACKGROUND_PROPERTY_KEY = registerProperty(Color.WHITE, null);

    public static final int PADDING_PROPERTY_KEY = registerProperty(0, null);

    public static final int CATEGORY_COLOR_PROPERTY_KEY = registerProperty(Color.RED, null);

    public static final int VALUE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, null);

    public static final int CATEGORY_SIZE_PROPERTY_KEY = registerProperty(15.0f, null);

    public static final int VALUE_SIZE_PROPERTY_KEY = registerProperty(10.0f, null);

    /**
     * The gesture that will trigger the tooltip.
     */
    protected TooltipTriggerMode triggerMode = TooltipTriggerMode.TAP;

    /**
     * Creates a new instance of the {@link ChartTooltipBehavior} class.
     *
     * @param context the current context.
     */
    public ChartTooltipBehavior(Context context) {
        super(context);
    }

    /**
     * Gets the current mode that triggers the tooltip.
     *
     * @return the current trigger mode.
     */
    @SuppressWarnings("unused")
    public TooltipTriggerMode getTriggerMode() {
        return this.triggerMode;
    }

    /**
     * Sets the current mode that triggers the tooltip.
     *
     * @param triggerMode the new trigger mode.
     */
    public void setTriggerMode(TooltipTriggerMode triggerMode) {
        if (triggerMode == null)
            throw new NullPointerException("triggerMode can not be null");

        this.triggerMode = triggerMode;
    }

    @Override
    public boolean onDrag(MotionEvent event, MotionEvent event2, RadSize distance, boolean isInHold) {
        close();

        return super.onDrag(event, event2, distance, isInHold);
    }

    @Override
    public boolean onTap(MotionEvent e) {
        if (this.triggerMode == TooltipTriggerMode.HOLD && !this.popupPresenter.isOpen() || this.triggerMode == TooltipTriggerMode.NONE) {
            return false;
        }

        return open(new Point(Math.round(e.getX()), Math.round(e.getY())), new Point(Math.round(e.getRawX()), Math.round(e.getRawY())));
    }

    @Override
    public boolean onPinch(ChartScaleGestureDetector detector, MotionEvent pinchEvent) {
        close();

        return super.onPinch(detector, pinchEvent);
    }

    @Override
    public boolean onHold(MotionEvent e) {
        if (this.triggerMode != TooltipTriggerMode.HOLD) {
            close();

            return false;
        }

        return open(new Point(Math.round(e.getX()), Math.round(e.getY())), new Point(Math.round(e.getRawX()), Math.round(e.getRawY())));
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        close();

        return false;
    }

    @Override
    public TooltipContentAdapter contentAdapter() {
        if (this.tooltipContentAdapter == null)
            this.tooltipContentAdapter = new ChartTooltipContentAdapter(this.context);

        return this.tooltipContentAdapter;
    }

    @Override
    public void setContentAdapter(TooltipContentAdapter adapter) {
        this.tooltipContentAdapter = adapter;
    }

    @Override
    public void reset() {
        this.close();
    }

    @Override
    protected TooltipPresenterBase createPresenter(Context context) {
        return new RadTooltipView(context, this);
    }

    @Override
    protected String getPaletteFamilyName() {
        return PALETTE_FAMILY_NAME;
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

    @Override
    protected void applyPalette(ChartPalette palette) {
        if (palette == null)
            return;

        PaletteEntry entry = palette.getEntry(getPaletteFamilyName(), 0);

        if (entry == null || !(this.popupPresenter instanceof RadTooltipView))
            return;

        PointerLayout pointer = ((RadTooltipView) this.popupPresenter).pointerLayout();

        String pointerColor = entry.getCustomValue("PointerFill");

        if(pointerColor != null) {
            pointer.setPointerColor(Color.parseColor(pointerColor));
        }

        String pointerSize = entry.getCustomValue("PointerSize");
        if(pointerSize != null) {
            pointer.setPointerSize((int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(pointerSize)));
        }

        String pointerMargin = entry.getCustomValue("PointerMargin");
        if(pointerMargin != null) {
            pointer.setTooltipMargin((int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(pointerMargin)));
        }

        if (this.contentAdapter() instanceof ChartTooltipContentAdapter) {
            ChartTooltipContentAdapter adapter = (ChartTooltipContentAdapter) this.contentAdapter();

            String contentFill = entry.getCustomValue("ContentFill");
            if(contentFill != null) {
                this.setValue(BACKGROUND_PROPERTY_KEY, PALETTE_VALUE, Color.parseColor(contentFill));
                adapter.setBackgroundColor(this.getBackgroundColor());
            }

            String paddingString = entry.getCustomValue("Padding");
            if(paddingString != null) {
                int padding = Integer.parseInt(paddingString);
                this.setValue(PADDING_PROPERTY_KEY, PALETTE_VALUE, padding);
                padding = this.getPadding();
                adapter.setPadding(padding, padding, padding, padding);
            }

            String categoryColor = entry.getCustomValue("CategoryTextColor");
            if(categoryColor != null) {
                this.setValue(CATEGORY_COLOR_PROPERTY_KEY, PALETTE_VALUE, Color.parseColor(categoryColor));
                adapter.setCategoryTextColor(this.getCategoryTextColor());
            }

            String valueColor = entry.getCustomValue("ValueTextColor");
            if(valueColor != null) {
                this.setValue(VALUE_COLOR_PROPERTY_KEY, PALETTE_VALUE, Color.parseColor(valueColor));
                adapter.setValueTextColor(this.getValueTextColor());
            }

            String categorySize = entry.getCustomValue("CategoryTextSize");
            if(categorySize != null) {
                this.setValue(CATEGORY_SIZE_PROPERTY_KEY, PALETTE_VALUE, Float.parseFloat(categorySize));
                adapter.setCategoryTextSize(this.getCategoryTextSize());
            }

            String valueSize = entry.getCustomValue("ValueTextSize");
            if(valueSize != null) {
                this.setValue(VALUE_SIZE_PROPERTY_KEY, PALETTE_VALUE, Float.parseFloat(valueSize));
                adapter.setValueTextSize(this.getValueTextSize());
            }
        }
    }

    @Override
    protected Point desiredPopupLocation(DataPoint selectedDataPoint) {
        int x;
        int y;

        RadRect slot;
        if (selectedDataPoint instanceof PieDataPoint) {
            slot = this.getPiePointLocation((PieDataPoint) selectedDataPoint);
        } else {
            slot = selectedDataPoint.getLayoutSlot();
        }

        if (this.alignTooltipVertically()) {
            x = (int) (slot.getX() + (slot.getWidth() / 2));
            y = (int) slot.getY();
        } else {
            x = (int) slot.getRight();
            y = (int) (slot.getY() + (slot.getHeight() / 2));
        }

        return new Point(x, y);
    }

    private RadRect getPiePointLocation(PieDataPoint selectedDataPoint) {
        PieSeries series = (PieSeries) selectedDataPoint.getParent().getPresenter();
        RadPoint location = series.getPointLocation(selectedDataPoint);
        if (location == null) {
            throw new IllegalStateException("The pie chart segments have not been created.");
        }

        return new RadRect(location.getX(), location.getY(), 0, 0);
    }

    @Override
    protected void onOpenFailed() {
        close();
    }
}
