package com.telerik.widget.numberpicker;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.telerik.android.common.Function;
import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.NotifyPropertyChangedBase;
import com.telerik.widget.dataform.engine.PropertyChangedListener;

public class RadNumberPicker extends FrameLayout implements View.OnClickListener {
    private double minimum = 0;
    private double maximum = 10;
    private double step = 1;
    private TextView numberView;
    private double value = 0;

    private String zeroFormatString = "%.0f";
    private String singleFormatString = "%.0f";
    private String pluralFormatString = "%.0f";

    private Function<Object, String> converter;

    private NotifyPropertyChangedBase propertyChangeImpl = new NotifyPropertyChangedBase();

    public RadNumberPicker(Context context) {
        this(context, null, R.layout.number_picker);
    }

    public RadNumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.layout.number_picker);
    }

    public RadNumberPicker(Context context, AttributeSet attrs, int layoutId) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(layoutId, this);

        TextView plusButton = Util.getLayoutPart(this, R.id.number_picker_plus, TextView.class);
        plusButton.setOnClickListener(this);
        TextView minusButton = Util.getLayoutPart(this, R.id.number_picker_minus, TextView.class);
        minusButton.setOnClickListener(this);

        numberView = Util.getLayoutPart(this, R.id.number_picker_view, TextView.class);
        updateTextView(value);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LicensingProvider.verify(this.getContext());
    }

    public Function<Object, String> getValueToStringConverter() {
        return this.converter;
    }

    public void setValueToStringConverter(Function<Object, String> converter) {
        this.converter = converter;
        updateTextView(value);
    }

    public String getZeroFormatString() {
        return zeroFormatString;
    }

    public void setZeroFormatString(String value) {
        zeroFormatString = value;
        updateTextView(this.value);

        this.propertyChangeImpl.notifyListeners("ZeroFormatString", value);
    }

    public String getPluralFormatString() {
        return pluralFormatString;
    }

    public void setPluralFormatString(String value) {
        pluralFormatString = value;
        updateTextView(this.value);

        this.propertyChangeImpl.notifyListeners("PluralFormatString", value);
    }
    public String getSingleFormatString() {
        return singleFormatString;
    }

    public void setSingleFormatString(String value) {
        singleFormatString = value;
        updateTextView(this.value);

        this.propertyChangeImpl.notifyListeners("SingleFormatString", value);
    }

    public double getStep() {
        return step;
    }

    public void setStep(double value) {
        this.step = value;

        this.propertyChangeImpl.notifyListeners("Step", value);
    }

    public double getMinimum() {
        return minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMinimum(double value) {
        this.minimum = value;

        this.propertyChangeImpl.notifyListeners("Minimum", value);
    }

    public void setMaximum(double value) {
        this.maximum = value;

        this.propertyChangeImpl.notifyListeners("Maximum", value);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.number_picker_minus) {
            double newValue = value - step;
            if(newValue >= minimum) {
                value = newValue;
            } else {
                return;
            }
        } else {
            double newValue = value + step;
            if(newValue <= maximum) {
                value = newValue;
            } else {
                return;
            }
        }

        this.updateTextView(value);
        propertyChangeImpl.notifyListeners("Value", this.value);
    }

    private void updateTextView(double value) {
        String formatString;
        if(value == 0) {
            formatString = zeroFormatString;
        } else if(value == 1 || value == -1) {
            formatString = singleFormatString;
        } else {
            formatString = pluralFormatString;
        }

        String result;
        if(converter != null) {
            result = converter.apply(value);
        } else {
            result = String.format(formatString, value);
        }
        numberView.setText(result);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        return new NumberPickerInstanceState(state, this);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        NumberPickerInstanceState pickerState = (NumberPickerInstanceState)state;

        this.setValue(pickerState.value);

        super.onRestoreInstanceState(pickerState.getSuperState());
    }

    public void addPropertyChangedListener(PropertyChangedListener listener) {
        propertyChangeImpl.addPropertyChangedListener(listener);
    }

    public void removePropertyChangedListener(PropertyChangedListener listener) {
        propertyChangeImpl.removePropertyChangedListener(listener);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        if(value < minimum) {
            value = minimum;
        }

        if(value > maximum) {
            value = maximum;
        }

        if(this.value == value) {
            return;
        }
        this.value = value;
        this.updateTextView(value);
        this.propertyChangeImpl.notifyListeners("Value", value);
    }
}
