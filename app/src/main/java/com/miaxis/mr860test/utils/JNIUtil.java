package com.miaxis.mr860test.utils;

/**
 * Created by xu.nan on 2016/12/22.
 */

public class JNIUtil {

    static {
        System.loadLibrary("wlt2bmp");
    }

    public static native int unpack(byte[] var0, byte[] var1, int var2);
}
