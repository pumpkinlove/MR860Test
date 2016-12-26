package com.miaxis.mr860test.domain;

import java.util.List;

/**
 * Created by xu.nan on 2016/12/26.
 */

public class SubmitEvent {

    private List<TestItem> itemList;
    private String path;

    public SubmitEvent() {
    }

    public SubmitEvent(List<TestItem> itemList, String path) {
        this.itemList = itemList;
        this.path = path;
    }

    public List<TestItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TestItem> itemList) {
        this.itemList = itemList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
