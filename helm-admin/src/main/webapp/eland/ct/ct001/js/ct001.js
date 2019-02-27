var ct001 = {
    /**
     * 检索房产
     * @param obj
     */
    schHs: function () {
        var container = $("#ct001SchDiv", navTab.getCurrentPanel());
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var ctStatusField = $("input[name=ctStatusField]", navTab.getCurrentPanel()).val();
        var ct001QueryForm = $("#ct001QueryForm_" + schemeType, navTab.getCurrentPanel());
        // 查询条件拼接
        var conditionNameArr = new Array();
        var conditionArr = new Array();
        var conditionValueArr = new Array();

        // 增加必须增加的数据条件
        var conditionNameTemp = $("input.js_conditionNames", ct001QueryForm).val().split(",");
        var conditionTemp = $("input.js_conditions", ct001QueryForm).val().split(",");
        var conditionValueTemp = $("input.js_conditionValues", ct001QueryForm).val().split(",");
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
            .find(":checkbox[checked][name=ct001ContractStatus]").each(
            function (i) {
                ctStatus.push($(this).val());
            }
        );
        if (ctStatus.length > 0) {
            conditionNameArr.push("HouseInfo.HsCtInfo." + ctStatusField);
            conditionArr.push("in");
            conditionValueArr.push(ctStatus.join("|"));
        }
        $("input[name=conditionName]", ct001QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", ct001QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", ct001QueryForm).val(conditionValueArr.join(","));
        // 查询处理结果
        Page.query(ct001QueryForm, "");
    },

    /**
     * 签约信息
     * @param hsId
     */
    ctInfo: function (hsId) {
        ct001.ctInfo(hsId, "");
    },

    /**
     * 签约信息
     * @param hsId
     */
    ctInfo: function (hsId, opFlag) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("opFlag", opFlag);
        packet.url = getGlobalPathRoot() + "eland/ct/ct001/ct001-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct001Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct001Context", navTab.getCurrentPanel()));
        });
    },

    /**
     * 签约信息
     * @param hsId
     */
    oneCtInfo: function (hsId, opFlag) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("opFlag", opFlag);
        packet.url = getGlobalPathRoot() + "eland/ct/ct001/ct001-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct00108OneDiv", navTab.getCurrentPanel()).html(response);
            initUI($("#ct00108OneDiv", navTab.getCurrentPanel()));
        });
    },


    /**
     * 房屋信息
     * @param hsId
     */
    openHouseView: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.openTab(null, "ph003-init", url, {title: "居民信息", fresh: true}, "居民信息");
    },

    /**
     * 生成业务报表
     * @param formId
     * @param templateName
     */
    generateCt: function (clickObj) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        //判断是否是多产权人签约
        var $thisTr = $(clickObj).closest("tr");
        var ctType = $("select[name=ctType]", $thisTr).val();
        // 无安置方式直接返回，不生成处理文档
        if (ctType == "") {
            return;
        }
        var hsCtId = $("input[name=hsCtId]", $thisTr).val();
        var hsId = $("input[name=hsId]", $thisTr).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-genMainCtDoc.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("ctType", ctType);
        packet.data.add("fromOp", "change");
        packet.data.add("schemeType", schemeType);
        core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (!isSuccess) {
                    $("select[name=ctType]", $thisTr).val("");
                    alertMsg.error(data.errMsg);
                }
            }
        );
    },

    /**
     * 导出安置协议
     */
    downloadDoc: function (clickObj) {
        //判断是否是多产权人签约
        var $thisTr = $(clickObj).closest("tr");
        var ctType = $("select[name=ctType]", $thisTr).val();
        var hsCtId = $("input[name=hsCtId]", $thisTr).val();
        var hsId = $("input[name=hsId]", $thisTr).val();
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-genMainCtDoc.gv?fromOp=download&hsId=" + hsId
            + "&hsCtId=" + hsCtId + "&ctType=" + ctType
            + "&schemeType=" + schemeType
            + "&prjCd=" + getPrjCd();
        window.open(url);
    },

    /**
     * 确认签约
     * @param hsId 房产id
     * @param hsCtId 签约id
     */
    cfmCt: function () {
        var hsIds = [];
        var hsCtIds = [];
        var ctTypes = [];
        // 获取本轮签约人、签约安置方式
        var container = $("#ct00107Table", navTab.getCurrentPanel());
        var checkSuccess = true;
        var checkMessage = true;
        container.find(">tr:visible").each(function (index, element) {
            var $tr = $(element);
            var hsId = $("input[name=hsId]", $tr).val();
            var hsCtId = $("input[name=hsCtId]", $tr).val();
            var ctType = $("select[name=ctType]", $tr).val();
            if (ctType == '') {
                checkSuccess = false;
                checkMessage = "第" + (index + 1) + "个签约人的安置方式不能为空！";
                return false;
            }
            var printCtStatus = $("input[name=printCtStatus]", $tr).val();
            if (printCtStatus != "1") {
                checkSuccess = false;
                checkMessage = "第" + (index + 1) + "个人的安置协议未打印!";
                return false;
            }
            hsIds.push(hsId);
            hsCtIds.push(hsCtId);
            ctTypes.push(ctType);
        });
        if (!checkSuccess) {
            alertMsg.warn(checkMessage);
            $.pdialog.closeCurrent();
            return;
        } else if (hsIds.length == 0) {
            alertMsg.warn("无签约人信息！");
            $.pdialog.closeCurrent();
            return;
        }
        var mark = "";
        var flag = false;
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-signControl.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                mark = jsonData.mark;
                flag = jsonData.flag;
                if (flag) {
                    // 更新签约令牌
                    var tokenId = jsonData.tokenId;
                    $("input[name=tokenId]", navTab.getCurrentPanel()).val(tokenId);
                    //  签证返回来的安置方式
                    var ctReadNotice = $("input[name=ctReadNotice]", navTab.getCurrentPanel()).val();
                    if (ctReadNotice != "") {
                        // 弹框 展示 签约须知
                        url = getGlobalPathRoot() + "eland/ct/ct001/ct001-viewCtNotice.gv?prjCd=" + getPrjCd()
                            + "&noticeId=" + ctReadNotice;
                        $.pdialog.open(url, "ct001_ct_notice", "签约须知", {
                            mask: true, maxable: true, height: 600, width: 800
                        });
                    } else {
                        ct001.readNoticeCfmCt();
                    }
                } else {
                    if (mark == "15") {
                        alertMsg.warn("本轮已完成签约，请等待下轮签约！");
                    } else if (mark == "2") {
                        alertMsg.warn("暂停签约中！");
                    } else {
                        alertMsg.warn("当前状态不允许确认签约！");
                    }
                    return false;
                }
            } else {
                // 清空签约令牌
                $("input[name=tokenId]", navTab.getCurrentPanel()).val("");
                alertMsg.error(jsonData.errMsg);
                return false;
            }
        });
    },

    /**
     * 阅读 签约须知 后确认签约
     * @param hsId
     * @param hsCtId
     */
    readNoticeCfmCt: function () {
        var hsIds = [];
        var hsCtIds = [];
        var ctTypes = [];
        // 获取本轮签约人、签约安置方式
        var container = $("#ct00107Table", navTab.getCurrentPanel());
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var checkSuccess = true;
        container.find(">tr:visible").each(function (index, element) {
            var $tr = $(element);
            var hsId = $("input[name=hsId]", $tr).val();
            var hsCtId = $("input[name=hsCtId]", $tr).val();
            var ctType = $("select[name=ctType]", $tr).val();
            if (ctType == '') {
                alertMsg.warn("第" + (index + 1) + "个签约人的安置方式不能为空！");
                checkSuccess = false;
                return false;
            }
            hsIds.push(hsId);
            hsCtIds.push(hsCtId);
            ctTypes.push(ctType);
        });
        if (hsIds.length == 0 || !checkSuccess) {
            alertMsg.warn("无签约人信息！");
            $.pdialog.closeCurrent();
            return;
        }
        // 获取签约令牌
        var tokenId = $("input[name=tokenId]", navTab.getCurrentPanel()).val();
        alertMsg.confirm("您是否已经确认签约人为被征收人（被委托人或被承诺人），并核对完签约文件完整有效，收取了相关材料？", {
            okCall: function () {
                alertMsg.confirm("对方是否已签订《签约须知》？", {
                    okCall: function () {
                        alertMsg.confirm("对方是否已经阅读协议并在纸质协议上完成签约？", {
                            okCall: function () {
                                // 控制二次提交
                                var clickObj = $(this);
                                var enabled = clickObj.attr("enabled");
                                if ("false" == enabled) {
                                    return;
                                } else {
                                    clickObj.attr("enabled", "false");
                                }
                                // 提交用户请求
                                var url = getGlobalPathRoot() + "eland/ct/ct001/ct00101-cfmContract.gv";
                                var packet = new AJAXPacket(url);
                                packet.data.add("prjCd", getPrjCd());
                                packet.data.add("hsCtIds", hsCtIds.join(","));
                                packet.data.add("hsIds", hsIds.join(","));
                                packet.data.add("ctTypes", ctTypes.join(","));
                                packet.data.add("schemeType", schemeType);
                                // 签约令牌
                                packet.data.add("tokenId", tokenId);
                                // 提交签约信息
                                core.ajax.sendPacketHtml(packet, function (response) {
                                    var data = eval("(" + response + ")");
                                    var isSuccess = data.success;
                                    if (isSuccess) {
                                        alertMsg.correct("确认签约成功，可继续打印回执单！");
                                        ct001.ctInfo(hsIds[0]);
                                    } else {
                                        alertMsg.error(data.errMsg);
                                    }
                                    $.pdialog.closeCurrent();
                                }, true);
                            },
                            cancelCall: function () {
                                $.pdialog.closeCurrent();
                            }
                        });
                    },
                    cancelCall: function () {
                        $.pdialog.closeCurrent();
                    }
                });
            },
            cancelCall: function () {
                $.pdialog.closeCurrent();
            }
        });
    },
    readNoticeCancel: function () {
        $.pdialog.closeCurrent();
    },

    /**
     * 取消签约
     * @param hsId 房产id
     * @param hsCtId 签约id
     */
    cancelCt: function (hsId, hsCtId) {
        alertMsg.confirm("是否取消签约？", {
            okCall: function () {
                var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
                var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-cancelContract.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("hsId", hsId);
                packet.data.add("hsCtId", hsCtId);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("schemeType", schemeType);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        alertMsg.correct("签约取消成功！");
                        ct001.ctInfo(hsId);
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 未签约切换安置方式
     * @param clickObj 点击对象
     */
    sltChgCtType: function (clickObj) {
        //判断是否是多产权人签约
        var $thisTr = $(clickObj).closest("tr");
        var ctType = $("select[name=ctType]", $thisTr).val();
        var hsCtId = $("input[name=hsCtId]", $thisTr).val();
        var hsId = $("input[name=hsId]", $thisTr).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-changeCtType.gv?opCode=change_ct_type";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("ctType", ctType);
        packet.data.add("opCode", "change_ct_type");
        core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (!isSuccess) {
                    $("select[name=ctType]", $thisTr).val("");
                    alertMsg.error(data.errMsg);
                }
            }
        );
    },

    /**
     * 切换安置方式
     * @param hsId 房产id
     * @param hsCtId 签约id
     */
    changeCtType: function (hsId, hsCtId, ctType) {
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-openChangeCtType.gv?prjCd=" + getPrjCd()
            + "&hsId=" + hsId + "&hsCtId=" + hsCtId + "&ctType=" + ctType + "&opCode=change_ct_type";
        $.pdialog.open(url, "ct001-openChangeCtType", "更换安置方式", {mask: true, maxable: false, height: 165, width: 400});
    },

    /**
     * 生成签约回执单
     * @param formId
     * @param templateName
     */
    generateCtOrder: function (hsId) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-generateCtOrder.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#ct001_ReportData", navTab.getCurrentPanel());
                container.html(response);
                container.initUI();
            }
        );
    },

    /**
     * 下载签约回执单
     * @param formId
     * @param templateName
     */
    downloadCtOrder: function (hsId) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-generateCtOrder.gv?prjCd=" + getPrjCd()
            + "&fromOp=download"
            + "&schemeType=" + schemeType
            + "&hsId=" + hsId;
        window.open(url);
    },


    /**
     * 多人签约打印协议
     * @param clickObj
     */
    printCtByRow: function (clickObj, printObj) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 判断是否是多产权人签约
        var $thisTr = $(clickObj).closest("tr");
        var ctType = $("select[name=ctType]", $thisTr).val();
        if (ctType == "") {
            alertMsg.warn("安置方式不能为空！");
            return;
        }
        var hsId = $("input[name=hsId]", $thisTr).val();
        var hsCtId = $("input[name=hsCtId]", $thisTr).val();
        var prjCd = getPrjCd();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-genMainCtDoc.gv?fromOp=print"
            + "&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&prjCd=" + prjCd
            + "&ctType=" + ctType
            + "&schemeType=" + schemeType;
        $.pdialog.open(url, "generateCt", "打印协议", {
            mask: true, max: true, resizable: false, close: function () {
                alertMsg.confirm("确认文档已打印？", {
                    okCall: function () {
                        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-printCtText.gv";
                        var packet = new AJAXPacket(url);
                        packet.data.add("printObj", printObj);
                        packet.data.add("prjCd", prjCd);
                        packet.data.add("hsCtId", hsCtId);
                        packet.data.add("ctType", ctType);
                        packet.data.add("schemeType", schemeType);
                        core.ajax.sendPacketHtml(packet, function (response) {
                            var data = eval("(" + response + ")");
                            var isSuccess = data.success;
                            if (isSuccess) {
                                $("input[name=printCtStatus]", $thisTr).val('1');
                            } else {
                                alertMsg.error(data.errMsg);
                            }
                        }, true);
                    }
                });
                return true;
            }
        });
    },

    /**
     * 确认打印协议， 或者确认打印回执单。
     * @param printObj 协议 or 回执单
     */
    printCt: function (obj, hsId, printObj, hsCtId) {
        try {
            var iframeDoc = document.getElementById("ct001DocIFrame").contentWindow.document;
            iframeDoc.getElementById("PageOfficeCtrl1").ShowDialog(4);
        } catch (e) {
            //  打印出错不进行处理
        }
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        alertMsg.confirm("确认文档已打印？", {
            okCall: function () {
                // 判断是否是多产权人签约
                var $thisTr = $(obj).closest("tr");
                if (hsCtId == "") {
                    var ctType = $("select[name=ctType]", $thisTr).val();
                    hsCtId = $("input[name=hsCtId]", $thisTr).val();
                } else {
                    ctType = $("select[name=ctType]", navTab.getCurrentPanel()).val();
                }
                var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-printCtText.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("printObj", printObj);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("hsCtId", hsCtId);
                packet.data.add("ctType", ctType);
                packet.data.add("schemeType", schemeType);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        //ct001.ctInfo(hsId);
                        if (printObj == 'printCt') {
                            $("input[name=printCtStatus]", $thisTr).val('1');
                            //$("#paintOrNo", navTab.getCurrentPanel()).val('1');
                        }
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }
        });
    },


    /**
     * 房屋信息
     * @param buildId
     */
    openHs: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },
    /**
     * 保存 上传附件
     */
    saveUploadFiles: function (hsCtId) {
        var packet = new AJAXPacket();
        packet.url = getGlobalPathRoot() + "eland/ct/ct001/ct001-saveUploadFiles.gv";
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("hsCtId", hsCtId);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("附件保存成功！");
            }
        }, true);
    },

    /**
     * 展示选房确认单
     * @param hsId
     * @param hsCtId
     */
    showCfmChHsNum: function (hsId, hsCtId) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-infoChHsNum.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("schemeType", schemeType);
        core.ajax.sendPacketHtml(packet, function (response) {
                $("#ct001Context", navTab.getCurrentPanel()).html(response);
                initUI($("#ct001Context", navTab.getCurrentPanel()));
            }
        );
    },


    /**
     * 生成序号单
     * @param formId
     * @param templateName
     */
    generateChHsNum: function (hsId) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-generateCfmChHsNum.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#ct001_ReportData", navTab.getCurrentPanel());
                container.html(response);
                container.initUI();
            }
        );
    },

    /**
     * 下载序号单
     * @param formId
     * @param templateName
     */
    downloadChHsNum: function (hsId) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-generateCfmChHsNum.gv?prjCd=" + getPrjCd()
            + "&hsId=" + hsId
            + "&schemeType=" + schemeType
            + "&fromOp=download";
        window.open(url);
    },

    /**
     * 添加签约人
     */
    addCtPerson: function (hsCtId) {
        var prjCd = getPrjCd();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-addCtPerson.gv?prjCd=" + prjCd + "&hsCtId=" + hsCtId;
        $.pdialog.open(url, "ct00107", "添加签约人",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    }
};

$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("ct001SchDiv", "ct001Context", {});
});
