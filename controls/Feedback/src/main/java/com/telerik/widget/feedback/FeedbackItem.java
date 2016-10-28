package com.telerik.widget.feedback;

import android.util.Log;

import org.json.JSONObject;

/**
 * Instances of this class represent information about a feedback item.
 */
public class FeedbackItem {
    private String commentsCount;
    private String projectId;
    private String text;
    private String author;
    private String id;
    private SystemInfo systemInfo;
    private Image image;
    private String state;
    private String createdAt;
    private String modifiedAt;
    private String rootId;
    private String uid;

    public FeedbackItem() {

    }

    public FeedbackItem(JSONObject source) {
        this.init(source);
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRootId() {
        return this.rootId;
    }

    public void setRootId(String id) {
        this.rootId = id;
    }

    public String getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(String count) {
        this.commentsCount = count;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setProjectId(String id) {
        this.projectId = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return this.modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public SystemInfo getSystemInfo() {
        return this.systemInfo;
    }

    public void setSystemInfo(SystemInfo info) {
        this.systemInfo = info;
    }

    public JSONObject toJson() {
        return JSONHelper.toJSONObject(this);
    }

    private void init(JSONObject source) {
        JSONHelper.init(this, source);
    }
}
