package com.bjshfb.helm.util;

import com.bjshfb.helm.cmpt.common.EntityInfo;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: helm
 * @description: 扩展实体信息工具类
 * @author: fuzq
 * @create: 2018-09-20 09:50
 **/
public class EntityInfoUtils {

    /**
     * @Description: TODO 根据类型返回扩展实体
     * @Param: [type]
     * @return: com.bjshfb.helm.cmpt.common.EntityInfo
     * @Author: fuzq
     * @Date: 2018/9/20 18:18
     **/
    public static EntityInfo getEntityInfo(String type) {
        XmlBean flowXml = FileUtils.createXmlByFile("classpath:expan-entity-info.xml");
        int num = flowXml.getListNum("Rows.Row");
        EntityInfo entityInfo = new EntityInfo();
        for (int i = 0; i < num; i++) {
            if (StringUtil.isEqual(type, flowXml.getStrValue("Rows.Row[" + i + "].LayerType"))) {
                entityInfo.setLayerType(type);
                entityInfo.setEntityId(flowXml.getStrValue("Rows.Row[" + i + "].EntityId"));
                entityInfo.setEntityName(flowXml.getStrValue("Rows.Row[" + i + "].EntityName"));
                entityInfo.setEntityCd(flowXml.getStrValue("Rows.Row[" + i + "].EntityCd"));
                entityInfo.setEntityQueryName(flowXml.getStrValue("Rows.Row[" + i + "].EntityQueryName"));
                entityInfo.setEntityQueryValue(flowXml.getStrValue("Rows.Row[" + i + "].EntityQueryValue"));
            }
        }
        return entityInfo;
    }
    /**
     * @Description: TODO 返回所有扩展实体属性
     * @Param: []
     * @return: java.util.List<com.bjshfb.helm.cmpt.common.EntityInfo>
     * @Author: fuzq
     * @Date: 2018/9/20 18:17
     **/
    public static List<EntityInfo> getAllEntityInfo() {

        List<EntityInfo> list = new ArrayList<EntityInfo>();
        XmlBean flowXml = FileUtils.createXmlByFile("classpath:expan-entity-info.xml");
        int num = flowXml.getListNum("Rows.Row");
        for (int i = 0; i < num; i++) {
            EntityInfo entityInfo = new EntityInfo();
            entityInfo.setLayerType(flowXml.getStrValue("Rows.Row[" + i + "].LayerType"));
            entityInfo.setEntityId(flowXml.getStrValue("Rows.Row[" + i + "].EntityId"));
            entityInfo.setEntityName(flowXml.getStrValue("Rows.Row[" + i + "].EntityName"));
            entityInfo.setEntityCd(flowXml.getStrValue("Rows.Row[" + i + "].EntityCd"));
            entityInfo.setEntityQueryName(flowXml.getStrValue("Rows.Row[" + i + "].EntityQueryName"));
            entityInfo.setEntityQueryValue(flowXml.getStrValue("Rows.Row[" + i + "].EntityQueryValue"));
            list.add(entityInfo);
        }
        return list;
    }
}
