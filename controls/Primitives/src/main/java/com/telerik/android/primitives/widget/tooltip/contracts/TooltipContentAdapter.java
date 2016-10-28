package com.telerik.android.primitives.widget.tooltip.contracts;

import android.view.View;

import com.telerik.android.common.Function;

/**
 * Tooltip content adapter providing the content view for a tooltip instance.
 */
public interface TooltipContentAdapter {

    /**
     * Gets the view according to the provided set of targets.
     *
     * @param targets set of targets.
     * @return content view.
     */
    View getView(Object[] targets);

    /**
     * Gets a value determining whether to apply the default styles or not.
     *
     * @return <code>true</code> will result in application of the default styles, <code>false</code> will prevent it.
     */
    boolean getIsApplyDefaultStyles();

    /**
     * Sets a value determining whether to apply the default styles or not.
     *
     * @param apply the determining value.&nbsp;<code>true</code> will result in application of the default styles, <code>false</code> will prevent it.
     */
    void setApplyDefaultStyles(boolean apply);

    /**
     * Gets the value to string converter, which will be used when translating a data point's data context. If not set, the default conversion will apply.
     *
     * @return the current value to string converter. If not set the converter will be <code>null</code>.
     */
    Function<Object, String> getValueToStringConverter();

    /**
     * Sets the value to string converter, which will be used when translating a data point's data context. If set to <code>null</code> the default conversion will apply.
     *
     * @param converter the new value to string converter.
     */
    void setValueToStringConverter(Function<Object, String> converter);

    /**
     * Gets the category to string converter, which will be used when translating a data point's data context. If not set, the default conversion will apply.
     *
     * @return the current category to string converter. If not set the converter will be <code>null</code>.
     */
    Function<Object, String> getCategoryToStringConverter();

    /**
     * Sets the category to string converter, which will be used when translating a data point's data context. If set to <code>null</code> the default conversion will apply.
     *
     * @param converter the new category to string converter.
     */
    void setCategoryToStringConverter(Function<Object, String> converter);
}
