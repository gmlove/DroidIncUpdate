package com.comeplus.droidincupdate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.comeplus.droidincupdate.updateaction.PatchProgressListener;
import com.comeplus.droidincupdate.updateaction.UpdateAction;
import com.comeplus.droidincupdate.updateaction.UpdateActionManager;
import com.comeplus.droidincupdate.utils.FileUtils;
import com.comeplus.droidincupdate.utils.NetUtils;

public class UpdateManager {

    private Context context = null;
    private ProgressListener progressListener = null;
    private UpdateConfig updateConfig = null;

    public UpdateManager(Context context, UpdateProgressListener progressListener) throws IOException, JSONException {
        if (context == null) {
            throw new IllegalArgumentException("null parameter!");
        }
        this.context = context;
        Config.init(context);
        this.progressListener = new ProgressListener(progressListener);
    }

    public void update() throws IOException, JSONException, NotSupportedVersionException {
        if (Config.FORCE_EXTRACT) {
            Log.d(Config.LOG_TAG, "force extract.");
            this.extractRes();
        } else if (!this.resExtracted()) {
            this.extractRes();
        } else {
            Log.d(Config.LOG_TAG, "files already extracted.");
        }
        progressListener.onExtractEnd();
        this.doUpdate();
    }

    public boolean isSpaceEnough() throws IOException {
        long assetsResLength = Config.getAssetsResSize();
        long availResSpace = FileUtils.availableSpace(Config.getIncUpdateResDir());
        long assetsLibsLength = Config.getAssetsLibSize();
        long availLibsSpace = FileUtils.availableSpace(Config.getIncUpdateLibsDir());
        if (assetsResLength * Config.minAvailSpaceRatio > availResSpace
                || assetsLibsLength * Config.minAvailSpaceRatio > availLibsSpace) {
            Log.d(Config.LOG_TAG, String.format(
                    "space not enough: resSpaceNeeded[%s] > %s, or libSpaceNeeded[%s] > %s",
                    assetsResLength * Config.minAvailSpaceRatio, availResSpace,
                    assetsLibsLength * Config.minAvailSpaceRatio, availLibsSpace));
            return false;
        }
        return true;
    }

    private boolean resExtracted() {
        return Config.isExtracted();
    }

    public void extractRes() throws IOException {
        Log.d(Config.LOG_TAG, "extractRes begin.");
        progressListener.beginExtract(context, new String[]{Config.INCUPDATE_RES_DIR_FROM_ASSETS, Config.INCUPDATE_LIBS_DIR_FROM_ASSETS});
        FileUtils.copyFromAssetsDir(this.context, Config.INCUPDATE_RES_DIR_FROM_ASSETS, Config.getIncUpdateResDir(), progressListener);
        FileUtils.copyFromAssetsDir(this.context, Config.INCUPDATE_LIBS_DIR_FROM_ASSETS, Config.getIncUpdateLibsDir(), progressListener);
        Config.setExtracted();
        Config.flushUpdateInfo();
        Log.d(Config.LOG_TAG, "extractRes end.");
    }

    public void doUpdate() throws IOException, JSONException, NotSupportedVersionException {
        FileUtils.ensureDir(Config.getIncUpdateTmpDirPath());
        String zipFilePath = Config.getIncUpdateTmpDirPath() + File.separator + "update.zip";
        NetUtils.downloadFile(Config.UPDATE_URL + "?version=" + Config.getResVersion() + "&" + SystemInfo.asUrlParam(context),
                zipFilePath, this.progressListener);
        progressListener.onDownloadEnd();
        ZipFile zipFile = null;
        try{
            zipFile = new ZipFile(zipFilePath);
            ZipEntry entry = zipFile.getEntry(Config.UPDATE_CONF_PATH_IN_UPDATE_ZIP);
            if(entry == null) {
                throw new IOException("can not found update config in update zip file: " + Config.UPDATE_CONF_PATH_IN_UPDATE_ZIP);
            } else {
                InputStream stream = zipFile.getInputStream(entry);
                this.updateConfig = new UpdateConfig(FileUtils.readStreamAsString(stream));
                if(updateConfig.isUpdateNotSupported()) {
                    throw new NotSupportedVersionException("This version is too old to support update. Please update from some app store.");
                }
                JSONArray actions = this.updateConfig.getUpdateActions();
                if(actions != null) {
                    PatchProgressListener lis = new PatchProgressListener() {
                        @Override
                        public void onPatchProgress(String currentFile) {
                            UpdateManager.this.progressListener.onPatchProgress(currentFile);
                        }
                    };
                    List<UpdateAction> updateActions = new ArrayList<UpdateAction>();
                    List<String> files = new ArrayList<String>();
                    for (int i = 0; i < actions.length(); i++) {
                        UpdateAction a = UpdateActionManager.createAction(zipFile, actions.get(i), lis);
                        updateActions.add(a);
                        files.add(a.getFileToHandlePath());
                    }
                    progressListener.beginPatch(context, files);
                    for (UpdateAction action : updateActions) {
                        action.exec();
                        progressListener.onPatchEnd(action.getFileToHandlePath());
                    }
                }
                progressListener.onPatchEnd();
                Config.setUpdated(updateConfig.getVersion());
                Config.flushUpdateInfo();
            }
        } finally {
            if(zipFile != null) {
                try{
                    zipFile.close();
                } catch(IOException e) {
                    Log.w(Config.LOG_TAG, "close zip file failed", e);
                }
            }
        }
    }

    public void clearTmpDir() throws IOException {
        FileUtils.emptyDir(Config.getIncUpdateTmpDirPath());
    }
    
}
