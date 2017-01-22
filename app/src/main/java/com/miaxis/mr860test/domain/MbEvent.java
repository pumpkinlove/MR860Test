package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/20.
 */

public class MbEvent {

    private int mbFlag;
    private int imgResult;
    private int mbResult;
    private String message;

    public MbEvent() {
    }

    public MbEvent(int mbFlag, int imgResult, int mbResult, String message) {
        this.mbFlag = mbFlag;
        this.imgResult = imgResult;
        this.mbResult = mbResult;
        this.message = message;
    }

    public int getMbFlag() {
        return mbFlag;
    }

    public void setMbFlag(int mbFlag) {
        this.mbFlag = mbFlag;
    }

    public int getImgResult() {
        return imgResult;
    }

    public void setImgResult(int imgResult) {
        this.imgResult = imgResult;
    }

    public int getMbResult() {
        return mbResult;
    }

    public void setMbResult(int mbResult) {
        this.mbResult = mbResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
