import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房  房产信息展示页面
 */
class ph00301 extends GroovyController {

    /**
     * 判断hsCd是否重复
     * @param request
     * @param response
     */
    public void queryHsCd(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String hsCd = requestUtil.getStrParam("hsCd");
        String hsId = requestUtil.getStrParam("hsId");
        String result = false;

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsCd");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsCd);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsId");
        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                if (rowCount > 1) {
                    result = false;
                } else if (rowCount == 1) {
                    String oldHsId = queryResult.getStrValue("OpResult.PageData.Row[0].hsId");
                    if (StringUtil.isEqual(oldHsId, hsId)) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else if (rowCount < 1) {
                    result = true;
                }
            }
        }
        String jsonStr = """{success:${result}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 初始化进入录入页面 */
    public ModelAndView initHs(HttpServletRequest request, HttpServletResponse response) {
        //获取系统通用表单编码----取得 修改评估数据表单 编码
        Map<String, String> map = getCfgCollection(request, "PRJ_FORM", true,
                NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String formCd = map.get("inputFormCd");
        if (StringUtil.isNotEmptyOrNull(formCd)) {
            return new ModelAndView("forward:/eland/ph/ph014/ph014-initWithAdd.gv?privilegeId=ph00301-initHs&formCd=" + formCd, new ModelMap());
        } else {
            // 传统信息存在
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            RequestUtil requestUtil = new RequestUtil(request);
            String prjCd = requestUtil.getStrParam("prjCd");
            String hsId = requestUtil.getStrParam("hsId");
            String buildId = requestUtil.getStrParam("buildId");
            ModelMap modelMap = new ModelMap();
            modelMap.put("hsId", hsId);
            modelMap.put("buildId", buildId);
            modelMap.put("regId", request.getParameter("regId"));
            modelMap.put("prjCd", prjCd);
            //查询房产状态  判断 是否 展示保存按钮： 只有信息录入状态可以保存。
            if (StringUtil.isNotEmptyOrNull(hsId)) {
                XmlBean dopData = new XmlBean();
                dopData.setStrValue("OpData.entityName", "HouseInfo");
                dopData.setStrValue("OpData.entityKey", hsId);
                dopData.setStrValue("OpData.ResultField.fieldName[0]", "hsStatus");
                svcRequest.addOp("QUERY_ENTITY_PROPERTY", dopData);
                SvcResponse svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    XmlBean result = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                    if (result != null) {
                        modelMap.put("hsStatus", result.getStrValue("OpResult.hsStatus"));
                    }
                }
            }
            return new ModelAndView("/eland/ph/ph003/ph00301_hs_ctrl", modelMap);
        }
    }

    /** 查询居民信息 */
    public ModelAndView initS(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        String hsId = requestUtil.getStrParam("hsId");
        String buildId = requestUtil.getStrParam("buildId");
        // 错误消息
        String errMsg = requestUtil.getStrParam("errMsg");
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        modelMap.put("buildId", buildId);
        modelMap.put("regId", request.getParameter("regId"));
        modelMap.put("prjCd", prjCd);
        modelMap.put("errMsg", errMsg);
        modelMap.put("procReadonly", requestUtil.getStrParam("procReadonly"));

        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlBean alterBean = null;
        XmlBean newXmlBean = null;

        /**
         * 征收项目：判断页面是否是 发起修改申请，流程页面进来的，展示修改记录。
         */
        if (procBean != null) {
            request.setAttribute("checkValueChange", true);
            newXmlBean = new XmlBean();
            XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
            XmlNode Variables = (XmlNode) ProcInsInfo.get("Variables");
            /**
             * 获取变化报文 合并 newXmlBean 处理 推送页面
             */
            if (Variables.get("alterRecord") != null) {
                byte[] alterRecord = (byte[]) (((XmlNode) Variables.get("alterRecord")).getNodeValue());
                // 获取byte数组 时转换编码格式。
                String alterStr = new String(alterRecord, "UTF-8");
                alterBean = new XmlBean(alterStr);
                //申明容器存储 数据推送前端页面
                //将原有的 changeXmlBean 重新组装成 newXmlBean

                List chEntityList = new ArrayList();
                int chEntityNum = alterBean.getListNum("Operation.ChangedEntities.ChangedEntity");
                for (int j = 0; j < chEntityNum; j++) {//operation 下有多少 chEntitys
                    XmlBean chEntity = alterBean.getBeanByPath("Operation.ChangedEntities.ChangedEntity[${j}]");
                    //重新拼装 报文对比
                    String entityNameEn = chEntity.getStrValue("ChangedEntity.entityNameEn");
                    String method = chEntity.getStrValue("ChangedEntity.method");
                    String entityId = chEntity.getStrValue("ChangedEntity.entityId");
                    String changeEntityPath = "Operation.${entityNameEn}_${entityId}.";

                    List chAttrList = new ArrayList();
                    int chAttrNum = chEntity.getListNum("ChangedEntity.ChangedAttrs.EntityAttr");
                    for (int k = 0; k < chAttrNum; k++) {       // chEntitys 下有多少 chAttrs
                        XmlBean chAttr = chEntity.getBeanByPath("ChangedEntity.ChangedAttrs.EntityAttr[${k}]");
                        chAttrList.add(chAttr.getRootNode());
                        newXmlBean.setStrValue(changeEntityPath + "method", method);
                        //获取变化的老值
                        String attrValueBef = chAttr.getStrValue("EntityAttr.attrValueBef");
                        newXmlBean.setStrValue(changeEntityPath + "${chAttr.getStrValue("EntityAttr.attrNameEn")}_old", attrValueBef);

                        //获取 变化的 新值
                        String attrValueAft = chAttr.getStrValue("EntityAttr.attrValueAft");
                        newXmlBean.setStrValue(changeEntityPath + "${chAttr.getStrValue("EntityAttr.attrNameEn")}", attrValueAft);
                    }
                    //将 chEntity 里单独属性存至 map，推送
                    Map chEntityMap = new HashMap();
                    chEntityMap.put("chAttrList", chAttrList);
                    chEntityMap.put("entityNameCh", chEntity.getStrValue("ChangedEntity.entityNameCh"));
                    chEntityMap.put("entityNameEn", chEntity.getStrValue("ChangedEntity.entityNameEn"));
                    chEntityMap.put("entityId", chEntity.getStrValue("ChangedEntity.entityId"));
                    chEntityMap.put("method", chEntity.getStrValue("ChangedEntity.method"));
                    chEntityList.add(chEntityMap);
                }
                modelMap.put("chEntityList", chEntityList);
            }
        }

        /**
         * 查询房产下所有录入信息
         */
        SvcResponse svcResponse = null;
        List hsOwnersList = new ArrayList();
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            //查询问题户  状态
            XmlBean dopData = new XmlBean();
            dopData.setStrValue("OpData.entityName", "HouseInfo");
            dopData.setStrValue("OpData.entityKey", hsId);
            //资料状态
            dopData.setStrValue("OpData.ResultField.fieldName[0]", "isVerify");
            dopData.setStrValue("OpData.ResultField.fieldName[1]", "isAttrUp");
            dopData.setStrValue("OpData.ResultField.fieldName[2]", "isComplete");
            dopData.setStrValue("OpData.ResultField.fieldName[3]", "hejuFlag");
            // 房产实体 下 某房产的 问题户详情：是否是，问题等级，详情，处理方案。
            dopData.setStrValue("OpData.ResultField.fieldName[4]", "isPbHs");
            dopData.setStrValue("OpData.ResultField.fieldName[5]", "pbLevel");
            dopData.setStrValue("OpData.ResultField.fieldName[6]", "pbDetail");
            dopData.setStrValue("OpData.ResultField.fieldName[7]", "pbPlan");
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", dopData);

            //单独查询问题分类
            dopData = new XmlBean();
            dopData.setStrValue("OpData.entityName", "HouseInfo");
            dopData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            dopData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            dopData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            dopData.setStrValue("OpData.ResultFields.fieldName[0]", "hsPbType");
            svcRequest.addOp("QUERY_ENTITY_CMPT", dopData);
            // 查询房屋标志
            dopData = new XmlBean();
            dopData.setStrValue("OpData.entityName", "HouseInfo");
            dopData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            dopData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            dopData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            dopData.setStrValue("OpData.ResultFields.fieldName[0]", "hsFlag");
            svcRequest.addOp("QUERY_ENTITY_CMPT", dopData);

            //基本信息  产权信息  房产管理信息
            XmlBean operationData = new XmlBean();
            operationData.setStrValue("OpData.HouseInfo.hsId", hsId);
            svcRequest.addOp("QUERY_HS_BASE_INFO", operationData);
            svcRequest.addOp("QUERY_HS_OWNER_INFO", operationData);
            svcRequest.addOp("QUERY_HS_TT_MNG_INFO", operationData);

            //户籍人员
            XmlBean personOpData = new XmlBean();
            personOpData.setStrValue("OpData.PersonInfo.hsId", hsId);
            svcRequest.addOp("QUERY_HS_PERSON_INFO", personOpData);

            //签约信息
            XmlBean reqCtData = new XmlBean();
            reqCtData.setStrValue("OpData.entityName", "HouseInfo");
            reqCtData.setStrValue("OpData.queryType", "2");
            reqCtData.setStrValue("OpData.Conditions.Condition[0].fieldName", "HouseInfo.hsId");
            reqCtData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            reqCtData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            reqCtData.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo.HsCtInfo.hsCtId");
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", reqCtData);
//            XmlBean contractReqData = new XmlBean();
//            contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
//            svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);

            //查询各分组：人员信息、附属信息、经营信息、保障性住房、信息自动审核
            String[] groupNames = ["personInfo", "affiliatedInfo", "businessInfo", "leasesInfo", "bzxHsInfo", "checkHsInfoComplete", "housePgInfo"];
            String nodePath = "OpData.";
            for (int i = 0; i < groupNames.length; i++) {
                XmlBean hsInfoBean = new XmlBean();
                hsInfoBean.setStrValue(nodePath + "entityName", "HouseInfo");
                hsInfoBean.setStrValue(nodePath + "groupName", groupNames[i]);
                hsInfoBean.setStrValue(nodePath + "entityKey", hsId);
                svcRequest.addOp("QUERY_ENTITY_GROUP", hsInfoBean);
            }

            //整体调用服务查询
            svcResponse = query(svcRequest);
            XmlBean houseInfo = new XmlBean();
            if (svcResponse.isSuccess()) {
                //获取资料状态 、 问题户详情：是否是问题户标志，问题等级，详情，处理方案。
                XmlBean dataStatusBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY");
                if (dataStatusBean != null) {
                    dataStatusBean = dataStatusBean.getBeanByPath("Operation.OpResult");
                    if (newXmlBean != null) {
                        dataStatusBean = margeXmlBean("OpResult", dataStatusBean, "Operation.HouseInfo_${hsId}", newXmlBean);
                    }
                    if (dataStatusBean != null) {
                        modelMap.put("dataStatus", dataStatusBean.getRootNode());
                    }
                }

                List<XmlBean> queryEntityCmptList = svcResponse.getAllOpRsp("QUERY_ENTITY_CMPT");
                XmlBean hsPbTypeBean = queryEntityCmptList.get(0).getBeanByPath("Operation.OpResult.PageData");
                if (hsPbTypeBean != null) {
                    int hsPbNum = hsPbTypeBean.getListNum("PageData.Row");
                    List<String> hsPbType = new ArrayList<String>(hsPbNum);
                    for (int i = 0; i < hsPbNum; i++) {
                        hsPbType.add(hsPbTypeBean.getStrValue("PageData.Row[${i}].hsPbType"));
                    }
                    modelMap.put("hsPbType", hsPbType);
                }
                /* 处理变化内容 */
                if (newXmlBean != null) {
                    List<String> hsPbType = modelMap.get("hsPbType");
                    if (hsPbType == null) {
                        hsPbType = new ArrayList<String>();
                    }
                    //新值
                    String hsPbTypesStr = newXmlBean.getStrValue("Operation.HouseInfo_${hsId}.hsPbTypes");
                    if (StringUtil.isNotEmptyOrNull(hsPbTypesStr)) {
                        XmlBean hsPbTypesBean = new XmlBean(hsPbTypesStr);
                        int newHsPbNum = hsPbTypesBean.getListNum("hsPbTypes.hsPbType");
                        List newHsPb = new ArrayList();
                        for (int i = 0; i < newHsPbNum; i++) {
                            newHsPb.add(hsPbTypesBean.getStrValue("hsPbTypes.hsPbType[${i}].hsPbType"));
                        }
                        modelMap.put("hsPbType_old", hsPbType);
                        modelMap.put("hsPbType", newHsPb);
                    }
                }

                // 获取相连房屋标志
                XmlBean hsFlagBean = queryEntityCmptList.get(1).getBeanByPath("Operation.OpResult.PageData");
                if (hsFlagBean != null) {
                    int hsFlagNum = hsFlagBean.getListNum("PageData.Row");
                    List<String> hsFlag = new ArrayList<String>(hsFlagNum);
                    for (int i = 0; i < hsFlagNum; i++) {
                        hsFlag.add(hsFlagBean.getStrValue("PageData.Row[${i}].hsFlag"));
                    }
                    modelMap.put("hsFlag", hsFlag);
                }
                /* 处理变化内容 */
                if (newXmlBean != null) {
                    List<String> hsFlag = modelMap.get("hsFlag");
                    if (hsFlag == null) {
                        hsFlag = new ArrayList<String>();
                    }
                    //新值
                    String hsFlagStr = newXmlBean.getStrValue("Operation.HouseInfo_${hsId}.hsFlags");
                    if (StringUtil.isNotEmptyOrNull(hsFlagStr)) {
                        XmlBean newHsFlagBean = new XmlBean(hsFlagStr);
                        int newHsFlagNum = newHsFlagBean.getListNum("hsFlags.hsFlag");
                        List newHsFlag = new ArrayList();
                        for (int i = 0; i < newHsFlagNum; i++) {
                            newHsFlag.add(newHsFlagBean.getStrValue("hsFlags.hsFlag[${i}].hsFlag"));
                        }
                        modelMap.put("hsFlag_old", hsFlag);
                        modelMap.put("hsFlag", newHsFlag);
                    }
                }

                // 获取所有除户籍外的人员ID列表
                Set<String> personIds = new HashSet<String>();
                XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_HS_BASE_INFO").getBeanByPath("Operation.HouseInfo");
                XmlBean hsTTBean = svcResponse.getFirstOpRsp("QUERY_HS_TT_MNG_INFO").getBeanByPath("Operation.HouseInfo");
                XmlBean hsOwnerInfoBean = svcResponse.getFirstOpRsp("QUERY_HS_OWNER_INFO").getBeanByPath("Operation.HouseInfo");
                if (rspBean != null) {
                    // 获取基本信息 与 修改记录的合并 信息
                    String hsOwnersStr = "";
                    if (newXmlBean != null) {
                        rspBean = margeXmlBean("HouseInfo", rspBean, "Operation.HouseInfo_${hsId}", newXmlBean);
                        hsTTBean = margeXmlBean("HouseInfo", hsTTBean, "Operation.HouseInfo_${hsId}", newXmlBean);

                        //单独处理 产权人 模块信息
                        hsOwnerInfoBean = margeXmlBean("HouseInfo", hsOwnerInfoBean, "Operation.HouseInfo_${hsId}", newXmlBean);
                        //从改变的 newXmlBean 里 获取 变化的字符串 赋值
                        hsOwnersStr = newXmlBean.getStrValue("Operation.HouseInfo_${hsId}.HsOwners");
                    }
                    //获取产权人 hsOwners的bean， 先从合并的流程取， 取不到就从 旧的hsOwnerInfoBean 里面去取
                    XmlBean hsOwnersBean = null;
                    if (StringUtil.isNotEmptyOrNull(hsOwnersStr)) {
                        hsOwnersBean = new XmlBean(hsOwnersStr);
                    } else {
                        hsOwnersBean = hsOwnerInfoBean.getBeanByPath("HouseInfo.HsOwners");
                    }
                    if (hsOwnersBean != null) {
                        int hsOwnerCount = hsOwnersBean.getListNum("HsOwners.HsOwner");
                        for (int i = 0; i < hsOwnerCount; i++) {
                            XmlBean ownerBean = hsOwnersBean.getBeanByPath("HsOwners.HsOwner[${i}]");
                            hsOwnersList.add(ownerBean.getRootNode());
                            // 增加到待查询人员列表
                            String ownerPsId = ownerBean.getStrValue("HsOwner.ownerPsId");
                            personIds.add(ownerPsId);
                        }
                        modelMap.put("hsOwnersList", hsOwnersList);
                    }
                    //获取房产基本信息，产权信息查询结果
                    modelMap.put("hsTTBean", hsTTBean.getRootNode());
                    modelMap.put("baseBean", rspBean.getRootNode());
                    modelMap.put("ownerInfo", hsOwnerInfoBean.getRootNode());
                }

                /**
                 * 处理变化的报文里的 人员信息
                 */
                Map<String, XmlBean> allChangePsMap = new LinkedHashMap<String, XmlBean>();
                // 户主ID：户主下增加的人员列表
                Map<String, Set> allFamPerIdMap = new HashMap();
                // 新增人员
                Set<String> psInsertSet = new LinkedHashSet<String>();
                // 新增的人员, 用于追加到人员里列表中
                Set<String> psChangeAddSet = new LinkedHashSet<String>();
                // 修改的人员
                Set<String> psUpdtSet = new LinkedHashSet<String>();
                // 删除的人员
                Set<String> psDelSet = new LinkedHashSet<String>();
                if (newXmlBean != null) {
                    //将newXmlBean 转换成 personMap 处理。
                    allChangePsMap = xmlBeanToPersonMap(newXmlBean);
                    for (String personId : allChangePsMap.keySet()) {
                        XmlBean psBean = allChangePsMap.get(personId);
                        if (psBean != null) {
                            String method = psBean.getStrValue("Person.method");
                            String oldHsId = psBean.getStrValue("Person.oldHsId");
                            String oldHsIdOld = psBean.getStrValue("Person.oldHsId_old");
                            // 所有新增人员都记录到变量中用于追加到页面输出的Map中
                            if (StringUtil.isEqual("1", method)) {
                                psChangeAddSet.add(personId);
                            }
                            if (StringUtil.isEqual("1", method)) {
                                if (!StringUtil.isEqual(oldHsId, hsId)) {
                                    continue;
                                }
                                // 处理新增的人员，新增的判断有无户主
                                psInsertSet.add(personId);
                                String familyPersonId = psBean.getStrValue("Person.familyPersonId");
                                if (StringUtil.isNotEmptyOrNull(familyPersonId)) {
                                    Set<String> addToFamIdSet = allFamPerIdMap.get(familyPersonId);
                                    if (addToFamIdSet == null) {
                                        addToFamIdSet = new LinkedHashSet<String>();
                                        allFamPerIdMap.put(familyPersonId, addToFamIdSet);
                                    }
                                    addToFamIdSet.add(personId);
                                }
                            } else if (StringUtil.isEqual("2", method)) {
                                // 修改的人员信息
                                if (StringUtil.isEqual(oldHsId, oldHsIdOld)) {
                                    psUpdtSet.add(personId);
                                } else {
                                    psDelSet.add(personId);
                                }
                            }
                        }
                    }
                }

                //获取户籍人员信息结果
                XmlBean personBean = svcResponse.getFirstOpRsp("QUERY_HS_PERSON_INFO");
                if (personBean != null) {
                    List hsPersList = new ArrayList();
                    int perCount = personBean.getListNum("Operation.OpResult.Persons.Person");
                    for (int i = 0; i < perCount; i++) {
                        XmlBean hsPsBean = personBean.getBeanByPath("Operation.OpResult.Persons.Person[${i}]");
                        String personId = hsPsBean.getStrValue("Person.personId");
                        hsPersList.add(hsPsBean.getRootNode());
                        // 处理信息暂存修改内容
                        if (newXmlBean != null) {
                            if (psUpdtSet.contains(personId)) {
                                hsPsBean = margeXmlBean("Person", hsPsBean, "Person", allChangePsMap.get(personId));
                            } else if (psDelSet.contains(personId)) {
                                hsPsBean.setStrValue("Person.method", "3");
                            } else {
                                String familyPersonId = hsPsBean.getStrValue("Person.familyPersonId");
                                //
                                if (StringUtil.isNotEmptyOrNull(familyPersonId) && StringUtil.isEqual(familyPersonId, personId)) {
                                    Set<String> addPs = allFamPerIdMap.get(familyPersonId);
                                    if (addPs != null) {
                                        for (String temp : addPs) {
                                            hsPersList.add(allChangePsMap.get(temp).getRootNode());
                                            psInsertSet.remove(temp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 新增的非户籍人员（如现场居住人等信息处理)
                    if (newXmlBean != null) {
                        for (String temp : psInsertSet) {
                            hsPersList.add(allChangePsMap.get(temp).getRootNode());
                        }
                    }
                    modelMap.put("hsPersonBean", hsPersList);
                }
                //获取签约信息查询结果
                XmlBean conRspData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA");
                if (conRspData != null) {
                    XmlBean ctInfo = conRspData.getBeanByPath("Operation.OpResult.PageData.Row[0].HouseInfo.HsCtInfo");
                    if (ctInfo != null) {
                        if (newXmlBean != null) {
                            ctInfo = margeXmlBean("HsCtInfo", ctInfo, "Operation.HsCtInfo_${hsId}", newXmlBean);
                        }
                        modelMap.put("ctInfo", ctInfo.getRootNode());
                    }
                }

                //通用分组 查询 人员信息 附属信息  经营信息 资料审核结果
                List rspBeanList = svcResponse.getAllOpRsp("QUERY_ENTITY_GROUP");
                for (int i = 0; i < rspBeanList.size(); i++) {
                    XmlBean allFind = new XmlBean();
                    allFind = rspBeanList.get(i).getBeanByPath("Operation.OpResult.HouseInfo");
                    if (allFind != null) {
                        houseInfo.merge(allFind);
                    }
                }
                if (houseInfo != null && newXmlBean != null) {
                    houseInfo = margeXmlBean("HouseInfo", houseInfo, "Operation.HouseInfo_${hsId}", newXmlBean);
                }
                modelMap.put("houseInfo", houseInfo.getRootNode());
                String ttRegId = houseInfo.getStrValue("HouseInfo.ttRegId");

                //查询区域 path
                svcRequest = RequestUtil.getSvcRequest(request);
                String prePath = "OpData.PrjRegInfo.";
                XmlBean opRegData = new XmlBean();
                opRegData.setStrValue(prePath + "regId", ttRegId);
                opRegData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));
                svcRequest.addOp("CONVERT_PATH_TO_ADDR", opRegData);
                SvcResponse regPathRsp = query(svcRequest);
                if (regPathRsp.isSuccess()) {
                    XmlBean regRsp = regPathRsp.getFirstOpRsp("CONVERT_PATH_TO_ADDR").getBeanByPath("Operation.OpResult");
                    String addrPathStr = "";
                    if (regRsp != null) {
                        int addrNum = regRsp.getListNum("OpResult.PrjReg");
                        for (int i = 0; i < addrNum; i++) {
                            String tempPath = regRsp.getStrValue("OpResult.PrjReg[${i}].regName");
                            if (StringUtil.isEmptyOrNull(tempPath)) {
                                continue;
                            }
                            addrPathStr = addrPathStr + "/" + tempPath;
                        }
                    }
                    modelMap.put("addrPathStr", addrPathStr);
                }

                // 调用服务获取所有的其他人员信息
                personIds.remove(null);
                personIds.remove("");
                if (personIds.size() > 0) {
                    String personIdsStr = personIds.join(",");
                    // 调用服务获取人员信息
                    svcRequest = RequestUtil.getSvcRequest(request);
                    /* 获取可用房源区域列表 */
                    // 增加等级
                    XmlBean opData = new XmlBean();
                    prePath = "OpData.";
                    opData.setStrValue(prePath + "entityName", "PersonInfo");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "personId");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "(${personIdsStr})");
                    opData.setStrValue(prePath + "Conditions.Condition[0].operation", "in");
                    opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "prjCd");
                    opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "${prjCd}");
                    opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");

                    opData.setStrValue(prePath + "PageInfo.pageSize", "1000");
                    opData.setStrValue(prePath + "PageInfo.currentPage", "1");

                    // 需要查询的字段
                    opData.setStrValue(prePath + "ResultFields.fieldName[0]", "personId");
                    opData.setStrValue(prePath + "ResultFields.fieldName[1]", "personName");
                    opData.setStrValue(prePath + "ResultFields.fieldName[2]", "personCertyNum");
                    opData.setStrValue(prePath + "ResultFields.fieldName[3]", "personCertyDocIds");
                    opData.setStrValue(prePath + "ResultFields.fieldName[4]", "personTelphone");
                    opData.setStrValue(prePath + "ResultFields.fieldName[5]", "familyAddr");
                    opData.setStrValue(prePath + "ResultFields.fieldName[6]", "personJobAddr");
                    opData.setStrValue(prePath + "ResultFields.fieldName[7]", "personLiveAddr");
                    // 增加查询
                    svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                    SvcResponse personQueryResp = query(svcRequest);
                    if (personQueryResp.isSuccess()) {
                        XmlBean queryResult = personQueryResp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                        Map<String, XmlNode> personsData = new HashMap<String, XmlNode>();
                        // 处理查询结果，返回json格式数据
                        if (queryResult != null) {
                            int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                            for (int i = 0; i < rowCount; i++) {
                                XmlBean row = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                                String personId = row.getStrValue("Row.personId");
                                if (newXmlBean != null) {
                                    XmlBean newPerson = allChangePsMap.get(personId);
                                    if (newPerson != null) {
                                        row = margeXmlBean("Row", row, "Person", newPerson);
                                    }
                                }
                                personsData.put(personId, row.getRootNode());
                            }

                            // 处理增加的人员信息
                            if (newXmlBean != null) {
                                for (String temp : psChangeAddSet) {
                                    XmlBean tempAddP = changBeanRootNote(allChangePsMap.get(temp), "Person", "Row")
                                    personsData.put(temp, tempAddP.getRootNode());
                                }
                            }
                            modelMap.put("personsData", personsData);
                        }
                    }
                }
            }
        }

        // 获取中介公司
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.CmpExt.prjCd", prjCd);
        svcRequest.setReqData(reqData);
        svcResponse = SvcUtil.callSvc("cmpExtService", "queryExtCmpsByPrjCd", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean cmpRspData = svcResponse.getRspData();
            //返回查询结果
            int cmpNum = cmpRspData.getListNum("SvcCont.ExtCmps.extCmp");
            Map cmpMap = new HashMap();
            for (int i = 0; i < cmpNum; i++) {
                cmpMap.put(cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpId"), cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpName"));
            }
            modelMap.put("cmpMap", cmpMap);
        }

        // 临时增加获取项目类型
        svcRequest = RequestUtil.getSvcRequest(request);
        reqData = new XmlBean();
        reqData.setStrValue("OpData.entityName", "CmpPrj");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "prjCd");
        reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", prjCd);
        reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        reqData.setStrValue("OpData.ResultFields.fieldName[0]", "prjType");
        svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            String prjType = rspData.getStrValue("PageData.Row[0].prjType");
            modelMap.put("prjType", prjType);
        }
        // 新建信息录入时候至少保留一个产权人信息
        if (hsOwnersList.size() == 0) {
            XmlBean initData = new XmlBean();
            initData.setStrValue("SvcCont.Operation[0].HouseInfo.HsOwners.HsOwner[0]", "");
            hsOwnersList.add(initData.getRootNode());
        }
        modelMap.put("hsOwnersList", hsOwnersList);
        return new ModelAndView("/eland/ph/ph003/ph00301_input", modelMap);
    }

    /** 居民信息修改记录 */
    public ModelAndView editRecords(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        String hsId = requestUtil.getStrParam("hsId");

        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.entityKey", hsId);
        String bigYM = request.getParameter("bigYM");
        if (!StringUtil.isPositiveNum(bigYM)) {
            bigYM = "2";
        }
        opData.setStrValue("OpData.bigYM", bigYM);
        svcRequest.addOp("QUERY_LOGING_INFO_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean operationBean = svcResponse.getFirstOpRsp("QUERY_LOGING_INFO_CMPT");
            //申明容器存储 数据推送前端页面
            List operationList = new ArrayList();
            int operationNum = operationBean.getListNum("Operation.OpResult.Operations.Operation");
            for (int i = 0; i < operationNum; i++) { // 整个出参有多少 operation
                XmlBean operation = operationBean.getBeanByPath("Operation.OpResult.Operations.Operation[${i}]");

                int chEntityNum = operation.getListNum("Operation.ChangedEntities.ChangedEntity");
                List chEntityList = new ArrayList();
                for (int j = 0; j < chEntityNum; j++) {//operation 下有多少 chEntitys
                    XmlBean chEntity = operation.getBeanByPath("Operation.ChangedEntities.ChangedEntity[${j}]");

                    int chAttrNum = chEntity.getListNum("ChangedEntity.ChangedAttrs.EntityAttr");
                    List chAttrList = new ArrayList();
                    for (int k = 0; k < chAttrNum; k++) {// chEntitys 下有多少 chAttrs
                        XmlBean chAttr = chEntity.getBeanByPath("ChangedEntity.ChangedAttrs.EntityAttr[${k}]");

                        //将最里层的 changeAttr 存入list推送页面
                        chAttrList.add(chAttr.getRootNode());
                    }
                    //将 chEntity 里单独属性存至 map，推送
                    Map chEntityMap = new HashMap();
                    chEntityMap.put("chAttrList", chAttrList);
                    chEntityMap.put("entityNameCh", chEntity.getStrValue("ChangedEntity.entityNameCh"));
                    chEntityMap.put("entityNameEn", chEntity.getStrValue("ChangedEntity.entityNameEn"));
                    chEntityMap.put("entityId", chEntity.getStrValue("ChangedEntity.entityId"));
                    chEntityMap.put("method", chEntity.getStrValue("ChangedEntity.method"));
                    chEntityList.add(chEntityMap);
                }
                Map operationMap = new HashMap();
                operationMap.put("opStaffId", operation.getStrValue("Operation.opStaffId"));
                operationMap.put("opEndTime", operation.getStrValue("Operation.opEndTime"));
                operationMap.put("chEntityList", chEntityList);
                operationList.add(operationMap);
            }
            modelMap.put("operationList", operationList);
        }
        modelMap.put("bigYM", bigYM);
        return new ModelAndView("/eland/ph/ph003/ph003_changeRecords", modelMap);
    }

    /**
     *  查询选房数
     * */
    public ModelAndView initNewHsArea(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        // 获取区域编号
        String regId = requestUtil.getStrParam("regId");
        String prjCd = requestUtil.getStrParam("prjCd");
        String regUseType = "3";
        //
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //查询该区域应安置系数，应安置面积，计算可安置面积
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", regId);
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "prjCd", prjCd);
        // 区域类型
        reqData.setStrValue(rootNodePath + "regUseType", regUseType);
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
        // 获取输出结果
        if (svcResponse.isSuccess()) {
            XmlBean prjRegNode = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
            modelMap.put("regInfo", prjRegNode.getRootNode());
        }

        Map<String, String> hsTps = getCfgCollection(request, "HS_ROOM_TYPE", true);
        modelMap.put("hsTps", hsTps);
        modelMap.put("prjCd", prjCd);
        modelMap.put("regId", regId);
        // 返回暂时界面
        return new ModelAndView("/eland/ph/ph003/ph00301_03", modelMap);
    }

    /** 查询区域信息 */
    public void queryReg(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String prePath = "OpData.";
        // 处理上级区域
        String upRegName = request.getParameter("upRegName");
        String upRegId = requestUtil.getStrParam("upRegId");
        if (StringUtil.isEmptyOrNull(upRegId) && StringUtil.isNotEmptyOrNull(upRegName)) {
            XmlBean tempBean = new XmlBean();
            tempBean.setStrValue(prePath + "entityName", "RegInfo");
            tempBean.setStrValue(prePath + "Conditions.Condition[0].fieldName", "regName");
            tempBean.setStrValue(prePath + "Conditions.Condition[0].fieldValue", upRegName);
            tempBean.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
            tempBean.setStrValue(prePath + "Conditions.Condition[1].fieldName", "regUseType");
            tempBean.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
            tempBean.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
            tempBean.setStrValue(prePath + "ResultFields.fieldName[0]", "regId");
            // 调用服务
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_CMPT", tempBean);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    Set<String> regIds = new LinkedHashSet<String>();
                    for (int i = 0; i < rowCount; i++) {
                        String regId = queryResult.getStrValue("OpResult.PageData.Row[${i}].regId");
                        regIds.add(regId);
                    }
                    upRegId = regIds.join(",");
                }
            }
        }
        // 处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "RegInfo");

        // 查询条件
        String[] fieldsNames = ["regName", "prjCd"];
        String[] operations = ["like", "="];
        int conditionCount = -1;
        for (int i = 0; i < fieldsNames.size(); i++) {
            // 腾退区域
            String fieldValue = requestUtil.getStrParam(fieldsNames[i]);
            if ("like".equals(operations[i])) {
                fieldValue = "%${fieldValue}%";
            }
            if (StringUtil.isNotEmptyOrNull(fieldValue)) {
                opData.setStrValue(prePath + "Conditions.Condition[${++conditionCount}].fieldName", fieldsNames[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].fieldValue", fieldValue);
                opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].operation", operations[i]);
            }
        }
        // 增加等级
        String regLevel = requestUtil.getStrParam("regLevel");
        if (StringUtil.isEmptyOrNull(regLevel)) {
            regLevel = "2";
        }
        opData.setStrValue(prePath + "Conditions.Condition[${++conditionCount}].fieldName", "regLevel");
        opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].fieldValue", regLevel);
        opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].operation", "=");

        // 上级区域限制
        if (StringUtil.isNotEmptyOrNull(upRegId)) {
            opData.setStrValue(prePath + "Conditions.Condition[${++conditionCount}].fieldName", "upRegId");
            opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].fieldValue", "(" + upRegId + ")");
            opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].operation", "in");
        }

        // 增加区域类型
        opData.setStrValue(prePath + "Conditions.Condition[${++conditionCount}].fieldName", "regUseType");
        opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].operation", "=");

        /*排序条件*/
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "upRegId");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        opData.setStrValue(prePath + "SortFields.SortField[1].fieldName", "showOrder");
        opData.setStrValue(prePath + "SortFields.SortField[1].sortOrder", "asc");

        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "regId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "regName");

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].regId");
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].regName");
                    Map<String, String> temp = ["regId": tempField01, "regName": tempField02];
                    result.add(temp);
                }
                ResponseUtil.print(response, JSONArray.fromObject(result).toString());
            } else {
                // 没有上述任意查询条件，执行查询
                ResponseUtil.printAjax(response, "{}")
                return;
            }
        }
    }

    /**
     * 根据地址查询院落门牌号列表，返回界面自助查询
     * @param request 请求信息
     * @param response 返回Json格式数据
     */
    public void queryBuild(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 处理结果
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "BuildInfo");
        // 地址和区域编号都为空，不返回
        if (StringUtil.isEmptyOrNull(request.getParameter("ttRegId")) &&
                StringUtil.isEmptyOrNull(request.getParameter("buildAddr"))) {
            // 没有上述任意查询条件，执行查询
            ResponseUtil.printAjax(response, "{}")
            return;
        }
        // 查询条件
        String[] fieldsNames = ["ttRegId", "buildAddr", "buildNo"];
        String[] operations = ["=", "like", "="];
        int conditionCount = -1;
        for (int i = 0; i < fieldsNames.size(); i++) {
            // 腾退区域
            String fieldValue = requestUtil.getStrParam(fieldsNames[i]);
            if ("like".equals(operations[i])) {
                fieldValue = "%${fieldValue}%";
            }
            if (StringUtil.isNotEmptyOrNull(fieldValue)) {
                opData.setStrValue(prePath + "Conditions.Condition[${++conditionCount}].fieldName", fieldsNames[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].fieldValue", fieldValue);
                opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].operation", operations[i]);
            }
        }
        /*排序条件*/
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "buildFullAddr");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "buildId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "ttRegId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "buildNo");
        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempBuildId = queryResult.getStrValue("OpResult.PageData.Row[${i}].buildId");
                    String tempBuildNo = queryResult.getStrValue("OpResult.PageData.Row[${i}].buildNo");
                    String tempRegId = queryResult.getStrValue("OpResult.PageData.Row[${i}].ttRegId");
                    Map<String, String> temp = ["buildId": tempBuildId, "buildNo": tempBuildNo, "ttRegId": tempRegId];
                    result.add(temp);
                }
                ResponseUtil.print(response, JSONArray.fromObject(result).toString());
            } else {
                // 没有上述任意查询条件，执行查询
                ResponseUtil.printAjax(response, "{}")
                return;
            }
        }
    }

    /** 查询所有房产全地址 */
    public void queryAllHsAddr(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();

        String newHsAddr = requestUtil.getStrParam("newHsAddr");
        String prjCd = requestUtil.getStrParam("prjCd");
        // 处理结果
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        // 查询条件
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsFullAddr");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "%${newHsAddr}%");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "like");

        /*排序条件*/
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "hsFullAddr");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 设置分页
        opData.setStrValue(prePath + "PageInfo.pageSize", "10");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsFullAddr");

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsId");
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsFullAddr");
                    Map<String, String> temp = ["hsId": tempField01, "hsFullAddr": tempField02];
                    result.add(temp);
                }
                ResponseUtil.print(response, JSONArray.fromObject(result).toString());
            } else {
                // 没有上述任意查询条件，执行查询
                ResponseUtil.printAjax(response, "{}")
                return;
            }
        }
    }

    /*---------根据院落编号检索院落信息-------------------------------------*/
    /**
     * 根据地址查询院落门牌号列表，返回界面自助查询
     * @param request 请求信息
     * @param response 返回Json格式数据
     */
    public ModelAndView initBuild(HttpServletRequest request, HttpServletResponse response) {
        // 文件输出
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("procReadonly", request.getParameter("procReadonly"));
        RequestUtil requestUtil = new RequestUtil(request);
        String buildId = requestUtil.getStrParam("buildId");
        // 使用通用组件获取院落的属性
        if (StringUtil.isNotEmptyOrNull(buildId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            // 查询需要的字段
            int i = 0;
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "buildType");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "buildLandSize");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "buildNote");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "buildHsNum");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "buildPosition");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "buildPositionX");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "buildPositionY");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "ttCompanyId");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "ttMainTalk");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "ttSecTalk");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "ttOrgId");
            opData.setStrValue("OpData.ResultField.fieldName[${i++}]", "ttRegId");
            // 调用服务查询数据
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_PROPERTY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean resultData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PROPERTY").getBeanByPath("Operation.OpResult");
                modelMap.put("buildInfo", resultData.getRootNode());
            }
        }
        // 获取中介公司信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取中介公司
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.CmpExt.prjCd", prjCd);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("cmpExtService", "queryExtCmpsByPrjCd", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean cmpRspData = svcResponse.getRspData();
            //返回查询结果
            int cmpNum = cmpRspData.getListNum("SvcCont.ExtCmps.extCmp");
            Map cmpMap = new HashMap();
            for (int i = 0; i < cmpNum; i++) {
                cmpMap.put(cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpId"), cmpRspData.getStrValue("SvcCont.ExtCmps.extCmp[${i}].extCmpName"));
            }
            modelMap.put("cmpMap", cmpMap);
        }
        return new ModelAndView("/eland/ph/ph003/ph00301_01", modelMap);
    }

    /**
     * 根据地址查询院落门牌号列表，返回界面自助查询
     * @param request 请求信息
     * @param response 返回Json格式数据
     */
    public ModelAndView initdHsList(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", requestUtil.getStrParam("buildId"));
        // 处理结果
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        String hsFullAddr = requestUtil.getStrParam("hsFullAddr");
        hsFullAddr = "%${hsFullAddr}%";
//        String buildAddr = requestUtil.getStrParam("buildAddr");
//        String buldUnitNum = requestUtil.getStrParam("buldUnitNum");
//        String hsAddrTail = requestUtil.getStrParam("hsAddrTail");

        //buildAddr 更新之后使用 斜线 区分开 ，截取 查询
//        String[] addrs = [];
//        if (StringUtil.isNotEmptyOrNull(buildAddr)) {
//            addrs = buildAddr.split("/");
//        }
//        if (StringUtil.isEmptyOrNull(buldUnitNum)) {
//            buldUnitNum = "";
//        }
//        String hsFullAddr = "";
//        if (addrs.length > 0) {
//            hsFullAddr = "%${addrs[addrs.length - 1]}%${buldUnitNum}%${hsAddrTail}%";
//            if (addrs.length > 1) {
//                hsFullAddr = "%${addrs[addrs.length - 2]}%${addrs[addrs.length - 1]}%${buldUnitNum}%${hsAddrTail}%";
//            }
//        }

        // 查询条件
        String[] fieldsNames = ["buildId"];
        String[] operations = ["="];
        int conditionCount = -1;
        for (int i = 0; i < fieldsNames.size(); i++) {
            // 腾退区域
            String fieldValue = requestUtil.getStrParam(fieldsNames[i]);
            if (StringUtil.isNotEmptyOrNull(fieldValue)) {
                opData.setStrValue(prePath + "Conditions.Condition[${++conditionCount}].fieldName", fieldsNames[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].fieldValue", fieldValue);
                opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].operation", operations[i]);
            }
        }
        opData.setStrValue(prePath + "Conditions.Condition[${++conditionCount}].fieldName", "hsFullAddr");
        opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].fieldValue", hsFullAddr);
        opData.setStrValue(prePath + "Conditions.Condition[${conditionCount}].operation", "like");
        /*排序条件*/
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "hsFullAddr");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 设置分页
        opData.setStrValue(prePath + "PageInfo.pageSize", "15");

        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "buildId");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "ttRegId");

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("FILTER_QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("FILTER_QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsId");
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsFullAddr");
                    String tempField03 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsOwnerPersons");
                    String tempField04 = queryResult.getStrValue("OpResult.PageData.Row[${i}].buildId");
                    String tempField05 = queryResult.getStrValue("OpResult.PageData.Row[${i}].ttRegId");
                    Map<String, String> temp = ["hsId": tempField01, "hsFullAddr": tempField02, "hsOwnerPersons": tempField03, "buildId": tempField04, "ttRegId": tempField05];
                    result.add(temp);
                }
                modelMap.put("result", result);
            }
        }
        return new ModelAndView("/eland/ph/ph003/ph00301_02", modelMap);
    }

    /**
     * 根据地址查询院落门牌号列表，返回界面自助查询
     * @param request 请求信息
     * @param response 返回Json格式数据
     */
    public void queryHsRule(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        // 请求参数
        String prePath = "OpData.";
        // 房产信息
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        svcRequest.addOp("QUERY_ENTITY_ATTR_RULES", opData);
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "BuildInfo");
        svcRequest.addOp("QUERY_ENTITY_ATTR_RULES", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean result = new XmlBean();
            List<XmlBean> queryResult = svcResponse.getAllOpRsp("QUERY_ENTITY_ATTR_RULES");
            for (XmlBean temp : queryResult) {
                result.merge(temp.getBeanByPath("Operation.OpResult"));
            }
            String jsonString = result.toJson();
            ResponseUtil.printSvcResponse(response, svcResponse, """ "data": ${jsonString}""");
        }
    }

    /** 保存信息录入 */
    public ModelAndView saveInputInfo(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        //新增 修改判断标志
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String buildType = request.getParameter("buildType");
        String hsType = request.getParameter("hsType");
        boolean addFlag = false;
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = "\${hsId}";
            addFlag = true;
        }

        //是否入库标志，
        boolean toDb = false;  //入库
        boolean toContext = false;
        if (StringUtil.isEqual("false", request.getParameter("whetherToDb"))) {
            //不入库
            toDb = true;
            toContext = false;
            svcRequest.setValue("Request.TcpCont.logWrite", "1");
        }

        String buildId = "\${build}";

        //一个地址要截取出来，建两个区域——>  reg  和  build
        String buildAddr = request.getParameter("buildAddr");       //地址
//        String buildNo = request.getParameter("buildNo");           //牌号
        String buldUnitNum = request.getParameter("buldUnitNum");   //单元
        String hsAddrTail = request.getParameter("hsAddrTail");       //尾巴--地址最后几位 楼房房间号  平房房号

        if (StringUtil.isEmptyOrNull(buldUnitNum) || StringUtil.isEqual(hsType, "3")) {
            buldUnitNum = "";
        }
        //新建区域管理
        String nodePath = "OpData.";
        // 院落基本信息管理
        // 院落管理信息
        String ttRegId = "\${ttRegId}";
        XmlBean yardMngOpData = new XmlBean();
        //原来保存建筑基本信息  参数
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildId", "\${build}");
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildType", buildType);
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildLandSize", request.getParameter("buildLandSize"));
        //地址 从前端获取并拼接。处理地址
        String buildFullAddr = "";
        String bldAddr = request.getParameter("buildAddr");
        String[] bldAddrs = [];
        if (StringUtil.isNotEmptyOrNull(bldAddr)) {
            bldAddrs = bldAddr.split("/");
        }
        for (int i = 0; i < bldAddrs.length; i++) {
            buildFullAddr += bldAddrs[i];
        }
        buildFullAddr = buildFullAddr + buldUnitNum;

        if (bldAddrs.length > 0 && bldAddrs.length < 1) {
            yardMngOpData.setStrValue(nodePath + "BuildInfo.buildAddr", "");
            yardMngOpData.setStrValue(nodePath + "BuildInfo.buildNo", bldAddrs[bldAddrs.length - 1]);
        } else if (bldAddrs.length > 1) {
            yardMngOpData.setStrValue(nodePath + "BuildInfo.buildAddr", bldAddrs[bldAddrs.length - 2]);
            yardMngOpData.setStrValue(nodePath + "BuildInfo.buildNo", bldAddrs[bldAddrs.length - 1]);
        } else {
            yardMngOpData.setStrValue(nodePath + "BuildInfo.buildAddr", "");
            yardMngOpData.setStrValue(nodePath + "BuildInfo.buildNo", "");
        }
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildUnitNum", buldUnitNum);
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildFullAddr", buildFullAddr);
        // 院落总户数
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildHsNum", request.getParameter("buildHsNum"));
        //保存院落坐标  中心点
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildPosition", request.getParameter("buildPosition"));
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildPositionX", request.getParameter("buildPositionX"));
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildPositionY", request.getParameter("buildPositionY"));
        yardMngOpData.setStrValue(nodePath + "BuildInfo.buildNote", request.getParameter("buildNote"));

        //------------------------------原来 保存管理信息---------------------------------
        //获取地址串 拼接报文
        int bn = 0;
        for (int i = 0; i < bldAddrs.length; i++) {
            String tempAddr = bldAddrs[i];
            if (StringUtil.isEmptyOrNull(tempAddr)) {
                continue;
            }
            yardMngOpData.setStrValue(nodePath + "BuildInfo.AddrPath.regName[${bn++}]", tempAddr);
        }

        String regEntityType = buildType;
        if (StringUtil.isEqual("1", buildType)) {
            regEntityType = "2";
        }
        yardMngOpData.setStrValue(nodePath + "BuildInfo.ttRegId", ttRegId);
        yardMngOpData.setStrValue(nodePath + "BuildInfo.regEntityType", regEntityType);  //建筑类型 接受前端传入
        yardMngOpData.setStrValue(nodePath + "BuildInfo.ttCompanyId", request.getParameter("ttCompanyId"));
        yardMngOpData.setStrValue(nodePath + "BuildInfo.ttMainTalk", request.getParameter("ttMainTalk"));
        yardMngOpData.setStrValue(nodePath + "BuildInfo.ttSecTalk", request.getParameter("ttSecTalk"));
        if (toDb) {
            yardMngOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            yardMngOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_BUILD_MNG_INFO", yardMngOpData);

        XmlBean baseOpData = new XmlBean();
        // 保存基本信息 BaseInfo
        baseOpData.setStrValue(nodePath + "HouseInfo.hsId", hsId);
        // 房屋类型
        baseOpData.setStrValue(nodePath + "HouseInfo.hsType", hsType);
        baseOpData.setStrValue(nodePath + "HouseInfo.hsCd", request.getParameter("hsCd"));

        //  地址截取都与建筑一致。
        if (bldAddrs.length > 0 && bldAddrs.length < 1) {
            baseOpData.setStrValue(nodePath + "HouseInfo.hsAddr", "");
            baseOpData.setStrValue(nodePath + "HouseInfo.hsAddrNo", bldAddrs[bldAddrs.length - 1]);
        } else if (bldAddrs.length > 1) {
            baseOpData.setStrValue(nodePath + "HouseInfo.hsAddr", bldAddrs[bldAddrs.length - 2]);
            baseOpData.setStrValue(nodePath + "HouseInfo.hsAddrNo", bldAddrs[bldAddrs.length - 1]);
        } else {
            baseOpData.setStrValue(nodePath + "HouseInfo.hsAddr", "");
            baseOpData.setStrValue(nodePath + "HouseInfo.hsAddrNo", "");
        }

        String hsFullAddr = request.getParameter("hsFullAddr");
        if (StringUtil.isEmptyOrNull(hsFullAddr) && StringUtil.isEqual(prjCd, "400")) {
            hsFullAddr = buildFullAddr;
        }
        baseOpData.setStrValue(nodePath + "HouseInfo.buldUnitNum", buldUnitNum);
        baseOpData.setStrValue(nodePath + "HouseInfo.hsAddrTail", hsAddrTail);
        baseOpData.setStrValue(nodePath + "HouseInfo.hsFullAddr", hsFullAddr);

        baseOpData.setStrValue(nodePath + "HouseInfo.buildId", buildId);
        // 正式房信息(间数、用途、朝向）
        baseOpData.setStrValue(nodePath + "HouseInfo.hsRoomNum", request.getParameter("hsRoomNum"));
        baseOpData.setStrValue(nodePath + "HouseInfo.hsUseType", request.getParameter("hsUseType"));
        baseOpData.setStrValue(nodePath + "HouseInfo.hsDt", request.getParameter("hsDt"));
        // 自建房信息(间数、用途、阳台封闭外扩）
        baseOpData.setStrValue(nodePath + "HouseInfo.hsSlfRoom", request.getParameter("hsSlfRoom"));
        baseOpData.setStrValue(nodePath + "HouseInfo.hsSlfUse", request.getParameter("hsSlfUse"));
        baseOpData.setStrValue(nodePath + "HouseInfo.landCertyNum", request.getParameter("landCertyNum"));

        // 保存房产地址坐标，自建房坐标；
        baseOpData.setStrValue(nodePath + "HouseInfo.hsRoomPos", request.getParameter("hsPoints"));
        baseOpData.setStrValue(nodePath + "HouseInfo.hsSlfRoomPos", request.getParameter("hsSlfPoints"));
        baseOpData.setStrValue(nodePath + "HouseInfo.hsPosX", request.getParameter("hsPosX"));
        baseOpData.setStrValue(nodePath + "HouseInfo.hsPosY", request.getParameter("hsPosY"));

        // 房屋状态
        String hsStatus = request.getParameter("hsStatus");

        baseOpData.setStrValue(nodePath + "HouseInfo.hsStatus", hsStatus);

        baseOpData.setStrValue(nodePath + "HouseInfo.hsNote", request.getParameter("hsNote"));
        if (toDb) {
            baseOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            baseOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_HS_BASE_INFO", baseOpData);

        // 保存腾退 管理信息
        XmlBean mngOpData = new XmlBean();
        String ttMainTalk = request.getParameter("housettMainTalk");
        String ttSecTalk = request.getParameter("housettSecTalk");
        if (StringUtil.isNotEmptyOrNull(ttMainTalk)) {
            mngOpData.setStrValue(nodePath + "HouseInfo.ttMainTalk", request.getParameter("housettMainTalk"));
        } else {
            mngOpData.setStrValue(nodePath + "HouseInfo.ttMainTalk", request.getParameter("ttMainTalk"));
        }
        if (StringUtil.isNotEmptyOrNull(ttSecTalk)) {
            mngOpData.setStrValue(nodePath + "HouseInfo.ttSecTalk", request.getParameter("housettSecTalk"));
        } else {
            mngOpData.setStrValue(nodePath + "HouseInfo.ttSecTalk", request.getParameter("ttSecTalk"));
        }
        mngOpData.setStrValue(nodePath + "HouseInfo.hsId", hsId);
        mngOpData.setStrValue(nodePath + "HouseInfo.ttCompanyId", request.getParameter("housettCompanyId"));
        mngOpData.setStrValue(nodePath + "HouseInfo.ttOrgId", request.getParameter("ttOrgId"));
        mngOpData.setStrValue(nodePath + "HouseInfo.ttRegId", ttRegId);
        if (toDb) {
            mngOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            mngOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_HS_TT_MNG_INFO", mngOpData);

        // 保存 经营信息
        XmlBean jyOpData = new XmlBean();
        jyOpData.setStrValue(nodePath + "entityName", "HouseInfo");
        jyOpData.setStrValue(nodePath + "EntityData.hsId", hsId);
        jyOpData.setStrValue(nodePath + "EntityData.businessStatus", request.getParameter("businessStatus"));
        jyOpData.setStrValue(nodePath + "EntityData.businessCertNum", request.getParameter("businessCertNum"));
        jyOpData.setStrValue(nodePath + "EntityData.businessCertDocIds", request.getParameter("businessCertDocIds"));
        jyOpData.setStrValue(nodePath + "EntityData.businessHsSize", request.getParameter("businessHsSize"));
        jyOpData.setStrValue(nodePath + "EntityData.busiMager", request.getParameter("busiMager"));
        jyOpData.setStrValue(nodePath + "EntityData.busiRelOwner", request.getParameter("busiRelOwner"));
        jyOpData.setStrValue(nodePath + "EntityData.yxDate", request.getParameter("yxDate"));
        jyOpData.setStrValue(nodePath + "EntityData.regstPlace", request.getParameter("regstPlace"));
        jyOpData.setStrValue(nodePath + "EntityData.businessNote", request.getParameter("businessNote"));
        if (toDb) {
            jyOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            jyOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_HS_BUSINESS_INFO", jyOpData);

        // 保存 低保、残疾信息、高龄、家庭结构、总户籍数、户籍人口
        XmlBean dbCjOpData = new XmlBean();
        dbCjOpData.setStrValue(nodePath + "entityName", "HouseInfo");
        dbCjOpData.setStrValue(nodePath + "EntityData.hsId", hsId);
        dbCjOpData.setStrValue(nodePath + "EntityData.familyStructure", request.getParameter("familyStructure"));
        dbCjOpData.setStrValue(nodePath + "EntityData.allFamNum", request.getParameter("allFamNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.famPsNum", request.getParameter("famPsNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.livePsNum", request.getParameter("livePsNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.isDivorce1", request.getParameter("isDivorce1"));
        dbCjOpData.setStrValue(nodePath + "EntityData.isDivorce2", request.getParameter("isDivorce2"));
        dbCjOpData.setStrValue(nodePath + "EntityData.hAgeNum", request.getParameter("hAgeNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.hAgeDocIds", request.getParameter("hAgeDocIds"));
        dbCjOpData.setStrValue(nodePath + "EntityData.cjNum", request.getParameter("cjNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.cjDocIds", request.getParameter("cjDocIds"));
        dbCjOpData.setStrValue(nodePath + "EntityData.dbNum", request.getParameter("dbNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.dbDocIds", request.getParameter("dbDocIds"));
        dbCjOpData.setStrValue(nodePath + "EntityData.seriousIll", request.getParameter("seriousIll"));
        dbCjOpData.setStrValue(nodePath + "EntityData.seriousIllDocIds", request.getParameter("seriousIllDocIds"));
        dbCjOpData.setStrValue(nodePath + "EntityData.jailNum", request.getParameter("jailNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.armyNum", request.getParameter("armyNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.motorNum", request.getParameter("motorNum"));
        dbCjOpData.setStrValue(nodePath + "EntityData.gzPerson", request.getParameter("gzPerson"));

        if (toDb) {
            dbCjOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            dbCjOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_ENTITY", dbCjOpData);

        // 房屋编制
        String[] hsFlags = request.getParameterValues("hsFlag");
        XmlBean hsFlagOpData = new XmlBean();
        hsFlagOpData.setStrValue(nodePath + "entityName", "HouseInfo");
        hsFlagOpData.setStrValue(nodePath + "EntityData.hsId", hsId);
        if (hsFlags != null) {
            for (int i = 0; i < hsFlags.length; i++) {
                hsFlagOpData.setStrValue(nodePath + "EntityData.HsFlags.HsFlag[${i}].hsFlag", hsFlags[i]);
            }
        } else {
            hsFlagOpData.setStrValue(nodePath + "EntityData.HsFlags", "");
        }
        if (toDb) {
            hsFlagOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            hsFlagOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_ENTITY", hsFlagOpData);

        // 问题类型
        String hsPbType = request.getParameter("saveHsPbType");
        String isPbHs = request.getParameter("isPbHs");
        if (StringUtil.isEqual("1", isPbHs)) {
            isPbHs = "1";
            String[] hsPbTypes = null;
            if (!StringUtil.isEmptyOrNull(hsPbType)) {
                hsPbType = hsPbType.substring(0, hsPbType.length() - 1);
                hsPbTypes = hsPbType.split(",");
            } else {
                hsPbTypes = [];
            }

            XmlBean pBData = new XmlBean();
            pBData.setStrValue(nodePath + "entityName", "HouseInfo");
            pBData.setStrValue(nodePath + "EntityData.hsId", hsId);
            pBData.setStrValue(nodePath + "EntityData.isPbHs", isPbHs);
            for (int i = 0; i < hsPbTypes.length; i++) {
                pBData.setStrValue(nodePath + "EntityData.hsPbTypes.hsPbType[${i}].hsPbType", hsPbTypes[i]);
            }
            if (hsPbTypes.length < 1) {
                pBData.setStrValue(nodePath + "EntityData.hsPbTypes", "");
            }
            pBData.setStrValue(nodePath + "EntityData.pbLevel", request.getParameter("pbLevel"));
            pBData.setStrValue(nodePath + "EntityData.pbDetail", request.getParameter("pbDetail"));
            pBData.setStrValue(nodePath + "EntityData.pbPlan", request.getParameter("pbPlan"));
            if (toDb) {
                pBData.setStrValue("OpData.toDb", "1");
            }
            if (toContext) {
                pBData.setStrValue("OpData.toContext", "1");
            }
            svcRequest.addOp("SAVE_ENTITY", pBData);

        } else {
            isPbHs = "0";
            XmlBean pBData = new XmlBean();
            pBData.setStrValue(nodePath + "entityName", "HouseInfo");
            pBData.setStrValue(nodePath + "EntityData.hsId", hsId);
            pBData.setStrValue(nodePath + "EntityData.isPbHs", isPbHs);
            if (toDb) {
                pBData.setStrValue("OpData.toDb", "1");
            }
            if (toContext) {
                pBData.setStrValue("OpData.toContext", "1");
            }
            svcRequest.addOp("SAVE_ENTITY", pBData);
        }

        // 保存 房屋附属信息
        XmlBean attachOpData = new XmlBean();
        attachOpData.setStrValue(nodePath + "entityName", "HouseInfo");
        attachOpData.setStrValue(nodePath + "EntityData.hsId", hsId);
        attachOpData.setStrValue(nodePath + "EntityData.ktNum", request.getParameter("ktNum"));
        attachOpData.setStrValue(nodePath + "EntityData.ktDocIds", request.getParameter("ktDocIds"));
        attachOpData.setStrValue(nodePath + "EntityData.rsqNum", request.getParameter("rsqNum"));
        attachOpData.setStrValue(nodePath + "EntityData.rsqDocIds", request.getParameter("rsqDocIds"));
        attachOpData.setStrValue(nodePath + "EntityData.ghNum", request.getParameter("ghNum"));
        attachOpData.setStrValue(nodePath + "EntityData.ghDocIds", request.getParameter("ghDocIds"));
        attachOpData.setStrValue(nodePath + "EntityData.yxNum", request.getParameter("yxNum"));
        attachOpData.setStrValue(nodePath + "EntityData.yxDocIds", request.getParameter("yxDocIds"));
        attachOpData.setStrValue(nodePath + "EntityData.wlNum", request.getParameter("wlNum"));
        attachOpData.setStrValue(nodePath + "EntityData.wlDocIds", request.getParameter("wlDocIds"));
        attachOpData.setStrValue(nodePath + "EntityData.mgdGrZf", request.getParameter("mgdGrZf"));
        attachOpData.setStrValue(nodePath + "EntityData.mgDDocIds", request.getParameter("mgDDocIds"));
        attachOpData.setStrValue(nodePath + "EntityData.fwfsNotes", request.getParameter("fwfsNotes"));

        attachOpData.setStrValue(nodePath + "EntityData.transferStatus", request.getParameter("transferStatus"));
        attachOpData.setStrValue(nodePath + "EntityData.transferDocs", request.getParameter("transferDocs"));
        if (toDb) {
            attachOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            attachOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_ENTITY", attachOpData);

        // 保存保障性住房信息
        XmlBean bzxHsInfo = new XmlBean();
        bzxHsInfo.setStrValue(nodePath + "entityName", "HouseInfo");
        bzxHsInfo.setStrValue(nodePath + "EntityData.hsId", hsId);
        bzxHsInfo.setStrValue(nodePath + "EntityData.isEngoy", request.getParameter("isEngoy"));
        bzxHsInfo.setStrValue(nodePath + "EntityData.aployCatgry", request.getParameter("aployCatgry"));
        bzxHsInfo.setStrValue(nodePath + "EntityData.isChoose", request.getParameter("isChoose"));
        bzxHsInfo.setStrValue(nodePath + "EntityData.aployName", request.getParameter("aployName"));
        bzxHsInfo.setStrValue(nodePath + "EntityData.chooseHsPlac", request.getParameter("chooseHsPlac"));
        bzxHsInfo.setStrValue(nodePath + "EntityData.bzxNote", request.getParameter("bzxNote"));
        if (toDb) {
            bzxHsInfo.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            bzxHsInfo.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_ENTITY", bzxHsInfo);

        // 调用人员信息保存组件  保存人员信息
        XmlBean personOpData = new XmlBean();
        //根路径下 公用信息
        personOpData.setStrValue(nodePath + "prjOrgId", request.getParameter("ttOrgId"));
        personOpData.setStrValue(nodePath + "regId", ttRegId);
        personOpData.setStrValue(nodePath + "oldHsId", hsId);

        //单个人员信息
        String[] personIds = request.getParameterValues("personId");
        String[] personNames = request.getParameterValues("personName");
        String[] familyOwnFlags = request.getParameterValues("familyOwnFlag");
        String[] familyPersonIds = request.getParameterValues("familyPersonId");
        String[] familyPersonRels = request.getParameterValues("familyPersonRel");
        String[] personCertyNums = request.getParameterValues("personCerty");
        String[] personTelphones = request.getParameterValues("personTelphone");
        String[] personCertyDocIds = request.getParameterValues("personCertyDocIds");
        String[] familyDocIds = request.getParameterValues("familyDocIds");
        String[] personJobAddr = request.getParameterValues("personJobAddr");
        String[] familyAddr = request.getParameterValues("familyAddr");
        String[] personLiveAddr = request.getParameterValues("personLiveAddr");
        String[] personAge = request.getParameterValues("personAge");
        String[] isLiveHere = request.getParameterValues("isLiveHere");
        String[] marryStatus = request.getParameterValues("marryStatus");
        String[] personType = request.getParameterValues("personType");

        nodePath = "OpData.Persons."
        int m = 0;
        for (int i = 0; i < personNames.length; i++) {
            personOpData.setStrValue(nodePath + "Person[${m}].personId", personIds[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].personName", personNames[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].familyOwnFlag", familyOwnFlags[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].familyPersonId", familyPersonIds[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].familyPersonRel", familyPersonRels[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].personCertyNum", personCertyNums[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].personTelphone", personTelphones[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].familyAddr", buildFullAddr);//取房屋地址
            personOpData.setStrValue(nodePath + "Person[${m}].personCertyDocIds", personCertyDocIds[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].familyDocIds", familyDocIds[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].personJobAddr", personJobAddr[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].familyAddr", familyAddr[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].personLiveAddr", personLiveAddr[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].personAge", personAge[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].isLiveHere", isLiveHere[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].marryStatus", marryStatus[i]);
            personOpData.setStrValue(nodePath + "Person[${m}].personType", personType[i]);
            m++;
        }
        if (toDb) {
            personOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            personOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_HS_PERSON_INFO", personOpData);

        // 保存 房产 产权信息
        nodePath = "OpData."
        XmlBean ownerOpDate = new XmlBean();
        ownerOpDate.setStrValue(nodePath + "hsId", hsId);
        ownerOpDate.setStrValue(nodePath + "hsBuildSize", request.getParameter("hsBuildSize"));
        ownerOpDate.setStrValue(nodePath + "hsUseSize", request.getParameter("hsUseSize"));
        ownerOpDate.setStrValue(nodePath + "hsOwnerType", request.getParameter("hsOwnerType"));
        ownerOpDate.setStrValue(nodePath + "hsOwnerModel", request.getParameter("hsOwnerModel"));  //要处理默认值
        ownerOpDate.setStrValue(nodePath + "hsOwnerPersons", request.getParameter("hsOwnerPersons"));
        ownerOpDate.setStrValue(nodePath + "hsPubOwnerName", request.getParameter("hsPubOwnerName"));
        ownerOpDate.setStrValue(nodePath + "hsPubOwnerCerty", request.getParameter("hsPubOwnerCerty"));
        ownerOpDate.setStrValue(nodePath + "hsOwnerNote", request.getParameter("hsOwnerNote"));
        ownerOpDate.setStrValue(nodePath + "landCertyNum", request.getParameter("landCertyNum"));  //土地证号
        ownerOpDate.setStrValue(nodePath + "landCertyDocIds", request.getParameter("landCertyDocIds"));

        nodePath = "OpData.HsOwners."
        String[] ownerPsIds = request.getParameterValues("ownerPsId");
        String[] ownerCertys = request.getParameterValues("ownerCerty");
        String[] ownerCertyDocIds = request.getParameterValues("ownerCertyDocIds");
        String[] ownerPrecents = request.getParameterValues("ownerPrecent");
        String[] ownerNotes = request.getParameterValues("ownerNote");
        int n = 0;
        for (int i = 0; i < ownerPsIds.length; i++) {
            ownerOpDate.setStrValue(nodePath + "HsOwner[${n}].ownerPsId", ownerPsIds[i]);
            ownerOpDate.setStrValue(nodePath + "HsOwner[${n}].ownerCerty", ownerCertys[i]);
            ownerOpDate.setStrValue(nodePath + "HsOwner[${n}].ownerCertyDocIds", ownerCertyDocIds[i]);
            ownerOpDate.setStrValue(nodePath + "HsOwner[${n}].ownerPrecent", ownerPrecents[i]);
            ownerOpDate.setStrValue(nodePath + "HsOwner[${n}].ownerNote", ownerNotes[i]);
            n++;
        }
        if (toDb) {
            ownerOpDate.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            ownerOpDate.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_HS_OWNER_INFO", ownerOpDate);

        // 腾退签约信息保存。调用通用保存
        String hsCtId = request.getParameter("hsCtId");
        if (StringUtil.isEmptyOrNull(hsCtId)) {
            hsCtId = "\${hsCtId}";
        }
        //签约人员编号名称特殊处理
        String ctPsIdStr = "";
        for (int i = 0; i < ownerPsIds.length; i++) {
            ctPsIdStr = ctPsIdStr + ownerPsIds[i] + ",";
        }
        if (ctPsIdStr.length() > 0) {
            ctPsIdStr = ctPsIdStr.substring(0, ctPsIdStr.length() - 1);
        }
        XmlBean contractOpData = new XmlBean();
        nodePath = "OpData.HsCtInfo.";
        contractOpData.setStrValue(nodePath + "hsCtId", hsCtId);
        contractOpData.setStrValue(nodePath + "hsId", hsId);
        contractOpData.setStrValue(nodePath + "ctType", request.getParameter("ctType"));
        contractOpData.setStrValue(nodePath + "isAgree", request.getParameter("isAgree"));
        contractOpData.setStrValue(nodePath + "ctPsIds", ctPsIdStr); //拼接ids
        //取产权人里存好的拼接好的names
        contractOpData.setStrValue(nodePath + "ctPsNames", request.getParameter("hsOwnerPersons"));
        //拼接委托人Id
        String[] ctWtPsIds = request.getParameterValues("ctWtPsId");
        String ctWtPsId = "";
        if (ctWtPsIds.length > 0) {
            for (int i = 0; i < ctWtPsIds.length; i++) {
                ctWtPsId = ctWtPsId + ctWtPsIds[i] + ",";
            }
            if (ctWtPsId.length() > 0) {
                ctWtPsId = ctWtPsId.substring(0, ctWtPsId.length() - 1);
            }
        }
//        协议状态待处理。
//        String ctStatus = request.getParameter("ctStatus");
//        if(StringUtil.isEmptyOrNull(ctStatus)){
//            ctStatus = "1";
//        }
//        contractOpData.setStrValue(nodePath + "ctStatus", ctStatus);
        contractOpData.setStrValue(nodePath + "ctWtPsId", ctWtPsId);
        contractOpData.setStrValue(nodePath + "ctNote", request.getParameter("ctNote"));
        contractOpData.setStrValue(nodePath + "ctWtPsDocs", request.getParameter("ctWtPsDocs"));
        if (toDb) {
            contractOpData.setStrValue("OpData.toDb", "1");
        }
        if (toContext) {
            contractOpData.setStrValue("OpData.toContext", "1");
        }
        svcRequest.addOp("SAVE_HOUSE_CT_INFO", contractOpData);

        // 保存关联附件
        String hsDocs = request.getParameter("hsDocs");
        if (StringUtil.isNotEmptyOrNull(hsDocs)) {
            String[] docIdArr = hsDocs.split(",");
            int count = 0;
            Set<String> processItem = new HashSet<String>();
            XmlBean docBeans = new XmlBean();
            for (String temp : docIdArr) {
                if (StringUtil.isNotEmptyOrNull(temp) && !processItem.contains(temp)) {
                    // 处理本次文档
                    docBeans.setStrValue("OpData.SvcDocs.svcDoc[${count}].docId", temp);
                    docBeans.setStrValue("OpData.SvcDocs.svcDoc[${count}].relType", "100");
                    docBeans.setStrValue("OpData.SvcDocs.svcDoc[${count++}].relId", "${hsId}");
                    //
                    processItem.add(temp);
                }
            }
            // 更新文档关联关系
            if (count > 0) {
                if (toDb) {
                    docBeans.setStrValue("OpData.toDb", "1");
                }
                if (toContext) {
                    docBeans.setStrValue("OpData.toContext", "1");
                }
                svcRequest.addOp("UPDATE_DOC_REL_BATCH", docBeans);
            }
        }

        // 调用启动流程组件
        if (StringUtil.isNotEmptyOrNull(request.getParameter("hsId")) && StringUtil.isNotEmptyOrNull(request.getParameter("busiKey"))) {
            XmlBean startProc = new XmlBean();
            String prePath = "OpData.WfExecute.";
            startProc.setStrValue(prePath + "procDefKey", request.getParameter("procDefKey"));
            startProc.setStrValue(prePath + "busiKey", request.getParameter("busiKey"));
            startProc.setStrValue(prePath + "procStUser", svcRequest.getReqStaffCd());
            startProc.setStrValue(prePath + "procInsName", request.getParameter("procInsName"));
            startProc.setStrValue(prePath + "assignee", request.getParameter("assignee"));
            startProc.setStrValue(prePath + "procPrjCd", request.getParameter("procPrjCd"));
            // 处理所有的流程变量
            prePath = "OpData.WfExecute.Variables.";
            startProc.setStrValue(prePath + "hsId", request.getParameter("hsId"));
            startProc.setStrValue(prePath + "assignee", request.getParameter("assignee"));
            startProc.setStrValue(prePath + "procInsName", request.getParameter("procInsName"));
            startProc.setStrValue(prePath + "procPrjCd", request.getParameter("procPrjCd"));

            prePath = "OpData.WfExecute.ProcBidVars.";
            startProc.setStrValue(prePath + "hsId", request.getParameter("hsId"));
            startProc.setStrValue(prePath + "assignee", request.getParameter("assignee"));
            startProc.setStrValue(prePath + "taskComment", request.getParameter("taskComment"));
            startProc.setStrValue(prePath + "procPrjCd", request.getParameter("procPrjCd"));
            // 启动腾退信息修改流程
            svcRequest.addOp("SAVE_START_PROC_BY_KEY_WH_DEFF", startProc);
        }

        //调用最新的服务
        SvcResponse svcResponse = SvcUtil.transaction(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        if (result) {
            if (addFlag) {
                //新增操作
                hsId = svcResponse.getFirstOpRsp("SAVE_HS_BASE_INFO").getStrValue("Operation.OpResult.HouseInfo.hsId");
                buildId = svcResponse.getFirstOpRsp("SAVE_HS_BASE_INFO").getStrValue("Operation.OpResult.HouseInfo.buildId");
                ttRegId = svcResponse.getFirstOpRsp("SAVE_HS_BASE_INFO").getStrValue("Operation.OpResult.HouseInfo.ttRegId");
            }
        }
        String saveFlag = request.getParameter("saveFlag");
        if ("1".equals(saveFlag)) {
            if (result) {
                //获取流程id，判断有没有执行流程
                String procInsId = null;
                XmlBean procInsXml = svcResponse.getFirstOpRsp("SAVE_START_PROC_BY_KEY_WH_DEFF");
                if (procInsXml != null) {
                    procInsId = procInsXml.getStrValue("Operation.OpResult.WfExecute.procInsId");
                }
                if (StringUtil.isEmptyOrNull(procInsId)) {
                    ResponseUtil.printAjax(response, """{success:${result}, errMsg:"${errMsg}", hsId:"${
                        hsId
                    }", buildId:"${buildId}", ttRegId:"${ttRegId}"}""");
//                    return new ModelAndView("redirect:/eland/ph/ph003/ph00301-initHs.gv?hsId=${hsId}");
                } else {
                    ResponseUtil.printAjax(response, """{success:${result}, errMsg:"${errMsg}", procInsId:"${
                        procInsId
                    }"}""");
//                    return new ModelAndView("redirect:/oframe/wf/wf001/wf001-viewWf.gv?procInsId=${procInsId}");
                }
            } else {
                ResponseUtil.printAjax(response, """{success:${result}, errMsg:"${errMsg}", hsId:"${hsId}", buildId:"${
                    buildId
                }", ttRegId:"${ttRegId}"}""");
            }
        } else {
            String jsonStr = """{success:${result}, errMsg:"${errMsg}", hsId:"${hsId}", buildId:"${
                buildId
            }", ttRegId:"${ttRegId}"}""";
            ResponseUtil.printAjax(response, jsonStr);
        }
    }

    /**
     * 录入信息审核
     * @param request
     * @param response
     */
    public ModelAndView auditing(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        String manualAuditResult = request.getParameter("manualAuditResult");
        String manualAuditNote = request.getParameter("manualAuditNote");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.EntityData.hsId", hsId);
        // 完成审批
        opData.setStrValue("OpData.EntityData.manualAuditStatus", "2");
        // 审批结果
        opData.setStrValue("OpData.EntityData.manualAuditResult", manualAuditResult);
        // 审批信息备注
        opData.setStrValue("OpData.EntityData.manualAuditNote", manualAuditNote);
        // 审批时间
        opData.setStrValue("OpData.EntityData.manualAuditTime", DateUtil.getSysDate());
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            return new ModelAndView("forward:/eland/ph/ph003/ph00301-initHs.gv?hsId=${hsId}");
        } else {
            ResponseUtil.print(response, "<div><input type='text' name='errMsg' value='${svcResponse.getErrMsg()}'/></div>");
        }
    }

    /**
     * 申请信息审批
     * @param request
     * @param response
     */
    public ModelAndView applyAuditing(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.EntityData.hsId", hsId);
        // 完成审批
        opData.setStrValue("OpData.EntityData.manualAuditStatus", "1");
        // 审批时间
        opData.setStrValue("OpData.EntityData.manualAuditTime", DateUtil.getSysDate());
        // 调用服务保存审批结果
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            return new ModelAndView("forward:/eland/ph/ph003/ph00301-initHs.gv?hsId=${hsId}");
        } else {
            ResponseUtil.print(response, "<div><input type='text' name='errMsg' value='${svcResponse.getErrMsg()}'/></div>");
        }
    }

    /** ------------------------------打开 院落 地图--------------------------------------- */
    public ModelAndView openBuildMap(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");

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
        return new ModelAndView("/eland/ph/ph003/ph00301_buildmap", modelMap);
    }

    /** ------------------------------打开 房产 地图--------------------------------------- */
    public ModelAndView openHsMap(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");

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
        return new ModelAndView("/eland/ph/ph003/ph00301_hsmap", modelMap);
    }

    /**
     * ①根据当前房屋地址全部房屋坐标
     *
     */
    public ModelAndView queryOthHs(HttpServletRequest request, HttpServletResponse response) {
        // todo 查询
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String hsId = requestUtil.getStrParam("hsId");
        String hsFullAddr = requestUtil.getStrParam("hsFullAddr");
//        String hsArea = requestUtil.getStrParam("hsArea");
//        String hsAddr = requestUtil.getStrParam("hsAddr");
        String prjCd = requestUtil.getStrParam("prjCd");
        // 处理结果
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsFullAddr");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "%${hsFullAddr}%");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "like");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "hsId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "!=");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "hsRoomPos");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "''");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "!=");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsRoomPos");//正式房坐标
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "hsSlfRoomPos");//自建房屋坐标
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "hsPosX");//中心点X
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "hsPosY");//中心点Y
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "hsFullAddr");//全地址
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "hsStatus");//房屋状态
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "hsOwnerPersons");//产权人
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "hsAddrNo");//房屋门牌
        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
//        println "opData-->" + opData;
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        boolean resResult = svcResponse.isSuccess();
        if (resResult) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
//            println "queryResult---------->" + queryResult;
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int count = queryResult.getListNum("PageData.Row");
                int middNum = 0;
                if (count == 0) {
                    middNum == count;
                } else if (count > 0 && (count % 2 == 0)) {
                    middNum = count / 2;
                } else if (count > 0 && (count % 2 != 0)) {
                    middNum = (count - 1) / 2;
                }
                for (int i = 0; i < count; i++) {
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.put("hsId", queryResult.getStrValue("PageData.Row[${i}].hsId"));
                    temp.put("hsRoomPos", queryResult.getStrValue("PageData.Row[${i}].hsRoomPos"));
                    temp.put("hsSlfRoomPos", queryResult.getStrValue("PageData.Row[${i}].hsSlfRoomPos"));
                    temp.put("hsPosX", queryResult.getStrValue("PageData.Row[${i}].hsPosX"));
                    temp.put("hsPosY", queryResult.getStrValue("PageData.Row[${i}].hsPosY"));
                    temp.put("hsFullAddr", queryResult.getStrValue("PageData.Row[${i}].hsFullAddr"));
                    temp.put("hsStatus", queryResult.getStrValue("PageData.Row[${i}].hsStatus"));
                    temp.put("hsOwnerPersons", queryResult.getStrValue("PageData.Row[${i}].hsOwnerPersons"));
                    temp.put("hsAddrNo", queryResult.getStrValue("PageData.Row[${i}].hsAddrNo"));
                    temp.put("middNum", middNum + "");
                    result.add(temp);
                }
            }
        }
        JSONArray othHsResult = JSONArray.fromObject(result);
        String errMsg = "";
        String jsonStr = """{"success": ${resResult}, "errMsg":"${
            errMsg
        }","othHsResult":${
            othHsResult.toString()
        }}""";
//        println "jsonStr-->" + jsonStr;
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * ①根据当前房屋地址全部院落坐标
     *
     */
    public ModelAndView queryOthBld(HttpServletRequest request, HttpServletResponse response) {
        // todo 查询
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String buildId = requestUtil.getStrParam("buildId");
        String buildFullAddr = requestUtil.getStrParam("buildFullAddr");
        String prjCd = requestUtil.getStrParam("prjCd");
        // 处理结果
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "BuildInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "buildFullAddr");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "%${buildFullAddr}%");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "like");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "buildId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", buildId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "!=");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "buildPosition");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "''");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "!=");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "buildId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "buildPosition");//院落位置
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "buildPositionX");//中心点位置X坐标
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "buildPositionY");//中心点位置Y坐标
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "buildFullAddr");//自建房屋坐标
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "buildNo");//自建房屋坐标
        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        boolean resResult = svcResponse.isSuccess();
        if (resResult) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
//            println "queryResult---------->" + queryResult;
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int count = queryResult.getListNum("PageData.Row");
                int middNum = 0;
                if (count == 0) {
                    middNum == count;
                } else if (count > 0 && (count % 2 == 0)) {
                    middNum = count / 2;
                } else if (count > 0 && (count % 2 != 0)) {
                    middNum = (count - 1) / 2;
                }
                for (int i = 0; i < count; i++) {
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.put("buildId", queryResult.getStrValue("PageData.Row[${i}].buildId"));
                    temp.put("buildPosition", queryResult.getStrValue("PageData.Row[${i}].buildPosition"));
                    temp.put("buildPositionX", queryResult.getStrValue("PageData.Row[${i}].buildPositionX"));
                    temp.put("buildPositionY", queryResult.getStrValue("PageData.Row[${i}].buildPositionY"));
                    temp.put("buildFullAddr", queryResult.getStrValue("PageData.Row[${i}].buildFullAddr"));
                    temp.put("buildNo", queryResult.getStrValue("PageData.Row[${i}].buildNo"));
                    temp.put("middNum", middNum + "");
                    result.add(temp);
                }
            }
        }
        JSONArray othBldResult = JSONArray.fromObject(result);
        String errMsg = "";
        String jsonStr = """{"success": ${resResult}, "errMsg":"${
            errMsg
        }","othBldResult":${
            othBldResult.toString()
        }}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 弹框查询方法 */
    public ModelAndView findPopupData(HttpServletRequest request, HttpServletResponse response) {
        //调用服务查询 院落 房产 状态对应的颜色信息
        SvcRequest svcReqFilt = RequestUtil.getSvcRequest(request);
        String clickId = request.getParameter("clickId");
        String clickType = request.getParameter("clickType");
        String prjCd = request.getParameter("prjCd");
        String entityName = "";
        String entityKey = "";
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", prjCd);

        // 处理结果，返回弹框页面。要修改弹框页面内容只需要修改一下两个页面 ph003_popup_hs  ph003_popup_bld
        String[] resultFields = [];
        String returnPage = "";
        if (StringUtil.isEqual(clickType, "house")) {
            entityName = "HouseInfo";
            entityKey = "hsId";
            resultFields = ["hsId", "hsOwnerPersons", "hsFullAddr", "hsOwnerType", "hsStatus", "hsBuildSize"];
            returnPage = "/eland/ph/ph003/ph003_popup_hs";
        } else if (StringUtil.isEqual(clickType, "build")) {
            entityName = "BuildInfo";
            entityKey = "buildId";
            resultFields = ["buildId", "buildFullAddr", "ttRegId", "buildType", "buildStatus", "buildLandSize"];
            returnPage = "/eland/ph/ph003/ph003_popup_bld";
        }

        XmlBean opData = new XmlBean();
        // 获取上级区域编号
        opData.setStrValue("OpData.entityName", entityName);
        /* 增加查询条件 */
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", entityKey);
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", clickId);
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        /* 需要获取的字段*/
        for (int i = 0; i < resultFields.length; i++) {
            opData.setStrValue("OpData.ResultFields.fieldName[${i}]", resultFields[i]);
        }
        svcReqFilt.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcReqFilt);

        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr;
        if (result) {
            XmlBean resultDataBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
            modelMap.put("selectData", resultDataBean.getRootNode());
        }
        return new ModelAndView(returnPage, modelMap);
    }

    /** 赠送面积初始化页面 */
    public ModelAndView hsSizeCtGive(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        String hsId = requestUtil.getStrParam("hsId");
        String prjCd = requestUtil.getStrParam("prjCd");

        //签约信息  选房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean contractReqData = new XmlBean();
        contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //获取签约信息查询结果
            XmlBean conRspData = svcResponse.getFirstOpRsp("QUERY_HS_CT_INFO");
            if (conRspData != null) {
                // 获取签约腾退信息
                XmlBean ctInfo = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo");
                String hsCtId = "";
                if (ctInfo != null) {
                    modelMap.put("ctInfo", ctInfo.getRootNode());
                    hsCtId = ctInfo.getStrValue("HsCtInfo.hsCtId");
                    modelMap.put("hsCtId", hsCtId);
                }
                //根据签约id--hsCtId查询 选房区域
                Map<String, String> giveMap = new LinkedHashMap();
                if (StringUtil.isNotEmptyOrNull(hsCtId)) {
                    XmlBean hsGvData = new XmlBean();
                    hsGvData.setStrValue("OpData.HsCtGive.hsCtId", hsCtId);
                    hsGvData.setStrValue("OpData.HsCtGive.giveStatusCd", "1");
//                        hsGvData.setStrValue("OpData.HsCtGive.getFlag", "true");
                    svcRequest = RequestUtil.getSvcRequest(request);
                    svcRequest.addOp("QUERY_GV_INFO_LIST", hsGvData);
                    SvcResponse giveQueryResp = query(svcRequest);
                    if (giveQueryResp.isSuccess()) {
                        XmlBean gvRsp = giveQueryResp.getFirstOpRsp("QUERY_GV_INFO_LIST");
                        Set hsGvRegId = new HashSet();
                        if (gvRsp != null) {
                            int giveNum = gvRsp.getListNum("Operation.OpResult.HsCtGives.HsCtGive");
                            List aGiveSize = new ArrayList();
                            List bGiveSize = new ArrayList();
                            for (int i = 0; i < giveNum; i++) {
                                XmlBean giveBean = gvRsp.getBeanByPath("Operation.OpResult.HsCtGives.HsCtGive[${i}]");
                                //判断是赠出还是 获赠
                                String hsCtAId = giveBean.getStrValue("HsCtGive.hsCtAId");
                                String hsCtRegId = giveBean.getStrValue("HsCtGive.regId");
                                hsGvRegId.add(hsCtRegId);
                                if (StringUtil.isEqual(hsCtId, hsCtAId)) {
                                    //赠出面积
                                    aGiveSize.add(giveBean.getRootNode());
                                } else {
                                    //获赠
                                    bGiveSize.add(giveBean.getRootNode());
                                }
                            }
                            hsGvRegId.remove("");
                            hsGvRegId.remove(null);
                            modelMap.put("hsGvRegId", hsGvRegId);
                            modelMap.put("aGiveSize", aGiveSize);
                            modelMap.put("bGiveSize", bGiveSize);
                        }
                    }
                    //获取签约信息里[面积分配]信息
                    int chooseRegNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.HsRegInfo");
                    //[选房区域id，选房bean]
                    Map<String, XmlNode> chRegMap = new HashMap();
                    for (int i = 0; i < chooseRegNum; i++) {
                        XmlBean chooseRegBean = null;
                        chooseRegBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.HsRegInfo[${i}]");
                        String chRegId = chooseRegBean.getStrValue("HsRegInfo.ttRegId");
                        chRegMap.put(chRegId, chooseRegBean.getRootNode());
                    }
                    modelMap.put("chooseRegMap", chRegMap);
                }
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ph/ph003/ph00301_giveSize", modelMap);
    }

    /** 查询赠送面积接收人 */
    public void findGvPerson(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String hsId = requestUtil.getStrParam("hsId");
        String gvRegId = requestUtil.getStrParam("gvRegId");
        String prjCd = requestUtil.getStrParam("prjCd");
        //查询赠送接收人
        XmlBean opData = new XmlBean();
        String prePath = "OpData.HsCtGive.";
        opData.setStrValue(prePath + "hsId", hsId);
        opData.setStrValue(prePath + "ttRegId", gvRegId);
        opData.setStrValue(prePath + "ctStatus", '1');
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_GV_CT_LIST", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_GV_CT_LIST");
            if (resultBean != null) {
                int rowCount = resultBean.getListNum("Operation.OpResult.HsCtGives.HsCtGive");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = resultBean.getStrValue("Operation.OpResult.HsCtGives.HsCtGive[${i}].hsCtId");
                    String tempField02 = resultBean.getStrValue("Operation.OpResult.HsCtGives.HsCtGive[${i}].ctPsNames");
                    Map<String, String> temp = ["hsCtId": tempField01, "ctPsNames": tempField02];
                    result.add(temp);
                }
                ResponseUtil.print(response, JSONArray.fromObject(result).toString());
            } else {
                ResponseUtil.printAjax(response, "{}");
                return;
            }
        }
    }

    /**  保存赠送记录 */
    public void saveGive(HttpServletRequest request, HttpServletResponse response) {
        //赠送保存成功之后，调用重新计算面积组建
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //获取赠送id  取消之
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HsCtGiveInfo");
        opData.setStrValue(prePath + "EntityData.hsCtGiveId", "\${hsCtGiveId}");
        opData.setStrValue(prePath + "EntityData.hsCtAId", request.getParameter("hsCtAId"));
        opData.setStrValue(prePath + "EntityData.hsCtBId", request.getParameter("hsCtBId"));
        opData.setStrValue(prePath + "EntityData.ctSize", request.getParameter("ctSize"));
        opData.setStrValue(prePath + "EntityData.giveStatusCd", "1");
        opData.setStrValue(prePath + "EntityData.giveStatusDate", DateUtil.getSysDate());
        opData.setStrValue(prePath + "EntityData.giveStatusStaffId", svcRequest.getReqStaffIdInt());
        opData.setStrValue(prePath + "EntityData.createStaffId", svcRequest.getReqStaffIdInt());
        opData.setStrValue(prePath + "EntityData.createDate", DateUtil.getSysDate());
        opData.setStrValue(prePath + "EntityData.regId", request.getParameter("ttRegId"));
        svcRequest.addOp("SAVE_ENTITY", opData);

        XmlBean giveData = new XmlBean();
        giveData.setStrValue("OpData.HsCtGive.hsCtGiveId", "\${hsCtGiveId}");
        svcRequest.addOp("SAVE_REVISE_ACR", giveData);
        SvcResponse svcResponse = transaction(svcRequest);
        //返回请求结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 取消赠送 */
    public void cancelGive(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String hsCtGiveId = requestUtil.getStrParam("hsCtGiveId");
        //获取赠送id  取消之
        XmlBean opData = new XmlBean();
        String prePath = "OpData.HsCtGive.";
        opData.setStrValue(prePath + "hsCtGiveId", hsCtGiveId);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("SAVE_CANCEL_GIVE", opData);
        svcRequest.addOp("SAVE_REVISE_ACR", opData);
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        //返回请求结果
        ResponseUtil.printAjax(response, """{success:"${result}", errorMsg:"${svcResponse.getErrMsg()}" }""");
    }

    /**  初始化 地址 区域树 */
    public ModelAndView initAddrTree(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("objName", request.getParameter("objName"));
        modelMap.put("objValue", request.getParameter("objValue"));
        modelMap.put("fromOp", request.getParameter("fromOp"));
        return new ModelAndView("/eland/ph/ph003/tree", modelMap);
    }

    /**
     * 处理改变的xmlBean 的合并
     * @param prePath oldBean获取子节点的路径
     * @param oldBean 要合并的oldbean
     * @param changeBean 变化的Bean
     * @return 返回合并之后的Bean
     */
    private XmlBean margeXmlBean(String oldPrePath, XmlBean oldBean, String newPrePath, XmlBean newXmlBean) {
        if (oldBean == null) {
            return oldBean;
        }
        List oldNodeList = oldBean.getNodeNames(oldPrePath);
        if (newXmlBean != null) {
            List nodeList = newXmlBean.getNodeNames("${newPrePath}");
            for (int i = 0; i < nodeList.size(); i++) {
                String attrName = nodeList.get(i);
                for (int j = 0; j < oldNodeList.size(); j++) {
                    String oldAttrName = oldNodeList[j];
                    //新值、旧值
                    String oldValue = newXmlBean.getStrValue("${newPrePath}.${attrName}_old");
                    String newValue = newXmlBean.getStrValue("${newPrePath}.${attrName}");
                    if (StringUtil.isEqual(attrName, oldAttrName)) {
                        if (StringUtil.isEmptyOrNull(oldValue)) {
                            oldValue = "空";
                        }
                        oldBean.setStrValue("${oldPrePath}.${oldAttrName}_old", oldValue);
                        oldBean.setStrValue("${oldPrePath}.${oldAttrName}", newValue);
                        continue;
                    }
                }
            }
        }
        return oldBean;
    }

    /**
     * 将newXmlBean 里的所有人 拉出来转换成Map
     * @param newXmlBean
     * @return
     */
    private Map xmlBeanToPersonMap(XmlBean newXmlBean) {
        Map<String, XmlBean> allPersonMap = new HashMap();
        List newEntityNames = newXmlBean.getNodeNames("Operation");
        for (int i = 0; i < newEntityNames.size(); i++) {
            String entityName = newEntityNames.get(i);
            //personInfo 实体
            if (entityName.startsWith("PersonInfo")) {
                String everyPsId = entityName.substring(entityName.indexOf("_") + 1, entityName.length());
                XmlBean person = newXmlBean.getBeanByPath("Operation.${entityName}");
                XmlBean personMapBean = changBeanRootNote(person, entityName, "Person")
                allPersonMap.put(everyPsId, personMapBean);
            }
        }
        return allPersonMap;
    }

    /**
     * 装换XML报文的跟节点
     * @param inXmlBean 需要转换的Bean
     * @param oldRootPath 原跟节点
     * @param newRootPath 新的跟节点
     * @return 转换后的XMLBean
     */
    private XmlBean changBeanRootNote(XmlBean inXmlBean, String oldRootPath, String newRootPath) {
        if (inXmlBean == null) {
            return null;
        }
        List<String> nodeNames = inXmlBean.getNodeNames(oldRootPath);
        XmlBean resultBean = new XmlBean();
        for (String temp : nodeNames) {
            resultBean.setValue(newRootPath + ".${temp}", inXmlBean.getValue(oldRootPath + ".${temp}"));
        }
        return resultBean;
    }

}
