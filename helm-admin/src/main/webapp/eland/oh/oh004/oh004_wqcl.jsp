<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="oh004.saveWq();"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div layoutH="55" style="border: 1px solid rgb(233, 233, 233);">
        <form id="oh004WqForm">
            <input type="hidden" name="personId" value="${personId}"/><%--户籍人口数 --%>
            <input type="hidden" name="hsCtId" value="${hsCtId}"/>

            <table class="border marb5" width="100%">
                <tr>
                    <th>
                        <lable>外迁户主：</lable>
                    </th>
                    <td>
                        <input type="text" value="${personName}"
                               name="personName" class="required readonly"/>
                    </td>
                    <th>
                        <lable>户籍人口数：</lable>
                    </th>
                    <td>
                        <input type="text" value="${personNum}"
                               name="personNum"class="readonly"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <lable>外迁状态：</lable>
                    </th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" name="isHkWq" itemCd="COMMON_YES_NO" value="${isHkWq}"/>
                    </td>
                    <th>
                        <lable>外迁补助：</lable>
                    </th>
                    <td>
                        <input type="text" name="wqBcMoney" value="${wqBcMoney}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <lable>备注说明：</lable>
                    </th>
                    <td colspan="3">
                        <input type="text" name="wqNote" value="${wqNote}"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>