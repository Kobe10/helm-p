/**
 * Created by shfb_wang on 2015/8/26 0026.
 */
var oh003 = {
    /**
     * 执行查询操作
     */
    refleshData: function () {
        // 查询
        var viewModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
        var viewFilter = $("input.js_query_filter", navTab.getCurrentPanel()).val();
        var oh003Form = $("#oh003Form", navTab.getCurrentPanel());
        var oh003QSch = $("#oh003QSch", navTab.getCurrentPanel());
        //定义查询条件数组，待添加条件
        var conditionName = [];
        var condition = [];
        var conditionValue = [];
        var resultField = $("input[name=resultField]", oh003Form);
        var jsQueryFilter = $("input[name=jsQueryFilter]", oh003Form);
        $("#oh003QSch", navTab.getCurrentPanel()).find("input").each(function () {
            var $this = $(this);
            var attrName = $this.attr("name");
            var attrValue = $this.val();
            if (!attrName || attrName == "") {
                return true;
            }
            if ($.trim(attrValue) != "") {
                conditionName.push($this.attr("name"));
                condition.push($this.attr("condition"));
                conditionValue.push($this.val());
            }
        });
        // 改变结果展示内容
        var resultFieldVal = "";
        if (viewFilter == "f2") {
            resultFieldVal = "oldBuldId_buildFullAddr,oldHsId_hsOwnerPersons,hsSt,hsAddr,buyPersonName,buyPersonTel,handHsDate,buyHsCtDate,cmpWyCost,cmpQnCost,jsTime";
        } else {
            resultFieldVal = "oldBuldId_buildFullAddr,oldHsId_hsOwnerPersons,hsSt,hsAddr,buyPersonName,buyPersonTel,handHsDate,buyHsCtDate,cmpWyCost,cmpQnCost";
        }
        jsQueryFilter.val(viewFilter);
        resultField.val(resultFieldVal);
        //将拼接的条件添加到 查询form里。
        $("input[name=conditionName]", oh003Form).val(conditionName.join(","));
        $("input[name=condition]", oh003Form).val(condition.join(","));
        $("input[name=conditionValue]", oh003Form).val(conditionValue.join(","));
        var form = $('#oh003Form', navTab.getCurrentPanel());
        if (!viewModel) {
            viewModel = "m1";
        }
        if (viewModel == "m1") {
            Page.query(form, "");
        } else {
            // 重新加载
            var packet = new AJAXPacket();
            packet.data.add("prjCd", getPrjCd());
            // 其他查询条件
            packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
            packet.data.add("conditions", $("input[name=condition]", form).val());
            packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
            packet.data.add("viewFilter", viewFilter);

            // 查询条件
            packet.url = getGlobalPathRoot() + "eland/oh/oh003/oh003-initRpt.gv";
            core.ajax.sendPacketHtml(packet, function (response) {
                $("#oh003_div", navTab.getCurrentPanel()).html(response);
                initUI($("#oh003_div", navTab.getCurrentPanel()));
            });

        }
    },

    /**
     * 批量结算
     */
    batchJs: function () {
        var newHsIds = new Array();
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=newHsId]").each(
            function (i) {
                newHsIds.push($(this).val());
            }
        );
        if (newHsIds.length == 0) {
            alertMsg.warn("请选择要结算的房源");
        } else {
            var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-initBEdit.gv?prjCd=" + getPrjCd()
                + "&newHsIds=" + newHsIds.join(",");
            $.pdialog.open(url, "oh00104", "批量结算",
                {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 600}
            );
        }
    },
    /**
     * 批量结算状态
     */
    batchJsStatus: function () {
        var newHsIds = new Array();
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=newHsId]").each(
            function (i) {
                newHsIds.push($(this).val());
            }
        );
        if (newHsIds.length == 0) {
            alertMsg.warn("请选择要结算的房源");
        } else {
            var url = getGlobalPathRoot() + "eland/oh/oh003/oh003-initBEdit.gv?prjCd=" + getPrjCd()
                + "&newHsIds=" + newHsIds.join(",");
            $.pdialog.open(url, "oh00104", "批量结算",
                {mask: true, max: false, maxable: false, resizable: false, width: 500, height: 200}
            );
        }
    },
    /**
     * 切换展示视图
     */
    changeShowModel: function (obj, model) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        if (currentModel == model) {
            return;
        }
        // 修改模式
        currentModelObj.val(model);
        $this.find("span").addClass("active");
        $this.siblings("li.model").find("span.active").removeClass("active");
        // 执行查询
        oh003.refleshData();
    },

    /**
     * 切换统计 过滤指标
     */
    changeShowFilter: function (obj, filter) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_filter", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        var oh003Qsch = $("#oh003QSch", navTab.getCurrentPanel())
        if (currentModel == filter) {
            //点击本身去掉过滤条件
            currentModelObj.val("");
            $this.find("span").removeClass("active");
            $this.siblings("li.filter").find("span").removeClass("active");
            //
            $("input[name=jsStatus]", oh003Qsch).val("");
            $("input.autocomplete", oh003Qsch).val("");
        } else {
            if ("f2" == filter) {
                $("input[name=jsStatus]", oh003Qsch).val("1");
                $("input.autocomplete", oh003Qsch).val("是");
            } else {
                $("input[name=jsStatus]", oh003Qsch).val("0");
                $("input.autocomplete", oh003Qsch).val("否");
            }
            // 修改模式
            currentModelObj.val(filter);
            $this.find("span").addClass("active");
            $this.siblings("li.filter").find("span.active").removeClass("active");
        }
        oh003.refleshData();
    },

    /**
     * 快速检索对话框打开
     */
    openQSch: function (clickObj) {
        var active = $("#oh003QSch", navTab.getCurrentPanel()).attr("active");
        if ("on" != active) {
            $("#oh003QSch", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#oh003QSch input:eq(0)", navTab.getCurrentPanel()).focus();
        } else {
            $("#oh003QSch", navTab.getCurrentPanel()).hide().attr("active", "");
        }
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#oh003QSch", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = false;
            } else {
                close = true;
            }
        }
        if (close) {
            $("#oh003QSch", navTab.getCurrentPanel()).hide().attr("active", "");
        }
    },

    /**
     * 快速检索
     */
    qSchData: function () {
        //清空查询form
        var formObj = $('#oh003Form', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val("");
        $("input[name=condition]", formObj).val("");
        $("input[name=conditionValue]", formObj).val("");
        oh003.closeQSch(true);
        oh003.refleshData();
    },

    /**
     * 查询院落下的所有房产
     */
    queryHsByBuildId: function (obj, buildId, param) {
        var $_this = $(obj);
        var oldhistory = $_this.next("#history");
        if (oldhistory.length > 0) {
            oldhistory.remove();
            return
        }
        var url = getGlobalPathRoot() + "eland/oh/oh003/oh003-queryHsByBuildId.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("buildId", buildId);
        packet.data.add("param", param);
        core.ajax.sendPacketHtml(packet, function (response) {
            var tdLength = $_this.children().length;
            var history = $("#history", navTab.getCurrentPanel());
            if (history.length > 0) {
                history.remove();
            }
            var htm = "<tr id='history'><td  colspan='" + tdLength + "'>" + response + "</td></tr>";
            $_this.after(htm);
            initUI($("#history", navTab.getCurrentPanel()));
        });
    },


    /**
     * 发起腾退任务
     */
    initiateTask: function (procDefKey, buildId) {
        index.startWf(procDefKey, {busiKey: buildId, backUrl: "eland/pb/pb002/pb002-init.gv?buldId=" + buildId});
    },

    /*修改居民信息*/
    editHouse: function (hsId) {
        var url = "eland/ph/ph003/ph00301-initHs.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph00301-initHs", opName: "信息登记", fresh: true});
    },


    /**
     * 房屋信息
     * @param buildId
     */
    openHs: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    }


};

