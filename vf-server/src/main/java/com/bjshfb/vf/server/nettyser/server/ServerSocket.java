package com.bjshfb.vf.server.nettyser.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: helm
 * @description: TODO netty 服务端
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
@Service
public class ServerSocket implements Runnable{
    public static final Logger logger = Logger.getLogger(ServerSocket.class);
    /**
     * 为了适用spring 初始化加载这个bean 需要另外启动一个线程去执行服务端的启动 否则会阻塞当前线程
     * */
    public static ExecutorService singleexecutor = Executors.newFixedThreadPool(2);

    private ChannelFuture f;

    public void run(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 1024);

            b.childHandler(new ChildChannelHandler());
            // 端口可配
            f = b.bind(port).sync();
            logger.debug("【netty 服务启动， 准备运行端口 : 5555 】");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("【netty 服务启动 系统错误】", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public void test() {
        singleexecutor.execute(new ServerSocket());
    }

    public static void main(String[] args) throws InterruptedException {
        new ServerSocket().run();

        while (true) {
            ChanleMap.sendMessageToUser("111", "discovery");
        }
    }

    @Override
    public void run() {
        run(5555);
    }
}
