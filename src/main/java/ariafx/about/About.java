package ariafx.about;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import ariafx.gui.fxml.imp.MovingStage;
import ariafx.opt.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class About  {
	
	public static void showAbout(){
		new About().show();
	}
	
	static Stage  stage;
	public static String App_Name = "ariafx";
	public static String App_Version = "0.30.0 2022.03.18";
	public static String App_Description = "Download Manager 2014-2022";
	public static String Code_Name = "ن";
	public static String about = "";
	public static String GithubLink = "https://github.com/salemebo/ariafx";
	public static String FacebookPage = "https://github.com/salemebo/ariafx";
	
	public void show(){
		stage = new Stage(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("About ariafx");
         
		about = getAbout();
		if(about == null){
			about = "add support to firefox by flashgot [cli] : \n"
					+ "[--url=URL] [--http-referer=REFERER] \n"
					+ "[--file-name=FNAME] [--user-agent=UA] \n"
					+ "[--cookie=COOKIE] [--cookie-file=CFILE] \n"
					+ "[--input-file=UFILE]";
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
	
	public static String getAbout(){
		String file = "";
		InputStream stream = About.class.getResourceAsStream("README.md");
		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(stream) );
		
		try {
			String str = "";
			while ((str = reader.readLine()) != null) {
				file += str + "\n";
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return file;
	}
	
	public class AboutFX implements Initializable{
		public URL xml;
		public AboutFX(){
			xml = getClass().getResource("About.xml");
		}
		
		@FXML
	    private AnchorPane anchor;
		
	    @FXML
	    private Text appName, description;

	    @FXML
	    private Hyperlink githupLink, facebookLink;

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
			githupLink.setText(GithubLink);
			githupLink.setOnAction((e)-> {
				try {
//					Desktop.getDesktop().browse(new URI(github_link));
					R.openInProcess(GithubLink);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			
			facebookLink.setOnAction((e)-> {
				try {
//					Desktop.getDesktop().browse(new URI(github_link));
					R.openInProcess(FacebookPage);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			
		}
		
	}
	
}