import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * User: shfb_wang 
 * Date: 2016/3/8 0008 18:36
 * Copyright(c) 北京四海富博计算机服务有限公司
 */

class mob002 extends GroovyController {

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        String orgId = request.getParameter("orgId");

        // 获取工号可以访问的项目工程
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 查询条件
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.staffCode", svcRequest.getReqStaffCd());
        reqData.setStrValue("SvcCont.orgId", orgId);
        reqData.setStrValue("SvcCont.isShowExp", "1");   // 是否显示无效项目标志。 1 不显示无效项目， 0  显示。
        // 调用服务查询实体全部属性
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "queryStaffProject", svcRequest);
        Map<String, String> prjMap = new LinkedHashMap<String, String>();
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

        if (StringUtil.isEmptyOrNull(prjCd) && prjMap.keySet().size() > 0) {
            prjCd = prjMap.keySet().toArray()[0];
        } else if (StringUtil.isEmptyOrNull(prjCd)) {
            prjCd = "0";
        }

        /**
         * 面板查询请求处理
         */
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        svcRequest.setReqData(reqData);
        svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean portalRhtsBean = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            if (portalRhtsBean != null) {
                List<XmlNode> portalRhts = new ArrayList<XmlNode>();
                int haveRhtPortal = portalRhtsBean.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    boolean isHaveRht = Boolean.parseBoolean(portalRhtsBean.getStrValue("portalRhts.portalRht[${i}].isHaveRht"));
                    if (isHaveRht) {
                        //处理 prjJobCd
                        String temp = portalRhtsBean.getStrValue("portalRhts.portalRht[${i}].navUrl");
                        if (temp.indexOf("?") > 0) {
                            String urlParam = temp.substring(temp.indexOf("?") + 1);
                            portalRhtsBean.getBeanByPath("portalRhts.portalRht[${i}]").setStrValue("portalRht.urlParam", urlParam);
                        }
                        portalRhts.add(portalRhtsBean.getBeanByPath("portalRhts.portalRht[${i}]").getRootNode());
                    }
                }

                modelMap.put("staffPanel", portalRhts);
            }
        }

        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/mobile/mob002/home", modelMap);
    }


    public ModelAndView loadPic(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        RequestUtil requestUtil = new RequestUtil(request);
        modelMap.put("prjJobCd", requestUtil.getStrParam("prjJobCd"));
        modelMap.put("prjJobGroup", requestUtil.getStrParam("prjJobGroup"));
        modelMap.put("chartType", requestUtil.getStrParam("chartType"));
        modelMap.put("initParams", JSONObject.fromObject(requestUtil.getRequestMap(request)).toString());
        if (StringUtil.isNotEmptyOrNull(request.getParameter("title"))) {
            modelMap.put("title", java.net.URLDecoder.decode(request.getParameter("title"), "utf-8"));
        }
        return new ModelAndView("/mobile/mob002/mob002_bar_pie", modelMap);
    }


    public ModelAndView load2Pic(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        modelMap.put("prjJobCd", requestUtil.getStrParam("prjJobCd"));
        modelMap.put("prjJobGroup", requestUtil.getStrParam("prjJobGroup"));
        modelMap.put("chartType", requestUtil.getStrParam("chartType"));
        modelMap.put("initParams", JSONObject.fromObject(requestUtil.getRequestMap(request)).toString());
        if (StringUtil.isNotEmptyOrNull(request.getParameter("title"))) {
            modelMap.put("title", java.net.URLDecoder.decode(request.getParameter("title"), "utf-8"));
        }
        return new ModelAndView("/mobile/mob002/mob002_bar_line", modelMap);
    }


}