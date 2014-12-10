package com.comeplus.droidincupdate.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.comeplus.droidincupdate.Config;
import com.comeplus.droidincupdate.ProgressListener;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.StatFs;
import android.util.Log;

public class FileUtils {

    public static interface FileProgressListener {
        void onWrite(long len);
    }
    
    public static long duAssetsDir(Context context, String assetsDir) throws IOException {
        if(Config.DEBUG) {
            Log.d(Config.LOG_TAG, "duAssetsDir: " + assetsDir);
        }
        long length = 0;
        AssetManager am = context.getAssets();
        try {
            length += am.open(assetsDir).available();
        } catch (FileNotFoundException e) {
            for (String f : am.list(assetsDir)) {
                length += duAssetsDir(context, assetsDir + File.separator + f);
            }
        }
        return length;
    }

    public static long duDir(String dirName) {
        long len = 0;
        File f = new File(dirName);
        if (f.isDirectory()) {
            for (String sf : f.list()) {
                len += duDir(f.getAbsolutePath() + File.separator + sf);
            }
        } else {
            len += f.length();
        }
        return len;
    }

    public static long availableSpace(String path) {
        StatFs statFs = new StatFs(path);
        long free = statFs.getAvailableBlocks() * statFs.getBlockSize();
        return free;
    }

    public static void writeFile(InputStream is, String filePath) throws IOException {
        writeFile(is, filePath, null);
    }

    public static void writeFile(InputStream is, String filePath, final FileProgressListener progressListener) throws IOException {
        if(Config.DEBUG){
            Log.d(Config.LOG_TAG, String.format("writeFile filePath=%s", filePath));
        }
        FileOutputStream fos = null;
        try {
            File f = createFileIfNotExist(filePath);
            fos = new FileOutputStream(f);
            IOUtils.PipeProgressListener lis = null;
            if(progressListener != null) {
                lis = new IOUtils.PipeProgressListener() {
                    @Override
                    public void onPipe(long piped) {
                        progressListener.onWrite(piped);
                    }
                };
            }
            IOUtils.pipe(is, fos, lis);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }
    }

    public static void writeStringToFile(String filePath, String content) throws IOException {
        writeFile(new ByteArrayInputStream(content.getBytes("utf8")), filePath);
    }

    public static void scanAssetDir(Context context, String assetsDir, List<String> result) throws IOException {
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(assetsDir);
            IOUtils.closeQuietly(is);
            throw new IOException("assetsDir is not directory: " + assetsDir);
        } catch (FileNotFoundException e) {
            // must be a directory
            for (String fn : am.list(assetsDir)) {
                String spath = assetsDir + File.separator + fn;
                try {
                    am.open(spath);
                    result.add(spath);
                } catch (FileNotFoundException e1) {
                    // must be a directory
                    scanAssetDir(context, spath, result);
                }
            }
        }
    }
    
    public static void copyFromAssetsDir(Context context, String assetsDir, String destDirPath, final ProgressListener progressListener) throws IOException {
        if(Config.DEBUG){
            Log.d(Config.LOG_TAG, String.format("copyFromAssetsDir assetsDir=%s, destDir=%s", assetsDir, destDirPath));
        }
        AssetManager am = context.getAssets();
        File destDir = new File(destDirPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
            if (!destDir.exists()) {
                throw new IOException("create directory failed: " + destDir);
            }
        }
        if (!destDir.isDirectory()) {
            throw new IOException("create directory failed file exists: " + destDir);
        }
        try {
            InputStream is = am.open(assetsDir);
            IOUtils.closeQuietly(is);
            throw new IOException("assetsDir is not directory: " + assetsDir);
        } catch (FileNotFoundException e) {
            // must be a directory
            for (String fn : am.list(assetsDir)) {
                final String spath = assetsDir + File.separator + fn;
                String dpath = destDir + File.separator + fn;
                try {
                    InputStream is1 = am.open(spath);
                    final int fileLen = is1.available();
                    FileProgressListener lis = null;
                    if(progressListener != null) {
                        lis = new FileProgressListener() {
                            @Override
                            public void onWrite(long len) {
                                //progressListener.onExtractProgress(spath, fileLen, len);
                            }
                        };
                    }
                    writeFile(is1, dpath, lis);
                    progressListener.onExtractEnd(spath);
                } catch (FileNotFoundException e1) {
                    // must be a directory
                    copyFromAssetsDir(context, spath, dpath, progressListener);
                }
            }
        }
    }

    public static String readStreamAsString(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        try{
            IOUtils.pipe(is, bos, null);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return bos.toString("utf8");
    }
    
    public static String readFileAsString(String filePath) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            return null;
        } else {
            return readStreamAsString(new FileInputStream(f));
        }
    }

    public static File createFileIfNotExist(String filePath) throws IOException {
        File f = new File(filePath);
        ensureDir(f.getParentFile().getAbsolutePath());
        if (!f.exists()) {
            if (!f.createNewFile()) {
                throw new IOException("create file failed: " + filePath);
            }
        } else if (f.isDirectory()) {
            throw new IOException("create file failed, file is directory: " + filePath);
        }
        return f;
    }

    public static void ensureDir(String path) throws IOException {
        File f = new File(path);
        if(!f.exists()) {
            f.mkdirs();
            if(!f.exists()) {
                throw new IOException("create directory failed: " + f.getAbsolutePath());
            }
            return;
        }
        if(!f.isDirectory()) {
            throw new IOException("create directory failed, path is file: " + f.getAbsolutePath());
        }
    }

    public static void emptyDir(String path) throws IOException {
        File f = new File(path);
        if(!f.exists()) {
            return;
        }
        if(f.isFile()) {
            throw new RuntimeException("file is not directory: " + path);
        }
        for(File sub: f.listFiles()) {
            if(sub.isDirectory()) {
                emptyDir(sub.getAbsolutePath());
            }
            if(!sub.delete()) {
                throw new IOException("file delete failed: " + sub.getAbsolutePath());
            }
        }
    }
    
    public static void unzip(InputStream zipFileInputStream, String destDir) throws IOException {
        ZipInputStream zis = null;
        String filename = null;
        ZipEntry ze = null;
        try {
            ensureDir(destDir);
            zis = new ZipInputStream(new BufferedInputStream(zipFileInputStream));
            byte[] buffer = new byte[1024];
            int count = -1;
            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                if (ze.isDirectory()) {
                    ensureDir(destDir + File.separator + filename);
                    continue;
                }
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(destDir + File.separator + filename);
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                } finally {
                    IOUtils.closeQuietly(fout);
                    zis.closeEntry();
                }
            }
        } finally {
            IOUtils.closeQuietly(zis);
        }
    }
}
