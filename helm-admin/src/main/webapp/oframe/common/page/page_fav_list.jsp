<%--自定义分页查询查询条件--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="canDefResultClass" value="${canDefResult == 'false'? 'hidden': ''}"/>
<div class="tree-bar tree-bars" id="fav">
    <span style="display: inline-block; margin-left: 10px;">我的收藏：</span>
    <c:forEach items="${favinfo}" var="fav">
        <c:choose>
            <c:when test="${fav.StaffFavoriteCondition.favStaff ==''}">
                    <span style="line-height: 20px;" onclick="Page.clickFav(this, '${param.callBack}');"
                          favId="${fav.StaffFavoriteCondition.favId}"
                          class="my_fav">
                          <label class="js_fav_name"
                                 style="cursor: pointer;">${fav.StaffFavoriteCondition.favName}</label>
                         <oframe:power prjCd="${param.prjCd}" rhtCd="EDIT_SYS_FAV">
                             <label class="js_fav_del"
                                    onclick="Page.deletefav(this)">X</label>
                         </oframe:power>
                    </span>
            </c:when>
            <c:otherwise>
                    <span style="line-height: 20px" onclick="Page.clickFav(this,'${param.callBack}');"
                          favId="${fav.StaffFavoriteCondition.favId}"
                          class="my_fav">
                         <label class="js_fav_name"
                                style="cursor: pointer;">${fav.StaffFavoriteCondition.favName}</label>
                         <label class="fav_del js_fav_del"
                                onclick="Page.deletefav(this)">X</label>
                    </span>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>