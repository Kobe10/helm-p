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
 * 网站问题解答
 * Created by Administrator on 2014/6/23 0023.
 */
class pj012 extends GroovyController {

    /**
     * 初始化项目流程信息
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj012/pj012", modelMap);
    }

    /**
     * 分页显示数据
     * @param request
     * @param response
     * @return
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 请求处理
        String method = request.getParameter("method");
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean questionBean = null;
        if (!StringUtil.isEmptyOrNull(request.getParameter("qId"))) {
            XmlBean reqData = new XmlBean();
            String nodePrePath = "SvcCont.PrjQAndA.";
            reqData.setValue(nodePrePath + "qId", request.getParameter("qId"));
            //请求信息
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("prjQAndAService", "queryPrjQAndA", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean rspData = svcResponse.getRspData();
                // 获取查询结果
                questionBean = rspData.getBeanByPath("SvcCont.PrjQAndA");
            }
            String qTime = questionBean.getStrValue("PrjQAndA.qTime");
            if (StringUtil.isNotEmptyOrNull(qTime)) {
                modelMap.put("qTime", DateUtil.toDateYmdHms(qTime));
            }
            String aTime = questionBean.getStrValue("PrjQAndA.aTime");
            if (StringUtil.isNotEmptyOrNull(aTime)) {
                modelMap.put("aTime", DateUtil.toDateYmdHms(aTime));
            }
        } else {
            questionBean = new XmlBean();
            questionBean.setStrValue("PrjQAndA.prjCd", request.getParameter("prjCd"));
        }
        modelMap.put("prjQAndA", questionBean.getRootNode());
        modelMap.put("method", method);

        return new ModelAndView("/eland/pj/pj012/pj01201", modelMap);
    }

/**
 * 保存信息
 * @param request
 * @param response
 * @return
 */
    public void save(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean qBean = new XmlBean();

        String nodePrePath = "SvcCont.PrjQAndA.";
        qBean.setStrValue(nodePrePath + "qId", request.getParameter("qId"));
        qBean.setStrValue(nodePrePath + "prjCd", request.getParameter("prjCd"));
        qBean.setStrValue(nodePrePath + "qReg", request.getParameter("qReg"));
        qBean.setStrValue(nodePrePath + "qStatus", request.getParameter("qStatus"));
        qBean.setStrValue(nodePrePath + "qAddr", request.getParameter("qAddr"));
        qBean.setStrValue(nodePrePath + "qName", request.getParameter("qName"));
        qBean.setStrValue(nodePrePath + "qTele", request.getParameter("qTele"));
        qBean.setStrValue(nodePrePath + "qText", request.getParameter("qText"));
        String qTime = request.getParameter("qTime");
        if (StringUtil.isNotEmptyOrNull(qTime)) {
            qBean.setStrValue(nodePrePath + "qTime", DateUtil.toDateYmdHmsWthH(qTime));
        }
        qBean.setStrValue(nodePrePath + "aText", request.getParameter("aText"));
        String aTime = request.getParameter("aTime");
        if (StringUtil.isNotEmptyOrNull(aTime)) {
            qBean.setStrValue(nodePrePath + "aTime", DateUtil.toDateYmdHmsWthH(aTime));
        }
        svcRequest.setReqData(qBean);
        SvcResponse svcResponse = callService("prjQAndAService", "savePrjQAndA", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }


    public void deleteQuestion(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();

        String[] qIds = request.getParameter("qIds").split(",");
        String[] prjCds = request.getParameter("prjCds").split(",");
        for (int i = 0; i < qIds.length; i++) {
            String nodePath = "SvcCont.PrjQAndA[" + i + "].";
            reqData.setStrValue(nodePath + "qId", qIds[i]);
            reqData.setStrValue(nodePath + "prjCd", prjCds[i]);
        }
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("prjQAndAService", "deletePrjQAndA", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");


    }
}