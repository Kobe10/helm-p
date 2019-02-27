import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Administrator on 2016/6/22.
 */
class formdesign extends GroovyController {
    /**
     * 解析配置为html
     */
    public void toHtml(HttpServletRequest request, HttpServletResponse response) {
        String shfbplugins = request.getParameter("shfbplugins");
        String tempArrStr = request.getParameter("tempArr");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String[] tempArr = tempArrStr.split(",");
        if (tempArr.length > 0) {
            for (int i = 0; i < tempArr.length; i++) {
                if (StringUtil.isNotEmptyOrNull(request.getParameter(tempArr[i]))) {
                    opData.setStrValue("OpData.attrs." + tempArr[i], request.getParameter(tempArr[i]));
                }
            }
            opData.removeNode("OpData.attrs.type");
            opData.setStrValue("OpData.type", shfbplugins);
            svcRequest.addOp("NEW_DESIGN", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean opResult = svcResponse.getFirstOpRsp("NEW_DESIGN").getBeanByPath("Operation.OpResult");
                String html = opResult.getStrValue("OpResult.retStr");
                ResponseUtil.printAjax(response, """ ${html} """);
            } else {
                ResponseUtil.printAjax(response, """ ${svcResponse.errMsg} """);
            }
        }
    }
}