<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="sys005Apply.saveCfg()"><span>保存</span></a></li>
    </ul>
</div>
<div class="pageContent">
    <form id="sys005Applyform" method="post" class="required-validate">
        <table class="form">
            <tr style="height: 60px;">
                <th width="20%"><label>要求上传图片：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" name="needPhoto" itemCd="COMMON_YES_NO" value="${needPhoto}" type="radio"/>
                </td>
            </tr>
            <tr style="height: 60px;">
                <th width="20%"><label>是否显示登陆框：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" name="needLogin" itemCd="COMMON_YES_NO" value="${needLogin}" type="radio"/>
                </td>
            </tr>
            <tr style="min-height: 60px;">
                <th><label>可选安置方式：</label></th>
                <td>
                    <table id="sys005ApplyTable" class="list" width="50%">
                        <thead>
                        <tr>
                            <td width="15%">安置码值</td>
                            <td width="50%">安置名称</td>
                            <td>操作 <a class="btnAdd" onclick="sys005Apply.addRow('sys005ApplyTable', this);">添加</a></td>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="hidden">
                            <td><input name="valueName" type="text"/></td>
                            <td><input name="valueCd" class="lsize" type="text"/></td>
                            <td>
                                <a class="btnDel" onclick="table.deleteRow(this);">删除</a>
                                <a class="btnAdd" onclick="table.addRow('sys005ApplyTable', this);">添加</a>
                                <a class="link" onclick="table.upRow('sys005ApplyTable', this);">[上移]</a>
                                <a class="link" onclick="table.downRow('sys005ApplyTable', this);">[下移]</a>
                            </td>
                        </tr>
                        <c:forEach items="${applyType}" var="item">
                            <tr>
                                <td><input name="valueCd" class="required" type="text" value="${item.valueCd}"/>
                                </td>
                                <td><input name="valueName" class="required lsize" type="text" value="${item.valueName}"/>
                                </td>
                                <td>
                                    <a class="btnDel" onclick="table.deleteRow(this);">删除</a>
                                    <a class="btnAdd" onclick="table.addRow('sys005ApplyTable', this);">添加</a>
                                    <a class="btnView" onclick="table.upRow('sys005ApplyTable', this);">[上移]</a>
                                    <a class="btnView" onclick="table.downRow('sys005ApplyTable', this);">[下移]</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr style="height: 60px;">
                <th><label>可选房屋社区：</label></th>
                <td>
                    <input type="text" name="keyWordTemp" onchange="sys005Apply.changeKeyWord(this);"/>
                    <label class="marl5 marr5 pad10 js_keyword"
                           style="position: relative;display: none;color:#ffffff;background-color:#6ab625;border-color:#6ab625;">
                        <input type="hidden" name="regAddr" value=""/>
                        <span class="in-block">${keyWord}</span>
                        <span class="removeX" onclick="$(this).parent().remove();">X</span>
                    </label>
                    <c:forEach items="${regAddr}" var="regAddrItem">
                        <label class="marl5 marr5 pad10 js_keyword"
                               style="position: relative;display: inline-block;color:#ffffff;background-color:#6ab625;border-color:#6ab625;">
                            <input type="hidden" name="regAddr" value="${regAddrItem.valueName}"/>
                            <span class="in-block">${regAddrItem.valueName}</span>
                            <span class="removeX" onclick="$(this).parent().remove();">X</span>
                        </label>
                    </c:forEach>
                </td>
            </tr>
        </table>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys005/js/sys005Apply.js"
               type="text/javascript"></oframe:script>