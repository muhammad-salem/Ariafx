package aria.core.download;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import aria.core.url.Item;

public class ItemReady {
	
	Item item;
	CloseableHttpClient httpClient;
	HttpGet httpGet;
	HttpResponse response;
	HttpEntity entity;
	
	public ItemReady(Item item) {
		this.item = item;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	/***
	 * Suppose to get the length of the file
	 * and get the default file name 
	 * @return {@link Item} the modified item
	 */
	public Item initItem() {
		item.setLastTry(System.currentTimeMillis());
		
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("anonymous", "anonymous");
		provider.setCredentials(AuthScope.ANY, credentials);
		httpClient = HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setDefaultCredentialsProvider(provider).build();
		
//		httpClient = HttpClients.createDefault();
		httpGet = new HttpGet(item.getURL());
		if (!item.getReferer().equals("null"))
			httpGet.addHeader(HttpHeaders.REFERER, item.getReferer());
		if (item.isAuthorized) {
			httpGet.addHeader(HttpHeaders.AUTHORIZATION,
					"Bearer " + item.getAuthorization());
		}
		try {
			HttpClientContext context = HttpClientContext.create();
			response = httpClient.execute(httpGet, context);
			entity = response.getEntity();
			item.length = entity.getContentLength();
			
			System.out.println(response.toString());
//			String url = context.getRedirectLocations().get(context.getRedirectLocations().size()-1).toString();
//			System.out.println(url);
			
//			Stage stage = new Stage();
//			WebView view = new WebView();
//			view.getEngine().load(url);
//			stage.setScene(new Scene(view));
//			stage.show();
			
		} catch (Exception  e) {
			e.printStackTrace();
			item.length = -1;
			System.err.println("ItemReady.initItem() "+e.getMessage()+"\n"+ item.getURL());
			return item;
		}
		
		if (item.ranges == null) {
			long sub = (int) item.getLength() / item.getChunksNum();
			item.ranges = new long[item.getChunksNum()][3];
			for (int i = 0; i < item.getChunksNum(); i++) {
				item.ranges[i][0] = sub * i;
				item.ranges[i][1] = sub * (i + 1) - 1;
				item.ranges[i][2] = 0;
			}
			item.ranges[item.getChunksNum() - 1][1] = item.getLength();
		}
		
//		System.err.println(item.length);
		
		return item;
	}

}
