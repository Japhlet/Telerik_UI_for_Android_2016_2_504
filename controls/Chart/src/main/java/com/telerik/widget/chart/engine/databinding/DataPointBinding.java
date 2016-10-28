package com.telerik.widget.chart.engine.databinding;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

/**
 * Encapsulates the base functionality for establishing a {@link DataPoint} binding.
 *
 * @see DataPoint
 */
public abstract class DataPointBinding {
    /**
     * Gets the value of the member which value is bound.
     *
     * @param instance holds the instance of the object that has its member bound.
     * @return The value of the bound member.
     * @throws IllegalArgumentException if the name is not properly set or if there is no such
     *                                  member in the passed instance Object.
     */
    public abstract Object getValue(Object instance) throws IllegalArgumentException;

    // TODO
    /// <summary>
    /// Raises the <see cref="PropertyChanged"/> event.
    /// </summary>
    /// <param name="name">The name of the property which value has changed.</param>
    protected void onPropertyChanged(String propertyName) {
//        PropertyChangedEventHandler eh = this.PropertyChanged;
//        if (eh != null)
//        {
//            eh(this, new PropertyChangedEventArgs(name));
//        }
    }
}
