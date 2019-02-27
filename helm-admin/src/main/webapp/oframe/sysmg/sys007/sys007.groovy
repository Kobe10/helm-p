import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys007 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys007/sys007", modelMap)
    }

    /**
     * 角色树获取
     * @param request
     * @param response
     */
    public void treeInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Role.roleCd", "");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("roleService", "queryTree", svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont.Role");
            int count = treeBean.getListNum("Role.Node") - 1;
            for (int i = 0; i < count; i++) {
                String id = treeBean.getStrValue("""Role.Node[${i}].roleCd""");
                String pId = treeBean.getStrValue("""Role.Node[${i}].upNodeId""");
                String name = treeBean.getStrValue("""Role.Node[${i}].roleName""");
                sb.append("""{ id: "${id}", pId: "${pId}", name: "${name}",open: "true"},""");
            }
            // 拼接最后一个节点
            String id = treeBean.getStrValue("""Role.Node[${count}].roleCd""");
            String pId = treeBean.getStrValue("""Role.Node[${count}].upNodeId""");
            String name = treeBean.getStrValue("""Role.Node[${count}].roleName""");
            sb.append("""{ id: "${id}", pId: "${pId}", name: "${name}",open: "true"}""");

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
    public ModelAndView treeNode(HttpServletRequest request, HttpServletResponse response) {
        // 请求参数
        String method = request.getParameter("method");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String nodeId = request.getParameter("cNodeId");
        svcRequest.setValue("Request.SvcCont.Role.Node.roleCd", nodeId);
        SvcResponse svcResponse = callService("roleService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            if (rspData != null) {
                XmlBean roleNode = rspData.getBeanByPath("SvcCont.Role.Node");
                int roleCount = roleNode.getListNum("Node.Staffs.Staff");
                List<XmlNode> staffList = new ArrayList<XmlNode>(roleCount);
                for (int i = 0; i < roleCount; i++) {
                    staffList.add(roleNode.getBeanByPath("Node.Staffs.Staff[" + i + "]").getRootNode());
                }
                modelMap.put("staffList", staffList);
                modelMap.put("nodeInfo", roleNode.getRootNode());
            }
            modelMap.put("buttonName", "保存");
            modelMap.put("method", method);
            modelMap.put("DATA_RHT_DEF", getCfgCollection(request, "DATA_RHT_DEF", true));
            return new ModelAndView("/oframe/sysmg/sys007/roleInfo", modelMap);
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
        String upTreeId = request.getParameter("pNodeId");
        svcRequest.setValue("Request.SvcCont.Role.Node.roleCd", upTreeId);
        SvcResponse svcResponse = callService("roleService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.Role.Node");
            XmlBean xmlBean = new XmlBean();
            xmlBean.setStrValue("Node.upNodeId", nodeInfoBean.getStrValue("Node.roleCd"));
            xmlBean.setStrValue("Node.upNodeName", nodeInfoBean.getStrValue("Node.roleName"));
            modelMap.put("buttonName", "保存");
            modelMap.put("method", method)
            modelMap.put("nodeInfo", xmlBean.getRootNode());
            return new ModelAndView("/oframe/sysmg/sys007/roleInfo", modelMap)
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
        String method = request.getParameter("method");

        XmlBean reqData = new XmlBean();
        String nodePrePath = "SvcCont.Role.Node.";
        reqData.setStrValue(nodePrePath + "roleCd", request.getParameter("roleCd"));
        reqData.setValue(nodePrePath + "roleKey", request.getParameter("roleKey"));
        reqData.setStrValue(nodePrePath + "roleName", request.getParameter("roleName"));
        reqData.setStrValue(nodePrePath + "upNodeId", request.getParameter("pNodeId"));
        reqData.setStrValue(nodePrePath + "statusCd", request.getParameter("statusCd"));
        svcRequest.setReqData(reqData);
        String methodName = null;
        if ("add".equals(method)) {
            methodName = "addTreeNode";
        } else {
            methodName = "updateTreeNode";
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
    public void deleteNode(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String roleCd = request.getParameter("nodeId");

        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Role.Node.roleCd", roleCd);
        svcRequest.setReqData(reqData);

        SvcResponse svcResponse = callService("roleService", "deleteTreeNode", svcRequest);

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

        reqData.setStrValue("SvcCont.moveTreeId", request.getParameter("mNodeId"));
        reqData.setStrValue("SvcCont.moveToTreeId", request.getParameter("tNodeId"));
        reqData.setStrValue("SvcCont.moveType", moveType);
        svcRequest.setReqData(reqData);

        SvcResponse svcResponse = callService("roleService", "moveTreeNode", svcRequest);

        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success: ${result}, errMsg:"${errMsg}", resultMap:{}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initRht(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys007/rhtTree", modelMap)
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void rhtInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        String roleCd = request.getParameter("roleCd");
        String rhtUseType = request.getParameter("rhtUseType");
        String grantType = request.getParameter("grantType");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtUseType", rhtUseType);
        SvcResponse svcResponse = callService("rhtTreeService", "queryTree", svcRequest);
        // 获取角色权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Role.roleCd", roleCd);
        svcRequest.setValue("Request.SvcCont.Role.grantType", grantType);
        SvcResponse roleResponse = callService("roleService", "queryRoleRht", svcRequest);
        boolean result = svcResponse.isSuccess() && roleResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + roleResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean roleRhtData = roleResponse.getRspData();
            int haveRhtCount = 0;
            if (roleRhtData != null) {
                haveRhtCount = roleRhtData.getListNum("SvcCont.Role.Rights.Right")
            };
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                roleMapList.add(roleRhtData.getStrValue(nodePath + "rhtId"));
            }

            XmlBean rhtTree = svcResponse.getBeanByPath("Response.SvcCont.RhtTree");
            if (rhtTree != null) {
                int count = rhtTree.getListNum("RhtTree.Node");
                for (int i = 0; i < count; i++) {
                    String id = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtId""");
                    String pId = rhtTree.getStrValue("""RhtTree.Node[${i}].uRhtId""");
                    String name = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtName""");
                    String rhtSubType = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtSubType""");
                    String nodeRhtUseType = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtUseType""");
                    String iconSkin = "n_folder";
                    if ("2".equals(rhtSubType)) {
                        iconSkin = "func";
                    } else if ("3".equals(rhtSubType)) {
                        iconSkin = "opr";
                    }
                    // 是否显示选择框
                    String noCheck = "false";
                    if (!StringUtil.isEqual(nodeRhtUseType, rhtUseType) && !"-".equals(nodeRhtUseType)) {
                        noCheck = "true"
                    }
                    boolean haveRht = roleMapList.contains(id);
                    sb.append("""{ id: "${id}", rhtId:"${id}", pId: "${pId}", checked: "${haveRht}",prjCd:"0",
                             "nocheck":${noCheck}, name: "${name}",open: "true",iconSkin:"${iconSkin}"},""");
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
    public void roleSData(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        String roleCd = request.getParameter("roleCd");
        String rhtUseType = request.getParameter("rhtUseType");
        String grantType = request.getParameter("grantType");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtUseType", rhtUseType);
        SvcResponse svcResponse = callService("orgService", "queryTree", svcRequest);
        // 获取角色权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Role.roleCd", roleCd);
        svcRequest.setValue("Request.SvcCont.Role.grantType", grantType);
        SvcResponse roleResponse = callService("roleService", "queryRoleRht", svcRequest);
        boolean result = svcResponse.isSuccess() && roleResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + roleResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean roleRhtData = roleResponse.getRspData();
            int haveRhtCount = 0;
            if (roleRhtData != null) {
                haveRhtCount = roleRhtData.getListNum("SvcCont.Role.Rights.Right")
            };
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                roleMapList.add(roleRhtData.getStrValue(nodePath + "rhtId"));
            }

            XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
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
                } else if (countPath == 2) {
                    iconSkin = "cmp";
                }
                boolean haveRht = roleMapList.contains(id);
                sb.append("""{ id: "${id}", rhtId:"${id}", pId: "${pId}", checked: "${haveRht}",prjCd:"0", iconSkin: "${
                    iconSkin
                }", name: "${name}",open: "${
                    isOpen
                }"},""");
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 项目数据权限
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void prjFunc(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String roleCd = request.getParameter("roleCd");
        String grantType = request.getParameter("grantType");

        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.prjCd", "0");
        reqData.setStrValue("SvcCont.Org.orgId", "");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryOrgTreeForStaffPrj", svcRequest);

        // 获取角色权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Role.roleCd", roleCd);
        svcRequest.setValue("Request.SvcCont.Role.grantType", grantType);
        SvcResponse roleResponse = callService("roleService", "queryRoleRht", svcRequest);

        boolean result = svcResponse.isSuccess() && roleResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + roleResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean roleRhtData = roleResponse.getRspData();
            int haveRhtCount = 0;
            if (roleRhtData != null) {
                haveRhtCount = roleRhtData.getListNum("SvcCont.Role.Rights.Right")
            };
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                String tempPrjCd = roleRhtData.getStrValue(nodePath + "prjCd");
                String tempRhtId = roleRhtData.getStrValue(nodePath + "rhtId");
                roleMapList.add(tempPrjCd + "-" + tempRhtId);
            }

            // 获取公司组织树
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
                    if (countPath == 1) {
                        iconSkin = "home";
                    } else if (countPath == 2) {
                        iconSkin = "cmp";
                    }
                    sb.append("""{ id: "${id}", "nocheck": true, iconSkin: "${iconSkin}",
                        pId: "${pId}", name: "${name}",open: "true"},""");
                }
            }

            /* 调用服务获取工号可管理的项目信息 */
            /* 调用服务获取工号可管理的项目信息 */
            svcRequest = RequestUtil.getSvcRequest(request);
            String prePath = "SvcCont.staffCode";
            reqData = new XmlBean();
            // 查询条件
            reqData.setStrValue(prePath, svcRequest.getReqStaffCd());
            reqData.setStrValue("SvcCont.orgId", request.getParameter("orgId"));
            // 调用服务查询实体全部属性
            svcRequest.setReqData(reqData);
            svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            Map<String, String> prjMap = new LinkedHashMap<String, String>();
            if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        prjMap.put(xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"),
                                xmlBean.getBeanByPath("CmpPrjs.CmpPrj[${i}]"))
                    }
                }
            }

            // 处理项目组织
            for (Map.Entry<String, XmlBean> tempPrjBean : prjMap.entrySet()) {
                String iterPrjCd = tempPrjBean.getKey();
                String iterRoleCd = tempPrjBean.getValue().getStrValue("CmpPrj.funcRole");
                svcRequest = RequestUtil.getSvcRequest(request);
                reqData = new XmlBean();
                reqData.setStrValue("SvcCont.Role.prjCd", iterPrjCd);
                reqData.setStrValue("SvcCont.Role.roleCd", iterRoleCd);
                reqData.setStrValue("SvcCont.Role.grantType", grantType);
                svcRequest.setReqData(reqData);
                svcResponse = callService("roleService", "queryRoleRhtTree", svcRequest);
                if (!svcResponse.isSuccess()) {
                    result = svcResponse.isSuccess();
                    errMsg = svcResponse.getErrMsg();
                    break;
                }
                // 获取公司组织树
                XmlBean tempNodeBean = svcResponse.getBeanByPath("Response.SvcCont.RhtTree");
                if (tempNodeBean == null) {
                    continue;
                }
                int tempCount = tempNodeBean.getListNum("RhtTree.Node");
                for (int i = 0; i < tempCount; i++) {
                    XmlBean tempBean = tempNodeBean.getBeanByPath("RhtTree.Node[${i}]");
                    String id = tempBean.getStrValue("Node.rhtId");
                    String pId = tempBean.getStrValue("Node.uRhtId");
                    String name = tempBean.getStrValue("Node.rhtName");
                    String rhtSubType = tempBean.getStrValue("""Node.rhtSubType""");
                    String iconSkin = "n_folder";
                    if ("2".equals(rhtSubType)) {
                        iconSkin = "func";
                    } else if ("3".equals(rhtSubType)) {
                        iconSkin = "opr";
                    }
                    if (StringUtil.isEmptyOrNull(pId) || "0".equals(pId)) {
                        pId = tempPrjBean.getValue().getStrValue("CmpPrj.ownOrg");
                        name = tempPrjBean.getValue().getStrValue("CmpPrj.prjName");
                    } else {
                        pId = iterPrjCd + "-" + pId;
                    }
                    String idKey = iterPrjCd + "-" + id;
                    boolean haveRht = roleMapList.contains(idKey);
                    sb.append("""{ id: "${iterPrjCd}-${id}", rhtId:"${id}", iconSkin: "folder", prjCd: "${iterPrjCd}",
                      iconSkin:"${iconSkin}", checked: ${haveRht}, pId: "${pId}",name: "${name}"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 项目数据权限
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void dataRht(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String roleCd = request.getParameter("roleCd");
        String grantType = request.getParameter("grantType");

        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.prjCd", "0");
        reqData.setStrValue("SvcCont.Org.orgId", "");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryOrgTreeForStaffPrj", svcRequest);
        // 获取角色权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Role.roleCd", roleCd);
        svcRequest.setValue("Request.SvcCont.Role.grantType", grantType);
        SvcResponse roleResponse = callService("roleService", "queryRoleRht", svcRequest);

        boolean result = svcResponse.isSuccess() && roleResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + roleResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean roleRhtData = roleResponse.getRspData();
            int haveRhtCount = 0;
            if (roleRhtData != null) {
                haveRhtCount = roleRhtData.getListNum("SvcCont.Role.Rights.Right")
            };
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                String tempRhtId = roleRhtData.getStrValue(nodePath + "rhtId");
                roleMapList.add(tempRhtId);
            }

            // 获取公司组织树
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
                    if (countPath == 1) {
                        iconSkin = "home";
                    } else if (countPath == 2) {
                        iconSkin = "cmp";
                    }
                    sb.append("""{ id: "${id}", "nocheck": true, iconSkin: "${iconSkin}",
                        pId: "${pId}", name: "${name}",open: "true"},""");
                }
            }

            /* 调用服务获取工号可管理的项目信息 */
            svcRequest = RequestUtil.getSvcRequest(request);
            String prePath = "SvcCont.staffCode";
            reqData = new XmlBean();
            // 查询条件
            reqData.setStrValue(prePath, svcRequest.getReqStaffCd());
            reqData.setStrValue("SvcCont.orgId", request.getParameter("orgId"));
            // 调用服务查询实体全部属性
            svcRequest.setReqData(reqData);
            svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            Map<String, String> prjMap = new LinkedHashMap<String, String>();
            if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        prjMap.put(xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"),
                                xmlBean.getBeanByPath("CmpPrjs.CmpPrj[${i}]"))
                    }
                }
            }

            // 处理项目组织
            for (String tempPrjCd : prjMap.keySet()) {
                svcRequest = RequestUtil.getSvcRequest(request);
                reqData = new XmlBean();
                reqData.setStrValue("SvcCont.Org.prjCd", tempPrjCd);
                reqData.setStrValue("SvcCont.Org.orgId", "");
                svcRequest.setReqData(reqData);
                svcResponse = callService("orgService", "queryTree", svcRequest);
                if (!svcResponse.isSuccess()) {
                    result = svcResponse.isSuccess();
                    errMsg = svcResponse.getErrMsg();
                    break;
                }
                // 获取公司组织树
                XmlBean tempNodeBean = svcResponse.getBeanByPath("Response.SvcCont");
                int tempCount = tempNodeBean.getListNum("SvcCont.TreeNode");
                for (int i = 0; i < tempCount; i++) {
                    String id = tempNodeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                    String pId = tempNodeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                    String name = tempNodeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                    if (StringUtil.isEmptyOrNull(pId)) {
                        pId = prjMap.get(tempPrjCd).getStrValue("CmpPrj.ownOrg");
                        name = prjMap.get(tempPrjCd).getStrValue("CmpPrj.prjName");
                    }
                    boolean haveRht = roleMapList.contains(id);
                    sb.append("""{ id: "${id}", rhtId: "${id}", checked: ${haveRht}, prjCd: "${tempPrjCd}",
                                iconSkin: "folder",pId: "${pId}",name: "${name}"},""");
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
    public void saveRht(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String rhtType = requestUtil.getStrParam("rhtType");
        String grantType = requestUtil.getStrParam("grantType");
        String roleCd = requestUtil.getStrParam("roleCd");
        String newRhtIds = requestUtil.getStrParam("newRhtIds");
        String prjCds = requestUtil.getStrParam("prjCds");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.Role.roleCd", roleCd);
        reqData.setValue("SvcCont.Role.grantType", grantType);
        String[] rhtIdArr = newRhtIds.split(",");
        String[] prjCdsArr = prjCds.split(",", rhtIdArr.length);
        if (rhtIdArr != null && !StringUtil.isEmptyOrNull(newRhtIds)) {
            for (int i = 0; i < rhtIdArr.length; i++) {
                String tempRhtId = rhtIdArr[i];
                String tempPrjCd = prjCdsArr[i];
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                reqData.setStrValue(nodePath + "rhtId", tempRhtId);
                reqData.setStrValue(nodePath + "prjCd", tempPrjCd);
            }
        }
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("roleService", "updateRoleRht", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }


}
