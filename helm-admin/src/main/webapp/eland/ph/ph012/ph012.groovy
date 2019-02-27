import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * Created by Administrator on 2015/12/14.
 * 关联房产 关系
 */
class ph012 extends GroovyController{
    /* 初始化进入页面 */
    public ModelAndView init(HttpServletRequest request,HttpServletResponse response){
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap =new ModelMap();
        String hsId = request.getParameter("hsId");
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            XmlBean reqCtData = new XmlBean();
            reqCtData.setStrValue("OpData.entityName", "HouseInfo");
            reqCtData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            reqCtData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            reqCtData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            reqCtData.setStrValue("OpData.ResultFields.fieldName[0]", "hsOwnerPersons");
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", reqCtData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    String hsOwnerPersons = queryResult.getStrValue("OpResult.PageData.Row[0].hsOwnerPersons");
                    modelMap.put("hsOwnerPersons", hsOwnerPersons);
                }
            }
        }
        return new ModelAndView("/eland/ph/ph012/ph012",modelMap);
    }

    /**
     * 编辑关系 弹框
     */
    public ModelAndView editRelDialog(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsCtReId = request.getParameter("hsCtReId");
        if (StringUtil.isNotEmptyOrNull(hsCtReId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.Conditions.Condition.fieldName", "hsCtReId");
            opData.setStrValue("OpData.Conditions.Condition.fieldValue", hsCtReId);
            opData.setStrValue("OpData.Conditions.Condition.operation", "=");
            svcRequest.addOp("QUERY_HSCTRE_INFO_CMPT", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_HSCTRE_INFO_CMPT");
                if (resultBean != "") {
                    XmlBean list = resultBean.getBeanByPath("Operation.OpResult.PageData.Row[0]");
                    modelMap.put("result", list.getRootNode());
                }
            }
        }
        return new ModelAndView("/eland/ph/ph012/ph012_dialog", modelMap);
    }

    /**
     * 获取房屋产权：根据产权人
     */
    public ModelAndView getHsByPsName(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsOwnerPersons = request.getParameter("hsOwnerPersons");
        String flag = request.getParameter("flag");
        modelMap.put("flag", flag);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        // 查询条件
        if(StringUtil.isNotEmptyOrNull(hsOwnerPersons)){
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsOwnerPersons");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "%${hsOwnerPersons}%");
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "like");
        }
        /*排序条件*/
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 设置分页
        opData.setStrValue(prePath + "PageInfo.pageSize", "10");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.ttOrgId");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.hsCtId");

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                for (int i = 0; i < rowCount; i++) {
                    String temp01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsId");
                    String temp02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsFullAddr");
                    String temp03 = queryResult.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsOwnerPersons");
                    String temp04 = queryResult.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.ttOrgId");
                    String temp05 = queryResult.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.HsCtInfo.hsCtId");
                    Map<String, String> temp = [
                            "hsId"          : temp01,
                            "hsFullAddr"    : temp02,
                            "hsOwnerPersons": temp03,
                            "ttOrgId"       : temp04,
                            "hsCtId"        : temp05,
                    ];
                    result.add(temp);
                }
                modelMap.put("result", result);
            }
        }
        return new ModelAndView("/eland/ph/ph012/ph012_hsTip", modelMap);
    }

    /**
     * 获取房屋产权：根据产权人
     */
    public void getHsRelByTwoCtId(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsOwnerPersons = request.getParameter("hsOwnerPersons");
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HsCtRelationInfo");
        // 查询条件
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsCtId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", request.getParameter("hsCtIdA"));
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "hsCtIdB");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", request.getParameter("hsCtIdB"));
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "relName");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "reNote");
        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                String temp01 = queryResult.getStrValue("OpResult.PageData.Row[0].relName");
                String temp02 = queryResult.getStrValue("OpResult.PageData.Row[0].reNote");
                String temp03 = queryResult.getStrValue("OpResult.PageData.Row[0].hsCtReId");

                ResponseUtil.print(response, """{"relName":"${temp01}","reNote":"${temp02}","hsCtReId":"${temp03}"}""");
            }
        }
        ResponseUtil.print(response, "{}");
    }

    /**
     * 保存关系
     */
    public void saveRel(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //接收参数
        String hsCtReId = request.getParameter("hsCtReId");
        String hsCtId = request.getParameter("hsCtIdA");
        String hsCtIdB = request.getParameter("hsCtIdB");
        String relName = request.getParameter("relName");
        String reNote = request.getParameter("reNote");
        if (StringUtil.isEmptyOrNull(hsCtReId)) {
            hsCtReId = "\${hsCtReId}";
        }
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.HsCtRelationInfo.hsCtReId", hsCtReId);
        opData.setStrValue("OpData.HsCtRelationInfo.hsCtId", hsCtId);
        opData.setStrValue("OpData.HsCtRelationInfo.hsCtIdB", hsCtIdB);
        opData.setStrValue("OpData.HsCtRelationInfo.relName", relName);
        opData.setStrValue("OpData.HsCtRelationInfo.reNote", reNote);
        svcRequest.addOp("SAVE_HSCTRE_INFO_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String jsonStr = """{success:${svcResponse.isSuccess()}, errMsg:"${svcResponse.getErrMsg()}" }""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 删除关系
     */
    public void deleteRel(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtReId = request.getParameter("hsCtReId");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HsCtRelationInfo");
        opData.setStrValue("OpData.entityKey", hsCtReId);
        svcRequest.addOp("DELETE_ENTITY_INFO", opData);

        SvcResponse svcResponse = transaction(svcRequest);
        String jsonStr = """{success:${svcResponse.isSuccess()}, errMsg:"${svcResponse.getErrMsg()}" }""";
        ResponseUtil.printAjax(response, jsonStr);
    }


}














