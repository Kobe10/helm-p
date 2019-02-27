import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.excel.ExcelReader
import com.shfb.oframe.core.util.properties.PropertiesUtil
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ph001 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String opCode = svcRequest.getStrValue("Request.TcpCont.OpCode");
        String inputRht = "";
        modelMap.put("opCode", opCode);
        // 获取权限编码ID,读取配置初始化查询条件
        String privilegeId = request.getParameter("privilegeId");
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.rhtId", privilegeId);
        SvcResponse svcRespon = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcRespon.isSuccess()) {
            XmlBean nodeInfoBean = svcRespon.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
            if (nodeInfoBean != null) {
                // 获取备注描述信息
                String conditions = nodeInfoBean.getStrValue("Node.rhtAttr02");
                XmlBean todoBean = new XmlBean();
                todoBean.setStrValue("Condition.conditionNames", "");
                todoBean.setStrValue("Condition.conditions", "");
                todoBean.setStrValue("Condition.conditionValues", "");
                //添加权限
                todoBean.setStrValue("Condition.rhtType", "");
                // 增加显示高级
                todoBean.setStrValue("Condition.advSch", "true");
                //添加resultField默认要查询的字段
                todoBean.setStrValue("Condition.resultField", "");
                if (StringUtil.isNotEmptyOrNull(conditions)) {
                    // 配置的Json字符串转换为XMLBean
                    todoBean = XmlBean.jsonStr2Xml(conditions, "Condition");
                    //获取json串里的参数；
                }
                inputRht = todoBean.getStrValue("Condition.inputRht");
                modelMap.put("conditions", todoBean.getRootNode());
            }
        }

        // 获取权限编码ID,读取配置初始化查询条件
        if (StringUtil.isNotEmptyOrNull(inputRht)) {
            svcRequest.setValue("Request.SvcCont.treeType", "1");
            svcRequest.setValue("Request.SvcCont.rhtId", inputRht);
            svcRespon = callService("rhtTreeService", "queryNodeInfo", svcRequest);
            if (svcRespon.isSuccess()) {
                XmlBean nodeInfoBean = svcRespon.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
                if (nodeInfoBean != null) {
                    String inputUrl = nodeInfoBean.getStrValue("Node.rhtAttr01");
                    modelMap.put("inputUrl", inputUrl);
                    // 从URL中获取FormCd
                    String formCd = "";
                    def matcher = inputUrl =~ /.*formCd=(.*)&?/
                    if (matcher.matches()) {
                        formCd = matcher.group(1);
                    } else {
                        matcher = inputUrl =~ /.*itemCd=(.*)&?/
                        if (matcher.matches()) {
                            String itemCd = matcher.group(1);
                            Map<String, String> map = getCfgCollection(request, "PRJ_FORM",
                                    true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
                            formCd = map.get(itemCd);
                        }
                    }
                    modelMap.put("inputName", nodeInfoBean.getStrValue("Node.rhtName"))
                    modelMap.put("inputRhtCd", nodeInfoBean.getStrValue("Node.rhtCd"));
                    modelMap.put("inputFormCd", formCd);
                }
            }
        }

        //查询快速检索中条件的中文名， 根据属性英文名称获取到属性的名称
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.prjCd", request.getParameter("prjCd"));
        svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT").getBeanByPath("Operation.OpResult.Entity");
            if (entity != null) {
                int groupCount = entity.getListNum("Entity.Groups.Group");
                Map<String, String> attrNameMap = new LinkedHashMap<String, String>();
                for (int i = 0; i < groupCount; i++) {
                    XmlBean group = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                    // 获取属性
                    int attrCount = group.getListNum("Group.Attrs.Attr");
                    String[] attrName = ["hsCd", "hsFullAddr", "hsOwnerPersons"];
                    for (int t = 0; t < attrName.length; t++) {
                        for (int k = 0; k < attrCount; k++) {
                            XmlBean tempAttrBean = group.getBeanByPath("Group.Attrs.Attr[${k}]");
                            String attrEnName = tempAttrBean.getStrValue("Attr.entityAttrNameEn");
                            if (StringUtil.isEqual(attrName[t], attrEnName)) {
                                attrNameMap.put("HouseInfo." + attrName[t], tempAttrBean.getStrValue("Attr.entityAttrNameCh"));
                                break;
                            }
                        }
                    }
                }
                modelMap.put("attrNameMap", attrNameMap);
            }
        }
        modelMap.put("initRegId", request.getParameter("regId"));
        // 获取显示模式
        modelMap.put("showJmTree", getCookieByName(request, "showJmTree"));
        modelMap.put("showJmTreeModel", getCookieByName(request, "showJmTreeModel"));
        // 获取输入的表单

        return new ModelAndView("/eland/ph/ph001/ph001", modelMap);
    }

    /**
     * 初始化建筑信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initB(HttpServletRequest request, HttpServletResponse response) {  // 返回数据
        ModelMap modelMap = new ModelMap();
        // 请求参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");
        String id = request.getParameter("rhtId");
        String regId = request.getParameter("regId");
        String rhtOrgId = request.getParameter("rhtOrgId");
        String rhtType = request.getParameter("rhtType");

        String regEntityType = request.getParameter("useType");
        modelMap.put("buildId", buildId);
        modelMap.put("id", id);
        modelMap.put("regEntityType", regEntityType);
        // 获取建筑信息
        XmlBean opData = new XmlBean();

        String conditionNames = request.getParameter("conditionNames");
        String conditions = request.getParameter("conditions");
        String conditionValues = request.getParameter("conditionValues");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        int addCount = 0;
        opData.setStrValue("OpData.entityName", "BuildInfo");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "buildId");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", buildId);
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        opData.setStrValue("OpData.ResultFields.fieldName[0]", "buildId");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "buildFullAddr");
        opData.setStrValue("OpData.ResultFields.fieldName[2]", "buildType");
        opData.setStrValue("OpData.ResultFields.fieldName[3]", "buldFloorNum");
        opData.setStrValue("OpData.ResultFields.fieldName[4]", "buildUnitNum");
        opData.setStrValue("OpData.ResultFields.fieldName[5]", "buildHsNum");
        opData.setStrValue("OpData.ResultFields.fieldName[6]", "buildLandSize");
//        opData.setStrValue("OpData.ResultFields.fieldName[7]", "hsCount");

        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        // 获取建筑内的房屋信息
        opData = new XmlBean();
        opData.setStrValue("OpData.ttOrgId", rhtOrgId);
        opData.setStrValue("OpData.ttRegId", regId);
        opData.setStrValue("OpData.regUseType", "1");
        opData.setStrValue("OpData.entityName", "HouseInfo");
        // 查询条件
        addCount = 0;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                    || StringUtil.isEmptyOrNull(conditionArr[i])
                    || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            opData.setStrValue("OpData.Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
            opData.setStrValue("OpData.Conditions.Condition[${addCount}].operation", conditionArr[i]);
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue("OpData.Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
            } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                opData.setStrValue("OpData.Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
            } else {
                opData.setStrValue("OpData.Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
            }
        }

        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue("OpData.OrgFilter.attrName", "\$ttOrgId");
        opData.setStrValue("OpData.OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue("OpData.OrgFilter.rhtType", rhtType);

        // 查询字段
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "hsAddrTail");
//        opData.setStrValue("OpData.ResultFields.fieldName[1]", "hsAddrNo");
        opData.setStrValue("OpData.ResultFields.fieldName[2]", "hsOwnerPersons");
        opData.setStrValue("OpData.ResultFields.fieldName[3]", "hsStatus");
        opData.setStrValue("OpData.ResultFields.fieldName[4]", "buldUnitNum");
        opData.setStrValue("OpData.ResultFields.fieldName[5]", "hsFullAddr");
        // 排序字段
        opData.setStrValue("OpData.SortFields.SortField[0].fieldName", "buldUnitNum");
        opData.setStrValue("OpData.SortFields.SortField[0].sortOrder", "asc");
        opData.setStrValue("OpData.SortFields.SortField[1].fieldName", "hsAddrTail");
        opData.setStrValue("OpData.SortFields.SortField[1].sortOrder", "asc");
        svcRequest.addOp("FILTER_QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务获取查询结果
        SvcResponse svcResponse = query(svcRequest);
        // 处理服务返回结果
        if (svcResponse.isSuccess()) {
            // 获取建筑的楼层数量
            List<XmlBean> allResult = svcResponse.getAllOpRsp("QUERY_ENTITY_CMPT");
            XmlBean opResult = allResult.get(0).getBeanByPath("Operation.OpResult.PageData.Row[0]");
            int floorNum = 0;
            int minNum = 0;
            if (opResult != null) {
                // 院落信息
                modelMap.put("buildInfo", opResult.getRootNode());
                // 建筑漏乘数
                floorNum = opResult.getIntValue("Row.buildFloorNum");
                // 获取房屋信息
                opResult = svcResponse.getFirstOpRsp("FILTER_QUERY_ENTITY_PAGE_DATA")
                        .getBeanByPath("Operation.OpResult.PageData");
                if (opResult != null) {
                    int rowCount = opResult.getListNum("PageData.Row");
                    Map<String, Set<String>> units = new LinkedHashMap<String, Set<String>>();
                    Map<String, XmlNode> houses = new HashMap<String, XmlNode>(rowCount);

                    for (int i = 0; i < rowCount; i++) {
                        XmlBean row = opResult.getBeanByPath("PageData.Row[${i}]");
                        // 获取房屋的单元名称， 门牌号
                        String unitName = row.getStrValue("Row.buldUnitNum");
                        String hsAddrTail = row.getStrValue("Row.hsAddrTail");
                        // 处理获取楼层和门牌号
                        String floorNo = "1";
                        String roomNo = hsAddrTail;
                        if (StringUtil.isNumeric(hsAddrTail) && hsAddrTail.length() > 2) {
                            int strLen = hsAddrTail.length();
                            floorNo = hsAddrTail.substring(0, strLen - 2);
                            roomNo = hsAddrTail.substring(strLen - 2, strLen);
                        }
                        if (StringUtil.isNumeric(floorNo) && floorNum < NumberUtil.getIntFromObj(floorNo)) {
                            floorNum = NumberUtil.getIntFromObj(floorNo);
                        }
                        if (StringUtil.isNumeric(floorNo) && minNum > NumberUtil.getIntFromObj(floorNo)) {
                            minNum = NumberUtil.getIntFromObj(floorNo);
                        }
                        // 从已有的信息中获取单元
                        Set<String> unit = units.get(unitName);
                        if (unit == null) {
                            unit = new TreeSet<String>();
                            units.put(unitName, unit);
                        }
                        // 从已有信息中获取
                        if (!unit.contains(roomNo)) {
                            unit.add(roomNo);
                        }
                        // 保存房源数据
                        houses.put("${unitName}_${floorNo}_${roomNo}".toString(), row.getRootNode());
                    }
                    // 返回单元和房屋列表
                    modelMap.put("units", units);
                    modelMap.put("houses", houses);
                }
            }
            // 设置建筑层数
            modelMap.put("floorNum", floorNum);
            modelMap.put("minNum", minNum);
        }
        modelMap.put("oldHsStatus", getCfgCollection(request, "OLD_HS_STATUS", true, svcRequest.getPrjCd()));
        modelMap.put("oldHsColorMap", getCfgCollection(request, "10002", true, svcRequest.getPrjCd()));


        return new ModelAndView("/eland/ph/ph001/ph001_build", modelMap);
    }
    /**
     * 显示院落统计报表
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initRpt(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        String opCode = requestUtil.getStrParam("uOpCode");
        // 返回数据
        ModelMap modelMap = new ModelMap();
        // 查询参数配置
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", ("RI_CP_" + opCode).toUpperCase());
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", true);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", svcRequest.getPrjCd());
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        List<JSONObject> list = new LinkedList<JSONObject>();
        if (svcResponse.isSuccess()) {
            int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            for (int i = 0; i < count; ++i) {
                JSONObject jsonObject = JSONObject.fromObject(svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].notes"));
                list.add(jsonObject);
            }
        }
        modelMap.put("list", list);
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ph/ph001/ph001_rpt", modelMap);
    }

    /**
     * 删除建筑信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteBuild(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");
        XmlBean operationData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        operationData.setStrValue(nodePath + "buildId", buildId);
        svcRequest.addOp("DELETE_TT_BUILD_INFO", operationData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "buildId: ${buildId}");
    }

    /**
     * 新增区域初始化
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView addRegion(HttpServletRequest request, HttpServletResponse response) {
        String upRegId = request.getParameter("upRegId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        /* 输入参数组织 */
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", upRegId);
        reqData.setStrValue(rootNodePath + "prjCd", request.getParameter("prjCd"));
        reqData.setStrValue(rootNodePath + "regUseType", request.getParameter("regUseType"));
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
        // 获取输出结果
        XmlBean prjRegNode = reqData.getBeanByPath("SvcCont.PrjReg");
        if (svcResponse.isSuccess()) {
            prjRegNode = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
        }
        rootNodePath = "PrjReg.";
        prjRegNode.setStrValue(rootNodePath + "regId", "");
        prjRegNode.setStrValue(rootNodePath + "regName", "");
        prjRegNode.setStrValue(rootNodePath + "regDesc", "");
        prjRegNode.setStrValue(rootNodePath + "upRegId", upRegId);
        prjRegNode.setStrValue(rootNodePath + "regAttr15", "");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        modelMap.put("nodeInfo", prjRegNode.getRootNode());

        return new ModelAndView("/eland/ph/ph001/ph00104", modelMap);
    }

    public ModelAndView editRegion(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        //  查看区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", requestUtil.getStrParam("regId"));
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "prjCd", requestUtil.getStrParam("prjCd"));
        // 区域类型
        reqData.setStrValue(rootNodePath + "regUseType", requestUtil.getStrParam("regUseType"));
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
        // 获取输出结果
        if (svcResponse.isSuccess()) {
            ModelMap modelMap = new ModelMap();
            XmlBean result = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
            if (result != null) {
                modelMap.put("nodeInfo", result.getRootNode());
            } else {
                modelMap.put("nodeInfo", "");
            }
            Map<String, String> regTypeMap = getCfgCollection(request, "REG_TYPE", true, svcRequest.getPrjCd());
            // 区域类型
            if (StringUtil.isEqual("1", result.getStrValue("PrjReg.regEntityType"))) {
                String name = regTypeMap.get("1");
                regTypeMap.clear();
                regTypeMap.put("1", name);
            } else {
                regTypeMap.remove("1");
            }
            modelMap.put("regTypeMap", regTypeMap);
            return new ModelAndView("/eland/ph/ph001/ph00103", modelMap);
        }
    }

    /**
     * 新增区域初始化
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView deleteRegion(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        //  查看区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String rootNodePath = "OpData.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "prjRegId", requestUtil.getStrParam("regId"));
        // 调用服务
        svcRequest.addOp("DELETE_TT_REG_CMPT", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 输出处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void moveRegion(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String moveType = request.getParameter("moveType");
        if ("prev".equals(moveType)) {
            moveType = "2"
        } else if ("next".equals(moveType)) {
            moveType = "3"
        } else {
            moveType = "1"
        }
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.prjCd", request.getParameter("prjCd"));
        opData.setStrValue("OpData.regUseType", "1");
        opData.setStrValue("OpData.moveId", request.getParameter("mNodeId"));
        opData.setStrValue("OpData.moveToId", request.getParameter("tNodeId"));
        opData.setStrValue("OpData.moveType", moveType);
        svcRequest.addOp("SAVE_MOVE_REG_TREE_CMPT", opData)
        SvcResponse svcResponse = transaction(svcRequest);
        // 输出处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 删除房产信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteHouse(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String houseId = request.getParameter("houseId");
        // 删除房产组件
        String nodePath = "OpData.";
        String[] houseIdArr = houseId.split(",");
        for (String hsId : houseIdArr) {
            if (StringUtil.isNotEmptyOrNull(hsId)) {
                XmlBean operationData = new XmlBean();
                operationData.setStrValue(nodePath + "hsId", hsId);
                svcRequest.addOp("DELETE_TT_HOUSE_INFO", operationData);
            }
        }
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 预计算设置
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView openRunScheme(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "PreassignedScheme");
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "schemeCreateTime");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "schemeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "schemeName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "schemeCreateTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "schemeStatus");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        Map<String, String> schemeMap = new LinkedHashMap<String, String>();
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT");
            if (resultBean != null) {
                List schList = new ArrayList();
                int schNum = resultBean.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < schNum; i++) {
                    XmlBean tempBean = resultBean.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                    schemeMap.put(tempBean.getStrValue("Row.schemeId"), tempBean.getStrValue("Row.schemeName"))
                }
            }
        }
        modelMap.put("schemeMap", schemeMap);
        return new ModelAndView("/eland/ph/ph001/ph001_run_scheme", modelMap);
    }

    /**
     * 导出选房信息
     */
    public void chooseExport(HttpServletRequest request, HttpServletResponse response) {
        XmlBean opData = new XmlBean("<OpData/>");
        String prePath = "OpData.";
        // 增加权限控制
        opData.setStrValue(prePath + "opName", "EXPORT_GFRMX_BYHSID_SCMPT");

        /* 项目组织和项目区域过滤处理 */
        //过滤的组织
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "HouseInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", "1");
        //过滤的区域 字段名
        String rhtRegIdValue = request.getParameter("rhtRegId");
        if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "HouseInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
            opData.setStrValue(prePath + "RegFilter.regUseType", "1");
        }

        // 从输入参数中获取查询条件和排序字段
        RequestUtil requestUtil = new RequestUtil(request);
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String[] sortColumnArr = requestUtil.getStrParam("sortColumns").split(",");
        String[] sortOrderArr = requestUtil.getStrParam("sortOrders").split(",", sortColumnArr.length);
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

        // 设置查询条件
        int addCount = 0;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                    || StringUtil.isEmptyOrNull(conditionArr[i])
                    || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
            } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
            }
        }

        // 排序信息
        int sortCount = 0;
        for (int i = 0; i < sortColumnArr.length; i++) {
            if (StringUtil.isEmptyOrNull(sortColumnArr[i])) {
                continue;
            }
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount}].fieldName", sortColumnArr[i]);
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount++}].sortOrder", sortOrderArr[i]);
        }

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("PRIVI_FILTER");
            String docPath = resultBean.getStrValue("Operation.OpResult.excelPath");
            //拿到文件名,封装成完整路径
            File localFile = ServerFile.getFile(docPath);
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, null, "GFPersonExportRel.xls", localFile, request.getHeader("USER-AGENT"), false);
        }
    }

    /**
     * 全量导出
     * @param request 请求信息
     * @param response 响应信息
     */
    public ModelAndView allExport(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String docFileUrl = "";
        //获取导出模板路径
        String formCd = request.getParameter("formCd");
        //查询模板路径
        XmlBean opData = null;
        String filePath = "";
        if (StringUtil.isNotEmptyOrNull(formCd)) {
            /* 根据formCd获取表单最高版本*/
            opData = new XmlBean();
            // 实体名
            opData.setStrValue("OpData.entityName", "SysFormInfoDef");
            // 条件
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "formCd");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", formCd);
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            // 查询结果
            opData.setStrValue("OpData.ResultFields.fieldName", "formVersion");
            // 排序条件
            opData.setStrValue("OpData.SortFields.SortField.fieldName", "formVersion");
            opData.setStrValue("OpData.SortFields.SortField.sortOrder", "desc");
            // 分页条件
            opData.setStrValue("OpData.PageInfo.pageSize", "1");
            opData.setStrValue("OpData.PageInfo.currentPage", "1");
            // 调用服务
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            // 启动服务
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
                if (pageData != null) {
                    //最高表单版本
                    String formVersion = pageData.getStrValue("PageData.Row[0].formVersion");
                    //下载模板
                    XmlBean paramData = new XmlBean();
                    svcRequest = RequestUtil.getSvcRequest(request);
                    paramData.setStrValue("OpData.formId", formCd + formVersion);
                    paramData.setStrValue("OpData.formTemplateCd", "IMPORT_EXCEL");
                    //判断是下载还是编辑模板，download下载、edit编辑，默认为编辑
                    svcRequest.addOp("FILE_DOWNLOAD_CMPT", paramData);
                    paramData.setStrValue("OpData.flag", "download");
                    svcResponse = transaction(svcRequest);
                    if (svcResponse.isSuccess()) {
                        XmlBean opResult = svcResponse.getFirstOpRsp("FILE_DOWNLOAD_CMPT");
                        if (opResult != null) {
                            String docPath = opResult.getValue("Operation.OpResult.path");
                            String cfgPath = PropertiesUtil.readPath("oframe", "com.shfb.oframe.common.report.reportfactory.report.path");
                            filePath = StringUtil.formatFilePath(cfgPath + docPath);
                        }
                    }
                }
                if (StringUtil.isEmptyOrNull(filePath)) {
                    ResponseUtil.printAjax(response, """{"success": false, "errMsg": "无导出模板，请联系管理员！"}""");
                    return;
                }
                opData = new XmlBean();
                String prePath = "OpData.";
                opData.setStrValue(prePath + "opName", "EXPORT_PBH_CMPT");
                opData.setStrValue(prePath + "filePath", filePath);
                opData.setStrValue(prePath + "cofNum", "6");

                /* 项目组织和项目区域过滤处理 */
                //过滤的组织
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "HouseInfo.\$ttOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "HouseInfo.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", "1");
                }

                // 从输入参数中获取查询条件和排序字段
                RequestUtil requestUtil = new RequestUtil(request);
                String conditionNames = requestUtil.getStrParam("conditionNames");
                String conditions = requestUtil.getStrParam("conditions");
                String conditionValues = requestUtil.getStrParam("conditionValues");
                String[] sortColumnArr = requestUtil.getStrParam("sortColumns").split(",");
                String[] sortOrderArr = requestUtil.getStrParam("sortOrders").split(",", sortColumnArr.length);
                String[] conditionFieldArr = conditionNames.split(",");
                String[] conditionArr = conditions.split(",");
                String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

                // 设置查询条件
                int addCount = 0;
                for (int i = 0; i < conditionFieldArr.length; i++) {
                    if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                            || StringUtil.isEmptyOrNull(conditionArr[i])
                            || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                        continue;
                    }
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                    if ("like".equalsIgnoreCase(conditionArr[i])) {
                        opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
                    } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                        String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                        opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
                    } else {
                        opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
                    }
                }
                // 排序信息
                int sortCount = 0;
                for (int i = 0; i < sortColumnArr.length; i++) {
                    if (StringUtil.isEmptyOrNull(sortColumnArr[i])) {
                        continue;
                    }
                    opData.setStrValue(prePath + "SortFields.SortField[${sortCount}].fieldName", sortColumnArr[i]);
                    opData.setStrValue(prePath + "SortFields.SortField[${sortCount++}].sortOrder", sortOrderArr[i]);
                }

                // 调用服务
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("PRIVI_FILTER", opData);
                svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    XmlBean resultBean = svcResponse.getFirstOpRsp("PRIVI_FILTER");
                    docFileUrl = resultBean.getStrValue("Operation.OpResult.filePath");
                    // 加密生成的文件
                    docFileUrl = StringUtil.encNetBase64Str(docFileUrl);
                }
            }
            ResponseUtil.printSvcResponse(response, svcResponse, """"docFileUrl": "${docFileUrl}" """);
        } else {
            ResponseUtil.printAjax(response, """{"success": false, "errMsg": "配置错误，请联系管理员！"}""")
        }
    }

    /**
     * 全量导入面板
     */
    public ModelAndView openImport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String formCd = request.getParameter("formCd");
        if (StringUtil.isNotEmptyOrNull(formCd)) {
            /* 根据formCd获取表单最高版本*/
            XmlBean opData = new XmlBean();
            // 实体名
            opData.setStrValue("OpData.entityName", "SysFormInfoDef");
            // 条件
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "formCd");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", formCd);
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            // 查询结果
            opData.setStrValue("OpData.ResultFields.fieldName", "formVersion");
            // 排序条件
            opData.setStrValue("OpData.SortFields.SortField.fieldName", "formVersion");
            opData.setStrValue("OpData.SortFields.SortField.sortOrder", "desc");
            // 分页条件
            opData.setStrValue("OpData.PageInfo.pageSize", "1");
            opData.setStrValue("OpData.PageInfo.currentPage", "1");
            // 调用服务
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            // 启动服务
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                        .getBeanByPath("Operation.OpResult.PageData");
                if (pageData != null) {
                    //最高表单版本
                    String formVersion = pageData.getStrValue("PageData.Row[0].formVersion");
                    //下载模板
                    XmlBean paramData = new XmlBean();
                    svcRequest = RequestUtil.getSvcRequest(request);
                    paramData.setStrValue("OpData.formId", formCd + formVersion);
                    paramData.setStrValue("OpData.formTemplateCd", "IMPORT_EXCEL");
                    paramData.setStrValue("OpData.flag", "download");//判断是下载还是编辑模板，download下载、edit编辑，默认为编辑
                    svcRequest.addOp("FILE_DOWNLOAD_CMPT", paramData);
                    svcResponse = transaction(svcRequest);
                    if (svcResponse.isSuccess()) {
                        XmlBean opResult = svcResponse.getFirstOpRsp("FILE_DOWNLOAD_CMPT");
                        if (opResult != null) {
                            String docPath = opResult.getValue("Operation.OpResult.path");
                            String docFileUrl = StringUtil.encNetBase64Str(docPath);
                            modelMap.put("docFileUrl", docFileUrl);
                        }
                    }
                }
            }
        }
        return new ModelAndView("/eland/ph/ph001/allImport_mb", modelMap);
    }
    /**
     * 全量导入
     * @param request 请求信息
     * @param response 响应信息
     */
    public ModelAndView allImport(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        MultipartFile localFile = super.getFile(request, "importMbFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."),
                originalFileName.length());
        String errMsg = "";
        boolean result = false;
        if (fileType.contains(".xls") || fileType.contains(".xlsx")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            File saveFile = ServerFile.createFile(remoteFileName);
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = new FileOutputStream(saveFile);
                inputStream = localFile.getInputStream();
                // 保存待上传文件信息
                byte[] b = new byte[1024 * 5];
                int len;
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                }
                outputStream.flush();
            } catch (IOException e) {
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (Exception e) {
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (Exception e) {
                    }
                }
            }
            //获取sheet页名字
            ExcelReader excelReader = new ExcelReader(saveFile);
            String sheetName = excelReader.getSheetName(0);
            XmlBean paramData = new XmlBean();
            String nodePath = "OpData.";
            paramData.setStrValue(nodePath + "filePath", saveFile.getAbsolutePath());
            if (StringUtil.isEqual("DATA_IMPORT", sheetName)) {
                paramData.setStrValue(nodePath + "configRowNum", "2");
            } else {
                paramData.setStrValue(nodePath + "configRowNum", "6");
            }
            paramData.setStrValue(nodePath + "serviceName", "IMPORT_PBH_CMPT");
            paramData.setStrValue(nodePath + "dealRowSize", "1");
            svcRequest.addOp("IMPORT_EXCEL_INFO", paramData);
            // 调用服务，执行数据查询
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                result = true;
            } else {
                errMsg = svcResponse.getErrMsg();
            }
            saveFile.delete();
        } else {
            errMsg = "文件格式不正确";
        }
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 根据名字获取cookie
     * @param request
     * @param name cookie名字
     * @return
     */
    private String getCookieByName(HttpServletRequest request, String name) {
        // 从cookie获取Session主键
        Cookie[] cookieArr = request.getCookies();
        if (cookieArr != null) {
            for (int i = 0; i < cookieArr.length; i++) {
                Cookie cookie = cookieArr[i];
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    protected Map<String, String> getCfgCollectionWithNote(HttpServletRequest httpServletRequest, String itemCd,
                                                           boolean isCached, int prjCd) {
        Object useCollection = (Map) httpServletRequest.getAttribute("C._" + itemCd + prjCd);
        if (useCollection == null) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(httpServletRequest);
            svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", itemCd);
            svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", Boolean.valueOf(isCached));
            svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", Integer.valueOf(prjCd));
            SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
            if (svcResponse.isSuccess()) {
                int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
                useCollection = new LinkedHashMap();

                for (int i = 0; i < count; ++i) {
                    String key = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].valueCd");
                    String notes = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].notes");
                    ((Map) useCollection).put(key, notes);
                }

                httpServletRequest.setAttribute("C._" + itemCd + prjCd, useCollection);
            }
        }

        return (Map) useCollection;
    }
}
