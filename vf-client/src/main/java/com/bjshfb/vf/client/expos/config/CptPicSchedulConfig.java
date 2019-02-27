package com.bjshfb.vf.client.expos.config;

import com.bjshfb.vf.client.expos.data.IpcOnvif;
import com.bjshfb.vf.client.expos.func.GetSnapshotURI;
import com.bjshfb.vf.client.util.CacheUtils;
import com.shfb.oframe.core.util.common.StringUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @program: helm
 * @description: 定时截图
 * @author: fuzq
 * @create: 2018-09-10 15:48
 **/
@Component
@EnableScheduling
public class CptPicSchedulConfig implements SchedulingConfigurer {
    /**
     * Date 转换成 Corn表达式
     * */
    private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";

    public static final Logger logger = Logger.getLogger(CptPicSchedulConfig.class);
    //时间周期表达式
    public static String cron;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(20));

        //从缓存中获取当前摄像头的定时截图时间
        Map<Object, Object> IpcMap = CacheUtils.getAllIpcCache();
        if (IpcMap != null) {
            for (Map.Entry<Object, Object> entry: IpcMap.entrySet()) {
                //项目部署时，会在这里执行一次，从缓存中拿到当前数据的定时执行时间
                IpcOnvif ipcOnvif = (IpcOnvif) entry.getValue();
                if (StringUtil.isNotEmptyOrNull(ipcOnvif.getCptPicTime())) {
                    //处理定时截图时间  逗号隔开
                    List<String> result = Arrays.asList(ipcOnvif.getCptPicTime().split(","));
                    for (String time : result) {
                        //拼接corn表达式  将时间转换成表达式
                        cron = time;
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                //任务逻辑代码部分.
                                logger.info("【摄像头 : " + ipcOnvif.getIpcId() +" 定时截图】");
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject = GetSnapshotURI.getSnapshotURI(ipcOnvif.getIpcId());

                                    if (StringUtil.isNotEmptyOrNull(jsonObject.getString("success")) && StringUtil.isEqual("true", jsonObject.getString("success"))) {
                                        logger.info("【摄像头: " + ipcOnvif.getIpcId() + " 定时截图成功】");
                                    }
                                } catch (Exception e) {
                                    logger.error("【定时截图失败】");
                                }
                            }
                        };
                        Trigger trigger = new Trigger() {
                            @Override
                            public Date nextExecutionTime(TriggerContext triggerContext) {
                                //任务触发，可修改任务的执行周期.
                                //每一次任务触发，都会执行这里的方法一次，重新获取下一次的执行时间
                                cron = time;
                                CronTrigger trigger = new CronTrigger(cron);
                                Date nextExec = trigger.nextExecutionTime(triggerContext);
                                return nextExec;
                            }
                        };
                        taskRegistrar.addTriggerTask(task, trigger);
                    }
                }
            }
        }
    }

    /***
     *
     * @param date 时间
     * @return  cron类型的日期
     */
    public static String getCron(final Date  date){
        SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        String formatTimeStr = "";
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    /***
     *
     * @param cron Quartz cron的类型的日期
     * @return  Date日期
     */

    public static Date getDate(final String cron) {

        if(cron == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        Date date = null;
        try {
            date = sdf.parse(cron);
        } catch (ParseException e) {
            logger.error("【Date时间转换Corn表达式错误】", e);
        }
        return date;
    }
}