package com.comeplus.droidincupdate.updateaction;

import java.io.IOException;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.comeplus.droidincupdate.Config;
import com.comeplus.droidincupdate.utils.FileUtils;
import com.comeplus.droidincupdate.utils.MD5;

public class ReplaceUpdateAction extends BaseUpdateAction {

    public ReplaceUpdateAction(ZipFile zipFile, JSONObject conf, PatchProgressListener lis) throws JSONException {
        super(zipFile, conf, lis);
    }

    @Override
    public void exec() throws IOException {
        String fileToHandle = getFileToHandlePath();
        FileUtils.writeFile(zipFile.getInputStream(entryToHandle), fileToHandle);
        if(this.lis != null) {
            this.lis.onPatchProgress(fileToHandle);
        }
        if(Config.DEBUG) {
            String md5 = MD5.caclFileMd5(fileToHandle);
            Log.d(Config.LOG_TAG, String.format("replace file[%s]: %s", fileToHandle, md5));
        }
    }

}
