package com.telerik.widget.dataform.visualization.viewers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

public class DataFormViewerState extends View.BaseSavedState {
    public static final Parcelable.Creator<DataFormViewerState> CREATOR = new Parcelable.Creator<DataFormViewerState>() {
        @Override
        public DataFormViewerState createFromParcel(Parcel source) {
            return new DataFormViewerState(source);
        }

        @Override
        public DataFormViewerState[] newArray(int size) {
            return new DataFormViewerState[size];
        }
    };

    public int editorId;

    public DataFormViewerState(Parcelable superState) {
        this(superState, null);
    }

    public DataFormViewerState(Parcelable superState, EntityPropertyViewer viewer) {
        super(superState);

        editorId = viewer.getEditorView().getId();
    }

    public DataFormViewerState(Parcel superState) {
        super(superState);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(editorId);
    }
}
