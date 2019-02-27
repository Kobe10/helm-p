import com.shfb.oframe.core.util.common.BigDecimalUtil
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

class pj008 extends GroovyController {

    /**
     * 初始化平房信息显示
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String regId = request.getParameter("regId");
        String regUseType = request.getParameter("regUseType");
        if (StringUtil.isEmptyOrNull(regId)) {
            regId = "";
        }
        modelMap.put("regId", regId);
        modelMap.put("regUseType", regUseType);
        if (!StringUtil.isEmptyOrNull(request.getParameter("regName"))) {
            modelMap.put("regName", java.net.URLDecoder.decode(request.getParameter("regName"), "UTF-8"));
        }
        return new ModelAndView("/eland/pj/pj008/pj008", modelMap);
    }

    /**
     * 初始化平房信息显示
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 页面展示推送
        XmlBean hxInfo = null;
        // 获取入参查询户型数据
        RequestUtil requestUtil = new RequestUtil(request);
        String regId = requestUtil.getStrParam("regId");
        String hxCd = requestUtil.getStrParam("hxCd");
        String regUseType = requestUtil.getStrParam("regUseType");
        if (StringUtil.isNotEmptyOrNull(regId) && StringUtil.isNotEmptyOrNull(hxCd)) {
            //  查看区域
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            //
            XmlBean reqData = new XmlBean();
            String rootNodePath = "OpData.";
            // 区域编码
            reqData.setStrValue(rootNodePath + "regId", regId);
            //  项目编码，必须
            reqData.setStrValue(rootNodePath + "hxCd", hxCd);
            // 区域类型
            reqData.setStrValue(rootNodePath + "regUseType", regUseType);

            // 调用服务
            svcRequest.addOp("QUERY_HS_HX_INFO", reqData);
            SvcResponse svcResponse = query(svcRequest);
            // 获取输出结果
            if (svcResponse.isSuccess()) {
                hxInfo = svcResponse.getFirstOpRsp("QUERY_HS_HX_INFO").getBeanByPath("Operation.OpResult.HsHxs.HsHx[0]");
            }
        }
        // 户型不存在
        if (hxInfo == null) {
            // 初始化户型信息
            hxInfo = new XmlBean();
            hxInfo.setStrValue("HsHx.regId", regId);
            hxInfo.setStrValue("HsHx.hxCd", hxCd);
            hxInfo.setStrValue("HsHx.hxBldSize", "0.00");
            hxInfo.setStrValue("HsHx.hxUseSize", "0.00");
            hxInfo.setStrValue("HsHx.hxSalePrice", "0.00");
        }
        // 推送户型根节点
        modelMap.put("nodeInfo", hxInfo.getRootNode());
        // 计算总房价
        double hxBldSize = hxInfo.getDoubleValue("HsHx.hxBldSize");
        double hxSalePrice = hxInfo.getDoubleValue("HsHx.hxSalePrice");
        modelMap.put("totalPrice", BigDecimalUtil.whetherRound(hxBldSize * hxSalePrice, 2));
        modelMap.put("regUseType", regUseType);
        modelMap.put("regId", regId);
        modelMap.put("prjCd", requestUtil.getStrParam("prjCd"));
        // 现实详细信息界面
        return new ModelAndView("/eland/pj/pj008/pj00801", modelMap);
    }

    /**
     * 初始化平房信息显示
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        //  查看区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        String rootNodePath = "OpData.hsHxs.hshx[0].";
        // 户型编码
        String hxId = requestUtil.getStrParam("hxId");
        if (StringUtil.isEmptyOrNull(hxId)) {
            hxId = "\${1}"
        }
        reqData.setStrValue(rootNodePath + "hxId", hxId);
        // 区域用途
        reqData.setStrValue(rootNodePath + "regUseType", requestUtil.getStrParam("regUseType"));
        // 区域编码
        reqData.setStrValue(rootNodePath + "hxCd", requestUtil.getStrParam("hxCd"));
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "prjCd", requestUtil.getStrParam("prjCd"));
        // 区域类型
        reqData.setStrValue(rootNodePath + "regId", requestUtil.getStrParam("regId"));
        // 户型名称
        reqData.setStrValue(rootNodePath + "hxName", requestUtil.getStrParam("hxName"));
        // 户型描述
        reqData.setStrValue(rootNodePath + "hxDesc", requestUtil.getStrParam("hxDesc"));
        // 户型朝向
        reqData.setStrValue(rootNodePath + "hxDt", requestUtil.getStrParam("hxDt"));
        // 户型建筑面积
        reqData.setStrValue(rootNodePath + "hxBldSize", requestUtil.getStrParam("hxBldSize"));
        // 户型使用面积
        reqData.setStrValue(rootNodePath + "hxUseSize", requestUtil.getStrParam("hxUseSize"));
        // 户型销售单价
        reqData.setStrValue(rootNodePath + "hxSalePrice", requestUtil.getStrParam("hxSalePrice"));
        // 户型图片位置
        reqData.setStrValue(rootNodePath + "hxImgPath", requestUtil.getStrParam("hxImgPath"));
        // 调用服务
        svcRequest.addOp("SAVE_HS_HX_INFO", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化平房信息显示
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);

        //  查看区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjHsHx.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "prjCd", requestUtil.getStrParam("prjCd"));
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "hxId", requestUtil.getStrParam("hxId"));

        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("projectService", "deleteProjectHxInfo", svcRequest);

        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }


}
