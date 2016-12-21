package com.miaxis.mr860test.domain;

import java.io.Serializable;

/**
 * Created by xu.nan on 2016/12/19.
 */

public class TestItem implements Serializable {

    private int id = 0;
    private String name = "";
    private int status = 0;
    private String opdate = "";
    private String remark = "";

    public TestItem() {
    }

    public TestItem(String name, int status, String opdate) {
        this.name = name;
        this.status = status;
        this.opdate = opdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOpdate() {
        return opdate;
    }

    public void setOpdate(String opdate) {
        this.opdate = opdate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
