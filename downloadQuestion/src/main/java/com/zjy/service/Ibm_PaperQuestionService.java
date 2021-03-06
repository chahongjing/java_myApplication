package com.zjy.service;

import com.alibaba.fastjson.JSON;
import com.zjy.dto.*;
import com.zjy.entity.Ibm_Major;
import com.zjy.entity.Ibm_Paper;
import com.zjy.entity.Ibm_PaperQuestion;
import com.zjy.enums.PaperType;
import com.zjy.utils.DbHelper;
import com.zjy.utils.ExcelHelper;
import com.zjy.utils.LogHelper;
import com.zjy.utils.PropertiesHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Ibm_PaperQuestionService {

    private Ibm_MajorService majorSrv;

    /**
     * 导出目录
     */
    private String excelPath;

    /**
     * 导出文件后缀
     */
    private String excelSuffix;

    /**
     * 导出类型
     */
    private String exportType;

    /**
     * 导出列名
     */
    private LinkedHashMap<String, String> fieldMap;

    private Logger logger = LogHelper.getLogger(Ibm_PaperQuestionService.class);

    /**
     * 构造器
     */
    public Ibm_PaperQuestionService() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        majorSrv = new Ibm_MajorService();
        // excel存放路径
        excelPath = PropertiesHelper.getInstance().getProperties("rootpath");
        // 没有配置默认在jar同级目录
        if (StringUtils.isBlank(excelPath)) {
            excelPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            excelPath = new File(excelPath).getParentFile().getAbsolutePath();
        }
        File path = Paths.get(excelPath, "excelFiles" + sdf.format(new Date())).toFile();
        excelPath = path.getAbsolutePath();
        if (!path.exists()) {
            path.mkdirs();
        }
        // excelSuffix = PropertiesHelper.getInstance().getProperties("excelsuffix");
        if (StringUtils.isBlank(excelSuffix)) {
            excelSuffix = ".xls";
        }
        exportType = PropertiesHelper.getInstance().getProperties("exporttype");
        if (StringUtils.isBlank(exportType) || (!"1".equals(exportType) && !"2".equals(exportType))) {
            exportType = "1";
        }
        // 导出列名
        fieldMap = new LinkedHashMap<>();
//        fieldMap.put("zhuanYeBianMa", "专业编码");
//        fieldMap.put("xueKeBianMa", "学科编码");
        fieldMap.put("xueKeMingCheng", "课程名称");
        fieldMap.put("shiJuanMingCheng", "试卷名称");
        fieldMap.put("shiJuanBianMa", "试卷编号");
        fieldMap.put("shiTiBianMa", "试题题库编码");
        fieldMap.put("shiJuanShiTiXuHao", "试卷题库序号");
        fieldMap.put("tiGan", "题干");
        fieldMap.put("xuanXiang", "选项");
        fieldMap.put("daAn", "答案");
        fieldMap.put("nanDuBianMa", "难度代码");
        fieldMap.put("nanDuMingCheng", "难度名称");
        fieldMap.put("zhiShiDianBianMa", "知识点编码");
        fieldMap.put("zhiShiDianMingCheng", "知识点名称");
        fieldMap.put("kaoShiYaoQiuBianMa", "考试要求编码");
        fieldMap.put("kaoShiYaoQiuMingCheng", "考试要求名称");
        fieldMap.put("tiXingBianMa", "题型代码");
        fieldMap.put("tiXingMingCheng", "题型名称");
        fieldMap.put("isPublic", "是否公开");
    }

    /**
     * 所有学科参与过组卷的都导出excel
     */
    public void exportAllXueKePaperQuestionExcel(List<Pair<Date, Date>> paperDateList, PaperType paperType) {
        List<Ibm_Major> majorList = majorSrv.getAllPaperMajor();
        if (majorList == null || majorList.isEmpty()) {
            logger.info("没有参与组卷的学科！");
            return;
        }
        logger.info("=================== 总共" + majorList.size() + "个学科待导出。");
        int i = 0;
        for (Ibm_Major major : majorList) {
            i++;
            if (major == null || StringUtils.isBlank(major.getMajorCode())) {
                continue;
            }

            String majorInfo = (major.getMajorName() + "(" + major.getMajorCode() + ")").replace(".", "_");
            logger.info("开始处理第" + i + "个学科【" + majorInfo + "】。");
            // List<Ibm_PaperQuestion> paperQuestionList = getPaperQuestionBySubject(major.getMajorCode());
            List<Ibm_PaperQuestion> paperQuestionList = getPaperQuestionBySubjectNew(major.getMajorCode(), paperDateList, paperType);
            if (paperQuestionList == null || paperQuestionList.isEmpty()) {
                logger.info("第" + i + "个学科【" + majorInfo + "】下没有参与组卷的试题！。\n");
                continue;
            }
            // 解析题干，选项，答案
            anasysContent(paperQuestionList);
            // 每个学科生成一个excel
            exportPaperQuestionExcel(major, paperQuestionList);
            logger.info("第" + i + "个学科【" + majorInfo + "】处理完成。\r\n");
        }
    }

    /**
     * 按学科导出excel
     *
     * @param major             学科信息
     * @param paperQuestionList 试卷试题列表
     */
    public void exportPaperQuestionExcel(Ibm_Major major, List<Ibm_PaperQuestion> paperQuestionList) {
        String majorInfo = major.getUtid().toString() + "_" + (major.getMajorCode() + "(" + major.getMajorName() + ")").replace(".", "_");
        String fileName = majorInfo + excelSuffix;
        File f = new File(excelPath + File.separator + fileName);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        OutputStream os;
//        if (major.getUtid() == 573) {
//            System.out.println("573");
//        }

        try {
            List<String> paperIds = paperQuestionList.stream().map(item -> item.getPaperId()).filter(item -> !StringUtils.isBlank(item)).distinct().collect(Collectors.toList());
            logger.info("此学科共" + paperIds.size() + "套试卷，" + paperQuestionList.size() + "个试题！");
            // paper
            if ("1".equals(exportType)) {
                Workbook wb = new HSSFWorkbook();
                os = new BufferedOutputStream(new FileOutputStream(f));
                int data = 0;
                String shiJuanBianMa;
                for (String paperId : paperIds) {
                    List<Ibm_PaperQuestion> shiTis = paperQuestionList.stream().filter(item -> paperId.equals(item.getPaperId())).collect(Collectors.toList());
                    if (shiTis.isEmpty()) continue;
                    logger.info("正在处理第" + (data + 1) + "套试卷（" + shiTis.size() + "个试题）！");

                    data++;
                    shiJuanBianMa = shiTis.get(0).getUtid() + "(" + shiTis.get(0).getShiJuanMingCheng() + ")";
                    ExcelHelper.listGroupToExcel(shiTis, fieldMap, shiJuanBianMa, wb);
                }
                if (data > 0) {
                    wb.write(os);
                    os.flush();
                }
                os.close();
            } else {
                // subject
                os = new BufferedOutputStream(new FileOutputStream(f));
                ExcelHelper.listToExcel(paperQuestionList, fieldMap, majorInfo, 65535, os);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(majorInfo + "导出excel异常！", e);
        }
    }

    /**
     * 查询学科下所有试卷试题
     *
     * @param subjectCode 学科编码
     * @return
     */
    public List<Ibm_PaperQuestion> getPaperQuestionBySubject(String subjectCode) {
        String sql = "select utid, paperId, ZhuanYeBianMa, XueKeBianMa, XueKeMingCheng, ShiJuanMingCheng," +
                "       ShiJuanBianMa, ShiJuanShiTiXuHao, ShiTiBianMa, NanDuBianMa, NanDuMingCheng, ZhiShiDianBianMa," +
                "       ZhiShiDianMingCheng, KaoShiYaoQiuBianMa, KaoShiYaoQiuMingCheng, TiXingBianMa, TiXingMingCheng, QtiXmlModel," +
                "       ispublic " +
                "  from (" +
                "    select p.utid, p.paperid, p.parentcode as ZhuanYeBianMa, p.subjectcode as XueKeBianMa, p.subjectname as XueKeMingCheng, p.name as ShiJuanMingCheng, p.papercode as ShiJuanBianMa," +
                "           row_number() over(Partition By p.paperid order by instr(p.papercontent, pq.paperquestionid, 1, 1)) as ShiJuanShiTiXuHao, q.questioncode as ShiTiBianMa," +
                "           q.Difficultycode as NanDuBianMa, Q.Difficultyname as NanDuMingCheng, q.knowledgecode as ZhiShiDianBianMa, q.knowledgecontent as ZhiShiDianMingCheng, q.abilitycode as KaoShiYaoQiuBianMa," +
                "           q.abilityname as KaoShiYaoQiuMingCheng, q.sectioncode as TiXingBianMa, q.sectionname as TiXingMingCheng, pq.sectioncode, pq.createtime," +
                "           nvl2(pq.qtixmlmodel, pq.qtixmlmodel, q.qtixmlmodel) as qtixmlmodel,case when q.ispublic = 1 then '是' else '否' end as ispublic " +
                "      from ibm_paper p" +
                "     inner join ibm_paperquestion pq on p.paperid = pq.paperid" +
                "     inner join ibm_formalquestion q on pq.formalquestionid = q.formalquestionid" +
                "     where p.subjectcode = '" + subjectCode.replace("'", "") + "'" +
                "  )" +
                " order by ZhuanYeBianMa, XueKeBianMa, ShiJuanMingCheng, ShiJuanBianMa, ShiJuanShiTiXuHao";
        return DbHelper.getList(sql, Ibm_PaperQuestion.class);
    }

    public List<Ibm_PaperQuestion> getPaperQuestionBySubjectNew(String subjectCode, List<Pair<Date, Date>> paperDateList, PaperType paperType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select utid, paperId, paperCode, parentCode, name, subjectCode, subjectName, paperContent from ibm_paper p " +
                "   where p.subjectcode = '" + subjectCode.replace("'", "") + "' ";
        if(paperDateList != null && !paperDateList.isEmpty()) {
            for (Pair<Date, Date> stringDatePair : paperDateList) {
                sql += " and (p.createon >= to_date('" + sdf.format(stringDatePair.getKey()) + "', 'yyyy-MM-dd hh24:mi:ss') and p.createon < to_date('" + sdf.format(stringDatePair.getValue()) + "', 'yyyy-MM-dd hh24:mi:ss'))";
            }
        }
        if(paperType != null) {
            if(paperType == PaperType.Formal) {
                sql += " and p.name not like '%模拟%' ";
            } else if(paperType == PaperType.Simulation) {
                sql += " and p.name like '%模拟%' ";
            }
        }

        sql +=  "    order by p.utid";
        List<Ibm_Paper> papers = DbHelper.getList(sql, Ibm_Paper.class);
        List<Ibm_PaperQuestion> pqs = new ArrayList<>();
        PaperTemplateConfigDTO paperModel = null;
        PaperSectionConfig psc = null;
        PaperItemConfig pic = null;
        for (Ibm_Paper paper : papers) {
            List<String> questionIds = new ArrayList<>();
            if (!StringUtils.isBlank(paper.getPaperContent())) {
                paper.setPaperContent(paper.getPaperContent().trim());
                try {
                    paperModel = JSON.parseObject(paper.getPaperContent(), PaperTemplateConfigDTO.class);
                } catch (Exception ex) {
                    logger.error("序列化错误,papercontent！" + paper.getPaperContent(), ex);
                }
                if (paperModel != null) {
                    boolean flag = false;
                    if (!paperModel.getTestParts().isEmpty() && !paperModel.getTestParts().get(0).getAssessmentSections().isEmpty()) {
                        for (int i = 0; i < paperModel.getTestParts().get(0).getAssessmentSections().size(); i++) {
                            if (flag) break;
                            psc = paperModel.getTestParts().get(0).getAssessmentSections().get(i);
                            if (!psc.getAssessmentItems().isEmpty()) {
                                for (int j = 0; j < psc.getAssessmentItems().size(); j++) {
                                    if (flag) break;
                                    pic = psc.getAssessmentItems().get(j);
                                    if(!StringUtils.isBlank(pic.getQuestionId())) {
                                        questionIds.add(pic.getQuestionId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(!questionIds.isEmpty()) {
                // 通过id获取试题
                pqs.addAll(getPaperQuestionByQuestionIds(paper, questionIds));
            }
        }
        return pqs;
    }

    private List<Ibm_PaperQuestion> getPaperQuestionByQuestionIds(Ibm_Paper paper, List<String> questionIds) {
        List<Ibm_PaperQuestion> pqs = new ArrayList<>();
        if(questionIds == null || questionIds.isEmpty()) return pqs;
        String ids = "'" + String.join("','", questionIds) + "'";
        if(!StringUtils.isBlank(ids)) ids = "," + ids;
        String sql = "select pq.paperQuestionId,  q.questioncode as ShiTiBianMa," +
                "           q.Difficultycode as NanDuBianMa, Q.Difficultyname as NanDuMingCheng, q.knowledgecode as ZhiShiDianBianMa, q.knowledgecontent as ZhiShiDianMingCheng, q.abilitycode as KaoShiYaoQiuBianMa," +
                "          q.abilityname as KaoShiYaoQiuMingCheng, q.sectioncode as TiXingBianMa, q.sectionname as TiXingMingCheng, pq.sectioncode, pq.createtime, " +
                "           nvl2(pq.qtixmlmodel, pq.qtixmlmodel, q.qtixmlmodel) as qtixmlmodel,case when q.ispublic = 1 then '是' else '否' end as ispublic " +
                "      from ibm_paperquestion pq " +
                "     inner join ibm_formalquestion q on pq.formalquestionid = q.formalquestionid " +
                "   where pq.paperquestionid in ('-1'" + ids + ")";
        pqs = DbHelper.getList(sql, Ibm_PaperQuestion.class);
        for (Ibm_PaperQuestion pq : pqs) {
            pq.setPaperContent(paper.getPaperContent());
            pq.setPaperId(paper.getPaperId());
            pq.setUtid(paper.getUtid());
            pq.setZhuanYeBianMa(paper.getParentCode());
            pq.setXueKeBianMa(paper.getSubjectCode());
            pq.setXueKeMingCheng(paper.getSubjectName());
            pq.setShiJuanMingCheng(paper.getName());
            pq.setShiJuanBianMa(paper.getPaperCode());
        }
        anasysContent(pqs);
        pqs = pqs.stream().sorted(Comparator.comparing(Ibm_PaperQuestion::getShiJuanShiTiXuHao)).collect(Collectors.toList());
        return pqs;
    }

    private void anasysContent(List<Ibm_PaperQuestion> paperQuestionList) {
        if (paperQuestionList == null || paperQuestionList.isEmpty()) return;
        QtiXmlModel xmlModel = null;
        PaperTemplateConfigDTO paperModel = null;
        PaperSectionConfig psc = null;
        PaperItemConfig pic = null;
        for (Ibm_PaperQuestion question : paperQuestionList) {
            if (!StringUtils.isBlank(question.getQtiXmlModel())) {
                question.setQtiXmlModel(question.getQtiXmlModel().trim());
                try {
                    xmlModel = JSON.parseObject(question.getQtiXmlModel(), QtiXmlModel.class);
                } catch (Exception ex) {
                    logger.error("序列化错误,qtixmlmodel！" + question.getQtiXmlModel(), ex);
                }
                if (xmlModel != null) {
                    question.setTiGan(getTiGan(xmlModel));
                    question.setXuanXiang(getXuanXiang(xmlModel));
                    question.setDaAn(getDaAn(xmlModel));
                }
            }
            if (!StringUtils.isBlank(question.getPaperContent())) {
                question.setPaperContent(question.getPaperContent().trim());
                try {
                    paperModel = JSON.parseObject(question.getPaperContent(), PaperTemplateConfigDTO.class);
                } catch (Exception ex) {
                    logger.error("序列化错误,papercontent！" + question.getPaperContent(), ex);
                }
                if (paperModel != null) {
                    boolean flag = false;
                    if (!paperModel.getTestParts().isEmpty() && !paperModel.getTestParts().get(0).getAssessmentSections().isEmpty()) {
                        for (int i = 0; i < paperModel.getTestParts().get(0).getAssessmentSections().size(); i++) {
                            if (flag) break;
                            psc = paperModel.getTestParts().get(0).getAssessmentSections().get(i);
                            if (!psc.getAssessmentItems().isEmpty()) {
                                for (int j = 0; j < psc.getAssessmentItems().size(); j++) {
                                    if (flag) break;
                                    pic = psc.getAssessmentItems().get(j);
                                    if (question.getPaperQuestionId().equals(pic.getQuestionId())) {
                                        flag = true;
                                        question.setShiJuanShiTiXuHao(pic.getSequence());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String getTiGan(QtiXmlModel xmlModel) {
        StringBuilder tiGan = new StringBuilder();
        ItemBody itemBodyInfo = xmlModel.getItemBodyInfo();
        if (itemBodyInfo == null) return tiGan.toString();
        List<ChoiceInteraction> choiceInteractions = itemBodyInfo.getChoiceInteractions();
        if (StringUtils.isBlank(itemBodyInfo.getPrompt()) && (choiceInteractions == null || choiceInteractions.isEmpty())) return tiGan.toString();
        if(!StringUtils.isBlank(itemBodyInfo.getPrompt())) {
            tiGan.append(getHtmlText(itemBodyInfo.getPrompt()));
        }
        for(ChoiceInteraction choiceInteraction : choiceInteractions) {
            tiGan.append(getHtmlText(choiceInteraction.getPrompt()));
        }
        return tiGan.toString();
    }

    private String getXuanXiang(QtiXmlModel xmlModel) {
        StringBuilder xuanXiang = new StringBuilder();
        ItemBody itemBodyInfo = xmlModel.getItemBodyInfo();
        if (itemBodyInfo == null) return xuanXiang.toString();
        List<ChoiceInteraction> choiceInteractions = itemBodyInfo.getChoiceInteractions();
        if (choiceInteractions == null || choiceInteractions.isEmpty()) return xuanXiang.toString();
        for(ChoiceInteraction choiceInteraction : choiceInteractions){
            List<SimpleChoice> choices = choiceInteraction.getChoices();
            if (choices == null || choices.isEmpty()) continue;
            for (SimpleChoice choice : choices) {
                xuanXiang.append(getHtmlText(choice.getIdentifier()) + "." + getHtmlText(choice.getValue()) + "\r\n    ");
            }
        }
        return xuanXiang.toString();
    }

    private String getDaAn(QtiXmlModel xmlModel) {
        StringBuilder daAn = new StringBuilder();
        List<ResponseDeclaration> responseInfos = xmlModel.getResponseInfos();
        if (responseInfos == null || responseInfos.isEmpty()) return daAn.toString();
        for (ResponseDeclaration responseInfo : responseInfos) {
            List<CorrectResponse> respinseInfos = responseInfo.getRespinseInfos();
            if (respinseInfos == null || respinseInfos.isEmpty()) continue;
            for (CorrectResponse response : respinseInfos) {
                if (response.getValue() == null || response.getValue().isEmpty()) continue;
                for (String item : response.getValue()) {
                    daAn.append(getHtmlText(item) + "\r\n    ");
                }
            }
        }
        return daAn.toString();
    }

    private String getHtmlText(String html) {
        if (StringUtils.isBlank(html)) return "";

        Document document = Jsoup.parse(html);
        html = document.text();
        if (!StringUtils.isBlank(html)) html = html.replace("&nbsp;", "  ");
        return html;
    }

    public String getExcelPath() {
        return excelPath;
    }

    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }
}
