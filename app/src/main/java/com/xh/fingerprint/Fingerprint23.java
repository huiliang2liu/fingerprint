package com.xh.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

@TargetApi(Build.VERSION_CODES.M)
public class Fingerprint23 implements IFingerprint {

    FingerprintManager mManager;


    private CancellationSignal mCancellationSignal;

    Fingerprint23(Context context) {
        mManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        if (mManager == null)
            return;
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
                listener.onFailed();
            }
        });
        mManager.authenticate(Utils.buildCryptoObject(), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                listener.onFailed();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);

            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                listener.onSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                listener.onFailed();
            }
        }, null);
    }


}
