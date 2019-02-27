import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/4/2
 * Time: 19:31
 * To change this template use file | Settings | File Templates.
 */
class ph008 extends GroovyController {
    /**
     * 开发测试用，后续删除
     */
    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();

        return new ModelAndView("/eland/ph/ph008/ph008_", modelMap);
    }

    /**
     * 开发测试用，刷数据使用
     */
    public void devTest(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = request.getParameter("prjCd");
        XmlBean paramData = new XmlBean();
        String nodePath = "OpData.";
        paramData.setStrValue(nodePath + "ruleFile", "webroot:/config/${prjCd}/updateHsCd_fresh.groovy");
        paramData.setStrValue(nodePath + "RuleParam.prjCd", prjCd);
        svcRequest.addOp("EXECUTE_RULE", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printAjax(response, """{success: "${svcResponse.isSuccess()}", errMsg:"${
            svcResponse.getErrMsg()
        }"}""");
    }

    /**
     * 光源里房产数据导入  ，excel。
     */
    public void gylHsData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = request.getParameter("prjCd");
        MultipartFile localFile = super.getFile(request, "gylHsDataFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;

        if (fileType.contains(".xls") || fileType.contains(".xlsx")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            File saveFile = ServerFile.createFile(remoteFileName);
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = new FileOutputStream(saveFile);
                inputStream = localFile.getInputStream();
                // 保存待上传文件信息
                byte[] b = new byte[1024 * 5];
                int len;
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                }
                outputStream.flush();
            } catch (IOException e) {
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (Exception e) {
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (Exception e) {
                    }
                }
            }
            XmlBean paramData = new XmlBean();
            String nodePath = "OpData.";
            paramData.setStrValue(nodePath + "filePath", saveFile.getAbsolutePath());
            paramData.setStrValue(nodePath + "configRowNum", "6");
            paramData.setStrValue(nodePath + "serviceName", "SAVE_CAN_DATA_INFO");
            paramData.setStrValue(nodePath + "dealRowSize", "1");
            svcRequest.addOp("IMPORT_EXCEL_INFO", paramData);
            // 调用服务，执行数据查询
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                result = true;
            } else {
                errMsg = svcResponse.getErrMsg();
            }
            saveFile.delete();
        } else {
            errMsg = "文件格式不正确";
        }
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    //###########################################   以上是刷数据功能   分割线   ########################################################

    /** 处理预审操作 */
    public ModelAndView doYuShen(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult1");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment1");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime1");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewyushen";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_yushen";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 完成预审操作  */
    public void completeYuShen(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult1", request.getParameter("procResult1"));
        opData.setStrValue(nodePath + "EntityData.comment1", request.getParameter("comment1"));
        String recordTime = request.getParameter("recordTime1");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime1", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime1"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime1", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult1: '${request.getParameter("procResult1")}'");
    }

    /** 确认签约操作  */
    public ModelAndView cfmContract(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String schemeType = request.getParameter("schemeType");
        XmlBean opData = new XmlBean();
        // 腾退签约动作组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue(nodePath + "HsCtInfo.schemeType", schemeType);
        svcRequest.addOp("CHANGE_TO_FORMAL_SIGN_TT", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 取消签约操作  */
    public ModelAndView cancelContract(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        XmlBean opData = new XmlBean();
        // 腾退签约动作组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
        svcRequest.addOp("CHANGE_TO_UN_SIGNED", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 审批房屋  */
    public ModelAndView examineHs(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String manualAuditResult = request.getParameter("manualAuditResult");
        XmlBean opData = new XmlBean();
        // 腾退签约动作组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.manualAuditResult", manualAuditResult);
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 提交 购房人资格审核  */
    public void cfmGfSh(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtChooseId = request.getParameter("hsCtChooseId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HsCtChooseInfo");
        opData.setStrValue(nodePath + "EntityData.hsCtChooseId", hsCtChooseId);
        opData.setStrValue(nodePath + "EntityData.zgResult", request.getParameter("zgResult"));
        String zgTime = request.getParameter("zgTime");
        if (StringUtil.isEmptyOrNull(zgTime)) {
            opData.setStrValue(nodePath + "EntityData.zgTime", DateUtil.toStringYmd(DateUtil.getSysDate()));
        } else {
            opData.setStrValue(nodePath + "EntityData.zgTime", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("zgTime"))));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsCtChooseId: '${hsCtChooseId}', zgResult: '${request.getParameter("zgResult")}' ");
    }

    /** 提交 领取购房合同材料  */
    public void cfmGfhtcl(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtChooseId = request.getParameter("hsCtChooseId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HsCtChooseInfo");
        opData.setStrValue(nodePath + "EntityData.hsCtChooseId", hsCtChooseId);
        opData.setStrValue(nodePath + "EntityData.clLqStatus", request.getParameter("clLqStatus"));
        String clLqTime = request.getParameter("clLqTime");
        if (StringUtil.isEmptyOrNull(clLqTime)) {
            opData.setStrValue(nodePath + "EntityData.clLqTime", DateUtil.toStringYmd(DateUtil.getSysDate()));
        } else {
            opData.setStrValue(nodePath + "EntityData.clLqTime", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("clLqTime"))));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsCtChooseId: '${hsCtChooseId}', clLqStatus: '${request.getParameter("clLqStatus")}' ");
    }

    /** 提交 签订购房合同  */
    public void cfmQdht(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String newHsId = request.getParameter("newHsId");
        String hsCtChooseId = request.getParameter("hsCtChooseId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "NewHsInfo");
        opData.setStrValue(nodePath + "EntityData.newHsId", newHsId);
        opData.setStrValue(nodePath + "EntityData.cmpQnCost", request.getParameter("cmpQnCost"));
        opData.setStrValue(nodePath + "EntityData.cmpWyCost", request.getParameter("cmpWyCost"));
        opData.setStrValue(nodePath + "EntityData.grQnCost", request.getParameter("grQnCost"));
        opData.setStrValue(nodePath + "EntityData.jsStatus", request.getParameter("jsStatus"));
        opData.setStrValue(nodePath + "EntityData.buyHsCtDate", request.getParameter("qdGfTime"));
//        opData.setStrValue(nodePath + "EntityData.jsDocIds", request.getParameter("jsDocIds"));

        String jsTime = request.getParameter("jsTime");
        String rzTime = request.getParameter("rzTime");
        if (StringUtil.isNotEmptyOrNull(jsTime) && StringUtil.isNotEmptyOrNull(rzTime)) {
            opData.setStrValue(nodePath + "EntityData.jsTime", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("jsTime"))));
            opData.setStrValue(nodePath + "EntityData.rzTime", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("rzTime"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.jsTime", DateUtil.toStringYmd(DateUtil.getSysDate()));
            opData.setStrValue(nodePath + "EntityData.rzTime", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }

        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);

        opData = new XmlBean();
        // 删除房产组件
        nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HsCtChooseInfo");
        opData.setStrValue(nodePath + "EntityData.hsCtChooseId", hsCtChooseId);
        opData.setStrValue(nodePath + "EntityData.qdGfStaus", request.getParameter("qdGfStatus"));
        opData.setStrValue(nodePath + "EntityData.qdGfTime", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("qdGfTime"))));
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 打开退租申请页面  */
    public ModelAndView openTzsh(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult2");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment2");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime2");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewTzsq";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openTzsq";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 退租申请  */
    public void submitTzsh(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult2", request.getParameter("procResult2"));
        opData.setStrValue(nodePath + "EntityData.comment2", request.getParameter("comment2"));
        String recordTime = request.getParameter("recordTime2");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime2", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime2"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime2", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult2: '${request.getParameter("procResult2")}'");
    }

    /** 打开私房核验页面  */
    public ModelAndView openSfhy(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult3");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment3");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime3");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }
        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewSfhy";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openSfhy";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 退租申请  */
    public void submitSfhy(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult3", request.getParameter("procResult3"));
        opData.setStrValue(nodePath + "EntityData.comment3", request.getParameter("comment3"));
        String recordTime = request.getParameter("recordTime3");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime3", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime3"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime3", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult3: '${request.getParameter("procResult3")}'");
    }

    /** 打开 完成退租 页面  */
    public ModelAndView openWctz(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult11");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment11");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime11");
        shBean.setStrValue("OpData.ResultFields.fieldName[4]", "hsPubZj");
        shBean.setStrValue("OpData.ResultFields.fieldName[5]", "hsPubGhf");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }


        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewWctz";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openWctz";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 完成退租  */
    public void submitWctz(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult11", request.getParameter("procResult11"));
        opData.setStrValue(nodePath + "EntityData.comment11", request.getParameter("comment11"));
        String recordTime = request.getParameter("recordTime11");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime11", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime11"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime11", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        opData.setStrValue(nodePath + "EntityData.hsPubZj", request.getParameter("hsPubZj"));
        opData.setStrValue(nodePath + "EntityData.hsPubGhf", request.getParameter("hsPubGhf"));
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult11: '${request.getParameter("procResult11")}'");
    }

    /** 打开 办理网签缴税核税 页面  */
    public ModelAndView openWqjs(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult4");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment4");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime4");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }


        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewWqjs";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openWqjs";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 办理网签缴税核税  */
    public void submitWqjs(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult4", request.getParameter("procResult4"));
        opData.setStrValue(nodePath + "EntityData.comment4", request.getParameter("comment4"));
        String recordTime = request.getParameter("recordTime4");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime4", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime4"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime4", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult4: '${request.getParameter("procResult4")}'");
    }

    /** 打开 办理房屋所有权转让登记 页面  */
    public ModelAndView openZrdj(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult5");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment5");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime5");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewZrdj";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openZrdj";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 房屋所有权转让登记  */
    public void submitZrdj(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult5", request.getParameter("procResult5"));
        opData.setStrValue(nodePath + "EntityData.comment5", request.getParameter("comment5"));
        String recordTime = request.getParameter("recordTime5");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime5", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime5"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime5", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult5: '${request.getParameter("procResult5")}'");
    }

    /** 打开 领取新房产证 页面  */
    public ModelAndView openLqxz(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult6");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment6");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime6");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewLqxz";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openLqxz";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 领取新房产证  */
    public void submitLqxz(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult6", request.getParameter("procResult6"));
        opData.setStrValue(nodePath + "EntityData.comment6", request.getParameter("comment6"));
        String recordTime = request.getParameter("recordTime6");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime6", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime6"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime6", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult6: '${request.getParameter("procResult6")}'");
    }

    /** 打开 腾房交房 页面  */
    public ModelAndView openTfjf(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");


        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult7");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment7");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime7");
        shBean.setStrValue("OpData.ResultFields.fieldName[4]", "jfAttr1");
        shBean.setStrValue("OpData.ResultFields.fieldName[5]", "jfAttr2");
        shBean.setStrValue("OpData.ResultFields.fieldName[6]", "jfAttr3");
        shBean.setStrValue("OpData.ResultFields.fieldName[7]", "jfAttr4");
        shBean.setStrValue("OpData.ResultFields.fieldName[8]", "jfAttr5");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewTfjf";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openTfjf";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 打开 房屋核验-----天桥 页面  */
    public ModelAndView openFwhyTQ(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");


        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult12");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment12");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime12");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewFwhyTQ";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openFwhyTQ";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 打开 资格审核处理-----天桥 页面  */
    public ModelAndView openZgshTQ(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");


        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult14");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment14");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime14");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewZgshTQ";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openZgshTQ";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 打开 配售居室审核-----天桥 页面  */
    public ModelAndView openPsshTQ(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");


        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult13");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment13");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime13");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewPsshTQ";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openPsshTQ";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 打开 发放购房合同处理-----什刹海 页面  */
    public ModelAndView openFfhtSCH(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String method = request.getParameter("method");


        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult15");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment15");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime15");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewFfhtSCH";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openFfhtSCH";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 腾房交房  */
    public void submitTfjf(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult7", request.getParameter("procResult7"));
        opData.setStrValue(nodePath + "EntityData.comment7", request.getParameter("comment7"));
        String recordTime = request.getParameter("recordTime7");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime7", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime7"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime7", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        opData.setStrValue(nodePath + "EntityData.jfAttr1", request.getParameter("jfAttr1"));
        opData.setStrValue(nodePath + "EntityData.jfAttr2", request.getParameter("jfAttr2"));
        opData.setStrValue(nodePath + "EntityData.jfAttr3", request.getParameter("jfAttr3"));
        opData.setStrValue(nodePath + "EntityData.jfAttr4", request.getParameter("jfAttr4"));
        opData.setStrValue(nodePath + "EntityData.jfAttr5", request.getParameter("jfAttr5"));
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult7: '${request.getParameter("procResult7")}'");
    }

    /** 处理 房屋核验-----天桥  */
    public void submitFwhyTQ(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult12", request.getParameter("procResult12"));
        opData.setStrValue(nodePath + "EntityData.comment12", request.getParameter("comment12"));
        String recordTime = request.getParameter("recordTime12");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime12", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime12"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime12", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult12: '${request.getParameter("procResult12")}'");
    }

    /** 处理 资格审核-----天桥  */
    public void submitZgshTQ(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult14", request.getParameter("procResult14"));
        opData.setStrValue(nodePath + "EntityData.comment14", request.getParameter("comment14"));
        String recordTime = request.getParameter("recordTime14");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime14", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime14"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime14", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult14: '${request.getParameter("procResult14")}'");
    }

    /** 处理 配售审核-----天桥  */
    public void submitPsshTQ(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult13", request.getParameter("procResult13"));
        opData.setStrValue(nodePath + "EntityData.comment13", request.getParameter("comment13"));
        String recordTime = request.getParameter("recordTime13");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime13", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime13"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime13", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult13: '${request.getParameter("procResult13")}'");
    }

    /** 处理 发放购房合同处理-----什刹海  */
    public void submitFfhtSCH(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 删除房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult15", request.getParameter("procResult15"));
        opData.setStrValue(nodePath + "EntityData.comment15", request.getParameter("comment15"));
        String recordTime = request.getParameter("recordTime15");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime15", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime15"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime15", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult15: '${request.getParameter("procResult15")}'");
    }

    /** 打开安置协议结算  */
    public ModelAndView openAzjs(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult8");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment8");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime8");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewAzjs";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openAzjs";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 打开面积补差结算-----什刹海  */
    public ModelAndView openMjjs(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult16");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment16");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime16");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewMjjs";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openMjjs";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 提交 安置协议结算  */
    public void submitAzjs(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult8", request.getParameter("procResult8"));
        opData.setStrValue(nodePath + "EntityData.comment8", request.getParameter("comment8"));
        String recordTime = request.getParameter("recordTime8");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime8", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime8"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime8", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult8: '${request.getParameter("procResult8")}'");
    }

    /** 提交 面积补差结算-----什刹海  */
    public void submitMjjs(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult16", request.getParameter("procResult16"));
        opData.setStrValue(nodePath + "EntityData.comment16", request.getParameter("comment16"));
        String recordTime = request.getParameter("recordTime16");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime16", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime16"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime16", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult16: '${request.getParameter("procResult16")}'");
    }

    /** 打开 户口外迁OA审核  */
    public ModelAndView openQhsh(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult9");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment9");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime9");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewQhsh";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openQhsh";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 提交 户口外迁OA审核  */
    public void submitQhsh(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult9", request.getParameter("procResult9"));
        opData.setStrValue(nodePath + "EntityData.comment9", request.getParameter("comment9"));
        String recordTime = request.getParameter("recordTime9");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime9", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime9"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime9", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult9: '${request.getParameter("procResult9")}'");
    }

    /** 打开 支付户口外迁费 */
    public ModelAndView openZffy(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult10");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment10");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime10");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewZffy";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openZffy";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 处理 支付户口外迁费 */
    public void submitZffy(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult10", request.getParameter("procResult10"));
        opData.setStrValue(nodePath + "EntityData.comment10", request.getParameter("comment10"));
        String recordTime = request.getParameter("recordTime10");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime10", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime10"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime10", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult10: '${request.getParameter("procResult10")}'");
    }

    /** 打开  领取新租赁合同  */
    public ModelAndView openLqxht(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String method = request.getParameter("method");

        XmlBean shBean = new XmlBean();
        shBean.setStrValue("OpData.entityName", "HouseInfo");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        shBean.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult11");
        shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment11");
        shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime11");
        svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
        SvcResponse shResult = query(svcRequest);
        if (shResult.isSuccess()) {
            XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
            XmlBean hsStatus = new XmlBean();
            if (hsStatusResult != null) {
                hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[0]");
            }
            if (hsStatus != null) {
                modelMap.put("hsStatus", hsStatus.getRootNode());
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewLqxht";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openLqxht";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 提交  领取新租赁合同   */
    public void submitLqxht(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 房产组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HouseInfo");
        opData.setStrValue(nodePath + "EntityData.hsId", hsId);
        opData.setStrValue(nodePath + "EntityData.procResult11", request.getParameter("procResult11"));
        opData.setStrValue(nodePath + "EntityData.comment11", request.getParameter("comment11"));
        String recordTime = request.getParameter("recordTime11");
        if (StringUtil.isNotEmptyOrNull(recordTime)) {
            opData.setStrValue(nodePath + "EntityData.recordTime11", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(request.getParameter("recordTime11"))));
        } else {
            opData.setStrValue(nodePath + "EntityData.recordTime11", DateUtil.toStringYmd(DateUtil.getSysDate()));
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "hsId: '${hsId}', procResult11: '${request.getParameter("procResult11")}'");
    }

    /** 打开房屋附件上传界面  */
    public ModelAndView openOash(HttpServletRequest request, HttpServletResponse response) {
        // 返回视图
        ModelMap modelMap = new ModelMap();
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String relId = request.getParameter("relId");
        String relType = request.getParameter("relType");
        String method = request.getParameter("method");
        String prjCd = request.getParameter("prjCd");
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", relType);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", relId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "prjCd");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", prjCd);
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
        //只查询状态为 1 的，
        opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
        // 增加查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
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
        modelMap.put("relId", relId);
        modelMap.put("relType", relType);
        String toOpPage = "";
        if (StringUtil.isEqual(method, "view")) {
            toOpPage = "/eland/ph/ph008/ph008_viewTfjf";
        } else {
            toOpPage = "/eland/ph/ph008/ph008_openOash";
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /** 调用规则计算 开发商结算金额 */
    public void calcSettlement(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String newHsId = request.getParameter("newHsId");
        String prjCd = request.getParameter("prjCd");
        String rzTime = request.getParameter("rzTime");
        String qdGfTime = request.getParameter("qdGfTime");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ruleCd", "calculateWyGnJe");
        opData.setStrValue("OpData.RuleParam.hsId", hsId);
        if (StringUtil.isNotEmptyOrNull(rzTime)) {
            opData.setStrValue("OpData.RuleParam.rzTime", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(rzTime)));
        }
        if (StringUtil.isNotEmptyOrNull(qdGfTime)) {
            opData.setStrValue("OpData.RuleParam.qyTime", DateUtil.toStringYmd(DateUtil.toDateYmdWthH(qdGfTime)));
        } else {
            ResponseUtil.printAjax(response, """{"success": false, "errMsg": "请指定签约时间"} """);
            return;
        }
        svcRequest.addOp("EXECUTE_RULE", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        String newHsIdRu = "";
        String rzTimeRu = "";
        String cmpQnCost = "";
        String cmpWyCost = "";
        String grQnCost = "";
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            int newHsNum = opResult.getListNum("Operation.OpResult.NewHsInfos.NewHsInfo");
            for (int i = 0; i < newHsNum; i++) {
                newHsIdRu = opResult.getStrValue("Operation.OpResult.NewHsInfos.NewHsInfo[${i}].newHsId");
                if (StringUtil.isEqual(newHsIdRu, newHsId)) {
                    rzTimeRu = opResult.getStrValue("Operation.OpResult.NewHsInfos.NewHsInfo[${i}].rzTime");
                    if (StringUtil.isNotEmptyOrNull(rzTimeRu)) {
                        rzTimeRu = DateUtil.toStringYmdWthH(DateUtil.toDateYmdHms(rzTimeRu));
                    }
                    cmpQnCost = opResult.getStrValue("Operation.OpResult.NewHsInfos.NewHsInfo[${i}].cmpQnCost");
                    cmpWyCost = opResult.getStrValue("Operation.OpResult.NewHsInfos.NewHsInfo[${i}].cmpWyCost");
                    grQnCost = opResult.getStrValue("Operation.OpResult.NewHsInfos.NewHsInfo[${i}].grQnCost");
                }
            }
        }
        ResponseUtil.printAjax(response, """{"success": ${svcResponse.isSuccess()}, "errMsg": "${
            svcResponse.getErrMsg()
        }", "hsId":"${hsId}","rzTime":"${
            rzTimeRu
        }","cmpQnCost":"${
            cmpQnCost
        }","cmpWyCost":"${cmpWyCost}","grQnCost":"${grQnCost}"}""");
    }
}