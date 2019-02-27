import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.web.controller.GroovyController
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/7/22 0022 15:50
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
class ph003_task extends GroovyController {

    /**
     * 发起修改申请  前处理逻辑
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initSendTask(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", request.getParameter("hsId"));
//        modelMap.put("prjCd", request.getParameter("prjCd"));
        return new ModelAndView("/eland/ph/ph003/ph003_taskAssignee", modelMap);
    }

    /**
     * 启动流程节点 展示房产信息
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initTask(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String buildId = request.getParameter("buildId");
        String busiKey = request.getParameter("busiKey");
        String hsId = busiKey.replace("HouseInfo_", "");
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = request.getParameter("hsId");
            if (StringUtil.isEmptyOrNull(hsId)) {
                XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
                XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
                XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
                hsId = ProcBidVars.get("hsId").toString();
            }
        }
        ModelMap modelMap = new ModelMap();
        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("hsId", hsId);
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph003/ph003_taskInit", modelMap);
    }

    /**
     * 启动流程节点 展示房产信息
     * @param request
     * @param response
     * @return
     */
    public ModelAndView initTaskView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String buildId = request.getParameter("buildId");
        String hsId = request.getParameter("hsId");

        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = ProcBidVars.get("hsId").toString();
        }

        XmlBean alterBean = null;
        if (ProcInsInfo != null) {
            XmlNode Variables = (XmlNode) ProcInsInfo.get("Variables");
            if (Variables.get("alterRecord") != null) {
                byte[] alterRecord = (byte[]) (((XmlNode) Variables.get("alterRecord")).getNodeValue());
                String alterStr = new String(alterRecord, "utf-8");
                alterBean = new XmlBean(alterStr);
                // 伪属性 处理   伪属性翻译 map
                List chEntityList = new ArrayList();
                int chEntityNum = alterBean.getListNum("Operation.ChangedEntities.ChangedEntity");
                for (int j = 0; j < chEntityNum; j++) {//operation 下有多少 chEntitys
                    XmlBean chEntity = alterBean.getBeanByPath("Operation.ChangedEntities.ChangedEntity[${j}]");
                    List chAttrList = new ArrayList();
                    List pseudoList = new ArrayList();
                    int chAttrNum = chEntity.getListNum("ChangedEntity.ChangedAttrs.EntityAttr");
                    for (int k = 0; k < chAttrNum; k++) {     // chEntitys 下有多少 chAttrs
                        XmlBean chAttr = chEntity.getBeanByPath("ChangedEntity.ChangedAttrs.EntityAttr[${k}]");
                        // 伪属性 处理   是伪属性
                        String isPseudoFlag = chAttr.getStrValue("EntityAttr.isPseudoFlag");
                        if (StringUtil.isEqual("1", isPseudoFlag)) {
                            Map pseMap = new HashMap();
                            Map thMap = new HashMap();
                            String attrNames = chAttr.getStrValue("EntityAttr.attrNames");
                            XmlBean attrNamesBean = new XmlBean(attrNames);
                            //伪属性的 所有字段集合
                            List attrList = attrNamesBean.getNodeNames("attrName");
                            for (int i = 0; i < attrList.size(); i++) {
                                String attrNamesVal = attrList.get(i);
                                pseMap.put(attrNamesVal, attrNamesBean.getStrValue("attrName.${attrNamesVal}"));
                            }
                            thMap.put("thMap", pseMap);
                            //旧的
                            String attrValueBef_name = chAttr.getStrValue("EntityAttr.attrValueBef_name");
                            if (StringUtil.isEmptyOrNull(attrValueBef_name)) {
                                continue;
                            }
                            List subBefKVMapList = beanPseudo(new XmlBean(attrValueBef_name), attrList);
                            thMap.put("befMapList", subBefKVMapList);
                            //新值
                            String attrValueAft_name = chAttr.getStrValue("EntityAttr.attrValueAft_name");
                            if (StringUtil.isEmptyOrNull(attrValueAft_name)) {
                                continue;
                            }
                            List subAftKVMapList = beanPseudo(new XmlBean(attrValueAft_name), attrList);
                            thMap.put("aftMapList", subAftKVMapList);
                            pseudoList.add(thMap);
                        }
                        chAttrList.add(chAttr.getRootNode());
                    }
                    //将 chEntity 里单独属性存至 map，推送
                    Map chEntityMap = new HashMap();
                    chEntityMap.put("pseudoList", pseudoList);
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

        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("hsId", hsId);
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph003/ph003_taskInitView", modelMap);
    }

    /**
     * 处理伪属性  转换为 map<>
     * @param pesudoBean 伪属性报文片段
     * @param attrList 伪属性字段集合
     * @return subKVMapList  伪属性的 map
     */
    private List<Map> beanPseudo(XmlBean pesudoBean, List attrList) {
        List subKVMapList = new ArrayList();
        if (pesudoBean == null || pesudoBean.getNodeNames("").size() == 0) {
            return subKVMapList;
        }
        String attrNameEns = pesudoBean.getNodeNames("").get(0);  //HsOwners
        String attrNameEn = attrNameEns.substring(0, attrNameEns.length() - 1);  //HsOwner
        String befPath = "${attrNameEns}.${attrNameEn}";  //HsOwners.HsOwner

        int befNum = pesudoBean.getListNum(befPath);
        for (int i = 0; i < befNum; i++) {
            Map subBefKVMap = new HashMap();
            for (int l = 0; l < attrList.size(); l++) {
                subBefKVMap.put(attrList.get(l), pesudoBean.getStrValue(befPath + "[${i}]." + attrList.get(l)));
                // map< hsOwnerId, 2805 >
            }
            subKVMapList.add(subBefKVMap);
        }
        return subKVMapList;
    }

    /**
     * 指挥处理 当前流程
     * @param request
     * @param response
     * @return
     */
    public ModelAndView cmdProc(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        if (StringUtil.isEmptyOrNull(hsId)) {
            XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
            XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
            XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
            hsId = ProcBidVars.get("hsId").toString();
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = (XmlBean) request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/eland/ph/ph003/ph003_cmdProc_st", modelMap);
    }
    /**
     * 指挥处理结果查看
     * @param request
     * @param response
     * @return
     */
    public ModelAndView cmdProcView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = (XmlBean) request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        return new ModelAndView("/eland/ph/ph003/ph003_cmdProc_vi", modelMap);
    }

    /**
     * 工作部处理 当前流程
     * @param request
     * @param response
     * @return
     */
    public ModelAndView deptProc(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        if (StringUtil.isEmptyOrNull(hsId)) {
            XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
            XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
            XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
            hsId = ProcBidVars.get("hsId").toString();
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = (XmlBean) request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/eland/ph/ph003/ph003_dptmtProc_st", modelMap);
    }

    /**
     * 工作部处理结果查看页面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView deptProcView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = (XmlBean) request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        return new ModelAndView("/eland/ph/ph003/ph003_dptmtProc_vi", modelMap);
    }

    /**
     * include  下一步处理人
     */
    public ModelAndView nextAssignee(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("tdNum", request.getParameter("tdNum"));
        return new ModelAndView("/eland/ph/ph003/ph003_nextAssignee", modelMap);
    }

    /** 启动锁定流程， 执行锁定居民信息 */
    public ModelAndView initLockHs(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String buildId = request.getParameter("buildId");
        String busiKey = request.getParameter("busiKey");
        String hsId = busiKey.replace("HouseInfo_", "");
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = request.getParameter("hsId");
            if (StringUtil.isEmptyOrNull(hsId)) {
                XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
                XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
                XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
                hsId = ProcBidVars.get("hsId").toString();
            }
        }
        ModelMap modelMap = new ModelMap();
        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("hsId", hsId);
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph003/ph003_taskLock", modelMap);
    }

    /** 展示锁定流程， 执行锁定居民信息  */
    public ModelAndView initLockHsView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 输入参数
        String buildId = request.getParameter("buildId");
        String hsId = request.getParameter("hsId");

        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = ProcBidVars.get("hsId").toString();
        }


        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("hsId", hsId);
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph003/ph003_taskLockView", modelMap);
    }

    /** 审核锁定房屋信息 */
    public ModelAndView verifyLock(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        if (StringUtil.isEmptyOrNull(hsId)) {
            XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
            XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
            XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
            hsId = ProcBidVars.get("hsId").toString();
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = (XmlBean) request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/eland/ph/ph003/ph003_verifyLock_st", modelMap);
    }

    /** 工作部处理结果查看页面 */
    public ModelAndView verifyLockView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = (XmlBean) request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        return new ModelAndView("/eland/ph/ph003/ph003_verifyLock_vi", modelMap);
    }

    /* 锁定流程驳回处理 */
    public ModelAndView lockReject(HttpServletRequest request, HttpServletResponse response) {
        String hsId = request.getParameter("hsId");
        if (StringUtil.isEmptyOrNull(hsId)) {
            XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
            XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
            XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
            hsId = ProcBidVars.get("hsId").toString();
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", hsId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = (XmlBean) request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/eland/ph/ph003/ph003_lockReject_st", modelMap);
    }

    /* 锁定流沉驳回处理展示 */
    public ModelAndView lockRejectView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = (XmlBean) request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        return new ModelAndView("/eland/ph/ph003/ph003_lockReject_vi", modelMap);
    }
}