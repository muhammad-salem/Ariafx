package d.core.url;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import d.core.url.type.Category;
import d.core.url.type.DownState;
import d.core.url.type.Queue;
import d.opt.Base64Encoder;
import d.opt.R;
import d.opt.Utils;

public class Item {
	
	public static int NUM_CHUNKS = 8;
	
	
	Url 		url;
	String 	filename;
	String 	saveto;
	String 	cacheFile;
	boolean isCopied = false;
	
	private DownState 	state;
	
	public long	length;
	public long	downloaded;
	
	private long added;
	long		 lastTry;
	public long	 timeLeft;

	String	queue;
	String	category;
	public boolean isStreaming = false;

	int chunksNum;
	public long[][] ranges;
	
	public boolean isAuthorized = false;
	private String authorization;
    
	private void initURL(){
		
		added = System.nanoTime();
		lastTry = added;
		timeLeft = -1;
		
		filename = FilenameUtils.getName(getURL());
		Category cat =  Category.getCategoryByExtension(FilenameUtils.getExtension(filename));
		category = cat.getName();
		saveto = cat.getSaveTo() + File.separator + filename;
		cacheFile = R.CachePath + File.separator + added + File.separator + filename;
		queue = Queue.Main.getName();
		downloaded = 0;
		length = 0;
		setState(DownState.InitDown);
		
		
		removeAuthrized();
//		chunksNum = NUM_CHUNKS;
		chunksNum = 1;
		
	}
	public Item(Url url) {
		this.url = url;
		initURL();
	}
	
	public Item(String url) throws Exception{
		try {
			this.url = new Url(url);
		} catch (MalformedURLException e) {
			throw new Exception("no url found in: " + url);
		}
		initURL();
	}
	
	public Item(String url, String referer) throws Exception  {
		try {
			this.url = new Url(url, referer);
		} catch (MalformedURLException e) {
			throw new Exception("no url found in: " + url);
		}
		initURL();
	}
	
	@Override
	public String toString() {
		String str = chunksNum+"\n";
//		if(ranges != null){
//			str =  ranges.length +  "\n";
//		}
		
		if(ranges != null){
			for (int i = 0; i < ranges.length; i++) {
				str += i + "- "+Arrays.toString(ranges[i]) +"\n" ;
			}
		}
	   return 	url 
			   + "\nFile Name:	"+ filename
			   + "\nFile length:	"+ length + " Byte - " + Utils.fileLengthUnite(length)
			   + "\nDownloaded:	"+ downloaded + " Byte - " + Utils.fileLengthUnite(downloaded)
			   + "\nState:	"+ getState()
			   + "\nSave to:	"+ saveto
			   + "\nCache file:	"+ cacheFile
			   + "\nIn Queue:	"+ queue
			   + "\nAt Category:	"+ category
			   + "\nAdded in :	"+ added
			   + "\nLast try at:	"+ lastTry
			   + "\nsubRanges : " +str; 
	   
	}
	
    
    public void setAuthrized(String user, String pass) {
    	String str = user+":"+pass;
    	Base64Encoder encoder = new Base64Encoder();
    	setAuthorization(encoder.encode(str.getBytes()));
        isAuthorized = true;
//        System.out.println("--------------authorization-----------------");
//        System.out.println(str);
//        System.out.println(authorization);
//		System.out.println(encoder.decode(authorization));
//        System.out.println("--------------authorization-----------------");
    }
    public void removeAuthrized() {
    	String str = "anonymous:anonymous";
        setAuthorization(new Base64Encoder().encode(str.getBytes()));
        isAuthorized= false;
    }

	public String getAuthorization() {
		if(isAuthorized)
			return authorization;
		return null;
	}

	private void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	
	public Url getUrl() {
		return url;
	}
	public void setUrl(Url url) {
		this.url = url;
	}
	
	public void setURL(String link) {
		url.setURL(link);
	}

	public String getURL() {
		return url.getURL();
	}
	
	public void setReferer(String link) {
		url.setReferer(link);
	}
	
	public String getReferer() {
		return url.getReferer();
	}
	
	public int getChunksNum() {
		return chunksNum;
	}
	public void setChunksNum(int chunksNum) {
		this.chunksNum = chunksNum;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getSaveto() {
		return saveto;
	}
	public void setSaveto(String saveto) {
		this.saveto = saveto;
	}
	public String getCacheFile() {
		return cacheFile;
	}
	public void setCacheFile(String cacheFile) {
		this.cacheFile = cacheFile;
	}
	
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public long getDownloaded() {
		return downloaded;
	}
	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}
	public long getAdded() {
		return added;
	}
	
	public long getLastTry() {
		return lastTry;
	}
	public void setLastTry(long lastTry) {
		this.lastTry = lastTry;
	}
	public long getTimeLeft() {
		return timeLeft;
	}
	public void setTimeLeft(long timeLeft) {
		this.timeLeft = timeLeft;
	}
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public void toJson() {
		toJson(R.OptDownloadsDir + File.separator + filename +  "-"+ added + ".json");
	}
	public void toJson(String file) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		String json = gson.toJson(this);
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(json);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Item fromJson(String file) {
		Gson gson = new Gson();
		Item item = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			item = gson.fromJson(reader, Item.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return item;
	}
	public DownState getState() {
		return state;
	}
	public void setState(DownState state) {
		this.state = state;
	}
	
	public void setCopied() {
		isCopied = true;
	}
	
	public void resetCopied() {
		isCopied = false;
	}
	
	
	public void copyFileToDownloadDir(){
		if(isCopied) return;
		Service<Void> service = new Service<Void>() {
			
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							FileUtils.copyFile(new File(cacheFile), new File(saveto));
						} catch (IOException e) {
							System.err.println("couldn't copy file");
							e.printStackTrace();
						}
						return null;
					}
				};
			}
		};
		service.start();
		service.setOnSucceeded((e)->{
			clearCache();
		});
	}
	
	public void clearCache(){
		FileUtils.deleteQuietly(new File(cacheFile).getParentFile());
	}
	

}
