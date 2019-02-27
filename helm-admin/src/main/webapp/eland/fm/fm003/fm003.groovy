import com.shfb.oframe.core.util.common.BigDecimalUtil
import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
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
 * Created by HuberyYan on 2016/5/23.
 */
class fm003 extends GroovyController {
    String mHsFmId = "";
    /**
     * 初始化 领款登记界面
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/fm/fm003/fm003", modelMap);
    }

    /**
     * 领款人登记界面
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");//获取hsId
        modelMap.put("hsId", hsId);
        String pId = request.getParameter("pId");//获取付款计划Id
        boolean isHavaUsedPlan = true;//是否有已启用计划
        boolean isCanEdit = true;//是否可编辑
        //跨实体查询基础部分
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        //要查询的结果
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsCd");//档案编号
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");//被安置人
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsFullAddr");//地址
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");//安置方式
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        //启动查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean baseResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData.Row.HouseInfo");
            modelMap.put("hsCd", baseResult.getStrValue("HouseInfo.hsCd"));
            modelMap.put("hsOwnerPersons", baseResult.getStrValue("HouseInfo.hsOwnerPersons"));
            modelMap.put("hsFullAddr", baseResult.getStrValue("HouseInfo.hsFullAddr"));
            modelMap.put("ctType", baseResult.getStrValue("HouseInfo.HsCtInfo.ctType_Name"));
            modelMap.put("personCertyNum", baseResult.getStrValue("HouseInfo.PersonInfo.personCertyNum"));
            modelMap.put("personTelphone", baseResult.getStrValue("HouseInfo.PersonInfo.personTelphone"));
        }

        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "PersonInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "oldHsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "familyOwnFlag");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "personCertyNum");//被安置人身份证
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "personTelphone");//被安置人电话
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        //启动查询
        svcResponse = query(svcRequest);
        String personCertyNum = "";
        String personTelphone = "";
        if (svcResponse.isSuccess()) {
            XmlBean baseResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (baseResult != null) {
                personCertyNum = baseResult.getStrValue("PageData.Row.personCertyNum");
                personTelphone = baseResult.getStrValue("PageData.Row.personTelphone")
            }
        }
        //综合部分------------------------------------------------------------
        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        opData.setStrValue("OpData.hsId", hsId);
        opData.setStrValue("OpData.pId", pId);
        //调用服务
        svcRequest.addOp("QUERY_PAYEE_CMPT", opData);
        //启动查询
        svcResponse = query(svcRequest);
        if (StringUtil.isNotEmptyOrNull(svcResponse.getErrMsg())) {
            String errorInfo = svcResponse.getErrMsg();
            modelMap.put("errorInfo", errorInfo);
            return new ModelAndView("/eland/fm/fm003/fm00301", modelMap);
        } else if (svcResponse.isSuccess()) {
            XmlBean hsFmResult = svcResponse.getFirstOpRsp("QUERY_PAYEE_CMPT").getBeanByPath("Operation.OpResult");
            if (hsFmResult != null) {
                //******总金额******
                //-----//验证用正常金额
                String totalMoney = hsFmResult.getDoubleValue("OpResult.totalMoney");
                //-----//格式化金额
                String gshTotalMoney = BigDecimalUtil.formatMoney(NumberUtil.getDoubleFromObj(hsFmResult.getDoubleValue("OpResult.totalMoney")),2);
                modelMap.put("totalMoney", totalMoney);
                modelMap.put("gshTotalMoney", gshTotalMoney);
                //付款计划---------------------------------------------------------------------------------------
                XmlBean payPlanBean = hsFmResult.getBeanByPath("OpResult.PayPlanInfos");
                int payNum = payPlanBean.getListNum("PayPlanInfos.PayPlanInfo");
                //已启用“付款计划名称”列表
                List<String> payList = new ArrayList<>();
                //以map<pId,pCode>格式，保存全部付款计划
                Map<String, String> pIdNameMap = new LinkedHashMap<>();
                for (int i = 0; i < payNum; i++) {
                    XmlBean listBean = payPlanBean.getBeanByPath("PayPlanInfos.PayPlanInfo[${i}]");
                    String tempPid = listBean.getStrValue("PayPlanInfo[${i}].pId");
                    String tempPcode = listBean.getStrValue("PayPlanInfo[${i}].pCode");
                    pIdNameMap.put(tempPid, tempPcode);
                    payList.add(tempPid);
                }
                if (!(pIdNameMap.size() > 0)) {
                    //******没有已启用付款计划，则返回提示字符串******
                    isHavaUsedPlan = false;
                }
                //******返回计划名称列表“第一笔款” “第二笔款”...******
                modelMap.put("payListInfo", pIdNameMap);
                //领款人信息----------------------------------------------------------------------------------------
                int hsFmNum = hsFmResult.getListNum("OpResult.HsFmInfos.HsFmInfo");
                //协议Id
                String hsCtId = "";
                //把领款人的信息以map<Pid,list<HsFmInfo>>格式存储
                Map<String, List<XmlNode>> planMap = new LinkedHashMap<>();
                if (hsFmNum == 0) {
                    //领款人信息为空，跨实体查询hsCtId
                    XmlBean opData2 = new XmlBean();
                    String prePath2 = "OpData.";
                    opData2.setStrValue(prePath2 + "queryType", "2");
                    opData2.setStrValue(prePath2 + "entityName", "HsCtInfo");
                    opData2.setStrValue(prePath2 + "Conditions.Condition[0].fieldName", "HsCtInfo.hsId");
                    opData2.setStrValue(prePath2 + "Conditions.Condition[0].fieldValue", hsId);
                    opData2.setStrValue(prePath2 + "Conditions.Condition[0].operation", "=");
                    //要查询的结果
                    opData2.setStrValue(prePath2 + "ResultFields.fieldName[0]", "HsCtInfo.hsCtId");//安置方式
                    //调用服务
                    svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData2);
                    //启动查询
                    SvcResponse svcResponse2 = query(svcRequest);
                    if (svcResponse2.isSuccess()) {
                        XmlBean baseResult = svcResponse2.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData.Row.HsCtInfo");
                        hsCtId = baseResult.getStrValue("HsCtInfo.hsCtId");
                    }
                } else {
                    for (int i = 0; i < hsFmNum; i++) {
                        XmlBean listBean = hsFmResult.getBeanByPath("OpResult.HsFmInfos.HsFmInfo[${i}]");
                        List<XmlNode> hsFmList = planMap.get(listBean.getStrValue("HsFmInfo.pId"));
                        if (hsFmList == null) {
                            hsFmList = new ArrayList<>();
                        }
                        hsFmList.add(listBean.getRootNode());
                        planMap.put(listBean.getStrValue("HsFmInfo.pId"), hsFmList)
                        hsCtId = listBean.getStrValue("HsFmInfo[${i}].hsCtId");
                    }
                }
                //协议信息----------------------------------------------------------------------------------
                List<XmlNode> returnList = new ArrayList();//对应付款计划的 HsFmInfo节点信息
                //查询已登记的第一条pId
                if (StringUtil.isEmptyOrNull(pId)) {
                    int pos = 0;
                    for (Map.Entry<String, List<XmlNode>> map : planMap.entrySet()) {
                        if (pos == 0) {
                            pId = map.getKey();
                        }
                        pos++;
                    }
                }
                //有启用的付款计划则查询，没有不查
                if (isHavaUsedPlan) {
                    //之前查询已登记的第一条pId，如果没有，返回启用计划的第一条pId
                    if (StringUtil.isEmptyOrNull(pId)) {
                        int pos = 0;
                        for (Map.Entry<String, List<XmlNode>> map : pIdNameMap.entrySet()) {
                            if (pos == 0) {
                                pId = map.getKey();
                            }
                            pos++;
                        }
                    }
                    //******pId******
                    modelMap.put("pId", pId);
                    returnList = planMap.get(pId);
                    if (returnList != null) {
                        for (int i = 0; i < returnList.size(); i++) {
                            //领款人状态
                            String fmStatus = ((Map<String, Object>) returnList.get(i).get("HsFmInfo")).get("fmStatus");
                            //领款人状态
                            if (!fmStatus.equals("7") && !fmStatus.equals("9")) {
                                isCanEdit = false;
                            }
                        }
                        //******对应付款协议下的人员信息******
                        modelMap.put("HsFmInfo", returnList);
                        //******对应付款协议下的备注******
                        modelMap.put("fmDesc", ((Map<String, Object>) returnList.get(0).get("HsFmInfo")).get("fmDesc"));
                        //******对应付款协议下的附件******
                        modelMap.put("fmPersonDocIds", ((Map<String, Object>) returnList.get(0).get("HsFmInfo")).get("fmPersonDocIds"));
                    } else {
                        //******人员信息为空返回布尔值******
                        modelMap.put("flag", 'false');
                        //******领款人为空，返回当前产权人信息******
                        modelMap.put("personCertyNum", personCertyNum);
                        modelMap.put("personTelphone", personTelphone);
                    }
                    //******是否可编辑******
                    modelMap.put("isCanEdit", isCanEdit);
                    //有已登记的pId || 有已启用的pId
                    if (StringUtil.isNotEmptyOrNull(pId)) {
                        //******协议Id******
                        modelMap.put("hsCtId", hsCtId);
                        //根据付款计划查询全部协议，并返回总金额-----------------------------------------------
                        svcRequest = RequestUtil.getSvcRequest(request);
                        opData = new XmlBean();
                        opData.setStrValue("OpData.pId", pId);
                        opData.setStrValue("OpData.hsCtId", hsCtId);
                        //调用服务
                        svcRequest.addOp("QUERY_HS_CT_BY_PID_CMPT", opData);
                        //启动服务
                        svcResponse = query(svcRequest);
                        if (svcResponse.isSuccess()) {
                            //全部协议信息list
                            XmlBean ctResult = svcResponse.getFirstOpRsp("QUERY_HS_CT_BY_PID_CMPT").getBeanByPath("Operation.OpResult.CtInfos");
                            if (ctResult != null) {
                                int ctNum = ctResult.getListNum("CtInfos.CtInfo");
                                List<XmlNode> ctList = new ArrayList<>();//对应付款计划下的 协议列表
                                for (int i = 0; i < ctNum; i++) {
                                    XmlBean listBean = ctResult.getBeanByPath("CtInfos.CtInfo[${i}]");
                                    double ctMoney = listBean.getDoubleValue("CtInfo.ctMoney");
                                    String gshCtMoney = BigDecimalUtil.formatMoney(NumberUtil.getDoubleFromObj(ctMoney),2);
                                    listBean.setStrValue("CtInfo.ctMoney",gshCtMoney);
                                    ctList.add(listBean.getRootNode());
                                }
                                //******协议信息列表******
                                modelMap.put("ctInfo", ctList);
                            }
                        }
                        //查询对应pId的五联单pFormCd-------------------------------------------------------------------------
                        svcRequest = RequestUtil.getSvcRequest(request);
                        opData = new XmlBean();
                        opData.setStrValue("OpData.entityName", "PaymentPlan");
                        //条件
                        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "pId");
                        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", pId);
                        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
                        //查询结果
                        opData.setStrValue("OpData.ResultFields.fieldName", "pFormCd");
                        //调用服务
                        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
                        //启动查询
                        svcResponse = query(svcRequest);
                        if (svcResponse.isSuccess()) {
                            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                            if (queryResult != null) {
                                //******五联单******
                                modelMap.put("pFormCd", queryResult.getStrValue("PageData.Row.pFormCd"));
                            }
                        }
                    } else {
                        //******协议信息列表******
                        modelMap.put("ctInfo", new ArrayList<XmlNode>());
                    }
                }

            }
            return new ModelAndView("/eland/fm/fm003/fm00301", modelMap);
        }
    }

    /**
     * 提交制折
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView commitApply(HttpServletRequest request, HttpServletResponse response) {
        //提交之前保存
        savePayInfo(request,response);
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //多领款人Id
        String hsFmId = "";
        if (mHsFmId == ""){
            hsFmId = request.getParameter("hsFmId");
        } else {
            hsFmId = mHsFmId;
        }
        //保存
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsFmId", hsFmId)
        //调用组件
        svcRequest.addOp("SAVE_BANK_BOOK_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 保存按钮
     * @param request
     * @param response
     */
    public void savePayInfo(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        //计划Id
        String hsId = request.getParameter("hsId");//房屋Id
        String pId = request.getParameter("pId");//领款计划Id
        String totalMoney = request.getParameter("totalMoney");//全部协议总金额
        //多领款人信息
        String[] fmPersonNames = request.getParameterValues("fmPersonName");//领款人姓名
        String[] fmMoneys = request.getParameterValues("fmMoney");//领款金额
        String[] fmPersonCertyNums = request.getParameterValues("fmPersonCertyNum");//身份证号
        String[] fmPersonTels = request.getParameterValues("fmPersonTel");//联系电话
        String[] fmStatus = request.getParameterValues("fmStatus");
        String fmPersonDocIds = request.getParameter("docId");//附件
        String fmDesc = request.getParameter("fmDesc");
        String hsCtId = request.getParameter("hsCtId");
        int names = fmPersonNames.size();
        int certys = fmPersonCertyNums.size();
        if (names > 0 && certys > 0 && names == certys) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.PayPlanInfo.hsId", hsId);
            opData.setStrValue("OpData.PayPlanInfo.pId", pId);
            opData.setStrValue("OpData.PayPlanInfo.totalMoney", totalMoney);
            int temp = 0;
            for (int i = 0; i < fmPersonNames.length; i++) {
                if (StringUtil.isEmptyOrNull(fmPersonNames[i])) {
                    continue;
                }
                opData.setStrValue("OpData.HsFmInfo[" + temp + "].fmPersonName", fmPersonNames[i]);
                opData.setStrValue("OpData.HsFmInfo[" + temp + "].fmMoney", fmMoneys[i]);
                opData.setStrValue("OpData.HsFmInfo[" + temp + "].fmPersonCertyNum", fmPersonCertyNums[i]);
                opData.setStrValue("OpData.HsFmInfo[" + temp + "].fmPersonTel", fmPersonTels[i]);
                opData.setStrValue("OpData.HsFmInfo[" + temp + "].fmStatus", fmStatus[i]);
                opData.setStrValue("OpData.HsFmInfo[" + temp + "].fmPersonDocIds", fmPersonDocIds);
                opData.setStrValue("OpData.HsFmInfo[" + temp + "].fmDesc", fmDesc);
                opData.setStrValue("OpData.HsFmInfo[" + temp++ + "].hsCtId", hsCtId);
            }
            // 调用服务
            svcRequest.addOp("SAVE_PAYEE_CMPT", opData);

            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()){
                XmlBean opResult = svcResponse.getFirstOpRsp("SAVE_PAYEE_CMPT").getBeanByPath("Operation.OpResult");
                if (opResult != null){
                    temp = opResult.getListNum("OpResult.hsFmId");
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < temp; i++) {
                        String tempValue = opResult.getStrValue("OpResult.hsFmId[" + i + "]");
                        stringBuffer.append(",").append(tempValue);
                    }
                    mHsFmId = stringBuffer.substring(1);
                }
            }
            // 返回处理结果
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }

    /**
     * 生成五联单
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public ModelAndView viewWld(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        String formId = request.getParameter("formId");
        String formCd = request.getParameter("formCd");
        String agent = request.getHeader("USER-AGENT");
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            XmlBean paramData = new XmlBean();
            // 生成报表模板
            paramData.setStrValue("OpData.formId", formId);
            paramData.setStrValue("OpData.formCd", formCd);
            // 生成报表参数
            paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", request.getParameter("hsId"));
            // 协议编号
            paramData.setStrValue("OpData.Report.Parameter[1].entityName", "HsCtInfo");
            paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "hsCtId");
            paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", request.getParameter("hsCtId"));

            // 增加到请求参数,调用服务
            svcRequest.addOp("GENERATE_REPORT", paramData);
            // 调用服务，执行数据查询
            SvcResponse svcResponse = transaction(svcRequest);

            // 处理服务返回结果
            String errMsg = "生成文档失败，请联系管理员!"
            boolean isSuccess = svcResponse.isSuccess();
            if (isSuccess) {
                XmlBean opResult = svcResponse.getFirstOpRsp("GENERATE_REPORT");

                if (opResult != null) {
                    // 返回结果类型
                    String resultType = opResult.getValue("Operation.OpResult.resultType");
                    if ("1".equals(resultType)) {
                        String docPath = opResult.getValue("Operation.OpResult.resultParam");
                        String docPrePath = "/reports/report"
                        String fromOp = request.getParameter("fromOp");
                        if (StringUtil.isEqual(fromOp, "view")) {
                            //本地查看
                            ModelMap modelMap = new ModelMap();
                            modelMap.put("docFileUrl", docPrePath + docPath);
                            return new ModelAndView("/eland/fm/fm003/fm003_doc_iframe", modelMap)
                        } else if (StringUtil.isEqual(fromOp, "download")) {
                            // 获取下载文件的后缀
                            String docExt = docPath.substring(docPath.lastIndexOf("."), docPath.length());
                            // 生成下载文件的展示名称
                            String docName = request.getParameter("docName");
                            if (StringUtil.isEmptyOrNull(docName)) {
                                docName = "信息报表(" + hsId + ")";
                            } else {
                                docName = java.net.URLDecoder.decode(docName, "utf-8")
                            }
                            docName = docName + "." + docExt;
                            // 获取下载的本地文件
                            String contextType = requestUtil.getStrParam("contextType");
                            File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                            // 文件下载输出到响应流
                            ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false, true);
                            return;
                        }
                    } else {
                        errMsg = errMsg + "只有文档类型的模板允许在该模块中生成,请调整表单配置!"
                    }
                }
            } else {
                errMsg = "生成文档失败，错误原因:\"" + svcResponse.getErrMsg() + "\"";
            }
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", errMsg);
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }

    /** 查看协议文档 */
    public ModelAndView viewDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/fm/fm003/fm003_doc_view", modelMap)
    }
}

