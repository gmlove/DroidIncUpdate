package com.comeplus.droidincupdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UpdateConfig {

    private JSONObject config = null;
    
    public UpdateConfig(String jsonStr) throws JSONException {
        if(Config.DEBUG) {
            Log.d(Config.LOG_TAG, "updateConfig: " + jsonStr);
        }
        this.config = new JSONObject(jsonStr);
    }
    
    public boolean isUpdateNotSupported() {
        return this.config.optBoolean("not_supported");
    }
    
    public String getVersion() {
        return this.config.optString("version");
    }
    
    public JSONArray getUpdateActions() {
         return this.config.optJSONArray("files_to_update");
    }
}
