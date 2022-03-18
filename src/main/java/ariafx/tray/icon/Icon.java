package ariafx.tray.icon;

import ariafx.tray.mouse.ActionListenerFX;
import ariafx.tray.mouse.MouseListenerFX;
import ariafx.tray.mouse.MouseMotionListenerFX;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public class Icon {

    static Toolkit toolkit = Toolkit.getDefaultToolkit();
    TrayIcon icon;
    PopupMenu popup;
    Image img;

    public Icon(String title, URL img) {
        setImg(img);
        popup = new PopupMenu();

        icon = new TrayIcon(getImg(), title, popup);
        icon.setImageAutoSize(true);
    }

    public TrayIcon getIcon() {
        return icon;
    }

    public void setIcon(TrayIcon icon) {
        this.icon = icon;
    }

    public Image getImg() {
        return img;
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

    public void addActionListener(ActionListenerFX e) {
        icon.addActionListener(e);
    }

    public void addMouseListener(MouseListenerFX e) {
        icon.addMouseListener(e);
    }

    public void addMouseMotionListener(MouseMotionListenerFX e) {
        icon.addMouseMotionListener(e);
    }

    public void removeActionListener(ActionListenerFX e) {
        icon.removeActionListener(e);
    }

    public void removeMouseListener(MouseListenerFX e) {
        icon.removeMouseListener(e);
    }

    public void removeMouseMotionListener(MouseMotionListenerFX e) {
        icon.removeMouseMotionListener(e);
    }

    public MenuItem addMenuItem(String title, ActionListenerFX listener, int keyEvent) {
        MenuItem item = new MenuItem(title, new MenuShortcut(keyEvent));
        item.addActionListener(listener);
        popup.add(item);
        return item;
    }

    public MenuItem addMenuItem(String title, ActionListenerFX listener) {
        MenuItem item = new MenuItem(title);
        item.addActionListener(listener);
        popup.add(item);
        return item;
    }

    public MenuItem insertMenuItem(int index, String title, ActionListenerFX listener) {
        MenuItem item = new MenuItem(title);
        item.addActionListener(listener);
        popup.add(item);
        popup.insert(item, index);
        return item;
    }

    public MenuItem addMenuItem(String title, ActionListener listener) {
        MenuItem item = new MenuItem(title);
        item.addActionListener(listener);
        popup.add(item);
        return item;
    }

    public MenuItem addMenuItem(MenuItem item, ActionListener listener) {
        item.addActionListener(listener);
        popup.add(item);
        return item;
    }

    public MenuItem addMenuItem(MenuItem item) {
        popup.add(item);
        return item;
    }

    public void addSeparator() {
        popup.addSeparator();
    }

    public void remove(MenuItem item) {
        popup.remove(item);
    }

    public void remove(int index) {
        popup.remove(index);
    }

    public Menu createMenu(String label) {
        Menu menu = new Menu(label);
        return menu;
    }

    public MenuItem addMenuItem(Menu menu, String title, ActionListenerFX listener) {
        MenuItem item = new MenuItem(title);
        item.addActionListener(listener);
        menu.add(item);
        return item;
    }

    public void removeFrom(Menu menu, int index) {
        menu.remove(index);
    }

    public void removeFrom(Menu menu, MenuComponent index) {
        menu.remove(index);
    }


}
