package aria.gui.fxml.notify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.controlsfx.control.Notifications;

import aria.opt.R;

import com.sun.javafx.PlatformUtil;

public class Notifier {
	
	static String notify_icon = null;
	static Image image = null;
	
	public static boolean stopNotify = false;
	public static boolean darkStyle = false;
	public static Pos pos = Pos.TOP_RIGHT;
	
	public static boolean isNativeNotification() {
		if(PlatformUtil.isLinux()){
			File file = new File("/usr/bin/notify-send");
			if(file.exists()){
//				return true;
				return false;
			}
		}else if(PlatformUtil.isWindows()) {
			// not supported yet!!
			return false;
		}else if(PlatformUtil.isMac()) {
			// not supported yet!!
			return false;
		}
		return false;
	}
	
	public static void NotifyUser(String  title, String body){
		if(stopNotify){ return; }
		if(isNativeNotification()){
			NotifyNative(title, body);
		}else{
			Notify(title, body);
		}
	}
	public static void NotifyNative(String  title, String body){
		List<String> list = new ArrayList<String>();
		if(notify_icon == null){
			notify_icon = new File(R.ConfigPath, "notify.png").getAbsolutePath();
		}else if (PlatformUtil.isWindows()) {
//			list.add("start");
		}else if (PlatformUtil.isMac()) {
//			list.add("open");
		}
		
		if(PlatformUtil.isLinux()){
			list.add("notify-send");
			
			list.add("-i");
			list.add(notify_icon);
			
			/*
			list.add("-t");
			list.add("10");
			
			list.add("-c");
			list.add("Network");
			
			list.add("-u");
			list.add("low");
			*/
			
			list.add(title);
			list.add(body);
			
			ProcessBuilder builder = new ProcessBuilder(list);
			try {
				builder.start();
			} catch (IOException e) {
				R.cout("error in open " + e.getMessage());
			}
		}
		else if (PlatformUtil.isWindows()) {
//			list.add("start");
		}else if (PlatformUtil.isMac()) {
//			list.add("open");
		}
		
		
		
	}
	
	static Notifications note;
	public static void Notify(String  title, String body){
		if(note == null) note = Notifications.create();
		if(image == null) {
			image = new Image(Notifier.class
					.getResourceAsStream("notify.png"));
			note.graphic(new ImageView(image));
		}
		
		
		note.title(title);
		note.text(body);
		note.position(pos);
		if(darkStyle) note.darkStyle();
		
		
		
		Platform.runLater(()->{
			// need fx thread
			note.show();
		});
	}
	
	
	/*
	Stage stage;
	AnchorPane pane;
	Notify notify;
	TranslateTransition tt;
	
	
	public Notifier() {
		try {
			notify = new Notify();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("notify.xml"));
			loader.setController(notify);
			pane = loader.load();
			pane.setBackground(Background.EMPTY);
		} catch (Exception e) {
			System.err.println("can't load notify.xml");
		}
		
		tt = new TranslateTransition(Duration.seconds(2), pane);
		tt.setCycleCount(2);
		tt.setAutoReverse(true);
		
		tt.setToX(-400);
	}
	
	public void show(){
		stage = new Stage(StageStyle.UNDECORATED);
		stage.setScene(new Scene(pane, Color.TRANSPARENT));
		Rectangle2D screen = Screen.getPrimary().getBounds();
		stage.setX(screen.getWidth() - 400 );
		stage.setY(pane.getHeight() + 50);
		stage.show();
		
		tt.play();
		tt.setDelay(Duration.minutes(1));
		tt.setOnFinished((e)->{
			stage.close();
		});
	}
	
	public void setTitle(String title){
		notify.title.setText(title);
	}
	
	public void setIcon(Image image){
		notify.image.setImage(image);
	}
	
	public void setDetails(String... sub){
		ObservableList<Label> labels = FXCollections.observableArrayList();
		for (String str : sub) {
			labels.add(new Label(str));
		}
		notify.list.setItems(labels);
	}
	
	
	public class Notify implements Initializable{
		
		@FXML
	    private ImageView image;

	    @FXML
	    private Label title;

	    @FXML
	    private ListView<Label> list;

		
		@Override
		public void initialize(URL location, ResourceBundle resources) {
			
		}
		
	}
	*/

}