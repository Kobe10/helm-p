package com.bjshfb.helm.cmpt.service.date;

import com.bjshfb.helm.cmpt.common.EntityInfo;
import com.bjshfb.helm.util.EntityInfoUtils;
import com.bjshfb.helm.util.FileUtils;
import com.shfb.oframe.core.component.impl.AbstractComponent;
import com.shfb.oframe.core.dao.domin.DaoParam;
import com.shfb.oframe.core.dao.domin.ResultParam;
import com.shfb.oframe.core.service.impl.ExecuteContext;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.exception.BusinessException;
import com.shfb.oframe.core.util.exception.CheckException;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: helm
 * @description: 图层数据通用查询
 * @author: fuzq
 * @create: 2018-09-14 16:36
 **/
@Service("mapDateQueryCmpt")
public class MapDateQueryCmpt extends AbstractComponent {
    private final String singleQuery = "1";
    private final String complexQuery = "2";
    private final String layerQuery = "3";

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        //图层类型 ：1通用图层 2扩展图层
        String queryType = executeParam.getStrValue("OpData.queryType");
        String data = executeParam.getStrValue("OpData.data");
        JSONObject jsonObject;
        XmlBean opData = new XmlBean();
        JSONObject queryData = JSONObject.fromObject(data);
        JSONObject resultJson = new JSONObject();
        // 查询单独图层数据  根据图层Id查询
        if (StringUtil.isEqual(singleQuery, queryType)) {
            String layerType = queryData.getString("layerType");
            String mapDataId = queryData.getString("mapDataId");
            //图层Id
            String layerId = queryData.getString("layerId");
            if (StringUtil.isEqual("1", layerType)) {
                //通用实体查询
                resultJson = handlerGeneral(mapDataId, null, null, null);
            } else {
                EntityInfo entityInfo = EntityInfoUtils.getEntityInfo(layerType);
                if (entityInfo != null) {
                    String entityName = entityInfo.getEntityName();
                    String entityCd = entityInfo.getEntityCd();
                    resultJson = handlerSingleExpans(entityName, entityCd, null, null, layerId, mapDataId);
                }
            }
        }
        // 综合查询图层数据   名称地址模糊查询
        else if (StringUtil.isEqual(complexQuery, queryType)) {
            //图层类型
            String keyWord = queryData.getString("keyWord");
            resultJson = queryAllDataInfo(keyWord);
        }
        // 针对图层数据查询  单一字段进行查询
        else if (StringUtil.isEqual(layerQuery, queryType)) {
            //图层Id
            String mapLayerDefId = queryData.getString("layerId");
            //图层类型
            String keyWord = queryData.getString("keyWord");
            //查询字段
            String queryName = queryData.getString("queryName");
            //通用图层查询
            resultJson = handlerGeneral(null, keyWord, queryName, mapLayerDefId);
        }
        XmlBean xmlBean = new XmlBean();
        xmlBean.setStrValue("Data", resultJson);
        return xmlBean;
    }

    /**
     * @Description: TODO 处理通用模型查询
     * @Param: [mapDataId]
     * @return: net.sf.json.JSONObject
     * @Author: fuzq
     * @Date: 2018/9/14 19:30
     **/
    public JSONObject handlerGeneral(String mapDataId, String keyWord, String queryName, String layerId) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //获取图层Id
        String mapLayerDefId = getmapLayerDefIdBymapDataId(mapDataId);
        JSONObject jsonObject = queryAttrDef(mapLayerDefId);
        JSONObject resultJson = new JSONObject();
        //查询单挑记录的所有伪属性
        DaoParam result = new DaoParam();
        String preDate = "OpData.";
        //模糊查询
        if (StringUtil.isNotEmptyOrNull(queryName)) {
            //如果是查询单一字段
            result.addCondition(queryName, "like", "%" + keyWord + "%");
            result.addCondition("mapLayerDefId", "=", layerId);
            //查询名称地址
            result.addQueryParams("mapDataName", "mapDataAddr", "mapDataId");
            result.addEntityName("MapDataInfo");
            ResultParam resultParam = entityDao.queryPageData(result);
            if (resultParam.isHaveData()) {
                for (int i = 0; i < resultParam.getValueLists().length; i++) {
                    Map<String, String> map = new HashMap<String, String>();
                    //拼接出参
                    if (resultParam.getValueLists()[i][0] != null) {
                        map.put("mapDataName", resultParam.getValueLists()[i][0].toString());
                    } else {
                        map.put("mapDataName", "");
                    }
                    if (resultParam.getValueLists()[i][1] != null) {
                        map.put("mapDataAddr", resultParam.getValueLists()[i][1].toString());
                    } else {
                        map.put("mapDataAddr", "");
                    }
                    if (resultParam.getValueLists()[i][2] != null) {
                        map.put("mapDataId", resultParam.getValueLists()[i][2].toString());
                    } else {
                        map.put("mapDataId", "");
                    }
                    list.add(map);
                }
            }
        } else {
            result.addCondition("mapDataId", "=", mapDataId);
            result.addQueryParams("dataAttrCd", "dataAttrValue");
            result.addEntityName("MapDataInfo");
            ResultParam resultParam = entityDao.queryPageData(result);
            if (resultParam.isHaveData()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < resultParam.getValueLists().length; i++) {
                    //拼接出参
                    if (resultParam.getValueLists()[i][0] != null) {
                        if (resultParam.getValueLists()[i][1] != null) {
                            map.put(resultParam.getValueLists()[i][0].toString(), resultParam.getValueLists()[i][1].toString());
                        } else {
                            map.put(resultParam.getValueLists()[i][0].toString(), "");
                        }
                    }
                }
                list.add(map);
            }
        }
        resultJson.put("mapDataInfo", list);

        return resultJson;
    }

    /**
     * @Description: TODO 单一扩展实体查询 拼接条件
     * @Param: [entityName, entityCd]
     * @return: com.shfb.oframe.core.util.common.XmlBean
     * @Author: fuzq
     * @Date: 2018/9/14 17:32
     **/
    public JSONObject handlerSingleExpans(String entityName, String entityCd, String keyWord, String queryName, String layerId, String dataId) {
        JSONObject resultJson = new JSONObject();
        DaoParam result = new DaoParam();
        String preDate = "OpData.";
        if (StringUtil.isNotEmptyOrNull(layerId)) {
            JSONObject attrDef;
            attrDef = queryAttrDef(layerId);
            List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
            if (StringUtil.isNotEmptyOrNull(queryName)) {

            } else {
                result.addEntityName(entityName).addCondition(entityCd, "=", dataId);
                List<String> list = new ArrayList<String>();
                //遍历图层属性 拼接报文
                Iterator iterator = attrDef.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    String value = attrDef.getString(key);
                    if (StringUtil.isNotEmptyOrNull(value)) {
                        list.add(value);
                    }
                }
                result.addQueryParams(list);
                ResultParam resultParam = entityDao.queryPageData(result);
                //拼接结果
                if (resultParam.isHaveData()) {
                    Map<String, String> map = new HashMap<String, String>();
                    attrDef.clear();
                    attrDef = queryAttrDef(layerId);
                    Iterator iterator1 = attrDef.keys();
                    while (iterator1.hasNext()) {
                        String key = iterator1.next().toString();
                        map.put(attrDef.getString(key), resultParam.getStrValueByAttr(attrDef.getString(key)));
                    }
                    resultList.add(map);
                }
            }
            resultJson.put("mapDataInfo", resultList);
        }
        return resultJson;
    }

    /**
     * @Description: TODO 查询图层Id
     * @Param: [mapDataId]
     * @return: java.lang.String
     * @Author: fuzq
     * @Date: 2018/9/14 19:23
     **/
    public String getmapLayerDefIdBymapDataId(String mapDataId) {
        DaoParam daoParam = new DaoParam("MapDataInfo");
        daoParam.addEqualCondition("mapDataId", mapDataId);
        daoParam.addQueryParams("mapLayerDefId");
        ResultParam resultParam = entityDao.queryPageData(daoParam);
        String mapLayerDefId = "";
        if (resultParam.isHaveData()) {
            mapLayerDefId = resultParam.getStrValueByAttr("mapLayerDefId");
        }
        return mapLayerDefId;
    }


    /**
     * @Description: TODO 查询当前图层的属性 json格式
     * @Param: [mapLayerDefId]
     * @return: net.sf.json.JSONObject
     * @Author: fuzq
     * @Date: 2018/9/14 17:37
     **/
    public JSONObject queryAttrDef(String mapLayerDefId) {

        JSONObject jsonObject = new JSONObject();
        //查询该户下产权单位的户
        DaoParam daoParam = new DaoParam("MapLayerDef");
        daoParam.addEqualCondition("mapLayerDefId", mapLayerDefId);
        daoParam.addQueryParams("attrDef", "mapLayerDefId");
        ResultParam resultParam = entityDao.queryPageData(daoParam);
        if (resultParam.isHaveData()) {
            String s = resultParam.getStrValueByAttr("attrDef");
            jsonObject = JSONObject.fromObject(s);
        }
        return jsonObject;
    }

    /**
     * @Description: TODO 查询所有图层数据
     * @Param: [keyWord, list]
     * @return: net.sf.json.JSONObject
     * @Author: fuzq
     * @Date: 2018/9/21 10:21
     **/
    public JSONObject queryAllDataInfo(String keyWord) {
        JSONObject resultJson = new JSONObject();
        Set<String> set = new TreeSet<String>();
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        //名称模糊查询
        DaoParam daoParam = new DaoParam("MapDataInfo");
        daoParam.addCondition("mapDataName", "like", "%" + keyWord + "%");
        daoParam.addQueryParams("mapDataId");
        ResultParam resultParam = entityDao.queryPageData(daoParam);
        if (resultParam.isHaveData()) {
            for (int i = 0; i < resultParam.getValues().length; i++) {
                set.add(resultParam.getValueLists()[0][i].toString());
            }
        }

        //地址模糊查询
        daoParam = new DaoParam("MapDataInfo").addCondition("mapDataAddr", "like", "%" + keyWord + "%")
                .addQueryParams("mapDataId");
        resultParam = entityDao.queryPageData(daoParam);
        if (resultParam.isHaveData()) {
            for (int i = 0; i < resultParam.getValues().length; i++) {
                set.add(resultParam.getValueLists()[0][i].toString());
            }
        }

        //遍历查询所有数据 拼接
        for (String str : set) {
            Map<String, String> map = new HashMap<String, String>();
            daoParam = new DaoParam("MapDataInfo").addEqualCondition("mapDataId", str)
                    .addQueryParams("mapDataName", "mapDataAddr", "mapDataId", "mapLayerDefId");
            resultParam = entityDao.queryPageData(daoParam);
            map.put("mapDataName", resultParam.getStrValueByAttr("mapDataName"));
            map.put("mapDataAddr", resultParam.getStrValueByAttr("mapDataAddr"));
            map.put("mapDataId", resultParam.getStrValueByAttr("mapDataId"));
            map.put("mapLayerDefId", resultParam.getStrValueByAttr("mapLayerDefId"));
            //查询图层Id对应的图层名称
            if (StringUtil.isNotEmptyOrNull(resultParam.getStrValueByAttr("mapLayerDefId"))) {
                daoParam = new DaoParam("MapLayerDef").addEqualCondition("mapLayerDefCd", resultParam.getStrValueByAttr("mapLayerDefId"))
                        .addQueryParams("mapLayerDefName");
                resultParam = entityDao.queryPageData(daoParam);
                if (resultParam.isHaveData()) {
                    map.put("mapLayerDefName", resultParam.getStrValueByAttr("mapLayerDefName"));
                }
            } else {
                map.put("mapLayerDefName", "");
            }
            resultList.add(map);
        }
        resultJson.put("mapDataInfo", resultList);
        return resultJson;
    }
}