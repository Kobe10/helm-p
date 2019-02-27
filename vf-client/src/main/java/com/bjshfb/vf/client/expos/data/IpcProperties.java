package com.bjshfb.vf.client.expos.data;

/**
 * @program: helm
 * @description: 初始化配置文件
 * @author: fuzq
 * @create: 2018-09-04 17:08
 **/
public class IpcProperties {
    private String id;
    private String ip;
    private String user;
    private String psw;
    private String viewUsers;
    private String controlUsers;
    private String primaryId;
    private String cptPicTime;

    public String getCptPicTime() {
        return cptPicTime;
    }

    public void setCptPicTime(String cptPicTime) {
        this.cptPicTime = cptPicTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getViewUsers() {
        return viewUsers;
    }

    public void setViewUsers(String viewUsers) {
        this.viewUsers = viewUsers;
    }

    public String getControlUsers() {
        return controlUsers;
    }

    public void setControlUsers(String controlUsers) {
        this.controlUsers = controlUsers;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    @Override
    public boolean equals(Object obj) {
        //如果引用指向的为同一个对象，则返回true
        if (this == obj) {
            return true;
        }
        //当引用指向的为不同的对象的时候，判断对象属性是否相等
        if (obj != null && obj.getClass() == IpcProperties.class) {
            IpcProperties ipcProperties = (IpcProperties) obj;
            if (ipcProperties.getId().equals(this.getId()) && ipcProperties.getIp().equals(this.getIp())
                    && ipcProperties.getPsw().equals(this.getPsw())
                    && ipcProperties.getControlUsers().equals(this.getControlUsers())
                    && ipcProperties.getUser().equals(this.getUser())
                    && ipcProperties.getViewUsers().equals(this.getViewUsers())) {
                return true;
            }
        }
        return false;
    }
}
