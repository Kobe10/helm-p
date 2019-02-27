import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
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
 * Created by linql on 2016/03/12.
 * 居民信息报表, 通过表单管理及居民信息单据实现居民信息报表的动态生成
 */
class ph013 extends GroovyController {

    /* 初始化进入页面 */

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/ph/ph013/ph013", modelMap);
    }

    /* 初始化进入页面 */

    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        // 页面数据推送
        ModelMap modelMap = new ModelMap();

        // 获取输入参数
        String hsId = request.getParameter("hsId");
        modelMap.put("hsId", hsId);
        modelMap.put("hsCtId", request.getParameter("hsCtId"));
        modelMap.put("lockRptType", request.getParameter("lockRptType"));
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
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsOwnerPersons");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        //获取各种状态  跳转不同页面    0，未生成  1， 已生成
        String hsJsStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                modelMap.put("hsInfo", houseInfo.getRootNode());
            }
        }
        return new ModelAndView("/eland/ph/ph013/ph013_info", modelMap);
    }

    /**
     * 生成及下载安置协议
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView getCt(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        String hsCtId = request.getParameter("hsCtId");
        String schemeType = request.getParameter("schemeType");
        String fromOp = request.getParameter("fromOp");
        // 生成签约协议文档
        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        paramData.setStrValue("OpData.HsCtInfo.hsCtId", hsCtId);
        paramData.setStrValue("OpData.HsCtInfo.schemeType", schemeType);
        if("no" == fromOp) {
            paramData.setStrValue("OpData.HsCtInfo.notGenerateDoc", "true");
        }
        svcRequest.addOp("CREATE_AGREEMENT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        if ("no" == fromOp) {
            ResponseUtil.printSvcResponse(response, svcResponse, "");
            return;
        }
        // 处理服务返回结果
        String errMsg = "生成文档失败，请联系管理员!"
        boolean isSuccess = svcResponse.isSuccess();
        if (isSuccess) {
            XmlBean opResult = svcResponse.getFirstOpRsp("CREATE_AGREEMENT");
            if (opResult != null) {
                return dealReportResult(request, response, opResult);
            }
        } else {
            errMsg = "生成文档失败，错误原因:\"" + svcResponse.getErrMsg() + "\"";
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("errMsg", errMsg);
        return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
    }

    /**
     * 获取生成居民信息报表
     * 接收输入参数： rptType(表单编码，必须传递), hsId(房屋编号, 必须传递），hsCtId（协议编号）
     * 扩展参数接收： HouseInfo_XXXX，HsCtInfo_XXXX，用于扩展房屋和协议的属性信息
     * 下载查看参数：fromOp：view（查看）， download(下载)
     **/
    public ModelAndView getRpt(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String entityNames = request.getParameter("entityNames");
        String entityKeys = request.getParameter("entityKeys");
        if (StringUtil.isEmptyOrNull(entityNames)) {
            entityNames = "HouseInfo,HsCtInfo";
            entityKeys = "hsId,hsCtId";
        }
        Map<String, String> requestMap = requestUtil.getRequestMap(request);
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        String formCd = request.getParameter("formCd");
        if (StringUtil.isEmptyOrNull(formCd)) {
            formCd = requestUtil.getStrParam("rptType");
        }
        paramData.setStrValue("OpData.formCd", formCd);
        String[] entityNameArr = entityNames.split(",");
        String[] entityKeyArr = entityKeys.split(",");
        // 输入参数
        int addEntityCount = 0;
        for (int i = 0; i < entityKeyArr.length; i++) {
            if (StringUtil.isEmptyOrNull(entityNameArr[i])) {
                continue;
            }
            String entityName = entityNameArr[i];
            String entityKey = entityKeyArr[i];
            // 生成报表参数
            paramData.setStrValue("OpData.Report.Parameter[${addEntityCount}].entityName", entityName);
            int addCount = 0;
            paramData.setStrValue("OpData.Report.Parameter[${addEntityCount}].Property[${addCount}].attrName", entityKey);
            paramData.setStrValue("OpData.Report.Parameter[${addEntityCount}].Property[${addCount++}].value",
                    requestUtil.getStrParam(entityKey));
            // 循环处理请求信息
            for (Map.Entry<String, String> entry : requestMap) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                if (paramName.startsWith(entityName + "_")) {
                    String addName = paramName.substring(10);
                    paramData.setStrValue(
                            "OpData.Report.Parameter[${addEntityCount}].Property[${addCount}].attrName", addName);
                    paramData.setStrValue(
                            "OpData.Report.Parameter[${addEntityCount}].Property[${addCount++}].value", paramValue);
                }
            }
            addEntityCount++;
        }

        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        // 处理服务返回结果
        String errMsg = "生成文档失败，请联系管理员!"
        boolean isSuccess = svcResponse.isSuccess();
        if (isSuccess) {
            XmlBean opResult = svcResponse.getFirstOpRsp("GENERATE_REPORT");
            if (opResult != null) {
                return dealReportResult(request, response, opResult);
            }
        } else {
            errMsg = "生成文档失败，错误原因:\"" + svcResponse.getErrMsg() + "\"";
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("errMsg", errMsg);
        return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
    }

    /**
     * 处理生成的office文档
     * @param request
     * @param response
     * @param opResult
     * @return
     */
    private ModelAndView dealReportResult(HttpServletRequest request, HttpServletResponse response, XmlBean opResult) {
        // 返回结果类型
        String resultType = opResult.getValue("Operation.OpResult.resultType");
        if ("1" == resultType) {
            String docPath = opResult.getValue("Operation.OpResult.resultParam");
            String docPrePath = "/reports/report"
            String fromOp = request.getParameter("fromOp");
            if (StringUtil.isEqual(fromOp, "view")) {
                //本地查看
                ModelMap modelMap = new ModelMap();
                String docFileUrl = StringUtil.encNetBase64Str(docPrePath + docPath);
                modelMap.put("docFileUrl", docFileUrl);
                // 增加入参控制文档展示控件的相对高度
                String subLayoutH = request.getParameter("subLayoutH");
                if (StringUtil.isEmptyOrNull(subLayoutH)) {
                    subLayoutH = "140";
                }
                modelMap.put("subLayoutH", subLayoutH);
                // 设置控件的编号
                // 增加入参控制文档展示控件的相对高度
                String docObjId = request.getParameter("docObjId");
                if (StringUtil.isEmptyOrNull(docObjId)) {
                    docObjId = "Ph013PageOfficeCtrl1";
                }
                modelMap.put("docObjId", docObjId);
                return new ModelAndView("/eland/ph/ph013/ph013_doc_iframe", modelMap)
            } else if (StringUtil.isEqual(fromOp, "print")) {
                // 弹出答应对话宽
                ModelMap modelMap = new ModelMap();
                String docFileUrl = StringUtil.encNetBase64Str(docPrePath + docPath);
                modelMap.put("docFileUrl", docFileUrl);
                // 增加入参控制文档展示控件的相对高度
                String subLayoutH = request.getParameter("subLayoutH");
                if (StringUtil.isEmptyOrNull(subLayoutH)) {
                    subLayoutH = "140";
                }
                modelMap.put("subLayoutH", subLayoutH);
                // 设置控件的编号
                // 增加入参控制文档展示控件的相对高度
                String docObjId = request.getParameter("docObjId");
                if (StringUtil.isEmptyOrNull(docObjId)) {
                    docObjId = "Ph013PrintPageOfficeCtrl1";
                }
                modelMap.put("docObjId", docObjId);
                return new ModelAndView("/eland/ph/ph013/ph013_doc_print", modelMap)
            } else if (StringUtil.isEqual(fromOp, "download")) {
                // 获取下载文件的后缀
                String docExt = docPath.substring(docPath.lastIndexOf("."), docPath.length());
                // 生成下载文件的展示名称
                String docName = request.getParameter("docName");
                if (StringUtil.isEmptyOrNull(docName)) {
                    docName = "信息报表";
                } else {
                    docName = java.net.URLDecoder.decode(docName, "utf-8")
                }
                docName = docName + docExt;
                // 获取下载的本地文件
                String contextType = request.getParameter("contextType");
                String agent = request.getHeader("USER-AGENT");
                File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false, true);
                return;
            }
        } else {
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", "只有文档类型的模板允许在该模块中生成,请调整表单配置!");
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }

    /** 查看协议文档 */
    public ModelAndView viewDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String docFileUrl = request.getParameter("docFileUrl");
        docFileUrl = StringUtil.dcdNetBase64Str(docFileUrl);
        modelMap.put("docFileUrl", docFileUrl);
        // 增加入参控制文档展示控件的相对高度
        String docObjId = request.getParameter("docObjId");
        if (StringUtil.isEmptyOrNull(docObjId)) {
            docObjId = "Ph013PageOfficeCtrl1";
        }
        modelMap.put("docObjId", docObjId);
        // 返回界面
        return new ModelAndView("/eland/ph/ph013/ph013_doc_view", modelMap)
    }

    /**
     * 下载服务的文档
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void downDoc(HttpServletRequest request, HttpServletResponse response) {
        String docFileUrl = request.getParameter("docFileUrl");
        String agent = request.getHeader("USER-AGENT");
        docFileUrl = StringUtil.dcdNetBase64Str(docFileUrl);
        // 获取下载文件的后缀
        String docExt = docFileUrl.substring(docFileUrl.lastIndexOf("."), docFileUrl.length());
        // 生成下载文件的展示名称
        String docName = request.getParameter("docName");
        if (StringUtil.isEmptyOrNull(docName)) {
            docName = "信息报表";
        } else {
            docName = java.net.URLDecoder.decode(docName, "utf-8")
        }
        docName = docName + docExt;
        File localFile = new File(StringUtil.formatFilePath("webroot:" + docFileUrl));
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, "", docName, localFile, agent, false, true);
        return;
    }

    /** 打印文档 */
    public ModelAndView printDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct001/ct001_doc_print", modelMap)
    }

}