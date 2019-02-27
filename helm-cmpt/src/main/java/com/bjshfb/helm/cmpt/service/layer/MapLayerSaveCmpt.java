package com.bjshfb.helm.cmpt.service.layer;

import com.shfb.oframe.core.component.impl.AbstractComponent;
import com.shfb.oframe.core.service.impl.ExecuteContext;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.exception.BusinessException;
import com.shfb.oframe.core.util.exception.CheckException;
import org.springframework.stereotype.Service;

/**
 * @program: helm
 * @description: 图层定义服务
 * @author: fuzq
 * @create: 2018-09-11 15:31
 **/
@Service("mapLayerSaveCmpt")
public class MapLayerSaveCmpt extends AbstractComponent {

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        String mapLayerDefId = executeParam.getStrValue("OpData.mapLayerDefId");

        // 修改
        if (StringUtil.isNotEmptyOrNull(mapLayerDefId)) {
            this.saveEntityInfo(executeContext, "MapLayerDef", executeParam);
        } else { //新增
            mapLayerDefId = "${mapLayerDefId}";
            executeParam.setStrValue("OpData.mapLayerDefId", mapLayerDefId);
            this.saveEntityInfo(executeContext, "MapLayerDef", executeParam);
        }

        return new XmlBean();
    }
}
