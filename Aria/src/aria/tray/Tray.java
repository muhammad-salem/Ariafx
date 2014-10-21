package aria.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

import javax.swing.UIManager;

import aria.Main;


public class Tray {

	SystemTray tray;
	TrayIcon main;
	TrayIcon list;
	PopupMenu popupMain;
	PopupMenu popupList;
	
	Image mainImg;
	Image listImg;
	public Tray() {
		
		
		tray = SystemTray.getSystemTray();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		if(!SystemTray.isSupported()) return;
		
		popupMain = new PopupMenu("Tray Main");
		
		
		mainImg = toolkit.getImage(Main.class.getResource("./gui/fxml/res/24/view-list-compact-symbolic.png"));
		main = new TrayIcon(mainImg,"fxGet",popupMain);
		
		popupList = new PopupMenu("Tray List");
		MenuItem item = new MenuItem("Show all");
		item.addActionListener((l)->{
			Platform.exit();
			System.exit(0);
		});
		
		popupList.add(item);
		
		
		
		listImg = toolkit.getImage(Main.class.getResource("./gui/fxml/res/24/view-list-details-symbolic.png"));
		list = new TrayIcon(listImg,"list", popupList);
		
		System.out.println(main.getSize().toString());
		
		try {
			tray.add(main);
			tray.add(list);
		} catch (AWTException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
					"can not add system tray", e);
		}
		
		System.out.println("Tray.Tray()");
		try {
			System.out.println("Tray.Tray()");
			UIManager.setLookAndFeel(UIManager.getLookAndFeel());
			System.out.println("Tray.Tray()");
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		
	}

}
