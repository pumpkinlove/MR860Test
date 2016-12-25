package com.miaxis.mr860test.domain;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class ToastEvent {
    String message;

    public ToastEvent() {
    }

    public ToastEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
