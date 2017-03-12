package com.miaxis.mr860test.domain;

import android.graphics.Bitmap;

/**
 * Created by xu.nan on 2017/3/1.
 */

public class IdCardEvent {
    private String name;
    private String cardNo;
    private String gender;
    private String race;
    private String birthday;
    private String validPeriodStart;
    private String validPeriodEnd;
    private String regOrg;
    private Bitmap photo;
    private String address;
    private String remark;  //预留

    private String finger0;
    private String finger1;

    private String fingerPosition0;
    private String fingerPosition1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getValidPeriodStart() {
        return validPeriodStart;
    }

    public void setValidPeriodStart(String validPeriodStart) {
        this.validPeriodStart = validPeriodStart;
    }

    public String getValidPeriodEnd() {
        return validPeriodEnd;
    }

    public void setValidPeriodEnd(String validPeriodEnd) {
        this.validPeriodEnd = validPeriodEnd;
    }

    public String getRegOrg() {
        return regOrg;
    }

    public void setRegOrg(String regOrg) {
        this.regOrg = regOrg;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFinger0() {
        return finger0;
    }

    public void setFinger0(String finger0) {
        this.finger0 = finger0;
    }

    public String getFinger1() {
        return finger1;
    }

    public void setFinger1(String finger1) {
        this.finger1 = finger1;
    }

    public String getFingerPosition0() {
        return fingerPosition0;
    }

    public void setFingerPosition0(String fingerPosition0) {
        this.fingerPosition0 = fingerPosition0;
    }

    public String getFingerPosition1() {
        return fingerPosition1;
    }

    public void setFingerPosition1(String fingerPosition1) {
        this.fingerPosition1 = fingerPosition1;
    }
}
