package com.bjshfb.vf.client.expos.config;

import com.bjshfb.vf.client.expos.data.Command;
import com.bjshfb.vf.client.expos.data.IpcOnvif;
import com.bjshfb.vf.client.expos.data.IpcProperties;
import com.bjshfb.vf.client.expos.env.EnvConfig;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.FFmpegManager;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.FFmpegManagerImpl;
import com.bjshfb.vf.client.expos.init.InitIpc;
import com.bjshfb.vf.client.util.CacheUtils;
import com.bjshfb.vf.client.util.FileUtils;
import com.bjshfb.vf.client.util.HttpClientUtils;
import com.bjshfb.vf.client.util.XmlUtils;
import com.shfb.oframe.core.util.cache.CacheManager;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: helm
 * @description: 定时任务
 * @author: fuzq
 * @create: 2018-09-04 12:51
 **/
@Configuration
@EnableScheduling
public class SchedulingConfig {
    public static final Logger logger = Logger.getLogger(SchedulingConfig.class);

    /**
     * @Description: TODO 对比配置变化 更新缓存信息 若摄像头断开连接 停止ffmpeg任务
     * @Param: []
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 17:27
     **/
    @Scheduled(cron = "0 0/1 * * * ?") // 每1分钟执行一次
    public void getToken() {
        logger.info("【定时任务启动】: 拉取配置文件");
        //拉取配置文件  对比配置文件数据变化  更新缓存
        XmlBean xmlBean = HttpClientUtils.getXmlBean();
        if (xmlBean == null) {
            //删除所有数据
            Map<String, IpcProperties> newMap = new HashMap<>();
            Map<String, IpcProperties> oldMap = XmlUtils.xmlbeanToMap(XmlUtils.xmlBean);
            Map<String, List<IpcProperties>> map = XmlUtils.diff(oldMap, newMap);
            //更新xmlBean
            XmlUtils.xmlBean = xmlBean;
            //更新缓存 更新IPC
            updateIpc(map);
        } else {
            //对比新旧数据
            Map<String, IpcProperties> newMap = XmlUtils.xmlbeanToMap(xmlBean);
            Map<String, IpcProperties> oldMap = XmlUtils.xmlbeanToMap(XmlUtils.xmlBean);

            Map<String, List<IpcProperties>> map = XmlUtils.diff(oldMap, newMap);
            //更新xmlBean
            XmlUtils.xmlBean = xmlBean;
            //更新缓存 更新IPC
            updateIpc(map);
        }
    }

    /**
     * @Description: TODO 定时上报摄像头在线状态
     * @Param: []
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 17:24
     **/
    @Scheduled(cron = "0 0/1 * * * ?") // 每1分钟执行一次
    public void reportStatus() {
        logger.info("【定时任务启动】: 上报所有摄像头状态");
        //获取缓存中的所有IPC的在线离线状态
        List<Command> list = checkStatus();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", list);

        Map<String, String> map = new HashMap<>();
        String url = EnvConfig.Url + "/vf/service/ipc/update/" + EnvConfig.PrjCd + "/" + EnvConfig.Client;
        map.put("type", "1");
        map.put("data", jsonObject.toString());
        map.put("prjCd", "143");
        String result = HttpClientUtils.doPostToServer(url, map);
        try {
            jsonObject = JSONObject.fromObject(result);
            if (StringUtil.isNotEmptyOrNull(jsonObject.getString("success"))) {
                if (StringUtil.isEqual(jsonObject.getString("success"), "true")) {
                    logger.info("更新摄像头在线状态成功");
                } else {
                    logger.error("更新摄像头在线状态失败");
                }
            }
        } catch (Exception e) {
            logger.error("【更新摄像头在线状态异常】");
        }
    }

    public static void updateIpc(Map<String, List<IpcProperties>> map) {
        List<IpcProperties> delList = map.get("delete");
        stop(delList);

        List<IpcProperties> insertList = map.get("insert");
        start(insertList);

        List<IpcProperties> updateList = map.get("update");
        update(updateList);
    }

    /**
     * @param delList
     * @Description: TODO 删除流 更新缓存
     * @Param: []
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/4 18:29
     */
    public static void stop(List<IpcProperties> delList) {
        for (IpcProperties ipcProperties : delList) {
            //移除缓存
            CacheUtils.removeCache(ipcProperties.getId());

            //停止进程
            FFmpegManager manager = new FFmpegManagerImpl();
            logger.debug("【停止ffmpeg任务， 任务ID: " + ipcProperties.getId() + " 】");
            manager.stop(ipcProperties.getId());
        }
    }

    /**
     * @param insertList
     * @Description: TODO 新增摄像头 新增流
     * @Param: []
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/4 18:30
     */
    public static void start(List<IpcProperties> insertList) {
        //获取缓存 更新缓存
        Map<Object, Object> IpcMap = new HashMap<>();
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");
        for (IpcProperties ipcProperties : insertList) {
            logger.info("【新增摄像头 " + ipcProperties.getId() + "】");
            String userName = ipcProperties.getUser();
            String passWd = ipcProperties.getPsw();
            String id = ipcProperties.getId();
            String ipAddr = ipcProperties.getIp();
            String viewUsers = ipcProperties.getViewUsers();
            String controlUsers = ipcProperties.getControlUsers();
            String primaryId = ipcProperties.getPrimaryId();
            String cptPicTime = ipcProperties.getCptPicTime();
            IpcOnvif ipcOnvif = InitIpc.initConnect(userName, passWd, ipAddr, id, viewUsers, controlUsers, primaryId, null);
            ipcOnvif.setCptPicTime(cptPicTime);
            IpcMap.put(id, ipcOnvif);
        }
        //缓存初始化信息
        CacheManager.getInstance().put(FileUtils.CACHE_NAME, "ipc", IpcMap);
    }

    /**
     * param: [updateList]
     * describe: TODO 更新IPC缓存 更新
     * creat_user: fuzq
     * creat_date: 2018/9/4
     **/
    public static void update(List<IpcProperties> updateList) {
        //获取缓存 更新缓存
        Map<Object, Object> IpcMap = new HashMap<>();
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");

        for (IpcProperties ipcProperties : updateList) {
            String userName = ipcProperties.getUser();
            String passWd = ipcProperties.getPsw();
            String id = ipcProperties.getId();
            String ipAddr = ipcProperties.getIp();
            String viewUsers = ipcProperties.getViewUsers();
            String controlUsers = ipcProperties.getControlUsers();
            String primaryId = ipcProperties.getPrimaryId();
            String cptPicTime = ipcProperties.getCptPicTime();
            IpcOnvif ipcOnvif = InitIpc.initConnect(userName, passWd, ipAddr, id, viewUsers, controlUsers, primaryId, null);

            ipcOnvif.setCptPicTime(cptPicTime);
            IpcMap.put(id, ipcOnvif);
        }
        //缓存初始化信息
        CacheManager.getInstance().put(FileUtils.CACHE_NAME, "ipc", IpcMap);
    }

    /**
     * @Description: TODO 获取IPC在线离线状态
     * @Param: []
     * @return: java.util.List<com.bjshfb.vf.client.expos.data.Command>
     * @Author: fuzq
     * @Date: 2018/9/6 16:59
     **/
    public static List<Command> checkStatus() {
        //获取缓存 更新缓存
        Map<Object, Object> IpcMap;
        IpcMap = CacheManager.getInstance().get(FileUtils.CACHE_NAME, "ipc");
        List<Command> list = new ArrayList<>();
        if (StringUtil.isNotEmptyOrNull(IpcMap)) {
            for (Map.Entry<Object, Object> entry : IpcMap.entrySet()) {
                IpcOnvif ipcOnvif = (IpcOnvif) entry.getValue();
                Command command = new Command(ipcOnvif.getIsOnline(), ipcOnvif.getIpcId(), ipcOnvif.getPrimaryId());
                list.add(command);
            }
        }
        return list;
    }
}