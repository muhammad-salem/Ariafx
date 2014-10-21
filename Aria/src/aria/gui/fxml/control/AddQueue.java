package aria.gui.fxml.control;


import java.net.URL;
import java.util.ResourceBundle;

import aria.core.url.type.Queue;
import aria.core.url.type.Type;
import aria.gui.fxml.MainFxGet;
import aria.gui.fxml.imp.MovingStage;
import aria.opt.R;
import aria.opt.Setting;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AddQueue {
	
	Stage stage;
    public Stage getStage() {
		return stage;
	}
	public void setStage(Stage stage) {
		this.stage = stage;
		this.stage.setTitle("Add Category");
	}
	

	public AddQueue() {
		
	}
	
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


    
    boolean checkPramter(){
    	if (name.getText().equals("")) {
			return false;
		}
    	return true;
    }
    
    @FXML
    void addNewQueue(ActionEvent event) {
    	if(checkPramter()){
    		int p = Integer.valueOf(parallel.getValue());
    		Queue queue = new Queue(name.getText(), p ,
    				closeAtEnd.isSelected(), shutDownOS.isSelected());
    		Queue.add(queue);
    		R.LoadTreeItems();
    		MainFxGet.queue.getChildren().add(new TreeItem<Type>(queue));
    		Setting.updateSetting();
    		stage.close();
    	}
    }

    
    @FXML
    void minimizePrograme(ActionEvent event) {
    	stage.setIconified(true);
    }

    @FXML
    void cancelAction(ActionEvent event) {
    	stage.close();
    }

    @FXML
    void initialize() {
    	ObservableList<String> list = 
    			FXCollections.observableArrayList("1","2","3","4","5","6","7","8");
    	
    	
    	
        parallel.setItems(list);
        parallel.setValue("1");
        parallel.valueProperty().addListener((obv, old, value)->{
    		try {
        		Integer.valueOf(value);
    		} catch (Exception e) {
    			parallel.setValue("1");
    		}
       });
    }
    
/**-------------------------------- Add queuq -----------------------------------------*/
    
    public static void  addQueue() {
    	addQueue(new Stage(StageStyle.UNDECORATED));
    }
    public static void  addQueue(Stage stage) {
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
}
