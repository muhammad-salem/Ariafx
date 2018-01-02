package ariafx.nativemessaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;

import ariafx.opt.R;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChromeMSG {

	long date;
	int id;
	String mime;
	long fileSize;
	boolean list;
	boolean page;
	String url;
	String origUrl;
	String cookies = null ;
	String referrer;
	String filename;
	String post;
	String useragent;

	public static ChromeMSG CreateMessage(String parse) {
		Gson gson = new Gson();
		ChromeMSG msg = gson.fromJson(parse, ChromeMSG.class);
		return msg;
	}
	/**
	 * [--url=URL] 
	 * [--http-referer=REFERER] 
	 * [--file-name=FNAME]  
	 * [--user-agent=UA] 
	 * [--cookie=COOKIE] 
	 * [--cookie-file=CFILE] 
	 * [--input-file=UFILE]
	 * [--fileSize=int]
	 * @return
	 */
	public String[] toArgs() {
		String args = "";
		
		args += "--url=" + getURL() + ":;";
		if(!getReferrer().equals("") ){
			args += "--http-referer=" + getReferrer() + ":;";
		}
		if(!getFilename().equals("") ){
			args += "--file-name=" + getFilename() + ":;";
		}
		
		if(!getUserAgent().equals("") ){
			args += "--user-agent=" + getUserAgent() + ":;";
		}
		
		if(getCookies() != null ){
//			args += "--cookie=" + getCookies() + ":;";
			
			File cookeFile = new File(R.NewLink + 
					R.separator + getDate() + 
					R.separator + "cookfile.txt");
			
			try {
				FileUtils.forceMkdir(cookeFile.getParentFile());
				FileOutputStream stream = new FileOutputStream(cookeFile);
				stream.write(getCookies().getBytes());
				stream.flush();
				stream.close();
				
				args += "--cookie-file=" + cookeFile.getAbsolutePath() + ":;";
			} catch (Exception e) {
				R.cout("can't create cookies");
				R.cout(e.getMessage());
			}
			
		}
		
		if (getFileSize() != -1) {
				args += " --fileSize=" + getFileSize() + ":;";
		}
		
		
//		if(isList() ){
//			args += "--input-file=" + isList() + ":;";
//		}
		
		
		
		return args.split(":;");
	}
	

	public String getURL() {
		return url;
	}

	public String getOrigUrl() {
		return origUrl;
	}

	public String getReferrer() {
		return referrer;
	}

	public String getCookies() {
		return cookies;
	}

	public String getFilename() {
		return filename;
	}

	public String getPostData() {
		return post;
	}

	public String getUserAgent() {
		return useragent;
	}

	public String getMime() {
		return mime;
	}

	public int getID() {
		return id;
	}

	public long getFileSize() {
		return fileSize;
	}

	public long getDate() {
		return date;
	}

	public boolean isList() {
		return list;
	}

	public boolean isPage() {
		return page;
	}

	@Override
	public String toString() {
		String str = "";
		
		str += !getURL().equals("") 		? ( "url: " + getURL() + "\n") : "";
		str += !getOrigUrl().equals("")		? ( "origUrl: " + getOrigUrl() + "\n") : "";
		str += !getFilename().equals("") 	? ( "filename: " + getFilename() + "\n") : "";
		str += !getMime().equals("") 		? ( "mime: " + getMime() + "\n") : "";
		str += !getReferrer().equals("") 	? ( "referrer: " + getReferrer() + "\n") : "";
		str += !getUserAgent().equals("") 	? ( "userAgent: " + getUserAgent() + "\n") : "";
		str += getID() != -1 				? ( "id: " + getID() + "\n") : "";
		str += getDate() != -1 				? ( "date: " + getDate() + "\n") : "";
		str += isPage() 					? ( "is Page \n" ) : "";
		str += isList() 					? ( "is list \n" ) : "";
		str += !getCookies().equals("") 	? ( "cookies: \n" + getCookies() + "\n" ): "";

		return str;
	}

	public static String toJson(Object object) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		return gson.toJson(object);
	}

	public static boolean toJsonFile(String file, Object object) {
		return writeJson(file, toJson(object));
	}

	public static boolean writeJson(String file, String json) {
		try {
			FileUtils.forceMkdir(new File(file).getParentFile());
			FileWriter writer = new FileWriter(file);
			writer.write(json);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// System.out.println(json);
		return true;
	}

	/**
	 *
	 * @param <T>
	 *            the type of the desired object
	 * @param file
	 *            file path to read from.
	 */
	public static <T> T fromJson(String file, Class<T> classT) {
		Gson gson = new Gson();
		T t = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			t = gson.fromJson(reader, classT);
			// System.out.println(url);
		} catch (Exception e) {
			System.err.println("no file found");
			return null;
		}
		return t;
	}

}
