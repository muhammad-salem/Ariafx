package aria.gui.fxml.imp;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;

public abstract class StageCtrl implements Initializable{

	public static URL FXML ;
    

	@FXML
	public Button maxmizeButton, 
    		   minmizeButton,
    		   closeButton,
    		   fullScreenButton;
	
    
    @FXML
    public HBox hBoxStgCtrl;

    @FXML
    public abstract void  setMinimize(ActionEvent event);

    @FXML
    public abstract void setFullSCreen(ActionEvent event);

    @FXML
    public abstract void setMaximize(ActionEvent event);

    @FXML
    public abstract void close(ActionEvent event);

	@Override
	public abstract void initialize(URL location, ResourceBundle resources);
	
	/**
	 * remove maxmizeButton and fullScreenButton
	 * called after implementation of initialize(URL location, ResourceBundle resources) 
	 * @param style allowed StageStyle.UTILITY StageStyle.UNIFIED
	 */
	public void setStageStyle(StageStyle style){
		if(style.equals(StageStyle.UTILITY)){
			hBoxStgCtrl.getChildren().removeAll(fullScreenButton, maxmizeButton);
		} else if(style.equals(StageStyle.UNIFIED)){
			hBoxStgCtrl.getChildren().remove(fullScreenButton);
		} 
	}
	
	
	
}

