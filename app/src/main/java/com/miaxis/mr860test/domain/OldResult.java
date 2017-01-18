package com.miaxis.mr860test.domain;

/**
 * Created by xu.nan on 2017/1/16.
 */

public class OldResult {

    private int code            = 0;
    private int successCount    = 0;
    private int failCount       = 0;
    private int successTvId     = 0;
    private int failTvId        = 0;
    private int progressTvid    = 0;

    public OldResult() {
    }

    public OldResult(int code) {
        this.code = code;
    }

    public OldResult(int code, int successCount, int failCount, int successTvId, int failTvId, int progressTvid) {
        this.code = code;
        this.successCount = successCount;
        this.failCount = failCount;
        this.successTvId = successTvId;
        this.failTvId = failTvId;
        this.progressTvid = progressTvid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getSuccessTvId() {
        return successTvId;
    }

    public void setSuccessTvId(int successTvId) {
        this.successTvId = successTvId;
    }

    public int getFailTvId() {
        return failTvId;
    }

    public void setFailTvId(int failTvId) {
        this.failTvId = failTvId;
    }

    public int getProgressTvid() {
        return progressTvid;
    }

    public void setProgressTvid(int progressTvid) {
        this.progressTvid = progressTvid;
    }
}
