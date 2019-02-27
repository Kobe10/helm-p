import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 院落腾退流程处理
 */
class ph007 extends GroovyController {

    /** 启动流程 */
    public ModelAndView initStart(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String buildId = request.getParameter("busiKey");
        if (StringUtil.isEmptyOrNull(buildId)) {
            XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
            XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
            XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
            buildId = ProcBidVars.get("buildId").toString();
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("buildId", buildId);
        String method = request.getParameter("method");
        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //基本信息 BaseInfo
        operationData.setStrValue(nodePath + "BuildInfo.buildId", buildId);
        svcRequest.addOp("QUERY_BUILD_BASE_INFO", operationData);
        // 查询管理信息
        svcRequest.addOp("QUERY_BUILD_MNG_INFO", operationData);
        // 腾退成本分析
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/getBuildAccept.groovy")
        opData.setStrValue("OpData.RuleParam.buildId", buildId);
        svcRequest.addOp("EXECUTE_RULE", opData);

        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_BASE_INFO");
            if (rspBean != null) {
                modelMap.put("buildBean", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_MNG_INFO");
            if (rspBean != null) {
                modelMap.put("buildMng", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            if (rspBean != null) {
                modelMap.put("buildBcYg", rspBean.getBeanByPath("Operation.OpResult.RuleResult.BuldCbYg").getRootNode())
            }
        }
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph007/ph007_start", modelMap);
    }

    /** 启动流程 */
    public ModelAndView initView(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String buildId = request.getParameter("busiKey");
        if (StringUtil.isEmptyOrNull(buildId)) {
            XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
            XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
            XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
            buildId = ProcBidVars.get("buildId").toString();
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("buildId", buildId);
        String method = request.getParameter("method");
        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //基本信息 BaseInfo
        operationData.setStrValue(nodePath + "BuildInfo.buildId", buildId);
        svcRequest.addOp("QUERY_BUILD_BASE_INFO", operationData);
        // 查询管理信息
        svcRequest.addOp("QUERY_BUILD_MNG_INFO", operationData);
        // 腾退成本分析
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/getBuildAccept.groovy")
        opData.setStrValue("OpData.RuleParam.buildId", buildId);
        svcRequest.addOp("EXECUTE_RULE", opData);

        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_BASE_INFO");
            if (rspBean != null) {
                modelMap.put("buildBean", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_MNG_INFO");
            if (rspBean != null) {
                modelMap.put("buildMng", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            if (rspBean != null) {
                modelMap.put("buildBcYg", rspBean.getBeanByPath("Operation.OpResult.RuleResult.BuldCbYg").getRootNode())
            }
        }
        modelMap.put("flag", "view");
        modelMap.put("buildId", buildId);
        return new ModelAndView("/eland/ph/ph007/ph007_view", modelMap);
    }

    /** 开始 上报院落   ph00701_sbyl_st */
    public ModelAndView sbylStart(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数

        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 调用分页服务获取分页数据
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        modelMap.put("buildId", buildId);
        String method = request.getParameter("method");
        //根据不同方法给定不同跳转页面
        String nodePath = "OpData.";
        XmlBean operationData = new XmlBean();
        //基本信息 BaseInfo
        operationData.setStrValue(nodePath + "BuildInfo.buildId", buildId);
        svcRequest.addOp("QUERY_BUILD_BASE_INFO", operationData);
        // 查询管理信息
        svcRequest.addOp("QUERY_BUILD_MNG_INFO", operationData);
        // 腾退成本分析
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ruleFile", "webroot:/config/${prjCd}/getBuildAccept.groovy")
        opData.setStrValue("OpData.RuleParam.buildId", buildId);
        svcRequest.addOp("EXECUTE_RULE", opData);

        //调用最新的服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_BASE_INFO");
            if (rspBean != null) {
                modelMap.put("buildBean", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("QUERY_BUILD_MNG_INFO");
            if (rspBean != null) {
                modelMap.put("buildMng", rspBean.getBeanByPath("Operation.BuildInfo").getRootNode());
            }
            rspBean = svcResponse.getFirstOpRsp("EXECUTE_RULE");
            if (rspBean != null) {
                modelMap.put("buildBcYg", rspBean.getBeanByPath("Operation.OpResult.RuleResult.BuldCbYg").getRootNode())
            }
        }

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/eland/ph/ph007/ph00701_sbyl_st", modelMap);
    }

    /** 查看 上报院落   ph00701_sbyl_vi */
    public ModelAndView sbylView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        return new ModelAndView("/eland/ph/ph007/ph00701_sbyl_vi", modelMap);
    }

    /** 开始 公司整院审核  ph00702_zysh_st */
    public ModelAndView zyshStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);

        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/eland/ph/ph007/ph00702_zysh_st", modelMap);
    }

    /** 查看 公司整院审核    ph00702_zysh_vi */
    public ModelAndView zyshView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        return new ModelAndView("/eland/ph/ph007/ph00702_zysh_vi", modelMap);
    }

    /** 开始  腾退预审   ph00703_ttys_st */
    public ModelAndView ttysStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        if (hsInfoList.size() > 0) {
            String hsId = "";
            for (int i = 0; i < hsInfoList.size(); i++) {
                XmlNode hsNode = hsInfoList.get(i);
                XmlNode rowNode = hsNode.get("Row");
                String hsIdTemp = rowNode.get("hsId");
                if (StringUtil.isNotEmptyOrNull(hsIdTemp)) {
                    hsId = hsIdTemp + "," + hsId;
                }
            }
            //获取到每个房的hsId,查询每个房产的审核状态
            if (hsId.length() > 0) {
                hsId = hsId.substring(0, hsId.length() - 1);
            }
            XmlBean shBean = new XmlBean();
            shBean.setStrValue("OpData.entityName", "HouseInfo");
            shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", "(${hsId})");
            shBean.setStrValue("OpData.Conditions.Condition[0].operation", "IN");

            shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
            shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult1");
            shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment1");
            shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime1");
            svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
            SvcResponse shResult = query(svcRequest);
            Map hsShMap = new HashMap();
            if (shResult.isSuccess()) {
                XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
                int hsStuNum = hsStatusResult.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < hsStuNum; i++) {
                    XmlBean hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                    hsShMap.put(hsStatus.getStrValue("Row.hsId"), hsStatus.getRootNode());
                }
                modelMap.put("hsShMap", hsShMap);
            }

        }

        return new ModelAndView("/eland/ph/ph007/ph00703_ttys_st", modelMap);
    }

    /** 查看  腾退预审  ph00703_ttys_vi */
    public ModelAndView ttysView(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        ModelMap modelMap = new ModelMap();
        modelMap.put("buildId", buildId);
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);

        if (hsInfoList.size() > 0) {
            String hsId = "";
            for (int i = 0; i < hsInfoList.size(); i++) {
                XmlNode hsNode = hsInfoList.get(i);
                XmlNode rowNode = hsNode.get("Row");
                String hsIdTemp = rowNode.get("hsId");
                if (StringUtil.isNotEmptyOrNull(hsIdTemp)) {
                    hsId = hsIdTemp + "," + hsId;
                }
            }
            //获取到每个房的hsId,查询每个房产的审核状态
            if (hsId.length() > 0) {
                hsId = hsId.substring(0, hsId.length() - 1);
            }
            XmlBean shBean = new XmlBean();
            shBean.setStrValue("OpData.entityName", "HouseInfo");
            shBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            shBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", "(${hsId})");
            shBean.setStrValue("OpData.Conditions.Condition[0].operation", "IN");

            shBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
            shBean.setStrValue("OpData.ResultFields.fieldName[1]", "procResult1");
            shBean.setStrValue("OpData.ResultFields.fieldName[2]", "comment1");
            shBean.setStrValue("OpData.ResultFields.fieldName[3]", "recordTime1");
            svcRequest.addOp("QUERY_ENTITY_CMPT", shBean);
            SvcResponse shResult = query(svcRequest);
            Map hsShMap = new HashMap();
            if (shResult.isSuccess()) {
                XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
                int hsStuNum = hsStatusResult.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < hsStuNum; i++) {
                    XmlBean hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                    hsShMap.put(hsStatus.getStrValue("Row.hsId"), hsStatus.getRootNode());
                }
                modelMap.put("hsShMap", hsShMap);
            }

        }
        return new ModelAndView("/eland/ph/ph007/ph00703_ttys_vi", modelMap);
    }

    /** 开始  签订安置协议  ph00704_qdxy_st */
    public ModelAndView qdxyStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = (XmlBean) request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        if (hsInfoList.size() > 0) {
            String hsId = "";
            for (int i = 0; i < hsInfoList.size(); i++) {
                XmlNode hsNode = (XmlNode) hsInfoList.get(i);
                XmlNode rowNode = (XmlNode) hsNode.get("Row");
                String hsIdTemp = rowNode.get("hsId");
                if (StringUtil.isNotEmptyOrNull(hsIdTemp)) {
                    hsId = hsIdTemp + "," + hsId;
                }
            }
            //获取到每个房的hsId,查询每个房产的审核状态
            if (hsId.length() > 0) {
                hsId = hsId.substring(0, hsId.length() - 1);
            }
            XmlBean qyBean = new XmlBean();
            qyBean.setStrValue("OpData.entityName", "HsCtInfo");
            qyBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            qyBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", "(${hsId})");
            qyBean.setStrValue("OpData.Conditions.Condition[0].operation", "IN");

            qyBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
            qyBean.setStrValue("OpData.ResultFields.fieldName[1]", "hsCtId");
            qyBean.setStrValue("OpData.ResultFields.fieldName[2]", "ctStatus");
            qyBean.setStrValue("OpData.ResultFields.fieldName[3]", "ctStatusDate");
            svcRequest.addOp("QUERY_ENTITY_CMPT", qyBean);
            SvcResponse shResult = query(svcRequest);
            Map hsCtMap = new HashMap();
            if (shResult.isSuccess()) {
                XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
                int hsStuNum = hsStatusResult.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < hsStuNum; i++) {
                    XmlBean hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                    hsCtMap.put(hsStatus.getStrValue("Row.hsId"), hsStatus.getRootNode());
                }
                modelMap.put("hsCtMap", hsCtMap);
            }
        }
        return new ModelAndView("/eland/ph/ph007/ph00704_qdxy_st", modelMap);
    }

    /** 查看  签订安置协议  ph00704_qdxy_vi */
    public ModelAndView qdxyView(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = (XmlBean) request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        if (hsInfoList.size() > 0) {
            String hsId = "";
            for (int i = 0; i < hsInfoList.size(); i++) {
                XmlNode hsNode = (XmlNode) hsInfoList.get(i);
                XmlNode rowNode = (XmlNode) hsNode.get("Row");
                String hsIdTemp = rowNode.get("hsId");
                if (StringUtil.isNotEmptyOrNull(hsIdTemp)) {
                    hsId = hsIdTemp + "," + hsId;
                }
            }
            //获取到每个房的hsId,查询每个房产的审核状态
            if (hsId.length() > 0) {
                hsId = hsId.substring(0, hsId.length() - 1);
            }
            XmlBean qyBean = new XmlBean();
            qyBean.setStrValue("OpData.entityName", "HsCtInfo");
            qyBean.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
            qyBean.setStrValue("OpData.Conditions.Condition[0].fieldValue", "(${hsId})");
            qyBean.setStrValue("OpData.Conditions.Condition[0].operation", "IN");

            qyBean.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
            qyBean.setStrValue("OpData.ResultFields.fieldName[1]", "hsCtId");
            qyBean.setStrValue("OpData.ResultFields.fieldName[2]", "ctStatus");
            qyBean.setStrValue("OpData.ResultFields.fieldName[3]", "ctStatusDate");
            svcRequest.addOp("QUERY_ENTITY_CMPT", qyBean);
            SvcResponse shResult = query(svcRequest);
            Map hsCtMap = new HashMap();
            if (shResult.isSuccess()) {
                XmlBean hsStatusResult = shResult.getFirstOpRsp("QUERY_ENTITY_CMPT");
                int hsStuNum = hsStatusResult.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < hsStuNum; i++) {
                    XmlBean hsStatus = hsStatusResult.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                    hsCtMap.put(hsStatus.getStrValue("Row.hsId"), hsStatus.getRootNode());
                }
                modelMap.put("hsCtMap", hsCtMap);
            }
        }
        return new ModelAndView("/eland/ph/ph007/ph00704_qdxy_vi", modelMap);
    }

    /** 开始  购房人资格审核 ph00705_gfsh_st */
    public ModelAndView gfshStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00705_gfsh_st", modelMap);
    }

    /** 查看  购房人资格审核  ph00705_gfsh_vi */
    public ModelAndView gfshView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();
        modelMap.put("buildId", buildId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00705_gfsh_vi", modelMap);
    }

    /** 开始  OA审核资料准备  ph00705_oash_st */
    public ModelAndView oashStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00705_oash_st", modelMap);
    }

    /** 查看  OA审核资料准备  ph00705_oash_vi */
    public ModelAndView oashView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();
        modelMap.put("buildId", buildId);
        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00705_oash_vi", modelMap);
    }

    /** 开始  私房核验  ph00705_sfhy_st */
    public ModelAndView sfhyStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00705_sfhy_st", modelMap);
    }

    /** 查看  私房核验  ph00705_sfhy_vi */
    public ModelAndView sfhyView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00705_sfhy_vi", modelMap);
    }

    /** 开始  OA审核资料  ph00706_oashzl_st */
    public ModelAndView oashzlStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00706_oashzl_st", modelMap);
    }

    /** 查看  OA审核资料  ph00706_oashzl_vi */
    public ModelAndView oashzlView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00706_oashzl_vi", modelMap);
    }

    /** 开始  安置协议盖上签字  ph00707_xygz_st */
    public ModelAndView xygzStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00707_xygz_st", modelMap);
    }

    /** 查看  安置协议盖上签字  ph00707_xygz_vi */
    public ModelAndView xygzView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00707_xygz_vi", modelMap);
    }

    /** 开始  公房退租申请  ph00708_tzsq_st */
    public ModelAndView tzsqStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();
        modelMap.put("buildId", buildId);

        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00708_tzsq_st", modelMap);
    }

    /** 查看  公房退租申请  ph00708_tzsq_vi */
    public ModelAndView tzsqView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00708_tzsq_vi", modelMap);
    }

    /** 开始  挑选安置房  ph00709_txfw_st  判断公私房 执行不同情况*/
    public ModelAndView txfwStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);

        return new ModelAndView("/eland/ph/ph007/ph00709_txfw_st", modelMap);
    }

    /** 查看  挑选安置房  ph00709_txfw_vi 判断公私房 执行不同情况*/
    public ModelAndView txfwView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00709_txfw_vi", modelMap);
    }

    /** 开始  完成退租手续  ph00710_wctz_st */
    public ModelAndView wctzStart(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00710_wctz_st", modelMap);
    }

    /** 查看  完成退租手续  ph00710_wctz_vi */
    public ModelAndView wctzView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();
        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00710_wctz_vi", modelMap);
    }

    /** 开始  办理网签、核税、缴税等手续  ph00710_wqjs_st */
    public ModelAndView wqjsStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00710_wqjs_st", modelMap);
    }

    /** 查看  办理网签、核税、缴税等手续  ph00710_wqjs_vi */
    public ModelAndView wqjsView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00710_wqjs_vi", modelMap);
    }

    /** 开始  办理房屋所有权转让登记  ph00711_zrdj_st */
    public ModelAndView zrdjStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00711_zrdj_st", modelMap);
    }

    /** 查看  办理房屋所有权转让登记  ph00711_zrdj_vi */
    public ModelAndView zrdjView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00711_zrdj_vi", modelMap);
    }

    /** 开始  领取新房产证  ph00712_lqxz_st */
    public ModelAndView lqxzStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00712_lqxz_st", modelMap);
    }

    /** 查看  领取新房产证  ph00712_lqxz_vi */
    public ModelAndView lqxzView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00712_lqxz_vi", modelMap);
    }

    /** 开始  腾房交房  ph00713_tfjf_st */
    public ModelAndView tfjfStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00713_tfjf_st", modelMap);
    }

    /** 查看  腾房交房  ph00713_tfjf_vi */
    public ModelAndView tfjfView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00713_tfjf_vi", modelMap);
    }

    /** 开始  安置协议结算  ph00714_azjs_st */
    public ModelAndView azjsStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00714_azjs_st", modelMap);
    }

    /** 查看  安置协议结算  ph00714_azjs_vi */
    public ModelAndView azjsView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00714_azjs_vi", modelMap);
    }

    /** 查看  领取购房合同材料  ph00715_gfhtcl_vi */
    public ModelAndView gfhtclStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00715_gfhtcl_st", modelMap);
    }

    /** 查看  领取购房合同材料  ph00715_gfhtcl_vi */
    public ModelAndView gfhtclView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00715_gfhtcl_vi", modelMap);
    }

    /** 开始  签订购房合同，结算物业费  ph00715_qdgfht_st */
    public ModelAndView qdgfhtStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00715_qdgfht_st", modelMap);
    }

    /** 查看  签订购房合同，结算物业费  ph00715_qdgfht_vi */
    public ModelAndView qdgfhtView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00715_qdgfht_vi", modelMap);
    }

    /** 开始  户口外迁费OA审核  ph00716_qhsh_st */
    public ModelAndView qhshStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00716_qhsh_st", modelMap);
    }

    /** 查看  户口外迁费OA审核  ph00716_qhsh_vi */
    public ModelAndView qhshView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00716_qhsh_vi", modelMap);
    }

    /** 开始  支付户口外迁费  ph00717_zffy_st */
    public ModelAndView zffyStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00717_zffy_st", modelMap);
    }

    /** 查看  支付户口外迁费  ph00717_zffy_vi */
    public ModelAndView zffyView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());

        /* 根据buildId 查询所有之下的房产 */
        List hsInfoList = new ArrayList();
        hsInfoList = searchHsByBuildId(request, response);
        modelMap.put("hsInfoList", hsInfoList);
        return new ModelAndView("/eland/ph/ph007/ph00717_zffy_vi", modelMap);
    }

    /** 开始  领取新租赁合同  ph00718_lqxht_st */
    public ModelAndView lqxhtStart(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        modelMap.put("buildId", buildId);
        String runIndex = request.getParameter("R_I");
        XmlBean runTask = request.getAttribute("RUN_TASK_INF_" + runIndex);
        String taskId = runTask.getStrValue("RuTaskInfo.taskId");
        modelMap.put("taskId", taskId);
        return new ModelAndView("/eland/ph/ph007/ph00718_lqxht_st", modelMap);
    }

    /** 查看  领取新租赁合同  ph00718_lqxht_vi */
    public ModelAndView lqxhtView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hiIndex = request.getParameter("H_I");
        XmlBean hiTask = request.getAttribute("HIS_TASK_INF_" + hiIndex);
        modelMap.put("hiTaskInfo", hiTask.getRootNode());
        return new ModelAndView("/eland/ph/ph007/ph00718_lqxht_vi", modelMap);
    }

    /** 根据建筑ID 查询 之下所有房产 */
    public List searchHsByBuildId(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        XmlNode procBean = (XmlNode) request.getAttribute("PROC_INST_INFO");
        XmlNode ProcInsInfo = (XmlNode) procBean.get("ProcInsInfo");
        XmlNode ProcBidVars = (XmlNode) ProcInsInfo.get("ProcBidVars");
        String buildId = ProcBidVars.get("buildId").toString();

        /* 根据buildId 查询所有之下的房产 */
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "buildId");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", buildId);
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");

        opData.setStrValue("OpData.ResultFields.fieldName[0]", "hsId");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "hsOwnerPersons");
        opData.setStrValue("OpData.ResultFields.fieldName[2]", "hsOwnerType");
        opData.setStrValue("OpData.ResultFields.fieldName[3]", "hsBuildSize");
        opData.setStrValue("OpData.ResultFields.fieldName[4]", "procResult1");
        opData.setStrValue("OpData.ResultFields.fieldName[5]", "procResult2");
        opData.setStrValue("OpData.ResultFields.fieldName[6]", "procResult3");
        opData.setStrValue("OpData.ResultFields.fieldName[7]", "procResult4");
        opData.setStrValue("OpData.ResultFields.fieldName[8]", "procResult5");
        opData.setStrValue("OpData.ResultFields.fieldName[9]", "procResult6");
        opData.setStrValue("OpData.ResultFields.fieldName[10]", "procResult7");
        opData.setStrValue("OpData.ResultFields.fieldName[11]", "procResult8");
        opData.setStrValue("OpData.ResultFields.fieldName[12]", "procResult9");
        opData.setStrValue("OpData.ResultFields.fieldName[13]", "procResult10");
        opData.setStrValue("OpData.ResultFields.fieldName[14]", "procResult11");
        opData.setStrValue("OpData.ResultFields.fieldName[15]", "hsPubZj");
        opData.setStrValue("OpData.ResultFields.fieldName[16]", "hsPubGhf");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        List hsInfoList = new ArrayList();
        if (svcResponse.isSuccess()) {
            XmlBean result = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT");
            int hsInfoNum = result.getListNum("Operation.OpResult.PageData.Row");
            for (int i = 0; i < hsInfoNum; i++) {
                XmlBean temp = result.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                hsInfoList.add(temp.getRootNode());
            }
        }
        return hsInfoList;
    }

    /** include  下一步处理人  */
    public ModelAndView nextAssignee(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("tdNum", request.getParameter("tdNum"));
        return new ModelAndView("/eland/ph/ph007/nextAssignee", modelMap);
    }
}
