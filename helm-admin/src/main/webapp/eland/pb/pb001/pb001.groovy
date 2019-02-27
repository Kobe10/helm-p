import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import com.shfb.oframe.sysmg.repo.vo.SvcDoc
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class pb001 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("regId", request.getParameter("regId"));
        // 获取显示模式
        modelMap.put("showYlTree", getCookieByName(request, "showYlTree"));
        modelMap.put("showYlTreeModel", getCookieByName(request, "showYlTreeModel"));
        return new ModelAndView("/eland/pb/pb001/pb001", modelMap);
    }

    /**
     * 初始化建筑概要信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initBuildSummary(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        String buildId = request.getParameter("buildId");
        modelMap.put("buildId", buildId);
        String method = request.getParameter("method");
        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //基本信息 BaseInfo
        operationData.setStrValue(nodePath + "BuildInfo.buildId", buildId);
        svcRequest.addOp("QUERY_BUILD_BASE_INFO", operationData);

        // 查询管理信息
        svcRequest.addOp("QUERY_BUILD_MNG_INFO", operationData);

        // 查询腾退成本信息
        operationData = new XmlBean();
        //基本信息 BaseInfo
        operationData.setStrValue(nodePath + "entityName", "BuildInfo");
        operationData.setStrValue(nodePath + "groupName", "ttCost");
        operationData.setStrValue(nodePath + "entityKey", buildId);
        svcRequest.addOp("QUERY_ENTITY_GROUP", operationData);

        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_BASE_INFO");
            if (rspBean != null) {
                modelMap.put("buildBean", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_MNG_INFO");
            if (rspBean != null) {
                modelMap.put("buildMng", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_GROUP");
            if (rspBean != null) {
                modelMap.put("buildBcYg", rspBean.getBeanByPath("Operation.OpResult.BuildInfo").getRootNode());
            }
        }
        // 调用服务获取院落的统计信息
        return new ModelAndView("/eland/pb/pb001/pb001_info", modelMap);
    }

    /**
     * 显示院落统计报表
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initBRpt(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        // 返回数据
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/pb/pb001/pb001_rpt", modelMap);
    }

    /**
     * 初始化主界面查询界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initQ(HttpServletRequest request, HttpServletResponse response) {

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        String queryType = requestUtil.getStrParam("queryType");
        String regId = requestUtil.getStrParam("regId");
        String prjCd = requestUtil.getStrParam("prjCd");
        /* 输入参数组织 */
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", regId);
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "prjCd", requestUtil.getStrParam("prjCd"));
        // 区域类型
        reqData.setStrValue(rootNodePath + "regUseType", "1");
        // 查询用户区域
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryUseRegWithPic", svcRequest);

        // 界面展示参数
        ModelMap modelMap = new ModelMap();
        // 获取区域名称
        if (svcResponse.isSuccess()) {
            regId = svcResponse.getRspData().getStrValue("SvcCont.PrjReg.regId");
            String regName = svcResponse.getRspData().getStrValue("SvcCont.PrjReg.regName");
            modelMap.put("regId", regId);
            modelMap.put("regName", regName);
        }
        // 项目编号
        modelMap.put("prjCd", prjCd);
        // 返回处理界面
        if (StringUtil.isEmptyOrNull(queryType) || "1".equals(queryType)) {
            return new ModelAndView("/eland/pb/pb001/pb00101", modelMap);
        } else if ("2".equals(queryType)) {
            return new ModelAndView("/eland/pb/pb001/pb00102", modelMap);
        }
    }

    /**
     * 删除房产信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteHouse(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String houseId = request.getParameter("houseId");
        XmlBean operationData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        operationData.setStrValue(nodePath + "hsId", houseId);
        svcRequest.addOp("DELETE_TT_HOUSE_INFO", operationData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 删除院落信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteBuild(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");
        XmlBean operationData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        operationData.setStrValue(nodePath + "buildId", buildId);
        svcRequest.addOp("DELETE_TT_BUILD_INFO", operationData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "buildId: ${buildId}");
    }

    /**
     * 签约协议生成
     */
    public void generateContract(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        RequestUtil requestUtil = new RequestUtil(request);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String hsCtId = "";
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            //查询签约id
            XmlBean paramData = new XmlBean();
            String nodePath = "OpData.";
            paramData.setStrValue(nodePath + "entityName", "HsCtInfo");
            paramData.setStrValue(nodePath + "Conditions.Condition[0].fieldName", "hsId");
            paramData.setStrValue(nodePath + "Conditions.Condition[0].fieldValue", hsId);
            paramData.setStrValue(nodePath + "Conditions.Condition[0].operation", "=");
            paramData.setStrValue(nodePath + "ResultFields.fieldName[0]", "hsCtId");
            svcRequest.addOp("QUERY_ENTITY_CMPT", paramData);
            SvcResponse svcResponse1 = query(svcRequest);
            if (svcResponse1.isSuccess()) {
                hsCtId = svcResponse1.getFirstOpRsp("QUERY_ENTITY_CMPT").getStrValue("Operation.OpResult.PageData.Row.hsCtId");
                //生成协议之前，判断数据库是否有签约款项，如没有。调组件保存。
                svcRequest = RequestUtil.getSvcRequest(request);
                paramData = new XmlBean();
                nodePath = "OpData.";
                paramData.setStrValue(nodePath + "OpName", "CREATE_AGREEMENT");
                paramData.setStrValue(nodePath + "HsCtInfo.hsId", hsId);
                paramData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
                svcRequest.addOp("CREATE_AGREEMENT", paramData);
                SvcResponse svcResponse2 = transaction(svcRequest);
                if (svcResponse2.isSuccess()) {
                    XmlBean opResult1 = svcResponse2.getFirstOpRsp("CREATE_AGREEMENT");

                    //执行ct_1规则
                    svcRequest = RequestUtil.getSvcRequest(request);
                    paramData = new XmlBean();
                    paramData.setStrValue(nodePath + "ruleFile", "webroot:/config/${prjCd}/generateDoc.groovy");
                    paramData.setStrValue(nodePath + "RuleParam.hsId", hsId);
                    paramData.setStrValue(nodePath + "RuleParam.prjCd", prjCd);
                    svcRequest.addOp("EXECUTE_RULE", paramData);
                    // 调用服务，执行数据查询
                    SvcResponse svcResponse = query(svcRequest);
                    boolean isSuccess = svcResponse.isSuccess();
                    String docId = "";
                    if (isSuccess) {
                        XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
                        if (opResult != null) {
                            List<SvcDoc> docList = opResult.getValue("Operation.OpResult.svcDocList");
                            String docName = "";
                            String docPath = "";
                            for (SvcDoc doc : docList) {
                                docName = doc.getDocName();
                                docPath = doc.getDocPath();
                            }
                            String contextType = requestUtil.getStrParam("contextType");
                            File localFile = ServerFile.getFile(docPath);
                            // 下载内容进行缓存
                            //20秒之内重新进入该页面的话不会进入该servlet的
                            Date modifiedTime = DateUtil.toDateYmdHms("20150101000000");
                            response.setDateHeader("Last-Modified", modifiedTime.getTime());
                            response.setDateHeader("Expires", modifiedTime.getTime() + 6553600);
                            response.setHeader("Cache-Control", "public");
                            response.setHeader("Pragma", "Pragma");
                            // 文件下载输出到响应流
                            ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
                        }
                    }
                }
            } else {

            }
        }
    }

    /**
     * 根据名字获取cookie
     * @param request
     * @param name cookie名字
     * @return
     */
    private String getCookieByName(HttpServletRequest request, String name) {
        // 从cookie获取Session主键
        Cookie[] cookieArr = request.getCookies();
        if (cookieArr != null) {
            for (int i = 0; i < cookieArr.length; i++) {
                Cookie cookie = cookieArr[i];
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
