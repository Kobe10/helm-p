import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * 项目概述
 */
class mb004 extends GroovyController {

    /**
     * 初始化项目公告面板
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) { // 返回结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        // 根据项目查询
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "CmpPrj");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "prjCd");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", prjCd);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "prjDesc");
        svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            String prjDesc = rspData.getStrValue("PageData.Row[0].prjDesc");
            modelMap.put("prjDesc", prjDesc);
        }
        return new ModelAndView("/eland/mb/mb004/mb004", modelMap);
    }
}
