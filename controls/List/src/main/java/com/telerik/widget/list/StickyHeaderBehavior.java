package com.telerik.widget.list;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Represents a {@link com.telerik.widget.list.ListViewBehavior} that can be used to 'stick' the
 * last seen item header to the top of the list so that it remains visible after scrolls.
 */
public class StickyHeaderBehavior extends ListViewBehavior {

    protected Drawable stickyHeaderImage;

    private Drawable stickyHeaderImageCache;
    private int stickyHeaderPositionCache = INVALID_POSITION;
    private boolean isHorizontal = false;
    ListViewWrapperAdapter adapter;

    static final int INVALID_POSITION = -1;
    RecyclerView.AdapterDataObserver dataObserver;
    boolean isDataObserverRegistered = false;

    /**
     * Creates a new instance of the {@link com.telerik.widget.list.StickyHeaderBehavior} class.
     */
    public StickyHeaderBehavior() {
        dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                invalidate();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                invalidate();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                invalidate();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                invalidate();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                invalidate();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                invalidate();
            }
        };
    }

    public void invalidate() {
        stickyHeaderPositionCache = INVALID_POSITION;
        stickyHeaderImage = null;
        onScrolled(0, 0);
    }

    @Override
    public void onAttached(RadListView listView) {
        super.onAttached(listView);
        if(listView.getAdapter() == null) {
            return;
        }

        this.adapter = listView.wrapperAdapter();
        this.adapter.registerAdapterDataObserver(dataObserver);
        listView.setAdapter(listView.getAdapter());
        isDataObserverRegistered = true;
    }

    @Override
    public void onDetached(RadListView listView) {
        if(adapter != null && isDataObserverRegistered) {
            adapter.unregisterAdapterDataObserver(dataObserver);
            isDataObserverRegistered = false;
        }
        this.adapter = null;
        super.onDetached(listView);
    }

    @Override
    void onAdapterChanged(ListViewWrapperAdapter adapter) {
        if(this.adapter != null && isDataObserverRegistered) {
            this.adapter.unregisterAdapterDataObserver(dataObserver);
            this.isDataObserverRegistered = false;
        }
        super.onAdapterChanged(adapter);
        this.adapter = adapter;
        if(this.adapter != null) {
            this.adapter.registerAdapterDataObserver(dataObserver);
            this.isDataObserverRegistered = true;
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        isHorizontal = dx != 0;
        RecyclerView.LayoutManager layoutManager = owner().getLayoutManager();

        if(!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }

        int headerDeterminerPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        int headerChangerCandidate = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        if(headerDeterminerPosition < 0) {
            return;
        }
        if(headerChangerCandidate < 0) {
            headerChangerCandidate = headerDeterminerPosition;
        }

        if(dx < 0 || dy < 0) {
            int change = headerChangerCandidate;
            headerChangerCandidate = headerDeterminerPosition;
            headerDeterminerPosition = change;
        }

        int stickyHeaderPosition = getItemHeaderPosition(headerDeterminerPosition);
        int stickyHeaderCandidatePosition = getItemHeaderPosition(headerChangerCandidate);

        this.stickyHeaderImage = getStickyImageForPosition(stickyHeaderPosition);
        if(dx < 0 || dy < 0) {
            this.stickyHeaderImage = getStickyImageForPosition(stickyHeaderCandidatePosition);
        }

        if(headerChangerCandidate != 0) {
            if(stickyHeaderPosition != stickyHeaderCandidatePosition) {
                View headerChangerView = getViewForPosition(headerChangerCandidate);

                if(dx < 0 || dy < 0) {
                    placeImageOnBottom(this.stickyHeaderImage, headerChangerView);
                } else {
                    placeImageOnTop(this.stickyHeaderImage, headerChangerView);
                }
            } else {
                Rect bounds = new Rect(0, 0, this.stickyHeaderImage.getBounds().width(), this.stickyHeaderImage.getBounds().height());
                this.stickyHeaderImage.setBounds(bounds);
            }
        }
    }

    @Override
    public void onDispatchDraw(Canvas canvas) {
        if(stickyHeaderImage != null) {
            stickyHeaderImage.draw(canvas);
        }
    }

    protected Drawable getStickyImageForPosition(int position) {
        if(this.stickyHeaderPositionCache != position || this.stickyHeaderImageCache == null) {
            View headerView = getViewForPosition(position);
            this.stickyHeaderImageCache = createImageFromView(headerView);
            this.stickyHeaderImageCache.setBounds(0, 0, headerView.getWidth(), headerView.getHeight());
            this.stickyHeaderPositionCache = position;
        }
        return stickyHeaderImageCache;
    }

    protected View getViewForPosition(int position) {
        ListViewAdapter adapter = (ListViewAdapter)owner().getAdapter();
        ListViewHolder viewHolder = (ListViewHolder)owner().findViewHolderForAdapterPosition(position);
        if(viewHolder == null) {
            viewHolder = adapter.onCreateViewHolder(owner(), adapter.getItemViewType(position));
            adapter.onBindViewHolder(viewHolder, position);

            View view = viewHolder.itemView;
            if (view.getLayoutParams() == null) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
            }

            int ownerMeasureSpecWidth;
            int ownerMeasureSpecHeight;

            if (owner().getLayoutManager().canScrollVertically()) {
                ownerMeasureSpecWidth = View.MeasureSpec.makeMeasureSpec(owner().getWidth(), View.MeasureSpec.EXACTLY);
                ownerMeasureSpecHeight = View.MeasureSpec.makeMeasureSpec(owner().getHeight(), View.MeasureSpec.UNSPECIFIED);
            } else {
                ownerMeasureSpecWidth = View.MeasureSpec.makeMeasureSpec(owner().getWidth(), View.MeasureSpec.UNSPECIFIED);
                ownerMeasureSpecHeight = View.MeasureSpec.makeMeasureSpec(owner().getHeight(), View.MeasureSpec.EXACTLY);
            }

            int paddingWidth = owner().getPaddingLeft() + owner().getPaddingRight();
            int paddingHeight = owner().getPaddingTop() + owner().getPaddingBottom();

            int viewWidth = view.getLayoutParams().width;
            int viewHeight = view.getLayoutParams().height;

            int viewMeasureSpecWidth = ViewGroup.getChildMeasureSpec(ownerMeasureSpecWidth, paddingWidth, viewWidth);
            int viewMeasureSpecHeight = ViewGroup.getChildMeasureSpec(ownerMeasureSpecHeight, paddingHeight, viewHeight);

            view.measure(viewMeasureSpecWidth, viewMeasureSpecHeight);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

            return view;
        }
        return viewHolder.itemView;
    }

    protected int getItemHeaderPosition(int itemPosition) {
        if(isHeader(itemPosition)) {
            return itemPosition;
        }

        for(int i = itemPosition; i >= 0; i--) {
            if(isHeader(i)){
                return i;
            }
        }

        return INVALID_POSITION;
    }

    protected Drawable createImageFromView(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        BitmapDrawable swipeImage = new BitmapDrawable(owner().getResources(), bitmap);
        return swipeImage;
    }

    private boolean isHeader(int position) {
        if(owner().getAdapter() instanceof ListViewDataSourceAdapter) {
            return ((ListViewDataSourceAdapter)owner().getAdapter()).isGroupHeader(position);
        }
        return false;
    }

    private void placeImageOnTop(Drawable image, View view) {
        if(isHorizontal) {
            int right = image.getBounds().right;
            int width = image.getBounds().width();
            if(right > view.getLeft()) {
                Rect bounds = new Rect(view.getLeft() - width, 0, view.getLeft(), view.getHeight());
                image.setBounds(bounds);
            }
        } else {
            int bottom = image.getBounds().bottom;
            int height = image.getBounds().height();
            if (bottom > view.getTop()) {
                Rect bounds = new Rect(0, view.getTop() - height, view.getWidth(), view.getTop());
                image.setBounds(bounds);
            }
        }
    }

    private void placeImageOnBottom(Drawable image, View view) {
        if(isHorizontal) {
            int left = image.getBounds().left;
            int width = image.getBounds().width();
            if (left < view.getRight()) {
                int leftBounds = Math.min(view.getRight() - width, 0);
                Rect bounds = new Rect(leftBounds, 0, leftBounds + width, leftBounds + width);
                image.setBounds(bounds);
            }
        } else {
            int top = image.getBounds().top;
            int height = image.getBounds().height();
            if (top < view.getBottom()) {
                int topBounds = Math.min(view.getBottom() - height, 0);
                Rect bounds = new Rect(0, topBounds, view.getWidth(), topBounds + height);
                image.setBounds(bounds);
            }
        }
    }
}
