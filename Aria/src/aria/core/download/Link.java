package aria.core.download;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
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
		HttpClientBuilder  builder = HttpClients.custom();
		if (haveCookie()) {
			CookieStore store = getCookieStore();
			if(store != null)
				builder.setDefaultCookieStore(getCookieStore());
		}
		
		HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
		builder.setConnectionManager(connMrg);
		builder.setRedirectStrategy(new LaxRedirectStrategy());
		
		CloseableHttpClient  httpClient = builder.build();
		HttpGet httpGet = new HttpGet(getURL());
		
		if(getUserAgent() != null){
			httpGet.addHeader(HttpHeaders.USER_AGENT, getUserAgent());
		}
		HttpClientContext context = HttpClientContext.create();
		
		try {
			HttpResponse response = httpClient.execute(httpGet, context);
			
			if (response.getStatusLine().getStatusCode() / 100 != 2) {
				System.out.println(
						  " process canceld \"state code\":"
						+ response.getStatusLine().getStatusCode());
				return;
			}
			
			setLength(response.getEntity().getContentLength());
			
			System.out.println(response.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		System.err.println(item);
	}

	public CookieStore getCookieStore() {
		return item.getCookieStore();
	}
	
	public List<String> readCookieLines() {
		return item.readCookieLines();
	}
	
}
