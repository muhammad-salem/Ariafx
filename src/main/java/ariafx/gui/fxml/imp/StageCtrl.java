package ariafx.gui.fxml.imp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class StageCtrl implements Initializable {

    public static URL FXML;


    @FXML
    public Button minimizeButton, maximizeButton, fullScreenButton, closeButton;


    @FXML
    public HBox hBoxStgCtrl;

    @FXML
    public abstract void setMinimize(ActionEvent event);

    @FXML
    public abstract void setFullScreen(ActionEvent event);

    @FXML
    public abstract void setMaximize(ActionEvent event);

    @FXML
    public abstract void close(ActionEvent event);

    @Override
    public abstract void initialize(URL location, ResourceBundle resources);

    /**
     * remove maximizeButton and fullScreenButton
     * called after implementation of initialize(URL location, ResourceBundle resources)
     *
     * @param style allowed StageStyle.UTILITY StageStyle.UNIFIED
     */
    public void setStageStyle(StageStyle style) {
        if (style.equals(StageStyle.UTILITY)) {
            hBoxStgCtrl.getChildren().removeAll(fullScreenButton, maximizeButton);
        } else if (style.equals(StageStyle.UNIFIED)) {
            hBoxStgCtrl.getChildren().remove(fullScreenButton);
        }
    }


}

