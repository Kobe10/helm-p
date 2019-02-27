<%--测试地图展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<script type="text/javascript">
    $(document).ready(init());

    var map, hrjc_1, hrjc_2, drawControls;
    var pointLayer;
    var lineLayer;
    var polygonLayer;
    var boxLayer;
    // pink tile avoidance;
    OpenLayers.IMAGE_RELOAD_ATTEMPTS = 5;
    // make OL compute scale according to WMS spec
    OpenLayers.DOTS_PER_INCH = 25.4 / 0.28;

    function init() {
//            边界设定
        var bounds = new OpenLayers.Bounds(
                800, 300,
                2050, 1100
        );

        map = new OpenLayers.Map('map', {
//                增加控制按钮，导航、缩放
            controls: [
                new OpenLayers.Control.PanZoomBar({
                    position: new OpenLayers.Pixel(2, 15)
                }),
                new OpenLayers.Control.Navigation(),
                new OpenLayers.Control.LayerSwitcher(),
                new OpenLayers.Control.MousePosition()
            ],
            maxExtent: bounds,
            maxResolution: "auto",
            projection: "EPSG:3785",
            units: 'm'
        });

        // setup tiled layer
        hrjc_2 = new OpenLayers.Layer.WMS(
                "华融房屋 2", "http://192.168.1.200:8080/geoserver/hrjc/wms",
                {
                    "LAYERS": 'hrjc:hrjc_2_dwg_Polyline',
                    "STYLES": '',
                    format: 'image/png'
                },
                {
                    singleTile: true,
                    ratio: 1,
                    isBaseLayer: false,
                    yx: {'EPSG:3785': true}
                }
        );

        // setup single tiled layer
        hrjc_1 = new OpenLayers.Layer.WMS(
                "华融基础区域 1", "http://192.168.1.200:8080/geoserver/hrjc/wms",
                {
                    "LAYERS": 'hrjc:hrjc_1_dwg_Polyline',
                    "STYLES": '',
                    format: 'image/png'
                },
                {
                    singleTile: true,
                    ratio: 1,
                    isBaseLayer: true,
                    yx: {'EPSG:3785': true}
                }
        );

        pointLayer = new OpenLayers.Layer.Vector("点图层");
        lineLayer = new OpenLayers.Layer.Vector("线图层");
        polygonLayer = new OpenLayers.Layer.Vector("多边形");
        boxLayer = new OpenLayers.Layer.Vector("box");

//        将图层加入地图map
        map.addLayers([hrjc_1, hrjc_2, pointLayer, lineLayer, polygonLayer, boxLayer]);

        drawControls = {
            point: new OpenLayers.Control.DrawFeature(pointLayer,
                    OpenLayers.Handler.Point),
            line: new OpenLayers.Control.DrawFeature(lineLayer,
                    OpenLayers.Handler.Path),
            polygon: new OpenLayers.Control.DrawFeature(polygonLayer,
                    OpenLayers.Handler.Polygon),
            box: new OpenLayers.Control.DrawFeature(boxLayer,
                    OpenLayers.Handler.RegularPolygon, {
                        handlerOptions: {
                            sides: 4,
                            irregular: true
                        }
                    }
            )
        };

        for (var key in drawControls) {
            map.addControl(drawControls[key]);
        }

        map.setCenter(new OpenLayers.LonLat(${centerPoint}), ${level});
//        map.zoomLevels = 5;
        // 设定视图缩放地图程度为最大
//        map.zoomToMaxExtent();
    }


    function toggleControl(element) {
        for (key in drawControls) {
            var control = drawControls[key];
            if (element.value == key && element.checked) {
                control.activate();
            } else {
                control.deactivate();
            }
        }
    }

    function alertDrawPoint(element) {
//        var features = lineLayer.features;
        var features = polygonLayer.features;
        alert(polygonLayer.features);
        var temp;
        alert("所画图形有：" + features.length + " 个");

        for (var i = 0; i < features.length; i++) {
            var geom = features[i].geometry;
//            var lonlats = geom.getVertices();

            temp = geom.getVertices().toString() + "|" + temp;
            alert("这就是你画的东东：" + geom.getVertices());
        }
        if (temp != undefined) {
            temp = temp.substring(0, temp.length - 10);
        }
        $("#showPoint").html(temp);

//        异步请求保存点坐标
//        var url = getGlobalPathRoot() + "eland/mb/mb006/mb006-init.gv";
//        var ajaxPacket = new AJAXPacket(url);
//        ajaxPacket.data.add("strPoint", temp);
//        ajaxPacket.data.add("centerPoint", map.getCenter.getCenterLonLat());
//        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
//            var jsonData = eval("(" + response + ")");
//            // 服务调用是否成功
//            var isSuccess = jsonData.success;
//            if (isSuccess) {
//            }
//        });
    }
</script>

<div>
    <h3 id="title">来画个东西</h3>

    <div id="map" style="clear: both; position: relative; width: 100%; height: 400px; border: 1px red solid;"></div>

    <ul id="controlToggle">
        <li style="line-height: 15px">
            <input type="radio" name="type" value="none" id="noneToggle"
                   onclick="toggleControl(this);" checked="checked"/>
            <label for="noneToggle">navigate</label>
        </li>
        <li style="line-height: 15px">
        <input type="radio" name="type" value="polygon" id="polygonToggle" onclick="toggleControl(this);"/>
        <label for="polygonToggle">draw polygon</label>
        </li>
        <li style="line-height: 15px">
        <input type="radio" name="type" value="box" id="boxToggle" onclick="toggleControl(this);"/>
        <label for="boxToggle">draw box</label>
        </li>
    </ul>

    <p><input type="button" onclick="alertDrawPoint(this);" value="点我"/>查看所画坐标</p>

    <div id="showPoint"></div>
    <br/>
    <br/>
</div>
