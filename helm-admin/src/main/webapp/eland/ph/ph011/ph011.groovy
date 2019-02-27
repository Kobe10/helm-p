import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ph011 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", request.getParameter("buildId"));
        modelMap.put("id", request.getParameter("upRegId"));
        return new ModelAndView("/eland/ph/ph011/ph011", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView bldFjDialog(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", request.getParameter("buildId"));
        return new ModelAndView("/eland/ph/ph011/ph011_bld_dialog", modelMap);
    }

    /**
     * 右侧面板显示文件夹下级文件及文件夹(区域下的子区域信息)
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView showFile(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        // 获取输入参数
        String rhtRegId = request.getParameter("rhtRegId");
        // 获取指定rhtRegId下级文件
        // 查询条件
        String regUseType = request.getParameter("regUseType");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = request.getParameter("prjCd");
        // 查询区域树
        XmlBean opData = new XmlBean();
        opData.setValue("OpData.PrjReg.prjCd", prjCd);
        opData.setValue("OpData.PrjReg.regUseType", regUseType);
        svcRequest.addOp("QUERY_REG_TREE", opData)
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        if (result) {
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_REG_TREE").getBeanByPath("Operation.OpResult.PrjReg");
            if (treeBean != null) {
                int count = treeBean.getListNum("PrjReg.Node");
                List folderList = new ArrayList();
                for (int i = 0; i < count; i++) {
                    String upRegId = treeBean.getStrValue("""PrjReg.Node[${i}].upRegId""");
                    String regId = treeBean.getStrValue("""PrjReg.Node[${i}].regId""");
                    if (StringUtil.isEqual(rhtRegId, upRegId)) {
                        XmlBean temp = treeBean.getBeanByPath("PrjReg.Node[" + i + "]");
                        folderList.add(temp.getRootNode());
                    }
                    if (StringUtil.isEqual(regId, rhtRegId)) {
                        modelMap.put("upRegId", upRegId);
                    }
                }
                modelMap.put("folderList", folderList);
            }
        }
        return new ModelAndView("/eland/ph/ph011/ph01101", modelMap);
    }

    /**
     * 右侧面板显示建筑相关信息(包含房屋和附件)
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView showBuildFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String cBuildId = request.getParameter("cBuildId");
        String cRegId = request.getParameter("cRegId");
        String prjCd = request.getParameter("prjCd");
        modelMap.put("cBuildId", cBuildId)
        // 查询建筑房屋
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "HouseInfo");

        reqData.setStrValue("OpData.ttOrgId", request.getParameter("rhtOrgId"));
        reqData.setStrValue("OpData.ttRegId", request.getParameter("cRegId"));
        reqData.setStrValue("OpData.regUseType", request.getParameter("regUseType"));

        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "buildId");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", cBuildId);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        reqData.setStrValue("OpData.ResultFields.fieldName[1]", "hsOwnerPersons");
        reqData.setStrValue("OpData.ResultFields.fieldName[2]", "hsFullAddr");
        svcRequest.addOp("FILTER_QUERY_ENTITY_DATA", reqData);

        // 查询建筑附件
        reqData = new XmlBean();
        // 关联编号
        reqData.setStrValue("OpData.entityName", "SvcDocInfo");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "relId");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", cBuildId);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        // 关联类型
        reqData.setStrValue("OpData.Conditions.Condition[1].fieldName", "relType");
        reqData.setStrValue("OpData.Conditions.Condition[1].fieldValue", "200");
        reqData.setStrValue("OpData.Conditions.Condition[1].operation", "=");
        // 项目编码
        reqData.setStrValue("OpData.Conditions.Condition[2].fieldName", "prjCd");
        reqData.setStrValue("OpData.Conditions.Condition[2].fieldValue", prjCd);
        reqData.setStrValue("OpData.Conditions.Condition[2].operation", "=");

        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "docId");
        reqData.setStrValue("OpData.ResultFields.fieldName[1]", "docName");
        reqData.setStrValue("OpData.ResultFields.fieldName[2]", "docTypeName");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", reqData);

        // 查询建筑的上级区域
        reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "RegInfo");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "regId");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", cRegId);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "upRegId");
        reqData.setStrValue("OpData.ResultFields.fieldName[1]", "regId");
        svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);
        // 调服务
        SvcResponse svcResponse = query(svcRequest);
        List folderList = new ArrayList();
        if (svcResponse.isSuccess()) {
            XmlBean tempBean = svcResponse.getFirstOpRsp("FILTER_QUERY_ENTITY_DATA");
            if (tempBean != null) {
                int rowCount = tempBean.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < rowCount; i++) {
                    Map rowMap = new HashMap();
                    String hsId = tempBean.getStrValue("Operation.OpResult.PageData.Row[" + i + "].hsId");
                    String hsOwnerPersons = tempBean.getStrValue("Operation.OpResult.PageData.Row[" + i + "].hsOwnerPersons");
                    String hsFullAddr = tempBean.getStrValue("Operation.OpResult.PageData.Row[" + i + "].hsFullAddr");
                    rowMap.put("hsId", hsId);
                    rowMap.put("hsOwnerPersons", hsOwnerPersons);
                    rowMap.put("hsFullAddr", hsFullAddr);
                    folderList.add(rowMap);
                }
                modelMap.put("folderList", folderList);
            }
            tempBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA");
            if (tempBean != null) {
                List attachment = new ArrayList();
                int rowCount = tempBean.getListNum("Operation.OpResult.PageData.Row");
                Set docTN = new HashSet();
                for (int i = 0; i < rowCount; i++) {
                    Map rowMap = new HashMap();
                    String hsId = tempBean.getStrValue("Operation.OpResult.PageData.Row[" + i + "].docId");
                    String hsOwnerPersons = tempBean.getStrValue("Operation.OpResult.PageData.Row[" + i + "].docName");
                    String hsFullAddr = tempBean.getStrValue("Operation.OpResult.PageData.Row[" + i + "].docTypeName");
                    rowMap.put("docId", hsId);
                    rowMap.put("docName", hsOwnerPersons);
                    rowMap.put("docTypeName", hsFullAddr);
                    docTN.add(hsFullAddr);
                    attachment.add(rowMap);
                }
                modelMap.put("attachment", attachment);
                modelMap.put("docTN", docTN);
            }
            tempBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
            if (tempBean != null) {
                tempBean = tempBean.getBeanByPath("OpResult.PageData");
                modelMap.put("upRegId", tempBean.getStrValue("PageData.Row.upRegId"));
            }
        }
        return new ModelAndView("/eland/ph/ph011/ph01102", modelMap);
    }

    /**
     * 右侧面板显示房屋相关信息（房屋内的附件）
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView showHouseFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String cRegId = request.getParameter("cRegId");
        String cHsId = request.getParameter("cHsId");
        String prjCd = request.getParameter("prjCd");

        // 查询房屋下的附件
        XmlBean reqData = new XmlBean();

        reqData.setStrValue("OpData.entityName", "SvcDocInfo");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "relId");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", cHsId);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        reqData.setStrValue("OpData.Conditions.Condition[1].fieldName", "relType");
        reqData.setStrValue("OpData.Conditions.Condition[1].fieldValue", "100");
        reqData.setStrValue("OpData.Conditions.Condition[1].operation", "=");

        // 项目编码
        reqData.setStrValue("OpData.Conditions.Condition[2].fieldName", "prjCd");
        reqData.setStrValue("OpData.Conditions.Condition[2].fieldValue", prjCd);
        reqData.setStrValue("OpData.Conditions.Condition[2].operation", "=");

        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "docId");
        reqData.setStrValue("OpData.ResultFields.fieldName[1]", "docName");
        reqData.setStrValue("OpData.ResultFields.fieldName[2]", "docTypeName");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", reqData);

        // 查询建筑的上级区域
        reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "RegInfo");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "regId");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", cRegId);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "upRegId");
        reqData.setStrValue("OpData.ResultFields.fieldName[1]", "regId");
        svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);

        SvcResponse svcResponse = query(svcRequest);
        List attachment = new ArrayList();
        if (svcResponse.isSuccess()) {
            XmlBean tempBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            if (tempBean != null) {
                int rowCount = tempBean.getListNum("OpResult.PageData.Row");
                Set docTN = new HashSet();
                for (int i = 0; i < rowCount; i++) {
                    Map rowMap = new HashMap();
                    String docId = tempBean.getStrValue("OpResult.PageData.Row[" + i + "].docId");
                    String docName = tempBean.getStrValue("OpResult.PageData.Row[" + i + "].docName");
                    String docTypeName = tempBean.getStrValue("OpResult.PageData.Row[" + i + "].docTypeName");
                    rowMap.put("docId", docId);
                    rowMap.put("docName", docName);
                    rowMap.put("docTypeName", docTypeName);
                    docTN.add(docTypeName);

                    attachment.add(rowMap);
                }
                modelMap.put("relId", cHsId);
                modelMap.put("attachment", attachment);
                modelMap.put("docTN", docTN);
                // 获取上级
                tempBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                if (tempBean != null) {
                    tempBean = tempBean.getBeanByPath("OpResult.PageData");
                    modelMap.put("upRegId", tempBean.getStrValue("PageData.Row.upRegId"));
                }
            }
        }
        return new ModelAndView("/eland/ph/ph011/ph01103", modelMap);
    }

    /**
     * 查询附件文件夹内容
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView selAttachmentFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String relId = request.getParameter("relId");
        String relType = request.getParameter("relType");
        String docTypeName = request.getParameter("docTypeName");
        String prjCd = request.getParameter("prjCd");

        XmlBean reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "SvcDocInfo");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "relId");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", relId);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");//

        reqData.setStrValue("OpData.Conditions.Condition[1].fieldName", "relType");
        reqData.setStrValue("OpData.Conditions.Condition[1].fieldValue", relType);
        reqData.setStrValue("OpData.Conditions.Condition[1].operation", "=");

        reqData.setStrValue("OpData.Conditions.Condition[2].fieldName", "docTypeName");
        reqData.setStrValue("OpData.Conditions.Condition[2].fieldValue", docTypeName);
        reqData.setStrValue("OpData.Conditions.Condition[2].operation", "=");

        //查询没有被删除的文件    statusCd，1：未删除，2：已删除
        reqData.setStrValue("OpData.Conditions.Condition[3].fieldName", "statusCd");
        reqData.setStrValue("OpData.Conditions.Condition[3].fieldValue", "1");
        reqData.setStrValue("OpData.Conditions.Condition[3].operation", "=");

        // 项目编码
        reqData.setStrValue("OpData.Conditions.Condition[4].fieldName", "prjCd");
        reqData.setStrValue("OpData.Conditions.Condition[4].fieldValue", prjCd);
        reqData.setStrValue("OpData.Conditions.Condition[4].operation", "=");

        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "docId");
        reqData.setStrValue("OpData.ResultFields.fieldName[1]", "docName");
        reqData.setStrValue("OpData.ResultFields.fieldName[3]", "docTypeName");


        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", reqData);
        SvcResponse svcResponse = query(svcRequest);
        List attachmentList = new ArrayList();
        if (svcResponse.isSuccess()) {
            XmlBean folderBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            if (folderBean != null) {
                int rowCount = folderBean.getListNum("OpResult.PageData.Row");
                for (int i = 0; i < rowCount; i++) {
                    Map rowMap = new HashMap();
                    String docId = folderBean.getStrValue("OpResult.PageData.Row[" + i + "].docId");
                    String docName = folderBean.getStrValue("OpResult.PageData.Row[" + i + "].docName");
                    rowMap.put("docId", docId);
                    rowMap.put("docName", docName);
                    attachmentList.add(rowMap);
                }
                modelMap.put("attachmentList", attachmentList);
            }
        }
        modelMap.put("docTypeName", docTypeName);
        return new ModelAndView("/eland/ph/ph011/ph01104", modelMap);
    }

    /**
     * 右侧面板显示文件夹上级文件及文件夹
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView showUpFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String regId = request.getParameter("regId");
        String jsonStr = "";
        if (StringUtil.isEqual(regId, "1")) {
            jsonStr = """{success:${false}, errMsg:"当前已到根节点",upRegId:"0"}""";
        } else {
            XmlBean reqData = new XmlBean();
            reqData.setStrValue("OpData.entityName", "RegInfo");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "regId");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", regId);
            reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");

            reqData.setStrValue("OpData.ResultFields.fieldName[0]", "upRegId");
            reqData.setStrValue("OpData.ResultFields.fieldName[1]", "regId");
            svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);
            String upRegId = "";
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean folderBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                upRegId = folderBean.getStrValue("PageData.Row.upRegId");
            }
            jsonStr = """{success:${svcResponse.isSuccess()}, errMsg:"${svcResponse.getErrMsg()}",upRegId:${
                upRegId
            }}""";
        }
        ResponseUtil.printAjax(response, jsonStr);
    }
}
