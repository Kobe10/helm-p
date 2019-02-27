// 定义全局divID变量
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