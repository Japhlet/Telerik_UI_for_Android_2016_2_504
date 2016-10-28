package com.telerik.android.primitives.widget.sidedrawer.contents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.telerik.android.common.Util;
import com.telerik.android.primitives.R;

public class NavigationItemView extends FrameLayout {
    private NavigationItem navigationItem;
    private TextView textView;

    public NavigationItemView(Context context) {
        this(context, null);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.navigation_item_view, this);

        textView = Util.getLayoutPart(this, R.id.navigationItemText, TextView.class);
    }

    public NavigationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationItem getNavigationItem() {
        return this.navigationItem;
    }

    public void setNavigationItem(NavigationItem value) {
        navigationItem = value;

        if(value != null) {
            textView.setText(value.getText());
        }
    }
}
