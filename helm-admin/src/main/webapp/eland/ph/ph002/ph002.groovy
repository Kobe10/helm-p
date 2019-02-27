import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.excel.ExcelWriter
import com.shfb.oframe.core.util.exception.BusinessException
import com.shfb.oframe.core.util.report.ReportData
import com.shfb.oframe.core.util.report.ReportFactory
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.util.zip.MbillZipUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import com.shfb.oframe.sysmg.repo.vo.SvcDoc
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 建筑详细信息展示页面
 */
class ph002 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回结果展示
        ModelMap modelMap = new ModelMap();
        // 获取院落的地址信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        // 基本信息 BaseInfo
        reqData.setStrValue("OpData.entityName", "BuildInfo");
        reqData.setStrValue("OpData.entityKey", request.getParameter("buldId"));
        reqData.setStrValue("OpData.ResultField.fieldName[0]", "buildNo");
        reqData.setStrValue("OpData.ResultField.fieldName[1]", "ttRegId_upRegId");
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean buildInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY");
            String buildNo = buildInfo.getStrValue("Operation.OpResult.buildNo");
            String upRegId = buildInfo.getStrValue("Operation.OpResult.ttRegId_upRegId");
            modelMap.put("buildNo", buildNo);
            modelMap.put("ttUpRegId", upRegId);
            // 获取院落的区域信息
            svcRequest = RequestUtil.getSvcRequest(request);
            reqData = new XmlBean();
            // 获取上级区域编号
            reqData.setStrValue("OpData.entityName", "RegInfo");
            /* 增加查询条件 */
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "upRegId");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", upRegId);
            reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            reqData.setStrValue("OpData.Conditions.Condition[1].fieldName", "regUseType");
            reqData.setStrValue("OpData.Conditions.Condition[1].fieldValue", "1");
            reqData.setStrValue("OpData.Conditions.Condition[1].operation", "=");
            // 排序字段
            reqData.setStrValue("OpData.SortFields.SortField[0].fieldName", "regName");
            reqData.setStrValue("OpData.Conditions.SortField[0].sortOrder", "asc");
            /* 需要获取的字段*/
            reqData.setStrValue("OpData.ResultFields.fieldName[0]", "prjBuldId");
            reqData.setStrValue("OpData.ResultFields.fieldName[1]", "regName");
            svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);

            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean buildDatas = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT")
                        .getBeanByPath("Operation.OpResult.PageData");
                int count = buildDatas.getListNum("PageData.Row");
                List<XmlNode> regBuilList = new ArrayList<XmlNode>(count);
                for (int i = 0; i < count; i++) {
                    regBuilList.add(buildDatas.getBeanByPath("PageData.Row[${i}]").getRootNode());
                }
                modelMap.put("regBuilList", regBuilList);
            }
        }
        modelMap.put("buildingRegId", request.getParameter("regId"));
        modelMap.put("buildId", request.getParameter("buldId"));
        return new ModelAndView("/eland/ph/ph002/ph002", modelMap);
    }

    /*----------------------------------房产信息初始化-----------------------------------------*/
    /**
     * 初始化进度跟踪模板展示
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initHs(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", request.getParameter("buildId"));
        modelMap.put("buildingRegId", request.getParameter("buildingRegId"));
        // 调用服务获取房产信息汇总数据
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.ParamInfo.Param[0].";
        reqData.setStrValue(nodePath + "name", "buldId");
        reqData.setStrValue(nodePath + "value", request.getParameter("buildId"));
        nodePath = "SvcCont.ParamInfo.Param[1].";
        reqData.setStrValue(nodePath + "name", "prjCd");
        reqData.setStrValue(nodePath + "value", request.getParameter("prjCd"));
        reqData.setStrValue("SvcCont.QuerySvc.prjJobGroup", "yard_hs_info");
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 读取汇总结果
            Map<String, String> resultMap = new HashMap<String, String>();
            XmlBean rspData = svcResponse.getRspData();
            int seriesCnt = rspData.getListNum("SvcCont.ReportData.series");
            for (int i = 0; i < seriesCnt; i++) {
                XmlBean temp = rspData.getBeanByPath("SvcCont.ReportData.series[$i]");
                String sName = temp.getStrValue("series.name");
                List sValue = temp.getValue("series.data");
                resultMap.put(sName, sValue.get(0));
            }
            modelMap.put("resultMap", resultMap);
        }
        // 调用服务获取院落的统计信息
        return new ModelAndView("/eland/ph/ph002/ph00203", modelMap);
    }

    /*----------------------------------初始化腾退管理信息-----------------------------------------*/
    /**
     * 初始化腾退管理信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initMng(HttpServletRequest request, HttpServletResponse response) {
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
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Operation[0].";
        //拼接调用组件名称
        reqData.setStrValue(nodePath + "OpName", "QUERY_BUILD_MNG_INFO");
        //基本信息 BaseInfo
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildId", buildId);

        svcRequest.setReqData(reqData);
        //调用最新的服务
        SvcResponse svcResponse = SvcUtil.query(svcRequest);

        reqData = new XmlBean();
        reqData.setStrValue("SvcCont.CmpExt.prjCd", prjCd);
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse1 = SvcUtil.callSvc("cmpExtService", "queryExtCmpsByPrjCd", svcRequest);


        if (svcResponse.isSuccess() && svcResponse1.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            XmlBean cmpRspData = svcResponse1.getRspData();
            modelMap.put("buildMng", rspData.getBeanByPath("SvcCont.Operation[0].BuildInfo").getRootNode());
            //返回查询结果
            int cmpNum = cmpRspData.getListNum("SvcCont.ExtCmps.extCmp");
            Map cmpMap = new HashMap();
            for (int i = 0; i < cmpNum; i++) {
                cmpMap.put(cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpId"), cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpName"));
            }
            modelMap.put("cmpMap", cmpMap);
        }
        String toGo = "";
        if (StringUtil.isEqual("edit", method)) {
            toGo = "/eland/ph/ph002/ph00204_edit";
        } else {
            toGo = "/eland/ph/ph002/ph00204";
        }
        // 调用服务获取院落的统计信息
        return new ModelAndView(toGo, modelMap);
    }

    /**
     * 保存腾退管理信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView saveMng(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");

        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Operation[0].";
        //拼接调用组件名称
        reqData.setStrValue(nodePath + "OpName", "SAVE_BUILD_MNG_INFO");
        //管理信息 manageInfo
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildId", buildId);
        reqData.setStrValue(nodePath + "OpData.BuildInfo.ttOrgId", request.getParameter("prjOrgId"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.ttRegId", request.getParameter("ttRegId"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.ttUpRegId", request.getParameter("ttUpRegId"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.ttCompanyId", request.getParameter("ttCompanyId"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.ttMainTalk", request.getParameter("ttMainTalk"));

        svcRequest.setReqData(reqData);
        //调用最新的服务
        SvcResponse svcResponse = SvcUtil.transaction(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        // 调用服务
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /*----------------------------------导出成本信息-----------------------------------------*/
    /**
     * 导出成本信息
     * @param request
     * @param response
     */
    public void exportTtCb(HttpServletRequest request, HttpServletResponse response) {
        //保存参数为文档所用
        ReportData reportData = new ReportData();
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
        //总建筑面积
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "BuildInfo");
        opData.setStrValue("OpData.Conditions.Condition.fieldName", "buildId");
        opData.setStrValue("OpData.Conditions.Condition.fieldValue", buildId);
        opData.setStrValue("OpData.Conditions.Condition.operation", "=");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "buildFullAddr");
        opData.setStrValue("OpData.ResultFields.fieldName[2]", "totalJlbz");
        opData.setStrValue("OpData.ResultFields.fieldName[3]", "totalYaz");
        opData.setStrValue("OpData.ResultFields.fieldName[4]", "totalBuldSize");
        opData.setStrValue("OpData.ResultFields.fieldName[5]", "totalLandSize");
        opData.setStrValue("OpData.ResultFields.fieldName[6]", "totalJmdj");
        opData.setStrValue("OpData.ResultFields.fieldName[7]", "totalZddj");
        opData.setStrValue("OpData.ResultFields.fieldName[8]", "totalCb");
        opData.setStrValue("OpData.ResultFields.fieldName[9]", "totalGfk");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        XmlBean hsOpData = new XmlBean();
        hsOpData.setStrValue("OpData.entityName", "HouseInfo");
        hsOpData.setStrValue("OpData.Conditions.Condition[0].fieldName", "buildId");
        hsOpData.setStrValue("OpData.Conditions.Condition[0].fieldValue", "${buildId}");
        hsOpData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        hsOpData.setStrValue("OpData.PageInfo.pageSize", "100");
        hsOpData.setStrValue("OpData.ResultFields.fieldName[0]", "hsBuildSize");  //建筑面积
        hsOpData.setStrValue("OpData.ResultFields.fieldName[1]", "hsUseSize");    //使用面积
        hsOpData.setStrValue("OpData.ResultFields.fieldName[2]", "hsOwnerType");      //产别
        hsOpData.setStrValue("OpData.ResultFields.fieldName[3]", "hsOwnerPersons");   //申请人
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", hsOpData);
        SvcResponse svcResponse = query(svcRequest);
        String buildFullAddr = "";
        if (svcResponse.isSuccess()) {
            List rspList = svcResponse.getAllOpRsp("QUERY_ENTITY_PAGE_DATA");
            XmlBean rspBuildBean = rspList.get(0).getBeanByPath("Operation.OpResult");
            buildFullAddr = rspBuildBean.getStrValue("OpResult.PageData.Row[0].buildFullAddr");
            reportData.addParameters("buildFullAddr", buildFullAddr);
            reportData.addParameters("totalJlBz", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalJlBz"));
            reportData.addParameters("totalYazMj", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalYaz"));
            reportData.addParameters("totalYazMjMoney", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalGfk"));
            reportData.addParameters("totalCb", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalCb"));
            reportData.addParameters("jMdJ", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalJmdj"));
            reportData.addParameters("zDdJ", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalZddj"));
            reportData.addParameters("totalBuldSize", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalBuldSize"));
            reportData.addParameters("buldLandSize", rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalLandSize"))

            XmlBean rspHsBean = rspList.get(1).getBeanByPath("Operation.OpResult");
            if (rspHsBean != null) {
                int rowCount = rspHsBean.getListNum("OpResult.PageData.Row");
                double temp = 0.0;
                List list = new ArrayList();
                for (int i = 0; i < rowCount; i++) {
                    Map rowMap = new HashMap();
                    String tempField01 = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].hsBuildSize");
                    Double tempField02 = Double.parseDouble(rspHsBean.getStrValue("OpResult.PageData.Row[${i}].hsUseSize"));
                    String tempField03 = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].hsOwnerType_Name");
                    String tempField04 = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].hsOwnerPersons");
                    rowMap.put("hsBuildSize", tempField01);
                    rowMap.put("hsUseSize", tempField02);
                    rowMap.put("hsOwnerType", tempField03);
                    rowMap.put("hsOwnerPersons", tempField04);
                    temp = tempField02 + temp;
                    list.add(rowMap);
                }
                reportData.addParameters("totalHsUseSize", temp);             //使用面积合计
                reportData.addParameters("rowList", list);
            } else {
                throw new BusinessException("eland-web", "数据有误!");
            }
        }
        /**
         * 生成文档
         */
        String docName = "${buildFullAddr}情况说明.doc";
        String downloadPath = "${prjCd}/${docName}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        ReportFactory.makeReportByFreeMaker(reportData, "${prjCd}/template/院落情况说明.xml", outPutFile);
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /**
     * 导出成本信息-----天桥
     * @param request
     * @param response
     */
    public void exportTtCbTQ(HttpServletRequest request, HttpServletResponse response) {
        //保存参数为文档所用
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
        //总建筑面积
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "BuildInfo");
        opData.setStrValue("OpData.Conditions.Condition.fieldName", "buildId");
        opData.setStrValue("OpData.Conditions.Condition.fieldValue", buildId);
        opData.setStrValue("OpData.Conditions.Condition.operation", "=");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "buildFullAddr");
        opData.setStrValue("OpData.ResultFields.fieldName[2]", "totalJlbz");
        opData.setStrValue("OpData.ResultFields.fieldName[3]", "totalYaz");
        opData.setStrValue("OpData.ResultFields.fieldName[4]", "totalBuldSize");
        opData.setStrValue("OpData.ResultFields.fieldName[5]", "totalLandSize");
        opData.setStrValue("OpData.ResultFields.fieldName[6]", "totalJmdj");
        opData.setStrValue("OpData.ResultFields.fieldName[7]", "totalZddj");
        opData.setStrValue("OpData.ResultFields.fieldName[8]", "totalCb");
        opData.setStrValue("OpData.ResultFields.fieldName[9]", "totalGfk");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        XmlBean hsOpData = new XmlBean();
        hsOpData.setStrValue("OpData.entityName", "HouseInfo");
        hsOpData.setStrValue("OpData.Conditions.Condition[0].fieldName", "HouseInfo.buildId");
        hsOpData.setStrValue("OpData.Conditions.Condition[0].fieldValue", "${buildId}");
        hsOpData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        hsOpData.setStrValue("OpData.PageInfo.pageSize", "100");
        hsOpData.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo.hsBuildSize");  //建筑面积
        hsOpData.setStrValue("OpData.ResultFields.fieldName[1]", "HouseInfo.hsUseSize");    //使用面积
        hsOpData.setStrValue("OpData.ResultFields.fieldName[2]", "HouseInfo.hsOwnerType");      //产别
        hsOpData.setStrValue("OpData.ResultFields.fieldName[3]", "HouseInfo.hsOwnerPersons");   //申请人
        hsOpData.setStrValue("OpData.ResultFields.fieldName[4]", "HouseInfo.hsRoomNum");   //房屋间数
        hsOpData.setStrValue("OpData.ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.hsCtId");
        hsOpData.setStrValue("OpData.ResultFields.fieldName[6]", "HouseInfo.hsId");
        hsOpData.setStrValue("OpData.ResultFields.fieldName[7]", "HouseInfo.hsFlag");
        hsOpData.setStrValue("OpData.queryType", "2");//跨实体查询

        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", hsOpData);
        SvcResponse svcResponse = query(svcRequest);
        String buildFullAddr = "";
        //模板文件
        String templatePath = StringUtil.formatFilePath("webroot:config/${prjCd}/template/院落成本表.xlsx");
        //输出excel文件
        String excelName = buildFullAddr + "院落成本表" + ".xlsx";
        String excelPath = "temp/" + excelName;
        //复制excel文件
        ExcelWriter excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), new File(templatePath));
        if (svcResponse.isSuccess()) {
            List rspList = svcResponse.getAllOpRsp("QUERY_ENTITY_PAGE_DATA");
            XmlBean rspBuildBean = rspList.get(0).getBeanByPath("Operation.OpResult");
            buildFullAddr = rspBuildBean.getStrValue("OpResult.PageData.Row[0].buildFullAddr");
            String totalJmdj = rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalJmdj");
            String totalZddj = rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalZddj");
            String totalBuldSize = rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalBuldSize");
            String totalLandSize = rspBuildBean.getStrValue("OpResult.PageData.Row[0].totalLandSize");

            excelWriter.changeSheet(buildFullAddr);

            XmlBean rspHsBean = rspList.get(1).getBeanByPath("Operation.OpResult");
            if (rspHsBean != null) {
                int rowCount = rspHsBean.getListNum("OpResult.PageData.Row");
                int totalRoomNum = 0;
                double totalMoney = 0;
                for (int i = 0; i < rowCount; i++) {
                    excelWriter.copyRow(i + 2, i + 3);
                    String tempField01 = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsBuildSize");
                    String tempField03 = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsOwnerType_Name");
                    String tempField04 = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsOwnerPersons");
                    String hsRoomNum = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsRoomNum");
                    String hsCtId = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.HsCtInfo.hsCtId");
                    String hsId = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsId");
                    String hsFlag = rspHsBean.getStrValue("OpResult.PageData.Row[${i}].HouseInfo.hsFlag");
                    //判断是否临街，按照参数里配置取值
                    if (StringUtil.isEqual("3", hsFlag)) {
                        hsFlag = "是";
                    } else {
                        hsFlag = "否";
                    }
                    String money = "0.0";
                    totalRoomNum += NumberUtil.intValueOf(hsRoomNum);

                    //查询补偿款
                    opData = new XmlBean();
                    svcRequest = RequestUtil.getSvcRequest(request);
                    opData.setStrValue("OpData.hsCtId", hsCtId);
                    opData.setStrValue("OpData.hsId", hsId);
                    svcRequest.addOp("QUERY_CTITEM_VALUE_CMPT", opData);
                    svcResponse = query(svcRequest);
                    if (svcResponse.isSuccess()) {
                        XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_CTITEM_VALUE_CMPT");
                        money = resultBean.getStrValue("Operation.OpResult.RuleResult.HsBcJe.Items.Item[0].money");
                    }
                    totalMoney += Double.parseDouble(money);

                    excelWriter.writeData(i + 2, 2, tempField04);//产承人
                    excelWriter.writeData(i + 2, 3, tempField03);//产别
                    excelWriter.writeData(i + 2, 4, hsFlag);//临街
                    excelWriter.writeData(i + 2, 5, hsRoomNum);//房屋间数
                    excelWriter.writeData(i + 2, 6, tempField01);//建筑面积
                    excelWriter.writeData(i + 2, 8, money);//补偿款

                }
                //向excel里写数据
                excelWriter.writeData(2, 1, buildFullAddr);
                excelWriter.writeData(2, 7, totalLandSize);
                excelWriter.writeData(2, 9, totalJmdj);
                excelWriter.writeData(2, 10, totalZddj);
                excelWriter.writeData(2 + rowCount, 0, "合计");
                excelWriter.writeData(2 + rowCount, 5, totalRoomNum);//房屋总间数
                excelWriter.writeData(2 + rowCount, 6, totalBuldSize);
                excelWriter.writeData(2 + rowCount, 7, totalLandSize);
                excelWriter.writeData(2 + rowCount, 8, totalMoney);//总补偿
                excelWriter.writeData(2 + rowCount, 9, totalJmdj);
                excelWriter.writeData(2 + rowCount, 10, totalZddj);

                //合并单元格
                excelWriter.mergedRegion(2, 1 + rowCount, 0, 0);
                excelWriter.mergedRegion(2, 1 + rowCount, 1, 1);
                excelWriter.mergedRegion(2, 1 + rowCount, 7, 7);
                excelWriter.mergedRegion(2, 1 + rowCount, 9, 9);
                excelWriter.mergedRegion(2, 1 + rowCount, 10, 10);
                excelWriter.mergedRegion(2 + rowCount, 2 + rowCount, 0, 4);
            } else {
                throw new BusinessException("eland-web", "数据有误!");
            }
        }
        //文件关闭并保存
        excelWriter.closeBook();
        /**
         * 生成文档
         */
        String docName = "${buildFullAddr}情况说明.xlsx";
        String downloadPath = "${excelPath}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /**
     * 导出补偿确认单----天桥
     */
    public void exportBcqrdTQ(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        RequestUtil requestUtil = new RequestUtil(request);
        String flag = request.getParameter("flag");
        String hsId = request.getParameter("hsId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();

        opData.setStrValue("OpData.ruleCd", "generateTtQrDoc");
        opData.setStrValue("OpData.RuleParam.flag", flag);
        opData.setStrValue("OpData.RuleParam.hsId", hsId);
        svcRequest.addOp("EXECUTE_RULE", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            SvcDoc svcDoc = opResult.getValue("Operation.OpResult.svcDoc");
            String docName = svcDoc.getDocName();
            String docPath = svcDoc.getDocPath();
            String contextType = requestUtil.getStrParam("contextType");
            File localFile = new File(StringUtil.formatFilePath("webroot:" + docPath));
            // 下载内容进行缓存
            //20秒之内重新进入该页面的话不会进入该servlet的
            Date modifiedTime = DateUtil.toDateYmdHms("20150101000000");
            response.setDateHeader("Last-Modified", modifiedTime.getTime());
            response.setDateHeader("Expires", modifiedTime.getTime() + 6553600);
            response.setHeader("Cache-Control", "public");
            response.setHeader("Pragma", "Pragma");
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false);
        }
    }
    /*----------------------------------初始化腾退 成本信息-----------------------------------------*/
    /**
     * 初始化腾退管理信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initTtYg(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        ModelMap modelMap = new ModelMap();
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/getBuildAccept.groovy")
        opData.setStrValue("OpData.RuleParam.buildId", buildId);
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            if (opResult != null) {
                modelMap.put("buildBcYg", opResult.getBeanByPath("Operation.OpResult.RuleResult.BuldCbYg").getRootNode())
            }
        }
        modelMap.put("buildId", buildId);
        // 调用服务获取院落的统计信息
        return new ModelAndView("/eland/ph/ph002/ph00205", modelMap);
    }

    /*----------------------------------腾退 补偿款项使用情况-----------------------------------------*/
    /**
     * 初始化腾退款项
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initChartBck(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", request.getParameter("buildId"));

        // 调用服务获取院落的统计信息
        return new ModelAndView("/eland/ph/ph002/ph00206", modelMap);
    }

    /*----------------------------------新房源使用情况-----------------------------------------*/
    /**
     * 初始化新房源使用情况
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initChartNHs(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", request.getParameter("buildId"));
        // 调用分页服务获取分页数据
        // 调用服务获取院落的统计信息
        return new ModelAndView("/eland/ph/ph002/ph00207", modelMap);
    }

    /*----------------------------------新房源使用情况-----------------------------------------*/
    /**
     * 初始化新房源使用情况
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "200");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", buildId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        //只查询状态为 1 的，
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
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
                    if ("fileUpLoad".equals(tempField02) || "".equals(tempField02)) {
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
        modelMap.put("buildId", buildId);
        // 返回上传界面
        return new ModelAndView("/eland/ph/ph002/ph00208", modelMap);
    }

    /*----------------------------------基本信息初始化展示-----------------------------------------*/
    /**
     * 基本信息展示
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initYardInfo(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        String buildId = request.getParameter("buildId");
        modelMap.put("buildId", buildId);
        String method = request.getParameter("method");
        //根据不同方法给定不同跳转页面
        String goTo = "";
        if ("add".equals(method)) {
            //新增院落
            String regId = request.getParameter("regId");
            if (StringUtil.isNotEmptyOrNull(regId)) {
                modelMap.put("ttRegId", regId);
            }
            goTo = "/eland/ph/ph002/ph00201_add";
        } else {
            XmlBean reqData = new XmlBean();
            //拼接调用组件名称
            //基本信息 BaseInfo
            reqData.setStrValue("OpData.BuildInfo.buildId", buildId);
            svcRequest.addOp("QUERY_BUILD_BASE_INFO", reqData);
            // 查询腾退管理信息
            reqData = new XmlBean();
            //拼接调用组件名称
            //基本信息 BaseInfo
            reqData.setStrValue("OpData.BuildInfo.buildId", buildId);
            svcRequest.addOp("QUERY_BUILD_MNG_INFO", reqData);
            //调用最新的服务
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                // 基本信息
                XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_BUILD_BASE_INFO");
                modelMap.put("buildBean", rspData.getBeanByPath("Operation.BuildInfo").getRootNode());
                // 腾退管理信息
                XmlBean cmpRspData = svcResponse.getFirstOpRsp("QUERY_BUILD_MNG_INFO").getBeanByPath("Operation.BuildInfo");

                modelMap.put("buildMng", cmpRspData.getRootNode());
                svcRequest = RequestUtil.getSvcRequest(request);
                // 数据
                reqData = new XmlBean();
                String nodePath = "SvcCont.SysCfgs.";
                reqData.setValue(nodePath + "SysCfg[0].itemCd", "MAP_LAYER_URL");
                reqData.setValue(nodePath + "SysCfg[0].isCached", "true");
                reqData.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
                reqData.setValue(nodePath + "SysCfg[1].itemCd", "MAP_LAYER_NAME1");
                reqData.setValue(nodePath + "SysCfg[1].isCached", "true");
                reqData.setValue(nodePath + "SysCfg[1].prjCd", prjCd);
                reqData.setValue(nodePath + "SysCfg[2].itemCd", "MAP_LAYER_NAME2");
                reqData.setValue(nodePath + "SysCfg[2].isCached", "true");
                reqData.setValue(nodePath + "SysCfg[2].prjCd", prjCd);

                svcRequest.setReqData(reqData);
                // 调用服务
                svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
                if (svcResponse.isSuccess()) {
                    XmlBean cfgBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs");
                    int cfgNum = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg");
                    String baselayerUrl = "";
                    String layerName1 = "";
                    String layerName2 = "";
                    for (int i = 0; i < cfgNum; i++) {
                        String itemCd = cfgBean.getStrValue("SysCfgs.SysCfg[${i}].itemCd");
                        if (StringUtil.isEqual("MAP_LAYER_URL", itemCd)) {
                            baselayerUrl = cfgBean.getStrValue("SysCfgs.SysCfg[${i}].dftValue");
                        } else if (StringUtil.isEqual("MAP_LAYER_NAME1", itemCd)) {
                            layerName1 = cfgBean.getStrValue("SysCfgs.SysCfg[${i}].dftValue");
                        } else if (StringUtil.isEqual("MAP_LAYER_NAME2", itemCd)) {
                            layerName2 = cfgBean.getStrValue("SysCfgs.SysCfg[${i}].dftValue");
                        }
                    }
                    //查询原有地图坐标，返回页面绘制地图
                    modelMap.put("baselayerUrl", baselayerUrl);
                    modelMap.put("layerName1", layerName1);
                    modelMap.put("layerName2", layerName2);
                }
            }
            if (StringUtil.isEqual("edit", method)) {
                goTo = "/eland/ph/ph002/ph00201_edit";
            } else {
                goTo = "/eland/ph/ph002/ph00201_view";
            }
        }
        return new ModelAndView(goTo, modelMap);
    }

    /*------------------------院落基本信息、管理信息 新增、保存  ---------------------------*/
    /**
     * 院落信息保存  保存基本信息  管理信息
     * @param request
     * @param response
     */
    public void saveYardInfo(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");
        boolean isAdd = false;
        if (StringUtil.isEmptyOrNull(buildId)) {
            buildId = "\${buildId}";
            isAdd = true;
        }
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Operation[0].";
        //拼接调用组件名称
        reqData.setStrValue(nodePath + "OpName", "SAVE_BUILD_BASE_INFO");
        //基本信息 BaseInfo
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildId", buildId);
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildAddr", request.getParameter("buildAddr"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildNo", request.getParameter("buildAddrNo"));
        String buildFullAddr = request.getParameter("buildAddr") + request.getParameter("buildAddrNo");
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildFullAddr", buildFullAddr);
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildType", request.getParameter("buildType"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildFloorNum", request.getParameter("buildFloorNum"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildUnitNum", request.getParameter("buildUnitNum"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildStatus", request.getParameter("buildStatus"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildNote", request.getParameter("buildNote"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildLandSize", request.getParameter("buildLandSize"));
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildHsNum", request.getParameter("buildHsNum"));
        //地图信息保存   坐标集合串、经度、维度、缩放等级
        reqData.setStrValue(nodePath + "OpData.BuildInfo.buildPosition", request.getParameter("buildPoints"));

        if (isAdd) {
            //管理信息  组件
            nodePath = "SvcCont.Operation[1].";
            //拼接调用组件名称
            reqData.setStrValue(nodePath + "OpName", "SAVE_BUILD_MNG_INFO");
            //管理信息 manageInfo
            reqData.setStrValue(nodePath + "OpData.BuildInfo.buildId", buildId);
            reqData.setStrValue(nodePath + "OpData.BuildInfo.ttOrgId", request.getParameter("ttOrgId"));
            reqData.setStrValue(nodePath + "OpData.BuildInfo.ttRegId", request.getParameter("ttRegId"));
            reqData.setStrValue(nodePath + "OpData.BuildInfo.ttUpRegId", request.getParameter("ttUpRegId"));
            reqData.setStrValue(nodePath + "OpData.BuildInfo.ttCompanyId", request.getParameter("ttCompanyId"));
            reqData.setStrValue(nodePath + "OpData.BuildInfo.ttMainTalk", request.getParameter("ttMainTalk"));
        }

        svcRequest.setReqData(reqData);
        //调用最新的服务
        SvcResponse svcResponse = SvcUtil.transaction(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        if (result && isAdd) {
            buildId = svcResponse.getFirstOpRsp("SAVE_BUILD_BASE_INFO").getStrValue("OpResult.BuildInfo.buildId");
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", buildId:"${buildId}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /*----------------------------------进度跟踪模板展示-----------------------------------------*/
    /**
     * 初始化进度跟踪模板展示
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initWM(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", request.getParameter("buildId"));
        return new ModelAndView("/eland/ph/ph002/ph00209", modelMap);
    }

    /*-----------------------------------保存院落进度跟踪备忘--------------------------------------*/
    /**
     * 保存院落进度跟踪备忘
     * @param request
     * @param response
     * @return
     */
    public void saveWM(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String recordId = request.getParameter("recordId");
        if (StringUtil.isEmptyOrNull(recordId)) {
            recordId = "\${recordId}";
        }
        XmlBean rowData = new XmlBean();
        rowData.setStrValue("OpData.entityName", "RecordInfo");
        rowData.setStrValue("OpData.EntityData.recordId", recordId);
        rowData.setStrValue("OpData.EntityData.recordRelId", request.getParameter("relObjectId"));
        rowData.setStrValue("OpData.EntityData.recordType", request.getParameter("relType"));
        rowData.setStrValue("OpData.EntityData.recordContext", request.getParameter("talkContext"));
        rowData.setStrValue("OpData.EntityData.publishStf", svcRequest.getReqStaff());
        rowData.setStrValue("OpData.EntityData.publishDateTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        rowData.setStrValue("OpData.EntityData.doRecordPerson", request.getParameter("doRecordPerson"));
        rowData.setStrValue("OpData.EntityData.recordToPerson", request.getParameter("recordToPerson"));
        String startTime = request.getParameter("startTime");
        if (StringUtil.isNotEmptyOrNull(startTime)) {
            rowData.setStrValue("OpData.EntityData.startTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdHmsWthH(startTime)));
        } else {
            rowData.setStrValue("OpData.EntityData.startTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        }
        String endTime = request.getParameter("endTime");
        if (StringUtil.isNotEmptyOrNull(endTime)) {
            rowData.setStrValue("OpData.EntityData.endTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdHmsWthH(endTime)));
        } else {
            rowData.setStrValue("OpData.EntityData.endTime", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", rowData);
        SvcResponse svcRsp = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcRsp, "");
    }
    /**
     * 删除院落进度跟踪备忘
     * @param request
     * @param response
     */
    public void deleteWM(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "RecordInfo");
        opData.setStrValue("OpData.entityKey", request.getParameter("recordId"));
        svcRequest.addOp("DELETE_ENTITY_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /*----------------------------------待办任务初始化-----------------------------------------*/
    /**
     * 初始化进度跟踪模板展示
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initTD(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String buildId = request.getParameter("buildId");
        // 请求报文
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.busiKey", buildId);
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
        modelMap.put("buildId", buildId);
        // 调用服务获取院落待完成的任务
        return new ModelAndView("/eland/ph/ph002/ph00202", modelMap);
    }

    /*-------------------------------------发起任务初始化---0202_initiateTask----------------------------------------------*/
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
        String buildId = request.getParameter("buildId");

        modelMap.put("initPer", initPer);
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph002/ph00202_initTask", modelMap);
    }

    /*-------------------------------------确认发出任务---0302 cfmSendTask----------------------------------------------*/
    /**
     * 确认发出任务
     * @param request
     * @param response
     */
    public void cfmSendTask(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        String buildId = request.getParameter("buildId");
        String taskName = request.getParameter("taskName");
        String taskDetails = request.getParameter("taskDetails");
        String targetPer = request.getParameter("targetPer");
        String taskEndDate = request.getParameter("taskEndDate");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 签约信息保存
        XmlBean opData = new XmlBean();
        String prePath = "OpData.WfExecute.";
        opData.setStrValue(prePath + "procDefKey", request.getParameter("procDefKey"));
        opData.setStrValue(prePath + "busiKey", "Build_${buildId}");
        opData.setStrValue(prePath + "procStUser", svcRequest.getReqStaffCd());
        opData.setStrValue(prePath + "Variables.TaskName", taskName);
        opData.setStrValue(prePath + "Variables.TaskAssignee", targetPer);
        opData.setStrValue(prePath + "Variables.Description", taskDetails);
        opData.setStrValue(prePath + "Variables.DueDate", taskEndDate);
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

    /*-------------------------------------历史任务---0302_historyTask ----------------------------------------------*/
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView historyTask(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String initPer = svcRequest.getReqStaffIdInt().toString();
        ModelMap modelMap = new ModelMap();
        String buildId = request.getParameter("buildId");
        // 请求报文
        XmlBean opData = new XmlBean();
        opData.setStrValue("SvcCont.WF.WfExecute.busiKey", "Build_${buildId}");
        svcRequest.setReqData(opData);
        SvcResponse svcResponse = callService("wfExecuteService", "queryHiTaskListByBusiKey", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            if (rspData != null) {
                //历史任务多个。itar遍历
                int taskCount = rspData.getListNum("SvcCont.WF.HiTasks.hitask");
                List<XmlNode> hiTaskList = new ArrayList<>();
                for (int i = 0; i < taskCount; i++) {
                    XmlBean taskBean = rspData.getBeanByPath("SvcCont.WF.HiTasks.hitask[${i}]");
                    hiTaskList.add(taskBean.getRootNode());
                }
                modelMap.put("hiTaskList", hiTaskList);
            }
        }
        modelMap.put("initPer", initPer);
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph002/ph00202_historyTask", modelMap);
    }

    /**
     * 导出居民附件信息
     * @param request 请求路径
     * @param response 响应结果
     */
    public void exportFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "200");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", buildId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        //只查询状态为 1 的，
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
        // 增加类型限制
        String docTypeNames = request.getParameter("docTypeName");
        if (StringUtil.isNotEmptyOrNull(docTypeNames)) {
            String[] temp = docTypeNames.split(",");
            List<String> valueList = new ArrayList<String>();
            for (String tempItem : temp) {
                valueList.add("'" + tempItem + ",");
            }
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "statusCd");
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", "( + ${valueList.join(',')} + )");
            opData.setStrValue(prePath + "Conditions.Condition[3].operation", "in");
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
        opData.setStrValue(prePath + "entityName", "BuildInfo");
        opData.setStrValue(prePath + "entityKey", buildId);
        opData.setStrValue(prePath + "ResultField.fieldName", "buildFullAddr");
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            // 获取产权人名称
            String buildFullAddr = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY")
                    .getStrValue("Operation.OpResult.buildFullAddr");
            // 获取查询文档处理结果
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                Map<String, List<String>> zipFileMap = new LinkedHashMap<String, List<String>>();
                for (int i = 0; i < rowCount; i++) {
                    String docPath = queryResult.getStrValue("OpResult.PageData.Row[${i}].docPath");
                    String docTypeName = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
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
                String downloadFileName = buildFullAddr;
                if (zipFileMap.size() > 0) {
                    tempFile = ServerFile.getFile("temp/", UUID.randomUUID().toString() + downloadExt);
                    MbillZipUtil.addFilesToZip(tempFile.getPath(), zipFileMap);
                } else {
                    downloadExt = ".txt";
                    downloadFileName = "无内容";
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
     * 导出整院附件信息
     * @param request 请求路径
     * @param response 响应结果
     */
    public void exportYardFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");

        /* 调用服务获取院落内的房屋信息 */
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "buildId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", buildId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsOwnerPersons");
        // 增加院内房屋信息
        svcRequest.addOp("FILTER_QUERY_ENTITY_DATA", opData);

        /* 查询院落附件 */
        // 组织输出报文
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "200");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", buildId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        // 只查询状态为 1 的，
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
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);

        /* 增加查询院落地址 */
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "BuildInfo");
        opData.setStrValue(prePath + "entityKey", buildId);
        opData.setStrValue(prePath + "ResultField.fieldName", "buildFullAddr");
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);

        // 调用服务获取信息
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        if (svcResponse.isSuccess()) {

            // 压缩文件输出
            Map<String, List<String>> zipFileMap = new LinkedHashMap<String, List<String>>();
            // 房屋地址，去房屋的地址
            String buildFullAddr = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY")
                    .getStrValue("Operation.OpResult.buildFullAddr");

            // 获取院落的附件
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT")
                    .getBeanByPath("Operation.OpResult.PageData");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("PageData.Row");
                for (int i = 0; i < rowCount; i++) {
                    String docPath = queryResult.getStrValue("PageData.Row[${i}].docPath");
                    String docTypeName = queryResult.getStrValue("PageData.Row[${i}].docTypeName");

                    File localFile = ServerFile.getFile(docPath);
                    if (localFile.exists()) {
                        String toDir = "院落附件/" + docTypeName;
                        List<String> typeFile = zipFileMap.get(toDir);
                        if (typeFile == null) {
                            typeFile = new ArrayList<String>();
                        }
                        typeFile.add(localFile.getPath());
                        zipFileMap.put(toDir, typeFile);
                    }
                }
            }

            // 获取院落下房屋信息列表
            XmlBean pageData = svcResponse.getFirstOpRsp("FILTER_QUERY_ENTITY_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            int rowCount = 0;
            if (pageData != null) {
                rowCount = pageData.getListNum("PageData.Row");
            }
            Map<String, String> hsMap = new LinkedHashMap<String, String>(rowCount);
            for (int i = 0; i < rowCount; i++) {
                String hsId = pageData.getStrValue("PageData.Row[${i}].hsId");
                String hsOwnerPersons = pageData.getStrValue("PageData.Row[${i}].hsOwnerPersons");
                hsMap.put(hsId, hsOwnerPersons);
            }
            // 调用服务获取房屋的附件
            svcRequest = RequestUtil.getSvcRequest(request);
            // 获取可用房源区域列表
            opData = new XmlBean();
            opData.setStrValue(prePath + "entityName", "SvcDocInfo");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "100");
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
            // 关联对象查询条件
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "(" + hsMap.keySet().join(",") + ")");
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "in");
            // 只查询状态为 1 的，
            opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
            opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
            opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");

            opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "prjCd");
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", prjCd);
            opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");

            // 增加类型限制
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
                    opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "docTypeName");
                    opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", "(${valueList.join(',')})");
                    opData.setStrValue(prePath + "Conditions.Condition[3].operation", "in");
                }
            }
            // 需要查询的字段
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docPath");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docName");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docTypeName");
            opData.setStrValue(prePath + "ResultFields.fieldName[3]", "relId");
            // 需要排序的字段
            opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "docTypeName");
            opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
            // 增加查询
            svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
            // 调用服务
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                // 获取查询文档处理结果
                queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT")
                        .getBeanByPath("Operation.OpResult.PageData");
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    rowCount = queryResult.getListNum("PageData.Row");
                    for (int i = 0; i < rowCount; i++) {
                        String docPath = queryResult.getStrValue("PageData.Row[${i}].docPath");
                        String relId = queryResult.getStrValue("PageData.Row[${i}].relId");
                        String docTypeName = queryResult.getStrValue("PageData.Row[${i}].docTypeName");

                        File localFile = ServerFile.getFile(docPath);
                        if (localFile.exists()) {
                            String toDir = "居民附件/" + hsMap.get(relId) + "/" + docTypeName;
                            List<String> typeFile = zipFileMap.get(toDir);
                            if (typeFile == null) {
                                typeFile = new ArrayList<String>();
                            }
                            typeFile.add(localFile.getPath());
                            zipFileMap.put(toDir, typeFile);
                        }
                    }
                }
            }
            // 创建输出文件
            File tempFile = null;
            String downloadExt = ".zip";
            String downloadFileName = buildFullAddr;
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
