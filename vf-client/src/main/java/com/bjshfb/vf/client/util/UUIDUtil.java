package com.bjshfb.vf.client.util;

import java.util.UUID;

/**
 * @Author fuzq
 * @Description: TODO UUID生成工具类
 * @Date: 14:44 2018/5/2
 */
public interface UUIDUtil {
    /**
     * 格式化UUID 去掉"-"
     * */
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 格式化UUID 取前16位当UID
     * */
    public static String getUid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0,15);
    }
}
