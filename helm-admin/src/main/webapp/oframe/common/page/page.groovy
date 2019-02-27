import com.alibaba.fastjson.JSONArray
import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.excel.ExcelReader
import com.shfb.oframe.core.util.excel.ExcelType
import com.shfb.oframe.core.util.excel.ExcelWriter
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.apache.poi.ss.formula.functions.T
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method

class page extends GroovyController {

    /**
     * 码表值选取函数正则式
     */
    private final String CODE_SLT_PAT = """^CODE\\(.*\\)\$""";

    /**
     * 码表值选取函数正则式
     */
    private final String TREE_CODE_SLT_PAT = """^TREE_CODE\\(.*\\)\$""";

    /**
     * 日期类型选取函数正则式
     */
    private final String DATE_SLT_PAT = """^DATE\\(.*\\)\$""";

    /**
     * 金额类型选取函数正则式
     */
    private final String MONEY_SLT_PAT = """^MONEY\$""";

    /**
     * 员工选取函数正则式
     */
    private final String STAFF_SLT_PAT = """^STAFF\$""";

    /**
     * 员工选取函数正则式
     */
    private final String ORG_SLT_PAT = """^ORG\$""";

    /**
     * 数值类型
     */
    private final String NUMBER_TYPE_PAT = "^((DOUBLE)|(INT)|(DECIMAL)|(NUMBER)).*";
    /**
     * 码表值选取函数正则式
     */
    private final String SIGN_SLT_PAT = "SIGN";

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        paramMap.remove("pageSize");
        paramMap.remove("sortColumn");
        paramMap.remove("sortOrder");
        paramMap.remove("sortColumn");
        paramMap.remove("queryCondition");

        String reqPath = "SvcCont.";
        //扩展支持自定义 组件查询  @param selfDefCmpt: 自定义调用组件 名称
        String selfDefCmpt = requestUtil.getStrParam("selfDefCmpt");
        if (StringUtil.isNotEmptyOrNull(selfDefCmpt)) {
            reqPath = "OpData.";
        }
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue(reqPath + "PageInfo.currentPage", requestUtil.getStrParam("currentPage"));
        reqData.setValue(reqPath + "PageInfo.pageSize", requestUtil.getStrParam("pageSize"));
        // 排序信息
        reqData.setValue(reqPath + "SortInfo.sortColumn", requestUtil.getStrParam("sortColumn"));
        reqData.setValue(reqPath + "SortInfo.sortOrder", requestUtil.getStrParam("sortOrder"));
        // SQL信息
        reqData.setStrValue(reqPath + "QuerySvc.subSvcName", requestUtil.getStrParam("subSvcName"));
        int i = 0;
        for (Map.Entry tempItem : paramMap) {
            reqData.setStrValue(reqPath + "ParamInfo.Param[${i}].name", tempItem.getKey());
            reqData.setStrValue(reqPath + "ParamInfo.Param[${i}].value", tempItem.getValue());
            i++;
        }

        // 调用服务 或 调用组件
        SvcResponse svcResponse = null;
        //扩展支持自定义 组件查询
        if (StringUtil.isNotEmptyOrNull(selfDefCmpt)) {
            svcRequest.addOp(selfDefCmpt, reqData);
            svcResponse = query(svcRequest);
        } else {
            String serviceName = "pageService";
            String serviceMethod = "queryPageData";
            String selfDefSvc = requestUtil.getStrParam("selfDefSvc");
            if (StringUtil.isNotEmptyOrNull(selfDefSvc)) {
                String[] temp = selfDefSvc.split("-");
                serviceName = temp[0];
                serviceMethod = temp[1];
            }
            svcRequest.setReqData(reqData);
            svcResponse = callService(serviceName, serviceMethod, svcRequest);
        }

        // 添加分页查询数据参数
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            //获取出参
            XmlBean rspData = null;
            Map<String, Object> pagination = new HashMap<String, Object>();
            if (StringUtil.isNotEmptyOrNull(selfDefCmpt)) {
                rspData = svcResponse.getFirstOpRsp(selfDefCmpt).getBeanByPath("Operation.OpResult");
                int rowNum = rspData.getListNum("OpResult.PageData.Row");
                List resultList = new ArrayList(rowNum);
                for (int j = 0; j < rowNum; j++) {
                    XmlBean result = rspData.getBeanByPath("OpResult.PageData.Row[${j}]");
                    resultList.add(result.getRootNode());
                }
                // 返回结果集
                modelMap.addAttribute("returnList", resultList);
                pagination.put("currentPage", rspData.getValue("OpResult.PageInfo.currentPage"));
                pagination.put("totalPage", rspData.getValue("OpResult.PageInfo.totalPage"));
                pagination.put("totalRecord", rspData.getValue("OpResult.PageInfo.totalRecord"));

            } else {
                rspData = svcResponse.getRspData();
                // 返回结果集
                modelMap.addAttribute("returnList", rspData.getValue("SvcCont.PageData"));
                pagination.put("currentPage", rspData.getValue("SvcCont.PageInfo.currentPage"));
                pagination.put("totalPage", rspData.getValue("SvcCont.PageInfo.totalPage"));
                pagination.put("totalRecord", rspData.getValue("SvcCont.PageInfo.totalRecord"));
            }
            // 分页信息
            modelMap.addAttribute("pagination", pagination);
            // 排序字段
            modelMap.addAttribute("sortColumn", requestUtil.getStrParam("sortColumn"));
            // 排序方向
            modelMap.addAttribute("sortOrder", requestUtil.getStrParam("sortOrder"));
            // 每页显示页数
            modelMap.addAttribute("pageSize", requestUtil.getStrParam("pageSize"));

        } else {
            modelMap.put("errMsg", svcResponse.getErrMsg());
        }
        // 排序查询条件
        // 调整页面
        String forward = paramMap.get("forward").toString();
        JSONObject json = JSONObject.fromObject(paramMap);
        modelMap.addAttribute("queryCondition", json.toString());
        modelMap.put("prjCd", request.getParameter("prjCd"));
        // divId
        modelMap.addAttribute("divId", paramMap.get("divId"));
        // 当前操作用户工号
        modelMap.addAttribute("loginStaffId", svcRequest.getReqStaff());
        if (StringUtil.isEmptyOrNull(paramMap.get("remindFlag"))) {
            modelMap.addAttribute("remindFlag", "0");
        } else {
            modelMap.addAttribute("remindFlag", paramMap.get("remindFlag"));
        }
        // 跳转页面
        return new ModelAndView(forward, modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void export(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        paramMap.remove("pageSize");
        paramMap.remove("sortColumn");
        paramMap.remove("sortOrder");
        paramMap.remove("sortColumn");
        paramMap.remove("queryCondition");

        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // 定义到处的数据大小
        reqData.setValue("SvcCont.PageInfo.currentPage", "1");
        reqData.setValue("SvcCont.PageInfo.pageSize", "65536");
        // 排序信息
        reqData.setValue("SvcCont.SortInfo.sortColumn", requestUtil.getStrParam("sortColumn"));
        reqData.setValue("SvcCont.SortInfo.sortOrder", requestUtil.getStrParam("sortOrder"));
        // SQL信息
        reqData.setStrValue("SvcCont.QuerySvc.subSvcName", requestUtil.getStrParam("subSvcName"));
        int i = 0;
        for (Map.Entry tempItem : paramMap) {
            reqData.setStrValue("SvcCont.ParamInfo.Param[${i}].name", tempItem.getKey());
            reqData.setStrValue("SvcCont.ParamInfo.Param[${i}].value", tempItem.getValue());
            i++;
        }
        svcRequest.setReqData(reqData);
        String serviceName = "pageService";
        String serviceMethod = "queryPageData";
        String selfDefSvc = requestUtil.getStrParam("selfDefSvc");
        if (StringUtil.isNotEmptyOrNull(selfDefSvc)) {
            String[] temp = selfDefSvc.split("-");
            serviceName = temp[0];
            serviceMethod = temp[1];
        }
        // 调用服务
        SvcResponse svcResponse = callService(serviceName, serviceMethod, svcRequest);
        // 添加分页查询数据参数
        String data = "{}";
        if (svcResponse.isSuccess()) {
            //
            XmlBean rspData = svcResponse.getRspData();
            // 获取查询结果
            List<Map<String, Object>> result = rspData.getValue("SvcCont.PageData");
            //
            String templatePath = request.getParameter("templatePath");
            if (log.debugEnabled) {
                log.debug("模板路径：" + templatePath);
            }
            // 返回结果集
            //使用新模版需要传过来路径，路径不存在默认走老版本逻辑
            ExcelWriter excelWriter;
            String excelName;
            String excelPath;
            int j = 0;
            //模板文件
            //String templatePath = "webroot:eland/hs/hs001/房源信息导出excl模版.xls";//测试房源信息
            if (StringUtil.isEmptyOrNull(templatePath) || (StringUtil.isNotEmptyOrNull(templatePath) && new File(templatePath).exists())) {
                String colNames = requestUtil.getStrParam("colNames");
                String colModels = requestUtil.getStrParam("colModels");
                excelName = UUID.randomUUID().toString() + ".xls";
                excelPath = "export/" + excelName;
                File excelFile = ServerFile.createFile("export" + File.separator, excelName);
                excelWriter = new ExcelWriter(excelFile);
                excelWriter.createSheet("导出数据", 0);
                String[] valueKeys = null;
                if (!StringUtil.isEmptyOrNull(colNames)) {
                    excelWriter.writeData(j, 0, colNames.split(","));
                    valueKeys = colModels.split(",");
                    j++;
                }
                for (Map<String, Object> rowData : result) {
                    excelWriter.writeData(j, 0, convertToString(rowData, valueKeys));
                    j++;
                }
            } else {
                templatePath = StringUtil.formatFilePath(templatePath);
                //输出excel文件
                excelName = UUID.randomUUID().toString() + ".xls";
                excelPath = "temp/" + excelName;
                File templateFile = new File(templatePath);
                ExcelReader excelReader = new ExcelReader(templateFile);
                excelReader.changeSheet("导出数据");
                //1，获取匹配字段
                String[] regexsStr = excelReader.readData(1);
                //2，处理获得的字段，交给方法处理返回string[] ，  将${ xxxx} 替换为真实值  处理函数（xxxx，xxxx），替换为真实值
                excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), templateFile);
                excelWriter.changeSheet("导出数据");
                //从第二行写起，覆盖掉匹配字段
                //3，调用函数，获得写出行数组
                int count = 1;
                for (Map<String, Object> rowData : result) {
                    if (count > 1) {
                        excelWriter.insertRow(count + 2);
                        excelWriter.copyRow(count + 1, count + 2);
                    }
                    excelWriter.writeData(count, 0, temFunchion(rowData, regexsStr, count, request));
                    count++;
                }
            }
            excelWriter.closeBook();
            data = """ {\"remoteFile\": \"${excelPath}\", "clientFile": "${excelName}\"}  """
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

    /**
     * 分页查询条件自定义
     * @param request 请求信息
     * @param response 响应信息
     * @return 返回分页查询条件自定义界面
     */
    public ModelAndView condition(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String reqStaff = svcRequest.getReqStaff();
        String prjCd = request.getParameter("prjCd");
        String entityName = requestUtil.getStrParam("entityName");
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String resultFields = requestUtil.getStrParam("resultFields");

        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        String[] resultFieldArr = resultFields.split(",");

        // 按照属性名称整理查询字段的所有值
        Map<String, XmlBean> conditionMap = new LinkedHashMap<String, XmlBean>();

        for (int i = 0; i < conditionFieldArr.length; i++) {
            String conditionField = conditionFieldArr[i];
            if (StringUtil.isEmptyOrNull(conditionField)) {
                continue;
            }
            XmlBean xmlBean = conditionMap.get(conditionField);
            if (xmlBean == null) {
                xmlBean = new XmlBean();
                conditionMap.put(conditionField, xmlBean);
            }
            // 追加查询条件
            XmlBean temp = new XmlBean();
            temp.setStrValue("Cond.condition", conditionArr[i]);
            temp.setStrValue("Cond.conditionValue", conditionValueArr[i]);
            xmlBean.setBeanByPath("CondField", temp);
        }
        // 排序信息
        String[] sortColumnArr = requestUtil.getStrParam("sortColumn").split(",");
        String[] sortOrderArr = requestUtil.getStrParam("sortOrder").split(",", sortColumnArr.length);
        Map<String, String> sortMap = new HashMap<String, String>();
        for (int i = 0; i < sortColumnArr.length; i++) {
            if (StringUtil.isNotEmptyOrNull(sortColumnArr[i])) {
                sortMap.put(sortColumnArr[i], sortOrderArr[i]);
            }
        }
        // 必须包含的字段
        String[] forceResultField = requestUtil.getStrParam("forceResultField").split(",");
        // 请求参数
        String prePath = "OpData.";

        //根据结果字段判断是否是跨实体查询    by  结果字段含有点即为 跨实体查询
        String cmptName = "QUERY_ENTITY_CONFIG_CMPT";
        if (resultFieldArr[0].indexOf(".") != -1) {
            //是跨实体查询
            cmptName = "QUERY_ENTITY_ALL_CFG_INFO";
        }

        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", entityName);
        if (StringUtil.isEqual(cmptName, "QUERY_ENTITY_CONFIG_CMPT")) {
            opData.setStrValue("OpData.prjCd", prjCd);
        }
        svcRequest.addOp(cmptName, opData);
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "StaffFavoriteCondition");
        opData.setStrValue("OpData.StaffFavoriteCondition.entityName", entityName);
        opData.setStrValue("OpData.StaffFavoriteCondition.favStaff", reqStaff);
        opData.setStrValue("OpData.StaffFavoriteCondition.opCode", request.getParameter("tabOpCode"));
        svcRequest.addOp("QUERY_FAVORITE_CMPT", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取实体属性
            XmlBean entity = svcResponse.getFirstOpRsp(cmptName).getBeanByPath("Operation.OpResult.Entity");
            Map<String, List<XmlNode>> groups = new LinkedHashMap<String, List<XmlNode>>();
            Map<String, XmlNode> resultFieldMap = new HashMap<String, XmlNode>();
            // 分组属性XmlBean→→→→→→→→→List集合
            getFormatGroups(entity, conditionMap, sortMap, forceResultField, resultFieldArr, groups, resultFieldMap);
            // 处理自定义作业字表，不允许作为检索条件，不允许排序
            int jobDefCount = entity.getListNum("Entity.JobDefs.JobDef");
            for (int i = 0; i < jobDefCount; i++) {
                XmlBean temp = entity.getBeanByPath("Entity.JobDefs.JobDef[${i}]");
                String groupName = temp.getStrValue("JobDef.jobGroupName");
                if (StringUtil.isEmptyOrNull(groupName)) {
                    groupName = "统计指标";
                }
                List<XmlNode> groupAttrList = groups.get(groupName);
                if (groupAttrList == null) {
                    groupAttrList = new ArrayList<XmlNode>(jobDefCount);
                    groups.put(groupName, groupAttrList);
                }
                // 增加本指标
                temp.setStrValue("Attr.entityAttrNameEn", temp.getStrValue("JobDef.jobCd"));
                temp.setStrValue("Attr.entityAttrNameCh", temp.getStrValue("JobDef.jobName"));
                temp.setStrValue("Attr.canConditionFlag", "0");
                temp.setStrValue("Attr.canOrderFlag", "0");
                temp.setStrValue("Attr.canResultFlag", "1");
                // 是否已选择输出字段
                if (resultFieldArr.contains(temp.getStrValue("JobDef.jobCd"))) {
                    temp.setStrValue("Attr.addClass", "selected");
                    resultFieldMap.put(temp.getStrValue("JobDef.jobCd"), temp.getRootNode());
                }
                groupAttrList.add(temp.getRootNode());
            }
            // 显示的字段
            List<XmlNode> resultFieldList = new ArrayList<XmlNode>();
            for (String temp : resultFieldArr) {
                XmlNode tempNode = resultFieldMap.get(temp);
                if (tempNode != null) {
                    resultFieldList.add(tempNode);
                }
            }
            modelMap.put("resultFieldList", resultFieldList);
            modelMap.put("groups", groups);
            // 转换查询条件
            List<XmlNode> queryCondList = new ArrayList<XmlNode>(conditionMap.size());
            for (Map.Entry<String, XmlBean> temp : conditionMap) {
                XmlBean queryCond = temp.getValue();
                String entityAttrNameEn = queryCond.getStrValue("CondField.entityAttrNameEn");
                // 获取该字段的所有查询条件
                int condCount = queryCond.getListNum("CondField.Cond");
                String[] fieldConditionNames = new String[condCount];
                String[] fieldConditions = new String[condCount];
                String[] fieldConditionValues = new String[condCount];
                // 拼接查询条件
                for (int i = 0; i < condCount; i++) {
                    XmlBean tempCon = queryCond.getBeanByPath("CondField.Cond[${i}]");
                    fieldConditionNames[i] = entityAttrNameEn;
                    fieldConditions[i] = tempCon.getStrValue("Cond.condition");
                    fieldConditionValues[i] = tempCon.getStrValue("Cond.conditionValue");
                }
                queryCond.setStrValue("CondField.conditionName", fieldConditionNames.join(","));
                queryCond.setStrValue("CondField.condition", fieldConditions.join(","));
                queryCond.setStrValue("CondField.conditionValue", fieldConditionValues.join(","));

                // 获取查询字面值
                String conditionText = "";
                String entitySaveType = queryCond.getStrValue("CondField.entitySaveType");
                String attrFrontSltRule = queryCond.getStrValue("CondField.attrFrontSltRule");
                Map<String, String> condition = getCfgCollection(request, "QUERY_CONDITION", true);

                if (attrFrontSltRule.matches(CODE_SLT_PAT)) {
                    // 选择码表数据
                    String codeValue = attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2);
                    Map<String, String> codeMap = getCfgCollection(request, codeValue, true, NumberUtil.getIntFromObj(prjCd));
                    List<String> disArr = new ArrayList<String>(fieldConditionValues.length);
                    for (String tempStr : fieldConditionValues) {
                        for (String realT : tempStr.split("\\|")) {
                            if (StringUtil.isNotEmptyOrNull(realT)) {
                                disArr.add(codeMap.get(realT));
                            }
                        }
                    }
                    conditionText = disArr.join("、");
                } else  if (attrFrontSltRule.matches(TREE_CODE_SLT_PAT)) {
                    // 选择码表数据
                    String codeValue = attrFrontSltRule.substring(11, attrFrontSltRule.length() - 2);
                    Map<String, String> codeMap = getCfgCollection(request, codeValue, true, NumberUtil.getIntFromObj(prjCd));
                    List<String> disArr = new ArrayList<String>(fieldConditionValues.length);
                    for (String tempStr : fieldConditionValues) {
                        for (String realT : tempStr.split("\\|")) {
                            if (StringUtil.isNotEmptyOrNull(realT)) {
                                disArr.add(codeMap.get(realT));
                            }
                        }
                    }
                    conditionText = disArr.join("、");
                } else if (attrFrontSltRule.matches(STAFF_SLT_PAT)) {
                    List<String> disArr = new ArrayList<String>();
                    // 选择码表数据
                    for (String tempStr : fieldConditionValues) {
                        if (StringUtil.isNotEmptyOrNull(tempStr)) {
                            disArr.add(staff([tempStr], "", request));
                        }
                    }
                    conditionText = disArr.join("、");
                } else if (attrFrontSltRule.matches(ORG_SLT_PAT)) {
                    List<String> disArr = new ArrayList<String>();
                    // 选择码表数据
                    for (String tempStr : fieldConditionValues) {
                        if (StringUtil.isNotEmptyOrNull(tempStr)) {
                            disArr.add(org([prjCd, tempStr], "Org.Node.orgName", request));
                        }
                    }
                    conditionText = disArr.join("、");
                } else if (entitySaveType.startsWith("INT") || entitySaveType.startsWith("NUMBER")
                        || entitySaveType.startsWith("DOUBLE")
                        || entitySaveType.startsWith("DATE") || entitySaveType.startsWith("DATETIME")) {
                    List<String> disArr = new ArrayList<String>();
                    boolean isDate = attrFrontSltRule.matches(DATE_SLT_PAT);
                    String dateFormat = "";
                    if (isDate) {
                        dateFormat = attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2);
                    }
                    for (int i = 0; i < fieldConditionValues.length; i++) {
                        String cTmpStr = fieldConditionValues[i];
                        if (isDate) {
                            cTmpStr = DateUtil.format(DateUtil.toDateYmdHms(cTmpStr), dateFormat)
                        }
                        String cNTmpStr = fieldConditions[i];
                        if (StringUtil.isNotEmptyOrNull(cTmpStr)) {
                            if (">=".equals(cNTmpStr)) {
                                disArr.add(cTmpStr);
                                disArr.add("-");
                            } else if ("<=".equals(cNTmpStr)) {
                                if (disArr.size() > 0 && "-".equals(disArr.last())) {
                                    disArr.add(cTmpStr);
                                } else {
                                    disArr.add("-");
                                    disArr.add(cTmpStr);
                                }
                            } else {
                                disArr.add(condition.get(cNTmpStr) + """ "${cTmpStr}" """);
                            }
                        }
                    }
                    conditionText = disArr.join("");
                } else {
                    List<String> disArr = new ArrayList<String>();
                    for (int i = 0; i < fieldConditionValues.length; i++) {
                        String cTmpStr = fieldConditionValues[i];
                        String cNTmpStr = fieldConditions[i];
                        disArr.add(condition.get(cNTmpStr) + """ "${cTmpStr}" """);
                    }
                    conditionText = disArr.join("、");
                }
                queryCond.setStrValue("CondField.conditionText", conditionText);
                // 对二维码进行修正
                if (temp.key.startsWith("UID.")) {
                    // 增到返回结果中
                    queryCond.setStrValue("CondField.entityAttrNameCh", "二维码");
                    queryCond.setStrValue("CondField.entityAttrNameEn", temp.key);
                }
                // 增到返回结果中
                queryCondList.add(queryCond.getRootNode());
            }
            modelMap.put("queryCondList", queryCondList);
            // 获取收藏的结果
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_FAVORITE_CMPT");
            int attrCount = queryResult.getListNum("Operation.OpResult.StaffFavoriteCondition");
            List<XmlNode> favinfo = new ArrayList<XmlNode>(attrCount);
            for (int i = 0; i < attrCount; i++) {
                XmlBean tempBean = queryResult.getBeanByPath("Operation.OpResult.StaffFavoriteCondition[${i}]");
                favinfo.add(tempBean.getRootNode());
            }
            modelMap.put("favinfo", favinfo);
        }
        // 返回处理结果
        String canDefResult = request.getParameter("canDefResult");
        if (StringUtil.isEmptyOrNull(canDefResult)) {
            canDefResult = "true";
        }
        modelMap.put("canDefResult", canDefResult);
        modelMap.put("entityName", entityName);
        return new ModelAndView("/oframe/common/page/page_condition", modelMap);
    }

    /**
     * 分页查询条件自定义(带标签查询，按人员条件查询)
     * @param request 请求信息
     * @param response 响应信息
     * @return 返回分页查询条件自定义界面
     */
    public ModelAndView conditionTwo(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String reqStaff = svcRequest.getReqStaff();
        String prjCd = request.getParameter("prjCd");
        String entityName = requestUtil.getStrParam("entityName");
        /********* 条件获取 *********** 条件获取 ************ 条件获取 *********** 条件获取 ***********/
        // 房屋条件获取
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        // 要查询的结果字段
        String resultFields = requestUtil.getStrParam("resultFields");
        // 自动补全条件、人员条件、标签条件获取
        String autoConditions = requestUtil.getStrParam("autoConditions");
        modelMap.put("autoConditions", autoConditions);
        String psConditions = requestUtil.getStrParam("psConditions");
        String tagConditions = requestUtil.getStrParam("tagConditions");
        /********* 条件数组转换 *********** 条件数组转换 ************ 条件数组转换 *********** 条件数组转换 ***********/
        // （房屋实体及其子实体）检索条件转数组
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        // 检索结果转数组
        String[] resultFieldArr = resultFields.split(",");
        // （人员实体）检索条件转数组 TODO json格式转换
        String[] conditionPsFieldArr = [];
        String[] conditionPsArr = [];
        String[] conditionPsValueArr = [];
        if (StringUtil.isNotEmptyOrNull(psConditions)) {
            JSONArray psConJA = JSONArray.parse(psConditions);
            conditionPsFieldArr = new String[psConJA.size()];
            conditionPsArr = new String[psConJA.size()];
            conditionPsValueArr = new String[psConJA.size()];
            for (int i = 0; i < psConJA.size(); i++) {
                JSONObject psConJO = psConJA.get(i);
                conditionPsFieldArr[i] = psConJO.get("conditionName");
                conditionPsArr[i] = psConJO.get("condition");
                conditionPsValueArr[i] = psConJO.get("conditionValue");
            }
        }
        // （标签信息）检索条件转数组 TODO json格式转换
        String[] conditionTagFieldArr = [];
        String[] conditionTagArr = [];
        String[] conditionTagValueArr = [];
        String tagIds = "";
        if (StringUtil.isNotEmptyOrNull(tagConditions)) {
            JSONArray tagConJA = JSONArray.parse(tagConditions);
            conditionTagFieldArr = new String[tagConJA.size()];
            conditionTagArr = new String[tagConJA.size()];
            conditionTagValueArr = new String[tagConJA.size()];
            for (int i = 0; i < tagConJA.size(); i++) {
                JSONObject tagConJO = tagConJA.get(i);
                conditionTagFieldArr[i] = tagConJO.get("conditionName");
                conditionTagArr[i] = tagConJO.get("condition");
                conditionTagValueArr[i] = tagConJO.get("conditionValue");
                tagIds = tagIds + conditionTagFieldArr[i] + ",";
            }
        }
        if (StringUtil.isNotEmptyOrNull(tagIds)) {
            tagIds = tagIds.substring(0, tagIds.length() - 1);
        }
        /********* 条件处理 *********** 条件处理 ************ 条件处理 *********** 条件处理 ***********/
        // （房屋实体及其子实体）按照属性名称整理查询条件
        Map<String, XmlBean> conditionMap = formatCondition(conditionFieldArr, conditionArr, conditionValueArr);
        // （房屋实体及其子实体）排序信息
        String[] sortColumnArr = requestUtil.getStrParam("sortColumn").split(",");
        String[] sortOrderArr = requestUtil.getStrParam("sortOrder").split(",", sortColumnArr.length);
        Map<String, String> sortMap = new HashMap<String, String>();
        for (int i = 0; i < sortColumnArr.length; i++) {
            if (StringUtil.isNotEmptyOrNull(sortColumnArr[i])) {
                sortMap.put(sortColumnArr[i], sortOrderArr[i]);
            }
        }
        // 必须包含的字段
        String[] forceResultField = requestUtil.getStrParam("forceResultField").split(",");
        // （人员实体）条件处理
        // TODO （人员实体）按照属性名称整理查询条件（已完成）
        Map<String, XmlBean> conditionPsMap = formatCondition(conditionPsFieldArr, conditionPsArr, conditionPsValueArr);
        // （标签信息）条件处理
        // TODO （标签信息）按照属性名称整理查询条件（已完成）
        Map<String, XmlBean> conditionTagMap = formatCondition(conditionTagFieldArr, conditionTagArr, conditionTagValueArr);
        /********* 获取实体属性报文 *********** 获取实体属性报文 ************ 获取实体属性报文 *********** 获取实体属性报文 ***********/
        // 返回处理结果
        XmlBean opData = new XmlBean();
        // 拼接查询房屋实体属性
        opData.setStrValue("OpData.entityName", "HouseInfo");
        svcRequest.addOp("QUERY_ENTITY_ALL_CFG_INFO", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取（房屋实体及其子实体）属性
            XmlBean hsEntity = svcResponse.getFirstOpRsp("QUERY_ENTITY_ALL_CFG_INFO").getBeanByPath("Operation.OpResult.Entity");
            // 处理（房屋实体及其子实体）所有分组
            Map<String, List<XmlNode>> hsGroups = new LinkedHashMap<String, List<XmlNode>>();
            Map<String, XmlNode> resultHsMap = new HashMap<String, XmlNode>();
            getFormatGroups(hsEntity, conditionMap, sortMap, forceResultField, resultFieldArr, hsGroups, resultHsMap);
            // 处理自定义作业字表，不允许作为检索条件，不允许排序
            int jobDefCount = hsEntity.getListNum("Entity.JobDefs.JobDef");
            for (int i = 0; i < jobDefCount; i++) {
                XmlBean temp = hsEntity.getBeanByPath("Entity.JobDefs.JobDef[${i}]");
                String groupName = temp.getStrValue("JobDef.jobGroupName");
                if (StringUtil.isEmptyOrNull(groupName)) {
                    groupName = "统计指标";
                }
                List<XmlNode> groupAttrList = hsGroups.get(groupName);
                if (groupAttrList == null) {
                    groupAttrList = new ArrayList<XmlNode>(jobDefCount);
                    hsGroups.put(groupName, groupAttrList);
                }
                // 增加本指标
                temp.setStrValue("Attr.entityAttrNameEn", temp.getStrValue("JobDef.jobCd"));
                temp.setStrValue("Attr.entityAttrNameCh", temp.getStrValue("JobDef.jobName"));
                temp.setStrValue("Attr.canConditionFlag", "0");
                temp.setStrValue("Attr.canOrderFlag", "0");
                temp.setStrValue("Attr.canResultFlag", "1");
                // 是否已选择输出字段
                if (resultFieldArr.contains(temp.getStrValue("JobDef.jobCd"))) {
                    temp.setStrValue("Attr.addClass", "selected");
                    resultHsMap.put(temp.getStrValue("JobDef.jobCd"), temp.getRootNode());
                }
                groupAttrList.add(temp.getRootNode());
            }
            // 显示的字段
            List<XmlNode> resultFieldList = new ArrayList<XmlNode>();
            for (String temp : resultFieldArr) {
                XmlNode tempNode = resultHsMap.get(temp);
                if (tempNode != null) {
                    resultFieldList.add(tempNode);
                }
            }
            modelMap.put("resultFieldList", resultFieldList);
            modelMap.put("hsGroups", hsGroups);
            // 转换（房屋实体及其子实体）查询条件
            List<XmlNode> queryCondList = formatQueryCondition(request, prjCd, conditionMap);
            modelMap.put("queryCondList", queryCondList);
        }
        // 查询人员实体属性
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "PersonInfo");
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_ALL_CFG_INFO", opData);
        // 调用服务
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取（人员实体）属性
            XmlBean psEntity = svcResponse.getFirstOpRsp("QUERY_ENTITY_ALL_CFG_INFO").getBeanByPath("Operation.OpResult.Entity");
            // 处理（人员实体）所有分组
            Map<String, List<XmlNode>> psGroups = new LinkedHashMap<String, List<XmlNode>>();
            Map<String, XmlNode> resultPsMap = new HashMap<String, XmlNode>();
            getFormatGroups(psEntity, conditionPsMap, new HashMap<String, String>(), new String[0], new String[0], psGroups, resultPsMap);
            modelMap.put("psGroups", psGroups);
            // 转换（人员实体）查询条件
            List<XmlNode> queryPsCondList = formatQueryCondition(request, prjCd, conditionPsMap);
            modelMap.put("queryPsCondList", queryPsCondList);
        }
        // TODO 按条件查询全部标签（已完成）
        opData = new XmlBean();
        opData.setStrValue("OpData.tagId", "");
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_SIGN_CMPT", opData);
        // 调用服务
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取标签主标题
            XmlBean tagEntity = svcResponse.getFirstOpRsp("QUERY_SIGN_CMPT").getBeanByPath("Operation.OpResult");
            // 标签报文结构转为实体属性格式
            XmlBean formatToAttrBean = getTagFormatGroups(tagEntity);
            // 处理（标签信息）所有分组
            Map<String, List<XmlNode>> tagGroups = new LinkedHashMap<String, List<XmlNode>>();
            Map<String, XmlNode> resultTagMap = new HashMap<String, XmlNode>();
            getFormatGroups(formatToAttrBean, conditionTagMap, new HashMap<String, String>(), new String[0], new String[0], tagGroups, resultTagMap);
            modelMap.put("tagGroups", tagGroups);
            // 转换（标签信息）查询条件
            List<XmlNode> queryTagCondList = formatTagQueryCondition(request, conditionTagMap);
            modelMap.put("queryTagCondList", queryTagCondList);
        }
        // 查询收藏信息
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "StaffFavoriteCondition");
        opData.setStrValue("OpData.StaffFavoriteCondition.entityName", entityName);
        opData.setStrValue("OpData.StaffFavoriteCondition.favStaff", reqStaff);
        opData.setStrValue("OpData.StaffFavoriteCondition.opCode", request.getParameter("tabOpCode"));
        svcRequest.addOp("QUERY_FAVORITE_CMPT", opData);
        // 调用服务
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_FAVORITE_CMPT");
            int attrCount = queryResult.getListNum("Operation.OpResult.StaffFavoriteCondition");
            List<XmlNode> favinfo = new ArrayList<XmlNode>(attrCount);
            for (int i = 0; i < attrCount; i++) {
                XmlBean tempBean = queryResult.getBeanByPath("Operation.OpResult.StaffFavoriteCondition[${i}]");
                favinfo.add(tempBean.getRootNode());
            }
            modelMap.put("favinfo", favinfo);
        }
        // 返回处理结果
        String canDefResult = request.getParameter("canDefResult");
        if (StringUtil.isEmptyOrNull(canDefResult)) {
            canDefResult = "true";
        }
        modelMap.put("canDefResult", canDefResult);
        modelMap.put("entityName", entityName);
        return new ModelAndView("/oframe/common/page/page_condition_two", modelMap);
    }

    /**
     * 收藏查询
     * @param request 请求信息
     * @param response 响应信息
     * @return 返回分页查询条件自定义界面
     */
    public ModelAndView favList(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String reqStaff = svcRequest.getReqStaff();
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "StaffFavoriteCondition");
        opData.setStrValue("OpData.StaffFavoriteCondition.entityName", request.getParameter("entityName"));
        opData.setStrValue("OpData.StaffFavoriteCondition.favStaff", reqStaff);
        opData.setStrValue("OpData.StaffFavoriteCondition.opCode", request.getParameter("tabOpCode"));
        svcRequest.addOp("QUERY_FAVORITE_CMPT", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取收藏的结果
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_FAVORITE_CMPT");
            int attrCount = queryResult.getListNum("Operation.OpResult.StaffFavoriteCondition");
            List<XmlNode> favinfo = new ArrayList<XmlNode>(attrCount);
            for (int i = 0; i < attrCount; i++) {
                XmlBean tempBean = queryResult.getBeanByPath("Operation.OpResult.StaffFavoriteCondition[${i}]");
                favinfo.add(tempBean.getRootNode());
            }
            modelMap.put("favinfo", favinfo);
        }
        return new ModelAndView("/oframe/common/page/page_fav_list", modelMap);
    }

    /**
     * 选择查询条件
     * @param request 请求信息
     * @param response 响应信息
     * @return 返回分页查询条件自定义界面
     */
    public ModelAndView valueDef(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String entityName = requestUtil.getStrParam("entityName");
        String prjCd = requestUtil.getStrParam("prjCd");
        String entityAttrNameEn = requestUtil.getStrParam("entityAttrNameEn");
        String condition = requestUtil.getStrParam("condition");
        String conditionValue = requestUtil.getStrParam("conditionValue");
        // 返回处理结果
        XmlBean opData = new XmlBean();

        opData.setStrValue("OpData.entityName", entityName);
        String componentName = "QUERY_ENTITY_CONFIG_CMPT";
        if (entityAttrNameEn.contains(".")) {
            componentName = "QUERY_ENTITY_ALL_CFG_INFO";
        }
        if (StringUtil.isEqual(componentName, "QUERY_ENTITY_CONFIG_CMPT")) {
            opData.setStrValue("OpData.prjCd", prjCd);
        }
        svcRequest.addOp(componentName, opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);

        // 处理返回结果
        XmlBean attrBean = null;
        if (svcResponse.isSuccess()) {
            // 获取实体属性
            XmlBean entity = svcResponse.getFirstOpRsp(componentName)
                    .getBeanByPath("Operation.OpResult.Entity");
            // 处理所有分组
            int groupCount = entity.getListNum("Entity.Groups.Group");

            for (int i = 0; i < groupCount; i++) {
                XmlBean temp = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                // 获取属性
                int attrCount = temp.getListNum("Group.Attrs.Attr");
                for (int k = 0; k < attrCount; k++) {
                    XmlBean tempBean = temp.getBeanByPath("Group.Attrs.Attr[${k}]");
                    if (StringUtil.isEqual(entityAttrNameEn, tempBean.getStrValue("Attr.entityAttrNameEn"))) {
                        attrBean = tempBean;
                        break;
                    }
                }
                if (attrBean != null) {
                    break;
                }
            }
        }
        String forwardPage = "/oframe/common/page/value_slt_text";
        if (attrBean != null) {
            // 是否可以排序
            String canOrderFlag = attrBean.getStrValue("Attr.canOrderFlag");
            modelMap.put("canOrder", "1".equals(canOrderFlag));
            // 获取类型
            String entitySaveType = attrBean.getStrValue("Attr.entitySaveType");
            String attrFrontSltRule = attrBean.getStrValue("Attr.attrFrontSltRule");
            // startValue, endValue
            String startValue = null;
            String endValue = null;
            List<String> sltValues = null;
            if (StringUtil.isNotEmptyOrNull(condition)) {
                List<String> tempCArr = condition.split(",");
                sltValues = conditionValue.split(",");
                int startIdx = tempCArr.indexOf(">=");
                if (startIdx >= 0) {
                    startValue = sltValues.get(startIdx);
                }
                int endIdx = tempCArr.indexOf("<=");

                if (endIdx >= 0) {
                    endValue = sltValues.get(endIdx);
                }
            }
            if (attrFrontSltRule.matches(CODE_SLT_PAT)) {
                // 选择码表数据
                forwardPage = "/oframe/common/page/value_slt_opt";
                String codeValue = attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2);
                Map<String, String> values = getCfgCollection(request, codeValue, true, NumberUtil.getIntFromObj(prjCd));
                modelMap.put("values", values);
                modelMap.put("sltValues", sltValues);
                String model = " ";
                if (codeValue.endsWith("_MORE")) {// 表示该属性在数据库中存储为，一格有多个值，用逗号隔开
                    model = "more";
                }
                modelMap.put("model", model);
            } else if (attrFrontSltRule.matches(TREE_CODE_SLT_PAT)) {
                // 选择码表数据
                forwardPage = "/oframe/common/page/value_slt_tree";
                String codeValue = attrFrontSltRule.substring(11, attrFrontSltRule.length() - 2);
                modelMap.put("sltValues", conditionValue);
                Map<String, String> values = getCfgCollection(request, codeValue, true, NumberUtil.getIntFromObj(prjCd));
                List<String> allNames = new ArrayList<String>();
                for(String temp: conditionValue.split("\\|")) {
                    String tempName = values.get(temp);
                    if(StringUtil.isNotEmptyOrNull(tempName)) {
                        allNames.add(tempName);
                    }
                }
                modelMap.put("sltNames", allNames.join(","));
                modelMap.put("itemCd", codeValue);
            } else if (attrFrontSltRule.matches(DATE_SLT_PAT)) {
                String dateFormat = attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2);
                if (StringUtil.isNotEmptyOrNull(startValue)) {
                    modelMap.put("startValue", DateUtil.format(DateUtil.toDateYmdHms(startValue), dateFormat));
                }
                if (StringUtil.isNotEmptyOrNull(endValue)) {
                    modelMap.put("endValue", DateUtil.format(DateUtil.toDateYmdHms(endValue), dateFormat));
                }
                modelMap.put("dateFormat", dateFormat);
                forwardPage = "/oframe/common/page/value_slt_date";
            } else if (attrFrontSltRule.matches(ORG_SLT_PAT)) {
                modelMap.put("conditionValue", conditionValue);
                forwardPage = "/oframe/common/page/value_slt_org";
            } else if (attrFrontSltRule.matches(STAFF_SLT_PAT)) {
                modelMap.put("conditionValue", conditionValue);
                forwardPage = "/oframe/common/page/value_slt_staff";
            } else if (entitySaveType.startsWith("INT") || entitySaveType.startsWith("NUMBER")
                    || entitySaveType.startsWith("DOUBLE")) {
                modelMap.put("startValue", startValue);
                modelMap.put("endValue", endValue);
                forwardPage = "/oframe/common/page/value_slt_num";
            } else {
                if (StringUtil.isEmptyOrNull(condition)) {
                    condition = "like";
                }
                modelMap.put("condition", condition);
                modelMap.put("conditionValue", conditionValue);
            }
        }
        return new ModelAndView(forwardPage, modelMap);
    }

    /**
     * 标签信息栏
     * 选择查询条件
     * @param request 请求信息
     * @param response 响应信息
     * @return 返回分页查询条件自定义界面
     */
    public ModelAndView tagValueDef(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String prjCd = requestUtil.getStrParam("prjCd");
        String parentTagId = requestUtil.getStrParam("entityAttrNameEn");
        String condition = requestUtil.getStrParam("condition");
        String conditionValue = requestUtil.getStrParam("conditionValue");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.tagId", parentTagId);
        svcRequest.addOp("QUERY_SIGN_CMPT", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        // 处理返回结果
        XmlBean attrBean = null;
        if (svcResponse.isSuccess()) {
            // 获取实体属性
            XmlBean tagBean = svcResponse.getFirstOpRsp("QUERY_SIGN_CMPT").getBeanByPath("Operation.OpResult");
            // 处理所有分组
            int tagCount = tagBean.getListNum("OpResult.Tag");
            if (tagCount == 1) {
                // 是否可以多选
                String isCheckbox = tagBean.getStrValue("OpResult.Tag.isCheckbox");
                Map<String, String> values = new LinkedHashMap<>();
                int childTagCount = tagBean.getListNum("OpResult.Tag.SubTags.Tag");
                for (int i = 0; i < childTagCount; i++) {
                    XmlBean tempBean = tagBean.getBeanByPath("OpResult.Tag.SubTags.Tag[${i}]");
                    String tagId = tempBean.getStrValue("Tag.tagId");
                    String tagName = tempBean.getStrValue("Tag.tagName");
                    values.put(tagId, tagName);
                }
                modelMap.put("values", values);
                modelMap.put("isCheckbox", "true".equals(isCheckbox));
                String startValue = null;
                String endValue = null;
                List<String> sltValues = null;
                if (StringUtil.isNotEmptyOrNull(condition)) {
                    List<String> tempCArr = condition.split(",");
                    sltValues = conditionValue.split(",");
                    int startIdx = tempCArr.indexOf(">=");
                    if (startIdx >= 0) {
                        startValue = sltValues.get(startIdx);
                    }
                    int endIdx = tempCArr.indexOf("<=");

                    if (endIdx >= 0) {
                        endValue = sltValues.get(endIdx);
                    }
                }
                modelMap.put("canOrder", false);
                modelMap.put("condition", condition);
                modelMap.put("sltValues", sltValues);
            } else {

            }
        }
        return new ModelAndView("/oframe/common/page/value_slt_sign", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView data(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        String entityName = requestUtil.getStrParam("entityName");
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String rhtType = requestUtil.getStrParam("rhtType");
        String resultFields = requestUtil.getStrParam("resultField");
        String forceResultField = requestUtil.getStrParam("forceResultField");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        String[] resultFieldArr = resultFields.split(",");
        modelMap.put("loginStaffId", svcRequest.getReqStaff());
        // 获取当天日期字符串
        String tempDay = DateUtil.toStringYmd(DateUtil.getSysDate());
        // 转为 年月日 Date类型
        Date upDate = DateUtil.toDateYmd(tempDay);
        // 转为 年月日时分秒 长度
        modelMap.put("tempDate", DateUtil.toStringYmdHms(upDate));
        //根据结果字段判断是否是跨实体查询
        boolean whetherCrossEntity = false;
        if (resultFields.indexOf(".") != -1) {
            //是跨实体查询
            whetherCrossEntity = true;
        }

        //将页面设置的必须 查询字段拼进结果字段中，查询具体值
        Set<String> resultNeedFieldSet = new LinkedHashSet<String>();
        if (StringUtil.isNotEmptyOrNull(forceResultField)) {
            String[] forceResultFieldArr = forceResultField.split(",");
            resultNeedFieldSet.addAll(forceResultFieldArr);
        }
        resultNeedFieldSet.addAll(resultFieldArr);
        String[] resultNeedFieldArr = resultNeedFieldSet.toArray();

        //获取工作流结果字段
        String taskResultField = requestUtil.getStrParam("taskResultField");
        String[] taskResultFieldArr = taskResultField.split(",");

        // 排序信息
        String[] sortColumnArr = requestUtil.getStrParam("sortColumn").split(",");
        String[] sortOrderArr = requestUtil.getStrParam("sortOrder").split(",", sortColumnArr.length);
        Map<String, String> sortMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < sortColumnArr.length; i++) {
            if (StringUtil.isNotEmptyOrNull(sortColumnArr[i])) {
                sortMap.put(sortColumnArr[i], sortOrderArr[i]);
            }
        }

        /* 查询配置数据*/
        String prePath = "OpData.";
        // 增加查询
        Map<String, XmlBean> resultTitle = getResultField(request, sortMap);
        // 实际需要查询的字段，包含需要翻译的字段
        Set<String> resultFieldSet = new LinkedHashSet<String>();
        resultFieldSet.addAll(resultFieldArr);

        // 结果标题
        List<XmlNode> resultTitleArr = new ArrayList<XmlNode>();
        for (XmlBean temp : resultTitle.values()) {
            if (temp == null) {
                continue;
            }
            String frontSltRule = temp.getStrValue("Result.frontSltRule");
            String attrNameEn = temp.getStrValue("Result.attrNameEn");
            String entitySaveType = temp.getStrValue("Result.entitySaveType");
            if (frontSltRule.matches(CODE_SLT_PAT) || frontSltRule.matches(TREE_CODE_SLT_PAT)) {
                resultFieldSet.add(attrNameEn + "_Name");
                temp.setStrValue("Result.frontSltRuleView", "CODE");
            } else if (frontSltRule.matches(DATE_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "DATE");
                temp.setStrValue("Result.frontSltRuleViewParam", frontSltRule.substring(6, frontSltRule.length() - 2));
            } else if (frontSltRule.matches(ORG_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "ORG");
            } else if (frontSltRule.matches(MONEY_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "MONEY");
            } else if (frontSltRule.matches(STAFF_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "STAFF");
            } else if (StringUtil.isEqual(frontSltRule, SIGN_SLT_PAT)) {
                resultFieldSet.add(attrNameEn + "_Name");
                temp.setStrValue("Result.frontSltRuleView", "SIGN");
            }
            // 设置数字类型, 用于前端汇总
            if (frontSltRule.matches(ORG_SLT_PAT)) {
                temp.setValue("Result.isNumber", false);
            } else if (entitySaveType.matches(NUMBER_TYPE_PAT)) {
                temp.setValue("Result.isNumber", true);
            } else {
                temp.setValue("Result.isNumber", false);
            }
            resultTitleArr.add(temp.getRootNode());
        }
        modelMap.put("resultTitle", resultTitleArr);

        /* 查询数据*/
        XmlBean opData = new XmlBean();

        /**
         * 查询是否有查询 工作流程任务
         */
        if (StringUtil.isNotEmptyOrNull(request.getParameter("wfTaskFlag"))) {
            opData.setStrValue(prePath + "procDefKey", request.getParameter("procDefKey"));
            opData.setStrValue(prePath + "taskDefId", request.getParameter("taskDefId"));
            opData.setStrValue(prePath + "isActive", request.getParameter("isActive"));
            opData.setStrValue(prePath + "isStart", request.getParameter("isStart"));
        }

        opData.setStrValue(prePath + "entityName", entityName);
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
            } else if ("REGEXP".equalsIgnoreCase(conditionArr[i])) {// 问题树正则匹配  正则表达式  '(,100101)|(^100101)'
                //处理多条件
                String[] rLikeFieldValueArr = tempConditionValueArr.split("\\|");
                for (int j = 0; j<rLikeFieldValueArr.length; j++) {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(," + rLikeFieldValueArr[j] + ")|(^" + rLikeFieldValueArr[j] + ")");
                }
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", tempConditionValueArr);
            }
        }

        // 分页信息
        int pageSize = requestUtil.getIntParam("pageSize");
        int currentPage = requestUtil.getIntParam("currentPage");
        opData.setStrValue(prePath + "PageInfo.pageSize", pageSize);
        opData.setStrValue(prePath + "PageInfo.currentPage", currentPage);

        // 排序信息
        int sortCount = 0;
        for (Map.Entry<String, String> temp : sortMap.entrySet()) {
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount}].fieldName", temp.key);
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount++}].sortOrder", temp.value);
        }

        // 权限查询增加参数
        String rhtRegId = requestUtil.getStrParam("rhtRegId");
        if (StringUtil.isNotEmptyOrNull(rhtRegId)) {
            opData.setStrValue(prePath + "ttRegId", requestUtil.getStrParam("rhtRegId"));
            opData.setStrValue(prePath + "regUseType", requestUtil.getStrParam("regUseType"));
            opData.setStrValue(prePath + "rhtType", rhtType);
        }
        // 需要查询的字段
        int tempCount = 0;
        for (String temp : resultNeedFieldArr) {
            opData.setStrValue(prePath + "ResultFields.fieldName[${tempCount++}]", temp);
        }

        // 工作流需要查询的字段
        tempCount = 0;
        for (String temp : taskResultFieldArr) {
            if (StringUtil.isNotEmptyOrNull(temp)) {
                opData.setStrValue(prePath + "TaskFields.fieldName[${tempCount++}]", temp);
            }
        }
        // 增加实体可查询，排序字段的查询
        String cmptName = request.getParameter("cmptName");
        // 跨实体查询
        if (whetherCrossEntity) {
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "PRIVI_FILTER";
            }
            // 权限过滤组件
            if ("PRIVI_FILTER".equals(cmptName) || "TASK_PRIVI_FILTER".equals(cmptName)) {
                //过滤的组织 字段名
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "\$entityName.\$ttOrgId");
                // 把原来写死的权限改成读取配置中的对应的配置信息
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
                }

                opData.setStrValue(prePath + "queryType", "2");   //通用查询，不传此参数==>通用分页查询， 传入此参数==>通用跨实体查询
                opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
            } else if (StringUtil.isEqual(cmptName, "QUERY_ENTITY_PAGE_DATA")) {
                //兼容 前端指定执行通用查询 跨实体组件，无法拼接跨实体参数。
                opData.setStrValue(prePath + "queryType", "2");
            } else if ("true".equals(request.getParameter("addRightFilter"))) {
                opData.setStrValue(prePath + "queryType", "2");
                //过滤的组织 字段名
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "\$entityName.\$ttOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));
                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
                }
            }
        } else {
            //不跨实体
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "FILTER_QUERY_ENTITY_PAGE_DATA";
            }
            // 权限过滤组件
            if ("FILTER_QUERY_ENTITY_PAGE_DATA".equals(cmptName)) {
                opData.setStrValue(prePath + "ttOrgId", request.getParameter("rhtOrgId"));
                opData.setStrValue(prePath + "ttRegId", request.getParameter("rhtRegId"));
                opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
                opData.setStrValue(prePath + "rhtType", rhtType);
            } else if ("true".equals(request.getParameter("addRightFilter"))) {
                opData.setStrValue(prePath + "ttOrgId", request.getParameter("rhtOrgId"));
                opData.setStrValue(prePath + "ttRegId", request.getParameter("rhtRegId"));
                opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
                opData.setStrValue(prePath + "rhtType", rhtType);
            }
        }

        // 调用组件获取服务数据
        svcRequest.addOp(cmptName, opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 查询结果处理
            XmlBean queryResult = svcResponse.getFirstOpRsp(cmptName).getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                //如果returnDataType为jsonData返回json数据
                String returnDataType = request.getParameter("returnDataType");
                if (StringUtil.isEqual("jsonData", returnDataType)) {
                    ResponseUtil.printAjax(response, queryResult.toJson());
                    return;
                }
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<XmlNode> resultList = new ArrayList<XmlNode>();
                for (int i = 0; i < rowCount; i++) {
                    resultList.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                }
                // 返回结果集
                Map<String, Object> pagination = new HashMap<String, Object>();
                modelMap.put("returnList", resultList);
                // 查询条件备份
                Map<String, Object> paramMap = requestUtil.getRequestMap(request);
                paramMap.remove("pageSize");
                paramMap.remove("sortColumn");
                paramMap.remove("sortOrder");
                paramMap.remove("sortColumn");
                paramMap.remove("queryCondition");
                JSONObject json = JSONObject.fromObject(paramMap);
                modelMap.addAttribute("queryCondition", json.toString());
                // 返回分页信息
                pagination.put("currentPage", queryResult.getValue("OpResult.PageInfo.currentPage"));
                pagination.put("totalPage", queryResult.getValue("OpResult.PageInfo.totalPage"));
                pagination.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
                // 分页信息
                modelMap.addAttribute("pagination", pagination);
                // 分页大小
                modelMap.addAttribute("pageSize", requestUtil.getStrParam("pageSize"));
            } else {
                Map<String, Object> pagination = new HashMap<String, Object>();
                // 返回分页信息
                pagination.put("currentPage", "0");
                pagination.put("totalPage", "0");
                pagination.put("totalRecord", "0");
                // 分页信息
                modelMap.addAttribute("pagination", pagination);
            }
            // 排序字段
            modelMap.addAttribute("sortColumn", requestUtil.getStrParam("sortColumn"));
            // o排序p方向
            modelMap.addAttribute("sortOrder", requestUtil.getStrParam("sortOrder"));
            // 每页显示页数
            modelMap.addAttribute("pageSize", requestUtil.getStrParam("pageSize"));
        }
        modelMap.addAttribute("divId", requestUtil.getStrParam("divId"));
        // 调转到配置页面
        String forward = requestUtil.getStrParam("forward");
        return new ModelAndView(forward, modelMap);
    }

    /**
     * 带标签条件的的高级检索
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView dataWithTag(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String entityName = requestUtil.getStrParam("entityName");
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String rhtType = requestUtil.getStrParam("rhtType");
        String resultFields = requestUtil.getStrParam("resultField");
        String forceResultField = requestUtil.getStrParam("forceResultField");
        // 新增条件（自动匹配、人员信息、标签信息）
        String autoConditions = requestUtil.getStrParam("autoConditions");
        String psConditions = requestUtil.getStrParam("psConditions");
        String tagConditions = requestUtil.getStrParam("tagConditions");
        String staffCode = svcRequest.getReqStaffCd();
        /** 条件转数组 */
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        String[] resultFieldArr = resultFields.split(",");

        /** 原有逻辑，判断查询类型和查询结果字段翻译处理 */
        //根据结果字段判断是否是跨实体查询
        boolean whetherCrossEntity = false;
        if (resultFields.indexOf(".") != -1) {
            //是跨实体查询
            whetherCrossEntity = true;
        }
        //将页面设置的必须 查询字段拼进结果字段中，查询具体值
        Set<String> resultNeedFieldSet = new LinkedHashSet<String>();
        if (StringUtil.isNotEmptyOrNull(forceResultField)) {
            String[] forceResultFieldArr = forceResultField.split(",");
            resultNeedFieldSet.addAll(forceResultFieldArr);
        }
        resultNeedFieldSet.addAll(resultFieldArr);
        String[] resultNeedFieldArr = resultNeedFieldSet.toArray();

        //获取工作流结果字段
        String taskResultField = requestUtil.getStrParam("taskResultField");
        String[] taskResultFieldArr = taskResultField.split(",");

        // 排序信息
        String[] sortColumnArr = requestUtil.getStrParam("sortColumn").split(",");
        String[] sortOrderArr = requestUtil.getStrParam("sortOrder").split(",", sortColumnArr.length);
        Map<String, String> sortMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < sortColumnArr.length; i++) {
            if (StringUtil.isNotEmptyOrNull(sortColumnArr[i])) {
                sortMap.put(sortColumnArr[i], sortOrderArr[i]);
            }
        }

        /* 查询配置数据*/
        String prePath = "OpData.";
        // 增加查询
        Map<String, XmlBean> resultTitle = getResultField(request, sortMap);

        // 实际需要查询的字段，包含需要翻译的字段
        Set<String> resultFieldSet = new LinkedHashSet<String>();
        resultFieldSet.addAll(resultFieldArr);

        // 结果标题
        List<XmlNode> resultTitleArr = new ArrayList<XmlNode>();
        for (XmlBean temp : resultTitle.values()) {
            if (temp == null) {
                continue;
            }
            String frontSltRule = temp.getStrValue("Result.frontSltRule");
            String attrNameEn = temp.getStrValue("Result.attrNameEn");
            String entitySaveType = temp.getStrValue("Result.entitySaveType");
            if (frontSltRule.matches(CODE_SLT_PAT) || frontSltRule.matches(TREE_CODE_SLT_PAT)) {
                resultFieldSet.add(attrNameEn + "_Name");
                temp.setStrValue("Result.frontSltRuleView", "CODE");
            } else if (frontSltRule.matches(DATE_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "DATE");
                temp.setStrValue("Result.frontSltRuleViewParam", frontSltRule.substring(6, frontSltRule.length() - 2));
            } else if (frontSltRule.matches(ORG_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "ORG");
            } else if (frontSltRule.matches(MONEY_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "MONEY");
            } else if (frontSltRule.matches(STAFF_SLT_PAT)) {
                temp.setStrValue("Result.frontSltRuleView", "STAFF");
            } else if (StringUtil.isEqual(frontSltRule, SIGN_SLT_PAT)) {
                resultFieldSet.add(attrNameEn + "_Name");
                temp.setStrValue("Result.frontSltRuleView", "SIGN");
            }
            // 设置数字类型, 用于前端汇总
            if (frontSltRule.matches(ORG_SLT_PAT)) {
                temp.setValue("Result.isNumber", false);
            } else if (entitySaveType.matches(NUMBER_TYPE_PAT)) {
                temp.setValue("Result.isNumber", true);
            } else {
                temp.setValue("Result.isNumber", false);
            }
            resultTitleArr.add(temp.getRootNode());
        }
        modelMap.put("resultTitle", resultTitleArr);

        XmlBean opData = new XmlBean();
        /** 推送新增的查询条件 */
        opData.setStrValue(prePath + "OtherCondition.autoConditions", autoConditions);
        opData.setStrValue(prePath + "OtherCondition.psConditions", psConditions);
        opData.setStrValue(prePath + "OtherCondition.tagConditions", tagConditions);
        opData.setStrValue(prePath + "OtherCondition.staffCode", staffCode);
        opData.setStrValue(prePath + "OtherCondition.whetherCrossEntity", whetherCrossEntity);
        /**
         * 查询是否有查询 工作流程任务
         */
        if (StringUtil.isNotEmptyOrNull(request.getParameter("wfTaskFlag"))) {
            opData.setStrValue(prePath + "procDefKey", request.getParameter("procDefKey"));
            opData.setStrValue(prePath + "taskDefId", request.getParameter("taskDefId"));
            opData.setStrValue(prePath + "isActive", request.getParameter("isActive"));
            opData.setStrValue(prePath + "isStart", request.getParameter("isStart"));
        }

        opData.setStrValue(prePath + "entityName", entityName);
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

            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + tempConditionValueArr + "%");
            } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                String usedValue = tempConditionValueArr.replaceAll("\\|", ",");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
            } else if ("REGEXP".equalsIgnoreCase(conditionArr[i])) {// 问题树正则匹配  正则表达式  '(,100101)|(^100101)'
                //处理多条件
                String[] rLikeFieldValueArr = tempConditionValueArr.split("\\|");
                for (int j = 0; j<rLikeFieldValueArr.length; j++) {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(," + rLikeFieldValueArr[j] + ")|(^" + rLikeFieldValueArr[j] + ")");
                }
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", tempConditionValueArr);
            }

        }
        // 分页信息
        int pageSize = requestUtil.getIntParam("pageSize");
        int currentPage = requestUtil.getIntParam("currentPage");
        opData.setStrValue(prePath + "PageInfo.pageSize", pageSize);
        opData.setStrValue(prePath + "PageInfo.currentPage", currentPage);

        // 排序信息
        int sortCount = 0;
        for (Map.Entry<String, String> temp : sortMap.entrySet()) {
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount}].fieldName", temp.key);
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount++}].sortOrder", temp.value);
        }

        // 权限查询增加参数
        String rhtRegId = requestUtil.getStrParam("rhtRegId");
        if (StringUtil.isNotEmptyOrNull(rhtRegId)) {
            opData.setStrValue(prePath + "ttRegId", requestUtil.getStrParam("rhtRegId"));
            opData.setStrValue(prePath + "regUseType", requestUtil.getStrParam("regUseType"));
            opData.setStrValue(prePath + "rhtType", rhtType);
        }
        // 需要查询的字段
        int tempCount = 0;
        for (String temp : resultNeedFieldArr) {
            opData.setStrValue(prePath + "ResultFields.fieldName[${tempCount++}]", temp);
        }

        // 工作流需要查询的字段
        tempCount = 0;
        for (String temp : taskResultFieldArr) {
            if (StringUtil.isNotEmptyOrNull(temp)) {
                opData.setStrValue(prePath + "TaskFields.fieldName[${tempCount++}]", temp);
            }
        }
        // 增加实体可查询，排序字段的查询
        String cmptName = request.getParameter("cmptName");
        // 跨实体查询
        if (whetherCrossEntity) {
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "PRIVI_FILTER";
            }
            // 权限过滤组件
            if ("PRIVI_FILTER".equals(cmptName) || "TASK_PRIVI_FILTER".equals(cmptName)) {
                //过滤的组织 字段名
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "\$entityName.\$ttOrgId");
                // 把原来写死的权限改成读取配置中的对应的配置信息
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
                }

                opData.setStrValue(prePath + "queryType", "2");   //通用查询，不传此参数==>通用分页查询， 传入此参数==>通用跨实体查询
                opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
            } else if (StringUtil.isEqual(cmptName, "QUERY_ENTITY_PAGE_DATA")) {
                //兼容 前端指定执行通用查询 跨实体组件，无法拼接跨实体参数。
                opData.setStrValue(prePath + "queryType", "2");
            } else if ("true".equals(request.getParameter("addRightFilter"))) {
                opData.setStrValue(prePath + "queryType", "2");
                //过滤的组织 字段名
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "\$entityName.\$ttOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));
                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
                }
            }
        } else {
            //不跨实体
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "FILTER_QUERY_ENTITY_PAGE_DATA";
            }
            // 权限过滤组件
            if ("FILTER_QUERY_ENTITY_PAGE_DATA".equals(cmptName)) {
                opData.setStrValue(prePath + "ttOrgId", request.getParameter("rhtOrgId"));
                opData.setStrValue(prePath + "ttRegId", request.getParameter("rhtRegId"));
                opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
                opData.setStrValue(prePath + "rhtType", rhtType);
            } else if ("true".equals(request.getParameter("addRightFilter"))) {
                opData.setStrValue(prePath + "ttOrgId", request.getParameter("rhtOrgId"));
                opData.setStrValue(prePath + "ttRegId", request.getParameter("rhtRegId"));
                opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
                opData.setStrValue(prePath + "rhtType", rhtType);
            }
        }
        // 调用组件获取服务数据
        svcRequest.addOp(cmptName, opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 查询结果处理
            XmlBean queryResult = svcResponse.getFirstOpRsp(cmptName).getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                //如果returnDataType为jsonData返回json数据
                String returnDataType = request.getParameter("returnDataType");
                if (StringUtil.isEqual("jsonData", returnDataType)) {
                    ResponseUtil.printAjax(response, queryResult.toJson());
                    return;
                }
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<XmlNode> resultList = new ArrayList<XmlNode>();
                for (int i = 0; i < rowCount; i++) {
                    resultList.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                }
                // 返回结果集
                Map<String, Object> pagination = new HashMap<String, Object>();
                modelMap.put("returnList", resultList);
                // 查询条件备份
                Map<String, Object> paramMap = requestUtil.getRequestMap(request);
                paramMap.remove("pageSize");
                paramMap.remove("sortColumn");
                paramMap.remove("sortOrder");
                paramMap.remove("sortColumn");
                paramMap.remove("queryCondition");
                JSONObject json = JSONObject.fromObject(paramMap);
                modelMap.addAttribute("queryCondition", json.toString());
                // 返回分页信息
                pagination.put("currentPage", queryResult.getValue("OpResult.PageInfo.currentPage"));
                pagination.put("totalPage", queryResult.getValue("OpResult.PageInfo.totalPage"));
                pagination.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
                // 分页信息
                modelMap.addAttribute("pagination", pagination);
                // 分页大小
                modelMap.addAttribute("pageSize", requestUtil.getStrParam("pageSize"));
            } else {
                Map<String, Object> pagination = new HashMap<String, Object>();
                // 返回分页信息
                pagination.put("currentPage", "0");
                pagination.put("totalPage", "0");
                pagination.put("totalRecord", "0");
                // 分页信息
                modelMap.addAttribute("pagination", pagination);
            }
            // 排序字段
            modelMap.addAttribute("sortColumn", requestUtil.getStrParam("sortColumn"));
            // o排序p方向
            modelMap.addAttribute("sortOrder", requestUtil.getStrParam("sortOrder"));
            // 每页显示页数
            modelMap.addAttribute("pageSize", requestUtil.getStrParam("pageSize"));
        }
        modelMap.addAttribute("divId", requestUtil.getStrParam("divId"));
        // 调转到配置页面
        String forward = requestUtil.getStrParam("forward");
        return new ModelAndView(forward, modelMap);
    }

    /**
     * 导出数据
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void dataExport(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        String entityName = requestUtil.getStrParam("entityName");
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        // 新增条件（自动匹配、人员信息、标签信息）
        String autoConditions = requestUtil.getStrParam("autoConditions");
        String psConditions = requestUtil.getStrParam("psConditions");
        String tagConditions = requestUtil.getStrParam("tagConditions");
        String staffCode = svcRequest.getReqStaffCd();

        String rhtType = requestUtil.getStrParam("rhtType");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

        // 请求界面
        String resultFields = request.getParameter("resultField");
        //判断是否是跨实体查询 标志
        boolean whetherCrossEntity = false;
        if (resultFields.contains(".")) {
            whetherCrossEntity = true;
        }
        // 排序信息
        String[] sortColumnArr = requestUtil.getStrParam("sortColumn").split(",");
        String[] sortOrderArr = requestUtil.getStrParam("sortOrder").split(",", sortColumnArr.length);
        Map<String, String> sortMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < sortColumnArr.length; i++) {
            if (StringUtil.isNotEmptyOrNull(sortColumnArr[i])) {
                sortMap.put(sortColumnArr[i], sortOrderArr[i]);
            }
        }
        // 增加查询
        Map<String, XmlBean> resultTitle = getResultField(request, sortMap);
        // 实际需要查询的字段，包含需要翻译的字段
        Set<String> queryFieldSet = new LinkedHashSet<String>();
        List<String[]> queryFields = new ArrayList<String[]>();
        // 结果标题
        for (XmlBean temp : resultTitle.values()) {
            String frontSltRule = temp.getStrValue("Result.frontSltRule");
            String entitySaveType = temp.getStrValue("Result.entitySaveType");
            String attrNameEn = temp.getStrValue("Result.attrNameEn");
            queryFieldSet.add(attrNameEn);
            String[] fieldDef = new String[3];
            fieldDef[1] = attrNameEn;
            if (frontSltRule.matches(CODE_SLT_PAT) || frontSltRule.matches(TREE_CODE_SLT_PAT)) {
                fieldDef[0] = CODE_SLT_PAT;
            } else if (frontSltRule.matches(DATE_SLT_PAT)) {
                String format = frontSltRule.substring(6, frontSltRule.length() - 2);
                fieldDef[0] = DATE_SLT_PAT;
                fieldDef[2] = format;
            } else if (frontSltRule.matches(ORG_SLT_PAT)) {
                fieldDef[0] = ORG_SLT_PAT;
            } else if (frontSltRule.matches(MONEY_SLT_PAT)) {
                fieldDef[0] = MONEY_SLT_PAT;
            } else if (frontSltRule.matches(STAFF_SLT_PAT)) {
                fieldDef[0] = STAFF_SLT_PAT;
            } else if (entitySaveType.matches(NUMBER_TYPE_PAT)) {
                fieldDef[0] = NUMBER_TYPE_PAT;
            } else if (frontSltRule.matches(SIGN_SLT_PAT)) {
                fieldDef[0] = SIGN_SLT_PAT;
            } else {
                fieldDef[0] = "";
            }
            queryFields.add(fieldDef);
        }

        // 导出数据条件
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
            } else if ("REGEXP".equalsIgnoreCase(conditionArr[i])) {// 问题树正则匹配  正则表达式  '(,100101)|(^100101)'
                //处理多条件
                String[] rLikeFieldValueArr = tempConditionValueArr.split("\\|");
                for (int j = 0; j<rLikeFieldValueArr.length; j++) {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", tempConditionFieldArr);
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(," + rLikeFieldValueArr[j] + ")|(^" + rLikeFieldValueArr[j] + ")");
                }
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", tempConditionValueArr);
            }
        }
        opData.setStrValue(prePath + "entityName", entityName);
        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", 65536);
        opData.setStrValue(prePath + "PageInfo.currentPage", 1);
        // 排序信息
        addCount = 0;
        for (int i = 0; i < sortColumnArr.length; i++) {
            String sortColumn = sortColumnArr[i];
            String sortOrder = sortOrderArr[i];
            if (StringUtil.isNotEmptyOrNull(sortColumn) && StringUtil.isNotEmptyOrNull(sortOrder)) {
                opData.setStrValue(prePath + "SortFields.SortField[${addCount}].fieldName", sortColumn);
                opData.setStrValue(prePath + "SortFields.SortField[${addCount++}].sortOrder", sortOrder);
            }
        }
        // 权限查询增加参数
        String rhtRegId = requestUtil.getStrParam("rhtRegId");
        if (StringUtil.isNotEmptyOrNull(rhtRegId)) {
            opData.setStrValue(prePath + "ttRegId", requestUtil.getStrParam("rhtRegId"));
            opData.setStrValue(prePath + "regUseType", requestUtil.getStrParam("regUseType"));
        }
        // 需要查询的字段
        int fieldCount = 0;
        for (String temp : queryFieldSet) {
            opData.setStrValue(prePath + "ResultFields.fieldName[${fieldCount++}]", temp);
        }
        /** 要导出标签信息，必须查询房屋主键 */
        opData.setStrValue(prePath + "ResultFields.fieldName[${fieldCount++}]", "HouseInfo.hsId");

        // 增加实体可查询，排序字段的查询
        String cmptName = request.getParameter("cmptName");
        //跨实体查询
        if (whetherCrossEntity) {
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "PRIVI_FILTER";
            }
            // 权限过滤组件
            if ("PRIVI_FILTER".equals(cmptName)) {
                //过滤的组织 字段名
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "\$entityName.\$ttOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", rhtType);

                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
                }

                opData.setStrValue(prePath + "queryType", "2");   //通用查询，不传此参数==>通用分页查询， 传入此参数==>通用跨实体查询
                opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
            } else if (StringUtil.isEqual(cmptName, "QUERY_ENTITY_PAGE_DATA")) {
                //兼容 前端指定执行通用查询 跨实体组件，无法拼接跨实体参数。
                opData.setStrValue(prePath + "queryType", "2");
            }
        } else {
            //不跨实体
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "FILTER_QUERY_ENTITY_PAGE_DATA";
            }
            // 权限过滤组件
            if ("FILTER_QUERY_ENTITY_PAGE_DATA".equals(cmptName)) {
                opData.setStrValue(prePath + "ttOrgId", request.getParameter("rhtOrgId"));
                opData.setStrValue(prePath + "ttRegId", request.getParameter("rhtRegId"));
                opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
                opData.setStrValue(prePath + "rhtType", rhtType);
            }
        }
        /** 推送新增的查询条件 */
        opData.setStrValue(prePath + "OtherCondition.autoConditions", autoConditions);
        opData.setStrValue(prePath + "OtherCondition.psConditions", psConditions);
        opData.setStrValue(prePath + "OtherCondition.tagConditions", tagConditions);
        opData.setStrValue(prePath + "OtherCondition.staffCode", staffCode);
        opData.setStrValue(prePath + "OtherCondition.whetherCrossEntity", whetherCrossEntity);

        // 增加查询
        svcRequest.addOp(cmptName, opData);
        SvcResponse svcResponse = query(svcRequest);
        // 添加分页查询数据参数
        String data = "{}";
        if (svcResponse.isSuccess()) {
            XmlBean result = svcResponse.getFirstOpRsp(cmptName);
            int rowCount = result.getListNum("Operation.OpResult.PageData.Row");
            // 需要写出的文件
            ExcelWriter excelWriter;
            String excelName = UUID.randomUUID().toString() + ".xlsx";
            String excelPath = "temp/" + excelName;
            // 获取请求参数模板
            String templatePath = request.getParameter("templatePath");

            if (StringUtil.isNotEmptyOrNull(templatePath)) {
                // 使用模板输出数据
                List<Map<String, String>> resultList = new ArrayList<String, String>();
                String[] nameArr = null;
                if (rowCount > 0) {
                    //判断是否跨实体，拼接不同格式
                    if (whetherCrossEntity) {
                        nameArr = result.getBeanByPath("Operation.OpResult.PageData.Row[0].${entityName}")
                                .getNodeNames(entityName);
                    } else {
                        nameArr = result.getBeanByPath("Operation.OpResult.PageData.Row[0]").getNodeNames("Row");
                    }
                }
                // 循环
                for (int i = 0; i < rowCount; i++) {
                    XmlBean tempBean = result.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                    Map<String, String> temp = new HashMap<String, String>();
                    for (String tempStr : nameArr) {
                        //判断是否跨实体，拼接不同格式
                        if (whetherCrossEntity) {
                            temp.put(entityName + "." + tempStr, tempBean.getStrValue("Row." + entityName + "." + tempStr));
                        } else {
                            temp.put(tempStr, tempBean.getStrValue("Row." + tempStr));
                        }
                    }
                    resultList.add(temp);
                }

                //使用新模版需要传过来路径，路径不存在默认走老版本逻辑
                templatePath = StringUtil.formatFilePath(templatePath);
                File templateFile = new File(templatePath);
                ExcelReader excelReader = new ExcelReader(templateFile);
                excelReader.changeSheet(0);
                //1，获取匹配字段
                int startRow = 0;
                String[] regexsStr = new String[0];
                for (int i = 0; i < excelReader.rowCnt; i++) {
                    regexsStr = excelReader.readData(i);
                    if (isDataRow(regexsStr)) {
                        startRow = i;
                        break;
                    }
                }
                //2，处理获得的字段，交给方法处理返回string[] ,将${ xxxx} 替换为真实值  处理函数（xxxx，xxxx），替换为真实值
                excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), templateFile);
                excelWriter.changeSheet(excelReader.getSheetName(0));
                //3，调用函数，获得写出行数组
                int count = startRow;
                int i = 1;
                for (Map<String, String> rowData : resultList) {
                    if (count > startRow) {
                        excelWriter.insertRow(count + 2);
                        excelWriter.copyRow(count + 1, count + 2);
                    }
                    excelWriter.writeData(count, 0, temFunchion(rowData, regexsStr, i++, request));
                    count++;
                }
            } else {
                // 不适用模板导出数据
                excelWriter = new ExcelWriter(ServerFile.createFile(excelPath));
                excelWriter.createSheet("导出数据", 0);
                excelWriter.changeSheet("导出数据");
                // 写入标题数据
                int tempIdx = 0;
                for (XmlBean temp : resultTitle.values()) {
                    String attrNameCh = temp.getStrValue("Result.attrNameCh");
                    excelWriter.writeData(0, tempIdx++, attrNameCh);
                }
                XmlBean pageData = result.getBeanByPath("Operation.OpResult.PageData");
                // 写入数据
                for (int idx = 0; idx < rowCount; idx++) {
                    XmlBean tempBean = pageData.getBeanByPath("PageData.Row[${idx}]");
                    int rowIdx = idx + 1;
                    int colIdx = 0;
                    for (String[] temp : queryFields) {
                        String attrNameEn = temp[1];
                        Object value = null;
                        if (CODE_SLT_PAT.equals(temp[0]) || SIGN_SLT_PAT.equals(temp[0])) {
                            value = tempBean.getStrValue("Row." + attrNameEn + "_Name");
                            excelWriter.writeData(rowIdx, colIdx++, value);
                        } else if (DATE_SLT_PAT.equals(temp[0])) {
                            value = tempBean.getStrValue("Row." + attrNameEn);
                            if (StringUtil.isNotEmptyOrNull(value)) {
                                // 带时分表格式的特殊处理
                                if ("yyyy-MM-dd HH:mm:ss.SSS".equals(temp[2])) {
                                    value = DateUtil.format(DateUtil.parse(value, "yyyyMMddHHmmssSSS"), temp[2]);
                                } else {
                                    value = DateUtil.format(DateUtil.toDateYmdHms(value), temp[2]);
                                }
                            }
                            excelWriter.writeData(rowIdx, colIdx++, value);
                        } else if (ORG_SLT_PAT.equals(temp[0])) {
                            value = tempBean.getStrValue("Row." + attrNameEn);
                            value = org([prjCd, value], "Org.Node.orgName", request)
                            excelWriter.writeData(rowIdx, colIdx++, value);
                        } else if (MONEY_SLT_PAT.equals(temp[0])) {
                            value = tempBean.getStrValue("Row." + attrNameEn);
                            excelWriter.writeData(rowIdx, colIdx++, value, ExcelType.NUMBER);
                        } else if (NUMBER_TYPE_PAT.equals(temp[0])) {
                            value = tempBean.getStrValue("Row." + attrNameEn);
                            excelWriter.writeData(rowIdx, colIdx++, value, ExcelType.NUMBER);
                        } else if (STAFF_SLT_PAT.equals(temp[0])) {
                            value = tempBean.getStrValue("Row." + attrNameEn);
                            value = staff([value], "", request);
                            if (value == null) {
                                value = "";
                            }
                            excelWriter.writeData(rowIdx, colIdx++, value);
                        } else {
                            value = tempBean.getStrValue("Row." + attrNameEn);
                            excelWriter.writeData(rowIdx, colIdx++, value);
                        }
                    }
                }
            }
            // 关闭文件
            if (excelWriter != null) {
                excelWriter.closeBook();
            }
            data = """ {\"remoteFile\": \"${excelPath}\", "clientFile": "${excelName}\"}  """;
        }

        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

    /**
     *  staff(${admin_staff},Staff.StaffCd)
     *  支持一个参数条件，staffId 或staffcode
     * @param csList 参数条件list；
     * @param ysPath 约束路径；
     * @param request
     * @return
     */
    public String staff(ArrayList csList, String ysPath, HttpServletRequest request) {
        Object staff;
        //定义返回值
        Object retStr;
        //遍历csMap 组织入参
        String staff_id = "";
        String staff_code = "";
        if (csList != null) {
            staff_id = csList.get(0);
        }
        String path = ysPath;
        if (StringUtil.isEmptyOrNull(path)) {
            path = "Staff.StaffName";
        }
        staff = request.getAttribute("N._" + staff_id);
        if (staff == null) {
            staff = request.getAttribute("N._" + staff_code);
        }
        if (staff != null) {
            SvcResponse svcResponse = (SvcResponse) staff;
            retStr = svcResponse.getValue("Response.SvcCont." + path);
        }
        if (retStr == null) {
            if (path.startsWith("Staff")) {
                if (StringUtil.isNotEmptyOrNull(staff_code) || StringUtil.isNotEmptyOrNull(staff_id)) {
                    //调用服务，根据staff_id或staff_code查询staffName
                    SvcRequest svcRequest = new SvcRequest();
                    XmlBean reqData = new XmlBean();
                    reqData.setValue("SvcCont.Staff.staffId", staff_id);
                    reqData.setValue("SvcCont.staffCode", staff_code);
                    // 不同时为空时，调用后台服务
                    svcRequest.setReqData(reqData);
                    SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
                    //依Id形式缓存
                    if (StringUtil.isNotEmptyOrNull(staff_id)) {
                        request.setAttribute("N._" + staff_id, svcResponse);
                    }
                    //依据Id形式缓存失败,依code形式缓存
                    if (request.getAttribute("N._" + staff_id) == null) {
                        request.setAttribute("N._" + staff_code, svcResponse);
                    }
                } else {
                    log.warn("staffId和staffCode不能同时为空：" + path);
                }
            } else {
                log.warn("property格式不正确" + path);
            }
            // 从缓存报文中取需要的值
            staff = request.getAttribute("N._" + staff_id);
            if (staff == null) {
                staff = request.getAttribute("N._" + staff_code);
            }
            SvcResponse svcResponse = (SvcResponse) staff;
            if (svcResponse != null) {
                retStr = svcResponse.getValue("Response.SvcCont." + path);
            }
        }
        //日期格式转换
        if (path.endsWith("Date") && retStr != null) {
            String el = "^(\\d{4})-([0-1]\\d)-([0-3]\\d)\\s([0-5]\\d):([0-5]\\d):([0-5]\\d)\$";
            boolean dateFlag = retStr.toString().matches(el);
            if (!dateFlag) {
                //时间格式不正确，转换成正确格式输出
                retStr = DateUtil.format(DateUtil.toDateYmdHms(retStr.toString()), "yyyy-MM-dd HH:mm:ss");
            }
        }
        //
        if (StringUtil.isEmptyOrNull(retStr.toString())) {
            log.debug("没有查询到对应结果。 参数条件list：" + csList + " 约束路径ysPath：" + ysPath);
        }
        return retStr;
    }

    /**
     *  org(${prjCd}&${prj_org_id},Org.Node..prjOrgName)
     *  支持传递 prj_cd ，org_id 两个参数条件：顺序为prj_cd org_cd。
     * @param csList 参数数组
     * @param ysPath 约束路径
     * @param request
     * @return
     */
    public String org(ArrayList csList, String ysPath, HttpServletRequest request) {
        //存储报文
        Object returnOrg;
        Object writeOrg;
        String path = ysPath;
        if (StringUtil.isEmptyOrNull(path)) {
            //默认，查找显示orgName
            path = "Org.Node.orgName";
        }
        String prj_cd = "";
        String org_id = "";
        if (csList != null && csList.size() > 1) {
            prj_cd = csList.get(0);
            org_id = csList.get(1);
        } else {
            log.warn("参数不足，请检查配置参数。对应的约束路径为：" + ysPath);
            return "";
        }
        returnOrg = request.getAttribute("O._" + prj_cd + org_id);
        if (returnOrg != null) {
            SvcResponse svcResponse = (SvcResponse) returnOrg;
            writeOrg = svcResponse.getValue("Response.SvcCont." + path);
        }
        //如果不存在
        if (writeOrg == null) {
            if (StringUtil.isNotEmptyOrNull(org_id) && StringUtil.isNotEmptyOrNull(prj_cd)) {
                if (path.startsWith("Org.Node")) {
                    SvcRequest svcRequest = new SvcRequest();
                    XmlBean reqData = new XmlBean();
                    //
                    reqData.setValue("SvcCont.Org.Node.prjCd", prj_cd);
                    //组织Id
                    reqData.setValue("SvcCont.Org.Node.orgId", org_id);
                    svcRequest.setReqData(reqData);
                    SvcResponse svcResponse = SvcUtil.callSvc("orgService", "queryNodeInfo", svcRequest);
                    //组织报文存在时，进行缓存
                    if (StringUtil.isNotEmptyOrNull(svcResponse.toString())) {
                        request.setAttribute("O._" + prj_cd + org_id, svcResponse);
                    }
                    //Org.Node.orgName
                    writeOrg = svcResponse.getValue("Response.SvcCont." + path);
                } else {
                    log.warn("oframe:org  property的格式不正确！" + path);
                }
            } else {
                log.warn("orgId、prjCd不允许为空" + org_id + prj_cd);
            }
        }
        //日期类型的处理
        if (path.endsWith("Date") && writeOrg != null) {
            String el = "^(\\d{4})-([0-1]\\d)-([0-3]\\d)\\s([0-5]\\d):([0-5]\\d):([0-5]\\d)\$";
            boolean dateFlag = writeOrg.toString().matches(el);
            if (!dateFlag) {
                //时间格式不正确，转换成正确格式输出
                writeOrg = DateUtil.format(DateUtil.toDateYmdHms(writeOrg.toString()), "yyyy-MM-dd HH:mm:ss");
            }
        }
        if (StringUtil.isEmptyOrNull(writeOrg)) {
            log.debug("没有查询到对应结果。 参数条件list：" + csList + " 约束路径ysPath：" + ysPath);
        }
        return writeOrg;
    }

    /**
     *  status(${live_status}，STATUS_CD)
     *  获取状态信息
     * @param csList
     * @param ysPath
     * @param request
     * @return
     */
    public String status(ArrayList csList, String ysPath, HttpServletRequest request) {
        String key = "";
        String retStr = "";
        if (csList != null) {
            key = csList.get(0).toString();
        }
        Map<String, String> collection = this.getCfgCollection(request, ysPath, true);
        if (collection != null) {
            retStr = collection.get(key)
        }
        //返回值
        return retStr;
    }

    /**
     * 收藏查询条件
     * @param request 请求参数，含entityName参数
     * @param response 响应页面
     * @return
     */
    public ModelAndView fav(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("entityName", request.getParameter("entityName"));
        modelMap.put("favName", DateUtil.toStringYmd(DateUtil.getSysDate()));
        return new ModelAndView("/oframe/common/page/page_fav", modelMap);
    }

    /**
     * 保存收藏
     * @param request 请求参数，含entityName参数
     * @param response 响应页面
     * @return
     */
    public void saveFav(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        ModelMap modelMap = new ModelMap();
        XmlBean opData = new XmlBean();
        String favName = requestUtil.getStrParam("favName");
        String entityName = requestUtil.getStrParam("entityName");
        String favContent = requestUtil.getStrParam("favContent");
        String favFlag = requestUtil.getStrParam("favFlag");
        String prjCd = requestUtil.getStrParam("prjCd");
        String favStaff = svcRequest.getReqStaff();

        if (StringUtil.isEqual("0", favFlag)) {
            opData.setStrValue("OpData.StaffFavoriteCondition.favStaff", favStaff);
        }
        opData.setStrValue("OpData.entityName", "StaffFavoriteCondition");
        opData.setStrValue("OpData.StaffFavoriteCondition.entityName", entityName);
        opData.setStrValue("OpData.StaffFavoriteCondition.prjCd", prjCd);
        opData.setStrValue("OpData.StaffFavoriteCondition.favName", favName);
        opData.setStrValue("OpData.StaffFavoriteCondition.favDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        opData.setStrValue("OpData.StaffFavoriteCondition.favContent", favContent);
        // 收藏对应的功能编码
        opData.setStrValue("OpData.StaffFavoriteCondition.opCode", requestUtil.getStrParam("tabOpCode"));


        svcRequest.addOp("SAVE_FAVORITE_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String favId = "";
        if (svcResponse.isSuccess()) {
            favId = svcResponse.getFirstOpRsp("SAVE_FAVORITE_CMPT").getStrValue("Operation.OpResult.StaffFavoriteCondition.favId");
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "favId: ${favId}");
    }

    /**
     * 删除收藏
     * @param request 请求参数，含entityName参数
     * @param response 响应页面
     * @return
     */
    public void deleteFav(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        ModelMap modelMap = new ModelMap();
        String favId = requestUtil.getStrParam("favId");
        String entityName = requestUtil.getStrParam("entityName");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", entityName);
        opData.setStrValue("OpData.entityKey", favId);

        svcRequest.addOp("DELETE_ENTITY_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
    }

    /**
     * 点击收藏
     * @param request 请求参数，含entityName参数
     * @param response 响应页面
     * @return
     */
    public void queryFav(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        ModelMap modelMap = new ModelMap();
        String favId = requestUtil.getStrParam("favId");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "StaffFavoriteCondition");
        opData.setStrValue("OpData.entityKey", favId);

        svcRequest.addOp("QUERY_ALL_ENTITY", opData);
        SvcResponse svcResponse = query(svcRequest);
        String infofav = new String();
        if (svcResponse.isSuccess()) {
            XmlBean newHsInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                    .getBeanByPath("Operation.OpResult.StaffFavoriteCondition");
            infofav = newHsInfo.getStrValue("StaffFavoriteCondition.favContent");
        }
        String jsonStr = """{"success":"${svcResponse.isSuccess()}","errMsg":"${
            svcResponse.getErrMsg()
        }","infofav":${
            infofav
        }}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    private String[] convertToString(Map<String, Object> objectMap, String[] valueKeys) {
        int size = 0;
        if (valueKeys == null) {
            size = objectMap.size();
        } else {
            size = valueKeys.size();
        }
        String[] tempStr = new String[size];
        if (valueKeys == null) {
            int i = 0;
            for (Map.Entry<String, Object> tempItem : objectMap.entrySet()) {
                tempStr[i] = StringUtil.obj2Str(tempItem.value);
                i++;
            }
        } else {
            for (int i = 0; i < size; i++) {
                tempStr[i] = StringUtil.obj2Str(objectMap.get(valueKeys[i]));
            }
        }
        return tempStr;
    }

    private boolean isDataRow(String[] str) {
        if (str == null) {
            return false;
        }
        for (String temp : str) {
            if (temp != null && temp.matches("^\\\$\\{.*}\$")) {
                return true;
            }
        }
        return false

    }

    /**
     * 根据请求信息，获取需要检索的字段列表
     * @param request 请求信息
     * @return 以fieldName为key的查询结果
     */
    private Map<String, XmlBean> getResultField(HttpServletRequest request, Map<String, String> sortMap) {
        // 请求界面
        String resultFields = request.getParameter("resultField");
        String entityName = request.getParameter("entityName");
        String prjCd = request.getParameter("prjCd");
        // 按照查询的字段获取表头数据
        Map<String, XmlBean> resultTitle = new LinkedHashMap<String, XmlBean>();
        String[] resultFieldArr = resultFields.split(",");
        for (String temp : resultFieldArr) {
            resultTitle.put(temp, new XmlBean("<Result><attrNameEn>${temp}</attrNameEn></Result>"));
        }
        /* 查询配置数据*/
        String prePath = "OpData.";
        // 增加查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", entityName);

        //根据结果字段判断是否是跨实体查询    by  结果字段含有点即为 跨实体查询
        String cmptName = "QUERY_ENTITY_CONFIG_CMPT";
        if (resultFieldArr[0].indexOf(".") != -1) {
            //是跨实体查询
            cmptName = "QUERY_ENTITY_ALL_CFG_INFO";
        }
        if (StringUtil.isEqual(cmptName, "QUERY_ENTITY_CONFIG_CMPT")) {
            opData.setStrValue("OpData.prjCd", prjCd);
        }
        svcRequest.addOp(cmptName, opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取实体属性
            XmlBean entity = svcResponse.getFirstOpRsp(cmptName)
                    .getBeanByPath("Operation.OpResult.Entity");
            // 处理所有分组
            int groupCount = entity.getListNum("Entity.Groups.Group");
            for (int i = 0; i < groupCount; i++) {
                XmlBean temp = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                // 获取属性
                int attrCount = temp.getListNum("Group.Attrs.Attr");
                for (int k = 0; k < attrCount; k++) {
                    XmlBean attrBean = temp.getBeanByPath("Group.Attrs.Attr[${k}]");
                    String entityAttrNameEn = attrBean.getStrValue("Attr.entityAttrNameEn");
                    if (resultTitle.containsKey(entityAttrNameEn)) {
                        XmlBean fieldBean = new XmlBean();
                        fieldBean.setStrValue("Result.attrNameEn", attrBean.getStrValue("Attr.entityAttrNameEn"));
                        fieldBean.setStrValue("Result.attrNameCh", attrBean.getStrValue("Attr.entityAttrNameCh"));
                        fieldBean.setStrValue("Result.attrNameDesc", attrBean.getStrValue("Attr.entityAttrDesc"));
                        fieldBean.setStrValue("Result.css", attrBean.getStrValue("Attr.attrRuleCss"));
                        fieldBean.setStrValue("Result.entitySaveType", attrBean.getStrValue("Attr.entitySaveType"));
                        fieldBean.setStrValue("Result.frontSltRule", attrBean.getStrValue("Attr.attrFrontSltRule"));
                        String sortOrder = sortMap.get(entityAttrNameEn);
                        if ("1".equals(attrBean.getStrValue("Attr.canOrderFlag"))) {
                            if (sortOrder == null) {
                                fieldBean.setStrValue("Result.sortable", "sortable");
                            } else {
                                fieldBean.setStrValue("Result.sortable", "sortable " + sortOrder);
                            }
                        }
                        resultTitle.put(entityAttrNameEn, fieldBean);
                    }
                }
            }
            // 处理自定义作业字表，不允许作为检索条件，不允许排序
            int jobDefCount = entity.getListNum("Entity.JobDefs.JobDef");
            for (int i = 0; i < jobDefCount; i++) {
                XmlBean attrBean = entity.getBeanByPath("Entity.JobDefs.JobDef[${i}]");
                String jobName = attrBean.getStrValue("JobDef.jobName");
                if (StringUtil.isEmptyOrNull(jobName)) {
                    jobName = "统计指标";
                }
                String entityAttrNameEn = attrBean.getStrValue("JobDef.jobCd");
                // 增加本指标
                if (resultTitle.containsKey(entityAttrNameEn)) {
                    XmlBean fieldBean = new XmlBean();
                    fieldBean.setStrValue("Result.attrNameEn", attrBean.getStrValue("JobDef.jobCd"));
                    fieldBean.setStrValue("Result.attrNameCh", jobName);
                    resultTitle.put(entityAttrNameEn, fieldBean);
                }
            }
        }
        return resultTitle;
    }

    /**
     * 根据请求信息，获取需要检索的字段列表
     * @param request 请求信息
     * @return 以fieldName为key的查询结果
     */
    private Map<String, XmlBean> getConditionField(HttpServletRequest request) {
        // 返回结果
        Map<String, XmlBean> resultMap = new HashMap<String, XmlBean>();
        // 增加查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        /* 查询配置数据*/
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", request.getParameter("entityName"));
        svcRequest.addOp("QUERY_ENTITY_SLT_CFG_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 表头处理
            XmlBean entityCfgBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_SLT_CFG_CMPT")
                    .getBeanByPath("Operation.OpResult");
            /// 获取需要翻译的字段
            int conditionCount = entityCfgBean.getListNum("OpResult.Condition");
            for (int idx = 0; idx < conditionCount; idx++) {
                XmlBean temp = entityCfgBean.getBeanByPath("OpResult.Condition[$idx]");
                String attrNameEn = temp.getStrValue("Condition.attrNameEn");
                resultMap.put(attrNameEn, temp);
            }
        }
        return resultMap;
    }

    /**
     * 获取输出行的数组
     * @param objectMap 从服务中获取的值
     * @param valueKeys 模板中的参数数组
     * @param request
     * @return 输出行的数组
     */
    private String[] temFunchion(Map<String, Object> objectMap,
                                 String[] valueKeys, int index, HttpServletRequest request) {
        int size;
        String[] returnStr;
        if (valueKeys != null) {
            size = valueKeys.length;
            returnStr = new String[size];
            for (int i = 0; i < size; i++) {
                String str = valueKeys[i].trim();
                if (str.matches("^\".*\"\$")) {
                    //常量获取"xxxx"，
                    str = str.replaceAll("\"", "").trim();
                    returnStr[i] = str;
                } else if (str.equals("\${index}")) {
                    returnStr[i] = index;
                } else if (str.matches("^\\\$\\{.*}\$")) {
                    //变量获取${xxxx}，
                    str = str.replaceAll("\\\$|\\{|}", "").trim();
                    Object objBl = objectMap.get(str);
                    if (objBl != null) {
                        returnStr[i] = StringUtil.obj2Str(objBl)
                    } else if (StringUtil.isNotEmptyOrNull(str)) {
                        returnStr[i] = request.getParameter(str);
                    }
                } else if (str.matches("^\\w{1,}\\(.*")) {
                    //取函数，调用服务，获得值
                    //O(${org_id}&${prjCd},Node.orgName)
                    String hsStr = str;
                    String strName = hsStr.substring(0, hsStr.lastIndexOf("("));//函数名
                    //条件参数，可能存在多个
                    ArrayList csList = new ArrayList<String>();
                    String str1 = hsStr.substring(hsStr.indexOf("(") + 1, hsStr.lastIndexOf(","));
                    String[] csStrs = str1.split("&");
                    for (int l = 0; l < csStrs.length; l++) {
                        String s = csStrs[l];
                        String csValue = "";
                        if (s.matches("^\\\$\\{.*}\$")) {
                            //变量获取${xxxx}，
                            s = s.replaceAll("\\\$|\\{|}", "").trim();
                            Object objBl = objectMap.get(s);
                            if (objBl != null) {
                                csValue = StringUtil.obj2Str(objBl)
                            } else if (StringUtil.isNotEmptyOrNull(s)) {
                                csValue = request.getParameter(s);
                            }
                            //将对应的参数与值存到参数Map中
                            csList.add(l, csValue);
                        } else {
                            //前端获取 不是变量 是具体值 直接存入 参数list
                            csList.add(l, s);
                        }
                    }
                    //输出结果约束
                    String str2 = hsStr.substring(hsStr.indexOf(",") + 1, hsStr.lastIndexOf(")"));
                    try {
                        Class[] aClass = new Class[3];
                        aClass[0] = csList.getClass();
                        aClass[1] = str2.getClass();
                        aClass[2] = HttpServletRequest.class;
                        Method method = this.getClass().getDeclaredMethod(strName, aClass);
                        //执行对应的方法
                        String returnOfHs = (String) method.invoke(this, csList, str2, request);
                        returnStr[i] = returnOfHs;
                        log.debug("函数：" + str + "  返回值：" + returnOfHs);
                    } catch (Exception e) {
                        log.error("函数方法调用错误或函数方法不存在", e);
                    }
                    //将函数替换为服务所取的值
                } else {
                    returnStr[i] = "0";
                    log.warn("模板第二行，格式不正确，错误记录：【" + str + "】")
                }
            }
        } else {
            log.warn("使用自定义导出模板，请在第二行写应用常量，变量或函数。");
        }
        return returnStr;
    }

    protected Map<String, String> getCfgCollectionWithNote(HttpServletRequest httpServletRequest, String itemCd,
                                                           boolean isCached, int prjCd) {
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

    /**
     * 按照属性名称整理查询字段的所有值，页面获取的条件
     * @param conditionFieldArr
     * @param conditionArr
     * @param conditionValueArr
     * @return
     */
    private Map<String, XmlBean> formatCondition(String[] conditionFieldArr, String[] conditionArr, String[] conditionValueArr) {
        Map<String, XmlBean> conditionMap = new LinkedHashMap<String, XmlBean>();
        for (int i = 0; i < conditionFieldArr.length; i++) {
            String conditionField = conditionFieldArr[i];
            if (StringUtil.isEmptyOrNull(conditionField)) {
                continue;
            }
            XmlBean xmlBean = conditionMap.get(conditionField);
            if (xmlBean == null) {
                xmlBean = new XmlBean();
                conditionMap.put(conditionField, xmlBean);
            }
            // 追加查询条件
            XmlBean temp = new XmlBean();
            temp.setStrValue("Cond.condition", conditionArr[i]);
            temp.setStrValue("Cond.conditionValue", conditionValueArr[i]);
            xmlBean.setBeanByPath("CondField", temp);
        }
        return conditionMap;
    }

    /**
     * 实体分组及属性格式化，转为 Map<String, List<XmlNode>>
     * @param entity
     * @param conditionMap
     * @param sortMap
     * @param forceResultField
     * @param resultFieldArr
     * @param groups
     * @param resultFieldMap
     */
    private void getFormatGroups(XmlBean entity, Map<String, XmlBean> conditionMap, Map<String, String> sortMap, String[] forceResultField, String[] resultFieldArr, Map<String, List<XmlNode>> groups, Map<String, XmlNode> resultFieldMap) {
        /** 把标签标题提前 */
        Map<String, List<XmlNode>> tempHsGroups = new LinkedHashMap<String, List<XmlNode>>();
        // 处理所有分组
        int groupCount = entity.getListNum("Entity.Groups.Group");
        for (int i = 0; i < groupCount; i++) {
            XmlBean temp = entity.getBeanByPath("Entity.Groups.Group[${i}]");
            String groupName = temp.getStrValue("Group.GroupInfo.entityGroupNameCh");
            List<XmlNode> groupAttrList = new ArrayList<XmlNode>();
            if (groupName.contains("标签")) {
                groups.put(groupName, groupAttrList);
            } else {
                tempHsGroups.put(groupName, groupAttrList);
            }
            // 获取属性
            int attrCount = temp.getListNum("Group.Attrs.Attr");
            for (int k = 0; k < attrCount; k++) {
                XmlBean attrBean = temp.getBeanByPath("Group.Attrs.Attr[${k}]");
                String entityAttrNameEn = attrBean.getStrValue("Attr.entityAttrNameEn");
                // 检索条件字段
                XmlBean condBean = conditionMap.get(entityAttrNameEn);
                if (condBean != null) {
                    condBean.setStrValue("CondField.entityAttrNameEn", entityAttrNameEn);
                    condBean.setStrValue("CondField.entityAttrNameCh", attrBean.getStrValue("Attr.entityAttrNameCh"));
                    condBean.setStrValue("CondField.entitySaveType", attrBean.getStrValue("Attr.entitySaveType"));
                    condBean.setStrValue("CondField.attrFrontSltRule", attrBean.getStrValue("Attr.attrFrontSltRule"));
                }
                // 判断排序处理
                if (sortMap.containsKey(entityAttrNameEn)) {
                    attrBean.setStrValue("Attr.sortClass", "sortable " + sortMap.get(entityAttrNameEn));
                } else if ("1".equals(attrBean.getStrValue("Attr.canOrderFlag"))) {
                    attrBean.setStrValue("Attr.sortClass", "sortable");
                }
                // 判断必须输出的字段
                if (forceResultField.contains(entityAttrNameEn)) {
                    attrBean.setStrValue("Attr.required", "true");
                }
                // 是否已选择输出字段
                if (resultFieldArr.contains(entityAttrNameEn)) {
                    attrBean.setStrValue("Attr.addClass", "selected");
                    resultFieldMap.put(entityAttrNameEn, attrBean.getRootNode());
                }
                // 无法作为检索条件、排序字段、结果字段则不展示
                if ("1".equals(attrBean.getStrValue("Attr.canConditionFlag")) ||
                        "1".equals(attrBean.getStrValue("Attr.canOrderFlag")) ||
                        "1".equals(attrBean.getStrValue("Attr.canResultFlag"))) {
                    groupAttrList.add(attrBean.getRootNode());
                }
            }
        }
        groups.putAll(tempHsGroups);
    }

    /**
     * 标签转为类似实体分组和属性格式
     */
    private XmlBean getTagFormatGroups(XmlBean tagEntity) {
        XmlBean tempBean = new XmlBean();
        tempBean.setStrValue("Entity.EntityInfo.entityName", "SignInfo");
        tempBean.setStrValue("Entity.EntityInfo.entityDesc", "标签信息");
        tempBean.setStrValue("Entity.Groups.Group[0].GroupInfo.entityGroupNameCh", "系统标签");
        tempBean.setStrValue("Entity.Groups.Group[1].GroupInfo.entityGroupNameCh", "个人标签");
        int tagCount = tagEntity.getListNum("OpResult.Tag");
        int sysIndex = 0;
        int psIndex = 0;
        for (int i = 0; i < tagCount; i++) {
            XmlBean everyTagBean = tagEntity.getBeanByPath("OpResult.Tag[${i}]");
            String tagId = everyTagBean.getStrValue("Tag.tagId");
            String tagName = everyTagBean.getStrValue("Tag.tagName");
            String tagType = everyTagBean.getStrValue("Tag.tagType");
            if (StringUtil.isEqual(tagType, "0")) {// 系统标签
                tempBean.setStrValue("Entity.Groups.Group[0].Attrs.Attr[${sysIndex}].entityAttrNameCh", "(系统)" + tagName);
                tempBean.setStrValue("Entity.Groups.Group[0].Attrs.Attr[${sysIndex}].canConditionFlag", "1");
                tempBean.setStrValue("Entity.Groups.Group[0].Attrs.Attr[${sysIndex}].canorderflag", "1");
                tempBean.setStrValue("Entity.Groups.Group[0].Attrs.Attr[${sysIndex++}].entityAttrNameEn", tagId);
            } else {// 个人标签
                tempBean.setStrValue("Entity.Groups.Group[1].Attrs.Attr[${psIndex}].entityAttrNameCh", "(个人)" + tagName);
                tempBean.setStrValue("Entity.Groups.Group[1].Attrs.Attr[${psIndex}].canConditionFlag", "1");
                tempBean.setStrValue("Entity.Groups.Group[1].Attrs.Attr[${psIndex}].canorderflag", "1");
                tempBean.setStrValue("Entity.Groups.Group[1].Attrs.Attr[${psIndex++}].entityAttrNameEn", tagId);
            }
        }
        return tempBean;
    }

    /**
     * 格式化查询条件及其字段翻译，推送到页面
     * @param request
     * @param conditionMap
     * @return
     */
    private List<XmlNode> formatQueryCondition(HttpServletRequest request, String prjCd, Map<String, XmlBean> conditionMap) {
        List<XmlNode> queryCondList = new ArrayList<XmlNode>(conditionMap.size());
        for (Map.Entry<String, XmlBean> temp : conditionMap) {
            XmlBean queryCond = temp.getValue();
            String entityAttrNameEn = queryCond.getStrValue("CondField.entityAttrNameEn");
            // 获取该字段的所有查询条件
            int condCount = queryCond.getListNum("CondField.Cond");
            String[] fieldConditionNames = new String[condCount];
            String[] fieldConditions = new String[condCount];
            String[] fieldConditionValues = new String[condCount];
            // 拼接查询条件
            for (int i = 0; i < condCount; i++) {
                XmlBean tempCon = queryCond.getBeanByPath("CondField.Cond[${i}]");
                fieldConditionNames[i] = entityAttrNameEn;
                fieldConditions[i] = tempCon.getStrValue("Cond.condition");
                fieldConditionValues[i] = tempCon.getStrValue("Cond.conditionValue");
            }
            queryCond.setStrValue("CondField.conditionName", fieldConditionNames.join(","));
            queryCond.setStrValue("CondField.condition", fieldConditions.join(","));
            queryCond.setStrValue("CondField.conditionValue", fieldConditionValues.join(","));

            // 获取查询字面值
            String conditionText;
            String entitySaveType = queryCond.getStrValue("CondField.entitySaveType");
            String attrFrontSltRule = queryCond.getStrValue("CondField.attrFrontSltRule");
            Map<String, String> condition = getCfgCollection(request, "QUERY_CONDITION", true);

            if (attrFrontSltRule.matches(CODE_SLT_PAT)) {
                // 选择码表数据
                String codeValue = attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2);
                Map<String, String> codeMap = getCfgCollection(request, codeValue, true, NumberUtil.getIntFromObj(prjCd));
                List<String> disArr = new ArrayList<String>(fieldConditionValues.length);
                for (String tempStr : fieldConditionValues) {
                    for (String realT : tempStr.split("\\|")) {
                        if (StringUtil.isNotEmptyOrNull(realT)) {
                            disArr.add(codeMap.get(realT));
                        }
                    }
                }
                conditionText = disArr.join("、");
            } else if (attrFrontSltRule.matches(STAFF_SLT_PAT)) {
                List<String> disArr = new ArrayList<String>();
                // 选择码表数据
                for (String tempStr : fieldConditionValues) {
                    if (StringUtil.isNotEmptyOrNull(tempStr)) {
                        disArr.add(staff([tempStr], "", request));
                    }
                }
                conditionText = disArr.join("、");
            } else if (attrFrontSltRule.matches(ORG_SLT_PAT)) {
                List<String> disArr = new ArrayList<String>();
                // 选择码表数据
                for (String tempStr : fieldConditionValues) {
                    if (StringUtil.isNotEmptyOrNull(tempStr)) {
                        disArr.add(org([prjCd, tempStr], "Org.Node.orgName", request));
                    }
                }
                conditionText = disArr.join("、");
            } else if (entitySaveType.startsWith("INT") || entitySaveType.startsWith("NUMBER")
                    || entitySaveType.startsWith("DOUBLE")
                    || entitySaveType.startsWith("DATE") || entitySaveType.startsWith("DATETIME")) {
                List<String> disArr = new ArrayList<String>();
                boolean isDate = attrFrontSltRule.matches(DATE_SLT_PAT);
                String dateFormat = "";
                if (isDate) {
                    dateFormat = attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2);
                }
                for (int i = 0; i < fieldConditionValues.length; i++) {
                    String cTmpStr = fieldConditionValues[i];
                    if (isDate) {
                        cTmpStr = DateUtil.format(DateUtil.toDateYmdHms(cTmpStr), dateFormat)
                    }
                    String cNTmpStr = fieldConditions[i];
                    if (StringUtil.isNotEmptyOrNull(cTmpStr)) {
                        if (">=".equals(cNTmpStr)) {
                            disArr.add(cTmpStr);
                            disArr.add("-");
                        } else if ("<=".equals(cNTmpStr)) {
                            if (disArr.size() > 0 && "-".equals(disArr.last())) {
                                disArr.add(cTmpStr);
                            } else {
                                disArr.add("-");
                                disArr.add(cTmpStr);
                            }
                        } else {
                            disArr.add(condition.get(cNTmpStr) + """ "${cTmpStr}" """);
                        }
                    }
                }
                conditionText = disArr.join("");
            } else {
                List<String> disArr = new ArrayList<String>();
                for (int i = 0; i < fieldConditionValues.length; i++) {
                    String cTmpStr = fieldConditionValues[i];
                    String cNTmpStr = fieldConditions[i];
                    disArr.add(condition.get(cNTmpStr) + """ "${cTmpStr}" """);
                }
                conditionText = disArr.join("、");
            }
            queryCond.setStrValue("CondField.conditionText", conditionText);
            // 对二维码进行修正
            if (temp.key.startsWith("UID.")) {
                // 增到返回结果中
                queryCond.setStrValue("CondField.entityAttrNameCh", "二维码");
                queryCond.setStrValue("CondField.entityAttrNameEn", temp.key);
            }
            // 增到返回结果中
            queryCondList.add(queryCond.getRootNode());
        }
        return queryCondList;
    }

    /**
     * 标签条件
     * 格式化查询条件及其字段翻译，推送到页面
     * @param request
     * @param conditionMap
     * @return
     */
    private List<XmlNode> formatTagQueryCondition(HttpServletRequest request, Map<String, XmlBean> conditionMap) {
        List<XmlNode> queryCondList = new ArrayList<XmlNode>(conditionMap.size());
        for (Map.Entry<String, XmlBean> temp : conditionMap) {
            XmlBean queryCond = temp.getValue();
            String parentTagId = queryCond.getStrValue("CondField.entityAttrNameEn");
            // 获取该字段的所有查询条件
            int condCount = queryCond.getListNum("CondField.Cond");
//            String[] fieldConditionNames = new String[condCount];
//            String[] fieldConditions = new String[condCount];
//            String[] fieldConditionValues = new String[condCount];
            // 拼接查询条件
            for (int i = 0; i < condCount; i++) {
                XmlBean tempCon = queryCond.getBeanByPath("CondField.Cond[${i}]");
                String fieldConditionNames = parentTagId;
                String fieldConditions = tempCon.getStrValue("Cond.condition");
                String fieldConditionValues = tempCon.getStrValue("Cond.conditionValue");
                // 相同标签下不同条件，要拆成两个，因此需要一个过度用的XmlBean
                XmlBean tempQueryCond = new XmlBean();
                tempQueryCond.setStrValue("CondField.entityAttrNameEn", queryCond.getStrValue("CondField.entityAttrNameEn"));
                tempQueryCond.setStrValue("CondField.entityAttrNameCh", queryCond.getStrValue("CondField.entityAttrNameCh"));
                tempQueryCond.setStrValue("CondField.conditionName", fieldConditionNames);
                tempQueryCond.setStrValue("CondField.condition", fieldConditions);
                tempQueryCond.setStrValue("CondField.conditionValue", fieldConditionValues);
                String con_name;
                if ("in".equalsIgnoreCase(fieldConditions)) {
                    con_name = "包含";
                } else if ("not in".equalsIgnoreCase(fieldConditions)) {
                    con_name = "不包含";
                }
                tempQueryCond.setStrValue("CondField.condition_Name", con_name);
                /** **********************/
                XmlBean opData = new XmlBean();
                opData.setStrValue("OpData.tagId", parentTagId);
                SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("QUERY_SIGN_CMPT", opData);
                // 调用服务
                SvcResponse svcResponse = query(svcRequest);
                // 处理返回结果
                XmlBean attrBean = null;
                if (svcResponse.isSuccess()) {
                    // 获取实体属性
                    XmlBean tagBean = svcResponse.getFirstOpRsp("QUERY_SIGN_CMPT").getBeanByPath("Operation.OpResult");
                    // 处理所有分组
                    int tagCount = tagBean.getListNum("OpResult.Tag");
                    if (tagCount == 1) {
                        // 是否可以多选
                        String isCheckbox = tagBean.getStrValue("OpResult.Tag.isCheckbox");
                        Map<String, String> codeMap = new LinkedHashMap<>();
                        int childTagCount = tagBean.getListNum("OpResult.Tag.SubTags.Tag");
                        for (int j = 0; j < childTagCount; j++) {
                            XmlBean tempBean = tagBean.getBeanByPath("OpResult.Tag.SubTags.Tag[${j}]");
                            String tagId = tempBean.getStrValue("Tag.tagId");
                            String tagName = tempBean.getStrValue("Tag.tagName");
                            codeMap.put(tagId, tagName);
                        }
                        List<String> disArr = new ArrayList<String>();
                        for (String realT : fieldConditionValues.split("\\|")) {
                            if (StringUtil.isNotEmptyOrNull(realT)) {
                                disArr.add(codeMap.get(realT));
                            }
                        }
                        tempQueryCond.setStrValue("CondField.conditionText", disArr.join("、"));
                        // 增到返回结果中
                        queryCondList.add(tempQueryCond.getRootNode());
                    }
                }
            }
        }
        return queryCondList;
    }
}
