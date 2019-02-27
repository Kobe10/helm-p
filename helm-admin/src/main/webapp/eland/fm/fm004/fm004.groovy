import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
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
 * Created by HuberyYan on 2016/7/5.
 * 人地房  项目地图展示
 */
class pj016 extends GroovyController {
    private static final String SCHEME_TYPE = "100";

    /** 设置随机颜色   多维度展示颜色推送。*/
    def colors = ["#ccffff", "#ff0000", "#ffff00", "#ffcc00", "#ff6600", "#ff66ff", "#ff00ff", "#66ff00", "#66ffff", "#663399", "#0066ff", "#FFB90F", "#FFBBFF", "#FF6347", "#FF00FF", "#9AFF9A", "#ADFF2F", "#87CEFF", "#7FFF00", "#7CCD7C", "#4EEE94", "#0000FF", "#00868B", "#68228B", "#8B4789", "#8B2252", "#9932CC", "#ffff00", "#ffffff", "#ff6600", "#ff66ff", "#ff0000", "#ff00ff", "#66ff00", "#66ffff", "#663399", "#0066ff", "#FFB90F", "#FFBBFF", "#FF6347", "#FF00FF", "#9AFF9A", "#ADFF2F", "#87CEFF", "#7FFF00", "#7CCD7C", "#4EEE94", "#0000FF", "#00868B", "#68228B", "#8B4789", "#8B2252", "#9932CC"] as String[];

    /** 展示所有状态地图 */
    public ModelAndView showAllMap(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        String lon = request.getParameter("lon");
        String lat = request.getParameter("lat");
        String level = request.getParameter("level");

        /**
         *  查询所有的配置参数。图层地址，以及图层名称
         */
        SvcRequest svcReqCfg = RequestUtil.getSvcRequest(request);
        XmlBean reqDataCfg = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.";
        reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "geoserver_map");
        reqDataCfg.setValue(nodePath + "SysCfg[0].isCached", "true");
        reqDataCfg.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
        svcReqCfg.setReqData(reqDataCfg);
        // 调用服务
        SvcResponse svcRspCfg = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcReqCfg);
        if (svcRspCfg.isSuccess()) {
            XmlBean cfgBean = svcRspCfg.getRspData().getBeanByPath("SvcCont.SysCfgs");
            int cfgValueNum = svcRspCfg.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            String baselayerUrl = "";
            String layerName1 = "";
            String layerName2 = "";
            String lonLatLevel = "";
            String mapBounds = "";
            for (int i = 0; i < cfgValueNum; i++) {
                //获取该配置参数的 key ， value
                String val = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueCd");
                String relValue = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueName");
                if (StringUtil.isEqual("MAP_LAYER_URL", val)) {
                    baselayerUrl = relValue;
                } else if (StringUtil.isEqual("MAP_LAYER_NAME1", val)) {
                    layerName1 = relValue;
                } else if (StringUtil.isEqual("MAP_LAYER_NAME2", val)) {
                    layerName2 = relValue;
                } else if (StringUtil.isEqual("MAP_LON_LAT_LEVEL", val)) {
                    lonLatLevel = relValue;
                } else if (StringUtil.isEqual("MAP_BOUNDS", val)) {
                    mapBounds = relValue;
                }
            }
            //查询原有地图坐标，返回页面绘制地图
            modelMap.put("baselayerUrl", baselayerUrl);
            modelMap.put("layerName1", layerName1);
            modelMap.put("layerName2", layerName2);
            modelMap.put("lonLatLevel", lonLatLevel);
            modelMap.put("mapBounds", mapBounds);
        }
        // 获取登录信息
        String staffCd = getStaffCd(request);//获取staffCode主键
        //获取方案信息
        SessionUtil sessionUtil = new SessionUtil(request);
        //获取方案
        Map<String, Object> mainMap = sessionUtil.getObject(staffCd);
        //方案信息
        modelMap.put("initResult", mainMap);
        //返回参数页面传递参数，设置中心
        modelMap.put("lon", lon);
        modelMap.put("lat", lat);
        modelMap.put("level", level);
        String fullFlag = request.getParameter("fullFlag");
        modelMap.put("fullFlag", fullFlag);
        return new ModelAndView("/eland/fm/fm004/fm004", modelMap);
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
            //TODO：修改地图范围，从码表获取
            bboxTemps = [1353, 600, 1475, 647];
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
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "buildPositionX");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", bboxTemps[0]);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", ">");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "buildPositionX");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", bboxTemps[2]);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "<");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "buildPositionY");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", bboxTemps[1]);
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", ">");
        opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "buildPositionY");
        opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", bboxTemps[3]);
        opData.setStrValue(prePath + "Conditions.Condition[3].operation", "<");
        //判断查询 维度，是建筑，还是房屋。
        //获取 自定义查询条件 维度
        //当前查询维度标志，并获取码值供下方获取状态予之对应颜色使用
//        String showDimensionMark = request.getParameter("showDimensionMark");
//        String showDimensionMarkParam = "";
        if (StringUtil.isEqual(whichOne, "build")) {
            int conNum = 4;
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
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

        //搜索条件类型
        String whichOne = request.getParameter("whichOne");

        //查询
        XmlBean opDataHs = new XmlBean();
        String prePath = "OpData.";
        opDataHs.setStrValue(prePath + "entityName", "HouseInfo");
        opDataHs.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsPosX");
        opDataHs.setStrValue(prePath + "Conditions.Condition[0].fieldValue", bboxTemps[0]);
        opDataHs.setStrValue(prePath + "Conditions.Condition[0].operation", ">");
        opDataHs.setStrValue(prePath + "Conditions.Condition[1].fieldName", "HouseInfo.hsPosX");
        opDataHs.setStrValue(prePath + "Conditions.Condition[1].fieldValue", bboxTemps[2]);
        opDataHs.setStrValue(prePath + "Conditions.Condition[1].operation", "<");
        opDataHs.setStrValue(prePath + "Conditions.Condition[2].fieldName", "HouseInfo.hsPosY");
        opDataHs.setStrValue(prePath + "Conditions.Condition[2].fieldValue", bboxTemps[1]);
        opDataHs.setStrValue(prePath + "Conditions.Condition[2].operation", ">");
        opDataHs.setStrValue(prePath + "Conditions.Condition[3].fieldName", "HouseInfo.hsPosY");
        opDataHs.setStrValue(prePath + "Conditions.Condition[3].fieldValue", bboxTemps[3]);
        opDataHs.setStrValue(prePath + "Conditions.Condition[3].operation", "<");
        //判断查询 维度，是建筑，还是房屋。
        //获取 自定义查询条件 维度
        //当前查询维度标志，并获取码值供下方回去状态予之对应颜色使用
//        String showDimensionMark = request.getParameter("showDimensionMark");
//        String showDimensionMarkParam = "";
        if (StringUtil.isEqual(whichOne, "house")) {
            int conNum = 4;
            for (int i = 0; i < selectConditionEnName.length; i++) {
                String dimension = selectConditionEnName[i];
                String dimensionVal = request.getParameter(dimension);
                //处理当前查询维度获取码值
//                if (StringUtil.isEqual(dimension, showDimensionMark)) {
//                    showDimensionMarkParam = selectConditionParam[i];
//                }
                //获取传递条件，查询数据
                if (dimensionVal != null) {
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "HouseInfo.${dimension}");
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldValue", "(${dimensionVal})");
                    opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].operation", "IN");
                }
            }
            //获取 输入的 查询条件
            String inputCondition = request.getParameter("inputCondition");
            String inputConditionValue = request.getParameter("inputConditionValue");
            if (StringUtil.isNotEmptyOrNull(inputCondition) && !StringUtil.isEqual("undefined", inputCondition)) {
                opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldName", "HouseInfo.${inputCondition}");
                opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum}].fieldValue", "%${inputConditionValue}%");
                opDataHs.setStrValue(prePath + "Conditions.Condition[${conNum++}].operation", "like");
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
        //拼接动态结果字段
        if (StringUtil.isEqual(whichOne, "house")) {
            for (int i = 0; i < selectConditionEnName.length; i++) {
                //获取传递条件，查询数据
                opDataHs.setStrValue(prePath + "ResultFields.fieldName[${6 + i}]", "HouseInfo.${selectConditionEnName[i]}");
            }
        }

        opDataHs.setStrValue(prePath + "opName", "QUERY_MORE_ENTITY_CMPT");
        svcReqFilt.addOp("PRIVI_FILTER", opDataHs);
        SvcResponse svcResponse = query(svcReqFilt);
        if (svcResponse.isSuccess()) {
            List<Map<String, String>> hsResultMapList = new ArrayList<Map<String, String>>();
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

                    Map<String, String> temp = ["hsId": tempField01, "hsFullAddr": tempField02, "hsStatus": tempField03, "hsRoomPos": tempField04, "hsSlfRoomPos": tempField05, "hsOwnerPersons": tempField06];
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
            return hsResultMapList;
        }
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

    /** 弹框查询方法 院落、房产对应状态颜色 */
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

        // 处理结果，返回弹框页面。要修改弹框页面内容只需要修改一下两个页面 fm004_popup_hs  fm004_popup_bld
        String[] resultFields = [];
        String returnPage = "";
        if (StringUtil.isEqual(clickType, "house")) {
            entityName = "HouseInfo";
            entityKey = "hsId";
            resultFields = ["hsId", "hsOwnerPersons", "hsFullAddr", "hsOwnerType", "hsStatus", "hsBuildSize"];
            returnPage = "/eland/fm/fm004/fm004_popup_hs";
        } else if (StringUtil.isEqual(clickType, "build")) {
            entityName = "BuildInfo";
            entityKey = "buildId";
            resultFields = ["buildId", "buildFullAddr", "ttRegId", "buildType", "buildStatus", "buildLandSize"];
            returnPage = "/eland/fm/fm004/fm004_popup_bld";
        } else if (StringUtil.isEqual(clickType, "camera")) {
            entityName = "CameraMngInfo";
            entityKey = "camId";
            resultFields = ["camId", "camCd", "channelCd", "camAddr", "accessAddr", "markPos"];
            returnPage = "/eland/fm/fm004/fm004_popup_cam";
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

    /** 根据不同条件推送不同查询条件页面   */
    public ModelAndView searchPage(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String whichOne = request.getParameter("whichOne");
        int prjCd = NumberUtil.getIntFromObj(request.getParameter("prjCd"));
        //调用方法查询 配置参数
        Map conditionMap = queryConditionCfg(request, whichOne);
        def selectConditionEnName = (String[]) conditionMap.get("selectConditionEnName");
        def selectConditionChName = (String[]) conditionMap.get("selectConditionChName");
        def selectConditionParam = (String[]) conditionMap.get("selectConditionParam");

        List<Map> conditionList = new ArrayList();
        for (int i = 0; i < selectConditionEnName.length; i++) {
            Map testMap = new HashMap();
            testMap.put("dimension", selectConditionChName[i]);
            testMap.put("inputName", selectConditionEnName[i]);
            testMap.put("paramName", getCfgCollection(request, "${selectConditionParam[i]}", true, prjCd));
            testMap.put("color", colors);
            conditionList.add(testMap);
        }
        modelMap.put("list", conditionList);
        String toPage = "/eland/fm/fm004/fm004_empty";
        if (StringUtil.isEqual(whichOne, "build")) {
            toPage = "/eland/fm/fm004/fm004_search_bld";
        } else if (StringUtil.isEqual(whichOne, "house")) {
            toPage = "/eland/fm/fm004/fm004_search_hs";
        }
        return new ModelAndView(toPage, modelMap);
    }

    /** 地图全屏展示 */
    public ModelAndView screenFull(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/fm/fm004/fm00401_full", modelMap);
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

    /**
     * 获取登录StaffCode作为最外层主键
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public String getStaffCd(HttpServletRequest request) {
        // 获取登录信息
        SessionUtil sessionUtil = new SessionUtil(request);
        String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
        XmlBean loginBean = new XmlBean(loginInfo);
        String staffCd = loginBean.getStrValue("LoginInfo.staffCode");//登录信息作为主键
        return staffCd;
    }

    /**
     * 查询预分方案各项合计金额
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public String queryYuFenMoney(HttpServletRequest request, HttpServletResponse response) {
        //返回json 字符串
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        boolean result = false;
        String errMsg = "";
        String schemeArray;
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");//房屋Id
        String ctType = request.getParameter("ctType");//安置方式
        String prjCd = request.getParameter("prjCd");//安置方式
        if (StringUtil.isEqual("0", ctType)) {
            ctType = "";
        }
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            //入参
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.schemeType", SCHEME_TYPE);//预分方案
            opData.setStrValue("OpData.hsId", hsId);
            opData.setStrValue("OpData.ctType", ctType);
            //调用服务
            svcRequest.addOp("GET_HS_COST_MONEY", opData);
            //启动服务
            SvcResponse svcResponse = query(svcRequest);
            result = svcResponse.isSuccess();
            errMsg = svcResponse.getErrMsg();
            if (result) {
                XmlBean resultBean = svcResponse.getFirstOpRsp("GET_HS_COST_MONEY").getBeanByPath("Operation.OpResult.RuleResult.HsBcJe");
                if (resultBean != null) {
                    Map<String, String> map = getCfgCollection(request, "CB_MONEY_DY", true, NumberUtil.getIntFromObj(prjCd));
                    int count = resultBean.getListNum("HsBcJe.Items.Item");
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String itemName = entry.getValue();
                        for (int i = 0; i < count; i++) {
                            String name = resultBean.getStrValue("HsBcJe.Items.Item[${i}].nameCh");//预分方案各项名称
                            String value = resultBean.getStrValue("HsBcJe.Items.Item[${i}].money");//预分方案各项值
                            if (StringUtil.isEqual(name, itemName)) {
                                sb.append("""{ "name":"${name}","value":"${value}" },""");
                                sb2.append(name + ":" + value + ",");
                                continue;
                            }
                        }
                    }
                    schemeArray = sb2.substring(0, sb2.length() - 1).toString();
                }
            }
        } else {
            result = false;
            errMsg = "未选择房产";
            schemeArray = "";
        }
        String jsonStr = """{"success":${result},"errMsg":"${errMsg}","schemeInfo":[${
            sb.toString()
        }],"schemeArray":"${schemeArray}" }""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 保存到session
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public void saveS(HttpServletRequest request, HttpServletResponse response) {
        // 获取登录信息
        SessionUtil sessionUtil = new SessionUtil(request);
        String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
        XmlBean loginBean = new XmlBean(loginInfo);
        String staffCd = loginBean.getStrValue("LoginInfo.staffCode");//最外层主键
        String schemeName = request.getParameter("schemeName");//主键
        String ctName = request.getParameter("ctName");//人名
        String hsId = request.getParameter("hsId");//房屋Id
        String ctType = request.getParameter("ctType");//安置方式
        String buildId = request.getParameter("buildId");//建筑Id
        String prjCd = request.getParameter("prjCd");//项目编号
        String planName = request.getParameter("planName");//方案名称
        if (StringUtil.isEmptyOrNull(hsId)) {
            String jsonStr = """{"success":"${false}","errMsg":"该方案未选择房产，请选择后再保存","schemeInfo":[],"schemeArray":"" }""";
            ResponseUtil.printAjax(response, jsonStr);
        }
        // 在Session中保存方案信息
        Map<String, String> schemeInfo = new HashMap<>();
        schemeInfo.put("hsId", hsId);
        schemeInfo.put("schemeType", SCHEME_TYPE);
        schemeInfo.put("ctType", ctType);
        schemeInfo.put("prjCd", prjCd);
        schemeInfo.put("ctName", ctName);
        schemeInfo.put("buildId", buildId);
        schemeInfo.put("planName", planName);
        Map<String, Object> map = sessionUtil.getObject(staffCd);
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(schemeName, schemeInfo);
        sessionUtil.setAttr(staffCd, map);
    }

    /**
     * 删除session
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public void deleteS(HttpServletRequest request, HttpServletResponse response) {
        // 获取登录信息
        SessionUtil sessionUtil = new SessionUtil(request);
        String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
        XmlBean loginBean = new XmlBean(loginInfo);
        String staffCd = loginBean.getStrValue("LoginInfo.staffCode");//登录信息作为主键
        String schemeName = request.getParameter("schemeName");//详细主键
        //删除
        Map<String, Object> map = sessionUtil.getObject(staffCd);
        map.remove(schemeName);
        sessionUtil.setAttr(staffCd, map);
    }

    /**
     * 点击对比，查询session
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView queryDuiBi(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取登录信息
        String staffCd = getStaffCd(request);//获取staffCode主键
        //获取方案信息
        SessionUtil sessionUtil = new SessionUtil(request);
        //获取方案
        Map<String, Object> mainMap = sessionUtil.getObject(staffCd);
        //方案信息
        modelMap.put("initResult", mainMap);
        return new ModelAndView("/eland/fm/fm004/fm004_match", modelMap);
    }

    /**
     * 根据buildId查询hsId
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView queryHsByBuild(HttpServletRequest request, HttpServletResponse response) {
        String buildId = request.getParameter("buildId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        //实体名
        opData.setStrValue("OpData.entityName", "HouseInfo");
        //条件
        opData.setStrValue("OpData.Conditions.Condition.fieldName", "buildId");
        opData.setStrValue("OpData.Conditions.Condition.fieldValue", buildId);
        opData.setStrValue("OpData.Conditions.Condition.operation", "=");
        //查询结果
        opData.setStrValue("OpData.ResultFields.fieldName", "hsId");
        opData.setStrValue("OpData.ResultFields.fieldName", "hsOwnerPersons");
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        //启动服务
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        //返回json 字符串
        StringBuilder sbId = new StringBuilder();
        StringBuilder sbName = new StringBuilder();
        String errMsg = svcResponse.getErrMsg();
        if (result) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (resultBean != null) {
                int count = resultBean.getListNum("PageData.Row");
                for (int i = 0; i < count; i++) {
                    String hsId = resultBean.getStrValue("PageData.Row[${i}].hsId");
                    String psName = resultBean.getStrValue("PageData.Row[${i}].hsOwnerPersons");
                    sbId.append(hsId + ",");
                    sbName.append(psName + ",");
                }
            }
        }
        String jsonStr = """{"success":"${result}","errMsg":"${errMsg}","hsId":"${
            sbId.substring(0, sbId.length() - 1)
        }","ctName":"${
            sbName.substring(0, sbName.length() - 1)
        }" }""";
        ResponseUtil.printAjax(response, jsonStr);
    }

}