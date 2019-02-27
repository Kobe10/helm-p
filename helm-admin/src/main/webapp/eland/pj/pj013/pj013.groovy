import com.shfb.oframe.core.util.common.DateUtil
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
/**
 * 项目资料管理
 * Created by Administrator on 2014/6/23 0023.
 */
class pj013 extends GroovyController {

    /**
     * 初始化 项目资料管理
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj013/pj013", modelMap);
    }

    /**
     * 详细信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView initS(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/pj/pj013/pj01301", modelMap);
    }


/**
 * 分页显示项目资料数据
 * @param request
 * @param response
 * @return
 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 请求处理
        String method = request.getParameter("method");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        if (!method.equals("add")) {
            String docIdStr = request.getParameter("docId");
            String prjCdStr = request.getParameter("prjCd");
            XmlBean reqData = new XmlBean();
            String nodePrePath = "SvcCont.";
            reqData.setValue(nodePrePath + "Document.docId", docIdStr);
            reqData.setValue(nodePrePath + "Document.prjCd", prjCdStr);
            //请求信息
            svcRequest.setReqData(reqData);
            SvcResponse svcResponse = callService("prjDocService", "queryDocuments", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean docBean = svcResponse.getRspData();
                // 推送文件资料信息
                modelMap.put("document", docBean.getBeanByPath("SvcCont.Documents.Document").getRootNode());
                String pubDate = docBean.getValue("SvcCont.Documents.Document.pubDate");
                if (StringUtil.isNotEmptyOrNull(pubDate)) {
                    modelMap.put("pubDate", DateUtil.toDateYmdWthH(pubDate));
                }
                //组织
                String orgId = docBean.getStrValue("SvcCont.Documents.Document.orgId");
                String orgName = docBean.getStrValue("SvcCont.Documents.Document.orgName");
                modelMap.put("nodeInfo.BaseInfo.prjOrgId", orgId);
                modelMap.put("prjOrgName", orgName);
                //关键字
                String keyWord = docBean.getValue("SvcCont.Documents.Document.keyWord");
                if (StringUtil.isNotEmptyOrNull(keyWord)) {
                    String[] keyWords = keyWord.split(",");
                    modelMap.put("keyWords", keyWords);
                }

                // 推送文档信息
                XmlBean svcDocsBean = docBean.getBeanByPath("SvcCont.Documents.SvcDocs");
                if (svcDocsBean != null) {
                    List<XmlNode> svcDocs = new ArrayList<XmlNode>();
                    int svcDocCount = svcDocsBean.getListNum("SvcDocs.svcDoc");
                    for (int j = 0; j < svcDocCount; j++) {
                        svcDocs.add(svcDocsBean.getBeanByPath("SvcDocs.svcDoc[" + j + "]").getRootNode())
                    }
                    modelMap.put("svcDocs", svcDocs);
                }
                // 推送关联文档信息
                XmlBean relDocsBean = docBean.getBeanByPath("SvcCont.Documents.RelDocuments");
                if (relDocsBean != null) {
                    List<XmlNode> relDocs = new ArrayList<XmlNode>();
                    int relDocCount = relDocsBean.getListNum("RelDocuments.relDocument");
                    for (int j = 0; j < relDocCount; j++) {
                        relDocs.add(relDocsBean.getBeanByPath("RelDocuments.relDocument[" + j + "]").getRootNode())
                    }
                    modelMap.put("relDocs", relDocs);
                }
            }
        }
        modelMap.put("method", method);
        return new ModelAndView("/eland/pj/pj013/pj01301", modelMap);
    }

/**
 * 新增项目资料信息
 * @param request
 * @param response
 * @return
 */
    public void save(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean docBean = new XmlBean();

        String nodePrePath = "SvcCont.Documents.Document.";
        docBean.setStrValue(nodePrePath + "docId", request.getParameter("documentId"));
        docBean.setStrValue(nodePrePath + "prjCd", request.getParameter("prjCd"));
        docBean.setStrValue(nodePrePath + "docName", request.getParameter("docName"));
        docBean.setStrValue(nodePrePath + "docType", request.getParameter("docType"));
        docBean.setStrValue(nodePrePath + "docSrc", request.getParameter("docSrc"));
        docBean.setStrValue(nodePrePath + "docStatus", request.getParameter("docStatus"));
        docBean.setValue(nodePrePath + "pubDate", request.getParameter("pubDate"));
        docBean.setStrValue(nodePrePath + "pubOrg", request.getParameter("pubOrg"));
        docBean.setStrValue(nodePrePath + "docCd", request.getParameter("docCd"));
        String[] keyWords = request.getParameterValues("keyWord");
        String keyWord = "";
        if (keyWords != null) {
            for (int i = 0; i < keyWords.length; i++) {
                if (StringUtil.isEmptyOrNull(keyWords[i])) {
                    continue;
                }
                keyWord = keyWords[i] + "," + keyWord;
            }
        }
        if (keyWord.length() > 0) {
            keyWord = keyWord.substring(0, keyWord.length() - 1);
        }
        docBean.setStrValue(nodePrePath + "keyWord", keyWord);
        docBean.setStrValue(nodePrePath + "docDesc", request.getParameter("docDesc"));
        docBean.setStrValue(nodePrePath + "orgId", request.getParameter("prjOrgId"));

        String[] svcDocIds = request.getParameterValues("docId");
        String docPath = "SvcCont.Documents.";
        if (svcDocIds != null) {
            int k = 0;
            for (int i = 0; i < svcDocIds.length; i++) {
                if (StringUtil.isEmptyOrNull(svcDocIds[i])) {
                    continue;
                }
                docBean.setValue(docPath + "SvcDocs.svcDoc[" + k++ + "].docId", svcDocIds[i]);
            }
        }

        String[] relBDocId = request.getParameterValues("relBDocId");
        String[] relName = request.getParameterValues("relName");
        if (relBDocId != null) {
            int k = 0;
            for (int j = 0; j < relBDocId.length; j++) {
                if (StringUtil.isEmptyOrNull(relBDocId[j])) {
                    continue;
                }
                docBean.setValue(docPath + "RelDocuments.relDocument[" + k + "].relBDocId", relBDocId[j]);
                docBean.setValue(docPath + "RelDocuments.relDocument[" + k++ + "].relName", relName[j]);
            }
        }

        svcRequest.setReqData(docBean);
        SvcResponse svcResponse = callService("prjDocService", "saveAddDocuments", svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

}