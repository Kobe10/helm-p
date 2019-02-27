<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--户籍信息--%>
<c:set var="psType1" value="0"/>
<c:set var="psType2" value="0"/>
<c:set var="psType3" value="0"/>
<c:forEach items="${hsPersonBean}" var="hsPerson">
    <c:if test="${hsPerson.Person.personType == '1'}">
        <c:set var="psType1" value="${psType1+1}"/>
    </c:if>
    <c:if test="${hsPerson.Person.personType == '2'}">
        <c:set var="psType2" value="${psType2+1}"/>
    </c:if>
    <c:if test="${hsPerson.Person.personType == '3' || hsPerson.person.isLiveHere == '1'}">
        <c:set var="psType3" value="${psType3+1}"/>
    </c:if>
</c:forEach>
<div class="tabs">
    <div class="tabsHeader">
        <div class="tabsHeaderContent">
            <ul>
                <li class="js_load_tab selected"
                    onclick="ph00303.loadTabContext(this);">
                    <a href="javascript:void(0);"><span>在册户籍（${psType1}人）</span></a>
                </li>
                <li class="js_load_tab"
                    onclick="ph00303.loadTabContext(this);">
                    <a href="javascript:void(0);"><span>非本址产承人户籍（${psType2}人）</span></a>
                </li>
                <li class="js_load_tab"
                    onclick="ph00303.loadTabContext(this);">
                    <a href="javascript:void(0);"><span>其他现场居住人（${psType3}人）</span></a>
                </li>
            </ul>
        </div>
    </div>
    <div class="tabsContent">
        <%--在册人员信息--%>
        <div style="min-height: 200px">
            <div style="width: 99%;height: 100%;">
                <table class="list" id="persons" width="100%">
                    <thead>
                    <tr>
                        <th>成员姓名</th>
                        <th>户主关系</th>
                        <th>证件号码</th>
                        <th>年龄</th>
                        <th>联系电话</th>
                        <th>婚姻状况</th>
                        <th>工作单位</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${hsPersonBean}" var="hsPerson">
                        <c:if test="${hsPerson.Person.personType == '1'}">
                            <tr>
                                <td>${hsPerson.Person.personName}</td>
                                <td>${hsPerson.Person.familyPersonRel}</td>
                                <td>${hsPerson.Person.personCertyNum}
                                <span class="js_doc_info link marr10" docTypeName="身份证" editable="false"
                                      onclick="ph00303.showAndSyncDoc(this)" style="float: right">
                                    <input type="hidden" name="personCertyDocIds"
                                           value="${hsPerson.Person.personCertyDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                                </td>
                                <td>${hsPerson.Person.personAge}</td>
                                <td>${hsPerson.Person.personTelphone}</td>
                                <td><oframe:name prjCd="${param.prjCd}" itemCd="MARRY_STATUS" value="${hsPerson.Person.marryStatus}"/></td>
                                <td>${hsPerson.Person.personJobAddr}</td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <%--非本址人员--%>
        <div style="min-height: 200px">
            <div style="width: 99%;height: 100%;">
                <table class="list" id="notFamAddr" width="100%">
                    <thead>
                    <tr>
                        <th>成员姓名</th>
                        <th>户主关系</th>
                        <th>证件号码</th>
                        <th>年龄</th>
                        <th>户籍所在地</th>
                        <th>联系电话</th>
                        <th>婚姻状况</th>
                        <th>工作单位</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${hsPersonBean}" var="hsPerson">
                        <c:if test="${hsPerson.Person.personType == '2'}">
                            <tr>
                                <td>${hsPerson.Person.personName}</td>
                                <td>${hsPerson.Person.familyPersonRel}</td>
                                <td>${hsPerson.Person.personCertyNum}
                                        <span class="js_doc_info link marr10" docTypeName="身份证" editable="false"
                                              onclick="ph00303.showAndSyncDoc(this)" style="float: right">
                                            <input type="hidden" name="personCertyDocIds"
                                                   value="${hsPerson.Person.personCertyDocIds}">
                                            <label style="cursor:pointer;">查看</label>
                                        </span>
                                </td>
                                <td>${hsPerson.Person.personAge}</td>
                                <td>${hsPerson.Person.familyAddr}</td>
                                <td>${hsPerson.Person.personTelphone}</td>
                                <td><oframe:name prjCd="${param.prjCd}" itemCd="MARRY_STATUS" value="${hsPerson.Person.marryStatus}"/></td>
                                <td>${hsPerson.Person.personJobAddr}</td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <%--现场居住人员--%>
        <div style="min-height: 200px">
            <div style="width: 99%;height: 100%;">
                <table class="list" id="livePerson" width="100%">
                    <thead>
                    <tr>
                        <th>成员姓名</th>
                        <th>户主关系</th>
                        <th>证件号码</th>
                        <th>年龄</th>
                        <th>联系电话</th>
                        <th>婚姻状况</th>
                        <th>工作单位</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${hsPersonBean}" var="hsPerson">
                        <c:if test="${hsPerson.Person.personType == '3'}">
                            <tr>
                                <td>${hsPerson.Person.personName}</td>
                                <td>${hsPerson.Person.familyPersonRel}</td>
                                <td>${hsPerson.Person.personCertyNum}
                                        <span class="js_doc_info link marr10" docTypeName="身份证" editable="false"
                                              onclick="ph00303.showAndSyncDoc(this)" style="float: right">
                                            <input type="hidden" name="personCertyDocIds"
                                                   value="${hsPerson.Person.personCertyDocIds}">
                                            <label style="cursor:pointer;">查看</label>
                                        </span>
                                </td>
                                <td>${hsPerson.Person.personAge}</td>
                                <td>${hsPerson.Person.personTelphone}</td>
                                <td><oframe:name prjCd="${param.prjCd}" itemCd="MARRY_STATUS" value="${hsPerson.Person.marryStatus}"/></td>
                                <td>${hsPerson.Person.personJobAddr}</td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ph00303 = {
        showAndSyncDoc: function (clickObj) {
            var $span = $(clickObj);
            var docIds = $span.find("input[type=hidden]").val();
            var docTypeName = $span.attr("docTypeName");
            if (!docTypeName) {
                docTypeName = "";
            }
            docTypeName = encodeURI(encodeURI(docTypeName));
            var editAble = $span.attr("editAble");
            if (!editAble) {
                editAble = true;
            }
            var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName +
                    "&docIds=" + docIds + "&editAble=" + editAble;
            $.pdialog.open(url, "file", "查看附件", {
                height: 600, width: 800, mask: true
            });
        },

        /**
         * 加载tab页的内容
         * @param obj
         */
        loadTabContext: function (obj) {
            var _this = $(obj);
            var url = _this.attr("url");
            var urlData = getGlobalPathRoot() + encodeURI(encodeURI(url));
            var contextDiv = _this.closest("div.tabs").find("div.tabsContent").find(">div:eq(" + _this.index() + ")");
            if (contextDiv.html() == "") {
//                var packet = new AJAXPacket(urlData);
//                packet.noCover = true;
//                packet.data.add("prjCd", getPrjCd());
//                core.ajax.sendPacketHtml(packet, function (response) {
//                    contextDiv.html(response);
                initUI(contextDiv);
//                })
            }
        }
    };
    $(document).ready(function () {
        var $span = $("span.js_doc_info", navTab.getCurrentPanel());
        $span.find("input[type=hidden]").each(function () {
            if ($(this).val() && $(this).val()=="") {
                $(this).parent("span.js_doc_info").show();
            } else {
                $(this).parent("span.js_doc_info").hide();
            }
        });
    });
</script>
