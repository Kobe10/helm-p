package com.bjshfb.vf.client.expos.nettycli.client;

import com.bjshfb.vf.client.expos.func.Discovery;
import com.bjshfb.vf.client.expos.func.GetSnapshotURI;
import com.bjshfb.vf.client.expos.func.PtzIpc;
import com.bjshfb.vf.client.expos.nettycli.msg.Request;
import com.bjshfb.vf.client.expos.nettycli.msg.Response;
import com.shfb.oframe.core.util.common.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.xml.soap.SOAPException;
import java.util.List;

/**
 * @program: helm
 * @description: TODO 客户端消息处理
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class MyClientHandler extends SimpleChannelInboundHandler<Response> {
    public static final Logger logger = Logger.getLogger(MyClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws SOAPException {
        //反馈
        Response resMsg = (Response) msg;
        Request request = new Request();

        request.setRequestId(resMsg.getRequestId());
        //处理业务逻辑 分发请求
        String result = dispacher(resMsg);

        request.setParam(result);
        ctx.writeAndFlush(request);
        //释放
        ReferenceCountUtil.release(msg);
    }

    /**
     * @Description: TODO 获取服务端请求 分发处理业务 返回结果到服务端
     * @Param: [ctx, msg]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/26 15:44
     **/
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Response msg) throws Exception {
        //反馈
        Request request = new Request();
        request.setRequestId(msg.getRequestId());
        logger.debug("【获取服务端数据" + msg.getResult() + "】");
        request.setParam(msg.getResult() + " 请求成功，反馈结果请接受处理。");
        //处理业务逻辑 分发请求
        String result = dispacher(msg);

        request.setParam(result);
        ctx.writeAndFlush(request);
        //释放
        ReferenceCountUtil.release(msg);
    }

    /**
     * @Description: TODO 初步连接发送初始化连接信息
     * @Param: [ctx]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/18 10:35
     **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //存储通道信息
        ChanleMap.put("111", ctx.channel());
    }

    /**
     * @param s
     * @Description: TODO 分发请求处理服务端命令
     * @Param: [s]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/17 16:06
     */
    private static String dispacher(Response s) throws SOAPException {
        String ipcId = "";
        String command = "";
        String eventType = "";
        JSONObject jsonObject = new JSONObject();
        if (StringUtil.isNotEmptyOrNull(s.getMethod())) {
            //处理字符串
            if (StringUtil.isEqual(s.getMethod(), "discovery")) {
                List<String> ipList = Discovery.findIpc();
                jsonObject.put("success", true);
                jsonObject.put("errMsg", "");
                jsonObject.put("data", ipList);
            }
            if (StringUtil.isEqual(s.getMethod(), "getSnapshot")) {
                if (StringUtil.isNotEmptyOrNull(s.getIpcId())) {
                    ipcId = s.getIpcId();
                    jsonObject = GetSnapshotURI.getSnapshotURI(ipcId);
                }
            }
            if (StringUtil.isEqual(s.getMethod(), "controlIpc")) {
                if (StringUtil.isNotEmptyOrNull(s.getIpcId())) {
                    ipcId = s.getIpcId();
                }
                if (StringUtil.isNotEmptyOrNull(s.getParams())) {
                    command = s.getParams();
                }
                if (StringUtil.isNotEmptyOrNull(s.getEventType())) {
                    eventType = s.getEventType();
                }
                if (PtzIpc.controlIpc(ipcId, command, eventType)) {
                    jsonObject.put("success", true);
                    jsonObject.put("errMsg", "");
                    jsonObject.put("data", "");
                } else {
                    jsonObject.put("success", false);
                    jsonObject.put("errMsg", "");
                    jsonObject.put("data", "转动失败");
                }
            }
        } else {

        }
        return jsonObject.toString();
    }
}
