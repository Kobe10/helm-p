package com.bjshfb.vf.client.ipc.onvif.soap.devices;

import com.bjshfb.vf.client.ipc.onvif.soap.OnvifDevice;
import com.bjshfb.vf.client.ipc.onvif.soap.SOAP;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.media.wsdl.*;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.*;

import javax.xml.soap.SOAPException;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.List;

public class MediaDevices implements Serializable{
	private OnvifDevice onvifDevice;
	private SOAP soap;

	public MediaDevices(OnvifDevice onvifDevice) {
		this.onvifDevice = onvifDevice;
		this.soap = onvifDevice.getSoap();
	}

	@Deprecated
	public String getHTTPStreamUri(int profileNumber) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.HTTP);
		setup.setTransport(transport);
		return getStreamUri(setup, profileNumber);
	}

	public String getHTTPStreamUri(String profileToken) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.HTTP);
		setup.setTransport(transport);
		return getStreamUri(profileToken, setup);
	}

	@Deprecated
	public String getUDPStreamUri(int profileNumber) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.UDP);
		setup.setTransport(transport);
		return getStreamUri(setup, profileNumber);
	}

	public String getUDPStreamUri(String profileToken) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.UDP);
		setup.setTransport(transport);
		return getStreamUri(profileToken, setup);
	}

	@Deprecated
	public String getTCPStreamUri(int profileNumber) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.TCP);
		setup.setTransport(transport);
		return getStreamUri(setup, profileNumber);
	}

	public String getTCPStreamUri(String profileToken) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.TCP);
		setup.setTransport(transport);
		return getStreamUri(profileToken, setup);
	}

	@Deprecated
	public String getRTSPStreamUri(int profileNumber) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.TCP);
		setup.setTransport(transport);
		return getStreamUri(setup, profileNumber);
	}
	
	public String getRTSPStreamUri(String profileToken) throws ConnectException, SOAPException {
		StreamSetup setup = new StreamSetup();
		setup.setStream(StreamType.RTP_UNICAST);
		Transport transport = new Transport();
		transport.setProtocol(TransportProtocol.TCP);
		setup.setTransport(transport);
		return getStreamUri(profileToken, setup);
	}
	
	@Deprecated
	public String getStreamUri(StreamSetup streamSetup, int profileNumber) throws ConnectException, SOAPException {
		Profile profile = onvifDevice.getDevices().getProfiles().get(profileNumber);
		return getStreamUri(profile, streamSetup);
	}

	@Deprecated
	public String getStreamUri(Profile profile, StreamSetup streamSetup) throws ConnectException, SOAPException {
		return getStreamUri(profile.getToken(), streamSetup);
	}

	public String getStreamUri(String profileToken, StreamSetup streamSetup) throws SOAPException, ConnectException {
		GetStreamUri request = new GetStreamUri();
		GetStreamUriResponse response = new GetStreamUriResponse();

		request.setProfileToken(profileToken);
		request.setStreamSetup(streamSetup);

		try {
			response = (GetStreamUriResponse) soap.createSOAPMediaRequest(request, response, false);
		}
		catch (SOAPException | ConnectException e) {
			throw e;
		}

		if (response == null) {
			return null;
		}

		return onvifDevice.replaceLocalIpWithProxyIp(response.getMediaUri().getUri());
	}

	public static VideoEncoderConfiguration getVideoEncoderConfiguration(Profile profile) {
		return profile.getVideoEncoderConfiguration();
	}

	public VideoEncoderConfigurationOptions getVideoEncoderConfigurationOptions(String profileToken) throws SOAPException, ConnectException {
		GetVideoEncoderConfigurationOptions request = new GetVideoEncoderConfigurationOptions();
		GetVideoEncoderConfigurationOptionsResponse response = new GetVideoEncoderConfigurationOptionsResponse();

		request.setProfileToken(profileToken);

		try {
			response = (GetVideoEncoderConfigurationOptionsResponse) soap.createSOAPMediaRequest(request, response, false);
		}
		catch (SOAPException | ConnectException e) {
			throw e;
		}

		if (response == null) {
			return null;
		}

		return response.getOptions();
	}

	public boolean setVideoEncoderConfiguration(VideoEncoderConfiguration videoEncoderConfiguration) throws SOAPException, ConnectException {
		SetVideoEncoderConfiguration request = new SetVideoEncoderConfiguration();
		SetVideoEncoderConfigurationResponse response = new SetVideoEncoderConfigurationResponse();

		request.setConfiguration(videoEncoderConfiguration);
		request.setForcePersistence(true);

		try {
			response = (SetVideoEncoderConfigurationResponse) soap.createSOAPMediaRequest(request, response, true);
		}
		catch (SOAPException | ConnectException e) {
			throw e;
		}

		if (response == null) {
			return false;
		}

		return true;
	}

	public String getSceenshotUri(String profileToken) throws SOAPException, ConnectException {
		return getSnapshotUri(profileToken);
	}

	public String getSnapshotUri(String profileToken) throws SOAPException, ConnectException {
		GetSnapshotUri request = new GetSnapshotUri();
		GetSnapshotUriResponse response = new GetSnapshotUriResponse();

		request.setProfileToken(profileToken);

		try {
			response = (GetSnapshotUriResponse) soap.createSOAPMediaRequest(request, response, true);
		}
		catch (SOAPException | ConnectException e) {
			throw e;
		}
		
		if (response == null || response.getMediaUri() == null) {
			return null;
		}
		
		return onvifDevice.replaceLocalIpWithProxyIp(response.getMediaUri().getUri());
	}

	public List<VideoSource> getVideoSources() throws SOAPException, ConnectException {
		GetVideoSources request = new GetVideoSources();
		GetVideoSourcesResponse response = new GetVideoSourcesResponse();

		try {
			response = (GetVideoSourcesResponse) soap.createSOAPMediaRequest(request, response, false);
		}
		catch (SOAPException | ConnectException e) {
			throw e;
		}

		if (response == null) {
			return null;
		}

		return response.getVideoSources();
	}
}
