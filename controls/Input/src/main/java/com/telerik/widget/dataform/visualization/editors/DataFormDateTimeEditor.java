package com.telerik.widget.dataform.visualization.editors;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.telerik.android.common.Function;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class DataFormDateTimeEditor extends EntityPropertyEditor implements View.OnClickListener {
    private Calendar date;
    private DateFormat format;
    protected Context context;
    private Function<Object, String> converter;

    public DataFormDateTimeEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerViewId, editorLayoutId, editorViewId, validationLayoutId, property);

        context = dataForm.getContext();
        editorView.setOnClickListener(this);
        EditText picker = (EditText)this.editorView;
        if(picker.getHint() == null) {
            picker.setHint(property.getHintText());
        }
    }

    public Function<Object, String> getValueToStringConverter() {
        return this.converter;
    }

    public void setValueToStringConverter(Function<Object, String> converter) {
        this.converter = converter;
        updatePickerText();
    }

    public DateFormat getDateFormat() {
        if(format == null) {
            return this.getDefaultFormat();
        } else {
            return this.format;
        }
    }

    public void setTimeInMillis(Long value) {
        if(date == null) {
            date = Calendar.getInstance();
        }
        date.setTimeInMillis(value);
        updatePickerText();
    }

    public Long getTimeInMillis() {
        if(date == null) {
            return null;
        }
        return date.getTimeInMillis();
    }

    public void setCalendar(Calendar value) {
        if(value == null) {
            throw new IllegalArgumentException("calendar can't be null");
        }
        date = value;
        updatePickerText();
    }

    public Calendar getCalendar() {
        return date;
    }

    public void setDateFormat(DateFormat value) {
        this.format = value;
        updatePickerText();
    }

    protected abstract DateFormat getDefaultFormat();

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        if(entityValue != null) {
            if (Calendar.class.isAssignableFrom(property().type())) {
                date = (Calendar) entityValue;
            } else {
                Number num = (Number) entityValue;
                date = Calendar.getInstance();
                date.setTimeInMillis(num.longValue());
            }
        }

        updatePickerText();
    }

    private void updatePickerText() {
        EditText picker = (EditText)this.editorView;
        String result;
        if(converter != null) {
            result = converter.apply(value());
        } else {
            result = date == null ? null : getDateFormat().format(new Date(date.getTimeInMillis()));
        }
        picker.setText(result);
    }

    public AlertDialog getDialog() {
        return this.createDialog();
    }

    protected abstract AlertDialog createDialog();

    @Override
    public Object value() {
        if(date == null) {
            return null;
        }
        if(Calendar.class.isAssignableFrom(property().type())) {
            return date;
        }
        return date.getTimeInMillis();
    }

    @Override
    public void onClick(View v) {
        showDialog();
    }

    protected void showDialog() {
        AlertDialog dialog = getDialog();
        if(dialog != null) {
            dialog.show();
        }
    }

    protected void onPropertyValueChanged(Object value) {
        Calendar newDate;
        if(Calendar.class.isAssignableFrom(property().type())) {
            newDate = (Calendar) value;
        } else {
            newDate = Calendar.getInstance();
            newDate.setTimeInMillis((long)value);
        }
        long millis = newDate.getTimeInMillis();

        if(Calendar.class.isAssignableFrom(property().type())) {
            this.applyEntityValueToEditor(newDate);
            this.onEditorValueChanged(newDate);
        } else {
            this.applyEntityValueToEditor(millis);
            this.onEditorValueChanged(millis);
        }
    }
}
