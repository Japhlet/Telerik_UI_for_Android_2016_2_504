package com.telerik.widget.dataform.visualization;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import com.telerik.widget.dataform.engine.EntityProperty;

import java.util.HashMap;

public class RadDataFormInstanceState extends View.BaseSavedState {
    public static final Parcelable.Creator<RadDataFormInstanceState> CREATOR = new Parcelable.Creator<RadDataFormInstanceState>() {
        @Override
        public RadDataFormInstanceState createFromParcel(Parcel source) {
            return new RadDataFormInstanceState(source);
        }

        @Override
        public RadDataFormInstanceState[] newArray(int size) {
            return new RadDataFormInstanceState[size];
        }
    };

    public HashMap<String, Integer> editorIds = new HashMap<>();

    public RadDataFormInstanceState(Parcelable superState) {
        this(superState, null);
    }

    public RadDataFormInstanceState(Parcelable superState, RadDataForm dataForm) {
        super(superState);

        for(EntityProperty property : dataForm.getEntity().properties()) {
            editorIds.put(property.name(), dataForm.getExistingEditorForProperty(property.name()).getEditorView().getId());
        }
    }

    public RadDataFormInstanceState(Parcel superState) {
        super(superState);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeMap(editorIds);
    }
}
