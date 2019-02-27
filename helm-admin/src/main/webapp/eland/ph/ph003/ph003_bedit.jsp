<%--批量修改房源数据--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="edit" onclick="ph003_bedit.batchSaveHs()"><span>修改</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <input type="hidden" name="houseIds" value="${houseIds}">
        <table class="border">
            <tr>
                <th width="20%"><label>修改项目：</label></th>
                <td>
                    <select name="fieldName" style="width: auto;"
                            onchange="ph003_bedit.changeCondition(this);">
                        <c:forEach items="${queryConditionInfo}" var="item">
                            <option value="${item.EntityAttr.attrNameEn}"
                                    attrSltType="${item.EntityAttr.attrSltType}"
                                    attrRuleCss="${item.EntityAttr.attrRuleCss}"
                                    attrRelItemCd="${item.EntityAttr.attrRelItemCd}">${item.EntityAttr.attrNameCh}</option>
                        </c:forEach>
                    </select>
                    等于
                    <span style="position: relative;">
                        <input type="hidden" class="js_real_value"/>
                        <input name="fieldValue" type="text" atOption="ph003_bedit.getOpt"
                               value="" realValue="" style="width: 50%;"
                               onkeydown="if(event.keyCode == 13){ph003_bedit.addField(this);}"
                               class="autocomplete textInput">
                           </span>
                    <button type="button" class="btn btn-more" onclick="ph003_bedit.addField(this);">
                        <i style="font-style:normal;">添加</i>
                    </button>
                    <button type="button" class="btn btn-more" onclick="ph003_bedit.clearAll(this);">
                        <i style="font-style:normal;">清空</i>
                    </button>
                </td>
            </tr>
            <tr>
                <th><label>修改内容：</label></th>
                <td class="js_condition">
                    <label class="marl5 marr5 marb5 qkeyword js_keyword hidden">
                        <input type="hidden" name="fieldName" value="">
                        <input type="hidden" name="fieldValue" value="">
                        <span class="in-block"></span>
                        <span class="removeX" onclick="$(this).parent().remove();">X</span>
                    </label>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    ph003_bedit = {
        /**
         * 删除所有查询条件
         * **/
        clearAll: function (obj) {
            var container = $("td.js_condition", $.pdialog.getCurrent());
            $("label.js_keyword", container).each(function () {
                if (!$(this).hasClass("hidden")) {
                    $(this).remove();
                }
            });
        },

        /**
         * 增查询条件
         **/
        addField: function (obj) {
            var sltTd = $(obj).closest("td");
            var container = $("td.js_condition", $.pdialog.getCurrent());
            var newKey = $("label.js_keyword", container).first().clone();
            $("span.in-block", newKey).val("");
            newKey.appendTo(container);
            var fieldName = $("select[name=fieldName]", sltTd).val();
            var attrSltType = $("select[name=fieldName]", sltTd).find("option:selected").attr("attrSltType");
            var fieldNameText = $("select[name=fieldName]", sltTd).find("option:selected").text();
            var fieldValue = $("input[name=fieldValue]", sltTd).attr("realValue");
            var realValue = $("input.js_real_value", sltTd).val();
            if (attrSltType == "ORG") {
                fieldValue = realValue;
            } else if (attrSltType == "REG('1')") {
                fieldValue = realValue;
            }
            var fieldValueText = $("input[name=fieldValue]", sltTd).val();
            if (fieldValue == "") {
                fieldValue = fieldValueText;
            }
            // 赋值
            $("span.in-block", newKey).html("\"" + fieldNameText + "\"等于\"" + fieldValueText + "\"");
            $("input[name=fieldName]", newKey).val(fieldName);
            $("input[name=fieldValue]", newKey).val(fieldValue);
            newKey.show();
            newKey.css({"display": "inline-block"});
            return false;
        },

        /**
         * 修改查询条件
         **/
        changeCondition: function (obj) {
            var $td = $(obj).closest("td");
            var sltOption = $(obj).find("option:selected");
            var attrRelItemCd = sltOption.attr("attrRelItemCd");
            $("input[name=fieldValue]", $td).val("");
            $("input[name=fieldValue]", $td).attr("realValue", "");
        },

        /**
         * 获取区域参数
         * @param obj
         * @returns
         */
        getOpt: function (obj) {
            return {
                processData: function (result) {
                    var myData = [];
                    for (i = 0; i < result.length; i++) {
                        var dataRow = {};
                        dataRow.data = result[i];
                        dataRow.value = result[i].value;
                        myData.push(dataRow);
                    }
                    return myData;
                },
                showResult: function (value, data) {
                    return data.value;
                },
                displayValue: function (value, data) {
                    return data.value;
                },
                remoteDataType: "json",
                delay: 300,
                minChars: 0,
                filterResults: true,
                onItemSelect: function (obj) {
                    var data = obj.data;
                    var source = $(obj.source);
                    source.attr("realValue", data.key);
                },
                onNoMatch: function (obj) {
                    var source = $(obj.source);
                    var $td = source.closest("td");
                    var sltOption = $("select[name=fieldName]", $td).find("option:selected");
                    if (sltOption.attr("attrRelItemCd")) {
                        source.attr("realValue", "");
                        source.val("");
                        $("input.js_real_value", $td).val("");
                    }
                },
                fetchData: function (obj) {
                    var resultData = {};
                    var $td = $(obj).closest("td");
                    var sltOption = $("select[name=fieldName]", $td).find("option:selected");
                    var attrSltType = sltOption.attr("attrSltType");
                    var attrRelItemCd = sltOption.attr("attrRelItemCd");
                    if (attrSltType == "CODE" && attrRelItemCd && attrRelItemCd != "") {
                        var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?";
                        var ajaxPacket = new AJAXPacket(url);
                        ajaxPacket.data.add("prjCd", getPrjCd());
                        ajaxPacket.data.add("itemCd", attrRelItemCd);
                        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                            resultData = eval("(" + response + ")");
                        }, false);
                    } else if (attrSltType == "ORG") {
                        $(obj).sltOrg(obj, {relObj: $(obj), offsetY: 26, width: $(obj).width() + 5});
                    } else if (attrSltType == "REG('1')") {
                        $(obj).sltReg(obj, {relObj: $(obj), offsetY: 26, width: $(obj).width() + 5});
                    }
                    return resultData;
                }
            }
        },

        /**
         * 提交选择条件
         **/
        batchSaveHs: function () {
            var container = $.pdialog.getCurrent();
            var fieldNameArr = [];
            var fieldValueArr = [];
            $("label.js_keyword", container).each(function () {
                var $this = $(this);
                var fieldName = $("input[name=fieldName]", $this).val();
                var fieldValue = $("input[name=fieldValue]", $this).val();
                if (fieldName == "") {
                    return true;
                } else {
                    fieldNameArr.push(fieldName);
                    fieldValueArr.push(fieldValue);
                }
            });
            if (fieldNameArr.length == 0) {
                return;
            }
            // 提交修改内容
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-bEdit.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("prjCd", getPrjCd());
            ajaxPacket.data.add("fieldNames", fieldNameArr.join(","));
            ajaxPacket.data.add("fieldValues", fieldValueArr.join(","));
            ajaxPacket.data.add("houseIds", $("input[name=houseIds]", container).val());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("修改成功");
                    $.pdialog.closeCurrent();
                    ph001.refleshBuild();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
</script>
