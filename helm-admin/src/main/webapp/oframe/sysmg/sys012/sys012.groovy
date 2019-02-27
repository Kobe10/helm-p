import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
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

class sys012 extends GroovyController {

    /** 初始化主界面 */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys012/sys012", modelMap);
    }

    /** 实体树 属性结构 数据  */
    public void treeInfo(HttpServletRequest request, HttpServletResponse response) {
        //根据查询结果拼接 树形结构 数据  先定义最根节点
        StringBuilder sb = new StringBuilder();
        sb.append("""{ id: "root", pId: "", iconSkin: "folder", name: "系统业务实体模型", open: "true"},""");
        sb.append("""{ id: "p_0", pId: "root", iconSkin: "folder", name: "基础框架通用实体模型", open: "false"},""");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean prjOpData = new XmlBean();
        prjOpData.setStrValue("OpData.entityName", "CmpPrj");
        prjOpData.setStrValue("OpData.ResultFields.fieldName[0]", "prjCd");
        prjOpData.setStrValue("OpData.ResultFields.fieldName[1]", "prjName");
        svcRequest.addOp("QUERY_ENTITY_CMPT", prjOpData);
        SvcResponse prjRsp = query(svcRequest);
        if (prjRsp.isSuccess()) {
            XmlBean prjResult = prjRsp.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (prjResult != null) {
                int prjNum = prjResult.getListNum("PageData.Row");
                for (int i = 0; i < prjNum; i++) {
                    String prjCd = prjResult.getStrValue("PageData.Row[${i}].prjCd")
                    String prjName = prjResult.getStrValue("PageData.Row[${i}].prjName")
                    sb.append("""{ id: "p_${prjCd}", pId: "root", iconSkin: "folder", name: "${
                        prjName
                    }模型", open: "false"},""");
                }
            }
        }

        // 调用服务获取实体
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.subSvcName", "entity000");
        opData.setStrValue("OpData.PageInfo.pageSize", "10000");
        opData.setStrValue("OpData.PageInfo.currentPage", "1");
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (treeBean != null) {
                int count = treeBean.getListNum("PageData.Row");
                for (int i = 0; i < count; i++) {
                    XmlBean temp = treeBean.getBeanByPath("PageData.Row[${i}]");
                    String prjCd = temp.getStrValue("Row.prj_cd");
                    String name = temp.getStrValue("Row.entity_desc");
                    String enName = temp.getStrValue("Row.entity_name");
                    sb.append("""{ id: "e_${i + 1}",pId: "p_${prjCd}",iconSkin: "folder",nType: "1", name: "${
                        name
                    }", enName: "${enName}",currPrjCd:"${prjCd}",open: "false"},""");
                }
            }
        }
        String jsonStr = """resultMap:{treeJson:[${sb.toString()}]}""";
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initE(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("staffId", svcRequest.getReqStaff());

        String currPrjCd = request.getParameter("currPrjCd");
        String id = request.getParameter("id");
        modelMap.put("id", id);

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", request.getParameter("enName"));
        opData.setStrValue("OpData.prjCd", currPrjCd);
        svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entityInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT")
                    .getBeanByPath("Operation.OpResult.Entity.EntityInfo");
            if (entityInfo != null) {
                modelMap.put("entityInfo", entityInfo.getRootNode());
            }
        }
        return new ModelAndView("/oframe/sysmg/sys012/sys012_entity", modelMap);
    }

    /**
     * 初始化分组
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initG(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("staffId", svcRequest.getReqStaff());

        // 输入参数
        String entityName = request.getParameter("entityName");
        String groupId = request.getParameter("groupId");
        String id = request.getParameter("id");
        String pId = request.getParameter("pId");
        String prjCd = request.getParameter("prjCd");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", entityName);
        opData.setStrValue("OpData.prjCd", prjCd);
        svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT")
                    .getBeanByPath("Operation.OpResult.Entity");
            // 处理所有分组
            int groupCount = entity.getListNum("Entity.Groups.Group");
            for (int i = 0; i < groupCount; i++) {
                XmlBean groupInfo = entity.getBeanByPath("Entity.Groups.Group[${i}].GroupInfo");
                String cGroupId = groupInfo.getStrValue("GroupInfo.entityGroupId");
                if (StringUtil.isEqual(groupId, cGroupId)) {
                    modelMap.put("group", groupInfo.getRootNode());
                    break;
                }
            }
        }
        //
        modelMap.put("id", id);
        modelMap.put("pId", pId);
        modelMap.put("entityName", entityName);
        return new ModelAndView("/oframe/sysmg/sys012/sys012_group", modelMap);
    }

    /**
     * 初始化分组
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initA(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("staffId", svcRequest.getReqStaff());

        // 输入参数
        String entityName = request.getParameter("entityName");
        String groupId = request.getParameter("groupId");
        String attrName = request.getParameter("attrName");
        String id = request.getParameter("id");
        String prjCd = request.getParameter("prjCd");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", entityName);
        opData.setStrValue("OpData.prjCd", prjCd);
        svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT").getBeanByPath("Operation.OpResult.Entity");
            if (entity != null) {
                String usePrjCd = entity.getStrValue("Entity.EntityInfo.prjCd");
                modelMap.put("usePrjCd", usePrjCd);
                // 处理所有分组
                int groupCount = entity.getListNum("Entity.Groups.Group");
                Map<String, String> groupCollection = new LinkedHashMap<String, String>(groupCount);
                XmlBean attrBean = null;
                for (int i = 0; i < groupCount; i++) {
                    XmlBean group = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                    String cGroupId = group.getStrValue("Group.GroupInfo.entityGroupId");
                    String groupName = group.getStrValue("Group.GroupInfo.entityGroupNameCh");
                    groupCollection.put(cGroupId, groupName);
                }
                modelMap.put("groups", groupCollection);
                for (int i = 0; i < groupCount; i++) {
                    XmlBean group = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                    String cGroupId = group.getStrValue("Group.GroupInfo.entityGroupId");
                    String groupName = group.getStrValue("Group.GroupInfo.entityGroupNameCh");
                    if (attrBean == null && StringUtil.isEqual(groupId, cGroupId)) {
                        // 获取属性
                        int attrCount = group.getListNum("Group.Attrs.Attr");
                        for (int k = 0; k < attrCount; k++) {
                            XmlBean tempAttrBean = group.getBeanByPath("Group.Attrs.Attr[${k}]");
                            String attrEnName = tempAttrBean.getStrValue("Attr.entityAttrNameEn");
                            if (StringUtil.isEqual(attrName, attrEnName)) {
                                attrBean = tempAttrBean;
                                String mutiAttrFlag = attrBean.getStrValue("Attr.mutiAttrFlag");
                                modelMap.put("mutiAttrFlag", mutiAttrFlag)
                                modelMap.put("attr", attrBean.getRootNode());
                                break;
                            }
                        }
                    } else if (attrBean != null) {
                        break;
                    }
                }
                modelMap.put("groups", groupCollection);
            }
        }
        Map<String, String> muti = new LinkedHashMap<String, String>();
        muti.put("1", "接受变量");
        muti.put("2", "从上下文中取");
        muti.put("0", "不接受变量");
        modelMap.put("muti", muti);

        Map<String, String> mutiAttrFlagMap = new LinkedHashMap<String, String>();
        mutiAttrFlagMap.put("1", "一对一");
        mutiAttrFlagMap.put("2", "一对多");
        mutiAttrFlagMap.put("", "无");
        modelMap.put("mutiAttrFlagMap", mutiAttrFlagMap);
        // 属性名称
        modelMap.put("entityName", entityName);
        modelMap.put("entityGroupId", groupId);
        modelMap.put("id", id);
        return new ModelAndView("/oframe/sysmg/sys012/sys012_attr", modelMap);
    }

    /**
     * 实体属性目录树
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView entityTree(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String nodeId = request.getParameter("nodeId");
        String entityName = request.getParameter("enName");
        String prjCd = request.getParameter("prjCd");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", entityName);
        opData.setStrValue("OpData.prjCd", prjCd);
        svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        StringBuilder sb = new StringBuilder();

        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT")
                    .getBeanByPath("Operation.OpResult.Entity");
            // 处理所有分组
            int groupCount = entity.getListNum("Entity.Groups.Group");
            for (int i = 0; i < groupCount; i++) {
                XmlBean temp = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                String groupName = temp.getStrValue("Group.GroupInfo.entityGroupNameCh");
                String groupId = temp.getStrValue("Group.GroupInfo.entityGroupId");
                sb.append("""{ id: "g${groupId}", pId: "${nodeId}", nType: "2", groupId: "${groupId}",
                            entityName:"${entityName}",iconSkin: "folder", name: "${groupName}",currPrjCd:"${
                    prjCd
                }",open: "false"},""");
                // 获取属性
                int attrCount = temp.getListNum("Group.Attrs.Attr");
                for (int k = 0; k < attrCount; k++) {
                    XmlBean attrBean = temp.getBeanByPath("Group.Attrs.Attr[${k}]");
                    String attrId = "a${groupId}-${k}";
                    String attrChName = attrBean.getStrValue("Attr.entityAttrNameCh");
                    String attrEnName = attrBean.getStrValue("Attr.entityAttrNameEn");
                    sb.append("""{ id: "${attrId}", pId: "g${groupId}", nType: "3",
                           attrName: "${attrEnName}", entityName: "${entityName}",
                           groupId: "${groupId}",  name: "${attrChName}",currPrjCd:"${prjCd}",open: "false"},""");
                }
            }
        }
        String jsonStr = """resultMap:{treeJson:[${sb.toString()}]}""";
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 导出实体
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void export(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String entityName = request.getParameter("entityName");
        String usePrjCd = request.getParameter("usePrjCd");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", entityName);
        opData.setStrValue("OpData.prjCd", usePrjCd);
        svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT")
                    .getBeanByPath("Operation.OpResult.Entity");
            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(entity.toXML(), "UTF-8");
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, null, "${entityName}.xml", xmlFile, request.getHeader("USER-AGENT"));
        }
    }

    /**
     * 初始化新增
     */
    public ModelAndView initAdd(HttpServletRequest request, HttpServletResponse response) {
        String usePrjCd = request.getParameter("usePrjCd");
        String view = request.getParameter("view");
        if (view == "entity") {
            ModelMap modelMap = new ModelMap();
            modelMap.put("usePrjCd", usePrjCd);
            return new ModelAndView("/oframe/sysmg/sys012/sys01201_entity", modelMap);
        } else if (view == "group") {
            ModelMap modelMap = new ModelMap();
            String entityName = request.getParameter("entityName");
            String pId = request.getParameter("pId");
            modelMap.put("pId", pId);
            modelMap.put("entityName", entityName);
            modelMap.put("usePrjCd", usePrjCd);
            return new ModelAndView("/oframe/sysmg/sys012/sys01201_group", modelMap);
        } else if (view == "attr") {
            // 输入参数
            String entityName = request.getParameter("entityName");
            String pId = request.getParameter("pId");
            // 返回处理结果
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            ModelMap modelMap = new ModelMap();
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", entityName);
            opData.setStrValue("OpData.prjCd", usePrjCd);
            svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT")
                        .getBeanByPath("Operation.OpResult.Entity");
                // 处理所有分组
                int groupCount = entity.getListNum("Entity.Groups.Group");
                Map<String, String> groupCollection = new LinkedHashMap<String, String>(groupCount);
                XmlBean attrBean = null;
                for (int i = 0; i < groupCount; i++) {
                    XmlBean group = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                    String cGroupId = group.getStrValue("Group.GroupInfo.entityGroupId");
                    String groupName = group.getStrValue("Group.GroupInfo.entityGroupNameCh");
                    groupCollection.put(cGroupId, groupName);
                }
                modelMap.put("groups", groupCollection);
            }
            Map<String, String> muti = new LinkedHashMap<String, String>();
            muti.put("1", "接受变量");
            muti.put("2", "从上下文中取");
            muti.put("0", "不接受变量");
            modelMap.put("muti", muti);
            Map<String, String> mutiAttrFlagMap = new LinkedHashMap<String, String>();
            mutiAttrFlagMap.put("1", "一对一");
            mutiAttrFlagMap.put("2", "一对多");
            mutiAttrFlagMap.put("", "无");
            modelMap.put("mutiAttrFlagMap", mutiAttrFlagMap);
            // 属性名称
            modelMap.put("entityName", entityName);
            modelMap.put("pId", pId);
            // 属性ID
            modelMap.put("usePrjCd", usePrjCd);
            return new ModelAndView("/oframe/sysmg/sys012/sys01201_attr", modelMap);
        }

    }

    /**
     * 新增实体
     * @param request
     * @param response
     */
    public void saveEntity(HttpServletRequest request, HttpServletResponse response) {
        int count;
        String id = request.getParameter("id");
        String primarySaveField = request.getParameter("primarySaveField");
        String primarySaveTable = request.getParameter("primarySaveTable");
        String entityName = request.getParameter("entityName");
        String primaryField = request.getParameter("primaryField");
        String entityDesc = request.getParameter("entityDesc");
        String primarySeqName = request.getParameter("primarySeqName");
        String dataViewUrl = request.getParameter("dataViewUrl");
        String oldEntityName = request.getParameter("oldEntityName");
        String usePrjCd = request.getParameter("usePrjCd");
        XmlBean opData = new XmlBean();
        if (!StringUtil.isEmptyOrNull(oldEntityName)) {
            opData.setStrValue("OpData.EntityInfo.oldEntityName", oldEntityName);
        } else {
            opData.setStrValue("OpData.EntityInfo.primarySaveField", primarySaveField);
            opData.setStrValue("OpData.EntityInfo.primarySaveTable", primarySaveTable);
        }
        opData.setStrValue("OpData.EntityInfo.entityName", entityName);
        opData.setStrValue("OpData.EntityInfo.primaryField", primaryField);
        opData.setStrValue("OpData.EntityInfo.entityDesc", entityDesc);
        opData.setStrValue("OpData.EntityInfo.prjCd", usePrjCd);
        opData.setStrValue("OpData.EntityInfo.primarySeqName", primarySeqName);
        opData.setStrValue("OpData.EntityInfo.dataViewUrl", dataViewUrl);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("SAVE_CONFIG_ENTITY_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        StringBuilder sb = new StringBuilder();
        if (svcResponse.isSuccess()) {
            // 调用服务获取实体
            opData = new XmlBean();
            opData.setStrValue("OpData.subSvcName", "entity000");
            opData.setStrValue("OpData.PageInfo.pageSize", "1000");
            opData.setStrValue("OpData.PageInfo.currentPage", "1");
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                if (treeBean != null) {
                    count = treeBean.getListNum("PageData.Row") + 1;
                    String tid = "e" + count;
                    if (StringUtil.isEmptyOrNull(id)) {
                        tid = id;
                    }
                    sb.append("""treeJson:{ id: "${tid}",pId: "e${usePrjCd}",iconSkin: "folder",nType: "1", name: "${
                        entityName
                    }",enName: "${entityDesc}",open: "false"},""");
                }
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, sb.toString());
    }

    /**
     * 新增分组
     * @param request
     * @param response
     */
    public void saveGroup(HttpServletRequest request, HttpServletResponse response) {
        String entityGroupId = request.getParameter("entityGroupId");
        String entityName = request.getParameter("entityName");
        String entityGroupNameEn = request.getParameter("entityGroupNameEn");
        String entityGroupNameCh = request.getParameter("entityGroupNameCh");
        String entityGroupDesc = request.getParameter("entityGroupDesc");
        String jid = request.getParameter("id");
        String pId = request.getParameter("pId");
        String entityGroupStatus = "1";
        String usePrjCd = request.getParameter("usePrjCd");
        XmlBean opData = new XmlBean();

        if (!StringUtil.isEmptyOrNull(entityGroupId)) {
            opData.setStrValue("OpData.Entity.GroupInfo.entityGroupId", entityGroupId);
        }
        opData.setStrValue("OpData.Entity.GroupInfo.prjCd", usePrjCd);
        opData.setStrValue("OpData.Entity.GroupInfo.entityName", entityName);
        opData.setStrValue("OpData.Entity.GroupInfo.entityGroupNameEn", entityGroupNameEn);
        opData.setStrValue("OpData.Entity.GroupInfo.entityGroupNameCh", entityGroupNameCh);
        opData.setStrValue("OpData.Entity.GroupInfo.entityGroupDesc", entityGroupDesc);
        opData.setStrValue("OpData.Entity.GroupInfo.entityGroupStatus", entityGroupStatus);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("SAVE_CONFIG_ENTITY_GROUP_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        StringBuffer sb = new StringBuffer();
        if (svcResponse.isSuccess()) {
            String id;
            XmlBean treeBean = svcResponse.getFirstOpRsp("SAVE_CONFIG_ENTITY_GROUP_CMPT").getBeanByPath("Operation.OpData.Entity.GroupInfo");
            String groupId = treeBean.getStrValue("GroupInfo.entityGroupId");
            id = "g" + groupId;
            if (!StringUtil.isEmptyOrNull(entityGroupId)) {
                id = jid;
            }
            sb.append("""treeJson:{ id: "${id}", pId: "${pId}", nType: "2", groupId: "${groupId}",
            entityName:"${entityName}",iconSkin: "folder", name: "${entityGroupNameCh}",open: "false"}""");
        }

        ResponseUtil.printSvcResponse(response, svcResponse, sb.toString());
    }

    /**
     * 新增属性
     * @param request
     * @param response
     */
    public void saveAttr(HttpServletRequest request, HttpServletResponse response) {
        String entityName = request.getParameter("entityName");
        String id = request.getParameter("id");
        String prjCd = request.getParameter("prjCd");

        String oldEntityAttrNameEn = request.getParameter("oldEntityAttrNameEn");
        String oldEntityGroupId = request.getParameter("oldEntityGroupId");
        String entityGroupId = request.getParameter("entityGroupId");
        String entityAttrNameEn = request.getParameter("entityAttrNameEn");
        String entityAttrNameCh = request.getParameter("entityAttrNameCh");
        String mutiAttrFlag = request.getParameter("mutiAttrFlag");
        String attrCanVariable = request.getParameter("attrCanVariable");
        String attrRelEntity = request.getParameter("attrRelEntity");
        String attrRelEntiyField = request.getParameter("attrRelEntiyField");
        String entitySaveTable = request.getParameter("entitySaveTable");
        String entitySaveField = request.getParameter("entitySaveField");
        String entitySaveType = request.getParameter("entitySaveType");
        String attrDefaultValue = request.getParameter("attrDefaultValue");
        String attrRuleCss = request.getParameter("attrRuleCss");
        String attrFrontCheckRule = request.getParameter("attrFrontCheckRule");
        String attrRuleDesc = request.getParameter("attrRuleDesc");
        String attrValueNameFunc = request.getParameter("attrValueNameFunc");
        String attrBackCheckRule = request.getParameter("attrBackCheckRule");
        String attrValueMonitor = request.getParameter("attrValueMonitor");
        String isSupportFunc = request.getParameter("isSupportFunc");
        String canConditionFlag = request.getParameter("canConditionFlag");
        String canOrderFlag = request.getParameter("canOrderFlag");
        String canResultFlag = request.getParameter("canResultFlag");
        String attrFrontSltRule = request.getParameter("attrFrontSltRule");
        String entityAttrDesc = request.getParameter("entityAttrDesc");
        String attrModifyRule = request.getParameter("attrModifyRule");
        String deleteCaseadeFlag = request.getParameter("deleteCaseadeFlag");
        String usePrjCd = request.getParameter("usePrjCd");
        XmlBean opData = new XmlBean();
        String prePath = "OpData.Entity.attrInfo.";
        if (!StringUtil.isEmptyOrNull(oldEntityAttrNameEn)) {
            opData.setStrValue(prePath + "oldEntityGroupId", oldEntityGroupId);
            opData.setStrValue(prePath + "oldEntityAttrNameEn", oldEntityAttrNameEn);
        }

        opData.setStrValue(prePath + "deleteCaseadeFlag", deleteCaseadeFlag);
        opData.setStrValue(prePath + "attrModifyRule", attrModifyRule);
        opData.setStrValue(prePath + "entityGroupId", entityGroupId);
        opData.setStrValue(prePath + "prjCd", usePrjCd);
        opData.setStrValue(prePath + "entityAttrNameEn", entityAttrNameEn);
        opData.setStrValue(prePath + "entityAttrNameCh", entityAttrNameCh);
        opData.setStrValue(prePath + "mutiAttrFlag", mutiAttrFlag);
        opData.setStrValue(prePath + "attrCanVariable", attrCanVariable);
        opData.setStrValue(prePath + "attrRelEntity", attrRelEntity);
        opData.setStrValue(prePath + "attrRelEntiyField", attrRelEntiyField);
        opData.setStrValue(prePath + "entitySaveTable", entitySaveTable);
        opData.setStrValue(prePath + "entitySaveField", entitySaveField);
        opData.setStrValue(prePath + "entitySaveType", entitySaveType);
        opData.setStrValue(prePath + "attrDefaultValue", attrDefaultValue);
        opData.setStrValue(prePath + "attrRuleCss", attrRuleCss);
        opData.setStrValue(prePath + "attrFrontCheckRule", attrFrontCheckRule);
        opData.setStrValue(prePath + "attrRuleDesc", attrRuleDesc);
        opData.setStrValue(prePath + "attrValueNameFunc", attrValueNameFunc);
        opData.setStrValue(prePath + "attrBackCheckRule", attrBackCheckRule);
        opData.setStrValue(prePath + "attrValueMonitor", attrValueMonitor);
        opData.setStrValue(prePath + "isSupportFunc", isSupportFunc);
        opData.setStrValue(prePath + "canConditionFlag", canConditionFlag);
        opData.setStrValue(prePath + "canOrderFlag", canOrderFlag);
        opData.setStrValue(prePath + "canResultFlag", canResultFlag);
        opData.setStrValue(prePath + "attrFrontSltRule", attrFrontSltRule);
        opData.setStrValue(prePath + "entityAttrDesc", entityAttrDesc);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("SAVE_CONFIG_ENTITY_ATTR_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        //重置实体信息
        SvcRequest svcRequest1 = RequestUtil.getSvcRequest(request);
        XmlBean opDataXml = new XmlBean();
        opDataXml.setStrValue("OpData.prjCd", usePrjCd);
        opDataXml.setStrValue("OpData.entityName", entityName);
        svcRequest1.addOp("RESET_ENTITY_CMPT", opDataXml);
        transaction(svcRequest1);

        StringBuffer sb = new StringBuffer();
        if (svcResponse.isSuccess()) {
            if (!StringUtil.isEmptyOrNull(id)) {
                sb.append("""treeJson:{ id: "${id}", pId: "g${entityGroupId}", nType: "3",
                           attrName: "${entityAttrNameEn}", entityName: "${entityName}",
                           groupId: "${entityGroupId}",  name: "${entityAttrNameCh}",open: "false"},""");
            } else {
                svcRequest = RequestUtil.getSvcRequest(request);
                opData = new XmlBean();
                opData.setStrValue("OpData.entityName", entityName);
                opData.setStrValue("OpData.prjCd", prjCd);
                svcRequest.addOp("QUERY_ENTITY_CONFIG_CMPT", opData);
                svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ENTITY_CONFIG_CMPT")
                            .getBeanByPath("Operation.OpResult.Entity");
                    // 处理所有分组
                    int groupCount = entity.getListNum("Entity.Groups.Group");
                    for (int i = 0; i < groupCount; i++) {
                        XmlBean temp = entity.getBeanByPath("Entity.Groups.Group[${i}]");
                        String groupId = temp.getStrValue("Group.GroupInfo.entityGroupId");
                        if (StringUtil.isEqual(groupId, entityGroupId)) {
                            int attrCount = temp.getListNum("Group.Attrs.Attr");
                            id = "a${groupId}-${attrCount}";
                            break;
                        }
                    }
                }
                sb.append("""treeJson:{ id: "${id}", pId: "g${entityGroupId}", nType: "3",
                           attrName: "${entityAttrNameEn}", entityName: "${entityName}",
                           groupId: "${entityGroupId}",  name: "${entityAttrNameCh}",open: "false"},""");
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, sb.toString());
    }

    /**
     * 快速查询属性,推送页面
     * @param request
     * @param response
     */
    public ModelAndView quickQuery(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys012/sys012_quick_list", modelMap);
    }

    /**
     * 删除实体.分组,属性
     * @param request
     * @param response
     */
    public void deleteInfo(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //接受前端传递参数
        String deleteObj = request.getParameter("deleteObj");
        String usePrjCd = request.getParameter("usePrjCd");
        String entityName = request.getParameter("entityName");
        String entityDefId = request.getParameter("entityDefId");
        String entityGroupId = request.getParameter("entityGroupId");
        String entityAttrNameEn = request.getParameter("entityAttrNameEn");

        XmlBean prjOpData = new XmlBean();
        prjOpData.setStrValue("OpData.Entity.usePrjCd", usePrjCd);
        prjOpData.setStrValue("OpData.Entity.entityName", entityName);
        prjOpData.setStrValue("OpData.Entity.entityDefId", entityDefId);
        prjOpData.setStrValue("OpData.Entity.entityGroupId", entityGroupId);
        prjOpData.setStrValue("OpData.Entity.entityAttrNameEn", entityAttrNameEn);
        //判断删除对象, 决定使用组件
        if (StringUtil.isEqual("entity", deleteObj)) {
            svcRequest.addOp("DELETE_ENTITY_INFO_CMPT", prjOpData);
        } else if (StringUtil.isEqual("group", deleteObj)) {
            svcRequest.addOp("DELETE_ENTITY_GROUP_CMPT", prjOpData);
        } else if (StringUtil.isEqual("attr", deleteObj)) {
            svcRequest.addOp("DELETE_ENTITY_GROUP_ATTR_CMPT", prjOpData);
        }
        SvcResponse svcResponse = transaction(svcRequest);
        //返回输出结果
        ResponseUtil.printAjax(response, """{success:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }"}""");
    }

    /**
     * 打开配置导入
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView openImport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys012/sys012_import", modelMap);
    }

    //导入xml配置参数
    public ModelAndView saveImportData(HttpServletRequest request, HttpServletResponse response) {
        // 请求工具类
        RequestUtil requestUtil = new RequestUtil(request);
        XmlBean resultXmlBean = new XmlBean();
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "sys012ImportXmlFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;
        // 获取导入的项目编号,prjCd
        String toPrjCd = requestUtil.getStrParam("toPrjCd");
        if (StringUtil.isEmptyOrNull(toPrjCd)) {
            toPrjCd = requestUtil.getStrParam("prjCd");
        }
        if (fileType.contains(".xml")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            File xmlFile = ServerFile.createFile(remoteFileName);
            /* file2String */
            StringWriter outputStream = null;
            InputStreamReader inputStream = null;
            XmlBean reqData = null;
            try {
                outputStream = new StringWriter();
                inputStream = new InputStreamReader(localFile.getInputStream(), "UTF-8");
                // 保存待上传文件信息
                char[] b = new char[1024 * 5];
                int len;
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                }
                outputStream.flush();
                // 转换导入的报文
                String xmlString = outputStream.toString();
                xmlString = xmlString.replaceAll("\\\$\\{prjCd\\}", toPrjCd);
                println(xmlString);
                reqData = new XmlBean(xmlString);
            } catch (Exception e) {
                log.error("导入文件处理失败", e);
                ResponseUtil.printAjax(response, """{isSuccess: false, errMsg: "导入文件处理失败" """);
                return;
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
                        log.error("文件关闭失败", e);
                    }
                }
            }
            XmlBean opData = new XmlBean();
            opData.setBeanByPath("OpData", reqData);
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("SAVE_ENTITY_CONFIG_CMPT", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            //重置实体信息
            String entityName = opData.getStrValue("OpData.Entity.EntityInfo.entityName");
            SvcRequest svcRequest1 = RequestUtil.getSvcRequest(request);
            XmlBean opDataXml = new XmlBean();
            opDataXml.setStrValue("OpData.prjCd", toPrjCd);
            opDataXml.setStrValue("OpData.entityName", entityName);
            svcRequest1.addOp("RESET_ENTITY_CMPT", opDataXml);
            transaction(svcRequest1);

            if (svcResponse.isSuccess()) {
                resultXmlBean = svcResponse.getFirstOpRsp("SAVE_ENTITY_CONFIG_CMPT").getBeanByPath("Operation.OpResult");
                if (resultXmlBean != null) {
                    resultXmlBean = resultXmlBean.getBeanByPath("OpResult.UpdateInfos");
                }
                result = true;
            } else {
                errMsg = svcResponse.getErrMsg();
            }
            xmlFile.delete();
        } else {
            errMsg = "文件格式不正确";
        }
        String res = "";
        if (resultXmlBean != null) {
            res = resultXmlBean.toString();
        }
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}",resultXml:"${res}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }


    public ModelAndView initImportResult(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String result = request.getParameter("res");
        if (StringUtil.isNotEmptyOrNull(result)) {
            XmlBean res = new XmlBean(result);
            int updateAttrCount = res.getListNum("UpdateInfos.Info");
            List<XmlNode> returnList = new ArrayList<XmlNode>();
            for (int i = 0; i < updateAttrCount; i++) {
                returnList.add(res.getBeanByPath("UpdateInfos.Info[${i}]").getRootNode());
            }
            modelMap.put("returnList", returnList);
        }
        return new ModelAndView("/oframe/sysmg/sys012/sys012_importResult", modelMap);
    }

}
