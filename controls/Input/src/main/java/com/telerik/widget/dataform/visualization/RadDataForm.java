package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import com.telerik.android.common.Procedure;
import com.telerik.android.common.Procedure2;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.DataFormMetadata;
import com.telerik.widget.dataform.engine.Entity;
import com.telerik.widget.dataform.engine.EntityBase;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.engine.EntityPropertyCommitListener;
import com.telerik.widget.dataform.engine.EntityPropertyEditorDependency;
import com.telerik.widget.dataform.engine.EntityPropertyMetadata;
import com.telerik.widget.dataform.engine.JsonEntity;
import com.telerik.widget.dataform.engine.LabelPosition;
import com.telerik.widget.dataform.engine.ValidationInfo;
import com.telerik.widget.dataform.visualization.core.CommitMode;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;
import com.telerik.widget.dataform.visualization.core.ValidationMode;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * RadDataForm automatically generates UI for editing an object with public properties.
 * A property is a getter or a combination of getter and setter witch a specific signature.
 * For more information refer to the @see <a href="http://http://docs.telerik.com/devtools/android/controls/dataform/dataform-overview">online help</a>.
 */
public class RadDataForm extends ScrollView implements EntityPropertyEditor.OnValidationEventListener {
    private CommitMode commitMode = CommitMode.IMMEDIATE;
    private LabelPosition labelPosition = LabelPosition.TOP;
    private Entity entity;
    private boolean manualCommit = false;
    private boolean canScroll = true;

    private DataFormEntityAdapter adapter;
    private ArrayList<EntityPropertyEditor> pendingEditors = new ArrayList<>();
    private boolean isReadOnly;
    private ArrayList<EntityPropertyEditorDependency> dependencies = new ArrayList<>();
    private DataFormLayoutManager layoutManager;
    private ValidationMode validationMode;
    private boolean manualValidation;
    private int editorsMainLayout = R.layout.data_form_editor_layout_1;
    private int editorsHeaderLayout = R.layout.data_form_editor_header_layout_1;
    private int editorsValidationLayout = R.layout.data_form_editor_validation_layout_1;
    private ArrayList<Procedure<DataFormValidationInfo>> validationFinishedListeners = new ArrayList<>();
    private ArrayList<EntityPropertyCommitListener> commitListeners = new ArrayList<>();
    private ArrayList<ValidationInfo> validationInfos = new ArrayList<>();
    private Iterable<EntityPropertyViewer> editors;
    private DataFormMetadata metadata;
    private Procedure<EntityPropertyViewer> editorCustomizations;

    /**
     * Creates a new instance of RadDataForm.
     * @param context The app context.
     */
    public RadDataForm(Context context) {
        this(context, null, R.layout.data_form_root_layout);
    }

    /**
     * Creates a new instance of RadDataForm. Used by the XML run-time.
     * @param context The app context.
     * @param attrs The xml attributes.
     */
    public RadDataForm(Context context, AttributeSet attrs) {
        this(context, attrs, R.layout.data_form_root_layout);
    }

    /**
     * Creates an instance of the {@link RadDataForm} control with the provided arguments.
     *
     * @param context the application context.
     * @param attrs   a set of styleable attributes.
     * @param layoutId the XML id for the root layout.
     */
    public RadDataForm(Context context, AttributeSet attrs, int layoutId) {
        super(context, attrs);

        layoutManager = new DataFormTableLayoutManager(context, layoutId);
        this.adapter = new DataFormEntityAdapter(this);

        this.setFocusableInTouchMode(true);
        this.setClickable(true);
        this.setFocusable(true);
    }

    public void addCommitListener(EntityPropertyCommitListener listener) {
        this.commitListeners.add(listener);
        if(getEntity() != null) {
            getEntity().addCommitListener(listener);
        }
    }

    public void removeCommitListener(EntityPropertyCommitListener listener) {
        this.commitListeners.remove(listener);
        if(getEntity() != null) {
            getEntity().removeCommitListener(listener);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if(gainFocus) {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(LabelPosition labelPosition) {
        if(this.labelPosition == labelPosition) {
            return;
        }
        this.labelPosition = labelPosition;

        this.setEditorsMainLayout(labelPosition == LabelPosition.TOP ? R.layout.data_form_editor_layout_1 : R.layout.data_form_editor_layout_2);
        this.setEditorsHeaderLayout(labelPosition == LabelPosition.TOP ? R.layout.data_form_editor_header_layout_1 : R.layout.data_form_editor_header_layout_2);

        reload();
    }

    public void setMetadata(DataFormMetadata metadata) {
        if(this.metadata == metadata) {
            return;
        }
        this.metadata = metadata;

        setIsReadOnly(metadata.isReadOnly());
        setCommitMode(metadata.getCommitMode());
        setValidationMode(metadata.getValidationMode());

        reload();
    }

    public DataFormMetadata getMetadata() {
        return this.metadata;
    }

    @SuppressWarnings("unused")
    public void setEditorsMainLayout(int value) {
        if(this.editorsMainLayout == value) {
            return;
        }
        this.editorsMainLayout = value;
        reload();
    }

    @SuppressWarnings("unused")
    public int getEditorsMainLayout() {
        return editorsMainLayout;
    }

    public int getEditorsHeaderLayout() {
        return editorsHeaderLayout;
    }

    public void setEditorsHeaderLayout(int value) {
        if(this.editorsHeaderLayout == value) {
            return;
        }
        this.editorsHeaderLayout = value;
        reload();
    }

    public int getEditorsValidationLayout() {
        return editorsValidationLayout;
    }

    public void setEditorsValidationLayout(int value) {
        if(this.editorsValidationLayout == value) {
            return;
        }
        this.editorsValidationLayout = value;
        reload();
    }

    public Procedure<EntityPropertyViewer> getEditorCustomizations() {
        return editorCustomizations;
    }

    public void setEditorCustomizations(Procedure<EntityPropertyViewer> editorCustomizations) {
        if(this.editorCustomizations == editorCustomizations) {
            return;
        }
        this.editorCustomizations = editorCustomizations;
        reload();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LicensingProvider.verify(this.getContext());
    }

    /**
     * Gets the data form layout manager.
     */
    @SuppressWarnings("unused")
    public DataFormLayoutManager getLayoutManager() {
        return this.layoutManager;
    }

    /**
     * Sets the data form layout manager.
     */
    public void setLayoutManager(DataFormLayoutManager value) {
        if(this.layoutManager == value) {
            return;
        }

        this.layoutManager = value;

        if(value == null) {
            this.layoutManager = new DataFormTableLayoutManager(getContext(), R.layout.data_form_root_layout);
        }

        this.reload();
    }

    /**
     * Gets the actual edited object.
     */
    public Object getEditedObject() {
        if(entity == null) {
            return null;
        }

        return this.entity.getSourceObject();
    }

    /**
     * Gets the data form entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets the data form entity.
     */
    public void setEntity(Object value) {
        if(value == null) {
            return;
        }

        detachAllListeners(this.entity);

        if(value instanceof JSONObject) {
            this.setEntity(new JsonEntity((JSONObject)value));
        } else {
            this.setEntity(new EntityBase(value));
        }

        attachAllListeners(this.entity);
    }

    private void detachAllListeners(Entity entity) {
        if(entity == null) {
            return;
        }
        for(EntityPropertyCommitListener listener : commitListeners) {
            entity.removeCommitListener(listener);
        }
    }

    private void attachAllListeners(Entity entity) {
        if(entity == null) {
            return;
        }
        for(EntityPropertyCommitListener listener : commitListeners) {
            entity.addCommitListener(listener);
        }
    }

    /**
     * Sets the data form entity.
     */
    public void setEntity(Entity value) {
        this.entity = value;

        if(this.adapter != null) {
            reload();
        }

        if(this.entity != null && !this.isReadOnly) {
            for(EntityPropertyEditorDependency dependency : dependencies) {
                dependency.load();
                dependency.update();
            }
        }
    }

    /**
     * Adds an editor dependency for a given property.
     * When one of the dependencies change, the callback argument is called so that editor corresponding to the property can be updated.
     * @param propertyName The property which should be updated when a dependency changes.
     * @param onDependencyChangedCallback The callback which defines the dependency.
     * @param dependencies The dependencies on which the property argument depends.
     */
    @SuppressWarnings("unused")
    public void addEditorDependency(String propertyName, Procedure2<RadDataForm, EntityPropertyEditor> onDependencyChangedCallback, String... dependencies) {
        EntityPropertyEditorDependency dependency = new EntityPropertyEditorDependency(this, propertyName, onDependencyChangedCallback, dependencies);
        this.dependencies.add(dependency);

        if(isReadOnly) {
            return;
        }

        if(this.entity != null) {
            dependency.load();
            dependency.update();
        }
    }

    /**
     * Removes the dependency for the given editor.
     */
    @SuppressWarnings("unused")
    public void removeEditorDependency(String dependentEditorName) {
        EntityPropertyEditorDependency dependencyToRemove = null;
        for(EntityPropertyEditorDependency dependency : dependencies) {
            if(dependency.editorName().equals(dependentEditorName)) {
                dependencyToRemove = dependency;
                break;
            }
        }

        if(dependencyToRemove == null) {
            return;
        }

        dependencies.remove(dependencyToRemove);
        dependencyToRemove.unload();
    }

    /**
     * Clears all editor dependencies.
     */
    @SuppressWarnings("unused")
    public void clearDependencies() {
        for(int i = dependencies.size() - 1; i > -1; --i) {
            EntityPropertyEditorDependency dependency = dependencies.get(i);
            dependency.unload();
            dependencies.remove(i);
        }
    }

    /**
     * Gets an existing editor for the given property.
     * @param propertyName The property for which to find an existing editor.
     */
    public EntityPropertyViewer getExistingEditorForProperty(String propertyName) {
        if(this.editors == null) {
            return null;
        }

        for(EntityPropertyViewer viewer : this.editors) {
            if(viewer.property().name().equals(propertyName)) {
                return viewer;
            }
        }

        return null;
    }

    /**
     * Removes all editors recreates them and adds them again.
     * Reload is called when a new entity or a new adapter is set.
     */
    public void reload() {
        unload();

        if(this.entity == null || this.adapter == null) {
            return;
        }

        if(metadata != null) {
            for (EntityProperty property : entity.properties()) {
                String propertyName = property.name();
                EntityPropertyMetadata propertyMetadata = metadata.getMetadataForProperty(propertyName);
                property.readMetadata(propertyMetadata);
            }
        }

        load();
    }

    /**
     * Gets a value that determines if the data form is in read-only mode.
     */
    @SuppressWarnings("unused")
    public boolean getIsReadOnly() {
        return this.isReadOnly;
    }

    /**
     * Sets a value that determines if the data form is in read-only mode.
     */
    @SuppressWarnings("unused")
    public void setIsReadOnly(boolean value) {
        if(this.isReadOnly == value) {
            return;
        }
        this.isReadOnly = value;
        this.reload();
    }

    @SuppressWarnings("unused")
    public ValidationMode getValidationMode() {
        return this.validationMode;
    }

    @SuppressWarnings("unused")
    public void setValidationMode(ValidationMode value) {
        if(this.validationMode == value) {
            return;
        }
        this.validationMode = value;

        updateEditorsValidationMode(value);
    }

    private void updateEditorsValidationMode(ValidationMode value) {
        if(editors == null) {
            return;
        }

        for(EntityPropertyViewer viewer : editors) {
            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;
            editor.setValidationMode(value);
        }
    }

    /**
     * Gets the commit mode.
     */
    @SuppressWarnings("unused")
    public CommitMode getCommitMode() {
        return this.commitMode;
    }

    /**
     * Sets the commit mode.
     */
    public void setCommitMode(CommitMode mode) {
        if(this.commitMode == mode) {
            return;
        }
        this.commitMode = mode;

        updateEditorsCommitMode(mode);
    }

    /**
     * Gets the data form adapter.
     */
    public DataFormEntityAdapter getAdapter() {
        return this.adapter;
    }

    /**
     * Sets a the data form adapter.
     */
    public void setAdapter(DataFormEntityAdapter adapter) {
        if(this.adapter == adapter) {
            return;
        }

        if (adapter == null) {
            throw new IllegalArgumentException("adapter must not be null.");
        }

        this.adapter = adapter;

        if(this.entity != null) {
            reload();
        }
    }

    @SuppressWarnings("unused")
    public void resetManualCommit() {
        manualCommit = false;
    }

    /**
     * Call this method to apply the editor values to the
     * target object.
     */
    @SuppressWarnings("unused")
    public void commitChanges() {

        this.validateChanges();

        if(!pendingEditors.isEmpty()) {
            return;
        }

        if(manualCommit) {
            return;
        }

        if(editors == null) {
            return;
        }

        manualCommit = true;

        this.pendingEditors.clear();

        for (EntityPropertyViewer viewer : editors) {
            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;
            this.pendingEditors.add(editor);
        }

        for (EntityPropertyViewer viewer : editors) {
            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;
            editor.tryApplyValueToProperty();
        }
    }

    public void addValidationFinishedListener(Procedure<DataFormValidationInfo> listener) {
        this.validationFinishedListeners.add(listener);
    }

    public void removeValidationFinishedListener(Procedure<DataFormValidationInfo> listener) {
        this.validationFinishedListeners.remove(listener);
    }

    protected void onValidationFinished(DataFormValidationInfo info) {
        ArrayList<Procedure<DataFormValidationInfo>> tempList = new ArrayList<>();
        for(Procedure<DataFormValidationInfo> listener : this.validationFinishedListeners) {
            tempList.add(listener);
        }

        for(Procedure<DataFormValidationInfo> listener : tempList) {
            listener.apply(info);
        }
    }

    @SuppressWarnings("unused")
    public void validateChanges() {
        if(editors == null) {
            return;
        }

        manualValidation = true;
        for(EntityPropertyViewer viewer : editors) {
            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;

            pendingEditors.add(editor);
        }

        for(EntityPropertyViewer viewer : editors) {
            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;

            editor.validate();
        }
    }

    private void updateEditorsCommitMode(CommitMode mode) {
        if(editors == null) {
            return;
        }

        for(EntityPropertyViewer viewer : editors) {

            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;
            editor.setCommitMode(mode);
        }
    }

    private void load() {
        this.editors = this.isReadOnly ? this.adapter.getViewersForEntity(this.entity) : this.adapter.getEditorsForEntity(this.entity);
        this.addView(this.layoutManager.arrangeEditors(editors), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        this.updateEditorsValidationMode(validationMode);
        this.updateEditorsCommitMode(commitMode);

        for(EntityPropertyViewer viewer : editors) {
            if(!this.isReadOnly && !viewer.property().getReadOnly()) {
                EntityPropertyEditor editor = (EntityPropertyEditor) viewer;
                editor.addValidationListener(this);
            }

            viewer.load();

            if(getEditorCustomizations() != null) {
                getEditorCustomizations().apply(viewer);
            }
        }
    }

    private void unload() {
        if(editors != null) {
            for (EntityPropertyViewer editor : editors) {
                editor.unload();

                if (editor instanceof EntityPropertyEditor) {
                    ((EntityPropertyEditor) editor).removeValidationListener(this);
                }
            }
        }

        this.removeAllViews();
        this.layoutManager.unload();
        editors = null;
    }

    public boolean hasValidationErrors() {
        if(editors == null) {
            return false;
        }

        for (EntityPropertyViewer viewer : editors) {

            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;
            if (editor.validationInfo() != null && !editor.validationInfo().isValid()) {
                return true;
            }
        }

        return false;
    }

    private void commitManually() {
        for (EntityPropertyViewer viewer : editors) {
            if(!(viewer instanceof EntityPropertyEditor)) {
                continue;
            }

            EntityPropertyEditor editor = (EntityPropertyEditor)viewer;

            if(getEntity().notifyCommitListenersBefore(editor.property())) {
                continue;
            }

            editor.property().commit();
            getEntity().notifyCommitListenersAfter(editor.property());
        }
    }

    @Override
    public void onValidationEvent(EntityPropertyEditor editor, ValidationInfo info) {
        if(!this.pendingEditors.remove(editor)) {
            // This happens when a single property is being manually validated.
            // For manual validation of a single property the data form should
            // not raise the full validation finished notification.
            return;
        }
        validationInfos.add(info);

        if(this.pendingEditors.size() == 0) {
            this.onValidationFinished(new DataFormValidationInfo(validationInfos));
            validationInfos.clear();
        }

        if(manualValidation) {
            if(this.pendingEditors.size() == 0) {
                manualValidation = false;
            }
            return;
        }

        if (manualCommit && this.pendingEditors.isEmpty()) {
            manualCommit = false;
            if (!this.hasValidationErrors()) {
                this.commitManually();
            }
        }
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return canScroll && super.onTouchEvent(e);
            default:
                return super.onTouchEvent(e);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return canScroll && super.onInterceptTouchEvent(e);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new RadDataFormInstanceState(super.onSaveInstanceState(), this);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        RadDataFormInstanceState dataFormState = (RadDataFormInstanceState)state;

        for(EntityProperty property : this.entity.properties()) {
            getExistingEditorForProperty(property.name()).getEditorView().setId(dataFormState.editorIds.get(property.name()));
        }

        super.onRestoreInstanceState(dataFormState.getSuperState());
    }
}