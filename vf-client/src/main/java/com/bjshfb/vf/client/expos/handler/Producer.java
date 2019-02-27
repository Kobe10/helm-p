//package com.bjshfb.vf.client.expos.handler;
//
//import com.bjshfb.vf.client.expos.data.Command;
//import org.apache.log4j.Logger;
//
//import java.util.concurrent.BlockingQueue;
//
///**
// * @program: helm
// * @description: 命令生产者
// * @author: fuzq
// * @create: 2018-08-30 18:15
// **/
//public class Producer implements Runnable {
//    public static final Logger logger = Logger.getLogger(Producer.class);
//
//    private volatile boolean isProducing = true;
//
//    private BlockingQueue<Command> queue;
//
//    private String ipcId;
//
//    private String command;
//
//    public String getIpcId() {
//        return ipcId;
//    }
//
//    public void setIpcId(String ipcId) {
//        this.ipcId = ipcId;
//    }
//
//    public String getCommand() {
//        return command;
//    }
//
//    public void setCommand(String command) {
//        this.command = command;
//    }
//
//    public Producer(BlockingQueue<Command> queue, String ipcId, String command) {
//        this.queue = queue;
//        this.ipcId = ipcId;
//        this.command = command;
//    }
//
//    @Override
//    public void run() {
//        Command com = null;
//        logger.info("【生产者线程 : Id" + Thread.currentThread().getId() + " 】");
//        while (isProducing) {
//            com = new Command(command, ipcId,"");
//            if (!queue.offer(com)) {
//                logger.error("【生产者数据提交到缓冲区错误】");
//            }
//        }
//    }
//
//    public void stop() {
//        isProducing = false;
//    }
//}
