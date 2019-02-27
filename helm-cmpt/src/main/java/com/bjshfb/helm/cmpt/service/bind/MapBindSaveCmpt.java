package com.bjshfb.helm.cmpt.service.bind;

import com.bjshfb.helm.cmpt.service.date.MapDateSaveCmpt;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: helm
 * @description: 数据关联保存
 * @author: fuzq
 * @create: 2018-09-13 16:21
 **/
@Service("mapBindSaveCmpt")
public class MapBindSaveCmpt extends AbstractComponent {
    public static String REL_ENTITY_NAME = "MapDataRel";

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        //删除所有 然后进行保存
        String mapDataIdA = executeParam.getStrValue("OpData.mapDataIdA");
        String mapDataIdZ = executeParam.getStrValue("OpData.mapDataIdZ");
        String mapDataRelCd = executeParam.getStrValue("OpData.mapDataRelCd");
        String mapLayerDefId = executeParam.getStrValue("OpData.mapLayerDefId");

        XmlBean xmlBean = new XmlBean();

        //批量操作

        List<String> zIdList = queryZIds(mapDataIdA, mapLayerDefId);
        //批量删除数据
        for (String id : zIdList) {
            this.deleteEntity(REL_ENTITY_NAME, id);
        }
        //增加关联关系
        if (StringUtil.isNotEmptyOrNull(mapDataIdZ)) {
            List<String> listZ = new ArrayList<String>();
            if (StringUtil.isNotEmptyOrNull(mapDataIdZ)) {
                listZ = Arrays.asList(mapDataIdZ.split(","));
            }
            for (String id : listZ) {
                XmlBean opData = new XmlBean();
                String preDate = "OpData.";
                opData.setStrValue(preDate + "mapDataRelId", "${mapDataRelId" + id + "}");
                opData.setStrValue(preDate + "mapDataIdA", mapDataIdA);
                opData.setStrValue(preDate + "mapLayerRelDefId", mapLayerDefId);
                opData.setStrValue(preDate + "mapDataIdZ", id);
                opData.setStrValue(preDate + "mapDataRelCd", mapDataRelCd);
                this.saveEntityInfo(executeContext, REL_ENTITY_NAME, opData);
            }
        }
        return xmlBean;
    }

    /**
     * @Description: TODO 查询当前图层数据关联的所有Z端数据
     * @Param: [mapLayerDefId]
     * @return: net.sf.json.JSONObject
     * @Author: fuzq
     * @Date: 2018/9/13 11:13
     **/
    public List<String> queryZIds(String mapDataIdA, String mapLayerDefId) {
        DaoParam daoParam = new DaoParam(REL_ENTITY_NAME);
        daoParam.addEqualCondition("mapDataIdA", mapDataIdA);
        daoParam.addEqualCondition("mapLayerRelDefId", mapLayerDefId);
        daoParam.addQueryParams("mapDataRelId");
        ResultParam resultParam = entityDao.queryPageData(daoParam);
        List<String> zIdList = new ArrayList<String>();
        if (resultParam.isHaveData()) {
            for (int i = 0; i < resultParam.getValues().length; i++) {
                zIdList.add(resultParam.getValueLists()[0][i].toString());
            }
        }
        return zIdList;
    }
}
