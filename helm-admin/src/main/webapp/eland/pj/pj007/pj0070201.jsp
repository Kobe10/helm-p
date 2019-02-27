<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="pageContent" layoutH="1" style="padding: 0;margin: 0;">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="add" onclick="pj0070201.initHouseInfo('${fromOp}')"><span>确定生成</span></a></li>
        </ul>
    </div>
    <form id="pj0070201form" method="post" class="required-validate">
        <div layoutH="43">
            <table id="pj0070201Table" class="form">
                <tr>
                    <th width="20%"><label>楼房层数：</label></th>
                    <td>
                        <input type="text" name="initFloorNm" value="1"
                               class="required number" onchange="pj0070201.flashSetting('1');"/>
                        <input type="hidden" name="initFloorNmBak">
                    </td>
                </tr>
                <tr>
                    <th><label>楼层编号：</label></th>
                    <td>
                        <ul class="floorSetting">
                            <li class="hidden" style="float: left; border: 1px blue dotted; padding: 5px;margin: 2px;">
                                <div>
                                    <input type="text" tabindex="100" name="floorNb" style="width: 40px;height: 15px;">
                                </div>
                            </li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <th><label>单元数量：</label></th>
                    <td>
                        <input type="text" name="initUnNm" onchange="pj0070201.flashSetting('2');"
                               class="required number" value="1"/>
                        <input type="hidden" name="initUnNmBak">
                    </td>
                </tr>
                <tr>
                    <th><label>单元名称：</label></th>
                    <td>
                        <ul class="unitSetting">
                            <li class="hidden" style="float: left; border: 1px blue dotted; padding: 5px;margin: 2px;">
                                <div>
                                    <input type="text" name="unitNb" onblur="pj0070201.changeUnitName(this)"
                                           tabindex="300" style="width: 60px;height: 15px;">
                                </div>
                            </li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <th><label>每层户数：</label></th>
                    <td>
                        <input type="text" class="required number" onblur="pj0070201.flashSetting('3');"
                               name="initHouseNm" value="1"/>
                        <input type="hidden" name="initHouseNmBak">
                    </td>
                </tr>
                <tr>
                    <th><label>户型设置：</label></th>
                    <td>
                        <ul class="houseSetting">
                            <li class="hidden" style="float: left; border: 1px blue dotted; padding: 5px;margin: 2px;">
                                <div class="marb5">
                                    单元：
                                    <select style="width: 60px" name="hsUnit"></select>
                                </div>
                                <div class="marb5">
                                    门号： <input type="text" name="hsNm" style="width: 60px;height: 15px;">
                                </div>
                                <div class="marb5">
                                    户型：
                                    <oframe:select prjCd="${param.prjCd}" style="width: 60px" collection="${hxMap}" name="hsHx"
                                                   value="${hsHx}"></oframe:select>
                                </div>
                            </li>
                        </ul>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj007/js/pj0070201.js" type="text/javascript"/>
