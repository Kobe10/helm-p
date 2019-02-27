var pj015 = {

  /**
   * 数据移动权限
   * @returns {boolean}
   */
  getMoveAble: function () {
    var result = false;
    var obj = $("input[name='moveAble']", navTab.getCurrentPanel());
    if (obj && $(obj).val() == "1") {
      result = true;
    }
    return result;
  },
  /**
   * 移动节点-拖动放下节点时候触发
   * @param treeId  所在 zTree 的 treeId
   * @param treeNodes 被拖拽的节点 JSON 数据集合
   * @param targetNode treeNode 被拖拽放开的目标节点JSON 数据对象。
   * @param moveType  指定移动到目标节点的相对位置,"inner"：成为子节点，"prev"：成为同级前一个节点，"next"：成为同级后一个节点
   */
  beforeDrop: function (treeId, treeNodes, targetNode, moveType) {
    // 只考虑拖拽一个节点的情况
    var packet = new AJAXPacket();
    // 目录树类型
    var catalogType = $("input[name='catalogType']:checked").val();
    packet.data.add("catalogType", catalogType);
    // 当前节点信息
    packet.data.add("mNodeId", treeNodes[0].id);
    packet.data.add("tNodeId", targetNode.id);
    packet.data.add("moveType", moveType);
    // 项目编码
    var prjCd = getPrjCd();
    packet.data.add("prjCd", prjCd);
    // 提交界面展示
    packet.url = getGlobalPathRoot() + "eland/pj/pj015/pj015-moveDoc.gv";
    var moveResult = false;
    // 处理删除
    core.ajax.sendPacketHtml(packet, function (response) {
      var data = eval("(" + response + ")");
      var isSuccess = data.success;
      if (isSuccess) {
        moveResult = true;
      } else {
        alertMsg.error(data.errMsg);
        moveResult = false;
      }
    }, false);
    return moveResult;
  },

  //初始化左侧目录树
  initDocTree: function () {
    // 重新加载
    var packet = new AJAXPacket();
    var prjCd = getPrjCd();
    packet.data.add("prjCd", prjCd);
    packet.data.add("objValue", "");
    packet.url = getGlobalPathRoot() + "eland/pj/pj015/pj015-treeInfo.gv";
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
              onClick: pj015.clickNode,
              beforeDrop: pj015.beforeDrop
            },
            view: {
              selectedMulti: false
            },
            edit: {
              drag: {
                isCopy: false,
                isMove: pj015.getMoveAble()
              },
              enable: pj015.getMoveAble(),
              isMove: false,
              showRemoveBtn: false,
              showRenameBtn: false
            }
          };
          // 初始化目录树
          var zTree = $.fn.zTree.init($("#pj015Tree", navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);
          var rootNode = zTree.getNodesByFilter(function (node) {
            return node.level == 0
          }, true);
          pj015.docInfo(rootNode.id);
        }
        else {
          alertMsg.warn(jsonData.data.errMsg);
        }
      }, true, false
    );
  },

  /**
   * 选择项目树
   */
  sltDocTree: function (obj) {
    var url = getGlobalPathRoot() + "oframe/common/tree/tree-init.gv";
    var inputObj = $(obj).prev("input[type=text]");
    var option = {href: url, height: "230", offsetY: 31, relObj: inputObj};
    option.width = inputObj.width() + 24;
    $(obj).openTip(option);
    tree.getAjaxPacket = pj015.sltRDocTreeAjaxPackage;
  },
  /**
   * 选择项目树
   */
  sltBDocTree: function (obj) {
    var url = getGlobalPathRoot() + "oframe/common/tree/tree-init.gv?treeCheck=true";
    var inputObj = $(obj).prev("input[type=text]");
    var option = {href: url, height: "230", offsetX: 250, width: 300, offsetY: 200, relObj: inputObj};
    $("#pj01502RelDiv", $.pdialog.getCurrent()).openTip(option);
    tree.slfCfmSlt = pj015.bAddTree;
    tree.getAjaxPacket = pj015.sltRDocTreeAjaxPackage;
  },

  /**
   * 获取读取树的URL路径
   * @returns {AJAXPacket}
   */
  sltRDocTreeAjaxPackage: function () {
    var packet = new AJAXPacket();
    packet.url = getGlobalPathRoot() + "eland/pj/pj015/pj015-treeInfo.gv";
    packet.data.add("showFile", "true");
    // 已经选择对象处理
    var $table = $("#pj01502", $.pdialog.getCurrent());
    var relDocIds = [];
    $("input[name=relDocId]", $table).each(function () {
      var value = $(this).val();
      if (value != "") {
        relDocIds.push(value);
      }
    });
    packet.data.add("objValue", relDocIds.join(","));
    return packet;
  },

  /**
   * 批量增加关联文档
   * @param sltDocArr 选的关联文档数据
   */
  bAddTree: function (sltDocArr) {
    var $table = $("#pj01502", $.pdialog.getCurrent());
    for (var i = 0; i < sltDocArr.length; i++) {
      var sltNode = sltDocArr[i];
      var isExists = $("input[name=relDocId][value=" + sltNode.id + "]", $table).length > 0;
      if (!isExists) {
        var hiddenRow = $table.find("tr:eq(1)");
        var copyRow = $(hiddenRow).clone().removeClass("hidden");
        $(hiddenRow).after(copyRow);
        $(copyRow).initUI();
        $("input[name=relDocId]", copyRow).val(sltNode.id);
        $("input[name=relDocName]", copyRow).val(sltNode.name);
      }
    }
  },

  /**
   * 打开检索条件
   * @param obj
   */
  queryCondition: function (obj) {
    var triangle = $("#triangle");
    if (triangle.show()) {
      triangle.hide();
    }
    Page.queryCondition(obj, {
      width: '100%',
      left: 85,
      formObj: $(obj).closest('div.js_query_condition').next('form'),
      changeConditions: pj015.changeConditions
    });
  },

  /**
   * 改变查询条件
   * @param newCondition
   */
  changeConditions: function (newCondition) {
    var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
    currentModelObj.val("1");
    var formObj = $('#pj015QryFrm', navTab.getCurrentPanel());
    $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
    $("input[name=condition]", formObj).val(newCondition.conditions);
    $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
    $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
    $("input[name=resultField]", formObj).val(newCondition.resultFields);
    // 排序字段
    $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
    $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
    pj015.refleshFolder();
  },

  /**
   * 展开或显示区域树
   * @param obj
   */
  extendOrClose: function (obj) {
    var $span = $(obj);
    var zTree = $.fn.zTree.getZTreeObj("pj015Tree");
    if ($span.html() == "展开") {
      zTree.expandAll(true);
      $span.html("折叠");
    } else {
      zTree.expandAll(false);
      $span.html("展开");
    }
  },

  /**
   * 显示上级目录
   */
  showUpFolder: function () {
    // 展开树节点
    var docIdPath = $("input[name=docIdPath]", navTab.getCurrentPanel()).val();
    var docIdArr = docIdPath.split("/");
    if (docIdArr.length > 2) {
      pj015.docInfo(docIdArr[docIdArr.length - 2]);
    }
  },

  /**
   * 切换展示视图
   */
  //changeShowModel: function (obj, model) {
  //    var $this = $(obj);
  //    var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
  //    var currentModel = currentModelObj.val();
  //    if (currentModel == model) {
  //        return;
  //    }
  //    // 修改模式
  //    currentModelObj.val(model);
  //    if (model == '2') {
  //        $this.find("span").addClass("active");
  //        $this.next("li").find("span").removeClass("active");
  //    } else {
  //        $this.find("span").addClass("active");
  //        $this.prev("li").find("span").removeClass("active");
  //    }
  //    // 执行查询
  //    pj015.refleshFolder();
  //},

  /**
   * 刷新文件夹
   */
  refleshFolder: function () {
    // 文档编号
    var form = $("#pj015QryFrm", navTab.getCurrentPanel());
    var docId = $("input[name=upDocId]", form).val();
    var queryModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
    // 图片预览模式
    if (queryModel == "2") {
      var packet = new AJAXPacket();
      packet.data.add("prjCd", getPrjCd());
      packet.data.add("docId", docId);
      packet.data.add("queryModel", queryModel);
      packet.data.add("resultField", $("input[name=resultField]", form).val());
      packet.data.add("queryType", "1");
      //判断条件中是否有 queryType，如果没有就拼上
      var conditionName = $("input[name=conditionName]", form).val().split(",");
      var nameIdx = -1;
      for (var i = 0; i < conditionName.length; i++) {
        var temp = conditionName[i];
        if (temp == "queryType") {
          nameIdx = i;
          break;
        }
      }
      if (nameIdx != -1) {
        // 其他查询条件
        packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
        packet.data.add("conditions", $("input[name=condition]", form).val());
        packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
      } else {
        // 其他查询条件
        packet.data.add("conditionNames", $("input[name=conditionName]", form).val() + ",queryType");
        packet.data.add("conditions", $("input[name=condition]", form).val() + ",=");
        packet.data.add("conditionValues", $("input[name=conditionValue]", form).val() + ",1");
      }
      // 请求连接x
      packet.url = getGlobalPathRoot() + "eland/pj/pj015/pj015-showFile.gv";
      core.ajax.sendPacketHtml(packet, function (response) {
        $("#pj015Context", navTab.getCurrentPanel()).html(response);
        initUI($("#pj015Context", navTab.getCurrentPanel()));
        // 同步目录路径
        pj015.syncPath();
      });
    } else {
      var url = getGlobalPathRoot() + "oframe/common/page/page-data.gv";
      var packet = new AJAXPacket(url);
      // 请求参数
      packet.data.add("entityName", $("input[name=entityName]", form).val());
      packet.data.add("conditionName", $("input[name=conditionName]", form).val() + ",docId");
      packet.data.add("condition", $("input[name=condition]", form).val() + ",=");
      packet.data.add("conditionValue", $("input[name=conditionValue]", form).val() + "," + docId);
      // 排序字段
      packet.data.add("sortColumn", $("input[name=sortColumn]", form).val());
      packet.data.add("sortOrder", $("input[name=sortOrder]", form).val());
      // 上级文档编号
      packet.data.add("upDocId", $("input[name=upDocId]", form).val());
      packet.data.add("divId", $("input[name=divId]", form).val());
      packet.data.add("resultField", $("input[name=resultField]", form).val());
      packet.data.add("forward", $("input[name=forward]", form).val());
      var cmptName = $("input[name=cmptName]", form).val();
      if (cmptName != undefined) {
        packet.data.add("cmptName", cmptName);
      }

      // 分页信息
      packet.data.add("currentPage", 1);
      packet.data.add("pageSize", 20);
      packet.data.add("prjCd", getPrjCd());
      // 判断是点击查询还是翻页
      core.ajax.sendPacketHtml(packet, function (response) {
        var resultDiv = $("div.js_page", navTab.getCurrentPanel());
        resultDiv.html(response);
        resultDiv.initUI();
        // 更新路径
        pj015.syncPath();
      }, true);
      packet = null;
    }

  },

  /**
   * 同步更新目录
   */
  syncPath: function () {
    // 展开树节点
    var docIdPath = $("input[name=docIdPath]", navTab.getCurrentPanel()).val();
    var docIdNamePath = $("input[name=docNamePath]", navTab.getCurrentPanel()).val();
    var treeObj = $.fn.zTree.getZTreeObj("pj015Tree");
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
        docNav = docNav + '---><a class="link" onclick="pj015.docInfo('
          + docIdArr[i] + ')">' + docNameArr[i] + '</a>';
      }
    }
    $("div.js_folder_path", navTab.getCurrentPanel()).html(docNav);
  },

  /**
   * 左侧区域树点击事件
   */
  clickNode: function (event, treeId, treeNode) {
    pj015.docInfo(treeNode.id)
  },

  /**
   * 显示文件夹内的文件列表
   * @param regId
   */
  docInfo: function (docId) {
    var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
    $("input[name=upDocId]", navTab.getCurrentPanel()).val(docId);
    currentModelObj.val("2");
    pj015.refleshFolder();
  },

  /**
   * 下载文件
   * @param docId 文件编号
   */
  viewPics: function (currentDocId) {
    var picDocIds = [];
    $("input.js_pic_id", navTab.getCurrentPanel()).each(function () {
      picDocIds.push($(this).val());
    });
    if (picDocIds.length > 0) {
      ImgPage.viewPics(picDocIds.join(","), currentDocId);
    }
  },

  /**
   * 双击下载文件
   * @param clickObj 操作对象
   * @param flag 操作类型
   */
  clickDownFile: function (docId, svcDocId, prjCd) {
    if (!prjCd) {
      prjCd = getPrjCd();
    }
    //调用 DocRecord 方法手动写 文档操作日志
    var url = getGlobalPathRoot() + "eland/pj/pj015/pj015-docRecord.gv?prjCd=" + prjCd;
    var ajaxPacket = new AJAXPacket(url);
    ajaxPacket.data.add("docId", docId);
    ajaxPacket.data.add("title", "2");
    ajaxPacket.data.add("message", "下载文件");
    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
      var jsonData = eval("(" + response + ")");
      var isSuccess = jsonData.isSuccess;
      // 修改后的文件名称
      if (isSuccess) {
        window.open(getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + svcDocId);
      } else {
        alertMsg.error(jsonData.errMsg);
      }
    }, false);
  },

  /**
   * 右键下载文件
   * @param clickObj 操作对象
   * @param flag 操作类型
   */
  downFile: function (clickObj, flag) {
    var clickObj = $(clickObj).closest("li.album-li");
    var docId = clickObj.find("input[name=docId]").val();
    var svcDocId = clickObj.find("input[name=svcDocId]").val();
    var prjCd = clickObj.find("input[name=prjCd]").val();
    pj015.clickDownFile(docId, svcDocId, prjCd);
  },

  /**
   * 采用文本编辑器打开文件
   * @param docId 项目文档编号
   * @param svcDocId 存储文档编号
   * @param docName 文档名称
   * @param isEditable 是否可以编辑
   * @param openType 打开类型 根据系统码表:OP_DEAL_TYPE 配置
   */
  openFile: function (docId, svcDocId, docName, isEditable, openType) {
    //调用 DocRecord 方法手动写 文档操作日志
    var url = getGlobalPathRoot() + "eland/pj/pj015/pj015-docRecord.gv?prjCd=" + getPrjCd();
    var ajaxPacket = new AJAXPacket(url);
    ajaxPacket.data.add("docId", docId);
    ajaxPacket.data.add("title", openType);
    ajaxPacket.data.add("message", "打开文件");
    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
      var jsonData = eval("(" + response + ")");
      var isSuccess = jsonData.isSuccess;
      // 修改后的文件名称
      var url = getGlobalPathRoot() + "oframe/common/file/file-openDocEditIframe.gv?docId=" + svcDocId
        + "&prjCd=" + getPrjCd() + "&isEditable=" + isEditable;
      if (isSuccess) {
        $.pdialog.open(url, "openDocEditIframe", docName,
          {mask: true, max: true, maxable: true, resizable: true});
      } else {
        alertMsg.error(jsonData.errMsg);
      }
    }, false);
  },

  /**
   * 采用文本编辑器打开文件
   * @param docId 项目文档编号
   * @param svcDocId 存储文档编号
   * @param docName 文档名称
   * @param isEditable 是否可以编辑
   * @param openType 打开类型 根据系统码表:OP_DEAL_TYPE 配置
   */
  openPdf: function (docId, svcDocId, prjCd) {
    //调用 DocRecord 方法手动写 文档操作日志
    var url = getGlobalPathRoot() + "eland/pj/pj015/pj015-docRecord.gv?prjCd=" + getPrjCd();
    var ajaxPacket = new AJAXPacket(url);
    ajaxPacket.data.add("docId", docId);
    ajaxPacket.data.add("title", "2");
    ajaxPacket.data.add("message", "打开文件");
    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
      var jsonData = eval("(" + response + ")");
      var isSuccess = jsonData.isSuccess;
      if (isSuccess) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-viewPdf.gv?docId=" + svcDocId
          + "&prjCd=" + prjCd;
        window.open(url);
      } else {
        alertMsg.error(jsonData.errMsg);
      }
    }, false);
  },

  /**
   * 保存文件信息
   */
  saveDocInfo: function () {
    //调用 DocRecord 方法手动写 文档操作日志
    var url = getGlobalPathRoot() + "eland/pj/pj015/pj015-docRecord.gv?prjCd=" + getPrjCd();
    var ajaxPacket = new AJAXPacket(url);
    var $frm = $("#pj015Docform", $.pdialog.getCurrent());
    var upDocId = $("input[name=upDocId]", $.pdialog.getCurrent()).val();
    var docId = $("input[name=docId]", $.pdialog.getCurrent()).val();
    var docName = $("input[name=docName]", $.pdialog.getCurrent()).val();
    var fileFlag = $("input[name=fileFlag]", $.pdialog.getCurrent()).val();
    ajaxPacket.data.add("docId", docId);
    ajaxPacket.data.add("title", "4");
    ajaxPacket.data.add("message", "修改文件");
    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
      var jsonData = eval("(" + response + ")");
      var isSuccess = jsonData.isSuccess;
      // 修改后的文件名称
      if (isSuccess) {
        if ($frm.valid()) {
          // 提交页面数据
          var url = getGlobalPathRoot() + "eland/pj/pj015/pj015-saveDocInfo.gv?prjCd=" + getPrjCd();
          var ajaxPacket = new AJAXPacket(url);
          ajaxPacket.data.data = $frm.serializeArray();
          //如果是文件夹，并且文件夹里的授权发生变化，弹框确认是否级联修改授权。
          var oldToStaffId = $("input[name=oldToStaffId]", $.pdialog.getCurrent()).val();
          var toStaffId = $("input[name=toStaffId]", $.pdialog.getCurrent()).val();
          var oldArr = oldToStaffId.split(",");
          var newArr = toStaffId.split(",");
          //判断授权角色是否发生变化
          var oldRoleArr = [];
          $("input[name=oldRoles]", $.pdialog.getCurrent()).each(function () {
            if ($(this).val() != '') {
              oldRoleArr.push($(this).val());
            }
          });
          var newRoleCdArr = [];
          $("input[name=roleCd]:checked", $.pdialog.getCurrent()).each(function () {
            if ($(this).val() != '') {
              newRoleCdArr.push($(this).val());
            }
          });

          if (fileFlag == '1' && (oldArr.sort().toString() != newArr.sort().toString() || oldRoleArr.sort().toString() != newRoleCdArr.sort().toString())) {
            alertMsg.confirm("是否级联授权文件夹下的所有文件？", {
              okCall: function () {
                ajaxPacket.data.data.push({name: "cascadeFlag", value: "1"});
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                  var jsonData = eval("(" + response + ")");
                  // 服务调用是否成功
                  var isSuccess = jsonData.success;
                  // 修改后的文件名称
                  if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $.pdialog.closeCurrent();
                    var zTree = $.fn.zTree.getZTreeObj("pj015Tree");
                    var treeNode = zTree.getNodesByParam("id", docId, null);
                    if (treeNode.length > 0) {
                      treeNode[0].name = docName;
                      zTree.updateNode(treeNode[0]);
                    }
                    // 刷新当前文件夹
                    pj015.docInfo(upDocId);
                  } else {
                    alertMsg.error(jsonData.errMsg);
                  }
                });
              },
              cancelCall: function () {
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                  var jsonData = eval("(" + response + ")");
                  // 服务调用是否成功
                  var isSuccess = jsonData.success;
                  // 修改后的文件名称
                  if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $.pdialog.closeCurrent();
                    var zTree = $.fn.zTree.getZTreeObj("pj015Tree");
                    var treeNode = zTree.getNodesByParam("id", docId, null);
                    if (treeNode.length > 0) {
                      treeNode[0].name = docName;
                      zTree.updateNode(treeNode[0]);
                    }
                    // 刷新当前文件夹
                    pj015.docInfo(upDocId);
                  } else {
                    alertMsg.error(jsonData.errMsg);
                  }
                });
              }
            });
          } else {
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
              var jsonData = eval("(" + response + ")");
              // 服务调用是否成功
              var isSuccess = jsonData.success;
              // 修改后的文件名称
              if (isSuccess) {
                alertMsg.correct("处理成功");
                $.pdialog.closeCurrent();
                var zTree = $.fn.zTree.getZTreeObj("pj015Tree");
                var treeNode = zTree.getNodesByParam("id", docId, null);
                if (treeNode.length > 0) {
                  treeNode[0].name = docName;
                  zTree.updateNode(treeNode[0]);
                }
                // 刷新当前文件夹
                pj015.docInfo(upDocId);
              } else {
                alertMsg.error(jsonData.errMsg);
              }
            });
          }
        }
      } else {
        alertMsg.error(jsonData.errMsg);
      }
    }, false);
  },

  /**
   * 新建文件夹
   */
  addFolder: function () {
    var upDocId = $("input[name=upDocId]", navTab.getCurrentPanel()).val();
    var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj015/pj015-addFolder.gv");
    var ulN = $("#pj015Context", navTab.getCurrentPanel()).find("li").length;
    var newName = "新建文件夹(" + ulN + ")";

    // 查询参数
    packet.data.add("upDocId", upDocId);
    packet.data.add("prjCd", getPrjCd());
    packet.data.add("newName", newName);
    // 保存区域信息
    core.ajax.sendPacketHtml(packet, function (response) {
        var data = eval("(" + response + ")");
        var isSuccess = data.success;
        if (isSuccess) {
          var addDoc = data.resultMap.treeJson[0];
          var zTree = $.fn.zTree.getZTreeObj("pj015Tree");
          var pNode = zTree.getNodeByParam("id", addDoc.pId, null);
          zTree.addNodes(pNode, addDoc);
          // 刷新当前目录
          pj015.docInfo(upDocId)
        } else {
          alertMsg.error(data.errMsg);
        }
      }
    );
  },

  /**
   * 在目录下批量新建多个文件
   */
  addFile: function (docInfoArr) {
    var upDocId = $("input[name=upDocId]", navTab.getCurrentPanel()).val();
    var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj015/pj015-addFile.gv");
    // 查询参数
    var packetData = [];
    packetData.push({name: "upDocId", value: upDocId});
    packetData.push({name: "prjCd", value: getPrjCd()});
    for (var i = 0; i < docInfoArr.length; i++) {
      packetData.push({name: "svcDocId", value: docInfoArr[i].docId});
      packetData.push({name: "docName", value: docInfoArr[i].docName});
    }
    packet.data.data = packetData;
    // 保存区域信息
    core.ajax.sendPacketHtml(packet, function (response) {
        var data = eval("(" + response + ")");
        var isSuccess = data.success;
        if (isSuccess) {
          pj015.docInfo(upDocId)
        } else {
          alertMsg.error(data.errMsg);
        }
      }
    );
  },

  /**
   * 开始修改文件名称
   * @param clickObj 点击对象
   */
  startChangeFileName: function (clickObj) {
    var clickSpan = $(clickObj);
    $("label", clickSpan).addClass("hidden");
    $("input", clickSpan).removeClass("hidden");
    $("input", clickSpan).val($("label", clickSpan).html()).focus();
  },

  /**
   * 删除文件和文件夹
   * @param clickObj 点击菜单项目
   * @param flag 标志位
   */
  deleteFile: function (clickObj, flag) {
    var docId = $(clickObj).closest("li.album-li").find("input[name=docId]").val();
    pj015.deleteFileById(docId, flag);
  },

  /**
   * 删除文件和文件夹
   * @param clickObj 点击菜单项目
   * @param flag 标志位
   */
  deleteFileById: function (docId, flag) {
    var message = "您确定要删除该文件吗?";
    if (flag == 1) {
      message = "您确定要删除该文件夹及下面的所有子文件吗?";
    }
    alertMsg.confirm(message, {
      okCall: function () {
        // 执行真实的删除
        pj015.realDeleteFile(docId);
      }
    });
  },

  /**
   * 修改文件/文件夹
   * @param clickObj 点击菜单项目
   * @param flag 标志位
   */
  editFile: function (clickObj, flag) {
    var docId = $(clickObj).closest("li.album-li").find("input[name=docId]").val();
    pj015.editFileById(docId, flag);
  },

  /**
   * 修改文件内容
   * @param clickObj 点击菜单项目
   * @param flag 标志位
   */
  editFileDoc: function (clickObj, flag) {
    var liObj = $(clickObj).closest("li.album-li");
    var docId = liObj.find("input[name=docId]").val();
    var docName = liObj.find("input[name=docName]").val();
    var svcDocId = liObj.find("input[name=svcDocId]").val();
    pj015.openFile(docId, svcDocId, docName, true, '4');
    // 隐藏菜单
    $("ul.js_menu_active", navTab.getCurrentPanel()).remove();
  },

  /**
   * 移动文件和文件夹
   * @param clickObj
   * @param flag
   */
  moveDoc: function (clickObj, flag) {
    var liObj = $(clickObj).closest("li.album-li");
    var docId = liObj.find("input[name=docId]").val();
    // 当前的上级节点
    var upDocId = liObj.find("input[name=upDocId]").val();
    var url = getGlobalPathRoot() + "oframe/common/tree/tree-init.gv?treeData="
      + encodeURI("eland/pj/pj015/pj015-treeInfo.gv");
    var option = {href: url, height: "530", offsetX: 250, width: 400, offsetY: 200};
    $(liObj).openTip(option);
    tree.slfClickNode = function (clickNode) {
      var toDocId = clickNode.id;
      // 移动到的节点与单前上级节点一致不移动
      if (upDocId == toDocId) {
        return false;
      } else {
        // 调用服务移动文件路径
        var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj015/pj015-moveDoc.gv");
        // 查询参数
        var packetData = [];
        packetData.push({name: "mNodeId", value: docId});
        packetData.push({name: "tNodeId", value: toDocId});
        packetData.push({name: "prjCd", value: getPrjCd()});
        packet.data.data = packetData;
        // 保存区域信息
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
              // 删除被移动的节点
              liObj.remove();
            } else {
              alertMsg.error(data.errMsg);
            }
          }, false
        );
        return true;
      }
    }
  },

  /**
   * 修改文件/文件夹
   * @param clickObj 点击菜单项目
   * @param flag 标志位
   */
  editFileById: function (docId, flag) {

    var title = "文件修改";
    if (flag == 1) {
      title = "文件夹修改";
    }
    var url = getGlobalPathRoot() + "eland/pj/pj015/pj015-editFile.gv?"
      + "&docId=" + docId + "&prjCd=" + getPrjCd() + "&fileFlag=" + flag;
    $.pdialog.open(url, "pj01502", title,
      {mask: true, max: false, maxable: false, resizable: true, width: 800, height: 600});
    // 隐藏菜单
    $("ul.js_menu_active", navTab.getCurrentPanel()).remove();
  },

  /**
   * 实际删除文件
   * @param docId 文件编号
   */
  realDeleteFile: function (docId) {
    //
    var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj015/pj015-deleteFile.gv");
    // 查询参数
    var packetData = [];
    packetData.push({name: "docId", value: docId});
    packetData.push({name: "prjCd", value: getPrjCd()});
    packet.data.data = packetData;
    // 保存区域信息
    core.ajax.sendPacketHtml(packet, function (response) {
        var data = eval("(" + response + ")");
        var isSuccess = data.success;
        if (isSuccess) {
          // 删除树节点
          var zTree = $.fn.zTree.getZTreeObj("pj015Tree");
          var treeNode = zTree.getNodesByParam("id", docId, null);
          if (treeNode.length > 0) {
            zTree.removeNode(treeNode[0], false);
          }
          // 刷新目录区域
          var upDocId = $("input[name=upDocId]", navTab.getCurrentPanel()).val();
          pj015.docInfo(upDocId);
        } else {
          alertMsg.error(data.errMsg);
        }
      }
    );
  },

  /**
   * 改变文件名称
   * @param clickObj 点击对象
   */
  changeFileName: function (clickObj) {
    var inputObj = $(clickObj);
    var clickLi = inputObj.closest("li");
    var labelObj = inputObj.prev("label");
    var docId = $("input[name=docId]", clickLi).val();
    if (inputObj.val() != labelObj.html()) {
      var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj015/pj015-changeName.gv");
      var packetData = [];
      packetData.push({name: "prjCd", value: getPrjCd()});
      packetData.push({name: "docId", value: docId});
      packetData.push({name: "docName", value: inputObj.val()});
      packet.data.data = packetData;
      // 保存区域信息
      core.ajax.sendPacketHtml(packet, function (response) {
          var data = eval("(" + response + ")");
          var isSuccess = data.success;
          if (isSuccess) {
            // 文件名称修改成功
            labelObj.html(inputObj.val());
            // 修改隐藏
            inputObj.addClass("hidden");
            labelObj.removeClass("hidden");
            //
            var zTree = $.fn.zTree.getZTreeObj("pj015Tree");
            var treeNode = zTree.getNodesByParam("id", docId, null);
            if (treeNode.length > 0) {
              treeNode[0].name = inputObj.val();
              zTree.updateNode(treeNode[0]);
            }

            // 刷新当前文件夹
            var upDocId = $("input[name=upDocId]", navTab.getCurrentPanel()).val();
            pj015.docInfo(upDocId)
          } else {
            alertMsg.error(data.errMsg);
          }
        }
      );
    } else {
      // 修改隐藏
      inputObj.addClass("hidden");
      labelObj.removeClass("hidden");
    }
  },

  /**
   *  显示或隐藏目录
   */
  extendOrClosePanel: function (clickObj) {
    var $pucker = $(clickObj);
    var $content = $pucker.closest("div.panelHeader").next("div.panelContent");
    if ($pucker.hasClass("collapsable")) {
      $pucker.removeClass("collapsable").addClass("expandable");
      $content.hide();
      pj015.adjustHeight();
    } else {
      $pucker.removeClass("expandable").addClass("collapsable");
      $content.show();
      pj015.adjustHeight();
    }
  },

  /**
   * 显示关联文档信息
   * @param clickObj 点击对象
   */
  uploadDoc: function (clickObj) {
    var $span = $(clickObj);
    var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd();
    $.pdialog.open(url, "file", "附件上传", {
      height: 600, width: 800, mask: true,
      close: pj015.docClosed,
      param: {clickSpan: $span}
    });
  },

  /**
   * 关闭上传窗口
   * @param param
   * @returns {boolean}
   */
  docClosed: function (param) {
    var uploadedFiles = file.getAllUploadFiles();
    // 转换获取的上传文件
    var tempArray = new Array();
    for (var i = 0; i < uploadedFiles.length; i++) {
      var uploadedFile = uploadedFiles[i];
      var temp = {};
      temp.docId = uploadedFile.docId;
      temp.docName = uploadedFile.fileName;
      tempArray.push(temp);
    }
    if (tempArray.length > 0) {
      // 调用服务增加服务节点
      pj015.addFile(tempArray);
    }
    // 调用文件的关闭
    return file.closeD();
  },

  /**
   * 点击开始上传文件
   */
  startImport: function () {
    $("#uploadFile", navTab.getCurrentPanel()).val("");
    $("#uploadFile", navTab.getCurrentPanel()).unbind("change", pj015.importFile);
    $("#uploadFile", navTab.getCurrentPanel()).bind("change", pj015.importFile);
  },

  /**
   * Excel 导入 Person
   */
  importFile: function (obj) {
    var uploadURL = getGlobalPathRoot() + "oframe/common/file/file-upload.gv?prjCd=" + getPrjCd();
    var docNamePath = $("input[name=docNamePath]", navTab.getCurrentPanel()).val();
    $.ajaxFileUpload({
        url: uploadURL,
        secureuri: false,
        fileElementId: "uploadFile",
        dataType: 'json',
        data: {subDir: docNamePath},
        success: function (data, status, fileElementId) {
          if (data.success) {
            // 调用服务增加服务节点
            pj015.addFile(data.data);
          } else {
            alertMsg.error(data.errMsg);
          }
        }
      }
    )
  },

  /**
   * 上传图片处理事件
   */
  uploadFile: function () {
    if (typeof FileReader == "undefined") {
      alertMsg.error("您的浏览器不支持拖拽上传");
    }
    var dragFile = event.dataTransfer.files;
    //上传文件到服务器
    var fd = new FormData();
    for (var i = 0; i < dragFile.length; i++) {
      var tempFile = dragFile[i];
      var reader = new FileReader();
      reader.readAsDataURL(tempFile);
      reader.onload = (function (file) {
        return function (e) {
        }
      })(tempFile);
      fd.append("uploadFile", tempFile);
    }
    var docNamePath = $("input[name=docNamePath]", navTab.getCurrentPanel()).val();
    fd.append("subDir", docNamePath);
    pj015.sendFileToServer(fd);
    // 停止响应事件
    stopEvent(event);
  },

  /**
   * 将图片上传到服务器
   * @param formData
   */
  sendFileToServer: function (formData) {
    var uploadURL = getGlobalPathRoot() + "oframe/common/file/file-upload.gv?prjCd=" + getPrjCd();
    var extraData = {};
    var jqXHR = $.ajax({
      xhr: function () {
        var xhrObj = $.ajaxSettings.xhr();
        return xhrObj;
      },
      url: uploadURL,
      type: "POST",
      contentType: false,
      processData: false,
      cache: false,
      data: formData,
      success: function (data) {
        var jsonData = eval("(" + data + ")");
        if (jsonData.success) {
          // 新建文件
          var docInfoArr = jsonData.data;
          pj015.addFile(docInfoArr);
        } else {
          alertMsg.error(jsonData.errMsg);
        }
      }
    });
  },
  //关键字自动显示
  changeKeyWord: function (obj) {
    var changeTd = $(obj).parent();
    var keyWord = $(obj).val();
    if (!/^\s*$/.test(keyWord)) {
      var newKey = $("label.js_keyword", changeTd).first().clone();
      $("span.in-block", newKey).val("");
      newKey.appendTo(changeTd.last("label"));
      $("span.in-block", newKey).html(keyWord);
      $("label.js_keyword", changeTd).last().show();
      $("input[name=keyWord]", newKey).val(keyWord);
      $(obj).val("");
    } else {
      alert("关键字不可以为空！");
    }
  },

  /**
   * 快速检索对话框打开
   * @param conditionFieldArr 查询字段数组
   * @param conditionArr 查询条件数组
   * @param conditionValueArr 查询值数组
   */
  openQSch: function (clickObj) {
    if (!$(clickObj).hasClass("active")) {
      $("#pj015QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
      $("#pj015QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
    }
  },

  /**
   * 离开检索按钮
   */
  leaveQSch: function () {
    setTimeout(function () {
      pj015.closeQSch(false);
    }, 300);
  },

  /**
   * 关闭检索
   * @param forceFlag 强制关闭
   */
  closeQSch: function (forceFlag) {
    var close = forceFlag;
    if (!forceFlag) {
      var active = $("#pj015QSchDialog", navTab.getCurrentPanel()).attr("active");
      if ("on" == active) {
        close = true;
      } else {
        close = false;
      }
    }
    if (close) {
      $("#pj015QSchDialog", navTab.getCurrentPanel()).hide();
    }
  },

  /**
   * 快速检索
   */
  qSchData: function () {
    var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
    currentModelObj.val("1");
    var formObj = $('#pj015QryFrm', navTab.getCurrentPanel());
    var docId = $("input[name=upDocId]", $("#pj015QryFrm", navTab.getCurrentPanel())).val();
    var conditionName = $("input[name=conditionName]", formObj).val().split(",");
    var condition = $("input[name=condition]", formObj).val().split(",");
    var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
    var queryTypeValue = $("input[name=queryType]:checked", $('#pj015QSchDialog', navTab.getCurrentPanel())).val();
    $("#pj015QSchDialog", navTab.getCurrentPanel()).find("input").each(function () {
      var $this = $(this);
      var attrName = $this.attr("name");
      var attrValue = $this.val();
      //if ($.trim(attrValue) == "") {
      //    return true;
      //}
      if (attrName == "queryType") {
        attrValue = queryTypeValue;
      }
      var nameIdx = -1;
      for (var i = 0; i < conditionName.length; i++) {
        var temp = conditionName[i];
        if (temp == attrName) {
          nameIdx = i;
          break;
        }
      }
      if (nameIdx != -1) {
        condition[nameIdx] = $this.attr("condition");
        conditionValue[nameIdx] = attrValue;
      } else {
        conditionName.push($this.attr("name"));
        condition.push($this.attr("condition"));
        conditionValue.push(attrValue);
      }
    });
    $("input[name=conditionName]", formObj).val(conditionName.join(","));
    $("input[name=condition]", formObj).val(condition.join(","));
    $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
    pj015.closeQSch(true);
    pj015.refleshFolder();
  },

  /**
   * 增加授权用户
   * @param staffId 用户编号
   * @param staffName 用户名称
   */
  addRhtStaff: function (staffId, staffName) {
    //选中 授权用户
    var staffIdArr = [];
    var staffIdStr = $("input[name=toStaffId]", $.pdialog.getCurrent()).val();
    var currDiv = $("div.js_toStaff_div", $.pdialog.getCurrent());
    var hideSpan = $("span.hidden", currDiv).clone(true);
    if (staffIdStr != '') {
      staffIdArr = staffIdStr.split(",");
    }
    var idInArr = false;
    for (var i = 0; i < staffIdArr.length; i++) {
      var tempStaffId = staffIdArr[i];
      if (tempStaffId == staffId) {
        idInArr = true;
        break;
      }
    }
    if (!idInArr) {
      staffIdArr.push(staffId);
      $("input[name=currentStaff]", hideSpan).val(staffId);
      hideSpan.removeClass("hidden").addClass("js_toStaff_span");
      $("span.js_toStaff_name", hideSpan).text(staffName);
      currDiv.append(hideSpan);
      $("input[name=toStaffId]", $.pdialog.getCurrent()).val(staffIdArr.join(","));
    }
  },
  /**
   * 删除 授权用户列表
   * @param obj
   */
  rmToStaff: function (obj) {
    var _this = $(obj);
    var currSpan = _this.closest("span");
    var currStaffInput = $("input[name=currentStaff]", currSpan).val();
    var staffIdArr = [];
    var staffIdStr = $("input[name=toStaffId]", $.pdialog.getCurrent()).val();
    if (staffIdStr && staffIdStr != undefined && staffIdStr != '') {
      staffIdArr = staffIdStr.split(",");
    }
    var newArray = [];
    var x = 0;
    for (var i = 0; i < staffIdArr.length; i++) {
      if (staffIdArr[i] == currStaffInput) {
        _this.parent().remove();

      } else {
        newArray[x++] = staffIdArr[i];
      }
    }
    $("input[name=toStaffId]", $.pdialog.getCurrent()).val(newArray.join(","));
  },

  /**
   * 授权角色
   * @param obj
   */
  editRole: function (obj) {
    var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-roleTree.gv?prjCd=" + getPrjCd();
    $(obj).openTip({href: url, width: "250", offsetX: 0, offsetY: 30});
  },

  /**
   * 打开上传图片
   */
  changeFileUpload: function () {
    var uploadURL = getGlobalPathRoot() + "/oframe/common/file/file-upload.gv?prjCd=" + getPrjCd();
    var fileElementId = pj015.uploadObj.find("input[type=file]").attr("id");
    var docNamePath = $("input[name=docNamePath]", navTab.getCurrentPanel()).val();
    $.ajaxFileUpload({
        url: uploadURL,
        secureuri: false,
        fileElementId: fileElementId,
        dataType: 'json',
        data: {subDir: docNamePath},
        success: function (data, status, fileElementId) {
          if (data.success) {
            var newSvcDocId = data.data[0].docId;
            var newSvcDocName = data.data[0].docName;
            var clickObj = pj015.uploadObj.closest("li.album-li");
            var docId = clickObj.find("input[name=docId]").val();
            var oldSvcDocId = clickObj.find("input[name=svcDocId]").val();
            //调用 DocRecord 方法手动写 文档操作日志
            var url = getGlobalPathRoot() + "eland/pj/pj015/pj015-docRecord.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("docId", docId);
            ajaxPacket.data.add("title", "4");
            ajaxPacket.data.add("message", "重新上传了文件【<a target='_blank' href='" + getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + oldSvcDocId + "'>原文件下载</a>】");
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
              var jsonData = eval("(" + response + ")");
              var isSuccess = jsonData.isSuccess;
              // 修改后的文件名称
              if (isSuccess) {
                pj015.doRealChangeFile(docId, newSvcDocId, oldSvcDocId);
              } else {
                alertMsg.error(jsonData.errMsg);
              }
            }, false);
          } else {
            alertMsg.error(data.errMsg);
          }
        }
      })
  },

  /**
   * 执行文件更新
   * @param docId
   * @param newSvcDocId
   * @param oldSvcDocId
   */
  doRealChangeFile: function (docId, newSvcDocId, oldSvcDocId) {
    // 获取当前修改的文件
    var upDocId = $("input[name=upDocId]", navTab.getCurrentPanel()).val();
    // 修改上传的文件
    var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj015/pj015-changeFile.gv");
    // 查询参数
    var packetData = [];
    packetData.push({name: "docId", value: docId});
    packetData.push({name: "newSvcDocId", value: newSvcDocId});
    packetData.push({name: "oldSvcDocId", value: oldSvcDocId});
    packetData.push({name: "prjCd", value: getPrjCd()});
    packet.data.data = packetData;
    // 保存区域信息
    core.ajax.sendPacketHtml(packet, function (response) {
        var data = eval("(" + response + ")");
        var isSuccess = data.success;
        if (isSuccess) {
          alertMsg.correct("文件更新成功");
          pj015.docInfo(upDocId)
        } else {
          alertMsg.error(data.errMsg);
        }
      }
    );
  },

  /**
   * 点击重新上传文件
   * @param obj
   */
  changeFile: function (obj) {
    var clickFileBtn = $(obj).find("input[type=file][name=importPic]");
    clickFileBtn.attr("id", new Date().getTime());
    clickFileBtn.val("");
    clickFileBtn.unbind("change", pj015.changeFileUpload);
    clickFileBtn.bind("change", pj015.changeFileUpload);
    pj015.uploadObj = $(obj);
  }

};
$(document).ready(function () {
  //初始化左侧区域树
  pj015.initDocTree();
  // 支持拖拽则增加样式提示
  if (typeof FileReader != "undefined") {
    $("#pj015Context", navTab.getCurrentPanel()).addClass("dragable");
  }
  // 右键点击事件
  navTab.getCurrentPanel().undelegate("li.album-li", "contextmenu");
  navTab.getCurrentPanel().bind("click", function () {
    $("ul.js_menu_active", navTab.getCurrentPanel()).hide();
  });
  // 右键点击事件
  navTab.getCurrentPanel().delegate("li.album-li", "contextmenu", function () {
    $("ul.js_menu_active", navTab.getCurrentPanel()).remove();
    var docId = $("input[name=docId]", this).val();
    var docFlag = $("input[name=docFlag]", this).val();
    var albumContext = $("div.album-context", this);
    var menuTemplate = null;
    if (docFlag == "1") {
      menuTemplate = $("ul.js_folder_template", navTab.getCurrentPanel());
    } else {
      menuTemplate = $("ul.js_file_template", navTab.getCurrentPanel());
    }
    var newMenu = menuTemplate.clone();
    if (!albumContext.hasClass("js_doc")) {
      newMenu.find("li.js_doc_edit").remove();
    }
    newMenu.removeClass("hidden");
    newMenu.removeClass("js_file_template");
    newMenu.removeClass("js_folder_template");
    newMenu.addClass("js_menu_active");
    newMenu.css("left", event.offsetX + 10 + "px");
    newMenu.css("top", event.offsetY + 10 + "px");
    $(this).append(newMenu);
    return false;
  });
  // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
  LeftMenu.init("pj015DivTree", "pj015ContextDiv", {});

});
