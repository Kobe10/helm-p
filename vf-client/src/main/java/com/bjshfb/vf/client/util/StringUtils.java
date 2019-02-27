package com.bjshfb.vf.client.util;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: helm
 * @description: 字符串工具类
 * @author: fuzq
 * @create: 2018-09-18 17:19
 **/
public class StringUtils {
    /**
     * @Description: TODO 将${} 替换成 对应的值
     * @Param: [content, map]
     * @return: java.lang.String
     * @Author: fuzq
     * @Date: 2018/9/25 15:05
     **/
    public static String renderString(String content, Map<String, String> map) {
        Set<Map.Entry<String, String>> sets = map.entrySet();
        for (Map.Entry<String, String> entry : sets) {
            String regex = "\\{" + entry.getKey() + "\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            content = matcher.replaceAll(entry.getValue());
        }
        return content;
    }
}
