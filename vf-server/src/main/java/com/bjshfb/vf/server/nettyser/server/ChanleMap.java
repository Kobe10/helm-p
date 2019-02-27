package com.bjshfb.vf.server.nettyser.server;

import com.bjshfb.vf.server.nettyser.future.SyncWrite;
import com.bjshfb.vf.server.nettyser.msg.Request;
import com.bjshfb.vf.server.nettyser.msg.Response;
import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @program: helm
 * @description: 存储通信通道  服务端存储
 * @author: fuzq
 * @create: 2018-09-26 15:30
 **/
public class ChanleMap {
    public static final Logger logger = Logger.getLogger(ChanleMap.class);

    public static int count = 0;

    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ChanleMap(String info) {
        this.info = info;
    }

    /**
     * 存储通道集合
     */
    private static final ConcurrentMap<String, Channel> USERS = PlatformDependent.newConcurrentHashMap();

    /**
     * 存储通道
     *
     * @param key     唯一键
     * @param channel 通道
     */
    public static void put(String key, Channel channel) {
        USERS.put(key, channel);
    }

    /**
     * 移除通道
     *
     * @param channel 通道
     * @return 移除结果
     */
    public static boolean remove(Channel channel) {
        String key = null;
        boolean b = USERS.containsValue(channel);
        if (b) {
            Set<Map.Entry<String, Channel>> entries = USERS.entrySet();
            for (Map.Entry<String, Channel> entry : entries) {
                Channel value = entry.getValue();
                if (value.equals(channel)) {
                    key = entry.getKey();
                    break;
                }
            }
        } else {
            return true;
        }
        return remove(key);
    }

    /**
     * 移出通道
     *
     * @param key 键
     */
    public static boolean remove(String key) {
        Channel remove = USERS.remove(key);
        boolean containsValue = USERS.containsValue(remove);
        logger.info("\n\t⌜⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓\n" +
                "\t├ [移出结果]: {}\n" +
                "\t⌞⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓⎓" + containsValue);
        return containsValue;
    }


    /**
     * @Description: TODO 给某个客户端发送消息
     * @Param: [userName, method, ipcId, params]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/26 15:37
     **/
    public static String sendMessageToUser(String userName, String method, String ipcId, String params, String eventType) {
        Request request = null;
        try {
            //构建发送参数
            Channel future = USERS.get(userName);
            if (future == null) {
                return null;
            }
            Response response = new Response();
            response.setIpcId(ipcId);
            response.setMethod(method);
            response.setParams(params);
            response.setEventType(eventType);
            SyncWrite s = new SyncWrite();
            request = s.writeAndSync(future, response, 500);
            return request.getParam();
        } catch (Exception e) {
            logger.error("【 请求结果失败，请求方法" + request.getMethod() + " 】");
        }
        return request.getParam();
    }

    public static String sendMessageToUser(String userName, String method) {
        return sendMessageToUser(userName, method, null);
    }

    public static String sendMessageToUser(String userName, String method, String ipcId) {
        return sendMessageToUser(userName, method, ipcId, null, null);
    }
}
