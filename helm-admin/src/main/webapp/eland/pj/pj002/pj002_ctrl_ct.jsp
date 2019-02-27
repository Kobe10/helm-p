<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style>
    .input {
        width: auto;
    }
</style>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="ct001CtrlCt.saveCtCtrl()"><span>保存签约控制</span></a></li>
        <li><a class="delete" onclick="ct001CtrlCt.pauseCtCtrl()"><span>暂停居民签约</span></a></li>
        <li><a class="hight-level" onclick="ct001CtrlCt.resetCtData()"><span>重置签约数据</span></a></li>
    </ul>
</div>

<div>
    <form id="ct001_ct" method="post" class="required-validate entermode">
        <input name="scId" value="${ctrlInfo.SysControlInfo.scId}" type="hidden"/>
        <input name="scType" value="1" type="hidden"/>
        <table class="border">
            <tr>
                <th width="15%">签约启动开关：</th>
                <td>
                    <c:if test="${ctrlInfo.SysControlInfo.startCd == '1'}">
                        <input id="startCd1" type="radio" name="startCd" value="1" checked>
                        <label for="startCd1" title="启动控制签约进度（各控制时间段生效）" class="cursorhp">启动签约控制</label>
                        <%--<input id="startCd2" type="radio" name="startCd" value="2">--%>
                        <%--<label for="startCd2" title="暂停系统内所有签约动作" class="cursorhp">暂停签约</label>--%>
                        <input id="startCd3" type="radio" name="startCd" value="3">
                        <label for="startCd3" title="开放所有签约动作（各控制时间段 无效）" class="cursorhp">关闭签约控制</label>
                    </c:if>
                    <c:if test="${ctrlInfo.SysControlInfo.startCd == '2'}">
                        <input id="startCd1" type="radio" name="startCd" value="1">
                        <label for="startCd1" title="启动控制签约进度（各控制时间段生效）" class="cursorhp">启动签约控制</label>
                        <%--<input id="startCd2" type="radio" name="startCd" value="2" checked>--%>
                        <%--<label for="startCd2" title="暂停系统内所有签约动作" class="cursorhp">暂停签约</label>--%>
                        <input id="startCd3" type="radio" name="startCd" value="3">
                        <label for="startCd3" title="开放所有签约动作（各控制时间段 无效）" class="cursorhp">关闭签约控制</label>
                    </c:if>
                    <c:if test="${ctrlInfo.SysControlInfo.startCd == '3' || ctrlInfo.SysControlInfo.startCd == '' || ctrlInfo.SysControlInfo.startCd == null}">
                        <input id="startCd1" type="radio" name="startCd" value="1">
                        <label for="startCd1" title="启动控制签约进度（各控制时间段生效）" class="cursorhp">启动签约控制</label>
                        <%--<input id="startCd2" type="radio" name="startCd" value="2">--%>
                        <%--<label for="startCd2" title="暂停系统内所有签约动作" class="cursorhp">暂停签约</label>--%>
                        <input id="startCd3" type="radio" name="startCd" value="3" checked>
                        <label for="startCd3" title="开放所有签约动作（各控制时间段 无效）" class="cursorhp">关闭签约控制</label>
                    </c:if>
                </td>
            </tr>
            <tr>
                <th>下一签约序号：</th>
                <td>${ctOrder}</td>
            </tr>
            <tr>
                <th>签约间隔时长：</th>
                <td><input type="text" class="input digits required" name="spaceTime" min="1" max="60"
                           value="${ctrlInfo.SysControlInfo.spaceTime}">(分钟)
                </td>
            </tr>
            <tr>
                <th>阅读协议时长：</th>
                <td><input type="text" class="input digits required" name="readTime" min="0" max="60"
                           value="${ctrlInfo.SysControlInfo.readTime}">(分钟)
                </td>
            </tr>
            <tr>
                <th>预签约时间段：</th>

                <td><input type="text" name="preCtDateSt" class="input required date" datefmt="yyyy-MM-dd"
                           value='<oframe:date value="${ctrlInfo.SysControlInfo.preCtDateSt}" format="yyyy-MM-dd"/>'>
                    —
                    <input type="text" name="preCtDateEnd" class="input required date" datefmt="yyyy-MM-dd"
                           value='<oframe:date value="${ctrlInfo.SysControlInfo.preCtDateEnd}" format="yyyy-MM-dd"/>'>
                </td>
            </tr>

            <tr>
                <th>暂停时间段：</th>
                <td>
                    <table id="pauseTable" width="90%" style="text-align: center">
                        <tr style="background-color: #ff7976; color: #ffffff">
                            <td width="35%" style="font-size: 15px">开始时间</td>
                            <td width="25%" style="font-size: 15px">结束时间</td>
                            <td width="20%" style="font-size: 15px">暂停时长</td>
                            <td width="20%" style="font-size: 15px">操作</td>
                        </tr>
                        <c:forEach items="${ctTimes}" var="item">
                            <c:if test="${item.CtTimeSlice.ctTimeType == '1'}">
                                <tr class="js_tr_time">
                                    <input type="hidden" name="pCtTimeSt" value="${item.CtTimeSlice.ctTimeSt}"/>
                                    <input type="hidden" name="pCtTimeEnd" value="${item.CtTimeSlice.ctTimeEnd}"/>
                                    <input type="hidden" name="pEndTimeSpace" value="${item.CtTimeSlice.endTimeSpace}"/>
                                    <input type="hidden" name="pCtTimeType" value="${item.CtTimeSlice.ctTimeType}"/>
                                    <td>
                                        <oframe:date value="${item.CtTimeSlice.showCtTimeSt}"
                                                     format="yyyy-MM-dd HH:mm:ss"/>
                                    </td>
                                    <td>
                                        <oframe:date value="${item.CtTimeSlice.showCtTimeEnd}"
                                                     format="yyyy-MM-dd HH:mm:ss"/>
                                    </td>
                                    <td>${item.CtTimeSlice.stopLong} 秒</td>
                                    <td>
                                        <span class="link marr5" onclick="table.deleteRow(this);">删除</span>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </table>
                </td>
            </tr>
            <tr>
                <th>签约时间段：</th>
                <td>
                    <table id="signTable_booklet" width="90%" style="text-align: center">
                        <tr style="background-color: #e7f2f8;">
                            <td width="35%" style="font-size: 15px">开始时间</td>
                            <td width="35%" style="font-size: 15px">结束时间</td>
                            <td width="30%"><span class="link js_reload"
                                                  onclick="ct001CtrlCt.addDate('signTable_booklet',this)">添加</span>
                            </td>
                        </tr>
                        <tr class="js_hidden_tr js_tr_time" style="display: none">
                            <td><input type="text" name="ctTimeSt" class="input noErrorTip date" datefmt="HH:mm"></td>
                            <td><input type="text" name="ctTimeEnd" class="input noErrorTip date" datefmt="HH:mm"></td>
                            <td>
                                <span class="link marr5" onclick="ct001CtrlCt.deleteDate(this);">删除</span>
                                <span class="link marr5" onclick="table.upRow('signTable_booklet',this);">上移</span>
                                <span class="link marr5" onclick="table.downRow('signTable_booklet',this);">下移</span>
                            </td>
                        </tr>
                        <c:forEach items="${ctTimes}" var="item">
                            <c:if test="${item.CtTimeSlice.ctTimeType != '1'}">
                                <tr class="js_tr_time">
                                    <td><input type="text" name="ctTimeSt" class="input required noErrorTip date"
                                               datefmt="HH:mm" value="${item.CtTimeSlice.ctTimeSt}"></td>
                                    <td><input type="text" name="ctTimeEnd" class="input required noErrorTip date"
                                               datefmt="HH:mm" value="${item.CtTimeSlice.ctTimeEnd}"></td>
                                    <td>
                                        <span class="link marr5" onclick="ct001CtrlCt.deleteDate(this);">删除</span>
                                        <span class="link marr5"
                                              onclick="table.upRow('signTable_booklet',this);">上移</span>
                                        <span class="link marr5"
                                              onclick="table.downRow('signTable_booklet',this);">下移</span>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </table>
                </td>
            </tr>
        </table>
    </form>
</div>
