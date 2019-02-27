import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.exception.BusinessException
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * User: shfb_wang 
 * Date: 2016/3/8 0008 18:36
 * Copyright(c) 北京四海富博计算机服务有限公司
 */

class mob003 extends GroovyController {
    private static final Log LOG = LogFactory.getLog(ResponseUtil.class);
    private static final String PAGE_ENCODING = "UTF-8";

    /**
     * 移动端登录,登录成功返回用户名称及token信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        // 1.获取输入请求信息
        //登陆信息
        String userName = request.getParameter("user");
        String password = request.getParameter("password");
        String token = RequestUtil.getOpAccept(DateUtil.getSysDate()); ;
        //设备信息
        //设备类型(ios、android、pc固定)
        String device = request.getParameter("device");
        // 没有设备编号的时候要进行空转换
        if (StringUtil.isEmptyOrNull(device)) {
            device = "";
        }

        device = device.trim().toLowerCase();
        if (device.startsWith("iphone")) {
            device = "ios";
        }

        //别名(用于app推送消息)
        String alias = request.getParameter("alias");
        //设备id,同一设备同一app只有一个registId
        String registId = request.getParameter("registId");
        //项目编号
        String prjCd = request.getParameter("prjCd");
        //todo sjp ip
        String ipAddr = RequestUtil.getIpAddr(request);
        //2.查询该账号是否有app登陆权限
        SvcRequest rhtRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqXml = new XmlBean();
        reqXml.setStrValue("SvcCont.rhtCd", "LOGIN_ELAND_APP");
        reqXml.setStrValue("SvcCont.rightType", "1");
        reqXml.setStrValue("SvcCont.prjCd", 0);
        reqXml.setStrValue("SvcCont.staffCode", userName);
        rhtRequest.setReqData(reqXml);
        SvcResponse rhtResponse = callService("staffService", "appLoginRht", rhtRequest);
        boolean isHaveRht = false;
        if (rhtResponse.isSuccess()) {
            String path = "Response.SvcCont.haveRight";
            isHaveRht = Boolean.valueOf(rhtResponse.getStrValue(path));
        }
        //3.获取工号可以访问的项目工程，避免空指针问题
        if (StringUtil.isEmptyOrNull(prjCd) && StringUtil.isNotEmptyOrNull(userName) && isHaveRht) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean reqData = new XmlBean();
            // 查询条件
            reqData.setStrValue("SvcCont.staffCode", userName);
            reqData.setStrValue("SvcCont.orgId", "");
            reqData.setStrValue("SvcCont.isShowExp", "1");   // 是否显示无效项目标志。 1 不显示无效项目， 0  显示。
            // 调用服务查询实体全部属性
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            Map<String, String> prjMap = new LinkedHashMap<String, String>();
            if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        prjMap.put(xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"),
                                xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjName"))
                    }
                }
            }
            if (StringUtil.isEmptyOrNull(prjCd) && prjMap.keySet().size() > 0) {
                prjCd = prjMap.keySet().toArray()[0];
            } else {
                prjCd = "0";
            }
        }
        String result = "";
        if (isHaveRht) {
            //4.check
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.setValue("Request.SvcCont.staffCode", userName);
            svcRequest.setValue("Request.SvcCont.password", password);
            svcRequest.setValue("Request.SvcCont.loginAccept", token);
            svcRequest.setValue("Request.SvcCont.loginIpAddr", ipAddr);
            svcRequest.setValue("Request.SvcCont.device", device);
            svcRequest.setValue("Request.SvcCont.alias", alias);
            svcRequest.setValue("Request.SvcCont.registId", registId);
            SvcResponse svcResponse = callService("staffService", "checkStaffLogin", svcRequest);
            if (svcResponse.isSuccess()) {
                //5.login
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.setValue("Request.SvcCont.staffCode", userName);
                svcRequest.setValue("Request.SvcCont.password", password);
                svcRequest.setValue("Request.SvcCont.loginAccept", token);
                svcRequest.setValue("Request.SvcCont.device", device);
                svcResponse = callService("staffService", "saveStaffLogin", svcRequest);
                if (svcResponse.isSuccess()) {
                    // 保存登录信息
                    SessionUtil sessionUtil = new SessionUtil(token, request);
                    // 登录工号信息
                    String staffId = svcResponse.getStrValue("Response.SvcCont.Staff.StaffId");
                    String staffCd = svcResponse.getStrValue("Response.SvcCont.Staff.StaffCd");
                    String staffName = svcResponse.getStrValue("Response.SvcCont.Staff.StaffName");
                    String orgId = svcResponse.getStrValue("Response.SvcCont.Staff.OrgId");
                    // 在Session中保存登录信息
                    String loginInfo = """<LoginInfo>
                                    <StaffId>${staffId}</StaffId>
                                    <staffCode>${staffCd}</staffCode>
                                    <staffName>${staffName}</staffName>
                                    <orgId>${orgId}</orgId>
                                    <prjCd>${prjCd}</prjCd>
                               </LoginInfo>""";

                    sessionUtil.setAttr(SessionUtil.LOGIN_IN_SESSION_KEY, loginInfo);
                    // 返回处理结果
                    result = """{"isSuccess": ${svcResponse.isSuccess()}, "version": "1","userName": "${
                        staffName
                    }", "token": "${token}", "errMsg": ""}""";
                } else {
                    // 返回处理结果
                    result = """{"isSuccess": ${svcResponse.isSuccess()}, "errMsg": "${svcResponse.errMsg}"}""";
                }
            } else {
                // 返回处理结果
                result = """{"isSuccess": ${svcResponse.isSuccess()}, "errMsg": "${svcResponse.errMsg}"}""";
            }
        } else {
            result = """{"isSuccess": ${isHaveRht}, "errMsg": "当前用户没有应用访问权限"}""";
        }
        // 输出
        printAjaxJSON(response, result);
    }

    /**
     * 用户退出
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getParameter("token");
        SessionUtil sessionUtil = new SessionUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.loginAccept", token);
        SvcResponse svcResponse = callService("staffService", "saveStaffLogout", svcRequest);
        sessionUtil.removeAll();
        String result = """{"isSuccess": ${svcResponse.isSuccess()}, "errMsg": "${svcResponse.errMsg}"}""";
        printAjaxJSON(response, result);
    }

    /**
     * 系统工号有权限访问的项目
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView staffPrj(HttpServletRequest request, HttpServletResponse response) {
        // 获取工号可以访问的项目工程
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String staffCd = svcRequest.getReqStaffCd();
        // 输出结果准备
        List prjList = new ArrayList();
        JSONObject jsonObject = new JSONObject();
        // 输出公告信息
        if (!StringUtil.isEmptyOrNull(staffCd)) {
            // 查询条件
            XmlBean reqData = new XmlBean();
            reqData.setStrValue("SvcCont.staffCode", staffCd);
            reqData.setStrValue("SvcCont.isShowExp", "1");
            // 调用服务查询实体全部属性
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        prjList.add([
                                prjCd  : xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"),
                                prjName: xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjName")
                        ]);
                    }
                }
            }
            jsonObject.put("isSuccess", svcResponse.isSuccess());
            jsonObject.put("errMsg", svcResponse.getErrMsg());
        } else {
            jsonObject.put("isSuccess", "false");
            jsonObject.put("errMsg", "参数不合法");
        }
        jsonObject.put("staffPr", prjList);
        printAjaxJSON(response, jsonObject.toString());
    }

    /**
     * 获取首页公告
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void mainNotice(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用服务数据
        // 定义到处的数据大小
        String currentPage = request.getParameter("currentPage");
        if (StringUtil.isEmptyOrNull(currentPage)) {
            currentPage = "1";
        }
        String pageSize = request.getParameter("pageSize");
        if (StringUtil.isEmptyOrNull(pageSize)) {
            pageSize = "20";
        }

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HelmNotice");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "publishStatus");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        opData.setValue(prePath + "PageInfo.currentPage", currentPage);
        opData.setValue(prePath + "PageInfo.pageSize", pageSize);

        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "publishOrder");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "desc");
        opData.setStrValue(prePath + "SortFields.SortField[1].fieldName", "publishDate");
        opData.setStrValue(prePath + "SortFields.SortField[1].sortOrder", "desc");

        int addCount = 0;
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "noticeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "noticeTitle");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "publishDate");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "noticeSummary");

        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        List noticeList = new ArrayList();
        String totalPage = "";
        if (svcResponse.isSuccess()) {
            // 响应结果
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            int count = pageData.getListNum("PageData.Row");
            // 获取查询结果
            // TODO:日期后台翻译问题
            for (int i = 0; i < count; i++) {
                XmlBean rowInfo = pageData.getBeanByPath("PageData.Row[${i}]");
                String publishDate = rowInfo.getStrValue("Row.publishDate");
                if(StringUtil.isNotEmptyOrNull(publishDate)) {
                    publishDate = DateUtil.toStringYmdWthH(DateUtil.toDateYmdHms(publishDate))
                }
                noticeList.add([
                        noticeId   : rowInfo.getStrValue("Row.noticeId"),
                        noticeDate : publishDate,
                        noticeTitle: rowInfo.getStrValue("Row.noticeTitle")
                ]);
            }
        } else {
            noticeList = new ArrayList();
        }
        // 输出公告信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isSuccess", svcResponse.isSuccess());
        jsonObject.put("success", svcResponse.isSuccess());
        jsonObject.put("errMsg", svcResponse.getErrMsg());
        jsonObject.put("currentPage", currentPage);
        jsonObject.put("totalPage", totalPage);
        jsonObject.put("noticeList", noticeList);
        jsonObject.put("data", noticeList);
        printAjaxJSON(response, jsonObject.toString());
    }

    /**
     * 查询首页 多维度查询入口列表
     */
    public void searchItems(HttpServletRequest request, HttpServletResponse response) {
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcReqCfg = RequestUtil.getSvcRequest(request);
        XmlBean reqDataCfg = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.";
        reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "APP_SEARCH_ITEMS");
        reqDataCfg.setValue(nodePath + "SysCfg[0].isCached", "true");
        reqDataCfg.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
        svcReqCfg.setReqData(reqDataCfg);
        // 调用服务
        SvcResponse svcRspCfg = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcReqCfg);
        List searchItems = new ArrayList();
        if (svcRspCfg.isSuccess()) {
            XmlBean cfgBean = svcRspCfg.getRspData().getBeanByPath("SvcCont.SysCfgs");
            int cfgValueNum = svcRspCfg.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            for (int i = 0; i < cfgValueNum; i++) {
                //获取该配置参数的 key ， value
                String busikey = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueCd");
                String searchName = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueName");
                String notes = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].notes");
                /**
                 * 从备注JSON 拆分 图标url。
                 * 从参数配置json里拿到要过滤的属性字段,然后获取该属性字段的配置参数。
                 * 根据配置json, 获取实际查询过滤字段、过滤字段对应查询码表。
                 */
                JSONObject jsonNote = JSONObject.fromObject(notes);
                String iconUrl = jsonNote.get("iconUrl");
                String statusCdCfg = jsonNote.get("statusCdCfg");
                String defaultSearchCd = jsonNote.get("defaultSearchCd");

                /*
                 * 讲实际过滤的字段的对应配置参数,码值查出来,返回,供搜索页展示过滤条件使用。
                 */
                JSONArray searchStatusVal = new JSONArray();
                Map cfgMap = getCfgCollection(request, statusCdCfg, true, NumberUtil.getIntFromObj(prjCd));
                for (String cfgKey : cfgMap.keySet()) {
                    JSONObject temp = new JSONObject();
                    temp.put("statusCd", cfgKey);
                    temp.put("statusCdName", cfgMap.get(cfgKey));
                    searchStatusVal.push(temp);
                }
                //拼接 返回json字符串,包括必要的字段。
                searchItems.add([
                        busikey        : busikey,
                        searchName     : searchName,
                        searchStatusVal: searchStatusVal,
                        defaultSearchCd: defaultSearchCd,
                        iconUrl        : iconUrl,
                ]);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isSuccess", svcRspCfg.isSuccess());
        jsonObject.put("errMsg", svcRspCfg.getErrMsg());
        jsonObject.put("searchItems", searchItems);
        printAjaxJSON(response, jsonObject.toString());
    }

    /**
     * 项目公告主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView notice(HttpServletRequest request, HttpServletResponse response) {
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
            for (int i = 0; i < nPicCount; i++) {
                Map prjNoticePicMap = new HashMap();
                String path = "SvcCont.NoticePics.noticePic[${i}]";
                XmlBean prjNoticePic = rspData.getBeanByPath(path);
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
        return new ModelAndView("/mobile/mob003/notice", modelMap);
    }

    /**
     * 居民信息查询
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void queryJm(HttpServletRequest request, HttpServletResponse response) {
        String busikey = request.getParameter("busikey");
        String[] filterValues = request.getParameterValues("filterValues");
        String queryValue = request.getParameter("queryValue");

        if (StringUtil.isEmptyOrNull(busikey)) {
            throw new BusinessException("eland-web", "查询主键不可为空!");
        }
        /**
         * 1、从配置参数 APP_SEARCH_ITEMS 里拿到 busikey 对应的配置json
         * 2、从中获取对应的要查询的属性字眼, 展示结果字段,
         * 3、执行查询并处理结果,赋值给固定的 返回结果字段。
         */
        String statusCdField = "";
        String[] resultField = [];
        // 1、从配置参数 APP_SEARCH_ITEMS 里拿到 busikey 对应的配置json
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcReqCfg = RequestUtil.getSvcRequest(request);
        XmlBean reqDataCfg = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.";
        reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "APP_SEARCH_ITEMS");
        reqDataCfg.setValue(nodePath + "SysCfg[0].isCached", "true");
        reqDataCfg.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
        svcReqCfg.setReqData(reqDataCfg);
        // 调用服务
        SvcResponse svcRspCfg = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcReqCfg);
        if (svcRspCfg.isSuccess()) {
            XmlBean cfgBean = svcRspCfg.getRspData().getBeanByPath("SvcCont.SysCfgs");
            int cfgValueNum = svcRspCfg.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            for (int i = 0; i < cfgValueNum; i++) {
                //获取该配置参数的 key ， value
                if (StringUtil.isEqual(busikey, cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueCd"))) {
                    String notes = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].notes");
                    /**
                     * 从备注JSON 拆分 图标url。
                     * 从参数配置json里拿到要过滤的属性字段,然后获取该属性字段的配置参数。
                     * 根据配置json, 获取实际查询过滤字段、过滤字段对应查询码表。
                     */
                    //2、从中获取对应的要查询的属性字眼, 展示结果字段,
                    JSONObject jsonNote = JSONObject.fromObject(notes);
                    statusCdField = jsonNote.get("statusCdField");
                    resultField = jsonNote.get("resultField");
                    break;
                }
            }
        }

        //默认要展示的 字段,
        String[] realResultField = ["HouseInfo.hsFullAddr", "HouseInfo.ttOrgId", "HouseInfo.hsOwnerPersons"];
        //合并默认展示字段,以及动态展示字段。
        List resultFieldlist = new ArrayList(Arrays.asList(resultField));
        resultFieldlist.addAll(Arrays.asList(realResultField));

        String condNames = "";
        String conds = "";
        String condValues = "";
        if (StringUtil.isNotEmptyOrNull(statusCdField) && filterValues != null) {
            condNames = statusCdField;
            conds = "IN";
            condValues = filterValues.join("|");
        }

        //拼接 总查询入参JSON
        JSONObject queryConditon = [
                entityName     : "HouseInfo",
                resultFields   : resultFieldlist.toArray(),
                conditionNames : [condNames],
                conditions     : [conds],
                conditionValues: [condValues],
                sortColumnArr  : ["HouseInfo.hsFullAddr"],
                sortOrderArr   : ["desc"],
                cmptName       : ""
        ];

        if (StringUtil.isNotEmptyOrNull(queryValue)) {
            //获取json里 查询条件默认值,根据输入查询条件,动态拼接新查询条件。
            JSONArray conditionNames = (JSONArray) queryConditon.get("conditionNames");
            JSONArray conditions = (JSONArray) queryConditon.get("conditions");
            JSONArray conditionValues = (JSONArray) queryConditon.get("conditionValues");

            // 长度小于4 并且是没有数字的时候按照产权人名称查询
            if (queryValue.length() < 4 && !queryValue.matches(".*\\d.*")) {
                //产权人名称查询
                conditionNames.push("HouseInfo.hsOwnerPersons");
                conditions.push("like");
                conditionValues.push(queryValue);
            } else {
                // 安置房屋地址查询
                conditionNames.push("HouseInfo.hsFullAddr");
                conditions.push("like");
                conditionValues.push(queryValue);
            }
            queryConditon.put("conditionNames", conditionNames);
            queryConditon.put("conditions", conditions);
            queryConditon.put("conditionValues", conditionValues);
        }

        // 3、执行查询并处理结果,赋值给固定的 返回结果字段。
        String resultJson = entityQuery(request, queryConditon, false);

        boolean success = JSONObject.fromObject(resultJson).get("isSuccess");
        if (success) {
            JSONObject resultJsonObj = JSONObject.fromObject(resultJson);
            //将查询结果 转换为json对象处理, 拿到整个返回结果。
            JSONArray queryListArr = (JSONArray) resultJsonObj.get("queryList");
            //循环结果列表里 每户数据
            for (JSONObject result : queryListArr) {
                //处理每户数据里 要展示的结果字段,替换为固定字段推给出参。
                int i = 0;
                for (String fieldTemp : resultField) {
                    //将配置参数码表里配置的json 与结果json可以做 对应转换,使key保持一致
                    String temp = fieldTemp.replace("HouseInfo.", "").replace(".", "_");

                    //HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus
                    //HsCtInfo_HsCtChooseInfo_chooseStatus
                    String resultField1 = result.get(temp + "_Name") ? result.get(temp + "_Name") : result.get(temp);
                    result.put("resultField" + i++, resultField1);
                }
            }
            resultJsonObj.put("queryList", queryListArr);
            resultJson = resultJsonObj.toString();
        }
        printAjaxJSON(response, resultJson);
    }

    /**
     * 查询个人信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void messageCount(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        JSONObject queryCondition = [
                entityName     : "NoticeInfo",
                resultFields   : ["noticeId"],
                conditionNames : ["toStaffId", "noticeReadStatus"],
                conditions     : ["=", "="],
                conditionValues: [svcRequest.getReqStaffIdInt(), "0"],
                sortColumnArr  : [],
                sortOrderArr   : [],
                cmptName       : "QUERY_ENTITY_PAGE_DATA"
        ];
        // 定义到处的数据大小
        String resultJson = entityQuery(request, queryCondition, true);
        printAjaxJSON(response, resultJson);
    }

    /**
     * 查询个人信息列表
     * 0未读；1已读；2不可读【通知状态为删除状态时为当前状态值】
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void messageList(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String noticeReadStatus = request.getParameter("noticeStatus");
        JSONObject queryConditon = [
                entityName     : "NoticeInfo",
                resultFields   : ["noticeId", "noticeContent", "createTime", "createStaffId"],
                conditionNames : ["toStaffId", "noticeReadStatus"],
                conditions     : ["=", "="],
                conditionValues: [svcRequest.getReqStaffIdInt(), noticeReadStatus],
                sortColumnArr  : ["createTime"],
                sortOrderArr   : ["desc"],
                cmptName       : "QUERY_ENTITY_PAGE_DATA"
        ];
        // 定义到处的数据大小
        String resultJson = entityQuery(request, queryConditon, false);
        JSONObject resultObj = JSONObject.fromObject(resultJson);
        //修改返回时间格式
        modifyTime(resultObj);
//        ResponseUtil.printAjax(response, resultObj.toString());
        response.setContentType("text/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.print(resultObj.toString());
        } catch (IOException e) {
            LOG.error("Error:Cannot create PrintWriter Object !");
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    public void modifyTime(JSONObject jsonObj) {
        JSONArray queryList = jsonObj.getJSONArray("queryList");
        Date now = new Date();
        for (int i = 0; i < queryList.size(); i++) {
            JSONObject tempObj = queryList.getJSONObject(i);
            String createTime = tempObj.getString("createTime");
            //算出与当前时间的间隔
            Date past = DateUtil.toDateYmdHms(createTime);
            int millis = (now.getTime() - past.getTime()) / 1000;
            String time = "";
            if (millis <= 0) {
                time = DateUtil.toStringYmdWthH(past);
            } else if (Math.floor(millis / (60 * 60 * 24)) > 0) {
                time = DateUtil.toStringYmdWthH(past);
            } else if (Math.floor(millis / (60 * 60)) > 0) {
                time = Math.round(Math.floor(millis / (60 * 60))) + "小时前";
            } else if (Math.floor(millis / (60)) > 0) {
                time = Math.round(Math.floor(millis / (60))) + "分钟前";
            } else {
                time = "刚刚"
            };
            tempObj.put("createTime_Name", time);
        }
    }

    /**
     * 标志个人消息 状态
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void setMessRead(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", getStaffPrjCd(request));
        //接收传入参数
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.NoticeInfo.noticeId", request.getParameter("noticeId"));
        svcRequest.addOp("SAVE_NOTICE_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);

        JSONObject result = new JSONObject();
        result.put("isSuccess", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());
        printAjaxJSON(response, result);
    }

    /**
     * 我的待办任务数量
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView taskCount(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("OpData.PageInfo.currentPage", "1");
        reqData.setValue("OpData.PageInfo.pageSize", "1000");
        // 排序信息
        reqData.setValue("OpData.SortInfo.sortColumn", "createTime");
        reqData.setValue("OpData.SortInfo.sortOrder", "asc");
        // 当前处理人
        reqData.setStrValue("OpData.ParamInfo.Param[0].name", "assignee");
        reqData.setStrValue("OpData.ParamInfo.Param[0].value", svcRequest.getReqStaffCd());
        // 调用服务
        svcRequest.addOp("QUERY_PROC_TASK_PAGE", reqData);
        SvcResponse svcResponse = query(svcRequest);
        // 添加分页查询数据参数
        ModelMap modelMap = new ModelMap();
        // 返回结果
        JSONObject result = new JSONObject();
        result.put("isSuccess", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());

        if (svcResponse.isSuccess()) {
            // 返回结果集
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_PROC_TASK_PAGE").getBeanByPath("Operation.OpResult");
            // 返回分页信息
            result.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
        }
        printAjaxJSON(response, result.toString());
    }

    /**
     * 代办任务
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView todoTask(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        int pageSize = requestUtil.getIntParam("pageSize");
        int currentPage = requestUtil.getIntParam("currentPage");
        if (0 == pageSize) {
            pageSize = 10;
        }
        if (0 == currentPage) {
            currentPage = 1;
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("OpData.PageInfo.currentPage", currentPage);
        reqData.setValue("OpData.PageInfo.pageSize", pageSize);
        // 排序信息
        reqData.setValue("OpData.SortInfo.sortColumn", "createTime");
        reqData.setValue("OpData.SortInfo.sortOrder", "asc");
        // 当前处理人
        reqData.setStrValue("OpData.ParamInfo.Param[0].name", "assignee");
        reqData.setStrValue("OpData.ParamInfo.Param[0].value", svcRequest.getReqStaffCd());
        // 调用服务
        svcRequest.addOp("QUERY_PROC_TASK_PAGE", reqData);
        SvcResponse svcResponse = query(svcRequest);
        // 添加分页查询数据参数
        ModelMap modelMap = new ModelMap();
        // 返回结果
        JSONObject result = new JSONObject();
        result.put("isSuccess", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());

        if (svcResponse.isSuccess()) {
            // 返回结果集
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_PROC_TASK_PAGE").getBeanByPath("Operation.OpResult");
            // 返回分页信息
            result.put("currentPage", queryResult.getValue("OpResult.PageInfo.currentPage"));
            result.put("totalPage", queryResult.getValue("OpResult.PageInfo.totalPage"));
            result.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
            int rowNum = queryResult.getListNum("OpResult.PageData.Row");
            List resultList = new ArrayList(rowNum);
            for (int j = 0; j < rowNum; j++) {
                XmlBean tempRow = queryResult.getBeanByPath("OpResult.PageData.Row[${j}]");
                Map<String, String> tempMap = new LinkedHashMap<String, String>();
                for (String tempStr : tempRow.getNodeNames("Row")) {
                    tempMap.put(tempStr, tempRow.getStrValue("Row." + tempStr));
                }
                resultList.add(tempMap);
            }
            // 返回结果集
            result.put("queryList", resultList);

        }
        printAjaxJSON(response, result.toString());
    }

    /**
     * 已办任务
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView doneTask(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        int pageSize = requestUtil.getIntParam("pageSize");
        int currentPage = requestUtil.getIntParam("currentPage");
        if (0 == pageSize) {
            pageSize = 10;
        }
        if (0 == currentPage) {
            currentPage = 1;
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("OpData.PageInfo.currentPage", currentPage);
        reqData.setValue("OpData.PageInfo.pageSize", pageSize);
        // 排序信息
        reqData.setValue("OpData.SortInfo.sortColumn", "createTime");
        reqData.setValue("OpData.SortInfo.sortOrder", "asc");
        // 当前处理人
        reqData.setStrValue("OpData.ParamInfo.Param[0].name", "assignee");
        reqData.setStrValue("OpData.ParamInfo.Param[0].value", svcRequest.getReqStaffCd());
        // 当前处理人
        reqData.setStrValue("OpData.ParamInfo.Param[1].name", "isComplete");
        reqData.setStrValue("OpData.ParamInfo.Param[1].value", "1");
        // 调用服务
        svcRequest.addOp("QUERY_PROC_TASK_PAGE", reqData);
        SvcResponse svcResponse = query(svcRequest);
        // 添加分页查询数据参数
        ModelMap modelMap = new ModelMap();
        // 返回结果
        JSONObject result = new JSONObject();
        result.put("isSuccess", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());

        if (svcResponse.isSuccess()) {
            // 返回结果集
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_PROC_TASK_PAGE").getBeanByPath("Operation.OpResult");
            // 返回分页信息
            result.put("currentPage", queryResult.getValue("OpResult.PageInfo.currentPage"));
            result.put("totalPage", queryResult.getValue("OpResult.PageInfo.totalPage"));
            result.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
            int rowNum = queryResult.getListNum("OpResult.PageData.Row");
            List resultList = new ArrayList(rowNum);
            for (int j = 0; j < rowNum; j++) {
                XmlBean tempRow = queryResult.getBeanByPath("OpResult.PageData.Row[${j}]");
                Map<String, String> tempMap = new LinkedHashMap<String, String>();
                for (String tempStr : tempRow.getNodeNames("Row")) {
                    tempMap.put(tempStr, tempRow.getStrValue("Row." + tempStr));
                }
                resultList.add(tempMap);
            }
            // 返回结果集
            result.put("queryList", resultList);
        }
        printAjaxJSON(response, result.toString());
    }

    /**
     * 返回首页的图片
     * @param request 请求信息
     * @param response 响应信息
     */
    public void appImg(HttpServletRequest request, HttpServletResponse response) {
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.SysCfgs.SysCfg[0].itemCd", "APP_CONF");
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);

        // 返回结果
        JSONObject result = new JSONObject();
        result.put("isSuccess", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());

        if (svcResponse.isSuccess()) {
            // 读取配置数据
            XmlBean resData = svcResponse.getRspData();
            XmlBean sysCfgBean = resData.getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            int valueCount = sysCfgBean.getListNum("SysCfg.Values.Value");
            for (int i = 0; i < valueCount; i++) {
                String nodePath = "SysCfg.Values.Value[${i}].";
                String valueCd = sysCfgBean.getStrValue(nodePath + "valueCd")
                String notes = sysCfgBean.getStrValue(nodePath + "notes")
                if (valueCd == "APP_TOP_IMG") {
                    if (StringUtil.isNotEmptyOrNull(notes)) {
                        result.put("appPicList", JSONArray.fromObject(notes));
                    }
                    break;
                }
            }
        }
        printAjaxJSON(response, result.toString());
    }

    /**
     * 获取有权限的业务APP
     * @param request 请求信息
     * @param response 响应信息
     */
    public void getSpaApps(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.level", "-1");
        svcRequest.setValue("Request.SvcCont.rhtCd", "SYS_APP");
        SvcResponse svcResponse = callService("staffService", "queryMenuTree", svcRequest);

        // 返回结果
        JSONObject result = new JSONObject();
        result.put("isSuccess", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());

        // APP应用列表
        JSONArray spaList = new JSONArray();
        // 服务调用成功
        if (svcResponse.isSuccess()) {
            // 获取节点数量
            XmlBean menuBean = svcResponse.getBeanByPath("Response.SvcCont.Staff.Menu");
            if (menuBean != null) {
                int count = menuBean.getListNum("Menu.Node");
                for (int i = 0; i < count; i++) {
                    XmlBean tempBean = menuBean.getBeanByPath("Menu.Node[${i}]");
                    String rhtId = tempBean.getStrValue("Node.rhtId");
                    String navUrl = tempBean.getStrValue("Node.navUrl");
                    String cNodeName = tempBean.getStrValue("Node.rhtName");
                    if (navUrl.startsWith("href:")) {
                        XmlBean rhtInfo = getTreeNodeInfo(request, rhtId);
                        if (rhtInfo != null) {
                            String notes = rhtInfo.getStrValue("Node.rhtAttr02");
                            JSONObject temp = JSONObject.fromObject(notes);
                            if (StringUtil.isEmptyOrNull(temp.get("spaName"))) {
                                temp.put("spaName", cNodeName);
                            }
                            spaList.add(temp);
                        }
                    }
                }
            }
        }
        result.put("spaList", JSONArray.fromObject(spaList));
        printAjaxJSON(response, result.toString());
    }

    /**
     * 获取首页指标
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void mainPoint(HttpServletRequest request, HttpServletResponse response) {
        String prjCd = request.getParameter("prjCd");
        /**
         * 面板查询请求处理
         */
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.staffCode", svcRequest.getReqStaffCd());
        reqData.setStrValue("SvcCont.isShowExp", "1");   // 是否显示无效项目标志。 1 不显示无效项目， 0  显示。
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        List portalRhts = new ArrayList();
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean portalRhtsBean = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            if (portalRhtsBean != null) {
                int haveRhtPortal = portalRhtsBean.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    boolean isHaveRht = Boolean.parseBoolean(portalRhtsBean.getStrValue("portalRhts.portalRht[${i}].isHaveRht"));
                    if (isHaveRht) {
                        XmlBean tempBean = portalRhtsBean.getBeanByPath("portalRhts.portalRht[${i}]");
                        String temp = tempBean.getStrValue("portalRht.navUrl");
                        if (temp.contains("mb005") || temp.contains("mb008")) {
                            portalRhts.add([
                                    rhtCd  : tempBean.getStrValue("portalRht.rhtId"),
                                    rhtName: tempBean.getStrValue("portalRht.rhtName")
                            ]);
                        }
                    }
                }
            }
        }
        // 输出公告信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isSuccess", svcResponse.isSuccess());
        jsonObject.put("errMsg", svcResponse.getErrMsg());
        jsonObject.put("mainPoint", portalRhts);
        printAjaxJSON(response, jsonObject.toString());
    }

    /**
     * 指标展示
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView pointView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String rhtCd = request.getParameter("rhtCd");
        String returnPage = '/mobile/mob003/pie';
        if (StringUtil.isEqual(rhtCd, '3215')) {
            returnPage = '/mobile/mob003/line';
        } else if (StringUtil.isEqual(rhtCd, '3217')) {
            returnPage = '/mobile/mob003/bar';
        }
        return new ModelAndView(returnPage, modelMap);
    }

    /**
     * 指标展示 一次性 查询指标所有内容
     */
    public ModelAndView quotaDetail(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        /*
         * 获取 rhtCd => rhtId 来 获取指标面板 实际配置 url 参数
         */
        String returnPage = "/mobile/mob003/mob003_chart";
        String rhtType = "1";
        String rhtId = request.getParameter("rhtCd");
        modelMap.put("privilegeId", rhtId);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtId", rhtId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean result = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node")
            String oldRhtUrl = result.getStrValue("Node.rhtAttr01");
            String rhtUrl = oldRhtUrl.substring(oldRhtUrl.indexOf("?") + 1);
            /*
             * 分割 url 获取实际请求参数
             */
            Map<String, String> paramMap = new HashMap();
            //默认参数
            paramMap.put("prjCd", prjCd);
            paramMap.put("privilegeId", rhtId);
            if (StringUtil.isNotEmptyOrNull(rhtUrl)) {
                /*
                 * 拆分具体参数   eg: prjJobCd=hs_status_chart&chartType=pie
                 * prjJobCd: 指标编号
                 * prjJobGroup: 指标分组
                 * chartType: 图表类型
                 */
                String[] params = rhtUrl.split('&');
                if (params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        //  eg:  prjJobCd=hs_status_chart
                        if (StringUtil.isNotEmptyOrNull(params[i])) {
                            String[] temp = params[i].split("=");
                            paramMap.put(temp[0], temp[1]);
                        }
                    }
                }
            } else {
                //TODO: 没有rhtUrl 报错。
                modelMap.put("returnJson", false);
            }

            /*
             * 拿到最后请求参数 请求具体服务拿到 绘制图表的json数据
             * 判断不同服务要查询不同的 服务
             */
            modelMap.put("title", paramMap.get("title"));
            modelMap.put("chartType", paramMap.get("chartType"));
            if (StringUtil.contains(oldRhtUrl, "mb008-initReg")) {
                // 调用私有服务,查询区域纬度 图表数据: "queryRegChartData",
                modelMap.put("returnJson", queryRegChartData(request, paramMap));
                returnPage = "/mobile/mob003/mob003_reg_chart";
            } else if (StringUtil.contains(oldRhtUrl, "mb008-init")) {
                // 调用私有服务,查询组织纬度 图表数据: "queryOrgChartData",
                modelMap.put("returnJson", queryOrgChartData(request, paramMap));
                returnPage = "/mobile/mob003/mob003_org_chart";
            } else {
                // 调用私有服务,查询 通用 图表数据: "queryChartData"
                modelMap.put("returnJson", queryChartData(request, paramMap));
                returnPage = "/mobile/mob003/mob003_chart";
            }
        } else {
            //TODO: 查不到 指标 报错
            modelMap.put("returnJson", false);
        }

        return new ModelAndView(returnPage, modelMap);
    }

    /**
     * 移动端查询指标数据接口
     * @param request
     * @param response
     */
    public void pointData(HttpServletRequest request, HttpServletResponse response) {
        String prjCd = request.getParameter("prjCd");
        /*
         * 获取 rhtCd => rhtId 来 获取指标面板 实际配置 url 参数
         */
        String rhtType = "1";
        String rhtId = request.getParameter("rhtCd");
        boolean success = true;
        String errmsg = "";
        String jsonRet = "{}";
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtId", rhtId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean result = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node")
            String oldRhtUrl = result.getStrValue("Node.rhtAttr01");
            String rhtUrl = oldRhtUrl.substring(oldRhtUrl.indexOf("?") + 1);
            /*
             * 分割 url 获取实际请求参数
             */
            Map<String, String> paramMap = new HashMap();
            //默认参数
            paramMap.put("prjCd", prjCd);
            paramMap.put("privilegeId", rhtId);
            if (StringUtil.isNotEmptyOrNull(rhtUrl)) {
                /*
                 * 拆分具体参数   eg: prjJobCd=hs_status_chart&chartType=pie
                 * prjJobCd: 指标编号
                 * prjJobGroup: 指标分组
                 * chartType: 图表类型
                 */
                String[] params = rhtUrl.split('&');
                if (params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        //  eg:  prjJobCd=hs_status_chart
                        if (StringUtil.isNotEmptyOrNull(params[i])) {
                            String[] temp = params[i].split("=");
                            paramMap.put(temp[0], temp[1]);
                        }
                    }
                }
            } else {
                //。
                success = false;
                errmsg = "指标配置有问题，没有rhtUrl，请联系管理员"
            }
            String chartType = paramMap.get("chartType")
            /*
             * 拿到最后请求参数 请求具体服务拿到 绘制图表的json数据
             * 判断不同服务要查询不同的 服务
             */
            if (StringUtil.contains(oldRhtUrl, "mb008-initReg")) {
                // 调用私有服务,查询区域纬度 图表数据: "queryRegChartData",
                jsonRet = queryRegChartData(request, paramMap);
                jsonRet = dealJsonRegChart(jsonRet, chartType)
            } else if (StringUtil.contains(oldRhtUrl, "mb008-init")) {
                // 调用私有服务,查询组织纬度 图表数据: "queryOrgChartData",
                jsonRet = queryOrgChartData(request, paramMap)
                jsonRet = dealJsonOrgChart(jsonRet, chartType)
            } else {
                // 调用私有服务,查询 通用 图表数据: "queryChartData"
                jsonRet = queryChartData(request, paramMap)
                jsonRet = dealJsonChart(jsonRet, chartType)
            }
        } else {
            success = false;
            errmsg = "指标不存在，请联系管理员"
        }
        if (success) {
            ResponseUtil.printAjax(response, jsonRet)
        } else {
            ResponseUtil.printAjax(response, """ {"success":${success},"errMsg":${errmsg}  """)
        }
    }

    String dealJsonRegChart(String jsonStr, String chartType) {
        com.alibaba.fastjson.JSONObject retJson = new com.alibaba.fastjson.JSONObject(16, true);
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        boolean success = jsonObject.getBoolean("success");
        if (!success) {
            return jsonStr;
        }
        retJson.put("success", success);
        retJson.put("errMsg", jsonObject.getString("errMsg"));
        def legendBean = getValue(jsonObject, "legend")
        //组织名称
        retJson.put("xNames", getValue(legendBean, "legend"))
        def yNameB = getValue(jsonObject, "xAxis")
        retJson.put("yNames", getValue(yNameB, "xAxis"))
        //组织id
        retJson.put("xValue", getValue(yNameB, "regIds"))
        JSONObject data = getValue(jsonObject, "data");
        JSONObject series = getValue(data, "series");
        String titleName = getValue(jsonObject, "titleText");
        retJson.put("jobData", new Data(getValue(series, "series"), retJson.get("xNames"))
                .getJsonArray(titleName, chartType))
        retJson.put("title", titleName)
        return retJson.toJSONString()
    }

    class Data {
        JSONArray jsonArrayData;
        JSONArray xNameArrayData;

        Data(JSONArray jsonArrayData, JSONArray xNameArrayData) {
            this.jsonArrayData = jsonArrayData
            this.xNameArrayData = xNameArrayData
        }

        //转换为符合要求的格式
        public JSONArray getJsonArray(String titleName, String chartType) {
            JSONArray retJsonArray = new JSONObject();
            JSONObject retObj = new JSONObject();
            JSONArray dataArray = new JSONArray();
            String type = "";
            for (JSONObject jsonObject : jsonArrayData) {
                JSONArray xDataArray = jsonObject.remove("data");
                String yName = jsonObject.remove("name");
                for (int i = 0; i < xDataArray.size(); i++) {
                    def xValue = xDataArray.get(i);
                    JSONObject dataBean = new JSONObject();
                    dataBean.put("xName", xNameArrayData.get(i));
                    //将name调整为yName
                    dataBean.put("yName", yName)
                    dataBean.putAll(jsonObject);
                    //存储节点 为yValue
                    dataBean.put("yValue", xValue);
                    if (i == 0) {
                        type = dataBean.get("chartType");
                    }
                    dataArray.add(dataBean)
                }
            }
            if (chartType == null) {
                chartType = type;
            }
            retObj.put("name", titleName)
            retObj.put("type", chartType)
            retObj.put("data", dataArray);
            retJsonArray.add(retObj)
            return retJsonArray;
        }
    }

    String dealJsonOrgChart(String jsonStr, String chartType) {
        com.alibaba.fastjson.JSONObject retJson = new com.alibaba.fastjson.JSONObject(16, true);
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        boolean success = jsonObject.getBoolean("success");
        if (!success) {
            return jsonStr;
        }
        retJson.put("success", success);
        retJson.put("errMsg", jsonObject.getString("errMsg"));
        def legendBean = getValue(jsonObject, "legend")
        //组织名称
        retJson.put("xNames", getValue(legendBean, "legend"))
        def yNameB = getValue(jsonObject, "xAxis")
        retJson.put("yNames", getValue(yNameB, "xAxis"))
        //组织id
        retJson.put("xValue", getValue(yNameB, "orgIds"))
        JSONObject data = getValue(jsonObject, "data");
        JSONObject series = getValue(data, "series");
        String titleName = getValue(jsonObject, "titleText");
        retJson.put("jobData", new Data(getValue(series, "series"), retJson.get("xNames"))
                .getJsonArray(titleName, chartType))
        retJson.put("title", titleName)
        return retJson.toJSONString()
    }

    private String dealJsonChart(String jsonStr, String chartType) {
        com.alibaba.fastjson.JSONObject retJson = new com.alibaba.fastjson.JSONObject(16, true);
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        boolean success = jsonObject.getBoolean("success");
        if (!success) {
            return jsonStr;
        }
        retJson.put("success", success);
        retJson.put("errMsg", jsonObject.getString("errMsg"));
        //处理符合格式的出参数据
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject reportJson = data.getJSONObject("Report");
        retJson.put("xNames", getValue(reportJson, "xAxis"))
        String yStrs = getValue(reportJson, "yNames");
        retJson.put("yNames", yStrs.split(","))
        Data dataB = new Data(getValue(reportJson, "series"), retJson.get("xNames"));
        String title = getValue(reportJson, "title")
        retJson.put("jobData", dataB.getJsonArray(yStrs, chartType))
        retJson.put("title", title)
        retJson.put("showName", getValue(reportJson, "showName"))
        retJson.put("minValue", getValue(reportJson, "minValue"))
        retJson.put("maxValue", getValue(reportJson, "maxValue"))
        return retJson.toJSONString();
    }

    private Object getValue(JSONObject reportJson, String key) {
        if (reportJson != null) {
            if (reportJson.containsKey(key)) {
                return reportJson.get(key);
            }
        }
        return null;
    }
    /**
     * 获取员工有权限的项目编号
     * @param httpServletRequest
     * @return
     */
    private String getStaffPrjCd(HttpServletRequest request) {
        String prjCd = request.getParameter("prjCd");
        if (StringUtil.isNotEmptyOrNull(prjCd)) {
            return prjCd;
        } else {
            // 获取工号可以访问的项目工程
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            // 查询条件
            XmlBean reqData = new XmlBean();
            reqData.setStrValue("SvcCont.staffCode", svcRequest.getReqStaffCd());
            reqData.setStrValue("SvcCont.isShowExp", "1");
            // 调用服务查询实体全部属性
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            // 入参没有指定项目编号返回第一个项目编号
            if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        prjCd = xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd");
                    }
                }
            }
            return prjCd;
        }
    }

    /**
     * 实体信息查询
     * @param request 请求域
     * @param queryConditions 查询条件
     * @param getTotalCount 查询总记录数
     * @return
     */
    private String entityQuery(HttpServletRequest request, JSONObject queryConditions, boolean getTotalCount) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        int pageSize = requestUtil.getIntParam("pageSize");
        int currentPage = requestUtil.getIntParam("currentPage");
        if (getTotalCount) {
            pageSize = 1;
            currentPage = 1;
        }
        if (0 == pageSize) {
            pageSize = 20;
        }
        if (0 == currentPage) {
            currentPage = 1;
        }
        // 服务端定义嘻嘻
        String entityName = queryConditions.get("entityName");
        String[] conditionFieldArr = queryConditions.get("conditionNames");
        if (conditionFieldArr == null) {
            conditionFieldArr = new ArrayList();
        }
        String[] conditionArr = queryConditions.get("conditions");
        if (conditionArr == null) {
            conditionArr = new ArrayList();
        }
        String[] conditionValueArr = queryConditions.get("conditionValues");
        if (conditionValueArr == null) {
            conditionValueArr = new ArrayList();
        }
        String[] resultFieldArr = queryConditions.get("resultFields");
        if (resultFieldArr == null) {
            resultFieldArr = new ArrayList();
        }
        String[] sortColumnArr = queryConditions.get("sortColumnArr");
        if (sortColumnArr == null) {
            sortColumnArr = new ArrayList();
        }
        String[] sortOrderArr = queryConditions.get("sortOrderArr");
        if (sortOrderArr == null) {
            sortOrderArr = new ArrayList();
        }
        // 增加实体可查询，排序字段的查询
        String cmptName = queryConditions.get("cmptName");

        //根据结果字段判断是否是跨实体查询
        boolean whetherCrossEntity = false;
        if (resultFieldArr.join(",").indexOf(".") != -1) {
            //是跨实体查询
            whetherCrossEntity = true;
        }
        // 排序信息
        Map<String, String> sortMap = new LinkedHashMap<String, String>();
        if (sortColumnArr != null) {
            for (int i = 0; i < sortColumnArr.length; i++) {
                if (StringUtil.isNotEmptyOrNull(sortColumnArr[i])) {
                    sortMap.put(sortColumnArr[i], sortOrderArr[i]);
                }
            }
        }

        /* 查询配置数据*/
        String prePath = "OpData.";
        // 增加查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", getStaffPrjCd(request));
        // 实际需要查询的字段，包含需要翻译的字段
        Set<String> resultFieldSet = new LinkedHashSet<String>();
        resultFieldSet.addAll(resultFieldArr);

        /* 查询数据*/
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", entityName);
        int addCount = 0;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                    || StringUtil.isEmptyOrNull(conditionArr[i])
                    || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
            } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
            }
        }

        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", pageSize);
        opData.setStrValue(prePath + "PageInfo.currentPage", currentPage);

        // 排序信息
        int sortCount = 0;
        for (Map.Entry<String, String> temp : sortMap.entrySet()) {
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount}].fieldName", temp.key);
            opData.setStrValue(prePath + "SortFields.SortField[${sortCount++}].sortOrder", temp.value);
        }

        // 权限查询增加参数
        String rhtRegId = requestUtil.getStrParam("rhtRegId");
        if (StringUtil.isNotEmptyOrNull(rhtRegId)) {
            opData.setStrValue(prePath + "ttRegId", requestUtil.getStrParam("rhtRegId"));
            opData.setStrValue(prePath + "regUseType", requestUtil.getStrParam("regUseType"));
        }

        // 需要查询的字段
        int tempCount = 0;
        for (String temp : resultFieldArr) {
            opData.setStrValue(prePath + "ResultFields.fieldName[${tempCount++}]", temp);
        }

        // 跨实体查询
        if (whetherCrossEntity) {
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "PRIVI_FILTER";
            }
            // 权限过滤组件
            if ("PRIVI_FILTER".equals(cmptName) || "TASK_PRIVI_FILTER".equals(cmptName)) {
                //过滤的组织 字段名
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "${entityName}.ttOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
                }
                // 通用查询，不传此参数==>通用分页查询， 传入此参数==>通用跨实体查询
                opData.setStrValue(prePath + "queryType", "2");
                opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
            } else if (StringUtil.isEqual(cmptName, "QUERY_ENTITY_PAGE_DATA")) {
                //兼容 前端指定执行通用查询 跨实体组件，无法拼接跨实体参数。
                opData.setStrValue(prePath + "queryType", "2");
            } else if ("true".equals(request.getParameter("addRightFilter"))) {
                opData.setStrValue(prePath + "queryType", "2");
                //过滤的组织 字段名
                String regOrgIdValue = request.getParameter("rhtOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrName", "${entityName}.ttOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
                opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));
                //过滤的区域 字段名
                String rhtRegIdValue = request.getParameter("rhtRegId");
                if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                    opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
                    opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                    opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
                }
            }
        } else {
            //不跨实体
            if (StringUtil.isEmptyOrNull(cmptName)) {
                cmptName = "FILTER_QUERY_ENTITY_PAGE_DATA";
            }
            // 权限过滤组件
            if ("FILTER_QUERY_ENTITY_PAGE_DATA".equals(cmptName)) {
                opData.setStrValue(prePath + "ttOrgId", request.getParameter("rhtOrgId"));
                opData.setStrValue(prePath + "ttRegId", request.getParameter("rhtRegId"));
                opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
            } else if ("true".equals(request.getParameter("addRightFilter"))) {
                opData.setStrValue(prePath + "ttOrgId", request.getParameter("rhtOrgId"));
                opData.setStrValue(prePath + "ttRegId", request.getParameter("rhtRegId"));
                opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
            }
        }

        // 调用组件获取服务数据
        svcRequest.addOp(cmptName, opData);
        SvcResponse svcResponse = query(svcRequest);

        // 返回结果
        JSONObject result = new JSONObject();
        result.put("isSuccess", svcResponse.isSuccess());
        result.put("errMsg", svcResponse.getErrMsg());
        // 处理正确返回的结果
        if (svcResponse.isSuccess()) {
            // 查询结果处理
            XmlBean queryResult = svcResponse.getFirstOpRsp(cmptName).getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                if (getTotalCount) {
                    // 只返回总记录数
                    result.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
                } else {
                    // 返回分页信息
                    result.put("currentPage", queryResult.getValue("OpResult.PageInfo.currentPage"));
                    result.put("totalPage", queryResult.getValue("OpResult.PageInfo.totalPage"));
                    result.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
                    // 分页数据
                    int headerCount = queryResult.getListNum("OpResult.PageHeader.Attr");
                    Map<String, String> resultPathOutMap = new LinkedHashMap<String>();
                    for (int i = 0; i < headerCount; i++) {
                        XmlBean tempRow = queryResult.getBeanByPath("OpResult.PageHeader.Attr[${i}]");
                        String attrEnName = tempRow.getStrValue("Attr.entityAttrNameEn");
                        String outName = attrEnName.replaceAll("^" + entityName + ".", "")
                        outName = outName.replaceAll("\\.", "_");
                        resultPathOutMap.put(attrEnName, outName);
                        if (StringUtil.isNotEmptyOrNull(tempRow.getStrValue("Attr.attrFrontSltRule"))) {
                            attrEnName = tempRow.getStrValue("Attr.entityAttrNameEn") + "_Name";
                            outName = attrEnName.replaceAll("^" + entityName + ".", "")
                            outName = outName.replaceAll("\\.", "_");
                            resultPathOutMap.put(attrEnName, outName);
                        }
                    }
                    // 返回查询数据
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    List<Map> resultList = new ArrayList<Map>();
                    for (int i = 0; i < rowCount; i++) {
                        XmlBean tempRow = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                        Map<String, String> rowOut = new LinkedHashMap<String, String>();
                        for (Map.Entry<String, String> tempEntry : resultPathOutMap.entrySet()) {
                            rowOut.put(tempEntry.value, tempRow.getStrValue("Row." + tempEntry.key));
                        }
                        resultList.add(rowOut);
                    }
                    // 返回结果集
                    result.put("queryList", resultList);
                }
            } else {
                if (getTotalCount) {
                    // 只返回总记录数
                    result.put("totalRecord", "0");
                } else {
                    result.put("queryList", new ArrayList());
                    // 返回分页信息
                    result.put("currentPage", "1");
                    result.put("totalPage", "0");
                    result.put("totalRecord", "0");
                }
            }
        }
        return result.toString();
    }

    /**
     * 获取统计指标数据
     * @param request
     * @param paramMap 参数对象
     * @return String 图表所需 json 数据
     */
    private String queryChartData(HttpServletRequest request, Map paramsMap) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        String orgIdPath = requestUtil.getStrParam("orgIdPath");
        String regIdPath = requestUtil.getStrParam("regIdPath");
        // 调用服务数据
        XmlBean reqData = new XmlBean();
//        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        int k = 0;
        for (Map.Entry tempItem : paramsMap) {
            if (tempItem.getKey().equals("prjJobCd")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobCd", tempItem.getValue());
            } else if (tempItem.getKey().equals("prjJobGroup")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobGroup", tempItem.getValue());
            } else if (tempItem.getKey().equals("regUseType")) {
                reqData.setStrValue("SvcCont.QuerySvc.regType", tempItem.getValue());
            } else {
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].name", tempItem.getKey());
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].value", tempItem.getValue());
                k++;
            }
        }
        String queryRegId = null;
        if (StringUtil.isNotEmptyOrNull(regIdPath)) {
            String[] tempArr = regIdPath.split("/");
            queryRegId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjRegId", queryRegId);
        }
        String regName = "";
        if (!StringUtil.isEmptyOrNull(queryRegId)) {
            // 获取区域名称
            SvcRequest regSvcRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            regSvcRequest.setValue("Request.SvcCont.PrjReg.regId", queryRegId);
            regSvcRequest.setValue("Request.SvcCont.PrjReg.prjCd", paramsMap.get("prjCd"));
            String regUseType = paramsMap.get("regUseType");
            if (StringUtil.isEmptyOrNull(regUseType)) {
                regUseType = "1";
            }
            regSvcRequest.setValue("Request.SvcCont.PrjReg.regUseType", regUseType);
            SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", regSvcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean tempBean = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
                if (tempBean != null) {
                    regName = tempBean.getStrValue("PrjReg.regName");
                }
            }
        }

        String queryOrgId = null;
        if (StringUtil.isNotEmptyOrNull(orgIdPath)) {
            String[] tempArr = orgIdPath.split("/");
            queryOrgId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjOrgId", queryOrgId);
        }
        String orgName = "";
        if (!StringUtil.isEmptyOrNull(queryOrgId)) {
            // 获取管理组织名称
            SvcRequest orgQueryRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            orgQueryRequest.setValue("Request.SvcCont.Org.Node.orgId", queryOrgId);
            orgQueryRequest.setValue("Request.SvcCont.Org.Node.prjCd", paramsMap.get("prjCd"));
            SvcResponse orgQueryResponse = callService("orgService", "queryNodeInfo", orgQueryRequest);
            if (orgQueryResponse.isSuccess()) {
                XmlBean tempBean = orgQueryResponse.getRspData().getBeanByPath("SvcCont.Org.Node");
                if (tempBean != null) {
                    orgName = tempBean.getStrValue("Node.orgName");
                }
            }
        }

        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            // 获取服务的输出参数进行转换，将List转换为XMLBean数组元素
            XmlBean tempResultBean = new XmlBean();
            int seriesNum = 0;
            if (null != rspData) {
                List<String> xAxisData = rspData.getValue("SvcCont.ReportData.xAxis");
                if (xAxisData != null) {
                    for (int i = 0; i < xAxisData.size(); i++) {
                        tempResultBean.setStrValue("Report.xAxis[${i}]", xAxisData.get(i));
                    }
                }
                // 处理序列内容
                seriesNum = rspData.getListNum("SvcCont.ReportData.series");
            }
            List<String> yNames = new ArrayList<>();
            for (int i = 0; i < seriesNum; i++) {
                XmlBean series = rspData.getBeanByPath("SvcCont.ReportData.series[${i}]");
                String yName = series.getStrValue("series.name")
                yNames.add(yName)
                tempResultBean.setStrValue("Report.series[${i}].name", yName);
                tempResultBean.setStrValue("Report.series[${i}].jobCd", series.getStrValue("series.jobCd"));
                tempResultBean.setStrValue("Report.series[${i}].valueSuffix", series.getStrValue("series.valueSuffix"));
                tempResultBean.setStrValue("Report.series[${i}].stackName", series.getStrValue("series.stackName"));
                tempResultBean.setStrValue("Report.series[${i}].chartType", series.getStrValue("series.chartType"));
                // 数据值
                List<String> tempList = series.getValue("series.data");
                if (tempList != null) {
                    for (int j = 0; j < tempList.size(); j++) {
                        tempResultBean.setStrValue("Report.series[${i}].data[${j}]", tempList.get(j));
                    }
                }
            }
            //添加y 名称集合
            tempResultBean.setStrValue("Report.yNames", StringUtils.join(yNames, ","))
            // 处理标题
            if (rspData != null) {
                tempResultBean.setStrValue("Report.title", rspData.getStrValue("SvcCont.ReportData.title"));
            }
            String displayName = regName;
            if (StringUtil.isNotEmptyOrNull(displayName) || StringUtil.isNotEmptyOrNull(orgName)) {
                displayName = displayName + "  ";
            }
            displayName = displayName + orgName;
            tempResultBean.setStrValue("Report.showName", displayName);
            // 最大值与最小值
            tempResultBean.setStrValue("Report.minValue", paramsMap.get("minValue"));
            tempResultBean.setStrValue("Report.maxValue", paramsMap.get("maxValue"));

            // 返回具体json结果。
            return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}", "data":${
                tempResultBean.toJson()
            } }""";
        } else {
            return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}"}""";
        }
    }

    /**
     * 按照管理组织统计数据
     * @param request 请求报文
     * @param paramMap 参数对象
     * @return String 图表所需 json 数据
     */
    private String queryOrgChartData(HttpServletRequest request, Map paramsMap) {
        RequestUtil requestUtil = new RequestUtil(request);
        //接收入参 xmlBean
        XmlBean reqData = new XmlBean();
        // 获取请求的orgId路径
        String orgIdPath = paramsMap.get("orgIdPath");
        int k = 0;
        for (Map.Entry tempItem : paramsMap) {
            if (tempItem.getKey().equals("prjJobCd")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobCd", tempItem.getValue());
            } else if (tempItem.getKey().equals("prjJobGroup")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobGroup", tempItem.getValue());
            } else if (tempItem.getKey().equals("regUseType")) {
                reqData.setStrValue("SvcCont.QuerySvc.regType", tempItem.getValue());
            } else {
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].name", tempItem.getKey());
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].value", tempItem.getValue());
                k++;
            }
        }
        // 判断编号是否为空
        String queryOrgId = null;
        if (StringUtil.isNotEmptyOrNull(orgIdPath)) {
            String[] tempArr = orgIdPath.split("/");
            queryOrgId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjOrgId", queryOrgId);
        }
        String orgName = "";
        if (!StringUtil.isEmptyOrNull(queryOrgId)) {
            // 获取管理组织名称
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            svcRequest.setValue("Request.SvcCont.Org.Node.orgId", queryOrgId);
            svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", paramsMap.get("prjCd"));
            SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean tempBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org.Node");
                if (tempBean != null) {
                    orgName = tempBean.getStrValue("Node.orgName");
                }
            }
        }
        //调用 旧服务获取 查询结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryOrgChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData().getBeanByPath("SvcCont.ReportDatas");
            if (rspData != null && rspData.getValue("ReportDatas.xAxis") instanceof List) {
                //获取多个维度的 数据
                int m = rspData.getListNum("ReportDatas.ReportData");
                //获取横坐标
                List xAxis = rspData.getValue("ReportDatas.xAxis");
                // 图例
                List legendList = new ArrayList();
                //大标题
                String titleText = "";
                // 各个维度的数据
                List<List> reportDataList = new ArrayList();
                List<Integer> orgIdList = new ArrayList();
                XmlBean seriesBean = new XmlBean();
                int num = 0;
                for (int i = 0; i < m; i++) {
                    // 以组织维度来绘制数据
                    String prjOrgName = rspData.getStrValue("ReportDatas.ReportData[${i}].prjOrgName");
                    if (StringUtil.isNotEmptyOrNull(orgName) && prjOrgName.startsWith(orgName)) {
                        String tempStr = prjOrgName.substring(orgName.length(), prjOrgName.length());
                        if (tempStr.endsWith("组")) {
                            tempStr = tempStr.substring(0, tempStr.length() - 1);
                        }
                        legendList.add(tempStr);
                    } else {
                        legendList.add(prjOrgName);
                    }
                    // 管理组织编号
                    int orgId = rspData.getIntValue("ReportDatas.ReportData[${i}].prjOrgId");
                    orgIdList.add(orgId);
                    //
                    titleText = rspData.getStrValue("ReportDatas.ReportData[${i}].series.name");
                    reportDataList.add((List) rspData.getValue("ReportDatas.ReportData[${i}].series.data"));
                }
                //
                List dataRowList = new ArrayList();
                if (reportDataList.size() > 0) {
                    for (int j = 0; j < reportDataList.get(0).size(); j++) {
                        List data = new ArrayList();
                        for (int l = 0; l < reportDataList.size(); l++) {
                            String dataStr = reportDataList.get(l).get(j);
                            data.add(dataStr);
                        }
                        dataRowList.add(data);
                    }
                }
                for (int i = 0; i < dataRowList.size(); i++) {
                    seriesBean.setStrValue("series.series[${num}].name", xAxis.get(i));
                    seriesBean.setStrValue("series.series[${num}].stackName", rspData.getStrValue("ReportDatas.ReportData[${i}].series.stackName"));
                    seriesBean.setStrValue("series.series[${num}].chartType", rspData.getStrValue("ReportDatas.ReportData[${i}].series.chartType"));
                    seriesBean.setValue("series.series[${num++}].data", dataRowList.get(i));
                }

                String jsonData = rspData.toJson();
                if (seriesBean != null) {
                    jsonData = seriesBean.toJson();
                }
                JSONObject xAxisJson = new JSONObject();
                xAxisJson.put("xAxis", xAxis);
                xAxisJson.put("orgIds", orgIdList);
                xAxisJson.put("orgName", orgName);
                JSONObject legendJson = new JSONObject();
                legendJson.put("legend", legendList);

                // 返回具体json结果。
                return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}", "data":${
                    jsonData.toString()
                },"orgIdPath":"${orgIdPath}","legend":${legendJson.toString()},"xAxis":${
                    xAxisJson.toString()
                },"titleText": "${titleText}" }""";

            } else {
                return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}"}""";
            }
        } else {
            return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}"}""";
        }
    }

    private String queryRegChartData(HttpServletRequest request, Map paramsMap) {
        RequestUtil requestUtil = new RequestUtil(request);
        //接收入参 xmlBean
        XmlBean reqData = new XmlBean();
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        // 获取请求的orgId路径
        String regIdPath = paramsMap.get("regIdPath");
        int k = 0;
        for (Map.Entry tempItem : paramsMap) {
            if (tempItem.getKey().equals("prjJobCd")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobCd", tempItem.getValue());
            } else if (tempItem.getKey().equals("prjJobGroup")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobGroup", tempItem.getValue());
            } else if (tempItem.getKey().equals("regUseType")) {
                reqData.setStrValue("SvcCont.QuerySvc.regType", tempItem.getValue());
            } else {
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].name", tempItem.getKey());
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].value", tempItem.getValue());
                k++;
            }
        }
        // 目标区域编号R
        String prjRegId = null;
        if (StringUtil.isNotEmptyOrNull(regIdPath)) {
            String[] tempArr = regIdPath.split("/");
            prjRegId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjRegId", prjRegId);
        }

        String regName = "";
        if (!StringUtil.isEmptyOrNull(prjRegId)) {
            // 获取区域名称
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            svcRequest.setValue("Request.SvcCont.PrjReg.regId", prjRegId);
            svcRequest.setValue("Request.SvcCont.PrjReg.prjCd", request.getParameter("prjCd"));
            String regUseType = request.getParameter("regUseType");
            if (StringUtil.isEmptyOrNull(regUseType)) {
                regUseType = "1";
            }
            svcRequest.setValue("Request.SvcCont.PrjReg.regUseType", regUseType);
            SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean tempBean = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
                if (tempBean != null) {
                    regName = tempBean.getStrValue("PrjReg.regName");
                }
            }
        }

        //调用 旧服务获取 查询结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryRegChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData().getBeanByPath("SvcCont.ReportDatas");
            if (rspData != null && rspData.getValue("ReportDatas.xAxis") instanceof List) {
                //获取多个维度的 数据
                int m = rspData.getListNum("ReportDatas.ReportData");
                //获取横坐标
                List xAxis = rspData.getValue("ReportDatas.xAxis");
                // 图例
                List legendList = new ArrayList();
                //大标题
                String titleText = "";
                // 各个维度的数据
                List<List> reportDataList = new ArrayList();
                List<Integer> regIdList = new ArrayList();
                XmlBean seriesBean = new XmlBean();
                int num = 0;
                for (int i = 0; i < m; i++) {
                    // 以组织维度来绘制数据
                    String prjRegName = rspData.getStrValue("ReportDatas.ReportData[${i}].prjRegName");
                    if (StringUtil.isNotEmptyOrNull(prjRegName) && prjRegName.startsWith(regName)) {
                        String tempStr = prjRegName.substring(regName.length(), prjRegName.length());
                        legendList.add(tempStr);
                    } else {
                        legendList.add(prjRegName);
                    }
                    // 管理区域变量
                    int tempRegId = rspData.getIntValue("ReportDatas.ReportData[${i}].prjRegId");
                    regIdList.add(tempRegId);
                    //
                    titleText = rspData.getStrValue("ReportDatas.ReportData[${i}].series.name");
                    reportDataList.add((List) rspData.getValue("ReportDatas.ReportData[${i}].series.data"));
                }
                //
                List dataRowList = new ArrayList();
                if (reportDataList.size() > 0) {
                    for (int j = 0; j < reportDataList.get(0).size(); j++) {
                        List data = new ArrayList();
                        for (int l = 0; l < reportDataList.size(); l++) {
                            String dataStr = reportDataList.get(l).get(j);
                            data.add(dataStr);
                        }
                        dataRowList.add(data);
                    }
                }

                for (int i = 0; i < dataRowList.size(); i++) {
                    seriesBean.setStrValue("series.series[${num}].name", xAxis.get(i));
                    seriesBean.setStrValue("series.series[${num}].stackName",
                            rspData.getStrValue("ReportDatas.ReportData[${i}].series.stackName"));
                    seriesBean.setStrValue("series.series[${num}].chartType",
                            rspData.getStrValue("ReportDatas.ReportData[${i}].series.chartType"));
                    seriesBean.setValue("series.series[${num++}].data", dataRowList.get(i));
                }

                String jsonData = rspData.toJson();
                if (seriesBean != null) {
                    jsonData = seriesBean.toJson();
                }
                JSONObject xAxisJson = new JSONObject();
                xAxisJson.put("xAxis", xAxis);
                xAxisJson.put("regIds", regIdList);
                xAxisJson.put("regName", regName);
                JSONObject legendJson = new JSONObject();
                legendJson.put("legend", legendList);
                return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}", "data":${
                    jsonData.toString()
                },"regIdPath":"${regIdPath}","legend":${legendJson.toString()},"xAxis":${
                    xAxisJson.toString()
                },"titleText": "${titleText}"}""";
            } else {
                return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}"}""";
            }
        } else {
            return """{"success": ${svcResponse.isSuccess()}, "errMsg":"${svcResponse.getErrMsg()}"}""";
        }
    }

    /**
     * 获取权限资源节点信息
     * @param request
     * @param rhtId
     * @return
     */
    private getTreeNodeInfo(HttpServletRequest request, String rhtId) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.rhtId", rhtId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
            return nodeInfoBean;
        } else {
            return null;
        }
    }

    /**
     * 返回消息头改为json，避免通过中间层访问数据过长而导致乱码
     *
     * @param response
     * @param printObj
     */
    public static void printAjaxJSON(HttpServletResponse response, Object printObj) {
        response.setContentType("text/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(printObj);
        } catch (IOException var4) {
            LOG.error("Error:Cannot create PrintWriter Object !");
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }
}