/**
 * Created by shfb_wang on 2015/5/13.
 */
var pb004 = {

    //初始化左侧区域树
    initFullRegTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", "1");
        packet.data.add("objValue", "");
        packet.url = getGlobalPathRoot() + "eland/pj/pj004/pj004-treeInfo.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var regTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: pb004.clickNode
                        },
                        view: {
                            //addHoverDom: pb004.addHoverDom,
                            //removeHoverDom: pb004.removeHoverDom,
                            selectedMulti: false
                        },
                        edit: {
                            //drag: {
                            //    isCopy: false,
                            //    isMove: pb004.getMoveAble()
                            //},
                            //enable: pb004.getMoveAble(),
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#pb004Tree", navTab.getCurrentPanel()), regTreeSetting, treeJson);


                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    //初始化之后查询 根节点的 数据
                    pb004.initSummary(rootNode.id);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },

    /**
     * 展开所有节点。折叠所有节点
     * @param obj
     */
    extendOrClose: function (obj) {
        var $span = $(obj);
        var zTree = $.fn.zTree.getZTreeObj("pb004Tree");
        if ($span.html() == "展开") {
            zTree.expandAll(true);
            $span.html("折叠");
        } else {
            zTree.expandAll(false);
            $span.html("展开");
        }
    },

    //点击树形节点
    clickNode: function (event, treeId, treeNode) {
        pb004.initSummary(treeNode.id);
    },

    /**
     * /**
     * 左侧区域树点击事件
     * 初始化查询
     * @param rhtRegId
     */
    initSummary: function (rhtRegId) {
        //赋值 数据过滤
        $("input[name=rhtRegId]", navTab.getCurrentPanel()).val(rhtRegId);
        var form2 = $('#pb004frm', navTab.getCurrentPanel());
        Page.query(form2, "pb004.adjustHeight()");

        //var build = $("input[name=buildId]", navTab.getCurrentPanel());
        pb004.sltSomeOne();
    },

    //改变全选框
    checkAllBox: function (obj) {
        var checkIdStr = $("input[name=cancelCheckIds]", navTab.getCurrentPanel()).val();
        var checkIds = [];
        if (checkIdStr != "") {
            checkIds = checkIdStr.split(",");
        }
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
                    var checkedVal = $(this).val();
                    for (var idx = 0; idx < checkIds.length; idx++) {
                        if (checkedVal == checkIds[idx]) {
                            checkIds.splice(idx, 1);
                            break;
                        }
                    }
                } else {
                    $(this).removeAttr('checked');
                    var inIdx = $.inArray(checkIds, $(this).val());
                    if ($(this).val() != '' && inIdx < 0) {
                        checkIds.push($(this).val());
                    }
                }
            }
        );
        $("input[name=cancelCheckIds]", navTab.getCurrentPanel()).val(checkIds.join(","));
        pb004.sltSomeOne(obj);
    },

    /**
     * 勾选不同院落时查询不同
     */
    sltSomeOne: function (obj) {
        var $this = $(obj);

        //获取当前查询条件。调用自助查询，返回结果，--->保持查询条件维持不变。
        var formObj = $('#pb004frm', navTab.getCurrentPanel());

        var entityName = $("input[name=entityName]", formObj).val();
        var conditionName = $("input[name=conditionName]", formObj).val();
        var condition = $("input[name=condition]", formObj).val();
        var conditionValue = $("input[name=conditionValue]", formObj).val();
        var resultField = $("input[name=resultField]", formObj).val();
        var sortColumn = $("input[name=sortColumn]", formObj).val();
        var sortOrder = $("input[name=sortOrder]", formObj).val();
        var regUseType = $("input[name=regUseType]", formObj).val();
        var rhtRegId = $("input[name=rhtRegId]", formObj).val();

        var url = getGlobalPathRoot() + "eland/pb/pb004/pb004-initReport.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("entityName", entityName);
        packet.data.add("conditionName", conditionName);
        packet.data.add("condition", condition);
        packet.data.add("conditionValue", conditionValue);
        packet.data.add("resultField", resultField);
        packet.data.add("orderAttrsName", sortColumn);
        packet.data.add("orderAttrsOrder", sortOrder);
        packet.data.add("regUseType", regUseType);
        packet.data.add("rhtRegId", rhtRegId);

        //单独处理去勾选的
        var buildIds = $this.val();
        if (!$this.hasClass("checkAllInList")) {
            var cancelCheckIds = $("input[name=cancelCheckIds]", navTab.getCurrentPanel()).val();
            if (buildIds) {
                if (cancelCheckIds) {
                    //点击勾选之后如果存在去掉，如果未勾选就勾选。
                    var rmOrAdd = false;
                    var tempIds = cancelCheckIds.split(",");
                    for (var i = 0; i < tempIds.length; i++) {
                        if (tempIds[i] === buildIds) {
                            tempIds.splice(i, 1);
                            rmOrAdd = true;
                        }
                    }
                    if (rmOrAdd) {
                        cancelCheckIds = tempIds.join(",");
                    } else {
                        cancelCheckIds = cancelCheckIds + "," + buildIds;
                    }
                } else {
                    cancelCheckIds = buildIds;
                }
                $("input[name=cancelCheckIds]", navTab.getCurrentPanel()).val(cancelCheckIds);
            }
            packet.data.add("buildIds", cancelCheckIds);
        }

        core.ajax.sendPacketHtml(packet, function (response) {
            $("#pb00402Context", navTab.getCurrentPanel()).html(response);
            initUI($("#pb00402Context", navTab.getCurrentPanel()));
        });
    },

    //调用分页查询之后如果已取消。去掉勾选。
    findUnCheck: function () {
        var thisDiv = $("#pb004List", navTab.getCurrentPanel());
        var checkIds = [];
        checkIds = $("input[name=cancelCheckIds]", navTab.getCurrentPanel()).val().split(",");
        for (var i = 0; i < checkIds.length; i++) {
            thisDiv.find("input[name=buildId]").each(function () {
                var _this = $(this);
                if (checkIds[i] === _this.val()) {
                    _this.attr("checked", false);
                }
            });
        }
    },

    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryBuild: function (obj) {
        Page.queryCondition(obj, {
            width: "100%",
            changeConditions: pb004.changeConditions,
            formObj: $("#pb004frm", navTab.getCurrentPanel())
        });
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#pb004frm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        // 刷新建筑
        Page.query(formObj, "pb004.adjustHeight()");
    },
    adjustHeight: function () {
        var treeH = $("#pb004DivTree", navTab.getCurrentPanel()).height();
        var summaryH = $("#pb00402Context", navTab.getCurrentPanel()).height();
        var height = treeH - summaryH - 115;
        var gridScroller = $("div.js_page", navTab.getCurrentPanel()).find("div.gridScroller");
        gridScroller.css("height", height + "px");
        gridScroller.css("width", gridScroller.prev("div.gridHeader").width());
    }
};

$(document).ready(function () {
    pb004.initFullRegTree();

    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var leftDiv = $("#pb004DivTree", navTab.getCurrentPanel());
                var rightDiv = $("#pb004RightDiv", navTab.getCurrentPanel());
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
            var rightDiv = $("#pb004RightDiv", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#pb004RightDiv", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });
});