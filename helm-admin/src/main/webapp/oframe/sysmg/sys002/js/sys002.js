sys002 = {
    openAdd: function () {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=add";
        $.pdialog.open(url, "sys00201", "添加员工", {mask: true, max: true});
    },
    openEdit: function (staffId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=edit&staffId=" + staffId;
        $.pdialog.open(url, "sys00201", "修改员工", {mask: true, max: true});
    },
    openView: function (staffId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=view&staffId=" + staffId;
        $.pdialog.open(url, "sys00201", "查看员工", {mask: true, max: true});
    },
    //打开新建岗位
    openPost: function (staffId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=view&staffId=" + staffId + "&openFrom=openPos";
        $.pdialog.open(url, "sys00202", "岗位选择", {mask: true, max: false, width: 800, height: 600});
    },
    saveStaff: function () {
        var $form = $("#sys00201form", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-save.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    editOrg: function (obj) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-orgTree.gv?prjCd=" + getPrjCd();
        $(obj).openTip({href: url, height: "300", width: "250", offsetX: 0, offsetY: 30});
    },
    editRole: function (obj) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-roleTree.gv?prjCd=" + getPrjCd();
        $(obj).openTip({href: url, height: "300", width: "250", offsetX: 0, offsetY: 30});
    },
    editRht: function (obj) {
        var staffId = $("input[name=staffId]", $.pdialog.getCurrent()).val();
        if (staffId == "") {
            alertMsg.warn("请先保存工号信息");
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-initRht.gv?prjCd=" + getPrjCd();
        $(obj).openTip({href: url, height: "300", width: "250", offsetX: 0, offsetY: 30});
    },
    queryRht: function () {
        var staffId = $("input[name=staffId]", $.pdialog.getCurrent()).val();
        if (staffId == "") {
            return;
        }
        var jsonData = $("#sys00201Rhtfrm", $.pdialog.getCurrent()).serializeArray();
        jsonData.push({
            name: 'currentPage',
            value: '1'
        });
        jsonData.push({
            name: 'staffId',
            value: staffId
        });
        Query.pubQuery(jsonData, 'sys00201_rht_list_print');
    },


    /** ******************************************************* 员工授权 *********************************************************** */
    loadRoleStaff: function (obj) {
        return true;
    },

    /**
     * 加载系统功能
     * @param obj
     * @param grantType 授权类型
     */
    loadSFunc: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            var staffId = $("input[name=staffId]", navTab.getCurrentPanel()).val();
            if (staffId == "" || staffId == undefined) {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-staffRhtInfo.gv?prjCd=" + getPrjCd();
            packet.data.add("rhtType", "1");
            packet.data.add("rhtUseType", "0");
            packet.data.add("grantType", grantType);
            packet.data.add("staffId", staffId);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var rhtTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "ps", "N": "s"}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#sys002RoleRhtTree_1"), rhtTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true);
        }
    },
    /**
     * 保存角色权限
     * @param rhtType 角色类型
     * @param rhtUseType 调整的分类
     */
    saveStaffRht: function (treeId, grantType) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var nodes = treeObj.getCheckedNodes(true);
        console.log(nodes);
        var checkedNodes = "";
        var checkedPrjCds = "";
        if (nodes) {
            var tempArr = [];
            var prjCdArr = [];
            for (var i = 0; i < nodes.length; i++) {
                tempArr.push(nodes[i].rhtId);
                if (nodes[i].prjCd) {
                    prjCdArr.push(nodes[i].prjCd);
                } else {
                    prjCdArr.push(0);
                }
            }
            checkedNodes = tempArr.toString();
            checkedPrjCds = prjCdArr.toString();
        }
        // 重新加载
        var packet = new AJAXPacket();
        // 角色编码
        var staffId = $("input[name=staffId]", navTab.getCurrentPanel()).val();
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-saveStaffRhtInfo.gv";
        packet.data.add("grantType", grantType);
        packet.data.add("staffId", staffId);
        packet.data.add("newRhtIds", checkedNodes);
        packet.data.add("prjCds", checkedPrjCds);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("保存成功")
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 加载系统数据
     * @param obj
     * @param grantType
     */
    loadSData: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            var staffId = $("input[name=staffId]", navTab.getCurrentPanel()).val();
            if (staffId == "" || staffId == undefined) {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-staffSData.gv?prjCd=" + getPrjCd();
            packet.data.add("rhtType", "1");
            packet.data.add("rhtUseType", "0");
            packet.data.add("grantType", grantType);
            packet.data.add("staffId", staffId);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var rhtTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onCheck: sys002.checkNode
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "", "N": ""}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#sys002RoleRhtTree_2"), rhtTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true);
        }
    },
    /**
     * 保存 系统数据
     * @param treeId
     * @param grantType
     */
    saveStaffSData: function(treeId, grantType){

    },

    /**
     * 加载项目功能
     * @param obj
     * @param grantType
     */
    loadPrjFunc: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            var staffId = $("input[name=staffId]", navTab.getCurrentPanel()).val();
            if (staffId == "" || staffId == undefined) {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-prjFunc.gv?prjCd=" + getPrjCd();
            packet.data.add("grantType", grantType);
            packet.data.add("staffId", staffId);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var rhtTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "ps", "N": "s"}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#sys002RoleRhtTree_PFunc"), rhtTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     * 加载居民数据 树
     * @param obj
     * @param grantType
     */
    loadDataRht: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            // 角色编码
            var staffId = $("input[name=staffId]", navTab.getCurrentPanel()).val();
            if (staffId == "" || staffId == undefined) {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-staffDataRht.gv?prjCd=" + getPrjCd();
            packet.data.add("grantType", grantType);
            packet.data.add("staffId", staffId);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var rhtTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onCheck: sys002.checkNode
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "", "N": ""}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#sys002RoleRhtTree_" + grantType), rhtTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true);
        }
    },


    /**
     * 选中父节点取消所有子节点，选中子节点取消所有父节点
     * @param event
     * @param treeId
     * @param treeNode
     */
    checkNode: function (event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var parentNode = treeNode.getParentNode();
        if (parentNode) {
            sys002.checkAllParent(treeObj, parentNode, false);
        }
        var childNodes = treeNode.children;
        sys002.checkAllSub(treeObj, childNodes, false);
    },
    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            sys002.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },
    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                sys002.checkAllSub(treeObj, child.children, checkStatus)
            }
        }
    }
};
