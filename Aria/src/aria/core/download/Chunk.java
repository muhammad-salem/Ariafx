package aria.core.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import aria.core.url.Item;
import aria.core.url.Url;

public class Chunk extends Service<Number> {

	public int id;
	public CloseableHttpClient httpClient;
	public HttpGet httpGet;
	public Header[] headers;
	
	public long [] range;
	public String url, saveFile;
	
	public RandomAccessFile file;
	public boolean stop = false;
	
	
	
	public Chunk(int id, String url, String saveFile, Header[] headers, long [] range) {
		super();
		this.id = id;
		this.url = url;
		this.saveFile = saveFile;
		this.headers = headers;
		this.range = range;
		
	}
	
	public Chunk(int id, String url, String saveFile, Header[] headers /*, long end*/) {
		super();
		this.id = id;
		this.url = url;
		this.saveFile = saveFile;
		this.headers = headers;
		
//		this.range = new long[3];
//		this.range[0] = 0;					//start
//		this.range[1] = end;				//end
//		this.range[2] = 0;					//download
		
	}
	
	/**
	 * quick simple chunk download the file no chunks 
	 * @param url the {@link Url}  
	 * @param saveFile where to save the content of that link
	 */
	public Chunk( String url, String saveFile) {
		this.id = 0;
		this.url = url;
		this.saveFile = saveFile;
	}
	
	public Chunk(int id, Item item) {
		super();
		this.id = id;
		this.url = item.getURL();
//		this.saveFile = item.getSaveto();
		this.saveFile = item.getCacheFile();
		this.range = item.ranges[id];
	}

	@Override
	protected void scheduled() {
		initChunk();
		super.scheduled();
	}
	
	@Override
	public boolean cancel() {
		stop = true;
		return super.cancel();
	}
	
	@Override
	protected void failed() {
		stop = true;
	}
	
	private void initChunk(){
		httpClient = HttpClients.createDefault();
		httpGet = new HttpGet(url);
		if(headers != null)
			httpGet.setHeaders(headers);
		try {
			FileUtils.forceMkdir(new File(saveFile).getParentFile());
		} catch (IOException e) {
			System.err.println("couldn't make dir file");
			e.printStackTrace();
		}
	}
	
	@Override
	protected Task<Number> createTask() {
		
		return new Task<Number>() {
			
			@Override
			protected Number call() throws Exception {
				if(range != null){
					if(range[2] >= range[0] && range[0] != 0) return -1;
					if(range[1] == (range[0] + range[2]) ) return -1;
					/* how to get range for that @param id */
					String byteRange = range[0]+range[2] + "-" + range[1];
					httpGet.addHeader(HttpHeaders.RANGE, "bytes=" + byteRange);
				}
				HttpClientContext context = HttpClientContext.create();
				HttpResponse response = httpClient.execute(httpGet, context);
				
				System.out.println("start:"+ id +"-> "+ response.toString());
				
				if(response.getStatusLine().getStatusCode()/100 != 2){
					System.out.println("id:"+id+" process canceld \"state code\":" 
							+ response.getStatusLine().getStatusCode());
					cancel();
					return 0;
				}
				updateTitle(response.getStatusLine().toString());
				HttpEntity entity = response.getEntity();
				
				if(range == null){
					range = new long [3];
					range[0] = 0;								//start
					range[1] = entity.getContentLength();		//end
					range[2] = 0;								//download
				}
				
				file = new RandomAccessFile(saveFile, "rw");
				file.seek(range[0]+range[2] );
				
				if (entity != null) {
					
		            InputStream inputStream = entity.getContent();
		            int read = 0;
		            long length =  range[1]-range[0];
		            byte[] bytes = new byte[1024];
		            while ((read = inputStream.read(bytes)) != -1 
		            		&& !isCancelled()
		            		&& !stop) {
		            	file.write(bytes, 0, read);
		            	range[2]  += read;
		                Platform.runLater(()->{
		                	updateProgress(range[2] , length );
//			                updateTitle(response.getStatusLine().toString() + " State: " + getState());
			                updateValue(range[2] );
		                });
		            }
		            file.close();
					inputStream.close();
		        }
		        else {
		            System.out.println("Download failed!");
		        }
				
				/*how to update the chunk progress*/
				updateProgress(range[2] , range[1]-range[0]);
				updateValue(range[2] );
//				System.out.println("finish "+ stateProperty().get().toString());
				stop = true;
				return 0;
			}
		};
	}


}
