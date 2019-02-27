package com.bjshfb.helm.cmpt.service.layer;

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
 * @description: 删除图层
 * @author: fuzq
 * @create: 2018-09-11 16:18
 **/
@Service("mapLayerDelCmpt")
public class MapLayerDelCmpt extends AbstractComponent {
    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        String mapLayerDefIds = executeParam.getStrValue("OpData.mapLayerDefId");
        // 图层id串
        List<String> result = new ArrayList<String>();
        if (StringUtil.isNotEmptyOrNull(mapLayerDefIds)) {
            result = Arrays.asList(mapLayerDefIds.split(","));
        }
        // 错误信息
        String errStr = "";
        // 数据校验
        errStr = checkData(result);
        // 是否存在图层数据
        if (!StringUtil.isNotEmptyOrNull(errStr)) {
            //删除图层
            for (String str : result) {
                this.deleteEntity("MapLayerDef", str);
            }
        } else {
            errStr = "错误信息:" + errStr + "存在数据关联或者存在图层数据";
        }
        XmlBean xmlBean = new XmlBean();
        xmlBean.setStrValue("OpData.errMsg", errStr);
        return xmlBean;
    }

    /**
     * @Description: TODO 验证是否存在图层数据
     * @Param: [list]
     * @return: java.lang.String
     * @Author: fuzq
     * @Date: 2018/9/11 16:23
     **/
    public String checkData(List<String> list) {
        StringBuilder dateBuilder = new StringBuilder();
        StringBuilder bindBuilder = new StringBuilder();
        for (String str : list) {
            // 判断是否存在图层数据
            DaoParam daoParam = new DaoParam("MapDataInfo");
            daoParam.addEqualCondition("mapLayerDefId", str);
            daoParam.addQueryParams("mapDataId");
            ResultParam resultParam = entityDao.queryPageData(daoParam);
            // 存在图层数据 返回当前图层名称
            if (resultParam.isHaveData()) {
                dateBuilder.append(str + ",");
            }

            // 判断是否关联A 端
            DaoParam daoParam1 = new DaoParam("MapDataRel");
            daoParam1.addEqualCondition("mapDataIdA", str);
            daoParam1.addQueryParams("mapDataRelId");
            ResultParam resultParam1 = entityDao.queryPageData(daoParam1);
            if (resultParam1.isHaveData()) {
                bindBuilder.append(str + ",");
            }

            // 判断是否关联Z 端
            DaoParam daoParam2 = new DaoParam("MapDataRel");
            daoParam2.addEqualCondition("mapDataIdZ", str);
            daoParam2.addQueryParams("mapDataRelId");
            ResultParam resultParam2 = entityDao.queryPageData(daoParam2);
            if (resultParam2.isHaveData() || resultParam1.isHaveData()) {
                bindBuilder.append(str + ",");
            }
        }
        String errMsg = bindBuilder.toString() + dateBuilder.toString();
        return errMsg;
    }
}