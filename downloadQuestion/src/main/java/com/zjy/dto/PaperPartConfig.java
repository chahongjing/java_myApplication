package com.zjy.dto;

import java.util.List;

/**
 * Created by Administrator on 2018/1/12.
 */
public class PaperPartConfig {
    private String identifier;
    private String elementType;
    private String directions;
    //结构随机
    //public Boolean jgrandom { set; get; }
    //试题随机
    private Boolean strandom;
    //试题顺序随机
    //public Boolean stsxrandom { set; get; }
    //选项随机
    private Boolean xxrandom;
    private List<PaperSectionConfig> assessmentSections;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public Boolean getStrandom() {
        return strandom;
    }

    public void setStrandom(Boolean strandom) {
        this.strandom = strandom;
    }

    public Boolean getXxrandom() {
        return xxrandom;
    }

    public void setXxrandom(Boolean xxrandom) {
        this.xxrandom = xxrandom;
    }

    public List<PaperSectionConfig> getAssessmentSections() {
        return assessmentSections;
    }

    public void setAssessmentSections(List<PaperSectionConfig> assessmentSections) {
        this.assessmentSections = assessmentSections;
    }
}
