import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.CacheSession
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class login extends GroovyController {

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("/mobile/mob001/login");
    }

    public ModelAndView check(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入请求信息
        String userName = request.getParameter("systemUserCode");
        String password = request.getParameter("password");
        String ipAddr = RequestUtil.getIpAddr(request);
        String opAccept = RequestUtil.getOpAccept(DateUtil.getSysDate());
        // 调用服务进行登录验证
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffCode", userName);
        svcRequest.setValue("Request.SvcCont.password", password);
        svcRequest.setValue("Request.SvcCont.loginAccept", opAccept);
        svcRequest.setValue("Request.SvcCont.loginIpAddr", ipAddr);
        SvcResponse svcResponse = callService("staffService", "checkStaffLogin", svcRequest);
        int needOutCnt = 0;
        if (svcResponse.isSuccess()) {
            // 获取需要强制退出的登录流水
            needOutCnt = svcResponse.getListNum("Response.SvcCont.Staff.NeedOut");
        }
        // 返回处理结果
        String result = """{"isSuccess": ${svcResponse.isSuccess()}, "loginAccept": "${opAccept}",
                         "errMsg": "${svcResponse.errMsg}", "outNum": ${needOutCnt}}""";
        // 输出
        ResponseUtil.printAjax(response, result);
    }


    public ModelAndView staffLogin(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入请求信息
        String userName = request.getParameter("systemUserCode");
        String password = request.getParameter("password");
        String prjCd = request.getParameter("prjCd");
        String loginAccept = request.getParameter("loginAccept");
        String jumpMainFlag = request.getParameter("jumpMainFlag");
        // 调用服务进行登录验证
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffCode", userName);
        svcRequest.setValue("Request.SvcCont.password", password);
        svcRequest.setValue("Request.SvcCont.loginAccept", loginAccept);
        SvcResponse svcResponse = callService("staffService", "saveStaffLogin", svcRequest);
        //从后台获取流水号
        if (svcResponse.isSuccess()) {
            // 向响应中设置cookie，记录登录流水
            Cookie cookie = new Cookie(CacheSession.SESSION_COOKIE_KEY, StringUtil.obj2Str(loginAccept));
            // 浏览器内存有效
            cookie.setMaxAge(-1);
            // 整个站点有效
            String path = request.getContextPath();
            // 处理WebLogic问题path等于空firefox不识别的问题
            if (StringUtil.isEmptyOrNull(path)) {
                path = "/";
            }
            cookie.setPath(path);
            // 添加到输出
            response.addCookie(cookie);
            // 保存登录信息
            SessionUtil sessionUtil = new SessionUtil(loginAccept, request);
            // 登录工号信息
            String staffId = svcResponse.getStrValue("Response.SvcCont.Staff.StaffId");
            String staffCd = svcResponse.getStrValue("Response.SvcCont.Staff.StaffCd");
            String staffName = svcResponse.getStrValue("Response.SvcCont.Staff.StaffName");
            String orgId = svcResponse.getStrValue("Response.SvcCont.Staff.OrgId");
            // 在Session中保存登录信息
            String loginInfo = """<LoginInfo>
                                    <StaffId>${staffId}</StaffId>
                                    <staffCode>${staffCd}</staffCode>
                                    <staffName>${staffName}</staffName>
                                    <orgId>${orgId}</orgId>
                                    <prjCd>${prjCd}</prjCd>
                               </LoginInfo>""";

            sessionUtil.setAttr(SessionUtil.LOGIN_IN_SESSION_KEY, loginInfo);

            // 获取需要强制退出的登录流水
            int needOutCnt = svcResponse.getListNum("Response.SvcCont.Staff.NeedOut");
            for (int i = 0; i < needOutCnt; i++) {
                String tempLoginAccept = svcResponse.getStrValue("Response.SvcCont.Staff.NeedOut[" + i + "].loginAccept");
                sessionUtil.removeOtherByKey(tempLoginAccept);
            }
        }
        // 是否跳转到主页面
        if ("0".equals(jumpMainFlag)) {
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        } else {
            String from = request.getParameter("from");
            if (StringUtil.isEmptyOrNull(from)) {
                // 输出
                return new ModelAndView("redirect:/mobile/mob002/mob002-init.gv");
            } else {
                return new ModelAndView("redirect:" + from);
            }
        }
    }

    /**
     * 系统退出
     * @param request
     * @param response
     * @return
     */
    public ModelAndView staffLogout(HttpServletRequest request, HttpServletResponse response) {
        SessionUtil sessionUtil = new SessionUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.loginAccept", sessionUtil.getSessionKey());
        callService("staffService", "saveStaffLogout", svcRequest);
        sessionUtil.removeAll();
        return new ModelAndView("redirect:/mobile/mob001/login-init.gv");
    }
}
