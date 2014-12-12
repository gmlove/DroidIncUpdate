package org.cocos2dx.lib;

import java.io.File;

import android.util.Log;

public class Config {

    private static boolean useFileInsteadOfAsset = false;
    private static String resDirName = null;
    private static String assetsResPath = null;
    public static final String LOG_TAG = "libcocos2d";
    
    public static String fixPath(String path) {
        if(useFileInsteadOfAsset && path.charAt(0) != '/') {
            String fixed = resDirName + File.separator + path;
            Log.d(LOG_TAG, String.format("fixPath: path=%s, fixed=%s", path, fixed));
            return fixed;
        }
        return path;
    }

    public static void init(boolean useFileInsteadOfAsset, String resDirName, String assetsResPath) {
        Config.useFileInsteadOfAsset = useFileInsteadOfAsset;
        Config.resDirName = resDirName;
        Config.assetsResPath = assetsResPath;
        Log.d(LOG_TAG, String.format("resDirName: %s, assetsResPath: %s", resDirName, assetsResPath));
        if(resDirName.length() != 0 && resDirName.charAt(resDirName.length()-1) != '/') {
            resDirName = resDirName + '/';
        }
        if(assetsResPath.length() != 0 && assetsResPath.charAt(assetsResPath.length()-1) != '/') {
            assetsResPath = assetsResPath + '/';
        }
        if(useFileInsteadOfAsset) {
            Log.d(LOG_TAG, String.format("useAssets: false, resPath: %s", resDirName));
            Config.initPath(false, resDirName);
        } else {
            Log.d(LOG_TAG, String.format("useAssets: true, resPath: %s", assetsResPath));
            Config.initPath(true, assetsResPath);
        }
    }
    
    private static native void initPath(boolean useAssets, String resPath);
    
}
