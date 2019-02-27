var oh004 = {
    /**
     * 执行查询操作
     */
    refleshData: function () {
        // 查询
        var viewModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
        var viewFilter = $("input.js_query_filter", navTab.getCurrentPanel()).val();
        var oh004Form = $("#oh004Form", navTab.getCurrentPanel());
        var oh004QSch = $("#oh004QSch", navTab.getCurrentPanel());
        var resultField = $("input[name=resultField]", oh004Form);
        var jsQueryFilter = $("input[name=jsQueryFilter]", oh004Form);

        // 覆盖的查询条件
        var coverConditionName = [];
        var coverCondition = [];
        var coverConditionValue = [];
        var coverNames = [];
        // 循环当前查询条件
        $("input", oh004QSch).each(function () {
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
        var oldConditionName = $("input[name=conditionName]", oh004Form).val().split(",");
        var oldCondition = $("input[name=condition]", oh004Form).val().split(",");
        var oldConditionValue = $("input[name=conditionValue]", oh004Form).val().split(",");

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

        // 改变结果展示内容
        var resultFieldVal = "";
        if (viewFilter == "f2") {
            resultFieldVal = "personName,personId,hsId,hsCtId,ctDate,buildFullAddr,buildType,buildSize";
        } else {
            resultFieldVal = "personName,personId,hsId,hsCtId,ctDate,buildFullAddr,buildType,buildSize";
        }
        jsQueryFilter.val(viewFilter);
        resultField.val(resultFieldVal);
        //将拼接的条件添加到 查询form里。
        $("input[name=conditionName]", oh004Form).val(newConditionName.join(","));
        $("input[name=condition]", oh004Form).val(newCondition.join(","));
        $("input[name=conditionValue]", oh004Form).val(newConditionValue.join(","));
        if (!viewModel) {
            viewModel = "m1";
        }
        if (viewModel == "m1") {
            Page.query(oh004Form, "");
        } else {
            // 重新加载
            var packet = new AJAXPacket();
            packet.data.add("prjCd", getPrjCd());
            // 其他查询条件
            packet.data.add("conditionNames", $("input[name=conditionName]", oh004Form).val());
            packet.data.add("conditions", $("input[name=condition]", oh004Form).val());
            packet.data.add("conditionValues", $("input[name=conditionValue]", oh004Form).val());
            packet.data.add("viewFilter", viewFilter);

            // 查询条件
            packet.url = getGlobalPathRoot() + "eland/oh/oh004/oh004-initRpt.gv";
            core.ajax.sendPacketHtml(packet, function (response) {
                $("#oh004_div", navTab.getCurrentPanel()).html(response);
                initUI($("#oh004_div", navTab.getCurrentPanel()));
            });
        }
    },

    /**
     * 切换展示视图
     */
    changeShowModel: function (obj, model) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        if (currentModel == model) {
            return;
        }
        // 修改模式
        currentModelObj.val(model);
        $this.find("span").addClass("active");
        $this.siblings("li.model").find("span.active").removeClass("active");
        // 执行查询
        oh004.refleshData();
    },

    /**
     * 切换统计 过滤指标
     */
    changeShowFilter: function (obj, filter) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_filter", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        var oh004Qsch = $("#oh004QSch", navTab.getCurrentPanel());
        if (currentModel == filter) {
            //点击本身去掉过滤条件
            currentModelObj.val("");
            $this.find("span").removeClass("active");
            $this.siblings("li.filter").find("span").removeClass("active");
            //
            $("input[isHkWq=isHkWq]", oh004Qsch).val("");
            $("input.autocomplete", oh004Qsch).val("");
        } else {
            if ("f2" == filter) {
                $("input[isHkWq=isHkWq]", oh004Qsch).val("1");
                $("input.autocomplete", oh004Qsch).val("是");
            } else {
                $("input[isHkWq=isHkWq]", oh004Qsch).val("0");
                $("input.autocomplete", oh004Qsch).val("否");
            }
            // 修改模式
            currentModelObj.val(filter);
            $this.find("span").addClass("active");
            $this.siblings("li.filter").find("span.active").removeClass("active");
        }
        oh004.refleshData();
    },

    /**
     * 快速检索对话框打开
     */
    openQSch: function (clickObj) {
        var active = $("#oh004QSch", navTab.getCurrentPanel()).attr("active");
        if ("on" != active) {
            $("#oh004QSch", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#oh004QSch input:eq(0)", navTab.getCurrentPanel()).focus();
        } else {
            $("#oh004QSch", navTab.getCurrentPanel()).hide().attr("active", "");
        }
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#oh004QSch", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = false;
            } else {
                close = true;
            }
        }
        if (close) {
            $("#oh004QSch", navTab.getCurrentPanel()).hide().attr("active", "");
        }
    },

    /**
     * 打开户口外迁处理页面
     */
    extDeal: function (personId, isHkWq) {
        var url = getGlobalPathRoot() + "eland/oh/oh004/oh004-extDeal.gv?prjCd=" + getPrjCd()
            +"&personId=" + personId+"&isHkWq="+ isHkWq;
        $.pdialog.open(url, "oh004Wqcl", "户口外迁处理",
            {mask: true, max: false, maxable: true, resizable: true, width: 800, height: 480}
        );
        stopEvent(event);
    },
    /**
     * 保存户口外迁处理
     */
    saveWq:function(){
        var url = getGlobalPathRoot() + "eland/oh/oh004/oh004-saveWq.gv";
        var $form = $("#oh004WqForm", $.pdialog.getCurrent());
        var packet = new AJAXPacket(url);
        packet.data.data = $form.serializeArray();
        packet.data.data.push({name: "prjCd", value: getPrjCd()});
        core.ajax.sendPacketHtml(packet,function(response){
            var jsonData = eval("("+response+")");
            if(jsonData.success){
                alertMsg.correct("处理成功");
                $.pdialog.closeCurrent();
                oh004.refleshData();
            }else{
                alertMsg.error(jsonData.error);
            }
        })
    }

};

