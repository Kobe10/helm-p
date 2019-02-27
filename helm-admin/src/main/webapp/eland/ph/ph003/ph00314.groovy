import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
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
 * 居民访谈记录处理
 */
class ph00314 extends GroovyController {
    String RECORD_TYPE = "3";
    /**
     * 初始化访谈信息
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initTalkRecord(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = request.getParameter("prjCd");
        modelMap.put("staffId", svcRequest.getReqStaff());
        SvcResponse svcResponse;
        String hsId = request.getParameter("hsId");
        String staffName = "";
        XmlBean resultBean = new XmlBean();
        // 查询登陆人姓名******************************************************
        String nodePath = "SvcCont.Staff.";
        resultBean.setValue(nodePath + "staffId", svcRequest.getReqStaff());
        svcRequest.setReqData(resultBean);
        svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
            staffName = staffBean.getStrValue("Staff.StaffName");
        }
        // 查询谈话记录信息*******************************************************
        svcRequest = RequestUtil.getSvcRequest(request);
        resultBean = new XmlBean();
        String prePath = "PrjTalkRecord.";
        resultBean.setStrValue(prePath + "relObjId", hsId);
        resultBean.setStrValue(prePath + "prjCd", request.getParameter("prjCd"));
        // 调用服务获取房屋的基本信息
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "HouseInfo");
            opData.setStrValue("OpData.entityKey", hsId);
            opData.setStrValue("OpData.ResultField.fieldName[0]", "hsOwnerPersons");
            opData.setStrValue("OpData.ResultField.fieldName[1]", "ttMainTalk");
            // 调用服务查询数据
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            // 调用服务
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (opResult != null) {
                    String ttMainTalk = opResult.getStrValue("OpResult.ttMainTalk");
                    if (StringUtil.isEmptyOrNull(ttMainTalk)) {
                        ttMainTalk = staffName;
                    }
                    resultBean.setStrValue(prePath + "doRecordPerson", ttMainTalk);
                    resultBean.setStrValue(prePath + "recordToPerson", opResult.getStrValue("OpResult.hsOwnerPersons"));
                    resultBean.setStrValue(prePath + "startTime", DateUtil.toStringYmdHmsWthH(DateUtil.getSysDate()));
                }
            }
        }
        modelMap.put("talkInfo", resultBean.getRootNode());
        // 查询权限信息，是否有查看他人记录的权限*******************************************************
        svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        boolean haveRht;
        // 判断是否有管理日志权限
        reqData.setValue("SvcCont.rightType", "1");
        reqData.setValue("SvcCont.rhtCd", "hsrd_super_rht");
        reqData.setValue("SvcCont.prjCd", prjCd);
        svcRequest.setReqData(reqData);
        svcResponse = SvcUtil.callSvc("staffService", "queryStaffHaveRht", svcRequest);
        if (svcResponse.isSuccess()) {
            haveRht = Boolean.valueOf(svcResponse.getRspData().getStrValue("SvcCont.haveRight"));
        } else {
            haveRht = false;
        }
        // 没有管理权限，则判断是否有查看他人日志权限
        if (!haveRht){
            svcRequest = RequestUtil.getSvcRequest(request);
            reqData = new XmlBean();
            reqData.setValue("SvcCont.rightType", "1");
            reqData.setValue("SvcCont.rhtCd", "hsrd_other_rht");
            reqData.setValue("SvcCont.prjCd", prjCd);
            svcRequest.setReqData(reqData);
            svcResponse = SvcUtil.callSvc("staffService", "queryStaffHaveRht", svcRequest);
            if (svcResponse.isSuccess()) {
                haveRht = Boolean.valueOf(svcResponse.getRspData().getStrValue("SvcCont.haveRight"));
            }
        }
        modelMap.put("haveRht", haveRht);
        return new ModelAndView("/eland/ph/ph003/ph00314", modelMap);
    }

    /**
     * 保存访谈信息
     * @param request
     * @param response
     * @return
     */
    public void saveTalkRecord(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String recordId = request.getParameter("recordId");
        if (StringUtil.isEmptyOrNull(recordId)) {
            recordId = "\${recordId}";
        }
        XmlBean rowData = new XmlBean();
        rowData.setStrValue("OpData.entityName", "RecordInfo");
        rowData.setStrValue("OpData.EntityData.recordId", recordId);
        rowData.setStrValue("OpData.EntityData.recordType", RECORD_TYPE);
        rowData.setStrValue("OpData.EntityData.recordRelId", request.getParameter("recordRelId"));
        rowData.setStrValue("OpData.EntityData.doRecordPerson", request.getParameter("doRecordPerson"));
        rowData.setStrValue("OpData.EntityData.recordToPerson", request.getParameter("recordToPerson"));
        String startTime = request.getParameter("startTime");
        if (StringUtil.isNotEmptyOrNull(startTime)) {
            rowData.setStrValue("OpData.EntityData.startTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdHmsWthH(startTime)));
        } else {
            rowData.setStrValue("OpData.EntityData.startTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        }
        String endTime = request.getParameter("endTime");
        if (StringUtil.isNotEmptyOrNull(endTime)) {
            rowData.setStrValue("OpData.EntityData.endTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdHmsWthH(endTime)));
        } else {
            rowData.setStrValue("OpData.EntityData.endTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        }
        rowData.setStrValue("OpData.EntityData.recordContext", request.getParameter("recordContext"));
        rowData.setStrValue("OpData.EntityData.publishStf", svcRequest.getReqStaff());
        rowData.setStrValue("OpData.EntityData.publishDateTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        svcRequest.addOp("SAVE_ENTITY", rowData);
        SvcResponse svcRsp = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcRsp, "");
    }

    /**
     * 删除访谈记录
     * @param request
     * @param response
     */
    public void deleteRecord(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "RecordInfo");
        opData.setStrValue("OpData.entityKey", request.getParameter("recordId"));
        svcRequest.addOp("DELETE_ENTITY_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 编辑访谈记录
     * @param request
     * @param response
     */
    public ModelAndView initRecord(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        String talkRecordId = request.getParameter("recordId");
        if (StringUtil.isNotEmptyOrNull(talkRecordId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "RecordInfo");
            opData.setStrValue("OpData.entityKey", talkRecordId);
            opData.setStrValue("OpData.ResultField.fieldName[0]", "recordId");
            opData.setStrValue("OpData.ResultField.fieldName[1]", "recordRelId");
            opData.setStrValue("OpData.ResultField.fieldName[2]", "recordType");
            opData.setStrValue("OpData.ResultField.fieldName[3]", "doRecordPerson");
            opData.setStrValue("OpData.ResultField.fieldName[4]", "recordToPerson");
            opData.setStrValue("OpData.ResultField.fieldName[5]", "startTime");
            opData.setStrValue("OpData.ResultField.fieldName[6]", "endTime");
            opData.setStrValue("OpData.ResultField.fieldName[7]", "recordContext");
            opData.setStrValue("OpData.ResultField.fieldName[8]", "publishStf");
            opData.setStrValue("OpData.ResultField.fieldName[9]", "publishDateTime");
            // 调用服务查询数据
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            // 调用服务
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (opResult != null) {
                    String startTime = opResult.getStrValue("OpResult.startTime");
                    if (StringUtil.isNotEmptyOrNull(startTime)) {
                        opResult.setStrValue("OpResult.startTime", DateUtil.toStringYmdHmsWthH(DateUtil.toDateYmdHms(startTime)));
                    }
                    String endTime = opResult.getStrValue("OpResult.endTime");
                    if (StringUtil.isNotEmptyOrNull(endTime)) {
                        opResult.setStrValue("OpResult.endTime", DateUtil.toStringYmdHmsWthH(DateUtil.toDateYmdHms(endTime)));
                    }
                }
                modelMap.put("talkInfo", opResult.getRootNode());
            }
        }

        modelMap.put("method", request.getParameter("method"));
        return new ModelAndView("/eland/ph/ph003/ph00314_edit", modelMap);
    }
}