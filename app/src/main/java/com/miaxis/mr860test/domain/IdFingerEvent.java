package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/17.
 */

public class IdFingerEvent {

    private String finger0;
    private String finger1;

    public IdFingerEvent() {
    }

    public IdFingerEvent(String finger0, String finger1) {
        this.finger0 = finger0;
        this.finger1 = finger1;
    }

    public String getFinger0() {
        return finger0;
    }

    public void setFinger0(String finger0) {
        this.finger0 = finger0;
    }

    public String getFinger1() {
        return finger1;
    }

    public void setFinger1(String finger1) {
        this.finger1 = finger1;
    }
}
