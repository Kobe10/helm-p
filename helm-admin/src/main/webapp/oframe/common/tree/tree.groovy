import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.web.controller.GroovyController
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys006 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String treeData = request.getParameter("treeData");
        if (StringUtil.isEmptyOrNull(treeData)) {
            treeData = "oframe/sysmg/sys006/sys006-treeInfo.gv?rhtType=1"
        }
        modelMap.put("treeData", treeData);
        modelMap.put("treeCheck", request.getParameter("treeCheck"));
        return new ModelAndView("/oframe/common/tree/tree", modelMap)
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView code(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String itemCd = request.getParameter("itemCd");
        String sltData = request.getParameter("sltData");
        String treeCheck = request.getParameter("treeCheck");
        String treeData = "oframe/sysmg/sys005/sys005-treeData.gv?itemCd=" + itemCd + "&sltData=" + sltData + "&treeCheck=" + treeCheck;
        modelMap.put("treeData", treeData);
        modelMap.put("treeCheck", request.getParameter("treeCheck"));
        return new ModelAndView("/oframe/common/tree/tree", modelMap)
    }

}
