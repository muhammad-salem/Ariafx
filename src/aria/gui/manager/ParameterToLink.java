package aria.gui.manager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import aria.core.download.Link;
import aria.core.url.Item;
import aria.gui.fxml.control.DisplayUrlInfo;
import aria.opt.Parameter;

public class ParameterToLink {
	Parameter param;
	Link link;
	private ParameterToLink(Parameter param){
		this.param = param;
	}
	
	private void initLink() {
		String url = param.getUrl();
		String ref = param.getReferer();
		
		if(url == null){
			if(param.haveInputFile()){
				// case of more than one link
				String path =  copyInputFile();
				try {
					List<String> urls = FileUtils.readLines(new File(path));
					for (String string : urls) {
						System.out.println(string);
					}
					
					
				} catch (Exception e) {
					
				}
				
			}
		}else{
			try {
				link = new Link(url);
				if(ref != null){
					link.setReferer(ref);
				}
				
				if(param.getFileName() != null){
					link.setFilename(param.getFileName());
				}else {
					link.setFilename(Item.removeParamterMark(FilenameUtils.getName(url)));
				}
				
				if(param.getUserAgent() != null){
					link.setUserAgent(param.getUserAgent());
				}
				
				
				if(param.haveCookieFile()){
					link.setCookieFile(copyCookieFile());
				}
				
				
//				Retrieve info from Internet 
				link.retrieveInfo();
				
//				link.readCookieLines();
				
				DisplayUrlInfo.AddLink(link);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("ParameterToLink.setItem()" + url);
			}
			
			param.clear();
		}
		
	}
	
	private String copyCookieFile() {
		File srcFile, destFile = new File(param.getCookieFile());
		if (param.haveCookieFile()) {
			srcFile = new File(param.getCookieFile());
			destFile = new File(getSaveFileFor("input.cookie"));
			try {
				FileUtils.copyFile(srcFile, destFile);
			} catch (IOException e) {
				return srcFile.getPath();
			}
		}
		return destFile.getPath();
	}
	
	private String copyInputFile() {
		File srcFile, destFile = new File(param.getCookieFile());
		if (param.haveInputFile()) {
			srcFile = new File(param.getInputFile());
			destFile = new File(getSaveFileFor("input.link"));
			try {
				FileUtils.copyFile(srcFile, destFile);
			} catch (IOException e) {
				return srcFile.getPath();
			}
		}
		return destFile.getPath();
	}
	
	
	private String getSaveFileFor(String name){
		File file = new File(link.getCacheFile()).getParentFile();
		return  file.getPath() + File.separator + name;
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
			lines = FileUtils.readLines(new File(link.getCookieFile()));
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
	
	
	/**
	 * 
	 * @param param
	 */
	
	public static void initLink(Parameter param) {
		ParameterToLink toLink = new ParameterToLink(param);
		Platform.runLater(new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				toLink.initLink();
				return null;
			}
		});
		
		
	}






	
}
