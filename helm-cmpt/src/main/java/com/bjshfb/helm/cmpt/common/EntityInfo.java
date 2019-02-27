package com.bjshfb.helm.cmpt.common;

/**
 * @program: helm
 * @description: 扩展实体基本信息
 * @author: fuzq
 * @create: 2018-09-20 09:47
 **/
public class EntityInfo {
    private String layerType;

    private String entityName;

    private String entityId;

    private String entityCd;

    private String entityQueryName;

    private String entityQueryValue;


    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityCd() {
        return entityCd;
    }

    public void setEntityCd(String entityCd) {
        this.entityCd = entityCd;
    }

    public String getEntityQueryName() {
        return entityQueryName;
    }

    public void setEntityQueryName(String entityQueryName) {
        this.entityQueryName = entityQueryName;
    }

    public String getEntityQueryValue() {
        return entityQueryValue;
    }

    public void setEntityQueryValue(String entityQueryValue) {
        this.entityQueryValue = entityQueryValue;
    }
}
