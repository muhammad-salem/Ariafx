package aria.gui.fxml.control;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import aria.core.download.Chunk;
import aria.core.download.Link;
import aria.gui.fxml.imp.MovingStage;
import aria.gui.fxml.imp.ProgressCircle;
import aria.gui.manager.ItemBinding;

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
//		MovingStage.pikeToMoving(stage, anchor);
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
		progress.setDoneText("Download");
		
		link.runningProperty().addListener((obv, old, value)->{
			if(!link.isInitState()){
				if(value || link.getState() == State.CANCELLED ){
					show();
				}else{
					cancel(null);
				}
			}
//			else if( link.canStartCopy()){
//				if(value || link.getState() == State.CANCELLED ){
//					show();
//				}else{
//					cancel(null);
//				}
//			}
			
		});
		
		ToggleGroup toggle = new ToggleGroup();
		graph.setToggleGroup(toggle);
		chunksButton.setToggleGroup(toggle);
		
		chunksButton.selectedProperty().addListener((obv, pld, value)->{
			if(value){
				addonsBox.setLayoutX(0);
//				chunksTable.getItems().clear();
//				chunksTable.getItems().addAll(link.getChunks());
			}
			chunksTable.getItems().clear();
			chunksTable.getItems().addAll(link.getChunks());
		});
		
		graph.selectedProperty().addListener((obv, pld, value)->{
			if(value){
				addonsBox.setLayoutX(-540);
				link.canCollectSpeedData();
			}
			else{
				link.stopCollectSpeedData();
			}
		});
		
		
		initTable();
		//chunksTable.getItems().addAll(link.getChunks());
		
		ItemBinding.bindItem(link, this);
		MovingStage.pikeToMoving(stage, anchor);
	}
	
	void initTable(){
		chunkDone.setCellValueFactory(new PropertyValueFactory<Chunk, String>( "done"));
		chunkSize.setCellValueFactory(new PropertyValueFactory<Chunk, String>( "size"));
		chunkStateCode.setCellValueFactory(new PropertyValueFactory<Chunk, String>( "stateCode"));
		chunkID.setCellValueFactory(new PropertyValueFactory<Chunk, Integer>( "id"));
		chunkProgress.setCellValueFactory(new PropertyValueFactory<Chunk, Double>( "progress"));
		
		chunkProgress.setCellFactory( ProgressBarTableCell.<Chunk> forTableColumn());
		
		chunksTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
	}

	/**-----------------------------@FXML--------------------------**/
	@FXML
	public AnchorPane anchor;

    @FXML
    public TextField textMaxLimit;
    
    @FXML
    private HBox addonsBox;
    
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
    private RadioButton graph, chunksButton;
    
    @FXML
    public Label panename, labelTransferRate, 
    	labelAddress, labelTransferRate2, labelSaveTo,
    	labelStatus, labelDownloaded, labelFileSize, 
    	labelResume, filename, labelTimeLeft;

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
    	link.stopCollectSpeedData();
    	stage.close();
    }

    @FXML
    void checkLink(ActionEvent event) {
    	Button button = (Button) event.getSource();
    	if(link.isRunning()){
    		link.cancel();
    		button.setText("Start");
    	}else{
    		link.restart();
    		button.setText("Pause");
    	}
    }

	
    
    
    

}

