<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="tabs">
    <div class="tabsHeader">
        <div class="tabsHeaderContent">
            <ul>
                <c:if test="${isPhHs=='1'}">
                    <li class="js_load_tab selected"
                        onclick="ph00301_showHsOtherInfo.loadTabContext(this);">
                        <a href="javascript:void(0);"><span>问题信息</span></a>
                    </li>
                    <li class="js_load_tab"
                        onclick="ph00301_showHsOtherInfo.loadTabContext(this);">
                        <a href="javascript:void(0);"><span>人员状况</span></a>
                    </li>
                </c:if>
                <c:if test="${isPhHs!='1'}">
                    <li class="js_load_tab selected"
                        onclick="ph00301_showHsOtherInfo.loadTabContext(this);">
                        <a href="javascript:void(0);"><span>人员状况</span></a>
                    </li>
                </c:if>

                <li class="js_load_tab"
                    onclick="ph00301_showHsOtherInfo.loadTabContext(this);">
                    <a href="javascript:void(0);"><span>附属信息</span></a>
                </li>
                <li class="js_load_tab"
                    onclick="ph00301_showHsOtherInfo.loadTabContext(this);">
                    <a href="javascript:void(0);"><span>经营状况</span></a>
                </li>
                <li class="js_load_tab"
                    onclick="ph00301_showHsOtherInfo.loadTabContext(this);">
                    <a href="javascript:void(0);"><span>保障房申请情况</span></a>
                </li>
            </ul>
        </div>
    </div>
    <div class="tabsContent">
        <c:if test="${isPhHs=='1'}">
            <div style="min-height: 150px">
                <table class="border marb5" width="100%">
                    <tr>
                        <th width="10%"><label>问题分类：</label></th>
                        <td width="40%">
                                ${phType}
                        </td>
                        <th width="10%"><label>问题程度：</label></th>
                        <td width="15%">
                                ${pbLevel}
                        </td>
                    </tr>
                    <tr>
                        <th><label>问题详情：</label></th>
                        <td colspan="3">
                                ${pbDetail}
                        </td>
                    </tr>
                    <tr>
                        <th><label>谈户方案：</label></th>
                        <td colspan="3">
                                ${pbPlan}
                        </td>
                    </tr>
                </table>
            </div>
        </c:if>
        <%--其它信息--%>
        <div style="min-height: 150px">
            <table class="border marb5" width="100%">
                <tr>
                    <th width="10%"><label>残疾证数：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.cjNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="残疾低保证件" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="cjDocIds" value="${houseInfo.HouseInfo.cjDocIds}">
                            <label style="cursor:pointer;">查看</label></span>
                    </td>
                    <th width="10%"><label>低保证数：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.dbNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="残疾低保证件" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="dbDocIds" value="${houseInfo.HouseInfo.dbDocIds}">
                            <label style="cursor:pointer;">查看</label></span>
                    </td>
                    <th width="10%"><label>高龄人数：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.hAgeNum}</span></td>
                </tr>
                <tr>
                    <th width="10%"><label>大病人数：</label></th>
                    <td width="15%" style="padding: 1px 2px">
                        <%--展示模式--%>
                        <div class="js_viewModel">
                            <span class="marl10 js_hs_doc_num js_seriousIll">${houseInfo.HouseInfo.seriousIll}</span>
                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr10"
                                       name="编辑" rhtCd="edit_serious_ill_rht"
                                       onClick="ph00301_showHsOtherInfo.editIllNum(this);"/>
                            <span class="js_doc_info link marr20 hidden" docTypeName="大病附件" editAble="false" style="float: right"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)">
                                <input type="hidden" class="js_seriousIllDocIds"
                                       value="${houseInfo.HouseInfo.seriousIllDocIds}">
                                <label style="cursor:pointer;">查看</label>
                            </span>
                        </div>
                        <%--编辑模式--%>
                        <div class="js_editModel hidden">
                            <input type="text" class="digits needDoc" name="seriousIll"
                                   style="width:65%;height: 24px;padding: 0 2px"
                                   onchange="ph00301_showHsOtherInfo.flashInputRule(this);"
                                   value="${houseInfo.HouseInfo.seriousIll}"/>
                            <span class="js_doc_info link hidden" docTypeName="大病附件"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)">
                                <input type="hidden" name="seriousIllDocIds"
                                       value="${houseInfo.HouseInfo.seriousIllDocIds}">
                                <label style="cursor:pointer;">查看</label>
                            </span>
                            <span onclick="ph00301_showHsOtherInfo.editIllNum(this);" style="float: right;"
                                  class="link marr10"> X </span>
                            <span onclick="ph00301_showHsOtherInfo.saveIllNum('${hsId}', this);" style="float: right;"
                                  class="link marr10"> √ </span>
                        </div>
                    </td>
                    <th width="10%"><label>两劳人数：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.jailNum}</span></td>
                    <th width="10%"><label>服兵役人数：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.armyNum}</span></td>
                </tr>
                <tr>
                    <th width="10%"><label>公职人员：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.gzPerson}</span></td>
                    <td colspan="4"></td>
                </tr>
                <tr>
                    <th><label>备注说明：</label></th>
                    <td colspan="5">${houseInfo.HouseInfo.hsNote}</td>
                </tr>
            </table>
        </div>
        <%--附属信息--%>
        <div style="min-height: 150px">
            <table class="border marb5" width="100%">
                <tr>
                    <th width="10%"><label>空调数量：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.ktNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="移机补助发票" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="ktDocIds" value="${houseInfo.HouseInfo.ktDocIds}">
                            <label style="cursor:pointer;">查看</label>
                            </span>
                    </td>
                    <th width="10%"><label>有线数量：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.yxNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="移机补助发票" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="yxDocIds" value="${houseInfo.HouseInfo.yxDocIds}">
                            <label style="cursor:pointer;">查看</label>
                            </span>
                    </td>
                    <th width="10%"><label>固话数量：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.ghNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="移机补助发票" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="ghDocIds" value="${houseInfo.HouseInfo.ghDocIds}">
                            <label style="cursor:pointer;">查看</label>
                            </span>
                    </td>
                </tr>
                <tr>
                    <th><label>热水器数量：</label></th>
                    <td><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.rsqNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="移机补助发票" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="rsqDocIds" value="${houseInfo.HouseInfo.rsqDocIds}">
                            <label style="cursor:pointer;">查看</label>
                            </span>
                    </td>
                    <th><label>网络数量：</label></th>
                    <td><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.wlNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="移机补助发票" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="rsqDocIds" value="${houseInfo.HouseInfo.wlDocIds}">
                            <label style="cursor:pointer;">查看</label>
                            </span>
                    </td>
                    <th width="10%"><label>机动车数量：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.motorNum}</span></td>
                </tr>
                <tr>
                    <th><label>煤改电个人承担：</label></th>
                    <td><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.mgdGrZf}</span>
                            <span class="js_doc_info link marr10" docTypeName="移机补助发票" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="mgDDocIds" value="${houseInfo.HouseInfo.mgDDocIds}">
                            <label style="cursor:pointer;">查看</label>
                            </span>
                    </td>
                    <td colspan="4"></td>
                </tr>
                <tr style="height: 60px">
                    <th><label>备注说明：</label></th>
                    <td colspan="5"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.fwfsNotes}</span></td>
                </tr>
            </table>
        </div>
        <%--营业信息--%>
        <div style="min-height: 150px">
            <table class="border marb5" width="100%">
                <tr>
                    <th width="10%"><label>营业执照编号：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.businessCertNum}</span>
                            <span class="js_doc_info link marr10" docTypeName="经营证件" editable="false"
                                  onclick="ph00301_showHsOtherInfo.showDoc(this)" style="float:right;">
                            <input type="hidden" name="businessCertDocIds"
                                   value="${houseInfo.HouseInfo.businessCertDocIds}">
                            <label style="cursor:pointer;">查看</label>
                            </span>
                    </td>
                    <th width="10%"><label>经营面积：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.businessHsSize}</span></td>
                    <th width="10%"><label>执照有效期：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.yxDate}</span></td>
                </tr>
                <tr>
                    <th width="10%"><label>经营者姓名：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.busiMager}</span></td>
                    <th width="10%"><label>注册地点：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.regstPlace}</span></td>
                    <th width="10%"><label>与产承人关系：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.busiRelOwner}</span></td>
                </tr>
                <tr class="js_busi" style="height: 60px;">
                    <th><label>备注说明：</label></th>
                    <td colspan="5"><span class="marl10">${houseInfo.HouseInfo.businessNote}</span></td>
                </tr>
            </table>
        </div>
        <%--申请保障性住房情况--%>
        <div style="min-height: 150px">
            <table class="border marb5" width="100%">
                <tr>
                    <th width="10%"><label>是否享受过：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num"><oframe:name prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" value="${houseInfo.HouseInfo.isEngoy}"/></span></td>
                    <th width="10%"><label>申请类别：</label></th>
                    <td width="15%"><span class="marl10">${houseInfo.HouseInfo.aployCatgry}</span></td>
                    <th width="10%"><label>是否已选房：</label></th>
                    <td width="15%"><span class="marl10">
                        <oframe:name prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" value="${houseInfo.HouseInfo.isChoose}"/></span>
                    </td>
                </tr>
                <tr>
                    <th width="10%"><label>申请人姓名：</label></th>
                    <td width="15%"><span class="marl10 js_hs_doc_num">${houseInfo.HouseInfo.aployName}</span></td>
                    <th width="10%"><label>所选房源地址：</label></th>
                    <td colspan="3" width="15%"><span class="marl10">${houseInfo.HouseInfo.chooseHsPlac}</span></td>
                </tr>
                <tr style="height: 60px;">
                    <th><label>备注说明：</label></th>
                    <td colspan="5"><span class="marl10">${houseInfo.HouseInfo.bzxNote}</span></td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script>
    var ph00301_showHsOtherInfo = {
        /**
         * 编辑大病人数, 或取消编辑
         */
        editIllNum: function (obj) {
            var $thisTd = $(obj).closest("td");
            var $thisDiv = $(obj).closest("div");
            if ($thisDiv.hasClass("js_viewModel")) {
                $("div.js_viewModel", $thisTd).hide();
                $("div.js_editModel", $thisTd).show();
            } else {
                $("div.js_viewModel", $thisTd).show();
                $("div.js_editModel", $thisTd).hide();
            }
        },

        /**
         * 处理是否有附件
         */
        flashInputRule: function (obj) {
            var checkObj = $(obj);
            var objValue = $(obj).val();
            if (checkObj.hasClass("ctRequired")) {
                if (objValue == "") {
                    checkObj.addClass("inputWarn");
                } else {
                    checkObj.removeClass("inputWarn");
                }
            }
            if (checkObj.hasClass("needDoc")) {
                var checkValue = "";
                if (checkObj.hasClass("number") || checkObj.hasClass("digits")) {
                    if (objValue == "") {
                        objValue = 0;
                    } else {
                        objValue = parseFloat(objValue);
                    }
                    checkValue = 0;
                }
                var spanInfo = checkObj.next("span.js_doc_info");
                if (spanInfo.length > 0) {
                    if (objValue == checkValue) {
                        spanInfo.removeClass("docWarn").addClass("hidden").hide();
                    } else {
                        spanInfo.removeClass("hidden").show();
                        var docIds = spanInfo.find("input[type=hidden]").val();
                        if (docIds == "") {
                            spanInfo.addClass("docWarn").removeClass("hidden");
                            spanInfo.find("label").html("上传");
                        } else {
                            spanInfo.removeClass("docWarn");
                            spanInfo.find("label").html("查看");
                        }
                    }
                }
            }
        },

        /**
         * 保存大病人数
         */
        saveIllNum: function (hsId, obj) {
            var $thisDiv = $(obj).closest("div");
            var seriousIll = $("input[name=seriousIll]", $thisDiv).val();
            var seriousIllDocIds = $("input[name=seriousIllDocIds]", $thisDiv).val();

            var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-saveIllNum.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("hsId", hsId);
            packet.data.add("seriousIll", seriousIll);
            packet.data.add("seriousIllDocIds", seriousIllDocIds);
            packet.noCover = true;
            core.ajax.sendPacketHtml(packet, function (response) {
                var result = eval("(" + response + ")");
                if (result.success) {
                    alertMsg.correct("修改成功！");
                    ph00301_showHsOtherInfo.editIllNum(obj);
                    var viewDiv = $("div.js_viewModel", navTab.getCurrentPanel());
                    $("span.js_seriousIll", viewDiv).text(seriousIll);
                    $("input.js_seriousIllDocIds", viewDiv).val(seriousIllDocIds);
                } else {
                    alertMsg.error(result.errMsg);
                }
            }, true);
        },

        /**
         * 显示关联文档信息
         * @param clickObj 点击对象
         */
        showDoc: function (clickObj) {
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
            var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
                    + "&docIds=" + docIds + "&editAble=" + editAble
                    + "&relType=100&relId=${hsId}";
            $.pdialog.open(url, "file", "查看附件", {
                height: 600, width: 800, mask: true,
                close: ph00301_showHsOtherInfo.docClosed,
                param: {clickSpan: $span}
            });
        },

        /**
         * 关闭上传窗口
         * @param param
         * @returns {boolean}
         */
        docClosed: function (param) {
            var uploadedFiles = file.getAllUploadFiles();
            var $span = param.clickSpan;
            var docIdArr = [];
            for (var i = 0; i < uploadedFiles.length; i++) {
                var uploadedFile = uploadedFiles[i];
                docIdArr.push(uploadedFile.docId);
            }
            $span.find("input[type=hidden]").val(docIdArr.join(","));
            if (docIdArr.length > 0) {
                var label = $span.find("label");
                if (label.html() == "上传") {
                    label.html("查看");
                }
                $span.removeClass("docWarn");
            } else {
                var label = $span.find("label");
                if (label.html() == "查看") {
                    label.html("上传");
                }
                $span.addClass("docWarn");
            }
            // 调用文件的关闭
            return file.closeD();
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
            if ($(this).val() && $(this).val() != "") {
                $(this).parent("span.js_doc_info").show();
            } else {
                $(this).parent("span.js_doc_info").hide();
            }
        });
    });
</script>
