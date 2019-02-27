package com.bjshfb.vf.client.expos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;

/**
 * @program: helm
 * @description: Ffmpeg推流路径配置文件  首先加载本机的配置文件 如果没有 再加载classpath路径下的
 * @author: fuzq
 * @create: 2018-09-25 11:55
 **/
@Configuration
@Order(1)
@PropertySource(value = {"classpath:ffmpeg-addr.properties", "file:${spring.profiles.path}/ffmpeg-addr.properties" }, ignoreResourceNotFound = true,
        encoding = "UTF-8", name = "ffmpeg-addr.properties")
public class FfmpegAddrConfig {
    @Value("${ffmpegaddr}")
    private String ffmpegaddr;

    public String getFfmpegAddr() {

        return ffmpegaddr;
    }

    public void setFfmpegAddr(String ffmpegAddr) {
        this.ffmpegaddr = ffmpegAddr;
    }
}


