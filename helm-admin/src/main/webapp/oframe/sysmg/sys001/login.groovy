import com.shfb.oframe.core.util.cache.CacheManager
import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.Encrypt
import com.shfb.oframe.core.util.common.RandomStringUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.properties.PropertiesUtil
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.CacheSession
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.imageio.ImageIO
import javax.servlet.ServletOutputStream
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.awt.image.BufferedImage

class login extends GroovyController {

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        //验证cookie中是否有帐号、密码
        Object mapc = checkNameAndWord(request);
        //mapc返回map类型，代表有值，取出帐号密码，进行验证
        if (mapc instanceof HashMap) {
            Map<String, String> map = (Map<String, String>) mapc;
            //获取帐号
            String userName = map.get("userName");
            //获取密码
            String password = map.get("password");
            //验证帐号和密码
            String opAccept = RequestUtil.getOpAccept(DateUtil.getSysDate());
            String ipAddr = RequestUtil.getIpAddr(request);
            // 调用服务进行登录验证
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.setValue("Request.SvcCont.staffCode", userName);
            svcRequest.setValue("Request.SvcCont.password", password);
            svcRequest.setValue("Request.SvcCont.loginAccept", opAccept);
            svcRequest.setValue("Request.SvcCont.loginIpAddr", ipAddr);
            SvcResponse svcResponse = callService("staffService", "checkStaffLogin", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean rspData = svcResponse.getRspData();
                if (rspData == null) {
                    rspData = new XmlBean();
                }
                //是否过期
                String isExpire = rspData.getStrValue("SvcCont.isExpire");
                if (!StringUtil.isEqual("1", isExpire)) {//没有过期，则重定向，过期则走正常逻辑——跳转到登录页面
                    //重定向
                    return new ModelAndView("forward:/oframe/sysmg/sys001/login-staffLogin.gv?"
                            + "loginAccept=" + opAccept
                            + "&systemUserCode=" + userName
                            + "&password=" + password);
                }
            }
        }
        return new ModelAndView("/oframe/sysmg/sys001/login");
    }

    /**
     * 天恒信息管理登录界面
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initTh(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("/oframe/sysmg/sys001/login_th");
    }

    /**
     * 超时登录对话框
     * @param request 请求地址
     * @param response 响应信息
     * @return 返回处理界面
     */
    public ModelAndView initD(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("staffCode", request.getParameter("staffCode"));
        return new ModelAndView("/oframe/sysmg/sys001/login_d", modelMap);
    }

    public ModelAndView check(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入请求信息
        String userName = request.getParameter("systemUserCode");
        String password = request.getParameter("password");
        String validate = request.getParameter("validate");
        String opAccept = RequestUtil.getOpAccept(DateUtil.getSysDate());
        //使用缓存保存验证码信息
        String sessionId = request.getSession().getId();
        String randomWord = CacheManager.getInstance().get("RANDOMWORD_INFO", sessionId);
        if (randomWord == null) {
            randomWord = "";
        }
        // 删除换粗
        CacheManager.getInstance().remove("RANDOMWORD_INFO", sessionId);
        if (!randomWord.equalsIgnoreCase(validate)) {
            // 返回处理结果
            String result = """{"isSuccess": false, "loginAccept": "${opAccept}",
                         "errMsg": "验证码错误!"}""";
            // 输出
            ResponseUtil.printAjax(response, result);
            return;
        } else {
            String ipAddr = RequestUtil.getIpAddr(request);
            // 调用服务进行登录验证
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.setValue("Request.SvcCont.staffCode", userName);
            svcRequest.setValue("Request.SvcCont.password", password);
            svcRequest.setValue("Request.SvcCont.device", "pc");
            svcRequest.setValue("Request.SvcCont.loginAccept", opAccept);
            svcRequest.setValue("Request.SvcCont.loginIpAddr", ipAddr);
            SvcResponse svcResponse = callService("staffService", "checkStaffLogin", svcRequest);
            int needOutCnt = 0;
            if (svcResponse.isSuccess()) {
                // 获取需要强制退出的登录流水
                needOutCnt = svcResponse.getListNum("Response.SvcCont.Staff.NeedOut");
            }
            String isExpire = svcResponse.getStrValue("Response.SvcCont.isExpire")
            // 返回处理结果
            String result = """{"isSuccess": ${svcResponse.isSuccess()}, "loginAccept": "${opAccept}", "isExpire":
                "${isExpire}", "errMsg": "${svcResponse.errMsg}", "outNum": ${needOutCnt}}""";
            // 输出
            ResponseUtil.printAjax(response, result);
        }
    }


    public ModelAndView staffLogin(HttpServletRequest request, HttpServletResponse response) {
        //判断是否保持登陆标识
        boolean autoLoginFlag = false;
        String stayLogin = request.getParameter("stayLogin");
        if (StringUtil.isNotEmptyOrNull(stayLogin)) {
            autoLoginFlag = true;
        }
        // 获取输入请求信息
        String userName = request.getParameter("systemUserCode");
        String password = request.getParameter("password");
        String prjCd = request.getParameter("prjCd");
        String loginAccept = request.getParameter("loginAccept");
        String jumpMainFlag = request.getParameter("jumpMainFlag");
        String ipAddr = RequestUtil.getIpAddr(request);
        // 获取工号可以访问的项目工程，避免空指针问题
        if (StringUtil.isEmptyOrNull(prjCd) && StringUtil.isNotEmptyOrNull(userName)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean reqData = new XmlBean();
            // 查询条件
            reqData.setStrValue("SvcCont.staffCode", userName);
            reqData.setStrValue("SvcCont.orgId", "");
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
            if (StringUtil.isEmptyOrNull(prjCd) && prjMap.keySet().size() > 0) {
                prjCd = prjMap.keySet().toArray()[0];
            } else {
                prjCd = "0";
            }
        }

        // 调用服务进行登录验证
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffCode", userName);
        svcRequest.setValue("Request.SvcCont.password", password);
        svcRequest.setValue("Request.SvcCont.loginAccept", loginAccept);
        svcRequest.setValue("Request.SvcCont.loginIpAddr", ipAddr);
        svcRequest.setValue("Request.SvcCont.device", "pc");
        SvcResponse svcResponse = callService("staffService", "saveStaffLogin", svcRequest);
        // 从后台获取流水号
        if (svcResponse.isSuccess()) {
            // 向响应中设置cookie，记录登录流水
            Cookie cookie = new Cookie(CacheSession.SESSION_COOKIE_KEY, StringUtil.obj2Str(loginAccept));
            // 浏览器内存有效
            cookie.setMaxAge(-1);
            // 整个站点有效
            String path = request.getContextPath();
            // 处理WebLogic问题path等于空firefox不识别的问题
            if (StringUtil.isEmptyOrNull(path)) {
                path = "/";
            }
            cookie.setPath(path);
            // 添加到输出
            response.addCookie(cookie);
            // 保存登录信息
            SessionUtil sessionUtil = new SessionUtil(loginAccept, request);
            // 登录工号信息
            String staffId = svcResponse.getStrValue("Response.SvcCont.Staff.StaffId");
            String staffCd = svcResponse.getStrValue("Response.SvcCont.Staff.StaffCd");
            String staffName = svcResponse.getStrValue("Response.SvcCont.Staff.StaffName");
            String orgId = svcResponse.getStrValue("Response.SvcCont.Staff.OrgId");
            // 在Session中保存登录信息
            String loginInfo = """<LoginInfo>
                                    <StaffId>${staffId}</StaffId>
                                    <staffCode>${staffCd}</staffCode>
                                    <staffName>${staffName}</staffName>
                                    <orgId>${orgId}</orgId>
                                    <prjCd>${prjCd}</prjCd>
                               </LoginInfo>""";

            sessionUtil.setAttr(SessionUtil.LOGIN_IN_SESSION_KEY, loginInfo);

            // 获取需要强制退出的登录流水
            int needOutCnt = svcResponse.getListNum("Response.SvcCont.Staff.NeedOut");
            for (int i = 0; i < needOutCnt; i++) {
                String tempLoginAccept = svcResponse.getStrValue("Response.SvcCont.Staff.NeedOut[" + i + "].loginAccept");
                sessionUtil.removeOtherByKey(tempLoginAccept);
            }
        }
        // 是否跳转到主页面
        if ("0".equals(jumpMainFlag)) {
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        } else {
            if (autoLoginFlag) {
                //帐号、密码用竖线隔开并加密
                String loginInfoToDES = Encrypt.encryptToDES(userName + "|" + password);
                //加密后的帐号、密码存入cookie
                Cookie userCookie = new Cookie("USER", loginInfoToDES);
                //设置cookie有效期为一周
                userCookie.setMaxAge(60 * 60 * 24 * 7);
                //cookie有效路径是网站根目录
                userCookie.setPath(getStayPath(request));
                //保存
                response.addCookie(userCookie);
            }
            String from = request.getParameter("from");
            if (StringUtil.isEmptyOrNull(from)) {
                // 输出
                return new ModelAndView("redirect:/oframe/main/main-init.gv");
            } else {
                return new ModelAndView("redirect:" + from);
            }
        }
    }

    /**
     * 系统退出
     * @param request
     * @param response
     * @return
     */
    public ModelAndView staffLogout(HttpServletRequest request, HttpServletResponse response) {
        SessionUtil sessionUtil = new SessionUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.loginAccept", sessionUtil.getSessionKey());
        callService("staffService", "saveStaffLogout", svcRequest);
        sessionUtil.removeAll();
        /**退出时，清空cookie*/
        String path = getSessionPath(request);
        // 用户回话清除
        Cookie cookie = new Cookie(CacheSession.SESSION_COOKIE_KEY, "");
        //设置cookie有效期
        cookie.setMaxAge(0);
        //设置cookie有效路径
        cookie.setPath(path);
        response.addCookie(cookie);
        // 记住密码清楚
        Cookie userCookie = new Cookie("USER", null);
        //设置cookie有效期
        userCookie.setMaxAge(0);
        //设置cookie有效路径
        userCookie.setPath(getStayPath(request));
        response.addCookie(userCookie);
        return new ModelAndView("redirect:/");
    }

    /**
     * 获取随机验证码
     *
     * @param request 请求信息
     * @param response 响应信息
     */
    public void randomImage(HttpServletRequest request, HttpServletResponse response) {
        //使用缓存保存验证码信息
        String sessionId = request.getSession().getId();
        // 不缓存信息
        response.setContentType("image/jpg;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String randomWord = RandomStringUtil.randomAlphanumeric(4);
        BufferedImage randomImage = RandomStringUtil.randomImage(randomWord, 60, 23);
        ServletOutputStream outStream = response.getOutputStream();
        ImageIO.write(randomImage, "jpg", outStream);
        outStream.flush();
        outStream.close();
        CacheManager.getInstance().put("RANDOMWORD_INFO", sessionId, randomWord);
    }

    /**
     * 首页登录重置
     * @param request
     * @param response
     * @return
     */
    public ModelAndView loginResetPwd(HttpServletRequest request, HttpServletResponse response) {
        String oldPassWd = request.getParameter("oldPassWd")
        String passWord = request.getParameter("passWord")
        String systemUserCode = request.getParameter("systemUserCode")
        //校验 用户名 密码是否正确
        SvcResponse checkResponse = checkLogin(request, systemUserCode, oldPassWd, "", "");
        //正确后 登录跳转到主页面
        if (checkResponse.isSuccess()) {
            //修改密码
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opXml = new XmlBean();
            opXml.setStrValue("SvcCont.staffCode", systemUserCode)
            opXml.setStrValue("SvcCont.oldPassword", oldPassWd);
            opXml.setStrValue("SvcCont.newPassword", passWord);
            svcRequest.setReqData(opXml);
            SvcResponse resetResponse = callService("staffService", "changeStaffPwd", svcRequest)
            if (resetResponse.isSuccess()) {
                //修改成功，登录操作
                String opAccept = RequestUtil.getOpAccept(DateUtil.getSysDate());
                //设置登录流水
                request.setAttribute("loginAccept", opAccept)
                ResponseUtil.printSvcResponse(response, resetResponse,
                        "\"loginAccept\":\"" + opAccept + "\",\"systemUserCode\":\"" + systemUserCode + "\",\"passWord\":\"" + passWord + "\"");
            } else {
                ResponseUtil.printSvcResponse(response, resetResponse, "");
            }
        } else {
            //密码不正确
            ResponseUtil.printSvcResponse(response, checkResponse, "")
        }
    }

    private SvcResponse checkLogin(HttpServletRequest request, String userName, String password, String opAccept, String ipAddr) {
        // 调用服务进行登录验证
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffCode", userName);
        svcRequest.setValue("Request.SvcCont.password", password);
        svcRequest.setValue("Request.SvcCont.loginAccept", svcRequest.getReqId());
        svcRequest.setValue("Request.SvcCont.loginIpAddr", svcRequest.getIpAddr());
        return callService("staffService", "checkStaffLogin", svcRequest);
    }

    /**
     * 判断cookies中是否有帐号密码
     */
    public Object checkNameAndWord(HttpServletRequest request) {
        String userName = "";
        String password = "";
        Map<String, String> map = new HashMap<String, String>();
        //获取当前站点的所有Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {//对cookies中的数据进行遍历，找到用户名、密码的数据
                if (StringUtil.isEqual("USER", cookies[i].getName())) {
                    //帐号、密码串
                    String usersOfDEF = cookies[i].getValue();
                    //解密
                    String users = Encrypt.decryptByDES(usersOfDEF);
                    if (users.contains("|")) {
                        //解密后的帐号
                        userName = users.substring(0, users.indexOf("|"));
                        password = users.substring(users.indexOf("|") + 1, users.length());
                    }
                }
            }

        }
        if (StringUtil.isNotEmptyOrNull(userName) && StringUtil.isNotEmptyOrNull(password)) {
            map.put("userName", userName);
            map.put("password", password);
            return map;
        }
        return "false";
    }

    /**
     * 返回获取session路径
     * @return
     */
    private String getSessionPath(HttpServletRequest request) {
        // 整个站点有效
//        String path = PropertiesUtil.readString("oframe-web", "ROOT_COOKIE_PATH");
//        if (StringUtil.isEmptyOrNull(path)) {
//            path = request.getContextPath();
//        }
        String path = request.getContextPath();
        if (StringUtil.isEmptyOrNull(path)) {
            path = "/"
        }
        return path;
    }

    /**
     * 返回获取登录记住密码路
     * @return
     */
    private String getStayPath(HttpServletRequest request) {
        // 整个站点有效
//        String path = PropertiesUtil.readString("oframe-web", "STAY_COOKIE_PATH");
        String path = request.getContextPath() + "/oframe/sysmg/sys001/";
//        if (StringUtil.isEmptyOrNull(path)) {
//            path = request.getContextPath() + "/oframe/sysmg/sys001/";
//        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }
}