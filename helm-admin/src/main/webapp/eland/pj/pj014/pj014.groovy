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
class pj014 extends GroovyController {

/**
 * 初始化 外聘单位管理
 * @param request 请求信息流
 * @param response 处理结果流
 * @return 业务数据
 */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj014/pj014", modelMap);
    }

    /**
     * 删除外聘单位
     * @param request
     * @param response
     * @return
     */
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 解析请求参数
        String extCmpId = request.getParameter("extCmpId");
        String prjExtCmpId = request.getParameter("prjExtCmpId");

        ModelMap modelMap = new ModelMap();
        if (StringUtil.isNotEmptyOrNull(extCmpId) && StringUtil.isNotEmptyOrNull(prjExtCmpId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CmpExtCmp");
            opData.setStrValue("OpData.entityKey", extCmpId);
            svcRequest.addOp("DELETE_ENTITY_INFO", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                opData = new XmlBean();
                opData.setStrValue("OpData.entityName", "PrjExtCmp");
                opData.setStrValue("OpData.entityKey", prjExtCmpId);
                svcRequest.addOp("DELETE_ENTITY_INFO", opData);
                svcResponse = transaction(svcRequest);
            }
        }

    }

/**
 * 显示外聘单位信息
 * @param request
 * @param response
 * @return
 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        // 解析请求参数
        String method = request.getParameter("method");
        String extCmpId = request.getParameter("extCmpId");
        String prjExtCmpId = request.getParameter("prjExtCmpId");
        String prjCd = request.getParameter("prjCd");

        ModelMap modelMap = new ModelMap();
        if (StringUtil.isNotEmptyOrNull(extCmpId) && StringUtil.isNotEmptyOrNull(prjExtCmpId)) {
            opData.setStrValue("OpData.entityName", "CmpExtCmp");
            opData.setStrValue("OpData.entityKey", extCmpId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "PrjExtCmp");
            opData.setStrValue("OpData.entityKey", prjExtCmpId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                List<XmlBean> allResult = svcResponse.getAllOpRsp("QUERY_ALL_ENTITY")
                XmlBean CmpExtCmpinfo = allResult.get(0).getBeanByPath("Operation.OpResult.CmpExtCmp");
                XmlNode info = CmpExtCmpinfo.getRootNode();
                modelMap.put("CmpExtCmp", info);

                XmlBean PrjExtCmp = allResult.get(1).getBeanByPath("Operation.OpResult.PrjExtCmp");
                XmlNode prjInfo = PrjExtCmp.getRootNode();
                modelMap.put("PrjExtCmp", prjInfo);
            }
        }
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/pj/pj014/pj01401", modelMap);
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

        boolean addCmp = false;
        if (StringUtil.isEmptyOrNull(extCmpId)) {
            extCmpId = "\${extCmpId}";
            addCmp = true;
        }

        boolean addPrjCmp = false;
        if (StringUtil.isEmptyOrNull(prjExtCmpId)) {
            prjExtCmpId = "\${prjExtCmpId}";
            addPrjCmp = true;
        }

        // 保存公司
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CmpExtCmp");
        opData.setStrValue("OpData.EntityData.extCmpId", extCmpId);
        opData.setStrValue("OpData.EntityData.extCmpName", extCmpName);
        opData.setStrValue("OpData.EntityData.extCmpDesc", extCmpDesc);
        opData.setStrValue("OpData.EntityData.createStaff", svcRequest.getReqStaff());
        if (addCmp) {
            opData.setStrValue("OpData.EntityData.createTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);

        // 保存项目的聘用记录
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "PrjExtCmp");
        opData.setStrValue("OpData.EntityData.prjExtCmpId", prjExtCmpId);
        opData.setStrValue("OpData.EntityData.prjCd", prjCd);
        opData.setStrValue("OpData.EntityData.startDate", startDate);
        opData.setStrValue("OpData.EntityData.endDate", endDate);
        opData.setStrValue("OpData.EntityData.extNote", extNote);
        opData.setStrValue("OpData.EntityData.statusCd", "1");

        if (addPrjCmp) {
            opData.setStrValue("OpData.EntityData.createDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            opData.setStrValue("OpData.EntityData.statusDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            opData.setStrValue("OpData.EntityData.recentPayDate", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }

        opData.setStrValue("OpData.EntityData.createStf", svcRequest.getReqStaff());
        opData.setStrValue("OpData.EntityData.extCmpId", extCmpId);
        svcRequest.addOp("SAVE_ENTITY", opData);

        SvcResponse svcResponse = transaction(svcRequest);

        // 输出返回结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

}