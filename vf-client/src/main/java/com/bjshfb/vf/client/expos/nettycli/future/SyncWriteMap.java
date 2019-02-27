package com.bjshfb.vf.client.expos.nettycli.future;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: helm
 * @description: TODO
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class SyncWriteMap {

    public static Map<String, WriteFuture> syncKey = new ConcurrentHashMap<String, WriteFuture>();

}
