var pj0070203 = {
    setCol: function (fromOp) {
        var container = null;
        if (fromOp == "pj00702") {
            container = pj00702PicContainer;
        } else if (fromOp == "pj004") {
            container = pj004.getContainer();
        } else {
            return;
        }
        // 设置的楼层下标
        var unitIdx = parseInt($("input[name=unitIdx]", $.pdialog.getCurrent()).val());
        var doorIdx = $("input[name=doorIdx]", $.pdialog.getCurrent()).val();
        var doorNm = $("input[name=doorNm]", $.pdialog.getCurrent()).val();
        var hsHx = "";
        var hsHxSlt = $("select[name=hsHx]", $.pdialog.getCurrent());
        if (hsHxSlt && hsHxSlt.length > 0) {
            hsHx = hsHxSlt.val();
        }
        var hsHxSltName = $("select[name=hsHx]", $.pdialog.getCurrent()).find("option:selected").text();
        container.data.build.units[unitIdx].door[doorIdx].nbm = doorNm;
        // 设置户型编号
        var entityKeys = container.entities.keys();
        for (var idx in entityKeys) {
            var key = entityKeys[idx];
            var entity = container.entities.get(key);
            if (entity.getData().type == 3 &&
                entity.getData().unit == unitIdx &&
                entity.getData().door == doorIdx) {
                var floorIdx = entity.getData().floor;
                var unit = entity.getData().unit;
                var floorNm = container.data.build.floor[floorIdx];
                entity.changeEntity({"text": floorNm + doorNm + "(" + hsHxSltName + ")"});
            }
        }
        // 设置户型
        container.data.build.units[unitIdx].door[doorIdx].hx = hsHx;
        // 关闭对话框
        $.pdialog.closeCurrent();
    }
}