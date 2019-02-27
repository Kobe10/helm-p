package com.bjshfb.vf.client.expos.func;

import com.bjshfb.vf.client.expos.data.IpcOnvif;
import com.bjshfb.vf.client.expos.data.IpcPosition;
import com.bjshfb.vf.client.expos.data.Precision;
import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.bjshfb.vf.client.ipc.onvif.soap.devices.PtzDevices;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.PTZSpeed;
import com.bjshfb.vf.client.util.CacheUtils;
import com.shfb.oframe.core.util.common.StringUtil;

import javax.xml.soap.SOAPException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: helm
 * @description: 控制IPC移动 放大缩小 连续转动与停止
 * @author: fuzq
 * @create: 2018-08-29 19:02
 **/
public class PtzIpc {
    private static Lock lock = new ReentrantLock();

    /**
     * @Description: TODO PTZ IPC
     * @Param: [ipcId, command, eventType]
     * @return: boolean
     * @Author: fuzq
     * @Date: 2018/9/27 15:53
     **/
    public static boolean controlIpc(String ipcId, String command, String eventType) throws SOAPException {
        //转动事件
        if (!StringUtil.isEqual(eventType, "release")) {
            //获取当前位置
            IpcPosition ipcPosition = getCurrentPosi(ipcId);
            //获取精度
            Precision precision = CacheUtils.getPreCache();
            //移动IPC
            ipcPosition = calcuPre(precision, ipcPosition, command, ipcId);

            boolean flag = moveIpc(ipcPosition, ipcId, ipcPosition.getxType(), eventType);
            if (flag) {
                return true;
            }
            return false;
        } else {
            //停止连续事件
            boolean flag = stopMoveIpc(ipcId, eventType);
            if (flag) {
                return true;
            }
            return false;
        }
    }

    /**
     * @Description: TODO 停止连续转动
     * @Param: [ipcId, eventType]
     * @return: boolean
     * @Author: fuzq
     * @Date: 2018/9/27 15:51
     **/
    private static boolean stopMoveIpc(String ipcId, String eventType) {
        IpcOnvif ipcOnvif = CacheUtils.getIpcCache(ipcId);
        String profileToken = null;
        if (ipcOnvif != null) {
            profileToken = ipcOnvif.getProfile().getToken();
        } else {
            return false;
        }
        OnvifDevice onvifDevice = ipcOnvif.getOnvifDevice();
        PtzDevices ptzDevices = onvifDevice.getPtz();
        if (StringUtil.isEqual(eventType, "release")) {
            if (ptzDevices.stopMove(profileToken)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @Description: TODO 转动摄像头
     * @Param: [ipcPosition, ipcId, type, eventType]
     * @return: boolean
     * @Author: fuzq
     * @Date: 2018/9/27 15:54
     **/
    private static boolean moveIpc(IpcPosition ipcPosition, String ipcId, String type, String eventType) throws SOAPException {

        IpcOnvif ipcOnvif = CacheUtils.getIpcCache(ipcId);
        String profileToken = null;
        if (ipcOnvif != null) {
            profileToken = ipcOnvif.getProfile().getToken();
        }
        OnvifDevice onvifDevice = ipcOnvif.getOnvifDevice();
        PtzDevices ptzDevices = onvifDevice.getPtz();
        //控制摄像头转动逻辑
        float x = ipcPosition.getX();
        float y = ipcPosition.getY();
        float zoom = ipcPosition.getZoom();

        //单击事件
        if (StringUtil.isEqual("click", eventType)) {
            if (StringUtil.isEqual(type, "1")) {
                if (ptzDevices.relativeMove(profileToken, x, y, zoom)) {
                    //更新位置缓存
                    CacheUtils.setCurrentPosition(ipcPosition, ipcId);
                    return true;
                } else {
                    return false;
                }
                //解锁
            } else if (StringUtil.isEqual(type, "2")) {
                if (ptzDevices.absoluteMove(profileToken, x, y, zoom)) {
                    //更新位置缓存
                    CacheUtils.setCurrentPosition(ipcPosition, ipcId);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (StringUtil.isEqual("press", eventType)) {
            //长按事件
            if (ptzDevices.continuousMove(profileToken, x, y, zoom)) {
                return true;
            } else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    /**
     * 获取当前位置
     */
    private static IpcPosition getCurrentPosi(String ipcId) {
        IpcPosition ipcPosition = CacheUtils.getCurrentPosition(ipcId);
        return ipcPosition;
    }

    /**
     * 处理指令  左移- 右移+ 上移+ 下移-  放大+ 缩小-
     *
     * @param precision
     */
    private static IpcPosition calcuPre(Precision precision, IpcPosition ipcPosition, String command, String ipcId) {
        float xPre = precision.getxPre();
        float yPre = precision.getyPre();
        float zoomPre = precision.getZoomPre();
        String type = "";

        float xx = (float) 0.0;
        float yy = (float) 0.0;
        float zz = (float) 0.0;
        if (StringUtil.isEqual("left", command)) {//左  位移变化
            xx = -xPre;
        }
        if (StringUtil.isEqual("right", command)) {//右
            xx = xPre;
        }
        if (StringUtil.isEqual("leftup", command)) {//左上
            xx = -xPre;
            yy = yPre;
        }
        if (StringUtil.isEqual("rightup", command)) {//右上
            xx = xPre;
            yy = yPre;
        }
        if (StringUtil.isEqual("up", command)) {//上
            yy = yPre;
        }
        if (StringUtil.isEqual("leftdown", command)) {//左下
            xx = -xPre;
            yy = -yPre;
        }
        if (StringUtil.isEqual("rightdown", command)) {//右下
            xx = xPre;
            yy = -yPre;
        }
        if (StringUtil.isEqual("down", command)) {//下
            yy = -yPre;
        }
        if (StringUtil.isEqual("zoomout", command)) {//放大
            zz = zoomPre;
        }
        if (StringUtil.isEqual("zoomin", command)) {//缩小
            zz = -zoomPre;
        }
        if (StringUtil.isEqual("reset", command)) {//重置
            IpcOnvif ipcOnvif = CacheUtils.getIpcCache(ipcId);
            String profileToken = null;
            if (ipcOnvif != null) {
                profileToken = ipcOnvif.getProfile().getToken();
            }
            OnvifDevice onvifDevice = ipcOnvif.getOnvifDevice();
            PtzDevices ptzDevices = onvifDevice.getPtz();
            float xxx = ipcOnvif.getPtzPreset().getPTZPosition().getPanTilt().getX();
            float yyy = ipcOnvif.getPtzPreset().getPTZPosition().getPanTilt().getY();
            float zzz = ipcOnvif.getPtzPreset().getPTZPosition().getZoom().getX();
            xx = xx;
            yy = yy;
            zz = zz;
            type = "2";
        } else {
            type = "1";
        }
        ipcPosition.setX(xx);
        ipcPosition.setY(yy);
        ipcPosition.setZoom(zz);
        ipcPosition.setxType(type);
        return ipcPosition;
    }
}