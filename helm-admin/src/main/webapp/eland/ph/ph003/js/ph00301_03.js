/**
 * Created by shfb_wang on 2015/3/17.
 */
var ph00301_03 = {

    /**
     * 根据区域刷新赠送面积页面
     */
    changeChReg: function (obj) {
        //切换了选房赠送区域，清空选房人
        $(obj).closest("table").find("input[name=hsCtBId]", $.pdialog.getCurrent()).val("");
        $(obj).closest("table").find("input[name=hsCtBIdName]", $.pdialog.getCurrent()).val("");
    },

    saveGive: function (hsId) {
        var $frm = $("#ph00301GiveFrm", $.pdialog.getCurrent());
        if (!$frm.valid()) {
            return;
        }
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-saveGive.gv";
        var ttRegId = $("select[name=gvReg]", $.pdialog.getCurrent()).val();
        var hsCtAId = $("input[name=hsCtAId]", $.pdialog.getCurrent()).val();
        var hsCtBId = $("input[name=hsCtBId]", $.pdialog.getCurrent()).val();
        var ctSize = $("input[name=giveSize]", $.pdialog.getCurrent()).val();
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("ttRegId", ttRegId);
        ajaxPacket.data.add("hsCtAId", hsCtAId);
        ajaxPacket.data.add("hsCtBId", hsCtBId);
        ajaxPacket.data.add("ctSize", ctSize);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("赠送成功！");
                $.pdialog.closeCurrent(this);
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    //校验赠送的面积是否大于实际面积
    checkSize: function (obj) {
        var $this = $(obj);
        var giveSize = $(obj).val();
        var sjSize = $("select[name=gvReg]", $.pdialog.getCurrent()).find("option:selected").attr("cangivesize");
        if (parseFloat(giveSize) > parseFloat(sjSize)) {
            $this.addClass("inputWarn").val("赠送面积不能大于：" + sjSize);
        } else {
            $this.removeClass("inputWarn");
        }

    },

    checkIn: function (obj) {
        var $this = $(obj);
        var giveSize = $(obj).val();
        if (giveSize.startWith("赠送面积不能大于")) {
            $this.val("");
        }
    },

    cancelGive: function (obj) {
        var $this = $(obj);
        var hsCtGiveId = $("input[name=hsCtGiveId]", $.pdialog.getCurrent()).val();
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-cancelGive.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("hsCtGiveId", hsCtGiveId);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("取消赠送成功！");
                $.pdialog.closeCurrent(obj);
            } else {
                alertMsg.warn(jsonData.errorMsg);
            }
        });
    },

    /**
     * 获取赠送人
     * @param obj
     * @returns {string}
     */
    getUrl: function (obj) {
        //var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();
        //var chRegId = $("select[name=gvReg]", $.pdialog.getCurrent()).find("option:selected").val();
        return getGlobalPathRoot() + "eland/ph/ph003/ph00301-findGvPerson.gv?prjCd=" + getPrjCd();
    },
    getOption: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].ctPsNames;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.ctPsNames;
            },
            mustMatch: false,
            remoteDataType: "json",
            autoFill: false,
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                $("input[name=hsCtBId]", $.pdialog.getCurrent()).val(obj.data.hsCtId);
                $("input[name=hsCtBIdName]", $.pdialog.getCurrent()).val(obj.data.ctPsNames);
            },
            onNoMatch: function (obj) {
                $("input[name=hsCtBId]", $.pdialog.getCurrent()).val("");
            },
            dyExtraParams: function (obj) {
                var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();
                var chRegId = $("select[name=gvReg]", $.pdialog.getCurrent()).find("option:selected").val();
                return {
                    gvRegId: chRegId,
                    hsId: hsId
                }
            }
        }
    }
};