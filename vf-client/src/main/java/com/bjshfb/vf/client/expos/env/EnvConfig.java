package com.bjshfb.vf.client.expos.env;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: helm
 * @description: 环境变量
 * @author: fuzq
 * @create: 2018-09-10 19:03
 **/
@Configuration
public class EnvConfig {
    public static final Logger logger = Logger.getLogger(EnvConfig.class);

    public static String Url;
    public static String Client;
    public static String PrjCd;
    public static String NettyAddr;
    public static String ConfigAddr;

    @Value("${com.bjshfb.url}")
    private String url;

    @Value("${com.bjshfb.client}")
    private String client;

    @Value("${com.bjshfb.prjCd}")
    private String prjCd;

    @Value("${com.bjshfb.nettyAddr}")
    private String nettyAddr;

    @Value("${spring.profiles.path}")
    private String configAddr;

    /**
     * @Description: TODO 初始化环境变量
     * @Param: []
     * @return: java.lang.String
     * @Author: fuzq
     * @Date: 2018/9/10 19:44
     **/
    @Bean
    public String initiali() {
        Url = url;
        Client = client;
        PrjCd = prjCd;
        NettyAddr = nettyAddr;
        ConfigAddr = configAddr;
        return "";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getPrjCd() {
        return prjCd;
    }

    public void setPrjCd(String prjCd) {
        this.prjCd = prjCd;
    }

    public String getNettyAddr() {
        return nettyAddr;
    }

    public void setNettyAddr(String nettyAddr) {
        this.nettyAddr = nettyAddr;
    }

    public String getConfigAddr() {
        return configAddr;
    }

    public void setConfigAddr(String configAddr) {
        this.configAddr = configAddr;
    }
}
