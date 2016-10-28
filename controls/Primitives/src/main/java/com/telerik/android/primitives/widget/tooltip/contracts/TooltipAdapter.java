package com.telerik.android.primitives.widget.tooltip.contracts;

import android.graphics.Point;
import android.graphics.Rect;

import com.telerik.android.common.math.RadRect;

/**
 * Tooltip adapter providing the needed information for managing a tooltip instance.
 */
public interface TooltipAdapter {
    /**
     * The offset from the touch event according to the current target view instance and the world coordinates of the same touch event.
     * @return the current offset.
     */
    Point rawOffset();

    /**
     * The currently available layout slot in which the tooltip should fit.
     *
     * @return the currently available layout slot.
     */
    Rect availableLayoutSlot();

    /**
     * States whether the tooltip should be placed vertically or horizontally to the currently selected target.
     *
     * @return <code>true</code> to place it above or bellow with above being the first choice, <code>false</code> to place it left or right with right being the first choice.
     */
    boolean alignTooltipVertically();

    /**
     * Gets the data for the tooltip using the passed context.
     *
     * @param context context used to extract the data for the current tooltip.
     * @return the extracted data.
     */
    Object[] getTooltipData(Object context);

    /**
     * Gets the clip of the current plot area.
     *
     * @return the clip of the current plot area.
     */
    RadRect getPlotAreaClip();

    /**
     * Provides a content adapter for the current tooltip instance.
     *
     * @return content adapter.
     * @see TooltipContentAdapter
     */
    TooltipContentAdapter contentAdapter();

    void setContentAdapter(TooltipContentAdapter adapter);
}
