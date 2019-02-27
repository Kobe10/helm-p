var ph011 = {

    /**
     * 是否是院落弹出框
     * @returns {boolean}
     */
    isBldDialog: function () {
        var ph011BldDialog = $("input[name=ph011BldDialog]", $.pdialog.getCurrent());
        return ph011BldDialog.length > 0;
    },

    /**
     * 获取当前项目资料容器
     * @returns {*}
     */
    getContainer: function () {
        if (ph011.isBldDialog()) {
            return $.pdialog.getCurrent();
        } else {
            return navTab.getCurrentPanel();
        }
    },

    //初始化左侧区域树
    initFullRegTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", "1");
        packet.data.add("objValue", "");
        packet.url = getGlobalPathRoot() + "eland/pj/pj004/pj004-treeInfo.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjOrgTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: ph011.clickNode
                        },
                        view: {
                            selectedMulti: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#ph011Tree", ph011.getContainer()), pjOrgTreeSetting, treeJson);
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    ph011.docInfo(rootNode.id);
                } else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },

    /**
     * 显示文件夹内的文件列表
     * @param regId
     */
    docInfo: function (id) {
        $("input[name=cRegId]", ph011.getContainer()).val(id);
        $("input[name=cBuildId]", ph011.getContainer()).val("");
        $("input[name=cHsId]", ph011.getContainer()).val("");
        $("input[name=cType]", ph011.getContainer()).val("");
        ph011.refleshFolder();
    },

    /**
     * 显示上级目录
     */
    showUpFolder: function () {
        var goUpFlag = $("input[name=goUpFlag]", ph011.getContainer()).val();
        var upRegId = $("input[name=upRegId]", ph011.getContainer()).val();
        var cRegId = $("input[name=cRegId]", ph011.getContainer()).val();
        var cBuildId = $("input[name=cBuildId]", ph011.getContainer()).val();
        var cHsId = $("input[name=cHsId]", ph011.getContainer()).val();
        var cType = $("input[name=cType]", ph011.getContainer()).val();
        if (goUpFlag == "reg01") {
            var zTree = $.fn.zTree.getZTreeObj("ph011Tree");
            var rootNode = zTree.getNodesByFilter(function (node) {
                return node.id == upRegId
            }, true);
            if (rootNode == null) {
                alertMsg.warn("当前是根节点");

            } else {
                ph011.docInfo(upRegId);
            }
        } else if (goUpFlag == "reg02") {
            // 对话框没有目录树结构
            if (ph011.isBldDialog()) {
                alertMsg.warn("当前是根节点");
            } else {
                ph011.docInfo(upRegId);
            }
        } else if (goUpFlag == "reg03") {
            ph011.hsByBuildId(cRegId, cBuildId);
        } else if (goUpFlag == "reg04") {
            if (cHsId == "") {
                ph011.hsByBuildId(cRegId, cBuildId);
            } else {
                ph011.attByHouseId(cHsId);
            }
        }
    },

    /**
     * 刷新文件夹
     */
    refleshFolder: function () {
        // 文档编号
        var form = $("#ph011queryForm", ph011.getContainer());
        if (ph011.isBldDialog()) {
            form = $("#ph011BldDialogQueryForm", ph011.getContainer());
        }
        var rhtRegId = $("input[name=cRegId]", form).val();
        var cBuildId = $("input[name=cBuildId]", form).val();
        var regUseType = $("input[name=regUseType]", form).val();
        // 图片预览模式
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("rhtRegId", rhtRegId);
        packet.data.add("regUseType", regUseType);
        // 请求连接x
        packet.url = getGlobalPathRoot() + "eland/ph/ph011/ph011-showFile.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph011Context", ph011.getContainer()).html(response);
            initUI($("#ph011Context", ph011.getContainer()));
            $("#exprExcel", ph011.getContainer()).hide();
            ph011.syncTreeNode();
        });
    },

    /**
     * 触发点击到指定文件夹
     */
    syncTreeNode: function () {
        // 对话框没有目录树结构
        if (ph011.isBldDialog()) {
            return;
        }
        var rhtRegId = $("input[name=cRegId]", ph011.getContainer()).val();
        var zTree = $.fn.zTree.getZTreeObj("ph011Tree");
        var pNode = zTree.getNodeByParam("id", rhtRegId, null);
        if (pNode) {
            zTree.selectNode(pNode);
        }
    },

    /**
     * 同步更新目录
     */
    syncPath: function () {
        // 展开树节点
        var docIdPath = $("input[name=docIdPath]", ph011.getContainer()).val();
        var docIdNamePath = $("input[name=docNamePath]", ph011.getContainer()).val();
        var treeObj = $.fn.zTree.getZTreeObj("ph011Tree");
        var docIdArr = docIdPath.split("/");
        var docNameArr = docIdNamePath.split("/");
        var docNav = '<a class="current">项目资料</a>';
        for (var i = 0; i < docIdArr.length; i++) {
            if ("" != docIdArr[i]) {
                var treeNode = treeObj.getNodesByParam("id", docIdArr[i], null);
                if (treeNode.length > 0) {
                    treeObj.expandNode(treeNode[0], true, false, false);
                }
                // 导航菜单
                docNav = docNav + '---><a class="link" onclick="ph011.docInfo('
                    + docIdArr[i] + ')">' + docNameArr[i] + '</a>';
            }
        }
        $("div.js_folder_path", ph011.getContainer()).html(docNav);
    },

    /**
     * 左侧区域树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        //如果点击的是区域
        if (treeNode.useType == "" || treeNode.useType == '1') {
            ph011.docInfo(treeNode.id);
        } else {
            //查询实体的组件
            ph011.hsByBuildId(treeNode.id, treeNode.buldId);
        }
    },

    /**
     * 点击左侧的节点显示建筑的附件和房屋
     * @param regId
     */
    hsByBuildId: function (cRegId, buldId) {
        // 清空历史信息
        $("input[name=cHsId]", ph011.getContainer()).val("");
        $("input[name=cType]", ph011.getContainer()).val("");
        // 设置当前显示信息
        $("input[name=cRegId]", ph011.getContainer()).val(cRegId);
        $("input[name=cBuildId]", ph011.getContainer()).val(buldId);
        ph011.refleshBuildId();
    },

    refleshBuildId: function () {
        // 文档编号
        var form = $("#ph011queryForm", ph011.getContainer());
        if (ph011.isBldDialog()) {
            form = $("#ph011BldDialogQueryForm", ph011.getContainer());
        }
        var cRegId = $("input[name=cRegId]", form).val();
        var cBuildId = $("input[name=cBuildId]", form).val();
        var regUseType = $("input[name=regUseType]", form).val();
        // 图片预览模式
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("cBuildId", cBuildId);
        packet.data.add("cRegId", cRegId);
        // 请求连接x
        packet.url = getGlobalPathRoot() + "eland/ph/ph011/ph011-showBuildFile.gv";//ph01102
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph011Context", ph011.getContainer()).html(response);
            $("#exprExcel", ph011.getContainer()).show();
            initUI($("#ph011Context", ph011.getContainer()));
            ph011.syncTreeNode();
        });
    },

    /**
     * 显示文件夹内的文件列表
     * @param regId
     */
    attByHouseId: function (hsId) {
        var form = $("#ph011queryForm", ph011.getContainer());
        if (ph011.isBldDialog()) {
            form = $("#ph011BldDialogQueryForm", ph011.getContainer());
        }
        $("input[name=cHsId]", form).val(hsId);
        ph011.refleshHouse();
    },

    /**
     * 显示房屋内的附件
     */
    refleshHouse: function () {
        // 文档编号
        var form = $("#ph011queryForm", ph011.getContainer());
        if (ph011.isBldDialog()) {
            form = $("#ph011BldDialogQueryForm", ph011.getContainer());
        }
        var cHsId = $("input[name=cHsId]", form).val();
        var cRegId = $("input[name=cRegId]", form).val();
        var cBuildId = $("input[name=cBuildId]", form).val();
        var regUseType = $("input[name=regUseType]", form).val();
        // 图片预览模式
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("cRegId", cRegId);
        packet.data.add("cBuildId", cBuildId);
        packet.data.add("cHsId", cHsId);
        packet.data.add("regUseType", regUseType);
        // 请求连接xph01103
        packet.url = getGlobalPathRoot() + "eland/ph/ph011/ph011-showHouseFile.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph011Context", ph011.getContainer()).html(response);
            initUI($("#ph011Context", ph011.getContainer()));
            var docCount = $("#ph011Context", ph011.getContainer()).find("li.album-li").length;
            if (docCount > 0) {
                $("#exprExcel", ph011.getContainer()).show();
            } else {
                $("#exprExcel", ph011.getContainer()).hide();
            }
            ph011.syncTreeNode();
            // 同步目录路径
            //ph011.syncPath();
        });
    },
    /**
     * 下载文件
     * @param docId 文件编号
     */
    downFile: function (docId) {
        window.open(getGlobalPathRoot() + "oframe/common/file/file-download.gv?prjCd=" + getPrjCd() + "&docId=" + docId);
    },

    /**
     * 下载文件
     * @param docId 文件编号
     */
    viewPdf: function (docId) {
        window.open(getGlobalPathRoot() + "oframe/common/file/file-viewPdf.gv?prjCd=" + getPrjCd() + "&docId=" + docId);
    },

    /**
     * 显示关联的附件
     * @param relId
     * @param relType
     * @param docTypeName
     */
    selAttachment: function (relId, relType, docTypeName) {
        var form = $("#ph011queryForm", ph011.getContainer());
        if (ph011.isBldDialog()) {
            form = $("#ph011BldDialogQueryForm", ph011.getContainer());
        }
        $("input[name=upRegId]", form).val();
        // 文档编号
        // 图片预览模式
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("relId", relId);
        packet.data.add("relType", relType);
        packet.data.add("docTypeName", docTypeName);
        // 请求连接x
        packet.url = getGlobalPathRoot() + "eland/ph/ph011/ph011-selAttachmentFile.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph011Context", ph011.getContainer()).html(response);
            initUI($("#ph011Context", ph011.getContainer()));
            // 同步目录路径
            //ph011.syncPath();
        });
    },

    /**
     * 导出 生成公房结算明细表（房屋）
     * @param buildId 院落信息
     * @param relId 房屋信息
     */
    exportFjCl: function () {
        var cBuildId = $("input[name=cBuildId]", ph011.getContainer()).val();
        var cHsId = $("input[name=cHsId]", ph011.getContainer()).val();
        var docTypeName = $("input[name=docTypeName]", ph011.getContainer()).val();
        if (!docTypeName) {
            docTypeName = "";
        }
        var url = "";
        if (cHsId != "") {
            url = getGlobalPathRoot() + "eland/ph/ph003/ph003-exportFj.gv?hsId=" + cHsId
                + "&docTypeName=" + encodeURI(encodeURI(docTypeName))
                + "&prjCd=" + getPrjCd();
        } else if (cBuildId != "") {
            url = getGlobalPathRoot() + "eland/ph/ph002/ph002-exportYardFj.gv?buildId=" + cBuildId
                + "&docTypeName=" + encodeURI(encodeURI(docTypeName))
                + "&prjCd=" + getPrjCd();
        }
        window.open(url)
    },

    extendOrClose: function (obj) {
        var $span = $(obj);
        var zTree = null;
        if ($("#ph011Tree", ph011.getContainer()).is(":hidden")) {
            zTree = $.fn.zTree.getZTreeObj("ph011OrgTree");
        } else {
            zTree = $.fn.zTree.getZTreeObj("ph011Tree");
        }
        if ($span.html() == "展开") {
            zTree.expandAll(true);
            $span.html("折叠");
        } else {
            zTree.expandAll(false);
            $span.html("展开");
        }
    }
};

