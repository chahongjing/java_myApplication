package com.iflytek.itembank.service;

import com.iflytek.itembank.entity.Ibm_Major;
import com.iflytek.itembank.utils.DbHelper;

import java.util.List;

public class Ibm_MajorService {
    /**
     * 获取所有参与过组卷的学科
     *
     * @return
     */
    public List<Ibm_Major> getAllPaperMajor() {
        String sql = "select utid, majorId, majorcode, majorname from ibm_major " +
                "where majorcode in (select subjectcode from ibm_paper where subjectcode is not null and status > 1)";
        return DbHelper.getList(sql, Ibm_Major.class);
    }
}
