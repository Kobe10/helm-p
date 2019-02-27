/**
 * 人地房信息收集界面
 */
var ph000 = {


    /**
     * 查看居民详情
     * @param hsId
     */
    viewHouse: function () {
        var hsId = $("input[name=HouseInfo_hsId]", navTab.getCurrentPanel()).val();
        if (hsId != "") {
            var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
            index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
        } else {
            alertMsg.error("当前信息未保存");
        }
    },

    /**
     * 检索房产
     */
    schHs: function (successJs) {
        if (!successJs) {
            successJs = "";
        }
        // 查询数据
        var ph000QueryForm = $("#ph000QueryForm", navTab.getCurrentPanel());
        // 查询处理结果
        Page.query(ph000QueryForm, successJs);
    },

    /**
     * 快速查询按钮
     */
    qSchHs: function () {
        var container = $("#ph000SchDiv", navTab.getCurrentPanel());
        var formObj = $('#ph000QueryForm', container);
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        // 查询条件
        $("select[name=schType]", container).find("option").each(function () {
            var $this = $(this);
            var attrName = $this.val();
            var attrValue = "";
            if ($this.attr("selected")) {
                attrValue = $("input[name=schValue]", container).val();
            }
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
            if (nameIdx != -1) {
                if ($.trim(attrValue) == "") {
                    conditionName.splice(nameIdx, 1);
                    condition.splice(nameIdx, 1);
                    conditionValue.splice(nameIdx, 1);
                } else {
                    condition[nameIdx] = "like";
                    conditionValue[nameIdx] = attrValue;
                }
            } else if ($.trim(attrValue) != "") {
                conditionName.push(attrName);
                condition.push("like");
                conditionValue.push(attrValue);
            }
        });
        // 设置查询条件到表单,提交查询
        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
        // 查询数据
        ph000.schHs('');
    },

    /**
     * 调用通用查询查询房源数据
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: ph000.changeConditions,
            showContainer: $("#ph000AdanceSch", navTab.getCurrentPanel()),
            formObj: $("#ph000QueryForm", navTab.getCurrentPanel()),
            callback: function (showContainer) {
                showContainer.find("div.tip").prepend($('<div class="triangle triangle-left" style="top: 45px; left: -30px;"></div>'));
                showContainer.find("a.js-more").trigger("click");
            }
        });
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#ph000QueryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);

        // 查询数据
        ph000.schHs();
    },

    /**
     * 查看居民文档信息
     * @param hsId 居民信息编号
     * @param infoKey 子实体主键
     */
    info: function (hsId) {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("formCd", $("input[name=formCd]", navTab.getCurrentPanel()).val());
        packet.url = getGlobalPathRoot() + "eland/ph/ph000/ph000-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph000HsInfo", navTab.getCurrentPanel()).html(response);
            initUI($("#ph000HsInfo", navTab.getCurrentPanel()));
        });
    },

    /**
     * 管理附件
     * @param hsId
     * @param buildId
     */
    openOash: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openOash.gv?relType=100&relId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008-openOash", "附件管理", {mask: true, height: 600, width: 800});
    },

    /**
     * 上一户下一户
     */
    singleQuery: function (flag) {
        var hsId = $("input[name=HouseInfo_hsId]", navTab.getCurrentPanel()).val();
        var packet = new AJAXPacket();
        packet.noCover = true;
        // 提交表单查询获取下一户的信息
        var ph000QueryForm = $("#ph000QueryForm", navTab.getCurrentPanel());
        var jsonData = ph000QueryForm.serializeArray();
        jsonData.push({name: "hsId", value: hsId});
        jsonData.push({name: "flag", value: flag});
        jsonData.push({name: "prjCd", value: getPrjCd()});
        packet.data.data = jsonData;
        // 查询上一个、下一个 交房
        packet.url = getGlobalPathRoot() + "eland/ph/ph000/ph000-singleQuery.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var hsId = data.hsId;
            if (hsId) {
                ph000.info(data.hsId);
            } else {
                if (flag == 'last') {
                    alertMsg.warn("已经是第一个人");
                } else if (flag == 'next') {
                    alertMsg.warn("已经是最后一个人");
                }
            }
        });
    },

    /**
     * 同步格式化 日期类型属性
     */
    syncDateAttr: function () {
        $(":input", navTab.getCurrentPanel()).each(function () {
            var thisObj = $(this);
            if (thisObj.hasClass("date")) {
                var fmtData = thisObj.attr("formatedata");
                if (fmtData && fmtData != '' && thisObj.val()) {
                    var d = new Date(thisObj.val());
                    var parse = d.formatDate("yyyyMMdd");
                    thisObj.val(parse);
                }
            }
        });
    }
};
$(document).ready(function () {
    LeftMenu.init("ph000SchDiv", "ph000Context", {cookieName: "ph00SchTree"});
});