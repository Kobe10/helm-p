<%--测试地图展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<script type="text/javascript">
    $(document).ready(initView());

    var map, hrjc_1, hrjc_2, drawControls, pointLayer, linelayer, polygonLayer, boxLayer, strtemp, point2layer;


    function initView() {
//            边界设定
        var bounds = new OpenLayers.Bounds(
                799.9998301217565, 299.9997965772656,
                2049.9998301217565, 1100.0000401611535
        );

        map = new OpenLayers.Map('map2', {
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
            maxResolution: 4.8828125,
            projection: "EPSG:3785",
            units: 'm'
        });

        //4、自定义线的风格
        var style_line = {
            strokeColor: "red",
            strokeOpacity: 0.8,
            strokeWidth: 2,
            pointRadius: 20,
            label: "",
            fontSize: '12px',
            fontFamily: '宋体',
            labelXOffset: 30,
            labelYOffset: 10,
            labelAlign: 'rm'
        };


        // setup tiled layer
        hrjc_2 = new OpenLayers.Layer.WMS(
                "hrjc:hrjc_2_dwg_Polyline", "http://192.168.1.200:8080/geoserver/hrjc/wms",
                {
                    "LAYERS": 'hrjc:hrjc_2_dwg_Polyline',
                    "STYLES": '',
                    format: 'image/jpeg'
                },
                {
                    singleTile: true,
                    ratio: 1,
                    isBaseLayer: false,
                    yx: {'EPSG:3785': false}
                }
        );

        // setup single tiled layer
        hrjc_1 = new OpenLayers.Layer.WMS(
                "hrjc:hrjc_1_dwg_Polyline", "http://192.168.1.200:8080/geoserver/hrjc/wms",
                {
                    "LAYERS": 'hrjc:hrjc_1_dwg_Polyline',
                    "STYLES": '',
                    format: 'image/jpeg'
                },
                {
                    singleTile: true,
                    ratio: 1,
                    isBaseLayer: true,
                    yx: {'EPSG:3785': true}
                }
        );

        // 1、新建矢量图层
        linelayer = new OpenLayers.Layer.Vector("线图层");

        //2、获取坐标
        strtemp = "${linePoint}";
//        strtemp = "POINT(1442.9651162791 713.66279069768),POINT(1442.9651162791 710.17441860466),POINT(1441.3953488372 709.88372093024),POINT(1441.9186046512 707.44186046512),POINT(1440.0581395349 707.55813953489),POINT(1440.523255814 704.53488372094),POINT(1446.8023255814 705.05813953489),POINT(1446.2790697675 713.83720930233), POINT(1442.6744186047 713.48837209303)";

        var lineNum = strtemp.split("|");   //切分每条线
        var arrtemp = [];                   //点 数组

        //遍历每条线
        for (var i = 0; i < lineNum.length; i++) {
            arrtemp = lineNum[i].split(',');        //切分每条线上的点

            var pointList = new Array();
            //3、将坐标字符串分解为坐标数组
            for (var j = 0; j < arrtemp.length; j++) {
                var arr = convertPoint2LonLat(arrtemp[j]);
                pointList.push(new OpenLayers.Geometry.Point(arr[0], arr[1]));
            }

            var linestring = new OpenLayers.Geometry.LineString(pointList); //坐标数组
            var lineFeature = new OpenLayers.Feature.Vector(linestring, null, style_line);

            //5、生成线图层
            linelayer.addFeatures([lineFeature]);
        }

        // 6、加入图层到地图
        map.addLayers([hrjc_1, hrjc_2, linelayer]);

        //设置初始化的起始点，缩放级别
        map.setCenter(new OpenLayers.LonLat(${centerPoint}), ${level});
        // 设定视图缩放地图程度为最大
//            map.zoomToMaxExtent();

    }

    function convertPoint2LonLat(origStr) {
        var pointStr = origStr.replace('POINT(', '');
        pointStr = pointStr.replace(')', '');
        return  pointStr.split(' ');
    }

</script>
<div>
    <h3>还原点</h3>

    <div id="map2" style="clear: both; position: relative;  width: 100%; height: 400px; border: 1px red solid;"></div>
</div>