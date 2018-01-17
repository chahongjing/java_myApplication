package com.zjy.dto;

import java.util.List;

/**
 * Created by Administrator on 2018/1/12.
 */
public class PaperSectionConfig {
    private String identifier;
    private String elementType;
    private String directions;
    private String sectionType;
    private List<PaperItemConfig> assessmentItems;

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

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public List<PaperItemConfig> getAssessmentItems() {
        return assessmentItems;
    }

    public void setAssessmentItems(List<PaperItemConfig> assessmentItems) {
        this.assessmentItems = assessmentItems;
    }
}
