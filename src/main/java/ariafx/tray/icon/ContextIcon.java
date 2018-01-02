package ariafx.tray.icon;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ContextIcon {
	
	Stage stage;
	ContextMenu context;
	
	boolean fromFXML;
	
	public ContextIcon(boolean hasFXML) {
		AnchorPane pane = new AnchorPane();
		pane.setPrefSize(0, 0);
		pane.setMaxSize(0, 0);
		pane.setMinSize(0, 0);
		
		stage = new Stage(StageStyle.UNDECORATED);
		stage.setScene(new Scene(pane, 0, 0));
		stage.setMaxHeight(0);
		stage.setMaxWidth(0);
		stage.setMinHeight(0);
		stage.setMinWidth(0);
		stage.setAlwaysOnTop(true);
		
		fromFXML = hasFXML;
		initContextMenu();
	}
	
	private void initContextMenu(){
		if(fromFXML){
			return;
		}
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				context = new ContextMenu();
				context.setAutoFix(true);
			}
		});
		
	}
	
	public void show(Stage stage, int x, int y) {
		stage.setX(x);
		stage.setY(y);
		Platform.runLater(() -> {
			stage.show();
			context.setAnchorX(x);
			context.setAnchorY(y);
			context.show(stage);
		});

	}
	
	public void setContext(ContextMenu context) {
		this.context = context;
	}
	
	public ContextMenu getContext() {
		return context;
	}
	
	void add(MenuItem e){
		context.getItems().add(e);
	}
	
	void remove(MenuItem e){
		context.getItems().remove(e);
	}
	
	public ObservableList<MenuItem> getItems(){
		return context.getItems();
	}

}
