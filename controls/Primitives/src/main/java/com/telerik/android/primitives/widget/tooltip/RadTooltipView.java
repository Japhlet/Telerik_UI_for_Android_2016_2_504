package com.telerik.android.primitives.widget.tooltip;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

import com.telerik.android.common.Util;
import com.telerik.android.primitives.R;
import com.telerik.android.primitives.widget.tooltip.containers.PointerLayout;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipAdapter;
import com.telerik.android.primitives.widget.tooltip.views.TooltipPresenterBase;

/**
 * Tooltip view that displays information about a given {@link View} target.
 */
public class RadTooltipView extends TooltipPresenterBase {

    private PointerLayout pointerLayout;

    /**
     * Creates a new instance of the {@link RadTooltipView} class using a default tooltip container layout.
     *
     * @param context the context to be used for the current tooltip instance.
     * @param tooltipAdapter the adapter to be used when managing the tooltip.
     */
    public RadTooltipView(Context context, TooltipAdapter tooltipAdapter) {
        this(context, tooltipAdapter, R.layout.tooltip_container);
    }

    /**
     * Creates a new instance of the {@link RadTooltipView} class.
     *
     * @param context the context to be used for the current tooltip instance.
     * @param tooltipAdapter the adapter to be used when managing the tooltip.
     * @param tooltipLayout the layout resource id to be used as the tooltip's container.
     */
    public RadTooltipView(Context context, TooltipAdapter tooltipAdapter, int tooltipLayout) {
        super(context, tooltipAdapter, tooltipLayout);

        this.pointerLayout = Util.getLayoutPart(this.tooltipContentContainer, R.id.chart_tooltip_pointer, PointerLayout.class);
    }

    /**
     * Gets the current pointer layout.
     *
     * @return the current pointer layout.
     */
    public PointerLayout pointerLayout() {
        return this.pointerLayout;
    }

    /**
     * Gets the current pointer size.
     *
     * @return the current pointer size.
     */
    public int getPointerSize() {
        return this.pointerLayout.getPointerSize();
    }

    /**
     * Sets the current pointer size.
     *
     * @param size the new pointer size.
     */
    public void setPointerSize(int size) {
        this.pointerLayout.setPointerSize(size);
    }

    @Override
    public void open(Point desiredPopupLocation) {
        super.open(desiredPopupLocation);

        this.pointerLayout.updateContainerLocation(new Point((int) this.tooltipBounds.getX(), (int) this.tooltipBounds.getY()));
        this.pointerLayout.updateTargetLocation(this.targetPoint);
        this.pointerLayout.setAlignPointerVertically(this.tooltipAdapter.alignTooltipVertically());
    }
}
