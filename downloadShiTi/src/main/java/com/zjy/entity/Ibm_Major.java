package com.zjy.entity;

public class Ibm_Major {
    private Integer utid;
    private String majorId;
    private String majorCode;
    private String majorName;

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public Integer getUtid() {
        return utid;
    }

    public void setUtid(Integer utid) {
        this.utid = utid;
    }
}
