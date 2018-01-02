package ariafx.core.download;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import ariafx.core.url.Item;
import ariafx.core.url.Url;
import ariafx.core.url.type.DownState;
import ariafx.notify.Notifier;
import ariafx.opt.Utils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.util.Duration;

public class Download extends Service<Number> {

	public Item item;
	public Chunk[] chunks;
	public boolean[] chunkState;
	
	public static int ParallelChunks = 4;
	
	public Download(Item item) {
		super();
		this.item = item;
		initStateLine();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.statusProperty().addListener(new ChangeListener<Animation.Status>() {

			@Override
			public void changed(ObservableValue<? extends Animation.Status> observable,
					Animation.Status oldValue, Animation.Status newValue) {
				
				if(newValue.equals(Animation.Status.STOPPED)){
					sTime = 0;
					setCurrentTime("");
				}
			}

			
		});
		
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
		if (!isRunning())
			start();
		// init = false;
	}

	public boolean isInitState() {
		return init;
	}

	@Override
	protected Task<Number> createTask() {
		Task<Number> taskInit = new Task<Number>() {

			@Override
			protected Number call() throws Exception {
				updateProgress(item.downloaded,
						(item.length == -1) ? item.downloaded : item.length);
				updateValue(item.downloaded);
				updateMessage("Updated.");
				//init = false;
				return 1;
			}
		};
		
		taskInit.setOnSucceeded((e)->{
			init = false;
		});

		Task<Number> taskDown = new Task<Number>() {

			@Override
			protected Number call() throws Exception {
				item.setLastTry(System.currentTimeMillis());
				// check item length so can callRange layer
				if (item.isUnknowLength()) {
					// item = new ReadyItem(item).initItem();
					if (chunks != null) {
						chunks = null;
					}
					chunks = new Chunk[item.getChunksNum()];
					chunkState = new boolean[item.getChunksNum()];
					chunks[0] = new Chunk(0, item);

					if (item.ranges == null) {
						item.ranges = new long[item.getChunksNum()][3];
						item.ranges[0][0] = 0;
						item.ranges[0][1] = 0;
						item.ranges[0][2] = 0;
					}
				} else {
					callRange();
					creatChunks();
					
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
					updateProgress(item.downloaded,
							item.length == -1 ? item.downloaded : item.length);
					updateValue(item.downloaded);
					// System.out.println(equalsTrue);
					if (equalsTrue)
						break;
					Thread.sleep(300);
					
					for (int i = 0; i < item.getChunksNum(); i++) {
						
						if(chunks[i].httpStateCode/100 == 5){
							chunks[i].cancel();
							chunks[i].start();
							System.out.println("restat chunk "+ i 
									+ " chunks[i].httpStateCode "+ chunks[i].httpStateCode);
						}
						Thread.sleep(300);
						
					}
					
				}

				updateProgress(item.downloaded,
						item.length == -1 ? item.downloaded : item.length);
				updateValue(item.downloaded);
				return 1;
			}
		};
		if (init)
			return taskInit;
		return taskDown;
	}

	void stopAndPause() {
		timeline.stop();
		setDownState(DownState.Pause);
		
	}

	public void initStateLine() {
		setOnSucceeded((e) -> {
			
			timeline.stop();
			if (item.downloaded == item.length) {
				setDownState(DownState.Complete);
				if (!item.isCopied){
					moveFileAfterDownload();
					Notifier.NotifyUser(item.getFilename(), "Download Complete");
				}
				
			} else if (item.downloaded > item.length) {
				if (item.isUnknowLength()) {
					setDownState(DownState.Pause);
				} else {
					// in case of somthig wrong
					setDownState(DownState.Complete);
					moveFileAfterDownload();
				}
			} else {
				setDownState(DownState.Pause);
				checkRetry();
			}

		});
		setOnCancelled((e) -> {
			stopChunks();
			stopAndPause();
			resetRetry();
		});
		setOnReady((e) -> {
			stopAndPause();
		});
		setOnScheduled((e) -> {
			stopAndPause();
		});
		setOnRunning((e) -> {
			timeline.play();
			if(isInitState()){
				setDownState(DownState.Pause);
			}else{
				setDownState(DownState.Downloading);
			}
			
		});
		
		setOnFailed((e) -> {
			timeline.stop();
//			setDownState(DownState.Failed);
			if (item.isUnknowLength()) {
				setDownState(DownState.Complete);
				moveFileAfterDownload();
				
			} else {
				setDownState(DownState.Failed);
				checkRetry();
			}
			
		});

	}
	
	private int retry = 0;
	public static int MAX_Retry = 99;
	public void checkRetry(){
		if(isInitState()){
			return;
		}
		if(!Utils.isAnyNetWorkInterfaceUp()){
			return;
		}
		if(++retry < MAX_Retry){
			restart();
//			System.out.println("retry:"+ retry);
		}
	}
	
	public void resetRetry(){
		retry = 0;
	}
	
	public int getRetry(){
		return retry;
	}
	
	public StringExpression retryProperty() {
		return Bindings.concat(retry);
				
	}

	private void moveFileAfterDownload() {

		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				File srcFile = new File(item.getCacheFile());
				File destFile = new File(item.getSaveto());
				if (destFile.exists()) {
					if (checkConfliectName()) {
						destFile = new File(item.getSaveto());
					}

				}
				FileUtils.moveFile(srcFile, destFile);
				item.setSaveto(destFile.getAbsolutePath());
				item.setFilename(FilenameUtils.getName(item.getSaveto()));
				item.clearCache();
				return null;
			}
		}).run();
		item.setCopied();

	}

	public File getConflicetFileNameFor(File destFile) {
		return getNewName(destFile, 0);
	}

	public File getNewName(File file, int x) {
		String name = FilenameUtils.getBaseName(file.getAbsolutePath());
		String exe = FilenameUtils.getExtension(file.getAbsolutePath());
		File newFile = new File(file.getParent() + File.separator + name + "_"
				+ x + exe);
		if (newFile.exists()) {
			return getNewName(file, ++x);
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
		if (downState == null)
			return downState = new SimpleObjectProperty<DownState>(this,
					"downState", item.getState());
		return downState;
	}

	private StringProperty transferRate;
	private double transfer = 0;

	public double getTransferRate() {
		return transfer;
	}

	public void setTransferRate(double rate) {
		transfer = rate;
//		transferRateProperty().set(Utils.fileLengthUnite(rate).concat("/sec"));
		transferRateProperty().set(Utils.sizeLengthFormate0_0( rate).concat("/sec"));
	}

	public StringProperty transferRateProperty() {
		if (transferRate == null)
			transferRate = new SimpleStringProperty(this, "transferRate");
		return transferRate;
	}
	
	private StringProperty remaining;
	
	public String getRemaining() {
		return remainingProperty().get();
	}
	
	public void setRemaining(long value) {
		item.timeLeft = value;
		setRemaining(Utils.fileLengthUnite(value));
	}

	public void setRemaining(String value) {
		remainingProperty().set(value);
	}
	
	public StringProperty remainingProperty() {
		if (remaining == null)
			remaining = new SimpleStringProperty(this, "remaining");
		return remaining;
	}
	
	
	private StringProperty currentTime;
	private int sTime = 0;
	
	public String getCurrentTime() {
		return currentTimeProperty().get();
	}
	
	public void setCurrentTime(long value) {
		setCurrentTime(Utils.getTime(value));
	}

	public void setCurrentTime(String value) {
		currentTimeProperty().set(value);
	}
	
	public StringProperty currentTimeProperty() {
		if (currentTime == null)
			currentTime = new SimpleStringProperty(this, "currentTime");
		return currentTime;
	}
	

	Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), (e) -> {
		calculateSpeed();
		setCurrentTime( ++sTime);
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
			/*
			 * Data<Integer, Double> e = new Data<>(xSeries++, num);
			 * series.getData().add(e); Tooltip.install(e.getNode(), new
			 * Tooltip(Utils.fileLengthUnite(num)));
			 */
			series.getData().add(new Data<>(xSeries++, num));
		} else {
			series = new Series<>();
			series.getData().add(new Data<>(xSeries++, num));
		}
		if (series.getData().size() > 60) {
			series.getData().remove(0, series.getData().size() - 60);
			upperBound = xSeries; // start series from left to right
		}
		upperBound = xSeries; // start series from right to left
	}

	private double downedLength = -1;

	private double getDownedLength() {
		if (downedLength != -1)
			return downedLength;
		return downedLength = item.downloaded;
	}

	public SimpleStringProperty lefttimeProperty = new SimpleStringProperty(
			this, "lefttimeProperty", "");

	public void calculateSpeed() {
		//
		double newDownLength = 0;
		for (int i = 0; i < item.getChunksNum(); i++) {
			newDownLength += item.ranges[i][2];
		}
		
		// calc speed
		downedLength = newDownLength - getDownedLength();
		
		setTransferRate(downedLength);
		if (collectData)
			addData(downedLength / 1024);
		// collectData = !collectData;

		// calc time left
		item.timeLeft = (long) ((item.length - newDownLength) / downedLength);
		lefttimeProperty.set(Utils.getTime(item.timeLeft));

		// calc remaining
		if(!item.isUnknowLength()){
			setRemaining(item.length - item.downloaded);
		}

		// make downedLength equal to downloaded from 1 sec ago.
		downedLength = newDownLength;
	}

	public void bindState() {

		if (getState() == State.SUCCEEDED) {
			if (item.downloaded == item.length) {
				setDownState(DownState.Complete);
			} else if (item.downloaded > item.length) {
				if (item.isUnknowLength()) {
					setDownState(DownState.Pause);
				} else {
					setDownState(DownState.Failed);
				}
			} else {
				setDownState(DownState.Pause);
			}
		} else if (getState() == State.CANCELLED || getState() == State.READY) {
			setDownState(DownState.Pause);
		} else if (getState() == State.RUNNING) {
			setDownState(DownState.Downloading);
		} else if (getState() == State.FAILED) {
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
	 * check if the file exists, true will change the file name of the file
	 * false keep unchanged
	 * 
	 * @return true if file name had been changed
	 */
	public boolean checkConfliectName() {
		return item.checkConfliectName();
	}

	public String getCacheFile() {
		return item.getCacheFile();
	}

	public String getCookieFile() {
		return item.getCookieFile();
	}

	public void setCacheFile(String cacheFile) {
		item.setCacheFile(cacheFile);
	}

	public void setCookieFile(String cookieFile) {
		item.setCookieFile(cookieFile);
	}

	public void setHasCookie() {
		item.setHasCookie();
	}

	public boolean haveCookie() {
		return item.haveCookie();
	}

	public String getUserAgent() {
		return item.getUserAgent();
	}

	public void setUserAgent(String agent) {
		item.setUserAgent(agent);
	}

	public void callRange() {
		if (item.ranges == null) {
			item.ranges = new long[1][3];
			if (item.isUnknowLength()) {
				
				item.ranges[0][0] = 0;
				item.ranges[0][1] = item.downloaded;
				item.ranges[0][2] = item.downloaded;
				
			} else {
				
//				item.ranges = new long[item.getChunksNum()][3];
//				
//				item.ranges[item.getChunksNum() - 1][1] = item.getLength();
//				item.ranges[item.getChunksNum() - 1][0] = item.getLength() - (1024*1024) ;
//				item.ranges[item.getChunksNum() - 1][2] = 0;
//				long sub =  item.getLength() - (1024*1024) / (int)item.getChunksNum() - 1;
//				
//				for (int i = 0; i < item.getChunksNum() -1; i++) {
//					item.ranges[i][0] = sub * i;
//					item.ranges[i][1] = sub * (i + 1) - 1;
//					
//				}
				
				item.ranges = new long[item.getChunksNum()][3];
				
				long sub =  item.getLength() / (long)item.getChunksNum();
				
				for (int i = 0; i < item.getChunksNum(); i++) {
					item.ranges[i][0] = i * sub;
					item.ranges[i][1] = (i + 1) * sub - 1;
					item.ranges[i][2] = 0;
				}
				item.ranges[item.getChunksNum()-1][1] = item.getLength()-1;
			}
		}
		
//		for (int i = 0; i < item.ranges.length; i++) {
//			System.out.println(Arrays.toString(item.ranges[i]));
//		}
	}
	
	public void callRangeForStreaming() {
		if (item.ranges == null) {
			item.ranges = new long[1][3];
			if (item.isUnknowLength()) {
				
				item.ranges[0][0] = 0;
				item.ranges[0][1] = item.downloaded;
				item.ranges[0][2] = 0;
				
			} else {
				
				item.ranges = new long[item.getChunksNum()][3];
				
				long sub =  item.getLength() / (int)item.getChunksNum();
				
				for (int i = 0; i < item.getChunksNum(); i++) {
					item.ranges[i][0] = sub * i;
					item.ranges[i][1] = sub * (i + 1) - 1;
					
				}
				// set last 2 chunk 
				item.ranges[item.getChunksNum()-2][1] = item.ranges[item.getChunksNum()-1][1]- (2*1024*1024);
				item.ranges[item.getChunksNum()-1][0] = item.ranges[item.getChunksNum()-2][1]+1;
				item.ranges[item.getChunksNum()-1][1] = item.getLength()-1;
				
			}
		}
		
//		for (int i = 0; i < item.ranges.length; i++) {
//			System.out.println(Arrays.toString(item.ranges[i]));
//		}
	}

	public void creatChunks() {
		if (chunks != null) {
			for (int i = 0; i < chunks.length; i++) {
				if (chunks[i].isRunning()) {
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
	public void stopChunks() {
		if (chunks != null) {
			for (int i = 0; i < chunks.length; i++) {
				chunks[i].cancel();
				chunks[i] = null;
			}
			chunks = null;
		} else {
			// System.err.println("chunks == null");

		}

	}

	public long getContentLengthFromContentRange(String range) {
		return item.getContentLengthFromContentRange(range);
	}
	
	public Chunk[] getChunks(){
		return chunks;
	}
	
}
