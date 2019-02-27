package com.bjshfb.vf.client.expos.nettycli.client;

import com.bjshfb.vf.client.expos.nettycli.future.SyncWrite;
import com.bjshfb.vf.client.expos.nettycli.msg.Request;
import com.bjshfb.vf.client.expos.nettycli.msg.Response;
import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @program: helm
 * @description: TODO 客户端存储
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
    public static void sendMessageToUser(String userName, String method, String ipcId, String params) {
        try {
            Response response = new Response();
            response.setResult("");
            SyncWrite s = new SyncWrite();
            //默认userName为111
            Channel channel = USERS.get(userName);
            Request request = s.writeAndSync(channel, response, 1000);
        } catch (Exception e) {
            logger.error("【netty 服务端发送信息失败 】", e);
        }
    }
}
