import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.web.controller.GroovyController
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 房源销控展示
 */
class oh005 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String regUseType = request.getParameter("regUseType");
        if (StringUtil.isEmptyOrNull(regUseType)) {
            regUseType = "2";
        }
        modelMap.put("regUseType", regUseType);
        return new ModelAndView("/eland/oh/oh005/oh005", modelMap);
    }

}
