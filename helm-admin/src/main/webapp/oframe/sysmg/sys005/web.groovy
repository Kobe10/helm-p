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

class web extends GroovyController {

    /**
     * 居民申请是否上传图片
     */
    String APPLY_NEED_PIC_220 = "APPLY_NEED_PIC_220";

    /**
     * 网站是否需要登陆框
     */
    String NEED_LOGIN_220 = "NEED_LOGIN_220";

    /**
     * 项目可选安置方式
     */
    String APPLY_TYPE = "220-10001";

    /**
     * 项目可选社区
     */
    String REG_ADDR = "220-REG_ADDR";

    /**
     * 首页图片
     */
    String WEB_PIC_220 = "WEB_PIC_220";
    /**
     * 友情链接
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView flink(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        reqData.setValue(nodePath + "itemCd", request.getParameter("itemCd"));
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            modelMap.put("nodeInfo", staffBean.getRootNode());
            int roleCount = staffBean.getListNum("SysCfg.Values.Value");
            List<Map<String, String>> valueList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < roleCount; i++) {
                nodePath = "SysCfg.Values.Value[${i}].";
                Map<String, String> item = new HashMap<String, String>();
                item.put("valueCd", staffBean.getStrValue(nodePath + "valueCd"));
                item.put("valueName", staffBean.getStrValue(nodePath + "valueName"));
                item.put("notes", staffBean.getStrValue(nodePath + "notes"));
                valueList.add(item);
            }
            modelMap.put("valueList", valueList);
        }
        return new ModelAndView("/oframe/sysmg/sys005/flink", modelMap)
    }

    /**
     * 友情链接
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView apply(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 输出内容
        String needPhoto = "1";
        String needLogin = "1";
        List<Map<String, String>> regAddr = new ArrayList<Map<String, String>>();
        List<Map<String, String>> applyType = new ArrayList<Map<String, String>>();
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.SysCfgs.SysCfg[0].itemCd", APPLY_NEED_PIC_220);
        reqData.setValue("SvcCont.SysCfgs.SysCfg[1].itemCd", APPLY_TYPE);
        reqData.setValue("SvcCont.SysCfgs.SysCfg[2].itemCd", REG_ADDR);
        reqData.setValue("SvcCont.SysCfgs.SysCfg[3].itemCd", NEED_LOGIN_220);
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 读取配置数据
            XmlBean resData = svcResponse.getRspData();
            int cfgCnt = resData.getListNum("SvcCont.SysCfgs.SysCfg");
            for (int j = 0; j < cfgCnt; j++) {
                XmlBean sysCfgBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[${j}]");
                String itemCd = sysCfgBean.getStrValue("SysCfg.itemCd");
                if (StringUtil.isEqual(itemCd, APPLY_NEED_PIC_220)) {
                    needPhoto = sysCfgBean.getStrValue("SysCfg.dftValue");
                } else if (StringUtil.isEqual(itemCd, NEED_LOGIN_220)) {
                    needLogin = sysCfgBean.getStrValue("SysCfg.dftValue");
                } else if (StringUtil.isEqual(itemCd, APPLY_TYPE)) {
                    int roleCount = sysCfgBean.getListNum("SysCfg.Values.Value");
                    for (int i = 0; i < roleCount; i++) {
                        String nodePath = "SysCfg.Values.Value[${i}].";
                        Map<String, String> item = new HashMap<String, String>();
                        item.put("valueCd", sysCfgBean.getStrValue(nodePath + "valueCd"));
                        item.put("valueName", sysCfgBean.getStrValue(nodePath + "valueName"));
                        item.put("notes", sysCfgBean.getStrValue(nodePath + "notes"));
                        applyType.add(item);
                    }
                } else if (StringUtil.isEqual(itemCd, REG_ADDR)) {
                    int roleCount = sysCfgBean.getListNum("SysCfg.Values.Value");
                    for (int i = 0; i < roleCount; i++) {
                        String nodePath = "SysCfg.Values.Value[${i}].";
                        Map<String, String> item = new HashMap<String, String>();
                        item.put("valueCd", sysCfgBean.getStrValue(nodePath + "valueCd"));
                        item.put("valueName", sysCfgBean.getStrValue(nodePath + "valueName"));
                        item.put("notes", sysCfgBean.getStrValue(nodePath + "notes"));
                        regAddr.add(item);
                    }
                }
            }
        }
        // 输出内容设置
        modelMap.put("needPhoto", needPhoto);
        modelMap.put("needLogin", needLogin);
        modelMap.put("applyType", applyType);
        modelMap.put("regAddr", regAddr);
        return new ModelAndView("/oframe/sysmg/sys005/apply", modelMap)
    }

    /**
     * 保存居民申请配置
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView saveApply(HttpServletRequest request, HttpServletResponse response) {

        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.SysCfgs.SysCfg[0].itemCd", APPLY_NEED_PIC_220);
        reqData.setValue("SvcCont.SysCfgs.SysCfg[1].itemCd", APPLY_TYPE);
        reqData.setValue("SvcCont.SysCfgs.SysCfg[2].itemCd", REG_ADDR);
        reqData.setValue("SvcCont.SysCfgs.SysCfg[3].itemCd", NEED_LOGIN_220);

        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 请求参数
            XmlBean newReqData = new XmlBean();
            // 读取配置数据
            XmlBean resData = svcResponse.getRspData();
            int cfgCnt = resData.getListNum("SvcCont.SysCfgs.SysCfg");
            int addIdx = 0;
            for (int j = 0; j < cfgCnt; j++) {
                XmlBean sysCfgBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[${j}]");
                String itemCd = sysCfgBean.getStrValue("SysCfg.itemCd");
                if (StringUtil.isEqual(itemCd, APPLY_NEED_PIC_220)) {
                    sysCfgBean.setStrValue("SysCfg.dftValue", request.getParameter("needPhoto"));
                    newReqData.setBeanByPath("SvcCont.SysCfgs", sysCfgBean);
                } else if (StringUtil.isEqual(itemCd, NEED_LOGIN_220)) {
                    sysCfgBean.setStrValue("SysCfg.dftValue", request.getParameter("needLogin"));
                    newReqData.setBeanByPath("SvcCont.SysCfgs", sysCfgBean);
                } else if (StringUtil.isEqual(itemCd, APPLY_TYPE)) {
                    sysCfgBean.removeNode("SysCfg.Values");
                    String[] valueCds = request.getParameterValues("valueCd");
                    String[] valueNames = request.getParameterValues("valueName");
                    int m = 0;
                    if (valueCds != null) {
                        for (int i = 0; i < valueCds.length; i++) {
                            if (StringUtil.isEmptyOrNull(valueCds[i])) {
                                continue;
                            }
                            String nodePath = "SysCfg.Values.Value[${m++}].";
                            sysCfgBean.setStrValue(nodePath + "valueCd", valueCds[i]);
                            sysCfgBean.setStrValue(nodePath + "valueName", valueNames[i]);
                        }
                    }
                    newReqData.setBeanByPath("SvcCont.SysCfgs", sysCfgBean);
                } else if (StringUtil.isEqual(itemCd, REG_ADDR)) {
                    sysCfgBean.removeNode("SysCfg.Values");
                    String[] regAddr = request.getParameterValues("regAddr");
                    int m = 0;
                    if (regAddr != null) {
                        for (int i = 0; i < regAddr.length; i++) {
                            if (StringUtil.isEmptyOrNull(regAddr[i])) {
                                continue;
                            }
                            String nodePath = "SysCfg.Values.Value[${m++}].";
                            sysCfgBean.setStrValue(nodePath + "valueCd", regAddr[i]);
                            sysCfgBean.setStrValue(nodePath + "valueName", regAddr[i]);
                        }
                    }
                    newReqData.setBeanByPath("SvcCont.SysCfgs", sysCfgBean);
                }
            }
            // 请求调用服务
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.setReqData(newReqData);
            // 调用服务
            svcResponse = SvcUtil.callSvc("sysCfgService", "saveSysCfgData", svcRequest);
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 首页图片
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView webPic(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 输出内容
        List<Map<String, String>> webPicList = new ArrayList<Map<String, String>>();
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.SysCfgs.SysCfg[0].itemCd", WEB_PIC_220);
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 读取配置数据
            XmlBean resData = svcResponse.getRspData();
            int cfgCnt = resData.getListNum("SvcCont.SysCfgs.SysCfg");
            for (int j = 0; j < cfgCnt; j++) {
                XmlBean sysCfgBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[${j}]");
                String itemCd = sysCfgBean.getStrValue("SysCfg.itemCd");
                if (StringUtil.isEqual(itemCd, WEB_PIC_220)) {
                    int roleCount = sysCfgBean.getListNum("SysCfg.Values.Value");
                    for (int i = 0; i < roleCount; i++) {
                        String nodePath = "SysCfg.Values.Value[${i}].";
                        Map<String, String> item = new HashMap<String, String>();
                        item.put("valueCd", sysCfgBean.getStrValue(nodePath + "valueCd"));
                        item.put("valueName", sysCfgBean.getStrValue(nodePath + "valueName"));
                        item.put("notes", sysCfgBean.getStrValue(nodePath + "notes"));
                        webPicList.add(item);
                    }
                }
            }
        }
        // 输出内容设置
        modelMap.put("webPicList", webPicList);
        return new ModelAndView("/oframe/sysmg/sys005/webpic", modelMap)
    }


    /**
     * 保存首页图片
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView saveWebPic(HttpServletRequest request, HttpServletResponse response) {

        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.SysCfgs.SysCfg[0].itemCd", WEB_PIC_220);
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 请求参数
            XmlBean newReqData = new XmlBean();
            // 读取配置数据
            XmlBean resData = svcResponse.getRspData();
            int cfgCnt = resData.getListNum("SvcCont.SysCfgs.SysCfg");
            for (int j = 0; j < cfgCnt; j++) {
                XmlBean sysCfgBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[${j}]");
                String itemCd = sysCfgBean.getStrValue("SysCfg.itemCd");
                if (StringUtil.isEqual(itemCd, WEB_PIC_220)) {
                    sysCfgBean.removeNode("SysCfg.Values");
                    String[] docIds = request.getParameterValues("docId");
//                    String[] docNames = request.getParameterValues("docName");
                    int m = 0;
                    if (docIds != null) {
                        for (int i = 0; i < docIds.length; i++) {
                            if (StringUtil.isEmptyOrNull(docIds[i])) {
                                continue;
                            }
                            String nodePath = "SysCfg.Values.Value[${m++}].";
                            sysCfgBean.setStrValue(nodePath + "valueCd", docIds[i]);
                            sysCfgBean.setStrValue(nodePath + "valueName", docIds[i]);
                        }
                    }
                    newReqData.setBeanByPath("SvcCont.SysCfgs", sysCfgBean);
                }
            }
            // 请求调用服务
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.setReqData(newReqData);
            // 调用服务
            svcResponse = SvcUtil.callSvc("sysCfgService", "saveSysCfgData", svcRequest);
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
}
