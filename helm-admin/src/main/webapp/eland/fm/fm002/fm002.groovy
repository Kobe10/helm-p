import com.shfb.oframe.core.util.common.NumberUtil
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
 * User: shfb_linql
 * Date: 2016/3/22 0022 15:48
 * Copyright(c) 北京四海富博计算机服务有限公司
 *
 * 财务管理 模块 付款计划
 */
class fm002 extends GroovyController {
    /**
     * 初始化 付款计划界面
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String pId = request.getParameter("pId");
        modelMap.put("pId", pId);
        //请求获取报文上半部分
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        //查询实体
        opData.setStrValue("OpData.entityName","PaymentPlan");
        //查询结果
        opData.setStrValue("OpData.ResultFields.fieldName[0]","pId");
        opData.setStrValue("OpData.ResultFields.fieldName[1]","pCode");
        opData.setStrValue("OpData.ResultFields.fieldName[2]","pStatus");
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_CMPT",opData);
        //启动查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()){
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (queryResult != null){
                List list = new ArrayList();
                int planNum = queryResult.getListNum("PageData.Row");
                for (int i = 0; i < planNum; i++) {
                    XmlBean tempBean = queryResult.getBeanByPath("PageData.Row[${i}]");
                    list.add(tempBean.getRootNode());
                }
                modelMap.put("planList",list);
            }
        }
        return new ModelAndView("/eland/fm/fm002/fm002", modelMap);
    }

    /**
     * 点击查询
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView  展示视图
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        //请求获取报文上半部分
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //取一对一的值
        String pId = request.getParameter("pId");//计划Id
        if (StringUtil.isNotEmptyOrNull(pId)){
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName","PaymentPlan");
            opData.setStrValue("OpData.entityKey", pId);
            svcRequest.addOp("QUERY_ALL_ENTITY",opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()){
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.PaymentPlan");
                if (queryResult != null){
                    modelMap.put("PaymentPlan",queryResult.getRootNode());
                    //取出pCtSchemeIds值 oframe:select 要用
                    String pCtSchemeIds = queryResult.getStrValue("PaymentPlan.pCtSchemeIds");
                    if(StringUtil.isNotEmptyOrNull(pCtSchemeIds)){
                        String[] temp = pCtSchemeIds.split(",");
                        List templist = temp.toList();
                        modelMap.put("pCtSchemeIds", templist);
                    }
                }
            }
        }
        Map<String, String> schemeTypes = getCfgCollection(request, "SCHEME_TYPE", true, NumberUtil.getIntFromObj("300"));
        modelMap.put("schemeTypes", schemeTypes);
        return  new ModelAndView("/eland/fm/fm002/fm00201",modelMap)
    }

    /**
     * 保存
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView  展示视图
     */
    public ModelAndView savePlan(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        //请求获取报文上半部分
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //取一对一的值
        String pId = request.getParameter("pId");//计划编号
        String pCode = request.getParameter("pCode");//计划名称
        String pStatus = request.getParameter("pStatus");//计划状态，码值为PAY_PLAN_STATUS(0:计划，1:启用)
        String pFormCd = request.getParameter("pFormCd");//五联单表单Cd
        String pCtSchemeIds = request.getParameter("pCtSchemeIds");//关联协议类型
        String pPreRule = request.getParameter("pPreRule");//付款前置条件
        String pPreExecRule = request.getParameter("pPreExecRule");//执行规则
        String pCalcMoney = request.getParameter("pCalcMoney");//付款总金额计算方式，规则片段
        String pCalcMoneyRule = request.getParameter("pCalcMoneyRule");//付款总金额计算方式，执行规则
        //拼接下半部分报文
        XmlBean opData = new XmlBean();
        String nodePath = "OpData.";
        //赋值
        opData.setStrValue(nodePath + "pCode", pCode);
        opData.setStrValue(nodePath + "pStatus", pStatus);
        opData.setStrValue(nodePath + "pFormCd", pFormCd);
        opData.setStrValue(nodePath + "pCtSchemeIds", pCtSchemeIds);
        opData.setStrValue(nodePath + "pPreRule", pPreRule);
        opData.setStrValue(nodePath + "pPreExecRule", pPreExecRule);
        opData.setStrValue(nodePath + "pCalcMoney", pCalcMoney);
        opData.setStrValue(nodePath + "pCalcMoneyRule", pCalcMoneyRule);
        if (StringUtil.isNotEmptyOrNull(pId)) {
            //修改
            opData.setStrValue(nodePath + "pId", pId);
        } else {
            //新增
            opData.setStrValue(nodePath + "pId", "");
        }
        //调用组件（构成框架SvcCont.Operation.）
        svcRequest.addOp("SAVE_PAYMENT_PLAN_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, """pId: '${pId}'""");
    }

    /**
     * 删除
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView  展示视图
     */
    public ModelAndView delPlan(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String pId = request.getParameter("pId");//计划编号
        XmlBean opData = new XmlBean();
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "pId", pId);
        svcRequest.addOp("DELETE_PAY_PLAN_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
    /**
     * iframe
     */
    public ModelAndView payCode(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回视图
        ModelMap modelMap = new ModelMap();
        String flag = request.getParameter("flag");
        modelMap.put("flag",flag);
        return new ModelAndView("/eland/fm/fm002/fm002_pay_code", modelMap);
    }

}