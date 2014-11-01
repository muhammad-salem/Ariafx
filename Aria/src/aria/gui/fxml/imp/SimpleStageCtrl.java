package aria.gui.fxml.imp;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SimpleStageCtrl extends StageCtrl{
	
	Stage stage;
	public SimpleStageCtrl(Stage stage){
		this.stage = stage;
		this.stage.initStyle(StageStyle.UNDECORATED);
		if(FXML == null){
			FXML = getClass().getResource("StageCtrl.xml");
		}
		
//		stage.setOnCloseRequest(( event) -> {
//			
//			Setting setting = new Setting();
//			Utils.toJsonFile(R.SettingPath, setting);
//			
//            System.out.println(event.getEventType().getName());
////            R.SAVE_CHANGES();
//            System.out.println("Closed");
//        });
	}

	@Override
	public void setMinimize(ActionEvent event) {
		stage.setIconified(true);
	}

	@Override
	public void setMaximize(ActionEvent event) {
		if (stage.isMaximized()) {
			stage.setMaximized(false);
			maxmizeButton.setId("maximize");
		} else {
			stage.setMaximized(true);
			maxmizeButton.setId("restore");
		}
	}

	@Override
	public void setFullSCreen(ActionEvent event) {
		if (stage.isFullScreen()) {
			stage.setFullScreen(false);
		} else {
			stage.setFullScreen(true);
		}
	}

	@Override
	public void close(ActionEvent event) {
//		Setting setting = Setting.getInstance();
//		Utils.toJsonFile(R.SettingPath, setting);
//      System.out.println("Closed");
		stage.close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
}