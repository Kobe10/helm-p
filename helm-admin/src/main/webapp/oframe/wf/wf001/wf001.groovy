import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 工作流通用处理流程
 */
class wf001 extends GroovyController {

    /**
     * 启动工作流
     * <p>
     *     1、获取流程定义KEY，调用流程定义说明；
     *     2、根据流程定义 出参获取下一步处理人信息；
     *     3、根据流程定义说明展示流程处理界面;（获取表单定义formCd，获取定义表单页面）
     * </p>
     * @param request 请求信息
     *          1、procDefKey： 流程定义编码(必须提供）
     *          2、busiKey：流程业务编号,直接存入流程模型中，如果加工请在接口调用前处理。(根据业务需要可选)（HouseInfo_XXX等)
     *          3、backUrl：回退的URL，在取消的时候回退到指定的界面(可选）
     *          4、prjCd: 项目编号，必须提供，否则流程中相关界面无法获取项目编号信息;
     *          5、其他参数: 其他参数服务都会接收并推动到界面，以"rq_参数名称"的形式分配到界面
     * @param response 响应结果
     */
    public ModelAndView startWf(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 流程定义KEY
        String procDefKey = requestUtil.getStrParam("procDefKey");
        String busiKey = requestUtil.getStrParam("busiKey");
        String backUrl = requestUtil.getStrParam("backUrl");

        // 推送所有的请求参数到界面
        Map<String, Object> requestData = requestUtil.getRequestMap(request);
        modelMap.put("requestData", requestData);

        // 调用服务获取流程定义的
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用流程组件获取，流程的定义信息
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.procDefKey", procDefKey);
        svcRequest.addOp("QUERY_SINGLE_PROC_DEF_BY_KEY", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean procDefInfo = svcResponse.getFirstOpRsp("QUERY_SINGLE_PROC_DEF_BY_KEY")
                    .getBeanByPath("Operation.OpResult.ProcDefInfo");
            if (procDefInfo != null) {
                String stFormKey = procDefInfo.getStrValue("ProcDefInfo.stFormKey");
                if (StringUtil.isNotEmptyOrNull(stFormKey)) {
                    JSONObject jsonObject = JSONObject.fromObject(stFormKey);
                    String editPage = jsonObject.get("editPage");
                    if (StringUtil.isEmptyOrNull(editPage)) {
                        /**
                         * 调用组件获取 要展示的表单
                         */
                        svcRequest = RequestUtil.getSvcRequest(request);
                        XmlBean paramData = new XmlBean();
                        // 生成报表模板
                        paramData.setStrValue("OpData.WfExecute.procDefKey", procDefKey);
                        paramData.setStrValue("OpData.WfExecute.busiKey", busiKey);
                        //设置 展示状态： 编辑模式、展示模式、对比模式 todo:如果有动态参数传到这里
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.attrName", "showFlag");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.value", "edit");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.attrName", "taskId");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.value", "");

                        // 增加到请求参数,调用服务
                        svcRequest.addOp("TASK_FORM", paramData);
                        // 调用服务，执行数据查询
                        SvcResponse pageResponse = query(svcRequest);
                        if (pageResponse.isSuccess()) {
                            XmlBean opResult = pageResponse.getFirstOpRsp("TASK_FORM");
                            List operaList = new ArrayList();
                            int operNum = opResult.getListNum("Operation.form.OperationDefs.OperationDef");
                            for (int i = 0; i < operNum; i++) {
                                operaList.add(opResult.getBeanByPath("Operation.form.OperationDefs.OperationDef[${i}]").getRootNode());
                            }
                            String pageStr = opResult.getStrValue("Operation.form.resultTemplate");
                            String operDefCode = opResult.getStrValue("Operation.form.OperationDefs.operDefCode");

                            //返回展示页面字符串
                            modelMap.put("article", pageStr);
                            modelMap.put("operaList", operaList);
                            modelMap.put("operDefCode", operDefCode);
                        }
                    } else {
                        // 获取流程的启动groovy层路基
                        modelMap.put("url", editPage);
                    }
                }
                modelMap.put("procDefInfo", procDefInfo.getRootNode());
            }
        }
        modelMap.put("startStaff", svcRequest.getReqStaff());
        modelMap.put("startDate", DateUtil.toStringYmdwithsfm(DateUtil.getSysDate()));
        modelMap.put("procDefKey", procDefKey);
        modelMap.put("busiKey", busiKey);
        modelMap.put("backUrl", backUrl);
        modelMap.put("prjCd", request.getParameter("prjCd"));
        // 返回视图
        return new ModelAndView("/oframe/wf/wf001/wf001_start", modelMap)
    }

    /**
     *  启动流程服务接口
     *  入参：
     *   框架固有参数：
     *      procDefKey：流程定义编码，自动查找最新的流程启动流程;
     *      procDefId: 流程定义ID，与procDefKey两者保留一个就可以;
     *      procInsName: 情动的流程实例名称，在流程查看的时候显示;
     *      busiKey：启动流程关联的业务实体码值
     *   流程业务参数：
     *      WF_PRI_XXX：流程实例变量, 请求中以WF_PRI_开头的参数将作为流程实例变量存储，所有环节共享后面的覆盖前面的。
     *      WF_TSK_XXX: 任务节点变量, 请求中以WF_TSK_开头的参数将作为任务实例变量存储，当前任务可见。
     *  出参：
     *      procInsId: 流程实例ID
     * @param request
     * @param response
     */
    public void doStartWf(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求参数信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 签约信息保存
        XmlBean opData = new XmlBean();
        String prePath = "OpData.WfExecute.";
        opData.setStrValue(prePath + "procDefId", request.getParameter("procDefId"));
        opData.setStrValue(prePath + "procInsName", requestUtil.getStrParam("procInsName"));
        opData.setStrValue(prePath + "procDefKey", request.getParameter("procDefKey"));
        opData.setStrValue(prePath + "busiKey", request.getParameter("busiKey"));
        opData.setStrValue(prePath + "procStUser", svcRequest.getReqStaffCd());

        // 处理所有的流程变量
        Map<String, Object> requestData = requestUtil.getRequestMap(request);
        prePath = "OpData.WfExecute.Variables.";
        String preProcVariablesPath = "OpData.WfExecute.ProcBidVars.";

        for (Map.Entry<String, Object> temp : requestData.entrySet()) {
            String key = temp.getKey();
            if (temp.getKey().startsWith("WF_TSK_")) {
                key = key.substring(7, key.length());
                opData.setStrValue(preProcVariablesPath + key, temp.getValue());
            } else if (temp.getKey().startsWith("WF_PRI_")) {
                key = key.substring(7, key.length());
                opData.setStrValue(prePath + key, temp.getValue());
            }
        }
        // 流程关联的项目编号
        String procPrjCd = request.getParameter("procPrjCd");
        if (StringUtil.isEmptyOrNull(procPrjCd)) {
            procPrjCd = request.getParameter("prjCd");
        }
        opData.setStrValue(prePath + "procPrjCd", procPrjCd);
        String invokeCmpt = "SAVE_START_PROC_BY_DEF_KEY";
        /**
         * 根据标志 busiStartFlag  判断是否处理业务数据,
         * 该标志在 wf001.js doStartWithBusiness() 方法指定
         */
        String busiStartFlag = request.getParameter("busiStartFlag");
        if (StringUtil.isNotEmptyOrNull(busiStartFlag) && StringUtil.isEqual(busiStartFlag, "true")) {
            //调用方法保存业务数据 不入库，入上下文
            doSaveBusinessData(request, svcRequest);
            invokeCmpt = "SAVE_START_PROC_BY_KEY_WH_DEFF";
        }
        // 增加绑定固定流程变量
        svcRequest.addOp(invokeCmpt, opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String jsonData = "";
        if (svcResponse.isSuccess()) {
            String procInsId = svcResponse.getFirstOpRsp(invokeCmpt).getStrValue("Operation.OpResult.WfExecute.procInsId");
            jsonData = "procInsId: ${procInsId}";
        }
        ResponseUtil.printSvcResponse(response, svcResponse, jsonData);
    }

    /**
     * 查看工作流任务
     * <p>
     *     1、获取流程实例编号
     *     2、调用服务获取流程实例信息，含流程中的任务列表
     *     3、任务处理中，并且为当前操作人显示处理细节，非当前人只显示进行中环节
     * </p>
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView viewWf(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理对象
        ModelMap modelMap = new ModelMap();
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = null;
        // 流程定义KEY
        String procInsId = requestUtil.getStrParam("procInsId");
        String backUrl = requestUtil.getStrParam("backUrl");
        // 调用服务获取流程定义的
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 请求参数
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.procInsId", procInsId);
        svcRequest.addOp("QUERY_RU_PROC_DETAIL_BY_ID", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);

        //拼接流程级变量  任务级变量里的 操作定义 js
        String operDefCodeAll = "";
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_RU_PROC_DETAIL_BY_ID").getBeanByPath("Operation.OpResult");
            // 流程信息不存在
            if (queryResult == null) {
                return null;
            }
            XmlBean procInsInfo = queryResult.getBeanByPath("OpResult.ProcInsInfo");
            prjCd = procInsInfo.getStrValue("ProcInsInfo.Variables.procPrjCd");
            procInsInfo.setStrValue("ProcInsInfo.procStTimeDisp",
                    DateUtil.toStringYmdwithsfm(DateUtil.toDateYmdHms(procInsInfo.getStrValue("ProcInsInfo.procStTime"))));
            String stFormKey = procInsInfo.getStrValue("ProcInsInfo.stFormKey");
            if (StringUtil.isNotEmptyOrNull(stFormKey)) {
                JSONObject jsonObject = JSONObject.fromObject(stFormKey);
                String viewPage = jsonObject.get("viewPage");
                if (StringUtil.isNotEmptyOrNull(viewPage)) {
                    procInsInfo.setStrValue("ProcInsInfo.viewPage", viewPage);
                } else {
                    String procDefKey = procInsInfo.getStrValue("ProcInsInfo.procDefKey");
                    String busiKey = procInsInfo.getStrValue("ProcInsInfo.busiKey");
                    /**
                     * 调用组件获取 要展示的表单
                     */
                    svcRequest = RequestUtil.getSvcRequest(request);
                    svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
                    XmlBean paramData = new XmlBean();
                    // 生成报表模板
                    paramData.setStrValue("OpData.WfExecute.procInsId", procInsId);
                    paramData.setStrValue("OpData.WfExecute.procDefKey", procDefKey);
                    paramData.setStrValue("OpData.WfExecute.busiKey", busiKey);
                    //设置 展示状态： 编辑模式、展示模式、对比模式
                    paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.attrName", "showFlag");
                    paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.value", "show");
                    paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.attrName", "taskId");
                    paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.value", "");

                    // 增加到请求参数,调用服务
                    svcRequest.addOp("TASK_FORM", paramData);
                    // 调用服务，执行数据查询
                    SvcResponse pageResponse = query(svcRequest);
                    if (pageResponse.isSuccess()) {
                        XmlBean opResult = pageResponse.getFirstOpRsp("TASK_FORM");
                        List operaList = new ArrayList();
                        int operNum = opResult.getListNum("Operation.form.OperationDefs.OperationDef");
                        for (int i = 0; i < operNum; i++) {
                            operaList.add(opResult.getBeanByPath("Operation.form.OperationDefs.OperationDef[${i}]").getRootNode());
                        }
                        String pageStr = opResult.getStrValue("Operation.form.resultTemplate");
                        String operDefCode = opResult.getStrValue("Operation.form.OperationDefs.operDefCode");
                        operDefCodeAll += ";" + operDefCode;
                        //返回展示页面字符串
                        modelMap.put("procPage", pageStr);
//                        modelMap.put("operaList", operaList);
//                        modelMap.put("operDefCode", operDefCode);
                    }
                }
            }
            // 流程实例信息
            modelMap.put("procInstInfo", procInsInfo);
            modelMap.put("PROC_INST_INFO", procInsInfo.getRootNode());
            // 执行中任务信息
            int runTaskCount = queryResult.getListNum("OpResult.RuTaskInfos.RuTaskInfo");
            List<XmlNode> runTaskInfos = new ArrayList<XmlNode>(runTaskCount);
            for (int i = 0; i < runTaskCount; i++) {
                XmlBean runTask = queryResult.getBeanByPath("OpResult.RuTaskInfos.RuTaskInfo[${i}]");
                runTask.setStrValue("RuTaskInfo.createTimeDisp",
                        DateUtil.toStringYmdwithsfm(DateUtil.toDateYmdHms(runTask.getStrValue("RuTaskInfo.createTime"))));
                String tskFormKey = runTask.getStrValue("RuTaskInfo.tskFormKey");
                if (StringUtil.isNotEmptyOrNull(tskFormKey)) {
                    JSONObject jsonObject = JSONObject.fromObject(tskFormKey);
                    String editPage = jsonObject.get("editPage");
                    if (StringUtil.isNotEmptyOrNull(editPage)) {
                        // 获取流程的启动groovy层路基
                        runTask.setStrValue("RuTaskInfo.editPage", editPage);
                    } else {
                        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
                        String procDefKey = procInsInfo.getStrValue("ProcInsInfo.procDefKey");
                        String busiKey = procInsInfo.getStrValue("ProcInsInfo.busiKey");
                        /**
                         * 调用组件获取 要展示的表单
                         */
                        svcRequest = RequestUtil.getSvcRequest(request);
                        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
                        XmlBean paramData = new XmlBean();
                        // 生成报表模板
                        paramData.setStrValue("OpData.WfExecute.procDefKey", procDefKey);
                        paramData.setStrValue("OpData.WfExecute.taskId", taskId);
                        paramData.setStrValue("OpData.WfExecute.busiKey", busiKey);
                        //设置 展示状态： 编辑模式、展示模式、对比模式
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.attrName", "showFlag");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.value", "edit");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.attrName", "taskId");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.value", taskId);

                        // 增加到请求参数,调用服务
                        svcRequest.addOp("TASK_FORM", paramData);
                        // 调用服务，执行数据查询
                        SvcResponse pageResponse = query(svcRequest);
                        if (pageResponse.isSuccess()) {
                            XmlBean opResult = pageResponse.getFirstOpRsp("TASK_FORM");
                            List operaList = new ArrayList();
                            int operNum = opResult.getListNum("Operation.form.OperationDefs.OperationDef");
                            for (int j = 0; j < operNum; j++) {
                                operaList.add(opResult.getBeanByPath("Operation.form.OperationDefs.OperationDef[${j}]").getRootNode());
                            }
                            String pageStr = opResult.getStrValue("Operation.form.resultTemplate");
                            String operDefCode = opResult.getStrValue("Operation.form.OperationDefs.operDefCode");
                            operDefCodeAll += ";" + operDefCode;
                            //返回展示页面字符串
//                            modelMap.put("runPage", pageStr);
                            modelMap.put("operaList", operaList);
//                            modelMap.put("operDefCode", operDefCode);
                            runTask.setStrValue("RuTaskInfo.runPage", pageStr);
//                            runTask.setStrValue("RuTaskInfo.operDefCode", operDefCode);
                        }
                    }
                }
                runTaskInfos.add(runTask.getRootNode());
                // 保存到model中，用于传递任务信息
                modelMap.put("RUN_TASK_INF_" + i, runTask);
            }
            //推送所有表单 js
            modelMap.put("operDefCode", operDefCodeAll);

            if (StringUtil.isEmptyOrNull(request.getParameter("query"))) {
                modelMap.put("runTaskInfos", runTaskInfos);
            }
        }

        // 调用流程组件获取，流程的定义信息
        modelMap.put("currentStaff", svcRequest.getReqStaffCd());
        modelMap.put("currentDate", DateUtil.toStringYmdwithsfm(DateUtil.getSysDate()));
        modelMap.put("backUrl", backUrl);
        modelMap.put("procInsId", procInsId);
        modelMap.put("prjCd", prjCd);
        // 获取流程的启动groovy层路基，
        return new ModelAndView("/oframe/wf/wf001/wf001_view", modelMap)
    }

    /**
     * 查看工作流任务
     * <p>
     *     1、获取流程实例编号
     *     2、调用服务获取流程实例信息，含流程中的任务列表
     *     3、任务处理中，并且为当前操作人显示处理细节，非当前人只显示进行中环节
     * </p>
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView viewHis(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理对象
        ModelMap modelMap = new ModelMap();
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 流程定义KEY
        String procInsId = requestUtil.getStrParam("procInsId");
        String prjCd = requestUtil.getStrParam("prjCd");
        // 调用服务获取流程定义的
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 请求参数
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.procInsId", procInsId);
        svcRequest.addOp("QUERY_RU_PROC_DETAIL_BY_ID", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_RU_PROC_DETAIL_BY_ID").getBeanByPath("Operation.OpResult");
            XmlBean procInsInfo = queryResult.getBeanByPath("OpResult.ProcInsInfo");
            procInsInfo.setStrValue("ProcInsInfo.procStTimeDisp",
                    DateUtil.toStringYmdwithsfm(DateUtil.toDateYmdHms(procInsInfo.getStrValue("ProcInsInfo.procStTime"))));
            String stFormKey = procInsInfo.getStrValue("ProcInsInfo.stFormKey");
            if (StringUtil.isNotEmptyOrNull(stFormKey)) {
                JSONObject jsonObject = JSONObject.fromObject(stFormKey);
                String viewPage = jsonObject.get("viewPage");
                procInsInfo.setStrValue("ProcInsInfo.viewPage", viewPage);
            }
            // 流程实例信息
            modelMap.put("procInstInfo", procInsInfo);
            modelMap.put("PROC_INST_INFO", procInsInfo.getRootNode());
            // 历史任务
            int hisTaskCount = queryResult.getListNum("OpResult.HiTaskInfos.HiTaskInfo");
            List<XmlNode> hisTaskInfos = new ArrayList<XmlNode>(hisTaskCount);
            for (int i = 0; i < hisTaskCount; i++) {
                XmlBean histTask = queryResult.getBeanByPath("OpResult.HiTaskInfos.HiTaskInfo[${i}]");
                histTask.setStrValue("HiTaskInfo.endTimeDisp",
                        DateUtil.toStringYmdwithsfm(DateUtil.toDateYmdHms(histTask.getStrValue("HiTaskInfo.endTime"))));
                String tskFormKey = histTask.getStrValue("HiTaskInfo.tskFormKey");
                if (StringUtil.isNotEmptyOrNull(tskFormKey)) {
                    JSONObject jsonObject = JSONObject.fromObject(tskFormKey);
                    String viewPage = jsonObject.get("viewPage");
                    if (StringUtil.isNotEmptyOrNull(viewPage)) {
                        // 获取流程的启动groovy 路径
                        histTask.setStrValue("HiTaskInfo.viewPage", viewPage);
                    } else {
                        String busiKey = procInsInfo.getStrValue("ProcInsInfo.busiKey");
                        String taskId = histTask.getStrValue("HiTaskInfo.taskId"); ;
                        /**
                         * 调用组件获取 要展示的表单
                         */
                        svcRequest = RequestUtil.getSvcRequest(request);
                        XmlBean paramData = new XmlBean();
                        // 生成报表模板
                        paramData.setStrValue("OpData.WfExecute.taskId", taskId);
                        paramData.setStrValue("OpData.WfExecute.busiKey", busiKey);
                        //设置 展示状态： 编辑模式、展示模式、对比模式
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.attrName", "showFlag");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[0].Property.value", "show");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.attrName", "taskId");
                        paramData.setStrValue("OpData.WfExecute.Report.Parameter[1].Property.value", taskId);

                        // 增加到请求参数,调用服务
                        svcRequest.addOp("TASK_FORM", paramData);
                        // 调用服务，执行数据查询
                        SvcResponse pageResponse = query(svcRequest);
                        if (pageResponse.isSuccess()) {
                            XmlBean opResult = pageResponse.getFirstOpRsp("TASK_FORM");
                            List operaList = new ArrayList();
                            int operNum = opResult.getListNum("Operation.form.OperationDefs.OperationDef");
                            for (int j = 0; j < operNum; j++) {
                                operaList.add(opResult.getBeanByPath("Operation.form.OperationDefs.OperationDef[${j}]").getRootNode());
                            }
                            String pageStr = opResult.getStrValue("Operation.form.resultTemplate");
//                            String operDefCode = opResult.getStrValue("Operation.form.OperationDefs.operDefCode");

                            histTask.setStrValue("HiTaskInfo.hisPage", pageStr);
                            //返回展示页面字符串
//                            modelMap.put("hisPage", pageStr);
//                            modelMap.put("operaList", operaList);
//                            modelMap.put("operDefCode", operDefCode);
                        }
                    }
                }
                hisTaskInfos.add(histTask.getRootNode());
                //
                modelMap.put("HIS_TASK_INF_" + i, histTask);
            }
            modelMap.put("hisTaskInfos", hisTaskInfos);
        }
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/oframe/wf/wf001/wf001_his", modelMap)
    }

    /**
     * 处理并完成任务
     *  请求参数要求存在：
     *      taskId: 处理的任务编号，必须提供
     *  请求参数中：
     *      WF_PRI_：开头的参数将作为流程实例变量存储
     *      WF_TSK_：开头的参数将作为任务实例变量存储
     * @param request 业务请求
     * @param response 响应结果
     */
    public ModelAndView doTask(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> requestData = requestUtil.getRequestMap(request);
        String preProcVariablesPath = "OpData.WfExecute.ProcVariables.";
        String preTskBindPath = "OpData.WfExecute.TskBidVars.";
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.taskId", requestUtil.getStrParam("taskId"));
        for (Map.Entry<String, Object> temp : requestData.entrySet()) {
            String key = temp.getKey();
            if (temp.getKey().startsWith("WF_PRI_")) {
                key = key.substring(7, key.length());
                opData.setStrValue(preProcVariablesPath + key, temp.getValue());
            } else if (key.startsWith("WF_TSK_")) {
                key = key.substring(7, key.length());
                opData.setStrValue(preTskBindPath + key, temp.getValue());
            }
        }
        /**
         * 根据标志 busiStartFlag  判断是否处理业务数据
         */
        String busiDoTaskFlag = request.getParameter("busiDoTaskFlag");
        if (StringUtil.isNotEmptyOrNull(busiDoTaskFlag) && StringUtil.isEqual(busiDoTaskFlag, "true")) {
            //TODO：调用方法保存业务数据
            doSaveBusinessData(request, svcRequest);
        }
        svcRequest.addOp("SAVE_COMPLETE_TASK", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 取消删除流程
     *  请求参数要求存在：
     *      procInsId：流程实例编号
     *
     * @param request 业务请求
     * @param response 响应结果
     */
    public ModelAndView delWf(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.procInsId", request.getParameter("procInsId"));
        svcRequest.addOp("DEL_PROC_INS_BY_ID", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 查看运行中流程的运转流程图
     *  请求参数要求存在：
     *      procInsId：流程实例编号
     *
     * @param request 业务请求
     * @param response 响应结果
     */
    public void viewRunWfPic(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.procDefId", request.getParameter("procDefId"));
        opData.setStrValue("OpData.WfExecute.procInsId", request.getParameter("procInsId"));
        svcRequest.addOp("GENERATE_RU_DIAGARM", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("GENERATE_RU_DIAGARM");
            String docPath = resultBean.getStrValue("Operation.OpResult.filePath");
            //拿到文件名,封装成完整路径
            File localFile = ServerFile.getFile(docPath);
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, null, "运行流程图.png", localFile, request.getHeader("USER-AGENT"), false);
        }
    }

    /**
     * 处理业务数据，讲业务数据调用组件保存至 svcRequest
     * @param request
     * @param svcRequest
     */
    private void doSaveBusinessData(HttpServletRequest request, SvcRequest svcRequest) {
        /**
         * 拼接 表单基本信息 报文
         */
        Map<String, String[]> reqParamMap = request.getParameterMap();

        // 创建以某一个实体为 key 的map
        Map<String, XmlBean> entityMap = new LinkedHashMap<String, XmlBean>();
        //entityMap = [HouseInfo_20 : updateEntity]
        if (reqParamMap != null) {
            Iterator<String> it = reqParamMap.keySet().iterator();
            while (it.hasNext()) {
                String reqAttr = it.next();             //从前端接收到的提交属性： eg: WF_BUSI_HouseInfo_hsCd_20
                if (!reqAttr.startsWith("WF_BUSI_")) {
                    continue;
                }
                //截取需要保存的业务属性
                reqAttr = reqAttr.substring(8, reqAttr.length());

                //截取 下面所需的 实体名、属性名、主键值：
                int first_order = reqAttr.indexOf("_");
                int last_order = reqAttr.lastIndexOf("_");
                String entityAttr = reqAttr.substring(first_order + 1, last_order);  //eg: hsCd
                String entityName = reqAttr.substring(0, first_order);               //eg: HouseInfo
                String entityPrjKeyVal = reqAttr.substring(last_order + 1);          //eg：20

                //截取不同实体需要存储的Map key ：  eg：HouseInfo_20
                String mapKey = entityName + "_" + entityPrjKeyVal;

                //获取请求参数里的  参数值：value[]
                String[] listValue = reqParamMap.get("WF_BUSI_" + reqAttr);
                String value = null;
                if (listValue.length == 1) {
                    value = listValue[0];
                } else {
                    //结果值 如果不止 一个 需要 处理
                    value = listValue.join(",");
                }

                //拼接map里 每个key 对应 value节点报文： eg：updateEntity = <OpData><EntityData><hsId>20</hsId></EntityData></OpData>
                XmlBean updateEntity = entityMap.get(mapKey);
                if (updateEntity == null) {
                    updateEntity = new XmlBean();
                    updateEntity.setStrValue("OpData.entityName", entityName);
                    updateEntity.setStrValue("OpData.EntityData.${entityAttr}", value);
                    entityMap.put(mapKey, updateEntity);
                } else {
                    updateEntity.setStrValue("OpData.EntityData.${entityAttr}", value);
                }
            }
            for (Object temp : entityMap.keySet()) {
                XmlBean tempBean = entityMap.get(temp);
                tempBean.setStrValue("OpData.toDb", "1");
                tempBean.setStrValue("OpData.toContext", "1");
                svcRequest.addOp("SAVE_ENTITY", tempBean);
            }
        }
    }
}
