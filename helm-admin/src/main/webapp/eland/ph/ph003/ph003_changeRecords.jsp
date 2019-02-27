<%--
    房产信息变更记录
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/2 0002 16:24
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <style type="text/css">
        div.changeRecords * {
            margin: 0;
            padding: 0;
            list-style-type: none;
        }

        div.changeRecords a, img {
            border: 0;
        }

        div.changeRecords .pageContent {
            font: 12px/180% Arial, Helvetica, sans-serif, "新宋体";
        }

        div.changeRecords .changeRecords {
            width: 1280px;
            margin: 20px auto 0 auto;
        }

        /*history*/
        div.changeRecords .history {
            background: url(${pageContext.request.contextPath}/oframe/themes/images/chrecord_line04.gif) repeat-y 197px 0;
            overflow: hidden;
            position: relative;
        }

        div.changeRecords .history-date {
            overflow: hidden;
            position: relative;
        }

        div.changeRecords .history-date h2 {
            background: #fff url(${pageContext.request.contextPath}/oframe/themes/images/chrecord_icon06.gif) no-repeat 168px 0;
            height: 59px;
            font-size: 25px;
            font-family: 微软雅黑;
            font-weight: normal;
            padding-left: 45px; /*margin-bottom:20px;*/
        }

        div.changeRecords .history-date h2.first {
            position: absolute;
            left: 0;
            top: 0;
            width: 935px;
            z-index: 99;
        }

        div.changeRecords .history-date h2 a {
            color: #00bbff;
            display: inline-block;
            *display: inline;
            zoom: 1;
            background: url(${pageContext.request.contextPath}/oframe/themes/images/chrecord_icon08.gif) no-repeat right 50%;
            font-size: 25px;
            font-family: 微软雅黑;
            font-weight: normal;
            padding-right: 10px;
            margin: 21px 100px 0 0;
            text-decoration: underline;
        }

        div.changeRecords .history-date h2 a:hover {
            text-decoration: none;
        }

        div.changeRecords .history-date h2 img {
            vertical-align: -5px;
        }

        div.changeRecords .history-date h2.date02 {
            background: none;
        }

        div.changeRecords .history-date ul li {
            background: url(${pageContext.request.contextPath}/oframe/themes/images/chrecord_icon07.gif) no-repeat 190px 0; /*padding-bottom:20px;*/
            zoom: 1;
        }

        div.changeRecords .history-date ul li.last {
            padding-bottom: 0;
        }

        div.changeRecords .history-date ul li:after {
            content: " ";
            display: block;
            height: 0;
            clear: both;
            visibility: hidden;
        }

        div.changeRecords .history-date ul li h3 {
            float: left;
            width: 168px;
            text-align: right;
            padding-right: 19px;
            color: #c3c3c3;
            font: normal 18px/16px Arial;
        }

        div.changeRecords .history-date ul li h3 span {
            display: block;
            color: #d0d0d0;
            font-size: 14px;
        }

        div.changeRecords .history-date ul li dl {
            float: left;
            width: 80%;
            padding-left: 41px;
            margin-top: 0px;
            font-family: 微软雅黑;
        }

        div.changeRecords .history-date ul li dl dt {
            font: 20px/22px 微软雅黑;
            color: #4e4e4e;
        }

        div.changeRecords .history-date ul li dl dt span {
            display: block;
            color: #5d5d5d;
            font-size: 14px;
            margin-left: 5px;
        }

        div.changeRecords .history-date ul li dl dt span span {
            display: inline-block;
            text-decoration: line-through
        }

        div.changeRecords .history-date ul li.green h3 {
            color: #1db702;
        }

        div.changeRecords .history-date ul li.green h3 span {
            color: #a8dda3;
        }

        div.changeRecords .history-date ul li.green dl {
            margin-top: -8px;
        }

        div.changeRecords .history-date ul li.green dl dt {
            font-size: 30px;
            line-height: 28px;
        }

        div.changeRecords .history-date ul li.green dl dt a {
            display: inline-block;
            *display: inline;
            zoom: 1;
            overflow: hidden;
            vertical-align: middle;
            margin-left: 12px;
        }

        div.changeRecords .history-date ul li.green dl dd {
            padding-top: 20px;
            display: none;
        }

        div.changeRecords .history-date ul li.green dl dd img {
            float: left;
        }

        div.changeRecords .history-date ul li.green dl dd p {
            overflow: hidden;
            zoom: 1;
            line-height: 21px;
            color: #5d5d5d;
        }

        div.changeRecords .history-date h2.first .more-history {
            font-size: 16px;
            background: transparent;
            margin-left: 30px;
        }

        div.changeRecords .history-date h2.first .more-history:hover {
            text-decoration: underline;
        }

        * body .history-date ul li dl dt {
            _font-size: 14px !important;
            _font-weight: bold;
        }

        * body .history-date ul li dl dt span {
            _font-weight: normal !important;
        }

        * body .history-date ul li.green dl dt a {
            _background: transparent !important;
            *background: transparent !important;
            *font-size: 14px !important;
            _font-weight: normal !important;
        }
    </style>
    <script type="text/javascript">
        var ph003_chRcd = {
            /*修改记录*/
            editRecords: function (hsId, obj) {
                var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-editRecords.gv?hsId=" + hsId
                        + "&prjCd=" + getPrjCd()
                        + "&bigYM=" + $(obj).val();
                $.pdialog.open(url, "ph00301-editRecords", "修改记录", {max: true, mask: true});
            },

            systole: function () {
                if (!$(".history").length) {
                    return;
                }
                var $warpEle = $(".history-date"),
                        $targetA = $warpEle.find("h2 a,ul li dl dt a,h2 img"),
                        parentH,
                        eleTop = [];

                parentH = $warpEle.parent().height();
                $warpEle.parent().css({"height": 59});

                setTimeout(function () {
                    $warpEle.find("ul").children(":not('h2:first')").each(function (idx) {
                        eleTop.push($(this).position().top);
                        $(this).css({"margin-top": -eleTop[idx]}).children().hide();
                    }).animate({"margin-top": 0}, 900).children().fadeIn();

                    $warpEle.parent().animate({"height": parentH}, 1900);

                    $warpEle.find("ul").children(":not('h2:first')").addClass("bounceInDown").css({
                        "-webkit-animation-duration": "2s",
                        "-webkit-animation-delay": "0",
                        "-webkit-animation-timing-function": "ease",
                        "-webkit-animation-fill-mode": "both"
                    }).end().children("h2").css({"position": "relative"});
                }, 600);

                $targetA.click(function () {
                    $(this).parent().css({"position": "relative"});
                    $(this).parent().siblings().slideToggle();
                    $warpEle.parent().removeAttr("style");
                    return false;
                });
            }
        };
        $(function () {
            ph003_chRcd.systole();
        });
    </script>

    <div class="changeRecords">
        <table class="border">
            <tr>
                <th width="150px;"><label>最近月份数：</label></th>
                <td><input type="number" value="${bigYM}"
                           onchange="ph003_chRcd.editRecords('${param.hsId}', this)"
                           class="textInput digits"></td>
            </tr>
        </table>
        <div layoutH="150">
            <div class="history mart5">
                <c:forEach items="${operationList}" var="operation" varStatus="varStatus1">
                    <c:set var="chNum" value="0"/>
                    <div class="history-date">
                        <ul>
                            <c:if test="${varStatus1.index == 0}">
                                <h2 class="first"><a href="#nogo"><oframe:date value="${operation.opEndTime}"
                                                                               format="yy/MM/dd"/></a>
                                    <img src="${pageContext.request.contextPath}/oframe/themes/images/chrecord_img05.gif"
                                         alt="房产更新记录"/>
                                </h2>
                            </c:if>
                            <c:if test="${varStatus1.index != 0}">
                                <h2 class="date02"><a href="#nogo">
                                    <oframe:date value="${operation.opEndTime}" format="yy/MM/dd"/>
                                </a>
                                </h2>
                            </c:if>
                            <c:set var="chEntity" value="${operation.chEntityList}"/>
                            <li>
                                <h3><oframe:date value="${operation.opEndTime}" format="HH:mm:ss"/>
                                    <span><oframe:staff staffId="${operation.opStaffId}"/></span>
                                </h3>
                                <dl>
                                    <c:forEach items="${chEntity}" var="changedEntity" varStatus="varStatus2">
                                        <c:set var="chAttr" value="${changedEntity.chAttrList}"/>
                                        <c:forEach items="${chAttr}" var="entityAttr" varStatus="varStatus3">
                                            <c:if test="${entityAttr.EntityAttr.isPseudoFlag != 'true'}">
                                                <c:set var="chNum" value="${chNum+1}"/>
                                                <dt>
                                                    <c:set var="attrValueBef"
                                                           value="${entityAttr.EntityAttr.attrValueBef}${''}"/>
                                                    <c:set var="attrValueAft"
                                                           value="${entityAttr.EntityAttr.attrValueAft}${''}"/>
                                                    <span>${chNum}、${entityAttr.EntityAttr.attrNameCh}：
                                            <c:choose>
                                                <c:when test="${entityAttr.EntityAttr.attrValueBef_name != null && entityAttr.EntityAttr.attrValueBef_name != ''}">
                                                    旧：<span>${entityAttr.EntityAttr.attrValueBef_name}</span>，
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${attrValueBef != '' && attrValueBef != null}">
                                                            旧：<span>${attrValueBef}</span>，
                                                        </c:when>
                                                        <c:otherwise>
                                                            旧：<span>空值</span>，
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:choose>
                                                <c:when test="${entityAttr.EntityAttr.attrValueAft_name != null && entityAttr.EntityAttr.attrValueAft_name != ''}">
                                                    新：${entityAttr.EntityAttr.attrValueAft_name}。
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${attrValueAft != '' && attrValueAft != null}">
                                                            新：${attrValueAft}。
                                                        </c:when>
                                                        <c:otherwise>
                                                            新：空值。
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                                </dt>
                                            </c:if>
                                        </c:forEach>
                                    </c:forEach>
                                </dl>
                            </li>
                        </ul>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
    <br/><br/><br/>
</div>