var sys011 = {
    /**
     * 打开快速检索对话框
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#sys011create", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#sys011create input:eq(0)", navTab.getCurrentPanel()).focus();
        } else {
            sys011.closeQSch(false);
        }
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#sys011create", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = true;
            } else {
                close = false;
            }
        }
        if (close) {
            $("#sys011create", navTab.getCurrentPanel()).hide();
        }
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
                $("input[type=hidden]", navTab.getCurrentPanel()).val(obj.data.STAFF_CODE);
                $("input[type=text]", navTab.getCurrentPanel()).val(obj.data.STAFF_NAME);
            }
        }
    },

    /**
     * 初始化权限树 tip页
     * @param obj
     */
    initRhtTree: function (obj) {
        var $this = $(obj);
        var inputObj = $(obj).prev("input[type=text]");
        var url = getGlobalPathRoot() + "oframe/sysmg/sys011/sys011-initRhtTree.gv?prjCd=" + getPrjCd();
        $this.openTip({
            href: url,
            width: inputObj.width() + 24,
            height: "280",
            offsetX: 105,
            offsetY: 75,
            relObj: inputObj
        });
    },

    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("rhtType", "1");
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys006/sys006-treeInfo.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var treeJson = jsonData.resultMap.treeJson;
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                // 左侧树的生成和回调
                var sys011RhtTreeSetting = {
                    data: {
                        simpleData: {
                            enable: true
                        }
                    },
                    view: {
                        selectedMulti: false
                    },
                    callback: {
                        onClick: sys011.clickNode
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#sys011RhtTree"), sys011RhtTreeSetting, treeJson);
                // 显示根节点数据
                var rootNode = zTree.getNodesByFilter(function (node) {
                    return node.level == 0
                }, true);
            } else {
                alertMsg.warn(jsonData.data.errMsg);
            }
        }, true, false);
    },

    /**
     * 点击 权限树 选中按钮权限查询
     * @param event
     * @param treeId
     * @param treeNode
     */
    clickNode: function (event, treeId, treeNode) {
        if (treeNode.iconSkin == 'opr') {
            $("input[name=rht_id]", navTab.getCurrentPanel()).val(treeNode.id);
            $("input[name=rht_name]", navTab.getCurrentPanel()).val(treeNode.name);

            var tipObj = $("#openTip", $.pdialog.getCurrent());
            if (tipObj.length == 0) {
                tipObj = $("#openTip", navTab.getCurrentPanel());
            }
            $(tipObj).remove();
        }
    },

    /**
     *
     */
    clearCond: function (obj) {
        "use strict";
        $("input[name=rht_id]", navTab.getCurrentPanel()).val("");
        $("input[name=rht_name]", navTab.getCurrentPanel()).val("");

        $("#openTip").closeTip();
    },

    /**
     * 查询日志详情
     * @param transactionId
     */
    logDetail: function (transactionId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys011/sys011-logDetail.gv?prjCd=" + getPrjCd() + "&transactionId=" + transactionId;
        $.pdialog.open(url, "sys011-logDetail", "日志详情", {height: 600, width: 800, mask: true});
    },

    /*
     0，取到开始时间time1与结束时间time2
     1，与当前时间比较
     2，获得 年与月 的 值yearAndMonth1，yearAndMonth2
     3，判断时间区域的选择正确：结束时间 即第二个时间  > 开始时间 即第一个时间
     4，(1)开始时间与结束时间属于同一月
     (2)如果不选择，默认查询当前 年月
     */
    checkTime: function () {
        //结束时间
        var timeEnd = $("input[name=log_time2]", navTab.getCurrentPanel());
        var endTime = timeEnd.val();
        //开始时间
        var timeStart = $("input[name=log_time1]", navTab.getCurrentPanel());
        var startTime = timeStart.val();
        var yearMonth;
        //年月
        //没有选择日期
        if (startTime == "") {
            timeStart.addClass("error");
            alertMsg.warn("开始时间不可为空！");
            return false;
        } else if (endTime == "") {
            timeEnd.addClass("error");
            alertMsg.warn("结束时间不可为空！");
            return false;
        } else {
            //有选择日期
            startTime = new Date(Date.parse(startTime.replace(/\-/g, "/")));
            endTime = new Date(Date.parse(endTime.replace(/\-/g, "/")));

            var nowDate = new Date().getTime();//当前时间
            if (nowDate - startTime < 0) {
                timeStart.addClass("error");
                alertMsg.warn("开始时间不可超过当前，请选择正确的时间！");
                return false;
            }

            //时间区间
            var runTime = endTime.getTime() - startTime.getTime();
            if (runTime <= 0) {
                timeStart.addClass("error");
                timeEnd.addClass("error");
                alertMsg.warn("结束时间不可超过开始时间，请选择正确的时间！");
                return false;
            }

            //开始时间的年月
            var ymStart = sys011.dateToYearMonthString(startTime);
            //结束时间的年月
            var ymEnd = sys011.dateToYearMonthString(endTime);
            if (ymStart != ymEnd) {
                timeStart.addClass("error");
                timeEnd.addClass("error");
                alertMsg.error("请选择同年月的时间区间！");
                return false;
            }
            yearMonth = ymStart;
        }
        $("#tableName", navTab.getCurrentPanel()).val(yearMonth);
        sys011.queryInfo();
    },

    queryInfo: function () {
        "use strict";
        Query.queryList('sys011frm', 'sys011_list_print');
        sys011.closeQSch();
    },

    /**
     * 时间转换为  ‘年月’
     *
     * var date = new Date();
     * var year = date.getFullYear();
     * var month = date.getMonth()+1;
     * var day = date.getDate();
     * var hour = date.getHours();
     * var minute = date.getMinutes();
     * var second = date.getSeconds();
     */
    dateToYearMonthString: function (date) {
        if (date == "" || date == "NaN") {
            return;
        }
        var ye = date.getFullYear();
        var mon = date.getMonth() + 1;
        if (mon < 10) {
            mon = "0" + mon;
        }
        return ye + "" + mon;
    }
};

$(document).ready(function () {
    // 执行查询
    sys011.checkTime();
});
