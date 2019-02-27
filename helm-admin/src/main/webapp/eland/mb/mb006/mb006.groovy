import com.shfb.oframe.core.web.controller.GroovyController
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2014/12/17
 * Time: 15:40
 * 测试地图展示
 */
class mb006 extends GroovyController {

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        String strPoint = "POINT(1442.9651162791 713.66279069768),POINT(1442.9651162791 710.17441860466),POINT(1441.3953488372 709.88372093024),POINT(1441.9186046512 707.44186046512),POINT(1440.0581395349 707.55813953489),POINT(1440.523255814 704.53488372094),POINT(1446.8023255814 705.05813953489),POINT(1446.2790697675 713.83720930233),POINT(1442.6744186047 713.48837209303)";
//        String centerPoint = "1439.11421,705.39170";
        String centerPoint = "1308.11421,386.39170";
        String level = "3";

        modelMap.put("linePoint", strPoint);
        modelMap.put("centerPoint", centerPoint);
        modelMap.put("level", level);

        return new ModelAndView("/eland/mb/mb006/mb006", modelMap);
    }

    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String strPoint = "POINT(1274.61421 421.8917),POINT(1346.86421 425.1417),POINT(1347.36421 381.6417),POINT(1275.86421 379.1417)|POINT(1341.11421 413.1417),POINT(1282.86421 386.1417),POINT(1341.11421 388.6417)|POINT(1281.86421 414.3917),POINT(1282.36421 392.3917),POINT(1341.36421 417.8917)";
//        String centerPoint = "1446.11421,717.39170";
        String centerPoint = "1312,403";
        String level = "5";

        modelMap.put("linePoint", strPoint);
        modelMap.put("centerPoint", centerPoint);
        modelMap.put("level", level);

//        return new ModelAndView("/eland/mb/mb006/mb006_view", modelMap);
        return new ModelAndView("/eland/mb/mb006/mb006_view_polygon", modelMap);
    }

}