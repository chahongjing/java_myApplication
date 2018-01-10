package com.zjy;

import com.zjy.service.Ibm_PaperQuestionService;
import com.zjy.service.TestBeanService;
import com.zjy.utils.LogHelper;
import org.slf4j.Logger;

/**
 * Hello world!
 */
public class App {
    protected static Logger logger = LogHelper.getLogger(App.class);

    public static void main(String[] args) {
//        try {
//            new Ibm_PaperQuestionService().exportAllXueKePaperQuestionExcel();
//            System.in.read();
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("生成excel异常！", e);
//        }
        TestBeanService beanService = new TestBeanService();
        beanService.getBeans();
    }
}
