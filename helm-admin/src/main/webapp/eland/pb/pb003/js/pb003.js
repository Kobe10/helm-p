var pb003 = {

    /**
     * 数据删除权限
     * @returns {boolean}
     */
    getRemoveAble: function () {
        var result = false;
        var obj = $("input[name='removeAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },

    /**
     * 数据修改权限
     * @returns {boolean}
     */
    getEditAble: function () {
        var result = false;
        var obj = $("input[name='editAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },

    //初始化左侧目录树
    initPayTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("objValue", "");
        packet.url = getGlobalPathRoot() + "eland/pb/pb003/pb003-treeInfo.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjOrgTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: pb003.clickNode
                            //beforeDrop: pb003.beforeDrop
                        },
                        view: {
                            addHoverDom: pb003.addHoverDom,
                            removeHoverDom: pb003.removeHoverDom,
                            selectedMulti: false
                        },
                        edit: {
                            drag: {
                                isCopy: false
                                //isMove: pb003.getMoveAble()
                            },
                            //enable: pb003.getMoveAble(),
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#pb003Tree", navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 1;
                    }, true);
                    pb003.initP(rootNode.id);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },

    /**
     * 光标移动到 节点上的事件
     */
    addHoverDom: function (event, treeNode) {
        //console.log(treeNode);
        var divArea = navTab.getCurrentPanel();
        var sObj = $("#" + treeNode.tId + "_span", divArea);

        if (treeNode.id != '1' && pb003.getRemoveAble() && $("#removeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 remove' id='removeBtn_" + treeNode.tId + "' title='删除外聘单位' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                alertMsg.confirm("您确定要删除该公司吗?", {
                    okCall: function () {
                        pb003.removeReg("pb003Tree", treeNode);
                    }
                });
                return false;
            });
        }
        if (treeNode.id != '1' && pb003.getEditAble() && $("#eidtRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 edit' id='eidtRegBtn_" + treeNode.tId + "' title='修改外聘单位' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#eidtRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                pb003.initUpdate(treeNode.id);
                return false;
            });
        }
        if (treeNode.id == '1' && pb003.getEditAble() && $("#addRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 add' id='addRegBtn_" + treeNode.tId + "' title='新增外聘单位' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                pb003.addReg(treeNode.tId);
                return false;
            });
        }
    },

    /**
     * 修改公司
     * @param upRegId 上级外聘单位节点
     * @param regId
     */
    initUpdate: function (regId) {
        if (regId == 0) {
            return;
        }
        var prjCd = getPrjCd();
        var url = getGlobalPathRoot() + "eland/pb/pb003/pb00302-initUpdate.gv?prjCd=" + prjCd + "&extCmpId=" + regId;
        $.pdialog.open(url, "pb00302", "外聘单位信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },

    /**
     * 保存
     *
     */
    save: function () {
        var $form = $("#pb003frm", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/pb/pb003/pb00302-save.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $.pdialog.closeCurrent();
                    pb003.initPayTree();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 添加公司
     * @param upRegId 上级外聘单位节点
     * @param regId
     */
    addReg: function (regId) {
        if (regId == 0) {
            return;
        }
        var prjCd = getPrjCd();
        var url = getGlobalPathRoot() + "eland/pb/pb003/pb00302-initUpdate.gv?prjCd=" + prjCd;
        $.pdialog.open(url, "pb003032", "外聘单位信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },

    /**
     * 删除节点
     * @param treeId 删除节点
     * @param treeNode
     */
    removeReg: function (treeId, treeNode) {
        var packet = new AJAXPacket();
        // 当前节点信息
        packet.data.add("extCmpId", treeNode.id);
        // 外聘单位归属项目
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/pb/pb003/pb00302-delete.gv";
        //处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.info("删除成功！");
                pb003.initPayTree();
            } else {
                alertMsg.error(data.errMsg);
                return false;
            }
        }, true);

    },

    /**
     * 移开光标删除事件
     * @param treeId  树编号
     * @param treeNode 选择节点编号
     */
    removeHoverDom: function (treeId, treeNode) {
        $("#removeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#addRegBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#eidtRegBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
    },

    /**
     * 左侧外聘单位树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        if (treeNode.id != '1') {
            pb003.initP(treeNode.id);
        }
    },

    /**
     * 显示右侧页面
     * @param regId
     */
    initP: function (extCmpId) {
        var packet = new AJAXPacket();
        packet.data.add("extCmpId", extCmpId);
        packet.data.add("prjCd", getPrjCd());
        packet.url = getGlobalPathRoot() + "eland/pb/pb003/pb003-initData.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#pb003Context", navTab.getCurrentPanel()).html(response);
            initUI($("#pb003Context", navTab.getCurrentPanel()));
        })
    },

    /**
     * 加载tab页的内容
     * @param obj
     */
    loadTabContext: function (obj) {
        var _this = $(obj);
        var url = _this.attr("url");
        var urlData = getGlobalPathRoot() + encodeURI(encodeURI(url));
        var contextDiv = _this.closest("div.tabs").find("div.tabsContent").find(">div:eq(" + _this.index() + ")");
        if (contextDiv.html() == "") {
            var packet = new AJAXPacket(urlData);
            packet.noCover = true;
            packet.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(packet, function (response) {
                contextDiv.html(response);
                initUI(contextDiv);
            })
        }
        var spanText = _this.find("span").text();
        if (spanText != '待支付院落') {
            $("span.js_PayButton").hide();
        } else {
            $("span.js_PayButton").show();
        }
    },

    /**
     * 支付勾选院落
     * @param obj
     */
    paySelectBuild: function (obj, extCmpId) {
        var itemCds = [];
        $(obj).closest("div.tabs").find("div.tabsContent").find(">div:eq(0)").find(":checkbox[checked][name=buildId]").each(
            function (i) {
                itemCds.push($(this).val());
            }
        );

        //判断如果勾选了。就打开支付页面。
        if (itemCds.length > 0) {
            var url = getGlobalPathRoot() + "eland/pb/pb003/pb003-initPay.gv?buildId=" + itemCds + "&extCmpId=" + extCmpId + "&prjCd=" + getPrjCd();
            $.pdialog.open(url, "pb003", "支付院落费用", {
                mask: true,
                max: false,
                maxable: false,
                resizable: true,
                width: 800,
                height: 450
            });
        } else {
            alertMsg.warn("请选择要支付院落");
        }
    },

    //改变全选框
    checkAllBox: function (obj) {
        var $this = $(obj);
        var $div = $this.closest("div.grid");
        var groups = $this.attr("group");
        var checked = $this.is(":checked") ? "all" : "none";
        $div.find(":checkbox[group=" + groups + "]").each(
            function (i) {
                if ($(this).hasClass("checkAllInList")) {
                    return true;
                } else if ("all" == checked) {
                    $(this).attr('checked', "true");
                } else {
                    $(this).removeAttr('checked');
                }
            }
        );
        pb003.counts(obj);
    },

    //计算支付页面所需数据
    counts: function (obj) {
        var fixedServiceFee = $("input[name=fixedServiceFee]").val();
        var totalZhFee = 0.0;
        var feeCount = 0.0;   //总服务费 = 固定服务费 + 总走户费
        var hsCount = 0;
        var buildCount = 0;

        //loop every input,get value
        $(obj).closest("div.js_allContent").find(":checkbox[checked][name=buildId]").each(
            function (i) {
                var _thisTr = $(this).closest("tr");
                buildCount++;
                var everyZhFee = $("input[name=everyZhFee]", _thisTr).val();
                if (everyZhFee != '') {
                    totalZhFee += parseFloat(everyZhFee);
                    feeCount += parseFloat(everyZhFee);
                }

                var everyBdHsCount = $("input[name=everyBdHsCount]", _thisTr).val();
                if (everyBdHsCount != '') {
                    hsCount += parseInt(everyBdHsCount);
                }
            }
        );

        $("input[name=totalZhFee]", $.pdialog.getCurrent()).val(totalZhFee);
        //总服务费 = 固定服务费 + 总走户费
        if (fixedServiceFee == '') {
            fixedServiceFee = 0;
        }
        $("input[name=feeCount]", $.pdialog.getCurrent()).val(feeCount + parseInt(fixedServiceFee));
        $("input[name=buildCount]", $.pdialog.getCurrent()).val(buildCount);
        $("input[name=hsCount]", $.pdialog.getCurrent()).val(hsCount);
    },

    //计算 固定服务费
    fixedServiceFee: function () {
        var fromDate = $("input[name=fromDate]").val();
        var toDate = $("input[name=toDate]").val();
        //alert("--fromDate-->" + fromDate + "--toDate->" + toDate);

        var iDays = pb003.DateDiff(fromDate, toDate);
        if (iDays) {
            $("input[name=fixedServiceFee]").val(parseInt(iDays) * 180000);
        } else {
            $("input[name=fixedServiceFee]").val(0);
        }
        //调用 修改总服务费
        var buildId = $("input[name=buildId]");
        pb003.counts(buildId);
    },

    //确认支付
    payCfm: function (obj) {
        var $thisDiv = $(obj).closest("div.js_allContent");
        var fixedServiceFee = $("input[name=fixedServiceFee]", $thisDiv).val();
        var totalZhFee = $("input[name=totalZhFee]", $thisDiv).val();
        var feeCount = $("input[name=feeCount]", $thisDiv).val();
        var buildCount = $("input[name=buildCount]", $thisDiv).val();
        var fromDate = $("input[name=fromDate]", $thisDiv).val();
        var toDate = $("input[name=toDate]", $thisDiv).val();
        var hsCount = $("input[name=hsCount]", $thisDiv).val();

        var buildNum = 0;
        var buildMap = {};
        $("tr.js_everyBuildTr").find(":checkbox[checked][name=buildId]").each(function (e) {
            var $tr = $(this).closest("tr.js_everyBuildTr");
            if ($tr) {
                buildNum++;
                var buildId = $(this).val();
                var everyZhFee = $tr.find("input[name=everyZhFee]").val();
                buildMap[buildId] = everyZhFee;
            }
        });

        if (buildNum == 0) {
            alertMsg.warn("请选择支付院落！");
        } else {
            var extCmpId = $("input[name=extCmpId]", $.pdialog.getCurrent()).val();
            if (!extCmpId) {
                return false;
            }
            //异步提交后台
            var url = getGlobalPathRoot() + "eland/pb/pb003/pb003-savePay.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("extCmpId", extCmpId);
            packet.data.add("fixedServiceFee", fixedServiceFee);
            packet.data.add("totalZhFee", totalZhFee);
            packet.data.add("feeCount", feeCount);
            packet.data.add("buildCount", buildCount);
            packet.data.add("fromDate", fromDate);
            packet.data.add("toDate", toDate);
            packet.data.add("hsCount", hsCount);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("buildMap", JSON.stringify(buildMap));

            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("支付成功！");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.warn("支付失败！" + jsonData.errMsg);
                }
            });
        }
    },

    //查看支付记录详情
    viewPayDetail: function (payRecordId) {
        var url = getGlobalPathRoot() + "eland/pb/pb003/pb003-viewPayDetail.gv?payRecordId=" + payRecordId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pb003", "支付记录详情", {
            mask: true,
            max: false,
            maxable: false,
            resizable: true,
            width: 800,
            height: 230
        });
    },

    //计算两个日期相差几个月
    DateDiff: function (sDate1, sDate2) {  //sDate1和sDate2是yyyy-MM-dd格式
        var aDate, bDate, oDate1, oDate2, iDays;
        aDate = sDate1.split("-");
        oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);  //转换为 国际标准时间 格式
        bDate = sDate2.split("-");
        oDate2 = new Date(bDate[1] + '-' + bDate[2] + '-' + bDate[0]);
        iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24 / 30); //把相差的毫秒数转换为月数
        //return iDays;  //返回相差天数

        //两个日期
        // 拆分年月日
        var Date1 = sDate1.split('-');
        // 得到月数
        Date1 = parseInt(Date1[0]) * 12 + parseInt(Date1[1]);
        // 拆分年月日
        var Date2 = sDate2.split('-');
        // 得到月数
        Date2 = parseInt(Date2[0]) * 12 + parseInt(Date2[1]);
        var m = Date1 - Date2;
        if (m > 0) {
            alertMsg.error("支付周期输入有误!");
            return false;
        }
        //var m = Math.abs(sDate1 - sDate2);
        return Math.abs(m);  //返回相差月份
    },

    //打开院落详情
    openBuilding: function (buldId) {
        var url = "eland/ph/ph002/ph002-init.gv?buldId=" + buldId;
        index.quickOpen(url, {opCode: "pb002-init", opName: "院落详情", fresh: true});
    }
};

$(document).ready(function () {
    pb003.initPayTree();
    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var leftDiv = $("#pb003TreePanel", navTab.getCurrentPanel());
                var rightDiv = $("#pb003Context", navTab.getCurrentPanel());
                var newWidth = $(moveObj).position().left;
                leftDiv.width($(moveObj).position().left);
                rightDiv.css("margin-left", (newWidth + 10) + "px");
                var resizeGrid = $("div.j-resizeGrid", rightDiv);
                if (resizeGrid.length > 0) {
                    resizeGrid.jResize();
                }
                $(moveObj).css("left", 0);
            }
        });
    });
    $("span.split_but", navTab.getCurrentPanel()).mousedown(function (event) {
        stopEvent(event);
    });
    $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
        var splitLine = $(this).parent();
        var leftMenu = splitLine.prev("div.left_menu");
        if (leftMenu.is(":visible")) {
            leftMenu.hide();
            splitLine.addClass("menu_hide");
            var rightDiv = $("#pb003Context", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#pb003Context", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });
});
