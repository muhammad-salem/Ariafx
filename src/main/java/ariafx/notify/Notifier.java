package ariafx.notify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.image.Image;

import org.apache.commons.io.FileUtils;

import ariafx.opt.R;

import com.sun.javafx.PlatformUtil;

public class Notifier {
	

	static String notify_icon = null;
	static String notify_mac= null;
	static String notify_toast = null;
	static String notify_Platform = null;
	
	static Image image = null;
	public static boolean  NativeNotificationSupport = false;
	public static boolean stopNotify = false;
	public static Pos pos = Pos.TOP_RIGHT;
	
	static{
		NativeNotificationSupport = isNativeNotification();

		URL source = Notifier.class.getResource("notify.png");
		File destination = new File(new File(R.ConfigPath, "notify"), "notify.png");
		Copy(source, destination);
		notify_icon = destination.getAbsolutePath();
		File notifyDir = new File(R.ConfigPath, "notify");
		notifyDir.mkdir();
		if(PlatformUtil.isWindows()){
			source = Notifier.class.getResource("toast.exe");
			destination = new File(new File(R.ConfigPath, "notify"), "toast.exe");
			Copy(source, destination);
			notify_toast = destination.getAbsolutePath();
		}else if(PlatformUtil.isMac()){
			source = Notifier.class.getResource("AriafxNotify.zip");
			destination = new File(new File(R.ConfigPath, "notify"), "AriafxNotify.zip");
			Copy(source, destination);
			Extract(destination);
			notify_mac = destination.getParent() 
						 + "/AriafxNotify.app/Contents/MacOS/applet";
		}
	}
	
	
	
	public static void NotifyUser(String  title, String body){
		if(stopNotify){ return; }
		if(NativeNotificationSupport){ NotifyNative(title, body); }
	}
	public static void NotifyNative(String  title, String body){

		switch (notify_Platform) {
		case "Unix":
			NotifyNativeUnix(title,  body);
			break;
		case "Windows":
			NotifyNativeWindows(title,  body);
			break;
		case "Mac":
			NotifyNativeMac(title,  body);
			break;
		default:
			break;
		}
		
		
	}
	

	private static void NotifyNativeMac(String title, String body) {
		List<String> list = new ArrayList<String>();
//		"/AriafxNotify.app/Contents/MacOS/applet";
		list.add(notify_mac);
		
		ProcessBuilder builder = new ProcessBuilder(list);
		builder.environment().put("msg", title);
		builder.environment().put("head", body);
		try {
			builder.start();
		} catch (Exception e) {
			R.cout("error in open " + e.getMessage());
		}
	
	}
	private static void NotifyNativeWindows(String title, String body) {
		List<String> list = new ArrayList<String>();
		list.add(notify_toast);
		list.add("-t");
		list.add(title);
		list.add("-m");
		list.add(body);
		list.add("-p");
		list.add(notify_icon);
		
		ProcessBuilder builder = new ProcessBuilder(list);
		try {
			builder.start();
		} catch (IOException e) {
			R.cout("error in open " + e.getMessage());
		}
	}
	private static void NotifyNativeUnix(String title, String body) {
		List<String> list = new ArrayList<String>();
		list.add("notify-send");
		list.add("-i");  list.add(notify_icon);
		list.add(title); list.add(body);
		list.add("-t");  list.add("1000");
		/*
		list.add("-c"); list.add("Network");
		list.add("-u"); list.add("low");
		*/
		
		ProcessBuilder builder = new ProcessBuilder(list);
		try {
			builder.start();
		} catch (IOException e) {
			R.cout("error in open " + e.getMessage());
		}
		
	}
	
	
	
	public static boolean isNativeNotification() {
		String osname = System.getProperty("os.name");
		if(PlatformUtil.isUnix() || PlatformUtil.isLinux()){
			File file = new File("/usr/bin/notify-send");
			if(file.exists()){
				notify_Platform = "Unix";
				return true;
			}else {
				notify_Platform = "";
				return false;
			}
		}else if(PlatformUtil.isWindows()) {
			if(PlatformUtil.isWin7OrLater())
				notify_Platform = "Windows";
				return true;
		}else if(PlatformUtil.isMac()) {
			notify_Platform = "Mac";
			return true;
		}
		notify_Platform = "";
		return false;
	}
	

	private static void Extract(File destination) {
		List<String> list = new ArrayList<String>();
		// unzip ~/Desktop/table/AriafxNotify.zip -d ~/Documents/
		list.add("unzip");
		list.add(destination.getAbsolutePath());
		list.add("-d");
		list.add(destination.getParent());
		
		ProcessBuilder builder = new ProcessBuilder(list);
		try {
			builder.start();
		} catch (Exception e) {
			R.cout("error in open " + e.getMessage());
		}
	}
	
	public static void Copy(URL source, File destination){
		try {
			FileUtils.copyURLToFile(source, destination);
			
		} catch (IOException e) {
			R.cout("filed Copy "+ source);
		}
	}
	

}