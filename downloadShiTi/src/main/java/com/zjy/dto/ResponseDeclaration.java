package com.zjy.dto;

import java.util.ArrayList;
import java.util.List;

public class ResponseDeclaration {
    private String Identifier;

    private List<CorrectResponse> RespinseInfos;

    public ResponseDeclaration() {
        this.RespinseInfos = new ArrayList<>();
    }

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        Identifier = identifier;
    }

    public List<CorrectResponse> getRespinseInfos() {
        return RespinseInfos;
    }

    public void setRespinseInfos(List<CorrectResponse> respinseInfos) {
        RespinseInfos = respinseInfos;
    }
}
