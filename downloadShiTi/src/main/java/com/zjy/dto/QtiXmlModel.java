package com.iflytek.itembank.dto;

import java.util.ArrayList;
import java.util.List;

public class QtiXmlModel {
    public QtiXmlModel()
    {
        this.ItemBodyInfo = new ItemBody();
        this.ResponseInfos = new ArrayList<>();
    }

    private List<ResponseDeclaration> ResponseInfos;

    private ItemBody ItemBodyInfo;

    public List<ResponseDeclaration> getResponseInfos() {
        return ResponseInfos;
    }

    public void setResponseInfos(List<ResponseDeclaration> responseInfos) {
        ResponseInfos = responseInfos;
    }

    public ItemBody getItemBodyInfo() {
        return ItemBodyInfo;
    }

    public void setItemBodyInfo(ItemBody itemBodyInfo) {
        ItemBodyInfo = itemBodyInfo;
    }
}
