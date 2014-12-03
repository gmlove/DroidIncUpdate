package com.comeplus.droidincupdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.comeplus.droidincupdate.utils.FileUtils;

public class ProgressListener {
    long totalLength = 0;
    long extractedLength = 0;
    List<String> filesToExtract = new ArrayList<String>();
    List<String> extractedFiles = new ArrayList<String>();
    
    List<String> filesToPatch = new ArrayList<String>();
    List<String> patchedFiles = new ArrayList<String>();
    
    UpdateProgressListener target = null;
    
    public ProgressListener(UpdateProgressListener target) {
        this.target = target;
    }
    
    void beginExtract(Context context, String[] assetDirs) throws IOException {
        this.totalLength = 0;
        this.totalLength = FileUtils.duAssetsDir(context, Config.INCUPDATE_RES_DIR_FROM_ASSETS);
        this.totalLength += FileUtils.duAssetsDir(context, Config.INCUPDATE_LIBS_DIR_FROM_ASSETS);
        for(String assetsDir: assetDirs) {
            FileUtils.scanAssetDir(context, assetsDir, this.filesToExtract);
        }
    }
    
    public void onExtractProgress(String currentFile, long currentFileLength, long currentFileExtractedLength) {
        if(target == null) {
            return;
        }
        target.onExtractProgress(totalLength, extractedLength, filesToExtract, extractedFiles, currentFile, currentFileLength, currentFileExtractedLength);
    }

    public void onExtractEnd(String currentFile) {
        this.extractedFiles.add(currentFile);
    }
    
    public void onDownloadProgress(long totalLength, long downloadedLength) {
        if(target != null) {
            target.onDownloadProgress(totalLength, downloadedLength);
        }
    }

    void beginPatch(Context context, List<String> files) {
        this.filesToPatch.addAll(files);
    }
    
    public void onPatchProgress(String currentFile) {
        if(target != null) {
            target.onPatchProgress(filesToPatch, patchedFiles, currentFile);
        }
    }
    
    public void onPatchEnd(String file) {
        this.patchedFiles.add(file);
    }

    public void onExtractEnd() {
        if(target == null) {
            return;
        }
        target.onExtractEnd();
    }
    
    public void onDownloadEnd() {
        if(target == null) {
            return;
        }
        target.onDownloadEnd();
    }

    public void onPatchEnd() {
        if(target == null) {
            return;
        }
        target.onPatchEnd();
    }
}