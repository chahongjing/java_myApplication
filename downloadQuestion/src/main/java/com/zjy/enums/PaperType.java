package com.zjy.enums;


import java.util.Arrays;

/**
 * Created by Administrator on 2018/1/17.
 */
public enum PaperType {
    All(0, "所有试卷"),
    Formal(1, "正式试卷"),
    Simulation(2, "模拟试卷");

    private int value;
    private String name;

    PaperType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PaperType getByValue(int value) {
        return Arrays.stream(PaperType.values()).filter(item -> item.getValue() == value).findFirst().get();
    }

    public int getValue() {
        return value;
    }
}
