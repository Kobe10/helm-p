<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/24 0024 9:22
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="pageNav">
    <a href="javascript:index.quickOpen('eland/ph/ph001/ph001-init.gv',{opCode:'ph001-init',opName:'居民信息',fresh:true})">居民信息</a>---><a class="current">信息录入</a>
</div>
<c:set var="editAble" value="true"/>
<c:set var="procReadonly" value="true"/>
<div class="mart5" style="padding: 0 5px;">
    <div class="panelBar">
        <ul class="toolBar">
            <c:set var="superRhtShowSaveBtn" value="false"/>
            <c:choose>
                <c:when test="${hsStatus == '100'}">
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                               name="保存继续" rhtCd="edit_old_hs_rht"
                               onClick="ph00301_input.saveInputInfo('1');"/>
                </c:when>
                <c:otherwise>
                    <%--super administrator right--%>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                               name="保存继续" rhtCd="super_edit_hs_rht"
                               onClick="ph00301_input.saveInputInfo('1');"/>
                    <oframe:power prjCd="${param.prjCd}" rhtCd="super_edit_hs_rht">
                        <c:set var="superRhtShowSaveBtn" value="true"/>
                    </oframe:power>

                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${hsId != null && hsId != ''}">
                    <%--<li><a class="save" onclick="ph00301_task.initTask('${hsId}');"><span>发起修改申请</span></a></li>--%>
                    <li><a class="reflesh" onclick="ph00301_input.flushInputInfo('${hsId}');"><span>信息刷新</span></a></li>
                    <li><a class="new-area" onclick="ph00301_input.openCalculate();"><span>补偿计算</span></a></li>
                    <li><a class="hight-level" onclick="ph00301_input.viewHouse('${hsId}')"><span>居民详情</span></a></li>
                </c:when>
                <c:otherwise>
                    <oframe:power prjCd="${param.prjCd}" rhtCd="edit_old_hs_rht">
                        <c:if test="${!superRhtShowSaveBtn}">
                            <li><a class="save" onclick="ph00301_input.saveInputInfo('1');"><span>保存继续</span></a></li>
                        </c:if>
                    </oframe:power>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
    <%--引入 房产信息详情页 --%>
    <jsp:include page="/eland/ph/ph003/ph00301-initS.gv?hsId=${hsId}&procReadonly=true"/>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_input.js" type="text/javascript"/>