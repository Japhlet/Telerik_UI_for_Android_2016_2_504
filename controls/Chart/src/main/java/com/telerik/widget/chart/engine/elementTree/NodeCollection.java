package com.telerik.widget.chart.engine.elementTree;

import java.util.ArrayList;

/**
 * Represents a collection of {@link ChartNode} instances.
 */
public class NodeCollection extends ArrayList<ChartNode> {

    private ChartElement owner;
    private boolean suspendIndexShift = false;

    /**
     * Creates an instance of the {@link NodeCollection} class with a specified owner.
     *
     * @param owner an instance of the {@link ChartElement} class representing the owner.
     */
    protected NodeCollection(ChartElement owner) {
        this.owner = owner;
    }

    /**
     * Gets the current owner of this {@link com.telerik.widget.chart.engine.elementTree.NodeCollection}.
     *
     * @return the {@link ChartElement} instance representing the owner of the collection.
     */
    public ChartElement getOwner() {
        return this.owner;
    }

    @Override
    public boolean add(ChartNode node) {
        if (node == null) {
            throw new IllegalArgumentException("cannot add a null node");
        }

        this.insertCore(super.size(), node);
        return true;
    }

    /**
     * Removes the provided {@link ChartNode} instance from the collection.
     *
     * @param node the node to remove.
     */
    public void remove(ChartNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }

        int index = this.indexOf(node);

        if (index == -1) {
            throw new IllegalArgumentException("The provided node does not exist in this collection.");
        }

        this.removeCore(index);
    }

    @Override
    public ChartNode remove(int index) {
        if (index < 0 || index >= super.size()) {
            throw new ArrayIndexOutOfBoundsException("index");
        }

        return this.removeCore(index);
    }

    @Override
    public void clear() {
        super.clear();
        this.suspendIndexShift = true;

        int count = super.size();
        for (int i = count - 1; i >= 0; i--) {
            this.removeCore(i);
        }

        this.suspendIndexShift = false;
    }

    private int indexOf(ChartNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }

        if (node.parent == this.owner) {
            return node.index;
        }

        return -1;
    }

    private void insertCore(int index, ChartNode node) {
        if (!this.verifyAddChild(node)) {
            return;
        }

        super.add(index, node);
        node.index = index;

        this.shiftNodesIndexes(index, 1);

        this.owner.onChildInserted(index, node);
    }

    private ChartNode removeCore(int index) {
        ChartNode node = super.get(index);

        if (!this.verifyRemoveChild(node)) {
            return null;
        }

        super.remove(index);
        node.index = -1;
        node.collectionIndex = -1;
        this.shiftNodesIndexes(index - 1, -1);
        this.owner.onChildRemoved(index, node);

        return node;
    }

    private boolean verifyAddChild(ChartNode node) {
        ModifyChildrenResult result = this.owner.canAddChild(node);
        if (result == ModifyChildrenResult.CANCEL) {
            return false;
        }

        if (result == ModifyChildrenResult.REFUSE) {
            throw new IllegalArgumentException("Specified node is not accepted by the element");
        }

        if (node.parent != null) {
            throw new IllegalStateException("ChildNode is already parented by a ChartElement instance.");
        }

        return true;
    }

    private boolean verifyRemoveChild(ChartNode node) {
        ModifyChildrenResult result = this.owner.canRemoveChild(node);

        if (result == ModifyChildrenResult.CANCEL) {
            return false;
        }

        if (result == ModifyChildrenResult.REFUSE) {
            throw new IllegalArgumentException("Specified node may not be removed from the element");
        }

        return true;
    }

    private void shiftNodesIndexes(int index, int offset) {
        if (this.suspendIndexShift) {
            return;
        }

        int count = super.size();

        for (int i = index + 1; i < count; i++) {
            super.get(i).index += offset;
        }
    }
}
