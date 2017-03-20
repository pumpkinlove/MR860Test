package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/3/20.
 */

public class OpenBlueToothEvent {
    private boolean flag;

    public OpenBlueToothEvent(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
