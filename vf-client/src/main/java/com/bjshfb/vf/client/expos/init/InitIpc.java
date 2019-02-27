package com.bjshfb.vf.client.expos.init;

import com.bjshfb.vf.client.expos.config.FfmpegAddrConfig;
import com.bjshfb.vf.client.expos.data.IpcOnvif;
import com.bjshfb.vf.client.expos.data.IpcPosition;
import com.bjshfb.vf.client.expos.data.Precision;
import com.bjshfb.vf.client.expos.env.EnvConfig;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.FFmpegManager;
import com.bjshfb.vf.client.expos.ffmpeg.FFmpegCommandManager.FFmpegManagerImpl;
import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.bjshfb.vf.client.ipc.onvif.soap.devices.PtzDevices;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.FloatRange;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.PTZStatus;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.Profile;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.Vector2D;
import com.bjshfb.vf.client.util.FileUtils;
import com.bjshfb.vf.client.util.HttpClientUtils;
import com.bjshfb.vf.client.util.ImageUtils;
import com.bjshfb.vf.client.util.XmlUtils;
import com.shfb.oframe.core.util.cache.CacheManager;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: helm
 * @description: 初始化Ipc信息
 * @author: fuzq
 * @create: 2018-08-29 16:22
 **/
@Component
@Order(2)
public class InitIpc implements ApplicationRunner {

    @Autowired
    private FfmpegAddrConfig ffmpegAddrConfig;
    /**
     * 日志处理
     */
    public static final Logger logger = Logger.getLogger(InitIpc.class);

    public static String ffmpegAddr = "";

    /**
     * 获取初始化xml文件
     */
    public void init() {
        ffmpegAddr = ffmpegAddrConfig.getFfmpegAddr();
        logger.info("【==========初始化IPC信息==========】");
        XmlBean flowXml = HttpClientUtils.getXmlBean();
        if (flowXml == null) {
            logger.error("【拉取配置文件失败，初始化失败，请重启服务】");
        } else {
            //初始化
            XmlUtils.xmlBean = flowXml;
            int num = flowXml.getListNum("Rows.Row");
            Map<Object, Object> IpcMap = new HashMap<>();
            String userName = "";
            String passWd = "";
            String ipAddr = "";
            String id = "";
            String viewUsers = "";
            String controlUsers = "";
            String primaryId = "";
            String cptPicTime = "";
            String camRestPos = "";

            //初始化所有摄像头连接信息 进行连接  -----  优化多线程处理连接
            for (int i = 0; i < num; i++) {
                userName = flowXml.getStrValue("Rows.Row[" + i + "].user");
                passWd = flowXml.getStrValue("Rows.Row[" + i + "].psw");
                id = flowXml.getStrValue("Rows.Row[" + i + "].id");
                ipAddr = flowXml.getStrValue("Rows.Row[" + i + "].ip");
                viewUsers = flowXml.getStrValue("Rows.Row[" + i + "].viewUsers");
                controlUsers = flowXml.getStrValue("Rows.Row[" + i + "].controlUsers");
                primaryId = flowXml.getStrValue("Rows.Row[" + i + "].camId");//主键
                cptPicTime = flowXml.getStrValue("Rows.Row[" + i + "].cptPicTime");//定时截图时间
                camRestPos = flowXml.getStrValue("Rows.Row[" + i + "].camRestPos");//初始化位置
                IpcOnvif ipcOnvif = initConnect(userName, passWd, ipAddr, id, viewUsers, controlUsers, primaryId, camRestPos);
                ipcOnvif.setCptPicTime(cptPicTime);
                IpcMap.put(id, ipcOnvif);
            }
            //缓存初始化信息
            CacheManager.getInstance().put(FileUtils.CACHE_NAME, "ipc", IpcMap);

            //缓存精度信息
            Precision precision = initPrecision();
            CacheManager.getInstance().put(FileUtils.CACHE_NAME, "precision", precision);
        }
    }

    /**
     * 初始化连接信息
     */
    public static IpcOnvif initConnect(String userName, String passWd, String ipAddr, String id, String viewUsers, String controlUsers, String primaryId, String camRestPos) {
        IpcOnvif ipcOnvif = new IpcOnvif();
        try {
            //初始化连接信息
            OnvifDevice nvt = new OnvifDevice(ipAddr, userName, passWd);
            List<Profile> profiles = nvt.getDevices().getProfiles();
            if (profiles.size() <= 0) {
                logger.error("=========【初始化摄像头：  id:【" + id + "】,  ip地址为:【" + ipAddr + "】  异常】========");
            } else {
                //处理鉴权用户
                List<String> viewList = Arrays.asList(StringUtils.split(viewUsers, ","));
                List<String> controlList = Arrays.asList(StringUtils.split(controlUsers, ","));

                ipcOnvif.setIpcId(id);
                ipcOnvif.setIpcUName(userName);
                ipcOnvif.setIpcPwd(passWd);
                ipcOnvif.setIpcIp(ipAddr);
                ipcOnvif.setProfiles(profiles);
                ipcOnvif.setViewUsers(viewList);
                ipcOnvif.setControlUsers(controlList);
                ipcOnvif.setBasePwd(nvt.getEncryptedPassword());
                ipcOnvif.setPrimaryId(primaryId);
                //设置在线
                ipcOnvif.setIsOnline("0");

                String commandModel = ffmpegAddr;
                //获取信号流输出地址
                Map<String, String> map = new HashMap<>();
                map.put("user", userName);
                map.put("passwd", passWd);
                map.put("ip", ipAddr);
                map.put("ipcId", id);
                commandModel = com.bjshfb.vf.client.util.StringUtils.renderString(commandModel, map);
                System.out.println("【命令】" + commandModel);
                if (profiles.size() > 0) {
                    ipcOnvif.setProfile(profiles.get(0));
                }
                ipcOnvif.setOnvifDevice(nvt);

                //初始化位置信息
                PtzDevices ptzDevices = nvt.getPtz();
                String token = profiles.get(0).getToken();

                //上传摄像头初始位置图片
                JSONObject jsonObject = ImageUtils.imageUpload(id, "1", token, userName, nvt);
                //更新摄像头
                //如果拍照成功，更新照片id到
                if (jsonObject.get("success").equals(true)) {
                    Map<String, String> map1 = new HashMap<>();
                    map.put("type", "2");
                    map.put("prjCd", "143");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    map.put("camCoverPic", jsonObject1.getString("docId"));
                    map.put("ipcId", id);
                    map.put("primaryId", primaryId);
                    String url = EnvConfig.Url + "/vf/service/ipc/update/" + EnvConfig.PrjCd + "/" + EnvConfig.Client;
                    String result = HttpClientUtils.doPostToServer(url, map);
                    try {
                        jsonObject = JSONObject.fromObject(result);
                        if (StringUtil.isNotEmptyOrNull(jsonObject.getString("success"))) {
                            if (StringUtil.isEqual(jsonObject.getString("success"), "true")) {
                                logger.info("【更新摄像头缩略图成功】");
                            } else {
                                logger.error("【更新摄像头缩略图失败】");
                            }
                        }
                    } catch (Exception e) {
                        logger.error("【更新摄像头缩略图异常】");
                    }
                }

                //初始化转动状态信息
                if (ptzDevices.isAbsoluteMoveSupported(token)) {//绝对移动
                    ipcOnvif.setIsAbsoluteMoveSupportedStatus("1");
                    PTZStatus ptzStatus = ptzDevices.getStatus(token);
                    IpcPosition ipcPosition = new IpcPosition(ptzStatus.getPosition().getPanTilt().getX(), ptzStatus.getPosition().getPanTilt().getY(), ptzStatus.getPosition().getZoom().getX());
                    ipcOnvif.setIpcPosition(ipcPosition);
                    ipcPosition.setxType("1");
                    //初始化范围值
                    FloatRange panRange = ptzDevices.getPanSpaces(token);
                    FloatRange tiltRange = ptzDevices.getTiltSpaces(token);
                    FloatRange zoomSpaces = ptzDevices.getZoomSpaces(token);
                    ipcPosition.setxMax(panRange.getMax());
                    ipcPosition.setxMin(panRange.getMin());

                    ipcPosition.setyMin(tiltRange.getMin());
                    ipcPosition.setyMax(tiltRange.getMax());

                    ipcPosition.setZoomMin(zoomSpaces.getMin());
                    ipcPosition.setZoomMax(zoomSpaces.getMax());

                    //初始化角度范围
                    ipcOnvif.setPanSpaces(ptzDevices.getPanSpaces(token));
                    ipcOnvif.setTiltSpaces(ptzDevices.getTiltSpaces(token));
                    ipcOnvif.setZoomSpaces(ptzDevices.getZoomSpaces(token));

                    //初始化预设列表
                    ipcOnvif.setPtzPresets(ptzDevices.getPresets(token));
                    if (ptzDevices.getPresets(token).size() > 0) {
                        if (StringUtil.isNotEmptyOrNull(camRestPos)) {//如果当前初始位置不为空
                            List<String> result = Arrays.asList(camRestPos.split(","));
                            Vector2D vector2D = new Vector2D();
                            vector2D.setX(Float.parseFloat(result.get(0)));
                            vector2D.setY(Float.parseFloat(result.get(1)));
                            ptzDevices.getPresets(token).get(0).getPTZPosition().setPanTilt(vector2D);
                            ipcOnvif.setPtzPreset(ptzDevices.getPresets(token).get(0));//获取初始化预设
                        } else {
                            ipcOnvif.setPtzPreset(ptzDevices.getPresets(token).get(0));//获取初始化预设
                        }
                    }
                } else {
                    ipcOnvif.setIsAbsoluteMoveSupportedStatus("0");
                }

                if (ptzDevices.isPtzOperationsSupported(token)) {//PTZ操作
                    ipcOnvif.setIsPtzOperationsSupportedStatus("1");
                } else {

                    ipcOnvif.setIsPtzOperationsSupportedStatus("0");
                }

                if (ptzDevices.isRelativeMoveSupported(token)) {//相对移动
                    ipcOnvif.setIsRelativeMoveSupportedStatus("1");
                } else {
                    ipcOnvif.setIsRelativeMoveSupportedStatus("0");
                }

                if (ptzDevices.isContinuosMoveSupported(token)) {//相对移动
                    ipcOnvif.setIsContinuosMoveSupportedStatus("1");
                } else {
                    ipcOnvif.setIsContinuosMoveSupportedStatus("0");
                }
                //推流服务开始
                pushIpc(commandModel, id);
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return ipcOnvif;
    }

    /**
     * 初始化精度信息
     */
    public static Precision initPrecision() {
        XmlBean flowXml = FileUtils.createXmlByFile("classpath:precision.xml");
        float x = Float.valueOf(flowXml.getStrValue("Precision.Ptz.x"));
        float y = Float.valueOf(flowXml.getStrValue("Precision.Ptz.y"));
        float zoom = Float.valueOf(flowXml.getStrValue("Precision.Zoom"));

        Precision precision = new Precision(x, y, zoom);
        return precision;
    }

    /**
     * @Description: //TODO
     * @Param: [input, outhls, outlive] 推流地址 输出地址hls 输出地址live
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/4 12:13
     **/
    public static void pushIpc(String command, String appName) {
        FFmpegManager manager = new FFmpegManagerImpl();

        // 执行任务，id就是appName，如果执行失败返回为null
//        command = "D:/ffmpeg-20180926-476a771-win64-static/ffmpeg-20180926-476a771-win64-static/bin/" +command;
        String id = manager.start(appName, command, true);
        logger.info("【执行任务 " + id + " 】");
}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }
}