package ariafx.download.method;

import javafx.concurrent.Task;
import ariafx.core.download.Link;
import ariafx.core.url.Item;
import ariafx.core.url.Url;

public abstract class DownloadMethod extends Link {

	public DownloadMethod(Url url) {
		super(url);
	}

	public DownloadMethod(String url) throws Exception {
		super(url);
	}

	public DownloadMethod(String url, String referrer) throws Exception {
		super(url, referrer);
	}

	public DownloadMethod(Item item) {
		super(item);
	}
	
	public Task<Number> getDefaultUpdateTask(){
		Task<Number> taskInit = new Task<Number>() {
			@Override
			protected Number call() throws Exception {
				updateProgress(item.downloaded, (item.length == -1) ? item.downloaded : item.length);
				updateValue(item.downloaded);
				updateMessage("Updated.");
				//init = false;
				return 1;
			}
		};
		return taskInit;
	}
	
	public abstract Task<Number> createDownloadTask();
	
	
	@Override
	protected Task<Number> createTask() {
		System.out.println("createTask");
		Task<Number> task = null;
		if(init){
			task = getDefaultUpdateTask();
			task.setOnSucceeded((e)->{
				init = false;
			});
		} else{
			task = createDownloadTask();
		}
		return task;
	}
	
	

}
