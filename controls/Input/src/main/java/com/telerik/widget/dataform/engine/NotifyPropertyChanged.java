package com.telerik.widget.dataform.engine;

public interface NotifyPropertyChanged {
    void addPropertyChangedListener(PropertyChangedListener listener);
    void removePropertyChangedListener(PropertyChangedListener listener);
}
