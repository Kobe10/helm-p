import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房  房产信息展示页面
 */
class ph004 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init_bak(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        // 是否直接计算标志
        String hsId = requestUtil.getStrParam("hsId");
        String prjCd = requestUtil.getStrParam("prjCd");
        //查询 项目类型；确定项目是征收、腾退
        String prjType = null;
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "CmpPrj");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "prjCd");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", prjCd);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "prjType");
        svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);

        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取项目类型
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            prjType = rspData.getStrValue("PageData.Row[0].prjType");
        }

        // 房屋标志
        String[] hsFlagArr = request.getParameterValues("hsFlag");
        List<String> hsFlag = new ArrayList<String>();
        if (hsFlagArr != null) {
            hsFlag.addAll(hsFlagArr);
        }
        //推送到页面的参数
        XmlBean calParam = null;

        // 判断 是否根据 某房产 计算
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            SvcRequest hsAttrSvcReq = RequestUtil.getSvcRequest(request);
            XmlBean hsBean = new XmlBean();
            hsBean.setStrValue("OpData.entityName", "HouseInfo");
            hsBean.setStrValue("OpData.queryType", "2");
            hsBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "HouseInfo.hsId");
            hsBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            hsBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            hsBean.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo.hsBuildSize");
            hsBean.setStrValue("OpData.ResultFields.fieldName[1]", "HouseInfo.businessHsSize");
            hsBean.setStrValue("OpData.ResultFields.fieldName[2]", "HouseInfo.hsFlag");
            hsBean.setStrValue("OpData.ResultFields.fieldName[3]", "HouseInfo.allFamNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[4]", "HouseInfo.dbNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[5]", "HouseInfo.cjNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[6]", "HouseInfo.mgdGrZf");
            hsBean.setStrValue("OpData.ResultFields.fieldName[7]", "HouseInfo.ktNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[8]", "HouseInfo.ghNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[9]", "HouseInfo.yxNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[10]", "HouseInfo.rsqNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[11]", "HouseInfo.HsCtInfo.ctType");
            hsBean.setStrValue("OpData.ResultFields.fieldName[12]", "HouseInfo.hAgeNum");
            hsBean.setStrValue("OpData.ResultFields.fieldName[13]", "HouseInfo.seriousIll");
            hsBean.setStrValue("OpData.ResultFields.fieldName[14]", "HouseInfo.hsOwnerType");
            hsBean.setStrValue("OpData.ResultFields.fieldName[15]", "HouseInfo.hsType");

            // 房屋评估及房改售房信息
            hsBean.setStrValue("OpData.ResultFields.fieldName[16]", "HouseInfo.newWithHold");
            hsBean.setStrValue("OpData.ResultFields.fieldName[17]", "HouseInfo.transferStatus");
            hsBean.setStrValue("OpData.ResultFields.fieldName[18]", "HouseInfo.pgMoney1");
            hsBean.setStrValue("OpData.ResultFields.fieldName[19]", "HouseInfo.pgMoney2");
            hsBean.setStrValue("OpData.ResultFields.fieldName[20]", "HouseInfo.withHold");
            hsBean.setStrValue("OpData.ResultFields.fieldName[21]", "HouseInfo.basePrice");

            // 查询数据
            hsAttrSvcReq.addOp("QUERY_ENTITY_PAGE_DATA", hsBean);

            XmlBean hsFlagBean = new XmlBean();
            hsFlagBean.setStrValue("OpData.entityName", "HouseInfo");
            hsFlagBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            hsFlagBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            hsFlagBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            hsFlagBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsFlag");
            hsAttrSvcReq.addOp("QUERY_ENTITY_PAGE_DATA", hsFlagBean);
            SvcResponse hsAttrRsp = query(hsAttrSvcReq);

            if (hsAttrRsp.isSuccess()) {
                List hsBeanList = hsAttrRsp.getAllOpRsp("QUERY_ENTITY_PAGE_DATA");
                hsBean = hsBeanList.get(0).getBeanByPath("Operation.OpResult.PageData");
                calParam = hsBean.getBeanByPath("PageData.Row[0].HouseInfo");
                //  房屋标记
                hsFlag = new ArrayList<String>();
                XmlBean tempBean = hsBeanList.get(1);
                int count = tempBean.getListNum("Operation.OpResult.PageData.Row");
                for (int m = 0; m < count; m++) {
                    hsFlag.add(tempBean.getStrValue("Operation.OpResult.PageData.Row[${m}].hsFlag"));
                }
            } else {
                modelMap.put("errMsg", hsAttrRsp.getErrMsg());
            }
        }

        // 初始进入 计算页面，给定页面计算参数默认值
        if (calParam == null) {
            calParam = new XmlBean();
        }
        //默认值设置
        setBeanDefaultValue(calParam, "HouseInfo.hsBuildSize", "0.00");
        setBeanDefaultValue(calParam, "HouseInfo.businessHsSize", "0.00");
        setBeanDefaultValue(calParam, "HouseInfo.hsFlag", null);
        setBeanDefaultValue(calParam, "HouseInfo.allFamNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.dbNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.cjNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.mgdGrZf", "0");
        setBeanDefaultValue(calParam, "HouseInfo.ktNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.ghNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.yxNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.rsqNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.ctType", "1");
        setBeanDefaultValue(calParam, "HouseInfo.hAgeNum", "0");
        setBeanDefaultValue(calParam, "HouseInfo.seriousIll", "0");
        setBeanDefaultValue(calParam, "HouseInfo.newWithHold", "0.00");
        setBeanDefaultValue(calParam, "HouseInfo.basePrice", "0.00");
        setBeanDefaultValue(calParam, "HouseInfo.transferStatus", "0");
        setBeanDefaultValue(calParam, "HouseInfo.pgMoney1", "0.00");
        setBeanDefaultValue(calParam, "HouseInfo.pgMoney2", "0.00");
        setBeanDefaultValue(calParam, "HouseInfo.withHold", "0.00");
        modelMap.put("calParam", calParam.getRootNode());
        modelMap.put("hsFlag", hsFlag);

        // 项目编号
        modelMap.put("prjCd", prjCd);
        modelMap.put("hsId", hsId);

        // 根据项目类型：返回输出页面 征收、腾退
        String toPage = "/eland/ph/ph004/ph004";
        if ("2".equals(prjType)) {
            toPage = "/eland/ph/ph004/ph00401";
        }
        return new ModelAndView(toPage, modelMap);
    }

    /**
     * 初始补偿计算器  界面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        //获取系统通用表单编码----取得 修改评估数据表单 编码
        Map map = getCfgCollection(request, "PRJ_FORM", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = "-1";
        }
        //调用 获取表单 信息 返回页面
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        paramData.setStrValue("OpData.formCd", map.get("calculator"));
        // 生成报表参数
        paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", hsId);
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "showFlag");
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", "edit");

        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse pageResponse = query(svcRequest);
        if (pageResponse.isSuccess()) {
            XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
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
        }
        return new ModelAndView("/eland/ph/ph004/ph004_formula", modelMap);
    }

    //计算
    public ModelAndView cal(HttpServletRequest request, HttpServletResponse response) {

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();

        XmlBean opData = new XmlBean();
        String prePath = "OpData.RuleParam."

        Map<String, String[]> reqParamMap = request.getParameterMap();
        if (reqParamMap != null) {
            Iterator<String> it = reqParamMap.keySet().iterator();
            while (it.hasNext()) {
                String keyName = it.next();
                if (!StringUtil.contains(keyName, '_')) {
                    continue;
                }
                //截取 下面所需的 实体名、属性名、主键值：
                int first_order = keyName.indexOf("_");
                int last_order = keyName.lastIndexOf("_");
                String entityAttr = keyName.substring(first_order + 1, last_order);

                String[] listValue = reqParamMap.get(keyName);
                String value = null;
                if (listValue.length == 1) {
                    value = listValue[0];
                } else {
                    value = listValue.join(",");
                }
                opData.setStrValue(prePath + entityAttr, value);
            }
        }

        opData.removeNode("OpData.RuleParam.hsId");
        // 调用服务
        svcRequest.addOp("GET_HS_MONEY", opData);

        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //获取调用规则计算结果
            XmlBean opResult = svcResponse.getFirstOpRsp("GET_HS_MONEY");
            // 计算结果
            XmlBean calResult = opResult.getBeanByPath("Operation.OpResult.RuleResult.HsBcJe");
            if (calResult != null) {
                String schemeId = calResult.getStrValue("HsBcJe.schemeId");

                int itemSize = calResult.getListNum("HsBcJe.Items.Item");
                List<XmlNode> items = new LinkedList<XmlNode>();
                for (int i = 0; i < itemSize; i++) {
                    XmlBean temp = calResult.getBeanByPath("HsBcJe.Items.Item[$i]");
                    if ("1".equals(temp.getStrValue("Item.inUsed"))) {
                        items.add(temp.getRootNode());
                    }
                }
                // 计算结果
                modelMap.put("schemeId", schemeId);
                modelMap.put("items", items);
                modelMap.put("errMsg", "");
            } else {
                modelMap.put("errMsg", opResult.getBeanByPath("Operation.OpResult.errorMsg"));
            }
        } else {
            modelMap.put("errMsg", svcResponse.getErrMsg());
        }

        // 根据项目类型：返回输出页面 征收、腾退
        return new ModelAndView("/eland/ph/ph004/ph004_result", modelMap);
    }

    //反转Linkedlist 元素顺序
    public LinkedList<XmlNode> reverseLinkedList(LinkedList<XmlNode> changeList) {
        LinkedList<XmlNode> returnList = new LinkedList<XmlNode>();
        reverseOrder(returnList, changeList);
        return returnList;
    }

    private void reverseOrder(LinkedList<XmlNode> returnList, LinkedList<XmlNode> changeList) {
        XmlNode i = changeList.poll();
        if (changeList.size() == 0) {
            returnList.offer(i);
        } else {
            reverseOrder(returnList, changeList);
            returnList.offer(i);
        }
    }

    //三元运算
    private void setBeanDefaultValue(XmlBean xmlBean, String path, String defaultValue) {
        if (StringUtil.isEmptyOrNull(xmlBean.getStrValue(path))) {
            xmlBean.setStrValue(path, defaultValue);
        }
    }
}
