import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 项目指标查看
 */
class mb005 extends GroovyController {
    String[] colorArr = ["#58a7f7", "#ff0000", "#84ff00", "#ffe400", "#0090ff", "#ef0eef", "#1dd326", "#0042ff", "#ffea00",
                         "#ff0000", "#0096ff", "#30ff00", "#a40ff9", "#2cc6d8", "#9e960e", "#ff7575", "#2cd8ba", "#00e4ff",
                         "#ffaa4e", "#3c9615", "#fc69fa", "#9473ff", "#2cd8c2", "#d8ff00", "#ff009c", "#d88d2c", "#0e8a8a",
                         "#fcb1b1", "#67b414", "#00d8ff", "#81f3bf", "#f4bdf4", "#1dd326", "#db804a", "#a384c3", "#b73400",
                         "#2328e7", "#5ce8f8", "#fffaa3", "#9d6ef3", "#38fe3c", "#31b9c9", "#9f4ae7", "#989b48", "#da2bd8",
                         "#2d9dc6", "#90f1a0", "#ff9e6f", "#ffa4dc", "#9fe0f8", "#0a8307", "#ff9000", "#e941a8"];
    /**
     * 初始化项目公告面板
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) { // 返回结果
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        int prjCd = NumberUtil.getIntFromObj(request.getParameter("prjCd"));
        //取地图颜色作为指标颜色
        Map<String, String> colorMap = getCfgCollection(request, "MAP_COLORS", true, prjCd);
        String colorStr = "";
        if (colorMap.size() > 0) {
            for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                colorStr = entry.getValue();
                if (colorStr != null) {
                    break;
                }
            }
            colorStr = "\"" + colorStr.toString().replace(" ", "").replace(",", "\",\"") + "\"";
        } else {//没有地图颜色，则手动设置颜色
            colorStr = "\"" + colorArr.toString().replace(" ", "").replace("[", "").replace("]", "").replace(",", "\",\"") + "\"";
        }
        modelMap.put("colorStr", colorStr);
        modelMap.put("prjJobCd", requestUtil.getStrParam("prjJobCd"));
        modelMap.put("prjJobGroup", requestUtil.getStrParam("prjJobGroup"));
        modelMap.put("chartType", requestUtil.getStrParam("chartType"));
        modelMap.put("initParams", JSONObject.fromObject(requestUtil.getRequestMap(request)).toString());
        if (StringUtil.isNotEmptyOrNull(request.getParameter("title"))) {
            modelMap.put("title", java.net.URLDecoder.decode(request.getParameter("title"), "utf-8"));
        }
        return new ModelAndView("/eland/mb/mb005/mb005", modelMap);
    }
}
