package com.shfb.portal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shfb.oframe.core.util.common.NumberUtil;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.service.bo.SvcRequest;
import com.shfb.oframe.core.util.service.bo.SvcResponse;
import com.shfb.oframe.core.util.spring.SvcUtil;
import com.shfb.oframe.core.web.controller.GroovyController;
import com.shfb.oframe.core.web.session.SessionUtil;
import com.shfb.oframe.core.web.util.RequestUtil;
import com.shfb.oframe.core.web.util.ResponseUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 地图改造
 */
@Controller
@RequestMapping({"/eland/map"})
public class MapController extends GroovyController {

    private static final String GEOSERVER_MAP = "geoserver_map";//地图参数
    private static final String MAP_CONT_PARAM = "MAP_CONT_PARAM";//房地图检索
    private static final String MAP_LAYER_CONFIG = "MAP_LAYER_CONFIG";//图层配置参数
    private static final String MAP_COLORS = "MAP_COLORS";//地图颜色

    /**
     * 地图配置
     *
     * @param request  请求信息
     * @param response 响应信息
     * @return ModelAndView
     */
    @RequestMapping(value = "/config.gv", method = RequestMethod.POST)
    public void mapConfig(HttpServletRequest request, HttpServletResponse response) throws JspException {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int prjCd = NumberUtil.getIntFromObj(getPrjCd(request));
        svcRequest.setValue("Request.TcpCont.PrjCd", prjCd);
        /** 查询【geoserver_map】参数配置，JSON节点：wms */
        Map<String, String> geoserverMap = getCfgCollection(request, GEOSERVER_MAP, true, prjCd);
        String map_layer_url = geoserverMap.get("MAP_LAYER_URL");

        String map_bounds = geoserverMap.get("MAP_BOUNDS");
        String[] map_bounds_arr = map_bounds.replaceAll(" ", "").split(",");
        String mapBoundsStr = "[" + StringUtils.join(map_bounds_arr, ",") + "]";

        String map_lon_lat_level = geoserverMap.get("MAP_LON_LAT_LEVEL");
        String[] lon_lat_level_clear = map_lon_lat_level.replaceAll(" ", "").split(";");
        String[] lon_lat_level_arr = new String[2];
        String lonLatLevelStr = "";
        String lon = "";
        String lat = "";
        for (int i = 0; i < lon_lat_level_clear.length; i++) {
            String[] tempBounds = lon_lat_level_clear[i].split("=");
            if (StringUtil.isEqual(tempBounds[0], "lon")) {
                lon = tempBounds[1];
            } else if (StringUtil.isEqual(tempBounds[0], "lat")) {
                lat = tempBounds[1];
            }
        }
        if (StringUtil.isNotEmptyOrNull(lon) && StringUtil.isNotEmptyOrNull(lat)) {
            lon_lat_level_arr = new String[]{lon, lat};
            lonLatLevelStr = "[" + StringUtils.join(lon_lat_level_arr, ",") + "]";
        }

        String map_layer_name1 = geoserverMap.get("MAP_LAYER_NAME1");

        String map_level = geoserverMap.get("MAP_LEVEL");
        String maxLevel = "10";
        String minLevel = "1";
        String initLevel = "1";
        String[] map_level_clear = map_level.replaceAll(" ", "").split(";");
        for (int i = 0; i < map_level_clear.length; i++) {
            String[] templevel = map_level_clear[i].split(",");
            if (StringUtil.isEqual(templevel[0], "maxLevel")) {
                maxLevel = templevel[1];
            } else if (StringUtil.isEqual(templevel[0], "minLevel")) {
                minLevel = templevel[1];
            } else if (StringUtil.isEqual(templevel[0], "initLevel")) {
                initLevel = templevel[1];
            }
        }
        //地图参数配置json
        StringBuilder wmsJSON = new StringBuilder("{\"wms\":{");
        wmsJSON.append("\"serverAddr\":\"" + map_layer_url + "\",");//地图访问地址，对应参数中的：MAP_LAYER_URL
        wmsJSON.append("\"bbox\":" + mapBoundsStr + ",");//地图大小，对应参数中的：MAP_BOUNDS
        wmsJSON.append("\"center\":" + lonLatLevelStr + ",");//地图中心点，对应参数中的MAP_LON_LAT_LEVEL中定义的lon和lat
        wmsJSON.append("\"layers\":\"" + map_layer_name1 + "\",");//图层名称，对应参数中的：MAP_LAYER_NAME1
        wmsJSON.append("\"maxLevel\":\"" + maxLevel + "\",");
        wmsJSON.append("\"minLevel\":\"" + minLevel + "\",");
        wmsJSON.append("\"initLevel\":\"" + initLevel + "\"}");
        /** 查询【MAP_CONT_PARAM】参数配置 返回第一个，JSON节点：default */
        Map<String, String> mapContParam = getCfgCollection(request, MAP_CONT_PARAM, true, prjCd);
        String dimension = "";
        for (Map.Entry<String, String> entry : mapContParam.entrySet()) {
            String tempType = entry.getKey();
            if (StringUtil.isNotEmptyOrNull(tempType)) {
                String tempDimension = "HouseInfo_" + tempType;
                dimension = tempDimension.replaceAll("\\.", "_");
                break;
            }
        }
        //默认展示内容
        StringBuilder defaultJson = new StringBuilder(",\"default\":{");
        defaultJson.append("\"layers\": [\"zsHs\", \"zjHs\"],");//默认显示的图层
        defaultJson.append("\"dimension\": \"" + dimension + "\"}");//默认显示的维度
        wmsJSON.append(defaultJson);
        /** 查询参数【MAP_LAYER_CONFIG】获取配置信息，没有参数不返回图层，JSON节点：layers */
        //查询是否有查看图层的权限
        boolean haveHsRht = isHaveRht(request, "show_build_rht", prjCd);
        boolean haveJkRht = isHaveRht(request, "show_cam_rht", prjCd);
        List<Map<String, String>> mapLayerConfig = getCfgCollectionWhitNode(request, MAP_LAYER_CONFIG, true, svcRequest.getPrjCd());
        if (!mapLayerConfig.isEmpty()) {
            // 可选图层及定义信息
            StringBuilder layersJson = new StringBuilder(",\"layers\":[");
            StringBuilder mapDim = new StringBuilder();
            for (int i = 0; i < mapLayerConfig.size(); i++) {
                if (i > 0 && mapDim.length() > 0) {
                    mapDim.append(",");
                }
                boolean isAppend = false;
                String tempValueCd = mapLayerConfig.get(i).get("valueName");
                if (((StringUtil.isEqual("zsHs", tempValueCd) || StringUtil.isEqual("zjHs", tempValueCd)))) {
                    isAppend = true;
                } else if (StringUtil.isEqual("jkXx", tempValueCd)) {
                    isAppend = true;
                }
                if (isAppend) {
                    String tempJson = mapLayerConfig.get(i).get("notes");
                    mapDim.append(tempJson);
                }
            }
            layersJson.append(mapDim);
            layersJson.append("]");
            wmsJSON.append(layersJson);
        }
        /** 根据参数【MAP_CONDITION_PARAM】配置返回可选维度信息，JSON节点：dimension && JSON节点：entityCondition */
        List<Map<String, String>> mapContParamList = getCfgCollectionWhitNode(request, MAP_CONT_PARAM, true, svcRequest.getPrjCd());
        if (!mapContParamList.isEmpty()) {
            StringBuilder layersJson = new StringBuilder(",\"dimension\":[");
            StringBuilder entityConditionJson = new StringBuilder(",\"entityCondition\":[");
            for (int i = 0; i < mapContParamList.size(); i++) {
                Map<String, String> tempMap = mapContParamList.get(i);
                String valueCd = tempMap.get("valueCd").replaceAll("\\.", "_");
                String valueName = tempMap.get("valueName");
                String notes = tempMap.get("notes");
                // JSON节点：dimension
                layersJson.append("{\"dimId\":\"HouseInfo_" + valueCd + "\",\"dimName\":\"" + valueName + "\"}");
                if (i < (mapContParamList.size() - 1)) {
                    layersJson.append(",");
                } else {
                    layersJson.append("]");
                }
                /** *********** */
                // JSON节点：entityCondition
                //根据notes查询参数编码
                StringBuilder tempNodeJson = new StringBuilder("\"values\":[");
                Map<String, String> notesMap = getCfgCollection(request, notes, true, prjCd);
                int maoSize = notesMap.size();
                for (Map.Entry<String, String> entry : notesMap.entrySet()) {
                    String tempKey = entry.getKey();
                    String tempValue = entry.getValue();
                    tempNodeJson.append("{\"key\":\"" + tempKey + "\",\"values\":\"" + tempValue + "\"}");
                    maoSize--;
                    if (maoSize > 0) {
                        tempNodeJson.append(",");
                    } else {
                        tempNodeJson.append("]}");
                    }
                }
                entityConditionJson.append("{\"key\":\"HouseInfo_" + valueCd + "\",\"name\":\"" + valueName + "\",").append(tempNodeJson);
                if (i < (mapContParamList.size() - 1)) {
                    entityConditionJson.append(",");
                } else {
                    entityConditionJson.append("]");
                }
            }
            layersJson.append(entityConditionJson);
            wmsJSON.append(layersJson);
        }
        /** 固定返回内容空，原有系统没有专题 */
        wmsJSON.append(",\"businessTopic\":{}}");
        String jsonStr = "{\"success\":true, \"errMsg\":\"\", \"data\":" + wmsJSON + "}";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 地图数据
     *
     * @param request  请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    @RequestMapping(value = "/data.gv", method = RequestMethod.POST)
    public void mapData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int prjCd = NumberUtil.getIntFromObj(getPrjCd(request));
        svcRequest.setValue("Request.TcpCont.PrjCd", prjCd);
        /** 获取参数配置【MAP_COLORS】第一个颜色数组 */
        Map<String, String> mapColors = getCfgCollection(request, MAP_COLORS, true, prjCd);
        String[] mapColorsArr = new String[]{};
        for (Map.Entry<String, String> entry : mapColors.entrySet()) {
            String tempColors = entry.getValue();
            if (StringUtil.isNotEmptyOrNull(tempColors)) {
                mapColorsArr = tempColors.split(",");
                break;
            }
        }
        if (mapColorsArr.length == 0) {
            String jsonStr = "{\"success\":false, \"errMsg\":\"参数【MAP_COLORS】配置不正确\", \"data\":\"\"}";
            ResponseUtil.printAjax(response, jsonStr);
            return;
        }
        /** 获取监控图层的图片路径【MAP_LAYER_CONFIG】，iconUrl */
        List<Map<String, String>> mapLayerConfig = getCfgCollectionWhitNode(request, MAP_LAYER_CONFIG, true, svcRequest.getPrjCd());
        String iconUrl = "";
        if (!mapLayerConfig.isEmpty()) {
            // 可选图层及定义信息
            for (int i = 0; i < mapLayerConfig.size(); i++) {
                String valueName = mapLayerConfig.get(i).get("valueName");
                if (StringUtil.isEqual(valueName, "jkXx")) {
                    String tempJson = mapLayerConfig.get(i).get("notes");
                    JSONObject tempJkxx = JSON.parseObject(tempJson);
                    iconUrl = tempJkxx.getString("iconUrl");
                }
            }
        }
        /** 获取入参 */
        String jsonParam = request.getParameter("jsonParam");//获取入参JSONString
        JSONObject jsonObject = JSON.parseObject(jsonParam);//转为JSON串
        String layerIds = jsonObject.get("layerIds").toString();//需要查询的图层
        //没有图层不返回信息
        if (StringUtil.isEmptyOrNull(layerIds)) {
            String jsonStr = "{\"success\":false, \"errMsg\":\"没有查看任何图层的权限！\", \"data\":\"\"}";
            ResponseUtil.printAjax(response, jsonStr);
            return;
        }
        String dimension = jsonObject.get("dimension").toString();//当前展示的指标维度，与接口【地图配置】输出一致，根据这个获取业务数据；
        //展示维度信息，转为【MAP_CONT_PARAM】对应valueCd
        String tempValueCd = dimension.replaceAll("HouseInfo_", "").replaceAll("_", "\\.");
        //展示维度名称
        String tempValueName = "";
        //根据valueCd查询展示维度，对应的码表
        List<Map<String, String>> mapContParamList = getCfgCollectionWhitNode(request, MAP_CONT_PARAM, true, svcRequest.getPrjCd());
        //根据 参数编码 获取码值，并生成 valueCd = 码值,valueName = 名称,color = 颜色
        List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        //展示维度对应码表，生成一个Map<valueCd,count>,用来在循环房屋时计数
        Map<String, Integer> countMap = new HashMap<String, Integer>();
        for (int i = 0; i < mapContParamList.size(); i++) {
            Map<String, String> tempMap = mapContParamList.get(i);
            if (StringUtil.isEqual(tempMap.get("valueCd"), tempValueCd)) {
                tempValueName = tempMap.get("valueName");//参数名称
                String itemCd = tempMap.get("notes");//参数编码
                //List<Map<String,String>>填充值
                Map<String, String> itemCdMap = getCfgCollection(request, itemCd, true, prjCd);
                int upCount = 0;
                for (Map.Entry<String, String> entry : itemCdMap.entrySet()) {
                    Map<String, String> ncMap = new HashMap<String, String>();
                    ncMap.put("valueCd", entry.getKey());
                    ncMap.put("valueName", entry.getValue());
                    ncMap.put("color", mapColorsArr[upCount++]);
                    listMap.add(ncMap);
                    countMap.put(entry.getKey(), 0);
                }
            }
        }
        JSONArray conditions = jsonObject.getJSONArray("conditions");//检索条件
        JSONArray mapBound = jsonObject.getJSONArray("mapBound");//地图显示范围 "mapBound": [123, 123, 234, 234]
        //拆分X、Y坐标范围
        String[] minBound = new String[]{mapBound.get(0).toString(), mapBound.get(1).toString()};
        String[] maxBound = new String[]{mapBound.get(2).toString(), mapBound.get(3).toString()};
        double minX = NumberUtil.getDoubleFromObj(minBound[0]);
        double minY = NumberUtil.getDoubleFromObj(minBound[1]);
        double maxX = NumberUtil.getDoubleFromObj(maxBound[0]);
        double maxY = NumberUtil.getDoubleFromObj(maxBound[1]);

        XmlBean opData = new XmlBean();
        /** 查询房屋信息 */
        opData.setStrValue("OpData.entityName", "HouseInfo");
        //1.查询条件  中心点坐标在范围内(拼接固定查询条件)
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "HouseInfo.hsPosX");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", maxX);
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "<=");
        opData.setStrValue("OpData.Conditions.Condition[1].fieldName", "HouseInfo.hsPosX");
        opData.setStrValue("OpData.Conditions.Condition[1].fieldValue", minX);
        opData.setStrValue("OpData.Conditions.Condition[1].operation", ">=");
        opData.setStrValue("OpData.Conditions.Condition[2].fieldName", "HouseInfo.hsPosY");
        opData.setStrValue("OpData.Conditions.Condition[2].fieldValue", maxY);
        opData.setStrValue("OpData.Conditions.Condition[2].operation", "<=");
        opData.setStrValue("OpData.Conditions.Condition[3].fieldName", "HouseInfo.hsPosY");
        opData.setStrValue("OpData.Conditions.Condition[3].fieldValue", minY);
        opData.setStrValue("OpData.Conditions.Condition[3].operation", ">=");
        //2.拼接权限资源的条件
        int index = 4;
        index = addRhtCondition(opData, prjCd, request, index);
        //3.添加入参条件
        if (!conditions.isEmpty()) {
            Map<String, List<String>> conMap = new HashMap<String, List<String>>();
            for (int i = 0; i < conditions.size(); i++) {
                JSONObject tempJO = JSON.parseObject(conditions.get(i).toString());
                String tempKey = tempJO.getString("key");
                if (tempKey == null) {
                    continue;
                }
                String fieldValue = tempJO.get("value").toString();
                String fieldName = tempKey.replaceAll("_", "\\.");
                if (conMap.containsKey(fieldName)) {
                    List<String> valueList = conMap.get(fieldName);
                    if (!valueList.contains(fieldValue)) {
                        valueList.add(fieldValue);
                    }
                } else {
                    List<String> valueList = new ArrayList<String>();
                    valueList.add(fieldValue);
                    conMap.put(fieldName, valueList);
                }
            }
            for (Map.Entry<String, List<String>> entry : conMap.entrySet()) {
                String fieldName = entry.getKey();
                List<String> fieldValue = entry.getValue();
                if (fieldValue.size() == 1) {
                    opData.setStrValue("OpData.Conditions.Condition[" + (index) + "].fieldName", fieldName);
                    opData.setStrValue("OpData.Conditions.Condition[" + (index) + "].fieldValue", fieldValue.get(0));
                    opData.setStrValue("OpData.Conditions.Condition[" + (index++) + "].operation", "=");
                } else if (fieldValue.size() > 1) {
                    opData.setStrValue("OpData.Conditions.Condition[" + (index) + "].fieldName", fieldName);
                    opData.setStrValue("OpData.Conditions.Condition[" + (index) + "].fieldValue", "(" + StringUtils.join(fieldValue, ",") + ")");
                    opData.setStrValue("OpData.Conditions.Condition[" + (index++) + "].operation", "in");
                }
            }
        }
        //4.添加组织过滤
        opData.setStrValue("OpData.OrgFilter.attrName", "HouseInfo.ttOrgId");
        opData.setStrValue("OpData.OrgFilter.attrValue", "");
        opData.setStrValue("OpData.OrgFilter.rhtType", "1");
        //查询结果
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo.hsId");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue("OpData.ResultFields.fieldName[2]", "HouseInfo.hsRoomPos");
        opData.setStrValue("OpData.ResultFields.fieldName[3]", "HouseInfo.hsSlfRoomPos");
        opData.setStrValue("OpData.ResultFields.fieldName[4]", "HouseInfo.hsPosX");
        opData.setStrValue("OpData.ResultFields.fieldName[5]", "HouseInfo.hsPosY");
        opData.setStrValue("OpData.ResultFields.fieldName[6]", dimension.replaceAll("_", "\\."));//当前展示维度结果
        opData.setStrValue("OpData.opName", "QUERY_MORE_ENTITY_CMPT");
        svcRequest.addOp("PRIVI_FILTER", opData);
        /** 查询监控信息 */
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CameraMngInfo");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "camId");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "markPos");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        //调用服务
        SvcResponse svcResponse = query(svcRequest);
        boolean isSuccess = svcResponse.isSuccess();
        StringBuilder resultJSON = new StringBuilder("{\"layers\":[");//最终返回的JSON串
        StringBuilder layersJSON = new StringBuilder();//layers节点内的内容
        StringBuilder summaryJSON = new StringBuilder("\"summary\":{");//summary节点,统计地图范围内的维度汇总数据
        summaryJSON.append("\"title\":\"" + tempValueName + "\",");
        summaryJSON.append("\"data\":[");
        if (isSuccess) {
            XmlBean hsData = svcResponse.getFirstOpRsp("PRIVI_FILTER")
                    .getBeanByPath("Operation.OpResult.PageData");
            XmlBean jkData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            String[] layerIdsArr = layerIds.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            //循环展示图层，拼接对应图层json
            for (int i = 0; i < layerIdsArr.length; i++) {
                String layerId = layerIdsArr[i];
                //房屋和监控节点不同，分开拼
                if (!StringUtil.isEqual(layerId, "jkXx") && hsData != null) {
                    int hsCount = hsData.getListNum("PageData.Row");
                    //拼接固定值的节点
                    layersJSON.append("{\"layerId\":\"" + layerId + "\",");
                    if (StringUtil.isEqual(layerId, "zsHs")) {
                        layersJSON.append("\"layerName\":\"正式房\",");
                    } else {
                        layersJSON.append("\"layerName\":\"自建房\",");
                    }
                    layersJSON.append("\"fillType\":\"fill\",");
                    //循环房屋，拼接房屋坐标信息
                    StringBuilder hsDataJSON = new StringBuilder("\"data\":[");
                    Integer totalNum = 0;
                    if (hsCount > 0) {
                        for (int j = 0; j < hsCount; j++) {
                            if (j > 0) {
                                hsDataJSON.append(",");
                            }
                            String hsId = hsData.getStrValue("PageData.Row[" + j + "].HouseInfo.hsId");
                            String hsOwnerPersons = hsData.getStrValue("PageData.Row[" + j + "].HouseInfo.hsOwnerPersons");
                            String hsPosX = hsData.getStrValue("PageData.Row[" + j + "].HouseInfo.hsPosX");
                            String hsPosY = hsData.getStrValue("PageData.Row[" + j + "].HouseInfo.hsPosY");
                            String dimensionParam = hsData.getStrValue("PageData.Row[" + j + "]." + dimension.replaceAll("_", "\\.") + "");
                            hsDataJSON.append("{\"text\":\"" + hsOwnerPersons + "\",");
                            String posStr = "[]";//房屋多边形坐标数组
                            String posArr;
                            if (StringUtil.isEqual(layerId, "zsHs")) {
                                posArr = hsData.getStrValue("PageData.Row[" + j + "].HouseInfo.hsRoomPos");//正式房
                            } else {
                                posArr = hsData.getStrValue("PageData.Row[" + j + "].HouseInfo.hsSlfRoomPos");//自建房
                            }
                            if (!posArr.isEmpty()) {
                                posStr = "[" + posArr.replaceAll("POINT\\(", "[").replaceAll("\\)", "]").replaceAll(" ", ",").replaceAll("\\|", "],[") + "]";
                            }
                            hsDataJSON.append("\"path\":[" + posStr + "],");
                            String fillColor = "\"\"";
                            for (int l = 0; l < listMap.size(); l++) {
                                Map<String, String> tempColor = listMap.get(l);
                                if (dimensionParam.isEmpty()) {
                                    fillColor = "\"\"";
                                } else {
                                    if (tempColor.get("valueCd").equals(dimensionParam)) {
                                        fillColor = "\"" + tempColor.get("color") + "\"";
                                    }
                                    if (!"\"\"".equals(fillColor)) {
                                        break;
                                    }
                                }
                            }
                            hsDataJSON.append("\"fillColor\":" + fillColor + ",");
                            hsDataJSON.append("\"relId\":\"" + hsId + "\",");
                            hsDataJSON.append("\"centerPoint\":[" + hsPosX + "," + hsPosY + "]}");
                            //确保只循环一遍房屋，来控制统计数据正确性
                            if (i == 0) {
                                if (!dimensionParam.isEmpty()) {
                                    totalNum = countMap.get(dimensionParam);
                                    if (totalNum != null) {
                                        totalNum++;
                                        countMap.put(dimensionParam, totalNum);
                                    }
                                }
                            }
                        }
                        hsDataJSON.append("]");
                        layersJSON.append(hsDataJSON);
                    }
                } else if (StringUtil.isEqual(layerId, "jkXx") && jkData != null) {
                    layersJSON.append("{\"layerId\":\"" + layerId + "\",");
                    layersJSON.append("\"layerName\":\"监控\",");
                    StringBuilder jkDataJSON = new StringBuilder("\"data\":[");
                    int jkCount = jkData.getListNum("PageData.Row");
                    if (jkCount > 0) {
                        for (int j = 0; j < jkCount; j++) {
                            String camId = jkData.getStrValue("PageData.Row[" + j + "].camId");
                            String markPos = jkData.getStrValue("PageData.Row[" + j + "].markPos");
                            String[] markPosArr = markPos.split(",");
                            double markPosX = NumberUtil.getDoubleFromObj(markPosArr[0]);
                            double markPosY = NumberUtil.getDoubleFromObj(markPosArr[1]);
                            if (markPosX >= minX && markPosX <= maxX && markPosY >= minY && markPosY <= maxY) {
                                jkDataJSON.append("{\"relId\":\"" + camId + "\",\"centerPoint\":[" + markPosX + "," + markPosY + "]");
                                jkDataJSON.append(",\"iconUrl\":\"" + iconUrl + "\",\"path\":[" + markPosX + "," + markPosY + "]}");
                                if (j != (jkCount - 1)) {
                                    jkDataJSON.append(",");
                                }
                            }
                        }
                    }
                    jkDataJSON.append("]");//收尾
                    layersJSON.append(jkDataJSON);
                }
                if (layersJSON.length() > 0) {
                    if (i == (layerIdsArr.length - 1)) {
                        layersJSON.append("}");
                    } else {
                        layersJSON.append("},");
                    }
                }
            }
        }
        resultJSON.append(layersJSON);
        resultJSON.append("],");//结尾加逗号，后面增加summary节点
        //拼接summary节点内的内容
        for (int i = 0; i < listMap.size(); i++) {
            String cd = listMap.get(i).get("valueCd");
            String name = listMap.get(i).get("valueName");
            String color = listMap.get(i).get("color");
            int value = countMap.get(cd);
            summaryJSON.append("{\"value\":" + value + ",");
            summaryJSON.append("\"name\":\"" + name + "\",");
            summaryJSON.append("\"color\":\"" + color + "\"}");
            if (i != (listMap.size() - 1)) {
                summaryJSON.append(",");
            }
        }
        summaryJSON.append("]}");
        resultJSON.append(summaryJSON).append("}");
        String jsonStr = "{\"success\":true, \"errMsg\":\"\", \"data\":" + resultJSON + "}";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 拼接权限资源的条件
     *
     * @param opData
     * @param prjCd
     */
    private int addRhtCondition(XmlBean opData, int prjCd, HttpServletRequest request, int index) {
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
                if (StringUtil.isNotEmptyOrNull(fieldName) && StringUtil.isNotEmptyOrNull(fieldValue) && StringUtil.isNotEmptyOrNull(operation)) {
                    List<String> fieldNames = Arrays.asList(fieldName.split(","));
                    List<String> fieldValues = Arrays.asList(fieldValue.split(","));
                    List<String> operations = Arrays.asList(operation.split(","));
                    List<Integer> nums = new ArrayList<Integer>();
                    nums.add(fieldNames.size());
                    nums.add(fieldValues.size());
                    nums.add(operations.size());
                    int smallNum = Collections.min(nums);
                    for (int i = 0; i < smallNum; i++) {
                        String tempFN = fieldNames.get(i);
                        String tempFV = fieldValues.get(i);
                        String tempOpt = operations.get(i);
                        if (StringUtil.isEqual("in", tempOpt.toLowerCase())) {
                            tempFV = tempFV.replace("|", ",");
                            tempFV = "(" + tempFV + ")";
                        }
                        opData.setStrValue("OpData.Conditions.Condition[" + index + "].fieldName", tempFN);
                        opData.setStrValue("OpData.Conditions.Condition[" + index + "].operation", tempOpt);
                        opData.setStrValue("OpData.Conditions.Condition[" + index + "].fieldValue", tempFV);
                        index++;
                    }
                }

            }
        }
        return index;
    }


    /**
     * 数据检索
     *
     * @param request  请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    @RequestMapping(value = "/query.gv", method = RequestMethod.POST)
    public void mapQuery(HttpServletRequest request, HttpServletResponse response) {

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int prjCd = NumberUtil.getIntFromObj(getPrjCd(request));
        svcRequest.setValue("Request.TcpCont.PrjCd", prjCd);
        int count = 0;
        StringBuilder resultJSON = new StringBuilder("{");
        StringBuilder dataJSON = new StringBuilder();
        /** 获取入参 */
        String jsonParam = request.getParameter("jsonParam");//获取入参JSONString
        JSONObject jsonObject = JSON.parseObject(jsonParam);//转为JSON串
        String searchKey = jsonObject.getString("searchKey");//检索条件, 为空不检索,有数字检索地址，其他先检索产权人获取到直接返回，没有获取到检索地址
        String[] queryField;
        if (searchKey.isEmpty()) {
            return;
        } else if (hasDigit(searchKey)) {//包含数字
            queryField = new String[]{"hsFullAddr"};
        } else {
            queryField = new String[]{"hsOwnerPersons", "hsFullAddr"};
        }
        JSONArray conditions = jsonObject.getJSONArray("conditions");//检索条件

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");

        int conCount = queryField.length;
        for (int i = 0; i < conCount; i++) {
            //查询条件
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", queryField[i]);
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", "%" + searchKey + "%");
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "LIKE");
            if (!conditions.isEmpty()) {
                int conditionCount = 1;
                for (int j = 0; j < conditions.size(); j++) {
                    JSONObject tempJO = JSON.parseObject(conditions.get(j).toString());
                    String tempKey = tempJO.getString("key");
                    if (tempKey == null) {
                        continue;
                    }
                    String tempValue = tempJO.get("value").toString();
                    opData.setStrValue("OpData.Conditions.Condition[" + conditionCount + "].fieldName", tempKey.replaceAll("_", "\\."));
                    opData.setStrValue("OpData.Conditions.Condition[" + conditionCount + "].fieldValue", tempValue);
                    opData.setStrValue("OpData.Conditions.Condition[" + conditionCount++ + "].operation", "=");
                }
            }
            // 分页查询
            String pageSize = jsonObject.getString("pageSize");
            String currentPage = jsonObject.getString("currentPage");
            if (StringUtil.isEmptyOrNull(pageSize)) {
                pageSize = "10";
            }
            if (StringUtil.isEmptyOrNull(currentPage)) {
                currentPage = "1";
            }
            opData.setStrValue("OpData.PageInfo.pageSize", pageSize);
            opData.setStrValue("OpData.PageInfo.currentPage", currentPage);
            // 查询字段
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "hsFullAddr");//房屋地址
            opData.setStrValue("OpData.ResultFields.fieldName[2]", "hsOwnerPersons");//固定属性，产权人姓名
            opData.setStrValue("OpData.ResultFields.fieldName[3]", "hsOwnerType");//固定属性，产权性质
            opData.setStrValue("OpData.ResultFields.fieldName[4]", "hsBuildSize");//固定属性，建筑面积
            opData.setStrValue("OpData.ResultFields.fieldName[5]", "hsRoomPos");
            opData.setStrValue("OpData.ResultFields.fieldName[6]", "hsSlfRoomPos");
            opData.setStrValue("OpData.ResultFields.fieldName[7]", "hsPosX");
            opData.setStrValue("OpData.ResultFields.fieldName[8]", "hsPosY");
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            SvcResponse svcResponse = query(svcRequest);
            boolean isSuccess = svcResponse.isSuccess();
            if (isSuccess) {
                XmlBean resultData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                        .getBeanByPath("Operation.OpResult.PageData");
                String pageInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                        .getBeanByPath("Operation.OpResult.PageInfo").toJson();
                if (resultData != null) {
                    count = resultData.getListNum("PageData.Row");
                    if (count > 0) {
                        resultJSON.append("\"total\":" + count + ",");
                        resultJSON.append("\"pageInfo\":" + pageInfo + ",");
                        resultJSON.append("\"data\":[");
                        if (count > 20) {
                            count = 20;
                        }
                        for (int j = 0; j < count; j++) {
                            String hsId = resultData.getStrValue("PageData.Row[" + j + "].hsId");
                            String hsFullAddr = resultData.getStrValue("PageData.Row[" + j + "].hsFullAddr");
                            //固定属性
                            String hsOwnerPersons = resultData.getStrValue("PageData.Row[" + j + "].hsOwnerPersons");
                            String hsOwnerType = resultData.getStrValue("PageData.Row[" + j + "].hsOwnerType_Name");
                            String hsBuildSize = resultData.getStrValue("PageData.Row[" + j + "].hsBuildSize");
                            //中心点坐标
                            String hsPosX = resultData.getStrValue("PageData.Row[" + j + "].hsPosX");
                            String hsPosY = resultData.getStrValue("PageData.Row[" + j + "].hsPosY");
                            //房屋坐标
                            String hsRoomPos = resultData.getStrValue("PageData.Row[" + j + "].hsRoomPos");
                            String hsSlfRoomPos = resultData.getStrValue("PageData.Row[" + j + "].hsSlfRoomPos");
                            String[] posArr = new String[]{hsRoomPos, hsSlfRoomPos};
                            for (int k = 0; k < posArr.length; k++) {
                                String tempPos = posArr[k];
                                if (!tempPos.isEmpty()) {
                                    String[] hsPosArr = tempPos.split("\\|");
                                    for (int l = 0; l < hsPosArr.length; l++) {
                                        String pos = hsPosArr[l].replaceAll("POINT\\(", "[").replaceAll("\\)", "]").replaceAll(" ", ",");
                                        //第一户第一次进来只拼json,第二次进来时增加逗号分隔
                                        if (l > 0) {
                                            dataJSON.append(",");
                                        } else if (dataJSON.length() > 0) {//判断是否是第一户，如果不是，则第一次进来也要加逗号分隔
                                            dataJSON.append(",");
                                        }
                                        dataJSON.append("{\"title\":\"" + hsFullAddr + "\",");
                                        dataJSON.append("\"tags\":[\"" + hsOwnerPersons + "\",\"" + hsOwnerType + "\",\"" + hsBuildSize + "\"],");
                                        dataJSON.append("\"centerPoint\":[" + hsPosX + "," + hsPosY + "],");
                                        dataJSON.append("\"path\":[[" + pos + "]],");
                                        dataJSON.append("\"relId\":\"" + hsId + "\"}");
                                    }
                                }
                            }
                        }
                        resultJSON.append(dataJSON).append("]");
                    }
                    break;
                } else if (i == (conCount - 1)) {
                    resultJSON.append("\"total\": 0").append(",\"data\": []");
                }
            } else if (i == (conCount - 1)) {
                resultJSON.append("\"total\": 0").append(",\"data\": []");
            }
        }
        resultJSON.append("}");
        String jsonStr = "{\"success\":true, \"errMsg\":\"\", \"data\":" + resultJSON + "}";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 数据详情
     *
     * @param request  请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    @RequestMapping(value = "/info.gv", method = RequestMethod.POST)
    public void mapInfo(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder dataJSON = new StringBuilder();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int prjCd = NumberUtil.getIntFromObj(getPrjCd(request));
        svcRequest.setValue("Request.TcpCont.PrjCd", prjCd);
        /** 获取入参 */
        String jsonParam = request.getParameter("jsonParam");//获取入参JSONString
        JSONObject jsonObject = JSON.parseObject(jsonParam);//转为JSON串
        String layerId = jsonObject.getString("layerId");//所属图层编号，用于判断实体对象
        String[] attrArr = new String[]{"hsOwnerPersons", "hsOwnerType", "hsBuildSize"};
        String entityName = "HouseInfo";
        String entityId = "HouseInfo.hsId";
        String tittle = "hsFullAddr";
        String cmptName = "QUERY_MORE_ENTITY_CMPT";
        String nodeName = "PageData.Row.HouseInfo.";
        if (StringUtil.isEqual(layerId, "jkXx")) {
            entityName = "CameraMngInfo";
            entityId = "camId";
            tittle = "camAddr";
            attrArr = new String[]{"camCd", "channelCd"};
            cmptName = "QUERY_ENTITY_PAGE_DATA";
            nodeName = "PageData.Row.";
        }
        String relId = jsonObject.getString("relId");//实体的业务主键ID
        String dimension = jsonObject.getString("dimension");//当前展示的维度
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", entityName);
        //查询条件
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", entityId);
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", relId);
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        if (StringUtil.isEqual(layerId, "jkXx")) {
            /** 查询监控信息 */
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "camCd");//固定属性,摄像头分组
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "channelCd");//固定属性,通道编号
            opData.setStrValue("OpData.ResultFields.fieldName[2]", "markPos");//坐标点
            opData.setStrValue("OpData.ResultFields.fieldName[3]", tittle);
        } else {
            /** 查询房屋信息 */
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo." + tittle);//标题展示
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");//固定属性，产权人姓名
            opData.setStrValue("OpData.ResultFields.fieldName[2]", "HouseInfo.hsOwnerType");//固定属性，产权性质
            opData.setStrValue("OpData.ResultFields.fieldName[3]", "HouseInfo.hsBuildSize");//固定属性，建筑面积
            opData.setStrValue("OpData.ResultFields.fieldName[4]", "HouseInfo.hsRoomPos");
            opData.setStrValue("OpData.ResultFields.fieldName[5]", "HouseInfo.hsSlfRoomPos");
            opData.setStrValue("OpData.ResultFields.fieldName[6]", "HouseInfo.hsPosX");
            opData.setStrValue("OpData.ResultFields.fieldName[7]", "HouseInfo.hsPosY");
            opData.setStrValue("OpData.ResultFields.fieldName[8]", dimension.replaceAll("_", "."));
        }
        svcRequest.addOp(cmptName, opData);
        //启动服务
        SvcResponse svcResponse = query(svcRequest);
        boolean isSuccess = svcResponse.isSuccess();
        if (isSuccess) {
            XmlBean resultData = svcResponse.getFirstOpRsp(cmptName)
                    .getBeanByPath("Operation.OpResult.PageData");
            if (resultData != null) {
                String pathStr = "[]";//房屋多边形坐标数组
                String attrAddr = resultData.getStrValue(nodeName + tittle);//tittle名称
                String centerPoint;//中心坐标
                if (!StringUtil.isEqual(layerId, "jkXx")) {
                    String hsPosX = resultData.getStrValue(nodeName + "hsPosX");
                    String hsPosY = resultData.getStrValue(nodeName + "hsPosY");
                    centerPoint = "[" + hsPosX + "," + hsPosY + "]";
                    //多边形坐标
                    String posStr;
                    if (StringUtil.isEqual(layerId, "zsHs")) {
                        posStr = resultData.getStrValue(nodeName + "hsRoomPos");
                    } else {
                        posStr = resultData.getStrValue(nodeName + "hsSlfRoomPos");
                    }
                    if (!posStr.isEmpty()) {
                        pathStr = "[" + posStr.replaceAll("POINT\\(", "[").replaceAll("\\)", "]").replaceAll(" ", ",").replaceAll("\\|", "],[") + "]";
                    }
                } else {
                    String markPos = resultData.getStrValue(nodeName + "markPos");
                    centerPoint = "[" + markPos + "]";
                    pathStr = markPos;
                }
                dataJSON.append("{\"title\":\"" + attrAddr + "\",");
                dataJSON.append("\"tags\":[");
                //tags节点，固定属性
                String tags = "";
                for (int j = 0; j < attrArr.length; j++) {
                    String value = resultData.getStrValue(nodeName + attrArr[j] + "_Name");
                    if (StringUtil.isEmptyOrNull(value)) {
                        value = resultData.getStrValue(nodeName + attrArr[j]);
                    }
                    tags = tags + "\"" + value + "\",";
                }
                //拼接维度属性
                String dValue = resultData.getStrValue(dimension.replaceAll("_", "."));
                tags = tags + "\"" + dValue + "\"";
                dataJSON.append(tags).append("],");
                //centerPoint节点
                centerPoint = "\"centerPoint\":" + centerPoint;
                dataJSON.append(centerPoint).append(",");
                //path节点
                dataJSON.append("\"path\":[" + pathStr + "],");
                //relId节点
                dataJSON.append("\"relId\":\"" + relId + "\"}");
            }
        }
//        resultJSON.append("}");
        String jsonStr = "{\"success\":true, \"errMsg\":\"\", \"data\":" + dataJSON + "}";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 查询参数配置的全部值
     */
    protected List<Map<String, String>> getCfgCollectionWhitNode(HttpServletRequest request, String itemCd, boolean isCached, int prjCd) {
        List<Map<String, String>> cfgList = new ArrayList<Map<String, String>>();
        // 调用服务获取后台数据
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", itemCd);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", isCached);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", prjCd);
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            for (int i = 0; i < count; i++) {
                Map<String, String> useCollection = new LinkedHashMap<String, String>();
                String valueCd = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].valueCd");
                String valueName = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].valueName");
                String notes = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].notes");
                useCollection.put("valueCd", valueCd);
                useCollection.put("valueName", valueName);
                useCollection.put("notes", notes);
                cfgList.add(useCollection);
            }
        }
        return cfgList;
    }

    /**
     * 判断一个字符串是否含有数字
     */
    public boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 查询是否有权限
     */
    public boolean isHaveRht(HttpServletRequest request, String rhtCd, int prjCd) {
        boolean haveRht = false;
        if (StringUtil.isNotEmptyOrNull(rhtCd)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean reqData = new XmlBean();
            reqData.setValue("SvcCont.rightType", "1");
            reqData.setValue("SvcCont.rhtCd", rhtCd);
            reqData.setValue("SvcCont.prjCd", prjCd);
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffHaveRht", svcRequest);
            if (svcResponse.isSuccess()) {
                haveRht = Boolean.valueOf(svcResponse.getRspData().getStrValue("SvcCont.haveRight"));
            } else {
                haveRht = false;
            }
        }
        return haveRht;
    }

    /**
     * 从Session中获取prjCd
     */
    private String getPrjCd(HttpServletRequest request) {
        String prjCd = "0";
        SessionUtil sessionUtil = new SessionUtil(request);
        String tempJSONStr = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
        if (StringUtil.isNotEmptyOrNull(tempJSONStr)) {
            XmlBean tempBean = new XmlBean(tempJSONStr);
            prjCd = tempBean.getStrValue("LoginInfo.prjCd");
        }
        return prjCd;
    }

//    /**
//     * 返回消息头改为json，避免通过中间层访问数据过长而导致乱码
//     *
//     * @param response
//     * @param printObj
//     */
//    public static void printAjaxJSON(HttpServletResponse response, Object printObj) {
//        response.setContentType("text/json; charset=UTF-8");
//        response.setHeader("Cache-Control", "no-cache");
//        PrintWriter out = null;
//        try {
//            out = response.getWriter();
//            out.print(printObj);
//        } catch (IOException var4) {
//            LOG.error("Error:Cannot create PrintWriter Object !");
//        } finally {
//            if (out != null) {
//                out.close();
//            }
//        }
//
//    }

}
