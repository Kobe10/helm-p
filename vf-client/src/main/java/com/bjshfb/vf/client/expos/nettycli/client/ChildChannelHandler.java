package com.bjshfb.vf.client.expos.nettycli.client;

import com.bjshfb.vf.client.expos.nettycli.codec.RpcDecoder;
import com.bjshfb.vf.client.expos.nettycli.codec.RpcEncoder;
import com.bjshfb.vf.client.expos.nettycli.msg.Request;
import com.bjshfb.vf.client.expos.nettycli.msg.Response;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @program: helm
 * @description: TODO 客户端通道初始化
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(20, 10, 0));
        ch.pipeline().addLast(new RpcDecoder(Response.class));
        ch.pipeline().addLast(new RpcEncoder(Request.class));
        ch.pipeline().addLast(new MyClientHandler());
    }

}
