<%@ page language="java" contentType="text/html; charset=UTF-8"
         import="com.baidu.ueditor.ActionEnter"
         pageEncoding="UTF-8" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%

    request.setCharacterEncoding("utf-8");
    response.setHeader("Content-Type", "text/html");
    //跟路径
    String rootPath = application.getRealPath("/");
    //返回的json串
    String fileJson = new ActionEnter(request, rootPath).exec();
    String action = request.getParameter("action");
    if (StringUtil.isNotEmptyOrNull(action) && action.startsWith("upload")) {
        //项目编号
        String prjCd = "";
        //项目名称（格式：/xxxx）
        String webName = request.getContextPath();
        request.setAttribute("prjCd", prjCd);
        request.setAttribute("rootPath", rootPath);
        request.setAttribute("fileJson", fileJson);
        request.setAttribute("webName", webName);
        request.getRequestDispatcher("upFile-upload.gv").forward(request, response);
    } else {
        response.getWriter().write(fileJson);
    }
%>