import com.shfb.oframe.core.util.common.DateUtil
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
 * User: shfb_wang 
 * Date: 2016/3/22 0022 15:48
 * Copyright(c) 北京四海富博计算机服务有限公司
 *
 * 财务管理 模块 存折管理
 */
class fm001 extends GroovyController {
    /**
     * 初始化 存折管理界面
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        //查询实体
        opData.setStrValue("OpData.entityName", "PaymentPlan");
        //查询结果
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "pStatus");
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", "1");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "pId");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "pCode");
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        //启动查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (queryResult != null) {
                List list = new ArrayList();
                int planNum = queryResult.getListNum("PageData.Row");
                for (int i = 0; i < planNum; i++) {
                    XmlBean tempBean = queryResult.getBeanByPath("PageData.Row[${i}]");
                    list.add(tempBean.getRootNode());
                }
                modelMap.put("planList", list);
            }
        }
        return new ModelAndView("/eland/fm/fm001/fm00101", modelMap);
    }

    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String pId = request.getParameter("pId");
        modelMap.put("pId", pId);

        return new ModelAndView("/eland/fm/fm001/fm001", modelMap);
    }
    /**
     * 打开撤回申请页面
     */
    public ModelAndView revocationView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsFmId = request.getParameter("hsFmId");
        modelMap.put("hsFmId", hsFmId);
        return new ModelAndView("/eland/fm/fm001/fm001_revocationView", modelMap);
    }

    /**
     * 打回备注保存
     * @param request
     * @param response
     * @return
     */
    public ModelAndView revocation(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsFmId = request.getParameter("hsFmId");//付款编号
        String fmDesc = request.getParameter("fmDesc");//打回备注
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsFmId", hsFmId);
        opData.setStrValue("OpData.fmDesc", fmDesc);
        //调用组件
        svcRequest.addOp("BACK_PAYEE_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化 批量提交 制折 页面
     */
    public ModelAndView initBatch(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsFmIds = request.getParameter("hsFmIds");
        //请求页面、 推送页面
        String tab = request.getParameter("tab");
        modelMap.put("hsFmIds", hsFmIds);
        return new ModelAndView("/eland/fm/fm001/fm001_${tab}", modelMap);
    }

    /**
     * 批量保存 提交制折 批次 数据
     * @param request
     * @param response
     */
    public void bSaveSubmit(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //提交页面、 完成页面
        String tab = request.getParameter("tab");
        if (StringUtil.isEqual(tab, "submit")) {
            // 房源ID列表
            String[] hsFmIds = requestUtil.getStrParam("hsFmIds").split(",");
            String fmMakeNum = request.getParameter("fmMakeNum");
            for (String hsFmId : hsFmIds) {
                if (StringUtil.isEmptyOrNull(hsFmId)) {
                    continue;
                }
                XmlBean rowData = new XmlBean();
                rowData.setStrValue("OpData.entityName", "HsFmInfo");
                rowData.setStrValue("OpData.EntityData.hsFmId", hsFmId);
                rowData.setStrValue("OpData.EntityData.fmMakeNum", fmMakeNum);
                rowData.setStrValue("OpData.EntityData.fmStatus", "12"); //制折中
                rowData.setStrValue("OpData.EntityData.fmToMakeDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
                // 调用服务
                svcRequest.addOp("SAVE_ENTITY", rowData);
            }
        } else if (StringUtil.isEqual(tab, "complete")) {
            // 房源ID列表
            String[] hsFmIds = requestUtil.getStrParam("hsFmIds").split(",");
            for (String hsFmId : hsFmIds) {
                if (StringUtil.isEmptyOrNull(hsFmId)) {
                    continue;
                }
                XmlBean rowData = new XmlBean();
                rowData.setStrValue("OpData.entityName", "HsFmInfo");
                rowData.setStrValue("OpData.EntityData.hsFmId", hsFmId);
                rowData.setStrValue("OpData.EntityData.fmStatus", "13"); //完成制折
                rowData.setStrValue("OpData.EntityData.fmMakedDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
                // 调用服务
                svcRequest.addOp("SAVE_ENTITY", rowData);
            }
        }

        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化 暂缓制折 页面
     */
    public ModelAndView pauseOrCancel(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String flag = request.getParameter("flag");
        String hsFmId = request.getParameter("hsFmId");

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HsFmInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsFmId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsFmId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName", "fmStopDesc");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (rspData != null) {
                String fmStopDesc = rspData.getStrValue("PageData.Row[0].fmStopDesc");
                modelMap.put("fmStopDesc", fmStopDesc);
            }
        }

        modelMap.put("flag", flag);
        modelMap.put("hsFmId", hsFmId);
        return new ModelAndView("/eland/fm/fm001/fm001_pause", modelMap);
    }

    /**
     * 修改暂缓说明
     * @param request
     * @param response
     */
    public void savePause(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String flag = request.getParameter("flag");
        String hsFmId = request.getParameter("hsFmId");
        String fmStopDesc = request.getParameter("fmStopDesc");
        if (StringUtil.isNotEmptyOrNull(hsFmId)) {
            XmlBean rowData = new XmlBean();
            rowData.setStrValue("OpData.entityName", "HsFmInfo");
            rowData.setStrValue("OpData.EntityData.hsFmId", hsFmId);
            rowData.setStrValue("OpData.EntityData.fmStopDesc", fmStopDesc);
            if (StringUtil.isEqual(flag, "0")) {
                rowData.setStrValue("OpData.EntityData.fmStatus", "10"); //暂缓制折
                rowData.setStrValue("OpData.EntityData.fmStopStDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            } else {
                rowData.setStrValue("OpData.EntityData.fmStatus", "11"); //取消暂缓  等待制折
                rowData.setStrValue("OpData.EntityData.fmStopEnDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            }
            // 调用服务
            svcRequest.addOp("SAVE_ENTITY", rowData);
        }

        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 生成五联单
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public ModelAndView viewWld(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        String hsCtId = request.getParameter("hsCtId");
        String hsFmId = request.getParameter("hsFmId");
        String agent = request.getHeader("USER-AGENT");
        String pId = "";
        String pFormCd = "";

        //查询计划pId
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HsFmInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsFmId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsFmId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "pId");
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        //启动查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean baseResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (baseResult != null) {
                pId = baseResult.getStrValue("PageData.Row.pId");
            }
        }

        //根据pId查询formCd
        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "PaymentPlan");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "pId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", pId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "pFormCd");
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        //启动查询
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean baseResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (baseResult != null) {
                pFormCd = baseResult.getStrValue("PageData.Row.pFormCd");
            }
        }

        if (StringUtil.isNotEmptyOrNull(hsId)) {
            svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean paramData = new XmlBean();
            // 生成报表模板
            paramData.setStrValue("OpData.formCd", pFormCd);
            // 生成报表参数
            paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", hsId);
            // 协议编号
            paramData.setStrValue("OpData.Report.Parameter[1].entityName", "HsCtInfo");
            paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "hsCtId");
            paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", hsCtId);
            //付款id
            paramData.setStrValue("OpData.Report.Parameter[2].entityName", "HsFmInfo");
            paramData.setStrValue("OpData.Report.Parameter[2].Property[0].attrName", "hsFmId");
            paramData.setStrValue("OpData.Report.Parameter[2].Property[0].value", hsFmId);

            // 增加到请求参数,调用服务
            svcRequest.addOp("GENERATE_REPORT", paramData);
            // 调用服务，执行数据查询
            svcResponse = transaction(svcRequest);

            // 处理服务返回结果
            String errMsg = "生成文档失败，请联系管理员!"
            boolean isSuccess = svcResponse.isSuccess();
            if (isSuccess) {
                XmlBean opResult = svcResponse.getFirstOpRsp("GENERATE_REPORT");

                if (opResult != null) {
                    // 返回结果类型
                    String resultType = opResult.getValue("Operation.OpResult.resultType");
                    if ("1".equals(resultType)) {
                        String docPath = opResult.getValue("Operation.OpResult.resultParam");
                        String docPrePath = "/reports/report"
                        String fromOp = request.getParameter("fromOp");
                        if (StringUtil.isEqual(fromOp, "view")) {
                            //本地查看
                            ModelMap modelMap = new ModelMap();
                            modelMap.put("docFileUrl", docPrePath + docPath);
                            return new ModelAndView("/eland/ph/ph013/ph013_doc_iframe", modelMap)
                        } else if (StringUtil.isEqual(fromOp, "download")) {
                            // 获取下载文件的后缀
                            String docExt = docPath.substring(docPath.lastIndexOf("."), docPath.length());
                            // 生成下载文件的展示名称
                            String docName = request.getParameter("docName");
                            if (StringUtil.isEmptyOrNull(docName)) {
                                docName = "信息报表(" + hsId + ")";
                            } else {
                                docName = java.net.URLDecoder.decode(docName, "utf-8")
                            }
                            docName = docName + "." + docExt;
                            // 获取下载的本地文件
                            String contextType = requestUtil.getStrParam("contextType");
                            File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                            // 文件下载输出到响应流
                            ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false, true);
                            return;
                        }
                    } else {
                        errMsg = errMsg + "只有文档类型的模板允许在该模块中生成,请调整表单配置!"
                    }
                }
            } else {
                errMsg = "生成文档失败，错误原因:\"" + svcResponse.getErrMsg() + "\"";
            }
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", errMsg);
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }
}