package com.bjshfb.helm.controller;

import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.service.bo.SvcRequest;
import com.shfb.oframe.core.util.service.bo.SvcResponse;
import com.shfb.oframe.core.web.controller.GroovyController;
import com.shfb.oframe.core.web.util.RequestUtil;
import com.shfb.oframe.core.web.util.ResponseUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: helm
 * @description: 图层定义控制层
 * @author: fuzq
 * @create: 2018-09-11 14:36
 **/
@Controller
@RequestMapping({"/shfb/layer/define"})
public class LayerDefineController extends GroovyController {

    /**
     * @Description: TODO 新增修改图层
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/11 14:42
     **/
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        // 图层编号
        String mapLayerDefId = requestUtil.getStrParam("mapLayerDefId");
        // 图层图标
        String layIconDocId = requestUtil.getStrParam("layIconDocId");
        // 显示图标
        String showIconDocId = requestUtil.getStrParam("showIconDocId");
        // 图层类型
        String mapLayerType = requestUtil.getStrParam("mapLayerType");
        // 图层样式 html格式
        String showStyle = requestUtil.getStrParam("showStyle");
        // 图层属性 json格式
        String attrDef = requestUtil.getStrParam("attrDef");

        XmlBean reqData = new XmlBean();

        String nodePath = "OpData.";
        reqData.setStrValue(nodePath + "mapLayerDefId", mapLayerDefId);
        reqData.setStrValue(nodePath + "layIconDocId", layIconDocId);
        reqData.setStrValue(nodePath + "showIconDocId", showIconDocId);
        reqData.setStrValue(nodePath + "mapLayerType", mapLayerType);
        reqData.setStrValue(nodePath + "showStyle", showStyle);
        reqData.setStrValue(nodePath + "attrDef", attrDef);

        svcRequest.addOp("MAP_LAYER_SAVE_CMPT", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            jsonObject.put("success", true);
            jsonObject.put("errMsg", "");
            jsonObject.put("data", "");
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "保存图层属性错误");
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }

    /**
     * @Description: TODO 删除图层 （支持批量）
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/11 14:43
     **/
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        JSONObject jsonObject = new JSONObject();

        String mapLayerDefIds = requestUtil.getStrParam("mapLayerDefIds");
        XmlBean reqData = new XmlBean();
        String nodePath = "OpData.";
        reqData.setStrValue(nodePath + "mapLayerDefId", mapLayerDefIds);
        svcRequest.addOp("LAYER_DEL_ENTITY", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //判断是否有错误信息
            String errMsg = "";
            if (StringUtil.isNotEmptyOrNull(svcResponse.getFirstOpRsp("LAYER_DEL_ENTITY").getStrValue("Operation.OpData.errMsg"))) {
                errMsg = svcResponse.getFirstOpRsp("LAYER_DEL_ENTITY").getStrValue("Operation.OpData.errMsg");
            }
            jsonObject.put("success", true);
            jsonObject.put("errMsg", errMsg);
            jsonObject.put("data", "");
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "删除图层错误");
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }

    /**
     * @Description: TODO 验证权限
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/11 17:09
     **/
    @RequestMapping(value = "/rthCheck", method = RequestMethod.GET)
    public void rthCheck(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.rhtCd", "Layer_limit_v");
        svcRequest.setValue("Request.SvcCont.rightType", "1");
        SvcResponse svcResponse = callService("staffService", "queryStaffHaveRht", svcRequest);
        boolean isHaveRht = false;
        if (svcResponse.isSuccess()) {
            String path = "Response.SvcCont.haveRight";
            isHaveRht = Boolean.valueOf(svcResponse.getStrValue(path));
        }
        JSONObject result = new JSONObject();
        result.put("success", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());
        result.put("isHaveRht", isHaveRht);
        ResponseUtil.printAjax(response, result);
    }

    /**
     * @Description: TODO 查询图层 权限验证
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/11 17:09
     **/
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        JSONObject jsonObject = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        XmlBean reqData = new XmlBean();
        String keyWord = requestUtil.getStrParam("keyWord");
        String mapLayerDefId = requestUtil.getStrParam("mapLayerDefId");
        // 1 表示初始化地图查询  2表示图层快捷查询
        String type = requestUtil.getStrParam("type");
        if (!StringUtil.isNotEmptyOrNull(type)) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "查询类型不能为空");
            jsonObject.put("data", "");
            ResponseUtil.printAjaxJson(response, jsonObject);
        }
        String nodePath = "OpData.";
        reqData.setStrValue(nodePath + "keyWord", keyWord);
        reqData.setStrValue(nodePath + "mapLayerDefId", mapLayerDefId);

        svcRequest.addOp("MAP_LAYER_QUERY_ENTITY", reqData);
        SvcResponse svcResponse = query(svcRequest);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (svcResponse.isSuccess()) {
            //处理图层全部数据
            int num = 0;
            XmlBean dataXmlBean = new XmlBean();
            if (svcResponse.getFirstOpRsp("MAP_LAYER_QUERY_ENTITY").getBeanByPath("Operation.OpResult.PageData") != null) {
                num = svcResponse.getFirstOpRsp("MAP_LAYER_QUERY_ENTITY").getBeanByPath("Operation.OpResult.PageData").getListNum("PageData.Row");
                dataXmlBean = svcResponse.getFirstOpRsp("MAP_LAYER_QUERY_ENTITY").getBeanByPath("Operation.OpResult.PageData");
            }
            // 初始化查询图层数据信息
            if (StringUtil.isNotEmptyOrNull(type) && StringUtil.isEqual("1", type)) {
                for (int i = 0; i < num; i++) {
                    XmlBean xmlBeanRow = dataXmlBean.getBeanByPath("PageData.Row[" + i + "]");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("mapLayerDefId", xmlBeanRow.getStrValue("Row.mapLayerDefId"));
                    map.put("mapLayerDefCd", xmlBeanRow.getStrValue("Row.mapLayerDefCd"));
                    map.put("layIconDocId", xmlBeanRow.getStrValue("Row.layIconDocId"));
                    map.put("mapLayerType", xmlBeanRow.getStrValue("Row.mapLayerType"));
                    map.put("showIconDocId", xmlBeanRow.getStrValue("Row.showIconDocId"));
                    map.put("showStyle", xmlBeanRow.getStrValue("Row.showStyle"));
                    map.put("attrDef", xmlBeanRow.getStrValue("Row.attrDef"));
                    map.put("mapLayerDefName", xmlBeanRow.getStrValue("Row.mapLayerDefName"));
                    list.add(map);
                }
            } else if (StringUtil.isNotEmptyOrNull(mapLayerDefId)) {
                //根据Id查询单一图层数据
                for (int i = 0; i < num; i++) {
                    XmlBean xmlBeanRow = dataXmlBean.getBeanByPath("PageData.Row[" + i + "]");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("mapLayerDefId", xmlBeanRow.getStrValue("Row.mapLayerDefId"));
                    map.put("mapLayerDefCd", xmlBeanRow.getStrValue("Row.mapLayerDefCd"));
                    map.put("layIconDocId", xmlBeanRow.getStrValue("Row.layIconDocId"));
                    map.put("showMinLevel", xmlBeanRow.getStrValue("Row.showMinLevel"));
                    map.put("showMaxLevel", xmlBeanRow.getStrValue("Row.showMaxLevel"));
                    map.put("showStyle", xmlBeanRow.getStrValue("Row.showStyle"));
                    map.put("attrDef", xmlBeanRow.getStrValue("Row.attrDef"));
                    map.put("mapLayerType", xmlBeanRow.getStrValue("Row.mapLayerType"));
                    map.put("showIconDocId", xmlBeanRow.getStrValue("Row.showIconDocId"));
                    map.put("mapLayerDefName", xmlBeanRow.getStrValue("Row.mapLayerDefName"));
                    list.add(map);
                }
            } else if (StringUtil.isNotEmptyOrNull(type) && StringUtil.isEqual("2", type)) {
                for (int i = 0; i < num; i++) {
                    XmlBean xmlBeanRow = dataXmlBean.getBeanByPath("PageData.Row[" + i + "]");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("mapLayerDefId", xmlBeanRow.getStrValue("Row.mapLayerDefId"));
                    map.put("mapLayerType", xmlBeanRow.getStrValue("Row.mapLayerType"));
                    map.put("mapLayerDefName", xmlBeanRow.getStrValue("Row.mapLayerDefName"));
                    list.add(map);
                }
            }
            jsonObject.put("success", true);
            jsonObject.put("errMsg", "");
            JSONObject layerJson = new JSONObject();
            layerJson.put("layerInfo", list);
            jsonObject.put("data", layerJson);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "");
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }
}