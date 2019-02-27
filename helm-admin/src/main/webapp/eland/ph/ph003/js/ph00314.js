var ph00314 = {
    /**
     * 编辑完毕点击提交按钮
     */
    submitRecord: function () {
        var $form = $("#ph00314frm", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00314-saveTalkRecord.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("记录成功");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    /**
     * 编辑完毕点击提交按钮
     */
    deleteRecord: function (recordId) {
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00314-deleteRecord.gv?prjCd=" + getPrjCd();
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("recordId", recordId);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("记录成功");
                Page.query($("#_ph00314frm", $.pdialog.getCurrent()), "");
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },
    /**
     * 点击编辑按钮
     */
    editRecord: function (recordId) {
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00314-initRecord.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("recordId", recordId);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var r = $(response);
            $("#ph00314FrmDiv", $.pdialog.getCurrent()).html(r).initUI();
            $(".edit_hidden", $.pdialog.getCurrent()).addClass("hidden");
        });
    },
    /**
     * 点击详情按钮
     */
    viewRecord: function (recordId) {
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00314-initRecord.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("recordId", recordId);
        ajaxPacket.data.add("prjCd", getPrjCd());
        ajaxPacket.data.add("method", "view");
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var r = $(response);
            $("#ph00314FrmDiv", $.pdialog.getCurrent()).html(r).initUI();
            $(".edit_hidden", $.pdialog.getCurrent()).addClass("hidden");
        });
    },

    /**
     * 控制谈话记录和谈话内容交替隐藏
     */
    changeShowInfo: function () {
        var isHidden = $(".def_hidden", $.pdialog.getCurrent()).hasClass('hidden');
        if (isHidden) {
            $(".def_hidden", $.pdialog.getCurrent()).removeClass('hidden');
            $(".def_show", $.pdialog.getCurrent()).addClass('hidden');
            // 图标交替
            $(".change_img", $.pdialog.getCurrent()).removeClass("reveal");
            $(".change_img", $.pdialog.getCurrent()).addClass("conceal");
            // 按钮交替
            $(".set_hidden", $.pdialog.getCurrent()).show();
            $(".set_show", $.pdialog.getCurrent()).hide();
        } else {
            $(".def_hidden", $.pdialog.getCurrent()).addClass('hidden');
            $(".def_show", $.pdialog.getCurrent()).removeClass('hidden');
            // 图标交替

            $(".change_img", $.pdialog.getCurrent()).addClass("reveal");
            $(".change_img", $.pdialog.getCurrent()).removeClass("conceal");
            // 按钮交替
            $(".set_hidden", $.pdialog.getCurrent()).hide();
            $(".set_show", $.pdialog.getCurrent()).show();
        }
    }
};
$(document).ready(function () {
    $("textarea.publicEditor", $.pdialog.getCurrent()).each(function () {
        var $this = $(this);
        var edtOpt = $this.attr("edtOpt");
        var uneditUE = $this.hasClass("uneditUE");
        var option = {};
        if (edtOpt) {
            option = eval('(' + edtOpt + ')');
        }
        var publicEditor;
        if (uneditUE) {
            publicEditor = {
                minFrameHeight: 300,
                toolbars: [
                    ['undo', 'redo', '|', 'formatmatch', 'bold', 'italic', 'underline', '|',
                        'customstyle', 'paragraph', 'fontfamily', 'fontsize', 'justify', '|', 'insertimage', 'attachment', '|', 'cleardoc', 'fullscreen']
                ],
                readonly: true
            };
        } else {
            publicEditor = {
                minFrameHeight: 300,
                toolbars: [
                    ['undo', 'redo', '|', 'formatmatch', 'bold', 'italic', 'underline', '|',
                        'customstyle', 'paragraph', 'fontfamily', 'fontsize', 'justify', '|', 'insertimage', 'attachment', '|', 'cleardoc', 'fullscreen']
                ]
            };
        }

        $.extend(option, publicEditor);
        var editor = new UE.ui.Editor(option);
        editor.render(this);
    });
});