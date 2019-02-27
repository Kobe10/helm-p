/**
 * Created by shfb_wang on 2015/3/30.
 */
var ph008 = {
    //完成腾退预审
    completeYuShen: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult1 = $("select[name=procResult1]", $table).val();
        var comment1 = $("textarea[name=comment1]", $table).val();
        var recordTime1 = $("input[name=recordTime1]", $table).val();
        var hsId = $("input[name=hsId]", $table).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-completeYuShen.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult1", procResult1);
        packet.data.add("comment1", comment1);
        packet.data.add("recordTime1", recordTime1);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("预审成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 确认 购房人资格审核
     */
    cfmGfSh: function (obj) {
        var $this = $(obj);
        var thisTable = $this.closest("table");
        var hsCtChooseId = $("input[name=hsCtChooseId]", $this.closest("div.js_panel_context")).val();
        var zgTime = $("input[name=zgTime]", thisTable);
        var zgResult = $("input[name=zgResult" + hsCtChooseId + "]:checked", thisTable);

        if (!zgTime.val()) {
            alertMsg.warn("审核时间必须输入！");
        } else {
            var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-cfmGfSh.gv?prjCd=" + getPrjCd();
            var packet = new AJAXPacket(url);
            packet.data.add("hsCtChooseId", hsCtChooseId);
            packet.data.add("zgResult", zgResult.val());
            packet.data.add("zgTime", zgTime.val());
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("提交成功！");
                    //$this.closest("tr").hide();
                    //$("input[name=zgResult" + hsCtChooseId + "]", thisTable).attr("disabled", "disabled");
                    //zgTime.attr("readonly", "readonly").attr("disabled", "disabled");
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 确认 领取购房合同材料
     */
    cfmGfhtcl: function (obj) {
        var $this = $(obj);
        //获取到最大的一个table 然后find 领取材料 tr
        var thisTr = $this.closest("table.everyChHsTable").find("tr.js_gfhtcl");
        var hsCtChooseId = $("input[name=hsCtChooseId]", $this.closest("div.js_panel_context")).val();
        var clLqTime = $("input[name=clLqTime]", thisTr);
        var clLqStatus = $("input[name=clLqStatus" + hsCtChooseId + "]:checked", thisTr);

        if (!clLqTime.val()) {
            alertMsg.warn("领取时间必须输入！");
        } else {
            var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-cfmGfhtcl.gv?prjCd=" + getPrjCd();
            var packet = new AJAXPacket(url);
            packet.data.add("hsCtChooseId", hsCtChooseId);
            packet.data.add("clLqStatus", clLqStatus.val());
            packet.data.add("clLqTime", clLqTime.val());
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("提交成功！");
                    //$this.closest("tr").hide();
                    //$("input[name=clLqStatus" + hsCtChooseId + "]", thisTr).attr("disabled", "disabled");
                    //clLqTime.attr("readonly", "readonly").attr("disabled", "disabled");
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 确认 领取购房合同材料
     */
    cfmQdht: function (obj) {
        var $this = $(obj);
        //获取到最大的一个table 然后find 签合同
        var thisTable = $this.closest("table.everyChHsTable");
        var newHsId = $("input[name=newHsId]", $this.closest("div.js_panel_context")).val();
        var hsCtChooseId = $("input[name=hsCtChooseId]", $this.closest("div.js_panel_context")).val();
        // 入住时间
        var rzTime = $("input[name=rzTime]", thisTable).val();
        // 签订购房合同
        var qdGfStatus = $("input[name=qdGfStatus" + hsCtChooseId + "]:checked", thisTable).val();
        var qdGfTime = $("input[name=qdGfTime]", thisTable).val();
        // 物业供暖费用
        var jsTime = $("input[name=jsTime]", thisTable).val();
        var jsStatus = $("input[name=jsStatus" + hsCtChooseId + "]:checked", thisTable).val();
        var cmpQnCost = $("input[name=cmpQnCost]", thisTable).val();
        var cmpWyCost = $("input[name=cmpWyCost]", thisTable).val();
        var grQnCost = $("input[name=grQnCost]", thisTable).val();
        //var jsDocIds = $("input[name=docIds]", thisTable).val();

        if (!rzTime || !qdGfTime) {
            alertMsg.warn("签约时间、入住时间必须指定！");
        } else {
            var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-cfmQdht.gv?prjCd=" + getPrjCd();
            var packet = new AJAXPacket(url);
            packet.data.add("hsCtChooseId", hsCtChooseId);
            packet.data.add("newHsId", newHsId);
            packet.data.add("jsTime", jsTime);
            packet.data.add("jsStatus", jsStatus);
            packet.data.add("rzTime", rzTime);
            packet.data.add("qdGfStatus", qdGfStatus);
            packet.data.add("qdGfTime", qdGfTime);
            packet.data.add("cmpQnCost", cmpQnCost);
            packet.data.add("cmpWyCost", cmpWyCost);
            packet.data.add("grQnCost", grQnCost);
            //packet.data.add("jsDocIds", jsDocIds);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("提交成功！");
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 提交退租申请
     */
    submitTzsq: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult2 = $("select[name=procResult2]", $table).val();
        var comment2 = $("textarea[name=comment2]", $table).val();
        var recordTime2 = $("input[name=recordTime2]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitTzsh.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult2", procResult2);
        packet.data.add("comment2", comment2);
        packet.data.add("recordTime2", recordTime2);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交完成退租
     */
    submitWctz: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult11 = $("select[name=procResult11]", $table).val();
        var comment11 = $("textarea[name=comment11]", $table).val();
        var recordTime11 = $("input[name=recordTime11]", $table).val();
        var hsPubZj = $("input[name=hsPubZj]", $table).val();
        var hsPubGhf = $("input[name=hsPubGhf]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitWctz.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsPubZj", hsPubZj);
        packet.data.add("hsPubGhf", hsPubGhf);
        packet.data.add("procResult11", procResult11);
        packet.data.add("comment11", comment11);
        packet.data.add("recordTime11", recordTime11);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交私房核验
     */
    submitSfhy: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult3 = $("select[name=procResult3]", $table).val();
        var comment3 = $("textarea[name=comment3]", $table).val();
        var recordTime3 = $("input[name=recordTime3]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitSfhy.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult3", procResult3);
        packet.data.add("comment3", comment3);
        packet.data.add("recordTime3", recordTime3);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交网签缴税核税
     */
    submitWqjs: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult4 = $("select[name=procResult4]", $table).val();
        var comment4 = $("textarea[name=comment4]", $table).val();
        var recordTime4 = $("input[name=recordTime4]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitWqjs.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult4", procResult4);
        packet.data.add("comment4", comment4);
        packet.data.add("recordTime4", recordTime4);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交网签缴税核税
     */
    submitZrdj: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult5 = $("select[name=procResult5]", $table).val();
        var comment5 = $("textarea[name=comment5]", $table).val();
        var recordTime5 = $("input[name=recordTime5]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitZrdj.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult5", procResult5);
        packet.data.add("comment5", comment5);
        packet.data.add("recordTime5", recordTime5);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交领取新房产证
     */
    submitLqxz: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult6 = $("select[name=procResult6]", $table).val();
        var comment6 = $("textarea[name=comment6]", $table).val();
        var recordTime6 = $("input[name=recordTime6]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitLqxz.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult6", procResult6);
        packet.data.add("comment6", comment6);
        packet.data.add("recordTime6", recordTime6);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交腾房交房
     */
    submitTfjf: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult7 = $("select[name=procResult7]", $table).val();
        var comment7 = $("textarea[name=comment7]", $table).val();
        var recordTime7 = $("input[name=recordTime7]", $table).val();
        var jfAttr1 = $("input[name=jfAttr1]:checked", $table).val();
        var jfAttr2 = $("input[name=jfAttr2]:checked", $table).val();
        var jfAttr3 = $("input[name=jfAttr3]:checked", $table).val();
        var jfAttr4 = $("input[name=jfAttr4]:checked", $table).val();
        var jfAttr5 = $("input[name=jfAttr5]:checked", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitTfjf.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult7", procResult7);
        packet.data.add("comment7", comment7);
        packet.data.add("recordTime7", recordTime7);
        packet.data.add("jfAttr1", jfAttr1);
        packet.data.add("jfAttr2", jfAttr2);
        packet.data.add("jfAttr3", jfAttr3);
        packet.data.add("jfAttr4", jfAttr4);
        packet.data.add("jfAttr5", jfAttr5);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交 房屋核验-----天桥
     */
    submitFwhyTQ: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult12 = $("input[name=procResult12]:checked", $table).val();
        var comment12 = $("textarea[name=comment12]", $table).val();
        var recordTime12 = $("input[name=recordTime12]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitFwhyTQ.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult12", procResult12);
        packet.data.add("comment12", comment12);
        packet.data.add("recordTime12", recordTime12);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交 资格审核-----天桥
     */
    submitZgshTQ: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult14 = $("input[name=procResult14]:checked", $table).val();
        var comment14 = $("textarea[name=comment14]", $table).val();
        var recordTime14 = $("input[name=recordTime14]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitZgshTQ.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult14", procResult14);
        packet.data.add("comment14", comment14);
        packet.data.add("recordTime14", recordTime14);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交 配售审核-----天桥
     */
    submitPsshTQ: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult13 = $("input[name=procResult13]:checked", $table).val();
        var comment13 = $("textarea[name=comment13]", $table).val();
        var recordTime13 = $("input[name=recordTime13]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitPsshTQ.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult13", procResult13);
        packet.data.add("comment13", comment13);
        packet.data.add("recordTime13", recordTime13);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交 发放购房合同处理-----什刹海
     */
    submitFfhtSCH: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult15 = $("input[name=procResult15]:checked", $table).val();
        var comment15 = $("textarea[name=comment15]", $table).val();
        var recordTime15 = $("input[name=recordTime15]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitFfhtSCH.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult15", procResult15);
        packet.data.add("comment15", comment15);
        packet.data.add("recordTime15", recordTime15);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },
    /**
     * 提交安置协议结算
     */
    submitAzjs: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult8 = $("select[name=procResult8]", $table).val();
        var comment8 = $("textarea[name=comment8]", $table).val();
        var recordTime8 = $("input[name=recordTime8]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitAzjs.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult8", procResult8);
        packet.data.add("comment8", comment8);
        packet.data.add("recordTime8", recordTime8);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交面积补差结算
     */
    submitMjjs: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult16 = $("select[name=procResult16]", $table).val();
        var comment16 = $("textarea[name=comment16]", $table).val();
        var recordTime16 = $("input[name=recordTime16]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitMjjs.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult16", procResult16);
        packet.data.add("comment16", comment16);
        packet.data.add("recordTime16", recordTime16);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交 户口外迁OA审核
     */
    submitQhsh: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult9 = $("select[name=procResult9]", $table).val();
        var comment9 = $("textarea[name=comment9]", $table).val();
        var recordTime9 = $("input[name=recordTime9]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitQhsh.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult9", procResult9);
        packet.data.add("comment9", comment9);
        packet.data.add("recordTime9", recordTime9);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交 支付户口外欠费
     */
    submitZffy: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult10 = $("select[name=procResult10]", $table).val();
        var comment10 = $("textarea[name=comment10]", $table).val();
        var recordTime10 = $("input[name=recordTime10]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitZffy.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult10", procResult10);
        packet.data.add("comment10", comment10);
        packet.data.add("recordTime10", recordTime10);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 提交 领取新租赁合同
     */
    submitLqxht: function (obj) {
        var $this = $(obj);
        var $table = $this.closest("table");
        var procResult11 = $("select[name=procResult11]", $table).val();
        var comment11 = $("textarea[name=comment11]", $table).val();
        var recordTime11 = $("input[name=recordTime11]", $table).val();
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();

        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-submitLqxht.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("procResult11", procResult11);
        packet.data.add("comment11", comment11);
        packet.data.add("recordTime11", recordTime11);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("提交成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj, relId) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));

        var relType = $span.attr("relType");

        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?docTypeName=" + docTypeName
            + "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=" + relType + "&relId=" + relId;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            close: ph008.docClosed,
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
            if (label.html().startWith("上传")) {
                label.html(label.html().replace("上传", "查看"));
            }
        } else {
            var label = $span.find("label");
            if (label.html().startWith("查看")) {
                label.html(label.html().replace("查看", "上传"));
            }
        }
        // 调用文件的关闭
        return file.closeD();
    },


    /**
     * 折叠合并窗口。
     */
    extendOrClosePanel: function (clickObj) {
        var $pucker = $(clickObj);
        var $content = $pucker.closest("div.panelHeader").next("div.panelContent");
        if ($pucker.hasClass("collapsable")) {
            $pucker.removeClass("collapsable").addClass("expandable");
            $content.hide();
        } else {
            $pucker.removeClass("expandable").addClass("collapsable");
            $content.show();
        }
    }
};