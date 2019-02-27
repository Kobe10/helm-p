package com.bjshfb.helm.controller;

import com.bjshfb.helm.cmpt.service.bind.MapBindSaveCmpt;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.service.bo.SvcRequest;
import com.shfb.oframe.core.util.service.bo.SvcResponse;
import com.shfb.oframe.core.web.controller.GroovyController;
import com.shfb.oframe.core.web.util.RequestUtil;
import com.shfb.oframe.core.web.util.ResponseUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
 * @description: 数据关联
 * @author: fuzq
 * @create: 2018-09-13 15:51
 **/
@Controller
@RequestMapping({"/shfb/layer/bind"})
public class MapBindController extends GroovyController {

    /**
     * @Description: TODO 数据绑定
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/13 16:58
     **/
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        // 数据A端
        String mapDataIdA = requestUtil.getStrParam("mapDataIdA");
        // 数据Z端
        String mapDataIdZ = requestUtil.getStrParam("mapDataIdZ");
        // 关联类型 0包含
        String mapDataRelCd = requestUtil.getStrParam("mapDataRelCd");
        // 关联Z端图层Id
        String mapLayerDefId = requestUtil.getStrParam("mapLayerDefId");
        if (!StringUtil.isNotEmptyOrNull(mapDataIdA)) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "关联数据A端数据不能为空");
            jsonObject.put("data", "");
            ResponseUtil.printAjaxJson(response, jsonObject);
            return;
        }
        if (!StringUtil.isNotEmptyOrNull(mapLayerDefId)) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "关联数据Z端图层Id不能为空");
            jsonObject.put("data", "");
            ResponseUtil.printAjaxJson(response, jsonObject);
            return;
        }


        XmlBean reqData = new XmlBean();
        String nodePath = "OpData.";
        reqData.setStrValue(nodePath + "mapDataIdA", mapDataIdA);
        reqData.setStrValue(nodePath + "mapDataIdZ", mapDataIdZ);
        reqData.setStrValue(nodePath + "mapDataRelCd", mapDataRelCd);
        reqData.setStrValue(nodePath + "mapLayerDefId", mapLayerDefId);

        svcRequest.addOp("MAP_BIND_SAVE_CMPT", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            jsonObject.put("success", true);
            jsonObject.put("errMsg", "");
            jsonObject.put("data", "");
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "关联数据保存失败");
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }

    /**
     * @Description: TODO 关联数据查询
     * @Param: [request, response, mapDataIdA]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/13 17:05
     **/
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String mapDataIdA = requestUtil.getStrParam("mapDataIdA");
        String mapLayerDefId = requestUtil.getStrParam("mapLayerDefId");
        String mapDataRelCd = requestUtil.getStrParam("mapDataRelCd");

        XmlBean reqData = new XmlBean();
        String nodePath = "OpData.";
        reqData.setStrValue(nodePath + "mapDataIdA", mapDataIdA);
        reqData.setStrValue(nodePath + "mapLayerDefId", mapLayerDefId);
        reqData.setStrValue(nodePath + "mapDataRelCd", mapDataRelCd);

        svcRequest.addOp("MAP_BIND_QUERY_CMPT", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            int num = 0;
            XmlBean dataXmlBean = new XmlBean();
            if (svcResponse.getFirstOpRsp("MAP_BIND_QUERY_CMPT").getBeanByPath("Operation.Rows") != null) {
                num = svcResponse.getFirstOpRsp("MAP_BIND_QUERY_CMPT").getListNum("Operation.Rows");
                dataXmlBean = svcResponse.getFirstOpRsp("MAP_BIND_QUERY_CMPT").getBeanByPath("Operation.Rows");
            }
            for (int i = 0; i < num; i++) {
                XmlBean xmlBeanRow = dataXmlBean.getBeanByPath("Rows.Row[" + i + "]");
                Map<String, String> map = new HashMap<String, String>();
                map.put("mapDataId", xmlBeanRow.getStrValue("Row.mapDataId"));
                map.put("mapDataName", xmlBeanRow.getStrValue("Row.mapDataName"));
                list.add(map);
            }
            JSONObject bindJson = new JSONObject();
            bindJson.put("bindData", list);
            jsonObject.put("success", true);
            jsonObject.put("errMsg", "");
            jsonObject.put("data", bindJson);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "关联数据查询失败");
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }
}