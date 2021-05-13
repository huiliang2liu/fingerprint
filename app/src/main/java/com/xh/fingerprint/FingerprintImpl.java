package com.xh.fingerprint;

import android.util.Log;

public class FingerprintImpl implements IFingerprint {
    private static final String TAG = FingerprintImpl.class.getSimpleName();
    private IFingerprint mFingerprint;

    FingerprintImpl(IFingerprint fingerprint) {
        this.mFingerprint = fingerprint;
    }

    @Override
    public boolean isHardwareDetected() {
        return mFingerprint.isHardwareDetected();
    }

    @Override
    public void authenticate(FingerprintListener listener) {
        if (listener == null) {
            Log.d(TAG, "listener is null");
            return;
        }
        mFingerprint.authenticate(listener);
    }
}
