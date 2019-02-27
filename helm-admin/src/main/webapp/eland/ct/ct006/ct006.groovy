import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 签约处理控制层
 */
class ct006 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", requestUtil.getStrParam("hsId"));
        String schemeType = requestUtil.getStrParam("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "4";
        }
        modelMap.put("schemeType", schemeType);
        // 区域及权限控制类型
        String regUseType = requestUtil.getStrParam("regUseType");
        if (StringUtil.isEmptyOrNull(regUseType)) {
            regUseType = "2";
        }
        modelMap.put("regUseType", regUseType);
        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 推送输出结果到界面
        modelMap.put("ctStatusField", jsonObject.get("attr_status"));
        modelMap.put("conditionNames", jsonObject.get("conditionNames", ""));
        modelMap.put("conditions", jsonObject.get("conditions", ""));
        modelMap.put("conditionValues", jsonObject.get("conditionValues", ""));
        return new ModelAndView("/eland/ct/ct006/ct006", modelMap);
    }

    /**
     * 右侧展示房源签约信息
     * @param request
     * @param response
     * @return
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取输入参数
        String oldHsId = request.getParameter("oldHsId");
        String prjCd = request.getParameter("prjCd");
        String newHsId = request.getParameter("newHsId");
        String schemeType = request.getParameter("schemeType");
        String opFlag = request.getParameter("opFlag");

        // 推送界面展示信息
        modelMap.put("schemeType", schemeType);

        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 推送输出结果到界面
        String ctStatusField = jsonObject.get("attr_status");
        String ctDateField = jsonObject.get("attr_date");
        modelMap.put("ctStatusField", ctStatusField);
        modelMap.put("ctDateField", ctDateField);

        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", oldHsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 被安置人基本信息
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsOwnerPersons");
        // 被安置人主协议信息
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.chooseHsSid");
        // 本次协议信息
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.hsDlStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        // 协议打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo." + ctStatusField);
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo." + ctDateField);
        // 实体信息查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        // 查询安置房源信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "newHsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", newHsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 房源基本信息
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "statusDate");
        // 实体信息查询
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);

        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        //获取各种状态  跳转不同页面    0，未生成  1， 已生成
        String currentCtStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                currentCtStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctStatusField);
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctDateField);
                // 推送界面展示信息
                modelMap.put("hsInfo", houseInfo.getRootNode());
                modelMap.put("ctDate", ctDate);
                modelMap.put("ctStatus", currentCtStatus);

            }
            pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean newHsInfo = pageData.getBeanByPath("PageData.Row[0]");
                modelMap.put("newHsInfo", newHsInfo.getRootNode());
            }
        }
        // 根据结算状态过滤跳转页面   00501--> 已结算页面  00502-->
        String forwardPage = "/eland/ct/ct006/ct006_ct";
        if ("viewDoc".equals(opFlag)) {
            // 协议未打印: 继续留在签约页面
            forwardPage = "/eland/ct/ct006/ct006_ct";
        } else if ("viewFj".equals(opFlag)) {
            // 已经签约： 去上传附件页面
            forwardPage = "forward:/eland/ct/ct006/ct006-initFj.gv"
        } else if (StringUtil.isEqual("2", currentCtStatus)) {
            // 已经签约： 去上传附件页面
            forwardPage = "forward:/eland/ct/ct006/ct006-initFj.gv"
        } else {
            // 协议未打印: 继续留在签约页面
            forwardPage = "/eland/ct/ct006/ct006_ct";
        }

        modelMap.put("oldHsId", oldHsId);
        modelMap.put("newHsId", newHsId);
        return new ModelAndView(forwardPage, modelMap);
    }

    /**
     * 生成协议文档
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public ModelAndView generateCt(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.HsCtInfo.hsId", requestUtil.getStrParam("oldHsId"));
        paramData.setStrValue("OpData.HsCtInfo.hsCtId", requestUtil.getStrParam("hsCtId"));
        paramData.setStrValue("OpData.HsCtInfo.schemeType", requestUtil.getStrParam("schemeType"));
        paramData.setStrValue("OpData.HsCtInfo.ctType", "1");
        svcRequest.addOp("CREATE_AGREEMENT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        boolean isSuccess = svcResponse.isSuccess();
        if (isSuccess) {
            XmlBean opResult = svcResponse.getFirstOpRsp("CREATE_AGREEMENT");
            if (opResult != null) {
                String docPrePath = "/reports/report";
                String docPath = docPrePath + opResult.getValue("Operation.OpResult.resultParam");
                String fromOp = request.getParameter("fromOp");
                if (StringUtil.isEqual(fromOp, "view")) {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPath);
                    return new ModelAndView("/eland/ct/ct006/ct006_doc_iframe", modelMap)
                } else if (StringUtil.isEqual(fromOp, "download")) {
                    // 获取下载文件的后缀
                    String docExt = docPath.substring(docPath.lastIndexOf("."), docPath.length());
                    // 生成下载文件的展示名称
                    String docName = request.getParameter("docName");
                    if (StringUtil.isEmptyOrNull(docName)) {
                        docName = "居民协议(" + requestUtil.getStrParam("oldHsId") + ")";
                    } else {
                        docName = java.net.URLDecoder.decode(docName, "utf-8")
                    }
                    docName = docName + "." + docExt;
                    // 获取下载的本地文件
                    String contextType = requestUtil.getStrParam("contextType");
                    File localFile = new File(StringUtil.formatFilePath("webroot:" + docPath));
                    // 文件下载输出到响应流
                    ResponseUtil.downloadFile(response, contextType, docName,
                            localFile, request.getHeader("USER-AGENT"), false, true);
                    return;
                }
            }
        } else {
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", "生成文档失败，请联系管理员!");
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }

    /**
     * 查看签约协议文档
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView viewDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct005/ct005_doc_view", modelMap)
    }

    /**
     * 确认签约处理
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView cfmCt(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String newHsId = request.getParameter("newHsId");

        // 确认签约处理
        String nodePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue(nodePath + "HsCtInfo.schemeType", request.getParameter("schemeType"));
        // 签约操作人及操作时间
        opData.setStrValue(nodePath + "HsCtInfo.hsDlStaff", svcRequest.getReqStaff());

        svcRequest.addOp("CHANGE_TO_FORMAL_SIGN_TT", opData);
        // 处理交房状态
        opData = new XmlBean();
        opData.setStrValue(nodePath + "entityName", "NewHsInfo");
        opData.setStrValue(nodePath + "EntityData.newHsId", newHsId);
        opData.setStrValue(nodePath + "EntityData.handleStatus", "4");

        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 取消签约处理
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView cancelCt(HttpServletRequest request, HttpServletResponse response) {
        String hsCtId = request.getParameter("hsCtId");
        String newHsId = request.getParameter("newHsId");

        // 请求参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 确认签约处理
        String nodePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue(nodePath + "HsCtInfo.schemeType", request.getParameter("schemeType"));
        svcRequest.addOp("CHANGE_TO_UN_SIGNED", opData);

        // 处理交房状态,回退交房状态为延期已通知
        opData = new XmlBean();
        opData.setStrValue(nodePath + "entityName", "NewHsInfo");
        opData.setStrValue(nodePath + "EntityData.newHsId", newHsId);
        opData.setStrValue(nodePath + "EntityData.handleStatus", "3");
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 已经签约显示签约附件
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        String oldHsId = request.getParameter("oldHsId");
        String newHsId = request.getParameter("newHsId");
        String schemeType = request.getParameter("schemeType");

        ModelMap modelMap = new ModelMap();
        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 获取附件名称
        String signDocName = jsonObject.get("sign_doc_name");
        String photoDocName = jsonObject.get("photo_doc_name");
        // 推动到界面视图
        modelMap.put("signDocName", signDocName);
        modelMap.put("photoDocName", photoDocName);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取签字附件列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", oldHsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 附件类型
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "docTypeName");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "('${signDocName}',${photoDocName})");
        //只查询状态为 1 的，
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docName");
        // 增加查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcRsp = query(svcRequest);
        // 返回处理结果
        if (svcRsp.isSuccess()) {
            XmlBean queryResult = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<XmlNode> hsDocs = new ArrayList<XmlNode>(rowCount);
                String photoDocIds = "";
                String signDocIds = "";
                for (int i = 0; i < rowCount; i++) {
                    hsDocs.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                    String docType = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                    if (StringUtil.isEqual(docType, signDocName)) {
                        signDocIds += queryResult.getStrValue("OpResult.PageData.Row[${i}].docId") + ",";
                    }
                    if (StringUtil.isEqual(docType, photoDocName)) {
                        photoDocIds += queryResult.getStrValue("OpResult.PageData.Row[${i}].docId") + ",";
                    }
                }
                modelMap.put("hsDocs", hsDocs);
                if (photoDocIds.length() > 0) {
                    modelMap.put("photoDocIds", photoDocIds.substring(0, photoDocIds.length() - 1));
                }
                if (signDocIds.length() > 0) {
                    modelMap.put("signDocIds", signDocIds.substring(0, signDocIds.length() - 1));
                }
            }
        }
        modelMap.put("oldHsId", oldHsId);
        modelMap.put("newHsId", newHsId);
        return new ModelAndView("/eland/ct/ct006/ct006_fj", modelMap);
    }

    /**
     * 获取预分配置数据
     * @param request
     * @return
     */
    private JSONObject getSchemeTypeCfg(HttpServletRequest request, schemeType) {
        // 获取预分分类定义
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", "SCHEME_TYPE");
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", true);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", svcRequest.getPrjCd());
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            String schemeJsonStr = null;
            for (int i = 0; i < count; ++i) {
                String key = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].valueCd");
                if (StringUtil.isEqual(key, schemeType)) {
                    schemeJsonStr = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].notes");
                    break;
                }
            }
            // 获取定义类型
            return JSONObject.fromObject(schemeJsonStr);
        } else {
            return new JSONObject();
        }
    }

}

