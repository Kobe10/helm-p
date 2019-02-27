<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>测试视频rtsp协议播放</title>
</head>
<body>
<div style="background: rgb(132, 132, 132); padding: 0px;width: 800px; height: 550px;" id="pla">
    <object type='application/x-vlc-plugin' id='vlc' events='false'
            style="width: 100%;height: 100%;"
            pluginspage="http://www.videolan.org/">
        <param name='mrl' value='${cameralist.CameraMngInfo.accessAddr}'/>
        <param name='volume' value='50'/>
        <param name='autoplay' value='true'/>
        <param name='loop' value='false'/>
        <param name='fullscreen' value='true'/>
        <param name='controls' value='false'/>
    </object>
    <%--<embed id="qt_video" src="sample.mov" qtsrc="${cameralist.CameraMngInfo.accessAddr}"--%>
    <%--bgColor="#585858" width="100%" height="100%"--%>
    <%--scale="ToFit" autoplay="true" loop="false" controller="true" wmode="transparent"--%>
    <%--VOLUME=0 type="video/quicktime" pluginspage="http://www.apple.com/quicktime/">--%>
    <%--</embed>--%>
</div>
</body>
<script type="text/javascript">
    setTimeout(function () {
        var vlc = document.getElementById("vlc");
        vlc.style.width = "100%";
        vlc.style.height = "100%";
        vlc.width = "100%";
        vlc.height = "100%";
    }, 200);
</script>
</html>
