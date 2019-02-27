package com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager;

import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.config.FFmpegConfig;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.dao.TaskDao;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.entity.TaskEntity;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.service.CommandAssembly;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.service.TaskHandler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.util.PropertiesUtil.load;


/**
 * FFmpeg命令操作管理器，可执行FFmpeg命令/停止/查询任务信息
 *
 * @author eguid
 * @version 2016年10月29日
 * @since jdk1.7
 */
public interface FFmpegManager {

    public static FFmpegConfig config = (FFmpegConfig) load("loadFFmpeg.properties", FFmpegConfig.class);

    /**
     * 存放任务信息
     */
    public static ConcurrentMap<String, TaskEntity> map = new ConcurrentHashMap<>();

    /**
     * 初始化：存储每一个执行进程的停止状态  初始状态为0  如果是通过接口停止的状态为1，如果是通过其他渠道停止的状态为2 key为进程type--也就是每个摄像头的id
     */
    public static ConcurrentHashMap ffmpegStatus = new ConcurrentHashMap();

    /**
     * 注入自己实现的持久层
     *
     * @param taskDao
     */
    public void setTaskDao(TaskDao taskDao);

    /**
     * 注入ffmpeg命令处理器
     *
     * @param taskHandler
     */
    public void setTaskHandler(TaskHandler taskHandler);

    /**
     * 注入ffmpeg命令组装器
     *
     * @param commandAssembly
     */
    public void setCommandAssembly(CommandAssembly commandAssembly);

    /**
     * 通过命令发布任务（默认命令前不加FFmpeg路径）
     *
     * @param id      - 任务标识
     * @param command - FFmpeg命令
     * @return
     */
    public String start(String id, String command);

    /**
     * 通过命令发布任务
     *
     * @param id      - 任务标识
     * @param commond - FFmpeg命令
     * @param hasPath - 命令中是否包含FFmpeg执行文件的绝对路径
     * @return
     */
    public String start(String id, String commond, boolean hasPath);

    /**
     * 通过组装命令发布任务
     *
     * @param assembly -组装命令（详细请参照readme文档说明）
     * @return
     */
    public String start(Map<String, String> assembly);


    /**
     * 停止任务
     *
     * @param id
     * @return
     */
    public boolean stop(String id);

    /**
     * 停止全部任务
     *
     * @return
     */
    public int stopAll();

    /**
     * 通过id查询任务信息
     *
     * @param id
     */
    public TaskEntity query(String id);

    /**
     * 查询全部任务信息
     */
    public Collection<TaskEntity> queryAll();
}
