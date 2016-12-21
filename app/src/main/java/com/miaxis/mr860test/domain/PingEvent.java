package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2016/12/21.
 */

public class PingEvent {

    public PingEvent() {
    }

    public PingEvent(String content) {
        this.content = content;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
