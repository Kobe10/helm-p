import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.properties.PropertiesUtil
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

class sys014 extends GroovyController {
    /**
     * 初始化消息详情界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView showInfo(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String id = request.getParameter("noticeId");
        String num = request.getParameter("num");
        XmlBean OpData = new XmlBean();
        OpData.setStrValue("OpData.NoticeInfo.noticeReadStatus", num);
        svcRequest.addOp("QUERY_NOTICE_INFO", OpData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean noticeInfoList = svcResponse.getFirstOpRsp("QUERY_NOTICE_INFO").getBeanByPath("Operation.OpResult.NoticeInfos");
            if (noticeInfoList != null) {
                int count = noticeInfoList.getListNum("NoticeInfos.NoticeInfo");
                for (int i = 0; i < count; i++) {
                    XmlBean noticeInfo = noticeInfoList.getBeanByPath("NoticeInfos.NoticeInfo[${i}]");
                    String[] name = new String[2];
                    name[0] = noticeInfo.getStrValue("NoticeInfo.createStaffId");
                    name[1] = noticeInfo.getStrValue("NoticeInfo.toStaffId");
                    String noticeId = noticeInfo.getStrValue("NoticeInfo.noticeId");
                    if (StringUtil.isEqual(id, noticeId)) {
                        for (int j = 0; j < 2; j++) {
                            svcRequest = RequestUtil.getSvcRequest(request);
                            // 数据
                            XmlBean reqData = new XmlBean();
                            String nodePath = "SvcCont.Staff.";
                            reqData.setValue(nodePath + "staffId", name[j]);
                            // 请求信息
                            svcRequest.setReqData(reqData);
                            // 调用服务
                            svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);

                            //配置角色数据展现
                            if (svcResponse.isSuccess()) {
                                XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
                                name[j] = staffBean.getStrValue("Staff.StaffName");
                            }
                        }
                        noticeInfo.setStrValue("NoticeInfo.createStaffName", name[0]);
                        noticeInfo.setStrValue("NoticeInfo.toStaffName", name[1]);
                        modelMap.put("noticeInfo", noticeInfo.getRootNode());
                    }
                }
            }
        }
        return new ModelAndView("/oframe/sysmg/sys014/noticeInfo", modelMap);
    }


    /**
     * 初始化保存消息界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void saveNotice(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String noticeContent = request.getParameter("noticeContent");
        String noticeNote = request.getParameter("noticeNote");
        String toStaffId = request.getParameter("toStaffId");

        String[] toStaffs = null;
        String[] toOrgs = null;
        if (StringUtil.isNotEmptyOrNull(toStaffId)) {
            toStaffs = toStaffId.split(",");
        }
        String prjOrgId = request.getParameter("toOrgId");
        if (StringUtil.isNotEmptyOrNull(prjOrgId)) {
            toOrgs = prjOrgId.split(",");
        }


        XmlBean OpData = new XmlBean();
        OpData.setStrValue("OpData.NoticeInfo.noticeId", "\${noticeId}");
        OpData.setStrValue("OpData.NoticeInfo.noticeContent", noticeContent);
        OpData.setStrValue("OpData.NoticeInfo.noticeNote", noticeNote);
        if (toStaffs != null) {
            for (int i = 0; i < toStaffs.length; i++) {
                OpData.setStrValue("OpData.NoticeInfo.Tostaffs.staffId[${i}]", toStaffs[i]);
            }
        }
        if (toOrgs != null) {
            for (int i = 0; i < toOrgs.length; i++) {
                OpData.setStrValue("OpData.NoticeInfo.ToOrgs.orgId[${i}]", toOrgs[i]);
            }
        }
        svcRequest.addOp("SAVE_NOTICE_INFO_CMPT", OpData);
        SvcResponse svcResponse = transaction(svcRequest);
        //调用发送app消息服务
        boolean isSend=PropertiesUtil.readBoolean("oframe-service", "jiguang.isSend");
        if(isSend){
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.staffId", toStaffId);
            opData.setStrValue("OpData.content", noticeContent);
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("SEND_MOB_NOTICE_CMPT", opData);
            transaction(svcRequest);
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int createStaffId = svcRequest.getReqStaffIdInt();
        String createStaffName;
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Staff.";
        reqData.setValue(nodePath + "staffId", createStaffId);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
            createStaffName = staffBean.getStrValue("Staff.StaffName");
        }
        modelMap.put("currentStaffId", createStaffId);
        modelMap.put("currentStaffName", createStaffName);
        return new ModelAndView("/oframe/sysmg/sys014/sys014", modelMap);

    }

    /**
     * 标记为已读
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView biaoNotice(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean OpData = new XmlBean();
        String noticeId = request.getParameter("noticeId")
        noticeId = noticeId.substring(0, noticeId.length() - 1);
        OpData.setStrValue("OpData.NoticeInfo.noticeId", noticeId);
        svcRequest.addOp("SAVE_NOTICE_INFO", OpData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 独立对话框显示 staff 树
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initStaffTree(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("objName", request.getParameter("objName"));
        modelMap.put("objValue", request.getParameter("objValue"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        return new ModelAndView("/oframe/sysmg/sys004/staffTree", modelMap);
    }
}
