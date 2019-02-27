package com.bjshfb.vf.server.nettyser.future;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: helm
 * @description: TODO 同步存储客户端返回数据
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class SyncWriteMap {
    /**
     * 线程安全
     * */
    public static Map<String, WriteFuture> syncKey = new ConcurrentHashMap<String, WriteFuture>();

}
