package com.telerik.widget.chart.engine.elementTree;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;
import com.telerik.widget.chart.engine.chartAreas.LoadContext;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyBagObject;
import com.telerik.widget.chart.engine.view.ChartElementPresenter;

/**
 * This class is a base for all models that comprise the Chart and are used by the Chart engine
 * to calculate the coordinates and bounds of the visual elements.
 */
public abstract class ChartNode extends PropertyBagObject {

    public static final int PROPERTY_CHANGING_MESSAGE = ChartMessage.register();
    public static final int PROPERTY_CHANGED_MESSAGE = ChartMessage.register();
    protected ChartAreaModel chartArea;
    private RadRect layoutSlot = RadRect.getEmpty();
    protected boolean invalidateScheduled = false;
    protected boolean isVisible = true;
    public boolean trackPropertyChanging = false;
    public boolean trackPropertyChanged = false;
    protected ChartElement parent;

    int index;
    int collectionIndex;
    NodeState nodeState = NodeState.INITIAL;
    LoadContext loadContext;

    /**
     * Creates an instance of the {@link ChartNode} class.
     */
    protected ChartNode() {
        this.index = -1;
        this.collectionIndex = -1;
    }

    /**
     * Returns a boolean value indicating whether the current node is visible
     * and takes part in the chart layout.
     *
     * @return <code>true</code> if the node is visible, otherwise <code>false</code>.
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Sets a boolean value indicating whether the current node is visible
     * and takes part in the chart layout.
     *
     * @param value <code>true</code> if the {@link ChartNode} instance is visible, otherwise <code>false</code>.
     */
    public void setIsVisible(boolean value) {
        this.isVisible = value;
    }

    /**
     * Returns an instance of the {@link ChartAreaModel}
     * class representing the chart model which this node is part of.
     *
     * @return the {@link com.telerik.widget.chart.engine.chartAreas.ChartAreaModel} model.
     */
    public ChartAreaModel chartArea() {
        return this.chartArea;
    }

    /**
     * Gets the index of the {@link ChartNode} instance in its parent {@link NodeCollection}.
     *
     * @return the index of the {@link ChartNode}.
     */
    public int index() {
        return this.index;
    }

    /**
     * Gets the index of the {@link ChartNode} instance in its parent {@link ElementCollection}.
     *
     * @return the index of the {@link ChartNode}.
     */
    public int collectionIndex() {
        return this.collectionIndex;
    }

    /**
     * Gets a value of the {@link NodeState} enum which determines the state of the current
     * {@link ChartNode}.
     *
     * @return the state of the {@link ChartNode}.
     */
    public NodeState getNodeState() {
        return this.nodeState;
    }

    /**
     * Gets a value determining whether the node three of the parent chart is been loaded.
     *
     * @return <code>true</code> if the tree is loaded, otherwise <code>false</code>.
     */
    public boolean isTreeLoaded() {
        return this.chartArea != null && this.chartArea.isTreeLoaded();
    }

    /**
     * Gets an instance of the {@link RadRect} class which depicts the layout slot of the current
     * {@link ChartNode}.
     *
     * @return the layout slot of the {@link ChartNode}.
     */
    public RadRect getLayoutSlot() {
        return this.layoutSlot;
    }

    /**
     * Gets the parent element of the current {@link ChartNode}.
     *
     * @return an instance of the {@link ChartElement} class representing the parent of the current
     * {@link ChartNode}.
     */
    public ChartElement getParent() {
        return this.parent;
    }

    /**
     * Initializes the parent of the current {@link ChartNode} to the specified {@link ChartElement}.
     *
     * @param value the {@link ChartElement} instance which represents the parent of this
     *              {@link ChartNode}.
     */
    public void setParent(ChartElement value) {
        if (this.parent == value) {
            return;
        }

        ChartElement oldParent = this.parent;
        this.setParentCore(value);
        this.onParentChanged(oldParent);
    }

    /**
     * Gets the {@link ChartElementPresenter} instance whichrepresents the visual element in which
     * this {@link ChartNode} is visualised.
     *
     * @return the {@link ChartElementPresenter} instance.
     */
    public ChartElementPresenter getPresenter() {
        if (this.parent == null) {
            return null;
        }

        return this.parent.getPresenter();
    }

    /**
     * Arranges the current {@link ChartNode} into a rectangle depicted by the provided
     * {@link RadRect} instance.
     *
     * @param rect the {@link RadRect} instance that represents the bounds in which to arrange the
     *             {@link ChartNode}.
     * @return an instance of the {@link RadRect} class that represents the {@link ChartNode} bounds
     * after the arrange has been done.
     */
    public RadRect arrange(final RadRect rect) {
        return this.arrange(rect, true);
    }

    /**
     * Arranges the current {@link ChartNode} into a rectangle depicted by the provided
     * {@link RadRect} instance and rounds the layout parameters if required.
     *
     * @param rect              the {@link RadRect} instance that represents the bounds in which to
     *                          arrange the {@link ChartNode}.
     * @param shouldRoundLayout <code>true</code> if layout coordinates rounding is required,
     *                          otherwise <code>false</code>.
     * @return an instance of the {@link RadRect} class that represents the {@link ChartNode} bounds
     * after the arrange has been done.
     */
    public RadRect arrange(final RadRect rect, boolean shouldRoundLayout) {
        if (!this.isTreeLoaded()) {
            return RadRect.getEmpty();
        }

        RadRect arrangeRect = this.arrangeOverride(rect);
        // do not allow negative width and height
        if (arrangeRect.getWidth() < 0) {
            arrangeRect = new RadRect(arrangeRect.getX(), arrangeRect.getY(), 0.0, arrangeRect.getHeight());
        }
        if (arrangeRect.getHeight() < 0) {
            arrangeRect = new RadRect(arrangeRect.getX(), arrangeRect.getY(), arrangeRect.getWidth(), 0.0);
        }

        if (shouldRoundLayout) {
            arrangeRect = RadRect.round(arrangeRect);
        }

        this.layoutSlot = arrangeRect;
        this.invalidateScheduled = false;
        return this.layoutSlot;
    }

    /**
     * Invalidates the layout parameters of the current {@link ChartNode} instance in the context of
     * the owning chart area.
     */
    public void invalidate() {
        if (this.invalidateScheduled || !this.isTreeLoaded()) {
            return;
        }

        this.invalidateScheduled = true;

        this.chartArea.invalidateNode(this);
    }

    /**
     * Applies layout snapping and corrects rounding errors.
     */
    public void applyLayoutRounding() {
    }

    @Override
    public boolean setValue(int key, Object value) {
        if (!this.trackPropertyChanging && !this.trackPropertyChanged) {
            return super.setValue(key, value);
        }

        Object currentValue = this.getValue(key);
        if (currentValue != null && currentValue.equals(value)) {
            return false;
        }

        RadPropertyEventArgs args = new RadPropertyEventArgs(key, currentValue, value);

        if (this.trackPropertyChanging) {
            this.onPropertyChanging(args);
            if (args.Cancel) {
                return false;
            }
        }

        this.propertyStore.setEntry(key, value);

        if (this.trackPropertyChanged) {
            this.onPropertyChanged(args);
        }

        return true;
    }

    @Override
    public boolean clearValue(int key) {
        if (!this.trackPropertyChanging && !this.trackPropertyChanged) {
            return super.clearValue(key);
        }

        Object currentValue = this.getValue(key);
        RadPropertyEventArgs args = new RadPropertyEventArgs(key, currentValue, null);

        if (this.trackPropertyChanging) {
            this.onPropertyChanging(args);
            if (args.Cancel) {
                return false;
            }
        }

        this.propertyStore.removeEntry(key);

        if (this.trackPropertyChanged) {
            this.onPropertyChanged(args);
        }

        return true;
    }

    /**
     * Loads the current {@link ChartNode} into the provided {@link LoadContext} instance.
     *
     * @param context the {@link LoadContext} into which the current {@link ChartNode} will be loaded.
     */
    public void load(LoadContext context) {
        if (this.nodeState == NodeState.LOADING || this.nodeState == NodeState.LOADED) {
            return;
        }

        this.nodeState = NodeState.LOADING;

        // keep references to the load context and plot area
        this.loadContext = context;
        this.chartArea = context.getChartArea();

        // allow inheritors to provide their own custom logic
        this.loadCore(context);

        this.nodeState = NodeState.LOADED;
    }

    void receiveMessage(ChartMessage message) {
        this.processMessage(message);

        // tell the message that we were the last receiver
        message.previousReceiver = this;
    }

    void unload() {
        this.nodeState = NodeState.UNLOADING;
        this.unloadCore();
        this.nodeState = NodeState.UNLOADED;
    }

    /**
     * Gets a value from the {@link MessageDispatchMode} enum which defines how the specified message
     * will be dispatched for this {@link ChartNode}.
     *
     * @param messageId the id of the message for which to get the dispatch mode.
     */
    protected MessageDispatchMode getMessageDispatchMode(int messageId) {
        return MessageDispatchMode.BUBBLE;
    }

    /**
     * Called when the current {@link ChartNode} executes its core loading logic.
     *
     * @param context the {@link LoadContext} representing the load context.
     */
    protected void loadCore(LoadContext context) {
    }

    /**
     * Called when the node unloads. Resets the parent, the load context and the chart area references.
     */
    protected void unloadCore() {
        this.parent = null;
        this.loadContext = null;
        this.chartArea = null;
    }

    /**
     * Called when the layout engine arranges the current {@link ChartNode} in the provided rectangle.
     *
     * @param rect an instance of the {@link RadRect} class depicting the layout slot for the
     *             current {@link ChartNode}
     * @return an instance of the {@link RadRect} class representing the layout slot into which the
     * node has been arranged.
     */
    protected RadRect arrangeOverride(final RadRect rect) {
        return rect;
    }

    /**
     * Called when the current {@link ChartNode} receives a message.
     *
     * @param message an instance of the {@link ChartMessage} class representing the message.
     */
    protected void processMessage(ChartMessage message) {
    }

    /**
     * Called when the parent of the current {@link ChartNode} changes.
     *
     * @param oldParent an instance of the  {@link ChartElement} representing the old parent.
     */
    protected void onParentChanged(ChartElement oldParent) {
    }

    /**
     * Called when a property on the current {@link ChartNode} is about to change.
     *
     * @param e an instance of the {@link RadPropertyEventArgs} class containing information about the event.
     */
    protected void onPropertyChanging(RadPropertyEventArgs e) {
        if (!this.isTreeLoaded()) {
            return;
        }

        ChartMessage message = new ChartMessage(
                this, PROPERTY_CHANGING_MESSAGE, e, this.getMessageDispatchMode(PROPERTY_CHANGING_MESSAGE));
        this.chartArea.getDispatcher().dispatchMessage(message);
    }

    /**
     * Called when a property on the current {@link ChartNode} has changed.
     *
     * @param e an instance of the {@link RadPropertyEventArgs} class containing information about the event.
     */
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        if (!this.isTreeLoaded()) {
            return;
        }

        ChartMessage message = new ChartMessage(
                this, PROPERTY_CHANGED_MESSAGE, e, this.getMessageDispatchMode(PROPERTY_CHANGING_MESSAGE));
        this.chartArea.getDispatcher().dispatchMessage(message);

        // raise the system PropertyChanged event
        this.raisePropertyChanged(e.getPropertyName(), e.getKey());
    }

    /**
     * Fires the property changed event for the specified property name.
     *
     * @param propertyName the name of the property for which to fire the property changed event.
     * @param propKey      the key of the property in the property store for which the event will be fired.
     */
    protected void raisePropertyChanged(String propertyName, int propKey) {
/* TODO c#
        // raise the system PropertyChanged event
        PropertyChangedEventHandler eh = this.Events[PropertyChangedEventKey] as PropertyChangedEventHandler;
        if (eh == null) {
            return;
        }

        if (String.IsNullOrEmpty(name)) {
            name = PropertyKeys.getNameByKey(this.GetType(), propKey);
        }

        eh(this, new PropertyChangedEventArgs(name));
*/
    }

    private void setParentCore(ChartElement parent) {
        this.parent = parent;
    }
}
