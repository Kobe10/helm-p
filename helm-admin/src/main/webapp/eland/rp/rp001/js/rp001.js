var rp001 = {
    changeReportItem: function (obj) {
        var rp001SchContainer = $("#rp001SchContainer", navTab.getCurrentPanel());
        var _obj = $(obj);
        if (_obj.attr("checked")) {
            $("div.js-slt-item", rp001SchContainer).append(_obj.parent().clone());
            var textName = _obj.parent().find("a").text();
            rp001.refreshItem(_obj.val(), textName);
        } else {
            var value = _obj.val();
            var sameCheckItem = $("div.js-slt-item", rp001SchContainer).find("input[type=checkbox][value=" + value + "]");
            sameCheckItem.parent().remove();
            $("div.js-job-cd-list").find("input[type=checkbox][value=" + value + "]").removeAttr("checked");
            $("#reportDiv", rp001SchContainer).find("div." + _obj.val()).remove();
            //
            var rp001DivContainer = $("#rp001DivContainer");
            rp001DivContainer.find("div." + _obj.val()).remove();
        }
    },
    refreshItem: function (jobCdType, jobCdName) {
        // 调用服务增加展示图标
        var rp001DivContainer = $("#rp001DivContainer");
        // 查询参数
        var url = getGlobalPathRoot() + "eland/rp/rp001/rp001-info.gv";
        // 计算比率
        if (jobCdType == "survey_ratio") {
            url = getGlobalPathRoot() + "eland/rp/rp001/rp001-ratio.gv";
        } else if (jobCdType == "hs_tp") {
            url = getGlobalPathRoot() + "eland/rp/rp001/rp001-hsTp.gv";
        }
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("jobCdType", jobCdType);
        packet.data.add("jobCdName", jobCdName);
        packet.data.add("reportDate", $("input[name=reportDate]", navTab.getCurrentPanel()).val());
        // 不显示背景
        packet.noCover = true;

        // 请求获取销售品销售统计报文
        core.ajax.sendPacketHtml(packet, function (response) {
            var newRptItem = $(response);
            var oldDiv = rp001DivContainer.find("div." + jobCdType);
            if (oldDiv.length == 1) {
                oldDiv.html("");
                oldDiv.html(newRptItem.html());
            } else {
                rp001DivContainer.append(newRptItem);
            }
        }, true);
    },
    /**
     * 显示指标的详细访问规则
     * @param obj 点击对象
     */
    openTip: function (jobCdType, jobCdName) {
        var rp001SchContainer = $("#rp001SchContainer", navTab.getCurrentPanel());
        var jobCdDivDescSpan = $("input[type=checkbox][value=" + jobCdType + "]", rp001SchContainer).parent().find("span.hidden");
        var jobCdTypeDesc = "";
        if (jobCdDivDescSpan.length > 0) {
            jobCdTypeDesc = $(jobCdDivDescSpan).html();
        }
        var url = getGlobalPathRoot() + "eland/rp/rp001/rp001-desc.gv?prjCd=" + getPrjCd() + "&jobCdTypeDesc=" + encodeURI(encodeURI(jobCdTypeDesc));
        $.pdialog.open(url, "jobDesc", jobCdName, {mask: true});
    },

    removeAll: function (obj) {
        var rp001SchContainer = $(obj).parent().parent();
        $("input[type=checkbox]", rp001SchContainer).each(function () {
            $(this).removeAttr("checked");
            rp001.changeReportItem(this);
        });
    },

    addAll: function (obj) {
        var container = $(obj).parent().parent();
        $("input[type=checkbox]", container).each(function () {
            var _this = $(this);
            if (!_this.attr("checked")) {
                _this.attr("checked", "checked");
                rp001.changeReportItem(this);
            }
        });
    },
    refreshAll: function () {
        var container = $("#rp001SchContainer", navTab.getCurrentPanel()).find("div.js-slt-item");
        $("input[type=checkbox]", container).each(function () {
            var _this = $(this);
            if (_this.attr("checked")) {
                var textName = _this.parent().find("a").text();
                rp001.refreshItem(_this.val(), textName);
            }
        });
    }
};
