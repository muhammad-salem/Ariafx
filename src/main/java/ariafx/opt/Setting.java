/**
 * 
 */
package ariafx.opt;

import ariafx.core.download.Download;
import ariafx.core.download.ProxySetting;
import ariafx.core.download.ProxySetting.ProxyConfig;
import ariafx.core.download.ProxySetting.ProxyType;
import ariafx.core.url.Item;
import ariafx.core.url.type.Category;
import ariafx.core.url.type.Queue;
import ariafx.gui.fxml.AriafxMainGUI;
import ariafx.gui.manager.DownList;

import java.util.Arrays;

/**
 * @author salem
 *
 */
public class Setting {

	/**
	 * save the default setting her
	 */
	double time_to_save_in_minutes  = 1;
	boolean isMinimal;
	int ParallelChunks = Download.ParallelChunks;
	String[] paths;
	Category[] categories;
	Queue[] queues;
	ProxySetting proxySetting;

	Item[] items;
	
	private static Setting setting;

	public Setting() {
		paths = R.getPaths();
		categories = Category.getNewCatArray();
		queues = Queue.getNewQueues();
		items = DownList.getItems();
		time_to_save_in_minutes = 1.0;
		isMinimal = AriafxMainGUI.isMinimal;
		ParallelChunks = Download.ParallelChunks;
		proxySetting = ProxySetting.createDefalutProxySetting();
	}

	@Override
	public String toString() {
		String str  = Arrays.toString(paths)+ "\n";
			   str += Arrays.toString(categories)+ "\n";
			   str += Arrays.toString(items)+ "\n";
		return str;
	}


	public static Setting getSetting() {
		return setting;
	}
	
	public static void updateSetting(){
		double t = setting.time_to_save_in_minutes;
		ProxySetting ps = getProxySetting();
		setting = new Setting();
		setting.time_to_save_in_minutes = t;
		setProxySetting(ps);
	}

	public static void setSetting(Setting setting) {
		Setting.setting = setting;
		if(setting == null) 
			Setting.setting = new Setting();
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
	
	public static int getParallelChunks() {
		return getSetting().ParallelChunks;
	}
	
	public static void settParallelChunks(int num) {
		getSetting().ParallelChunks = num;
	}


	public static ProxySetting getProxySetting() {
		return getSetting().proxySetting;
	}


	public static void setProxySetting(ProxySetting proxySetting) {
		getSetting().proxySetting = proxySetting;
	}
	
	public static String GetRemoteAddress() {
		return getProxySetting().getRemoteAddress();
	}
	public static void SetRemoteAddress(String remoteAddress) {
		getProxySetting().setRemoteAddress(remoteAddress);
	}
	public static int GetRemotePort() {
		return getProxySetting().getRemotePort();
	}
	public static void SetRemotePort(int remotePort) {
		getProxySetting().setRemotePort(remotePort);
	}
	
	public static ProxyType GetProxyType() {
		return getProxySetting().getProxyType();
	}
	public static void SetProxyType(ProxyType proxyType) {
		getProxySetting().setProxyType(proxyType);
	}
	
	public static ProxyConfig GetProxyConfig() {
		return getProxySetting().getProxyConfig();
	}
	public static void SetProxyConfig(ProxyConfig proxyConfig) {
		getProxySetting().setProxyConfig(proxyConfig);
	}
	
	public static void USE_No_Proxy() {
		getProxySetting().setProxyConfig(ProxyConfig.NoProxy); 
	}
	
	public static void USE_System_Proxy() {
		getProxySetting().setProxyConfig(ProxyConfig.SystemProxy);
	}
	
	
	public static void Use_Manual_Proxy_Settings(String remoteAddress, String remotePort) {
		getProxySetting().setProxyConfig(ProxyConfig.ManualProxy);
		getProxySetting().setProxyType(ProxyType.HTTP);
		SetRemoteAddress(remoteAddress);
		SetRemotePort(Integer.parseInt(remotePort));
	}
	
	public static void Use_Manual_Proxy_Settings(String remoteAddress, int remotePort, ProxyType proxyType) {
		getProxySetting().setProxyConfig(ProxyConfig.ManualProxy);
		getProxySetting().setProxyType(proxyType);
		SetRemoteAddress(remoteAddress);
		SetRemotePort(remotePort);
	}
	
	public static void Use_AutoConfiguration_Proxy(String url) {
		getProxySetting().setProxyConfig(ProxyConfig.AutoConfigProxy);
		getProxySetting().setUrlAutoConfig(url);
	}
	
}
