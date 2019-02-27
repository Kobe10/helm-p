import com.shfb.oframe.core.util.common.DateUtil
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

/**
 * 项目资料管理
 * Created by Administrator on 2014/6/23 0023.
 */
class pj015 extends GroovyController {

    /**
     * 初始化 项目资料管理
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj015/pj015", modelMap);
    }

    /**
     * 修改文件
     * @param request
     * @param response
     * @return
     */
    public ModelAndView editFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        //返回通用参数
        modelMap.put("prjCd", request.getParameter("prjCd"))
        modelMap.put("fileFlag", request.getParameter("fileFlag"));      //文件标志：0：文件， 1：文件夹
        // 获取输入参数docId
        String docId = request.getParameter("docId");

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 查询条件
        opData.setStrValue(prePath + "entityName", "DocumentInfo");
        opData.setStrValue(prePath + "entityKey", docId);
        // 调用服务查询实体全部属性
        svcRequest.addOp("QUERY_ALL_ENTITY", opData);
        // 查询操作日志
        svcRequest.addOp("QUERY_LOGING_INFO_CMPT", opData);

        SvcResponse svcResponse = query(svcRequest);
        List resultList = new ArrayList();
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.DocumentInfo");
            if (resultBean != null) {
                //将查询结果返回到前端处理
                modelMap.put("docInfo", resultBean.getRootNode());
                //单独处理文件所赋予的权限    1:角色权限，   2：个人权限
                List roleList = new ArrayList();
                List userList = new ArrayList();
                int rhtNum = resultBean.getListNum("DocumentInfo.DocRhtCtrs.DocRhtCtr");
                for (int i = 0; i < rhtNum; i++) {
                    String docRelType = resultBean.getStrValue("DocumentInfo.DocRhtCtrs.DocRhtCtr[${i}].docRelType");
                    String docRelId = resultBean.getStrValue("DocumentInfo.DocRhtCtrs.DocRhtCtr[${i}].docRelId");
                    if (StringUtil.isEqual("1", docRelType)) {
                        roleList.add(docRelId);
                    } else if (StringUtil.isEqual("2", docRelType)) {
                        userList.add(docRelId);
                    }
                }
                //将结果拼接为字符串 传到前端修改用
                modelMap.put("roleStr", roleList.join(","));
                modelMap.put("userStr", userList.join(","));
                //将结果list传到前端展示用
                modelMap.put("roleList", roleList);
                modelMap.put("userList", userList);

                // 关联文档列表
                int relCount = resultBean.getListNum("DocumentInfo.DocumentRels.DocumentRel");
                List<XmlNode> relDocList = new ArrayList<>(relCount);
                for (int i = 0; i < relCount; i++) {
                    relDocList.add(resultBean.getBeanByPath("DocumentInfo.DocumentRels.DocumentRel[${i}]").getRootNode());
                }
                modelMap.put("relDocList", relDocList);
            }

            XmlBean resultLogBean = svcResponse.getFirstOpRsp("QUERY_LOGING_INFO_CMPT")
                    .getBeanByPath("Operation.OpResult.Operations");
            if (resultLogBean != null) {
                int count = resultLogBean.getListNum("Operations.Operation");
                for (int i = 0; i < count; i++) {
                    Map rowMap = new HashMap();
                    String fromIp = resultLogBean.getStrValue("Operations.Operation[${i}].fromIp");
                    String opStartTime = resultLogBean.getStrValue("Operations.Operation[${i}].opStartTime");
                    String opStaffCode = resultLogBean.getStrValue("Operations.Operation[${i}].opStaffCode");
                    String opDealType = resultLogBean.getStrValue("Operations.Operation[${i}].opDealType");
                    String opNote = resultLogBean.getStrValue("Operations.Operation[${i}].opNote");
                    if (StringUtil.isEmptyOrNull(opDealType)) {
                        continue;
                    }
                    rowMap.put("fromIp", fromIp);
                    rowMap.put("opStartTime", opStartTime);
                    rowMap.put("opStaffCode", opStaffCode);
                    rowMap.put("opDealType", opDealType);
                    rowMap.put("opNote", opNote);
                    resultList.add(rowMap);
                }
            }
            modelMap.put("resultList", resultList);
        }
        return new ModelAndView("/eland/pj/pj015/pj01502", modelMap);
    }

    /**
     * 手动增加 文档操作日志
     * @param request
     * @param response
     */
    public void docRecord(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //操作文档docId
        String docId = request.getParameter("docId");
        //操作类型
        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String prjCd = request.getParameter("prjCd");
        Map opdealMap = getCfgCollection(request, "OP_DEAL_TYPE", true, NumberUtil.getIntFromObj(prjCd));
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 查询条件
        opData.setStrValue(prePath + "fromIp", svcRequest.getIpAddr());
        opData.setStrValue(prePath + "transactionId", svcRequest.getReqId());
        opData.setStrValue(prePath + "opService", "downloadFile");
        opData.setStrValue(prePath + "opMessage", opdealMap.get(title));
        opData.setStrValue(prePath + "opNote", message);

        opData.setStrValue(prePath + "opDealType", title);
        opData.setStrValue(prePath + "OpEntitys.OpEntity.entityName", "DocumentInfo");
        opData.setStrValue(prePath + "OpEntitys.OpEntity.entityKey", "${docId}");

        svcRequest.addOp("ADD_LOG_WRITER_CMPT", opData);
        // 调用服务查询实体全部属性
        SvcResponse svcResponse = query(svcRequest);
        ResponseUtil.printAjax(response, """{isSuccess:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }"}""");
    }

    /**
     * 目录树数据
     */
    public void treeInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String objValue = request.getParameter("objValue");
        // 是否显示文件夹
        String showFile = request.getParameter("showFile");
        Set<String> checkedOrg = new HashSet<String>();
        if (StringUtil.isNotEmptyOrNull(objValue)) {
            String[] objValues = objValue.split(",");
            for (int i = 0; i < objValues.length; i++) {
                checkedOrg.add(objValues[i]);
            }
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 查询条件
        opData.setStrValue(prePath + "entityName", "DocumentInfo");
        opData.setStrValue(prePath + "needRootFlag", "true");
        if (!"true".equals(showFile)) {
            opData.setStrValue(prePath + "Conditions.Condition.fieldName", "docFlag");
            opData.setStrValue(prePath + "Conditions.Condition.fieldValue", "1");
            opData.setStrValue(prePath + "Conditions.Condition.operation", "=");
        }
        // 排序
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", "docId");
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", "desc");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "upDocId");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "docFlag");
        opData.setStrValue(prePath + "opName", "QUERY_TREE_CMPT");
        // 调用服务增加查询
        svcRequest.addOp("QUERY_DOCUMENT_WITH_RHT", opData);

        // 调用服务增加查询
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder strb = new StringBuilder();
        if (result) {
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_DOCUMENT_WITH_RHT").getBeanByPath("Operation.OpResult");
            if (treeBean != null) {
                int count = treeBean.getListNum("OpResult.Node") - 1;
                if (count != null) {
                    for (int i = 0; i < count; i++) {
                        String id = treeBean.getStrValue("""OpResult.Node[${i}].docId""");
                        String pId = treeBean.getStrValue("""OpResult.Node[${i}].upDocId""");
                        String name = treeBean.getStrValue("""OpResult.Node[${i}].docName""");
                        String docFlag = treeBean.getStrValue("""OpResult.Node[${i}].docFlag""");
                        String iconSkin = "";
                        if ("1".equals(docFlag)) {
                            iconSkin = "folder";
                        }
                        boolean haveChecked = checkedOrg.contains(id);
                        boolean open = false;
                        strb.append("""{ id: "${id}", pId: "${pId}", iconSkin: "${iconSkin}",
                             checked: "${haveChecked}", name: "${name}",open: "${open}"},""");
                    }
                    // 拼接最后一个节点
                    String id = treeBean.getStrValue("""OpResult.Node[${count}].docId""");
                    String pId = treeBean.getStrValue("""OpResult.Node[${count}].upDocId""");
                    String name = treeBean.getStrValue("""OpResult.Node[${count}].docName""");
                    boolean haveChecked = checkedOrg.contains(id);
                    strb.append("""{ id: "${id}", pId: "${pId}", iconSkin: "folder", checked: "${
                        haveChecked
                    }", name: "${
                        name
                    }",open: "true"}""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${strb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 添加文件夹
     * @param request 请求信息
     * @param response 响应信息
     */
    public void addFolder(HttpServletRequest request, HttpServletResponse response) {
        // 获取新增文件夹所属上级id
        String upDocId = request.getParameter("upDocId");
        String prjCd = request.getParameter("prjCd");
        String newName = request.getParameter("newName");
        Map opdealMap = getCfgCollection(request, "OP_DEAL_TYPE", true, NumberUtil.getIntFromObj(prjCd));
        String jsonStr;
        //
        StringBuilder strb = new StringBuilder();
        if (StringUtil.isNotEmptyOrNull(upDocId)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = new XmlBean();
            // 把获取的数据信息赋给新建文件夹
            opData.setStrValue("OpData.entityName", "DocumentInfo");
            opData.setStrValue("OpData.DocumentInfo.docId", "\${docId}");
            opData.setStrValue("OpData.DocumentInfo.upDocId", "${upDocId}");
            opData.setStrValue("OpData.DocumentInfo.docName", newName);
            opData.setStrValue("OpData.DocumentInfo.docFlag", "1");
            opData.setStrValue("OpData.DocumentInfo.prjCd", prjCd);
            svcRequest.addOp("SAVE_DOCUMENT_INFO", opData);
            //调用最新的服务
            SvcResponse svcResponse = transaction(svcRequest);
            boolean result = svcResponse.isSuccess();
            if (result) {
                XmlBean folderBean = svcResponse.getFirstOpRsp("SAVE_DOCUMENT_INFO").getBeanByPath("Operation.OpData");
                String docId = "";
                if (folderBean != null) {
                    docId = folderBean.getStrValue("OpData.DocumentInfo.docId");
                }
                strb.append("""{ id: "${docId}", pId: "${upDocId}", iconSkin: "folder",  name: "${newName}"}""");

                //文档操作记录
                opData = new XmlBean();
                String prePath = "OpData.";
                // 查询条件
                opData.setStrValue(prePath + "fromIp", svcRequest.getIpAddr());
                opData.setStrValue(prePath + "transactionId", svcRequest.getReqId());
                opData.setStrValue(prePath + "opService", "SAVE_DOCUMENT_INFO");
                opData.setStrValue(prePath + "opMessage", opdealMap.get(3));
                opData.setStrValue(prePath + "opDealType", "3");
                opData.setStrValue(prePath + "OpEntitys.OpEntity.entityName", "DocumentInfo");
                opData.setStrValue(prePath + "OpEntitys.OpEntity.entityKey", "${docId}");

                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("ADD_LOG_WRITER_CMPT", opData);
                svcResponse = query(svcRequest);
            }
            String errMsg = svcResponse.getErrMsg();
            jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${strb.toString()}]}}""";
        }
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 双击修改文件名称
     * @param request
     * @param response
     */
    public void changeName(HttpServletRequest request, HttpServletResponse response) {
        // 获取参数
        String docId = request.getParameter("docId");
        String docName = request.getParameter("docName");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "DocumentInfo");
        opData.setStrValue("OpData.DocumentInfo.docId", "${docId}");
        opData.setStrValue("OpData.DocumentInfo.docName", "${docName}");
        svcRequest.addOp("SAVE_TREE_CMPT", opData);
        //调用最新的服务
        SvcResponse svcResponse = transaction(svcRequest);
        //
        StringBuilder strb = new StringBuilder();
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        if (result) {
            XmlBean folderBean = svcResponse.getFirstOpRsp("SAVE_TREE_CMPT").getBeanByPath("Operation.OpData");
            String upDocId = folderBean.getStrValue("OpData.DocumentInfo.upDocId");
            strb.append("""{ id: "${docId}", pId: "${upDocId}", name: "${docName}"}""");
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultName:${strb.toString()}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 上传文件
     */
    public void addFile(HttpServletRequest request, HttpServletResponse response) {
        // 获取新增文件夹所属上级id
        String upDocId = request.getParameter("upDocId");
        String prjCd = request.getParameter("prjCd");
        //文档操作类型
        Map opdealMap = getCfgCollection(request, "OP_DEAL_TYPE", true, NumberUtil.getIntFromObj(prjCd));
        String[] docNameArr = request.getParameterValues("docName");
        String[] svcDocIdArr = request.getParameterValues("svcDocId");
        if (StringUtil.isNotEmptyOrNull(upDocId)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = null;
            SvcResponse svcResponse = new SvcResponse();
            if (docNameArr != null) {
                for (int i = 0; i < docNameArr.length; i++) {
                    opData = new XmlBean();
                    // 把获取的数据信息赋给新建文件
                    opData.setStrValue("OpData.entityName", "DocumentInfo");
                    opData.setStrValue("OpData.DocumentInfo.docId", "\${docId_${i}}")
                    opData.setStrValue("OpData.DocumentInfo.upDocId", "${upDocId}");
                    opData.setStrValue("OpData.DocumentInfo.prjCd", "${prjCd}");
                    opData.setStrValue("OpData.DocumentInfo.docName", "${docNameArr[i]}");
                    opData.setStrValue("OpData.DocumentInfo.svcDocId", "${svcDocIdArr[i]}");
                    opData.setStrValue("OpData.DocumentInfo.docFlag", "0");
                    //需要保存操作类型

                    svcRequest.addOp("SAVE_DOCUMENT_INFO", opData);
                }
                //调用最新的服务
                svcResponse = transaction(svcRequest);
                if (svcResponse.isSuccess()) {
                    XmlBean result = svcResponse.getFirstOpRsp("SAVE_DOCUMENT_INFO").getBeanByPath("Operation.OpData");
                    if (result != null) {
                        int count = result.getListNum("OpData.DocumentInfo");
                        String[] ids = new String[count];
                        for (int i = 0; i < count; i++) {
                            String id = result.getStrValue("OpData.DocumentInfo[${i}].docId");
                            ids[i] = id;
                        }
                        for (int i = 0; i < count; i++) {
                            //文档操作记录
                            svcRequest = RequestUtil.getSvcRequest(request);
                            String docId = request.getParameter("upDocId");
                            svcRequest = RequestUtil.getSvcRequest(request);
                            opData = new XmlBean();
                            String prePath = "OpData.";
                            // 查询条件
                            opData.setStrValue(prePath + "fromIp", svcRequest.getIpAddr());
                            opData.setStrValue(prePath + "transactionId", svcRequest.getReqId());
                            opData.setStrValue(prePath + "opService", "SAVE_DOCUMENT_INFO");
                            opData.setStrValue(prePath + "opMessage", opdealMap.get(3));
                            opData.setStrValue(prePath + "opDealType", "3");
                            opData.setStrValue(prePath + "OpEntitys.OpEntity.entityName", "DocumentInfo");
                            opData.setStrValue(prePath + "OpEntitys.OpEntity.entityKey", ids[i]);
                            svcRequest.addOp("ADD_LOG_WRITER_CMPT", opData);
                            svcResponse = query(svcRequest);
                        }
                    }
                }
            }
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }

    /**
     * 修改文件保存文件信息
     * @param request
     * @param response
     */
    public void saveDocInfo(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String docId = requestUtil.getStrParam("docId");
        String docName = requestUtil.getStrParam("docName");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        if (StringUtil.isNotEmptyOrNull(docId)) {
            opData.setStrValue("OpData.entityName", "DocumentInfo");
            opData.setStrValue("OpData.DocumentInfo.docId", "${docId}");
            opData.setStrValue("OpData.DocumentInfo.upDocId", requestUtil.getStrParam("upDocId"));
            opData.setStrValue("OpData.DocumentInfo.prjCd", requestUtil.getStrParam("prjCd"));
            opData.setStrValue("OpData.DocumentInfo.docName", docName);
            opData.setStrValue("OpData.DocumentInfo.docType", requestUtil.getStrParam("docType"));
            opData.setStrValue("OpData.DocumentInfo.docSrc", requestUtil.getStrParam("docSrc"));
            opData.setStrValue("OpData.DocumentInfo.docCd", requestUtil.getStrParam("docCd"));
            String[] keyWord = request.getParameterValues("keyWord");
            List<String> keyWorkList = new ArrayList<String>();
            if (keyWord != null) {
                for (String temp : keyWord) {
                    if (StringUtil.isNotEmptyOrNull(temp)) {
                        keyWorkList.add(temp);
                    }
                }
            }
            opData.setStrValue("OpData.DocumentInfo.docKey", keyWorkList.join(","));
            if (StringUtil.isNotEmptyOrNull(requestUtil.getStrParam("pubDate"))) {
                opData.setStrValue("OpData.DocumentInfo.pubDate", DateUtil.toDateYmdWthH(requestUtil.getStrParam("pubDate")));
            }
            opData.setStrValue("OpData.DocumentInfo.pubOrg", requestUtil.getStrParam("pubOrg"));
            opData.setStrValue("OpData.DocumentInfo.docStatus", requestUtil.getStrParam("docStatus"));
            opData.setStrValue("OpData.DocumentInfo.docDesc", requestUtil.getStrParam("docDesc"));
//            opData.setStrValue("OpData.DocumentInfo.orgId", requestUtil.getStrParam("orgId"));

            //是否级联授权文件夹，只对文件夹有效
            String cascadeFlag = requestUtil.getStrParam("cascadeFlag");
            if (StringUtil.isEqual("1", cascadeFlag)) {
                opData.setStrValue("OpData.cascadeFlag", "true");
            }

            //增加 角色 授权报文
            String[] roleCds = request.getParameterValues("roleCd");
            int x = 0;
            if (roleCds != null) {
                for (String temp : roleCds) {
                    if (StringUtil.isNotEmptyOrNull(temp)) {
                        opData.setStrValue("OpData.DocumentInfo.DocRhtCtrs.DocRhtCtr[${x}].docRelType", "1");
                        opData.setStrValue("OpData.DocumentInfo.DocRhtCtrs.DocRhtCtr[${x++}].docRelId", temp);
                    }
                }
            }
            //增加 个人用户 授权报文
            String toStaffIdStr = requestUtil.getStrParam("toStaffId");
            if (StringUtil.isNotEmptyOrNull(toStaffIdStr)) {
                String[] toStaffId = toStaffIdStr.split(",");
                if (toStaffId != null) {
                    for (String temp : toStaffId) {
                        if (StringUtil.isNotEmptyOrNull(temp)) {
                            opData.setStrValue("OpData.DocumentInfo.DocRhtCtrs.DocRhtCtr[${x}].docRelType", "2");
                            opData.setStrValue("OpData.DocumentInfo.DocRhtCtrs.DocRhtCtr[${x++}].docRelId", temp);
                        }
                    }
                }
            }

            // 保存关联文档
            String[] relDocTypeArr = request.getParameterValues("relDocType");
            String[] relDocIdArr = request.getParameterValues("relDocId");
            if (relDocIdArr != null && relDocIdArr.length > 1) {
                int count = 0;
                for (int i = 0; i < relDocIdArr.length; i++) {
                    String relDocId = relDocIdArr[i];
                    if (StringUtil.isEmptyOrNull(relDocId)) {
                        continue;
                    } else {
                        opData.setStrValue("OpData.DocumentInfo.DocumentRels.DocumentRel[${count}].relDocId", relDocId);
                        opData.setStrValue("OpData.DocumentInfo.DocumentRels.DocumentRel[${count++}].relName", relDocTypeArr[i]);
                    }
                }
            } else {
                opData.setStrValue("OpData.DocumentInfo.DocumentRels", "");
            }
            svcRequest.addOp("SAVE_DOCUMENT_INFO", opData);
        }
        //调用最新的服务
        SvcResponse svcResponse = transaction(svcRequest);

        boolean result = svcResponse.isSuccess();
        String resultJson = "data: null";
        if (result) {
            XmlBean folderBean = svcResponse.getFirstOpRsp("SAVE_DOCUMENT_INFO").getBeanByPath("Operation.OpData");
            String upDocId = folderBean.getStrValue("OpData.DocumentInfo.upDocId");
            resultJson = """"data":{"id": "${docId}", "pId": "${upDocId}", "name": "${docName}"}""";
        }
        ResponseUtil.printSvcResponse(response, svcResponse, resultJson);
    }

    /**
     * 删除文件及下级文件夹
     */
    public void deleteFile(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数docId
        String docId = request.getParameter("docId");
        if (StringUtil.isNotEmptyOrNull(docId)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = new XmlBean();
            // 把获取的数据信息赋给新建文件夹
            opData.setStrValue("OpData.entityName", "DocumentInfo");
            opData.setStrValue("OpData.docId", "${docId}")
            svcRequest.addOp("DELETE_TREE_CMPT", opData);
            //调用最新的服务
            SvcResponse svcResponse = transaction(svcRequest);
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }

    /**
     * 删除文件及下级文件夹
     */
    public void moveDoc(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数docId
        String docId = request.getParameter("mNodeId");
        String toDocId = request.getParameter("tNodeId");
        String moveType = request.getParameter("moveType");
        String prjCd = request.getParameter("prjCd");
        if ("prev".equals(moveType)) {
            moveType = "2"
        } else if ("next".equals(moveType)) {
            moveType = "3"
        } else {
            moveType = "1"
        }
        if (StringUtil.isNotEmptyOrNull(docId)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = new XmlBean();
            // 把获取的数据信息赋给新建文件夹
            opData.setStrValue("OpData.entityName", "DocumentInfo");
            opData.setStrValue("OpData.moveId", "${docId}");
            opData.setStrValue("OpData.moveType", moveType);
            opData.setStrValue("OpData.moveToId", "${toDocId}");
            svcRequest.addOp("MOVE_TREE_CMPT", opData);
            //调用最新的服务
            SvcResponse svcResponse = transaction(svcRequest);
            if(svcResponse.isSuccess()){
                //文档操作记录
                //文档操作类型
                Map opdealMap = getCfgCollection(request, "OP_DEAL_TYPE", true, NumberUtil.getIntFromObj(prjCd));
                opData = new XmlBean();
                String prePath = "OpData.";
                opData.setStrValue(prePath + "fromIp", svcRequest.getIpAddr());
                opData.setStrValue(prePath + "transactionId", svcRequest.getReqId());
                opData.setStrValue(prePath + "opService", "SAVE_DOCUMENT_INFO");
                opData.setStrValue(prePath + "opMessage", opdealMap.get(5));
                opData.setStrValue(prePath + "opDealType", "5");
                opData.setStrValue(prePath + "OpEntitys.OpEntity.entityName", "DocumentInfo");
                opData.setStrValue(prePath + "OpEntitys.OpEntity.entityKey", docId);
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("ADD_LOG_WRITER_CMPT", opData);
                SvcResponse addLogRsp = query(svcRequest);
            }
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }

    /**
     * 重新上传文件
     * @param request
     * @param response
     */
    public void changeFile(HttpServletRequest request, HttpServletResponse response) {
        // 获取参数
        String docId = request.getParameter("docId");
        String newSvcDocId = request.getParameter("newSvcDocId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "DocumentInfo");
        opData.setStrValue("OpData.DocumentInfo.docId", "${docId}");
        opData.setStrValue("OpData.DocumentInfo.svcDocId", newSvcDocId);
        svcRequest.addOp("SAVE_TREE_CMPT", opData);
        //调用最新的服务
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 右侧面板显示文件夹下级文件及文件夹
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView showFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        // 获取输入参数
        String docId = request.getParameter("docId");
        // 查询条件
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "DocumentInfo");
//        opData.setStrValue(prePath + "docId", "${docId}");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "docId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", docId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 其他查询条件
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

        int addCount = 1;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i]) || StringUtil.isEmptyOrNull(conditionArr[i]) || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            if (StringUtil.isEqual(conditionFieldArr[i], "queryType")) {
                conditionValueArr[i] = "1";
            }
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
            }
        }

        // 排序
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", "docFlag");
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", "desc");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docFlag");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "svcDocId");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "prjCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "upDocId");

//        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_CMPT");
        // 调用服务增加查询
        svcRequest.addOp("QUERY_DOCUMENTINFO_PAGE", opData);

        SvcResponse svcResponse = query(svcRequest);
        List returnList = new ArrayList();
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_DOCUMENTINFO_PAGE").getBeanByPath("Operation.OpResult");
            if (resultBean != null) {
                int rowCount = resultBean.getListNum("OpResult.PageData.Row");
                for (int i = 0; i < rowCount; i++) {
                    Map rowMap = new HashMap();
                    XmlBean tempField01 = resultBean.getBeanByPath("OpResult.PageData.Row[${i}]");
                    returnList.add(tempField01.getRootNode());
                }
                modelMap.put("returnList", returnList);
            }
        }
        modelMap.put("upDocId", docId);
        String toPage = "/eland/pj/pj015/pj01501";
        return new ModelAndView(toPage, modelMap);
    }

}