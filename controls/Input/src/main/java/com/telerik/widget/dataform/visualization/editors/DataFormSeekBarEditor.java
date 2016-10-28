package com.telerik.widget.dataform.visualization.editors;

import android.util.Log;
import android.widget.SeekBar;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

import java.util.HashMap;

public class DataFormSeekBarEditor extends EntityPropertyEditor implements SeekBar.OnSeekBarChangeListener {
    private SeekBar coreEditor;
    private int max;
    private int min;

    public DataFormSeekBarEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_seek_bar_editor,
                R.id.data_form_seekbar_editor,
                dataForm.getEditorsValidationLayout(), property);

        this.coreEditor = (SeekBar) this.editorView;
        this.coreEditor.setOnSeekBarChangeListener(this);
    }

    @Override
    public Object value() {
        return getTypedValue();
    }

    public int getTypedValue() {
        return min + this.coreEditor.getProgress();
    }

    public void setTypedValue(int value) {
        this.coreEditor.setProgress(value);
    }

    public void setMin(int value) {
        min = value;

        coreEditor.setMax(Math.abs(max - min));
    }

    public int getMin() {
        return min;
    }

    public void setMax(int value) {
        max = value;

        coreEditor.setMax(Math.abs(max - min));
    }

    public int getMax() {
        return max;
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        if(entityValue == null) {
            return;
        }

        setTypedValue((Integer) entityValue);
    }

    @Override
    public void applyParams(HashMap<String, Object> params) {
        for(String key : params.keySet()) {
            Object value = params.get(key);
            switch (key) {
                case "min":
                case "minimum":
                    setMin(((Number) value).intValue());
                    break;
                case "max":
                case "maximum":
                    setMax(((Number) value).intValue());
                    break;
                default:
                    Log.e("DataForm", String.format("The key %s is not recognized. Use one of the following: min, max", key));
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.onEditorValueChanged(value());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
