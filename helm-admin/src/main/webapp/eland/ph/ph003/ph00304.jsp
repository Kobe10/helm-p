<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>产权信息
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 300px;">
        <div style="height: 100%; width: 100%; display: inline-block; float: right;">
            <table class="border" width="100%">
                <tr>
                    <th width="18%"><label>房屋产别:</label></th>
                    <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${hsOwnerBean.HouseInfo.hsOwnerType}"/></td>
                    <th width="18%"><label>分配方式:</label></th>
                    <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWN_TYPE" value="${hsOwnerBean.HouseInfo.hsOwnerModel}"/></td>
                </tr>
                <tr>
                    <th><label>建筑面积:</label></th>
                    <td>${hsOwnerBean.HouseInfo.hsBuildSize}</td>
                    <th><label>使用面积:</label></th>
                    <td>${hsOwnerBean.HouseInfo.hsUseSize}</td>
                </tr>
                <tr>
                    <th>
                        <label>产权人:</label>
                    </th>
                    <td>
                        <c:choose>
                            <c:when test="${hsOwnerBean.HouseInfo.hsOwnerType == '0'}">
                                ${hsOwnerBean.HouseInfo.hsOwnerPersons}
                            </c:when>
                            <c:otherwise>
                                ${hsOwnerBean.HouseInfo.hsPubOwnerName}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <th><label>产权证号:</label></th>
                    <td>
                        <c:choose>
                            <c:when test="${hsOwnerBean.HouseInfo.hsOwnerType == '0'}">
                                <%--${hsOwnerBean.HouseInfo.HsOwners.HsOwner[0].ownerCerty}--%>
                                ${ownerCerty}
                                <span class="js_doc_info link marr5" docTypeName="产权证号" editable="false"
                                      onclick="ph00304.showDoc(this)" style="float:right;">
                                    <input type="hidden" name="ownerCertyDocIds"
                                           value="${ownerCertyDocs}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </c:when>
                            <c:otherwise>
                                ${hsOwnerBean.HouseInfo.hsPubOwnerCerty}
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th><label>承租人:</label></th>
                    <td>
                        <c:choose>
                            <c:when test="${hsOwnerBean.HouseInfo.hsOwnerType == '0'}">
                            </c:when>
                            <c:otherwise>
                                ${hsOwnerBean.HouseInfo.hsOwnerPersons}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <th><label>租赁合同号:</label></th>
                    <td>
                        <c:choose>
                            <c:when test="${hsOwnerBean.HouseInfo.hsOwnerType == '0'}">
                            </c:when>
                            <c:otherwise>
                                ${hsOwnerBean.HouseInfo.HsOwners.HsOwner[0].ownerCerty}
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr style="height: 155px;">
                    <th><label>产权备注:</label></th>
                    <td colspan="3">${hsOwnerBean.HouseInfo.hsOwnerNote}</td>
                </tr>
                <tr class="hidden">
                    <th class="subTitle" style="text-align: left;" colspan="4"><label>产权人信息:</label></th>
                </tr>
                <tr class="hidden">
                    <td colspan="4" valign="top">
                        <div style="display: inline-block; width: 48%; margin: 5px; position: relative;">
                            <table class="border">
                                <tr>
                                    <th width="30%"><label>产权人：</label></th>
                                    <td></td>
                                </tr>
                                <tr>
                                    <th><label>产权证号：</label></th>
                                    <td></td>
                                </tr>
                                <tr>
                                    <th><label>产权占比：</label></th>
                                    <td></td>
                                </tr>
                                <tr>
                                    <th><label>产权面积：</label></th>
                                    <td></td>
                                </tr>
                                <tr style="height: 80px;">
                                    <th><label>信息备注：</label></th>
                                    <td></td>
                                </tr>
                            </table>
                        </div>
                        <div style="display: inline-block; width: 48%; margin: 5px; position: relative;">
                            <table class="border">
                                <tr>
                                    <th width="30%"><label>产权人：</label></th>
                                    <td></td>
                                </tr>
                                <tr>
                                    <th><label>产权证号：</label></th>
                                    <td></td>
                                </tr>
                                <tr>
                                    <th><label>产权占比：</label></th>
                                    <td></td>
                                </tr>
                                <tr>
                                    <th><label>产权面积：</label></th>
                                    <td></td>
                                </tr>
                                <tr style="height: 80px;">
                                    <th><label>信息备注：</label></th>
                                    <td></td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ph00304 = {
        /**
         * 显示关联文档信息
         * @param clickObj 点击对象
         */
        showDoc: function (clickObj) {
            var $span = $(clickObj);
            var docIds = $span.find("input[type=hidden]").val();
            var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
            var docTypeName = $span.attr("docTypeName");
            if (!docTypeName) {
                docTypeName = "";
            }
            docTypeName = encodeURI(encodeURI(docTypeName));

            var editAble = $span.attr("editAble");
            if (!editAble) {
                editAble = true;
            }
            var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
                    + "&docIds=" + docIds + "&editAble=" + editAble;
            $.pdialog.open(url, "file", "查看附件", {
                height: 600, width: 800, mask: true
            });
        }
    };
    $(document).ready(function () {
        var $span = $("span.js_doc_info", navTab.getCurrentPanel());
        $span.find("input[type=hidden]").each(function () {
            if ($(this).val() != '' && $(this).val()) {
                $(this).parent("span.js_doc_info").show();
            } else {
                $(this).parent("span.js_doc_info").hide();
            }
        });
    });
</script>
