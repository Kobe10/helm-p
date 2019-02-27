page_input = {
    /** 筛选条件自动补全 */
    getScress: function () {
        return {
            fetchData: function (obj) {
                if ($.trim($(obj).val()) == "") {
                    return page_input.getAllLi();
                } else {
                    return page_input.getAllLabel(false);
                }
            },
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].chName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.chName;
            },
            displayValue: function (value, data) {
                return data.chName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            maxItemsToShow: 15,
            maxCacheLength: 30,
            sortResults: false,
            onItemSelect: function (obj) {
                var text = obj.data;
                var source = $(obj.source);
                var container = $("div.js_query_div", navTab.getCurrentPanel());
                $("label:contains(" + text.chName + ")", container).each(function () {
                    if ($(this).closest("span.rst_field").attr("entityattrnameen") == text.enValue) {
                        var $this = $(this);
                        var rstField = $this.closest("span.rst_field");
                        var condFieldName = rstField.attr("entityAttrNameEn");
                        var entityAttrNameCh = $("label.rst_field_name", rstField).html();
                        var matchLi = null;
                        if (rstField.length == 0) {
                            rstField = $this.closest("li.qry_cod");
                            condFieldName = rstField.attr("condFieldName");
                            entityAttrNameCh = rstField.attr("entityAttrNameCh");
                            matchLi = rstField;
                        } else {
                            $("ul.js_conditions_ul>li", container).each(function () {
                                var _this = $(this);
                                var liCondFieldName = _this.attr("condFieldName");
                                if (liCondFieldName == condFieldName) {
                                    matchLi = _this;
                                    return false;
                                }
                            });
                        }
                        var conditionNames = [];
                        var conditions = [];
                        var conditionValues = [];
                        // 检索条件显示
                        var conditionsDisp = "";
                        if (!matchLi) {
                            matchLi = $("ul.js_conditions_ul", container).find("li:eq(0)").clone();
                            matchLi.show();
                            $("ul.js_conditions_ul", container).append(matchLi);
                            // 设置检索条件内容
                            matchLi.attr("condFieldName", condFieldName);
                            matchLi.attr("conditionName", conditionNames.join(" "));
                            matchLi.attr("condition", conditions.join(" "));
                            matchLi.attr("conditionValue", conditionValues.join(" "));
                            matchLi.attr("entityAttrNameCh", entityAttrNameCh);
                            $("span", matchLi).html(entityAttrNameCh + "：" + conditionsDisp)
                        }
                        /**
                         * 选中后清空input里内容
                         **/
                        $(".pull-empty", container).attr("value", "");
                        // 自动调用click事件，跳出隐藏框
                        $("span", matchLi).trigger("click");

                        /**
                         * 筛选条件点击显示隐藏框，点击取消关闭
                         **/
                        container.delegate("button.js_cle_con", "click", function () {
                            stopEvent(event);
                            var $this = $(this);
                            var liObj = $this.closest("li");
                            liObj.remove();
                        });
                    }
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
            }
        }
    },
    /*筛选条件自动补全*/
    getPsScress: function () {
        return {
            fetchData: function () {
                return page_input.getPsLabel(false);
            },
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].chName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.chName;
            },
            displayValue: function (value, data) {
                return data.chName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            maxItemsToShow: 15,
            maxCacheLength: 30,
            sortResults: false,
            onItemSelect: function (obj) {
                var text = obj.data;
                var source = $(obj.source);
                var container = $("div.js_query_div", navTab.getCurrentPanel());
                $("label:contains(" + text.chName + ")", container).each(function () {
                    if ($(this).closest("span.rst_ps_field").attr("entityattrnameen") == text.enValue) {
                        var $this = $(this);
                        var rstField = $this.closest("span.rst_ps_field");
                        var condFieldName = rstField.attr("entityAttrNameEn");
                        var entityAttrNameCh = $("label.rst_ps_field_name", rstField).html();
                        var matchLi = null;
                        if (rstField.length == 0) {
                            rstField = $this.closest("li.qry_ps_cod");
                            condFieldName = rstField.attr("condFieldName");
                            entityAttrNameCh = rstField.attr("entityAttrNameCh");
                            matchLi = rstField;
                        } else {
                            $("ul.js_ps_conditions_ul>li", container).each(function () {
                                var _this = $(this);
                                var liCondFieldName = _this.attr("condFieldName");
                                if (liCondFieldName == condFieldName) {
                                    matchLi = _this;
                                    return false;
                                }
                            });
                        }
                        var conditionNames = [];
                        var conditions = [];
                        var conditionValues = [];
                        // 检索条件显示
                        var conditionsDisp = "";
                        if (!matchLi) {
                            matchLi = $("ul.js_ps_conditions_ul", container).find("li:eq(0)").clone();
                            matchLi.show();
                            $("ul.js_ps_conditions_ul", container).append(matchLi);
                            // 设置检索条件内容
                            matchLi.attr("condFieldName", condFieldName);
                            matchLi.attr("conditionName", conditionNames.join(" "));
                            matchLi.attr("condition", conditions.join(" "));
                            matchLi.attr("conditionValue", conditionValues.join(" "));
                            matchLi.attr("entityAttrNameCh", entityAttrNameCh);
                            $("span", matchLi).html(entityAttrNameCh + "：" + conditionsDisp)
                        }
                        /**
                         * 选中后清空input里内容
                         **/
                        $(".pull-empty", container).attr("value", "");
                        // 自动调用click事件，跳出隐藏框
                        $("span", matchLi).trigger("click");

                        /**
                         * 筛选条件点击显示隐藏框，点击取消关闭
                         **/
                        container.delegate("button.js_cle_con", "click", function () {
                            stopEvent(event);
                            var $this = $(this);
                            var liObj = $this.closest("li");
                            liObj.remove();
                        });
                    }
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
            }
        }
    },

    /*筛选条件自动补全*/
    getTagScress: function () {
        return {
            fetchData: function () {
                return page_input.getTagLabel(false);
            },
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].chName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.chName;
            },
            displayValue: function (value, data) {
                return data.chName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            maxItemsToShow: 15,
            maxCacheLength: 30,
            sortResults: false,
            onItemSelect: function (obj) {
                var text = obj.data;
                var source = $(obj.source);
                var container = $("div.js_query_div", navTab.getCurrentPanel());
                $("label:contains(" + text.chName + ")", container).each(function () {
                    if ($(this).closest("span.rst_tag_field").attr("entityattrnameen") == text.enValue) {
                        var $this = $(this);
                        var rstField = $this.closest("span.rst_tag_field");
                        var condFieldName = rstField.attr("entityAttrNameEn");
                        var entityAttrNameCh = $("label.rst_tag_field_name", rstField).html();
                        var matchLi = null;
                        if (rstField.length == 0) {
                            rstField = $this.closest("li.qry_tag_cod");
                            condFieldName = rstField.attr("condFieldName");
                            entityAttrNameCh = rstField.attr("entityAttrNameCh");
                            matchLi = rstField;
                        } else {
                            $("ul.js_tag_conditions_ul>li", container).each(function () {
                                var _this = $(this);
                                var liCondFieldName = _this.attr("condFieldName");
                                // 支持相同标签不同条件检索
                                // if (liCondFieldName == condFieldName) {
                                //     matchLi = _this;
                                //     return false;
                                // }
                            });
                        }
                        var conditionNames = [];
                        var conditions = [];
                        var conditionValues = [];
                        // 检索条件显示
                        var conditionsDisp = "";
                        if (!matchLi) {
                            matchLi = $("ul.js_tag_conditions_ul", container).find("li:eq(0)").clone();
                            matchLi.show();
                            $("ul.js_tag_conditions_ul", container).append(matchLi);
                            // 设置检索条件内容
                            matchLi.attr("condFieldName", condFieldName);
                            matchLi.attr("conditionName", conditionNames.join(" "));
                            matchLi.attr("condition", conditions.join(" "));
                            matchLi.attr("conditionValue", conditionValues.join(" "));
                            matchLi.attr("entityAttrNameCh", entityAttrNameCh);
                            $("span", matchLi).html(entityAttrNameCh + "：" + conditionsDisp)
                        }
                        /**
                         * 选中后清空input里内容
                         **/
                        $(".pull-empty", container).attr("value", "");
                        // 自动调用click事件，跳出隐藏框
                        $("span", matchLi).trigger("click");

                        /**
                         * 筛选条件点击显示隐藏框，点击取消关闭
                         **/
                        container.delegate("button.js_cle_con", "click", function () {
                            stopEvent(event);
                            var $this = $(this);
                            var liObj = $this.closest("li");
                            liObj.remove();
                        });
                    }
                });
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
            }
        }
    },

    /*内容检索自动补全*/
    getRetrieval: function () {
        return {
            fetchData: function () {
                return page_input.getAllLabel(true);
            },
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].chName;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.chName;
            },
            displayValue: function (value, data) {
                return data.chName;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            maxItemsToShow: 30,
            maxCacheLength: 30,
            sortResults: false,
            onItemSelect: function (obj) {
                var text = obj.data;
                var container = $("div.js_query_div", navTab.getCurrentPanel());
                var matchSpan = null;
                $("span.rst_field", container).each(function () {
                    var $this = $(this);
                    if ($this.attr("entityAttrNameEn") == text.enValue) {
                        matchSpan = $this;
                        return false;
                    }
                });
                if (matchSpan && !matchSpan.hasClass("selected")) {
                    matchSpan.find("label.rst_field_slt").trigger("click");
                }
                /*选中后清空input里内容*/
                $(".pull-empty", container).attr("value", "").focusout();
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
            }
        }
    },

    /*获取当前页检索条件下的li*/
    getAllLi: function () {
        var rstArrLi = [];
        $('span.js_fld_name', navTab.getCurrentPanel()).each(function () {
            var thisSpan = $(this);
            var everyLi = {};
            var resultfieldname = thisSpan.parent().attr("resultfieldname");
            if (resultfieldname != "HouseInfo.sysTags" && resultfieldname != "HouseInfo.useTags") {
                everyLi.chName = thisSpan.text();
                everyLi.enValue = resultfieldname;
                rstArrLi.push(everyLi);
            }
        });
        return rstArrLi;
    },

    /*获取当前页所有label*/
    getAllLabel: function (selectFilter) {
        if (!selectFilter) {
            selectFilter = false;
        }
        var rstArr = [];
        var container = $("div.js_cond_list", navTab.getCurrentPanel());
        $('span.rst_field', container).each(function () {
            var thisLabel = $(this);
            var everyRow = {};
            everyRow.chName = thisLabel.attr("title");
            everyRow.enValue = thisLabel.attr("entityAttrNameEn");
            if (selectFilter && thisLabel.hasClass("selected")) {
                // 过滤已经选择的项目
                return true;
            }
            rstArr.push(everyRow);
        });
        return rstArr;

    },

    /*获取当前页所有（人员属性对应）label*/
    getPsLabel: function (selectFilter) {
        if (!selectFilter) {
            selectFilter = false;
        }
        var rstArr = [];
        $('span.rst_ps_field', navTab.getCurrentPanel()).each(function (i) {
            var thisLabel = $(this);
            var everyRow = {};
            everyRow.chName = thisLabel.attr("title");
            everyRow.enValue = thisLabel.attr("entityAttrNameEn");
            if (selectFilter && thisLabel.hasClass("selected")) {
                // 过滤已经选择的项目
                return true;
            }
            rstArr.push(everyRow);
        });
        return rstArr;

    },
    /*获取当前页所有（人员属性对应）label*/
    getTagLabel: function (selectFilter) {
        if (!selectFilter) {
            selectFilter = false;
        }
        var rstArr = [];
        $('span.rst_tag_field', navTab.getCurrentPanel()).each(function (i) {
            var thisLabel = $(this);
            var everyRow = {};
            everyRow.chName = thisLabel.attr("title");
            everyRow.enValue = thisLabel.attr("entityAttrNameEn");
            if (selectFilter && thisLabel.hasClass("selected")) {
                // 过滤已经选择的项目
                return true;
            }
            rstArr.push(everyRow);
        });
        return rstArr;
    }
};

$(document).ready(function () {
    var container = $("div.js_query_div", navTab.getCurrentPanel());
    var entityName = $("input.js_entityName", container).val();
    var canDefResult = $("input.js_canDefResult", container).val();
    /**
     * 选中列表字段
     **/
    $("label.rst_field_slt", container).bind("click", function () {
        if ("false" === canDefResult) {
            return;
        }
        var $this = $(this);
        var rstField = $this.closest("span.rst_field");
        var canConditionFlag = rstField.attr("canConditionFlag");
        if (0 == canConditionFlag) {
            return;
        }
        var condFieldName = rstField.attr("entityAttrNameEn");
        var rstFieldName = $("label.rst_field_name", rstField).html();
        // 循环获取要求显示的字段
        var showUl = $("ul.js_show_ul", container);
        if (rstField.hasClass("selected")) {
            rstField.removeClass("selected");
            var matchLi = null;
            $("li.qry_cod", showUl).each(function () {
                var _this = $(this);
                if (condFieldName === _this.attr("resultFieldName")) {
                    matchLi = _this;
                }
            });
            if (matchLi) {
                matchLi.remove();
            }
        } else {
            rstField.addClass("selected");
            // 新增选择内容
            var newLi = $("li.qry_cod:eq(0)", showUl).clone();
            newLi.show();
            $("span.js_fld_name", newLi).html(rstFieldName);
            newLi.attr("resultFieldName", condFieldName);
            var canOrderFlag = rstField.attr("canOrderFlag");
            if (canOrderFlag != "1") {
                $("label.sortable", newLi).removeClass("sortable");
            }
            showUl.append(newLi);
        }
    });

    /**
     * 删除过滤条件
     **/
    container.delegate("label.js_del_cond", "click", function () {
        stopEvent(event);
        var $this = $(this);
        var liObj = $this.closest("li");
        liObj.remove();
    });

    /**
     * 删除显示字段
     **/
    container.delegate("label.js_del_rslt", "click", function () {
        var $this = $(this);
        var liObj = $this.closest("li");
        var resultFieldName = liObj.attr("resultFieldName");
        // 查找所有显示的字段
        $("label.rst_field_slt", container).each(function () {
            var rstField = $(this).closest("span.rst_field");
            var resultRequired = rstField.attr("resultRequired");
            if (resultRequired != "true" && resultFieldName == rstField.attr("entityAttrNameEn")) {
                liObj.remove();
                rstField.removeClass("selected");
                // 取消选择
                return false;
            }
        });
    });

    /**
     * 选中，取消事件
     */
    container.delegate("span.js_val_order", "click", function () {
        var $this = $(this);
        $this.siblings(".js_val_order").removeClass("val_selected");
        if ($this.hasClass("val_selected")) {
            $this.removeClass("val_selected")
        } else {
            $this.addClass("val_selected")
        }
    });

    /**
     * 选中值，取消事件
     */
    container.delegate("span.js_opt_val", "click", function () {
        var $this = $(this);
        if ($this.hasClass("val_selected")) {
            $this.removeClass("val_selected")
        } else {
            $this.addClass("val_selected")
        }
    });
    /**
     * 选中值，取消事件
     */
    container.delegate("span.js_tag_opt_val", "click", function () {
        var $this = $(this);
        var isCheckbox = $(this).attr("ischeckbox");
        if (isCheckbox != "false") {
            if ($this.hasClass("val_selected")) {
                $this.removeClass("val_selected");
            } else {
                $this.addClass("val_selected");
            }
        } else {
            $this.closest(".clearfix").find(".val_selected").removeClass("val_selected");
            $this.addClass("val_selected");
        }
    });

    /**
     * 排序处理
     */
    container.delegate("label.sortable", "click", function () {
        var $this = $(this);
        if ($this.hasClass("asc")) {
            $this.removeClass("asc");
            $this.addClass("desc")
        } else if ($this.hasClass("desc")) {
            $this.removeClass("desc");
            $this.removeClass("asc")
        } else {
            $this.removeClass("desc");
            $this.addClass("asc")
        }
    });

    /**
     * 确定隐藏条件定义
     */
    container.delegate("button.js_cfm_con", "click", function () {
        var $this = $(this);
        var rstField = $this.closest("span.rst_field");
        var condFieldName = rstField.attr("entityAttrNameEn");
        var entityAttrNameCh = $("label.rst_field_name", rstField).html();
        var matchLi = null;
        if (rstField.length == 0) {
            rstField = $this.closest("li.qry_cod");
            condFieldName = rstField.attr("condFieldName");
            entityAttrNameCh = rstField.attr("entityAttrNameCh");
            matchLi = rstField;
        } else {
            $("ul.js_conditions_ul>li", container).each(function () {
                var _this = $(this);
                var liCondFieldName = _this.attr("condFieldName");
                if (liCondFieldName == condFieldName) {
                    matchLi = _this;
                    return false;
                }
            });
        }
        // 处理类型
        var condType = $("input.js_condType", rstField).val();
        var model = $("input.js_model", rstField).val();
        var conditionNames = [];
        var conditions = [];
        var conditionValues = [];
        // 检索条件显示
        var conditionsDisp = "";
        if ("text" === condType) {
            // 文本类型
            var conditionValue = $("input.js_text_cond_value", rstField).val();
            var condition = $("select.js_text_cond", rstField).val();
            var conditionText = $("select.js_text_cond", rstField).find("option:selected").html();
            if (conditionValue != "") {
                conditionNames.push(condFieldName);
                conditions.push(condition);
                conditionValues.push(conditionValue);
                // 显示的文本内容
                conditionsDisp = conditionsDisp + conditionText + "\"" + conditionValue + "\""
            }
        } else if ("opt" == condType) {
            var selectValues = [];
            var selectText = [];
            // 获取选择的选项值
            $("span.val_selected", rstField).each(function () {
                var _this = $(this);
                selectValues.push(_this.attr("value"));
                selectText.push(_this.html());
            });
            if (selectValues.length > 0) {
                conditionNames.push(condFieldName);
                if ("more" == model){
                    conditions.push("LIKE");
                    conditionValues.push(selectValues.join("%"));
                } else {
                    conditions.push("in");
                    conditionValues.push(selectValues.join("|"));
                }
                // 显示的文本内容
                conditionsDisp = conditionsDisp + selectText.join("、");
            } else {
                conditionNames.push(condFieldName);
                conditions.push("in");
                conditionValues.push("");
                // 显示的文本内容
                conditionsDisp = conditionsDisp;
            }
        } else if ("date" == condType) {
            // 日期或数字类型的，按照区间过滤
            // 文本类型
            var startValue = $("input.js_start_value", rstField).val();
            var endValue = $("input.js_end_value", rstField).val();
            var condition = $("select.js_text_cond", rstField).val();
            if (startValue != "") {
                conditionNames.push(condFieldName);
                conditions.push(">=");
                var useValue = startValue;
                useValue = useValue.replace(/[-: ]/g, "");
                // 补全14位
                for (var i = useValue.length; i < 14; i++) {
                    useValue = useValue + "0";
                }
                conditionValues.push(useValue);
            }
            if (endValue != "") {
                conditionNames.push(condFieldName);
                conditions.push("<=");
                var useValue = endValue;
                useValue = useValue.replace(/[-: ]/g, "");
                // 补全14位
                for (var i = useValue.length; i < 14; i++) {
                    useValue = useValue + "0";
                }
                conditionValues.push(useValue);
            }
            conditionsDisp = conditionsDisp + startValue;
            conditionsDisp = conditionsDisp + "-";
            conditionsDisp = conditionsDisp + endValue;
        } else if ("num" == condType) {
            // 日期或数字类型的，按照区间过滤
            // 文本类型
            var startValue = $("input.js_start_value", rstField).val();
            var endValue = $("input.js_end_value", rstField).val();
            var condition = $("select.js_text_cond", rstField).val();
            if (startValue != "") {
                conditionNames.push(condFieldName);
                conditions.push(">=");
                conditionValues.push(startValue);
            }
            if (endValue != "") {
                conditionNames.push(condFieldName);
                conditions.push("<=");
                conditionValues.push(endValue);
            }
            conditionsDisp = conditionsDisp + startValue;
            conditionsDisp = conditionsDisp + "-";
            conditionsDisp = conditionsDisp + endValue;
        } else if ("staff" == condType) {
            // 文本类型
            var conditionValue = $("input.js_staff_id", rstField).val();
            var condition = "=";
            var conditionText = $("input.js_staff_name", rstField).val();
            if (conditionValue != "") {
                conditionNames.push(condFieldName);
                conditions.push(condition);
                conditionValues.push(conditionValue);
                // 显示的文本内容
                conditionsDisp = conditionsDisp + "\"" + conditionText + "\""
            }
        } else if ("org" == condType) {
            // 文本类型
            var conditionValue = $("input.js_org_id", rstField).val();
            var condition = "=";
            var conditionText = $("input.js_org_name", rstField).val();
            if (conditionValue != "") {
                conditionNames.push(condFieldName);
                conditions.push(condition);
                conditionValues.push(conditionValue);
                // 显示的文本内容
                conditionsDisp = conditionsDisp + "\"" + conditionText + "\""
            }
        } else if ("codeTree" == condType) {
            // 文本类型
            var conditionValue = $("input.js_code_cd", rstField).val();
            var condition = "REGEXP";
            var conditionText = $("input.js_code_name", rstField).val();
            if (conditionValue != "") {
                conditionValue = conditionValue.split(",").join("|");
                conditionNames.push(condFieldName);
                conditions.push(condition);
                conditionValues.push(conditionValue);
                // 显示的文本内容
                conditionsDisp = conditionsDisp + "\"" + conditionText + "\""
            }
        }
        // 检索条件名称
        if (conditionNames.length > 0) {
            if (!matchLi) {
                matchLi = $("ul.js_conditions_ul", container).find("li:eq(0)").clone();
                matchLi.show();
                $("ul.js_conditions_ul", container).append(matchLi);
            }
            // 设置检索条件内容
            matchLi.attr("condFieldName", condFieldName);
            matchLi.attr("conditionName", conditionNames.join(","));
            matchLi.attr("condition", conditions.join(","));
            matchLi.attr("conditionValue", conditionValues.join(","));
            matchLi.attr("entityAttrNameCh", entityAttrNameCh);
            $("span", matchLi).html(entityAttrNameCh + "：" + conditionsDisp);
        }
        $("div.rst_field_cond", rstField).hide();
    });

    /**
     * 标签检索条件
     * 确定隐藏条件定义
     */
    container.delegate("button.js_tag_cfm_con", "click", function () {
        var $this = $(this);
        var rstField = $this.closest("span.rst_tag_field");
        var condFieldName = rstField.attr("entityAttrNameEn");
        var entityAttrNameCh = $("label.rst_tag_field_name", rstField).html();
        var matchLi = null;
        if (rstField.length == 0) {
            rstField = $this.closest("li.qry_tag_cod");
            condFieldName = rstField.attr("condFieldName");
            entityAttrNameCh = rstField.attr("entityAttrNameCh");
            matchLi = rstField;
        } else {
            $("ul.js_tag_conditions_ul>li", container).each(function () {
                var _this = $(this);
                var liCondFieldName = _this.attr("condFieldName");
                if (liCondFieldName == condFieldName) {
                    matchLi = _this;
                    return false;
                }
            });
        }
        // 处理类型
        var conditionNames = [];
        var conditions = [];
        var conditionValues = [];
        // 检索条件显示
        var conditionsDisp = "";
        var selectValues = [];
        var selectText = [];
        var chooseConditions = $("input[name='condition']:checked", rstField).val();
        // 获取选择的选项值
        $("span.val_selected", rstField).each(function () {
            var _this = $(this);
            selectValues.push(_this.attr("value"));
            selectText.push(_this.html());
        });
        if (selectValues.length > 0) {
            conditionNames.push(condFieldName);
            conditions.push(chooseConditions);
            conditionValues.push(selectValues.join("|"));
            // 显示的文本内容
            conditionsDisp = conditionsDisp + selectText.join("、");
        } else {
            conditionNames.push(condFieldName);
            conditions.push(chooseConditions);
            conditionValues.push("");
            // 显示的文本内容
            conditionsDisp = conditionsDisp;
        }
        // 检索条件名称
        if (conditionNames.length > 0) {
            if (!matchLi) {
                matchLi = $("ul.js_tag_conditions_ul", container).find("li:eq(0)").clone();
                matchLi.show();
                $("ul.js_tag_conditions_ul", container).append(matchLi);
            }
            // 设置检索条件内容
            matchLi.attr("condFieldName", condFieldName);
            matchLi.attr("conditionName", conditionNames.join(","));
            matchLi.attr("condition", conditions.join(","));
            matchLi.attr("conditionValue", conditionValues.join(","));
            matchLi.attr("entityAttrNameCh", entityAttrNameCh);
            var tempColon = ":";
            if (conditions == "IN") {
                tempColon = "\"包含\"";
            } else if (conditions == "NOT IN") {
                tempColon = "\"不包含\"";
            }
            $("span", matchLi).html(entityAttrNameCh + tempColon + conditionsDisp);
        }
        $("div.rst_tag_field_cond", rstField).hide();
    });

    /**
     * 取消隐藏条件定义
     */
    container.delegate("button.js_cle_con", "click", function () {
        var $this = $(this);
        var rstCond = $this.closest("div.rst_field_cond");
        rstCond.hide();

    });

    /**
     * 点击弹出条件检索框
     */
    container.delegate("label.rst_field_name,span.js_query_cond", "click", function () {
        var $this = $(this);
        var rstField = $this.closest("span.rst_field");
        // 已经定义的检索条件点击事件
        var conditionSearch = false;
        var canConditionFlag = null
        if (rstField.length == 0) {
            rstField = $this.closest("li.qry_cod");
            canConditionFlag = "1";
            conditionSearch = true;
        } else {
            canConditionFlag = rstField.attr("canConditionFlag");
        }
        // 不允许作为检索条件
        if (canConditionFlag != "1") {
            return;
        }
        // 处理当前字段的展示
        var rstFieldCond = $("div.rst_field_cond", rstField);
        // 查询条件已经显示，则直接隐藏查询
        if (rstFieldCond.is(":visible")) {
            rstFieldCond.hide();
            return;
        } else if (rstFieldCond.html() != "") {
            rstFieldCond.show();
        }
        // 处理请求显示内容
        var entityAttrNameEn = null;
        var condition = null;
        var conditionValue = null;
        var conditionName = null;
        if (conditionSearch) {
            entityAttrNameEn = rstField.attr("condFieldName");
            if (entityAttrNameEn.indexOf("UID.") == 0) {
                return;
            }
            condition = rstField.attr("condition");
            conditionValue = rstField.attr("conditionValue");
            conditionName = rstField.attr("conditionName");
        } else {
            entityAttrNameEn = rstField.attr("entityAttrNameEn");
            // 循环找到匹配的检索条件
            var matchLi = null;
            $("ul.js_conditions_ul>li", container).each(function () {
                var _this = $(this);
                var liCondFieldName = _this.attr("condFieldName");
                if (liCondFieldName == entityAttrNameEn) {
                    matchLi = _this;
                    return false;
                }
            });
            if (matchLi) {
                condition = matchLi.attr("condition");
                conditionValue = matchLi.attr("conditionValue");
                conditionName = matchLi.attr("conditionName");
            }
        }
        var attrEntityArr = entityAttrNameEn.split(".");
        var attrEntity = attrEntityArr[0];
        if (attrEntity != entityName) {
            entityName = attrEntityArr[0];
        }
        // 可见，先隐藏其他的查询对象
        $("div.rst_field_cond", container).hide();
        // 获取属性
        var packet = new AJAXPacket();
        packet.noCover = true;
        var prjCd = getPrjCd();
        packet.data.add("entityAttrNameEn", entityAttrNameEn);
        packet.data.add("prjCd", prjCd);
        packet.data.add("entityName", entityName);
        packet.data.add("condition", condition ? condition : "");
        packet.data.add("conditionValue", conditionValue ? conditionValue : "");

        // 调用获取展示界面
        packet.url = getGlobalPathRoot() + "oframe/common/page/page-valueDef.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var condContainer = rstFieldCond.closest("table.js_def_table");
            rstFieldCond.css("max-width", condContainer.width());
            rstFieldCond.html(response);
            initUI(rstFieldCond);
            // 显示已有查询条件
            var position = rstField.position();
            var top = position.top + 36;
            var left = position.left + 6;
            var triangle = rstFieldCond.find("div.triangle");
            // 高度超过整体高度，在上面显示
            if (top + rstFieldCond.height() > $(window).height()) {
                top = position.top - rstFieldCond.height() - 26;
                triangle.removeClass("triangle-up").addClass("triangle-down");
                triangle.css("bottom", "-10px");
            } else {
                triangle.removeClass("triangle-down").addClass("triangle-up");
                triangle.css("top", "-10px");
            }
            // 宽度超过整体宽度，在左侧显示
            if (left + rstFieldCond.width() > condContainer.width()) {
                left = condContainer.width() - rstFieldCond.width() - 26;
                triangle.css("left", (position.left - left + 35) + "px");
            } else {
                triangle.css("left", "35px");
            }
            rstFieldCond.css("top", top).css("left", left).css("max-width", condContainer.width());
            rstFieldCond.show();
            var inputTab = rstFieldCond.find("input.textInput");
            if (inputTab.length >= 1) {
                inputTab.eq(0).focus();
            }
        });
    });

    /**
     * 标签条件检索
     * 点击弹出条件检索框
     */
    container.delegate("label.rst_tag_field_name,span.js_tag_query_cond", "click", function () {
        var $this = $(this);
        var rstField = $this.closest("span.rst_tag_field");
        // 已经定义的检索条件点击事件
        var conditionSearch = false;
        var canConditionFlag = null
        if (rstField.length == 0) {
            rstField = $this.closest("li.qry_tag_cod");
            canConditionFlag = "1";
            conditionSearch = true;
        } else {
            canConditionFlag = rstField.attr("canConditionFlag");
        }
        // 不允许作为检索条件
        if (canConditionFlag != "1") {
            return;
        }
        // 处理当前字段的展示
        var rstFieldCond = $("div.rst_tag_field_cond", rstField);
        // 查询条件已经显示，则直接隐藏查询
        if (rstFieldCond.is(":visible")) {
            rstFieldCond.hide();
            return;
        } else if (rstFieldCond.html() != "") {
            rstFieldCond.show();
        }
        // 处理请求显示内容
        var entityAttrNameEn = null;
        var condition = null;
        var conditionValue = null;
        var conditionName = null;
        if (conditionSearch) {
            entityAttrNameEn = rstField.attr("condFieldName");
            if (entityAttrNameEn.indexOf("UID.") == 0) {
                return;
            }
            condition = rstField.attr("condition");
            conditionValue = rstField.attr("conditionValue");
            conditionName = rstField.attr("conditionName");
        } else {
            entityAttrNameEn = rstField.attr("entityAttrNameEn");
            // 循环找到匹配的检索条件
            var matchLi = null;
            $("ul.js_tag_conditions_ul>li", container).each(function () {
                var _this = $(this);
                var liCondFieldName = _this.attr("condFieldName");
                if (liCondFieldName == entityAttrNameEn) {
                    matchLi = _this;
                    return false;
                }
            });
            if (matchLi) {
                condition = matchLi.attr("condition");
                conditionValue = matchLi.attr("conditionValue");
                conditionName = matchLi.attr("conditionName");
            }
        }
        // 可见，先隐藏其他的查询对象
        $("div.rst_tag_field_cond", container).hide();
        // 获取属性
        var packet = new AJAXPacket();
        packet.noCover = true;
        var prjCd = getPrjCd();
        packet.data.add("entityAttrNameEn", entityAttrNameEn);
        packet.data.add("prjCd", prjCd);
        packet.data.add("entityName", entityName);
        packet.data.add("condition", condition ? condition : "");
        packet.data.add("conditionValue", conditionValue ? conditionValue : "");
        // 调用获取展示界面
        packet.url = getGlobalPathRoot() + "oframe/common/page/page-tagValueDef.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var condContainer = rstFieldCond.closest("table.js_def_table");
            rstFieldCond.css("max-width", condContainer.width());
            rstFieldCond.html(response);
            initUI(rstFieldCond);
            // 显示已有查询条件
            var position = rstField.position();
            var top = position.top + 36;
            var left = position.left + 6;
            var triangle = rstFieldCond.find("div.triangle");
            // 高度超过整体高度，在上面显示
            if (top + rstFieldCond.height() > $(window).height()) {
                top = position.top - rstFieldCond.height() - 26;
                triangle.removeClass("triangle-up").addClass("triangle-down");
                triangle.css("bottom", "-10px");
            } else {
                triangle.removeClass("triangle-down").addClass("triangle-up");
                triangle.css("top", "-10px");
            }
            // 宽度超过整体宽度，在左侧显示
            if (left + rstFieldCond.width() > condContainer.width()) {
                left = condContainer.width() - rstFieldCond.width() - 26;
                triangle.css("left", (position.left - left + 35) + "px");
            } else {
                triangle.css("left", "35px");
            }
            rstFieldCond.css("top", top).css("left", left).css("max-width", condContainer.width());
            rstFieldCond.show();
            var inputTab = rstFieldCond.find("input.textInput");
            if (inputTab.length >= 1) {
                inputTab.eq(0).focus();
            }
        });
    });

    /*点击展开更多*/
    container.delegate("a.js-more", "click", function () {
        var _this = $(this);
        var jsShow = $('#js-show', navTab.getCurrentPanel());
        if (_this.hasClass("current")) {
            _this.removeClass('current');
            jsShow.css("display", "none");
        } else {
            _this.addClass('current');
            jsShow.css("display", "");
        }

    });

});
