import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class page extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void data(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // SQL信息
        reqData.setStrValue("SvcCont.QuerySvc.subSvcName", requestUtil.getStrParam("subSvcName"));
        int i = 0;
        for (Map.Entry tempItem : paramMap) {
            reqData.setStrValue("SvcCont.QueryInfo.Param[${i}].name", tempItem.getKey());
            reqData.setStrValue("SvcCont.QueryInfo.Param[${i}].value", tempItem.getValue());
            i++;
        }
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("autoCmpltService", "queryComplateData", svcRequest);
        // 添加分页查询数据参数
        if (svcResponse.isSuccess()) {
            JSONArray json = JSONArray.fromObject(svcResponse.getRspData().getValue("SvcCont.QueryData"));
            ResponseUtil.print(response, json.toString());
        }
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void dataWithRht(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // SQL信息
        reqData.setStrValue("SvcCont.QuerySvc.subSvcName", requestUtil.getStrParam("subSvcName"));
        int i = 0;
        for (Map.Entry tempItem : paramMap) {
            reqData.setStrValue("SvcCont.QueryInfo.Param[${i}].name", tempItem.getKey());
            reqData.setStrValue("SvcCont.QueryInfo.Param[${i}].value", tempItem.getValue());
            i++;
        }
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("autoCmpltService", "queryComplateData", svcRequest);
        // 添加分页查询数据参数
        if (svcResponse.isSuccess()) {
            JSONArray json = JSONArray.fromObject(svcResponse.getRspData().getValue("SvcCont.QueryData"));
            ResponseUtil.print(response, json.toString());
        }
    }
}
