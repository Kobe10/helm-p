import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys003 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 获取登录信息
        SessionUtil sessionUtil = new SessionUtil(request);
        String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
        XmlBean loginBean = new XmlBean(loginInfo);
        String staffCd = loginBean.getStrValue("LoginInfo.staffCode");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        modelMap.put("staffCd", staffCd);
        return new ModelAndView("/oframe/sysmg/sys003/sys003", modelMap)
    }


    public void chgPwd(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String staffCd = request.getParameter("staffCd");
        if(StringUtil.isEmptyOrNull(staffCd)) {
            SessionUtil sessionUtil = new SessionUtil(request);
            String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
            XmlBean loginBean = new XmlBean(loginInfo);
            staffCd = loginBean.getStrValue("LoginInfo.staffCode");
        }
        String newPassword = request.getParameter("newPassword");
        String oldPassword = request.getParameter("oldPassword");

        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.staffCode", staffCd);
        reqData.setStrValue("SvcCont.oldPassword", oldPassword);
        reqData.setStrValue("SvcCont.newPassword", newPassword);
        //
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("staffService", "changeStaffPwd", svcRequest);
        // 返回前台
        ResponseUtil.printSvcResponse(response, svcResponse, "");

    }



    public void resetPwd(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String staffCd = request.getParameter("staffCd");
        if(StringUtil.isEmptyOrNull(staffCd)) {
            SessionUtil sessionUtil = new SessionUtil(request);
            String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
            XmlBean loginBean = new XmlBean(loginInfo);
            staffCd = loginBean.getStrValue("LoginInfo.staffCode");
        }
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.staffCode", staffCd);
        reqData.setStrValue("SvcCont.newPassword", "111111");
        //
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("staffService", "resetStaffPwd", svcRequest);
        // 返回前台
        ResponseUtil.printSvcResponse(response, svcResponse, "");

    }



}
