package d.tray;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import d.Main;

public class FxTray extends Application {
	
	String title = "fxGet list";

	public FxTray() {
		
	}

	public static void main(String[] args) { launch(args); }

	@Override
	public void start(Stage stage) throws Exception {
//		stage.setAlwaysOnTop(true);
//		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setTitle(title);
		stage.getIcons().add(new Image(Main.class.getResource("appicns_Sparrow.png").openStream()));
		
		VBox box = new VBox();
		box.setPadding(new Insets(5));
		box.setSpacing(5);
		box.setStyle("-fx-border-style: solid;-fx-border-color: #de4a45;-fx-border-width: 1px;");
		
		HBox vBox = new HBox();
		vBox.setPrefSize(100, 30);
		
		
		Image image = new Image(Main.class.getResource("appicns_Sparrow.png").openStream());
		ImageView imageView1 = new ImageView(image);
		imageView1.setFitHeight(30);
		imageView1.setFitWidth(30);
		
		ImageView imageView2 = new ImageView(image);
		imageView2.setFitHeight(30);
		imageView2.setFitWidth(30);
		
		ImageView imageView3 = new ImageView(image);
		imageView3.setFitHeight(30);
		imageView3.setFitWidth(30);
		
		Button b1 = new Button();
		b1.setGraphic(imageView1);
		
		Button b2 = new Button();
		b2.setGraphic(imageView2);
		
		Button b3 = new Button();
		b3.setGraphic(imageView3);
		
		vBox.getChildren().addAll(b1, b2, b3);
		
		box.getChildren().add(vBox);
		
		ListView<String> list = new ListView<>(FXCollections.observableArrayList("test","s","df"));
		list.setPrefSize(100, 200);
		
		b1.setOnAction((e)->{box.getChildren().add(0, list); stage.sizeToScene(); calStatgePosition(stage); });
		
		
		Scene scene = new Scene(box);
		
		stage.setScene(scene);
		stage.show();
		calStatgePosition(stage);
	}

	Rectangle2D rec = Screen.getPrimary().getBounds();
	private void calStatgePosition(Stage stage) {
		stage.setX(rec.getWidth() - stage.getWidth() - 10);
		stage.setY(rec.getHeight() - stage.getHeight() - 10);
	}

}
