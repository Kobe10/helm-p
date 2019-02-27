var ch001 = {
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },
    /**
     * 打开快速设置对话框
     */
    toggleEdit: function (obj) {
        var $thisSpan = $(obj);
        var target = $("#ch001create", navTab.getCurrentPanel());
        if (target.hasClass("active")) {
            target.hide().removeClass("active");
            ch001.saveCookie();
            $thisSpan.html("设置");
            // 保存设置开关
            var setValue = $(":radio[checked][name=countSwitch]").val();
            $("#countSwitchFlag", navTab.getCurrentPanel()).val(setValue);
            // 刷新数据
            ch001.schHs(false);
        } else {
            target.show().addClass("active");
            $thisSpan.html("保存");
        }
    },

    /**
     * 保存设置cookie
     */
    saveCookie: function () {
        var target = $("#ch001create", navTab.getCurrentPanel());
        var ctTypes = [];
        target.find(":checkbox[checked][name=ctType]").each(function () {
                ctTypes.push($(this).val());
            }
        );
        setPCookie("ctType", ctTypes.toString(), "/");
        var chStatus = [];
        target.find(":checkbox[checked][name=chooseStatus]").each(function () {
                chStatus.push($(this).val());
            }
        );
        setPCookie("chooseStatus", chStatus.toString(), "/");
        var isGiveUpHs = [];
        target.find(":checkbox[checked][name=isGiveUpHs]").each(function () {
                isGiveUpHs.push($(this).val());
            }
        );
        setPCookie("isGiveUpHs", isGiveUpHs.toString(), "/");
        var countSwitch = target.find(":radio[checked][name=countSwitch]").val();
        setPCookie("countSwitch", countSwitch, "/");
        var kxMinSize = target.find("input[name=kxMinSize]").val();
        setPCookie("kxMinSize", kxMinSize.toString(), "/");
        var kxMaxSize = target.find("input[name=kxMaxSize]").val();
        setPCookie("kxMaxSize", kxMaxSize.toString(), "/");
    },

    // 根据选房个数调整table大小
    adjustHeight: function () {
        var containHead = $("#ch001Head", navTab.getCurrentPanel()).closest("div.unitBox").height();
        var ch001Head = $("#ch001Head", navTab.getCurrentPanel()).closest("div").height();
        var tabsHeader = $("div.tabsHeader", navTab.getCurrentPanel()).height();
        if (!tabsHeader) {
            tabsHeader = 0;
        }
        var listHeight = containHead - ch001Head - tabsHeader - 172;
        if (tabsHeader > 0) {
            listHeight = listHeight - 10;
        }
        // 报表区域高度
        var ch001_ReportData = $("#ch001_ReportData", navTab.getCurrentPanel());
        ch001_ReportData.css("height", listHeight + 110 + "px");
        // 图像高度
        $("div.album-list", ch001_ReportData).css("height", listHeight + 68 + "px");
        // 列表数据高度
        $("#ch001001_list_print", navTab.getCurrentPanel()).find("div.gridScroller").css("height", listHeight + "px");
        $("#statusShow", navTab.getCurrentPanel()).css("top", ch001Head + tabsHeader + 210 + "px");
        $("#ch001001div div.js_page", navTab.getCurrentPanel()).css("height", listHeight + 75 + "px");
    },

    /**
     * 自动补齐
     * @param obj
     * @returns {string}
     */
    getUrl: function () {
        var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
        return getGlobalPathRoot() + "eland/ch/ch001/ch001-queryPer.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
    },
    getOption: function () {
        return {
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            sortResults: false,
            filterResults: false,
            onError: function (var1, var2, var3) {
            },
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input[name=buyPersonId]").val(data.personId);
                tr.find("input[name=buyFamilyPersonRel]").val(data.familyPersonRel);
                tr.find("input[name=buyPersonCertyNum]").val(data.personCertyNum);
                tr.find("input[name=buyPersonTelphone]").val(data.personTelphone);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input[name=buyPersonId]").val("");
                tr.find("input[name=buyFamilyPersonRel]").val("");
                tr.find("input[name=buyPersonCertyNum]").val("");
                tr.find("input[name=buyPersonTelphone]").val("");
            }
        }
    },
    ///**
    // * 居室查询
    // * @param obj
    // * @param hsTp
    // */
    //queryJuShi: function (obj, hsTp) {
    //    $(".btn_click").css({
    //        'border': '1px solid #000000',
    //        'color': 'black',
    //        'background-color': '#FDFDFD'
    //    });
    //    $(obj).css({
    //        'border': '1px solid #61BBEC',
    //        'color': '#ffffff',
    //        'background-color': '#61BBEC'
    //    });
    //    $("input[name=hsTp]").val(hsTp);
    //    ch001.query();
    //},
    /**
     * 切换展示视图
     */
    changeShowModel: function (obj, model, hsId) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        if (currentModel == model) {
            return;
        }
        // 修改模式
        currentModelObj.val(model);
        $this.find(">a>span").addClass("active");
        $this.siblings("li").find(">a>span").removeClass("active");
        // 执行查询
        ch001.query(hsId);
    },

    /**
     * 选房列表
     */
    query: function (hsId) {
        var formObj = $('#ch001001frm', navTab.getCurrentPanel());
        if (formObj.length < 1) {
            return
        }
        var chsType = $("input[name=chsType]", navTab.getCurrentPanel()).val();
        var hsClass = $("input[name=hsClass]", navTab.getCurrentPanel()).val();
        var ch00101QryDiv = $('#ch00101QryDiv', navTab.getCurrentPanel());
        var hsTP = $("input[name=hsTp]", ch00101QryDiv).val();
        var minSize = $("input[name=minSize]", ch00101QryDiv).val();
        var maxSize = $("input[name=maxSize]", ch00101QryDiv).val();
        var chooseHsAddr = $("input[name=chooseHsAddr]", ch00101QryDiv).val();
        var hsJush = $("input[name=huJushFlag]", ch00101QryDiv).val();
        var chooseHsRegId = $("input[name=chooseHsRegIdFlag]", ch00101QryDiv).val();
        var hsHxName = $("input[name=hsHxName]", ch00101QryDiv).val();
        var selectedHs = $("input[name=selectedHs]", navTab.getCurrentPanel()).val();
        // 展示模式
        var flag = $("input.js_query_model", navTab.getCurrentPanel()).val();
        var conditionNames = [];
        var conditions = [];
        var conditionValues = [];
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
        // 房屋居室类型  一居  二居
        conditionNames.push("NewHsInfo.hsJush");
        conditions.push("=");
        conditionValues.push(hsJush);
        // 房屋居室  一居室一厅一卫
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
        // 房源区域
        conditionNames.push("NewHsInfo.ttRegId");
        conditions.push("=");
        conditionValues.push(chooseHsRegId);
        // 是否控制选房
        if(chsType != "1") {
            conditionNames.push("flag");
            conditions.push("=");
            conditionValues.push("false");
        }

        // 设置检索条件
        $("input[name=conditionName]", formObj).val(conditionNames.join(","));
        $("input[name=condition]", formObj).val(conditions.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValues.join(","));
        $("input[name=regUseType]", formObj).val(hsClass);
        $("input[name=rhtType]", formObj).val(hsClass);
        var fullScreen = $("input[name=fullScreen]", navTab.getCurrentPanel()).val();
        // 执行查询
        if (flag == '2' || flag == '3') {
            var displayBuildPage = '';
            if (flag == '3') {
                displayBuildPage = '/eland/ch/ch001/ch00104_build';
            } else {
                displayBuildPage = '/eland/ch/ch001/ch00101_build';
            }
            var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-initB.gv";
            var packet = new AJAXPacket(url);
            // 其他查询条件
            packet.data.data = formObj.serializeArray();
            packet.data.data.push({name: "prjCd", value: getPrjCd()});
            packet.data.data.push({name: "chsType", value: chsType});
            if (flag == '3')
                packet.data.data.push({name: "displayBuildPage", value: displayBuildPage});
            core.ajax.sendPacketHtml(packet, function (response) {
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
                ch001.adjustHeight();
                if (fullScreen == 'true') {
                    ch001.fullScreen();
                }
            }, true);
            packet = null;
        } else {
            if (fullScreen == 'true') {
                Page.query(formObj, "ch001.fullScreen();");
            } else {
                Page.query(formObj, "ch001.adjustHeight();");
            }
        }
    },

    /**
     * 取消选房
     */
    quXiao: function () {
        var hsCtId = $("input[name=hsCtId]", navTab.getCurrentPanel()).val();
        alertMsg.confirm("您确定取消所有已选房?", {
            okCall: function () {
                var hsCtChooseId = "";
                var yiXFang = $(".js_yiXFang", navTab.getCurrentPanel());
                var count = yiXFang.length;
                for (var i = 0; i < count; i++) {
                    hsCtChooseId = hsCtChooseId + $("input[name=hsCtChooseId]", yiXFang.eq(i)).val() + ",";
                }
                // 重新加载
                var packet = new AJAXPacket();
                var prjCd = getPrjCd();
                packet.data.add("prjCd", prjCd);
                packet.data.add("hsCtChooseId", hsCtChooseId);
                packet.data.add("hsCtId", hsCtId);
                packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-quXiao.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        alertMsg.correct("取消选房成功");
                        var hsId = $("input[name=hidHsId]").val();
                        ch001.chInfo(hsId);
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 取消选房
     */
    quXiaoChoose: function () {
        var hsCtId = $("input[name=hsCtId]").val();
        alertMsg.confirm("您确定取消所有已选房?", {
            okCall: function () {
                var hsCtChooseId = "";
                var yiXFang = $(".js_yiXFang", navTab.getCurrentPanel());
                var count = yiXFang.length;
                for (var i = 0; i < count; i++) {
                    hsCtChooseId = hsCtChooseId + $("input[name=hsCtChooseId]", yiXFang.eq(i)).val() + ",";
                }
                // 重新加载
                var packet = new AJAXPacket();
                var prjCd = getPrjCd();
                packet.data.add("prjCd", prjCd);
                packet.data.add("hsCtChooseId", hsCtChooseId);
                packet.data.add("hsCtId", hsCtId);
                packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-quXiaoChoose.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        alertMsg.correct("取消选房成功");
                        var hsId = $("input[name=hidHsId]").val();
                        ch001.chInfo(hsId);
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 选择人查询相应的可选房源信息
     * @param obj
     */
    choosePerson: function (obj) {
        var $thisTr = $(obj).closest("tr");
        var chooseHsRegId = $("input[name=chooseHsRegId]", $thisTr).val();
        var huJush = $("input[name=huJush]", $thisTr).val();
        var ch00101QryDiv = $('#ch00101QryDiv', navTab.getCurrentPanel());
        $("input[name=huJushFlag]", ch00101QryDiv).val(huJush);
        if (chooseHsRegId != '0') {
            $("input[name=chooseHsRegIdFlag]", ch00101QryDiv).val(chooseHsRegId);
        }
        ch001.query('');
    },

    /**
     * 选择人查询相应的可选房源信息
     * @param obj
     */
    choosePersonBySize: function (obj) {
        var $thisTr = $(obj).closest("tr");
        var preBldSize = $("input[name=preBldSize]", $thisTr).val();
        var ch00101QryDiv = $('#ch00101QryDiv', navTab.getCurrentPanel());
        $("input[name=minSize]", ch00101QryDiv).val(preBldSize);
        $("input[name=maxSize]", ch00101QryDiv).val(preBldSize);
        ch001.query('');
    },

    /**
     * 编辑购房人信息
     * @param obj 编辑购房人信息
     */
    editRow: function (obj) {
        var $this = $(obj);
        var thisTr = $this.closest("tr");
        var inEdit = $this.attr("inEdit");
        if (inEdit == "true") {
            $("input.js_editable", thisTr).addClass("readonly").attr("readonly", "readonly");
            $this.attr("inEdit", "false");
        } else {
            $("input.js_editable", thisTr).removeClass("readonly").removeAttr("readonly");
            $this.attr("inEdit", "true");
        }

    },

    /**
     * 取消选房
     */
    cancelChoose: function (obj) {
        stopEvent(event);
        var thisTr = $(obj).closest("tr");
        var newHsId = $("input[name=newHsId]", thisTr).val();
        if (newHsId == "") {
            return;
        }
        //删除隐藏的input中的值
        var selectedHs = $("input[name=selectedHs]", navTab.getCurrentPanel()).val().split(",");
        for (var i = 0; i < selectedHs.length; i++) {
            if (selectedHs[i] == newHsId) {
                selectedHs.splice(i, 1);
                break;
            }
        }
        // 获取选中的房源信息
        $("input[name=selectedHs]", navTab.getCurrentPanel()).val(selectedHs.join(","));
        $("span[name=chooseStatusText]", thisTr).text("未选房");
        $("td[name=tempAddr]", thisTr).text("");
        $("input[name=chooseStatus]", thisTr).val("1");
        $("input[name=chooseHsAddr]", thisTr).val("");
        $("input[name=newHsId]", thisTr).val("");
        $("input[name=oldNewHsId]", thisTr).val("");
        $("input[name=chooseHsRegId]", thisTr).val("");
        $("input[name=chooseHsRegIdFlag]", navTab.getCurrentPanel()).val("");
        // 重新查询房源数据
        ch001.query($("input[name=hidHsId]", navTab.getCurrentPanel()).val());
    },

    /**
     * 删除选房
     */
    deleteRow: function (obj) {
        var $this = $(obj).parent().parent();
        var thisTr = $(obj).closest("tr");
        var newHsId = $("input[name=newHsId]", thisTr).val();
        //删除隐藏的input中的值
        var selectedHs = $("input[name=selectedHs]", navTab.getCurrentPanel()).val().split(",");
        for (var i = 0; i < selectedHs.length; i++) {
            if (selectedHs[i] == newHsId) {
                selectedHs.splice(i, 1);
                break;
            }
        }
        // 获取选中的房源信息
        $("input[name=selectedHs]", navTab.getCurrentPanel()).val(selectedHs.join(","));
        // 删出选中的房后，把列表中相应的房  改成未选
        $("input[name=newHsId]", $("#ch001001_list_print", navTab.getCurrentPanel())).each(function () {
            if ($(this).val() == newHsId) {
                var td = $(this).parent().parent();
                $("span[name=chooseFlag]", td).text("[未选]");
            }
        });
        if ($this.closest("table").find("tr").length == 2) {
            $("input.js_hs_info", $this).val("");
            $("#hsAddrId", $this).html("");
        } else {
            $this.remove();
            ch001.adjustHeight();
        }
        // 重新查询房源数据
        ch001.query($("input[name=hidHsId]", navTab.getCurrentPanel()).val());
    },

    /**
     * 外迁选房操作
     */
    chooseDetail: function (hsId, fromOp, method) {
        var url = getGlobalPathRoot() + "eland/oh/oh002/oh002-outChooseHs.gv?hsId=" + hsId + "&prjCd=" + getPrjCd() + "&fromOp=" + fromOp + "&method=" + method;
        $.pdialog.open(url, "oh002", '选房详情', {max: true, mask: true});
    },

    /**
     * 确认选房
     * @param obj
     */
    cfmHsChoose: function (obj) {
        var $this = $(obj);
        var thisTd = $this.closest("td");
        var thisTr = thisTd.parent();
        var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-cfmHsChoose.gv";

        var chooseHsAddr = thisTd.find("input[name=chooseHsAddr]").val();
        var newHsId = thisTd.find("input[name=newHsId]").val();
        var oldNewHsId = thisTd.find("input[name=oldNewHsId]").val();
        var hsCtId = $("input[name=hsCtId]", navTab.getCurrentPanel()).val();
        var hsCtChooseId = thisTd.find("input[name=hsCtChooseId]").val();
        var personNoticeAddr = thisTr.find("input[name=personNoticeAddr]").val();
        var buyPersonTel = thisTr.find("input[name=buyPersonTel]").val();
        var packet = new AJAXPacket(url);
        packet.data.add("newHsId", newHsId);
        packet.data.add("oldNewHsId", oldNewHsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("hsCtChooseId", hsCtChooseId);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("personNoticeAddr", personNoticeAddr);
        packet.data.add("buyPersonTel", buyPersonTel);
        if (chooseHsAddr) {
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("执行成功！");
                    //修改隐藏项的值
                    $("span[name=chooseStatusText]", thisTr).text("已选房");
                    thisTd.find("input[name=chooseStatus]").val('2');
                    thisTd.find("input[name=oldNewHsId]").val(newHsId);
                    $("span[name=editeBtn]", thisTd).hide();
                    $("span[name=saveBtn]", thisTd).hide();
                    $("span.cfmHidFlagOther", thisTd).show();
                    $("span.cfmHidFlag", thisTd).hide();
                    $("span[name=drawConfirm1]", thisTd).show();
                    $("span[name=drawConfirm2]", thisTd).hide();
                    $("input[name=selectedHs]", navTab.getCurrentPanel()).val("");
                    //刷新房源
                    ch001.query('');
                    ch001.createConfirm(obj);
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            });
        } else {
            alertMsg.warn("请选房再提交！");
        }
    },

    /**
     * 确认选房
     * @param obj
     */
    cfmHsChooseAll: function () {
        var $form = $("#ch00101ChsForm", navTab.getCurrentPanel());
        var formData = $form.serializeArray();
        var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-cfmHsChoose.gv";
        var packet = new AJAXPacket(url);
        formData.push({name: "prjCd", value: getPrjCd()});
        packet.data.data = formData;
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("执行成功！");
                // 重新查询房源数据
                ch001.chInfo($("input[name=hidHsId]", navTab.getCurrentPanel()).val());
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 取消选房
     * @param obj
     */
    cancelHsChoose: function (obj) {
        var $this = $(obj);
        var thisTd = $this.closest("td");
        var thisTr = thisTd.parent();
        var hsCtChooseId = thisTd.find("input[name=hsCtChooseId]").val();
        var hsCtId = $("input[name=hsCtId]", navTab.getCurrentPanel()).val();
        if (hsCtChooseId) {
            alertMsg.confirm("确认取消选房吗？", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-cancelHsChoose.gv";
                    var packet = new AJAXPacket(url);
                    packet.data.add("hsCtChooseId", hsCtChooseId);
                    packet.data.add("hsCtId", hsCtId);
                    packet.data.add("prjCd", getPrjCd());
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("取消成功！");
                            //清空数据
                            $("span[name=chooseStatusText]", thisTr).text("未选房");
                            $("td[name=tempAddr]", thisTr).text("");
                            $("input[name=chooseStatus]", thisTd).val("1");
                            $("input[name=chooseHsAddr]", thisTd).val("");
                            $("input[name=newHsId]", thisTd).val("");
                            $("input[name=oldNewHsId]", thisTd).val("");
                            $("input[name=chooseHsRegId]", thisTd).val("");
                            $("input[name=chooseHsRegIdFlag]", navTab.getCurrentPanel()).val("");
                            //调整按钮
                            $("span[name=editeBtn]", thisTd).show();
                            $("span.cfmHidFlagOther", thisTd).hide();
                            $("span.cfmHidFlag", thisTd).show();
                            $("span[name=drawConfirm1]", thisTd).hide();
                            $("span[name=drawConfirm2]", thisTd).hide();
                            //刷新房源
                            ch001.query('');
                        } else {
                            alertMsg.warn(jsonData.errMsg);
                        }
                    });
                }
            });
        }
    },

    /**
     * 删除选房
     * @param obj
     */
    deleteHsChoose: function (obj) {
        var $this = $(obj);
        var thisTd = $this.closest("td");
        var hsCtChooseId = thisTd.find("input[name=hsCtChooseId]").val();
        var hsCtId = $("input[name=hsCtId]", navTab.getCurrentPanel()).val();
        if (hsCtChooseId) {
            alertMsg.confirm("确认删除选房吗？", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-deleteHsChoose.gv";
                    var packet = new AJAXPacket(url);
                    packet.data.add("hsCtChooseId", hsCtChooseId);
                    packet.data.add("hsCtId", hsCtId);
                    packet.data.add("prjCd", getPrjCd());
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("执行成功！");
                            // 重新查询房源数据
                            ch001.chInfo($("input[name=hidHsId]", navTab.getCurrentPanel()).val());
                        } else {
                            alertMsg.warn(jsonData.errMsg);
                        }
                    });
                }
            });
        } else {

        }
    },

    /**
     * 生成选房确认单
     */
    createConfirm: function (obj) {
        var thisTd = $(obj).closest("td");
        var newHsId = $("input[name=newHsId]", thisTd).val();
        var hsCtId = $("input[name=hsCtId]", navTab.getCurrentPanel()).val();
        var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
        var hsCtChooseId = $("input[name=hsCtChooseId]", thisTd).val();
        var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-createConfirm.gv?fromOp=view"
            + "&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&newHsId=" + newHsId
            + "&prjCd=" + getPrjCd()
            + "&hsCtChooseId=" + hsCtChooseId;
        $.pdialog.open(url, "viewQrd", "确认单", {
            mask: true, max: true, resizable: false, close: function () {
                alertMsg.confirm("确认文档已打印？", {
                    okCall: function () {
                        // 重新加载
                        var packet = new AJAXPacket();
                        var prjCd = getPrjCd();
                        packet.data.add("prjCd", prjCd);
                        packet.data.add("hsId", hsId);
                        packet.data.add("hsCtChooseIds", hsCtChooseId);
                        packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-doPrint.gv";
                        core.ajax.sendPacketHtml(packet, function (response) {
                            var data = eval("(" + response + ")");
                            var isSuccess = data.success;
                            if (isSuccess) {
                                alertMsg.correct("打印标记成功");
                            } else {
                                alertMsg.error(data.errMsg);
                            }
                        });
                    }
                });
                return true;
            }
        });
    },

    /**
     * 外迁选房
     */
    changeHs: function (newHsId, hsAddr, hsTp, preBldSize, clickObj) {
        var selectTr = $("tr.selected", $("#yaoXFang", navTab.getCurrentPanel()));
        var spanObj = $(clickObj).find("span.js_select");
        if (selectTr.length == 0) {
            alertMsg.warn('请先选择你要登记的购房人');
            return;
        }
        var flag = "false";
        $("tr", $("#yaoXFang", navTab.getCurrentPanel())).each(function () {
            var newHsIdTr = $("input[name=newHsId]", $(this)).val();
            if (newHsIdTr == newHsId) {
                flag = "true";
                return false;
            }
        });
        if (flag == "true") {
            alertMsg.warn('此房源已被选，请选择其它房源');
            return;
        }
        if ($("input[name=chooseStatus]", selectTr).val() == '2') {
            alertMsg.warn('此购房人已经确认选房，如需修改请先取消选房');
            return;
        }
        var selectedHs = $("input[name=selectedHs]", navTab.getCurrentPanel()).val().split(",");
        // 增加选中事件
        if (spanObj.hasClass("btn_opt_selected")) {
            // 取消选中
            spanObj.removeClass("btn_opt_selected");
            spanObj.addClass("btn_opt_un_select");
            $("input[name=chooseHsAddr]", selectTr).val('');
            $("input[name=newHsId]", selectTr).val('');
            $("td[name=tempAddr]", selectTr).text('');
            //删除隐藏的input中的值
            for (var i = 0; i < selectedHs.length; i++) {
                if (selectedHs[i] == newHsId) {
                    selectedHs.splice(i, 1);
                    break;
                }
            }
        } else {
            // 增加选中的房源
            $(clickObj).closest("div.js_page").find("span.btn_opt_selected").each(function () {
                $(this).removeClass("btn_opt_selected").addClass("btn_opt_un_select");
                var tempNewHsId = $(this).closest("td").find("input[name=newHsId]").val();
                //删除隐藏的input中的值
                for (var i = 0; i < selectedHs.length; i++) {
                    if (selectedHs[i] == tempNewHsId) {
                        selectedHs.splice(i, 1);
                        break;
                    }
                }
            });
            spanObj.removeClass("btn_opt_un_select");
            spanObj.addClass("btn_opt_selected");
            $("input[name=chooseHsAddr]", selectTr).val(hsAddr);
            $("input[name=newHsId]", selectTr).val(newHsId);
            $("td[name=tempAddr]", selectTr).text(hsAddr);
            selectedHs.push(newHsId);
        }
        $("input[name=selectedHs]", navTab.getCurrentPanel()).val(selectedHs.join(","));
    },

    /**
     * 进行选房
     */
    selectHs: function (newHsId, hsAddr, hsHxName, hsTp, preBldSize, clickObj) {
        var favContainer = $("#yaoXFang", navTab.getCurrentPanel());
        var spanObj = $(clickObj).find("span.js_select");
        var addHs = $(".js_xFang").find("input[name=newHsId][value=" + newHsId + "]");
        if (spanObj.length == 0) {
            return;
        }
        // 兼容错误数据，已经添加则返回
        if (addHs.length == 1) {
            return;
        }
        // 验证是否可以继续选房
        // 可选套数
        var canChooseHsNum = $("input[name=canChooseNum]", navTab.getCurrentPanel()).val();
        // 已选套数
        var selectedHsNum = $("input[name=newHsId][value!='']", favContainer).length;
        if (CheckUtil.checkNum(canChooseHsNum) && canChooseHsNum == selectedHsNum) {
            alertMsg.warn("您最多允许选中【" + canChooseHsNum + "】套房源！");
            return;
        }
        // 增加选中事件
        if (spanObj.hasClass("btn_opt_selected")) {
            addHs.closest("tr").find("span.js_delectBtn").trigger("click");
            // 取消选中
            spanObj.removeClass("btn_opt_selected");
            spanObj.addClass("btn_opt_un_select");
            return;
        } else {
            // 增加选中的房源
            spanObj.removeClass("btn_opt_un_select");
            spanObj.addClass("btn_opt_selected");
        }
        var spanTemplate = $(".jFang:eq(0)");
        var newSpan = spanTemplate;
        if ($("input[name=newHsId]", newSpan).val() != "") {
            newSpan = spanTemplate.clone();
            // 清除选房信息
            $("input.js_hs_info", newSpan).val("");
            favContainer.append(newSpan);
            initUI(newSpan);
        }
        $("input[name=newHsId]", newSpan).val(newHsId);
        $("input[name=chooseHsAddr]", newSpan).val(hsAddr);
        $("input[name=hsHxName]", newSpan).val(hsHxName);
        $("input[name=hsTp]", newSpan).val(hsTp);
        $("input[name=preBldSize]", newSpan).val(preBldSize);
        $("#hsAddrId", newSpan).html(hsAddr);
        var selectedHs = $("input[name=selectedHs]", navTab.getCurrentPanel()).val().split(",");
        selectedHs.push(newHsId);
        $("input[name=selectedHs]", navTab.getCurrentPanel()).val(selectedHs.join(","));
        ch001.adjustHeight();
    },

    /**
     * 提交选房
     * @param hsId
     */
    chQren: function () {
        var chsType = $("input[name=chsType]", navTab.getCurrentPanel()).val();
        if (chsType === "1") {
            ch001.cfmChsModel01();
        } else if (chsType === "3") {
            ch001.cfmChsModel03();
        }
    },

    /**
     * 回迁后台面积控制选房
     * @param hsId
     */
    cfmChsModel01: function () {
        var $form = $("#ch00101ChsForm", navTab.getCurrentPanel());
        // 验证选房信息
        var newHsIds = [];
        var xFang = $(".js_xFang", navTab.getCurrentPanel());
        var count = xFang.length;
        for (var i = 0; i < count; i++) {
            var newHsId = $("input[name=newHsId]", xFang.eq(i)).val();
            if (newHsId != "") {
                newHsIds.push(newHsId);
                var buyPersonName = $("input[name=buyPersonName]", xFang.eq(i)).val();
                if ($.trim(buyPersonName) == "") {
                    alertMsg.warn("请输入购房人");
                    return
                }
            }
        }
        if (newHsIds.length > 0) {
            alertMsg.confirm("您确定要提交选房?", {
                okCall: function () {
                    var packet = new AJAXPacket();
                    var prjCd = getPrjCd();
                    var formData = $form.serializeArray();
                    formData.push({name: "prjCd", value: getPrjCd()});
                    packet.data.data = formData;
                    packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-cfmChoose.gv";
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            alertMsg.correct("选房成功");
                            var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
                            ch001.chInfo(hsId);
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    });
                }
            });
        } else {
            alertMsg.warn("请先选房再确认");
        }
    },
    /**
     * 按照面积提交选房
     * @param hsId
     */
    cfmChsModel03: function () {
        var $form = $("#ch00101ChsForm", navTab.getCurrentPanel());
        // 验证选房信息
        var newHsIds = [];
        var xFang = $(".js_xFang", navTab.getCurrentPanel());
        var count = xFang.length;
        for (var i = 0; i < count; i++) {
            var newHsId = $("input[name=newHsId]", xFang.eq(i)).val();
            if (newHsId != "") {
                newHsIds.push(newHsId);
                var buyPersonName = $("input[name=buyPersonName]", xFang.eq(i)).val();
                if ($.trim(buyPersonName) == "") {
                    alertMsg.warn("请输入购房人");
                    return
                }
            } else {
                alertMsg.warn("存在未完成的选房");
                return
            }
        }
        if (newHsIds.length > 0) {
            alertMsg.confirm("您确定要提交选房?", {
                okCall: function () {
                    var packet = new AJAXPacket();
                    var prjCd = getPrjCd();
                    var formData = $form.serializeArray();
                    formData.push({name: "prjCd", value: getPrjCd()});
                    packet.data.data = formData;
                    packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-cfmChoose.gv";
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            alertMsg.correct("选房成功");
                            var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
                            ch001.chInfo(hsId);
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    });
                }
            });
        } else {
            alertMsg.warn("请先选房再确认");
        }
    },

    /**
     * 打印
     * @param hsId
     */
    printCh: function (hsId) {
        try {
            var iframeDoc = document.getElementById("ch001DocIFrame").contentWindow.document;
            iframeDoc.getElementById("PageOfficeCtrl1").ShowDialog(4);
        } catch (e) {
        }
        alertMsg.confirm("确认文档已打印？", {
                okCall: function () {
                    // 获取选房记录
                    var hsCtChooseIdArr = [];
                    $("input[name=hsCtChooseId]", navTab.getCurrentPanel()).each(function () {
                        var hsCtChooseId = $(this).val();
                        if (hsCtChooseId != "") {
                            hsCtChooseIdArr.push(hsCtChooseId);
                        }
                    });
                    // 重新加载
                    var packet = new AJAXPacket();
                    var prjCd = getPrjCd();
                    packet.data.add("prjCd", prjCd);
                    packet.data.add("hsId", hsId);
                    packet.data.add("hsCtChooseIds", hsCtChooseIdArr.join(","));
                    packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-doPrint.gv";
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            alertMsg.correct("打印标记成功");
                            ch001.chInfo(hsId, "");
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    });
                }
            }
        );
    },
    /**
     * 上一户下一户
     */
    singleQuery: function (chooseHsSid, flag, clear) {
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        var ch001QueryForm = $("#ch001QueryForm", navTab.getCurrentPanel());
        var jsonData = ch001QueryForm.serializeArray();
        jsonData.push({name: "flag", value: flag});
        jsonData.push({name: "chooseHsSid", value: chooseHsSid});
        jsonData.push({name: "prjCd", value: prjCd});
        packet.data.data = jsonData;
        // 查询上一个、下一个选房人
        packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-singleQuery.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var hsId = data.hsId;
            if (hsId) {
                ch001.chInfo(data.hsId);
            } else {
                if (flag == 'last') {
                    alertMsg.warn("已经是第一个人");
                } else if (flag == 'next') {
                    alertMsg.warn("已经是最后一个人");
                    if (clear == 'clear') {
                        clearTimeout(index.ch001Countdown);
                    }
                }
            }
        });
    },

    /**
     * 检索房产
     * @param obj
     */
    schHs: function (initFlag) {
        var ch001SchDiv = $("#ch001SchDiv", navTab.getCurrentPanel());
        var conditionNameArr = [];
        var conditionArr = [];
        var conditionValueArr = [];
        var schType = $("select[name=schType]", ch001SchDiv).val();
        var schValue = $("input[name=schValue]", ch001SchDiv).val();
        var schCon = 'like';
        if (schType == 'HouseInfo.HsCtInfo.chooseHsSid') {
            schCon = '=';
        }
        conditionNameArr.push(schType);
        conditionArr.push(schCon);
        conditionValueArr.push(schValue);

        conditionNameArr.push("HouseInfo.HsCtInfo.ctStatus");
        conditionArr.push("=");
        conditionValueArr.push("2");
        // 选房状态
        var chStatus = [];
        navTab.getCurrentPanel().find(":checkbox[checked][name=chooseStatus]").each(
            function (i) {
                chStatus.push($(this).val());
            }
        );
        if (chStatus.length > 0) {
            conditionNameArr.push("HouseInfo.HsCtInfo.chHsStatus");
            conditionArr.push("in");
            conditionValueArr.push(chStatus.join("|"));
        }
        var ctType = [];
        navTab.getCurrentPanel().find("input[checked][name=ctType]").each(
            function (i) {
                ctType.push($(this).val());
            }
        );
        if (ctType.length > 0) {
            conditionNameArr.push("HouseInfo.HsCtInfo.ctType");
            conditionArr.push("in");
            conditionValueArr.push(ctType.join("|"));
        }

        var ch001QueryForm = $("#ch001QueryForm", navTab.getCurrentPanel());
        $("input[name=conditionName]", ch001QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", ch001QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", ch001QueryForm).val(conditionValueArr.join(","));
        // 查询处理结果
        if (initFlag) {
            Page.query(ch001QueryForm, '$("span.split_but", navTab.getCurrentPanel()).click()');
        } else {
            Page.query(ch001QueryForm);
        }

    },

    /**
     * 选房信息
     * @param hsId
     */
    chInfo: function (hsId, showFlag) {
        if (!showFlag) {
            showFlag = "";
        }
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("showFlag", showFlag);
        packet.data.add("chsType", $("input[name=chsType]", navTab.getCurrentPanel()).val());
        packet.data.add("xyFormCd", $("input[name=xyFormCd]", navTab.getCurrentPanel()).val());
        packet.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ch001Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ch001Context", navTab.getCurrentPanel()));
            ch001.countDown("");
            ch001.adjustHeight();
            // 获取可选套数，如果套数>2增
            var canChooseHsNum = $("input[name=canChooseNum]", navTab.getCurrentPanel()).val();
            if (canChooseHsNum && CheckUtil.checkNum(canChooseHsNum) && canChooseHsNum > 1) {
                $("#ch00101ListModel", navTab.getCurrentPanel()).trigger("click");
            }
        });
    },

    /**
     * 房屋信息
     * @param hsId
     */
    openHouseView: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.openTab(null, "ph003-init", url, {title: "居民信息", fresh: true});
    },

    /**
     * 生成选房确认单
     * @param formId
     * @param templateName
     */
    generateCh: function (hsId) {
        var ctType = $("#ctType", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-generateCh.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("ctType", ctType);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#ch001_ReportData", navTab.getCurrentPanel());
                container.html(response);
                container.find("iframe").height(container.height() - 10);
            }
        );
    },

    /**
     * 下载选房确认单
     * @param formId
     * @param templateName
     */
    downloadCh: function (hsId) {
        var ctType = $("#ctType", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-generateCh.gv?hsId=" + hsId + "&fromOp=download&prjCd=" + getPrjCd() + "&ctType=" + ctType;
        window.open(url);
    },

    /**
     * 启动签约倒计时
     * @param flag
     */
    countDown: function (flag) {
        var $startControl = $("span.js_count_control", navTab.getCurrentPanel());
        if (flag == "start" && $startControl.html() == "[延长时间]") {
            var chsAddTimeRight = $("input[name=chsAddTimeRight]", navTab.getCurrentPanel()).val();
            if (chsAddTimeRight == "1") {
                // 延长签约时间
                var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
                var url = getGlobalPathRoot() + "eland/ch/ch001/ch001-openDelay.gv";
                $.pdialog.open(url, "ch001-openDelay", "延长时间",
                    {mask: true, max: false, maxable: false, resizable: false, width: 500, height: 130});
            }
        }
        // 非选房状态不显示倒计时
        var spaceTimeObj = $("input[name=spaceTime]", navTab.getCurrentPanel());
        if (spaceTimeObj.length == 0) {
            clearTimeout(index.ch001Countdown);
            return;
        }

        var countSwitch = $("input[name=countSwitch]", navTab.getCurrentPanel()).val();
        if ($startControl.html() != "[延长时间]" && (countSwitch == "1" || flag == "start")) {
            var spaceTime = $("input[name=spaceTime]", navTab.getCurrentPanel()).val();
            if (spaceTime && spaceTime != "" && spaceTime != "0") {
                index.ch001CountTime = Number(spaceTime) * 60;
                ch001.count();
            }
            var chsAddTimeRight = $("input[name=chsAddTimeRight]", navTab.getCurrentPanel()).val();
            if (chsAddTimeRight == "1") {
                $startControl.html("[延长时间]");
            } else {
                $startControl.html("");
            }
        }

    },

    // 延长倒计时时间
    delayCount: function () {
        var ch001Delayform = $("#ch001Delayform", $.pdialog.getCurrent());
        if (ch001Delayform.valid()) {
            index.ch001CountTime = index.ch001CountTime + parseFloat($("input[name=delayNum]", ch001Delayform).val()) * 60;
            $.pdialog.closeCurrent();
        }
    },

    /**
     * 倒计时处理
     */
    count: function () {
        if (index.ch001CountTime > 0) {
            var minite = Math.floor(index.ch001CountTime / 60) % 60;
            var second = Math.floor(index.ch001CountTime % 60);
            if (minite == 0) {
                $(".countTime").html(second + "秒");
            } else {
                $(".countTime").html(minite + "分" + second + "秒");
            }
            if (minite < 2) {
                $(".countTime").css("font-size", "60px");
            } else {
                $(".countTime").css("font-size", "50px");
            }
            index.ch001CountTime--;
        } else {
            clearTimeout(index.ch001Countdown);
            var chooseHsSid = $("input[name=chooseHsSid]", navTab.getCurrentPanel()).val();
            ch001.singleQuery(chooseHsSid, 'next', 'clear');
        }
        index.ch001Countdown = setTimeout("ch001.count()", 1000);
    },
    /**
     * 全屏返回切换
     * @param obj
     */
    changeFull: function (obj) {
        var fullScreen = $("input[name=fullScreen]", navTab.getCurrentPanel());
        if (fullScreen.val() == 'true') {
            fullScreen.val('false');
            ch001.restoreScreen();
            $(obj).find("span").text("全屏");
        } else {
            fullScreen.val('true');
            ch001.fullScreen();
            $(obj).find("span").text("还原");
        }
    },

    /**
     * 全屏
     */
    fullScreen: function () {
        var obj = $("#ch001_ReportData", navTab.getCurrentPanel());
        var width = $(window).width();
        var height = $(window).height();
        obj.css({
            "position": "fixed",
            "height": height,
            "width": width,
            "top": "0px",
            "bottom": "0px",
            "z-index": "99999",
            "left": "0px"
        }).removeClass("mart5");
        // 列表数据高度
        $("#ch001001_list_print", navTab.getCurrentPanel()).find("div.gridScroller").css("height", height - 116 + "px");
        $("#statusShow", navTab.getCurrentPanel()).css("top", 46 + "px");
        $("#ch001001div div.js_page", navTab.getCurrentPanel()).css("height", height - 40 + "px");
    },

    /**
     * 还原
     */
    restoreScreen: function () {
        var obj = $("#ch001_ReportData", navTab.getCurrentPanel());
        obj.css({
            "position": "relative",
            "height": "100%",
            "width": "100%",
            "z-index": ""
        }).addClass("mart5");
        ch001.adjustHeight();
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
            appendToContainer: $("#ch001_ReportData", navTab.getCurrentPanel()),
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
    startEdite: function (obj) {

        var $tr = $(obj).closest("tr");
        $("input[name=personNoticeAddr]", $tr).removeClass("readonly").removeAttr("readonly");
        $("input[name=buyPersonTel]", $tr).removeClass("readonly").removeAttr("readonly");
        $("span[name=editeBtn]", $tr).hide();
        $("span[name=saveBtn]", $tr).show();
    },
    saveNoticeInfo: function (obj) {
        var $tr = $(obj).closest("tr");
        $("input[name=personNoticeAddr]", $tr).addClass("readonly").attr("readonly", "readonly");
        $("input[name=buyPersonTel]", $tr).addClass("readonly").attr("readonly", "readonly");
        $("span[name=editeBtn]", $tr).show();
        $("span[name=saveBtn]", $tr).hide();
        var hsCtChooseId = $("input[name=hsCtChooseId]", $tr).val();
        var personNoticeAddr = $("input[name=personNoticeAddr]", $tr).val();
        var buyPersonTel = $("input[name=buyPersonTel]", $tr).val();
        var newHsId = $("input[name=newHsId]", $tr).val();
        var ajaxPacket = new AJAXPacket();
        ajaxPacket.url = getGlobalPathRoot() + "eland/oh/oh006/oh006-saveNoticeInfo.gv";
        ajaxPacket.data.add("prjCd", getPrjCd());
        ajaxPacket.data.add("hsCtChooseId", hsCtChooseId);
        ajaxPacket.data.add("personNoticeAddr", personNoticeAddr);
        ajaxPacket.data.add("buyPersonTel", buyPersonTel);
        ajaxPacket.data.add("newHsId", newHsId);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("保存成功！");
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });

    },
    drawConfirm: function (obj) {
        var $tr = $(obj).closest("tr");
        var hsCtChooseId = $("input[name=hsCtChooseId]", $tr).val();
        var ajaxPacket = new AJAXPacket();
        ajaxPacket.url = getGlobalPathRoot() + "eland/ch/ch001/ch001-drawConfirm.gv";
        ajaxPacket.data.add("prjCd", getPrjCd());
        ajaxPacket.data.add("hsCtChooseId", hsCtChooseId);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("领取成功！");
                $("span[name=drawConfirm1]", $tr).hide();
                $("span[name=drawConfirm2]", $tr).show();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 加载购房协议签订表单
     */
    loadGfxy: function () {
        var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
        var formCd = $("input[name=xyFormCd]", navTab.getCurrentPanel()).val();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("formCd", formCd);
        packet.data.add("showNextPrev", "false");
        packet.url = getGlobalPathRoot() + "eland/ph/ph014/ph014-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var toContainer = $("div.js_gfxy_container", navTab.getCurrentPanel());
            toContainer.html(response);
            initUI(toContainer);
        });
    }

};

$(document).ready(function () {
    //选房状态读取cookie
    var chooseStatus = $("input[name=chooseStatus]", navTab.getCurrentPanel()).val().split("%2C");
    navTab.getCurrentPanel().find(":checkbox[name=chooseStatus]").each(function () {
        for (var i = 0; i < chooseStatus.length; i++) {
            if ($(this).val() == chooseStatus[i]) {
                $(this).attr("checked", "true");
                break;
            }
        }
    });
    //选房状态读取cookie
    var isGiveUpHs = $("input[name=isGiveUpHs]", navTab.getCurrentPanel()).val().split("%2C");
    navTab.getCurrentPanel().find(":checkbox[name=isGiveUpHs]").each(function () {
        for (var i = 0; i < isGiveUpHs.length; i++) {
            if ($(this).val() == isGiveUpHs[i]) {
                $(this).attr("checked", "true");
                break;
            }
        }
    });
    //最小面积可选读取cookie
    var kxMinSize = $("input[name=kxMinSize]", navTab.getCurrentPanel()).val();
    navTab.getCurrentPanel().find("input[name=kxMinSize]").val(kxMinSize);
    //最大面积可选读取cookie
    var kxMaxSize = $("input[name=kxMaxSize]", navTab.getCurrentPanel()).val();
    navTab.getCurrentPanel().find("input[name=kxMaxSize]").val(kxMaxSize);
    //倒计时开关读取cookie
    var countSwitch = $("input[name=countSwitch]", navTab.getCurrentPanel()).val();
    navTab.getCurrentPanel().find(":radio[name=countSwitch]").each(function () {
        if ($(this).val() == countSwitch) {
            $(this).attr("checked", "true");
        }
    });
    // 查询房屋
    ch001.schHs(true);

    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("ch001SchDiv", "ch001Context", {});
});
