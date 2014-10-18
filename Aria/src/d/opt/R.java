package d.opt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

import com.sun.javafx.PlatformUtil;

import d.core.download.Link;
import d.core.url.Item;
import d.core.url.type.Category;
import d.core.url.type.Queue;
import d.core.url.type.Type;
import d.gui.manager.DownList;
import d.gui.manager.DownManager;

public class R {
	
	public static String[] getPaths(){
		return new String[]{DefaultPath, CachePath, ConfigPath, OptDownloadsDir,
				OptQueuesDir, OptCategoresDir,
				downloadsPath, queuesPath, categoresPath};
	}

	public static String separator = File.separator;
	public static String app_name = "aria";
	public static String UserHome = System.getProperty("user.home");
	public static String DefaultPath = UserHome + separator + "Downloads" 
						+ separator + app_name;
	
	public static String CachePath = UserHome + separator 
						+ ".cache" + separator + app_name;
	
	public static String ConfigPath = UserHome + separator 
						+ ".config" + separator + app_name;
	static{
		if(PlatformUtil.isWin7OrLater()){
			CachePath  = UserHome +  separator + "AppData" 
					+ separator + "Roaming" + separator + app_name + separator + "cache";
			ConfigPath = UserHome + separator + "AppData" 
					+ separator + "Roaming" + separator + app_name + separator + "config";
		} else if(PlatformUtil.isWindows()){
			DefaultPath = UserHome + separator + "Documents" + separator +  "Downloads" 
					+ separator + app_name;
			CachePath  = UserHome +  separator + "Application Data" 
					+ separator + app_name + separator + "cache";
			ConfigPath = UserHome + separator + "Application Data" 
					+ separator + app_name + separator + "config";
		}
	}
	
	public static String SettingPath = ConfigPath + separator+ "setting.json";

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
			Files.setAttribute(creator.toPath(), "dos:hidden", true);
			Files.setAttribute(creator.getParentFile().toPath(), "dos:hidden", true);
		} catch (Exception e) {
			if(e instanceof IOException){
				System.err.println("Can't make hidden");
			}else{
				System.err.println("Can't creat directory");
			} 
		}
		
		creator = new File(CachePath);
		try {
			FileUtils.forceMkdir(creator);
			Files.setAttribute(creator.toPath(), "dos:hidden", true);
			Files.setAttribute(creator.getParentFile().toPath(), "dos:hidden", true);
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
		Setting setting = Utils.fromJson(SettingPath, Setting.class);
		Setting.setSetting(setting);
	}

	public static void SaveSetting() {
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
		Category.categoryList.clear();
		for (Category c : Category.getCategores()) {
			if(!c.equals(Category.Default))
				Type.addTreeCategory(c);
		}
		Queue.queueList.clear();
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

	public static void SAVE_CHANGES() {
		DownList.pauseAllDownload();
//		DeleteTemp();
		InitiDirs();
		Setting.updateSetting();
		SaveSetting();
		SaveDownloads();
		SaveQueues();
		SaveCategores();
	}

	public static void Save_Changes_Progress() {
		Setting.updateSetting();
		SaveSetting();
	}

	public static void INIT_CHANGES() {
		InitiDirs();
		ReadSetting();
		InitNewCategores();
		InitNewQueue();
//		ReadDownloads();		//need fx thread
		LoadTreeItems();
		
	}

}
