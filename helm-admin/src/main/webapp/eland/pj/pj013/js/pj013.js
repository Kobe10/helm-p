var pj013 = {
    openAdd: function () {
        var url = getGlobalPathRoot() + "eland/pj/pj013/pj013-info.gv?method=add" +
            "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj01301", "项目资料", {mask: true, max: true});
    },
    openEdit: function (docId) {
        var url = getGlobalPathRoot() + "eland/pj/pj013/pj013-info.gv?method=edit&docId=" + docId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj01301", "项目资料", {mask: true, max: true});
    },
    openView: function (docId) {
        var url = getGlobalPathRoot() + "eland/pj/pj013/pj013-info.gv?method=view&docId=" + docId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj01301", "项目资料", {mask: true, max: true});
    },

    save: function () {
        var $form = $("#pj01301frm", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/pj/pj013/pj013-save.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },

    //获取组织
    editOrg: function (obj) {
        var url = getGlobalPathRoot() + "eland/pj/pj003/pj003-initTree.gv";
        var width = $(obj).width() - 250;
        $(obj).openTip({href: url, height: "230",
            width: "200", offsetX: -23, offsetY: 31});
    },

    //关键字自动显示
    changeKeyWord: function (obj) {
        var changeTd = $(obj).parent();
        var keyWord = $(obj).val();
        if (!/^\s*$/.test(keyWord)) {
            var newKey = $("label.js_keyword", changeTd).first().clone();
            $("span.in-block", newKey).val("");
            newKey.appendTo(changeTd.last("label"));
            $("span.in-block", newKey).html(keyWord);
            $("label.js_keyword", changeTd).last().show();
            $("input[name=keyWord]", newKey).val(keyWord);
        } else {
            alert("关键字不可以为空！");
        }
    },

    //自动补全资料名称
    getUrl: function (obj) {
        return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?subSvcName=pj013Data";
    },
    getOption: function (obj) {
        var oldDocId = $("input[name=documentId]").val();
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].DOC_NAME;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                if (data.DOC_ID != oldDocId) {
                    return data.DOC_NAME;
                }
            },
            mustMatch: true,
            remoteDataType: "json",
            autoFill: true,
            onItemSelect: function (obj) {
                $("input[name=relBDocId]", $(obj.source).parent()).val(obj.data.DOC_ID);
            }
        }
    }

};