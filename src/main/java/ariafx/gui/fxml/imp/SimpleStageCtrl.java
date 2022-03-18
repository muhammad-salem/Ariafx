package ariafx.gui.fxml.imp;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class SimpleStageCtrl extends StageCtrl {

    Stage stage;

    public SimpleStageCtrl(Stage stage) {
        this.stage = stage;
        this.stage.initStyle(StageStyle.UNDECORATED);
        if (FXML == null) {
            FXML = getClass().getResource("StageCtrl.xml");
        }
    }

    @Override
    public void setMinimize(ActionEvent event) {
        stage.setIconified(true);
    }

    @Override
    public void setMaximize(ActionEvent event) {
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            maximizeButton.setId("maximize");
        } else {
            stage.setMaximized(true);
            maximizeButton.setId("restore");
        }
    }

    @Override
    public void setFullScreen(ActionEvent event) {
		stage.setFullScreen(!stage.isFullScreen());
    }

    @Override
    public void close(ActionEvent event) {
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}