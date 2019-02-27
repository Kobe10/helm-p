function initEnv() {
  $("body").append(DWZ.frag["dwzFrag"]);

  if ($.browser.msie && /6.0/.test(navigator.userAgent)) {
    try {
      document.execCommand("BackgroundImageCache", false, true);
    } catch (e) {
    }
  }
  //清理浏览器内存,只对IE起效
  if ($.browser.msie) {
    window.setInterval("CollectGarbage();", 10000);
  }

  $(window).resize(function () {
    initLayout();
    $(this).trigger(DWZ.eventType.resizeGrid);
  });

  var ajaxbg = $("#background,#progressBar");
  ajaxbg.hide();

  if ($.fn.jBar) {
    $("#leftside").jBar({minW: 150, maxW: 700});
  }

  if ($.taskBar) $.taskBar.init();
  navTab.init();
  if ($.fn.switchEnv) $("#switchEnvBox").switchEnv();

  if ($.fn.navMenu) $("#navMenu").navMenu();
  setTimeout(function () {
    initLayout();
    initUI();
    // navTab styles
    var jTabsPH = $("div.tabsPageHeader");
    jTabsPH.find(".tabsLeft").hoverClass("tabsLeftHover");
    jTabsPH.find(".tabsRight").hoverClass("tabsRightHover");
    jTabsPH.find(".tabsMore").hoverClass("tabsMoreHover");
  }, 10);
}
function initLayout() {
  var iContentW = $(window).width() - (DWZ.ui.sbar ? $("#sidebar").width() + 10 : 34) - 5;
  var iContentH = $(window).height() - $("#header").height() - $("#top").height() + 3;
  $("#container").width(iContentW);
  var tabsPageHeader = $("#container .tabsPageHeader");
  var tabsPageHeaderHeight = 40;
  if (tabsPageHeader.hasClass("hidden")) {
    tabsPageHeaderHeight = 0;
  }
  $("#container .tabsPageContent").height(iContentH - tabsPageHeaderHeight).find("[layoutH]").layoutH();
  $("#sidebar, #sidebar_s .collapse, #splitBar, #splitBarProxy").height(iContentH - 5);
  $("#taskbar").css({top: iContentH + $("#header").height() + 5, width: $(window).width()});
}

function initUI(_box) {
  var $p = $(_box || document);

  $("div.panel", $p).jPanel();

  //tables
  $("table.table", $p).jTable();

  // css tables
  $('table.list', $p).cssTable();

  //auto bind tabs
  $("div.tabs", $p).each(function () {
    var $this = $(this);
    var options = {};
    options.currentIndex = $this.attr("currentIndex") || 0;
    options.eventType = $this.attr("eventType") || "click";
    $this.tabs(options);
  });

  $p.undelegate("div.img-wrap label span", "click");
  $p.delegate("div.img-wrap label span.remove", "click", function (e) {
      var packet = new AJAXPacket();
      var docId = $(this).parent().find("input[type=hidden][name=docId]").val();
      //增加删除确认提醒
      packet.url = getGlobalPathRoot() + "oframe/common/file/file-delete.gv?docId=" + docId;
      var _this = $(this);
      alertMsg.confirm("确定删除该信息吗？", {
        okCall: function () {
          core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
              _this.parent("label").remove();
//                            console.log(_this.parent().html());
            } else {
              alertMsg.error("删除失败！");
            }
          });
        }
      });
    }
  );
  $p.undelegate("div.img-wrap img", "click");
  $p.delegate("div.img-wrap img", "click", function (e) {
    stopEvent(e);
    var _this = $(this);
    var docName = _this.parent().find("input[type=hidden][name=docName]").val();
    if (docName && !docName.isImg()) {
      $(this).parent().find("a span").click();
      return;
    }
    // 获取可以展示的图片地址列表和当前的展示位置
    // 实际图片地址
    var currentSrc = _this.attr("realSrc");
    // 所有图片地址
    var imgSrcArr = [];
    var currentIdx = -1;
    var totalCount = 0;
    _this.parents("div.img-wrap").find("img").each(function (idx, obj) {
      var realSrc = $(obj).attr("realSrc");
      if (realSrc && realSrc != '') {
        totalCount++;
        imgSrcArr.push(realSrc);
        if (realSrc == currentSrc) {
          currentIdx = totalCount - 1;
        }
      }
    });
    var filter = _this.css("filter");
    var sInner = '<div class="imgShadow"></div><div class="imgDialog" id="imgDialog" allRealSrc="' + imgSrcArr.join(";") + '" currentIdx="' + currentIdx + '">'
      + '<a href="javascript:void(0)" class="close"></a>'
      + '<div class="btn btnLeft hidden" style="visibility: visible;"><a href="javascript:;" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a></div>'
      + '<img style="filter:' + filter + '" src="' + currentSrc + '">'
      + '<div class="btn btnRight hidden" style="visibility: visible;"><a href="javascript:;" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a></div>'
      + '<iframe transparent="transparent" style="position:absolute;top:0;left:0;width: 100%;height: 100%; z-index: 0;"></iframe></div>';
    $p.append(sInner);

    // 初始化显示内容
    $("#imgDialog div.btnLeft", $p).show();
    $("#imgDialog div.btnRight", $p).show();
    // 第一张
    if (currentIdx == 0 || currentIdx == -1) {
      $("#imgDialog div.btnLeft", $p).hide();
    }
    // 最后一张
    if (currentIdx == totalCount - 1 || currentIdx == -1) {
      $("#imgDialog div.btnRight", $p).hide();
    }

    $("#imgDialog div.btnRight", $p).click(function (event) {
      var currentIdx = parseInt($("#imgDialog").attr("currentIdx"));
      var imgSrcArr = $("#imgDialog").attr("allRealSrc").split(";");
      var totalCount = imgSrcArr.length;
      if (currentIdx == totalCount - 1) {
        currentIdx = currentIdx;
      } else {
        currentIdx++;
      }
      $("#imgDialog").attr("currentIdx", currentIdx);

      // 初始化显示内容
      $("#imgDialog div.btnLeft").show();
      $("#imgDialog div.btnRight").show();
      // 第一张
      if (currentIdx == 0) {
        $("#imgDialog div.btnLeft").hide();
      }
      // 最后一张
      if (currentIdx == totalCount - 1) {
        $("#imgDialog div.btnRight").hide();
      }
      //
      $("#imgDialog img").attr("src", imgSrcArr[currentIdx]);
    });

    $("#imgDialog div.btnLeft").click(function (event) {
      var currentIdx = parseInt($("#imgDialog").attr("currentIdx"));
      var imgSrcArr = $("#imgDialog").attr("allRealSrc").split(";");
      var totalCount = imgSrcArr.length;
      if (currentIdx <= 0) {
        currentIdx = 0;
      } else {
        currentIdx--;
      }
      $("#imgDialog").attr("currentIdx", currentIdx);
      // 初始化显示内容
      $("#imgDialog div.btnLeft").show();
      $("#imgDialog div.btnRight").show();
      // 第一张
      if (currentIdx == 0) {
        $("#imgDialog div.btnLeft").hide();
      }
      // 最后一张
      if (currentIdx == totalCount - 1) {
        $("#imgDialog div.btnRight").hide();
      }
      //
      $("#imgDialog img").attr("src", imgSrcArr[currentIdx]);
    });
    $("#imgDialog img", $p).one('load', function () {
      $("#imgDialog").css({width: $(this).width(), marginLeft: -($(this).width() / 2) + 'px'});
      if ($(this).height() >= $(window).height()) {
        $(this).height($(window).height() - 100).css("marginTop", '50px');
      }
    }).each(function () {
      if (this.complete) $(this).load();
    });

    // 绑定图片翻页事件
    $(document).bind("keydown", ImgPage.keyJump);
    $("#imgDialog .close", $p).click(function (e) {
      $(".imgShadow").remove();
      $("#imgDialog").remove();
      // 取消绑定图片翻页事件
      $(document).unbind("keydown", ImgPage.keyJump);
    });
  });

  $(".btn-more", $p).each(function () {
    $(this).bind("mouseover", function () {
      $(this).openBtnMenu({
        "offsetX": $(this).attr("offsetX"),
        "offsetY": $(this).attr("offsetY"),
        "btnContainer": $(this).attr("btnContainer")
      });
    });
    $(this).mouseout(function () {
      $.fn.deActiveBtnMenu();
    });
  });

  $(".js_more", $p).each(function () {
    $(this).bind("mouseover", function () {
      $(this).openBtnMenu({
        "offsetX": $(this).attr("offsetX"),
        "offsetY": $(this).attr("offsetY"),
        "openDirection": $(this).attr("openDirection")
      });
    });
    $(this).mouseout(function () {
      $.fn.deActiveBtnMenu();
    });
  });

// auto bind auto select
  $("input.autocomplete", $p).each(function () {
      var $this = $(this);
      var atArray = $(this).attr("atArray");
      var atOption = $(this).attr("atOption");
      var atUrl = $(this).attr("atUrl");
      if (atOption && !atOption == "") {
        atOption = eval(atOption)($this);
      } else {
        atOption = {};
      }
      if (atArray && !atArray == "") {
        atArray = eval(atArray)($this);
        $(this).autocomplete(atArray, atOption);
      } else if (atUrl && !atUrl == "") {
        atUrl = eval(atUrl)($this);
        $(this).autocomplete(atUrl, atOption);
      } else {
        $(this).autocomplete(atOption);
      }
    }
  );

// 初始化颜色选择器
  if ($.fn.colorPicker) {
    $(".color-pick", $p).colorPicker();
  }

//    $("ul.tree", $p).jTree();
  $('div.accordion', $p).each(function () {
    var $this = $(this);
    var extendAll = $this.attr("extendAll");
    if (extendAll) {
      //Get the width and height of each div
      $this.find(".accordionContent").each(function () {
        var _contentDiv = $(this);
        //Assignment, ease for use
        _contentDiv.attr("height", _contentDiv.height());
        _contentDiv.attr("width", _contentDiv.width());
      });
    } else {
      $this.accordion({fillSpace: $this.attr("fillSpace"), alwaysOpen: true, active: 0});
    }
  });

//    $(":button.checkboxCtrl, :checkbox.checkboxCtrl", $p).checkboxCtrl($p);
  $(":button.checkboxCtrl, :checkbox.checkboxCtrl", $p).click(function (event) {
    $this = $(this);
    var groups = $this.attr("group");
    var checked = $this.is(":checked") ? "all" : "none";
    $(this).closest("div.grid").find(":checkbox[group=" + groups + "]").each(
      function (i) {
        if (!$(this).attr("disabled")) {
          if ($(this).hasClass("checkboxCtrl")) {
            return true;
          } else if ("all" == checked) {
            $(this).attr('checked', "true");
          } else {
            $(this).removeAttr('checked');
          }
        }
      }
    );
  });

// init styles
  $("input[type=text], input[type=password], textarea", $p).addClass("textInput").focusClass("focus");
  $("input[readonly], textarea[readonly]", $p).addClass("readonly");
  $("input[disabled=true], textarea[disabled=true]", $p).addClass("disabled");
  $("input[type=text]", $p).not("div.tabs input[type=text]", $p).filter("[alt]").inputAlert();
//Grid ToolBar
  $("div.panelBar li, div.panelBar", $p).hoverClass("hover");

//Button
  $("div.button", $p).hoverClass("buttonHover");
  $("div.buttonActive", $p).hoverClass("buttonActiveHover");
//tabsPageHeader
  $("div.tabsHeader li, div.tabsPageHeader li, div.accordionHeader, div.accordion", $p).hoverClass("hover");
//validate form
  $("form.required-validate", $p).each(function () {
    var $form = $(this);
    $form.validate({
      onsubmit: false,
      focusInvalid: false,
      focusCleanup: true,
      errorElement: "span",
      ignore: ".ignore",
      invalidHandler: function (form, validator) {
        var errors = validator.numberOfInvalids();
        if (errors) {
          var message = DWZ.msg("validateFormError", [errors]);
          alertMsg.error(message);
        }
      }
    });
    $form.find('input[customvalid]').each(function () {
      var $input = $(this);
      $input.rules("add", {
        customvalid: $input.attr("customvalid")
      })
    });
  });
  if ($.fn.datepicker) {
    $('input.date', $p).each(function () {
      var $this = $(this);
      var opts = {};
      if ($this.attr("dateFmt")) opts.pattern = $this.attr("dateFmt");
      if ($this.attr("minDate")) opts.minDate = $this.attr("minDate");
      if ($this.attr("maxDate")) opts.maxDate = $this.attr("maxDate");
      if ($this.attr("mmStep")) opts.mmStep = $this.attr("mmStep");
      if ($this.attr("ssStep")) opts.ssStep = $this.attr("ssStep");
      $this.datepicker(opts);
    });
  }
  // link
  $("div.withTitle:not(.noClose)>h1", $p).each(function () {
    var _this = $(this);
    _this.unbind("click");
    _this.bind("click", function () {
      var clickObj = $(this);
      if (clickObj.hasClass("close")) {
        clickObj.nextAll().show();
        clickObj.removeClass("close");
        clickObj.addClass("open");
      } else {
        clickObj.nextAll().hide();
        clickObj.removeClass("open");
        clickObj.addClass("close");
      }
    });
    // 初始化隐藏
    if (_this.hasClass("close")) {
      _this.nextAll().hide();
    }
  });
  // link
  $("div.readonly", $p).each(function () {
    var _this = $(this);
    $("input:not(.write)", _this).addClass("readonly").attr("readonly", "readonly");
    $("input[type=checkbox]:not(.write)", _this).addClass("readonly").attr("disabled", "disabled");
    $("select:not(.write)", _this).addClass("readonly").attr("disabled", "disabled");
    $("textarea:not(.write)", _this).addClass("readonly").attr("readonly", "readonly");
  });
  // readonly样式问题
  $("input[readOnly]", $p).keydown(function (event) {
    if (event.target) {
      if ($(event.target).hasClass("readonly")) {
        stopEvent(event);
      }
    }
  });
  // navTab
  $("a[target=navTab]", $p).each(function () {
    $(this).click(function (event) {
      var $this = $(this);
      var title = $this.attr("title") || $this.text();
      var tabid = $this.attr("rel") || "_blank";
      var fresh = eval($this.attr("fresh") || "true");
      var external = eval($this.attr("external") || "false");
      var url = unescape($this.attr("href")).replaceTmById($(event.target).parents(".unitBox:first"));
      DWZ.debug(url);
      if (!url.isFinishedTm()) {
        alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
        return false;
      }
      navTab.openTab(tabid, url, {title: title, fresh: fresh, external: external});

      event.preventDefault();
    });
  });

  //dialogs
  $("a[target=dialog]", $p).each(function () {
    $(this).click(function (event) {
      var $this = $(this);
      var title = $this.attr("title") || $this.text();
      var rel = $this.attr("rel") || "_blank";
      var options = {};
      var w = $this.attr("width");
      var h = $this.attr("height");
      if (w) options.width = w;
      if (h) options.height = h;
      options.max = eval($this.attr("max") || "false");
      options.mask = eval($this.attr("mask") || "false");
      options.maxable = eval($this.attr("maxable") || "true");
      options.minable = eval($this.attr("minable") || "true");
      options.fresh = eval($this.attr("fresh") || "true");
      options.resizable = eval($this.attr("resizable") || "true");
      options.drawable = eval($this.attr("drawable") || "true");
      options.close = eval($this.attr("close") || "");
      options.param = $this.attr("param") || "";

      var url = unescape($this.attr("href")).replaceTmById($(event.target).parents(".unitBox:first"));
      DWZ.debug(url);
      if (!url.isFinishedTm()) {
        alertMsg.error($this.attr("warn") || DWZ.msg("alertSelectMsg"));
        return false;
      }
      $.pdialog.open(url, rel, title, options);

      return false;
    });
  });
  $("a[target=ajax]", $p).each(function () {
    $(this).click(function (event) {
      var $this = $(this);
      var rel = $this.attr("rel");
      if (rel) {
        var $rel = $("#" + rel);
        $rel.loadUrl($this.attr("href"), {}, function () {
          $rel.find("[layoutH]").layoutH();
        });
      }

      event.preventDefault();
    });
  });

  if ($.fn.sortDrag) $("div.sortDrag", $p).sortDrag();

// dwz.ajax.js
  if ($.fn.ajaxTodo) $("a[target=ajaxTodo]", $p).ajaxTodo();
  if ($.fn.dwzExport) $("a[target=dwzExport]", $p).dwzExport();

  if ($.fn.lookup) $("a[lookupGroup]", $p).lookup();
  if ($.fn.multLookup) $("[multLookup]:button", $p).multLookup();
  if ($.fn.suggest) $("input[suggestFields]", $p).suggest();
  if ($.fn.itemDetail) $("table.itemDetail", $p).itemDetail();
  if ($.fn.selectedTodo) $("a[target=selectedTodo]", $p).selectedTodo();
  if ($.fn.pagerForm) $("form[rel=pagerForm]", $p).pagerForm({parentBox: $p});

  // 这里放其他第三方jQuery插件...
  $p.find("[layoutH]").layoutH();

  if (window.UE && UE.ui) {
    $("textarea.editor", $p).each(function () {
      var $this = $(this);
      var edtOpt = $this.attr("edtOpt");
      var simpleEditor = $this.hasClass("simpleEditor");
      var option = {};
      if (edtOpt) {
        option = eval('(' + edtOpt + ')');
      }
      if (simpleEditor) {
        var simpleOption = {
          minFrameHeight: 300,
          toolbars: [
            ['source', 'undo', 'redo', 'bold', 'italic', 'underline', '|',
              'customstyle', 'paragraph', 'fontfamily', 'fontsize']
          ]
        };
        $.extend(option, simpleOption);
      }
      var editor = new UE.ui.Editor(option);
      editor.render(this);
    });
  }

  // 回车进入下一个元素
  $("form.entermode", $p).find("input,select").bind("keydown", function () {
    var e = event || window.event;
    if (e.keyCode == 13 && e.srcElement.type != 'button'
      && e.srcElement.type != 'submit' && e.srcElement.type != 'reset'
      && e.srcElement.type != 'hidden' && e.srcElement.type != 'textarea'
      && e.srcElement.type != 'radio' && e.srcElement.type != 'file'
      && e.srcElement.type != '') {
      e.keyCode = 9;
    }
  });

  // 分类表头,点击折叠内容，再次点击展开。
  $(".accordionHeader", $p).bind("click", function () {
    var $this = $(this);
    var accordionContent = $this.next(".accordionContent");
    var h2Obj = $this.find(">h2");
    if (h2Obj.hasClass("collapsable")) {
      accordionContent.animate({
        height: '0px'
      });
      h2Obj.removeClass("collapsable");
    } else {
      h2Obj.addClass("collapsable");
      var oldHeight = accordionContent.attr("height");
      var oldWidth = accordionContent.attr("width");
      accordionContent.animate({
        width: oldWidth,
        height: oldHeight
      });
      accordionContent.find(">ul").width(oldWidth);
    }
  });

  $(":input.titleFormat", $p).each(function () {
    $(this).bind("mouseover", function () {
      TitleFormat.titleMouseOver(this, event);
    });
    $(this).mouseout(function () {
      TitleFormat.titleMouseOut(this);
    });
  });

  // input中radio和checkbox的处理
  $("label.for-label", $p).bind("click", function (event) {
    var targetType = $(event.target).attr("type");
    if (!event.forClick && targetType != "checkbox" && targetType != "radio") {
      var newEvent = jQuery.Event("click");
      newEvent.forClick = true;
      $("input[type=radio]", $(this)).trigger(newEvent);
      $("input[type=checkbox]", $(this)).trigger(newEvent);
    }
  });
}


