import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.exception.BusinessException
import com.shfb.oframe.core.util.rule.RuleManager
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
 * Created by zhouyx on 2016/5/11.
 * 裁决
 */
class ph015 extends GroovyController {
    /**
     * 初始化编辑页面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initS(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String recordType = request.getParameter("recordType");
        String method = request.getParameter("method");
        modelMap.put("recordType", recordType);
        modelMap.put("method", "edit");
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");

        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        String busiKey = ProcInsInfo.get("busiKey").toString();
        String hsId = "";
        if (StringUtil.isNotEmptyOrNull(busiKey)) {
            hsId = busiKey.replace("HouseInfo_", "");
        }
        if (StringUtil.isEmptyOrNull(hsId)) {
            throw new BusinessException("eland-web", "房屋数据未取到，请检查busiKey!");
        }
        modelMap.put("hsId", hsId);

        //调用规则 获取下一步处理人
        String prjCd = request.getParameter("prjCd");
        String procInsId = ProcInsInfo.get("procInsId").toString();
        String staffCd = RuleManager.executeRule(prjCd, "getNextPersonRule", [procInsId]);
        modelMap.put("staffCd", staffCd);

        return new ModelAndView("/eland/ph/ph015/ph015", modelMap);
    }

    /**
     * 初始化查看页面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initV(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String recordType = request.getParameter("recordType");
        String method = request.getParameter("method");
        modelMap.put("recordType", recordType);
        modelMap.put("method", "view");
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        //从busiKey 中截取 hsId
        String busiKey = ProcInsInfo.get("busiKey").toString();
        String hsId = "";
        if (StringUtil.isNotEmptyOrNull(busiKey)) {
            hsId = busiKey.replace("HouseInfo_", "");
        }
        if (StringUtil.isEmptyOrNull(hsId)) {
            throw new BusinessException("eland-web", "房屋数据未取到，请检查busiKey!");
        }
        modelMap.put("hsId", hsId);

        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        modelMap.put("taskId", hiTask.getStrValue("HiTaskInfo.taskId"));

        return new ModelAndView("/eland/ph/ph015/ph015", modelMap);
    }

    /**
     * 保存谈话
     * @param request
     * @param response
     * @return
     */
    public void saveWM(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean recordBean = new XmlBean();
        String nodePath = "OpData.";
        String recordId = request.getParameter("recordId");
        String recordRelId = request.getParameter("recordRelId");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        //如果recordId为空，则为新增谈话
        if (StringUtil.isEmptyOrNull(recordId)) {
            recordId = "\${recordId}";
        }
        recordBean.setStrValue(nodePath + "entityName", "RecordInfo");
        recordBean.setStrValue(nodePath + "EntityData.recordId", recordId);
        recordBean.setStrValue(nodePath + "EntityData.recordRelId", recordRelId);
        recordBean.setStrValue(nodePath + "EntityData.recordType", request.getParameter("recordType"));
        recordBean.setStrValue(nodePath + "EntityData.docId", request.getParameter("docId"));
        recordBean.setStrValue(nodePath + "EntityData.doRecordPerson", request.getParameter("doRecordPerson"));
        recordBean.setStrValue(nodePath + "EntityData.recordToPerson", request.getParameter("recordToPerson"));
        recordBean.setStrValue(nodePath + "EntityData.startTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdHmsWthH(startTime)));
        recordBean.setStrValue(nodePath + "EntityData.endTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdHmsWthH(endTime)));
        recordBean.setStrValue(nodePath + "EntityData.recordContext", request.getParameter("recordContext"));
        recordBean.setStrValue(nodePath + "EntityData.recordTopic", request.getParameter("recordTopic"));
        svcRequest.addOp("SAVE_ENTITY", recordBean);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView editView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String recordId = request.getParameter("recordId");
        String recordRelId = request.getParameter("recordRelId");
        String recordType = request.getParameter("recordType");
        String method = request.getParameter("method");
        String recordTopic = request.getParameter("recordTopic");
        modelMap.put("recordRelId", recordRelId);
        modelMap.put("recordId", recordId);
        modelMap.put("recordType", recordType);
        modelMap.put("method", method);
        modelMap.put("recordTopic", recordTopic);
        if (StringUtil.isNotEmptyOrNull(recordId)) {
            XmlBean opData = new XmlBean();
            String prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "RecordInfo");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "recordId");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", recordId);
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

            /*需要查询的字段*/
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "endTime");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "startTime");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "recordToPerson");
            opData.setStrValue(prePath + "ResultFields.fieldName[3]", "doRecordPerson");
            opData.setStrValue(prePath + "ResultFields.fieldName[4]", "recordContext");
            opData.setStrValue(prePath + "ResultFields.fieldName[5]", "docId");

            // 调用服务
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    XmlBean list = queryResult.getBeanByPath("OpResult.PageData.Row[0]");
                    modelMap.put("resultList", list.getRootNode());
                }
            }
        }
        String forwardPage = "/eland/ph/ph015/ph015_add";
        if (StringUtil.isEqual(recordType, '2')) {
            forwardPage = "/eland/ph/ph015/ph01501_add";
        }
        return new ModelAndView(forwardPage, modelMap);
    }

    /**
     * 删除谈话
     */
    public void deleteView(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String recordId = request.getParameter("recordId");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "RecordInfo");
        opData.setStrValue("OpData.entityKey", recordId);
        svcRequest.addOp("DELETE_ENTITY_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result},errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
}