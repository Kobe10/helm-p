<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:choose>
            <c:when test="${method != 'view'}">
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                           name="保存" rhtCd="edit_prj_ques_rht"
                           onClick="pj012.save();"/>
            </c:when>
        </c:choose>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div layoutH="105">
    <form id="pj01201frm" method="post">
        <input type="hidden" name="qId" value="${prjQAndA.PrjQAndA.qId}"/>
        <input type="hidden" name="prjCd" value="${prjQAndA.PrjQAndA.prjCd}"/>

        <div class="withTitle ${writeAbleClass}">
            <h1>问题信息</h1>
            <table class="form">
                <tr>
                    <th>
                        <label>提问人区域：</label>
                    </th>
                    <td>
                        <input type="text" name="qReg" value="${prjQAndA.PrjQAndA.qReg}"/>(例：XX社区)
                    </td>
                    <th>
                        <label>提问人地址：</label>
                    </th>
                    <td>
                        <input type="text" class="msize required" name="qAddr" value="${prjQAndA.PrjQAndA.qAddr}"/>(例：XX胡同XX号院XX)
                    </td>
                </tr>
                <tr>
                    <th>
                        <label>提问人姓名：</label>
                    </th>
                    <td>
                        <input type="text" name="qName" value="${prjQAndA.PrjQAndA.qName}"/>
                    </td>
                    <th>
                        <label>联系方式：</label>
                    </th>
                    <td>
                        <input type="text" name="qTele" value="${prjQAndA.PrjQAndA.qTele}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label>提问时间：</label>
                    </th>
                    <td colspan="3">
                        <c:if test="${qTime != null}">
                            <input type="text" class="date" datefmt="yyyy-MM-dd HH:mm:ss" name="qTime"
                                   value='<fmt:formatDate value="${qTime}" type="both" pattern="yyyy-MM-dd HH:mm:ss"/>'/>
                        </c:if>
                        <c:if test="${qTime == null}">
                            <input type="text" class="date" datefmt="yyyy-MM-dd HH:mm:ss" name="qTime"
                                   value="${qTime}"/>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label>问题详情：</label>
                    </th>
                    <td align="left" colspan="3">
                        <textarea rows="10" cols="120" class="required"
                                  name="qText">${prjQAndA.PrjQAndA.qText}</textarea>
                    </td>
                </tr>
            </table>
        </div>
        <div class="withTitle ${writeAbleClass}">
            <h1>解答信息</h1>
            <table class="form">
                <tr>
                    <th>
                        <label>问题状态：</label>
                    </th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" name="qStatus" itemCd="QUES_STATUS"
                                       value="${prjQAndA.PrjQAndA.qStatus}"> </oframe:select>
                    </td>
                    <th>
                        <label>解答时间：</label>
                    </th>
                    <td>
                        <c:if test="${aTime != null}">
                            <input type="text" class="date" datefmt="yyyy-MM-dd HH:mm:ss" name="aTime"
                                   value='<fmt:formatDate value="${aTime}" type="both" pattern="yyyy-MM-dd HH:mm:ss"/>'/>
                        </c:if>
                        <c:if test="${aTime == null}">
                            <input type="text" class="date" datefmt="yyyy-MM-dd HH:mm:ss" name="aTime"
                                   value="${aTime}"/>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label>问题解答：</label>
                    </th>
                    <td colspan="3">
                        <textarea rows="10" cols="120" name="aText">${prjQAndA.PrjQAndA.aText}</textarea>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>
