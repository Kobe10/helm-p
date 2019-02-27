/**
 * 增加直接打开浮层功能
 * options参数说明：
 * href:页面请求URL
 * args:页面请求json参数
 * relObj:弹出框高度和宽度参考的对象
 * appendBody: 控制弹出追加位置
 * appendObj：控制弹出追加位置
 * width:openTip的宽度，可选，默认120
 * height:openTip的高度，可选，默认200
 * offsetY:openTip相对于点击元素的X轴位置偏移，可选，正负数，默认0
 * offsetY:openTip相对于点击元素的Y轴位置偏移，可选，正负数，默认0
 */
(function ($) {
    $.fn.openTip = function (options) {
        $("#openTip").remove();
        var op = {};
        var $openTip = $("<div id='openTip' class='tip' style='z-index: 1005'></div>");
        var $this = $(this);
        var defaultOp = {
            height: "200",
            offsetX: 0,
            offsetY: 0,
            args: {}
        };
        if ($.isPlainObject(options)) {
            op = $.extend(defaultOp, options);
        }
        // 临时处理解决tab页面内部选择框的问题
        if ($this.is("div") && $this.hasClass("unitBox")) {
            $this.append($openTip);
        } else if (op.appendBody) {
            var appentToObj = op.appendObj;
            if (!appentToObj || appentToObj.length == 0) {
                appentToObj = navTab.getCurrentPanel();
            }
            var relObj = op.relObj;
            if (!relObj || relObj.length == 0) {
                relObj = $this;
            }
            var offset = relObj.offset();
            var useTop = offset.top + relObj.outerHeight() - appentToObj.offset().top;
            var useLeft = offset.left - appentToObj.offset().left;
            var position = {top: useTop, left: useLeft};
            if (op.offsetY) {
                position.top = position.top + op.offsetY;
            }
            if (op.offsetX) {
                position.left = position.left + op.offsetX;
            }
            $openTip.css(position);
            if (!op.width) {
                op.width = relObj.outerWidth();
            }
            $openTip.css({height: op.height, width: op.width});
            appentToObj.append($openTip);
        } else {
            var top = op.offsetY;
            var left = op.offsetX;
            $openTip.css({
                left: left + "px",
                top: top + "px",
                width: op.width ? op.width : "120",
                height: op.height,
                appendBody: false
            });
            $openTip.insertAfter($this);
        }
        $openTip.data("clickObj", $this);
        if (op.href) {
            var tipPacket = new AJAXPacket(op.href);
            tipPacket.data.data = op.args;
            core.ajax.sendPacketHtml(tipPacket, function (response) {
                $openTip.html(response);
                initUI($openTip);
                $openTip.css({"overflow": "auto"});
            }, true, false);
            tipPacket = null;
        } else if (op.html) {
            $openTip.html(op.html);
            initUI($openTip);
            $openTip.css({"overflow": "auto"});
        } else {
            initUI($openTip);
            $openTip.css({"overflow": "auto"});
        }
        if ($this.is("input") || $this.is("a")) {
            eventType = "click";
        } else {
            eventType = "click";
        }
        $openTip.bind("click", function (e) {
            e.stopPropagation();
        });
    };
    $.fn.closeTip = function (options) {
        $(this).closest("div.tip").remove();
    };

    /**
     * 打开菜单
     * @param options
     */
    $.fn.openBtnMenu = function (options) {
        if (!options) {
            options = {};
        }
        var appendObj = navTab.getCurrentPanel();
        if (options.btnContainer) {
            appendObj = $.pdialog.getCurrent();
        }
        // 删除原有的菜单
        $("#btnMenu", appendObj).remove();
        // 本次参照对象
        var relObj = options.relObj;
        if (!relObj) {
            relObj = $(this);
        }
        if (relObj.find("ul.menu li").length == 0) {
            return;
        }
        var appendObjOffset = appendObj.offset();
        var menu = relObj.find("ul.menu").clone();
        //打开菜单方向
        if (!options.openDirection) {
            //默认向左打开
            menu.append($('<div class="triangle triangle-right" style="border-left-color: #2c7bae;right: -10px; top: 3px;"></div>'));
        } else {
            //指定openDirection 按指定处理
            if (options.openDirection == 'down') {
                menu.append($('<div class="triangle triangle-up" style="border-bottom-color: #2c7bae;left: 20px; top: -10px;"></div>'));
            }
        }

        $(appendObj).append(menu);
        // 计算位置
        var offset = relObj.offset();
        var inputBottom = offset.top - appendObjOffset.top;
        var left = offset.left - appendObjOffset.left - 10;
        if (!options.offsetX) {
            left = left - menu.outerWidth() - 1;
        } else {
            left = left - options.offsetX;
        }
        if (options.offsetY) {
            inputBottom = inputBottom - options.offsetY;
        }
        var position = {top: inputBottom, left: left};
        menu.css(position).attr("id", "btnMenu").attr("active", "on");
        // 菜单事件
        menu.data("relObj", relObj);
        menu.mouseover($.fn.activeBtnMenu);
        menu.find("li").mouseover($.fn.activeBtnMenu);
        menu.find("li").click(function () {
            $("#btnMenu").remove();
        });
        menu.mouseout(function () {
            $.fn.deActiveBtnMenu();
        });
        menu.removeClass("hidden");
    };

    /**
     * 取消显示菜单
     */
    $.fn.activeBtnMenu = function () {
        var appendObj = $(document);
        // 删除原有的菜单
        $("#btnMenu", appendObj).attr("active", "on");
    };

    /**
     * 取消显示菜单
     */
    $.fn.deActiveBtnMenu = function () {
        var appendObj = $(document);
        // 删除原有的菜单
        $("#btnMenu", appendObj).attr("active", "off");
        setTimeout(function () {
            $.fn.removeBtnMenu();
        }, 300);
    };

    /**
     * 删除菜单
     * @param options
     */
    $.fn.removeBtnMenu = function (options) {
        var appendObj = $(document);
        // 删除原有的菜单
        var active = $("#btnMenu", appendObj).attr("active");
        if ("on" != active) {
            $("#btnMenu", appendObj).remove();
        }
    }
})(jQuery);
