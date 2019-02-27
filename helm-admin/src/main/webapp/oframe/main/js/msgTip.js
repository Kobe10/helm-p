var MsgTip = {

    container: null,

    templateMsg: null,

    init: function () {
        MsgTip.container = $("#messageTipArea");
        MsgTip.templateMsg = MsgTip.container.find("div.messageTipItem:eq(0)");
    },

    /**
     * 消息列表中增加消息
     * @param messageInfo {messageId: "100", title: "你有新的消息001", messgage: "张三提交给你你一个信息的审批"}
     */
    addMsg: function (messageInfo) {
        var newItem = MsgTip.templateMsg.clone();
        MsgTip.container.append(newItem);
        newItem.find("span.messageTitle").html(messageInfo.title);
        newItem.find("div.messageTipContext").html(messageInfo.messgage);
        newItem.slideDown(1500);
        var closeBtn = newItem.find(".closeMessage");
        closeBtn.bind("click", function () {
            MsgTip.closeMsg(this);
        });
        // 定时器关闭，弹出的消息
        var timmer = setTimeout(function () {
            MsgTip.closeMsg(closeBtn);
        }, 5000)
    },

    /**
     * 关闭消息对话框
     * @param obj 关闭按钮对象
     */
    closeMsg: function (obj) {
        var $closeBtn = $(obj);
        $closeBtn.closest("div.messageTipItem").slideUp(1000, function () {
            $(this).remove();
        });
    }

};
$(function () {
    MsgTip.init();
});


