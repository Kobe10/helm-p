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
};