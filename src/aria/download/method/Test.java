package aria.download.method;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import aria.core.download.Chunk;
import aria.core.url.Item;

public class Test extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	DownloadMethod download ; 
	
	@Override
	public void start(Stage stage) throws Exception {
		
		AnchorPane pane = new AnchorPane();
		Button button = new Button("click");
		pane.getChildren().add(button);
		
		stage.setScene(new Scene(pane, 500,500));
		stage.show();
		
		Item item = new Item("http://ice31.securenetsystems.net/NOGOUM?type=.flv");
		download = new DownloadMethod(item) {
				
				@Override
				public Task<Number> createDownloadTask() {
					
					
					return new Task<Number>() {
						
						@Override
						protected Number call() throws Exception {
							Chunk chunk = new Chunk(0, getItem());
							chunk.start();
							System.out.println(getItem());
							long x = 0;
							while (chunk.isRunning()) {
								System.out.print(".");
								try {
									Thread.sleep(300);
									updateValue(++x);
								} catch (Exception e) {
								}
							}
							System.out.println("out");
							return null;
						}
					};
				}
				
			};
//		download.initStateLine();
//		download.updateProgressShow();
		
		button.setOnAction(e ->{
			download.retrieveInfo();
			download.setChunksNum(1);
//			download.callRange();
//			download.updateProgressShow();
			download.start();
			System.out.println("clicked");
			});
//		download.start();
		
	}

}
