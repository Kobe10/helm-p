import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys011 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String staffCode = svcRequest.getReqStaffCd();

        //结束时间 2014-9-10 15:04:25
        String endTimeStr = DateUtil.getCurrMonthLastDay("yyyy-MM-dd") + " 00:00:00";
        Date endTime = DateUtil.toDateYmdHmsWthH(endTimeStr);
        //开始时间
        String startTimeStr = DateUtil.toStringYmdHmsWthH(endTime).substring(0, 7) + "-01 00:00:00";
        Date startTime = DateUtil.toDateYmdHmsWthH(startTimeStr);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        modelMap.put("staffCode", staffCode);
        modelMap.put("defultTime1", startTime);
        modelMap.put("defultTime2", endTime);
        return new ModelAndView("/oframe/sysmg/sys011/sys011", modelMap);
    }

    /**
     * 初始化 权限树
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initRhtTree(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys011/sys011_rht_tree", modelMap)
    }

    /**
     * 日志详情
     * @param request
     * @param response
     * @return
     */
    public ModelAndView logDetail(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();

        String transactionId = request.getParameter("transactionId");
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "TransactionLogInfo");
        reqData.setStrValue("OpData.entityKey", transactionId);
        reqData.setStrValue("OpData.ResultField.fieldName[0]", "transactionId");
        reqData.setStrValue("OpData.ResultField.fieldName[1]", "opStaffId");
        reqData.setStrValue("OpData.ResultField.fieldName[2]", "opService");
        reqData.setStrValue("OpData.ResultField.fieldName[3]", "opStartTime");
        reqData.setStrValue("OpData.ResultField.fieldName[4]", "opMessage");
        reqData.setStrValue("OpData.ResultField.fieldName[5]", "opCode");
        reqData.setStrValue("OpData.ResultField.fieldName[6]", "fromIp");
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean operBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
            //申明容器存储 数据推送前端页面
            List operationList = new ArrayList();
            if(operBean != null){
                for (int r = 0; r < 1; r++) {
                    String opMess = operBean.getStrValue("OpResult.opMessage");
                    //判断 日志表存储 blob 数据是否是 报文.
                    if (StringUtil.isNotEmptyOrNull(opMess) && opMess.startsWith("<")) {
                        //获取修改的报文 组织成xmlBean 推送前端展示
                        XmlBean operaBean = new XmlBean(opMess);

                        int chEntityNum = operaBean.getListNum("Operation.ChangedEntities.ChangedEntity");
                        List chEntityList = new ArrayList();
                        for (int j = 0; j < chEntityNum; j++) {//operation 下有多少 chEntitys
                            XmlBean chEntity = operaBean.getBeanByPath("Operation.ChangedEntities.ChangedEntity[${j}]");

                            int chAttrNum = chEntity.getListNum("ChangedEntity.ChangedAttrs.EntityAttr");
                            List chAttrList = new ArrayList();
                            for (int k = 0; k < chAttrNum; k++) {// chEntitys 下有多少 chAttrs
                                XmlBean chAttr = chEntity.getBeanByPath("ChangedEntity.ChangedAttrs.EntityAttr[${k}]");

                                //将最里层的 changeAttr 存入list推送页面
                                chAttrList.add(chAttr.getRootNode());
                            }
                            //将 chEntity 里单独属性存至 map，推送
                            Map chEntityMap = new HashMap();
                            chEntityMap.put("chAttrList", chAttrList);
                            chEntityMap.put("entityNameCh", chEntity.getStrValue("ChangedEntity.entityNameCh"));
                            chEntityMap.put("entityNameEn", chEntity.getStrValue("ChangedEntity.entityNameEn"));
                            chEntityMap.put("entityId", chEntity.getStrValue("ChangedEntity.entityId"));
                            chEntityMap.put("method", chEntity.getStrValue("ChangedEntity.method"));
                            chEntityList.add(chEntityMap);
                        }
                        Map operationMap = new HashMap();
                        operationMap.put("opStaffId", operBean.getStrValue("OpResult.opStaffId"));
                        operationMap.put("opEndTime", operBean.getStrValue("OpResult.opStartTime"));
                        operationMap.put("opCode", operBean.getStrValue("OpResult.opCode"));
                        operationMap.put("fromIp", operBean.getStrValue("OpResult.fromIp"));
                        operationMap.put("chEntityList", chEntityList);
                        operationList.add(operationMap);
                    }
                }
            }
            modelMap.put("operationList", operationList);
        }

        return new ModelAndView("/oframe/sysmg/sys011/sys011_log_detail", modelMap);
    }
}
