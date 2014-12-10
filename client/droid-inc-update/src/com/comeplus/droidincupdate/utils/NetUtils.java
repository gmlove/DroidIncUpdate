package com.comeplus.droidincupdate.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.comeplus.droidincupdate.Config;
import com.comeplus.droidincupdate.DownloadException;
import com.comeplus.droidincupdate.ProgressListener;

public class NetUtils {

    public static void downloadFile(String urlStr, String filePathToWrite, ProgressListener lis) throws DownloadException {
        if(Config.DEBUG) {
            Log.d(Config.LOG_TAG, String.format("downloadFile: %s to %s", urlStr, filePathToWrite));
        }
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String msg = "Server returned HTTP " + connection.getResponseCode()
                        + " [" + urlStr + "] " + connection.getResponseMessage();
                Log.d(Config.LOG_TAG, msg);
                throw new IOException("download file failed, server error: " + urlStr);
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(filePathToWrite);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                if(lis != null)
                    lis.onDownloadProgress(fileLength, total);
                output.write(data, 0, count);
            }
        } catch (IOException e) {
            throw new DownloadException(e);
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
            if (connection != null)
                connection.disconnect();
        }
    }
}
