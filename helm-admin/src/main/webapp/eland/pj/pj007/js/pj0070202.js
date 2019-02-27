var pj0070202 = {
    /**
     * 设置行配置信息
     */
    setRow: function (fromOp) {
        var container = null;
        if (fromOp == "pj00702") {
            container = pj00702PicContainer;
        } else if (fromOp == "pj004") {
            container = pj004.getContainer();
        } else {
            return;
        }
        // 设置的楼层下标
        var floorIdx = parseInt($("input[name=floorIdx]", $.pdialog.getCurrent()).val());
        var floorNm = $("input[name=floorNm]", $.pdialog.getCurrent()).val();
        var bakFloorNm = container.data.build.floor[floorIdx];
        if (bakFloorNm != floorNm) {
            container.data.build.floor[floorIdx] = floorNm;
            // 设置户型编号
            var entityKeys = container.entities.keys();
            for (var idx in entityKeys) {
                var key = entityKeys[idx];
                var entity = container.entities.get(key);
                if (entity.getData().floor == floorIdx
                    && entity.getData().type == 3) {
                    var door = entity.getData().door;
                    var unit = entity.getData().unit;
                    var doorNm = container.data.build.units[unit].door[door].nbm;
                    entity.changeEntity({"text": floorNm + doorNm})
                }
            }
        }
        $.pdialog.closeCurrent();
    }
}