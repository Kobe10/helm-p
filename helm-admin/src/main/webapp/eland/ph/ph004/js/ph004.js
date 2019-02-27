var ph004 = {
    calculate: function () {
        // 提交表单
        var formObj = $("#ph004Frm", navTab.getCurrentPanel());
        // 提交表单验证
        if (formObj.valid()) {
            // 提交表单进行数据计算
            // 序列化表单数据
            var jsonData = $(formObj).serializeArray();
            jsonData.push({
                name: 'prjCd',
                value: getPrjCd()
            });
            // 请求页面获取当前页面数据
            var packet = new AJAXPacket(getGlobalPathRoot() + "eland/ph/ph004/ph004-cal.gv");
            packet.data.data = jsonData;
            // 判断是点击查询还是翻页
            core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#calResult", navTab.getCurrentPanel());
                container.html(response);
                initUI(container);
                var options = {
                    initialState: 'expanded'
                };
                $("#ph004TreeBody", navTab.getCurrentPanel()).treetable(options);
                ph004.serializIdText();
                ph004.adjustHeight();
            });
        }
    },

    /**
     * 处理 每个节点展示 文本
     */
    serializIdText: function () {
        var treeTable = $("#ph004TreeBody", navTab.getCurrentPanel());
        var treeTrs = treeTable.find("tr[data-tt-id][data-tt-parent-id]:visible");
        var trLength = treeTrs.length;
        var idMap = new Map();
        var pidMap = new Map();
        //loop all tr construct two Map;
        treeTrs.each(function () {
            var _oneTr = $(this);
            var oneTrId = _oneTr.attr("data-tt-id");
            var oneTrPid = _oneTr.attr("data-tt-parent-id");
            idMap.put(oneTrId, _oneTr);
            var _oneTrChild = pidMap.get(oneTrPid);
            if (!_oneTrChild) {
                _oneTrChild = [];
                pidMap.put(oneTrPid, _oneTrChild);
            }
            _oneTrChild.push(_oneTr);
            _oneTrChild = null;
        });

        //loop all tr set tr text
        treeTrs.each(function (index, element) {
            var cTr = $(this);
            var cTrId = cTr.attr("data-tt-id");
            var cTrPid = cTr.attr("data-tt-parent-id");
            var childArr = pidMap.get(cTrPid);

            var childIdx = ph004.objInArray(cTrId, childArr);
            var pareText = "";
            //获取当前元素位于数组第几个，set tr text
            if (cTrPid != '') {
                var parentTr = idMap.get(cTrPid);
                pareText = parentTr.find("td:eq(0)").find("label").text();
            }
            var currText = "";
            if (pareText != "") {
                pareText = pareText + "-";
            }
            currText = pareText + (childIdx + 1);
            cTr.find("td:eq(0)").find("label").text(currText);
        });
    },

    objInArray: function (id, array) {
        for (var i = 0; i < array.length; i++) {
            var obj1 = array[i];
            if (id == obj1.attr("data-tt-id")) {
                return i;
            }
        }
        return -1;
    },

    /**
     * 显示/隐藏检索条件
     * @param obj 工作流信息
     */
    showHideCondition: function (obj) {
        var ph004Condition = $("#ph004Condition", navTab.getCurrentPanel());
        var $this = $(obj);
        if ($this.hasClass("collapsable")) {
            $this.removeClass("collapsable").addClass("expandable");
            ph004Condition.find("div.panelContent").hide();
        } else {
            $this.removeClass("expandable").addClass("collapsable");
            ph004Condition.find("div.panelContent").show();
        }
        ph004.adjustHeight();
    },

    /**
     * 自动调整计算结果高度
     */
    adjustHeight: function () {
        var ph004Condition = $("#ph004Condition", navTab.getCurrentPanel());
        var calResultH = $("#calResult", navTab.getCurrentPanel());
        calResultH.attr("layoutH", 110 + ph004Condition.height());
        calResultH.layoutH();
    },

    /**
     * 打印调查表
     */
    printInfo: function (id) {
        var ph000Form = $("#ph004_div", navTab.getCurrentPanel());
        var _printBoxId = 'printBox';
        var $contentBox = ph000Form ? ph000Form : $("body"),
            $printBox = $('#' + _printBoxId);
        if ($printBox.size() == 0) {
            $printBox = $('<div id="' + _printBoxId + '"></div>').appendTo("body");
        }
        $printBox.html($contentBox.html()).find("[layoutH]").height("auto")
        $printBox.find("div.subBar").remove()
        $printBox.find("div.panelFooter").remove()
        $printBox.printArea({
            extraCss: getGlobalPathRoot() + "oframe/themes/common/FormChecker.print.css"
        });
    }
};
