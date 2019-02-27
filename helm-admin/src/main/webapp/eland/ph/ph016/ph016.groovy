import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房  房产信息展示页面
 */
class ph016 extends GroovyController {
    /**
     * 初始化标记
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {

        //1-初始化单户  2-初始化批量
        String signType = request.getParameter("signType");
        //hsId 多个用逗号隔开
        String hsId = request.getParameter("hsId");
        ModelMap modelMap = new ModelMap();
        //1.查询标签定义
        XmlBean signInfoXml = new XmlBean("<OpResult/>")
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean("<OpData/>");
        svcRequest.addOp("QUERY_SIGN_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultXml = svcResponse.getFirstOpRsp("QUERY_SIGN_CMPT");
            if (resultXml != null) {
                signInfoXml = resultXml.getBeanByPath("Operation.OpResult");
            }
        }
        List<Map<String, XmlNode>> signInfos = formatResult(signInfoXml);
        modelMap.put("signInfos", signInfos);
        if (StringUtil.isEqual("1", signType)) {
            //单户标记
            //2.查询已选标签
            XmlBean signRelInfo = new XmlBean("<OpResult/>")
            opData = new XmlBean("<OpData/>");
            opData.setStrValue("OpData.relId", hsId);
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_REL_SIGN_CMPT", opData);
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean resultXml = svcResponse.getFirstOpRsp("QUERY_REL_SIGN_CMPT");
                if (resultXml != null) {
                    signRelInfo = resultXml.getBeanByPath("Operation.OpResult");
                }
            }
            List<Map<String, XmlNode>> signRelInfos = formatResult(signRelInfo);
            modelMap.put("signRelInfos", signRelInfos);
            //3.查询房信息
            XmlBean houseInfo = new XmlBean("<Row/>");
            opData = new XmlBean("<OpData/>");
            opData.setStrValue("OpData.entityName", "HouseInfo");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "hsCd");
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "hsOwnerPersons");
            //调用服务
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean resultXml = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA");
                if (resultXml != null) {
                    XmlBean rowXml = resultXml.getBeanByPath("Operation.OpResult.PageData.Row[0]");
                    if (rowXml != null) {
                        houseInfo = rowXml;
                    }
                }
            }
            houseInfo.rename("Row", "HouseInfo");
            modelMap.put("houseInfo", houseInfo.getRootNode());
        }
        modelMap.put("signType", signType);
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ph/ph016/sign", modelMap);
    }

    /** 独立对话框显示 staff 树  */
    public ModelAndView initPbTree(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("objName", request.getParameter("objName"));
        modelMap.put("objValue", request.getParameter("objValue"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        String prjCd = "0";
        if (request.getParameter("prjCd") != null) {
            prjCd = request.getParameter("prjCd");
        }
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ph/ph016/pbTree", modelMap);
    }

    /**
     * 问题选择目录树
     * @param request 请求信息
     * @param response 响应信息
     */
    public void pbTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取问题参数
        String prjCd = request.getParameter("prjCd");
        String checkedIdStr = request.getParameter("checkedIds");
        String hsOwnerType = request.getParameter("hsOwnerType");
        if (StringUtil.isEqual(hsOwnerType, "101") || StringUtil.isEqual(hsOwnerType, "102")) {
            //私房
            hsOwnerType = "1";
        } else if (StringUtil.isEqual(hsOwnerType, "201")) {
            //直管公房
            hsOwnerType = "2"
        } else if (StringUtil.isEqual(hsOwnerType, "202")) {
            //自管公房
            hsOwnerType = "3";
        } else if (StringUtil.isEqual(hsOwnerType, "7")) {
            //其它
            hsOwnerType = "4";
        } else {
            //全部
            hsOwnerType = "all";
        }
        if (StringUtil.isEmptyOrNull(prjCd)) {
            prjCd = "0";
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        reqData.setStrValue(nodePath + "itemCd", "PROBLEM_TREE");
        reqData.setValue(nodePath + "prjCd", prjCd);
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        StringBuilder sb = new StringBuilder();
        if (svcResponse.isSuccess()) {
            XmlBean valuesXml = svcResponse.getBeanByPath("Response.SvcCont.SysCfgs.SysCfg.Values");
            if (valuesXml != null) {
                int listNum = valuesXml.getListNum("Values.Value");
                List<String> checkedIds = new ArrayList<>();
                if (StringUtil.isNotEmptyOrNull(checkedIdStr)) {
                    checkedIds = Arrays.asList(checkedIdStr.split(","));
                }
                //处理pid
                Map<String, String> idMap = new HashMap<>();
                for (int i = 0; i < listNum; i++) {
                    XmlBean valueXml = valuesXml.getBeanByPath("Values.Value[" + i + "]");
                    String id = valueXml.getStrValue("Value.valueCd");
                    String pId = "";
                    if (StringUtil.isEqual(id, "100")) {
                        pId = "";
                    } else if (id.length() > 3) {
                        pId = id.substring(0, id.length() - 3);
                    }
                    idMap.put(id, pId);
                }
                for (int i = 0; i < listNum; i++) {
                    XmlBean valueXml = valuesXml.getBeanByPath("Values.Value[" + i + "]");
                    String id = valueXml.getStrValue("Value.valueCd");
                    String name = valueXml.getStrValue("Value.valueName");
                    String note = valueXml.getStrValue("Value.notes");
                    if (StringUtil.isNotEmptyOrNull(note)) {
                        JSONObject noteObj = JSONObject.parseObject(note);
                        String hsOwnerStr = StringUtil.obj2Str(noteObj.get("code"));
                        List<String> hsOwnerTypes = Arrays.asList(hsOwnerStr.split(","));
                        if (!StringUtil.isEqual(hsOwnerType, "all") && !hsOwnerTypes.contains(hsOwnerType)) {
                            continue;
                        }
                    }
                    String pId = idMap.get(id);
                    String isCheckBox = "";
                    if (idMap.values().contains(id)) {
                        isCheckBox = "true";
                    } else {
                        isCheckBox = "false";
                    }
                    String isCheck = "";
                    if (checkedIds.contains(id)) {
                        isCheck = "true";
                    } else {
                        isCheck = "false";
                    }
                    sb.append("""{ id: "${id}", iconSkin: "",nocheck:"${isCheckBox}",checked: "${isCheck}", pId: "${
                        pId
                    }", name: "${
                        name
                    }",open: "true"},""");
                }
            }
        }

        String jsonStr = """{success: "true", errMsg: "", resultMap: {treeJson: [${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 保存标签定义
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView
     */
    public void saveSignDef(HttpServletRequest request, HttpServletResponse response) {
        //1.解析入参 json->xml
        String signJson = request.getParameter("request");
        XmlBean opData = new XmlBean("<OpData/>");
        XmlBean tagXml = new XmlBean("<Tag/>");
        if (StringUtil.isNotEmptyOrNull(signJson)) {
            JSONObject signObj = JSONObject.parseObject(signJson);
            String tagId = signObj.getString("tagId");
            String tagName = signObj.getString("tagName");
            //标签类型 0-系统级 1-个人级
            String tagType = signObj.getString("tagType");
            //是否多选 true-多选 false-单选
            String isCheckbox = signObj.getString("isCheckbox");
            if (StringUtil.isEmptyOrNull(tagType)) {
                tagType = "0";
            }
            tagXml.setStrValue("Tag.tagId", tagId);
            tagXml.setStrValue("Tag.tagName", tagName);
            tagXml.setStrValue("Tag.tagType", tagType);
            //默认关联类型为100，hsId
            tagXml.setStrValue("Tag.relType", "100");
            tagXml.setStrValue("Tag.isCheckbox", isCheckbox);
            //子标签
            JSONArray subTags = signObj.getJSONArray("subTags");
            XmlBean subTagsXml = new XmlBean("<SubTags/>")
            for (int i = 0; i < subTags.size(); i++) {
                XmlBean subTagXml = new XmlBean("<Tag/>");
                JSONObject subTag = subTags.getJSONObject(i);
                String subTagId = subTag.getString("tagId");
                String subTagName = subTag.getString("tagName");
                subTagXml.setStrValue("Tag.tagId", subTagId);
                subTagXml.setStrValue("Tag.tagName", subTagName);
                subTagsXml.setBeanByPath("SubTags", subTagXml);
            }
            tagXml.setBeanByPath("Tag", subTagsXml);
        }
        opData.setBeanByPath("OpData", tagXml);
        //2.调用保存服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("SAVE_SIGN_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        //3.返回结果
        JSONObject resultObj = new JSONObject();
        if (svcResponse.isSuccess()) {
            resultObj.put("isSuccess", true);
            resultObj.put("errMsg", "");
            XmlBean resultXml = svcResponse.getFirstOpRsp("SAVE_SIGN_CMPT");
            if (resultXml != null) {
                XmlBean rTagXml = resultXml.getBeanByPath("Operation.OpResult.Tag");
                resultObj.put("opResult", getJson(rTagXml));
            }
        } else {
            resultObj.put("isSuccess", false);
            resultObj.put("errMsg", svcResponse.getErrMsg());
            resultObj.put("opResult", "");
        }
        ResponseUtil.printAjax(response, resultObj);
    }

    /**
     * 删除标签定义
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView
     */
    public void deleteSignDef(HttpServletRequest request, HttpServletResponse response) {
        String tagId = request.getParameter("tagId");
        XmlBean opData = new XmlBean("<OpData/>");
        opData.setStrValue("OpData.tagId", tagId);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("DELETE_SIGN_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        JSONObject resultObj = new JSONObject();
        resultObj.put("isSuccess", svcResponse.isSuccess());
        resultObj.put("opResult", "");
        if (svcResponse.isSuccess()) {
            resultObj.put("errMsg", "");
        } else {
            String errMsg = svcResponse.getErrMsg();
            if (StringUtil.isEmptyOrNull(errMsg)) {
                errMsg = "删除标签失败！"
            }
            resultObj.put("errMsg", errMsg);
        }
        ResponseUtil.printAjax(response, resultObj);
    }

    /**
     * 绑定、解绑、保存关联标签
     * @param request 请求信息
     * @param response 响应信息
     * @return void
     */
    public void relSignManager(HttpServletRequest request, HttpServletResponse response) {
        //1.拼接入参
        XmlBean opData = new XmlBean("<OpData/>");
        String data = request.getParameter("request");
        JSONObject dataObj = JSONObject.parseObject(data);
        //0-绑定 1-解绑 2-全量保存
        opData.setStrValue("OpData.bindingFlag", dataObj.getString("bindingFlag"));
        //relId
        JSONArray relIds = dataObj.getJSONArray("relIds");
        XmlBean relIdXml = new XmlBean("<RelId/>")
        for (int i = 0; i < relIds.size(); i++) {
            relIdXml.setStrValue("RelId.relId[" + i + "]", relIds.getString(i));
        }
        opData.setBeanByPath("OpData", relIdXml);
        //tagId
        JSONArray tagIds = dataObj.getJSONArray("tagIds");
        XmlBean tagIdXml = new XmlBean("<TagId/>")
        for (int i = 0; i < tagIds.size(); i++) {
            tagIdXml.setStrValue("TagId.tagId[" + i + "]", tagIds.getString(i));
        }
        opData.setBeanByPath("OpData", tagIdXml);
        //2.调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("BINDING_MANAGER_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        //返回结果
        JSONObject resultObj = new JSONObject();
        resultObj.put("isSuccess", svcResponse.isSuccess());
        resultObj.put("opResult", "");
        resultObj.put("errMsg", svcResponse.getErrMsg());
        ResponseUtil.printAjax(response, resultObj);
    }

    /**
     * 适配前端el获取
     * @param tagsXml
     * @return
     */
    private List<Map<String, Object>> formatResult(XmlBean tagsXml) {
        List<Map<String, Object>> result = new ArrayList<>();
        int tagNum = tagsXml.getListNum("OpResult.Tag");
        for (int i = 0; i < tagNum; i++) {
            Map<String, Object> tagMap = new LinkedHashMap<>();
            XmlBean tagXml = tagsXml.getBeanByPath("OpResult.Tag[" + i + "]");
            int subNum = tagXml.getListNum("Tag.SubTags.Tag");
            List<XmlNode> subTags = new ArrayList<>();
            for (int j = 0; j < subNum; j++) {
                XmlBean subXml = tagXml.getBeanByPath("Tag.SubTags.Tag[" + j + "]")
                subTags.add(subXml.getRootNode());
            }
            tagMap.put("subTags", subTags);
            tagXml.removeNode("Tag.SubTags");
            tagMap.put("tags", tagXml.getRootNode());
            result.add(tagMap);
        }
        return result;
    }

    /**
     * 处理子节点一个和多个xml转json(一个JsonObject，多个JsonArray)
     * @param tagsXml
     * @return
     */
    private String getJson(XmlBean tagXml) {
        int subNum = tagXml.getListNum("Tag.SubTags.Tag");
        if (subNum > 1) {
            return tagXml.toJson();
        } else if (subNum == 1) {
            String tempJson = tagXml.toJson();
            JSONObject tempObj = JSONObject.parseObject(tempJson);
            JSONObject tagObj = tempObj.getJSONObject("Tag");
            JSONObject subObj = tagObj.getJSONObject("SubTags");
            JSONObject subTagObj = subObj.getJSONObject("Tag");
            JSONArray subArr = new JSONArray();
            subArr.add(subTagObj);
            subObj.put("Tag", subArr);
            return tempObj.toJSONString();
        } else {
            return "";
        }
    }
}
