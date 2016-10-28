package com.telerik.widget.list;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class CollapsibleGroupsBehavior extends ListViewBehavior {
    private ListViewWrapperAdapter adapter;
    private int collapseImageId;
    private int expandImageResource = -1;
    private int collapseImageResource = R.drawable.ic_collapse;
    private ArrayList<Object> collapsedGroups = new ArrayList<>();
    private List<CollapseGroupListener> listeners = new ArrayList<>();

    public CollapsibleGroupsBehavior() {
        this(R.id.groupHeaderCollapseImage);
    }

    public CollapsibleGroupsBehavior(int resourceId) {
        collapseImageId = resourceId;
    }

    public int getCollapseImageResource() {
        return collapseImageResource;
    }

    public void setCollapseImageResource(int collapseImageResource) {
        if(this.collapseImageResource == collapseImageResource) {
            return;
        }
        this.collapseImageResource = collapseImageResource;
        refreshCollapseImage();
    }

    public int getExpandImageResource() {
        return expandImageResource;
    }

    public void setExpandImageResource(int expandImageResource) {
        if(this.expandImageResource == expandImageResource) {
            return;
        }
        this.expandImageResource = expandImageResource;
        refreshCollapseImage();
    }

    void handleIsCollapsed(View view, int position) {
        ImageView collapseImage = (ImageView)view.findViewById(collapseImageId);
        if(collapseImage != null) {
            collapseImage.setVisibility(View.VISIBLE);
            if(expandImageResource != -1) {
                int newResource = isGroupCollapsed(position) ? expandImageResource : collapseImageResource;
                collapseImage.setImageResource(newResource);
                ViewCompat.setRotation(collapseImage, 0);
            } else {
                collapseImage.setImageResource(collapseImageResource);
                float newRotation = isGroupCollapsed(position) ? 180 : 0;
                ViewCompat.setRotation(collapseImage, newRotation);
            }
        }
    }

    private void showIsCollapsed(View view) {
        ImageView collapseImage = (ImageView)view.findViewById(collapseImageId);
        if(collapseImage != null) {
            collapseImage.setVisibility(View.VISIBLE);
        }
    }

    private void hideIsCollapsed(View view) {
        ImageView collapseImage = (ImageView)view.findViewById(collapseImageId);
        if(collapseImage != null) {
            collapseImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            if(collapsedGroups.size() > 0) {
                int[] positions = new int[collapsedGroups.size()];
                for (int i = 0; i < collapsedGroups.size(); i++) {
                    Object item = collapsedGroups.get(i);
                    int position = ((ListViewAdapter) owner().getAdapter()).getPosition(item);
                    positions[i] = position;
                }
                bundle.putIntArray("collapsedGroups", positions);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            int[] positions = bundle.getIntArray("collapsedGroups");
            if(positions != null) {
                for (int position : positions) {
                    changeIsGroupCollapsed(position);
                }
            }
        }
    }

    @Override
    void onAdapterChanged(ListViewWrapperAdapter adapter) {
        this.adapter.setCollapsibleBehavior(null);
        collapsedGroups.clear();
        super.onAdapterChanged(adapter);
        this.adapter = adapter;
        adapter.setCollapsibleBehavior(this);
    }

    @Override
    public void onAttached(RadListView listView) {
        super.onAttached(listView);
        if(listView.wrapperAdapter() != null) {
            adapter = listView.wrapperAdapter();
            adapter.setCollapsibleBehavior(this);
            if(owner().getLayoutManager() != null) {
                int viewsCount = owner().getLayoutManager().getChildCount();
                for (int i = 0; i < viewsCount; i++) {
                    View v = owner().getLayoutManager().getChildAt(i);
                    showIsCollapsed(v);
                }
            }
        }
    }

    @Override
    public void onDetached(RadListView listView) {
        this.adapter.setCollapsibleBehavior(null);
        collapsedGroups.clear();
        if(owner().getLayoutManager() != null) {
            int viewsCount = owner().getLayoutManager().getChildCount();
            for (int i = 0; i < viewsCount; i++) {
                View v = owner().getLayoutManager().getChildAt(i);
                hideIsCollapsed(v);
            }
        }
        this.adapter = null;
        super.onDetached(listView);
    }

    @Override
    public void onTapUp(MotionEvent motionEvent) {
        View pressedView = owner().findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int position = owner().getChildAdapterPosition(pressedView);
        changeIsGroupCollapsed(position);
    }

    public void expandAll() {
        if(!(owner().getAdapter() instanceof ListViewDataSourceAdapter)) {
            return;
        }
        ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)owner().getAdapter();
        int collapsedGroupsSize = collapsedGroups.size();
        for(int i = collapsedGroupsSize - 1; i >= 0; i--) {
            Object item = collapsedGroups.get(i);
            int position = dataSourceAdapter.getPosition(item);
            View view = viewForPosition(position);
            expandGroup(position, view);
        }
    }

    private void refreshCollapseImage() {
        if(!(owner().getAdapter() instanceof ListViewDataSourceAdapter)) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = owner().getLayoutManager();
        for(int i = 0; i < layoutManager.getChildCount(); i++) {
            View view = layoutManager.getChildAt(i);
            if(view == null) {
                continue;
            }
            ImageView collapseImage = (ImageView) view.findViewById(collapseImageId);
            if(collapseImage == null) {
                continue;
            }
            int position = owner().getChildAdapterPosition(view);
            if (!isGroupCollapsed(position)) {
                collapseImage.setImageResource(collapseImageResource);
            } else {
                int rotation = expandImageResource == -1 ? 180 : 0;
                int imageResource = expandImageResource == -1 ? collapseImageResource : expandImageResource;
                collapseImage.setImageResource(imageResource);
                ViewCompat.setRotation(collapseImage, rotation);
            }
        }
    }

    public void collapseAll() {
        if(!(owner().getAdapter() instanceof ListViewDataSourceAdapter)) {
            return;
        }
        ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)owner().getAdapter();
        for(int i = 0; i < dataSourceAdapter.getItemCount(); i++) {
            if(dataSourceAdapter.isGroupHeader(i)) {
                View view = viewForPosition(i);
                collapseGroup(i, view);
            }
        }
    }

    private void changeIsGroupCollapsed(int position) {
        if(!(owner().getAdapter() instanceof ListViewDataSourceAdapter)) {
            return;
        }
        ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)owner().getAdapter();
        if(dataSourceAdapter.isGroupHeader(position)) {
            View view = viewForPosition(position);
            if(isGroupCollapsed(position)) {
                expandGroup(position, view);
            } else {
                collapseGroup(position, view);
            }
        }
    }

    public void changeIsGroupCollapsed(Object groupHeader) {
        if(!(owner().getAdapter() instanceof ListViewDataSourceAdapter)) {
            return;
        }
        ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)owner().getAdapter();
        int position = dataSourceAdapter.getPosition(groupHeader);
        changeIsGroupCollapsed(position);
    }

    private void collapseGroup(int position, View view) {
        if(isGroupCollapsed(position)) {
            return;
        }
        if(view != null) {
            ImageView collapseImage = (ImageView) view.findViewById(collapseImageId);
            if (collapseImage != null) {
                if (expandImageResource != -1) {
                    collapseImage.setImageResource(expandImageResource);
                } else {
                    collapseImage.setImageResource(collapseImageResource);
                    ViewCompat.animate(collapseImage).rotation(180).start();
                }
            }
        }

        ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)owner().getAdapter();
        Object item = dataSourceAdapter.getItem(position);
        collapsedGroups.add(item);
        for(CollapseGroupListener listener : listeners) {
            listener.onGroupCollapsed(item);
        }
        dataSourceAdapter.notifyItemChanged(position);
        int updatePosition = position + 1;
        while (updatePosition < dataSourceAdapter.getItemCount() && !dataSourceAdapter.isGroupHeader(updatePosition)) {
            dataSourceAdapter.notifyItemChanged(updatePosition);
            updatePosition++;
        }
    }

    private void expandGroup(int position, View view) {
        if(!isGroupCollapsed(position)) {
            return;
        }
        if(view != null) {
            ImageView collapseImage = (ImageView) view.findViewById(collapseImageId);
            if (collapseImage != null) {
                collapseImage.setImageResource(collapseImageResource);
                if (expandImageResource == -1) {
                    ViewCompat.animate(collapseImage).rotation(0).start();
                } else {
                    ViewCompat.setRotation(collapseImage, 0);
                }
            }
        }

        ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)owner().getAdapter();
        Object item = dataSourceAdapter.getItem(position);
        collapsedGroups.remove(item);
        for(CollapseGroupListener listener : listeners) {
            listener.onGroupExpanded(item);
        }
        dataSourceAdapter.notifyItemChanged(position);
        int updatePosition = position + 1;
        while (updatePosition < dataSourceAdapter.getItemCount() && !dataSourceAdapter.isGroupHeader(updatePosition)) {
            dataSourceAdapter.notifyItemChanged(updatePosition);
            updatePosition++;
        }
    }

    private View viewForPosition(int position) {
        int itemsInLayout = owner().getLayoutManager().getChildCount();
        for(int i = 0; i < itemsInLayout; i++) {
            View view = owner().getLayoutManager().getChildAt(i);
            if (owner().getLayoutManager().getPosition(view) == position) {
                return view;
            }
        }
        return null;
    }

    public boolean isGroupCollapsed(Object group) {
        return collapsedGroups.contains(group);
    }

    boolean isGroupCollapsed(int index) {
        if(!(owner().getAdapter() instanceof ListViewDataSourceAdapter)) {
            return false;
        }
        ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)owner().getAdapter();
        Object item = dataSourceAdapter.getItem(index);
        return collapsedGroups.contains(item);
    }

    public void addListener(CollapseGroupListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(CollapseGroupListener listener) {
        this.listeners.remove(listener);
    }

    public interface CollapseGroupListener {

        void onGroupCollapsed(Object group);

        void onGroupExpanded(Object group);
    }
}
