package com.telerik.widget.dataform.visualization.editors;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataFormDateEditor extends DataFormDateTimeEditor implements DatePickerDialog.OnDateSetListener {
    public DataFormDateEditor(RadDataForm dataForm, EntityProperty property) {
        this(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_date_editor,
                R.id.data_form_date_editor,
                dataForm.getEditorsValidationLayout(), property);
    }

    public DataFormDateEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerId, int editorLayoutId, int editorId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerId, editorLayoutId, editorId, validationLayoutId, property);

        this.setDateFormat(new SimpleDateFormat("EEE, dd.MM"));
    }

    @Override
    protected AlertDialog createDialog() {
        Calendar date = getCalendar();
        if(date == null) {
            date = Calendar.getInstance();
        }
        return new DatePickerDialog(context, this, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar newDate = getCalendar();
        if(newDate == null) {
            newDate = Calendar.getInstance();
        }
        newDate.set(Calendar.YEAR, year);
        newDate.set(Calendar.MONTH, month);
        newDate.set(Calendar.DAY_OF_MONTH, day);

        this.setCalendar(newDate);
        if(Calendar.class.isAssignableFrom(property().type())) {
            this.applyEntityValueToEditor(newDate);
            this.onEditorValueChanged(newDate);
        } else {
            this.applyEntityValueToEditor(newDate.getTimeInMillis());
            this.onEditorValueChanged(newDate.getTimeInMillis());
        }
    }

    @Override
    protected DateFormat getDefaultFormat() {
        return DateFormat.getDateInstance();
    }
}
