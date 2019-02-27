import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class mb003 extends GroovyController {

    /**
     * 区域组织统计面板
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("/eland/mb/mb003/mb003");
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void regSummary(HttpServletRequest request, HttpServletResponse response) {
        // 根据项目查询
        SvcRequest svcRequest = null;
        Map<String, String> oldHsColorMap = new LinkedHashMap<String, String>();
        if ("0".equals(request.getParameter("picType"))) {
            svcRequest = RequestUtil.getSvcRequest(request);
            // 数据
            XmlBean reqData = new XmlBean();
            reqData.setValue("SvcCont.prjCd", request.getParameter("prjCd"));
            // 调用项目配置颜色
            svcRequest.setReqData(reqData);
            // 调用服务
            SvcResponse svcResponse = SvcUtil.callSvc("projectService", "queryProject", svcRequest);
            if (svcResponse.isSuccess()) {
                List<Map<String, String>> tempList = svcResponse.getRspData().getBeanByPath(
                        "SvcCont.Project.PrjCfg").getValue("PrjCfg.oldHsColorList");
                for (Map<String, String> tempItem : tempList) {
                    oldHsColorMap.put(tempItem.get("valueName"), tempItem.get("color"));
                }
            }
        }
        // 请求
        svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // SQL信息
        String nodePrePath = "SvcCont.PrjReg.";
        reqData.setStrValue(nodePrePath + "prjCd", requestUtil.getStrParam("prjCd"));
        reqData.setStrValue(nodePrePath + "regUseType", "1");
        reqData.setStrValue(nodePrePath + "regId", requestUtil.getStrParam("regId"));
        reqData.setStrValue(nodePrePath + "prjOrgId", requestUtil.getStrParam("prjOrgId"));
        String picType = request.getParameter("picType");
        // 查询条件
        int k = 0;
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        for (Map.Entry tempItem : paramMap) {
            reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].name", tempItem.getKey());
            reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].value", tempItem.getValue());
            k++;
        }
        // 查询SQL
        reqData.setStrValue("SvcCont.QuerySvc.subSvcName", "hs005020${picType}Chart");
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("chartService", "queryRegSummary", svcRequest);
        // 添加分页查询数据参数
        if (svcResponse.isSuccess()) {
            ModelMap modelMap = new ModelMap();
            //
            XmlBean rspData = svcResponse.getRspData();
            // 获取当前区域信息
            String cRegName = rspData.getStrValue(nodePrePath + "regName");
            String cRegId = rspData.getStrValue(nodePrePath + "regId");
            String upRegId = rspData.getStrValue(nodePrePath + "upRegId");
            // 获取子区域数量
            int subRegCount = rspData.getListNum(nodePrePath + "SubRegs.SubReg")

            // 输出图标数据
            List<String> group1 = new ArrayList<String>();
            Set<String> group2 = new LinkedHashSet<String>();
            // 状态， 区域， 值
            Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();

            // 循环获取所有的状态
            // 循环列表
            for (int i = 0; i < subRegCount; i++) {
                List<Map<String, Object>> chartData = rspData.getValue(nodePrePath + "SubRegs.SubReg[" + i + "].chartData");
                String regName = rspData.getValue(nodePrePath + "SubRegs.SubReg[" + i + "].regName");
                group1.add(regName);
                for (Map<String, Object> row : chartData) {
                    // 增加状态类别
                    String colName = row.get("col_name");
                    String colValue = row.get("col_value")
                    group2.add(colName);
                    Map<String, String> colValueMap = dataMap.get(colName);
                    if (colValueMap == null) {
                        colValueMap = new HashMap<String, String>();
                        dataMap.put(colName, colValueMap);
                    }
                    colValueMap.put(regName, colValue);
                }
            }

            // 获取每一个状态在各个分组的数据
            StringBuilder dataSb = new StringBuilder("[");
            int m = 0;
            for (String colName : group2) {
                // 循环状态在各个组的数据，没有为0
                List<String> tempItem = new ArrayList<>();
                Map<String, String> colValueMap = dataMap.get(colName);
                for (String regName : group1) {
                    String value = 0;
                    if (colValueMap != null) {
                        value = colValueMap.get(regName);
                    }
                    if (value == null) {
                        value = 0;
                    }
                    tempItem.add(value);
                }
                String color = oldHsColorMap.get(colName);
                if (StringUtil.isEmptyOrNull(color)) {
                    // 颜色不指定
                    dataSb.append("""{name: '${colName}',type:'bar', data: ${
                        JSONArray.fromObject(tempItem)
                    }},""");
                } else {
                    // 指定颜色
                    dataSb.append("""{name: '${colName}',type:'bar', data: ${
                        JSONArray.fromObject(tempItem)
                    }},""");
                }
            }
            if (dataSb.length() > 1) {
                dataSb.deleteCharAt(dataSb.length() - 1);
            }
            dataSb.append("]");
            // 转换输出
            Map<String, String> result = new HashMap<String, String>();
            result.put("success", "true");
            result.put("errMsg", svcResponse.errMsg);
            result.put("upRegName", cRegName);
            result.put("group", group2);
            result.put("upRegId", upRegId);
            // 状态作为横坐标
            result.put("categories", JSONArray.fromObject(group1));
            result.put("series", dataSb.toString());
            ResponseUtil.printAjax(response, String.valueOf(JSONObject.fromObject(result)));
        }
    }

    /**
     *  统计申请院落
     */
    public void orgSummary(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // SQL信息
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        int k = 0;
        for (Map.Entry tempItem : paramMap) {
            reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].name", tempItem.getKey());
            reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].value", tempItem.getValue());
            k++;
        }
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("chartService", "queryReportData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            String jsonData = "[]";
            if (rspData != null) {
                jsonData = rspData.toJson();
            }
            ResponseUtil.printSvcResponse(response, svcResponse, """ "data": ${jsonData}""");
        } else {
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }
}
