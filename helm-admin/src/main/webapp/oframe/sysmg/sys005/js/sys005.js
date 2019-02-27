sys005 = {
    openAdd: function () {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-info.gv?prjCd=" + getPrjCd() + "&method=add";
        $.pdialog.open(url, "sys00501", "添加参数", {mask: true, max: true});
    },
    openEdit: function (itemCd,cfgPrjCd) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-info.gv?prjCd=" + getPrjCd() + "&method=edit&itemCd=" + itemCd + "&cfgPrjCd=" + cfgPrjCd;
        $.pdialog.open(url, "sys00501", "修改参数", {mask: true, max: true});
    },
    openView: function (itemCd,cfgPrjCd) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-info.gv?prjCd=" + getPrjCd() + "&method=view&itemCd=" + itemCd + "&cfgPrjCd=" + cfgPrjCd;
        $.pdialog.open(url, "sys00501", "查看参数", {mask: true, max: true});
    },
    addRow: function (divId, obj) {
        var obj = table.addRow(divId, obj);
        $(obj).find("input[name='valueCd']").addClass("required");
        $(obj).find("input[name='valueName']").addClass("required");
    },

  /**
   * 批量删除
   * @param
   */
  batchDel: function (obj) {
        var itemCds = [];
        var cfgPrjCds = [];
        $(obj).closest("div.js_sys005").find(":checkbox[checked][name=itemCd]").each(
          function (i) {
              itemCds.push($(this).val());
              cfgPrjCds.push($(this).attr("cfgPrjCd"));
          }
        );
        if (itemCds.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("删除该记录可能导致系统错误，你确定要删除选中配置数据吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-delete.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("itemCd", itemCds.join(","));
                    ajaxPacket.data.add("cfgPrjCd", cfgPrjCds.join(","));
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
    },

    deleteCfg: function (itemCd,cfgPrjCd) {
        alertMsg.confirm("删除该记录可能导致系统错误，你确定要删除该配置数据吗?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-delete.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("itemCd", itemCd);
                ajaxPacket.data.add("cfgPrjCd", cfgPrjCd);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
    },
    //导出xml
    exportXml: function (obj) {
        var itemCds = [];
        var cfgPrjCds = [];
        $(obj).closest("div.js_sys005").find(":checkbox[checked][name=itemCd]").each(
            function (i) {
                itemCds.push($(this).val());
                cfgPrjCds.push($(this).attr("cfgPrjCd"));
            }
        );
        if (itemCds.length == 0) {
            alertMsg.warn("请选择要导出的记录");
        } else {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-exportData.gv";
            var ajaxPacket = new AJAXPacket(url, "数据导出处理中，请稍候......");
            ajaxPacket.data.add("cfgCds", itemCds.toString());
            ajaxPacket.data.add("cfgPrjCds", cfgPrjCds.toString());
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    var downloadUrl = getGlobalPathRoot() + "oframe/common/file/file-down.gv?prjCd=" + getPrjCd();
                    downloadUrl = downloadUrl + "&remoteFile=" + encodeURI(jsonData.data.remoteFile);
                    downloadUrl = downloadUrl + "&clientFile=" + encodeURI(jsonData.data.clientFile);
                    window.open(downloadUrl, "_self");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
            ajaxPacket = null;
        }
    },

    //导入配置参数
    importXml: function () {
        alertMsg.confirm("导入文件中的项目编码会替换为当前项目编码，你确定要导入?", {
            okCall: function () {
                var uploadURL = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-saveImportData.gv?prjCd=" + getPrjCd();
                $.ajaxFileUpload({
                        url: uploadURL,
                        secureuri: false,
                        fileElementId: "importSysCfg",
                        dataType: 'json',
                        success: function (data, status, fileElementId) {
                            if (data.isSuccess) {
                                alertMsg.correct("处理成功");
                                $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                            } else {
                                alertMsg.error(data.errMsg);
                            }
                        }
                    }
                )
            }
        });
    },

    /**
     * 导入项目参数
     */
    startImport: function () {
        $("#sys005ImportForm", navTab.getCurrentPanel())[0].reset();
        $("#importSysCfg", navTab.getCurrentPanel()).unbind("change", sys005.importXml);
        $("#importSysCfg", navTab.getCurrentPanel()).bind("change", sys005.importXml);
    },

    

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#sys005create", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = true;
            } else {
                close = false;
            }
        }
        if (close) {
            $("#sys005create", navTab.getCurrentPanel()).hide();
        }
    },

    /**
     * 打开快速检索对话框
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#sys005create", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#sys005create input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    }



};
$(document).ready(function () {
    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
});
