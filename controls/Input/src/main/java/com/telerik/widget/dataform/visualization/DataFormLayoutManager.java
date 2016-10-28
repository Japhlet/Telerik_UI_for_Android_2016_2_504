package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

/**
 * Used by RadDataForm to arrange its editors on screen.
 */
public abstract class DataFormLayoutManager {
    protected Context context;
    private int layoutId;

    /**
     * Creates a new instance of DataFormLayoutManager with the specified XML layout id.
     */
    public DataFormLayoutManager(Context context, int layout) {
        this.context = context;
        this.layoutId = layout;
    }

    /**
     * Clears the data form layout.
     */
    public void unload() {
    }

    /**
     * Arranges the specified editors.
     */
    public ViewGroup arrangeEditors(Iterable<EntityPropertyViewer> editors) {
        ViewGroup rootLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);

        this.arrangeEditorsCore(editors, rootLayout);

        return rootLayout;
    }

    protected abstract void arrangeEditorsCore(Iterable<EntityPropertyViewer> editors, ViewGroup rootLayout);
}
