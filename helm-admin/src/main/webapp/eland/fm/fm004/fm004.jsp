<%--打开 编辑 房产地图页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/fm/fm004/js/fm004.js"/>
<style>
    #fm004Container .phBtn {
        width: 60px;
        height: 40px;
        position: absolute;
        z-index: 1000;
        right: 10px;
        top: 15px;
        cursor: pointer;
    }

    #fm004Container .phBtn button {
        border-radius: 10px;
        display: inherit;
        width: 60px;
        height: 40px;
        background-color: #67c0dc;
        cursor: pointer;
    }

    #fm004Container .phBtn button:hover {
        background-color: #3C90C8;
    }

    #fm004Container .phBtn button label {
        color: #ffffff;
        cursor: pointer;
    }

    #fm004Container .phBtn button.active {
        background-color: #3078a4;
    }

    #fm004Container .colorCheck {
        text-align: left;
        line-height: 30px;
        height: 30px;
        word-break: keep-all;
        overflow: hidden;
        cursor: pointer;
        margin: 2px;
        background-color: #e7f2f8;
    }

    #fm004Container .colorCheck:hover {
        color: #ffffff;
        background-color: #aabcdc;
    }

    #fm004Container .colorCheck div {
        display: inline-block;
        margin-left: 5px;
    }

    #fm004Container div.arrow button.contraction {
        width: 15px;
        height: 130px;
        position: absolute;
        z-index: 100;
        background: #3C90C8;
        margin-left: 1px;
        margin-top: 18%;
        left: 4px;
    }

    #fm004Container div.arrow button.extend {
        width: 15px;
        height: 130px;
        position: absolute;
        z-index: 100;
        background: #3C90C8;
        margin-left: 1px;
        margin-top: 18%;
    }

    #fm004Container div.selected {
        color: #000 !important;
        background-color: #3c9fd5 !important;
    }

    #fm004Container span.fullScreen {
        position: absolute;
        top: 15px !important;
        left: 100px !important;
        z-index: 10;
    }

    #fm004Container span.sltMoveIcon {
        position: absolute;
        top: 15px;
        left: 60px;
        z-index: 10;
    }

    #fm004Container div.searchDiv {
        width: 400px;
        z-index: 100;
        position: absolute;
        background-color: white;
    }

    #contentHead {
        height: 40px;
    }

    #contentBody {
        clear: both;
        height: 209px;
    }

    #contentBody h1 {
        color: #0000cc;
        margin-top: 5px;
    }

    #fm004Assess {
        position: absolute;
        bottom: 0px;
        left: 0px;
        width: 100%;
        height: 250px;
    }

    #fm004Assess label.removeX {
        cursor: pointer;
        color: #fb7226;
        background: url("${pageContext.request.contextPath}/oframe/themes/blue/images/duibi/del.png") no-repeat;
        width: 20px;
        display: inline-block;
        background-size: 15px;
        margin-left: 10px;
        position: absolute;
        height: 20px;
        top: 2px;
        right: -5px;
    }

    #fm004Assess .newadd {
        cursor: pointer;
        color: #fb7226;
        background: url("${pageContext.request.contextPath}/oframe/themes/blue/images/duibi/add.png") no-repeat;
        width: 20px;
        display: inline-block;
        background-size: 20px;
        margin-left: 3px;
        position: absolute;
        height: 20px;
        top: 5px;
        left: 0px;
    }

    #fm004Assess .duibi {
        cursor: pointer;
        color: #fb7226;
        background: url("${pageContext.request.contextPath}/oframe/themes/blue/images/duibi/duibi.png") no-repeat;
        width: 20px;
        display: inline-block;
        background-size: 20px;
        margin-left: 3px;
        position: absolute;
        height: 20px;
        top: 5px;
        left: 0px;
    }

    #contentHead .add {
        padding: 4px 10px;
        margin-top: 10px;
        text-decoration: none;
    }

    #contentHead > span {
        padding: 9px 10px;
        background-color: #ebebeb;
        border: 1px solid grey;
        display: inline-block;
        float: left;
        color: black;
        cursor: pointer;
        text-align: center;
        position: relative;
        min-width: 62px;
        min-height: 21px;
    }

    #contentHead > span.active {
        background-color: #C5C1C1;
        border: 1px solid #608ab6;
        color: blue;
    }

    #contentHsId {
        float: left;
        border: 1px solid black;
        width: 30%;
        box-sizing: border-box;
        background-color: white;
    }

    #contentHsIdTitle {
        line-height: 30px;
        padding-left: 20px;
    }

    #contentScheme {
        float: left;
        border: 1px solid black;
        width: 70%;
        box-sizing: border-box;
        line-height: 30px;
        background-color: white;
    }

    #contentSchemeTitle {
        line-height: 30px;
    }

    #contentSchemeTitle input {
        margin-left: 10px;
    }

    #hsIdDiv {
        border-top: 1px solid black;
        height: 169px;
    }

    #hsIdDiv > span {
        border: 1px solid #b7b7b7;
        padding: 7px;
        padding-right: 20px;
        margin-left: 5px;
        margin-top: 5px;
        display: inline-block;
        position: relative;
    }

    #schemeDiv {
        border-top: 1px solid black;
        height: 169px;
    }

    #schemeDiv > span {
        border: 1px solid #b7b7b7;
        padding: 4px 10px;
        margin-left: 5px;
        margin-top: 10px;
        display: inline-block;
    }

    #fm004Assess .grid .gridTbody .selected {
        background-color: aliceblue !important;
    }

</style>

<%--房产详细地图--%>
<div id="fm004Container" layoutH="10" class="mar5">
    <%--地图左侧搜索框--%>
    <div layoutH="16" class="panel left_menu hidden map-left searchDiv">
        <h1>
            <span class="panel_title">地图检索</span>
            <span>
                <c:set var="chSearch" value="h"/>
                <oframe:power prjCd="${param.prjCd}" rhtCd="show_build_rht">
                    <input type="radio" name="sltSearchObj" value="house" id="sltSearchObjHs"
                           onchange="fm004Search.chooseSearch(this);" checked/><label for="sltSearchObjHs">房产信息</label>
                    <input type="radio" name="sltSearchObj" value="build" id="sltSearchObjBd"
                           onchange="fm004Search.chooseSearch(this);"/><label for="sltSearchObjBd">院落信息</label>
                    <c:set var="chSearch" value="b"/>
                </oframe:power>
                <c:if test="${chSearch == 'h'}">
                    <input type="radio" name="sltSearchObj" value="house" id="sltSearchObjHs"
                           onchange="fm004Search.chooseSearch(this);" checked/><label for="sltSearchObjHs">房产信息</label>
                </c:if>
            </span>
            <span class="right marr10" style="cursor: pointer;" value="" onclick="fm004Search.reset();">重置</span>
        </h1>
        <%--展示查询条件区域--%>
        <div layoutH="56" class="js_fm004_search"></div>
    </div>
    <div class="arrow">
        <button class="contraction" type="button" onclick="fm004Search.hideList();">
            <span style="color: #ffffff;margin-left: -5px">></span>
        </button>
    </div>
    <div id="fm004AdanceSch" layoutH="56" style="position: absolute;right:5px;top: -35px;"></div>

    <%--地图右侧 展示 图层按钮--%>
    <div id="fm004_viewAll_map"
         style="clear: both;height: 99%; border: 1px #000000 solid;position: relative;overflow: hidden;">
        <div class="phBtn">
            <c:if test="${chSearch == 'h'}">
                <oframe:power prjCd="${param.prjCd}" rhtCd="show_build_rht">
                    <button type="button" class="mart10 js_showLayer build"
                            onclick="fm004Map.changeLayer(this, 'build');"
                            flag="build">
                        <label>院落</label></button>
                </oframe:power>
                <button type="button" class="mart10 js_showLayer house active"
                        onclick="fm004Map.changeLayer(this, 'house');"
                        flag="house">
                    <label>房产</label></button>
            </c:if>
            <c:if test="${chSearch == 'b'}">
                <oframe:power prjCd="${param.prjCd}" rhtCd="show_build_rht">
                    <button type="button" class="mart10 js_showLayer build" flag="build"
                            onclick="fm004Map.changeLayer(this, 'build');"><label>院落</label>
                    </button>
                </oframe:power>
                <button type="button" class="mart10 js_showLayer house active"
                        onclick="fm004Map.changeLayer(this, 'house');"
                        flag="house">
                    <label>房产</label></button>
            </c:if>
        </div>
    </div>
</div>
<div id="fm004Assess">
    <div id="contentHead" class="marl5 marr5">
        <span name="headTemp" style="display: none"
              onclick="fm004.queryResult(this);">
            <input type="hidden" name="hsId" value=""/>
            <input type="hidden" name="buildId" value=""/>
            <input type="hidden" name="ctType" value=""/>
            <input type="hidden" name="ctName" value=""/>
            <input type="hidden" name="schemeName" value=""/>
            <input type="hidden" name="planName" value=""/>
            <span class="sName" onfocusout="fm004.changeName(this);" contenteditable="true">方案</span>
            <label class="removeX" onclick="fm004.removeScheme(this);"></label>
        </span>
        <c:forEach var="item" items="${initResult}">
            <span onclick="fm004.queryResult(this);">
                <input type="hidden" name="hsId" value="${item.value.hsId}"/>
                <input type="hidden" name="buildId" value="${item.value.buildId}"/>
                <input type="hidden" name="ctType" value="${item.value.ctType}"/>
                <input type="hidden" name="ctName" value="${item.value.ctName}"/>
                <input type="hidden" name="schemeName" value="${item.key}"/>
                <input type="hidden" name="planName" value="${item.value.planName}"/>
                <span class="sName" onfocusout="fm004.changeName(this);"
                      contenteditable="true">${item.value.planName}</span>
                <label class="removeX" onclick="fm004.removeScheme(this)"></label>
            </span>
        </c:forEach>
        <span onclick="fm004.addScheme(this);" class="add">
            <a class="add" href="javascript:void(0);"><label class="newadd"></label>新增</a>
        </span>
        <span onclick="fm004.openMatch();" class="add">
            <a class="add" href="javascript:void(0);"><label class="duibi"></label>对比</a>
        </span>
    </div>
    <div id="contentBody" class="marl5 marr5">
        <div id="contentHsId">
            <div id="contentHsIdTitle">已选居民</div>
            <div id="hsIdDiv">
                <span name="hsIdTemp" style="display: none">
                    <input type="hidden" name="hsId" value=""/>
                    <span></span>
                    <label class="removeX" onclick="fm004.removeHsId(this)"></label>
                </span>
            </div>
        </div>
        <div id="contentScheme">
            <div id="contentSchemeTitle">
                <span class="marl10">成本计算模型  </span>
                <oframe:select name="ctType"
                               prjCd="${param.prjCd}"
                               withEmpty="true"
                               cssClass="marl5"
                               id="fm004CtType"
                               type="radio"
                               itemCd="10001"
                               onChange="fm004.changeCtType(this);"/>

            </div>
            <div id="schemeDiv">
                <span name="schemeTemp" style="display: none"></span>
            </div>
        </div>
    </div>
</div>

<script>
    /**
     * 定义地图对象，图层，控制模式
     */
    var viewMap, buildLayer, hsFormalLayer, hsSlfLayer, camLayer;
    var sltCtrl, sltFeature;

    /**
     * 绘制地图
     */
    var fm004Map = {
        /**
         * 调用通用查询查询房源数据
         * @param conditionFieldArr 查询字段数组
         * @param conditionArr 查询条件数
         * @param conditionValueArr 查询值数组
         */
        queryHs: function (obj) {
            // 动态设置高级查询的宽
            var width = $("#fm004Container", navTab.getCurrentPanel()).width()
                    - $("div.searchDiv", navTab.getCurrentPanel()).width()
                    - 10;
            $("#fm004AdanceSch", navTab.getCurrentPanel()).width(width);
            // 展开高级查询
            Page.queryCondition(obj, {
                changeConditions: fm004Map.changeHsConditions,
                showContainer: $("#fm004AdanceSch", navTab.getCurrentPanel()),
                formObj: $("#fm004QueryHsForm", navTab.getCurrentPanel()),
                callback: function (showContainer) {
                    showContainer.find("a.js-more").trigger("click");
                }
            });
        },

        /**
         * 改变查询条件
         * @param newCondition
         */
        changeHsConditions: function (newCondition) {
            var formObj = $('#fm004QueryHsForm', navTab.getCurrentPanel());
            $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
            $("input[name=condition]", formObj).val(newCondition.conditions);
            $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
            $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
            $("input[name=resultField]", formObj).val(newCondition.resultFields);
            // 查询数据
            fm004Map.loadMapData('search');
        },

        /**
         * 调用通用查询查询房源数据
         * @param conditionFieldArr 查询字段数组
         * @param conditionArr 查询条件数
         * @param conditionValueArr 查询值数组
         */
        queryBuild: function (obj) {
            // 动态设置高级查询的宽
            var width = $("#fm004Container", navTab.getCurrentPanel()).width()
                    - $("div.searchDiv", navTab.getCurrentPanel()).width()
                    - 10;
            $("#fm004AdanceSch", navTab.getCurrentPanel()).width(width);
            // 展开高级查询
            Page.queryCondition(obj, {
                changeConditions: fm004Map.changeBuildConditions,
                showContainer: $("#fm004AdanceSch", navTab.getCurrentPanel()),
                formObj: $("#fm004QueryBuildForm", navTab.getCurrentPanel()),
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
            var formObj = $('#fm004QueryBuildForm', navTab.getCurrentPanel());
            $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
            $("input[name=condition]", formObj).val(newCondition.conditions);
            $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
            $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
            $("input[name=resultField]", formObj).val(newCondition.resultFields);
            // 查询数据
            fm004Map.loadMapData('search');
        },
        /**
         * 初始加载地图数据，绘制地图。
         **/
        init: function () {
            //可配置参数从参数配置里查询
            var baselayerUrl = '${baselayerUrl}';
            var layerName1 = '${layerName1}';

            var lon = '${lon}', lat = '${lat}', level = '${level}';
            //为地图中心、缩放等级 赋值。如果有数据下面会重新计算 定位中心  // level = 0; lon = 1017; lat = 900;
            if (!lon || !lat) {
                ${lonLatLevel}
            }
            //设置地图展示div，初始化Map
            viewMap = new OpenLayers.Map({
                div: "fm004_viewAll_map",
                //增加控制按钮
                controls: [
                    new OpenLayers.Control.PanZoomBar({
                        position: new OpenLayers.Pixel(3, 5)
                    }),
                    new OpenLayers.Control.Navigation(),
//                    new OpenLayers.Control.LayerSwitcher(),      //图层选择工具
////                    new OpenLayers.Control.MousePosition(),      //显示鼠标位置
//                    new OpenLayers.Control.OverviewMap(),         //鹰眼图
                    new OpenLayers.Control.ScaleLine()             //比例尺
                ],
                maxExtent: new OpenLayers.Bounds(${mapBounds}),     //边界：left, bottom, right, top
                maxResolution: "auto",                              //最大分辨率（maxResolution）
//                numZoomLevels: 6,                                 //控制地图最大缩放等级
                projection: "EPSG:3785",
                units: "m"
            });

            //加载geoserver的两个图层，用于展示底图
            var baseLayer1 = new OpenLayers.Layer.WMS(
                    "基础区域", baselayerUrl,
                    {"layers": layerName1},
                    {
                        isBaseLayer: true
//                        opacity: .5
                    }
            );
            //加载geoserver的两个图层，用于展示底图
//            var layer_2 = new OpenLayers.Layer.WMS(
//                    "房屋图层", baselayerUrl,
//                    {"layers": layerName2, transparent: true},
//                    {
//                        displayInLayerSwitcher: false,
//                        opacity: .5,
//                        minScale: 2500,
//                        units: "m",
//                        transitionEffect: "resize"
//                    }
//            );

            //渲染器
            var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
            renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;

            //判断展示 设置建筑图层是否展示。
            var chSearch = true;
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
//                maxScale: 2000,  //设定多少分辨率下显示图层
                units: "m",
                renderers: renderer
            });
            buildLayer.setVisibility(!chSearch);

            //正式房产   区域 图层
            hsFormalLayer = new OpenLayers.Layer.Vector("正式房区域", {
                styleMap: new OpenLayers.StyleMap({
                    'default': {
                        fillColor: "#FF5500",
                        fillOpacity: 0.5
                    }
                }),
//                minScale: 2500,  //设定多少分辨率下显示图层
                units: "m",
                renderers: renderer
            });
            hsFormalLayer.setVisibility(chSearch);

            //自建房 图层区域
            hsSlfLayer = new OpenLayers.Layer.Vector("自建房区域", {
                styleMap: new OpenLayers.StyleMap({
                    'default': {
                        fillColor: "#FF5500",
                        fillOpacity: 0.5
                    }
                }),
//                minScale: 2500,  //设定多少分辨率下显示图层
                units: "m",
                renderers: renderer
            });
            hsSlfLayer.setVisibility(chSearch);

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
            viewMap.events.register("moveend", viewMap, fm004Map.moveMapReloadBbox);

//************************************************************通过选择操作模式  设置动作类型***********************************************************
            //添加选中事件
            var sltCtrlOpt = {
                onSelect: fm004Map.onFeatureSelect,
                onUnselect: fm004Map.onFeatureUnselect,
                selectStyle: {
                    fillColor: "blue",
                    fillOpacity: .2,
                    strokeColor: "blue",
                    strokeOpacity: .2,
                    strokeWidth: 2,
                    label: '',
                    fontSize: "14px",
                    fontFamily: "微软雅黑",
                    labelOutlineColor: "white",
                    labelOutlineWidth: 3
                },
                clickout: false, toggle: true,
                multiple: true, hover: false,
                toggleKey: "ctrlKey", // ctrl key removes from selection
                multipleKey: "shiftKey" // shift key adds to selection
            };

            sltCtrl = new OpenLayers.Control.SelectFeature([buildLayer, hsFormalLayer, hsSlfLayer, camLayer], sltCtrlOpt);

            // 自定义控制增加到 地图对象上。
            viewMap.addControl(sltCtrl);
            //将所有图层添加到地图对象中
            viewMap.addLayers([baseLayer1, buildLayer, hsSlfLayer, hsFormalLayer, camLayer]);
            //设置地图中心及缩放等级
            viewMap.setCenter(new OpenLayers.LonLat(lon, lat), 3);
        },

        /**
         * 移动地图重新加载
         */
        moveMapReloadBbox: function (evt) {
//            alert("toBBOX:" + viewMap.getExtent().toBBOX() + "\nWIDTH:" + viewMap.size.w + "\nHEIGHT:" + viewMap.size.h);
            fm004Map.loadMapData("load");
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
            fm004Map.loadMapData('load');
        },

        /**
         * 加载地图数据
         */
        loadMapData: function (action) {
            var chooseCont = $("input[name=sltSearchObj]:checked", navTab.getCurrentPanel()).val();
            var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-getDrawMapData.gv?prjCd=" + getPrjCd() + "&whichOne=" + chooseCont;
            if (viewMap != null) {
                url = url + "&bbox=" + viewMap.getExtent().toBBOX();
            }
            var ajaxPacket = new AJAXPacket(url);

            // 判断是搜索查询,还是移动地图查询, 以决定是否展示 加载遮罩层
            if (action == 'load') {
                ajaxPacket.noCover = true;
            }

            ajaxPacket.data.data = [];
            if ("house" == chooseCont) {
                ajaxPacket.data.data = $("#fm004QueryHsForm", navTab.getCurrentPanel()).serializeArray();
            } else if ("build" == chooseCont) {
                ajaxPacket.data.data = $("#fm004QueryBuildForm", navTab.getCurrentPanel()).serializeArray();
            }
            /**
             * 获取页面选中 展示图层,以决定加载 选中图层数据
             */
            //如果未指定展示图层,则选择已展示图层数据进行加载; 如果指定图层,则只加载指定图层
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
                    fm004Map.initEndDrawMap(jsonData.showDimensionMark, jsonData.buildResult, jsonData.hsResultMapList, jsonData.oldHsColorMap, jsonData.camResultList);

                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
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
            fm004Map.initBuildData(showDimensionMark, buildResult, oldHsColorMap);
            fm004Map.initHouseData(showDimensionMark, hsResultMapList, oldHsColorMap);
            fm004Map.initCamData(camResultList);
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
                    //根据方案中的builId控制选中效果
                    var selectScheme = $("span.active", navTab.getCurrentPanel());
                    var flag = false;
                    if (selectScheme.length > 0) {
                        var schemeBuildId = $("input[name=hsId]", selectScheme);
                        var schemeBuildIdArr = schemeBuildId.val().split(",");
                        for (var t = 0; t < schemeBuildIdArr.length; t++) {
                            if (buildTemp.buildId == schemeBuildIdArr[t]) {
                                flag = true;
                                break;
                            }
                        }
                    }
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
                    if (!flag) {
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
                    } else {
                        everyBldFeature.style = {
                            fillColor: "blue",
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
                    }
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
                        var selectScheme = $("span.active", navTab.getCurrentPanel());
                        var flag = false;
                        if (selectScheme.length > 0) {
                            var schemeHsId = $("input[name=hsId]", selectScheme);
                            var schemeHsIdArr = schemeHsId.val().split(",");
                            for (var t = 0; t < schemeHsIdArr.length; t++) {
                                if (hsTemp.hsId == schemeHsIdArr[t]) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
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
                        everyHsFormalFeature.attributes = {
                            clickId: hsTemp.hsId,
                            clickType: "house",
                            hsFullAddr: hsTemp.hsFullAddr,
                            hsStatus: hsTemp.hsStatus,
                            hsOwnerPersons: hsTemp.hsOwnerPersons
                        };
                        var colorH = oldHsColorMap[hsTemp[showDimensionMark]];
                        if (colorH == undefined) {
                            colorH = "grey";
                        }
                        if (!flag) {
                            everyHsFormalFeature.style = {
                                //推送颜色根据后台 维度不同推送不同map
                                fillColor: colorH,
                                fillOpacity: .7,
                                strokeColor: "black",
                                strokeOpacity: .5,
                                strokeWidth: 1,
                                label: '',
                                fontSize: "14px",
                                fontFamily: "微软雅黑",
                                labelOutlineColor: "white",
                                labelOutlineWidth: 3
                            };
                        } else {
                            everyHsFormalFeature.style = {
                                //推送颜色根据后台 维度不同推送不同map
                                fillColor: "blue",
                                fillOpacity: .7,
                                strokeColor: "black",
                                strokeOpacity: .5,
                                strokeWidth: 1,
                                label: '',
                                fontSize: "14px",
                                fontFamily: "微软雅黑",
                                labelOutlineColor: "white",
                                labelOutlineWidth: 3
                            };
                        }
                        if (viewMap.getScale() < '250') {
                            everyHsFormalFeature.style.label = everyHsFormalFeature.attributes.hsOwnerPersons;
                        }

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
                        var selectScheme = $("span.active", navTab.getCurrentPanel());
                        var flag = false;
                        if (selectScheme.length > 0) {
                            var schemeHsId = $("input[name=hsId]", selectScheme);
                            var schemeHsIdArr = schemeHsId.val().split(",");
                            for (var t = 0; t < schemeHsIdArr.length; t++) {
                                if (hsTemp.hsId == schemeHsIdArr[t]) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
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
                        everyHsSlfFeature.attributes = {
                            clickId: hsTemp.hsId,
                            clickType: "house",
                            hsFullAddr: hsTemp.hsFullAddr,
                            hsStatus: hsTemp.hsStatus,
                            hsOwnerPersons: hsTemp.hsOwnerPersons
                        };
                        var colorHs = oldHsColorMap[hsTemp[showDimensionMark]];
                        if (colorHs == undefined) {
                            colorHs = "grey";
                        }
                        if (!flag) {
                            everyHsSlfFeature.style = {
                                fillColor: colorHs,
                                fillOpacity: .7,
                                strokeColor: "black",
                                strokeOpacity: .5,
                                strokeWidth: 2,
                                strokeDashstyle: "longdashdot",   //虚线样式
                                label: '',
                                fontSize: "14px",
                                fontFamily: "微软雅黑",
                                labelOutlineColor: "white",
                                labelOutlineWidth: 3
                            };
                        } else {
                            everyHsSlfFeature.style = {
                                fillColor: "blue",
                                fillOpacity: .7,
                                strokeColor: "black",
                                strokeOpacity: .5,
                                strokeWidth: 2,
                                strokeDashstyle: "longdashdot",   //虚线样式
                                label: '',
                                fontSize: "14px",
                                fontFamily: "微软雅黑",
                                labelOutlineColor: "white",
                                labelOutlineWidth: 3
                            };
                        }
                        if (viewMap.getScale() < '250') {
                            everyHsSlfFeature.style.label = everyHsSlfFeature.attributes.hsOwnerPersons;
                        }
                        hsSlfLayer.addFeatures([everyHsSlfFeature]);
                    }
                }

            }
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
         * 点击选中事件
         */
        onFeatureSelect: function (feature) {
            sltFeature = feature;
            var clickType = feature.attributes.clickType;
            if (clickType == "house") {
                var clickHsId = feature.attributes.clickId;
                var clickCtName = feature.attributes.hsOwnerPersons;
                feature.style.label = clickCtName;
                var selectScheme = $("span.active", navTab.getCurrentPanel());
                //原有hsId
                var hsId = $("input[name=hsId]", selectScheme).val().split(",");
                var ctName = $("input[name=ctName]", selectScheme).val().split(",");
                if (hsId[0] != "") {
                    var flag = false;
                    for (var i = 0; i < hsId.length; i++) {
                        if (clickHsId == hsId[i]) {
                            flag = true;
                            break;
                        }
                    }
                    // 未找到匹配的数据
                    if (!flag) {
                        hsId.push(clickHsId);
                        ctName.push(clickCtName);
                    }
                    $("input[name=hsId]", selectScheme).val(hsId.join(","));
                    $("input[name=ctName]", selectScheme).val(ctName.join(","));
                } else {
                    $("input[name=hsId]", selectScheme).val(clickHsId);
                    $("input[name=ctName]", selectScheme).val(clickCtName);
                }
                fm004.saveSession();
                fm004.queryResult(selectScheme);
            } else if (clickType == "build") {
                var clickBuild = feature.attributes.clickId;
                var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-queryHsByBuild.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("buildId", clickBuild);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    if (data.success) {
                        var hsId = data.hsId.split(",");
                        var ctName = data.ctName.split(",");
                        var selectScheme = $("span.active", navTab.getCurrentPanel());
                        //原有hsId
                        var oldHsId = $("input[name=hsId]", selectScheme).val().split(",");
                        var oldCtName = $("input[name=ctName]", selectScheme).val().split(",");
                        //判断所选方案中是否有已选的房
                        if (oldHsId[0] != "") {
                            // 实际使用的数据
                            var newHsId = [];
                            var newCtName = [];
                            // 处理历史查询条件，存在覆盖则删除原有的查询条件
                            for (var i = 0; i < oldHsId.length; i++) {
                                var found = false;
                                for (var j = 0; j < hsId.length; j++) {
                                    if (oldHsId[i] == hsId[j]) {
                                        found = true;
                                        break;
                                    }
                                }
                                // 未找到匹配的条件
                                if (!found) {
                                    newHsId.push(oldHsId[i]);
                                    newCtName.push(oldCtName[i]);
                                }
                            }
                            // 追加新的查询条件
                            newHsId = newHsId.concat(hsId);
                            newCtName = newCtName.concat(ctName);
                            // 重新设置查询条件
                            $("input[name=hsId]", selectScheme).val(newHsId.join(","));
                            $("input[name=ctName]", selectScheme).val(newCtName.join(","));
                        } else {
                            $("input[name=hsId]", selectScheme).val(hsId.join(","));
                            $("input[name=ctName]", selectScheme).val(ctName.join(","));
                        }

                        //记录buildId
                        //原有buildId
                        var buildId = $("input[name=buildId]", selectScheme).val().split(",");
                        if (buildId[0] != "") {
                            var flag = false;
                            for (var i = 0; i < buildId.length; i++) {
                                if (clickBuild == buildId[i]) {
                                    flag = true;
                                    break;
                                }
                            }
                            // 未找到匹配的数据
                            if (!flag) {
                                buildId.push(clickBuild);
                            }
                            $("input[name=buildId]", selectScheme).val(buildId.join(","));
                        } else {
                            $("input[name=buildId]", selectScheme).val(buildId);
                        }
                        //记录session、执行查询
                        fm004.saveSession();
                        fm004.queryResult(selectScheme);
                    } else {
                        alertMsg.warn(data.errMsg);
                    }
                }, true);
            }
        },

        /**
         * 取消选中事件
         */
        onFeatureUnselect: function (feature) {
            var clickType = feature.attributes.clickType;
            if (clickType == "house") {
                var selHsId = feature.attributes.clickId;
                var selectScheme = $("span.active", navTab.getCurrentPanel());
                var schemeHsId = $("input[name=hsId]", selectScheme);
                var schemectName = $("input[name=ctName]", selectScheme);
                var schemeHsIdArr = schemeHsId.val().split(",");
                var schemectNameArr = schemectName.val().split(",");
                var newHsIdArray = [];
                var newctNameArray = [];
                var x = 0;
                for (var i = 0; i < schemeHsIdArr.length; i++) {
                    if (selHsId != schemeHsIdArr[i]) {
                        newHsIdArray[x] = schemeHsIdArr[i];
                        newctNameArray[x++] = schemectNameArr[i];
                    }
                }
                //删除其中的hsId后重新赋值
                schemeHsId.val(newHsIdArray.toString());
                schemectName.val(newctNameArray.toString());
                //更新session
                fm004.saveSession();
                fm004.queryResult(selectScheme);
            } else if (clickType == "build") {
                var clickBuild = feature.attributes.clickId;
                var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-queryHsByBuild.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("buildId", clickBuild);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    if (data.success) {
                        var hsId = data.hsId.split(",");
                        var ctName = data.ctName.split(",");
                        var selectScheme = $("span.active", navTab.getCurrentPanel());

                        //原有hsId
                        var oldHsId = $("input[name=hsId]", selectScheme).val().split(",");
                        var oldCtName = $("input[name=ctName]", selectScheme).val().split(",");

                        // 实际使用的数据
                        var newHsId = [];
                        var newCtName = [];
                        // 处理历史查询条件，存在覆盖则删除原有的查询条件
                        for (var i = 0; i < oldHsId.length; i++) {
                            var found = false;
                            for (var j = 0; j < hsId.length; j++) {
                                if (oldHsId[i] == hsId[j]) {
                                    found = true;
                                    break;
                                }
                            }
                            // 未找到匹配的条件
                            if (!found) {
                                newHsId.push(oldHsId[i]);
                                newCtName.push(oldCtName[i]);
                            }
                        }
                        // 重新设置查询条件
                        $("input[name=hsId]", selectScheme).val(newHsId.join(","));
                        $("input[name=ctName]", selectScheme).val(newCtName.join(","));
                        //处理原有buildId
                        var schemeBuildId = $("input[name=buildId]", selectScheme);
                        var schemeBuildIdArr = schemeBuildId.val().split(",");
                        var newBuildIdArray = [];
                        var x = 0;
                        for (var i = 0; i < schemeBuildIdArr.length; i++) {
                            if (selHsId != schemeBuildIdArr[i]) {
                                newBuildIdArray[x] = schemeBuildIdArr[i];
                            }
                        }
                        //删除其中的hsId后重新赋值
                        schemeBuildId.val(newBuildIdArray.toString());
                        fm004.saveSession();
                        fm004.queryResult(selectScheme);
                    } else {
                        alertMsg.warn(data.errMsg);
                    }
                }, true);
            }
        }
    };

    /**
     *  条件搜索
     */
    var fm004Search = {
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
            var totalWidth = $("#fm004Container", navTab.getCurrentPanel()).width();
            var leftWidth = left.width();
            if (left.is(":visible")) {
                left.hide();
                $(".arrow button").css({
                    'margin-left': '1px'
                });
                $(".arrow span").text(">");
                $("#fm004AdanceSch", navTab.getCurrentPanel()).width(totalWidth - 10);
            } else {
                left.show();
                $(".arrow button").css({
                    'margin-left': '400px'
                });
                $(".arrow span").text("<");
                $("input[name=inputConditionValue]", navTab.getCurrentPanel()).focus();
                $("#fm004AdanceSch", navTab.getCurrentPanel()).width(totalWidth - leftWidth - 10);
            }


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
        },
        /**
         * 点击展示不同查询条件页面
         */
        chooseSearch: function (obj) {
            var chooseCont = $(obj);
            var fm004SearchDiv = $("div.js_fm004_search", navTab.getCurrentPanel());
            var url = getGlobalPathRoot() + "/eland/fm/fm004/fm004-searchPage.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("whichOne", chooseCont.val());
            core.ajax.sendPacketHtml(packet, function (response) {
                fm004SearchDiv.html(response);
                initUI(fm004SearchDiv);
                fm004Map.loadMapData('search');
            }, true);
        }
    };


    $(document).ready(function () {
        //初始化查询条件页面
        var chooseCont = $("input[name=sltSearchObj]:first", navTab.getCurrentPanel());

        fm004Search.chooseSearch(chooseCont);

        /**
         * 延迟地图渲染
         */
        window.setTimeout(function () {
            fm004Map.init();
            sltCtrl.activate();
        }, 50);
        //初始化选择第一个方案
        $(">span:eq(1)", $("#contentHead", navTab.getCurrentPanel())).trigger("click");
    })
</script>