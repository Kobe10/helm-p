import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * 工作流程进展 通用页面
 *
 * User: shfb_wang 
 * Date: 2016/5/11 0011 17:16
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
class wf007 extends GroovyController {
    /**
     *  初始化 通用查询页面
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String privilegeId = request.getParameter("privilegeId");
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.rhtId", privilegeId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
            if (nodeInfoBean != null) {
                // 获取备注描述信息
                String jsonNote = nodeInfoBean.getStrValue("Node.rhtAttr02");
                XmlBean condBean = new XmlBean();
                condBean.setStrValue("Condition.conditionNames", "");
                condBean.setStrValue("Condition.conditions", "");
                condBean.setStrValue("Condition.conditionValues", "");
                if (StringUtil.isNotEmptyOrNull(jsonNote)) {
                    // 配置的Json字符串转换为XMLBean
                    condBean = XmlBean.jsonStr2Xml(jsonNote, "Condition");
                    //获取json串里的参数；
                }
                modelMap.put("conditions", condBean.getRootNode());

                /**
                 * 查询 指定流程的 环节
                 */
                JSONObject jsonObject = JSONObject.fromObject(jsonNote);
                String procKey = jsonObject.get("procKey");

                XmlBean opData = new XmlBean();
                opData.setStrValue("OpData.procDefKey", procKey);
                opData.setStrValue("OpData.entityName", "HouseInfo");
                String[] conditionNames = jsonObject.get("conditionNames").split(",");
                String[] conditions = jsonObject.get("conditions").split(",");
                String[] conditionValues = jsonObject.get("conditionValues").split(",");

                opData.setStrValue("OpData.OrgFilter.attrName", "HouseInfo.ttOrgId");
                opData.setStrValue("OpData.OrgFilter.attrValue", "");
                opData.setStrValue("OpData.OrgFilter.rhtType", "");
                //拼接 过滤 居民数据的查询条件。
                int t = 0;
                for (int i = 0; i < conditionNames.length; i++) {
                    String temp = conditionNames[i];
                    if (StringUtil.isEmptyOrNull(temp)) {
                        continue;
                    }
                    opData.setStrValue("OpData.Conditions.Condition[${t}].fieldName", conditionNames[i]);
                    opData.setStrValue("OpData.Conditions.Condition[${t}].operation", conditions[i]);
                    opData.setStrValue("OpData.Conditions.Condition[${t++}].fieldValue", conditionValues[i]);
                }

                svcRequest.addOp("STATE_TREE", opData);
                SvcResponse svcRsp = query(svcRequest);
                if (svcRsp.isSuccess()) {
                    XmlBean resultBean = svcRsp.getFirstOpRsp("STATE_TREE").getBeanByPath("Operation.OpResult");
                    if (resultBean != null) {
                        Map poolMap = new LinkedHashMap();
                        Map poolItemMap = new LinkedHashMap();
                        int poolNum = resultBean.getListNum("OpResult.Area");
                        for (int i = 0; i < poolNum; i++) {
                            XmlBean poolBean = resultBean.getBeanByPath("OpResult.Area[${i}]");
                            String poolName = resultBean.getStrValue("OpResult.Area[${i}].name");
                            poolMap.put(poolName, poolBean.getRootNode());

                            List poItList = new LinkedList();
                            int poItmNum = poolBean.getListNum("Area.Items.item");
                            for (int j = 0; j < poItmNum; j++) {
                                XmlBean poItBean = poolBean.getBeanByPath("Area.Items.item[${j}]");
                                poItList.add(poItBean.getRootNode());
                            }
                            poolItemMap.put(poolName, poItList);
                        }

                        modelMap.put("poolMap", poolMap);
                        modelMap.put("poolItemMap", poolItemMap);
                    }
                }
                modelMap.put("procKey", procKey);
            }
        }


        return new ModelAndView("/oframe/wf/wf007/wf007", modelMap);
    }


}