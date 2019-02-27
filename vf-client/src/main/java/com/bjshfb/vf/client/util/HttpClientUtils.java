package com.bjshfb.vf.client.util;

import com.bjshfb.vf.client.expos.env.EnvConfig;
import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.bjshfb.vf.client.ipc.onvif.soap.devices.PtzDevices;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.PTZStatus;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.Profile;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPException;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Author fuzq
 * @Description: TODO httpclient 工具类
 * @Date: 14:37 2018/6/6
 */
public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static CloseableHttpClient getHttpClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            //不进行主机名验证
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(),
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(100);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setDefaultCookieStore(new BasicCookieStore())
                    .setConnectionManager(cm).build();
            return httpclient;
        } catch (KeyManagementException e) {
            logger.error("调用【getHttpClient】方法异常，错误信息:[{}]", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("调用【getHttpClient】方法异常，错误信息:[{}]", e.getMessage());
        } catch (KeyStoreException e) {
            logger.error("调用【getHttpClient】方法异常，错误信息:[{}]", e.getMessage());
        }
        return HttpClients.createDefault();
    }

    public static String get(String uri, Map<String, Object> params, Map<String, String> headers) {
        org.apache.http.client.HttpClient client = getHttpClient();
        return get(client, uri, params, headers);
    }

    public static String get(org.apache.http.client.HttpClient client, String uri, Map<String, Object> params, Map<String, String> headers) {
        logger.debug("调用【get】方法开始，请求参数信息:[uri = {}, params = {}, headers = {}]", uri, params, headers);
        String result = null;
        try {
            result = StringUtils.EMPTY;
            String fullUrl = buildUrlWithParams(uri, params);
            HttpGet httpGet = new HttpGet(fullUrl);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpResponse httpResponse = client.execute(httpGet);
            InputStream input = httpResponse.getEntity().getContent();
            if (null != input) {
                try {
                    result = IOUtils.toString(input, "UTF-8");
                } catch (IOException e) {
                    throw e;
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }
        } catch (IOException e) {
            logger.error("调用【getHttpClient】方法异常，错误信息:[{}]", e.getMessage());
        }
        logger.debug("调用【get】方法执行结束，执行结果:[result = {}]", result);
        return result;
    }


    private static String buildUrlWithParams(String uri, Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(uri);
        if (null != params && !params.isEmpty()) {
            if (!uri.contains("?")) {
                urlBuilder.append("?");
            }
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String valueStr = null == value ? "" : value.toString();
                if (!urlBuilder.toString().endsWith("?")) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(key).append("=").append(URLEncoder.encode(valueStr, "utf-8"));
            }
        }
        String fullUrl = urlBuilder.toString();
        return fullUrl;
    }

    public static String doPost(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * @Description: TODO 调用server端 获取初始化配置信息
     * @Param: []
     * @return: com.shfb.oframe.core.util.common.XmlBean
     * @Author: fuzq
     * @Date: 2018/9/6 14:17
     **/
    public static XmlBean getXmlBean() {//拼接Url
        try {
            Map<String, String> map = new HashMap<>();
            map.put("prjCd", EnvConfig.PrjCd);
            String result = doPost(EnvConfig.Url + "/vf/service/ipc/info/" + EnvConfig.PrjCd + "/" + EnvConfig.Client, map);
            logger.warn("拉取配置文件信息结果:----------" + result);
            if (StringUtil.isEmptyOrNull(result)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.fromObject(result);

            if (jsonObject.get("success") != null) {
                if (jsonObject.getString("success").equals("true")) {
                    String s = jsonObject.getString("data");
                    if (StringUtil.isNotEmptyOrNull(s)) {
                        XmlBean xmlBean = new XmlBean(s);
                        return xmlBean;
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("---------------服务返回错误------------------" + e.getStackTrace());
        }
        return null;
    }

    /**
     * @Description: TODO 通过url返回快照的图片流  加入权限验证
     * @Param: [uri, userName, passWord] 图片快照url, IPC用户名(不加密), IPC密码(加密)
     * @return: java.io.InputStream
     * @Author: fuzq
     * @Date: 2018/9/6 10:18
     **/
    public static Map<String, String> getSnapshot(String uri, String userName, String passWord, String type) throws SOAPException, ConnectException {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        Map<String, String> map = new HashMap<>();
        try {
            //这里返回的加密的密码，通过base64进行加密
            String author = (userName + ":" + passWord);
            logger.info("【建立http连接，开始下载图片】");
            URL url = new URL(uri);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Authorization", author);
            httpConn.connect();
            //获取图片流
            inputStream = httpConn.getInputStream();

//            int len = 0;
//            byte[] data = new byte[1024];
////            将下载图片保存到本地
//            FileOutputStream fileOutputStream = null;
//            fileOutputStream = new FileOutputStream("D:\\test.jpg");//初始化一个FileOutputStream对象。
//            while ((len = inputStream.read(data)) != -1) {//循环读取inputStream流中的数据，存入文件流fileOutputStream
//                fileOutputStream.write(data, 0, len);
//            }
            //执行图片上传操作
            try {
                //上传图片
                map = ImageUtils.uploadImageByAcceptType(inputStream, type, 10, null);
            } catch (IOException e) {
                logger.error("【初始化摄像头，上传初始摄像头位置图失败】");
            }
        } catch (Exception e) {
            logger.error("【下载图片错误】");
        } finally {
            if (httpConn != null) {
//                httpConn.disconnect();
                logger.info("【关闭http连接，下载图片结束】");
            }
        }
        return map;
    }

    /**
     * @Description: TODO 调用server端接口 更新数据 上报状态
     * @Param: [url, map]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 14:19
     **/
    public static String doPostToServer(String url, Map<String, String> map) {
        String result = doPost(url, map);
        return result;
    }

    public static void main(String[] args) throws ConnectException, SOAPException {
        OnvifDevice nvt = new OnvifDevice("192.168.1.199", "admin", "shfb_718");

//        getSnapshot("http://192.168.1.199/onvif-http/snapshot?Profile_1", "admin", nvt.getEncryptedPassword());

//        HttpURLConnection httpConn = null;
//        try {
//            //url远程接口
//            String strURL = "http://192.168.1.199/onvif-http/snapshot?Profile_1";
//            //用户名
//            String username = "admin";
//            //密码
//            String password = "shfb_718";
//            OnvifDevice nvt = new OnvifDevice("192.168.1.199", username, password);
//            password = nvt.getEncryptedPassword();
//
//            //原先使用的时com.sun.org.apache.xml.internal.security.utils.Base64，这个包虽然在jdk中，
//            //但并不是标准的包，所以在gradle编译打包时总是无法引入改包，所以不用这个包
////            String author = "Basic " + Base64.encode((username+":"+ password).getBytes());
//            //使用jdk1.8中的java.util.Base64来对字符串加密
//            String author = (username + ":" + password);
//
//            URL url = new URL(strURL);
//            httpConn = (HttpURLConnection) url.openConnection();
//            httpConn.setRequestMethod("GET");
//            httpConn.setRequestProperty("Authorization", author);
//            httpConn.connect();
//
//            FileOutputStream fileOutputStream = null;
//            InputStream inputStream = httpConn.getInputStream();
//            int len = 0;
//            byte[] data = new byte[1024];
//
//            fileOutputStream = new FileOutputStream("D:\\test.jpg");//初始化一个FileOutputStream对象。
//            while ((len = inputStream.read(data)) != -1) {//循环读取inputStream流中的数据，存入文件流fileOutputStream
//                fileOutputStream.write(data, 0, len);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (httpConn != null) {
//                httpConn.disconnect();
//            }
//        }
    }
}