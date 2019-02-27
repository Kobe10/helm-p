import com.shfb.oframe.core.util.common.DateUtil
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
 * 外聘单位管理
 * Created by Administrator on 2014/6/23 0023.
 */
class pj016 extends GroovyController {

    /**
     * 删除外聘单位
     * @param request
     * @param response
     * @return
     */
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 解析请求参数
        String extCmpId = request.getParameter("extCmpId");
        String prjCd = request.getParameter("prjCd");
        if (StringUtil.isNotEmptyOrNull(extCmpId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CmpExtCmp");
            opData.setStrValue("OpData.entityKey", extCmpId);
            svcRequest.addOp("DELETE_ENTITY_INFO", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            ResponseUtil.printAjax(response, """{success:"${svcResponse.isSuccess()}",errMsg:"${svcResponse.getErrMsg()}"}""");
        }
    }

/**
 * 显示外聘单位信息
 * @param request
 * @param response
 * @return
 */
    public ModelAndView initUpdate(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 解析请求参数
        String extCmpId = request.getParameter("extCmpId");
        String prjCd = request.getParameter("prjCd");
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", prjCd);
        if (StringUtil.isNotEmptyOrNull(extCmpId)) {
            opData.setStrValue("OpData.entityName", "CmpExtCmp");
            opData.setStrValue("OpData.entityKey", extCmpId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if(svcResponse.isSuccess()){
                XmlBean cmpExtCmpinfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.CmpExtCmp");
                modelMap.put("info", cmpExtCmpinfo.getRootNode());
            }

            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "PrjExtCmp");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "extCmpId");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", extCmpId);
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            opData.setStrValue("OpData.Conditions.Condition[1].fieldName", "prjCd");
            opData.setStrValue("OpData.Conditions.Condition[1].fieldValue", prjCd);
            opData.setStrValue("OpData.Conditions.Condition[1].operation", "=");
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "extCmpId");
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "prjExtCmpId");
            opData.setStrValue("OpData.ResultFields.fieldName[2]", "startDate");
            opData.setStrValue("OpData.ResultFields.fieldName[3]", "endDate");
            opData.setStrValue("OpData.ResultFields.fieldName[4]", "extNote");
            svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
            SvcResponse svcResp = query(svcRequest);
            if(svcResp.isSuccess()){
                XmlBean prjExtCmpinfo = svcResp.getFirstOpRsp("QUERY_ENTITY_CMPT");
                if(prjExtCmpinfo != null){
                    modelMap.put("subInfo", prjExtCmpinfo.getBeanByPath("Operation.OpResult.PageData.Row[0]").getRootNode());
                }
            }
        }
        return new ModelAndView("/eland/pj/pj016/pj01602", modelMap);
    }

/**
 * 新增外聘单位信息
 * @param request
 * @param response
 * @return
 */
    public void save(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 解析请求参数
        String extCmpId = request.getParameter("extCmpId");
        String prjExtCmpId = request.getParameter("prjExtCmpId");
        String extCmpName = request.getParameter("extCmpName");
        String extCmpDesc = request.getParameter("extCmpDesc");
        String prjCd = request.getParameter("prjCd");
        String startDate = request.getParameter("startDate");
        String extNote = request.getParameter("extNote");
        String endDate = request.getParameter("endDate");

        SvcResponse svcResponse = null;
        ModelMap modelMap = new ModelMap();
        if (StringUtil.isNotEmptyOrNull(extCmpId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CmpExtCmp");
            opData.setStrValue("OpData.EntityData.extCmpId", extCmpId);
            opData.setStrValue("OpData.EntityData.extCmpName", extCmpName);
            opData.setStrValue("OpData.EntityData.extCmpDesc", extCmpDesc);
            svcRequest.addOp("SAVE_ENTITY", opData);

            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "PrjExtCmp");
            opData.setStrValue("OpData.EntityData.prjExtCmpId", prjExtCmpId);
            if(StringUtil.isNotEmptyOrNull(startDate)){
                opData.setStrValue("OpData.EntityData.startDate", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(startDate)));
            }
            if(StringUtil.isNotEmptyOrNull(endDate)){
                opData.setStrValue("OpData.EntityData.endDate", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(endDate)));
            }
            opData.setStrValue("OpData.EntityData.extNote", extNote);
            opData.setStrValue("OpData.EntityData.extCmpId", extCmpId);
            svcRequest.addOp("SAVE_ENTITY", opData);
            svcResponse = transaction(svcRequest);
            ResponseUtil.printAjax(response, """{success:"${svcResponse.isSuccess()}",errMsg:"${svcResponse.getErrMsg()}"}""");
        } else {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CmpExtCmp");
            opData.setStrValue("OpData.EntityData.extCmpId", "\${extCmpId}");
            opData.setStrValue("OpData.EntityData.extCmpName", extCmpName);
            opData.setStrValue("OpData.EntityData.extCmpDesc", extCmpDesc);
            opData.setStrValue("OpData.EntityData.createStaff", svcRequest.getReqStaff());
            opData.setStrValue("OpData.EntityData.createTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            svcRequest.addOp("SAVE_ENTITY", opData);

            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "PrjExtCmp");
            opData.setStrValue("OpData.EntityData.prjExtCmpId", "\${prjExtCmpId}");
            opData.setStrValue("OpData.EntityData.prjCd", prjCd);
            if(StringUtil.isNotEmptyOrNull(startDate)){
                opData.setStrValue("OpData.EntityData.startDate", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(startDate)));
            }
            if(StringUtil.isNotEmptyOrNull(endDate)){
                opData.setStrValue("OpData.EntityData.endDate", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(endDate)));
            }
            opData.setStrValue("OpData.EntityData.extNote", extNote);
            opData.setStrValue("OpData.EntityData.createDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            opData.setStrValue("OpData.EntityData.statusCd", "1");
            opData.setStrValue("OpData.EntityData.statusDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            opData.setStrValue("OpData.EntityData.recentPayDate", DateUtil.toStringYmd(DateUtil.getSysDate()));
            opData.setStrValue("OpData.EntityData.createStf", svcRequest.getReqStaff());
            opData.setStrValue("OpData.EntityData.extCmpId", "\${extCmpId}");
            svcRequest.addOp("SAVE_ENTITY", opData);
            svcResponse = transaction(svcRequest);
            ResponseUtil.printAjax(response, """{success:"${svcResponse.isSuccess()}",errMsg:"${svcResponse.getErrMsg()}"}""");
        }
    }
}