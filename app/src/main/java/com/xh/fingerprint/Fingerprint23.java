package com.xh.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

@TargetApi(Build.VERSION_CODES.M)
public class Fingerprint23 extends AbsFingerprint {


    private CancellationSignal mCancellationSignal;

    Fingerprint23(Context context) {
        super(context);
        if (isHardwareDetected())
            mCancellationSignal = new CancellationSignal();
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
        mManager.authenticate(Utils.buildCryptoObject(encrypt(),iv()), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
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
                authenticate(listener, result.getCryptoObject().getCipher());
//                listener.onSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                listener.onFailed();
            }
        }, null);
    }


}
