import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class pj001 extends GroovyController {

    /**
     * 初始化化项目管理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回结果
        ModelMap modelMap = new ModelMap();
        // 返回处理结果
        return new ModelAndView("/eland/pj/pj001/pj001", modelMap);
    }

    /**
     * 项目组织树
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView prjTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont","");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryOrgTreeForStaffPrj", svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
        if (result && treeBean != null) {
            int count = treeBean.getListNum("SvcCont.TreeNode");
            for (int i = 0; i < count; i++) {
                String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                String pId = treeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                String nodePath = treeBean.getStrValue("""SvcCont.TreeNode[${i}].nodePath""");
                String iconSkin = "folder";
                int countPath = nodePath.count("/");
                if (countPath == 1) {
                    iconSkin = "home";
                } else if (countPath == 2) {
                    iconSkin = "cmp";
                }
                sb.append("""{ id: "${id}", iconSkin: "${iconSkin}", pId: "${pId}", name: "${name}",open: "true"},""");
            }
            /* 调用服务获取工号可管理的项目信息 */
            // 获取工号可以访问的项目工程
            svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = new XmlBean();
            String prePath = "SvcCont.staffCode";
            // 查询条件
            opData.setStrValue(prePath, svcRequest.getReqStaffCd());
            opData.setStrValue("SvcCont.isShowExp", request.getParameter("isShowExp"));
            // 调用服务查询实体全部属性
            svcRequest.setReqData(opData);
            svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            Map<String, String> prjMap = new LinkedHashMap<String, String>();
            if (svcResponse.isSuccess()) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        XmlBean cmpPrjBean = xmlBean.getBeanByPath("CmpPrjs.CmpPrj[${i}]");
                        String prjCd = cmpPrjBean.getStrValue("CmpPrj.prjCd");
                        String id = "prjCd-" + prjCd;
                        String pId = cmpPrjBean.getStrValue("CmpPrj.ownOrg");
                        String name = cmpPrjBean.getStrValue("CmpPrj.prjName");
                        sb.append("""{ id: "${id}", prjCd: "${prjCd}", iconSkin: "prj", pId: "${pId}", name: "${
                            name
                        }"},""");
                    }
                }
            }
        }
        String jsonStr = """{success: ${result}, errMsg: "${errMsg}", resultMap: {treeJson: [${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
}
