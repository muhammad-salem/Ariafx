package aria.core.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import javax.net.ssl.SSLContext;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import aria.core.download.ProxySetting.ProxyConfig;
import aria.core.download.ProxySetting.ProxyType;
import aria.core.url.Item;
import aria.gui.fxml.control.ChunkUI;
import aria.opt.R;
import aria.opt.Setting;
import aria.opt.Utils;

public class Chunk extends Service<Number> implements ChunkUI{

	public CloseableHttpClient httpClient;
	public HttpGet httpGet;
	public HttpClientContext context;
	public Header[] headers;

	public long[] range;
	public String url, cachedFile;

	public RandomAccessFile file;
	public boolean stop = false;
	private Item item;
	
	public SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", -1);
	public SimpleStringProperty stateCode = new SimpleStringProperty(this, "stateCode", "");
	public SimpleStringProperty size = new SimpleStringProperty(this, "size", "");
	public SimpleStringProperty done = new SimpleStringProperty(this, "done", "");
	public int httpStateCode;
	
	
//	public Chunk(int id, String url, String saveFile, Header[] headers,
//			long[] range) {
//		super();
//		this.id = id;
//		this.url = url;
//		this.cachedFile = saveFile;
//		this.headers = headers;
//		this.range = range;
//
//	}
//
//	public Chunk(int id, String url, String saveFile, Header[] headers /*
//																		 * ,
//																		 * long
//																		 * end
//																		 */) {
//		super();
//		this.id = id;
//		this.url = url;
//		this.cachedFile = saveFile;
//		this.headers = headers;
//
//		// this.range = new long[3];
//		// this.range[0] = 0; //start
//		// this.range[1] = end; //end
//		// this.range[2] = 0; //download
//
//	}
//
//	/**
//	 * quick simple chunk download the file no chunks
//	 * 
//	 * @param url
//	 *            the {@link Url}
//	 * @param saveFile
//	 *            where to save the content of that link
//	 */
//	public Chunk(String url, String saveTo) {
//		this.id = 0;
//		this.url = url;
//		this.cachedFile = saveTo;
//		try {
//			item = new Item(url);
//			item.setCacheFile(saveTo);
//			item.isStreaming = true;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public Chunk(int id, Item item) {
		super();
		setId(id+1);
		this.item = item;
		this.url = item.getURL();
		// this.saveFile = item.getSaveto();
		this.cachedFile = item.getCacheFile();
		this.range = item.ranges[id];
		
	}
	
	
	private void closeConnection() {
		if(httpClient != null){
			try {
				httpGet.releaseConnection();
				if(file != null)
					file.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void scheduled() {
//		closeConnection();
		initChunk();
		super.scheduled();
	}

	@Override
	public boolean cancel() {
		stop = true;
		closeConnection();
		return super.cancel();
	}

	@Override
	protected void failed() {
		closeConnection();
		stop = true;
	}

	private void initChunk() {
		
		HttpClientBuilder  builder = HttpClients.custom();
		CookieStore store = null;
		context = HttpClientContext.create();
		if (item.haveCookie()) {
			store = item.getCookieStore();
			if(store != null){
				builder.setDefaultCookieStore(item.getCookieStore());
				context.setCookieStore(item.getCookieStore());
			}
				
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
		builder.setRedirectStrategy(new LaxRedirectStrategy());
		
		RequestConfig globalConfig = RequestConfig.custom()
//		        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
		        .setCookieSpec(CookieSpecs.DEFAULT)
		        .build();
		builder.setDefaultRequestConfig(globalConfig);
		builder.useSystemProperties();
		//builder.setProxy(new HttpHost("127.0.0.1", 8089));
		
		//proxy setting gos her
		//proxy setting gos her
				ProxySetting setting = Setting.getProxySetting();
				if(setting.getProxyConfig() != ProxyConfig.NoProxy){
					if(setting.getProxyConfig() == ProxyConfig.SystemProxy){
						builder.useSystemProperties();
					} else if(setting.getProxyConfig() == ProxyConfig.ManualProxy){
						HttpHost proxy = new HttpHost(setting.getRemoteAddress(), setting.getRemotePort());
						if(setting.getProxyType() == ProxyType.HTTPS){
							builder.setProxy(proxy);
						} 
						else if(setting.getProxyType() == ProxyType.HTTPS){
								proxy = new HttpHost(setting.getRemoteAddress(), setting.getRemotePort(), "https");
								builder.setProxy(proxy);
						} 
						else if(setting.getProxyType() == ProxyType.SOCKS){
							 InetSocketAddress socksaddr = new InetSocketAddress(setting.getRemoteAddress(), setting.getRemotePort());
							 context.setAttribute("socks.address", socksaddr);
						}
								
						
						
					} else if(setting.getProxyConfig() == ProxyConfig.AutoConfigProxy){
//								HttpHost proxy = new HttpHost(ProxySetting.HTTPRemoteAddress, ProxySetting.HTTPRemotePort);
						builder.setProxy(new HttpHost( setting.getUrlAutoConfig() ) );
					}
				}
		
		
		
		
		
		httpClient = builder.build();

		httpGet = new HttpGet(url);
		RequestConfig localConfig = RequestConfig.copy(globalConfig)
		        .setCookieSpec(CookieSpecs.STANDARD_STRICT)
//		        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
		        .build();
		httpGet.setConfig(localConfig);
		if (headers != null){
			httpGet.setHeaders(headers);
		}
		if(item.getUserAgent() != null){
			httpGet.addHeader(HttpHeaders.USER_AGENT, item.getUserAgent());
		}
		if (item.isAuthorized) {
			httpGet.addHeader(HttpHeaders.AUTHORIZATION,
					"Besic " + item.getAuthorization());
		}
		try {
			FileUtils.forceMkdir(new File(cachedFile).getParentFile());
		} catch (IOException e) {
			R.cout("couldn't make dir file");
		}
	}

	@Override
	protected Task<Number> createTask() {

		return new Task<Number>() {

			@Override
			protected Number call() throws Exception {
				if (range != null) {
					updateValue(range[0] + range[2]);
					updateProgress(range[2], (range[1] - range[0] ));
					setSize(Utils.fileLengthUnite(range[1] - range[0]));
					updateMessage(Utils.fileLengthUnite(range[2]));
					
					if (range[1] == (range[0] + range[2])){
						setStateCode("Done");
						return 1;
					}
					else if (range[1] == (range[0] + range[2] - 1)){
						setStateCode("Done");
						return 1;
					}else if (range[2] >= range[0] && (range[0] != 0  )){
						updateProgress((range[0] + range[2]), range[1]);
						setStateCode("Error");
						return -1;
					}
					
					/* how to get range for that @param id */
					String byteRange = range[0] + range[2] + "-";
					if (range[1] != -1) {
						byteRange += range[1];
					}

					httpGet.addHeader(HttpHeaders.RANGE, "bytes=" + byteRange);
				}
//				HttpClientContext context = HttpClientContext.create();
				HttpResponse response = httpClient.execute(httpGet, context);
				//System.out.println("-> " + response.toString());
				R.cout("-> " + response.toString());
				
				stateCode.set(response.getStatusLine().getReasonPhrase());
				setSize(Utils.fileLengthUnite(range[1] - range[0]));
				httpStateCode = response.getStatusLine().getStatusCode();
				
				if (response.getStatusLine().getStatusCode() / 100 != 2) {
					String strLog = "id:" + id.get()
							+ " process canceld \"state code\":"
							+ response.getStatusLine().getStatusCode();
					//System.out.println(strLog);
					R.cout(strLog);
					cancel();
					return 0;
				}
				
				updateTitle(response.getStatusLine().toString());
				HttpEntity entity = response.getEntity();

				if (range == null) {
					range = new long[3];
					range[0] = 0; // start
					range[1] = entity.getContentLength(); // end
					range[2] = 0; // download
				}

				file = new RandomAccessFile(cachedFile, "rw");
				file.seek(range[0] + range[2]);
				

				if (entity != null) {

					InputStream inputStream = entity.getContent();
					int read = 0;
					long length = range[1] - range[0];
					byte[] bytes = new byte[1024];
					while ((read = inputStream.read(bytes)) != -1
							&& !isCancelled() && !stop) {
						file.write(bytes, 0, read);
						range[2] += read;
						Platform.runLater(() -> {
							if (item.isUnknowLength() ) {
								updateProgress(range[2], range[1]);
								item.downloaded = range[2];
							} else {
								updateProgress(range[2], length);
							}
							// updateTitle(response.getStatusLine().toString() +
							// " State: " + getState());
							updateValue(range[2]);
							updateMessage(Utils.fileLengthUnite(range[2]));
						});
					}
					file.close();
					inputStream.close();
					httpGet.releaseConnection();
					
					if(!isCancelled() && !stop){
						setStateCode("Done");
					}
					
				} else {
					//System.out.println("Download failed!");
					R.cout("download failed");
				}

				/* how to update the chunk progress */
				// updateProgress(range[2] , range[1]-range[0]); // for now
				updateValue(range[2]);
				stop = true;
				return 1;
			}
		};
	}
	
	

	@Override
	public StringProperty stateCodeProperty() {
		return stateCode;
	}
	
	@Override
	public IntegerProperty idProperty() {
		return id;
	}
	@Override
	public StringProperty sizeProperty() {
		return size;
	}
	@Override
	public ReadOnlyStringProperty doneProperty() {
		return messageProperty();
	}
	
	
	
	
	

}
