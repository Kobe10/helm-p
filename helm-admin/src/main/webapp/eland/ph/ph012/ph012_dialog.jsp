<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/12/18 0018 16:24
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="ph012.saveRel();"><span>保存</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>

<div>
    <form id="ph012dialog">
        <input type="hidden" name="hsCtReId" value="${result.Row.hsCtReId}">
        <table class="border" width="100%">
            <tr>
                <th width="20%">被关联房产安置人：</th>
                <td width="30%" class="js_ctPsNamesA">
                    <input type="hidden" name="hsCtIdA" value="${result.Row.hsCtIdA}">
                    <input type="text" name="ctPsNamesA" value="${result.Row.ctPsNamesA}"
                           class="pull-left required noErrorTip" onchange="ph012.changePs(this,'ctPsNamesA')">
                    <a title="按安置人检索" class="btnLook"
                       onclick="ph012.getHsByPsName(this,'ctPsNamesA');">选择</a>
                </td>
                <th>被关联房屋管理组织：</th>
                <td class="js_ttOrgIdA"><oframe:org prjCd="${param.prjCd}" orgId="${result.Row.ttOrgIdA}"/></td>
            </tr>
            <tr>
                <th width="20%">被关联房产地址：</th>
                <td width="30%" colspan="3" class="js_hsFullAddrA">${result.Row.hsFullAddrA}</td>
            </tr>
            <tr>
                <th>关联房产安置人：</th>
                <td class="js_ctPsNamesB">
                    <input type="hidden" name="hsCtIdB" value="${result.Row.hsCtIdB}">
                    <input type="text" name="ctPsNamesB" value="${result.Row.ctPsNamesB}"
                           class="pull-left required noErrorTip" onchange="ph012.changePs(this,'ctPsNamesB')">
                    <a title="按安置人检索" class="btnLook"
                       onclick="ph012.getHsByPsName(this,'ctPsNamesB');">选择</a>
                </td>
                <th>关联房屋管理组织：</th>
                <td class="js_ttOrgIdB"><oframe:org prjCd="${param.prjCd}" orgId="${result.Row.ttOrgIdB}"/></td>
            </tr>
            <tr>
                <th>关联房产地址：</th>
                <td colspan="3" class="js_hsFullAddrB">${result.Row.hsFullAddrB}</td>
            </tr>
            <tr>
                <th>关联关系：</th>
                <td colspan="3"><input type="text" name="relName" class="required noErrorTip" value="${result.Row.relName}"></td>
            </tr>
            <tr>
                <th>备注说明：</th>
                <td colspan="3"><input type="text" name="reNote" value="${result.Row.reNote}"></td>
            </tr>
        </table>
    </form>
</div>
