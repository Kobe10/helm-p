/**
 * Created by shfb_wang on 2015/7/22 0022.
 */
var ph00301_task = {
    /**
     * 发起修改申请, 打开指定下一步处理人页面
     */
    initTask: function (hsId) {
        var url = getGlobalPathRoot() + "/eland/ph/ph003/ph003_task-initSendTask.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
        $.pdialog.open(url, "task", "发送修改申请", {
            mask: true, max: false, height: 400, width: 800
        });
    },

    /**
     * 修改申请  流程  保存房产信息
     * @param saveFlag
     */
    saveProcInfo: function (saveFlag) {

    },
    /**
     * 确认发送修改申请
     */
    cfmSendTask: function (obj) {
        //处理发起流程、并发送消息给下一步处理人
        //var toStaffId = $("input[name=toAssigneeId]", navTab.getCurrentPanel()).val();
        //if (toStaffId != '' && toStaffId != undefined) {
            var tranFlag = false;
            var ajaxPacket = ph00301_input.save(1);
            if (ajaxPacket) {
                /**
                 * 流程变量
                 */
                ajaxPacket.data.data.push({name: "whetherToDb", value: false});
                ajaxPacket.data.data.push({
                    name: "procDefId",
                    value: $("input[name=procDefId]", navTab.getCurrentPanel()).val()
                });
                ajaxPacket.data.data.push({
                    name: "procDefKey",
                    value: $("input[name=procDefKey]", navTab.getCurrentPanel()).val()
                });
                ajaxPacket.data.data.push({
                    name: "busiKey",
                    value: $("input[name=busiKey]", navTab.getCurrentPanel()).val()
                });
                ajaxPacket.data.data.push({
                    name: "taskId",
                    value: $("input[name=taskId]", navTab.getCurrentPanel()).val()
                });
                //ajaxPacket.data.data.push({
                //    name: "assignee",
                //    value: toStaffId
                //});
                ajaxPacket.data.data.push({
                    name: "procPrjCd",
                    value: $("input[name=procPrjCd]", navTab.getCurrentPanel()).val()
                });
                ajaxPacket.data.data.push({
                    name: "taskComment",
                    value: $("textarea[name=taskComment]", navTab.getCurrentPanel()).val()
                });
                ajaxPacket.data.data.push({
                    name: "procInsName",
                    value: $("input[name=procInsName]", navTab.getCurrentPanel()).val()
                });
                // 提交保存请求
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("修改申请发起成功");
                        var procInsId = jsonData.procInsId;
                        var redirectUrl = getGlobalPathRoot() + "oframe/wf/wf001/wf001-viewWf.gv?procInsId=" + procInsId;
                        document.location.replace(redirectUrl);
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                }, true);
            }
        //} else {
        //    alertMsg.warn("下一步处理人必须输入！");
        //}
    },

    /**
     * 审核通过
     * @param formId 提交的formId
     * @param hsId   当前element
     * @param passFlag   自定义的参数
     */
    cmdPassTask: function (formId, hsId, passFlag) {
        //var toAssigneeId = $("input[name=toAssigneeId", navTab.getCurrentPanel());
        //var toAssigneeVal = toAssigneeId.val();
        //if (passFlag && toAssigneeId.length > 0 && toAssigneeVal == '') {
        //    alertMsg.warn("请指定下一步处理人！");
        //    return;
        //}
        var option = {};
        if (formId == 'ph003_cmd_st' || formId == 'ph003_dept_st' || formId == 'ph003_lock_st') {
            if (passFlag) {
                option = {WF_TSK_isAgree: "true", WF_PRI_isAgree: "true"};
            } else {
                option = {WF_TSK_isAgree: "false", WF_PRI_isAgree: "false"};
            }
        } else {

        }

        //执行下一步
        wf001.doTask(formId, option);

    },

    /**
     * 撤销
     * @param forceFlag 强制关闭
     */
    Revoke: function (procInsId) {
        alertMsg.confirm("您确定要撤销该流程?", {
            okCall: function () {
                // 提交页面数据
                var url = getGlobalPathRoot() + "oframe/wf/wf004/wf004-revoke.gv?prjCd=" + getPrjCd() + "&procInsId=" + procInsId;
                var ajaxPacket = new AJAXPacket(url);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("撤销成功");
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
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
                var staffCdArr = [];
                var staffIdStr = $("input[name=toStaffId]", navTab.getCurrentPanel()).val();
                var staffCdStr = $("input[name=toStaffCd]", navTab.getCurrentPanel()).val();
                if (staffIdStr && staffIdStr != undefined && staffIdStr != '') {
                    staffIdArr = staffIdStr.split(",");
                    staffCdArr = staffCdStr.split(",");
                    var idInArr = false;
                    for (var i = 0; i < staffIdArr.length; i++) {
                        var staffId = staffIdArr[i];
                        if (staffId == obj.data.STAFF_ID) {
                            idInArr = true;
                            break;
                        }
                    }
                    if (!idInArr) {
                        if (staffIdArr.length >= 1) {
                            alertMsg.warn("下一步处理人只能指定一位！");
                        } else {
                            staffIdArr.push(obj.data.STAFF_ID);
                            staffCdArr.push(obj.data.STAFF_CODE);
                            var currTd = $("td.js_toStaff_tr", navTab.getCurrentPanel());
                            var hideSpan = $("span.hidden", currTd).clone(true);
                            $("input[name=currentStaff]", hideSpan).val(obj.data.STAFF_ID);
                            hideSpan.removeClass("hidden");
                            $("span.js_toStaff_name", hideSpan).text(obj.data.STAFF_NAME);
                            currTd.append(hideSpan);
                        }
                    }
                    $("input[name=toStaffId]", navTab.getCurrentPanel()).val(staffIdArr.join(","));
                    $("input[name=toStaffCd]", navTab.getCurrentPanel()).val(staffCdArr.join(","));
                } else {
                    var currTd = $("td.js_toStaff_tr", navTab.getCurrentPanel());
                    var hideSpan = $("span.hidden", currTd).clone(true);
                    $("input[name=currentStaff]", hideSpan).val(obj.data.STAFF_ID);
                    hideSpan.removeClass("hidden");
                    $("span.js_toStaff_name", hideSpan).text(obj.data.STAFF_NAME);
                    currTd.append(hideSpan);
                    $("input[name=toStaffId]", navTab.getCurrentPanel()).val(obj.data.STAFF_ID);
                    $("input[name=toStaffCd]", navTab.getCurrentPanel()).val(obj.data.STAFF_CODE);
                }
            }
        }
    },

    /**
     *获取处理环节 的 下一步处理人
     */
    getOptionNextAssignee: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].STAFF_CODE + "(" + result[i].STAFF_NAME + ")";
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.STAFF_NAME + "(" + data.STAFF_CODE + ")";
            },
            mustMatch: true,
            remoteDataType: "json",
            autoFill: true,
            delay: 100,
            minChars: 1,
            onItemSelect: function (obj) {
                $("input[name=toAssigneeId]", $(obj.source).closest("td")).val(obj.data.STAFF_ID);
                $("input[name=assignee]", $(obj.source).closest("td")).val(obj.data.STAFF_CODE);
                $("input[name=targetPerTemp]", $(obj.source).closest("td")).val(obj.data.STAFF_NAME);
            },
            onNoMatch: function (obj) {
                $("input[name=toAssigneeId]", $(obj.source).closest("td")).val("");
                $("input[name=assignee]", $(obj.source).closest("td")).val("");
            }
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
        var staffCdArr = [];
        var staffIdStr = $("input[name=toStaffId]", navTab.getCurrentPanel()).val();
        var staffCdStr = $("input[name=toStaffCd]", navTab.getCurrentPanel()).val();
        if (staffIdStr && staffIdStr != undefined && staffIdStr != '') {
            staffIdArr = staffIdStr.split(",");
            staffCdArr = staffCdStr.split(",");
        }
        var newArray = [];
        var newArrayCd = [];
        var x = 0;
        for (var i = 0; i < staffIdArr.length; i++) {
            if (staffIdArr[i] == currStaffInput) {
                _this.parent().remove();
            } else {
                newArray[x++] = staffIdArr[i];
                newArrayCd[x++] = staffCdArr[i];
            }
        }
        $("input[name=toStaffId]", navTab.getCurrentPanel()).val(newArray.join(","));
        $("input[name=toStaffCd]", navTab.getCurrentPanel()).val(newArrayCd.join(","));
    },

    /**
     * 发起 锁定流程
     */
    sendLockTask: function (formId, hsId, obj) {
        wf001.doStart(formId, {});
    }
};