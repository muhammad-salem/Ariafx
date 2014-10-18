package d.gui.fxml.notify;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Notifier {
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

}
