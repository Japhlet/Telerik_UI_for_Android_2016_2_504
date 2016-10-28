package com.telerik.widget.dataform.engine;

public class EntityProperties {
    private PropertyResolutionMode resolutionMode;

    public EntityProperties(){
        this.resolutionMode = PropertyResolutionMode.All;
    }

    public PropertyResolutionMode getPropertyMode(){
        return this.resolutionMode;
    }

    public void setPropertyMode(PropertyResolutionMode mode){
        this.resolutionMode = mode;
    }

    public enum PropertyResolutionMode {
        All,
        Getters
    }
}
