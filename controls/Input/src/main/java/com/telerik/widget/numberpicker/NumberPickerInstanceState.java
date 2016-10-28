package com.telerik.widget.numberpicker;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

public class NumberPickerInstanceState extends View.BaseSavedState {
    public static final Creator<NumberPickerInstanceState> CREATOR = new Creator<NumberPickerInstanceState>() {
        @Override
        public NumberPickerInstanceState createFromParcel(Parcel source) {
            return new NumberPickerInstanceState(source);
        }

        @Override
        public NumberPickerInstanceState[] newArray(int size) {
            return new NumberPickerInstanceState[size];
        }
    };

    public double value;

    public NumberPickerInstanceState(Parcelable superState) {
        super(superState);
    }

    public NumberPickerInstanceState(Parcelable superState, RadNumberPicker picker) {
        super(superState);

        this.value = picker.getValue();
    }

    public NumberPickerInstanceState(Parcel superState) {
        super(superState);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeDouble(this.value);
    }
}