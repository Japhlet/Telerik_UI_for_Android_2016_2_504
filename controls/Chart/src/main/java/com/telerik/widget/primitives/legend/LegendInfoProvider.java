package com.telerik.widget.primitives.legend;

import com.telerik.android.common.ObservableCollection;

/**
 * Should be implemented by views that want to use {@link RadLegendView} to display a legend.
 */
public interface LegendInfoProvider {

    /**
     * Gets the legend infos that contain legend information for a give view.
     */
    public ObservableCollection<LegendItem> getLegendInfos();
}
