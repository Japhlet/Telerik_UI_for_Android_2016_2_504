package com.telerik.widget.dataform.engine;

import com.telerik.widget.dataform.visualization.annotations.DataFormProperty;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class EntityPropertyCore implements EntityProperty, PropertyChangedListener {
    private String propertyName;
    private Entity ownerEntity;
    private Class propertyType;
    private PropertyConverter converter = new EmptyConverter();
    private PropertyValidator validator = new EmptyValidator();
    private Object valueCandidate;
    private boolean validationPending = false;
    private boolean readOnly;
    private ArrayList<ValidationCompletedListener> validationListeners = new ArrayList<>();
    private ArrayList<EntityPropertyCommitListener> commitListeners = new ArrayList<>();
    private ArrayList<EntityPropertyChangedListener> changedListeners = new ArrayList<>();
    private boolean skip;
    private String header;
    private int columnPosition;
    private int columnSpan = 1;
    private int position = -1;
    private Object[] enumConstants;
    private String groupName = "DefaultGroup";
    private String hintText = "";
    private boolean required;
    private Class<? extends EntityPropertyEditor> editor = EntityPropertyEditor.class;
    private HashMap<String, Object> editorParams = new HashMap<>();
    private Class<? extends EntityPropertyViewer> viewer = EntityPropertyViewer.class;
    private int editorLayoutId;
    private Object customMetadata;
    private int coreEditorLayout;
    private int headerLayout;
    private int validationLayout;
    private ValidationCompletedListener tempListener;


    /**
     * Creates an instance of the {@link EntityProperty} class with the provided arguments.
     *
     * @param name  the name of the property.
     * @param type  the type of the property
     * @param owner the owner {@link Entity} to which the property belongs.
     */
    public EntityPropertyCore(String name, Class type, Entity owner) {
        this.propertyName = name;
        this.propertyType = type;
        this.ownerEntity = owner;
    }

    @Override
    public Object[] getEnumConstants() {
        return enumConstants;
    }

    @Override
    public boolean isTypePrimitive() {
        return type().isPrimitive();
    }

    public void readMetadata(EntityPropertyMetadata metadata) {
        if(metadata == null) {
            return;
        }
        try {
            this.validator = metadata.getValidator();
            HashMap<String,Object> validatorParams = metadata.getValidatorParams();
            if(validatorParams != null && validatorParams.size() > 0 && validator instanceof PropertyValidatorBase) {
                ((PropertyValidatorBase)validator).applyParams(validatorParams);
            }
            this.converter = metadata.getConverter();
        } catch (Exception ex) {
            throw new Error(ex);
        }

        this.skip = metadata.getSkip();
        if(metadata.getHeader() == null && this.header == null) {
            this.header = headerFormat(propertyName);
        } else if(metadata.getHeader() != null) {
            this.header = metadata.getHeader();
        }
        this.position = metadata.getPosition();
        this.columnPosition = metadata.getColumnPosition();
        this.groupName = metadata.getGroupName();
        this.hintText = metadata.getHintText();
        this.required = metadata.getRequired();
        this.readOnly = metadata.getReadOnly();
        this.editor = metadata.getEditorType();
        this.editorParams = metadata.getEditorParams();
        this.viewer = metadata.getViewerType();
        this.columnSpan = metadata.getColumnSpan();
        this.editorLayoutId = metadata.getEditorLayoutId();
        ArrayList<Object> values = metadata.getValues();
        if(values != null && values.size() > 0) {
            this.enumConstants = values.toArray();
        }

        this.coreEditorLayout = metadata.getCoreEditorLayoutId();
        this.headerLayout = metadata.getHeaderLayoutId();
        this.validationLayout = metadata.getValidationLayoutId();
    }

    protected String headerFormat(String name) {
        return name.replaceAll("(.)([A-Z0-9])", "$1 $2");
    }

    protected void readMetadata(String name, Entity owner) {
    }

    protected DataFormProperty findMetadata(Method getter) {
        for(Annotation annotation : getter.getAnnotations()) {
            if(annotation instanceof DataFormProperty) {
                return (DataFormProperty)annotation;
            }
        }

        return null;
    }

    protected Method findMethod(String prefix, String name, Class ownerType) {
        Method[] methods = ownerType.getMethods();
        for(Method method : methods) {
            if(method.getName().equals(prefix + name)) {
                return method;
            }
        }

        return null;
    }

    public void setCustomMetadata(Object value) {
        this.customMetadata = value;
    }

    public Object getCustomMetadata() {
        return this.customMetadata;
    }

    public void setReadOnly(boolean value) {
        this.readOnly = value;
    }

    public boolean getReadOnly() {
        return this.readOnly;
    }

    public int getEditorLayoutId() {
        return editorLayoutId;
    }

    public void setEditorLayoutId(int value) {
        this.editorLayoutId = value;
    }

    @Override
    public int getCoreEditorLayoutId() {
        return coreEditorLayout;
    }

    @Override
    public void setCoreEditorLayoutId(int value) {
        coreEditorLayout = value;
    }

    @Override
    public int getHeaderLayoutId() {
        return headerLayout;
    }

    @Override
    public void setHeaderLayoutId(int value) {
        headerLayout = value;
    }

    @Override
    public int getValidationLayoutId() {
        return validationLayout;
    }

    @Override
    public void setValidationLayoutId(int value) {
        validationLayout = value;
    }

    @SuppressWarnings("unused")
    public Class<? extends EntityPropertyViewer> getViewerType() {
        return viewer;
    }

    @SuppressWarnings("unused")
    public void setViewerType(Class<EntityPropertyViewer> value) {
        this.viewer = value;
    }

    @SuppressWarnings("unused")
    public Class<? extends EntityPropertyEditor> getEditorType() {
        return this.editor;
    }

    @SuppressWarnings("unused")
    public void setEditorType(Class<EntityPropertyEditor> value) {
        this.editor = value;
    }

    @Override
    public HashMap<String, Object> getEditorParams() {
        return editorParams;
    }

    @Override
    public void setEditorParams(HashMap<String, Object> editorParams) {
        this.editorParams = editorParams;
    }

    public boolean getSkip() {
        return skip;
    }

    @SuppressWarnings("unused")
    public void setSkip(boolean value) {
        skip = value;
    }

    public void setColumnPosition(int value) {
        columnPosition = value;
    }

    public int getColumnPosition() {
        return columnPosition;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int value) {
        columnSpan = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int value) {
        position = value;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String value) {
        header = value;
    }

    @SuppressWarnings("unused")
    public String getGroupName() {
        return groupName;
    }

    @SuppressWarnings("unused")
    public void setGroupName(String value) {
        groupName = value;
    }

    public String getHintText() {
        return this.hintText;
    }

    public void setHintText(String value) {
        this.hintText = value;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean value) {
        required = value;
    }

    /**
     * Use to add an implementation of the {@link EntityPropertyChangedListener} interface to the
     * collection of objects that listen for changes of the value of this property.
     *
     * @param listener the event listener.
     */
    public void addOnChangedListener(EntityPropertyChangedListener listener) {
        if (this.changedListeners.contains(listener)) {
            return;
        }
        this.changedListeners.add(listener);
    }

    /**
     * Use to remove the provided implementation of the {@link EntityPropertyChangedListener} interface
     * from the collection of objects that listen for changes of the value of this property.
     *
     * @param listener the event listener to remove.
     */
    @Override
    public void removeOnChangedListener(EntityPropertyChangedListener listener) {
        if (!this.changedListeners.contains(listener)) {
            return;
        }
        this.changedListeners.remove(listener);
    }

    @Override
    public void addValidationCompletedListener(ValidationCompletedListener listener) {
        validationListeners.add(listener);
    }

    @Override
    public void removeValidationCompletedListener(ValidationCompletedListener listener) {
        validationListeners.remove(listener);
    }

    @Override
     public void addCommitListener(EntityPropertyCommitListener listener) {
        this.commitListeners.add(listener);
    }

    @Override
    public void removeCommitListener(EntityPropertyCommitListener listener) {
        this.commitListeners.remove(listener);
    }

    protected boolean notifyCommitListenersBefore() {
        boolean result = false;
        for(EntityPropertyCommitListener listener : this.commitListeners) {
            if(listener.onBeforeCommit(this)) {
                result = true;
            }
        }
        if(!result) {
            result = getOwner().notifyCommitListenersBefore(this);
        }

        return result;
    }

    protected void notifyCommitListenersAfter() {
        for(EntityPropertyCommitListener listener : this.commitListeners) {
            listener.onAfterCommit(this);
        }
        getOwner().notifyCommitListenersAfter(this);
    }

    protected void notifyChangedListeners() {
        for (EntityPropertyChangedListener listener : this.changedListeners) {
            listener.onChanged(this);
        }
    }

    @Override
    public void tryCommit(Object value) {
        tempListener = new ValidationCompletedListener() {
            @Override
            public void validationCompleted(ValidationInfo info) {
                EntityPropertyCore.this.removeValidationCompletedListener(tempListener);
                tempListener = null;

                if(info.isValid()) {
                    EntityPropertyCore.this.commit();
                }
            }
        };

        this.addValidationCompletedListener(tempListener);
        this.setValueCandidate(value);
    }

    /**
     * Gets the type of the property.
     *
     * @return an instance of the {@link Class} class representing the type.
     */
    @Override
    public Class type() {
        return this.propertyType;
    }

    /**
     * Returns the name of the property.
     *
     * @return a string representing the property name.
     */
    @Override
    public String name() {
        return this.propertyName;
    }

    /**
     * Gets the current value candidate that, in case of successful validation, will be persisted
     * on the source object's property.
     *
     * @return the value candidate.
     */
    @SuppressWarnings("unused")
    @Override
    public Object getValueCandidate() {
        return this.valueCandidate;
    }

    /**
     * Sets a value that, in case of successful validation, will be persisted on the source object's
     * property when {@code EntityProperty.commit()} is called.
     *
     * @param value the value candidate to set.
     */
    @Override
    public void setValueCandidate(Object value) {

        if (getReadOnly()) {
            throw new RuntimeException("Property cannot be written to.");
        }

        this.validate(value);
    }

    @Override
    public Entity getOwner() {
        return ownerEntity;
    }

    /**
     * Gets the value currently persisted on the source object.
     *
     * @return the value;
     */
    @Override
    public Object getValue() {
        Object value = this.ownerEntity.getProperty(this);

        if (converter != null) {
            return converter.convertFrom(value);
        }

        return value;
    }

    /**
     * Persists the validated value candidate on the source object's property. If there's not a valid
     * value candidate an exception will be thrown.
     */
    @Override
    public void commit() {

        if (this.validationPending) {
            throw new IllegalStateException("Validator must complete before committing.");
        }

        if (this.converter != null) {
            this.valueCandidate = converter.convertTo(this.valueCandidate);
        }

        if(this.notifyCommitListenersBefore()) {
            this.valueCandidate = null;
            return;
        }

        this.ownerEntity.setProperty(this, this.valueCandidate);

        this.notifyCommitListenersAfter();
        this.valueCandidate = null;
    }

    @SuppressWarnings("unused")
    @Override
    public void setConverter(PropertyConverter converter) {
        this.converter = converter;
    }

    @Override
    public PropertyConverter getConverter() {
        return this.converter;
    }

    @SuppressWarnings("unused")
    @Override
    public void setValidator(PropertyValidator validator) {
        this.validator = validator;
    }

    @Override
    public PropertyValidator getValidator() {
        return this.validator;
    }

    @Override
    public void validate(Object value) {
        if(validationPending) {
            return;
        }

        this.validationPending = true;

        PropertyValidator validator;
        if(this.validator == null) {
            validator = new EmptyValidator();
        } else {
            validator = this.validator;
        }

        validator.validate(value, this.propertyName, new ValidationCompletedListener() {
            @Override
            public void validationCompleted(ValidationInfo info) {
                validationPending = false;
                if (info.isValid()) {
                    valueCandidate = info.editorValue();
                }

                notifyValidationListeners(info);
            }
        });
    }

    protected void notifyValidationListeners(ValidationInfo info) {
        for(ValidationCompletedListener listener : validationListeners) {
            listener.validationCompleted(info);
        }
    }

    @Override
    public void onPropertyChanged(String propertyName, Object value) {
        if(propertyName.equals(this.name())) {
            this.notifyChangedListeners();
        }
    }
}
