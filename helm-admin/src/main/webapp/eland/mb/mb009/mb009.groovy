import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
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
        // 返回业务视图
        ModelMap modelMap = new ModelMap();
        // 调用服务获取院落信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean paramData = new XmlBean();
        String nodePath = "OpData.";
        paramData.setStrValue(nodePath + "entityName", "BuildInfo");
        paramData.setStrValue(nodePath + "Conditions.Condition[0].fieldName", "ttDesireCd");
        paramData.setStrValue(nodePath + "Conditions.Condition[0].fieldValue", "30");
        paramData.setStrValue(nodePath + "Conditions.Condition[0].operation", "=");

        paramData.setStrValue(nodePath + "ResultFields.fieldName[0]", "buildId");
        // 签约状态及总户数
        paramData.setStrValue(nodePath + "ResultFields.fieldName[1]", "buildCreateDate");
        paramData.setStrValue(nodePath + "ResultFields.fieldName[2]", "totalCjNum");
        // 签约
        paramData.setStrValue(nodePath + "ResultFields.fieldName[3]", "hsCtStCd");
        paramData.setStrValue(nodePath + "ResultFields.fieldName[4]", "hsCtStTime");
        // 选房
        paramData.setStrValue(nodePath + "ResultFields.fieldName[5]", "chsHsStCd");
        paramData.setStrValue(nodePath + "ResultFields.fieldName[6]", "chsHsStTime");
        // 退租过户
        paramData.setStrValue(nodePath + "ResultFields.fieldName[7]", "backRentStCd");
        paramData.setStrValue(nodePath + "ResultFields.fieldName[8]", "backRentStTime");
        // 交房
        paramData.setStrValue(nodePath + "ResultFields.fieldName[9]", "ttHsCd");
        paramData.setStrValue(nodePath + "ResultFields.fieldName[10]", "ttHsStTime");
        // 归档
        paramData.setStrValue(nodePath + "ResultFields.fieldName[11]", "placeFileCd");
        paramData.setStrValue(nodePath + "ResultFields.fieldName[12]", "placeFileStTime");

        svcRequest.addOp("QUERY_ENTITY_CMPT", paramData);
        // 调用服务，获取所有录入的建筑
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 意向院落统计
            int[] yxYlNum = [0, 0, 0, 0];
            // 签约院落统计
            int[] qyYlNum = [0, 0, 0, 0];
            // 选房院落统计
            int[] xfYlNum = [0, 0, 0, 0];
            // 退租过户院落统计
            int[] tzghYlNum = [0, 0, 0, 0];
            // 交房院落统计
            int[] jfYlNum = [0, 0, 0, 0];
            // 归档院落统计
            int[] gdYlNum = [0, 0, 0, 0];

            /* 循环结果，处理各院落分类 */
            XmlBean ylsBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT");
            int ylCount = ylsBean.getListNum("Operation.OpResult.PageData.Row");
            Date nowDate = DateUtil.getSysDate();
            for (int i = 0; i < ylCount; i++) {
                XmlBean tempBean = ylsBean.getBeanByPath("Operation.OpResult.PageData.Row[${i}]");
                // 院落户数
                int totalHsNum = tempBean.getIntValue("Row.totalCjNum");
                // 归档院落统计
                if ("1".equals(tempBean.getStrValue("Row.placeFileCd"))) {
                    // 意向院落
                    gdYlNum[0] = gdYlNum[0] + 1;
                    gdYlNum[1] = gdYlNum[1] + totalHsNum;
                    // 本月意向  toDateYmd
                    String createGdDateStr = tempBean.getStrValue("Row.placeFileStTime");
                    Date createGdDate = null;
                    if (StringUtil.isNotEmptyOrNull(createGdDateStr)) {
                        createGdDate = DateUtil.toDateYmd(createGdDateStr);
                    }
                    if (DateUtil.dateCompare(createGdDate, nowDate, DateUtil.MONTH) == 0) {
                        gdYlNum[2] = gdYlNum[2] + 1;
                        gdYlNum[3] = gdYlNum[3] + totalHsNum;
                    }
                    continue;
                }
                // 意向院落
                yxYlNum[0] = yxYlNum[0] + 1;
                yxYlNum[1] = yxYlNum[1] + totalHsNum;
                // 本月意向
                String createDateStr = tempBean.getStrValue("Row.buildCreateDate");
                Date createDate = null;
                if(StringUtil.isNotEmptyOrNull(createDateStr)){
                    createDate = DateUtil.toDateYmd(createDateStr);
                }
                if (DateUtil.dateCompare(createDate, nowDate, DateUtil.MONTH) == 0) {
                    yxYlNum[2] = yxYlNum[2] + 1;
                    yxYlNum[3] = yxYlNum[3] + totalHsNum;
                }
                // 签约院落
                if ("1".equals(tempBean.getStrValue("Row.hsCtStCd"))) {
                    // 意向院落
                    qyYlNum[0] = qyYlNum[0] + 1;
                    qyYlNum[1] = qyYlNum[1] + totalHsNum;
                    // 本月意向  toDateYmdWthH
                    String createQyDateStr = tempBean.getStrValue("Row.hsCtStTime");
                    Date createQyDate = null;
                    if(StringUtil.isNotEmptyOrNull(createQyDateStr)){
                        createQyDate = DateUtil.toDateYmd(createQyDateStr);
                    }
                    if (DateUtil.dateCompare(createQyDate, nowDate, DateUtil.MONTH) == 0) {
                        qyYlNum[2] = qyYlNum[2] + 1;
                        qyYlNum[3] = qyYlNum[3] + totalHsNum;
                    }
                }
                // 选房院落
                if ("1".equals(tempBean.getStrValue("Row.chsHsStCd"))) {
                    // 意向院落
                    xfYlNum[0] = xfYlNum[0] + 1;
                    xfYlNum[1] = xfYlNum[1] + totalHsNum;
                    // 本月意向  toDateYmd
                    String createXfDateStr = tempBean.getStrValue("Row.chsHsStTime");
                    Date createXfDate = null;
                    if(StringUtil.isNotEmptyOrNull(createXfDateStr)){
                        createXfDate = DateUtil.toDateYmd(createXfDateStr);
                    }
                    if (DateUtil.dateCompare(createXfDate, nowDate, DateUtil.MONTH) == 0) {
                        xfYlNum[2] = xfYlNum[2] + 1;
                        xfYlNum[3] = xfYlNum[3] + totalHsNum;
                    }
                }
                // 退租过户院落统计
                if ("1".equals(tempBean.getStrValue("Row.backRentStCd"))) {
                    // 意向院落
                    tzghYlNum[0] = tzghYlNum[0] + 1;
                    tzghYlNum[1] = tzghYlNum[1] + totalHsNum;
                    // 本月意向  toDateYmd
                    String createTzghDateStr = tempBean.getStrValue("Row.backRentStTime");
                    Date createTzghDate = null;
                    if(StringUtil.isNotEmptyOrNull(createTzghDateStr)){
                        createTzghDate = DateUtil.toDateYmd(createTzghDateStr);
                    }
                    if (DateUtil.dateCompare(createTzghDate, nowDate, DateUtil.MONTH) == 0) {
                        tzghYlNum[2] = tzghYlNum[2] + 1;
                        tzghYlNum[3] = tzghYlNum[3] + totalHsNum;
                    }
                }
                // 交房院落统计
                if ("1".equals(tempBean.getStrValue("Row.ttHsCd"))) {
                    // 意向院落
                    jfYlNum[0] = jfYlNum[0] + 1;
                    jfYlNum[1] = jfYlNum[1] + totalHsNum;
                    // 本月意向  toDateYmd
                    String createJfDateStr = tempBean.getStrValue("Row.ttHsStTime");
                    Date createJfDate = null;
                    if(StringUtil.isNotEmptyOrNull(createJfDateStr)){
                        createJfDate = DateUtil.toDateYmd(createJfDateStr);
                    }
                    if (DateUtil.dateCompare(createJfDate, nowDate, DateUtil.MONTH) == 0) {
                        jfYlNum[2] = jfYlNum[2] + 1;
                        jfYlNum[3] = jfYlNum[3] + totalHsNum;
                    }
                }
            }
            // 设置输出结果
            modelMap.put("yxYlNum", yxYlNum);
            modelMap.put("qyYlNum", qyYlNum);
            modelMap.put("xfYlNum", xfYlNum);
            modelMap.put("tzghYlNum", tzghYlNum);
            modelMap.put("jfYlNum", jfYlNum);
            modelMap.put("gdYlNum", gdYlNum);
        }
        // 调用服务
        return new ModelAndView("/eland/mb/mb009/mb009", modelMap);
    }
}
