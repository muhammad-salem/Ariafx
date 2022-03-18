package ariafx.gui.fxml.control;

import ariafx.core.download.Chunk;
import ariafx.core.download.Link;
import ariafx.core.url.type.ItemStatus;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.gui.fxml.imp.ProgressCircle;
import ariafx.gui.fxml.imp.ProgressStyled;
import ariafx.gui.fxml.imp.ProgressStyledTableCell;
import ariafx.gui.manager.ItemBinding;
import ariafx.tray.TrayUtility;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class DownUi implements Initializable {

    public static URL Fxml;
    /**
     * -----------------------------@FXML--------------------------
     **/
    @FXML
    public AnchorPane anchor;
    @FXML
    public TextField textMaxLimit;
    @FXML
    public Text remining;
    @FXML
    public Label panename, labelTransferRate,
            labelAddress, labelTransferRate2,
            labelStatus, labelDownloaded, labelFileSize,
            labelResume, filename, labelTimeLeft, labelCurrentTime;
    @FXML
    public Text textSaveTo;
    @FXML
    public CheckBox checkUseSpeedLimit,
            showCompleteDialoge, checkExitProgram,
            checkHangUpModem, checkTurnOff,
            checkRememberLimit;
    @FXML
    public ProgressCircle progress;
    @FXML
    public Button selector;
    @FXML
    public LineChart<Integer, Double> chart;
    @FXML
    public NumberAxis yAxis, xAxis;
	
	/*public void setStage(Stage stage) {
		this.stage = stage;
	}*/
    Stage stage;
    Link link;
    int id;
    boolean process_speed = true;  // true_false;
    @FXML
    private HBox addonsBox, displayBox;
    @FXML
    private TableView<Chunk> chunksTable;
    @FXML
    private TableColumn<Chunk, String> chunkStateCode;
    @FXML
    private TableColumn<Chunk, Double> chunkProgress;
    @FXML
    private TableColumn<Chunk, String> chunkSize;
    @FXML
    private TableColumn<Chunk, String> chunkDone;
    @FXML
    private TableColumn<Chunk, Integer> chunkID;
    @FXML
    private ToggleButton subprocess, speed, showInfo;
    @FXML
    private HBox subprogress;
    @FXML
    private ToggleButton toggleState, toggleLimite, toggleFinish;

    public DownUi() {
        this.stage = new Stage(StageStyle.UNDECORATED);
        if (Fxml == null) {
            Fxml = getClass().getResource("DownUi.xml");
        }
    }

    public DownUi(int id, Link link) {
        this();
        this.id = id;
        this.link = link;
        this.stage.setTitle(link.getFilename());
    }

    public Stage getStage() {
        return stage;
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public void setScene(AnchorPane anchorPane) {
        setScene(new Scene(anchorPane));
    }

    public void showAndMove(AnchorPane anchorPane) {
        setScene(anchorPane);
        stage.show();
        MovingStage.pikeToMoving(stage, anchor);
    }

    public void show() {
        stage.show();
        stage.setIconified(false);
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getFilename() {
        return link.getFilename();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progress.setDoneText("Download");
        link.runningProperty().addListener((obv, old, value) -> {
            if (!link.isInitState()) {
                if (value || link.getState() == State.CANCELLED) {
                    show();
                } else {
                    cancel(null);
                }
            }
        });

        link.downStateProperty().addListener((obv, old, value) -> {
            if (!link.isInitState()) {
                if (value == ItemStatus.DOWNLOADING) {
                    updateProcessTable();
                    show();
                    TrayUtility.addTListTray(this);

                } else if (value == ItemStatus.COMPLETE) {
                    TrayUtility.removeFromListTray(this);
                } else {
                    TrayUtility.removeFromListTray(this);
                }
            }

        });

        subprocess.setOnAction((e) -> {
            addonsBox.setLayoutX(0);
            link.stopCollectSpeedData();
            stage.setHeight(450);
            process_speed = true;
        });

        speed.setOnAction((e) -> {
            addonsBox.setLayoutX(-540);
            link.canCollectSpeedData();
            stage.setHeight(450);
            process_speed = false;
        });

        showInfo.setOnAction((e) -> {
            if (showInfo.getText().equals("<<")) {    // hide
                stage.setHeight(240);
                showInfo.setText(">>");
                clearProcessTable();
                link.stopCollectSpeedData();
            } else {                                // show
                stage.setHeight(450);
                showInfo.setText("<<");
                if (process_speed) {
                    updateProcessTable();
                } else {
                    link.canCollectSpeedData();
                }
            }

        });

        toggleState.setOnAction((e) -> {
            displayBox.setLayoutX(0);
        });

        toggleLimite.setOnAction((e) -> {
            displayBox.setLayoutX(-540);
        });

        toggleFinish.setOnAction((e) -> {
            displayBox.setLayoutX(-1080);
        });


        initTable();

        ItemBinding.bindItem(link, this);
        MovingStage.pikeToMoving(stage, anchor);

        labelCurrentTime.textProperty().bind(link.currentTimeProperty());
    }

    void initTable() {
        chunkDone.setCellValueFactory(new PropertyValueFactory<Chunk, String>("done"));
        chunkSize.setCellValueFactory(new PropertyValueFactory<Chunk, String>("size"));
        chunkStateCode.setCellValueFactory(new PropertyValueFactory<Chunk, String>("stateCode"));
        chunkID.setCellValueFactory(new PropertyValueFactory<Chunk, Integer>("id"));
        chunkProgress.setCellValueFactory(new PropertyValueFactory<Chunk, Double>("progress"));
        chunkProgress.setCellFactory(ProgressStyledTableCell.<Chunk>forTableColumn());
        chunksTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    private void clearProcessTable() {
        chunksTable.getItems().clear();
        subprogress.getChildren().clear();
    }

    private void updateProcessTable() {
        clearProcessTable();
//		if(!process_speed) return;
        Chunk[] chunks = link.getChunks();
        if (chunks == null) {
            try {
                Thread.sleep(500);
                chunks = link.getChunks();
            } catch (Exception e) {
            }
        }
        if (!(chunks == null)) {
            chunksTable.getItems().addAll(chunks);
            for (Chunk chunk : chunks) {
                ProgressBar progress = ProgressStyled.CreateProgressFlat();
                progress.progressProperty().bind(chunk.progressProperty());
                progress.setPrefHeight(subprogress.getPrefHeight());
                SimpleDoubleProperty testDoneSize = new SimpleDoubleProperty(1);
                testDoneSize.bind(chunk.progressProperty().multiply(chunk.getRange()[1] - chunk.getRange()[0]).divide(link.getLength()).multiply(subprocess.getWidth()));
                progress.minWidthProperty().bind(testDoneSize);
                subprogress.getChildren().add(progress);
            }
        }
    }

    @FXML
    void useSpeedLimter(ActionEvent event) {

    }

    @FXML
    void rememberSpeedLimter(ActionEvent event) {

    }

    @FXML
    void showDownloadCompleteDialoge(ActionEvent event) {

    }

    @FXML
    void hangUpModem(ActionEvent event) {

    }

    @FXML
    void closeProgramAfterDone(ActionEvent event) {

    }

    @FXML
    void shutDownOS(ActionEvent event) {

    }

    @FXML
    void minimize(ActionEvent event) {
        stage.setIconified(true);
    }

    @FXML
    void hideToTray(ActionEvent event) {
        stage.hide();
        TrayUtility.addTListTray(this);
    }

    @FXML
    void cancel(ActionEvent event) {
        if (link.isRunning()) {
            if (link.getDownState() != ItemStatus.COMPLETE)
                link.cancel();
        }
        link.stopCollectSpeedData();
        stage.close();

        TrayUtility.removeFromListTray(this);
    }

    @FXML
    void checkLink(ActionEvent event) {
        Button button = (Button) event.getSource();
        if (link.isRunning()) {
            link.cancel();
            button.setText("Resume");
        } else {
            link.restart();
            button.setText("Pause");
        }
    }


}

