/**
 * Created by Administrator on 2016/6/23.
 */
var oframedesign = {
    /**
     * 请求服务翻译成html
     */
    fnParseOptions: function () {
        var html = "";
        var inputs = document.getElementsByTagName("input");
        var url = getGlobalPathRoot() + "oframe/plugin/ueditor/formdesign/shfbHTML/formdesign-toHtml.gv";
        var packet = new AJAXPacket(url);
        var tempArr = [];
        $(inputs).each(function () {
            packet.data.add($(this).attr("id"), $(this).val());
            tempArr.push($(this).attr("id"));
        });
        packet.data.add("tempArr", tempArr.toString());
        core.ajax.sendPacketHtml(packet, function (response) {
            html = response;
        }, false);
        return html;
    },

    /**
     * 初始化校正蒙板
     * @param $inContainer
     */
    initCoverHeight: function ($inContainer) {
        var $container = $inContainer;
        if (!$container || $container.length == 0) {
            $container = $("body");
        }
        if ($container.hasClass("oframeplugin")) {
            var toEditObj = $inContainer;
            var newObjWidth = $(toEditObj).width();
            var newObjHeight = $(toEditObj).height();
            var coverSpan = $("span.oframecover", toEditObj);
            coverSpan.height(newObjHeight + "px");
            coverSpan.width(newObjWidth + "px");
        } else {
            $container.find("span.oframeplugin").each(function () {
                var toEditObj = $(this);
                var newObjWidth = $(toEditObj).width();
                var newObjHeight = $(toEditObj).height();
                var coverSpan = $("span.oframecover", toEditObj);
                coverSpan.height(newObjHeight + "px");
                coverSpan.width(newObjWidth + "px");
            });
        }
    }
}