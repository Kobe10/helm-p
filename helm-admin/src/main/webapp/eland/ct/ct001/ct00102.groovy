import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import groovy.json.JsonSlurper

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * User: shfb_linql
 * Date: 2016/1/29 0029 11:23
 * Copyright(c) 北京四海富博计算机服务有限公司
 *
 * 独立选房套餐处理控制层，gv。方便临时修改。
 */
class ct00102 extends GroovyController {

    /**
     *
     * @param request
     * @param response
     */
    public void cfmProd(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String typeId = request.getParameter("lockType");
        String sltPrd = request.getParameter("sltPrd");
        def jsonSlurper = new JsonSlurper()
        Map<String, String> sltPrdJson = jsonSlurper.parseText(sltPrd);

        // 更新可用房源数据
        String nodePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(nodePath + "typeId", typeId);
        for (Map.Entry<String, String> mapItem : sltPrdJson.entrySet()) {
            if (!StringUtil.isEqual("prdCd", mapItem.getKey())) {
                opData.setStrValue(nodePath + "LockEntityInfos.Used." + mapItem.getKey(), mapItem.getValue());
            }
        }
        svcRequest.addOp("UPDATE_LOCK_DATA_INFO", opData);

        // 更新选房数据信息
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HsCtInfo");
        opData.setStrValue("OpData.EntityData.hsCtId", hsCtId);
        opData.setStrValue("OpData.EntityData.cfmChsProdStatus", "2");
        opData.setStrValue("OpData.EntityData.cfmChsProdDate", DateUtil.toStringYmdHmsWthHS(DateUtil.getSysDate()));
        for (Map.Entry<String, String> mapItem : sltPrdJson.entrySet()) {
            if (!StringUtil.isEqual("prdCd", mapItem.getKey())) {
                opData.setStrValue(nodePath + "EntityData.chp" + mapItem.getKey(), mapItem.getValue());
            } else {
                opData.setStrValue(nodePath + "EntityData.cfmChsProdCd", mapItem.getValue());
            }
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        //
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     *
     * @param request
     * @param response
     */
    public void cancelProd(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String typeId = request.getParameter("lockType");
        String sltPrd = request.getParameter("sltPrd");
        def jsonSlurper = new JsonSlurper()
        Map<String, String> sltPrdJson = jsonSlurper.parseText(sltPrd);

        // 更新可用房源数据
        String nodePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(nodePath + "typeId", typeId);
        for (Map.Entry<String, String> mapItem : sltPrdJson.entrySet()) {
            if (!StringUtil.isEqual("prdCd", mapItem.getKey())) {
                opData.setStrValue(nodePath + "LockEntityInfos.Release." + mapItem.getKey(), mapItem.getValue());
            }
        }
        svcRequest.addOp("UPDATE_LOCK_DATA_INFO", opData);

        // 更新选房数据信息
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HsCtInfo");
        opData.setStrValue("OpData.EntityData.hsCtId", hsCtId);
        opData.setStrValue("OpData.EntityData.cfmChsProdStatus", "1");
        for (Map.Entry<String, String> mapItem : sltPrdJson.entrySet()) {
            if (!StringUtil.isEqual("prdCd", mapItem.getKey())) {
                opData.setStrValue(nodePath + "EntityData.chp" + mapItem.getKey(), mapItem.getValue());
            } else {
                opData.setStrValue(nodePath + "EntityData.cfmChsProdCd", mapItem.getValue());
            }
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
}