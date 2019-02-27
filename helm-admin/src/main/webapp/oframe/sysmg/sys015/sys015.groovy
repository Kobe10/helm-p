import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys015 extends GroovyController {
    /**
     * 初始化
     * @param request
     * @param response
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys015/sys015", modelMap)
    }

    /**
     * 删除收藏
     * @param request 请求参数，含entityName参数
     * @param response 响应页面
     * @return
     */
    public void deleteJob(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String jobTaskId = requestUtil.getStrParam("jobTaskId");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "JobTaskInfo");
        opData.setStrValue("OpData.entityKey", jobTaskId);
        svcRequest.addOp("DELETE_JOB_TASK_INFO_CMPT", opData);

        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 操作作业
     * @param request
     * @param response
     * @return
     */
    public ModelAndView operationJob(HttpServletRequest request, HttpServletResponse response) {
        String jobTaskId = request.getParameter("jobTaskId");
        String type = request.getParameter("type");
        String threadId = request.getParameter("threadId");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.dealMethod", type);
        opData.setStrValue("OpData.taskId", jobTaskId);
        if (type == "2") {
            opData.setStrValue("OpData.threadId", threadId);
        }
        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("DEAL_JOB_TASK_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 查询历史
     * @param request
     * @param response
     * @return
     */
    public ModelAndView query(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        String jobTaskId = requestUtil.getStrParam("jobTaskId");

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "JobTaskLog");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "jobTaskId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", jobTaskId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "jobStartTime");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "desc");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "jobStartTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "jobStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "jobEndTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "jobExecBy");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "jobNote");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "threadId");

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            List<XmlNode> resultList = new ArrayList<>();
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                if (rowCount > 10) {
                    rowCount = 10;
                }
                for (int i = 0; i < rowCount; i++) {
                    XmlBean list = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                    list.setStrValue("Row.jobTaskId", jobTaskId);
                    resultList.add(list.getRootNode());
                }
                modelMap.put("resultList", resultList);
            }
        }
        return new ModelAndView("/oframe/sysmg/sys015/sys015log_list", modelMap)
    }

    /**
     * 初始化修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView editView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String jobTaskId = request.getParameter("jobTaskId");
        if (StringUtil.isNotEmptyOrNull(jobTaskId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "JobTaskInfo");
            opData.setStrValue("OpData.entityKey", jobTaskId);
            // 调用服务查询数据
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean JobTaskInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                        .getBeanByPath("Operation.OpResult.JobTaskInfo");
                if (JobTaskInfo != null) {
                    modelMap.put("jobInfo", JobTaskInfo.getRootNode());
                }
            }
        }
        return new ModelAndView("/oframe/sysmg/sys015/sys015_add", modelMap);
    }

    /**
     * 添加作业
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String jobNameCh = request.getParameter("jobNameCh");
        String jobExecTime = request.getParameter("jobExecTime");
        String jobDescription = request.getParameter("jobDescription");
        String jobType = request.getParameter("jobType");
        String jobStartParam = request.getParameter("jobStartParam");
        String jobTaskId = request.getParameter("jobTaskId");
        String jobStatus = request.getParameter("jobStatus");
        String jobRunNow = request.getParameter("jobRunNow");
        if (!StringUtil.isEqual("1", jobStatus)) {
            jobStatus = "0";
        }
        XmlBean dOpData = new XmlBean();
        String nodePath = "OpData.";
        if (StringUtil.isNotEmptyOrNull(jobTaskId)) {
            dOpData.setStrValue(nodePath + "EntityData.jobTaskId", jobTaskId);
        }
        dOpData.setStrValue(nodePath + "entityName", "JobTaskInfo");
        dOpData.setStrValue(nodePath + "EntityData.jobStatus", jobStatus);
        dOpData.setStrValue(nodePath + "EntityData.jobRunNow", jobRunNow);
        dOpData.setStrValue(nodePath + "EntityData.jobNameCh", jobNameCh);
        dOpData.setStrValue(nodePath + "EntityData.jobExecTime", jobExecTime);
        dOpData.setStrValue(nodePath + "EntityData.jobDescription", jobDescription);
        dOpData.setStrValue(nodePath + "EntityData.jobType", jobType);
        dOpData.setStrValue(nodePath + "EntityData.jobStartParam", jobStartParam);
        svcRequest.addOp("SAVE_JOB_TASK_INFO_CMPT", dOpData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

}
