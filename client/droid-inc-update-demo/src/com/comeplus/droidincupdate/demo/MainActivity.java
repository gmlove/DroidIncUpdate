package com.comeplus.droidincupdate.demo;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comeplus.droidincupdate.Config;
import com.comeplus.droidincupdate.DownloadException;
import com.comeplus.droidincupdate.NotSupportedVersionException;
import com.comeplus.droidincupdate.UpdateManager;
import com.comeplus.droidincupdate.UpdateProgressListener;
import com.comeplus.droidincupdate.utils.FileUtils;

public class MainActivity extends Activity
{
    protected static final int UPDATE_MSG = 1;
    private String libPath = null;
    private TextView progressText = null;
    private TextView libFuncText = null;
    
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case UPDATE_MSG:
                progressText.setText((String)msg.obj);
                break;
            }
            super.handleMessage(msg);
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        System.loadLibrary("loader");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        progressText = new TextView(this);
        libFuncText = new TextView(this);
        layout.addView(progressText);
        layout.addView(libFuncText);
        this.setContentView(layout);

        new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
				    UpdateProgressListener lis = new UpdateProgressListener() {
                        
                        @Override
                        public void onPatchProgress(List<String> filesToPatch, List<String> patchedFiles, String currentFile) {
                            Log.d("demo", String.format("onPatchProgress: filesToPatch=%s, patchedFiles=%s, currentFile=%s",
                                    filesToPatch.toArray(), patchedFiles.toArray(), currentFile));
                            Message m = new Message();
                            m.what = UPDATE_MSG;
                            m.obj = String.format("Patching[%d/%d]: %s", patchedFiles.size(), filesToPatch.size(), currentFile);
                            MainActivity.this.handler.sendMessage(m);
                        }
                        
                        @Override
                        public void onPatchEnd() {
                            Log.d("demo", String.format("onPatchEnd"));
                            Message m = new Message();
                            m.what = UPDATE_MSG;
                            m.obj = String.format("Patch finished.");
                            MainActivity.this.handler.sendMessage(m);
                        }
                        
                        @Override
                        public void onExtractProgress(long totalLength, long extractedLength, List<String> filesToExtract,
                                List<String> extractedFiles, String currentFile, long currentFileLength, long currentFileExtractedLength) {
                            Log.d("demo", String.format("onExtractProgress: totalLength=%s, extractedLength=%s, filesToExtract=%s, "
                                    + "extractedFiles=%s, currentFile=%s, currentFileLength=%s, currentFileExtractedLength=%s",
                                    totalLength, extractedFiles.toArray(), filesToExtract.toArray(),
                                    extractedFiles.toArray(), currentFile, currentFileLength, currentFileExtractedLength));
                            Message m = new Message();
                            m.what = UPDATE_MSG;
                            m.obj = String.format("Extracting[%d/%d]: %s", extractedFiles.size(), filesToExtract.size(), currentFile);
                            MainActivity.this.handler.sendMessage(m);
                        }
                        
                        @Override
                        public void onExtractEnd() {
                            Log.d("demo", String.format("onExtractEnd"));
                            Message m = new Message();
                            m.what = UPDATE_MSG;
                            m.obj = String.format("Extract finished.");
                            MainActivity.this.handler.sendMessage(m);
                        }
                        
                        @Override
                        public void onDownloadProgress(long totalLength, long downloadedLength) {
                            Log.d("demo", String.format("onDownloadProgress: totalLength=%s, downloadedLength=%s",
                                    totalLength, downloadedLength));
                            Message m = new Message();
                            m.what = UPDATE_MSG;
                            m.obj = String.format("Downloading: %s%%.", (int)(downloadedLength * 100 / totalLength));
                            MainActivity.this.handler.sendMessage(m);
                        }
                        
                        @Override
                        public void onDownloadEnd() {
                            Log.d("demo", "onDownloadEnd");
                            Message m = new Message();
                            m.what = UPDATE_MSG;
                            m.obj = String.format("Download finished.");
                            MainActivity.this.handler.sendMessage(m);
                        }
                    };
				    Config.FORCE_EXTRACT = true; // use this in test
					UpdateManager um = new UpdateManager(MainActivity.this, lis);
					Log.d("demo", "assets dir[dir] length: " + FileUtils.duAssetsDir(MainActivity.this, "dir"));
					Log.d("demo", "dir[libs] length: " + FileUtils.duDir(MainActivity.this.getDir("libs", Context.MODE_PRIVATE).getAbsolutePath()));
					Log.d("demo", "dir["+Config.getIncUpdateResDir()+"] length: " + FileUtils.duDir(Config.getIncUpdateResDir()));
					Log.d("demo", "dir["+Config.getIncUpdateLibsDir()+"] length: " + FileUtils.duDir(Config.getIncUpdateLibsDir()));
					um.update();
					Log.d("demo", "dir["+Config.getIncUpdateResDir()+"] length: " + FileUtils.duDir(Config.getIncUpdateResDir()));
					Log.d("demo", "dir["+Config.getIncUpdateLibsDir()+"] length: " + FileUtils.duDir(Config.getIncUpdateLibsDir()));
					Log.d("demo", "update info content: " + FileUtils.readFileAsString(Config.getIncUpdateUpdateInfoFilePath()));

					libPath = Config.getIncUpdateLibFilePath("lib1.so");
                    int loadStatus =  MainActivity.this.load(libPath);
                    if(loadStatus != 0) {
                        Log.e("demo", "load lib failed: libpath=" + libPath + ", code=" + loadStatus);
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.callNative(libPath);
                            }
                        });
                    }

				
				} catch (IOException e) {
					Log.e("demo", "io error: ", e);
				} catch (DownloadException e) {
                    Log.e("demo", "download failed: ", e);
                } catch (JSONException e) {
                    Log.e("demo", "json parse error when duAssetsDir: ", e);
                } catch (NotSupportedVersionException e) {
                    Log.e("demo", "not supported version to update: ", e);
                    libPath = Config.getIncUpdateLibFilePath("lib1.so");
                    int loadStatus =  MainActivity.this.load(libPath);
                    if(loadStatus != 0) {
                        Log.e("demo", "load lib failed: libpath=" + libPath + ", code=" + loadStatus);
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.callNative(libPath);
                            }
                        });
                    }
                }
				
			}
		}).start();
        
    }

    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unload(this.libPath);
    }



    public void callNative(String libPath) {
        
        TextView  tv = libFuncText;
        int       x  = 1000;
        int       y  = 42;

        // here, we dynamically load the library at runtime
        // before calling the native method.
        //
        // System.loadLibrary("demo");

        // System.load(libPath);

        int  z = add(x, y);

        tv.setText( "The sum of " + x + " and " + y + " is " + z );
    }
    
    public native int add(int  x, int  y);
    
    public native int load(String libpath);
    
    public native int unload(String libpath);

}
