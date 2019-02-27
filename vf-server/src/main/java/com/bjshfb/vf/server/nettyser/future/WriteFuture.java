package com.bjshfb.vf.server.nettyser.future;

import com.bjshfb.vf.server.nettyser.msg.Request;

import java.util.concurrent.Future;

/**
 * @program: helm
 * @description: TODO
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public interface WriteFuture<T> extends Future<T> {

    Throwable cause();

    void setCause(Throwable cause);

    boolean isWriteSuccess();

    void setWriteResult(boolean result);

    String requestId();

    T response();

    void setResponse(Request response);

    boolean isTimeout();


}
