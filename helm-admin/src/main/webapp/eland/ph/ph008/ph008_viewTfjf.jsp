<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/4/1
  Time: 16:05
  To change this template use File | Settings | File Templates.
--%>
<%--流程业务处理-  退租申请 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panel" style="">
        <div class="js_panel_context">
            <input type="hidden" name="hsId" value="${hsId}"/>
            <table class="border">
                <tr>
                    <th width="20%"><label>处理结果：</label></th>
                    <td><oframe:name prjCd="${param.prjCd}" itemCd="FINISH_RESULT" value="${hsStatus.Row.procResult7}"/></td>
                </tr>
                <tr>
                    <th width="20%"><label>处理时间：</label></th>
                    <td><oframe:date value="${hsStatus.Row.recordTime7}" format="yyyy-MM-dd"/></td>
                </tr>
                <tr>
                    <th width="20%"><label>处理事项：</label></th>
                    <td>
                        <c:set var="jfAttr1" value=""/>
                        <c:if test="${hsStatus.Row.jfAttr1 == '钥匙'}">
                            <c:set var="jfAttr1" value="checked"/>
                        </c:if>
                        <c:set var="jfAttr2" value=""/>
                        <c:if test="${hsStatus.Row.jfAttr2 == '拍摄'}">
                            <c:set var="jfAttr2" value="checked"/>
                        </c:if>
                        <c:set var="jfAttr3" value=""/>
                        <c:if test="${hsStatus.Row.jfAttr3 == '宣传画'}">
                            <c:set var="jfAttr3" value="checked"/>
                        </c:if>
                        <input type="checkbox" name="jfAttr1" ${jfAttr1} disabled value="钥匙">钥匙
                        <input type="checkbox" name="jfAttr2" ${jfAttr2} disabled value="拍摄">拍摄
                        <input type="checkbox" name="jfAttr3" ${jfAttr3} disabled value="宣传画">宣传画
                    </td>
                </tr>
                <tr>
                    <th><label>备注说明：</label></th>
                    <td style="text-align: left">${hsStatus.Row.comment7}</td>
                </tr>
            </table>
        </div>
    </div>
</div>