package com.telerik.widget.chart.engine.view;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.elementTree.ChartNode;

/**
 * Defines a type which may visualize a logical widget element.
 */
public interface ChartElementPresenter {

    /**
     * Invalidates the visual representation of the specified logical node.
     *
     * @param node the node to be refreshed.
     */
    void refreshNode(ChartNode node);

    /**
     * Retrieves the desired size of the specified logical node's content.
     *
     * @param owner   node that holds the content.
     * @param content content of the node.
     * @return the size of the content.
     */
    RadSize measureContent(ChartNode owner, Object content);

    /**
     * Forces re-evaluation of the palette of this instance.
     */
    void invalidatePalette();

    /**
     * Gets the index of this presenter in its parent {@link com.telerik.widget.chart.visualization.common.PresenterCollection}.
     *
     * @return The index of this presenter in its parent {@link com.telerik.widget.chart.visualization.common.PresenterCollection}.
     */
    int getCollectionIndex();
}

