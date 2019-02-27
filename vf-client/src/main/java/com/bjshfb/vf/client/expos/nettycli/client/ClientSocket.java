package com.bjshfb.vf.client.expos.nettycli.client;

import com.bjshfb.vf.client.expos.config.CptPicSchedulConfig;
import com.bjshfb.vf.client.expos.env.EnvConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

/**
 * @program: helm
 * @description: TODO
 * @author: fuzq
 * @create: 2018-09-25 11:55
 **/
public class ClientSocket {
    public static final Logger logger = Logger.getLogger(ClientSocket.class);

    private ChannelFuture future;

    public void run(int port, String addr) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(workGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChildChannelHandler());
            b.option(ChannelOption.SO_KEEPALIVE, true);
            //端口可配
            future = b.connect(addr, port).sync();
            if (future.isSuccess()) {
                logger.debug("【连接服务器成功】");
                setFuture(future);
            }
            //等待服务监听端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放线程资源
//            workGroup.shutdownGracefully();
            //重连服务器
            reConnectServer();
        }
    }

    /**
     * @Description: TODO 客户端重连服务器
     * @Param: []
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/28 16:30
     **/
    private void reConnectServer() {
        int count = 0;
        try {
            while (count < 10) {
                Thread.sleep(10000);
                logger.warn("【客户端进行断线重连】");
                new ClientSocket().run(5555, EnvConfig.NettyAddr);
                count++;
            }
        } catch (InterruptedException e) {
            logger.error("【客户端连接服务器失败，请重启服务器确保客户端可以重连】");
        }
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }

    public static void main(String[] args) {
        new ClientSocket().run(5555, "localhost");
    }
}
