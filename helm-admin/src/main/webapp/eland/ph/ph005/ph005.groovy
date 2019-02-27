import com.alibaba.fastjson.JSON
import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import net.sf.json.JsonConfig
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房  项目地图展示
 */
class ph005 extends GroovyController {

    /** 展示所有状态地图 */
    public ModelAndView showAllMap(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        String privilegeId = request.getParameter("privilegeId");
        modelMap.put("prjCd", prjCd);
        modelMap.put("privilegeId", privilegeId);
        String lon = request.getParameter("lon");
        String lat = request.getParameter("lat");
        String level = request.getParameter("level");
        /**
         *  查询所有的配置参数。图层地址，以及图层名称
         */
        Map<String, String> mapConfig = getCfgCollection(request, "geoserver_map", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String baselayerUrl = mapConfig.get("MAP_LAYER_URL");
        String layerName1 = mapConfig.get("MAP_LAYER_NAME1");
        String layerName2 = mapConfig.get("MAP_LAYER_NAME2");
        String lonLatLevel = mapConfig.get("MAP_LON_LAT_LEVEL");
        String mapBounds = mapConfig.get("MAP_BOUNDS");
        String minScale = mapConfig.get("MIN_SCALE");
        String maxScale = mapConfig.get("MAX_SCALE");
        if (StringUtil.isEmptyOrNull(minScale)){
            minScale = "4000";
        }
        if (StringUtil.isEmptyOrNull(maxScale)){
            maxScale = "1000";
        }
        //查询原有地图坐标，返回页面绘制地图
        modelMap.put("baselayerUrl", baselayerUrl);
        modelMap.put("layerName1", layerName1);
        modelMap.put("layerName2", layerName2);
        modelMap.put("lonLatLevel", lonLatLevel);
        modelMap.put("mapBounds", mapBounds);
        modelMap.put("minScale", minScale);
        modelMap.put("maxScale", maxScale);

        //返回参数页面传递参数，设置中心
        modelMap.put("lon", lon);
        modelMap.put("lat", lat);
        modelMap.put("level", level);
        String fullFlag = request.getParameter("fullFlag");
        modelMap.put("fullFlag", fullFlag);
        return new ModelAndView("/eland/ph/ph005/ph005", modelMap);
    }

    /** 独立数据加载方法 获取绘图数据 */
    public void getDrawMapData(HttpServletRequest request, HttpServletResponse response) {
        //处理参数
        int prjCd = NumberUtil.getIntFromObj(request.getParameter("prjCd"));

        //将查询参数提取到 参数列表里配置 地图搜索条件 查询 自由配置参数 显示汉字、数据库实体字段、码表值，
        String whichOne = request.getParameter("whichOne");
        // 调用 私有方法查询 配置参数: 获取每个搜索页面配置的 筛选条件
        Map conditionMap = queryConditionCfg(request, whichOne);
        def selectConditionEnName = (String[]) conditionMap.get("selectConditionEnName");
        def selectConditionChName = (String[]) conditionMap.get("selectConditionChName");
        def selectConditionParam = (String[]) conditionMap.get("selectConditionParam");

        //获取bbox的范围   left,bottom,right,top
        String bbox = request.getParameter("bbox");
        String[] bboxTemps;
        if (StringUtil.isNotEmptyOrNull(bbox)) {
            bboxTemps = bbox.split(",");
        } else {
            Map<String, String> mapConfig = getCfgCollection(request, "geoserver_map", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
            String mapBounds = mapConfig.get("MAP_BOUNDS");
            bboxTemps = mapBounds.split(",");
        }

        //######################################### 根据传入 显示图层,判断加载 那个图层的数据 ########################
        //返回数据 最后要转换成json格式返回。
        List<Map<String, String>> buildResult = new ArrayList<Map<String, String>>();
        List<Map<String, String>> hsResultMapList = new ArrayList<Map<String, String>>();
        List<Map<String, String>> camResultList = new ArrayList<Map<String, String>>();

        //根据限制图层,分别调用不同 方法查询各自数据
        String showLayer = request.getParameter("showLayer");
        String[] showLayers = showLayer.split(",");
        if (showLayers.length > 0) {
            for (int i = 0; i < showLayers.length; i++) {
                String temp = showLayers[i];
                if (StringUtil.isEqual(temp, "build")) {
                    buildResult = getBuildData(request, bboxTemps, conditionMap);
                } else if (StringUtil.isEqual(temp, "house")) {
                    hsResultMapList = getHouseData(request, bboxTemps, conditionMap);
                } else if (StringUtil.isEqual(temp, "cam")) {
                    camResultList = getCameraData(request);
                }
            }
        }

        boolean result = false;
        String errMsg = "";
        String jsonStr;
        result = true;

        /**
         * 处理 搜索条件 选项展示颜色.
         */
        Map oldHsColorMap = new HashMap();
        //获取该维度的所有状态值，并赋予用于自定义颜色
        String dimensionColorStr = request.getParameter("dimensionColorList");
        String[] dimensionColorArr = [];
        if (StringUtil.isNotEmptyOrNull(dimensionColorStr)) {
            dimensionColorArr = dimensionColorStr.split(",");
        }
        Map dimensionColorMap = new HashMap();
        //获取当前查询维度.码值
        String showDimensionMark = request.getParameter("showDimensionMark");
        String showDimensionMarkParam = "";
        for (int i = 0; i < selectConditionEnName.length; i++) {
            String dimension = selectConditionEnName[i];
            //处理当前查询维度获取码值
            if (StringUtil.isEqual(dimension, showDimensionMark)) {
                showDimensionMarkParam = selectConditionParam[i];
            }
        }
        if (StringUtil.isNotEmptyOrNull(showDimensionMarkParam)) {
            dimensionColorMap = getCfgCollection(request, showDimensionMarkParam, true, prjCd);
        }
        if (dimensionColorMap != null && dimensionColorArr.length == dimensionColorMap.size()) {
            String[] mapKeySet = dimensionColorMap.keySet().toArray();
            for (int i = 0; i < mapKeySet.length; i++) {
                String mapkey = mapKeySet[i];
                oldHsColorMap.put(mapkey, dimensionColorArr[i]);
            }
        }

        /**
         * 处理结果，转换json 推送前端页面
         */
        //JSON报错net.sf.json.JSONException: java.lang.ClassCastException: JSON keys must be strings.，修正
        JsonConfig jsonConfig = new JsonConfig();
        // 排除,避免循环引用 There is a cycle in the hierarchy!
        jsonConfig.setIgnoreDefaultExcludes(true);
        jsonConfig.setAllowNonStringKeys(true);


        JSONArray hsResultJSON = JSONArray.fromObject(hsResultMapList);
        JSONArray buildResultJSON = JSONArray.fromObject(buildResult);
        JSONArray camResultJSON = JSONArray.fromObject(camResultList);
        JSONObject oldHsColorJSON = JSONObject.fromObject(oldHsColorMap);
        jsonStr = """{success: ${result}, errMsg:"${
            errMsg
        }",showDimensionMark:"${
            showDimensionMark
        }",buildResult:${
            buildResultJSON.toString()
        },hsResultMapList:${
            hsResultJSON.toString()
        },oldHsColorMap:${
            oldHsColorJSON.toString()
        },camResultList:${
            camResultJSON.toString()
        }}""";

        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 获取建筑地图数据 */
    private List getBuildData(HttpServletRequest request, String[] bboxTemps, Map conditionMap) {
        SvcRequest svcReqFilt = RequestUtil.getSvcRequest(request);
        //根据 系统配置查询 搜索条件.
        def selectConditionEnName = (String[]) conditionMap.get("selectConditionEnName");
        def selectConditionChName = (String[]) conditionMap.get("selectConditionChName");
        def selectConditionParam = (String[]) conditionMap.get("selectConditionParam");
        // 高级查询条件
        RequestUtil requestUtil = new RequestUtil(request);
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        //查询类型
        String whichOne = request.getParameter("whichOne");

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "BuildInfo");
        /* 查询条件 */
        //判断查询 维度，是建筑，还是房屋。
        //获取 自定义查询条件 维度
        //当前查询维度标志，并获取码值供下方获取状态予之对应颜色使用
//        String showDimensionMark = request.getParameter("showDimensionMark");
//        String showDimensionMarkParam = "";
        if (StringUtil.isEqual(whichOne, "build")) {
            int conNum = 0;
            for (int i = 0; i < selectConditionEnName.length; i++) {
                String dimension = selectConditionEnName[i];
                String dimensionVal = request.getParameter(dimension);
                //处理当前查询维度获取码值
//                if (StringUtil.isEqual(dimension, showDimensionMark)) {
//                    showDimensionMarkParam = selectConditionParam[i];
//                }
                //获取传递条件，查询数据
                if (dimensionVal != null) {
                    opData.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "${dimension}");
                    opData.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldValue", "(${dimensionVal})");
                    opData.setStrValue(prePath + "Conditions.Condition[${conNum++}].operation", "IN");
                }
            }
            //获取 输入的 查询条件
            String inputCondition = request.getParameter("inputCondition");
            String inputConditionValue = request.getParameter("inputConditionValue");
            if (StringUtil.isNotEmptyOrNull(inputCondition) && !StringUtil.isEqual("undefined", inputCondition)) {
                opData.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "${inputCondition}");
                opData.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldValue", "%${inputConditionValue}%");
                opData.setStrValue(prePath + "Conditions.Condition[${conNum++}].operation", "like");
            }

            String regOrgIdValue = "";
            String rhtRegIdValue = "";
            // 高级查询增加的查询条件
            for (int i = 0; i < conditionFieldArr.length; i++) {
                if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                        || StringUtil.isEmptyOrNull(conditionArr[i])
                        || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                    continue;
                }
                // 管理组织部作为直接查询条件
                if ("ttOrgId".equals(conditionFieldArr[i])) {
                    regOrgIdValue = conditionValueArr[i];
                    continue;
                }
                // 管理区域与不作为自己查询条件
                if ("ttRegId".equals(conditionFieldArr[i])) {
                    rhtRegIdValue = conditionValueArr[i];
                    continue;
                }

                opData.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", conditionFieldArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${conNum}].operation", conditionArr[i]);
                if ("like".equalsIgnoreCase(conditionArr[i])) {
                    opData.setStrValue(prePath + "Conditions.Condition[${conNum++}].fieldValue", "%" + conditionValueArr[i] + "%");
                } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                    String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                    opData.setStrValue(prePath + "Conditions.Condition[${conNum++}].fieldValue", "(" + usedValue + ")");
                }  else if ("REGEXP".equalsIgnoreCase(conditionArr[i])) {// 问题树正则匹配  正则表达式  '(,100101)|(^100101)'
                    //处理多条件
                    String[] rLikeFieldValueArr = conditionValues.split("\\|");
                    for (int j = 0; j<rLikeFieldValueArr.length; j++) {
                        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
                        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                        opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(," + rLikeFieldValueArr[j] + ")|(^" + rLikeFieldValueArr[j] + ")");
                    }
                } else {
                    opData.setStrValue(prePath + "Conditions.Condition[${conNum++}].fieldValue", conditionValueArr[i]);
                }
            }

            /* 组织和区域权限过滤特殊处理 */
            // 过滤的组织 字段名
            opData.setStrValue(prePath + "OrgFilter.attrName", "ttOrgId");
            opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
            opData.setStrValue(prePath + "OrgFilter.rhtType", "1");
            // 过滤的区域 字段名
            if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                opDataHs.setStrValue(prePath + "RegFilter.attrName", "ttRegId");
                opDataHs.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                opDataHs.setStrValue(prePath + "RegFilter.regUseType", "1");
            }
        }
        // 设置分页
        opData.setStrValue(prePath + "PageInfo.pageSize", "5000");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "buildId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "buildNo");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "buildAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "buildStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "buildPosition");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "ttRegId");
        //拼接动态结果字段
        if (StringUtil.isEqual(whichOne, "build")) {
            for (int i = 0; i < selectConditionEnName.length; i++) {
                //获取传递条件，查询数据
                opData.setStrValue(prePath + "ResultFields.fieldName[${6 + i}]", "${selectConditionEnName[i]}");
            }
        }
        // 数据权限过滤组件
        opData.setStrValue(prePath + "opName", "FILTER_QUERY_ENTITY_PAGE_DATA");
        svcReqFilt.addOp("PRIVI_FILTER", opData);
        // 调用服务查询院落数据
        SvcResponse svcResponse = query(svcReqFilt);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("PRIVI_FILTER")
                    .getBeanByPath("Operation.OpResult.PageData");
            List<Map<String, String>> buildResult = new ArrayList<Map<String, String>>();
            // 处理查询 建筑地图数据 结果，返回json格式数据
            if (queryResult != null) {
                int buildCount = queryResult.getListNum("PageData.Row");
                for (int i = 0; i < buildCount; i++) {
                    String tempField01 = queryResult.getStrValue("PageData.Row[${i}].buildId");
                    String tempField02 = queryResult.getStrValue("PageData.Row[${i}].buildNo");
                    String tempField03 = queryResult.getStrValue("PageData.Row[${i}].buildAddr");
                    String tempField04 = queryResult.getStrValue("PageData.Row[${i}].buildStatus");
                    String tempField05 = queryResult.getStrValue("PageData.Row[${i}].buildPosition");
                    String tempField06 = queryResult.getStrValue("PageData.Row[${i}].ttRegId");

                    Map<String, String> temp = ["buildId": tempField01, "buildNo": tempField02, "buildAddr": tempField03, "buildStatus": tempField04, "buildPosition": tempField05, "ttRegId": tempField06];
                    if (StringUtil.isEqual(whichOne, "build")) {
                        for (int j = 0; j < selectConditionEnName.length; j++) {
                            //获取传递条件，查询数据
                            String tempField = queryResult.getStrValue("PageData.Row[${i}].${selectConditionEnName[j]}");
                            String resul = selectConditionEnName[j];
                            temp.put(resul, tempField);
                        }
                    }
                    buildResult.add(temp);
                }
            }
            return buildResult;
        }
    }

    /** 获取房屋地图数据 */
    private List getHouseData(HttpServletRequest request, String[] bboxTemps, Map conditionMap) {
        SvcRequest svcReqFilt = RequestUtil.getSvcRequest(request);
        //根据 系统配置查询 搜索条件.
        def selectConditionEnName = (String[]) conditionMap.get("selectConditionEnName");
        def selectConditionChName = (String[]) conditionMap.get("selectConditionChName");
        def selectConditionParam = (String[]) conditionMap.get("selectConditionParam");
        // 高级查询条件
        RequestUtil requestUtil = new RequestUtil(request);
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String isPos = requestUtil.getStrParam("isPos");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

        //搜索条件类型
        String whichOne = request.getParameter("whichOne");

        //查询
        XmlBean opDataHs = new XmlBean();
        String prePath = "OpData.";
        int conNum = 0;
        opDataHs.setStrValue(prePath + "entityName", "HouseInfo");
        //判断查询 维度，是建筑，还是房屋。
        //获取 自定义查询条件 维度
        //当前查询维度标志，并获取码值供下方回去状态予之对应颜色使用
//        String showDimensionMark = request.getParameter("showDimensionMark");
//        String showDimensionMarkParam = "";
        if (StringUtil.isEqual(whichOne, "house")) {
            for (int i = 0; i < selectConditionEnName.length; i++) {
                String dimension = selectConditionEnName[i];
                String dimensionVal = request.getParameter(dimension);
                //获取传递条件，查询数据
                if (dimensionVal != null) {
                    if (dimension.indexOf(".") == -1 || !dimension.contains("HouseInfo.")) {
                        opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "HouseInfo.${dimension}");
                    } else {
                        opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "${dimension}");
                    }
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldValue", "(${dimensionVal})");
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].operation", "IN");
                }
            }
            // 定位的时候用于获取检索的房屋列表
            if ("true" == isPos) {
                String inputCondition = request.getParameter("inputCondition");
                String inputConditionValue = request.getParameter("inputConditionValue");
                if (StringUtil.isNotEmptyOrNull(inputCondition) && !StringUtil.isEqual("undefined", inputCondition)) {
                    if (inputCondition.indexOf(".") == -1) {
                        opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "HouseInfo.${inputCondition}");
                    } else {
                        opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "${inputCondition}");
                    }
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldValue", "%${inputConditionValue}%");
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].operation", "like");
                }
            }
            /** 获取权限资源内的条件 */
            int prjCd = requestUtil.getIntParam("prjCd");
            conNum = addRhtCondition(opDataHs, prjCd, request, conNum);

            String regOrgIdValue = "";
            String rhtRegIdValue = "";
            // 高级查询增加的查询条件
            for (int i = 0; i < conditionFieldArr.length; i++) {
                if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                        || StringUtil.isEmptyOrNull(conditionArr[i])
                        || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                    continue;
                }
                // 管理组织部作为直接查询条件
                if ("HouseInfo.ttOrgId".equals(conditionFieldArr[i])) {
                    regOrgIdValue = conditionValueArr[i];
                    continue;
                }
                // 管理区域与不作为自己查询条件
                if ("HouseInfo.ttRegId".equals(conditionFieldArr[i])) {
                    rhtRegIdValue = conditionValueArr[i];
                    continue;
                }

                opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", conditionFieldArr[i]);
                opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].operation", conditionArr[i]);
                if ("like".equalsIgnoreCase(conditionArr[i])) {
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].fieldValue", "%" + conditionValueArr[i] + "%");
                } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                    String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].fieldValue", "(" + usedValue + ")");
                }  else if ("REGEXP".equalsIgnoreCase(conditionArr[i])) {// 问题树正则匹配  正则表达式  '(,100101)|(^100101)'
                    //处理多条件
                    String[] rLikeFieldValueArr = conditionValueArr[i].split("\\|");
                    for (int j = 0; j<rLikeFieldValueArr.length; j++) {
                        opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", conditionFieldArr[i]);
                        opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].operation", conditionArr[i]);
                        opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].fieldValue", "(," + rLikeFieldValueArr[j] + ")|(^" + rLikeFieldValueArr[j] + ")");
                    }
                } else {
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].fieldValue", conditionValueArr[i]);
                }
            }

            /* 组织和区域权限过滤特殊处理 */
            // 过滤的组织 字段名
            opDataHs.setStrValue(prePath + "OrgFilter.attrName", "HouseInfo.ttOrgId");
            opDataHs.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
            opDataHs.setStrValue(prePath + "OrgFilter.rhtType", "1");
            // 过滤的区域 字段名
            if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                opDataHs.setStrValue(prePath + "RegFilter.attrName", "HouseInfo.ttRegId");
                opDataHs.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                opDataHs.setStrValue(prePath + "RegFilter.regUseType", "1");
            }
        }

        // 设置分页
        opDataHs.setStrValue(prePath + "PageInfo.pageSize", "5000");
        /*需要查询的字段*/
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsFullAddr");
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsStatus");
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.hsRoomPos");
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.hsSlfRoomPos");
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.hsOwnerPersons");
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsPosX");
        opDataHs.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.hsPosY");
        //拼接动态结果字段
        if (StringUtil.isEqual(whichOne, "house")) {
            for (int i = 0; i < selectConditionEnName.length; i++) {
                //获取传递条件，查询数据
                opDataHs.setStrValue(prePath + "ResultFields.fieldName[${8 + i}]", "HouseInfo.${selectConditionEnName[i]}");
            }
        }

        opDataHs.setStrValue(prePath + "opName", "QUERY_MORE_ENTITY_CMPT");
        svcReqFilt.addOp("PRIVI_FILTER", opDataHs);
        SvcResponse svcResponse = query(svcReqFilt);
        List<Map<String, String>> hsResultMapList = new ArrayList<Map<String, String>>();
        if (svcResponse.isSuccess()) {
            XmlBean hsResult = svcResponse.getFirstOpRsp("PRIVI_FILTER").getBeanByPath("Operation.OpResult.PageData");
            if (hsResult != null) {
                int rowCount = hsResult.getListNum("PageData.Row");
                for (int j = 0; j < rowCount; j++) {
                    String tempField01 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsId");
                    String tempField02 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsFullAddr");
                    String tempField03 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsStatus");
                    String tempField04 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsRoomPos");
                    String tempField05 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsSlfRoomPos");
                    String tempField06 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsOwnerPersons");
                    String tempField07 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsPosX");
                    String tempField08 = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.hsPosY");

                    Map<String, String> temp = ["hsId"          : tempField01, "hsFullAddr": tempField02, "hsStatus": tempField03,
                                                "hsRoomPos"     : tempField04, "hsSlfRoomPos": tempField05,
                                                "hsOwnerPersons": tempField06, "hsPosX": tempField07, "hsPosY": tempField08];
                    //自定义条件查询 结果推送多维度展示 区分状态颜色
                    if (StringUtil.isEqual(whichOne, "house")) {
                        for (int i = 0; i < selectConditionEnName.length; i++) {
                            //获取传递条件，查询数据
                            String tempField = hsResult.getStrValue("PageData.Row[${j}].HouseInfo.${selectConditionEnName[i]}");
                            String resul = selectConditionEnName[i];
                            if (resul.indexOf(".") > 0) {
                                resul = resul.substring(resul.indexOf(".") + 1, resul.length());
                            }
                            temp.put(resul, tempField);
                        }
                    }
                    hsResultMapList.add(temp);
                }
            }
        }
        return hsResultMapList;
    }

    /**
     * 拼接权限资源的条件
     *
     * @param opData
     * @param prjCd
     */
    public int addRhtCondition(XmlBean opData, int prjCd, HttpServletRequest request, int index) {
        String rhtType = "1";
        String rhtId = "ph005-showAllMap";
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtId", rhtId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            String path = "Response.SvcCont.RhtTree.Node.rhtAttr02";
            String note = svcResponse.getStrValue(path);
            if (StringUtil.isNotEmptyOrNull(note)) {
                JSONObject noteObj = JSON.parseObject(note);
                String fieldName = noteObj.getString("conditionNames");
                String fieldValue = noteObj.getString("conditionValues");
                String operation = noteObj.getString("conditions");
                List<String> fieldNames = Arrays.asList(fieldName.split(","));
                List<String> fieldValues = Arrays.asList(fieldValue.split(","));
                List<String> operations = Arrays.asList(operation.split(","));
                List<Integer> nums = new ArrayList<Integer>();
                nums.add(fieldNames.size());
                nums.add(fieldValues.size());
                nums.add(operations.size());
                int smallNum = Collections.min(nums);
                for (int i = 0; i < smallNum; i++) {
                    opData.setStrValue("OpData.Conditions.Condition[" + index + "].fieldName", fieldNames.get(i));
                    opData.setStrValue("OpData.Conditions.Condition[" + index + "].fieldValue", fieldValues.get(i));
                    opData.setStrValue("OpData.Conditions.Condition[" + index + "].operation", operations.get(i));
                    index++;
                }

            }
        }
        return index;
    }

    /** 获取监控地图数据 */
    private List getCameraData(HttpServletRequest request) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean camOpData = new XmlBean();
        String prePath = "OpData.";
        camOpData.setStrValue(prePath + "entityName", "CameraMngInfo");
        camOpData.setStrValue(prePath + "ResultFields.fieldName[0]", "camId");
        camOpData.setStrValue(prePath + "ResultFields.fieldName[1]", "camCd");
        camOpData.setStrValue(prePath + "ResultFields.fieldName[2]", "channelCd");
        camOpData.setStrValue(prePath + "ResultFields.fieldName[3]", "camAddr");
        camOpData.setStrValue(prePath + "ResultFields.fieldName[4]", "accessAddr");
        camOpData.setStrValue(prePath + "ResultFields.fieldName[5]", "markPos");
        svcRequest.addOp("QUERY_ENTITY_CMPT", camOpData);

        SvcResponse svcResponse = query(svcRequest);
        List<Map<String, String>> camResultList = new ArrayList<Map<String, String>>();
        if (svcResponse.isSuccess()) {
            XmlBean camResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (camResult != null) {
                int rowNum = camResult.getListNum("PageData.Row");
                for (int i = 0; i < rowNum; i++) {
                    String camId = camResult.getStrValue("PageData.Row[${i}].camId");
                    String camCd = camResult.getStrValue("PageData.Row[${i}].camCd");
                    String channelCd = camResult.getStrValue("PageData.Row[${i}].channelCd");
                    String camAddr = camResult.getStrValue("PageData.Row[${i}].camAddr");
                    String accessAddr = camResult.getStrValue("PageData.Row[${i}].accessAddr");
                    String markPos = camResult.getStrValue("PageData.Row[${i}].markPos");
                    Map<String, String> temp = ["camId": camId, "camCd": camCd, "channelCd": channelCd, "camAddr": camAddr, "accessAddr": accessAddr, "markPos": markPos];
                    camResultList.add(temp);
                }
            }
        }
        return camResultList;
    }

    /**
     * 点击地图节点弹框效果优化
     * @param request
     * @param response
     * @return
     */
    public ModelAndView findPopupData(HttpServletRequest request, HttpServletResponse response) {
        //调用服务查询 院落 房产 状态对应的颜色信息
        SvcRequest svcReqFilt = RequestUtil.getSvcRequest(request);
        String clickId = request.getParameter("clickId");
        String clickType = request.getParameter("clickType");
        String prjCd = request.getParameter("prjCd");
        String entityName = "";
        String entityKey = "";
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", prjCd);
        modelMap.put("clickId", clickId);
        // 处理结果，返回弹框页面。要修改弹框页面内容只需要修改一下两个页面 ph005_popup_hs  ph005_popup_bld
        String[] resultFields = [];
        String returnPage = "";

        //获取系统通用表单编码
        Map<String, String> map = getCfgCollection(request, "PRJ_FORM",
                true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String prjFormCd = map.get("mapPopForm");
        if (StringUtil.isNotEmptyOrNull(prjFormCd)) {
            if (StringUtil.isEqual(clickType, "house")) {
                entityName = "HouseInfo";
                entityKey = "hsId"
            } else if (StringUtil.isEqual(clickType, "build")) {
                entityName = "BuildInfo";
                entityKey = "buildId";
            } else if (StringUtil.isEqual(clickType, "camera")) {
                entityName = "CameraMngInfo";
                entityKey = "camId";
            }
            //查询指定房产的 交房信息
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            //调用 获取表单 信息 返回页面
            XmlBean paramData = new XmlBean();
            // 生成报表模板
            paramData.setStrValue("OpData.formCd", prjFormCd);
            // 生成报表参数
            paramData.setStrValue("OpData.Report.Parameter[0].entityName", entityName);
            int addHouseInfoCount = 0;
            //
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount}].attrName", entityKey);
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount++}].value", clickId);
            //
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount}].attrName", "clickType");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount++}].value", clickType);

            // 增加到请求参数,调用服务
            svcRequest.addOp("GENERATE_REPORT", paramData);
            // 调用服务，执行数据查询
            SvcResponse pageResponse = query(svcRequest);
            if (pageResponse.isSuccess()) {
                XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
                String pageStr = opResult.getStrValue("Operation.OpResult.resultParam");
                String operDefCode = opResult.getStrValue("Operation.OpResult.OperationDefs.operDefCode");
                //返回展示页面字符串
                modelMap.put("article", pageStr);
                modelMap.put("operDefCode", operDefCode);
                modelMap.put("clickType", clickType);
                // 是否包含界面框架
                return new ModelAndView("/eland/ph/ph005/ph005_popup_common", modelMap);
            }
        } else {
            // 没有指定表单读取固定的页面
            if (StringUtil.isEqual(clickType, "house")) {
                entityName = "HouseInfo";
                entityKey = "hsId";
                resultFields = ["hsId", "hsOwnerPersons", "hsFullAddr", "hsOwnerType", "hsStatus", "hsBuildSize"];
                returnPage = "/eland/ph/ph005/ph005_popup_hs";
            } else if (StringUtil.isEqual(clickType, "build")) {
                entityName = "BuildInfo";
                entityKey = "buildId";
                resultFields = ["buildId", "buildFullAddr", "ttRegId", "buildType", "buildStatus", "buildLandSize"];
                returnPage = "/eland/ph/ph005/ph005_popup_bld";
            } else if (StringUtil.isEqual(clickType, "camera")) {
                entityName = "CameraMngInfo";
                entityKey = "camId";
                resultFields = ["camId", "camCd", "channelCd", "camAddr", "accessAddr", "markPos"];
                returnPage = "/eland/ph/ph005/ph005_popup_cam";
            }
            XmlBean opData = new XmlBean();
            // 获取上级区域编号
            opData.setStrValue("OpData.entityName", entityName);
            /* 增加查询条件 */
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", entityKey);
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", clickId);
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            /* 需要获取的字段*/
            for (int i = 0; i < resultFields.length; i++) {
                opData.setStrValue("OpData.ResultFields.fieldName[${i}]", resultFields[i]);
            }
            svcReqFilt.addOp("QUERY_ENTITY_CMPT", opData);
            SvcResponse svcResponse = query(svcReqFilt);

            boolean result = svcResponse.isSuccess();
            String errMsg = svcResponse.getErrMsg();
            String jsonStr;
            if (result) {
                XmlBean resultDataBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                modelMap.put("selectData", resultDataBean.getRootNode());
            }
            return new ModelAndView(returnPage, modelMap);
        }
    }

    /** 根据不同条件推送不同查询条件页面   */
    public ModelAndView searchPage(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String whichOne = request.getParameter("whichOne");
        int prjCd = NumberUtil.getIntFromObj(request.getParameter("prjCd"));
        //调用方法查询 配置参数
        Map conditionMap = queryConditionCfg(request, whichOne);
        def selectConditionEnName = (String[]) conditionMap.get("selectConditionEnName");
        def selectConditionChName = (String[]) conditionMap.get("selectConditionChName");
        def selectConditionParam = (String[]) conditionMap.get("selectConditionParam");
        List<Map> conditionList = new ArrayList();
        Map<String, String> mapColors = getCfgCollection(request, "MAP_COLORS", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        int colorIndex = 1;
        for (int i = 0; i < selectConditionEnName.length; i++) {
            Map testMap = new HashMap();
            testMap.put("dimension", selectConditionChName[i]);
            testMap.put("inputName", selectConditionEnName[i]);
            testMap.put("paramName", getCfgCollection(request, "${selectConditionParam[i]}", true, prjCd));
            if (colorIndex > mapColors.size()) {
                colorIndex = 1;
            }
            String colors = mapColors.get(String.valueOf(colorIndex++));
            if (StringUtil.isEmptyOrNull(colors)) {
                colors = "#ccffff,#ff0000,#ffff00, #ffcc00, #ff6600, #ff66ff, #ff00ff, #66ff00, #66ffff";
            }
            colors = colors.replaceAll("\\s*", "");
            testMap.put("color", colors.split(","));
            conditionList.add(testMap);
        }
        modelMap.put("list", conditionList);
        String toPage = "/eland/ph/ph005/ph005_empty";
        if (StringUtil.isEqual(whichOne, "build")) {
            toPage = "/eland/ph/ph005/ph005_search_bld";
        } else if (StringUtil.isEqual(whichOne, "house")) {
            /** 获取权限编码ID,读取配置初始化查询条件 */
            String privilegeId = request.getParameter("privilegeId");
            if(StringUtil.isEmptyOrNull(privilegeId)) {
                privilegeId = "ph005-showAllMap"
            }
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
                    if (StringUtil.isNotEmptyOrNull(conditions)) {
                        // 配置的Json字符串转换为XMLBean
                        todoBean = XmlBean.jsonStr2Xml(conditions, "Condition");
                        //获取json串里的参数；
                    }
                    modelMap.put("conditions", todoBean.getRootNode());
                }
            }
            toPage = "/eland/ph/ph005/ph005_search_hs";
        }
        return new ModelAndView(toPage, modelMap);
    }

    /** 地图全屏展示 */
    public ModelAndView screenFull(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ph/ph005/ph00501_full", modelMap);
    }

    /**
     * 根据搜索条件配置类型, 动态查询搜索页面 配置选择条件
     * @param request
     * @param whichOne 搜索条件类型
     * @return
     */
    private Map queryConditionCfg(HttpServletRequest request, String whichOne) {
        Map resultMap = new HashMap();
        //将查询参数提取到 参数列表里配置 //地图条件 查询 自由配置参数 显示汉字、数据库实体字段、码表值，
        def selectConditionEnName = [] as String[];
        def selectConditionChName = [] as String[];
        def selectConditionParam = [] as String[];
        String prjCd = request.getParameter("prjCd");
        /**
         *  查询所有的配置参数。图层地址，以及图层名称
         */
        SvcRequest svcReqCfg = RequestUtil.getSvcRequest(request);
        XmlBean reqDataCfg = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.";
        //根据查询 实体 不同 查询不同的参数配置，  建筑 or 房产
        if (StringUtil.isEqual(whichOne, "build")) {
            reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "MAP_BD_PARAM");
        } else if (StringUtil.isEqual(whichOne, "house")) {
            reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "MAP_CONT_PARAM");
        }
        reqDataCfg.setValue(nodePath + "SysCfg[0].isCached", "true");
        reqDataCfg.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
        svcReqCfg.setReqData(reqDataCfg);
        // 调用服务
        SvcResponse svcRspCfg = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcReqCfg);
        if (svcRspCfg.isSuccess()) {
            XmlBean cfgBean = svcRspCfg.getRspData().getBeanByPath("SvcCont.SysCfgs");
            //获取配置参数自定义查询条件 数组
            int selectNum = svcRspCfg.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            List list1 = new ArrayList();
            List list2 = new ArrayList();
            List list3 = new ArrayList();
            for (int i = 0; i < selectNum; i++) {
                //获取该配置参数的 key ， value
                String valueCd = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueCd");
                String valueName = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueName");
                String notes = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].notes");
                list1.add(valueCd);
                list2.add(valueName);
                list3.add(notes);
            }
            selectConditionEnName = list1.toArray();
            selectConditionChName = list2.toArray();
            selectConditionParam = list3.toArray();
        }

        //自定义查询条件，多维度推送
        if (selectConditionEnName.length == 0) {
            if (StringUtil.isEqual(whichOne, "house")) {
                // 默认条件设置  如果查询结果为空。
                selectConditionEnName = ["hsOwnerType", "HsCtInfo.ctStatus", "HsCtInfo.chHsStatus"] as String[];
                selectConditionChName = ["房屋产权类型", "签约进度", "选房进度"] as String[];
                selectConditionParam = ["HS_OWNER_TYPE", "CONTRACT_STATUS", "CHOOSE_STATUS"] as String[];
            } else if (StringUtil.isEqual(whichOne, "build")) {
                selectConditionEnName = ["buildType", "buildStatus"] as String[];
                selectConditionChName = ["院落类型", "院落状态"] as String[];
                selectConditionParam = ["BUILDING_TYPE", "YARD_STATUS"] as String[];
            }
        }
        resultMap.put("selectConditionEnName", selectConditionEnName);
        resultMap.put("selectConditionChName", selectConditionChName);
        resultMap.put("selectConditionParam", selectConditionParam);
        return resultMap;
    }
}
