package com.telerik.widget.chart.engine.elementTree;

import java.util.EnumSet;

/**
 * The chart engine uses chart messages to dispatch notifications among its different components.
 */
public class ChartMessage {

    private static int counter = 0;

    private ChartNode sender;
    private int id = 0;

    /**
     * Creates a new instance of the {@link ChartMessage} class.
     *
     * @param sender       The sender of the message.
     * @param id           The message id.
     * @param data         The data of the message.
     * @param dispatchMode The {@link MessageDispatchMode}
     */
    public ChartMessage(ChartNode sender, int id, Object data, MessageDispatchMode dispatchMode) {
        this.sender = sender;
        this.id = id;
        this.data = data;
        this.dispatchMode = EnumSet.of(dispatchMode);
    }

    /**
     * A value indicating whether the message is handled (processed) by some receiver.
     */
    public boolean handled = false;

    /**
     * A value indicating whether message may continue being dispatched or not.
     */
    public boolean stopDispatch = false;

    /**
     * The current phase of the dispatch process.
     */
    public MessageDispatchPhase dispatchPhase;

    /**
     * The mode which determines how this message is dispatched.
     */
    public EnumSet<MessageDispatchMode> dispatchMode;

    /**
     * Gets the unique id for this message.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the {@link ChartNode} instance which generated this message.
     */
    public ChartNode getSender() {
        return this.sender;
    }

    /**
     * The {@link ChartNode} instance that previously received the message.
     */
    public ChartNode previousReceiver;

    /**
     * The raw data associated with the message.
     */
    public Object data;

    /**
     * Registers a new {@link ChartMessage}.
     */
    public static int register() {
        return counter++;
    }
}

