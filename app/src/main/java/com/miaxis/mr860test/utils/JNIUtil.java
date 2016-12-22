package com.miaxis.mr860test.utils;

/**
 * Created by xu.nan on 2016/12/22.
 */

public class JNIUtil {

    static {
        System.loadLibrary("dewlt2-jni");
    }

    public static native int wlt2bmp(byte[] var0, byte[] var1, int var2);
}
