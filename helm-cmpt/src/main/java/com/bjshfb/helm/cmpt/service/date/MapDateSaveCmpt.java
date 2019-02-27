package com.bjshfb.helm.cmpt.service.date;

import com.bjshfb.helm.cmpt.common.EntityInfo;
import com.bjshfb.helm.util.EntityInfoUtils;
import com.bjshfb.helm.util.FileUtils;
import com.shfb.oframe.core.component.impl.AbstractComponent;
import com.shfb.oframe.core.dao.domin.DaoParam;
import com.shfb.oframe.core.dao.domin.ResultParam;
import com.shfb.oframe.core.service.impl.ExecuteContext;
import com.shfb.oframe.core.util.common.DateUtil;
import com.shfb.oframe.core.util.common.Encrypt;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.exception.BusinessException;
import com.shfb.oframe.core.util.exception.CheckException;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @program: helm
 * @description: 摄像头图层数据
 * @author: fuzq
 * @create: 2018-09-13 10:49
 **/
@Service("mapDateSaveCmpt")
public class MapDateSaveCmpt extends AbstractComponent {
    //数据类型
    public static String GEN_TYPE = "1";
    public static String IPC_TYPE = "2";
    /**
     * 通用实体名称
     */
    public static String GEN_ENTITY_NAME = "MapDataInfo";
    /**
     * 扩展实体名称
     */
    public static String IPC_ENTITY_NAME = "CamDataInfo";

    @Override
    public XmlBean execute(ExecuteContext executeContext, XmlBean executeParam) throws BusinessException, CheckException {
        //图层类型 ：1通用图层 2扩展图层
        String layperType = executeParam.getStrValue("OpData.layperType");
        //查询图层属性 拼接报文 调用保存服务 这里需要处理扩展实体（实体名称不同）
        String mapLayerDefId = executeParam.getStrValue("OpData.mapLayerDefId");
        //获取页面数据
        String data = executeParam.getStrValue("OpData.data");
        JSONObject dataObj = JSONObject.fromObject(data);

        JSONObject jsonObject;
        XmlBean opData = new XmlBean();
        jsonObject = queryAttrDef(mapLayerDefId);
        Map<String, Object> map;
        if (jsonObject != null) {
            map = handleXml(executeContext, jsonObject, dataObj, layperType, mapLayerDefId);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                opData.setStrValue(entry.getKey(), entry.getValue());
            }
        }
        return opData;
    }

    /**
     * @Description: TODO 查询当前图层的属性 json格式
     * @Param: [mapLayerDefId]
     * @return: net.sf.json.JSONObject
     * @Author: fuzq
     * @Date: 2018/9/13 11:13
     **/
    public JSONObject queryAttrDef(String mapLayerDefId) {
        JSONObject jsonObject = new JSONObject();
        //查询该户下产权单位的户
        DaoParam daoParam = new DaoParam("MapLayerDef");
        daoParam.addEqualCondition("mapLayerDefId", mapLayerDefId);
        daoParam.addQueryParams("attrDef");
        ResultParam resultParam = entityDao.queryPageData(daoParam);
        if (resultParam.isHaveData()) {
            String s = resultParam.getStrValueByAttr("attrDef");
            jsonObject = JSONObject.fromObject(s);
        }
        return jsonObject;
    }

    /**
     * @Description: TODO 拼接报文 处理通用图层或者扩展图层
     * @Param: [jsonObject, executeParam, type]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/13 11:55
     **/
    public Map<String, Object> handleXml(ExecuteContext executeContext, JSONObject jsonObject, JSONObject dataObj, String type, String mapLayerDefId) throws CheckException, BusinessException {
        Map<String, Object> map = new HashMap<String, Object>();
        XmlBean opData = new XmlBean();
        XmlBean genOpData = new XmlBean();
        String preDate = "OpData.";
        if (queryAttrDef(mapLayerDefId) != null) {
            //通用字段的维护  图层基本数据ID
            String mapDataId = dataObj.getString("mapDataId");

            if (!StringUtil.isNotEmptyOrNull(mapDataId)) {
                mapDataId = "${mapDataId}";
            }
            genOpData.setStrValue(preDate + "mapDataId", mapDataId);
            genOpData.setStrValue(preDate + "mapLayerDefId", mapLayerDefId);
            //名称 地址  如果是通用类型
            if (StringUtil.isNotEmptyOrNull(dataObj.getString("mapDataName"))) {
                genOpData.setStrValue(preDate + "mapDataName", dataObj.getString("mapDataName"));
            }
            if (StringUtil.isNotEmptyOrNull(dataObj.getString("mapDataAddr"))) {
                genOpData.setStrValue(preDate + "mapDataAddr",  dataObj.getString("mapDataAddr"));
            }
            genOpData.setStrValue(preDate + "createDate", DateUtil.getSysDate());

            //通用保存
            if (StringUtil.isEqual(type, GEN_TYPE)) {
                //遍历图层属性 拼接报文
                Iterator iterator = jsonObject.keys();
                int i = 0;
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    String value = dataObj.getString(jsonObject.getString(key));
                    if (StringUtil.isNotEmptyOrNull(value)) {
                        //拼接伪属性
                        genOpData.setStrValue(preDate + "CamDataRhts.CamDataRht[" + i +"].dataAttrCd", jsonObject.getString(key));
                        genOpData.setStrValue(preDate + "CamDataRhts.CamDataRht[" + i +"].dataAttrValue", value);
                        i += 1;
                    }
                }
            }
            //保存通用信息
            Object genObject = this.saveEntityInfo(executeContext, GEN_ENTITY_NAME, genOpData);
            //扩展实体保存
            if (StringUtil.isEqual(type, IPC_TYPE)) {
                //获取扩展实体属性
                EntityInfo entityInfo = EntityInfoUtils.getEntityInfo(IPC_TYPE);
                if (entityInfo != null) {
                    String expanId = "";
                    //扩展实体Id 通过通用数据编码 查找扩展实体Id
                    DaoParam daoParam = new DaoParam(entityInfo.getEntityName());
                    daoParam.addEqualCondition(entityInfo.getEntityCd(), genObject.toString());
                    daoParam.addQueryParams(entityInfo.getEntityId());
                    ResultParam resultParam = entityDao.queryPageData(daoParam);
                    if (resultParam.isHaveData()) {
                        expanId = resultParam.getStrValueByAttr(entityInfo.getEntityId());
                    }
                    if (!StringUtil.isNotEmptyOrNull(expanId)) {
                        expanId = "${expanId}";
                    }
                    opData.setStrValue(preDate + entityInfo.getEntityId(), expanId);
                    opData.setStrValue(preDate + entityInfo.getEntityCd(), genObject.toString());
                    //遍历图层属性 拼接报文
                    Iterator iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        String value = dataObj.getString(jsonObject.getString(key));
                        if (StringUtil.isNotEmptyOrNull(value)) {
                            //这里对IPC的摄像头密码进行加密处理
                            if (StringUtil.isEqual("camPwd", jsonObject.getString(key))) {
                                String pwssWd = Encrypt.encryptToDES(value);
                                opData.setStrValue(preDate + jsonObject.getString(key), pwssWd);
                            } else {
                                opData.setStrValue(preDate + jsonObject.getString(key), value);
                            }
                        }
                    }
                    //保存扩展实体
                    Object ipcObject = this.saveEntityInfo(executeContext, entityInfo.getEntityName(), opData);
                } else {
                    //扩展实体配置文件为空 请确认配置文件
                }
            }
            map.put("mapDataId", genObject);
        }
        return map;
    }
}