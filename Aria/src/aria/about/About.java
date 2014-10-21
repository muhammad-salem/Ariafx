package aria.about;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.commons.io.FileUtils;

import aria.gui.fxml.imp.MovingStage;


public class About  {
	
	public static void showAbout(){
		new About().show();
	}
	
	static Stage  stage;
	public static String App_Name = "Aria";
	public static String App_Version = "v.0.1014.16";
	public static String App_Description = "Download Manager 2014";
	public static String Code_Name = "ن";
	public static String about = "";
	public static String github_link = "https://github.com/SalemEbo/aria";
	public void show(){
		stage = new Stage(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("About aria");
		
		try {
			String file = getClass().getResource("README.md").getFile();
			File readme = new File(file);
			about = FileUtils.readFileToString(readme);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		AboutFX fx = new AboutFX();
		FXMLLoader loader = new FXMLLoader(fx.xml);
		loader.setController(fx);
		
		try {
			AnchorPane pane = loader.load();
			Scene scene = new Scene(pane, Color.TRANSPARENT);
			stage.setScene(scene);
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		stage.show();
		MovingStage.pikeToMoving(stage, fx.anchor);
	}
	
	public class AboutFX implements Initializable{
		public URL xml;
		public AboutFX(){
			xml = getClass().getResource("About.xml");
		}
		
		@FXML
	    private AnchorPane anchor;
		
		@FXML
	    private Button facebook, github;

	    @FXML
	    private Text appName, description;

	    @FXML
	    private Hyperlink githupLink;

	    @FXML
	    private TextArea aboutText;

	    @FXML
	    void close(ActionEvent event) {
	    	stage.close();
	    }

		@Override
		public void initialize(URL location, ResourceBundle resources) {
			appName.setText(App_Name);
			description.setText(App_Description + " - " + App_Version + " [" + Code_Name + "]" );
			aboutText.setText(about);
			githupLink.setText(github_link);
			githupLink.setOnAction((e)-> {
				try {
//					Desktop.getDesktop().browse(new URI(github_link));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
		}
		
	}
	
}
