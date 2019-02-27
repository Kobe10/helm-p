var ph016 = {
    // 提交标签定义
    signDefJson: {
        "tagId": "",
        "tagName": "",
        "tagType": "",
        "isCheckbox": "",
        "subTags": [
            {
                "tagId": "",
                "tagName": ""
            }
        ]
    }
    , init: function () {
        // 单户时选中标签高亮
        var selectedJSON = ph016.getSelected();
        ph016.labelAddActive(selectedJSON);
        ph016.bindingEvent();
    }
    /**
     * 标签添加选中样式
     */
    , labelAddActive: function (selectedJSON) {
        if (!selectedJSON.tagIds) {
            return
        }
        $(".js-sign--label-value li.js-key-value").each(function () {
            if (selectedJSON.tagIds) {
                for (var i = 0; i < selectedJSON.tagIds.length; i++) {
                    if (selectedJSON.tagIds[i] === $(this).attr("tagid")) {
                        $(this).addClass("active")
                    }
                }
            }
        })
    }
    /**
     * 标签新建或编辑 id存在是编辑 id不存在是新建
     * @param id 标签id
     */
    , editorLabel: function (_this) {
        var labelBox = $(_this).closest("[class^='sign-label--']");
        var labelPraent = labelBox.find(".sign--label-key");
        var labelChild = labelBox.find(".sign--label-value li.js-key-value");

        var parentTagId = labelPraent.attr("tagId");
        var tagName = labelPraent.html();
        var tagType = labelPraent.attr("tagType");
        var isCheckbox = labelPraent.attr("isCheckbox");

        var subTags = [];
        labelChild.each(function () {
            var tagId = $(this).attr("tagid")
            subTags.push({
                "tagId": tagId,
                "tagName": $(this).html()
            });
        });

        // 如果是新增
        if ($(_this).attr("tagType")) {
            parentTagId = "";
            tagName = "";
            tagType = $(_this).attr("tagType");
            isCheckbox = "false";
            subTags = [];
        }

        var signDefJson = {
            "tagId": parentTagId,
            "tagName": tagName,
            "tagType": tagType,
            "isCheckbox": isCheckbox,
            "subTags": subTags
        };
        ph016.initDialog(signDefJson);
    }

    /**
     * 初始化弹框
     */
    , initDialog: function (signDefJson) {
        ph016.signDefJson = signDefJson;
        $(".parent-tagname").val(signDefJson.tagName);
        $(".parent-tagname").attr("tagid", signDefJson.tagId)
        $(".parent-tagname").attr("tagType", signDefJson.tagType)

        // 给多选框添加选中效果
        if (signDefJson.isCheckbox == "true") {
            $("input[name='isCheckbox']").attr("checked", true)
        } else {
            $("input[name='isCheckbox']").attr("checked", false)
        }

        if (signDefJson.subTags.length > 0) {
            for (var i = 0; i < signDefJson.subTags.length; i++) {
                ph016.createInputDom(signDefJson.subTags[i])
            }
        } else {
            ph016.createInputDom({tagId: "", tagName: ""})
        }

        // 绑定事件
        ph016.bindingEventDialog();
        // 显示弹框
        ph016.openSignDialog(signDefJson);
    }
    /**
     * 生成标签值输入dom元素
     * @param signDefJson
     */
    , createInputDom: function (json, dom) {
        var tempdom = "<div class=\"label-value mar-t10 js-label-value\">\n" +
            "                        <input class=\"input tagname textInput\" tagid=\"" + json.tagId + "\" type=\"text\" value=\"" + json.tagName + "\">\n" +
            "                        <svg class=\"js-dialog--label-add icon\" aria-hidden=\"true\">\n" +
            "                            <use xlink:href=\"#icon-jiahaocu\"></use>\n" +
            "                        </svg>\n" +
            "                        <svg class=\"js-dialog--label-delete icon\" aria-hidden=\"true\">\n" +
            "                            <use xlink:href=\"#icon-jianhaocu\"></use>\n" +
            "                        </svg>\n" +
            "                    </div>";
        if (dom && dom.length) {
            dom.after(tempdom)
        } else {
            $(".dialog-value").append(tempdom)
        }
    }
    /**
     * 生成 已选标签元素
     */
    , createLabelDom: function (json) {
        if (!json.tagId) {
            return
        }
        var labelKey = $(".sign--label-key[tagid='" + json.tagId + "']");
        if (labelKey.length > 0) {
            var oldIsCheckBox = labelKey.attr("ischeckbox");
            if (oldIsCheckBox != json.isCheckbox) {
                labelKey.attr("ischeckbox", json.isCheckbox);
            }
            labelKey.html(json.tagName);

            var selectedLabel = $(".js-selected--label span.js-selected--label-key[tagid='" + json.tagId + "']");
            if (selectedLabel.length > 0) {
                selectedLabel.html(json.tagName)
            }

            var tempdom = ""
            for (var i = 0; i < json.SubTags.Tag.length; i++) {
                tempdom = tempdom + "<li class=\"js-key-value\" title='" + json.SubTags.Tag[i].tagName + "' tagid=\"" + json.SubTags.Tag[i].tagId + "\">" + json.SubTags.Tag[i].tagName + "</li>"
            }
            tempdom = tempdom + "<li class=\"sign--label-edit js-editor-label\">编辑标签</li></ul>"
            $(".sign--label-key[tagid='" + json.tagId + "']").siblings(".js-sign--label-value").html(tempdom)
        } else {
            var tempdom = "<div class=\"sign-label--private\">" +
                "<div class=\"sign--label-key\" tagid=\"" + json.tagId + "\" tagtype=\"" + json.tagType + "\" ischeckbox=\"" + json.isCheckbox + "\">" + json.tagName + "</div>"
            for (var i = 0; i < json.SubTags.Tag.length; i++) {
                if (i == 0) {
                    tempdom = tempdom + "<ul class=\"sign--label-value js-sign--label-value\">"
                }
                tempdom = tempdom + "<li class=\"js-key-value\" title='" + json.SubTags.Tag[i].tagName + "' tagid=\"" + json.SubTags.Tag[i].tagId + "\">" + json.SubTags.Tag[i].tagName + "</li>"
                if (i == json.SubTags.Tag.length - 1) {
                    tempdom = tempdom + "<li class=\"sign--label-edit js-editor-label\">编辑标签</li></ul>"
                }
            }
            if (json.tagType == "1") {
                $(".sign-private--content").append(tempdom)
            } else {
                $(".sign-system--content").append(tempdom)
            }
        }
        var selectedJSON = ph016.getSelected();
        ph016.labelAddActive(selectedJSON);
    }
    /**
     *
     */
    , deleteLabelDom: function () {

    }
    /**
     * 增加已选标签
     * @param json {tagId:'',tagName:'',parentTagId:"",parentTagName:''}
     * @param _this 选中效果的  JQuery  dom元素
     */
    , addSelectedLabel: function (json) {
        var tagId = json.tagId;
        var tagName = json.tagName;
        var parentTagId = json.parentTagId;
        var parentTagName = json.parentTagName;
        var isCheckbox = json.isCheckbox;

        var selectLabel = $("[class^='js-selected--label'][tagid='" + tagId + "']");
        var selectLabelBox = $(".js-selected--label-key[tagid='" + parentTagId + "']").closest(".js-selected--label");

        // 如果在已选标签中存在 则返回
        if (selectLabel.length > 0) {
            return null
        }
        if (selectLabelBox.length > 0) {
            var tempdom = "<span class=\"js-selected--label-value\" tagid=\"" + tagId + "\">" + tagName + "</span>"
            if (isCheckbox == "false") {
                selectLabelBox.find(".js-selected--label-value").remove()
            }
            selectLabelBox.find(".js-selected--label-delete").before(tempdom)
            return true
        } else {
            var tempdom = " <div class=\"sign-selected--label js-selected--label\">\n" +
                "            <span class=\"js-selected--label-key\" tagid=\"" + parentTagId + "\">" + parentTagName + "</span>\n" +
                "            <span class=\"js-selected--label-value\" tagid=\"" + tagId + "\">" + tagName + "</span>\n" +
                "            <svg class=\"js-selected--label-delete icon\" aria-hidden=\"true\">\n" +
                "                <use xlink:href=\"#icon-dustbin_icon\"></use>\n" +
                "            </svg>\n" +
                "        </div>"
            $(".sign-selected--content").append(tempdom)
            return true
        }
        return null
    }
    /**
     * 仅适用于编辑标签中删除已选标签
     * @param tagId
     */
    , deleteSelectedLabel: function (tagId) {

        var selectLabel = $("[class^='js-selected--label'][tagid='" + tagId + "']");
        if (selectLabel.length < 0) {
            return null
        }
        var selectLabelBox = selectLabel.closest(".js-selected--label");

        if (selectLabel.hasClass("js-selected--label-key")) {
            selectLabelBox.remove();
            return true
        }
        if (selectLabelBox.find(".js-selected--label-value").length <= 1) {
            selectLabelBox.remove();
            return true
        } else {
            selectLabel.remove();
            return true
        }
    }
    /**
     * 弹框绑定事件
     */
    , bindingEventDialog: function () {
        // 移除事件委托
        $("div.js-label-value").unbind();
        // 绑定添加事件
        $("div.js-label-value").on("click", ".js-dialog--label-add", function () {
            ph016.createInputDom({tagId: "", tagName: ""}, $(this).closest("div.js-label-value"));
            ph016.bindingEventDialog();
        })
        // 绑定删除事件
        $("div.js-label-value").on("click", ".js-dialog--label-delete", function () {
            var valueBox = $(this).closest("div.js-label-value");
            var tagId = valueBox.find("input").attr("tagid");
            if (tagId) {
                if ($("div.js-label-value input[tagid!='']").length > 1) {
                    // 调用删除标签服务
                    var deleteLabel = $(".js-sign--label-value .js-key-value[tagid='" + tagId + "']");
                    ph016.deleteSignDef(tagId, [deleteLabel, valueBox], false);
                }
            } else {
                if ($("div.js-label-value").length > 1) {
                    valueBox.remove()
                }
            }
        })
    }
    /**
     * 绑定事件
     */
    , bindingEvent: function () {
        $(".js-sign-content").on("click", ".js-editor-label", function (event) {
            ph016.editorLabel(this)
        });
        $("span.js-editor-label").on("click", function (event) {
            ph016.editorLabel(this)
        });
        $(".js-sign-dalog--apply").on("click", function () {
            ph016.saveSignDef()
        });
        $(".js-sign-dalog--delete").on("click", function () {
            var parentTagId = $(this).closest(".sign-dialog--btns").siblings(".sign-dialog--content").find(".parent-tagname").attr("tagid")
            var dom = $(".sign--label-key[tagid='" + parentTagId + "']").closest("[class^='sign-label--']");
            alertMsg.confirm("删除后不可恢复且立即体现在全部数据中，你确定要删除?", {
                okCall: function () {
                    ph016.deleteSignDef(parentTagId, [dom], true)
                }
            });
        });

        $(".js-sign-content").on("click", "li.js-key-value", function () {
            var tagId = $(this).attr('tagid');
            var tagName = $(this).html();
            var parentTagId = $(this).closest("[class^='sign-label--']").find(".sign--label-key").attr("tagid");
            var isCheckbox = $(this).closest("[class^='sign-label--']").find(".sign--label-key").attr("ischeckbox");
            var parentTagName = $(this).closest("[class^='sign-label--']").find(".sign--label-key").html();

            if ($(this).hasClass("active")) {
                var deleteType = ph016.deleteSelectedLabel(tagId);
                if (deleteType) {
                    $(this).removeClass("active");
                }
            } else {
                var addType = ph016.addSelectedLabel({
                    tagId: tagId,
                    tagName: tagName,
                    parentTagId: parentTagId,
                    parentTagName: parentTagName,
                    isCheckbox: isCheckbox
                });
                if (addType) {
                    if (isCheckbox == "false") {
                        $(this).siblings(".js-key-value").removeClass("active")
                    }
                    $(this).addClass("active");
                }
            }
        });

        $(".sign-selected--content").on("click", ".js-selected--label-delete", function () {
            $(this).closest(".js-selected--label").remove();
            var parentTagId = $(this).siblings(".js-selected--label-key").attr("tagid")
            $(".sign--label-key[tagid='" + parentTagId + "']").siblings(".sign--label-value").find("li.js-key-value").removeClass("active")
        })
    }
    /**
     * 销毁弹框
     */
    ,
    destroyDialog: function () {
        $(".js-label-value").remove();
        ph016.closeSignDialog()
    }

    /**
     * 显示弹框
     */
    , openSignDialog: function (signDefJson) {
        if (signDefJson.tagId) {
            $(".js-sign-dalog--delete").show();
        } else {
            $(".js-sign-dalog--delete").hide();
        }
        $(".sign-dialog-box").show();
    }
    /**
     * 隐藏弹框
     */
    ,
    closeSignDialog: function () {
        $(".sign-dialog-box").hide();
    }
    /**
     * 根据已选则dom生成 服务json
     */
    , getSelected: function () {
        var temp = {
            "bindingFlag": "",
            "relIds": [],
            "tagIds": []
        }
        $(".js-selected--label").each(function () {
            $(this).find("[class='js-selected--label-value']").each(function () {
                if ($(this).attr("tagid")) {
                    temp.tagIds.push($(this).attr("tagid"))
                }
            })
        })
        var signType = $("input[name='signType']").val()

        if (signType === '2') {
            navTab.getCurrentPanel().find(":checkbox[checked][name=hsId]").each(
                function (i) {
                    temp.relIds.push($(this).val());
                }
            );
        } else {
            var houseId = $("input[name='hsId']", navTab.getCurrentPanel()).val() ? $("input[name='hsId']", navTab.getCurrentPanel()).val() : null;
            if (houseId) {
                temp.relIds.push(houseId)
            }
        }
        return temp
    }
    /**
     * 获取标签编辑弹框数据
     */
    , getDialogJson: function () {
        var tempJSON = {
            "tagId": "传-修改 不传-新增",
            "tagName": "",
            "tagType": "0",
            "isCheckbox": "false",
            "subTags": []
        }
        var parentTagId = $(".parent-tagname").attr("tagid") ? $(".parent-tagname").attr("tagid") : "";
        var tagType = $(".parent-tagname").attr("tagType") ? $(".parent-tagname").attr("tagType") : "";
        tempJSON.tagId = parentTagId;
        tempJSON.tagType = tagType;
        tempJSON.tagName = $(".parent-tagname").val();
        if ($("input[name='isCheckbox']").is(':checked')) {
            tempJSON.isCheckbox = "true";
        }
        $(".dialog-value").find(".tagname ").each(function () {
            var tagName = $(this).val();
            if (tagName) {
                var temp = {
                    "tagId": $(this).attr("tagId") ? $(this).attr("tagId") : "",
                    "tagName": tagName
                }
                tempJSON.subTags.push(temp)
            }

        })
        return tempJSON;
    }
//    ---------------调用服务--------------
    /**
     * 调用弹框标签提交服务
     * @param {"tagId": "传-修改 不传-新增",
     *         "tagName": "",
     *         "tagType": "0-系统级 1-个人级",
     *         "isCheckbox":"true-多选 false-不多选",
     *         "subTags": [
     *              {"tagId": "传-修改 不传-新增","tagName": ""},
     *              {"tagId": "传-修改 不传-新增","tagName": ""}
     *          ]
     *         }
     */
    , saveSignDef: function () {
        var paramJSON = ph016.getDialogJson();
        var url = getGlobalPathRoot() + "/eland/ph/ph016/ph016-saveSignDef.gv?prjCd=" + getPrjCd();
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("request", JSON.stringify(paramJSON))
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = JSON.parse(response)
            if (jsonData.isSuccess) {
                ph016.createLabelDom(JSON.parse(jsonData.opResult).Tag)
                ph016.destroyDialog();
            } else {
                alertMsg.warn(jsonData.errMsg)
            }
        })
    }
    /**
     * 删除标签  传子 tagid删除值，子tagid是最后一个，也会删除整个标签；传父 tagid 删除整个标签
     * @param tagId 删除标签的tagId
     * @param domArry [Arry] 删除标签后要删除的 dom 元素
     */
    , deleteSignDef: function (tagId, domArry, isCloseDialog) {
        if (!tagId) {
            return
        }
        var url = getGlobalPathRoot() + "eland/ph/ph016/ph016-deleteSignDef.gv?prjCd=" + getPrjCd()
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("tagId", tagId);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.isSuccess) {

                // 删除标签
                if (domArry) {
                    for (var i = 0; i < domArry.length; i++) {
                        if (domArry[i].length > 0) {
                            domArry[i].remove();
                        }
                    }
                }
                if (isCloseDialog) {
                    ph016.destroyDialog();
                }

                // 删除已选标签
                ph016.deleteSelectedLabel(tagId);
                // 删除标签
                ph016.deleteLabelDom(tagId);
            } else {
                alertMsg.error(jsonData.errMsg)
            }
        }, true);
    }
    /**
     * 0-绑定、1-解绑、2-保存
     * @param {"bindingFlag":"0-绑定 1-解绑 2-保存","relIds":["",""],"tagIds":["",""]}
     */
    , relSignManager: function (bindingFlag) {
        if (!bindingFlag) {
            return
        }
        var signType = $("input[name='signType']").val()
        var url = getGlobalPathRoot() + "eland/ph/ph016/ph016-relSignManager.gv?prjCd=" + getPrjCd();
        var paramJSON = ph016.getSelected();
        paramJSON.bindingFlag = bindingFlag
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("request", JSON.stringify(paramJSON));
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            $.pdialog.closeCurrent();
            if (jsonData.isSuccess) {
                if (bindingFlag == '0') {
                    alertMsg.correct("绑定成功！");
                } else if (bindingFlag == '1') {
                    alertMsg.correct("解绑成功！");
                } else {
                    alertMsg.correct("保存成功！");
                }
                if (signType === '2') {
                    ph001.refleshBuild();
                }
            } else {
                alertMsg.error(jsonData.errMsg)
            }
        }, true);
    }
}