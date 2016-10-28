package com.telerik.widget.dataform.visualization.editors;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

public class DataFormIntegerEditor extends EntityPropertyEditor implements TextWatcher, View.OnFocusChangeListener {
    protected EditText coreEditor;
    public DataFormIntegerEditor(RadDataForm dataForm, EntityProperty property) {
        this(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_integer_editor,
                R.id.data_form_integer_editor,
                dataForm.getEditorsValidationLayout(), property);
    }

    public DataFormIntegerEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerViewId, editorLayoutId, editorViewId, validationLayoutId, property);

        this.coreEditor = (EditText) this.editorView;
        this.coreEditor.addTextChangedListener(this);
        this.coreEditor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        this.coreEditor.setOnFocusChangeListener(this);
        if(this.coreEditor.getHint() == null) {
            this.coreEditor.setHint(property.getHintText());
        }
    }

    @Override
    public Object value() {
        Number numberValue = this.parseNumber();
        if(numberValue == null && !valueCanBeNull()) {
            return 0;
        }
        return numberValue;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Number numberValue = this.parseNumber();
        if (numberValue != null) {
            String numberStringValue = String.valueOf(numberValue);
            String editorValue = String.valueOf(this.coreEditor.getText());
            if(!numberStringValue.equals(editorValue)) {
                this.coreEditor.setText(numberStringValue);
                this.coreEditor.setSelection(this.coreEditor.getText().length());
            } else {
                this.onEditorValueChanged(numberValue);
            }
        } else {
            if(valueCanBeNull()) {
                this.onEditorValueChanged(null);
            }
        }
    }

    protected boolean valueCanBeNull() {
        return !property().isTypePrimitive();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    protected boolean canEditorFocus() {
        return this.coreEditor.isFocusable();
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        if(entityValue == null) {
            entityValue = "";
        }

        if(this.coreEditor.getText().toString().equals(entityValue.toString())) {
            return;
        }

        this.coreEditor.setText(entityValue.toString());
        this.coreEditor.setSelection(this.coreEditor.getText().length());
    }

    protected Number parseNumber() {
        try {
            return Integer.parseInt(this.coreEditor.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            this.onEditorLostFocus();
            if(parseNumber() == null && !valueCanBeNull()) {
                this.coreEditor.setText("0");
                this.coreEditor.setSelection(this.coreEditor.getText().length());
            }
        }
    }
}
