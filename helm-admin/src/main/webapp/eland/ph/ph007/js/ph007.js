/**
 * Created by shfb_wang on 2015/3/30.
 */
var ph007 = {
    /**
     * 初始化发起任务
     */
    initiateTask: function (procDefKey, busiKey) {
        index.startWf(procDefKey, {busiKey: busiKey});
    },

    /**
     * 导出院落说明
     * @param buildId
     */
    exportTtCb: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-exportTtCb.gv?buildId=" + buildId + "&prjCd=" + getPrjCd();
        window.open(url);
    },

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj, relId) {
        var $span = $(clickObj);
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
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
            + "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=" + relType + "&relId=" + relId;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            close: ph007.docClosed,
            param: {clickSpan: $span}
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
        var docIdArr = [];
        for (var i = 0; i < uploadedFiles.length; i++) {
            var uploadedFile = uploadedFiles[i];
            docIdArr.push(uploadedFile.docId);
        }
        $span.find("input[type=hidden]").val(docIdArr.join(","));
        if (docIdArr.length > 0) {
            var label = $span.find("label");
            if (label.html().startWith("上传")) {
                label.html(label.html().replace("上传", "查看"));
            }
        } else {
            var label = $span.find("label");
            if (label.html().startWith("查看")) {
                label.html(label.html().replace("查看", "上传"));
            }
        }
        // 调用文件的关闭
        return file.closeD();
    },

    /**
     * 获取任务指派人
     * @param obj
     * @returns {string}
     */
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
                return data.STAFF_NAME + " ( " + data.STAFF_CODE + " ) ";
            },
            mustMatch: false,
            remoteDataType: "json",
            autoFill: false,
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                $("input[name=assignee]", $(obj.source).closest("td")).val(obj.data.STAFF_CODE);
                $("input[name=targetPerTemp]", $(obj.source).closest("td")).val(obj.data.STAFF_NAME);
            },
            onNoMatch: function (obj) {
                $("input[name=assignee]", $(obj.source).closest("td")).val("");
            }
        }
    },

    /**
     * 预审处理弹框
     * @param hsId
     */
    openYuShen: function (hsId, obj) {
        var $this = $(obj);
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-doYuShen.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph00701", "预审处理", {
            max: false,
            mask: true,
            width: 600,
            height: 300,
            close: ph007.closeYuShen, param: {thisSpan: obj}
        });
    },

    /**
     * 关闭预审处理弹框
     * */
    closeYuShen: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult1]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 验证提交按钮是否允许提交。
     * */
    viewYuShen: function (hsId, obj) {
        var $this = $(obj);
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-doYuShen.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph00701", "预审处理", {
            max: false,
            mask: true,
            width: 600,
            height: 300
        });
    },

    /**
     * 提交预审流程
     */
    submitTask: function (formId, obj, jsonParam) {
        var $this = $(obj);
        var num = 0;
        $this.closest("table").find("td.js_result").each(function () {
            if ($(this).text() == '未审核') {
                num = num + 1;
            }
        });
        //if (num == 0) {
        ph007.submitVerifyassignee(formId, obj, jsonParam);
        //} else {
        //    alertMsg.warn("有房产未审核，无法提交！");
        //}
    },

    /**
     * 单独验证有没有输入 下一步处理人
     * */
    submitVerifyassignee: function (formId, obj, jsonParam) {
        var $this = $(obj);
        var num = 0;
        $this.closest("table").find("input.js_targetPerTemp").each(function () {
            if ($(this).val() == '') {
                $(this).addClass("inputWarn");
                num++;
            } else {
                $(this).removeClass("inputWarn");
            }
        });
        if (num > 0) {
            alertMsg.warn("请指定下一步任务处理人！");
        } else {
            //wf001.doTask(formId, jsonParam);
            wf001.doStart(formId, jsonParam);
            alertMsg.correct("提交处理成功！");
            navTab.closeCurrentTab();
        }
    },

    /**
     * 生成签约安置协议
     */
    generateDoc: function (hsId) {
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-generateContract.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        window.open(url);
    },

    /**
     * 确认签约
     */
    cfmContrant: function (hsCtId, obj) {
        var $this = $(obj);
        var thisTr = $this.closest("tr");
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-cfmContrant.gv?hsCtId=" + hsCtId + "&prjCd=" + getPrjCd();
        var ajaxPacket = new AJAXPacket(url);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("确认签约成功！");
                thisTr.find("td.js_result").text("已签约");
                thisTr.find("span.js_cfmContrant").addClass("hidden");
                thisTr.find("span.js_cancelContrant").removeClass("hidden");
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 取消签约
     */
    cancelContrant: function (hsCtId, obj) {
        var $this = $(obj);
        var thisTr = $this.closest("tr");
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-cancelContrant.gv?hsCtId=" + hsCtId + "&prjCd=" + getPrjCd();
        var ajaxPacket = new AJAXPacket(url);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("取消签约成功！");
                thisTr.find("td.js_result").text("未签约");
                thisTr.find("span.js_cfmContrant").removeClass("hidden");
                thisTr.find("span.js_cancelContrant").addClass("hidden");
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 购房人资格审核
     */
    buyPersonShenHe: function (hsId, obj) {
        var $this = $(obj);
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openGfsh.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "测试弹框", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 关闭购房人资格审核
     */
    closeBuyPerShenHe: function () {

    },

    /**
     * 打开退租申请
     */
    openTzsq: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openTzsh.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "退租申请", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeTzsq, param: {thisSpan: obj}
        });
    },

    /**
     * 关闭退租申请
     */
    closeTzsq: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult2]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 退租申请 结果
     */
    viewTzsq: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openTzsh.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "退租申请结果", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开私房核验
     */
    openSfhy: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openSfhy.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "私房核验", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeSfhy, param: {thisSpan: obj}
        });
    },

    /**
     * 关闭私房核验
     */
    closeSfhy: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult3]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 私房核验 结果
     */
    viewSfhy: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openSfhy.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "私房核验结果", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 完成退租
     */
    openWctz: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openWctz.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "完成退租", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeWctz, param: {thisSpan: obj}
        });
    },

    /**
     * 关闭完成退租
     */
    closeWctz: function (param) {
        var $this = $(param.thisSpan);
        var hsPubZj = $("input[name=hsPubZj]", $.pdialog.getCurrent()).val();
        var hsPubGhf = $("input[name=hsPubGhf]", $.pdialog.getCurrent()).val();
        $this.closest("tr").find("td.js_hsPubZj").text(hsPubZj);
        $this.closest("tr").find("td.js_hsPubGhf").text(hsPubGhf);
        return true;
    },

    /**
     * 查看 完成退租 结果
     */
    viewWctz: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openWctz.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "完成退租结果", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 网签缴税核税
     */
    openWqjs: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openWqjs.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "办理网签、核税、缴税手续", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeWqjs, param: {thisSpan: obj}
        });
    },

    /**
     * 关闭网签缴税核税
     */
    closeWqjs: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult4]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 网签缴税核税 结果
     */
    viewWqjs: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openWqjs.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "查看网签、核税、缴税结果", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 办理房屋所有权登记
     */
    openZrdj: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openZrdj.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "房屋所有权转让登记", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeZrdj, param: {thisSpan: obj}
        });
    },

    /**
     * 关闭办理房屋所有权登记
     */
    closeZrdj: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult5]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 办理房屋所有权登记 结果
     */
    viewZrdj: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openZrdj.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "房屋所有权转让登记", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 领取新证
     */
    openLqxz: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openLqxz.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "领取新证", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeLqxz, param: {thisSpan: obj}
        });
    },

    /**
     * 关闭领取新证
     */
    closeLqxz: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult6]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 领取新证 结果
     */
    viewLqxz: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openLqxz.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "查看领取新证结果", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 腾退交房
     */
    openTfjf: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openTfjf.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "腾退交房", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeTfjf, param: {thisSpan: obj}
        });
    },

    /**
     * 腾退交房
     */
    closeTfjf: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult7]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 腾退交房 结果
     */
    viewTfjf: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openTfjf.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "查看腾退交房结果", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 安置协议结算
     */
    openAzjs: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openAzjs.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "安置协议结算", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeAzjs, param: {thisSpan: obj}
        });
    },

    /**
     * 安置协议结算
     */
    closeAzjs: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult8]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 安置协议结算 结果
     */
    viewAzjs: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openAzjs.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "安置协议结算", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 户口外迁OA审核
     */
    openQhsh: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openQhsh.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "户口外迁OA审核", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeQhsh, param: {thisSpan: obj}
        });
    },

    /**
     * 户口外迁OA审核
     */
    closeQhsh: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult9]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 户口外迁OA审核 结果
     */
    viewQhsh: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openQhsh.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "户口外迁OA审核", {max: false, mask: true, width: 750, height: 350});
    },

    /**
     * 打开 支付户口外迁费
     */
    openZffy: function (hsId, obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openZffy.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "支付户口外迁费", {
            max: false, mask: true, width: 750, height: 350,
            close: ph007.closeZffy, param: {thisSpan: obj}
        });
    },

    /**
     * 支付户口外迁费
     */
    closeZffy: function (param) {
        var $this = $(param.thisSpan);
        var selectResult = $("select[name=procResult10]", $.pdialog.getCurrent()).find("option:selected").text();
        $this.closest("tr").find("td.js_result").text(selectResult);
        return true;
    },

    /**
     * 查看 支付户口外迁费 结果
     */
    viewZffy: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openZffy.gv?method=view&hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008", "支付户口外迁费", {max: false, mask: true, width: 750, height: 350});
    }
};