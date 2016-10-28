package com.telerik.widget.dataform.visualization.editors;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

public class DataFormTextEditor extends EntityPropertyEditor implements TextWatcher, View.OnFocusChangeListener {
    private EditText coreEditor;

    public DataFormTextEditor(RadDataForm dataForm, EntityProperty property) {
        this(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_text_editor,
                R.id.data_form_text_editor,
                dataForm.getEditorsValidationLayout(), property);
    }

    public DataFormTextEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerViewId, editorLayoutId, editorViewId, validationLayoutId, property);
    }

    @Override
    protected void initEditor(View editor, EntityProperty property) {
        this.coreEditor = (EditText) this.editorView;
        this.coreEditor.setMaxLines(1);
        this.coreEditor.setSingleLine(true);
        this.coreEditor.addTextChangedListener(this);
        this.coreEditor.setOnFocusChangeListener(this);
        if(this.coreEditor.getHint() == null) {
            this.coreEditor.setHint(this.property().getHintText());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newTextValue = s.toString();
        this.onEditorValueChanged(newTextValue);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public Object value() {
        return this.coreEditor.getText().toString();
    }

    @Override
    protected boolean canEditorFocus() {
        return this.coreEditor.isFocusable();
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        if(this.coreEditor.getText().toString().equals(entityValue)) {
            return;
        }

        this.coreEditor.setText((String) entityValue);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            this.onEditorLostFocus();
        }
    }
}
