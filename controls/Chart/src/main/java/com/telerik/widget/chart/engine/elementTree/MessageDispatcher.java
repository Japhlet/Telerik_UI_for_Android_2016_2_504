package com.telerik.widget.chart.engine.elementTree;

import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;

/**
 * Provides infrastructure for dispatching messages between {@link ChartNode} instances.
 */
public class MessageDispatcher {

    private ChartAreaModel chartArea;
    private boolean isEnabled = false;

    /**
     * Creates an instance of the {@link MessageDispatcher} class with a specified
     * {@link ChartAreaModel} owner.
     *
     * @param owner the {@link ChartAreaModel} for which messaging infrastructure will be provided.
     */
    public MessageDispatcher(ChartAreaModel owner) {
        this.isEnabled = true;
        this.chartArea = owner;
    }

    /**
     * Returns a boolean value determining whether the current {@link MessageDispatcher} is enabled.
     *
     * @return <code>true<code/> if the dispatcher is enabled, otherwise <code>false</code>.
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Dispatches the provided message to the element tree of the owning {@link ChartAreaModel}.
     *
     * @param message the {@link ChartMessage} instance to dispatch.
     */
    public void dispatchMessage(ChartMessage message) {
        if (!this.canDispatch(message)) {
            return;
        }

        this.dispatchToTree(message, message.getSender());
    }

    private boolean previewMessage(ChartMessage message) {
        this.chartArea.previewMessage(message);
        return message.stopDispatch;
    }

    private void dispatchToTree(ChartMessage message, ChartNode leaf) {
        if (message.dispatchMode.contains(MessageDispatchMode.BUBBLE)) {
            message.dispatchPhase = MessageDispatchPhase.BUBBLE;
            this.bubbleMessage(message, leaf);
        }

        if (message.stopDispatch) {
            return;
        }

        if (message.dispatchMode.contains(MessageDispatchMode.TUNNEL)) {
            message.dispatchPhase = MessageDispatchPhase.TUNNEL;
            this.tunnelMessage(message, leaf);
        }
    }

    private void bubbleMessage(ChartMessage message, ChartNode directTarget) {
        message.dispatchPhase = MessageDispatchPhase.BUBBLE;
        ChartElement parent = directTarget.getParent();

        while (parent != null) {
            parent.receiveMessage(message);
            if (message.stopDispatch) {
                return;
            }

            parent = parent.getParent();
        }
    }

    private void tunnelMessage(ChartMessage message, ChartNode directTarget) {
        ChartElement element = (ChartElement) directTarget;
        if (element == null) {
            return;
        }

        // TODO: What tree traversal approach should be used here?
        for (ChartNode descendant : element.enumDescendants(TreeTraversalMode.DEPTH_FIRST)) {
            descendant.receiveMessage(message);
            if (message.stopDispatch) {
                return;
            }
        }
    }

    private boolean canDispatch(ChartMessage message) {
        if (!this.isEnabled) {
            return false;
        }

        if (message.getSender() == null) {
            // We want to run the tests in both Debug and Release modes.
            this.throwNoSenderException();
            return false;
        }

        return !this.previewMessage(message);
    }

    private void throwNoSenderException() {
        throw new IllegalArgumentException("Must have a valid Sender at this point.");
    }
}

