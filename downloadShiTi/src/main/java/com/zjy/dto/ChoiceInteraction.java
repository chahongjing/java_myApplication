package com.iflytek.itembank.dto;

import java.util.ArrayList;
import java.util.List;

public class ChoiceInteraction {
    private String ResponseIdentifier;

    private String Sequence;

    /// <summary>指定题目是单选还是多选--指定答案个数:
    /// </summary>
    /// <remarks>
    /// 0是不限制答案个数，
    /// 1是只有一个答案，
    /// 2是只有2个答案
    /// </remarks>
    private int MaxChoices;

    /// <summary>题干
    /// </summary>
    private String Prompt;

    /// <summary>所有的选项
    /// </summary>
    private List<SimpleChoice> Choices;

    public ChoiceInteraction() {
        this.Choices = new ArrayList<>();
    }

    public String getResponseIdentifier() {
        return ResponseIdentifier;
    }

    public void setResponseIdentifier(String responseIdentifier) {
        ResponseIdentifier = responseIdentifier;
    }

    public String getSequence() {
        return Sequence;
    }

    public void setSequence(String sequence) {
        Sequence = sequence;
    }

    public int getMaxChoices() {
        return MaxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        MaxChoices = maxChoices;
    }

    public String getPrompt() {
        return Prompt;
    }

    public void setPrompt(String prompt) {
        Prompt = prompt;
    }

    public List<SimpleChoice> getChoices() {
        return Choices;
    }

    public void setChoices(List<SimpleChoice> choices) {
        Choices = choices;
    }
}
