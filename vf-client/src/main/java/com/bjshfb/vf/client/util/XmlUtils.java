package com.bjshfb.vf.client.util;

import com.bjshfb.vf.client.expos.data.IpcProperties;
import com.shfb.oframe.core.util.common.Encrypt;
import com.shfb.oframe.core.util.common.XmlBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: helm
 * @description: 定时监测xml更新状态
 * @author: fuzq
 * @create: 2018-09-04 16:59
 **/
public class XmlUtils {
    /**
     * 本地静态变量 : 存储IPC摄像头配置文件
     */
    public static XmlBean xmlBean = new XmlBean();

    public static Map<String, IpcProperties> xmlbeanToMap(XmlBean xmlBean) {
        Map<String, IpcProperties> map = new HashMap<>();
        if (xmlBean != null) {
            int num = xmlBean.getListNum("Rows.Row");
            for (int i = 0; i < num; i++) {
                IpcProperties ipcProperties = new IpcProperties();
                ipcProperties.setId(xmlBean.getStrValue("Rows.Row[" + i + "].id"));
                ipcProperties.setPrimaryId(xmlBean.getStrValue("Rows.Row[" + i + "].camId"));
                ipcProperties.setIp(xmlBean.getStrValue("Rows.Row[" + i + "].ip"));
                ipcProperties.setUser(xmlBean.getStrValue("Rows.Row[" + i + "].user"));
                ipcProperties.setPsw(xmlBean.getStrValue("Rows.Row[" + i + "].psw"));
                ipcProperties.setViewUsers(xmlBean.getStrValue("Rows.Row[" + i + "].viewUsers"));
                ipcProperties.setControlUsers(xmlBean.getStrValue("Rows.Row[" + i + "].controlUsers"));

                map.put(ipcProperties.getId(), ipcProperties);
            }
        }

        return map;
    }

    public static Map<String, List<IpcProperties>> diff(Map<String, IpcProperties> oldMap, Map<String, IpcProperties> newMap) {
        Map<String, List<IpcProperties>> map = new HashMap<>();
        List<IpcProperties> delList = new ArrayList<>();
        List<IpcProperties> updateList = new ArrayList<>();
        List<IpcProperties> insertList = new ArrayList<>();
        //比较两个map是否一致
        for (Map.Entry<String, IpcProperties> oldEntry : oldMap.entrySet()) {
            //判断是否删除摄像头
            if (!newMap.containsKey(oldEntry.getKey())) {
                //该摄像头已经删除
                delList.add(oldEntry.getValue());
                break;
            }
            //判断是否更新属性值
            for (Map.Entry<String, IpcProperties> newEntry : newMap.entrySet()) {
                if (!oldMap.containsKey(newEntry.getKey())) {
                    //判断是否新增摄像头
                    insertList.add(newEntry.getValue());
                }
                if (oldEntry.getKey().equals(newEntry.getKey())) {
                    //判断两个对象不相等
                    if (!oldEntry.getValue().equals(newEntry.getValue())) {
                        updateList.add(newEntry.getValue());
                    }
                }
            }
        }
        map.put("delete", delList);
        map.put("update", updateList);
        map.put("insert", insertList);
        return map;
    }
}
