package ariafx.opt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ariafx.core.download.Download;
import ariafx.core.download.Link;
import ariafx.core.url.Item;
import ariafx.core.url.type.Category;
import ariafx.core.url.type.Queue;
import ariafx.core.url.type.Type;
import ariafx.gui.fxml.AriafxMainGUI;
import ariafx.gui.manager.DownList;
import ariafx.gui.manager.DownManager;

import com.sun.javafx.PlatformUtil;

public class R {
	
	public static String[] getPaths(){
		return new String[]{DefaultPath,
				CachePath, LockFile,
				ConfigPath, NewLink,
				OptDownloadsDir, OptQueuesDir, OptCategoresDir,
				downloadsPath, queuesPath, categoresPath};
	}

	public static String separator = File.separator;
	public static String app_name = "ariafx";
//	public static String app_nameA = "Ariafx";
	public static String UserHome = System.getProperty("user.home");
	public static String DefaultPath = UserHome + separator + "Downloads" 
//						+ separator + app_nameA
						;
	
	public static String CachePath = UserHome + separator 
						+ ".cache" + separator + app_name;
	
	public static String ConfigPath = UserHome + separator 
						+ ".config" + separator + app_name;
	
	public static String LockFile = CachePath + separator + "lock";
	public static String NewLink = ConfigPath + separator + "newLink";
	public static String TempDir = "/tmp/";
	
	static{
		String str = "";
		if(PlatformUtil.isWin7OrLater()){
			str = UserHome + separator + "AppData" 
					+ separator + "Roaming" + separator + app_name + separator;
			CachePath  = str + "cache";
			ConfigPath = str + "config";
			
		} else if(PlatformUtil.isWindows()){
			DefaultPath = UserHome + separator + "Documents" + separator +  "Downloads" 
//					+ separator + app_nameA
					;
			str = UserHome +  separator + "Application Data" 
					+ separator + app_name + separator;
			CachePath  = str + "cache";
			ConfigPath = str + "config";
			
		} else if(PlatformUtil.isMac()){ // Library/Application Support/
			str = UserHome +  separator + "Library" + separator + "Application Support" 
					+ separator + app_name + separator;
			
			CachePath  = str + "cache";
			ConfigPath = str + "config";
			TempDir = UserHome + "/Library/Caches/TemporaryItems/";
		}
		
		LockFile = CachePath + separator + "lock";
		NewLink = ConfigPath + separator + "newLink";
	}
	
	public static String StaticPoint = ConfigPath + separator + "static.json";
	public static String SettingPath = ConfigPath + separator + "setting.json";

	public static String downloadsPath 	= ConfigPath + separator + "downloads.json";
	public static String queuesPath		= ConfigPath + separator + "queues.json";
	public static String categoresPath 	= ConfigPath + separator + "categores.json";

	public static String OptDownloadsDir = ConfigPath + separator + "downloads";
	public static String OptQueuesDir 	 = ConfigPath + separator + "queues";
	public static String OptCategoresDir = ConfigPath + separator + "categores";

	public static int NumConnection = 8;

	/* ========================================================================= */
	/* ========================== Resources Methods Init ======================= */
	public static void InitiDirs() {
		File creator = new File(DefaultPath);
		creator.mkdir();
		
		creator = new File(ConfigPath);
		try {
			FileUtils.forceMkdir(creator);
//			Files.setAttribute(creator.toPath(), "dos:hidden", true);
//			Files.setAttribute(creator.getParentFile().toPath(), "dos:hidden", true);
		} catch (Exception e) {
			if(e instanceof IOException){
				System.err.println("Can't make hidden");
			}else{
				System.err.println("Can't creat directory");
			} 
		}
		
		creator = new File(NewLink);
		creator.mkdir();
		
		creator = new File(CachePath);
		try {
			FileUtils.forceMkdir(creator);
//			Files.setAttribute(creator.toPath(), "dos:hidden", true);
//			Files.setAttribute(creator.getParentFile().toPath(), "dos:hidden", true);
		} catch (Exception e) {
			if(e instanceof IOException){
				System.err.println("Can't make hidden");
			}else{
				System.err.println("Can't creat directory");
			} 
		}
		

		creator = new File(OptDownloadsDir);
		creator.mkdir();
		creator = new File(OptQueuesDir);
		creator.mkdir();
		creator = new File(OptCategoresDir);
		creator.mkdir();
		String p = "";
		for (Category c : Category.categores) {
			if (c.getName().equals("Default"))
				continue;
			p = DefaultPath + File.separator + c.getName();
			creator = new File(p);
			creator.mkdir();
		}
		
		
	}

	/* ========================================================================= */
	/* ======================== Resources Methods onSave ======================= */
	public static void ReadSetting() {
		SettingPath = Utils.fromJson(StaticPoint, SettingPath.getClass());
		if(SettingPath == null){
			SettingPath = ConfigPath + separator + "setting.json";
		}
		Setting setting = Utils.fromJson(SettingPath, Setting.class);
		if(setting == null){
			setting = new Setting();
		}
		Setting.setSetting(setting);
		
		
		AriafxMainGUI.isMinimal = setting.isMinimal;
		DefaultPath = setting.paths[0];
		Download.ParallelChunks = setting.ParallelChunks;
		
		//System.out.println(Utils.toJson(setting));
	}

	public static void SaveSetting() {
		Utils.toJsonFile(StaticPoint, SettingPath);
		Utils.toJsonFile(SettingPath, Setting.getSetting());
//		System.out.println(Setting.getSetting());
	}
	

	public static void SaveCategores() {
		Category.saveNewCategories();
		
	}
	
	public static void InitNewCategores() {
		if(Setting.getSetting() != null){
			for (Category cat : Setting.getCategories()) {
				Category.add(cat);
//				Category.addTreeCategory(cat);
			}
		}
	}
	
	public static void ReadCategores() {
		Category[]list  = Utils.fromJson(categoresPath, Category[].class);
		for (Category category : list) {
			Category.getCategores().add(category);
		}
	}
	
	

	public static void ReadDownloads() {
		if(Setting.getSetting() != null){
			for (Item item : Setting.getItems()) {
				Link link = new Link(item);
				link.updateProgressShow();
				DownList.initGUI(link);
				DownManager.IniyDownUi(link);
			}
		}
	}

	public static void SaveDownloads() {
		DownList.saveItemsJson();
	}
	
	public static void InitNewQueue() {
		if(Setting.getSetting() != null){
			for (Queue queue : Setting.getQueues()) {
				 Queue.add(queue);
//				 Queue.addTreeQueue(queue);
			}
		}
	}
	
	public static void ReadQueues() {
		
	}

	public static void LoadTreeItems() {
		Type.categoryList.clear();
		for (Category c : Category.getCategores()) {
			if(!c.equals(Category.Default))
				Type.addTreeCategory(c);
		}
		Type.queueList.clear();
		for (Queue q : Queue.getQueues()) {
			if(!q.equals(Queue.Default))
				Type.addTreeQueue(q);
		}
	}
	
	public static void SaveQueues() {
		Queue.saveNewQueues();
	}

	public static void DeleteTemp() {
		File temp = new File(ConfigPath);
		try {
			delete(temp);
		} catch (IOException e) {
			System.out.println("Can't delete Files");
			e.printStackTrace();
		}
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
//				System.out.println("Delete Directory : "+ file.getAbsolutePath());

				} 
			else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
//					System.out.println("Delete Directory : "+ file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
//			System.out.println("Delete File : " + file.getAbsolutePath());
		}
	}
	
	public static void openInProcess(String str){
		List<String> list = new ArrayList<String>();
		
		if(PlatformUtil.isLinux()){
			list.add("xdg-open");
			list.add(str);
		}else if (PlatformUtil.isWindows()) {
			list.add("start");
			list.add(str);
		}else if (PlatformUtil.isMac()) {
			list.add("open");
			list.add(str);
		}
		ProcessBuilder builder = new ProcessBuilder(list);
		try {
			builder.start();
		} catch (IOException e) {
			R.cout("error in open ");
			R.cout(e.getMessage());
		}
		
	}

	public static void SAVE_CHANGES() {
		DownList.pauseAllDownload();
//		DeleteTemp();
		InitiDirs();
		Setting.updateSetting();
		SaveSetting();
//		SaveDownloads();
		SaveQueues();
		SaveCategores();
		
//		releaseLockFile();
		CloseLodgger();
	}

	public static void releaseLockFile(){
		FileUtils.deleteQuietly(new File(LockFile));
	}
	public static void initFileLock(){
		try {
			File file =  new File(LockFile);
			FileUtils.writeStringToFile(file,  " -- Start App" + System.currentTimeMillis() + "\n" , Charset.defaultCharset() , true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private static FileOutputStream logger = null;
	public static void InitLodgger() {
		if(logger != null){
			return;
		}
		try {
			File logfile = new File(CachePath + separator + "logger.log");
			logger = new FileOutputStream(logfile, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void cout(String strLog) {
		if(logger == null){
			InitLodgger();
		}
		try {
			logger.write(String.valueOf(new Date()+ "\t").getBytes());
			logger.write(strLog.getBytes());
			logger.write(String.valueOf("\n").getBytes());
			logger.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void CloseLodgger() {
		try {
			logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void Save_Changes_Progress() {
		Setting.updateSetting();
		SaveSetting();
	}

	public static void INIT_CHANGES() {
		InitiDirs();
		InitLodgger();
		ReadSetting();
		
		
//		initFileLock();
		
		InitNewCategores();
		InitNewQueue();
//		ReadDownloads();		//need fx thread
		LoadTreeItems();
		
	}

}
