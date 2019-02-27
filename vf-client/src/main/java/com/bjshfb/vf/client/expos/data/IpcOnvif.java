package com.bjshfb.vf.client.expos.data;

import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.FloatRange;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.PTZPreset;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.Profile;

import java.io.Serializable;
import java.util.List;

/**
 * @program: helm
 * @description: Ipc对象的基本信息
 * @author: fuzq
 * @create: 2018-08-29 16:02
 **/
public class IpcOnvif implements Serializable {
    private String primaryId;//摄像头主键

    private String ipcId;//摄像头Id

    private String ipcUName;//用户名

    private String ipcPwd;//密码

    private String basePwd;//加密后的密码

    private String ipcIp;//摄像头Ip

    private String ipcName;//摄像头Ip

    private IpcPosition ipcPosition;

    private List<String> viewUsers;//可以查看的用户

    private List<String> controlUsers;//可以控制的用户

    private OnvifDevice onvifDevice;

    private List<Profile> profiles;//摄像头基本配置信息列表

    private Profile profile;//摄像头基本配置信息列表

    private FloatRange panSpaces;//平移角度范围（x）

    private FloatRange tiltSpaces;//倾斜角度范围（y）

    private FloatRange zoomSpaces;//缩放范围

    private List<PTZPreset> ptzPresets;//所有预设的列表

    private PTZPreset ptzPreset;//当前相机预设

    private String outAddressHls;//推流的hls输出地址

    private String outAddressLive;//推流的live输出地址

    private String isOnline;//当前IPC是否在线

    private String cptPicTime;//定时截图时间，逗号分隔

    public String getCptPicTime() {
        return cptPicTime;
    }

    public void setCptPicTime(String cptPicTime) {
        this.cptPicTime = cptPicTime;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public String getOutAddressHls() {
        return outAddressHls;
    }

    public void setOutAddressHls(String outAddressHls) {
        this.outAddressHls = outAddressHls;
    }

    public String getOutAddressLive() {
        return outAddressLive;
    }

    public void setOutAddressLive(String outAddressLive) {
        this.outAddressLive = outAddressLive;
    }

    private String isPtzOperationsSupportedStatus;//是否支持任何PTZ简单操作 0不支持 1支持

    private String isAbsoluteMoveSupportedStatus;//是否支持绝对移动 0不支持 1支持

    private String isRelativeMoveSupportedStatus;//是否支持相对移动 0不支持 1支持

    private String isContinuosMoveSupportedStatus;//是否支持连续运动 0不支持 1支持

    public String getBasePwd() {
        return basePwd;
    }

    public void setBasePwd(String basePwd) {
        this.basePwd = basePwd;
    }

    public IpcPosition getIpcPosition() {
        return ipcPosition;
    }

    public void setIpcPosition(IpcPosition ipcPosition) {
        this.ipcPosition = ipcPosition;
    }

    public OnvifDevice getOnvifDevice() {
        return onvifDevice;
    }

    public void setOnvifDevice(OnvifDevice onvifDevice) {
        this.onvifDevice = onvifDevice;
    }

    public String getIpcId() {
        return ipcId;
    }

    public void setIpcId(String ipcId) {
        this.ipcId = ipcId;
    }

    public String getIpcIp() {
        return ipcIp;
    }

    public void setIpcIp(String ipcIp) {
        this.ipcIp = ipcIp;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getIpcName() {
        return ipcName;
    }

    public void setIpcName(String ipcName) {
        this.ipcName = ipcName;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public FloatRange getPanSpaces() {
        return panSpaces;
    }

    public void setPanSpaces(FloatRange panSpaces) {
        this.panSpaces = panSpaces;
    }

    public FloatRange getTiltSpaces() {
        return tiltSpaces;
    }

    public void setTiltSpaces(FloatRange tiltSpaces) {
        this.tiltSpaces = tiltSpaces;
    }

    public FloatRange getZoomSpaces() {
        return zoomSpaces;
    }

    public void setZoomSpaces(FloatRange zoomSpaces) {
        this.zoomSpaces = zoomSpaces;
    }

    public List<PTZPreset> getPtzPresets() {
        return ptzPresets;
    }

    public void setPtzPresets(List<PTZPreset> ptzPresets) {
        this.ptzPresets = ptzPresets;
    }

    public PTZPreset getPtzPreset() {
        return ptzPreset;
    }

    public void setPtzPreset(PTZPreset ptzPreset) {
        this.ptzPreset = ptzPreset;
    }

    public String getIsPtzOperationsSupportedStatus() {
        return isPtzOperationsSupportedStatus;
    }

    public void setIsPtzOperationsSupportedStatus(String isPtzOperationsSupportedStatus) {
        this.isPtzOperationsSupportedStatus = isPtzOperationsSupportedStatus;
    }

    public String getIsAbsoluteMoveSupportedStatus() {
        return isAbsoluteMoveSupportedStatus;
    }

    public void setIsAbsoluteMoveSupportedStatus(String isAbsoluteMoveSupportedStatus) {
        this.isAbsoluteMoveSupportedStatus = isAbsoluteMoveSupportedStatus;
    }

    public String getIsRelativeMoveSupportedStatus() {
        return isRelativeMoveSupportedStatus;
    }

    public void setIsRelativeMoveSupportedStatus(String isRelativeMoveSupportedStatus) {
        this.isRelativeMoveSupportedStatus = isRelativeMoveSupportedStatus;
    }

    public String getIsContinuosMoveSupportedStatus() {
        return isContinuosMoveSupportedStatus;
    }

    public void setIsContinuosMoveSupportedStatus(String isContinuosMoveSupportedStatus) {
        this.isContinuosMoveSupportedStatus = isContinuosMoveSupportedStatus;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public String getIpcUName() {
        return ipcUName;
    }

    public void setIpcUName(String ipcUName) {
        this.ipcUName = ipcUName;
    }

    public String getIpcPwd() {
        return ipcPwd;
    }

    public void setIpcPwd(String ipcPwd) {
        this.ipcPwd = ipcPwd;
    }

    public List<String> getViewUsers() {
        return viewUsers;
    }

    public void setViewUsers(List<String> viewUsers) {
        this.viewUsers = viewUsers;
    }

    public List<String> getControlUsers() {
        return controlUsers;
    }

    public void setControlUsers(List<String> controlUsers) {
        this.controlUsers = controlUsers;
    }
}
