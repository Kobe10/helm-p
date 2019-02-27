package com.bjshfb.vf.client.util;

import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @program: helm
 * @description: 文件工具类
 * @author: fuzq
 * @create: 2018-08-29 16:41
 **/
public class FileUtils {
    /**
     * 缓存大类的名称
     */
    public static final String CACHE_NAME = "SYS_CFG_CACHE";
    /**
     * utf-8 编码
     */
    private static final String U8 = "utf-8";

    /**
     * 生成XmlBean 根据文件路径
     *
     * @param filePath Xml文件
     * @return XmlBean 对象
     */
    public static XmlBean createXmlByFile(String filePath) {
        filePath = StringUtil.formatRelFilePath(filePath);
        return new XmlBean(stringFile(filePath));
    }

    /**
     * 获取jar包配置文件方式
     *
     * */
    private InputStream getInputStream(String filePath) {
        //获取文件名称
        InputStream in = getClass().getResourceAsStream(filePath);
        return in;
    }

    /**
     * 读字符串类型的文件
     *
     * @param file 文件对象
     * @return 对应文件字符串
     */
    public static String stringFile(String file) {
        InputStreamReader reader = null;
        InputStream inputStream = null;
        BufferedReader br = null;
        StringBuilder configStr = new StringBuilder();
        try {
            inputStream = new FileUtils().getInputStream(file);
            reader = new InputStreamReader(inputStream, U8);
            br = new BufferedReader(reader);
            String temp;
            while ((temp = br.readLine()) != null) {
                configStr.append(temp).append("\r\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return configStr.toString();
    }

    /**
     * 下载文件
     *
     * @param response
     * @param contextType
     * @param outputFileName
     * @param downloadFile
     * @param agent
     * @param needOpen
     * @param cache
     */
    public static void downloadFile(HttpServletResponse response, String contextType, String outputFileName, File downloadFile, String agent, Boolean needOpen, boolean cache) {
        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        String tempName = outputFileName.toLowerCase();
        if (!tempName.endsWith("doc") && !tempName.endsWith("docx")) {
            if (!tempName.endsWith("xls") && !tempName.endsWith("xlsx")) {
                if (!tempName.endsWith("jpg") && !tempName.endsWith("jpeg")) {
                    if (tempName.endsWith("gif")) {
                        contextType = "image/gif";
                    } else if (tempName.endsWith("bmp")) {
                        contextType = "application/x-bmp";
                    } else if (tempName.endsWith("png")) {
                        contextType = "application/x-png";
                    } else if (StringUtil.isEmptyOrNull(contextType)) {
                        contextType = "application/download";
                    }
                } else {
                    contextType = "image/jpeg";
                }
            } else {
                contextType = "application/x-xls";
            }
        } else {
            contextType = "application/msword";
        }

        if (needOpen == null) {
            if (!StringUtil.isEqual("application/download", contextType)) {
                needOpen = Boolean.valueOf(true);
            } else {
                needOpen = Boolean.valueOf(false);
            }
        }

        try {
            outputStream = response.getOutputStream();
            response.reset();
            response.setContentType(contextType);
            if (cache) {
                Long modifiedTime = Long.valueOf(downloadFile.lastModified());
                response.setDateHeader("Last-Modified", modifiedTime.longValue());
                response.setDateHeader("Expires", modifiedTime.longValue() + 86400000L);
                response.setHeader("Cache-Control", "max-age=86400");
                response.setHeader("Pragma", "Pragma");
            }

            String tempFileName = StringUtil.replace(outputFileName, "+", "%20");
            tempFileName = processFileName(tempFileName, agent);
            if (!needOpen.booleanValue()) {
                response.setHeader("content-disposition", "attachment; filename=" + tempFileName);
            } else {
                response.setHeader("content-disposition", "inline;filename=" + tempFileName);
            }

            // 跨域
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET");
            response.setHeader("X-Powered-By", "3.2.1");

            byte[] byteArr = new byte[1024];
            long fileLength = downloadFile.length();
            String length1 = String.valueOf(fileLength);
            response.setHeader("Content_Length", length1);
            fileInputStream = new FileInputStream(downloadFile);
            boolean var15 = false;

            int n;
            while ((n = fileInputStream.read(byteArr)) != -1) {
                outputStream.write(byteArr, 0, n);
            }

            response.flushBuffer();
        } catch (IOException var23) {

            var23.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException var22) {
                    var22.printStackTrace();
                }
            }

        }
    }

    private static String processFileName(String fileName, String agent) throws IOException {
        String codedfilename = null;
        if (null == agent || -1 == agent.indexOf("Firefox")) {
            codedfilename = URLEncoder.encode(fileName, "UTF-8");
        }

        return codedfilename;
    }
}
