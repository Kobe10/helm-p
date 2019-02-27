<%--院落信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <input type="hidden" name="buildId" value="${oldHsBean.HouseInfo.buildId}">

    <h1>房屋坐落<span class="panel_menu js_reload">刷新</span></h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="background-color: #ffffff; min-height: 300px;">
        <%--展示地图图形--%>
        <div class="border" style="position: relative; overflow: hidden;">
            <div id="ph00301_hs_view" style="width: 100%;height: 295px;"></div>
            <div class="mapPosition" title="大图" onclick="ph00301.openHsInMap();"></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var hsMapView;
    var ph00301 = {
        //在预览图上打开build地图
        openHsInMap: function () {
            var url = "eland/ph/ph005/ph005-showAllMap.gv?lon=" + hsMapView.getCenter().lon
                    + "&lat=" + hsMapView.getCenter().lat + "&level=3" + "&hsId=${hsId}";
            index.quickOpen(url, {opCode: "ph005-showAllMap", opName: "项目地图", fresh: true});
        },

        //初始化build地图
        initHsMapView: function () {
            //可配置参数从参数配置里查询
            var baselayerUrl = '${baselayerUrl}';
            var layerName1 = '${layerName1}';
            var layerName2 = '${layerName2}';
            //院落边界
            var buildBorderStr = '${buildPoints}';
            //判断房产坐标修改第几次。
            var hsPointsStr = '${oldHsBean.HouseInfo.hsRoomPos}';
            //判断自建房坐标修改第几次。
            var hsSlfPointsStr = '${oldHsBean.HouseInfo.hsSlfRoomPos}';

            //赋予 地图中心，缩放等级默认值。如果有数据下面会重新计算 定位中心
            var lon, lat, level;
            ${lonLatLevel}  //配置参数里的地图中心
            level = 3;

            hsMapView = new OpenLayers.Map({
                div: "ph00301_hs_view",
                //增加控制按钮
                controls: [
                    new OpenLayers.Control.Zoom({
                        position: new OpenLayers.Pixel(3, 5)
                    }),
                    new OpenLayers.Control.Navigation({zoomWheelEnabled: false})
                ],
                maxExtent: new OpenLayers.Bounds(${mapBounds}),     //边界:left, bottom, right, top
                maxResolution: "auto",                                      //最大分辨率（maxResolution）
                projection: "EPSG:3785",
                units: "degrees"
            });

            var layer_1 = new OpenLayers.Layer.WMS(
                    "基础区域", baselayerUrl,
                    {"layers": layerName1},
                    {
                        isBaseLayer: true,
                        opacity: .4
                    }
            );

            //设置正式住房 自定义样式  定义样式
            var myHsStyles = new OpenLayers.StyleMap({
                "default": new OpenLayers.Style({
                    pointRadius: 3,
                    fillColor: "#41D930",
                    fillOpacity: .7,
                    strokeColor: "#41D930",
                    strokeOpacity: .7,
                    strokeWidth: 2
                })
            });
            //设置 自建房 自定义样式  定义样式
            var myHsSlfStyles = new OpenLayers.StyleMap({
                "default": new OpenLayers.Style({
                    pointRadius: 3,
                    fillColor: "#2FD7DA",
                    fillOpacity: .7,
                    strokeColor: "#2FD7DA",
                    strokeOpacity: .7,
                    strokeWidth: 2
                })
            });

            // 1、新建矢量图层
            var ph00301_buildBorder_layer = new OpenLayers.Layer.Vector("建筑边界");
            var ph00301_hs_layer = new OpenLayers.Layer.Vector("房产详情", {
                styleMap: myHsStyles,
                rendererOptions: {zIndexing: true}
            });
            var ph00301_hsSlf_layer = new OpenLayers.Layer.Vector("自建房详情", {
                styleMap: myHsSlfStyles,
                rendererOptions: {zIndexing: true}
            });

            //2、获取坐标  判空处理  绘制院落边界 并计算中心坐标
            if (hsPointsStr != undefined && hsPointsStr != '') {
                var lineNum = hsPointsStr.split("|");   //切分每条线
                var arrtemp = [];                   //点 数组

                //设置两个数组存放X轴，Y轴坐标，取中心点
                var Xarr = [];
                var Yarr = [];
                //遍历每条线
                for (var i = 0; i < lineNum.length; i++) {
                    arrtemp = lineNum[i].split(',');        //切分每条线上的点
                    var pointList = [];
                    //3、将坐标字符串分解为坐标数组
                    for (var j = 0; j < arrtemp.length; j++) {
                        //获取所有点x轴 y轴坐标取中心点  (Xmax + Xmin)/2 , (Ymax + Xmin)/2
                        var arr = arrtemp[j].replace('POINT(', '').replace(')', '').split(' ');
                        Xarr.push(arr[0]);
                        Yarr.push(arr[1]);
                        pointList.push(new OpenLayers.Geometry.Point(arr[0], arr[1]));
                    }
                    var linestring = new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(pointList)); //坐标数组
                    var lineFeature = new OpenLayers.Feature.Vector(linestring, null, {
                        //#buildBorderColor 院落边界颜色
                        fillColor: "red",
                        fillOpacity: 0.1,
                        strokeColor: "red",
                        strokeOpacity: 0.5,
                        strokeWidth: 2
                    });

                    //5、生成线图层
                    ph00301_buildBorder_layer.addFeatures([lineFeature]);
                }
                //设置地图展示中心
                var xCenter = (Math.max.apply(null, Xarr) + Math.min.apply(null, Xarr)) / 2;
                var yCenter = (Math.max.apply(null, Yarr) + Math.min.apply(null, Yarr)) / 2;
                lon = xCenter;
                lat = yCenter;
            }

            //3. 获取房产坐标  绘制已有的房产地图
            if (hsPointsStr != undefined && hsPointsStr != '') {
                var hsNum = hsPointsStr.split("|");
                var hsArr = [];
                for (var iHs = 0; iHs < hsNum.length; iHs++) {
                    hsArr = hsNum[iHs].split(",");
                    var hsStrList = [];
                    //3、将坐标字符串分解为坐标数组
                    for (var jHs = 0; jHs < hsArr.length; jHs++) {
                        var hsArrs = hsArr[jHs].replace('POINT(', '').replace(')', '').split(' ');
                        hsStrList.push(new OpenLayers.Geometry.Point(hsArrs[0], hsArrs[1]));
                    }
                    var hsLineRel = new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(hsStrList)); //坐标数组
                    var hsLineFeature = new OpenLayers.Feature.Vector(hsLineRel, null, {
                        //#initColor 默认图形颜色
                        fillColor: "#41D930",
                        fillOpacity: .5,
                        strokeColor: "#41D930",
                        strokeOpacity: .5,
                        strokeWidth: 2
                    });
                    //5、生成线图层
                    ph00301_hs_layer.addFeatures([hsLineFeature]);
                }
            }

            //3. 获取自建房 坐标  绘制已有的 自建房地图
            if (hsSlfPointsStr != undefined && hsSlfPointsStr != '') {
                var hsSlfNum = hsSlfPointsStr.split("|");
                var hsSlfArr = [];
                for (var iHsSlf = 0; iHsSlf < hsSlfNum.length; iHsSlf++) {
                    hsSlfArr = hsSlfNum[iHsSlf].split(",");
                    var hsSlfStrList = [];
                    //3、将坐标字符串分解为坐标数组
                    for (var jHsSlf = 0; jHsSlf < hsSlfArr.length; jHsSlf++) {
                        var hsSlfArrs = hsSlfArr[jHsSlf].replace('POINT(', '').replace(')', '').split(' ');
                        hsSlfStrList.push(new OpenLayers.Geometry.Point(hsSlfArrs[0], hsSlfArrs[1]));
                    }
                    var hsSlfLineRel = new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(hsSlfStrList)); //坐标数组
                    var hsSlfLineFeature = new OpenLayers.Feature.Vector(hsSlfLineRel, null, {
                        //#initColor 默认图形颜色
                        fillColor: "#2FD7DA",
                        fillOpacity: .5,
                        strokeColor: "#2FD7DA",
                        strokeOpacity: .5,
                        strokeWidth: 2
                    });
                    //5、生成线图层
                    ph00301_hsSlf_layer.addFeatures([hsSlfLineFeature]);
                }
            }

            // 6、加入所有图层到地图
            hsMapView.addLayers([layer_1, ph00301_buildBorder_layer, ph00301_hs_layer, ph00301_hsSlf_layer]);

            //设置初始化的起始点，缩放级别
            hsMapView.setCenter(new OpenLayers.LonLat(lon, lat), level);
        }
    };

    $(document).ready(function () {
        // 初始化房产信息
        ph00301.initHsMapView();
    });
</script>