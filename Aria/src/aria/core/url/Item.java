package aria.core.url;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import aria.core.url.type.Category;
import aria.core.url.type.DownState;
import aria.core.url.type.Queue;
import aria.opt.Base64Encoder;
import aria.opt.R;
import aria.opt.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Item {

	public static int NUM_CHUNKS = 8;

	Url url;
	String filename;
	String saveto;
	String cacheFile;
	String cookieFile;
	String userAgent;
	public boolean isCopied = false;
	boolean hasCookie = false;

	private DownState state;

	public long length;
	public long downloaded;
	boolean unknowLength = false;

	private long added;
	long lastTry;
	public long timeLeft;

	String queue;
	String category;
	public boolean isStreaming = false;

	int chunksNum;
	public long[][] ranges;

	public boolean isAuthorized = false;
	private String authorization;

	private void initURL() {

		added = System.currentTimeMillis();
		lastTry = added;
		timeLeft = -1;

//		filename = FilenameUtils.getName(getURL());
//		filename = getFilename(filename);
		
		setFilename(removeParamterMark(FilenameUtils.getName(getURL())));

		Category cat = Category.getCategoryByExtension(FilenameUtils
				.getExtension(filename));
		category = cat.getName();
		saveto = cat.getSaveTo() + File.separator + filename;
		cacheFile = R.CachePath + File.separator + added 
				+ File.separator + filename;
		cookieFile = R.CachePath + File.separator + added 
				+ File.separator+ "cookie";
		
		queue = Queue.Main.getName();
		downloaded = 0;
		length = 0;
		setState(DownState.InitDown);

		removeAuthrized();
		// chunksNum = NUM_CHUNKS;
		chunksNum = 1;

	}

	public static String removeParamterMark(String filename) {
		int i = 0;
		for (int j = 0; j < filename.length(); j++) {
			if (filename.charAt(j) == '?') {
				i = j;
				break;
			}
			i = j;
		}
		if (i == filename.length() - 1)
			return filename;
		return filename.substring(0, i);
	}

	public Item(Url url) {
		this.url = url;
		initURL();
	}

	public Item(String url) throws Exception {
		try {
			this.url = new Url(url);
		} catch (MalformedURLException e) {
			throw new Exception("no url found in: " + url);
		}
		initURL();
	}

	public Item(String url, String referer) throws Exception {
		try {
			this.url = new Url(url, referer);
		} catch (MalformedURLException e) {
			throw new Exception("no url found in: " + url);
		}
		initURL();
	}

	@Override
	public String toString() {
		String str = chunksNum + "\n";
		// if(ranges != null){
		// str = ranges.length + "\n";
		// }

		if (ranges != null) {
			for (int i = 0; i < ranges.length; i++) {
				str += i + "- " + Arrays.toString(ranges[i]) + "\n";
			}
		}
		return url + "\nFile Name:	" + filename 
				+ "\nFile length:	" + length
					+ " Byte - " + Utils.fileLengthUnite(length) 
				+ "\nDownloaded:	"+  downloaded + " Byte - " 
					+ Utils.fileLengthUnite(downloaded)
				+ "\nState:	" + getState() + "\nSave to:	" + saveto
				+ "\nCache file:	" + cacheFile + "\nCookie file:	" + cookieFile
				+ "\nIn Queue:	" + queue + "\nAt Category:	" + category
				+ "\nAdded in :	" + added + "\nLast try at:	" + lastTry
				+ "\nsubRanges : " + str;

	}

	public void setAuthrized(String user, String pass) {
		String str = user + ":" + pass;
		Base64Encoder encoder = new Base64Encoder();
		setAuthorization(encoder.encode(str.getBytes()));
		isAuthorized = true;
		// System.out.println("--------------authorization-----------------");
		// System.out.println(str);
		// System.out.println(authorization);
		// System.out.println(encoder.decode(authorization));
		// System.out.println("--------------authorization-----------------");
	}

	public void removeAuthrized() {
		String str = "anonymous:anonymous";
		setAuthorization(new Base64Encoder().encode(str.getBytes()));
		isAuthorized = false;
	}

	public String getAuthorization() {
		if (isAuthorized)
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
		
		
//		setCacheFile(new File(getCacheFile()).getParent()
//				.concat(File.separator).concat(getFilename()));
//		setCookieFile(new File(getCookieFile()).getParent()
//				.concat(File.separator).concat(getFilename())
//				.concat(".cookie"));
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

	public String getCookieFile() {
		return cookieFile;
	}

	public void setCookieFile(String cookieFile) {
		setHasCookie();
		this.cookieFile = cookieFile;
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
		toJson(R.OptDownloadsDir + File.separator 
				+ added  + "-" 
				+ filename
				+ ".json");
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

	public boolean haveCookie() {
		return hasCookie;
	}

	public void setHasCookie() {
		hasCookie = true;
	}

	public void ignoreCookie() {
		hasCookie = false;
	}
	
	public String getUserAgent() { return userAgent;}
	public void setUserAgent(String agent) {userAgent = agent;}

	public void setUnknowLength() {
		unknowLength = true;
	}

	public void removeUnknowLength() {
		unknowLength = false;
	}

	public boolean isUnknowLength() {
		return unknowLength;
	}
	
	public void clearCache() {
		FileUtils.deleteQuietly(new File(cacheFile).getParentFile());
	}

	public void saveCookies(Set<Cookie> seleniumCookie) {
		Utils.writeJson(cookieFile, Utils.toJson(seleniumCookie));
		setHasCookie();
	}

	public CookieStore getCookieStore() {
		List<String> lines = readCookieLines();
		if (lines == null) {
			return null;
		}
		CookieStore store = new BasicCookieStore();
		for (String string : lines) {
			String[] str = string.split("\t");
			//     0		1		2	3		4			5		6   
			// .domain.com	TRUE	/	TRUE	ExpiryDate	name	value
			//
			
			BasicClientCookie cookie = new BasicClientCookie(str[5], str[6]);
			cookie.setDomain(str[0]);
			cookie.setPath(str[2]);
			cookie.setExpiryDate(new Date(Long.valueOf(str[4])));
			
//			// str[1] str[3] don't know what is that
//			cookie.setSecure(Boolean.valueOf(str[1]));
//			cookie.setSecure(Boolean.valueOf(str[3]));
			
			store.addCookie(cookie);
		}
		
		
		return store;
	}

	public List<String> readCookieLines() {
		List<String> lines;
		try {
			lines = FileUtils.readLines(new File(getCookieFile()));
//			for (String string : lines) {
//				System.out.println(string);
////				String[] str = string.split("\t");
////				for (String s : str) {
////					System.out.println(s);
////				}
//			}
		} catch (IOException e) {
			return null;
		}
		return lines;
	}
	

	

	// public CookieStore seleniumCookiesToCookieStore() {
	// Cookie[] seleniumCookies = getCookies();
	// CookieStore cookieStore = new BasicCookieStore();
	//
	// for (Cookie seleniumCookie : seleniumCookies) {
	// BasicClientCookie basicClientCookie = new BasicClientCookie(
	// seleniumCookie.getName(), seleniumCookie.getValue());
	// basicClientCookie.setDomain(seleniumCookie.getDomain());
	// basicClientCookie.setExpiryDate(seleniumCookie.getExpiry());
	// basicClientCookie.setPath(seleniumCookie.getPath());
	// cookieStore.addCookie(basicClientCookie);
	// }
	// return cookieStore;
	// }
	//
	// public CookieStore seleniumCookiesToCookieStore(Cookie[] seleniumCookies)
	// {
	// CookieStore cookieStore = new BasicCookieStore();
	// for (Cookie seleniumCookie : seleniumCookies) {
	// BasicClientCookie basicClientCookie = new BasicClientCookie(
	// seleniumCookie.getName(), seleniumCookie.getValue());
	// basicClientCookie.setDomain(seleniumCookie.getDomain());
	// basicClientCookie.setExpiryDate(seleniumCookie.getExpiry());
	// basicClientCookie.setPath(seleniumCookie.getPath());
	// cookieStore.addCookie(basicClientCookie);
	// }
	// return cookieStore;
	// }

}
