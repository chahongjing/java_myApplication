package com.zjy;

import com.zjy.enums.PaperType;
import com.zjy.service.FastdfsClientService;
import com.zjy.service.Ibm_PaperQuestionService;
import com.zjy.service.TestBeanService;
import com.zjy.utils.LogHelper;
import com.zjy.utils.PropertiesHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    protected static Logger logger = LogHelper.getLogger(App.class);
    private static PaperType paperType;
    private static List<Pair<Date, Date>> dateList;

    public static void main(String[] args) {
        Logger logger = LogHelper.getLogger(App.class);
        try {
            logger.info("开始导出数据！");
            Ibm_PaperQuestionService ibm_paperQuestionService = new Ibm_PaperQuestionService();
            init();
            ibm_paperQuestionService.exportAllXueKePaperQuestionExcel(dateList, paperType);
            //ibm_paperQuestionService.exportAllXueKePaperQuestionExcel();
            String message = "数据全部导出完成！！！请在“" + ibm_paperQuestionService.getExcelPath() + "”查看导出的内容！";
            logger.info(message);
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("生成excel异常！", e);
        }

        TestBeanService beanService = new TestBeanService();
        beanService.getBeans();

//        System.out.println("Hello World!");
//        String fid = FastdfsClientService.testUpload();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        FastdfsClientService.testDownload(fid);
    }

    public static void init()  {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strPaperType = PropertiesHelper.getInstance().getProperties("papertype");
        if(!StringUtils.isBlank(strPaperType)) {
            paperType = PaperType.getByValue(Integer.parseInt(strPaperType));
        } else {
            paperType = PaperType.All;
        }
        dateList = new ArrayList<>();
        Pair<Date, Date> kvPair;
        String strPaperCreateDate = PropertiesHelper.getInstance().getProperties("papercreatedate");
        if(!StringUtils.isBlank(strPaperCreateDate)) {
            String[] datePair = strPaperCreateDate.split(";");
            try {
                for (String pair : datePair) {
                    String[] beginAndEnd = pair.split(",");
                    if(beginAndEnd.length != 2) continue;
                    kvPair = new MutablePair<>(sdf.parse(beginAndEnd[0]), sdf.parse(beginAndEnd[1]));
                    dateList.add(kvPair);
                }
            } catch (Exception ex) {
                logger.error("日期解析错误！", ex);
            }
        }
    }
}
