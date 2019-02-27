/**
 * Created by Administrator on 2014/9/10.
 */
sys010={
    getUrl: function (obj) {
        return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?subSvcName=sys002Data";
    },
    getOption: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].STAFF_CODE ;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.STAFF_CODE + "-->" + data.STAFF_NAME;
            },
            mustMatch: true,
            remoteDataType: "json",
            autoFill: true
        }
    },

    check_input_box:function (){
    var rht_name_box = document.getElementById("staff_code")//获取权限名称输入框，如果获取不到，说明当前登陆用户没有查看所有的权限
    if(rht_name_box != null){
        document.getElementById("staffId_hidden").value=null;
    }
    Query.queryList('sys010frm', 'sys010_w_list_print');
},
    delete:function(staffId,rhtId){

        alertMsg.confirm("确认要删除此收藏么?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys010/sys010-delFav.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("staffId", staffId);
                ajaxPacket.data.add("rhtId", rhtId);
                ajaxPacket.data.add("prjCd", getPrjCd());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
    },
    /**
     * 打开快速检索对话框
     */
    openQSch: function () {
        var dialogId = "sys010Create";
        var div = $("#" + dialogId, navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },
    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function () {
        var dialogId = "sys010Create";
        $("#" + dialogId, navTab.getCurrentPanel()).hide().removeAttr("active");
    }

};
$(document).ready(function () {
    sys010.check_input_box();
});






