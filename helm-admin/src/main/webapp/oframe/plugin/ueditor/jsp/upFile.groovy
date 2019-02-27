import com.shfb.oframe.core.common.util.OframeCoreUtils
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.json.JSONObject
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by HuberyYan on 2016/6/15.
 */

class upFile extends GroovyController {

    /**
     * 上传
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) {
        String fileJson = request.getAttribute("fileJson");//json串
        String rootPath = request.getAttribute("rootPath");//跟路径
        String webName = request.getAttribute("webName");//项目名称,格式:/xxxx
        String prjCd = request.getAttribute("prjCd");//项目编号
        JSONObject jsonObject = new JSONObject(fileJson);
        boolean doHave = jsonObject.has("state");
        String state = "";
        if (doHave) {
            state = jsonObject.getString("state");
        }
        //上传文件处理-----------------------------------------------------------------
        //判断上传是否成功
        if (state.equals("SUCCESS")) {
            // 文件列表
            String url = jsonObject.getString("url");//地址
            String fileName = jsonObject.getString("original");//原名称
            String docTypeName = jsonObject.getString("type");//类型
            if (StringUtil.isEmptyOrNull(prjCd)) {
                prjCd = 0;
            }
            if (StringUtil.isEmptyOrNull(docTypeName)) {
                docTypeName = "ueditor"
            } else {
                docTypeName = URLDecoder.decode(docTypeName, "utf-8")
            }
            // 服务器端文件名称
            if (fileName != null) {
                if (!StringUtil.isEmptyOrNull(fileName)) {
                    //ueditor保存地址
                    String bdUrl = rootPath + url;
                    //在项目根目录下，指定路径创建文件
                    File file = ServerFile.createFile(url);
                    //把上传的文件从百度地址，"复制"到本地地址
                    OframeCoreUtils.copyFile(file.getPath(), bdUrl);
                }
            }
            // 获取请求码
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean reqData = new XmlBean();
            String nodePath = "SvcCont.Files.File.";
            reqData.setStrValue(nodePath + "docName", fileName);
            reqData.setStrValue(nodePath + "docPath", url);
            reqData.setStrValue(nodePath + "docTypeName", docTypeName);
            reqData.setStrValue(nodePath + "prjCd", prjCd);

            // 调用服务保存文件
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("fileService", "saveAddFile", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean rspData = svcResponse.getRspData();
                String docId = rspData.getStrValue("SvcCont.Files.File.docId");
                //把本地地址填入json串中
                jsonObject.put("url", webName + "/oframe/common/file/file-download.gv?docId=" + docId);
            } else {
                jsonObject.put("state", "上传服务处理失败")
            }
        }
        response.getWriter().write(jsonObject.toString());
    }
}
