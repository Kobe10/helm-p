/**
 * Created by Dennis on 15/9/14.
 */
var ct002 = {
    uploadObj: null,
    /**
     * 获取参数信息
     * @param obj
     * @returns
     */
    getCfgDataOpt: function (obj) {
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
            sortResults: false,
            filterResults: false,
            maxItemsToShow: 50,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var $td = source.closest("td");
                $("input[type=hidden]", $td).val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var $td = source.closest("td");
                var mustMatch = source.attr("mustMatch");
                if (!"false" == mustMatch) {
                    source.val("");
                }
                $("input[type=hidden]", $td).val("");
            },
            fetchData: function (obj) {
                var resultData = {};
                var $td = $(obj).closest("td");
                var attrRelItemCd = $(obj).attr("itemCd");
                if (attrRelItemCd && attrRelItemCd != "") {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("itemCd", attrRelItemCd);
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        resultData = eval("(" + response + ")");
                    }, false);
                }
                return resultData;
            }
        }
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
            sortResults: false,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var $td = source.closest("td");
                $("input[name=code]", $td).val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var $td = source.closest("td");
                $("input[name=code]", $td).val("");
                $("input[name=name]", $td).val(source.val());
            },
            fetchData: function (obj) {
                var resultData = {};
                var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-itemMap.gv?";
                var schemeType = $("select[name=schemeType]", navTab.getCurrentPanel()).val();
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCd", getPrjCd());
                ajaxPacket.data.add("schemeType", schemeType);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    resultData = eval("(" + response + ")");
                }, false);
                return resultData;
            }
        }
    },

    init: function (schemeId) {
        var url = "eland/ct/ct002/ct002-init.gv";
        if (schemeId) {
            url = "eland/ct/ct002/ct002-init.gv?schemeId=" + schemeId;
        }
        index.quickOpen(url, {opCode: "ct002-init", opName: "预分方案"});
    },


    /**
     *
     * @returns {*|jQuery}
     */
    getCode: function () {
        return $("textarea[name=ruleScript]", navTab.getCurrentPanel()).val();
    },

    /**
     * 方案类型定义说明
     * @param schemeType 方案类型
     */
    typeInfo: function (schemeType) {
        if (!schemeType) {
            schemeType = "";
        }
        var ulObj = $("ul.accordion_menu");
        ulObj.find("li.selected").each(function () {
            $(this).removeClass("selected");
        });
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-typeInfo.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeType", schemeType);
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct002Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct002Context", navTab.getCurrentPanel()));
        }, true);
        stopEvent(event);
    },


    info: function (schemeId, obj) {
        if (!schemeId) {
            schemeId = "";
        } else {
            if (obj) {
                obj = $("#" + schemeId, navTab.getCurrentPanel());
            }
        }
        var _this = $(obj);
        var ulObj = $("ul.accordion_menu");
        //var ulObj = _this.closest("ul.accordion_menu");
        ulObj.find("li.selected").each(function () {
            $(this).removeClass("selected");
        });
        _this.addClass("selected");
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-info.gv";
        var schemeType = _this.attr("schemeType");
        var packet = new AJAXPacket(url);
        //packet.noCover = true;
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeId", schemeId);
        packet.data.add("schemeType", schemeType);
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct002Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct002Context", navTab.getCurrentPanel()));
        }, true);
    },

    /**
     * 展示定义预分方案 页面
     */
    showDefinition: function (obj) {
        var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
        currentModelObj.val("1");
        var schemeId = $("input[name=schemeId]", navTab.getCurrentPanel()).val();
        //
        if (!schemeId) {
            schemeId = "";
        } else {
            if (obj) {
                obj = $("#" + schemeId, navTab.getCurrentPanel());
            }
        }
        var _this = $(obj);
        var ulObj = _this.closest("ul.accordion_menu");
        ulObj.find("li.active").each(function () {
            $(this).removeClass("active");
        });
        _this.addClass("active");

        var packet = new AJAXPacket();
        packet.url = getGlobalPathRoot() + "eland/ct/ct002/ct002-info.gv";
        //packet.noCover = true;
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeId", schemeId);
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct002Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct002Context", navTab.getCurrentPanel()));
        }, true);
    },
    /**
     * 展示 预计算结果 页面
     */
    showCalcResult: function (obj) {
        var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
        currentModelObj.val("2");
        var schemeId = $("input[name=schemeId]", navTab.getCurrentPanel()).val();
        //
        if (!schemeId) {
            schemeId = "";
        } else {
            if (obj) {
                obj = $("#" + schemeId, navTab.getCurrentPanel());
            }
        }
        var _this = $(obj);
        var ulObj = _this.closest("ul.accordion_menu");
        ulObj.find("li.active").each(function () {
            $(this).removeClass("active");
        });
        _this.addClass("active");

        var packet = new AJAXPacket();
        packet.url = getGlobalPathRoot() + "eland/ct/ct002/ct002-calcResult.gv";
        //packet.noCover = true;
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeId", schemeId);
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct002Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct002Context", navTab.getCurrentPanel()));
        }, true);
    },

    /**
     * 保存预分方案
     */
    saveScheme: function (schemeId, flag) {
        var schemeName = $("input[name=schemeName]", navTab.getCurrentPanel()).val();
        var schemeStatus = $("input[name=schemeStatus]", navTab.getCurrentPanel()).val();
        var ctType = $("select[name=ctType]", navTab.getCurrentPanel()).val();
        var conditionRule = $("select[name=conditionRule]", navTab.getCurrentPanel()).val();
        var schemeType = $("select[name=schemeType]", navTab.getCurrentPanel()).val();
        var ct002form = $("#ct002Form", navTab.getCurrentPanel());
        if (ct002form.valid()) {
            if (ct002.checkScheme()) {
                var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-saveScheme.gv";
                var packet = new AJAXPacket(url);
                packet.data.data = ct002form.serializeArray();
                packet.data.data.push({name: "prjCd", value: getPrjCd()});
                packet.data.data.push({name: "schemeName", value: schemeName});
                packet.data.data.push({name: "schemeStatus", value: schemeStatus});
                packet.data.data.push({name: "ctType", value: ctType});
                packet.data.data.push({name: "conditionRule", value: conditionRule});
                packet.data.data.push({name: "schemeId", value: schemeId});
                packet.data.data.push({name: "schemeType", value: schemeType});
                packet.data.data.push({name: "flag", value: flag});

                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.isSuccess) {
                        alertMsg.correct("保存成功！");
                        ct002.init(jsonData.schemeId);
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                }, true);
            }
        }
    },

    //check Scheme 条目是否有重复。
    checkScheme: function () {
        //验证条目不能重复
        var tempArray = new Array();
        var checkResult = true;
        $("#treeBody", navTab.getCurrentPanel()).find("tr").each(function () {
                var everyTr = $(this);
                var inputName = everyTr.find("input[name=name]");
                var inputCode = everyTr.find("input[name=code]");
                if (inputName) {
                    var newValue = inputName.val();
                    for (var i = 0; i < tempArray.length; i++) {
                        if (newValue == tempArray[i]) {
                            inputName.addClass("error noErrorTip");
                            alertMsg.warn("科目不可以重复选择！");
                            checkResult = false;
                            return false;
                        }
                    }
                    if (checkResult) {
                        checkResult = true;
                        tempArray.push(inputName.val());
                        inputName.val(inputName.val());
                        inputCode.val(inputName.closest("td").find("input[name=code]").val());
                    }

                }
            }
        );
        return checkResult;
    },

    /**
     * 删除方案
     */
    delScheme: function (schemeId) {
        alertMsg.confirm("确认删除该方案？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-delScheme.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("schemeId", schemeId);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.isSuccess) {
                        alertMsg.correct("删除成功！");
                        ct002.init();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                }, true);
            }
        });
    },

    /**
     * 启用方案
     */
    enableScheme: function (schemeId, ableFlag) {
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-enableScheme.gv";
        var schemeType = $("li.selected", navTab.getCurrentPanel()).attr("schemeType");
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeId", schemeId);
        packet.data.add("ableFlag", ableFlag);
        packet.data.add("schemeType", schemeType);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.isSuccess) {
                alertMsg.correct("处理成功！");
                ct002.init(schemeId);
                //ct002.showDefinition();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        }, true);
    },
    /**
     * 导出方案
     */
    exportScheme: function (schemeId) {
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-exportScheme.gv?prjCd=" + getPrjCd() + "&schemeId=" + schemeId;
        window.open(url);
    },
    /**
     * 导入方案
     */
    importScheme: function (obj) {
        var clickFileBtn = $(obj).find("input[type=file]");
        clickFileBtn.attr("id", new Date().getTime());
        clickFileBtn.val("");
        clickFileBtn.unbind("change", ct002.importXml);
        clickFileBtn.bind("change", ct002.importXml);
        ct002.uploadObj = $(obj);
    },
    importXml: function () {
        var uploadURL = getGlobalPathRoot() + "eland/ct/ct002/ct002-importScheme.gv?prjCd=" + getPrjCd();
        var importSchemeFile = ct002.uploadObj.parent().find("input[type=file]").attr("id");
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: importSchemeFile,
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.success) {
                        alertMsg.correct("导入成功！");
                        ct002.init();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }

            }
        )
    },

    /**
     * 预计算方案
     */
    openRunSetting: function (schemeId) {
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-runSetting.gv?prjCd=" + getPrjCd() + "&schemeId=" + schemeId;
        $.pdialog.open(url, "ct00201", "方案计算",
            {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 180});
    },
    /**
     * 打开预分计算面板
     */
    openRunScheme: function (schemeId) {
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-openRunScheme.gv?prjCd=" + getPrjCd() + "&schemeId=" + schemeId;
        $.pdialog.open(url, "ct002-runScheme", "预分计算",
            {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 150});
    },

    /**
     * 预计算方案
     */
    runScheme: function (schemeId) {
        //var $frm = $("#ct002RunSFrm", $.pdialog.getCurrent());
        //if ($frm.valid()) {
        //    var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-runScheme.gv";
        //    var packet = new AJAXPacket(url);
        //    var formData = $frm.serializeArray();
        //    formData.push({name: "prjCd", value: getPrjCd()});
        //    packet.data.data = formData;
        //    core.ajax.sendPacketHtml(packet, function (response) {
        //        var jsonData = eval("(" + response + ")");
        //        if (jsonData.isSuccess) {
        //            alertMsg.correct("后台计算中...");
        //            $.pdialog.closeCurrent();
        //        } else {
        //            alertMsg.error(jsonData.errMsg);
        //        }
        //    }, true);
        //}
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-runScheme.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeId", schemeId);
        packet.data.add("batchName", $("input[name=batchName]", $.pdialog.getCurrent()).val());
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.isSuccess) {
                alertMsg.correct("计算中...");
                ct002.query();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        }, true);
    },
    /**
     * 导出计算结果
     */
    exportResult: function (schemeId, resultId, preBatch) {
        var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-exportResult.gv";
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeId", schemeId);
        packet.data.add("resultId", resultId);
        packet.data.add("preBatch", preBatch);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot() + "oframe/common/file/file-down.gv?prjCd=" + getPrjCd();
                downloadUrl = downloadUrl + "&remoteFile=" + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile=" + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
    },

    /**
     * 查询
     */
    query: function () {
        var formObj = $("#ct002ResultQueryForm", navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#ct002SchDiv", navTab.getCurrentPanel());
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
        Page.query($("#ct002ResultQueryForm", navTab.getCurrentPanel()), "");
    },
    /**
     * 打开快速检索对话框
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#ct002SchDiv", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#ct002SchDiv input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 删除选中批次
     */
    delectSlt: function () {
        var resultIds = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=resultId]").each(
            function (i) {
                resultIds.push($(this).val());
            }
        );
        if (resultIds.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("选择的记录将被删除，你确定要删除吗？", {
                okCall: function () {
                    var packet = new AJAXPacket();
                    // 区域归属项目
                    var prjCd = getPrjCd();
                    packet.data.add("resultIds", resultIds.join(","));
                    packet.data.add("prjCd", prjCd);
                    // 提交界面展示
                    packet.url = getGlobalPathRoot() + "eland/ct/ct002/ct002-deleteResult.gv";
                    // 处理删除
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            ct002.query();
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    }, true);
                }
            });
        }
    },

    /**
     * 调用通用查询组件
     */
    queryScheme: function (obj) {
        ct002.closeQSch(true);
        Page.queryCondition(obj, {
            changeConditions: ct002.changeConditions,
            width: "100%",
            formObj: $("#ct002ResultQueryForm", navTab.getCurrentPanel())
        });
    },
    /**
     * 关闭检索
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#ct002SchDiv", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = true;
            } else {
                close = false;
            }
        }
        if (close) {
            $("#ct002SchDiv", navTab.getCurrentPanel()).hide();
        }
    },
    /**
     * 改变查询条件
     */
    changeConditions: function (newCondition) {
        var formObj = $('#ct002ResultQueryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        Page.query(formObj, "");
    },

    /**
     * 改变方案科目，调整隐藏条件
     * @param obj
     */
    changeSubjectNode: function (obj, alertFlag) {
        var _thisSelect = $(obj);
        var parTd = _thisSelect.parent();
        var temp = true;
        //验证条目不能重复
        $("#treeBody", navTab.getCurrentPanel()).find("input[name=code]").each(function () {
            var inputCode = $(this);
            if (inputCode.val() == _thisSelect.val()) {
                temp = false;
                var code = $("input[name=code]", parTd).val();
                if (code) {
                    _thisSelect.find("option[value=" + code + "]").attr("selected", true);
                } else {
                    _thisSelect.find("option:first").attr("selected", true);
                    $("input[name=name]", parTd).val(_thisSelect.find("option:selected").text());
                    $("input[name=code]", parTd).val(_thisSelect.val());
                }
                _thisSelect.addClass("error noErrorTip");
                if (alertFlag != 'noAlert') {
                    alertMsg.warn("科目不可以重复选择！");
                }
            } else {
                _thisSelect.removeClass("error");
            }
        });

        if (temp) {
            $("input[name=name]", parTd).val(_thisSelect.find("option:selected").text());
            $("input[name=code]", parTd).val(_thisSelect.val());
        }
    },

    ////添加行之后，刷新 select选中值。
    //afterAddCheckValue: function (addTr) {
    //    var addTrSelect = addTr.find("select[name=selectSubject]");
    //    ct002.changeSubjectNode(addTrSelect, "noAlert");
    //},

    /**
     *  ========================= 操作 treeTable ==============================
     **/
    /**
     * 增加孩子节点
     */
    addChildRowNode: function (obj, node) {
        var $this = $(obj);
        if (node == 'root') {
            var trId = '0';
        } else {
            trId = $this.closest("tr").attr("data-tt-id");
        }

        //取最大tr id +1 为新增节点id
        var addTrId = 0;
        $("tr[data-tt-id]", navTab.getCurrentPanel()).each(function () {
            var temp = $(this).attr("data-tt-id");
            if (addTrId < parseInt(temp)) {
                addTrId = parseInt(temp);
            }
        });
        addTrId += 1;
        var hidTabTr = $("#hiddenTable", navTab.getCurrentPanel()).find("tbody").clone(true);
        hidTabTr.find("tr").attr("data-tt-id", addTrId).attr("data-tt-parent-id", trId);
        $("input[name=subId]", hidTabTr).val(addTrId);
        $("input[name=upId]", hidTabTr).val(trId);
        hidTabTr.find("tr").find("td.indexNum").find("label").text(addTrId);

        var node = $("#treeBody", navTab.getCurrentPanel()).treetable("node", trId);
        $("#treeBody", navTab.getCurrentPanel()).treetable("loadBranch", node, hidTabTr.html());
        ct002.serializIdText();
    },
    /**
     * 插入兄弟节点
     */
    addBroRowNode: function (obj, node) {
        var treeTable = $("#treeBody", navTab.getCurrentPanel());
        var $this = $(obj);
        var currTr = $this.closest("tr");
        if (node == 'root') {
            var trPid = '0';
        } else {
            trPid = currTr.attr("data-tt-parent-id");
        }
        //插入之前 先判断 节点是否是当前父级最后一个
        var siblings = treeTable.find('tr[data-tt-parent-id=' + trPid + ']');
        // 获取该元素 位于父节点下 第几个
        var index = siblings.index(currTr);
        var isLastTr = false;
        if (index == siblings.length - 1) {
            isLastTr = true;
        }

        //取最大tr id +1 为新增节点id
        var addTrId = 0;
        $("tr[data-tt-id]", navTab.getCurrentPanel()).each(function () {
            var temp = $(this).attr("data-tt-id");
            if (addTrId < parseInt(temp)) {
                addTrId = parseInt(temp);
            }
        });
        addTrId += 1;
        var hidTabTr = $("#hiddenTable", navTab.getCurrentPanel()).find("tbody").clone(true);
        hidTabTr.find("tr").attr("data-tt-id", addTrId).attr("data-tt-parent-id", trPid);
        $("input[name=subId]", hidTabTr).val(addTrId);
        $("input[name=upId]", hidTabTr).val(trPid);
        hidTabTr.find("tr").find("td.indexNum").find("label").text(addTrId);
        var currNode = $("#treeBody", navTab.getCurrentPanel()).treetable("node", trPid);
        $("#treeBody", navTab.getCurrentPanel()).treetable("loadBranch", currNode, hidTabTr.html());


        //将插入到最后的节点，移动到当前点击tr之后；
        var insertTr = $('tr[data-tt-id=' + addTrId + ']');
        if (!isLastTr) {
            var nextTr = siblings.eq(index + 1);
            nextTr.before(insertTr);
        }
        //ct002.afterAddCheckValue(insertTr);
        initUI(insertTr);
        ct002.serializIdText();
    },
    /**
     * 删除该节点下的孩子节点，保留父节点
     */
    deleteChildsRowNode: function (obj) {
        var $this = $(obj);
        var trId = $this.closest("tr").attr("data-tt-id");
        var node = $("#treeBody", navTab.getCurrentPanel()).treetable("node", trId);
        $("#treeBody", navTab.getCurrentPanel()).treetable("unloadBranch", node);
        ct002.serializIdText();
    },
    /**
     * 删除treeTable节点
     */
    deleteRowNode: function (obj) {
        var $this = $(obj);
        var trId = $this.closest("tr").attr("data-tt-id");
        $("#treeBody", navTab.getCurrentPanel()).treetable("removeNode", trId);
        ct002.serializIdText();
    },
    /**
     * 上移整个节点，及其子节点
     */
    mvUpRowNode: function (obj) {
        var treeTable = $("#treeBody", navTab.getCurrentPanel());
        var $this = $(obj);
        var currTr = $this.closest("tr");
        var currTrPid = currTr.attr("data-tt-parent-id");
        var spPdleft = currTr.find('td:eq(0)>span').css("padding-left");

        //更换tr 显示位置
        var siblings = treeTable.find('tr[data-tt-parent-id=' + currTrPid + ']');
        // 获取该元素 位于父节点下 第几个
        var index = siblings.index(currTr);
        if (index < 1) {
            //alertMsg.warn("当前节点已经是第一个节点，无法上移！");
        } else {
            var prevTr = siblings.eq(index - 1);
            var selectTr = [];
            selectTr.push(currTr[0]);
            currTr.nextAll('tr').each(function () {
                var _this = $(this);
                var nextPid = _this.attr("data-tt-parent-id");
                var neTrSpan = _this.find('td:eq(0)>span').css("padding-left");
                if (nextPid == currTrPid || nextPid == '' || neTrSpan <= spPdleft) {
                    return false;
                } else {
                    selectTr.push(this);
                }
            });
            //将本节点移动到指定位置
            prevTr.before(selectTr);
        }
        ct002.serializIdText();
    },
    /**
     * 下移整个节点，及其子节点
     */
    mvDownRowNode: function (obj) {
        var treeTable = $("#treeBody", navTab.getCurrentPanel());
        var $this = $(obj);
        var currTr = $this.closest("tr");
        var currTrPid = currTr.attr("data-tt-parent-id");
        var spPdleft = currTr.find('td:eq(0)>span').css("padding-left");

        //获取同级所有节点 集
        var siblings = treeTable.find('tr[data-tt-parent-id=' + currTrPid + ']');
        // 获取该元素 位于父节点下 位置
        var index = siblings.index(currTr);
        if (index == siblings.length - 1) {
            alertMsg.warn("当前节点是最后一个节点，无法下移！");
        } else {
            var nextTr = siblings.eq(index + 1);
            var selectTr = [];
            selectTr.push(nextTr[0]);
            nextTr.nextAll('tr').each(function () {
                var _this = $(this);
                var nextPid = _this.attr("data-tt-parent-id");
                var neTrSpan = _this.find('td:eq(0)>span').css("padding-left");
                if (nextPid == currTrPid || nextPid == '' || neTrSpan <= spPdleft) {
                    return false;
                } else {
                    selectTr.push(this);
                }
            });
            //将本节点移动到指定位置
            currTr.before(selectTr);
        }
        ct002.serializIdText();
    },
    /**
     * 升级当前节点
     */
    upLevel: function (obj) {
        var $this = $(obj);
        var currTr = $this.closest("tr");
        var currTrId = currTr.attr('data-tt-id');
        var currTrPid = currTr.attr('data-tt-parent-id');
        var spPdleft = currTr.find('td:eq(0)>span').css("padding-left");

        if (currTrPid == '0') {
            alertMsg.warn("当前节点无法升级！");
        } else {
            var currTrParParId = $('tr[data-tt-id=' + currTrPid + ']', navTab.getCurrentPanel()).attr('data-tt-parent-id');
            //1、升级之前 的父级节点的 下一个兄弟节点
            var oldParent = $('tr[data-tt-id=' + currTrPid + ']', navTab.getCurrentPanel());
            var oldParNext = oldParent.nextAll('tr[data-tt-parent-id=' + oldParent.attr('data-tt-parent-id') + ']:eq(0)');

            //升级 相当于 将节点移动到 他上级的上级。
            $("#treeBody", navTab.getCurrentPanel()).treetable("move", currTrId, currTrParParId);
            //将移动之后的 节点 父id值 改为 移动之后的 pid
            currTr.attr('data-tt-parent-id', currTrParParId);
            $("input[name=subId]", currTr).val(currTrId);
            $("input[name=upId]", currTr).val(currTrParParId);

            //取 原来父节点下还有没有节点，没有就将父节点前 的 折叠箭头去掉。
            var oldPar = $('tr[data-tt-parent-id=' + currTrPid + ']', navTab.getCurrentPanel());
            if (oldPar.length == '0') {
                $('tr[data-tt-id=' + currTrPid + ']', navTab.getCurrentPanel()).find('td:eq(0)').find('span>a').remove();
            }

            /**
             * 升级之后 调整 tr位置
             * //1、升级之后的父级节点
             * //2、升级之后的父级的下一个兄弟节点
             * //3、升级之后的当前节点
             * //4、升级之后的 当前节点的 下一个兄弟节点
             */
            var selectTr = [];
            var parent = $('tr[data-tt-id=' + currTrParParId + ']', navTab.getCurrentPanel());
            var parentNext = parent.nextAll('tr[data-tt-parent-id=' + parent.attr('data-tt-parent-id') + ']:eq(0)');
            var currNext = currTr.nextAll('tr[data-tt-parent-id=' + currTr.attr('data-tt-parent-id') + ']:eq(0)');
            selectTr.push(currNext[0]);
            // 拿到 当前节点下一个兄弟节点下的所有tr，上移到 当前节点之前
            currNext.nextAll('tr').each(function () {
                var _this = $(this);
                var nextId = _this.attr('data-tt-id');
                var nextPid = _this.attr('data-tt-parent-id');
                var nextTrSpan = _this.find('td:eq(0)>span').css("padding-left");
                if ((parentNext && nextPid == parentNext.attr('data-tt-parent-id')) || nextPid == '' || nextTrSpan < currTr.find('td:eq(0)>span').css("padding-left")) {
                    return false;
                } else {
                    if (nextId == oldParNext.attr('data-tt-id')) {
                        return false;
                    } else {
                        selectTr.push(this);
                    }
                }
            });
            currTr.before(selectTr);

        }
        ct002.serializIdText();
    },
    /**
     * 降级 当前节点
     */
    downLevel: function (obj) {
        var $this = $(obj);
        var currTr = $this.closest("tr");
        var currTrId = currTr.attr('data-tt-id');
        var currTrPid = currTr.attr('data-tt-parent-id');
        var prevTr = currTr.prevAll('tr[data-tt-parent-id=' + currTrPid + ']:visible:first');
        if (!prevTr || prevTr.length == 0) {
            alertMsg.warn("当前节点无法降级！");
        } else {
            /**
             * 在升级之前 获取 升级之后要调整的 tr
             */
            //获取 当前节点 之前的 所有下级节点   上移到  升级后的当前节点之前
            var selectTr = [];
            prevTr.nextAll('tr').each(function () {
                var _this = $(this);
                var _thisId = _this.attr('data-tt-id');
                //从父节点的父节点开始找，碰到当前节点就结束
                if (_thisId == currTrId) {
                    return false;
                } else {
                    selectTr.push(this);
                }
            });


            var mvToId = prevTr.attr('data-tt-id');
            $("#treeBody", navTab.getCurrentPanel()).treetable("move", currTrId, mvToId);
            currTr.attr('data-tt-parent-id', mvToId);
            $("input[name=subId]", currTr).val(currTrId);
            $("input[name=upId]", currTr).val(mvToId);

            //自动降级 之后 调整 tr位置
            currTr.before(selectTr);
        }
        ct002.serializIdText();
    },
    /**
     * 处理 每个节点展示 文本
     */
    serializIdText: function () {
        var treeTable = $("#treeBody", navTab.getCurrentPanel());
        var treeTrs = treeTable.find("tr[data-tt-id][data-tt-parent-id]:visible");
        var trLength = treeTrs.length;
        var idMap = new Map();
        var pidMap = new Map();
        //loop all tr construct two Map;
        treeTrs.each(function () {
            var _oneTr = $(this);
            var oneTrId = _oneTr.attr("data-tt-id");
            var oneTrPid = _oneTr.attr("data-tt-parent-id");
            idMap.put(oneTrId, _oneTr);
            var _oneTrChild = pidMap.get(oneTrPid);
            if (!_oneTrChild) {
                _oneTrChild = [];
                pidMap.put(oneTrPid, _oneTrChild);
            }
            _oneTrChild.push(_oneTr);
            _oneTrChild = null;
        });

        //loop all tr set tr text
        treeTrs.each(function (index, element) {
            var cTr = $(this);
            var cTrId = cTr.attr("data-tt-id");
            var cTrPid = cTr.attr("data-tt-parent-id");
            var childArr = pidMap.get(cTrPid);

            var childIdx = ct002.objInArray(cTrId, childArr);
            var pareText = "";
            //获取当前元素位于数组第几个，set tr text
            if (cTrPid != '0') {
                var parentTr = idMap.get(cTrPid);
                pareText = parentTr.find("td:eq(0)").find("label").text();
            }
            var currText = "";
            if (pareText != "") {
                pareText = pareText + "-";
            }
            currText = pareText + (childIdx + 1);
            cTr.find("td:eq(0)").find("label").text(currText);
        });
    },
    objInArray: function (id, array) {
        for (var i = 0; i < array.length; i++) {
            var obj1 = array[i];
            if (id == obj1.attr("data-tt-id")) {
                return i;
            }
        }
        return -1;
    },

    addRow: function (divId, obj) {
        var obj = table.addRow(divId, obj);
        $(obj).find("input[name='valueCd']").addClass("required");
        $(obj).find("input[name='valueName']").addClass("required");
    },

    /**
     * 保存预分分类类型
     */
    saveSchemeTypeDef: function () {
        var ct002SchemeTypeDefForm = $("#ct002SchemeTypeDefForm", navTab.getCurrentPanel());
        if (ct002SchemeTypeDefForm.valid()) {
            var url = getGlobalPathRoot() + "eland/ct/ct002/ct002-saveSchemeTypeDef.gv";
            var packet = new AJAXPacket(url);
            packet.data.data = ct002SchemeTypeDefForm.serializeArray();
            packet.data.data.push({name: "prjCd", value: getPrjCd()});
            // 获取中金额
            var totalMoney = $("input[name='totalMoneyRadio']:checked", ct002SchemeTypeDefForm)
                .closest("tr").find("input[name=valueCd]").val();
            if (!totalMoney) {
                totalMoney = "";
            }
            packet.data.data.push({name: "totalMoney", value: totalMoney});
            // 提交服务进行保存
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    }
};

$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("ct002LeftDiv", "ct002Context", {});
});