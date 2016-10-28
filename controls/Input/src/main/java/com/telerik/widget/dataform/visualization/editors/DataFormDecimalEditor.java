package com.telerik.widget.dataform.visualization.editors;

import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormDecimalEditor extends DataFormIntegerEditor implements TextWatcher, View.OnFocusChangeListener {
    public DataFormDecimalEditor(RadDataForm dataForm, EntityProperty property) {
        this(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_integer_editor,
                R.id.data_form_integer_editor,
                dataForm.getEditorsValidationLayout(), property);
    }

    public DataFormDecimalEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerViewId, editorLayoutId, editorViewId, validationLayoutId, property);

        EditText coreEditor = (EditText)this.editorView;
        coreEditor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Number numberValue = this.parseNumber();
        if (numberValue != null) {
            String numberStringValue = String.valueOf(numberValue);
            String editorValue = String.valueOf(this.coreEditor.getText());
            if(editorValue.length() > 1 && editorValue.startsWith("0") && Character.isDigit(editorValue.charAt(1))) {
                Double numberDoubleValue = Double.parseDouble(numberStringValue);
                String valueFormatted = String.format("%.0f", numberDoubleValue);
                this.coreEditor.setText(valueFormatted);
                this.coreEditor.setSelection(valueFormatted.length());
            } else {
                this.onEditorValueChanged(numberValue);
            }
        } else {
            if(valueCanBeNull()) {
                this.onEditorValueChanged(null);
            }
        }
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        if(entityValue == null) {
            entityValue = "";
        }

        Number number = parseNumber();
        if(number != null && (Double)number == Double.parseDouble(entityValue.toString())) {
            return;
        }

        if(this.coreEditor.getText().toString().equals(entityValue.toString())) {
            return;
        }

        this.coreEditor.setText(entityValue.toString());
        this.coreEditor.setSelection(this.coreEditor.getText().length());
    }

    @Override
    protected Number parseNumber() {
        try {
            return Double.parseDouble(this.coreEditor.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);

        if(!hasFocus) {
            Number number = parseNumber();
            if(number == null || this.coreEditor.getText().toString().equals(number.toString())) {
                return;
            }

            this.coreEditor.setText(number.toString());
        }
    }
}
