import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by HuberyYan on 2016/6/2.
 */
class sys020 extends GroovyController {
    /**
     * 初始化
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys020/sys020", modelMap);
    }

    /**
     * 自定义发送短信
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView sendMessageZDY(HttpServletRequest request, HttpServletResponse response) {
        //请求获取报文上半部分
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //取值
        String rPhoneNum = request.getParameter("rPhoneNum");
        String sTime = request.getParameter("sTime");
        String sContent = request.getParameter("sContent");
        String[] phoneNum = rPhoneNum.split(",");
        XmlBean opData = new XmlBean();
        //入参
        int temp = phoneNum.length;
        for (int i = 0; i < temp; i++) {
            opData.setStrValue("OpData.rPhoneNum", phoneNum[i]);
            opData.setStrValue("OpData.sTime", StringUtil.isEmptyOrNull(sTime) ? "" : DateUtil.toStringYmdHms(DateUtil.toDateYmdHmsWthH(sTime)));
            opData.setStrValue("OpData.sContent", sContent);
            //调用组件（构成框架SvcCont.Operation.）
            svcRequest.addOp("SEND_SMS_CMPT", opData);
            //启动服务
            SvcResponse svcResponse = transaction(svcRequest);
            //返回信息
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }

    /**
     * 立即发送短信  若失败 记录失败原因
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView sendMessage(HttpServletRequest request, HttpServletResponse response) {
        //请求获取报文上半部分
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //取值
        String sId = request.getParameter("sId");
        XmlBean opData = new XmlBean();
        //入参
        opData.setStrValue("OpData.sId", sId);
        svcRequest.addOp("SEND_SMS_CMPT", opData);
        //启动服务
        SvcResponse svcResponse = transaction(svcRequest);
        //---------------------------发送失败返回错误提示！--------------------
        svcRequest = RequestUtil.getSvcRequest(request);
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "SmsSend");//查询实体
        opData.setStrValue("OpData.entityKey", sId);//实体主键
        //调用服务
        svcRequest.addOp("QUERY_ALL_ENTITY", opData);
        //启动查询
        SvcResponse svcResponse2 = query(svcRequest);
        boolean result = svcResponse2.isSuccess();
        StringBuilder stringBuilder = new StringBuilder();
        String failNote = "";
        if (result) {
            XmlBean resultBean = svcResponse2.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.SmsSend");
            String tempStatus = resultBean.getStrValue("SmsSend.sStatus");//发送状态   发送状态: 0未发送、1已发送、2发送失败、3信息错误
            if (StringUtil.isEqual(tempStatus, "2")) {
                failNote = resultBean.getStrValue("SmsSend.failNote");//错误原因
                result = false;
            }
        }
        //返回信息
        String jsonStr = """{success:${result}, errMsg:"${failNote}", resultMap:{treeJson:[${
            stringBuilder.toString()
        }]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 打开发送界面
     */
    public ModelAndView sendView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys020/sys020_add", modelMap);
    }

}