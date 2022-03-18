package ariafx.gui.fxml.control;


import ariafx.core.url.type.Queue;
import ariafx.core.url.type.Type;
import ariafx.gui.fxml.AriafxMainGUI;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.opt.R;
import ariafx.opt.Setting;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class AddQueue {

    Stage stage;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private DatePicker datePickerStartTime;
    @FXML
    private DatePicker datePickerStopTime;
    @FXML
    private CheckBox closeAtEnd, shutDownOS;
    @FXML
    private TextField name;
    @FXML
    private ComboBox<String> parallel;

    public AddQueue() {

    }

    /**
     * -------------------------------- Add queuq -----------------------------------------
     */

    public static void addQueue() {
        addQueue(new Stage(StageStyle.UNDECORATED));
    }

    public static void addQueue(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(AddQueue.class.getResource("addQueue.xml"));
            AnchorPane anchorPane = loader.load();
            AddQueue queue = loader.getController();
            stage.setScene(new Scene(anchorPane));
            stage.show();
            queue.setStage(stage);
            MovingStage.pikeToMoving(stage, anchorPane);
        } catch (Exception e) {

        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Add Category");
    }

    boolean checkParameter() {
        if (name.getText().equals("")) {
            return false;
        }
        return true;
    }

    @FXML
    void addNewQueue(ActionEvent event) {
        if (checkParameter()) {
            int p = Integer.valueOf(parallel.getValue());
            Queue queue = new Queue(name.getText(), p,
                    closeAtEnd.isSelected(), shutDownOS.isSelected());
            Queue.add(queue);
            R.LoadTreeItems();
            AriafxMainGUI.queue.getChildren().add(new TreeItem<Type>(queue));
            Setting.updateSetting();
            stage.close();
        }
    }

    @FXML
    void minimizeProgram(ActionEvent event) {
        stage.setIconified(true);
    }

    @FXML
    void cancelAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    void initialize() {
        ObservableList<String> list =
                FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8");


        parallel.setItems(list);
        parallel.setValue("1");
        parallel.valueProperty().addListener((obv, old, value) -> {
            try {
                Integer.valueOf(value);
            } catch (Exception e) {
                parallel.setValue("1");
            }
        });
    }
}
