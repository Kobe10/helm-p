package com.bjshfb.vf.client.ipc;

import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.bjshfb.vf.client.ipc.onvif.soap.devices.PtzDevices;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.*;
import com.shfb.oframe.core.util.common.ServerFile;
import com.shfb.oframe.core.util.common.StringUtil;

import javax.xml.soap.SOAPException;
import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static final String INFO = "Commands:\n  \n  url: Get snapshort URL.\n  info: Get information about each valid command.\n  profiles: Get all profiles.\n  exit: Exit this application.";

    /**
     * 打印摄像机时间
     */
    private static void testPrintTime() throws SOAPException, ConnectException {
        OnvifDevice nvt = new OnvifDevice("192.168.1.198", "admin", "shfb_718");
        Date nvtDate = nvt.getDevices().getDate();
        System.out.println(nvtDate.toString());
    }

    /**
     * 获取IPC的Token
     */
    private static List<String> getProfile(OnvifDevice nvt) throws SOAPException, ConnectException {
        List<Profile> profiles = nvt.getDevices().getProfiles();
        List<String> list = new ArrayList<>();
        for (Profile profile : profiles) {
            list.add(profile.getToken());
        }
        return list;
    }

    /**
     * 查看分辨率
     */
    private static void getMedia(OnvifDevice nvt) throws ConnectException, SOAPException {
        List<String> list = getProfile(nvt);
        for (String s : list) {
            System.out.println("Snapshot URI: " + nvt.getMedia().getSnapshotUri(s));
        }
    }

    /**
     * 移动摄像头
     */
    private static void ptzIPC(OnvifDevice nvt) throws ConnectException, SOAPException {
        List<String> list = getProfile(nvt);
        PtzDevices ptzDevices = nvt.getPtz(); //获取PTZ设备
        for (String s : list) {
            if (ptzDevices.isPtzOperationsSupported(s)) {//如果支持移动
                if (ptzDevices.getNode(s) != null) {
                    int ptzNode = ptzDevices.getNode(s).getMaximumNumberOfPresets();//获取最大移动值
                    System.out.println(ptzNode);
                }
            }
            float zoom = ptzDevices.getZoomSpaces(s).getMin();
            float x = (float) (1.00 / 2f);
            float y = (float) (2.0 / 2f);
            if (ptzDevices.isAbsoluteMoveSupported(s)) {
                ptzDevices.absoluteMove(s, x, y, zoom);
            }
        }
    }

    /**
     * 捕获快照
     * */
//    public static void main(String[] args) throws SOAPException, ConnectException {
//            OnvifDevice nvt = new OnvifDevice("192.168.1.199", "admin", "shfb_718");
//            List<Profile> profiles = nvt.getDevices().getProfiles();
//            String profileToken = profiles.get(0).getToken();
//            PtzDevices ptzDevices = nvt.getPtz();
//
//            ptzDevices.getPresets(profileToken).get(0).getPTZPosition();
////            nvt.getMedia().getSnapshotUri(profileToken);
//            System.out.println("Snapshot URI: "+nvt.getMedia().getSnapshotUri(profileToken));
//    }

    /**
     * 控制上移下移操作
     */
    public static void main(String[] args) throws ConnectException, SOAPException, InterruptedException {
        try {
            OnvifDevice nvt = new OnvifDevice("192.168.1.199", "admin", "shfb_718");
            List<Profile> profiles = nvt.getDevices().getProfiles();
            PtzDevices ptzDevices = nvt.getPtz();
            String profileToken = "";
            profileToken = profiles.get(2).getToken();
            FloatRange panRange = ptzDevices.getPanSpaces(profileToken);
            FloatRange tiltRange = ptzDevices.getTiltSpaces(profileToken);
            Date date = new Date();
            System.out.println("==============系统初始化开始时间：" + date.getTime() / 1000);
            float x = (panRange.getMax() + panRange.getMin()) / 2f;
            float y = (tiltRange.getMax() + tiltRange.getMin()) / 2f;
            float zoom = ptzDevices.getZoomSpaces(profileToken).getMin();
            Date date1 = new Date();
            System.out.println("==============系统初始化结束时间：" + date1.getTime() / 1000);
            System.out.println("==============总共花销时间：" + (date1.getTime() - date.getTime()) / 1000);
            int count = 1;

//            for (float i = x; i < 0.1; i += 0.1) {
//                Thread.sleep(5000);
            Lock lock = new ReentrantLock();
//            while (true) {

                PTZStatus ptzStatus = ptzDevices.getStatus(profileToken);
                System.out.println("----------:" + ptzStatus.getPosition().getPanTilt().getX() + "------------------:" + ptzStatus.getPosition().getPanTilt().getY());

                x += 0.1;
                y += 0.1;
                Date date2 = new Date();
                System.out.println("==============第" + count + "次调用开始时间：" + date2.getTime() / 1000);
//                if (ptzDevices.isAbsoluteMoveSupported(profileToken)) {
                ptzDevices.continuousMove(profileToken, x, y, zoom);

                Thread.sleep(10000);
                ptzDevices.stopMove(profileToken);
                PTZStatus ptzStatus1 = ptzDevices.getStatus(profileToken);
                System.out.println("----------:" + ptzStatus1.getPosition().getPanTilt().getX() + "------------------:" + ptzStatus1.getPosition().getPanTilt().getY());


//                }
                Date date3 = new Date();
                System.out.println("==============第" + count + "次调用结束时间：" + date3.getTime() / 1000);

                System.out.println("==============第" + count + "次调用总共花销时间：" + (date3.getTime() - date2.getTime()) / 1000);
                count++;
                System.out.println("zoom" + ptzDevices.getStatus(profileToken).getPosition().getZoom());

//            }

//            }

        } catch (ConnectException e) {
            System.err.println("Could not connect to NVT.");
        } catch (SOAPException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发现IPC操作
     */
//	public static void main(String args[]) {
//		InputStreamReader inputStream = new InputStreamReader(System.in);
//		BufferedReader keyboardInput = new BufferedReader(inputStream);
//		String input, cameraAddress, user, password;
//
//		try {
//			System.out.println("Please enter camera IP (with port if not 80):");
//			cameraAddress = keyboardInput.readLine();
//			System.out.println("Please enter camera username:");
//			user = keyboardInput.readLine();
//			System.out.println("Please enter camera password:");
//			password = keyboardInput.readLine();
//		}
//		catch (IOException e1) {
//			e1.printStackTrace();
//			return;
//		}
//
//		System.out.println("Connect to camera, please wait ...");
//		OnvifDevice cam;
//		try {
//			cam = new OnvifDevice(cameraAddress, user, password);
//		}
//		catch (ConnectException | SOAPException e1) {
//			System.err.println("No connection to camera, please try again.");
//			return;
//		}
//		System.out.println("Connection to camera successful!");
//
//		while (true) {
//			try {
//				System.out.println();
//				System.out.println("Enter a command (type \"info\" to get commands):");
//				input = keyboardInput.readLine();
//
//				switch (input) {
//				case "url": {
//					List<Profile> profiles = cam.getDevices().getProfiles();
//					for (Profile p : profiles) {
//						try {
//							System.out.println("URL from Profile \'" + p.getName() + "\': " + cam.getMedia().getSnapshotUri(p.getToken()));
//						}
//						catch (SOAPException e) {
//							System.err.println("Cannot grap snapshot URL, got Exception "+e.getMessage());
//						}
//					}
//					break;
//				}
//				case "profiles":
//					List<Profile> profiles = cam.getDevices().getProfiles();
//					System.out.println("Number of profiles: " + profiles.size());
//					for (Profile p : profiles) {
//						System.out.println("  Profile "+p.getName()+" token is: "+p.getToken());
//					}
//					break;
//				case "info":
//					System.out.println(INFO);
//					break;
//				case "quit":
//				case "exit":
//				case "end":
//					return;
//				default:
//					System.out.println("Unknown command!");
//					System.out.println();
//					System.out.println(INFO);
//					break;
//				}
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
    public static String formatRelFilePath(String classPath) {

        String realFilePath = "";
        if (StringUtil.isNotEmptyOrNull(classPath) && classPath.startsWith("classpath")) {
            String fileClassPath = classPath.substring(10, classPath.length());

            realFilePath = "/" + fileClassPath;
        } else if (StringUtil.isNotEmptyOrNull(classPath) && classPath.startsWith("")) {
            String webRoot = "";
            if (!webRoot.endsWith(File.separator)) {
                webRoot += File.separator;
            }
            realFilePath = webRoot + classPath.substring(8, classPath.length());
        } else if (StringUtil.isNotEmptyOrNull(classPath) && classPath.startsWith("")) {
            String rootPath = ServerFile.getServiceFileFolder();
            if (!rootPath.endsWith(File.separator)) {
                rootPath += File.separator;
            }
            realFilePath = rootPath + classPath.substring(7, classPath.length());
        } else {
            realFilePath = classPath;
        }

        return realFilePath;
    }
//
//    public static void main(String[] args) {
//        String name = "classpath:cxfm.xml";
//        System.out.println(formatRelFilePath(name));
//
//    }
}