import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 签约处理控制层
 */
class ct004 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", request.getParameter("hsId"));
        String schemeType = request.getParameter("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "1";
        }
        modelMap.put("schemeType", schemeType);
        return new ModelAndView("/eland/ct/ct004/ct004", modelMap);
    }

    /** 初始化 右侧 主界面 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
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
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");             //安置方式
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctStatus");  //签约状态
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.printCtStatus");   // 协议打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.printReCtStatus");   //回执单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsCtInfo.printChHsStatus");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "HouseInfo.HsCtInfo.ctOrder");   //序号单打印状态
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        //获取各种状态  跳转不同页面    0，未生成  1， 已生成
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctDate");
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
                // 查询同一批次签约的协议信息
                String ctOrder = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctOrder");
                opData = new XmlBean();
                svcRequest = RequestUtil.getSvcRequest(request);
                opData.setStrValue(prePath + "queryType", "2");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.HsCtInfo.ctOrder");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", ctOrder);
                opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
                opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
                opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");
                opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctDate");
                opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.hsId");
                svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
                    if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                        List<XmlNode> resultList = new ArrayList();
                        for (int i = 0; i < pageData.getListNum("PageData.Row"); i++) {
                            XmlBean list = pageData.getBeanByPath("PageData.Row[" + i + "]");
                            ctDate = pageData.getStrValue("PageData.Row[" + i + "].HouseInfo.HsCtInfo.ctDate");
                            if (StringUtil.isNotEmptyOrNull(ctDate)) {
                                Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                                ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                                pageData.setStrValue("PageData.Row[" + i + "].HouseInfo.HsCtInfo.ctDate", ctDate);
                            }
                            resultList.add(list.getRootNode());
                        }
                        modelMap.put("resultList", resultList);
                    }
                }
            }
        }
        String forwardPage = "/eland/ct/ct004/ct00401";

        String schemeType = request.getParameter("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "1";
        }
        modelMap.put("schemeType", schemeType);

        return new ModelAndView(forwardPage, modelMap);
    }

    /**
     * 生成安置协议
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public ModelAndView genMainCtDoc(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        String hsCtId = request.getParameter("hsCtId");
        String ctType = request.getParameter("ctType");
        String agent = request.getHeader("USER-AGENT");
        String fromOp = request.getParameter("fromOp");
        String schemeType = request.getParameter("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "1";
        }
        // 生成签约协议文档
        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        paramData.setStrValue("OpData.HsCtInfo.hsCtId", hsCtId);
        paramData.setStrValue("OpData.HsCtInfo.ctType", ctType);
        paramData.setStrValue("OpData.HsCtInfo.schemeType", schemeType);
        svcRequest.addOp("CREATE_AGREEMENT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        // 调整安置方式不生成
        if ("change".equals(fromOp)) {
            ResponseUtil.printSvcResponse(response, svcResponse, "");
            return;
        }
        // 生成协议文档
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("CREATE_AGREEMENT");
            if (opResult != null) {
                String docName = "${hsId}_hsContract.docx";
                String docPrePath = "/reports/report";
                String docPath = opResult.getValue("Operation.OpResult.resultParam");
                String contextType = requestUtil.getStrParam("contextType");
                File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                if (StringUtil.isEqual(fromOp, "download")) {
                    // 下载内容进行缓存
                    //20秒之内重新进入该页面的话不会进入该servlet的
                    Date modifiedTime = DateUtil.toDateYmdHms("20150101000000");
                    response.setDateHeader("Last-Modified", modifiedTime.getTime());
                    response.setDateHeader("Expires", modifiedTime.getTime() + 6553600);
                    response.setHeader("Cache-Control", "public");
                    response.setHeader("Pragma", "Pragma");
                    // 文件下载输出到响应流
                    ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false);
                } else if (StringUtil.isEqual(fromOp, "print")) {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct004/ct004_doc_print_iframe", modelMap)
                } else {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct001/ct001_doc_iframe", modelMap)
                }
            }
        } else {
            if (StringUtil.isEqual(fromOp, "download")) {
                // 下载错误不响应
                String downloadFileName = "生成文档失败.txt";
                File tempFile = ServerFile.createFile("temp/", UUID.randomUUID().toString() + downloadFileName);
                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, "", downloadFileName, tempFile, request.getHeader("USER-AGENT"));
            } else {
                //本地查看
                ModelMap modelMap = new ModelMap();
                modelMap.put("errMsg", "生成文档失败，请联系管理员!");
                return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
            }
        }
    }

    /** 查看协议文档 */
    public ModelAndView viewDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct004/ct004_doc_view", modelMap)
    }

    /** 打印签约文本 修改是否打印协议状态 */
    public ModelAndView printCtText(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String printObj = request.getParameter("printObj");
        String hsCtId = request.getParameter("hsCtId");
        XmlBean opData = new XmlBean();
        // 腾退签约动作组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HsCtInfo");
        opData.setStrValue(nodePath + "EntityData.hsCtId", hsCtId);
        String printObjVal = "";
        if (StringUtil.isEqual("printCt", printObj)) {
            printObjVal = "printCtStatus";
        } else if (StringUtil.isEqual("printReCt", printObj)) {
            printObjVal = "printReCtStatus";
        } else if (StringUtil.isEqual("printChHs", printObj)) {
            printObjVal = "printChHsStatus";
        }
        opData.setStrValue(nodePath + "EntityData.${printObjVal}", "1");
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 生成签约安置协议 */
    public ModelAndView printDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct004/ct004_doc_print", modelMap)
    }

}

