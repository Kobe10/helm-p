package com.bjshfb.vf.server.nettyser.server;

import com.bjshfb.vf.server.nettyser.future.SyncWriteFuture;
import com.bjshfb.vf.server.nettyser.future.SyncWriteMap;
import com.bjshfb.vf.server.nettyser.msg.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @program: helm
 * @description: TODO 自定义服务端handler
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class MyServerHandler extends SimpleChannelInboundHandler<Request> {
    public static final Logger logger = Logger.getLogger(MyServerHandler.class);

    /**
     * 客户端超时次数
     * */
     private ConcurrentMap<ChannelHandlerContext,Integer> clientOvertimeMap = new ConcurrentHashMap<ChannelHandlerContext, Integer>();
     /**
      * 超时次数超过该值则注销连接
      * */
     private final int MAX_OVERTIME  = 3;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Request msg) throws Exception {
        String requestId = msg.getRequestId();
        SyncWriteFuture future = (SyncWriteFuture) SyncWriteMap.syncKey.get(requestId);
        if (future != null) {
            future.setResponse(msg);
        }
    }

    /**
     * @Description: TODO 当客户端连接服务端之后会调用此方法 存储客户端的chanle和id
     * @Param: [ctx]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/18 10:04
     **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String uuid = ctx.channel().id().asLongText();
        //截取Ip作为 key
        ChanleMap.put("111", ctx.channel());
        logger.debug("【客户端 " + "111" + " 连接成功】");
    }

    /**
     * @Description: TODO 断开连接
     * @Param: [ctx]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/28 16:18
     **/
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("【客户端关闭1】");
        super.channelInactive(ctx);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
        logger.info("【客户端关闭2】");
    }
}

