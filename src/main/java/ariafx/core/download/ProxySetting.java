package ariafx.core.download;


public class ProxySetting {

    public ProxyConfig proxyConfig = ProxyConfig.NoProxy;
    public ProxyType proxyType = ProxyType.HTTP;
    public String RemoteAddress;
    public int RemotePort;
    public String urlAutoConfig = "";

    public ProxySetting() {
        proxyConfig = ProxyConfig.SystemProxy;
        proxyType = ProxyType.HTTP;
        RemoteAddress = "127.0.0.1";
        RemotePort = 8080;
    }

    public static ProxySetting createDefalutProxySetting() {
        return new ProxySetting();
    }

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

    public String getUrlAutoConfig() {
        return urlAutoConfig;
    }

    public void setUrlAutoConfig(String urlAutoConfig) {
        this.urlAutoConfig = urlAutoConfig;
    }

    public enum ProxyConfig {
        NoProxy,
        SystemProxy,
        ManualProxy,
        AutoConfigProxy
    }

    public enum ProxyType {
        HTTP,
        HTTPS,
        SOCKS
    }

}
