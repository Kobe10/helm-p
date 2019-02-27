/**
 * 通用树选择器
 * @type {{isCheckEnable: isCheckEnable, initFullTree: initFullTree, clickNode: clickNode}}
 */
var tree = {
    /**
     * 自定义树节点点击事件
     */
    slfClickNode: null,

    /**
     * 自定义确定按钮事件
     */
    slfCfmSlt: null,

    /**
     * 获取请求处理包
     */
    getAjaxPacket: null,

    /**
     * 验证是否支持点击
     * @returns {boolean}
     */
    isCheckEnable: function () {
        var treeCheck = $("#treeCheck").val();
        if (treeCheck && treeCheck != "") {
            return true;
        } else {
            return false;
        }
    },
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = null;
        if (tree.getAjaxPacket) {
            packet = tree.getAjaxPacket();
        } else {
            packet = new AJAXPacket();
            packet.url = getGlobalPathRoot() + $("#treeData").val();
        }
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
                        onClick: tree.clickNode,
                        onCheck: tree.checkNode
                    },
                    check: {
                        autoCheckTrigger: false,
                        enable: tree.isCheckEnable(),
                        chkStyle: "checkbox",
                        chkboxType: {"Y": "", "N": ""}
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#commonTreeContent"), pjOrgTreeSetting, treeJson);
            } else {
                alertMsg.warn(jsonData.data.errMsg);
            }
        }, true, false);
    },

    /**
     * 点击节点事件
     * @param event
     * @param treeId
     * @param treeNode
     * @returns {boolean}
     */
    clickNode: function (event, treeId, treeNode) {
        if (tree.isCheckEnable()) {
            return false;
        } else {
            if (tree.slfClickNode) {
                if (tree.slfClickNode(treeNode)) {
                    tree.closeTree();
                }
            } else {
                var tipObj = $("#openTip", $.pdialog.getCurrent());
                if (tipObj.length == 0) {
                    tipObj = $("#openTip", navTab.getCurrentPanel());
                }
                var clickObj = tipObj.data("clickObj");
                var rowObj = $(clickObj).parent();
                $(rowObj).find("input[type=hidden]").val(treeNode.id);
                $(rowObj).find("input[type=text]").val(treeNode.name);
                var fromOp = $("input[name=fromOp]", tipObj).val();
                tree.closeTree();
            }
        }
    },

    checkNode: function (event, treeId, treeNode) {
        if (tree.slfClickNode) {
            tree.slfClickNode(event, treeId, treeNode);
        }
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var parentNode = treeNode.getParentNode();
        if (parentNode) {
            tree.checkAllParent(treeObj, parentNode, false);
        }
        var childNodes = treeNode.children;
        tree.checkAllSub(treeObj, childNodes, false);
    },

    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            tree.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },

    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                tree.checkAllSub(treeObj, child.children, checkStatus)
            }
        }
    },

    onCfmSlt: function (obj) {
        var treeObj = $.fn.zTree.getZTreeObj("commonTreeContent");
        var nodes = treeObj.getCheckedNodes(true);
        if (tree.slfCfmSlt) {
            tree.slfCfmSlt(nodes);
        } else {
            var ids = [];
            var names = [];
            for(var i  = 0; i < nodes.length; i++) {
                ids.push(nodes[i].id)
                names.push(nodes[i].name)
            }
            var tipObj = $("#openTip", $.pdialog.getCurrent());
            if (tipObj.length == 0) {
                tipObj = $("#openTip", navTab.getCurrentPanel());
            }
            var clickObj = tipObj.data("clickObj");
            var rowObj = $(clickObj).parent();
            $(rowObj).find("input[type=hidden]").val(ids.join(","));
            $(rowObj).find("input[type=text]").val(names.join(","));
            var fromOp = $("input[name=fromOp]", tipObj).val();
            tree.closeTree();
        }
        tree.closeTree();
    },

    closeTree: function () {
        $("#openTip", $.pdialog.getCurrent()).remove();
        $("#openTip", navTab.getCurrentPanel()).remove();
        tree.getAjaxPacket = null;
        tree.slfClickNode = null;
        tree.slfCfmSlt = null;
    }
};
/**
 * 码表
 * @type {{getCfgDataOpt: CODE.getCfgDataOpt, getStaffOpt: CODE.getStaffOpt}}
 */
var CODE = {
    /**
     * 获取区域参数
     * @param obj
     * @returns
     */
    getCfgDataOptWithFilter: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].value;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.value;
            },
            displayValue: function (value, data) {
                return data.value;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            sortResults: false,
            filterResults: true,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var $td = source.closest("td");
                $("input[type=hidden]", $td).val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var $td = source.closest("td");
                var mustMatch = source.attr("mustMatch");
                if (!"false" == mustMatch) {
                    source.val("");
                }
                $("input[type=hidden]", $td).val("");
            },
            fetchData: function (obj) {
                var resultData = {};
                var $td = $(obj).closest("td");
                var attrRelItemCd = $(obj).attr("itemCd");
                if (attrRelItemCd && attrRelItemCd != "") {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("itemCd", attrRelItemCd);
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        resultData = eval("(" + response + ")");
                    }, false);
                }
                return resultData;
            }
        }
    },
    /**
     * 获取区域参数
     * @param obj
     * @returns
     */
    getCfgDataOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].value;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.value;
            },
            displayValue: function (value, data) {
                return data.value;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            sortResults: false,
            filterResults: false,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var $td = source.closest("td");
                $("input[type=hidden]", $td).val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var $td = source.closest("td");
                var mustMatch = source.attr("mustMatch");
                if (!"false" == mustMatch) {
                    source.val("");
                }
                $("input[type=hidden]", $td).val("");
            },
            fetchData: function (obj) {
                var resultData = {};
                var $td = $(obj).closest("td");
                var attrRelItemCd = $(obj).attr("itemCd");
                if (attrRelItemCd && attrRelItemCd != "") {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("itemCd", attrRelItemCd);
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        resultData = eval("(" + response + ")");
                    }, false);
                }
                return resultData;
            }
        }
    },
    /**
     * 获取系统员工
     * @param obj
     * @returns
     */
    getStaffOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].value;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.value;
            },
            displayValue: function (value, data) {
                return data.value;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            filterResults: false,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var $td = source.closest("td");
                $("input[type=hidden]", $td).val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var $td = source.closest("td");
                source.val("");
                $("input[type=hidden]", $td).val("");
            },
            fetchData: function (obj) {
                var resultData = {};
                var $td = $(obj).closest("td");
                var staffName = $(obj).val();
                var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-queryStaff.gv?";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("staffName", staffName);
                ajaxPacket.data.add("prjCd", getPrjCd());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    resultData = eval("(" + response + ")");
                }, false);
                return resultData;
            }
        }
    }
};

/**
 * 地图绘制行数
 * @type {{getMapInitData: null, cfmDraCallBack: null, initData: EMap.initData, calcPointsCenter: EMap.calcPointsCenter, cfmDraw: EMap.cfmDraw}}
 */
var EMap = {

    /**
     * 确认绘图回调函数
     */
    getMapInitData: null,

    /**
     * 确认绘图回调函数
     */
    cfmDraCallBack: null,

    /**
     * 获取地图绘制的初始数据
     * @returns {Array}
     */
    initData: function () {
        var result = [];
        if ($.isFunction(EMap.getMapInitData)) {
            result = EMap.getMapInitData();
        }
        return result;
    },
    /**
     * 计算所绘图的中心点
     * @param strPoints
     * @return xCenter yCenter
     */
    calcPointsCenter: function (strPoints) {
        if (strPoints != undefined && strPoints != '' && strPoints != null) {
            var lineNum = strPoints.split("|");   //切分每条线
            var arrtemp = [];                   //点 数组
            //设置两个数组存放X轴，Y轴坐标，取中心点
            var Xarr = [];
            var Yarr = [];
            //遍历每条线
            for (var i = 0; i < lineNum.length; i++) {
                arrtemp = lineNum[i].split(',');        //切分每条线上的点
                var pointList = [];
                //3、将坐标字符串分解为坐标数组
                for (var j = 0; j < arrtemp.length; j++) {
                    //获取所有点x轴 y轴坐标取中心点  (Xmax + Xmin)/2 , (Ymax + Xmin)/2
                    var arr = arrtemp[j].replace('POINT(', '').replace(')', '').split(' ');
                    Xarr.push(arr[0]);
                    Yarr.push(arr[1]);
                    pointList.push(new OpenLayers.Geometry.Point(arr[0], arr[1]));
                }
            }
            //设置地图展示中心
            var xCenter = (Math.max.apply(null, Xarr) + Math.min.apply(null, Xarr)) / 2;
            var yCenter = (Math.max.apply(null, Yarr) + Math.min.apply(null, Yarr)) / 2;
            lon = xCenter;
            lat = yCenter;
            return [xCenter, yCenter];
        } else {
            return [501546, 301055];
        }
    },

    /**
     * 确认绘图处理
     */
    cfmDraw: function (dataArr) {
        var result = true;
        if ($.isFunction(EMap.cfmDraCallBack)) {
            result = EMap.cfmDraCallBack(dataArr);
        }
        return result;
    }
};

/** **** hotkey Compressed by JSA(www.xidea.org)******* */
(function ($) {
    this.version = "(beta)(0.0.3)";
    this.all = {};
    this.special_keys = {
        27: "esc",
        9: "tab",
        32: "space",
        13: "return",
        8: "backspace",
        145: "scroll",
        20: "capslock",
        144: "numlock",
        19: "pause",
        45: "insert",
        36: "home",
        46: "del",
        35: "end",
        33: "pageup",
        34: "pagedown",
        37: "left",
        38: "up",
        39: "right",
        40: "down",
        112: "f1",
        113: "f2",
        114: "f3",
        115: "f4",
        116: "f5",
        117: "f6",
        118: "f7",
        119: "f8",
        120: "f9",
        121: "f10",
        122: "f11",
        123: "f12"
    };
    this.shift_nums = {
        "`": "~",
        "1": "!",
        "2": "@",
        "3": "#",
        "4": "$",
        "5": "%",
        "6": "^",
        "7": "&",
        "8": "*",
        "9": "(",
        "0": ")",
        "-": "_",
        "=": "+",
        ";": ":",
        "'": "\"",
        ",": "<",
        ".": ">",
        "/": "?",
        "\\": "|"
    };
    this.add = function (B, F, A) {
        if ($.isFunction(F)) {
            A = F;
            F = {}
        }
        var E = {}, D = {
            type: "keydown",
            propagate: false,
            disableInInput: true,
            target: "html"
        }, _ = this, E = $.extend(E, D, F || {});
        B = B.toLowerCase();
        var C = function (N) {
            N = $.event.fix(N);
            var A = N.data.selector, I = $(N.target);
            var J = N.which, B = N.type, D = String.fromCharCode(J)
                .toLowerCase(), C = _.special_keys[J], L = N.shiftKey, M = N.ctrlKey, G = N.altKey, O = true, F = null, H = _.all[A].events[B].callbackMap;
            if (E["disableInInput"] && I.is("textarea, input")
                && !(G || M || L))
                return;
            if (!L && !M && !G)
                F = H[C] || H[D];
            else {
                var K = "";
                if (G)
                    K += "alt+";
                if (M)
                    K += "ctrl+";
                if (L)
                    K += "shift+";
                F = H[K + C] || H[K + D] || H[K + _.shift_nums[D]]
            }
            if (F) {
                F.cb(N);
                if (!F.propagate) {
                    N.stopPropagation();
                    N.preventDefault();
                    return false
                }
            }
        };
        if (!this.all[E.target])
            this.all[E.target] = {
                events: {}
            };
        if (!this.all[E.target].events[E.type]) {
            this.all[E.target].events[E.type] = {
                callbackMap: {}
            };
            $(E.target).bind(E.type, {
                selector: E.target
            }, C)
        }
        this.all[E.target].events[E.type].callbackMap[B] = {
            cb: A,
            propagate: E.propagate
        };
        return $
    };
    this.remove = function (_, A) {
        A = A || {};
        target = A.target || "html";
        type = A.type || "keydown";
        _ = _.toLowerCase();
        $(target).unbind(type);
        delete this.all[target].events[type].callbackMap[_];
        return $
    };
    $.hotkeys = this;
    return $
})(jQuery);

/*打开弹出页面*/
(function ($) {
    "use strict";
    $.fn.sltOrg = function (obj, opt) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initTree.gv";
        var inputObj = $(obj).prev("input[type=text]");
        var option = {href: url, height: "230", offsetY: 31, relObj: inputObj};
        if (opt) {
            option = $.extend(option, opt);
        }
        if (!option.width && option.relObj.length > 0) {
            option.width = option.relObj.width() + 24;
        }
        // 设置默认选择项目
        if ((typeof(option.args) === 'undefined') || (typeof(option.args.orgPrjCd) === 'undefined')) {
            option.args = {orgPrjCd: getPrjCd()};
        }
        $(obj).openTip(option);
    };

    $.fn.sltReg = function (obj, opt) {
        var queryOpt = {treeType: 1, fromOp: ""};
        if (opt) {
            $.extend(queryOpt, opt);
        }
        var url = getGlobalPathRoot() + "eland/pj/pj004/pj004-initTree.gv?treeType=" + queryOpt.treeType
            + "&fromOp=" + queryOpt.fromOp;

        // 打开对话框
        var inputObj = $(obj).prev("input[type=text]");
        var option = {href: url, height: "230", offsetY: 31, relObj: inputObj};
        if (opt) {
            option = $.extend(option, opt);
        }
        if (!option.width && option.relObj.length > 0) {
            option.width = option.relObj.width() + 24;
        }
        $(obj).openTip(option);
    };

    $.fn.sltStaff = function (obj, opt) {
        var queryOpt = {fromOp: ""};
        if (opt) {
            $.extend(queryOpt, opt);
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initStaffTree.gv?fromOp=" + queryOpt.fromOp;
        if (opt.prjCd) {
            url += "&staffPrjCd=" + opt.prjCd;
        }
        // 打开对话框
        var inputObj = $(obj).prev("input[type=text]");
        var option = {href: url, height: "230", relObj: inputObj, appendBody: true};
        if (opt) {
            option = $.extend(option, opt);
        }
        if (!option.width && option.relObj.length > 0) {
            option.width = option.relObj.width() + 24;
        }
        $(obj).openTip(option);
    };

    $.fn.sltRole = function (obj, opt) {
        var queryOpt = {fromOp: ""};
        if (opt) {
            $.extend(queryOpt, opt);
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initRole.gv?fromOp=" + queryOpt.fromOp;
        if (opt.prjCd) {
            url += "&staffPrjCd=" + opt.prjCd;
        }
        // 打开对话框
        var inputObj = $(obj).prev("input[type=text]");
        var option = {href: url, height: "230", relObj: inputObj, appendBody: true};
        if (opt) {
            option = $.extend(option, opt);
        }
        if (!option.width && option.relObj.length > 0) {
            option.width = option.relObj.width() + 24;
        }
        $(obj).openTip(option);
    };

    $.fn.sltCode = function (obj, opt) {
        var queryOpt = {fromOp: ""};
        if (opt) {
            $.extend(queryOpt, opt);
        }
        var url = getGlobalPathRoot() + "oframe/common/tree/tree-code.gv?fromOp=" + queryOpt.fromOp
            + "&itemCd=" + queryOpt.itemCd
            + "&sltData=" + queryOpt.sltData
            + "&treeCheck=" + queryOpt.treeCheck;
        if (opt.prjCd) {
            url += "&staffPrjCd=" + opt.prjCd;
        }
        // 打开对话框
        var inputObj = $(obj).prev("input[type=text]");
        var option = {href: url, height: "230", relObj: inputObj, appendBody: true};
        if (opt) {
            option = $.extend(option, opt);
        }
        if (!option.width && option.relObj.length > 0) {
            option.width = option.relObj.width() + 24;
        }
        $(obj).openTip(option);
    };
})(jQuery);

/** *********shield.js********* */
(function ($) {
    // 屏蔽右键
    // document.oncontextmenu=new Function("event.returnValue=false");
    // 左键选择
    // document.onselectstart=new Function("event.returnValue=false");
    if (isIE()) {
        if (window.console) {
            console.log("\u56db\u6d77\u5bcc\u535a\u0020\u6b22\u8fce\u4f60");
            console.log("\u5982\u679c\u4f60\u4e5f\u559c\u6b22\u94bb\u7814\u0077\u0065\u0062\u6280\u672f\u90a3\u5c31\u52a0\u5165\u6211\u4eec\u5427\uff01\u0020\u0068\u0074\u0074\u0070\u003a\u002f\u002f\u0077\u0077\u0077\u002e\u0062\u006a\u0073\u0068\u0066\u0062\u002e\u0063\u006f\u006d\u002f");
            console.log("\u53d1\u9001\u7b80\u5386\u5230\uff1a\u0062\u006a\u0073\u0068\u0066\u0062\u0040\u0031\u0032\u0036\u002e\u0063\u006f\u006d");
        }
    } else {
        console.log("%c \u56db\u6d77\u5bcc\u535a\u0020\u6b22\u8fce\u4f60", "font-size:40px;color:blue;-webkit-text-fill-color:blue;-webkit-text-stroke: 1px blue;");
        console.log("%c \u5982\u679c\u4f60\u4e5f\u559c\u6b22\u94bb\u7814\u0077\u0065\u0062\u6280\u672f\u90a3\u5c31\u52a0\u5165\u6211\u4eec\u5427\uff01\u0020\u0068\u0074\u0074\u0070\u003a\u002f\u002f\u0077\u0077\u0077\u002e\u0062\u006a\u0073\u0068\u0066\u0062\u002e\u0063\u006f\u006d\u002f\u0073\u0068\u0066\u0062\u002f\u0070\u006f\u0072\u0074\u0061\u006c\u002f\u0073\u0068\u0066\u0062\u002d\u0064\u0065\u0074\u0061\u0069\u006c\u002e\u0067\u0076\u003f\u0072\u0068\u0074\u0049\u0064\u003d\u0033\u0032\u0030\u0036\u0026\u0066\u0069\u0072\u0073\u0074\u004c\u0065\u0076\u0065\u006c\u003d\u0036\u0026\u0070\u0075\u0062\u0049\u0064\u003d\u0034\u0035\u0033\u0033", "font-size: 14px;color:#333");
        console.log("%c \u53d1\u9001\u7b80\u5386\u5230\uff1a\u0062\u006a\u0073\u0068\u0066\u0062\u0040\u0031\u0032\u0036\u002e\u0063\u006f\u006d", "font-size: 14px;color:#333");
    }

    //处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外
    function banBackSpace(e) {
        var ev = e || window.event;//获取event对象
        var obj = ev.target || ev.srcElement;//获取事件源
        var t = obj.type || obj.getAttribute('type');//获取事件源类型
        //获取作为判断条件的事件类型
        var vReadOnly = obj.getAttribute('readonly');
        var vEnabled = obj.getAttribute('enabled');
        var contenteditable = obj.getAttribute('contenteditable');
        if (contenteditable == "true") {
            return true;
        }
        //处理null值情况
        vReadOnly = (vReadOnly == null) ? false : vReadOnly;
        vEnabled = (vEnabled == null) ? true : vEnabled;
        //当敲Backspace键时，事件源类型为密码或单行、多行文本的，
        //并且readonly属性为true或enabled属性为false的，则退格键失效
        var flag1 = (ev.keyCode == 8 && (t == "password" || t == "text" || t == "textarea")
        && (vReadOnly == true || vEnabled != true)) ? true : false;

        //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效
        var flag2 = (ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")
            ? true : false;
        //判断
        if (flag2) {
            return false;
        }
        if (flag1) {
            return false;
        }
    }

    //禁止后退键 作用于Firefox、Opera
    document.onkeypress = banBackSpace;
    //禁止后退键  作用于IE、Chrome
    document.onkeydown = banBackSpace;
})(jQuery);