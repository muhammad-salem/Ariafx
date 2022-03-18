package ariafx.tray;

import ariafx.gui.fxml.control.DownUi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;

import java.awt.*;

public class TrayUtility {

    public static Stage stage;
    static TrayIcon tray;
    static ObservableMap<DownUi, MenuItem> list = FXCollections.observableHashMap();

    public static void InitTray() {
        try {
            tray = new TrayIcon();
        } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void addTListTray(DownUi downUi) {
        if (list.get(downUi) != null) {
            return;
        }
        if (tray != null) {
            MenuItem item = tray.addtMenuitem(downUi.getFilename(), (e) -> {
                downUi.show();

            });
            list.put(downUi, item);
        }

    }

    public static void removeFromListTray(DownUi downUi) {
        if (tray != null) {
            MenuItem item = list.remove(downUi);
            tray.removeMenuitemList(item);
        }
    }

    public static void showAll() {
        list.forEach((t, u) -> t.show());
    }

}
