package com.telerik.widget.dataform.visualization.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telerik.android.common.Util;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.engine.EntityPropertyChangedListener;
import com.telerik.widget.dataform.engine.EntityPropertyEditorChangeListener;
import com.telerik.widget.dataform.engine.ValidationCompletedListener;
import com.telerik.widget.dataform.engine.ValidationInfo;
import com.telerik.widget.dataform.visualization.DataFormValidationViewBehavior;
import com.telerik.widget.dataform.visualization.RadDataForm;

import java.util.ArrayList;

/**
 * Base class for data form editors.
 */
public abstract class EntityPropertyEditor extends EntityPropertyViewer implements ValidationCompletedListener {
    private ArrayList<OnValidationEventListener> validationListeners = new ArrayList<>();
    private ValidationInfo lastValidationInfo;
    private ArrayList<EntityPropertyEditorChangeListener> changeListeners = new ArrayList<>();
    private CommitMode commitMode;
    private ValidationMode validationMode;
    private DataFormValidationViewBehavior validationViewBehavior;
    private int validationLayoutId;
    private ViewGroup validationContainer;
    private View validationView;

    private EntityPropertyChangedListener propertyChangedListener = new EntityPropertyChangedListener() {
        @Override
        public void onChanged(EntityProperty property) {
            applyEntityValueToEditor(property.getValue());
        }
    };

    public EntityPropertyEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerViewId, editorLayoutId, editorViewId, property);

        if(property.getValidationLayoutId() != 0) {
            validationLayoutId = property.getValidationLayoutId();
        }

        this.validationLayoutId = validationLayoutId;
        this.validationContainer = Util.getLayoutPart(this.rootLayout, R.id.data_form_validation_container, ViewGroup.class);
        this.validationView = LayoutInflater.from(dataForm.getContext()).inflate(validationLayoutId, dataForm, false);
        this.validationContainer.addView(this.validationView);

        this.setValidationViewBehavior(new DataFormValidationViewBehavior(dataForm.getContext()));
    }

    public int getValidationLayoutId() {
        return this.validationLayoutId;
    }

    public void setValidationLayoutId(int value) {
        this.validationLayoutId = value;

        this.resetValidationLayout(value);
    }

    protected void resetValidationLayout(int layoutId) {
        this.validationContainer.removeView(this.validationView);
        this.validationView = LayoutInflater.from(this.dataForm.getContext()).inflate(layoutId, this.dataForm, false);
        this.validationContainer.addView(this.validationView);
        this.validationViewBehavior.reset();
    }

    public void setValidationViewBehavior(DataFormValidationViewBehavior value) {
        if(this.validationViewBehavior != null) {
            this.validationViewBehavior.setEditor(null);
        }

        this.validationViewBehavior = value;

        if(value != null) {
            value.setEditor(this);
        }
    }

    @SuppressWarnings("unused")
    public DataFormValidationViewBehavior getValidationViewBehavior() {
        return this.validationViewBehavior;
    }

    /**
     * Adds an editor change listener.
     */
    public void addEditorChangeListener(EntityPropertyEditorChangeListener listener) {
        this.changeListeners.add(listener);
    }

    /**
     * Removes an editor change listener.
     */
    public void removeEditorChangeListener(EntityPropertyEditorChangeListener listener) {
        this.changeListeners.remove(listener);
    }

    /**
     * Sets the commit mode of this editor.
     */
    public void setCommitMode(CommitMode value) {
        commitMode = value;
    }

    /**
     * Gets the commit mode of this editor.
     */
    public CommitMode getCommitMode() {
        return commitMode;
    }

    @SuppressWarnings("unused")
    public ValidationMode getValidationMode() {
        return this.validationMode;
    }

    public void setValidationMode(ValidationMode value) {
        this.validationMode = value;
    }

    /**
     * Sets a validation listener.
     */
    public void addValidationListener(OnValidationEventListener listener) {
        this.validationListeners.add(listener);
    }

    public void removeValidationListener(OnValidationEventListener listener) {
        this.validationListeners.remove(listener);
    }

    /**
     * Gets the validation info produced by the last validation attempt.
     */
    public ValidationInfo validationInfo() {
        return this.lastValidationInfo;
    }

    public void load() {
        final EntityProperty property = this.property();

        try {
            this.applyEntityValueToEditor(property.getValue());
            property.addOnChangedListener(this.propertyChangedListener);
            property.addValidationCompletedListener(this);
        } catch (ClassCastException ex) {
            throw new Error(String.format("%s does not support properties of type %s. Please specify a value converter for your property.",
                    this.getClass().getSimpleName(),
                    property.type().getSimpleName()),
                    ex);
        }
    }

    public void unload() {
        EntityProperty property = this.property();
        property.removeOnChangedListener(this.propertyChangedListener);
        property.removeValidationCompletedListener(this);
    }

    @Override
    public void validationCompleted(ValidationInfo info) {
        lastValidationInfo = info;

        notifyValidationListeners(info);

        if (info.isValid() && getCommitMode() != CommitMode.MANUAL) {
            property().commit();
        }

        notifyChangeListeners();
    }

    private void notifyValidationListeners(ValidationInfo info) {
        for(OnValidationEventListener validationListener : this.validationListeners) {
            validationListener.onValidationEvent(EntityPropertyEditor.this, info);
        }
    }

    public void validate() {
        this.property().validate(this.value());
    }

    /**
     * Gets the current editor value and tries to set it on the target object property. In case
     * of successful validation, the value will be set on the target object.
     */
    public void tryApplyValueToProperty() {
        this.property().setValueCandidate(this.value());
    }

    /**
     * Gets the value currently produced by the editor.
     */
    public abstract Object value();

    protected boolean canEditorFocus(){
        return false;
    }

    protected void onEditorLostFocus() {
        if (this.getCommitMode() == CommitMode.ON_LOST_FOCUS) {
            this.property().setValueCandidate(this.value());
        } else if (this.validationMode == ValidationMode.ON_LOST_FOCUS) {
            this.property().validate(this.value());
        }
    }

    protected void onEditorValueChanged(Object value) {
        if (this.getCommitMode() == CommitMode.IMMEDIATE ||
                (this.getCommitMode() == CommitMode.ON_LOST_FOCUS && !this.canEditorFocus())) {
            this.property().setValueCandidate(value);
        } else if(validationMode == ValidationMode.IMMEDIATE) {
            this.property().validate(value);
        }
    }

    protected void notifyChangeListeners() {
        for(EntityPropertyEditorChangeListener listener : this.changeListeners) {
            listener.onEditorChanged(this);
        }
    }

    protected abstract void applyEntityValueToEditor(Object entityValue);

    public interface OnValidationEventListener {
        void onValidationEvent(EntityPropertyEditor editor, ValidationInfo info);
    }
}
