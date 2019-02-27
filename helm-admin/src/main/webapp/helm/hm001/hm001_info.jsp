<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="writeFlag" value="false"/>
<div class="pagecontent mar5">
    <div class="panelBar">
        <ul class="toolBar">
            <c:choose>
                <c:when test="${method != 'view'}">
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                               name="保存" rhtCd="edit_prj_notice_rht"
                               onClick="hm001.saveNotice();"/>
                </c:when>
            </c:choose>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="helm001Frm" method="post">
            <div class="tabs mart5">
                <div class="tabsHeader">
                    <div class="tabsHeaderContent">
                        <ul>
                            <li class="selected">
                                <a href="javascript:void(0);"><span>基本信息</span></a>
                            </li>
                            <li>
                                <a href="javascript:void(0);"><span>事记详情</span></a>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="tabsContent" style="padding: 0;border-top: 1px solid #3d91c8;">
                    <div>
                        <input type="hidden" name="noticeId" value="${helmNotice.HelmNotice.noticeId}"/>
                        <input type="hidden" name="prjCd" value="${helmNotice.HelmNotice.prjCd}"/>
                        <table class="border">
                            <tr>
                                <th width="200px">
                                    <label>公告标题：</label>
                                </th>
                                <td>
                                    <input type="text" class="required" name="noticeTitle"
                                           value="${helmNotice.HelmNotice.noticeTitle}"/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <label>事记类型：</label>
                                </th>
                                <td>
                                    <oframe:select type="radio" prjCd="${param.prjCd}" itemCd="HELM_NOTICE_TYPE"
                                                   name="noticeType" cssClass="required"
                                                   value="${helmNotice.HelmNotice.noticeType}"/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <label>公告状态：</label>
                                </th>
                                <td>
                                    <oframe:select type="radio" prjCd="${param.prjCd}" itemCd="NOTICE_STATUS"
                                                   name="publishStatus" cssClass="required"
                                                   value="${helmNotice.HelmNotice.publishStatus}"/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <label>发布排序：</label>
                                </th>
                                <td>
                                    <input type="number" class="number textInput" name="publishOrder"
                                           value='${helmNotice.HelmNotice.publishOrder}'/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <label>发布日期：</label>
                                </th>
                                <td>
                                    <input type="text" class="date" datefmt="yyyy-MM-dd" name="publishDate"
                                           value='${helmNotice.HelmNotice.publishDate_Name}'/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <label>内容简介：</label>
                                </th>
                                <td>
                                    <div style="width: 90%;">
                                         <textarea style="height: 200px;width: 100%;" class="editor simpleEditor"
                                                   cssClass="required"
                                                   name="noticeSummary">${helmNotice.HelmNotice.noticeSummary}</textarea>
                                    </div>

                                </td>
                            </tr>
                        </table>
                    </div>
                    <div>
                     <textarea layoutH="190" name="noticeContext" class="editor"
                               style="width: 100%;height: 100%">${helmNotice.HelmNotice.noticeContext}</textarea>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>