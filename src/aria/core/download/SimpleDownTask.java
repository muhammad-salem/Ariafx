package aria.core.download;

import java.io.InputStream;
import java.io.RandomAccessFile;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import aria.core.url.Item;

public class SimpleDownTask extends Task<Number> {

	public CloseableHttpClient httpClient;
	public HttpGet httpGet;
	
	public Item item;
	
	public RandomAccessFile file;
	public boolean isFinished= false;
	
	
	public SimpleDownTask(Item item) {
		this.item = item;
		this.httpClient = HttpClients.createDefault();
		this.httpGet = new HttpGet(item.getURL());
		
		
		
	}

	@Override
	protected Number call() throws Exception {
		if(item.ranges != null){
			/* how to get range for that @param id */
			String byteRange = item.ranges[0][0]+item.ranges[0][2] + "-" + item.ranges[1];
			httpGet.addHeader(HttpHeaders.RANGE, "bytes=" + byteRange);
		}
		
		HttpResponse response = httpClient.execute(httpGet);
		
		System.out.println(response.toString());
		
		if(response.getStatusLine().getStatusCode()/100 != 2){
			System.out.println("process canceld \"state code\":" 
					+ response.getStatusLine().getStatusCode());
			cancel();
			return 0;
		}
		updateTitle(response.getStatusLine().toString());
		HttpEntity entity = response.getEntity();
		
		if(item.ranges == null){
			item.ranges = new long [1][3];
			item.ranges[0][0] = 0;								//start
			item.ranges[0][1] = entity.getContentLength();		//end
			item.ranges[0][2] = 0;								//download
		}
		
		
		
		file = new RandomAccessFile(item.getSaveto(), "rw");
		file.seek(item.ranges[0][0]+item.ranges[0][2] );
		
		if (entity != null) {
			
            InputStream inputStream = entity.getContent();
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1 && !isCancelled()) {
            	file.write(bytes, 0, read);
            	item.ranges[0][2]  += read;
                Platform.runLater(()->{
                	updateProgress(item.ranges[0][2] , item.ranges[0][1]-item.ranges[0][0] );
	                updateTitle(response.getStatusLine().toString() + " State: " + getState());
	                updateValue(item.ranges[0][2] );
	                item.downloaded = item.ranges[0][2];
                });
            }
            file.close();
			inputStream.close();
        }
        else {
            System.out.println("Download failed!");
            cancel();
        }
		
		/*how to update the chunk progress*/
		updateProgress(item.ranges[0][2] , item.ranges[0][1]-item.ranges[0][0]);
//		System.out.println("finish "+ stateProperty().get().toString());
		isFinished = true;
		return 0;
	}

}
