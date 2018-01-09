package com.zjy.dto;

import java.util.ArrayList;
import java.util.List;

public class CorrectResponse {
    private List<String> Value;

    public CorrectResponse()
    {
        this.Value = new ArrayList<>();
    }

    public List<String> getValue() {
        return Value;
    }

    public void setValue(List<String> value) {
        Value = value;
    }
}
