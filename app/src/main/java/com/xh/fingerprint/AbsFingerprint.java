package com.xh.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;

@TargetApi(Build.VERSION_CODES.M)
public abstract class AbsFingerprint implements IFingerprint {
    private static final String TAG = AbsFingerprint.class.getSimpleName();
    protected Context mContext;
    protected FingerprintManager mManager;
    private String token = "";
    private String iv;

    AbsFingerprint(Context context) {
        this.mContext = context;
        mManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
    }

    @Override
    public  boolean isHardwareDetected() {
        return mManager == null ? false : mManager.isHardwareDetected() && mManager.hasEnrolledFingerprints();
    }

    protected boolean encrypt() {
        return token == null || token.isEmpty();
    }

    protected byte[] iv() {
        return encrypt() ? null : Base64.decode(iv, Base64.URL_SAFE);
    }


    protected final void authenticate(FingerprintListener listener, Cipher cipher) {
        listener.onSuccess();
        try {
            if (token == null || token.isEmpty()) {//加密
                byte[] encrypted = cipher.doFinal(Base64.encode("1111111111111111".getBytes(), 0));
                byte[] IV = cipher.getIV();
                token = Base64.encodeToString(encrypted, Base64.URL_SAFE);
//            String se="";
                iv = Base64.encodeToString(IV, Base64.URL_SAFE);
                Log.d(TAG, "se:" + token + " ,siv:" + iv);
            } else {//解密
                byte[] decrypted = cipher.doFinal(Base64.decode(token, Base64.URL_SAFE));
                Log.d(TAG, "解密:" + new String(Base64.decode(decrypted,0)));
            }
//            cipher.init(Cipher.ENCRYPT_MODE,Utils.GetKey());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
