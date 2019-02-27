import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 个人待办任务面板
 */
class mb001 extends GroovyController {

    /**
     * 待办任务统计面板
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        paramMap.remove("pageSize");
        paramMap.remove("sortColumn");
        paramMap.remove("sortOrder");
        paramMap.remove("sortColumn");
        paramMap.remove("queryCondition");

        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("OpData.PageInfo.currentPage", "1");
        reqData.setValue("OpData.PageInfo.pageSize", "7");
        // 排序信息
        reqData.setValue("OpData.SortInfo.sortColumn", "createTime");
        reqData.setValue("OpData.SortInfo.sortOrder", "asc");
        // 当前处理人
        reqData.setStrValue("OpData.ParamInfo.Param[0].name", "assignee");
        reqData.setStrValue("OpData.ParamInfo.Param[0].value", svcRequest.getReqStaffCd());
        // 调用服务
        svcRequest.addOp("QUERY_PROC_TASK_PAGE", reqData);
        SvcResponse svcResponse = query(svcRequest);
        // 添加分页查询数据参数
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            // 返回结果集
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_PROC_TASK_PAGE").getBeanByPath("Operation.OpResult");
            int rowNum = rspData.getListNum("OpResult.PageData.Row");
            List resultList = new ArrayList(rowNum);
            for (int j = 0; j < rowNum; j++) {
                XmlBean result = rspData.getBeanByPath("OpResult.PageData.Row[${j}]");
                resultList.add(result.getRootNode());
            }
            // 返回结果集
            modelMap.addAttribute("returnList", resultList);
            Map<String, Object> pagination = new HashMap<String, Object>();
            pagination.put("currentPage", rspData.getValue("OpResult.PageInfo.currentPage"));
            pagination.put("totalPage", rspData.getValue("OpResult.PageInfo.totalPage"));
            pagination.put("totalRecord", rspData.getValue("OpResult.PageInfo.totalRecord"));

        }
        // 跳转页面
        return new ModelAndView("/eland/mb/mb001/mb001", modelMap);
    }

    /**
     * 我的流程统计面板
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView myTask(HttpServletRequest request, HttpServletResponse response) {

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        paramMap.remove("pageSize");
        paramMap.remove("sortColumn");
        paramMap.remove("sortOrder");
        paramMap.remove("sortColumn");
        paramMap.remove("queryCondition");

        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("OpData.PageInfo.currentPage", "1");
        reqData.setValue("OpData.PageInfo.pageSize", "7");
        // 排序信息
        reqData.setValue("OpData.SortInfo.sortColumn", "procStTime");
        reqData.setValue("OpData.SortInfo.sortOrder", "asc");
        // 当前处理人
        reqData.setStrValue("OpData.ParamInfo.Param[0].name", "procStUser");
        reqData.setStrValue("OpData.ParamInfo.Param[0].value", svcRequest.getReqStaffCd());
        // 调用服务
        svcRequest.addOp("QUERY_PROC_INSTANC_EPAGE", reqData);
        SvcResponse svcResponse = query(svcRequest);
        // 添加分页查询数据参数
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            // 返回结果集
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_PROC_INSTANC_EPAGE").getBeanByPath("Operation.OpResult");
            int rowNum = rspData.getListNum("OpResult.PageData.Row");
            List resultList = new ArrayList(rowNum);
            for (int j = 0; j < rowNum; j++) {
                XmlBean result = rspData.getBeanByPath("OpResult.PageData.Row[${j}]");
                resultList.add(result.getRootNode());
            }
            // 返回结果集
            modelMap.addAttribute("returnList", resultList);
            Map<String, Object> pagination = new HashMap<String, Object>();
            pagination.put("currentPage", rspData.getValue("OpResult.PageInfo.currentPage"));
            pagination.put("totalPage", rspData.getValue("OpResult.PageInfo.totalPage"));
            pagination.put("totalRecord", rspData.getValue("OpResult.PageInfo.totalRecord"));

        }
        // 跳转页面
        return new ModelAndView("/eland/mb/mb001/mb00102", modelMap);
    }
}
