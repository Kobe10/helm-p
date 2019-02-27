import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.properties.PropertiesUtil
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class pj002 extends GroovyController {

    /**
     * 初始化化项目配置主页面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回结果
        ModelMap modelMap = new ModelMap();
        XmlBean prjXmlBean = new XmlBean();
        String prjCd = request.getParameter("prjCd");
        if (StringUtil.isEmptyOrNull(prjCd)) {
            // 设置项目初始值
            String addPrjCd = DateUtil.toStringYmd(DateUtil.getSysDate());
            modelMap.put("addPrjCd", addPrjCd);
        } else {
            // 调用服务获取项目信息
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CmpPrj");
            opData.setStrValue("OpData.groupName", "basicInfo");
            opData.setStrValue("OpData.entityKey", prjCd);
            svcRequest.addOp("QUERY_ENTITY_GROUP", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_GROUP");
                if (resultBean != null) {
                    prjXmlBean = resultBean.getBeanByPath("Operation.OpResult.CmpPrj")
                }
            }
        }
        // 返回处理结果
        modelMap.put("cmpPrj", prjXmlBean.getRootNode());
        return new ModelAndView("/eland/pj/pj002/pj002", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void saveBaseInfo(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        String serviceMethod = "updateProject";
        String method = "update";
        if (StringUtil.isEmptyOrNull(requestUtil.getStrParam("prjCd"))) {
            serviceMethod = "addProject";
            method = "add";
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.Project.BaseInfo.prjName", requestUtil.getStrParam("prjName"));
        reqData.setValue("SvcCont.Project.BaseInfo.prjDesc", requestUtil.getStrParam("prjDesc"));
        reqData.setValue("SvcCont.Project.BaseInfo.statusCd", requestUtil.getStrParam("statusCd"));
        reqData.setValue("SvcCont.Project.BaseInfo.ownOrg", requestUtil.getStrParam("ownOrg"));
        reqData.setValue("SvcCont.Project.BaseInfo.prjCity", requestUtil.getStrParam("prjCity"));
        reqData.setValue("SvcCont.Project.BaseInfo.prjCityReg", requestUtil.getStrParam("prjCityReg"));
        reqData.setValue("SvcCont.Project.BaseInfo.prjType", requestUtil.getStrParam("prjType"));
        if (StringUtil.isEqual("add", method)) {
            //新增时，项目编号，从前端取值。
            reqData.setValue("SvcCont.Project.BaseInfo.prjCd", requestUtil.getStrParam("addPrjCd"));
            // 征收区域
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[0].regId", "");
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[0].regName", requestUtil.getStrParam("prjName"));
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[0].regUseType", "1");
            // 回迁区域
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[1].regId", "");
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[1].regName", "回迁房源");
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[1].regUseType", "2");
            // 外迁房源
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[2].regId", "");
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[2].regName", "回迁房源");
            reqData.setValue("SvcCont.Project.RegInfos.RegInfo[2].regUseType", "3");
        } else {
            reqData.setValue("SvcCont.Project.BaseInfo.prjCd", requestUtil.getStrParam("prjCd"));
        }
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("cmpPrjService", serviceMethod, svcRequest);
        String prjCd = "";
        if (svcResponse.isSuccess()) {
            if ("add".equals(method)) {
                prjCd = svcResponse.getRspData().getStrValue("SvcCont.Project.BaseInfo.prjCd");
            }
        }
        // 处理成功输出项目编号
        ResponseUtil.printSvcResponse(response, svcResponse, "\"prjCd\": \"${prjCd}\", \"method\": \"${method}\"");
    }

    /**
     * 初始化项目配置组织架构
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initOrg(HttpServletRequest request, HttpServletResponse response) {
        // 返回结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj002/pj002_org", modelMap);
    }

    /**
     * 初始化项目配置组织架构
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView orgStaffInfo(HttpServletRequest request, HttpServletResponse response) {
        // 返回结果
        ModelMap modelMap = new ModelMap();
        // 获取项目编号
        String prjCd = request.getParameter("prjCd");
        // 获取组织编号
        String orgId = request.getParameter("orgId");

        // 服务调用请求
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用服务获取组织下的员工信息
        svcRequest.setValue("Request.SvcCont.Org.Node.orgId", orgId);
        svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", prjCd);
        SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org.Node");
            modelMap.put("nodeInfo", nodeInfoBean.getRootNode());
            // 组织下的工号信息
            int staffCount = nodeInfoBean.getListNum("Node.Staffs.Staff");
            List<XmlNode> staffList = new ArrayList<XmlNode>(staffCount);
            for (int i = 0; i < staffCount; i++) {
                staffList.add(nodeInfoBean.getBeanByPath("Node.Staffs.Staff[" + i + "]").getRootNode());
            }
            modelMap.put("staffList", staffList);
        }
        return new ModelAndView("/eland/pj/pj002/pj002_org_info", modelMap);
    }

    /**
     * 初始化项目角色定义
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initRole(HttpServletRequest request, HttpServletResponse response) {
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

        // 调用服务获取项目信息
        SvcRequest prjRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CmpPrj");
        opData.setStrValue("OpData.groupName", "basicInfo");
        opData.setStrValue("OpData.entityKey", request.getParameter("prjCd"));
        prjRequest.addOp("QUERY_ENTITY_GROUP", opData);
        SvcResponse prjResponse = query(prjRequest);
        if (svcResponse.isSuccess() && prjResponse.isSuccess()) {
            XmlBean resultBean = prjResponse.getFirstOpRsp("QUERY_ENTITY_GROUP");
            String prjRole = null;
            if (resultBean != null) {
                prjRole = resultBean.getStrValue("Operation.OpResult.CmpPrj.funcRole");
            }

            XmlBean rspData = svcResponse.getRspData();
            if (rspData != null) {
                int roleCount = rspData.getListNum("SvcCont.Role.Node");
                List<XmlNode> roleList = new ArrayList<XmlNode>(roleCount);
                for (int i = 0; i < roleCount; i++) {
                    // 项目管理角色不展示
                    XmlBean tempNode = rspData.getBeanByPath("SvcCont.Role.Node[" + i + "]");
                    if (!StringUtil.isEqual(prjRole, tempNode.getStrValue("Node.roleCd"))) {
                        roleList.add(tempNode.getRootNode());
                    }
                }
                modelMap.put("roleList", roleList);
            }
        }
        return new ModelAndView("/eland/pj/pj002/pj002_role", modelMap);
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
        if (StringUtil.isNotEmptyOrNull(roleCd)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.setValue("Request.SvcCont.Role.Node.roleCd", roleCd);
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
                    modelMap.put("roleNode", roleNode.getRootNode());
                }
            }
        }
        return new ModelAndView("/eland/pj/pj002/pj002_role_info", modelMap);
    }

    /**
     * 初始化化项目配置主页面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initStaff(HttpServletRequest request, HttpServletResponse response) {
        // 返回结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj002/pj002_staff", modelMap);
    }

    /**
     * 初始化化项目配置主页面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initFunc(HttpServletRequest request, HttpServletResponse response) {
        // 返回结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj002/pj002_func", modelMap);
    }

    /**
     * 初始化 项目参数 界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initParam(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        XmlBean reqData = new XmlBean();

        String nodePath = "SvcCont.SysCfg.";
        reqData.setValue(nodePath + "prjCd", request.getParameter("prjCd"));
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("sysCfgService", "queryPrjSysCfgData", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean cfgsBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs");
            List valueList = new ArrayList();
            int sysCfgNum = cfgsBean.getListNum("SysCfgs.SysCfg");
            for (int i = 0; i < sysCfgNum; i++) {
                XmlBean cfgBean = cfgsBean.getBeanByPath("SysCfgs.SysCfg[${i}]");
                valueList.add(cfgBean.getRootNode());
            }
            modelMap.put("prjCfgList", valueList);
        }
        return new ModelAndView("/eland/pj/pj002/pj002_param", modelMap);
    }

    /**
     * 查询 项目级参数，以及系统级参数
     * @param request
     * @param response
     * @return 项目参数以及系统级参数 展示页面
     */
    public ModelAndView prjSysCfg(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 数据
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        reqData.setValue(nodePath + "itemCd", request.getParameter("itemCd"));
        reqData.setValue(nodePath + "prjCd", request.getParameter("prjCd"));
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        //
        if (svcResponse.isSuccess()) {
            XmlBean cfgBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            modelMap.put("nodeInfo", cfgBean.getRootNode());
            int roleCount = cfgBean.getListNum("SysCfg.Values.Value");
            List<Map<String, String>> valueList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < roleCount; i++) {
                nodePath = "SysCfg.Values.Value[${i}].";
                Map<String, String> item = new HashMap<String, String>();
                item.put("valueCd", cfgBean.getStrValue(nodePath + "valueCd"));
                item.put("valueName", cfgBean.getStrValue(nodePath + "valueName"));
                item.put("notes", cfgBean.getStrValue(nodePath + "notes"));
                valueList.add(item);
            }
            modelMap.put("valueList", valueList);
        }
        return new ModelAndView("/eland/pj/pj002/pj002_param_info", modelMap)
    }

    /**
     * 初始化化项目配置主页面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView prjFuncTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        String roleCd = request.getParameter("roleCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtUseType", request.getParameter("rhtUseType"));
        SvcResponse svcResponse = callService("rhtTreeService", "queryTree", svcRequest);
        // 获取角色权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Role.roleCd", roleCd);
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
                    String iconSkin = "n_folder";
                    if ("2".equals(rhtSubType)) {
                        iconSkin = "func";
                    } else if ("3".equals(rhtSubType)) {
                        iconSkin = "opr";
                    }
                    boolean haveRht = roleMapList.contains(id);
                    sb.append("""{ id: "${id}", pId: "${pId}", checked: "${haveRht}",
                             name: "${name}",open: "true",iconSkin:"${iconSkin}"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化 项目参数 界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initCtrl(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj002/pj002_ctrl", modelMap);
    }
    /**
     * 查询环节控制
     * */
    public ModelAndView queryCtrl(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.SysControlInfo.scType", request.getParameter("scType"));
        opData.setStrValue("OpData.ctOrder", "x");//不为空就行
        svcRequest.addOp("QUERY_CONTROL_INFO", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_CONTROL_INFO").getBeanByPath("Operation.OpResult.SysControlInfo");
            String tempOrder = svcResponse.getFirstOpRsp("QUERY_CONTROL_INFO").getStrValue("Operation.OpResult.ctOrder");
            //下一签约序号
            String ctOrder = Integer.parseInt(tempOrder) + 1;
            modelMap.put("ctOrder", ctOrder);
            if (rspData != null) {
                modelMap.put("ctrlInfo", rspData.getRootNode());
                XmlBean times = new XmlBean(rspData.getBeanByPath("SysControlInfo").toString());
                List ctTimeList = new ArrayList();
                times = times.getBeanByPath("SysControlInfo.CtTimeSlices");
                if (times != null) {
                    int num = times.getListNum("CtTimeSlices.CtTimeSlice");
                    for (int i = 0; i < num; i++) {
                        int temp = times.getIntValue("CtTimeSlices.CtTimeSlice[${i}].ctTimeType");
                        //判断是否是暂停时间，如果是，转换 并推送前端展示
                        if (temp == 1) {
                            long ctTimeSt = times.getLongValue("CtTimeSlices.CtTimeSlice[${i}].ctTimeSt");
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(ctTimeSt);
                            times.setStrValue("CtTimeSlices.CtTimeSlice[${i}].showCtTimeSt", DateUtil.toStringYmdHms(c.getTime()));

                            long ctTimeEnd = times.getLongValue("CtTimeSlices.CtTimeSlice[${i}].ctTimeEnd");
                            c.setTimeInMillis(ctTimeEnd);
                            times.setStrValue("CtTimeSlices.CtTimeSlice[${i}].showCtTimeEnd", DateUtil.toStringYmdHms(c.getTime()));

                            long x = ctTimeEnd - ctTimeSt;
                            times.setValue("CtTimeSlices.CtTimeSlice[${i}].stopLong", (x / 1000));
                        }
                        ctTimeList.add(times.getBeanByPath("CtTimeSlices.CtTimeSlice[${i}]").getRootNode());
                    }
                }
                if (ctTimeList.size() == 0) {
                    ctTimeList.add(new XmlBean("<CtTimeSlice/>").getRootNode());
                }
                modelMap.put("ctTimes", ctTimeList);
            }
        }
        String scType = request.getParameter("scType");
        String toPage = "/eland/pj/pj002/pj002_ctrl_ct";
        if (StringUtil.isEqual(scType, "2")) {
            toPage = "/eland/pj/pj002/pj002_ctrl_ch";
        }
        return new ModelAndView(toPage, modelMap);
    }

    /**
     * 保存签约总控 信息 开关
     **/
    public ModelAndView saveCtrl(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //签约时间区间
        String[] ctTimeSts = request.getParameterValues("ctTimeSt");
        String[] ctTimeEnds = request.getParameterValues("ctTimeEnd");

        //暂停时间区间
        String[] pCtTimeSts = request.getParameterValues("pCtTimeSt");
        String[] pCtTimeEnds = request.getParameterValues("pCtTimeEnd");
        String[] pEndTimeSpaces = request.getParameterValues("pEndTimeSpace");
        String[] pCtTimeTypes = request.getParameterValues("pCtTimeType");

        XmlBean opData = new XmlBean();
        String paraPath = "OpData.SysControlInfo.";
        String scId = request.getParameter("scId");
        String scType = request.getParameter("scType");
        if (StringUtil.isEmptyOrNull(scId)) {
            scId = "\${scId}";
        }
        opData.setStrValue(paraPath + "scId", scId);
        opData.setStrValue(paraPath + "scType", scType);
        opData.setStrValue(paraPath + "startCd", request.getParameter("startCd"));
        opData.setStrValue(paraPath + "spaceTime", request.getParameter("spaceTime"));
        opData.setStrValue(paraPath + "readTime", request.getParameter("readTime"));
        if (StringUtil.isNotEmptyOrNull(request.getParameter("preCtDateSt"))) {
            opData.setStrValue(paraPath + "preCtDateSt", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("preCtDateSt"))));
        } else {
            opData.setStrValue(paraPath + "preCtDateSt", "");
        }
        if (StringUtil.isNotEmptyOrNull(request.getParameter("preCtDateEnd"))) {
            opData.setStrValue(paraPath + "preCtDateEnd", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("preCtDateEnd"))));
        } else {
            opData.setStrValue(paraPath + "preCtDateEnd", "");
        }
        //处理 签约时间区间。保存，伪属性不传会清空
        int num = 0;
        if (ctTimeSts != null) {
            for (int i = 0; i < ctTimeSts.length; i++) {
                String temp = ctTimeSts[i];
                if (StringUtil.isEmptyOrNull(temp)) {
                    continue;
                }
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num}].ctTimeSt", ctTimeSts[i]);
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num}].ctTimeEnd", ctTimeEnds[i]);
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num}].endTimeSpace", "");
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num++}].ctTimeType", "");
            }
        }

        //处理暂停时间区间，传递保存，防止不传会清掉伪属性
        if (pCtTimeSts != null) {
            for (int i = 0; i < pCtTimeSts.length; i++) {
                String temp = pCtTimeSts[i];
                if (StringUtil.isEmptyOrNull(temp)) {
                    continue;
                }
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num}].ctTimeSt", pCtTimeSts[i]);
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num}].ctTimeEnd", pCtTimeEnds[i]);
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num}].endTimeSpace", pEndTimeSpaces[i]);
                opData.setStrValue(paraPath + "CtTimeSlices.CtTimeSlice[${num++}].ctTimeType", pCtTimeTypes[i]);
            }
        }

        svcRequest.addOp("SAVE_SYS_CTR_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            //修改成功之后 清除远端服务缓存
            if (PropertiesUtil.readBoolean("oframe", "rmi_config")) {
                svcRequest = RequestUtil.getSvcRequest(request);
                XmlBean reqData = new XmlBean();
                reqData.setStrValue("OpData.Cache.cacheTypeName", "SYS_CONTROL_CACHE");
                reqData.setStrValue("OpData.Cache.cacheSubKey", "");
                svcRequest.addOp("CACHE_CFG_CMPT", reqData);
                svcResponse = transaction("ctService1", svcRequest);
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """""");
    }

    /**
     * 暂停签约
     * @param request
     * @param response
     * @return
     */
    public ModelAndView pauseCtCtrl(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj002/pj002_ctrl_ct_pause", modelMap);
    }

    /**
     * 暂停签约
     * @param request
     * @param response
     * @return
     */
    public ModelAndView resetCtData(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj002/pj002_ctrl_ct_reset", modelMap);
    }

    /**
     * 重置浅语数据
     * @param request
     * @param response
     * @return
     */
    public void doResetCtData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String resetDataFlag = request.getParameter("resetDataFlag");
        if ("1".equals(resetDataFlag)) {
            resetDataFlag = "0";
        } else {
            resetDataFlag = "1";
        }
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.resetSignInfo", resetDataFlag);
        svcRequest.addOp("CLEAR_SIGN_INFO_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            //修改成功之后 清除远端服务缓存
            if (PropertiesUtil.readBoolean("oframe", "rmi_config")) {
                svcRequest = RequestUtil.getSvcRequest(request);
                opData = new XmlBean();
                opData.setStrValue("OpData.resetSignInfo", resetDataFlag);
                svcRequest.addOp("CLEAR_SIGN_INFO_CMPT", opData);
                SvcResponse svcRep = transaction("ctService1", svcRequest);
                svcRep = transaction("ctService2", svcRequest);
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """""");
    }

    /**
     * 暂停签约
     * @param request
     * @param response
     * @return
     */
    public void savePauseCtCtrl(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String pauseTime = request.getParameter("pauseTime");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.pTime", pauseTime);
        svcRequest.addOp("SAVE_CTTIME_PAUSE_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            //修改成功之后 清除远端服务缓存
            if (PropertiesUtil.readBoolean("oframe", "rmi_config")) {
                svcRequest = RequestUtil.getSvcRequest(request);
                XmlBean reqData = new XmlBean();
                reqData.setStrValue("OpData.Cache.cacheTypeName", "SYS_CONTROL_CACHE");
                reqData.setStrValue("OpData.Cache.cacheSubKey", "");
                svcRequest.addOp("CACHE_CFG_CMPT", reqData);
                SvcResponse svcRep = transaction("ctService1", svcRequest);
                svcRep = transaction("ctService2", svcRequest);
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """""");
    }
}
