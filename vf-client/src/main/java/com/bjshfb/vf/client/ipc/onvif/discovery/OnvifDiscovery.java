package com.bjshfb.vf.client.ipc.onvif.discovery;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * @author th
 * @date 2015-06-18
 */
public class OnvifDiscovery implements Serializable {
	public static List<OnvifPointer> discoverOnvifDevices() {
		final ArrayList<OnvifPointer> onvifPointers = new ArrayList<>();
		final Collection<URL> urls = DeviceDiscovery.discoverWsDevicesAsUrls("^http$", ".*onvif.*");
		for (URL url : urls) {
			try {
				onvifPointers.add(new OnvifPointer(url));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return onvifPointers;
	}
}
