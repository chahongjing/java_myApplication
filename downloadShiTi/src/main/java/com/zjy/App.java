package com.iflytek.itembank;

import com.iflytek.itembank.service.Ibm_PaperQuestionService;
import com.iflytek.itembank.utils.LogHelper;
import org.slf4j.Logger;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Logger logger = LogHelper.getLogger(App.class);
        try {
            logger.info("开始导出数据！");
            Ibm_PaperQuestionService ibm_paperQuestionService = new Ibm_PaperQuestionService();
            ibm_paperQuestionService.exportAllXueKePaperQuestionExcel();
            String message = "数据全部导出完成！！！请在“" + ibm_paperQuestionService.getExcelPath() + "”查看导出的内容！";
            logger.info(message);
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("生成excel异常！", e);
        }
    }
}
