package com.ivsign.android.IDCReader;

public class IDCReaderSDK {

    private static final byte[] byLicData = new byte[]{5, 0, 1, 0, 91, 3, 51, 1, 90, -77, 30, 0};

    private IDCReaderSDK() {
    }

    public static native int wltInit(String licPath);

    public static int unpack(byte[] wltData) {
        return wltGetBMP(wltData, byLicData);
    }

    public static native int wltGetBMP(byte[] wltData, byte[] byLicData);

    static {
        System.loadLibrary("wltdecode");
    }
}