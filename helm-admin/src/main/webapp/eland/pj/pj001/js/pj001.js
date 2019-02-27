var pj001 = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        // 获取当前选中的组织树类型，传递项目编号获取组织树
        packet.data.add("prjCd", "0");
        packet.url = getGlobalPathRoot() + "eland/pj/pj001/pj001-prjTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var treeJson = jsonData.resultMap.treeJson;
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var prjTreeSetting = {
                    data: {
                        simpleData: {
                            enable: true
                        }
                    },
                    view: {
                        selectedMulti: false
                    },
                    callback: {
                        onClick: pj001.clickNode
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#pj001Tree", navTab.getCurrentPanel()), prjTreeSetting, treeJson);
                // 显示根节点数据
                var firstPrjNode = zTree.getNodesByFilter(function (node) {
                    if (node.prjCd) {
                        return true;
                    } else {
                        return false;
                    }
                }, true);
                if (firstPrjNode != null) {
                    zTree.selectNode(firstPrjNode);
                    pj001.clickNode(null, "pj001Tree", firstPrjNode);
                } else {
                    pj001.prjInfo("");
                }
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        }, true, false);
    },

    /**
     * 点击公司节点
     * @param event
     * @param treeId
     * @parma treeNode
     */
    clickNode: function (event, treeId, treeNode) {
        // 点击的是项目则展示项目编号
        if (treeNode.prjCd) {
            pj001.prjInfo(treeNode.prjCd);
        }
    },

    /**
     * 项目编号
     * @param prjCd
     */
    prjInfo: function (prjCd) {
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-init.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#pj001RightDiv", navTab.getCurrentPanel()).html(response);
            initUI($("#pj001RightDiv", navTab.getCurrentPanel()));
        }, true);
    }
}

/**
 * 界面初始化加载
 */
$(document).ready(function () {

    // 执行目录树初始化
    pj001.initFullTree();

    // 拖动屏幕切分效果
    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var leftDiv = $("#pj001TreeDiv", navTab.getCurrentPanel());
                var rightDiv = $("#pj001RightDiv", navTab.getCurrentPanel());
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
    /**
     * 快速查询绑定回车事件
     */
    $("#pj001QSchDialog input", navTab.getCurrentPanel()).bind("keydown", function () {
        var $this = $(this);
        if (event.keyCode == 13) {
            if ($this.val() != "") {
                $("#pj001QSchDialog button.js_query", navTab.getCurrentPanel()).trigger("click");
            } else {
                event.keyCode = 9;
            }
        }
    });
    $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
        var splitLine = $(this).parent();
        var leftMenu = splitLine.prev("div.left_menu");
        var rightDiv = $("#pj001RightDiv", navTab.getCurrentPanel());
        if (leftMenu.is(":visible")) {
            leftMenu.hide();
            splitLine.addClass("menu_hide");
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });
});