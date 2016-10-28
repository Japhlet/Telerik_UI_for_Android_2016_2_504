package com.telerik.widget.dataform.visualization.editors;

import android.util.Log;

import com.telerik.android.common.Function;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.engine.PropertyChangedListener;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.numberpicker.RadNumberPicker;

import java.util.HashMap;

public class DataFormNumberPickerEditor extends EntityPropertyEditor implements PropertyChangedListener {
    RadNumberPicker picker;

    public DataFormNumberPickerEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_number_picker,
                R.id.data_form_number_picker_editor,
                dataForm.getEditorsValidationLayout(),property);

        picker = (RadNumberPicker)editorView;
        picker.addPropertyChangedListener(this);
    }

    public Function<Object, String> getValueToStringConverter() {
        return picker.getValueToStringConverter();
    }

    public void setValueToStringConverter(Function<Object, String> converter) {
        picker.setValueToStringConverter(converter);
    }

    @Override
    public Object value() {
        if(property().type() == int.class) {
            return (int)picker.getValue();
        }
        if(property().type() == Integer.class) {
            return (((Number)picker.getValue()).intValue());
        }
        return picker.getValue();
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        if(entityValue == null) {
            return;
        }
        Double entityDoubleValue = ((Number) entityValue).doubleValue();
        if(entityDoubleValue.equals(picker.getValue())) {
            return;
        }
        picker.setValue(entityDoubleValue);
    }

    @Override
    public void applyParams(HashMap<String, Object> params) {
        for(String key : params.keySet()) {
            Object value = params.get(key);
            switch (key) {
                case "min":
                case "minimum":
                    picker.setMinimum(((Number) value).doubleValue());
                    break;
                case "max":
                case "maximum":
                    picker.setMaximum(((Number) value).doubleValue());
                    break;
                case "step":
                    picker.setStep(((Number) value).doubleValue());
                    break;
                case "zero":
                case "zeroFormat":
                case "zeroFormatString":
                    picker.setZeroFormatString((String) value);
                    break;
                case "single":
                case "singleFormat":
                case "singleFormatString":
                    picker.setSingleFormatString((String) value);
                    break;
                case "plural":
                case "pluralFormat":
                case "pluralFormatString":
                    picker.setPluralFormatString((String) value);
                    break;
                default:
                    Log.e("DataForm", String.format("The key %s is not recognized. Use one of the following: min, max, step, zeroFormat, singleFormat, pluralFormat", key));
            }
        }
    }

    @Override
    public void onPropertyChanged(String propertyName, Object value) {
        if(propertyName.equals("Value")) {
            if(property().type() == int.class || property().type() == Integer.class) {
                onEditorValueChanged(((Number)value).intValue());
            } else {
                onEditorValueChanged(value);
            }
        }
    }
}
