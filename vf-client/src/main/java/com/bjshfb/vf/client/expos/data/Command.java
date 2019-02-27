package com.bjshfb.vf.client.expos.data;

/**
 * @program: helm
 * @description: 摄像头命令
 * @author: fuzq
 * @create: 2018-09-06 11:51
 **/
public class Command {
    private String command;

    private String ipcId;

    private String primaryId;


    @Override
    public String toString() {
        return "Command{" +
                "command='" + command + '\'' +
                ", ipcId='" + ipcId + '\'' +
                '}';
    }

    public Command(String command, String ipcId, String primaryId) {
        this.command = command;
        this.ipcId = ipcId;
        this.primaryId = primaryId;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getIpcId() {
        return ipcId;
    }

    public void setIpcId(String ipcId) {
        this.ipcId = ipcId;
    }
}
