sys018 = {
    uploadObj: null,
    /**
     * 打开快速检索对话框
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#sys018create", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#sys018create input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 查询
     */
    query: function () {
        var formObj = $("#sys018form", navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#sys018create", navTab.getCurrentPanel());
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
        Page.query($("#sys018form", navTab.getCurrentPanel()), "");
    },

    /**
     * 打开添加页面
     */
    editView: function (jobId, para, obj) {
        var temp = $(obj).closest("tr");
        var jobCd = $("input[name=jobCd]", temp).val();
        var prjCd = $("input[name=prjCd]", temp).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys018/sys018-editView.gv?jobId=" +
            jobId + "&para=" + para + "&jobCd=" + jobCd + "&prjCd=" + prjCd;
        $.pdialog.open(url, "sys018", "指标编辑",
            {mask: true, max: true, maxable: true, resizable: true, width: 800, height: 480}
        );
        stopEvent(event);
    },

    /**
     * 删除组件
     * @param componetId
     */
    deleteView: function (prjCd, jobCd, jobId) {
        alertMsg.confirm("您确定要删除记录吗?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys018/sys018-deleteView.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCds", prjCd);
                ajaxPacket.data.add("jobCds", jobCd);
                ajaxPacket.data.add("jobId", jobId);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        sys018.query();
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
        // 获取编辑后的规则
        var codeText = window.frames['sys018IFrame'].getCode();
        $("textarea[name=qryParam01]", $.pdialog.getCurrent()).val(codeText);
        var qryParam01 = $("textarea[name=qryParam01]", $.pdialog.getCurrent()).val();

        var jobId = $("input[name=jobId]", $.pdialog.getCurrent()).val();
        var jobCd = $("input[name=jobCd]", $.pdialog.getCurrent()).val();
        var oldJobCd = $("input[name=oldJobCd]", $.pdialog.getCurrent()).val();
        var oldPrjCd = $("input[name=oldPrjCd]", $.pdialog.getCurrent()).val();
        var para = $("input[name=para]", $.pdialog.getCurrent()).val();
        var prjCd = $("select[name=prjCd]", $.pdialog.getCurrent()).val();

        //from提交
        var $form = $("#sys018RuleForm", $.pdialog.getCurrent());

        if ($.trim(jobCd) == "") {
            alertMsg.warn("指标编码必须输入");
            return;
        }
        if (para == "1") {//表示进行的是复制操作
            if ((oldJobCd == jobCd && oldPrjCd == prjCd)) {
                alertMsg.warn("请修改至少一项项目编号或者使用平台");
                return;
            }
        }
        if ($form.valid()) {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys018/sys018-add.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    if (jobId != "" && jobId != null) {
                        alertMsg.correct("修改作业成功");
                    } else {
                        alertMsg.correct("添加作业成功！");
                    }
                    sys018.query();
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(data.errMsg);
                }
            });
        }
    },

    /**
     * 批量删除组件
     * @param componetId
     */
    batchDeleteView: function () {
        var Ids = [];
        var prjCds = [];
        var jobCds = [];
        $("#sys018List", navTab.getCurrentPanel()).find(":checkbox[checked][name=jobId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var jobCd = $("input[name=jobCd]", temp).val();
                var prjCd = $("input[name=prjCd]", temp).val();
                if (prjCd == null || prjCd == "") {
                    prjCd = "0";
                }
                Ids.push($(this).val());
                prjCds.push(prjCd);
                jobCds.push(jobCd);
            }
        );
        if (Ids.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("您确定要删除记录吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys018/sys018-deleteView.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("jobId", Ids.toString());
                    ajaxPacket.data.add("prjCds", prjCds.toString());
                    ajaxPacket.data.add("jobCds", jobCds.toString());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            sys018.query();
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
     * 导出实体
     */
    exportEntity: function () {
        var jobCds = [];
        var prjCds = [];
        $("#sys018List", navTab.getCurrentPanel()).find(":checkbox[checked][name=jobId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var prjCd = $("input[name=prjCd]", temp).val();
                var jobCd = $("input[name=jobCd]", temp).val();
                if (prjCd == null || prjCd == "") {
                    prjCd = "0";
                }
                if (jobCd == null || jobCd == "") {
                    jobCd = "0";
                }
                jobCds.push(jobCd);
                prjCds.push(prjCd);
            });
        if (jobCds.length == 0) {
            //默认全部导出
            var entityName = $("input[name=entityName]", navTab.getCurrentPanel()).val();
            var jobCd = $("input[name=jobCd]", navTab.getCurrentPanel()).val();
            var url = getGlobalPathRoot() + "oframe/sysmg/sys018/sys018-export.gv?prjCd=" + getPrjCd() + "&entityName=" + entityName;
            window.open(url);
        } else {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys018/sys018-batchExport.gv?prjCds=" + prjCds.toString() + "&jobCds=" + jobCds.toString();
            window.open(url);
        }
    },

    /**
     * 导入配置
     */
    startImport: function (obj) {
        var clickFileBtn = $(obj).parent().find("input[type=file]");
        clickFileBtn.attr("id", new Date().getTime());
        clickFileBtn.val("");
        clickFileBtn.unbind("change", sys018.importXml);
        clickFileBtn.bind("change", sys018.importXml);
        sys018.uploadObj = $(obj);
    },

    /**
     * 改变导入文件
     */

//导入配置参数
    importXml: function () {
        var uploadURL = getGlobalPathRoot() + "oframe/sysmg/sys018/sys018-saveImportData.gv?prjCd=" + getPrjCd();
        var importJobFile = sys018.uploadObj.parent().find("input[type=file]").attr("id");
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: importJobFile,
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.isSuccess) {
                        alertMsg.correct("处理成功");
                        Page.query($("#sys018form", navTab.getCurrentPanel()), "");
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }

            }
        )
    },

    /**
     *
     * @returns {*|jQuery}
     */
    getCode: function () {
        return $("textarea[name=qryParam01]", $.pdialog.getCurrent()).val();
    },

    /**
     * 调用通用查询组件
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        sys018.closeQSch(true);
        Page.queryCondition(obj, {
            changeConditions: sys018.changeConditions,
            width: "100%",
            formObj: $("#sys018form", navTab.getCurrentPanel())
        });
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#sys018form', navTab.getCurrentPanel());
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
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#sys018create", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = true;
            } else {
                close = false;
            }
        }
        if (close) {
            $("#sys018create", navTab.getCurrentPanel()).hide();
        }
    }


}
$(document).ready(function () {
    // 执行查询
    sys018.query();
});