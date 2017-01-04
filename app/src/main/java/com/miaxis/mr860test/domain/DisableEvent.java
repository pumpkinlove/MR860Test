package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/4.
 */

public class DisableEvent {
    boolean flag;

    public DisableEvent() {
    }

    public DisableEvent(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
