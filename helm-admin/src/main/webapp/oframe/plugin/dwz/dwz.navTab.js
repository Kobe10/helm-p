/**
 * @author ZhangHuihua@msn.com
 *
 */
var navTab = {
    componentBox: null, // tab component. contain tabBox, prevBut, nextBut, panelBox
    _tabBox: null,
    _prevBut: null,
    _nextBut: null,
    _panelBox: null,
    _moreBut: null,
    _moreBox: null,
    _currentIndex: 0,
    _pIndex: -1,

    _op: {
        id: "navTab",
        stTabBox: ".navTab-tab",
        stPanelBox: ".navTab-panel",
        mainTabId: "wdsy",
        close$: "a.close",
        prevClass: "tabsLeft",
        nextClass: "tabsRight",
        stMore: ".tabsMore",
        stMoreLi: "ul.tabsMoreList"
    },

    init: function (options) {
        if ($.History) $.History.init("#container");
        var $this = this;
        $.extend(this._op, options);

        this.componentBox = $("#" + this._op.id);
        this._tabBox = this.componentBox.find(this._op.stTabBox);
        this._panelBox = this.componentBox.find(this._op.stPanelBox);
        this._prevBut = this.componentBox.find("." + this._op.prevClass);
        this._nextBut = this.componentBox.find("." + this._op.nextClass);
        this._moreBut = this.componentBox.find(this._op.stMore);
        this._moreBox = this.componentBox.find(this._op.stMoreLi);

        this._prevBut.click(function (event) {
            $this._scrollPrev()
        });
        this._nextBut.click(function (event) {
            $this._scrollNext()
        });
        this._moreBut.click(function () {
            $this._moreBox.show();
            return false;
        });
        $(document).click(function () {
            $this._moreBox.hide()
        });

        this._contextmenu(this._tabBox);
        this._contextmenu(this._getTabs());

        this._init();
        this._ctrlScrollBut();
    },
    _init: function () {
        var $this = this;
        this._getTabs().each(function (iTabIndex) {
            $(this).unbind("click").click(function (event) {
                $this._switchTab(iTabIndex);
            });
            $(this).find(navTab._op.close$).unbind("click").click(function () {
                $this._closeTab(iTabIndex);
            });
        });
        this._getMoreLi().each(function (iTabIndex) {
            $(this).find(">a").unbind("click").click(function (event) {
                $this._switchTab(iTabIndex);
            });
        });

        this._switchTab(this._currentIndex);
    },
    _contextmenu: function ($obj) { // navTab右键菜单
        var $this = this;
        $obj.contextMenu('navTabCM', {
            bindings: {
                reload: function (t, m) {
                    $this._reload(t, true);
                },
                closeCurrent: function (t, m) {
                    var tabId = t.attr("tabid");
                    if (tabId) $this.closeTab(tabId);
                    else $this.closeCurrentTab();
                },
                closeOther: function (t, m) {
                    var index = $this._indexTabId(t.attr("tabid"));
                    $this._closeOtherTab(index > 0 ? index : $this._currentIndex);
                },
                closeAll: function (t, m) {
                    $this.closeAllTab();
                },
                addFav: function (t, m) {
                    var addFav = m.find("[rel='addFav']");
                    if (!addFav.hasClass("disabled")) {
                        $this.addFav(t);
                    }
                }
            },
            ctrSub: function (t, m) {
                var mReload = m.find("[rel='reload']");
                var mCur = m.find("[rel='closeCurrent']");
                var mOther = m.find("[rel='closeOther']");
                var mAll = m.find("[rel='closeAll']");
                var addFav = m.find("[rel='addFav']");
                var $tabLi = $this._getTabs();
                var tabId = t.attr("tabid");
                if ($tabLi.size() < 2) {
                    mCur.addClass("disabled");
                    mOther.addClass("disabled");
                    mAll.addClass("disabled");
                }
                if ($this._currentIndex == 0 || tabId == $this._op.mainTabId) {
                    mCur.addClass("disabled");
                    //mReload.addClass("disabled");
                } else if ($tabLi.size() == 2) {
                    mOther.addClass("disabled");
                }
                var iOpenIndex = $this._indexTabId(tabId);
                if (iOpenIndex >= 0) {
                    var $tab = $tabLi.eq(iOpenIndex);
                    var data = $tab.data("opt");
                    var privilegeId = data.privilegeId;
                    if ($this._currentIndex == 0 || tabId == $this._op.mainTabId || !privilegeId || isNaN(privilegeId)) {
                        addFav.addClass("disabled");
                    }
                }
            }
        });
    },

    _getTabs: function () {
        return this._tabBox.find("> li");
    },
    _getPanels: function () {
        return this._panelBox.find("> div");
    },
    _getMoreLi: function () {
        return this._moreBox.find("> li");
    },
    _getTab: function (tabid) {
        var index = this._indexTabId(tabid);
        if (index >= 0) return this._getTabs().eq(index);
    },
    getPanel: function (tabid) {
        var index = this._indexTabId(tabid);
        if (index >= 0) return this._getPanels().eq(index);
    },
    _getTabsW: function (iStart, iEnd) {
        return this._tabsW(this._getTabs().slice(iStart, iEnd));
    },
    _tabsW: function ($tabs) {
        var iW = 0;
        $tabs.each(function () {
            iW += $(this).outerWidth(true);
        });
        return iW;
    },
    _indexTabId: function (tabid) {
        if (!tabid) return -1;
        var iOpenIndex = -1;
        this._getTabs().each(function (index) {
            if ($(this).attr("tabid") == tabid) {
                iOpenIndex = index;
                return;
            }
        });
        return iOpenIndex;
    },
    _getLeft: function () {
        return this._tabBox.position().left;
    },
    _getScrollBarW: function () {
        return this.componentBox.width() - 55;
    },

    _visibleStart: function () {
        var iLeft = this._getLeft(), iW = 0;
        var $tabs = this._getTabs();
        for (var i = 0; i < $tabs.size(); i++) {
            if (iW + iLeft >= 0) return i;
            iW += $tabs.eq(i).outerWidth(true);
        }
        return 0;
    },
    _visibleEnd: function () {
        var iLeft = this._getLeft(), iW = 0;
        var $tabs = this._getTabs();
        for (var i = 0; i < $tabs.size(); i++) {
            iW += $tabs.eq(i).outerWidth(true);
            if (iW + iLeft > this._getScrollBarW()) return i;
        }
        return $tabs.size();
    },
    _scrollPrev: function () {
        var iStart = this._visibleStart();
        if (iStart > 0) {
            this._scrollTab(-this._getTabsW(0, iStart - 1));
        }
    },
    _scrollNext: function () {
        var iEnd = this._visibleEnd();
        if (iEnd < this._getTabs().size()) {
            this._scrollTab(-this._getTabsW(0, iEnd + 1) + this._getScrollBarW());
        }
    },
    _scrollTab: function (iLeft, isNext) {
        var $this = this;
        this._tabBox.animate({left: iLeft + 'px'}, 200, function () {
            $this._ctrlScrollBut();
        });
    },
    _scrollCurrent: function () { // auto scroll current tab
        var iW = this._tabsW(this._getTabs());
        if (iW <= this._getScrollBarW()) {
            this._scrollTab(0);
        } else if (this._getLeft() < this._getScrollBarW() - iW) {
            this._scrollTab(this._getScrollBarW() - iW);
        } else if (this._currentIndex < this._visibleStart()) {
            this._scrollTab(-this._getTabsW(0, this._currentIndex));
        } else if (this._currentIndex >= this._visibleEnd()) {
            this._scrollTab(this._getScrollBarW() - this._getTabs().eq(this._currentIndex).outerWidth(true) - this._getTabsW(0, this._currentIndex));
        }
    },
    _ctrlScrollBut: function () {
        var iW = this._tabsW(this._getTabs());
        if (this._getScrollBarW() > iW) {
            this._prevBut.hide();
            this._nextBut.hide();
            this._tabBox.parent().removeClass("tabsPageHeaderMargin");
        } else {
            this._prevBut.show().removeClass("tabsLeftDisabled");
            this._nextBut.show().removeClass("tabsRightDisabled");
            this._tabBox.parent().addClass("tabsPageHeaderMargin");
            if (this._getLeft() >= 0) {
                this._prevBut.addClass("tabsLeftDisabled");
            } else if (this._getLeft() <= this._getScrollBarW() - iW) {
                this._nextBut.addClass("tabsRightDisabled");
            }
        }
    },

    _switchTab: function (iTabIndex) {
        var $tab = this._getTabs().removeClass("selected").eq(iTabIndex).addClass("selected");
        this._getPanels().hide().eq(iTabIndex).show();

        this._getMoreLi().removeClass("selected").eq(iTabIndex).addClass("selected");
        this._currentIndex = iTabIndex;

        this._scrollCurrent();
        this._reload($tab);
    },

    _closeTab: function (index, openTabid) {

        this._getTabs().eq(index).remove();
        this._getPanels().eq(index).trigger(DWZ.eventType.pageClear).remove();
        this._getMoreLi().eq(index).remove();

        if (this._currentIndex >= index) this._currentIndex--;

        if (openTabid) {
            var openIndex = this._indexTabId(openTabid);
            if (openIndex > 0) this._currentIndex = openIndex;
        } else if (this._pIndex > 0) {
            this._currentIndex = this._pIndex;
        }
        var maxIdx = this._getTabs().length - 1;
        if (this._currentIndex > maxIdx) {
            this._currentIndex = maxIdx;
        }

        this._init();
        this._scrollCurrent();
        this._reload(this._getTabs().eq(this._currentIndex));
    },
    closeTab: function (tabid) {
        var index = this._indexTabId(tabid);
        if (index > 0) {
            this._closeTab(index);
        }
    },
    closeCurrentTab: function (openTabid) { //openTabid 可以为空，默认关闭当前tab后，打开最后一个tab
        if (this._currentIndex > 0) {
            this._closeTab(this._currentIndex, openTabid);
        }
    },
    closeAllTab: function () {
        this._getTabs().filter(":gt(0)").remove();
        this._getPanels().filter(":gt(0)").trigger(DWZ.eventType.pageClear).remove();
        this._getMoreLi().filter(":gt(0)").remove();
        this._currentIndex = 0;
        this._init();
        this._scrollCurrent();
    },
    /**
     * 收藏指定功能点
     */
    addFav: function (t) {
        var $this = this;
        var tabId = t.attr("tabid");
        var iOpenIndex = $this._indexTabId(tabId);
        if (iOpenIndex >= 0) {
            var $tab = $this._getTabs().eq(iOpenIndex);
            var data = $tab.data("opt");
            var privilegeId = data.privilegeId;
            if (privilegeId && !isNaN(privilegeId)) {
                // 调用服务保存收藏功能
                // 提交页面数据
                var url = getGlobalPathRoot() + "oframe/main/main-addFav.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("privilegeId", privilegeId);
                ajaxPacket.data.add("prjCd", getPrjCd());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (!isSuccess) {
                        alertMsg.warn(jsonData.errMsg);
                    }
                });
            }
        }
    },
    _closeOtherTab: function (index) {
        index = index || this._currentIndex;
        if (index > 0) {
            var str$ = ":eq(" + index + ")";
            this._getTabs().not(str$).filter(":gt(0)").remove();
            this._getPanels().not(str$).filter(":gt(0)").trigger(DWZ.eventType.pageClear).remove();
            this._getMoreLi().not(str$).filter(":gt(0)").remove();
            this._currentIndex = 1;
            this._init();
            this._scrollCurrent();
        } else {
            this.closeAllTab();
        }
    },

    _loadUrlCallback: function ($panel) {
        $panel.find("[layoutH]").layoutH();
        $panel.find(":button.close").click(function () {
            navTab.closeCurrentTab();
        });
    },
    _reload: function ($tab, flag) {
        flag = flag || $tab.data("reloadFlag");
        var url = $tab.attr("url");
        var data = $tab.data("opt");
        if (flag && url) {
            $tab.data("reloadFlag", null);
            var $panel = this.getPanel($tab.attr("tabid"));

            if ($tab.hasClass("external")) {
                navTab.openExternal(url, $panel);
            } else {
                $panel.loadUrl(url, data, function () {
                    navTab._loadUrlCallback($panel);
                });
            }
        }
    },
    reloadFlag: function (tabid) {
        var $tab = this._getTab(tabid);
        if ($tab) {
            if (this._indexTabId(tabid) == this._currentIndex) this._reload($tab, true);
            else $tab.data("reloadFlag", 1);
        }
    },
    reload: function (url, options) {
        var op = $.extend({data: {}, navTabId: "", callback: null}, options);
        var $tab = op.navTabId ? this._getTab(op.navTabId) : this._getTabs().eq(this._currentIndex);
        var $panel = op.navTabId ? this.getPanel(op.navTabId) : this._getPanels().eq(this._currentIndex);
        if ($panel) {
            if (!url) {
                url = $tab.attr("url");
            }
            if (url) {
                if ($tab.hasClass("external")) {
                    navTab.openExternal(url, $panel);
                } else {
                    if ($.isEmptyObject(op.data)) { //获取pagerForm参数
                        var $pagerForm = $("#pagerForm", $panel);
                        op.data = $pagerForm.size() > 0 ? $pagerForm.serializeArray() : {}
                    }
                    $panel.ajaxUrl({
                        type: "POST", url: url, data: op.data, callback: function (response) {
                            navTab._loadUrlCallback($panel);
                            if ($.isFunction(op.callback)) op.callback(response);
                        }
                    });
                }
            }
        }
    },
    getCurrentPanel: function () {
        return this._getPanels().eq(this._currentIndex);
    },
    checkTimeout: function () {
        var json = DWZ.jsonEval(this.getCurrentPanel().html());
        if (json && json.statusCode == DWZ.statusCode.timeout) this.closeCurrentTab();
    },
    openExternal: function (url, $panel) {
        var ih = navTab._panelBox.height();
        $panel.html(DWZ.frag["externalFrag"].replaceAll("{url}", url).replaceAll("{height}", ih + "px"));
    },
    /**
     *
     * @param {Object} tabid
     * @param {Object} url
     * @param {Object} params: title, data, fresh
     */
    openTab: function (tabid, url, options) { //if found tabid replace tab, else create a new tab.
        var op = $.extend({title: "New Tab", data: {}, fresh: false, external: false}, options);
        // 记录当前的标签
        this._pIndex = this._currentIndex;
        var iOpenIndex = this._indexTabId(tabid);
        if (iOpenIndex >= 0) {
            var $tab = this._getTabs().eq(iOpenIndex);
            var span$ = $tab.attr("tabid") == this._op.mainTabId ? "> span > span" : "> span";
            $tab.find(">a").attr("title", op.title).find(span$).text(op.title);
            var $panel = this._getPanels().eq(iOpenIndex);
            if (op.fresh || !$tab.attr("url")) {
                $tab.attr("url", url);
                $tab.data("opt", op.data);
                if (op.external || url.isExternalUrl()) {
                    $tab.addClass("external");
                    navTab.openExternal(url, $panel);
                } else {
                    $tab.removeClass("external");
                    $panel.ajaxUrl({
                        type: "GET", url: url, data: op.data, callback: function () {
                            navTab._loadUrlCallback($panel);
                            /*if($($panel).find(".pageContent .panelBar").css("position") == "fixed"){
                             $($panel).find(".pageContent .panelBar:has('.toolBar')").width($($panel).find(".pageContent").width()-2);
                             }*/
                        }
                    });
                }
            }
            this._currentIndex = iOpenIndex;
            $panel.find(".pageContent>.panelBar").width($panel.find(".pageContent").width());
        } else {
            var tabFrag = '<li tabid="#tabid#"><a href="javascript:" title="#title#" class="#tabid#"><span>#title#</span></a><a href="javascript:;" class="close">close</a></li>';
            this._tabBox.append(tabFrag.replaceAll("#tabid#", tabid).replaceAll("#title#", op.title));
            this._panelBox.append('<div class="page unitBox"></div>');
            this._moreBox.append('<li><a href="javascript:" title="#title#">#title#</a></li>'.replaceAll("#title#", op.title));

            var $tabs = this._getTabs();
            var $tab = $tabs.filter(":last");
            $tab.data("opt", op.data)
            var $panel = this._getPanels().filter(":last");

            if (op.external || url.isExternalUrl()) {
                $tab.addClass("external");
                navTab.openExternal(url, $panel);
            } else {
                $tab.removeClass("external");
                $panel.ajaxUrl({
                    type: "GET", url: url, data: op.data, callback: function () {
                        navTab._loadUrlCallback($panel);
                        /*if($($panel).find(".pageContent .panelBar").css("position") == "fixed"){
                         $($panel).find(".pageContent .panelBar:has('.toolBar')").width($($panel).find(".pageContent").width()-2);
                         }*/

                    }
                });
            }
            // 回退历史处理
            if ($.History) {
                setTimeout(function () {
                    $.History.addHistory(tabid, function (tabid) {
                        var i = navTab._indexTabId(tabid);
                        if (i >= 0) navTab._switchTab(i);
                    }, tabid);
                }, 10);
            }

            this._currentIndex = $tabs.size() - 1;
            this._contextmenu($tabs.filter(":last").hoverClass("hover"));


        }
        // 设置模块编号
        $panel.attr("opCode", op.data.opCode);

        this._init();
        this._scrollCurrent();

        this._getTabs().eq(this._currentIndex).attr("url", url);
    },
    getCurrentTabId: function () {
        var $tab = $(this._getTabs()[this._currentIndex]);
        return $tab.attr("tabid");
    }
};