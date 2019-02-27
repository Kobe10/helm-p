<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>选房自助查询系统</title>

    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>

    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/colorPicker.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/jquery.autocomplete.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/eland/ch/ch003/css/style.css" rel="stylesheet" type="text/css"
          media="screen"/>

    <!-- 可以用dwz.min.js替换前面全部dwz.*.js (注意：替换是下面dwz.regional.zh.js还需要引入)-->
    <script src="${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/ueditor/lang/zh-cn/zh-cn.js" type="text/javascript"
            charset="utf-8"></script>
    <%--富文本编辑器--%>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.all.min.js"></script>

    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.autocomplete.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.colorPicker.js"
            type="text/javascript"></script>

    <!--自定义引入js-->
    <oframe:script src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js" type="text/javascript"/>
    <oframe:script src="${pageContext.request.contextPath}/eland/ch/ch003/js/ch003.js" type="text/javascript"/>
    <style type="text/css">
        .jFang {
            cursor: pointer;;
        }

        .selected {
            background-color: #00b7ee;
        }
    </style>
</head>
<body>
<div class="messageLogging">
    <input type="hidden" id="sysPrjCd" name="prjCd" value="${prjCd}"/>
    <input type="hidden" name="spaceTime" value="${spaceTime}">

    <div class="return">
        <a id="return" href="${pageContext.request.contextPath}/eland/ch/ch003/ch003-init.gv?prjCd=${prjCd}">返回重新查询</a>
    </div>
    <c:if test="${errorMsg != ''}">
        <div style="text-align: center;color: red; font-size: 30px;margin: 0 5px;">${errorMsg}
            <input type="hidden" name="errorMsg" value="${errorMsg}"/></div>
    </c:if>
    <table>
        <tbody>
        <tr>
            <td width="7%" class="tdTitle">被安置人：</td>
            <td width="20%" class="tdResult"> ${hsInfo.HouseInfo.hsOwnerPersons}(${hsInfo.HouseInfo.hsFullAddr})</td>
            <td width="7%" class="tdTitle">档案编号：</td>
            <td width="15%" class="tdResult">${hsInfo.HouseInfo.hsCd}</td>
            <td width="7%" class="tdTitle">安置方式：</td>
            <td width="20%" class="tdResult">
                <oframe:name prjCd="${param.prjCd}"
                             value="${hsInfo.HouseInfo.HsCtInfo.ctType}"
                             itemCd="10001"/>
            </td>
        </tr>
        <tr>
            <td width="7%" class="tdTitle">签约时间：</td>
            <td width="20%" class="tdResult"> ${ctDate}</td>
            <td class="tdTitle">签约序号：</td>
            <td class="tdResult"> ${hsInfo.HouseInfo.HsCtInfo.chooseHsSid}</td>
            <td class="tdTitle">配比信息：</td>
            <td class="tdResult">
                <c:forEach items="${hsTps}" var="item">
                    <div style="float: left;margin-left: 10px">
                        <c:set var="key" value="${item.key}${''}"/>
                        <c:if test="${key != '4'}">
                            ${item.value}：${tempArr[item.key-1]}
                        </c:if>
                    </div>
                </c:forEach>
            </td>
        </tr>
        </tbody>
    </table>
    <table id="buyTableList" class="list mart5" id="yaoXFang" width="100%" style="border-collapse: collapse;">
        <thead>
        <tr>
            <th width="8%" align="center">购房人</th>
            <th width="10%" align="center">购房人证件号</th>
            <th width="10%" align="center">通讯地址</th>
            <th width="10%" align="center">购房人电话</th>
            <th width="10%" align="center">购房居室</th>
            <th width="10%" align="center">购房状态</th>
            <th width="10%" align="center">操作</th>
        </tr>
        </thead>
        <c:forEach var="item" items="${chRegList}">
            <c:set var="chooseStatus" value="${item.chooseHs.chooseStatus}${''}"/>
            <tr class="jFang js_xFang" onclick="ch003cx.choosePerson('${hsId}',this)">
                <td>${item.chooseHs.buyPersonName}</td>
                <td>${item.chooseHs.buyPersonCerty}</td>
                <td>${item.chooseHs.personNoticeAddr}</td>
                <td>${item.chooseHs.buyPersonTel}</td>
                <td>${item.chooseHs.huJush_Name}</td>
                <td>
                    <input type="hidden" name="chooseStatus" value="${chooseStatus}"/>
                    <oframe:name prjCd="${param.prjCd}" itemCd="CHOOSE_STATUS" value="${chooseStatus}"/>
                </td>
                <td name="tempTd">
                    <input type="hidden" name="chooseHsRegId" value="${item.chooseHs.chooseHsRegId}"/>
                    <input type="hidden" name="hsJush" value="${item.chooseHs.huJush}"/>
                    <span class="link marl5 js_delectBtn">[房源查看]</span>
                </td>
            </tr>
        </c:forEach>
    </table>
    <div class="bottomList">
        <span class="schTitle">居室 ：</span>
        <span class="schValue">
            <input type="hidden" name="hsTp"/>
            <input type="text" itemCd="HS_ROOM_TYPE" atOption="ch003cx.getCfgDataOpt"
                   class="autocomplete textInput acInput" autocomplete="off">
        </span>
        <span class="schTitle">户型 ：</span>
        <span class="schValue">
            <input type="text" name="hsHxName"/>
        </span>
        <span class="schTitle">面积 ：</span>
        <span class="schValue">
             <input type="text" name="minSize"/>&nbsp;-&nbsp;<input type="text" name="maxSize"/>
        </span>
        <span class="schTitle">地址 ：</span>
        <span class="schValue">
             <input type="text" name="chooseHsAddr"/>
        </span>
        <input type="hidden" value="" name="ctrlHsJush"/>
        <input type="hidden" value="false" name="ctrlFlag"/>
        <input name="inquiry" value="查询" type="submit" onclick="ch003cx.query('${hsId}');">
        <a class="rightList">
            <span class="control active" onclick="ch003cx.changeShowModel(this,'2','${hsId}');">销控</span>
            <span class="list" onclick="ch003cx.changeShowModel(this, '1','${hsId}');">列表</span>
        </a>
    </div>
</div>
<div style="position: absolute;bottom: 30px; right:15px;width: 400px;background-color: white;border: 1px solid #7EC4CC;display: none;">
    <table class="border" width="100%">
        <tr>
            <td style="text-align: center;background-color: #3d91c8;font-weight: bolder;color: white;font-size: 20px">
                本次查询倒计时
            </td>
        </tr>
        <tr>
            <td style="text-align: center;min-height: 80px;">
                <span class="countTime" style="color: #ff0000;line-height: 80px;">${spaceTime}分0秒</span>
            </td>
        </tr>
    </table>
</div>
<div id="ch001001div" style="position: relative;">
    <form id="ch001001frm" method="post" class="entermode" allowTransparency="false">
        <input type="hidden" class="js_query_model" value="2"/>
        <input type="hidden" name="regUseType" value="2">
        <input type="hidden" name="rhtType" value="2">
        <input type="hidden" name="entityName" value="NewHsInfo"/>
        <input type="hidden" name="conditionName" value="">
        <input type="hidden" name="condition" value="">
        <input type="hidden" name="conditionValue" value="">
        <input type="hidden" name="sortColumn" value="NewHsInfo.hsAddr">
        <input type="hidden" name="sortOrder" value="asc">
        <input type="hidden" class="js_conditionValue" value="">
        <input type="hidden" name="forceResultField" value="NewHsInfo.newHsId">
        <input type="hidden" name="divId" value="result">
        <input type="hidden" name="resultField"
               value="NewHsInfo.hsAddr,NewHsInfo.hsHxName,NewHsInfo.hsTp,NewHsInfo.hsDt,NewHsInfo.preBldSize,NewHsInfo.preBldSize,NewHsInfo.statusCd">
        <input type="hidden" name="forward" id="forward" value="/eland/ch/ch003/ch003001_list"/>
        <input type="hidden" name="ctType" id="ctType" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/>
        <input type="hidden" name="cmptName" value="QUERY_NEW_HOUSE_REGION"/>
        <input type="hidden" name="selectedHs"/>
        <%--图形建筑展示页面--%>
        <input type="hidden" name="displayBuildPage" value="/eland/ch/ch003/ch00301_build"/>
    </form>
    <div class="js_page" id="result" style="overflow: auto"></div>
</div>

</body>
<script>
    $(document).ready(function () {
        $("input.autocomplete").each(function () {
                    var $this = $(this);
                    var atArray = $(this).attr("atArray");
                    var atOption = $(this).attr("atOption");
                    var atUrl = $(this).attr("atUrl");
                    if (atOption && !atOption == "") {
                        atOption = eval(atOption)($this);
                    } else {
                        atOption = {};
                    }
                    if (atArray && !atArray == "") {
                        atArray = eval(atArray)($this);
                        $(this).autocomplete(atArray, atOption);
                    } else if (atUrl && !atUrl == "") {
                        atUrl = eval(atUrl)($this);
                        $(this).autocomplete(atUrl, atOption);
                    } else {
                        $(this).autocomplete(atOption);
                    }
                }
        );
        $("#buyTableList").find("tr:eq(1)").trigger("click");
    });
</script>
