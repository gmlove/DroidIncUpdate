package com.comeplus.droidincupdate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.comeplus.droidincupdate.utils.FileUtils;
import com.comeplus.droidincupdate.utils.IOUtils;

import android.content.Context;
import android.util.Log;

public class Config {
    public static String LOG_TAG = "DroidIncUpdate";
    public static String PACKAGE_NAME = null;
    public static boolean DEBUG = true;
    public static boolean FORCE_EXTRACT = false;

    public static String INCUPDATE_LIBS_DIR_FROM_ASSETS = "incupdatelibs";
    public static String INCUPDATE_RES_DIR_FROM_ASSETS = "res";
    public static String INCUPDATE_CONFIG_FILE_FROM_ASSETS = "incupdate.conf";

    public static String INCUPDATE_PARENT_DIR = null; // should set this dir
    // relative paths
    public static String INCUPDATE_BASE_DIR = "incupdate";
    public static String INCUPDATE_LIBS_DIR = INCUPDATE_BASE_DIR + File.separator + "libs";
    public static String INCUPDATE_RES_DIR = INCUPDATE_BASE_DIR + File.separator + "assets_res";
    public static String INCUPDATE_TMP_DIR = INCUPDATE_BASE_DIR + File.separator + "tmp";

    public static String UPDATE_INFO_FILE_PATH = INCUPDATE_BASE_DIR + File.separator + "update";
    private static JSONObject updateInfo = null;
    private static JSONObject assetUpdateInfo = null;

    public static String UPDATE_URL = "http://172.16.11.8:38000/incupdate/update";
    public static String UPDATE_CONF_PATH_IN_UPDATE_ZIP = "updateConf.json";
    public static float minAvailSpaceRatio = 3f;

    /**
     * @param context
     * @throws IOException should not happen, or client tools must have bugs
     * @throws JSONException should not happen, or client tools must have bugs
     */
    public static void init(Context context) throws IOException, JSONException {
        INCUPDATE_PARENT_DIR = context.getFilesDir().getAbsolutePath();
        // read assetUpdateInfo
        String assetUpdateInfoRaw = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(INCUPDATE_CONFIG_FILE_FROM_ASSETS);
            PACKAGE_NAME = context.getPackageName();
            assetUpdateInfoRaw = FileUtils.readStreamAsString(is);
            if(DEBUG) {
                Log.d(LOG_TAG, "assetUpdateInfoRaw: " + assetUpdateInfoRaw);
            }
            if (assetUpdateInfoRaw == null) {
                throw new IOException("config file in assets not found: " + INCUPDATE_CONFIG_FILE_FROM_ASSETS);
            } else {
                assetUpdateInfo = new JSONObject(assetUpdateInfoRaw);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
        // read updateInfo
        String updateInfoRaw = null;
        try {
            updateInfoRaw = FileUtils.readFileAsString(getIncUpdateUpdateInfoFilePath());
            if(DEBUG) {
                Log.d(LOG_TAG, "updateInfoRaw: " + updateInfoRaw);
            }
            if (updateInfoRaw == null) {
                updateInfo = new JSONObject();
            } else {
                updateInfo = new JSONObject(updateInfoRaw);
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "new JSONObject failed from string" + updateInfoRaw);
            updateInfo = new JSONObject();
        }
    }

    public static String getIncUpdateLibsDir() {
        return INCUPDATE_PARENT_DIR + File.separator + INCUPDATE_LIBS_DIR;
    }

    public static String getIncUpdateResDir() {
        return INCUPDATE_PARENT_DIR + File.separator + INCUPDATE_RES_DIR;
    }

    public static String getIncUpdateUpdateInfoFilePath() {
        return INCUPDATE_PARENT_DIR + File.separator + UPDATE_INFO_FILE_PATH;
    }

    public static String getIncUpdateLibFilePath(String filePath) {
        return INCUPDATE_PARENT_DIR + File.separator + INCUPDATE_LIBS_DIR + File.separator + filePath;
    }
    
    public static String getIncUpdateResFilePath(String filePath) {
        return INCUPDATE_PARENT_DIR + File.separator + INCUPDATE_RES_DIR + File.separator + filePath;
    }
    
    public static String getIncUpdateTmpDirPath() {
        return INCUPDATE_PARENT_DIR + File.separator + INCUPDATE_TMP_DIR;
    }
    
    private static Object getFromUpdateInfo(String key) {
        return updateInfo.opt(key);
    }

    private static Object getFromAssetUpdateInfo(String key) {
        return assetUpdateInfo.opt(key);
    }

    public static long getAssetsLibSize() {
        Object size = getFromAssetUpdateInfo("libs_size");
        return size != null ? (Integer) size : 0;
    }

    public static long getAssetsResSize() {
        Object size = getFromAssetUpdateInfo("res_size");
        return size != null ? (Integer) size : 0;
    }

    static boolean isExtracted() {
        Object assetVersion = getFromUpdateInfo("asset_version");
        Object versionInAsset = getFromAssetUpdateInfo("version");
        if(DEBUG) {
            Log.d(LOG_TAG, String.format("isExtracted: assetVersion=%s, versionInAsset=%s", assetVersion, versionInAsset));
        }
        if(assetVersion == null) {
            return false;
        }
        if(versionInAsset == null) {
            throw new RuntimeException("must have version in asset config file.");
        }
        return ((String)assetVersion).equals((String)versionInAsset);
    }

    static void setExtracted() {
        try {
            Object versionInAsset = getFromAssetUpdateInfo("version");
            updateInfo.put("asset_version", versionInAsset);
            updateInfo.put("version", versionInAsset);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "put value to json failed.", e);
            throw new RuntimeException(e);
        }
    }

    static void setUpdated(String version) {
        try {
            updateInfo.put("version", version);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "put value to json failed.", e);
            throw new RuntimeException(e);
        }
    }
    
    static String getResVersion() {
        Object version = getFromUpdateInfo("version");
        if(version == null) {
            Object assetVersion = getFromUpdateInfo("asset_version");
            if(assetVersion == null) {
                throw new RuntimeException("no version or asset_version exists, may be resource not extracted yet");
            }
            return (String) assetVersion;
        } else {
            return (String) version;
        }
    }
    
    static void flushUpdateInfo() throws IOException {
        if(DEBUG) {
            Log.d(LOG_TAG, "update file: " + updateInfo.toString());
        }
        FileUtils.writeStringToFile(getIncUpdateUpdateInfoFilePath(), updateInfo.toString());
    }
    
    
    public static native int load(String libpath);
    
    public static native int unload(String libpath);
}
