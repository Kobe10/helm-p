import com.shfb.oframe.core.util.common.DateUtil
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

class hm001 extends GroovyController {

    /**
     * 组件的名称
     */
    private static final String ENTITY_NAME = "HelmNotice";

    /**
     * 初始化
     * @param request
     * @param response
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/helm/hm001/hm001", modelMap)
    }

    /**
     * 项目公告主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求处理
        String noticeId = request.getParameter("noticeId");
        //定义 公告信息 以及公告图片的载体
        XmlBean helmNotice = null;
        //判断请求是增加 还是查看or编辑
        if (StringUtil.isNotEmptyOrNull(noticeId)) {
            // 调用服务后去分类定义信息
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", ENTITY_NAME);
            opData.setStrValue("OpData.entityKey", noticeId);
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            //发布日期单独取出展示
            if (svcResponse.success) {
                helmNotice = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                        .getBeanByPath("Operation.OpResult." + ENTITY_NAME)
                String publishDate = helmNotice.getStrValue("HelmNotice.publishDate")
                if (StringUtil.isNotEmptyOrNull(publishDate)) {
                    helmNotice.setStrValue("HelmNotice.publishDate_Name",
                            DateUtil.toStringYmdWthH(DateUtil.toDateYmdHms(publishDate)))
                }
            }
        }
        if (helmNotice == null) {
            // 新增的直接带空数据打开空页面
            helmNotice = new XmlBean();
            helmNotice.setStrValue("HelmNotice.publishOrder", 0)
        }
        modelMap.put("helmNotice", helmNotice.getRootNode());
        return new ModelAndView("/helm/hm001/hm001_info", modelMap);
    }

    /**
     * 保存公告信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String noticeId = request.getParameter("noticeId");
        XmlBean opData = new XmlBean();
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", ENTITY_NAME);
        boolean add = false;
        if (StringUtil.isEmptyOrNull(noticeId)) {
            noticeId = "\${noticeId}"
            add = true
        }
        opData.setStrValue(nodePath + "EntityData.noticeId", noticeId);
        opData.setStrValue(nodePath + "EntityData.noticeType", request.getParameter("noticeType"));
        opData.setStrValue(nodePath + "EntityData.noticeTitle", request.getParameter("noticeTitle"));
        if (add) {
            opData.setStrValue(nodePath + "EntityData.noticeDate", svcRequest.getReqTime());
            opData.setStrValue(nodePath + "EntityData.createStaff", svcRequest.getReqStaff());
        }
        opData.setStrValue(nodePath + "EntityData.publishOrder", request.getParameter("publishOrder"));
        opData.setStrValue(nodePath + "EntityData.publishStatus", request.getParameter("publishStatus"));
        String publishDate = request.getParameter("publishDate")
        if (StringUtil.isNotEmptyOrNull(publishDate)) {
            opData.setStrValue(nodePath + "EntityData.publishDate", DateUtil.toDateYmdWthH(publishDate));
        }
        opData.setStrValue(nodePath + "EntityData.noticeSummary", request.getParameter("noticeSummary"));
        opData.setStrValue(nodePath + "EntityData.noticeContext", request.getParameter("noticeContext"));
        opData.setStrValue(nodePath + "EntityData.coverPicId", request.getParameter("coverPicId"));
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess() && add) {
            noticeId = svcResponse.getFirstOpRsp("SAVE_ENTITY").getStrValue("OpResult.entityKey")
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "\"noticeId\":\"${noticeId}\"");
    }

    /**
     * 重置签约排序
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView sort(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String noticeIds = request.getParameter("noticeIds");
        for (String noticeId : noticeIds.split(",")) {
            if (StringUtil.isEmptyOrNull(noticeId)) {
                continue
            }
            XmlBean opData = new XmlBean();
            String nodePath = "OpData.";
            opData.setStrValue(nodePath + "entityName", ENTITY_NAME);
            opData.setStrValue(nodePath + "EntityData.noticeId", noticeId);
            opData.setStrValue(nodePath + "EntityData.publishOrder", "0");
            svcRequest.addOp("SAVE_ENTITY", opData);
        }
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 删除公告信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String noticeIds = request.getParameter("noticeIds");
        for (String noticeId : noticeIds.split(",")) {
            if (StringUtil.isEmptyOrNull(noticeId)) {
                continue
            }
            XmlBean opData = new XmlBean();
            String nodePath = "OpData.";
            opData.setStrValue(nodePath + "entityName", ENTITY_NAME);
            opData.setStrValue(nodePath + "entityKey", noticeId);
            svcRequest.addOp("DELETE_ENTITY_INFO", opData);
        }
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 项目公告主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView viewMain(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("noticeId", request.getParameter("noticeId"));
        modelMap.put("prjCd", request.getParameter("prjCd"));
        return new ModelAndView("/helm/hm001/hm001_view_iframe", modelMap);
    }

    /**
     * 公告详情获取
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求处理
        String noticeId = request.getParameter("noticeId");
        //定义 公告信息 以及公告图片的载体
        XmlBean helmNotice = null;
        //判断请求是增加 还是查看or编辑
        if (StringUtil.isNotEmptyOrNull(noticeId)) {
            // 调用服务后去分类定义信息
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", ENTITY_NAME);
            opData.setStrValue("OpData.entityKey", noticeId);
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            //发布日期单独取出展示
            if (svcResponse.success) {
                helmNotice = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                        .getBeanByPath("Operation.OpResult." + ENTITY_NAME)
                String publishDate = helmNotice.getStrValue("HelmNotice.publishDate")
                if (StringUtil.isNotEmptyOrNull(publishDate)) {
                    helmNotice.setStrValue("HelmNotice.publishDate_Name",
                            DateUtil.toStringYmdWthH(DateUtil.toDateYmdHms(publishDate)))
                }
            }
        }
        if (helmNotice == null) {
            // 新增的直接带空数据打开空页面
            helmNotice = new XmlBean();
            helmNotice.setStrValue("HelmNotice.publishOrder", 0)
        }
        modelMap.put("helmNotice", helmNotice.getRootNode());
        return new ModelAndView("/helm/hm001/hm001_view", modelMap);
    }
}


