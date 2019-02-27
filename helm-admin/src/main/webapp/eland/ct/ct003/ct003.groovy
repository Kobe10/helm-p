import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import com.shfb.oframe.sysmg.repo.vo.SvcDoc
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 选房序号单 模块
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/10/23 0023 16:17
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
class ct003 extends GroovyController {
    /** 初始化 选房序号单 页面 */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        String schemeType = request.getParameter("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "1";
        }
        modelMap.put("hsId", hsId);
        modelMap.put("schemeType", schemeType);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        //
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.printCtStatus");   // 协议打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.printReCtStatus");   //回执单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsCtInfo.printChHsStatus");   //序号单打印状态
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        String ctStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                ctStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctStatus");
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctDate");
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
            }
        }
        return new ModelAndView("/eland/ct/ct003/ct003", modelMap);
    }

    /** 查看选房确认单页面  */
    public ModelAndView infoChHsNum(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        modelMap.put("hsId", hsId);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        //
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.printCtStatus");   // 协议打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.printReCtStatus");   //回执单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsCtInfo.printChHsStatus");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "HouseInfo.HsCtInfo.chooseHsSid");   //选房顺序号
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        String ctStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                ctStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctStatus");
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctDate");
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    if (ctDate.length() == 17) {
                        Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                        ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                    }
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
            }
        }
        return new ModelAndView("/eland/ct/ct003/ct00301", modelMap);
    }

    /**
     * 生成选房序号单,调用表单生成单据
     * @param request
     * @param response
     * @return
     */
    public ModelAndView generateCfmChHsNum(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String hsId = request.getParameter("hsId");
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 系统配置表
        Map<String, String> sysFormCfg = getCfgCollection(request, "PRJ_FORM", true, svcRequest.getPrjCd());
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        paramData.setStrValue("OpData.formCd", sysFormCfg.get("ctAndChFrm"));
        // 生成报表参数
        paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", request.getParameter("hsId"));
        // 协议编号
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "sourceType");
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", "chooseOrder");
        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        String errMsg = "";
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("GENERATE_REPORT");
            // 返回结果类型
            String resultType = opResult.getValue("Operation.OpResult.resultType");
            if ("1".equals(resultType)) {
                String docPath = opResult.getValue("Operation.OpResult.resultParam");
                String docPrePath = "/reports/report"
                String fromOp = request.getParameter("fromOp");
                if (StringUtil.isEqual(fromOp, "download")) {
                    // 获取下载文件的后缀
                    String docExt = docPath.substring(docPath.lastIndexOf("."), docPath.length());
                    // 生成下载文件的展示名称
                    String docName = request.getParameter("docName");
                    if (StringUtil.isEmptyOrNull(docName)) {
                        docName = "选房序号单(" + hsId + ")";
                    } else {
                        docName = java.net.URLDecoder.decode(docName, "utf-8")
                    }
                    docName = docName + "." + docExt;
                    // 获取下载的本地文件
                    String contextType = request.getParameter("contextType");
                    File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                    // 文件下载输出到响应流
                    ResponseUtil.downloadFile(response, contextType, docName, localFile,
                            request.getHeader("USER-AGENT"), false, true);
                    return;
                } else {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct003/ct003_doc_iframe", modelMap);
                }
            } else {
                errMsg = errMsg + "只有文档类型的模板允许在该模块中生成,请调整表单配置!"
            }
        } else {
            errMsg = "生成文档失败,请联系系统管理员";
        }
        String fromOp = request.getParameter("fromOp");
        if (StringUtil.isEqual(fromOp, "download")) {
            // 下载错误不响应
            String downloadFileName = "生成文档失败.txt";
            File tempFile = ServerFile.createFile("temp/", UUID.randomUUID().toString() + downloadFileName);
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, "", downloadFileName, tempFile, request.getHeader("USER-AGENT"));
        } else {
            //本地查看
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", errMsg);
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }
}