<%--项目资料--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav js_folder_path">
    <a class="current">项目资料</a>---><a class="link">项 目 目 录</a>
</div>
<input type="hidden" name="moveAble" value="1"/>

<div class="mar5" style="vertical-align: top;">
    <%--左侧区域树--%>
    <div style="width: 240px;float:left;position: relative;" id="pj015DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span>项 目 资 料</span>
            <span onclick="pj015.extendOrClose(this);" class="panel_menu js_reload">展开</span>
        </h1>
        <ul id="pj015Tree" class="ztree" layoutH="65"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="pj015ContextDiv" class="js_query_condition"
         style="margin-left: 250px;margin-right: 5px; position: relative;">
        <div class="panelBar ">
            <ul class="toolBar">
                <li id="upRegionLink">
                    <a class="hight-level"
                       onclick="pj015.showUpFolder()"<%--onclick="pj015.queryCondition(this);"--%>><span>上级目录</span></a>
                </li>
                <li class="" onclick="pj015.openQSch(this)">
                    <a title="点击进入检索" class="find" href="javascript:void(0)">
                        <span>检索资料</span></a>
                </li>
                <li>
                    <a class="reflesh" onclick="pj015.refleshFolder()"><span>刷新资料</span></a>
                </li>
                <li>
                    <a class="add" href="javascript:pj015.addFolder();">
                        <span>&nbsp;新建文件夹</span>
                    </a>
                </li>
                <li style="position: relative;" onclick="pj015.uploadDoc(this)">
                    <a class="import" title="选择文件上传">
                        <span>上传文件</span>
                    </a>
                </li>
                <%--<li style="float: right;" onclick="pj015.changeShowModel(this,'2');">--%>
                <%--<a class="pic" href="javascript:void(0)"><span class="active">图形</span></a>--%>
                <%--</li>--%>
                <%--<li style="float: right;" onclick="pj015.changeShowModel(this, '1');">--%>
                <%--<a class="list" href="javascript:void(0)"><span>列表</span></a>--%>
                <%--</li>--%>
            </ul>
        </div>
        <div id="pj015QSchDialog" class="hidden"
             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
            <div id="triangle" class="triangle triangle-up"
                 style="border-bottom-color: #2c7bae;top: -10px; left: 85px;"></div>
            <table class="border">
                <tr>
                    <th><label>资料名称：</label></th>
                    <td><input name="docName" condition="like" class="textInput"></td>
                    <th><label>资料关键字：</label></th>
                    <td><input name="docKey" condition="like" class="textInput"></td>
                    <th><label>含子文件文件夹：</label></th>
                    <td>
                        <input type="radio" name="queryType" condition="=" value="2" checked/>是
                        <input type="radio" name="queryType" condition="=" value="1"/>否
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align="center">
                        <button onclick="pj015.qSchData();" class="btn btn-primary js_query marr20">查询</button>
                        <button onclick="pj015.closeQSch(true);" class="btn btn-info">关闭</button>
                    </td>
                </tr>
            </table>
        </div>
        <div id="pj015Context" layoutH="57"
             class="js_page"
             ondrop="pj015.uploadFile()"
             ondragenter="return false"
             ondragover="return false"
             style="border: 1px solid #e9e9e9;">
        </div>
    </div>
    <form id="pj015QryFrm">
        <input type="hidden" name="entityName" value="DocumentInfo"/>
        <input type="hidden" name="conditionName" value="docName">
        <input type="hidden" name="condition" value="like">
        <input type="hidden" name="conditionValue" value="">
        <input type="hidden" name="sortColumn" value="docName">
        <input type="hidden" name="sortOrder" value="asc">
        <input type="hidden" name="upDocId" value=""/>
        <input type="hidden" name="divId" value="pj015Context">
        <input type="hidden" name="resultField" value="docName,docCd,docFlag,pubDate">
        <input type="hidden" name="forceResultField" value="docFlag">
        <input type="hidden" name="forward" id="forward" value="/eland/pj/pj015/pj01503"/>
        <input type="hidden" class="js_can_def_result" value="true"/>
        <input type="hidden" class="js_query_model" value="1"/>
        <input type="hidden" class="js_conditionValue" value="">
        <input type="hidden" name="cmptName" value="QUERY_DOCUMENTINFO_PAGE">
    </form>
</div>
<ul class="menu hidden js_file_template">
    <li onclick="pj015.editFile(this, 0);">文件详情</li>
    <oframe:power prjCd="${param.prjCd}" rhtCd="edit_prj_doc_rht">
        <li style="position: relative;" onclick="pj015.changeFile(this);" class="js_edit">
            <a>重新上传</a>
            <input style="width:110px; height:37px; position:absolute; right:0; top:0;
                 opacity:0;filter:alpha(opacity=0); z-index:11; cursor:pointer;" name="importPic" type="file">
        </li>
    </oframe:power>
    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="js_doc_edit" name="修改文件"
               rhtCd="edit_prj_doc_rht" onClick="pj015.editFileDoc(this, 0);"/>
    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="js_edit" name="移动文件"
               rhtCd="edit_prj_doc_rht" onClick="pj015.moveDoc(this, 0);"/>
    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="js_edit" name="下载文件"
               rhtCd="edit_prj_doc_rht" onClick="pj015.downFile(this, 0);"/>
    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="js_edit" name="删除文件"
               rhtCd="edit_prj_doc_rht" onClick="pj015.deleteFile(this, 0);"/>
</ul>
<ul class="menu hidden js_folder_template">
    <li onclick="pj015.editFile(this , 1);">文件夹详情</li>
    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="js_edit" name="删除文件夹"
               rhtCd="edit_prj_doc_rht" onClick="pj015.deleteFile(this, 1);"/>
</ul>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj015/js/pj015.js" type="text/javascript"/>