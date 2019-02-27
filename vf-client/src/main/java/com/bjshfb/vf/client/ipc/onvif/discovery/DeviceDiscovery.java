package com.bjshfb.vf.client.ipc.onvif.discovery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeviceDiscovery implements Serializable {
  public static int WS_DISCOVERY_TIMEOUT = 4000;
  public static int WS_DISCOVERY_PORT = 3702;
  public static String WS_DISCOVERY_ADDRESS_IPv4 = "239.255.255.250";
  public static String WS_DISCOVERY_ADDRESS_IPv6 = "[FF02::C]";
  public static String WS_DISCOVERY_PROBE_MESSAGE = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:tns=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"><soap:Header><wsa:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action><wsa:MessageID>urn:uuid:c032cfdd-c3ca-49dc-820e-ee6696ad63e2</wsa:MessageID><wsa:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To></soap:Header><soap:Body><tns:Probe/></soap:Body></soap:Envelope>";
  private static final Random random = new SecureRandom();

  public DeviceDiscovery() {
  }

  public static void main(String[] args) throws InterruptedException {
    Iterator i$ = discoverWsDevicesAsUrls().iterator();

    while(i$.hasNext()) {
      URL url = (URL)i$.next();
      System.out.println("Device discovered: " + url.toString());
    }

  }

  public static Collection<URL> discoverWsDevicesAsUrls() {
    return discoverWsDevicesAsUrls("", "");
  }

  public static Collection<URL> discoverWsDevicesAsUrls(String regexpProtocol, String regexpPath) {
    Collection<URL> urls = new TreeSet(new Comparator<URL>() {
      public int compare(URL o1, URL o2) {
        return o1.toString().compareTo(o2.toString());
      }
    });
    Iterator i$ = discoverWsDevices().iterator();

    while(i$.hasNext()) {
      String key = (String)i$.next();

      try {
        URL url = new URL(key);
        boolean ok = true;
        if (regexpProtocol.length() > 0 && !url.getProtocol().matches(regexpProtocol)) {
          ok = false;
        }

        if (regexpPath.length() > 0 && !url.getPath().matches(regexpPath)) {
          ok = false;
        }

        if (ok) {
          urls.add(url);
        }
      } catch (MalformedURLException var7) {
        var7.printStackTrace();
      }
    }

    return urls;
  }

  public static Collection<String> discoverWsDevices() {
    final Collection<String> addresses = new TreeSet();
    final CountDownLatch serverStarted = new CountDownLatch(1);
    final CountDownLatch serverFinished = new CountDownLatch(1);

    try {
      String uuid = UUID.randomUUID().toString();
      WS_DISCOVERY_PROBE_MESSAGE = WS_DISCOVERY_PROBE_MESSAGE.replaceAll("<wsa:MessageID>urn:uuid:.*</wsa:MessageID>", "<wsa:MessageID>urn:uuid:" + uuid + "</wsa:MessageID>");
      int port = random.nextInt(20000) + '鱀';
      final DatagramSocket server = new DatagramSocket(port);
      (new Thread() {
        public void run() {
          try {
            DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
            server.setSoTimeout(DeviceDiscovery.WS_DISCOVERY_TIMEOUT);
            long timerStarted = System.currentTimeMillis();

            while(System.currentTimeMillis() - timerStarted < (long)DeviceDiscovery.WS_DISCOVERY_TIMEOUT) {
              serverStarted.countDown();
              server.receive(packet);
              Collection<String> collection = DeviceDiscovery.parseSoapResponseForUrls(Arrays.copyOf(packet.getData(), packet.getLength()));
              Iterator i$ = collection.iterator();

              while(i$.hasNext()) {
                String key = (String)i$.next();
                addresses.add(key);
              }
            }
          } catch (SocketTimeoutException var11) {
            ;
          } catch (Exception var12) {
            var12.printStackTrace();
          } finally {
            serverFinished.countDown();
            server.close();
          }

        }
      }).start();

      try {
        serverStarted.await(1000L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var8) {
        var8.printStackTrace();
      }

      server.send(new DatagramPacket(WS_DISCOVERY_PROBE_MESSAGE.getBytes(), WS_DISCOVERY_PROBE_MESSAGE.length(), InetAddress.getByName(WS_DISCOVERY_ADDRESS_IPv4), WS_DISCOVERY_PORT));
    } catch (Exception var9) {
      var9.printStackTrace();
    }

    try {
      serverFinished.await((long)WS_DISCOVERY_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException var7) {
      var7.printStackTrace();
    }

    return addresses;
  }

  private static Collection<Node> getNodeMatching(Node body, String regexp) {
    Collection<Node> nodes = new ArrayList();
    if (body.getNodeName().matches(regexp)) {
      nodes.add(body);
    }

    if (body.getChildNodes().getLength() == 0) {
      return nodes;
    } else {
      NodeList returnList = body.getChildNodes();

      for(int k = 0; k < returnList.getLength(); ++k) {
        Node node = returnList.item(k);
        nodes.addAll(getNodeMatching(node, regexp));
      }

      return nodes;
    }
  }

  private static Collection<String> parseSoapResponseForUrls(byte[] data) throws SOAPException, IOException {
    Collection<String> urls = new ArrayList();
    MessageFactory factory = MessageFactory.newInstance("SOAP 1.2 Protocol");
    MimeHeaders headers = new MimeHeaders();
    headers.addHeader("Content-type", "application/soap+xml");
    SOAPMessage message = factory.createMessage(headers, new ByteArrayInputStream(data));
    SOAPBody body = message.getSOAPBody();
    Iterator i$ = getNodeMatching(body, ".*:XAddrs").iterator();

    while(i$.hasNext()) {
      Node node = (Node)i$.next();
      if (node.getTextContent().length() > 0) {
        urls.addAll(Arrays.asList(node.getTextContent().split(" ")));
      }
    }

    return urls;
  }
}
