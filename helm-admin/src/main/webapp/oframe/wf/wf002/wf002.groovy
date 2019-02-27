import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 流程管理控制器
 */
class wf002 extends GroovyController {

    /**
     * 发布流程初始化
     * <p>
     *     1、初始化流程发布界面
     * </p>
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 返回视图
        return new ModelAndView("/oframe/wf/wf002/wf002", modelMap)
    }

    /**
     *  发布流程
     * @param request 请求信息
     * @param response
     */
    public void deploy(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求参数信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取文件
        MultipartFile localFile = super.getFile(request, "wfFile");
        // 获取文件后缀
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(
                originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;
        File saveFile = null;
        String remoteFileName = "temp/" + UUID.randomUUID().toString() + fileType;
        saveFile = ServerFile.createFile(remoteFileName);
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(saveFile);
            inputStream = localFile.getInputStream();
            // 保存待上传文件信息
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            ResponseUtil.print(response, """{"success": false, "errMsg": "文件上传失败!" }""");
            return;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (Exception e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (Exception e) {
                }
            }
        }
        // 签约信息保存
        XmlBean opData = new XmlBean();
        String prePath = "OpData.WfExecute.";
        opData.setStrValue(prePath + "resourcePath", saveFile.getAbsolutePath());
        opData.setStrValue(prePath + "deployName", request.getParameter("deployName"));
        opData.setStrValue(prePath + "procDepCategory", request.getParameter("procDepCategory"));

        // 调用服务
        svcRequest.addOp("SAVE_DEP_PROC_ZIP", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 删除上传的文件
        if (saveFile != null) {
            saveFile.delete();
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 查询流程定义列表*/
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = requestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        svcRequest.addOp("QUERY_PROC_DEF_LIST", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean result = svcResponse.getFirstOpRsp("QUERY_PROC_DEF_LIST");
            if (result != null) {
                XmlBean procLists = result.getBeanByPath("Operation.OpResult");
                int num = procLists.getListNum("OpResult.ProcDefInfos.ProcDefInfo");
                List procList = new ArrayList();
                for (int i = 0; i < num; i++) {
                    procList.add(procLists.getBeanByPath("OpResult.ProcDefInfos.ProcDefInfo[${i}]").getRootNode());
                }
                modelMap.put("procList", procList);
            }
        }
        // 返回视图
        return new ModelAndView("/oframe/wf/wf002/wf002_list", modelMap)
    }

    /** 查看流程定义详情 */
    public ModelAndView findProcDetail(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = requestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.WfExecute.procDefKey", request.getParameter("procDefKey"));
        svcRequest.addOp("QUERY_SINGLE_PROC_DEF_BY_KEY", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean result = svcResponse.getFirstOpRsp("QUERY_SINGLE_PROC_DEF_BY_KEY");
            if (result != null) {
                XmlBean proc = result.getBeanByPath("Operation.OpResult.ProcDefInfo");
                modelMap.put("proc", proc.getRootNode());
            }
        }
        // 返回视图
        return new ModelAndView("/oframe/wf/wf002/wf002_detail", modelMap)
    }


}
