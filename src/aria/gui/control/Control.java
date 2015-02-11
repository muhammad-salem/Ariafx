package aria.gui.control;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import aria.gui.fxml.imp.MovingStage;
import aria.opt.R;
import aria.opt.Setting;
import aria.opt.Utils;


public class Control  {
	public static Control control = null;
	public static void ShowControlWindow() {
		if(control == null){
			control = new Control();
		}
		if(!control.stage.isShowing()){
			control.stage.show();
		}else{
			control.stage.hide();
		}
	}

	Control(){
		ShowControl();
	}
	Stage stage = new Stage(StageStyle.UNDECORATED);
	public void ShowControl() {
		
		ControlWin win = new ControlWin();
		FXMLLoader loader = new FXMLLoader(win.FXML);
		loader.setController(win);
		AnchorPane pane = null;
		try {
			pane = (AnchorPane)loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stage.setScene(new Scene(pane));
		stage.setAlwaysOnTop(true);
		stage.setTitle("Ariafx .. Control ...");
		MovingStage.pikeToMoving(stage, pane);
//		stage.show();
	}

	
	public class ControlWin implements Initializable{
		public URL FXML;
		public ControlWin() {
			if(FXML == null){
				FXML = getClass().getResource("control.xml");
			}
		}
		
		
		@FXML
	    private Button browserFirefox, browserChrome;

	    @FXML
	    private Button defalutPathChangeButton;

	    @FXML
	    private TextField defalutPathEdit;

	    @FXML
	    private VBox vbox;

	    @FXML
	    private ImageView browserImage;

	    @FXML
	    private TextArea browserSupport;

	    @FXML
	    private Slider browserSlider, autoSaveSlider;

	    
	    
	    String[] lines = null ;
		Button browserTempButton = null;
		TranslateTransition yTrans ;
		@Override
		public void initialize(URL location, ResourceBundle resources) {
			yTrans = new TranslateTransition(Duration.millis(500), vbox);
			
			
			ChangeListener<Number> listener = (obs, old, value)->{
				initBrowserSliderWork(value.intValue());
			};
			
			
			
			browserSlider.setMin(1);
			browserSlider.setValue(1);
			browserSlider.setShowTickMarks(true);
			browserSlider.setSnapToTicks(true);
			browserSlider.valueProperty().addListener(listener);
//			browserSlider.maxProperty().addListener(listenerMax);

			browserTempButton = browserChrome;
			browserChrome.setOnAction((e)->{
				initBrowserSliderForChrome();
			});
			initBrowserSliderForChrome();
			
			browserFirefox.setOnAction((e)->{
				initBrowserSliderForFirefox();
			});
			
			autoSaveSlider.setValue(Setting.getTime_to_save());
			autoSaveSlider.valueProperty().addListener((obs, old, value)->{
				Setting.setTime_to_save(value.doubleValue());
			});
			
			
			defalutPathEdit.setText(R.DefaultPath );
			defalutPathChangeButton.setOnAction((e)->{
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setInitialDirectory(new File(R.DefaultPath));
				chooser.setTitle("Select defalut dorectory:");
				File file = chooser.showDialog(stage);
				if(file == null) return;
				R.DefaultPath = file.getAbsolutePath();
				defalutPathEdit.setText(R.DefaultPath );
			});
			
			
		}
		
		public void initBrowserSliderWork(int v){
			if((v-1) < lines.length)
				browserSupport.setText(lines[ v-1 ]);
			try {
				if(browserChrome.equals(browserTempButton) ){
					Image image = new Image(getClass()
							.getResourceAsStream("img/linux-chrome" + v +".png"));
					browserImage.setImage(image);
					
				}else if(browserFirefox.equals(browserTempButton)){
					Image image = new Image(getClass()
							.getResourceAsStream("img/linux-firefox" + v +".png"));
					browserImage.setImage(image);
				}
			} catch (Exception e) {
				
			}
			
		}
		
		public void initBrowserSliderForChrome() {
			browserSlider.setMax(4);
			browserTempButton = browserChrome;
			lines = Utils.getStringFromInputStream(this.getClass()
					.getResourceAsStream("chrome-linux.txt")).split("\n");
			browserSlider.setValue(1);
			initBrowserSliderWork(1);
		}
		public void initBrowserSliderForFirefox() {
			browserSlider.setMax(7);
			browserTempButton = browserFirefox;
			lines = Utils.getStringFromInputStream(this.getClass()
					.getResourceAsStream("firefox-linux.txt")).split("\n");
			browserSlider.setValue(1);
			initBrowserSliderWork(1);
		}
		
		
		@FXML
	    void showBrowser(ActionEvent event) {
			yTrans.setToY(0);
			yTrans.play();
	    }

	    @FXML
	    void showControlPanel(ActionEvent event) {
	    	yTrans.setToY(-500);
			yTrans.play();
	    }

		
	    
	    void save() {

	    }

		
		@FXML
		void minimize(ActionEvent event) {
			stage.setIconified(true);
		}

		@FXML
		void close(ActionEvent event) {
			save();
			stage.close();
		}
		
	}
	
}
