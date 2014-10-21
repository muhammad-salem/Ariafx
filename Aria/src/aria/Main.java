package aria;

import aria.about.About;
import aria.core.download.Link;
import aria.gui.fxml.MainFxGet;
import aria.gui.fxml.imp.MovingStage;
import aria.gui.fxml.imp.SimpleStageCtrl;
import aria.gui.fxml.imp.StageCtrl;
import aria.gui.manager.DownList;
import aria.opt.R;
import aria.opt.Setting;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Main extends Application {
	
	@Override
	public void init() throws Exception {
//		R.INIT_CHANGES();
	}
	
	
	@Override
	public void stop() throws Exception {
		R.SAVE_CHANGES();
	}
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start( Stage stage) throws Exception {
		stage.getIcons().add(new Image(getClass().getResource("appicns_Sparrow.png").openStream()));
		Preloader preloader = new Preloader();
		preloader.start(new Stage());
		preloader.service.start();
		preloader.setOnSucceeded((e)->{
			preloader.close();
			tryCtrl(stage);
			Platform.runLater(()->{
				saveStateTimeLine();
			});
		});
		
//		Tray tray = new Tray();
		
		
	}

	public void tryCtrl(final Stage stage) {
		
		try {
			StageCtrl ctrl = new SimpleStageCtrl(stage);
			
			MainFxGet fxGet = new MainFxGet(stage);
			FXMLLoader loader = new FXMLLoader(MainFxGet.FXML);
			loader.setController(fxGet);
			AnchorPane pane = loader.load();
			
			
			FXMLLoader hBoxLoader = new FXMLLoader(StageCtrl.FXML);
			hBoxLoader.setController(ctrl);
			HBox hBox = hBoxLoader.load();
			ctrl.setStageStyle(StageStyle.UTILITY);
			pane.getChildren().addAll(hBox);
			AnchorPane.setRightAnchor(hBox, 0.0);
			AnchorPane.setTopAnchor(hBox, 0.0);
			stage.setTitle(About.App_Name);
			stage.setScene(new Scene(pane));
			stage.show();
			MovingStage.pikeToMoving(stage, pane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveStateTimeLine(){
//		R.ReadDownloads();
		Timeline line = new Timeline(new KeyFrame(Duration.minutes(Setting.getTime_to_save()), (e)->{
			
			for (Link link :DownList.DownloadList) {
				link.bindState();
			}
			R.Save_Changes_Progress();
		}));
		line.setCycleCount(Animation.INDEFINITE);
		line.play();
		
	}
	
	
}
