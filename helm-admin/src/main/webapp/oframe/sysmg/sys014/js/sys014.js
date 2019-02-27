var sys014 = {
    /**
     * 标记为已读
     */
    update: function () {
        var noticeId = "";
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=noticeId]").each(
            function (i) {
                noticeId = noticeId + $(this).val() + ",";
            }
        );
        if (noticeId == "") {
            alertMsg.warn("请先选择消息");
            return
        }
        alertMsg.confirm("您确定修改为已读?", {
            okCall: function () {
                var packet = new AJAXPacket();
                var prjCd = getPrjCd();
                packet.data.add("noticeId", noticeId);
                packet.url = getGlobalPathRoot() + "oframe/sysmg/sys014/sys014-biaoNotice.gv?prjCd=" + prjCd;
                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("已标记为已读");
                        sys014.loadTabContext(1);
                    } else {
                        alertMsg.error(jsonData.data.errMsg);
                    }
                });
            }
        });
    },

    //自动补全负责人
    getUrl: function (obj) {
        return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?subSvcName=sys002Data";
    },
    getOption: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].STAFF_NAME;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.STAFF_NAME;
            },
            remoteDataType: "json",
            minChars: 0,
            delay: 300,
            onItemSelect: function (obj) {
                var name = obj.data.STAFF_NAME.trim();
                var tdObj = $(obj.source).parent();
                $("input[type=hidden]", tdObj).val(obj.data.STAFF_ID);
                $("input[type=text]", tdObj).val(name);
            },
            onNoMatch: function (obj) {
                var tdObj = $(obj.source).parent();
                $("input[type=hidden]", tdObj).val("");
                $("input[type=text]", tdObj).val("");
            }
        }
    },

    queryNotice: function (tabIdx) {
        // 隐藏弹出框
        sys014.openQSch(tabIdx);
        var formObj = $('#sys014queryForm', navTab.getCurrentPanel());
        if (formObj.length < 1) {
            return
        }
        sys014.query(tabIdx);
    },
    /**
     * 执行点击查询动作
     */
    query: function (tabIdx) {
        var dialogId = "sys014QSchDialog";
        var queryForm = "sys014unReadQueryForm";
        if (tabIdx == "2") {
            dialogId = "sys014ReadDialog";
            queryForm = "sys014ReadQueryForm";
        } else if (tabIdx == "3") {
            dialogId = "sys014QSchDialog";
            queryForm = "sys014queryForm";
        }
        var formObj = $("#" + queryForm, navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#" + dialogId, navTab.getCurrentPanel());
        // 覆盖的查询条件
        var coverConditionName = [];
        var coverCondition = [];
        var coverConditionValue = [];
        var coverNames = [];
        // 循环当前查询条件，
        $("input,select", dialogObj).each(function () {
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
            } else if ("SELECT" == tagName) {
                coverNames.push(attrName);
                if (value != "") {
                    coverConditionName.push(attrName);
                    coverCondition.push("=");
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
        sys014.loadTabContext(tabIdx);
    },

    /**
     * 加载t内容
     * @param obj
     */
    loadTabContext: function (id) {
        if (id == "1") {
            // 执行查询
            Page.query($("#sys014unReadQueryForm"), "");
        } else if (id == "2") {
            // 执行查询
            Page.query($("#sys014ReadQueryForm"), "");
        } else if (id = "3") {
            // 执行查询
            Page.query($("#sys014queryForm"), "");
        }
    },

    /**
     * 打开快速检索对话框
     */
    openQSch: function (tabIdx) {
        var dialogId = "sys014QSchDialog";
        if (tabIdx == "2") {
            dialogId = "sys014ReadDialog";
        } else if (tabIdx == "3") {
            dialogId = "sys014QSchDialog";
        }
        var div = $("#" + dialogId, navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (tabIdx) {
        var dialogId = "sys014QSchDialog";
        if (tabIdx == "2") {
            dialogId = "sys014ReadDialog";
        } else if (tabIdx == "3") {
            dialogId = "sys014QSchDialog";
        }
        $("#" + dialogId, navTab.getCurrentPanel()).hide().removeAttr("active");
    },
    /**
     * 发送消息内容
     */
    sendMessage: function (obj) {
        var _this = $(obj);
        var currTable = _this.closest("table");
        var prjOrgId = $("input[name=toOrgId]", currTable).val();
        var toStaffId = $("input[name=toStaffId]", currTable).val();
        if ((toStaffId != '' && toStaffId != undefined) || (prjOrgId != '' && prjOrgId != undefined)) {
            var noticeContent = $("textarea[name=noticeContent]", currTable).val();
            var url = getGlobalPathRoot() + "oframe/sysmg/sys014/sys014-saveNotice.gv?prjCd=" + getPrjCd();
            var packet = new AJAXPacket(url);
            packet.data.add("toOrgId", prjOrgId);
            packet.data.add("toStaffId", toStaffId);
            packet.data.add("noticeContent", noticeContent);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("消息发送成功！");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        } else {
            alertMsg.warn("接收人必须输入！");
        }
    },

    /**
     * 删除 收件人列表 收件人
     * @param obj
     */
    rmToStaff: function (obj) {
        var _this = $(obj);
        var currSpan = _this.closest("span");
        var currStaffInput = $("input[name=currentStaff]", currSpan).val();
        var staffIdArr = [];
        var staffIdStr = $("input[name=toStaffId]", $.pdialog.getCurrent()).val();
        if (staffIdStr && staffIdStr != undefined && staffIdStr != '') {
            staffIdArr = staffIdStr.split(",");
        }
        var newArray = [];
        var x = 0;
        for (var i = 0; i < staffIdArr.length; i++) {
            if (staffIdArr[i] == currStaffInput) {
                _this.parent().remove();

            } else {
                newArray[x++] = staffIdArr[i];
            }
        }
        $("input[name=toStaffId]", $.pdialog.getCurrent()).val(newArray.join(","));
    },

    /**
     * 自动信息接收人
     */
    getUrlren: function (obj) {
        return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?subSvcName=sys002Data";
    },
    getOptionren: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].STAFF_CODE + "-->" + result[i].STAFF_NAME;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.STAFF_NAME;
            },
            mustMatch: true,
            remoteDataType: "json",
            autoFill: true,
            delay: 100,
            minChars: 1,
            onItemSelect: function (obj) {
                //选中 收件人
                var staffIdArr = [];
                var staffIdStr = $("input[name=toStaffId]", $.pdialog.getCurrent()).val();
                if (staffIdStr && staffIdStr != undefined && staffIdStr != '') {
                    staffIdArr = staffIdStr.split(",");
                    var idInArr = false;
                    for (var i = 0; i < staffIdArr.length; i++) {
                        var staffId = staffIdArr[i];
                        if (staffId == obj.data.STAFF_ID) {
                            idInArr = true;
                            break;
                        }
                    }
                    if (!idInArr) {
                        staffIdArr.push(obj.data.STAFF_ID);
                        var currTd = $("td.js_toStaff_tr", $.pdialog.getCurrent());
                        var hideSpan = $("span.hidden", currTd).clone(true);
                        $("input[name=currentStaff]", hideSpan).val(obj.data.STAFF_ID);
                        hideSpan.removeClass("hidden");
                        $("span.js_toStaff_name", hideSpan).text(obj.data.STAFF_NAME);
                        currTd.append(hideSpan);
                    }
                    $("input[name=toStaffId]", $.pdialog.getCurrent()).val(staffIdArr.join(","));
                } else {
                    var currTd = $("td.js_toStaff_tr", $.pdialog.getCurrent());
                    var hideSpan = $("span.hidden", currTd).clone(true);
                    $("input[name=currentStaff]", hideSpan).val(obj.data.STAFF_ID);
                    hideSpan.removeClass("hidden");
                    $("span.js_toStaff_name", hideSpan).text(obj.data.STAFF_NAME);
                    currTd.append(hideSpan);
                    $("input[name=toStaffId]", $.pdialog.getCurrent()).val(obj.data.STAFF_ID);
                }
            }
        }
    },

    /**
     * 删除接收组织
     * @param obj
     */
    rmToOrg: function (obj) {
        var _this = $(obj);
        var currSpan = _this.closest("span");
        var currentOrg = $("input[name=currentOrg]", currSpan).val();
        var orgIdArr = [];
        var orgIdStr = $("input[name=toOrgId]", $.pdialog.getCurrent()).val();
        if (orgIdStr && orgIdStr != undefined && orgIdStr != '') {
            orgIdArr = orgIdStr.split(",");
        }
        var newArray = [];
        var x = 0;
        for (var i = 0; i < orgIdArr.length; i++) {
            if (orgIdArr[i] == currentOrg) {
                _this.parent().remove();

            } else {
                newArray[x++] = orgIdArr[i];
            }
        }
        $("input[name=toOrgId]", $.pdialog.getCurrent()).val(newArray.join(","));
    },

    //打开选择 消息接收人 页面
    sltMsgToStaff: function (obj) {
        var thisObj = $(obj);
        var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initStaffTree.gv";
        // 打开对话框
        var inputObj = thisObj.prev("input[type=text]");
        var option = {href: url, height: "230", offsetY: 31, relObj: inputObj};
        if (!option.width && option.relObj.length > 0) {
            option.width = option.relObj.width() + 24;
        }
        thisObj.openTip(option);
    }
};


