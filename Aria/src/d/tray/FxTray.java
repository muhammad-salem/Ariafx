package d.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import d.Main;
import d.about.About;


public class FxTray extends Application{
	

	SystemTray tray;
	TrayIcon main;
	TrayIcon list;
	
	Stage stage;
	Context mainContext;
	ContextMenu context;

	Image mainImg;
	Image listImg;
	
	public FxTray()  {
		
		tray = SystemTray.getSystemTray();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		if(!SystemTray.isSupported()) return;
		
		
		mainImg = toolkit.getImage(Main.class.getResource("./gui/fxml/res/24/view-list-compact-symbolic.png"));
		main = new TrayIcon(mainImg, About.App_Name);
		
		listImg = toolkit.getImage(Main.class.getResource("./gui/fxml/res/24/view-list-details-symbolic.png"));
		list = new TrayIcon(listImg,"list");
		
	}
	
	 public void  initializeFX(){
		 stage = new Stage(StageStyle.UNDECORATED);
		 
		 try {
			 mainContext = new Context();
			 FXMLLoader loader = new FXMLLoader(getClass().getResource("MainContext.xml"));
			 loader.setController(mainContext);
			 context = loader.load();
			 
			AnchorPane pane = new AnchorPane();
			pane.setPrefSize(0, 0);
			stage.setScene(new Scene(pane));
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	 }
	
	public void addList(){
		try {
			tray.add(list);
		} catch (AWTException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
					"can not add system tray", e);
		}
	}
	
	public void addMain() {
		try {
			tray.add(main);
		} catch (AWTException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
					"can not add system tray", e);
		}
	}
	
	public void removeList() {
		tray.remove(list);
	}
	
	public void removeMain() {
		tray.remove(main);
	}
	
	public void addMainMouseListener() {
		main.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e. getY();
				
				mainContext.show(stage, x, y);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
	}
	
	
	public static void main(String[] args) { 
		launch(args);
	}
	
	
	public class Context implements Initializable {
		
		 @FXML
		 private ContextMenu context;


		@Override
		public void initialize(URL location, ResourceBundle resources) {
			
		}


		public void show(Stage stage, int x, int y) {
			stage.setX(x);
			stage.setY(y);
			
			stage.show();
			context.show(stage);
		}
		
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		FxTray tray = new FxTray();
		tray.initializeFX();
		tray.addMain();
		tray.addMainMouseListener();
	}
	
	
}
