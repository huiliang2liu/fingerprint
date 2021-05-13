package com.xh.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.P)
public class Fingerprint28 implements IFingerprint {
    private static final String TAG = Fingerprint28.class.getSimpleName();
    private BiometricPrompt mBiometricPrompt;
    private CancellationSignal mCancellationSignal;
    private FingerprintManager mManager;
    private Context mContext;

    Fingerprint28(Context context) {
        mContext = context;
        mManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        if (!isHardwareDetected())
            return;
        mBiometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle("指纹测试")
                .setDescription("描述")
                .setNegativeButton("取消", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "cancel button clicked");
                    }
                }).build();
        mCancellationSignal = new CancellationSignal();
    }

    @Override
    public boolean isHardwareDetected() {
        return mManager == null ? false : mManager.isHardwareDetected();
    }

    @Override
    public void authenticate(FingerprintListener listener) {
        if (!isHardwareDetected()) {
            listener.onFailed();
            return;
        }
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                Log.d(TAG, "canceled");
                listener.onFailed();
            }
        });
        mBiometricPrompt.authenticate(Utils.buildCryptoObject1(), mCancellationSignal, mContext.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d(TAG, "onAuthenticationError:" + errorCode);
                listener.onFailed();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                Log.d(TAG, "onAuthenticationHelp:" + helpString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "onAuthenticationSucceeded:" + result.toString());
                listener.onSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "onAuthenticationFailed");
                listener.onFailed();
            }
        });
    }
}
