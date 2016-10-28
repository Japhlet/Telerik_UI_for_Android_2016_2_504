package com.telerik.android.primitives.widget.sidedrawer.contents;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.telerik.android.common.Util;
import com.telerik.android.primitives.R;

import java.util.List;

public class NavigationDrawerContent extends FrameLayout implements View.OnClickListener {
    private NavigationItemsAdapter adapter;
    private CreateIntentCallback createIntentCallback;
    private LinearLayout itemsLayout;

    DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            onDataChanged();
        }

        @Override
        public void onInvalidated() {
            onDataInvalidated();
        }
    };

    public NavigationDrawerContent(Context context) {
        this(context, null);
    }

    public NavigationDrawerContent(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.navigation_drawer_content, this);

        this.itemsLayout = Util.getLayoutPart(this, R.id.navItemsLayout, LinearLayout.class);
        this.adapter = new NavigationItemsAdapter(context);
        this.adapter.registerDataSetObserver(observer);
    }

    public void setCreateIntentCallback(CreateIntentCallback value) {
        createIntentCallback = value;
    }

    public CreateIntentCallback getCreateIntentCallback() {
        return createIntentCallback;
    }

    public void setNavigationItems(List<NavigationItem> value) {
        this.adapter.setItems(value);
    }

    public void setNavigationItemsAdapter(NavigationItemsAdapter value) {
        if(this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }

        this.adapter = value;

        if(this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
        }
    }

    protected void onDataChanged() {
        this.itemsLayout.removeAllViews();
        this.generateViews();
    }

    protected void onDataInvalidated() {
        this.itemsLayout.removeAllViews();
        this.generateViews();
    }

    protected void generateViews() {
        for(int i = 0; i < this.adapter.getItems().size(); ++i) {
            View navItemView = this.adapter.getView(i, null, this);
            navItemView.setOnClickListener(this);
            this.itemsLayout.addView(navItemView);
        }
    }

    @Override
    public void onClick(View v) {
        NavigationItem navItem = ((NavigationItemView)v).getNavigationItem();
        Context context = getContext();
        if(navItem.getIntent() != null) {
            context.startActivity(navItem.getIntent());
        } else {
            Intent intent = this.createIntent(navItem);
            context.startActivity(intent);
        }
    }

    private Intent createIntent(NavigationItem item) {
        if(createIntentCallback != null) {
            return createIntentCallback.createIntent(item);
        }

        return createIntentCore(item);
    }

    protected Intent createIntentCore(NavigationItem item) {
        return new Intent(this.getContext(), item.getActivityClass());
    }
}
