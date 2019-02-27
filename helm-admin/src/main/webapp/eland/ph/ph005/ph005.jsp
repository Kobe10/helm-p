<%--打开 编辑 房产地图页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style>
    #ph005Container .phBtn {
        width: 60px;
        height: 40px;
        position: absolute;
        z-index: 1000;
        right: 10px;
        top: 15px;
        cursor: pointer;
    }

    #ph005Container .phBtn button {
        border-radius: 10px;
        display: inherit;
        width: 60px;
        height: 40px;
        background-color: #67c0dc;
        cursor: pointer;
    }

    #ph005Container .phBtn button:hover {
        background-color: #3C90C8;
    }

    #ph005Container .phBtn button label {
        color: #ffffff;
        cursor: pointer;
    }

    #ph005Container .phBtn button.active {
        background-color: #3078a4;
    }

    #ph005Container .colorCheck {
        text-align: left;
        line-height: 25px;
        height: 25px;
        word-break: keep-all;
        overflow: hidden;
        cursor: pointer;
        margin: 2px;
        background-color: #fbfbfb;
    }

    #ph005Container .colorCheck:hover {
        color: #fff;
        background-color: #3078a4;
    }

    #ph005Container .colorCheck div {
        display: inline-block;
        margin-left: 5px;
    }

    #ph005Container div.arrow button.contraction {
        width: 15px;
        height: 130px;
        position: absolute;
        z-index: 100;
        background: #3C90C8;
        margin-left: 1px;
        margin-top: 18%;
        left: 5px;
    }

    #ph005Container div.arrow button.extend {
        width: 15px;
        height: 130px;
        position: absolute;
        z-index: 100;
        background: #3C90C8;
        margin-left: 1px;
        margin-top: 18%;
    }

    #ph005Container div.selected {
        color: #000 !important;
        background-color: #3c9fd5 !important;
    }

    #ph005Container span.fullScreen {
        position: absolute;
        top: 15px !important;
        left: 100px !important;
        z-index: 10;
    }

    #ph005Container span.sltMoveIcon {
        position: absolute;
        top: 15px;
        left: 60px;
        z-index: 10;
    }

    #ph005Container div.searchDiv {
        z-index: 100;
    }
</style>

<%--房产详细地图--%>
<div id="ph005Container" layoutH="10" class="mar5">
    <%--地图左侧搜索框--%>
    <div style="position: absolute;">
        <div style="width: 240px;float: left;position: relative;z-index: 101;background-color: white;float: left;"
             id="ph005LeftDiv"
             layoutH="15"
             class="panel left_menu searchDiv">
            <h1>
                <span class="panel_title">地图检索</span>
                <span>
                    <c:set var="chSearch" value="h"/>
                    <input type="radio" name="sltSearchObj" value="house" id="sltSearchObjHs"
                           onchange="ph005Search.chooseSearch(this, false);" checked/>
                    <label for="sltSearchObjHs">房产</label>
                </span>
                <span class="right marr10" style="cursor: pointer;" value="" onclick="ph005Search.reset();">重置</span>
            </h1>
            <%--展示查询条件区域--%>
            <div layoutH="55" class="js_ph005_search"></div>
        </div>
        <div class="split_line" style="z-index: 102;" layoutH="15">
            <span class="split_but" title="展开/隐藏导航"></span>
        </div>
    </div>
    <%--地图右侧 展示 图层按钮--%>
    <div id="ph005RightDiv"
         style="margin-left: 5px;margin-right: 5px;border: 1px #000000 solid;position: relative;height: 99%; ">
        <span id="selectToggle" title="选择模式" class="slt_move_icon sltMoveIcon"
              onclick="ph005Map.switchToggleCtrl(this)"></span>
        <%--判断是否是全屏模式--%>
        <c:if test="${fullFlag != 'new'}">
            <span title="全屏" class="mapPosition fullScreen" onclick="ph005Map.fullScreen(this)"></span>
        </c:if>
        <div id="ph005_viewAll_map"
             style="height: 100%;position: relative;overflow: hidden;">
            <div style="text-align: right;position: absolute;right: 5px;bottom: 5px" title="初始缩放比例">
                <label class="initScale"></label>
            </div>
            <div class="phBtn">
                <c:if test="${chSearch == 'h'}">
                    <button type="button" class="mart10 js_showLayer house active"
                            onclick="ph005Map.changeLayer(this, 'house');"
                            flag="house">
                        <label>房产</label></button>
                </c:if>
                <oframe:power prjCd="${param.prjCd}" rhtCd="show_cam_rht">
                    <button class="mart10 js_showLayer cam" type="button"
                            onclick="ph005Map.changeLayer(this, 'cam');" flag="cam">
                        <label>监控</label></button>
                </oframe:power>
            </div>
        </div>
        <div id="clickNodeInfo" layoutH="18" class="hidden"
             style="position: absolute;right:0px;top: 0px; z-index: 1001;border-left: 1px solid black; width: 30%; min-width: 250px;background-color: white;">
            <div onclick="ph005Map.hideNodeInfo();"
                 style="position: absolute;right: 0; top:0;z-index: 1000; margin: 5px; cursor: pointer;">X
            </div>
            <div id="nodeInfo"></div>
        </div>
    </div>
    <div id="ph005AdanceSch" class=""
         style="position: absolute;right:6px;top: -34px;overflow-x: hidden; height: 458px;"></div>
    <div class="mar5 hidden"
         style="position: absolute;width: 100%;background-color: rgba(59, 66, 73, 0.16);top: 0;left: 0;box-sizing: border-box;"
         id="ph005MoveCover"
         layoutH="0"></div>
</div>

<script type="text/javascript">
    /**
     * 定义地图对象，图层，控制模式
     */
    var viewMap, buildLayer, hsFormalLayer, hsSlfLayer, camLayer;
    var ctrlModels, sltCtrl, sltFeature;

    /**
     * 绘制地图
     */
    var ph005Map = {

        // 当前选中的节点
        featureSelectHsId: null,

        isPreparePos: true,

        /**
         * 设置通用左侧树，显示隐藏、拖动
         * @param leftDivId 左侧divId
         * @param rightDivId 右侧divId
         * @param options 是否设置cookie {cookieName: "cookieName"}
         */
        initLeft: function (leftDivId, options) {
            if (!options) {
                options = {};
            }
            var cookieName = options.cookieName;
            var leftDiv = $("#" + leftDivId, navTab.getCurrentPanel());
            var coverDiv = $("#ph005MoveCover", navTab.getCurrentPanel());

            // 拖动屏幕切分效果
            $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
                coverDiv.show();
                $("div.split_line", navTab.getCurrentPanel()).jDrag({
                    scop: true, cellMinW: 20,
                    move: "horizontal",
                    event: event,
                    stop: function (moveObj) {
                        coverDiv.hide();
                        leftDiv.width($(moveObj).position().left);
                        var resizeGrid = $("div.j-resizeGrid", navTab.getCurrentPanel());
                        if (resizeGrid.length > 0) {
                            resizeGrid.each(function () {
                                $(this).jResize();
                            });
                        }
                        $(moveObj).css("left", 0);
                    }
                });
            });
            $("span.split_but", navTab.getCurrentPanel()).mousedown(function (event) {
                stopEvent(event);
            });
            $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
                var splitLine = $(this).parent();
                var leftMenu = splitLine.prev("div.left_menu");
                if (leftMenu.is(":visible")) {
                    leftMenu.hide();
                    splitLine.addClass("menu_hide");
                    // 保存cookie
                    if (cookieName) {
                        setPCookie(cookieName, "false", "/");
                    }
                } else {
                    leftMenu.show();
                    splitLine.removeClass("menu_hide");
                    // 保存cookie
                    if (cookieName) {
                        setPCookie(cookieName, "true", "/");
                    }
                }
                var resizeGrid = $("div.j-resizeGrid", navTab.getCurrentPanel());
                if (resizeGrid.length > 0) {
                    resizeGrid.each(function () {
                        $(this).jResize();
                    });
                }
            });
            if (cookieName && "false" == $.cookie(cookieName)) {
                $("span.split_but", navTab.getCurrentPanel()).trigger("click");
            }
        },

        /**
         * 调用通用查询查询房源数据
         * @param conditionFieldArr 查询字段数组
         * @param conditionArr 查询条件数
         * @param conditionValueArr 查询值数组
         */
        queryHs: function (obj) {
            // 动态设置高级查询的宽
            var width = $("#ph005Container", navTab.getCurrentPanel()).width()
                    - $("div.searchDiv", navTab.getCurrentPanel()).width()
                    - 10;
            var container = $("#ph005AdanceSch", navTab.getCurrentPanel());
            container.width(width);
            container.show();
            // 展开高级查询
            Page.queryCondition(obj, {
                changeConditions: ph005Map.changeHsConditions,
                closeCallBack: ph005Map.closeHsConditions,
                showContainer: $("#ph005AdanceSch", navTab.getCurrentPanel()),
                formObj: $("#ph005QueryHsForm", navTab.getCurrentPanel()),
                callback: function (showContainer) {
                    showContainer.find("a.js-more").trigger("click");
                }
            });
        },

        /**
         * 关闭高级查询回调
         **/
        closeHsConditions: function () {
            var container = $("#ph005AdanceSch", navTab.getCurrentPanel());
            container.hide();
        },

        /**
         * 改变查询条件
         * @param newCondition
         */
        changeHsConditions: function (newCondition) {
            var formObj = $('#ph005QueryHsForm', navTab.getCurrentPanel());
            $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
            $("input[name=condition]", formObj).val(newCondition.conditions);
            $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
            $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
            $("input[name=resultField]", formObj).val(newCondition.resultFields);
            $("#ph005AdanceSch", navTab.getCurrentPanel()).hide();
            // 查询数据
            ph005Map.loadMapData('search');
        },

        /**
         * 调用通用查询查询房源数据
         * @param conditionFieldArr 查询字段数组
         * @param conditionArr 查询条件数
         * @param conditionValueArr 查询值数组
         */
        queryBuild: function (obj) {
            // 动态设置高级查询的宽
            var width = $("#ph005Container", navTab.getCurrentPanel()).width()
                    - $("div.searchDiv", navTab.getCurrentPanel()).width()
                    - 10;
            $("#ph005AdanceSch", navTab.getCurrentPanel()).width(width);
            // 展开高级查询
            Page.queryCondition(obj, {
                changeConditions: ph005Map.changeBuildConditions,
                showContainer: $("#ph005AdanceSch", navTab.getCurrentPanel()),
                formObj: $("#ph005QueryBuildForm", navTab.getCurrentPanel()),
                callback: function (showContainer) {
                    showContainer.find("a.js-more").trigger("click");
                }
            });
        },

        /**
         * 改变查询条件
         * @param newCondition
         */
        changeBuildConditions: function (newCondition) {
            var formObj = $('#ph005QueryBuildForm', navTab.getCurrentPanel());
            $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
            $("input[name=condition]", formObj).val(newCondition.conditions);
            $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
            $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
            $("input[name=resultField]", formObj).val(newCondition.resultFields);
            // 查询数据
            ph005Map.loadMapData('search');
        },

        /**
         * 初始加载地图数据，绘制地图。
         **/
        init: function () {
            //可配置参数从参数配置里查询
            var baselayerUrl = '${baselayerUrl}';
            var layerName1 = '${layerName1}';

            var lon = '${lon}', lat = '${lat}', level = '${level}';
            //为地图中心、缩放等级 赋值。如果有数据下面会重新计算 定位中心
            // level = 0; lon = 1017; lat = 900;
            if (!lon || !lat) {
                ${lonLatLevel}
            }

            //设置地图展示div，初始化Map
            viewMap = new OpenLayers.Map({
                div: "ph005_viewAll_map",
                //增加控制按钮
                controls: [
                    new OpenLayers.Control.PanZoomBar({
                        position: new OpenLayers.Pixel(3, 5)
                    }),
                    new OpenLayers.Control.Navigation(),
                    new OpenLayers.Control.ScaleLine()             //比例尺
                ],
                maxExtent: new OpenLayers.Bounds(${mapBounds}),     //边界：left, bottom, right, top
                maxResolution: "auto",                              //最大分辨率（maxResolution）
                projection: "EPSG:3785",
                units: "m",
                zoom: 1
            });

            //加载geoserver的两个图层，用于展示底图
            var baseLayer1 = new OpenLayers.Layer.WMS(
                    "基础区域", baselayerUrl,
                    {"layers": layerName1},
                    {
                        isBaseLayer: true
                    }
            );

            //渲染器
            var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
            renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;

            //判断展示 设置建筑图层是否展示。
            var chSearch = true;
            if (${chSearch  == 'h'}) {
                chSearch = false;
            }
            //建筑区域 图层
            buildLayer = new OpenLayers.Layer.Vector("建筑区域", {
                styleMap: new OpenLayers.StyleMap({
                    'default': {
                        strokeColor: "black",
                        strokeOpacity: .8,
                        strokeWidth: 2,
                        fillColor: "#FF5500",
                        fillOpacity: 0.5
                    }
                }),
                units: "m",
                renderers: renderer
            });
            buildLayer.setVisibility(chSearch);
            /** add */
            // 正式房样式
            var zsStyle = new OpenLayers.Style({
                        //推送颜色根据后台 维度不同推送不同map
                        fillColor: "\${colorH}",
                        fillOpacity: .7,
                        strokeOpacity: .5,
                        strokeWidth: 1,
                        label: '\${hsOwnerPersons}',
                        fontSize: "14px",
                        fontFamily: "微软雅黑",
                        labelOutlineColor: "white",
                        labelOutlineWidth: 3
                    }, {
                        rules: [
                            new OpenLayers.Rule({
                                minScaleDenominator: ${minScale},
                                symbolizer: {
                                    label: ''
                                }
                            }),
                            new OpenLayers.Rule({
                                maxScaleDenominator: ${minScale},
                                minScaleDenominator: ${maxScale},
                                symbolizer: {
                                    label: '\${hsOwnerPersons}'
                                }
                            }),
                            new OpenLayers.Rule({
                                maxScaleDenominator: ${maxScale},
                                symbolizer: {
                                    label: ''
                                }
                            })
                        ]
                    }
                    )
            // 自建房样式
            var zjStyle = new OpenLayers.Style({
                        //推送颜色根据后台 维度不同推送不同map
                        fillColor: "\${colorHs}",
                        fillOpacity: .7,
                        strokeOpacity: .5,
                        strokeWidth: 1,
                        label: '\${hsOwnerPersons}',
                        strokeDashstyle: "dashdot",   //虚线样式
                        fontSize: "14px",
                        fontFamily: "微软雅黑",
                        labelOutlineColor: "white",
                        labelOutlineWidth: 3
                    }, {
                        rules: [
                            new OpenLayers.Rule({
                                minScaleDenominator: ${minScale},
                                symbolizer: {
                                    label: ''
                                }
                            }),
                            new OpenLayers.Rule({
                                maxScaleDenominator: ${minScale},
                                minScaleDenominator: ${maxScale},
                                symbolizer: {
                                    label: '\${hsOwnerPersons}'
                                }
                            }),
                            new OpenLayers.Rule({
                                maxScaleDenominator: ${maxScale},
                                symbolizer: {
                                    label: ''
                                }
                            })
                        ]
                    }
            )

            var selectStyle = new OpenLayers.Style({
                        //推送颜色根据后台 维度不同推送不同map
                        fillColor: "blue",
                        fillOpacity: .7,
                        strokeOpacity: .5,
                        strokeColor: 'blue',
                        strokeWidth: 5,
                        label: '\${hsOwnerPersons}',
                        fontSize: "14px",
                        fontFamily: "微软雅黑",
                        labelOutlineColor: "white",
                        labelOutlineWidth: 3
                    }, {
                        rules: [
                            new OpenLayers.Rule({
                                minScaleDenominator: ${minScale},
                                symbolizer: {
                                    label: ''
                                }
                            }),
                            new OpenLayers.Rule({
                                maxScaleDenominator: ${minScale},
                                minScaleDenominator: ${maxScale},
                                symbolizer: {
                                    label: '\${hsOwnerPersons}'
                                }
                            }),
                            new OpenLayers.Rule({
                                maxScaleDenominator: ${maxScale},
                                symbolizer: {
                                    label: ''
                                }
                            })
                        ]
                    }
            )
            /** end */
            //正式房产   区域 图层
            hsFormalLayer = new OpenLayers.Layer.Vector("正式房区域", {
                styleMap: new OpenLayers.StyleMap({'default': zsStyle, 'select': selectStyle}),
                units: "m",
                renderers: renderer
            });
            hsFormalLayer.setVisibility(!chSearch);


            //自建房 图层区域
            hsSlfLayer = new OpenLayers.Layer.Vector("自建房区域", {
                styleMap: new OpenLayers.StyleMap({'default': zjStyle, 'select': selectStyle}),
                units: "m",
                renderers: renderer
            });
            hsSlfLayer.setVisibility(!chSearch);

            //摄像头图层
            camLayer = new OpenLayers.Layer.Vector("摄像头", {
                styleMap: new OpenLayers.StyleMap(
                        {
                            graphicWidth: 20,
                            graphicHeight: 30,
                            graphicYOffset: -30,
                            externalGraphic: "${pageContext.request.contextPath}/oframe/themes/images/web-cam.png"
                        }
                ),
                visibility: false,      //设置初始化后不显示摄像头图层
                units: "m",
                renderers: renderer
            });

            //注册 移动加载事件 移动结束重新加载数据
//            viewMap.events.register("moveend", viewMap, ph005Map.moveMapReloadBbox);
            //************************************************************通过选择操作模式  设置动作类型***********************************************************
            //判断地图是否处于全屏状态，全屏时不允许点击事件。
            var sltCtrlOpt = {
                onSelect: ph005Map.onFeatureSelect,
                onUnselect: ph005Map.onFeatureUnselect,
                clickout: false, toggle: false
            };
            sltCtrl = new OpenLayers.Control.SelectFeature([buildLayer, hsFormalLayer, hsSlfLayer, camLayer], sltCtrlOpt);
            ctrlModels = {
                select: sltCtrl
            };
            //动态切换 模式 增加到 map里。
            for (var key in ctrlModels) {
                viewMap.addControl(ctrlModels[key]);
            }
            //将所有图层添加到地图对象中
            viewMap.addLayers([baseLayer1, buildLayer, hsSlfLayer, hsFormalLayer, camLayer]);
            //设置地图中心及缩放等级
            viewMap.setCenter(new OpenLayers.LonLat(lon, lat), level);

            //初始化查询条件页面
            var chooseContObj = $("input[name=sltSearchObj]:first", navTab.getCurrentPanel());
            var chooseCont = $.cookie("ph005ChooseCont");
            if (chooseCont && chooseCont != "") {
                var tempObj = $("input[name='sltSearchObj'][value='" + chooseCont + "']", navTab.getCurrentPanel());
                if (tempObj.length > 0) {
                    tempObj.attr("checked", true);
                    chooseContObj = tempObj;
                }
            }
            // 初始化加载
            ph005Search.chooseSearch(chooseContObj, true);
        },

        /**
         * 移动地图重新加载
         */
        moveMapReloadBbox: function (evt) {
            ph005Map.loadMapData("load");
            OpenLayers.Event.stop(evt);
        },


        /**
         * 点击按钮切换图层, 加载所选图层数据
         **/
        changeLayer: function (obj, model) {
            var $this = $(obj);
            if (model == 'house') {
                if (!$this.hasClass("active")) {
                    hsFormalLayer.setVisibility(true);
                    hsSlfLayer.setVisibility(true);
                } else {
                    hsFormalLayer.setVisibility(false);
                    hsSlfLayer.setVisibility(false);
                }
            } else if (model == 'build') {
                if (!$this.hasClass("active")) {
                    buildLayer.setVisibility(true);
                } else {
                    buildLayer.setVisibility(false);
                }
            } else if (model == 'cam') {
                if (!$this.hasClass("active")) {
                    camLayer.setVisibility(true);
                } else {
                    camLayer.setVisibility(false);
                }
            }
            $this.toggleClass('active');
            ph005Map.loadMapData('load');
        },

        /**
         * 切换数据维度
         **/
        changeDisp: function (action) {
            var showDimension = $("input[name=showDimension]:checked", navTab.getCurrentPanel()).val();
            setPCookie("ph005ShowDimension", showDimension, "/");
            ph005Map.loadMapData(action);
        },

        /**
         * 加载地图数据
         */
        loadMapData: function (action) {
            var chooseCont = $("input[name=sltSearchObj]:checked", navTab.getCurrentPanel()).val();
            var url = getGlobalPathRoot() + "eland/ph/ph005/ph005-getDrawMapData.gv?prjCd=" + getPrjCd()
                    + "&whichOne=" + chooseCont;
            if (viewMap != null) {
                var bbox = viewMap.getExtent().toBBOX();
                if (bbox.indexOf("NaN") > 0) {
                    return;
                }
                url = url + "&bbox=" + bbox;
            } else {
                return
            }
            var ajaxPacket = new AJAXPacket(url);

            // 判断是搜索查询,还是移动地图查询, 以决定是否展示 加载遮罩层
            var isPos = false;
            if (action == 'pos') {
                isPos = true;
            } else {
                ph005Map.isPreparePos = true;
                if (action == 'load') {
                    ajaxPacket.noCover = true;
                }
            }
            if (isPos && ph005Map.isPreparePos) {
                alertMsg.info("定位稍后，加载中……")
                return;
            }
            ajaxPacket.data.data = [];
            if ("house" == chooseCont) {
                ajaxPacket.data.data = $("#ph005QueryHsForm", navTab.getCurrentPanel()).serializeArray();
            } else if ("build" == chooseCont) {
                ajaxPacket.data.data = $("#ph005QueryBuildForm", navTab.getCurrentPanel()).serializeArray();
            }
            // 获取页面选中 展示图层,以决定加载 选中图层数据
            // 如果未指定展示图层,则选择已展示图层数据进行加载; 如果指定图层,则只加载指定图层
            var showLayer;
            $('button.js_showLayer.active', navTab.getCurrentPanel()).each(function () {
                var $btn = $(this);
                var temp = $btn.attr("flag");
                if (temp) {
                    if (showLayer) {
                        showLayer = temp + "," + showLayer;
                    } else {
                        showLayer = temp;
                    }
                }
            });
            //再次判断 是否有显示图层,如果没有,不发起请求.
            if (showLayer) {
                ajaxPacket.data.data.push({
                    name: "showLayer",
                    value: showLayer
                });
            } else {
                return false;
            }

            // 地图缩放时候，带上原有 搜索条件
            var inputCondition = $("select[name=inputCondition] option:selected", navTab.getCurrentPanel()).val();
            var inputConditionValue = $("input[name=inputConditionValue]", navTab.getCurrentPanel()).val();
            ajaxPacket.data.data.push({
                name: "inputCondition",
                value: inputCondition
            });
            ajaxPacket.data.data.push({
                name: "inputConditionValue",
                value: inputConditionValue
            });
            ajaxPacket.data.data.push({
                name: "isPos",
                value: isPos,
            });

            // 获取选中维度
            var showDimension = $("input[name=showDimension]:checked", navTab.getCurrentPanel()).val();
            ajaxPacket.data.data.push({
                name: "showDimensionMark",
                value: showDimension
            });

            // 获取 选中维度中 子选项 并拼接条件
            var dimension = [];
            $("input[name=showDimension]", navTab.getCurrentPanel()).each(function () {
                var currDimension = $(this);
                var dimensionValMap = new Map();
                var dimensionValList = [];
                var currDimensionValTr = currDimension.closest("tr").nextAll("tr");
                for (var i = 0; i < currDimensionValTr.length; i++) {
                    var tempTr = $(currDimensionValTr[i]);
                    if (tempTr.hasClass("js_slt_tr")) {
                        //获取所有选中条件
                        tempTr.find("div.selected input[name='" + currDimension.val() + "']").each(function () {
                            var currDimVal = $(this).val();
                            dimensionValList.push(currDimVal);
                        });
                    } else {
                        break;
                    }
                }
                if (dimensionValList.length != 0) {
                    dimensionValMap.put("name", currDimension.val());
                    dimensionValMap.put("value", dimensionValList);
                }
                if (dimensionValMap.size() > 0) {
                    dimension.push(dimensionValMap);
                }
            });
            for (var i = 0; i < dimension.length; i++) {
                var obj = dimension[i];
                ajaxPacket.data.data.push({
                    name: obj.get("name"),
                    value: obj.get("value")
                });
            }

            // 获取 选中维度中 子选项 现有展示颜色
            var colorList = [];
            $("input[name=showDimension]:checked", navTab.getCurrentPanel()).each(function () {
                var currDimension = $(this);
                var currDimensionValTr = currDimension.closest("tr").nextAll("tr");
                for (var i = 0; i < currDimensionValTr.length; i++) {
                    var tempTr = $(currDimensionValTr[i]);
                    if (tempTr.hasClass("js_slt_tr")) {
                        //获取该维度下所有颜色
                        tempTr.find("div input[name=allInputColor]").each(function () {
                            var allColorVal = $(this).val();
                            colorList.push(allColorVal);
                        });
                    } else {
                        break;
                    }
                }
            });
            if (colorList.length > 0) {
                ajaxPacket.data.data.push({
                    name: "dimensionColorList",
                    value: colorList
                });
            }

            // 不进行错误处理
            if('init' !== action) {
                ajaxPacket.noCover = true;
            } else {
                ajaxPacket.noCover = false;
            }
            ajaxPacket.errorFunction = function (data) {
            };
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 成功之后调用 清除每个图层的旧数据, 在清除之前先清除每个图层上的弹框，避免移动之后获取不到对象无法清除。
                    var layersTemp = viewMap.layers;
                    for (var j = 0; j < layersTemp.length; j++) {
                        var tempFeatures = layersTemp[j].features;
                        if (tempFeatures != undefined && tempFeatures.length > 0) {
                            for (var i = 0; i < tempFeatures.length; i++) {
                                if (tempFeatures[i] != undefined && tempFeatures[i] != null) {
                                    var popup = tempFeatures[i].popup;
                                    if (popup != undefined && popup != null) {
                                        viewMap.removePopup(popup);
                                        popup = null;
                                    }
                                }
                            }
                        }
                    }

                    // 是地图中心点定位
                    if (isPos) {
                        // 定位到本次检索数据的第一个中心点
                        var hsResultMapList = jsonData.hsResultMapList;
                        if (hsResultMapList.length > 0) {
                            var hsTemp = hsResultMapList[0];
                            var hsId = hsTemp.hsId;
                            ph005Map.centerAndSelectHs(hsId)
                            return;
                        }
                    } else {
                        // 根据点击图层判断重新加载图层，并清除选中图层数据重新渲染。
                        if (showLayer.indexOf("house") != -1) {
                            hsFormalLayer.removeAllFeatures();
                            hsSlfLayer.removeAllFeatures();
                        }
                        if (showLayer.indexOf("build") != -1) {
                            buildLayer.removeAllFeatures();
                        }
                        if (showLayer.indexOf("cam") != -1) {
                        }
                        // 获取到数据动态加载到地图里
                        ph005Map.initEndDrawMap(jsonData.showDimensionMark, jsonData.buildResult,
                                jsonData.hsResultMapList, jsonData.oldHsColorMap, jsonData.camResultList);
                        ph005Map.isPreparePos = false;
                        if (action == 'init' && '${param.hsId != ''}') {
                            ph005Map.centerAndSelectHs('${param.hsId}');
                        }
                    }
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        },

        /**
         * 选中并定位到指定房源
         **/
        centerAndSelectHs: function (hsId) {
            // 选中对应的房屋
            sltCtrl.unselectAll();
            // 所有选中的内容
            var tempAllSltFeature = [];
            for (var i = 0; i < hsFormalLayer.features.length; i++) {
                var features = hsFormalLayer.features[i];
                if (hsId == features.attributes.clickId) {
                    sltCtrl.select(features);
                    tempAllSltFeature.push(features)
                }
            }
            for (var i = 0; i < hsSlfLayer.features.length; i++) {
                var features = hsSlfLayer.features[i];
                if (hsId == features.attributes.clickId) {
                    sltCtrl.select(features);
                    tempAllSltFeature.push(features)
                }
            }
            // 获取内容区域边界
            var bounds = new OpenLayers.Bounds();
            for (var i = 0; i < tempAllSltFeature.length; i++) {
                var tempItem = tempAllSltFeature[i].geometry.getBounds()
                if (tempItem.left) {
                    if (!bounds.left || bounds.left > tempItem.left) {
                        bounds.left = tempItem.left;
                    }
                }
                if (tempItem.right) {
                    if (!bounds.right || bounds.right < tempItem.right) {
                        bounds.right = tempItem.right;
                    }
                }
                if (tempItem.top) {
                    if (!bounds.top || bounds.top < tempItem.top) {
                        bounds.top = tempItem.top;
                    }
                }
                if (tempItem.bottom) {
                    if (!bounds.bottom || bounds.bottom > tempItem.bottom) {
                        bounds.bottom = tempItem.bottom;
                    }
                }
            }
            if (tempAllSltFeature.length > 0) {
                viewMap.zoomToExtent(bounds);
                var zoom = viewMap.getZoom();
                if(zoom > 1) {
                    viewMap.zoomTo(zoom - 1);
                }
            }
        },

        /**
         *   初始化地图结束 ，加载数据
         * @param showDimensionMark 显示维度
         * @param buildResult 建筑数据
         * @param hsResultMapList 房产数据
         * @param oldHsColorMap 数据展示颜色
         * @param camResultList 监控数据
         */
        initEndDrawMap: function (showDimensionMark, buildResult, hsResultMapList, oldHsColorMap, camResultList) {
            ph005Map.initBuildData(showDimensionMark, buildResult, oldHsColorMap);
            ph005Map.initHouseData(showDimensionMark, hsResultMapList, oldHsColorMap);
            ph005Map.initCamData(camResultList);
        },

        /**
         * 根据地图数据,渲染图形：建筑
         */
        initBuildData: function (showDimensionMark, buildResult, oldHsColorMap) {
            //处理配置的实体属性，多级的特殊情况
            if (showDimensionMark.indexOf(".") > 0) {
                showDimensionMark = showDimensionMark.substring(showDimensionMark.indexOf(".") + 1, showDimensionMark.length);
            }
            //循环取得每个院落信息  填充到 feature 自定义属性中
            for (var i = 0; i < buildResult.length; i++) {
                var buildTemp = buildResult[i];

                if (buildTemp.buildPosition == undefined || buildTemp.buildPosition == '') {
                    continue;
                }
                //切割 得到 院落的个数
                var buildNum = buildTemp.buildPosition.split("|");
                var bldNumArr = [];
                //loop 每个院落 得到 单个院落坐标集:bldNumArr
                for (var bldIdx = 0; bldIdx < buildNum.length; bldIdx++) {
                    //获取单个院落的 每个坐标x、y点  存入坐标点集:everyBldPoints
                    var everyBldPoints = [];
                    bldNumArr = buildNum[bldIdx].split(",");
                    for (var bldPtIdx = 0; bldPtIdx < bldNumArr.length; bldPtIdx++) {
                        //拿到每个点的X、Y坐标集:bldXYs
                        var bldXYs = bldNumArr[bldPtIdx].replace('POINT(', '').replace(')', '').split(' ');
                        everyBldPoints.push(new OpenLayers.Geometry.Point(bldXYs[0], bldXYs[1]));
                    }
                    //为每一个院落everyBldFeature 设置自定义属性，自定义样式，并添加到院落图层上。
                    var everyBldFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(everyBldPoints)));
                    everyBldFeature.attributes = {
                        clickId: buildTemp.buildId,
                        clickType: "build",
                        buildNo: buildTemp.buildNo,
                        buildStatus: buildTemp.buildStatus,
                        buildAddr: buildTemp.buildAddr,
                        ttRegId: buildTemp.ttRegId
                    };
                    var colorT = oldHsColorMap[buildTemp[showDimensionMark]];
                    if (colorT == undefined) {
                        colorT = "grey";
                    }
                    everyBldFeature.style = {
                        fillColor: colorT,
                        fillOpacity: .7,
                        strokeColor: "black",
                        strokeOpacity: 1,
                        strokeWidth: 1,
                        label: everyBldFeature.attributes.buildNo,
                        fontSize: "14px",
                        fontFamily: "微软雅黑",
                        labelOutlineColor: "white",
                        labelOutlineWidth: 3
                    };
                    if (viewMap.getScale() > '1100') {
                        everyBldFeature.style.fillOpacity = .7;
                    }
                    buildLayer.addFeatures([everyBldFeature]);
                }
            }
        },

        /**
         * 根据地图数据,渲染图形：房产
         */
        initHouseData: function (showDimensionMark, hsResultMapList, oldHsColorMap) {
            //处理配置的实体属性，多级的特殊情况
            if (showDimensionMark.indexOf(".") > 0) {
                showDimensionMark = showDimensionMark.substring(showDimensionMark.indexOf(".") + 1, showDimensionMark.length);
            }
            //循环拿到每个房子的信息，填充到feature中，并区分开正式住房、自建房
            for (var i = 0; i < hsResultMapList.length; i++) {
                //loop 得到单个房产
                var hsTemp = hsResultMapList[i];
                //判断是否有地图
                if (hsTemp.hsRoomPos != undefined || hsTemp.hsRoomPos != '') {
                    //每个房产 正式住房数量
                    var hsFormalNum = hsTemp.hsRoomPos.split("|");
                    var hsFormalNumArr = [];
                    //正式住房 绘制地图
                    for (var hsForIdx = 0; hsForIdx < hsFormalNum.length; hsForIdx++) {
                        //获取单个正式住房的 每个坐标x、y点  存入坐标点集:everyHsFormalPoints
                        var everyHsFormalPoints = [];
                        hsFormalNumArr = hsFormalNum[hsForIdx].split(",");
                        for (var hsFormalIdx = 0; hsFormalIdx < hsFormalNumArr.length; hsFormalIdx++) {
                            //拿到每个点的X、Y坐标集:hsFormalXYs
                            var hsFormalXYs = hsFormalNumArr[hsFormalIdx].replace('POINT(', '').replace(')', '').split(' ');
                            everyHsFormalPoints.push(new OpenLayers.Geometry.Point(hsFormalXYs[0], hsFormalXYs[1]));
                        }

                        //为每一个院落everyHsFormalFeature 设置自定义属性，自定义样式，并添加到院落图层上。
                        var everyHsFormalFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(everyHsFormalPoints)));
                        var colorH = oldHsColorMap[hsTemp[showDimensionMark]];
                        if (colorH == undefined) {
                            colorH = "grey";
                        }
                        everyHsFormalFeature.attributes = {
                            clickId: hsTemp.hsId,
                            clickType: "house",
                            hsFullAddr: hsTemp.hsFullAddr,
                            hsStatus: hsTemp.hsStatus,
                            hsOwnerPersons: hsTemp.hsOwnerPersons,
                            colorH: colorH
                        };
                        hsFormalLayer.addFeatures([everyHsFormalFeature]);
                    }
                }
                //判断是否有自建房地图
                if (hsTemp.hsSlfRoomPos != undefined || hsTemp.hsSlfRoomPos != '') {
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
                            everyHsSlfPoints.push(new OpenLayers.Geometry.Point(hsSlfXYs[0], hsSlfXYs[1]));
                        }

                        //为每一个院落everyHsFormalFeature 设置自定义属性，自定义样式，并添加到院落图层上。
                        var everyHsSlfFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon(new OpenLayers.Geometry.LinearRing(everyHsSlfPoints)));
                        var colorHs = oldHsColorMap[hsTemp[showDimensionMark]];
                        if (colorHs == undefined) {
                            colorHs = "grey";
                        }
                        everyHsSlfFeature.attributes = {
                            clickId: hsTemp.hsId,
                            clickType: "house",
                            hsFullAddr: hsTemp.hsFullAddr,
                            hsStatus: hsTemp.hsStatus,
                            hsOwnerPersons: hsTemp.hsOwnerPersons,
                            colorHs: colorHs
                        };
                        hsSlfLayer.addFeatures([everyHsSlfFeature]);
                    }
                }
            }
            $(".initScale", navTab.getCurrentPanel()).html(viewMap.getScale());
        },

        /**
         * 根据地图数据,渲染图形：监控
         */
        initCamData: function (camResultList) {
            //获取摄像头信息，分布图层
            for (var i = 0; i < camResultList.length; i++) {
                //loop get every cameraInfo。
                var camInfo = camResultList[i];
                if (camInfo.markPos == undefined || camInfo.markPos == '') {

                } else {
                    var lonlat = camInfo.markPos.split(",");
                    //设置摄像头图层属性
                    var marker = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(lonlat[0], lonlat[1]));
                    marker.attributes = {
                        clickId: camInfo.camId,
                        clickType: "camera"
                    };
                    camLayer.addFeatures(marker);
                }
            }
        },


        /**
         * 点击选中弹框提示
         */
        onFeatureSelect: function (feature) {
            sltFeature = feature;
            var hsId = feature.attributes.clickId;
            // 处理同时选中多个的时候重复加载的问题
            if (ph005Map.featureSelectHsId == hsId) {
                return;
            } else {
                ph005Map.featureSelectHsId = hsId;
            }
            // 所有选中的内容
            for (var i = 0; i < hsFormalLayer.features.length; i++) {
                var features = hsFormalLayer.features[i];
                if (hsId == features.attributes.clickId) {
                    sltCtrl.select(features);
                }
            }
            for (var i = 0; i < hsSlfLayer.features.length; i++) {
                var features = hsSlfLayer.features[i];
                if (hsId == features.attributes.clickId) {
                    sltCtrl.select(features);
                }
            }
            var clickType = feature.attributes.clickType;
            var clickNodeInfo = $("#clickNodeInfo", navTab.getCurrentPanel());
            //异步 查询数据 ， 拼接弹出页面
            var url = getGlobalPathRoot() + "eland/ph/ph005/ph005-findPopupData.gv?prjCd=" + getPrjCd()
                    + "&clickId=" + feature.attributes.clickId + "&clickType=" + feature.attributes.clickType;
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.noCover = true;
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                ph005Map.featureSelectHsId = null;
//             弹框页面，异步请求页面
                var nodeInfo = $("#nodeInfo", clickNodeInfo);
                nodeInfo.html(response);
                initUI(nodeInfo);
                clickNodeInfo.show();
            });
        },

        /**
         * 隐藏节点详情
         **/
        hideNodeInfo: function () {
            var clickNodeInfo = $("#clickNodeInfo", navTab.getCurrentPanel());
            clickNodeInfo.hide();
        },

        /**
         * 取消选中弹框
         */
        onFeatureUnselect: function (feature) {
            // 选中对应的房屋
            sltCtrl.unselectAll();
            var clickNodeInfo = $("#clickNodeInfo", navTab.getCurrentPanel());
            clickNodeInfo.hide();
        },

        /**
         * 关闭弹框
         */
        onPopupClose: function (evt) {
            sltCtrl.unselect(sltFeature);
        },

        /**
         * 切换导航点击模式
         **/
        switchToggleCtrl: function (clickObject) {
            var $this = $(clickObject);
            if ($this.hasClass("un_slt_move_icon")) {
                $this.removeClass("un_slt_move_icon");
                $this.addClass("slt_move_icon");
                $this.attr("title", "选择模式");
                ph005Map.toggleCtrl("select");
            } else {
                $this.addClass("un_slt_move_icon");
                $this.removeClass("slt_move_icon");
                $this.attr("title", "移动模式");
                ph005Map.toggleCtrl("none")
            }
        },

        /**
         * 切换点击模式
         **/
        toggleCtrl: function (clickModel) {
            for (var key in ctrlModels) {
                var control = ctrlModels[key];
                if (clickModel == key) {
                    control.activate();
                } else {
                    control.deactivate();
                }
            }
        },

        /**
         * 点击切换全屏
         */
        fullScreen: function (obj) {
            var url = getGlobalPathRoot() + "eland/ph/ph005/ph005-screenFull.gv?prjCd=" + getPrjCd();
            window.open(url);
        }
    };

    /**
     *  条件搜索
     */
    var ph005Search = {
        /**
         * 重置搜索form
         */
        reset: function () {
            $('.form')[0].reset();
            $("div.selected", navTab.getCurrentPanel()).each(function () {
                $(this).removeClass("selected");
                $(this).find("span").css("color", "#000000");
            });
        },
        /**
         * 显示和隐藏左侧 检索条件 列表
         */
        hideList: function () {
            var left = $(".left_menu", navTab.getCurrentPanel());
            // 动态设置高级查询的宽
            var totalWidth = $("#ph005Container", navTab.getCurrentPanel()).width();
            var leftWidth = left.width();
            if (left.is(":visible")) {
                left.hide();
                $(".arrow button").css({
                    'margin-left': '1px'
                });
                $(".arrow span").text(">");
                $("#ph005AdanceSch", navTab.getCurrentPanel()).width(totalWidth - 10);
            } else {
                left.show();
                $(".arrow button").css({
                    'margin-left': '400px'
                });
                $(".arrow span").text("<");
                $("input[name=inputConditionValue]", navTab.getCurrentPanel()).focus();
                $("#ph005AdanceSch", navTab.getCurrentPanel()).width(totalWidth - leftWidth - 10);
            }
        },

        changeColor: function () {
            ph005Map.loadMapData('search');
        },

        /**
         * 点击查询条件。改变样式
         */
        checkCondition: function (obj) {
            var $this = $(obj);
            if ($this.hasClass("selected")) {
                $this.removeClass('selected');
                $this.find("span").css("color", "#000000");
            } else {
                $this.addClass('selected');
                $this.find("span").css("color", "#ffffff");
            }
            ph005Map.loadMapData('search');
        },
        /**
         * 点击展示不同查询条件页面
         */
        chooseSearch: function (obj, isInit) {
            var chooseCont = $(obj).val();
            setPCookie("ph005ChooseCont", chooseCont, "/");
            var ph005SearchDiv = $("div.js_ph005_search", navTab.getCurrentPanel());
            var privilegeId = '${privilegeId}';
            var url = getGlobalPathRoot() + "eland/ph/ph005/ph005-searchPage.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("whichOne", chooseCont);
            packet.data.add("hsId", '${param.hsId}');
            packet.data.add("privilegeId", privilegeId);
            core.ajax.sendPacketHtml(packet, function (response) {
                ph005SearchDiv.html(response);
                initUI(ph005SearchDiv);
                var showDimension = $.cookie("ph005ShowDimension");
                if (showDimension && showDimension != "") {
                    var tempObj = $("input[name='showDimension'][value='" + showDimension + "']", navTab.getCurrentPanel());
                    if (tempObj.length > 0) {
                        tempObj.attr("checked", true);
                    }
                }
                if (isInit) {
                    ph005Map.loadMapData('init');
                } else {
                    ph005Map.loadMapData('search');
                }

            }, true);
        }
    };

    /**
     * 弹框里 详情链接
     */
    var ph005 = {
        /**
         * 打开建筑信息页面
         **/
        viewBuilding: function (buildId) {
            var url = "eland/pb/pb002/pb002-init.gv?prjCd=" + getPrjCd() + "&buldId=" + buildId;
            index.quickOpen(url, {opName: "院落详情", opCode: "pb002", fresh: true});
        },
        /**
         * 查看居民信息
         **/
        viewHouse: function (hsId) {
            var url = "eland/ph/ph003/ph003-init.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
            index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情"});
        },
        /**
         * 打开查看监控信息
         **/
        openCam: function (camId, camAddr) {
            var url = getGlobalPathRoot() + "eland/ph/ph010/ph010-view.gv?prjCd=" + getPrjCd() + "&camId=" + camId;
            $.pdialog.open(url, "ph010-view", "摄像头:" + camAddr, {
                mask: true, max: false, height: 600, width: 800, resizable: false, maxable: false
            });
        }
    };

    $(document).ready(function () {
        // 拖动屏幕切分效果
        ph005Map.initLeft("ph005LeftDiv", {cookieName: "showMapLeft"});
        /**
         * 延迟初始化地图渲染
         */
        window.setTimeout(function () {
            ph005Map.init();
            ph005Map.toggleCtrl('select');
        }, 50);
    })
</script>