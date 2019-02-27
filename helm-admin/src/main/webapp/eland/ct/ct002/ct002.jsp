<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 15/9/14
  Time: 23:22
  To change this template use File | Settings | File Templates.
--%>
<%--预分方案--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ct/ct002/js/ct002.js" type="text/javascript"/>
<style>
    #ct002LeftDiv ul.accordion_menu li.active {
        color: #ffffff;
        background-color: #0088cc;
    }

    #ct002LeftDiv ul.accordion_menu li.active:hover {
        color: #ffffff;
        background-color: #0088cc;
    }

    #ct002LeftDiv span.cfg {
        color: white;
        line-height: 30px;
    }
</style>
<div class="pageNav">
    <a class="current">预分方案</a>
</div>
<div class="mar5" style="vertical-align: top;">
    <%--数据查询区域--%>
    <div id="ct002LeftDiv" style="width: 260px;position: relative;" layoutH="15" class="panel left_menu left">
        <h1>
            <span class="panel_title">项目预分方案</span>
            <span class="menu marr10 right cursorpt" onclick="ct002.importScheme(this);" style="position: relative;">导入
                  <input style="width:28px; height:34px; position:absolute; right:0; top:0; opacity:0;filter:alpha(opacity=0); z-index:11; cursor:pointer;"
                         name="importSchemeFile" id="importSchemeFile" type="file">
            </span>
            <span class="menu marr10 right cursorpt" onclick="ct002.info('')">新建</span>
        </h1>

        <div class="accordion" extendAll="true">
            <c:forEach items="${cfgMap}" var="typeItem">
                <div class="accordionHeader">
                    <h2 class="collapsable">
                        <label>
                            <span>${typeItem.value} </span>
                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="marr5 link right cfg"
                                       name="配置" rhtCd="ct_type_def_rht"
                                       onClick="ct002.typeInfo('${typeItem.key}');"/>
                        </label>
                    </h2>
                </div>
                <div class="accordionContent">
                    <ul class="accordion_menu">
                        <c:forEach items="${schList}" var="item">
                            <c:if test="${item.Row.schemeType == typeItem.key}">
                                <li id="${item.Row.schemeId}" schemeType="${item.Row.schemeType}"
                                    style="padding: 0;white-space:nowrap; text-overflow:ellipsis; overflow: hidden;"
                                    onclick="ct002.info('${item.Row.schemeId}', this)">
                                <span style="padding: 0 5px;color: red;font-weight: 900" class="left">
                                <c:if test="${item.Row.schemeStatus == '1'}">√</c:if>
                                <c:if test="${item.Row.schemeStatus == '0'}">&nbsp;&nbsp;</c:if>
                                <c:if test="${item.Row.schemeStatus == '-1'}">&nbsp;&nbsp;</c:if>
                                </span>${item.Row.schemeName}
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <input type="hidden" class="js_query_model" value="1"/>
    <%--签约区域--%>
    <div id="ct002Context" style="margin-left: 270px;margin-right: 5px;"></div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        <c:choose>
        <c:when test="${schemeId != '' && schemeId != null}">
        ct002.info('${schemeId}', $("#" +${schemeId}, navTab.getCurrentPanel()));
        </c:when>
        <c:otherwise>
        var li = $("ul.accordion_menu:first", navTab.getCurrentPanel()).find("li:first");
        var id = li.attr("id");
        ct002.info(id, li);
        </c:otherwise>
        </c:choose>
    });
</script>