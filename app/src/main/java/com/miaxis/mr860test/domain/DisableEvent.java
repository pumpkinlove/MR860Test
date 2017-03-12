package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/4.
 */

public class DisableEvent {
    boolean flag;
    boolean flag2;

    public DisableEvent() {
    }

    public DisableEvent(boolean flag) {
        this.flag = flag;
    }

    public DisableEvent(boolean flag, boolean flag2) {
        this.flag = flag;
        this.flag2 = flag2;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag2() {
        return flag2;
    }

    public void setFlag2(boolean flag2) {
        this.flag2 = flag2;
    }
}
