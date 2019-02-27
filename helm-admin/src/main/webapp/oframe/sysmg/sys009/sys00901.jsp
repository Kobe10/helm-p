<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <form method="post" id="sys009frm">
        <div class="searchBar">
            <table class="border">
                <tr>
                    <th>缓存大类：</th>
                    <td><input id="cacheTypeName" type="text" name="cacheTypeName"/></td>
                    <th>缓存引擎：</th>
                    <td><select id="cacheEngineType" name="cacheEngineType">
                        <option value="">--全部--</option>
                        <option value="ehcache">ehcache</option>
                        <option value="hashcache">hashcache</option>
                        <option value="emptyCache">emptyCache</option>
                        <option value="redis">redis</option>
                    </select></td>
                    <td style="text-align:left;">
                        <button type="button" id="schBtn" class="btn btn-primary" onclick="sys009.qryCacheInfo();">检索
                        </button>
                        <button type="button" class="btn btn-opt" onclick="sys009.removeAllCacheExpSession();"
                                title="清除除Session以外所有缓存项">清除所有
                        </button>
                    </td>
                </tr>
            </table>
        </div>
    </form>
    <div class="mart5">
        <div id="sys009_list_print">
            <table class="table" width="100%" layoutH="100">
                <thead>
                <tr>
                    <th width="5%">序号</th>
                    <th width="20%">缓存大类</th>
                    <th width="10%">缓存引擎</th>
                    <th width="20%">缓存描述</th>
                    <th width="20%">内容主键</th>
                    <th width="10%">操作</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <%--<div id="pagebox" class="panelBar bottom"></div>--%>
    </div>
</div>

<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys009/js/sys009.js" type="text/javascript"/>