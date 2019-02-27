import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.excel.ExcelReader
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import org.apache.commons.collections.map.LinkedMap
import org.apache.poi.hssf.usermodel.HSSFRow
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys002 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys002/sys002", modelMap)
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求处理
        String method = request.getParameter("method");
        if (!"add".equals(method)) {
            //  重新增加区域
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            // 数据
            XmlBean reqData = new XmlBean();
            String nodePath = "SvcCont.Staff.";
            reqData.setValue(nodePath + "staffId", request.getParameter("staffId"));
            String staffCode = "";
            // 请求信息
            svcRequest.setReqData(reqData);
            // 调用服务
            SvcResponse svcResponse = SvcUtil.callSvc("staffService", "queryStaffInfo", svcRequest);

            //配置角色数据展现
            if (svcResponse.isSuccess()) {
                XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.Staff");
                staffCode = staffBean.getStrValue("Staff.StaffCd");
                modelMap.put("nodeInfo", staffBean.getRootNode());
                int roleCount = staffBean.getListNum("Staff.Roles.Role");
                List<Map<String, String>> roleList = new ArrayList<Map<String, String>>();
                for (int i = 0; i < roleCount; i++) {
                    nodePath = "Staff.Roles.Role[${i}].";
                    Map<String, String> item = new HashMap<String, String>();
                    item.put("roleCd", staffBean.getStrValue(nodePath + "RoleCd"));
                    item.put("roleName", staffBean.getStrValue(nodePath + "RoleName"));
                    roleList.add(item);
                }
                modelMap.put("roleList", roleList);

                int posCount = staffBean.getListNum("Staff.Poss.Pos");
                List<Map<String, String>> posList = new ArrayList<Map<String, String>>();
                for (int i = 0; i < posCount; i++) {
                    nodePath = "Staff.Poss.Pos[${i}].";
                    Map<String, String> posMap = new HashMap<String, String>();
                    posMap.put("posId", staffBean.getStrValue(nodePath + "posId"));
                    posMap.put("posName", staffBean.getStrValue(nodePath + "posName"));
                    posMap.put("posType", staffBean.getStrValue(nodePath + "posType"));
                    posMap.put("posDesc", staffBean.getStrValue(nodePath + "posDesc"));
                    posMap.put("mainPos", staffBean.getStrValue(nodePath + "mainPos"));
                    posMap.put("posOwner", staffBean.getStrValue(nodePath + "posOwner"));
                    posMap.put("orgId", staffBean.getStrValue(nodePath + "orgId"));
                    posMap.put("orgName", staffBean.getStrValue(nodePath + "orgName"));
                    posList.add(posMap);
                }
                modelMap.put("positionList", posList);
            }
        }
        modelMap.put("method", method);
        String openFrom = request.getParameter("openFrom");
        String redirectPage = "";
        Map<String, String> prjMap = new LinkedMap<String, String>();
        // 增加公司组织
        prjMap.put("0", "公司组织");
        modelMap.put("prjMap", prjMap);
        if ("openPos".equals(openFrom)) {
            //打开选择岗位页面
            redirectPage = "/oframe/sysmg/sys002/sys00202";
        } else {
            //初始化基本信息页面
            redirectPage = "/oframe/sysmg/sys002/sys00201";
        }
        return new ModelAndView(redirectPage, modelMap);
    }

    public void save(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        String method = request.getParameter("method");
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.Staff.";
        reqData.setValue(nodePath + "staffId", request.getParameter("staffId"));
        reqData.setValue(nodePath + "staffCode", request.getParameter("staffCode"));
        reqData.setValue(nodePath + "password", request.getParameter("password"));
        reqData.setValue(nodePath + "staffName", request.getParameter("staffName"));
        reqData.setValue(nodePath + "orgId", request.getParameter("orgId"));
        //
        reqData.setStrValue(nodePath + "statusCd", request.getParameter("statusCd"));
        reqData.setStrValue(nodePath + "staffTel", request.getParameter("staffTel"));
        reqData.setStrValue(nodePath + "notes", request.getParameter("notes"));
        reqData.setStrValue(nodePath + "multLogin", request.getParameter("multLogin"));
        reqData.setStrValue(nodePath + "loginIp", request.getParameter("loginIp"));

        reqData.setStrValue(nodePath + "staffEmail", request.getParameter("staffEmail"));
        reqData.setStrValue(nodePath + "staffMobile", request.getParameter("staffMobile"));
        reqData.setStrValue(nodePath + "isLocked", request.getParameter("isLocked"));
        reqData.setStrValue(nodePath + "lockedTime", DateUtil.getSysDate());

        String[] roleCds = request.getParameterValues("roleCd");
        int j = 0;
        if (roleCds != null) {
            for (int i = 0; i < roleCds.length; i++) {
                if (StringUtil.isEmptyOrNull(roleCds[i])) {
                    continue;
                }
                nodePath = "SvcCont.Staff.Roles.Role[${j++}].";
                reqData.setStrValue(nodePath + "roleCd", roleCds[i]);
            }
        }

        //拼接岗位信息
        String[] posIds = request.getParameterValues("posId");
        int m = 0;
        if (posIds != null) {
            for (int i = 0; i < posIds.length; i++) {
                if (StringUtil.isEmptyOrNull(posIds[i])) {
                    continue;
                }
                nodePath = "SvcCont.Staff.Poss.Pos[${m++}].";
                reqData.setStrValue(nodePath + "posId", posIds[i]);
                reqData.setStrValue(nodePath + "mainPos", request.getParameter("mainPos" + posIds[i]));
                reqData.setStrValue(nodePath + "posOwner", request.getParameter("posOwner" + posIds[i]));
            }
        }

        // 请求调用服务
        svcRequest.setReqData(reqData);
        String svcMetho = "addStaffInfo";
        if ("edit".equals(method)) {
            svcMetho = "updateStaffInfo";
        }
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("staffService", svcMetho, svcRequest);
        String jsonStr = "";
        if (svcResponse.isSuccess()) {
            //do something
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    public ModelAndView orgTree(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys002/orgTree", modelMap);
    }

    public ModelAndView roleTree(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys002/roleTree", modelMap)
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initRht(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys002/rhtTree", modelMap);
    }
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void rhtInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        String staffCode = request.getParameter("staffCode");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        SvcResponse svcResponse = callService("rhtTreeService", "queryTree", svcRequest);
        // 获取角色权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffCode", staffCode);
        SvcResponse staffResponse = callService("staffService", "queryStaffRht", svcRequest);

        boolean result = svcResponse.isSuccess() && staffResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + staffResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean roleRhtData = staffResponse.getRspData();

            int haveRhtCount = 0;
            if (roleRhtData != null) {
                haveRhtCount = roleRhtData.getListNum("SvcCont.Staff.Rights.Right")
            };
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Staff.Rights.Right[${i}].";
                roleMapList.add(roleRhtData.getStrValue(nodePath + "rhtId"));
            }

            XmlBean rhtTree = svcResponse.getBeanByPath("Response.SvcCont.RhtTree");
            if (rhtTree != null) {
                int count = rhtTree.getListNum("RhtTree.Node") - 1;
                for (int i = 0; i < count; i++) {
                    String id = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtId""");
                    String pId = rhtTree.getStrValue("""RhtTree.Node[${i}].uRhtId""");
                    String name = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtName""");
                    boolean haveRht = roleMapList.contains(id);
                    sb.append("""{ id: "${id}", pId: "${pId}", checked: "${haveRht}", name: "${
                        name
                    }",open: "true"},""");
                }
                // 拼接最后一个节点
                String id = rhtTree.getStrValue("""RhtTree.Node[${count}].rhtId""");
                String pId = rhtTree.getStrValue("""RhtTree.Node[${count}].uRhtId""");
                String name = rhtTree.getStrValue("""RhtTree.Node[${count}].rhtName""");
                boolean haveRht = roleMapList.contains(id);
                sb.append("""{ id: "${id}", pId: "${pId}", checked: "${haveRht}", name: "${name}",open: "true"},""");

            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void saveRht(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        String staffId = request.getParameter("staffId");
        String newRhtIds = request.getParameter("newRhtIds");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.Staff.rhtType", rhtType);
        reqData.setValue("SvcCont.Staff.staffId", staffId);
        String[] rhtIdArr = newRhtIds.split(",");
        if (rhtIdArr != null && !StringUtil.isEmptyOrNull(newRhtIds)) {
            int i = 0;
            for (String tempItem : rhtIdArr) {
                String nodePath = "SvcCont.Staff.Rhts.Rht[${i++}].";
                reqData.setStrValue(nodePath + "rhtId", tempItem);
            }
        }
        //
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("staffService", "updateStaffRht", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    public ModelAndView queryPosition(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取输入参数
        String orgId = request.getParameter("orgId");
        String prjCd = request.getParameter("prjCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.Org.Node.orgId", orgId);
        svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", prjCd);
        SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);

        boolean result = svcResponse.isSuccess();
        List<XmlNode> posMapList = new ArrayList<XmlNode>();
        if (result) {
            // 获取组织岗位信息
            XmlBean orgPosition = svcResponse.getRspData();

            int posCount = 0;
            if (orgPosition != null) {
                posCount = orgPosition.getListNum("SvcCont.Org.Positions.Position");
            };
            for (int i = 0; i < posCount; i++) {
                posMapList.add(orgPosition.getBeanByPath("SvcCont.Org.Positions.Position[" + i + "]").getRootNode());
            }
        }
        modelMap.put("posMapList", posMapList);
        return new ModelAndView("/oframe/sysmg/sys002/selectPos", modelMap);
    }

    public ModelAndView importStaff(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        MultipartFile localFile = super.getFile(request, "orgImportFiled");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;

        if (fileType.contains(".xls") || fileType.contains(".xlsx")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            File saveFile = ServerFile.createFile(remoteFileName);
            InputStream inputStream = null;
            try {
                inputStream = localFile.getInputStream();
            } catch (IOException e) {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (Exception d) {
                    }
                }
            }
            ExcelReader excelReader = new ExcelReader(inputStream, localFile.getName());
            excelReader.changeSheet("导出数据");
            int maxRow = excelReader.getRowCnt();
            int count = 1;
            String rootPath = "SvcCont.Staff";
            XmlBean xmlBean = new XmlBean();
            int staffNum = 0;
            while (count != maxRow) {
                HSSFRow row = excelReader.getRow(count);
                count++;
                if (row == null || StringUtil.isEmptyOrNull(row.getCell(0).getStringCellValue())) {
                    continue;
                }
                if (StringUtil.isNotEmptyOrNull(row.getCell(0).getStringCellValue()) && StringUtil.isEmptyOrNull(row.getCell(1).getStringCellValue())) {
                    xmlBean.setStrValue(rootPath + "[${staffNum}]." + "staffName", row.getCell(0));
                } else {
                    xmlBean.setStrValue(rootPath + "[${staffNum}]." + "staffName", row.getCell(1));
                }

                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "staffCode", row.getCell(0));
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "password", "111111");
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "orgPath", row.getCell(4));
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "statusCd", "");
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "staffTel", row.getCell(2));
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "notes", "");
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "multLogin", "1");
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "staffEmail", row.getCell(3));
                xmlBean.setStrValue(rootPath + "[${staffNum}]." + "staffMobile", "");
                xmlBean.setStrValue(rootPath + "[${staffNum++}]." + "isLocked", "0");
            }
            svcRequest.setReqData(xmlBean);
            // 调用服务，执行数据查询
            SvcResponse svcResponse = callService("staffService", "addStaffInfo", svcRequest);
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

    /********************************************************* 员工授权 ************************************************************/
    /** 员工授权页面 */
    public ModelAndView staffTreeInfo(HttpServletRequest request, HttpServletResponse response) {
        String staffId = request.getParameter("staffId");
        ModelMap modelMap = new ModelMap();
        modelMap.put("staffId", staffId);
        modelMap.put("DATA_RHT_DEF", getCfgCollection(request, "DATA_RHT_DEF", true));
        return new ModelAndView("/oframe/sysmg/sys002/staffRoleRht", modelMap);
    }

    /** 员工系统功能权限树 */
    public void staffRhtInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        String staffId = request.getParameter("staffId");
        String grantType = request.getParameter("grantType");
        String rhtUseType = request.getParameter("rhtUseType");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtUseType", rhtUseType);
        SvcResponse svcResponse = callService("rhtTreeService", "queryTree", svcRequest);
        // 获取 staff 权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffId", staffId);
        svcRequest.setValue("Request.SvcCont.Staff.grantType", grantType);
        SvcResponse staffResponse = callService("staffService", "queryStaffRht", svcRequest);
        //处理 服务出参
        boolean result = svcResponse.isSuccess() && staffResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + staffResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean RhtData = staffResponse.getRspData();
            int haveRhtCount = 0;
            if (RhtData != null) {
                haveRhtCount = RhtData.getListNum("SvcCont.Role.Rights.Right");
            }
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                roleMapList.add(RhtData.getStrValue(nodePath + "rhtId"));
            }

            // 获取 staff 自有 权限map
            int staffRhtCount = 0;
            if (RhtData != null) {
                staffRhtCount = RhtData.getListNum("SvcCont.Staff.Rights.Right");
            }
            List<String> staffRhtMapList = new ArrayList<String>(staffRhtCount);
            for (int i = 0; i < staffRhtCount; i++) {
                String nodePath = "SvcCont.Staff.Rights.Right[${i}].";
                staffRhtMapList.add(RhtData.getStrValue(nodePath + "rhtId"));
            }

            //获取 传入的rhtType类型 的整个权限树；过滤出该员工已有的、该员工所在角色已有的权限。
            XmlBean rhtTree = svcResponse.getBeanByPath("Response.SvcCont.RhtTree");
            if (rhtTree != null) {
                int count = rhtTree.getListNum("RhtTree.Node");
                for (int i = 0; i < count; i++) {
                    String id = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtId""");
                    String pId = rhtTree.getStrValue("""RhtTree.Node[${i}].uRhtId""");
                    String name = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtName""");
                    String rhtSubType = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtSubType""");
                    String nodeRhtUseType = rhtTree.getStrValue("""RhtTree.Node[${i}].rhtUseType""");
                    String iconSkin = "n_folder";
                    if ("2".equals(rhtSubType)) {
                        iconSkin = "func";
                    } else if ("3".equals(rhtSubType)) {
                        iconSkin = "opr";
                    }
                    // 是否显示选择框
                    String noCheck = "false";
                    if (!StringUtil.isEqual(nodeRhtUseType, rhtUseType) && !"-".equals(nodeRhtUseType)) {
                        noCheck = "true"
                    }
                    boolean haveRht = roleMapList.contains(id) || staffRhtMapList.contains(id);
                    boolean chkDisabled = roleMapList.contains(id);  //判断权限是否是 角色上的 是不可以取消 设置 chkDisabled：true
                    sb.append("""{ id: "${id}", rhtId:"${id}", pId: "${pId}", checked: "${haveRht}", prjCd:"0",
                             "nocheck":${noCheck}, "chkDisabled":${chkDisabled}, name:"${
                        name
                    }" ,open:"true", iconSkin:"${iconSkin}"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
    /** 保存员工 系统功能授权 */
    public void saveStaffRhtInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String rhtType = requestUtil.getStrParam("rhtType");
        String grantType = requestUtil.getStrParam("grantType");
        String staffId = requestUtil.getStrParam("staffId");
        String newRhtIds = requestUtil.getStrParam("newRhtIds");
        String prjCds = requestUtil.getStrParam("prjCds");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //获取 保存 授权信息。
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.Staff.staffId", staffId);
        reqData.setValue("SvcCont.Staff.grantType", grantType);
        String[] rhtIdArr = newRhtIds.split(",");
        String[] prjCdsArr = prjCds.split(",", rhtIdArr.length);
        if (rhtIdArr != null && !StringUtil.isEmptyOrNull(newRhtIds)) {
            for (int i = 0; i < rhtIdArr.length; i++) {
                String tempRhtId = rhtIdArr[i];
                String tempPrjCd = prjCdsArr[i];
                String nodePath = "SvcCont.Staff.Rhts.Rht[${i}].";
                reqData.setStrValue(nodePath + "rhtId", tempRhtId);
                reqData.setStrValue(nodePath + "prjCd", tempPrjCd);
                reqData.setStrValue(nodePath + "grantRht", "1");
            }
        }
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("staffService", "updateStaffRht", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 加载员工授权 系统数据 树 */
    public void staffSData(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String rhtType = request.getParameter("rhtType");
        String staffId = request.getParameter("staffId");
        String rhtUseType = request.getParameter("rhtUseType");
        String grantType = request.getParameter("grantType");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", rhtType);
        svcRequest.setValue("Request.SvcCont.rhtUseType", rhtUseType);
        SvcResponse svcResponse = callService("orgService", "queryTree", svcRequest);
        // 获取 staff 权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffId", staffId);
        svcRequest.setValue("Request.SvcCont.Staff.grantType", grantType);
        SvcResponse staffResponse = callService("staffService", "queryStaffRht", svcRequest);
        boolean result = svcResponse.isSuccess() && staffResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + staffResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean RhtData = staffResponse.getRspData();
            int haveRhtCount = 0;
            if (RhtData != null) {
                haveRhtCount = RhtData.getListNum("SvcCont.Role.Rights.Right");
            }
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                roleMapList.add(RhtData.getStrValue(nodePath + "rhtId"));
            }

            // 获取 staff 自有 权限map
            int staffRhtCount = 0;
            if (RhtData != null) {
                staffRhtCount = RhtData.getListNum("SvcCont.Staff.Rights.Right");
            }
            List<String> staffRhtMapList = new ArrayList<String>(staffRhtCount);
            for (int i = 0; i < staffRhtCount; i++) {
                String nodePath = "SvcCont.Staff.Rights.Right[${i}].";
                staffRhtMapList.add(RhtData.getStrValue(nodePath + "rhtId"));
            }

            XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
            int count = treeBean.getListNum("SvcCont.TreeNode");
            for (int i = 0; i < count; i++) {
                String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                String pId = treeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                String nodePath = treeBean.getStrValue("""SvcCont.TreeNode[${i}].nodePath""");
                String iconSkin = "folder";
                int countPath = nodePath.count("/");
                boolean isOpen = false;
                if (countPath == 1) {
                    iconSkin = "home";
                    isOpen = true;
                } else if (countPath == 2) {
                    iconSkin = "cmp";
                }
                boolean haveRht = roleMapList.contains(id) || staffRhtMapList.contains(id);
                boolean chkDisabled = roleMapList.contains(id);  //判断权限是否是 角色上的 是不可以取消 设置 chkDisabled：true
                sb.append("""{ id: "${id}", rhtId:"${id}", pId: "${pId}", checked: "${haveRht}",prjCd:"0", iconSkin: "${
                    iconSkin
                }", "chkDisabled":${chkDisabled}, name: "${name}",open: "${
                    isOpen
                }"},""");
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 加载员工授权 项目功能 树 */
    public void prjFunc(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String staffId = request.getParameter("staffId");
        String grantType = request.getParameter("grantType");

        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.prjCd", "0");
        reqData.setStrValue("SvcCont.Org.orgId", "");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryOrgTreeForStaffPrj", svcRequest);

        // 获取 staff 权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffId", staffId);
        svcRequest.setValue("Request.SvcCont.Staff.grantType", grantType);
        SvcResponse staffResponse = callService("staffService", "queryStaffRht", svcRequest);

        boolean result = svcResponse.isSuccess() && staffResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + staffResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean RhtData = staffResponse.getRspData();
            int haveRhtCount = 0;
            if (RhtData != null) {
                haveRhtCount = RhtData.getListNum("SvcCont.Role.Rights.Right");
            }
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                String tempPrjCd = RhtData.getStrValue(nodePath + "prjCd");
                String tempRhtId = RhtData.getStrValue(nodePath + "rhtId");
                roleMapList.add(tempPrjCd + "-" + tempRhtId);
            }

            // 获取 staff 自有 权限map
            int staffRhtCount = 0;
            if (RhtData != null) {
                staffRhtCount = RhtData.getListNum("SvcCont.Staff.Rights.Right");
            }
            List<String> staffRhtMapList = new ArrayList<String>(staffRhtCount);
            for (int i = 0; i < staffRhtCount; i++) {
                String nodePath = "SvcCont.Staff.Rights.Right[${i}].";
                String tempPrjCd = RhtData.getStrValue(nodePath + "prjCd");
                String tempRhtId = RhtData.getStrValue(nodePath + "rhtId");
                staffRhtMapList.add(tempPrjCd + "-" + tempRhtId);
            }

            // 获取公司组织树
            XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
            if (treeBean != null) {
                int count = treeBean.getListNum("SvcCont.TreeNode");
                for (int i = 0; i < count; i++) {
                    String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                    String pId = treeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                    String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                    String nodePath = treeBean.getStrValue("""SvcCont.TreeNode[${i}].nodePath""");
                    String iconSkin = "folder";
                    int countPath = nodePath.count("/");
                    if (countPath == 1) {
                        iconSkin = "home";
                    } else if (countPath == 2) {
                        iconSkin = "cmp";
                    }
                    sb.append("""{ id: "${id}", "nocheck": true, iconSkin: "${iconSkin}",
                        pId: "${pId}", name: "${name}",open: "true"},""");
                }
            }

            /* 调用服务获取工号可管理的项目信息 */
            svcRequest = RequestUtil.getSvcRequest(request);
            String prePath = "SvcCont.staffCode";
            reqData = new XmlBean();
            reqData.setStrValue(prePath, svcRequest.getReqStaffCd());
            reqData.setStrValue("SvcCont.orgId", request.getParameter("orgId"));
            // 调用服务查询实体全部属性
            svcRequest.setReqData(reqData);
            svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            Map<String, XmlBean> prjMap = new LinkedHashMap<String, XmlBean>();
            if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        prjMap.put(xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"), xmlBean.getBeanByPath("CmpPrjs.CmpPrj[${i}]"))
                    }
                }
            }

            // 处理项目组织
            for (Map.Entry<String, XmlBean> tempPrjBean : prjMap.entrySet()) {
                String iterPrjCd = tempPrjBean.getKey();
                String iterRoleCd = tempPrjBean.getValue().getStrValue("CmpPrj.funcRole");
                svcRequest = RequestUtil.getSvcRequest(request);
                reqData = new XmlBean();
                reqData.setStrValue("SvcCont.Role.prjCd", iterPrjCd);
                reqData.setStrValue("SvcCont.Role.roleCd", iterRoleCd);
                reqData.setStrValue("SvcCont.Role.grantType", grantType);
                svcRequest.setReqData(reqData);
                svcResponse = callService("roleService", "queryRoleRhtTree", svcRequest);
                if (!svcResponse.isSuccess()) {
                    result = svcResponse.isSuccess();
                    errMsg = svcResponse.getErrMsg();
                    break;
                }
                // 获取公司组织树
                XmlBean tempNodeBean = svcResponse.getBeanByPath("Response.SvcCont.RhtTree");
                if (tempNodeBean == null) {
                    continue;
                }
                int tempCount = tempNodeBean.getListNum("RhtTree.Node");
                for (int i = 0; i < tempCount; i++) {
                    XmlBean tempBean = tempNodeBean.getBeanByPath("RhtTree.Node[${i}]");
                    String id = tempBean.getStrValue("Node.rhtId");
                    String pId = tempBean.getStrValue("Node.uRhtId");
                    String name = tempBean.getStrValue("Node.rhtName");
                    String rhtSubType = tempBean.getStrValue("""Node.rhtSubType""");
                    String iconSkin = "n_folder";
                    if ("2".equals(rhtSubType)) {
                        iconSkin = "func";
                    } else if ("3".equals(rhtSubType)) {
                        iconSkin = "opr";
                    }
                    if (StringUtil.isEmptyOrNull(pId) || "0".equals(pId)) {
                        pId = tempPrjBean.getValue().getStrValue("CmpPrj.ownOrg");
                        name = tempPrjBean.getValue().getStrValue("CmpPrj.prjName");
                    } else {
                        pId = iterPrjCd + "-" + pId;
                    }
                    String idKey = iterPrjCd + "-" + id;
                    boolean haveRht = roleMapList.contains(idKey) || staffRhtMapList.contains(idKey);
                    boolean chkDisabled = roleMapList.contains(idKey);  //判断权限是否是 角色上的 是不可以取消 设置 chkDisabled：true
                    sb.append("""{ id: "${iterPrjCd}-${id}", rhtId:"${id}", iconSkin: "folder", prjCd: "${iterPrjCd}",
                      iconSkin:"${iconSkin}", checked: ${haveRht}, "chkDisabled":${chkDisabled}, pId: "${pId}",name: "${
                        name
                    }"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 项目 居民数据权限 */
    public void staffDataRht(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String staffId = request.getParameter("staffId");
        String grantType = request.getParameter("grantType");

        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Org.prjCd", "0");
        reqData.setStrValue("SvcCont.Org.orgId", "");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("orgService", "queryOrgTreeForStaffPrj", svcRequest);

        // 获取 staff 权限
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.staffId", staffId);
        svcRequest.setValue("Request.SvcCont.Staff.grantType", grantType);
        SvcResponse staffResponse = callService("staffService", "queryStaffRht", svcRequest);

        boolean result = svcResponse.isSuccess() && staffResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg() + staffResponse.getErrMsg();
        StringBuilder sb = new StringBuilder();
        if (result) {
            // 获取角色权限map
            XmlBean RhtData = staffResponse.getRspData();
            int haveRhtCount = 0;
            if (RhtData != null) {
                haveRhtCount = RhtData.getListNum("SvcCont.Role.Rights.Right");
            }
            List<String> roleMapList = new ArrayList<String>(haveRhtCount);
            for (int i = 0; i < haveRhtCount; i++) {
                String nodePath = "SvcCont.Role.Rights.Right[${i}].";
                roleMapList.add(RhtData.getStrValue(nodePath + "rhtId"));
            }

            // 获取 staff 自有 权限map
            int staffRhtCount = 0;
            if (RhtData != null) {
                staffRhtCount = RhtData.getListNum("SvcCont.Staff.Rights.Right");
            }
            List<String> staffRhtMapList = new ArrayList<String>(staffRhtCount);
            for (int i = 0; i < staffRhtCount; i++) {
                String nodePath = "SvcCont.Staff.Rights.Right[${i}].";
                staffRhtMapList.add(RhtData.getStrValue(nodePath + "rhtId"));
            }

            // 获取公司组织树
            XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
            if (treeBean != null) {
                int count = treeBean.getListNum("SvcCont.TreeNode");
                for (int i = 0; i < count; i++) {
                    String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                    String pId = treeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                    String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                    String nodePath = treeBean.getStrValue("""SvcCont.TreeNode[${i}].nodePath""");
                    String iconSkin = "folder";
                    int countPath = nodePath.count("/");
                    if (countPath == 1) {
                        iconSkin = "home";
                    } else if (countPath == 2) {
                        iconSkin = "cmp";
                    }
                    sb.append("""{ id: "${id}", "nocheck": true, iconSkin: "${iconSkin}",
                        pId: "${pId}", name: "${name}",open: "true"},""");
                }
            }

            /* 调用服务获取工号可管理的项目信息 */
            svcRequest = RequestUtil.getSvcRequest(request);
            String prePath = "SvcCont.staffCode";
            reqData = new XmlBean();
            // 查询条件
            reqData.setStrValue(prePath, svcRequest.getReqStaffCd());
            reqData.setStrValue("SvcCont.orgId", request.getParameter("orgId"));
            // 调用服务查询实体全部属性
            svcRequest.setReqData(reqData);
            svcResponse = callService("staffService", "queryStaffProject", svcRequest);
            Map<String, XmlBean> prjMap = new LinkedHashMap<String, XmlBean>();
            if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
                XmlBean xmlBean = svcResponse.getRspData().getBeanByPath("SvcCont.CmpPrjs");
                if (null != xmlBean) {
                    int cmpPrjCount = xmlBean.getListNum("CmpPrjs.CmpPrj");
                    for (int i = 0; i < cmpPrjCount; i++) {
                        prjMap.put(xmlBean.getStrValue("CmpPrjs.CmpPrj[${i}].prjCd"),
                                xmlBean.getBeanByPath("CmpPrjs.CmpPrj[${i}]"))
                    }
                }
            }

            // 处理项目组织
            for (String tempPrjCd : prjMap.keySet()) {
                svcRequest = RequestUtil.getSvcRequest(request);
                reqData = new XmlBean();
                reqData.setStrValue("SvcCont.Org.prjCd", tempPrjCd);
                reqData.setStrValue("SvcCont.Org.orgId", "");
                svcRequest.setReqData(reqData);
                svcResponse = callService("orgService", "queryTree", svcRequest);
                if (!svcResponse.isSuccess()) {
                    result = svcResponse.isSuccess();
                    errMsg = svcResponse.getErrMsg();
                    break;
                }
                // 获取公司组织树
                XmlBean tempNodeBean = svcResponse.getBeanByPath("Response.SvcCont");
                int tempCount = 0;
                if (tempNodeBean != null) {
                    tempCount = tempNodeBean.getListNum("SvcCont.TreeNode");
                }
                for (int i = 0; i < tempCount; i++) {
                    String id = tempNodeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                    String pId = tempNodeBean.getStrValue("""SvcCont.TreeNode[${i}].upNodeId""");
                    String name = tempNodeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                    if (StringUtil.isEmptyOrNull(pId)) {
                        pId = prjMap.get(tempPrjCd).getStrValue("CmpPrj.ownOrg");
                        name = prjMap.get(tempPrjCd).getStrValue("CmpPrj.prjName");
                    }
                    boolean haveRht = roleMapList.contains(id) || staffRhtMapList.contains(id);
                    boolean chkDisabled = roleMapList.contains(id);  //判断权限是否是 角色上的 是不可以取消 设置 chkDisabled：true
                    sb.append("""{ id: "${id}", rhtId: "${id}", checked: ${haveRht}, prjCd: "${
                        tempPrjCd
                    }", "chkDisabled":${chkDisabled},
                                iconSkin: "folder",pId: "${pId}",name: "${name}"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 查询员工
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void queryStaff(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        String staffPrjCd = requestUtil.getStrParam("staffPrjCd");
        String staffName = requestUtil.getStrParam("staffName");

        String reqPath = "SvcCont.";
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue(reqPath + "PageInfo.currentPage", "1");
        reqData.setValue(reqPath + "PageInfo.pageSize", "100");
        // 排序信息
        reqData.setValue(reqPath + "SortInfo.sortColumn", "staff_name");
        reqData.setValue(reqPath + "SortInfo.sortOrder", "desc");
        // SQL信息
        reqData.setStrValue(reqPath + "QuerySvc.subSvcName", "sys002");
        reqData.setStrValue(reqPath + "ParamInfo.Param[0].name", "staffName");
        reqData.setStrValue(reqPath + "ParamInfo.Param[0].value", staffName);
        // 非系统岗位
        if (!"0".equals(staffPrjCd)) {
            reqData.setStrValue(reqPath + "ParamInfo.Param[1].name", "staffPrjCd");
            reqData.setStrValue(reqPath + "ParamInfo.Param[1].value", staffPrjCd);
        }
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("pageService", "queryPageData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            List returnList = rspData.getValue("SvcCont.PageData");
            List<Map<String, String>> result = new ArrayList<Map<String, String>>();
            for (int i = 0; i < returnList.size(); i++) {
                Map tempRow = returnList.get(i);
                Map<String, String> temp = new HashMap<String, String>();
                temp.put("key", tempRow.get("staff_code"));
                temp.put("staffId", tempRow.get("staff_id"));
                temp.put("staffCd", tempRow.get("staff_code"));
                temp.put("value", tempRow.get("staff_name"));
                result.add(temp);
            }
            ResponseUtil.print(response, JSONArray.fromObject(result).toString());
        }
    }
}
