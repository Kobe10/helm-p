<%--
    选择岗位页面
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2014/11/17 0017
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="pageContent" style="height: 100%;">
    <%--左侧组织树--%>
    <div id="orgTreeLeft"
         style="position: relative; border: 1px solid #93c7ea; width: 30%; display: inline-block; overflow: hidden;"
         layoutH="80">
        <h2 style="width: 100%;height: 20px;background-color: rgb(147,199,234); text-align: center; padding-top: 10px; color:#ffffff;">
            <span>组 织 信 息</span></h2>

        <div layoutH="120" style="overflow:auto; line-height:21px; background:#fff">
            <ul class="blueBox">
                <li>
                    <oframe:select prjCd="${param.prjCd}" collection="${prjMap}" onChange="sys00202.initFullTree();" name="sys00202TreeType"
                                    style="width:100%;"/>
                </li>
                <li>
                </li>
            </ul>
            <ul id="commonTreeContent_org" class="ztree"></ul>
        </div>
    </div>

    <%--中间岗位信息--%>
    <div style="position: relative; border: 1px solid #93c7ea; width: 33%; display: inline-block; overflow: hidden;"
         layoutH="80">
        <h2 style="width: 100%;height: 20px;background-color: rgb(147,199,234); text-align: center; padding-top: 10px; color:#ffffff;">
            <span>岗 位 信 息</span></h2>

        <div id="positionInfo"></div>
    </div>

    <%--右侧已选岗位信息--%>
    <div style="position: relative; border: 1px solid #93c7ea; width: 33%; display: inline-block; overflow: hidden;"
         layoutH="80">
        <h2 style="width: 100%;height: 20px;background-color: rgb(147,199,234); text-align: center; padding-top: 10px; color:#ffffff;">
            <span>已 选 岗 位</span></h2>

        <ul id="selectedPos">
            <%--展示之前已选岗位--%>
            <c:forEach items="${positionList}" var="node">
                <li><input type="checkbox" name="posId" checked value="${node.posId}"/>
                    <input type="hidden" name="posName" value="${node.posName}"/>
                        ${node.posName}</li>
            </c:forEach>
        </ul>
    </div>
    <div class="center">
        <button type="button" class="close btn btn-primary" onclick="sys00202.savePos();">保存</button>
        <button type="button" class="close btn btn-info">取消</button>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys002/js/sys00202.js" type="text/javascript"/>