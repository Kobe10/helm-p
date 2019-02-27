import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * 流程管理控制器
 */
class wf004 extends GroovyController {

    /**
     * 撤销流程
     * @param request 请求信息
     * @param response 响应结果
     */
    public void revoke(HttpServletRequest request, HttpServletResponse response) {
        String procInsId = request.getParameter("procInsId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean OpData = new XmlBean();
        OpData.setStrValue("OpData.WfExecute.procInsId", procInsId);
        svcRequest.addOp("DEL_PROC_INS_BY_ID", OpData);
        SvcResponse svcResponse = transaction(svcRequest);

        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 流程列表查询-初始化界面
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView initProcess(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int createStaffId = svcRequest.getReqStaffIdInt();
        String StaffCd;
        String StaffName;
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Staff.";
        reqData.setValue(nodePath + "staffId", createStaffId);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
            StaffCd = staffBean.getStrValue("Staff.StaffCd");
            StaffName = staffBean.getStrValue("Staff.StaffName");
        }
        modelMap.put("StaffCd", StaffCd);
        modelMap.put("StaffName", StaffName);
        return new ModelAndView("/oframe/wf/wf004/wf004", modelMap)
    }

    /**
     * 任务列表查询-初始化界面
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView initTask(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int createStaffId = svcRequest.getReqStaffIdInt();
        String StaffCd;
        String StaffName;
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Staff.";
        reqData.setValue(nodePath + "staffId", createStaffId);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
            StaffCd = staffBean.getStrValue("Staff.StaffCd");
            StaffName = staffBean.getStrValue("Staff.StaffName");
        }
        modelMap.put("StaffCd", StaffCd);
        modelMap.put("StaffName", StaffName);
        return new ModelAndView("/oframe/wf/wf004/wf00401", modelMap)
    }

    /**
     * 打开委托流程页面
     */
    public ModelAndView delegationWfView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String taskId = request.getParameter("taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/oframe/wf/wf004/wf00401_delegationWf", modelMap);
    }


    /**
     * 委托 保存
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView delegationWf(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //取值
        String taskId = request.getParameter("taskId");
        String wfStaff = request.getParameter("wfStaff");//被委托人staffCode
        //入参
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.taskId",taskId);
        opData.setStrValue("OpData.wfStaff", wfStaff);
        //调用服务
        svcRequest.addOp("SAVE_WEI_TUO_STAFF_CMPT",opData);
        //启动服务
        SvcResponse svcResponse  = transaction(svcRequest);
        //返回值
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

}
