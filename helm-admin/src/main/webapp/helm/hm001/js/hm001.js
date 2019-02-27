hm001 = {
  uploadObj: null,
  /**
   * 打开快速检索对话框
   */
  openQSch: function (clickObj) {
    if (!$(clickObj).hasClass("active")) {
      $("#hm001create", navTab.getCurrentPanel()).show().attr("active", "on");
      $("#hm001create input:eq(0)", navTab.getCurrentPanel()).focus();
    }
  },

  /**
   * 调用通用查询组件
   * @param conditionFieldArr 查询字段数组
   * @param conditionArr 查询条件数组
   * @param conditionValueArr 查询值数组
   */
  queryNotice: function (obj) {
    hm001.closeQSch(true);
    Page.queryCondition(obj, {
      changeConditions: hm001.changeConditions,
      width: "100%",
      formObj: $("#hm001form", navTab.getCurrentPanel())
    });
  },
  /**
   * 关闭检索
   * @param forceFlag 强制关闭
   */
  closeQSch: function (forceFlag) {
    var close = forceFlag;
    if (!forceFlag) {
      var active = $("#hm001create", navTab.getCurrentPanel()).attr("active");
      if ("on" == active) {
        close = true;
      } else {
        close = false;
      }
    }
    if (close) {
      $("#hm001create", navTab.getCurrentPanel()).hide();
    }
  },
  /**
   * 改变查询条件
   * @param newCondition
   */
  changeConditions: function (newCondition) {
    var formObj = $('#hm001form', navTab.getCurrentPanel());
    $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
    $("input[name=condition]", formObj).val(newCondition.conditions);
    $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
    $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
    $("input[name=resultField]", formObj).val(newCondition.resultFields);
    // 排序字段
    $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
    $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
    Page.query(formObj, "");
  },

  /**
   * 打开添加页面
   */
  editView: function (noticeId) {
    var url = getGlobalPathRoot() + "helm/hm001/hm001-info.gv?noticeId=" + noticeId;
    $.pdialog.open(url, "hm001-info", "大事记编辑", {mask: true, max: true});
    stopEvent(event);
  },


  /**
   * 打开添加页面
   */
  preView: function (noticeId) {
    var url = getGlobalPathRoot() + "helm/hm001/hm001-viewMain.gv?noticeId=" + noticeId;
    $.pdialog.open(url, "hm001-viewMain", "大事记编辑", {mask: true, max: false, height:768, width:1024});
    stopEvent(event);
  },

  /**
   * 保存公告信息
   */
  saveNotice: function () {
    var $form = $("#helm001Frm", $.pdialog.getCurrent());
    if ($form.valid()) {
      // 提交页面数据
      var url = getGlobalPathRoot() + "helm/hm001/hm001-save.gv";
      var ajaxPacket = new AJAXPacket(url);
      ajaxPacket.data.data = $form.serializeArray();
      core.ajax.sendPacketHtml(ajaxPacket, function (response) {
        var jsonData = eval("(" + response + ")");
        // 服务调用是否成功
        var isSuccess = jsonData.success;
        if (isSuccess) {
          $.pdialog.closeCurrent()
          alertMsg.correct("处理成功");
          $("#schBtn", navTab.getCurrentPanel()).trigger("click");
        } else {
          alertMsg.error(jsonData.errMsg);
        }
      });
    }
  },

  /**
   * 删除公告信息
   * @param obj
   */
  batchDelete: function (obj) {
    var noticeIds = new Array();
    var prjCds = new Array();
    $(obj).closest("div.hm001Div")
      .find(":checkbox[checked][name=noticeId]").each(
      function (i) {
        noticeIds.push($(this).val());
        prjCds.push(getPrjCd());
      }
    );
    if (noticeIds.length == 0) {
      alertMsg.warn("请选择要删除的记录");
    } else {
      alertMsg.confirm("您确定要删除记录吗?", {
        okCall: function () {
          var url = getGlobalPathRoot() + "helm/hm001/hm001-delete.gv";
          var ajaxPacket = new AJAXPacket(url);
          ajaxPacket.data.add("noticeIds", noticeIds.toString());
          ajaxPacket.data.add("prjCd", getPrjCd());
          core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
              alertMsg.correct("处理成功");
              $("#schBtn", navTab.getCurrentPanel()).trigger("click");
            } else {
              alertMsg.error(jsonData.errMsg);
            }
          });
        }
      });
    }
  },

  /**
   * 清空公告排序
   * @param obj
   */
  batchClear: function (obj) {
    var noticeIds = new Array();
    var prjCds = new Array();
    $(obj).closest("div.hm001Div")
      .find(":checkbox[checked][name=noticeId]").each(
      function (i) {
        noticeIds.push($(this).val());
        prjCds.push(getPrjCd());
      }
    );
    if (noticeIds.length == 0) {
      alertMsg.warn("请选择要清空的记录");
    } else {
      alertMsg.confirm("您确定要清空选中记录的排序吗?", {
        okCall: function () {
          var url = getGlobalPathRoot() + "helm/hm001/hm001-sort.gv";
          var ajaxPacket = new AJAXPacket(url);
          ajaxPacket.data.add("noticeIds", noticeIds.toString());
          ajaxPacket.data.add("prjCd", getPrjCd());
          core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
              alertMsg.correct("处理成功");
              $("#schBtn", navTab.getCurrentPanel()).trigger("click");
            } else {
              alertMsg.error(jsonData.errMsg);
            }
          });
        }
      });
    }
  },

  /**
   * 查询
   */
  query: function () {
    var formObj = $("#hm001form", navTab.getCurrentPanel());
    // 获取输入框
    var dialogObj = $("#hm001create", navTab.getCurrentPanel());
    // 覆盖的查询条件
    var coverConditionName = [];
    var coverCondition = [];
    var coverConditionValue = [];
    var coverNames = [];
    // 循环当前查询条件
    $("input", dialogObj).each(function () {
      var $this = $(this);
      var attrName = $this.attr("name");
      var tagName = $this[0].tagName;
      var condition = $this.attr("condition");
      var value = $this.val();
      if ("INPUT" == tagName && condition) {
        coverNames.push(attrName);
        if (value != "") {
          coverConditionName.push(attrName);
          coverCondition.push(condition);
          coverConditionValue.push(value);
        }
      }
    });
    // 获取历史查询条件
    var oldConditionName = $("input[name=conditionName]", formObj).val().split(",");
    var oldCondition = $("input[name=condition]", formObj).val().split(",");
    var oldConditionValue = $("input[name=conditionValue]", formObj).val().split(",");

    // 实际使用的查询条件
    var newConditionName = [];
    var newCondition = [];
    var newConditionValue = [];

    // 处理历史查询条件，存在覆盖则删除原有的查询条件
    for (var i = 0; i < oldConditionName.length; i++) {
      var conditionName = oldConditionName[i];
      var found = false;
      for (var j = 0; j < coverNames.length; j++) {
        if (conditionName == coverNames[j]) {
          found = true;
          break;
        }
      }
      // 未找到匹配的条件
      if (!found) {
        newConditionName.push(oldConditionName[i]);
        newCondition.push(oldCondition[i]);
        newConditionValue.push(oldConditionValue[i]);
      }
    }

    // 追加新的查询条件
    newConditionName = newConditionName.concat(coverConditionName);
    newCondition = newCondition.concat(coverCondition);
    newConditionValue = newConditionValue.concat(coverConditionValue);
    // 重新设置查询条件
    $("input[name=conditionName]", formObj).val(newConditionName.join(","));
    $("input[name=condition]", formObj).val(newCondition.join(","));
    $("input[name=conditionValue]", formObj).val(newConditionValue.join(","));
    // 再次执行查询
    Page.query($("#hm001form"), "");
  }

};

$(document).ready(function () {
  // 执行查询
  hm001.query();
});
