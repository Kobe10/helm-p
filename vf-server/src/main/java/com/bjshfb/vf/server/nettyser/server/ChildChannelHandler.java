package com.bjshfb.vf.server.nettyser.server;

import com.bjshfb.vf.server.nettyser.codec.RpcDecoder;
import com.bjshfb.vf.server.nettyser.codec.RpcEncoder;
import com.bjshfb.vf.server.nettyser.msg.Request;
import com.bjshfb.vf.server.nettyser.msg.Response;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @program: helm
 * @description: TODO 初始化服务端handler
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 解码转
        ch.pipeline().addLast(new RpcDecoder(Request.class));
        // 编码器
        ch.pipeline().addLast(new RpcEncoder(Response.class));
        // 在管道中添加我们自己的接收数据实现方法
        ch.pipeline().addLast(new MyServerHandler());
    }
}

