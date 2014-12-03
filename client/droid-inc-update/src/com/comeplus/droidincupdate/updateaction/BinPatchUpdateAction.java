package com.comeplus.droidincupdate.updateaction;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.comeplus.droidincupdate.BSPatch;
import com.comeplus.droidincupdate.Config;
import com.comeplus.droidincupdate.utils.FileUtils;
import com.comeplus.droidincupdate.utils.MD5;

public class BinPatchUpdateAction extends BaseUpdateAction {

    public BinPatchUpdateAction(ZipFile zipFile, JSONObject conf, PatchProgressListener lis) throws JSONException {
        super(zipFile, conf, lis);
    }

    @Override
    public void exec() throws IOException {
        // unzip patch file
        String tmpFilePath = Config.getIncUpdateTmpDirPath() + File.separator + newFilePathFromZip;
        File tmpFile = new File(tmpFilePath);
        FileUtils.ensureDir(tmpFile.getParentFile().getAbsolutePath());
        FileUtils.writeFile(zipFile.getInputStream(entryToHandle), tmpFilePath);
        
        // find source file, and rename to fileToHandle if needed
        String fileToHandle = getFileToHandlePath();
        String sfile = findFile(new String[]{fileToHandle, fileToHandle + ".backup", fileToHandle + ".patched"},
                sourceFileMd5);
        if(sfile == null) {
            throw new IOException("can not find source file md5 match " + sourceFileMd5);
        }
        File file = new File(fileToHandle);
        if(!sfile.equals(fileToHandle)) {
            if(file.exists()) {
                if(!file.delete()) {
                    throw new IOException("delete file failed: " + file.getAbsolutePath());
                }
            }
            if(!new File(sfile).renameTo(file)) {
                throw new IOException("rename file failed: " + sfile);
            }
        }
        file = new File(getFileToHandlePath());
        
        // patch file
        BSPatch.bspatch(fileToHandle, fileToHandle + ".patched", tmpFilePath);
        Log.d(Config.LOG_TAG, String.format("bspatch [%s & %s] -> %s", fileToHandle, tmpFilePath, fileToHandle + ".patched"));
        
        // delete file.backup if exists, rename file->file.backup file.patched->file
        File bakFile = new File(fileToHandle + ".backup");
        if(bakFile.exists()) {
            if(!bakFile.delete()){
                throw new IOException("delete backup file failed: " + bakFile.getAbsolutePath());
            }
        }
        if(!file.renameTo(bakFile)){
            throw new IOException("rename file failed: " + file.getAbsolutePath());
        }
        if(!new File(fileToHandle + ".patched").renameTo(new File(fileToHandle))){
            throw new IOException("rename file failed: " + fileToHandle + ".patched");
        }
        if(this.lis != null) {
            this.lis.onPatchProgress(fileToHandle);
        }

        Log.d(Config.LOG_TAG, "patch file success: " + fileToHandle);
        if(Config.DEBUG) {
            String md5 = MD5.caclFileMd5(fileToHandle);
            Log.d(Config.LOG_TAG, "new file md5: " + md5);
            if(!md5.equals(finalFileMd5)) {
                Log.e(Config.LOG_TAG, "error occured, md5 not match: " + md5 + "!=" + finalFileMd5);
            }
        }
    }
    
    private String findFile(String[] files, String version) throws IOException {
        for(String f: files) {
            if(!new File(f).exists()) {
                if(Config.DEBUG) {
                    Log.d(Config.LOG_TAG, "findFile file not exist: " + f);
                }
                continue;
            }
            String md5 = MD5.caclFileMd5(f);
            if(Config.DEBUG) {
                Log.d(Config.LOG_TAG, String.format("find file: file=%s, version=%s, targetVersion=%s", f, md5, version));
            }
            if(md5.equals(version)) {
                return f;
            }
        }
        return null;
    }
}
