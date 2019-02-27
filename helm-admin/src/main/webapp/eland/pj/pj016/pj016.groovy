import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
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
 * 外聘成本管理
 * Created by Administrator on 2014/6/23 0023.
 */
class pj016 extends GroovyController {

    public void treeInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String objValue = request.getParameter("objValue");
        // 是否显示文件夹
        String showFile = request.getParameter("showFile");
        Set<String> checkedOrg = new HashSet<String>();
        if (StringUtil.isNotEmptyOrNull(objValue)) {
            String[] objValues = objValue.split(",");
            for (int i = 0; i < objValues.length; i++) {
                checkedOrg.add(objValues[i]);
            }
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 查询条件
        opData.setStrValue(prePath + "entityName", "CmpExtCmp");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "extCmpId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "extCmpName");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        // 调用服务增加查询
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder strb = new StringBuilder();
        if (result) {
            boolean haveCheck = checkedOrg.contains("0");
            strb.append("""{"id": "1","pId":"0", "name": "项目外聘公司", "iconSkin": "folder", "checked": "${
                haveCheck
            }", "open": "true"},""");
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (treeBean != null) {
                int count = treeBean.getListNum("PageData.Row") - 1;
                if (count != null) {
                    for (int i = 0; i < count; i++) {
                        String id = treeBean.getStrValue("PageData.Row[${i}].extCmpId");
                        String name = treeBean.getStrValue("PageData.Row[${i}].extCmpName");

                        boolean haveChecked = checkedOrg.contains(id);
                        boolean open = false;
                        strb.append("""{"id": "${id}","pId":"1", "name": "${name}", "iconSkin": "folder", "checked": "${
                            haveChecked
                        }", "open": "${open}"},""");
                    }
                    // 拼接最后一个节点
                    String id = treeBean.getStrValue("PageData.Row[${count}].extCmpId");
                    String name = treeBean.getStrValue("PageData.Row[${count}].extCmpName");
                    boolean haveChecked = checkedOrg.contains(id);
                    strb.append("""{"id":"${id}","pId":"1", "name":"${name}","iconSkin": "folder", "checked": "${
                        haveChecked
                    }", " open": "true"}""");
                }
            }
        }
        String jsonStr = """{"success":"${result}", "errMsg":"${errMsg}", "resultMap":{"treeJson":[${
            strb.toString()
        }]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化 外聘成本管理
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
//        String extCmpId = request.getParameter("extCmpId");
//        if (StringUtil.isNotEmptyOrNull(extCmpId)) {
//            XmlBean opData = new XmlBean();
//            opData.setStrValue("OpData.entityName", "PayRecordInfo");
//            opData.setStrValue("OpData.ParamInfo.Param.name", "extCmpId");
//            opData.setStrValue("OpData.ParamInfo.Param.value", extCmpId);
//            opData.setStrValue("OpData.JobResultFields.fieldName[0]", "payBuldNum");
//            opData.setStrValue("OpData.JobResultFields.fieldName[1]", "payHsNum");
//            opData.setStrValue("OpData.JobResultFields.fieldName[2]", "unPayBuldNum");
//            opData.setStrValue("OpData.JobResultFields.fieldName[3]", "unPayHsNum");
//            opData.setStrValue("OpData.JobResultFields.fieldName[4]", "payFixSerFee");
//            opData.setStrValue("OpData.JobResultFields.fieldName[5]", "payZhFee");
//            opData.setStrValue("OpData.JobResultFields.fieldName[6]", "payTotalFee");
//            opData.setStrValue("OpData.JobResultFields.fieldName[7]", "unPayFixSerFee");
//            opData.setStrValue("OpData.JobResultFields.fieldName[8]", "unPayZhFee");
//            opData.setStrValue("OpData.JobResultFields.fieldName[9]", "unPayTotalFee");
//            svcRequest.addOp("JOB_DEF_SQL_CMPT", opData);
//            SvcResponse cmpResp = query(svcRequest);
//            if (cmpResp.isSuccess()) {
//                XmlBean cmpResult = cmpResp.getFirstOpRsp("JOB_DEF_SQL_CMPT");
//                if (cmpResult != null) {
//                    XmlBean jobDefBean = cmpResult.getBeanByPath("Operation.OpResult.JobDefResults");
//                    modelMap.put("jobDefBean", jobDefBean.getRootNode());
//                }
//            }
//        }
//        modelMap.put("extCmpId", extCmpId);
        return new ModelAndView("/eland/pj/pj016/pj016_tree", modelMap);
    }

    /**
     * 初始化 外聘成本管理
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView initData(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String extCmpId = request.getParameter("extCmpId");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "PayRecordInfo");
        opData.setStrValue("OpData.ParamInfo.Param.name", "extCmpId");
        opData.setStrValue("OpData.ParamInfo.Param.value", extCmpId);
        opData.setStrValue("OpData.JobResultFields.fieldName[0]", "payBuldNum");
        opData.setStrValue("OpData.JobResultFields.fieldName[1]", "payHsNum");
        opData.setStrValue("OpData.JobResultFields.fieldName[2]", "unPayBuldNum");
        opData.setStrValue("OpData.JobResultFields.fieldName[3]", "unPayHsNum");
        opData.setStrValue("OpData.JobResultFields.fieldName[4]", "payFixSerFee");
        opData.setStrValue("OpData.JobResultFields.fieldName[5]", "payZhFee");
        opData.setStrValue("OpData.JobResultFields.fieldName[6]", "payTotalFee");
        opData.setStrValue("OpData.JobResultFields.fieldName[7]", "unPayFixSerFee");
        opData.setStrValue("OpData.JobResultFields.fieldName[8]", "unPayZhFee");
        opData.setStrValue("OpData.JobResultFields.fieldName[9]", "unPayTotalFee");
        svcRequest.addOp("JOB_DEF_SQL_CMPT", opData);
        SvcResponse cmpResp = query(svcRequest);
        if (cmpResp.isSuccess()) {
            XmlBean cmpResult = cmpResp.getFirstOpRsp("JOB_DEF_SQL_CMPT");
            if (cmpResult != null) {
                XmlBean jobDefBean = cmpResult.getBeanByPath("Operation.OpResult.JobDefResults");
                modelMap.put("jobDefBean", jobDefBean.getRootNode());
            }
        }
        modelMap.put("extCmpId", extCmpId);
        return new ModelAndView("/eland/pj/pj016/pj016", modelMap);
    }

    //待支付院落
    public ModelAndView initWzf(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String extCmpId = request.getParameter("extCmpId");
        modelMap.put("extCmpId", extCmpId);
        return new ModelAndView("/eland/pj/pj016/pj016_wzf", modelMap);
    }

    //已支付院落
    public ModelAndView initYzf(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String extCmpId = request.getParameter("extCmpId");
        modelMap.put("extCmpId", extCmpId);
        return new ModelAndView("/eland/pj/pj016/pj016_yzf", modelMap);
    }

    //负责院落
    public ModelAndView initFuze(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String extCmpId = request.getParameter("extCmpId");
        modelMap.put("extCmpId", extCmpId);
        return new ModelAndView("/eland/pj/pj016/pj016_fuze", modelMap);
    }

    //支付记录
    public ModelAndView initZfjl(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String extCmpId = request.getParameter("extCmpId");
        modelMap.put("extCmpId", extCmpId);
        return new ModelAndView("/eland/pj/pj016/pj016_zfjl", modelMap);
    }

    //查看支付记录详情
    public ModelAndView viewPayDetail(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String payRecordId = request.getParameter("payRecordId");
        modelMap.put("payRecordId", payRecordId);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "PayRecordInfo");
        opData.setStrValue("OpData.entityKey", payRecordId);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ALL_ENTITY", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultTemp = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY");
            if (resultTemp != null) {
                XmlBean payResult = resultTemp.getBeanByPath("Operation.OpResult.PayRecordInfo");
                modelMap.put("payResult", payResult.getRootNode());
            }
        }
        return new ModelAndView("/eland/pj/pj016/pj016_zfjl_detail", modelMap);
    }

    //初始化支付页面
    public ModelAndView initPay(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String extCmpId = request.getParameter("extCmpId");
        String prjCd = request.getParameter("prjCd");
        modelMap.put("extCmpId", extCmpId);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "BuildInfo");
        opData.setStrValue("OpData.Conditions.Condition.fieldName", "buildId");
        opData.setStrValue("OpData.Conditions.Condition.fieldValue", "(${request.getParameter("buildId")})");
        opData.setStrValue("OpData.Conditions.Condition.operation", "IN");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "hsCount");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "buildFullAddr");
        opData.setStrValue("OpData.ResultFields.fieldName[2]", "totalZhFee");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT");
            if (queryResult != null) {
                XmlBean result = queryResult.getBeanByPath("Operation.OpResult.PageData");
                int rowNum = result.getListNum("PageData.Row");
                List buildList = new ArrayList(rowNum);
                for (int i = 0; i < rowNum; i++) {
                    buildList.add(result.getBeanByPath("PageData.Row[${i}]").getRootNode());
                }
                modelMap.put("buildList", buildList);
            }
            /**
             * 查询外聘公司 最后支付日期
             */
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "PrjExtCmp");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "extCmpId");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", extCmpId);
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
//            opData.setStrValue("OpData.Conditions.Condition[1].fieldName", "PrjExtCmp_prjCd");
//            opData.setStrValue("OpData.Conditions.Condition[1].fieldValue", prjCd);
//            opData.setStrValue("OpData.Conditions.Condition[1].operation", "=");
            opData.setStrValue("OpData.ResultFields.fieldName", "recentPayDate");
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
            SvcResponse cmpResp = query(svcRequest);
            if (cmpResp.isSuccess()) {
                XmlBean cmpBean = cmpResp.getFirstOpRsp("QUERY_ENTITY_CMPT");
                if (cmpBean != null) {
                    modelMap.put("cmpBean", cmpBean.getBeanByPath("Operation.OpResult.PageData.Row[0]").getRootNode());
                }
            }
        }
        return new ModelAndView("/eland/pj/pj016/pj01601", modelMap);
    }

    //保存 支付费用 。调用组件  SAVE_EXTERNAL_COST
    public void savePay(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String extCmpId = request.getParameter("extCmpId");
        String fixedServiceFee = request.getParameter("fixedServiceFee");
        String totalZhFee = request.getParameter("totalZhFee");
        String totalFee = request.getParameter("feeCount");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String buildMap = request.getParameter("buildMap");

        //保存支付表
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ExternalCostInfo.payRecordId", "\${payRecordId}");
        opData.setStrValue("OpData.ExternalCostInfo.extCmpId", extCmpId);
        opData.setStrValue("OpData.ExternalCostInfo.fixedServiceFee", fixedServiceFee);
        opData.setStrValue("OpData.ExternalCostInfo.totalZhFee", totalZhFee);
        opData.setStrValue("OpData.ExternalCostInfo.totalFee", totalFee);
        opData.setStrValue("OpData.ExternalCostInfo.fromDate", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(fromDate)));
        opData.setStrValue("OpData.ExternalCostInfo.toDate", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(toDate)));
        JSONObject jsonObject = JSONObject.fromObject(buildMap);
        Set buildIdSet = jsonObject.keySet();
        if (buildIdSet.size() > 0) {
            int x = 0;
            for (String temp : buildIdSet) {
                opData.setStrValue("OpData.ExternalCostInfo.BuildInfos.BuildInfo[${x}].buildId", temp);
                opData.setStrValue("OpData.ExternalCostInfo.BuildInfos.BuildInfo[${x++}].totalZhFee", jsonObject.get(temp));
            }
        }
        svcRequest.addOp("SAVE_EXTERNAL_COST", opData);
        SvcResponse svcResponse = transaction(svcRequest);
//        修改外聘公司表
        ResponseUtil.printAjax(response, """{"success":"${svcResponse.isSuccess()}","errMsg":"${
            svcResponse.getErrMsg()
        }"}""");
    }
}