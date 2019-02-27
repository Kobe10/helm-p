import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房自定义任务处理流程界面
 */
class ph006 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView doStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String hsId = request.getParameter("busiKey");
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ph/ph006/ph006_do_start", modelMap);
    }

    /**
     * 请求查看处理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView viewStart(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/ph/ph006/ph006_view_start", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView doTask(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String hsId = request.getParameter("busiKey");
        modelMap.put("hsId", hsId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        return new ModelAndView("/eland/ph/ph006/ph006_do_task", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView viewTask(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        return new ModelAndView("/eland/ph/ph006/ph006_view_task", modelMap);
    }

}
