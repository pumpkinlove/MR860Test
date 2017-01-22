package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/20.
 */

public class ScrollMessageEvent {
    private String message;

    public ScrollMessageEvent() {
    }

    public ScrollMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
