package com.telerik.widget.dataform.engine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class NotifyPropertyChangedBase implements NotifyPropertyChanged {
    private ArrayList<WeakReference<PropertyChangedListener>> listeners = new ArrayList<>();

    @Override
    public void addPropertyChangedListener(PropertyChangedListener listener) {
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removePropertyChangedListener(PropertyChangedListener listener) {
        ArrayList<WeakReference<PropertyChangedListener>> referencesToRemove = new ArrayList<>();

        for(WeakReference<PropertyChangedListener> reference : listeners ) {
            if(reference.get() == listener || reference.get() == null) {
                referencesToRemove.add(reference);
            }
        }

        for(WeakReference<PropertyChangedListener> reference : referencesToRemove) {
            listeners.remove(reference);
        }
    }

    public void notifyListeners(String propertyName, Object newValue) {
        for(WeakReference<PropertyChangedListener> reference : listeners) {
            PropertyChangedListener propertyChangedListener = reference.get();
            if(propertyChangedListener != null) {
                propertyChangedListener.onPropertyChanged(propertyName, newValue);
            }
        }
    }
}
