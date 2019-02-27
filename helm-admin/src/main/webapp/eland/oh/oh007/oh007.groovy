import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.excel.ExcelReader
import com.shfb.oframe.core.util.exception.SystemException
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 延期交房处理
 */
class oh001 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("regUseType", request.getParameter("regUseType"));
        // 获取显示模式
        modelMap.put("showFyTree", getCookieByName(request, "showFyTree"));
        modelMap.put("showFyTreeModel", getCookieByName(request, "showFyTreeModel"));
        return new ModelAndView("/eland/oh/oh007/oh007", modelMap);
    }

    /**
     * 交房统计界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initS(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取显示模式
        return new ModelAndView("/eland/oh/oh007/oh007_sum", modelMap);
    }

    /**
     * 交房延期处理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initDelay(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("newHsIds", request.getParameter("newHsIds"));
        // 获取显示模式
        return new ModelAndView("/eland/oh/oh007/oh007_delay", modelMap);
    }
    /**
     * 根据名字获取cookie
     * @param request
     * @param name cookie名字
     * @return
     */
    private String getCookieByName(HttpServletRequest request, String name) {
        // 从cookie获取Session主键
        Cookie[] cookieArr = request.getCookies();
        if (cookieArr != null) {
            for (int i = 0; i < cookieArr.length; i++) {
                Cookie cookie = cookieArr[i];
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
