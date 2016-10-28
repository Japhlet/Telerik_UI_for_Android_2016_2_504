package com.telerik.widget.list;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.telerik.widget.list.ItemReorderBehavior;
import com.telerik.widget.list.RadListView;

/**
 * Extended ItemReorderBehavior which allows reorder with a special element used as a handle. With
 * this behavior the reordering happens by simply dragging the item by the handle.
 */
public class ReorderWithHandlesBehavior extends ItemReorderBehavior {

    private RadListView owner;
    private boolean reorderStarted = false;
    private boolean reorderAttached = false;
    private int handleId;

    public ReorderWithHandlesBehavior(int handleId) {
        this.handleId = handleId;
    }


    @Override
    public boolean onShortPressDrag(float startX, float startY, float currentX, float currentY) {
        if (!reorderStarted) {
            ViewGroup view = (ViewGroup)this.resolveHandleViewForCoordinates(startX, startY);

            if (view == null) {
                return false;
            }
            View handleView = this.getReorderHandleOverride(view);
            if (handleView == null || handleView.getLeft() > startX || handleView.getRight() < startX) {
                return false;
            }
            startReorder(startX, startY);
            reorderStarted = true;
        }

        moveReorderImage(startX, startY, currentX, currentY);
        return true;
    }

    @Override
    public boolean onActionUpOrCancel(boolean isCanceled) {
        if (!reorderStarted) {
            return false;
        }
        endReorder(isCanceled);
        reorderStarted = false;
        return true;
    }

    @Override
    public boolean isInProgress() {
        return this.reorderAttached;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return reorderStarted;
    }

    @Override
    public void onAttached(RadListView owner) {
        super.onAttached(owner);
        this.owner = owner;
        this.reorderAttached = true;
    }

    @Override
    public void onDetached(RadListView listView) {
        super.onDetached(listView);
        this.reorderAttached = false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public void onLongPressDrag(float startX, float startY, float currentX, float currentY) {
    }

    @Override
    protected void endReorder(boolean isCanceled) {
        super.endReorder(isCanceled);
        owner().invalidate();
    }

    @Override
    public boolean onLongPressDragEnded(boolean isCanceled) {
        return false;
    }

    /**
     * Returns a {@link View} that contains the point defined by the provided coordinates.
     * @param startX the x-coordinate of the point.
     * @param startY the y-coordinate of the point.
     * @return the {@link View} that contains the point.
     */
    public View resolveHandleViewForCoordinates(float startX, float startY){
        return owner.findChildViewUnder(startX, startY);
    }


    /**
     * Gets an alternative {@link View} to be used as a reorder handle.
     * @return an instance of the {@link View} class to be used as the reorder handle.
     */
    public View getReorderHandleOverride(ViewGroup itemView){
        return itemView.findViewById(this.handleId);
    }
}
