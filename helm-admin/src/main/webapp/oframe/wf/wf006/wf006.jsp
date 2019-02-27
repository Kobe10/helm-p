<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/3/12 0012 10:33
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <div class="panelBar">
        <ul class="toolBar">
            <c:if test="${editFlag}">
                <li><a class="save" onclick="wf006.saveForm()"><span>保存</span></a></li>
                <li><a class="ensure" onclick="wf006.publishDialog('${formInfo.Row.formId}','${formInfo.Row.formDesc}')"><span>发布</span></a></li>
            </c:if>
            <li><a class="close" onclick="navTab.closeCurrentTab();"><span>取消</span></a></li>
        </ul>
    </div>
    <div class="tabs mart5" layoutH="60">
        <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
            <div class="tabsHeaderContent">
                <ul>
                    <li><a href="javascript:void(0);"><span>基本信息</span></a></li>
                    <li><a href="javascript:void(0);"><span>前置条件</span></a></li>
                    <li><a href="javascript:void(0);"><span>数据准备</span></a></li>
                    <li><a href="javascript:void(0);"><span>模板定义</span></a></li>
                    <li><a href="javascript:void(0);"><span>操作定义</span></a></li>
                </ul>
            </div>
        </div>
        <form id="wf006FormDesign" method="post" class="required-validate">
            <input type="hidden" name="formId" value="${formInfo.Row.formId}">
            <input type="hidden" name="formStatus" value="${formInfo.Row.formStatus}">

            <div class="tabsContent" id="wf006DsContainer">
                <div layoutH="100">
                    <div>
                        <table class="border">
                            <tr>
                                <th>表单编码：</th>
                                <td><input type="text" name="formCd" class="required" value="${formInfo.Row.formCd}">
                                </td>
                                <th>表单名称：</th>
                                <td><input type="text" name="formName" class="required"
                                           value="${formInfo.Row.formName}"></td>
                            </tr>
                            <tr>
                                <th>表单分类：</th>
                                <td>
                                    <input type="hidden" name="formTypeId" value="${formInfo.Row.formTypeId}"/>
                                    <input type="text" name="formType" class="required readonly" readonly
                                           onclick="wf006.sltFormType(this);"
                                           value="${formInfo.Row.formType}"></td>
                                <th>状态时间：</th>
                                <td><oframe:date value="${formInfo.Row.formStatuDate}" format="yyyy-MM-dd"/></td>
                            </tr>
                            <tr>
                                <th>表单描述：</th>
                                <td colspan="3">
                                    <textarea name="formDesc" rows="10">${formInfo.Row.formDesc}</textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div layoutH="100">
                    <div>
                        <table class="border">
                            <tr>
                                <th width="10%">配置说明：</th>
                                <td><span class="marl5">请按照模板接口要求配置生成前提条件、模板选取规则</span></td>
                            </tr>
                        </table>
                        <textarea name="exeFormRule" class="hidden">${formInfo.Row.exeFormRule}</textarea>
                        <iframe name="wf006PreCondIFrame" allowTransparency="false" id="wf006PreCondIFrame"
                                width="99.5%;"
                                style="background-color:#f5f9fc;"
                                src="${pageContext.request.contextPath}/oframe/wf/wf006/wf006-textCode.gv?textarea=exeFormRule&model=groovy&content=navTab"
                                layoutH="140">
                        </iframe>
                    </div>
                </div>
                <div layoutH="100">
                    <div>
                        <table class="border">
                            <tr>
                                <th width="10%">数据类型：</th>
                                <td>
                                    <oframe:select name="exeFormDataType" itemCd="FORM_DATA_TYPE" type="radio"
                                                   id="wf06FormDataType" value="${formInfo.Row.exeFormDataType}"/>
                                </td>
                            </tr>
                        </table>
                        <textarea name="exeDataParam" class="hidden">${formInfo.Row.exeDataParam}</textarea>
                        <iframe name="wf006dataPrepIFrame" allowTransparency="false" id="wf006dataPrepIFrame"
                                width="99.5%;"
                                style="background-color:#f5f9fc;"
                                src="${pageContext.request.contextPath}/oframe/wf/wf006/wf006-textCode.gv?textarea=exeDataParam&model=groovy&content=navTab"
                                layoutH="140">
                        </iframe>
                    </div>
                </div>
                <div layoutH="100">
                    <div>
                        <table id="wf006TempDef_tab" class="list" width="100%">
                            <thead>
                            <tr>
                                <td width="15%">模板编码</td>
                                <td width="20%">模板名称</td>
                                <td width="15%">模板类型</td>
                                <td width="15%">模板更新时间</td>
                                <td width="30%">操作
                                    <c:if test="${editFlag}">
                                    <span class="link mar10 btnAdd"
                                          onclick="wf006.addRow('wf006TempDef_tab', this);">[新增模板]</span>
                                    </c:if>
                                </td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="hidden">
                                <td>
                                    <input name="formTemplateId" type="hidden" value=""/>
                                    <input name="tempFilePath" type="hidden" value=""/>
                                    <input name="oldTemplateCd" type="hidden" value=""/>
                                    <input name="formTemplateCd" type="text" class="noErrorTip " value=""/>
                                </td>
                                <td>
                                    <input name="formTemplateName" type="text" class="noErrorTip " value=""/>
                                </td>
                                <td><oframe:select name="sysFormTemplateType" withEmpty="true" cssClass="noErrorTip "
                                                   itemCd="FORM_TEMPLATE_TYPE" value=""/>
                                </td>
                                <td><label class="js_upload_time"></label></td>
                                <td>
                                    <c:if test="${editFlag}">
                                    <span class="marl5 marr5 link import">
                                        <span onclick="wf006.uploadTemp(this);">
                                            <input style="width:70px; position:absolute;margin-top: 5px;
                                                     opacity:0;filter:alpha(opacity=0);z-index:11; cursor: pointer"
                                                   id="uploadTempFile0" name="uploadTempFile" type="file">
                                            [上传模板]
                                        </span>
                                    </span>
                                    </c:if>
                                    <span class="marl5 link marr5" onclick="wf006.downTemp(this);">[下载模板]</span>
                                    <c:if test="${editFlag}">
                                        <span class="marl5 link marr5" onclick="wf006.editTemp(this);">[编辑模板]</span>
                                        <span class="marl5 link marr5" onclick="table.deleteRow(this);">[删除模板]</span>
                                    </c:if>
                                </td>
                            </tr>
                            <c:forEach items="${formTempList}" var="item" varStatus="varStatus">
                                <tr>
                                    <td>
                                        <input name="formTemplateId" type="hidden" value="${item.Row.formTemplateId}"/>
                                        <input name="tempFilePath" type="hidden" value=""/>
                                        <input name="oldTemplateCd" type="hidden" value="${item.Row.formTemplateCd}"/>
                                        <input name="formTemplateCd" type="text" class="required noErrorTip"
                                               value="${item.Row.formTemplateCd}"/>
                                    </td>
                                    <td>
                                        <input name="formTemplateName" type="text" class="required noErrorTip "
                                               value="${item.Row.formTemplateName}"/>
                                    </td>
                                    <td>
                                        <input type="hidden" name="sysFormTemplateTypeOld" value="${item.Row.sysFormTemplateType}"/>
                                        <oframe:select name="sysFormTemplateType" cssClass="required noErrorTip"
                                                       itemCd="FORM_TEMPLATE_TYPE" withEmpty="true"
                                                       value="${item.Row.sysFormTemplateType}" onChange="wf006.chengeTemplateType(this);"/>
                                    </td>
                                    <td>
                                        <label class="js_upload_time">
                                            <oframe:date value="${item.Row.sysTemplateDate}"/>
                                        </label>
                                    </td>
                                    <td>
                                        <c:if test="${editFlag}">
                                        <span class="marl5 marr5 link import">
                                            <span style="cursor: pointer;" onclick="wf006.uploadTemp(this);">
                                                <input style="width:70px; position:absolute;margin-top: 5px;
                                                         opacity:0;filter:alpha(opacity=0);z-index:11; cursor:pointer;"
                                                       id="uploadTempFile_${varStatus.index}" name="uploadTempFile"
                                                       type="file">
                                                [上传模板]
                                            </span>
                                        </span>
                                        </c:if>
                                        <span class="marl5 marr5 link" onclick="wf006.downTemp(this);">[下载模板]</span>
                                        <c:if test="${editFlag}">
                                        <span class="marl5 marr5 link" onclick="wf006.editTemp(this);">[编辑模板]</span>
                                            <span class="marl5 marr5 link"
                                                  onclick="table.deleteRow(this);">[删除模板]</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div layoutH="100">
                    <div>
                        <table id="wf006OperDef_tab" class="list" width="100%">
                            <thead>
                            <tr onclick="wf006.showHideOp(this);">
                                <td width="10%">操作名称</td>
                                <td width="20%">调用方法</td>
                                <td width="15%">权限控制</td>
                                <td width="8%">展示样式</td>
                                <td width="10%">展示模板</td>
                                <td width="15%">显示条件</td>
                                <td>操作
                                    <c:if test="${editFlag}">
                                        <a class="btnAdd" onclick="table.addRow('wf006OperDef_tab', this);">添加</a>
                                    </c:if>
                                </td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="hidden">
                                <td><input name="operName" type="text"/></td>
                                <td><input name="operOnClick" type="text" class="titleFormat"/></td>
                                <td><input name="operRhtCd" type="text"/></td>
                                <td><input name="operClass" type="text"/></td>
                                <td><input name="operTemplate" type="text"/></td>
                                <td><input name="operRht" type="text" class="titleFormat"/></td>
                                <td>
                                    <c:if test="${editFlag}">
                                        <a class="marl5 btnView" onclick="table.upRow('wf006OperDef_tab', this);">上移</a>
                                        <a class="marl5 btnView"
                                           onclick="table.downRow('wf006OperDef_tab', this);">下移</a>
                                        <a class="marl5 btnAdd" onclick="table.addRow('wf006OperDef_tab', this);">增加</a>
                                        <a class="marl5 btnDel" onclick="table.deleteRow(this);">删除</a>
                                    </c:if>
                                </td>
                            </tr>
                            <c:forEach items="${opDefList}" var="item">
                                <tr>
                                    <td><input name="operName" type="text" class="required"
                                               value="${item.OperationDef.operName}"/></td>
                                    <td><input name="operOnClick" type="text" class="required titleFormat"
                                               title="${item.OperationDef.operOnClick}"
                                               value="${item.OperationDef.operOnClick}"/></td>
                                    <td><input name="operRhtCd" type="text" value="${item.OperationDef.operRhtCd}"/>
                                    </td>
                                    <td><input name="operClass" type="text" value="${item.OperationDef.operClass}"/>
                                    </td>
                                    <td><input name="operTemplate" type="text"
                                               value="${item.OperationDef.operTemplate}"/></td>
                                    <td><input name="operRht" type="text" class="titleFormat"
                                               title="${item.OperationDef.operRht}"
                                               value="${item.OperationDef.operRht}"/></td>
                                    <td>
                                        <c:if test="${editFlag}">
                                            <a class="marl5 btnView"
                                               onclick="table.upRow('wf006OperDef_tab', this);">上移</a>
                                            <a class="marl5 btnView"
                                               onclick="table.downRow('wf006OperDef_tab', this);">下移</a>
                                            <a class="marl5 btnAdd"
                                               onclick="table.addRow('wf006OperDef_tab', this);">增加</a>
                                            <a class="marl5 btnDel" onclick="table.deleteRow(this);">删除</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <table class="list" width="100%">
                            <thead>
                            <tr>
                                <td style="text-align: left;font-weight: bold" width="100%">
                                    <span class="padl10">扩展操作定义</span>
                                </td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>
                                    <textarea name="operDefCode" class="hidden">${operDefCode}</textarea>
                                    <iframe name="wf006operDefIFrame" allowTransparency="false" id="wf006operDefIFrame"
                                            width="99.5%;"
                                            style="background-color:#f5f9fc;"
                                            src="${pageContext.request.contextPath}/oframe/wf/wf006/wf006-textCode.gv?textarea=operDefCode&model=javascript&content=navTab"
                                            layoutH="180">
                                    </iframe>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/wf/wf006/js/wf006.js" type="text/javascript"/>