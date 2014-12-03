package com.comeplus.droidincupdate.updateaction;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateActionManager {

    public static UpdateAction createAction(ZipFile zipFile, Object jsonConf, PatchProgressListener lis) throws JSONException {
        JSONObject conf = (JSONObject)jsonConf;
        String actionName = conf.getString("action");
        UpdateAction action = createAction(actionName, zipFile, conf, lis);
        return action;
    }
    
    public static void execActions(List<UpdateAction> actions) throws IOException {
        for(UpdateAction action: actions) {
            action.exec();
        }
    }

    public static UpdateAction createAction(String name, ZipFile zipFile, JSONObject conf, PatchProgressListener lis) throws JSONException {
        if("replace".equals(name)) {
            return new ReplaceUpdateAction(zipFile, conf, lis);
        } else if("patch".equals(name)){
            return new BinPatchUpdateAction(zipFile, conf, lis);
        } else {
            throw new JSONException("unknown action name: " + name);
        }
    }
    
}
