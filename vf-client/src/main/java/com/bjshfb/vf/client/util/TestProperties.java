package com.bjshfb.vf.client.util;



import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.FFmpegManager;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.FFmpegManagerImpl;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.entity.TaskEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: helm
 * @description: 测试
 * @author: fuzq
 * @create: 2018-09-03 16:10
 **/
public class TestProperties {
    public static void test1() throws InterruptedException{
        FFmpegManager manager = new FFmpegManagerImpl();
        Map<String,String> map = new HashMap<String,String>();
        map.put("appName", "test123");
        map.put("input", "rtsp://admin:shfb_718@192.168.1.198");
        map.put("output", "rtmp://127.0.0.1:1935/hls/fzq");
        map.put("codec", "h264");
        map.put("fmt", "flv");
        map.put("fps", "25");
        map.put("rs", "640x360");
        map.put("twoPart", "2");
        // 执行任务，id就是appName，如果执行失败返回为null
        String id = manager.start(map);
        System.out.println(id);
        // 通过id查询
        TaskEntity info = manager.query(id);
        System.out.println(info);
        // 查询全部
        Collection<TaskEntity> infoList = manager.queryAll();
        System.out.println(infoList);
        Thread.sleep(30000);
        // 停止id对应的任务
        manager.stop(id);
    }

    public static void main(String[] args) throws InterruptedException {
        test1();
    }
}
