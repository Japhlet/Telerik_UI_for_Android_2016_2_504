package com.telerik.widget.dataform.visualization.editors;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.widget.TimePicker;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataFormTimeEditor extends DataFormDateTimeEditor implements TimePickerDialog.OnTimeSetListener {
    public DataFormTimeEditor(RadDataForm dataForm, EntityProperty property) {
        this(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_time_editor,
                R.id.data_form_time_editor,
                dataForm.getEditorsValidationLayout(), property);
    }

    public DataFormTimeEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerId, int editorLayoutId, int editorId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerId, editorLayoutId, editorId, validationLayoutId, property);

        this.setDateFormat(new SimpleDateFormat("hh:mm aaa"));
    }

    @Override
    protected AlertDialog createDialog() {
        Calendar date = getCalendar();
        if(date == null) {
            date = Calendar.getInstance();
        }
        return new TimePickerDialog(context, this, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar newTime = getCalendar();
        if(newTime == null) {
            newTime = Calendar.getInstance();
        }
        newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newTime.set(Calendar.MINUTE, minute);

        this.setCalendar(newTime);

        if(Calendar.class.isAssignableFrom(property().type())) {
            this.applyEntityValueToEditor(newTime);
            this.onEditorValueChanged(newTime);
        } else {
            this.applyEntityValueToEditor(newTime.getTimeInMillis());
            this.onEditorValueChanged(newTime.getTimeInMillis());
        }
    }

    @Override
    protected DateFormat getDefaultFormat() {
        return DateFormat.getTimeInstance();
    }
}
