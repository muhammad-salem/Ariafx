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
import aria.gui.fxml.AriafxMainGUI;
import aria.gui.fxml.imp.MovingStage;
import aria.gui.fxml.notify.Notifier;
import aria.gui.manager.DownList;
import aria.gui.manager.ParameterToLink;
import aria.nativemessaging.Chrome;
import aria.nativemessaging.ChromeMSG;
import aria.opt.AppInstance;
import aria.opt.Parameter;
import aria.opt.R;
import aria.opt.Setting;
import aria.tray.TrayUtile;

public class Ariafx extends Application {
	

	static String[] param;
	public static Stage ui;
	public static boolean silentStartup = false;
	public static Thread shutdownHookThread;
	
	@Override
	public void init() throws Exception {
		if(silentStartup) R.INIT_CHANGES();
	}
	
	public void initAfterFX() {
		Platform.runLater(() -> {
			// R.ReadDownloads();
			saveStateTimeLine();
			initMonitor();
			// init tray icon
			TrayUtile.InitTray();
			Platform.setImplicitExit(false);
		});
	}

	/**
	 * [--url=URL] 
	 * [--http-referer=REFERER] 
	 * [--file-name=FNAME]  
	 * [--user-agent=UA] 
	 * [--cookie=COOKIE] 
	 * [--cookie-file=CFILE] 
	 * [--input-file=UFILE]
	 * [--fileSize=int]
	 * [--silent]
	 * @return
	 */
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
			if(args.length != 0 && args[0].contains("--silent")){
				silentStartup = true;
			}
			param = args;
			launch(args);
		}
		
	}

	
	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(
				new Image(getClass().getResource("aria.png").openStream()));
		if(silentStartup){
			initUI(stage);
			initAfterFX();
			R.ReadDownloads();
		}else{
			Notifier.NotifyNative("Lunch Ariafx", "");
			Preloader preloader = new Preloader();
			preloader.start(new Stage());
			preloader.service.start();
			preloader.setOnSucceeded((e) -> {
				preloader.close();
				initUI(stage);
				initAfterFX();
				Notifier.NotifyUser("Ariafx is running", "Download Manager");
			});
		}
		ui = stage;
		
		shutdownHookThread = new Thread(){
			@Override
			public void run() {
				R.Save_Changes_Progress();
			}
		};
		Runtime.getRuntime().addShutdownHook(shutdownHookThread);
	}

	public void initUI(final Stage stage) {

		try {

			AriafxMainGUI mainGUI = new AriafxMainGUI(stage);
			FXMLLoader loader = new FXMLLoader(AriafxMainGUI.FXML);
			loader.setController(mainGUI);
			AnchorPane pane = loader.load();
			
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle(About.App_Name);
			stage.setScene(new Scene(pane));
			
			MovingStage.pikeToMoving(stage, pane);
			if(!silentStartup){
				stage.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void saveStateTimeLine() {
		
		Timeline line = new Timeline(new KeyFrame(Duration.minutes(Setting
				.getTime_to_save()), (e) -> {

			for (Link link : DownList.DownloadList) {
				link.bindState();
			}
			R.Save_Changes_Progress();
		}));
		line.setCycleCount(Animation.INDEFINITE);
		line.play();
		
	}
	
	static FileAlterationMonitor monitor;
	@Override
	public void stop() throws Exception {
//		stopMonitor();
//		R.SAVE_CHANGES();
		Exit();
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
//								param.clear();
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

	public static void StopMonitor() {
		try {
			
			monitor.stop();
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("falid stop mointor... or it had been stoped ealiary");
		}
	}
	
	public static void showUI() {
		ui.show();
		ui.setAlwaysOnTop(true);
		ui.setIconified(false);
		ui.setAlwaysOnTop(false);
	}
	
	public static void hideUI() {
		ui.hide();
		ui.setIconified(true);
	}
	
	public static void hideUITray() {
		ui.hide();
	}
	
	public static void Exit() {
		StopMonitor();
		R.SAVE_CHANGES();
		Runtime.getRuntime().removeShutdownHook(shutdownHookThread);
		Platform.setImplicitExit(true);
		Platform.exit();
		System.exit(0);
	}

}
