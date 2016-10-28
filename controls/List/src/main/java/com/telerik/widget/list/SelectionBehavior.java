package com.telerik.widget.list;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link com.telerik.widget.list.ListViewBehavior} that can be used to allow the end user
 * to select items from the list with gestures.
 */
public class SelectionBehavior extends ListViewBehavior implements ItemReorderBehavior.ItemReorderListener, SwipeExecuteBehavior.SwipeExecuteListener {

    private List<SelectionChangedListener> listeners = new ArrayList<SelectionChangedListener>();
    private SelectionMode selectionMode = SelectionMode.MULTIPLE;
    private SelectionOnTouch selectionOnTouch = SelectionOnTouch.AFTER_START;
    private SelectionOnTouch selectionOnLongPress = SelectionOnTouch.ALWAYS;
    private boolean isSelectionInProgress = false;
    private boolean isAttached = false;
    private boolean handleGestures = true;
    private int itemPostionOnHold = -1;

    /**
     * Creates a new instance of the SelectionBehavior.
     */
    public SelectionBehavior() {
    }

    /**
     * Gets the list of currently selected items.
     */
    public List selectedItems() {
        if(owner().getAdapter() == null) {
            return new ArrayList();
        }
        return ((ListViewAdapter)owner().getAdapter()).selectionService().selectedItems();
    }

    /**
     * Gets the current {@link com.telerik.widget.list.SelectionBehavior.SelectionOnTouch}.
     * This value is used to determine the behavior when the tap gesture is detected.
     * The default value is <code>AFTER_START</code>.
     *
     * @return the current value.
     */
    public SelectionOnTouch getSelectionOnTouch() {
        return selectionOnTouch;
    }

    /**
     * Sets a new value for {@link com.telerik.widget.list.SelectionBehavior.SelectionOnTouch}.
     * This value is used to determine the behavior when the tap gesture is detected.
     * The default value is <code>AFTER_START</code>.
     *
     * @param selectionOnTouch the new value.
     */
    public void setSelectionOnTouch(SelectionOnTouch selectionOnTouch) {
        this.selectionOnTouch = selectionOnTouch;
    }

    /**
     * Gets the current {@link com.telerik.widget.list.SelectionBehavior.SelectionOnTouch}.
     * This value is used to determine the behavior when the long press gesture is detected.
     * The default value is <code>ALWAYS</code>.
     *
     * @return the current value.
     */
    public SelectionOnTouch getSelectionOnLongPress() {
        return selectionOnLongPress;
    }

    /**
     * Sets a new {@link com.telerik.widget.list.SelectionBehavior.SelectionOnTouch} value for long press.
     * This value is used to determine the behavior when the long press gesture is detected.
     * The default value is <code>ALWAYS</code>.
     *
     * @param selectionOnLongPress the new value.
     */
    public void setSelectionOnLongPress(SelectionOnTouch selectionOnLongPress) {
        this.selectionOnLongPress = selectionOnLongPress;
    }

    /**
     * Gets the current {@link com.telerik.widget.list.SelectionBehavior.SelectionMode}.
     * The default value is <code>MULTIPLE</code>.
     *
     * @return the current value.
     */
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * Sets a new {@link com.telerik.widget.list.SelectionBehavior.SelectionMode}.
     * The default value is <code>MULTIPLE</code>.
     *
     * @param  selectionMode the new value.
     */
    public void setSelectionMode(SelectionMode selectionMode) {
        if(this.selectionMode == selectionMode) {
            return;
        }
        if(this.isInProgress()) {
            this.endSelection();
        }
        this.selectionMode = selectionMode;
        this.updateOwnerSelectionMode();
    }

    /**
     * Adds a listener to be called when the selection of items changes.
     *
     * @param listener the new listener.
     */
    public void addListener(SelectionChangedListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener that is called when the selection of items changes.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(SelectionChangedListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * A method that is used to notify the behavior that the selection should be stopped.
     */
    public void endSelection() {
        stopSelectionMode(true);
    }

    @Override
    public void onReorderStarted(int position) {
        // When item hold gesture starts, we are not yet sure if it intends selection or reorder.
        // This is why we track the position without starting selection.
        // If reorder occurs, we stop the tracking and otherwise - if the reorder finished
        // without any item is moved, then we select the tracked item.
        this.itemPostionOnHold = position;
        this.handleGestures = false;
    }

    @Override
    public void onReorderItem(int positionFrom, int positionTo) {
        this.itemPostionOnHold = -1;
    }

    @Override
    public void onReorderFinished() {
        this.handleGestures = true;
        if(selectionOnLongPress == SelectionOnTouch.NEVER ||
                (selectionOnLongPress == SelectionOnTouch.AFTER_START && !isSelectionInProgress)) {
            return;
        }
        if(this.itemPostionOnHold != -1) {
            this.changeIsSelected(itemPostionOnHold);
        }
    }

    @Override
    public void onSwipeStarted(int position) {
        this.handleGestures = false;
    }

    @Override
    public void onSwipeProgressChanged(int position, int currentOffset, View swipeContent) {
    }

    @Override
    public void onSwipeEnded(int position, int finalOffset) {
    }

    @Override
    public void onExecuteFinished(int position) {
        this.handleGestures = true;
    }

    @Override
    public boolean isInProgress() {
        return isSelectionInProgress;
    }

    public boolean getIsSelected(int position) {
        ListViewAdapter adapter = (ListViewAdapter)owner().getAdapter();
        if(adapter != null) {
            return getSelectionState(position);
        }
        return false;
    }

    public void changeIsSelected(int position, boolean value) {
        if(getIsSelected(position) == value) {
            return;
        }
        changeIsSelected(position);
    }

    public void changeIsSelected(int position) {
        boolean oldValue = getIsSelected(position);
        ListViewAdapter adapter = (ListViewAdapter)owner().getAdapter();
        if(adapter == null ||
                (!oldValue && !adapter.canSelect(position)) ||
                (oldValue && !adapter.canDeselect(position))) {
            return;
        }
        if(position >= 0 && position < adapter.getItemCount()) {
            startSelection();
            changeIsSelected(adapter, position);
            if(adapter.selectionService().selectedItems().size() == 0) {
                stopSelectionMode(false);
            }
        }
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        if(!handleGestures) {
            return;
        }

        if(selectionOnLongPress == SelectionOnTouch.NEVER ||
                (selectionOnLongPress == SelectionOnTouch.AFTER_START && !isSelectionInProgress)) {
            return;
        }

        View pressedView = findViewByCoordinates(motionEvent.getX(), motionEvent.getY());
        int position = owner().getChildAdapterPosition(pressedView);
        changeIsSelected(position);
    }

    @Override
    public void onTapUp(MotionEvent motionEvent){
        if(!handleGestures) {
            return;
        }

        if(selectionOnTouch == SelectionOnTouch.NEVER ||
                (selectionOnTouch == SelectionOnTouch.AFTER_START && !isSelectionInProgress)) {
            return;
        }
        View pressedView = findViewByCoordinates(motionEvent.getX(), motionEvent.getY());
        int position = owner().getChildAdapterPosition(pressedView);
        changeIsSelected(position);
    }

    @Override
    void onAdapterChanged(ListViewWrapperAdapter adapter) {
        super.onAdapterChanged(adapter);
        if(isSelectionInProgress) {
            stopSelectionMode(false);
        }
        this.updateOwnerSelectionMode();
        if(!isSelectionInProgress && selectedItems().size() > 0) {
            startSelection();
        }
    }

    @Override
    public void onAttached(RadListView listView) {
        super.onAttached(listView);
        this.isAttached = true;
        this.updateOwnerSelectionMode();
    }

    @Override
    public void onDetached(RadListView owner) {
        if(isSelectionInProgress) {
            endSelection();
        }
        if(owner != null && owner.getAdapter() != null) {
            ListViewAdapter adapter = (ListViewAdapter)owner.getAdapter();
            adapter.selectionService().setSelectionMode(com.telerik.android.data.SelectionMode.NONE);
        }
        this.isAttached = false;
        super.onDetached(owner);
    }

    @Override
    protected void onSaveInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            bundle.putBoolean("selectionInProgress", isSelectionInProgress);
            if (!isSelectionInProgress) {
                return;
            }
            int[] positions = new int[selectedItems().size()];
            for (int i = 0; i < selectedItems().size(); i++) {
                Object item = selectedItems().get(i);
                int position = ((ListViewAdapter) owner().getAdapter()).getPosition(item);
                positions[i] = position;
            }
            bundle.putIntArray("selection", positions);
            stopSelectionMode(true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            boolean isInProgress = bundle.getBoolean("selectionInProgress");
            if (!isInProgress) {
                return;
            }
            startSelection();
            int[] positions = bundle.getIntArray("selection");
            for (int position : positions) {
                changeIsSelected((ListViewAdapter) owner().getAdapter(), position);
            }
        }
    }

    /**
     * A method that initiates selection mode. By default it is started when
     * the long press gesture is detected.
     */
    protected void startSelection() {
        if(isSelectionInProgress) {
            return;
        }

        isSelectionInProgress = true;
        for(SelectionChangedListener listener : listeners) {
            listener.onSelectionStarted();
        }
    }

    private void changeIsSelected(ListViewAdapter adapter, int position) {
        boolean oldState = getSelectionState(position);
        changeSelectionState(position);
        for(SelectionChangedListener listener : listeners) {
            listener.onItemIsSelectedChanged(position, !oldState);
        }
    }

    private void updateOwnerSelectionMode() {
        if(!isAttached || owner().getAdapter() == null) {
            return;
        }

        ListViewAdapter adapter = (ListViewAdapter)owner().getAdapter();
        if(selectionMode == SelectionMode.SINGLE) {
            adapter.selectionService().setSelectionMode(com.telerik.android.data.SelectionMode.SINGLE);
        } else {
            adapter.selectionService().setSelectionMode(com.telerik.android.data.SelectionMode.MULTIPLE);
        }
    }

    private View findViewByCoordinates(float x, float y) {
        return owner().findChildViewUnder(x, y);
    }

    private boolean isPositionValid(int position) {
        if(owner().getAdapter() == null) {
            return false;
        }
        if(position < 0 || position >= owner().getAdapter().getItemCount()) {
            return false;
        }
        return true;
    }

    boolean getSelectionState(int position) {
        if (!isPositionValid(position)) {
            return false;
        }
        ListViewAdapter adapter = ((ListViewAdapter)owner().getAdapter());
        Object item = adapter.getItem(position);
        return adapter.selectionService().isItemSelected(item);
    }

    void changeSelectionState(int position) {
        ListViewAdapter adapter = ((ListViewAdapter) owner().getAdapter());
        boolean oldValue = getSelectionState(position);
        if((!oldValue && !adapter.canSelect(position)) ||
                (oldValue && !adapter.canDeselect(position))) {
            return;
        }
        Object item = adapter.getItem(position);
        Object oldSelection = null;
        if(adapter.selectionService().getSelectionMode() == com.telerik.android.data.SelectionMode.SINGLE &&
                adapter.selectionService().selectedItems().size() > 0) {
            oldSelection = adapter.selectionService().selectedItems().get(0);
        }
        if(adapter.selectionService().isItemSelected(item)) {
            adapter.selectionService().deselectItem(item);
        } else {
            adapter.selectionService().selectItem(item);
        }
        int oldSelectionPosition = -1;
        if(oldSelection != null) {
            oldSelectionPosition = adapter.getPosition(oldSelection);
        }

        int itemsInLayout = owner().getLayoutManager().getChildCount();
        for(int i = 0; i < itemsInLayout; i++) {
            View view = owner().getLayoutManager().getChildAt(i);
            if(oldSelectionPosition != -1) {
                if (owner().getLayoutManager().getPosition(view) == oldSelectionPosition) {
                    view.setSelected(false);
                }
            }
            if(owner().getLayoutManager().getPosition(view) == position) {
                view.setSelected(!oldValue);
            }
        }
    }

    private void stopSelectionMode(boolean notifySelectionService) {
        if(!isSelectionInProgress) {
            return;
        }

        isSelectionInProgress = false;
        if(owner().getAdapter() == null) {
            return;
        }

        ListViewAdapter adapter = (ListViewAdapter)owner().getAdapter();
        if(notifySelectionService) {
            adapter.selectionService().clearSelection();
        }
        for(SelectionChangedListener listener : listeners) {
            listener.onSelectionEnded();
        }
        if(notifySelectionService) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Interface definition for a callback to be invoked when the selection is changed.
     */
    public interface SelectionChangedListener {

        /**
         * Called when the selection starts.
         */
        void onSelectionStarted();

        /**
         * Called when the selection state of the item at the specified position changes.
         *
         * @param  position the position of the item whose selection state changes.
         * @param  newValue the new value whether the item is selected.
         */
        void onItemIsSelectedChanged(int position, boolean newValue);

        /**
         * Called when the selection ends.
         */
        void onSelectionEnded();
    }

    /**
     * An enumeration with the different modes of selection that can be used as a parameter for
     * the {@link #setSelectionMode(com.telerik.widget.list.SelectionBehavior.SelectionMode)} method.
     */
    public enum SelectionMode {
        /**
         * A mode that specifies that more than one item can be selected at a time.
         */
        MULTIPLE,

        /**
         * A mode that specifies that only one item can be selected at a time.
         */
        SINGLE
    }

    /**
     * An enumeration with the different modes of selection that can be used as a parameter for
     * the {@link #setSelectionOnTouch(com.telerik.widget.list.SelectionBehavior.SelectionOnTouch)} method.
     */
    public enum SelectionOnTouch {
        /**
         * A mode that specifies that single tap gesture should never be considered by this behavior.
         */
        NEVER,

        /**
         * A mode that specifies that the single tap gesture should be regarded the same way as the long tap.
         * That is change the selection state of the tapped item.
         */
        ALWAYS,

        /**
         * A mode that specifies that the single tap gesture should be considered only when the selection is in progress.
         * This means that the selection should be started by long press first and then taps will be able to change the
         * state of the tapped items.
         */
        AFTER_START
    }
}
