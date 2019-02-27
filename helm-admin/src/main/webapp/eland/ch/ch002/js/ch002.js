/**
 * User: shfb_wang
 * Date: 2015/11/10 0010 14 53
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
var ch002 = {
    /**
     * 增加新的选房区域
     * @param regId 选房区域编号
     */
    addNewHsArea: function (regId) {
        var containDiv = $("#js_all_choose_hs_div", $.pdialog.getCurrent());
        var regIdObj = $("input[name=regId][value=" + regId + "]", containDiv);
        if (regIdObj.length == 0) {
            var url = getGlobalPathRoot() + "eland/ch/ch002/ch002-initNewHsArea.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("regId", regId);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var resultDom = $(response);
                var hsBuildSize = $("input[name=hsBuildSize]", $.pdialog.getCurrent()).val();
                $("input[name=fpmj]", resultDom).val(hsBuildSize);
                var containDiv = $("#js_all_choose_hs_div", $.pdialog.getCurrent());
                containDiv.append(resultDom);
                resultDom.initUI();
                ch002.tipInit(resultDom);
                ch002.calcSize($("input[name=fpmj]", resultDom));
            }, true);
        } else {
            regIdObj.closest("div.js_hs_area_div").find("input[name=hsTypNum]:eq(0)").focus();
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
            ch002.flashInputRule($(this));
            $(this).blur(function () {
                ch002.flashInputRule($(this));
            })
        });
        // 附件上传提示
        var docInfoSpan = $("span.js_doc_info", $.pdialog.getCurrent());
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
                            ImgPage.viewPics(docId);
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
     * 自动计算新增选房的安置面积
     */
    calcSize: function (obj) {
        var hsBuildSize = $("input[name=hsBuildSize]", $.pdialog.getCurrent()).val();
        if (!hsBuildSize) {
            hsBuildSize = 0;
        }
        var $thisTd = $(obj).closest("td");
        var fpmj = $("input[name=fpmj]", $thisTd).val();
        //if (fpmj == null || fpmj == 0) {
        //    $("input[name=fpmj]", $thisTd).val(hsBuildSize);
        //    fpmj = hsBuildSize;
        //    //$(obj).addClass("inputWarn").val("分配选房面积不能大于建筑面积");
        //}
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
        var buildAddr = $("input[name=buildAddr]", $.pdialog.getCurrent()).val();
        var buildNo = $("input[name=buildNo]", $.pdialog.getCurrent()).val();
        if (buildAddr && buildNo) {
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

    /**
     * 自动补齐
     * @param obj
     * @returns {string}
     */
    getUrl: function (obj) {
        var hsId = $("input[name=hidHsId]", $.pdialog.getCurrent()).val();
        return getGlobalPathRoot() + "eland/ch/ch001/ch001-queryPer.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
    },

    getOption: function (obj) {
        return {
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
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onError: function (var1, var2, var3) {
            },
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input.js_ps_id").val(data.personId);
                tr.find("input.js_ps_certy").val(data.personCertyNum);
                tr.find("input.js_ps_tel").val(data.personTelphone);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input.js_ps_id").val("");
                tr.find("input.js_ps_certy").val("");
                tr.find("input.js_ps_tel").val("");
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

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        var hsId = $("input[name=hidHsId]", $.pdialog.getCurrent()).val();
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
            close: ch002.docClosed,
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
            ch002.syncPersons($span);
        }
        // 调用文件的关闭
        return file.closeD();
    },

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
            fetchData: function (clickObj) {
                var hsFullAddr = $("input[name=hsFullAddr]", $.pdialog.getCurrent()).val();
                return [{"hsFullAddr": hsFullAddr}];
            },
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
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
     * 增加共同居住人
     * @param clickSpan 点击对象
     */
    addLivePs: function (clickSpan) {
        var clickTable = $(clickSpan).closest("table");
        var templateTr = clickTable.find("tr.js_gtjz_info:first");
        var newTr = templateTr.clone().initUI();
        newTr.find("input").val("");
        newTr.removeClass("hidden");
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
     * 删除选房
     * @param clickSpan
     */
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
            relObjContainIdArr = ["js_all_choose_hs_div"];
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
                if (tr.hasClass("js_wt_info")) {
                    if (tr.hasClass("hidden")) {
                        if (personName == "") {
                            return true;
                        }
                    }
                }
                if (tr.hasClass("isDelPs")) {
                    return true;
                }
                var currentPerson = ch002.getPersonInfo(tr);
                if (currentPerson.personId == "" || currentPerson.personId == null) {
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
        for (var j = 0; j < personIds.length; j++) {
            var temp = personIds[j];
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
        var tempObj;
        // 身份证信息
        tempObj = personContain.find("input.js_ps_certy_doc");
        if (tempObj.length > 0) {
            person.personCertyDocIds = tempObj.val();
        }
        // 人员其他信息
        var moreTr = personContain.next("tr");
        if (moreTr.hasClass("js_ps_more")) {
            // 现住址
            tempObj = moreTr.find("input.js_live_addr");
            if (tempObj.length > 0) {
                person.personLiveAddr = tempObj.val();
            }
            // 户口所在地
            tempObj = moreTr.find("input.js_family_addr");
            if (tempObj.length > 0) {
                person.familyAddr = tempObj.val();
            }
            // 工作单位
            tempObj = moreTr.find("input.js_job_addr");
            if (tempObj.length > 0) {
                person.personJobAddr = tempObj.val();
            }
        }
        // 返回人员信息
        return person;
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
        var person = ch002.getPersonInfo(syncTr);
        // 人员信息
        if (person.personId != "") {
            $("input.js_ps_id", navTab.getCurrentPanel()).each(
                function () {
                    if ($(this).val() == person.personId) {
                        var syncToTr = $(this).closest("tr");
                        ch002.syncPerson(person, syncToTr);
                    }
                }
            );
        }
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
        var tempObj;
        // 身份证信息
        tempObj = syncTo.find("input.js_ps_certy_doc");
        if (tempObj.length > 0 && personData.personCertyDocIds != undefined) {
            tempObj.val(personData.personCertyDocIds);
        }
        // 人员其他信息
        var moreTr = syncTo.next("tr");
        if (moreTr.hasClass("js_ps_more")) {
            // 现住址
            tempObj = moreTr.find("input.js_live_addr");
            if (tempObj.length > 0 && personData.personLiveAddr != undefined) {
                tempObj.val(personData.personLiveAddr);
            }
            // 户口所在地
            tempObj = moreTr.find("input.js_family_addr");
            if (tempObj.length > 0 && personData.familyAddr != undefined) {
                tempObj.val(personData.familyAddr);
            }
            // 工作单位
            tempObj = moreTr.find("input.js_job_addr");
            if (tempObj.length > 0 && personData.personJobAddr != undefined) {
                tempObj.val(personData.personJobAddr);
            }
        }
    },

    /**
     * 保存选房信息
     */
    saveChooseHsInfo: function () {
        var $form = $("#ch00201ChooseHsInfo", $.pdialog.getCurrent());
        if ($form.length != 0 && $form.valid()) {
            //###### 增加人员信息，在form表单序列化之前 更新人员信息，避免购房人id 存不上
            var persons = ch002.getAllPsInfo(["js_all_choose_hs_div"], null);


            var hsId = $("input[name=hidHsId]", $.pdialog.getCurrent()).val();
            var hsCtId = $("input[name=hidHsCtId]", $.pdialog.getCurrent()).val();
            // 选房信息的共同居住人处理
            var hsAreaAllDiv = $("#js_all_choose_hs_div", $.pdialog.getCurrent());
            hsAreaAllDiv.find("div.js_hs_buy_info").each(function () {
                var $this = $(this);
                if ($this.hasClass("hidden")) {
                    return true;
                } else {
                    var gtJzPsIdArr = [];
                    var gtJzPsOwnerRelArr = [];
                    $("input[name=gtJzPsId]", $this).each(function () {
                        var value = $(this).val();
                        if (value != "") {
                            gtJzPsIdArr.push(value);
                        }
                    });
                    $("input[name=gtJzPsOwnerRel]", $this).each(function () {
                        var value = $(this).val();
                        if (value != "") {
                            gtJzPsOwnerRelArr.push(value);
                        }
                    });
                    $("input[name=gtJzPsIds]", $this).val(gtJzPsIdArr.join(","));
                    $("input[name=gtJzPsOwnerRels]", $this).val(gtJzPsOwnerRelArr.join(","));
                }
            });

            var ajaxPacket = new AJAXPacket();
            ajaxPacket.url = getGlobalPathRoot() + "eland/ch/ch002/ch002-saveChooseHsInfo.gv";

            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            ajaxPacket.data.data.push({name: "hsId", value: hsId});
            ajaxPacket.data.data.push({name: "hsCtId", value: hsCtId});

            //
            for (var i = 0; i < persons.length; i++) {
                var person = persons[i];
                ajaxPacket.data.data.push({
                    name: "personId",
                    value: person.personId
                });
                ajaxPacket.data.data.push({
                    name: "personName",
                    value: person.personName
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
            }

            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);

        }else{
            $.pdialog.closeCurrent();
        }
    }
};
$(function () {
    $("input[name=fpmj]", $.pdialog.getCurrent()).each(function () {
        ch002.calcSize(this);
    });

    // 绑定人员信息
    $("#ch00201ChooseHsInfo", $.pdialog.getCurrent()).delegate("input.js_ps", "blur", function () {
        ch002.syncPersons(this);
    });
});