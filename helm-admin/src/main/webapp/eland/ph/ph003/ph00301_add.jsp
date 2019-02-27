<%--院落信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>基本信息
        <span class="panel_menu js_reload">取消</span>
        <span class="panel_menu" onclick="ph00301.saveOldHs(this,'add');">保存</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="background-color: #ffffff; min-height: 300px;">
        <%----%>
        <div class="border" style="position: relative; width: 40%; display: inline-block; overflow: hidden;">
            <div id="ph00301PrjRegion" style="height: 300px;"></div>
        </div>
        <div style="width: 59%;height: 100%; display: inline-block; float: right;">
            <form id="ph00301_add_frm" method="post">
                <table class="border" width="100%">
                    <tr>
                        <th width="18%"><label>档案编号:</label></th>
                        <td><input type="text" name="hsCd" value="${oldHsBean.HouseInfo.hsCd}"/></td>
                        <th width="18%"><label>房屋状态:</label></th>
                        <td><oframe:select prjCd="${param.prjCd}" name="hsStatus" itemCd="OLD_HS_STATUS"
                                           value="${oldHsBean.HouseInfo.hsStatus}"/></td>
                    </tr>
                    <tr>
                        <th><label>院落地址:</label></th>
                        <td colspan="3"><input type="text" name="hsAddr" value="${oldHsBean.HouseInfo.hsAddr}"/></td>
                    </tr>
                    <tr>
                        <th><label>房屋地址:</label></th>
                        <td><input type="text" name="hsFullAddr" value="${oldHsBean.HouseInfo.hsFullAddr}"/></td>
                        <th><label>门牌号:</label></th>
                        <td><input type="text" name="hsAddrNo" value="${oldHsBean.HouseInfo.hsAddrNo}"/></td>
                    </tr>
                    <tr>
                        <th><label>房屋间数:</label></th>
                        <td><input type="text" name="hsRoomNum" value="${oldHsBean.HouseInfo.hsRoomNum}"/></td>
                        <th><label>自建房数:</label></th>
                        <td><input type="text" name="hsSlfRoom" value="${oldHsBean.HouseInfo.hsSlfRoom}"/></td>
                    </tr>
                    <tr>
                        <th style="height: 100px;"><label>备注说明:</label></th>
                        <td colspan="3"><textarea type="text" name="hsNote">${oldHsBean.HouseInfo.hsNote}</textarea>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
