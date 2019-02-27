var pj00101 = {
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
                    dataRow.value = result[i].STAFF_CODE + "-->" + result[i].STAFF_NAME;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.STAFF_CODE + "-->" + data.STAFF_NAME;
            },
            mustMatch: true,
            remoteDataType: "json",
            onItemSelect: function (obj) {
                $("input[name=staffId]", navTab.getCurrentPanel()).val(obj.data.STAFF_ID);
            }
        }
    },
    openPrj: function (method, prjCd) {
        var url = getGlobalPathRoot() + "eland/pj/pj001/pj001-init.gv?method="
            + method + "&prjCd=" + prjCd;
        $.pdialog.open(url, "pj00101-" + prjCd, "项目信息",
            {mask: true, max: true});
    }
};