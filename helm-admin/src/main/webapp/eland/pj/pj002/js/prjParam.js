var prjParam = {
    /**
     * 保存项目参数 配置
     */
    saveParam: function () {

    },

    /**
     * 查询具体参数详情
     */
    cfgDetail: function (obj) {
        var itemCd = "";
        if (obj != null) {
            var $this = $(obj);
            $this.siblings().removeClass("active");
            $this.addClass("active");
            itemCd = $this.attr("itemCd");
        } else {
            $("#pj002ParamList>li", navTab.getCurrentPanel()).removeClass("active");
        }
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
        packet.data.add("itemCd", itemCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-prjSysCfg.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var textContainer = $("#pj002ParamInfoDiv", navTab.getCurrentPanel());
            textContainer.html(response);
            initUI(textContainer);
        });
    },

    valueTypeChange: function () {
        var selectValue = $("select[name=valueType]", navTab.getCurrentPanel()).val();
        if ("1" == selectValue) {
            $("#pj002Param", navTab.getCurrentPanel()).addClass("hidden");
        } else {
            $("#pj002Param", navTab.getCurrentPanel()).removeClass("hidden");
        }
    },
    saveCfg: function () {
        var $form = $("#sys00501form", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-save.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "cfgPrjCd", value: $("input[name=prjCd]", navTab.getCurrentPanel()).val()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    addRow: function (divId, obj) {
        var obj = table.addRow(divId, obj);
        $(obj).find("input[name='valueCd']").addClass("required");
        $(obj).find("input[name='valueName']").addClass("required");
    }
};
$(document).ready(function () {
    // 执行目录树初始化
    var roleList = $("#pj002ParamList>li", navTab.getCurrentPanel());
    if (roleList.length > 0) {
        roleList.eq(0).trigger("click");
    }
});

