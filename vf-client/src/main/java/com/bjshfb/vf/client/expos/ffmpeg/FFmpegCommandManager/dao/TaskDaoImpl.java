package com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.dao;

import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.FFmpegManager;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.entity.TaskEntity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 任务信息持久层实现
 * 
 * @author eguid
 * @since jdk1.7
 * @version 2016年10月29日
 */
public class TaskDaoImpl implements TaskDao {
    public static final Logger logger = Logger.getLogger(TaskDaoImpl.class);

    /**
	 * 存放任务信息
	 * */
//	public static ConcurrentMap<String, TaskEntity> map = null;

	public TaskDaoImpl(int size) {
//		map = new ConcurrentHashMap<>(size);
	}

	@Override
	public TaskEntity get(String id) {
		return FFmpegManager.map.get(id);
	}

	@Override
	public Collection<TaskEntity> getAll() {
		return FFmpegManager.map.values();
	}

	@Override
	public int add(TaskEntity taskEntity) {
		String id = taskEntity.getId();
		if (id != null && !FFmpegManager.map.containsKey(id)) {
			FFmpegManager.map.put(taskEntity.getId(), taskEntity);
			if(FFmpegManager.map.get(id)!=null)
			{
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int remove(String id) {
		if(FFmpegManager.map.remove(id) != null){
			return 1;
		};
		return 0;
	}

	@Override
	public int removeAll() {
		int size = FFmpegManager.map.size();
		try {
			FFmpegManager.map.clear();
		} catch (Exception e) {
			return 0;
		}
		return size;
	}

	@Override
	public boolean isHave(String id) {
		return FFmpegManager.map.containsKey(id);
	}

	@Override
	public List<String> getMap() {
		List<String> list = new ArrayList<>();
		for (ConcurrentMap.Entry<String, TaskEntity> entry: FFmpegManager.map.entrySet()) {
			list.add(entry.getKey());
		}
		return list;
	}
}
