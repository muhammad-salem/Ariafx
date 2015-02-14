package aria.tray;

import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import aria.Ariafx;
import aria.about.About;
import aria.gui.control.Control;
import aria.gui.fxml.control.DownUi;
import aria.gui.fxml.control.FeatchURL;
import aria.tray.icon.Icon;
import aria.tray.mouse.ActionListenerFX;
import aria.tray.mouse.MouseListenerFX;


public class TrayIcon {

	SystemTray tray;
	
	Icon main;
	Icon list;
	
	
	public TrayIcon() {
		
		
		tray = SystemTray.getSystemTray();
		
		if(!SystemTray.isSupported()) return;
		
		main = new Icon("Ariafx", getClass().getResource("img/aria.png"));
		main.addActionListener((e)->{
			Ariafx.showUI();
		});
		
		main.addMouseListener(new MouseListenerFX() {
			
			@Override
			public void mouseReleasedFX(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressedFX(MouseEvent e) {
				
			}
			
			@Override
			public void mouseExitedFX(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEnteredFX(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClickedFX(MouseEvent e) {
				Ariafx.showUI();
			}
		});
		
		// add new link
		ActionListenerFX newLinkListener = new ActionListenerFX() {
			@Override
			public void actionPerformedFX(ActionEvent e) {
				FeatchURL.AddURL();
			}
		};
		main.addMenuitem("New Link", newLinkListener, KeyEvent.VK_N);
		
		// Change Setting
		ActionListenerFX showUI = new ActionListenerFX() {
			@Override
			public void actionPerformedFX(ActionEvent e) {
				Ariafx.showUI();
			}
		};
		main.addMenuitem("Show Ariafx", showUI);
		main.addSeparator();
		
		// Change Setting
		ActionListenerFX changeSetting = new ActionListenerFX() {
			@Override
			public void actionPerformedFX(ActionEvent e) {
				Control.ShowControlWindow();
			}
		};
		main.addMenuitem("Change Setting", changeSetting);
		
		ActionListenerFX aboutListener = new ActionListenerFX() {
			@Override
			public void actionPerformedFX(ActionEvent e) {
				About.showAbout();
			}
		};
		main.addMenuitem("About Arifx", aboutListener);
		main.addSeparator();
		
		ActionListenerFX closListener = new ActionListenerFX() {
			@Override
			public void actionPerformedFX(ActionEvent e) {
				Ariafx.Exit();
			}
		};
		main.addMenuitem("Exit Ariafx", closListener);
		
		list = new Icon("Down List", getClass().getResource("img/list.png"));
		ActionListenerFX showAll = new ActionListenerFX() {
			@Override
			public void actionPerformedFX(ActionEvent e) {
				TrayUtile.showAll();
			}
		};
		list.addMenuitem("Show all", showAll);
		list.addSeparator();
		
		try {
			tray.add(main.getIcon());
			tray.add(list.getIcon());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public MenuItem addDownUiToList(DownUi ui) {
		ActionListenerFX listener = new ActionListenerFX() {
			@Override
			public void actionPerformedFX(ActionEvent e) {
				ui.show();
			}
		};
		return list.addMenuitem(ui.getFilename(), listener);
	}
	
	public MenuItem insertMenuitem(int index, String title, ActionListenerFX listener) {
		
		return list.insertMenuitem(index, title, listener);
	}
	
	public void removeMenuitemList(int index) {
		list.remove(index);
	}
	
	public void removeMenuitemList(MenuItem item) {
		list.remove(item);
	}

	public MenuItem addtMenuitem(String title, ActionListenerFX listener) {
		MenuItem item = new MenuItem(title);
		item.addActionListener(listener);
		list.addMenuitem(item);
		return item;
	}
	
//	public static void main(String[] args) {
//		new TrayIcon();
//	}

}
