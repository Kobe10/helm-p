package com.bjshfb.vf.server.nettyser.future;

import com.bjshfb.vf.server.nettyser.msg.Request;
import com.bjshfb.vf.server.nettyser.msg.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @program: helm
 * @description: TODO 加锁写入数据 通道加锁
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class SyncWrite {

    public Request writeAndSync(final Channel channel, final Response request, final long timeout) throws Exception {

        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (request == null) {
            throw new NullPointerException("request");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout <= 0");
        }

        String requestId = UUID.randomUUID().toString();
        request.setRequestId(requestId);

        WriteFuture<Request> future = new SyncWriteFuture(request.getRequestId());
        SyncWriteMap.syncKey.put(request.getRequestId(), future);

        Request response = doWriteAndSync(channel, request, timeout, future);

        SyncWriteMap.syncKey.remove(request.getRequestId());
        return response;
    }

    /**
     * @Description: TODO 同步锁
     * @Param: [channel, request, timeout, writeFuture]
     * @return: com.bjshfb.vf.server.nettyser.msg.Request
     * @Author: fuzq
     * @Date: 2018/9/27 11:08
     **/
    private Request doWriteAndSync(final Channel channel, final Response request, final long timeout, final WriteFuture<Request> writeFuture) throws Exception {
        //方法监听 addListener
        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                writeFuture.setWriteResult(future.isSuccess());
                writeFuture.setCause(future.cause());
                //失败移除
                if (!writeFuture.isWriteSuccess()) {
                    SyncWriteMap.syncKey.remove(writeFuture.requestId());
                }
            }
        });

        Request response = writeFuture.get(timeout, TimeUnit.MILLISECONDS);
        int count = 0;
        while (response == null) {
            //不断获取返回结果 如果五秒之后还没返回 报错
            response = writeFuture.get(timeout, TimeUnit.MILLISECONDS);
            count++;
            if (count > 10) {
                break;
            }
        }
        if (response == null) {
            if (writeFuture.isTimeout()) {
                throw new TimeoutException();
            } else {
                // write exception
                throw new Exception(writeFuture.cause());
            }
        }
        return response;
    }
}