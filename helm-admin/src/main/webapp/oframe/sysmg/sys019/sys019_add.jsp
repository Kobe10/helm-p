<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style type="text/css">
    #hsSubTable tr td, #hsSubTable tr th, #hsSubTable1 tr th, #hsSubTable1 tr td {
        text-align: center;
    }
</style>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys019.editMsg();"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div layoutH="55" style="border: 1px solid rgb(233, 233, 233);">
        <form id="sys019MsgForm">
            <input type="hidden" name="messageDefId" value="${MessageDef.MessageDef.messageDefId}"/>

            <table class="border marb5" width="100%" id="sys019cuspro">
                <tr>
                    <th>
                        <lable>消息类型：</lable>
                    </th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${prjCdMap}" name="messageDefType"
                                       value="${MessageDef.MessageDef.messageDefType}"
                                       style="width:81%"/>
                    </td>
                    <th>
                        <lable>消息创建时间：</lable>
                    </th>
                    <td>
                        <input type="text"
                               value='<oframe:date value="${MessageDef.MessageDef.messageCreateTime}" _default="now" />'
                               class="date"
                               name="messageCreateTime"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <lable>执行消费定义：</lable>
                    </th>
                    <td>
                        <input type="text" value="${MessageDef.MessageDef.dealTemplate}"
                               placeholder="格式:baseMessageTemplateBean"
                               name="dealTemplate"/>
                    </td>
                    <th>
                        <lable>消息配置目标：</lable>
                    </th>
                    <td>
                        <input type="text" value="${MessageDef.MessageDef.messageDefTarget}"
                               name="messageDefTarget"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <lable>消息描述：</lable>
                    </th>
                    <td colspan="3">
                        <input type="text" value="${MessageDef.MessageDef.messageDefDesc}"
                               name="messageDefDesc"/>
                    </td>
                </tr>
                <tr style="background-color: #e2edf3;">
                    <td colspan="4"><strong class="marl10">[消息生产者定义]</strong></td>
                </tr>
                <tr>
                    <td colspan="4">
                        <table id="hsSubTable" class="border">
                            <thead>
                            <tr style="background-color: #f0f0f0">
                                <th width="15%">生产者名称</th>
                                <th width="10%">生产者类型</th>
                                <th width="10%">消息生产实体</th>
                                <th width="10%">生产消息条件</th>
                                <th width="15%">适配规则</th>
                                <th width="7%">消息生产者状态</th>
                                <th width="8%">生产者创建时间</th>
                                <th width="10%">操作<span style="cursor: pointer"
                                                        onclick="sys019.addRow('hsSubTable',this,'pro')">&nbsp;&nbsp;&nbsp;&nbsp;[插入]</span>
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <%--隐藏的生产者模板tr--%>
                            <tr class="hidden js_pro">
                                <td><input name="producerName" type="text" placeholder="不能为空！"/></td>
                                <td><input name="producerType" type="text"/></td>
                                <td><input name="producerEntity" type="text"/></td>
                                <td><input name="producerCondition" type="text"/></td>
                                <td><input name="producerRuleCd" type="text"/></td>
                                <td><oframe:select prjCd="${param.prjCd}" name="producerState"
                                                   itemCd="SYS_PRODUCER_DEF"/></td>
                                <td><input type="text" value='<oframe:date value="" _default="now" />' class="date"
                                           name="producerCreateTime"/>
                                </td>
                                <td>
                                    <span onclick="table.deleteRow(this)" style="cursor: pointer">[删除]</span>
                                </td>
                            </tr>
                            <c:forEach items="${messageProducer}" var="item" varStatus="varStatus">
                                <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
                                <tr>
                                    <td><input name="producerName" class="textInput" type="text"
                                               value="${item.messageProducer.producerName}"/></td>
                                    <td><input name="producerType" class="textInput" type="text"
                                               value="${item.messageProducer.producerType}"/></td>
                                    <td><input name="producerEntity" class="textInput" type="text"
                                               value="${item.messageProducer.producerEntity}"/></td>
                                    <td><input name="producerCondition" class="textInput" type="text"
                                               value="${item.messageProducer.producerCondition}"/></td>
                                    <td><input name="producerRuleCd" class="textInput" type="text"
                                               value="${item.messageProducer.producerRuleCd}"/>
                                    </td>
                                    <td><oframe:select prjCd="${param.prjCd}" name="producerState"
                                                       itemCd="SYS_PRODUCER_DEF"
                                                       value="${item.messageProducer.producerState}"/></td>
                                    <td>
                                        <input type="text"
                                               value='<oframe:date value="${item.messageProducer.producerCreateTime}"/>'
                                               class="textInput" placeholder="格式:YYYY-MM-DD"
                                               name="producerCreateTime"/>
                                    </td>
                                    <td>
                                        <span onclick="table.deleteRow(this)" style="cursor: pointer">[删除]</span>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr style="background-color: #e2edf3;">
                    <td colspan="4"><strong class="marl10">[消息消费者定义]</strong></td>
                </tr>
                <tr>
                    <td colspan="4">
                        <table id="hsSubTable1" class="border">
                            <thead>
                            <tr style="background-color: #f0f0f0">
                                <th width="15%">消息消费者名称</th>
                                <th width="40%">消息消费者描述</th>
                                <th width="10%">消费者状态</th>
                                <th width="10%">消费者创建时间</th>
                                <th width="10%">操作<span style="cursor: pointer"
                                                        onclick="sys019.addRow('hsSubTable1',this,'cus')">&nbsp;&nbsp;&nbsp;&nbsp;[插入]</span>
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <%--隐藏的消费者模板tr--%>
                            <tr class="hidden js_cus">
                                <td><input name="consumerName" type="text" placeholder="不能为空！"/></td>
                                <td><input name="consumerDesc" type="text"/></td>
                                <td><oframe:select prjCd="${param.prjCd}" name="consumerState"
                                                   itemCd="SYS_CONSUMER_DEF"/></td>
                                <td>
                                    <input type="text" value='<oframe:date value="" _default="now" />' class="date"
                                           name="consumerCreateTime"/>
                                </td>
                                <td>
                                    <span onclick="table.deleteRow(this)" style="cursor: pointer">[删除]</span>
                                </td>
                            </tr>
                            <c:forEach items="${messageConsumer}" var="item" varStatus="varStatus">
                                <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
                                <tr>
                                    <td><input name="consumerName" type="text" class="textInput"
                                               value="${item.messageConsumer.consumerName}"/>
                                    </td>
                                    <td><input name="consumerDesc" type="text" class="textInput"
                                               value="${item.messageConsumer.consumerDesc}"/>
                                    </td>
                                    <td><oframe:select prjCd="${param.prjCd}" name="consumerState"
                                                       itemCd="SYS_CONSUMER_DEF"
                                                       value="${item.messageConsumer.consumerState}"/>
                                    </td>
                                    <td>
                                        <input type="text"
                                               value='<oframe:date value="${item.messageConsumer.consumerCreateTime}"/>'
                                               class="textInput" placeholder="格式:YYYY-MM-DD"
                                               name="consumerCreateTime"/>
                                    </td>
                                    <td>
                                        <span onclick="table.deleteRow(this)" style="cursor: pointer">[删除]</span>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" style="background-color: #e2edf3">
                        <strong class="marl10">[消费者模板定义]</strong>
                    </td>
                </tr>
            </table>
            <textarea name="messageDefContent" class="hidden">${MessageDef.MessageDef.messageDefContent}</textarea>
            <iframe name="sys019IFrame" allowTransparency="false" id="sys019IFrame" width="99.5%;"
                    style="background-color:#e2edf3;"
                    src="${pageContext.request.contextPath}/oframe/sysmg/sys019/sys019-msgCode.gv" layoutH="218">
            </iframe>
        </form>
    </div>
</div>
