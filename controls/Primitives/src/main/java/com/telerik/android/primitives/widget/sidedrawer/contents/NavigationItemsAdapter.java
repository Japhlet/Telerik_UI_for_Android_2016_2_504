package com.telerik.android.primitives.widget.sidedrawer.contents;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class NavigationItemsAdapter extends BaseAdapter {
    private List<NavigationItem> navigationItems;
    private Context context;

    public NavigationItemsAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<NavigationItem> value) {
        this.navigationItems = value;
        this.notifyDataSetInvalidated();
    }

    public List<NavigationItem> getItems() {
        return navigationItems;
    }

    @Override
    public int getCount() {
        return navigationItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navigationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return navigationItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavigationItemView result = this.createNavigationItemView();
        result.setNavigationItem(navigationItems.get(position));
        return result;
    }

    protected NavigationItemView createNavigationItemView() {
        return new NavigationItemView(this.context);
    }
}
