import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * User: 移动端配置界面
 * Date: 2017/9/24 0008 18:36
 * Copyright(c) 北京四海富博计算机服务有限公司
 */

class mob004 extends GroovyController {

    /**
     * 主KEY
     */
    private static final String APP_CONF = "APP_CONF";

    /**
     * 首页轮播图
     */
    private static final String APP_TOP_IMG = "APP_TOP_IMG";


    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 输出内容
        List<Map<String, String>> webPicList = new ArrayList<Map<String, String>>();
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.SysCfgs.SysCfg[0].itemCd", "APP_CONF");
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 读取配置数据
            XmlBean resData = svcResponse.getRspData();
            XmlBean sysCfgBean = resData.getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            int valueCount = sysCfgBean.getListNum("SysCfg.Values.Value");
            for (int i = 0; i < valueCount; i++) {
                String nodePath = "SysCfg.Values.Value[${i}].";
                String valueCd = sysCfgBean.getStrValue(nodePath + "valueCd")
                String notes = sysCfgBean.getStrValue(nodePath + "notes")
                if (valueCd == APP_TOP_IMG) {
                    if (StringUtil.isNotEmptyOrNull(notes)) {
                        modelMap.put("appPicList", JSONArray.fromObject(notes));
                    }
                    break;
                }
            }
        }
        // 输出内容设置
        return new ModelAndView("/mobile/mob004/mob004", modelMap)
    }


    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        String key = request.getParameter("key");
        String value = request.getParameter("value");
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.SysCfgs.SysCfg[0].itemCd", APP_CONF);
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 请求参数
            XmlBean newReqData = new XmlBean();
            // 读取配置数据
            XmlBean resData = svcResponse.getRspData();
            XmlBean sysCfgBean = resData.getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");

            int valueCount = sysCfgBean.getListNum("SysCfg.Values.Value");
            boolean found = false;
            for (int i = 0; i < valueCount; i++) {
                String nodePath = "SysCfg.Values.Value[${i}].";
                String valueCd = sysCfgBean.getStrValue(nodePath + "valueCd");
                if (valueCd == key) {
                    sysCfgBean.setStrValue(nodePath + "notes", value);
                    found = true;
                    break;
                }
            }
            if (found) {
                // 请求调用服务
                newReqData.setBeanByPath("SvcCont.SysCfgs", sysCfgBean);
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.setReqData(newReqData);
                // 调用服务
                svcResponse = callService("sysCfgService", "saveSysCfgData", svcRequest);
                // 返回处理结果
                ResponseUtil.printSvcResponse(response, svcResponse, "");
            } else {
                String jsonStr = "{\"success\": " + false + ", \"errMsg\": \"配置不存在!\"}";
                ResponseUtil.printAjax(response, jsonStr);
                return;
            }
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
}