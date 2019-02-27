package com.bjshfb.vf.client.expos.func;

import com.bjshfb.vf.client.expos.data.IpcOnvif;
import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.bjshfb.vf.client.util.CacheUtils;
import com.bjshfb.vf.client.util.ImageUtils;
import com.shfb.oframe.core.util.common.StringUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;

/**
 * @program: helm
 * @description: 捕获快照
 * @author: fuzq
 * @create: 2018-08-29 19:01
 **/
public class GetSnapshotURI {
    public static final Logger logger = Logger.getLogger(GetSnapshotURI.class);

    /**
     * @Description: TODO 抓取图片，上传到服务器
     * @Param: [ipcId] 摄像头Id
     * @return: java.lang.String
     * @Author: fuzq
     * @Date: 2018/9/6 11:27
     **/
    public static JSONObject getSnapshotURI(String ipcId) {
        JSONObject jsonObject = new JSONObject();

        IpcOnvif ipcOnvif = CacheUtils.getIpcCache(ipcId);
        OnvifDevice onvifDevice = ipcOnvif.getOnvifDevice();
        String profileToken = null;
        if (ipcOnvif != null) {
            profileToken = ipcOnvif.getProfile().getToken();
        } else {
            jsonObject.put("success", false);
            jsonObject.put("date", "当前摄像头不存在");
            jsonObject.put("errMsg", "");
            return jsonObject;
        }
        if (StringUtil.isNotEmptyOrNull(profileToken) && StringUtil.isNotEmptyOrNull(ipcOnvif.getOnvifDevice())) {
            try {
                String url = ipcOnvif.getOnvifDevice().getMedia().getSnapshotUri(profileToken);
                ImageUtils.imageUpload(ipcId,"2", profileToken, ipcOnvif.getIpcUName(), onvifDevice);
            } catch (SOAPException e) {
                logger.error("【上传快照错误】:" , e);
            } catch (ConnectException e) {
                logger.error("【上传快照错误】:" , e);
            }
        }
        jsonObject.put("success", true);
        jsonObject.put("date", "");
        jsonObject.put("errMsg", "");
        return jsonObject;
    }
}
