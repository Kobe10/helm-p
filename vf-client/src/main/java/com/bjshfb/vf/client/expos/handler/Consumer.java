//package com.bjshfb.vf.client.expos.handler;
//
//import com.bjshfb.vf.client.expos.data.Command;
//import com.bjshfb.vf.client.expos.func.PtzIpc;
//import org.apache.log4j.Logger;
//
//import javax.xml.soap.SOAPException;
//import java.util.concurrent.BlockingQueue;
//
///**
// * @program: helm
// * @description: 消费
// * @author: fuzq
// * @create: 2018-08-30 20:06
// **/
//public class Consumer implements Runnable {
//    public static final Logger logger = Logger.getLogger(Consumer.class);
//
//    private BlockingQueue<Command> queue;
//
//    public Consumer(BlockingQueue<Command> queue) {
//        this.queue = queue;
//    }
//
//    @Override
//    public void run() {
//        if (queue.size() > 0) {
//            Command command = null;
//            try {
//                command = queue.take();
//                PtzIpc.controlIpc(command.getIpcId(), command.getCommand(), eventType);
//            } catch (InterruptedException e) {
//                logger.error("【移动摄像头异常】", e);
//            } catch (SOAPException e) {
//                logger.error("【移动摄像头异常】", e);
//            }
//        }
//    }
//}