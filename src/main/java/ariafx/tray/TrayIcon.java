package ariafx.tray;

import ariafx.Ariafx;
import ariafx.about.About;
import ariafx.gui.control.Control;
import ariafx.gui.fxml.control.DownUi;
import ariafx.gui.fxml.control.FeatchURL;
import ariafx.tray.icon.Icon;
import ariafx.tray.mouse.ActionListenerFX;
import ariafx.tray.mouse.MouseListenerFX;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class TrayIcon {

    SystemTray tray;

    Icon main;
    Icon list;


    public TrayIcon() throws UnsupportedOperationException {

        tray = SystemTray.getSystemTray();
        if (!SystemTray.isSupported()) return;
        main = new Icon("Ariafx", getClass().getResource("img/aria.png"));
        main.addActionListener((e) -> Ariafx.showUI());
        main.addMouseListener(new MouseListenerFX() {
            @Override
            public void mouseClickedFX(MouseEvent e) {
                Ariafx.showUI();
            }
        });
        // add new link
        main.addMenuItem("New Link", e -> FeatchURL.AddURL(), KeyEvent.VK_N);
        // Change Setting
        main.addMenuItem("Show Ariafx", e -> Ariafx.showUI());
        main.addSeparator();
        // Change Setting
        main.addMenuItem("Change Setting", e -> Control.ShowControlWindow());
        main.addMenuItem("About Arifx", e -> About.showAbout());
        main.addSeparator();
        main.addMenuItem("Exit Ariafx", e -> Ariafx.Exit());
        list = new Icon("Down List", getClass().getResource("img/list.png"));
        list.addMenuItem("Show all", e -> TrayUtility.showAll());
        list.addSeparator();

        try {
            tray.add(main.getIcon());
            tray.add(list.getIcon());
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public MenuItem addDownUiToList(DownUi ui) {
        return list.addMenuItem(ui.getFilename(), e -> ui.show());
    }

    public MenuItem insertMenuitem(int index, String title, ActionListenerFX listener) {

        return list.insertMenuItem(index, title, listener);
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
        list.addMenuItem(item);
        return item;
    }

}
