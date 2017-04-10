package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/20.
 */

public class ScrollMessageEvent {
    private CharSequence message;
    private CharSequence type;

    public ScrollMessageEvent() {
    }

    public ScrollMessageEvent(CharSequence message) {
        this.message = message;
    }

    public ScrollMessageEvent(CharSequence message, CharSequence type) {
        this.message = message;
        this.type = type;
    }

    public void setMessage(CharSequence message) {
        this.message = message;
    }

    public CharSequence getType() {
        return type;
    }

    public void setType(CharSequence type) {
        this.type = type;
    }

    public CharSequence getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
