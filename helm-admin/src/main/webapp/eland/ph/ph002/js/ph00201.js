// pink tile avoidance;
//OpenLayers.IMAGE_RELOAD_ATTEMPTS = 5;
// make OL compute scale according to WMS spec
//OpenLayers.DOTS_PER_INCH = 25.4 / 0.28;

//可配置参数
var baselayerUrl = $("input[name=baselayerUrl]", navTab.getCurrentPanel()).val();
var layerName1 = $("input[name=layerName1]", navTab.getCurrentPanel()).val();
var layerName2 = $("input[name=layerName2]", navTab.getCurrentPanel()).val();

var polygon_view;   //操作所在图层
var drawControls;   //选择操作
var yardView;

var ph00201 = {
    //在预览图上打开build地图
    openHsInMap: function () {
        var url = "eland/ph/ph005/ph005-showAllMap.gv?lon=" + yardView.getCenter().lon + "&lat=" + yardView.getCenter().lat + "&level=3";
        index.quickOpen(url, {opCode:"ph005-showAllMap",opName:"地图",fresh:true});
    },

    /**
     * 编辑已画图形 新增、删除
     */
    init: function (method) {
        //alert("暂时定位用：画图 ph00201.init() 方法 带有回退，删除，修改。");
        var lon, lat, level;
        var strPoints = $("input[name=buildPosition]", navTab.getCurrentPanel()).val();
        level = 2;  lon = 1017; lat = 900;

        /**************************************加载地图**************************************************************/
        var yardMap = new OpenLayers.Map({
            div: 'yardMap',
            // 增加控制按钮，导航、缩放
            controls: [
                //new OpenLayers.Control.PanZoomBar({position: new OpenLayers.Pixel(3, 5)}),  //设置导航条位于屏幕的定位，相对于地图块左上角。
                new OpenLayers.Control.PanZoom({position: new OpenLayers.Pixel(3, 5)}),
                new OpenLayers.Control.Navigation(),
                //new OpenLayers.Control.LayerSwitcher(),
                new OpenLayers.Control.MousePosition()
                //new OpenLayers.Control.ScaleLine()              //比例尺
                //new OpenLayers.Control.OverviewMap()            //鹰眼图
            ],
            maxExtent: new OpenLayers.Bounds(780,600,1270,1210),     //边界：left, bottom, right, top
            maxResolution: "auto",                                      //最大分辨率（maxResolution）
            numZoomLevels: 7,
            projection: "EPSG:4326"
            //units: 'm'
        });

        /**************************************加载基础图层************************************************************/
        var hrjc_1 = new OpenLayers.Layer.WMS(
            "基础区域",
            baselayerUrl,
            {"layers": layerName1},
            {
                isBaseLayer: true,
                opacity: .3
            }
        );
        var hrjc_2 = new OpenLayers.Layer.WMS(
            "房屋图层",
            baselayerUrl,
            {"layers": layerName2, transparent: true},
            {
                displayInLayerSwitcher: false,
                opacity: .3,            //透明度
                minScale: 250000000,    //最小显示分辨率
                units: "degrees"       //比例尺单位
                //transitionEffect: "resize"    //使图层放大或缩小时产生调整大小的动画效果
            }
        );

        // allow testing of specific renderers via "?renderer=Canvas", etc
        var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
        renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;

        //将之前的线标识出来，以便修改，
        polygon_view = new OpenLayers.Layer.Vector("原院落图形", {renderers: renderer});

        //添加选中事件
        var selectedFeatrue = null;
        polygon_view.events.on({
            featureselected: function (feature) {
                selectedFeatrue = this.selectedFeatures;
            },
            featureunselected: function (feature) {
                //document.getElementById('counter').innerHTML = this.selectedFeatures.length;
            }
        });

        /**************************************还原已经存储的图形。 坐标还原***********************************************/
        if (strPoints != undefined && strPoints != '') {
            //2、获取后台存储坐标
            var lineNum = strPoints.split("|");   //切分每条线
            var arrtemp = [];                   //点 数组
            //遍历每条线
            //设置两个数组存放X轴，Y轴坐标，取中心点
            var Xarr = [];
            var Yarr = [];
            for (var i = 0; i < lineNum.length; i++) {
                arrtemp = lineNum[i].split(',');        //切分每条线上的点

                var pointList = [];
                //3、将坐标字符串分解为坐标数组
                for (var j = 0; j < arrtemp.length; j++) {
                    var arr = arrtemp[j].replace('POINT(', '').replace(')', '').split(' ');
                    Xarr.push(arr[0]);
                    Yarr.push(arr[1]);
                    pointList.push(new OpenLayers.Geometry.Point(arr[0], arr[1]));
                }
                var linestring = new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(pointList)); //坐标数组
                var lineFeature = new OpenLayers.Feature.Vector(linestring, null);

                //5、生成线图层
                polygon_view.addFeatures([lineFeature]);
            }
            //设置地图展示中心
            var xCenter = (Math.max.apply(null, Xarr) + Math.min.apply(null, Xarr)) / 2;
            var yCenter = (Math.max.apply(null, Yarr) + Math.min.apply(null, Yarr)) / 2;
            lon = xCenter;
            lat = yCenter;
        }

        /**************************************将所有图层加入地图yardMap**************************************************************/
        yardMap.addLayers([hrjc_1, hrjc_2, polygon_view]);

        /**************************************通过选择操作模式  设置动作类型**************************************************************/
        drawControls = {
            polygon: new OpenLayers.Control.DrawFeature(
                polygon_view, OpenLayers.Handler.Polygon
            ),
            select: new OpenLayers.Control.SelectFeature(
                polygon_view,
                {
                    onSelect: function () {
                        alertMsg.confirm("确定要删除该图形吗?", {
                            okCall: function () {
                                polygon_view.removeFeatures(selectedFeatrue);
                                return false;
                            }
                        });
                    },
                    clickout: true, toggle: false,
                    multiple: false, hover: false,
                    toggleKey: "ctrlKey", // ctrl key removes from selection
                    multipleKey: "shiftKey" // shift key adds to selection
                    //box: true
                }
            )
        };

        for (var key in drawControls) {
            yardMap.addControl(drawControls[key]);
        }

        //设置地图中心以及缩放等级
        yardMap.setCenter(new OpenLayers.LonLat(lon, lat), level);
    },

    /**
     *  判断所选工作模式
     */
    toggleControl: function (element) {
        for (key in drawControls) {
            var control = drawControls[key];
            if (element.value == key && element.checked) {
                control.activate();
            } else {
                control.deactivate();
            }
        }
    },

    /**
     * 获取所画图形坐标
     */
    alertDrawPoint: function () {
        var tempFeatures = polygon_view.features;
        var temp;
        if (tempFeatures.length > 0) {
            for (var i = 0; i < tempFeatures.length; i++) {
                var geom = tempFeatures[i].geometry;
                temp = geom.getVertices().toString() + "|" + temp;
            }
            if (temp != undefined) {
                temp = temp.substring(0, temp.length - 10);
            }
            if (temp.substring(temp.length - 15) == '|POINT(NaN NaN)') {
                temp = temp.substring(0, temp.length - 15);
            }
        }
        console.log(temp);
    },

    /**
     * 编辑面板内容
     */
    editBuild: function (obj, buildId) {
        //替换整个面板div
        var _this = $(obj);
        var urlData = "eland/ph/ph002/ph002-initYardInfo.gv?method=edit&buildId=" + buildId + "&prjCd=" + getPrjCd();
        index.loadInfoPanelByInnerObj(obj, urlData);
    },

    /**
     * 展示已画院落图形
     */
    viewMap: function () {
        var lon, lat, level;
        var strPoints = $("input[name=buildPosition]", navTab.getCurrentPanel()).val();
        level = 5; lon = 1425; lat = 700;
        yardView = new OpenLayers.Map({
            div: "yardView",
            //增加控制按钮
            controls: [
                //new OpenLayers.Control.PanZoomBar({
                //    position: new OpenLayers.Pixel(3, 5)
                //}),
                new OpenLayers.Control.Navigation(),
                //new OpenLayers.Control.LayerSwitcher(),   //图层选择工具
                new OpenLayers.Control.MousePosition()
            ],
            maxExtent: new OpenLayers.Bounds(780,600,1270,1210),     //边界：left, bottom, right, top
            maxResolution: "auto",                                      //最大分辨率（maxResolution）
            numZoomLevels: 6,
            projection: "EPSG:3785",
            units: 'm'
        });

        var layer_1 = new OpenLayers.Layer.WMS(
            "基础区域",
            baselayerUrl,
            {"layers": layerName1},
            {isBaseLayer: true}
        );
        //var layer_2 = new OpenLayers.Layer.WMS(
        //    "房屋图层",
        //    baselayerUrl,
        //    {"layers": layerName2, transparent: true},
        //    {
        //        displayInLayerSwitcher: false,
        //        opacity: .5,
        //        minScale: 250000000,
        //        units: "degrees",
        //        transitionEffect: "resize"
        //    }
        //);

        // 1、新建矢量图层
        polygon_view = new OpenLayers.Layer.Vector("多边形图层");

        //2、获取坐标  判空处理
        if (strPoints != undefined && strPoints != '') {
            var lineNum = strPoints.split("|");   //切分每条线
            var arrtemp = [];                   //点 数组

            var Xarr = [];
            var Yarr = [];
            //遍历每条线
            for (var i = 0; i < lineNum.length; i++) {
                arrtemp = lineNum[i].split(',');        //切分每条线上的点

                var pointList = [];
                //3、将坐标字符串分解为坐标数组
                for (var j = 0; j < arrtemp.length; j++) {
                    var arr = arrtemp[j].replace('POINT(', '').replace(')', '').split(' ');
                    Xarr.push(arr[0]);
                    Yarr.push(arr[1]);
                    pointList.push(new OpenLayers.Geometry.Point(arr[0], arr[1]));
                }
                var linestring = new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(pointList)); //坐标数组
                var lineFeature = new OpenLayers.Feature.Vector(linestring, null, {
                    fillColor: "red",
                    fillOpacity: 0.2,
                    strokeColor: "red",
                    strokeOpacity: 0.5,
                    strokeWidth: 2
                });

                //5、生成线图层
                polygon_view.addFeatures([lineFeature]);
            }
            //设置地图展示中心
            var xCenter = (Math.max.apply(null, Xarr) + Math.min.apply(null, Xarr)) / 2;
            var yCenter = (Math.max.apply(null, Yarr) + Math.min.apply(null, Yarr)) / 2;
            lon = xCenter;
            lat = yCenter;
        }

        // 6、加入图层到地图
        yardView.addLayers([layer_1, polygon_view]);

        //设置初始化的起始点，缩放级别
        yardView.setCenter(new OpenLayers.LonLat(lon, lat), level);
    },

    /**
     * 保存院落信息
     */
    saveYardInfo: function (obj, method) {

        var _this = $(obj);
        //获取所画点坐标
        var features = polygon_view.features;
        var temp;
        if (features.length > 0) {
            for (var i = 0; i < features.length; i++) {
                var geom = features[i].geometry;
                temp = geom.getVertices().toString() + "|" + temp;
            }
            if (temp != undefined) {
                temp = temp.substring(0, temp.length - 10);
            }
            if (temp.substring(temp.length - 15) == '|POINT(NaN NaN)') {
                temp = temp.substring(0, temp.length - 15);
            }
        }

        var $ph00201frm;

        if (method == 'edit') {
            $ph00201frm = $("#ph00201_edit_frm", navTab.getCurrentPanel());
        } else if (method == 'add') {
            $ph00201frm = $("#ph00201_add_frm", navTab.getCurrentPanel());
        }
        if ($ph00201frm.valid()) {
            var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-saveYardInfo.gv?method=" + method;
            var ajaxPacket = new AJAXPacket(url);

            //序列化表单
            var jsonData = $ph00201frm.serializeArray();
            jsonData.push({
                name: 'buildPoints',
                value: temp
            });
            jsonData.push({
                name: 'prjCd',
                value: getPrjCd()
            });
            ajaxPacket.data.data = jsonData;

            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    if (method == 'edit') {
                        var panelContainer = _this.parents("div.panelContainer");
                        index.loadInfoPanel($(panelContainer).attr("panelKey"), $(panelContainer).attr("url"));
                    } else if (method == 'add') {
                        var buildId = jsonData.buildId;
                        var regId = $("input[name=ttUpRegId]", navTab.getCurrentPanel()).val();
                        var url = "eland/ph/ph002/ph002-init.gv?regId=" + regId + "&buldId=" + buildId;
                        index.quickOpen(url, {opCode: "pb002-init", opName: "院落详情", fresh: true});
                    }
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
};