package com.telerik.widget.list;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link com.telerik.widget.list.ListViewBehavior} that can be used to not load
 * all items at start but wait until the user 'demands' more. This can happen when a special button is
 * clicked (when the mode is MANUAL) or when a certain number of items before the end is reached
 * (when mode is AUTOMATIC).
 */
public class LoadOnDemandBehavior extends ListViewBehavior {

    private static final int DEFAULT_MAX_REMAINING_ITEMS = 10;

    private List<LoadOnDemandListener> listeners = new ArrayList<LoadOnDemandListener>();

    private ListViewWrapperAdapter adapter;
    private LoadOnDemandMode mode = LoadOnDemandMode.MANUAL;
    private boolean isAttachedToAdapter = false;

    private int maxRemainingItems = DEFAULT_MAX_REMAINING_ITEMS;

    private View manualDemandView;
    private View automaticDemandView;
    private boolean manualDemandIsDefault = true;
    private boolean automaticDemandIsDefault = true;
    private LoadingListener loadingListener;
    private boolean isLoading = false;
    private boolean isEnabled = true;
    private String textIdle;
    private String textBusy;
    private ViewGroup indicatorsHolder;

    /**
     * Creates a new instance of the LoadOnDemandBehavior with default settings.
     */
    public LoadOnDemandBehavior() {
        initListener();
    }

    /**
     * Creates a new instance of the LoadOnDemandBehavior which uses the provided views as presenters
     * of the load button in manual mode and the load indicator in automatic mode.
     *
     * Please note that if you use this method will have to manually handle the events and the states of your indicators.
     *
     * @param manualDemandView      View that represents the load more button.
     * @param automaticDemandView   View that represents the loading indicator.
     */
    public LoadOnDemandBehavior(View manualDemandView, View automaticDemandView) {
        if(manualDemandView != null) {
            this.manualDemandView = manualDemandView;
            this.manualDemandIsDefault = false;
        }
        if(automaticDemandView != null) {
            this.automaticDemandView = automaticDemandView;
            this.automaticDemandIsDefault = false;
        }
        initListener();
    }

    /**
     * Gets the current value of the items remaining that are necessary to invoke loading in the
     * automatic load on demand mode.
     *
     * For example, if you initially have 30 items, and you kept the default value, which is 10,
     * when the user sees the 20th item (30 items total - 10 max items remaining), the loading will
     * be initiated.
     *
     * @return the current max remaining items
     */
    public int getMaxRemainingItems() {
        return maxRemainingItems;
    }

    /**
     * Sets a new value for the remaining items that are necessary to invoke loading in the
     * automatic load on demand mode.
     *
     * For example, if you initially have 30 items, and you kept the default value, which is 10,
     * when the user sees the 20th item (30 items total - 10 max items remaining), the loading will
     * be initiated.
     *
     * @param maxRemainingItems max remaining items to be used
     */
    public void setMaxRemainingItems(int maxRemainingItems) {
        this.maxRemainingItems = maxRemainingItems;
    }

    /**
     * Makes the current behavior enabled.
     * True by default.
     *
     * @param enabled whether this behavior should be enabled
     */
    public void setEnabled(boolean enabled) {
        if(this.isEnabled == enabled) {
            return;
        }
        isEnabled = enabled;
        if(isAttachedToAdapter) {
            handleElementsVisibility();
        }
    }

    /**
     * Gets a value indicating whether the current behavior is enabled.
     * True by default.
     *
     * @return  whether this behavior is enabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets a new {@link com.telerik.widget.list.LoadOnDemandBehavior.LoadOnDemandMode}.
     * The default value is <code>MANUAL</code>.
     *
     * @param mode whether the user will demand items manually through button or automatically.
     */
    public void setMode(LoadOnDemandMode mode) {
        if(this.mode == mode) {
            return;
        }
        this.mode = mode;
        if(isAttachedToAdapter) {
            handleElementsVisibility();
            adapter.updateOnDemandSettings(indicatorsHolder, maxRemainingItems, mode == LoadOnDemandMode.AUTOMATIC);
            updateIndicatorState();
        }
    }

    /**
     * Gets the current {@link com.telerik.widget.list.LoadOnDemandBehavior.LoadOnDemandMode}.
     * The default value is <code>MANUAL</code>.
     *
     * @return whether the user will demand items manually through button or automatically.
     */
    public LoadOnDemandMode getMode() {
        return mode;
    }

    /**
     * Adds a listener to be called when items are demanded.
     *
     * @param listener the new listener.
     */
    public void addListener(LoadOnDemandListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener that is called when items are demanded.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(LoadOnDemandListener listener) {
        this.listeners.add(listener);
    }

    /**
     * A method that initiates loading. The default button that is used in manual mode
     * uses this method to invoke the loading when it is clicked.
     */
    public void startLoad() {
        if(!isLoading && isEnabled) {
            isLoading = true;
            updateIndicatorState();
            for(LoadOnDemandListener listener : listeners) {
                listener.onLoadStarted();
            }
        }
    }

    /**
     * A method that is used to notify the behavior that the loading is complete. In manual mode, when it is called the button
     * returns to its enabled state and in automatic the loading indicator disappears.
     */
    public void endLoad() {
        if(isLoading) {
            isLoading = false;
            updateIndicatorState();
            for (LoadOnDemandListener listener : listeners) {
                listener.onLoadFinished();
            }
        }
    }

    @Override
    public boolean isInProgress() {
        return isLoading;
    }

    @Override
    public void onAttached(RadListView listView) {
        super.onAttached(listView);

        if(manualDemandView == null) {
            manualDemandView = createDefaultManualDemandView();
        }
        if(automaticDemandView == null) {
            automaticDemandView = createDefaultAutomaticDemandView();
        }

        if(listView.getAdapter() == null) {
            return;
        }

        this.adapter = listView.wrapperAdapter();
        attachIndicatorsToAdapter();
    }

    @Override
    public void onDetached(RadListView listView) {
        detachIndicatorsFromAdapter();
        this.adapter = null;
        super.onDetached(listView);
    }

    @Override
    void onAdapterChanged(ListViewWrapperAdapter adapter) {
        detachIndicatorsFromAdapter();
        super.onAdapterChanged(adapter);
        this.adapter = adapter;
        attachIndicatorsToAdapter();
    }

    private void initListener() {
        loadingListener = new LoadingListener() {
            @Override
            public void onLoadingRequested() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        startLoad();
                    }
                });
            }

            @Override
            public void onLoadingFinished() {
                endLoad();
            }
        };
    }

    private View createDefaultManualDemandView() {
        LayoutInflater inflater = (LayoutInflater) owner().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View manualDemand = inflater.inflate(R.layout.on_demand_manual, owner(), false);
        manualDemand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoad();
            }
        });
        textIdle = owner().getContext().getResources().getString(R.string.on_demand_manual_button_idle);
        textBusy = owner().getContext().getResources().getString(R.string.on_demand_manual_button_busy);
        return manualDemand;
    }

    private View createDefaultAutomaticDemandView() {
        LayoutInflater inflater = (LayoutInflater) owner().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View automaticDemand = inflater.inflate(R.layout.on_demand_automatic, owner(), false);
        return automaticDemand;
    }

    private void updateIndicatorState(){
        if(mode == LoadOnDemandMode.MANUAL) {
            if(!manualDemandIsDefault) {
                return;
            }
            ((Button) manualDemandView).setText(this.isLoading ? textBusy : textIdle);
            ((Button) manualDemandView).setEnabled(!this.isLoading);
        } else {
            if(!automaticDemandIsDefault) {
                return;
            }
            ((ProgressBar) automaticDemandView).setIndeterminate(this.isLoading);
        }
    }

    private void handleElementsVisibility() {
        if(!isEnabled) {
            manualDemandView.setVisibility(View.GONE);
            automaticDemandView.setVisibility(View.GONE);
            return;
        }
        if(mode == LoadOnDemandMode.MANUAL) {
            manualDemandView.setVisibility(View.VISIBLE);
            automaticDemandView.setVisibility(View.GONE);
        } else {
            manualDemandView.setVisibility(View.GONE);
            automaticDemandView.setVisibility(View.VISIBLE);
        }
    }

    private void attachIndicatorsToAdapter() {
        indicatorsHolder = new FrameLayout(owner().getContext());
        indicatorsHolder.addView(this.manualDemandView);
        indicatorsHolder.addView(this.automaticDemandView);
        indicatorsHolder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        adapter.addLoadingListener(this.loadingListener);
        adapter.updateOnDemandSettings(indicatorsHolder, maxRemainingItems, mode == LoadOnDemandMode.AUTOMATIC);
        isAttachedToAdapter = true;
        handleElementsVisibility();
    }

    private void detachIndicatorsFromAdapter() {
        if(!isAttachedToAdapter) {
            return;
        }
        indicatorsHolder.removeAllViews();
        adapter.removeLoadingListener(this.loadingListener);
        adapter.updateOnDemandSettings(null, maxRemainingItems, mode == LoadOnDemandMode.AUTOMATIC);
        isAttachedToAdapter = false;
    }

    static interface LoadingListener {
        void onLoadingRequested();
        void onLoadingFinished();
    }

    /**
     * Interface definition for a callback to be invoked when load on demand starts or finishes.
     */
    public static interface LoadOnDemandListener {

        /**
         * Called when load on demand starts.
         */
        public void onLoadStarted();

        /**
         * Called when load on demand finishes.
         */
        public void onLoadFinished();
    }

    /**
     * An enumeration with the different modes that can be used as a parameter for
     * the {@link #setMode(com.telerik.widget.list.LoadOnDemandBehavior.LoadOnDemandMode)} method.
     */
    public static enum LoadOnDemandMode {
        /**
         * A mode that is represented by a button at the end of the list
         * that the user can click to request more items.
         */
        MANUAL,

        /**
         * A mode that is represented by a loading indicator at the end of the list
         * that displays while new items are loading. The loading process is initiated when
         * the user reaches near the end of the list of currently loaded items.
         */
        AUTOMATIC
    }
}
