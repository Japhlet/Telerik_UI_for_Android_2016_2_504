package com.telerik.widget.dataform.visualization.editors;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.telerik.android.common.Function;
import com.telerik.android.common.Util;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

import java.lang.reflect.Array;
import java.util.Arrays;

public class DataFormRadioGroupEditor extends EntityPropertyEditor implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup group;
    private Object[] values;
    private Function<Object, String> converter;

    public DataFormRadioGroupEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerViewId, editorLayoutId, editorViewId, validationLayoutId, property);

        group = (RadioGroup)editorView;

        group.setOnCheckedChangeListener(this);

        if(property.type().isEnum()) {
            setValues(property.type().getEnumConstants());
        } else if(property.getEnumConstants() != null) {
            setValues(property.getEnumConstants());
        }
    }

    public DataFormRadioGroupEditor(RadDataForm dataForm, EntityProperty property) {
        this(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_radio_group_editor,
                R.id.data_form_radio_group,
                dataForm.getEditorsValidationLayout(), property);
    }

    public Function<Object, String> getValueToStringConverter() {
        return this.converter;
    }

    public void setValueToStringConverter(Function<Object, String> converter) {
        this.converter = converter;
    }

    @Override
    public Object value() {
        View checkedButton = group.findViewById(group.getCheckedRadioButtonId());
        if(checkedButton == null) {
            return null;
        }

        return checkedButton.getTag();
    }

    public void setValues(Object[] values) {
        if(this.values == values) {
            return;
        }

        if(values != null && values.length == 0) {
            throw new IllegalArgumentException("Array argument can not have zero length.");
        }

        this.values = values;
        recreateRadioButtons();

        if(values == null) {
            return;
        }

        Object propertyValue = property().getValue();
        if(Arrays.asList(values).contains(propertyValue)) {
            this.applyEntityValueToEditor(property().getValue());
        } else {
            this.applyEntityValueToEditor(values[0]);
        }
    }

    protected void recreateRadioButtons() {
        group.removeAllViews();

        if(this.values == null) {
            return;
        }

        for(Object value : values) {
            RadioButton button = this.createButton(this.rootLayout().getContext());
            button.setTag(value);

            if(this.converter == null) {
                button.setText(value.toString());
            } else {
                converter.apply(value);
            }
            group.addView(button);
        }
    }

    protected RadioButton createButton(Context context) {
        return new RadioButton(context);
    }

    public Object[] getValues() {
        return this.values;
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        RadioButton buttonToCheck = findButtonByTag(entityValue);
        if(buttonToCheck == null) {
            return;
        }

        buttonToCheck.setChecked(true);
    }

    protected RadioButton findButtonByTag(Object tag) {
        for(int i = 0; i < group.getChildCount(); ++i) {
            RadioButton button = (RadioButton)group.getChildAt(i);
            if(Util.Equals(tag, button.getTag())) {
                return button;
            }
        }

        return null;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for(int i = 0; i < group.getChildCount(); ++i) {
            View radioButton = group.getChildAt(i);
            if(radioButton.getId() == checkedId) {
                this.onEditorValueChanged(radioButton.getTag());
                break;
            }
        }
    }
}
