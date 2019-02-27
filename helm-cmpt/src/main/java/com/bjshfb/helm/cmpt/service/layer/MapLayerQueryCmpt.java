package com.bjshfb.helm.cmpt.service.layer;

import com.shfb.oframe.core.component.impl.AbstractComponent;
import com.shfb.oframe.core.dao.domin.EntityInfo;
import com.shfb.oframe.core.service.impl.ExecuteContext;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.exception.BusinessException;
import com.shfb.oframe.core.util.exception.CheckException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: helm
 * @description: 查询图层服务
 * @author: fuzq
 * @create: 2018-09-14 09:52
 **/
@Service("mapLayerQueryCmpt")
public class MapLayerQueryCmpt extends AbstractComponent {

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        String keyWord = executeParam.getStrValue("OpData.keyWord");
        String mapLayerDefId = executeParam.getStrValue("OpData.mapLayerDefId");
        String prePath = "OpData.";
        //查询图层名称 图层类型
        XmlBean conditions = executeParam.getBeanByPath("OpData.Conditions");
        //排序字段
        XmlBean sortFields = executeParam.getBeanByPath("OpData.SortFields");
        XmlBean conditionAndSortBean = new XmlBean();
        conditionAndSortBean.setStrValue("OpData.entityName", "MapLayerDef");
        if (conditions != null) {
            conditionAndSortBean.setBeanByPath("OpData", conditions);
        }
        if (sortFields != null) {
            conditionAndSortBean.setBeanByPath("OpData", sortFields);
        }
        //裁决图层定义名称
        EntityInfo mapLayerDef = entityInfoDao.getEntityInfoByEntityName("MapLayerDef");
        List<String> attrNames = mapLayerDef.getAttrNames();
        XmlBean resultFieldBean = new XmlBean("<ResultFields></ResultFields>");
        for (String attrName : attrNames) {
            XmlBean attrNameBean = new XmlBean("<fieldName>" + attrName + "</fieldName>");
            resultFieldBean.append(attrNameBean);
        }
        //查询的字段
        conditionAndSortBean.setBeanByPath("OpData", resultFieldBean);

        if (StringUtil.isNotEmptyOrNull(mapLayerDefId)) {
            conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].fieldName", "mapLayerDefId");
            conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].fieldValue", mapLayerDefId);
            conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
            XmlBean layerData = super.getComponentByBeanName("queryEntityCmpt").execute(executeContext, conditionAndSortBean);
            return layerData;
        } else if (StringUtil.isNotEmptyOrNull(keyWord)){
            //图层名称匹配
            if (!StringUtil.isEmptyOrNull(keyWord)) {
                conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].fieldName", "mapLayerDefName");
                conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "%" + keyWord + "%");
                conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].operation", "like");
            }
            XmlBean layerName = super.getComponentByBeanName("queryEntityCmpt").execute(executeContext, conditionAndSortBean);

            //图层类型匹配
            if (!StringUtil.isEmptyOrNull(keyWord)) {
                //翻译图层类型
                conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].fieldName", "mapLayerType");
                conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "%" + keyWord + "%");
                conditionAndSortBean.setStrValue(prePath + "Conditions.Condition[0].operation", "like");
            }
            XmlBean layerType = super.getComponentByBeanName("queryEntityCmpt").execute(executeContext, conditionAndSortBean);

            //拼接参数  获取参数  取并集
            if (!StringUtil.isEmptyOrNull(layerName) && !layerName.isEmpty()) {
                if (!StringUtil.isEmptyOrNull(layerName)) {
                    int nameNum = layerName.getListNum("OpResult.PageData.Row");
                    int typeNum = layerType.getListNum("OpResult.PageData.Row");
                    List<String> layerIds = new ArrayList<String>();
                    for (int i = 0; i < nameNum; i++) {
                        layerIds.add(layerName.getStrValue("OpResult.PageData.Row[" + i + "].mapLayerDefId"));
                    }
                    for (int j = 0; j < typeNum; j++) {
                        if (!layerIds.contains(layerType.getStrValue("OpResult.PageData.Row[" + j + "].mapLayerDefId"))) {
                            layerName.getBeanByPath("OpResult.PageData").append(layerType.getBeanByPath("OpResult.PageData.Row[" + j + "]"));
                        }
                    }
                }
                return layerName;
            } else if (!StringUtil.isEmptyOrNull(layerType) && !layerType.isEmpty()) {
                if (!StringUtil.isEmptyOrNull(layerType)) {
                    int nameNum = layerName.getListNum("OpResult.PageData.Row");
                    int typeNum = layerType.getListNum("OpResult.PageData.Row");
                    List<String> layerIds = new ArrayList<String>();
                    for (int i = 0; i < typeNum; i++) {
                        layerIds.add(layerType.getStrValue("OpResult.PageData.Row[" + i + "].mapLayerDefId"));
                    }
                    for (int j = 0; j < nameNum; j++) {
                        if (!layerIds.contains(layerName.getStrValue("OpResult.PageData.Row[" + j + "].mapLayerDefId"))) {
                            layerType.getBeanByPath("OpResult.PageData").append(layerName.getBeanByPath("OpResult.PageData.Row[" + j + "]"));
                        }
                    }
                }
                return layerType;
            }

            return layerName;
        } else if (!StringUtil.isNotEmptyOrNull(keyWord)){
            XmlBean layerData = super.getComponentByBeanName("queryEntityCmpt").execute(executeContext, conditionAndSortBean);
            return layerData;
        } else {
            return null;
        }
    }
}
