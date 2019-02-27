import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class pj007 extends GroovyController {

    /* 初始化楼房信息显示
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */

    public ModelAndView initlFHs(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("fromOp", request.getParameter("fromOp"));
        // 获取区域户型信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", request.getParameter("regId"));
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "prjCd", request.getParameter("prjCd"));
        // 区域类型
        reqData.setStrValue(rootNodePath + "regUseType", request.getParameter("regUseType"));
        // 调用服务获取项目户型信息
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryRegHx", svcRequest);
        Map<String, String> hxMap = null;
        if (svcResponse.isSuccess()) {
            hxMap = svcResponse.getRspData().getValue("SvcCont.listData");
        }
        if (hxMap == null) {
            hxMap = new HashMap<String, String>();
        }
        modelMap.put("hxMap", hxMap);
        // 返回处理结果
        return new ModelAndView("/eland/pj/pj007/pj0070201", modelMap);
    }

    /* 初始化行信息设置
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */

    public ModelAndView initRowSet(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("floorIdx", request.getParameter("floorIdx"));
        modelMap.put("floorNm", request.getParameter("floorNm"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        return new ModelAndView("/eland/pj/pj007/pj0070202", modelMap);
    }

    /* 初始化列信息设置
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */

    public ModelAndView initColSet(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("unitIdx", request.getParameter("unitIdx"));
        modelMap.put("doorIdx", request.getParameter("doorIdx"));
        modelMap.put("hsHx", request.getParameter("hsHx"));
        modelMap.put("doorNm", request.getParameter("doorNm"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        // 获取区域户型信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", request.getParameter("regId"));
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "prjCd", request.getParameter("prjCd"));
        // 区域类型
        reqData.setStrValue(rootNodePath + "regUseType", request.getParameter("regUseType"));
        // 调用服务获取项目户型信息
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryRegHx", svcRequest);
        Map<String, String> hxMap = null;
        if (svcResponse.isSuccess()) {
            hxMap = svcResponse.getRspData().getValue("SvcCont.listData");
        }
        if (hxMap == null) {
            hxMap = new HashMap<String, String>();
        }
        modelMap.put("hxMap", hxMap);
        modelMap.put("regUseType", request.getParameter("regUseType"));
        return new ModelAndView("/eland/pj/pj007/pj0070203", modelMap);
    }

}
