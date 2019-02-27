<%--打开 编辑 房产地图页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房产地图--%>
<div class="panelBar" style="display: block; width: 20%;">
    <ul class="toolBar">
        <li><a class="edit" onclick="ph00301_hsmap.closeHsMap(this);"><span>确定</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>取消</span></a></li>

        <li style="float: left; margin-left: 20px;">
            <input type="radio" name="checkModeltype" value="select" id="noneToggle"
                   onclick="ph00301_hsmap.toggleCtrl(this);" checked="checked"/>
            <label for="noneToggle">导航模式</label>
        </li>
        <li style="float: left; margin-left: 20px;">
            <input type="radio" name="checkModeltype" value="addHsPolygon" id="addHsPolygon"
                   onclick="ph00301_hsmap.toggleCtrl(this);"/>
            <label for="addHsPolygon" style="color: #41D930">标注正式住房</label>
        </li>
        <li style="float: left; margin-left: 20px;">
            <input type="radio" name="checkModeltype" value="deleteHs" id="deleteHs"
                   onclick="ph00301_hsmap.toggleCtrl(this);"/>
            <label for="deleteHs" style="color: #41D930">删除正式住房</label>
        </li>

        <li style="float: left; margin-left: 50px;">
            <input type="radio" name="checkModeltype" value="addHsSlfPolygon" id="addHsSlfPolygon"
                   onclick="ph00301_hsmap.toggleCtrl(this);"/>
            <label for="addHsSlfPolygon" style="color: #2FD7DA">标注自建房</label>
        </li>
        <li style="float: left; margin-left: 20px;">
            <input type="radio" name="checkModeltype" value="deleteHsSlf" id="deleteHsSlf"
                   onclick="ph00301_hsmap.toggleCtrl(this);"/>
            <label for="deleteHsSlf" style="color: #2FD7DA">删除自建房</label>
        </li>
    </ul>
</div>
<%--房产详细地图--%>
<div layoutH="40" style="position: relative;">
    <div style="position: absolute;left: 10px; top: 10px;z-index: 10000; background-color: white;">
        <input style="width: 250px;color: darkblue; height: 25px;padding-left:5px;line-height: 30px;" type="text"
               onkeydown="if(event.keyCode == 13){ph00301_hsmap.queryOthHs(this);}"
               name="query" id="query" class="js_clear textInput pull-left"/>
        <a class="btnLook" style="margin-right: 0;" onclick="ph00301_hsmap.queryOthHs(this)">&nbsp;</a>
        <input type="hidden" name="HouseInfo_hsId" value=""/>
    </div>
    <div id="statusShow"
         style="position: absolute; min-width: 300px; right: 30px; top: 2px;background-color: #efefef;z-index: 100;">
            <span onclick="ph00301_hsmap.switchTip(this)"
                  style="position: absolute; top:0px; right: 0px;display: inline-block; " class="hideTip"></span>
        <table class="border">
            <tr>
                <th valign="top" style="text-align: center;"><label>绿色实线边框：</label></th>
                <td align="center">
                    正式房屋边界
                </td>
            </tr>
            <tr>
                <th valign="top" style="text-align: center;"><label>绿色虚线边框：</label></th>
                <td align="center">
                    自建房屋边界
                </td>
            </tr>
            <tr>
                <th valign="top" style="text-align: center;"><label>紫色实线边框：</label></th>
                <td align="center">
                    满足检索条件已标注房屋
                </td>
            </tr>
        </table>
    </div>
    <div id="ph00301_hs_map" tabindex="0"
         style="clear: both; width: 99%; height: 99%; border: 1px #000000 solid;position: relative; display: inline-block; overflow: hidden;">
    </div>
</div>
<script type="text/javascript">
    var hsMap, selectControl;
    var selectedHsFeature = null;

    var modelCtrls;
    var ph00301_bld_layer;
    var ph00301_oth_hs_layer;
    var ph00301_hs_layer;
    var ph00301_hsSlf_layer;
    var ph00301_buildBorder_layer;

    var ph00301_hsmap = {
        defaultLevel: 2,
        switchTip: function (obj) {
            var $this = $(obj);
            if ($this.hasClass("hideTip")) {
                $this.removeClass("hideTip");
                $this.addClass("showTip");
                $this.next("table").hide();
            } else {
                $this.removeClass("showTip");
                $this.addClass("hideTip");
                $this.next("table").show();
            }
        },
        /** 判断编辑房产类型---以及所选工作模式 */
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

        /** 初始化地图数据 */
        initHsMap: function () {
            //可配置参数从参数配置里查询
            var baselayerUrl = '${baselayerUrl}';
            var layerName1 = '${layerName1}';
            var layerName2 = '${layerName2}';
            //院落边界 [正式房坐标, 自建房坐标, 院落坐标]
            var dataArr = EMap.initData();
            var hsPointsStr = dataArr[0] ? dataArr[0] : "";
            var hsSlfPointsStr = dataArr[1] ? dataArr[1] : "";
            var bldPointsStr = dataArr[2] ? dataArr[2] : "";//所属院落坐标
            var hsArea = dataArr[3] ? dataArr[3] : "";//房屋社区
            var hsRoad = dataArr[4] ? dataArr[4] : "";//房屋街道
            var hsNo = dataArr[5] ? dataArr[5] : "";//房屋门牌
            var hsId = dataArr[6] ? dataArr[6] : "";//房屋编号
            var hsAddr = hsArea + hsRoad + hsNo;//房屋默认地址
            $("input[name=query]", $.pdialog.getCurrent()).val(hsAddr);
            $("input[name=HouseInfo_hsId]", $.pdialog.getCurrent()).val(hsId);
            var lon, lat, level;
            //赋予 地图中心，缩放等级默认值。如果有数据下面会重新计算 定位中心
            ${lonLatLevel}
            level = 2;
            hsMap = new OpenLayers.Map({
                div: "ph00301_hs_map",
                //增加控制按钮
                controls: [
                    new OpenLayers.Control.PanZoomBar({
                        position: new OpenLayers.Pixel(5, 30)
                    }),
                    new OpenLayers.Control.Navigation(),
                    new OpenLayers.Control.MousePosition()
                ],
                maxExtent: new OpenLayers.Bounds(${mapBounds}),     //边界：left, bottom, right, top
                maxResolution: "auto",                                      //最大分辨率（maxResolution）
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
            //设置所属院落(不可编辑，只能查看) 自定义样式  定义样式
            var myBldStyles = new OpenLayers.StyleMap({
                "default": new OpenLayers.Style({
                    pointRadius: 3,
                    fillColor: "#cfe2f3",
                    fillOpacity: .7,
                    strokeColor: "#cfe2f3",
                    strokeOpacity: .7,
                    strokeWidth: 2
                })
            });
            //设置同胡同下的房屋(不可编辑，只能查看) 自定义样式  定义样式
            var myOthHsStyles = new OpenLayers.StyleMap({
                "default": new OpenLayers.Style({
                    pointRadius: 3,
                    fillColor: "#fce5cd",
                    fillOpacity: .2,
                    strokeColor: "#fce5cd",
                    strokeOpacity: .8,
                    strokeWidth: 2
                })
            });
            //设置正式住房 自定义样式  定义样式
            var myHsStyles = new OpenLayers.StyleMap({
                "default": new OpenLayers.Style({
                    pointRadius: 3,
                    fillColor: "#41D930",
                    fillOpacity: .3,
                    strokeColor: "#41D930",
                    strokeOpacity: 1,
                    strokeWidth: 2
                })
            });
            //设置 自建房 自定义样式  定义样式
            var myHsSlfStyles = new OpenLayers.StyleMap({
                "default": new OpenLayers.Style({
                    pointRadius: 3,
                    fillColor: "#2FD7DA",
                    fillOpacity: .3,
                    strokeColor: "#41D930",
                    strokeOpacity: 1,
                    strokeWidth: 2,
                    strokeDashstyle: "longdashdot"
                })
            });
            // 1、新建矢量图层
            ph00301_buildBorder_layer = new OpenLayers.Layer.Vector("院落边界");
            ph00301_bld_layer = new OpenLayers.Layer.Vector("所属院落", {
                styleMap: myBldStyles,
                rendererOptions: {zIndexing: true}
            });
            ph00301_oth_hs_layer = new OpenLayers.Layer.Vector("同街道其他房屋", {
                styleMap: myOthHsStyles,
                rendererOptions: {zIndexing: true}
            });
            ph00301_hs_layer = new OpenLayers.Layer.Vector("房产详情", {
                styleMap: myHsStyles,
                rendererOptions: {zIndexing: true}
            });
            ph00301_hsSlf_layer = new OpenLayers.Layer.Vector("自建房详情", {
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

            /**2-1. 获取所属院落坐标  绘制已有的房产地图*/
            if (bldPointsStr != undefined && bldPointsStr != '') {
                var bldNum = bldPointsStr.split("|");
                var bldArr = [];
                for (var iBld = 0; iBld < bldNum.length; iBld++) {
                    bldArr = bldNum[iBld].split(",");
                    var bldStrList = [];
                    //3、将坐标字符串分解为坐标数组
                    for (var jBld = 0; jBld < bldArr.length; jBld++) {
                        var bldArrs = bldArr[jBld].replace('POINT(', '').replace(')', '').split(' ');
                        // 有编辑
                        bldStrList.push(new OpenLayers.Geometry.Point(bldArrs[0], bldArrs[1]));
                    }
                    //坐标数组
                    var bldLineRel = new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(bldStrList));
                    var bldLineFeature = new OpenLayers.Feature.Vector(bldLineRel, null, {
                        fillColor: "#cfe2f3",
                        fillOpacity: 0,
                        strokeColor: "#ff0000",
                        strokeOpacity: 1,
                        strokeWidth: 2
                    });
                    //5、生成线图层
                    ph00301_bld_layer.addFeatures([bldLineFeature]);
                }
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
                    var hsLineFeature = new OpenLayers.Feature.Vector(hsLineRel, null);
                    hsLineFeature.attributes = {
                        clickId: $("input[name=HouseInfo_hsId]").val(),
                        clickType: 'house'
                    };
                    hsLineFeature.style = {
                        fillColor: "#41D930",
                        fillOpacity: .3,
                        strokeColor: "#41D930",
                        strokeOpacity: 1,
                        strokeWidth: 2,
                        label: $("input[name=HouseInfo_hsAddrNo]").val(),
                        fontSize: "14px",
                        fontFamily: "微软雅黑",
                        labelOutlineColor: "white",
                        labelOutlineWidth: 3
                    };
                    //5、生成线图层
                    ph00301_hs_layer.addFeatures([hsLineFeature]);
                }
            }

            // 3. 获取自建房 坐标  绘制已有的 自建房地图
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
                    var hsSlfLineFeature = new OpenLayers.Feature.Vector(hsSlfLineRel, null);
                    hsSlfLineFeature.attributes = {
                        clickId: $("input[name=HouseInfo_hsId]").val(),
                        clickType: 'house'
                    };
                    hsSlfLineFeature.style = {
                        fillColor: "#2FD7DA",
                        fillOpacity: .3,
                        strokeColor: "#41D930",
                        strokeOpacity: 1,
                        strokeWidth: 2,
                        strokeDashstyle: "longdashdot",
                        label: $("input[name=HouseInfo_hsAddrNo]").val(),
                        fontSize: "14px",
                        fontFamily: "微软雅黑",
                        labelOutlineColor: "white",
                        labelOutlineWidth: 3
                    };
                    //5、生成线图层
                    ph00301_hsSlf_layer.addFeatures([hsSlfLineFeature]);
                }
            }

            //图层事件控制。
            ph00301_hs_layer.events.on({
                featureselected: function () {
                    selectedHsFeature = this.selectedFeatures;
                },
                featureunselected: function () {
                    selectedHsFeature = null;
                }
            });

            //图层事件控制。
            ph00301_hsSlf_layer.events.on({
                featureselected: function () {
                    selectedHsFeature = this.selectedFeatures;
                },
                featureunselected: function () {
                    selectedHsFeature = null;
                }
            });

            // 6、加入所有图层到地图
            hsMap.addLayers([layer_1, ph00301_buildBorder_layer, ph00301_bld_layer,
                ph00301_oth_hs_layer, ph00301_hs_layer, ph00301_hsSlf_layer]);
            /**************************************通过选择操作模式  设置动作类型**************************************************************/
            selectControl = new OpenLayers.Control.SelectFeature([ph00301_hs_layer, ph00301_hsSlf_layer],
                    {
                        onSelect: ph00301_hsmap.onFeatureSelect,
                        onUnselect: ph00301_hsmap.onFeatureUnselect,
                        //选中弹框颜色
                        selectStyle: {
                            fillColor: "blue",
                            fillOpacity: .2,
                            strokeColor: "blue",
                            strokeOpacity: .2,
                            strokeWidth: 2
                        }
                    });

            modelCtrls = {
//                select: selectControl,
                addHsPolygon: new OpenLayers.Control.DrawFeature(
                        ph00301_hs_layer, OpenLayers.Handler.Polygon
                ),
                addHsSlfPolygon: new OpenLayers.Control.DrawFeature(
                        ph00301_hsSlf_layer, OpenLayers.Handler.Polygon
                ),
                deleteHs: new OpenLayers.Control.SelectFeature(
                        ph00301_hs_layer,
                        {
                            selectStyle: {
                                //#selectColor 选中图形颜色
                                fillColor: "blue",
                                fillOpacity: .5,
                                strokeColor: "blue",
                                strokeOpacity: .5,
                                strokeWidth: 2
                            },
                            onSelect: function () {
                                alertMsg.confirm("确定要删除该住房吗?", {
                                    okCall: function () {
                                        ph00301_hs_layer.removeFeatures(selectedHsFeature);
                                        selectedHsFeature = null;
                                        return false;
                                    }
                                });
                            },
                            clickout: true, toggle: false,
                            multiple: false, hover: false,
                            toggleKey: "ctrlKey", // ctrl key removes from selection
                            multipleKey: "shiftKey" // shift key adds to selection
                        }
                ),
                deleteHsSlf: new OpenLayers.Control.SelectFeature(
                        ph00301_hsSlf_layer,
                        {
                            selectStyle: {
                                //#selectColor 选中图形颜色
                                fillColor: "blue",
                                fillOpacity: .4,
                                strokeColor: "blue",
                                strokeOpacity: .4,
                                strokeWidth: 2
                            },
                            onSelect: function () {
                                alertMsg.confirm("确定要删除该住房吗?", {
                                    okCall: function () {
                                        ph00301_hsSlf_layer.removeFeatures(selectedHsFeature);
                                        selectedHsFeature = null;
                                        return false;
                                    }
                                });
                            },
                            clickout: true, toggle: false,
                            multiple: false, hover: false,
                            toggleKey: "ctrlKey", // ctrl key removes from selection
                            multipleKey: "shiftKey" // shift key adds to selection
                        }
                )
            };
            //动态切换 模式 增加到 map里。
            for (var key in modelCtrls) {
                hsMap.addControl(modelCtrls[key]);
            }

            // 设置初始化的起始点，缩放级别
            if (bldPointsStr != undefined && bldPointsStr != '') {
                var newBound = ph00301_bld_layer.getDataExtent();
                hsMap.zoomToExtent(newBound);
            } else {
                hsMap.setCenter(new OpenLayers.LonLat(lon, lat), ph00301_hsmap.defaultLevel);
            }


            // 当前房屋没有信息执行检索
            if (ph00301_hs_layer.features == 0 && ph00301_hsSlf_layer.features == 0) {
                ph00301_hsmap.queryOthHs(null);
            }

            /**
             * 5、原有标注地图 增加键盘事件 esc...
             **/
            OpenLayers.Event.observe($.pdialog.getCurrent()[0], "keydown", function (evt) {
                var handled = false;
                switch (evt.keyCode) {
                    case 27: // esc
                        //动态切换 模式 增加到 map里。
                        modelCtrls["addHsPolygon"].cancel();
                        modelCtrls["addHsSlfPolygon"].cancel();
                        handled = true;
                        break;
                }
                if (handled) {
                    OpenLayers.Event.stop(evt);
                }
            });
        },

        /** 点击选中弹框提示 */
        onFeatureSelect: function (feature) {
            selectedHsFeature = feature;
            popup = new OpenLayers.Popup.FramedCloud("chicken",
                    feature.geometry.getBounds().getCenterLonLat(),
                    null,
                    "<div style='font-size:.8em'>待添加房产信息：id、name、超链接、Feature: " + feature.id + "<br>Area: " + feature.geometry.getArea() + "</div>",
                    null, true, ph00301_hsmap.onPopupClose);
            feature.popup = popup;
            hsMap.addPopup(popup);
        },

        /** 取消选中弹框 */
        onFeatureUnselect: function (feature) {
            hsMap.removePopup(feature.popup);
            feature.popup.destroy();
            feature.popup = null;
        },

        /** 关闭弹框 */
        onPopupClose: function (evt) {
            selectControl.unselect(selectedHsFeature);
        },

        /** 获取所画图形坐标 */
        showHsPoint: function () {
            var hsFeatures = ph00301_hs_layer.features;
            var hsSlfFeatures = ph00301_hsSlf_layer.features;
            var hsPoints;
            var hsSlfPoints;
            if (hsFeatures.length > 0) {
                for (var i = 0; i < hsFeatures.length; i++) {
                    var hsGeom = hsFeatures[i].geometry;
                    if (hsPoints == undefined) {
                        hsPoints = hsGeom.getVertices().toString();
                        continue;
                    }
                    hsPoints = hsGeom.getVertices().toString() + "|" + hsPoints;
                }
            }
            if (hsSlfFeatures.length > 0) {
                for (var j = 0; j < hsSlfFeatures.length; j++) {
                    var hsSlfGeom = hsSlfFeatures[j].geometry;
                    if (hsSlfPoints == undefined) {
                        hsSlfPoints = hsSlfGeom.getVertices().toString();
                        continue;
                    }
                    hsSlfPoints = hsSlfGeom.getVertices().toString() + "|" + hsSlfPoints;
                }
            }
        },

        /**
         * 关闭房屋地图修改页
         * */
        closeHsMap: function () {
            var hsFeatures = ph00301_hs_layer.features;
            var hsPoints;
            var hsSlfFeatures = ph00301_hsSlf_layer.features;
            var hsSlfPoints;
            if (hsFeatures.length > 0) {
                for (var i = 0; i < hsFeatures.length; i++) {
                    var hsGeom = hsFeatures[i].geometry;
                    if (hsPoints == undefined || hsPoints == 'POINT(NaN NaN)|') {
                        hsPoints = hsGeom.getVertices().toString();
                        continue;
                    }
                    hsPoints = hsGeom.getVertices().toString() + "|" + hsPoints;
                }
            }
            if (hsSlfFeatures.length > 0) {
                for (var j = 0; j < hsSlfFeatures.length; j++) {
                    var hsSlfGeom = hsSlfFeatures[j].geometry;
                    if (hsSlfPoints == undefined || hsSlfPoints == 'POINT(NaN NaN)|') {
                        hsSlfPoints = hsSlfGeom.getVertices().toString();
                        continue;
                    }
                    hsSlfPoints = hsSlfGeom.getVertices().toString() + "|" + hsSlfPoints;
                }
            }
            var center = [];
            if (hsPoints != undefined) {
                center = EMap.calcPointsCenter(hsPoints);
            } else if (hsSlfPoints != undefined) {
                center = EMap.calcPointsCenter(hsSlfPoints);
            } else {
                center[0] = '';
                center[1] = '';
            }
            var dataArr = [hsPoints, hsSlfPoints, center[0], center[1]];
            if (EMap.cfmDraw(dataArr)) {
                $.pdialog.closeCurrent();
                return true;
            }
        },

        /*按照查询条件查询满足条件的其他房屋*/
        queryOthHs: function (obj) {
            var resultData = {};
            var hsId = $("input[name=HouseInfo_hsId]", $.pdialog.getCurrent()).val();
            var hsFullAddr = $("input[name=query]", $.pdialog.getCurrent()).val();
            if (hsFullAddr == "") {
                ph00301_oth_hs_layer.removeAllFeatures();
                return;
            }
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-queryOthHs.gv?";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("hsId", hsId);
            ajaxPacket.data.add("hsFullAddr", hsFullAddr);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                resultData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = resultData.success;
                var othHsMapList = resultData.othHsResult;
                if (isSuccess) {
                    ph00301_oth_hs_layer.removeAllFeatures();
                    ph00301_hsmap.initHouseData(othHsMapList);
                }
            }, false);
        },

        /**
         * 根据地图数据,渲染图形：房产
         */
        initHouseData: function (othHsMapList) {
            var lon, lat;
            for (var i = 0; i < othHsMapList.length; i++) {
                // loop 得到单个房产
                var hsTemp = othHsMapList[i];
                // 判断是否有地图
                if (hsTemp.hsRoomPos != undefined && hsTemp.hsRoomPos != '') {
                    var middNum = hsTemp.middNum;
                    if (i == middNum) {
                        lon = hsTemp.hsPosX;
                        lat = hsTemp.hsPosY;
                    }
                    // 每个房产 正式住房数量
                    var hsFormalNum = hsTemp.hsRoomPos.split("|");
                    var hsFormalNumArr = [];
                    // 正式住房 绘制地图
                    for (var hsForIdx = 0; hsForIdx < hsFormalNum.length; hsForIdx++) {
                        // 获取单个正式住房的 每个坐标x、y点  存入坐标点集:everyHsFormalPoints
                        var everyHsFormalPoints = [];
                        hsFormalNumArr = hsFormalNum[hsForIdx].split(",");
                        for (var hsFormalIdx = 0; hsFormalIdx < hsFormalNumArr.length; hsFormalIdx++) {
                            //拿到每个点的X、Y坐标集:hsFormalXYs
                            var hsFormalXYs = hsFormalNumArr[hsFormalIdx].replace('POINT(', '').replace(')', '').split(' ');
                            if (hsFormalXYs != null && hsFormalXYs != '') {
                                everyHsFormalPoints.push(new OpenLayers.Geometry.Point(hsFormalXYs[0], hsFormalXYs[1]));
                            }
                        }
                        // 为每一个房屋everyHsFormalFeature 设置自定义属性，自定义样式，并添加到查询的图层上。
                        var everyHsFormalFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon(
                                new OpenLayers.Geometry.LinearRing(everyHsFormalPoints)));
                        everyHsFormalFeature.attributes = {
                            clickId: hsTemp.hsId,
                            clickType: "house",
                            hsFullAddr: hsTemp.hsFullAddr,
                            hsStatus: hsTemp.hsStatus,
                            hsOwnerPersons: hsTemp.hsOwnerPersons
                        };
                        everyHsFormalFeature.style = {
                            // 推送颜色根据后台 维度不同推送不同map
                            fillColor: "#fce5cd",
                            fillOpacity: .6,
                            strokeColor: "#0000ff",
                            strokeOpacity: .5,
                            strokeWidth: 2,
                            label: hsTemp.hsAddrNo + "\n" + hsTemp.hsOwnerPersons,
                            fontSize: "12px",
                            fontFamily: "微软雅黑",
                            labelOutlineColor: "white",
                            labelOutlineWidth: 3
                        };
                        ph00301_oth_hs_layer.addFeatures([everyHsFormalFeature]);
                    }

                }
                // 判断是否有自建房地图
                if (hsTemp.hsSlfRoomPos != undefined && hsTemp.hsSlfRoomPos != '') {
                    //每个房产 正式住房数量
                    var hsSlfNum = hsTemp.hsSlfRoomPos.split("|");
                    var hsSlfNumArr = [];
                    //正式住房 绘制地图
                    for (var hsSlfIdx = 0; hsSlfIdx < hsSlfNum.length; hsSlfIdx++) {
                        //获取单个正式住房的 每个坐标x、y点  存入坐标点集:everyHsSlfPoints
                        var everyHsSlfPoints = [];
                        hsSlfNumArr = hsSlfNum[hsSlfIdx].split(",");
                        for (var hsSlfPtIdx = 0; hsSlfPtIdx < hsSlfNumArr.length; hsSlfPtIdx++) {
                            //拿到每个点的X、Y坐标集:hsSlfXYs
                            var hsSlfXYs = hsSlfNumArr[hsSlfPtIdx].replace('POINT(', '').replace(')', '').split(' ');
                            if (hsSlfXYs != null && hsSlfXYs != '') {
                                everyHsSlfPoints.push(new OpenLayers.Geometry.Point(hsSlfXYs[0], hsSlfXYs[1]));
                            }
                        }
                        //为每一个房屋everyHsFormalFeature 设置自定义属性，自定义样式，并添加到院落图层上。
                        var everyHsSlfFeature = new OpenLayers.Feature.Vector(
                                new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(everyHsSlfPoints)));
                        everyHsSlfFeature.attributes = {
                            clickId: hsTemp.hsId,
                            clickType: "house",
                            hsFullAddr: hsTemp.hsFullAddr,
                            hsStatus: hsTemp.hsStatus,
                            hsOwnerPersons: hsTemp.hsOwnerPersons
                        };
                        everyHsSlfFeature.style = {
                            fillColor: "#999999",
                            fillOpacity: .6,
                            strokeColor: "#0000ff",
                            strokeOpacity: .5,
                            strokeWidth: 2,
                            strokeDashstyle: "longdashdot",   //虚线样式
                            label: hsTemp.hsAddrNo + "\n" + hsTemp.hsOwnerPersons,
                            fontSize: "12px",
                            fontFamily: "微软雅黑",
                            labelOutlineColor: "white",
                            labelOutlineWidth: 3
                        };
                        ph00301_oth_hs_layer.addFeatures([everyHsSlfFeature]);
                    }
                }

                // 设置初始化的起始点，缩放级别
                if (lon != null && lat != null && ph00301_hs_layer.features == 0 && ph00301_hsSlf_layer.features == 0) {
                    hsMap.setCenter(new OpenLayers.LonLat(lon, lat), 5);
                }
            }
        }
    };

    $(document).ready(function () {
        window.setTimeout(function () {
            //初始化地图渲染
            ph00301_hsmap.initHsMap();
        }, 500);
    })
</script>