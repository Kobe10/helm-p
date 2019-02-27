var ph012 = {
    /**
     * 打开快速检索对话框
     */
    openQSch: function (obj) {
        if (!$(obj).hasClass("active")) {
            $("#ph012create", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#ph012create input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },
    /**
     * 关闭检索
     */
    closeQSch: function () {
        $("#ph012create", navTab.getCurrentPanel()).hide();
    },
    /**
     *通用查询
     */
    query: function () {
        var formObj = $("#ph012form", navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#ph012create", navTab.getCurrentPanel());
        // 覆盖的查询条件
        var coverConditionName = [];
        var coverCondition = [];
        var coverConditionValue = [];
        var coverNames = [];
        // 循环当前查询条件
        $("input", dialogObj).each(function () {
            var $this = $(this);
            var attrName = $this.attr("name");
            var tagName = $this[0].tagName;
            var condition = $this.attr("condition");
            var value = $this.val();
            if ("INPUT" == tagName && condition) {
                coverNames.push(attrName);
                if (value != "") {
                    coverConditionName.push(attrName);
                    coverCondition.push(condition);
                    coverConditionValue.push(value);
                }
            }
        });
        // 获取历史查询条件
        var oldConditionName = $("input[name=conditionName]", formObj).val().split(",");
        var oldCondition = $("input[name=condition]", formObj).val().split(",");
        var oldConditionValue = $("input[name=conditionValue]", formObj).val().split(",");

        // 实际使用的查询条件
        var newConditionName = [];
        var newCondition = [];
        var newConditionValue = [];

        // 处理历史查询条件，存在覆盖则删除原有的查询条件
        for (var i = 0; i < oldConditionName.length; i++) {
            var conditionName = oldConditionName[i];
            var found = false;
            for (var j = 0; j < coverNames.length; j++) {
                if (conditionName == coverNames[j]) {
                    found = true;
                    break;
                }
            }
            // 未找到匹配的条件
            if (!found) {
                newConditionName.push(oldConditionName[i]);
                newCondition.push(oldCondition[i]);
                newConditionValue.push(oldConditionValue[i]);
            }
        }

        // 追加新的查询条件
        newConditionName = newConditionName.concat(coverConditionName);
        newCondition = newCondition.concat(coverCondition);
        newConditionValue = newConditionValue.concat(coverConditionValue);
        // 重新设置查询条件
        $("input[name=conditionName]", formObj).val(newConditionName.join(","));
        $("input[name=condition]", formObj).val(newCondition.join(","));
        $("input[name=conditionValue]", formObj).val(newConditionValue.join(","));
        // 再次执行查询
        Page.query($("#ph012form", navTab.getCurrentPanel()), "");
    },
    /**
     *单条删除
     */
    deleteRel: function (hsCtReId) {
        alertMsg.confirm("您确定要删除记录吗?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/ph/ph012/ph012-deleteRel.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCd", getPrjCd());
                ajaxPacket.data.add("hsCtReId", hsCtReId);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("删除成功！");
                        ph012.query();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
        stopEvent(event);
    },

    /**
     * 编辑所在行的值
     * */
    changeEdit: function (hsCtReId) {
        var url = getGlobalPathRoot() + "eland/ph/ph012/ph012-editRelDialog.gv?prjCd=" + getPrjCd() + "&hsCtReId=" + hsCtReId;
        $.pdialog.open(url, "ph012-editRel", "编辑关联房产", {
            mask: true, max: false, maxable: false, resizable: false, width: 800, height: 320
        });
    },
    /**
     * 保存所编辑 关系行
     */
    saveRel: function (obj) {
        var $form = $("#ph012dialog", $.pdialog.getCurrent());
        if ($form.valid()) {
            var hsCtIdA = $("input[name=hsCtIdA]", $.pdialog.getCurrent()).val();
            var hsCtIdB = $("input[name=hsCtIdB]", $.pdialog.getCurrent()).val();
            var relName = $("input[name=relName]", $.pdialog.getCurrent()).val();
            if (hsCtIdA && hsCtIdB && relName) {
                var hsCtReId = $("input[name=hsCtReId]", $.pdialog.getCurrent()).val();
                var reNote = $("input[name=reNote]", $.pdialog.getCurrent()).val();

                var url = getGlobalPathRoot() + "eland/ph/ph012/ph012-saveRel.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCd", getPrjCd());
                ajaxPacket.data.add("hsCtReId", hsCtReId);
                ajaxPacket.data.add("hsCtIdA", hsCtIdA);
                ajaxPacket.data.add("hsCtIdB", hsCtIdB);
                ajaxPacket.data.add("relName", relName);
                ajaxPacket.data.add("reNote", reNote);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("保存成功！");
                        $.pdialog.closeCurrent();
                        ph012.query();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                }, true);
            } else {
                alertMsg.warn("请选择要关联的房产，并填写关联关系！");
            }
        }
    },

    /**
     * 点击被安置人 查询 安置人对应房产
     */
    getHsByPsName: function (obj, flag) {
        var url = getGlobalPathRoot() + "eland/ph/ph012/ph012-getHsByPsName.gv?prjCd=" + getPrjCd() + "&flag=" + flag;
        $(obj).openTip({
            href: url, height: "auto", width: "500px", appendBody: true,
            offsetY: 0,
            offsetX: 0,
            args: {
                hsOwnerPersons: $("input[name=" + flag + "]", $.pdialog.getCurrent()).val()
            },
            relObj: $(obj).prev("input")
        });
    },

    /**
     * 根据查询结果选择 需要关联的产权人及 房产
     */
    chooseHsPs: function (hsCtId, hsFullAddr, hsOwnerPersons, flag, obj) {
        var $this = $(obj);
        var orgName = $this.find("td:last").html();
        $("#openTip").remove();
        if (flag == "ctPsNamesA") {
            $("input[name=hsCtIdA]", $.pdialog.getCurrent()).val(hsCtId);
            $("input[name=ctPsNamesA]", $.pdialog.getCurrent()).val(hsOwnerPersons);
            $("td.js_hsFullAddrA", $.pdialog.getCurrent()).html(hsFullAddr);
            $("td.js_ttOrgIdA", $.pdialog.getCurrent()).html(orgName);
        } else {
            $("input[name=hsCtIdB]", $.pdialog.getCurrent()).val(hsCtId);
            $("input[name=ctPsNamesB]", $.pdialog.getCurrent()).val(hsOwnerPersons);
            $("td.js_hsFullAddrB", $.pdialog.getCurrent()).html(hsFullAddr);
            $("td.js_ttOrgIdB", $.pdialog.getCurrent()).html(orgName);
        }

        //两条记录都选择之后 查询有关联关系。
        var hsCtIdA = $("input[name=hsCtIdA]", $.pdialog.getCurrent()).val();
        var hsCtIdB = $("input[name=hsCtIdB]", $.pdialog.getCurrent()).val();
        if (hsCtIdA && hsCtIdB) {
            var url = getGlobalPathRoot() + "eland/ph/ph012/ph012-getHsRelByTwoCtId.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("prjCd", getPrjCd());
            ajaxPacket.data.add("hsCtIdA", hsCtIdA);
            ajaxPacket.data.add("hsCtIdB", hsCtIdB);
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                $("input[name=hsCtReId]", $.pdialog.getCurrent()).val(jsonData.hsCtReId);
                $("input[name=relName]", $.pdialog.getCurrent()).val(jsonData.relName);
                $("input[name=reNote]", $.pdialog.getCurrent()).val(jsonData.reNote);
            });
        }
    },
    /**
     * 改变产权人清楚 所选房屋
     */
    changePs: function (obj, flag) {
        var $this = $(obj);
        var hsCtIdA = $("input[name=hsCtIdA]", $.pdialog.getCurrent()).val();
        var hsCtIdB = $("input[name=hsCtIdB]", $.pdialog.getCurrent()).val();

        if (flag == "ctPsNamesA") {
            if (hsCtIdA) {
                $("input[name=hsCtIdA]", $.pdialog.getCurrent()).val("");
                $("input[name=ctPsNamesA]", $.pdialog.getCurrent()).val("");
                $("td.js_hsFullAddrA", $.pdialog.getCurrent()).html("");
                $("td.js_ttOrgIdA", $.pdialog.getCurrent()).html("");
            }
        } else {
            if (hsCtIdB) {
                $("input[name=hsCtIdB]", $.pdialog.getCurrent()).val("");
                $("input[name=ctPsNamesB]", $.pdialog.getCurrent()).val("");
                $("td.js_hsFullAddrB", $.pdialog.getCurrent()).html("");
                $("td.js_ttOrgIdB", $.pdialog.getCurrent()).html("");
            }
        }
    },

    /**
     * 根据姓名 自动补全 房产
     */
    getHsUrl: function () {
        return getGlobalPathRoot() + "eland/ph/ph012/ph012-getHsByPsName.gv?prjCd=" + getPrjCd();
    },
    getHsOpt: function () {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].hsOwnerPersons;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.hsOwnerPersons;
            },
            displayValue: function (value, data) {
                return data.hsOwnerPersons;
            },
            dyExtraParams: function (obj) {
                return {
                    hsOwnerPersons: $("input[name=ctPsNamesA]", $.pdialog.getCurrent()).val(),
                    prjCd: getPrjCd()
                }
            },
            onItemSelect: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input[name=ctPsNamesA]").val(obj.data.hsOwnerPersons);
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            queryParamName: "hsOwnerPersons",
            filterResults: true
        }
    }


};

$(document).ready(function () {
    // 执行查询
    ph012.query();
});