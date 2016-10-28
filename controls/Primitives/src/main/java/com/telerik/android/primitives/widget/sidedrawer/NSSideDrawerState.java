package com.telerik.android.primitives.widget.sidedrawer;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class NSSideDrawerState extends View.BaseSavedState {
    public static final Creator<NSSideDrawerState> CREATOR = new Creator<NSSideDrawerState>() {
        @Override
        public NSSideDrawerState createFromParcel(Parcel source) {
            return new NSSideDrawerState(source);
        }

        @Override
        public NSSideDrawerState[] newArray(int size) {
            return new NSSideDrawerState[size];
        }
    };

    private NSSideDrawer drawer;
    private boolean tapOutside = false;
    private int touchTarget = 0;
    private DrawerLocation location;
    private boolean isLocked;
    private boolean isOpen;
    private String transitionTypeName;
    private DrawerTransition transition;
    private boolean closeOnBackPress;

    public NSSideDrawerState(NSSideDrawer drawer, Parcelable parentState) {
        super(parentState);

        this.drawer = drawer;
        this.tapOutside = drawer.getTapOutsideToClose();
        this.touchTarget = drawer.getTouchTargetThreshold();
        this.location = drawer.getDrawerLocation();
        this.isLocked = drawer.getIsLocked();
        this.isOpen = drawer.getIsOpen();
        this.transition = drawer.getDrawerTransition();
        if(this.transition != null) {
            this.transitionTypeName = this.transition.getClass().getName();
        }

        this.closeOnBackPress = drawer.getCloseOnBackPress();
    }

    public NSSideDrawerState(Parcel parcel) {
        super(parcel);
        tapOutside = (Boolean)parcel.readValue(null);
        touchTarget = parcel.readInt();
        location = (DrawerLocation)parcel.readValue(null);
        isLocked = (Boolean) parcel.readValue(null);
        isOpen = (Boolean)parcel.readValue(null);
        transitionTypeName = parcel.readString();

        if(!transitionTypeName.isEmpty()) {
            try {
                Class transitionClass = Class.forName(transitionTypeName);
                transition = (DrawerTransition) transitionClass.newInstance();
                transition.restoreInstanceState(parcel);

            } catch (ClassNotFoundException ex) {
                throw new Error(ex);
            } catch (InstantiationException ex) {
                throw new Error(ex);
            } catch (IllegalAccessException ex) {
                throw new Error(ex);
            }
        }

        this.closeOnBackPress = (Boolean)parcel.readValue(null);
    }

    public boolean getTapOutsideToClose() {
        return tapOutside;
    }

    public int getTouchTargetThreshold() {
        return touchTarget;
    }

    public DrawerLocation getDrawerLocation() {
        return location;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public DrawerTransition getTransition() {
        return this.transition;
    }

    @Override
    public int describeContents() {
        super.describeContents();
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(drawer.getTapOutsideToClose());
        dest.writeInt(drawer.getTouchTargetThreshold());
        dest.writeValue(drawer.getDrawerLocation());
        dest.writeValue(drawer.getIsLocked());
        dest.writeValue(drawer.getIsOpen());

        if(transitionTypeName != null) {
            dest.writeString(transitionTypeName);
        }
        dest.writeValue(closeOnBackPress);
    }
}
