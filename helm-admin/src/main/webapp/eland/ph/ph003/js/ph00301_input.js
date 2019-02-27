var ph00301_input = {
    /**
     * 点击取消样式
     */
    onclickhsCd: function () {
        var hscdInput = $("input[name=hsCd]", navTab.getCurrentPanel());
        var history = $("input[name=history]", navTab.getCurrentPanel());
        if (hscdInput.hasClass("errMsg")) {
            hscdInput.css({
                borderColor: "",
                color: ""
            }).val(history.val()).removeClass("errMsg");
            history.val("");
        }
    },

    /**
     * 判断hsCd是否重复
     */
    queryHsCd: function () {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-queryHsCd.gv";
        var ajaxPacket = new AJAXPacket(url);
        var hsCd = $("input[name=hsCd]", navTab.getCurrentPanel()).val();
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        if (hsCd == "") {
            return;
        }
        ajaxPacket.noCover = true;
        ajaxPacket.data.add("hsCd", hsCd);
        ajaxPacket.data.add("hsId", hsId);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {

            } else {
                var _this = $("input[name=hsCd]", navTab.getCurrentPanel());
                if (_this.hasClass("errMsg")) {
                    return;
                }
                $("input[name=history]", navTab.getCurrentPanel()).val(_this.val());
                _this.addClass("errMsg");
                _this.css({borderColor: "#f00", color: "#f00"}).val("档案编号重复，请重新输入。");
            }
        });
    },
    initAddrRegTree: function (obj) {
        var $this = $(obj);
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-initAddrTree.gv?prjCd=" + getPrjCd();
        $(obj).openTip({href: url, height: "300", appendBody: true});
    },

    /**
     * 查看居民信息
     */
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /* 居民选房信息登记 */
    editChooseHs: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ch/ch002/ch002-init.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ch002-init", "修改居民选房", {max: true, mask: true});
    },

    /**
     * 获取区域地址列表
     * @param obj
     * @returns {string}
     */
    getHtUrl: function (obj) {
        return getGlobalPathRoot() + "eland/ph/ph003/ph00301-queryReg.gv?prjCd=" + getPrjCd();
    },
    /**
     * 获取区域参数
     * @param obj
     */
    getHtOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].regName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.regName;
            },
            displayValue: function (value, data) {
                return data.regName;
            },
            mustMatch: false,
            remoteDataType: "json",
            autoFill: false,
            delay: 300,
            minChars: 0,
            dyExtraParams: function (obj) {
                return {
                    regName: $("input[name=buildAddr]", navTab.getCurrentPanel()).val(),
                    prjCd: getPrjCd()
                }
            },
            onItemSelect: function (obj) {
                var oldUpRegId = $("input[name=ttUpRegId]", navTab.getCurrentPanel()).val();
                if (oldUpRegId != obj.data.regId) {
                    $("input[name=ttUpRegId]", navTab.getCurrentPanel()).val(obj.data.regId);
                    var oldBuildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
                    if (oldBuildId != "") {
                        $("input[name=buildId]", navTab.getCurrentPanel()).val("");
                        $("input[name=buildNo]", navTab.getCurrentPanel()).val("");
                        $("input[name=ttRegId]", navTab.getCurrentPanel()).val("");
                        // 查询显示新建建筑信息
                        ph00301_input.showBuildInfo("");
                    }
                }
            },
            onNoMatch: function (obj) {
                var oldUpRegId = $("input[name=ttUpRegId]", navTab.getCurrentPanel()).val();
                if (oldUpRegId != "") {
                    // 上级区域清空
                    $("input[name=ttUpRegId]", navTab.getCurrentPanel()).val("");
                    var oldBuildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
                    if (oldBuildId != "") {
                        $("input[name=buildId]", navTab.getCurrentPanel()).val("");
                        $("input[name=buildNo]", navTab.getCurrentPanel()).val("");
                        $("input[name=ttRegId]", navTab.getCurrentPanel()).val("");
                        // 查询显示新建建筑信息
                        ph00301_input.showBuildInfo("");
                    }
                }
            }
        }
    },

    /**
     * 获取区域内建筑地址
     */
    getBldUrl: function (obj) {
        return getGlobalPathRoot() + "eland/ph/ph003/ph00301-queryBuild.gv?prjCd=" + getPrjCd();
    },

    /**
     * 获取区域内建筑参数
     * @param obj
     * @returns
     */
    getBldOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].buildNo;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.buildNo;
            },
            displayValue: function (value, data) {
                return data.buildNo;
            },
            mustMatch: false,
            remoteDataType: "json",
            autoFill: false,
            delay: 300,
            minChars: 0,
            dyExtraParams: function (obj) {
                return {
                    buildAddr: $("input[name=buildAddr]", navTab.getCurrentPanel()).val(),
                    buildNo: $("input[name=buildNo]", navTab.getCurrentPanel()).val()
                }
            },
            onItemSelect: function (obj) {
                var oldBuildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
                if (oldBuildId != obj.data.buildId) {
                    $("input[name=buildId]", navTab.getCurrentPanel()).val(obj.data.buildId);
                    $("input[name=ttRegId]", navTab.getCurrentPanel()).val(obj.data.ttRegId);
                    ph00301_input.showBuildInfo(obj.data.buildId);
                }
            },
            onNoMatch: function (obj) {
                var oldBuildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
                if (oldBuildId != "") {
                    $("input[name=buildId]", navTab.getCurrentPanel()).val("");
                    $("input[name=ttRegId]", navTab.getCurrentPanel()).val("");
                    // 查询显示新建建筑信息
                    ph00301_input.showBuildInfo("");
                }
            }
        }
    },

    /**
     * 显示建筑信息
     * @param buildId
     */
    showBuildInfo: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-initBuild.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("buildId", buildId);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var containDiv = $("div.js_build_info", navTab.getCurrentPanel());
            containDiv.html(response);
            initUI(containDiv);
            // 同步建筑信息到房屋信息
            ph00301_input.syncBuildToHs();
        });
    },

    /**
     * 同步建筑的信息到房屋信息
     */
    syncBuildToHs: function () {
        var buildInfo = $("div.js_build_info", navTab.getCurrentPanel());
        var hsBaseInfo = $("table.js_hs_base_info", navTab.getCurrentPanel());
        // 中介公司
        var housettCompanyId = $("select[name=housettCompanyId]", hsBaseInfo);
        if ($("input.js_db_housettCompanyId", hsBaseInfo).val() == "") {
            var ttCompanyId = $("select[name=ttCompanyId]", buildInfo);
            housettCompanyId.val(ttCompanyId.val());
        }
        // 主要谈话人
        var housettMainTalk = $("input[name=housettMainTalk]", hsBaseInfo);
        if ($("input.js_db_housettMainTalk", hsBaseInfo).val() == "") {
            var ttMainTalk = $("input[name=ttMainTalk]", buildInfo);
            housettMainTalk.val(ttMainTalk.val());
        }
        // 副谈人员
        var housettSecTalk = $("input[name=housettSecTalk]", hsBaseInfo);
        if ($("input.js_db_housettSecTalk", hsBaseInfo).val() == "") {
            var ttSecTalk = $("input[name=ttSecTalk]", buildInfo);
            housettSecTalk.val(ttSecTalk.val());
        }
    },

    /**
     * 根据房屋详细地址 查询 房屋
     * @param obj
     */
    queryBuildDetail: function (obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-initdHsList.gv?prjCd=" + getPrjCd();
        $(obj).openTip({
            href: url, height: "auto", appendBody: true,
            offsetY: 0,
            offsetX: 0,
            args: {
                hsFullAddr: $("input[name=hsFullAddr]", navTab.getCurrentPanel()).val(),
                buildId: $("input[name=buildId]", navTab.getCurrentPanel()).val(),
            },
            relObj: $(obj).prev("input")
        });
    },

    // 加入到流程中时显示有问题。
    initSchHs: function (hsId, buildId, regId) {
        $("#openTip").remove();
        var url = "eland/ph/ph003/ph00301-initHs.gv?hsId=" + hsId + "&buildId=" + buildId + "&regId=" + regId + "&prjCd=" + getPrjCd();
        index.quickOpen(url, {opCode: "ph00301-initHs", opName: "信息登记", fresh: true});
    },

    //initBuildType: function () {
    //    var bdType = $("input[name=buildTypeFlag]").val();
    //    if (bdType) {
    //        var buildType = $("select[name=buildType]", navTab.getCurrentPanel()).val(bdType);
    //    } else {
    //        var buildType = $("select[name=buildType]", navTab.getCurrentPanel()).val(1);
    //    }
    //    var buildTypeText = $("select[name=buildType]", navTab.getCurrentPanel()).find("option:selected").text();
    //    $("span.js_build_bdType").text(buildTypeText);
    //    this.onBuildTypeChange();
    //},

    /**
     * 改变房屋的类型
     */
    onBuildTypeChange: function () {
        var hsType = $("select[name=hsType]", navTab.getCurrentPanel()).val();
        // 改变建筑信息里的类型显示值
        if (hsType >= '300' && hsType < '400') {
            $("span.js_reg_build_2", navTab.getCurrentPanel()).hide();
            $("span.js_reg_build_2", navTab.getCurrentPanel()).find("input.js_required").each(function () {
                $(this).removeClass("required");
            });
            $("input[name=buildAddr]", navTab.getCurrentPanel()).attr("placeholder", "点击选择房屋对应的门牌位置");
        } else {
            // 楼房
            $("span.js_reg_build_2", navTab.getCurrentPanel()).show();
            $("span.js_reg_build_2", navTab.getCurrentPanel()).find("input.js_required").each(function () {
                $(this).addClass("required");
            });
            $("input[name=buildAddr]", navTab.getCurrentPanel()).attr("placeholder", "点击选择房屋对应的门牌位置");
        }
    },

    /**
     * 产权信息调整
     */
    onHsOwnerTypeChange: function () {
        var buildType = $("select[name=buildType]", navTab.getCurrentPanel()).val();
        var selectValue = $("select[name=hsOwnerType]", navTab.getCurrentPanel()).val();
        $("label.hsTypeLabel", navTab.getCurrentPanel()).each(
            function (i) {
                if (selectValue >= '100' && selectValue < '200') {
                    // 私产房
                    $(this).html($(this).html().replaceAll("承租人", "产权人"));
                    $(this).html($(this).html().replaceAll("户主", "产权人"));
                } else if (selectValue >= '200' && selectValue < '300') {
                    // 直管公产房
                    $(this).html($(this).html().replaceAll("产权人", "承租人"));
                    $(this).html($(this).html().replaceAll("户主", "承租人"));
                    ph00301_input.changeUseSize();
                } else if ("7" == selectValue) {
                    $(this).html($(this).html().replaceAll("产权人", "户主"));
                    $(this).html($(this).html().replaceAll("承租人", "户主"));
                }
            }
        );

        if (selectValue >= '100' && selectValue < '200') {
            //私房  == 房屋产权证号：
            $("label.hsOwnLabel", navTab.getCurrentPanel()).html("产权证号：");
            $("tr.js_hsPubOwnerName").hide();
        } else if (selectValue >= '200' && selectValue < '300') {
            //直管公房  == 公房租赁合同号：
            $("label.hsOwnLabel", navTab.getCurrentPanel()).html("租赁合同号：");
            $("tr.js_hsPubOwnerName").show();
        } else if ("7" == selectValue) {
            $("label.hsOwnLabel", navTab.getCurrentPanel()).html("");
            $("tr.js_hsPubOwnerName").hide();
        }
    },

    /**
     * 公房根据使用面积计算建筑面积
     */
    changeUseSize: function () {
        var hsTp = $("select[name=hsOwnerType]", navTab.getCurrentPanel()).val();
        var regBuildSize = $("input[name=hsBuildSize]", navTab.getCurrentPanel()).val();
        if (hsTp != "0" && regBuildSize == '') {
            // 公产房屋，按照使用面积自动计算建筑面积
            // TODO：政策未确定，不明确对应系数关系，明确后再调整
//            var regUseSize = $("input[name=hsUseSize]", navTab.getCurrentPanel()).val();
//            regBuildSize = regUseSize * 1.333;
//            regBuildSize = regBuildSize.toFixed(2);
            var hsBuildSize = $("input[name=hsBuildSize]", navTab.getCurrentPanel());
//            hsBuildSize.val(regBuildSize);
            ph00301_input.flashInputRule(hsBuildSize);
        }
    },
    /**
     * 煤改电费用计算
     */
    changeMGd: function () {
        var mgdGrZf = $("input[name=mgdGrZf]", navTab.getCurrentPanel()).val();
        var mgdGrZfReal = mgdGrZf * 0.9;
        mgdGrZfReal = mgdGrZfReal.toFixed(2);
        $("input[name=mgdGrZf_real]", navTab.getCurrentPanel()).val(mgdGrZfReal);
    },

    /**
     * 加载tab页的内容
     * @param obj
     */
    loadTabContext: function (obj) {
        var _this = $(obj);
        var url = _this.attr("url");
        var urlData = getGlobalPathRoot() + encodeURI(encodeURI(url));
        var contextDiv = _this.closest("div.tabs").find("div.tabsContent").find(">div:eq(" + _this.index() + ")");
        if (contextDiv.html() == "") {
            //var packet = new AJAXPacket(urlData);
            //packet.noCover = true;
            //packet.data.add("prjCd", getPrjCd());
            //core.ajax.sendPacketHtml(packet, function (response) {
            //    contextDiv.html(response);
            initUI(contextDiv);
            //})
        }
    },

    /**
     * 煤改电费用计算
     */
    changeBusinessSize: function () {
        var businessCertNum = $("input[name=businessCertNum]", navTab.getCurrentPanel()).val();
        var businessHsSize = $("input[name=businessHsSize]", navTab.getCurrentPanel()).val();
        if (businessCertNum != "" && businessHsSize == "") {
            $("input[name=businessHsSize]", navTab.getCurrentPanel()).val(
                $("input[name=hsBuildSize]", navTab.getCurrentPanel()).val());
        } else if (businessCertNum == "") {
            ("input[name=businessHsSize]", navTab.getCurrentPanel()).val("");
        }
    },

    /**
     * 获取界面的所有人员信息
     */
    getAllPsInfo: function (relObjContainIdArr, excludeObj, clickObj) {
        // 排除的信息对象
        var excludeMap = new Map();
        if (excludeObj && excludeObj.length > 0) {
            $("input.js_ps_name", excludeObj).each(function () {
                excludeMap.put($(this).val(), $(this));
            });
        }
        var clickObjPsId = "";
        if (clickObj) {
            clickObjPsId = clickObj.closest("tr").find("input.js_ps_id").val()
        }
        if (!relObjContainIdArr || relObjContainIdArr.length == 0) {
            relObjContainIdArr = ["ownerTable", "familyTable_booklet"];
        }
        var persons = new Map();
        var personIds = [];
        for (var i = 0; i < relObjContainIdArr.length; i++) {
            var relObj = $("#" + relObjContainIdArr[i]);
            $("input.js_ps_name", relObj).each(function () {
                var $this = $(this);
                var personName = $this.val();
                if (personName == "") {
                    return true;
                }
                var tr = $this.closest("tr");
                if (tr.hasClass("isDelPs")) {
                    return true;
                }
                var currentPerson = ph00301_input.getPersonInfo(tr);
                if (currentPerson.personId == "") {
                    currentPerson.personId = "${ps_id" + new Date().getTime() + "}";
                    tr.find("input.js_ps_id").val(currentPerson.personId);
                }
                // 排除的人员信息
                if (excludeMap.containsKey(personName)) {
                    if (clickObjPsId != "") {
                        if (clickObjPsId != currentPerson.personId) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                // 历史人员信息
                var oldPersonInfo = persons.get(currentPerson.personId);
                if (!oldPersonInfo) {
                    // 新增人员
                    personIds.push(currentPerson.personId);
                    oldPersonInfo = currentPerson;
                } else {
                    $.extend(oldPersonInfo, currentPerson);
                }
                persons.put(currentPerson.personId, oldPersonInfo);
            });
        }
        var results = [];
        for (var i = 0; i < personIds.length; i++) {
            var temp = personIds[i];
            var tempPerson = persons.get(temp);
            if (tempPerson) {
                results.push(tempPerson);
            }
        }
        // 返回顺序
        return results;
    },

    /**
     * 获取人员信息
     * @param obj
     */
    getOwnerPsOpt: function (obj) {
        return {
            fetchData: function (clickObj) {
                return ph00301_input.getAllPsInfo(
                    ["familyTable_booklet", "familyTable_owner"], $("#ownerTable", navTab.getCurrentPanel()),
                    clickObj);
            },
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.personName;
            },
            displayValue: function (value, data) {
                return data.personName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                ph00301_input.syncPerson(data, tr);
                tr.find("input").each(function () {
                    ph00301_input.flashInputRule($(this))
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                var personId = "${ps_id" + new Date().getTime() + "}";
                tr.find("input.js_ps_id").val(personId);
            }
        }
    },

    /**
     * 产权信息高级录入人员信息
     * @param obj
     */
    getOwnerAdvPsOpt: function (obj) {
        return {
            fetchData: function (clickObj) {
                return ph00301_input.getAllPsInfo(
                    ["familyTable_booklet", "familyTable_owner"], $("#advanceOwnerDiv", navTab.getCurrentPanel()),
                    clickObj);
            },
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.personName;
            },
            displayValue: function (value, data) {
                return data.personName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                ph00301_input.syncPerson(data, tr);
                tr.find("input").each(function () {
                    ph00301_input.flashInputRule($(this))
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                var personId = "${ps_id" + new Date().getTime() + "}";
                tr.find("input.js_ps_id").val(personId);
            }
        }
    },

    /**
     * 产权信息高级录入人员信息
     * @param obj
     */
    getOwnerWtPsOpt: function (obj) {
        return {
            fetchData: function (clickObj) {
                return ph00301_input.getAllPsInfo(
                    ["familyTable_booklet", "familyTable_owner"], $(clickObj).closest("table"),
                    clickObj);
            },
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.personName;
            },
            displayValue: function (value, data) {
                return data.personName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                ph00301_input.syncPerson(data, tr);
                tr.find("input").each(function () {
                    ph00301_input.flashInputRule($(this))
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                var personId = "${ps_id" + new Date().getTime() + "}";
                tr.find("input.js_ps_id").val(personId);
            }
        }
    },

    /**
     * 户籍信息获取人员信息
     * @param obj
     */
    getPsOpt: function (obj) {
        return {
            fetchData: function (clickObj) {
                return ph00301_input.getAllPsInfo(["advanceOwnerDiv"], $("table[id='familyTable_booklet']"), clickObj);
            },
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.personName;
            },
            displayValue: function (value, data) {
                return data.personName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                ph00301_input.syncPerson(data, tr);
                if (tr.find("input.js_ps_family_flag").val() == "1") {
                    tr.find("input.js_ps_family_ps_id").val(data.personId);
                    ph00301_input.changeFamilyPsId(tr, data.personId);
                }
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                var personId = "${ps_id" + new Date().getTime() + "}";
                tr.find("input.js_ps_id").val(personId);
                if (tr.find("input.js_ps_family_flag").val() == "1") {
                    tr.find("input.js_ps_family_ps_id").val(personId);
                    ph00301_input.changeFamilyPsId(tr, personId);
                }
            }
        }
    },

    /**
     * 获取区域地址列表
     * @param obj
     * @returns {string}
     */
    getPsRelUrl: function (obj) {
        return getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?prjCd=" + getPrjCd() + "&itemCd=MEM_REL_TYPE";
    },

    /**
     * 户籍信息获取人员信息
     * @param obj
     */
    getPsRelOpt: function (obj) {
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
            filterResults: false
        }
    },

    changeFamilyPsId: function (clickTr, newPersonId) {
        clickTr.nextAll("tr").each(function () {
            var $this = $(this);
            var trFamilyPerson = $this.find("input.js_ps_rel");
            if (trFamilyPerson.val() != "户主") {
                $this.find("input.js_ps_family_ps_id").val(newPersonId);
                return true;
            } else {
                return false;
            }
        });
    },

    /**
     * 获取总户籍数、户籍人口数
     */
    calcFamNum: function () {
        //统计户籍人数
        var famPsNum = 0;
        $("table[id='familyTable_booklet']").find("input.js_ps_name").each(function () {
            if ($(this).val() != '') {
                famPsNum++;
            }
        });
        $("input[name=famPsNum]").val(famPsNum);

        //统计户籍人数
        var famNum = 0;
        $("table[id='familyTable_booklet']").find("input.js_ps_rel").each(function () {
            if ($(this).val() != '') {
                if ($(this).val() == '户主') {
                    famNum++;
                }
            }
        });
        $("input[name=allFamNum]").val(famNum);
    },

    /**
     * 增加户主
     * @param obj
     */
    addFamily: function (tabId, obj) {
        var addRow = table.addRowNoInit(tabId, null);
        addRow.find("span.js_mem_rht").remove();
        $("input.js_ps_rel", addRow).attr("readonly", "readonly").addClass("readonly").val("户主").removeClass("autocomplete");
        $(addRow).initUI();
        var personId = "${ps_id" + new Date().getTime() + "}";
        addRow.find("input.js_ps_id").val(personId);
        addRow.find("input.js_ps_family_flag").val("1");
        addRow.find("input.js_ps_family_ps_id").val(personId);
        addRow.find("input[type=text]:eq(0)").focus();
    },

    /**
     * 伤处户籍
     * @param obj
     */
    deleteFamily: function (obj) {
        var clickTr = $(obj).closest("tr");
        clickTr.nextAll("tr").each(function () {
            var $this = $(this);
            var trFamilyPerson = $this.find("input.js_ps_rel");
            if (trFamilyPerson.val() != "户主") {
                $(this).remove();
                return true;
            } else {
                return false;
            }
        });
        clickTr.remove();

        //统计户籍人数, 户籍数
        ph00301_input.calcFamNum();
    },

    /**
     * 增加家庭成员
     * @param obj
     */
    addPerson: function (tabId, obj) {
        var addObj = $(obj);
        $(obj).closest("tr").nextAll("tr").each(function () {
            var $this = $(this);
            var trFamilyPerson = $this.find("input.js_ps_rel");
            if (trFamilyPerson.val() != "户主") {
                addObj = trFamilyPerson;
                return true;
            } else {
                return false;
            }
        });
        var familyPersonId = $(obj).closest("tr").find("input.js_ps_id").val();
        var addRow = table.addRow(tabId, addObj);
        var personId = "${" + new Date().getTime() + "}";
        addRow.find("span.js_family_rht").remove();
        addRow.find("input.js_ps_id").val(personId);
        addRow.find("input.js_ps_family_flag").val("0");
        addRow.find("input.js_ps_family_ps_id").val(familyPersonId);
        addRow.find("input[type=text]:eq(0)").focus();

    },

    /**
     * 删除家庭成员
     * @param obj
     */
    deletePerson: function (obj) {
        table.deleteRow(obj);
        //统计户籍人数, 户籍数
        ph00301_input.calcFamNum();
    },

    /**
     * 普通保存房产信息
     * @param saveFlag
     */
    saveInputInfo: function (saveFlag) {
        //调用通用的保存获取拼装的  ajaxPacket
        var ajaxPacket = ph00301_input.save(saveFlag);
        if (ajaxPacket) {
            ajaxPacket.data.data.push({name: "whetherToDb", value: true});
            // 提交保存请求
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                var hsId = jsonData.hsId;
                var buildId = jsonData.buildId;
                var ttRegId = jsonData.ttRegId;
                if (jsonData.success) {
                    var url = "eland/ph/ph003/ph00301-initHs.gv?hsId=" + hsId + "&buildId=" + buildId + "&regId=" + ttRegId + "&prjCd=" + getPrjCd();
                    index.quickOpen(url, {opCode: "ph00301-initHs", opName: "信息登记", fresh: true});
                    alertMsg.correct("处理成功");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     * 保存信息
     * @param saveFlag
     */
    save: function (saveFlag) {
        // 同步高级
        ph00301_input.syncOwnerInfo();
        var $form = $("#ph00103_inputFrm", navTab.getCurrentPanel());
        if ($form.valid()) {
            var pbT = "";
            $("input[name=hsPbType]:checked").each(function () {
                var hsPbType = $(this).val();
                pbT = pbT + hsPbType + ",";
            });
            $("input[name=saveHsPbType]").val(pbT);
            // 同步产权人信息
            ph00301_input.syncOwnerInfo();
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-saveInputInfo.gv";
            var ajaxPacket = new AJAXPacket(url);

            // 序列化时间处理
            ajaxPacket.data.data = $form.serializeArray();
            // 项目编码
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});

            // 增加人员信息
            var persons = ph00301_input.getAllPsInfo(["familyTable_booklet", "familyTable_owner", "familyTable_live", "advanceOwnerDiv", "js_hs_area_all_div"], null);
            for (var i = 0; i < persons.length; i++) {
                var person = persons[i];
                ajaxPacket.data.data.push({
                    name: "personId",
                    value: person.personId
                });
                ajaxPacket.data.data.push({
                    name: "familyPersonId",
                    value: person.familyPersonId ? person.familyPersonId : ""
                });
                ajaxPacket.data.data.push({
                    name: "familyOwnFlag",
                    value: person.familyOwnFlag ? person.familyOwnFlag : ""
                });
                ajaxPacket.data.data.push({
                    name: "personName",
                    value: person.personName
                });
                ajaxPacket.data.data.push({
                    name: "familyPersonRel",
                    value: person.familyPersonRel ? person.familyPersonRel : ""
                });
                ajaxPacket.data.data.push({
                    name: "personCerty",
                    value: person.personCerty
                });
                ajaxPacket.data.data.push({
                    name: "personTelphone",
                    value: person.personTelphone
                });
                ajaxPacket.data.data.push({
                    name: "personCertyDocIds",
                    value: person.personCertyDocIds ? person.personCertyDocIds : ""
                });
                ajaxPacket.data.data.push({
                    name: "familyDocIds",
                    value: person.familyDocIds ? person.familyDocIds : ""
                });
                ajaxPacket.data.data.push({
                    name: "personLiveAddr",
                    value: person.personLiveAddr ? person.personLiveAddr : ""
                });
                ajaxPacket.data.data.push({
                    name: "familyAddr",
                    value: person.familyAddr ? person.familyAddr : ""
                });
                ajaxPacket.data.data.push({
                    name: "personJobAddr",
                    value: person.personJobAddr ? person.personJobAddr : ""
                });
                ajaxPacket.data.data.push({
                    name: "personAge",
                    value: person.personAge ? person.personAge : ""
                });
                ajaxPacket.data.data.push({
                    name: "isLiveHere",
                    value: person.isLiveHere ? person.isLiveHere : ""
                });
                ajaxPacket.data.data.push({
                    name: "marryStatus",
                    value: person.marryStatus ? person.marryStatus : ""
                });
                ajaxPacket.data.data.push({
                    name: "personType",
                    value: person.personType ? person.personType : ""
                });
            }
            // 保存模式
            ajaxPacket.data.data.push({
                name: "saveFlag",
                value: saveFlag
            });
            // 所有上传附件, 新增房产的时候上传的附件需要进行管理
            var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
            if (hsId == "") {
                var hsDocs = [];
                $("span.js_doc_info", navTab.getCurrentPanel()).each(function () {
                    var $this = $(this);
                    var docId = $this.find("input[type=hidden]").val();
                    if (docId && docId != "") {
                        hsDocs.push(docId);
                    }
                });
                ajaxPacket.data.data.push({
                        name: "hsDocs",
                        value: hsDocs.join(",")
                    }
                );
            }
            return ajaxPacket;
        } else {
            return false;
        }

    },

    /**
     * 房产录入信息审核
     */
    auditing: function () {
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var manualAuditResult = $("input[name=manualAuditResult]:checked", navTab.getCurrentPanel()).val();
        var manualAuditNote = $("textarea[name=manualAuditNote]", navTab.getCurrentPanel()).val();
        if (hsId && hsId != '' && hsId != null) {
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-auditing.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("hsId", hsId);
            packet.data.add("manualAuditResult", manualAuditResult);
            packet.data.add("manualAuditNote", manualAuditNote);
            core.ajax.sendPacketHtml(packet, function (response) {
                var result = $(response);
                var errMsg = result.find("input[name=errMsg]").val();
                if (errMsg == "" || errMsg == undefined) {
                    alertMsg.correct("提交成功");
                    navTab.getCurrentPanel().empty().html(response).initUI();
                } else {
                    alertMsg.error(errMsg);
                }
            }, false);
        }
    },

    /**
     * 房产录入信息审核
     */
    applyAuditing: function () {
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        if (hsId && hsId != '' && hsId != null) {
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-applyAuditing.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("hsId", hsId);
            core.ajax.sendPacketHtml(packet, function (response) {
                var result = $(response);
                var errMsg = result.find("input[name=errMsg]").val();
                if (errMsg == "" || errMsg == undefined) {
                    alertMsg.correct("提交成功");
                    navTab.getCurrentPanel().empty().html(response).initUI();
                } else {
                    alertMsg.error(errMsg);
                }
            }, false);
        }
    },

    /**
     * 打开计算器
     */
    openCalculate: function () {
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        if (hsId == "") {
            alertMsg.warn("请先保存录入数据");
        } else {
            // 打开计算器
            var url = "eland/ph/ph004/ph004-init.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
            index.quickOpen(url, {opCode: "ph004-init", opName: "补偿计算", fresh: true});
        }
    },

    /**
     * 增加新的选房区域
     * @param regId 选房区域编号
     */
    addNewHsArea: function (regId) {
        var containDiv = $("#js_hs_area_all_div", navTab.getCurrentPanel());
        var regIdObj = $("input[name=regId][value=" + regId + "]", containDiv);
        if (regIdObj.length == 0) {
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-initNewHsArea.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("regId", regId);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var resultDom = $(response);
                var hsBuildSize = $("input[name=hsBuildSize]", navTab.getCurrentPanel()).val();
                $("input[name=fpmj]", resultDom).val(hsBuildSize);
                var containDiv = $("#js_hs_area_all_div", navTab.getCurrentPanel());
                containDiv.append(resultDom);
                resultDom.initUI();
                ph00301_input.tipInit(resultDom);
                ph00301_input.calcSize($("input[name=fpmj]", resultDom));
            });
        } else {
            regIdObj.closest("div.js_hs_area_div").find("input[name=hsTypNum]:eq(0)").focus();
        }
    },

    /**
     * 自动计算新增选房的安置面积
     */
    calcSize: function (obj) {
        var hsBuildSize = $("input[name=hsBuildSize]", navTab.getCurrentPanel()).val();
        var $thisTd = $(obj).closest("td");
        var fpmj = $("input[name=fpmj]", $thisTd).val();
        if (fpmj == null || fpmj == 0) {
            $("input[name=fpmj]", $thisTd).val(hsBuildSize);
            fpmj = hsBuildSize;
            //$(obj).addClass("inputWarn").val("分配选房面积不能大于建筑面积");
        }
        var regCoeff = $("input[name=regCoeff]", $thisTd).val();
        var ctHsCanSize = parseFloat(fpmj * regCoeff).toFixed(2);
        $("input[name=yazmj]", $thisTd).val(ctHsCanSize);
        //计算赠送过程
        var js_ctSizeA = 0;
        $("input.js_ctSizeA", $thisTd).each(function () {
            var $this = $(this);
            js_ctSizeA = parseFloat(js_ctSizeA) + parseFloat($this.val());
        });
        var js_ctSizeB = 0;
        $("input.js_ctSizeB", $thisTd).each(function () {
            var $this = $(this);
            js_ctSizeB = parseFloat(js_ctSizeB) + parseFloat($this.val());
        });
        var calcResult = (ctHsCanSize - js_ctSizeA + js_ctSizeB).toFixed(2);
        var calcStr = "";
        if (js_ctSizeA == 0 && js_ctSizeB == 0) {
            calcStr = calcResult;
        } else if (js_ctSizeA == 0 && js_ctSizeB != 0) {
            calcStr = calcResult + " ( " + ctHsCanSize + " + " + js_ctSizeB + " ) ";
        } else if (js_ctSizeA != 0 && js_ctSizeB == 0) {
            calcStr = calcResult + " ( " + ctHsCanSize + " - " + js_ctSizeA + " ) ";
        } else {
            calcStr = calcResult + " ( " + ctHsCanSize + " - " + js_ctSizeA + " + " + js_ctSizeB + " ) ";
        }
        //有赠送，显示详细赠送公式
        $thisTd.parent("tr").find("td.js_ctCanHsSize").text(calcStr);
        $thisTd.parent("tr").find("input[name=lastSize]", $.pdialog.getCurrent()).val(calcResult);

    },

    /**
     * 购房人信息
     * @param obj
     * @returns
     */
    getBuyPsOpt: function (obj) {
        return {
            fetchData: function (clickObj) {
                return ph00301_input.getAllPsInfo(
                    ["familyTable_booklet", "familyTable_owner", "familyTable_live", "advanceOwnerDiv", "js_hs_area_all_div"], $(clickObj).closest("table"),
                    clickObj);
            },
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.personName;
            },
            displayValue: function (value, data) {
                return data.personName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                ph00301_input.syncPerson(data, tr);
                // 人员ID为产权人，人员关系为空，自动未本人
                var buyPersonOwnerRel = $("input[name=buyPersonOwnerRel]", tr);
                if (buyPersonOwnerRel.length > 0) {
                    // 选择购房人
                    var ownObj = $("input.js_ps_id[value=" + data.personId + "]", $("#advanceOwnerDiv", navTab.getCurrentPanel()));
                    if (ownObj.length > 0) {
                        buyPersonOwnerRel.val("本人");
                    }
                } else {
                    /* 选择共同居住人 */
                    var ownObj = $("input.js_ps_id[value=" + data.personId + "]", $("table[id^='familyTable']", navTab.getCurrentPanel()));
                    if (ownObj.length > 0) {
                        // 本户籍人员， 获取购房人编号
                        var familyPersonId = tr.closest("table").find("input[name=buyPersonId]").val();
                        // 购房人不为空，且购房人与户主一致则直接
                        if (familyPersonId != "") {
                            var personInfo = ph00301_input.getPersonInfo(ownObj.closest("tr"));
                            if (personInfo.familyPersonId == familyPersonId) {
                                $("input[name=gtJzPsOwnerRel]", tr).val(personInfo.familyPersonRel);
                            }
                        }
                    }
                }

                tr.find("input").each(function () {
                    ph00301_input.flashInputRule($(this))
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                var personId = "${ps_id" + new Date().getTime() + "}";
                tr.find("input.js_ps_id").val(personId);
            }
        }
    },

    ///**
    // * 处理购房现住址 自动补全
    // */
    //getNowHsAddr: function (obj) {
    //    //获取现住址
    //    var buildAddr = $("input[name=buildAddr]", navTab.getCurrentPanel()).val();
    //    var buildNo = $("input[name=buildNo]", navTab.getCurrentPanel()).val();
    //    if (buildAddr && buildNo) {
    //        $(obj).val(buildAddr + buildNo);
    //    }
    //},

    /**
     * 获取地址列表
     * @param obj
     * @returns {string}
     */
    getAddrUrl: function (obj) {
        return getGlobalPathRoot() + "eland/ph/ph003/ph00301-queryAllHsAddr.gv?prjCd=" + getPrjCd();
    },
    /**
     * 自动填充地址信息
     * @param obj
     * @returns {String} hsFullAddr
     */
    getAddrOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].hsFullAddr;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.hsFullAddr;
            },
            displayValue: function (value, data) {
                return data.hsFullAddr;
            },
            dyExtraParams: function (obj) {
                return {
                    newHsAddr: $("input.js_live_addr:last").val(),
                    prjCd: getPrjCd()
                }
            },
            onItemSelect: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input.js_live_addr").val(obj.data.hsFullAddr);
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            queryParamName: "newHsAddr",
            filterResults: true
        }
    },

    /**
     * 产权信息高级录入人员信息
     * @param obj
     * @returns
     */
    getBuyPsWtOpt: function (obj) {
        return {
            fetchData: function (clickObj) {
                return ph00301_input.getAllPsInfo(
                    ["familyTable_booklet", "familyTable_owner", "familyTable_live", "advanceOwnerDiv"], $(clickObj).closest("tr").prev("tr"),
                    clickObj);
            },
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.personName;
            },
            displayValue: function (value, data) {
                return data.personName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                ph00301_input.syncPerson(data, tr);
                tr.find("input").each(function () {
                    ph00301_input.flashInputRule($(this))
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                var personId = "${ps_id" + new Date().getTime() + "}";
                tr.find("input.js_ps_id").val(personId);
            }
        }
    },

    /**
     * 增加新的房屋居室
     * @param clickSpan 点击的span对象
     */
    addNewHs: function (clickSpan) {
        // 数据准备
        var hsType = $(clickSpan).parent().find("input[name=hsType]").val();
        var chooseHsRegId = $(clickSpan).parent().find("input[name=regId]").val();
        var numObj = $(clickSpan).parent().find("input.js_num");
        var hsTypeName = $(clickSpan).parent().find("input[name=hsTypeName]").val();
        var hsAreaDiv = $(clickSpan).closest("div.js_hs_area_div");
        var showTable = hsAreaDiv.find("table.js_hs_buy_info_table");
        // 居室处理
        var addHsType = showTable.find("input[name=hsType][value=" + hsType + "]");
        var templateInfo = null;
        if (addHsType.length == 0) {
            // 增加居室数据
            var templateTr = showTable.find("tr:eq(0)");
            var addJsTr = templateTr.clone();
            addJsTr.removeClass("hidden").appendTo(showTable);
            // 增加新的行
            addJsTr.find("input[name=hsType]").val(hsType);
            addJsTr.find("label.js_js_name").html(hsTypeName);
            // 购房人内容显示模板
            templateInfo = addJsTr.find("div.js_hs_buy_info:eq(0)");
        } else {
            // 购房人内容显示模板
            templateInfo = addHsType.closest("tr").find("div.js_hs_buy_info:eq(0)");
        }
        // 增加
        var addTable = templateInfo.clone().removeClass("hidden");
        addTable.find("input").val("");
        addTable.find("input[name=chooseHsTp]").val(hsType);
        //给定默认现住址
        var buildAddr = $("input[name=buildAddr]", navTab.getCurrentPanel()).val();
        var buildNo = $("input[name=buildNo]", navTab.getCurrentPanel()).val();
        if (buildAddr != "" && buildNo != "" | buildAddr != null && buildNo != null | buildAddr != undefined && buildNo != undefined) {
            addTable.find("input.js_live_addr").val(buildAddr + buildNo + "号");
        } else {
            addTable.find("input.js_live_addr").val();
        }
        addTable.find("input[name=chooseHsRegId]").val(chooseHsRegId);
        initUI(addTable);
        templateInfo.parent().append(addTable);
        // 数量+1
        numObj.val(parseInt(numObj.val()) + 1)
    },

    delNewHs: function (clickSpan) {
        var currentInfo = $(clickSpan).closest("div.js_hs_buy_info");
        var hsType = currentInfo.closest("tr").find("input[name=hsType]").val();
        var summaryTable = currentInfo.closest("div.js_hs_area_div").find("table.js_summary_table");
        var connectHsTypeNumObj = summaryTable.find("input[name=hsType][value=" + hsType + "]").parent().find("input.js_num");
        var newCount = parseInt(connectHsTypeNumObj.val()) - 1;
        if (newCount < 0) {
            newCount = 0;
        }
        connectHsTypeNumObj.val(newCount);
        if (newCount == 0) {
            currentInfo.closest("tr").remove();
        } else {
            currentInfo.remove();
        }
    },

    /**
     * 处理委托
     * @param clickSpan
     */
    changeWt: function (clickSpan) {
        var wtStatus = $.trim($(clickSpan).html());
        var clickTr = $(clickSpan).closest("tr");
        var wtTr = clickTr.nextAll("tr.js_wt_info");
        if ("委托" == wtStatus) {
            // 当前操作委托
            wtStatus = "取消委托";
            wtTr.removeClass("hidden");
        } else {
            // 当前取消委托
            wtStatus = "委托";
            wtTr.find("input").val("");
            wtTr.addClass("hidden");
        }
        $(clickSpan).html(wtStatus);
    },
    /**
     * 增加共同居住人
     * @param clickSpan 点击对象
     */
    addLivePs: function (clickSpan) {
        var clickTable = $(clickSpan).closest("table");
        var templateTr = clickTable.find("tr:last");
        var newTr = templateTr.clone().initUI();
        newTr.find("input").val("");
        newTr.find("span.js_live_span").removeClass("hidden");
        clickTable.append(newTr);
    },
    /**
     * 删除共同居住人
     * @param clickObj 删除的对象
     */
    delLivePs: function (clickObj) {
        var clickTable = $(clickObj).closest("table");
        $(clickObj).closest("tr").remove();
    },
    /**
     * 同步共同居住人编号数据
     */
    flashGtJzPsIds: function () {

    },

    /**
     * 增加产权人
     */
    addOwner: function () {
        var jsOwnerDiv = $("div.js_owner_div:last", navTab.getCurrentPanel());
        if (jsOwnerDiv) {
            var newJsOwnerDiv = jsOwnerDiv.clone();
            newJsOwnerDiv.find("input").val("");
            newJsOwnerDiv.insertAfter(jsOwnerDiv);
            newJsOwnerDiv.initUI();
            //增加的产权人大于两个显示删除按钮。
            var ownerCount = $("div.js_owner_div", navTab.getCurrentPanel()).length;
            if (ownerCount >= 2) {
                $("span.removeX", $("div.js_owner_div", navTab.getCurrentPanel())).show();
            }
        }
        ph00301_input.switchOwnerType();
    },
    /**
     * 删除产权人
     * @param clickObj 点击对象
     */
    deleteOwner: function (clickObj) {
        var ownerCount = $("div.js_owner_div", navTab.getCurrentPanel()).length;
        if (ownerCount >= 2) {
            $(clickObj).parents("div.js_owner_div").remove();
            ph00301_input.syncOwnerDisp();
        }
        //当删除的产权人剩下一个的时候，隐藏删除按钮，默认留一个。
        var ownerDiv = $("div.js_owner_div", navTab.getCurrentPanel());
        if (ownerDiv.length == 1) {
            $("span.removeX", $("div.js_owner_div", navTab.getCurrentPanel())).hide();
        }
    },
    /**
     * 产权信息录入模式切换
     */
    switchModel: function (ownerModel) {
        // 切换到普通输入模式
        var advanceOwnerDiv = $("#advanceOwnerDiv", navTab.getCurrentPanel());
        if (ownerModel == "0") {
            advanceOwnerDiv.addClass("hidden");
            // 录入了多个产权人，不允许调整修改
            var ownerNum = advanceOwnerDiv.find("input[name='ownerName'][value!='']").length;
            var ownerTable = $("#ownerTable", navTab.getCurrentPanel());
            if (ownerNum <= 1) {
                ownerTable.find("input.js_owner_display").removeClass("readonly").removeAttr("readonly");
                ownerTable.find("input.js_owner_display:eq(0)").focus();
                ownerTable.find("input.js_ps_certy_doc").closest("span").attr("editAble", true);
                ownerTable.find("input.js_owner_certy_doc").closest("span").attr("editAble", true);
            } else {
                ownerTable.find("input.js_owner_display").addClass("readonly").attr("readonly", "true");
                ownerTable.find("input.js_ps_certy_doc").closest("span").attr("editAble", "false");
                ownerTable.find("input.js_owner_certy_doc").closest("span").attr("editAble", "false");
            }
            // 产权信息显示同步
            ph00301_input.syncOwnerDisp();
        } else {
            // 高级输入模式
            advanceOwnerDiv.removeClass("hidden");
            // 产权信息同步
            ph00301_input.syncOwnerInfo();
            // 原有的产权录入框不可用
            var ownerTable = $("#ownerTable", navTab.getCurrentPanel());
            ownerTable.find("input.js_owner_display").addClass("readonly").attr("readonly", "true");
            ownerTable.find("input.js_ps_certy_doc").closest("span").attr("editAble", "false");
            ownerTable.find("input.js_owner_certy_doc").closest("span").attr("editAble", "false");
            // 获取产权模式
            var hsOwnerModel = advanceOwnerDiv.find("select[name='hsOwnerModel']").val();
            if (hsOwnerModel == 10) {
                $("#addOwnerBtn", advanceOwnerDiv).addClass("hidden");
                // 定位到第一个输入框
                advanceOwnerDiv.find("input[name=ownerPrecent]:eq(0)").val("100");
            } else {
                $("#addOwnerBtn", advanceOwnerDiv).removeClass("hidden");
            }
            // 定位到第一个输入框
            advanceOwnerDiv.find("input.js_ps_name:eq(0)").focus();
        }
    },

    /**
     * 修改产权公有模式
     */
    switchOwnerType: function () {
        var advanceOwnerDiv = $("#advanceOwnerDiv", navTab.getCurrentPanel());
        var hsOwnerModel = advanceOwnerDiv.find("select[name='hsOwnerModel']").val();
        var ownerNum = advanceOwnerDiv.find("input[name='ownerName']").length;
        if (hsOwnerModel == "10") {
            if (ownerNum != 1) {
                alertMsg.warn("输入了多个产权人，请删除错误的产权人信息");
            } else {
                advanceOwnerDiv.find("input[name=ownerPrecent]:eq(0)").val("100");
            }
            $("#addOwnerBtn", advanceOwnerDiv).addClass("hidden");
        } else if (hsOwnerModel == "11") {
            advanceOwnerDiv.find("input[name=ownerPrecent]").val("1/" + ownerNum);
            $("#addOwnerBtn", advanceOwnerDiv).removeClass("hidden");
        } else {
            $("#addOwnerBtn", advanceOwnerDiv).removeClass("hidden");
        }
    },

    /**
     * 高级产权输入，刷新产权信息展示
     */
    syncOwnerDisp: function () {
        // 切换到普通输入模式
        var advanceOwnerDiv = $("#advanceOwnerDiv", navTab.getCurrentPanel());
        var psIds = [];
        // 人员名称
        var psNames = [];
        var psNamesChanged = false;
        // 人员证件号码
        var psCertys = [];
        var psCertysChanged = false;
        // 人员联系电话
        var psTels = [];
        var psTelsChanged = false;
        // 产权证件
        var ownerCertys = [];
        var ownerCertysChanged = false;
        // 人员证件附件
        var psCertysDoc = [];
        var psCertysDocChanged = false;
        // 产权证件附件
        var ownerCertyDocs = [];
        var ownerCertyDocsChanged = false;
        advanceOwnerDiv.find("input[name=ownerName].js_ps_name").each(function () {
            var table = $(this).closest("table");
            // 获取人员名称
            var psNameObj = table.find("input.js_ps_name");
            var psName = psNameObj.val();
            if (psName != "") {
                psNames.push(psName);
            }
            if (psNameObj.hasClass("isEdit")) {
                psNamesChanged = true;
            }

            // 产权人id
            psIds.push(table.find("input.js_ps_id").val());

            // 产权人证件号码
            var psCertyObj = table.find("input.js_ps_certy");
            psCertys.push(psCertyObj.val());
            if (psCertyObj.hasClass("isEdit")) {
                psCertysChanged = true;
            }

            // 产权人联系电话
            var psTelObj = table.find("input.js_ps_tel");
            psTels.push(psTelObj.val());
            if (psTelObj.hasClass("isEdit")) {
                psTelsChanged = true;
            }

            // 产权证号
            var ownerCertyObj = table.find("input.js_owner_certy");
            ownerCertys.push(ownerCertyObj.val());
            if (ownerCertyObj.hasClass("isEdit")) {
                ownerCertysChanged = true;
            }

            // 身份证附件
            var psCertyDocObj = table.find("input.js_ps_certy_doc");
            var tempVal = psCertyDocObj.val();
            if (tempVal != "") {
                psCertysDoc.push(tempVal);
            }
            if (psCertyDocObj.hasClass("isEdit")) {
                psCertysDocChanged = true;
            }

            // 产权证附件
            var ownerCertyDocObj = table.find("input.js_owner_certy_doc");
            var tempCertyVal = ownerCertyDocObj.val();
            if (tempCertyVal != "") {
                ownerCertyDocs.push(tempCertyVal);
            }
            if (ownerCertyDocObj.hasClass("isEdit")) {
                ownerCertyDocsChanged = true;
            }
        });
        var ownerTable = $("#ownerTable", navTab.getCurrentPanel());
        ownerTable.find("input.js_ps_id").val(psIds.join(","));
        // 同步名称
        var ownerPsNameObj = ownerTable.find("input.js_ps_name");
        ownerPsNameObj.val(psNames.join(","));
        if (psNamesChanged) {
            ownerPsNameObj.addClass("isEdit");
        }
        // 同步产权人身份证
        var ownerPsCertyObj = ownerTable.find("input.js_ps_certy");
        ownerPsCertyObj.val(psCertys.join(","));
        if (psCertysChanged) {
            ownerPsCertyObj.addClass("isEdit");
        }
        // 同步联系电话
        var ownerPsTel = ownerTable.find("input.js_ps_tel");
        ownerPsTel.val(psTels.join(","));
        if (psTelsChanged) {
            ownerPsTel.addClass("isEdit");
        }
        // 同步产权证件号码
        var ownerCertyObj = ownerTable.find("input.js_owner_certy");
        ownerCertyObj.val(ownerCertys.join(","));
        if (ownerCertysChanged) {
            ownerCertyObj.addClass("isEdit");
        }
        // 同步产权人身份证附件
        var ownerPsCertyDocObj = ownerTable.find("input.js_ps_certy_doc");
        ownerPsCertyDocObj.val(psCertysDoc.join(","));
        if (psCertysDocChanged) {
            ownerPsCertyDocObj.addClass("isEdit");
        }
        // 产权证附件
        var ownerCertyDocObj = ownerTable.find("input.js_owner_certy_doc");
        ownerCertyDocObj.val(ownerCertyDocs.join(","));
        if (ownerCertyDocsChanged) {
            ownerCertyDocObj.addClass("isEdit");
        }

        // 刷新显示
        $("input[type=text], textarea, select", ownerTable).each(function () {
            ph00301_input.flashInputRule($(this));
        });
    },

    /**
     * 高级产权输入，刷新产权信息展示
     */
    syncOwnerInfo: function () {
        // 切换到普通输入模式
        var advanceOwnerDiv = $("#advanceOwnerDiv", navTab.getCurrentPanel());
        var ownerDisTable = $("#ownerTable", navTab.getCurrentPanel());
        if (!ownerDisTable.find("input.js_ps_name").hasClass("readonly")) {
            var showTable = advanceOwnerDiv.find("input[name=ownerName]:eq(0)").closest("table");
            var showTr = advanceOwnerDiv.find("input[name=ownerName]:eq(0)").closest("tr");
            showTr.find("input.js_ps_id").val(ownerDisTable.find("input.js_ps_id").val());
            showTr.find("input.js_ps_name").val(ownerDisTable.find("input.js_ps_name").val());
            showTr.find("input.js_ps_certy").val(ownerDisTable.find("input.js_ps_certy").val());
            showTr.find("input.js_ps_tel").val(ownerDisTable.find("input.js_ps_tel").val());
            showTr.find("input.js_ps_certy_doc").val(ownerDisTable.find("input.js_ps_certy_doc").val());
            showTable.find("input.js_owner_certy").val(ownerDisTable.find("input.js_owner_certy").val());
            showTable.find("input.js_owner_certy_doc").val(ownerDisTable.find("input.js_owner_certy_doc").val());
            $("input[type=text], textarea, select", showTable).each(function () {
                ph00301_input.flashInputRule($(this));
            });
        }
    },

    /**
     * 事件处理
     */
    flashInputRule: function (obj) {
        var checkObj = $(obj);
        var objValue = $(obj).val();
        if (checkObj.hasClass("ctRequired")) {
            if (objValue == "") {
                checkObj.addClass("inputWarn");
            } else {
                checkObj.removeClass("inputWarn");
            }
        }
        if (checkObj.hasClass("needDoc")) {
            var checkValue = "";
            if (checkObj.hasClass("number") || checkObj.hasClass("digits")) {
                if (objValue == "") {
                    objValue = 0;
                } else {
                    objValue = parseFloat(objValue);
                }
                checkValue = 0;
            }
            var spanInfo = checkObj.next("span.js_doc_info");
            if (spanInfo.length > 0) {
                if (objValue == checkValue) {
                    spanInfo.removeClass("docWarn").addClass("hidden");
                } else {
                    spanInfo.removeClass("hidden");
                    var docIds = spanInfo.find("input[type=hidden]").val();
                    if (docIds == "") {
                        spanInfo.addClass("docWarn").removeClass("hidden");
                        spanInfo.find("label").html("上传");
                    } else {
                        spanInfo.removeClass("docWarn");
                        spanInfo.find("label").html("查看");
                    }
                }
            }
        }
    },
    tipInit: function (containObj) {
        $("input[type=text], textarea, select", containObj).each(function () {
            ph00301_input.flashInputRule($(this));
            $(this).blur(function () {
                ph00301_input.flashInputRule($(this));
            })
        });
        // 附件上传提示
        var docInfoSpan = $("span.js_doc_info", navTab.getCurrentPanel());
        docInfoSpan.each(function () {
            var $this = $(this);
            var hiddenDocIdObj = $this.find("input[type=hidden]");
            var docId = hiddenDocIdObj.val();
            if (docId == "") {
                $this.addClass("docWarn");
            }
            if (hiddenDocIdObj.hasClass("isEdit")) {
                $this.addClass("isEdit");
            }
        });
        docInfoSpan.bind("mouseover", function () {
            var $this = $(this);
            var docId = $this.find("input[type=hidden]").val();
            var browserLink = $this.find("a.js_browser");
            if (docId != "") {
                if (browserLink.length == 0) {
                    browserLink = $("<a class='js_browser'>浏览</a>");
                    browserLink.bind("click", function () {
                        var $this = $(this);
                        var docId = $this.parent().find("input[type=hidden]").val();
                        if (docId != "") {
                            ImgPage.viewPicsDiag(docId);
                        }
                        stopEvent(event);
                    });
                    $this.append(browserLink);
                }
            }
        });
        docInfoSpan.bind("mouseout", function () {
            var $this = $(this);
            var docId = $this.find("input[type=hidden]").val();
            var browserLink = $this.find("a.js_browser");
            if (browserLink.length > 0) {
                setTimeout(function () {
                    browserLink.remove();
                }, 1000);
            }
        });
    },

    /**
     * 初始化展示页面展示
     */
    initOwnerDisp: function () {
        var advanceOwnerDiv = $("#advanceOwnerDiv", navTab.getCurrentPanel());
        var ownerNum = advanceOwnerDiv.find("input[name='ownerName'][value!='']").length;
        var ownerTable = $("#ownerTable", navTab.getCurrentPanel());
        if (ownerNum <= 1) {
            ownerTable.find("input.js_owner_display").removeClass("readonly").removeAttr("readonly");
        } else {
            ownerTable.find("input.js_owner_display").addClass("readonly").attr("readonly", "true");
            ownerTable.find("input.js_ps_certy_doc").closest("span").attr("editAble", "false");
            ownerTable.find("input.js_owner_certy_doc").closest("span").attr("editAble", "false");
        }
        ph00301_input.syncOwnerDisp();
    },

    /**
     * 增加委托关系
     * @param clickObj
     */
    addOwnerWt: function (clickObj) {
        $(clickObj).closest('tr').next('tr').removeClass('hidden');
        $(clickObj).addClass("hidden");
    },

    /**
     * 取消产权人委托关系
     * @param clickObj
     */
    delOwnerWt: function (clickObj) {
        var trObj = $(clickObj).closest('tr');
        trObj.addClass('hidden').find("input").val("");
        trObj.prev("tr").find("span.js_add_wt").removeClass("hidden");
    },

    /**
     * 增加房产的信息录入规则
     */
    initRule: function () {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-queryHsRule.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    var data = jsonData.data.OpResult.EntityAttr;
                    var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
                    var needDefault = true;
                    if (hsId != "") {
                        needDefault = false;
                    }
                    if (data) {
                        for (var i = 0; i < data.length; i++) {
                            var temp = data[i];
                            var attrName = temp.attrName;
                            var obj = $("input[type=input][name=" + attrName + "]", navTab.getCurrentPanel());
                            if (obj) {
                                var attrDefValue = temp.attrDefValue;
                                var attrRuleDesc = temp.attrRuleDesc;
                                if (needDefault && attrDefValue != "") {
                                    obj.val(attrDefValue);
                                }
                                if (!obj.attr("title") && typeof attrRuleDesc === 'string' && attrRuleDesc != "") {
                                    obj.attr("title", attrRuleDesc);
                                }
                            }
                        }
                    }
                    jsonData = null;
                    // 提示信息
                    ph00301_input.tipInit(navTab.getCurrentPanel());
                }
            }
        )
    },

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));

        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
            + "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=100&relId=" + hsId;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            close: ph00301_input.docClosed,
            param: {clickSpan: $span}
        });
    },

    /**
     * 关闭上传窗口
     * @param param
     * @returns {boolean}
     */
    docClosed: function (param) {
        var uploadedFiles = file.getAllUploadFiles();
        var $span = param.clickSpan;
        var docIdArr = [];
        for (var i = 0; i < uploadedFiles.length; i++) {
            var uploadedFile = uploadedFiles[i];
            docIdArr.push(uploadedFile.docId);
        }
        $span.find("input[type=hidden]").val(docIdArr.join(","));
        if (docIdArr.length > 0) {
            var label = $span.find("label");
            if (label.html() == "上传") {
                label.html("查看");
            }
            $span.removeClass("docWarn");
        } else {
            var label = $span.find("label");
            if (label.html() == "查看") {
                label.html("上传");
            }
            $span.addClass("docWarn");
        }
        // 判断是否人员相关附件
        var tempObj = $span.closest("tr").find("input.js_ps_id");
        if (tempObj.length > 0) {
            ph00301_input.syncPersons($span);
        }
        // 调用文件的关闭
        return file.closeD();
    },

    showAndSyncDoc: function (clickObj) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));
        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName +
            "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=100&relId=" + hsId;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            close: editAble ? ph00301_input.docClosedSync : "",
            param: {clickSpan: $span}
        });
    },

    /**
     * 关闭上传并同步数据导异常区域
     * @param param
     * @returns {boolean}
     */
    docClosedSync: function (param) {
        var uploadedFiles = file.getAllUploadFiles();
        var $span = param.clickSpan;
        var docIdArr = [];
        for (var i = 0; i < uploadedFiles.length; i++) {
            var uploadedFile = uploadedFiles[i];
            docIdArr.push(uploadedFile.docId);
        }
        $span.find("input[type=hidden]").val(docIdArr.join(","));
        if (docIdArr.length > 0) {
            $span.removeClass("docWarn");
        }
        ph00301_input.syncOwnerInfo();
        // 判断是否人员相关附件
        var tempObj = $span.closest("tr").find("input.js_ps_id");
        if (tempObj.length > 0) {
            ph00301_input.syncPersons($span);
        }
        return true;
    },

    /**
     * --------------------------------------地图部分-----------------------------------------------
     **/

    /**
     * 设置建筑位置坐标
     */
    setBuildPos: function (obj) {
        //var $build = $(obj);
        var buildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-openBuildMap.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        EMap.getMapInitData = function () {
            var tempArr = [];
            //将修改的值赋予腾退录入主页面。
            tempArr.push($("input[name=buildPosition]", navTab.getCurrentPanel()).val());
            return tempArr;
        };
        EMap.cfmDraCallBack = function (dataArr) {
            //将修改的值赋予腾退录入主页面。
            $("input[name=buildPosition]", navTab.getCurrentPanel()).val(dataArr[0]);
            //为中心点赋值 入库
            $("input[name=buildPositionX]", navTab.getCurrentPanel()).val(dataArr[1]);
            $("input[name=buildPositionY]", navTab.getCurrentPanel()).val(dataArr[2]);
            return true;
        };
        $.pdialog.open(url, "ph00301", "院落地图", {
            mask: true, max: true
        });
    },


    /**
     * 设置房产位置坐标
     */
    setHsPos: function (obj) {
        //var $hs = $(obj);
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var buildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-openHsMap.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId + "&buildId=" + buildId;
        EMap.getMapInitData = function () {
            var tempArr = [];
            //将修改的值赋予腾退录入主页面。
            tempArr.push($("input[name=hsPoints]", navTab.getCurrentPanel()).val());
            tempArr.push($("input[name=hsSlfPoints]", navTab.getCurrentPanel()).val());
            return tempArr;
        };
        EMap.cfmDraCallBack = function (dataArr) {
            //将修改的值赋予腾退录入主页面。
            $("input[name=hsPoints]", navTab.getCurrentPanel()).val(dataArr[0]);
            $("input[name=hsSlfPoints]", navTab.getCurrentPanel()).val(dataArr[1]);
            //为中心点赋值 入库
            $("input[name=hsPosX]", navTab.getCurrentPanel()).val(dataArr[2]);
            $("input[name=hsPosY]", navTab.getCurrentPanel()).val(dataArr[3]);
            return true;
        };
        $.pdialog.open(url, "ph00301", "房产地图", {
            mask: true, max: true
        });
    },

    /**
     * 关闭院落地图修改页
     *  */



    /**
     * 获取人员信息
     * @param personContain 人员信息容器
     * @returns 人员信息Json对象
     */
    getPersonInfo: function (personContain) {
        var currTable = personContain.closest("table");
        var person = {};
        person.personId = personContain.find("input.js_ps_id").val();
        person.personName = personContain.find("input.js_ps_name").val();
        person.personCerty = personContain.find("input.js_ps_certy").val();
        person.personTelphone = personContain.find("input.js_ps_tel").val();
        person.personJobAddr = personContain.find("input.js_ps_job").val();
        person.personAge = personContain.find("input.js_ps_age").val();
        person.isLiveHere = personContain.find("select.js_ps_is_live").val();
        person.marryStatus = personContain.find("select.js_ps_marry").val();
        // 1、在册  2、非本址  3、现场居住
        var tabId = currTable.attr("id");
        if (tabId == 'familyTable_booklet') {
            person.personType = '1';
        } else if (tabId == 'familyTable_owner') {
            person.personType = '2';
        } else if (tabId == 'familyTable_live') {
            person.personType = '3';
        }

        var tempObj = personContain.find("input.js_ps_family_ps_id");
        if (tempObj.length > 0) {
            person.familyPersonId = tempObj.val();
        }
        tempObj = personContain.find("input.js_ps_family_flag");
        if (tempObj.length > 0) {
            person.familyOwnFlag = tempObj.val();
        }
        var tempObj = personContain.find("input.js_ps_family_ps_id");
        if (tempObj.length > 0) {
            person.familyPersonId = tempObj.val();
        }
        // 与户主关系
        tempObj = personContain.find("input.js_ps_rel");
        if (tempObj.length > 0) {
            person.familyPersonRel = tempObj.val();
        }
        // 身份证信息
        tempObj = personContain.find("input.js_ps_certy_doc");
        if (tempObj.length > 0) {
            person.personCertyDocIds = tempObj.val();
        }
        // 户口本信息
        tempObj = personContain.find("input.js_family_doc");
        if (tempObj.length > 0) {
            person.familyDocIds = tempObj.val();
        }
        // 人员其他信息
        //var moreTr = personContain.next("tr");
        //if (moreTr.hasClass("js_ps_more")) {
        // 现住址
        tempObj = personContain.find("input.js_live_addr");
        if (tempObj.length > 0) {
            person.personLiveAddr = tempObj.val();
        }
        // 户口所在地
        tempObj = personContain.find("input.js_family_addr");
        if (tempObj.length > 0) {
            person.familyAddr = tempObj.val();
        }
        // 工作单位
        tempObj = personContain.find("input.js_job_addr");
        if (tempObj.length > 0) {
            person.personJobAddr = tempObj.val();
        }
        //}

        // 返回人员信息
        return person;
    },
    /**
     * 刷新人员数据
     * @param personData
     * @param syncTo
     */
    syncPerson: function (personData, syncTo) {
        syncTo.find("input.js_ps_id").val(personData.personId);
        syncTo.find("input.js_ps_name").val(personData.personName);
        syncTo.find("input.js_ps_certy").val(personData.personCerty);
        syncTo.find("input.js_ps_tel").val(personData.personTelphone);
        var tempObj = syncTo.find("input.js_ps_family_ps_id");
        if (tempObj.length > 0 && personData.familyPersonId != undefined) {
            tempObj.val(personData.familyPersonId);
        }
        tempObj = syncTo.find("input.js_ps_family_flag");
        if (tempObj.length > 0 && personData.familyOwnFlag != undefined) {
            tempObj.val(personData.familyOwnFlag);
        }
        tempObj = syncTo.find("input.js_ps_rel");
        if (tempObj.length > 0 && personData.familyPersonRel != undefined) {
            tempObj.val(personData.familyPersonRel);
        }
        // 身份证信息
        tempObj = syncTo.find("input.js_ps_certy_doc");
        if (tempObj.length > 0 && personData.personCertyDocIds != undefined) {
            tempObj.val(personData.personCertyDocIds);
        }
        // 户口本信息
        tempObj = syncTo.find("input.js_family_doc");
        if (tempObj.length > 0 && personData.familyDocIds != undefined) {
            tempObj.val(personData.familyDocIds);
        }
        // 人员其他信息
        //var moreTr = syncTo.next("tr");
        //if (moreTr.hasClass("js_ps_more")) {
        // 现住址
        tempObj = syncTo.find("input.js_live_addr");
        if (tempObj.length > 0 && personData.personLiveAddr != undefined) {
            tempObj.val(personData.personLiveAddr);
        }
        // 户口所在地
        tempObj = syncTo.find("input.js_family_addr");
        if (tempObj.length > 0 && personData.familyAddr != undefined) {
            tempObj.val(personData.familyAddr);
        }
        // 工作单位
        tempObj = syncTo.find("input.js_job_addr");
        if (tempObj.length > 0 && personData.personJobAddr != undefined) {
            tempObj.val(personData.personJobAddr);
        }
        //}
    },

    /**
     * 根据同步对象所在的人员信息，同步具有相同人员编号的数据中
     * @param syncObj 同步对象
     */
    syncPersons: function (syncObjIn) {
        // 同步对象
        var syncTr = $(syncObjIn).closest("tr");
        if (syncTr.hasClass("js_ps_more")) {
            syncTr = syncTr.prev("tr");
        }
        // 获取人员信息
        var person = ph00301_input.getPersonInfo(syncTr);
        // 人员信息
        if (person.personId != "") {
            $("input.js_ps_id", navTab.getCurrentPanel()).each(
                function () {
                    if ($(this).val() == person.personId) {
                        var syncToTr = $(this).closest("tr");
                        ph00301_input.syncPerson(person, syncToTr);
                    }
                }
            );
        }
    },

    /**
     * 根据身份证计算年龄
     */
    checkIdCalcAge: function (obj) {
        var certyObj = $(obj);
        //获取输入身份证号码   简单验证15位 和18位身份证
        if (/(^\d{15}$)|(^\d{18}$)|(^\d{17}([0-9]|X|x)$)/.test(certyObj.val())) {
            var certyStr = certyObj.val();
            //获取出生日期
            //var birthday = certyStr.substring(6, 10) + "-" + certyStr.substring(10, 12) + "-" + certyStr.substring(12, 14);
            //获取性别
            //if (parseInt(certyStr.substr(16, 1)) % 2 == 1) {
            //   //男  后续处理代码
            //} else {
            //女  自定义处理
            //}
            //获取年龄
            var myDate = new Date();
            var month = myDate.getMonth() + 1;
            var day = myDate.getDate();
            var age = myDate.getFullYear() - certyStr.substring(6, 10) - 1;
            if (certyStr.substring(10, 12) < month || certyStr.substring(10, 12) == month && certyStr.substring(12, 14) <= day) {
                age++;
            }
            //年龄 age
            var currentTr = certyObj.closest("tr");
            $("input.js_ps_age", currentTr).val(age);
        }
        //else {
        //    alertMsg.warn("您输入的身份证不合法，请检查确认！");
        //}
    },

    showAddr: function (obj) {
        var $this = $(obj);
        var $hsAddrFull = $(obj).closest("span.js_showResult");
        var buildAddr = $hsAddrFull.find("input[name=buildAddr]", navTab.getCurrentPanel()).val();
        if (getPrjCd() == '400') {
            if (buildAddr.startWith("/")) {
                buildAddr = buildAddr.substring(1);
            }
            buildAddr = buildAddr.substring(buildAddr.indexOf("/"));
        }
        buildAddr = buildAddr.replaceAll("/", "");
        var buldUnitNum = $hsAddrFull.find("input[name=buldUnitNum]", navTab.getCurrentPanel()).val();
        var hsAddrTail = $hsAddrFull.find("input[name=hsAddrTail]", navTab.getCurrentPanel()).val();
        var fullAddrObj = $this.closest("table").find("input.js_showFullAddr");
        if (fullAddrObj.val() == "") {
            fullAddrObj.val(buildAddr + buldUnitNum + hsAddrTail);
        }
    },

    /**
     * 判断是否是问题户
     * @param obj
     */
    isPbHs: function (obj) {
        if ($("input[name=isPbHs]").attr("checked")) {
            $(".js_pb_hs").show();
        } else {
            $(".js_pb_hs").hide();
        }

    },

    /**
     * 刷新 房产录入界面信息
     * @param hsId
     */
    flushInputInfo: function (hsId) {
        if (hsId && hsId != '' && hsId != null) {
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-initHs.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("hsId", hsId);
            packet.noCover = true;
            core.ajax.sendPacketHtml(packet, function (response) {
                var result = $(response);
                var errMsg = result.find("input[name=errMsg]").val();
                if (errMsg == "" || errMsg == undefined) {
                    navTab.getCurrentPanel().empty().html(response).initUI();
                } else {
                    alertMsg.error(errMsg);
                }
            }, true);
        }
    }
};
$(function () {
    // 产权信息修改
    ph00301_input.onHsOwnerTypeChange();
    // 房产类型修改
    ph00301_input.onBuildTypeChange();
    // 煤改电费用
    ph00301_input.changeMGd();
    // 显示产权人展示
    ph00301_input.initOwnerDisp();
    // 区域页面实体填写规则
    ph00301_input.initRule();
    //计算户籍人数， 户籍数
    ph00301_input.calcFamNum();
    //判断hsCd是否重复
    ph00301_input.queryHsCd();

    $("input[name=fpmj]", navTab.getCurrentPanel()).each(function () {
        ph00301_input.calcSize(this);
    });

    // 绑定人员信息
    $("#ph00103_inputFrm", navTab.getCurrentPanel()).delegate("input.js_ps", "blur", function () {
        ph00301_input.syncPersons(this);
    });
});