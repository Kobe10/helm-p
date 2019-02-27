/**
 * 房屋结算处理
 */
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

class oh004 extends GroovyController {

    /**
     * 初始化右侧面板 页面
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/oh/oh004/oh004", modelMap);
    }


    /**
     * 初始化统计界面
     * @param request 请求信息
     * @param response 响应结果
     * @return 返回报表初始化界面
     */
    public ModelAndView initRpt(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        // 返回数据
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/oh/oh004/oh004_rpt", modelMap);
    }

    /**
     * 初始化修改页面
     */
    public ModelAndView extDeal(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = requestUtil.getSvcRequest(request);
        String personId = requestUtil.getStrParam("personId");
        String isHkWq = requestUtil.getStrParam("isHkWq");
        String conditionNames = "PersonInfo.familyOwnFlag,PersonInfo.isHkWq,PersonInfo.personId"
        String conditions = "=,=,="
        String conditionValues = "1," + isHkWq + "," + personId;
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",");
        XmlBean opData = new XmlBean();
        int flag = 0;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            opData.setStrValue("OpData.Conditions.Condition[" + flag + "].fieldName", conditionFieldArr[i]);
            opData.setStrValue("OpData.Conditions.Condition[" + flag + "].operation", conditionArr[i]);
            opData.setStrValue("OpData.Conditions.Condition[" + flag++ + "].fieldValue", conditionValueArr[i]);
        }
        svcRequest.addOp("QUERY_WQ_INFO_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_WQ_INFO_CMPT").getBeanByPath("Operation.OpResult");
            if(queryResult != null){
                String prePath = "OpResult.PageData.Row[0].";
                String wqBcMoney = queryResult.getStrValue(prePath+"wqBcMoney");
                String wqNote = queryResult.getStrValue(prePath+"wqNote");
                String personNum = queryResult.getStrValue(prePath+"personNum");
                String personName = queryResult.getStrValue(prePath+"personName");
                String hsCtId = queryResult.getStrValue(prePath+"hsCtId");
                modelMap.put("personName", personName);
                modelMap.put("personId", personId);
                modelMap.put("wqBcMoney", wqBcMoney);
                modelMap.put("wqNote", wqNote);
                modelMap.put("personNum", personNum);
                modelMap.put("hsCtId", hsCtId);
                modelMap.put("isHkWq", isHkWq);
            }
        }
        return new ModelAndView("/eland/oh/oh004/oh004_wqcl", modelMap);
    }

    /**
     * 外迁处理
     */
    public ModelAndView saveWq(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String wqPersonId = request.getParameter("personId");
        String wqBcMoney = request.getParameter("wqBcMoney");
        String wqNote = request.getParameter("wqNote");
        String wqPersonNum = request.getParameter("personNum");
        String hsCtId = request.getParameter("hsCtId");
        String isHkWq = request.getParameter("isHkWq");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.HkWqInfos.HkWqInfo.wqPersonId", wqPersonId);
        opData.setStrValue("OpData.HkWqInfos.HkWqInfo.wqBcMoney", wqBcMoney);
        opData.setStrValue("OpData.HkWqInfos.HkWqInfo.wqNote", wqNote);
        opData.setStrValue("OpData.HkWqInfos.HkWqInfo.wqPersonNum", wqPersonNum);
        opData.setStrValue("OpData.hsCtId", hsCtId);
        opData.setStrValue("OpData.isHkWq", isHkWq);
        svcRequest.addOp("SAVE_WQ_INFO_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

}


