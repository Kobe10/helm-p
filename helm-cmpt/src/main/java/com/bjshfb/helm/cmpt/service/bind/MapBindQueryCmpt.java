package com.bjshfb.helm.cmpt.service.bind;

import com.bjshfb.helm.cmpt.service.date.MapDateSaveCmpt;
import com.shfb.oframe.core.component.impl.AbstractComponent;
import com.shfb.oframe.core.dao.domin.DaoParam;
import com.shfb.oframe.core.dao.domin.ResultParam;
import com.shfb.oframe.core.service.impl.ExecuteContext;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.exception.BusinessException;
import com.shfb.oframe.core.util.exception.CheckException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: helm
 * @description: 数据绑定查询
 * @author: fuzq
 * @create: 2018-09-13 17:25
 **/
@Service("mapBindQueryCmpt")
public class MapBindQueryCmpt extends AbstractComponent {

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        String mapDataIdA = executeParam.getStrValue("OpData.mapDataIdA");
        String mapLayerDefId = executeParam.getStrValue("OpData.mapLayerDefId");
        String mapDataRelCd = executeParam.getStrValue("OpData.mapDataRelCd");
        XmlBean xmlBean = new XmlBean();

        DaoParam daoParam = new DaoParam(MapBindSaveCmpt.REL_ENTITY_NAME);
        daoParam.addEqualCondition("mapDataIdA", mapDataIdA);
        daoParam.addEqualCondition("mapLayerRelDefId", mapLayerDefId);
        daoParam.addEqualCondition("mapDataRelCd", mapDataRelCd);
        daoParam.addQueryParams("mapDataIdZ");
        ResultParam resultParam = entityDao.queryPageData(daoParam);
        List<String> zIdList = new ArrayList<String>();
        if (resultParam.isHaveData()) {
            //处理
            for (int i = 0; i < resultParam.getValues().length; i++) {
                zIdList.add(resultParam.getValueLists()[0][i].toString());
            }
        }
        for (int i = 0; i < zIdList.size(); i++) {
            daoParam = new DaoParam(MapDateSaveCmpt.GEN_ENTITY_NAME);
            daoParam.addEqualCondition("mapDataId", zIdList.get(i));
            daoParam.addQueryParams("mapDataName");
            resultParam = entityDao.queryPageData(daoParam);
            if (resultParam.isHaveData()) {
                String mapDataName = resultParam.getStrValueByAttr("mapDataName");
                xmlBean.setStrValue("Rows.Row[" + i + "].mapDataId", zIdList.get(i));
                xmlBean.setStrValue("Rows.Row[" + i + "].mapDataName", mapDataName);
            }
        }
        return xmlBean;
    }
}