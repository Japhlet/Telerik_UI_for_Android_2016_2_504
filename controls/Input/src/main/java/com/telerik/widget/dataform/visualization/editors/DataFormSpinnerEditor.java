package com.telerik.widget.dataform.visualization.editors;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.editors.adapters.EditorSpinnerAdapter;

public class DataFormSpinnerEditor extends EntityPropertyEditor implements AdapterView.OnItemSelectedListener {
    private Spinner coreEditor;
    private EditorSpinnerAdapter adapter;
    private boolean initializing = true;

    public DataFormSpinnerEditor(RadDataForm dataForm, EntityProperty property) {
        this(dataForm, property, null);
    }

    public DataFormSpinnerEditor(RadDataForm dataForm, EntityProperty property, EditorSpinnerAdapter adapter) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_spinner_editor,
                R.id.data_form_spinner_editor,
                dataForm.getEditorsValidationLayout(), property);
        this.adapter = adapter;

        this.coreEditor = (Spinner) this.editorView;
        if(adapter != null) {
            this.coreEditor.setAdapter(adapter);
        } else if(property.type().isEnum()) {
            this.adapter = new EditorSpinnerAdapter(dataForm.getContext(), R.layout.data_form_spinner_item, property.type().getEnumConstants());
            this.coreEditor.setAdapter(this.adapter);
        } else if(property.getEnumConstants() != null) {
            this.adapter = new EditorSpinnerAdapter(dataForm.getContext(), R.layout.data_form_spinner_item, property.getEnumConstants());
            this.coreEditor.setAdapter(this.adapter);
        }

        this.coreEditor.setOnItemSelectedListener(DataFormSpinnerEditor.this);
    }

    public EditorSpinnerAdapter getAdapter() {
        return (EditorSpinnerAdapter)coreEditor.getAdapter();
    }

    public void setAdapter(EditorSpinnerAdapter value) {
        this.coreEditor.setAdapter(value);
        this.adapter = value;

        Object propertyValue = this.property().getValue();
        if(value != null && propertyValue != null) {
            this.coreEditor.setSelection(value.getPosition(propertyValue));
        }
    }

    @Override
    protected boolean canEditorFocus() {
        return false;
    }

    @Override
    public Object value() {
        return this.coreEditor.getSelectedItem();
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        if(adapter == null || entityValue == null) {
            return;
        }

        this.coreEditor.setSelection(this.adapter.getPosition(entityValue));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(initializing) {
            initializing = false;
            return;
        }

        if(this.adapter == null) {
            return;
        }

        Object selectedItem = this.adapter.getItem(position);
        if(selectedItem == null) {
            return;
        }

        this.onEditorValueChanged(selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
