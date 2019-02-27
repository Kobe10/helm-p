<%--打开 编辑 院落地图页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar" style="display: block; width: 20%;">
    <ul class="toolBar">
        <li><a class="edit" onclick="ph00301_buildmap.closeBuildMap(this);"><span>确定</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>取消</span></a></li>
        <li style="float: left; margin-left: 80px;">
            <input type="radio" name="checkModeltype" value="none" id="noneToggle"
                   onclick="ph00301_buildmap.toggleCtrl(this);" checked="checked"/>
            <label for="noneToggle">导航模式</label>
        </li>
        <li style="float: left; margin-left: 40px;">
            <input type="radio" name="checkModeltype" value="polygon" id="polygonToggle"
                   onclick="ph00301_buildmap.toggleCtrl(this);"/>
            <label for="polygonToggle">标注建筑</label>
        </li>
        <%--<li style="float: left; margin-left: 40px;">--%>
        <%--<input type="radio" name="checkModeltype" value="modify" id="modifyToggle"--%>
        <%--onclick="ph00301_buildmap.toggleCtrl(this);"/>--%>
        <%--<label for="modifyToggle">修改院落</label>--%>
        <%--</li>--%>
        <li style="float: left; margin-left: 40px;">
            <input type="radio" name="checkModeltype" value="select" id="selectToggle"
                   onclick="ph00301_buildmap.toggleCtrl(this);"/>
            <label for="selectToggle">删除建筑</label>
        </li>
        <%--<li style="float: left; margin-left: 40px;">--%>
        <%--<input type="button" onclick="ph00301_buildmap.showBuildPoint();" value="查看图形坐标"/>--%>
        <%--</li>--%>
    </ul>
</div>
<%--院落地图--%>
<div layoutH="40">
    <div id="ph00301_buildMap"
         style="clear: both; width: 99%; height: 99%; border: 1px #000000 solid;position: relative; display: inline-block; overflow: hidden;">
    </div>
</div>
<script type="text/javascript">
    var modelCtrls;
    var ph00301_build_layer;

    var ph00301_buildmap = {
        /** 判断所选工作模式 */
        toggleCtrl: function (element) {
            for (key in modelCtrls) {
                var control = modelCtrls[key];
                if (element.value == key && element.checked) {
                    control.activate();
                } else {
                    control.deactivate();
                }
            }
        },

        /** 初始化地图数据 */
        initBuildMap: function () {
            //可配置参数从参数配置里查询
            var baselayerUrl = '${baselayerUrl}';
            var layerName1 = '${layerName1}';
            var layerName2 = '${layerName2}';
            <%--将主页面的坐标值 赋予弹出框 用于二次修改展示--%>
            var dataArr = EMap.initData();
            var strPoints = dataArr[0];
            var lon, lat, level;
            //调用计算坐标方法，通用计算方法 ph002.js
            ${lonLatLevel}
            level = 2;

            var buildMap = new OpenLayers.Map({
                div: "ph00301_buildMap",
                //增加控制按钮
                controls: [
                    new OpenLayers.Control.PanZoomBar({
                        position: new OpenLayers.Pixel(3, 5)
                    }),
                    new OpenLayers.Control.Navigation(),
                    //new OpenLayers.Control.LayerSwitcher(),   //图层选择工具
                    new OpenLayers.Control.MousePosition()
                ],
                maxExtent: new OpenLayers.Bounds(${mapBounds}),     //边界：left, bottom, right, top
                maxResolution: "auto",                                      //最大分辨率（maxResolution）
//                numZoomLevels: 6,
                projection: "EPSG:3785",
                units: "m"
            });

            var layer_1 = new OpenLayers.Layer.WMS(
                    "基础区域", baselayerUrl,
                    {"layers": layerName1},
                    {
                        isBaseLayer: true,
                        opacity: .4
                    }
            );
//            var layer_2 = new OpenLayers.Layer.WMS(
//                    "房屋图层", baselayerUrl,
//                    {"layers": layerName2, transparent: true},
//                    {
//                        displayInLayerSwitcher: false,
//                        opacity: .3,
//                        minScale: 250000000,
//                        units: "degrees",
//                        transitionEffect: "resize"
//                    }
//            );

            // 1、新建矢量图层
            ph00301_build_layer = new OpenLayers.Layer.Vector("多边形图层");

            //2、获取坐标  判空处理
            if (strPoints != undefined && strPoints != '') {
                var lineNum = strPoints.split("|");   //切分每条线
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
                    var lineFeature = new OpenLayers.Feature.Vector(linestring, null);

                    //5、生成线图层
                    ph00301_build_layer.addFeatures([lineFeature]);
                }
                //设置地图展示中心
                var xCenter = (Math.max.apply(null, Xarr) + Math.min.apply(null, Xarr)) / 2;
                var yCenter = (Math.max.apply(null, Yarr) + Math.min.apply(null, Yarr)) / 2;
                lon = xCenter;
                lat = yCenter;
            }

            //添加选中事件
            var selectedFeatrue = null;
            ph00301_build_layer.events.on({
                featureselected: function () {
                    selectedFeatrue = this.selectedFeatures;
                },
                featureunselected: function () {
                    selectedFeatrue = null;
                    //document.getElementById('counter').innerHTML = this.selectedFeatures.length;
                }
            });

            // 6、加入图层到地图
            buildMap.addLayers([layer_1, ph00301_build_layer]);

            /**************************************通过选择操作模式  设置动作类型**************************************************************/
            modelCtrls = {
                polygon: new OpenLayers.Control.DrawFeature(
                        ph00301_build_layer, OpenLayers.Handler.Polygon
                ),
                select: new OpenLayers.Control.SelectFeature(
                        ph00301_build_layer,
                        {
                            onSelect: function () {
                                alertMsg.confirm("确定要删除该住房吗?", {
                                    okCall: function () {
                                        ph00301_build_layer.removeFeatures(selectedFeatrue);
                                        selectedFeatrue = null;
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
//                modify: new OpenLayers.Control.ModifyFeature(ph00301_build_layer)
            };
            for (var key in modelCtrls) {
                buildMap.addControl(modelCtrls[key]);
            }

            //设置初始化的起始点，缩放级别
            buildMap.setCenter(new OpenLayers.LonLat(lon, lat), level);
        },

        /** 获取所画图形坐标 */
        showBuildPoint: function () {
            var tempFeatures = ph00301_build_layer.features;
            var buildPoints;
            if (tempFeatures.length > 0) {
                for (var i = 0; i < tempFeatures.length; i++) {
                    var geom = tempFeatures[i].geometry;
                    buildPoints = geom.getVertices().toString() + "|" + buildPoints;
                }
                if (buildPoints != undefined) {
                    buildPoints = buildPoints.substring(0, buildPoints.length - 10);
                }
                if (buildPoints.substring(buildPoints.length - 15) == '|POINT(NaN NaN)') {
                    buildPoints = buildPoints.substring(0, buildPoints.length - 15);
                }
            }
        },
        /**
         * 关闭院落地图修改页
         * */
        closeBuildMap: function () {
            var tempFeatures = ph00301_build_layer.features;
            var buildPoints;
            if (tempFeatures.length > 0) {
                for (var i = 0; i < tempFeatures.length; i++) {
                    var geom = tempFeatures[i].geometry;
                    if (buildPoints == undefined || buildPoints == 'POINT(NaN NaN)|') {
                        buildPoints = geom.getVertices().toString();
                        continue;
                    }
                    buildPoints = geom.getVertices().toString() + "|" + buildPoints;
                }
            }
            var center = EMap.calcPointsCenter(buildPoints);
            var dataArr = [buildPoints, center[0], center[1]];
            if (EMap.cfmDraw(dataArr)) {
                $.pdialog.closeCurrent();
                return true;
            }
        }
    };

    $(document).ready(function () {
        window.setTimeout(function () {
            //初始化地图渲染
            ph00301_buildmap.initBuildMap();
        }, 500);
    });
</script>