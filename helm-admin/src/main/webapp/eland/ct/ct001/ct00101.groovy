import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.properties.PropertiesUtil
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * User: shfb_wang 
 * Date: 2016/1/29 0029 11:23
 * Copyright(c) 北京四海富博计算机服务有限公司
 *
 * 独立确认签约操作，gv。方便临时修改。
 */
class ct00101 extends GroovyController {

    // 读取session信息锁获取
    private static Lock tokenLock = new ReentrantLock();

    /** 确认签约操作  */
    public void cfmContract(HttpServletRequest request, HttpServletResponse response) {
        // 获取签约TOKEN
        SessionUtil sessionUtil = new SessionUtil(request);
        String cfmCtToken = "";
        String tokenId = request.getParameter("tokenId");
        // 加锁读取会话信息
        try {
            tokenLock.lock();
            cfmCtToken = sessionUtil.getString("cfmCtToken");
            sessionUtil.removeAttr("cfmCtToken");
        } finally {
            tokenLock.unlock();
        }
        // 验证是否重复提交
        if (StringUtil.isEmptyOrNull(cfmCtToken) || !StringUtil.isEqual(tokenId, cfmCtToken)) {
            String jsonStr = "{success:false, errMsg:'非法请求,请勿重复提交！'}";
            ResponseUtil.printAjax(response, jsonStr);
            return;
        } else {
            // 返回处理结果
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            String hsCtIds = request.getParameter("hsCtIds");
            String ctTypes = request.getParameter("ctTypes");
            XmlBean opData = new XmlBean();
            // 腾退签约动作组件
            String nodePath = "OpData.";
            String[] hsCtIdArr = hsCtIds.split(",");
            String[] ctTypeArr = ctTypes.split(",");
            for (int i = 0; i < hsCtIdArr.length; i++) {
                opData.setStrValue(nodePath + "HsCtInfo.hsCtId[" + i + "]", hsCtIdArr[i]);
                opData.setStrValue(nodePath + "HsCtInfo.ctType[" + i + "]", ctTypeArr[i]);
            }
            svcRequest.addOp("CHANGE_TO_FORMAL_SIGN", opData);
            // 输出处理结果
            SvcResponse svcResponse = null;
            if (PropertiesUtil.readBoolean("oframe", "rmi_config")) {
                svcResponse = transaction("ctService1", svcRequest);
            } else {
                svcResponse = transaction(svcRequest);
            }
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }
}