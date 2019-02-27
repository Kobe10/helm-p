import com.shfb.oframe.core.util.common.StringUtil
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
        modelMap.put("rhtType", request.getParameter("rhtType"));
        return new ModelAndView("/oframe/sysmg/sys006/sys006", modelMap)
    }

    /**
     * 获取目录树
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void treeInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        SvcResponse svcResponse = callService("rhtTreeService", "queryTree", svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            XmlBean rhtTree = svcResponse.getBeanByPath("Response.SvcCont.RhtTree");
            if (rhtTree != null) {
                int count = rhtTree.getListNum("RhtTree.Node");
                for (int i = 0; i < count; i++) {
                    String id = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtId""");
                    String pId = rhtTree.getStrValue("""RhtTree.Node[${i}].uRhtId""");
                    String name = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtName""");
                    String rhtSubType = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtSubType""");
                    String iconSkin = "n_folder";
                    if ("2".equals(rhtSubType)) {
                        iconSkin = "func";
                    } else if ("3".equals(rhtSubType)) {
                        iconSkin = "opr";
                    }
                    boolean open = false;
                    if (StringUtil.isEmptyOrNull(pId)) {
                        open = true;
                    }
                    sb.append("""{ id: "${id}", pId: "${pId}", name: "${name}",open: "${open}", iconSkin: "${
                        iconSkin
                    }"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView treeNode(HttpServletRequest request, HttpServletResponse response) {
        // 请求参数
        String method = request.getParameter("method");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String rhtType = request.getParameter("nodeType");
        String rhtId = request.getParameter("cNodeId");
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtId", rhtId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
            modelMap.put("buttonName", "保存");
            modelMap.put("method", method)
            modelMap.put("nodeInfo", nodeInfoBean.getRootNode());
            return new ModelAndView("/oframe/sysmg/sys006/rhtInfo", modelMap)
        }
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initAdd(HttpServletRequest request, HttpServletResponse response) {
        // 请求参数
        String method = request.getParameter("method");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String rhtType = request.getParameter("nodeType");
        String upRhtId = request.getParameter("pNodeId");
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtId", upRhtId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
            XmlBean xmlBean = new XmlBean();
            xmlBean.setStrValue("Node.rhtType", rhtType);
            xmlBean.setStrValue("Node.uRhtId", nodeInfoBean.getStrValue("Node.rhtId"));
            xmlBean.setStrValue("Node.uRhtName", nodeInfoBean.getStrValue("Node.rhtName"));
            modelMap.put("buttonName", "保存");
            modelMap.put("method", method)
            modelMap.put("nodeInfo", xmlBean.getRootNode());
            return new ModelAndView("/oframe/sysmg/sys006/rhtInfo", modelMap)
        }
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void saveNode(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String rhtType = request.getParameter("nodeType");
        String rhtId = request.getParameter("cNodeId");
        String method = request.getParameter("method");
        String rhtCd = request.getParameter("cNodeCd");
        String oldRhtCd = request.getParameter("oldCNodeCd");

        XmlBean reqData = new XmlBean();
        String nodePrePath = "SvcCont.RhtTree.Node.";
        reqData.setStrValue("SvcCont.RhtTree.treeType", rhtType);
        reqData.setStrValue(nodePrePath + "rhtId", rhtId);
        reqData.setStrValue(nodePrePath + "rhtCd", rhtCd);
        reqData.setStrValue(nodePrePath + "rhtUseType", request.getParameter("rhtUseType"));
        reqData.setStrValue(nodePrePath + "rhtSubType", request.getParameter("rhtSubType"));
        reqData.setStrValue(nodePrePath + "rhtName", request.getParameter("cNodeName"));
        reqData.setStrValue(nodePrePath + "uRhtId", request.getParameter("pNodeId"));
        reqData.setStrValue(nodePrePath + "rhtAttr01", request.getParameter("cNodeAttr"));
        reqData.setStrValue(nodePrePath + "rhtAttr02", request.getParameter("cNodeDesc"));
        reqData.setStrValue(nodePrePath + "statusCd", request.getParameter("statusCd"));

        String methodName;
        if ("add".equals(method)) {
            methodName = "addTreeNode";
        } else {
            methodName = "updateTreeNode";
            // 更新时判断是否验证重复。①新旧cd相同，不验证；②新旧cd不同，验证是否已有重复
            if (!StringUtil.isEqual(rhtCd, oldRhtCd)) {
                reqData.setStrValue(nodePrePath + "isCheck", true);
            }
        }
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("rhtTreeService", methodName, svcRequest);

        String jsonStr = "";
        if (svcResponse.isSuccess()) {
            String id = svcResponse.getRspData().getStrValue("SvcCont.RhtTree.Node.rhtId");
            String pId = svcResponse.getRspData().getStrValue("SvcCont.RhtTree.Node.uRhtId");
            String name = svcResponse.getRspData().getStrValue("SvcCont.RhtTree.Node.rhtName");
            jsonStr = """method:"${method}", nodeInfo:{ id: "${id}", pId: "${pId}", name: "${name}",open: "true"}""";
        }
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteNode(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String rhtType = request.getParameter("nodeType");
        String rhtId = request.getParameter("nodeId");

        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.RhtTree.treeType", rhtType);
        reqData.setStrValue("SvcCont.RhtTree.Node.rhtId", rhtId);
        svcRequest.setReqData(reqData);

        SvcResponse svcResponse = callService("rhtTreeService", "deleteTreeNode", svcRequest);

        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void moveNode(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String rhtType = request.getParameter("nodeType");
        String rhtId = request.getParameter("nodeId");
        String moveType = request.getParameter("moveType");
        if ("prev".equals(moveType)) {
            moveType = "2"
        } else if ("next".equals(moveType)) {
            moveType = "3"
        } else {
            moveType = "1"
        }

        XmlBean reqData = new XmlBean();

        reqData.setStrValue("SvcCont.treeType", rhtType);
        reqData.setStrValue("SvcCont.moveRhtId", request.getParameter("mNodeId"));
        reqData.setStrValue("SvcCont.moveToRhtId", request.getParameter("tNodeId"));
        reqData.setStrValue("SvcCont.moveType", moveType);
        svcRequest.setReqData(reqData);

        SvcResponse svcResponse = callService("rhtTreeService", "moveTreeNode", svcRequest);

        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

}
