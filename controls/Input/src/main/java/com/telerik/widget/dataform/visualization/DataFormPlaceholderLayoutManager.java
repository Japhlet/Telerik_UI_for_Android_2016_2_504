package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.view.ViewGroup;

import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

public class DataFormPlaceholderLayoutManager extends DataFormLayoutManager {
    /**
     * Creates a new instance of DataFormLayoutManager with the specified XML layout id.
     */
    public DataFormPlaceholderLayoutManager(Context context, int layout) {
        super(context, layout);
    }

    @Override
    protected void arrangeEditorsCore(Iterable<EntityPropertyViewer> editors, ViewGroup rootLayout) {
        for(EntityPropertyViewer viewer : editors) {
            ViewGroup placeHolder = findPlaceHolder(viewer.property().name(), rootLayout);

            if(placeHolder == null) {
                // The user may not specify a placeholder for every editor.
                continue;
            }
            placeHolder.addView(viewer.rootLayout());
        }
    }

    protected ViewGroup findPlaceHolder(Object tag, ViewGroup rootLayout) {
        return (ViewGroup)rootLayout.findViewWithTag(tag);
    }
}
