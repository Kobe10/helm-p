var ph010 = {
    /**
     * 删除页面
     * @param camId
     */
    delectView: function (camId) {
        alertMsg.confirm("您确定要删除该摄像头信息?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-delectView.gv?prjCd=" + getPrjCd() + "&camId=" + camId;
                var packet = new AJAXPacket(url);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("删除成功");
                        ph010.init();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 保存页面
     * @param camId
     */
    editView: function (camId) {
        var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-editView.gv?prjCd=" + getPrjCd() + "&camId=" + camId;
        $.pdialog.open(url, "ph010", "摄像头信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 400}
        );

    },

    /**
     * 导入页面
     * @param camId
     */
    importView: function (camId) {
        var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-openImport.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph003_be", "导入摄像头",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 400}
        );

    },

    init: function () {
        var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-init.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        core.ajax.sendPacketHtml(packet, function (response) {
            $(".js_ph001", navTab.getCurrentPanel()).html(response);
            initUI($(".js_ph001", navTab.getCurrentPanel()));
        });
    },
    /**
     * 保存
     */
    saveMP: function () {
        var camId = $("input[name=camId]").val();
        var camCd = $("input[name=camCd]").val();
        var channelCd = $("input[name=channelCd]").val();
        var camAddr = $("input[name=camAddr]").val();
        var accessAddr = $("input[name=accessAddr]").val();
        var markPos = $("input[name=markPos]").val();
        var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-saveMP.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("camId", camId);
        packet.data.add("camCd", camCd);
        packet.data.add("channelCd", channelCd);
        packet.data.add("camAddr", camAddr);
        packet.data.add("accessAddr", accessAddr);
        packet.data.add("markPos", markPos);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("保存成功");
                ph010.init();
                $.pdialog.closeCurrent();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {changeConditions: ph010.changeConditions, width: "100%"});
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#ph010queryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        ph010.query();
    },

    query: function () {
        var formObj = $('#ph010queryForm', navTab.getCurrentPanel());
        Page.query(formObj, "");
    },

    /**
     * 点击开始上传文件
     */
    startImport: function () {
        $("#uploadFile", $.pdialog.getCurrent()).val("");
        $("#uploadFile", $.pdialog.getCurrent()).unbind("change", ph010.importFile);
        $("#uploadFile", $.pdialog.getCurrent()).bind("change", ph010.importFile);
    },

    /**
     * Excel 导入 Person
     */
    importFile: function (obj) {
        var uploadURL = getGlobalPathRoot() + "eland/ph/ph010/ph010-imput.gv?prjCd=" + getPrjCd();
        //var docNamePath = "录像";
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: "uploadFile",
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.isSuccess) {
                        alertMsg.correct("导入成功");
                        ph010.init();
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }
            }
        )
    },

    //设置摄像头位置
    setCamPos: function (obj, camId) {
        var $this = $(obj);
        var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-setCamPos.gv?prjCd=" + getPrjCd() + "&camId=" + camId;
        $.pdialog.open(url, "ph010", "摄像头标记", {
            mask: true, max: true
            //close: ph010.closeCamMap,
            //param: {clickSpan: $this}
        });
    }
};
$(document).ready(function () {
    ph010.query();
});
