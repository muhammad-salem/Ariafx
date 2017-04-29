package aria.gui.fxml.control;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.apache.commons.io.FilenameUtils;

import aria.core.download.Download;
import aria.core.download.Link;
import aria.core.url.type.Category;
import aria.core.url.type.DownState;
import aria.core.url.type.Queue;
import aria.gui.fxml.AriafxMainGUI;
import aria.gui.fxml.Item2Gui;
import aria.gui.fxml.imp.MovingStage;
import aria.gui.manager.DownList;
import aria.gui.manager.DownManager;
import aria.opt.R;
import aria.opt.Utils;

public class FeatchURL implements Initializable {

	private Stage stage;
//	private Item item;
	private Link link;

	public static URL FXML;

	public FeatchURL(Stage stage) {
		super();
		this.stage = stage;
		this.stage.initStyle(StageStyle.UNDECORATED);
		this.stage.setTitle("Add Link");
		if (FXML == null)
			FXML = getClass().getResource("fetchUrl.xml");
	}
	
	/**-------------------------0-----------------------------**/

	@FXML
	private CheckBox authrization;

	@FXML
	private VBox vBox;

	@FXML
	private PasswordField pass;

	@FXML
	private ImageView iconFile;

	@FXML
	private Label itemLength, message;

	@FXML
	private TextField user, editSavePath;
	
	
	@FXML
	private AnchorPane anchor, anchorVB1, anchorVB2;

	@FXML
	private TextField link1, linkUrl, referer;

	@FXML
	private ChoiceBox<Category> category;

	@FXML
	private ChoiceBox<Queue> queue;
	
	@FXML
    private ChoiceBox<Integer> parallelThread;

	// anchor
	private Transition animation;
	
	
	/**-------------------------1-----------------------------**/

	void initAnchor() {

		animation = new Transition() {
			
			{
				 setCycleDuration(Duration.seconds(1));
			}
			@Override
			protected void interpolate(double frac) {
				if (!Double.isNaN(frac)) {
					double hight = 150 + ( 70 * frac);
					anchor.setPrefHeight(hight);
					stage.setHeight(hight);
					vBox.setLayoutY(-(150*frac));
					
				}
				
			}
		};
		
		animation.setCycleCount(1);
//		animation.setAutoReverse(true);
		
	}

	@FXML
	void minimize(ActionEvent event) {
		stage.setIconified(true);
	}

	@FXML
	void close(ActionEvent event) {
		if(link != null){
			link.getItem().clearCache();
		}
		stage.close();
	}
	
	/**-------------------------2-----------------------------**/

	// anchorVB1
	void initAnchorVB1() {
		String url = Clipboard.getSystemClipboard().getString();
		if (url != null && Utils.verifyURL(url)) {
			
			link1.setText(url);
			link1.setFocusTraversable(false);
		}
		authrization.selectedProperty().addListener((obv, old, newV) -> {
			if (newV) {
				user.setDisable(false);
				pass.setDisable(false);
			} else {
				user.setDisable(true);
				pass.setDisable(true);
			}
		});
		
		ObservableList<Integer> listNum 
			= FXCollections.observableArrayList(1,2,3,4,5,6,7,8,10,16,20,24,32);
		parallelThread.setItems(listNum);
		parallelThread.getSelectionModel()
			.select(Integer.valueOf(Download.ParallelChunks));
	}

	@FXML
	void fetchInfo(ActionEvent event) {
		if(bindLink()){
			
			parallelThread.getSelectionModel()
			.select(Integer.valueOf(
					Utils.gesslChunkesNum(link.getLength())));
			
			// init to bimd item
			initAnchorVB2();
			// play animation
			animation.play();
		}
		
	}

	/**-------------------------3-----------------------------**/
	// anchorVB2

	void initAnchorVB2() {
		
		this.stage.setTitle("Fetch link Info");

		linkUrl.setText(link.getURL());

		// init category and queue
		category.setItems(Category.getCategores());
		category.selectionModelProperty()
				.get().selectedItemProperty()
				.addListener((obs, old, nw) -> {
					link.setCategory(nw.getName());
					editSavePath.setText(nw.getSaveTo()
							+ File.separator + link.getFilename());
		});
		category.getSelectionModel().select(Category.get(link.getCategory()));

		queue.setItems(Queue.getQueuesTree());
		queue.getSelectionModel().select(Queue.getQueue(link.getQueue()));

		queue.selectionModelProperty().get().selectedItemProperty()
				.addListener((obs, old, nw) -> {
					link.setQueue(nw.getName());
				});

		// init changes on saveto path
		editSavePath.textProperty().addListener((ob, old, str) -> {
			link.setSaveto(str);
			if(link.checkConfliectName()){
				str = link.getSaveto();
				editSavePath.setText(str);
			}
			link.setFilename(FilenameUtils.getName(str));
		});
		
		itemLength.setText(Utils.fileLengthUnite(link.getLength()));
		setupICONFile();
		
	}

	private void setupICONFile() {
		
		
//		iconFile.setImage(value);
	}

	@FXML
	void addCategory(ActionEvent event) {
		AddCategory.addCategory();
	}

	@FXML
	void addQueue(ActionEvent event) {
		AddQueue.addQueue();
	}

	@FXML
	void changeSaveTo(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialFileName(link.getFilename());
		chooser.setTitle("Save " + link.getFilename() + " to:");
		File file = new File(link.getSaveto());
		chooser.setInitialDirectory(file.getParentFile());
		file = chooser.showSaveDialog(null);
		if (file != null) {
			editSavePath.setText(file.getPath());
		}
	}

	@FXML
	void downLater(ActionEvent event) {
		Item2Gui gui = bindILink2Gui();
		if(gui == null){
			return;
		}else{
			DownList.guis.add(gui);
		}
		stage.close();
//		link.setDownState(DownState.Pause);
		link.updateProgressShow();
		DownManager.IniyDownUi(link);
	}

	@FXML
	void downloadNow(ActionEvent event) {
//		link.setDownState(DownState.Downloading);
		link.start();
		
		Item2Gui gui= bindILink2Gui();
		if(gui == null){
			return;
		}else{
			DownList.guis.add(gui);
		}
		stage.close();
		
		DownManager.IniyDownUi(link);
	}
	
	/**-------------------------4-----------------------------**/

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		initAnchor();
		initAnchorVB1();

	}
	/**-------------------------5-----------------------------**/
	
	private boolean bindLink() {
		String str = link1.getText();
		if (Utils.verifyURL(str)) {
			try {
//				item = new Item(str);
				link = new Link(str);
				if(!referer.getText().equals(""))
					link.setReferer(referer.getText());
				
				link.setDownState(DownState.InitDown);
				
				if (authrization.isSelected()) {
					link.setAuthrized(user.getText(), pass.getText());
				}
			} catch (Exception e) {
				message.setVisible(true);
				return false;
			}
		} else {
			message.setVisible(true);
			return false;
		}
		
		link.retrieveInfo();
//		link.callRange();
		
		return true;
	}
	
	/*
	private Item2Gui bindItem2Gui(){
		Item2Gui gui = null;
		try {
			gui = new Item2Gui(link);
			FXMLLoader loader;
			if(AriafxMainGUI.isMinimal){
				loader = new FXMLLoader(Item2Gui.FXMLmin);
			}else {
				loader= new FXMLLoader(Item2Gui.FXML);
			}
			loader.setController(gui);
			AnchorPane pane = loader.load();
			DownList.AddGuiToList(pane, link);
		}catch (Exception e) {
			System.err.println("error in loading fxml file.\n"
					+ Item2Gui.FXML.toString());
		}
		R.Save_Changes_Progress();
		return gui;
	}
	*/
	
	private Item2Gui bindILink2Gui(){
		Integer parl = parallelThread.getValue();
		if(parl == 1) link.setStreaming();
		link.setChunksNum(parl);
		
		link.setDownState(DownState.InitDown);
		link.callRange();
		
		
		Item2Gui gui = null;
		try {
			gui = new Item2Gui(link);
			FXMLLoader loader;
			if(AriafxMainGUI.isMinimal){
				loader = new FXMLLoader(Item2Gui.FXMLmin);
			}else {
				loader= new FXMLLoader(Item2Gui.FXML);
			}
			loader.setController(gui);
			AnchorPane pane = loader.load();
			DownList.AddGuiToList(pane, link);
		}catch (Exception e) {
			System.err.println("error in loading fxml file.\n"
					+ Item2Gui.FXML.toString());
		}
		R.Save_Changes_Progress();
		return gui;
	}
	
	
	/**-------------------------6-----------------------------**/

	public static void AddURL() {
		
		Platform.runLater(new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					Stage ui = new Stage();
					FeatchURL featchURL = new FeatchURL(ui);
					FXMLLoader loader = new FXMLLoader(FXML);
					loader.setController(featchURL);
					ui.setScene(new Scene(loader.load()));
					ui.show();
					MovingStage.pikeToMoving(ui, featchURL.anchor);
				} catch (Exception e) {
					System.err.println("error in loading fxml file.\n"
							+ FXML.toString());
				}
				return null;
			}
			
		});
		
		
	}

}
