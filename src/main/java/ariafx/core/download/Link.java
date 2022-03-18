package ariafx.core.download;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;


import ariafx.core.download.ProxySetting.ProxyConfig;
import ariafx.core.download.ProxySetting.ProxyType;
import ariafx.core.url.Item;
import ariafx.core.url.Url;
import ariafx.opt.R;
import ariafx.opt.Setting;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContexts;

public class Link extends Download {
	
	public Link(Url url){
		this(new Item(url));
	}
	
	public Link(String url) throws Exception{
		this(new Item(url));
	}
	
	public Link(String url, String referrer) throws Exception{
		this(new Item(url, referrer));
	}
	
	public Link(Item item) {
		super(item);
	}

	public int getChunksNum() {
		return item.getChunksNum();
	}

	public void setChunksNum(int chunksNum) {
		item.setChunksNum(chunksNum);
	}

	public String getFilename() {
		return item.getFilename();
	}

	public void setFilename(String filename) {
		item.setFilename(filename);
	}

	public String getSaveto() {
		return item.getSaveto();
	}

	public void setSaveto(String saveto) {
		item.setSaveto(saveto);
	}

	@Override
	public String getCacheFile() {
		return item.getCacheFile();
	}

	@Override
	public void setCacheFile(String cacheFile) {
		item.setCacheFile(cacheFile);
	}

	public long getLength() {
		return item.getLength();
	}

	public void setLength(long length) {
		item.setLength(length);
	}

	public long getDownloaded() {
		return item.getDownloaded();
	}

	public void setDownloaded(long downloaded) {
		item.setDownloaded(downloaded);
	}

	public long getAdded() {
		return item.getAdded();
	}

	public long getLastTry() {
		return item.getLastTry();
	}

	public void setLastTry(long lastTry) {
		item.setLastTry(lastTry);
	}

	public long getTimeLeft() {
		return item.getTimeLeft();
	}

	public void setTimeLeft(long timeLeft) {
		item.setTimeLeft(timeLeft);
	}

	public String getQueue() {
		return item.getQueue();
	}

	public void setQueue(String queue) {
		item.setQueue(queue);
	}

	public String getCategory() {
		return item.getCategory();
	}

	public void setCategory(String category) {
		item.setCategory(category);
	}
	public boolean isStreaming() {
		return item.isStreaming;
	}

	public void setStreaming() {
		item.isStreaming = true;
	}

	public void toJsonItem() {
		item.toJson();
	}

	public void toJsonItem(String file) {
		item.toJson(file);
	}

	public void retrieveInfo() {
		HttpClientBuilder builder = HttpClients.custom();
		CookieStore store = null;
		HttpClientContext context = HttpClientContext.create();
		store = getCookieStore();
		if(store != null){
			builder.setDefaultCookieStore(getCookieStore());
			context.setCookieStore(getCookieStore());
		}
		
			if(Setting.GetProxyConfig() == ProxyConfig.ManualProxy 
					&& Setting.GetProxyType() == ProxyType.SOCKS){
				// Client HTTP connection objects when fully initialized can be bound to
		        // an arbitrary network socket. The process of network socket initialization,
		        // its connection to a remote address and binding to a local one is controlled
		        // by a connection socket factory.

		        // SSL context for secure connections can be created either based on
		        // system or application specific properties.
		        SSLContext sslcontext = SSLContexts.createSystemDefault();

		        // Create a registry of custom connection socket factories for supported
		        // protocol schemes.	        
		        class SocksPlainConnectionSocketFactory extends PlainConnectionSocketFactory {

		            public SocksPlainConnectionSocketFactory() {
		                super();
		            }

		            @Override
		            public Socket createSocket(final HttpContext context) throws IOException {
		                InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
		                Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
		                return new Socket(proxy);
		            }

		        }
		        
		        class SocksSSLConnectionSocketFactory extends SSLConnectionSocketFactory {

		            public SocksSSLConnectionSocketFactory(final SSLContext sslContext) {
		                super(sslContext);
		            }

		            @Override
		            public Socket createSocket(final HttpContext context) throws IOException {
		                InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
		                Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
		                return new Socket(proxy);
		            }

		        }
		        
				Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
				            .register("http", new SocksPlainConnectionSocketFactory())
				            .register("https", new SocksSSLConnectionSocketFactory(sslcontext))
				            .build();
				 
				PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
				builder.setConnectionManager(cm);
			
		}else{
			HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
			builder.setConnectionManager(connMrg);
		}
		
		
		builder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);
		
		RequestConfig globalConfig = RequestConfig.custom()
		        .setCookieSpec(StandardCookieSpec.RELAXED)
		        .build();
		builder.setDefaultRequestConfig(globalConfig);
		builder.useSystemProperties();
		
		//proxy setting gos her
		ProxySetting setting = Setting.getProxySetting();
		if(setting.getProxyConfig() != ProxyConfig.NoProxy){
			if(setting.getProxyConfig() == ProxyConfig.SystemProxy){
				builder.useSystemProperties();
			} else if(setting.getProxyConfig() == ProxyConfig.ManualProxy){
				if (ProxyType.SOCKS.equals(setting.proxyType)){
					InetSocketAddress socksAddress = new InetSocketAddress(setting.getRemoteAddress(), setting.getRemotePort());
					context.setAttribute("socks.address", socksAddress);
				} else {
					URIScheme scheme = ProxyType.HTTPS.equals(setting.proxyType)
							? URIScheme.HTTPS
							: URIScheme.HTTP;
					HttpHost proxy = new HttpHost(scheme.getId(), setting.getRemoteAddress(), setting.getRemotePort());
					builder.setProxy(proxy);
				}
			} else if(setting.getProxyConfig() == ProxyConfig.AutoConfigProxy){
				builder.setProxy(new HttpHost(setting.getUrlAutoConfig() ) );
			}
		}
		
		
		builder.setUserAgent(item.getUserAgent());
		
		CloseableHttpClient httpClient = builder.build();
		HttpGet httpGet = new HttpGet(getURL());
		RequestConfig localConfig = RequestConfig.copy(globalConfig)
		        .setCookieSpec(StandardCookieSpec.RELAXED)
		        .build();
		httpGet.setConfig(localConfig);
		
		if(getUserAgent() != null){
			httpGet.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
		}
		httpGet.addHeader(HttpHeaders.RANGE, "bytes=0-");
		
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet, context);
			
			if (response.getCode() / 100 != 2) {
				R.info( " process canceled \"state code\":"+ response.getCode());
				return;
			}

			Header content = response.getFirstHeader("Content-Disposition");
			if(content != null){
				String name = content.getValue();

				String[] parts = name.split(";");
				if (parts.length > 1){
					Optional<String> prefer =  Stream.of(parts).filter(s -> s.contains("filename*=")).findAny();
					if (prefer.isPresent()){
						name = prefer.get();
						name = name.substring(name.indexOf("''") + 2);
					}
				}

				int x;
				if( (x = name.indexOf("=\"")) == -1){
					if( (x = name.indexOf("=")) == -1){
						x = 0;
					} else {
						x++;
					}
				} else {
					x += 2;
				}
				
				int y = name.indexOf("\";");
				if( y == -1){
					y = name.length();
				}
				name = name.substring(x, y);

				R.info(name);
				setFilename(new String(name.getBytes(), StandardCharsets.UTF_8));
				R.info(this.getFilename());
				setSaveto(new File(item.getSaveto()).getParent().concat(File.separator).concat(name));
				setCacheFile(new File(item.getCacheFile()).getParent().concat(File.separator).concat(name));
			}
			setLength(Long.valueOf(response.getHeader("Content-Length").getValue()));
			R.info(response.toString());
			if(getLength() == -1){
				Header range = response.getFirstHeader(HttpHeaders.CONTENT_RANGE);
				if(range != null){
					String str = range.getValue();
					long l = getContentLengthFromContentRange(str);
					setLength(l);
				}
			}
			response.close();
		} catch (Exception e) {
			R.info(e.getMessage());
		}
	}

	public CookieStore getCookieStore() {
		return item.getCookieStore();
	}
	
	public List<String> readCookieLines() {
		return item.readCookieLines();
	}
	
}
