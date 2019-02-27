var ct006 = {

    /**
     * 居民详情页面
     * @param hsId
     */
    viewHouse: function (oldHsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + oldHsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },


    /**
     * 检索房产
     * @param obj
     */
    schHs: function () {
        var container = $("#ct006SchDiv", navTab.getCurrentPanel());
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var ct006QueryForm = $("#ct006QueryForm_" + schemeType, navTab.getCurrentPanel());
        var ctStatusField = $("input[name=ctStatusField]", navTab.getCurrentPanel()).val();

        // 查询条件拼接
        var conditionNameArr = new Array();
        var conditionArr = new Array();
        var conditionValueArr = new Array();

        // 增加必须增加的数据条件
        var conditionNameTemp = $("input.js_conditionNames", ct006QueryForm).val().split(",");
        var conditionTemp = $("input.js_conditions", ct006QueryForm).val().split(",");
        var conditionValueTemp = $("input.js_conditionValues", ct006QueryForm).val().split(",");
        for (var i = 0; i < conditionNameTemp.length; i++) {
            if (i < conditionTemp.length && i < conditionValueTemp.length) {
                var tempName = conditionNameTemp[i];
                var tempCond = conditionTemp[i];
                var tempValue = conditionValueTemp[i];
                if (tempName != "" && tempCond != "" && tempValue != "") {
                    conditionNameArr.push(tempName);
                    conditionArr.push(tempCond);
                    conditionValueArr.push(tempValue);
                }
            }
        }
        // 查询条件
        var schType = $("select[name=schType]", container).val();
        var schValue = $("input[name=schValue]", container).val();
        conditionNameArr.push(schType);
        conditionArr.push("like");
        conditionValueArr.push(schValue);
        // 签约状态
        var ctStatus = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=ct006AccountStatus]").each(
            function (i) {
                var value = $(this).val();
                if (value == "1") {
                    // 未签约
                    value = "2|3";
                } else {
                    // 已签约
                    value = "4";
                }

                ctStatus.push(value);
            }
        );
        if (ctStatus.length > 0) {
            conditionNameArr.push("NewHsInfo.handleStatus");//zyx
            conditionArr.push("in");
            conditionValueArr.push(ctStatus.join("|"));
        }
        $("input[name=conditionName]", ct006QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", ct006QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", ct006QueryForm).val(conditionValueArr.join(","));
        // 查询处理结果
        Page.query(ct006QueryForm, "");
    },

    /**
     * 结算信息
     * @param hsId
     */
    ctInfo: function (oldHsId, newHsId) {
        ct006.ctInfo(oldHsId, newHsId, '');
    },

    /**
     * 结算信息
     * @param hsId
     */
    ctInfo: function (oldHsId, newHsId, opFlag) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("oldHsId", oldHsId);
        packet.data.add("newHsId", newHsId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("opFlag", opFlag);
        packet.url = getGlobalPathRoot() + "eland/ct/ct006/ct006-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct006Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct006Context", navTab.getCurrentPanel()));
        });
    },


    /**
     * 生成业务报表
     * @param formId
     * @param templateName
     */
    generateCt: function (oldHsId, hsCtId, newHsId) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct006/ct006-generateCt.gv?fromOp=view";
        var packet = new AJAXPacket(url);
        packet.data.add("oldHsId", oldHsId);
        packet.data.add("newHsId", newHsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#ct006_ReportData", navTab.getCurrentPanel());
                container.html(response);
                container.initUI();
            }
        );
    },


    /**
     * 确认打印协议， 或者确认打印回执单。
     * @param printObj 协议 or 回执单
     */
    printCt: function (obj, printObj, hsCtId) {
        try {
            var iframeDoc = document.getElementById("ct006DocIFrame").contentWindow.document;
            iframeDoc.getElementById("PageOfficeCtrl1").ShowDialog(4);
        } catch (e) {
            alertMsg.error("控件加载失败无法打印,请点击连接查看文档进行打印!");
        }
    },

    /**
     * 生成业务报表
     * @param formId
     * @param templateName
     */
    downloadCt: function (oldHsId, hsCtId, newHsId) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct006/ct006-generateCt.gv?fromOp=download&oldHsId=" + oldHsId
            + "&newHsId=" + newHsId
            + "&schemeType=" + schemeType
            + "&hsCtId=" + hsCtId
            + "&prjCd=" + getPrjCd();
        window.open(url);
    },

    /**
     * 确认签约
     * @param hsCtId 签约编号
     * @param hsId 关联房屋编号
     * @param newHsId: 新房源编号
     */
    cfmCt: function (hsCtId, oldHsId, newHsId) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct006/ct006-cfmCt.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("oldHsId", oldHsId);
        packet.data.add("newHsId", newHsId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("prjCd", getPrjCd());
        // 执行确认签约处理
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("确认签约处理成功！");
                ct006.ctInfo(oldHsId, newHsId);
            } else {
                alertMsg.warn(data.errMsg);
            }
        }, true);
        $.pdialog.close($.pdialog._current);
    },

    /**
     * 取消签约
     * @param hsCtId 签约编号
     * @param hsId 关联房屋编号
     * @param newHsId: 新房源编号
     */
    cancelCt: function (hsCtId, oldHsId, newHsId) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        alertMsg.confirm("是否取消签约？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/ct/ct006/ct006-cancelCt.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("oldHsId", oldHsId);
                packet.data.add("newHsId", newHsId);
                packet.data.add("hsCtId", hsCtId);
                packet.data.add("schemeType", schemeType);
                packet.data.add("prjCd", getPrjCd());
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        alertMsg.correct("签约取消成功！");
                        ct006.ctInfo(oldHsId, newHsId);
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }
        });
    },


    /**
     * 房屋信息
     * @param oldHsId
     */
    openHs: function (oldHsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + oldHsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },


    /** 协议附件信息管理 **/
    //查看 附件
    viewPic: function (docIds) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("docIds", docIds);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            navTab.getCurrentPanel().append($(response));
            FileViewer.closeFunc = function () {
                // 触发加载
                ct006.closedDoc();
            };
        });
    },

    //浏览所有附件的方法
    showFjFile: function (obj) {
        var $this = $(obj);
        var docIdTemp = $this.closest("div.js_ct006_fj_file").find("input[name=docIds]").val();
        if ("" == docIdTemp) {
            return;
        }
        //展示所有的附件。
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("docIds", docIdTemp);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var obj = $(response);
            navTab.getCurrentPanel().append($(response));
            FileViewer.closeFunc = function () {
                // 触发加载
                ct006.closedDoc();
            };
        });
    },

    //上传附件
    uploadFjFile: function (clickObj) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        var oldHsId = $("input[name=oldHsId]", navTab.getCurrentPanel()).val();
        var newHsId = $("input[name=newHsId]", navTab.getCurrentPanel()).val();
        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));
        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        //
        file.getDocTypeRelUrl = ct00502.getDocTypeRelUrl;
        var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName +
            "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=100&relId=" + oldHsId;
        $.pdialog.open(url, "file", "居民结算签字协议上传", {
            height: 600, width: 800, mask: true,
            close: editAble ? ct00502.closedDoc : "",
            param: {clickSpan: $span, oldHsId: oldHsId, newHsId: newHsId}
        });
    },

    closedDoc: function (param) {
        //关闭前刷新本页
        ct006.photograph(param.oldHsId, param.newHsId);
        return file.closeD();
    },

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj) {
        var $span = $(clickObj);
        var oldHsId = $("input[name=oldHsId]", navTab.getCurrentPanel()).val();
        var newHsId = $("input[name=newHsId]", navTab.getCurrentPanel()).val();
        var docIds = $span.find("input[type=hidden]").val();
        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));

        var relType = $span.attr("relType");

        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        // 打开附近上传对话框
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
            + "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=" + relType + "&relId=" + oldHsId;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            close: ct006.docClosed,
            param: {clickSpan: $span, oldHsId: oldHsId, newHsId: newHsId}
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
        //处理上传附件，展示
        var currentDiv = $span.closest("div.js_ct006_fj_file");
        var oldDocIds = $span.find("input[name=docIds]").val();
        var oldDocIdArr = [];
        oldDocIdArr = oldDocIds.split(",");
        var docIdArr = [];
        for (var i = 0; i < uploadedFiles.length; i++) {
            //保存已经上传的docId
            var uploadedFile = uploadedFiles[i];
            docIdArr.push(uploadedFile.docId);
        }
        $span.find("input[name=docIds]").val(docIdArr.join(","));
        ct006.ctInfo(param.oldHsId, param.newHsId, 'viewFj');
        // 调用文件的关闭
        return file.closeD();
    }
};

$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("ct006SchDiv", "ct006Context", {});
});
