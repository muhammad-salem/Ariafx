package aria.core.download;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import aria.core.url.Item;
import aria.opt.R;

public class ReadyItem {

	Item item;
	CloseableHttpClient httpClient;
	HttpGet httpGet;
	HttpResponse response;
	HttpEntity entity;
	
	public ReadyItem(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	

	/***
	 * Suppose to get the length of the file and get the default file name
	 * 
	 * @return {@link Item} the modified item
	 */
	public Item initItem() {
		item.setLastTry(System.currentTimeMillis());

//		CredentialsProvider provider = new BasicCredentialsProvider();
//		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
//				"anonymous", "anonymous");
//		provider.setCredentials(AuthScope.ANY, credentials);
		
		RequestConfig globalConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
		        .build();
		httpClient = HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy())
//				.setDefaultCredentialsProvider(provider)
				.setDefaultRequestConfig(globalConfig).build();

		// httpClient = HttpClients.createDefault();

		httpGet = new HttpGet(item.getURL());
		RequestConfig localConfig = RequestConfig.copy(globalConfig)
		        .setCookieSpec(CookieSpecs.BEST_MATCH)
		        .build();
		httpGet.setConfig(localConfig);
		
		if (!item.getReferer().equals("null"))
			httpGet.addHeader(HttpHeaders.REFERER, item.getReferer());
		if (item.isAuthorized) {
			httpGet.addHeader(HttpHeaders.AUTHORIZATION,
					"Besic " + item.getAuthorization());
		}
		httpGet.addHeader(HttpHeaders.RANGE, "bytes=0-");
		try {
			HttpClientContext context = HttpClientContext.create();
			response = httpClient.execute(httpGet, context);
			entity = response.getEntity();
			
			
			String name;
			Header content = response.getFirstHeader("Content-Disposition");
			
			if(content != null){
				//System.out.printf("%s %s\n", content.getName(), content.getValue());
				R.cout(content.getName() +" "+ content.getValue());
				name = content.getValue();
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
				if( (y = name.indexOf("\"")) == -1){
					y = name.length();
				}else{
//					y += 2;
				}
					
				name = name.substring(x, y);

				item.setFilename(name);
				item.setSaveto(new File(item.getSaveto()).getParent()
						.concat(File.separator).concat(name));
				item.setCacheFile(new File(item.getCacheFile()).getParent()
						.concat(File.separator).concat(name));
			}else{
				name = item.getFilename();
			}
			
			item.setSaveto(new File(item.getSaveto()).getParent()
					.concat(File.separator).concat(name));
			item.setCacheFile(new File(item.getCacheFile()).getParent()
					.concat(File.separator).concat(name));
			
			
			R.cout(response.toString());
//			System.out.println(response.toString());
			
			item.setLength(response.getEntity().getContentLength());
			
			if(item.getLength() == -1){
				Header range = response.getFirstHeader(HttpHeaders.CONTENT_RANGE);
				if(range != null){
					String str = range.getValue();
					long l = item.getContentLengthFromContentRange(str);
					item.setLength(l);
				}
			}
			if (item.length > 0) {
				item.removeUnknowLength();
			} else {
				item.setUnknowLength();
			}

		} catch (Exception e) {
			//e.printStackTrace();
			R.cout(e.getMessage());
			item.length = -1;
			item.setUnknowLength();
			item.setChunksNum(1);
			R.cout("ItemReady.initItem() " + e.getMessage() + "\n"
					+ item.getURL());
//			System.err.println("ItemReady.initItem() " + e.getMessage() + "\n"
//					+ item.getURL());
			return item;
		}finally{
			httpGet.releaseConnection();
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

		// System.err.println(item.length);
		return item;
	}

}
