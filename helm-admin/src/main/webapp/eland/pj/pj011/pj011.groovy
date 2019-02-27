import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 项目公告 后台管理 模块
 */
class pj011 extends GroovyController {

    /**
     * 初始化项目公告信息
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("noticeType", request.getParameter("noticeType"));
        return new ModelAndView("/eland/pj/pj011/pj011", modelMap);
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
        String method = request.getParameter("method");
        String defaultNoticeType = request.getParameter("noticeType");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //定义 公告信息 以及公告图片的载体
        XmlBean prjNoticeBean = null;
        List noticePics = new ArrayList();
        //判断请求是增加 还是查看or编辑
        if (!"add".equals(method)) {
            //查看or编辑  就先查询然后展示。
            XmlBean reqData = new XmlBean();
            String nodePath = "SvcCont.PrjNotice.";
            reqData.setValue(nodePath + "noticeId", request.getParameter("noticeId"));
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("prjNoticeService", "queryPrjNotice", svcRequest);
            //查询公告成功
            if (svcResponse.isSuccess()) {
                XmlBean rspData = svcResponse.getRspData();
                // 获取查询结果
                prjNoticeBean = rspData.getBeanByPath("SvcCont.PrjNotice");
                int nPicCount = rspData.getListNum("SvcCont.NoticePics.noticePic");
                XmlBean prjNoticePic = null;
                for (int i = 0; i < nPicCount; i++) {
                    Map prjNoticePicMap = new HashMap();
                    String path = "SvcCont.NoticePics.noticePic[${i}]";
                    prjNoticePic = rspData.getBeanByPath(path);
                    prjNoticePicMap.put("noticePicId", prjNoticePic.getStrValue("noticePic.noticePicId"));
                    prjNoticePicMap.put("docId", prjNoticePic.getStrValue("noticePic.docId"));
                    prjNoticePicMap.put("picDesc", prjNoticePic.getStrValue("noticePic.picDesc"));
                    noticePics.add(prjNoticePicMap);
                }
            }

            //发布日期单独取出展示
            String publishDate = prjNoticeBean.getStrValue("PrjNotice.publishDate");
            if (StringUtil.isNotEmptyOrNull(publishDate)) {
                modelMap.put("publishDate", DateUtil.toDateYmdHms(publishDate));
            }

        } else {
            //新增的直接带空数据打开空页面
            prjNoticeBean = new XmlBean();
            prjNoticeBean.setStrValue("PrjNotice.prjCd", request.getParameter("prjCd"));

        }
        modelMap.put("prjNotice", prjNoticeBean.getRootNode());
        modelMap.put("noticePics", noticePics);
        //传递默认公告类型。
        modelMap.put("defaultNoticeType", defaultNoticeType);
        modelMap.put("method", method);
        return new ModelAndView("/eland/pj/pj011/pj01101", modelMap);
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
        return new ModelAndView("/eland/pj/pj011/pj011_view_iframe", modelMap);
    }

    /**
     * 项目公告主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求处理
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //定义 公告信息 以及公告图片的载体
        XmlBean prjNoticeBean = null;
        List noticePics = new ArrayList();
        //判断请求是增加 还是查看or编辑
        //查看or编辑  就先查询然后展示。
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.PrjNotice.";
        reqData.setValue(nodePath + "noticeId", request.getParameter("noticeId"));
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjNoticeService", "queryPrjNotice", svcRequest);
        //查询公告成功
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            // 获取查询结果
            prjNoticeBean = rspData.getBeanByPath("SvcCont.PrjNotice");
            int nPicCount = rspData.getListNum("SvcCont.NoticePics.noticePic");
            XmlBean prjNoticePic = null;
            for (int i = 0; i < nPicCount; i++) {
                Map prjNoticePicMap = new HashMap();
                String path = "SvcCont.NoticePics.noticePic[${i}]";
                prjNoticePic = rspData.getBeanByPath(path);
                prjNoticePicMap.put("noticePicId", prjNoticePic.getStrValue("noticePic.noticePicId"));
                prjNoticePicMap.put("docId", prjNoticePic.getStrValue("noticePic.docId"));
                prjNoticePicMap.put("picDesc", prjNoticePic.getStrValue("noticePic.picDesc"));
                noticePics.add(prjNoticePicMap);
            }
            //发布日期单独取出展示
            String publishDate = prjNoticeBean.getStrValue("PrjNotice.publishDate");
            if (StringUtil.isNotEmptyOrNull(publishDate)) {
                modelMap.put("publishDate", DateUtil.toDateYmdHms(publishDate));
            }
            modelMap.put("prjNotice", prjNoticeBean.getRootNode());
        }
        return new ModelAndView("/eland/pj/pj011/pj011_view", modelMap);
    }

    public void save(HttpServletRequest request, HttpServletResponse response) {
        //传递命令
        String method = request.getParameter("method");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.PrjNotice.";
        reqData.setStrValue(nodePath + "noticeId", request.getParameter("noticeId"));
        reqData.setStrValue(nodePath + "prjCd", request.getParameter("prjCd"));
        reqData.setStrValue(nodePath + "noticeTitle", request.getParameter("noticeTitle"));
        reqData.setStrValue(nodePath + "noticeStatus", request.getParameter("noticeStatus"));
        reqData.setStrValue(nodePath + "noticeType", request.getParameter("noticeType"));

        String publishDate = request.getParameter("publishDate");
        if (StringUtil.isNotEmptyOrNull(publishDate)) {
            reqData.setStrValue(nodePath + "publishDate", DateUtil.toDateYmdWthH(publishDate));
        }
        reqData.setStrValue(nodePath + "noticeSummary", request.getParameter("noticeSummary"));
        reqData.setStrValue(nodePath + "noticeContext", request.getParameter("noticeContext"));

//      插入公告封面图片
        String noticePicCover = request.getParameter("noticePicCover");
        reqData.setStrValue(nodePath + "coverPicId", noticePicCover);

//       获取前台图片写入报文
        String[] docIds = request.getParameterValues("docId");
        int m = 0;
        for (int i = 0; i < docIds.length; i++) {
            if (StringUtil.isEmptyOrNull(docIds[i])) {
                continue;
            }
            nodePath = "SvcCont.NoticePics.noticePic[${m++}].";
            reqData.setStrValue(nodePath + "docId", docIds[i]);
            reqData.setStrValue(nodePath + "noticePicId", request.getParameter("noticePicId" + docIds[i]));
            reqData.setStrValue(nodePath + "picDesc", request.getParameter("noticePicDesc" + docIds[i]));
        }

        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("prjNoticeService", "savePrjNotice", svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    public void deleteNotice(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        String[] noticeIds = request.getParameter("noticeIds").split(",");
        String[] prjCds = request.getParameter("prjCds").split(",");
        for (int i = 0; i < noticeIds.length; i++) {
            String nodePath = "SvcCont.PrjNotice[" + i + "].";
            reqData.setStrValue(nodePath + "noticeId", noticeIds[i]);
            reqData.setStrValue(nodePath + "prjCd", prjCds[i]);
        }
        //
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("prjNoticeService", "deletePrjNotice", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 网站配置 首页视频单独配置   noticeType=0
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView inInfo(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求处理
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //定义 公告信息 以及公告图片的载体
        XmlBean prjNoticeBean = null;
        List noticePics = new ArrayList();
        //查看or编辑  就先查询然后展示。
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.PrjNotice.";
        reqData.setValue(nodePath + "noticeId", request.getParameter("noticeId"));
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjNoticeService", "queryPrjNotice", svcRequest);
        //查询公告成功
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            // 获取查询结果
            prjNoticeBean = rspData.getBeanByPath("SvcCont.PrjNotice");
            int nPicCount = rspData.getListNum("SvcCont.NoticePics.noticePic");
            XmlBean prjNoticePic = null;
            for (int i = 0; i < nPicCount; i++) {
                Map prjNoticePicMap = new HashMap();
                String path = "SvcCont.NoticePics.noticePic[${i}]";
                prjNoticePic = rspData.getBeanByPath(path);
                prjNoticePicMap.put("noticePicId", prjNoticePic.getStrValue("noticePic.noticePicId"));
                prjNoticePicMap.put("docId", prjNoticePic.getStrValue("noticePic.docId"));
                prjNoticePicMap.put("picDesc", prjNoticePic.getStrValue("noticePic.picDesc"));
                noticePics.add(prjNoticePicMap);
            }
            //发布日期单独取出展示
            String publishDate = prjNoticeBean.getStrValue("PrjNotice.publishDate");
            if (StringUtil.isNotEmptyOrNull(publishDate)) {
                modelMap.put("publishDate", DateUtil.toDateYmdHms(publishDate));
            }
            modelMap.put("prjNotice", prjNoticeBean.getRootNode());
            modelMap.put("noticePics", noticePics);
        }
        return new ModelAndView("/eland/pj/pj011/pj01102", modelMap);
    }

}

