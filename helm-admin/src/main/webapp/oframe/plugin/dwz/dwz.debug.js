/**
 * @author ZhangHuihua@msn.com
 *
 */

var DWZ = {
    // sbar: show sidebar
    keyCode: {
        ENTER: 13, ESC: 27, END: 35, HOME: 36,
        SHIFT: 16, TAB: 9,
        LEFT: 37, RIGHT: 39, UP: 38, DOWN: 40,
        DELETE: 46, BACKSPACE: 8
    },
    eventType: {
        pageClear: "pageClear",	// 用于重新ajaxLoad、关闭nabTab, 关闭dialog时，去除xheditor等需要特殊处理的资源
        resizeGrid: "resizeGrid"	// 用于窗口或dialog大小调整
    },
    isOverAxis: function (x, reference, size) {
        //Determines when x coordinate is over "b" element axis
        return (x > reference) && (x < (reference + size));
    },
    isOver: function (y, x, top, left, height, width) {
        //Determines when x, y coordinates is over "b" element
        return this.isOverAxis(y, top, height) && this.isOverAxis(x, left, width);
    },

    pageInfo: {
        pageNum: "pageNum",
        numPerPage: "numPerPage",
        orderField: "orderField",
        orderDirection: "orderDirection"
    },
    statusCode: {ok: 200, error: 300, timeout: 301},
    ui: {sbar: true},
    frag: {}, //page fragment
    _msg: {}, //alert message
    _set: {
        loginUrl: "", //session timeout
        loginTitle: "", //if loginTitle open a login dialog
        debug: false
    },
    msg: function (key, args) {
        var _format = function (str, args) {
            args = args || [];
            var result = str || "";
            for (var i = 0; i < args.length; i++) {
                result = result.replace(new RegExp("\\{" + i + "\\}", "g"), args[i]);
            }
            return result;
        };
        return _format(this._msg[key], args);
    },
    debug: function (msg) {
        if (this._set.debug) {
            if (typeof(console) != "undefined") console.log(msg);
            else alert(msg);
        }
    },
    loadLogin: function () {
        if ($.pdialog && DWZ._set.loginTitle) {
            $.pdialog.open(DWZ._set.loginUrl, "login", DWZ._set.loginTitle, {mask: true, width: 520, height: 260});
        } else {
            window.location = DWZ._set.loginUrl;
        }
    },

    /*
     * json to string
     */
    obj2str: function (o) {
        var r = [];
        if (typeof o == "string") return "\"" + o.replace(/([\'\"\\])/g, "\\$1").replace(/(\n)/g, "\\n").replace(/(\r)/g, "\\r").replace(/(\t)/g, "\\t") + "\"";
        if (typeof o == "object") {
            if (!o.sort) {
                for (var i in o)
                    r.push(i + ":" + DWZ.obj2str(o[i]));
                if (!!document.all && !/^\n?function\s*toString\(\)\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)) {
                    r.push("toString:" + o.toString.toString());
                }
                r = "{" + r.join() + "}"
            } else {
                for (var i = 0; i < o.length; i++) {
                    r.push(DWZ.obj2str(o[i]));
                }
                r = "[" + r.join() + "]"
            }
            return r;
        }
        return o.toString();
    },
    jsonEval: function (data) {
        try {
            if ($.type(data) == 'string')
                return eval('(' + data + ')');
            else return data;
        } catch (e) {
            return {};
        }
    },
    ajaxError: function (xhr, ajaxOptions, thrownError) {
        if (alertMsg) {
            if (xhr.status != 500) {
                alertMsg.error("<div>Http status: " + xhr.status + " " + xhr.statusText + "</div>"
                    + "<div>ajaxOptions: " + ajaxOptions + "</div>"
                    + "<div>thrownError: " + thrownError + "</div>"
                    + "<div>" + xhr.responseText + "</div>");
            } else {
                var errorJsonStr = xhr.getResponseHeader("Error-Json");
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
            }
        } else {
            alert("Http status: " + xhr.status + " " + xhr.statusText + "\najaxOptions: " + ajaxOptions + "\nthrownError:" + thrownError + "\n" + xhr.responseText);
        }
    },
    ajaxDone: function (json) {
        if (json.statusCode == DWZ.statusCode.error) {
            if (json.message && alertMsg) alertMsg.error(json.message);
        } else if (json.statusCode == DWZ.statusCode.timeout) {
            if (alertMsg) alertMsg.error(json.message || DWZ.msg("sessionTimout"), {okCall: DWZ.loadLogin});
            else DWZ.loadLogin();
        } else {
            if (json.message && alertMsg) alertMsg.correct(json.message);
        }
    },

    init: function (pageFrag, options) {
        var op = $.extend({
            loginUrl: "login.jsp", loginTitle: null, callback: null, debug: false,
            statusCode: {}
        }, options);
        this._set.loginUrl = op.loginUrl;
        this._set.loginTitle = op.loginTitle;
        this._set.debug = op.debug;
        $.extend(DWZ.statusCode, op.statusCode);
        $.extend(DWZ.pageInfo, op.pageInfo);

        jQuery.ajax({
            type: 'GET',
            url: pageFrag,
            dataType: 'xml',
            timeout: 50000,
            async: false,
            cache: false,
            error: function (xhr) {
                alert('Error loading XML document: ' + pageFrag + "\nHttp status: " + xhr.status + " " + xhr.statusText);
            },
            success: function (xml) {
                $(xml).find("_PAGE_").each(function () {
                    var pageId = $(this).attr("id");
                    if (pageId) DWZ.frag[pageId] = $(this).text();
                });

                $(xml).find("_MSG_").each(function () {
                    var id = $(this).attr("id");
                    if (id) DWZ._msg[id] = $(this).text();
                });

                if (jQuery.isFunction(op.callback)) op.callback();
            }
        });

        var _doc = $(document);
        if (!_doc.isBind(DWZ.eventType.pageClear)) {
            _doc.bind(DWZ.eventType.pageClear, function (event) {
                var box = event.target;
                if ($.fn.xheditor) {
                    $("textarea.editor", box).xheditor(false);
                }
            });
        }
    }
};


(function ($) {
    // DWZ set regional
    $.setRegional = function (key, value) {
        if (!$.regional) $.regional = {};
        $.regional[key] = value;
    };

    $.fn.extend({
        /**
         * @param {Object} op: {type:GET/POST, url:ajax请求地址, data:ajax请求参数列表, callback:回调函数 }
         */
        ajaxUrl: function (op) {
            var $this = $(this);

            $this.trigger(DWZ.eventType.pageClear);
            var ajaxbg = $("#background,#progressBar");
            ajaxbg.show();
            $.ajax({
                type: op.type || 'GET',
                url: op.url,
                data: op.data,
                cache: false,
                complete: function (XMLHttpRequest, textStatus) {
                    if (ajaxbg) {
                        ajaxbg.hide();
                    }
                },
                success: function (response) {
                    var json = DWZ.jsonEval(response);
                    if (json.statusCode == DWZ.statusCode.error) {
                        if (json.message) alertMsg.error(json.message);
                    } else {
                        // 处理IE9表格错位的问题
                        if (isIE()) {
                            response = response.replace(/td>\s+<td/g, 'td><td');
                        }
                        $this.html(response).initUI();
                        if ($.isFunction(op.callback)) op.callback(response);
                    }

                    if (json.statusCode == DWZ.statusCode.timeout) {
                        if ($.pdialog) $.pdialog.checkTimeout();
                        if (navTab) navTab.checkTimeout();

                        alertMsg.error(json.message || DWZ.msg("sessionTimout"), {
                            okCall: function () {
                                DWZ.loadLogin();
                            }
                        });
                    }
                },
                error: DWZ.ajaxError,
                statusCode: {
                    503: function (xhr, ajaxOptions, thrownError) {
                        alert(DWZ.msg("statusCode_503") || thrownError);
                    }
                }
            });
        },
        loadUrl: function (url, data, callback) {
            $(this).ajaxUrl({url: url, data: data, callback: callback});
        },
        initUI: function () {
            return this.each(function () {
                if ($.isFunction(initUI)) initUI(this);
            });
        },
        /**
         * adjust component inner reference box height
         * @param {Object} refBox: reference box jQuery Obj
         */
        layoutH: function ($refBox) {
            return this.each(function () {
                var $this = $(this);
                if (!$refBox) $refBox = $this.parents("div.layoutBox:first");
                var iRefH = $refBox.height();
                var iLayoutH = parseInt($this.attr("layoutH"));
                var iH = iRefH - iLayoutH > 50 ? iRefH - iLayoutH : 50;
                if ($this.closest(".page").find(">.pageHeader").length && $this.closest(".page").find(">.pageContent>.panelBar").length) {
                    iH = iH - $this.closest(".page").find(">.pageHeader").outerHeight(true);
                }
                if ($this.isTag("table")) {
                    $this.removeAttr("layoutH").wrap('<div layoutH="' + iLayoutH + '" style="overflow:auto;height:' + iH + 'px"></div>');
                } else {
                    $this.height(iH).css("overflow", "auto");
                }
            });
        },
        hoverClass: function (className, speed) {
            var _className = className || "hover";
            return this.each(function () {
                var $this = $(this), mouseOutTimer;
                $this.hover(function () {
                    if (mouseOutTimer) clearTimeout(mouseOutTimer);
                    $this.addClass(_className);
                }, function () {
                    mouseOutTimer = setTimeout(function () {
                        $this.removeClass(_className);
                    }, speed || 10);
                });
            });
        },
        focusClass: function (className) {
            var _className = className || "textInputFocus";
            return this.each(function () {
                $(this).focus(function () {
                    $(this).addClass(_className);
                }).blur(function () {
                    $(this).removeClass(_className);
                });
            });
        },
        inputAlert: function () {
            return this.each(function () {

                var $this = $(this);

                function getAltBox() {
                    return $this.parent().find("label.alt");
                }

                function altBoxCss(opacity) {
                    var position = $this.position();
                    return {
                        width: $this.width(),
                        top: position.top + 'px',
                        left: position.left + 'px',
                        opacity: opacity || 1
                    };
                }

                if (getAltBox().size() < 1) {
                    if (!$this.attr("id")) $this.attr("id", $this.attr("name") + "_" + Math.round(Math.random() * 10000));
                    var $label = $('<label class="alt" for="' + $this.attr("id") + '">' + $this.attr("alt") + '</label>').appendTo($this.parent());

                    $label.css(altBoxCss(1));
                    if ($this.val()) $label.hide();
                }

                $this.focus(function () {
                    getAltBox().css(altBoxCss(0.3));
                }).blur(function () {
                    if (!$(this).val()) getAltBox().show().css("opacity", 1);
                }).keydown(function () {
                    getAltBox().hide();
                });
            });
        },
        isTag: function (tn) {
            if (!tn) return false;
            return $(this)[0].tagName.toLowerCase() == tn ? true : false;
        },
        /**
         * 判断当前元素是否已经绑定某个事件
         * @param {Object} type
         */
        isBind: function (type) {
            var _events = $(this).data("events");
            return _events && type && _events[type];
        },
        /**
         * 输出firebug日志
         * @param {Object} msg
         */
        log: function (msg) {
            return this.each(function () {
                if (console) console.log("%s: %o", msg, this);
            });
        }
    });

    /**
     * 扩展String方法
     */
    $.extend(String.prototype, {
        isPositiveInteger: function () {
            return (new RegExp(/^[1-9]\d*$/).test(this));
        },
        isInteger: function () {
            return (new RegExp(/^\d+$/).test(this));
        },
        isNumber: function (value, element) {
            return (new RegExp(/^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/).test(this));
        },
        trim: function () {
            return this.replace(/(^\s*)|(\s*$)|\r|\n/g, "");
        },
        startsWith: function (pattern) {
            return this.indexOf(pattern) === 0;
        },
        endsWith: function (pattern) {
            var d = this.length - pattern.length;
            return d >= 0 && this.lastIndexOf(pattern) === d;
        },
        replaceSuffix: function (index) {
            return this.replace(/\[[0-9]+\]/, '[' + index + ']').replace('#index#', index);
        },
        trans: function () {
            return this.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"');
        },
        encodeTXT: function () {
            return (this).replaceAll('&', '&amp;').replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll(" ", "&nbsp;");
        },
        replaceAll: function (os, ns) {
            return this.replace(new RegExp(os, "gm"), ns);
        },
        replaceTm: function ($data) {
            if (!$data) return this;
            return this.replace(RegExp("({[A-Za-z_]+[A-Za-z0-9_]*})", "g"), function ($1) {
                return $data[$1.replace(/[{}]+/g, "")];
            });
        },
        replaceTmById: function (_box) {
            var $parent = _box || $(document);
            return this.replace(RegExp("({[A-Za-z_]+[A-Za-z0-9_]*})", "g"), function ($1) {
                var $input = $parent.find("#" + $1.replace(/[{}]+/g, ""));
                return $input.val() ? $input.val() : $1;
            });
        },
        isFinishedTm: function () {
            return !(new RegExp("{[A-Za-z_]+[A-Za-z0-9_]*}").test(this));
        },
        skipChar: function (ch) {
            if (!this || this.length === 0) {
                return '';
            }
            if (this.charAt(0) === ch) {
                return this.substring(1).skipChar(ch);
            }
            return this;
        },
        isValidPwd: function () {
            return (new RegExp(/^([_]|[a-zA-Z0-9]){6,32}$/).test(this));
        },
        isValidMail: function () {
            return (new RegExp(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/).test(this.trim()));
        },
        isSpaces: function () {
            for (var i = 0; i < this.length; i += 1) {
                var ch = this.charAt(i);
                if (ch != ' ' && ch != "\n" && ch != "\t" && ch != "\r") {
                    return false;
                }
            }
            return true;
        },
        isPhone: function () {
            return (new RegExp(/(^([0-9]{3,4}[-])?\d{3,8}(-\d{1,6})?$)|(^\([0-9]{3,4}\)\d{3,8}(\(\d{1,6}\))?$)|(^\d{3,8}$)/).test(this));
        },
        isUrl: function () {
            return (new RegExp(/^[a-zA-z]+:\/\/([a-zA-Z0-9\-\.]+)([-\w .\/?%&=:]*)$/).test(this));
        },
        isExternalUrl: function () {
            return this.isUrl() && this.indexOf("://" + document.domain) == -1;
        }
    });
})(jQuery);

/**
 * You can use this map like this:
 * var myMap = new Map();
 * myMap.put("key","value");
 * var key = myMap.get("key");
 * myMap.remove("key");
 */
function Map() {

    this.elements = [];

    this.size = function () {
        return this.elements.length;
    };

    this.isEmpty = function () {
        return (this.elements.length < 1);
    };

    this.clear = function () {
        this.elements = [];
    };

    this.put = function (_key, _value) {
        this.remove(_key);
        this.elements.push({key: _key, value: _value});
    };

    this.remove = function (_key) {
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            return false;
        }
        return false;
    };

    this.get = function (_key) {
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    return this.elements[i].value;
                }
            }
        } catch (e) {
            return null;
        }
    };

    this.element = function (_index) {
        if (_index < 0 || _index >= this.elements.length) {
            return null;
        }
        return this.elements[_index];
    };

    this.containsKey = function (_key) {
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    return true;
                }
            }
        } catch (e) {
            return false;
        }
        return false;
    };

    this.values = function () {
        var arr = [];
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].value);
        }
        return arr;
    };

    this.keys = function () {
        var arr = [];
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].key);
        }
        return arr;
    }
}
/**
 * @author ZhangHuihua@msn.com
 * ----------------------------------------------------------
 * These functions use the same 'format' strings as the 
 * java.text.SimpleDateFormat class, with minor exceptions.
 * The format string consists of the following abbreviations:
 * 
 * Field        | Full Form          | Short Form
 * -------------+--------------------+-----------------------
 * Year         | yyyy (4 digits)    | yy (2 digits), y (2 or 4 digits)
 * Month        | MMM (name or abbr.)| MM (2 digits), M (1 or 2 digits)
 *              | NNN (abbr.)        |
 * Day of Month | dd (2 digits)      | d (1 or 2 digits)
 * Day of Week  | EE (name)          | E (abbr)
 * Hour (1-12)  | hh (2 digits)      | h (1 or 2 digits)
 * Hour (0-23)  | HH (2 digits)      | H (1 or 2 digits)
 * Hour (0-11)  | KK (2 digits)      | K (1 or 2 digits)
 * Hour (1-24)  | kk (2 digits)      | k (1 or 2 digits)
 * Minute       | mm (2 digits)      | m (1 or 2 digits)
 * Second       | ss (2 digits)      | s (1 or 2 digits)
 * AM/PM        | a                  |
 *
 * NOTE THE DIFFERENCE BETWEEN MM and mm! Month=MM, not mm!
 * Examples:
 *  "MMM d, y" matches: January 01, 2000
 *                      Dec 1, 1900
 *                      Nov 20, 00
 *  "M/d/yy"   matches: 01/20/00
 *                      9/2/00
 *  "MMM dd, yyyy hh:mm:ssa" matches: "January 01, 2000 12:30:45AM"
 * ----------------------------------------------------------
 */
(function(){
var MONTH_NAMES=new Array('January','February','March','April','May','June','July','August','September','October','November','December','Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
var DAY_NAMES=new Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sun','Mon','Tue','Wed','Thu','Fri','Sat');
function LZ(x) {return(x<0||x>9?"":"0")+x}

/**
 * formatDate (date_object, format)
 * Returns a date in the output format specified.
 * The format string uses the same abbreviations as in parseDate()
 * @param {Object} date
 * @param {Object} format
 */
function formatDate(date,format) {
	format=format+"";
	var result="";
	var i_format=0;
	var c="";
	var token="";
	var y=date.getYear()+"";
	var M=date.getMonth()+1;
	var d=date.getDate();
	var E=date.getDay();
	var H=date.getHours();
	var m=date.getMinutes();
	var s=date.getSeconds();
	var yyyy,yy,MMM,MM,dd,hh,h,mm,ss,ampm,HH,H,KK,K,kk,k;
	// Convert real date parts into formatted versions
	var value={};
	if (y.length < 4) {y=""+(y-0+1900);}
	value["y"]=""+y;
	value["yyyy"]=y;
	value["yy"]=y.substring(2,4);
	value["M"]=M;
	value["MM"]=LZ(M);
	value["MMM"]=MONTH_NAMES[M-1];
	value["NNN"]=MONTH_NAMES[M+11];
	value["d"]=d;
	value["dd"]=LZ(d);
	value["E"]=DAY_NAMES[E+7];
	value["EE"]=DAY_NAMES[E];
	value["H"]=H;
	value["HH"]=LZ(H);
	if (H==0){value["h"]=12;}
	else if (H>12){value["h"]=H-12;}
	else {value["h"]=H;}
	value["hh"]=LZ(value["h"]);
	if (H>11){value["K"]=H-12;} else {value["K"]=H;}
	value["k"]=H+1;
	value["KK"]=LZ(value["K"]);
	value["kk"]=LZ(value["k"]);
	if (H > 11) { value["a"]="PM"; }
	else { value["a"]="AM"; }
	value["m"]=m;
	value["mm"]=LZ(m);
	value["s"]=s;
	value["ss"]=LZ(s);
	while (i_format < format.length) {
		c=format.charAt(i_format);
		token="";
		while ((format.charAt(i_format)==c) && (i_format < format.length)) {
			token += format.charAt(i_format++);
		}
		if (value[token] != null) { result += value[token]; }
		else { result += token; }
	}
	return result;
}

function _isInteger(val) {
	return (new RegExp(/^\d+$/).test(val));
}
function _getInt(str,i,minlength,maxlength) {
	for (var x=maxlength; x>=minlength; x--) {
		var token=str.substring(i,i+x);
		if (token.length < minlength) { return null; }
		if (_isInteger(token)) { return token; }
	}
	return null;
}

/**
 * parseDate( date_string , format_string )
 * 
 * This function takes a date string and a format string. It matches
 * If the date string matches the format string, it returns the date. 
 * If it does not match, it returns 0.
 * @param {Object} val
 * @param {Object} format
 */
function parseDate(val,format) {
	val=val+"";
	format=format+"";
	var i_val=0;
	var i_format=0;
	var c="";
	var token="";
	var token2="";
	var x,y;
	var now=new Date(1900,0,1);
	var year=now.getYear();
	var month=now.getMonth()+1;
	var date=1;
	var hh=now.getHours();
	var mm=now.getMinutes();
	var ss=now.getSeconds();
	var ampm="";
	
	while (i_format < format.length) {
		// Get next token from format string
		c=format.charAt(i_format);
		token="";
		while ((format.charAt(i_format)==c) && (i_format < format.length)) {
			token += format.charAt(i_format++);
		}
		// Extract contents of value based on format token
		if (token=="yyyy" || token=="yy" || token=="y") {
			if (token=="yyyy") { x=4;y=4; }
			if (token=="yy")   { x=2;y=2; }
			if (token=="y")    { x=2;y=4; }
			year=_getInt(val,i_val,x,y);
			if (year==null) { return 0; }
			i_val += year.length;
			if (year.length==2) {
				if (year > 70) { year=1900+(year-0); }
				else { year=2000+(year-0); }
			}
		} else if (token=="MMM"||token=="NNN"){
			month=0;
			for (var i=0; i<MONTH_NAMES.length; i++) {
				var month_name=MONTH_NAMES[i];
				if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
					if (token=="MMM"||(token=="NNN"&&i>11)) {
						month=i+1;
						if (month>12) { month -= 12; }
						i_val += month_name.length;
						break;
						}
					}
				}
			if ((month < 1)||(month>12)){return 0;}
		} else if (token=="EE"||token=="E"){
			for (var i=0; i<DAY_NAMES.length; i++) {
				var day_name=DAY_NAMES[i];
				if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
					i_val += day_name.length;
					break;
				}
			}
		} else if (token=="MM"||token=="M") {
			month=_getInt(val,i_val,token.length,2);
			if(month==null||(month<1)||(month>12)){return 0;}
			i_val+=month.length;
		} else if (token=="dd"||token=="d") {
			date=_getInt(val,i_val,token.length,2);
			if(date==null||(date<1)||(date>31)){return 0;}
			i_val+=date.length;
		} else if (token=="hh"||token=="h") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>12)){return 0;}
			i_val+=hh.length;
		} else if (token=="HH"||token=="H") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>23)){return 0;}
			i_val+=hh.length;}
		else if (token=="KK"||token=="K") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>11)){return 0;}
			i_val+=hh.length;
		} else if (token=="kk"||token=="k") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>24)){return 0;}
			i_val+=hh.length;hh--;
		} else if (token=="mm"||token=="m") {
			mm=_getInt(val,i_val,token.length,2);
			if(mm==null||(mm<0)||(mm>59)){return 0;}
			i_val+=mm.length;
		} else if (token=="ss"||token=="s") {
			ss=_getInt(val,i_val,token.length,2);
			if(ss==null||(ss<0)||(ss>59)){return 0;}
			i_val+=ss.length;
		} else if (token=="a") {
			if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
			else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
			else {return 0;}
			i_val+=2;
		} else {
			if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
			else {i_val+=token.length;}
		}
	}
	// If there are any trailing characters left in the value, it doesn't match
	if (i_val != val.length) { return 0; }
	// Is date valid for month?
	if (month==2) {
		// Check for leap year
		if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
			if (date > 29){ return 0; }
		} else { if (date > 28) { return 0; } }
	}
	if ((month==4)||(month==6)||(month==9)||(month==11)) {
		if (date > 30) { return 0; }
	}
	// Correct hours value
	if (hh<12 && ampm=="PM") { hh=hh-0+12; }
	else if (hh>11 && ampm=="AM") { hh-=12; }
	return new Date(year,month-1,date,hh,mm,ss);
}

Date.prototype.formatDate = function(dateFmt) {
	return formatDate(this, dateFmt);
};
String.prototype.parseDate = function(dateFmt) {
	if (this.length < dateFmt.length) {
		dateFmt = dateFmt.slice(0,this.length);
	}
	return parseDate(this, dateFmt);
};

/**
 * replaceTmEval("{1+2}-{2-1}")
 */
function replaceTmEval(data){
	return data.replace(RegExp("({[A-Za-z0-9_+-]*})","g"), function($1){
		return eval('(' + $1.replace(/[{}]+/g, "") + ')');
	});
}
/**
 * dateFmt:%y-%M-%d
 * %y-%M-{%d+1}
 * ex: new Date().formatDateTm('%y-%M-{%d-1}')
 * 	new Date().formatDateTm('2012-1')
 */
Date.prototype.formatDateTm = function(dateFmt) {
	var y = this.getFullYear();
	var m = this.getMonth()+1;
	var d = this.getDate();

	var sDate = dateFmt.replaceAll("%y",y).replaceAll("%M",m).replaceAll("%d",d);
	sDate = replaceTmEval(sDate);
	
	var _y=1900, _m=0, _d=1;
	var aDate = sDate.split('-');
	
	if (aDate.length > 0) _y = aDate[0];
	if (aDate.length > 1) _m = aDate[1]-1;
	if (aDate.length > 2) _d = aDate[2];
	
	return new Date(_y,_m,_d).formatDate('yyyy-MM-dd');
};

})();

/**
 * @requires jquery.validate.js
 * @author ZhangHuihua@msn.com
 */
(function ($) {
    if ($.validator) {
        $.validator.addMethod("alphanumeric", function (value, element) {
            return this.optional(element) || /^\w+$/i.test(value);
        }, "Letters, numbers or underscores only please");

        $.validator.addMethod("lettersonly", function (value, element) {
            return this.optional(element) || /^[a-z]+$/i.test(value);
        }, "Letters only please");

        $.validator.addMethod("phone", function (value, element) {
            return this.optional(element) || /^[0-9 \(\)]{7,30}$/.test(value);
        }, "Please specify a valid phone number");

        $.validator.addMethod("postcode", function (value, element) {
            return this.optional(element) || /^[0-9 A-Za-z]{5,20}$/.test(value);
        }, "Please specify a valid postcode");

        $.validator.addMethod("hssize", function (value, element) {
            return this.optional(element) || /^\d+(\.\d\d)$/g.test(value);
        }, "Please specify a valid house size");

        $.validator.addMethod("date", function (value, element) {
            value = value.replace(/\s+/g, "");
            if (String.prototype.parseDate) {
                var $input = $(element);
                var pattern = $input.attr('dateFmt') || 'yyyy-MM-dd';

                return !$input.val() || $input.val().parseDate(pattern);
            } else {
                return this.optional(element) || value.match(/^\d{4}[\/-]\d{1,2}[\/-]\d{1,2}$/);
            }
        }, "Please enter a valid date.");

        /*自定义js函数验证
         * <input type="text" name="xxx" customvalid="xxxFn(element)" title="xxx" />
         */
        $.validator.addMethod("idcard", function (value, element) {
            return this.optional(element) || CheckUtil.IdCardValidate(value);
        }, "Please enter a valid card.");

        $.validator.addClassRules({
            date: {date: true},
            alphanumeric: {alphanumeric: true},
            lettersonly: {lettersonly: true},
            phone: {phone: true},
            postcode: {postcode: true},
            idcard: {idcard: true}
        });
        $.validator.setDefaults({errorElement: "span"});
        $.validator.autoCreateRanges = true;
    }

})(jQuery);/**
 * jQuery ajax history plugins
 * @author ZhangHuihua@msn.com
 */


(function ($) {

    $.extend({

        History: {
            _hash: new Array(),
            _cont: undefined,
            _currentHash: "",
            _callback: undefined,
            init: function (cont, callback) {
                $.History._cont = cont;
                $.History._callback = callback;
                var current_hash = location.hash.replace(/\?.*$/, '');
                $.History._currentHash = current_hash;
                if ($.browser.msie) {
                    if ($.History._currentHash == '') {
                        $.History._currentHash = '#';
                    }
                    $("body").append('<iframe id="jQuery_history" style="display: none;" src="about:blank"></iframe>');
                    var ihistory = $("#jQuery_history")[0];
                    var iframe = ihistory.contentDocument || ihistory.contentWindow.document;
                    iframe.open();
                    iframe.close();
                    iframe.location.hash = current_hash;
                }
                if ($.isFunction(this._callback))
                    $.History._callback(current_hash.skipChar("#"));
                setInterval($.History._historyCheck, 100);
            },
            _historyCheck: function () {
                var current_hash = "";
                if ($.browser.msie) {
                    var ihistory = $("#jQuery_history")[0];
                    var iframe = ihistory.contentWindow;
                    current_hash = iframe.location.hash.skipChar("#").replace(/\?.*$/, '');
                } else {
                    current_hash = location.hash.skipChar('#').replace(/\?.*$/, '');
                }
//				if (!current_hash) {
//					if (current_hash != $.History._currentHash) {
//						$.History._currentHash = current_hash;
//						//TODO
//					}
//				} else {
                if (current_hash != $.History._currentHash) {
                    $.History._currentHash = current_hash;
                    $.History.loadHistory(current_hash);
                }
//				}

            },
            addHistory: function (hash, fun, args) {
                $.History._currentHash = hash;
                var history = [hash, fun, args];
                $.History._hash.push(history);
                if ($.browser.msie) {
                    var ihistory = $("#jQuery_history")[0];
                    var iframe = ihistory.contentDocument || ihistory.contentWindow.document;
                    iframe.open();
                    iframe.close();
                    iframe.location.hash = hash.replace(/\?.*$/, '');
                    location.hash = hash.replace(/\?.*$/, '');
                } else {
                    location.hash = hash.replace(/\?.*$/, '');
                }
            },
            loadHistory: function (hash) {
                if ($.browser.msie) {
                    location.hash = hash;
                }
                for (var i = 0; i < $.History._hash.length; i += 1) {
                    if ($.History._hash[i][0] == hash) {
                        $.History._hash[i][1]($.History._hash[i][2]);
                        return;
                    }
                }
            }
        }
    });
})(jQuery);
/**
 * @author Roger Wu
 */

(function($) {
var jmenus = new Map();
// If the DWZ scope is not available, add it
$.dwz = $.dwz || {};

$(window).resize(function(){
	setTimeout(function(){
		for (var i=0; i<jmenus.size();i++){
			fillSpace(jmenus.element(i).key);
		}
	}, 100);
});
$.fn.extend({
	accordion: function(options, data) {

		var args = Array.prototype.slice.call(arguments, 1);

		return this.each(function() {
			if (options.fillSpace) jmenus.put(options.fillSpace, this);
			if (typeof options == "string") {
				var accordion = $.data(this, "dwz-accordion");
				accordion[options].apply(accordion, args);
			// INIT with optional options
			} else if (!$(this).is(".dwz-accordion"))
				$.data(this, "dwz-accordion", new $.dwz.accordion(this, options));
		});
	},
	/**
	 * deprecated, use accordion("activate", index) instead
	 * @param {Object} index
	 */
	activate: function(index) {
		return this.accordion("activate", index);
	}	
});

$.dwz.accordion = function(container, options) {
	
	// setup configuration
	this.options = options = $.extend({}, $.dwz.accordion.defaults, options);
	this.element = container;
	
	$(container).addClass("dwz-accordion");
	if ( options.navigation ) {
		var current = $(container).find("a").filter(options.navigationFilter);
		if ( current.length ) {
			if ( current.filter(options.header).length ) {
				options.active = current;
			} else {
				options.active = current.parent().parent().prev();
				current.addClass("current");
			}
		}
	}
	// calculate active if not specified, using the first header
	options.headers = $(container).find(options.header);
	options.active = findActive(options.headers, options.active);

	if ( options.fillSpace ) {
		fillSpace(options.fillSpace);		
	} else if ( options.autoheight ) {
		var maxHeight = 0;
		options.headers.next().each(function() {
			maxHeight = Math.max(maxHeight, $(this).outerHeight());
		}).height(maxHeight);
	}

	options.headers
		.not(options.active || "")
		.next()
		.hide();
	options.active.find("h2").addClass(options.selectedClass);
	
	if (options.event)
		$(container).bind((options.event) + ".dwz-accordion", clickHandler);
};

$.dwz.accordion.prototype = {
	activate: function(index) {
		// call clickHandler with custom event
		clickHandler.call(this.element, {
			target: findActive( this.options.headers, index )[0]
		});
	},
	
	enable: function() {
		this.options.disabled = false;
	},
	disable: function() {
		this.options.disabled = true;
	},
	destroy: function() {
		this.options.headers.next().css("display", "");
		if ( this.options.fillSpace || this.options.autoheight ) {
			this.options.headers.next().css("height", "");
		}
		$.removeData(this.element, "dwz-accordion");
		$(this.element).removeClass("dwz-accordion").unbind(".dwz-accordion");
	}
}

function scopeCallback(callback, scope) {
	return function() {
		return callback.apply(scope, arguments);
	};
}

function completed(cancel) {
	// if removed while animated data can be empty
	if (!$.data(this, "dwz-accordion"))
		return;
	var instance = $.data(this, "dwz-accordion");
	var options = instance.options;
	options.running = cancel ? 0 : --options.running;
	if ( options.running )
		return;
	if ( options.clearStyle ) {
		options.toShow.add(options.toHide).css({
			height: "",
			overflow: ""
		});
	}
	$(this).triggerHandler("change.dwz-accordion", [options.data], options.change);
}

function fillSpace(key){
	var obj = jmenus.get(key);
	if (!obj) return;
	
	var parent = $(obj).parent();
	var height = parent.height() - (($(".accordionHeader", obj).size()) * ($(".accordionHeader:first-child", obj).outerHeight())) -2;

	var os = parent.children().not(obj);
	$.each(os, function(i){
		height -= $(os[i]).outerHeight();
	});
	$(".accordionContent",obj).height(height);
}

function toggle(toShow, toHide, data, clickedActive, down) {
	var options = $.data(this, "dwz-accordion").options;
	options.toShow = toShow;
	options.toHide = toHide;
	options.data = data;
	var complete = scopeCallback(completed, this);
	
	// count elements to animate
	options.running = toHide.size() == 0 ? toShow.size() : toHide.size();
	
	if ( options.animated ) {
		if ( !options.alwaysOpen && clickedActive ) {
			$.dwz.accordion.animations[options.animated]({
				toShow: jQuery([]),
				toHide: toHide,
				complete: complete,
				down: down,
				autoheight: options.autoheight
			});
		} else {
			$.dwz.accordion.animations[options.animated]({
				toShow: toShow,
				toHide: toHide,
				complete: complete,
				down: down,
				autoheight: options.autoheight
			});
		}
	} else {
		if ( !options.alwaysOpen && clickedActive ) {
			toShow.toggle();
		} else {
			toHide.hide();
			toShow.show();
		}
		complete(true);
	}
}

function clickHandler(event) {
	var options = $.data(this, "dwz-accordion").options;
	if (options.disabled)
		return false;
	
	// called only when using activate(false) to close all parts programmatically
	if ( !event.target && !options.alwaysOpen ) {
		options.active.find("h2").toggleClass(options.selectedClass);
		var toHide = options.active.next(),
			data = {
				instance: this,
				options: options,
				newHeader: jQuery([]),
				oldHeader: options.active,
				newContent: jQuery([]),
				oldContent: toHide
			},
			toShow = options.active = $([]);
		toggle.call(this, toShow, toHide, data );
		return false;
	}
	// get the click target
	var clicked = $(event.target);
	
	// due to the event delegation model, we have to check if one
	// of the parent elements is our actual header, and find that
	if ( clicked.parents(options.header).length )
		while ( !clicked.is(options.header) )
			clicked = clicked.parent();
	
	var clickedActive = clicked[0] == options.active[0];
	
	// if animations are still active, or the active header is the target, ignore click
	if (options.running || (options.alwaysOpen && clickedActive))
		return false;
	if (!clicked.is(options.header))
		return;

	// switch classes
	options.active.find("h2").toggleClass(options.selectedClass);
	if ( !clickedActive ) {
		clicked.find("h2").addClass(options.selectedClass);
	}

	// find elements to show and hide
	var toShow = clicked.next(),
		toHide = options.active.next(),
		//data = [clicked, options.active, toShow, toHide],
		data = {
			instance: this,
			options: options,
			newHeader: clicked,
			oldHeader: options.active,
			newContent: toShow,
			oldContent: toHide
		},
		down = options.headers.index( options.active[0] ) > options.headers.index( clicked[0] );
	
	options.active = clickedActive ? $([]) : clicked;
	toggle.call(this, toShow, toHide, data, clickedActive, down );

	return false;
};

function findActive(headers, selector) {
	return selector != undefined
		? typeof selector == "number"
			? headers.filter(":eq(" + selector + ")")
			: headers.not(headers.not(selector))
		: selector === false
			? $([])
			: headers.filter(":eq(0)");
}

$.extend($.dwz.accordion, {
	defaults: {
		selectedClass: "collapsable",
		alwaysOpen: true,
		animated: 'slide',
		event: "click",
		header: ".accordionHeader",
		autoheight: true,
		running: 0,
		navigationFilter: function() {
			return this.href.toLowerCase() == location.href.toLowerCase();
		}
	},
	animations: {
		slide: function(options, additions) {
			options = $.extend({
				easing: "swing",
				duration: 300
			}, options, additions);
			if ( !options.toHide.size() ) {
				options.toShow.animate({height: "show"}, options);
				return;
			}
			var hideHeight = options.toHide.height(),
				showHeight = options.toShow.height(),
				difference = showHeight / hideHeight;
			options.toShow.css({ height: 0}).show();
			options.toHide.filter(":hidden").each(options.complete).end().filter(":visible").animate({height:"hide"},{
				step: function(now) {
					var current = (hideHeight - now) * difference;
					if ($.browser.msie || $.browser.opera) {
						current = Math.ceil(current);
					}
					options.toShow.height( current );
				},
				duration: options.duration,
				easing: options.easing,
				complete: function() {
					if ( !options.autoheight ) {
						options.toShow.css({height:"auto"});
					}
					options.toShow.css({overflow:"auto"});
					options.complete();
				}
			});
		},
		bounceslide: function(options) {
			this.slide(options, {
				easing: options.down ? "bounceout" : "swing",
				duration: options.down ? 1000 : 200
			});
		},
		easeslide: function(options) {
			this.slide(options, {
				easing: "easeinout",
				duration: 700
			})
		}
	}
});
})(jQuery);
function initEnv() {
  $("body").append(DWZ.frag["dwzFrag"]);

  if ($.browser.msie && /6.0/.test(navigator.userAgent)) {
    try {
      document.execCommand("BackgroundImageCache", false, true);
    } catch (e) {
    }
  }
  //清理浏览器内存,只对IE起效
  if ($.browser.msie) {
    window.setInterval("CollectGarbage();", 10000);
  }

  $(window).resize(function () {
    initLayout();
    $(this).trigger(DWZ.eventType.resizeGrid);
  });

  var ajaxbg = $("#background,#progressBar");
  ajaxbg.hide();

  if ($.fn.jBar) {
    $("#leftside").jBar({minW: 150, maxW: 700});
  }

  if ($.taskBar) $.taskBar.init();
  navTab.init();
  if ($.fn.switchEnv) $("#switchEnvBox").switchEnv();

  if ($.fn.navMenu) $("#navMenu").navMenu();
  setTimeout(function () {
    initLayout();
    initUI();
    // navTab styles
    var jTabsPH = $("div.tabsPageHeader");
    jTabsPH.find(".tabsLeft").hoverClass("tabsLeftHover");
    jTabsPH.find(".tabsRight").hoverClass("tabsRightHover");
    jTabsPH.find(".tabsMore").hoverClass("tabsMoreHover");
  }, 10);
}
function initLayout() {
  var iContentW = $(window).width() - (DWZ.ui.sbar ? $("#sidebar").width() + 10 : 34) - 5;
  var iContentH = $(window).height() - $("#header").height() - $("#top").height() + 3;
  $("#container").width(iContentW);
  var tabsPageHeader = $("#container .tabsPageHeader");
  var tabsPageHeaderHeight = 40;
  if (tabsPageHeader.hasClass("hidden")) {
    tabsPageHeaderHeight = 0;
  }
  $("#container .tabsPageContent").height(iContentH - tabsPageHeaderHeight).find("[layoutH]").layoutH();
  $("#sidebar, #sidebar_s .collapse, #splitBar, #splitBarProxy").height(iContentH - 5);
  $("#taskbar").css({top: iContentH + $("#header").height() + 5, width: $(window).width()});
}

function initUI(_box) {
  var $p = $(_box || document);

  $("div.panel", $p).jPanel();

  //tables
  $("table.table", $p).jTable();

  // css tables
  $('table.list', $p).cssTable();

  //auto bind tabs
  $("div.tabs", $p).each(function () {
    var $this = $(this);
    var options = {};
    options.currentIndex = $this.attr("currentIndex") || 0;
    options.eventType = $this.attr("eventType") || "click";
    $this.tabs(options);
  });

  $p.undelegate("div.img-wrap label span", "click");
  $p.delegate("div.img-wrap label span.remove", "click", function (e) {
      var packet = new AJAXPacket();
      var docId = $(this).parent().find("input[type=hidden][name=docId]").val();
      //增加删除确认提醒
      packet.url = getGlobalPathRoot() + "oframe/common/file/file-delete.gv?docId=" + docId;
      var _this = $(this);
      alertMsg.confirm("确定删除该信息吗？", {
        okCall: function () {
          core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
              _this.parent("label").remove();
//                            console.log(_this.parent().html());
            } else {
              alertMsg.error("删除失败！");
            }
          });
        }
      });
    }
  );
  $p.undelegate("div.img-wrap img", "click");
  $p.delegate("div.img-wrap img", "click", function (e) {
    stopEvent(e);
    var _this = $(this);
    var docName = _this.parent().find("input[type=hidden][name=docName]").val();
    if (docName && !docName.isImg()) {
      $(this).parent().find("a span").click();
      return;
    }
    // 获取可以展示的图片地址列表和当前的展示位置
    // 实际图片地址
    var currentSrc = _this.attr("realSrc");
    // 所有图片地址
    var imgSrcArr = [];
    var currentIdx = -1;
    var totalCount = 0;
    _this.parents("div.img-wrap").find("img").each(function (idx, obj) {
      var realSrc = $(obj).attr("realSrc");
      if (realSrc && realSrc != '') {
        totalCount++;
        imgSrcArr.push(realSrc);
        if (realSrc == currentSrc) {
          currentIdx = totalCount - 1;
        }
      }
    });
    var filter = _this.css("filter");
    var sInner = '<div class="imgShadow"></div><div class="imgDialog" id="imgDialog" allRealSrc="' + imgSrcArr.join(";") + '" currentIdx="' + currentIdx + '">'
      + '<a href="javascript:void(0)" class="close"></a>'
      + '<div class="btn btnLeft hidden" style="visibility: visible;"><a href="javascript:;" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a></div>'
      + '<img style="filter:' + filter + '" src="' + currentSrc + '">'
      + '<div class="btn btnRight hidden" style="visibility: visible;"><a href="javascript:;" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a></div>'
      + '<iframe transparent="transparent" style="position:absolute;top:0;left:0;width: 100%;height: 100%; z-index: 0;"></iframe></div>';
    $p.append(sInner);

    // 初始化显示内容
    $("#imgDialog div.btnLeft", $p).show();
    $("#imgDialog div.btnRight", $p).show();
    // 第一张
    if (currentIdx == 0 || currentIdx == -1) {
      $("#imgDialog div.btnLeft", $p).hide();
    }
    // 最后一张
    if (currentIdx == totalCount - 1 || currentIdx == -1) {
      $("#imgDialog div.btnRight", $p).hide();
    }

    $("#imgDialog div.btnRight", $p).click(function (event) {
      var currentIdx = parseInt($("#imgDialog").attr("currentIdx"));
      var imgSrcArr = $("#imgDialog").attr("allRealSrc").split(";");
      var totalCount = imgSrcArr.length;
      if (currentIdx == totalCount - 1) {
        currentIdx = currentIdx;
      } else {
        currentIdx++;
      }
      $("#imgDialog").attr("currentIdx", currentIdx);

      // 初始化显示内容
      $("#imgDialog div.btnLeft").show();
      $("#imgDialog div.btnRight").show();
      // 第一张
      if (currentIdx == 0) {
        $("#imgDialog div.btnLeft").hide();
      }
      // 最后一张
      if (currentIdx == totalCount - 1) {
        $("#imgDialog div.btnRight").hide();
      }
      //
      $("#imgDialog img").attr("src", imgSrcArr[currentIdx]);
    });

    $("#imgDialog div.btnLeft").click(function (event) {
      var currentIdx = parseInt($("#imgDialog").attr("currentIdx"));
      var imgSrcArr = $("#imgDialog").attr("allRealSrc").split(";");
      var totalCount = imgSrcArr.length;
      if (currentIdx <= 0) {
        currentIdx = 0;
      } else {
        currentIdx--;
      }
      $("#imgDialog").attr("currentIdx", currentIdx);
      // 初始化显示内容
      $("#imgDialog div.btnLeft").show();
      $("#imgDialog div.btnRight").show();
      // 第一张
      if (currentIdx == 0) {
        $("#imgDialog div.btnLeft").hide();
      }
      // 最后一张
      if (currentIdx == totalCount - 1) {
        $("#imgDialog div.btnRight").hide();
      }
      //
      $("#imgDialog img").attr("src", imgSrcArr[currentIdx]);
    });
    $("#imgDialog img", $p).one('load', function () {
      $("#imgDialog").css({width: $(this).width(), marginLeft: -($(this).width() / 2) + 'px'});
      if ($(this).height() >= $(window).height()) {
        $(this).height($(window).height() - 100).css("marginTop", '50px');
      }
    }).each(function () {
      if (this.complete) $(this).load();
    });

    // 绑定图片翻页事件
    $(document).bind("keydown", ImgPage.keyJump);
    $("#imgDialog .close", $p).click(function (e) {
      $(".imgShadow").remove();
      $("#imgDialog").remove();
      // 取消绑定图片翻页事件
      $(document).unbind("keydown", ImgPage.keyJump);
    });
  });

  $(".btn-more", $p).each(function () {
    $(this).bind("mouseover", function () {
      $(this).openBtnMenu({
        "offsetX": $(this).attr("offsetX"),
        "offsetY": $(this).attr("offsetY"),
        "btnContainer": $(this).attr("btnContainer")
      });
    });
    $(this).mouseout(function () {
      $.fn.deActiveBtnMenu();
    });
  });

  $(".js_more", $p).each(function () {
    $(this).bind("mouseover", function () {
      $(this).openBtnMenu({
        "offsetX": $(this).attr("offsetX"),
        "offsetY": $(this).attr("offsetY"),
        "openDirection": $(this).attr("openDirection")
      });
    });
    $(this).mouseout(function () {
      $.fn.deActiveBtnMenu();
    });
  });

// auto bind auto select
  $("input.autocomplete", $p).each(function () {
      var $this = $(this);
      var atArray = $(this).attr("atArray");
      var atOption = $(this).attr("atOption");
      var atUrl = $(this).attr("atUrl");
      if (atOption && !atOption == "") {
        atOption = eval(atOption)($this);
      } else {
        atOption = {};
      }
      if (atArray && !atArray == "") {
        atArray = eval(atArray)($this);
        $(this).autocomplete(atArray, atOption);
      } else if (atUrl && !atUrl == "") {
        atUrl = eval(atUrl)($this);
        $(this).autocomplete(atUrl, atOption);
      } else {
        $(this).autocomplete(atOption);
      }
    }
  );

// 初始化颜色选择器
  if ($.fn.colorPicker) {
    $(".color-pick", $p).colorPicker();
  }

//    $("ul.tree", $p).jTree();
  $('div.accordion', $p).each(function () {
    var $this = $(this);
    var extendAll = $this.attr("extendAll");
    if (extendAll) {
      //Get the width and height of each div
      $this.find(".accordionContent").each(function () {
        var _contentDiv = $(this);
        //Assignment, ease for use
        _contentDiv.attr("height", _contentDiv.height());
        _contentDiv.attr("width", _contentDiv.width());
      });
    } else {
      $this.accordion({fillSpace: $this.attr("fillSpace"), alwaysOpen: true, active: 0});
    }
  });

//    $(":button.checkboxCtrl, :checkbox.checkboxCtrl", $p).checkboxCtrl($p);
  $(":button.checkboxCtrl, :checkbox.checkboxCtrl", $p).click(function (event) {
    $this = $(this);
    var groups = $this.attr("group");
    var checked = $this.is(":checked") ? "all" : "none";
    $(this).closest("div.grid").find(":checkbox[group=" + groups + "]").each(
      function (i) {
        if (!$(this).attr("disabled")) {
          if ($(this).hasClass("checkboxCtrl")) {
            return true;
          } else if ("all" == checked) {
            $(this).attr('checked', "true");
          } else {
            $(this).removeAttr('checked');
          }
        }
      }
    );
  });

// init styles
  $("input[type=text], input[type=password], textarea", $p).addClass("textInput").focusClass("focus");
  $("input[readonly], textarea[readonly]", $p).addClass("readonly");
  $("input[disabled=true], textarea[disabled=true]", $p).addClass("disabled");
  $("input[type=text]", $p).not("div.tabs input[type=text]", $p).filter("[alt]").inputAlert();
//Grid ToolBar
  $("div.panelBar li, div.panelBar", $p).hoverClass("hover");

//Button
  $("div.button", $p).hoverClass("buttonHover");
  $("div.buttonActive", $p).hoverClass("buttonActiveHover");
//tabsPageHeader
  $("div.tabsHeader li, div.tabsPageHeader li, div.accordionHeader, div.accordion", $p).hoverClass("hover");
//validate form
  $("form.required-validate", $p).each(function () {
    var $form = $(this);
    $form.validate({
      onsubmit: false,
      focusInvalid: false,
      focusCleanup: true,
      errorElement: "span",
      ignore: ".ignore",
      invalidHandler: function (form, validator) {
        var errors = validator.numberOfInvalids();
        if (errors) {
          var message = DWZ.msg("validateFormError", [errors]);
          alertMsg.error(message);
        }
      }
    });
    $form.find('input[customvalid]').each(function () {
      var $input = $(this);
      $input.rules("add", {
        customvalid: $input.attr("customvalid")
      })
    });
  });
  if ($.fn.datepicker) {
    $('input.date', $p).each(function () {
      var $this = $(this);
      var opts = {};
      if ($this.attr("dateFmt")) opts.pattern = $this.attr("dateFmt");
      if ($this.attr("minDate")) opts.minDate = $this.attr("minDate");
      if ($this.attr("maxDate")) opts.maxDate = $this.attr("maxDate");
      if ($this.attr("mmStep")) opts.mmStep = $this.attr("mmStep");
      if ($this.attr("ssStep")) opts.ssStep = $this.attr("ssStep");
      $this.datepicker(opts);
    });
  }
  // link
  $("div.withTitle:not(.noClose)>h1", $p).each(function () {
    var _this = $(this);
    _this.unbind("click");
    _this.bind("click", function () {
      var clickObj = $(this);
      if (clickObj.hasClass("close")) {
        clickObj.nextAll().show();
        clickObj.removeClass("close");
        clickObj.addClass("open");
      } else {
        clickObj.nextAll().hide();
        clickObj.removeClass("open");
        clickObj.addClass("close");
      }
    });
    // 初始化隐藏
    if (_this.hasClass("close")) {
      _this.nextAll().hide();
    }
  });
  // link
  $("div.readonly", $p).each(function () {
    var _this = $(this);
    $("input:not(.write)", _this).addClass("readonly").attr("readonly", "readonly");
    $("input[type=checkbox]:not(.write)", _this).addClass("readonly").attr("disabled", "disabled");
    $("select:not(.write)", _this).addClass("readonly").attr("disabled", "disabled");
    $("textarea:not(.write)", _this).addClass("readonly").attr("readonly", "readonly");
  });
  // readonly样式问题
  $("input[readOnly]", $p).keydown(function (event) {
    if (event.target) {
      if ($(event.target).hasClass("readonly")) {
        stopEvent(event);
      }
    }
  });
  // navTab
  $("a[target=navTab]", $p).each(function () {
    $(this).click(function (event) {
      var $this = $(this);
      var title = $this.attr("title") || $this.text();
      var tabid = $this.attr("rel") || "_blank";
      var fresh = eval($this.attr("fresh") || "true");
      var external = eval($this.attr("external") || "false");
      var url = unescape($this.attr("href")).replaceTmById($(event.target).parents(".unitBox:first"));
      DWZ.debug(url);
      if (!url.isFinishedTm()) {
        alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
        return false;
      }
      navTab.openTab(tabid, url, {title: title, fresh: fresh, external: external});

      event.preventDefault();
    });
  });

  //dialogs
  $("a[target=dialog]", $p).each(function () {
    $(this).click(function (event) {
      var $this = $(this);
      var title = $this.attr("title") || $this.text();
      var rel = $this.attr("rel") || "_blank";
      var options = {};
      var w = $this.attr("width");
      var h = $this.attr("height");
      if (w) options.width = w;
      if (h) options.height = h;
      options.max = eval($this.attr("max") || "false");
      options.mask = eval($this.attr("mask") || "false");
      options.maxable = eval($this.attr("maxable") || "true");
      options.minable = eval($this.attr("minable") || "true");
      options.fresh = eval($this.attr("fresh") || "true");
      options.resizable = eval($this.attr("resizable") || "true");
      options.drawable = eval($this.attr("drawable") || "true");
      options.close = eval($this.attr("close") || "");
      options.param = $this.attr("param") || "";

      var url = unescape($this.attr("href")).replaceTmById($(event.target).parents(".unitBox:first"));
      DWZ.debug(url);
      if (!url.isFinishedTm()) {
        alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
        return false;
      }
      $.pdialog.open(url, rel, title, options);

      return false;
    });
  });
  $("a[target=ajax]", $p).each(function () {
    $(this).click(function (event) {
      var $this = $(this);
      var rel = $this.attr("rel");
      if (rel) {
        var $rel = $("#" + rel);
        $rel.loadUrl($this.attr("href"), {}, function () {
          $rel.find("[layoutH]").layoutH();
        });
      }

      event.preventDefault();
    });
  });

  if ($.fn.sortDrag) $("div.sortDrag", $p).sortDrag();

// dwz.ajax.js
  if ($.fn.ajaxTodo) $("a[target=ajaxTodo]", $p).ajaxTodo();
  if ($.fn.dwzExport) $("a[target=dwzExport]", $p).dwzExport();

  if ($.fn.lookup) $("a[lookupGroup]", $p).lookup();
  if ($.fn.multLookup) $("[multLookup]:button", $p).multLookup();
  if ($.fn.suggest) $("input[suggestFields]", $p).suggest();
  if ($.fn.itemDetail) $("table.itemDetail", $p).itemDetail();
  if ($.fn.selectedTodo) $("a[target=selectedTodo]", $p).selectedTodo();
  if ($.fn.pagerForm) $("form[rel=pagerForm]", $p).pagerForm({parentBox: $p});

  // 这里放其他第三方jQuery插件...
  $p.find("[layoutH]").layoutH();

  if (window.UE && UE.ui) {
    $("textarea.editor", $p).each(function () {
      var $this = $(this);
      var edtOpt = $this.attr("edtOpt");
      var simpleEditor = $this.hasClass("simpleEditor");
      var option = {};
      if (edtOpt) {
        option = eval('(' + edtOpt + ')');
      }
      if (simpleEditor) {
        var simpleOption = {
          minFrameHeight: 300,
          toolbars: [
            ['source', 'undo', 'redo', 'bold', 'italic', 'underline', '|',
              'customstyle', 'paragraph', 'fontfamily', 'fontsize']
          ]
        };
        $.extend(option, simpleOption);
      }
      var editor = new UE.ui.Editor(option);
      editor.render(this);
    });
  }

  // 回车进入下一个元素
  $("form.entermode", $p).find("input,select").bind("keydown", function () {
    var e = event || window.event;
    if (e.keyCode == 13 && e.srcElement.type != 'button'
      && e.srcElement.type != 'submit' && e.srcElement.type != 'reset'
      && e.srcElement.type != 'hidden' && e.srcElement.type != 'textarea'
      && e.srcElement.type != 'radio' && e.srcElement.type != 'file'
      && e.srcElement.type != '') {
      e.keyCode = 9;
    }
  });

  // 分类表头,点击折叠内容，再次点击展开。
  $(".accordionHeader", $p).bind("click", function () {
    var $this = $(this);
    var accordionContent = $this.next(".accordionContent");
    var h2Obj = $this.find(">h2");
    if (h2Obj.hasClass("collapsable")) {
      accordionContent.animate({
        height: '0px'
      });
      h2Obj.removeClass("collapsable");
    } else {
      h2Obj.addClass("collapsable");
      var oldHeight = accordionContent.attr("height");
      var oldWidth = accordionContent.attr("width");
      accordionContent.animate({
        width: oldWidth,
        height: oldHeight
      });
      accordionContent.find(">ul").width(oldWidth);
    }
  });

  $(":input.titleFormat", $p).each(function () {
    $(this).bind("mouseover", function () {
      TitleFormat.titleMouseOver(this, event);
    });
    $(this).mouseout(function () {
      TitleFormat.titleMouseOut(this);
    });
  });

  // input中radio和checkbox的处理
  $("label.for-label", $p).bind("click", function (event) {
    var targetType = $(event.target).attr("type");
    if (!event.forClick && targetType != "checkbox" && targetType != "radio") {
      var newEvent = jQuery.Event("click");
      newEvent.forClick = true;
      $("input[type=radio]", $(this)).trigger(newEvent);
      $("input[type=checkbox]", $(this)).trigger(newEvent);
    }
  });
}


/**
 * Theme Plugins
 * @author ZhangHuihua@msn.com
 */
(function($){
	$.fn.extend({
		theme: function(options){
			var op = $.extend({themeBase:"themes"}, options);
			var _themeHref = op.themeBase + "/#theme#/style.css";
			return this.each(function(){
				var jThemeLi = $(this).find(">li[theme]");
				var setTheme = function(themeName){
					$("head").find("link[href$='style.css']").attr("href", _themeHref.replace("#theme#", themeName));
					jThemeLi.find(">div").removeClass("selected");
					jThemeLi.filter("[theme="+themeName+"]").find(">div").addClass("selected");
					
					if ($.isFunction($.cookie)) $.cookie("dwz_theme", themeName);
				}
				
				jThemeLi.each(function(index){
					var $this = $(this);
					var themeName = $this.attr("theme");
					$this.addClass(themeName).click(function(){
						setTheme(themeName);
					});
				});
					
				if ($.isFunction($.cookie)){
					var themeName = $.cookie("dwz_theme");
					if (themeName) {
						setTheme(themeName);
					}
				}
				
			});
		}
	});
})(jQuery);
/**
 * @author zhanghuihua@msn.com
 */
(function($){
	$.fn.navMenu = function(){
		return this.each(function(){
			var $box = $(this);
			$box.find("li>a").click(function(){
				var $a = $(this);
				$.post($a.attr("href"), {}, function(html){
					$("#sidebar").find(".accordion").remove().end().append(html).initUI();
					$box.find("li").removeClass("selected");
					$a.parent().addClass("selected");
					navTab.closeAllTab();
				});
				return false;
			});
		});
	}
	
	$.fn.switchEnv = function(){
		var op = {cities$:">ul>li", boxTitle$:">a>span"};
		return this.each(function(){
			var $this = $(this);
			$this.click(function(){
				if ($this.hasClass("selected")){
					_hide($this);
				} else {
					_show($this);
				}
				return false;
			});
			
			$this.find(op.cities$).click(function(){
				var $li = $(this);

				$.post($li.find(">a").attr("href"), {}, function(html){
					_hide($this);
					$this.find(op.boxTitle$).html($li.find(">a").html());
					navTab.closeAllTab();
					$("#sidebar").find(".accordion").remove().end().append(html).initUI();
				});
				return false;
			});
		});
	}
	
	function _show($box){
		$box.addClass("selected");
		$(document).bind("click",{box:$box}, _handler);
	}
	function _hide($box){
		$box.removeClass("selected");
		$(document).unbind("click", _handler);
	}
	
	function _handler(event){
		_hide(event.data.box);
	}
})(jQuery);


/**
 * @author ZhangHuihua@msn.com
 */
$.setRegional("alertMsg", {
    title: {error: "Error", info: "Information", warn: "Warning", correct: "Successful", confirm: "Confirmation"},
    butMsg: {ok: "OK", yes: "Yes", no: "No", cancel: "Cancel"}
});
var alertMsg = {
    _boxId: "#alertMsgBox",
    _bgId: "#alertBackground",
    _closeTimer: null,

    _types: {error: "error", info: "info", warn: "warn", correct: "correct", confirm: "confirm"},

    _getTitle: function (key) {
        return $.regional.alertMsg.title[key];
    },

    _keydownOk: function (event) {
        if (event.keyCode == DWZ.keyCode.ENTER) event.data.target.trigger("click");
        return false;
    },
    _keydownEsc: function (event) {
        if (event.keyCode == DWZ.keyCode.ESC) event.data.target.trigger("click");
    },
    /**
     *
     * @param {Object} type
     * @param {Object} msg
     * @param {Object} buttons [button1, button2]
     */
    _open: function (type, msg, buttons) {
        $(this._boxId).remove();
        var butsHtml = "";
        if (buttons) {
            for (var i = 0; i < buttons.length; i++) {
                var sRel = buttons[i].call ? "callback" : "";
                butsHtml += DWZ.frag["alertButFrag"].replace("#butMsg#", buttons[i].name).replace("#callback#", sRel);
            }
        }
        var boxHtml = DWZ.frag["alertBoxFrag"].replace("#type#", type).replace("#title#", this._getTitle(type)).replace("#message#", msg).replace("#butFragment#", butsHtml);
        $(boxHtml).appendTo("body").css("top", "14px");

        if (this._closeTimer) {
            clearTimeout(this._closeTimer);
            this._closeTimer = null;
        }
        if (this._types.info == type || this._types.correct == type) {
            this._closeTimer = setTimeout(function () {
                alertMsg.close()
            }, 1000);
        } else {
            $(this._bgId).show();
        }

        var jButs = $(this._boxId).find("a.button");
        var jCallButs = jButs.filter("[rel=callback]");
        var jDoc = $(document);

        for (var i = 0; i < buttons.length; i++) {
            if (buttons[i].call) jCallButs.eq(i).click(buttons[i].call);
            if (buttons[i].keyCode == DWZ.keyCode.ENTER) {
                jDoc.bind("keydown", {target: jButs.eq(i)}, this._keydownOk);
            }
            if (buttons[i].keyCode == DWZ.keyCode.ESC) {
                jDoc.bind("keydown", {target: jButs.eq(i)}, this._keydownEsc);
            }
        }
    },
    close: function () {
        $(document).unbind("keydown", this._keydownOk).unbind("keydown", this._keydownEsc);
        $(this._boxId).animate({top: -$(this._boxId).height()}, 500, function () {
            $(this).remove();
        });
        $(this._bgId).hide();
    },
    error: function (msg, options) {
        this._alert(this._types.error, msg, options);
    },
    info: function (msg, options) {
        this._alert(this._types.info, msg, options);
    },
    warn: function (msg, options) {
        this._alert(this._types.warn, msg, options);
    },
    correct: function (msg, options) {
        this._alert(this._types.correct, msg, options);
    },
    _alert: function (type, msg, options) {
        // 用户未登陆或登录已经失效弹出登录框
        if ("用户未登录或登录失效!" == msg) {
            var staffCode = $("#layout").find("input.js_main_login_staff").val();
            var url = getGlobalPathRoot() + "oframe/sysmg/sys001/login-initD.gv?staffCode=" + staffCode;
            $.pdialog.open(url, "sys001d", "超时登录",
                {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 320});
            return;
        }
        var op = {okName: $.regional.alertMsg.butMsg.ok, okCall: null};
        $.extend(op, options);
        var buttons = [
            {name: op.okName, call: op.okCall, keyCode: DWZ.keyCode.ENTER}
        ];
        this._open(type, msg, buttons);
    },
    /**
     *
     * @param {Object} msg
     * @param {Object} options {okName, okCal, cancelName, cancelCall}
     */
    confirm: function (msg, options) {
        var op = {okName: $.regional.alertMsg.butMsg.ok, okCall: null, cancelName: $.regional.alertMsg.butMsg.cancel, cancelCall: null};
        $.extend(op, options);
        var buttons = [
            {name: op.okName, call: op.okCall, keyCode: DWZ.keyCode.ENTER},
            {name: op.cancelName, call: op.cancelCall, keyCode: DWZ.keyCode.ESC}
        ];
        this._open(this._types.confirm, msg, buttons);
    }
};

/**
 * @author zhanghuihua@msn.com
 */

(function($){
	var menu, shadow, hash;
	$.fn.extend({
		contextMenu: function(id, options){
			var op = $.extend({
				    shadow : true,
				    bindings:{},
					ctrSub:null
				}, options
			);
			
			if (!menu) {
				menu = $('<div id="contextmenu"></div>').appendTo('body').hide();
			}
			if (!shadow) {
				shadow = $('<div id="contextmenuShadow"></div>').appendTo('body').hide();
			}
			
			hash = hash || [];
			hash.push({
				id : id,
				shadow: op.shadow,
				bindings: op.bindings || {},
				ctrSub: op.ctrSub
			});
			
			var index = hash.length - 1;
			$(this).bind('contextmenu', function(e) {
				display(index, this, e, op);
				return false;
			});
			return this;
		}
	});
	
	function display(index, trigger, e, options) {
		var cur = hash[index];

		var content = $(DWZ.frag[cur.id]);
		content.find('li').hoverClass();
	
		// Send the content to the menu
		menu.html(content);
	
		$.each(cur.bindings, function(id, func) {
			$("[rel='"+id+"']", menu).bind('click', function(e) {
				hide();
				func($(trigger), $("#"+cur.id));
			});
		});
		
		var posX = e.pageX;
		var posY = e.pageY;
		if ($(window).width() < posX + menu.width()) posX -= menu.width();
		if ($(window).height() < posY + menu.height()) posY -= menu.height();

		menu.css({'left':posX,'top':posY}).show();
		if (cur.shadow) shadow.css({width:menu.width(),height:menu.height(),left:posX+3,top:posY+3}).show();
		$(document).one('click', hide);
		
		if ($.isFunction(cur.ctrSub)) {cur.ctrSub($(trigger), $("#"+cur.id));}
	}
	
	function hide() {
		menu.hide();
		shadow.hide();
	}
})(jQuery);

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
};/**
 * @author ZhangHuihua@msn.com
 * 
 */
(function($){
	$.fn.extend({

		/**
		 * options: reverse[true, false], eventType[click, hover], currentIndex[default index 0]
		 * 			stTab[tabs selector], stTabPanel[tab panel selector]
		 * 			ajaxClass[ajax load], closeClass[close tab]
		 */ 
		tabs: function (options){
			var op = $.extend({reverse:false, eventType:"click", currentIndex:0, stTabHeader:"> .tabsHeader", stTab:">.tabsHeaderContent>ul", stTabPanel:"> .tabsContent", ajaxClass:"j-ajax", closeClass:"close", prevClass:"tabsLeft", nextClass:"tabsRight"}, options);
			
			return this.each(function(){
				initTab($(this));
			});
			
			function initTab(jT){
				var jSelector = jT.add($("> *", jT));
				var jTabHeader = $(op.stTabHeader, jSelector);
				var jTabs = $(op.stTab + " li", jTabHeader);
				var jGroups = $(op.stTabPanel + " > *", jSelector);

				jTabs.unbind().find("a").unbind();
				jTabHeader.find("."+op.prevClass).unbind();
				jTabHeader.find("."+op.nextClass).unbind();
				
				jTabs.each(function(iTabIndex){
					if (op.currentIndex == iTabIndex) $(this).addClass("selected");
					else $(this).removeClass("selected");
					
					if (op.eventType == "hover") $(this).hover(function(event){switchTab(jT, iTabIndex)});
					else $(this).click(function(event){switchTab(jT, iTabIndex)});

					$("a", this).each(function(){
						if ($(this).hasClass(op.ajaxClass)) {
							$(this).click(function(event){
								var jGroup = jGroups.eq(iTabIndex);
								if (this.href && !jGroup.attr("loaded")) jGroup.loadUrl(this.href,{},function(){
									jGroup.find("[layoutH]").layoutH();
									jGroup.attr("loaded",true);
								});
								event.preventDefault();
							});
							
						} else if ($(this).hasClass(op.closeClass)) {
							$(this).click(function(event){
								jTabs.eq(iTabIndex).remove();
								jGroups.eq(iTabIndex).remove();
								if (iTabIndex == op.currentIndex) {
									op.currentIndex = (iTabIndex+1 < jTabs.size()) ? iTabIndex : iTabIndex - 1;
								} else if (iTabIndex < op.currentIndex){
									op.currentIndex = iTabIndex;
								}
								initTab(jT);
								return false;
							});
						}
					});
				});

				switchTab(jT, op.currentIndex);
			}
			
			function switchTab(jT, iTabIndex){
				var jSelector = jT.add($("> *", jT));
				var jTabHeader = $(op.stTabHeader, jSelector);
				var jTabs = $(op.stTab + " li", jTabHeader);
				var jGroups = $(op.stTabPanel + " > *", jSelector);
				
				var jTab = jTabs.eq(iTabIndex);
				var jGroup = jGroups.eq(iTabIndex);
				if (op.reverse && (jTab.hasClass("selected") )) {
					jTabs.removeClass("selected");
					jGroups.hide();
				} else {
					op.currentIndex = iTabIndex;
					jTabs.removeClass("selected");
					jTab.addClass("selected");
					
					jGroups.hide().eq(op.currentIndex).show();
				}
				
				if (!jGroup.attr("inited")){
					jGroup.attr("inited", 1000).find("input[type=text]").filter("[alt]").inputAlert();
				}
			}
			
		}
	});
})(jQuery);/**
 * @author Roger Wu
 * @version 1.0
 */
(function($){
 	$.fn.extend({jresize:function(options) {
        if (typeof options == 'string') {
                if (options == 'destroy') 
					return this.each(function() {
							var dialog = this;		
							$("div[class^='resizable']",dialog).each(function() {
								$(this).hide();
							});
	                });
        }
		return this.each(function(){
			var dialog = $(this);			
			var resizable = $(".resizable");
			$("div[class^='resizable']",dialog).each(function() {
				var bar = this;
				$(bar).mousedown(function(event) {
					$.pdialog.switchDialog(dialog);
					$.resizeTool.start(resizable, dialog, event, $(bar).attr("tar"));
					return false;
				}).show();
			});
		});
	}});
	$.resizeTool = {
		start:function(resizable, dialog, e, target) {
			$.pdialog.initResize(resizable, dialog, target);
			$.data(resizable[0], 'layer-drag', {
				options: $.extend($.pdialog._op, {target:target, dialog:dialog,stop:$.resizeTool.stop})
			});
			$.layerdrag.start(resizable[0], e, $.pdialog._op);
		},
		stop:function(){
			var data = $.data(arguments[0], 'layer-drag');
			$.pdialog.resizeDialog(arguments[0], data.options.dialog, data.options.target);
			$("body").css("cursor", "");
			$(arguments[0]).hide();
		}
	};
	$.layerdrag = { 
		start:function(obj, e, options) {
			if (!$.layerdrag.current) {
				$.layerdrag.current = {
					el: obj,
					oleft: parseInt(obj.style.left) || 0,
					owidth: parseInt(obj.style.width) || 0,
					otop: parseInt(obj.style.top) || 0,
					oheight:parseInt(obj.style.height) || 0,
					ox: e.pageX || e.screenX,
					oy: e.pageY || e.clientY
				};
				$(document).bind('mouseup', $.layerdrag.stop);
				$(document).bind('mousemove', $.layerdrag.drag);
			}
			return $.layerdrag.preventEvent(e);
		},
        drag: function(e) {
                if (!e) var e = window.event;
                var current = $.layerdrag.current;
				var data = $.data(current.el, 'layer-drag');
				var lmove = (e.pageX || e.screenX) - current.ox;
				var tmove = (e.pageY || e.clientY) - current.oy;
				if((e.pageY || e.clientY) <= 0 || (e.pageY || e.clientY) >= ($(window).height() - $(".dialogHeader", $(data.options.dialog)).outerHeight())) return false;
				var target = data.options.target;	
				var width = current.owidth;	
				var height = current.oheight;		
				if (target != "n" && target != "s") {
					width += (target.indexOf("w") >= 0)?-lmove:lmove;
				}
				if (width >= $.pdialog._op.minW) {
					if (target.indexOf("w") >= 0) {
						current.el.style.left = (current.oleft + lmove) + 'px';
					}
					if (target != "n" && target != "s") {
						current.el.style.width = width + 'px';
					}
				}
				if (target != "w" && target != "e") {
					height += (target.indexOf("n") >= 0)?-tmove:tmove;
				}
				if (height >= $.pdialog._op.minH) {
					if (target.indexOf("n") >= 0) {
						current.el.style.top = (current.otop + tmove) + 'px';
					}
					if (target != "w" && target != "e") {
						current.el.style.height = height + 'px';
					}
				}
				return $.layerdrag.preventEvent(e);
        },     
        stop: function(e) {
                var current = $.layerdrag.current;
                var data = $.data(current.el, 'layer-drag');
				$(document).unbind('mousemove', $.layerdrag.drag);
				$(document).unbind('mouseup', $.layerdrag.stop);
                if (data.options.stop) {
                        data.options.stop.apply(current.el, [ current.el ]);
                }
                $.layerdrag.current = null;
				return $.layerdrag.preventEvent(e);
        },
		preventEvent:function(e) {
                if (e.stopPropagation) e.stopPropagation();
                if (e.preventDefault) e.preventDefault();
                return false;
		}
	};
})(jQuery);/**
 * @author Roger Wu
 * @version 1.0
 */
(function($){

	$.fn.extend({
		jTask:function(options){
			return this.each(function(){
				var $task = $(this);
				var id = $task.attr("id");
				$task.click(function(e){
					var dialog = $("body").data(id);
					if ($task.hasClass("selected")) {
						$("a.minimize", dialog).trigger("click");
					} else {
						if (dialog.is(":hidden")) {
							$.taskBar.restoreDialog(dialog);
						} else
							$(dialog).trigger("click");
					}
					$.taskBar.scrollCurrent($(this));
					return false;
				});
				$("div.close", $task).click(function(e){
					$.pdialog.close(id)
					return false;
				}).hoverClass("closeHover");
				
				$task.hoverClass("hover");
			});
		}
	});
	$.taskBar = {
		_taskBar:null,
		_taskBox:null,
		_prevBut:null,
		_nextBut:null,
		_op:{id:"taskbar", taskBox:"div.taskbarContent",prevBut:".taskbarLeft",prevDis:"taskbarLeftDisabled", nextBut:".taskbarRight",nextDis:"taskbarRightDisabled", selected:"selected",boxMargin:"taskbarMargin"},
		init:function(options) {
			var $this = this;
			$.extend(this._op, options);
			this._taskBar = $("#" + this._op.id);
			if (this._taskBar.size() == 0) {
				this._taskBar = $(DWZ.frag["taskbar"]).appendTo($("#layout"));
				
				this._taskBar.find(".taskbarLeft").hoverClass("taskbarLeftHover");
				this._taskBar.find(".taskbarRight").hoverClass("taskbarRightHover");
			}
			this._taskBox = this._taskBar.find(this._op.taskBox);
			this._taskList = this._taskBox.find(">ul");
			this._prevBut = this._taskBar.find(this._op.prevBut);
			this._nextBut = this._taskBar.find(this._op.nextBut);
			this._prevBut.click(function(e){$this.scrollLeft()});
			this._nextBut.click(function(e){$this.scrollRight()});

			this._contextmenu(this._taskBox); // taskBar右键菜单
		},

		_contextmenu:function(obj) {
			$(obj).contextMenu('dialogCM', {
				bindings:{
					closeCurrent:function(t,m){
						var obj = t.isTag("li")?t:$.taskBar._getCurrent();
						$("div.close", obj).trigger("click");
					},
					closeOther:function(t,m){
						var selector = t.isTag("li")?("#" +t.attr("id")):".selected";
						var tasks = $.taskBar._taskList.find(">li:not(:"+selector+")");
						tasks.each(function(i){
							$("div.close",tasks[i]).trigger("click");
						});
					},
					closeAll:function(t,m){
						var tasks = $.taskBar._getTasks();
						tasks.each(function(i){
							$("div.close",tasks[i]).trigger("click");
						});
					}
				},
				ctrSub:function(t,m){
					var mCur = m.find("[rel='closeCurrent']");
					var mOther = m.find("[rel='closeOther']");
					if(!$.taskBar._getCurrent()[0]) {
						mCur.addClass("disabled");
						mOther.addClass("disabled");
					} else {
						if($.taskBar._getTasks().size() == 1)
							mOther.addClass("disabled");
					}
				}
			});
		},
		_scrollCurrent:function(){
			var iW = this._tasksW(this._getTasks());
			if (iW > this._getTaskBarW()) {
				var $this = this;
				var lTask = $(">li:last-child", this._taskList);
				var left = this._getTaskBarW() - lTask.position().left - lTask.outerWidth(true);
				this._taskList.animate({
					left: left + 'px'
				}, 200, function(){
					$this._ctrlScrollBut();
				});
			} else {
				this._ctrlScrollBut();
			}
		},
		_getTaskBarW:function(){
			return this._taskBox.width()- (this._prevBut.is(":hidden")?this._prevBut.width()+2:0) - (this._nextBut.is(":hidden")?this._nextBut.width()+2:0);
		},
		_scrollTask:function(task){
			var $this = this;
			if(task.position().left + this._getLeft()+task.outerWidth() > this._getBarWidth()) {
				var left = this._getTaskBarW()- task.position().left  - task.outerWidth(true) - 2;
				this._taskList.animate({left: left + 'px'}, 200, function(){
					$this._ctrlScrollBut();
				});
			} else if(task.position().left + this._getLeft() < 0) {
				var left = this._getLeft()-(task.position().left + this._getLeft());
				this._taskList.animate({left: left + 'px'}, 200, function(){
					$this._ctrlScrollBut();
				});
			}
		},
		/**
		 * 控制左右移动按钮何时显示与隐藏
		 */
		_ctrlScrollBut:function(){
			var iW = this._tasksW(this._getTasks());
			if (this._getTaskBarW() > iW) {
				this._taskBox.removeClass(this._op.boxMargin);
				this._nextBut.hide();
				this._prevBut.hide();
				if(this._getTasks().eq(0)[0])this._scrollTask(this._getTasks().eq(0));
			} else {
				this._taskBox.addClass(this._op.boxMargin);
				this._nextBut.show().removeClass(this._op.nextDis);
				this._prevBut.show().removeClass(this._op.prevDis);
				if (this._getLeft() >= 0){
					this._prevBut.addClass(this._op.prevDis);
				}
				if (this._getLeft() <= this._getTaskBarW() - iW) {
					this._nextBut.addClass(this._op.nextDis);
				} 
			}
		},
		_getLeft: function(){
			return this._taskList.position().left;
		},
		/**
		 * 取得第一个完全显示在taskbar上的任务
		 */
		_visibleStart: function(){
			var iLeft = this._getLeft();
			var jTasks = this._getTasks();
			for (var i=0; i<jTasks.size(); i++){
				if (jTasks.eq(i).position().left + jTasks.eq(i).outerWidth(true) + iLeft >= 0) return jTasks.eq(i);
			}
			return jTasks.eq(0);
		},
		/**
		 * 取得最后一个完全显示在taskbar上的任务
		 */
		_visibleEnd: function(){
			var iLeft = this._getLeft();
			var jTasks = this._getTasks();
			for (var i=0; i<jTasks.size(); i++){
				if (jTasks.eq(i).position().left + jTasks.eq(i).outerWidth(true) + iLeft > this._getBarWidth()) return jTasks.eq(i);
			}
			return jTasks.eq(jTasks.size()-1);
		},
		/**
		 * 取得所有的任务
		 */
		_getTasks:function(){
			return this._taskList.find(">li");
		},
		/**
		 * 计算所传入的所有任务的宽度和
		 * @param {Object} jTasks
		 */
		_tasksW:function(jTasks){
			var iW = 0;
			jTasks.each(function(){
				iW += $(this).outerWidth(true);
			});
			return iW;
		},
		_getBarWidth: function() {
			return this._taskBar.innerWidth();
		},
		/**
		 * 在任务栏上新加一个任务
		 * @param {Object} id
		 * @param {Object} title
		 */
		addDialog: function(id, title){
			this.show();
			var task = $("#"+id,this._taskList);
			if (!task[0]) {
				var taskFrag = '<li id="#taskid#"><div class="taskbutton"><span>#title#</span></div><div class="close">Close</div></li>';
				this._taskList.append(taskFrag.replace("#taskid#", id).replace("#title#", title));
				task = $("#"+id,this._taskList);
				task.jTask();
			} else {
				$(">div>span", task).text(title);
			}
			this._contextmenu(task);
			this.switchTask(id);
			this._scrollTask(task);
		},
		/**
		 * 关闭一个任务
		 * @param {Object} id
		 */
		closeDialog: function(obj){
			var task = (typeof obj == 'string')? $("#"+obj, this._taskList):obj;
			task.remove();
			if(this._getTasks().size() == 0){
				this.hide();
			}			
			this._scrollCurrent();
		},
		/**
		 * 
		 * @param {Object} id or dialog
		 */
		restoreDialog:function(obj){
			var dialog = (typeof obj == 'string')?$("body").data(obj):obj;
			var id = (typeof obj == 'string')?obj:dialog.data("id");
			var task = $.taskBar.getTask(id);
			$(".resizable").css({top: $(window).height()-60,left:$(task).position().left,height:$(task).outerHeight(),width:$(task).outerWidth()
			}).show().animate({top:$(dialog).css("top"),left: $(dialog).css("left"),width:$(dialog).css("width"),height:$(dialog).css("height")},250,function(){
				$(this).hide();
				$(dialog).show();
				$.pdialog.attachShadow(dialog);
			});
			$.taskBar.switchTask(id);
		},
		/**
		 * 把任务变成不是当前的
		 * @param {Object} id
		 */
		inactive:function(id){
			$("#" + id, this._taskList).removeClass("selected");
		},
		/**
		 * 向左移一个任务
		 */
		scrollLeft: function(){
			var task = this._visibleStart();
			this._scrollTask(task);
		},
		/**
		 * 向右移一个任务
		 */
		scrollRight: function(){
			var task = this._visibleEnd();
			this._scrollTask(task);
		},
		/**
		 * 移出当前点击的任务
		 * @param {Object} task
		 */
		scrollCurrent:function(task){
			this._scrollTask(task);
		},
		/**
		 * 切换任务
		 * @param {Object} id
		 */
		switchTask:function(id) {
			this._getCurrent().removeClass("selected");
			this.getTask(id).addClass("selected");
		},
		_getCurrent:function() {
			return this._taskList.find(">.selected");
		},
		getTask:function(id) {
			return $("#" + id, this._taskList);
		},
		/**
		 * 显示任务栏
		 */
		show:function(){
			if (this._taskBar.is(":hidden")) {
				this._taskBar.css("top", $(window).height() - 34 + this._taskBar.outerHeight()).show();
				this._taskBar.animate({
					top: $(window).height() - this._taskBar.outerHeight()
				}, 500);
			}
		},
		/**
		 * 隐藏任务栏
		 */
		hide:function(){
			this._taskBar.animate({
				top: $(window).height() - 29 + this._taskBar.outerHeight(true)
			}, 500,function(){
				$.taskBar._taskBar.hide();
			});
		}
	}
})(jQuery);/**
 * @author Roger Wu
 * reference:dwz.drag.js, dwz.dialogDrag.js, dwz.resize.js, dwz.taskBar.js
 */
(function ($) {
    $.pdialog = {
        _op: {
            height: 300,
            width: 580,
            minH: 40,
            minW: 50,
            total: 20,
            max: false,
            mask: false,
            resizable: true,
            drawable: true,
            maxable: false,
            minable: true,
            fresh: true
        },
        _current: null,
        _zIndex: 42,
        getCurrent: function () {
            return this._current;
        },
        reload: function (url, options) {
            var op = $.extend({data: {}, dialogId: "", callback: null}, options);
            var dialog = (op.dialogId && $("body").data(op.dialogId)) || this._current;
            if (dialog) {
                var jDContent = dialog.find(".dialogContent");
                jDContent.ajaxUrl({
                    type: "POST", url: url, data: op.data, callback: function (response) {
                        jDContent.find("[layoutH]").layoutH(jDContent);
                        $(".pageContent", dialog).width($(dialog).width() - 4);
                        $(":button.close", dialog).click(function () {
                            $.pdialog.close(dialog);
                            return false;
                        });
                        if ($.isFunction(op.callback)) op.callback(response);
                    }
                });
            }
        },
        //打开一个层
        open: function (url, dlgid, title, options) {
            var op = $.extend({}, $.pdialog._op, options);
            var dialog = $("body").data(dlgid);
            //重复打开一个层
            if (dialog) {
                if (dialog.is(":hidden")) {
                    dialog.show();
                }
                if (op.fresh || url != $(dialog).data("url")) {
                    dialog.data("url", url);
                    dialog.find(".dialogHeader").find("h1").html(title);
                    this.switchDialog(dialog);
                    var jDContent = dialog.find(".dialogContent");
                    var urlData = op.data;
                    if (!urlData) {
                        urlData = {};
                    }
                    jDContent.loadUrl(url, urlData, function () {
                        jDContent.find("[layoutH]").layoutH(jDContent);
                        $(".pageContent", dialog).width($(dialog).width() - 4);
                        $("button.close").click(function () {
                            $.pdialog.close(dialog);
                            return false;
                        });
                    });
                }

            } else { //打开一个全新的层
                $("body").append(DWZ.frag["dialogFrag"]);
                dialog = $(">.dialog:last-child", "body");
                dialog.data("id", dlgid);
                dialog.data("url", url);
                if (options.close) dialog.data("close", options.close);
                if (options.param) dialog.data("param", options.param);
                // 自定义函数
                if (options.sFunc) dialog.data("sFunc", options.sFunc);
                ($.fn.bgiframe && dialog.bgiframe());
                dialog.find(".dialogHeader").find("h1").html(title);
                $(dialog).css("zIndex", ($.pdialog._zIndex += 2));
                $("div.shadow").css("zIndex", $.pdialog._zIndex - 3).show();
                $.pdialog._init(dialog, options);
                $(dialog).click(function () {
                    $.pdialog.switchDialog(dialog);
                });
                if (op.resizable)
                    dialog.jresize();
                if (op.drawable)
                    dialog.dialogDrag();
                $("a.close", dialog).click(function (event) {
                    $.pdialog.close(dialog);
                    return false;
                });
                if (op.maxable) {
                    $("a.maximize", dialog).show().click(function (event) {
                        $.pdialog.switchDialog(dialog);
                        $.pdialog.maxsize(dialog);
                        dialog.jresize("destroy").dialogDrag("destroy");
                        return false;
                    });
                } else {
                    $("a.maximize", dialog).hide();
                }
                $("a.restore", dialog).click(function (event) {
                    $.pdialog.restore(dialog);
                    dialog.jresize().dialogDrag();
                    return false;
                });
                if (op.minable) {
                    $("a.minimize", dialog).show().click(function (event) {
                        $.pdialog.minimize(dialog);
                        return false;
                    });
                } else {
                    $("a.minimize", dialog).hide();
                }
                $("div.dialogHeader a", dialog).mousedown(function () {
                    return false;
                });
                $("div.dialogHeader", dialog).dblclick(function () {
                    if ($("a.restore", dialog).is(":hidden"))
                        $("a.maximize", dialog).trigger("click");
                    else
                        $("a.restore", dialog).trigger("click");
                });
                if (op.max) {
                    $.pdialog.maxsize(dialog);
                    dialog.jresize("destroy").dialogDrag("destroy");
                }
                $("body").data(dlgid, dialog);
                if ($.pdialog._current && op.mask) {
                    dialog.data("pid", $.pdialog._current.data("id"));
                }
                $.pdialog._current = dialog;
                $.pdialog.attachShadow(dialog);
                //load data
                var jDContent = $(".dialogContent", dialog);
                var urlData = op.data;
                if (!urlData) {
                    urlData = {};
                }
                jDContent.loadUrl(url, urlData, function () {
                    jDContent.find("[layoutH]").layoutH(jDContent);
                    $(".pageContent", dialog).width($(dialog).width() - 4);
                    $("button.close").click(function () {
                        $.pdialog.close(dialog);
                        return false;
                    });
                });
            }
            if (op.mask) {
                $(dialog).css("zIndex", 1000);
                $("a.minimize", dialog).hide();
                $(dialog).data("mask", true);
                $("#dialogBackground").show();
            } else {
                //add a task to task bar
                if (op.minable) $.taskBar.addDialog(dlgid, title);
            }
            //事件不在传播，dialog的click事件响应切换当前dialog的编号
            stopEvent(event);
        },
        /**
         * 切换当前层
         * @param {Object} dialog
         */
        switchDialog: function (dialog) {
            var index = $(dialog).css("zIndex");
            $.pdialog.attachShadow(dialog);
            if ($.pdialog._current != dialog) {
                var cindex = $($.pdialog._current).css("zIndex");
                $($.pdialog._current).css("zIndex", index);
                $(dialog).css("zIndex", cindex);
                $("div.shadow").css("zIndex", cindex - 1);
                $.pdialog._current = dialog;
            }
            $.taskBar.switchTask(dialog.data("id"));
        },
        /**
         * 给当前层附上阴隐层
         * @param {Object} dialog
         */
        attachShadow: function (dialog) {
            var shadow = $("div.shadow");
            if (shadow.is(":hidden")) shadow.show();
            shadow.css({
                top: parseInt($(dialog)[0].style.top) - 2,
                left: parseInt($(dialog)[0].style.left) - 4,
                height: parseInt($(dialog).height()) + 8,
                width: parseInt($(dialog).width()) + 8,
                zIndex: parseInt($(dialog).css("zIndex")) - 1
            });
            $(".shadow_c", shadow).children().andSelf().each(function () {
                $(this).css("height", $(dialog).outerHeight() - 4);
            });
        },
        _init: function (dialog, options) {
            var op = $.extend({}, this._op, options);
            var height = op.height > op.minH ? op.height : op.minH;
            var width = op.width > op.minW ? op.width : op.minW;
            if (isNaN(dialog.height()) || dialog.height() < height) {
                $(dialog).height(height + "px");
                $(".dialogContent", dialog).height(height - $(".dialogHeader", dialog).outerHeight() - $(".dialogFooter", dialog).outerHeight());
            }
            if (isNaN(dialog.css("width")) || dialog.width() < width) {
                $(dialog).width(width + "px");
            }

            var iTop = ($(window).height() - dialog.height()) / 2;
            dialog.css({
                left: ($(window).width() - dialog.width()) / 2,
                top: iTop > 0 ? iTop : 0
            });
        },
        /**
         * 初始化半透明层
         * @param {Object} resizable
         * @param {Object} dialog
         * @param {Object} target
         */
        initResize: function (resizable, dialog, target) {
            $("body").css("cursor", target + "-resize");
            resizable.css({
                top: $(dialog).css("top"),
                left: $(dialog).css("left"),
                height: $(dialog).css("height"),
                width: $(dialog).css("width")
            });
            resizable.show();
        },
        /**
         * 改变阴隐层
         * @param {Object} target
         * @param {Object} options
         */
        repaint: function (target, options) {
            var shadow = $("div.shadow");
            if (target != "w" && target != "e") {
                shadow.css("height", shadow.outerHeight() + options.tmove);
                $(".shadow_c", shadow).children().andSelf().each(function () {
                    $(this).css("height", $(this).outerHeight() + options.tmove);
                });
            }
            if (target == "n" || target == "nw" || target == "ne") {
                shadow.css("top", options.otop - 2);
            }
            if (options.owidth && (target != "n" || target != "s")) {
                shadow.css("width", options.owidth + 8);
            }
            if (target.indexOf("w") >= 0) {
                shadow.css("left", options.oleft - 4);
            }
        },
        /**
         * 改变左右拖动层的高度
         * @param {Object} target
         * @param {Object} tmove
         * @param {Object} dialog
         */
        resizeTool: function (target, tmove, dialog) {
            $("div[class^='resizable']", dialog).filter(function () {
                return $(this).attr("tar") == 'w' || $(this).attr("tar") == 'e';
            }).each(function () {
                $(this).css("height", $(this).outerHeight() + tmove);
            });
        },
        /**
         * 改变原始层的大小
         * @param {Object} obj
         * @param {Object} dialog
         * @param {Object} target
         */
        resizeDialog: function (obj, dialog, target) {
            var oleft = parseInt(obj.style.left);
            var otop = parseInt(obj.style.top);
            var height = parseInt(obj.style.height);
            var width = parseInt(obj.style.width);
            if (target == "n" || target == "nw") {
                tmove = parseInt($(dialog).css("top")) - otop;
            } else {
                tmove = height - parseInt($(dialog).css("height"));
            }
            $(dialog).css({left: oleft, width: width, top: otop, height: height});
            $(".dialogContent", dialog).css("width", (width - 2) + "px");
            $(".pageContent", dialog).css("width", (width - 4) + "px");
            if (target != "w" && target != "e") {
                var content = $(".dialogContent", dialog);
                content.css({height: height - $(".dialogHeader", dialog).outerHeight() - $(".dialogFooter", dialog).outerHeight()});
                content.find("[layoutH]").layoutH(content);
                $.pdialog.resizeTool(target, tmove, dialog);
            }
            $.pdialog.repaint(target, {oleft: oleft, otop: otop, tmove: tmove, owidth: width});

            $(window).trigger(DWZ.eventType.resizeGrid);
        },
        close: function (dialog) {
            if (typeof dialog == 'string') dialog = $("body").data(dialog);
            var close = dialog.data("close");
            var go = true;
            if (close && $.isFunction(close)) {
                var param = dialog.data("param");
                if (param && param != "") {
                    param = DWZ.jsonEval(param);
                    go = close(param);
                } else {
                    go = close();
                }
                if (!go) return;
            }
            var dialogCloseFun = $("span.dCloseFunc", dialog).attr("dCloseFunc");
            if (dialogCloseFun) {
                try {
                    eval("(" + dialogCloseFun + ")");
                } catch (e) {
                }
            }
            $(dialog).hide();
            $(dialog).find(".dialogContent").html("");
            $("div.shadow").hide();
            if ($(dialog).data("mask")) {
                $("#dialogBackground").hide();
            } else {
                if ($(dialog).data("id")) $.taskBar.closeDialog($(dialog).data("id"));
            }
            var pid = $(dialog).data("pid");
            $("body").removeData($(dialog).data("id"));
            $(dialog).trigger(DWZ.eventType.pageClear).remove();
            if (pid) {
                $.pdialog._current = null;
                this.switchDialog($("body").data(pid));
            }
        },
        closeCurrent: function () {
            this.close($.pdialog._current);
        },

        reloadCurrent: function () {
            $.pdialog.reload($.pdialog.getCurrent().data("url"), {});
        },

        reloadDialog: function (dialogId) {
            var dialog = $("body").data(dialogId);
            if (dialog) {
                $.pdialog.reload(dialog.data("url"), {dialogId: dialogId});
            }
        },

        checkTimeout: function () {
            var $conetnt = $(".dialogContent", $.pdialog._current);
            var json = DWZ.jsonEval($conetnt.html());
            if (json && json.statusCode == DWZ.statusCode.timeout) this.closeCurrent();
        },
        maxsize: function (dialog) {
            $(dialog).data("original", {
                top: $(dialog).css("top"),
                left: $(dialog).css("left"),
                width: $(dialog).css("width"),
                height: $(dialog).css("height")
            });
            $("a.maximize", dialog).hide();
            $("a.restore", dialog).show();
            var iContentW = $(window).width();
            var iContentH = $(window).height();
//            var iContentH = $(window).height() - 34;
            $(dialog).css({top: "0px", left: "0px", width: iContentW + "px", height: iContentH + "px"});
            $.pdialog._resizeContent(dialog, iContentW, iContentH);
        },
        restore: function (dialog) {
            var original = $(dialog).data("original");
            var dwidth = parseInt(original.width);
            var dheight = parseInt(original.height);
            $(dialog).css({
                top: original.top,
                left: original.left,
                width: dwidth,
                height: dheight
            });
            $.pdialog._resizeContent(dialog, dwidth, dheight);
            $("a.maximize", dialog).show();
            $("a.restore", dialog).hide();
            $.pdialog.attachShadow(dialog);
        },
        minimize: function (dialog) {
            $(dialog).hide();
            $("div.shadow").hide();
            var task = $.taskBar.getTask($(dialog).data("id"));
            $(".resizable").css({
                top: $(dialog).css("top"),
                left: $(dialog).css("left"),
                height: $(dialog).css("height"),
                width: $(dialog).css("width")
            }).show().animate({
                top: $(window).height() - 60,
                left: task.position().left,
                width: task.outerWidth(),
                height: task.outerHeight()
            }, 250, function () {
                $(this).hide();
                $.taskBar.inactive($(dialog).data("id"));
            });
        },
        _resizeContent: function (dialog, width, height) {
            var content = $(".dialogContent", dialog);
            content.css({
                width: (width - 2) + "px",
                height: height - $(".dialogHeader", dialog).outerHeight() - $(".dialogFooter", dialog).outerHeight()
            });
            content.find("[layoutH]").layoutH(content);
            $(".pageContent", dialog).css("width", (width - 4) + "px");

            $(window).trigger(DWZ.eventType.resizeGrid);
        }
    };
})(jQuery);/**
 * @author Roger Wu
 */
(function($){
	$.fn.dialogDrag = function(options){
        if (typeof options == 'string') {
                if (options == 'destroy') 
					return this.each(function() {
							var dialog = this;		
							$("div.dialogHeader", dialog).unbind("mousedown");
	                });
        }
		return this.each(function(){
			var dialog = $(this);
			$("div.dialogHeader", dialog).mousedown(function(e){
				$.pdialog.switchDialog(dialog);
				dialog.data("task",true);
				setTimeout(function(){
					if(dialog.data("task"))$.dialogDrag.start(dialog,e);
				},100);
				return false;
			}).mouseup(function(e){
				dialog.data("task",false);
				return false;
			});
		});
	};
	$.dialogDrag = {
		currId:null,
		_init:function(dialog) {
			this.currId = new Date().getTime();
			var shadow = $("#dialogProxy");
			if (!shadow.size()) {
				shadow = $(DWZ.frag["dialogProxy"]);
				$("body").append(shadow);
			}
			$("h1", shadow).html($(".dialogHeader h1", dialog).text());
		},
		start:function(dialog,event){
				this._init(dialog);
				var sh = $("#dialogProxy");
				sh.css({
					left: dialog.css("left"),
					top: dialog.css("top"),
					height: dialog.css("height"),
					width: dialog.css("width"),
					zIndex:parseInt(dialog.css("zIndex")) + 1
				}).show();
				$("div.dialogContent",sh).css("height",$("div.dialogContent",dialog).css("height"));
				sh.data("dialog",dialog);
				dialog.css({left:"-10000px",top:"-10000px"});
				$(".shadow").hide();				
				$(sh).jDrag({
					selector:".dialogHeader",
					stop: this.stop,
					event:event
				});
				return false;
		},
		stop:function(){
			var sh = $(arguments[0]);
			var dialog = sh.data("dialog");
			$(dialog).css({left:$(sh).css("left"),top:$(sh).css("top")});
			$.pdialog.attachShadow(dialog);
			$(sh).hide();
		}
	}
})(jQuery);/**
 * @author Roger Wu 
 * @version 1.0
 */
(function($){
	$.fn.cssv = function(pre){
		var cssPre = $(this).css(pre);
		return cssPre.substring(0, cssPre.indexOf("px")) * 1;
	};
	$.fn.jBar = function(options){
		var op = $.extend({container:"#container", collapse:".collapse", toggleBut:".toggleCollapse div", sideBar:"#sidebar", sideBar2:"#sidebar_s", splitBar:"#splitBar", splitBar2:"#splitBarProxy"}, options);
		return this.each(function(){
			var jbar = this;
			var sbar = $(op.sideBar2, jbar);
			var bar = $(op.sideBar, jbar);
			$(op.toggleBut, bar).click(function(){
				DWZ.ui.sbar = false;
				$(op.splitBar).hide();
				var sbarwidth = sbar.cssv("left") + sbar.outerWidth();
				var barleft = sbarwidth - bar.outerWidth();
				var cleft = $(op.container).cssv("left") - (bar.outerWidth() - sbar.outerWidth());
				var cwidth = bar.outerWidth() - sbar.outerWidth() + $(op.container).outerWidth();
				$(op.container).animate({left: cleft,width: cwidth},50,function(){
					bar.animate({left: barleft}, 500, function(){
						bar.hide();
						sbar.show().css("left", -50).animate({left: 5}, 200);
						$(window).trigger(DWZ.eventType.resizeGrid);
					});
				});
				$(op.collapse,sbar).click(function(){
					var sbarwidth = sbar.cssv("left") + sbar.outerWidth();
					if(bar.is(":hidden")) {
						$(op.toggleBut, bar).hide();
						bar.show().animate({left: sbarwidth}, 500);
						$(op.container).click(_hideBar);
					} else {
						bar.animate({left: barleft}, 500, function(){
							bar.hide();
						});
					}
					function _hideBar() {
						$(op.container).unbind("click", _hideBar);
						if (!DWZ.ui.sbar) {
							bar.animate({left: barleft}, 500, function(){
								bar.hide();
							});
						}
					}
					return false;
				});
				return false;
			});
			$(op.toggleBut, sbar).click(function(){
				DWZ.ui.sbar = true;
				sbar.animate({left: -25}, 200, function(){				
					bar.show();
				});
				bar.animate({left: 5}, 800, function(){
					$(op.splitBar).show();
					$(op.toggleBut, bar).show();					
					var cleft = 5 + bar.outerWidth() + $(op.splitBar).outerWidth();
					var cwidth = $(op.container).outerWidth() - (cleft - $(op.container).cssv("left"));
					$(op.container).css({left: cleft,width: cwidth});
					$(op.collapse, sbar).unbind('click');
					$(window).trigger(DWZ.eventType.resizeGrid);
				});
				return false;
			});
			$(op.splitBar).mousedown(function(event){
				$(op.splitBar2).each(function(){
					var spbar2 = $(this);
					setTimeout(function(){spbar2.show();}, 100);
					spbar2.css({visibility: "visible",left: $(op.splitBar).css("left")});					
					spbar2.jDrag($.extend(options, {obj:$("#sidebar"), move:"horizontal", event:event,stop: function(){
						$(this).css("visibility", "hidden");
						var move = $(this).cssv("left") - $(op.splitBar).cssv("left");
						var sbarwidth = bar.outerWidth() + move;
						var cleft = $(op.container).cssv("left") + move;
						var cwidth = $(op.container).outerWidth() - move;
						bar.css("width", sbarwidth);
						$(op.splitBar).css("left", $(this).css("left"));
						$(op.container).css({left: cleft,width: cwidth});

					}}));
					return false;					
				});
			});
		});
	}
})(jQuery);
/**
 * @author Roger Wu
 */
(function($){
	$.fn.jDrag = function(options){
		if (typeof options == 'string') {
			if (options == 'destroy') 
				return this.each(function(){
					$(this).unbind('mousedown', $.rwdrag.start);
					$.data(this, 'pp-rwdrag', null);
				});
		}
		return this.each(function(){
			var el = $(this);
			$.data($.rwdrag, 'pp-rwdrag', {
				options: $.extend({
					el: el,
					obj: el
				}, options)
			});
			if (options.event) 
				$.rwdrag.start(options.event);
			else {
				var select = options.selector;
				$(select, obj).bind('mousedown', $.rwdrag.start);
			}
		});
	};
	$.rwdrag = {
		start: function(e){
			document.onselectstart=function(e){return false};//禁止选择

			var data = $.data(this, 'pp-rwdrag');
			var el = data.options.el[0];
			$.data(el, 'pp-rwdrag', {
				options: data.options
			});
			if (!$.rwdrag.current) {
				$.rwdrag.current = {
					el: el,
					oleft: parseInt(el.style.left) || 0,
					otop: parseInt(el.style.top) || 0,
					ox: e.pageX || e.screenX,
					oy: e.pageY || e.screenY
				};
				$(document).bind("mouseup", $.rwdrag.stop).bind("mousemove", $.rwdrag.drag);
			}
		},
		drag: function(e){
			if (!e)  var e = window.event;
			var current = $.rwdrag.current;
			var data = $.data(current.el, 'pp-rwdrag');
			var left = (current.oleft + (e.pageX || e.clientX) - current.ox);
			var top = (current.otop + (e.pageY || e.clientY) - current.oy);
			if (top < 1) top = 0;
			if (data.options.move == 'horizontal') {
				if ((data.options.minW && left >= $(data.options.obj).cssv("left") + data.options.minW) && (data.options.maxW && left <= $(data.options.obj).cssv("left") + data.options.maxW)) 
					current.el.style.left = left + 'px';
				else if (data.options.scop) {
					if (data.options.relObj) {
						if ((left - parseInt(data.options.relObj.style.left)) > data.options.cellMinW) {
							current.el.style.left = left + 'px';
						}
					} else 
						current.el.style.left = left + 'px';
				}
			} else if (data.options.move == 'vertical') {
					current.el.style.top = top + 'px';
			} else {
				var selector = data.options.selector ? $(data.options.selector, data.options.obj) : $(data.options.obj);
				if (left >= -selector.outerWidth() * 2 / 3 && top >= 0 && (left + selector.outerWidth() / 3 < $(window).width()) && (top + selector.outerHeight() < $(window).height())) {
					current.el.style.left = left + 'px';
					current.el.style.top = top + 'px';
				}
			}
			
			if (data.options.drag) {
				data.options.drag.apply(current.el, [current.el]);
			}
			
			return $.rwdrag.preventEvent(e);
		},
		stop: function(e){
			var current = $.rwdrag.current;
			var data = $.data(current.el, 'pp-rwdrag');
			$(document).unbind('mousemove', $.rwdrag.drag).unbind('mouseup', $.rwdrag.stop);
			if (data.options.stop) {
				data.options.stop.apply(current.el, [current.el]);
			}
			$.rwdrag.current = null;

			document.onselectstart=function(e){return true};//启用选择
			return $.rwdrag.preventEvent(e);
		},
		preventEvent:function(e){
			if (e.stopPropagation) e.stopPropagation();
			if (e.preventDefault) e.preventDefault();
			return false;			
		}
	};
})(jQuery);
/**
 * @author ZhangHuihua@msn.com
 */
(function ($) {
    var _op = {
        cursor: 'move', // selector 的鼠标手势
        sortBoxs: 'div.sortDrag', //拖动排序项父容器
        replace: false, //2个sortBox之间拖动替换
        items: '> *', //拖动排序项选择器
        selector: '', //拖动排序项用于拖动的子元素的选择器，为空时等于item
        zIndex: 1000
    };
    var sortDrag = {
        start: function ($sortBox, $item, event, op) {
            var $placeholder = this._createPlaceholder($item);
            var $helper = $item.clone();
            var position = $item.position();
            $helper.data('$sortBox', $sortBox).data('op', op).data('$item', $item).data('$placeholder', $placeholder);
            $helper.addClass('sortDragHelper').css({position: 'absolute', top: position.top, left: position.left, zIndex: op.zIndex, width: $item.width() + 'px', height: $item.height() + 'px'}).jDrag({
                selector: op.selector,
                drag: this.drag,
                stop: this.stop,
                event: event
            });

            $item.before($placeholder).before($helper).hide();
            return false;
        },
        drag: function () {
            var $helper = $(arguments[0]), $sortBox = $helper.data('$sortBox'), $placeholder = $helper.data('$placeholder');
            var $items = $sortBox.find($helper.data('op')['items']).filter(':visible').filter(':not(.sortDragPlaceholder, .sortDragHelper)');
            var helperPos = $helper.position(), firstPos = $items.eq(0).position();

            var $overBox = sortDrag._getOverSortBox($helper);
            if ($overBox.length > 0 && $overBox[0] != $sortBox[0]) { //移动到其他容器
                $placeholder.appendTo($overBox);
                $helper.data('$sortBox', $overBox);
            } else {
                for (var i = 0; i < $items.length; i++) {
                    var $this = $items.eq(i), position = $this.position();

                    if (helperPos.top > position.top + 10) {
                        $this.after($placeholder);
                    } else if (helperPos.top <= position.top) {
                        $this.before($placeholder);
                        break;
                    }
                }
            }
        },
        stop: function () {
            var $helper = $(arguments[0]), $sortBox = $helper.data('$sortBox'), $item = $helper.data('$item'), $placeholder = $helper.data('$placeholder');

            var position = $placeholder.position();
            $helper.animate({
                top: (position.top) + "px",
                left: position.left + "px"
            }, {
                complete: function () {
                    if ($helper.data('op')['replace']) { //2个sortBox之间替换处理
                        $srcBox = $item.parents(_op.sortBoxs + ":first");
                        $destBox = $placeholder.parents(_op.sortBoxs + ":first");
                        if ($srcBox[0] != $destBox[0]) { //判断是否移动到其他容器中
                            $replaceItem = $placeholder.next();
                            if ($replaceItem.size() > 0) {
                                $replaceItem.insertAfter($item);
                            }
                        }
                    }
                    $item.insertAfter($placeholder).show();
                    $placeholder.remove();
                    $helper.remove();
                },
                duration: 300
            });
        },
        _createPlaceholder: function ($item) {
            return $('<' + $item[0].nodeName + ' class="sortDragPlaceholder"/>').css({
                width: $item.outerWidth() + 'px',
                height: $item.outerHeight() + 'px',
                marginTop: $item.css('marginTop'),
                marginRight: $item.css('marginRight'),
                marginBottom: $item.css('marginBottom'),
                marginLeft: $item.css('marginLeft')
            });
        },
        _getOverSortBox: function ($item) {
            var itemPos = $item.position();
            var y = itemPos.top + ($item.height() / 2), x = itemPos.left + ($item.width() / 2);
            return $(_op.sortBoxs).filter(':visible').filter(function () {
                var $sortBox = $(this), sortBoxPos = $sortBox.position();
                return DWZ.isOver(y, x, sortBoxPos.top, sortBoxPos.left, $sortBox.height(), $sortBox.width());
            });
        }
    };

    $.fn.sortDrag = function (options) {
        return this.each(function () {
            var op = $.extend({}, _op, options);
            var $sortBox = $(this);
            if ($sortBox.attr('selector')) op.selector = $sortBox.attr('selector');
            $sortBox.find(op.items).each(function (i) {
                var $item = $(this), $selector = $item;
                if (op.selector) {
                    $selector = $item.find(op.selector).css({cursor: op.cursor});
                }
                $selector.mousedown(function (event) {
                    sortDrag.start($sortBox, $item, event, op);
                    event.preventDefault();
                });
            });
        });
    }
    $.fn.addSortDrag = function (options, sortObj) {
        return this.each(function () {
            var op = $.extend({}, _op, options);
            var $sortBox = $(this);
            if ($sortBox.attr('selector')) op.selector = $sortBox.attr('selector');
            $sortBox.find(sortObj).each(function (i) {
                var $item = $(this), $selector = $item;
                $selector.mousedown(function (event) {
                    sortDrag.start($sortBox, $item, event, op);
                    event.preventDefault();
                });
            });
        });
    }
})(jQuery);
/**
 * Theme Plugins
 * @author ZhangHuihua@msn.com
 */
(function($){
	$.fn.extend({
		cssTable: function(options){

			return this.each(function(){
				var $this = $(this);
				var $trs = $this.find('tbody>tr');
				var $grid = $this.parent(); // table
				var nowrap = $this.hasClass("nowrap");
				
				$trs.hoverClass("hover").each(function(index){
					var $tr = $(this);
					if (!nowrap && index % 2 == 1) $tr.addClass("trbg");
					
					$tr.click(function(){
						$trs.filter(".selected").removeClass("selected");
						$tr.addClass("selected");
						var sTarget = $tr.attr("target");
						if (sTarget) {
							if ($("#"+sTarget, $grid).size() == 0) {
								$grid.prepend('<input id="'+sTarget+'" type="hidden" />');
							}
							$("#"+sTarget, $grid).val($tr.attr("rel"));
						}
					});
					
				});

				$this.find("thead [orderField]").orderBy({
					targetType: $this.attr("targetType"),
					rel:$this.attr("rel"),
					asc: $this.attr("asc") || "asc",
					desc:  $this.attr("desc") || "desc"
				});
			});
		}
	});
})(jQuery);
/**
 * @author Roger Wu v1.0
 * @author ZhangHuihua@msn.com 2011-4-1
 */
(function ($) {
    $.fn.jTable = function (options) {
        return this.each(function () {
            var $table = $(this), nowrapTD = $table.attr("nowrapTD");
            var tlength = $table.width();
            var adjust = "true";
            if (typeof($table.attr("adjust")) != "undefined") {
                adjust = $table.attr("adjust");
            }
            var aStyles = [];
            var $tc = $table.parent().addClass("j-resizeGrid"); // table parent container
            var layoutH = $(this).attr("layoutH");

            var oldThs = $table.find("thead>tr:last-child").find("th");

            for (var i = 0, l = oldThs.size(); i < l; i++) {
                var $th = $(oldThs[i]);
                var style = [], width = $th.innerWidth() - (100 * $th.innerWidth() / tlength) - 2;
                if (adjust != "true" && parseInt($th.attr("width")) > width) {
                    width = parseInt($th.attr("width"));
                }
                style[0] = parseInt(width);
                style[1] = $th.attr("align");
                aStyles[aStyles.length] = style;
            }
            $(this).wrap("<div class='grid'></div>");
            var $grid = $table.parent().html($table.html());
            var thead = $grid.find("thead");
            thead.wrap("<div class='gridHeader'><div class='gridThead'><table style='width:" + (tlength - 20) + "px;'></table></div></div>");

            var lastH = $(">tr:last-child", thead);
            var ths = $(">th", lastH);
            $("th", thead).each(function () {
                var $th = $(this);
                $th.html("<div class='gridCol' title='" + $th.text() + "'>" + $th.html() + "</div>");
            });

            ths.each(function (i) {
                var $th = $(this), style = aStyles[i];
                $th.addClass(style[1]).hoverClass("hover").removeAttr("align").removeAttr("width").width(style[0]);
            }).filter("[orderField]").orderBy({
                targetType: $table.attr("targetType"),
                rel: $table.attr("rel"),
                asc: $table.attr("asc") || "asc",
                desc: $table.attr("desc") || "desc"
            });

            var tbody = $grid.find(">tbody");
            var layoutStr = layoutH ? " layoutH='" + layoutH + "'" : "";

            tbody.wrap("<div class='gridScroller'" + layoutStr + " style='width:" + $tc.width() + "px;'><div class='gridTbody'><table style='width:" + (tlength - 20) + "px;'></table></div></div>");
            var ftr = $(">tr:first-child", tbody);
            var $trs = tbody.find('>tr');

            $trs.hoverClass().each(function () {
                var $tr = $(this);
                var $ftds = $(">td", this);

                for (var i = 0; i < $ftds.size(); i++) {
                    var $ftd = $($ftds[i]);
                    if (nowrapTD != "false") $ftd.html("<div>" + $ftd.html() + "</div>");
                    if (i < aStyles.length) $ftd.addClass(aStyles[i][1]);
                }
                $tr.click(function () {
                    $trs.filter(".selected").removeClass("selected");
                    $tr.addClass("selected");
                    var sTarget = $tr.attr("target");
                    if (sTarget) {
                        if ($("#" + sTarget, $grid).size() == 0) {
                            $grid.prepend('<input id="' + sTarget + '" type="hidden" />');
                        }
                        $("#" + sTarget, $grid).val($tr.attr("rel"));
                    }
                });
            });

            $(">td", ftr).each(function (i) {
                if (i < aStyles.length) $(this).width(aStyles[i][0]);
            });
            $grid.append("<div class='resizeMarker' style='height:300px; left:57px;display:none;'></div><div class='resizeProxy' style='height:300px; left:377px;display:none;'></div>");

            var scroller = $(".gridScroller", $grid);
            scroller.scroll(function (event) {
                var header = $(".gridThead", $grid);
                if (scroller.scrollLeft() > 0) {
                    header.css("position", "relative");
                    var scroll = scroller.scrollLeft();
                    header.css("left", scroller.cssv("left") - scroll);
                }
                if (scroller.scrollLeft() == 0) {
                    header.css("position", "relative");
                    header.css("left", "0px");
                }
                return false;
            });


            $(">tr", thead).each(function () {

                $(">th", this).each(function (i) {
                    var th = this, $th = $(this);
                    $th.mouseover(function (event) {
                        var offset = $.jTableTool.getOffset(th, event).offsetX;
                        if ($th.outerWidth() - offset < 5) {
                            $th.css("cursor", "col-resize").mousedown(function (event) {
                                $(".resizeProxy", $grid).show().css({
                                    left: $.jTableTool.getRight(th) - $(".gridScroller", $grid).scrollLeft(),
                                    top: $.jTableTool.getTop(th),
                                    height: $.jTableTool.getHeight(th, $grid),
                                    cursor: "col-resize"
                                });
                                $(".resizeMarker", $grid).show().css({
                                    left: $.jTableTool.getLeft(th) + 1 - $(".gridScroller", $grid).scrollLeft(),
                                    top: $.jTableTool.getTop(th),
                                    height: $.jTableTool.getHeight(th, $grid)
                                });
                                $(".resizeProxy", $grid).jDrag($.extend(options, {scop: true, cellMinW: 20, relObj: $(".resizeMarker", $grid)[0],
                                        move: "horizontal",
                                        event: event,
                                        stop: function () {
                                            var pleft = $(".resizeProxy", $grid).position().left;
                                            var mleft = $(".resizeMarker", $grid).position().left;
                                            var move = pleft - mleft - $th.outerWidth() - 9;

                                            var cols = $.jTableTool.getColspan($th);
                                            var cellNum = $.jTableTool.getCellNum($th);
                                            var oldW = $th.width(), newW = $th.width() + move;
                                            $th.width(newW + "px");

                                            // 数据区域大小
                                            var tempftr = $(">tr:first-child", tbody);
                                            var $dcell = $(">td", tempftr).eq(cellNum - 1);
                                            $dcell.width(newW + "px");

                                            var $table1 = $(thead).parent();
                                            $table1.width(($table1.width() - oldW + newW) + "px");

                                            var $table2 = $(tbody).parent();
//                                            $table2.width(($table2.width() - oldW + newW) + "px");
                                            $table2.width($table1.width());

                                            $(".resizeMarker,.resizeProxy", $grid).hide();
                                        }
                                    })
                                );
                            });
                        } else {
                            $th.css("cursor", $th.attr("orderField") ? "pointer" : "default");
                            $th.unbind("mousedown");
                        }
                        return false;
                    });
                });
            });

            function _resizeGrid() {
                $("div.j-resizeGrid").each(function () {
                    var width = $(this).innerWidth();
                    if (width) {
                        $("div.gridScroller", this).width(width + "px");
                    }
                });
            }

            $(window).unbind(DWZ.eventType.resizeGrid).bind("resizeGrid", _resizeGrid);
        });
    };

    $.fn.jResize = function () {
        var $containerDiv = $(this);
        // 改变的大小
        var newWidth = $containerDiv.width();
        var gridThead = $("div.gridThead", $containerDiv);
        if (gridThead.length > 0) {
            var titleTable = $(">table", gridThead);
            titleTable.css("width", newWidth - 20);
        }
        var gridScroller = $("div.gridScroller", $containerDiv);
        gridScroller.css("width", newWidth);
        var gridTbody = $("div.gridTbody", $containerDiv);
        if (gridTbody.length > 0) {
            // 改变的大小
            var bodyTable = $(">table", gridTbody);
            bodyTable.css("width", newWidth - 20);
        }
    };

    $.jTableTool = {
        getLeft: function (obj) {
            var width = 0;
            $(obj).prevAll().each(function () {
                width += $(this).outerWidth();
            });
            return width - 1;
        },
        getRight: function (obj) {
            var width = 0;
            $(obj).prevAll().andSelf().each(function () {
                width += $(this).outerWidth();
            });
            return width - 1;
        },
        getTop: function (obj) {
            var height = 0;
            $(obj).parent().prevAll().each(function () {
                height += $(this).outerHeight();
            });
            return height;
        },
        getHeight: function (obj, parent) {
            var height = 0;
            var head = $(obj).parent();
            head.nextAll().andSelf().each(function () {
                height += $(this).outerHeight();
            });
            $(".gridTbody", parent).children().each(function () {
                height += $(this).outerHeight();
            });
            return height;
        },
        getCellNum: function (obj) {
            return $(obj).prevAll().andSelf().size();
        },
        getColspan: function (obj) {
            return $(obj).attr("colspan") || 1;
        },
        getStart: function (obj) {
            var start = 1;
            $(obj).prevAll().each(function () {
                start += parseInt($(this).attr("colspan") || 1);
            });
            return start;
        },
        getPageCoord: function (element) {
            var coord = {x: 0, y: 0};
            while (element) {
                coord.x += element.offsetLeft;
                coord.y += element.offsetTop;
                element = element.offsetParent;
            }
            return coord;
        },
        getOffset: function (obj, evt) {
            if ($.browser.msie) {
                var objset = $(obj).offset();
                var evtset = {
                    offsetX: evt.pageX || evt.screenX,
                    offsetY: evt.pageY || evt.screenY
                };
                var offset = {
                    offsetX: evtset.offsetX - objset.left,
                    offsetY: evtset.offsetY - objset.top
                };
                return offset;
            }
            var target = evt.target;
            if (target.offsetLeft == undefined) {
                target = target.parentNode;
            }
            var pageCoord = $.jTableTool.getPageCoord(target);
            var eventCoord = {
                x: window.pageXOffset + evt.clientX,
                y: window.pageYOffset + evt.clientY
            };
            var offset = {
                offsetX: eventCoord.x - pageCoord.x,
                offsetY: eventCoord.y - pageCoord.y
            };
            return offset;
        }
    };
})(jQuery);
/**
 * @author ZhangHuihua@msn.com
 * 
 */

/**
 * 普通ajax表单提交
 * @param {Object} form
 * @param {Object} callback
 * @param {String} confirmMsg 提示确认信息
 */
function validateCallback(form, callback, confirmMsg) {
	var $form = $(form);

	if (!$form.valid()) {
		return false;
	}
	
	var _submitFn = function(){
		$.ajax({
			type: form.method || 'POST',
			url:$form.attr("action"),
			data:$form.serializeArray(),
			dataType:"json",
			cache: false,
			success: callback || DWZ.ajaxDone,
			error: DWZ.ajaxError
		});
	}
	
	if (confirmMsg) {
		alertMsg.confirm(confirmMsg, {okCall: _submitFn});
	} else {
		_submitFn();
	}
	
	return false;
}
/**
 * 带文件上传的ajax表单提交
 * @param {Object} form
 * @param {Object} callback
 */
function iframeCallback(form, callback){
	var $form = $(form), $iframe = $("#callbackframe");
	if(!$form.valid()) {return false;}

	if ($iframe.size() == 0) {
		$iframe = $("<iframe id='callbackframe' name='callbackframe' src='about:blank' style='display:none'></iframe>").appendTo("body");
	}
	if(!form.ajax) {
		$form.append('<input type="hidden" name="ajax" value="1" />');
	}
	form.target = "callbackframe";
	
	_iframeResponse($iframe[0], callback || DWZ.ajaxDone);
}
function _iframeResponse(iframe, callback){
	var $iframe = $(iframe), $document = $(document);
	
	$document.trigger("ajaxStart");
	
	$iframe.bind("load", function(event){
		$iframe.unbind("load");
		$document.trigger("ajaxStop");
		
		if (iframe.src == "javascript:'%3Chtml%3E%3C/html%3E';" || // For Safari
			iframe.src == "javascript:'<html></html>';") { // For FF, IE
			return;
		}

		var doc = iframe.contentDocument || iframe.document;

		// fixing Opera 9.26,10.00
		if (doc.readyState && doc.readyState != 'complete') return; 
		// fixing Opera 9.64
		if (doc.body && doc.body.innerHTML == "false") return;
	   
		var response;
		
		if (doc.XMLDocument) {
			// response is a xml document Internet Explorer property
			response = doc.XMLDocument;
		} else if (doc.body){
			try{
				response = $iframe.contents().find("body").text();
				response = jQuery.parseJSON(response);
			} catch (e){ // response is html document or plain text
				response = doc.body.innerHTML;
			}
		} else {
			// response is a xml document
			response = doc;
		}
		
		callback(response);
	});
}

/**
 * navTabAjaxDone是DWZ框架中预定义的表单提交回调函数．
 * 服务器转回navTabId可以把那个navTab标记为reloadFlag=1, 下次切换到那个navTab时会重新载入内容. 
 * callbackType如果是closeCurrent就会关闭当前tab
 * 只有callbackType="forward"时需要forwardUrl值
 * navTabAjaxDone这个回调函数基本可以通用了，如果还有特殊需要也可以自定义回调函数.
 * 如果表单提交只提示操作是否成功, 就可以不指定回调函数. 框架会默认调用DWZ.ajaxDone()
 * <form action="/user.do?method=save" onsubmit="return validateCallback(this, navTabAjaxDone)">
 * 
 * form提交后返回json数据结构statusCode=DWZ.statusCode.ok表示操作成功, 做页面跳转等操作. statusCode=DWZ.statusCode.error表示操作失败, 提示错误原因. 
 * statusCode=DWZ.statusCode.timeout表示session超时，下次点击时跳转到DWZ.loginUrl
 * {"statusCode":"200", "message":"操作成功", "navTabId":"navNewsLi", "forwardUrl":"", "callbackType":"closeCurrent", "rel"."xxxId"}
 * {"statusCode":"300", "message":"操作失败"}
 * {"statusCode":"301", "message":"会话超时"}
 * 
 */
function navTabAjaxDone(json){
	DWZ.ajaxDone(json);
	if (json.statusCode == DWZ.statusCode.ok){
		if (json.navTabId){ //把指定navTab页面标记为需要“重新载入”。注意navTabId不能是当前navTab页面的
			navTab.reloadFlag(json.navTabId);
		} else { //重新载入当前navTab页面
			var $pagerForm = $("#pagerForm", navTab.getCurrentPanel());
			var args = $pagerForm.size()>0 ? $pagerForm.serializeArray() : {}
			navTabPageBreak(args, json.rel);
		}
		
		if ("closeCurrent" == json.callbackType) {
			setTimeout(function(){navTab.closeCurrentTab(json.navTabId);}, 100);
		} else if ("forward" == json.callbackType) {
			navTab.reload(json.forwardUrl);
		} else if ("forwardConfirm" == json.callbackType) {
			alertMsg.confirm(json.confirmMsg || DWZ.msg("forwardConfirmMsg"), {
				okCall: function(){
					navTab.reload(json.forwardUrl);
				},
				cancelCall: function(){
					navTab.closeCurrentTab(json.navTabId);
				}
			});
		} else {
			navTab.getCurrentPanel().find(":input[initValue]").each(function(){
				var initVal = $(this).attr("initValue");
				$(this).val(initVal);
			});
		}
	}
}

/**
 * dialog上的表单提交回调函数
 * 服务器转回navTabId，可以重新载入指定的navTab. statusCode=DWZ.statusCode.ok表示操作成功, 自动关闭当前dialog
 * 
 * form提交后返回json数据结构,json格式和navTabAjaxDone一致
 */
function dialogAjaxDone(json){
	DWZ.ajaxDone(json);
	if (json.statusCode == DWZ.statusCode.ok){
		if (json.navTabId){
			navTab.reload(json.forwardUrl, {navTabId: json.navTabId});
		} else {
			var $pagerForm = $("#pagerForm", navTab.getCurrentPanel());
			var args = $pagerForm.size()>0 ? $pagerForm.serializeArray() : {}
			navTabPageBreak(args, json.rel);
		}
		if ("closeCurrent" == json.callbackType) {
			$.pdialog.closeCurrent();
		}
	}
}

/**
 * 处理navTab上的查询, 会重新载入当前navTab
 * @param {Object} form
 */
function navTabSearch(form, navTabId){
	var $form = $(form);
	if (form[DWZ.pageInfo.pageNum]) form[DWZ.pageInfo.pageNum].value = 1;
	navTab.reload($form.attr('action'), {data: $form.serializeArray(), navTabId:navTabId});
	return false;
}
/**
 * 处理dialog弹出层上的查询, 会重新载入当前dialog
 * @param {Object} form
 */
function dialogSearch(form){
	var $form = $(form);
	if (form[DWZ.pageInfo.pageNum]) form[DWZ.pageInfo.pageNum].value = 1;
	$.pdialog.reload($form.attr('action'), {data: $form.serializeArray()});
	return false;
}
function dwzSearch(form, targetType){
	if (targetType == "dialog") dialogSearch(form);
	else navTabSearch(form);
	return false;
}
/**
 * 处理div上的局部查询, 会重新载入指定div
 * @param {Object} form
 */
function divSearch(form, rel){
	var $form = $(form);
	if (form[DWZ.pageInfo.pageNum]) form[DWZ.pageInfo.pageNum].value = 1;
	if (rel) {
		var $box = $("#" + rel);
		$box.ajaxUrl({
			type:"POST", url:$form.attr("action"), data: $form.serializeArray(), callback:function(){
				$box.find("[layoutH]").layoutH();
			}
		});
	}
	return false;
}
/**
 * 
 * @param {Object} args {pageNum:"",numPerPage:"",orderField:"",orderDirection:""}
 * @param String formId 分页表单选择器，非必填项默认值是 "pagerForm"
 */
function _getPagerForm($parent, args) {
	var form = $("#pagerForm", $parent).get(0);

	if (form) {
		if (args["pageNum"]) form[DWZ.pageInfo.pageNum].value = args["pageNum"];
		if (args["numPerPage"]) form[DWZ.pageInfo.numPerPage].value = args["numPerPage"];
		if (args["orderField"]) form[DWZ.pageInfo.orderField].value = args["orderField"];
		if (args["orderDirection"] && form[DWZ.pageInfo.orderDirection]) form[DWZ.pageInfo.orderDirection].value = args["orderDirection"];
	}
	
	return form;
}


/**
 * 处理navTab中的分页和排序
 * targetType: navTab 或 dialog
 * rel: 可选 用于局部刷新div id号
 * data: pagerForm参数 {pageNum:"n", numPerPage:"n", orderField:"xxx", orderDirection:""}
 * callback: 加载完成回调函数
 */
function dwzPageBreak(options){
	var op = $.extend({ targetType:"navTab", rel:"", data:{pageNum:"", numPerPage:"", orderField:"", orderDirection:""}, callback:null}, options);
	var $parent = op.targetType == "dialog" ? $.pdialog.getCurrent() : navTab.getCurrentPanel();

	if (op.rel) {
		var $box = $parent.find("#" + op.rel);
		var form = _getPagerForm($box, op.data);
		if (form) {
			$box.ajaxUrl({
				type:"POST", url:$(form).attr("action"), data: $(form).serializeArray(), callback:function(){
					$box.find("[layoutH]").layoutH();
				}
			});
		}
	} else {
		var form = _getPagerForm($parent, op.data);
		var params = $(form).serializeArray();
		
		if (op.targetType == "dialog") {
			if (form) $.pdialog.reload($(form).attr("action"), {data: params, callback: op.callback});
		} else {
			if (form) navTab.reload($(form).attr("action"), {data: params, callback: op.callback});
		}
	}
}
/**
 * 处理navTab中的分页和排序
 * @param args {pageNum:"n", numPerPage:"n", orderField:"xxx", orderDirection:""}
 * @param rel： 可选 用于局部刷新div id号
 */
function navTabPageBreak(args, rel){
	dwzPageBreak({targetType:"navTab", rel:rel, data:args});
}
/**
 * 处理dialog中的分页和排序
 * 参数同 navTabPageBreak 
 */
function dialogPageBreak(args, rel){
	dwzPageBreak({targetType:"dialog", rel:rel, data:args});
}


function ajaxTodo(url, callback){
	var $callback = callback || navTabAjaxDone;
	if (! $.isFunction($callback)) $callback = eval('(' + callback + ')');
	$.ajax({
		type:'POST',
		url:url,
		dataType:"json",
		cache: false,
		success: $callback,
		error: DWZ.ajaxError
	});
}

/**
 * http://www.uploadify.com/documentation/uploadify/onqueuecomplete/	
 */
function uploadifyQueueComplete(queueData){

	var msg = "The total number of files uploaded: "+queueData.uploadsSuccessful+"<br/>"
		+ "The total number of errors while uploading: "+queueData.uploadsErrored+"<br/>"
		+ "The total number of bytes uploaded: "+queueData.queueBytesUploaded+"<br/>"
		+ "The average speed of all uploaded files: "+queueData.averageSpeed;
	
	if (queueData.uploadsErrored) {
		alertMsg.error(msg);
	} else {
		alertMsg.correct(msg);
	}
}
/**
 * http://www.uploadify.com/documentation/uploadify/onuploadsuccess/
 */
function uploadifySuccess(file, data, response){
    alertMsg.info(data);
}

/**
 * http://www.uploadify.com/documentation/uploadify/onuploaderror/
 */
function uploadifyError(file, errorCode, errorMsg) {
	alertMsg.error(errorCode+": "+errorMsg);
}


/**
 * http://www.uploadify.com/documentation/
 * @param {Object} event
 * @param {Object} queueID
 * @param {Object} fileObj
 * @param {Object} errorObj
 */
function uploadifyError(event, queueId, fileObj, errorObj){
    if ("Cancelled" == errorObj) {
        return;
    } else {
        alertMsg.error(errorObj);
    }
    /*alert("event:" + event + "\nqueueId:" + queueId + "\nfileObj.name:"
     + fileObj.name + "\nerrorObj.type:" + errorObj.type + "\nerrorObj.info:" + errorObj.info);*/
}


$.fn.extend({
	ajaxTodo:function(){
		return this.each(function(){
			var $this = $(this);
			$this.click(function(event){
				var url = unescape($this.attr("href")).replaceTmById($(event.target).parents(".unitBox:first"));
				DWZ.debug(url);
				if (!url.isFinishedTm()) {
					alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
					return false;
				}
				var title = $this.attr("title");
				if (title) {
					alertMsg.confirm(title, {
						okCall: function(){
							ajaxTodo(url, $this.attr("callback"));
						}
					});
				} else {
					ajaxTodo(url, $this.attr("callback"));
				}
				event.preventDefault();
			});
		});
	},
	dwzExport: function(){
		function _doExport($this) {
			var $p = $this.attr("targetType") == "dialog" ? $.pdialog.getCurrent() : navTab.getCurrentPanel();
			var $form = $("#pagerForm", $p);
			var url = $this.attr("href");
			window.location = url+(url.indexOf('?') == -1 ? "?" : "&")+$form.serialize();
		}
		
		return this.each(function(){
			var $this = $(this);
			$this.click(function(event){
				var title = $this.attr("title");
				if (title) {
					alertMsg.confirm(title, {
						okCall: function(){_doExport($this);}
					});
				} else {_doExport($this);}
			
				event.preventDefault();
			});
		});
	}
});

/**
 * 
 * @author ZhangHuihua@msn.com
 * @param {Object} opts Several options
 */
(function($){
	$.fn.extend({
		pagination: function(opts){
			var setting = {
				first$:"li.j-first", prev$:"li.j-prev", next$:"li.j-next", last$:"li.j-last", nums$:"li.j-num>a", jumpto$:"li.jumpto",
				pageNumFrag:'<li class="#liClass#"><a href="javascript:;">#pageNum#</a></li>'
			};
			return this.each(function(){
				var $this = $(this);
				var pc = new Pagination(opts);
				var interval = pc.getInterval();
	
				var pageNumFrag = '';
				for (var i=interval.start; i<interval.end;i++){
					pageNumFrag += setting.pageNumFrag.replaceAll("#pageNum#", i).replaceAll("#liClass#", i==pc.getCurrentPage() ? 'selected j-num' : 'j-num');
				}
				$this.html(DWZ.frag["pagination"].replaceAll("#pageNumFrag#", pageNumFrag).replaceAll("#currentPage#", pc.getCurrentPage())).find("li").hoverClass();
	
				var $first = $this.find(setting.first$);
				var $prev = $this.find(setting.prev$);
				var $next = $this.find(setting.next$);
				var $last = $this.find(setting.last$);
				
				if (pc.hasPrev()){
					$first.add($prev).find(">span").hide();
					_bindEvent($prev, pc.getCurrentPage()-1, pc.targetType(), pc.rel());
					_bindEvent($first, 1, pc.targetType(), pc.rel());
				} else {
					$first.add($prev).addClass("disabled").find(">a").hide();
				}
				
				if (pc.hasNext()) {
					$next.add($last).find(">span").hide();
					_bindEvent($next, pc.getCurrentPage()+1, pc.targetType(), pc.rel());
					_bindEvent($last, pc.numPages(), pc.targetType(), pc.rel());
				} else {
					$next.add($last).addClass("disabled").find(">a").hide();
				}
	
				$this.find(setting.nums$).each(function(i){
					_bindEvent($(this), i+interval.start, pc.targetType(), pc.rel());
				});
				$this.find(setting.jumpto$).each(function(){
					var $this = $(this);
					var $inputBox = $this.find(":text");
					var $button = $this.find(":button");
					$button.click(function(event){
						var pageNum = $inputBox.val();
						if (pageNum && pageNum.isPositiveInteger()) {
							dwzPageBreak({targetType:pc.targetType(), rel:pc.rel(), data: {pageNum:pageNum}});
						}
					});
					$inputBox.keyup(function(event){
						if (event.keyCode == DWZ.keyCode.ENTER) $button.click();
					});
				});
			});
			
			function _bindEvent($target, pageNum, targetType, rel){
				$target.bind("click", {pageNum:pageNum}, function(event){
					dwzPageBreak({targetType:targetType, rel:rel, data:{pageNum:event.data.pageNum}});
					event.preventDefault();
				});
			}
		},
		
		orderBy: function(options){
			var op = $.extend({ targetType:"navTab", rel:"", asc:"asc", desc:"desc"}, options);
			return this.each(function(){
				var $this = $(this).css({cursor:"pointer"}).click(function(){
					var orderField = $this.attr("orderField");
					var orderDirection = $this.hasClass(op.asc) ? op.desc : op.asc;
					dwzPageBreak({targetType:op.targetType, rel:op.rel, data:{orderField: orderField, orderDirection: orderDirection}});
				});
				
			});
		},
		pagerForm: function(options){
			var op = $.extend({pagerForm$:"#pagerForm", parentBox:document}, options);
			var frag = '<input type="hidden" name="#name#" value="#value#" />';
			return this.each(function(){
				var $searchForm = $(this), $pagerForm = $(op.pagerForm$, op.parentBox);
				var actionUrl = $pagerForm.attr("action").replaceAll("#rel#", $searchForm.attr("action"));
				$pagerForm.attr("action", actionUrl);
				$searchForm.find(":input").each(function(){
					var $input = $(this), name = $input.attr("name");
					if (name && (!$input.is(":checkbox,:radio") || $input.is(":checked"))){
						if ($pagerForm.find(":input[name='"+name+"']").length == 0) {
							var inputFrag = frag.replaceAll("#name#", name).replaceAll("#value#", $input.val());
							$pagerForm.append(inputFrag);
						}
					}
				});
			});
		}
	});
	
	var Pagination = function(opts) {
		this.opts = $.extend({
			targetType:"navTab",	// navTab, dialog
			rel:"", //用于局部刷新div id号
			totalCount:0,
			numPerPage:10,
			pageNumShown:10,
			currentPage:1,
			callback:function(){return false;}
		}, opts);
	}
	
	$.extend(Pagination.prototype, {
		targetType:function(){return this.opts.targetType},
		rel:function(){return this.opts.rel},
		numPages:function() {
			return Math.ceil(this.opts.totalCount/this.opts.numPerPage);
		},
		getInterval:function(){
			var ne_half = Math.ceil(this.opts.pageNumShown/2);
			var np = this.numPages();
			var upper_limit = np - this.opts.pageNumShown;
			var start = this.getCurrentPage() > ne_half ? Math.max( Math.min(this.getCurrentPage() - ne_half, upper_limit), 0 ) : 0;
			var end = this.getCurrentPage() > ne_half ? Math.min(this.getCurrentPage()+ne_half, np) : Math.min(this.opts.pageNumShown, np);
			return {start:start+1, end:end+1};
		},
		getCurrentPage:function(){
			var currentPage = parseInt(this.opts.currentPage);
			if (isNaN(currentPage)) return 1;
			return currentPage;
		},
		hasPrev:function(){
			return this.getCurrentPage() > 1;
		},
		hasNext:function(){
			return this.getCurrentPage() < this.numPages();
		}
	});
})(jQuery);
/**
 * reference dwz.util.date.js
 * @author ZhangHuihua@msn.com
 *
 */
(function ($) {
    $.setRegional("datepicker", {
        dayNames: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
        monthNames: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    });

    $.fn.datepicker = function (opts) {
        var setting = {
            box$: "#calendar",
            year$: "#calendar [name=year]", month$: "#calendar [name=month]",
            tmInputs$: "#calendar .time :text", hour$: "#calendar .time .hh", minute$: "#calendar .time .mm", second$: "#calendar .time .ss",
            tmBox$: "#calendar .tm", tmUp$: "#calendar .time .up", tmDown$: "#calendar .time .down",
            close$: "#calendar .close", calIcon$: "a.inputDateButton",
            main$: "#calendar .main", days$: "#calendar .days", dayNames$: "#calendar .dayNames",
            clearBut$: "#calendar .clearBut", okBut$: "#calendar .okBut"
        };

        function changeTmMenu(sltClass) {
            var $tm = $(setting.tmBox$);
            $tm.removeClass("hh").removeClass("mm").removeClass("ss");
            if (sltClass) {
                $tm.addClass(sltClass);
                $(setting.tmInputs$).removeClass("slt").filter("." + sltClass).addClass("slt");
            }
        }

        function clickTmMenu($input, type) {
            $(setting.tmBox$).find("." + type + " li").each(function () {
                var $li = $(this);
                $li.click(function () {
                    $input.val($li.text());
                });
            });
        }

        function keydownInt(e) {
            if (!((e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode == DWZ.keyCode.DELETE || e.keyCode == DWZ.keyCode.BACKSPACE))) {
                return false;
            }
        }

        function changeTm($input, type) {
            var ivalue = parseInt($input.val()), istart = parseInt($input.attr("start")) || 0, iend = parseInt($input.attr("end"));
            var istep = parseInt($input.attr('step') || 1);
            if (type == 1) {
                if (ivalue <= iend - istep) {
                    $input.val(ivalue + istep);
                }
            } else if (type == -1) {
                if (ivalue >= istart + istep) {
                    $input.val(ivalue - istep);
                }
            } else if (ivalue > iend) {
                $input.val(iend);
            } else if (ivalue < istart) {
                $input.val(istart);
            }
        }

        return this.each(function () {
            var $this = $(this);
            var dp = new Datepicker($this.val(), opts);

            function generateCalendar(dp) {
                var dw = dp.getDateWrap();
                var minDate = dp.getMinDate();
                var maxDate = dp.getMaxDate();

                var monthStart = new Date(dw.year, dw.month - 1, 1);
                var startDay = monthStart.getDay();
                var dayStr = "";
                if (startDay > 0) {
                    monthStart.setMonth(monthStart.getMonth() - 1);
                    var prevDateWrap = dp.getDateWrap(monthStart);
                    for (var t = prevDateWrap.days - startDay + 1; t <= prevDateWrap.days; t++) {
                        var _date = new Date(dw.year, dw.month - 2, t);
                        var _ctrClass = (_date >= minDate && _date <= maxDate) ? '' : 'disabled';
                        dayStr += '<dd class="other ' + _ctrClass + '" chMonth="-1" day="' + t + '">' + t + '</dd>';
                    }
                }
                for (var t = 1; t <= dw.days; t++) {
                    var _date = new Date(dw.year, dw.month - 1, t);
                    var _ctrClass = (_date >= minDate && _date <= maxDate) ? '' : 'disabled';
                    if (t == dw.day) {
                        dayStr += '<dd class="slt ' + _ctrClass + '" day="' + t + '">' + t + '</dd>';
                    } else {
                        dayStr += '<dd class="' + _ctrClass + '" day="' + t + '">' + t + '</dd>';
                    }
                }
                for (var t = 1; t <= 42 - startDay - dw.days; t++) {
                    var _date = new Date(dw.year, dw.month, t);
                    var _ctrClass = (_date >= minDate && _date <= maxDate) ? '' : 'disabled';
                    dayStr += '<dd class="other ' + _ctrClass + '" chMonth="1" day="' + t + '">' + t + '</dd>';
                }

                var $days = $(setting.days$).html(dayStr).find("dd");
                $days.not('.disabled').click(function () {
                    var $day = $(this);

                    if (!dp.hasTime()) {
                        var oldValue = $this.val();
                        $this.val(dp.formatDate(dp.changeDay($day.attr("day"), $day.attr("chMonth"))));
                        if (oldValue != $this.val()) {
                            $this.change();
                        }
                        closeCalendar();
                    } else {
                        $days.removeClass("slt");
                        $day.addClass("slt");
                    }
                });

                if (!dp.hasDate()) $(setting.main$).addClass('nodate'); // 仅时间，无日期

                if (dp.hasTime()) {
                    $("#calendar .time").show();

                    var $hour = $(setting.hour$).val(dw.hour).focus(function () {
                        changeTmMenu("hh");
                    });
                    var iMinute = parseInt(dw.minute / dp.opts.mmStep) * dp.opts.mmStep;
                    var $minute = $(setting.minute$).val(iMinute).attr('step', dp.opts.mmStep).focus(function () {
                        changeTmMenu("mm");
                    });
                    var $second = $(setting.second$).val(dp.hasSecond() ? dw.second : 0).attr('step', dp.opts.ssStep).focus(function () {
                        changeTmMenu("ss");
                    });

                    $hour.add($minute).add($second).click(function () {
                        return false
                    });

                    clickTmMenu($hour, "hh");
                    clickTmMenu($minute, "mm");
                    clickTmMenu($second, "ss");
                    $(setting.box$).click(function () {
                        changeTmMenu();
                    });

                    var $inputs = $(setting.tmInputs$);
                    $inputs.keydown(keydownInt).each(function () {
                        var $input = $(this);
                        $input.keyup(function () {
                            changeTm($input, 0);
                        });
                    });
                    $(setting.tmUp$).click(function () {
                        $inputs.filter(".slt").each(function () {
                            changeTm($(this), 1);
                        });
                    });
                    $(setting.tmDown$).click(function () {
                        $inputs.filter(".slt").each(function () {
                            changeTm($(this), -1);
                        });
                    });

                    if (!dp.hasHour()) $hour.attr("disabled", true);
                    if (!dp.hasMinute()) $minute.attr("disabled", true);
                    if (!dp.hasSecond()) $second.attr("disabled", true);
                }

            }

            function closeCalendar() {
                $(setting.box$).remove();
                $(document).unbind("click", closeCalendar);
            }

            function showCalendar(event) {
                closeCalendar();
                var dp = new Datepicker($this.val(), opts);
                var offset = $this.offset();
                var iTop = offset.top + this.offsetHeight;
                $(DWZ.frag['calendarFrag']).appendTo("body").css({
                    left: offset.left + 'px',
                    top: iTop + 'px'
                }).show().click(function (event) {
                    event.stopPropagation();
                });

                ($.fn.bgiframe && $(setting.box$).bgiframe());

                var dayNames = "";
                $.each($.regional.datepicker.dayNames, function (i, v) {
                    dayNames += "<dt>" + v + "</dt>"
                });
                $(setting.dayNames$).html(dayNames);

                var dw = dp.getDateWrap();
                var $year = $(setting.year$);
                var yearstart = dp.getMinDate().getFullYear();
                var yearend = dp.getMaxDate().getFullYear();
                for (y = yearstart; y <= yearend; y++) {
                    $year.append('<option value="' + y + '"' + (dw.year == y ? 'selected="selected"' : '') + '>' + y + '</option>');
                }
                var $month = $(setting.month$);
                $.each($.regional.datepicker.monthNames, function (i, v) {
                    var m = i + 1;
                    $month.append('<option value="' + m + '"' + (dw.month == m ? 'selected="selected"' : '') + '>' + v + '</option>');
                });

                // generate calendar
                generateCalendar(dp);
                $year.add($month).change(function () {
                    dp.changeDate($year.val(), $month.val());
                    generateCalendar(dp);
                });

                // fix top
                var iBoxH = $(setting.box$).outerHeight(true);
                if (iTop > iBoxH && iTop > $(window).height() - iBoxH) {
                    $(setting.box$).css("top", offset.top - iBoxH);
                }

                $(setting.close$).click(function () {
                    closeCalendar();
                });
                $(setting.clearBut$).click(function () {
                    $this.val("");
                    closeCalendar();
                });
                $(setting.okBut$).click(function () {
                    var $dd = $(setting.days$).find("dd.slt");

                    if ($dd.hasClass("disabled")) return false;

                    var date = dp.changeDay($dd.attr("day"), $dd.attr("chMonth"));

                    if (dp.hasTime()) {
                        date.setHours(parseInt($(setting.hour$).val()));
                        date.setMinutes(parseInt($(setting.minute$).val()));
                        date.setSeconds(parseInt($(setting.second$).val()));
                    }
                    var oldValue = $this.val();
                    $this.val(dp.formatDate(date));
                    if (oldValue != $this.val()) {
                        $this.change();
                    }
                    closeCalendar();
                });
                $(document).bind("click", closeCalendar);
                return false;

            }

            $this.click(showCalendar);
            $this.focus(showCalendar);

            $this.parent().find(setting.calIcon$).click(function () {
                $this.trigger("click");
                return false;
            });
        });

    }

    var Datepicker = function (sDate, opts) {
        this.opts = $.extend({
            pattern: 'yyyy-MM-dd',
            minDate: "1900-01-01",
            maxDate: "2099-12-31",
            mmStep: 1,
            ssStep: 1
        }, opts);

        //动态minDate、maxDate
        var now = new Date();
        this.opts.minDate = now.formatDateTm(this.opts.minDate);
        this.opts.maxDate = now.formatDateTm(this.opts.maxDate);

        this.sDate = sDate.trim();
    }

    $.extend(Datepicker.prototype, {
        get: function (name) {
            return this.opts[name];
        },
        _getDays: function (y, m) {//获取某年某月的天数
            return m == 2 ? (y % 4 || !(y % 100) && y % 400 ? 28 : 29) : (/4|6|9|11/.test(m) ? 30 : 31);
        },

        _minMaxDate: function (sDate) {
            var _count = sDate.split('-').length - 1;
            var _format = 'y-M-d';
            if (_count == 1) _format = 'y-M';
            else if (_count == 0) _format = 'y';

            return sDate.parseDate(_format);
        },
        getMinDate: function () {
            return this._minMaxDate(this.opts.minDate);
        },
        getMaxDate: function () {
            var _sDate = this.opts.maxDate;
            var _count = _sDate.split('-').length - 1;
            var _date = this._minMaxDate(_sDate);

            if (_count < 2) { //format:y-M、y
                var _day = this._getDays(_date.getFullYear(), _date.getMonth() + 1);
                _date.setDate(_day);
                if (_count == 0) {//format:y
                    _date.setMonth(11);
                }
            }

            return _date;
        },
        getDateWrap: function (date) { //得到年,月,日
            if (!date) date = this.parseDate(this.sDate) || new Date();
            var y = date.getFullYear();
            var m = date.getMonth() + 1;
            var days = this._getDays(y, m);
            return {
                year: y, month: m, day: date.getDate(),
                hour: date.getHours(), minute: date.getMinutes(), second: date.getSeconds(),
                days: days, date: date
            }
        },
        /**
         * @param {year:2010, month:05, day:24}
         */
        changeDate: function (y, m, d) {
            var date = new Date(y, m - 1, d || 1);
            this.sDate = this.formatDate(date);
            return date;
        },
        changeDay: function (day, chMonth) {
            if (!chMonth) chMonth = 0;
            var dw = this.getDateWrap();
            return this.changeDate(dw.year, dw.month + parseInt(chMonth), day);
        },
        parseDate: function (sDate) {
            if (!sDate) return null;
            return sDate.parseDate(this.opts.pattern);
        },
        formatDate: function (date) {
            return date.formatDate(this.opts.pattern);
        },
        hasHour: function () {
            return this.opts.pattern.indexOf("H") != -1;
        },
        hasMinute: function () {
            return this.opts.pattern.indexOf("m") != -1;
        },
        hasSecond: function () {
            return this.opts.pattern.indexOf("s") != -1;
        },
        hasTime: function () {
            return this.hasHour() || this.hasMinute() || this.hasSecond();
        },
        hasDate: function () {
            var _dateKeys = ['y', 'M', 'd', 'E'];
            for (var i = 0; i < _dateKeys.length; i++) {
                if (this.opts.pattern.indexOf(_dateKeys[i]) != -1) return true;
            }

            return false;
        }
    });
})(jQuery);
 /**
 * @author Roger Wu
 */
(function($){
	$.extend($.fn, {
		jBlindUp: function(options){
			var op = $.extend({duration: 500, easing: "swing", call: function(){}}, options);
			return this.each(function(){
				var $this = $(this);
				$(this).animate({height: 0}, {
					step: function(){},
					duration: op.duration,
					easing: op.easing,
					complete: function(){ 
						$this.css({display: "none"});
						op.call();
					}
				});
			});
		},
		jBlindDown: function(options){
			var op = $.extend({to:0, duration: 500,easing: "swing",call: function(){}}, options);
			return this.each(function(){
				var $this = $(this);
				var	fixedPanelHeight = (op.to > 0)?op.to:$.effect.getDimensions($this[0]).height;
				$this.animate({height: fixedPanelHeight}, {
					step: function(){},
					duration: op.duration,
					easing: op.easing,
					complete: function(){ 
					$this.css({display: ""});
					op.call(); }
				});
			});
		},
		jSlideUp:function(options) {
			var op = $.extend({to:0, duration: 500,easing: "swing",call: function(){}}, options);
			return this.each(function(){
				var $this = $(this);
				$this.wrapInner("<div></div>");
				var	fixedHeight = (op.to > 0)?op.to:$.effect.getDimensions($(">div",$this)[0]).height;
				$this.css({overflow:"visible",position:"relative"});
				$(">div",$this).css({position:"relative"}).animate({top: -fixedHeight}, {
					easing: op.easing,
					duration: op.duration,
					complete:function(){$this.html($(this).html());}
				});
				
			});
		},
		jSlideDown:function(options) {
			var op = $.extend({to:0, duration: 500,easing: "swing",call: function(){}}, options);
			return this.each(function(){
				var $this = $(this);
				var	fixedHeight = (op.to > 0)?op.to:$.effect.getDimensions($this[0]).height;
				$this.wrapInner("<div style=\"top:-" + fixedHeight + "px;\"></div>");
				$this.css({overflow:"visible",position:"relative", height:"0px"})
				.animate({height: fixedHeight}, {
					duration: op.duration,
					easing: op.easing,
					complete: function(){  $this.css({display: "", overflow:""}); op.call(); }
				});
				$(">div",$this).css({position:"relative"}).animate({top: 0}, {
					easing: op.easing,
					duration: op.duration,
					complete:function(){$this.html($(this).html());}
				});
			});
		}
	});
	$.effect = {
		getDimensions: function(element, displayElement){
			var dimensions = new $.effect.Rectangle;
			var displayOrig = $(element).css('display');
			var visibilityOrig = $(element).css('visibility');
			var isZero = $(element).height()==0?true:false;
			if ($(element).is(":hidden")) {
				$(element).css({visibility: 'hidden', display: 'block'});
				if(isZero)$(element).css("height","");
				if ($.browser.opera)
					refElement.focus();
			}
			dimensions.height = $(element).outerHeight();
			dimensions.width = $(element).outerWidth();
			if (displayOrig == 'none'){
				$(element).css({visibility: visibilityOrig, display: 'none'});
				if(isZero) if(isZero)$(element).css("height","0px");
			}
			return dimensions;
		}
	}
	$.effect.Rectangle = function(){
		this.width = 0;
		this.height = 0;
		this.unit = "px";
	}
})(jQuery);
/**
 * @author Roger Wu
 * @version 1.0
 */
(function ($) {
    $.extend($.fn, {
        jPanel: function (options) {
            var op = $.extend({header: "panelHeader", headerC: "panelHeaderContent", content: "panelContent", coll: "collapsable", exp: "expandable", footer: "panelFooter", footerC: "panelFooterContent"}, options);
            return this.each(function () {
                var $panel = $(this);
                var close = $panel.hasClass("close");
                var collapse = $panel.hasClass("collapse");

                var $content = $(">div[class!=" + op.header+"]:eq(0)", $panel).addClass(op.content);
                var title = $(">h1", $panel).wrap('<div class="' + op.header + '"><div class="' + op.headerC + '"></div></div>');
                if (collapse)$("<a href=\"\"></a>").addClass(close ? op.exp : op.coll).appendTo(title);

                var header = $(">div:first", $panel);
                var footer = $('<div class="' + op.footer + '"><div class="' + op.footerC + '"></div></div>').appendTo($panel);

                var defaultH = $panel.attr("defH") ? $panel.attr("defH") : 0;
                var minH = $panel.attr("minH") ? $panel.attr("minH") : 0;
                if (defaultH > 0)
                    $content.height(defaultH + "px");
                else if (minH > 0) {
                    $content.css("minHeight", minH + "px");
                }
                if (close) {
                    $content.css({
                        display: "none"
                    });
                }

                if (!collapse) return;
                var $pucker = $("a", header);
                var inH = $content.innerHeight();
                if (minH > 0 && minH >= inH) defaultH = minH;
                else defaultH = inH;
                $pucker.click(function () {
                    if ($pucker.hasClass(op.exp)) {
                        $content.jBlindDown({to: defaultH, call: function () {
                            $pucker.removeClass(op.exp).addClass(op.coll);
                            //if (minH > 0) $content.css("minHeight", minH + "px");
                            if (minH >= inH) $content.css("height", minH + "300px");
                        }});
                    } else {
                        if (minH > 0) $content.css("minHeight", "");
                        if (minH >= inH) $content.css("height", minH + "px");
                        $content.jBlindUp({call: function () {
                            $pucker.removeClass(op.coll).addClass(op.exp);
                        }});
                    }
                    return false;
                });
            });
        }
    });
})(jQuery);     
/**
 * @author ZhangHuihua@msn.com
 * 
 */
(function($){
	$.printBox = function(rel){
		var _printBoxId = 'printBox';
		var $contentBox = rel ? $('#'+rel) : $("body"),
			$printBox = $('#'+_printBoxId);
			
		if ($printBox.size()==0){
			$printBox = $('<div id="'+_printBoxId+'"></div>').appendTo("body");
		}

		$printBox.html($contentBox.html()).find("[layoutH]").height("auto");
		window.print();

	}

})(jQuery);
/**
 * @author ZhangHuihua@msn.com
 */
(function ($) {
    // jQuery validate
    $.extend($.validator.messages, {
        required: "必填字段",
        remote: "请修正该字段",
        email: "请输入正确格式的电子邮件",
        url: "请输入合法的网址",
        date: "请输入合法的日期",
        dateISO: "请输入合法的日期 (ISO).",
        number: "请输入合法的数字",
        digits: "只能输入整数",
        creditcard: "请输入合法的信用卡号",
        equalTo: "请再次输入相同的值",
        accept: "请输入拥有合法后缀名的字符串",
        maxlength: $.validator.format("长度最多是 {0} 的字符串"),
        minlength: $.validator.format("长度最少是 {0} 的字符串"),
        rangelength: $.validator.format("长度介于 {0} 和 {1} 之间的字符串"),
        range: $.validator.format("请输入一个介于 {0} 和 {1} 之间的值"),
        max: $.validator.format("请输入一个最大为 {0} 的值"),
        min: $.validator.format("请输入一个最小为 {0} 的值"),
        hssize: "请保留两位小数",

        alphanumeric: "字母、数字、下划线",
        lettersonly: "必须是字母",
        phone: "数字、空格、括号"
    });

    // DWZ regional
    $.setRegional("datepicker", {
        dayNames: ['日', '一', '二', '三', '四', '五', '六'],
        monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
    });
    $.setRegional("alertMsg", {
        title: {error: "错误", info: "提示", warn: "警告", correct: "成功", confirm: "确认提示"},
        butMsg: {ok: "确定", yes: "是", no: "否", cancel: "取消"}
    });
})(jQuery);