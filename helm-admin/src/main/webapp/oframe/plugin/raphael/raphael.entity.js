/**
 * Created by linqiliand on 2014/4/11.
 */
function EntityContainer(_container) {
    // 工程属性
    this.container = _container;
    if (_container && typeof _container != "string") {
        this.container = $(_container);
    } else {
        this.container = $("#" + _container);
    }
    // 禁止右键菜单
    this.container.bind("contextmenu", function (e) {
        return false;
    });

    this.width = null;
    this.height = null;
    // 文档编号,兼容原来的信息
    this.imgSrc = null;
    // 文档基础路径
    this.imgBaseUrl = null;
    this.entities = new Map();
    this.sltEntities = new Map();
    this.movable = false;
    this.resizable = false;
    this.editAble = false;
    this._paper = Raphael(_container);
    this.panZoom = null;
    this.rV = new Date().getTime();

    this.data = {};

    /**
     * 清空
     */
    this.clear = function () {
        if (this.panZoom) {
            this.panZoom.zoomIn(this.panZoom.getCurrentZoom());
        }
        if (this._paper) {
            this._paper.clear();
        }
        this.width = null;
        this.height = null;
        this.imgSrc = null;
        this.imgBaseUrl = null;
        this.entities = new Map();
        this.sltEntities = new Map();
        this.movable = false;
        this.resizable = false;
        this.editAble = false;
        this.entityClick = null;
        this.entityDbclick = null;
        this.entityMouseDown = null;
        this.entityTextChange = null;
        this.data = {};
    }

    /**
     * 缩小
     */
    this.zoomIn = function () {
        this.panZoom.zoomIn(1);
    }
    /**
     * 放大
     */
    this.zoomOut = function () {
        this.panZoom.zoomOut(1);
    }

    /**
     * 缩放还原
     */
    this.zoomReset = function () {
        var currentZoom = this.panZoom.getCurrentZoom();
        if (currentZoom > 0) {
            this.panZoom.zoomOut(currentZoom);
        } else if (currentZoom < 0) {
            this.panZoom.zoomIn(0 - currentZoom);
        }
        //
        var currPos = this.panZoom.getCurrentPosition();

    }
    /**
     * 缩放还原
     */
    this.dragReset = function () {
        var currPos = this.panZoom.getCurrentPosition();
        this.panZoom.pan(currPos.x, currPos.y);
    }

    /**
     * 初始化区域统计
     */
    this.init = function () {
        // 获取整个显示区域的高度
        var calHeight = this.container.height();
        if (this.container.attr("iLayoutH")) {
            var $refBox = this.container.parents("div.layoutBox:first");
            var iRefH = $refBox.height();
            // 获取偏差高度
            var iLayoutH = parseInt(this.container.attr("iLayoutH"));
            calHeight = iRefH - iLayoutH;
        }
        var calWidth = parseInt(this.container.width());
        this.container.width(calWidth);
        this.container.height(calHeight);
        this._paper.setSize(calWidth, calHeight);
        // 计算初始缩放大小
        var useRadio = 0;
        if (this.height && this.width) {
            var yRadio = calHeight / this.height;
            var xRadio = calWidth / this.width;
            if (xRadio > yRadio) {
                useRadio = yRadio;
            } else {
                useRadio = xRadio;
            }
        } else {
            useRadio = 1;
            this.height = this._paper.height;
            this.width = this._paper.width;
        }
        //
        if (this.imgSrc && this.imgSrc != "") {
            var imgWidth = this.width;
            var imgHeight = this.height;
            this.img = this._paper.image(getGlobalPathRoot() + this.imgBaseUrl
                + this.imgSrc, 0, 0, imgWidth, imgHeight).toBack();
        } else {
            this.img = null;
        }
        //
        var calTemp = (useRadio - 1) * 10;
        var initialZoom = parseInt(calTemp);
        if (calTemp != initialZoom) {
            initialZoom = initialZoom - 1;
        }
        // 初始化大小
        var minZoom = -15;
        this.panZoom = this._paper.panzoom({initialZoom: initialZoom, initialPosition: {x: 0, y: 0}, minZoom: minZoom});
        this.panZoom.enable();
        // bug
        this._paper.safari();
        this._paper.canvas.setAttribute('preserveAspectRatio', 'none');

        this.entities = new Map();
        this.sltEntities = new Map();
    };

    /**
     * 清除背景图片
     */
    this.cleanImg = function () {
        this.imgSrc = null;
        this.imgBaseUrl = null;
        if (this.img) {
            this.img.remove();
            this.img = null;
        }
    };
    /**
     * 加载背景图片
     * @param docId
     */
    this.loadImg = function (docId) {
        this.loadImgSrc("oframe/common/file/file-downview.gv?docId=", docId);
    };

    /**
     * 加载背景图片
     * @param docId
     */
    this.loadImgSrc = function (imgBaseSrc, docId) {
        this.cleanImg();
        this.imgBaseUrl = imgBaseSrc;
        this.imgSrc = docId;
        var loadimg = new Image();
        var _container = this;
        loadimg.src = getGlobalPathRoot() + this.imgBaseUrl + this.imgSrc + "&_v=" + this.rV;
        loadimg.onload = function (e) {
            var imgWidth = loadimg.width;
            var imgHeight = loadimg.height;
            // 进行缩放的比率
            var xRatio = imgWidth / _container.width;
            var yRatio = imgHeight / _container.height;
            var useRatio = 1;
            if (xRatio > 1 || yRatio > 1) {
                if (xRatio < yRatio) {
                    useRatio = yRatio;
                } else {
                    useRatio = xRatio;
                }
            }
            // 显示的高度
            var dWidth = imgWidth / useRatio;
            var dHeight = imgHeight / useRatio;
            // 显示位置
            var xPos = (_container.width - dWidth) / 2;
            var yPos = (_container.height - dHeight) / 2;
            xPos = xPos / (1 - (_container.panZoom.getCurrentZoom() * _container.panZoom.getZoomStep()));
            yPos = yPos / (1 - (_container.panZoom.getCurrentZoom() * _container.panZoom.getZoomStep()));
            // 重新设置图片
            _container.img = _container._paper.image(loadimg.src, xPos, yPos,
                dWidth, dHeight);
        };
    };

    /**
     * 旋转背景图片
     */
    this.rotateBackImg = function (degree) {
        this.img.rotate(degree);
        this.rV = this.rV + 1;
    }

    /**
     * 转换为Json对象
     */
    this.toJson = function () {
        var json = {};
        var entityArr = [];
        var entityKeys = this.entities.keys();

        for (var entityId in entityKeys) {
            var entity = this.entities.get(entityKeys[entityId]);
            entityArr.push(entity.toJson());
        }
        json.entities = entityArr;
        json.imgBaseUrl = this.imgBaseUrl;
        json.imgSrc = this.imgSrc;
        json.width = this.width;
        json.height = this.height;
        json.movable = this.movable;
        json.resizable = this.resizable;
        json.editAble = this.editAble;
        json.data = this.data;
        return json;
    }

    /**
     * 从json对象中恢复图片
     * @param jsonProject json图像数据内容
     * @param isShow 是否可编辑
     */
    this.fromJson = function (jsonProject) {
        // 重新初始化
        this.width = jsonProject.width;
        this.height = jsonProject.height;
        this.imgSrc = jsonProject.imgSrc;
        this.imgBaseUrl = jsonProject.imgBaseUrl;
        this.movable = jsonProject.movable;
        this.resizable = jsonProject.resizable;
        this.editAble = jsonProject.editAble;
        this.data = jsonProject.data;
        // 初始化
        this.init();
        var entityArr = jsonProject.entities;
        for (var i in entityArr) {
            var tempEntity = entityArr[i];
            var entity = new Entity(this, tempEntity);
            entity.setData(tempEntity.data);
            this.entities.put(entity.id, entity);
        }
    }

    this.selectEntry = function (id) {
        this.unSelectAll();
        var sltEntity = this.entities.get(id);
        sltEntity.markSelect();
    }

    /**
     * 双击区域
     * @param id 点击项目id
     */
    this.entityDbclick = null;

    /**
     * 扩展函数
     * @param id 点击项目id
     */
    this.entityClick = null;

    /**
     * 名称修改
     * @param id 点击项目id
     */
    this.entityTextChange = null;

    /**
     * 鼠标右键点击事件
     * @type {null}
     */
    this.entityMouseDown = null;

    /**
     * 增加区域
     * @param id 区域编号
     * @param text 区域名称
     */
    this.addEntity = function (entity) {
        this.entities.put(entity.id, entity);
    }

    this.onContainerClick = null;

    /**
     * 取消所有对象的选择
     * @param excludId 排除不删除元素
     */
    this.unSelectAll = function (excludId) {
        var regionsKeys = this.sltEntities.keys();
        for (var regionId in regionsKeys) {
            if (excludId != regionsKeys[regionId]) {
                this.unSelect(regionsKeys[regionId]);
            }
        }
    }

    /**
     * 增加选中
     * @param id 区域编号
     */
    this.addSelected = function (id) {
        this.sltEntities.put(id, this.entities.get(id));
        // 自定义了点击事件，触发点击事件
        if (this.entityClick) {
            this.entityClick(id);
        }
    }

    /**
     * 取消区域选中
     * @param entityId 区域编号
     */
    this.unSelect = function (entityId) {
        var entity = this.sltEntities.get(entityId);
        entity.unSelectEntry();
        this.sltEntities.remove(entityId);
    }

    /**
     * 删除选中元素
     */
    this.removeSelected = function () {
        // 删除选中的元素
        var keys = this.sltEntities.keys();

        for (var i = 0; i < keys.length; i++) {
            var sltId = keys[i];
            this.removeEntity(sltId);
        }
    }

    /**
     * 删除指定节点
     * @param entityId
     */
    this.removeEntity = function (entityId) {
        var entity = this.entities.get(entityId);
        if (entity) {
            entity.remove();
            this.sltEntities.remove(entityId);
            this.entities.remove(entityId);
        }
    }

    /**
     * 修改容器属性
     * @param jsonData 属性内容
     */
    this.changeAttr = function (jsonData) {
        if (jsonData.imgSrc) {
            this.imgSrc = jsonData.imgSrc;
            if (jsonData.imgBaseUrl) {
                this.imgBaseUrl = jsonData.imgBaseUrl;
            } else {
                this.imgBaseUrl = "oframe/common/file/file-download.gv?docId=";
            }
            if (this.img) {
                this.img.remove();
            }
            // 重新设置图片
            this.img = this._paper.image(getGlobalPathRoot() + this.imgBaseUrl + this.imgSrc, 0, 0,
                parseInt(this._paper.width), parseInt(this._paper.height)).toBack();
            this.width = this._paper.width;
            this.height = this._paper.height;
        }
    }
}

/*
 * 绘制一个房间
 * params:画布,外部矩形横坐标,外部矩形纵坐标,外部矩形的宽,外部矩形的高,边线像素，文本内容, 输入颜色, 输入透明度
 */
function Entity(building, jsonData) {
    // 数据类型转换
    var startX = parseInt(jsonData.x);
    var startY = parseInt(jsonData.y);
    var outW = parseInt(jsonData.width);
    var outH = parseInt(jsonData.height);
    // 获取输入参数
    var outBorderSize = jsonData.borderSize ? jsonData.borderSize : 1;
    var text = jsonData.text;
    var inColor = jsonData.color;
    var inFillOpacity = jsonData.fillOpacity;
    var data = jsonData.data ? jsonData.data : {};
    var realStroke = jsonData.stroke ? jsonData.stroke : "black";
    var fontColor = jsonData.fontColor ? jsonData.fontColor : "black";
    // 原始颜色
    var realColor = inColor ? inColor : "white";
    var sltColor = "red";
    // 透明度
    var fillOpacity = inFillOpacity ? inFillOpacity : "0.5";
    // 处理输入参数
    outW = outW < minW ? minW : outW;
    outH = outH < minH ? minH : outH;
    // 标记编辑中，编辑中不允许移动
    var inEdit = false;
    // 标记选中
    var isSelected = false;
    // 临时path使用对象
    var tempPath = null;
    var tempPathStr = jsonData.path;

    // 设置标号
    this.id = null;

    /**
     * 转换为Json对象
     * @returns {{}}
     */
    this.toJson = function () {
        var json = {};
        json.id = this.id;
        if (outElement.type == "rect") {
            json.x = outElement.attr("x");
            json.y = outElement.attr("y");
            json.width = outElement.attr("width");
            json.height = outElement.attr("height");
        } else if (outElement.type == "path") {
            json.path = outElement.attr("path").toString();
            var pathBox = Raphael.pathBBox(json.path);
            json.x = pathBox.x;
            json.y = pathBox.y;
            json.width = pathBox.width;
            json.height = pathBox.height;
        }
        json.text = outElement.data("inText").attr("text");
        json.color = realColor;
        json.fillOpacity = fillOpacity;
        json.sltColor = sltColor;
        json.data = data;
        json.stroke = realStroke;
        json.fontColor = fontColor;
        var inText = outElement.data("inText");
        if (inText) {
            json.textX = inText.attr("x");
            json.textY = inText.attr("y");
        }
        return json;
    }

    // 建筑对象
    var _building = building;
    // 获取建筑的画布
    var _paper = building._paper;
    // 定义内部矩形区域的宽度和高度
    var inW = 10, inH = 10;
    // 最小宽度和高度
    var minW = 60, minH = 10;

    this.changeEntity = function (jsonData) {
        if (jsonData.text) {
            var _inText = outElement.data("inText");
            // 调整图片的位置
            if (outElement.type == "rect") {
                var centX = outElement.attr("x") + outElement.attr("width") / 2;
                _inText.attr("text", jsonData.text);
                _inText.attr("x", centX - _inText.getBBox().width / 2);
            } else {
                _inText.attr("text", jsonData.text);
            }
        }
        if (jsonData.color || jsonData.fillOpacity) {
            realColor = jsonData.color ? jsonData.color : realColor;
            fillOpacity = jsonData.fillOpacity ? jsonData.fillOpacity : fillOpacity;
            changeColor(realColor, fillOpacity);
        }
        if (jsonData.stroke) {
            realStroke = jsonData.stroke;
            if (!isSelected) {
                outElement.attr("stroke", realStroke);
            }
        }
        if (jsonData.fontColor) {
            fontColor = jsonData.fontColor;
            outElement.data("inText").attr("stroke", jsonData.fontColor);
        }
    }

    this.getText = function () {
        return outElement.data("inText").attr("text");
    }
    this.moveEntityFront = function () {
        moveEntityFront();
    }
    this.moveEntityBack = function () {
        moveEntityBack();
    }

    /**
     * 设置内部数据
     * @param jsonData 数据
     */
    this.setData = function (jsonData) {
        data = jsonData;
    }

    /**
     * 获取业务数据
     * @returns {{}}
     */
    this.getData = function () {
        return data;
    }

    var containerMouseDownFunction = function (event) {
        var $this = $(this);
        var x = event.pageX - $this.offset().left;
        var y = event.pageY - $this.offset().top;

        // 对缩放进行还原
        x = x * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep())) + _building.panZoom.getCurrentPosition().x;
        y = y * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep())) + _building.panZoom.getCurrentPosition().y;

        if (event.button == 0) {
            if (tempPath == null) {
                tempPathStr = "M" + x + " " + y;
                tempPath = _paper.path(tempPathStr);
            } else {
                tempPathStr += "L" + x + " " + y;
                tempPath.attr("path", tempPathStr);
            }
        } else if (event.button == 2 && tempPath != null) {
            var pathBox = Raphael.pathBBox(tempPathStr + "Z");
            if (pathBox.width < 10 && pathBox.height < 10) {
                // 整个不够大时候不进行闭合
            } else {
                tempPathStr += "Z";
                tempPath.attr("path", tempPathStr);
                initOutElement(tempPath);
                _building.container.unbind("mousedown", containerMouseDownFunction);
                _building.container.unbind("mousemove", containerMouseOverFunction);
                tempPath = null;
                tempPathStr = "";
            }
        }
        stopEvent(event);
    }
    var containerMouseOverFunction = function (event) {
        var $this = $(this);
        var x = event.pageX - $this.offset().left;
        var y = event.pageY - $this.offset().top;

        // 对缩放进行还原
        x = x * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep())) + _building.panZoom.getCurrentPosition().x;
        y = y * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep())) + _building.panZoom.getCurrentPosition().y;

        if (tempPath) {
            var tempTempPath = tempPathStr + "L" + x + " " + y;
            tempPath.attr("path", tempTempPath);
        }
        stopEvent(event);
    };

    /**
     * 初始化区域
     * @param newElement
     */
    var initOutElement = function (newElement, jsonOption) {
        if (!jsonOption) {
            jsonOption = {};
        }
        var oldId = null;
        if (outElement) {
            oldId = outElement.id;
            if (outElement.data("inText")) {
                outElement.data("inText").remove();
            }
            if (outElement.data("inRect")) {
                outElement.data("inRect").remove();
            }
            outElement.remove();
        }
        outElement = newElement;

        // 修改ID同时需要修改id关联的数据
        if (oldId) {
            outElement.id = oldId;
        }
        outElement.data("inText", null);
        outElement.data("inRect", null);
        // 填充
        outElement.attr({
            'fill': realColor, 'stroke': realStroke,
            'stroke-width': outBorderSize, "fill-opacity": fillOpacity
        });
        // 是否支持移动
        if (_building.movable) {
            outElement.drag(outMove, outStartMove, outEndMove);
        }
        // 创建内部文本框
        var inText = null;
        if (!jsonOption.textX) {
            var centX = 0;
            var centY = 0;
            if (outElement.type == "rect") {
                centX = outElement.attr("x") + outElement.attr("width") / 2;
                centY = outElement.attr("y") + outElement.attr("height") / 2;
            } else if (outElement.type == "path") {
                // 修改文本的位置
                var pathBox = Raphael.pathBBox(outElement.attr("path").toString());
                centX = pathBox.x + pathBox.width / 2;
                centY = pathBox.y + pathBox.height / 2;
            }
            //
            inText = _paper.text(centX, centY, text);
            inText.attr("x", inText.attr("x") - inText.getBBox().width / 2);
        } else {
            inText = _paper.text(jsonOption.textX, jsonOption.textY, text);
        }
        // 字体颜色
        inText.data("outContainer", outElement);
        // 根据浏览器类型调整显示字体大小，IE会自动缩小字体
        var fontSize = "12";
        if (isIE()) {
            fontSize = "20";
        }
        inText.attr({
            'text-anchor': 'start',
            "font-family": "Arial, Helvetica, sans-serif",
            "font-size": fontSize, "stroke": fontColor
        });
        // 是否支持移动
        if (_building.movable) {
            inText.drag(textMove, textStartMove, textEndMove);
        }
        // 编辑状态
        if (_building.editAble) {
            _paper.inlineTextEditing(inText);
            inText.click(startEdit);
        } else {
            inText.dblclick(dbclick);
            inText.click(selectEntry);
            inText.mousedown(function (handle) {
                if (_building.entityMouseDown) {
                    var outContainer = this.data("outContainer");
                    return _building.entityMouseDown(handle, outContainer.id);
                }
                event.preventDefault();
            });
        }
        outElement.data("inText", inText);
        // 创建内部矩形
        if (outElement.type == "rect") {
            var inRect = _paper.rect(startX + outW - inW,
                startY + outH - inH,
                inW - outBorderSize,
                inH - outBorderSize);
            inRect.attr({"fill": realColor, "stroke": "#fff", 'stroke-width': '0', "fill-opacity": 0});
            if (_building.resizable) {
                inRect.drag(inMove, inStartMove, inEndMove);
            }
            inRect.click(selectEntry);
            inRect.hover(function () {
                if (_building.resizable) {
                    this.attr('cursor', 'se-resize');
                }
            }, function () {
            });
            outElement.data("inRect", inRect);
        }
        // 自定义双击事件
        outElement.dblclick(dbclick);
        outElement.click(selectEntry);
        outElement.mousedown(function (handle) {
            if (_building.entityMouseDown) {
                _building.entityMouseDown(handle, this.id);
            }
        });
        // 改变鼠标的状态
        outElement.hover(function () {
            if (_building.movable) {
                this.attr('cursor', 'move');
            } else {
                this.attr('cursor', 'hand');
            }
        }, function () {
        });
    }

    /**
     * 开始自定义形状
     */
    this.startDefSharp = function () {
        _building.container.bind("mousedown", containerMouseDownFunction);
        _building.container.bind("mousemove", containerMouseOverFunction);
    }

    // 创建外部矩形
    var outElement = null;

    // 初始化外部区域
    if (tempPathStr) {
        initOutElement(_paper.path(tempPathStr), {textX: jsonData.textX, textY: jsonData.textY});
    } else {
        initOutElement(_paper.rect(startX, startY, outW, outH), {textX: jsonData.textX, textY: jsonData.textY});
    }
    // 新绘制的放在最前面
    moveEntityFront();

    this.id = outElement.id;
    // 保存到容器中
    _building.addEntity(this);
    this.getEntityDom = function () {
        return outElement;
    }

    function dbclick(event) {
        if (_building.entityDbclick) {
            _building.entityDbclick(outElement.id);
        } else if (_building.editAble) {
            startEdit(event);
        }
    }

    function moveEntityBack() {
        // 控制内容顺序
        if (outElement.data("inRect")) {
            outElement.data("inRect").toBack();
        }
        if (outElement.data("inText")) {
            outElement.data("inText").toBack();
        }
        outElement.toBack();
        if (_building.img) {
            _building.img.toBack();
        }
    }

    function moveEntityFront() {
        // 控制内容顺序
        outElement.toFront();
        if (outElement.data("inRect")) {
            outElement.data("inRect").toFront();
        }
        if (outElement.data("inText")) {
            outElement.data("inText").toFront();
        }
    }

    this.unSelectEntry = function () {
        if (!isSelected) {
            return;
        } else {
            //changeColor(realColor, fillOpacity);
            outElement.attr({'stroke': realStroke, 'stroke-width': outBorderSize});
            isSelected = false;
        }
    }


    this.markSelect = function () {
        if (isSelected) {
            return;
        } else {
            moveEntityFront();
            outElement.attr({'stroke': 'red', 'stroke-width': "3"});
            isSelected = true;
            _building.addSelected(outElement.id);
        }
    }

    function selectEntry(event) {
        if (inEdit) {
            var input = inText.inlineTextEditing.input;
            input.blur();
        }
        if (isSelected) {
            if (event.ctrlKey) {
                _building.unSelect(outElement.id);
            } else if (_building.sltEntities.keys().length > 1) {
                // 取消所有的选择
                _building.unSelectAll(outElement.id);
            }
            return;
        } else {
            if (!event.ctrlKey) {
                _building.unSelectAll();
            }
//            _building.container.focus();
            outElement.attr({'stroke': 'red', 'stroke-width': "3"});
            //
            _building.addSelected(outElement.id);
            //
            isSelected = true;
        }
        stopEvent(event);
    }

    /**
     * 改变颜色,同时修改内部和外部对象的颜色
     * @param newColor 调整后的颜色
     * @param fillOpacity 透明度
     */
    function changeColor(newColor, newFillOpacity) {
        if (!newFillOpacity) {
            newFillOpacity = fillOpacity;
        }
        // 内部矩形区域
        if (outElement.data("inRect")) {
            outElement.data("inRect").attr({'fill': newColor});
        }
        // 外部矩形区域
        outElement.attr({'fill': newColor, "fill-opacity": newFillOpacity});
    }

    /**
     * 删除元素
     */
    this.remove = function () {
        if (outElement.data("inText")) {
            outElement.data("inText").remove();
        }
        if (outElement.data("inRect")) {
            outElement.data("inRect").remove();
        }
        outElement.remove();
    }

    /**
     * 事件触发编辑事件
     */
    function startEdit(event) {
        if (_building.editAble) {
            var input = inText.inlineTextEditing.startEditing();
            inEdit = true;
            input.addEventListener("blur", function (e) {
                inText.inlineTextEditing.stopEditing();
                inEdit = false;
                _building.container.focus();
                if (_building.entityTextChange) {
                    _building.entityTextChange(outElement.id, this.value);
                }
            }, true);
            input.addEventListener("keydown", function (e) {
                if (e.keyCode == 13) {
                    input.blur();
                }
            })
        }
    }

    /*
     * 定义拖动外部矩形的开始移动，移动和结束移动事件
     * 注：dx：相对起始点的x位移,dy:相对起始点的y位移,x:鼠标的 x轴位置,y:鼠标的 y轴位置,event:DOM事件对象,e.clientX:当前鼠标x坐标,e.clientY：当前鼠标Y坐标
     */
    function outStartMove(dx, dy) {
        // 事件不再传播
        stopEvent(event);
        // 编辑中不允许移动
        if (inEdit) {
            return;
        }
        // 为元素(外部矩形outRect)自定义属性并赋值
        this.tempx = 0;
        this.tempy = 0;
        if (this.type == "rect") {
            this.tempx = parseInt(this.attr("x"));
            this.tempy = parseInt(this.attr("y"));
        }
        var inText = this.data("inText");
        this.inTextX = parseInt(inText.attr("x"));
        this.inTextY = parseInt(inText.attr("y"));
    }

    /*
     * dx：相对起始点的x位移,dy:相对起始点的y位移,x:鼠标的 x轴位置,y:鼠标的 y轴位置
     */
    function outMove(dx, dy, x, y) {
        // 对缩放进行还原
        dx = dx * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep()));
        dy = dy * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep()));
        // 事件不再传播
        stopEvent(event);
        // 编辑中不允许移动
        if (inEdit) {
            return;
        }
        if (this.type == "rect") {
            // 拖动外部矩形时改变外部矩形的位置
            var attr = {'x': this.tempx + dx, 'y': this.tempy + dy, 'cursor': 'move'};
            this.attr(attr);
            // 获取存放的数据对象
            var temp_rect = this.data("inRect");

            var temp_rect_attr = {
                x: this.tempx + dx + parseInt(this.attr("width")) - inW,
                y: this.tempy + dy + parseInt(this.attr("height")) - inH
            };
            temp_rect.attr(temp_rect_attr);
            // 改变内部文本框的位置
            var temp_text = this.data("inText");
            var temp_text_attr = {
                x: this.inTextX + dx,
                y: this.inTextY + dy
            };
            temp_text.attr(temp_text_attr);
        } else if (this.type == "path") {
            // path信息调整
            var trans_x = dx - this.tempx;
            var trans_y = dy - this.tempy;

            var newPath = Raphael.transformPath(this.attr('path'), "...t" + trans_x + "," + trans_y);
            this.attr("path", newPath);
            this.tempx = dx;
            this.tempy = dy;
            // 修改文本的位置
            var temp_text = this.data("inText");
            var temp_text_attr = {
                x: this.inTextX + dx,
                y: this.inTextY + dy
            };
            temp_text.attr(temp_text_attr);
        }
    }

    function outEndMove() {
        // 编辑中不允许移动
        if (inEdit) {
            return;
        }
        this.animate({}, 300);
    }

    //对内部矩形的操作
    var beforeMoveX_in = 0; // 拖动内部矩形之前内部矩形的X坐标点
    var beforeMoveY_in = 0; // 拖动内部矩形之前内部矩形的Y坐标点
    var beforeMoveW_out = 0;// 拖动内部矩形之前外部矩形的宽度
    var beforeMoveH_out = 0;// 拖动内部矩形之前外部矩形的高度

    /*
     * 定义拖动内部矩形的开始移动，移动和结束移动事件
     */
    function inStartMove() {
        // 事件不再传播
        stopEvent(event);
        // 编辑中不允许移动
        if (inEdit) {
            return;
        }
        //为当前元素(矩形inRect)自定义属性并赋值
        this.mx = this.attr("x");
        this.my = this.attr("y");

        //获取内部矩形拖动之前的x坐标并赋值给变量beforeMoveX_in
        beforeMoveX_in = this.attr("x");
        //获取内部矩形拖动之前的y坐标并赋值给变量beforeMoveY_in
        beforeMoveY_in = this.attr("y");

        // 获取内部矩形拖动之前外部矩形的宽度
        beforeMoveW_out = parseInt(outElement.attr("width"));
        //获取内部矩形拖动之前外部矩形的高度
        beforeMoveH_out = parseInt(outElement.attr("height"));
        //
        var inText = outElement.data("inText");
        this.inTextX = parseInt(inText.attr("x"));
        this.inTextY = parseInt(inText.attr("y"));
    }

    function inMove(dx, dy, x, y) {
        // 对缩放进行还原
        dx = dx * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep()));
        dy = dy * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep()));
        // 事件不再传播
        stopEvent(event);
        // 编辑中不允许移动
        if (inEdit) {
            return;
        }
        //当拖动内部矩形时，改变外部矩形的宽高
        var tempW = beforeMoveW_out + dx;
        var tempH = beforeMoveH_out + dy;
        if (tempW <= minW && tempH <= minH) {
            return;
        }
        if (tempW < minW) {
            tempW = minW;
        }
        if (tempH < minH) {
            tempH = minH;
        }
        outElement.attr({
            'width': tempW,//拖动后的宽度 = 拖动前的宽度 + x轴的位移
            'height': tempH//拖动后的高度 = 拖动前的高度 + y轴的位移
        });
        // 调整内部文件框的位置
        var temp_text_attr = {
            x: this.inTextX + dx / 2,
            y: this.inTextY + dy / 2
        };
        outElement.data("inText").attr(temp_text_attr);
        // 计算内部矩形位置
        var innerX = outElement.attr("x") + tempW - inW;
        var innerY = outElement.attr("y") + tempH - inH;
        // 对内部矩形重新定位
        this.attr('x', innerX);
        this.attr('y', innerY);
    }

    function inEndMove(e) {
        // 事件不再传播
        stopEvent(event);
        // 编辑中不允许移动
        if (inEdit) {
            return;
        }
    }

    /*
     * 文本开始拖动事件
     */
    function textStartMove() {
        // 事件不再传播
        stopEvent(event);
        this.mx = this.attr("x");
        this.my = this.attr("y");
    }

    /**
     * 文本拖动时间
     * @param dx
     * @param dy
     * @param x
     * @param y
     */
    function textMove(dx, dy, x, y) {
        // 对缩放进行还原
        dx = dx * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep()));
        dy = dy * (1 - (_building.panZoom.getCurrentZoom() * _building.panZoom.getZoomStep()));
        // 事件不再传播
        stopEvent(event);
        var newX = this.mx + dx;
        var newY = this.my + dy;
        // 对内部矩形重新定位
        this.attr('x', newX);
        this.attr('y', newY);
    }

    function textEndMove(e) {
        // 事件不再传播
        stopEvent(event);
    }
}