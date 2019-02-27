import com.alibaba.fastjson.JSONArray
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys004 extends GroovyController {

    /** 初始化主界面 */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys004/sys004", modelMap)
    }

    public void treeInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.prjCd", request.getParameter("prjCd"));
        reqData.setStrValue("SvcCont.Org.orgId", "");
        reqData.setStrValue("SvcCont.Org.grantType", request.getParameter("grantType"));
        reqData.setStrValue("SvcCont.fullTree", request.getParameter("fullTree"));
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryTree", svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
            if (treeBean != null) {
                int count = treeBean.getListNum("SvcCont.TreeNode");
                for (int i = 0; i < count; i++) {
                    String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                    String pId = treeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                    String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                    String nodePath = treeBean.getStrValue("""SvcCont.TreeNode[${i}].nodePath""");
                    String iconSkin = "folder";
                    int countPath = nodePath.count("/");
                    boolean isOpen = false;
                    if (countPath == 1) {
                        iconSkin = "home";
                        isOpen = true;
                    } else if (countPath == 2 && "0".equals(request.getParameter("prjCd"))) {
                        iconSkin = "cmp";
                    }
                    sb.append("""{ id: "${id}", iconSkin: "${iconSkin}", pId: "${pId}", name: "${name}",open: "${
                        isOpen
                    }"},""");
                }
            }
        }
        String jsonStr = """{success: ${result}, errMsg: "${errMsg}", resultMap: {treeJson: [${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 组织角色组合树
     * @param request
     * @param response
     */
    public void orgRoleTreeInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryOrgRoleTree", svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
            if (treeBean != null) {
                int count = treeBean.getListNum("SvcCont.TreeNode");
                for (int i = 0; i < count; i++) {
                    String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].id""");
                    String pId = treeBean.getStrValue("""SvcCont.TreeNode[${i}].pId""");
                    String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].name""");
                    String nodePath = treeBean.getStrValue("""SvcCont.TreeNode[${i}].nodePath""");
                    String type = treeBean.getStrValue("""SvcCont.TreeNode[${i}].type""");
                    String iconSkin = "folder";
                    int countPath = nodePath.count("/");
                    boolean isOpen = false;
                    boolean noCheck = true;
                    if ("org".equals(type)) {
                        if (countPath == 1) {
                            iconSkin = "home";
                            isOpen = true;
                        } else if (countPath == 2 && "0".equals(request.getParameter("prjCd"))) {
                            iconSkin = "cmp";
                        }
                    } else if ("prj".equals(type)) {
                        iconSkin = "prj";
                    } else {
                        noCheck = false;
                        iconSkin = "role";
                    }

                    sb.append("""{ id: "${id}", iconSkin: "${iconSkin}", type:"${type}", pId: "${pId}", name: "${
                        name
                    }", "nocheck": ${noCheck},open: "${
                        isOpen
                    }"},""");
                }
            }
        }
        String jsonStr = """{success: ${result}, errMsg: "${errMsg}", resultMap: {treeJson: [${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

/** 独立对话框显示项目树 */
    public ModelAndView initTree(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("objName", request.getParameter("objName"));
        modelMap.put("objValue", request.getParameter("objValue"));
        modelMap.put("orgTreeFlag", request.getParameter("orgTreeFlag"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        modelMap.put("orgPrjCd", request.getParameter("orgPrjCd"));
        return new ModelAndView("/oframe/sysmg/sys004/orgTree", modelMap);
    }

    /** 独立对话框显示 staff 树  */
    public ModelAndView initStaffTree(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("objName", request.getParameter("objName"));
        modelMap.put("objValue", request.getParameter("objValue"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        String staffPrjCd = "0";
        if (request.getParameter("staffPrjCd") != null) {
            staffPrjCd = request.getParameter("staffPrjCd");
        }
        modelMap.put("staffPrjCd", staffPrjCd);
        return new ModelAndView("/oframe/sysmg/sys004/staffTree", modelMap);
    }

    /** 独立对话框显示 role 树  */
    public ModelAndView initRole(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("objName", request.getParameter("objName"));
        modelMap.put("objValue", request.getParameter("objValue"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        String staffPrjCd = "0";
        if (request.getParameter("staffPrjCd") != null) {
            staffPrjCd = request.getParameter("staffPrjCd");
        }
        modelMap.put("staffPrjCd", staffPrjCd);
        return new ModelAndView("/oframe/sysmg/sys004/roleTree", modelMap);
    }

    /** 打开组织基本信息，修改组织名称 */
    public ModelAndView initBaseInfo(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String nodeId = request.getParameter("cNodeId");
        String prjCd = request.getParameter("prjCd");
        svcRequest.setValue("Request.SvcCont.Org.Node.orgId", nodeId);
        svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", prjCd);
        SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org");
            modelMap.put("nodeInfo", nodeInfoBean.getBeanByPath("Org.Node").getRootNode());
            return new ModelAndView("/oframe/sysmg/sys004/sys004_base", modelMap)
        }
    }

    /** 打开组织基本信息，用于向组织中增加子组织 */
    public ModelAndView initSubInfo(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String nodeId = request.getParameter("cNodeId");
        String prjCd = request.getParameter("prjCd");
        svcRequest.setValue("Request.SvcCont.Org.Node.orgId", nodeId);
        svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", prjCd);
        SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org");
            modelMap.put("nodeInfo", nodeInfoBean.getBeanByPath("Org.Node").getRootNode());
            return new ModelAndView("/oframe/sysmg/sys004/sys004_sub", modelMap)
        }
    }

    /** 显示组织节点信息 */
    public ModelAndView treeNode(HttpServletRequest request, HttpServletResponse response) {
        // 请求参数
        String method = request.getParameter("method");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String nodeId = request.getParameter("cNodeId");
        String prjCd = request.getParameter("prjCd");
        svcRequest.setValue("Request.SvcCont.Org.Node.orgId", nodeId);
        svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", prjCd);
        SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org");
            modelMap.put("nodeInfo", nodeInfoBean.getBeanByPath("Org.Node").getRootNode());
            // 岗位信息列表
            List<XmlNode> positionList = new ArrayList<XmlNode>();
            int positionCount = nodeInfoBean.getListNum("Org.Positions.Position");
            for (int i = 0; i < positionCount; i++) {
                positionList.add(nodeInfoBean.getBeanByPath("Org.Positions.Position[" + i + "]").getRootNode());
            }
            modelMap.put("positionList", positionList);
            return new ModelAndView("/oframe/sysmg/sys004/orgInfo", modelMap)
        }
    }

    /**
     * 显示组织节点信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView orgRole(HttpServletRequest request, HttpServletResponse response) {
        // 请求参数
        String method = request.getParameter("method");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String orgId = request.getParameter("orgId");

        // 调用服务获取组织角色组织列表
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Role.orgId", orgId);
        svcRequest.setStrValue("Request.SvcCont.Role.prjCd", request.getParameter("prjCd"));
        SvcResponse svcResponse = callService("roleService", "queryRoleByOrg", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            if (rspData != null) {
                int roleCount = rspData.getListNum("SvcCont.Role.Node");
                List<XmlNode> roleList = new ArrayList<XmlNode>(roleCount);
                for (int i = 0; i < roleCount; i++) {
                    roleList.add(rspData.getBeanByPath("SvcCont.Role.Node[" + i + "]").getRootNode());
                }
                modelMap.put("roleList", roleList);
            }
        }
        return new ModelAndView("/oframe/sysmg/sys004/orgRole", modelMap);
    }

    /**
     * 显示角色信息
     * @param request 请求信息
     * @param response 响应信息
     * @return 角色显示视图
     */
    public ModelAndView roleInfo(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String roleCd = request.getParameter("roleCd");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Role.Node.roleCd", roleCd);
        SvcResponse svcResponse = callService("roleService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            if (rspData != null) {
                XmlBean roleNode = svcResponse.getRspData().getBeanByPath("SvcCont.Role.Node");
                int roleCount = roleNode.getListNum("Node.Staffs.Staff");
                List<XmlNode> staffList = new ArrayList<XmlNode>(roleCount);
                for (int i = 0; i < roleCount; i++) {
                    staffList.add(roleNode.getBeanByPath("Node.Staffs.Staff[" + i + "]").getRootNode());
                }
                modelMap.put("staffList", staffList);
                modelMap.put("roleNode", roleNode.getRootNode());
            }
        }
        return new ModelAndView("/oframe/sysmg/sys004/roleInfo", modelMap);
    }

    /**
     * 显示组织岗位信息
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开组织岗位编辑界面
     */
    public ModelAndView initPosInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求参数
        RequestUtil requestUtil = new RequestUtil(request);
        String clickIdx = requestUtil.getStrParam("clickIdx");
        String posId = requestUtil.getStrParam("posId");
        String posName = requestUtil.getStrParam("posName");
        if (!StringUtil.isEmptyOrNull(posName)) {
            posName = java.net.URLDecoder.decode(posName, "utf-8")
        }
        String posType = requestUtil.getStrParam("posType");
        String posDesc = requestUtil.getStrParam("posDesc");
        if (!StringUtil.isEmptyOrNull(posName)) {
            posDesc = java.net.URLDecoder.decode(posDesc, "utf-8")
        }
        // 设置输出参数
        ModelMap modelMap = new ModelMap();
        modelMap.put("clickIdx", clickIdx);
        modelMap.put("posId", posId);
        modelMap.put("posName", posName);
        modelMap.put("posType", posType);
        modelMap.put("posDesc", posDesc);
        // 返回输出结果
        return new ModelAndView("/oframe/sysmg/sys004/posInfo", modelMap);
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
        String upTreeId = request.getParameter("pNodeId");
        svcRequest.setValue("Request.SvcCont.Org.Node.orgId", upTreeId);
        svcRequest.setStrValue("Request.SvcCont.Org.Node.prjCd", request.getParameter("prjCd"));
        SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org.Node");
            XmlBean xmlBean = new XmlBean();
            xmlBean.setStrValue("Node.upNodeId", nodeInfoBean.getStrValue("Node.orgId"));
            xmlBean.setStrValue("Node.upNodeName", nodeInfoBean.getStrValue("Node.orgName"));
            xmlBean.setStrValue("Node.prjCd", nodeInfoBean.getStrValue("Node.prjCd"));
            modelMap.put("method", method)
            modelMap.put("nodeInfo", xmlBean.getRootNode());
            return new ModelAndView("/oframe/sysmg/sys004/orgInfo", modelMap)
        }
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void saveNode(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        XmlBean reqData = new XmlBean();
        String nodePrePath = "SvcCont.Org.Node.";
        String orgId = requestUtil.getStrParam("orgId");
        reqData.setStrValue(nodePrePath + "orgId", orgId);
        reqData.setStrValue(nodePrePath + "prjCd", requestUtil.getStrParam("prjCd"));
        reqData.setStrValue(nodePrePath + "orgName", requestUtil.getStrParam("orgName"));
        reqData.setStrValue(nodePrePath + "upNodeId", requestUtil.getStrParam("pNodeId"));
        reqData.setStrValue(nodePrePath + "statusCd", requestUtil.getStrParam("statusCd"));
        // 工号信息
        String staffIds = requestUtil.getStrParam("staffIds");
        String[] staffIdArr = staffIds.split(",");
        int addCount = 0;
        for (String temp : staffIdArr) {
            if (StringUtil.isEmptyOrNull(temp)) {
                continue;
            }
            reqData.setStrValue(nodePrePath + "Staff.staffId[${addCount++}]", temp);
        }

        // 岗位信息
        svcRequest.setReqData(reqData);
        String methodName = "updateTreeNode";
        String method = "update";
        if (StringUtil.isEmptyOrNull(orgId)) {
            methodName = "addTreeNode";
            method = "add";
        }

        SvcResponse svcResponse = callService("orgService", methodName, svcRequest);
        String jsonStr = "";
        if (svcResponse.isSuccess()) {
            String id = svcResponse.getRspData().getStrValue("SvcCont.Org.Node.orgId");
            String pId = svcResponse.getRspData().getStrValue("SvcCont.Org.Node.upNodeId");
            String name = svcResponse.getRspData().getStrValue("SvcCont.Org.Node.orgName");
            jsonStr = """method: "${method}", nodeInfo:{ id: "${id}", pId: "${pId}", name: "${name}",open: "true"}""";
        }
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 保存多组织节点信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void saveNodes(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String[] orgNameArr = request.getParameterValues("orgName");
        String[] orgStyleArr = request.getParameterValues("orgStyle");
        String[] orgLogoArr = request.getParameterValues("orgLogo");
        String[] orgIdArr = request.getParameterValues("orgId");
        String[] upNodeIdArr = request.getParameterValues("upNodeId");
        if (orgNameArr != null && orgNameArr.length >= 1) {
            XmlBean reqData = new XmlBean();
            reqData.setStrValue("SvcCont.prjCd", request.getParameter("prjCd"));
            String nodePrePath = "SvcCont.OrgNodes.Node";
            int addCount = 0;
            for (int i = 0; i < orgNameArr.length; i++) {
                if (StringUtil.isEmptyOrNull(orgNameArr[i])) {
                    continue;
                }
                reqData.setStrValue(nodePrePath + "[${addCount}]." + "orgId", orgIdArr[i]);
                reqData.setStrValue(nodePrePath + "[${addCount}]." + "orgName", orgNameArr[i]);
                reqData.setStrValue(nodePrePath + "[${addCount}]." + "orgStyle", orgStyleArr[i]);
                reqData.setStrValue(nodePrePath + "[${addCount}]." + "orgLogo", orgLogoArr[i]);
                reqData.setStrValue(nodePrePath + "[${addCount++}]." + "upNodeId", upNodeIdArr[i]);
            }
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("orgService", "saveTreeNodes", svcRequest);
            JSONArray jsonArray = new JSONArray();
            if (svcResponse.isSuccess()) {
                XmlBean rspData = svcResponse.getRspData();
                int resultCount = rspData.getListNum("SvcCont.OrgNodes.Node");
                for (int i = 0; i < resultCount; i++) {
                    String id = rspData.getStrValue("SvcCont.OrgNodes.Node[${i}].orgId");
                    String pId = rspData.getStrValue("SvcCont.OrgNodes.Node[${i}].upNodeId");
                    String name = rspData.getStrValue("SvcCont.OrgNodes.Node[${i}].orgName");
                    // 控制节点显示
                    String nodePath = rspData.getStrValue("""SvcCont.OrgNodes.Node[${i}].nodePath""");
                    String iconSkin = "folder";
                    int countPath = nodePath.count("/");
                    if (countPath == 1) {
                        iconSkin = "home";
                    } else if (countPath == 2 && "0".equals(request.getParameter("prjCd"))) {
                        iconSkin = "cmp";
                    }
                    JSONObject tempObject = new JSONObject();
                    tempObject.put("id", id);
                    tempObject.put("pId", pId);
                    tempObject.put("name", name);
                    tempObject.put("iconSkin", iconSkin);
                    jsonArray.add(tempObject);
                }
            }
            ResponseUtil.printSvcResponse(response, svcResponse, "OrgNodes:" + jsonArray.toString());
        } else {
            ResponseUtil.printAjax(response, """{success:false, errMsg:"保存失败，新增组织为空！"}""");
        }
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteNode(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String orgId = request.getParameter("nodeId");
        String prjCd = request.getParameter("prjCd");

        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.Node.orgId", orgId);
        reqData.setStrValue("SvcCont.Org.Node.prjCd", prjCd);

        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "deleteTreeNode", svcRequest);

        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success: ${result}, errMsg: "${errMsg}", resultMap:{}}""";
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
        String moveType = request.getParameter("moveType");
        if ("prev".equals(moveType)) {
            moveType = "2"
        } else if ("next".equals(moveType)) {
            moveType = "3"
        } else {
            moveType = "1"
        }
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.prjCd", request.getParameter("prjCd"));
        reqData.setStrValue("SvcCont.moveTreeId", request.getParameter("mNodeId"));
        reqData.setStrValue("SvcCont.moveToTreeId", request.getParameter("tNodeId"));
        reqData.setStrValue("SvcCont.moveType", moveType);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "moveTreeNode", svcRequest);

        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success: ${result}, errMsg:"${errMsg}", resultMap:{}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 工号选择目录树
     * @param request 请求信息
     * @param response 响应信息
     */
    public void staffTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String prjCd = request.getParameter("prjCd");
        if (StringUtil.isEmptyOrNull(prjCd)) {
            prjCd = "0";
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.prjCd", request.getParameter("prjCd"));
        reqData.setStrValue("SvcCont.Org.orgId", "");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryTree", svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
        if (result && treeBean != null) {
            int count = treeBean.getListNum("SvcCont.TreeNode");
            String firstOrgId = "";
            for (int i = 0; i < count; i++) {
                String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                if (i == 0) {
                    firstOrgId = id;
                }
                String pId = treeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                String nodePath = treeBean.getStrValue("""SvcCont.TreeNode[${i}].nodePath""");
                String iconSkin = "folder";
                int countPath = nodePath.count("/");
                boolean isOpen = false;
                if (countPath == 1) {
                    iconSkin = "home";
                    isOpen = true;
                } else if (countPath == 2 && "0".equals(request.getParameter("prjCd"))) {
                    iconSkin = "cmp";
                }
                sb.append("""{ id: "${id}", iconSkin: "${iconSkin}", nocheck: "true", pId: "${pId}", name: "${
                    name
                }",open: "${
                    isOpen
                }"},""");
            }

            // 查询组织下的所有人员
            svcRequest = RequestUtil.getSvcRequest(request);
            // 数据
            reqData = new XmlBean();
            String nodePath = "SvcCont.";
            reqData.setStrValue(nodePath + "ParamInfo.Param[0].name", "prjCd");
            reqData.setStrValue(nodePath + "ParamInfo.Param[0].value", prjCd);
            reqData.setStrValue(nodePath + "ParamInfo.Param[1].name", "orgId");
            reqData.setStrValue(nodePath + "ParamInfo.Param[1].value", firstOrgId);
            reqData.setStrValue("SvcCont.PageInfo.currentPage", "1");
            reqData.setStrValue("SvcCont.PageInfo.pageSize", "10000");
            if ("0".equals(prjCd)) {
                reqData.setStrValue("SvcCont.QuerySvc.subSvcName", "sys002");
            } else {
                reqData.setStrValue("SvcCont.QuerySvc.subSvcName", "sys00201");
            }
            svcRequest.setReqData(reqData);
            // 调用服务
            svcResponse = callService("staffService", "queryPageWithRht", svcRequest);
            if (svcResponse.isSuccess()) {
                // 获取工号列表
                XmlBean rspData = svcResponse.getRspData();
                // 返回结果集
                List<Map<String, Object>> staffList = rspData.getValue("SvcCont.PageData");
                for (int i = 0; i < staffList.size(); i++) {
                    Map<String, Object> tempMap = staffList.get(i);
                    sb.append("""{ id: "staff_${tempMap.staff_id}", staffId:"${tempMap.staff_id}",
                                 staffCd:"${tempMap.staff_code}", name: "${tempMap.staff_name}",
                                 iconSkin: "user", pId: "${tempMap.org_id}"},""");
                }
            }
        }
        String jsonStr = """{success: ${result}, errMsg: "${errMsg}", resultMap: {treeJson: [${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void saveRoleInfo(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String roleCd = request.getParameter("roleCd");
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        XmlBean reqData = new XmlBean();
        String nodePrePath = "SvcCont.Role.Node.";
        reqData.setStrValue(nodePrePath + "roleCd", roleCd);
        reqData.setValue(nodePrePath + "prjCd", requestUtil.getStrParam("prjCd"));
        reqData.setValue(nodePrePath + "orgId", requestUtil.getStrParam("orgId"));
        reqData.setStrValue(nodePrePath + "roleName", requestUtil.getStrParam("roleName"));
        reqData.setStrValue(nodePrePath + "upNodeId", requestUtil.getStrParam("upRoleCd"));
        // 工号信息
        String staffIds = requestUtil.getStrParam("staffIds");
        String[] staffIdArr = staffIds.split(",");
        int addCount = 0;
        for (String temp : staffIdArr) {
            if (StringUtil.isEmptyOrNull(temp)) {
                continue;
            }
            reqData.setStrValue(nodePrePath + "Staff.staffId[${addCount++}]", temp);
        }
        // 调用服务保存
        svcRequest.setReqData(reqData);
        String methodName = "updateTreeNode";
        String method = "update";
        if (StringUtil.isEmptyOrNull(roleCd)) {
            methodName = "addTreeNode";
            method = "add";
        }
        SvcResponse svcResponse = callService("roleService", methodName, svcRequest);
        String jsonStr = "";
        if (svcResponse.isSuccess()) {
            String id = svcResponse.getRspData().getStrValue("SvcCont.Role.Node.roleCd");
            String pId = svcResponse.getRspData().getStrValue("SvcCont.Role.Node.upNodeId");
            String name = svcResponse.getRspData().getStrValue("SvcCont.Role.Node.roleName");

            jsonStr = """method: "${method}", nodeInfo:{ id: "${id}", pId: "${pId}", name: "${name}",open: "true"}""";
        }
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteRoleInfo(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String roleCd = request.getParameter("roleCd");
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        XmlBean reqData = new XmlBean();
        String nodePrePath = "SvcCont.Role.Node.";
        reqData.setStrValue(nodePrePath + "roleCd", roleCd);
        // 调用服务保存
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("roleService", "deleteTreeNode", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 打开员工导入
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView openImport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys004/orgInfo_import", modelMap);
    }

    /**
     * 下载导入模板
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public void downTemplate(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        String templateFile = new File(StringUtil.formatFilePath("webroot:/oframe/sysmg/sys004/importTemplate.xls"));
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, "", "员工导入模板.xls", new File(templateFile), agent);
    }

    /**
     * base64转换
     */
    public void base64Image(HttpServletRequest request, HttpServletResponse response) {
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "importPic");
        byte[] b = localFile.getSize() / 1024;
        String base64Image = "";
        String errMsg = "";
        if (b[0] < 20) {
            String originalFileName = localFile.getOriginalFilename();
            String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
            if (fileType.contains(".png") || fileType.contains(".jpg")) {
                base64Image = "data:image/jpg;base64," + StringUtil.ecoderBase64(localFile.getBytes());
            }
        } else {
            errMsg = "图片大小不能超过20KB";
        }
        JSONObject jsonData = new JSONObject();
        jsonData.put("base64Image", base64Image);
        jsonData.put("errMsg", errMsg);

        ResponseUtil.printAjax(response, jsonData.toString());
    }

}
