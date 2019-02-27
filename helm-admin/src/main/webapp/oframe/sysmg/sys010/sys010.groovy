import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 网站问题解答
 * Created by Administrator on 2014/6/23 0023.
 */
class pj012 extends GroovyController {

    /**
     * 初始化项目流程信息
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SessionUtil sessionUtil = new SessionUtil(request);
        String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
        //用xml格式的String来构建xmlBean对象
        XmlBean xmlBean = new XmlBean(loginInfo);
        //解析出来需要的信息
       String staffId =  xmlBean.getStrValue("LoginInfo.StaffId");
        //从session中获取登陆用户信息，推送到页面
        modelMap.addAttribute("staffId",staffId);
        return new ModelAndView("/oframe/sysmg/sys010/sys010", modelMap);
    }

    public void delFav(HttpServletRequest request, HttpServletResponse response){
        //请求参数封装
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        // 获取输入参数
        String rightId = request.getParameter("rhtId");
        String staffId = request.getParameter("staffId");
        reqData.setValue("SvcCont.Favorite.staffId", staffId);
        reqData.setValue("SvcCont.Favorite.rightId", rightId);

        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "saveDelFavoriteRht", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }



}