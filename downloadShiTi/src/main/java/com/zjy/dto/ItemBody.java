package com.iflytek.itembank.dto;

import java.util.ArrayList;
import java.util.List;

public class ItemBody {
    private String Prompt;

    private List<ChoiceInteraction> ChoiceInteractions;

    public ItemBody() {
        this.ChoiceInteractions = new ArrayList<>();
    }

    public String getPrompt() {
        return Prompt;
    }

    public void setPrompt(String prompt) {
        Prompt = prompt;
    }

    public List<ChoiceInteraction> getChoiceInteractions() {
        return ChoiceInteractions;
    }

    public void setChoiceInteractions(List<ChoiceInteraction> choiceInteractions) {
        ChoiceInteractions = choiceInteractions;
    }
}
