<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="tabs">
    <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
        <div class="tabsHeaderContent">
            <ul>
                <li><a href="javascript:void(0);"><span>基本信息</span></a></li>
                <li onclick="pj002.prjOrg(this);"><a href="javascript:void(0);"><span>项目组织</span></a></li>
                <li onclick="pj002.prjRole(this);"><a href="javascript:void(0);"><span>项目角色</span></a></li>
                <li onclick="pj002.prjStaff(this);"><a href="javascript:void(0);"><span>项目团队</span></a></li>
                <li onclick="pj002.prjFunc(this);"><a href="javascript:void(0);"><span>项目功能</span></a></li>
                <%--<li onclick="pj002.prjCtrl(this);"><a href="javascript:void(0);"><span>环节控制</span></a></li>--%>
                <li onclick="pj002.prjParam(this);"><a href="javascript:void(0);"><span>项目参数</span></a></li>
            </ul>
        </div>
    </div>
    <div class="tabsContent" id="pj002NavContext">
        <div layoutH="50">
            <div class="panelBar">
                <ul class="toolBar">
                    <li><a class="save" onclick="pj002.savePrjBase()"><span>保存项目</span></a></li>
                </ul>
            </div>
            <div layoutH="90">
                <form id="pj002form" method="post" class="required-validate">
                    <input type="hidden" name="prjCd" value="${cmpPrj.CmpPrj.prjCd}"/>
                    <input type="hidden" name="funcRole" value="${cmpPrj.CmpPrj.funcRole}"/>
                    <table class="border">
                        <tr>
                            <th width="10%"><label>项目名称：</label></th>
                            <td width="50%">
                                <input class="required" name="prjName" type="text" class="required"
                                       value="${cmpPrj.CmpPrj.prjName}"/>
                            </td>
                            <th width="10%"><label>项目编码：</label></th>
                            <td>
                                <c:choose>
                                    <c:when test="${cmpPrj.CmpPrj.prjCd == null}">
                                        <input class="required textInput" name="addPrjCd" type="number" min="0" value="${addPrjCd}"/>
                                    </c:when>
                                    <c:otherwise>
                                        ${cmpPrj.CmpPrj.prjCd}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><label>负责部门：</label></th>
                            <td style="position: relative;">
                                <input type="hidden" name="ownOrg" value="${cmpPrj.CmpPrj.ownOrg}"/>
                                <input name="ownStaffId" type="text" class="pull-left required" readonly
                                       title="<oframe:org orgId="${cmpPrj.CmpPrj.ownOrg}"
                                        property="Org.Node.nodePathName" prjCd="0"/>"
                                       value="<oframe:org orgId="${cmpPrj.CmpPrj.ownOrg}" prjCd="0"/>"/>
                                <a title="按地址检索房屋" class="btnLook" style="float: left;"
                                   onclick="$.fn.sltOrg(this,{args:{orgPrjCd: 0},offsetX: 3});">选择</a>
                            </td>
                            <th><label>施工城市：</label></th>
                            <td>
                                <oframe:select prjCd="${cmpPrj.CmpPrj.prjCd}" itemCd="CITIES" name="prjCity"
                                               value="${cmpPrj.CmpPrj.prjCity}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>项目状态：</label></th>
                            <td>
                                <oframe:select prjCd="${cmpPrj.CmpPrj.prjCd}" itemCd="COM_STATUS_CD" name="statusCd"
                                               value="${cmpPrj.CmpPrj.statusCd}"/>
                            </td>
                            <th><label>项目类型：</label></th>
                            <td>
                                <c:choose>
                                    <c:when test="${cmpPrj.CmpPrj.prjCd == null}">
                                        <oframe:select prjCd="${cmpPrj.CmpPrj.prjCd}" name="prjType" cssClass="required"
                                                       itemCd="PRJ_TYPE"
                                                       value="${cmpPrj.CmpPrj.prjType}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" value="${cmpPrj.CmpPrj.prjType}" name="prjType">
                                        <oframe:name prjCd="${cmpPrj.CmpPrj.prjCd}" itemCd="PRJ_TYPE"
                                                     value="${cmpPrj.CmpPrj.prjType}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th colspan="4" class="subTitle">
                                <label style="float: left;">项目描述:</label>
                            </th>
                        </tr>
                        <tr>
                            <td colspan="4" align="left">
                                <textarea name="prjDesc" class="editor simpleEditor"
                                          style="width: 100%;min-height: 200px;" rows="10"
                                          cols="75">${cmpPrj.CmpPrj.prjDesc}</textarea>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
        <div layoutH="50"></div>
        <div layoutH="50"></div>
        <div layoutH="50"></div>
        <div layoutH="50"></div>
        <%--<div layoutH="50"></div>--%>
        <div layoutH="50"></div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj002/js/pj002.js" type="text/javascript"/>
