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

class sys008 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys008/sys008", modelMap)
    }

    /**
     * 强制退出
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void forceOut(HttpServletRequest request, HttpServletResponse response) {
        SessionUtil sessionUtil = new SessionUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String loginAccept = request.getParameter("loginAccept");
        SvcResponse svcResponse = null;
        String[] loginAccepts = loginAccept.split(",");
        for (int i = 0; i < loginAccepts.length; i++) {
            svcRequest.setValue("Request.SvcCont.loginAccept", loginAccepts[i]);
            // 强制退出
            svcRequest.setValue("Request.SvcCont.outMode", "0");

            svcResponse = callService("staffService", "saveStaffLogout", svcRequest);
            sessionUtil.removeOtherByKey(loginAccepts[i]);
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
}
