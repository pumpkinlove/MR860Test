package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2016/12/22.
 */

public class CommonEvent {

    private int code;
    private int result;
    private String content;

    public CommonEvent() {
    }

    public CommonEvent(int code, int result, String content) {
        this.code = code;
        this.result = result;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
