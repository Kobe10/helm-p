import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.excel.ExcelWriter
import com.shfb.oframe.core.util.report.ReportData
import com.shfb.oframe.core.util.report.ReportFactory
import com.shfb.oframe.core.util.report.XDocReport
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.DecimalFormat

/**
 * 建筑详细信息展示页面
 */
class ph009 extends GroovyController {

    /*----------------------------------导出公房退租信息-----------------------------------------*/
    /**
     * 承租人变更申请
     * @param request
     * @param response
     */
    public void exportGfTz(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
//        String buildId = "4";
//        String hsId = "4";
        //保存参数为文档所用
        ReportData reportData = new ReportData();

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportGfTzRule.groovy");
        opData.setStrValue("OpData.RuleParam.hsId", hsId);
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            String ownerCerty = opResult.getStrValue("Operation.OpResult.RuleResult.ownerCerty");
            if (StringUtil.isEmptyOrNull(ownerCerty)) {
                ownerCerty = "___";
                reportData.addParameters("ownerCerty", ownerCerty);
            } else {
                reportData.addParameters("ownerCerty", ownerCerty);
            }
        }

        String hsOwnerPersons = "";
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "HouseInfo");
            opData.setStrValue("OpData.entityKey", hsId);
            // 查询的字段
            opData.setStrValue("OpData.ResultField.fieldName[0]", "hsAddrNo"); // 房屋门牌号
            opData.setStrValue("OpData.ResultField.fieldName[1]", "hsOwnerPersons"); // 房屋产权人
            opData.setStrValue("OpData.ResultField.fieldName[2]", "hsCd"); // 协议编号
            opData.setStrValue("OpData.ResultField.fieldName[4]", "hsFullAddr"); //房屋地址
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult1 = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                String hsAddrNo = queryResult1.getValue("OpResult.hsAddrNo");
                if (StringUtil.isEmptyOrNull(hsAddrNo)) {
                    hsAddrNo = "___";
                    reportData.addParameters("hsAddrNo", hsAddrNo);
                } else {
                    reportData.addParameters("hsAddrNo", hsAddrNo);
                }

                hsOwnerPersons = queryResult1.getValue("OpResult.hsOwnerPersons");
                if (StringUtil.isEmptyOrNull(hsOwnerPersons)) {
                    hsOwnerPersons = "___";
                    reportData.addParameters("hsOwnerPersons", hsOwnerPersons);
                } else {
                    reportData.addParameters("hsOwnerPersons", hsOwnerPersons);
                }
                String hsCd = queryResult1.getValue("OpResult.hsCd");
                if (StringUtil.isEmptyOrNull(hsCd)) {
                    hsCd = "___";
                    reportData.addParameters("hsCd", hsCd);
                } else {
                    reportData.addParameters("hsCd", hsCd);
                }
                String hsFullAddr = queryResult1.getValue("OpResult.hsFullAddr");
                if (StringUtil.isEmptyOrNull(hsFullAddr)) {
                    hsFullAddr = "___";
                    reportData.addParameters("hsFullAddr", hsFullAddr);
                } else {
                    reportData.addParameters("hsFullAddr", hsFullAddr);
                }
            }
        }
        /**
         * 生成文档
         */
        String docName = "承租人${hsOwnerPersons}变更申请书-金盈.doc";
        String downloadPath = "${prjCd}/${docName}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        ReportFactory.makeReportByFreeMaker(reportData, "${prjCd}/template/承租人变更申请书-金盈.xml", outPutFile);
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出公房退租 信息-----------------------------------------*/
    /**
     * 居民换房申请书
     * @param request
     * @param response
     */
    public void exportZmHf(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
//        String buildId = "4";
//        String hsId = "4";
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //保存参数为文档所用
        ReportData reportData = new ReportData();
        XmlBean opData = null;
        String hsOwnerPersons = "";
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            /**
             * 使用通用组件获取房屋的属性
             */
            opData = new XmlBean();
            String prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "HouseInfo");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsId");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "${hsId}");
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "hsOwnerType");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "0");
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "!=");
            // 设置分页
            opData.setStrValue(prePath + "PageInfo.pageSize", "20");
            // 需要查询的字段
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsAddrNo");  //房屋号
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsOwnerPersons");   //承租人
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "hsCd");   //协议编号
            opData.setStrValue(prePath + "ResultFields.fieldName[3]", "hsPubOwnerCerty");   //协议编号
            opData.setStrValue(prePath + "ResultFields.fieldName[4]", "hsFullAddr");   //房屋地址
            // 增加查询
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            SvcResponse svcResponse = query(svcRequest);
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");

            String hsCd = "";
            String hsAddrNo = "";
            String hsPubOwnerCerty = "";
            String hsFullAddr = "";
            // 处理查询结果,并保存参数为文档所用
            if (queryResult != null) {
                hsOwnerPersons = queryResult.getStrValue("OpResult.PageData.Row.hsOwnerPersons");
                if (StringUtil.isEmptyOrNull(hsOwnerPersons)) {
                    hsOwnerPersons = "___";
                    reportData.addParameters("hsOwnerPersons", hsOwnerPersons);
                } else {
                    reportData.addParameters("hsOwnerPersons", hsOwnerPersons);
                }
                hsCd = queryResult.getStrValue("OpResult.PageData.Row.hsCd");
                if (StringUtil.isEmptyOrNull(hsCd)) {
                    hsCd = "___";
                    reportData.addParameters("hsCd", hsCd);
                } else {
                    reportData.addParameters("hsCd", hsCd);
                }
                hsAddrNo = queryResult.getStrValue("OpResult.PageData.Row.hsAddrNo");
                if (StringUtil.isEmptyOrNull(hsAddrNo)) {
                    hsAddrNo = "___";
                    reportData.addParameters("hsAddrNo", hsAddrNo);
                } else {
                    reportData.addParameters("hsAddrNo", hsAddrNo);
                }
                hsPubOwnerCerty = queryResult.getStrValue("OpResult.PageData.Row.hsPubOwnerCerty");
                if (StringUtil.isEmptyOrNull(hsPubOwnerCerty)) {
                    hsPubOwnerCerty = "___";
                    reportData.addParameters("hsPubOwnerCerty", hsPubOwnerCerty);
                } else {
                    reportData.addParameters("hsPubOwnerCerty", hsPubOwnerCerty);
                }
                hsFullAddr = queryResult.getStrValue("OpResult.PageData.Row.hsFullAddr");
                if (StringUtil.isEmptyOrNull(hsFullAddr)) {
                    hsFullAddr = "___";
                    reportData.addParameters("hsFullAddr", hsFullAddr);
                } else {
                    reportData.addParameters("hsFullAddr", hsFullAddr);
                }
            }
        }
        // 查询合同号
        opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportGfTzRule.groovy");
        opData.setStrValue("OpData.RuleParam.hsId", hsId);
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            String ownerCerty = opResult.getStrValue("Operation.OpResult.RuleResult.ownerCerty");
            if (StringUtil.isEmptyOrNull(ownerCerty)) {
                ownerCerty = "___";
                reportData.addParameters("ownerCerty", ownerCerty);
            } else {
                reportData.addParameters("ownerCerty", ownerCerty);
            }
        }
        /**
         * 生成文档
         */
        String docName = "${hsOwnerPersons}换房申请书.doc";
        String downloadPath = "${prjCd}/${docName}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        ReportFactory.makeReportByFreeMaker(reportData, "${prjCd}/template/居民换房申请书.xml", outPutFile);
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出过户介绍信 信息-----------------------------------------*/
    /**
     * 过户介绍信信息
     * @param request
     * @param response
     */
    public void exportGhJsX(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
//        String buildId = "4";
//        String hsId = "4";
        DecimalFormat format = new DecimalFormat("#.00");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportGhJsXRule.groovy");
        opData.setStrValue("OpData.RuleParam.hsId", hsId);
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        ReportData reportData = new ReportData();
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            String regName = opResult.getStrValue("Operation.OpResult.RuleResult.regName");
            if (StringUtil.isEmptyOrNull(regName)) {
                regName = "___";
                reportData.addParameters("regName", regName); //换房至的区域
            } else {
                reportData.addParameters("regName", regName); //换房至的区域
            }
            String yiNum = opResult.getStrValue("Operation.OpResult.RuleResult.yiNum");
            String erNum = opResult.getStrValue("Operation.OpResult.RuleResult.erNum");
            String sanNum = opResult.getStrValue("Operation.OpResult.RuleResult.sanNum");
            reportData.addParameters("yiNum", yiNum); //一居数量
            reportData.addParameters("erNum", erNum); //二居数量
            reportData.addParameters("sanNum", sanNum); //三居数量
        }
        String hsOwnerPersons = "___";
        String hsFullAddr = "___";
        String hsUseSize = "___";
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            svcRequest = RequestUtil.getSvcRequest(request);
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "HouseInfo");
            opData.setStrValue("OpData.entityKey", hsId);
            // 查询的字段
            opData.setStrValue("OpData.ResultField.fieldName[0]", "hsOwnerPersons"); // 房屋产权人
            opData.setStrValue("OpData.ResultField.fieldName[1]", "hsUseSize"); // 使用面积
            opData.setStrValue("OpData.ResultField.fieldName[2]", "hsOwnerType"); // 使用面积
            opData.setStrValue("OpData.ResultField.fieldName[3]", "hsFullAddr"); // 房屋地址
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult1 = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                String hsOwnerType = queryResult1.getValue("OpResult.hsOwnerType");
                if (hsOwnerType != "0") {
                    hsUseSize = queryResult1.getValue("OpResult.hsUseSize");
                    Double hsUseSizeDou = Double.parseDouble(hsUseSize);
                    String hsUseSizeStr = format.format(hsUseSizeDou);
                    if (StringUtil.isEmptyOrNull(hsUseSize)) {
                        reportData.addParameters("hsUseSize", "0.00");
                    } else {
                        reportData.addParameters("hsUseSize", hsUseSizeStr);
                    }
                    hsOwnerPersons = queryResult1.getValue("OpResult.hsOwnerPersons");
                    if (StringUtil.isEmptyOrNull(hsOwnerPersons)) {
                        hsOwnerPersons = "___";
                        reportData.addParameters("hsOwnerPersons", hsOwnerPersons);
                    } else {
                        reportData.addParameters("hsOwnerPersons", hsOwnerPersons);
                    }
                    hsFullAddr = queryResult1.getValue("OpResult.hsFullAddr");
                    if (StringUtil.isEmptyOrNull(hsFullAddr)) {
                        hsFullAddr = "___";
                        reportData.addParameters("hsFullAddr", hsFullAddr);
                    } else {
                        reportData.addParameters("hsFullAddr", hsFullAddr);
                    }
                } else {
                    reportData.addParameters("hsFullAddr", hsFullAddr);
                    reportData.addParameters("hsUseSize", "___");
                    reportData.addParameters("hsOwnerPersons", "___");
                }
            }
        }
        /**
         * 生成文档
         */
        String docName = "${hsOwnerPersons}过户介绍信.doc";
        String downloadPath = "${prjCd}/${docName}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        ReportFactory.makeReportByFreeMaker(reportData, "${prjCd}/template/过户介绍信.xml", outPutFile);
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出挑选安置房购房 信息-----------------------------------------*/
    /**
     * 挑选安置房  院落购房明细表
     * @param request
     * @param response
     */
    public void exportTxAzF(HttpServletRequest request, HttpServletResponse response) {
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
//        String buildId = "4";
        //保存参数为文档所用
        ReportData reportData = new ReportData();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 院落地址
        String buildFullAddr = "";
        // 生成文档的路径
        String outFilePath = "";
        String docName = "";
        if (StringUtil.isNotEmptyOrNull(buildId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            // 查询的字段
            opData.setStrValue("OpData.ResultField.fieldName", "buildFullAddr"); // 院落地址和门号
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    buildFullAddr = queryResult.getValue("OpResult.buildFullAddr");
                    reportData.addParameters("buildFullAddr", buildFullAddr);
                }
            }
            svcRequest = RequestUtil.getSvcRequest(request);
            opData = new XmlBean();
            opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportTxAzFRule.groovy");
            opData.setStrValue("OpData.RuleParam.buildId", buildId);
            opData.setStrValue("OpData.RuleParam.buildFullAddr", buildFullAddr);
            opData.setStrValue("OpData.RuleParam.prjCd", prjCd);
            svcRequest.addOp("EXECUTE_RULE", opData);
            // 调用服务，执行数据查询
            svcResponse = query(svcRequest);
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            // 生成文档的路径
            outFilePath = opResult.getStrValue("Operation.OpResult.RuleResult.outFilePath");
            docName = opResult.getStrValue("Operation.OpResult.RuleResult.docName");
        }
        /**
         * 生成文档
         */
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outFilePath);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出办理网签、核税、缴税等手续合同 信息-----------------------------------------*/
    /**
     * 办理网签、核税、缴税等手续合同明细表
     * @param request
     * @param response
     */
    public void exportWqHsJs(HttpServletRequest request, HttpServletResponse response) {
        String prjCd = request.getParameter("prjCd");
        String hsId = request.getParameter("hsId");
//        String hsCtId = "14";
        ReportData reportData = new ReportData();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 生成文档的路径
        String outFilePath = "";
        String docName = "";

        String hsCtId = "";
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
        }
        XmlBean opData = new XmlBean();

        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportWqHsJsRule.groovy");
        opData.setStrValue("OpData.RuleParam.hsCtId", hsCtId);
        opData.setStrValue("OpData.RuleParam.prjCd", prjCd);
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            // 生成文档的路径
            outFilePath = opResult.getStrValue("Operation.OpResult.RuleResult.outFilePath");
            docName = opResult.getStrValue("Operation.OpResult.RuleResult.docName");
        }
        /**
         * 生成文档
         */
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outFilePath);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------腾房交房（房屋）-->房屋交接验收确认 信息-----------------------------------------*/
    /**
     * 腾房交房（房屋）房屋交接验收确认单
     * @param request
     * @param response
     */
    public void exportTfJf(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String hsFullAddr = "";
//        String hsId = "88";
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ReportData reportData = new ReportData();
        DecimalFormat format = new DecimalFormat("#.00");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.entityKey", hsId);
        // 查询的字段
        opData.setStrValue("OpData.ResultField.fieldName[0]", "hsOwnerPersons"); // 房屋产权人
        opData.setStrValue("OpData.ResultField.fieldName[1]", "hsFullAddr"); // 房屋完整地址
        opData.setStrValue("OpData.ResultField.fieldName[2]", "hsBuildSize"); // 建筑面积
        opData.setStrValue("OpData.ResultField.fieldName[3]", "hsRoomNum"); // 房屋间数
        opData.setStrValue("OpData.ResultField.fieldName[4]", "hsSlfRoom"); // 自建房间数
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            String hsOwnerPersons = "";
            String hsBuildSize = "";
            String hsRoomNum = "";
            String hsSlfRoom = "";
            Double hsSize = 0.00;
            XmlBean queryResult1 = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
            if (queryResult1 != null) {
                hsOwnerPersons = queryResult1.getValue("OpResult.hsOwnerPersons");
                hsFullAddr = queryResult1.getValue("OpResult.hsFullAddr");
                hsBuildSize = queryResult1.getValue("OpResult.hsBuildSize");
                if (StringUtil.isNotEmptyOrNull(hsBuildSize)) {
                    hsSize = Double.parseDouble(hsBuildSize);
                }
                hsRoomNum = queryResult1.getValue("OpResult.hsRoomNum");
                hsSlfRoom = queryResult1.getValue("OpResult.hsSlfRoom");
                // 保存参数为文档所用
                reportData.addParameters("hsOwnerPersons", StringUtil.obj2Str(hsOwnerPersons));
                reportData.addParameters("hsFullAddr", StringUtil.obj2Str(hsFullAddr));
                reportData.addParameters("hsBuildSize", format.format(hsSize));
                reportData.addParameters("hsRoomNum", StringUtil.obj2Str(hsRoomNum));
                reportData.addParameters("hsSlfRoom", StringUtil.obj2Str(hsSlfRoom));
            }
        }
        /**
         * 生成文档
         */
        String docName = "${hsFullAddr}交接验收确认单.doc";
        String downloadPath = "${prjCd}/${docName}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        ReportFactory.makeReportByFreeMaker(reportData, "${prjCd}/template/房屋交接验收确认单.xml", outPutFile);
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出腾退交房（房屋交接清单）  信息-----------------------------------------*/
    /**
     * 房屋交接清单 信息
     * @param request
     * @param response
     */
    public void exportFwJjQd(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
//        String hsId = "77";
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.entityKey", hsId);
        // 查询的字段
        opData.setStrValue("OpData.ResultField.fieldName[0]", "hsFullAddr"); // 房屋完整地址
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
        SvcResponse svcResponse = query(svcRequest);
        //模板文件
        String templatePath = StringUtil.formatFilePath("webroot:eland/ph/ph009/房屋交接清单.xls");
        //输出excel文件
        String excelName = ServerFile.generateFileName(".xls");
        String excelPath = "temp/" + excelName;
        //复制excel文件 读取内容
        ExcelWriter excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), new File(templatePath));
        excelWriter.changeSheet("导出数据");
        String hsFullAddr = "";
        if (svcResponse.isSuccess()) {
            XmlBean queryResult1 = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
            if (queryResult1 != null) {
                hsFullAddr = queryResult1.getValue("OpResult.hsFullAddr");
            }
            excelWriter.writeData(3, 0, hsFullAddr + "号");
        }
        excelWriter.closeBook();
        /**
         * 生成文档
         */
        String docName = "${hsFullAddr}交接清单.xls";
        String downloadPath = "${excelPath}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出安置协议结算（房屋） 信息-----------------------------------------*/
    /**
     * 生成 公房结算明细表（房屋） 信息
     * @param request
     * @param response
     */
    public void exportGfSs(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
//        String hsCtId = "15";

        String hsFullAddr = "___";
        String hsOwnerPersons = "___";
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.entityKey", hsId);
        // 查询的字段
        opData.setStrValue("OpData.ResultField.fieldName[0]", "hsOwnerPersons"); // 房屋产权人
        opData.setStrValue("OpData.ResultField.fieldName[1]", "hsFullAddr"); // 房屋完整地址
        svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult1 = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
            if (queryResult1 != null) {
                hsOwnerPersons = queryResult1.getValue("OpResult.hsOwnerPersons");
                hsFullAddr = queryResult1.getValue("OpResult.hsFullAddr");
            }
        }

        //查询签约id
        String hsCtId = "";
        XmlBean paramData = new XmlBean();
        String nodePath = "OpData.";
        paramData.setStrValue(nodePath + "entityName", "HsCtInfo");
        paramData.setStrValue(nodePath + "Conditions.Condition[0].fieldName", "hsId");
        paramData.setStrValue(nodePath + "Conditions.Condition[0].fieldValue", hsId);
        paramData.setStrValue(nodePath + "Conditions.Condition[0].operation", "=");
        paramData.setStrValue(nodePath + "ResultFields.fieldName[0]", "hsCtId");
        svcRequest.addOp("QUERY_ENTITY_CMPT", paramData);
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            hsCtId = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getStrValue("Operation.OpResult.PageData.Row.hsCtId");
        }

        ReportData reportData = new ReportData();
        // 生成文档的路径
        String excelPath = "";
        opData = new XmlBean();

        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportGfSsRule.groovy");
        opData.setStrValue("OpData.RuleParam.hsCtId", hsCtId);
        opData.setStrValue("OpData.RuleParam.prjCd", prjCd);
        opData.setStrValue("OpData.RuleParam.hsOwnerPersons", hsOwnerPersons);
        opData.setStrValue("OpData.RuleParam.hsFullAddr", hsFullAddr);
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 调用服务，执行数据查询
        svcResponse = query(svcRequest);
        XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
        excelPath = opResult.getStrValue("Operation.OpResult.RuleResult.excelPath");
        /**
         * 生成文档
         */
        String docName = "${hsFullAddr}号院落结算金额明细表.xls";
        String downloadPath = "${excelPath}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出完成退租手续（房屋） 信息-----------------------------------------*/
    /**
     * 公房租金过户明细
     * @param request
     * @param response
     */
    public void exportZjGhMx(HttpServletRequest request, HttpServletResponse response) {
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
//        String hsId = "77";
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        //模板文件
        String templatePath = StringUtil.formatFilePath("webroot:eland/ph/ph009/租金及过户费明细表.xlsx");
        //输出excel文件
        String excelName = ServerFile.generateFileName(".xls");
        String excelPath = "temp/" + excelName;
        //复制excel文件 读取内容
        ExcelWriter excelWriter = new ExcelWriter(ServerFile.createFile(excelPath), new File(templatePath));
        excelWriter.changeSheet("导出数据");
        String hsFullAddr = "";

        /**
         * 使用通用组件获取房屋的属性
         * */
        opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "buildId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "${buildId}");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "hsOwnerType");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "200");
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", ">=");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "hsOwnerType");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "300");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "<");
        // 设置分页
        opData.setStrValue(prePath + "PageInfo.pageSize", "200");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsBuildSize");  //建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsOwnerPersons");   //承租人
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "hsFullAddr");   //院落完整地址
        // 增加查询
        SvcRequest svcRequest1 = RequestUtil.getSvcRequest(request);
        svcRequest1.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest1);
        XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
        // 起始行
        int rowNum = 2;
        // 序号
        int xhNum = 1;
        double temp1 = 0;
        double temp2 = 0;
        double temp3 = 0;
        // 处理查询结果,并保存参数为文档所用
        if (queryResult != null) {
            DecimalFormat format = new DecimalFormat("#.00");
            int rowCount = queryResult.getListNum("OpResult.PageData.Row");
            hsFullAddr = queryResult.getStrValue("OpResult.PageData.Row[0].hsFullAddr");
            String firstRow = "${hsFullAddr}租金及过户费用明细表";
            if (StringUtil.isEmptyOrNull(hsFullAddr)) {
                hsFullAddr = "___";
                firstRow = "${hsFullAddr}租金及过户费用明细表";
                excelWriter.writeData(0, 0, firstRow);
            } else {
                excelWriter.writeData(0, 0, firstRow);
            }

            String hsOwnerPersons = "";
            for (int i = 0; i < rowCount; i++) {
                excelWriter.insertRow(rowNum + 1);
                excelWriter.copyRow(rowNum, rowNum + 1);
                Double hsBuildSize = Double.parseDouble(queryResult.getStrValue("OpResult.PageData.Row[${i}].hsBuildSize"));
                hsOwnerPersons = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsOwnerPersons");
                // 计算年租金
                Double yearMoney = hsBuildSize * 20 * 12;
                // 过户费用
                Double gHMoney = hsBuildSize * 500;

                temp1 = hsBuildSize + temp1;
                temp2 = yearMoney + temp2;


                excelWriter.writeData(rowNum, 0, xhNum);
                excelWriter.writeData(rowNum, 1, hsOwnerPersons);
                excelWriter.writeData(rowNum, 2, format.format(hsBuildSize));
                excelWriter.writeData(rowNum, 3, format.format(yearMoney));
                if (gHMoney > 10000) {
                    gHMoney = 10000;
                    temp3 = gHMoney + temp3;
                    excelWriter.writeData(rowNum, 4, 10000);
                } else {
                    temp3 = gHMoney + temp3;
                    excelWriter.writeData(rowNum, 4, format.format(gHMoney));
                }
                rowNum++;
                xhNum++;
            }

            // 写入合计并行
            excelWriter.writeData(rowNum, 0, "合计");
            excelWriter.writeData(rowNum, 1, "");
            excelWriter.writeData(rowNum, 2, format.format(temp1));
            excelWriter.writeData(rowNum, 3, format.format(temp2));
            excelWriter.writeData(rowNum, 4, format.format(temp3));
            excelWriter.writeData(rowNum, 5, "");
            excelWriter.mergedRegion(rowNum, rowNum, 0, 1);

            // 写入最后一行
            excelWriter.copyRow(2, rowNum + 1);
            excelWriter.writeData(rowNum + 1, 0, "注：租金按照一年12月计算，过户费用每户最多不超过10000元");
            excelWriter.writeData(rowNum + 1, 1, "");
            excelWriter.writeData(rowNum + 1, 2, "");
            excelWriter.writeData(rowNum + 1, 3, "");
            excelWriter.writeData(rowNum + 1, 4, "");
            excelWriter.writeData(rowNum + 1, 5, "");
            excelWriter.mergedRegion(rowNum + 1, rowNum + 1, 0, 5);

        }

        excelWriter.closeBook();
        /**
         * 生成文档
         */
        String docName = "${hsFullAddr}号租金及过户费明细表.xlsx";
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
     * 导出购房人资格审核阶段的 购房人员明细表
     *
     * @param request
     * @param response
     */
    public void exportGfRyMx(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = request.getParameter("prjCd");
        String hsId = request.getParameter("hsId");
        String buildId = request.getParameter("buildId");
//        String prjCd = "206";
//        String hsId = 21;
//        String buildId = 11;
        // 将查询参数传递给服务组件
        XmlBean opData = new XmlBean();
//        opData.setStrValue("OpData.ruleCd", "exportGfryMxRule");
        opData.setStrValue("OpData.buildId", buildId);
        opData.setStrValue("OpData.prjCd", prjCd);
        svcRequest.addOp("EXPORT_GFR_MX_CMPT", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //获取要插入表中的数据
            XmlBean rsqData = svcResponse.getFirstOpRsp("EXPORT_GFR_MX_CMPT");
            String excelPath = rsqData.getStrValue("Operation.OpResult.excelPath");
            String excelName = rsqData.getStrValue("Operation.OpResult.excelName");
            //结束操作

            String outPutFile = StringUtil.formatFilePath("server:${excelPath}");
            RequestUtil requestUtil = new RequestUtil(request);
            String contextType = requestUtil.getStrParam("contextType");
            String agent = request.getHeader("USER-AGENT");
            File localFile = new File(outPutFile);

            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, contextType, excelName, localFile, agent);
        }

    }

    /**
     * 导出购房人资格审核阶段的 购房人户籍人口明细表
     *
     * @param request
     * @param response
     */
    public void exportGfrhj(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");

//        String prjCd = "206";
//        String hsId = 21;
//        String buildId = 36;
        // 将查询参数传递给服务组件
        XmlBean opData = new XmlBean();
//        opData.setStrValue("OpData.ruleCd", "exportGfrhjMxRule");
        opData.setStrValue("OpData.buildId", buildId);
        opData.setStrValue("OpData.prjCd", prjCd);
        svcRequest.addOp("EXPORT_GFR_HJ_MX_CMPT", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //获取要插入表中的数据
            XmlBean rsqData = svcResponse.getFirstOpRsp("EXPORT_GFR_HJ_MX_CMPT");
            String excelPath = rsqData.getStrValue("Operation.OpResult.excelPath");
            String excelName = rsqData.getStrValue("Operation.OpResult.excelName");
            //结束操作

            String outPutFile = StringUtil.formatFilePath("server:${excelPath}");
            RequestUtil requestUtil = new RequestUtil(request);
            String contextType = requestUtil.getStrParam("contextType");
            String agent = request.getHeader("USER-AGENT");
            File localFile = new File(outPutFile);

            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, contextType, excelName, localFile, agent);
        }

    }

    /**
     * 导出 购房人资格审核阶段的 家庭成员情况申报表-----天桥
     */
    public void exportJtCyQkTQ(HttpServletRequest request, HttpServletResponse response) {
        ReportData reportData = new ReportData();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HsCtInfo");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "hsCtId");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            String hsCtId = "";
            if (pageData != null){
                hsCtId = pageData.getStrValue("PageData.Row.hsCtId");
            }
            if (hsCtId != null){
                svcRequest = RequestUtil.getSvcRequest(request);
                opData = new XmlBean();
                opData.setStrValue("OpData.entityName", "HsCtChooseInfo");
                opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsCtId");
                opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsCtId);
                opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
                opData.setStrValue("OpData.ResultFields.fieldName[1]", "buyPersonName");//hsOwnerPersons ownerCerty
                opData.setStrValue("OpData.ResultFields.fieldName[2]", "buyPersonCerty");
                opData.setStrValue("OpData.ResultFields.fieldName[3]", "chooseHsAddr");
                svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                // 调用服务
                svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
                    String buyPersonName = "";
                    String buyPersonCerty = "";
                    String chooseHsAddr = "";
                    if (pageData != null) {
                        buyPersonName = pageData.getStrValue("PageData.Row[0].buyPersonName");
                        buyPersonCerty = pageData.getStrValue("PageData.Row[0].buyPersonCerty");
                        chooseHsAddr = pageData.getStrValue("PageData.Row[0].chooseHsAddr");
                    }
                    reportData.addParameters("buyPersonName", buyPersonName);
                    reportData.addParameters("buyPersonCerty", buyPersonCerty);
                    reportData.addParameters("chooseHsAddr", chooseHsAddr);
                }
            }
        }
        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        opData.setStrValue("OpData.ruleCd", "获取家庭申报表路径");
        svcRequest.addOp("EXECUTE_RULE", opData);
        svcResponse = query(svcRequest);
        String relPath = "";
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            if (opResult != null) {
                relPath = opResult.getStrValue("Operation.OpResult.Result.relPath");
            }
        }
        String sysDateStr = DateUtil.toStringYmdWthH(new Date());
        String downloadPath = "reports/report/${sysDateStr}/${hsId}/${hsId}_choose.docx";
        String downloadFile = StringUtil.formatFilePath("webroot:${downloadPath}");
        XDocReport.reportDoc(reportData, relPath, downloadFile);
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        String name = "${hsId}家庭成员情况.docx"
        File localFile = new File(downloadFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, name, localFile, agent);
    }

    /*----------------------------------导出结算物业供暖费 信息-----------------------------------------*/
    /**
     * 物业供暖明细
     */
    public void exportWyGnMx(HttpServletRequest request, HttpServletResponse response) {
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
//        String buildId = "4";
        //保存参数为文档所用
        ReportData reportData = new ReportData();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 院落地址
        String buildAddr = "";
        // 生成文档的路径
        String outFilePath = "";
        String docName = "";
        if (StringUtil.isNotEmptyOrNull(buildId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            // 查询的字段
            opData.setStrValue("OpData.ResultField.fieldName", "buildAddr"); // 院落地址
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    buildAddr = queryResult.getValue("OpResult.buildAddr");
                    reportData.addParameters("buildAddr", buildAddr);
                }
            }
            svcRequest = RequestUtil.getSvcRequest(request);
            opData = new XmlBean();
            opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportWyGnMxRule.groovy");
            opData.setStrValue("OpData.RuleParam.buildId", buildId);
            opData.setStrValue("OpData.RuleParam.buildAddr", buildAddr);
            opData.setStrValue("OpData.RuleParam.prjCd", prjCd);
            svcRequest.addOp("EXECUTE_RULE", opData);
            // 调用服务，执行数据查询
            svcResponse = query(svcRequest);
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            // 生成文档的路径
            outFilePath = opResult.getStrValue("Operation.OpResult.RuleResult.outFilePath");
            docName = opResult.getStrValue("Operation.OpResult.RuleResult.docName");
        }
        /**
         * 生成文档
         */
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outFilePath);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出结算物业供暖费 信息-----------------------------------------*/
    /**
     * 物业供暖
     */
    public void exportWyGn(HttpServletRequest request, HttpServletResponse response) {
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
//        String buildId = "5";
        //保存参数为文档所用
        ReportData reportData = new ReportData();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 院落地址
        String buildAddr = "";
        // 生成文档的路径
        String excelPath = "";
        if (StringUtil.isNotEmptyOrNull(buildId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            // 查询的字段
            opData.setStrValue("OpData.ResultField.fieldName", "buildAddr"); // 院落地址
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    buildAddr = queryResult.getValue("OpResult.buildAddr");
                    reportData.addParameters("buildAddr", buildAddr);
                }
            }
            svcRequest = RequestUtil.getSvcRequest(request);
            opData = new XmlBean();
//            opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportWyGnRule.groovy");
            opData.setStrValue("OpData.buildId", buildId);
            opData.setStrValue("OpData.buildAddr", buildAddr);
            opData.setStrValue("OpData.prjCd", prjCd);
            svcRequest.addOp("EXPORT_WY_GN_MX_CMPT", opData);
            // 调用服务，执行数据查询
            svcResponse = query(svcRequest);
            XmlBean opResult = svcResponse.getFirstOpRsp("EXPORT_WY_GN_MX_CMPT");
            // 生成文档的路径
            excelPath = opResult.getStrValue("Operation.OpResult.excelPath");

        }
        /**
         * 生成文档
         */
        String docName = "${buildAddr}物业供暖明细.xlsx";
        String downloadPath = "${excelPath}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出办理网签、核税、缴税等手续合同 信息-----------------------------------------*/
    /**
     * 办理网签、核税、缴税等手续合同明细表
     * @param request
     * @param response
     */
    public void exportWqHsJsMx(HttpServletRequest request, HttpServletResponse response) {
        String prjCd = request.getParameter("prjCd");
        String hsId = request.getParameter("hsId");
//        String hsCtId = "14";
        ReportData reportData = new ReportData();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 生成文档的路径
        String excelPath = "";
        String ctPsNames = "";

        String hsCtId = "";
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
        }
        XmlBean opData = new XmlBean();

        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/generateDocRule/exportWqHsJsMxRule.groovy");
        opData.setStrValue("OpData.RuleParam.hsCtId", hsCtId);
        opData.setStrValue("OpData.RuleParam.prjCd", prjCd);
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            // 生成文档的路径
            excelPath = opResult.getStrValue("Operation.OpResult.RuleResult.excelPath");
            ctPsNames = opResult.getStrValue("Operation.OpResult.RuleResult.ctPsNames");
        }
        /**
         * 生成文档
         */
        String docName = "${ctPsNames}合同金额明细表.xlsx";
        String downloadPath = "${excelPath}";
        String outPutFile = StringUtil.formatFilePath("server:${downloadPath}");
        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /*----------------------------------导出 定向审批表-----------------------------------------*/
    /**
     * 导出 定向审批表
     * @param request
     * @param response
     */
    public void exportDxFsP(HttpServletRequest request, HttpServletResponse response) {
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 院落地址
        String buildFullAddr = "";
        String docName = "";
        String outPutFile = "";
        XmlBean opData = new XmlBean();
        if (StringUtil.isNotEmptyOrNull(buildId)) {
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            // 查询的字段
            opData.setStrValue("OpData.ResultField.fieldName", "buildFullAddr");           // 院落地址
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    buildFullAddr = queryResult.getValue("OpResult.buildFullAddr");

                }
            }

            svcRequest = RequestUtil.getSvcRequest(request);
            opData = new XmlBean();
            opData.setStrValue("OpData.ruleCd", "exportDxFsPRule");
            opData.setStrValue("OpData.RuleParam.buildId", buildId);
            opData.setStrValue("OpData.RuleParam.buildFullAddr", buildFullAddr);
            opData.setStrValue("OpData.RuleParam.prjCd", prjCd);
            svcRequest.addOp("EXECUTE_RULE", opData);
            // 调用服务，执行数据查询
            svcResponse = query(svcRequest);
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            //
            docName = opResult.getStrValue("Operation.OpResult.RuleResult.docName");
            outPutFile = opResult.getStrValue("Operation.OpResult.RuleResult.outPutFile");
        }

        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }

    /**
     * 导出 定向审批表----------天桥
     * @param request
     * @param response
     */
    public void exportDxFsPTQ(HttpServletRequest request, HttpServletResponse response) {
        String buildId = request.getParameter("buildId");
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 院落地址
        String buildFullAddr = "";
        String docName = "";
        String outPutFile = "";
        XmlBean opData = new XmlBean();
        if (StringUtil.isNotEmptyOrNull(buildId)) {
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            // 查询的字段
            opData.setStrValue("OpData.ResultField.fieldName", "buildFullAddr");           // 院落地址
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    buildFullAddr = queryResult.getValue("OpResult.buildFullAddr");

                }
            }

            svcRequest = RequestUtil.getSvcRequest(request);
            opData = new XmlBean();
            opData.setStrValue("OpData.ruleCd", "exportDxFsPRuleTQ");
            opData.setStrValue("OpData.RuleParam.buildId", buildId);
            opData.setStrValue("OpData.RuleParam.buildFullAddr", buildFullAddr);
            opData.setStrValue("OpData.RuleParam.prjCd", prjCd);
            svcRequest.addOp("EXECUTE_RULE", opData);
            // 调用服务，执行数据查询
            svcResponse = query(svcRequest);
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            //
            docName = opResult.getStrValue("Operation.OpResult.RuleResult.docName");
            outPutFile = opResult.getStrValue("Operation.OpResult.RuleResult.outPutFile");
        }

        RequestUtil requestUtil = new RequestUtil(request);
        String contextType = requestUtil.getStrParam("contextType");
        String agent = request.getHeader("USER-AGENT");
        File localFile = new File(outPutFile);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, contextType, docName, localFile, agent);
    }


}