/**
 * Created by Dennis on 16/5/12.
 *
 */
var wf007 = {

    /**
     * 房屋信息
     */
    openHs: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /**
     * 快速检索对话框打开
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#wf007QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#wf007QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#wf007QSchDialog", navTab.getCurrentPanel()).attr("active");
            close = "on" != active;
        }
        if (close) {
            $("#wf007QSchDialog", navTab.getCurrentPanel()).hide();
        }
    },

    /**
     * 修改查询状态
     */
    changeActive: function (obj, val) {
        var $this = $(obj);
        var currentModelObj = $("input[name=isActive]", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        if (currentModel == val) {
            return;
        }
        // 修改模式
        currentModelObj.val(val);
        $this.find("span").addClass("active");
        $this.siblings().find("span.active").removeClass("active");

        wf007.refresh();
    },

    /**
     * 选择流程环节 查询处于该环节的 业务数据
     */
    sltFlowLink: function (obj, procKey, poItemId) {
        var $this = $(obj);
        //拼接流程参数
        if (procKey && poItemId) {
            var isStart = $("input[name=isStart]", $("#wf007Right", navTab.getCurrentPanel())).val();
            var isActive = $("input[name=isActive]", $("#wf007Right", navTab.getCurrentPanel())).val();

            if ($this.attr("isStart") == '1') {
                isStart = '1';
            } else {
                isStart = '0';
            }

            $("input[name=isStart]", $("#wf007Right", navTab.getCurrentPanel())).val(isStart);
            $("input[name=isActive]", $("#wf007Right", navTab.getCurrentPanel())).val(isActive);
            $("input[name=procDefKey]", $("#wf007Right", navTab.getCurrentPanel())).val(procKey);
            $("input[name=taskDefId]", $("#wf007Right", navTab.getCurrentPanel())).val(poItemId);
        }

        var formObj = $('#wf007queryForm', navTab.getCurrentPanel());
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        $("#wf007QSchDialog", navTab.getCurrentPanel()).find("input").each(function () {
            var $this = $(this);
            var attrName = $this.attr("name");
            var attrValue = $this.val();
            if (!attrName || attrName == "") {
                return true;
            }
            var nameIdx = -1;
            for (var i = 0; i < conditionName.length; i++) {
                var temp = conditionName[i];
                if (temp == attrName) {
                    nameIdx = i;
                    break;
                }
            }
            //form conditions had this field replace it.
            if (nameIdx != -1) {
                if ($.trim(attrValue) == "") {
                    conditionName.splice(nameIdx, 1);
                    condition.splice(nameIdx, 1);
                    conditionValue.splice(nameIdx, 1);
                } else {
                    condition[nameIdx] = $this.attr("condition");
                    conditionValue[nameIdx] = $this.val();
                }
            } else if ($.trim(attrValue) != "") {
                //form conditions hadn't this field add it.
                conditionName.push($this.attr("name"));
                condition.push($this.attr("condition"));
                conditionValue.push($this.val());
            }
        });
        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));

        wf007.closeQSch(true);
        wf007.refresh();
    },

    /**
     * 刷新右侧环节数据
     */
    refresh: function () {
        var form = $("#wf007queryForm", navTab.getCurrentPanel());
        wf007.query(form, "");
    },

    /**
     * 公共查询界面
     */
    query: function (formObj, successEvalJs) {
        //工作流参数
        var procDefKey = $("input[name=procDefKey]", $("#wf007Right", navTab.getCurrentPanel())).val();
        var taskDefId = $("input[name=taskDefId]", $("#wf007Right", navTab.getCurrentPanel())).val();
        var isActive = $("input[name=isActive]", $("#wf007Right", navTab.getCurrentPanel())).val();
        var isStart = $("input[name=isStart]", $("#wf007Right", navTab.getCurrentPanel())).val();
        var procKey = $("input[name=procKey]", $("#wf007Right", navTab.getCurrentPanel())).val();

        var url = getGlobalPathRoot() + "oframe/common/page/page-data.gv";
        var packet = new AJAXPacket(url);
        packet.data.data = formObj.serializeArray();
        packet.data.data.push({"name": "currentPage", "value": 1});
        packet.data.data.push({"name": "pageSize", "value": 20});
        packet.data.data.push({"name": "prjCd", "value": getPrjCd()});

        if (procDefKey && taskDefId && isActive) {
            packet.data.data.push({"name": "wfTaskFlag", "value": "1"});
            packet.data.data.push({"name": "procDefKey", "value": procDefKey});
            packet.data.data.push({"name": "taskDefId", "value": taskDefId});
            packet.data.data.push({"name": "isActive", "value": isActive});
            packet.data.data.push({"name": "isStart", "value": isStart});
            packet.data.data.push({"name": "procKey", "value": procKey});
        }
        // 判断是点击查询还是翻页
        core.ajax.sendPacketHtml(packet, function (response) {
            var resultDiv = $(formObj).next("div.js_page");
            if (resultDiv.length == 0) {
                resultDiv = $("<div class='js_page'></div>");
                $(formObj).after(resultDiv);
            }
            var divOver = resultDiv.css("overflow");
            resultDiv.css("overflow", "hidden");
            resultDiv.html(response);
            resultDiv.initUI();
            resultDiv.css("overflow", divOver);
            // 分页加载完成后触发回调函数
            if (successEvalJs && successEvalJs != "") {
                eval(successEvalJs);
            }
        }, true);
        packet = null;
    }
};

$(document).ready(function () {
    LeftMenu.init("wf007Left", "wf007Right", {});

    /**
     * 菜单项目点击触发其他项目取消选中效果
     */
    $(".accordion_menu li", navTab.getCurrentPanel()).bind("click", function () {
        $(".accordion_menu li", navTab.getCurrentPanel()).removeClass("selected");
        $(this).addClass("selected");
    });

    /**
     * 点击第一个项目
     */
    $(".accordion_menu li:eq(0)", navTab.getCurrentPanel()).trigger("click");
});