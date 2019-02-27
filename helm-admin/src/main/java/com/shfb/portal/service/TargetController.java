package com.shfb.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.service.bo.SvcRequest;
import com.shfb.oframe.core.util.service.bo.SvcResponse;
import com.shfb.oframe.core.web.controller.AbstractBaseController;
import com.shfb.oframe.core.web.util.RequestUtil;
import com.shfb.oframe.core.web.util.ResponseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 指标服务
 */
@Controller
@RequestMapping({"/shfb/svc/target"})
public class TargetController extends AbstractBaseController {

    /**
     * 截取指标类型的长度
     */
    private final static int CHAR_TYPE_LEN = 10;
    /**
     * 截取jobCd的长度
     */
    private final static int PRJ_JOB_CD_LEN = 9;
    /**
     * 是指标类型
     */
    private final static String TYPE = "2";
    /**
     * 用来判断在权限资源上配置的路径上是否有chartType
     */
    private final static String CHAR_TYPE = "chartType";
    /**
     * 用来判断在权限资源上配置的路径上是否有chartType
     */
    private final static String PRJ_JOB_CD = "prjJobCd";

    /**
     * 查询所有的指标
     */
    @RequestMapping(method = RequestMethod.GET, value = "/query/alltarget.gv")
    public void queryAllTarget(HttpServletRequest request, HttpServletResponse response) {
        String prjCd = request.getParameter("prjCd");
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.level", "-1");
        svcRequest.setValue("Request.SvcCont.rhtCd", "SYS_PORTAL");
        SvcResponse svcResponse = callService("staffService", "queryMenuTree", svcRequest);
        XmlBean resultXml = new XmlBean(svcResponse.toXML());
        JSONArray data = new JSONArray();
        //1.获取上下级关系
        XmlBean menuXml = resultXml.getBeanByPath("Response.SvcCont.Staff.Menu");
        //去除系统面板
        menuXml.removeNode("Menu.Node[0]");
        int listNum = menuXml.getListNum("Menu.Node");
        //map用于存储每个组的报文和每个组下面的指标的报文集合
        Map<XmlBean, List<XmlBean>> menuMap = new LinkedHashMap<XmlBean, List<XmlBean>>();
        for (int i = 0; i < listNum; i++) {
            XmlBean nodeXml_i = menuXml.getBeanByPath("Menu.Node[" + i + "]");
            //获取当前指标ID
            String rhtId = nodeXml_i.getStrValue("Node.rhtId");
            for (int j = 0; j < listNum; j++) {
                XmlBean nodeXml_k = menuXml.getBeanByPath("Menu.Node[" + j + "]");
                //获取上级指标ID
                String uRhtId = nodeXml_k.getStrValue("Node.uRhtId");
                //获取指标类型
                String rhtSubType = nodeXml_k.getStrValue("Node." +
                        "");
                //如果上级指标ID等于当前指标ID就说明是一个分组
                if (StringUtil.isEqual(rhtId, uRhtId)) {
                    //判断如果集合中中存在就添加没有就新建再添加
                    if (menuMap.containsKey(nodeXml_i)) {
                        List<XmlBean> subNodes = menuMap.get(nodeXml_i);
                        if (TYPE.equals(rhtSubType)) {
                            subNodes.add(nodeXml_k);
                        }
                    } else {
                        List<XmlBean> subNodes = new ArrayList<XmlBean>();
                        subNodes.add(nodeXml_k);
                        menuMap.put(nodeXml_i, subNodes);
                    }
                }
            }
        }
        //2.解析上下级关系，拼接json
        //遍历集合
        for (Map.Entry<XmlBean, List<XmlBean>> entry : menuMap.entrySet()) {
            JSONObject tempObj = new JSONObject(true);
            XmlBean upNodeXml = entry.getKey();
            //获取指标cd
            String groupId = upNodeXml.getStrValue("Node.rhtCd");
            //指标名称
            String groupName = upNodeXml.getStrValue("Node.rhtName");
            JSONArray tempArray = new JSONArray();
            List<XmlBean> subNodes = entry.getValue();

            for (XmlBean subXml : subNodes) {
                JSONObject subTempObj = new JSONObject(true);
                String cd = subXml.getStrValue("Node.rhtCd");
                String name = subXml.getStrValue("Node.rhtName");
                String navUrl = subXml.getStrValue("Node.navUrl");
                //去掉我的任务我的代办
                if (navUrl.contains("mb005") || navUrl.contains("mb008")) {
                    String rhtSubType = subXml.getStrValue("Node.rhtSubType");
                    if (!TYPE.equals(rhtSubType)) {
                        continue;
                    }
                    //调用方法处理类型
                    String type = getType(request, navUrl, prjCd);
                    subTempObj.put("cd", cd);
                    subTempObj.put("name", name);
                    subTempObj.put("type", type);
                    tempArray.add(subTempObj);
                }

            }
            //如果下面只有一个空文件夹就不展示
            if (tempArray.size() > 0) {
                tempObj.put("groupId", groupId);
                tempObj.put("groupName", groupName);
                tempObj.put("groupValue", tempArray);
                data.add(tempObj);
            }
        }

        JSONObject resultObj = new JSONObject(true);
        resultObj.put("success", svcResponse.isSuccess());
        resultObj.put("errMsg", svcResponse.getErrMsg());
        resultObj.put("data", data);

        ResponseUtil.printAjax(response, resultObj.toJSONString());
    }

    //处理指标类型
    private String getType(HttpServletRequest request, String navUrl, String prjCd) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String type = "";
        //eland/mb/mb005/mb005-init.gv?prjJobCd=hs_die_status&chartType=pie
        //处理指标类型
        if (StringUtil.isNotEmptyOrNull(navUrl) && navUrl.contains(CHAR_TYPE)) {
            int typeIndex = navUrl.indexOf(CHAR_TYPE);
            type = navUrl.substring(typeIndex + CHAR_TYPE_LEN);
            int moreIdx = type.indexOf("&");
            if (moreIdx > 0) {
                type = type.substring(0, moreIdx);
            }
        } else {
            //如果路径上没有带类型就要查询
            if (StringUtil.isNotEmptyOrNull(navUrl) && navUrl.contains(PRJ_JOB_CD)) {
                int beginIndex = navUrl.indexOf(PRJ_JOB_CD);
                String prjJobCd = "";
                if (navUrl.contains("&")) {
                    int lastIndex = navUrl.indexOf("&");
                    prjJobCd = navUrl.substring(beginIndex + PRJ_JOB_CD_LEN, lastIndex);
                } else {
                    prjJobCd = navUrl.substring(beginIndex + PRJ_JOB_CD_LEN);
                }
                XmlBean opData = new XmlBean();
                opData.setStrValue("OpData.PrjJobDef.jobCd", prjJobCd);
                opData.setStrValue("OpData.PrjJobDef.prjCd", prjCd);
                // 调用服务查询数据
                svcRequest.addOp("QUERY_PRJ_JOB_DEF_CMPT", opData);
                SvcResponse svcRes = query(svcRequest);
                if (svcRes.isSuccess()) {
                    XmlBean prjJobInfo = svcRes.getFirstOpRsp("QUERY_PRJ_JOB_DEF_CMPT");
                    if (prjJobInfo != null) {
                        type = prjJobInfo.getStrValue("Operation.OpResult.PrjJobDef.exeParam10");
                    }
                }
            }

        }
        return type;
    }

    /**
     * 获取首页指标
     *
     * @param request  请求信息
     * @param response 响应信息
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/query/defTarget.gv")
    public void queryDefTarget(HttpServletRequest request, HttpServletResponse response) {
        String prjCd = request.getParameter("prjCd");
        /**
         * 面板查询请求处理
         */
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setStrValue("Request.TcpCont.PrjCd", prjCd);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.staffCode", svcRequest.getReqStaffCd());
        // 是否显示无效项目标志。 1 不显示无效项目， 0  显示。
        reqData.setStrValue("SvcCont.isShowExp", "1");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("staffService", "queryPortalInfo", svcRequest);
        JSONArray portalRhts = new JSONArray();
        if (svcResponse.isSuccess() && svcResponse.getRspData() != null) {
            XmlBean portalRhtsBean = svcResponse.getRspData().getBeanByPath("SvcCont.StaffPortal.portalRhts");
            if (portalRhtsBean != null) {
                int haveRhtPortal = portalRhtsBean.getListNum("portalRhts.portalRht");
                for (int i = 0; i < haveRhtPortal; i++) {
                    boolean isHaveRht = Boolean.parseBoolean(portalRhtsBean.getStrValue("portalRhts.portalRht[" + i + "].isHaveRht"));
                    if (isHaveRht) {
                        XmlBean tempBean = portalRhtsBean.getBeanByPath("portalRhts.portalRht[" + i + "]");
                        String temp = tempBean.getStrValue("portalRht.navUrl");
                        if (temp.contains("mb005") || temp.contains("mb008")) {
                            String navUrl = tempBean.getStrValue("portalRht.navUrl");
                            if (!"#".equals(navUrl)) {
                                JSONObject jsonData = new JSONObject(true);
                                String cd = tempBean.getStrValue("portalRht.rhtCd");
                                jsonData.put("cd", cd);
                                jsonData.put("name", tempBean.getStrValue("portalRht.rhtName"));
                                //调用方法处理类型
                                String type = getType(request, navUrl, prjCd);
                                jsonData.put("type", type);
                                portalRhts.add(jsonData);
                            }

                        }
                    }
                }
            }
        }
        // 输出公告信息
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("success", svcResponse.isSuccess());
        jsonObject.put("errMsg", svcResponse.getErrMsg());
        jsonObject.put("data", portalRhts);
        ResponseUtil.printAjax(response, jsonObject.toString());
    }

    @Override
    protected Method getScriptMethod(Object o, String s) {
        return null;
    }
}

