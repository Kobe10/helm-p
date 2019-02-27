<%--院落状态统计--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="background-color: #ffffff;min-height:300px;">
    <style type="text/css">
        label.p_num {
            font-weight: bold;
            color: #ff0000;
        }
        .arrow-right {
            display: inline-block;
            width: 0;
            height: 0;
            border-top: 5px solid transparent;
            border-bottom: 5px solid transparent;
            border-left: 5px solid #2c7bae;
            margin-right: 5px;;
        }
    </style>
    <ul class="marl10 marr10 news-list">
        <li>
            <p class="news-p clearfix">
                <label class="arrow-right"></label>
                整院意向： 累计院落数（<label class="p_num">${yxYlNum[0]}</label>），累计户数（<label class="p_num">${yxYlNum[1]}</label>）
                ，本月院落数（<label class="p_num">${yxYlNum[2]}</label>），本月户数（<label class="p_num">${yxYlNum[3]}</label>）
            </p>
        </li>
        <li>
            <p class="news-p clearfix">
                <label class="arrow-right"></label>
                整院签约： 累计院落数（<label class="p_num">${qyYlNum[0]}</label>），累计户数（<label class="p_num">${qyYlNum[1]}</label>）
                ，本月院落数（<label class="p_num">${qyYlNum[2]}</label>），本月户数（<label class="p_num">${qyYlNum[3]}</label>）
            </p>
        </li>
        <li>
            <p class="news-p clearfix">
                <label class="arrow-right"></label>
                完成选房： 累计院落数（<label class="p_num">${xfYlNum[0]}</label>），累计户数（<label class="p_num">${xfYlNum[1]}</label>）
                ，本月院落数（<label class="p_num">${xfYlNum[2]}</label>），本月户数（<label class="p_num">${xfYlNum[3]}</label>）
            </p>
        </li>
        <li>
            <p class="news-p clearfix">
                <label class="arrow-right"></label>
                退租过户： 累计院落数（<label class="p_num">${tzghYlNum[0]}</label>），累计户数（<label
                    class="p_num">${tzghYlNum[1]}</label>）
                ，本月院落数（<label class="p_num">${tzghYlNum[2]}</label>），本月户数（<label class="p_num">${tzghYlNum[3]}</label>）
            </p>
        </li>

        <li>
            <p class="news-p clearfix">
                <label class="arrow-right"></label>
                完成交房： 累计院落数（<label class="p_num">${jfYlNum[0]}</label>），累计户数（<label class="p_num">${jfYlNum[1]}</label>）
                ，本月院落数（<label class="p_num">${jfYlNum[2]}</label>），本月户数（<label class="p_num">${jfYlNum[3]}</label>）
            </p>
        </li>

        <li>
            <p class="news-p clearfix">
                <label class="arrow-right"></label>
                归档院落： 累计院落数（<label class="p_num">${gdYlNum[0]}</label>），累计户数（<label class="p_num">${gdYlNum[1]}</label>）
                ，本月院落数（<label class="p_num">${gdYlNum[2]}</label>），本月户数（<label class="p_num">${gdYlNum[3]}</label>）
            </p>
        </li>
    </ul>
</div>
