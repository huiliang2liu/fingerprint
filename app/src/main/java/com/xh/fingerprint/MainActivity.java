package com.xh.fingerprint;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IFingerprint fingerprint=new IFingerprint.Builder(getApplicationContext()).build();
        fingerprint.authenticate(new FingerprintListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"onSuccess:");
            }

            @Override
            public void onFailed() {
                Log.d(TAG,"onFailed");
            }
        });
    }


}