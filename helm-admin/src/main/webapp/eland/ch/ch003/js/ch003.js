/**
 * Created by xh on 2016/2/23.
 */
navTab = {
    getCurrentPanel: function () {
        return $(document.body);
    }
};

(function ($) {
    $.fn.extend({
        initUI: function () {
        }
    });
})
var ch003cx = {
    ch003Countdown: null,
    ch003CountTime: null,
    adjustHeight: function () {
        var containHead = $("div.messageLogging").height();
        $("div.js_page", navTab.getCurrentPanel()).css("height", ($(window).height() - containHead) + "px");
    },
    /**
     * 切换展示视图
     */
    changeShowModel: function (obj, model, hsId) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_model");
        var currentModel = currentModelObj.val();
        if (currentModel == model) {
            return;
        }
        // 修改模式
        currentModelObj.val(model);
        $this.addClass("active");
        $this.siblings("span").removeClass("active");
        // 执行查询
        ch003cx.query(hsId, model);
    },

    /**
     * 选择人员
     * @param trObj
     */
    choosePerson: function (hsId, trObj) {
        var $tr = $(trObj);
        var hsJush = $tr.find("input[name=hsJush]").val();
        $("input[name=ctrlHsJush]").val(hsJush);
        $tr.siblings("tr").removeClass("selected");
        $tr.addClass("selected");
        ch003cx.query(hsId);
    },
    /**
     * 选房列表
     */
    query: function (hsId) {
        // 有错误信息，直接返回
        var errorMsg = $("input[name=errorMsg]");
        if (errorMsg.length == 1 && errorMsg.val() != "") {
            return;
        }

        var formObj = $('#ch001001frm');
        if (formObj.length < 1) {
            return
        }
        var ctType = $("input[name=ctType]", formObj).val();
        var ch00101QryDiv = $('#ch00101QryDiv');
        var hsTP = $("input[name=hsTp]").val();
        var minSize = $("input[name=minSize]").val();
        var maxSize = $("input[name=maxSize]").val();
        var chooseHsAddr = $("input[name=chooseHsAddr]").val();
        var hsHxName = $("input[name=hsHxName]").val();
        var selectedHs = $("input[name=selectedHs]").val();
        var hsJush = $("input[name=ctrlHsJush]").val();
        var ctrlFlag = $("input[name=ctrlFlag]").val();
        //选房控制
        var hsClass;
        if (ctType == "1") {
            // 回迁
            hsClass = "2";
        } else if (ctType == "2") {
            // 外迁
            hsClass = "3";
        } else {
            // 其他方式
            hsClass = "0"
        }
        // 展示模式
        var flag = $("input.js_query_model").val();
        var conditionNames = new Array();
        var conditions = new Array();
        var conditionValues = new Array();
        // 房源状态
        if (flag == "1") {
            // 未选择
            conditionNames.push("NewHsInfo.statusCd");
            conditions.push("=");
            conditionValues.push("2");
        } else {
            // 已选和未选
            conditionNames.push("NewHsInfo.statusCd");
            conditions.push("in");
            conditionValues.push("2|3");
        }
        // 房屋居室
        conditionNames.push("NewHsInfo.hsTp");
        conditions.push("=");
        conditionValues.push(hsTP);
        // 建筑面积
        conditionNames.push("NewHsInfo.preBldSize");
        conditions.push(">=");
        conditionValues.push(minSize);
        conditionNames.push("NewHsInfo.preBldSize");
        conditions.push("<=");
        conditionValues.push(maxSize);
        // 房源类型
        conditionNames.push("NewHsInfo.hsClass");
        conditions.push("=");
        conditionValues.push(hsClass);
        // 房源地址
        conditionNames.push("NewHsInfo.hsAddr");
        conditions.push("like");
        conditionValues.push(chooseHsAddr);
        // 户型名称
        conditionNames.push("NewHsInfo.hsHxName");
        conditions.push("like");
        conditionValues.push(hsHxName);
        // 房源编号
        conditionNames.push("HouseInfo.hsId");
        conditions.push("=");
        conditionValues.push(hsId);
        // 居室查询
        conditionNames.push("NewHsInfo.hsJush");
        conditions.push("=");
        conditionValues.push(hsJush);
        // 选房控制
        conditionNames.push("flag");
        conditions.push("=");
        conditionValues.push(ctrlFlag);
        // 设置检索条件
        $("input[name=conditionName]", formObj).val(conditionNames.join(","));
        $("input[name=condition]", formObj).val(conditions.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValues.join(","));
        $("input[name=regUseType]", formObj).val(hsClass);
        $("input[name=rhtType]", formObj).val(hsClass);
        // 清空查询结果
        $(formObj).next("div.js_page").empty();
        // 执行查询
        if (flag == '2') {
            var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-initB.gv";
            var packet = new AJAXPacket(url);
            // 其他查询条件
            packet.data.data = formObj.serializeArray();
            packet.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(packet, function (response) {
                var resultDiv = $(formObj).next("div.js_page");
                resultDiv.html(response);
                ch003cx.adjustHeight();
            }, true);
            packet = null;
        } else {
            ch003cx.adjustHeight();
            Page.query(formObj, "");
        }
    },

    /**
     * 获取可用户型
     * @param obj
     * @return
     */
    getCfgDataOpt: function (obj) {
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
            sortResults: false,
            filterResults: false,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var $td = source.parent("span");
                $("input[type=hidden]", $td).val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var $td = source.parent("span");
                source.val("");
                $("input[type=hidden]", $td).val("");
            },
            fetchData: function (obj) {
                var resultData = {};
                var attrRelItemCd = $(obj).attr("itemCd");
                if (attrRelItemCd && attrRelItemCd != "") {
                    var url = getGlobalPathRoot() + "eland/ch/ch003/ch003-data.gv";
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

    /** * 翻页 ** */
    jumpPage: function (currentPage, _divId) {
        if (_divId == "") {
            return;
        }
        var divObj = $("#" + _divId);
        var $pObj = divObj;
        $pObj.find("input[name='currentPage']").val(currentPage);
        // 翻页跳转
        var jsonData = $.parseJSON($pObj.find("input[name='queryCondition']").val());
        jsonData.currentPage = $pObj.find("input[name='currentPage']").val();
        jsonData.sortColumn = $pObj.find("input[name='sortColumn']").val();
        jsonData.sortOrder = $pObj.find("input[name='sortOrder']").val();
        jsonData.pageSize = $pObj.find("input[name='pageSize']").val();
        ch003cx.pubQuery(jsonData, _divId, null);
    },

    /** * 页面跳转 ** */
    skipTo: function (_divId) {
        var divObj = $("#" + _divId);
        var $pObj = divObj;
        var pageNoValue = $pObj.find("input[name=skipToNo]").val();
        if (!isNaN(pageNoValue)) {
            var pageNo = parseInt(pageNoValue);
            if (pageNo > 0 && pageNo <= parseInt($pObj.find("input[name='totalPage']").val())) {
                ch003cx.jumpPage(pageNo, _divId);
            } else {
                $pObj.find("input[name='skipToNo']").attr("value", '');
            }
        } else {
            $pObj.find("input[name='skipToNo']").attr("value", '');
        }
    },

    /** * 排序 ** */
    sort: function (_divId, obj) {
        var divObj = $("#" + _divId);
        var $pObj = divObj.parent();
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
            ch003cx.jumpPage(1, _divId);
        }
    },
    /**
     * 公共查询界面
     */
    pubQuery: function (jsonData, _divId, successEvalJs) {
        var divObj = $("#" + _divId);
        var $pObj = divObj;
        var url = getGlobalPathRoot() + "oframe/common/page/page-data.gv";
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
            var toContain = divObj;
            var errMsg = $("input[name=errMsg]", r).val();
            if (errMsg != '') {
                // 查询失败l
                alertMsg.error(errMsg);
            } else {
                // 执行成功
                toContain.html(r);
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
     * 启动倒计时
     */
    countDown: function () {
        if (ch003cx.ch003Countdown) {
            clearTimeout(ch003cx.ch003Countdown);
        }
        // 非选房状态不显示倒计时
        var spaceTimeObj = $("input[name=spaceTime]", navTab.getCurrentPanel());
        if (spaceTimeObj.length == 0) {
            return;
        }

        var spaceTime = $("input[name=spaceTime]", navTab.getCurrentPanel()).val();
        if (spaceTime && spaceTime != "" && spaceTime != "0") {
            ch003cx.ch003CountTime = Number(spaceTime) * 60;
            ch003cx.count();
        }
    },

    /**
     * 倒计时处理
     */
    count: function () {
        if (ch003cx.ch003CountTime > 0) {
            var minite = Math.floor(ch003cx.ch003CountTime / 60) % 60;
            var second = Math.floor(ch003cx.ch003CountTime % 60);
            if (minite == 0) {
                $(".countTime").html(second + "秒");
            } else {
                $(".countTime").html(minite + "分" + second + "秒");
            }
            if (minite < 1) {
                $(".countTime").css("font-size", "60px");
            } else {
                $(".countTime").css("font-size", "50px");
            }
            ch003cx.ch003CountTime--;
        } else {
            clearTimeout(ch003cx.ch003Countdown);

            window.location.reload($("#return").attr("href"));
        }
        ch003cx.ch003Countdown = setTimeout("ch003cx.count()", 1000);
    }
};



