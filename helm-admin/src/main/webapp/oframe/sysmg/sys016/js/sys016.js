sys016 = {
    uploadObj: null,
    /**
     * 打开快速检索对话框
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#sys016create", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#sys016create input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 调用通用查询组件
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        sys016.closeQSch(true);
        Page.queryCondition(obj, {
            changeConditions: sys016.changeConditions,
            width: "100%",
            formObj: $("#sys016form", navTab.getCurrentPanel())
        });
    },
    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#sys016create", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = true;
            } else {
                close = false;
            }
        }
        if (close) {
            $("#sys016create", navTab.getCurrentPanel()).hide();
        }
    },
    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#sys016form', navTab.getCurrentPanel());
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
     * 导出实体
     */
    exportEntity: function () {
        var Ids = [];
        var prjCds = [];
        $("#sys016List", navTab.getCurrentPanel()).find(":checkbox[checked][name=componetId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var prjCd = $("input[name=prjCd]", temp).val();
                var id = $("input[name=componentNameEn]", temp).val();
                if (prjCd == null || prjCd == "") {
                    prjCd = "0";
                }
                if (id == null || id == "") {
                    id = "0";
                }
                Ids.push(id);
                prjCds.push(prjCd);
            });
        if (Ids.length == 0) {
            //默认全部导出
            var entityName = $("input[name=entityName]", navTab.getCurrentPanel()).val();
            var url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-export.gv?prjCd=" + getPrjCd() + "&entityName=" + entityName;
            window.open(url);
        } else {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-batchExport.gv?prjCds=" + prjCds.toString() + "&Ids=" + Ids.toString();
            window.open(url);
        }
    },
    /**
     * 批量删除组件
     * @param componetId
     */
    batchDeleteView: function () {
        var Ids = [];//comId
        var prjCds = [];
        var enames = [];//组件英文名称
        $("#sys016List", navTab.getCurrentPanel()).find(":checkbox[checked][name=componetId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var componentNameEn = $("input[name=componentNameEn]", temp).val();
                var prjCd = $("input[name=prjCd]", temp).val();
                if (prjCd == null || prjCd == "") {
                    prjCd = "0";
                }
                Ids.push($(this).val());
                prjCds.push(prjCd);
                enames.push(componentNameEn);
            }
        );
        if (Ids.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("您确定要删除记录吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-deleteView.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("componetId", Ids.toString());
                    ajaxPacket.data.add("prjCds", prjCds.toString());
                    ajaxPacket.data.add("enames", enames.toString());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            sys016.query();
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
        stopEvent(event);
    },
    /**
     * 打开添加页面
     */
    editView: function (componetId, para, obj) {
        var temp = $(obj).closest("tr");
        var componentNameEn = $("input[name=componentNameEn]", temp).val();
        var ComPrjCd = $("input[name=ComPrjCd]", temp).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-editView.gv?componetId=" +
            componetId + "&para=" + para + "&componentNameEn=" + componentNameEn + "&ComPrjCd=" + ComPrjCd;
        $.pdialog.open(url, "sys016", "组件定义",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 480}
        );
        stopEvent(event);
    },
    /**
     * 删除组件
     * @param componetId
     */
    deleteView: function (prjCd, componentNameEn, componetId) {
        alertMsg.confirm("您确定要删除记录吗?", {
            okCall: function () {
                //var prjCd = obj.closest("td").prev().text();
                //var ename = obj.closest("td").prev().prev().text();
                var url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-deleteView.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCds", prjCd);
                ajaxPacket.data.add("enames", componentNameEn);
                ajaxPacket.data.add("componetId", componetId);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        sys016.query();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
        stopEvent(event);
    },

    /**
     * 添加
     */
    editJob: function () {
        var componetId = $("input[name=componetId]", $.pdialog.getCurrent()).val();
        var componentName = $("input[name=componentName]", $.pdialog.getCurrent()).val();
        var componentNameEn = $("input[name=componentNameEn]", $.pdialog.getCurrent()).val();
        var prjCd = $("select[name=prjCd]", $.pdialog.getCurrent()).val();
        var componentNameCh = $("input[name=componentNameCh]", $.pdialog.getCurrent()).val();
        var entityName = $("input[name=entityName]", $.pdialog.getCurrent()).val();
        var executeBeforeRule = $("input[name=executeBeforeRule]", $.pdialog.getCurrent()).val();
        var executeAfterRule = $("input[name=executeAfterRule]", $.pdialog.getCurrent()).val();
        var oldComponentNameEn = $("input[name=oldComponentNameEn]", $.pdialog.getCurrent()).val();
        var oldPrjCd = $("input[name=oldPrjCd]", $.pdialog.getCurrent()).val();
        var para = $("input[name=para]", $.pdialog.getCurrent()).val();
        var componentNote = $("textarea[name=componentNote]", $.pdialog.getCurrent()).val();
        if (componentName == "") {
            alertMsg.warn("组件名称必须输入");
            return;
        }
        if (componentNameEn == "") {
            alertMsg.warn("组件实现必须输入");
            return;
        }
        if (para == "1") {//表示进行的是复制操作
            if ((oldComponentNameEn == componentNameEn && oldPrjCd == prjCd)) {
                alertMsg.warn("请修改至少一项项目编号或者组件名称");
                return;
            }
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-add.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("componetId", componetId);
        packet.data.add("componentName", componentName);
        packet.data.add("componentNameEn", componentNameEn);
        packet.data.add("prjCd", prjCd);
        packet.data.add("componentNameCh", componentNameCh);
        packet.data.add("entityName", entityName);
        packet.data.add("executeBeforeRule", executeBeforeRule);
        packet.data.add("executeAfterRule", executeAfterRule);
        packet.data.add("componentNote", componentNote);
        packet.data.add("oldComponentNameEn", oldComponentNameEn);
        packet.data.add("oldPrjCd", oldPrjCd);
        packet.data.add("para", para);
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                if (componetId != "" && componetId != null) {
                    alertMsg.correct("保存组件成功");
                } else {
                    alertMsg.correct("添加组件成功！");
                }
                sys016.query();
            } else {
                alertMsg.error(data.errMsg);
            }
            $.pdialog.closeCurrent();
        });
    },

    /**
     * 查询
     */
    query: function () {
        var formObj = $("#sys016form", navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#sys016create", navTab.getCurrentPanel());
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
        Page.query($("#sys016form"), "");
    },

    /**
     * 查看历史
     */
    queryLog: function (obj, jobTaskId) {
        var $_this = $(obj);
        var oldhistory = $_this.next("#history");
        if (oldhistory.length > 0) {
            oldhistory.remove();
            return
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-query.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("jobTaskId", jobTaskId);
        core.ajax.sendPacketHtml(packet, function (response) {
            var tdLength = $_this.children().length;
            var history = $("#history", navTab.getCurrentPanel());
            if (history.length > 0) {
                history.remove();
            }
            var htm = "<tr id='history'><td  colspan='" + tdLength + "'>" + response + "</td></tr>";
            $_this.after(htm);
            initUI($("#history", navTab.getCurrentPanel()));
        });
    },
    /**
     * 导入配置
     */
    startImport: function (obj) {
        var clickFileBtn = $(obj).parent().find("input[type=file]");
        clickFileBtn.attr("id", new Date().getTime());
        clickFileBtn.val("");
        clickFileBtn.unbind("change", sys016.importXml);
        clickFileBtn.bind("change", sys016.importXml);
        sys016.uploadObj = $(obj);
    },
    /**
     * 改变导入文件
     */
    //clickInput:function () {
    //    $("#importComponent", navTab.getCurrentPanel()).val("");
    //    $("#importComponent", navTab.getCurrentPanel()).unbind("change", sys016.importXml);
    //    $("#importComponent", navTab.getCurrentPanel()).bind("change", sys016.importXml);
    //    $("#importComponent", navTab.getCurrentPanel()).click();
    //},
//导入配置参数
    importXml: function () {
        var uploadURL = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-saveImportData.gv?prjCd=" + getPrjCd();
        var importComponent = sys016.uploadObj.parent().find("input[type=file]").attr("id");
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: importComponent,
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.isSuccess) {
                        alertMsg.correct("处理成功");
                        var res = data.resultXml;
                        if (res != "" && res != "null") {
                            var packet = new AJAXPacket();
                            packet.data.add("res", res);
                            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys016/sys016-initImportResult.gv";
                            core.ajax.sendPacketHtml(packet, function (response) {
                                $("#sys016_page_data", navTab.getCurrentPanel()).html(response);
                                initUI($("#sys016_page_data", navTab.getCurrentPanel()));
                            });
                        } else {
                        }
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }

            }
        )
    },
    /**
     * 点击移除历史
     * @param obj
     */
    removeHistory: function (obj) {
        var history = $("#history", navTab.getCurrentPanel());
        history.remove();
    }
};

$(document).ready(function () {
    // 执行查询
    sys016.query();
});
