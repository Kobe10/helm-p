var oh006 = {
    /**
     * 删除选房
     * @param clickSpan
     */
    delNewHs: function (clickSpan) {
        var currentInfo = $(clickSpan).parent("div");
        var relTable = currentInfo.closest("table");
        var countObj = relTable.find("h1.js_js_name>label.js_reg_count");
        countObj.html(parseInt(countObj.html()) - 1);
        currentInfo.remove();
    },

    //初始化区域树
    initHxTree: function () {
        var hiddenTree = $("#hiddenTree", navTab.getCurrentPanel())
        if (hiddenTree.html() == "") {
            // 获取区域类型
            var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
            // 重新加载
            var packet = new AJAXPacket();
            var prjCd = getPrjCd();
            packet.noCover = true;
            packet.data.add("prjCd", prjCd);
            packet.data.add("treeType", "3");
            packet.data.add("objValue", "");
            packet.url = getGlobalPathRoot() + "eland/oh/oh006/oh006-treeInfo.gv";
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
                                onClick: oh006.clickNode
                            },
                            view: {
                                selectedMulti: false
                            },
                            edit: {
                                showRemoveBtn: false,
                                showRenameBtn: false
                            }
                        };
                        // 初始化目录树
                        var zTree = $.fn.zTree.init($("#hiddenTree", navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);
                        hiddenTree.show();
                        hiddenTree.removeClass("hidden");
                    }
                    else {
                        alertMsg.warn(jsonData.data.errMsg);
                    }
                }, true, false
            );
        }
        if (hiddenTree.is(':hidden')) {
            hiddenTree.show();
        } else {
            hiddenTree.hide();
        }
    },

    /**
     * 区域树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        //if (treeNode.pId != null) {
        //获取配比中的值
        var hsTypeNum = $("input[name=hsTypeNum" + treeNode.hsType + "]", navTab.getCurrentPanel()).val();
        //获取列表中相应的div
        var flagTable = $("div[flag=" + treeNode.name + "]", navTab.getCurrentPanel());
        var hsAreaNum = flagTable.length;
        if (hsAreaNum > hsTypeNum || hsAreaNum == hsTypeNum) {
            alertMsg.warn("超过配比限制");
            return false;
        }
        var hsAreaDiv = $("div.js_hs_area_div", navTab.getCurrentPanel());
        var tempTable = $("#tempTable", navTab.getCurrentPanel());
        var addHsType = $("table[name=" + treeNode.name + "]", navTab.getCurrentPanel());
        var templateInfo = null;
        //没有找到就拷贝table,如果找到就拷贝table下的第二个tr
        if (addHsType.length == 0) {
            var addTable = tempTable.clone();
            addTable.removeClass("hidden").appendTo(hsAreaDiv);
            addTable.attr("name", treeNode.name);
            addTable.attr("id", "");
            addTable.find("h1.js_js_name>label.js_reg_name").html(treeNode.name);
            addTable.find("h1.js_js_name>label.js_reg_count").html(1);
            //购房人内容显示模板
            templateInfo = addTable.find("div.js_hs_buy_info:eq(0)");
        } else {
            templateInfo = addHsType.find("div.js_hs_buy_info:eq(0)");
            var countObj = addHsType.find("h1.js_js_name>label.js_reg_count");
            countObj.html(parseInt(countObj.html()) + 1);
        }
        var addDiv = templateInfo.clone().removeClass("hidden");
        //增加属性标志 提供 查找列表中相应的居室数使用
        addDiv.attr("flag", treeNode.name);
        addDiv.find("input").val("");
        addDiv.find("input[name=huJush]").val(treeNode.hsType);
        addDiv.find("input[name=hiddenRegId]").val("");
        addDiv.find("input[name=hsHxTp]").val(treeNode.hsType);
        //给定默认现住址
        var buildAddr = $("input[name=buildAddr]", navTab.getCurrentPanel()).val();
        var buildNo = $("input[name=buildNo]", navTab.getCurrentPanel()).val();
        if (buildAddr && buildNo) {
            addDiv.find("input.js_live_addr").val(buildAddr + buildNo + "号");
        } else {
            addDiv.find("input.js_live_addr").val();
        }
        addDiv.find("input[name=chooseHsRegId]").val("");
        initUI(addDiv);
        templateInfo.parent().append(addDiv);
        $("#hiddenTree", navTab.getCurrentPanel()).hide();
        //} else {
        //    alertMsg.warn("请点居室节点,无可用居室请确认房源区域是否有可用数据!");
        //}
    },

    /**
     * 上一户下一户
     */
    singleQuery: function (hsId, flag) {
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        var oh006QueryForm = $("#oh006QueryForm", navTab.getCurrentPanel());
        var jsonData = oh006QueryForm.serializeArray();
        jsonData.push({name: "flag", value: flag});
        jsonData.push({name: "hsId", value: hsId});
        jsonData.push({name: "prjCd", value: prjCd});
        packet.data.data = jsonData;
        // 查询上一个、下一个选房人
        packet.url = getGlobalPathRoot() + "eland/oh/oh006/oh006-singleQuery.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var hsId = data.hsId;
            if (hsId != 'null') {
                oh006.chInfo(data.hsId);
            } else {
                if (flag == 'last') {
                    alertMsg.warn("已经是第一个人");
                } else if (flag == 'next') {
                    alertMsg.warn("已经是最后一个人");
                }
            }
        });
    },

    /**
     * 居民详情页面
     * @param hsId
     */
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /**
     * 检索房产
     * @param obj
     */
    schHs: function () {
        // 查询数据
        var oh006QueryForm = $("#oh006QueryForm", navTab.getCurrentPanel());
        // 查询处理结果
        Page.query(oh006QueryForm, "");
    },
    /**
     * 快速查询按钮
     */
    qSchHs: function () {
        var container = $("#oh006SchDiv", navTab.getCurrentPanel());
        var formObj = $('#oh006QueryForm', container);
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        // 查询条件
        $("select[name=schType]", container).find("option").each(function () {
            var $this = $(this);
            var attrName = $this.val();
            var attrValue = "";
            if ($this.attr("selected")) {
                attrValue = $("input[name=schValue]", container).val();
            }
            if (!attrName || attrName == "") {
                return true;
            }
            var nameIdx = -1;
            for (var i = 0; i < conditionName.length; i++) {
                var temp = conditionName[i];
                if (temp == attrName) {
                    nameIdx = i;
                    break;
                }
            }
            if (nameIdx != -1) {
                if ($.trim(attrValue) == "") {
                    conditionName.splice(nameIdx, 1);
                    condition.splice(nameIdx, 1);
                    conditionValue.splice(nameIdx, 1);
                } else {
                    condition[nameIdx] = "like";
                    conditionValue[nameIdx] = attrValue;
                }
            } else if ($.trim(attrValue) != "") {
                conditionName.push(attrName);
                condition.push("like");
                conditionValue.push(attrValue);
            }
        });


        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
        // 查询数据
        oh006.schHs();
    },
    /**
     * 调用通用查询查询房源数据
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: oh006.changeConditions,
            showContainer: $("#oh006AdanceSch", navTab.getCurrentPanel()),
            formObj: $("#oh006QueryForm", navTab.getCurrentPanel()),
            callback: function (showContainer) {
                showContainer.find("div.tip").prepend($('<div class="triangle triangle-left" style="top: 45px; left: -30px;"></div>'));
                showContainer.find("a.js-more").trigger("click");
            }
        });
    },

    /**
     * 自动补齐
     * @param obj
     * @returns {string}
     */
    getUrl: function (obj) {
        var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
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
        var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
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
            close: oh006.docClosed,
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
            oh006.syncPersons($span);
        }
        // 调用文件的关闭
        return file.closeD();
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
        var person = oh006.getPersonInfo(syncTr);
        // 人员信息
        if (person.personId != "") {
            $("input.js_ps_id", navTab.getCurrentPanel()).each(
                function () {
                    if ($(this).val() == person.personId) {
                        var syncToTr = $(this).closest("tr");
                        oh006.syncPerson(person, syncToTr);
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
     * 获取房源区域列表
     * @param obj
     * @returns {string}
     */
    getRegUrl: function (obj) {
        return getGlobalPathRoot() + "eland/oh/oh006/oh006-queryNewHsReg.gv?prjCd=" + getPrjCd();
    },

    /**
     * 自动填充地址信息
     * @param obj
     * @returns {String} hsFullAddr
     */
    getRegOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                if (result) {
                    for (var i = 0; i < result.length; i++) {
                        var dataRow = {};
                        dataRow.data = result[i];
                        dataRow.value = result[i].regName;
                        myData.push(dataRow);
                    }
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.regName;
            },
            displayValue: function (value, data) {
                return data.regName;
            },
            onItemSelect: function (obj) {
                var source = $(obj.source);
                var table = source.closest("table");
                var oldRegId = $("input[name=hiddenRegId]", table).val();
                if (oldRegId != obj.data.regId) {
                    table.find("input.[name=hiddenRegId]").val(obj.data.regId);
                    table.find("input.[name=chooseHsRegId]").val(obj.data.regId);
                    table.find("input[name=hsAddr]").val("");
                }
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            queryParamName: "hsAddr",
            filterResults: true
        }
    },

    /**
     * 选房明细导出
     */
    chooseExport: function (hsId) {
        var rhtRegId = "";
        var rhtOrgId = "";
        var form = $('#oh006QueryForm', navTab.getCurrentPanel());
        var conditionNames = $("input[name=conditionName]", form).val() + ",HouseInfo.hsId";
        var conditions = $("input[name=condition]", form).val() + ",=";
        var conditionValues = $("input[name=conditionValue]", form).val() + "," + hsId;

        var sortColumns = $("input[name=sortColumn]", form).val();
        var sortOrders = $("input[name=sortOrder]", form).val();
        // 其他查询条件
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-chooseExport.gv?prjCd=" + getPrjCd()
            + "&rhtRegId=" + rhtRegId + "&rhtOrgId=" + rhtOrgId + "&conditionNames=" + conditionNames
            + "&conditions=" + conditions + "&conditionValues=" + conditionValues + "&sortColumns=" + sortColumns
            + "&sortOrders=" + sortOrders;
        // 执行导出功能
        window.open(url);
    },

    /**
     * 打开导入面板
     */
    importMb: function () {
        var url = getGlobalPathRoot() + "eland/oh/oh006/oh006-openImport.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "allImport", "信息导入", {
            mask: true,
            max: false,
            maxable: false,
            resizable: false,
            width: 600,
            height: 180
        });
    },

    /**
     * 选房信息导入
     */
    chooseImport: function () {
        var importMbFile = $("#importMbFile", $.pdialog.getCurrent()).val();
        if (importMbFile == "") {
            alertMsg.warn("请选择导入文件");
            return;
        }
        var uploadURL = getGlobalPathRoot() + "eland/oh/oh006/oh006-chooseImport.gv?prjCd=" + getPrjCd();
        var ajaxbg = $("#background,#progressBar");
        ajaxbg.show();
        $.ajaxFileUpload({
            url: uploadURL,
            secureuri: false,
            fileElementId: "importMbFile",
            dataType: 'json',
            success: function (data) {
                if (data.isSuccess) {
                    alertMsg.correct("处理成功");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(data.errMsg);
                }
                ajaxbg.hide();
            }
        })
    },

    /**
     * 获取地址列表
     * @param obj
     * @returns {string}
     */
    getAddrUrl: function (obj) {
        return getGlobalPathRoot() + "eland/oh/oh002/oh002-queryNewHsAddr.gv?prjCd=" + getPrjCd();
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
                if (result) {
                    for (var i = 0; i < result.length; i++) {
                        var dataRow = {};
                        dataRow.data = result[i];
                        dataRow.value = result[i].hsAddr;
                        myData.push(dataRow);
                    }
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.hsAddr;
            },
            displayValue: function (value, data) {
                return data.hsAddr;
            },
            dyExtraParams: function (obj) {
                var $table = $(obj).closest("table");
                var regId = $table.find("input[name=hiddenRegId]").val();
                if (regId == '0') {
                    regId = "";
                }
                return {
                    hsAddr: $table.find("input[name=hsAddr]").val(),
                    hsHxTp: $table.find("input[name=hsHxTp]").val(),
                    regId: regId,
                    prjCd: getPrjCd()
                }
            },
            onItemSelect: function (obj) {
                var source = $(obj.source);
                var table = source.closest("table");
                table.find("input.js_newHsId").val(obj.data.newHsId);
                table.find("input.js_hsHxName").val(obj.data.hsHxName);
                table.find("input.js_preBldSize").val(obj.data.preBldSize);
                table.find("input.js_live_addr[name=hsAddr]").val(obj.data.hsAddr);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var table = source.closest("table");
                table.find("input.js_newHsId").val("");
                table.find("input.js_hsHxName").val("");
                table.find("input.js_preBldSize").val("");
                table.find("input.js_live_addr[name=hsAddr]").val("");
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            queryParamName: "hsAddr",
            filterResults: true
        }
    },

    /**
     * 获取地址列表
     * @param obj
     * @returns {string}
     */
    getAddrUrlNow: function (obj) {
        return getGlobalPathRoot() + "eland/ph/ph003/ph00301-queryAllHsAddr.gv?prjCd=" + getPrjCd();
    },
    /**
     * 自动填充地址信息
     * @param obj
     * @returns {String} hsFullAddr
     */
    getAddrOptNow: function (obj) {
        return {
            fetchData: function (clickObj) {
                var hsFullAddr = $("input[name=hsFullAddr]", navTab.getCurrentPanel()).val();
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
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#oh006QueryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        // 查询数据
        oh006.schHs();
    },

    /**
     * 增加新的房屋居室
     * @param clickSpan 点击的span对象
     */
    addHs: function (clickSpan) {
        var numObj = $(clickSpan).parent().find("input.js_num");
        // 数量+1
        numObj.val(parseInt(numObj.val()) + 1)
    },

    /**
     * 减少新的房屋居室
     * @param clickSpan 点击的span对象
     */
    subHs: function (clickSpan) {
        // 数据准备
        var numObj = $(clickSpan).parent().find("input.js_num");
        var hsTypeName = $(clickSpan).parent().find("input[name=hsTypeName]").val();
        //获取列表中相应的div
        var flagTable = $("div[flag=" + hsTypeName + "]", navTab.getCurrentPanel());
        var hsAreaNum = flagTable.length;
        if ((hsAreaNum == parseInt(numObj.val()) || hsAreaNum > parseInt(numObj.val())) && numObj.val() != 0) {
            alertMsg.warn("已登记了" + hsAreaNum + "套房源，请先删除对应的登记信息");
            return false;
        }
        // 数量-1
        if (numObj.val() != 0) {
            numObj.val(parseInt(numObj.val()) - 1);
        }
    },


    /**
     * 选房信息
     * @param hsId
     */
    chInfo: function (hsId) {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.url = getGlobalPathRoot() + "eland/oh/oh006/oh006-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#oh006Info", navTab.getCurrentPanel()).html(response);
            initUI($("#oh006Info", navTab.getCurrentPanel()));
        });
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
                var currentPerson = oh006.getPersonInfo(tr);
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
            //tempObj = $("input.js_notice_addr");
            //if (tempObj.length > 0){
            //    person.personNoticeAddr = tempObj.val();
            //}
        }
        // 返回人员信息
        return person;
    },
    /**
     * 审批通过
     */
    approve: function (choosePbStatus) {
        var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
        var hsCtId = $("input[name=hidHsCtId]", navTab.getCurrentPanel()).val();
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("choosePbStatus", choosePbStatus);
        packet.url = getGlobalPathRoot() + "eland/oh/oh006/oh006-approve.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            if (data.success) {
                alertMsg.correct("操作成功！");
                oh006.chInfo(hsId);
            } else {
                alertMsg.error(data.errMsg);
            }
        });
    },

    /**
     * 保存选房信息
     */
    saveChooseHsInfo: function () {
        var $form = $("#oh00601ChooseHsInfo", navTab.getCurrentPanel());
        if ($form.length != 0 && $form.valid()) {
            //###### 增加人员信息，在form表单序列化之前 更新人员信息，避免购房人id 存不上
            var persons = oh006.getAllPsInfo(["js_all_choose_hs_div"], null);
            var hsId = $("input[name=hidHsId]", navTab.getCurrentPanel()).val();
            var hsCtId = $("input[name=hidHsCtId]", navTab.getCurrentPanel()).val();
            // 选房信息的共同居住人处理
            var hsAreaAllDiv = $("#js_all_choose_hs_div", navTab.getCurrentPanel());
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
                        if (value == "") {
                            gtJzPsOwnerRelArr.push("--");
                        }else{
                            gtJzPsOwnerRelArr.push(value);
                        }
                    });
                    $("input[name=gtJzPsIds]", $this).val(gtJzPsIdArr.join(","));
                    $("input[name=gtJzPsOwnerRels]", $this).val(gtJzPsOwnerRelArr.join(","));
                }
            });

            var ajaxPacket = new AJAXPacket();
            ajaxPacket.url = getGlobalPathRoot() + "eland/oh/oh006/oh006-saveChooseHsInfo.gv";

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
                //ajaxPacket.data.data.push({
                //    name: "personNoticeAddr",
                //    value: person.personNoticeAddr ? person.personNoticeAddr : ""
                //});
            }

            //配比信息
            //数据准备
            var matchTable = $("#matchTable", navTab.getCurrentPanel());
            var yjNum = $("input[name=hsTypeNum1]", matchTable).val();
            if (!yjNum) {
                yjNum = 0;
            }
            var ejNum = $("input[name=hsTypeNum2]", matchTable).val();
            if (!ejNum) {
                ejNum = 0;
            }
            var sjNum = $("input[name=hsTypeNum3]", matchTable).val();
            if (!sjNum) {
                sjNum = 0;
            }
            var ljNum = $("input[name=hsTypeNum4]", matchTable).val();
            if (!ljNum) {
                ljNum = 0;
            }
            var choosePbNote = $("textarea[name=choosePbNote]", matchTable).val();
            ajaxPacket.data.data.push({
                name: "ljNum",
                value: ljNum
            });
            ajaxPacket.data.data.push({
                name: "yjNum",
                value: yjNum
            });
            ajaxPacket.data.data.push({
                name: "ejNum",
                value: ejNum
            });
            ajaxPacket.data.data.push({
                name: "sjNum",
                value: sjNum
            });
            ajaxPacket.data.data.push({
                name: "choosePbNote",
                value: choosePbNote
            });

            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    oh006.chInfo(hsId);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },
    switchTip: function (obj) {
        var $this = $(obj);
        if ($this.hasClass("hideTip")) {
            $this.removeClass("hideTip");
            $this.addClass("showTip");
            var tableObj = $this.next("table");
            var calResultH = $("div.js_hs_area_div", navTab.getCurrentPanel());
            calResultH.attr("layoutH", calResultH.attr("layoutH") + tableObj.height());
            calResultH.height(calResultH.height() + tableObj.height());
            tableObj.hide();
        } else {
            $this.removeClass("showTip");
            $this.addClass("hideTip");
            var tableObj = $this.next("table");
            tableObj.show();
            var calResultH = $("div.js_hs_area_div", navTab.getCurrentPanel());
            calResultH.attr("layoutH", calResultH.attr("layoutH") - tableObj.height());
            calResultH.height(calResultH.height() - tableObj.height());
        }
    },
    switchHsTip: function (obj) {
        var $this = $(obj);
        var controlTrs = $this.closest("tr").next("tr");
        if ($this.hasClass("hideTip")) {
            $this.removeClass("hideTip");
            $this.addClass("showTip");
            controlTrs.hide();
        } else {
            $this.removeClass("showTip");
            $this.addClass("hideTip");
            controlTrs.show();
        }
    },
    saveNoticeInfo: function (obj) {
        var $table = $(obj).closest("table");
        var hsCtChooseId=$("input[name=hsCtChooseId]",$table).val();
        var personNoticeAddr=$("input[name=personNoticeAddr]",$table).val();
        var buyPersonTel=$("input[name=buyPersonTel]",$table).val();
        var newHsId=$("input[name=newHsId]",$table).val();
        var ajaxPacket = new AJAXPacket();
        ajaxPacket.url = getGlobalPathRoot() + "eland/oh/oh006/oh006-saveNoticeInfo.gv";
        ajaxPacket.data.add("prjCd",getPrjCd());
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
    }
};
$(document).ready(function () {
    // 查询房屋
    oh006.schHs();
    // 拖动屏幕切分效果
    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var leftDiv = $("#oh006SchDiv", navTab.getCurrentPanel());
                var rightDiv = $("#oh006Context", navTab.getCurrentPanel());
                var newWidth = $(moveObj).position().left;
                leftDiv.width($(moveObj).position().left);
                rightDiv.css("margin-left", (newWidth + 10) + "px");
                var resizeGrid = $("div.j-resizeGrid", navTab.getCurrentPanel());
                if (resizeGrid.length > 0) {
                    resizeGrid.jResize();
                }
                $(moveObj).css("left", 0);
            }
        });
    });
    $("span.split_but", navTab.getCurrentPanel()).mousedown(function (event) {
        stopEvent(event);
    });
    $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
        var splitLine = $(this).parent();
        var leftMenu = splitLine.prev("div.left_menu");
        if (leftMenu.is(":visible")) {
            leftMenu.hide();
            splitLine.addClass("menu_hide");
            var rightDiv = $("#oh006Context", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#oh006Context", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });
});
