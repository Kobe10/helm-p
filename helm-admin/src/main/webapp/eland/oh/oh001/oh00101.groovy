import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.excel.ExcelWriter
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFColor
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 房源管理导入导出
 */
class oh001 extends GroovyController {

    /**
     * 以图形化展示建筑信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initB(HttpServletRequest request, HttpServletResponse response) {
        /* 输入参数可以动态定制的部分 */
        // 自定义的展示界面
        String displayBuildPage = request.getParameter("displayBuildPage");
        // 需要扩展查询的字段, 多个字段用","分割
        String moreResultField = request.getParameter("moreResultField");
        // 颜色定义码表
        String colorItemCd = request.getParameter("colorItemCd");
        if (StringUtil.isEmptyOrNull(colorItemCd)) {
            colorItemCd = "10003"
        }

        // 返回数据
        ModelMap modelMap = new ModelMap();
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");
        String regUseType = request.getParameter("regUseType");
        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        // 查询区域信息
        XmlBean opData = new XmlBean();
        opData.setValue("OpData.PrjReg.prjCd", prjCd);
        opData.setValue("OpData.PrjReg.regUseType", regUseType);
        opData.setValue("OpData.PrjReg.rightControlFlag", false);
        svcRequest.addOp("QUERY_REG_TREE", opData);

        // 请求参数
        opData = new XmlBean();
        String prePath = "OpData.";
        // 查询实体
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        // 实际调用的服务
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        opData.setStrValue(prePath + "queryType", "2");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "NewHsInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

        //过滤的区域 字段名
        if (StringUtil.isNotEmptyOrNull(regId)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", regId);
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        }
        // 其他查询条件
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
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
        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", "10000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        //排序
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");

        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "NewHsInfo.hsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "NewHsInfo.statusCd");
        // 街道地址
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "NewHsInfo.hsSt");
        // 楼宇编号
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "NewHsInfo.hsNo");
        // 地块
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "NewHsInfo.hsFl");
        // 单元
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "NewHsInfo.hsUn");
        // 门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "NewHsInfo.hsNm");
        // 归属区域
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "NewHsInfo.ttRegId");
        // 户型名称
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "NewHsInfo.hsHxName");
        //建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "NewHsInfo.hsBldSize");
        // 购房人
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "NewHsInfo.buyPersonName");
        // 房源编号
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "NewHsInfo.newHsId");
        // 单独门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "NewHsInfo.hsOnlyNm");
        // 增加扩展的查询字段
        if (StringUtil.isNotEmptyOrNull(moreResultField)) {
            int idx = 13;
            for (String temp : moreResultField.split(",")) {
                if (StringUtil.isNotEmptyOrNull(temp)) {
                    opData.setStrValue(prePath + "ResultFields.fieldName[${idx++}]", temp);
                }
            }
        }
        // 调用服务
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 处理服务返回结果
        if (svcResponse.isSuccess()) {
            // 解析区域目录树
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_REG_TREE")
                    .getBeanByPath("Operation.OpResult.PrjReg");
            // 区域名称映射关系, key: 区域ID、value: 区域Bean
            Map<String, XmlBean> hsAreaMap = new LinkedHashMap<String, XmlBean>();
            Map<String, XmlBean> foundHsAreaMap = new LinkedHashMap<String, XmlBean>();
            if (treeBean != null) {
                int count = treeBean.getListNum("PrjReg.Node");
                // 获取直接挂到根节点下的节点
                for (int i = 0; i < count; i++) {
                    XmlBean tempBean = treeBean.getBeanByPath("PrjReg.Node[${i}]");
                    String id = tempBean.getStrValue("Node.regId");
                    // 保存区域节点
                    hsAreaMap.put(id, tempBean);
                }
            }

            // 获取建筑的楼层数量
            XmlBean opResult = svcResponse.getFirstOpRsp("PRIVI_FILTER");
            opResult = opResult.getBeanByPath("Operation.OpResult.PageData");
            // 建筑列表
            Map<String, XmlBean> buildMap = new LinkedHashMap<String, XmlBean>();
            if (opResult != null) {
                int rowCount = opResult.getListNum("PageData.Row");
                // 所有的单元信息， key：区域编号即楼房, value: 楼房的单元信息
                Map<String, Set<String>> units = new LinkedHashMap<String, Set<String>>();
                // 按照“区域编号_单元编号_楼层编号_房间号”的形式存储每个房间
                Map<String, XmlNode> houses = new HashMap<String, XmlNode>(rowCount);
                // 按照“区域编号_单元编号_门牌号”的形式存储门牌的第一个房间
                Map<String, XmlNode> roomNoHsInfo = new HashMap<String, XmlNode>(rowCount);
                int minNum = 0;
                for (int i = 0; i < rowCount; i++) {
                    XmlBean row = opResult.getBeanByPath("PageData.Row[${i}]");
                    String ttRegId = row.getStrValue("Row.NewHsInfo.ttRegId");
                    // String hsArea = row.getStrValue("Row.NewHsInfo.hsFl");
                    String hsNo = hsAreaMap.get(ttRegId).getStrValue("Node.regName");
                    String unitName = row.getStrValue("Row.NewHsInfo.hsUn");
                    String hsNm = row.getStrValue("Row.NewHsInfo.hsNm");
                    String hsOnlyNm = row.getStrValue("Row.NewHsInfo.hsOnlyNm");
                    // 设置房屋选对应的房源区域
                    XmlBean hsAreaBean = getHsAreaBean(ttRegId, hsAreaMap, foundHsAreaMap);
                    String hsAreaId = hsAreaBean.getStrValue("Node.regId");
                    String hsAreaName = hsAreaBean.getStrValue("Node.regName");
                    row.setStrValue("Row.NewHsInfo.hsAreaId", hsAreaId);
                    row.setStrValue("Row.NewHsInfo.hsAreaName", hsAreaName);
                    // 建筑信息
                    XmlBean buildBean = buildMap.get(ttRegId);
                    if (buildBean == null) {
                        buildBean = new XmlBean();
                        // 房屋地址
                        buildBean.setStrValue("BuildInfo.ttRegId", ttRegId);
                        buildBean.setStrValue("BuildInfo.buildAddr", "${hsAreaName}-${hsNo}");
                        buildMap.put(ttRegId, buildBean);
                    }
                    // 处理楼房的楼层
                    int floorNum = buildBean.getIntValue("BuildInfo.floorNum");
                    String roomNo = "";
                    String floorNo = "";

                    int strOnlyLen = hsOnlyNm.length();
                    int strLen = hsNm.length();
                    if (strLen > 2) {
                        floorNo = hsNm.substring(0, strLen - 2);
                        if (strOnlyLen > 0) {
                            roomNo = hsOnlyNm;
                        } else {
                            roomNo = hsNm.substring(strLen - 2, strLen);
                        }
                    }
                    if (StringUtil.isPositiveNum(floorNo) && floorNum < NumberUtil.getIntFromObj(floorNo)) {
                        floorNum = NumberUtil.getIntFromObj(floorNo);
                    }
                    if (minNum > NumberUtil.getIntFromObj(floorNo)) {
                        minNum = NumberUtil.getIntFromObj(floorNo);
                    }

                    buildBean.setStrValue("BuildInfo.floorNum", floorNum);
                    buildBean.setStrValue("BuildInfo.minNum", minNum);

                    // 获取楼房的单元信息，不存创建存储
                    Map<String, Set<String>> bUnits = units.get(ttRegId);
                    if (bUnits == null) {
                        bUnits = new TreeMap<String, Set<String>>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        units.put(ttRegId, bUnits);
                    }

                    // 根据单元名称获取单元的房号信息
                    Set<String> unit = bUnits.get(unitName);
                    if (unit == null) {
                        unit = new TreeSet<String>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        bUnits.put(unitName, unit);
                    }
                    if (!unit.contains(roomNo)) {
                        unit.add(roomNo);
                        // 记录当前房间作为整个单元房号的信息
                        roomNoHsInfo.put("${ttRegId}_${unitName}_${roomNo}".toString(), row.getRootNode())
                    }
                    // 保存房源数据
                    houses.put("${ttRegId}_${unitName}_${floorNo}_${roomNo}".toString(), row.getRootNode());
                }

                // 返回单元和房屋列表
                List<XmlNode> builds = new ArrayList<XmlNode>(buildMap.size());
                for (XmlBean temp : buildMap.values()) {
                    builds.add(temp.getRootNode());
                }
                modelMap.put("builds", builds);
                modelMap.put("units", units);
                modelMap.put("roomNoHsInfo", roomNoHsInfo);
                modelMap.put("houses", houses);
            }
        }
        // 调用服务查询 院落 房产 状态对应的颜色信息
        svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean OpData = new XmlBean();
        OpData.setStrValue("OpData.SysCfgs.SysCfg[0].itemCd", colorItemCd);
        OpData.setStrValue("OpData.SysCfgs.SysCfg[0].isCached", "true");
        OpData.setStrValue("OpData.SysCfgs.SysCfg[0].prjCd", request.getParameter("prjCd"));
        svcRequest.addOp("QUERY_SYS_CFG_DATA", OpData);
        svcResponse = query(svcRequest);
        // 处理服务返回结果
        Map newHsColorMap = new HashMap();
        if (svcResponse.isSuccess()) {
            XmlBean colorResult = svcResponse.getFirstOpRsp("QUERY_SYS_CFG_DATA")
                    .getBeanByPath("Operation.OpResult.SysCfgs[0].SysCfg.Values");
            if (colorResult != null) {
                int count = colorResult.getListNum("Values.Value");
                List<XmlNode> newHsColorList = new ArrayList<XmlNode>();
                for (int i = 0; i < count; i++) {
                    XmlBean color = colorResult.getBeanByPath("Values.Value[${i}]");
                    newHsColorMap.put(color.getStrValue("Value.valueCd"), color.getStrValue("Value.valueName"));
                    newHsColorList.add(color.getRootNode());
                }
                modelMap.put("newHsColorMap", newHsColorMap);
                modelMap.put("newHsColorList", newHsColorList);
            }
        }
        if (StringUtil.isEmptyOrNull(displayBuildPage)) {
            displayBuildPage = "/eland/oh/oh001/oh00101_build";
        }
        return new ModelAndView(displayBuildPage, modelMap);
    }

    /**
     * 按照楼房导出房源销控表
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void exportB(HttpServletRequest request, HttpServletResponse response) {
        // 需要扩展查询的字段, 多个字段用","分割
        String moreResultField = request.getParameter("moreResultField");
        // 颜色定义码表
        String colorItemCd = request.getParameter("colorItemCd");
        if (StringUtil.isEmptyOrNull(colorItemCd)) {
            colorItemCd = "10003"
        }
        String colorRelCode = request.getParameter("colorRelCode");
        if (StringUtil.isEmptyOrNull(colorRelCode)) {
            colorRelCode = "NEW_HS_STATUS"
        }
        // 房源展示字段
        String showCdFieldName = request.getParameter("showCdFieldName");
        if (StringUtil.isEmptyOrNull(showCdFieldName)) {
            showCdFieldName = "statusCd";
        }

        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");
        String regUseType = request.getParameter("regUseType");
        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String data = "{}";
        // 查询区域信息
        XmlBean opData = new XmlBean();
        opData.setValue("OpData.PrjReg.prjCd", prjCd);
        opData.setValue("OpData.PrjReg.regUseType", regUseType);
        opData.setValue("OpData.PrjReg.rightControlFlag", false);
        svcRequest.addOp("QUERY_REG_TREE", opData);

        // 请求参数
        opData = new XmlBean();
        String prePath = "OpData.";
        // 查询实体
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        // 实际调用的服务
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        opData.setStrValue(prePath + "queryType", "2");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "NewHsInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

        //过滤的区域 字段名
        if (StringUtil.isNotEmptyOrNull(regId)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", regId);
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        }
        // 其他查询条件
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
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
        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", "10000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        //排序
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "NewHsInfo.hsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "NewHsInfo.statusCd");
        // 街道地址
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "NewHsInfo.hsSt");
        // 楼宇编号
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "NewHsInfo.hsNo");
        // 地块
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "NewHsInfo.hsFl");
        // 单元
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "NewHsInfo.hsUn");
        // 门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "NewHsInfo.hsNm");
        // 建筑标准
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "NewHsInfo.ttRegId");
        //户型名称
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "NewHsInfo.hsHxName");
        //建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "NewHsInfo.hsBldSize");
        // 购房人
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "NewHsInfo.buyPersonName");
        //预测面积
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "NewHsInfo.preBldSize");
        // 单独门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "NewHsInfo.hsOnlyNm");
        // 增加扩展的查询字段
        if (StringUtil.isNotEmptyOrNull(moreResultField)) {
            int idx = 13;
            for (String temp : moreResultField.split(",")) {
                if (StringUtil.isNotEmptyOrNull(temp)) {
                    opData.setStrValue(prePath + "ResultFields.fieldName[${idx++}]", temp);
                }
            }
        }
        // 调用服务
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);

        // 处理服务返回结果
        if (svcResponse.isSuccess()) {
            // 解析区域目录树
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_REG_TREE").getBeanByPath("Operation.OpResult.PrjReg");
            // 区域名称映射关系, key: 区域ID、value: 区域Bean
            Map<String, XmlBean> hsAreaMap = new LinkedHashMap<String, XmlBean>();
            Map<String, XmlBean> foundHsAreaMap = new LinkedHashMap<String, XmlBean>();
            if (treeBean != null) {
                int count = treeBean.getListNum("PrjReg.Node");
                // 获取直接挂到根节点下的节点
                for (int i = 0; i < count; i++) {
                    XmlBean tempBean = treeBean.getBeanByPath("PrjReg.Node[${i}]");
                    String id = tempBean.getStrValue("Node.regId");
                    // 保存区域节点
                    hsAreaMap.put(id, tempBean);
                }
            }
            // 获取建筑的楼层数量
            XmlBean opResult = svcResponse.getFirstOpRsp("PRIVI_FILTER");
            opResult = opResult.getBeanByPath("Operation.OpResult.PageData");
            // 获取房源状态颜色表
            // 颜色定义,
            Map<String, String> colorMap = getCfgCollection(request, colorItemCd, true, svcRequest.getPrjCd());
            Map<String, String> statusMap = getCfgCollection(request, colorRelCode, true, svcRequest.getPrjCd());

            // 建筑列表
            Map<String, XmlBean> buildMap = new LinkedHashMap<String, XmlBean>();
            if (opResult != null) {
                int rowCount = opResult.getListNum("PageData.Row");
                Map<String, Set<String>> units = new LinkedHashMap<String, Set<String>>();
                Map<String, XmlBean> houses = new HashMap<String, XmlBean>(rowCount);
                int minNum = 0;
                int floorNum;
                for (int i = 0; i < rowCount; i++) {
                    XmlBean row = opResult.getBeanByPath("PageData.Row[${i}]");
                    // 获取查询数据
                    String ttRegId = row.getStrValue("Row.NewHsInfo.ttRegId");
                    // String hsSt = row.getStrValue("Row.NewHsInfo.hsSt");
                    // String hsArea = row.getStrValue("Row.NewHsInfo.hsFl");
                    String hsNo = hsAreaMap.get(ttRegId).getStrValue("Node.regName");
                    String unitName = row.getStrValue("Row.NewHsInfo.hsUn");
                    String hsNm = row.getStrValue("Row.NewHsInfo.hsNm");
                    String hsOnlyNm = row.getStrValue("Row.NewHsInfo.hsOnlyNm");

                    // 设置房屋选对应的房源区域
                    XmlBean hsAreaBean = getHsAreaBean(ttRegId, hsAreaMap, foundHsAreaMap);
                    String hsAreaId = hsAreaBean.getStrValue("Node.regId");
                    String hsAreaName = hsAreaBean.getStrValue("Node.regName");
                    row.setStrValue("Row.NewHsInfo.hsAreaId", hsAreaId);
                    row.setStrValue("Row.NewHsInfo.hsAreaName", hsAreaName);

                    XmlBean buildBean = buildMap.get(ttRegId);
                    if (buildBean == null) {
                        buildBean = new XmlBean();
                        // 房屋地址
                        buildBean.setStrValue("BuildInfo.ttRegId", ttRegId);
                        buildBean.setStrValue("BuildInfo.buildAddr", "${hsAreaName}-${hsNo}");
                        buildMap.put(ttRegId, buildBean);
                    }

                    // 处理楼房的楼层
                    floorNum = buildBean.getIntValue("BuildInfo.floorNum");
                    minNum = buildBean.getIntValue("BuildInfo.minNum");
                    String roomNo = "";
                    String floorNo = "";

                    int strOnlyLen = hsOnlyNm.length();
                    int strLen = hsNm.length();
                    if (strLen > 2) {
                        floorNo = hsNm.substring(0, strLen - 2);
                        if (strOnlyLen > 0) {
                            roomNo = hsOnlyNm;
                        } else {
                            roomNo = hsNm.substring(strLen - 2, strLen);
                        }
                    }
                    if (StringUtil.isPositiveNum(floorNo) && floorNum < NumberUtil.getIntFromObj(floorNo)) {
                        floorNum = NumberUtil.getIntFromObj(floorNo);
                    }
                    if (minNum > NumberUtil.getIntFromObj(floorNo)) {
                        minNum = NumberUtil.getIntFromObj(floorNo);
                    }

                    buildBean.setStrValue("BuildInfo.floorNum", floorNum);
                    buildBean.setStrValue("BuildInfo.minNum", minNum);
                    // 单元信息
                    Map<String, Set<String>> bUnits = units.get(ttRegId);
                    if (bUnits == null) {
                        bUnits = new TreeMap<String, Set<String>>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        units.put(ttRegId, bUnits);
                    }
                    // 当前楼层的单元
                    Set<String> unit = bUnits.get(unitName);
                    if (unit == null) {
                        unit = new TreeSet<String>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        bUnits.put(unitName, unit);
                    }
                    if (!unit.contains(roomNo)) {
                        unit.add(roomNo);
                    }
                    // 保存房源数据
                    houses.put("${ttRegId}_${unitName}_${floorNo}_${roomNo}".toString(), row);
                }

                String templatePath = StringUtil.formatFilePath("webroot:eland/oh/oh001/newHsExport.xlsx");
                String excelName = ServerFile.generateFileName(".xlsx");
                String excelPath = "temp/" + excelName;
                ExcelWriter excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), new File(templatePath));
                int sheetWidth = excelWriter.getWorkbook().getSheetAt(0).getColumnWidth(0);

                // 样式
                CellStyle hsCellStyleTemp = excelWriter.getCellStyle(0, 0);
                Map<String, CellStyle> statusCellStyle = new HashMap<String, CellStyle>();
                for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                    String itemStatusCd = entry.getKey();
                    String itemColor = entry.getValue();
                    CellStyle newCellStyle = excelWriter.getWorkbook().createCellStyle();
                    newCellStyle.cloneStyleFrom(hsCellStyleTemp);
                    newCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode(itemColor)));
                    statusCellStyle.put(itemStatusCd, newCellStyle);
                }

                // 楼层单元格样式
                CellStyle louCCellStyle = excelWriter.getCellStyle(0, 2);
                // 单元标题样式
                CellStyle danYCellStyle = excelWriter.getCellStyle(0, 4);
                // 标题单元格样式
                CellStyle biaoTCellStyle = excelWriter.getCellStyle(0, 6);
                // 获取行高
                short height = excelWriter.getWorkbook().getSheetAt(0).getRow(0).getHeight();

                // 写入正式数据的开始行号
                int startDataRowIdx = 1;
                int con = 1;
                for (XmlBean buildXml : buildMap.values()) {
                    String buildttRegId = buildXml.getStrValue("BuildInfo.ttRegId");
                    String floorNumber = buildXml.getStrValue("BuildInfo.floorNum");
                    String minNumber = buildXml.getStrValue("BuildInfo.minNum");
                    String buildAddr = buildXml.getStrValue("BuildInfo.buildAddr");
                    Sheet sheet = excelWriter.getWorkbook().createSheet();
                    excelWriter.setWritableSheet(sheet);
                    excelWriter.getWorkbook().setSheetName(con, buildAddr);
                    con++;

                    // 写入样例说明
                    int statusIdx = 0;
                    for (Map.Entry<String, CellStyle> entry : statusCellStyle.entrySet()) {
                        CellStyle temp = entry.getValue();
                        excelWriter.writeData(0, statusIdx + 1, statusMap.get(entry.getKey()), temp, height);
                        statusIdx++;
                    }
                    // 写入楼层
                    int floor = NumberUtil.getIntFromObj(floorNumber) - NumberUtil.getIntFromObj(minNumber);
                    for (int j = 1; j <= floor; j++) {
                        excelWriter.writeData(startDataRowIdx + j + 1, 0,
                                "${NumberUtil.getIntFromObj(floorNumber) - j + 1}层", louCCellStyle, height);
                    }
                    // 写入单元
                    Map tempMap = (Map) units[buildttRegId];
                    Iterator it = tempMap.entrySet().iterator();
                    int count = 0;
                    while (it.hasNext()) {
                        Map.Entry tempEntryMap = (Map.Entry) it.next();
                        TreeSet unitMap = tempEntryMap.getValue();
                        int dan = unitMap.size();
                        excelWriter.mergedRegion(startDataRowIdx + 1, startDataRowIdx + 1, count + 1, dan + count);
                        for (int i = 0; i < dan; i++) {
                            excelWriter.writeData(startDataRowIdx + 1, count + 1 + i, "", danYCellStyle, height);
                        }
                        excelWriter.writeData(startDataRowIdx + 1, count + 1,
                                tempEntryMap.getKey() + "单元", danYCellStyle, height);
                        for (int j = 1; j <= floor; j++) {
                            Iterator itm = unitMap.iterator();
                            int excelClo = count + 1;
                            while (itm.hasNext()) {
                                String id = "${buildttRegId}_" + tempEntryMap.key + "_" + (NumberUtil.getIntFromObj(floorNumber) - j + 1) + "_" + itm.next();
                                XmlBean house = houses[id];
                                if (house != null) {
                                    String hsNm = house.getStrValue("Row.NewHsInfo.hsNm");
                                    String personName = house.getStrValue("Row.NewHsInfo.buyPersonName");
                                    String statusCd = house.getStrValue("Row.NewHsInfo." + showCdFieldName);
                                    String personInfo = hsNm + "(" + house.getStrValue("Row.NewHsInfo.preBldSize") + "  " + house.getStrValue("Row.NewHsInfo.hsHxName") + ")";
                                    if (StringUtil.isNotEmptyOrNull(personName)) {
                                        personInfo = personInfo + personName;
                                    }
                                    excelWriter.writeData(startDataRowIdx + j + 1, excelClo,
                                            personInfo, statusCellStyle.get(statusCd), height);
                                }
                                excelClo++;
                            }
                        }
                        count += dan;
                    }
                    for (int i = 1; i <= count; i++) {
                        sheet.setColumnWidth(i, sheetWidth);
                    }

                    // 合并表头样式
                    for (int i = 0; i <= count; i++) {
                        excelWriter.writeData(startDataRowIdx, i, "", biaoTCellStyle, height);
                    }
                    excelWriter.mergedRegion(startDataRowIdx, startDataRowIdx, 0, count);
                    excelWriter.writeData(startDataRowIdx, 0, buildAddr, biaoTCellStyle, height);
                }
                excelWriter.getWorkbook().removeSheetAt(0);
                excelWriter.closeBook();
                //结束操作
                data = """ {\"remoteFile\": \"${excelPath}\", "clientFile": "${excelName}\"}  """
            } else {
                ResponseUtil.printAjax(response, """{"success": false, "errMsg": "没有数据", "data": ""}""");
                return;
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

    /**
     * 按照户型导出房源销控表
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void exportByHx(HttpServletRequest request, HttpServletResponse response) {
        // 返回数据
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");
        String regUseType = request.getParameter("regUseType");

        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String data = "{}";

        // 查询区域信息
        XmlBean opData = new XmlBean();
        opData.setValue("OpData.PrjReg.prjCd", prjCd);
        opData.setValue("OpData.PrjReg.regUseType", regUseType);
        svcRequest.addOp("QUERY_REG_TREE", opData);

        // 请求参数
        opData = new XmlBean();
        String prePath = "OpData.";
        // 查询实体
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        // 实际调用的服务
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        opData.setStrValue(prePath + "queryType", "2");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "NewHsInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

        //过滤的区域 字段名
        if (StringUtil.isNotEmptyOrNull(regId)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", regId);
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        }
        // 其他查询条件
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
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

        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", "10000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        //排序（选房区域、户型名称）
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "NewHsInfo.hsSt");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        opData.setStrValue(prePath + "SortFields.SortField[1].fieldName", "NewHsInfo.hsHxName");
        opData.setStrValue(prePath + "SortFields.SortField[1].sortOrder", "asc");

        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "NewHsInfo.hsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "NewHsInfo.statusCd");
        // 街道地址
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "NewHsInfo.hsSt");
        // 楼宇编号
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "NewHsInfo.hsNo");
        // 地块
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "NewHsInfo.hsFl");
        // 单元
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "NewHsInfo.hsUn");
        // 门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "NewHsInfo.hsNm");
        // 建筑标准
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "NewHsInfo.ttRegId");
        //户型名称
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "NewHsInfo.hsHxName");
        //建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "NewHsInfo.hsBldSize");
        // 购房人
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "NewHsInfo.buyPersonName");
        //预测面积
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "NewHsInfo.preBldSize");
        // 单独门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "NewHsInfo.hsOnlyNm");
        // 调用服务
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);

        // 处理服务返回结果
        if (svcResponse.isSuccess()) {
            // 解析区域目录树
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_REG_TREE").getBeanByPath("Operation.OpResult.PrjReg");
            // 区域名称映射关系, key: 区域ID、value: 区域Bean
            Map<String, XmlBean> hsAreaMap = new LinkedHashMap<String, XmlBean>();
            Map<String, XmlBean> foundHsAreaMap = new LinkedHashMap<String, XmlBean>();
            if (treeBean != null) {
                int count = treeBean.getListNum("PrjReg.Node");
                // 获取直接挂到根节点下的节点
                for (int i = 0; i < count; i++) {
                    XmlBean tempBean = treeBean.getBeanByPath("PrjReg.Node[${i}]");
                    String id = tempBean.getStrValue("Node.regId");
                    // 保存区域节点
                    hsAreaMap.put(id, tempBean);
                }
            }
            // 获取建筑的楼层数量
            XmlBean opResult = svcResponse.getFirstOpRsp("PRIVI_FILTER");
            opResult = opResult.getBeanByPath("Operation.OpResult.PageData");
            // 获取房源状态颜色表
            Map<String, String> colorMap = getCfgCollection(request, "10003", true, svcRequest.getPrjCd());
            Map<String, String> statusMap = getCfgCollection(request, "NEW_HS_STATUS", true, svcRequest.getPrjCd());

            // 建筑列表
            Map<String, XmlBean> buildMap = new LinkedHashMap<String, XmlBean>();
            // 户型列表key：hsAreaId-hsHxName，value：户型信息
            Map<String, XmlBean> hxMap = new TreeMap<String, XmlBean>(new Comparator<String>() {
                @Override
                int compare(String o1, String o2) {
                    char[] o1Arr = o1.toCharArray();
                    char[] o2Arr = o2.toCharArray();
                    int minLength = o1Arr.length < o2.length() ? o1Arr.length : o2.length();
                    int sameIdex = -1;
                    for (int k = 0; k < minLength; k++) {
                        if (o1Arr[k] == o2Arr[k]) {
                            sameIdex = k;
                            break;
                        }
                    }
                    if (sameIdex == -1) {
                        return o1.compareTo(o2);
                    } else if (o1.length() > o2.length()) {
                        return 1;
                    } else if (o1.length() < o2.length()) {
                        return -1;
                    } else {
                        return o1.compareTo(o2);
                    }
                }
            });

            // 户型楼房数量
            Map<String, Set<String>> hxBuildMap = new LinkedHashMap<String, Set<String>>();

            if (opResult != null) {
                int rowCount = opResult.getListNum("PageData.Row");
                Map<String, Set<String>> units = new LinkedHashMap<String, Set<String>>();
                Map<String, XmlBean> houses = new HashMap<String, XmlBean>(rowCount);
                int minNum = 0;
                int floorNum;
                // 循环所有房源数据，汇总房源建筑信息
                for (int i = 0; i < rowCount; i++) {
                    XmlBean row = opResult.getBeanByPath("PageData.Row[${i}]");
                    String ttRegId = row.getStrValue("Row.NewHsInfo.ttRegId");
                    // String hsArea = row.getStrValue("Row.NewHsInfo.hsFl");
                    String hsNo = hsAreaMap.get(ttRegId).getStrValue("Node.regName");
                    String unitName = row.getStrValue("Row.NewHsInfo.hsUn");
                    String hsNm = row.getStrValue("Row.NewHsInfo.hsNm");
                    String hsOnlyNm = row.getStrValue("Row.NewHsInfo.hsOnlyNm");
                    String hsHxName = row.getStrValue("Row.NewHsInfo.hsHxName");
                    // 对应的房源区域
                    XmlBean hsAreaBean = getHsAreaBean(ttRegId, hsAreaMap, foundHsAreaMap);
                    String hsAreaId = hsAreaBean.getStrValue("Node.regId");
                    String hsAreaName = hsAreaBean.getStrValue("Node.regName");
                    // 转换户型汇总数据
                    String sheetKey = hsAreaId + "-" + hsHxName;
                    if (!hxMap.containsKey(sheetKey)) {
                        XmlBean tempBean = new XmlBean();
                        tempBean.setStrValue("HxInfo.areaName", hsAreaName);
                        tempBean.setStrValue("HxInfo.areaId", hsAreaId);
                        tempBean.setStrValue("HxInfo.hsHxName", hsHxName);
                        hxMap.put(sheetKey, tempBean);
                    }
                    // 记录户型拥有的建筑数量
                    Set<String> hsBuildInfo = hxBuildMap.get(sheetKey);
                    if (hsBuildInfo == null) {
                        hsBuildInfo = new LinkedHashSet<String>();
                        hxBuildMap.put(sheetKey, hsBuildInfo);
                    }
                    if (!hsBuildInfo.contains(ttRegId)) {
                        hsBuildInfo.add(ttRegId);
                    }
                    // 汇总建筑信息, 统计出建筑的总楼层，总单元数量
                    XmlBean buildBean = buildMap.get(sheetKey + "-" + ttRegId);
                    if (buildBean == null) {
                        buildBean = new XmlBean();
                        // 房屋地址
                        buildBean.setStrValue("BuildInfo.ttRegId", ttRegId);
                        buildBean.setStrValue("BuildInfo.buildAddr", "${hsAreaName}-${hsNo}(${hsHxName})");
                        buildMap.put(sheetKey + "-" + ttRegId, buildBean);
                    }

                    // 处理楼房的楼层
                    floorNum = buildBean.getIntValue("BuildInfo.floorNum");
                    minNum = buildBean.getIntValue("BuildInfo.minNum");
                    String roomNo = "";
                    String floorNo = "";
                    int strOnlyLen = hsOnlyNm.length();
                    int strLen = hsNm.length();
                    if (strLen > 2) {
                        floorNo = hsNm.substring(0, strLen - 2);
                        if (strOnlyLen > 0) {
                            roomNo = hsOnlyNm;
                        } else {
                            roomNo = hsNm.substring(strLen - 2, strLen);
                        }
                    }
                    if (StringUtil.isPositiveNum(floorNo) && floorNum < NumberUtil.getIntFromObj(floorNo)) {
                        floorNum = NumberUtil.getIntFromObj(floorNo);
                    }
                    if (minNum > NumberUtil.getIntFromObj(floorNo)) {
                        minNum = NumberUtil.getIntFromObj(floorNo);
                    }
                    buildBean.setStrValue("BuildInfo.floorNum", floorNum);
                    buildBean.setStrValue("BuildInfo.minNum", minNum);
                    // 单元信息
                    Map<String, Set<String>> bUnits = units.get(sheetKey + "-" + ttRegId);
                    if (bUnits == null) {
                        bUnits = new TreeMap<String, Set<String>>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        units.put(sheetKey + "-" + ttRegId, bUnits);
                    }

                    // 当前楼层的单元
                    Set<String> unit = bUnits.get(unitName);
                    if (unit == null) {
                        unit = new TreeSet<String>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        bUnits.put(unitName, unit);
                    }
                    if (!unit.contains(roomNo)) {
                        unit.add(roomNo);
                    }
                    // 保存房源数据
                    houses.put("${sheetKey}-${ttRegId}-${unitName}-${floorNo}-${roomNo}".toString(), row);
                }
                // 通过“区域ID+户型名称”——》“建筑信息” ——》 ”建筑内的单元信息" ——》 ”单元内的房屋信息"
                // 数据加工处理完成开始写入Excel
                String templatePath = StringUtil.formatFilePath("webroot:eland/oh/oh001/newHsExport.xlsx");
                String excelName = ServerFile.generateFileName(".xlsx");
                String excelPath = "temp/" + excelName;
                ExcelWriter excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), new File(templatePath));
                int sheetWidth = excelWriter.getWorkbook().getSheetAt(0).getColumnWidth(0);
                // 样式
                CellStyle hsCellStyleTemp = excelWriter.getCellStyle(0, 0);
                Map<String, CellStyle> statusCellStyle = new HashMap<String, CellStyle>();
                for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                    String itemStatusCd = entry.getKey();
                    String itemColor = entry.getValue();
                    CellStyle newCellStyle = excelWriter.getWorkbook().createCellStyle();
                    newCellStyle.cloneStyleFrom(hsCellStyleTemp);
                    newCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode(itemColor)));
                    statusCellStyle.put(itemStatusCd, newCellStyle);
                }

                // 楼层单元格样式
                CellStyle louCCellStyle = excelWriter.getCellStyle(0, 2);
                // 单元标题样式
                CellStyle danYCellStyle = excelWriter.getCellStyle(0, 4);
                // 标题单元格样式
                CellStyle biaoTCellStyle = excelWriter.getCellStyle(0, 6);
                // 获取行高
                short height = excelWriter.getWorkbook().getSheetAt(0).getRow(0).getHeight();

                // 写入正式数据的开始行号
                int startDataRowIdx = 1;
                int con = 1;

                // 循环户型列表
                for (XmlBean hxInfo : hxMap.values()) {
                    String hsAreaId = hxInfo.getStrValue("HxInfo.areaId");
                    String areaName = hxInfo.getStrValue("HxInfo.areaName");
                    String hsHxName = hxInfo.getStrValue("HxInfo.hsHxName");
                    String sheetKey = hsAreaId + "-" + hsHxName;
                    Sheet sheet = excelWriter.getWorkbook().createSheet();
                    excelWriter.setWritableSheet(sheet);
                    excelWriter.getWorkbook().setSheetName(con, hsHxName + "(" + areaName + ")");
                    con++;

                    // 获取户型拥有的建筑数量
                    Set<String> hxBuildIds = hxBuildMap.get(sheetKey);
                    int statusIdx = 0;
                    // 写入样例说明
                    for (Map.Entry<String, CellStyle> entry : statusCellStyle.entrySet()) {
                        CellStyle temp = entry.getValue();
                        excelWriter.writeData(0, statusIdx + 1, statusMap.get(entry.getKey()), temp, height);
                        statusIdx++;
                    }
                    // 循环处理sheet页面中的每一个房屋
                    int buildStartRow = startDataRowIdx;
                    for (String ttRegId : hxBuildIds) {
                        XmlBean buildXml = buildMap.get(sheetKey + "-" + ttRegId);
                        // 获取建筑信息
                        String floorNumber = buildXml.getStrValue("BuildInfo.floorNum");
                        String minNumber = buildXml.getStrValue("BuildInfo.minNum");
                        String buildAddr = buildXml.getStrValue("BuildInfo.buildAddr");
                        int floor = NumberUtil.getIntFromObj(floorNumber) - NumberUtil.getIntFromObj(minNumber);

                        // 写入楼层数据
                        for (int j = 1; j <= floor; j++) {
                            excelWriter.writeData(buildStartRow + j + 1, 0,
                                    "${NumberUtil.getIntFromObj(floorNumber) - j + 1}层", louCCellStyle, height);
                        }

                        // 获取单元信息
                        Map tempMap = (Map) units[sheetKey + "-" + ttRegId];
                        Iterator it = tempMap.entrySet().iterator();
                        int count = 0;
                        // 循环建筑的单元
                        while (it.hasNext()) {
                            // 处理单元数据
                            Map.Entry tempEntryMap = (Map.Entry) it.next();
                            TreeSet unitMap = tempEntryMap.getValue();
                            int dan = unitMap.size();
                            // 合并单元数量， 写入单元数据
                            excelWriter.mergedRegion(buildStartRow + 1, buildStartRow + 1,
                                    count + 1, dan + count);
                            for (int i = 0; i < dan; i++) {
                                excelWriter.writeData(buildStartRow + 1, count + 1 + i,
                                        "", danYCellStyle, height);
                            }
                            excelWriter.writeData(buildStartRow + 1, count + 1,
                                    tempEntryMap.getKey() + "单元", danYCellStyle, height);
                            // 按照楼层一次写入这个单元的漏乘数据
                            for (int j = 1; j <= floor; j++) {
                                Iterator itm = unitMap.iterator();
                                int excelClo = count + 1;
                                while (itm.hasNext()) {
                                    String id = "${sheetKey}-${ttRegId}-" + tempEntryMap.key + "-" + (NumberUtil.getIntFromObj(floorNumber) - j + 1) + "-" + itm.next();
                                    XmlBean house = houses[id];
                                    if (house != null) {
                                        String hsNm = house.getStrValue("Row.NewHsInfo.hsNm");
                                        String personName = house.getStrValue("Row.NewHsInfo.buyPersonName");
                                        String statusCd = house.getStrValue("Row.NewHsInfo.statusCd");
                                        String personInfo = hsNm;
                                        if (StringUtil.isNotEmptyOrNull(personName)) {
                                            personInfo = personInfo + "(" + personName + ")";
                                        }
                                        excelWriter.writeData(buildStartRow + j + 1, excelClo,
                                                personInfo, statusCellStyle.get(statusCd), height);
                                    }
                                    excelClo++;
                                }
                            }
                            count += dan;
                        }
                        // 设置列宽
                        for (int i = 1; i <= count; i++) {
                            sheet.setColumnWidth(i, sheetWidth);
                        }

                        // 合并房屋地址标题栏
                        for (int i = 0; i <= count; i++) {
                            excelWriter.writeData(buildStartRow, i, "", biaoTCellStyle, height);
                        }
                        excelWriter.mergedRegion(buildStartRow, buildStartRow, 0, count);
                        excelWriter.writeData(buildStartRow, 0, buildAddr, biaoTCellStyle, height);
                        // 下一个楼层增加
                        buildStartRow = buildStartRow + floor + 3;
                    }
                }
                excelWriter.getWorkbook().removeSheetAt(0);
                excelWriter.closeBook();
                //结束操作
                data = """ {\"remoteFile\": \"${excelPath}\", "clientFile": "${excelName}\"}  """
            } else {
                ResponseUtil.printAjax(response, """{"success": false, "errMsg": "没有数据", "data": ""}""");
                return;
            }

        }
        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

    /**
     * 按照居室类型导出销控表
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void exportByJs(HttpServletRequest request, HttpServletResponse response) {
        // 返回数据
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");
        String regUseType = request.getParameter("regUseType");

        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String data = "{}";

        // 查询区域信息
        XmlBean opData = new XmlBean();
        opData.setValue("OpData.PrjReg.prjCd", prjCd);
        opData.setValue("OpData.PrjReg.regUseType", regUseType);
        svcRequest.addOp("QUERY_REG_TREE", opData);

        // 请求参数
        opData = new XmlBean();
        String prePath = "OpData.";
        // 查询实体
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        // 实际调用的服务
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        opData.setStrValue(prePath + "queryType", "2");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "NewHsInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

        //过滤的区域 字段名
        if (StringUtil.isNotEmptyOrNull(regId)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", regId);
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        }
        // 其他查询条件
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
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

        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", "10000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        //排序（选房区域、户型名称）
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "NewHsInfo.hsSt");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        opData.setStrValue(prePath + "SortFields.SortField[1].fieldName", "NewHsInfo.hsHxName");
        opData.setStrValue(prePath + "SortFields.SortField[1].sortOrder", "asc");

        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "NewHsInfo.hsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "NewHsInfo.statusCd");
        // 街道地址
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "NewHsInfo.hsSt");
        // 楼宇编号
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "NewHsInfo.hsNo");
        // 地块
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "NewHsInfo.hsFl");
        // 单元
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "NewHsInfo.hsUn");
        // 门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "NewHsInfo.hsNm");
        // 建筑标准
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "NewHsInfo.ttRegId");
        //户型名称
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "NewHsInfo.hsHxName");
        //建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "NewHsInfo.hsBldSize");
        // 购房人
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "NewHsInfo.buyPersonName");
        //预测面积
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "NewHsInfo.preBldSize");
        //居室类型
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "NewHsInfo.hsJush");
        // 单独门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[14]", "NewHsInfo.hsOnlyNm");
        // 调用服务
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);

        // 处理服务返回结果
        if (svcResponse.isSuccess()) {
            // 解析区域目录树
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_REG_TREE").getBeanByPath("Operation.OpResult.PrjReg");
            // 区域名称映射关系, key: 区域ID、value: 区域Bean
            Map<String, XmlBean> hsAreaMap = new LinkedHashMap<String, XmlBean>();
            Map<String, XmlBean> foundHsAreaMap = new LinkedHashMap<String, XmlBean>();
            if (treeBean != null) {
                int count = treeBean.getListNum("PrjReg.Node");
                // 获取直接挂到根节点下的节点
                for (int i = 0; i < count; i++) {
                    XmlBean tempBean = treeBean.getBeanByPath("PrjReg.Node[${i}]");
                    String id = tempBean.getStrValue("Node.regId");
                    // 保存区域节点
                    hsAreaMap.put(id, tempBean);
                }
            }
            // 获取建筑的楼层数量
            XmlBean opResult = svcResponse.getFirstOpRsp("PRIVI_FILTER");
            opResult = opResult.getBeanByPath("Operation.OpResult.PageData");
            // 获取房源状态颜色表
            Map<String, String> colorMap = getCfgCollection(request, "10003", true, svcRequest.getPrjCd());
            Map<String, String> statusMap = getCfgCollection(request, "NEW_HS_STATUS", true, svcRequest.getPrjCd());
            Map<String, String> jushTypeMap = getCfgCollection(request, "NEW_HS_JUSH", true, svcRequest.getPrjCd());

            // 建筑列表
            Map<String, XmlBean> buildMap = new LinkedHashMap<String, XmlBean>();
            // 户型列表key：hsAreaId-hsHxName，value：户型信息
            Map<String, XmlBean> hxMap = new TreeMap<String, XmlBean>(new Comparator<String>() {
                @Override
                int compare(String o1, String o2) {
                    char[] o1Arr = o1.toCharArray();
                    char[] o2Arr = o2.toCharArray();
                    int minLength = o1Arr.length < o2.length() ? o1Arr.length : o2.length();
                    int sameIdex = -1;
                    for (int k = 0; k < minLength; k++) {
                        if (o1Arr[k] == o2Arr[k]) {
                            sameIdex = k;
                            break;
                        }
                    }
                    if (sameIdex == -1) {
                        return o1.compareTo(o2);
                    } else if (o1.length() > o2.length()) {
                        return 1;
                    } else if (o1.length() < o2.length()) {
                        return -1;
                    } else {
                        return o1.compareTo(o2);
                    }
                }
            });

            // 户型楼房数量
            Map<String, Set<String>> hxBuildMap = new LinkedHashMap<String, Set<String>>();

            if (opResult != null) {
                int rowCount = opResult.getListNum("PageData.Row");
                Map<String, Set<String>> units = new LinkedHashMap<String, Set<String>>();
                Map<String, XmlBean> houses = new HashMap<String, XmlBean>(rowCount);
                int minNum = 0;
                int floorNum;
                // 循环所有房源数据，汇总房源建筑信息
                for (int i = 0; i < rowCount; i++) {
                    XmlBean row = opResult.getBeanByPath("PageData.Row[${i}]");
                    String ttRegId = row.getStrValue("Row.NewHsInfo.ttRegId");
                    // String hsArea = row.getStrValue("Row.NewHsInfo.hsFl");
                    String hsNo = hsAreaMap.get(ttRegId).getStrValue("Node.regName");
                    String unitName = row.getStrValue("Row.NewHsInfo.hsUn");
                    String hsNm = row.getStrValue("Row.NewHsInfo.hsNm");
                    String hsOnlyNm = row.getStrValue("Row.NewHsInfo.hsOnlyNm");
                    String hsHxName = row.getStrValue("Row.NewHsInfo.hsHxName");
                    String hsJush = row.getStrValue("Row.NewHsInfo.hsJush");
                    // 对应的房源区域
                    XmlBean hsAreaBean = getHsAreaBean(ttRegId, hsAreaMap, foundHsAreaMap);
                    String hsAreaId = hsAreaBean.getStrValue("Node.regId");
                    String hsAreaName = hsAreaBean.getStrValue("Node.regName");
                    // 转换户型汇总数据
                    String sheetKey = hsAreaId + "-" + hsJush;
                    if (!hxMap.containsKey(sheetKey)) {
                        XmlBean tempBean = new XmlBean();
                        tempBean.setStrValue("HxInfo.areaName", hsAreaName);
                        tempBean.setStrValue("HxInfo.areaId", hsAreaId);
                        tempBean.setStrValue("HxInfo.hsHxName", hsHxName);
                        tempBean.setStrValue("HxInfo.hsJush", hsJush);
                        hxMap.put(sheetKey, tempBean);
                    }
                    // 记录户型拥有的建筑数量
                    Set<String> hsBuildInfo = hxBuildMap.get(sheetKey);
                    if (hsBuildInfo == null) {
                        hsBuildInfo = new LinkedHashSet<String>();
                        hxBuildMap.put(sheetKey, hsBuildInfo);
                    }
                    if (!hsBuildInfo.contains(ttRegId)) {
                        hsBuildInfo.add(ttRegId);
                    }
                    // 汇总建筑信息, 统计出建筑的总楼层，总单元数量
                    XmlBean buildBean = buildMap.get(sheetKey + "-" + ttRegId);
                    if (buildBean == null) {
                        buildBean = new XmlBean();
                        // 房屋地址
                        buildBean.setStrValue("BuildInfo.ttRegId", ttRegId);
                        buildBean.setStrValue("BuildInfo.buildAddr", "${hsAreaName}-${hsNo}(${jushTypeMap.get(hsJush)})");
                        buildMap.put(sheetKey + "-" + ttRegId, buildBean);
                    }

                    // 处理楼房的楼层
                    floorNum = buildBean.getIntValue("BuildInfo.floorNum");
                    minNum = buildBean.getIntValue("BuildInfo.minNum");
                    String roomNo = "";
                    String floorNo = "";
                    int strOnlyLen = hsOnlyNm.length();
                    int strLen = hsNm.length();
                    if (strLen > 2) {
                        floorNo = hsNm.substring(0, strLen - 2);
                        if (strOnlyLen > 0) {
                            roomNo = hsOnlyNm;
                        } else {
                            roomNo = hsNm.substring(strLen - 2, strLen);
                        }
                    }
                    if (StringUtil.isPositiveNum(floorNo) && floorNum < NumberUtil.getIntFromObj(floorNo)) {
                        floorNum = NumberUtil.getIntFromObj(floorNo);
                    }
                    if (minNum > NumberUtil.getIntFromObj(floorNo)) {
                        minNum = NumberUtil.getIntFromObj(floorNo);
                    }
                    buildBean.setStrValue("BuildInfo.floorNum", floorNum);
                    buildBean.setStrValue("BuildInfo.minNum", minNum);
                    // 单元信息
                    Map<String, Set<String>> bUnits = units.get(sheetKey + "-" + ttRegId);
                    if (bUnits == null) {
                        bUnits = new TreeMap<String, Set<String>>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        units.put(sheetKey + "-" + ttRegId, bUnits);
                    }

                    // 当前楼层的单元
                    Set<String> unit = bUnits.get(unitName);
                    if (unit == null) {
                        unit = new TreeSet<String>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj1.compareTo(obj2);
                            }
                        });
                        bUnits.put(unitName, unit);
                    }
                    if (!unit.contains(roomNo)) {
                        unit.add(roomNo);
                    }
                    // 保存房源数据
                    houses.put("${sheetKey}-${ttRegId}-${unitName}-${floorNo}-${roomNo}".toString(), row);
                }
                // 通过“区域ID+户型名称”——》“建筑信息” ——》 ”建筑内的单元信息" ——》 ”单元内的房屋信息"
                // 数据加工处理完成开始写入Excel
                String templatePath = StringUtil.formatFilePath("webroot:eland/oh/oh001/newHsExport.xlsx");
                String excelName = ServerFile.generateFileName(".xlsx");
                String excelPath = "temp/" + excelName;
                ExcelWriter excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), new File(templatePath));
                int sheetWidth = excelWriter.getWorkbook().getSheetAt(0).getColumnWidth(0);
                // 样式
                CellStyle hsCellStyleTemp = excelWriter.getCellStyle(0, 0);
                Map<String, CellStyle> statusCellStyle = new HashMap<String, CellStyle>();
                for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                    String itemStatusCd = entry.getKey();
                    String itemColor = entry.getValue();
                    CellStyle newCellStyle = excelWriter.getWorkbook().createCellStyle();
                    newCellStyle.cloneStyleFrom(hsCellStyleTemp);
                    newCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.decode(itemColor)));
                    statusCellStyle.put(itemStatusCd, newCellStyle);
                }

                // 楼层单元格样式
                CellStyle louCCellStyle = excelWriter.getCellStyle(0, 2);
                // 单元标题样式
                CellStyle danYCellStyle = excelWriter.getCellStyle(0, 4);
                // 标题单元格样式
                CellStyle biaoTCellStyle = excelWriter.getCellStyle(0, 6);
                // 获取行高
                short height = excelWriter.getWorkbook().getSheetAt(0).getRow(0).getHeight();

                // 写入正式数据的开始行号
                int startDataRowIdx = 1;
                int con = 1;

                // 循环户型列表
                for (XmlBean hxInfo : hxMap.values()) {
                    String hsAreaId = hxInfo.getStrValue("HxInfo.areaId");
                    String areaName = hxInfo.getStrValue("HxInfo.areaName");
                    String hsJush = hxInfo.getStrValue("HxInfo.hsJush");
                    String sheetKey = hsAreaId + "-" + hsJush;
                    Sheet sheet = excelWriter.getWorkbook().createSheet();
                    excelWriter.setWritableSheet(sheet);
                    excelWriter.getWorkbook().setSheetName(con, jushTypeMap.get(hsJush) + "(" + areaName + ")");
                    con++;

                    // 获取户型拥有的建筑数量
                    Set<String> hxBuildIds = hxBuildMap.get(sheetKey);
                    int statusIdx = 0;
                    // 写入样例说明
                    for (Map.Entry<String, CellStyle> entry : statusCellStyle.entrySet()) {
                        CellStyle temp = entry.getValue();
                        excelWriter.writeData(0, statusIdx + 1, statusMap.get(entry.getKey()), temp, height);
                        statusIdx++;
                    }
                    // 循环处理sheet页面中的每一个房屋
                    int buildStartRow = startDataRowIdx;
                    for (String ttRegId : hxBuildIds) {
                        XmlBean buildXml = buildMap.get(sheetKey + "-" + ttRegId);
                        // 获取建筑信息
                        String floorNumber = buildXml.getStrValue("BuildInfo.floorNum");
                        String minNumber = buildXml.getStrValue("BuildInfo.minNum");
                        String buildAddr = buildXml.getStrValue("BuildInfo.buildAddr");
                        int floor = NumberUtil.getIntFromObj(floorNumber) - NumberUtil.getIntFromObj(minNumber);

                        // 写入楼层数据
                        for (int j = 1; j <= floor; j++) {
                            excelWriter.writeData(buildStartRow + j + 1, 0,
                                    "${NumberUtil.getIntFromObj(floorNumber) - j + 1}层", louCCellStyle, height);
                        }

                        // 获取单元信息
                        Map tempMap = (Map) units[sheetKey + "-" + ttRegId];
                        Iterator it = tempMap.entrySet().iterator();
                        int count = 0;
                        // 循环建筑的单元
                        while (it.hasNext()) {
                            // 处理单元数据
                            Map.Entry tempEntryMap = (Map.Entry) it.next();
                            TreeSet unitMap = tempEntryMap.getValue();
                            int dan = unitMap.size();
                            // 合并单元数量， 写入单元数据
                            excelWriter.mergedRegion(buildStartRow + 1, buildStartRow + 1,
                                    count + 1, dan + count);
                            for (int i = 0; i < dan; i++) {
                                excelWriter.writeData(buildStartRow + 1, count + 1 + i,
                                        "", danYCellStyle, height);
                            }
                            excelWriter.writeData(buildStartRow + 1, count + 1,
                                    tempEntryMap.getKey() + "单元", danYCellStyle, height);
                            // 按照楼层一次写入这个单元的漏乘数据
                            for (int j = 1; j <= floor; j++) {
                                Iterator itm = unitMap.iterator();
                                int excelClo = count + 1;
                                while (itm.hasNext()) {
                                    String id = "${sheetKey}-${ttRegId}-" + tempEntryMap.key + "-" + (NumberUtil.getIntFromObj(floorNumber) - j + 1) + "-" + itm.next();
                                    XmlBean house = houses[id];
                                    if (house != null) {
                                        String hsNm = house.getStrValue("Row.NewHsInfo.hsNm");
                                        String personName = house.getStrValue("Row.NewHsInfo.buyPersonName");
                                        String statusCd = house.getStrValue("Row.NewHsInfo.statusCd");
                                        String personInfo = hsNm;
                                        if (StringUtil.isNotEmptyOrNull(personName)) {
                                            personInfo = personInfo + "(" + personName + ")";
                                        }
                                        excelWriter.writeData(buildStartRow + j + 1, excelClo,
                                                personInfo, statusCellStyle.get(statusCd), height);
                                    }
                                    excelClo++;
                                }
                            }
                            count += dan;
                        }
                        // 设置列宽
                        for (int i = 1; i <= count; i++) {
                            sheet.setColumnWidth(i, sheetWidth);
                        }

                        // 合并房屋地址标题栏
                        for (int i = 0; i <= count; i++) {
                            excelWriter.writeData(buildStartRow, i, "", biaoTCellStyle, height);
                        }
                        excelWriter.mergedRegion(buildStartRow, buildStartRow, 0, count);
                        excelWriter.writeData(buildStartRow, 0, buildAddr, biaoTCellStyle, height);
                        // 下一个楼层增加
                        buildStartRow = buildStartRow + floor + 3;
                    }
                }
                excelWriter.getWorkbook().removeSheetAt(0);
                excelWriter.closeBook();
                //结束操作
                data = """ {\"remoteFile\": \"${excelPath}\", "clientFile": "${excelName}\"}  """
            } else {
                ResponseUtil.printAjax(response, """{"success": false, "errMsg": "没有数据", "data": ""}""");
                return;
            }

        }
        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

    /**
     * 获取区域节点对应房源区域ID(房源区域：第二级节点)
     * @param regId
     * @param hsAreaMap
     * @return
     */
    private XmlBean getHsAreaBean(String regId, Map<String, XmlBean> hsAreaMap, Map<String, XmlBean> fountHsAreaMap) {
        XmlBean result = fountHsAreaMap.get(regId);
        if (result != null) {
            return result;
        }
        // 当前Bean对象
        XmlBean currentBean = hsAreaMap.get(regId);
        if (currentBean != null) {
            // 获取当前节点的上级节点编号
            String pId = currentBean.getStrValue("Node.upRegId");
            // 获取当前节点的上级节点,上级节点不为空
            XmlBean currentPBean = hsAreaMap.get(pId);
            if (currentPBean != null) {
                String pUId = currentPBean.getStrValue("Node.upRegId");
                // 当前节点的上级节点的PId等于0的时候返回当前节点
                if ("0".equals(pUId)) {
                    fountHsAreaMap.put(regId, currentBean);
                    return currentBean;
                } else {
                    return getHsAreaBean(pId, hsAreaMap, fountHsAreaMap);
                }
            } else if (StringUtil.isEqual(pId, "0")) {
                // 当前节点已经是根节点则直接返回
                fountHsAreaMap.put(regId, currentBean);
                return currentBean;
            }
        }
        return null;
    }

}
