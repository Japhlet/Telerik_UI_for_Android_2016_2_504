package com.telerik.widget.chart.engine.elementTree;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.chartAreas.LoadContext;
import com.telerik.widget.chart.engine.view.ChartElementPresenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Base class for nodes that may have children.
 */
public abstract class ChartElement extends ChartNode {

    public NodeCollection children;
    ChartElementPresenter presenter;

    /**
     * Initializes a new instance of the {@link ChartElement} class.
     */
    protected ChartElement() {
        this.children = new NodeCollection(this);
        this.presenter = null;
    }

    @Override
    public ChartElementPresenter getPresenter() {
        if (this.presenter != null) {
            return this.presenter;
        }

        if (this.parent != null) {
            return this.parent.getPresenter();
        }

        return null;
    }

    /**
     * Assigns the given {@link ChartElementPresenter} to this element.
     *
     * @param presenter The presenter.
     */
    public void setPresenter(ChartElementPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Searches up the parent chain and returns the first parent of type T.
     *
     * @param <T> The type of parent to search for.
     * @return The first parent of type T.
     */
    public <T extends ChartElement> T findAncestor(Class<T> clazz) {
        ChartElement currParent = this.parent;
        while (currParent != null) {

            if (currParent.getClass().isAssignableFrom(clazz)) {
                return (T) currParent;
            }

            currParent = currParent.parent;
        }

        return null;
    }

    /**
     * Gets a boolean value that determines whether a given element
     * resides in the element hierarchy of this element.
     *
     * @param node The node to check.
     */
    public boolean isAncestorOf(ChartNode node) {
        ChartElement parent = node.parent;

        while (parent != null) {
            if (parent == this) {
                return true;
            }

            parent = parent.parent;
        }

        return false;
    }

    /* TODO c# /// <summary>
    /// Searches down the subtree of elements, using breadth-first approach, and returns the first descendant of type T.
    /// </summary>
    /// <typeparam name="T">Must be a <see cref="ChartNode"/>.</typeparam>

    public <T extends ChartNode> T findDescendant()
    {
        return this.findDescendant(node => node is T)as T;
    }
*/

    /**
     * Searches down the subtree of elements, using breadth-first approach, and returns the first descendant of type T that matches the provided criteria.
     *
     * @param criteria The criteria that determines which descendant to return.
     */
    public ChartNode findDescendant(Predicate<ChartNode> criteria) {
        if (criteria == null) {
            return null;
        }

        for (ChartNode node : this.enumDescendants(TreeTraversalMode.BREADTH_FIRST)) {
            if (criteria.apply(node)) {
                return node;
            }
        }

        return null;
    }

    /**
     * When iterated over returns all descendants in {@link TreeTraversalMode#DEPTH_FIRST} order.
     */
    public Iterable<ChartNode> enumDescendants() {
        return this.enumDescendants(null, TreeTraversalMode.DEPTH_FIRST);
    }

    /**
     * When iterated over returns all descendants in the provided {@link TreeTraversalMode} order.
     *
     * @param traverseMode The tree traversal mode.
     */
    public Iterable<ChartNode> enumDescendants(TreeTraversalMode traverseMode) {
        return this.enumDescendants(null, traverseMode);
    }

    /**
     * When iterated over in the given {@link TreeTraversalMode}, returns children that match the provided predicate.
     *
     * @param predicate    The predicate to apply to children nodes.
     * @param traverseMode The tree traversal mode.
     */
    public Iterable<ChartNode> enumDescendants(Predicate<ChartNode> predicate, TreeTraversalMode traverseMode) {
        ArrayList<ChartNode> descendants = new ArrayList<ChartNode>();
        switch (traverseMode) {
            case BREADTH_FIRST:
                Queue<ChartElement> children = new LinkedList<ChartElement>();
                children.offer(this);

                while (children.size() > 0) {
                    ChartElement childElement = children.poll();
                    for (ChartNode nestedChild : childElement.children) {
                        if (predicate == null) {
                            descendants.add(nestedChild);
                        } else if (predicate.apply(nestedChild)) {
                            descendants.add(nestedChild);
                        }

                        ChartElement nestedChildElement = (ChartElement) nestedChild;
                        if (nestedChildElement != null) {
                            children.offer(nestedChildElement);
                        }
                    }
                }
                break;
            default:
                for (ChartNode child : this.children) {
                    if (predicate == null) {
                        descendants.add(child);
                    } else if (predicate.apply(child)) {
                        descendants.add(child);
                    }

                    ChartElement childElement = (ChartElement) child;
                    if (childElement != null) {
                        childElement.enumDescendants(predicate, traverseMode);
                    }
                }
                break;
        }
        return descendants;
    }

    public interface Predicate<T> {

        boolean apply(T t);
    }

    /**
     * Called when a child has been inserted into the children of this node.
     *
     * @param index The index of the new child.
     * @param child The new child.
     */
    protected void onChildInserted(int index, ChartNode child) {
        child.setParent(this);

        if (this.nodeState == NodeState.LOADING || this.nodeState == NodeState.LOADED) {
            assert this.loadContext != null; //, "Must have valid LoadContext if state is LOADING or LOADED.");
            child.load(this.loadContext);
        }
    }

    /**
     * Called when a child is removed.
     *
     * @param index The index of the removed child.
     * @param child The removed child.
     */
    protected void onChildRemoved(int index, ChartNode child) {
        child.setParent(null);
        child.unload();
    }

    /**
     * Gets a {@link ModifyChildrenResult} value that determines if a child can be added to this element.
     *
     * @param child The child to be considered.
     */
    public ModifyChildrenResult canAddChild(ChartNode child) {
        return ModifyChildrenResult.REFUSE;
    }

    /**
     * Gets a {@link ModifyChildrenResult} value that determines if a child can be removed from this element.
     *
     * @param child The child to be considered.
     */
    protected ModifyChildrenResult canRemoveChild(ChartNode child) {
        return ModifyChildrenResult.ACCEPT;
    }

    @Override
    protected void loadCore(LoadContext context) {
        super.loadCore(context);

        for (ChartNode node : this.children) {
            node.load(context);
            node.setParent(this);
        }
    }

    @Override
    protected void unloadCore() {
        for (ChartNode child : this.children) {
            child.unload();
        }

        super.unloadCore();

        this.presenter = null;
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        for (ChartNode child : this.children) {
            child.arrange(rect);
        }

        return rect;
    }
}

