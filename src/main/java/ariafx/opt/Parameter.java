package ariafx.opt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Parameter {

	
	
	HashMap<String, String> map = new HashMap<String, String>();
	long added = 0;
	boolean haveCookieFile = false;
	boolean haveInputFile = false;
	boolean hasKnowSize = false;
	
	
	public long getAdded() { return added; }
	public boolean haveCookieFile() { return haveCookieFile; }
	public boolean haveInputFile()  { return haveInputFile; }

	public Parameter() {
		this.added = System.currentTimeMillis();
	}

	public Parameter(String[] args) {
		this();
		setParamter(args);
		
	}

	public String addUrl(String url) {
		return map.put("--url", url);
	}
	public String getUrl() {
		return map.get("--url") == null ? null : map.get("--url").replaceAll(" ", "");
	}

	public String addCookie(String cookie) {
		return map.put("--cookie", cookie);
	}
	public String getCookie() {
		return map.get("--cookie");
	}

	public String addReferer(String referer) {
		return map.put("--http-referer", referer);
	}
	
	public String getReferer() {
		return map.get("--http-referer") == null ? null : map.get("--http-referer").replaceAll(" ", "");
	}
	
	public String addUserAgent(String agent) {
		return map.put("--user-agent", agent);
	}

	public String getUserAgent() {
		return map.get("--user-agent");
	}

	public String addFileName(String name) {
		return map.put("--file-name", name);
	}
	public String getFileName() {
		return map.get("--file-name");
	}

	public String getInputFile() {
		return map.get("--input-file");
	}

	public String addCookieFile(String cookieFile) {
		return map.put("--cookie-file", cookieFile);
	}
	public String getCookieFile() {
		return map.get("--cookie-file");
	}
	
	public String addFileSize(String fileSize) {
		return map.put("--fileSize", fileSize);
	}
	public String getFileSizeString() {
		return map.get("--fileSize");
	}
	public long getFileSize() {
		return Long.valueOf(map.get("--fileSize"));
	}

	public void setParamter(String[] args) {
		
//		System.out.println(Arrays.toString(args));
		
		for (int i = 0; i < args.length; i++) {
			String str = args[i];

//			[--url=URL] [--http-referer=REFERER] [--file-name=FNAME]  
//			[--user-agent=UA] [--cookie=COOKIE] [--cookie-file=CFILE] 
//			[--input-file=UFILE] [--fileSize=fileSize]
			

			if (args[i].contains("--url") && !args[i].contains("=")) {
				map.put(args[i++], args[i].replaceAll(" ", ""));
			} else if (args[i].contains("--cookie") && !args[i].contains("=")) {
				map.put(args[i++], args[i]);
			} else if (args[i].contains("--http-referer")
					&& !args[i].contains("=")) {
				map.put(args[i++], args[i]).replaceAll(" ", "");
			} else if (args[i].contains("--user-agent")
					&& !args[i].contains("=")) {
				map.put(args[i++], args[i]);
			} else if (args[i].contains("--file-name")
					&& !args[i].contains("=")) {
				map.put(args[i++], args[i]);
			} else if (args[i].contains("--input-file")
					&& !args[i].contains("=")) {
				map.put(args[i++], args[i]);
			} else if (args[i].contains("--cookie-file")
					&& !args[i].contains("=")) {
				addCookieFile(args[++i]);
			} else if (args[i].contains("--fileSize")
					&& !args[i].contains("=")) {
				map.put(args[i++], args[i]);
				hasKnowSize = true;
			}
			
			

			else if (str.contains("=")) {
				for (int j = 0; j < str.length(); j++) {
					if (str.charAt(j) == '=') {
						String str1 = str.substring(0, j);
						String str2 = str.substring(j + 1, str.length());
						for (int k = i + 1; k < args.length; k++) {
							if (args[k].contains("--")) {
								break;
							} else {
								str2 += " ".concat(args[k]);
								i = k;
							}
						}
						map.put(str1, str2);
//						System.err.println(str1);
//						System.err.println(str2);
						break;
					}
				}

			} else {
				map.put( args[i], "unknown");
			}
		}
		
		
		copyFiles();
		toJsonFile();
	}

	public void copyFiles() {
		if (getCookieFile() != null) {
			haveCookieFile = true;
			File srcFile = new File(getCookieFile());
			if(srcFile.exists()){
				File destFile = new File(getCookieFilePath());
				try {
					FileUtils.copyFile(srcFile, destFile);
//					map.put("--cookie-file", destFile.getPath());
					addCookieFile(destFile.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				haveCookieFile = false;
				map.remove("--cookie-file");
			}
		}
		
		if (getCookie() != null) {
			String[] strs = getCookie().split("\t");
			if(haveCookieFile){
				if(strs.length > 4){
					File destFile = new File(getCookieFilePath());
					try{
						FileUtils.writeStringToFile(destFile, getCookie(), "UTF-8", true);
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{
				File file = new File(getCookieFilePath());
				try{
					FileUtils.writeStringToFile(file, getCookie(), "UTF-8", true);
				}catch (IOException e) {
					haveCookieFile = false;
					map.remove("--cookie-file");
					return;
				}
				haveCookieFile = true;
				map.put("--cookie-file", file.getPath());
			}
		}
		

		if (getInputFile() != null) {
			haveInputFile = true;
			File srcFile = new File(getInputFile());
			if(srcFile.exists()){
				File destFile = new File(getSaveFileFor("input", ".link"));
				try {
					FileUtils.copyFile(srcFile, destFile);
					map.put("--input-file", destFile.getPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				haveInputFile = false;
				map.remove("--input-file");
			}
			
		}
	}
	
	private String getCookieFilePath() {
		return getSaveFileFor("input", ".cookie");
	}
	public void clear() {
//		FileUtils.deleteQuietly(new File(R.NewLink + R.separator + getAdded()));
		File paramfile = new File(R.NewLink + R.separator + getAdded());
		try {
			R.delete(paramfile);
		} catch (Exception e) {
			R.cout("Can't delete paramter " + getAdded());
		}
		
	}

	public HashMap<String, String> getMap() {
		return map;
	}

	public String getSaveFileFor(String name){
		return getSaveFileFor(name, "");
	}
	public String getSaveFileFor(String name, String extn){
		return R.NewLink + R.separator 
			   + getAdded() + R.separator
			   + name + extn;
	}
	
	@Override
	public String toString() {
		String str = "url:	" + getUrl() + "\n";
		if (getReferer() != null)
			str += "ref:	" + getReferer() + "\n";
		if (getFileName() != null)
			str += "name:	" + getFileName() + "\n";
		if (getFileSizeString() != null)
			str += "FileSize:	" + getFileSizeString() + " Byte\n";
		str += "agent:	" + getUserAgent() + "\n";
		if (getCookie() != null)
			str += "cookie:	" + getCookie() + "\n";
		if (getCookieFile() != null)
			str += "cokFile:	" + getCookieFile() + "\n";
		if (getInputFile() != null)
			str += "inputF:	" + getInputFile();
		return str;
	}

	public String toJson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();

		Gson gson = builder.create();
		return gson.toJson(this);
	}

	public boolean toJsonFile(File dir) {
		String str = dir.getPath() + File.separator + getAdded() + ".json";
		return writeJson(str, toJson());
	}
	
	public boolean toJsonFile() {
		String str = getSaveFileFor("input", ".json");
		return writeJson(str, toJson());
	}

	public boolean toJsonFile(String dir) {
		String str = dir + File.separator + getAdded() + ".json";
		return writeJson(str, toJson());
	}

	public static boolean writeJson(String file, String json) {
		try {
			FileUtils.writeStringToFile(new File(file), json,
					StandardCharsets.UTF_8, false);
//			System.err.println(json);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean writeJson(File file, String json) {
		try {
			FileUtils.writeStringToFile(file, json, 
					StandardCharsets.UTF_8, false);
//			System.err.println(json);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Parameter fromJson(String file) {
		return fromJson(file, Parameter.class);
	}

	public static Parameter fromJson(File file) {
		return fromJson(file.getPath(), Parameter.class);
	}

	public static <T> T fromJson(File file, Class<T> classT) {
		return fromJson(file.getPath(), classT);
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
		} catch (Exception e) {
			System.err.println("no file found");
			return null;
		}
		return t;
	}
	

}
