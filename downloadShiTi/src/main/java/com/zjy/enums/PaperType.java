package com.zjy.enums;

/**
 * Created by Administrator on 2018/1/17.
 */
public enum PaperType {
    All(0, "所有试卷"),
    Formal(1, "正式试卷"),
    Simulation(2, "模拟试卷");

    private int value;
    private String name;
    PaperType(int value, String name){
        this.value = value;
        this.name = name;
    }
}
