import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class query extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 获取权限编码ID,读取配置初始化查询条件
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String privilegeId = request.getParameter("privilegeId");
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.rhtId", privilegeId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
            if (nodeInfoBean != null) {
                // 获取备注描述信息
                String conditions = nodeInfoBean.getStrValue("Node.rhtAttr02");
                XmlBean todoBean = new XmlBean();
                if (StringUtil.isNotEmptyOrNull(conditions)) {
                    // 配置的Json字符串转换为XMLBean
                    todoBean = XmlBean.jsonStr2Xml(conditions, "Condition");
                    //获取json串里的参数；
                }
                modelMap.put("conditions", todoBean.getRootNode());
            }
        }
        //获取系统通用表单编码
        return new ModelAndView("/oframe/common/query/query", modelMap)
    }



}
