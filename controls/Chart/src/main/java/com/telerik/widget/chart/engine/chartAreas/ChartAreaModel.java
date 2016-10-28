package com.telerik.widget.chart.engine.chartAreas;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.elementTree.ChartMessage;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ElementCollection;
import com.telerik.widget.chart.engine.elementTree.MessageDispatcher;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
import com.telerik.widget.chart.engine.elementTree.NodeState;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.view.ChartView;

import java.util.ArrayList;

/**
 * This class represents a non-visual model which contains all elements that comprise a Chart.
 * This class is used by the drawing mechanism to visualize the chart elements. You are not supposed
 * to use this class in your application.
 */
public class ChartAreaModel extends ChartElement {

    private ChartPlotAreaModel plotArea;
    private ChartView view;

    private MessageDispatcher dispatcher;
    private byte suspendUpdate;

    /**
     * Creates an instance of the {@link ChartAreaModel} class by initializing its plot area and messaging
     * infrastructure.
     */
    public ChartAreaModel() {
        this.dispatcher = new MessageDispatcher(this);
        this.plotArea = new ChartPlotAreaModel();
        this.children.add(this.plotArea);
    }

    /**
     * Gets the current chart view.
     *
     * @return the current chart view.
     */
    public ChartView getView() {
        return this.view;
    }

    /**
     * Gets an instance of the {@link ChartPlotAreaModel} class
     * representing the part of the chart where the data is plotted.
     *
     * @return the {@link ChartPlotAreaModel} instance.
     */
    public ChartPlotAreaModel getPlotArea() {
        return this.plotArea;
    }

    /**
     * Gets a {@link ElementCollection} containing the
     * currently available series in this {@link ChartAreaModel}.
     *
     * @return the series collection.
     */
    public ElementCollection<ChartSeriesModel> getSeries() {
        return this.plotArea.series;
    }

    @Override
    public boolean isTreeLoaded() {
        return this.view != null && this.getNodeState() == NodeState.LOADED;
    }

    /**
     * Gets a boolean value determining whether the messages and invalidation
     * mechanisms are suspended.
     *
     * @return <code>true<code/> if the model is suspended, otherwise <code>false</code>.
     */
    public boolean isSuspended() {
        return this.suspendUpdate > 0;
    }

    /**
     * Gets the {@link MessageDispatcher} instance associated with this {@link ChartAreaModel}.
     *
     * @return the {@link MessageDispatcher} instance.
     */
    public MessageDispatcher getDispatcher() {
        return this.dispatcher;
    }

    /**
     * Called when the Chart's zoom factor changes.
     */
    public void onZoomChanged() {
        if (this.isTreeLoaded()) {
            this.processZoomChanged();
        }
    }

    /**
     * Called when the Chart's pan offset changes.
     */
    public void onPanOffsetChanged() {
        if (this.isTreeLoaded()) {
            this.processPanOffsetChanged();
        }
    }

    /**
     * Locks the {@link ChartAreaModel} for performing state update calls.
     * When the model is locked, no messages or {@link ChartNode} updates are processed.
     */
    public void beginUpdate() {
        this.suspendUpdate++;
    }

    /**
     * Unlocks the {@link ChartAreaModel} thus allowing the processing of {@link ChartNode} updates
     * and messages.
     *
     * @param refresh if set to <code>true</code> forces the invalidation and refreshing of the model.
     */
    public void endUpdate(boolean refresh) {
        if (this.suspendUpdate == 0) {
            return;
        }

        this.suspendUpdate--;
        if (this.suspendUpdate == 0 && refresh) {
            this.invalidateNode(this);
        }
    }

    /**
     * Loads the element tree of the current {@link ChartAreaModel} with the corresponding
     * {@link ChartView}.
     *
     * @param view the {@link ChartView} to load the tree with.
     */
    public void loadElementTree(ChartView view) {
        if (this.isTreeLoaded()) {
            return;
        }

        this.view = view;
        this.load(new LoadContext(this));
    }

    /**
     * Invalidates the state of the specified {@link ChartNode} and refreshes it.
     *
     * @param node the {@link ChartNode} to invalidate and refresh.
     */
    public void invalidateNode(ChartNode node) {
        if (!this.isTreeLoaded() || this.isSuspended()) {
            return;
        }

        if (node.getPresenter() != null) {
            node.getPresenter().refreshNode(node);
        } else {
            this.view.refreshNode(node);
        }
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        this.beginUpdate();

        this.plotArea.arrange(rect);
        this.applyLayoutRounding();

        this.endUpdate(false);

        return rect;
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child == this.plotArea) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }

    /**
     * Used by the {@link MessageDispatcher} to delegate messages to the
     * {@link ChartAreaModel}.
     *
     * @param message the {@link ChartMessage} instance representing the message.
     */
    public void previewMessage(ChartMessage message) {
        if (this.isSuspended() || !this.isTreeLoaded()) {
            message.stopDispatch = true;
        } else {
            if (message.getId() == ChartNode.PROPERTY_CHANGED_MESSAGE) {
                this.invalidate(PropertyKeys.getPropertyFlags(((RadPropertyEventArgs) message.data).getKey()));
            } else if (message.getId() == ChartSeriesModel.DATA_POINTS_MODIFIED_MESSAGE_KEY) {
                this.invalidate(ChartAreaInvalidateFlags.ALL);
            }
        }
    }

    /**
     * Invalidates the {@link ChartAreaModel} by specifying what types of calculations
     * will be performed after validation.
     *
     * @param flags a set of flas specifying the type of invalidation.
     */
    public void invalidate(int flags) {
        if (this.isTreeLoaded()) {
            this.invalidateCore(flags);
        }
    }

    void invalidateCore(int flags) {
        if ((flags & ChartAreaInvalidateFlags.INVALIDATE_SERIES) == ChartAreaInvalidateFlags.INVALIDATE_SERIES) {
            for (ChartSeriesModel series : this.plotArea.series) {
                series.invalidate();
            }
        }
    }

    /**
     * Generates a collection of reasons describing why the chart
     * could not be loaded.
     *
     * @return an {@link java.lang.Iterable} implementation providing access
     * to the not loaded reasons.
     */
    public Iterable<String> getNotLoadedReasons() {
        return new ArrayList<String>();
    }

    /**
     * Performs processing related to changes in the zoom factor of the chart.
     */
    protected void processZoomChanged() {
        this.invalidate();
    }

    /**
     * Performs processing related to changes in the pan offset of the chart.
     */
    protected void processPanOffsetChanged() {
        this.invalidate();
    }
}

