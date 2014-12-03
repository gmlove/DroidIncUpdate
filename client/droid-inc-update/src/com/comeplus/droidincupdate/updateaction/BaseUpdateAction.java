package com.comeplus.droidincupdate.updateaction;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import com.comeplus.droidincupdate.Config;

public abstract class BaseUpdateAction implements UpdateAction {

    public static final int TYPE_LIB = 0;
    public static final int TYPE_RES = 1;
    
    protected int type;
    protected ZipFile zipFile;
    protected JSONObject conf;
    protected String fileToHandle;
    protected String newFilePathFromZip;
    protected ZipEntry entryToHandle;
    protected String sourceFileMd5 = null;
    protected String finalFileMd5 = null;
    protected PatchProgressListener lis = null;
    
    public BaseUpdateAction(ZipFile zipFile, JSONObject conf, PatchProgressListener lis) throws JSONException {
        this.conf = conf;
        this.zipFile = zipFile;
        this.lis = lis;
        this.parseConf();
    }

    private void parseConf() throws JSONException {
        String type = this.conf.getString("type");
        if("lib".equals(type)) {
            this.type = TYPE_LIB;
        } else if("res".equals(type)) {
            this.type = TYPE_RES;
        } else {
            throw new JSONException("unknown update action type: " + type);
        }
        fileToHandle = this.conf.getString("old_file");
        if(fileToHandle == null) {
            throw new JSONException("old_file must be set in update action config: " + this.conf.toString());
        }
        newFilePathFromZip = this.conf.getString("new_file");
        if(newFilePathFromZip == null) {
            throw new JSONException("new_file must be set in update action config: " + this.conf.toString());
        }
        entryToHandle = zipFile.getEntry(newFilePathFromZip);
        if(entryToHandle == null) {
            throw new JSONException("new_file must exists in zip file: " + newFilePathFromZip);
        }
        if(Config.DEBUG) {
            finalFileMd5 = this.conf.getString("md5");
            sourceFileMd5 = this.conf.getString("src_md5");
        }
    }

    public String getFileToHandlePath() {
        switch (this.type) {
            case TYPE_LIB:
                return Config.getIncUpdateLibFilePath(fileToHandle);
            case TYPE_RES:
                return Config.getIncUpdateResFilePath(fileToHandle);
            default:
                throw new RuntimeException("unkown type: " + this.type);
        }
    }
    
}
