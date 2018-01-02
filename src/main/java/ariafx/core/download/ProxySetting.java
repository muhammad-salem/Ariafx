package ariafx.core.download;


public class ProxySetting {
	
//	private static ProxySetting proxySetting;
	
	public ProxySetting() {
		proxyConfig = ProxyConfig.SystemProxy;
		proxyType = ProxyType.HTTP;
		RemoteAddress = "127.0.0.1";
		RemotePort = 8080;
	}

	public enum ProxyConfig {
		NoProxy,
		SystemProxy,
		ManualProxy,
		AutoConfigProxy;
	}
	
	public enum ProxyType {
		HTTP,
		HTTPS,
		SOCKS;
	}
	
	public ProxyConfig proxyConfig = ProxyConfig.NoProxy;
	public ProxyType proxyType = ProxyType.HTTP;
	
	public String RemoteAddress;
	public int RemotePort;
//	public String HTTPRemoteAddress;
//	public int HTTPRemotePort;
//	public String HTTPSRemoteAddress;
//	public int HTTPSRemotePort;
//	public String SOCKETRemoteAddress;
//	public int SOCKETRemotePort;
	public String urlAutoConfig = "";
	
	public static ProxySetting createDefalutProxySetting() {
		return new ProxySetting();
	}
//	public static ProxySetting getSetting() {
//		return proxySetting;
//	}
//	public static void setSetting(ProxySetting setting) {
//		ProxySetting.proxySetting = setting;
//	}
	public ProxyConfig getProxyConfig() {
		return proxyConfig;
	}
	public void setProxyConfig(ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
	}
	public ProxyType getProxyType() {
		return proxyType;
	}
	public void setProxyType(ProxyType proxyType) {
		this.proxyType = proxyType;
	}
	public String getRemoteAddress() {
		return RemoteAddress;
	}
	public void setRemoteAddress(String remoteAddress) {
		RemoteAddress = remoteAddress;
	}
	public int getRemotePort() {
		return RemotePort;
	}
	public void setRemotePort(int remotePort) {
		RemotePort = remotePort;
	}
//	public String getHTTPSRemoteAddress() {
//		return HTTPSRemoteAddress;
//	}
//	public void setHTTPSRemoteAddress(String hTTPSRemoteAddress) {
//		HTTPSRemoteAddress = hTTPSRemoteAddress;
//	}
//	public int getHTTPSRemotePort() {
//		return HTTPSRemotePort;
//	}
//	public void setHTTPSRemotePort(int hTTPSRemotePort) {
//		HTTPSRemotePort = hTTPSRemotePort;
//	}
//	public String getSOCKETRemoteAddress() {
//		return SOCKETRemoteAddress;
//	}
//	public void setSOCKETRemoteAddress(String sOCKETRemoteAddress) {
//		SOCKETRemoteAddress = sOCKETRemoteAddress;
//	}
//	public int getSOCKETRemotePort() {
//		return SOCKETRemotePort;
//	}
//	public void setSOCKETRemotePort(int sOCKETRemotePort) {
//		SOCKETRemotePort = sOCKETRemotePort;
//	}
	public String getUrlAutoConfig() {
		return urlAutoConfig;
	}
	public void setUrlAutoConfig(String urlAutoConfig) {
		this.urlAutoConfig = urlAutoConfig;
	}
	
	
//	public static void USE_No_Proxy() {
//		proxySetting.proxyConfig = ProxyConfig.NoProxy;
//	}
//	
//	public static void USE_System_Proxy() {
//		proxySetting.proxyConfig = ProxyConfig.SystemProxy;
//	}
//	
//	
//	public static void Use_Manual_Proxy_Settings(String remoteIP, String port) {
//		proxySetting.setProxyConfig(ProxyConfig.ManualProxy);
//		proxySetting.proxyType = ProxyType.HTTP;
//		proxySetting.RemoteAddress = remoteIP;
//		proxySetting.RemotePort = Integer.valueOf(port);
//		Setting.setProxySetting(proxySetting);
//	}
//	
//	public static void Use_Manual_Proxy_Settings(String remoteIP, String port, ProxyType proxyType) {
//		proxySetting.setProxyConfig(ProxyConfig.ManualProxy);
//		proxySetting.proxyType = proxyType;
//		proxySetting.RemoteAddress = remoteIP;
//		proxySetting.RemotePort = Integer.valueOf(port);
//	}
//	
//	public static void Use_AutoConfiguration_Proxy(String url) {
//		proxySetting.proxyConfig = ProxyConfig.AutoConfigProxy;
//		proxySetting.urlAutoConfig = url;
//	}

}
