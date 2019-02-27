package com.bjshfb.vf.client.util;

import com.bjshfb.vf.client.expos.data.UploadProperty;
import com.bjshfb.vf.client.expos.env.EnvConfig;
import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.shfb.oframe.core.util.common.StringUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.soap.SOAPException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyq
 * @description
 * @create in 2018/6/12 21:38
 */
@Component
@EnableConfigurationProperties(UploadProperty.class)
public class ImageUtils {
    public static final Logger logger = Logger.getLogger(ImageUtils.class);


    //spring 中无法注入静态变量，只能通过间接注入的方式，使用 @AutoWired直接报错，使用Resource时
    // 直接报找到了2个同样的bean，但是我其实只有1个这样的Bean。
    @Resource(name = "uploadProperty")
    private UploadProperty tempUploadProperty;

    private static UploadProperty uploadProperty;

    // 在servlet中 会在构造函数之后执行, 同样可以实现  InitializingBean 接口
    @PostConstruct
    private void init() {
        uploadProperty = tempUploadProperty;
    }

    /**
     * 图片上传，默认支持所有格式的图片, 文件默认最大为 10MB
     *
     * @param inputStream
     * @return 图片存储路径
     */
    public static Map<String, String> uploadImage(InputStream inputStream, Map<String, String> parasMap) throws IOException {
        return uploadImageByAcceptType(inputStream, null, uploadProperty.getMaxSize(), parasMap);
    }

    public static String getPath() {
        return uploadProperty.getPath();
    }

    /**
     * 图片上传，默认支持所有格式的图片
     *
     * @param inputStream
     * @param maxSize     文件最大多少，单位 mb
     * @return 图片存储路径
     */
    public static Map<String, String> uploadImage(InputStream inputStream, int maxSize, Map<String, String> parasMap) throws IOException {
        return uploadImageByAcceptType(inputStream, null, maxSize, parasMap);
    }

    /**
     * 上传图片（可限定文件类型）
     *
     * @param inputStream
     * @param type        图片类型 1表示缩略图 2表示快照截图
     * @param maxSize     文件最大为10MB
     * @return 图片存储路径。
     */
    public static Map<String, String> uploadImageByAcceptType(InputStream inputStream, String type, int maxSize, Map<String, String> parasMap) throws IOException {
        Map<String, String> map = new HashMap<>();
        //获取图片流大小
        int len = 0;
        byte[] data = new byte[1024];
        int size = 0;
//        while ((len = inputStream.read(data)) != -1) {//循环读取inputStream流中的数据，存入文件流fileOutputStream
//            size += 1;
//        }
        size = (int) Math.ceil(maxSize / 1024);
        if (size > maxSize) {
            logger.error("【上传图片类型过大】");
            return null;
        }
        try {
            // 文件处理
            String suffix = ".jpg";//图片后缀
            String prjCd = "143";
            String albId = "111";
            Path path = Paths.get(uploadProperty.getPath(), prjCd, albId);//上传路径
            //创建上传文件
            String filePath = path.toAbsolutePath().toString();
            File fileDir = new File(filePath);
            fileDir.mkdirs();
            String uuid = UUIDUtil.getUid();
            File realFile = new File(fileDir, uuid + suffix);
            IOUtils.copy(inputStream, new FileOutputStream(realFile));

            // 参数处理  返回图片路径
            String tempPath = "/" + prjCd + "/" + albId + "/" + uuid + suffix;

            map.put("path", uploadProperty.getPath().replaceAll("\\.", "") + tempPath);//文件路径
            map.put("name", uuid + suffix);//文件名
            map.put("suffix", suffix);//后缀名
            map.put("relType", "1");//文件类型
        } catch (IOException e) {
            logger.error("【上传图片异常】", e.fillInStackTrace());
        }
        return map;
    }

    /**
     * @Description: //TODO
     * @Param: [type, token, userName, onvifDevice]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 15:07
     **/
    public static JSONObject imageUpload(String ipcId, String type, String token, String userName, OnvifDevice onvifDevice) throws ConnectException, SOAPException {
        //获取屏幕截图，上传到服务器上
        Map<String, String> map = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        map = HttpClientUtils.getSnapshot(onvifDevice.getMedia().getSnapshotUri(token), userName, onvifDevice.getEncryptedPassword(), type);

        String url = "";
        //调用接口  更新数据库
        url = EnvConfig.Url + "/vf/service/ipc/file/" + EnvConfig.PrjCd + "/" + EnvConfig.Client;
        //调用上传图片服务
        Map<String, String> parasMap = new HashMap<>();
        if (StringUtil.isNotEmptyOrNull(map.get("path"))) {
            parasMap.put("docPath", map.get("path"));
        } else {
            parasMap.put("docPath", "");
        }
        if (StringUtil.isNotEmptyOrNull(map.get("name"))) {
            parasMap.put("docName", map.get("name"));
        } else {
            parasMap.put("docName", "");
        }
        parasMap.put("docTypeName", "1");
        parasMap.put("prjCd", "143");
        parasMap.put("relType", "1");
        parasMap.put("relId", ipcId);
        String result = HttpClientUtils.doPostToServer(url, parasMap);

        try {
            jsonObject = JSONObject.fromObject(result);
            if (StringUtil.isNotEmptyOrNull(jsonObject.getString("success"))) {
                if (StringUtil.isEqual(jsonObject.getString("success"), "true")) {
                    return jsonObject;
                }
            } else {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "上传图片错误");
                jsonObject.put("date", "");
            }
        } catch (Exception o) {
            logger.error("【调用图片上传服务返回错误】");
        }
        return jsonObject;
    }
}
