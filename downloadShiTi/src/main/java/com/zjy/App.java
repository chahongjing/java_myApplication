package com.zjy;

import com.zjy.enums.PaperType;
import com.zjy.service.Ibm_PaperQuestionService;
import com.zjy.service.TestBeanService;
import com.zjy.utils.LogHelper;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    protected static Logger logger = LogHelper.getLogger(App.class);

    public static void main(String[] args) {
        Logger logger = LogHelper.getLogger(App.class);
        try {
            logger.info("开始导出数据！");
            Ibm_PaperQuestionService ibm_paperQuestionService = new Ibm_PaperQuestionService();
            List<Pair<Date, Date>> paperDateList = new ArrayList<>();
            PaperType pt = PaperType.Formal;
            Pair<Date, Date> pair = new MutablePair<Date, Date>(new Date(117, 1, 1), new Date(118, 1, 1));
            paperDateList.add(pair);
            ibm_paperQuestionService.exportAllXueKePaperQuestionExcel(paperDateList, pt);
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
    }
}
