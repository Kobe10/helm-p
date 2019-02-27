index = {
    isFull: null,
    ch001Countdown: null,
    ch001CountTime: null,
    /**
     * 调整项目
     */
    changeProject: function () {
        $("#changePrjForm").submit();
    },

    /**
     * 动态树打开tab页面
     *
     * @param event
     *            点击时间
     * @param treeId
     *            树节点ID
     * @param treeNode
     *            树节点对象
     */
    funcTreeOnClick: function (event, treeId, treeNode) {
        if ("#" == treeNode.nvUrl) {

        } else {
            var url = treeNode.nvUrl;
            var opCode = treeNode.opCode;
            var opName = treeNode.name;
            var privilegeId = treeNode.id;
            var prjCd = getPrjCd();
            if (url.lastIndexOf("?") == -1) {
                url = url + "?";
            }
            index.openTab(opCode, url + "&opCode=" + opCode + "&prjCd=" + prjCd + "&privilegeId=" + privilegeId, opCode, opName);
        }
    },

    /**
     * 加载首页面板
     * @param privilegeId 面板权限编号
     * @param rhtUrl 面板URL
     * @param opCode 面板编码
     * @param opName 面板名称
     */
    loadPanel: function (privilegeId, rhtUrl, opCode, opName) {
        if (rhtUrl == "" || rhtUrl == "#") {
            return;
        }
        var urlData = getGlobalPathRoot() + encodeURI(encodeURI(rhtUrl));
        var packet = new AJAXPacket(urlData);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("privilegeId", privilegeId);
        packet.noCover = true;
        core.ajax.sendPacketHtml(packet, function (response) {
            var contextDiv = $("#myWorkBench").find("div.js_panel_" + privilegeId).find("div.js_panel_context");
            contextDiv.html(response);
            initUI(contextDiv);
        })
    },

    /**
     * 信息展示页面面板内容加载
     * @param panelKey 展示内容的URL地址
     * @param rhtUrl 页面打开地址
     */
    loadInfoPanel: function (panelKey, rhtUrl) {
        var urlData = getGlobalPathRoot() + encodeURI(encodeURI(rhtUrl));
        var packet = new AJAXPacket(urlData);
        packet.noCover = true;
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var contextDiv = $("div.panelContainer[panelKey=" + panelKey + "]");
            contextDiv.html(response);
            initUI(contextDiv);
        })
    },

    /**
     * 使用新的URL地址替换点击对象所在的面板
     * @param clickObj 点击的对象
     * @param rhtUrl 打开的URL地址
     */
    loadInfoPanelByInnerObj: function (clickObj, rhtUrl) {
        var container = $(clickObj).parents("div.panelContainer");
        if (container.length == 0) {
            container = $(clickObj).parents("div.panel").parent();
        }
        index.loadInfoPanel(container.attr("panelKey"), rhtUrl);
    },

    /**
     * 打开导航Tab页面
     *
     * @param obj 点击的对象
     * @param privilegeId
     *            权限ID
     * @param url
     *            界面URL
     * @param opCode
     *            操作代码
     * @param opName
     *            操作名称
     */
    openTab: function (obj, privilegeId, _url, opCode, opName) {
        if (_url == "#") {
        } else if (_url.indexOf("href:") == 0) {
            var _url = _url.substr(5);
            if (_url.indexOf("http") < 0) {
                _url = getGlobalPathRoot() + _url;
            }
            if (_url.indexOf("?") < 0) {
                _url = _url + "?"
            }
            if (_url.indexOf("prjCd=") < 0) {
                _url = _url + "&prjCd=" + getPrjCd();
            }
            window.open(_url);
        } else {
            var url = getGlobalPathRoot() + _url;
            if(_url.indexOf("/") == 0) {
                url = getGlobalPathRoot().substr(1, getGlobalPathRoot().length) + _url
            }
            var data = {opCode: opCode, prjCd: getPrjCd(), privilegeId: privilegeId};
            navTab.openTab(opCode, url, {title: opName, fresh: false, data: data});
            // 切换当前选中的tab页面
            var selectedObj;
            if ($(obj).hasClass("top_li")) {
                selectedObj = $(obj);
            } else {
                selectedObj = $(obj).parents("li.top_li");
            }
            //$(selectedObj).siblings("li.current").removeClass("current");
            //selectedObj.addClass("current");
        }
    },

    /**
     * 快速打开Tab页面
     */
    quickOpen: function (_url, jsonData) {
        if (!jsonData) {
            jsonData = {};
        }
        if (jsonData.dialog) {
            index.isFull = true;
        }
        if (index.isFull) {
            if (jsonData.opCode !== "ph003-init") {
                alertMsg.warn("全屏地图页只能打开一个页签");
                return
            }
        }
        if (_url == "#") {
        } else if (_url.indexOf("href:") == 0) {
            var _url = _url.substr(5);
            alert(_url);
            window.open(_url);
        } else {
            // 连接地址
            var url = getGlobalPathRoot() + _url;
            var data = $.extend({opCode: "", prjCd: getPrjCd(), fresh: true}, jsonData);
            navTab.openTab(data.opCode, url, {title: data.opName, data: data, fresh: data.fresh});
            // 切换当前选中的tab页面
            var obj = null;
            if (obj) {
                var selectedObj;
                if ($(obj).hasClass("top_li")) {
                    selectedObj = $(obj);
                } else {
                    selectedObj = $(obj).parents("li.top_li");
                }
                $(selectedObj).siblings("li.current").removeClass("current");
                selectedObj.addClass("current ");
            }
        }
    },

    /**
     * 打开账号设置界面
     */
    openSetting: function () {
        var url = getGlobalPathRoot() + "oframe/main/main-setting.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "setting", "我的设置",
            {mask: true, max: false, maxable: false, resizable: false, width: 700, height: 500});
    }
    ,

    /**
     * 切换皮肤
     */

    switchSkin: function () {
        $.fn.extend({
            theme: function (options) {
                var op = $({themeBase: "themes"}, options);
                var _themeHref = op.themeBase + "/#theme#/style.css";
                return this.each(function () {
                    var jThemeLi = $(this).find(">li[theme]");
                    var setTheme = function (themeName) {
                        $("head").find("link[href$='style.css']").attr("href", _themeHref.replace("#theme#", themeName));
                        jThemeLi.find(">div").removeClass("selected");
                        jThemeLi.filter("[theme=" + themeName + "]").find(">div").addClass("selected");

                        if ($.isFunction($.cookie)) $.cookie("theme", themeName);
                    };

                    jThemeLi.each(function () {
                        var $this = $(this);
                        var themeName = $this.attr("theme");
                        $this.addClass(themeName).click(function () {
                            setTheme(themeName);
                        });
                    });

                    if ($.isFunction($.cookie)) {
                        var themeName = $.cookie("theme");
                        if (themeName) {
                            setTheme(themeName);
                        }
                    }

                });
            }
        });
    }
    ,

    /**
     * 导航条
     **/
    adjustNav: function () {
        var $oUl = $('#sf-menu');
        var rightAttr = parseFloat($oUl.attr("rightAttr"));
        var $header = $('#header');
        var $goLeft = $('.goleft', $header);
        var $goRight = $('.goright', $header);
        var headerLog = $(".headerNav", $header);
        var $oUlLis = $oUl.find(">li");
        var ulNeedLength = $oUlLis.length * $oUlLis.eq(0).width();
        var containerWidth = $header.width();
        if ((ulNeedLength + headerLog.width()) > containerWidth) {
            headerLog.hide();
        } else {
            headerLog.show();
        }
        $oUl.width(ulNeedLength);
        if (ulNeedLength > containerWidth) {
            var minusWidth = containerWidth - ulNeedLength;
            var newRightWidth = minusWidth + rightAttr - 20;
            // 是否显示右侧移动按钮
            if (newRightWidth > 20) {
                newRightWidth = 20;
                $oUl.attr("rightAttr", 0 - minusWidth);
                $goRight.hide();
            } else {
                $goRight.show();
            }
            if (newRightWidth < minusWidth) {
                newRightWidth = minusWidth - 20;
                $oUl.attr("rightAttr", "0");
                $goLeft.hide();
            } else {
                $goLeft.show();
            }
            $oUl.css("right", newRightWidth + "px");
        } else {
            $oUl.attr("rightAttr", "0");
            $oUl.css("right", "0px");
            $goLeft.hide();
            $goRight.hide();
        }
    }
    ,

    /**
     * 导航条
     **/
    navMove: function (direct) {
        var $oUl = $('#sf-menu');
        var rightAttr = parseFloat($oUl.attr("rightAttr"));
        var moveWidth = $oUl.find(">li").eq(0).width();
        $oUl.attr("rightAttr", rightAttr + direct * moveWidth);
        index.adjustNav();
    }
    ,


    /**
     * 字体大小调整
     */
    bigger: function (obj) {
        var _this = $(obj);
        //获取字体大小
        var fontSize = $(".page").css("font-size");
        if (fontSize) {
            var textFontSize = parseInt(fontSize.substring(0, fontSize.length - 2));
            if (_this.hasClass("bigger")) {
                textFontSize = textFontSize + 1;
            } else {
                textFontSize = textFontSize - 1;
            }
            if (textFontSize >= 12 && textFontSize <= 18) {
                $("#fontStyle").attr("href", getGlobalPathRoot() + "oframe/themes/common/font-" + textFontSize + ".css");
                index.adjustNav();
                setPCookie("fontSize", textFontSize, getGlobalPathRoot() + "/oframe/main/main-init.gv");
            }
        }
    }
    ,

    /**
     * 查询子系统菜单树
     *
     * @param obj
     * @parame privilegeId
     */
    queryMenuTree: function (pId, url) {
        //继续请求
        //if navUrl存在，不处理
        var clickItemId = "top_" + pId;
        var ulContainer = $("#" + clickItemId).parent().find("ul:eq(0)");
        var loadingMsg = "loading...";
        if (ulContainer.html() && (ulContainer.html() != "" && ulContainer.html() != loadingMsg)) {
            return;
        }
        // 到controller中取数据回来调用画树函数
        var url = getGlobalPathRoot() + "oframe/main/main-menuTree.gv";
        var packetTree = new AJAXPacket(url);
        packetTree.noCover = true;
        packetTree.data.add("pOpCode", pId);
        packetTree.data.add("prjCd", getPrjCd());
        packetTree.errorFunction = function (data) {
        };
        ulContainer.html(loadingMsg);
        core.ajax.sendPacketHtml(packetTree, function (response) {
            ulContainer.html(response);
        });
        packetTree = null;
    }
    ,

    /**
     * 修改用户口令
     */
    chgPwd: function () {
        var $form = $("#settingpwdform", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/main/main-chgPwd.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    $($form)[0].reset();
                    alertMsg.correct("处理成功");
                } else {
                    alertMsg.warn(data.errMsg);
                }
            });
        }
    }
    ,

    /**
     * 保存员工信息
     */
    saveStaff: function () {
        var $form = $("#settingbaseform", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/main/main-saveStaff.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
    ,
//修改员工组织
    editOrg: function (obj) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-orgTree.gv";
        $(obj).openTip({href: url, height: "300", width: "250", offsetX: 0, offsetY: 30});
    }
    ,
    /**
     * 保存面板布局
     */
    savePanel: function () {
        // 获取所有选择的面板
        var choosePanelArr = [];
        $("#settingPanelDiv", $.pdialog.getCurrent()).find("div.js_panel_choose").each(function () {
            choosePanelArr.push($(this).attr("rhtId"));
        });
        // 提交页面数据
        var url = getGlobalPathRoot() + "oframe/main/main-savePanel.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("rhtIds", choosePanelArr.join(","));
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("保存成功");
                navTab._reload(navTab._getTab(navTab._op.mainTabId), true);
                $.pdialog.closeCurrent();
            } else {
                alertMsg.error(data.errMsg);
            }
        });

    }
    ,
    /**
     * 删除面板
     * @param clickObj 点击的删除对象
     */
    removePanel: function (clickObj) {
        $(clickObj).closest("div.js_panel_choose").remove();
        initUI($("div.js_panel_choose"));
    }
    ,

    /**
     * 初始化化面板树
     */
    initSettingTree: function () {
        // 到controller中取数据回来调用画树函数
        var panelStaffCode = $("input[name=panelStaffCode]", $.pdialog.getCurrent).val();
        var url = getGlobalPathRoot() + "oframe/main/main-settingTree.gv?panelStaffCode=" + panelStaffCode;
        var packetTree = new AJAXPacket(url);
        packetTree.data.add("prjCd", getPrjCd());
        packetTree.noCover = true;
        core.ajax.sendPacketHtml(packetTree, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var rhtTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            beforeCheck: index.beforeCheck,
                            onCheck: index.panelCheck
                        },
                        check: {
                            autoCheckTrigger: false,
                            chkDisabled: true,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "", "N": ""}
                        }
                    };
                    // 初始化目录树
                    var settingMbRhtTree = $.fn.zTree.init($("#settingMbRhtTree"), rhtTreeSetting, treeJson);
                    var nodes = settingMbRhtTree.transformToArray(settingMbRhtTree.getNodes());
                    var checkedPanel = $("div.js_panel_choose", $.pdialog.getCurrent);
                    for (var i = 0; i < nodes.length; i++) {
                        var node = nodes[i];
                        for (var j = 0; j < checkedPanel.length; j++) {
                            var checked = $(checkedPanel[j]);
                            if (node.id == checked.attr("id")) {
                                node.checked = "true";
                                settingMbRhtTree.updateNode(node);
                            }
                        }
                    }
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        );
    }
    ,

//勾选之前判断是否允许勾选
    beforeCheck: function (treeId, treeNode) {
        //根节点不允许勾选
        if (treeNode.href == '#' || treeNode.href == '') {
            return false;
        }
    }
    ,

// 勾选多选框。
    panelCheck: function (event, treeId, treeNode) {
        var rootPanelDiv = $("#settingPanelDiv", $.pdialog.getCurrent());
        if (treeNode.checked) {
            //勾选，添加到右侧。
            var hiddenDiv = $("div:first", $(rootPanelDiv));
            var addPanelDiv = hiddenDiv.clone();
            $(addPanelDiv).appendTo(rootPanelDiv.last("div")).attr({
                id: treeNode.id,
                rhtId: treeNode.id
            }).addClass("inUsePanel js_panel_choose").removeClass("hidden").attr("style", "display: block");
            $(addPanelDiv).children("label").html(treeNode.name);
            rootPanelDiv.addSortDrag({}, addPanelDiv);
        } else {
            var result = $("#" + treeNode.id + ".js_panel_choose", $.pdialog.getCurrent());
            $(result).remove();
            //取消勾选，删除右侧div
        }
    }
    ,

    /**
     * 启动流程
     * @param procDefKey 流程定义名称
     * @param otherJsonData 流程启动参数
     */
    startWf: function (procDefKey, prjCd, otherJsonData) {
        var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-startWf.gv?";
        var urlParam = "procDefKey=" + procDefKey + "&prjCd=" + prjCd;
        if (otherJsonData) {
            for (var item in otherJsonData) {
                urlParam = urlParam + "&" + item + "=" + otherJsonData[item];
            }
        }
        window.open(url + urlParam, "启动流程");
    }
    ,

    /**
     * 查看流程
     * @param procInsId 流程实例ID
     */
    viewWf: function (procInsId) {
        var iWidth = $(window).width() * 0.6; //弹出窗口的宽度;
        var iHeight = $(window).height() - 100; //弹出窗口的高度;
        var iTop = (window.screen.availHeight - 30 - iHeight) / 2; //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth - 10 - iWidth) / 2; //获得窗口的水平位置;
        var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-viewWf.gv?procInsId=" + procInsId + "&prjCd=" + getPrjCd();
        window.open(url, "任务处理", "height=" + iHeight + ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft);
    }
    ,

    /**
     * 标记为已读
     */
    update: function (obj, noticeId, num) {
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        noticeId = noticeId + ",";
        packet.data.add("noticeId", noticeId);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys014/sys014-biaoNotice.gv?prjCd=" + prjCd;
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                var ulObj = $(obj).closest("ul");
                $(obj).closest("li").remove();
                if (ulObj.find("li").length == 2) {
                    // 没有新的消息了重新刷获取新的消息
                    index.getMessage(true);
                }
                $(obj).closest("li").remove();
            } else {
                alertMsg.error(jsonData.data.errMsg);
            }
        });
        stopEvent(event);
    }
    ,

//跳转页面
    showView: function () {
        var url = "oframe/sysmg/sys014/sys014-init.gv";
        index.quickOpen(url, {opCode: "sys014-init", opName: "消息管理", fresh: true})
    }
    ,
    /**
     * 动态获取消息
     */
    getMessage: function (forceFlag) {
        var messageContain = $("#top a.message");
        var active = messageContain.attr("active");
        if (forceFlag || !$("div.js_message_context", messageContain).is(":visible")) {
            var packet = new AJAXPacket();
            var prjCd = getPrjCd();
            packet.url = getGlobalPathRoot() + "oframe/main/main-showNotice.gv?prjCd=" + prjCd;
            packet.noCover = true;
            core.ajax.sendPacketHtml(packet, function (response) {
                $("div.js_message_context", messageContain).html(response);
                initUI($("div.js_message_context", messageContain));
            });
            // 主动更新消息数量
            index.getMessageCount();
        }
        messageContain.attr("active", "on");
        // 主动更新消息数量
        $("div.js_message_context", messageContain).show();
    }
    ,

    /**
     * 鼠标移动离开链接，则延迟关闭消息框
     */
    closeMessage: function () {
        var messageContain = $("#top a.message");
        messageContain.attr("active", "off");
        // 延迟关闭
        setTimeout(function () {
            var messageContain = $("#top a.message");
            var active = messageContain.attr("active");
            if ("off" == active) {
                $("div.js_message_context", messageContain).hide();
            }
        }, 200);
    }
    ,

    /**
     * 获取消息数量
     */
    getMessageCount: function () {
        var messageContain = $("#top a.message");
        var active = messageContain.attr("active");
        if (active != "on") {
            // 调用服务获取消息数量
            var packet = new AJAXPacket();
            var prjCd = getPrjCd();
            packet.url = getGlobalPathRoot() + "oframe/main/main-countNotice.gv?prjCd=" + prjCd;
            packet.noCover = true;
            // 不进行错误处理
            packet.errorFunction = function (data) {
            };
            core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var messagesCount = data.count;
                var messageLabel = $("label.js_messageCount", messageContain);
                var oldCount = messageLabel.html();
                messageLabel.html(messagesCount);
                if (oldCount == "" && messagesCount != 0) {
                    MsgTip.addMsg({
                        messageId: "-1",
                        title: "未读消息提醒",
                        messgage: "您有[" + messagesCount + "]条未读消息，请及时处理！"
                    });
                } else {
                    // 重新获取
                    var messageCountInt = parseInt(messagesCount);
                    var oldCountInt = parseInt(oldCount);
                    if (messageCountInt > oldCountInt) {
                        // 获取信息的消息，推送提示
                        packet = new AJAXPacket();
                        var prjCd = getPrjCd();
                        packet.url = getGlobalPathRoot() + "oframe/main/main-getMessageJson.gv";
                        packet.data.add("prjCd", getPrjCd());
                        packet.data.add("fetchCount", messageCountInt - oldCountInt);
                        packet.noCover = true;
                        core.ajax.sendPacketHtml(packet, function (response) {
                            var jsonData = eval("(" + response + ")");
                            if (jsonData.success && jsonData.messageData.PageData) {
                                var messageData = jsonData.messageData.PageData.Row;
                                var jsonArrData = messageData;
                                if (!$.isArray([messageData])) {
                                    jsonArrData = new Array();
                                    jsonArrData.push(messageData);
                                }
                                for (var i = 0; i < jsonArrData.length; i++) {
                                    var temp = jsonArrData[i];
                                    MsgTip.addMsg({
                                        messageId: temp.noticeId,
                                        title: temp.createStaffName,
                                        messgage: temp.noticeContent
                                    });
                                }
                            }
                        });
                    }
                }
                if (messagesCount == 0) {
                    messageLabel.hide();
                    $("div.js_message_context", messageContain).hide();
                } else {
                    messageLabel.show();
                }
            });
        }
    }
    ,

    /**
     * 初始化打开发消息窗口
     */
    initSendMess: function () {
        var url = getGlobalPathRoot() + "oframe/main/main-view.gv";
        $.pdialog.open(url, "message", "发送消息", {
            mask: true, max: false, height: 400, width: 800
        })
    }
    ,

    /**
     * 项目切换
     * @param toPrjCd 切换后的项目
     */
    changePrj: function (toPrjCd) {
        document.location.replace(getGlobalPathRoot() + "oframe/main/main-init.gv?prjCd=" + toPrjCd);
    }
    ,
    /**
     * 删除项目目录
     */
    removePrjMenu: function () {
        var active = $("#prjMenu").attr("active");
        if ("on" != active) {
            $("#prjListForMenu").addClass("hidden");
        }
    }
    ,

    /**
     * 初始化化目录树
     */
    initPrjTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        // 获取当前选中的组织树类型，传递项目编号获取组织树
        packet.data.add("prjCd", "0");
        packet.data.add("isShowExp", "1"); // 设置是否显示 无效项目标志
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
                        onClick: index.clickPrjTreeNode
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#prjListForMenu"), prjTreeSetting, treeJson);
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        }, true, false);
    }
    ,
    /**
     * 点击项目选择目录节点
     * @param event
     * @param treeId
     * @param treeNode
     */
    clickPrjTreeNode: function (event, treeId, treeNode) {
        if (treeNode.prjCd) {
            if (treeNode.prjCd != getPrjCd()) {
                index.changePrj(treeNode.prjCd);
                $("#prjListForMenu").addClass("hidden");
            }
        } else {
            document.location.replace(getGlobalPathRoot() + "oframe/main/main-init.gv?prjCd=0&orgId=" + treeNode.id);
        }
    }
    ,

    /**
     * 查询系统版本
     */
    webVersion: function () {
        var url = getGlobalPathRoot() + "oframe/main/main-webVersion.gv";
        $.pdialog.open(url, "version", "系统版本信息", {
            mask: true, max: false, height: 400, width: 800
        })
    }

    /**
     * 保存项目prjCd到浏览器缓存
     */
    ,
    saveSysPrjCdToCache: function () {
        var sysPrjCd = $("#sysPrjCd").val();
        if (sysPrjCd && sysPrjCd !== "") {
            setPCookie("sysPrjCd", sysPrjCd, "/");
        }
    }
};

// 区域信息保存
var regionMap = new Map();
//高拍仪
var Capture = null;
// 高拍仪打开的镜头
var nDeviceIndex = null;

$(function () {
    if ($("#short-link ul li").length == 0) {
        $("#short-link").hide();
        $("#container").css("left", '1px');
    } else {
        $("#short-link").show();
        $("#container").css("left", '40px');
        $("#short-link").jCarouselLite({
            btnNext: "#short-link .short-link-prev",
            btnPrev: "#short-link .short-link-next",
            speed: 600,
            visible: 4,
            circular: false,
            vertical: true
        });
    }
    // 获取消息数量
    index.getMessageCount();
    /**
     * 定时更新消息数量
     * 1分钟同步一次:600000
     */
    setInterval("index.getMessageCount()", 60000);
    //setTimeout(mb008.mb0(), 1000);

    //
    $("#prjMenu").bind("mouseover", function () {
        $("#prjListForMenu").removeClass("hidden");
        $(this).attr("active", "on");
    });

    $("#prjMenu").mouseout(function () {
        $("#prjMenu").attr("active", "off");
        setTimeout(function () {
            index.removePrjMenu();
        }, 300);
    });
    // 加载项目目录树
    index.initPrjTree();
    $(window).bind("resize", index.adjustNav);
    // 调整页面导航
    index.adjustNav();
    // 保存项目prjCd到浏览器缓存
    index.saveSysPrjCdToCache();
});



