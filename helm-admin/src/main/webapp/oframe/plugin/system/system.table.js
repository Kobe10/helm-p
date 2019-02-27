var table = {
    /**
     * 拷贝增加行
     */
    addRow: function (tableId, clickObject) {
        var $table = $("#" + tableId);
        var hiddenRow = $table.find("tr:eq(1)");
        var copyRow = $(hiddenRow).clone().removeClass("hidden");
        // 获取行号
        if (clickObject) {
            var index = $table.find("tr").index($(clickObject).parent().parent());
            if (index == 0) {
                $(hiddenRow).after(copyRow);
            } else {
                $(clickObject).parent().parent().after(copyRow);
            }
        } else {
            $table.append(copyRow);
        }
        $(copyRow).initUI();
        return copyRow;
    },

    /**
     * 拷贝增加行
     */
    addRowNoInit: function (tableId, clickObject) {
        var $table = $("#" + tableId);
        var hiddenRow = $table.find("tr:eq(1)");
        var copyRow = $(hiddenRow).clone().removeClass("hidden");
        // 获取行号
        if (clickObject) {
            var index = $table.find("tr").index($(clickObject).parent().parent());
            if (index == 0) {
                $table.append(copyRow);
            } else {
                $(clickObject).parent().parent().after(copyRow);
            }
        } else {
            $table.append(copyRow);
        }
        return copyRow;
    },
    /**
     * 拷贝增加行
     */
    deleteRow: function (clickObject) {
        $(clickObject).closest("tr").remove();
    },

    /**
     * 行上移
     */
    upRow: function (tableId, clickObject) {
        // 获取行号
        var index = $("#" + tableId).find("tr").index($(clickObject).parent().parent());
        if (index == 2) {

        } else {
            var trObj = $(clickObject).parent().parent();
            $(trObj).prev().before($(trObj));
        }
    },
    /**
     * 行下移动
     */
    downRow: function (tableId, clickObject) {
        // 获取行号
        var index = $("#" + tableId).find("tr").index($(clickObject).parent().parent());
        if (index == 1) {

        } else {
            var trObj = $(clickObject).parent().parent();
            $(trObj).next().after($(trObj));
        }
    }
};