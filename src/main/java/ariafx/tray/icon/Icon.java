package ariafx.tray.icon;

import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.net.URL;

import ariafx.tray.mouse.ActionListenerFX;
import ariafx.tray.mouse.MouseListenerFX;
import ariafx.tray.mouse.MouseMotionListenerFX;

public class Icon {

	TrayIcon icon;
	PopupMenu popup;
	Image img;
	static Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	public Icon(String title, URL img) {
		setImg(img);
		popup = new PopupMenu();
		
		icon = new TrayIcon(getImg(), title, popup);
		icon.setImageAutoSize(true);
	}
	
	public void setIcon(TrayIcon icon) {
		this.icon = icon;
	}
	public TrayIcon getIcon() {
		return icon;
	}
	public void setImg(String img) {
		this.img = toolkit.getImage(img);
	}
	public void setImg(URL url) {
		this.img = toolkit.getImage(url);
	}
	public void setImg(Image img) {
		this.img = img;
	}
	public Image getImg() {
		return img;
	}
	
	public void addActionListener(ActionListenerFX e){
		icon.addActionListener(e);
	}
	public void addMouseListener(MouseListenerFX e){
		icon.addMouseListener(e);
	}
	public void addMouseMotionListener(MouseMotionListenerFX e){
		icon.addMouseMotionListener(e);
	}
	
	public void removeActionListener(ActionListenerFX e){
		icon.removeActionListener(e);
	}
	public void removeMouseListener(MouseListenerFX e){
		icon.removeMouseListener(e);
	}
	public void removeMouseMotionListener(MouseMotionListenerFX e){
		icon.removeMouseMotionListener(e);
	}
	
	public MenuItem addMenuitem(String title, ActionListenerFX listener, int keyEvent ){
		MenuItem item = new MenuItem(title, new MenuShortcut(keyEvent));
		item.addActionListener(listener);
		popup.add(item);
		return item;
	}
	
	public MenuItem addMenuitem(String title, ActionListenerFX listener ){
		MenuItem item = new MenuItem(title);
		item.addActionListener(listener);
		popup.add(item);
		return item;
	}
	
	public MenuItem insertMenuitem(int index, String title, ActionListenerFX listener ){
		MenuItem item = new MenuItem(title);
		item.addActionListener(listener);
		popup.add(item);
		popup.insert(item, index);
		return item;
	}
	
	public MenuItem addMenuitem(String title, ActionListener listener ){
		MenuItem item = new MenuItem(title);
		item.addActionListener(listener);
		popup.add(item);
		return item;
	}
	
	public MenuItem addMenuitem(MenuItem item, ActionListener listener ){
		item.addActionListener(listener);
		popup.add(item);
		return item;
	}
	
	public MenuItem addMenuitem(MenuItem item){
		popup.add(item);
		return item;
	}
	
	public void addSeparator(){
		popup.addSeparator();
	}
	public void remove(MenuItem item) {
		popup.remove(item);
	}
	
	public void remove(int index) {
		 popup.remove(index);
	}
	
	public Menu createMenu(String label){
		Menu menu = new Menu(label);
		return menu;
	}
	
	public MenuItem addMenuitem(Menu menu, String title, ActionListenerFX listener ){
		MenuItem item = new MenuItem(title);
		item.addActionListener(listener);
		menu.add(item);
		return item;
	}
	
	public void removeFrom(Menu menu, int index ){
		menu.remove(index);
	}
	
	public void removeFrom(Menu menu, MenuComponent  index ){
		menu.remove(index);
	}


	
	
	
}
