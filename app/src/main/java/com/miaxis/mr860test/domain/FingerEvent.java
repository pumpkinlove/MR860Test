package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/4.
 */

public class FingerEvent {
    private int ivId;
    private byte[] imgBuff;

    public FingerEvent() {
    }

    public FingerEvent(int ivId, byte[] imgBuff) {
        this.ivId = ivId;
        this.imgBuff = imgBuff;
    }

    public int getIvId() {
        return ivId;
    }

    public void setIvId(int ivId) {
        this.ivId = ivId;
    }

    public byte[] getImgBuff() {
        return imgBuff;
    }

    public void setImgBuff(byte[] imgBuff) {
        this.imgBuff = imgBuff;
    }
}
