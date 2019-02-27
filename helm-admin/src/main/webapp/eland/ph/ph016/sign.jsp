<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/4/10
  Time: 16:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<style>
    .sign-box {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        position: relative;
    }

    .sign-box .panelBar {
        background-color: #fff;
        border: none;
    }

    .sign-selected--title {
        background-color: #F2F2F2;
        height: 28px;
        line-height: 28px;
        font-size: 12px;
        text-indent: 10px;
        color: #3e90c8;
        font-weight: bold;
        border-bottom: 1px solid #ddd;
        border-top: 1px solid #ddd;
    }

    .sign-selected--content {
        flex: 1;
        border-bottom: 1px solid #ddd;
        padding-bottom: 10px;
        padding-right: 10px;
        overflow-y: auto
    }

    .sign-selected--label {
        margin-top: 10px;
        margin-left: 10px;
        display: inline-block;
        padding: 1px 5px;
        border: 1px solid #3e90c8;
        border-radius: 3px;
        color: #333333;
        background-color: #ccffff;
    }

    .sign-selected--label span {
        font-size: 12px;
        color: #333333;

    }

    .sign-selected--label .js-selected--label-key:after {
        content: ':';
    }

    .sign-selected--label .js-selected--label-value:after {
        content: "、";
    }

    .sign-selected--label .js-selected--label-value:last-of-type:after {
        content: "";
    }

    /*------标签新增----------*/

    .sign-add {
        flex: 2;
        display: flex;
        flex-direction: column;
    }

    .sign-add--title {
        height: 28px;
        line-height: 28px;
        display: flex;
    }

    .sign-add--content {
        flex: 1;
        display: flex;
    }

    .sign-private--title, .sign-system--title {
        flex: 1;
        border-bottom: 1px solid #ddd;
        text-align: center;
    }

    .sign-private--title, .sign-private--content {
        border-right: 1px solid #ddd;
    }

    .sign-dialog--content::-webkit-scrollbar, .sign-selected--content::-webkit-scrollbar, .sign-private--content::-webkit-scrollbar, .sign-system--content::-webkit-scrollbar {
        width: 3px;
    }

    .sign-dialog--content::-webkit-scrollbar-thumb, .sign-selected--content::-webkit-scrollbar-thumb, .sign-private--content::-webkit-scrollbar-thumb, .sign-system--content::-webkit-scrollbar-thumb { /*滚动条里面小方块*/
        border-radius: 3px;
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        background: #535353;
    }

    .sign-dialog--content::-webkit-scrollbar-track, .sign-selected--content::-webkit-scrollbar-track, .sign-private--content::-webkit-scrollbar-track, .sign-system--content::-webkit-scrollbar-track { /*滚动条里面轨道*/
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        border-radius: 3px;
        background: #EDEDED;
    }

    .sign-private--content, .sign-system--content {
        flex: 1;
        padding-bottom: 10px;
        padding-right: 10px;
        overflow-y: auto;
    }

    .sign-label--private, .sign-label--system {
        margin-top: 10px;
        margin-left: 10px;
        width: 113px;
        height: auto;
        display: inline-block;
        vertical-align: top;
        text-align: center;
        border: 1px solid #ddd;
        cursor: pointer;
        position: relative;
    }

    .sign--label-key {
        width: 100%;
        height: 28px;
        line-height: 28px;
        transition: background-color 0.5s, color 0.5s;
        -moz-transition: background-color 0.5s, color 0.5s; /* Firefox 4 */
        -webkit-transition: background-color 0.5s, color 0.5s; /* Safari 和 Chrome */
        -o-transition: background-color 0.5s, color 0.5s; /* Opera */
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .sign--label-value {
        width: 100%;
        height: auto;
        background-color: #ffffff;
        border-left: 1px solid #ddd;
        border-right: 1px solid #ddd;
        position: absolute;
        z-index: 999;
        left: -1px;
    }

    .to-up {
        width: 100%;
        height: auto;
        background-color: #ffffff;
        border-left: 1px solid #ddd;
        border-right: 1px solid #ddd;
        position: absolute;
        z-index: 999;
        bottom: 28px;
        left: -1px;
    }

    .sign--label-value li {
        width: 100%;
        height: 0px;
        line-height: 28px;
        cursor: pointer;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        transition: height 0.5s, background-color 0.5s, color 0.5s;
        -moz-transition: height 0.5s, background-color 0.5s, color 0.5s; /* Firefox 4 */
        -webkit-transition: height 0.5s, background-color 0.5s, color 0.5s; /* Safari 和 Chrome */
        -o-transition: height 0.5s, background-color 0.5s, color 0.5s; /* Opera */
    }

    .sign-label--private:hover .sign--label-key, .sign-label--system:hover .sign--label-key {
        background-color: #3e90c8;
        color: #fff;
    }

    .sign--label-value li:hover {
        color: #3e90c8;
    }

    .sign-label--private:hover .sign--label-value, .sign-label--system:hover .sign--label-value {
        border-bott: 1px solid #ddd;
    }

    .sign-label--private:hover .sign--label-value li, .sign-label--system:hover .sign--label-value li {
        height: 28px;
        border-top: 1px solid #ddd;
    }

    .sign-label--private:hover .sign--label-value li:last-of-type, .sign-label--system:hover .sign--label-value li:last-of-type {
        border-bottom: 1px solid #ddd;
    }

    .sign--label-edit {
        background-color: #fff !important;
        color: #3e90c8 !important;
        text-decoration: underline !important;
    }

    .sign--label-value li.active {
        background-color: #ccffff;
    }

    .sign-add-btn {
        color: #3e90c8;
        cursor: pointer;
    }

    .icon {
        width: 1.5em;
        height: 1em;
        vertical-align: -0.15em;
        fill: currentColor;
        overflow: hidden;
    }

    /*----------单户标签被安置人等信息--------------*/
    .sign-single--box {
        display: flex;
        width: 100%;
        height: 28px;
        line-height: 28px;
        border-top: 1px solid #ddd;
    }

    .sign-single--box-title {
        flex: 1;
        border-right: 1px solid #ddd;
        text-align: right;
        padding-right: 5px;
    }

    .border-left-ddd {
        border-left: 1px solid #ddd;
    }

    .sign-single--box-show {
        padding-left: 5px;
        flex: 3;
    }

    /*--------弹框-------------*/
    .sign-dialog-box {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        text-align: center;
        display: none;
    }

    .sign-dialog {
        background-color: #ffffff;
        box-shadow: 3px 3px 5px #dddddd;
        position: absolute;
        left: 50%;
        top: 47%;
        transform: translate(-50%, -50%);
        width: 480px;
        height: 300px;
        z-index: 999;
        border: 1px solid #ccc;
    }

    .sign-dialog--content {
        width: 100%;
        height: 232px;
        overflow-y: auto;
        padding-bottom: 10px;
        padding-top: 10px;
    }

    .sign-dialog--content-row {
        margin-top: 10px;
        padding-left: 155px;
        position: relative;
        text-align: left;
    }

    .sign-dialog--content-row .sign-dialog-label {
        position: absolute;
        left: 90px;
        top: 5px;
    }

    .sign-dialog--content-row .input {
        width: 170px;
    }

    .sign-dialog--content-row .icon {
        color: #3e90c8;
    }

    .mar-t10 {
        margin-top: 10px !important;
    }

    .sign-dialog--btns {
        border-top: 1px solid #ddd;
    }

    .sign-dialog--btn {
        display: inline-block;
        margin: 10px 5px;
        border: 1px solid #666;
        padding: 2px 15px;
        cursor: pointer;
        border-radius: 5px;
    }

    .sign-dialog--btn:hover {
        background-color: #3e90c8;
        color: #ffffff;
    }
</style>
<!--signType 1-初始化单户 2-初始化批量-->
<div class="sign-box">
    <input name="tagType" type="hidden" value="${tagType}">
    <input name="signType" type="hidden" value="${signType}">

    <div class="panelBar ">
        <ul class="toolBar">
            <c:if test="${signType == '2'}">
                <li onclick="ph016.relSignManager('0')"><a class="add"><span>绑定</span></a></li>
                <li onclick="ph016.relSignManager('1')"><a class="delete"><span>移除</span></a></li>
            </c:if>
            <c:if test="${signType == '1'}">
                <li onclick="ph016.relSignManager('2')"><a class="save"><span>保存</span></a></li>
            </c:if>
            <li onclick="$.pdialog.closeCurrent();"><a class="close"><span>关闭</span></a></li>
        </ul>
    </div>
    <c:if test="${signType == '1'}">
        <c:set var="HouseInfo" value="${houseInfo.HouseInfo}"/>
        <div class="sign-single--box">
            <div class="sign-single--box-title">档案编号:</div>
            <div class="sign-single--box-show">${HouseInfo.hsCd}</div>
            <div class="sign-single--box-title border-left-ddd">被安置人:</div>
            <div class="sign-single--box-show">${HouseInfo.hsOwnerPersons}</div>
        </div>
    </c:if>
    <div class="sign-selected--title"><span>已选标签</span></div>
    <div class="sign-selected--content">
        <!--单户操作时循环生成已选标签-->
        <c:set var="SignRelInfo" value="${signRelInfos}"/>
        <c:forEach items="${SignRelInfo}" var="tagMap">
            <div class="js-selected--label sign-selected--label">
                <span class="js-selected--label-key" tagid="${tagMap.tags.Tag.tagId}">${tagMap.tags.Tag.tagName}</span>
                <c:forEach items="${tagMap.subTags}" var="subTag">
                    <span class="js-selected--label-value" tagid="${subTag.Tag.tagId}">${subTag.Tag.tagName}</span>
                </c:forEach>
                <svg class="js-selected--label-delete icon" aria-hidden="true">
                    <use xlink:href="#icon-dustbin_icon"></use>
                </svg>
            </div>
        </c:forEach>
        <!--单户操作时循环生成已选标签-->
    </div>
    <div class="sign-add">
        <div class="sign-add--title">
            <div class="sign-private--title">个人标签<span class="sign-add-btn js-editor-label" tagType="1">【新建】</span>
            </div>
            <div class="sign-system--title">系统标签
                <%--<oframe:op prjCd="${param.prjCd}" template="span" cssClass="export sign-add-btn js-editor-label"--%>
                <%--name="新建" rhtCd="add_sys_tag_rht"/>--%>
                <oframe:power rhtCd="add_sys_tag_rht" prjCd="${param.prjCd}">
                    <span class="sign-add-btn js-editor-label" tagType="0">【新建】</span>
                </oframe:power>
            </div>
        </div>
        <div class="sign-add--content">
            <c:set var="SignInfo" value="${signInfos}"/>
            <div class="sign-private--content js-sign-content">
                <!--单户操作时循环生成个人标签-->
                <c:forEach items="${SignInfo}" var="tagMap">
                    <c:set var="tagType" value="${tagMap.tags.Tag.tagType}"/>
                    <%--tagType:0-系统级 1-个人级--%>
                    <c:if test="${tagType!=null && tagType =='1'}">
                        <div class="sign-label--private">
                            <div class="sign--label-key" tagid="${tagMap.tags.Tag.tagId}"
                                 tagtype="${tagMap.tags.Tag.tagType}"
                                 ischeckbox="${tagMap.tags.Tag.isCheckbox}">${tagMap.tags.Tag.tagName}</div>
                            <ul class="sign--label-value js-sign--label-value">
                                <c:forEach items="${tagMap.subTags}" var="subTag">
                                    <li class="js-key-value" title="${subTag.Tag.tagName}" tagid="${subTag.Tag.tagId}">${subTag.Tag.tagName}</li>
                                </c:forEach>
                                <li class="sign--label-edit js-editor-label">编辑标签</li>
                            </ul>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
            <div class="sign-system--content js-sign-content">
                <!--单户操作时循环生成系统标签-->
                <c:forEach items="${SignInfo}" var="tagMap">
                    <c:set var="tagType" value="${tagMap.tags.Tag.tagType}"/>
                    <%--tagType:0-系统级 1-个人级--%>
                    <c:if test="${tagType!=null && tagType =='0'}">
                        <div class="sign-label--system">
                            <div class="sign--label-key" tagid="${tagMap.tags.Tag.tagId}"
                                 tagtype="${tagMap.tags.Tag.tagType}"
                                 ischeckbox="${tagMap.tags.Tag.isCheckbox}">${tagMap.tags.Tag.tagName}</div>
                            <ul class="sign--label-value js-sign--label-value">
                                <c:forEach items="${tagMap.subTags}" var="subTag">
                                    <li class="js-key-value" title="${subTag.Tag.tagName}" tagid="${subTag.Tag.tagId}">${subTag.Tag.tagName}</li>
                                </c:forEach>
                                <oframe:power rhtCd="edit_sys_tag_rht" prjCd="${param.prjCd}">
                                    <li class="js-editor-label sign--label-edit js-editor-label">编辑标签</li>
                                </oframe:power>
                            </ul>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </div>
    <div class="sign-dialog-box">
        <div class="sign-dialog">
            <div class="sign-dialog--content">
                <div class="sign-dialog--content-row">
                    <label class="sign-dialog--content-name sign-dialog-label">标签名称:</label>
                    <input class="input parent-tagname" tagid="" type="text">
                    <label><input type="checkbox" name="isCheckbox">多选</label>
                </div>
                <div class="sign-dialog--content-row dialog-value">
                    <label class="sign-dialog--content-name sign-dialog-label">标签内容:</label>
                </div>
            </div>
            <div class="sign-dialog--btns">
                <span class="sign-dialog--btn js-sign-dalog--apply">提交</span>
                <span class="sign-dialog--btn js-sign-dalog--delete">删除</span>
                <span class="sign-dialog--btn js-sign-dalog--cancel" onclick="ph016.destroyDialog()">取消</span>
            </div>
        </div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph016/js/batch_mark.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph016/iconfont/iconfont.js"/>
<script>
    $(document).ready(function () {
        // 单户时选中样式
        ph016.init();
    });
</script>
