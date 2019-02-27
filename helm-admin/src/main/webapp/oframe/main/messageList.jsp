<%--个人首页--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <ul style="position: absolute;">
        <span class="up-tip"></span>
        <li style="background-color: #ecf2f7;color: #8b9aa9;font-weight: bold;padding: 1px">
            <span class="marl10" style="color: #0000ff">您有${count}条新消息</span>
            <span class="btn-opt"
                  style="float: right;color: white;margin: 3px 5px; padding: 0 10px;border-radius: 3px"
                  onclick="index.initSendMess()">发 消 息</span>
        </li>
        <c:forEach var="item" items="${returnList}">
            <li class="context">
                <dl>
                    <dt>
                        <span class="marl5">${item.Row.createStaffName}</span>
                        <span class="marr5 marl10 link" style="float: right;"
                              onclick="index.update(this, ${item.Row.noticeId},'0')">标记为已读</span>
                        <span class="stime" style="float: right;">
                            <oframe:date value="${item.Row.createTime}" format="yyyy-MM-dd HH:mm:ss"/>
                        </span>
                    </dt>
                    <dd style="word-break: break-all;">
                            ${item.Row.noticeContent}
                    </dd>
                </dl>
            </li>
        </c:forEach>
        <li class="more" onclick="index.showView()">更多消息</li>
    </ul>
</div>