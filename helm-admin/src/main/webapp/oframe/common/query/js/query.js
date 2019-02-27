/**
 * 通用查询
 * @type {{showQueryDiv: showQueryDiv}}
 */
query = {

    /**
     * 显示查询条件自定义框
     * @param clickLi 点击对象
     */
    showQueryDiv: function (clickLi) {
        var queryDiv = $("div.js_query_div", navTab.getCurrentPanel());
        var clickObj = $(clickLi);
        if (clickObj.hasClass("active")) {
            queryDiv.hide();
            clickObj.removeClass("active");
        } else {
            queryDiv.show();
            clickObj.addClass("active");
        }
    },

    addQryField: function (clickObj) {
        var clickLabel = $(clickObj);
        var clickLi = clickLabel.closest("li");
        var newLi = clickLi.clone();
        newLi.find("select,input").val("");
        clickLi.after(newLi);
    },

    removeQryField: function (clickObj) {
        var clickLabel = $(clickObj);
        var clickLi = clickLabel.closest("li");
        if (clickLi.siblings().length == 0) {
            clickLi.find("select,input").val("");
        } else {
            clickLi.remove();
        }
    }
}