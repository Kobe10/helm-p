<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="padding: 0 5px;">
    <div class="pageNav">
        <a class="current">消息管理--〉我的消息</a>
    </div>
    <div class="tabs mart5 ">
        <div class="tabsHeader">
            <div class="tabsHeaderContent">
                <ul>
                    <li class="selected" onclick="sys014.loadTabContext(1);">
                        <a href="javascript:void(0);"><span>未读消息</span></a>
                    </li>
                    <li onclick="sys014.loadTabContext(2);">
                        <a href="javascript:void(0);"><span>已阅消息</span></a>
                    </li>
                    <li class="js_load_tab"
                        onclick="sys014.loadTabContext(3);">
                        <a href="javascript:void(0);"><span>发送消息</span></a>
                    </li>
                </ul>
            </div>
        </div>

        <div class="tabsContent" style="padding: 0;border-top: 1px solid #3d91c8;">
            <div>
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="sys014.update()">
                            <a class="ensure" href="javascript:void(0)"><span>标记已读</span></a>

                        </li>
                        <li onclick="sys014.loadTabContext(1);">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                        </li>
                    </ul>
                </div>
                <form id="sys014unReadQueryForm">
                    <input type="hidden" name="entityName" value="NoticeInfo"/>
                    <input type="hidden" name="conditionName" value="NoticeInfo.noticeReadStatus,NoticeInfo.toStaffId">
                    <input type="hidden" name="condition" value="=,=">
                    <input type="hidden" name="conditionValue" value="0,${currentStaffId}">
                    <input type="hidden" name="sortColumn" value="NoticeInfo.createTime">
                    <input type="hidden" name="sortOrder" value="asc">
                    <input type="hidden" class="js_conditionValue" value="">
                    <input type="hidden" name="divId" value="unReadDiv">
                    <input type="hidden" name="forceResultField"
                           value="NoticeInfo.noticeReadStatus,NoticeInfo.noticeId">
                    <input type="hidden" name="resultField"
                           value="NoticeInfo.createStaffId,NoticeInfo.noticeContent,NoticeInfo.createTime">
                    <input type="hidden" name="forward" value="/oframe/sysmg/sys014/sys01401_list"/>
                    <input type="hidden" name="cmptName" value="QUERY_ENTITY_PAGE_DATA">
                </form>
                <div class="js_page" layoutH="90"></div>
            </div>
            <div>
                <div style="position: relative;">
                    <div class="panelBar">
                        <ul class="toolBar">
                            <li onclick="sys014.openQSch(2);">
                                <a class="find" href="javascript:void(0)"><span>检索</span></a>
                            </li>
                            <li onclick="sys014.loadTabContext(2);">
                                <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                            </li>
                        </ul>
                    </div>
                    <div id="sys014ReadDialog" class="hidden"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <div class="triangle triangle-up"
                             style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                        <table class="border">
                            <tr>
                                <th><label>发送用户：</label></th>
                                <td>
                                    <input name="NoticeInfo.createStaffId" hAttr="staffId" condition="=" type="hidden">
                                    <input name="NoticeInfo.createStaffName" type="text" class="pull-left autocomplete"
                                           value="${StaffName}" atOption="CODE.getStaffOpt"/>
                                    <a title="选择"
                                       onclick="$.fn.sltStaff(this,{offsetX: 0, fromOp:'sys01401', prjCd:'0'});"
                                       class="btnLook">选择</a>
                                </td>
                                <th><label>发送时间：</label></th>
                                <td>
                                    <input name="NoticeInfo.createTime" type="text"
                                           condition=">="
                                           class="date" datefmt="yyyyMMddHHmmss"
                                           style="width: 200px">
                                    --
                                    <input name="NoticeInfo.createTime" type="text" class="date"
                                           condition="<="
                                           datefmt="yyyyMMddHHmmss"
                                           style="width: 200px">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="6" align="center">
                                    <button onclick="sys014.queryNotice(2)" class="btn btn-primary js_query marr20">
                                        查询
                                    </button>
                                    <button onclick="sys014.closeQSch(2)" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <form id="sys014ReadQueryForm">
                    <input type="hidden" name="entityName" value="NoticeInfo"/>
                    <input type="hidden" name="conditionName" value="NoticeInfo.noticeReadStatus,NoticeInfo.toStaffId">
                    <input type="hidden" name="condition" value="=,=">
                    <input type="hidden" name="conditionValue" value="1,${currentStaffId}">
                    <input type="hidden" name="sortColumn" value="NoticeInfo.createTime">
                    <input type="hidden" name="sortOrder" value="desc">
                    <input type="hidden" class="js_conditionValue" value="">
                    <input type="hidden" name="forceResultField"
                           value="NoticeInfo.noticeReadStatus,NoticeInfo.noticeId">
                    <input type="hidden" name="divId" value="readDiv">
                    <input type="hidden" name="resultField"
                           value="NoticeInfo.createStaffId,NoticeInfo.createTime,NoticeInfo.noticeContent,NoticeInfo.statusTime">
                    <input type="hidden" name="forward" value="/oframe/sysmg/sys014/sys01402_list"/>
                    <input type="hidden" name="cmptName" value="QUERY_ENTITY_PAGE_DATA">
                </form>
                <div class="js_page" layoutH="90"></div>
            </div>
            <div style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="sys014.openQSch(3);">
                            <a class="find" href="javascript:void(0)"><span>检索</span></a>
                        </li>
                        <li onclick="sys014.loadTabContext(3);">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                        </li>
                    </ul>
                </div>
                <div id="sys014QSchDialog" class="hidden"
                     style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                    <div class="triangle triangle-up"
                         style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                    <table class="border">
                        <tr>

                            <th width="8%"><label>发送用户：</label></th>
                            <td>
                                <c:set var="haveRht" value="false"/>
                                <oframe:power prjCd="${param.prjCd}" rhtCd="query_staff_name">
                                    <c:set var="haveRht" value="true"/>
                                </oframe:power>
                                <c:choose>
                                    <c:when test="${haveRht}">
                                        <input name="NoticeInfo.createStaffName" type="text"
                                               class="pull-left autocomplete"
                                               value="${currentStaffName}" atOption="CODE.getStaffOpt"/>
                                        <a title="选择"
                                           onclick="$.fn.sltStaff(this,{offsetX: 0, fromOp:'sys01401', prjCd:'0'});"
                                           class="btnLook">选择</a>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="text" readonly disabled class="readonly"
                                               value="${currentStaffName}"/>
                                    </c:otherwise>
                                </c:choose>
                                <input name="NoticeInfo.createStaffId" hAttr="staffId"
                                       value="${currentStaffId}" condition="=" type="hidden">
                            </td>
                            <th width="8%"><label>接收用户：</label></th>
                            <td>
                                <input type="text"
                                       atOption="sys014.getOption"
                                       atUrl="sys014.getUrl"
                                       class="autocomplete" value=""/>
                                <input name="NoticeInfo.toStaffId" condition="=" type="hidden" value="">
                            </td>
                            <th width="8%"><label>发送时间：</label></th>
                            <td>
                                <input name="NoticeInfo.createTime" type="text"
                                       condition=">="
                                       class="date" datefmt="yyyyMMddHHmmss"
                                       style="width: 100px">
                                --
                                <input name="NoticeInfo.createTime" type="text" class="date"
                                       condition="<=" datefmt="yyyyMMddHHmmss"
                                       style="width: 100px">
                            </td>
                            <th width="8%"><label>阅读状态：</label></th>
                            <td width="10%">
                                <oframe:select prjCd="${param.prjCd}" itemCd="MESSAGE_STATUS"
                                               name="NoticeInfo.noticeReadStatus"
                                               value=""/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="8" align="center">
                                <button onclick="sys014.queryNotice(3)" class="btn btn-primary js_query marr20">查询
                                </button>
                                <button onclick="sys014.closeQSch(3)" class="btn btn-info">关闭</button>
                            </td>
                        </tr>
                    </table>
                </div>
                <form id="sys014queryForm">
                    <input type="hidden" name="entityName" value="NoticeInfo"/>
                    <input type="hidden" name="conditionName" value="NoticeInfo.createStaffId">
                    <input type="hidden" name="condition" value="=">
                    <input type="hidden" name="conditionValue" value="${currentStaffId}">
                    <input type="hidden" name="sortColumn" value="NoticeInfo.createTime">
                    <input type="hidden" name="sortOrder" value="desc">
                    <input type="hidden" class="js_conditionValue" value="">
                    <input type="hidden" name="forceResultField"
                           value="NoticeInfo.noticeReadStatus,NoticeInfo.noticeId">
                    <input type="hidden" name="divId" value="queryDiv">
                    <input type="hidden" name="resultField"
                           value="NoticeInfo.createStaffId,NoticeInfo.toStaffId,NoticeInfo.noticeContent,NoticeInfo.createTime,NoticeInfo.noticeReadStatus,NoticeInfo.statusTime">
                    <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys014/sys01402_list"/>
                    <input type="hidden" name="cmptName" value="QUERY_ENTITY_PAGE_DATA">
                </form>
                <div class="js_page" layoutH="90"></div>
            </div>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys014/js/sys014.js" type="text/javascript"/>
<script type="text/javascript">
    $(document).ready(function () {
        sys014.loadTabContext(1);
    });
</script>