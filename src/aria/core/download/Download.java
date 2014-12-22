package aria.core.download;

import java.io.File;
import java.util.Arrays;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.util.Duration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import aria.core.url.Item;
import aria.core.url.Url;
import aria.core.url.type.DownState;
import aria.opt.Utils;

public class Download extends Service<Number> {

	

	public Item item;
	public Chunk[] chunks;
	public boolean[] chunkState;

	public Download(Item item) {
		super();
		this.item = item;
		initStateLine();
		timeline.setCycleCount(Timeline.INDEFINITE);
	}

	
	
	/***
	 * Override control state method
	 */
	
	@Override
	public boolean cancel() {
		stopChunks();
		return super.cancel();
	}
	
	@Override
	protected void failed() {
		stopChunks();
	}
	
	protected boolean init = false;
	public void updateProgressShow() {
		init = true;
		if(!isRunning())
			start();
//		init = false;
	}
	public boolean isInitState(){
		return init;
	}
	

	@Override
	protected Task<Number> createTask() {
		Task<Number> taskInit = new Task<Number>() {
			
			@Override
			protected Number call() throws Exception {
				updateProgress(item.downloaded, (item.length == -1) ? item.downloaded : item.length);
				updateValue(item.downloaded);
				updateMessage("Updated.");
				init = false;
				return 1;
			}
		};
		
		Task<Number> taskDown = new Task<Number>() {

			@Override
			protected Number call() throws Exception {
				item.setLastTry( System.currentTimeMillis());
				//check item length so can callRange layer
				if(item.isUnknowLength() ){
//					item = new ReadyItem(item).initItem();
					if(chunks != null){
						chunks = null;
					}
					chunks = new Chunk[item.getChunksNum()];
					chunkState = new boolean[item.getChunksNum()];
					chunks[0] = new Chunk(0, item);
					
					if (item.ranges == null) {
						item.ranges = new long[item.getChunksNum()][3];
						item.ranges[0][0] = 0;
						item.ranges[0][1] = -1;
						item.ranges[0][2] = 0;
					}
				}else{
					creatChunks();
					callRange();
				}	
				
				
				boolean[] temp = new boolean[item.getChunksNum()];
				Arrays.fill(temp, true);
				
				for (int i = 0; i < chunks.length; i++) {
					chunks[i].start();
				}
				
				while (!isCancelled()) {
					long down = 0;
					for (int i = 0; i < item.getChunksNum(); i++) {
						down += item.ranges[i][2];
						chunkState[i] = chunks[i].stop;
					}
					
					boolean equalsTrue = Arrays.equals(chunkState, temp);
					item.downloaded = down;
					updateProgress(item.downloaded, item.length == -1? item.downloaded : item.length);
					updateValue(item.downloaded);
//					System.out.println(equalsTrue);
					if (equalsTrue)break;
					Thread.sleep(300);
				}

				updateProgress(item.downloaded, item.length == -1? item.downloaded : item.length);
				updateValue(item.downloaded);
				return 1;
			}
		};
		if(init) return taskInit;
		return taskDown;
	}
	
	
	
	void stopAndPause(){
		timeline.stop();
		setDownState(DownState.Pause);
		
	}
	
	
	public void initStateLine() {
		setOnSucceeded((e) -> {
			
			timeline.stop();
			if (item.downloaded == item.length) {
				setDownState(DownState.Complete);
				if (!item.isCopied){
					
					new Thread(new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							File srcFile = new File(item.getCacheFile());
							File destFile = new File(item.getSaveto());
							if(destFile.exists()){
								if(checkConfliectName()){
									destFile = new File(item.getSaveto());
								}
								
							}
							FileUtils.moveFile(srcFile, destFile);
							item.setSaveto(destFile.getAbsolutePath());
							item.setFilename(
									FilenameUtils.getName(item.getSaveto()));
							item.clearCache();
							return null;
						}
					}).run();
					item.setCopied();
				}
			}
			else if (item.downloaded > item.length) {
				if(item.isUnknowLength()){
					setDownState(DownState.Pause);
				}else{
					setDownState(DownState.Failed);
				}
				
			} 
			else {
				setDownState(DownState.Pause);
			}

		});
		setOnCancelled((e) -> {
			stopChunks();
			stopAndPause();
		});
		setOnReady((e) -> {
			stopAndPause();
		});
		setOnScheduled((e) -> {
			stopAndPause();
		});
		setOnRunning((e) -> {
			timeline.play();
			setDownState(DownState.Downloading);
			
		});
		setOnFailed((e) -> {
			timeline.stop();
			setDownState(DownState.Failed);
			
		});
		
	}
	
	public File getConflicetFileNameFor(File destFile) {
		return getNewName(destFile, 0);
	}
	
	public File getNewName(File file, int x){
		String name = FilenameUtils.getBaseName(file.getAbsolutePath());
		String exe = FilenameUtils.getExtension(file.getAbsolutePath());
		File newFile = new File(file.getParent() 
				+ File.separator + name +"_" + x + exe);
		if(newFile.exists()){
			return getNewName(file,  ++x);
		}
		return newFile;
	}

	private ObjectProperty<DownState> downState;

	public void setDownState(DownState state) {
		downStateProperty().set(state);
		item.setState(state);
	}
	
	public DownState getDownState() {
		return downStateProperty().get();
	}
	
	public String getDown_State() {
		return getDownState().toString();
	}

	public ObjectProperty<DownState> downStateProperty() {
		if(downState == null)
			return downState = new SimpleObjectProperty<DownState>(this, "downState",item.getState());
		return downState  ;
	}
	
	

	private StringProperty transferRate;
	private double transfer = 0;

	public double getTransferRate() {
		return transfer;
	}

	public void setTransferRate(double rate) {
		transfer = rate;
		transferRateProperty().set(Utils.fileLengthUnite(rate).concat("/sec"));
	}

	public StringProperty transferRateProperty() {
		if(transferRate == null)
			transferRate =  new SimpleStringProperty(this,"transferRate");
		return transferRate;
	}

	Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> {
		calculateSpeed();
	}));

	private boolean collectData = false;

	public void canCollectSpeedData() {
		collectData = true;
	}
	
	public void stopCollectSpeedData() {
		collectData = false;
	}

	private int xSeries = 0;
	public Series<Integer, Double> series;
	public int upperBound = 60;

	
	public synchronized void addData(Double num) {
		if (series != null) {
			/*Data<Integer, Double> e = new Data<>(xSeries++, num);
			series.getData().add(e);
			Tooltip.install(e.getNode(), new Tooltip(Utils.fileLengthUnite(num))); */
			series.getData().add(new Data<>(xSeries++, num));
		} else {
			series = new Series<>();
			series.getData().add(new Data<>(xSeries++, num));
		}
		if (series.getData().size() > 60) {
			series.getData().remove(0, series.getData().size() - 60);
			upperBound = xSeries;			// start series from left to right
		}
		upperBound = xSeries;				// start series from right to left
	}

	private double downedLength  = -1;
	private double getDownedLength(){
		if(downedLength != -1)
			return downedLength;
		return downedLength = item.downloaded;
	}
	
	public SimpleStringProperty lefttimeProperty = new SimpleStringProperty(this, "lefttimeProperty", "");
	
	public void calculateSpeed() {
		// 
		double newDownLength = item.downloaded ;
		// calc speed 
		downedLength = newDownLength - getDownedLength();
		setTransferRate(downedLength);
		if (collectData)
			addData(downedLength/1000);
		// collectData = !collectData;
		
		// calc time left
		item.timeLeft = (long) ((item.length - newDownLength) / downedLength);
		lefttimeProperty.set(Utils.calcTimeLeft(item.timeLeft));
		
//		Duration duration = new Duration(item.timeLeft);
//		duration.
		
		// make downedLength equal to downloaded from 1 sec ago.
		downedLength = newDownLength;
	}
	
	public void bindState(){
		
		if(getState() == State.SUCCEEDED){
			if (item.downloaded == item.length) {
				setDownState(DownState.Complete);
			}
			else if (item.downloaded > item.length) {
				if(item.isUnknowLength()){
					setDownState(DownState.Pause);
				}else{
					setDownState(DownState.Failed);
				}
			}
			else {
				setDownState(DownState.Pause);
			}
		}else if(getState() == State.CANCELLED||
				getState() == State.READY){
			setDownState(DownState.Pause);
		}else if(getState() == State.RUNNING){
			setDownState(DownState.Downloading);
		}else if(getState() == State.FAILED){
			setDownState(DownState.Failed);
		}
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public Url getUrl() {
		return item.getUrl();
	}

	public void setUrl(Url url) {
		item.setUrl(url);
	}

	public String getURL() {
		return item.getURL();
	}

	public String getReferer() {
		return item.getReferer();
	}

	public void setURL(String url) {
		item.setURL(url);
	}

	public void setReferer(String ref) {
		item.setReferer(ref);
	}
	
	/**
	 * check if the file exists,
	 * true will change the file name of the file
	 * false keep unchanged
	 * @return true if file name had been changed
	 */
	public boolean checkConfliectName(){
		return item.checkConfliectName();
	}
	
	public String getCacheFile() { return item.getCacheFile(); }
	public String getCookieFile() { return item.getCookieFile();}
	
	public void setCacheFile(String cacheFile) {item.setCacheFile(cacheFile);}
	public void setCookieFile(String cookieFile) {item.setCookieFile(cookieFile);}
	public void setHasCookie() {item.setHasCookie();}
	public boolean haveCookie() { return item.haveCookie(); }
	
	public String getUserAgent() { return item.getUserAgent();}
	public void setUserAgent(String agent) {item.setUserAgent(agent);}



	public void callRange(){
		if (item.ranges == null) {
			item.ranges = new long[1][3];
			if(item.isUnknowLength()){
				item.ranges[0][0] = 0;
				item.ranges[0][1] = item.downloaded;
				item.ranges[0][2] = 0;
			}else{
				long sub = (int) item.getLength() / item.getChunksNum();
				item.ranges = new long[item.getChunksNum()][3];
				for (int i = 0; i < item.getChunksNum(); i++) {
					item.ranges[i][0] = sub * i;
					item.ranges[i][1] = sub * (i + 1) - 1;
					item.ranges[i][2] = 0;
				}
				item.ranges[item.getChunksNum() - 1][1] = item.getLength();
			}
			
		}
	}
	
	public void creatChunks(){
		if(chunks != null){
			for (int i = 0; i < chunks.length; i++) {
				if(chunks[i].isRunning()){
					chunks[i].cancel();
				}
				chunks[i] = null;
			}
			chunks = null;
		}
		chunks = new Chunk[item.getChunksNum()];
		chunkState = new boolean[item.getChunksNum()];
		for (int i = 0; i < chunks.length; i++) {
			chunks[i] = new Chunk(i, item);
		}
	}
	

	// stop chunks
	public void stopChunks(){
		if(chunks != null){
			for (int i = 0; i < chunks.length; i++) {
				chunks[i].cancel();
				chunks[i] = null;
			}
			chunks = null;
		}else{
//			System.err.println("chunks == null");
			
		}
		
	}
	
	public long getContentLengthFromContentRange(String range){
		return item.getContentLengthFromContentRange(range);
	}
	
	
}
