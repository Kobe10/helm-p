package com.shfb.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.service.bo.SvcRequest;
import com.shfb.oframe.core.util.service.bo.SvcResponse;
import com.shfb.oframe.core.util.spring.SvcUtil;
import com.shfb.oframe.core.web.controller.AbstractBaseController;
import com.shfb.oframe.core.web.util.RequestUtil;
import com.shfb.oframe.core.web.util.ResponseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/12/30.
 */
@Controller
@RequestMapping({"/shfb/svc/staff"})
public class StaffController extends AbstractBaseController {

    /**
     * 查询所有有权限的某个节点下所有的权限编码
     *
     * @param request  rhtCd
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/rhtcds.gv")
    public void getAllCdsByRoot(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String rhtCd = request.getParameter("rhtCd");
        String rhtId = request.getParameter("rhtId");
        String rightType = request.getParameter("rightType");
        if (StringUtil.isEmptyOrNull(rightType)) {
            rightType = "1";
        }

        String level = request.getParameter("level");
        XmlBean requestData = new XmlBean();
        requestData.setStrValue("SvcCont.rightType", rightType);
        requestData.setStrValue("SvcCont.rhtId", rhtId);
        requestData.setStrValue("SvcCont.rhtCd", rhtCd);
        requestData.setStrValue("SvcCont.level", level);
        svcRequest.setReqData(requestData);
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryRhtCdsWithRht", svcRequest);
        ResponseUtil.printAjax(response, haveRht(svcResponse));
    }

    //处理权限节点信息
    private com.alibaba.fastjson.JSONObject haveRht(SvcResponse svcResponse) {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("success", svcResponse.isSuccess());
        jsonObject.put("errMsg", svcResponse.getErrMsg());
        jsonObject.put("data", "[]");
        if (svcResponse.isSuccess()) {
            XmlBean responseData = svcResponse.getRspData();
            if (responseData != null) {
                XmlBean menu = responseData.getBeanByPath("SvcCont.Staff.Menu");
                if (menu != null) {
                    com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(menu.toJson());
                    if (jsonObject1.containsKey("Menu")) {
                        com.alibaba.fastjson.JSONObject jsonObject2 = jsonObject1.getJSONObject("Menu");
                        if (jsonObject2.containsKey("Node")) {
                            JSONArray jsonArray = jsonObject2.getJSONArray("Node");
                            jsonObject.put("data", jsonArray);
                        }
                    }
                }
            }
        }
        return jsonObject;
    }

//    /**
//     * 返回消息头改为json，避免通过中间层访问数据过长而导致乱码
//     *
//     * @param response
//     * @param printObj
//     */
//    public static void printAjaxJSON(HttpServletResponse response, Object printObj) {
//        response.setContentType("text/json; charset=UTF-8");
//        response.setHeader("Cache-Control", "no-cache");
//        PrintWriter out = null;
//        try {
//            out = response.getWriter();
//            out.print(printObj);
//        } catch (IOException var4) {
//            LOG.error("Error:Cannot create PrintWriter Object !");
//        } finally {
//            if (out != null) {
//                out.close();
//            }
//        }
//
//    }

    @Override
    protected Method getScriptMethod(Object scriptObject, String methodName) {
        return null;
    }
}
