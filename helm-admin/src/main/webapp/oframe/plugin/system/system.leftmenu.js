/**
 * 左侧树的页面 点击收缩 展示
 * 拖拽拉宽，收窄
 * @type {{init: LeftMenu.init}}
 */
var LeftMenu = {
    /**
     * 设置通用左侧树，显示隐藏、拖动
     * @param leftDivId 左侧divId
     * @param rightDivId 右侧divId
     * @param options 是否设置cookie {cookieName: "cookieName"}
     */
    init: function (leftDivId, rightDivId, options, isHaveLogo) {
        if (!options) {
            options = {};
        }
        var cookieName = options.cookieName;
        var clickCallBack = options.clickCallBack;

        var leftDiv = $("#" + leftDivId, navTab.getCurrentPanel());
        var rightDiv = $("#" + rightDivId, navTab.getCurrentPanel());
        // 拖动屏幕切分效果
        $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
            $("div.split_line", navTab.getCurrentPanel()).jDrag({
                scop: true, cellMinW: 20,
                move: "horizontal",
                event: event,
                stop: function (moveObj) {
                    var newWidth = $(moveObj).position().left;
                    if (isHaveLogo && newWidth <= 40) {
                        leftDiv.width("40");
                    } else {
                        leftDiv.width($(moveObj).position().left);
                    }
                    rightDiv.css("margin-left", (newWidth + 10) + "px");
                    var resizeGrid = $("div.j-resizeGrid", navTab.getCurrentPanel());
                    if (resizeGrid.length > 0) {
                        resizeGrid.each(function () {
                            $(this).jResize();
                        });
                    }
                    $(moveObj).css("left", 0);
                    if (clickCallBack && $.isFunction(clickCallBack)) {
                        clickCallBack({});
                    }
                }
            });
        });
        $("span.split_but", navTab.getCurrentPanel()).mousedown(function (event) {
            stopEvent(event);
        });
        $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
            var splitLine = $(this).parent();
            var leftMenu = splitLine.prev("div.left_menu");
            var menu_left = leftMenu.attr("class").indexOf("menu_left");
            var isHidden = false;
            if (menu_left != -1) {
                isHidden = true;
            } else {
                isHidden = false;
            }
            if (leftMenu.is(":visible")) {
                var cookieResult = "false";
                // 如果是需要图标的
                if (isHaveLogo) {
                    // 左侧div是否有menu_left的class
                    if (isHidden) {
                        leftMenu.removeClass("menu_left");
                        splitLine.removeClass("menu_hide");
                        rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
                        cookieResult = "true";
                    } else {
                        leftMenu.addClass("menu_left");
                        splitLine.addClass("menu_hide");
                        rightDiv.css("margin-left", "50px");
                        cookieResult = "false";
                    }
                } else {
                    leftMenu.hide();
                    splitLine.addClass("menu_hide");
                    rightDiv.css("margin-left", "10px");
                }
                // 保存cookie
                if (cookieName) {
                    setPCookie(cookieName, cookieResult, "/");
                }
                // 调整后触发回调函数
                if (clickCallBack && $.isFunction(clickCallBack)) {
                    clickCallBack({});
                }
            } else {
                leftMenu.show();
                splitLine.removeClass("menu_hide");
                rightDiv.css("margin-left", leftMenu.width() + 10 + "px");


                // 保存cookie
                if (cookieName) {
                    setPCookie(cookieName, "true", "/");
                }
                // 调整后触发回调函数
                if (clickCallBack && $.isFunction(clickCallBack)) {
                    clickCallBack({});
                }
            }
            var resizeGrid = $("div.j-resizeGrid", navTab.getCurrentPanel());
            if (resizeGrid.length > 0) {
                resizeGrid.each(function () {
                    $(this).jResize();
                });
            }
        });
        if (cookieName && "false" == $.cookie(cookieName)) {
            $("span.split_but", navTab.getCurrentPanel()).trigger("click");
        }

    }
};