package com.bjshfb.helm.cmpt.service.date;

import com.bjshfb.helm.cmpt.common.EntityInfo;
import com.bjshfb.helm.util.EntityInfoUtils;
import com.shfb.oframe.core.component.impl.AbstractComponent;
import com.shfb.oframe.core.dao.domin.DaoParam;
import com.shfb.oframe.core.dao.domin.ResultParam;
import com.shfb.oframe.core.service.impl.ExecuteContext;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.exception.BusinessException;
import com.shfb.oframe.core.util.exception.CheckException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: helm
 * @description: 数据删除
 * @author: fuzq
 * @create: 2018-09-13 15:05
 **/
@Service("mapDateDelCmpt")
public class MapDateDelCmpt extends AbstractComponent {

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        String mapDataIds = executeParam.getStrValue("OpData.mapDataIds");
        String expanIds = executeParam.getStrValue("OpData.expanIds");
        String layperType = executeParam.getStrValue("OpData.layperType");

        //确认是否有关联数据
        List<String> list = new ArrayList<String>();

        //删除通用数据
        if (StringUtil.isNotEmptyOrNull(mapDataIds)) {
            List<String> mapList;
            mapList = Arrays.asList(mapDataIds.split(","));
            for (String mapDataId : mapList) {
                this.deleteEntity(MapDateSaveCmpt.GEN_ENTITY_NAME, mapDataId);
                //查找扩展实体数据
                if (!StringUtil.isEqual("1", layperType)) {
                    EntityInfo entityInfo = EntityInfoUtils.getEntityInfo(layperType);
                    DaoParam daoParam = new DaoParam(entityInfo.getEntityName());
                    daoParam.addEqualCondition(entityInfo.getEntityCd(), mapDataId);
                    daoParam.addQueryParams(entityInfo.getEntityId());
                    ResultParam resultParam = entityDao.queryPageData(daoParam);
                    if (resultParam.isHaveData()) {
                        if (StringUtil.isNotEmptyOrNull(resultParam.getStrValueByAttr(entityInfo.getEntityId()))) {
                            list.add(resultParam.getStrValueByAttr(entityInfo.getEntityId()));
                        }
                    }
                }
            }
        }
        //扩展实体删除
        if (!StringUtil.isEqual("1", layperType)) {
            EntityInfo entityInfo = EntityInfoUtils.getEntityInfo(layperType);
            //遍历查找扩展实体主键
            for (String id : list) {
                this.deleteEntity(entityInfo.getEntityName(), id);
            }
        }
        return new XmlBean();
    }
}