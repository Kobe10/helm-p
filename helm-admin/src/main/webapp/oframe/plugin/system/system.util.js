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
