package aria;

import java.io.File;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import aria.about.About;
import aria.core.download.Link;
import aria.gui.fxml.MainFxGet;
import aria.gui.fxml.imp.MovingStage;
import aria.gui.manager.DownList;
import aria.gui.manager.ParameterToLink;
import aria.nativemessaging.Chrome;
import aria.nativemessaging.ChromeMSG;
import aria.opt.AppInstance;
import aria.opt.Parameter;
import aria.opt.R;
import aria.opt.Setting;

public class Aria extends Application {
	

	static String[] param;  
	@Override
	public void init() throws Exception {
//		 R.INIT_CHANGES();
	}

	public static void main(String[] args) throws Exception {
		
		if(args.length != 0 && args[0].contains(Chrome.extensions_id)){
			String str = Chrome.readMessage();
			ChromeMSG msg = ChromeMSG.CreateMessage(str.trim());
			args = msg.toArgs();
		}
		
		if(AppInstance.isAppInstanceExists()){
			if(args.length != 0){
				/*Parameter parameter =*/ new Parameter(args);
				//System.out.println(Arrays.toString(args));
				//System.out.println(parameter.toString());
			}
			Platform.exit();
		}else{
			param = args;
			launch(args);
		}
		
	}

	
	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(
				new Image(getClass().getResource("aria.png").openStream()));
		Preloader preloader = new Preloader();
		preloader.start(new Stage());
		preloader.service.start();
		preloader.setOnSucceeded((e) -> {
			preloader.close();
			tryCtrl(stage);
			Platform.runLater(() -> {
				saveStateTimeLine();
			});
		});
		
	}

	public void tryCtrl(final Stage stage) {

		try {

			MainFxGet fxGet = new MainFxGet(stage);
			FXMLLoader loader = new FXMLLoader(MainFxGet.FXML);
			loader.setController(fxGet);
			AnchorPane pane = loader.load();

			stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle(About.App_Name);
			stage.setScene(new Scene(pane));
			stage.show();
			MovingStage.pikeToMoving(stage, pane);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void saveStateTimeLine() {
		// R.ReadDownloads();
		Timeline line = new Timeline(new KeyFrame(Duration.minutes(Setting
				.getTime_to_save()), (e) -> {

			for (Link link : DownList.DownloadList) {
				link.bindState();
			}
			R.Save_Changes_Progress();
		}));
		line.setCycleCount(Animation.INDEFINITE);
		line.play();
		
		initMonitor();
	}
	
	FileAlterationMonitor monitor;
	@Override
	public void stop() throws Exception {
		stopMonitor();
		R.SAVE_CHANGES();
	}
	
	public void initMonitor() {
		IOFileFilter fileFilter = FileFilterUtils.and(
				FileFilterUtils.fileFileFilter(),
				FileFilterUtils.suffixFileFilter(".json"));
		IOFileFilter dirFilter = FileFilterUtils.and(
				FileFilterUtils.directoryFileFilter(),
				HiddenFileFilter.VISIBLE);
		
		IOFileFilter filter = FileFilterUtils.or(fileFilter, dirFilter);
		
		FileAlterationObserver observer = 
				new FileAlterationObserver(new File(R.NewLink), filter);
		observer.addListener(new FileAlterationListener() {
			
			@Override
			public void onStop(FileAlterationObserver observer) {}
			@Override
			public void onStart(FileAlterationObserver observer) {}
			@Override
			public void onFileDelete(File file) {}
			
			@Override
			public void onFileCreate(File file) {
				new Service<Void>() {
					@Override
					protected Task<Void> createTask() {
						return new Task<Void>() {

							@Override
							protected Void call() throws Exception {
								Parameter param = Parameter.fromJson(file);
//								System.out.println(param);
								ParameterToLink.initLink(param);
//						param.clear();
								return null;
							}
						};
					}
				}.start();
			}
			
			@Override
			public void onFileChange(File file) {}
			@Override
			public void onDirectoryDelete(File directory) {}
			@Override
			public void onDirectoryCreate(File directory) {}
			@Override
			public void onDirectoryChange(File directory) {}
		});
		
		monitor = new FileAlterationMonitor(1000, observer);
		try {
			monitor.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(param.length != 0){
			Platform.runLater(new Task<Void>() {
				@Override
				protected Void call() throws Exception {
//					System.out.println(Arrays.toString(param));
					Parameter parameter = new Parameter();
					parameter.setParamter(param);
					return null;
				}
			});
		}
		
	}

	public void stopMonitor() {
		try {
			monitor.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
