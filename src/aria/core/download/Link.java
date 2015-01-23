package aria.core.download;

import java.io.File;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import aria.core.url.Item;
import aria.core.url.Url;
import aria.opt.R;

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
	
	
	
	public void setAuthrized(String user, String pass) {
		item.setAuthrized(user, pass);
	}

	public void removeAuthrized() {
		item.removeAuthrized();
	}

	public String getAuthorization() {
		return item.getAuthorization();
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

	public String getCacheFile() {
		return item.getCacheFile();
	}

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
	
	public void removeStreaming() {
		item.isStreaming = false;
		//implement of changes in ranges
		{
			/**
			 *  start new download from item.downloaded,
			 *  conquer and div the rest for item.chunksNum
			 */
			item.setChunksNum(4);
			item.ranges = new long[item.getChunksNum()][3];
			long remaning = item.length - item.downloaded;
			long sub = remaning / item.getChunksNum();
			
			for (int i = 0; i < item.getChunksNum(); i++) {
				item.ranges[i][0] = remaning + (sub * i);
				item.ranges[i][1] = remaning + (sub * (i + 1) - 1);
				item.ranges[i][2] = 0;
			}
			item.ranges[item.getChunksNum() - 1][1] = item.getLength();
		}
	}

	public void toJsonItem() {
		item.toJson();
	}

	public void toJsonItem(String file) {
		item.toJson(file);
	}

	public void retrieveInfo() {
		//initLodgger();
		
		HttpClientBuilder  builder = HttpClients.custom();
		CookieStore store = null;
		if (haveCookie()) {
			store = getCookieStore();
			if(store != null)
				builder.setDefaultCookieStore(getCookieStore());
		}
		
		HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
		builder.setConnectionManager(connMrg);
		builder.setRedirectStrategy(new LaxRedirectStrategy());
		
		RequestConfig globalConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
		        .build();
		builder.setDefaultRequestConfig(globalConfig);
		
		CloseableHttpClient  httpClient = builder.build();
		HttpGet httpGet = new HttpGet(getURL());
		RequestConfig localConfig = RequestConfig.copy(globalConfig)
		        .setCookieSpec(CookieSpecs.BEST_MATCH)
		        .build();
		httpGet.setConfig(localConfig);
		
		if(getUserAgent() != null){
			httpGet.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
		}
		if (item.isAuthorized) {
			httpGet.addHeader(HttpHeaders.AUTHORIZATION,
					"Besic " + item.getAuthorization());
		}
		httpGet.addHeader(HttpHeaders.RANGE, "bytes=0-");
		
		
		HttpClientContext context = HttpClientContext.create();
		if (haveCookie() && store != null) {
			context.setCookieStore(store);
		}
		
		
		try {
			HttpResponse response = httpClient.execute(httpGet, context);
			
			if (response.getStatusLine().getStatusCode() / 100 != 2) {
				//System.out.println( " process canceld \"state code\":"
				//		+ response.getStatusLine().getStatusCode());
				R.cout( " process canceld \"state code\":"
						+ response.getStatusLine().getStatusCode());
				return;
			}
			
			Header content = response.getFirstHeader("Content-Disposition");
			
			if(content != null){
				System.out.printf("%s %s\n", content.getName(), content.getValue());
				String name = content.getValue();
				int x = 0;
				if( (x = name.indexOf("=\"")) == -1){
					if( (x = name.indexOf("=")) == -1){
						x = 0;
					}else{
						x++;
					}
				}else{
					x += 2;
				}
				
				int y = 0;
				if( (y = name.indexOf("\";")) == -1){
					y = name.length();
				}else{
//					y += 2;
				}
					
				name = name.substring(x, y);

				setFilename(name);
				setSaveto(new File(item.getSaveto()).getParent()
						.concat(File.separator).concat(name));
				setCacheFile(new File(item.getCacheFile()).getParent()
						.concat(File.separator).concat(name));
			}
			setLength(response.getEntity().getContentLength());
//			System.out.println(response.toString());
			R.cout(response.toString());
			if(getLength() == -1){
				Header range = response.getFirstHeader(HttpHeaders.CONTENT_RANGE);
				if(range != null){
					String str = range.getValue();
					long l = getContentLengthFromContentRange(str);
					setLength(l);
				}
			}
			
		} catch (Exception e) {
			R.cout(e.getMessage());
			//e.printStackTrace();
		}finally{
			httpGet.releaseConnection();
		}
		
//		R.cout(item);
	}

	public CookieStore getCookieStore() {
		return item.getCookieStore();
	}
	
	public List<String> readCookieLines() {
		return item.readCookieLines();
	}
	
}
