var ph001001 = {
    /**
     * 删除
     * @param buildId
     */
    deletehouse: function (hsId) {
        alertMsg.confirm("是否删除该房屋？", {
            okCall: function () {
                var packet = new AJAXPacket();
                // 区域归属项目
                packet.data.add("houseId", hsId);
                packet.data.add("prjCd", getPrjCd());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-deleteHouse.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        //重新查询数据
                        $(document).ready(function () {
                            Page.query($("#queryForm", navTab.getCurrentPanel()));
                        });

                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }
        });
    },

    /**
     * 添加
     * @param buildId
     */
    add: function () {
        var url = "eland/ph/ph003/ph00301-initS.gv";
        index.openTab(null, "ph00301-initS", url, {title: "居民录入", fresh: true});
    },

    /**
     * 房屋信息
     * @param buildId
     */
    openHouseView:function(hsId){
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.openTab(null, "ph003-init", url, {title: "居民信息", fresh: true});
    }

};