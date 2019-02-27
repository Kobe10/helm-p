import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.util.HtmlUtils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys017 extends GroovyController {

    public static final String RULE_TYPE_ENTITY_NAME = "RuleType";
    public static final String RULE_INFO_ENTITY_NAME = "RuleInfo";

    /**
     * 初始化规则管理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys017/sys017", modelMap)
    }

    /**
     * 目录树数据
     */
    public void ruleTypeTree(HttpServletRequest request, HttpServletResponse response) {
        String loadRule = request.getParameter("loadRule");
        // 处理请求参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 查询条件
        opData.setStrValue(prePath + "entityName", RULE_TYPE_ENTITY_NAME);
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "ruleTypeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "ruleTypeName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "upRuleTypeId");
        // 调用服务增加查询
        svcRequest.addOp("QUERY_RULE_TYPE_RHT_CMPT", opData);
        // 调用服务增加查询
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> ruleTypeIdList = new ArrayList<String>();
        if (result) {
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_RULE_TYPE_RHT_CMPT").getBeanByPath("Operation.OpResult");
            if (treeBean != null) {
                int count = treeBean.getListNum("OpResult.Node");
                int i = 0;
                for (i = 0; i < count; i++) {
                    String id = treeBean.getStrValue("""OpResult.Node[${i}].ruleTypeId""");
                    ruleTypeIdList.add(id);
                    String pId = treeBean.getStrValue("""OpResult.Node[${i}].upRuleTypeId""");
                    String name = treeBean.getStrValue("""OpResult.Node[${i}].ruleTypeName""");
                    boolean open = false;
                    if (i == 0) {
                        open = true;
                    }
                    stringBuilder.append("""{ id: "${id}",typeId: "${id}", pId: "${
                        pId
                    }", iconSkin: "folder",name: "${
                        name
                    }",open: "${
                        open
                    }"},""");
                }
            }
            // 获取规则目录下的所有规则
            if (ruleTypeIdList.size() > 0 && "1".equals(loadRule)) {
                svcRequest = RequestUtil.getSvcRequest(request);
                opData = new XmlBean();
                // 查询条件
                opData.setStrValue(prePath + "ruleTypeId", "${ruleTypeIdList.join(',')}");
                // 调用服务查询数据
                svcRequest.addOp("QUERY_RULE_TYPE_BY_TYPES", opData);
                svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_RULE_TYPE_BY_TYPES")
                            .getBeanByPath("Operation.OpResult.RuleInfos");
                    if (resultBean != null) {
                        // 获取数据行
                        int i = 0;
                        def count = resultBean.getListNum("RuleInfos.RuleInfo");
                        for (i = 0; i < count - 1; i++) {
                            String id = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleId""");
                            String pId = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleTypeId""");
                            String name = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleName""");
                            stringBuilder.append("""{ id: "${id}", pId: "${pId}", name: "${
                                HtmlUtils.htmlEscape(name)
                            }"},""");
                        }
                        String id = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleId""");
                        String pId = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleTypeId""");
                        String name = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleName""");
                        stringBuilder.append("""{ id: "${id}", pId: "${pId}", name: "${
                            HtmlUtils.htmlEscape(name)
                        }"},""");
                    }
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${
            stringBuilder.toString()
        }]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 目录类型规则
     */
    public void ruleTypeNodes(HttpServletRequest request, HttpServletResponse response) {
        // 处理请求参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 查询条件
        opData.setStrValue("OpData.ruleTypeId", request.getParameter("ruleTypeId"));
        // 调用服务查询数据
        svcRequest.addOp("QUERY_RULE_TYPE_BY_TYPES", opData);
        SvcResponse svcResponse = query(svcRequest);
        StringBuilder stringBuilder = new StringBuilder();
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_RULE_TYPE_BY_TYPES")
                    .getBeanByPath("Operation.OpResult.RuleInfos");
            if (resultBean != null) {
                // 获取数据行
                int i = 0;
                def count = resultBean.getListNum("RuleInfos.RuleInfo");
                for (i = 0; i < count - 1; i++) {
                    String id = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleCd""");
                    String pId = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleTypeId""");
                    String name = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleName""");
                    String rulePrjCd = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].prjCd""");
                    stringBuilder.append("""{ id: "${id}", pId: "${pId}", name: "${
                        HtmlUtils.htmlEscape(name)
                    }", rulePrjCd: "${
                        rulePrjCd
                    }"},""");
                }
                String id = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleCd""");
                String pId = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleTypeId""");
                String name = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].ruleName""");
                String rulePrjCd = resultBean.getStrValue("""RuleInfos.RuleInfo[${i}].prjCd""");
                stringBuilder.append("""{ id: "${id}", pId: "${pId}", name: "${
                    HtmlUtils.htmlEscape(name)
                }", rulePrjCd: "${
                    rulePrjCd
                }"},""");
            }
        }
        String jsonStr = """resultMap:{treeJson:[${stringBuilder.toString()}]}""";
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 初始化规则管理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView ruleTypeInit(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回视图
        ModelMap modelMap = new ModelMap();
        // 获取输入参数
        String ruleTypeId = request.getParameter("ruleTypeId");
        if (!StringUtil.isEmptyOrNull(ruleTypeId)) {
            // 调用服务后去分类定义信息
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
            opData.setStrValue("OpData.entityKey", ruleTypeId);
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.RuleType");
                modelMap.put("ruleTypeInfo", opResult.getRootNode());
                int rhtCnt = opResult.getListNum("RuleType.RuleTypeRhts.RuleTypeRht");
                if (rhtCnt > 0) {
                    /* 处理授权角色信息, 后去角色MAP对象 */
                    Map<String, String> roleMap = new LinkedHashMap<String, String>();
                    svcRequest = RequestUtil.getSvcRequest(request);
                    XmlBean reqData = new XmlBean();
                    reqData.setStrValue("SvcCont.Role.roleCd", "");
                    svcRequest.setReqData(reqData);
                    svcResponse = callService("roleService", "queryTree", svcRequest);
                    boolean result = svcResponse.isSuccess();
                    if (result) {
                        XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont.Role");
                        int count = treeBean.getListNum("Role.Node");
                        for (int i = 0; i < count; i++) {
                            String id = treeBean.getStrValue("""Role.Node[${i}].roleCd""");
                            String name = treeBean.getStrValue("""Role.Node[${i}].roleName""");
                            roleMap.put(id, name);
                        }
                    }
                    List<XmlNode> rhtInfos = new ArrayList<XmlNode>(rhtCnt);
                    for (int i = 0; i < rhtCnt; i++) {
                        XmlBean tempBean = opResult.getBeanByPath("RuleType.RuleTypeRhts.RuleTypeRht[${i}]");
                        String rhtId = tempBean.getStrValue("RuleTypeRht.rhtId");
                        tempBean.setStrValue("RuleTypeRht.rhtName", roleMap.get(rhtId));
                        rhtInfos.add(tempBean.getRootNode());
                    }
                    modelMap.put("rhtList", rhtInfos);
                }

            }
        } else {
            XmlBean newXmlBean = new XmlBean();
            newXmlBean.setStrValue("RuleType.ruleTypeId", "");
            newXmlBean.setStrValue("RuleType.upRuleTypeId", request.getParameter("upRuleTypeId"));
            modelMap.put("ruleTypeInfo", newXmlBean.getRootNode());
        }
        return new ModelAndView("/oframe/sysmg/sys017/sys017_rule_type", modelMap)
    }

    /**
     * 保存规则分类
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void saveRuleType(HttpServletRequest request, HttpServletResponse response) {
        // 输出结果
        StringBuilder stringBuilder = new StringBuilder();
        // 获取新增文件夹所属上级id
        String ruleTypeId = request.getParameter("ruleTypeId");
        String upRuleTypeId = request.getParameter("upRuleTypeId");
        String ruleTypeName = request.getParameter("ruleTypeName");
        boolean addFlag = false;
        if (StringUtil.isEmptyOrNull(ruleTypeId)) {
            ruleTypeId = ""
            addFlag = true;
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
        opData.setStrValue("OpData.RuleType.ruleTypeId", ruleTypeId);
        opData.setStrValue("OpData.RuleType.upRuleTypeId", upRuleTypeId);
        opData.setStrValue("OpData.RuleType.ruleTypeName", ruleTypeName);

        // 角色授权信息
        String[] rhtIdArr = request.getParameterValues("rhtId");
        int addCount = 0;
        for (int i = 0; i < rhtIdArr.length; i++) {
            String temp = rhtIdArr[i];
            if (StringUtil.isNotEmptyOrNull(temp)) {
                opData.setStrValue("OpData.RuleType.RuleTypeRhts.RuleTypeRht[${addCount}].rhtType", "0");
                opData.setStrValue("OpData.RuleType.RuleTypeRhts.RuleTypeRht[${addCount++}].rhtId", temp);
            }
        }

        svcRequest.addOp("SAVE_RULE_TYPE_CMPT", opData);
        //调用最新的服务
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean folderBean = svcResponse.getFirstOpRsp("SAVE_RULE_TYPE_CMPT").getBeanByPath("Operation.OpData");
            String newRuleTypeId = folderBean.getStrValue("OpData.RuleType.ruleTypeId");
            stringBuilder.append("""{ id: "${newRuleTypeId}", typeId: "${newRuleTypeId}", pId: "${
                upRuleTypeId
            }", iconSkin: "folder",  name: "${ruleTypeName}"}""");
        }
        String jsonStr = """data: {addFlag: ${addFlag}, node:[${stringBuilder.toString()}]}""";
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 保存规则分类
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void deleteRuleType(HttpServletRequest request, HttpServletResponse response) {
        // 输出结果
        StringBuilder stringBuilder = new StringBuilder();
        // 获取新增文件夹所属上级id
        String ruleTypeId = request.getParameter("ruleTypeId");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
        opData.setStrValue("OpData.entityKey", ruleTypeId);
        svcRequest.addOp("DELETE_RULE_TYPE_CMPT", opData);
        //调用最新的服务
        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 移动分类
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void moveRuleType(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String moveType = request.getParameter("moveType");
        if ("prev".equals(moveType)) {
            moveType = "2"
        } else if ("next".equals(moveType)) {
            moveType = "3"
        } else {
            moveType = "1"
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.moveId", request.getParameter("moveId"));
        opData.setStrValue("OpData.moveType", moveType);
        opData.setStrValue("OpData.moveToId", request.getParameter("moveToId"));
        svcRequest.addOp("MOVE_RULE_TYPE_CMPT", opData);
        //调用最新的服务
        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化规则管理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView ruleInit(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回视图
        ModelMap modelMap = new ModelMap();
        String ruleCd = request.getParameter("ruleCd");
        String ruleTypeId = request.getParameter("ruleTypeId");
        String rulePrjCd = request.getParameter("rulePrjCd");
        String ruleId = request.getParameter("ruleId");
        String cmptName = "QUERY_RULE_INFO_ENABLED_CMPT";

        // 定义服务请求
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用服务获取使用平台
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CmpPrj");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "prjCd");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "prjName");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);

        // 规则编号及版本信息
        String usePrjCd = "";
        String ruleEnableTime = "";
        String ruleStatusTime = "";
        // 新增规则
        boolean addRule = true;
        //判断是否是新增 ruleCd 为空时为新增
        if (StringUtil.isNotEmptyOrNull(ruleCd)) {
            addRule = false;
            // 调用实体查询组件获取规则配置信息
            opData = new XmlBean();
            if (StringUtil.isNotEmptyOrNull(ruleId)) {
                cmptName = "QUERY_ALL_ENTITY";
                opData.setStrValue("OpData.entityName", RULE_INFO_ENTITY_NAME);
                opData.setStrValue("OpData.entityKey", ruleId);
            } else {
                opData.setStrValue("OpData.ruleCd", ruleCd);
                opData.setStrValue("OpData.prjCd", rulePrjCd);
            }
            // 调用服务
            svcRequest.addOp(cmptName, opData);
        }
        SvcResponse svcResponse = query(svcRequest);

        // 处理返回结果
        if (svcResponse.isSuccess()) {
            XmlBean ruleInfo = null;
            XmlBean resultBean = null;
            if (!addRule) {
                resultBean = svcResponse.getFirstOpRsp(cmptName);
                ruleInfo = resultBean.getBeanByPath("Operation.OpResult.RuleInfo");
                // 用于查询版本信息
                ruleCd = ruleInfo.getStrValue("RuleInfo.ruleCd");
                usePrjCd = ruleInfo.getStrValue("RuleInfo.prjCd");
                ruleEnableTime = ruleInfo.getStrValue("RuleInfo.ruleEnableTime");
                ruleStatusTime = ruleInfo.getStrValue("RuleInfo.ruleStatusTime");
                if (StringUtil.isNotEmptyOrNull(ruleEnableTime)) {
                    Date ctDateD = DateUtil.parse(ruleEnableTime, "yyyyMMddHHmmss");
                    ruleEnableTime = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss");
                    modelMap.put("ruleEnableTime", ruleEnableTime);
                }
                if (StringUtil.isNotEmptyOrNull(ruleStatusTime)) {
                    Date ctDateD = DateUtil.parse(ruleStatusTime, "yyyyMMddHHmmss");
                    ruleStatusTime = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss");
                    modelMap.put("ruleStatusTime", ruleStatusTime);
                }
            } else {
                ruleInfo = new XmlBean();
                ruleInfo.setStrValue("RuleInfo.ruleTypeId", ruleTypeId);
                String ruleScript = """/**
 * 在此编写规则脚本
 * 请填写规则用途：
 * ${DateUtil.toStringYmdHmsWthH(new Date())}
 */

def entryMethod(temp){

}""";
                ruleInfo.setStrValue("RuleInfo.ruleScript", ruleScript);
                ruleInfo.setStrValue("RuleInfo.entryMethod", "entryMethod");
                ruleInfo.setStrValue("RuleInfo.prjCd", request.getParameter("prjCd"));
            }
            modelMap.put("ruleInfo", ruleInfo.getRootNode());
            // 处理适用平台
            resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            Map<String, String> prjMap = new LinkedHashMap<String, String>();
            prjMap.put("0", "系统基础平台");
            if (resultBean != null) {
                int prjNum = resultBean.getListNum("PageData.Row");
                for (int i = 0; i < prjNum; i++) {
                    String prjCd = resultBean.getStrValue("PageData.Row[${i}].prjCd");
                    String prjName = resultBean.getStrValue("PageData.Row[${i}].prjName");
                    prjMap.put(prjCd, prjName);
                }
            }
            modelMap.put("prjMap", prjMap);

            // 修改规则则获取规则的版本
            if (!addRule) {
                // 按照规则编号，使用平台查询所有历史版本号
                svcRequest = RequestUtil.getSvcRequest(request);
                opData = new XmlBean();
                opData.setStrValue("OpData.entityName", RULE_INFO_ENTITY_NAME);

                // 查询条件
                opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "ruleCd");
                opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", ruleCd);
                opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");

                // 使用平台
                opData.setStrValue("OpData.Conditions.Condition[1].fieldName", "prjCd");
                opData.setStrValue("OpData.Conditions.Condition[1].fieldValue", usePrjCd);
                opData.setStrValue("OpData.Conditions.Condition[1].operation", "=");
                // 查询版本
                opData.setStrValue("OpData.ResultFields.fieldName[0]", "ruleVersion");
                opData.setStrValue("OpData.ResultFields.fieldName[1]", "ruleId");
                svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
                // 调用服务获取版本列表
                svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    // 处理适用平台
                    resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                    Map<String, String> versionMap = new LinkedHashMap<String, String>();
                    if (resultBean != null) {
                        int prjNum = resultBean.getListNum("PageData.Row");
                        for (int i = 0; i < prjNum; i++) {
                            String temp01 = resultBean.getStrValue("PageData.Row[${i}].ruleId");
                            String temp02 = resultBean.getStrValue("PageData.Row[${i}].ruleVersion");
                            versionMap.put(temp01, temp02);
                        }
                    }
                    modelMap.put("versionMap", versionMap);
                }
            }

        }
        return new ModelAndView("/oframe/sysmg/sys017/sys017_rule", modelMap)
    }

    /**
     * 初始化规则管理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView ruleCode(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回视图
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys017/sys017_rule_code", modelMap)
    }

    /**
     * 保存规则
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void saveRuleInfo(HttpServletRequest request, HttpServletResponse response) {
        // 输出结果
        StringBuilder stringBuilder = new StringBuilder();
        // 原规则编号
        String oldRuleCd = request.getParameter("oldRuleCd");
        String oldPrjCd = request.getParameter("oldPrjCd");
        // 获取新增文件夹所属上级id
        String ruleId = request.getParameter("ruleId");
        String ruleTypeId = request.getParameter("ruleTypeId");
        String ruleCd = request.getParameter("ruleCd");
        String ruleName = request.getParameter("ruleName");
        String usePrjCd = request.getParameter("usePrjCd");
        boolean addFlag = false;
        if (StringUtil.isEmptyOrNull(ruleId)) {
            ruleId = ""
            addFlag = true;
        } else if (!StringUtil.isEqual(oldPrjCd, usePrjCd) || !StringUtil.isEqual(oldRuleCd, ruleCd)) {
            // 规则编码和规则适用平台发生修改将自动新建新的规则
            ruleId = "";
            addFlag = true;
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
        opData.setStrValue("OpData.EntityData.ruleId", ruleId);
        opData.setStrValue("OpData.EntityData.ruleCd", ruleCd);
        opData.setStrValue("OpData.EntityData.prjCd", usePrjCd);
        opData.setStrValue("OpData.EntityData.ruleName", ruleName);
        opData.setStrValue("OpData.EntityData.entryMethod", request.getParameter("entryMethod"));
        opData.setStrValue("OpData.EntityData.ruleDesc", request.getParameter("ruleDesc"));
        opData.setStrValue("OpData.EntityData.ruleScript", request.getParameter("ruleScript"));
        opData.setStrValue("OpData.EntityData.ruleScriptType", request.getParameter("ruleScriptType"));

        opData.setStrValue("OpData.EntityData.ruleTypeId", ruleTypeId);

        svcRequest.addOp("SAVE_RULE_CFG_CMPT", opData);
        //调用最新的服务
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("SAVE_RULE_CFG_CMPT").getBeanByPath("Operation.OpResult.entityKey");
            String newRuleId = resultBean.getStrValue("entityKey");
            stringBuilder.append("nodeInfo: {id: '${ruleCd}'," + " pId: '${ruleTypeId}', name: '${ruleName}',rulePrjCd: '${usePrjCd}',ruleId: '${newRuleId}'}");
        }
        JSONObject result = new JSONObject();
        result.put("success", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());
        result.put("data", JSONObject.fromObject("{addFlag:" + addFlag + "," + stringBuilder.toString() + "}"));
        ResponseUtil.printAjax(response, result.toString());
    }

    /**
     * 修改规则状态
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void updateRuleStatus(HttpServletRequest request, HttpServletResponse response) {
        // 获取新增文件夹所属上级id
        String ruleId = request.getParameter("ruleId");
        String statusCd = request.getParameter("statusCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
        opData.setStrValue("OpData.EntityData.ruleId", ruleId);
        opData.setStrValue("OpData.EntityData.ruleStatus", statusCd);
        svcRequest.addOp("SAVE_RULE_CFG_CMPT", opData);
        // 调用服务
        SvcResponse svcResponse = transaction(svcRequest);
        //调用最新的服务
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 修改规则状态
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void moveRule(HttpServletRequest request, HttpServletResponse response) {
        // 获取新增文件夹所属上级id
        String ruleCd = request.getParameter("moveCd");
        String ruleTypeId = request.getParameter("moveToId");
        String rulePrjCd = request.getParameter("rulePrjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
        opData.setStrValue("OpData.EntityData.ruleCd", ruleCd);
        opData.setStrValue("OpData.EntityData.prjCd", rulePrjCd);
        opData.setStrValue("OpData.EntityData.ruleTypeId", ruleTypeId);
        svcRequest.addOp("MOVE_RULE_CMPT", opData);
        // 调用服务
        SvcResponse svcResponse = transaction(svcRequest);
        //调用最新的服务
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 导出规则
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void exportRule(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用实体查询组件获取规则配置信息
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.entityName", RULE_INFO_ENTITY_NAME);
        opData.setStrValue("OpData.entityKey", request.getParameter("ruleId"));
        // 调用服务
        svcRequest.addOp("QUERY_ALL_ENTITY", opData);
        SvcResponse svcResponse = query(svcRequest);
        //处理返回结果
        String data = "{}";
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY");
            XmlBean ruleInfo = resultBean.getBeanByPath("Operation.OpResult.RuleInfo");
            String ruleCd = ruleInfo.getStrValue("RuleInfo.ruleCd")
            String export = ruleInfo.toString();
            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(export, "UTF-8");
            data = """{\"remoteFile\":\"${filePath}\","clientFile":"${ruleCd}.xml\"}"""
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

    /**
     * 批量导出规则
     */
    public void batchExport(HttpServletRequest request, HttpServletResponse response) {
        String ruleCd = request.getParameter("ruleCd");
        String rulePrjCd = request.getParameter("rulePrjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String[] ruleCds = ruleCd.split(",");
        String[] rulePrjCds = rulePrjCd.split(",");
        int temp = 0;
        for (int i = 0; i < ruleCds.length; i++) {
            opData.setStrValue("OpData.RuleInfo[" + temp + "].ruleCd", ruleCds[i]);
            opData.setStrValue("OpData.RuleInfo[" + temp++ + "].prjCd", rulePrjCds[i]);
        }
        svcRequest.addOp("EXPORT_RULES_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        String data = "{}";
        if (svcResponse.isSuccess()) {
            XmlBean ruleInfo = svcResponse.getFirstOpRsp("EXPORT_RULES_CMPT").getBeanByPath("Operation.OpResult.RuleInfos");
            String export = ruleInfo.toString();
            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(export, "UTF-8");
            data = """{\"remoteFile\":\"${filePath}\","clientFile":"批量规则导出.xml\"}"""
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

    /**
     * 打开配置导入
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView openImport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String ruleTypeId = request.getParameter("ruleTypeId");
        modelMap.put("ruleTypeId", ruleTypeId);
        // 获取工号的项目
        // 获取工号可以访问的项目工程
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "SvcCont.staffCode";
        XmlBean reqData = new XmlBean();
        // 查询条件
        reqData.setStrValue(prePath, svcRequest.getReqStaffCd());
        reqData.setStrValue("SvcCont.isShowExp", "1");   // 是否显示无效项目标志。 1 不显示无效项目， 0  显示。
        // 调用服务查询实体全部属性
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "queryStaffProject", svcRequest);
        Map<String, String> prjMap = new LinkedHashMap<String, String>();
        // 手动增加一个系统基础平台的选项
        prjMap.put("0", "系统基础平台");
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
            if (null != xmlBean) {
                int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                for (int i = 0; i < cmpPrjCount; i++) {
                    prjMap.put(xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"),
                            xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjName"))
                }
            }
        }
        modelMap.put("prjMap", prjMap);
        return new ModelAndView("/oframe/sysmg/sys017/sys017_import", modelMap);
    }

    /**
     * 导入规则
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void importRule(HttpServletRequest request, HttpServletResponse response) {
        // 输出结果
        StringBuilder stringBuilder = new StringBuilder();
        // 原规则编号
        String toPrjCd = request.getParameter("toPrjCd");
        String ruleTypeId = request.getParameter("ruleTypeId");
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "sys017ImportXmlFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        boolean result = false;
        String errMsg = "";
        String batUpdateRule = "";
        String sinUpdateRule = "";
        if (fileType.contains(".xml")) {
            /* file2String */
            try {
                String fileText = new InputStreamReader(localFile.getInputStream(), "UTF-8").getText();
                XmlBean tempRuleBean = new XmlBean(fileText);
                int ruleNum = tempRuleBean.getListNum("RuleInfos.RuleInfo");
                for (int i = 0; i < ruleNum; i++) {
                    XmlBean ruleXml = tempRuleBean.getBeanByPath("RuleInfos.RuleInfo[" + i + "]");
                    ruleXml.setStrValue("RuleInfo.prjCd", toPrjCd);
                }
                fileText = tempRuleBean.toString();
                if (StringUtil.isEmptyOrNull(errMsg)) {
                    byte[] bytes = fileText.getBytes("utf-8");
                    if (bytes.length > 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                        fileText = new String(bytes, 3, bytes.length - 3, "utf-8");
                    }
                    XmlBean importBean = new XmlBean(fileText);
                    // 组织报文调用服务
                    SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
                    XmlBean opData = new XmlBean();
                    XmlBean flagNode = importBean.getBeanByPath("RuleInfos");
                    if (flagNode != null) {
                        opData.setBeanByPath("OpData", importBean);
                        svcRequest.addOp("IMPORT_RULES_CMPT", opData);
                    } else {
                        // 把获取的数据信息赋给新建文件夹
                        opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
                        opData.setStrValue("OpData.EntityData.ruleCd", importBean.getStrValue("RuleInfo.ruleCd"));
                        opData.setStrValue("OpData.EntityData.prjCd", importBean.getStrValue("RuleInfo.prjCd"));
                        opData.setStrValue("OpData.EntityData.ruleName", importBean.getStrValue("RuleInfo.ruleName"));
                        opData.setStrValue("OpData.EntityData.entryMethod", importBean.getStrValue("RuleInfo.entryMethod"));
                        opData.setStrValue("OpData.EntityData.ruleDesc", importBean.getStrValue("RuleInfo.ruleDesc"));
                        opData.setStrValue("OpData.EntityData.ruleScript", importBean.getStrValue("RuleInfo.ruleScript"));
                        opData.setStrValue("OpData.EntityData.ruleScriptType", importBean.getStrValue("RuleInfo.ruleScriptType"));
                        opData.setStrValue("OpData.EntityData.ruleTypeId", ruleTypeId);
                        svcRequest.addOp("SAVE_RULE_CFG_CMPT", opData);
                    }
                    //调用最新的服务
                    SvcResponse svcResponse = transaction(svcRequest);
                    if (svcResponse.isSuccess()) {
                        if (flagNode != null) {
                            XmlBean resultBean = svcResponse.getFirstOpRsp("IMPORT_RULES_CMPT");
                            batUpdateRule = resultBean.getStrValue("Operation.OpResult.ruleCds");
                        } else {
                            XmlBean resultBean = svcResponse.getFirstOpRsp("SAVE_RULE_CFG_CMPT");
                            sinUpdateRule = resultBean.getStrValue("Operation.OpResult.entityKey");
                        }
                        result = true;
                    } else {
                        errMsg = svcResponse.getErrMsg();
                    }
                }
            } catch (Exception e) {
                log.error("导入文件读取失败,请确认文件内容是否正确", e);
                errMsg = "导入文件读取失败，请确认文件内容是否正确";
            }
        } else {
            errMsg = "文件格式不正确";
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", result);
        jsonObject.put("errMsg", errMsg);
        jsonObject.put("batUpdateRule", batUpdateRule);
        jsonObject.put("sinUpdateRule", sinUpdateRule);
        ResponseUtil.printAjax(response, jsonObject.toString());
    }
    /**
     * 修改规则状态
     * @param request 用户请求参数
     * @param response 处理响应结果
     */
    public void deleteRule(HttpServletRequest request, HttpServletResponse response) {
        // 获取新增文件夹所属上级id
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 把获取的数据信息赋给新建文件夹
        opData.setStrValue("OpData.entityName", RULE_TYPE_ENTITY_NAME);
        opData.setStrValue("OpData.entityKey", request.getParameter("ruleId"));
        svcRequest.addOp("DELETE_RULE_CFG_CMPT", opData);
        // 调用服务
        SvcResponse svcResponse = transaction(svcRequest);
        //调用最新的服务
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }


}


