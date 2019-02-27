import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class sys005 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 可用项目
        Map map = new LinkedHashMap();
        map.put("0", "系统基础平台");
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CmpPrj");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "prjCd");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "prjName");
        // 调用服务查询数据
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean prjInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (prjInfo != null) {
                int count = prjInfo.getListNum("PageData.Row")
                for (int i = 0; i < count; i++) {
                    map.put(prjInfo.getStrValue("PageData.Row[" + i + "].prjCd"), prjInfo.getStrValue("PageData.Row[" + i + "].prjName"));
                }
            }
        }
        // 返回可用项目
        modelMap.put("prjCdMap", map);
        return new ModelAndView("/oframe/sysmg/sys005/sys005", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void data(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String itemCd = requestUtil.getStrParam("itemCd");
        String prjCd = requestUtil.getStrParam("prjCd");
        // 处理结果
        Map<String, String> mapResult = getCfgCollection(request, itemCd, true, NumberUtil.getIntFromObj(prjCd));
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (Map.Entry<String, String> entry : mapResult.entrySet()) {
            Map<String, String> temp = new HashMap<String, String>();
            temp.put("key", entry.key);
            temp.put("value", entry.value);
            result.add(temp);
        }
        ResponseUtil.print(response, JSONArray.fromObject(result).toString());
    }

    /**
     * 树形结果
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void treeData(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String itemCd = requestUtil.getStrParam("itemCd");
        String prjCd = requestUtil.getStrParam("prjCd");
        String sltData = requestUtil.getStrParam("sltData");
        String treeCheck = requestUtil.getStrParam("treeCheck");
        List<String> checkedIds = new ArrayList<>();
        if (StringUtil.isNotEmptyOrNull(sltData)) {
            checkedIds = Arrays.asList(sltData.split(","));
        }
        // 处理结果
        Map<String, String> mapResult = getCfgCollection(request, itemCd, true, NumberUtil.getIntFromObj(prjCd));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> mapEntry : mapResult.entrySet()) {
            String id = mapEntry.getKey();
            String pId = "";
            if (id.length() == 3) {
                pId = "";
            } else {
                pId = id.substring(0, id.length() - 3);
            }
            // 是否叶子节点
            String name = mapEntry.getValue();
            //
            boolean nocheck = true;
            if ("true".equals(treeCheck)) {
                nocheck = false;
            }
            boolean isCheck = false;
            if (checkedIds.contains(id)) {
                isCheck = true;
            }
            sb.append("""{ id: "${id}", iconSkin: "",nocheck:"${nocheck}",checked: "${isCheck}", pId: "${
                pId
            }", name: "${name}",open: "true"},""");
        }
        String jsonStr = """{success: "true", errMsg: "", resultMap: {treeJson: [${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
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
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 请求处理
        String method = request.getParameter("method");
        log.debug("method:" + method);
        if (!"add".equals(method)) {
            // 数据
            XmlBean reqData = new XmlBean();
            String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
            reqData.setStrValue(nodePath + "itemCd", request.getParameter("itemCd"));
            reqData.setValue(nodePath + "prjCd", request.getParameter("cfgPrjCd"));
            // 请求信息
            svcRequest.setReqData(reqData);
            // 调用服务
            SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
            //
            if (svcResponse.isSuccess()) {
                XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
                modelMap.put("nodeInfo", staffBean.getRootNode());
                int roleCount = staffBean.getListNum("SysCfg.Values.Value");
                List<Map<String, String>> valueList = new ArrayList<Map<String, String>>();
                for (int i = 0; i < roleCount; i++) {
                    nodePath = "SysCfg.Values.Value[${i}].";
                    Map<String, String> item = new HashMap<String, String>();
                    item.put("valueCd", staffBean.getStrValue(nodePath + "valueCd"));
                    item.put("valueName", staffBean.getStrValue(nodePath + "valueName"));
                    item.put("notes", staffBean.getStrValue(nodePath + "notes"));
                    valueList.add(item);
                }
                modelMap.put("valueList", valueList);
            }
        }
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CmpPrj");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "prjCd");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "prjName");
        // 调用服务查询数据
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean prjInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (prjInfo != null) {
                int count = prjInfo.getListNum("PageData.Row")
                Map map = new LinkedHashMap();
                map.put("0", "系统基础平台");
                for (int i = 0; i < count; i++) {
                    map.put(prjInfo.getStrValue("PageData.Row[" + i + "].prjCd"), prjInfo.getStrValue("PageData.Row[" + i + "].prjName"));
                }
                modelMap.put("prjCdMap", map);
            }
        }

        modelMap.put("method", method);
        return new ModelAndView("/oframe/sysmg/sys005/sys00501", modelMap)
    }

    /**
     * 单项信息配置
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView config(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        reqData.setValue(nodePath + "itemCd", request.getParameter("itemCd"));
        reqData.setValue(nodePath + "prjCd", request.getParameter("prjCd"));
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            modelMap.put("nodeInfo", staffBean.getRootNode());
            int roleCount = staffBean.getListNum("SysCfg.Values.Value");
            List<Map<String, String>> valueList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < roleCount; i++) {
                nodePath = "SysCfg.Values.Value[${i}].";
                Map<String, String> item = new HashMap<String, String>();
                item.put("valueCd", staffBean.getStrValue(nodePath + "valueCd"));
                item.put("valueName", staffBean.getStrValue(nodePath + "valueName"));
                item.put("notes", staffBean.getStrValue(nodePath + "notes"));
                valueList.add(item);
            }
            modelMap.put("valueList", valueList);
        }
        return new ModelAndView("/oframe/sysmg/sys005/sys00502", modelMap)
    }

    public void save(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        String method = request.getParameter("method");
        //  重新增加区域
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 数据
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        reqData.setValue(nodePath + "itemCd", request.getParameter("itemCd"));
        reqData.setValue(nodePath + "itemName", request.getParameter("itemName"));
        reqData.setValue(nodePath + "note", request.getParameter("note"));
        reqData.setValue(nodePath + "dftValue", request.getParameter("dftValue"));
        reqData.setValue(nodePath + "valueType", request.getParameter("valueType"));
        reqData.setValue(nodePath + "statusCd", request.getParameter("statusCd"));
        reqData.setValue(nodePath + "prjCd", request.getParameter("cfgPrjCd"));
        reqData.setValue(nodePath + "itemUseType", request.getParameter("itemUseType"));
        reqData.setValue(nodePath + "withEmpty", request.getParameter("withEmpty"));

        String[] valueCds = request.getParameterValues("valueCd");
        String[] valueNames = request.getParameterValues("valueName");
        String[] valueNotes = request.getParameterValues("notes");
        int j = 0;
        if (valueCds != null) {
            for (int i = 0; i < valueCds.length; i++) {
                if (StringUtil.isEmptyOrNull(valueCds[i])) {
                    continue;
                }
                nodePath = "SvcCont.SysCfgs.SysCfg[0].Values.Value[${j++}].";
                reqData.setStrValue(nodePath + "valueCd", valueCds[i]);
                reqData.setStrValue(nodePath + "valueName", valueNames[i]);
                reqData.setStrValue(nodePath + "notes", valueNotes[i]);
            }
        }
        // 请求调用服务
        svcRequest.setReqData(reqData);
        String svcMetho = "saveSysCfgData";
        // 调用服务
        SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", svcMetho, svcRequest);
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    public void delete(HttpServletRequest request, HttpServletResponse response) {
        //  重新增加区域
       RequestUtil requestUtil = new RequestUtil(request);
        String[] itemCds = requestUtil.getStrParam("itemCd").split(",")
        String[] cfgPrjCds =  requestUtil.getStrParam("cfgPrjCd").split(",")
        // 数据
        SvcResponse svcResponse
        for (int i = 0; i < itemCds.length; i++) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean reqData = new XmlBean();
            String nodePath = "SvcCont.SysCfg.";
            reqData.setValue(nodePath + "itemCd", itemCds[i]);
            reqData.setValue(nodePath + "prjCd", cfgPrjCds[i]);
            // 请求调用服务
            svcRequest.setReqData(reqData);
            // 调用服务
            svcResponse = SvcUtil.callSvc("sysCfgService", "deleteSysCfgData", svcRequest);
            if (!svcResponse.success) {
                break;
            }
        }
        if (svcResponse != null) {
            // 返回处理结果
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        } else {
            // 输出json格式化工具
            printAjax(response, """{"success":false, "errMsg": "没有数据"}""");
        }
    }

    /**
     * 导入XML配置参数
     * @param request 用户请求
     * @param response 导入处理结果
     */
    public void saveImportData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "importSysCfgFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;
        if (fileType.contains(".xml")) {
            try {
                String fileText = new InputStreamReader(localFile.getInputStream(), "UTF-8").getText();
                byte[] bytes = fileText.getBytes("utf-8");
                if (bytes.length > 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    fileText = new String(bytes, 3, bytes.length - 3, "utf-8");
                }
                fileText = fileText.replaceAll("\\\$\\{prjCd\\}", StringUtil.obj2Str(svcRequest.getPrjCd()));
                // 请求参数
                XmlBean reqData = new XmlBean(fileText);
                svcRequest.setReqData(reqData);
                SvcResponse svcResponse = SvcUtil.callSvc("sysCfgService", "saveSysCfgData", svcRequest);
                if (svcResponse.isSuccess()) {
                    result = true;
                } else {
                    errMsg = svcResponse.getErrMsg();
                }
            } catch (Exception e) {
                log.error("导入文件读取失败,请确认文件内容是否正确", e);
                errMsg = "导入文件读取失败，请确认文件内容是否正确";
            }
        } else {
            errMsg = "文件格式不正确";
        }
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

//导出XML配置参数
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        //导出先获取要导出的ids
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        XmlBean reqData = new XmlBean();
        String strCfgCds = requestUtil.getStrParam("cfgCds");
        String cfgPrjCd = requestUtil.getStrParam("cfgPrjCds");
        String[] cfgCds = strCfgCds.split(",");
        String[] cfgPrjCds = cfgPrjCd.split(",");
        if (StringUtil.isNotEmptyOrNull(strCfgCds)) {
            for (int i = 0; i < cfgCds.length; i++) {
                reqData.setStrValue("SvcCont.SysCfgs.SysCfg[${i}].itemCd", cfgCds[i]);
                reqData.setStrValue("SvcCont.SysCfgs.SysCfg[${i}].prjCd", cfgPrjCds[i]);
            }
        }
        //调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        //处理返回结果
        String data = "{}";
        if (svcResponse.isSuccess()) {
            XmlBean resData = svcResponse.getRspData();
            String export = resData.getBeanByPath("SvcCont").toString();

            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(export, "UTF-8");
            data = """{\"remoteFile\":\"${filePath}\","clientFile":"${fileName}\"}"""
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """ \"data\": ${data}""");
    }

}
