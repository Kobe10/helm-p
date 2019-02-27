wf002 = {
    /**
     * Excel 导入 Person
     */
    doDeploy: function () {
        var wf002Frm = $("#wf002Frm", navTab.getCurrentPanel());
        if (!wf002Frm.valid()) {
            return;
        }
        var wfFile = $("#wfFile", navTab.getCurrentPanel()).val();
        if (wfFile == "") {
            alertMsg.warn("请选择导入的文件");
            return;
        }
        var uploadURL = getGlobalPathRoot() + "oframe/wf/wf002/wf002-deploy.gv?prjCd=" + getPrjCd();
        $.ajaxFileUpload({
                url: uploadURL,
                data: {
                    deployName: $("input[name=deployName]", wf002Frm).val(),
                    procDepCategory: $("input[name=procDepCategory]", wf002Frm).val()
                },
                secureuri: false,
                fileElementId: "wfFile",
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.success) {
                        alertMsg.correct("处理成功");
                        $("input,textarea,select", wf002Frm).val("");
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }
            }
        );
    },

    /*查询流程定义详情*/
    findProcDetail: function (procDefKey) {
        var url = getGlobalPathRoot() + "oframe/wf/wf002/wf002-findProcDetail.gv?prjCd=" + getPrjCd() + "&procDefKey=" + procDefKey;
        $.pdialog.open(url, "wf002", "流程定义详情", {max: false, mask: true, width: 800, height: 350});
    }
};