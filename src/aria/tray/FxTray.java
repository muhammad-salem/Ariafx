package aria.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import aria.Ariafx;
import aria.about.About;
import aria.tray.mouse.MouseListenerFX;


public class FxTray extends Application{
	

	SystemTray tray;
	TrayIcon main;
	TrayIcon list;
	
	Context mainContext;
	ContextMenu contextMenuMain;
	
	ContextMenu contextMenuList;

	Image mainImg;
	Image listImg;
	
	public FxTray()  {
		
		tray = SystemTray.getSystemTray();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		if(!SystemTray.isSupported()) return;
		
		
		mainImg = toolkit.getImage(Ariafx.class.getResource("aria.png"));
		main = new TrayIcon(mainImg, About.App_Name);
		main.setImageAutoSize(true);
		
		listImg = toolkit.getImage(getClass().getResource("img/list.png"));
		list = new TrayIcon(listImg,"list");
		list.setImageAutoSize(true);
		
	}
	
	 public void  initializeFX(){
		 
		 try {
			 mainContext = new Context();
			 FXMLLoader loader = new FXMLLoader(getClass().getResource("MainContext.xml"));
			 loader.setController(mainContext);
			 contextMenuMain = loader.load();
			 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 contextMenuList = new ContextMenu();
		 
		 MenuItem item = new MenuItem("Show All");
		 item.setOnAction((e)->{
			 TrayUtile.showAll();
		 });
		 contextMenuList.getItems().add(item);
		 
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
		main.addMouseListener(new MouseListenerFX() {
			
			@Override
			public void mouseReleasedFX(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressedFX(MouseEvent e) {
				int x = e.getX();
				int y = e. getY();
				mainContext.show(TrayUtile.stage, x, y);
			}
			
			@Override
			public void mouseExitedFX(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEnteredFX(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClickedFX(MouseEvent e) {
				Ariafx.showUI();
			}
		});
	}
	
	public void addListMouseListener() {
		list.addMouseListener(new MouseListenerFX() {
			
			@Override
			public void mouseReleasedFX(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressedFX(MouseEvent e) {
				int x = e.getX();
				int y = e. getY();
				contextMenuList.show(TrayUtile.stage, x, y);
			}
			
			@Override
			public void mouseExitedFX(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEnteredFX(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClickedFX(MouseEvent e) {
//				Ariafx.showUI();
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
