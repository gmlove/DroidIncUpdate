package com.comeplus.droidincupdate.demo;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;

import com.comeplus.droidincupdate.UpdateManager;

public class UpdateActivity extends Activity {

    public UpdateActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    new UpdateManager(UpdateActivity.this, null);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    
    
}
