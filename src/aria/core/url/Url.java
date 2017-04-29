package aria.core.url;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aria.opt.Utils;

public class Url {
	
	public String url;
	public String referer;
	
	
	public Url(URL url){
		this.url = url.toString();
		this.referer = "";
	}
	
	public Url(URL url, URL referer){
		this.url = url.toString();
		this.referer = referer.toString();
	}
	
	public Url(String url) throws MalformedURLException {
		try {
			if(url.indexOf("%") < 6){
				url = URLDecoder.decode(url, "UTF-8");
			}
			
//			URI uri = new URI(url);
//			url = uri.toURL().toString();
//			System.out.println("from uri " +  url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(Utils.verifyURL(url)){
			this.url = url;
			this.referer = "";
		} else {
			System.err.println("fired exp");
			throw new MalformedURLException("not url formate");
		}
//		System.out.println("creted");
	}
	
	public Url(String url, String referer) throws MalformedURLException {
		this(url);
		this.referer = referer;
	}
	
	@Override
	public String toString() {
		String str = "URL:		" + url ;
		if(!referer.equals("")) str += "\nReferer:	" + referer ;
	   return str;
	}
	
	public String toIDMString() {
		String str = "<\n"+url ;
		if(!referer.equals("")) str += "\nreferer: " + referer ;
		str += "\n>";
	   return str;
	}
	
	public static List<Url> fromEf2IDMFile(File file) {
		List<Url> urls = new ArrayList<Url>();
		
		List<String> list;
		try {
			list = FileUtils.readLines(file, Charset.defaultCharset());
		} catch (IOException e) {
			return new ArrayList<Url>();
		}
		list.removeIf(new Predicate<String>() {

			@Override
			public boolean test(String str) {
				if(str.contains(">") || str.contains("<")) return true;
				return false;
			}
		});
		
		int i = 0;
		do {
			try {
				Url url = new Url(list.get(i));
				urls.add(url);
				++i;
				if(i >= list.size()) break;
				if(list.get(i).contains("referer: ")){
					String str = list.get(i).replaceFirst("referer: ", "");
					url.setReferer(str);
					++i;
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
		} while (true);
	   return urls;
	}
	
	public static List<Url> fromTextFile(File file) {
		List<Url> urls = new ArrayList<Url>();
		
		List<String> list;
		try {
			list = FileUtils.readLines(file, Charset.defaultCharset());
		} catch (IOException e) {
			return new ArrayList<Url>();
		}
		
		for (int i = 0; i < list.size(); i++) {
			try {
				Url url = new Url(list.get(i));
				urls.add(url);
			} catch (Exception e) {
				continue;
			}
		}
		
	   return urls;
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
//		System.out.println(json);
	}
	public static Url fromJson(String file) {
		Gson gson = new Gson();
		Url url = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			url = gson.fromJson(reader, Url.class);
//			System.out.println(url);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return url;
	}
	/*
    public static boolean verifyURL(String fileURL) {

        String str = fileURL.toLowerCase();
        // Allow FTP, HTTP and HTTPS URLs.
        if (str.startsWith("http://") || str.startsWith("https://")
                || str.startsWith("ftp://")) {
            // Verify format of URL.
            URL verifiedUrl = null;
            try {
                verifiedUrl = new URL(fileURL);
            } catch (MalformedURLException e) {
                System.err.println("not valid url");
                return false;
            }

            // Make sure URL specifies a file.
           
            if (verifiedUrl.getFile().length() < 9) {
                return false;
            }
//             System.out.println(verifiedUrl.toString() + " >>");
        }else{
        	return false;
        }
        return true;

    }
    */


	public boolean setURL(String link) {
		try {
			if(link.indexOf("%") < 6){
				link = URLDecoder.decode(url, "UTF-8");
			}
			
//			URI uri = new URI(url);
//			url = uri.toURL().toString();
//			System.out.println("from uri " +  url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(Utils.verifyURL(link)){
			url = link;
			return true;
		}
		return false;
	}

	public String getURL() {
		return url;
	}
	
	public URL getUrl() {
		
		try {
			URL url = new URL(this.url);
			return url;
		} catch (MalformedURLException e) {
		}
		return null;
	}
	

	public boolean setReferer(String link) {
		try {
			if(link.indexOf("%") < 6){
				link = URLDecoder.decode(url, "UTF-8");
			}
			
//			URI uri = new URI(url);
//			url = uri.toURL().toString();
//			System.out.println("from uri " +  url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(Utils.verifyURL(link)){
			referer = link;
			return true;
		}
		return false;
	}

	public String getReferer() {
		return referer;
	}
	public URL getReFeReR() {
		
		try {
			URL url = new URL(this.referer);
			return url;
		} catch (MalformedURLException e) {
		}
		return null;
	}

	
}
