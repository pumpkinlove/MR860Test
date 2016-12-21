package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2016/12/19.
 */

public class ResultEvent {
    private int id;

    private int status;

    public ResultEvent(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
