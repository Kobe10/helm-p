<%--打开 编辑 Camera地图页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--Camera地图--%>
<div class="panelBar" style="display: block; width: 20%;">
    <ul class="toolBar">
        <input type="hidden" name="camId" value="${camId}"/>
        <li><a class="edit" onclick="ph010_cam.closeCamMap(this);"><span>保存</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>

        <%--<li class="mgr-25"><span style="color: #ff0000; background: none">单击地图位置添加标记</span></li>--%>
        <li class="mgr-25">
            <input type="radio" name="camModel" value="true" checked/>开启标记
            <input type="radio" name="camModel" value="false"/>关闭标记
            <%--<a class="close" type="button" value="Rm Marker" onclick="ph010_cam.rmMarker();">清除</a>--%>
        </li>
        <%--<li class="mgr-25"><input type="button" onclick="ph010_cam.showCamPoint();" value="查看图形坐标"/></li>--%>
    </ul>

</div>
<%--Camera详细地图--%>
<div layoutH="40">
    <div id="cam_map_div" tabindex="0"
         style="clear: both; width: 99%; height: 99%; border: 1px #000000 solid;position: relative; display: inline-block; overflow: hidden;">
    </div>
</div>
<script type="text/javascript">
    var camMap = null;
    var marker_layer = null;
    var modelCtrls;

    OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {
        defaultHandlerOptions: {
            'single': true,
            'double': false,
            'pixelTolerance': 0,
            'stopSingle': false,
            'stopDouble': false
        },

        initialize: function (options) {
            this.handlerOptions = OpenLayers.Util.extend(
                    {}, this.defaultHandlerOptions
            );
            OpenLayers.Control.prototype.initialize.apply(
                    this, arguments
            );
            this.handler = new OpenLayers.Handler.Click(
                    this, {
                        'click': this.trigger
                    }, this.handlerOptions
            );
        },

        trigger: function (e) {
            var lonlat = camMap.getLonLatFromViewPortPx(e.xy);
            var camModel = $("input[name=camModel]:checked").val();
            if (camModel == 'true') {
                if (marker_layer.markers.length == 0) {
                    ph010_cam.addMarker(lonlat.lon, lonlat.lat, 0.8);
                } else {
                    ph010_cam.rmMarker();
                    ph010_cam.addMarker(lonlat.lon, lonlat.lat, 0.8);
                }
            }
//            alert("You clicked near " + lonlat.lat + " N, " + +lonlat.lon + " E");
        }
    });

    var ph010_cam = {
        /** 判断编辑Camera类型---以及所选工作模式 */
        toggleCtrl: function (element) {
            for (var key in modelCtrls) {
                var control = modelCtrls[key];
                if (element.value == key && element.checked) {
                    control.activate();
                } else {
                    control.deactivate();
                }
            }
        },

        init: function () {
            var wms_url = '${baselayerUrl}';
            var layer_name = '${layerName1}';
            var camPos = '${cameraInfo.CameraMngInfo.markPos}';

            //创建map对象，
            camMap = new OpenLayers.Map({
                div: "cam_map_div",
                //增加控制按钮
                controls: [
                    new OpenLayers.Control.PanZoomBar({
                        position: new OpenLayers.Pixel(3, 5)
                    }),
                    new OpenLayers.Control.Navigation()
//                    new OpenLayers.Control.LayerSwitcher(),   //图层选择工具
//                    new OpenLayers.Control.MousePosition()
                ],
                maxExtent: new OpenLayers.Bounds(${mapBounds}),     //边界：left, bottom, right, top
                maxResolution: "auto",                                      //最大分辨率（maxResolution）
                numZoomLevels: 5,
                projection: "EPSG:3785",
                units: "m"
            });

            var wms_layer = new OpenLayers.Layer.WMS("基础区域",
                    wms_url,
                    {layers: layer_name},
                    {
                        singleTile: true,
                        isBaseLayer: true,
                        opacity: .4
                    });

            marker_layer = new OpenLayers.Layer.Markers("markers");
            if (camPos != null && camPos != '') {
                var lonlat = camPos.split(",");
                ph010_cam.addMarker(lonlat[0], lonlat[1], 0.8);
            }

            // 添加图层
            camMap.addLayers([wms_layer, marker_layer]);
            // 放大到全屏
            camMap.zoomToMaxExtent();
            // 单击事件
            var click = new OpenLayers.Control.Click();
            camMap.addControl(click);
            click.activate();

            if (camPos != null && camPos != '') {
                var lonlat = camPos.split(",");
                camMap.setCenter(new OpenLayers.LonLat(lonlat[0], lonlat[1]), 1);
            }
        },

        addMarker: function (x, y, opacity) {
            var size = new OpenLayers.Size(20, 30);
            var offset = new OpenLayers.Pixel(-(size.w / 2), -size.h);
            var icon = new OpenLayers.Icon('${pageContext.request.contextPath}/oframe/themes/images/web-cam.png', size, offset);
            var markerCam = new OpenLayers.Marker(new OpenLayers.LonLat(x, y), icon);
            //透明度
            markerCam.setOpacity(opacity);
            marker_layer.addMarker(markerCam);
        },

        //移除图层上所有的markers。
        rmMarker: function () {
            marker_layer.clearMarkers();
        },

        /** 获取所画图形坐标 测试用 */
        showCamPoint: function () {
            console.log(camMap.getLayersByName("markers"));
            console.log(camMap.getLayersByName("markers").markers);
        },

        //保存摄像头位置信息
        closeCamMap: function () {
            var camId = $("input[name=camId]", $.pdialog.getCurrent()).val();
            var pos;
            if (marker_layer.markers[0] != null && marker_layer.markers[0] != 'undefined') {
                pos = marker_layer.markers[0].lonlat.lon.toFixed(4) + "," + marker_layer.markers[0].lonlat.lat.toFixed(4);
            } else {
                pos = "";
            }
            var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-saveCamPos.gv?prjCd=" + getPrjCd();
            var packet = new AJAXPacket(url);
            packet.data.add("camId", camId);
            packet.data.add("posStr", pos);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    };

    $(document).ready(function () {
        window.setTimeout(function () {
            //初始化地图渲染
            ph010_cam.init();
        }, 500);
    })

</script>