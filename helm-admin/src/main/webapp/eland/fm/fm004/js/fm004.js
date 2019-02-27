/**
 * Created by X on 2016/7/5.
 */
/**
 * 弹框里 详情链接
 */
var fm004 = {

    /**
     * 切换回编辑模式
     * @param obj
     */
    changeName: function (obj) {
        var $this = $(obj);
        // 判断名字内容是否改变
        //var $thisSp = $this.closest("span");
        var selectScheme = $("span.active", navTab.getCurrentPanel());
        var oldPlanName = $("input[name=planName]", selectScheme);
        var newPlanName = $this.text();
        if (oldPlanName.val() != newPlanName) {
            oldPlanName.val(newPlanName);
            // 内容改变修改,保存修改结果
            fm004.saveSession();
        }
    },

    /**
     * 打开建筑信息页面
     **/
    viewBuilding: function (buildId) {
        var url = "eland/pb/pb002/pb002-init.gv?prjCd=" + getPrjCd() + "&buldId=" + buildId;
        index.quickOpen(url, {opName: "院落详情", opCode: "pb002", fresh: true});
    },
    /**
     * 查看居民信息
     **/
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情"});
    },
    /**
     * 打开查看监控信息
     **/
    openCam: function (camId, camAddr) {
        var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-view.gv?prjCd=" + getPrjCd() + "&camId=" + camId;
        $.pdialog.open(url, "ph010-view", "摄像头:" + camAddr, {
            mask: true, max: false, height: 600, width: 800, resizable: false, maxable: false
        });
    },

    /**
     * 新增方案
     * @param obj
     */
    addScheme: function (obj) {
        var $this = $(obj);
        //保存session
        var hsId = "";
        var buildId = "";
        var ctName = "";
        var ctType = "1";
        var planName = "方案";
        var schemeName = new Date().getTime();
        var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-saveS.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("hsId", hsId);
        packet.data.add("buildId", buildId);
        packet.data.add("ctName", ctName);
        packet.data.add("ctType", ctType);
        packet.data.add("planName", planName);
        packet.data.add("schemeName", schemeName);
        core.ajax.sendPacketHtml(packet, function (response) {
            //保存成功后新增方案
            $("span.active", navTab.getCurrentPanel()).removeClass("active");
            var tempSpan = $("span[name=headTemp]", navTab.getCurrentPanel()).clone().css("display", "").attr("name", "").addClass("active");
            $("input[name=schemeName]", tempSpan).val(schemeName);
            $("input[name=ctType]", tempSpan).val(ctType);
            $("input[name=planName]", tempSpan).val(planName);
            $("span.sName", tempSpan).text(planName);
            $this.before(tempSpan);
            //刷新 已选居民 区域
            var hsIdDiv = $("#hsIdDiv", navTab.getCurrentPanel());
            $("span.js_clear", hsIdDiv).remove();
            //刷新 预估成本 区域
            var schemeDiv = $("#schemeDiv", navTab.getCurrentPanel());
            $("span.js_clear", schemeDiv).remove();
            //清空安置方式
            $("input[name=ctType]:checked", $("#contentScheme", navTab.getCurrentPanel())).removeAttrs("checked");
            fm004Map.loadMapData('load');
        }, true);
    },

    /**
     * 删除方案
     * @param obj
     */
    removeScheme: function (obj) {
        alertMsg.confirm("确定删除此方案吗？", {
            okCall: function () {
                var $this = $(obj).parent("span");
                //删除session
                var schemeName = $("input[name=schemeName]", $this).val();
                var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-deleteS.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("schemeName", schemeName);
                core.ajax.sendPacketHtml(packet, function (response) {
                    //删除成功后删除相应方案
                    $this.remove();
                    $(">span:eq(1)", $("#contentHead", navTab.getCurrentPanel())).trigger("click");
                }, true);
            }
        })
    },

    /**
     * 切换安置方式
     */
    changeCtType: function (obj) {
        var $this = $(obj);
        var selectScheme = $("span.active", navTab.getCurrentPanel());
        $("input[name=ctType]", selectScheme).val($this.val());
        //更新session
        var hsId = $("input[name=hsId]", selectScheme).val();
        if (hsId == "") {
            return false;
        }
        fm004.saveSession();
        fm004.queryResult(selectScheme);
    },

    /**
     * 删除人
     * @param obj
     */
    removeHsId: function (obj) {
        var $this = $(obj).parent("span");
        var selectScheme = $("span.active", navTab.getCurrentPanel());
        var delHsId = $("input[name=hsId]", $this).val();
        var schemeHsId = $("input[name=hsId]", selectScheme);
        var schemectName = $("input[name=ctName]", selectScheme);
        var schemeHsIdArr = schemeHsId.val().split(",");
        var schemectNameArr = schemectName.val().split(",");
        var newHsIdArray = [];
        var newctNameArray = [];
        var x = 0;
        for (var i = 0; i < schemeHsIdArr.length; i++) {
            if (delHsId != schemeHsIdArr[i]) {
                newHsIdArray[x] = schemeHsIdArr[i];
                newctNameArray[x++] = schemectNameArr[i];
            }
        }
        //删除其中的hsId后重新赋值
        schemeHsId.val(newHsIdArray.toString());
        schemectName.val(newctNameArray.toString());
        //更新session
        fm004.saveSession();
        //删除成功后删除相应方案
        $this.remove();
        fm004.queryResult(selectScheme);
        fm004Map.loadMapData('load');
    },

    /**
     * 记录session
     */
    saveSession: function () {
        var selectScheme = $("span.active", navTab.getCurrentPanel());
        var hsId = $("input[name=hsId]", selectScheme).val();
        var buildId = $("input[name=buildId]", selectScheme).val();
        var ctName = $("input[name=ctName]", selectScheme).val();
        var ctType = $("input[name=ctType]", selectScheme).val();
        var planName = $("input[name=planName]", selectScheme).val();
        var schemeName = $("input[name=schemeName]", selectScheme).val();
        if (schemeName == "") {
            schemeName = new Date().getTime();
        }
        var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-saveS.gv";
        var packet = new AJAXPacket(url);
        packet.noCover = true;
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("hsId", hsId);
        packet.data.add("buildId", buildId);
        packet.data.add("ctName", ctName);
        packet.data.add("ctType", ctType);
        packet.data.add("planName", planName);
        packet.data.add("schemeName", schemeName);
        core.ajax.sendPacketHtml(packet, function (response) {
        }, true);
    },

    /**
     * 选择方案
     */
    queryResult: function (obj) {
        var $this = $(obj);
        var hsId = $("input[name=hsId]", $this).val();
        if (hsId == "") {
            //清空数据区域
            var hsIdDiv = $("#hsIdDiv", navTab.getCurrentPanel());
            $("span.js_clear", hsIdDiv).remove();
            var schemeDiv = $("#schemeDiv", navTab.getCurrentPanel());
            $("span.js_clear", schemeDiv).remove();
            $("input[name=ctType]:checked", $("#contentScheme", navTab.getCurrentPanel())).removeAttrs("checked");
            $this.closest("div").find(">span").each(function () {
                $(this).removeClass("active");
            });
            $this.addClass("active");
            fm004Map.loadMapData('load');
            return false;
        }
        $this.closest("div").find(">span").each(function () {
            $(this).removeClass("active");
        });
        $this.addClass("active");
        var ctName = $("input[name=ctName]", $this).val();
        var ctType = $("input[name=ctType]", $this).val();
        if (ctType == "") {
            ctType = "1";
        }
        var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-queryYuFenMoney.gv";
        var packet = new AJAXPacket(url);
        packet.noCover = true;
        packet.data.add("ctType", ctType);
        packet.data.add("hsId", hsId);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            //刷新 已选居民 区域
            var hsIdDiv = $("#hsIdDiv", navTab.getCurrentPanel());
            $("span.js_clear", hsIdDiv).remove();
            var hsIds = hsId.split(",");
            if (hsId != "") {
                var ctNames = ctName.split(",");
                for (var i = 0; i < hsIds.length; i++) {
                    var tempObj = $("span[name=hsIdTemp]", navTab.getCurrentPanel()).clone().css("display", "").attr("name", "").addClass("js_clear");
                    $("input[name=hsId]", $(tempObj)).val(hsIds[i]);
                    $(">span", $(tempObj)).text(ctNames[i]);
                    $("span[name=hsIdTemp]", navTab.getCurrentPanel()).after(tempObj);
                }
            }
            //安置方式
            $("input[name=ctType]:checked", $("#contentScheme", navTab.getCurrentPanel())).removeAttrs("checked");
            $("input[name=ctType][value=" + ctType + "]", $("#contentScheme", navTab.getCurrentPanel())).attr("checked", "true");
            //刷新 预估成本 区域
            var schemeDiv = $("#schemeDiv", navTab.getCurrentPanel());
            $("span.js_clear", schemeDiv).remove();
            if (isSuccess) {
                var tempArr = data.schemeArray.split(",");
                if (tempArr.length > 0) {
                    for (var t = 0; t < tempArr.length; t++) {
                        var schemeObj = $("span[name=schemeTemp]", navTab.getCurrentPanel()).clone().css("display", "").attr("name", "").addClass("js_clear");
                        schemeObj.text(tempArr[t]);
                        $("span[name=schemeTemp]", navTab.getCurrentPanel()).before(schemeObj);
                    }
                }
            } else {
                var schemeObj = $("span[name=schemeTemp]", navTab.getCurrentPanel()).clone().css("display", "").attr("name", "").addClass("js_clear");
                schemeObj.text(data.errMsg);
                $("span[name=schemeTemp]", navTab.getCurrentPanel()).after(schemeObj);
            }
            fm004Map.loadMapData('load');
        }, true);
    },

    /**
     * 打开对比页面
     */
    openMatch: function () {
        var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-queryDuiBi.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "fm004openMatch", "成本对比",
            {mask: true, width: 800, height: 400, resizable: true, max: true}
        )
    },

    /**
     * 克隆列
     */
    cloneRank: function (obj) {
        var $this = $(obj);
        var thisTr = $this.closest("tr");
        var cloneTd = $this.closest("td").clone();
        $(thisTr).append(cloneTd);
        var hsId = $("input[name=hsId]", cloneTd).val();
        var ctType = $("input[name=ctType]", cloneTd).val();
        var ctName = $("input[name=ctName]", cloneTd).val();
        var schemeName = new Date().getTime();
        $("input[name=schemeName]", cloneTd).val(schemeName);
        //新增session
        fm004.saveMatchSession(cloneTd);
        fm004.addCol(hsId, ctType, ctName);
    },

    /**
     * 新增列
     */
    addCol: function (hsId, ctType, ctName) {
        var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-queryYuFenMoney.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("ctType", ctType);
        packet.data.add("hsId", hsId);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var temp = 0;
                $("tr[name=queryTr]", $("#fm004match", $.pdialog.getCurrent())).each(function () {
                    var $thisTr = $(this);
                    if ($thisTr.context.rowIndex == 1) {
                        //填充计算模型
                        var cloneTd = $("td:eq(0)", $thisTr).clone(true).css("display", "");
                        $("select[name=ctType]", cloneTd).find("option[value=" + ctType + "]").attr("selected", "true");
                        $thisTr.append(cloneTd);
                    } else if ($thisTr.context.rowIndex == 2) {
                        //填充方案户籍
                        var cloneTd = $("td:eq(0)", $thisTr).clone().css("display", "");
                        var hsIdArr = hsId.split(",");
                        var ctNameArr = ctName.split(",");
                        for (var i = 0; i < hsIdArr.length; i++) {
                            var cloneObj = $("div >span[name=hsIdTemp]", cloneTd).clone().css("display", "").attr("name", "");
                            $("input[name=hsId]", $(cloneObj)).val(hsIdArr[i]);
                            $(">span", $(cloneObj)).text(ctNameArr[i]);
                            $("span[name=hsIdTemp]", $(cloneTd)).after(cloneObj);
                        }
                        $thisTr.append(cloneTd);
                    } else {
                        //填充款项
                        var flag = false;
                        var temp = 0;
                        //查看款项里有没有这一项,如果有写入数据，如果没有写入“-”
                        for (var k = 0; k < data.schemeInfo.length; k++) {
                            if ($("th", $thisTr).text() == data.schemeInfo[k].name) {
                                flag = true;
                                temp = k;
                                break;
                            }
                        }
                        if(flag){
                            $thisTr.append("<td>" + data.schemeInfo[temp].value + "</td>");
                        }else{
                            $thisTr.append("<td>-</td>");
                        }
                    }
                });
            } else {
                alertMsg.warn(data.errMsg);
            }
        }, false);
    },

    /**
     * 删除列
     */
    delcol: function (obj) {
        var thisTd = $(obj).closest("td");
        //因为第二行和第三行有隐藏的td，所以需要处理一下
        var col1 = $(thisTd[0]).context.cellIndex - 1;
        var col2 = $(thisTd[0]).context.cellIndex;
        var tabTr = $("tr", $("#fm004match", $.pdialog.getCurrent()));
        for (var i = 0; i < tabTr.length; i++) {
            if (i == 1 || i == 2) {
                tabTr.eq(i).find("td").eq(col2).remove();
            } else {
                tabTr.eq(i).find("td").eq(col1).remove();
            }
        }
    },
    /**
     * 删除方案户籍
     */
    delHsId: function (obj) {
        var thisTd = $(obj).closest("td");
        var thisSpan = $(obj).parent("span");
        var headTr = $("tr:eq(0)", $("#fm004match", $.pdialog.getCurrent()));
        //因为此行有隐藏td所以需要-2
        var col = $(thisTd[0]).context.cellIndex - 2;
        var headTd = $("td:eq(" + col + ")", headTr);
        var delHsId = $("input[name=hsId]", thisSpan).val();

        var schemeHsId = $("input[name=hsId]", headTd);
        var schemectName = $("input[name=ctName]", headTd);
        var schemeHsIdArr = schemeHsId.val().split(",");
        var schemectNameArr = schemectName.val().split(",");
        var newHsIdArray = [];
        var newctNameArray = [];
        var x = 0;
        for (var i = 0; i < schemeHsIdArr.length; i++) {
            if (delHsId != schemeHsIdArr[i]) {
                newHsIdArray[x] = schemeHsIdArr[i];
                newctNameArray[x++] = schemectNameArr[i];
            }
        }
        //删除其中的hsId后重新赋值
        schemeHsId.val(newHsIdArray.toString());
        schemectName.val(newctNameArray.toString());
        thisSpan.remove();
        fm004.saveMatchSession(headTd);
        fm004.refreshData(headTd, col + 1);
    },

    /**
     * 对比页面切换安置方式
     */
    changeMatchType: function (obj) {
        var thisTd = $(obj).closest("td");
        var headTr = $("tr:eq(0)", $("#fm004match", $.pdialog.getCurrent()));
        //因为此行有隐藏td所以需要-2
        var col = $(thisTd[0]).context.cellIndex - 2;
        var headTd = $("td:eq(" + col + ")", headTr);
        $("input[name=ctType]", headTd).val($(obj).val());
        fm004.saveMatchSession(headTd);
        fm004.refreshData(headTd, col + 1);
    },

    /**
     * 删除对比页面方案
     * @param obj
     */
    removeMatchScheme: function (obj, flag) {
        alertMsg.confirm("确定删除此方案吗？", {
            okCall: function () {
                var $this = $(obj).closest("td");
                //删除session
                var schemeName = $("input[name=schemeName]", $this).val();
                var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-deleteS.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("schemeName", schemeName);
                core.ajax.sendPacketHtml(packet, function (response) {
                    // 删除列标记位
                    if (flag == "1") {
                        fm004.delcol(obj);
                    }
                }, true);
            }
        })
    },

    /**
     * 对比页面记录session
     */
    saveMatchSession: function (obj) {
        var hsId = $("input[name=hsId]", $(obj)).val();
        var buildId = $("input[name=buildId]", $(obj)).val();
        var ctName = $("input[name=ctName]", $(obj)).val();
        var ctType = $("input[name=ctType]", $(obj)).val();
        var planName = $("input[name=planName]", $(obj)).val();
        var schemeName = $("input[name=schemeName]", $(obj)).val();
        if (schemeName == "") {
            schemeName = new Date().getTime();
        }
        var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-saveS.gv";
        var packet = new AJAXPacket(url);
        packet.noCover = true;
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("hsId", hsId);
        packet.data.add("buildId", buildId);
        packet.data.add("ctName", ctName);
        packet.data.add("ctType", ctType);
        packet.data.add("planName", planName);
        packet.data.add("schemeName", schemeName);
        core.ajax.sendPacketHtml(packet, function (response) {
        }, true);
    },

    /**
     * 刷新列
     */
    refreshData: function (obj, col) {
        var $this = $(obj);
        var hsId = $("input[name=hsId]", $this).val();
        var ctType = $("input[name=ctType]", $this).val();
        var ctName = $("input[name=ctName]", $this).val();
        var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-queryYuFenMoney.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("ctType", ctType);
        packet.data.add("hsId", hsId);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var temp = 0;
                $("tr[name=queryTr]", $("#fm004match", $.pdialog.getCurrent())).each(function () {
                    var $thisTr = $(this);
                    if ($thisTr.context.rowIndex == 1) {
                        //填充计算模型
                        $("select[name=ctType]", $("td:eq(" + col + ")", $thisTr)).find("option[value=" + ctType + "]").attr("selected", "true");
                    } else if ($thisTr.context.rowIndex == 2) {
                        $("div", $("td:eq(" + col + ")", $thisTr)).remove();
                        //填充方案户籍
                        var cloneTd = $("td:eq(0)>div", $thisTr).clone().css("display", "");
                        var hsIdArr = hsId.split(",");
                        var ctNameArr = ctName.split(",");
                        for (var i = 0; i < hsIdArr.length; i++) {
                            var cloneObj = $(">span[name=hsIdTemp]", cloneTd).clone().css("display", "").attr("name", "");
                            $("input[name=hsId]", $(cloneObj)).val(hsIdArr[i]);
                            $(">span", $(cloneObj)).text(ctNameArr[i]);
                            $("span[name=hsIdTemp]", $(cloneTd)).after(cloneObj);
                        }
                        $("td:eq(" + col + ")", $thisTr).append(cloneTd);
                    } else {
                        //填充款项
                        var col_ = col - 1;
                        var flag = false;
                        var tempk = 0;
                        //查看款项里有没有这一项,如果有写入数据，如果没有写入“-”
                        for (var k = 0; k < data.schemeInfo.length; k++) {
                            if ($("th", $thisTr).text() == data.schemeInfo[k].name) {
                                flag = true;
                                tempk = k;
                                break;
                            }
                        }
                        if (flag) {
                            $("td:eq(" + col_ + ")", $thisTr).text( data.schemeInfo[tempk].value);
                        } else {
                            $("td:eq(" + col_ + ")", $thisTr).text("-");
                        }
                    }
                });
            } else {
                alertMsg.warn(data.errMsg);
            }
        }, false);
    }
};