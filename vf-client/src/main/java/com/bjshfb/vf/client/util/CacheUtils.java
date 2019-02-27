package com.bjshfb.vf.client.util;

import com.bjshfb.vf.client.expos.data.IpcOnvif;
import com.bjshfb.vf.client.expos.data.IpcPosition;
import com.bjshfb.vf.client.expos.data.Precision;
import com.shfb.oframe.core.util.cache.CacheManager;
import com.shfb.oframe.core.util.common.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: helm
 * @description: 缓存工具类
 * @author: fuzq
 * @create: 2018-08-29 19:05
 **/
public class CacheUtils {
    /**
     * 获取缓存中的所有IPC信息
     * */
    public static Map<Object, Object> getAllIpcCache() {
        Map<Object, Object> IpcMap = new HashMap<>();
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");
        return IpcMap;
    }



    /**
     * 获取Ipc缓存
     */
    public static IpcOnvif getIpcCache(String id) {
        IpcOnvif ipcOnvif = new IpcOnvif();
        Map<Object, Object> IpcMap = new HashMap<>();
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");
        if (IpcMap.containsKey(id)) {
            ipcOnvif = (IpcOnvif) IpcMap.get(id);
            return ipcOnvif;
        } else {
            return null;
        }
    }

    /**
     * 获取操作精度
     */
    public static Precision getPreCache() {
        Precision precision = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "precision");
        return precision;
    }

    /**
     * 获取当前位置信息
     */
    public static IpcPosition getCurrentPosition(String id) {
        IpcOnvif ipcOnvif;
        Map<Object, Object> IpcMap;
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");
        if (IpcMap.containsKey(id)) {
            ipcOnvif = (IpcOnvif) IpcMap.get(id);
            IpcPosition ipcPosition = ipcOnvif.getIpcPosition();
            return ipcPosition;
        } else {
            return null;
        }
    }

    /**
     * 更新当前位置缓存
     */
    public static void setCurrentPosition(IpcPosition ipcPosition, String id) {
        IpcOnvif ipcOnvif = new IpcOnvif();
        Map<Object, Object> IpcMap = new HashMap<>();
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");
        if (IpcMap.containsKey(id)) {
            ipcOnvif = (IpcOnvif) IpcMap.get(id);
            ipcOnvif.setIpcPosition(ipcPosition);
        }
    }

    /**
     * param: [id]
     * describe: TODO 删除缓存
     * creat_user: fuzq
     * creat_date: 2018/9/4
     **/
    public static void removeCache(String id) {

        Map<Object, Object> IpcMap = new HashMap<>();
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");
        IpcMap.remove(id);
        CacheManager.getInstance().put(FileUtils.CACHE_NAME, "ipc", IpcMap);
    }
}
