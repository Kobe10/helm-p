var ph013 = {
    /**
     * 查看居民详情
     * @param hsId
     */
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: ph013.changeConditions,
            showContainer: $("#ph013AdanceSch", navTab.getCurrentPanel()),
            formObj: $("#ph013QueryForm", navTab.getCurrentPanel()),
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
        var formObj = $('#ph013QueryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);

        // 查询数据
        ph013.schHs();
    },
    /**
     * 检索房产
     * @param obj
     */
    schHs: function () {
        // 查询数据
        var ph013QueryForm = $("#ph013QueryForm", navTab.getCurrentPanel());
        // 查询处理结果
        Page.query(ph013QueryForm, "");
    },


    qSchHs: function () {
        var container = $("#ph013SchDiv", navTab.getCurrentPanel());
        var formObj = $('#ph013QueryForm', container);
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


        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
        // 查询数据
        ph013.schHs();
    },

    /**
     * 查看居民文档信息
     * @param hsId 居民信息编号
     * @param hsCtId 协议编号
     */
    info: function (hsId, hsCtId) {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("lockRptType", $("input[name=lockRptType]", navTab.getCurrentPanel()).val());
        packet.url = getGlobalPathRoot() + "eland/ph/ph013/ph013-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph013HsInfo", navTab.getCurrentPanel()).html(response);
            initUI($("#ph013HsInfo", navTab.getCurrentPanel()));
        });
    },

    /**
     * 锁定居民报表类型,再次点击其他居民的时候自动跳转到对应的报表
     */
    lockRptType: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        var nowSltType = $("select[name=hsRptType]", navTab.getCurrentPanel()).val();
        $("input[name=lockRptType]", navTab.getCurrentPanel()).val(nowSltType);
        alertMsg.correct("锁定成功!");
    },

    /**
     * 生成业务报表
     * @param formId
     * @param templateName
     */
    getRpt: function () {
        // 获取居民信息容器
        var container = $("#ph013Context", navTab.getCurrentPanel());
        // 当前选择的报表类型
        var nowSltType = $("select[name=hsRptType]", container).val();
        var hsId = $("input[name=hsId]", container).val();
        var hsCtId = $("input[name=hsCtId]", container).val();
        var url = getGlobalPathRoot() + "eland/ph/ph013/ph013-getRpt.gv?fromOp=view";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("rptType", nowSltType);
        core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#ph013_ReportData", navTab.getCurrentPanel());
                container.html(response);
                container.initUI();
            }
        );
    },

    /**
     * 生成业务报表
     * @param formId
     * @param templateName
     */
    downRpt: function () {
        // 获取居民信息容器
        var container = $("#ph013Context", navTab.getCurrentPanel());
        // 当前选择的报表类型
        var nowSltType = $("select[name=hsRptType]", container).val();
        // 设置下载的文档的名称,用于保存到业务客户端;
        var nowSltTypeText = $("select[name=hsRptType]", container).find("option:selected").text();
        var hsOwnerPersons = $("input[name=hsOwnerPersons]", container).val();
        var docName = hsOwnerPersons + nowSltTypeText;
        docName = encodeURI(encodeURI(docName));
        var hsId = $("input[name=hsId]", container).val();
        var hsCtId = $("input[name=hsCtId]", container).val();
        var url = getGlobalPathRoot() + "eland/ph/ph013/ph013-getRpt.gv?fromOp=download&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&rptType=" + nowSltType
            + "&prjCd=" + getPrjCd()
            + "&docName=" + docName;
        window.open(url);
    },

    /**
     * 确认打印协议， 或者确认打印回执单。
     * @param printObj 协议 or 回执单
     */
    printRpt: function (obj, hsId, printObj, hsCtId) {
        try {
            var iframeDoc = document.getElementById("ph013DocIFrame").contentWindow.document;
            iframeDoc.getElementById("Ph013PageOfficeCtrl1").ShowDialog(4);
        } catch (e) {
            alertMsg.warn("您的浏览器不支持直接打印文档,请点击[打开文档]链接后进行打印!")
        }
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

$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("ph013SchDiv", "ph013Context", {});
});
