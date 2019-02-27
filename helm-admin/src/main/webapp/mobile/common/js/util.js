var home = 'home.html';//主页
var mine = 'mine.html';//我的

function currentUser() {
    if (isAndroid()) {
        try {
            var obj = eval('(' + Global.currentUser() + ')');
            return obj;
        } catch (error) {

        }
    } else if (isiOS()) {
        // 需要提前先调用getCurrentUser后才能有值，注意不能保证随时能取到数据，
        // 为了安全起见，建议在getCurrentUser的回调函数中再做登录处理
//		alert("aa:"+request("username"));
//		alert("bb:"+window.top.iosCurrentUser);
        if (request("username") != "") {
            var obj = {
                'username': request("username"),
                'userid': request("userid")
            };
            return obj;
        } else {
            var obj = {
                'username': window.top.iosCurrentUser
                , 'userid': window.top.iosCurrentUserId
            };
            return obj;
        }
        //iosCurrentUser =eval("{'username':'"+request("username")+"'");
//		alert(iosCurrentUser.username);
//		return iosCurrentUser;
    }
    var username = "{'username':'13716968768','userid':'1458','supplierid':'6529653305,6533148720','sign':'1111111'}";
    var username = "{'username':'18701213189','userid':'1482','supplierid':'6529643198,6523044152','sign':'1111111'}";
    var obj = eval('(' + username + ')');
    return obj;
}

var iosCurrentUser = "";	// 从IOS返回的当前用户ID
var iosCurrentUserId = "";
//从IOS取得当前用户的ID
function getCurrentUser() {
//	alert("getCurrent1:"+window.top.iosCurrentUser);
    window.top.iosCurrentUser = request("username");
    window.top.iosCurrentUserId = request("userid");
//	alert("getCurrent2:"+window.top.iosCurrentUser);
//	document.getElementsByTagName("iframe")[0].reflashUserInfo();
//	alert("getCurrent3:");
    WebViewJavascriptBridge.callHandler("getData", {key: "username"}, function (data) {
        window.top.iosCurrentUser = data.username;	// 从IOS返回的当前用户ID
        window.top.iosCurrentUserId = data.userid;	// 从IOS返回的当前用户ID
    });
}
function openUrl(url, refresh) {
    if (isAndroid()) {
        if (arguments.length == 1) {
            Global.openUrl(url, "0");
        } else {
            Global.openUrl(url, refresh);
        }
    } else if (isiOS()) {
        window.parent.WebViewJavascriptBridge.send({
            url: url,
            refresh: refresh
        });
    }
    else {
        window.location.href = url.replace("m/", "");
    }
}
function upPage(refresh) {
    if (isAndroid()) {
        if (arguments.length == 0) {
            Global.backRefresh("0");
        } else if (refresh == "0") {
            Global.backRefresh("0");
        } else {
            Global.backRefresh("1");
        }
    } else if (isiOS()) {
        WebViewJavascriptBridge.callHandler("upPage", {
            refresh: refresh
        });
    }
    else {
        window.history.go(-1);
        return false;
    }
}


//获取用户对象
function currentUserOject() {
    var user = currentUser();
    var userObject = null;
    if (user != undefined && user != null) {
        $.ajax({
                url: "HomeAction!mineInit.do",
                type: "post",
                async: false,
                data: {"userName": user.username},
                dataType: "json",
                success: function (result) {
                    if (result.id != undefined) {
                        userObject = result;
                    }
                }
            }
        );
    }
    return userObject;
}
function request(paras) {
    var url = location.href;
    var paramStr = url.substring(url.indexOf('?') + 1, url.length).split('&');
    var j;
    var paramObj = {};
    for (var i = 0; j = paramStr[i]; i++) {
        paramObj[j.substring(0, j.indexOf('=')).toLowerCase()] = j.substring(j.indexOf('=') + 1, j.length);
    }

    var returnValue = paramObj[paras.toLowerCase()];

    if (typeof (returnValue) == "undefined") {
        return "";
    } else {
        return returnValue;
    }
}


var session = {
    get: function (key) {
        if (isAndroid()) {
            return Session.getItem(key);
        } else if (isiOS()) {
            return getCookie(key);
        } else {
            return window.sessionStorage.getItem(key);
        }
    },

    set: function (key, value) {
        try {
            if (isAndroid()) {
                Session.setItem(key, value);
            } else if (isiOS()) {
                setCookie(key, value);
            } else {
                window.sessionStorage.setItem(key, value);
            }
        } catch (e) {
        }
        return this;
    },

    remove: function (key) {
        try {
            if (isAndroid()) {
                Session.removeItem(key);
            } else if (isiOS()) {
                delCookie(key);
            } else {
                window.sessionStorage.removeItem(key);
            }
        } catch (e) {
        }
        ;
        return this;
    },

    clear: function () {
        if (isAndroid()) {
            Session.clear();
        } else if (isiOS()) {
            clearCookie();
        }
        else {
            window.sessionStorage.clear();
        }
        return this;
    }
};

localData = {
    get: function (key) {
        return window.localStorage.getItem(key);
    },

    set: function (key, value) {
        try {
            window.localStorage.setItem(key, value);
        } catch (e) {
        }
        return this;
    },

    remove: function (key) {
        try {
            window.localStorage.removeItem(key);
        } catch (e) {
        }
        ;
        return this;
    },

    clear: function () {
        window.localStorage.clear();
        return this;
    }
};
function setCookie(name, value) {
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
    document.cookie = name + "=" + encodeURIComponent(value) + ";expires=" + exp.toGMTString();
}
function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) {
        return decodeURIComponent(arr[2]);
    }
    else
        return null;
}
function delCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = getCookie(name);
    if (cval != null)
        document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}
function clearCookie() {
    var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
    if (keys) {
        for (var i = keys.length; i--;)
            document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString();
    }
}
/**
 * *********  操作实例  **************
 *    var map = new HashMap();
 *    map.put("key1","Value1");
 *    map.put("key2","Value2");
 *    map.put("key3","Value3");
 *    map.put("key4","Value4");
 *    map.put("key5","Value5");
 *    alert("size："+map.size()+" key1："+map.get("key1"));
 *    map.remove("key1");
 *    map.put("key3","newValue");
 *    var values = map.values();
 *    for(var i in values){
*		document.write(i+"："+values[i]+"   ");
*	}
 *    document.write("<br>");
 *    var keySet = map.keySet();
 *    for(var i in keySet){
*		document.write(i+"："+keySet[i]+"  ");
*	}
 *    alert(map.isEmpty());
 */

function HashMap() {
    //定义长度
    var length = 0;
    //创建一个对象
    var obj = new Object();

    /**
     * 判断Map是否为空
     */
    this.isEmpty = function () {
        return length == 0;
    };

    /**
     * 判断对象中是否包含给定Key
     */
    this.containsKey = function (key) {
        return (key in obj);
    };

    /**
     * 判断对象中是否包含给定的Value
     */
    this.containsValue = function (value) {
        for (var key in obj) {
            if (obj[key] == value) {
                return true;
            }
        }
        return false;
    };

    /**
     *向map中添加数据
     */
    this.put = function (key, value) {
        if (!this.containsKey(key)) {
            length++;
        }
        obj[key] = value;
    };

    /**
     * 根据给定的Key获得Value
     */
    this.get = function (key) {
        return this.containsKey(key) ? obj[key] : null;
    };

    /**
     * 根据给定的Key删除一个值
     */
    this.remove = function (key) {
        if (this.containsKey(key) && (delete obj[key])) {
            length--;
        }
    };

    /**
     * 获得Map中的所有Value
     */
    this.values = function () {
        var _values = new Array();
        for (var key in obj) {
            _values.push(obj[key]);
        }
        return _values;
    };

    /**
     * 获得Map中的所有Key
     */
    this.keySet = function () {
        var _keys = new Array();
        for (var key in obj) {
            _keys.push(key);
        }
        return _keys;
    };

    /**
     * 获得Map的长度
     */
    this.size = function () {
        return length;
    };

    /**
     * 清空Map
     */
    this.clear = function () {
        length = 0;
        obj = new Object();
    };

    /**
     * 转换为json格式
     */
    this.toJSON = function () {
        return JSON.stringify(obj);
    };

    /**
     * 字符串转换为json
     */
    this.toMap = function (json) {
        obj = JSON.parse(json);
        for (var item in obj) {
            length++;
        }
        return this;
    };
}

function isAndroid() {
    var u = navigator.userAgent, app = navigator.appVersion;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //android终端或者uc浏览器
    return isAndroid;
}
function isiOS() {
    var u = navigator.userAgent, app = navigator.appVersion;
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    return isiOS;
}

function isWeiXin() {
    var ua = window.navigator.userAgent.toLowerCase();
    if (ua.match(/MicroMessenger/i) == 'micromessenger') {
        return true;
    } else {
        return false;
    }
}

function syxqref() {
    var param = "&param=back";
    var path = getPath();
    var type = request("type");
    var url = window.location.href;
    var r = document.referrer;
    r = decodeURIComponent(r);
    var type = request("type");
    if (r.indexOf("main") != -1) {
        //订阅消息(0)还是交易消息(1)
        var msgtype = request("msgtype");
        if (msgtype == "0") {
            window.location.href = path + "../m/dyxxlist.jsp";//临时
            return;
        }
        if (msgtype == "1") {
            window.location.href = path + "../m/jyxxlist.jsp";//临时
            return;
        }
    }
    if (r.indexOf("xjsxq") != -1 || r.indexOf("jjyqxq") != -1) {
        window.location.href = path + "../m/sy.jsp?type=" + type + param;
        return;
    }
    if (r.indexOf("zbggxqxx") != -1 || r.indexOf("ggxq") != -1) {
        window.location.href = path + "../m/find.jsp?type=" + type + param;
        return;
    }
    if (type != "") {
        if (r.indexOf("?") != -1) {
            window.location.href = r + "&type=" + type + param;
        } else {
            window.location.href = r + "?type=" + type + param;
        }
        return;
    }
    if (r.indexOf("type") != -1) {
        window.location.href = r;
        return;
    }
    if (r.indexOf("xx") != -1) {
        window.location.href = r;
        return;
    }
}
__CreateJSPath = function (js) {
    var scripts = document.getElementsByTagName("script");
    var path = "";
    for (var i = 0, l = scripts.length; i < l; i++) {
        var src = scripts[i].src;
        if (src.indexOf(js) != -1) {
            var ss = src.split(js);
            path = ss[0];
            break;
        }
    }
    var href = location.href;
    href = href.split("#")[0];
    href = href.split("?")[0];
    var ss = href.split("/");
    ss.length = ss.length - 1;
    href = ss.join("/");
    if (path.indexOf("https:") == -1 && path.indexOf("http:") == -1 && path.indexOf("file:") == -1 && path.indexOf("\/") != 0) {
        path = href + "/" + path;
    }
    return path;
}
var bootPATH = getRootPath();//__CreateJSPath("util.js");

//js获取项目根路径，如： http://localhost:8083/uimcardprj
function getRootPath() {
    //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
    var curWwwPath = window.document.location.href;
    //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://localhost:8083
    var localhostPaht = curWwwPath.substring(0, pos);
    //获取带"/"的项目名，如：/uimcardprj
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPaht + projectName);
}

function funcChina(obj) {
    if (/.*[\u4e00-\u9fa5]+.*$/.test(obj)) {
        //alert("不能含有汉字！");
        return false;
    }
    return true;
}

function loginhg(username, pass) {
    $.getJSON("http://10.246.105.130/LoginAction!loginValiteIndex.do?name=" + username + "&password=" + pass + "&jsoncallback=?", function (result) {
        alert(result);
    });
}

function backbutton() {

}
var event = {
    addEventListener: function (key, value) {
        if (isAndroid()) {
            return Event.addEventListener(key, value);
        }
    },
    removeEventListener: function (key) {
        if (isAndroid()) {
            return Event.removeEventListener(key);
        }
    },
    clearEventListener: function () {
        if (isAndroid()) {
            return Event.clearEventListener();
        }
    }
}

