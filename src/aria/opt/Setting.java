/**
 * 
 */
package aria.opt;

import java.util.Arrays;

import aria.core.url.Item;
import aria.core.url.type.Category;
import aria.core.url.type.Queue;
import aria.gui.fxml.AriafxMainGUI;
import aria.gui.manager.DownList;

/**
 * @author salem
 *
 */
public class Setting {

	/**
	 * save the default setting her
	 */
	

	double time_to_save_in_minutes  = 2;
	boolean isMinimal;
	String[] paths;
	Category[] categories;
	Queue[] queues;
	Item[] items;
	
	private static Setting setting;


	public Setting() {
		paths = R.getPaths();
		categories = Category.getNewCatArray();
		queues = Queue.getNewQueues();
		items = DownList.getItems();
		time_to_save_in_minutes = 2.0;
		isMinimal = AriafxMainGUI.isMinimal;
	}
	
	
	@Override
	public String toString() {
		String str  = Arrays.toString(paths)+ "\n";
			   str += Arrays.toString(categories)+ "\n";
//			   str += Arrays.toString(queues)+ "\n";
			   str += Arrays.toString(items)+ "\n";
		return str;
	}


	public static Setting getSetting() {
		return setting;
	}
	
	public static void updateSetting(){
		double t = setting.time_to_save_in_minutes;
		setting = new Setting();
		setting.time_to_save_in_minutes = t;
	}

	public static void setSetting(Setting setting) {
		Setting.setting = setting;
		if(setting == null) Setting.setting = new Setting();
	}
	
	public static String[] getPaths() {
		return getSetting().paths;
	}

	public static Category[] getCategories() {
		return getSetting().categories;
	}

	public static Queue[] getQueues() {
		return getSetting().queues;
	}

	public static Item[] getItems() {
		return getSetting().items;
	}
	
	public static double getTime_to_save() {
		
		return getSetting().time_to_save_in_minutes;
	}
	
	public static void setTime_to_save(double t) {
		getSetting().time_to_save_in_minutes = t;
	}
	
	public static boolean getMinimal() {
		
		return getSetting().isMinimal;
	}
	
	public static void setMinimal(boolean v) {
		getSetting().isMinimal = v;
	}


	public static String getDefaultPath() {
		return Setting.getPaths()[0];
	}
	
}
