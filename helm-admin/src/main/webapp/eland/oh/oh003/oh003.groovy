/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/8/27 0027 18:02
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
/**
 * 房屋结算处理
 */
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class oh003 extends GroovyController {

    /**
     * 初始化右侧面板 页面
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/oh/oh003/oh003", modelMap);
    }
    /**
     * 初始化批量修改状态
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initBEdit(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 返回数据
        ModelMap modelMap = new ModelMap();
        String newHsIds = requestUtil.getStrParam("newHsIds");
        modelMap.put("newHsIds", newHsIds);
        return new ModelAndView("/eland/oh/oh003/oh00301_bedit", modelMap);
    }
    /**
     * @param request
     * @param response
     * @return
     */
    public ModelAndView bEdit(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 房源ID列表
        String[] newHsIdArr = requestUtil.getStrParam("newHsIds").split(",");
        //结算状态
        String jsStatus = requestUtil.getStrParam("jsStatus");
        //结算时间
        String jsTime = requestUtil.getStrParam("jsTime");
        XmlBean opData = new XmlBean();
        SvcResponse svcResponse = null;
        for (String newHsId : newHsIdArr) {
            if (StringUtil.isEmptyOrNull(newHsId)) {
                continue;
            }
            String BasePath = "OpData.";
            opData.setStrValue(BasePath + "entityName", "NewHsInfo");
            opData.setStrValue(BasePath + "EntityData.newHsId", newHsId);
            opData.setStrValue(BasePath + "EntityData.jsStatus", jsStatus);
            opData.setStrValue(BasePath + "EntityData.jsTime", jsTime);
            svcRequest.addOp("SAVE_ENTITY", opData);
            // 调用服务
            svcResponse = transaction(svcRequest);
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化统计界面
     * @param request 请求信息
     * @param response 响应结果
     * @return 返回报表初始化界面
     */
    public ModelAndView initRpt(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        // 返回数据
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/oh/oh003/oh003_rpt", modelMap);
    }


}


