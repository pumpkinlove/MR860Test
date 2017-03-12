package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/4.
 */

public class DisableEvent {
    boolean flag;
    boolean flag2;
    boolean flag3;
    boolean flag4;


    public DisableEvent() {
    }

    public DisableEvent(boolean flag) {
        this.flag = flag;
    }

    public DisableEvent(boolean flag, boolean flag2) {
        this.flag = flag;
        this.flag2 = flag2;
    }

    public DisableEvent(boolean flag, boolean flag2, boolean flag3) {
        this.flag = flag;
        this.flag2 = flag2;
        this.flag3 = flag3;
    }

    public DisableEvent(boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        this.flag = flag;
        this.flag2 = flag2;
        this.flag3 = flag3;
        this.flag4 = flag4;
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

    public boolean isFlag3() {
        return flag3;
    }

    public void setFlag3(boolean flag3) {
        this.flag3 = flag3;
    }

    public boolean isFlag4() {
        return flag4;
    }

    public void setFlag4(boolean flag4) {
        this.flag4 = flag4;
    }
}
