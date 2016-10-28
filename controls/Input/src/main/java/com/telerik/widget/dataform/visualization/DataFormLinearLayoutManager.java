package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

public class DataFormLinearLayoutManager extends DataFormLayoutManager {
    private int orientation = LinearLayout.VERTICAL;

    public DataFormLinearLayoutManager(Context context) {
        this(context, R.layout.data_form_linear_layout);
    }

    public DataFormLinearLayoutManager(Context context, int layout) {
        super(context, layout);
    }

    public void setOrientation(int value) {
        this.orientation = value;
    }

    public int getOrientation() {
        return this.orientation;
    }

    @Override
    protected void arrangeEditorsCore(Iterable<EntityPropertyViewer> editors, ViewGroup rootLayout) {
        ((LinearLayout)rootLayout).setOrientation(this.orientation);

        for(EntityPropertyViewer viewer : editors) {
            LinearLayout.LayoutParams params;
            if(orientation == LinearLayout.HORIZONTAL) {
                params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
            } else {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            rootLayout.addView(viewer.rootLayout(), params);
        }
    }
}
