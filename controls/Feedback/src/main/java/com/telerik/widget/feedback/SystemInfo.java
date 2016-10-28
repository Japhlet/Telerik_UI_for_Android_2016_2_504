package com.telerik.widget.feedback;

import org.json.JSONObject;

/**
 * Encapsulates system information about the device. Used by {@link com.telerik.widget.feedback.RadFeedback}.
 */
public class SystemInfo {
    private String uuid;
    private String appId;
    private String OSVersion;
    private String model;
    private String widthInPixels;
    private String heightInPixels;
    private String appVersion;

    public SystemInfo(){

    }

    public String getAppVersion(){
        return this.appVersion;
    }

    public void setAppVersion(String appVersion){
        this.appVersion = appVersion;
    }

    public SystemInfo(JSONObject source){
        this.init(source);
    }

    public String getUuid(){
        return this.uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getAppId(){
        return this.appId;
    }

    public void setAppId(String appId){
        this.appId = appId;
    }

    public String getOSVersion(){
        return this.OSVersion;
    }

    public void setOSVersion(String osVersion){
        this.OSVersion = osVersion;
    }

    public String getModel(){
        return this.model;
    }

    public void setModel(String model){
        this.model = model;
    }

    public String getWidthInPixels(){
        return this.widthInPixels;
    }

    public void setWidthInPixels(String widthInPixels){
        this.widthInPixels = widthInPixels;
    }

    public String getHeightInPixels(){
        return this.heightInPixels;
    }

    public void setHeightInPixels(String heightInPixels){
        this.heightInPixels = heightInPixels;
    }

    public JSONObject toJson(){
        return JSONHelper.toJSONObject(this);
    }

    public void init(JSONObject source){
        JSONHelper.init(this, source);
    }
}
