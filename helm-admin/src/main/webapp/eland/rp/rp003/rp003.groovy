import com.shfb.oframe.core.web.controller.GroovyController
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/5/13 10:35
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
/**
 * 院落统计明细报表模块
 */
class rp003 extends GroovyController {

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        return new ModelAndView("/eland/rp/rp003/rp003", modelMap);
    }
}