package com.telerik.widget.chart.engine.elementTree;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * Represents a {@link ChartNode} and is a base class for nodes containing business data. This class is abstract and should not be directly used
 * in your application.
 */
public abstract class ContentNode extends ChartNode {

    static final int ContentPropertyKey = PropertyKeys.register(ContentNode.class, "CONTENT", ChartAreaInvalidateFlags.ALL);

    /**
     * Holds the desired size of the current {@link ContentNode}.
     */
    public RadSize desiredSize = RadSize.getEmpty();

    /**
     * Gets the provided business data associated with this {@link ContentNode}.
     *
     * @return the business data.
     */
    public Object getContent() {
        return this.getValue(ContentPropertyKey);
    }

    /**
     * Sets the provided business data to this {@link ContentNode}.
     *
     * @param value the business data.
     */
    public void setContent(Object value) {
        this.setValue(ContentPropertyKey, value);
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        if (e.getKey() == ContentPropertyKey) {
            this.desiredSize = RadSize.getEmpty();
        }

        super.onPropertyChanged(e);
    }
}

