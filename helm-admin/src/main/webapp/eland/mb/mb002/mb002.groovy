import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 公告信息展示面板
 */
class mb002 extends GroovyController {

    /**
     * 初始化项目公告面板
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // 定义到处的数据大小
        reqData.setValue("SvcCont.PageInfo.currentPage", "1");
        reqData.setValue("SvcCont.PageInfo.pageSize", "5");
        // SQL信息
        reqData.setStrValue("SvcCont.QuerySvc.subSvcName", "pt001");
        // 排序信息
        reqData.setValue("SvcCont.SortInfo.sortColumn", "publish_date");
        reqData.setValue("SvcCont.SortInfo.sortOrder", "desc");
        // SQL信息{
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].name", "prjCd");
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].value", request.getParameter("prjCd"));
        // 发布状态
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].name", "noticeStatus");
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].value", "2");

        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("pageService", "queryPageData", svcRequest);
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            // 响应结果
            XmlBean rspData = svcResponse.getRspData();
            // 获取查询结果
            List<Map<String, Object>> noticeList = rspData.getValue("SvcCont.PageData");
            modelMap.put("noticeList", noticeList);
        }
        // 返回处理结果
        return new ModelAndView("/eland/mb/mb002/mb002", modelMap);
    }
}
