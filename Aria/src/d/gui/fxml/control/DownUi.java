package d.gui.fxml.control;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import d.core.download.Link;
import d.gui.fxml.imp.MovingStage;
import d.gui.fxml.imp.ProgressCircle;
import d.gui.manager.ItemBinding;

public class DownUi implements Initializable {

	Stage stage;
	Link link;
	int id;
	
	public static URL Fxml;
	
	public DownUi() {
		this.stage = new Stage(StageStyle.UNDECORATED);
		if(Fxml == null)
			Fxml = getClass().getResource("DownUi.xml");
	}
	
	public DownUi(int id , Link link) {
		this();
		this.id  = id;
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
		MovingStage.pikeToMoving(stage, anchor);
	}
	
	/*public void setStage(Stage stage) {
		this.stage = stage;
	}*/

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		link.runningProperty().addListener((obv, old, value)->{
			if(!link.isInitState()){
				if(value || link.getState() == State.CANCELLED ){
					show();
				}else{
					cancel(null);
				}
			}
			
		});
		
		graph.selectedProperty().addListener((obv, pld, value)->{
			if(value){
//				anchor.setPrefHeight(415);
				stage.setHeight(415);
				link.canCollectSpeedData();
			}else{
//				anchor.setPrefHeight(245);
				stage.setHeight(245);
				link.stopCollectSpeedData();
			}
		});
		
		
		ItemBinding.bindItem(link, this);
	}

	/**-----------------------------@FXML--------------------------**/
	@FXML
	public AnchorPane anchor;

    @FXML
    public TextField textMaxLimit;


    @FXML
    public Label panename, labelTransferRate, 
    	labelAddress, labelTransferRate2, labelSaveTo,
    	labelStatus, labelDownloaded, labelFileSize, 
    	labelResume, filename, labelTimeLeft;

    @FXML
    public CheckBox checkUseSpeedLimit, graph, 
    		showCompleteDialoge, checkExitProgram, 
    		checkHangUpModem, checkTurnOff,
    		checkRememberLimit;


    @FXML
    public ProgressCircle progress;

    @FXML
    public Button selector;


    @FXML
    public  LineChart<Integer, Double> chart;
    @FXML
    public NumberAxis yAxis, xAxis;

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
    void CloseProgrameAfterDone(ActionEvent event) {

    }

    @FXML
    void shutDownOS(ActionEvent event) {
    	
    }

    @FXML
    void minimize(ActionEvent event) {
    	stage.setIconified(true);
    }

    @FXML
    void cancel(ActionEvent event) {
    	if(link.isRunning()){
    		link.cancel();
    	}
    	stage.close();
    }

    @FXML
    void checkLink(ActionEvent event) {
    	if(link.isRunning()){
    		link.cancel();
    	}else{
    		link.restart();
    	}
    }

	
    
    
    

}

