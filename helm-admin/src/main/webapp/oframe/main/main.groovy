import com.jcabi.manifests.Manifests
import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class main extends GroovyController {
    String[] colorArr = ["#58a7f7", "#ff0000", "#84ff00", "#ffe400", "#0090ff", "#ef0eef", "#1dd326", "#0042ff", "#ffea00",
                         "#ff0000", "#0096ff", "#30ff00", "#a40ff9", "#2cc6d8", "#9e960e", "#ff7575", "#2cd8ba", "#00e4ff",
                         "#ffaa4e", "#3c9615", "#fc69fa", "#9473ff", "#2cd8c2", "#d8ff00", "#ff009c", "#d88d2c", "#0e8a8a",
                         "#fcb1b1", "#67b414", "#00d8ff", "#81f3bf", "#f4bdf4", "#1dd326", "#db804a", "#a384c3", "#b73400",
                         "#2328e7", "#5ce8f8", "#fffaa3", "#9d6ef3", "#38fe3c", "#31b9c9", "#9f4ae7", "#989b48", "#da2bd8",
                         "#2d9dc6", "#90f1a0", "#ff9e6f", "#ffa4dc", "#9fe0f8", "#0a8307", "#ff9000", "#e941a8"];
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 获取登录信息
        SessionUtil sessionUtil = new SessionUtil(request);
        String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
        XmlBean loginBean = new XmlBean(loginInfo);
        // 获取传入的项目编号
        String treeType = request.getParameter("treeType");
        String pOpCode = request.getParameter("pOpCode");
        if (StringUtil.isEmptyOrNull(pOpCode)) {
            pOpCode = "1";
        }
        // 当前项目编号
        String prjCd = request.getParameter("prjCd");
        String orgId = request.getParameter("orgId");

        // 获取工号的项目
        // 获取工号可以访问的项目工程
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "SvcCont.staffCode";
        XmlBean reqData = new XmlBean();
        // 查询条件
        reqData.setStrValue(prePath, svcRequest.getReqStaffCd());
        reqData.setStrValue("SvcCont.orgId", orgId);
        reqData.setStrValue("SvcCont.isShowExp", "1");   // 是否显示无效项目标志。 1 不显示无效项目， 0  显示。
        // 调用服务查询实体全部属性
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "queryStaffProject", svcRequest);
        Map<String, String> prjMap = new LinkedHashMap<String, String>();
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
            if (null != xmlBean) {
                int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                for (int i = 0; i < cmpPrjCount; i++) {
                    prjMap.put(xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"),
                            xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjName"))
                }
            }
        }
        if (StringUtil.isEmptyOrNull(orgId)) {
            if (StringUtil.isEmptyOrNull(prjCd) && prjMap.keySet().size() > 0) {
                String sysPrjCd = getCookieByName(request, "sysPrjCd")
                if (!StringUtil.isEmptyOrNull(prjMap.get(sysPrjCd))) {
                    prjCd = sysPrjCd;
                } else {
                    prjCd = prjMap.keySet().toArray()[0];
                }
            } else if (StringUtil.isEmptyOrNull(prjCd)) {
                prjCd = "0";
                orgId = loginBean.getStrValue("LoginInfo.orgId");
            }
        }
        // 更新Session中的prjCd
        loginBean.setStrValue("LoginInfo.prjCd", prjCd);
        sessionUtil.setAttr(SessionUtil.LOGIN_IN_SESSION_KEY, loginBean.toXML());
        // 获取顶级菜单
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        svcRequest.setStrValue("Request.SvcCont.rhtId", pOpCode);
        svcRequest.setValue("Request.SvcCont.level", 1);
        svcRequest.setValue("Request.SvcCont.treeType", 1);
        svcResponse = callService("staffService", "queryMenuTree", svcRequest);
        List menuList = new ArrayList();
        XmlBean subSystems = svcResponse.getBeanByPath("Response.SvcCont.Staff.Menu");
        if (subSystems != null) {
            int count = subSystems.getListNum("Menu.Node") - 1;
            for (int i = 0; i < count; i++) {
                Map subMenuMap = new HashMap();
                String id = subSystems.getStrValue("""Menu.Node[${i}].rhtId""");
                if (StringUtil.isEqual(pOpCode, id)) {
                    continue;
                }
                String pId = subSystems.getStrValue("""Menu.Node[${i}].uRhtId""");
                String cd = subSystems.getStrValue("""Menu.Node[${i}].rhtCd""");
                String name = subSystems.getStrValue("""Menu.Node[${i}].rhtName""");
                String url = subSystems.getStrValue("""Menu.Node[${i}].navUrl""");
                subMenuMap.put("id", id);
                subMenuMap.put("pId", pId);
                subMenuMap.put("cd", cd);
                subMenuMap.put("name", name);
                subMenuMap.put("url", url);
                menuList.add(subMenuMap);
            }
            // 拼接最后一个节点
            String id = subSystems.getStrValue("""Menu.Node[${count}].rhtId""");
            if (!StringUtil.isEqual(pOpCode, id)) {
                Map subMenuMap = new HashMap();
                String pId = subSystems.getStrValue("""Menu.Node[${count}].uRhtId""");
                String cd = subSystems.getStrValue("""Menu.Node[${count}].rhtCd""");
                String name = subSystems.getStrValue("""Menu.Node[${count}].rhtName""");
                String url = subSystems.getStrValue("""Menu.Node[${count}].navUrl""");
                subMenuMap.put("id", id);
                subMenuMap.put("pId", pId);
                subMenuMap.put("cd", cd);
                subMenuMap.put("name", name);
                subMenuMap.put("url", url);
                menuList.add(subMenuMap);
            }
        }

        /**
         * 收藏功能请求以及处理响应
         */
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        svcRequest.setStrValue("Request.SvcCont.prjCd", prjCd);
        svcRequest.setStrValue("Request.SvcCont.treeType", treeType);
        svcResponse = callService("staffService", "queryStaffFavoriteRht", svcRequest);
        XmlBean collectionItems = svcResponse.getBeanByPath("Response.SvcCont.Staff.Collection");

        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        /**
         * 收藏结果存储
         */
        if (collectionItems != null) {
            modelMap.put("collectionItems", collectionItems.getRootNode());
        }

        /**
         * 面板查询请求处理
         */
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        svcRequest.setReqData(reqData);
        svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean portalRhtsBean = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            if (portalRhtsBean != null) {
                List<XmlNode> portalRhts = new ArrayList<XmlNode>();
                int haveRhtPortal = portalRhtsBean.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    boolean isHaveRht = Boolean.parseBoolean(portalRhtsBean.getStrValue("portalRhts.portalRht[${i}].isHaveRht"));
                    if (isHaveRht) {
                        portalRhts.add(portalRhtsBean.getBeanByPath("portalRhts.portalRht[${i}]").getRootNode());
                    }
                }
                modelMap.put("staffPanel", portalRhts);
            }
        }

        //展示菜单树
        modelMap.put("menuList", menuList);
        // 登录信息
        modelMap.put("loginInfo", loginBean.getRootNode());
        modelMap.put("prjName", prjMap.get(prjCd));
        modelMap.put("prjCd", prjCd);
        modelMap.put("orgId", orgId);
        // 设置字体大小
        String fontSize = getCookieByName(request, "fontSize");
        if (StringUtil.isNotEmptyOrNull(fontSize)) {
            modelMap.put("fontSize", fontSize);
        }
        //查询组织信息
        String orgStyle = "";
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Org.Node.orgId", loginBean.getStrValue("LoginInfo.orgId"));
        svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", prjCd);
        svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org");
            orgStyle = nodeInfoBean.getStrValue("Org.Node.orgStyle");
            String orgLogo = nodeInfoBean.getStrValue("Org.Node.orgLogo");
            modelMap.put("orgLogo", orgLogo);
        }
        //网站换肤
        String theme = getCookieByName(request, "dwz_theme");
        if (StringUtil.isNotEmptyOrNull(theme)) {
            modelMap.put("theme", theme);
        } else {
            //判断 如果组织没有设置样式 取默认值
            if (StringUtil.isNotEmptyOrNull(orgStyle)) {
                modelMap.put("theme", orgStyle);
            } else {
                modelMap.put("theme", "blue");
            }
        }
        // 返回主登录界面
        return new ModelAndView("/oframe/main/index", modelMap);
    }

    /**
     * 切换项目
     * @param request 请求名称
     * @param response 响应结果
     * @return 返回结果
     */
    public void changePrj(HttpServletRequest request, HttpServletResponse response) {
        // 获取切换后的项目
        String toPrjCd = request.getParameter("toPrjCd");
        // 获取工号可以访问的项目工程
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        SvcResponse svcResponse = callService("projectService", "queryStaffProject", svcRequest);
        boolean haveRight = false;
        String errMsg = "没有权限访问";
        if (svcResponse.isSuccess()) {
            Map<String, String> tempMap = svcResponse.getValue("Response.SvcCont.listData");
            for (Object temp : tempMap.keySet()) {
                if (StringUtil.isEqual(toPrjCd, temp)) {
                    haveRight = true;
                    break;
                }
            }
        } else {
            errMsg = svcResponse.getErrMsg();
        }
        // 有权限
        if (haveRight) {
            SessionUtil sessionUtil = new SessionUtil(request);
            String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
            XmlBean loginBean = new XmlBean(loginInfo);
            loginBean.setStrValue("LoginInfo.prjCd", toPrjCd);
            sessionUtil.setAttr(SessionUtil.LOGIN_IN_SESSION_KEY, loginBean.toXML());
        }
        ResponseUtil.printAjax(response, """{"success": ${haveRight}, "errMsg": "${errMsg}"}""");
    }


    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys014/message", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView portal(HttpServletRequest request, HttpServletResponse response) {
        // 获取登录信息
        SessionUtil sessionUtil = new SessionUtil(request);
        String url = request.getParameter("url");
        ModelMap modelMap = new ModelMap();
        // 获取顶级菜单
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        SvcResponse svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean potalRhts = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            if (potalRhts != null) {
                List<XmlNode> portalRhts = new ArrayList<XmlNode>();
                int haveRhtPortal = potalRhts.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    boolean isHaveRht = Boolean.parseBoolean(potalRhts.getStrValue("portalRhts.portalRht[${i}].isHaveRht"));
                    if (isHaveRht) {
                        String newurl = potalRhts.getBeanByPath("portalRhts.portalRht[${i}]").getStrValue("portalRht.navUrl");
                        if (StringUtil.contains(newurl, "mb005") || StringUtil.contains(newurl, "mb008")) {
                            potalRhts.setStrValue("portalRhts.portalRht[${i}].needFullScreen", "true");
                        } else if (StringUtil.contains(newurl, "mb001-init")) {
                            potalRhts.setStrValue("portalRhts.portalRht[${i}].needWorkMore", "true");
                        } else if (StringUtil.contains(newurl, "mb001-myTask")) {
                            potalRhts.setStrValue("portalRhts.portalRht[${i}].needTaskMore", "true");
                        }
                        portalRhts.add(potalRhts.getBeanByPath("portalRhts.portalRht[${i}]").getRootNode());
                    }
                }
                modelMap.put("staffPanel", portalRhts);
            }
        }
        return new ModelAndView("/oframe/main/portal", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView leftPortal(HttpServletRequest request, HttpServletResponse response) {
        // 获取有权限的目录树结构信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.level", "-1");
        svcRequest.setValue("Request.SvcCont.rhtCd", "SYS_PORTAL");
        SvcResponse svcResponse = callService("staffService", "queryMenuTree", svcRequest);
        Map<String, XmlBean> portalRht = new HashMap<String, XmlBean>();
        if (svcResponse.isSuccess()) {
            XmlBean mbTree = svcResponse.getBeanByPath("Response.SvcCont.Staff.Menu");
            if (mbTree != null) {
                int count = mbTree.getListNum("Menu.Node");
                for (int i = 0; i < count; i++) {
                    String id = mbTree.getStrValue("""Menu.Node[${i}].rhtId""");
                    portalRht.put(id, mbTree.getBeanByPath("Menu.Node[${i}]"));
                }
            }
        }
        // 获取登录信息
        XmlBean reqData = new XmlBean();
        ModelMap modelMap = new ModelMap();
        // 获取顶级菜单
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setReqData(reqData);
        svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        // 权限资源对照关系
        Map<String, List<XmlNode>> tempMap = new LinkedHashMap<String, List<XmlNode>>();
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean potalRhts = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            if (potalRhts != null) {
                int haveRhtPortal = potalRhts.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    XmlBean protalRhtBean = potalRhts.getBeanByPath("portalRhts.portalRht[${i}]");
                    boolean isHaveRht = Boolean.parseBoolean(protalRhtBean.getStrValue("portalRht.isHaveRht"));
                    String rhtId = protalRhtBean.getStrValue("portalRht.rhtId");
                    XmlBean nodeBean = portalRht.get(rhtId);
                    if (nodeBean == null) {
                        continue;
                    }
                    String uRhtId = nodeBean.getStrValue("Node.uRhtId");
                    if (isHaveRht) {
                        String navUrl = protalRhtBean.getStrValue("portalRht.navUrl");
                        if (StringUtil.contains(navUrl, "mb005") || StringUtil.contains(navUrl, "mb008")) {
                            protalRhtBean.setStrValue("portalRht.needFullScreen", "true");
                        }
                        List<XmlNode> tempXmlNode = tempMap.get(uRhtId);
                        if (tempXmlNode == null) {
                            tempXmlNode = new ArrayList<XmlBean>();
                            tempMap.put(uRhtId, tempXmlNode);
                        }
                        tempXmlNode.add(protalRhtBean.getRootNode());
                    }
                }
            }
        }
        Map<String, XmlNode> portalRhts = new LinkedHashMap<String, List<XmlNode>>();
        for (String temp : tempMap.keySet()) {
            XmlBean upNode = portalRht.get(temp);
            if (upNode == null) {
                continue;
            }
            portalRhts.put(upNode.getStrValue("Node.rhtName"), tempMap.get(temp));
        }
        modelMap.put("staffPanel", portalRhts);
        return new ModelAndView("/oframe/main/portal_left", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView screen(HttpServletRequest request, HttpServletResponse response) {
        // 获取登录信息
        XmlBean reqData = new XmlBean();
        ModelMap modelMap = new ModelMap();
        String id = request.getParameter("id");
        String theme = request.getParameter("themeType");
        String fontColor = "black";
        if ("black" == theme || "blue" == theme) {
            fontColor = "white";
        }
        modelMap.put("fontColor", fontColor);
        // 获取顶级菜单
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int prjCd = NumberUtil.getIntFromObj(request.getParameter("prjCd"));
        //取地图颜色作为指标颜色
        Map<String, String> colorMap = getCfgCollection(request, "MAP_COLORS", true, prjCd);
        String colorStr = "";
        if (colorMap.size() > 0) {
            for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                colorStr = entry.getValue();
                if (colorStr != null) {
                    break;
                }
            }
            colorStr = "\"" + colorStr.toString().replace(" ", "").replace(",", "\",\"") + "\"";
        } else {//没有地图颜色，则手动设置颜色
            colorStr = "\"" + colorArr.toString().replace(" ", "").replace("[", "").replace("]", "").replace(",", "\",\"") + "\"";
        }
        modelMap.put("colorStr", colorStr);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean potalRhts = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            XmlBean portalRht = null;
            if (potalRhts != null) {
                int haveRhtPortal = potalRhts.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    boolean isHaveRht = Boolean.parseBoolean(potalRhts.getStrValue("portalRhts.portalRht[${i}].isHaveRht"));
                    if (isHaveRht) {
                        String newId = potalRhts.getBeanByPath("portalRhts.portalRht[${i}]").getStrValue("portalRht.rhtId");
                        if (StringUtil.isEqual(id, newId)) {
                            portalRht = potalRhts.getBeanByPath("portalRhts.portalRht[${i}]");
                            String navUrl = potalRhts.getBeanByPath("portalRhts.portalRht[${i}]").getStrValue("portalRht.navUrl");
                            if (StringUtil.contains(navUrl, "mb005")) {
                                portalRht.setStrValue("portalRht.opMb", "mb005");
                            } else if (StringUtil.contains(navUrl, "mb008-initReg")) {
                                portalRht.setStrValue("portalRht.opMb", "mb008-initReg");
                            } else if (StringUtil.contains(navUrl, "mb008-init")) {
                                portalRht.setStrValue("portalRht.opMb", "mb008-initOrg");
                            }
                            // 转换请求参数
                            int paramIdx = navUrl.indexOf("?");
                            if (paramIdx > 0) {
                                String requestParam = navUrl.substring(paramIdx + 1);
                                String[] paramStrArr = requestParam.split("&");
                                for (String temp : paramStrArr) {
                                    String[] nameValue = temp.split("=");
                                    if (nameValue.length == 2) {
                                        modelMap.put(nameValue[0], nameValue[1]);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                modelMap.put("portalRht", portalRht.getRootNode());
            }
        }
        return new ModelAndView("/oframe/main/screen", modelMap);
    }

    /**
     * 获取目录树
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView menuTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String pOpCode = request.getParameter("pOpCode");
        String jsonStr = "";
        if (StringUtil.isEmptyOrNull(pOpCode)) {
            pOpCode = "1";//rhtId--frist 1  从系统管理级别开始
        }
        // 获取顶级菜单
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.SvcCont.rhtId", pOpCode);
        svcRequest.setValue("Request.SvcCont.level", 1);
        svcRequest.setValue("Request.SvcCont.treeType", 1);
        SvcResponse svcResponse = callService("staffService", "queryMenuTree", svcRequest);
        List menuList = new ArrayList();
        XmlBean subSystems = svcResponse.getBeanByPath("Response.SvcCont.Staff.Menu");
        if (subSystems != null) {
            int count = subSystems.getListNum("Menu.Node") - 1;
            for (int i = 0; i < count; i++) {
                Map subMenuMap = new HashMap();
                String id = subSystems.getStrValue("""Menu.Node[${i}].rhtId""");
                if (StringUtil.isEqual(pOpCode, id)) {
                    continue;
                }
                String pId = subSystems.getStrValue("""Menu.Node[${i}].uRhtId""");
                String cd = subSystems.getStrValue("""Menu.Node[${i}].rhtCd""");
                String name = subSystems.getStrValue("""Menu.Node[${i}].rhtName""");
                String url = subSystems.getStrValue("""Menu.Node[${i}].navUrl""");
                subMenuMap.put("id", id);
                subMenuMap.put("pId", pId);
                subMenuMap.put("cd", cd);
                subMenuMap.put("name", name);
                subMenuMap.put("url", url);
                menuList.add(subMenuMap);
            }
            // 拼接最后一个节点
            String id = subSystems.getStrValue("""Menu.Node[${count}].rhtId""");
            if (!StringUtil.isEqual(pOpCode, id)) {
                Map subMenuMap = new HashMap();
                String pId = subSystems.getStrValue("""Menu.Node[${count}].uRhtId""");
                String cd = subSystems.getStrValue("""Menu.Node[${count}].rhtCd""");
                String name = subSystems.getStrValue("""Menu.Node[${count}].rhtName""");
                String url = subSystems.getStrValue("""Menu.Node[${count}].navUrl""");
                subMenuMap.put("id", id);
                subMenuMap.put("pId", pId);
                subMenuMap.put("cd", cd);
                subMenuMap.put("name", name);
                subMenuMap.put("url", url);
                menuList.add(subMenuMap);
            }
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("subMenu", menuList);
        return new ModelAndView("/oframe/main/subMenu", modelMap);
    }

    /**
     * 打开设置页面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView setting(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //查询员工基本信息
        XmlBean baseInfo = new XmlBean();
        baseInfo.setStrValue("SvcCont.Staff.staffId", svcRequest.getReqStaffIdInt());
        svcRequest.setReqData(baseInfo);
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
            modelMap.put("nodeInfo", staffBean.getRootNode());
            int roleCount = staffBean.getListNum("Staff.Roles.Role");
            List<Map<String, String>> roleList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < roleCount; i++) {
                String nodePath = "Staff.Roles.Role[${i}].";
                Map<String, String> item = new HashMap<String, String>();
                item.put("roleCd", staffBean.getStrValue(nodePath + "RoleCd"));
                item.put("roleName", staffBean.getStrValue(nodePath + "RoleName"));
                roleList.add(item);
            }
            modelMap.put("roleList", roleList);
        }
        // 面板设置不受项目的限制
        /** 面板查询请求处理 */
        svcRequest = RequestUtil.getSvcRequest(request);
        baseInfo = new XmlBean();
        baseInfo.setStrValue("SvcCont.Staff.controlByPrj", "true");
        baseInfo.setStrValue("SvcCont.Staff.showDefault", "false");
        svcRequest.setReqData(baseInfo);
        svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean potalRhts = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            if (potalRhts != null) {
                List<XmlNode> portalRhts = new ArrayList<XmlNode>();
                int haveRhtPortal = potalRhts.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    portalRhts.add(potalRhts.getBeanByPath("portalRhts.portalRht[${i}]").getRootNode());
                }
                modelMap.put("staffPanel", portalRhts);
            }
        }
        // 打开系统设置界面
        return new ModelAndView("/oframe/main/setting", modelMap);
    }

    /**
     * 添加收藏列表
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void addFav(HttpServletRequest request, HttpServletResponse response) {
        //请求参数封装
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        // 获取输入参数
        String privilegeId = request.getParameter("privilegeId");
        reqData.setValue("SvcCont.rightId", privilegeId);
        log.debug("privilegeId=" + privilegeId);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "saveAddFavoriteRht", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 密码修改
     * @param request 请求参数
     * @param response 响应结果
     */
    public ModelAndView chgPwd(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String staffCd = request.getParameter("staffCd");
        String newPassword = request.getParameter("newPassword");
        String oldPassword = request.getParameter("oldPassword");
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.staffCode", staffCd);
        reqData.setStrValue("SvcCont.oldPassword", oldPassword);
        reqData.setStrValue("SvcCont.newPassword", newPassword);
        //
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("staffService", "changeStaffPwd", svcRequest);
        // 返回前台
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 员工信息保存
     * @param request
     * @param response
     */
    public void saveStaff(HttpServletRequest request, HttpServletResponse response) {

        // 返回处理结果
        String method = request.getParameter("method");
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Staff.";
        reqData.setStrValue(nodePath + "orgId", request.getParameter("orgId"));
        reqData.setStrValue(nodePath + "staffCode", request.getParameter("staffCode"));
        reqData.setStrValue(nodePath + "staffName", request.getParameter("staffName"));
        reqData.setStrValue(nodePath + "staffTel", request.getParameter("staffTel"));
        reqData.setStrValue(nodePath + "staffMobile", request.getParameter("staffMobile"));
        reqData.setStrValue(nodePath + "staffEmail", request.getParameter("staffEmail"));
        reqData.setStrValue(nodePath + "notes", request.getParameter("notes"));
        // 请求调用服务
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", "saveUpdateBaseInfo", svcRequest);

        String jsonStr = "";
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 密码选择的面板
     * @param request 请求参数
     * @param response 响应结果
     */
    public void savePanel(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        // 获取请求的权限ID
        String rhtIds = request.getParameter("rhtIds");
        if (!StringUtil.isEmptyOrNull(rhtIds)) {
            // 返回权限ID
            String[] rhtIdArr = rhtIds.split(",");
            for (int i = 0; i < rhtIdArr.length; i++) {
                String preNodePath = "SvcCont.StaffPortal.staffPortals.staffPortal[${i}].";
                reqData.setStrValue(preNodePath + "rhtId", rhtIdArr[i]);
                reqData.setStrValue(preNodePath + "colIdx", "1");
                reqData.setStrValue(preNodePath + "showOrder", "${i}");
            }
        }
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("staffService", "updateOrInsertPortalInfo", svcRequest);
        // 返回前台
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void settingTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String staffCode = request.getParameter("panelStaffCode");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.level", "-1");
        svcRequest.setValue("Request.SvcCont.rhtCd", "SYS_PORTAL");
        SvcResponse svcResponse = callService("staffService", "queryMenuTree", svcRequest);
        // 获取角色权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffCode", staffCode);
        SvcResponse staffResponse = callService("staffService", "queryStaffRht", svcRequest);
        boolean result = svcResponse.isSuccess() && staffResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + staffResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean panelRhtData = staffResponse.getRspData();
            int haveRhtCount = 0;
            if (panelRhtData != null) {
                haveRhtCount = panelRhtData.getListNum("SvcCont.Staff.Rights.Right")
            };
            List<String> panelList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Staff.Rights.Right[${i}].";
                panelList.add(panelRhtData.getStrValue(nodePath + "rhtId"));
            }

            XmlBean mbTree = svcResponse.getBeanByPath("Response.SvcCont.Staff.Menu");
            if (mbTree != null) {
                int count = mbTree.getListNum("Menu.Node");
                for (int i = 0; i < count; i++) {
                    String id = mbTree.getStrValue("""Menu.Node[${i}].rhtId""");
                    String pId = mbTree.getStrValue("""Menu.Node[${i}].uRhtId""");
                    String name = mbTree.getStrValue("""Menu.Node[${i}].rhtName""");
                    String href = mbTree.getStrValue("""Menu.Node[${i}].navUrl""");
                    String rhtSubType = mbTree.getStrValue("""Menu.Node[${i}].rhtSubType""");
                    boolean checkEnable = !StringUtil.isEqual("2", rhtSubType);
                    sb.append("""{ id: "${id}", pId: "${pId}", "nocheck": ${checkEnable}, href: "${
                        href
                    }", checked: "false", name: "${
                        name
                    }",open: "true"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化统计消息界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void countNotice(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = new SvcRequest();

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "NoticeInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "noticeReadStatus");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "0");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "toStaffId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", RequestUtil.getStaffId(request));
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "noticeId");
        opData.setStrValue(prePath + "PageInfo.pageSize", "4");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        svcRequest.addOp("QUERY_ONE_ENTITY_COUNT_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        String count = "0";
        if (svcResponse.isSuccess()) {
            //获取签约控制启动状态
            XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_ONE_ENTITY_COUNT_DATA")
                    .getBeanByPath("Operation.OpResult");
            if (opResult != null) {
                count = opResult.getStrValue("OpResult.count");
            }
        }
        String countNotice = "'count':'${count}'";
        ResponseUtil.printSvcResponse(response, svcResponse, countNotice);
    }

    /**
     * 初始化未读消息界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView showNotice(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String count = "0";
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "NoticeInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "noticeReadStatus");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "0");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "toStaffId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", svcRequest.getReqStaffIdInt());
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "noticeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "createStaffId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "noticeContent");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "createTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "noticeNote");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "noticeDelStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "toStaffId");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "noticeReadStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "statusTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "noticeDealNote");
        // 排序条件
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", "createTime");
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", "desc");
        opData.setStrValue(prePath + "PageInfo.pageSize", "4");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //获取签约控制启动状态
            XmlBean noticeInfoList = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            if (noticeInfoList != null) {
                count = noticeInfoList.getStrValue("OpResult.PageInfo.totalRecord");
                modelMap.put("count", count);
                int temp = noticeInfoList.getListNum("OpResult.PageData.Row");
                List<XmlNode> returnList = new ArrayList<XmlNode>(temp);
                for (int i = 0; i < temp; i++) {
                    XmlBean noticeInfo = noticeInfoList.getBeanByPath("OpResult.PageData.Row[${i}]");
                    String[] name = new String[2];
                    name[0] = noticeInfo.getStrValue("Row.createStaffId");
                    name[1] = noticeInfo.getStrValue("Row.toStaffId");
                    for (int j = 0; j < 2; j++) {
                        svcRequest = RequestUtil.getSvcRequest(request);
                        // 数据
                        XmlBean reqData = new XmlBean();
                        String nodePath = "SvcCont.Staff.";
                        reqData.setValue(nodePath + "staffId", name[j]);
                        // 请求信息
                        svcRequest.setReqData(reqData);
                        // 调用服务
                        svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);

                        //配置角色数据展现
                        if (svcResponse.isSuccess()) {
                            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
                            name[j] = staffBean.getStrValue("Staff.StaffName");
                        }
                    }
                    noticeInfo.setStrValue("Row.createStaffName", name[0]);
                    noticeInfo.setStrValue("Row.toStaffName", name[1]);
                    returnList.add(noticeInfo.getRootNode());
                }
                modelMap.put("returnList", returnList);
            }
        }
        return new ModelAndView("/oframe/main/messageList", modelMap);
    }

    /**
     * 初始化未读消息界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void getMessageJson(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String count = request.getParameter("fetchCount");
        if (StringUtil.isEmptyOrNull(count)) {
            count = "1";
        }
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "NoticeInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "noticeReadStatus");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "0");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "toStaffId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", svcRequest.getReqStaffIdInt());
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        //
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "noticeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "createStaffId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "noticeContent");
        // 排序条件
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", "createTime");
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", "desc");
        opData.setStrValue(prePath + "PageInfo.pageSize", count);
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        String data = "[]";
        if (svcResponse.isSuccess()) {
            //获取签约控制启动状态
            XmlBean noticeInfoList = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            if (noticeInfoList != null) {
                count = noticeInfoList.getStrValue("OpResult.PageInfo.totalRecord");
                modelMap.put("count", count);
                int temp = noticeInfoList.getListNum("OpResult.PageData.Row");
                for (int i = 0; i < temp; i++) {
                    XmlBean noticeInfo = noticeInfoList.getBeanByPath("OpResult.PageData.Row[${i}]");
                    String createStaff = noticeInfo.getStrValue("Row.createStaffId");
                    svcRequest = RequestUtil.getSvcRequest(request);
                    // 数据
                    XmlBean reqData = new XmlBean();
                    String nodePath = "SvcCont.Staff.";
                    reqData.setValue(nodePath + "staffId", createStaff);
                    // 请求信息
                    svcRequest.setReqData(reqData);
                    // 调用服务
                    SvcResponse tempSvcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);
                    //配置角色数据展现
                    if (tempSvcResponse.isSuccess()) {
                        XmlBean staffBean = tempSvcResponse.getRspData().getBeanByPath("SvcCont.Staff");
                        createStaff = staffBean.getStrValue("Staff.StaffName");
                    }
                    noticeInfo.setStrValue("Row.createStaffName", createStaff);
                }
                data = noticeInfoList.getBeanByPath("OpResult.PageData").toJson();
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "messageData: ${data}")
    }

    /**
     * 根据名字获取cookie
     * @param request
     * @param name cookie名字
     * @return
     */
    private static String getCookieByName(HttpServletRequest request, String name) {
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
     * 查看系统当前版本、 以及依赖包版本
     * @param request
     * @param response
     * @return
     */
    public ModelAndView webVersion(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String title = Manifests.read("Implementation-Title");
        String version = Manifests.read("Implementation-Version");
        String vendor = Manifests.read("Implementation-Vendor-Id");
        String builtBy = Manifests.read("Built-By");
        String buildJdk = Manifests.read("Build-Jdk");
        String buildTime = Manifests.read("Build-Time");
        String classPath = Manifests.read("Class-Path");

        String[] dependenicesArr = classPath.split(" ");

        List dependOframe = new ArrayList();
        List dependEland = new ArrayList();
        for (int i = 0; i < dependenicesArr.length; i++) {
            String temp = dependenicesArr[i].trim();
            if (StringUtil.isNotEmptyOrNull(temp) && temp.startsWith("oframe")) {
                dependOframe.add(temp);
            }
            if (StringUtil.isNotEmptyOrNull(temp) && temp.startsWith("eland")) {
                dependEland.add(temp);
            }
        }

        modelMap.put("title", title);
        modelMap.put("version", version);
        modelMap.put("vendor", vendor);
        modelMap.put("builtBy", builtBy);
        modelMap.put("buildJdk", buildJdk);
        modelMap.put("buildTime", buildTime);
        modelMap.put("dependOframe", dependOframe);
        modelMap.put("dependEland", dependEland);

        return new ModelAndView("/oframe/main/webVersion", modelMap);
    }
}
