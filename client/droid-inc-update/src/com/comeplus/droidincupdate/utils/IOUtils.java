package com.comeplus.droidincupdate.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.comeplus.droidincupdate.Config;

public class IOUtils {
    
    public static interface PipeProgressListener {
        void onPipe(long piped);
    }
    
    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                Log.w(Config.LOG_TAG, "close closeable failed: ", e);
            }
        }
    }

    public static void pipe(InputStream is, OutputStream os, int bufSize, PipeProgressListener progressListener) throws IOException {
        byte[] buffer = new byte[bufSize];
        long piped = 0;
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            if (os != null) {
                os.write(buffer, 0, len);
            }
            if(progressListener != null) {
                piped += len;
                //progressListener.onPipe(piped);
            }
        }
    }

    public static void pipe(InputStream is, OutputStream os, PipeProgressListener progressListener) throws IOException {
        pipe(is, os, 1024, progressListener);
    }

}
