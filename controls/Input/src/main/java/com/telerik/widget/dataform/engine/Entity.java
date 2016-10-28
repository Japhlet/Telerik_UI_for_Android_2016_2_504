package com.telerik.widget.dataform.engine;

/**
 * This class represent an arbitrary business object with its collection of getters and setters. This
 * class exposes methods for invoking the corresponding getters and setters thus allowing the modification
 * of the values of the associated object by the data form control.
 */
public interface Entity {
    /**
     * Sets the given property with the provided value.
     *
     * @param target an instance of {@link EntityProperty} class representing the property to set.
     * @param value  the value to set.
     */
    void setProperty(EntityProperty target, Object value);

    /**
     * Gets the value of the provided property.
     *
     * @param source an instance of the {@link EntityProperty} class representing the property to get.
     * @return the value of the property.
     */
    Object getProperty(EntityProperty source);
    /**
     * Returns the {@link EntityProperty} instances
     * representing the editable fields of the associated source object.
     *
     * @return an {@link Iterable} implementation containing the editable properties.
     */
    Iterable<EntityProperty> properties();

    Object getSourceObject();

    void addCommitListener(EntityPropertyCommitListener listener);

    void removeCommitListener(EntityPropertyCommitListener listener);

    boolean notifyCommitListenersBefore(EntityProperty property);

    void notifyCommitListenersAfter(EntityProperty property);
}
