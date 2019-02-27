import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.NumberUtil
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
 * Created by linql on 2016/03/14.
 * 居民交房处理
 */
class ph014 extends GroovyController {

    /**
     * 初始化进入页面, 读取权限配置信息,通过界面进行数据过滤
     * @param request 请求信息
     * @param response 响应结果
     * @return 系统展示框架
     */
    public ModelAndView initWithAdd(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("forward:/eland/ph/ph014/ph014-init.gv?showAdd=true");
    }

    /**
     * 初始化进入页面, 读取权限配置信息,通过界面进行数据过滤
     * @param request 请求信息
     * @param response 响应结果
     * @return 系统展示框架
     */
    public ModelAndView initNoSch(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String showAdd = request.getParameter("showAdd");
        modelMap.put("showAdd", showAdd);
        // 获取权限编码ID,读取配置初始化查询条件
        String formCd = request.getParameter("formCd");
        String itemCd = request.getParameter("itemCd");
        //获取系统通用表单编码
        if (StringUtil.isEmptyOrNull(formCd) && StringUtil.isNotEmptyOrNull(itemCd)) {
            Map<String, String> map = getCfgCollection(request, "PRJ_FORM",
                    true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
            String prjFormCd = map.get(itemCd);
            if (StringUtil.isNotEmptyOrNull(prjFormCd)) {
                formCd = prjFormCd;
            }
        }
        modelMap.put("formCd", formCd);
        modelMap.put("withFrame", request.getParameter("withFrame"));
        return new ModelAndView("/eland/ph/ph014/ph014_no_search", modelMap);
    }

    /**
     * 初始化进入页面, 读取权限配置信息,通过界面进行数据过滤
     * @param request 请求信息
     * @param response 响应结果
     * @return 系统展示框架
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String showAdd = request.getParameter("showAdd");
        modelMap.put("showAdd", showAdd);
        // 获取权限编码ID,读取配置初始化查询条件
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String privilegeId = request.getParameter("privilegeId");
        // 处理权限编码为空的场合
        if (StringUtil.isNotEmptyOrNull(privilegeId)) {
            svcRequest.setValue("Request.SvcCont.treeType", "1");
            svcRequest.setValue("Request.SvcCont.rhtId", privilegeId);
            SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
                if (nodeInfoBean != null) {
                    // 获取备注描述信息
                    String conditions = nodeInfoBean.getStrValue("Node.rhtAttr02");
                    XmlBean todoBean = new XmlBean();
                    todoBean.setStrValue("Condition.conditionNames", "");
                    todoBean.setStrValue("Condition.conditions", "");
                    todoBean.setStrValue("Condition.conditionValues", "");
                    //添加权限
                    todoBean.setStrValue("Condition.rhtType", "");
                    if (StringUtil.isNotEmptyOrNull(conditions)) {
                        // 配置的Json字符串转换为XMLBean
                        todoBean = XmlBean.jsonStr2Xml(conditions, "Condition");
                        //获取json串里的参数；
                    }
                    modelMap.put("conditions", todoBean.getRootNode());
                }
            }
        }
        String formCd = request.getParameter("formCd");
        String itemCd = request.getParameter("itemCd");
        //获取系统通用表单编码
        Map<String, String> map = getCfgCollection(request, "PRJ_FORM",
                true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String prjFormCd = map.get(itemCd);
        if (StringUtil.isNotEmptyOrNull(prjFormCd)) {
            formCd = prjFormCd;
        }
        modelMap.put("formCd", formCd);
        modelMap.put("withFrame", request.getParameter("withFrame"));
        return new ModelAndView("/eland/ph/ph014/ph014", modelMap);
    }

    /**
     * 进入详情处理界面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String formCd = request.getParameter("formCd");
        String itemCd = request.getParameter("itemCd");
        String infoKey = request.getParameter("infoKey");
        String withFrame = request.getParameter("withFrame");
        String showPrevNext = request.getParameter("showPrevNext");
        if (StringUtil.isEmptyOrNull(showPrevNext)) {
            showPrevNext = "true";
        }
        String showNextPrev = request.getParameter("showNextPrev");
        if (StringUtil.isEmptyOrNull(showNextPrev)) {
            modelMap.put("showNextPrev", true);
        } else if (StringUtil.isEqual("false", showNextPrev)) {
            modelMap.put("showNextPrev", false);
        } else if (StringUtil.isEqual("true", showNextPrev)) {
            modelMap.put("showNextPrev", true);
        }
        //获取系统通用表单编码
        Map<String, String> map = getCfgCollection(request, "PRJ_FORM", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String prjFormCd = map.get(itemCd);
        if (StringUtil.isNotEmptyOrNull(prjFormCd)) {
            formCd = prjFormCd;
        }
        //查询指定房产的 交房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String prePath = "OpData.";
        reqData.setStrValue(prePath + "entityName", "HouseInfo");
        reqData.setStrValue(prePath + "queryType", "2");
        //如果传入hsid是空的，查所有符合条件集合 取第一个
        int paramNum = 0;
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            reqData.setStrValue(prePath + "Conditions.Condition[${paramNum}].fieldName", "HouseInfo.hsId");
            reqData.setStrValue(prePath + "Conditions.Condition[${paramNum}].fieldValue", hsId);
            reqData.setStrValue(prePath + "Conditions.Condition[${paramNum++}].operation", "=");
        }
        // 查询存折领取页面信息
        if ("CZ_LQ_CL".equals(formCd)) {
            reqData.setStrValue(prePath + "Conditions.Condition[${paramNum}].fieldName", "HouseInfo.HsFmInfo.hsFmId");
            reqData.setStrValue(prePath + "Conditions.Condition[${paramNum}].fieldValue", infoKey);
            reqData.setStrValue(prePath + "Conditions.Condition[${paramNum++}].operation", "=");
            //分页大小
            reqData.setStrValue(prePath + "PageInfo.pageSize", "1");
            reqData.setStrValue(prePath + "PageInfo.currentPage", "0");
            //查询房屋基本信息
            reqData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");
            reqData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsCd");
            reqData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsOwnerPersons");
            reqData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.hsFullAddr");
            // 查询领折信息
            reqData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsFmInfo.fmGetDate");
            reqData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsFmInfo.fmGetOpDesc");
            reqData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.HsFmInfo.fmGetOpDocIds");
            reqData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsFmInfo.fmToMakeDate");
            reqData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsFmInfo.fmMakedDate");
            reqData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsFmInfo.fmGetOpDate");
            reqData.setStrValue(prePath + "ResultFields.fieldName[10]", "HouseInfo.HsFmInfo.fmStatus");
            reqData.setStrValue(prePath + "ResultFields.fieldName[11]", "HouseInfo.HsFmInfo.fmPersonName");
            reqData.setStrValue(prePath + "ResultFields.fieldName[12]", "HouseInfo.HsFmInfo.fmMoney");
            reqData.setStrValue(prePath + "ResultFields.fieldName[13]", "HouseInfo.HsFmInfo.pId");
            //调用服务
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", reqData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                // 房屋信息
                XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
                if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                    XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                    hsId = houseInfo.getStrValue("HouseInfo.hsId");
                    modelMap.put("hsInfo", houseInfo.getRootNode());
                }
            }
            /** 查询计划 */
            svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = new XmlBean();
            //查询实体
            opData.setStrValue("OpData.entityName", "PaymentPlan");
            //查询结果
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "pId");
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "pCode");
            opData.setStrValue("OpData.ResultFields.fieldName[2]", "pStatus");
            //调用服务
            svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
            //启动查询
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                if (queryResult != null) {
                    int planNum = queryResult.getListNum("PageData.Row");
                    Map<String, String> pIdNameMap = new LinkedHashMap<>();
                    for (int i = 0; i < planNum; i++) {
                        XmlBean tempBean = queryResult.getBeanByPath("PageData.Row[${i}]");
                        String tempPid = tempBean.getStrValue("Row.pId");
                        String tempPcode = tempBean.getStrValue("Row.pCode");
                        pIdNameMap.put(tempPid, tempPcode);
                    }
                    modelMap.put("payListInfo", pIdNameMap);
                }
            }
            modelMap.put("hsId", hsId);
            modelMap.put("formCd", formCd);
            modelMap.put("infoKey", infoKey);
            // 领款处理界面
            return new ModelAndView("/eland/ph/ph014/ph014_lk_info", modelMap);
        } else {
            //调用 获取表单 信息 返回页面
            XmlBean paramData = new XmlBean();
            // 生成报表模板
            paramData.setStrValue("OpData.formCd", formCd);
            // 生成报表参数
            paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
            int addHouseInfoCount = 0;
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount}].attrName", "hsId");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount++}].value", hsId);
            // 循环处理请求信息
            RequestUtil requestUtil = new RequestUtil(request);
            Map<String, String> requestMap = requestUtil.getRequestMap(request);
            for (Map.Entry<String, String> entry : requestMap) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                if (paramName.startsWith("HouseInfo_")) {
                    String addName = paramName.substring(10);
                    paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount}].attrName", addName);
                    paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount++}].value", paramValue);
                }
            }
            // 增加到请求参数,调用服务
            svcRequest.addOp("GENERATE_REPORT", paramData);
            // 调用服务，执行数据查询
            SvcResponse pageResponse = query(svcRequest);
            if (pageResponse.isSuccess()) {
                XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
                String operStr = opResult.getStrValue("Operation.OpResult.resultParam");
                List operaList = new ArrayList();
                int operNum = opResult.getListNum("Operation.OpResult.OperationDefs.OperationDef");
                for (int i = 0; i < operNum; i++) {
                    operaList.add(opResult.getBeanByPath("Operation.OpResult.OperationDefs.OperationDef[${i}]").getRootNode());
                }
                String pageStr = opResult.getStrValue("Operation.OpResult.resultParam");
                String operDefCode = opResult.getStrValue("Operation.OpResult.OperationDefs.operDefCode");

                //返回展示页面字符串
                modelMap.put("article", pageStr);
                modelMap.put("operaList", operaList);
                modelMap.put("operDefCode", operDefCode);
                modelMap.put("showPrevNext", showPrevNext);
                modelMap.put("infoKey", infoKey);
                modelMap.put("hsId", hsId);
                if ("false".equals(withFrame)) {
                    // 是否包含界面框架
                    return new ModelAndView("/eland/ph/ph014/ph014_info", modelMap);
                } else {
                    return new ModelAndView("/eland/ph/ph014/ph014_common_info", modelMap);
                }

            }
        }
    }

    /**
     * 对外公示界面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView page(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        //查询指定房产的 交房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //调用 获取表单 信息 返回页面
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        paramData.setStrValue("OpData.formCd", request.getParameter("formCd"));
        // 生成报表参数
        paramData.setStrValue("OpData.Report.Parameter[0].entityName", "SysFormInfoDef");
        int addHouseInfoCount = 0;
        // 循环处理请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, String> requestMap = requestUtil.getRequestMap(request);
        for (Map.Entry<String, String> entry : requestMap) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue();
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount}].attrName", paramName);
            paramData.setStrValue("OpData.Report.Parameter[0].Property[${addHouseInfoCount++}].value", paramValue);
        }
        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse pageResponse = query(svcRequest);
        if (pageResponse.isSuccess()) {
            XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
            String pageStr = opResult.getStrValue("Operation.OpResult.resultParam");
            String operDefCode = opResult.getStrValue("Operation.OpResult.OperationDefs.operDefCode");
            //返回展示页面字符串
            modelMap.put("article", pageStr);
            modelMap.put("operDefCode", operDefCode);
            // 是否包含界面框架
            return new ModelAndView("/eland/ph/ph014/ph014_page", modelMap);
        }
    }

    /** 查询上一户、下一户hsId */
    public void singleQuery(HttpServletRequest request, HttpServletResponse response) {
        // 获取房屋编号
        RequestUtil requestUtil = new RequestUtil(request);
        String inHsId = requestUtil.getStrParam("hsId");
        // 查询条件
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String infoKeyPath = requestUtil.getStrParam("infoKeyPath");
        String sortColumn = requestUtil.getStrParam("rSortColumn");
        String rhtType = request.getParameter("rhtType");
        if (StringUtil.isEmptyOrNull(sortColumn)) {
            sortColumn = "HouseInfo.hsId"
        }
        String sortOrder = requestUtil.getStrParam("rSortOrder");
        if (StringUtil.isEmptyOrNull(sortColumn)) {
            sortOrder = "asc"
        }
        //项目编号
        String prjCd = requestUtil.getStrParam("prjCd");
        // 排序条件
        String flag = request.getParameter("flag");
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 获取基本信息
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        // 查询条件
        int addCount = 0;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                    || StringUtil.isEmptyOrNull(conditionArr[i])
                    || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            String tempConditionFieldArr = conditionFieldArr[i];
            String tempConditionValueArr = conditionValueArr[i];
            if (tempConditionFieldArr.startsWith("UID.")) {
                tempConditionFieldArr = tempConditionFieldArr.substring(4);
                tempConditionValueArr = StringUtil.dcdNetBase64Str(tempConditionValueArr);
            }
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + tempConditionValueArr + "%");
            } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                String usedValue = tempConditionValueArr.replaceAll("\\|", ",");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", tempConditionValueArr);
            }
        }
        // 排序条件
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", sortColumn);
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", sortOrder);
        //权限条件
        opData.setStrValue(prePath + "OrgFilter.attrName", "\$entityName.\$ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.rhtType", rhtType);
        opData.setStrValue(prePath + "OrgFilter.attrValue", "");
        // 分页条件
        opData.setStrValue(prePath + "PageInfo.pageSize", "100000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.hsFullAddr");
        if (StringUtil.isNotEmptyOrNull(infoKeyPath)) {
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", infoKeyPath);
        }
        // 数据过滤
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        svcRequest.addOp("PRIVI_FILTER", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        String hsId = "";
        String infoKey = '';
        String hsCtId = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("PRIVI_FILTER")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                int rowCount = pageData.getListNum("PageData.Row");
                int getIdx = -1;
                if (StringUtil.isEmptyOrNull(inHsId)) {
                    if (StringUtil.isEqual("next", flag)) {
                        getIdx = rowCount - 1;
                    } else {
                        getIdx = 0;
                    }
                }
                for (int i = 0; i < rowCount; i++) {
                    String cHsId = pageData.getStrValue("PageData.Row[${i}].HouseInfo.hsId");
                    if (StringUtil.isEqual(inHsId, cHsId)) {
                        if (StringUtil.isEqual("next", flag)) {
                            if (i < rowCount - 1) {
                                getIdx = i + 1;
                            }
                        } else {
                            getIdx = i - 1;
                        }
                        break;
                    }
                }
                if (getIdx >= 0) {
                    hsId = pageData.getStrValue("PageData.Row[${getIdx}].HouseInfo.hsId");
                    hsCtId = pageData.getStrValue("PageData.Row[${getIdx}].HouseInfo.HsCtInfo.hsCtId");
                    if (StringUtil.isNotEmptyOrNull(infoKeyPath)) {
                        infoKey = pageData.getStrValue("PageData.Row[${getIdx}]." + infoKeyPath);
                    }
                }
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """hsId:'${hsId}', hsCtId: '${hsCtId}', infoKey: '${
            infoKey
        }'""");
    }

    /**
     * TODO: 保存通用 配置模板方法
     * @param request 请求信息
     * @param response 响应结果
     */
    public void saveCommonForm(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        /**
         * 拼接 表单基本信息 报文
         */
        Map<String, String[]> reqParamMap = request.getParameterMap();

        // 创建以某一个实体为 key 的map
        Map<String, XmlBean> entityMap = new LinkedHashMap<String, XmlBean>();
        //entityMap = [HouseInfo_20 : updateEntity]
        if (reqParamMap != null) {
            Iterator<String> it = reqParamMap.keySet().iterator();
            while (it.hasNext()) {
                String reqAttr = it.next();             //从前端接收到的提交属性： eg: HouseInfo_hsCd_20
                if (!StringUtil.contains(reqAttr, '_')) {
                    continue;
                }
                //截取 下面所需的 实体名、属性名、主键值：
                int first_order = reqAttr.indexOf("_");
                int last_order = reqAttr.lastIndexOf("_");
                String entityAttr = reqAttr.substring(first_order + 1, last_order);  //eg: hsCd
                String entityName = reqAttr.substring(0, first_order);               //eg: HouseInfo
                String entityPrjKeyVal = reqAttr.substring(last_order + 1);          //eg：20

                //截取不同实体需要存储的Map key ：  eg：HouseInfo_20
                String mapKey = entityName + "_" + entityPrjKeyVal;

                //获取请求参数里的  参数值：value[]
                String[] listValue = reqParamMap.get(reqAttr);
                String value = null;
                if (listValue.length == 1) {
                    value = listValue[0];
                } else {
                    //结果值 如果不止 一个 需要 处理
                    value = listValue.join(",");
                }

                //拼接map里 每个key 对应 value节点报文： eg：updateEntity = <OpData><EntityData><hsId>20</hsId></EntityData></OpData>
                XmlBean updateEntity = entityMap.get(mapKey);
                if (updateEntity == null) {
                    updateEntity = new XmlBean();
                    updateEntity.setStrValue("OpData.entityName", entityName);
                    updateEntity.setStrValue("OpData.EntityData.${entityAttr}", value);
                    entityMap.put(mapKey, updateEntity);
                } else {
                    updateEntity.setStrValue("OpData.EntityData.${entityAttr}", value);
                }
            }
            for (Object temp : entityMap.keySet()) {
                XmlBean tempBean = entityMap.get(temp);
                svcRequest.addOp("SAVE_ENTITY", tempBean);
            }
        }
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, null);
    }

    /**
     * 领款数据保存
     * @param request 请求信息
     * @param response 响应结果
     */
    public void saveLkForm(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsFmId = request.getParameter("hsFmId");
        /**
         * 拼接 表单基本信息 报文
         */
        XmlBean reqData = new XmlBean();
        String reqPath = "OpData.";
        reqData.setStrValue(reqPath + "entityName", "HsFmInfo");
        reqData.setStrValue(reqPath + "EntityData.hsFmId", hsFmId);
        reqData.setStrValue(reqPath + "EntityData.fmStatus", request.getParameter("fmStatus"));
        reqData.setStrValue(reqPath + "EntityData.fmStatusDate", svcRequest.getReqTime());
        // 存折领取时间
        String fmGetDate = request.getParameter("fmGetDate");
        if (StringUtil.isNotEmptyOrNull(fmGetDate)) {
            Date fmGetDateTemp = DateUtil.toDateYmdWthH(fmGetDate);
            fmGetDate = DateUtil.toStringYmdHms(fmGetDateTemp);
        }
        reqData.setStrValue(reqPath + "EntityData.fmGetDate", fmGetDate);
        reqData.setStrValue(reqPath + "EntityData.fmGetOpDesc", request.getParameter("fmGetOpDesc"));
        reqData.setStrValue(reqPath + "EntityData.fmGetOpDocIds", request.getParameter("fmGetOpDocIds"));
        reqData.setStrValue(reqPath + "EntityData.fmGetOpDate", svcRequest.getReqTime());
        svcRequest.addOp("SAVE_ENTITY", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, """ """);
    }

    /**
     * 根据类型加载附件信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initDocs(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String docTypeName = request.getParameter("docTypeName");

        //查询指定房产的 交房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            //查询 交房确认单 附件
            svcRequest = RequestUtil.getSvcRequest(request);
            // 获取可用房源区域列表
            XmlBean opData = new XmlBean();
            String prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "SvcDocInfo");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "100");
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", hsId);
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
            opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "docTypeName");
            opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", docTypeName);
            opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "statusCd");
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", "1");
            opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");

            // 需要查询的字段
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docName");

            // 增加查询
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            SvcResponse svcRsp = query(svcRequest);
            //返回处理结果
            if (svcRsp.isSuccess()) {
                XmlBean queryResult = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    List<XmlNode> hsDocs = new ArrayList<XmlNode>(rowCount);
                    for (int i = 0; i < rowCount; i++) {
                        hsDocs.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                    }
                    modelMap.put("hsDocs", hsDocs);
                }
            }
        }
        return new ModelAndView("/eland/ph/ph014/ph014_docs", modelMap);
    }

    private Map<String, String> getCfgCollectionWithNote(HttpServletRequest httpServletRequest, String itemCd, boolean isCached, int prjCd) {
        Object useCollection = (Map) httpServletRequest.getAttribute("C._" + itemCd + prjCd);
        if (useCollection == null) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(httpServletRequest);
            svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", itemCd);
            svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", Boolean.valueOf(isCached));
            svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", Integer.valueOf(prjCd));
            SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
            if (svcResponse.isSuccess()) {
                int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
                useCollection = new LinkedHashMap();

                for (int i = 0; i < count; ++i) {
                    String key = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].valueCd");
                    String notes = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].notes");
                    ((Map) useCollection).put(key, notes);
                }

                httpServletRequest.setAttribute("C._" + itemCd + prjCd, useCollection);
            }
        }

        return (Map) useCollection;
    }
}