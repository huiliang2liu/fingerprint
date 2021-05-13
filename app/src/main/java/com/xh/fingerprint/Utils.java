package com.xh.fingerprint;

import android.annotation.TargetApi;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.lang.reflect.Method;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class Utils {
    private static final String KEY_NAME = "com.createchance.android.sample.fingerprint_authentication_key";

    // We always use this keystore on Android.
    private static final String KEYSTORE_NAME = "AndroidKeyStore";

    // Should be no need to change these values.
    private static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = KEY_ALGORITHM + "/" +
            BLOCK_MODE + "/" +
            ENCRYPTION_PADDING;
    private static KeyStore mKeyStore;

    private static boolean runHide = false;

    static {
        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
            mKeyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runHide() {
        if (runHide)
            return;
        runHide = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                Method forName = Class.class.getDeclaredMethod("forName", String.class);
                Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
                Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
                Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
                Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
                Object sVmRuntime = getRuntime.invoke(null);
                setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{new String[]{"L"}});
            } catch (Throwable e) {
                Log.e("[error]", "reflect bootstrap failed:", e);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static FingerprintManager.CryptoObject buildCryptoObject() {
        Cipher cipher = createCipher(true);
        if (cipher == null)
            return null;
        return new FingerprintManager.CryptoObject(cipher);
    }

    @TargetApi(Build.VERSION_CODES.P)
    public static BiometricPrompt.CryptoObject buildCryptoObject1() {
        Cipher cipher = createCipher(true);
        if (cipher == null)
            return null;
        return new BiometricPrompt.CryptoObject(cipher);
    }

    private static Cipher createCipher(boolean retry) {
        Cipher cipher = null;
        try {
            Key key = GetKey();
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            try {
                mKeyStore.deleteEntry(KEY_NAME);
                if (retry)
                    createCipher(false);
            } catch (KeyStoreException keyStoreException) {
                keyStoreException.printStackTrace();
                return null;
            }
        }
        return cipher;
    }

    private static Key GetKey() throws Exception {
        Key secretKey;
        if (!mKeyStore.isKeyEntry(KEY_NAME)) {
            CreateKey();
        }

        secretKey = mKeyStore.getKey(KEY_NAME, null);
        return secretKey;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void CreateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }
}
