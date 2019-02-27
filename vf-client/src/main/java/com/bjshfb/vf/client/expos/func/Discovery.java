package com.bjshfb.vf.client.expos.func;

import com.bjshfb.vf.client.ipc.onvif.discovery.DeviceDiscovery;
import com.bjshfb.vf.client.util.FileUtils;
import com.shfb.oframe.core.util.cache.CacheManager;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: helm
 * @description: 发现设备
 * @author: fuzq
 * @create: 2018-08-29 18:45
 **/
public class Discovery {
    /**
     * 发现设备
     */
    public static List<String> findIpc() {
        Map<Object, Object> ipMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        Collection<URL> urls = DeviceDiscovery.discoverWsDevicesAsUrls("^http$", ".*onvif.*");
        for (URL url : urls) {
            //获取内网Ip地址 判断是否未Ip地址
            if (isIP(url.getHost())) {
                list.add(url.getHost());
            }
        }
        ipMap.put("ipAddr", list);
        //加入缓存
        CacheManager.getInstance().put(FileUtils.CACHE_NAME, "ipAddr", ipMap);
        return list;
    }

    private static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        return ipAddress;
    }
}
