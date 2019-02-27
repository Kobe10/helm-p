import com.shfb.oframe.core.util.cache.config.CacheManagerInfo
import com.shfb.oframe.core.util.common.NumberUtil
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

class sys009 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys009/sys00901", modelMap)
    }

    /**
     * 查询缓存项
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView findCache(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Cache.cacheTypeName", requestUtil.getStrParam("cacheTypeName"));
        reqData.setStrValue("SvcCont.Cache.cacheEngineType", requestUtil.getStrParam("cacheEngineType"));
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("cacheService", "queryAllCache", svcRequest);
        // 返回处理结果
        List<CacheManagerInfo.CacheItems.CacheItem> resultList = null;
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            resultList = rspData.getValue("SvcCont.Cache.resultList");
            modelMap.put("resultList", resultList);
            return new ModelAndView("/oframe/sysmg/sys009/sys009", modelMap);
        }
    }

    /**
     * 清空缓存项
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView removeCache(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Cache.cacheTypeName", requestUtil.getStrParam("cacheTypeName"));
        reqData.setStrValue("SvcCont.Cache.cacheSubKey", requestUtil.getStrParam("cacheSubKey"));
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("cacheService", "deleteAllCache", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 清空 所有缓存项 session除外
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView removeAllCacheExpSession(HttpServletRequest request, HttpServletResponse response) {
        String cacheTypeNameStr = request.getParameter("cacheTypeName");
        String[] cacheTypeNames = cacheTypeNameStr.split(",");
        SvcResponse svcResponse = null;
        for (int i = 0; i < cacheTypeNames.length; i++) {
            String temp = cacheTypeNames[i];
            if (StringUtil.isEmptyOrNull(temp) || StringUtil.isEqual("SESSION_INFO", temp)) {
                continue;
            }
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean reqData = new XmlBean();
            reqData.setStrValue("SvcCont.Cache.cacheTypeName", cacheTypeNames[i]);
            svcRequest.setReqData(reqData);
            svcResponse = callService("cacheService", "deleteAllCache", svcRequest);
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
}
