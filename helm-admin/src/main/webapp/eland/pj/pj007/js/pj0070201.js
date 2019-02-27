var pj0070201 = {
    /**
     * 根据输入的楼层，单元， 户数生成配置信息
     * flag: 1: 楼层修改 2：单元修改 3：户数修改
     */
    flashSetting: function (flag) {
        // 楼层处理
        var floorNum = parseInt($("input[name=initFloorNm]", $.pdialog.getCurrent()).val());
        var initFloorNmBak = parseInt($("input[name=initFloorNmBak]", $.pdialog.getCurrent()).val());
        if ((flag == "1" || flag == "") && initFloorNmBak != floorNum) {
            var floorSettingUl = $("ul.floorSetting", $.pdialog.getCurrent());
            var floorSetTemplate = $("li.hidden", floorSettingUl);
            // 删除原有的数据
            floorSettingUl.find("li").each(function () {
                var _this = $(this);
                if (!_this.hasClass("hidden")) {
                    _this.remove();
                }
            });
            for (var i = 1; i <= floorNum; i++) {
                var tempItem = floorSetTemplate.clone();
                $("input[name=floorNb]", tempItem).val(i);
                floorSettingUl.append(tempItem);
                tempItem.removeClass("hidden");
                tempItem.show();
            }
            $("input[name=initFloorNmBak]", $.pdialog.getCurrent()).val(floorNum);
        }

        // 单元处理
        var unNo = parseInt($("input[name=initUnNm]", $.pdialog.getCurrent()).val());
        var unNoBak = parseInt($("input[name=initUnNmBak]", $.pdialog.getCurrent()).val());
        if ((flag == "2" || flag == "") && unNo != unNoBak) {
            var unitSettingUl = $("ul.unitSetting", $.pdialog.getCurrent());
            var unitSetTemplate = $("li.hidden", unitSettingUl);
            // 删除原有的数据
            unitSettingUl.find("li").each(function () {
                var _this = $(this);
                if (!_this.hasClass("hidden")) {
                    _this.remove();
                }
            });
            for (var i = 1; i <= unNo; i++) {
                var tempItem = unitSetTemplate.clone();
                $("input[name=unitNb]", tempItem).val(i + "单元").attr("idx", i);
                unitSettingUl.append(tempItem);
                tempItem.removeClass("hidden");
            }
            //
            $("input[name=unNoBak]", $.pdialog.getCurrent()).val(unNo);
            //户型数量发生编号
            // 获取当前有的所有单元
            var unitNames = new Array()
            $("input[name=unitNb]", $.pdialog.getCurrent()).each(function () {
                var unitName = $(this).val();
                if (unitName != "") {
                    unitNames.push(unitName);
                }
            });
            // 设置模板中的可选单元
            $("select[name=hsUnit]", $.pdialog.getCurrent()).each(function () {
                var $this = $(this);
                //
                var oldValue = $this.val();
                // 清空所有选项
                $this.html("");
                for (var idx = 0; idx < unitNames.length; idx++) {
                    var optionHtml = "<option idx='" + (idx + 1) + "', value='" + unitNames[idx] + "'>" + unitNames[idx] + "</option>";
                    $this.append(optionHtml);
                }
                $this.val(oldValue);
            })
        }

        // 户籍处理
        var houseNum = parseInt($("input[name=initHouseNm]", $.pdialog.getCurrent()).val());
        var houseNumBak = parseInt($("input[name=initHouseNmBak]", $.pdialog.getCurrent()).val());
        if ((flag == "3" || flag == "") && houseNum != houseNumBak) {
            var houseSettingUl = $("ul.houseSetting", $.pdialog.getCurrent());
            var houseSetTemplate = $("li.hidden", houseSettingUl);
            // 删除原有的数据
            houseSettingUl.find("li").each(function () {
                var _this = $(this);
                if (!_this.hasClass("hidden")) {
                    _this.remove();
                }
            });
            for (var i = 1; i <= houseNum; i++) {
                var tempItem = houseSetTemplate.clone();
                var text = i;
                if (text < 10) {
                    text = "0" + i;
                }
                $("input[name=hsNm]", tempItem).val(text);
                houseSettingUl.append(tempItem);
                tempItem.removeClass("hidden");
            }
            //
            $("input[name=initHouseNmBak]", $.pdialog.getCurrent()).val(houseNum);
        }
    },

    /**
     * 修改单元名称
     */
    changeUnitName: function (obj) {
        var newValue = $(obj).val();
        var idx = $(obj).attr("idx");
        // 设置模板中的可选单元
        $("select[name=hsUnit]", $.pdialog.getCurrent()).each(function () {
            var matchOption = $("option[idx='" + idx + "']", $(this));
            if (matchOption) {
                matchOption.val(newValue);
                matchOption.text(newValue);
            }
        });
    },
    /**
     * 根据单元数，楼层数， 户数生成房源信息
     */
    initHouseInfo: function (fromOp) {
        var container = null;
        if (fromOp == "pj00702") {
            container = pj00702PicContainer;
        } else if (fromOp == "pj004") {
            container = pj004.getContainer();
        } else {
            return;
        }
        // 获取输入参数
        var floorNum = parseInt($("input[name=initFloorNm]", $.pdialog.getCurrent()).val());

        // 楼层信息
        var floorArr = new Array();
        var floorSettingUl = $("ul.floorSetting", $.pdialog.getCurrent());
        // 删除原有的数据
        floorSettingUl.find("li").each(function () {
            var _this = $(this);
            if (!_this.hasClass("hidden")) {
                floorArr.push($("input[name=floorNb]", _this).val());
            }
        });

//        var unNo = parseInt($("input[name=initUnNm]", $.pdialog.getCurrent()).val());
//        var houseNum = parseInt($("input[name=initHouseNm]", $.pdialog.getCurrent()).val());
//
//
//        // 单元信息
        var unitArr = new Array();
        var unitSettingUl = $("ul.unitSetting", $.pdialog.getCurrent());
        // 删除原有的数据
        unitSettingUl.find("li").each(function () {
            var _this = $(this);
            var unitInfo = {};
            if (!_this.hasClass("hidden")) {
                unitInfo.name = $("input[name=unitNb]", _this).val();
                unitInfo.door = new Array();
                unitArr.push(unitInfo);
            }
        });

        // 户型信息补充到单元
        var doorInfo = new Array();
        var houseSettingUl = $("ul.houseSetting", $.pdialog.getCurrent());
        // 删除原有的数据
        houseSettingUl.find("li").each(function () {
            var _this = $(this);
            if (!_this.hasClass("hidden")) {
                var door = {};
                door.nbm = $("input[name=hsNm]", _this).val();
                door.hx = $("select[name=hsHx]", _this).val();
                door.hxName = $("select[name=hsHx]", _this).find("option:selected").text();
                var unitName = $("select[name=hsUnit]", _this).val();
                var matchUnit;
                for (var j = 0; j < unitArr.length; j++) {
                    if (unitName == unitArr[j].name) {
                        matchUnit = unitArr[j];
                        break;
                    }
                }
                // 添加到匹配的单元
                if (matchUnit) {
                    matchUnit.door.push(door);
                }
            }
        });

        // 组织数据，分配户型到每个单元，组织成一个节点
        var buildJson = {};
        buildJson.floor = floorArr;
        buildJson.units = new Array();
        // 过滤掉没有房间的单元
        for (var i = 0; i < unitArr.length; i++) {
            if (unitArr[i].door.length > 0) {
                buildJson.units.push(unitArr[i]);
            }
        }
        // 绘制图像
        pj0070201.paintBuildind(buildJson, container);
        // 标记需要重新生成房源数据
        container.data.houseGenerated = false;
        $.pdialog.closeCurrent();
    },

    /**
     *  根据楼房数据配置绘制图像
     * @param buildJson 数据配置
     */
    paintBuildind: function (buildJson, container) {
        // 清空原始数据
        container._paper.clear();
        container.entities = new Map();
        container.sltEntities = new Map();
        container.data.build = buildJson;
        // 楼房区块的高度
        var buildEntityHeight = 20;
        // 单元区块的高度
        var buildUnitHeight = 20;
        // 单元间的间隔宽度
        var buildUnitWidthBwt = 5;

        // 计算户籍的高宽
        var floorNm = buildJson.floor.length;
        var doorNm = 0;
        for (var idx in buildJson.units) {
            doorNm = doorNm + buildJson.units[idx].door.length;
        }
        var unWidth = parseInt((container.width - (buildJson.units.length - 1) * buildUnitWidthBwt) / doorNm);
        var unHeight = parseInt((container.height - buildEntityHeight - buildUnitHeight) / floorNm);

        // 绘制上面的楼房信息
        var jsonData = {"x": 0, "y": 0, "fillOpacity": "1",
            "width": unWidth * doorNm + (buildUnitWidthBwt - 1) * buildUnitWidthBwt, "height": buildEntityHeight,
            "text": container.data.regAddr,
            "color": "#99FFCC", "borderSize": 2}
        var titleEntity = new Entity(container, jsonData);
        // 楼房户籍信息
        titleEntity.getData().type = 1;

        //
        var buildUnitStartX = 0;
        for (var i = 1 in buildJson.units) {
            //  获取单元
            var unit = buildJson.units[i];
            var doorArr = unit.door;
            // 循环房间
            for (var k in doorArr) {
                var doorNbm = doorArr[k].nbm;
                var doorHxName = doorArr[k].hxName;
                // 循环楼层
                var totalFloor = buildJson.floor.length;
                for (var j in buildJson.floor) {

                    var hsNm = buildJson.floor[j] + doorNbm;
                    var startH = buildEntityHeight + unHeight * (totalFloor - j - 1);
                    var showText = hsNm;
                    if (doorHxName && doorHxName != "") {
                        showText = hsNm + "(" + doorHxName + ")";
                    }
                    var jsonData = {"x": buildUnitStartX + k * unWidth, "fillOpacity": "1",
                        "y": startH, "width": unWidth, "height": unHeight, "text": showText}
                    var entity = new Entity(container, jsonData);
                    // 保存单元，门，楼层信息
                    entity.getData().regEntityType = 4;
                    entity.getData().hsNm = hsNm;
                    entity.getData().unit = i;
                    entity.getData().door = k;
                    entity.getData().floor = j;
                    entity.getData().type = 3;
                }
            }
            // 绘制单元信息
            var buildUnitWidth = unWidth * doorArr.length;
            // 绘制单元
            var jsonData = {"x": buildUnitStartX, "y": buildEntityHeight + unHeight * totalFloor,
                "width": buildUnitWidth, "height": buildUnitHeight, "text": unit.name,
                "color": "#CCCCCC", "borderSize": 2, "fillOpacity": "1"}
            var unitEntity = new Entity(container, jsonData);
            unitEntity.getData().unit = i;
            unitEntity.getData().type = 2;

            // 下一个单元开始位置
            buildUnitStartX = buildUnitStartX + buildUnitWidth + buildUnitWidthBwt;
            //
        }
        // 选中第一个
        var count = container.entities.size();
        if (count > 0) {
            // 触发第一个的选中事件
            var entity = container.entities.values()[0];
            entity.getEntityDom().click();
        }
    }
}
$(document).ready(function () {
    pj0070201.flashSetting("");
    $("input[name=initFloorNm]", $.pdialog.getCurrent()).focus();
});