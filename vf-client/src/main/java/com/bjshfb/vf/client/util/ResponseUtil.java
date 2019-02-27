package com.bjshfb.vf.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author shfb
 * @Description: TODO Ajax请求响应处理
 * @Date: 10:03 2018/5/2
 */
public final class ResponseUtil {
    /**
     * 日志输出
     */
//    public static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    /**
     * 编码格式
     */
    private static final String PAGE_ENCODING = "UTF-8";

    /**
     * 构造函数私有化
     */
    private ResponseUtil() {

    }

    /**
     * 把信息通过输出流输出到界面
     *
     * @param response HttpServletResponse对象-Action中通过HttpServletResponse response =
     *                 ServletActionContext.getResponse()获取
     * @param printObj String-要输出到界面的字符串
     */
    public static void printAjax(HttpServletResponse response, Object printObj) {
        response.setContentType("text/html; charset=" + PAGE_ENCODING);
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.print(printObj);
            out.close();
        } catch (IOException e) {
//            logger.error("Error:{}!", e.getMessage());
        }
    }

    /**
     * 把信息通过输出流输出到界面
     *
     * @param response HttpServletResponse对象-Action中通过HttpServletResponse response =
     *                 ServletActionContext.getResponse()获取
     * @param printObj String-要输出到界面的字符串
     */
    public static void printAjax1(HttpServletResponse response, Object printObj) {
        response.setContentType("text/html; charset=" + PAGE_ENCODING);
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.print(printObj);
            out.close();
        } catch (IOException e) {
//            logger.error("Error:{}!", e.getMessage());
        }
    }
}
