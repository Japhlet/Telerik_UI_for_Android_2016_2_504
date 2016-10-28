package com.telerik.widget.list;

import android.view.View;
import android.view.ViewGroup;

public class CollapsedViewHolder extends ListViewHolder {
    public CollapsedViewHolder(View itemView) {
        super(itemView);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        if(layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }
        layoutParams.height = 0;
        itemView.setLayoutParams(layoutParams);
    }
}
