<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:choose>
    <c:when test="${param.xyFormCd != null && param.xyFormCd != ''}">
        <div class="tabs">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <li class="selected">
                            <a href="javascript:void(0);"><span>选房处理</span></a>
                        </li>
                        <li onclick="ch001.loadGfxy();">
                            <a href="javascript:void(0);"><span>购房协议</span></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent layoutBox" layoutH="48">
                    <%--内容展示区域--%>
                <div style="position: relative;">
                    <jsp:include page="/eland/ch/ch001/ch001-chsModel03Choose.gv"/>
                </div>
                <div class="js_gfxy_container form_sub_rpt_data" style="position: relative;"></div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="layoutBox" style="position: relative;">
            <jsp:include page="/eland/ch/ch001/ch001-chsModel03Choose.gv"/>
        </div>
    </c:otherwise>
</c:choose>
