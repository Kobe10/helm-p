import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.util.zip.MbillZipUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房  房产信息展示页面
 */
class ph003 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", request.getParameter("hsId"));
        modelMap.put("regId", request.getParameter("regId"));
        modelMap.put("prjCd", request.getParameter("prjCd"));
        // 页面返回结果展示
        // 获取院落的地址信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        // 基本信息 BaseInfo
        reqData.setStrValue("OpData.entityName", "HouseInfo");
        reqData.setStrValue("OpData.entityKey", request.getParameter("hsId"));
        reqData.setStrValue("OpData.ResultField.fieldName[0]", "buildId");
        reqData.setStrValue("OpData.ResultField.fieldName[1]", "hsAddr");
        reqData.setStrValue("OpData.ResultField.fieldName[2]", "hsAddrNo");
        reqData.setStrValue("OpData.ResultField.fieldName[3]", "hsOwnerPersons");
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean buildInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY");
            String buildId = buildInfo.getStrValue("Operation.OpResult.buildId");
            modelMap.put("buildId", buildId);
            modelMap.put("hsAddr", buildInfo.getStrValue("Operation.OpResult.hsAddr"));
            modelMap.put("hsAddrNo", buildInfo.getStrValue("Operation.OpResult.hsAddrNo"));
            modelMap.put("hsOwnerPersons", buildInfo.getStrValue("Operation.OpResult.hsOwnerPersons"));
            // 获取院落的区域信息
            svcRequest = RequestUtil.getSvcRequest(request);
            reqData = new XmlBean();
            // 获取上级区域编号
            reqData.setStrValue("OpData.entityName", "HouseInfo");
            /* 增加查询条件 */
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "buildId");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", buildId);
            reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            // 排序字段
            reqData.setStrValue("OpData.SortFields.SortField[0].fieldName", "hsOwnerPersons");
            reqData.setStrValue("OpData.Conditions.SortField[0].sortOrder", "asc");
            // 设置分页大小
            // 定义到处的数据大小
            reqData.setStrValue("OpData.PageInfo.pageSize", "100");
            reqData.setStrValue("OpData.PageInfo.currentPage", "0");

            /* 需要获取的字段*/
            reqData.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
            reqData.setStrValue("OpData.ResultFields.fieldName[1]", "hsOwnerPersons");
            svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);

            reqData = new XmlBean();
            reqData.setStrValue("OpData.entityName", "RegInfo");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "prjBuldId");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", buildId);
            reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            reqData.setStrValue("OpData.Conditions.Condition[1].fieldName", "prjCd");
            reqData.setStrValue("OpData.Conditions.Condition[1].fieldValue", svcRequest.getPrjCd());
            reqData.setStrValue("OpData.Conditions.Condition[1].operation", "=");
            reqData.setStrValue("OpData.ResultFields.fieldName[0]", "upRegId");
            reqData.setStrValue("OpData.ResultFields.fieldName[1]", "regId");
            svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                List<XmlBean> result = svcResponse.getAllOpRsp("QUERY_ENTITY_CMPT");
                XmlBean buildDatas = result.get(0)
                        .getBeanByPath("Operation.OpResult");
                int count = buildDatas.getListNum("OpResult.PageData.Row");
                List<XmlNode> hsList = new ArrayList<XmlNode>(count);
                for (int i = 0; i < count; i++) {
                    hsList.add(buildDatas.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                }
                modelMap.put("hsList", hsList);
                XmlBean regInfo = result.get(1).getBeanByPath("Operation.OpResult.PageData");
                String upRegId = regInfo.getStrValue("PageData.Row[0].upRegId");
                String regId = regInfo.getStrValue("PageData.Row[0].regId");
                modelMap.put("upRegId", upRegId);
                modelMap.put("regId", regId);
            }
        }
        // 是否显示评估信息
        Map<String, String> tempCfg = getCfgCollection(request, "PRJ_FORM", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String updatePgFormCd = tempCfg.get("updatePg");
        if (StringUtil.isEmptyOrNull(updatePgFormCd)) {
            modelMap.put("showPg", false);
        } else {
            modelMap.put("showPg", true);
        }

        return new ModelAndView("/eland/ph/ph003/ph003", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initBase(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        Map<String, String> map = getCfgCollection(request, "PRJ_FORM", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String formCd = map.get("inputFormCd");
        //查询指定房产的 交房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        paramData.setStrValue("OpData.formCd", formCd);
        // 生成报表参数
        paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", hsId);
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "showFlag");
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", "true");
        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse pageResponse = query(svcRequest);
        if (pageResponse.isSuccess()) {
            XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
            String pageStr = opResult.getStrValue("Operation.OpResult.resultParam");
            String operDefCode = opResult.getStrValue("Operation.OpResult.OperationDefs.operDefCode");
            //返回展示页面字符串
            modelMap.put("article", pageStr);
            modelMap.put("operDefCode", operDefCode);
        }
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ph/ph003/ph00301_base", modelMap);
    }

    /*-------------------------------------基本信息--0301-----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initPos(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String regId = request.getParameter("regId");
        String method = request.getParameter("method");
        String prjCd = request.getParameter("prjCd");
        modelMap.put("hsId", hsId);
        modelMap.put("regId", regId);
        modelMap.put("prjCd", prjCd);

        XmlBean reqData = new XmlBean();
        //基本信息 BaseInfo
        reqData.setStrValue("OpData.HouseInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_BASE_INFO", reqData);
        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_HS_BASE_INFO");
            modelMap.put("oldHsBean", rspData.getBeanByPath("Operation.HouseInfo").getRootNode());
            String buildId = rspData.getStrValue("Operation.HouseInfo.buildId");
            //修改查看原有地图,展示院落边界
            String buildPoints = "";
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            // 查询需要的字段
            opData.setStrValue("OpData.ResultField.fieldName[0]", "buildPosition");
            // 调用服务查询数据
            SvcRequest svcRequest1 = RequestUtil.getSvcRequest(request);
            svcRequest1.addOp("QUERY_ENTITY_PROPERTY", opData);
            SvcResponse svcResponse1 = query(svcRequest1);
            if (svcResponse1.isSuccess()) {
                XmlBean resultData = svcResponse1.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (resultData != null) {
                    buildPoints = resultData.getStrValue("OpResult.buildPosition");
                }
            }
            modelMap.put("buildPoints", buildPoints);
        }

        //---------------------------------------------------配置的地图参数-------------------------------------------
        SvcRequest svcReqCfg = RequestUtil.getSvcRequest(request);
        XmlBean reqDataCfg = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.";
        reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "geoserver_map");
        reqDataCfg.setValue(nodePath + "SysCfg[0].isCached", "true");
        reqDataCfg.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
        svcReqCfg.setReqData(reqDataCfg);
        // 调用服务
        SvcResponse svcRspCfg = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcReqCfg);
        if (svcRspCfg.isSuccess()) {
            XmlBean cfgBean = svcRspCfg.getRspData().getBeanByPath("SvcCont.SysCfgs");
            int cfgValueNum = svcRspCfg.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            String baselayerUrl = "";
            String layerName1 = "";
            String layerName2 = "";
            String lonLatLevel = "";
            String mapBounds = "";
            for (int i = 0; i < cfgValueNum; i++) {
                //获取该配置参数的 key ， value
                String val = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueCd");
                String relValue = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueName");
                if (StringUtil.isEqual("MAP_LAYER_URL", val)) {
                    baselayerUrl = relValue;
                } else if (StringUtil.isEqual("MAP_LAYER_NAME1", val)) {
                    layerName1 = relValue;
                } else if (StringUtil.isEqual("MAP_LAYER_NAME2", val)) {
                    layerName2 = relValue;
                } else if (StringUtil.isEqual("MAP_LON_LAT_LEVEL", val)) {
                    lonLatLevel = relValue;
                } else if (StringUtil.isEqual("MAP_BOUNDS", val)) {
                    mapBounds = relValue;
                }
            }
            //查询原有地图坐标，返回页面绘制地图
            modelMap.put("baselayerUrl", baselayerUrl);
            modelMap.put("layerName1", layerName1);
            modelMap.put("layerName2", layerName2);
            modelMap.put("lonLatLevel", lonLatLevel);
            modelMap.put("mapBounds", mapBounds);
        }
        return new ModelAndView("/eland/ph/ph003/ph00301_pos", modelMap);
    }

    /*------------------------房产基本信息、管理信息 保存  --0301 -----------------------------------*/
    /**
     * 房产信息保存  保存基本信息  管理信息
     * @param request
     * @param response
     */
    public void saveBaseInfo(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        boolean isAdd = false;
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = "\${hsId}";
            isAdd = true;
        }
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Operation[0].";
        //拼接调用组件名称
        reqData.setStrValue(nodePath + "OpName", "SAVE_HS_BASE_INFO");
        //基本信息 BaseInfo
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsId", hsId);
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsCd", request.getParameter("hsCd"));
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsAddr", request.getParameter("hsAddr"));
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsAddrNo", request.getParameter("hsAddrNo"));
        String hsFullAddr = request.getParameter("hsAddr") + request.getParameter("hsAddrNo");
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsFullAddr", hsFullAddr);

        reqData.setStrValue(nodePath + "OpData.HouseInfo.buildId", request.getParameter("buildId"));
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRoomNum", request.getParameter("hsRoomNum"));
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsSlfRoom", request.getParameter("hsSlfRoom"));
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsStatus", request.getParameter("hsStatus"));
        reqData.setStrValue(nodePath + "OpData.HouseInfo.hsNote", request.getParameter("hsNote"));

        if (isAdd) {
            // 管理信息  组件
            nodePath = "SvcCont.Operation[1].";
            //拼接调用组件名称
            reqData.setStrValue(nodePath + "OpName", "SAVE_HS_TT_MNG_INFO");
            //管理信息 manageInfo
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsId", hsId);
            reqData.setStrValue(nodePath + "OpData.HouseInfo.ttOrgId", request.getParameter("ttOrgId"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.ttRegId", request.getParameter("ttRegId"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.ttCompanyId", request.getParameter("ttCompanyId"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.ttMainTalk", request.getParameter("ttMainTalk"));

            // 人员  组件
            nodePath = "SvcCont.Operation[2].";
            //拼接调用组件名称
            reqData.setStrValue(nodePath + "OpName", "SAVE_HS_PERSON_INFO");
            //管理信息 manageInfo
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsId", hsId);
            String[] perList = request.getParameterValues("personId");
            if (perList != null) {
                for (int i = 0; i < perList.length; i++) {
                    reqData.setStrValue(nodePath + "OpData.PersonInfos.personId[${i}]", NumberUtil.getIntFromObj(perList[i]));
                }
            }

            // 产权 组件
            nodePath = "SvcCont.Operation[3].";
            //拼接调用组件名称
            reqData.setStrValue(nodePath + "OpName", "SAVE_HS_OWNER_INFO");
            //管理信息 manageInfo
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsId", hsId);
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRegType", request.getParameter("hsRegType"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRegBuildSize", request.getParameter("hsRegBuildSize"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRegUseSize", request.getParameter("hsRegUseSize"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRegNameDis", request.getParameter("hsRegNameDis"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRegName", request.getParameter("hsRegName"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRegCertyNum", request.getParameter("hsRegCertyNum"));
            reqData.setStrValue(nodePath + "OpData.HouseInfo.hsRegLandNum", request.getParameter("hsRegLandNum"));
            //拼接产权人
            String[] hsOwnerList = request.getParameterValues("HsOwnerPersonId");
            String[] certyNumList = request.getParameterValues("HsOwnerCertyNum");
            String[] certyPercentList = request.getParameterValues("HsOwnerCertyPercent");
            int m = 0;
            if (hsOwnerList != null) {
                for (int i = 0; i < perList.length; i++) {
                    if (hsOwnerList[i] == null) {
                        continue;
                    }
                    reqData.setStrValue(nodePath + "OpData.HsOwners.HsOwner[${m}].personId", NumberUtil.getIntFromObj(hsOwnerList[i]));
                    reqData.setStrValue(nodePath + "OpData.HsOwners.HsOwner[${m}].certyNum", certyNumList[i]);
                    reqData.setStrValue(nodePath + "OpData.HsOwners.HsOwner[${m}].certyPercent", certyPercentList[i]);
                    m++;
                }
            }
        }

        svcRequest.setReqData(reqData);
        //调用最新的服务
        SvcResponse svcResponse = SvcUtil.transaction(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        if (result && isAdd) {
            hsId = svcResponse.getFirstOpRsp("SAVE_HS_BASE_INFO").getStrValue("OpResult.HouseInfo.hsId");
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", hsId:"${hsId}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /*-------------------------------------人员信息--0303-----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initPer(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        // 调用服务
        ModelMap modelMap = new ModelMap();
        XmlBean personOpData = new XmlBean();
        personOpData.setStrValue("OpData.PersonInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_PERSON_INFO", personOpData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //获取户籍人员信息结果
            XmlBean personBean = svcResponse.getFirstOpRsp("QUERY_HS_PERSON_INFO");
            if (personBean != null) {
                List hsPersList = new ArrayList();
                int perCount = personBean.getListNum("Operation.OpResult.Persons.Person");
                for (int i = 0; i < perCount; i++) {
                    hsPersList.add(personBean.getBeanByPath("Operation.OpResult.Persons.Person[${i}]").getRootNode());
                }
                modelMap.put("hsPersonBean", hsPersList);
            }
        }
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ph/ph003/ph00303", modelMap);
    }

    /*-------------------------------------产权信息---0304----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initHRt(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");

        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        modelMap.put("regId", regId);
        modelMap.put("prjCd", prjCd);

        //根据不同方法给定不同跳转页面
        XmlBean operationData = new XmlBean();
        //基本信息 BaseInfo
        operationData.setStrValue("OpData.HouseInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_OWNER_INFO", operationData);
        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getRspData();
            if (rspBean != null) {
                modelMap.put("hsOwnerBean", rspBean.getBeanByPath("SvcCont.Operation[0].HouseInfo").getRootNode());
                int hsOwnersNum = rspBean.getListNum("SvcCont.Operation[0].HouseInfo.HsOwners.HsOwner");
                //拼接多个人的  产权证号  一块展示
                String ownerCerty = "";
                for (int i = 0; i < hsOwnersNum; i++) {
                    String temp = rspBean.getStrValue("SvcCont.Operation[0].HouseInfo.HsOwners.HsOwner[${i}].ownerCerty");
                    if (StringUtil.isNotEmptyOrNull(temp)) {
                        ownerCerty = temp + "," + ownerCerty;
                    }
                }
                if (ownerCerty.length() > 0) {
                    ownerCerty = ownerCerty.substring(0, ownerCerty.length() - 1);
                }
                String ownerCertyDocs = "";
                for (int i = 0; i < hsOwnersNum; i++) {
                    String temp = rspBean.getStrValue("SvcCont.Operation[0].HouseInfo.HsOwners.HsOwner[${i}].ownerCertyDocIds");
                    if (StringUtil.isNotEmptyOrNull(temp)) {
                        ownerCertyDocs = temp + "," + ownerCertyDocs;
                    }
                }
                if (ownerCertyDocs.length() > 0) {
                    ownerCertyDocs = ownerCertyDocs.substring(0, ownerCertyDocs.length() - 1);
                }
                modelMap.put("ownerCerty", ownerCerty);
                modelMap.put("ownerCertyDocs", ownerCertyDocs);

            }
            //循环获取产权人
            int hsOwnerNum = rspBean.getListNum("SvcCont.Operation[0].HouseInfo.HsOwners.HsOwner");

        }
        return new ModelAndView("/eland/ph/ph003/ph00304", modelMap);
    }

    /*--------------------------------------其他信息 合并展示---03_initOtherInfo--------------------------------------------*/
    /**
     * 将以下所有面板信息合并展示
     */
    public ModelAndView initOtherInfo(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        String hsId = requestUtil.getStrParam("hsId");
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);

        String[] groupNames = ["basicInfo", "personInfo", "businessInfo", "affiliatedInfo", "leasesInfo", "bzxHsInfo"];
        String nodePath = "OpData.";
        for (int i = 0; i < groupNames.length; i++) {
            XmlBean hsInfoBean = new XmlBean();
            hsInfoBean.setStrValue(nodePath + "entityName", "HouseInfo");
            hsInfoBean.setStrValue(nodePath + "groupName", groupNames[i]);
            hsInfoBean.setStrValue(nodePath + "entityKey", hsId);
            svcRequest.addOp("QUERY_ENTITY_GROUP", hsInfoBean);
        }
        //通用分组 查询 人员信息 附属信息  经营信息
        SvcResponse svcResponse = query(svcRequest);
        XmlBean houseBean = new XmlBean();
        List rspBeanList = svcResponse.getAllOpRsp("QUERY_ENTITY_GROUP");
        for (int i = 0; i < rspBeanList.size(); i++) {
            XmlBean allFind = new XmlBean();
            allFind = rspBeanList.get(i).getBeanByPath("Operation.OpResult.HouseInfo");
            if (allFind != null) {
                houseBean.merge(allFind);
            }
        }

        svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean dopData = new XmlBean();
        // 查询实体
        dopData.setStrValue("OpData.entityName", "HouseInfo");
        dopData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        dopData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        dopData.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        //得到购房人的ID,调用服务
        dopData.setStrValue("OpData.ResultFields.fieldName[0]", "isPbHs");
        dopData.setStrValue("OpData.ResultFields.fieldName[1]", "pbLevel");
        dopData.setStrValue("OpData.ResultFields.fieldName[2]", "pbDetail");
        dopData.setStrValue("OpData.ResultFields.fieldName[3]", "pbPlan");
        dopData.setStrValue("OpData.ResultFields.fieldName[4]", "hsPbType");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", dopData);
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean houseResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (houseResult != null) {
                int count = houseResult.getListNum("PageData.Row");
                String isPhHs = houseResult.getStrValue("PageData.Row[0].isPbHs");
                String pbLevel = houseResult.getStrValue("PageData.Row[0].pbLevel_Name");
                String pbDetail = houseResult.getStrValue("PageData.Row[0].pbDetail");
                String pbPlan = houseResult.getStrValue("PageData.Row[0].pbPlan");
                modelMap.put("isPhHs", isPhHs);
                modelMap.put("pbLevel", pbLevel);
                modelMap.put("pbDetail", pbDetail);
                modelMap.put("pbPlan", pbPlan);

                String phType = "";
                for (int i = 0; i < count; i++) {
                    String hsPbType_Name = houseResult.getStrValue("PageData.Row[${i}].hsPbType_Name");
                    phType += hsPbType_Name + "、";
                }
                phType = phType.substring(0, phType.length() - 1)
                modelMap.put("phType", phType);
            }
        }
        modelMap.put("houseInfo", houseBean.getRootNode());
        return new ModelAndView("/eland/ph/ph003/ph00301_showOtherInfo", modelMap);
    }
    /**
     * 修改 大病人数
     * @param request
     * @param response
     */
    public void saveIllNum(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String seriousIll = request.getParameter("seriousIll");
        String seriousIllDocIds = request.getParameter("seriousIllDocIds");

        boolean result = false;
        String errMsg = "";
        if (StringUtil.isEmptyOrNull(hsId)) {
            errMsg = "保存失败，请刷新后重新编辑！";
        } else {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "HouseInfo");
            opData.setStrValue("OpData.EntityData.hsId", hsId);
            opData.setStrValue("OpData.EntityData.seriousIll", seriousIll);
            opData.setStrValue("OpData.EntityData.seriousIllDocIds", seriousIllDocIds);
            svcRequest.addOp("SAVE_ENTITY", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            result = svcResponse.isSuccess();
            if (!result) {
                errMsg = svcResponse.getErrMsg();
            }
        }


        String jsonStr = """{success:${result}, errMsg:"${errMsg}", hsId:"${hsId}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /*-------------------------------------房屋经营状况---0310----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initJY(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");

        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        modelMap.put("regId", regId);
        modelMap.put("prjCd", prjCd);

        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //通用入参 BaseInfo
        operationData.setStrValue(nodePath + "entityName", "HouseInfo");
        operationData.setStrValue(nodePath + "groupName", "businessInfo");
        operationData.setStrValue(nodePath + "entityKey", hsId);
        // 查询管理信息
        svcRequest.addOp("QUERY_ENTITY_GROUP", operationData);
        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_GROUP");
            if (rspBean != null) {
                modelMap.put("bizBean", rspBean.getBeanByPath("Operation.OpResult.HouseInfo").getRootNode());
            }
        }
        return new ModelAndView("/eland/ph/ph003/ph00310", modelMap);
    }

    /*-------------------------------------人员状况统计---0311----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initChartPer(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");

        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        modelMap.put("regId", regId);
        modelMap.put("prjCd", prjCd);

        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //通用入参 BaseInfo
        operationData.setStrValue(nodePath + "entityName", "HouseInfo");
        operationData.setStrValue(nodePath + "groupName", "personInfo");
        operationData.setStrValue(nodePath + "entityKey", hsId);
        // 查询管理信息
        svcRequest.addOp("QUERY_ENTITY_GROUP", operationData);
        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_GROUP");
            if (rspBean != null) {
                modelMap.put("personBean", rspBean.getBeanByPath("Operation.OpResult.HouseInfo").getRootNode());
            }
        }
        return new ModelAndView("/eland/ph/ph003/ph00311", modelMap);
    }

    /*-------------------------------------房屋附属信息---0312----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initHsFs(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");

        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        modelMap.put("regId", regId);
        modelMap.put("prjCd", prjCd);

        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //通用入参 BaseInfo
        operationData.setStrValue(nodePath + "entityName", "HouseInfo");
        operationData.setStrValue(nodePath + "groupName", "affiliatedInfo");
        operationData.setStrValue(nodePath + "entityKey", hsId);
        // 查询管理信息
        svcRequest.addOp("QUERY_ENTITY_GROUP", operationData);
        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_GROUP");
            if (rspBean != null) {
                XmlBean houseInfo = rspBean.getBeanByPath("Operation.OpResult.HouseInfo");
                modelMap.put("affiliatedBean", rspBean.getBeanByPath("Operation.OpResult.HouseInfo").getRootNode());
            }
        }
        return new ModelAndView("/eland/ph/ph003/ph00312", modelMap);
    }

    /*-------------------------------------腾退管理信息---0313----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initPG_back(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.entityKey", hsId);
        opData.setStrValue("OpData.ResultField.fieldName[0]", "pgStatus");
        opData.setStrValue("OpData.ResultField.fieldName[1]", "pgDocs");
        opData.setStrValue("OpData.ResultField.fieldName[2]", "pgCompany");
        opData.setStrValue("OpData.ResultField.fieldName[3]", "basePrice");
        opData.setStrValue("OpData.ResultField.fieldName[4]", "pgMoney1");
        opData.setStrValue("OpData.ResultField.fieldName[5]", "pgMoney2");
        opData.setStrValue("OpData.ResultField.fieldName[6]", "transferStatus");
        opData.setStrValue("OpData.ResultField.fieldName[7]", "transferDocs");
        opData.setStrValue("OpData.ResultField.fieldName[8]", "withHold");
        opData.setStrValue("OpData.ResultField.fieldName[9]", "newWithHold");
        opData.setStrValue("OpData.ResultField.fieldName[10]", "afterChangeNewWithHold");
        opData.setStrValue("OpData.ResultField.fieldName[11]", "afterChangePgMoney2");
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        if (svcResponse.isSuccess()) {
            XmlBean houseInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");

            if (houseInfo != null) {
                modelMap.put("pgStatus", houseInfo.getStrValue("OpResult.pgStatus"));
                modelMap.put("pgDocs", houseInfo.getStrValue("OpResult.pgDocs"));
                modelMap.put("pgCompany", houseInfo.getStrValue("OpResult.pgCompany"));
                modelMap.put("basePrice", houseInfo.getStrValue("OpResult.basePrice"));
                modelMap.put("pgMoney1", houseInfo.getStrValue("OpResult.pgMoney1"));
                modelMap.put("pgMoney2", houseInfo.getStrValue("OpResult.pgMoney2"));
                modelMap.put("transferStatus", houseInfo.getStrValue("OpResult.transferStatus"));
                modelMap.put("transferDocs", houseInfo.getStrValue("OpResult.transferDocs"));
                modelMap.put("withHold", houseInfo.getStrValue("OpResult.withHold"));
                modelMap.put("newWithHold", houseInfo.getStrValue("OpResult.newWithHold"));
                modelMap.put("afterChangeNewWithHold", houseInfo.getStrValue("OpResult.afterChangeNewWithHold"));
                modelMap.put("afterChangePgMoney2", houseInfo.getStrValue("OpResult.afterChangePgMoney2"));
            }
        }
        if ("edit".equals(method)) {
            return new ModelAndView("/eland/ph/ph003/ph00301_pgedit", modelMap);
        } else {
            return new ModelAndView("/eland/ph/ph003/ph00301_pg", modelMap);
        }
    }

    /**
     * 查询评估信息展示 调用表单
     * @return
     */
    public ModelAndView initPG(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        //获取系统通用表单编码----取得 修改评估数据表单 编码
        Map map = getCfgCollection(request, "PRJ_FORM", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        //调用 获取表单 信息 返回页面
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        paramData.setStrValue("OpData.formCd", map.get("updatePg"));
        // 生成报表参数
        paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", hsId);
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "showFlag");
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", "show");

        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse pageResponse = query(svcRequest);
        if (pageResponse.isSuccess()) {
            XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
            List operaList = new ArrayList();
            int operNum = opResult.getListNum("Operation.OpResult.OperationDefs.OperationDef");
            for (int i = 0; i < operNum; i++) {
                operaList.add(opResult.getBeanByPath("Operation.OpResult.OperationDefs.OperationDef[${i}]").getRootNode());
            }
            String pageStr = opResult.getStrValue("Operation.OpResult.resultParam");
            String operDefCode = opResult.getStrValue("Operation.OpResult.OperationDefs.operDefCode");

            //返回展示页面字符串
            modelMap.put("article", pageStr);
            modelMap.put("operaList", operaList);
            modelMap.put("operDefCode", operDefCode);
        }
        return new ModelAndView("/eland/ph/ph003/ph00301_pg", modelMap);
    }


    public ModelAndView savePG(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        modelMap.put("hsId", hsId);

        String pgStatus = request.getParameter("pgStatus");
        String pgDocs = request.getParameter("pgDocs");
        String pgCompany = request.getParameter("pgCompany");
        String basePrice = request.getParameter("basePrice");
        String pgMoney1 = request.getParameter("pgMoney1");
        String pgMoney2 = request.getParameter("pgMoney2");
        String transferStatus = request.getParameter("transferStatus");
        String transferDocs = request.getParameter("transferDocs");
        String withHold = request.getParameter("withHold");
        String newWithHold = request.getParameter("newWithHold");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.EntityData.hsId", hsId);
        opData.setStrValue("OpData.EntityData.pgStatus", pgStatus);
        opData.setStrValue("OpData.EntityData.pgDocs", pgDocs);
        opData.setStrValue("OpData.EntityData.pgCompany", pgCompany);
        opData.setStrValue("OpData.EntityData.basePrice", basePrice);
        opData.setStrValue("OpData.EntityData.pgMoney1", pgMoney1);
        opData.setStrValue("OpData.EntityData.pgMoney2", pgMoney2);
        opData.setStrValue("OpData.EntityData.transferStatus", transferStatus);
        opData.setStrValue("OpData.EntityData.transferDocs", transferDocs);
        opData.setStrValue("OpData.EntityData.withHold", withHold);
        opData.setStrValue("OpData.EntityData.newWithHold", newWithHold);
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}", hsId:"${hsId}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /*-------------------------------------腾退管理信息---0313----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initTtMng(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");

        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        modelMap.put("regId", regId);
        modelMap.put("prjCd", prjCd);

        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //通用入参 BaseInfo
        operationData.setStrValue(nodePath + "entityName", "HouseInfo");
        operationData.setStrValue(nodePath + "groupName", "leasesInfo");
        operationData.setStrValue(nodePath + "entityKey", hsId);
        // 查询管理信息
        svcRequest.addOp("QUERY_ENTITY_GROUP", operationData);
        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_GROUP");
            if (rspBean != null) {
                modelMap.put("houseMng", rspBean.getBeanByPath("Operation.OpResult.HouseInfo").getRootNode());
            }
        }
        // 获取中介公司信息
        SvcRequest svcRequest1 = RequestUtil.getSvcRequest(request);
        // 获取中介公司
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.CmpExt.prjCd", prjCd);
        svcRequest1.setReqData(reqData);
        SvcResponse svcResponse1 = SvcUtil.callSvc("cmpExtService", "queryExtCmpsByPrjCd", svcRequest1);
        if (svcResponse1.isSuccess()) {
            XmlBean cmpRspData = svcResponse1.getRspData();
            //返回查询结果
            int cmpNum = cmpRspData.getListNum("SvcCont.ExtCmps.extCmp");
            Map cmpMap = new HashMap();
            for (int i = 0; i < cmpNum; i++) {
                cmpMap.put(cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpId"), cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpName"));
            }
            modelMap.put("cmpMap", cmpMap);
        }
        return new ModelAndView("/eland/ph/ph003/ph00313", modelMap);
    }

    /**
     * 查询展示签约信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initTt(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 请求信息
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String hsCtId = "";
        /**
         * 调用组件查询配置参数 内容
         * 推送页面 schemeTypeMap: 存储schemeType, schemeName
         * 下面查询使用 schemeNoteMap: 存储schemeType, schemeNote
         */
        Map<String, String> schemeTypeMap = new LinkedHashMap<>();
        Map<String, String> schemeNoteMap = new LinkedHashMap<>();
        XmlBean reqDataCfg = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.";
        reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "SCHEME_TYPE");
        reqDataCfg.setValue(nodePath + "SysCfg[0].isCached", "true");
        reqDataCfg.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
        svcRequest.setReqData(reqDataCfg);
        // 调用服务
        SvcResponse svcRspCfg = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcRspCfg.isSuccess()) {
            XmlBean cfgBean = svcRspCfg.getRspData().getBeanByPath("SvcCont.SysCfgs");
            int cfgValueNum = svcRspCfg.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            for (int i = 0; i < cfgValueNum; i++) {
                //获取该配置参数的 key ， value
                String val = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueCd");
                String relValue = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueName");
                String notes = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].notes");
                schemeTypeMap.put(val, relValue);
                schemeNoteMap.put(val, notes);
            }
        }
        modelMap.put("schemeTypeMap", schemeTypeMap);

        //调用组件查询hsCtId
        svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "HouseInfo");
        reqData.setStrValue("OpData.queryType", "2");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo.HsCtInfo.hsCtId");
        reqData.setStrValue("OpData.ResultFields.fieldName[1]", "HouseInfo.HsCtInfo.ctType");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", reqData);
        //签约信息  选房信息
        XmlBean contractReqData = new XmlBean();
        contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);
        SvcResponse svcRsp = query(svcRequest);
        if (svcRsp.isSuccess()) {
            XmlBean hsCtBean = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (hsCtBean != null) {
                hsCtId = hsCtBean.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.hsCtId");
                //安置方式
                String ctType = hsCtBean.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctType");
                modelMap.put("ctType", ctType);
            }

            /**
             * 根据不同签约类型，查询不同签约信息，
             * 查询不同类型 对应的款项信息
             * 查询属性从 参数note 里取 schemeNoteMap
             * hsBcJsMap 签约信息map
             * hsBcJsItemsMap 签约款项map
             */
            Map hsBcJsMap = new HashMap();
            Map hsBcJsItemsMap = new HashMap();
            Iterator<String> it = schemeTypeMap.keySet().iterator();
            while (it.hasNext()) {
                svcRequest = RequestUtil.getSvcRequest(request);
                reqData = new XmlBean();
                String schemeTypeTem = it.next();
                reqData.setStrValue("OpData.hsId", hsId);
                reqData.setStrValue("OpData.schemeType", schemeTypeTem);
                reqData.setStrValue("OpData.hsCtId", hsCtId);
                svcRequest.addOp("QUERY_CTITEM_VALUE_CMPT", reqData);
                SvcResponse svcResponse = query(svcRequest);
                XmlBean HsBcJe = new XmlBean();
                if (svcResponse.isSuccess()) {
                    XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_CTITEM_VALUE_CMPT").getBeanByPath("Operation.OpResult");
                    if (rspData != null && rspData.getBeanByPath("OpResult.RuleResult") != null) {
                        HsBcJe = rspData.getBeanByPath("OpResult.RuleResult.HsBcJe");
                        //取出HsBcJe节点下的Items重新组装成list，推送前端循环
                        int num = HsBcJe.getListNum("HsBcJe.Items.Item");
                        List<XmlNode> ctItems = new ArrayList<XmlNode>();
                        for (int i = 0; i < num; i++) {
                            ctItems.add(HsBcJe.getBeanByPath("HsBcJe.Items.Item[${i}]").getRootNode());
                        }
                        hsBcJsItemsMap.put(schemeTypeTem, ctItems);
                        String errMsg = rspData.getStrValue("OpResult.errorMsg");
                        if (StringUtil.isNotEmptyOrNull(errMsg)) {
                            XmlBean errMsgBean = new XmlBean(errMsg);
                            Map errMap = new HashMap();
                            String tempPath = "ExceptionInfos.ExceptionInfo";
                            int errNum = errMsgBean.getListNum(tempPath);
                            for (int i = 0; i < errNum; i++) {
                                errMap.put(errMsgBean.getStrValue(tempPath + "[${i}].subjectId")
                                        + "_" + schemeTypeTem, errMsgBean.getStrValue(tempPath + "[${i}].message"));
                            }
                            modelMap.put("errMap", errMap);
                        }

                    }
                    /**
                     * 查询不同种类签约状态，签约时间
                     */
                    String schemeJsonStr = schemeNoteMap.get(schemeTypeTem);
                    if (StringUtil.isNotEmptyOrNull(schemeJsonStr)) {
                        JSONObject jsonObject = JSONObject.fromObject(schemeJsonStr);
                        String ctStatusField = jsonObject.get("attr_status");
                        String ctDateField = jsonObject.get("attr_date");
                        svcRequest = RequestUtil.getSvcRequest(request);
                        XmlBean opData = new XmlBean();
                        opData.setStrValue("OpData.queryType", "2");
                        opData.setStrValue("OpData.entityName", "HouseInfo");
                        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "HouseInfo.hsId");
                        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
                        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
                        if (StringUtil.isNotEmptyOrNull(ctStatusField)) {
                            opData.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo.HsCtInfo." + ctStatusField);
                        }
                        if (StringUtil.isNotEmptyOrNull(ctDateField)) {
                            opData.setStrValue("OpData.ResultFields.fieldName[1]", "HouseInfo.HsCtInfo." + ctDateField);
                        }
                        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                        // 调用服务
                        svcResponse = query(svcRequest);
                        if (svcResponse.isSuccess()) {
                            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
                            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                                String ctStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctStatusField + "_Name");
                                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctDateField);
                                HsBcJe.setStrValue("HsBcJe.ctStatus", ctStatus);
                                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                                }
                                HsBcJe.setStrValue("HsBcJe.ctDate", ctDate);
                            }
                        }
                    }
                } else {
                    HsBcJe = new XmlBean();
                    HsBcJe.setStrValue("HsBcJe.errMsg", svcResponse.getErrMsg());
                }
                hsBcJsMap.put(schemeTypeTem, HsBcJe.getRootNode());
            }
            modelMap.put("hsBcJsItemsMap", hsBcJsItemsMap);
            modelMap.put("hsBcJsMap", hsBcJsMap);

            // 所有的人员信息
            Set<String> personIds = new HashSet<String>();
            /**
             * 获取选房信息
             */
            XmlBean hsCtInfo = svcRsp.getFirstOpRsp("QUERY_HS_CT_INFO").getBeanByPath("Operation.OpResult.HsCtInfo");
            if (hsCtInfo != null) {
                int num = hsCtInfo.getListNum("HsCtInfo.chooseHs");
                List<XmlNode> chooseHs = new ArrayList<XmlNode>();
                for (int i = 0; i < num; i++) {
                    XmlBean chooseBean = hsCtInfo.getBeanByPath("HsCtInfo.chooseHs[${i}]");
                    chooseHs.add(chooseBean.getRootNode());
                    // 人员信息
                    personIds.add(chooseBean.getStrValue("chooseHs.buyPersonId"));
                    personIds.add(chooseBean.getStrValue("chooseHs.buyPsWtIds"));
                    String livePsIds = chooseBean.getStrValue("chooseHs.gtJzPsIds");
                    if (StringUtil.isNotEmptyOrNull(livePsIds)) {
                        personIds.addAll(livePsIds.split(","))
                    }
                }
                modelMap.put("chooseHs", chooseHs);
            }

            // 调用服务获取所有的其他人员信息
            personIds.remove("");
            if (personIds.size() > 0) {
                String personIdsStr = personIds.join(",");
                // 调用服务获取人员信息
                svcRequest = RequestUtil.getSvcRequest(request);
                /* 获取可用房源区域列表 */
                // 增加等级
                XmlBean opData = new XmlBean();
                String prePath = "OpData.";
                opData.setStrValue(prePath + "entityName", "PersonInfo");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "personId");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "(${personIdsStr})");
                opData.setStrValue(prePath + "Conditions.Condition[0].operation", "in");
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "prjCd");
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "${prjCd}");
                opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
                opData.setStrValue(prePath + "PageInfo.pageSize", "100");
                opData.setStrValue(prePath + "PageInfo.currentPage", "1");
                // 需要查询的字段
                opData.setStrValue(prePath + "ResultFields.fieldName[0]", "personId");
                opData.setStrValue(prePath + "ResultFields.fieldName[1]", "personName");
                opData.setStrValue(prePath + "ResultFields.fieldName[2]", "personCertyNum");
                opData.setStrValue(prePath + "ResultFields.fieldName[3]", "personCertyDocIds");
                opData.setStrValue(prePath + "ResultFields.fieldName[4]", "personTelphone");
                // 增加查询
                svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                SvcResponse personQueryResp = query(svcRequest);
                if (personQueryResp.isSuccess()) {
                    XmlBean queryResult = personQueryResp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                    Map<String, XmlNode> personsData = new HashMap<String, XmlNode>();
                    // 处理查询结果，返回json格式数据
                    if (queryResult != null) {
                        int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                        List<Map<String, String>> newHsAreaList = new ArrayList<Map<String, String>>();
                        for (int i = 0; i < rowCount; i++) {
                            XmlBean row = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                            String personId = row.getStrValue("Row.personId");
                            personsData.put(personId, row.getRootNode());
                        }
                        modelMap.put("personsData", personsData);
                    }
                }
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ph/ph003/ph00305", modelMap);
    }

    /**
     * 打开赠送详情
     * @param request
     * @param response
     * @return
     */
    public ModelAndView openGive(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 请求信息
        String hsCtGiveId = request.getParameter("hsCtGiveId");
        String prjCd = request.getParameter("prjCd");
        //签约信息  选房信息
        XmlBean contractReqData = new XmlBean();
        contractReqData.setStrValue("OpData.entityName", "HsCtGiveInfo");
        contractReqData.setStrValue("OpData.entityKey", hsCtGiveId);
        svcRequest.addOp("QUERY_ALL_ENTITY", contractReqData);
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        if (svcResponse.isSuccess()) {
            // 房产基本信息
            XmlBean GiveInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.HsCtGiveInfo");
            modelMap.put("giveInfo", GiveInfo.getRootNode());
        }
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ph/ph003/ph00305_giveDetail", modelMap);
    }

    /**
     *  保存腾退信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView saveTt(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 请求信息
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String hsCtId = request.getParameter("hsCtId");
        String ctDate = request.getParameter("ctDate");
        if (StringUtil.isEmptyOrNull(ctDate)) {
            ctDate = DateUtil.toStringYmdHmsWthHS(DateUtil.getSysDate());
        }
        int ctDateNum = ctDate.length();
        if (8 <= ctDateNum && ctDateNum < 17) {
            int temp = 17 - ctDateNum;
            for (int i = 0; i < temp; i++) {
                ctDate = ctDate + "0";
            }

        } else if (ctDateNum < 8) {
            ctDate = DateUtil.toStringYmdHmsWthHS(DateUtil.getSysDate());
        }
        String ctNote = request.getParameter("ctNote");
        String moneyNames = request.getParameter("moneyNames");
        String buildCtLimitDate = request.getParameter("buildCtLimitDate");
        String buildCtLimitDateBak = request.getParameter("buildCtLimitDateBak");
        String buildId = request.getParameter("buildId");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 签约信息保存
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        opData.setStrValue("OpData.HsCtInfo.ctDate", ctDate);
        opData.setStrValue("OpData.HsCtInfo.ctNote", ctNote);
        opData.setStrValue("OpData.toDb", "0");
        opData.setStrValue("OpData.toContext", "0");
        svcRequest.addOp("SAVE_HOUSE_CT_INFO", opData);
        // 签约款
        opData = new XmlBean();
        opData.setStrValue("OpData.HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue("OpData.HsCtInfo.ctStatus", "2");
        String[] moneyNamesArr = moneyNames.split(",");
        for (String temp : moneyNamesArr) {
            opData.setStrValue("OpData.HsCtInfo.${temp}", request.getParameter(temp));
        }
        svcRequest.addOp("SAVE_CT_MONEY", opData);
        // 整院签约时间不一致，调整签约时间
        if (!StringUtil.isEqual(buildCtLimitDate, buildCtLimitDateBak)) {
            opData = new XmlBean();
            opData.setStrValue("OpData.BuildInfo.buildId", buildId);
            opData.setStrValue("OpData.BuildInfo.buildCtLimitDate", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(buildCtLimitDate)));
            svcRequest.addOp("SAVE_BUILD_BASE_INFO", opData);
        }
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /*-------------------------------------进度跟踪 ---0309-----------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initWM(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = request.getParameter("prjCd");
        modelMap.put("hsId", request.getParameter("hsId"));
        modelMap.put("regId", request.getParameter("regId"));
        modelMap.put("prjCd", prjCd);
        modelMap.put("staffId", svcRequest.getReqStaff());

        XmlBean reqData = new XmlBean();
        boolean haveRht;
        // 判断是否有管理日志权限
        reqData.setValue("SvcCont.rightType", "1");
        reqData.setValue("SvcCont.rhtCd", "hsrd_super_rht");
        reqData.setValue("SvcCont.prjCd", prjCd);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffHaveRht", svcRequest);
        if (svcResponse.isSuccess()) {
            haveRht = Boolean.valueOf(svcResponse.getRspData().getStrValue("SvcCont.haveRight"));
        } else {
            haveRht = false;
        }
        // 没有管理权限，则判断是否有查看他人日志权限
        if (!haveRht){
            svcRequest = RequestUtil.getSvcRequest(request);
            reqData = new XmlBean();
            reqData.setValue("SvcCont.rightType", "1");
            reqData.setValue("SvcCont.rhtCd", "hsrd_other_rht");
            reqData.setValue("SvcCont.prjCd", prjCd);
            svcRequest.setReqData(reqData);
            svcResponse = SvcUtil.callSvc("staffService", "queryStaffHaveRht", svcRequest);
            if (svcResponse.isSuccess()) {
                haveRht = Boolean.valueOf(svcResponse.getRspData().getStrValue("SvcCont.haveRight"));
            }
        }

        modelMap.put("haveRht", haveRht);
        return new ModelAndView("/eland/ph/ph003/ph00309", modelMap);
    }

    /*-----------------------------------房产附件---0315----------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "100");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        //只查询状态为 1 的，
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");

        opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "prjCd");
        opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", prjCd);
        opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
        // 增加查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();

        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                Map<String, List<String>> dataMap = new LinkedHashMap<String, List<String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docId");
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                    if ("fileUpLoad".equals(tempField02)) {
                        tempField02 = "其他";
                    }
                    List<String> tempItem = dataMap.get(tempField02);
                    if (tempItem == null) {
                        tempItem = new ArrayList<String>();
                        dataMap.put(tempField02, tempItem);
                    }
                    tempItem.add(tempField01);
                }
                List<Map<String, String>> attachTypeSummary = new ArrayList<Map<String, String>>();
                for (Map.Entry<String, List<String>> entry : dataMap) {
                    List<String> temp = entry.getValue();
                    attachTypeSummary.add(["name": entry.getKey(), "count": entry.getValue().size(), "ids": temp.join(",")])
                }

                modelMap.put("attachTypeSummary", attachTypeSummary);
            }
        }
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ph/ph003/ph00315", modelMap);
    }

    /*-------------------------------------待办任务---0302----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initTD(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String hsId = request.getParameter("hsId");
        // 请求报文
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.busiKey", "HouseInfo_" + hsId);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_RU_TSK_BY_BUSI_KEY", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_RU_TSK_BY_BUSI_KEY").getBeanByPath("Operation.OpResult");
            if (opResult != null) {
                int taskCount = opResult.getListNum("OpResult.WfExecute.RuTasks.RuTask");
                List<XmlNode> toDoTask = new ArrayList<XmlNode>(taskCount);
                for (int i = 0; i < taskCount; i++) {
                    toDoTask.add(opResult.getBeanByPath("OpResult.WfExecute.RuTasks.RuTask[${i}]").getRootNode());
                }
                modelMap.put("toDoTask", toDoTask);
            }
        }
        modelMap.put("hsId", hsId);

        String staffName = "";
        XmlBean resultBean = new XmlBean();
        // 查询登陆人姓名******************************************************
        svcRequest = RequestUtil.getSvcRequest(request);
        String nodePath = "SvcCont.Staff.";
        resultBean.setValue(nodePath + "staffId", svcRequest.getReqStaff());
        svcRequest.setReqData(resultBean);
        svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
            staffName = staffBean.getStrValue("Staff.StaffName");
        }
        // 查询谈话记录信息*******************************************************
        svcRequest = RequestUtil.getSvcRequest(request);
        resultBean = new XmlBean();
        String prePath = "PrjTalkRecord.";
        resultBean.setStrValue(prePath + "relObjId", hsId);
        resultBean.setStrValue(prePath + "prjCd", request.getParameter("prjCd"));
        // 调用服务获取房屋的基本信息
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "HouseInfo");
            opData.setStrValue("OpData.entityKey", hsId);
            opData.setStrValue("OpData.ResultField.fieldName[0]", "hsOwnerPersons");
            opData.setStrValue("OpData.ResultField.fieldName[1]", "ttMainTalk");
            // 调用服务查询数据
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            // 调用服务
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (opResult != null) {
                    String ttMainTalk = opResult.getStrValue("OpResult.ttMainTalk");
                    if (StringUtil.isEmptyOrNull(ttMainTalk)) {
                        ttMainTalk = staffName;
                    }
                    resultBean.setStrValue(prePath + "doRecordPerson", ttMainTalk);
                    resultBean.setStrValue(prePath + "recordToPerson", opResult.getStrValue("OpResult.hsOwnerPersons"));
                    resultBean.setStrValue(prePath + "startTime", DateUtil.toStringYmdHmsWthH(DateUtil.getSysDate()));
                }
            }
        }
        modelMap.put("talkInfo", resultBean.getRootNode());
        return new ModelAndView("/eland/ph/ph003/ph00302", modelMap);
    }

    /*-------------------------------------发起任务初始化---0302_initiateTask----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initiateTask(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String initPer = svcRequest.getReqStaffIdInt().toString();
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String hsId = request.getParameter("hsId");
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ph/ph003/ph00302_initTask", modelMap);
    }

    /*-------------------------------------确认发出任务---0302 cfmSendTask----------------------------------------------*/
    /**
     * 确认发出任务
     * @param request
     * @param response
     */
    public void cfmSendTask(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 请求信息
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String taskName = request.getParameter("taskName");
        String taskStartDate = request.getParameter("taskStartDate");
        String taskDetails = request.getParameter("taskDetails");
        String targetPer = request.getParameter("targetPer");
        String taskEndDate = request.getParameter("taskEndDate");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 签约信息保存
        XmlBean opData = new XmlBean();
        String prePath = "OpData.WfExecute.";
        opData.setStrValue(prePath + "procDefKey", "REMIND_TASK_DEF_KEY");
        opData.setStrValue(prePath + "busiKey", "HouseInfo_${hsId}");
        opData.setStrValue(prePath + "procStUser", svcRequest.getReqStaffCd());
        opData.setStrValue(prePath + "Variables.TaskName", taskName);
        opData.setStrValue(prePath + "Variables.TaskAssignee", targetPer);
        opData.setStrValue(prePath + "Variables.Description", taskDetails);
        opData.setStrValue(prePath + "Variables.DueTime", taskEndDate);

        svcRequest.addOp("SAVE_START_PROC_BY_DEF_KEY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String jsonData = "";
        if (svcResponse.isSuccess()) {
            String procInsId = svcResponse.getFirstOpRsp("SAVE_START_PROC_BY_DEF_KEY")
                    .getStrValue("Operation.OpResult.WfExecute.procInsId");
            jsonData = "procInsId: ${procInsId}";
        }
        ResponseUtil.printSvcResponse(response, svcResponse, jsonData);
    }

    /*-------------------------------------处理任务初始化---0302 completeTask----------------------------------------------*/
    /**
     * 初始化处理任务
     * @param request
     * @param response
     */
    public ModelAndView completeTask(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String initPer = svcRequest.getReqStaffIdInt().toString();
        ModelMap modelMap = new ModelMap();
//        String hsId = request.getParameter("hsId");
        String taskId = request.getParameter("taskId");
        // 请求报文
        XmlBean opData = new XmlBean();
        opData.setStrValue("SvcCont.WF.ActiveTaskId", taskId);
        svcRequest.setReqData(opData);
        SvcResponse svcResponse = callService("wfExecuteService", "queryTaskInfoById", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            if (rspData != null) {
                //出参不够。处理任务滞后写
                modelMap.put("taskInfo", rspData.getBeanByPath("SvcCont.WF").getRootNode());
            }
        }
        modelMap.put("initPer", initPer);
        modelMap.put("taskId", taskId);
        modelMap.put("doStaffCd", svcRequest.getReqStaffCd());
        return new ModelAndView("/eland/ph/ph003/ph00302_completeTask", modelMap);
    }

    /*-------------------------------------任务初始化---0302 cfmCompleteTask--saveCompleteTask--------------------------------------------*/
    /**
     * 初始化处理任务
     * @param request
     * @param response
     */
    public ModelAndView cfmCompleteTask(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String initPer = svcRequest.getReqStaffIdInt().toString();
        ModelMap modelMap = new ModelMap();
//        String hsId = request.getParameter("hsId");
        String taskId = request.getParameter("taskId");
        String varPreRoot = "SvcCont.WF.Variables.";
        // 请求报文
        XmlBean opData = new XmlBean();
        opData.setStrValue("SvcCont.WF.WfExecute.taskId", taskId);
        opData.setStrValue("SvcCont.WF.WfExecute.comment", request.getParameter("taskCompleteDetails"));
        opData.setStrValue(varPreRoot + "variable[0].name", "DueDate");
        opData.setStrValue(varPreRoot + "variable[0].value", DateUtil.toStringYmdHmsWthH(DateUtil.getSysDate()));

        svcRequest.setReqData(opData);
        SvcResponse svcResponse = callService("wfExecuteService", "saveCompleteTask", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /*-------------------------------------历史任务---0302_historyTask ----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView historyTask(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("/eland/ph/ph003/ph00302_historyTask");
    }

    /**
     * 导出居民附件信息
     * @param request 请求路径
     * @param response 响应结果
     */
    public void exportFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "100");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        //只查询状态为 1 的，
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");

        opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "prjCd");
        opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", prjCd);
        opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");
        // 增加类型限制
        String docTypeNames = request.getParameter("docTypeName");
        if (StringUtil.isNotEmptyOrNull(docTypeNames)) {
            docTypeNames = java.net.URLDecoder.decode(docTypeNames, "utf-8");
            String[] temp = docTypeNames.split(",");
            List<String> valueList = new ArrayList<String>();
            for (String tempItem : temp) {
                if (StringUtil.isNotEmptyOrNull(tempItem)) {
                    valueList.add("'" + tempItem + "'");
                }
            }
            if (valueList.size() > 0) {
                opData.setStrValue(prePath + "Conditions.Condition[4].fieldName", "docTypeName");
                opData.setStrValue(prePath + "Conditions.Condition[4].fieldValue", "(${valueList.join(',')})");
                opData.setStrValue(prePath + "Conditions.Condition[4].operation", "in");
            }
        }

        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docPath");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docTypeName");
        // 需要排序的字段
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "docTypeName");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 增加查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        // 查询产权人名称
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "entityKey", hsId);
        opData.setStrValue(prePath + "ResultField.fieldName", "hsOwnerPersons");
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);

        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            // 获取产权人名称
            String hsOwnerPersons = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getStrValue("Operation.OpResult.hsOwnerPersons");

            // 获取查询文档处理结果
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("PageData.Row");
                Map<String, List<String>> zipFileMap = new LinkedHashMap<String, List<String>>();
                List<String> zipFile = new ArrayList<String>(rowCount);
                for (int i = 0; i < rowCount; i++) {
                    String docPath = queryResult.getStrValue("PageData.Row[${i}].docPath");
                    String docTypeName = queryResult.getStrValue("PageData.Row[${i}].docTypeName");
                    String toDir = docTypeName;
                    List<String> typeFile = zipFileMap.get(toDir);
                    File localFile = ServerFile.getFile(docPath);
                    if (localFile.exists()) {
                        if (typeFile == null) {
                            typeFile = new ArrayList<String>();
                        }
                        typeFile.add(localFile.getPath());
                        zipFileMap.put(toDir, typeFile);
                    }
                }
                File tempFile = null;
                String downloadExt = ".zip";
                String downloadFileName = hsOwnerPersons;
                if (zipFileMap.size() > 0) {
                    tempFile = ServerFile.getFile("temp/", UUID.randomUUID().toString() + downloadExt);
                    MbillZipUtil.addFilesToZip(tempFile.getPath(), zipFileMap);
                } else {
                    downloadExt = ".txt";
                    downloadFileName = "文件不存在";
                    tempFile = ServerFile.createFile("temp/", UUID.randomUUID().toString() + downloadExt);
                }
                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, "", downloadFileName + downloadExt, tempFile, request.getHeader("USER-AGENT"));
                // 删除临时文件
                tempFile.delete();
            }
        }
    }

    /**
     * 初始化批量修改
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initBEdit(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        String opCode = requestUtil.getStrParam("opCode");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String houseIds = requestUtil.getStrParam("houseIds");

        // 调用服务获取后台数据
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", ("BAT_EDIT_HS_DEF_" + opCode).toUpperCase());
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", "true");
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", prjCd);
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        Map<String, String> item = new LinkedHashMap<String, String>();
        XmlBean queryResult = new XmlBean();
        if (svcResponse.isSuccess()) {
            int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            int temp = 0;
            for (int i = 0; i < count; i++) {
                item.put(svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + temp + "].valueCd"),
                        svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + temp++ + "].valueName"))
            }
        }

        // 房产信息
        XmlBean opData = new XmlBean();
        // 返回处理结果
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.prjCd", prjCd);
        svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT").getBeanByPath("Operation.OpResult.Entity");
            if (entity != null) {
                // 处理所有分组
                int groupCount = entity.getListNum("Entity.Groups.Group");
                XmlBean attrBean = null;
                int temp = 0;
                for (int p = 0; p < item.size(); p++) {
                    queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp + "].attrNameEn",
                            item.keySet().toArray()[p]);
                    queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp + "].attrNameCh",
                            item.get(item.keySet().toArray()[p]));
                    for (int i = 0; i < groupCount; i++) {
                        XmlBean group = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                        if (attrBean == null) {
                            // 获取属性
                            int attrCount = group.getListNum("Group.Attrs.Attr");
                            for (int k = 0; k < attrCount; k++) {
                                XmlBean tempAttrBean = group.getBeanByPath("Group.Attrs.Attr[${k}]");
                                String attrEnName = tempAttrBean.getStrValue("Attr.entityAttrNameEn");
                                if (StringUtil.isEqual(item.keySet().toArray()[p], attrEnName)) {
                                    //找到属性后获取前端检索规则节点拼报文
                                    String attrFrontSltRule = tempAttrBean.getStrValue("Attr.attrFrontSltRule");
                                    if (StringUtil.contains(attrFrontSltRule, "REG(")) {
                                        attrFrontSltRule = "REG('" + attrFrontSltRule.substring(5, attrFrontSltRule.length() - 2) + "')";
                                    }
                                    if (StringUtil.contains(attrFrontSltRule, "CODE(")) {
                                        queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp + "].attrSltType", "CODE");
                                        queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp++ + "].attrRelItemCd",
                                                attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2));
                                        break;
                                    } else {
                                        queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp++ + "].attrSltType", attrFrontSltRule);
                                        break;
                                    }
                                }
                            }
                        } else if (attrBean != null) {
                            break;
                        }
                    }
                }
            }
        }
        int attrCount = queryResult.getListNum("Operation.OpResult.EntityAttr");
        List<XmlNode> queryConditionInfo = new ArrayList<XmlNode>(attrCount);
        for (int i = 0; i < attrCount; i++) {
            XmlBean tempBean = queryResult.getBeanByPath("Operation.OpResult.EntityAttr[${i}]");
            queryConditionInfo.add(tempBean.getRootNode());
        }
        modelMap.put("queryConditionInfo", queryConditionInfo);
        modelMap.put("houseIds", houseIds);
        return new ModelAndView("/eland/ph/ph003/ph003_bedit", modelMap);
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    public ModelAndView bEdit(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 房源ID列表
        String[] houseIds = requestUtil.getStrParam("houseIds").split(",");
        String[] fieldNameArr = requestUtil.getStrParam("fieldNames").split(",");
        String[] fieldValues = requestUtil.getStrParam("fieldValues").split(",");
        for (String houseId : houseIds) {
            if (StringUtil.isEmptyOrNull(houseId)) {
                continue;
            }
            XmlBean rowData = new XmlBean();
            rowData.setStrValue("OpData.HouseInfo.hsId", houseId);
            for (int i = 0; i < fieldNameArr.length; i++) {
                rowData.setStrValue("OpData.HouseInfo." + fieldNameArr[i], fieldValues[i]);
            }
            // 调用服务
            svcRequest.addOp("SAVE_PBH_CMPT", rowData);
        }

        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
}
