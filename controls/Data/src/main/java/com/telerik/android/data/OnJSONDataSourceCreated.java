package com.telerik.android.data;

import org.json.JSONException;
import org.json.JSONObject;

public interface OnJSONDataSourceCreated {
    void onError(JSONException ex);
    void onDataSourceCreated(RadDataSource<JSONObject> result);
}
