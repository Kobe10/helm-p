import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.excel.ExcelReader
import com.shfb.oframe.core.util.exception.SystemException
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 外迁房源管理
 */
class oh001 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("regUseType", request.getParameter("regUseType"));
        // 获取显示模式
        modelMap.put("showFyTree", getCookieByName(request, "showFyTree"));
        modelMap.put("showFyTreeModel", getCookieByName(request, "showFyTreeModel"));
        return new ModelAndView("/eland/oh/oh001/oh001", modelMap);
    }

    /**
     * 获取户型目录结构
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void hxTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String prjCd = request.getParameter("prjCd");
        String treeType = request.getParameter("treeType");
        String objValue = request.getParameter("objValue");
        Set<String> checkedOrg = new HashSet<String>();
        if (StringUtil.isNotEmptyOrNull(objValue)) {
            String[] objValues = objValue.split(",");
            for (int i = 0; i < objValues.length; i++) {
                checkedOrg.add(objValues[i]);
            }
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 查询区域信息
        XmlBean opData = new XmlBean();
        opData.setValue("OpData.PrjReg.prjCd", prjCd);
        opData.setValue("OpData.PrjReg.regUseType", treeType);
        opData.setValue("OpData.PrjReg.rightControlFlag", false);
        svcRequest.addOp("QUERY_REG_TREE", opData);

        // 查询房源信息
        opData = new XmlBean();
        String prePath = "OpData.";
        // 查询实体
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        // 实际调用的服务
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", treeType);
        //过滤的区域 字段名
        String rhtRegId = request.getParameter("rhtRegId");
        if (StringUtil.isNotEmptyOrNull(rhtRegId)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", regId);
            opData.setStrValue(prePath + "RegFilter.regUseType", treeType);
        }
        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", "10000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        // 排序
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "hsJush");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        opData.setStrValue(prePath + "SortFields.SortField[1].fieldName", "hsHxName");
        opData.setStrValue(prePath + "SortFields.SortField[1].sortOrder", "asc");
        // 户型名称
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "hsJush");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "ttRegId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "hsHxName");
        svcRequest.addOp("PRIVI_FILTER", opData);

        // 调用服务获取数据
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        List<JSONObject> list = new LinkedList<JSONObject>();
        if (result) {
            // 解析区域目录树
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_REG_TREE").getBeanByPath("Operation.OpResult.PrjReg");
            // 区域名称映射关系, key: 区域ID、value: 区域Bean
            Map<String, XmlBean> hsAreaMap = new LinkedHashMap<String, XmlBean>();
            Map<String, XmlBean> foundHsAreaMap = new LinkedHashMap<String, XmlBean>();
            if (treeBean != null) {
                int count = treeBean.getListNum("PrjReg.Node");
                // 获取根节点路径
                String rootId = null;
                for (int i = 0; i < count; i++) {
                    String id = treeBean.getStrValue("""PrjReg.Node[${i}].regId""");
                    String pId = treeBean.getStrValue("""PrjReg.Node[${i}].upRegId""");
                    if ("0".equals(pId)) {
                        rootId = id;
                        break;
                    }
                }
                // 获取直接挂到根节点下的节点
                if (StringUtil.isNotEmptyOrNull(rootId)) {
                    for (int i = 0; i < count; i++) {
                        XmlBean tempBean = treeBean.getBeanByPath("PrjReg.Node[${i}]");
                        String id = tempBean.getStrValue("Node.regId");
                        String pId = tempBean.getStrValue("Node.upRegId");
                        String name = tempBean.getStrValue("Node.regName");
                        String useType = tempBean.getStrValue("Node.regEntityType");
                        // 保存区域节点
                        hsAreaMap.put(id, tempBean);
                        // 输出区域目录树
                        boolean haveChecked = checkedOrg.contains(id);
                        boolean open = true;
                        if (!"0".equals(pId) && !(pId.equals(rootId))) {
                            continue;
                        }
                        String iconSkin = "folder";
                        JSONObject objContact = new JSONObject();
                        objContact.put("id", id)
                        objContact.put("iconSkin", iconSkin)
                        objContact.put("pId", pId)
                        objContact.put("useType", useType)
                        objContact.put("checked", haveChecked)
                        objContact.put("name", name)
                        objContact.put("open", open)
                        list.add(objContact)
                    }
                }
            }
            // 解析居室、户型结构树
            XmlBean houseBean = svcResponse.getFirstOpRsp("PRIVI_FILTER")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (houseBean != null) {
                // 获取居室码表
                Map<String, String> tpMap = getCfgCollection(request, "NEW_HS_JUSH", true, svcRequest.getPrjCd())
                int rowCount = houseBean.getListNum("PageData.Row");
                // key：区域ID， value：户型名称
                Map<String, Set<String>> addTpMap = new LinkedHashMap<String, Set<String>>();
                // key：区域ID-hsJush， value：户型名称
                Map<String, String> addHxMap = new LinkedHashMap<String, String>();
                for (int i = 0; i < rowCount; i++) {
                    XmlBean row = houseBean.getBeanByPath("PageData.Row[${i}]");
                    String ttRegId = row.getStrValue("Row.ttRegId");
                    String hsTp = row.getStrValue("Row.hsJush");
                    String hsHxName = row.getStrValue("Row.hsHxName");
                    // 对应的房源区域
                    XmlBean hsAreaBean = getHsAreaBean(ttRegId, hsAreaMap, foundHsAreaMap);
                    if (hsAreaBean != null) {
                        String areaId = hsAreaBean.getStrValue("Node.regId");
                        // 居室结构存在
                        Set<String> hsTpSet = addTpMap.get(areaId);
                        if (hsTpSet == null) {
                            hsTpSet = new LinkedHashSet<String>();
                            addTpMap.put(areaId, hsTpSet);
                        }
                        if (!hsTpSet.contains(hsTp)) {
                            // 不存在居室增加居室
                            hsTpSet.add(hsTp);
                            // 输出居室目录结构
                            String name = tpMap.get(hsTp);
                            // 户型存在
                            JSONObject objContact = new JSONObject();
                            objContact.put("id", "tp-${areaId}-${hsTp}")
                            objContact.put("iconSkin", "folder")
                            objContact.put("pId", areaId)
                            objContact.put("areaId", areaId)
                            objContact.put("open", true)
                            objContact.put("useType", "hsTp")
                            objContact.put("hsTp", "${hsTp}")
                            objContact.put("name", name)
                            list.add(objContact)
                        }
                        // 处理户型输出
                        // 居室结构存在
                        String hxKey = areaId + "-" + hsTp;

                        Set<String> hsHxSet = addHxMap.get(hxKey);
                        if (hsHxSet == null) {
                            hsHxSet = new TreeSet<String>(new Comparator<String>() {
                                @Override
                                int compare(String o1, String o2) {
                                    char[] o1Arr = o1.toCharArray();
                                    char[] o2Arr = o2.toCharArray();
                                    int minLength = o1Arr.length < o2.length() ? o1Arr.length : o2.length();
                                    int sameIdex = -1;
                                    for (int k = 0; k < minLength; k++) {
                                        if (o1Arr[k] == o2Arr[k]) {
                                            sameIdex = k;
                                            break;
                                        }
                                    }
                                    if (sameIdex == -1) {
                                        return o1.compareTo(o2);
                                    } else if (o1.length() > o2.length()) {
                                        return 1;
                                    } else if (o1.length() < o2.length()) {
                                        return -1;
                                    } else {
                                        return o1.compareTo(o2);
                                    }
                                }
                            });
                            addHxMap.put(hxKey, hsHxSet);
                        }
                        if (!hsHxSet.contains(hsHxName)) {
                            // 不存在居室增加居室
                            hsHxSet.add(hsHxName);
                        }
                    }
                }
                // 添加所有的户型
                for (Map.Entry<String, Set<String>> tempEnty : addHxMap) {
                    String[] keyInfo = tempEnty.key.split("-");
                    String areaId = "";
                    if (keyInfo.length > 0) {
                        areaId = keyInfo[0];
                    }
                    String hsTp = "";
                    if (keyInfo.length > 1) {
                        hsTp = keyInfo[1];
                    }
                    for (String hsHxName : tempEnty.value) {
                        JSONObject objContact = new JSONObject();
                        objContact.put("pId", "tp-${areaId}-${hsTp}")
                        objContact.put("useType", "hsHx")
                        objContact.put("areaId", areaId)
                        objContact.put("hsTp", "${hsTp}")
                        objContact.put("name", "${hsHxName}")
                        list.add(objContact)
                    }
                }
            }
        }
        JSONObject resultMap = new JSONObject();
        resultMap.put("treeJson", list)
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("success", "${result}");
        responseJSON.put("errMsg", "${errMsg}");
        responseJSON.put("resultMap", resultMap);
        ResponseUtil.printAjax(response, responseJSON);
    }

    /**
     * 保存区域权限
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView saveReg(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String regId = request.getParameter("regId");
        String upRegId = request.getParameter("upRegId");
        String regName = request.getParameter("regName");
        String prjOrgId = request.getParameter("prjOrgId");
        String regDesc = request.getParameter("regDesc");
        String cfmUpdateOrg = request.getParameter("cfmUpdateOrg");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String allResult;
        String resultMsg;

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "RegInfo");
        opData.setStrValue("OpData.EntityData.regId", regId);
        opData.setStrValue("OpData.EntityData.regName", regName);
        opData.setStrValue("OpData.EntityData.regDesc", regDesc);
        opData.setStrValue("OpData.EntityData.prjOrgId", prjOrgId);
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);

        if (svcResponse.isSuccess()) {
            allResult = true;
            if (StringUtil.isEqual("true", cfmUpdateOrg)) {
                allResult = false;
                svcRequest = RequestUtil.getSvcRequest(request);
                opData = new XmlBean();
                opData.setStrValue("OpData.PrjRegInfo.prjRegId", regId);
                opData.setStrValue("OpData.PrjRegInfo.prjOrgId", prjOrgId);
                opData.setStrValue("OpData.PrjRegInfo.regUseType", request.getParameter("regUseType"));
                svcRequest.addOp("UPDATE_CASD_REG_INFO", opData);
                SvcResponse updateOrgRsp = transaction(svcRequest);
                if (updateOrgRsp.isSuccess()) {
                    allResult = true;
                } else {
                    resultMsg = updateOrgRsp.getErrMsg();
                }
            }
        } else {
            resultMsg = svcResponse.getErrMsg();
        }

        String data = """{"regId" : "${regId}", "upRegId": "${upRegId}", "regName": "${regName}"}""";
        String jsonStr = """ {success: ${allResult}, errMsg: "${resultMsg}", data: ${data} }""";
        // 输出处理结果
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 区域详细信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initS(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        String regId = request.getParameter("regId");
        modelMap.put("sltRegId", regId);
        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 请求参数
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";

        // 查询实体
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        // 实际调用的服务
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        opData.setStrValue(prePath + "queryType", "2");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "NewHsInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

        //过滤的区域 字段名
        if (StringUtil.isNotEmptyOrNull(regId)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", regId);
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        }

        // 其他查询条件
        String conditionNames = requestUtil.getStrParam("conditionNames");
        String conditions = requestUtil.getStrParam("conditions");
        String conditionValues = requestUtil.getStrParam("conditionValues");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
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
        opData.setStrValue(prePath + "PageInfo.pageSize", "10000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        //排序
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "NewHsInfo.hsJush");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "NewHsInfo.hsJush");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "NewHsInfo.hsHxName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "NewHsInfo.statusCd");
        // 调用服务
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 查询结果处理
            XmlBean queryResult = svcResponse.getFirstOpRsp("PRIVI_FILTER")
                    .getBeanByPath("Operation.OpResult");
            if (queryResult == null) {
                queryResult = new XmlBean();
            }
            // 居室值列表
            Map<String, Set> hsTpHxNames = new LinkedHashMap<String, Set>();
            // key： 居室-户型名称-状态： value： 值
            Map<String, Integer> hsTpValue = new LinkedHashMap<String, Integer>();
            // 居室-状态 汇总信息
            Map<String, Integer> hsTpSummary = new LinkedHashMap<String, Integer>();
            // 记录户型最多
            int rowCount = queryResult.getListNum("OpResult.PageData.Row");
            for (int i = 0; i < rowCount; i++) {
                XmlBean tempRow = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                String nowHsTp = tempRow.getStrValue("Row.NewHsInfo.hsTp");
                String nowHsHxName = tempRow.getStrValue("Row.NewHsInfo.hsHxName");
                String hsCd = tempRow.getStrValue("Row.NewHsInfo.statusCd");
                // 当前居室数据的户型列表
                Set<String> hsTpData = hsTpHxNames.get(nowHsTp);
                if (hsTpData == null) {
                    hsTpData = new LinkedHashSet<String>();
                    hsTpHxNames.put(nowHsTp, hsTpData);
                }
                if (!hsTpData.contains(nowHsHxName)) {
                    hsTpData.add(nowHsHxName);
                }
                //  整理居室户型对应的数量
                String dKey = nowHsTp + "-" + nowHsHxName + "-" + hsCd;
                Integer countValue = hsTpValue.get(dKey);
                if (countValue == null) {
                    countValue = 0;
                }
                hsTpValue.put(dKey, ++countValue);

                // 户型汇总数据保存
                String key = nowHsTp + "-" + hsCd;
                Integer count = hsTpSummary.get(key);
                if (count == null) {
                    count = 0;
                }
                count = count + 1;
                hsTpSummary.put(key, count);
            }
            // 计算总数
            int maxTpCount = 1;
            for (Set temp : hsTpHxNames.values()) {
                if (temp.size() > maxTpCount) {
                    maxTpCount = temp.size();
                }
            }
            //房源状态
            Map newHsStatus = getCfgCollection(request, "NEW_HS_STATUS", true);
            modelMap.put("newHsStatus", newHsStatus);
            modelMap.put("hsTpHxNames", hsTpHxNames);
            modelMap.put("hsTpValue", hsTpValue);
            modelMap.put("hsTpSummary", hsTpSummary);
            modelMap.put("maxTpCount", maxTpCount);
            modelMap.put("hsTpCount", hsTpHxNames.size());
        }
        // 查询参数配置
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", "HR_CP");
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", true);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", svcRequest.getPrjCd());
        svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        List<JSONObject> list = new LinkedList<JSONObject>();
        if (svcResponse.isSuccess()) {
            int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            for (int i = 0; i < count; ++i) {
                JSONObject jsonObject = JSONObject.fromObject(svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].notes"));
                list.add(jsonObject);
            }
        }
        modelMap.put("list", list);
        return new ModelAndView("/eland/oh/oh001/oh00101_sum", modelMap);
    }

    /**
     * 房源详细信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String newHsId = request.getParameter("newHsId");
        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "NewHsInfo");
        opData.setStrValue("OpData.entityKey", newHsId);
        // 调用服务查询数据
        svcRequest.addOp("QUERY_ALL_ENTITY", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean newHsInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                    .getBeanByPath("Operation.OpResult.NewHsInfo");
            modelMap.put("newHs", newHsInfo.getRootNode());
            modelMap.put("prjCd", request.getParameter("prjCd"));
        }
        return new ModelAndView("/eland/oh/oh001/oh00101_info", modelMap);
    }

    /**
     * 修改房源信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String newHsId = request.getParameter("newHsId");
        if (StringUtil.isEmptyOrNull(newHsId)) {
            newHsId = "\${1}";
        }
        opData.setStrValue("NewHsInfo.newHsId", newHsId);
        opData.setStrValue("NewHsInfo.ttRegId", request.getParameter("ttRegId"));
        opData.setStrValue("NewHsInfo.ttOrgId", request.getParameter("ttOrgId"));
        opData.setStrValue("NewHsInfo.hsHxId", request.getParameter("hsHxId"));
        opData.setStrValue("NewHsInfo.hsHxName", request.getParameter("hsHxName"));
        opData.setStrValue("NewHsInfo.hsTp", request.getParameter("hsTp"));
        opData.setStrValue("NewHsInfo.hsDt", request.getParameter("hsDt"));
        opData.setStrValue("NewHsInfo.hsUn", request.getParameter("hsUn"));
        opData.setStrValue("NewHsInfo.hsNm", request.getParameter("hsNm"));
        opData.setStrValue("NewHsInfo.hsAddr", request.getParameter("hsAddr"));
        opData.setStrValue("NewHsInfo.preBldSize", request.getParameter("preBldSize"));
        opData.setStrValue("NewHsInfo.hsBldSize", request.getParameter("hsBldSize"));
        opData.setStrValue("NewHsInfo.hsUseSize", request.getParameter("hsUseSize"));
        opData.setStrValue("NewHsInfo.hsUnPrice", request.getParameter("hsUnPrice"));
        opData.setStrValue("NewHsInfo.hsNode", request.getParameter("hsNode"));
        opData.setStrValue("NewHsInfo.statusCd", request.getParameter("statusCd"));
        opData.setStrValue("NewHsInfo.wyUnPrice", request.getParameter("wyUnPrice"));
        opData.setStrValue("NewHsInfo.gnUnPrice", request.getParameter("gnUnPrice"));
        opData.setStrValue("NewHsInfo.aplyHsDate", request.getParameter("aplyHsDate"));
        opData.setStrValue("NewHsInfo.handHsDate", request.getParameter("handHsDate"));
        opData.setStrValue("NewHsInfo.buyHsCtDate", request.getParameter("buyHsCtDate"));
        opData.setStrValue("NewHsInfo.rzTime", request.getParameter("rzTime"));
        opData.setStrValue("NewHsInfo.cmpWyCost", request.getParameter("cmpWyCost"));
        opData.setStrValue("NewHsInfo.cmpQnCost", request.getParameter("cmpQnCost"));

        // 调用服务
        // 增加新房源数据
        XmlBean reqData = new XmlBean();
        reqData.setBeanByPath("OpData.NewHsInfos", opData);
        svcRequest.addOp("SAVE_NEW_HS_INFO", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 删除房源数据
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) {
        String newHsId = request.getParameter("newHsId");
        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "NewHsInfo");
        opData.setStrValue("OpData.entityKey", newHsId);
        // 调用服务查询数据
        svcRequest.addOp("DELETE_NEW_HS", opData);
        SvcResponse svcResponse = query(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");

    }

    /**
     * 新增区域初始化
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView addReg(HttpServletRequest request, HttpServletResponse response) {
        String upRegId = request.getParameter("upRegId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        /* 输入参数组织 */
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", upRegId);
        reqData.setStrValue(rootNodePath + "prjCd", request.getParameter("prjCd"));
        reqData.setStrValue(rootNodePath + "regUseType", request.getParameter("regUseType"));
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
        // 获取输出结果
        XmlBean prjRegNode = reqData.getBeanByPath("SvcCont.PrjReg");
        if (svcResponse.isSuccess()) {
            prjRegNode = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
        }
        rootNodePath = "PrjReg.";
        prjRegNode.setStrValue(rootNodePath + "regId", "");
        prjRegNode.setStrValue(rootNodePath + "regName", "");
        prjRegNode.setStrValue(rootNodePath + "regDesc", "");
        prjRegNode.setStrValue(rootNodePath + "upRegId", upRegId);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        modelMap.put("nodeInfo", prjRegNode.getRootNode());
        String regPath = prjRegNode.getStrValue(rootNodePath + "regPath");
        String regUseType = prjRegNode.getStrValue(rootNodePath + "regUseType");
        if (regPath.count("/") == 1 && "3".equals(regUseType)) {
            return new ModelAndView("/eland/oh/oh001/oh00104", modelMap);
        } else {
            return new ModelAndView("/eland/oh/oh001/oh00103", modelMap);
        }
    }

    /**
     * 新增区域初始化
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView deleteRegion(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        //  查看区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String rootNodePath = "OpData.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "prjRegId", requestUtil.getStrParam("regId"));
        reqData.setStrValue(rootNodePath + "regUseType", request.getParameter("regUseType"));
        // 调用服务
        svcRequest.addOp("DELETE_EMG_REG", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 输出处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void moveRegion(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String moveType = request.getParameter("moveType");
        if ("prev".equals(moveType)) {
            moveType = "2"
        } else if ("next".equals(moveType)) {
            moveType = "3"
        } else {
            moveType = "1"
        }
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.prjCd", request.getParameter("prjCd"));
        reqData.setStrValue("SvcCont.regUseType", request.getParameter("regUseType"));
        reqData.setStrValue("SvcCont.moveId", request.getParameter("mNodeId"));
        reqData.setStrValue("SvcCont.moveToId", request.getParameter("tNodeId"));
        reqData.setStrValue("SvcCont.moveType", moveType);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "saveMoveTree", svcRequest);
        // 输出处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView editReg(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        //  查看区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        // 区域编码
        reqData.setStrValue(rootNodePath + "regId", requestUtil.getStrParam("regId"));
        //  项目编码，必须
        reqData.setStrValue(rootNodePath + "prjCd", requestUtil.getStrParam("prjCd"));
        // 区域类型
        reqData.setStrValue(rootNodePath + "regUseType", requestUtil.getStrParam("regUseType"));
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
        // 获取输出结果
        if (svcResponse.isSuccess()) {
            ModelMap modelMap = new ModelMap();
            XmlBean prjRegNode = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
            modelMap.put("nodeInfo", prjRegNode.getRootNode());
            String regPath = prjRegNode.getStrValue("PrjReg.regPath");
            // 外迁房源的二级区域代表房产类型
            if (regPath.count("/") == 2 && "3".equals(requestUtil.getStrParam("regUseType"))) {
                // 获取区域的房源使用统计信息
                String prePath = "OpData.";
                XmlBean opData = new XmlBean();
                opData.setStrValue(prePath + "opName", "QUERY_ATTR_COUNT_CMPT");
                opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
                opData.setStrValue(prePath + "RegFilter.attrValue", requestUtil.getStrParam("regId"));
                opData.setStrValue(prePath + "RegFilter.regUseType", requestUtil.getStrParam("regUseType"));
                opData.setStrValue(prePath + "RegFilter.setPath", "OpData.Conditions");

                opData.setStrValue(prePath + "OrgFilter.attrName", "NewHsInfo.ttOrgId");
                opData.setStrValue(prePath + "OrgFilter.attrValue", "");
                opData.setStrValue(prePath + "OrgFilter.rhtType", "1");
                opData.setStrValue(prePath + "OrgFilter.setPath", "OpData.Conditions");
                // 查询维度
                opData.setStrValue(prePath + "XAttrs.attrName[0]", "NewHsInfo.hsTp");
                // 统计指标
                opData.setStrValue(prePath + "ResultAttrs.AttrInfo[0].type", "2");
                opData.setStrValue(prePath + "ResultAttrs.AttrInfo[0].attrName", "NewHsInfo.newHsId");

                // 查询条件
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "NewHsInfo.statusCd");
                opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "3");
                // 执行组件查询
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("PRIVI_FILTER", opData);
                svcResponse = query(svcRequest);
                if (svcResponse.isSuccess()) {
                    // 服务调用成功
                    XmlBean opResult = svcResponse.getFirstOpRsp("PRIVI_FILTER").getBeanByPath("Operation.OpResult");
                    if (opResult != null) {
                        int rowCount = opResult.getListNum("OpResult.TableData.Row");
                        for (int i = 0; i < rowCount; i++) {
                            XmlBean temp = opResult.getBeanByPath("OpResult.TableData.Row[${i}]");
                            modelMap.put("ht_" + temp.getStrValue("Row.f1"), temp.getStrValue("Row.f0"));
                        }
                    }

                }
                return new ModelAndView("/eland/oh/oh001/oh00104", modelMap);
            } else {
                return new ModelAndView("/eland/oh/oh001/oh00103", modelMap);
            }
        }
    }

    /**
     * 房源数据查询
     * @param request 查询条件
     * @param response 查询结果
     * @return 返回值
     */
    public ModelAndView queryHs(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        String[] conditionFieldArr = new String[0];
        String[] conditionArr = new String[0];
        String[] conditionValueArr = new String[0];
        for (int i = 0; i < conditionFieldArr.length; i++) {
            opData.setStrValue(prePath + "Conditions.Condition[${i}].fieldName", conditionFieldArr[i]);
            opData.setStrValue(prePath + "Conditions.Condition[${i}].fieldValue", conditionArr[i]);
            opData.setStrValue(prePath + "Conditions.Condition[${i}].operation", conditionValueArr[i]);
        }
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        opData.setStrValue(prePath + "ttRegId", requestUtil.getStrParam("regId"));
        opData.setStrValue(prePath + "regUseType", request.getParameter("regUseType"));

        // 需要查询的字段
        String[] resultFieldArr = ["newHsId", "ttRegId", "hsHxName", "hsTp", "hsDt", "hsAddr",
                                   "hsBldSize", "hsUseSize", "hsUnPrice", "hsSalePrice", "statusCd"];
        for (int i = 0; i < resultFieldArr.length; i++) {
            opData.setStrValue(prePath + "ResultFields.fieldName[${i}]", resultFieldArr[i]);
        }
        // 增加查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("FILTER_QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("FILTER_QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<XmlNode> resultList = new ArrayList<XmlNode>();
                for (int i = 0; i < rowCount; i++) {
                    resultList.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode())
                }
                modelMap.put("returnList", resultList);
                // 返回结果集
                Map<String, Object> pagination = new HashMap<String, Object>();
                // 返回分页信息
                pagination.put("currentPage", queryResult.getValue("OpResult.PageInfo.currentPage"));
                pagination.put("totalPage", queryResult.getValue("OpResult.PageInfo.totalPage"));
                pagination.put("totalRecord", queryResult.getValue("OpResult.PageInfo.totalRecord"));
                // 分页信息
                modelMap.addAttribute("pagination", pagination);
            }
            // 排序字段
            modelMap.addAttribute("sortColumn", requestUtil.getStrParam("sortColumn"));
            // 排序方向
            modelMap.addAttribute("sortOrder", requestUtil.getStrParam("sortOrder"));
            // 每页显示页数
            modelMap.addAttribute("pageSize", requestUtil.getStrParam("pageSize"));
        }
        String forward = "/eland/oh/oh001/oh00101_list";
        return new ModelAndView(forward, modelMap);
    }

    /**
     * 打开房源导入
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView openImport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("regUseType", request.getParameter("regUseType"));
        return new ModelAndView("/eland/oh/oh001/oh00101_import", modelMap);
    }

    /**
     * 下载导入模板
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public void downTemplate(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        String templateFile = new File(StringUtil.formatFilePath("webroot:/eland/oh/oh001/importTemplate.xlsx"));
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, "", "房源导入模板.xlsx", new File(templateFile), agent);
    }

    /**
     * 打开房源导入
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView importHs(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil((request));
        MultipartFile localFile = super.getFile(request, "oHouseImportFiled");
        ExcelReader excelReader = null;
        try {
            excelReader = new ExcelReader(localFile.getInputStream(), localFile.getOriginalFilename());
        } catch (SystemException exception) {
            ResponseUtil.print(response, """{"success": false, "errMsg": "数据解析错误，请确认使用了正确的模板!错误详情：${
                exception.getMessage()
            }"}""");
            return;
        }
        // 模板正确
        int rowCount = excelReader.getRowCnt();
        if (rowCount < 3) {
            ResponseUtil.print(response, """{"success": false, "errMsg": "请使用模板导入数据"}""");
            return;
        }
        // 户型结构
        Map<String, String> hsTpMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> temp : getCfgCollection(request, "HS_ROOM_TYPE", true, svcRequest.getPrjCd())) {
            hsTpMap.put(temp.value, temp.key);
        }
        // 房源状态
        Map<String, String> hsStatusMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> temp : getCfgCollection(request, "NEW_HS_STATUS", true, svcRequest.getPrjCd())) {
            hsStatusMap.put(temp.value, temp.key);
        }

        // 房屋朝向
        Map<String, String> hsDtMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> temp : getCfgCollection(request, "HS_ROOM_DT", true, svcRequest.getPrjCd())) {
            hsDtMap.put(temp.value, temp.key);
        }

        // 居室类型
        Map<String, String> hsJushMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> temp : getCfgCollection(request, "NEW_HS_JUSH", true, svcRequest.getPrjCd())) {
            hsJushMap.put(temp.value, temp.key);
        }

        // 模板标题
        Map<String, String> titleField = ["序号"  : "", "系统编号": "newHsId", "完整地址": "hsAddr",
                                          "楼房区域": "hsSt", "地块": "hsFl", "楼号": "hsNo",
                                          "单元"  : "hsUn", "房号": "hsNm", "预测建面": "preBldSize",
                                          "实测建面": "hsBldSize", "套内面积": "hsUseSize",
                                          "建面单价": "hsUnPrice", "房屋总价": "hsSalePrice",
                                          "户型"  : "hsHxName", "户型结构": "hsTp", "朝向": "hsDt",
                                          "调拨时间": "aplyHsDate", "交房时间": "handHsDate",
                                          "物业单价": "wyUnPrice", "取暖单价": "gnUnPrice", "房源状态": "statusCd", "居室类型": "hsJush"];

        // 读取解析Excel文件中的内容
        String[] firstTitle = excelReader.readData(1);
        // 读取第二行标题
        String[] secondTitle = excelReader.readData(2);

        // 标对应的英文名称
        String[] reqIdxName = new String[firstTitle.length];

        String firstValue = firstTitle[0];
        // 抬头合并单元格的内容统一为相同值
        for (int i = 0; i < firstTitle.length; i++) {
            String titleValue = firstTitle[i];
            if (StringUtil.isEmptyOrNull(titleValue)) {
                titleValue = firstValue;
                firstTitle[i] = titleValue;
            }
            if (StringUtil.isEmptyOrNull(secondTitle[i])) {
                secondTitle[i] = titleValue;
            }
            String dbFileName = secondTitle[i];
            String filedName = titleField.get(secondTitle[i]);
            if (filedName == null) {
                // 不满足模板需求
                ResponseUtil.print(response, """{"success": false, "errMsg": "第${i + 1}列[${
                    dbFileName
                }]在模板中未定义，请使用模板填写导入数据"}""");
                return;
            } else {
                reqIdxName[i] = filedName;
            }
            firstValue = titleValue;
        }

        //组装数据导入报文
        XmlBean reqData = new XmlBean();
        for (int i = 3; i <= rowCount; i++) {
            String[] rowData = excelReader.readData(i);
            // 判断是否所有数据都为空，为空跳过
            boolean hasData = false;
            XmlBean rowBean = new XmlBean();
            int regNameCount = 0;
            String tempHsAddr = "";
            for (int j = 0; j < reqIdxName.length; j++) {
                String fieldName = reqIdxName[j];
                String firstTitleName = firstTitle[j];
                String fieldValue = rowData[j];
                if (StringUtil.isEmptyOrNull(fieldValue)) {
                    rowBean.setStrValue("NewHsInfo." + reqIdxName[j], fieldValue);
                    continue;
                } else {
                    hasData = true;
                }
                // 户型结构
                if ("hsTp".equals(fieldName)) {
                    String valueCd = hsTpMap.get(fieldValue);
                    if (valueCd == null) {
                        // 不满足模板需求
                        ResponseUtil.print(response,
                                """{"success": false, "errMsg": "第${i + 1}行[${firstTitle[j]}]列[${
                                    fieldValue
                                }]在系统中未配置，正确的值可以为${hsTpMap.keySet()}"}""");
                        return;
                    } else {
                        fieldValue = valueCd;
                    }
                }
                // 房源状态
                if ("statusCd".equals(fieldName)) {
                    String valueCd = hsStatusMap.get(fieldValue);
                    if (valueCd == null) {
                        // 不满足模板需求
                        ResponseUtil.print(response,
                                """{"success": false, "errMsg": "第${i + 1}行[${firstTitle[j]}]列[${
                                    fieldValue
                                }]在系统中未配置，正确的值可以为${hsStatusMap.keySet()}"}""");
                        return;
                    } else {
                        fieldValue = valueCd;
                    }
                }
                // 朝向
                if ("hsDt".equals(fieldName)) {
                    String valueCd = hsDtMap.get(fieldValue);
                    if (valueCd == null) {
                        // 不满足模板需求
                        ResponseUtil.print(response,
                                """{"success": false, "errMsg": "第${i + 1}行[${firstTitle[j]}]列[${
                                    fieldValue
                                }]在系统中未配置，正确的值可以为${hsDtMap.keySet()}"}""");
                        return;
                    } else {
                        fieldValue = valueCd;
                    }
                }
                // 朝向
                if ("hsJush".equals(fieldName)) {
                    String valueCd = hsJushMap.get(fieldValue);
                    if (valueCd == null) {
                        // 不满足模板需求
                        ResponseUtil.print(response,
                                """{"success": false, "errMsg": "第${i + 1}行[${firstTitle[j]}]列[${
                                    fieldValue
                                }]在系统中未配置，正确的值可以为${hsJushMap.keySet()}"}""");
                        return;
                    } else {
                        fieldValue = valueCd;
                    }
                }
                if ("楼房位置".equals(firstTitleName)) {
                    String areaName = secondTitle[j];
                    if ("楼房区域".equals(areaName)) {
                        areaName = "";
                    } else {
                        tempHsAddr = tempHsAddr + fieldValue + "-";
                        if ("楼号".equals(areaName)) {
                            areaName = "号楼";
                        }
                    }
                    rowBean.setStrValue("NewHsInfo.RegNames.regName[${regNameCount++}]",
                            fieldValue + areaName);
                }
                rowBean.setStrValue("NewHsInfo." + reqIdxName[j], fieldValue);
            }
            if (!hasData) {
                continue;
            }
            // 房屋详细地址
            String hsNm = rowBean.getStrValue("NewHsInfo.hsNm");
            if (hsNm.length() < 2) {
                // 不满足模板需求
                ResponseUtil.print(response,
                        """{"success": false, "errMsg": "第${i + 1}行的房号长度小于2位,无法获取楼层信息"}""");
                return;
            }
//            String flNm = hsNm.substring(0, hsNm.length() - 2);
            // 房屋地址(地块-单元-楼层-门牌)
            String hsAddr = rowBean.getStrValue("NewHsInfo.hsAddr");
            if (StringUtil.isEmptyOrNull(hsAddr)) {
                hsAddr = tempHsAddr + rowBean.getStrValue("NewHsInfo.hsUn") /*+ "-" + flNm */ + "-" + hsNm;
            }
            rowBean.setStrValue("NewHsInfo.hsAddr", hsAddr);
            String newHsId = rowBean.getStrValue("NewHsInfo.newHsId");
            if (StringUtil.isEmptyOrNull(newHsId)) {
                rowBean.setStrValue("NewHsInfo.newHsId", "\${${i}}");
            }
            rowBean.setStrValue("NewHsInfo.hsClass", requestUtil.getStrParam("regUseType"));
            // 增加新房源数据
            reqData.setBeanByPath("OpData.NewHsInfos", rowBean);
            reqData.setStrValue("OpData.NewHsInfos.regUseType", requestUtil.getStrParam("regUseType")); ;
        }
        // 调用服务
        svcRequest.addOp("SAVE_NEW_HS_INFO", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
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
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String newHsIds = requestUtil.getStrParam("newHsIds");
        String prjCd = requestUtil.getStrParam("prjCd");

        // 调用服务获取后台数据
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", "BAT_EDIT_NHS_DEF");
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
        opData.setStrValue("OpData.entityName", "NewHsInfo");
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
                    queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp + "].attrNameEn", item.keySet().toArray()[p]);
                    queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp + "].attrNameCh", item.get(item.keySet().toArray()[p]));
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
                                        queryResult.setStrValue("Operation.OpResult.EntityAttr[" + temp++ + "].attrRelItemCd", attrFrontSltRule.substring(6, attrFrontSltRule.length() - 2));
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
        modelMap.put("newHsIds", newHsIds);
        return new ModelAndView("/eland/oh/oh001/oh00101_bedit", modelMap);
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public ModelAndView bEdit(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 房源ID列表
        String[] newHsIdArr = requestUtil.getStrParam("newHsIds").split(",");
        String[] fieldNameArr = requestUtil.getStrParam("fieldNames").split(",");
        String[] fieldValues = requestUtil.getStrParam("fieldValues").split(",");
        XmlBean opData = new XmlBean();
        for (String newHsId : newHsIdArr) {
            if (StringUtil.isEmptyOrNull(newHsId)) {
                continue;
            }
            XmlBean rowData = new XmlBean();
            rowData.setStrValue("NewHsInfo.newHsId", newHsId);
            for (int i = 0; i < fieldNameArr.length; i++) {
                rowData.setStrValue("NewHsInfo." + fieldNameArr[i], fieldValues[i]);
            }
            opData.setBeanByPath("OpData.NewHsInfos", rowData);
        }
        // 调用服务
        svcRequest.addOp("SAVE_NEW_HS_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
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

    /**
     * 获取区域节点对应房源区域ID(房源区域：第二级节点)
     * @param regId
     * @param hsAreaMap
     * @return
     */
    private XmlBean getHsAreaBean(String regId, Map<String, XmlBean> hsAreaMap, Map<String, XmlBean> fountHsAreaMap) {
        XmlBean result = fountHsAreaMap.get(regId);
        if (result != null) {

            return result;
        }
        // 当前Bean对象
        XmlBean currentBean = hsAreaMap.get(regId);
        if (currentBean != null) {
            String cPId = currentBean.getStrValue("Node.upRegId");
            XmlBean currentPBean = hsAreaMap.get(cPId);
            if (currentPBean != null) {
                String pPId = currentPBean.getStrValue("Node.upRegId");
                // 当前节点的上级节点的PId等于0的时候返回
                if ("0".equals(pPId)) {
                    fountHsAreaMap.put(regId, currentBean);
                    return currentBean;
                } else {
                    return getHsAreaBean(cPId, hsAreaMap, fountHsAreaMap);
                }
            }
        }
        return null;
    }

}
