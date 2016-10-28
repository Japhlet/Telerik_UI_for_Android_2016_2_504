package com.telerik.widget.dataform.engine;

import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

import java.util.HashMap;

/**
 * This class represents a combination of a get and a set methods which together define a property.
 */
public interface EntityProperty {
    @SuppressWarnings("unused")
    void setCustomMetadata(Object value);
    @SuppressWarnings("unused")
    Object getCustomMetadata();

    @SuppressWarnings("unused")
    void setReadOnly(boolean value);

    @SuppressWarnings("unused")
    boolean getReadOnly();

    int getEditorLayoutId();
    @SuppressWarnings("unused")
    void setEditorLayoutId(int value);

    int getCoreEditorLayoutId();
    void setCoreEditorLayoutId(int value);

    int getHeaderLayoutId();
    void setHeaderLayoutId(int value);

    int getValidationLayoutId();
    void setValidationLayoutId(int value);

    Object[] getEnumConstants();
    boolean isTypePrimitive();

    void readMetadata(EntityPropertyMetadata metadata);

    @SuppressWarnings("unused")
    Class<? extends EntityPropertyViewer> getViewerType();

    @SuppressWarnings("unused")
    void setViewerType(Class<EntityPropertyViewer> value);

    @SuppressWarnings("unused")
    Class<? extends EntityPropertyEditor> getEditorType();

    @SuppressWarnings("unused")
    void setEditorType(Class<EntityPropertyEditor> value);

    HashMap<String, Object> getEditorParams();

    void setEditorParams(HashMap<String, Object> params);

    boolean getSkip();

    @SuppressWarnings("unused")
    void setSkip(boolean value);

    @SuppressWarnings("unused")
    void setColumnPosition(int value);

    int getColumnPosition();

    int getColumnSpan();

    @SuppressWarnings("unused")
    void setColumnSpan(int value);

    int getPosition();

    void setPosition(int value);

    String getHeader();

    void setHeader(String value);

    @SuppressWarnings("unused")
    String getGroupName();

    @SuppressWarnings("unused")
    void setGroupName(String value);

    String getHintText();

    void setHintText(String value);

    boolean getRequired();

    void setRequired(boolean value);

    /**
     * Use to add an implementation of the {@link EntityPropertyChangedListener} interface to the
     * collection of objects that listen for changes of the value of this property.
     *
     * @param listener the event listener.
     */
    void addOnChangedListener(EntityPropertyChangedListener listener);

    /**
     * Use to remove the provided implementation of the {@link EntityPropertyChangedListener} interface
     * from the collection of objects that listen for changes of the value of this property.
     *
     * @param listener the event listener to remove.
     */
    void removeOnChangedListener(EntityPropertyChangedListener listener);

    void addValidationCompletedListener(ValidationCompletedListener listener);

    void removeValidationCompletedListener(ValidationCompletedListener listener);

    void addCommitListener(EntityPropertyCommitListener listener);

    void removeCommitListener(EntityPropertyCommitListener listener);

    /**
     * Gets the type of the property.
     *
     * @return an instance of the {@link Class} class representing the type.
     */
    Class type();

    /**
     * Returns the name of the property.
     *
     * @return a string representing the property name.
     */
    String name();

    /**
     * Gets the current value candidate that, in case of successful validation, will be persisted
     * on the source object's property.
     *
     * @return the value candidate.
     */
    @SuppressWarnings("unused")
    Object getValueCandidate();

    /**
     * Sets a value that, in case of successful validation, will be persisted on the source object's
     * property when {@code EntityProperty.commit()} is called.
     *
     * @param value the value candidate to set.
     */
    void setValueCandidate(Object value);

    Entity getOwner();

    /**
     * Gets the value currently persisted on the source object.
     *
     * @return the value;
     */
    Object getValue();

    /**
     * Persists the validated value candidate on the source object's property. If there's not a valid
     * value candidate an exception will be thrown.
     */
    void commit();

    /**
     * Tries to validate the provided value and if the validation succeeds, the value is committed.
     */
    void tryCommit(Object value);

    @SuppressWarnings("unused")
    void setConverter(PropertyConverter converter);

    @SuppressWarnings("unused")
    PropertyConverter getConverter();

    @SuppressWarnings("unused")
    void setValidator(PropertyValidator validator);

    @SuppressWarnings("unused")
    PropertyValidator getValidator();

    void validate(Object value);
}