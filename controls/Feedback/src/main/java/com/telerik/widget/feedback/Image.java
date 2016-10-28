package com.telerik.widget.feedback;

import org.json.JSONObject;

/**
 * Created by ginev on 15/05/2014.
 */
public class Image {
    private String uri;
    private String fileName = "screenshot.png";
    private String contentType;
    private String createdAt;
    private String base64;

    public Image() {

    }

    public Image(JSONObject source) {
        this.init(source);
    }

    public void setbase64(String base64) {
        this.base64 = base64;
    }

    public String getbase64() {
        return this.base64;
    }

    public void setFilename(String name) {
        this.fileName = name;
    }

    public String getFilename() {
        return this.fileName;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void init(JSONObject source) {
        JSONHelper.init(this, source);
    }
}
