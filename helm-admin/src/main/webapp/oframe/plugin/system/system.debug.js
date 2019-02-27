/**
 * 解析运行环境调用system.js文件的页面URL，用于获取运行环境的根目录 majhc 2009-11-6 15:44:09
 */
function getGlobalPathRoot() {
    var contextPath = document.getElementById('systemScript').getAttribute('contextPath');
    if(contextPath == undefined) {
        alert("引入system.js需要指定id为systemScript，同时提供contextPath属性")
    } else {
        return contextPath
    }
}

/*
 * 通过JS格式化日期
 */
function fomateDate(oDate, sFomate, bZone) {
    sFomate = sFomate.replace("YYYY", oDate.getFullYear());
    sFomate = sFomate.replace("YY", String(oDate.getFullYear()).substr(2))
    sFomate = sFomate.replace("MM", oDate.getMonth() + 1)
    sFomate = sFomate.replace("DD", oDate.getDate());
    sFomate = sFomate.replace("hh", oDate.getHours());
    sFomate = sFomate.replace("mm", oDate.getMinutes());
    sFomate = sFomate.replace("ss", oDate.getSeconds());
    if (bZone) sFomate = sFomate.replace(/\b(\d)\b/g, '0$1');
    return sFomate;
}

/**
 * 检测浏览器
 * @returns {boolean}
 */
function isIE() {
    if (!!window.ActiveXObject || "ActiveXObject" in window) {
        return true;
    } else {
        return false;
    }
}
function stopEvent(event) {
    if (!event) {
        return;
    }
    if (event.stopPropagation) {
        event.stopPropagation();
    } else {
        event.cancelBubble = true;
    }
    if (event.preventDefault) {
        event.preventDefault();
    } else {
        event.returnValue = false;
    }
}
function setPCookie(cookieName, cookieValue, cookiePath) {
    var expiresTime = new Date();
    expiresTime.setFullYear(2050, 12, 31);
    $.cookie(cookieName, cookieValue, {
        expires: expiresTime,
        path: cookiePath,
        secure: false
    });
}
function getPrjCd() {
    var prjCd = $("#sysPrjCd").val();
    if (!prjCd) {
        prjCd = "";
    }
    return prjCd;
}

var JMap = function () {
    this.clear();
};
JMap.prototype.clear = function () {
    this.data = {};
};
// JMap.prototype.add=function(n,v){ this.data[n] = v };
JMap.prototype.add = function (n, v) {
    if (typeof (v) == "object" && v != null) {
        this.serialize(this.data, n, v);
    }
    this.data[n] = v;
};

JMap.prototype.serialize = function (originObj, name, obj) {
    if (obj.length) {
        var vLength = obj.length;
        for (var i = 0; i < vLength; i++) {
            var index = name + "[" + i + "]";
            if (obj[i] && typeof (obj[i]) == "object") {
                jmap = new JMap();
                jmap.serialize(originObj, index, obj[i]);
            } else {
                originObj[index] = obj[i];
            }
        }
    } else {
        for (var s in obj) {
            if (typeof (obj[s]) == "object") {
                jmap = new JMap();
                jmap.serialize(originObj, s, obj[s]);
            } else {
                originObj[s] = obj[s];
            }
        }
    }
};

JMap.prototype.findValueByName = function (n) {
    return this.data[n];
};

//construction
function Map() {
    this.obj = {};
}
//add a key-value
Map.prototype.put = function (key, value) {
    this.obj[key] = value;
};
//get a value by a key,if don't exist,return undefined
Map.prototype.get = function (key) {
    return this.obj[key];
};
//remove a value by a key
Map.prototype.remove = function (key) {
    if (this.get(key) == undefined) {
        return;
    }
    delete this.obj[key];
};
Map.prototype.containsKey = function (key) {
    if (this.get(key) == undefined) {
        return false;
    } else {
        return true;
    }

};
//clear the map
Map.prototype.clear = function () {
    this.obj = {};
};
//get the size
Map.prototype.size = function () {
    var ary = this.keys();
    return ary.length;
};
//get all keys
Map.prototype.keys = function () {
    var ary = [];
    for (var temp in this.obj) {
        ary.push(temp);
    }
    return ary;
};

//get all values
Map.prototype.values = function () {
    var ary = [];
    for (var temp in this.obj) {
        ary.push(this.obj[temp]);
    }
    return ary;
};


String.prototype.startWith = function (str) {
    var reg = new RegExp("^" + str);
    return reg.test(this);
};

String.prototype.endWith = function (str) {
    var reg = new RegExp(str + "$");
    return reg.test(this);
};

String.prototype.isImg = function () {
    //判断是否是图片 - strFilter必须是小写列举
    var strFilter = ".jpeg|.gif|.jpg|.png|.bmp|.pic|";
    if (this.indexOf(".") > -1) {
        var p = this.lastIndexOf(".");
        var strPostfix = this.substring(p, this.length) + '|';
        strPostfix = strPostfix.toLowerCase();
        if (strFilter.indexOf(strPostfix) > -1) {
            return true;
        }
    }
    return false;
};

/**
 * 标题格式化，快速展示。
 * @type {{split_str: TitleFormat.split_str, titleMouseOver: TitleFormat.titleMouseOver, titleMouseOut: TitleFormat.titleMouseOut}}
 */
var TitleFormat = {
    /**
     * JS字符切割函数
     * @params     string                原字符串
     * @params    words_per_line        每行显示的字符数
     */
    split_str: function (string, words_per_line) {
        //空串，直接返回
        if (typeof string == 'undefined' || string.length == 0) return '';
        //单行字数未设定，非数值，则取 逗号分隔
        if (typeof words_per_line == 'undefined' || isNaN(words_per_line) || words_per_line == 0) {
            //取出i=0时的字，避免for循环里换行时多次判断i是否为0
            var output_string = string.substring(0, 1);
            var temp = 1;
            var subStr = string;
            //循环分隔字符串
            for (var i = 1; i < string.length; i++) {
                if (i == temp) {
                    if (temp == 1) {
                        output_string += "";
                    } else {
                        output_string += "<br/>";
                    }
                    subStr = string.substring(temp - 1, string.length);
                    temp = temp + subStr.indexOf(",") + 1;
                }
                //每次拼入一个字符
                output_string += string.substring(i, i + 1);
            }
        } else {
            //格式化成整形值
            words_per_line = parseInt(words_per_line);
            //取出i=0时的字，避免for循环里换行时多次判断i是否为0
            output_string = string.substring(0, 1);

            //循环分隔字符串
            for (i = 1; i < string.length; i++) {
                //如果当前字符是每行显示的字符数的倍数，输出换行
                if (i % words_per_line == 0) {
                    output_string += "<br/>";
                }
                //每次拼入一个字符
                output_string += string.substring(i, i + 1);
            }
        }
        return output_string;
    },

    /**
     * 鼠标悬停显示 title 内容
     * @param obj 当前悬停 元素
     * @param event 事件
     * @param words_per_line 显示框每行展示字符数
     * @returns {boolean}
     */
    titleMouseOver: function (obj, event, words_per_line) {
        //无TITLE悬停，直接返回
        if (typeof obj.title == 'undefined' || obj.title == '') {
            return false;
        }
        //不存在title_show标签则自动新建
        var title_show = document.getElementById("title_show");
        if (title_show == null) {
            title_show = document.createElement("div");                            //新建Element
            document.getElementsByTagName('body')[0].appendChild(title_show);      //加入body中
            var attr_id = document.createAttribute('id');                          //新建Element的id属性
            attr_id.nodeValue = 'title_show';                                      //为id属性赋值
            title_show.setAttributeNode(attr_id);                                  //为Element设置id属性

            var attr_style = document.createAttribute('style');                    //新建Element的style属性
            attr_style.nodeValue = 'position:absolute;'                            //绝对定位
                + 'text-align: left;'
                + 'border:solid 1px #999999; background:#f5f6f8;'              //边框、背景颜色
                + 'border-radius:2px;box-shadow:2px 3px #999999;'              //圆角、阴影
                + 'line-height:18px;'                                            //行间距
                + 'z-index:1010;'                                                //展示层级
                + 'font-size:12px; padding: 2px 5px;';                          //字体大小、内间距
            try {
                title_show.setAttributeNode(attr_style);                        //为Element设置style属性
            } catch (e) {
                //IE6
                title_show.style.position = 'absolute';
                title_show.style.border = 'solid 1px #999999';
                title_show.style.background = '#EDEEF0';
                title_show.style.lineHeight = '18px';
                title_show.style.fontSize = '18px';
                title_show.style.padding = '2px 5px';
            }
        }

        //存储并删除原TITLE
        document.title_value = obj.title;
        obj.title = '';
        //单行字数未设定，非数值，则取默认值50
        if (typeof words_per_line == 'undefined' || isNaN(words_per_line)) {
            words_per_line = 0;
        }
        //格式化成整形值
        words_per_line = parseInt(words_per_line);
        //在title_show中按每行限定字数显示标题内容，模拟TITLE悬停效果
        title_show.innerHTML = this.split_str(document.title_value, words_per_line);
        //显示悬停效果DIV
        title_show.style.display = 'block';

        //根据鼠标位置设定悬停效果DIV位置
        event = event || window.event;                            //鼠标、键盘事件
        var top_down = 15;                                        //下移15px避免遮盖当前标签
        //最左值为当前鼠标位置 与 body宽度减去悬停效果DIV宽度的最小值，否则将右端导致遮盖
        var left = Math.min(event.clientX, document.body.clientWidth - title_show.clientWidth);
        title_show.style.left = left + "px";                        //设置title_show在页面中的X轴位置。
        title_show.style.top = (event.clientY + top_down) + "px";    //设置title_show在页面中的Y轴位置。
    },
    /**
     * 鼠标离开隐藏TITLE
     * @params obj 当前悬停的标签
     */
    titleMouseOut: function (obj) {
        var title_show = document.getElementById("title_show");
        //不存在悬停效果，直接返回
        if (title_show == null) return false;
        //存在悬停效果，恢复原TITLE
        obj.title = document.title_value;

        //隐藏悬停效果DIV
        title_show.style.display = "none";
        //清空所有
        document.title_value = null;
        title_show = null;
    }
};
/**
 *  补充系统点击按钮事件，增加记录日志的OpCode
 * @type {{}}
 */
var OpLog = {
    opCode: null,
    click: function (obj) {
        "use strict";
        if ($(obj).attr("rhtCd")) {
            OpLog.opCode = $(obj).attr("rhtCd");
        }
    }
};
/** **** ajax.js******* */
var core = {};
core.version = "2.00";
$.ajaxSetup({cache: true});
core.ajax = {
    receivePacket: function (packet) {
    },
    /**
     * 获取AJAXPacket请求结果
     * @param packet 请求参数与路径
     * @param process 回调处理方法
     * @param aysncflag 异步处理标识 true:异步处理 false:同步处理，不传默认true
     * @param packet.timeout 设置超时时间，单位毫秒，默认值为30000（30秒）,为0时不超时
     * 同步处理dynamicLoadpage后的方法将进行等待，服务器处理完成后才进行处理
     * 异步处理在服务端未执行完成，dynamicLoadpage后的方法立即执行，不等待服务器返回
     */
    sendPacket: function (packet, process, aysncflag) {
        // 默认超时时间为30秒 放宽至一分钟
        var defaultTimeouts = "60000";
        if (packet.timeout) {
            defaultTimeouts = packet.timeout;
        }
        if (aysncflag == null) {
            aysncflag = true;
        }
        var showCover = true;
        if (packet.noCover) {
            showCover = !packet.noCover;
        }
        var sendUrl = packet.url;
        var sendData = packet.data.data;
        // 点击按钮不可用
        if (packet.clickObject) {
            $(packet.clickObject).attr("disabled", true);
            if ($(packet.clickObject).attr("class")) {
                var _class = $(packet.clickObject).attr("class").split(' '), _btnClass = "";
                for (var i = 0; i < _class.length; i++) {
                    if (/^big/.test(_class[i])) {
                        _btnClass = _class[i];
                        break;
                    }
                }
                if (!$(packet.clickObject).hasClass(_btnClass + "_dis")) $(packet.clickObject).addClass(_btnClass + "_dis");
            }
        }
        var ajaxbg = null;
        if (showCover) {
            ajaxbg = $("#background,#progressBar");
            ajaxbg.show();
        }
        $.ajax({
            url: sendUrl,
            data: sendData,
            type: "POST",
            async: aysncflag,
            //timeout : defaultTimeouts,
            dataType: "html",
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            complete: function (XMLHttpRequest, textStatus) {
                if (ajaxbg) {
                    ajaxbg.hide();
                }
            },
            success: function (data) {
                eval(data);
                process && process(response);
                response = null;
                // 按钮恢复可用
                if (packet.clickObject) {
                    $(packet.clickObject).removeAttr("disabled");
                    if ($(packet.clickObject).attr("class")) {
                        var _class = $(packet.clickObject).attr("class").split(' '), _btnClass = "";
                        for (var i = 0; i < _class.length; i++) {
                            if (/^big/.test(_class[i])) {
                                _btnClass = _class[i];
                                break;
                            }
                        }
                        if ($(packet.clickObject).hasClass(_btnClass + "_dis")) $(packet.clickObject).removeClass(_btnClass + "_dis");
                    }
                }
            },
            error: function (data) {
                if (data.status == "404") {
                    alertMsg.warn("文件不存在!");
                } else if (data.status == "500") {
                    var errorJsonStr = data.getResponseHeader("Error-Json");
                    if (errorJsonStr != null || errorJsonStr != "") {
                        var errorJson = eval("(" + errorJsonStr + ")");
                        if (errorJson.code && errorJson.code == "121004") {
                            top.alertMsg.warn("用户未登录或登录失效!");
                        } else if (errorJson.code && errorJson.code == "121005") {
                            top.alertMsg.warn("禁止非法进入系统!");
                        } else {
                            alertMsg.warn("服务信息返回错误!");
                        }
                    } else {
                        alertMsg.warn("服务信息返回错误!");
                    }
                } else {
                    alertMsg.warn("SYS-" + data.status + ":系统错误!");
                }
                // 按钮恢复可用
                if (packet.clickObject) {
                    $(packet.clickObject).removeAttr("disabled");
                }
                if (data.statusText == "timeout") {
                    alertMsg.warn("页面加载超时，请重试!");
                }
            }
        });
    },

    /**
     * 获取自定义请求结果，可以使页面、json字符串等任意结果
     * @param packet 请求参数与路径
     * @param process 回调处理方法
     * @param aysncflag 异步处理标识 true:异步处理 false:同步处理，不传默认true
     * @param packet.timeout 设置超时时间，单位毫秒，默认值为30000（30秒），为0时不超时
     * 同步处理dynamicLoadpage后的方法将进行等待，服务器处理完成后才进行处理
     * 异步处理在服务端未执行完成，dynamicLoadpage后的方法立即执行，不等待服务器返回
     */
    sendPacketHtml: function (packet, process, aysncflag) {
        // 默认超时时间为30秒 放宽至一分钟
        var defaultTimeouts = "60000";
        if (packet.timeout) {
            defaultTimeouts = packet.timeout;
        }
        // 默认展示进度条
        if (aysncflag == null) {
            aysncflag = true;
        }
        var showCover = true;
        if (packet.noCover) {
            showCover = !packet.noCover;
        }
        var ajaxbg = null;
        if (showCover) {
            ajaxbg = $("#background,#progressBar");
            ajaxbg.show();
        }
        var sendUrl = packet.url;

        if (OpLog.opCode == null) {
            OpLog.opCode = "";
        }
        if (sendUrl.indexOf("?") != '-1') {
            sendUrl += "&opCode=" + OpLog.opCode;
        } else {
            sendUrl += "?opCode=" + OpLog.opCode;
        }
        var sendData = packet.data.data;
        // 点击按钮不可用
        if (packet.clickObject) {
            $(packet.clickObject).attr("disabled", true);
            if ($(packet.clickObject).attr("class")) {
                var _class = $(packet.clickObject).attr("class").split(' '), _btnClass = "";
                for (var i = 0; i < _class.length; i++) {
                    if (/^big/.test(_class[i])) {
                        _btnClass = _class[i];
                        break;
                    }
                }
                if (!$(packet.clickObject).hasClass(_btnClass + "_dis")) $(packet.clickObject).addClass(_btnClass + "_dis");
            }
        }
        $.ajax({
            url: sendUrl,
            data: sendData,
            type: "POST",
            async: aysncflag,
            dataType: "html",
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            complete: function (XMLHttpRequest, textStatus) {
                if (ajaxbg) {
                    ajaxbg.hide();
                }
            },
            success: function (data) {
                // 处理IE9表格错误显示的问题
                if (isIE()) {
                    data = data.replace(/td>\s+<td/g, 'td><td');
                }
                process && process(data);
                // 按钮恢复可用
                if (packet.clickObject) {
                    $(packet.clickObject).removeAttr("disabled");
                    if ($(packet.clickObject).attr("class")) {
                        var _class = $(packet.clickObject).attr("class").split(' '), _btnClass = "";
                        for (var i = 0; i < _class.length; i++) {
                            if (/^big/.test(_class[i])) {
                                _btnClass = _class[i];
                                break;
                            }
                        }
                        if ($(packet.clickObject).hasClass(_btnClass + "_dis")) $(packet.clickObject).removeClass(_btnClass + "_dis");
                    }
                }
            },
            error: function (data) {
                // 存在自定义的
                if (packet.errorFunction) {
                    packet.errorFunction(data);
                } else {
                    if (data.status == "404") {
                        alertMsg.warn("文件不存在!");
                    } else if (data.status == "500") {
                        var errorJsonStr = data.getResponseHeader("Error-Json");
                        if (errorJsonStr != null || errorJsonStr != "") {
                            var errorJson = eval("(" + errorJsonStr + ")");
                            if (errorJson.code && errorJson.code == "121004") {
                                top.alertMsg.warn("用户未登录或登录失效!");
                            } else if (errorJson.code && errorJson.code == "121005") {
                                top.alertMsg.warn("禁止非法进入系统!");
                            } else {
                                alertMsg.warn("服务信息返回错误!");
                            }
                        } else {
                            alertMsg.warn("服务信息返回错误!");
                        }
                    } else {
                        alertMsg.warn("系统错误,错误编码：" + data.status);
                    }

                    if (data.statusText == "timeout") {
                        alertMsg.warn("页面加载超时，请重试!");
                    }
                }
                // 按钮恢复可用
                if (packet.clickObject) {
                    $(packet.clickObject).removeAttr("disabled");
                }
            }
        });
    },

    /**
     * 发送大数据包
     * @param packet 请求参数与路径
     * @param process 回调处理方法
     * @param aysncflag 异步处理标识 true:异步处理 false:同步处理，不传默认true
     * @param packet.timeout 设置超时时间，单位毫秒，默认值为30000（30秒），为0时不超时
     * 同步处理dynamicLoadpage后的方法将进行等待，服务器处理完成后才进行处理
     * 异步处理在服务端未执行完成，dynamicLoadpage后的方法立即执行，不等待服务器返回
     */
    sendHPacket: function (packet, process, aysncflag) {
        // 默认超时时间为30秒 放宽至一分钟
        var defaultTimeouts = "60000";
        if (packet.timeout) {
            defaultTimeouts = packet.timeout;
        }
        // 默认展示进度条
        if (aysncflag == null) {
            aysncflag = true;
        }
        var showCover = true;
        if (packet.noCover) {
            showCover = !packet.noCover;
        }
        var ajaxbg = null;
        if (showCover) {
            ajaxbg = $("#background,#progressBar");
            ajaxbg.show();
        }
        var sendUrl = packet.url;
        var sendData = packet.data.data;
        // 点击按钮不可用
        if (packet.clickObject) {
            $(packet.clickObject).attr("disabled", true);
            if ($(packet.clickObject).attr("class")) {
                var _class = $(packet.clickObject).attr("class").split(' '), _btnClass = "";
                for (var i = 0; i < _class.length; i++) {
                    if (/^big/.test(_class[i])) {
                        _btnClass = _class[i];
                        break;
                    }
                }
                if (!$(packet.clickObject).hasClass(_btnClass + "_dis")) $(packet.clickObject).addClass(_btnClass + "_dis");
            }
        }
        $.ajax({
            url: sendUrl,
            processData: false,
            data: JSON.stringify(sendData),
            type: "POST",
            async: aysncflag,
            dataType: "html",
            contentType: "application/json;charset=UTF-8",
            complete: function (XMLHttpRequest, textStatus) {
                if (ajaxbg) {
                    ajaxbg.hide();
                }
            },
            success: function (data) {
                process && process(data);
                // 按钮恢复可用
                if (packet.clickObject) {
                    $(packet.clickObject).removeAttr("disabled");
                    if ($(packet.clickObject).attr("class")) {
                        var _class = $(packet.clickObject).attr("class").split(' '), _btnClass = "";
                        for (var i = 0; i < _class.length; i++) {
                            if (/^big/.test(_class[i])) {
                                _btnClass = _class[i];
                                break;
                            }
                        }
                        if ($(packet.clickObject).hasClass(_btnClass + "_dis")) $(packet.clickObject).removeClass(_btnClass + "_dis");
                    }
                }
            },
            error: function (data) {
                // 自定义错误函数
                if (packet.errorFunction) {
                    packet.errorFunction(data);
                } else {
                    // 未设置自定义错误寒素,按照系统框架函数处理
                    if (data.status == "404") {
                        alertMsg.warn("文件不存在!");
                    } else if (data.status == "500") {
                        var errorJsonStr = data.getResponseHeader("Error-Json");
                        if (errorJsonStr != null || errorJsonStr != "") {
                            var errorJson = eval("(" + errorJsonStr + ")");
                            if (errorJson.code && errorJson.code == "121004") {
                                top.alertMsg.warn("用户未登录或登录失效!");
                            } else if (errorJson.code && errorJson.code == "121005") {
                                top.alertMsg.warn("禁止非法进入系统!");
                            } else {
                                alertMsg.warn("服务信息返回错误!");
                            }
                        } else {
                            alertMsg.warn("服务信息返回错误!");
                        }
                    } else {
                        alertMsg.warn("系统错误,错误编码：" + data.status);
                    }
                    if (data.statusText == "timeout") {
                        alertMsg.warn("页面加载超时，请重试!");
                    }
                }
                // 按钮恢复可用
                if (packet.clickObject) {
                    $(packet.clickObject).removeAttr("disabled");
                }
            }
        });
    },

    /**
     * Ajax获取动态页面内容,填充指定控件内容
     * @param packet 请求信息
     * @param id 填充控件ID
     * @param aysncflag 异步处理标识 true:异步处理 false:同步处理，不传默认false
     * 同步处理dynamicLoadpage后的方法将进行等待，服务器处理完成后才进行处理
     * 异步处理在服务端未执行完成，dynamicLoadpage后的方法立即执行，不等待服务器返回
     */
    dynamicLoadpage: function (packet, id, aysncflag) {
        core.ajax.sendPacketHtml(packet, function (data) {
            $("#" + id).html(data);
        }, aysncflag);
    },
    loadjs: function (response) {
        var f = response.match(/<script(.|\n)*?>(.|\n|\r\n)*?<\/script>/ig);
        if (f) {
            for (var i = 0; i < f.length; i++) {
                var st = f[i].match(/<script(.|\n)*?>((.|\n|\r\n)*)?<\/script>/im)[2];
                if (st) {//如果st为真，说明是内联代码，eval执行
                    window.eval(st);
                } else {//否则，说明是引入文件，动态创建script标签执行
                    var jsfile = $(f[i]);
                    $("body").append(jsfile);
                }
            }
        }
    }
};

AJAXPacket = function (url, text, clickObject, timeout) {
    this.url = url;
    this.clickObject = clickObject;
    this.data = new JMap();
    this.statusText = text;
    this.guid = "";
    this.timeout = timeout;
};/**
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
/**文件上传及文件扫描 **/
var file = {
    /**
     * 标记是否处理成功
     */
    upLoadedFiles: [],


    /**
     * 默认文件分类获取方法
     * @return {string}
     */
    defaultDocTypeRelUrl: function () {
        var docRelType = $("#uploadDocTypeName", $.pdialog.getCurrent()).attr("typeItemCd");
        return getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?prjCd=" + getPrjCd() + "&itemCd=" + docRelType;
    },

    /**
     * 扩展的获取URL路径方法
     */
    getDocTypeRelUrl: function () {
        var docRelType = $("#uploadDocTypeName", $.pdialog.getCurrent()).attr("typeItemCd");
        return getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?prjCd=" + getPrjCd() + "&itemCd=" + docRelType;
    },

    /**
     * 成功上传提交回调
     */
    successCallBack: null,

    /**
     * 打开文件上传对话框(不允许选分类，但是可以选择已有）
     * @param clickObj 点击对象
     * @param relType 关联类型
     * @param relId 关联对象编号
     * @param docTypeName 文档分类
     * @param prjCd 项目编号
     * @param docIds 已关联的文件编号
     * @param editAble 是否允许删除附件
     *  @param subDir 子目录路径
     */
    openSUpload: function (clickObj, relType, relId, docTypeName, prjCd, docIds, editAble, subDir) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + prjCd + "&docTypeName=" + docTypeName +
            "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=" + relType + "&relId=" + relId + "&subDir=" + subDir;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            param: {clickSpan: clickObj}
        });
    },

    /**
     * 打开上传对话框（可选择关联分类）
     * @param clickObj 点击对象
     * @param relType 关联类型
     * @param relId 关联对象编号
     * @param docTypeName 默认文档分类
     * @param prjCd 项目编号
     * @param docIds 已关联的文件编号
     * @param editAble 是否允许删除附件
     * @param subDir 子目录路径
     * @param typeItemCd  文档分类码表
     *
     */
    openAUpload: function (clickObj, relType, relId, docTypeName, prjCd, docIds, editAble, subDir, typeItemCd) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?prjCd=" + prjCd + "&docTypeName=" + docTypeName +
            "&docIds=" + docIds + "&editAble=" + editAble + "&typeItemCd=" + typeItemCd
            + "&relType=" + relType + "&relId=" + relId + "&subDir=" + subDir;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            param: {clickSpan: clickObj}
        });
    },

    /**
     * 选择文件类型上传附件,适用于多文件上传
     * @param clickObj 点击对象
     */
    startUpload: function (clickObj) {
        var closeTable = $(clickObj).closest("table");
        var docTypeName = closeTable.find("input[name=docTypeName]").val();
        if (docTypeName != "") {
            $('#fileUpload').uploadify('upload', "*")
        } else {
            alertMsg.warn("附件类型必须选择");
        }
    },

    /**
     * 户附件类型信息
     * @param obj
     * @returns map
     */
    getDocTypeRelOpt: function (obj) {
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
            onItemSelect: function (obj) {
                $("input[name=docTypeName]", $.pdialog.getCurrent()).val(obj.data.value);
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            sortResults: false,
            filterResults: false
        }
    },

    /**
     * 关闭
     */
    closeD: function () {
        return scan.closeScan();
    },


    /**
     * 获取所有在上传区域的文件列表
     * @returns {Array}
     */
    getAllUploadFiles: function () {
        var temp = [];
        $("div.js_upload span.js_pic_span", $.pdialog.getCurrent()).each(function () {
            var $this = $(this);
            var docId = $this.attr("docId");
            if (docId != "") {
                var tempFileItem = {};
                tempFileItem.docId = docId;
                tempFileItem.fileName = $this.attr("docName");
                tempFileItem.docPath = $this.attr("docPath");
                temp.push(tempFileItem);
            }
        });
        $("div.js_uploaded span.js_pic_span", $.pdialog.getCurrent()).each(function () {
            var $this = $(this);
            var checked = $this.find("input[type=checkbox]").attr("checked");
            if (checked) {
                var docId = $this.attr("docId");

                if (docId != "") {
                    var tempFileItem = {};
                    tempFileItem.docId = docId;
                    tempFileItem.fileName = $this.attr("docName");
                    tempFileItem.docPath = $this.attr("docPath");
                    temp.push(tempFileItem);
                }
            }
        });
        // 获取上传的图片列表
        var scanFiles = scan.getUploadFiles();
        for (var i = 0; i < scanFiles.length; i++) {
            var tempFileItem = {};
            tempFileItem.docId = scanFiles[i].docId;
            tempFileItem.fileName = scanFiles[i].fileName;
            tempFileItem.docPath = scanFiles[i].docPath;
            temp.push(tempFileItem);
        }
        // 返回所有上传的文件列表
        return temp;
    },

    /**
     * 标记是否处理成功
     */
    uploadSuccess: false,

    /**
     * 文件上传成功
     */
    uploadifySuccess: function (upfile, data, response) {
        var jsonData = eval("(" + data + ")");
        var docId = jsonData.data[0].docId;
        var hiddenSpan = $("#uploadContain").find("span.hidden");
        var newSpan = hiddenSpan.clone();
        $(newSpan).find("a span").html(upfile.name);
        $(newSpan).find("img").attr("docId", docId);
        $(newSpan).attr("docId", docId);
        if (upfile.name.isImg()) {
            $(newSpan).find("img").attr("src", getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + docId);
        }
        // 保存到上传文件数组中
        var tempFileItem = {};
        tempFileItem.docId = docId;
        tempFileItem.fileName = upfile.name;
        tempFileItem.docPath = jsonData.data[0].docPath;
        // 设置到标签保存
        $(newSpan).attr("docId", tempFileItem.docId);
        $(newSpan).attr("docName", tempFileItem.fileName);
        $(newSpan).attr("docPath", tempFileItem.docPath);
        // 增加显示
        $(newSpan).removeClass("hidden");
        hiddenSpan.parent().append(newSpan);
        // 增加到保存区
        file.upLoadedFiles.push(tempFileItem);
    },
    /**
     *  保存上传的数据
     */
    saveUpFile: function () {
        // 扫描文件
        var jsonData = {};
        var $form = $("#scan001", $.pdialog.getCurrent());
        if ($form.length > 0 && $form.valid()) {
            jsonData = $form.serializeArray();
        }

        // 上传文件
        var docIdArr = [];
        for (var i = 0; i < file.upLoadedFiles.length; i++) {
            docIdArr.push(file.upLoadedFiles[i].docId);
        }
        // 文档为空返回
        if (docIdArr.length == 0 && jsonData.length == 0) {
            // 文档关联
            file.getDocTypeRelUrl = file.defaultDocTypeRelUrl;
            // 提交回调
            file.successCallBack = null;
            // 关闭镜头
            scan.closeScan();
            // 关闭当前框
            $.pdialog.closeCurrent();
            return;
        }

        jsonData.push({"name": "docId", "value": docIdArr.join(",")});
        jsonData.push({"name": "docTypeName", "value": $("input[name=docTypeName]", $.pdialog.getCurrent()).val()});
        jsonData.push({"name": "relType", "value": $("input[name=relType]", $.pdialog.getCurrent()).val()});
        jsonData.push({"name": "relId", "value": $("input[name=relId]", $.pdialog.getCurrent()).val()});
        jsonData.push({"name": "prjCd", "value": getPrjCd()});

        // 提交页面数据
        var url = getGlobalPathRoot() + "oframe/common/file/file-updateFileRel.gv";
        if (docIdArr.length == 0) {
            url = getGlobalPathRoot() + "oframe/common/file/file-upbase64.gv";
        }
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.data = jsonData;
        // 提交数据
        core.ajax.sendHPacket(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                // 标记文件上传成功
                file.uploadSuccess = true;
                // 获取scan上传的文件
                var fileArray = jsonData.data;
                for (var i = 0; i < fileArray.length; i++) {
                    var tempFileItem = {};
                    tempFileItem.docId = jsonData.data[i].docId;
                    tempFileItem.fileName = jsonData.data[i].docName;
                    tempFileItem.docPath = jsonData.data[i].docPath;
                    scan.upLoadedFiles.push(tempFileItem);
                }
                // 提交成功进行回调
                var callBackResult = true;
                if ($.isFunction(file.successCallBack)) {
                    callBackResult = file.successCallBack(file.getAllUploadFiles());
                }
                if (callBackResult) {
                    // 文档关联
                    file.getDocTypeRelUrl = file.defaultDocTypeRelUrl;
                    // 提交回调
                    file.successCallBack = null;
                    // 关闭镜头
                    scan.closeScan();
                    // 关闭返回处理结果
                    $.pdialog.closeCurrent();
                } else {
                    return false;
                }

            } else {
                // 错误提示
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 删除上传的文件
     * @param obj
     */
    removeFile: function (obj) {
        $(obj).parent().remove();
    },

    /**
     * 查看图片
     * @param obj
     */
    viewImg: function (obj) {
        var src = $(obj).attr("src");
        if ("" != src) {
            $(obj).parents("div:eq(0)").find("img.selected").removeClass("selected");
            $(obj).addClass("selected");
            window.open(src);
        }
    },
    /**
     * 下载文件
     * @param docId 文件编号
     */
    viewPdf: function (obj) {
        var docId = $(obj).attr("docId");
        window.open(getGlobalPathRoot() + "oframe/common/file/file-viewPdf.gv?prjCd=" + getPrjCd() + "&docId=" + docId);
    },

    removeImg: function (obj) {
        var src = $(obj).parent().find("img").attr("src");
        if (src != null) {
            var showSrc = $("#file_imgViewer").attr("src");
            if (showSrc == src) {
                $("#file_imgViewer").attr("src", "").hide();
            }
        }
        var docId = $(obj).closest("span.js_pic_span").attr("docId");
        if (docId) {
            alertMsg.confirm("确定删除该附件吗？", {
                okCall: function () {
                    var packet = new AJAXPacket();
                    packet.data.add("docId", docId);
                    packet.url = getGlobalPathRoot() + "/oframe/common/file/file-delete.gv";
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            //删除展示框。
                            $(obj).closest("span.js_pic_span").remove();
                        } else {
                            // 错误提示
                            alertMsg.error(jsonData.errMsg);
                        }
                    }, true);
                }
            });
        }
    },

    getUploadFiles: function () {
        return file.upLoadedFiles;
    },

    clearUploadFiles: function () {
        file.upLoadedFiles = [];
    },

    cancelUpload: function () {
        return true;
    },
    /**
     * 切换选择模式
     * @param modelFlag
     */
    switchModel: function (modelFlag) {
        if ("1" == modelFlag) {
            // 选择已有模式
            $("div.js_upload", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_uploaded", $.pdialog.getCurrent()).removeClass("hidden");
            $("div.js_scan", $.pdialog.getCurrent()).addClass("hidden");
            // 刷新文件选择
            file.flashPic();
        } else if ("2" == modelFlag) {
            // 选择已有模式
            $("div.js_upload", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_uploaded", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_scan", $.pdialog.getCurrent()).removeClass("hidden");
            // 刷新文件选择
            setTimeout(scan.init, 500);
        } else {
            $("div.js_upload", $.pdialog.getCurrent()).removeClass("hidden");
            $("div.js_uploaded", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_scan", $.pdialog.getCurrent()).addClass("hidden");
        }
    },
    /**
     * 选择已有图片模式数据更新
     */
    flashPic: function () {
        var sltItems = file.getAllUploadFiles();
        // 转换为Map处理
        var tempMap = new Map();
        for (var i = 0; i < sltItems.length; i++) {
            tempMap.put(sltItems[i].docId, sltItems[i]);
        }
        // 选择的类型
        var sltDocTypeName = $("select[name=sltDocTypeName]", $.pdialog.getCurrent()).val();
        $("div.js_uploaded span.js_pic_span", $.pdialog.getCurrent()).each(function () {
            var $this = $(this);
            var docId = $this.attr("docId");
            var docTypeName = $this.attr("docTypeName");
            if (sltDocTypeName != "") {
                if (sltDocTypeName == docTypeName) {
                    $this.removeClass("hidden");
                } else {
                    $this.addClass("hidden");
                }
            } else {
                $this.removeClass("hidden");
            }
            if (tempMap.get(docId)) {
                $this.find("input[type=checkbox]").attr("checked", true);
            } else {
                $this.find("input[type=checkbox]").removeAttr("checked");
            }
        });
    },

    /**
     * 关闭文件上传，关闭镜头
     */
    closeUpFile: function () {
        if (scan.isOpen) {
            scan.closeScan();
        }
        scan.clearUploadFiles();
        file.clearUploadFiles();
        // 文档关联
        file.getDocTypeRelUrl = file.defaultDocTypeRelUrl;
        // 提交回调
        file.successCallBack = null;
    }
};

var scan = {

    /**
     * 上传文件列表
     */
    upLoadedFiles: [],

    /**
     * 设备是否打开
     */
    isOpen: false,

    /**
     * 提交上传
     */
    sendUpload: function () {
        var $form = $("#scan001", $.pdialog.getCurrent());
        if ($("input[name=upFile][value!='']", $.pdialog.getCurrent()).length == 0) {
            // 没有上传附件直接返回
            $.pdialog.closeCurrent();
        } else if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/common/file/file-upbase64.gv";
            var ajaxPacket = new AJAXPacket(url);
            var jsonData = $form.serializeArray();
            jsonData.push({"name": "prjCd", "value": getPrjCd()});
            ajaxPacket.data.data = jsonData;
            // 提交
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    var fileArray = jsonData.data;
                    for (var i = 0; i < fileArray.length; i++) {
                        var tempFileItem = {};
                        tempFileItem.docId = jsonData.data[i].docId;
                        tempFileItem.fileName = jsonData.data[i].docName;
                        tempFileItem.docPath = jsonData.data[i].docPath;
                        scan.upLoadedFiles.push(tempFileItem);
                    }
                    $.pdialog.closeCurrent();
                } else {
                    alert(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 初始化页面，打开高拍仪
     */
    init: function () {
        if (scan.isOpen) {
            return;
        }
        if (!isIE()) {
            alert("文档扫描只支持IE浏览器，请使用IE浏览器！");
            return;
        }
        // 根据js的脚本内容，必须先获取object对象
        Capture = $("#ScanCapture", $.pdialog.getCurrent())[0];
        if (!Capture) {
            return;
        }
        // 设备号
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        // 打开文档摄像头
        var result = Capture.OpenDevice(nDeviceIndex);

        if (result == 0) {
            // 标记镜头已经打开
            scan.isOpen = true;
            // 设置图片质量
            Capture.SetJPGQuality(70);
            // 调整切边模式
            scan.cutType(parseInt($("input[name=cutType]", $.pdialog.getCurrent()).val()));
            // 默认选择90度
            scan.changeRotation(90);
            // 设置DPI
            scan.changeDpi(200);
        } else {
            alertMsg.error("镜头打开失败！错误编码：" + result);
        }
    },

    /**
     * 关闭页面，关闭高拍仪
     */
    closeScan: function () {
        if (!scan.isOpen) {
            return true;
        }
        var currentIdx = scan.getCurrentDeviceIdx();
        var result = 0;
        if (!scan.isDigit(currentIdx)) {
            // 全部执行一次关闭
            Capture.CloseDeviceEx();
        } else {
            result = Capture.CloseDeviceEx();
        }
        if (result != 0) {
            alertMsg.error("镜头关闭失败!错误编码：" + result);
        } else {
            Capture = null;
            scan.isOpen = false;
        }
        return true;
    },

    /**
     * 正则判断文本框中的内容是否为数字
     */
    isDigit: function (iVal) {
        var patrn = /^-?\d+(\.\d+)?$/;
        if (!patrn.exec(iVal)) {
            return false
        }
        return true
    },

    /**
     * 打开摄像头的设置界面
     */
    changeScan: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        Capture.ShowDevicePages(nDeviceIndex);
    },

    /**
     * 重启镜头
     */
    resetScan: function () {
        scan.closeScan();
        scan.init();
    },

    getCurrentDeviceIdx: function () {
        return parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
    },

    /**
     * 切换摄像头
     */
    changeDevice: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var currentIdx = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        if (currentIdx == 0) {
            currentIdx = 1;
        } else {
            currentIdx = 0;
        }
        //关闭摄像头
        var result = Capture.CloseDeviceEx();
        if (result != 0) {
            alertMsg.error("镜头关闭失败！错误编码：" + result);
        }
        // 镜头已经关闭
        scan.isOpen = false;
        // 重新打开摄像头
        $("input[name=deviceIdx]", $.pdialog.getCurrent()).val(currentIdx);
        scan.init();
    },

    changeColorMode: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        var colorMode = parseInt($("input[name=colorMode]", $.pdialog.getCurrent()).val());
        colorMode++;
        if (colorMode > 2) {
            colorMode = 0;
        }
        //关闭摄像头
        if (Capture.SetColorMode(colorMode) == 0) {
            $("input[name=colorMode]", $.pdialog.getCurrent()).val(colorMode);
            scan.flashText();
        }
    },

    /**
     *
     */
    changeCutType: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        var cutType = parseInt($("input[name=cutType]", $.pdialog.getCurrent()).val());
        cutType++;
        if (cutType > 2) {
            cutType = 0;
        }
        scan.cutType(cutType);
    },

    /**
     * 设置拍照存档的xDPI,yDPI
     */
    changeDpi: function (dpiValue) {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        if (Capture.OpenDevice(nDeviceIndex) != 1) {
            return;
        }
        if (Capture.OpenDevice(nDeviceIndex) == 1) {
            if (dpiValue == "") {
                alertMsg.error("DPI值都不能为空！");
                return;
            }
            if (scan.isDigit(dpiValue)) {
                var dpi = parseInt(dpiValue);
                if (Capture.SetGrabbedDPIEx(dpi) != 0) {
                    alertMsg.error("DPI设置失败");
                } else {
                    scan.flashText();
                }
            } else {
                alertMsg.error("含有非法字符，请重新输入数字！");
            }
        } else {
            alertMsg.error("找不到设备或打开失败！");
        }
    },

    /**
     * 旋转摄像头
     * @param rotation 旋转角度
     */
    changeRotation: function (rotation) {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        var nRotation = parseInt(rotation);
        if (nRotation % 90 != 0) {
            Capture.SetDeviceRotate(nDeviceIndex, 0);
        }
        var result = Capture.SetDeviceRotate(nDeviceIndex, nRotation);
        if (result != 0) {
            alertMsg.error("旋转镜头失败");
        }
    },

    //拍照并且存档,若勾选条码则一起获取条码信息
    catchPic: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var saveFilePath = $("input[name=fileDir]", $.pdialog.getCurrent()).val();
        var fileName = (new Date()).getTime() + ".jpg";
        var szFileName = saveFilePath + fileName;
        var base64Str = Capture.GetBase64String();
        scan.addPreview(base64Str, fileName);
    },

    /**
     * 切边类型
     * @param szMode 设置切边类型
     */
    cutType: function (szMode) {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        var result01;
        if (szMode == 0) {
            result01 = Capture.SetCutPageType(nDeviceIndex, 0);
        } else if (szMode == 1) {
            result01 = Capture.SetCutPageType(nDeviceIndex, 1);
        } else if (szMode == 2) {
            result01 = Capture.SetCutPageType(nDeviceIndex, 2);
        } else {
            result01 = Capture.SetCutPageType(nDeviceIndex, 0);
        }
        if (result01 == 0) {
            $("input[name=cutType]", $.pdialog.getCurrent()).val(szMode);
            scan.flashText();
        } else {
            alertMsg.error("切边模式切换失败!");
        }
    },

    /**
     * 预览图片
     * @param fullFile 图片文件名称
     * @param fileName 文件名称
     * @param fileBase64 文件的Base64编码
     */
    addPreview: function (fileBase64, fileName) {
        var ulContainer = $("#picUl", $.pdialog.getCurrent());
        var liTemplate = $("li.hidden", ulContainer);
        // 拷贝信息的
        var newSpanText = $("#scanTemplate", $.pdialog.getCurrent());
        var newObj = liTemplate.clone().append(newSpanText.html());
        var pic = $("img", newObj);
        //var file = document.getElementById("f");
        var ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // gif在IE浏览器暂时无法显示
        if (ext != 'png' && ext != 'jpg' && ext != 'bmp' && ext != 'tif') {
            alertMsg.error("文件必须为图片！");
            return;
        }
        // IE浏览器
        if (document.all) {
            // 设置img的src为base64编码的透明图片 取消显示浏览器默认图片
            pic.attr("src", 'data:image/jpeg;base64,' + fileBase64);
            pic.attr("realSrc", 'data:image/jpeg;base64,' + fileBase64);
        }
        // 删除文件
        $("input[name=fileName]", newObj).val(fileName).addClass("required");
        $("input[name=upFile]", newObj).val(fileBase64);
        ulContainer.append(newObj);
        newObj.removeClass("hidden");
    },

    /**
     * 刷新状态栏
     */
    flashText: function () {
        // 稳当类型
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        var menuContain = $("ul.js_scan_menu");
        if (nDeviceIndex == 0) {
            $("span.deviceType", menuContain).html("[文档镜头]");
        } else if (nDeviceIndex == 1) {
            $("span.deviceType", menuContain).html("[人像镜头]");
        } else {
            $("span.deviceType", menuContain).html("");
        }
        // 切边模式
        var cutType = parseInt($("input[name=cutType]", $.pdialog.getCurrent()).val());
        if (cutType == 0) {
            $("span.cutType", menuContain).html("[不切边]");
        } else if (cutType == 1) {
            $("span.cutType", menuContain).html("[自动切边]");
        } else if (cutType == 2) {
            $("span.cutType", menuContain).html("[自定义切边]");
        } else {
            $("span.cutType", menuContain).html("");
        }
        // 色彩模式
        var colorMode = parseInt($("input[name=colorMode]", $.pdialog.getCurrent()).val());
        if (colorMode == 0) {
            $("span.colorMode", menuContain).html("[彩色]");
        } else if (colorMode == 1) {
            $("span.colorMode", menuContain).html("[灰度]");
        } else if (colorMode == 2) {
            $("span.colorMode", menuContain).html("[黑白]");
        } else {
            $("span.colorMode", menuContain).html("");
        }
    },

    /**
     * 删除图片
     * @param obj 点击对象
     */
    removePic: function (obj) {
        $(obj).parent().parent().remove();
    },

    /**
     * 获取上传的文件列表
     * @returns {*}
     */
    getUploadFiles: function () {
        return scan.upLoadedFiles;
    },

    /**
     * 清除上传的文件列表
     */
    clearUploadFiles: function () {
        scan.upLoadedFiles = [];
        scan.isOpen = false;
    }
};


/**
 * 图片翻页函数
 * @type {{keyJump: keyJump}}
 */
/**
 * 文件查看器
 * @type {{closeFunc: null}}
 */
var FileViewer = {
    closeFunc: null
};
var ImgPage = {

    /**
     * 自定义图片查看路径预览多个图片
     * @param imgObj
     */
    viewPicWithSlfUrl: function (imgBaseUrl, paramsIds, options) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("imgBaseUrl", imgBaseUrl);
        ajaxPacket.data.add("docIds", paramsIds);
        if (options) {
            for (var item in options) {
                ajaxPacket.data.add(item, options[item]);
            }
        }
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            navTab.getCurrentPanel().append($(response));
            FileViewer.closeFunc = function () {
            };
        });
    },

    /**
     * 打开查看单个图片视图，不允许上传
     * @param imgObj
     */
    viewPic: function (imgObj) {
        var docId = $(imgObj).attr("docId");
        if (docId) {
            var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("docIds", docId);
            ajaxPacket.data.add("canDel", "false");
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                navTab.getCurrentPanel().append($(response));
                FileViewer.closeFunc = function () {
                };
            });
        }
    },

    /**
     * 查看多个图片，运行上传
     * @param docIds
     */
    viewPics: function (docIds, currentDocId) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("docIds", docIds);
        ajaxPacket.data.add("currentDocId", currentDocId);
        ajaxPacket.data.add("canDel", "false");
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            navTab.getCurrentPanel().append($(response));
            FileViewer.closeFunc = function () {
            };
        });
    },


    /**
     * 查看多个图片，运行上传
     * @param docIds
     */
    viewPicsDiag: function (docIds, currentDocId) {
        if (!docIds) {
            docIds = "";
        }
        if (!currentDocId) {
            currentDocId = "";
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv?canDel=false&dialogFlag=true"
            + "&currentDocId=" + currentDocId
            + "&docIds=" + docIds;
        //
        var iWidth = $(window).width() * 0.6; //弹出窗口的宽度;
        var iHeight = $(window).height() - 100; //弹出窗口的高度;
        if (isIE()) {
            // 非模态对话框仅IE支持
            window.showModelessDialog(url, "附件浏览", "dialogWidth=" + iWidth + "px;dialogHeight=" + iHeight + "px;status=no;help=no;scrollbars=no");
        } else {

            var iTop = (window.screen.availHeight - 30 - iHeight) / 2; //获得窗口的垂直位置;
            var iLeft = (window.screen.availWidth - 10 - iWidth) / 2; //获得窗口的水平位置;
            window.open(url, "附件浏览", "height=" + iHeight + ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft);
        }
    },


    keyJump: function (event) {
        var lKeyCode = null;
        if (navigator.appname == "Netscape") {
            lKeyCode = event.which;
        } else {
            lKeyCode = event.keyCode
        }
        if (lKeyCode == 38) {
            // 上一页
            if (!$("#imgDialog #rotRight").is(":hidden")) {
                $("#imgDialog #rotRight").click();
            }
            stopEvent(event);
        } else if (lKeyCode == 39) {
            // 下一页
            if (!$("#imgDialog div.btnRight").is(":hidden")) {
                $("#imgDialog div.btnRight").click();
            }
            stopEvent(event);
        } else if (lKeyCode == 37) {
            // 上一页
            if (!$("#imgDialog div.btnLeft").is(":hidden")) {
                $("#imgDialog div.btnLeft").click();
            }
            stopEvent(event);
        } else if (lKeyCode == 40) {
            // 上一页
            if (!$("#imgDialog #rotLeft").is(":hidden")) {
                $("#imgDialog #rotLeft").click();
            }
            stopEvent(event);
        }
    }
};
var upFile = {
    /**
     * 关闭上传窗口
     * @param param
     * @returns {boolean}
     */
    uploadLoadFileClosed: function (param) {
        var uploadedFiles = file.getUploadFiles();
        if (file.uploadSuccess) {
            var divId = param.divId;
            var hiddenSpan = $("#" + divId).find("label.hidden");
            for (var i = 0; i < uploadedFiles.length; i++) {
                var uploadedFile = uploadedFiles[i];
                var newSpan = hiddenSpan.clone();
                var downLoadUrl = getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + uploadedFile.docId;
                var imgSrc = downLoadUrl;
                var fileName = uploadedFile.fileName;
                if (!fileName.isImg()) {
                    imgSrc = getGlobalPathRoot() + "oframe/themes/images/word.png";
                }
                $(newSpan).find("img").attr("src", imgSrc).attr("realSrc", imgSrc).attr("title", fileName);
                $(newSpan).find("input[name=docId]").val(uploadedFile.docId);
                $(newSpan).find("input[name=docName]").val(fileName);
                $(newSpan).find("input[name=docTypeName]").val($("#" + divId).attr("docTypeName"));
                $(newSpan).find("a").attr("href", downLoadUrl).attr("title", fileName).find("span").html(fileName);
                $(newSpan).removeClass("hidden");
                $("#" + divId).append(newSpan);
            }
        }
        return true;
    },
    openUploadFile: function (divContainer) {
        var divContainerId = $(divContainer).closest("div.drag").attr("id");
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?docTypeName="
            + $("#" + divContainerId).attr("docTypeName")
            + "&relType=" + $("#" + divContainerId).attr("relType")
            + "&relId=" + $("#" + divContainerId).attr("relId");
        $.pdialog.open(url, "file", "附件上传", {
            mask: true,
            close: upFile.uploadLoadFileClosed,
            param: {divId: divContainerId}
        });
    },

    /**
     * 正则判断文本框中的内容是否为数字
     */
    isDigit: function (iVal) {
        var patrn = /^-?\d+(\.\d+)?$/;
        if (!patrn.exec(iVal)) {
            return false
        }
        return true
    },

    /**
     * 关闭页面，关闭高拍仪
     */
    closeScan: function (param) {
        if (!isIE()) {
            return true;
        }
        if (Capture == null && param == null) {
            return true;
        }
        if (typeof(Capture) == "undefined" || typeof(scan) == "undefined") {
            return true;
        }
        var currentIdx = scan.getCurrentDeviceIdx();
        var result;
        if (Capture != null && Capture.CloseDeviceEx && $.isFunction(Capture.CloseDeviceEx)) {
            // 全部执行一次关闭
            Capture.CloseDeviceEx();
        }
        if (result != 0) {
            alertMsg.error("镜头关闭失败!");
        } else {
            Capture = null;
        }
        var uploadedFiles = scan.getUploadFiles();
        var divId = param.divId;
        var hiddenSpan = $("#" + divId).find("label.hidden");
        for (var i = 0; i < uploadedFiles.length; i++) {
            var uploadedFile = uploadedFiles[i];
            var newSpan = hiddenSpan.clone();
            var downLoadUrl = getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + uploadedFile.docId;
            var imgSrc = downLoadUrl;
            var fileName = uploadedFile.fileName;
            if (!fileName.isImg()) {
                imgSrc = getGlobalPathRoot() + "oframe/themes/images/word.png";
            }
            $(newSpan).find("img").attr("src", imgSrc).attr("realSrc", imgSrc).attr("title", fileName);
            $(newSpan).find("input[name=docId]").val(uploadedFile.docId);
            $(newSpan).find("input[name=docName]").val(fileName);
            $(newSpan).find("input[name=docTypeName]").val($("#" + divId).attr("docTypeName"));
            $(newSpan).find("a").attr("href", downLoadUrl).attr("title", fileName).find("span").html(fileName);
            $(newSpan).removeClass("hidden");
            $("#" + divId).append(newSpan);
        }
        return true;
    },

    /**
     * 打开
     * @param divContainer
     */
    openScanPage: function (divContainer) {
        var divContainerId = $(divContainer).closest("div.drag").attr("id");
        var url = getGlobalPathRoot() + "oframe/common/file/file-scan.gv?docTypeName="
            + $("#" + divContainerId).attr("docTypeName")
            + "&relType=" + $("#" + divContainerId).attr("relType")
            + "&relId=" + $("#" + divContainerId).attr("relId");
        $.pdialog.open(url, "file", "文档扫描", {
            mask: true,
            max: true,
            close: upFile.closeScan,
            param: {divId: divContainerId}
        });
    },

    /**
     * 实现文件的拖拽上传
     * @param event 拖拽事件
     */
    dropFile: function (event, divContainer) {
        var divId = $(divContainer).closest("div.drag").attr("id");
        if (typeof FileReader == "undefined") {
            alertMsg.warn("您的浏览器不支持拖拽上传");
        }
        var dragFile = event.dataTransfer.files;
        //上传文件到服务器
        var fd = new FormData();
        for (var i = 0; i < dragFile.length; i++) {
            var tempFile = dragFile[i];
            var reader = new FileReader();
            reader.readAsDataURL(tempFile);
            reader.onload = (function (file) {
                return function (e) {
                }
            })(tempFile);
            fd.append($("#" + divId).attr("docTypeName"), tempFile);
        }
        // 增加关联类型
        fd.append("relType", $("#" + divId).attr("relType"));
        fd.append("relId", $("#" + divId).attr("relId"));
        fd.append("prjCd", getPrjCd());
        // 上传文件
        upFile.sendFileToServer(fd, divId);
        // 停止响应事件
        stopEvent();
    },

    sendFileToServer: function (formData, divId) {
        var uploadURL = getGlobalPathRoot() + "oframe/common/file/file-upload.gv";
        //Extra Data.
        var extraData = {};
        var jqXHR = $.ajax({
            xhr: function () {
                var xhrObj = $.ajaxSettings.xhr();
                return xhrObj;
            },
            url: uploadURL,
            type: "POST",
            contentType: false,
            processData: false,
            cache: false,
            data: formData,
            success: function (data) {
                var jsonData = eval("(" + data + ")");
                if (jsonData.success) {
//
                    var hiddenSpan = $("#" + divId).find("label.hidden");
                    for (var i = 0; i < jsonData.data.length; i++) {
                        var uploadedFile = jsonData.data[i];
                        var newSpan = hiddenSpan.clone();
                        var downLoadUrl = getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + uploadedFile.docId;
                        var imgSrc = downLoadUrl;
                        var fileName = uploadedFile.docName;
                        if (!fileName.isImg()) {
                            imgSrc = getGlobalPathRoot() + "oframe/themes/images/word.png";
                        }
                        $(newSpan).find("img").attr("src", imgSrc).attr("realSrc", imgSrc).attr("title", fileName);
                        $(newSpan).find("input[name=docId]").val(uploadedFile.docId);
                        $(newSpan).find("input[name=docName]").val(fileName);
                        $(newSpan).find("input[name=docTypeName]").val($("#" + divId).attr("docTypeName"));
                        $(newSpan).find("a").attr("href", downLoadUrl).attr("title", fileName).find("span").html(fileName);
                        $(newSpan).removeClass("hidden");
                        $("#" + divId).append(newSpan);
                    }
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }

            }
        });
    },
    removeFile: function (obj) {
        var packet = new AJAXPacket();
        packet.url = $(obj).attr("href");
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                $(obj).parent().remove();
            } else {
            }
        });
    }
};
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
};var table = {
    /**
     * 拷贝增加行
     */
    addRow: function (tableId, clickObject) {
        var $table = $("#" + tableId);
        var hiddenRow = $table.find("tr:eq(1)");
        var copyRow = $(hiddenRow).clone().removeClass("hidden");
        // 获取行号
        if (clickObject) {
            var index = $table.find("tr").index($(clickObject).parent().parent());
            if (index == 0) {
                $(hiddenRow).after(copyRow);
            } else {
                $(clickObject).parent().parent().after(copyRow);
            }
        } else {
            $table.append(copyRow);
        }
        $(copyRow).initUI();
        return copyRow;
    },

    /**
     * 拷贝增加行
     */
    addRowNoInit: function (tableId, clickObject) {
        var $table = $("#" + tableId);
        var hiddenRow = $table.find("tr:eq(1)");
        var copyRow = $(hiddenRow).clone().removeClass("hidden");
        // 获取行号
        if (clickObject) {
            var index = $table.find("tr").index($(clickObject).parent().parent());
            if (index == 0) {
                $table.append(copyRow);
            } else {
                $(clickObject).parent().parent().after(copyRow);
            }
        } else {
            $table.append(copyRow);
        }
        return copyRow;
    },
    /**
     * 拷贝增加行
     */
    deleteRow: function (clickObject) {
        $(clickObject).closest("tr").remove();
    },

    /**
     * 行上移
     */
    upRow: function (tableId, clickObject) {
        // 获取行号
        var index = $("#" + tableId).find("tr").index($(clickObject).parent().parent());
        if (index == 2) {

        } else {
            var trObj = $(clickObject).parent().parent();
            $(trObj).prev().before($(trObj));
        }
    },
    /**
     * 行下移动
     */
    downRow: function (tableId, clickObject) {
        // 获取行号
        var index = $("#" + tableId).find("tr").index($(clickObject).parent().parent());
        if (index == 1) {

        } else {
            var trObj = $(clickObject).parent().parent();
            $(trObj).next().after($(trObj));
        }
    }
};var CheckUtil = {
    /**************************************************************************************/
    /*************************************数字的验证*****************************************/
    /**************************************************************************************/
    /**
     * 检查输入的一串字符是否全部是数字
     * 输入:str  字符串
     * 返回:true 或 flase; true表示为数字
     */
    checkNum: function
        (str) {
        return str.match(/\D/) == null;
    },
    /**
     * 检查输入的一串字符是否为小数
     * 输入:str  字符串
     * 返回:true 或 flase; true表示为小数
     */
    checkDecimal: function (str) {
        if (str.match(/^-?\d+(\.\d+)?$/g) == null) {
            return false;
        } else {
            return true;
        }
    },
    /**
     * 检测房屋大小（必须保留两位小数)
     * 输入:str  字符串
     * 返回:true 或 flase; true表示为合法的面积
     */
    checkHouseSize: function (str) {
        if (str.match(/^\d+(\.\d\d)$/g) == null) {
            return false;
        } else {
            return true;
        }
    },
    /**
     * 检查输入的一串字符是否为整型数据
     * 输入:str  字符串
     * 返回:true 或 flase; true表示为小数
     */
    checkInteger: function (str) {
        if (str.match(/^[-+]?\d*$/) == null) {
            return false;
        } else {
            return true;
        }
    },
    /**
     * 检查输入的一串字符是否是字符
     * 输入:str  字符串
     * 返回:true 或 flase; true表示为全部为字符 不包含汉字
     */
    checkStr: function (str) {
        if (/[^\x00-\xff]/g.test(str)) {
            return false;
        } else {
            return true;
        }
    },
    /**
     * 检查输入的一串字符是否包含汉字
     * 输入:str  字符串
     * 返回:true 或 flase; true表示包含汉字
     */
    checkChinese: function (str) {
        if (escape(str).indexOf("%u") != -1) {
            return true;
        } else {
            return false;
        }
    },


    /**
     * 检查输入的邮箱格式是否正确
     * 输入:str  字符串
     * 返回:true 或 flase; true表示格式正确
     */
    checkEmail: function (str) {
        if (str.match(/[A-Za-z0-9_-]+[@](\S*)(net|com|cn|org|cc|tv|[0-9]{1,3})(\S*)/g) == null) {
            return false;
        } else {
            return true;
        }
    },


    /**
     * 检查输入的手机号码格式是否正确
     * 输入:str  字符串
     * 返回:true 或 flase; true表示格式正确
     */
    checkMobilePhone: function (str) {
        if (str.match(/^1[3|4|5|7|8][0-9]\d{8}$/) == null) {
            return false;
        } else {
            return true;
        }
    },


    /**
     * 检查输入的固定电话号码是否正确
     * 输入:str  字符串
     * 返回:true 或 flase; true表示格式正确
     */
    checkTelephone: function (str) {
        if (str.match(/^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/) == null) {
            return false;
        }
        else {
            return true;
        }
    },

    /**
     * 检查QQ的格式是否正确
     * 输入:str  字符串
     *  返回:true 或 flase; true表示格式正确
     */
    checkQQ: function (str) {
        if (str.match(/^\d{5,10}$/) == null) {
            return false;
        }
        else {
            return true;
        }
    },

    /**
     * 检查输入的身份证号是否正确
     * 输入:str  字符串
     *  返回:true 或 flase; true表示格式正确
     */
    checkCard: function (str) {
        //15位数身份证正则表达式
        var arg1 = /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/;
        //18位数身份证正则表达式
        var arg2 = /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[A-Z])$/;
        if (str.match(arg1) == null && str.match(arg2) == null) {
            return false;
        }
        else {
            return true;
        }
    },

    /**
     * 检查输入的IP地址是否正确
     * 输入:str  字符串
     *  返回:true 或 flase; true表示格式正确
     */
    checkIP: function (str) {
        var arg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
        if (str.match(arg) == null) {
            return false;
        }
        else {
            return true;
        }
    },

    /**
     * 检查输入的URL地址是否正确
     * 输入:str  字符串
     *  返回:true 或 flase; true表示格式正确
     */
    checkURL: function (str) {
        if (str.match(/(http[s]?|ftp):\/\/[^\/\.]+?\..+\w$/i) == null) {
            return false
        }
        else {
            return true;
        }
    },

    /**
     * 检查输入的字符是否具有特殊字符
     * 输入:str  字符串
     * 返回:true 或 flase; true表示包含特殊字符
     * 主要用于注册信息的时候验证
     */
    checkQuote: function (str) {
        var items = new Array("~", "`", "!", "@", "#", "$", "%", "^", "&", "*", "{", "}", "[", "]", "(", ")");
        items.push(":", ";", "'", "|", "\\", "<", ">", "?", "/", "<<", ">>", "||", "//");
        items.push("admin", "administrators", "administrator", "管理员", "系统管理员");
        items.push("select", "delete", "update", "insert", "create", "drop", "alter", "trancate");
        str = str.toLowerCase();
        for (var i = 0; i < items.length; i++) {
            if (str.indexOf(items[i]) >= 0) {
                return true;
            }
        }
        return false;
    },

    /**************************************************************************************/
    /*************************************时间的验证*****************************************/
    /**************************************************************************************/

    /**
     * 检查日期格式是否正确
     * 输入:str  字符串
     * 返回:true 或 flase; true表示格式正确
     * 注意：此处不能验证中文日期格式
     * 验证短日期（2007-06-05）
     */
    checkDate: function (str) {
        //var value=str.match(/((^((1[8-9]\d{2})|([2-9]\d{3}))(-)(10|12|0?[13578])(-)(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(-)(11|0?[469])(-)(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(-)(0?2)(-)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)(-)(0?2)(-)(29)$)|(^([3579][26]00)(-)(0?2)(-)(29)$)|(^([1][89][0][48])(-)(0?2)(-)(29)$)|(^([2-9][0-9][0][48])(-)(0?2)(-)(29)$)|(^([1][89][2468][048])(-)(0?2)(-)(29)$)|(^([2-9][0-9][2468][048])(-)(0?2)(-)(29)$)|(^([1][89][13579][26])(-)(0?2)(-)(29)$)|(^([2-9][0-9][13579][26])(-)(0?2)(-)(29)$))/);
        var value = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
        if (value == null) {
            return false;
        }
        else {
            var date = new Date(value[1], value[3] - 1, value[4]);
            return (date.getFullYear() == value[1] && (date.getMonth() + 1) == value[3] && date.getDate() == value[4]);
        }
    },

    /**
     * 检查时间格式是否正确
     * 输入:str  字符串
     * 返回:true 或 flase; true表示格式正确
     * 验证时间(10:57:10)
     */
    checkTime: function (str) {
        var value = str.match(/^(\d{1,2})(:)?(\d{1,2})\2(\d{1,2})$/)
        if (value == null) {
            return false;
        }
        else {
            if (value[1] > 24 || value[3] > 60 || value[4] > 60) {
                return false
            }
            else {
                return true;
            }
        }
    },

    /**
     * 检查全日期时间格式是否正确
     * 输入:str  字符串
     * 返回:true 或 flase; true表示格式正确
     * (2007-06-05 10:57:10)
     */
    checkFullTime: function (str) {
        //var value = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/);
        var value = str.match(/^(?:19|20)[0-9][0-9]-(?:(?:0[1-9])|(?:1[0-2]))-(?:(?:[0-2][1-9])|(?:[1-3][0-1])) (?:(?:[0-2][0-3])|(?:[0-1][0-9])):[0-5][0-9]:[0-5][0-9]$/);
        if (value == null) {
            return false;
        } else {
            //var date = new Date(checkFullTime[1], checkFullTime[3] - 1, checkFullTime[4], checkFullTime[5], checkFullTime[6], checkFullTime[7]);
            //return (date.getFullYear() == value[1] && (date.getMonth() + 1) == value[3] && date.getDate() == value[4] && date.getHours() == value[5] && date.getMinutes() == value[6] && date.getSeconds() == value[7]);
            return true;
        }
    },


    /**************************************************************************************/
    /************************************身份证号码的验证*************************************/
    /**************************************************************************************/

    /**
     * 身份证15位编码规则：dddddd yymmdd xx p
     * dddddd：地区码
     * yymmdd: 出生年月日
     * xx: 顺序类编码，无法确定
     * p: 性别，奇数为男，偶数为女
     * <p />
     * 身份证18位编码规则：dddddd yyyymmdd xxx y
     * dddddd：地区码
     * yyyymmdd: 出生年月日
     * xxx:顺序类编码，无法确定，奇数为男，偶数为女
     * y: 校验码，该位数值可通过前17位计算获得
     * <p />
     * 18位号码加权因子为(从右到左) Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2,1 ]
     * 验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]
     * 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )
     * i为身份证号码从右往左数的 2...18 位; Y_P为脚丫校验码所在校验码数组位置
     *
     */
    Wi: [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1],// 加权因子
    ValideCode: [1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2],// 身份证验证位值.10代表X
    IdCardValidate: function (idCard) {
        idCard = CheckUtil.trim(idCard.replace(/ /g, ""));
        if (idCard.length == 15) {
            return CheckUtil.isValidityBrithBy15IdCard(idCard);
        }
        else if (idCard.length == 18) {
            var a_idCard = idCard.split("");// 得到身份证数组
            if (CheckUtil.isValidityBrithBy18IdCard(idCard)
                && CheckUtil.isTrueValidateCodeBy18IdCard(a_idCard)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    },

    /**
     * 判断身份证号码为18位时最后的验证位是否正确
     * @param a_idCard 身份证号码数组
     * @return
     */
    isTrueValidateCodeBy18IdCard: function (a_idCard) {
        var sum = 0; // 声明加权求和变量
        if (a_idCard[17].toLowerCase() == 'x') {
            a_idCard[17] = 10;// 将最后位为x的验证码替换为10方便后续操作
        }
        for (var i = 0; i < 17; i++) {
            sum += CheckUtil.Wi[i] * a_idCard[i];// 加权求和
        }
        valCodePosition = sum % 11;// 得到验证码所位置
        if (a_idCard[17] == CheckUtil.ValideCode[valCodePosition]) {
            return true;
        }
        else {
            return false;
        }
    },

    /**
     * 通过身份证判断是男是女
     * @param idCard 15/18位身份证号码
     * @return 'female'-女、'male'-男
     */
    maleOrFemalByIdCard: function (idCard) {
        idCard = trim(idCard.replace(/ /g, ""));// 对身份证号码做处理。包括字符间有空格。
        if (idCard.length == 15) {
            if (idCard.substring(14, 15) % 2 == 0) {
                return 'female';
            }
            else {
                return 'male';
            }
        }
        else if (idCard.length == 18) {
            if (idCard.substring(14, 17) % 2 == 0) {
                return 'female';
            }
            else {
                return 'male';
            }
        }
        else {
            return null;
        }
    },

    /**
     * 验证18位数身份证号码中的生日是否是有效生日
     * @param idCard 18位书身份证字符串
     * @return
     */
    isValidityBrithBy18IdCard: function (idCard18) {
        var year = idCard18.substring(6, 10);
        var month = idCard18.substring(10, 12);
        var day = idCard18.substring(12, 14);
        var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
        // 这里用getFullYear()获取年份，避免千年虫问题
        if (temp_date.getFullYear() != parseFloat(year) ||
            temp_date.getMonth() != parseFloat(month) - 1 ||
            temp_date.getDate() != parseFloat(day)) {
            return false;
        }
        else {
            return true;
        }
    },

    /**
     * 验证15位数身份证号码中的生日是否是有效生日
     * @param idCard15 15位书身份证字符串
     * @return
     */
    isValidityBrithBy15IdCard: function (idCard15) {
        var year = idCard15.substring(6, 8);
        var month = idCard15.substring(8, 10);
        var day = idCard15.substring(10, 12);
        var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
        // 对于老身份证中的你年龄则不需考虑千年虫问题而使用getYear()方法
        if (temp_date.getYear() != parseFloat(year) ||
            temp_date.getMonth() != parseFloat(month) - 1 ||
            temp_date.getDate() != parseFloat(day)) {
            return false;
        }
        else {
            return true;
        }
    },

    //去掉字符串头尾空格
    trim: function (str) {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }
};/**
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
})(jQuery);// 定义全局divID变量
var divId = "dataList";
var Query = {
    /**
     * 获取翻页的div对象
     * @param _divId
     * @returns {*|jQuery|HTMLElement}
     */
    getDivObj: function (_divId) {
        var divObj = $("#" + _divId, navTab.getCurrentPanel());
        if ($.pdialog.getCurrent()) {
            var tempObj = $("#" + _divId, $.pdialog.getCurrent());
            if (tempObj.length > 0) {
                divObj = tempObj;
            }
        }

        return divObj;
    },

    /**
     * 查询员工信息表单提交
     *
     * @param formId
     *            表单ID
     * @param _divId
     *            展示结果DIV展示区域
     * @param _divPId
     *            展示结果DIV展示区域
     * @param str
     *            回调执行字符串
     */
    queryList: function (formId, _divId, str) {
        var divObj = Query.getDivObj(_divId);
        var $pObj = divObj.parent();
        // 提交表单
        var formObj = $("#" + formId);
        // 提交表单验证
        if (!formObj.valid()) {
            return;
        }
        // 查询按钮提交
        var defaultSortColumn = "";
        var defaultSortOrder = "";
        $("th.sortable", $pObj).each(
            function () {
                if (defaultSortColumn == "") {
                    var sortColumn = $(this).attr("sortColumn");
                    defaultSortOrder = $(this).attr("defaultSort") || "";
                    defaultSortColumn = $(this).attr("defaultSort") ? sortColumn : "";
                    if (defaultSortColumn != "") {
                        $(this).siblings("th").andSelf().removeClass("asc desc");
                        $(this).addClass(defaultSortOrder);
                        return false;
                    } else {
                        return true;
                    }
                }
            });
        $pObj.find("input[name=currentPage]").val("1");
        // 序列化表单数据
        var jsonData = $(formObj).serializeArray();
        var hasPrjCd = false;
        for (var i = 0; i < jsonData.length; i++) {
            var tempItem = jsonData[i];
            if ("prjCd" == tempItem.name) {
                hasPrjCd = true;
                break;
            }
        }
        if (!hasPrjCd) {
            jsonData.push({
                name: 'prjCd',
                value: getPrjCd()
            });
        }

        jsonData.push({
            name: 'sortColumn',
            value: defaultSortColumn
        });
        jsonData.push({
            name: 'currentPage',
            value: '1'
        });
        jsonData.push({
            name: 'sortOrder',
            value: defaultSortOrder
        });


        Query.pubQuery(jsonData, _divId, str);
    },
    /**
     * 公共查询界面
     */
    pubQuery: function (jsonData, _divId, str, flag) {
        var divObj = Query.getDivObj(_divId);
        var $pObj = divObj.parent();
        var packet = new AJAXPacket(getGlobalPathRoot() + "oframe/common/page/page-list.gv");
        var pageSize = $pObj.find("input[name='pageSize']").val();
        if (!pageSize && !jsonData.pageSize) {
            pageSize = 20;
        }
        if (flag != "1") {
            jsonData.push({
                name: 'divId',
                value: _divId
            });
            jsonData.push({
                name: 'pageSize',
                value: pageSize
            });
            packet.data.data = jsonData;
        } else {
            packet.data.add("divId", _divId);
            packet.data.add("pageSize", pageSize);
            packet.data.data = jsonData;
        }
        // 判断是点击查询还是翻页
        core.ajax.sendPacketHtml(packet, function (response) {
            var r = $(response);
            var toContain = $(".gridScroller tbody", divObj);
            if (toContain.length == 0) {
                toContain = divObj;
            }
            var resultObj = r.find("tbody");
            if (resultObj.length == 0) {
                resultObj = r.find("#result");
            }
            var errMsg = $("input[name=errMsg]", r).val();
            if (errMsg != '') {
                // 查询失败l
                alertMsg.error(errMsg);
            } else {
                // 执行成功
                toContain.html(resultObj.html());
                Query.refresh(divObj);
                //  绑定更多按钮
                $("button.btn-more", toContain).each(function () {
                    $(this).bind("mouseover", function () {
                        $(this).openBtnMenu();
                    });
                    $(this).mouseout(function () {
                        $.fn.deActiveBtnMenu();
                    });
                });
                // 存在分页
                if (r.find("#pagebox")) {
                    $pObj.find("div[id=pagebox]").length ?
                        $pObj.find("div[id=pagebox]").html(r.find("#pagebox").html())
                        : divObj.after(r.find("#pagebox"));
                }
                core.ajax.loadjs(response);
                eval(str);
                //
                $("#" + _divId).find(":button.checkboxCtrl, :checkbox.checkboxCtrl").each(
                    function (i) {
                        $(this).removeAttr("checked");
                    }
                );
            }

        }, true);
        packet = null;
    },

    refresh: function (obj) {
        var frths = $(obj).find("div.gridThead table tr:first>th");
        if (frths.length > 0) {
            var frtds = $(obj).find("div.gridTbody table tr:first>td").addClass("fRow");
            frtds.each(function (i) {
                var newW = frths.eq(i).width();
                $(this).width(newW);
            });
        }
        var $trs = $(obj).find("div.gridTbody table tbody>tr");
        $trs.hoverClass().each(function () {
            var $tr = $(this);
            var $ftds = $(">td", this);
            $tr.click(function () {
                $trs.filter(".selected").removeClass("selected");
                $tr.addClass("selected");
            });
        });
        return false;
    },
    /**
     * Excel导出
     * @param listDiv  展示结果集区域div层id
     * @param colNames 导出列汉字名称
     * @param colModels 导出列对应字段名称
     * @param translation 待翻译字段信息 格式如：STATUS_CD-100
     */
    exportExcel: function (_divId, colNames, colModels, translation) {
        var divObj = Query.getDivObj(_divId);
        var $pObj = divObj.parent();
        // 查询条件
        var jsonData = $.parseJSON($("input[name=queryCondition]", $pObj).val());
        if (!jsonData) {
            alertMsg.info("请先执行查询");
            return;
        }
        // 传递列信息
        jsonData.colNames = colNames || "";
        jsonData.colModels = colModels || "";
        // 传递需要翻译字段
        if (translation) {
            jsonData.translation = translation || "";
        }
        var url = getGlobalPathRoot() + "oframe/common/page/page-export.gv";
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.data = jsonData;
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot()
                    + "oframe/common/file/file-down.gv?";
                downloadUrl = downloadUrl + "remoteFile="
                    + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile="
                    + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },
    //依据模板导出Excel文件  templatePath 模板路径
    exportExcelByTemplate: function (_divId, templatePath) {
        var divObj = Query.getDivObj(_divId);
        var $pObj = divObj.parent();
        // 查询条件
        var jsonData = $.parseJSON($("input[name=queryCondition]", $pObj).val());
        if (!jsonData) {
            alertMsg.info("请先执行查询");
            return;
        }
        // 传递列信息
        jsonData.templatePath = templatePath || "";

        var url = getGlobalPathRoot() + "oframe/common/page/page-export.gv";
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.data = jsonData;
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot()
                    + "oframe/common/file/file-down.gv?";
                downloadUrl = downloadUrl + "remoteFile="
                    + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile="
                    + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },
    /**
     * 非分页Excel导出 全称（export excel with out pagination）
     * @param formId 提交的form表单ID
     * @param colNames 导出列汉字名称
     * @param colModels 导出列对应字段名称
     * @param translation 待翻译字段信息 格式如：STATUS_CD-100
     */
    expExcelWOPagination: function (formId, colNames, colModels, translation) {
        // 提交表单
        var formObj = Query.getDivObj(formId);
        // 序列化表单数据
        var jsonData = $(formObj).serializeArray();
        // 传递列信息
        jsonData.push({
            name: 'colNames',
            value: colNames
        });
        jsonData.push({
            name: 'colModels',
            value: colModels
        });
        // 传递需要翻译字段
        if (translation) {
            jsonData.translation = translation || "";
        }
        var url = getGlobalPathRoot() + "npage/common/page/export.do";
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.data = jsonData;
        core.ajax.sendPacket(packet, function (response) {
            var jsonData = response.data;
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot()
                    + "npage/common/page/download.do?";
                downloadUrl = downloadUrl + "remoteFile="
                    + encodeURI(jsonData.resultMap.remoteFile);
                downloadUrl = downloadUrl + "&clientFile="
                    + encodeURI(jsonData.resultMap.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },
    /** * 翻页 ** */
    jumpPage: function (currentPage, _divId) {
        if (_divId == "") {
            return;
        }
        var divObj = Query.getDivObj(_divId);
        var $pObj = divObj.parent();
        $pObj.find("input[name='currentPage']").val(currentPage);
        // 翻页跳转
        var jsonData = $.parseJSON($pObj.find("input[name='queryCondition']").val());
        jsonData.currentPage = $pObj.find("input[name='currentPage']").val();
        jsonData.sortColumn = $pObj.find("input[name='sortColumn']").val();
        jsonData.sortOrder = $pObj.find("input[name='sortOrder']").val();
        jsonData.pageSize = $pObj.find("input[name='pageSize']").val();
        Query.pubQuery(jsonData, _divId, "", "1");
    },

    /** * 页面跳转 ** */
    skipTo: function (_divId) {
        var divObj = Query.getDivObj(_divId);
        var $pObj = divObj.parent();
        var pageNoValue = $pObj.find("input[name=skipToNo]").val();
        if (!isNaN(pageNoValue)) {
            var pageNo = parseInt(pageNoValue);
            if (pageNo > 0 && pageNo <= parseInt($pObj.find("input[name='totalPage']").val())) {
                Query.jumpPage(pageNo, _divId);
            } else {
                $pObj.find("input[name='skipToNo']").attr("value", '');
            }
        } else {
            $pObj.find("input[name='skipToNo']").attr("value", '');
        }

    },
    /** * 排序 ** */
    sort: function (divId, obj) {
        var divObj = Query.getDivObj(divId);
        var $pObj = divObj.parent();
        var jsonData = $.parseJSON($pObj.find("input[name=queryCondition]").val());
        var orderBy = $(obj).attr("sortColumn");
        if (!jsonData) {
            alertMsg.info("请先执行查询");

        } else {
            // 清除样式
            $(obj).siblings("th").andSelf().removeClass("asc desc");
            var oldSortOrder = $pObj.find("input[name=sortOrder]").val();
            var newSortOrder = "";
            if (oldSortOrder == "" || oldSortOrder == "desc") {
                newSortOrder = "asc";
            } else {
                newSortOrder = "desc";
            }
            // 赋值新的排序及样式
            $pObj.find("input[name=sortOrder]").val(newSortOrder);
            $pObj.find("input[name=sortColumn]").val(orderBy);
            $(obj).addClass(newSortOrder);
            // 刷新界面
            Query.jumpPage(1, divId);
        }
    }
};
var Page = {

    /**
     * 获取翻页的div对象
     * @param _divId
     * @returns {*|jQuery|HTMLElement}
     */
    getDivObj: function (_divId) {
        var divObj = $("#" + _divId, navTab.getCurrentPanel());
        if ($.pdialog.getCurrent()) {
            var tempObj = $("#" + _divId, $.pdialog.getCurrent());
            if (tempObj.length > 0) {
                divObj = tempObj;
            }
        }
        return divObj;
    },
    /**
     * 打开查询房源条件设置对话框
     */
    queryCondition: function (obj, opt) {
        var option = {left: 25};
        if (opt) {
            option = $.extend(option, opt);
        }
        var clickObj = $(obj);
        // 自定义展示的位置
        var showContainer = option.showContainer;
        // 默认值展示在按钮区域之后
        var barDiv = [];
        var showRsltDiv = [];
        if (!showContainer) {
            barDiv = clickObj.closest("div.panelBar");
            showRsltDiv = barDiv.next("div.tip");
        }
        if (clickObj.hasClass("active")) {
            clickObj.removeClass("active");
            if (showRsltDiv.length > 0) {
                showRsltDiv.remove();
            } else if (showContainer) {
                showContainer.empty();
                if ($.isFunction(option.closeCallBack)) {
                    option.closeCallBack();
                }
            }
        } else {
            // 增加活动样式
            clickObj.addClass("active");
            // 表单对象
            var formObj = option.formObj;
            var entityName = $("input[name=entityName]", formObj).val();
            var conditionNames = $("input[name=conditionName]", formObj).val();
            var conditions = $("input[name=condition]", formObj).val();
            var conditionValues = $("input[name=conditionValue]", formObj).val();
            var conditionValueTexts = $("input.js_conditionValue", formObj).val();
            var resultFields = $("input[name=resultField]", formObj).val();
            var canDefResult = $("input.js_canDefResult", formObj).val();
            var canDefCondition = $("input.js_canDefCondition", formObj).val();
            // 排序信息
            var sortColumn = $("input[name=sortColumn]", formObj).val();
            var sortOrder = $("input[name=sortOrder]", formObj).val();
            // 必须包含的字段
            var forceResultField = $("input[name=forceResultField]", formObj).val();
            // 请求地址
            var url = getGlobalPathRoot() + "oframe/common/page/page-condition.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("prjCd", getPrjCd());
            ajaxPacket.data.add("entityName", entityName);
            ajaxPacket.data.add("conditionNames", conditionNames);
            ajaxPacket.data.add("conditions", conditions);
            ajaxPacket.data.add("conditionValues", conditionValues);
            ajaxPacket.data.add("canDefResult", canDefResult);
            ajaxPacket.data.add("canDefCondition", canDefCondition);
            ajaxPacket.data.add("resultFields", resultFields);
            ajaxPacket.data.add("conditionValueTexts", conditionValueTexts);
            // 当前的功能模块
            ajaxPacket.data.add("tabOpCode", navTab.getCurrentPanel().attr('opCode'));
            // 排序字段
            ajaxPacket.data.add("sortColumn", sortColumn ? sortColumn : "");
            ajaxPacket.data.add("sortOrder", sortOrder ? sortOrder : "");
            // 必须包含的字段列表
            ajaxPacket.data.add("forceResultField", forceResultField ? forceResultField : "");
            // 展示选择界面
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var rsp = $(response);
                if (showContainer) {
                    showContainer.html(rsp);
                    showContainer.initUI();
                } else {
                    var left = clickObj.offset().left;
                    rsp.prepend($('<div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: ' + option.left + 'px;"></div>'));
                    barDiv.after(rsp);
                    rsp.initUI();
                    if (option.height) {
                        rsp.css("height", option.height);
                    }
                    if (option.width) {
                        rsp.css("width", option.width);
                    }
                }
                // 记录标记
                Page.relObj = clickObj;
                Page.formObj = option.formObj;
                Page.changeConditions = option.changeConditions;
                if (option.callback) {
                    option.callback(showContainer);
                }
            });
        }
    },

    /**
     * 打开查询房源条件设置对话框
     * (新调整的高级检索，带标签查询和按照人员查询)
     */
    queryConditionTwo: function (obj, opt) {
        var option = {left: 25};
        if (opt) {
            option = $.extend(option, opt);
        }
        var clickObj = $(obj);
        // 自定义展示的位置
        var showContainer = option.showContainer;
        // 默认值展示在按钮区域之后
        var barDiv = [];
        var showRsltDiv = [];
        if (!showContainer) {
            barDiv = clickObj.closest("div.panelBar");
            showRsltDiv = barDiv.next("div.tip");
        }
        if (clickObj.hasClass("active")) {
            clickObj.removeClass("active");
            if (showRsltDiv.length > 0) {
                // showRsltDiv.remove();
                showRsltDiv.hide();
            } else if (showContainer) {
                showContainer.empty();
            }
        } else {
            // 增加活动样式
            clickObj.addClass("active");
            // 如果页面存在，不调服务查询，直接返回当前页面
            if (showRsltDiv.length > 0) {
                showRsltDiv.show();
                return;
            }
            // 表单对象
            var formObj = option.formObj;
            var entityName = $("input[name=entityName]", formObj).val();
            var conditionNames = $("input[name=conditionName]", formObj).val();
            var conditions = $("input[name=condition]", formObj).val();
            var conditionValues = $("input[name=conditionValue]", formObj).val();
            // 增加自动补全条件、人员条件、标签条件
            var autoConditions = $("input[name=autoConditions]", formObj).val();
            var psConditions = $("input[name=psConditions]", formObj).val();
            var tagConditions = $("input[name=tagConditions]", formObj).val();

            var conditionValueTexts = $("input.js_conditionValue", formObj).val();
            var resultFields = $("input[name=resultField]", formObj).val();
            var canDefResult = $("input.js_canDefResult", formObj).val();
            var canDefCondition = $("input.js_canDefCondition", formObj).val();
            // 排序信息
            var sortColumn = $("input[name=sortColumn]", formObj).val();
            var sortOrder = $("input[name=sortOrder]", formObj).val();
            // 必须包含的字段
            var forceResultField = $("input[name=forceResultField]", formObj).val();
            // 请求地址
            var url = getGlobalPathRoot() + "oframe/common/page/page-conditionTwo.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("prjCd", getPrjCd());
            ajaxPacket.data.add("entityName", entityName);
            ajaxPacket.data.add("conditionNames", conditionNames);
            ajaxPacket.data.add("conditions", conditions);
            ajaxPacket.data.add("conditionValues", conditionValues);
            // 增加自动补全条件、人员条件、标签条件
            ajaxPacket.data.add("autoConditions", autoConditions);
            ajaxPacket.data.add("psConditions", psConditions);
            ajaxPacket.data.add("tagConditions", tagConditions);

            ajaxPacket.data.add("canDefResult", canDefResult);
            ajaxPacket.data.add("canDefCondition", canDefCondition);
            ajaxPacket.data.add("resultFields", resultFields);
            ajaxPacket.data.add("conditionValueTexts", conditionValueTexts);
            // 当前的功能模块
            ajaxPacket.data.add("tabOpCode", navTab.getCurrentPanel().attr('opCode'));
            // 排序字段
            ajaxPacket.data.add("sortColumn", sortColumn ? sortColumn : "");
            ajaxPacket.data.add("sortOrder", sortOrder ? sortOrder : "");
            // 必须包含的字段列表
            ajaxPacket.data.add("forceResultField", forceResultField ? forceResultField : "");
            // 展示选择界面
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var rsp = $(response);
                if (showContainer) {
                    showContainer.html(rsp);
                    showContainer.initUI();
                } else {
                    var left = clickObj.offset().left;
                    rsp.prepend($('<div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: ' + option.left + 'px;"></div>'));
                    barDiv.after(rsp);
                    rsp.initUI();
                    if (option.height) {
                        rsp.css("height", option.height);
                    }
                    if (option.width) {
                        rsp.css("width", option.width);
                    }
                }
                // 记录标记
                Page.relObj = clickObj;
                Page.formObj = option.formObj;
                Page.changeConditions = option.changeConditions;
                if (option.callback) {
                    option.callback(showContainer);
                }
            });
        }
    },
    /**
     * 查询员工信息表单提交
     *
     * @param formId
     *            表单ID
     * @param _divId
     *            展示结果DIV展示区域
     * @param _divPId
     *            展示结果DIV展示区域
     * @param str
     *            回调执行字符串
     */
    queryPage: function (formId, _divId, successEvalJs) {
        var divObj = Page.getDivObj(_divId);
        var $pObj = divObj.parent();
        if (divObj.hasClass("js_page")) {
            $pObj = divObj;
        }
        // 提交表单
        var formObj = $("#" + formId);
        // 提交表单验证
        if (!formObj.valid()) {
            return;
        }
        // 查询按钮提交
        var defaultSortColumn = "";
        var defaultSortOrder = "";
        var resultFields = [];
        $("th", $pObj).each(
            function () {
                var $this = $(this);
                // 追加需要检索的字段
                if ($this.attr("fieldName")) {
                    resultFields.push($this.attr("fieldName"));
                }
                // 处理默认排序
                if ($this.hasClass("sortable") && defaultSortColumn == "") {
                    var sortColumn = $this.attr("fieldName");
                    defaultSortOrder = $this.attr("defaultSort") || "";
                    defaultSortColumn = $this.attr("defaultSort") ? sortColumn : "";
                    if (defaultSortColumn != "") {
                        $this.siblings("th").andSelf().removeClass("asc desc");
                        $this.addClass(defaultSortOrder);
                        return false;
                    } else {
                        return true;
                    }
                }
            });
        $pObj.find("input[name=currentPage]").val("1");
        // 转换form表单数据
        var formData = formObj.serializeArray();
        // 序列化表单数据
        var jsonData = {};
        for (var i = 0; i < formData.length; i++) {
            jsonData[formData[i].name] = formData[i].value;
        }
        //
        jsonData.resultFields = resultFields.join(",");
        jsonData.sortColumn = defaultSortColumn;
        jsonData.currentPage = "1";
        jsonData.sortOrder = defaultSortOrder;
        jsonData.prjCd = getPrjCd();
        Page.pubQuery(jsonData, _divId, successEvalJs);
    },

    /**
     * 公共查询界面
     */
    pubQuery: function (jsonData, _divId, successEvalJs) {
        var divObj = Page.getDivObj(_divId);
        var $pObj = divObj.parent();
        if (divObj.hasClass("js_page")) {
            $pObj = divObj;
        }
        var forward = jsonData.forward;
        if (forward.indexOf("forward:") == 0) {
            forward = forward.substr(8, forward.length);
        }
        var tempStr = forward.substr(1, forward.length);
        var url = getGlobalPathRoot() + "oframe/common/page/page-data.gv";
        //  自定义提交URL地址
        if (jsonData.slfDefUrl) {
            url = jsonData.slfDefUrl;
        }
        var packet = new AJAXPacket(url);
        var pageSize = $pObj.find("input[name='pageSize']").val();
        if (!pageSize && !jsonData.pageSize) {
            pageSize = 20;
        }
        jsonData.divId = _divId;
        jsonData.pageSize = pageSize;
        packet.data.data = jsonData;
        // 判断是点击查询还是翻页
        core.ajax.sendPacketHtml(packet, function (response) {
            var r = $(response);
            var toContain = $(".gridScroller tbody", divObj);
            if (toContain.length == 0) {
                toContain = divObj;
            }
            var resultObj = r.find("tbody");
            if (resultObj.length == 0) {
                resultObj = r.find("#result");
            }
            var errMsg = $("input[name=errMsg]", r).val();
            if (errMsg != '') {
                // 查询失败l
                alertMsg.error(errMsg);
            } else {
                // 执行成功
                toContain.html(resultObj.html());
                Page.refresh(divObj);
                //  绑定更多按钮
                $("button.btn-more", toContain).each(function () {
                    $(this).bind("mouseover", function () {
                        $(this).openBtnMenu();
                    });
                    $(this).mouseout(function () {
                        $.fn.deActiveBtnMenu();
                    });
                });
                // 存在分页
                if (r.find("#pagebox")) {
                    $pObj.find("div[id=pagebox]").length ?
                        $pObj.find("div[id=pagebox]").html(r.find("#pagebox").html())
                        : divObj.after(r.find("#pagebox"));
                }
                core.ajax.loadjs(response);
                //
                divObj.find(":button.checkboxCtrl, :checkbox.checkboxCtrl").each(
                    function (i) {
                        $(this).removeAttr("checked");
                    }
                );
                // 分页加载完成后触发回调函数
                if (successEvalJs && successEvalJs != "") {
                    eval(successEvalJs);
                }
            }
        }, true);
        packet = null;
    },

    /**
     * 刷新查询结果
     * @param obj
     * @returns {boolean}
     */
    refresh: function (obj) {
        var frths = $(obj).find("div.gridThead table tr:first>th");
        if (frths.length > 0) {
            var frtds = $(obj).find("div.gridTbody table tr:first>td").addClass("fRow");
            frtds.each(function (i) {
                var newW = frths.eq(i).width();
                $(this).width(newW);
            });
        }
        var $trs = $(obj).find("div.gridTbody table tbody>tr");
        $trs.hoverClass().each(function () {
            var $tr = $(this);
            var $ftds = $(">td", this);
            $tr.click(function () {
                $trs.filter(".selected").removeClass("selected");
                $tr.addClass("selected");
            });
        });
        return false;
    },

    //依据模板导出Excel文件  templatePath 模板路径
    exportExcel: function (listDiv, allFlag) {
        var divObj = Page.getDivObj(listDiv);
        var $pObj = divObj.parent();
        if (divObj.hasClass("js_page")) {
            $pObj = divObj;
        }
        // 查询条件
        var jsonData = $.parseJSON($("input[name=queryCondition]", $pObj).val());
        if (!jsonData) {
            alertMsg.info("请先执行查询");
            return;
        }
        // 获取排序
        jsonData.sortColumn = $pObj.find("input[name='sortColumn']").val();
        jsonData.sortOrder = $pObj.find("input[name='sortOrder']").val();
        // 执行查询
        var forward = jsonData.forward;
        if (forward.indexOf("forward:") == 0) {
            forward = forward.substr(8, forward.length);
        }
        var tempStr = forward.substr(1, forward.length);
        var url = getGlobalPathRoot() + "oframe/common/page/page-dataExport.gv";
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.data = jsonData;
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot()
                    + "oframe/common/file/file-down.gv?";
                downloadUrl = downloadUrl + "remoteFile="
                    + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile="
                    + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "12313");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },

    //依据模板导出Excel文件  templatePath 模板路径
    exportExcelByTemplate: function (listDiv, templatePath, resultField) {
        var divObj = Page.getDivObj(listDiv);
        var $pObj = divObj.parent();
        if (divObj.hasClass("js_page")) {
            $pObj = divObj;
        }
        // 查询条件
        var jsonData = $.parseJSON($("input[name=queryCondition]", $pObj).val());
        if (!jsonData) {
            alertMsg.info("请先执行查询");
            return;
        }
        // 获取排序
        jsonData.sortColumn = $pObj.find("input[name='sortColumn']").val();
        jsonData.sortOrder = $pObj.find("input[name='sortOrder']").val();
        // 传递列信息
        jsonData.templatePath = templatePath || "";
        if (resultField) {
            jsonData.resultField = resultField;
        }
        // 返回内容
        if (!jsonData.resultField || jsonData.resultField == "") {
            alertMsg.info("至少选择一个查询内容!");
            return;
        }
        // 调整模块信息
        var forward = jsonData.forward;
        if (forward.indexOf("forward:") == 0) {
            forward = forward.substr(8, forward.length);
        }
        var tempStr = forward.substr(1, forward.length);
        var url = getGlobalPathRoot() + "oframe/common/page/page-dataExport.gv";
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.data = jsonData;
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot()
                    + "oframe/common/file/file-down.gv?";
                downloadUrl = downloadUrl + "remoteFile="
                    + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile="
                    + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },

    /** * 翻页 ** */
    jumpPage: function (currentPage, _divId) {
        if (_divId == "") {
            return;
        }
        var divObj = Page.getDivObj(_divId);
        var $pObj = divObj.parent();
        if (divObj.hasClass("js_page")) {
            $pObj = divObj;
        }
        $pObj.find("input[name='currentPage']").val(currentPage);
        // 翻页跳转
        var jsonData = $.parseJSON($pObj.find("input[name='queryCondition']").val());
        jsonData.currentPage = $pObj.find("input[name='currentPage']").val();
        jsonData.sortColumn = $pObj.find("input[name='sortColumn']").val();
        jsonData.sortOrder = $pObj.find("input[name='sortOrder']").val();
        jsonData.pageSize = $pObj.find("input[name='pageSize']").val();
        Page.pubQuery(jsonData, _divId, null);
    },

    /** * 页面跳转 ** */
    skipTo: function (_divId) {
        var divObj = Page.getDivObj(_divId);
        var $pObj = divObj.parent();
        if (divObj.hasClass("js_page")) {
            $pObj = divObj;
        }
        var pageNoValue = $pObj.find("input[name=skipToNo]").val();
        if (!isNaN(pageNoValue)) {
            var pageNo = parseInt(pageNoValue);
            if (pageNo > 0 && pageNo <= parseInt($pObj.find("input[name='totalPage']").val())) {
                Page.jumpPage(pageNo, _divId);
            } else {
                $pObj.find("input[name='skipToNo']").attr("value", '');
            }
        } else {
            $pObj.find("input[name='skipToNo']").attr("value", '');
        }
    },

    /** * 排序 ** */
    sort: function (_divId, obj) {
        var divObj = Page.getDivObj(_divId);
        var $pObj = divObj.parent();
        if (divObj.hasClass("js_page")) {
            $pObj = divObj;
        }
        var jsonData = $.parseJSON($pObj.find("input[name=queryCondition]").val());
        var orderBy = $(obj).attr("fieldName");
        if (!jsonData) {
            alertMsg.info("请先执行查询");
        } else {
            // 清除样式
            $(obj).siblings("th").andSelf().removeClass("asc desc");
            var oldSortOrder = $pObj.find("input[name=sortOrder]").val();
            var newSortOrder = "";
            if (oldSortOrder == "" || oldSortOrder == "desc") {
                newSortOrder = "asc";
            } else {
                newSortOrder = "desc";
            }
            // 赋值新的排序及样式
            $pObj.find("input[name=sortOrder]").val(newSortOrder);
            $pObj.find("input[name=sortColumn]").val(orderBy);
            $(obj).addClass(newSortOrder);
            // 刷新界面
            Page.jumpPage(1, _divId);
        }
    },
    /*--------------- 查询条件处理----------------------*/
    /**
     * 条件关联对象
     */
    relObj: null,

    /**
     * 条件改变调用事件
     */
    changeConditions: null,

    /**
     * 表单对象
     */
    formObj: null,

    /**
     * 删除所有查询条件
     * **/
    clearAll: function (obj) {
        var container = $("td.js_condition", $.pdialog.getCurrent());
        $("label.js_keyword", container).each(function () {
            if (!$(this).hasClass("hidden")) {
                $(this).remove();
            }
        });
    },

    /**
     * 增加查询条件
     * @param clickObj
     */
    addQryField: function (clickObj) {
        var clickLabel = $(clickObj);
        var clickLi = clickLabel.closest("li");
        var newLi = clickLi.clone();
        newLi.find("select,input").val("");
        clickLi.after(newLi);
        newLi.initUI();
    },

    /**
     * 删除查询条件
     * @param clickObj
     */
    removeQryField: function (clickObj) {
        var clickLabel = $(clickObj);
        var clickLi = clickLabel.closest("li");
        if (clickLi.siblings().length == 0) {
            clickLi.find("select,input").val("");
        } else {
            clickLi.remove();
        }
    },

    /**
     * 获取下拉列表码值
     * @param obj 点击对象
     * @returns
     */
    getOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
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
            maxItemsToShow: 20,
            sortResults: null,
            filterResults: true,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var $td = source.closest("li");
                $("input[name=conditionValue]", $td).val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var $td = source.closest("li");
                var sltOption = $("select[name=conditionName]", $td).find("option:selected");
                if (sltOption.attr("frontSltRule")) {
                    $("input[name=conditionValue]", $td).val("");
                    source.val("");
                } else {
                    $("input[name=conditionValue]", $td).val(source.val());
                }
            },
            fetchData: function (obj) {
                var resultData = {};
                var $td = $(obj).closest("li");
                var sltOption = $("select[name=conditionName]", $td).find("option:selected");
                var frontSltRule = sltOption.attr("frontSltRule");
                if (frontSltRule && frontSltRule != "") {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("itemCd", frontSltRule);
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        resultData = eval("(" + response + ")");
                    }, false);
                }
                return resultData;
            }
        }
    },

    /**
     * 获取检索条件
     * @param obj
     * @returns {{}}
     */
    getCondition: function (obj) {
        var container = $(obj).closest("div.js_query_div");
        var conditionNameArr = [];
        var conditionArr = [];
        var conditionValueArr = [];
        var conditionValueTextArr = [];
        var resultField = [];

        // 排序字段
        var sortColumnArr = [];
        var sortOrderArr = [];

        // 获取所有检索条件
        $("ul.js_conditions_ul li.qry_cod", container).each(function () {
            var $this = $(this);
            var conditionName = $this.attr("conditionName");
            var condition = $this.attr("condition");
            var conditionValue = $this.attr("conditionValue");
            var conditionValueText = $("span", $this).html();
            if (!conditionName || conditionName == "") {
                return true;
            } else {
                // 返回处理结果
                conditionNameArr.push(conditionName);
                conditionArr.push(condition);
                conditionValueArr.push(conditionValue);
                conditionValueTextArr.push(conditionValueText);
            }
        });

        // 获取自动匹配检索条件
        var autoConditions = $("input[name=autoCondition]", container).val();
        // 获取人员实体检索条件
        var psConditionsJson = [];
        $("ul.js_ps_conditions_ul li.qry_ps_cod", container).each(function () {

            var $this = $(this);
            var conditionName = $this.attr("conditionName");
            var condition = $this.attr("condition");
            var conditionValue = $this.attr("conditionValue");
            if (!conditionName || conditionName == "") {
                return true;
            } else {
                var tempConNameArr = conditionName.split(",");
                var tempConArr = condition.split(",");
                var tempConValueArr = conditionValue.split(",");
                for (var i = 0; i < tempConNameArr.length; i++) {
                    var psTempJson = {};
                    var tempConName = tempConNameArr[i];
                    var tempCon = tempConArr[i];
                    var tempConValue = tempConValueArr[i];
                    // 返回处理结果
                    psTempJson.conditionName = tempConName;
                    psTempJson.condition = tempCon;
                    psTempJson.conditionValue = tempConValue;
                    psConditionsJson.push(psTempJson)
                }
            }
        });
        // 获取标签信息检索条件
        var tagConditionsJson = [];
        $("ul.js_tag_conditions_ul li.qry_tag_cod", container).each(function () {
            var tagTempJson = {};
            var $this = $(this);
            var conditionName = $this.attr("conditionName");
            var condition = $this.attr("condition");
            var conditionValue = $this.attr("conditionValue");
            if (!conditionName || conditionName == "") {
                return true;
            } else {
                // 返回处理结果
                tagTempJson.conditionName = conditionName;
                tagTempJson.condition = condition;
                tagTempJson.conditionValue = conditionValue;
            }
            tagConditionsJson.push(tagTempJson);
        });

        // 所有查询字段
        $("ul.js_show_ul li.qry_cod", container).each(function () {
            var $this = $(this);
            var resultFieldName = $this.attr("resultFieldName");
            if (resultFieldName != "") {
                resultField.push($this.attr("resultFieldName"));
                var sortLabel = $("label:eq(0)", $this);
                if (sortLabel.hasClass("asc")) {
                    sortColumnArr.push($this.attr("resultFieldName"));
                    sortOrderArr.push("asc");
                } else if (sortLabel.hasClass("desc")) {
                    sortColumnArr.push($this.attr("resultFieldName"));
                    sortOrderArr.push("desc");
                }
            }
        });
        // 返回内容
        if (resultField.length == 0) {
            alertMsg.info("至少选择一个查询内容!");
            return;
        }
        // 所有查询条件
        var conditions = {};
        conditions.conditionNames = conditionNameArr.join(",");
        conditions.conditions = conditionArr.join(",");
        conditions.conditionValues = conditionValueArr.join(",");
        conditions.conditionValueTexts = conditionValueTextArr.join(",");
        conditions.resultFields = resultField.join(",");
        // 排序字段
        conditions.sortColumn = sortColumnArr.join(",");
        conditions.sortOrder = sortOrderArr.join(",");

        conditions.autoConditions = autoConditions;
        conditions.psConditionsJson = JSON.stringify(psConditionsJson);
        conditions.tagConditionsJson = JSON.stringify(tagConditionsJson);
        // 返回查询条件
        return conditions;
    },

    /**
     * 保存检索字段
     * @param obj
     */
    saveCondition: function (obj) {
        // 容器
        var container = $(obj).closest("div.js_query_div");
        // 获取检索条件
        var conditions = Page.getCondition(obj);
        // 获取实体名称
        var entityName = $("input.js_entityName", container).val();
        // 收藏检索条件
        var url = getGlobalPathRoot() + "oframe/common/page/page-fav.gv?entityName=" + entityName;
        $.pdialog.open(url, "page-fav", "检索收藏",
            {
                mask: true, max: false,
                max: false, maxable: false, resizable: false, width: 500, height: 250

            });
    },

    /**
     * 保存收藏
     **/
    saveFav: function (entityName) {
        var $favForm = $("#favForm", $.pdialog.getCurrent());
        if ($favForm.valid()) {
            var $button = $("button.js_saveCondition", navTab.getCurrentPanel());
            var favContent = Page.getCondition($button);
            var favName = $("input[name=favName]", $.pdialog.getCurrent()).val();
            var favFlag = "0";
            if ($("input[name=favFlag]").attr("checked") == 'checked') {
                favFlag = "1";
            }
            var packet = new AJAXPacket();
            packet.data.add("favName", favName);
            packet.data.add("entityName", entityName);
            packet.data.add("favFlag", favFlag);
            packet.data.add("tabOpCode", navTab.getCurrentPanel().attr("opCode"));
            packet.data.add("favContent", JSON.stringify(favContent));
            packet.url = getGlobalPathRoot() + "oframe/common/page/page-saveFav.gv?prjCd=" + getPrjCd();
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    var container = $("div.js_query_div", navTab.getCurrentPanel());
                    var favContainer = $("#fav", container);
                    if (favFlag == "1") {
                        var spanTemplate = favContainer.find("span.my_fav:eq(0)");
                    } else {
                        var spanTemplate = favContainer.find("span.my_fav:eq(1)");
                    }
                    var newSpan = spanTemplate.clone();
                    $("label.js_fav_name", newSpan).html(favName);
                    newSpan.attr("favId", jsonData.favId);
                    newSpan.show();
                    favContainer.append(newSpan);
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.msg);
                }
            });
        }
    },

    /**
     * 删除收藏
     * @param favId
     */
    deletefav: function (obj, favId) {
        alertMsg.confirm("是否删除该收藏？", {
            okCall: function () {
                var packet = new AJAXPacket();
                var favId = $(obj).closest("span").attr("favId");
                packet.data.add("favId", favId);
                packet.data.add("entityName", "StaffFavoriteCondition");
                packet.data.add("prjCd", getPrjCd());
                packet.url = getGlobalPathRoot() + "oframe/common/page/page-deleteFav.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    $(obj).closest("span").remove();
                });
            }
        });
        stopEvent(event);
    },

    /**
     * 提交选择条件
     **/
    submitCondition: function (obj) {
        var container = $(obj).closest("div.js_query_div");
        // 获取检索条件
        var conditions = Page.getCondition(obj);
        // 应用检索条件
        Page.applyCondition(obj, conditions);
    },

    /**
     * 点击收藏自
     * @param obj
     */
    clickFav: function (obj, favId) {

        var packet = new AJAXPacket();

        var prjCd = getPrjCd();
        var favId = $(obj).attr("favId");
        packet.data.add("favId", favId);
        packet.data.add("prjCd", getPrjCd());
        packet.url = getGlobalPathRoot() + "oframe/common/page/page-queryFav.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                var infofav = jsonData.infofav;
                Page.applyCondition(obj, infofav);
            }
        });
    },

    /**
     * 关闭高级检索
     */
    closeCondition: function (obj) {
        var container = $(obj).closest("div.js_query_div");
        // 删除条件框
        container.remove();
        if (Page.relObj) {
            Page.relObj.removeClass("active");
        }
        ph005Map.closeHsConditions();
        // 清空绑定
        Page.changeConditions = null;
        Page.relObj = null;
        Page.formObj = null;
    },

    /** 标签信息
     * 关闭高级检索
     */
    closeConditionTwo: function (obj) {
        var container = $(obj).closest("div.js_query_div");
        // 删除条件框
        // container.remove();
        container.hide();
        if (Page.relObj) {
            Page.relObj.removeClass("active");
        }

        // 清空绑定
        // Page.changeConditions = null;
        // Page.relObj = null;
        // Page.formObj = null;
    },

    /**
     * 应用检索条件
     * @param obj 点击对象
     * @param conditions 检索条件
     */
    applyCondition: function (obj, conditions) {
        var container = $(obj).closest("div.js_query_div");
        // 获取检索条件
        if (!conditions) {
            conditions = Page.getCondition(obj);
        }
        // 执行检索条件
        if ($.isFunction(Page.changeConditions)) {
            Page.changeConditions(conditions);
        } else if (Page.formObj) {
            var formObj = Page.formObj;
            $("input[name=conditionName]", formObj).val(conditions.conditionNames);
            $("input[name=condition]", formObj).val(conditions.conditions);
            $("input[name=conditionValue]", formObj).val(conditions.conditionValues);
            $("input.js_conditionValue", formObj).val(conditions.conditionValueTexts);
            $("input[name=resultField]", formObj).val(conditions.resultFields);
            // 排序字段
            $("input[name=sortColumn]", formObj).val(conditions.sortColumn);
            $("input[name=sortOrder]", formObj).val(conditions.sortOrder);
            //
            $("input[name=autoConditions]", formObj).val(conditions.autoConditions);
            $("input[name=psConditions]", formObj).val(conditions.psConditionsJson);
            $("input[name=tagConditions]", formObj).val(conditions.tagConditionsJson);

            // 查询表单
            Page.query(Page.formObj, "");
        }

        // 删除条件框
        var tempClass = $(obj).attr("class");
        if (tempClass === "my_fav") {//点击收藏重新加载
            container.remove();
        } else {
            container.hide();
        }
        if (Page.relObj) {
            Page.relObj.removeClass("active");
        }
        // 清空绑定

        if (tempClass === "my_fav") {//点击收藏重新加载
            Page.changeConditions = null;
            Page.relObj = null;
            Page.formObj = null;
        }

    },

    /**
     * 公共查询界面
     */
    query: function (formObj, successEvalJs) {
        var forward = $("input[name=forward]", formObj).val();
        if (forward.indexOf("forward:") == 0) {
            forward = forward.substr(8, forward.length);
        }
        var tempStr = forward.substr(1, forward.length);
        var modelName = tempStr.substr(0, tempStr.indexOf("/"));
        var url = getGlobalPathRoot() + "oframe/common/page/page-data.gv";
        //  自定义提交URL地址
        var slfDefUrl =   $("input[name=slfDefUrl]", formObj).val();
        if (slfDefUrl && slfDefUrl != "") {
            url = slfDefUrl;
        }
        var packet = new AJAXPacket(url);
        packet.data.data = formObj.serializeArray();
        packet.data.data.push({"name": "currentPage", "value": 1});
        packet.data.data.push({"name": "pageSize", "value": 20});
        packet.data.data.push({"name": "prjCd", "value": getPrjCd()});
        // 判断是点击查询还是翻页
        core.ajax.sendPacketHtml(packet, function (response) {
            var r = $(response);
            var errMsg = $("input[name=errMsg]", r).val();
            if (errMsg != '') {
                // 查询失败l
                alertMsg.error(errMsg);
            } else {
                var resultDiv = $(formObj).next("div.js_page");
                if (resultDiv.length == 0) {
                    resultDiv = $("<div class='js_page'></div>");
                    $(formObj).after(resultDiv);
                }
                var divOver = resultDiv.css("overflow");
                resultDiv.css("overflow", "hidden");
                resultDiv.html(response);
                resultDiv.initUI();
                resultDiv.css("overflow", divOver);
                // 分页加载完成后触发回调函数
                if (successEvalJs && successEvalJs != "") {
                    eval(successEvalJs);
                }
            }
        }, true);
        packet = null;
    },

    /**
     * 带标签检索的查询
     * 新改造的公共查询界面
     */
    queryWithTag: function (formObj, successEvalJs) {
        var forward = $("input[name=forward]", formObj).val();
        if (forward.indexOf("forward:") == 0) {
            forward = forward.substr(8, forward.length);
        }
        var tempStr = forward.substr(1, forward.length);
        var modelName = tempStr.substr(0, tempStr.indexOf("/"));
        var url = getGlobalPathRoot() + "oframe/common/page/page-dataWithTag.gv";
        var packet = new AJAXPacket(url);
        packet.data.data = formObj.serializeArray();
        packet.data.data.push({"name": "currentPage", "value": 1});
        packet.data.data.push({"name": "pageSize", "value": 20});
        packet.data.data.push({"name": "prjCd", "value": getPrjCd()});
        // 判断是点击查询还是翻页
        core.ajax.sendPacketHtml(packet, function (response) {
            var r = $(response);
            var errMsg = $("input[name=errMsg]", r).val();
            if (errMsg != '') {
                // 查询失败l
                alertMsg.error(errMsg);
            } else {
                var resultDiv = $(formObj).next("div.js_page");
                if (resultDiv.length == 0) {
                    resultDiv = $("<div class='js_page'></div>");
                    $(formObj).after(resultDiv);
                }
                var divOver = resultDiv.css("overflow");
                resultDiv.css("overflow", "hidden");
                resultDiv.html(response);
                resultDiv.initUI();
                resultDiv.css("overflow", divOver);
                // 分页加载完成后触发回调函数
                if (successEvalJs && successEvalJs != "") {
                    eval(successEvalJs);
                }
            }
        }, true);
        packet = null;
    },

    /**
     * 修改排序字段
     * @param obj 点击对象
     */
    changeSort: function (obj) {
        var $this = $(obj);
        if ($this.hasClass("asc")) {
            $this.removeClass("asc").addClass("desc");
        } else if ($this.hasClass("desc")) {
            $this.removeClass("desc");
        } else {
            $this.addClass("asc");
        }
        return false;
    },
    /**
     * 选所有查询结果
     * @param obj
     */
    sltAllResult: function (obj) {
        $(obj).closest('td').find('input[type=checkbox]').attr('checked', true);
    },
    /**
     * 取消所有选择的查询结果
     * @param obj
     */
    clearAllResult: function (obj) {
        $(obj).closest('td').find('input[type=checkbox]').each(function () {
            var $obj = $(this);
            if (!$obj.attr("disabled")) {
                $obj.removeAttr('checked');
            }
        });
    },
    /**
     * 获取任务指派人
     * @param obj
     * @returns {string}
     */
    getStaffUrl: function (obj) {
        return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?subSvcName=sys002Data";
    },

    getStaffOption: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].STAFF_NAME;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.STAFF_NAME + " ( " + data.STAFF_CODE + " ) ";
            },
            mustMatch: false,
            remoteDataType: "json",
            autoFill: false,
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                $("input[type=hidden]", $(obj.source).parent()).val(obj.data.STAFF_ID);
            },
            onNoMatch: function (obj) {
                $("input[name=hidden]", $(obj.source).parent()).val("");
            }
        }
    }
};