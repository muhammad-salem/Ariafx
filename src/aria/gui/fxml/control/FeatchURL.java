package aria.gui.fxml.control;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import aria.core.download.Link;
import aria.core.download.ReadyItem;
import aria.core.url.Item;
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
	private Item item;
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
	private CheckBox authrization, stream;

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
	private Transition animation, linkUrlAnim;
	
	private String saveTemp;
	
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
		
		linkUrlAnim = new Transition() {
			
			{
				 setCycleDuration(Duration.seconds(1));
			}
			@Override
			protected void interpolate(double frac) {
				if (!Double.isNaN(frac)) {
					// from anchorVB2
					int length = item.getURL().length();
			        int n = Math.round(length * (float) frac);
			        linkUrl.setText(item.getURL().substring(0, n));
					// for save to 
			        length = saveTemp.length();
			        n = Math.round(length * (float) frac);
			        editSavePath.setText(saveTemp.substring(0, n));
			        
				}
				
			}
		};
		linkUrlAnim.setCycleCount(1);
//		linkUrlAnim.setAutoReverse(true);
		
		animation.setOnFinished((e)->{ linkUrlAnim.play();});
		
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
		
		stream.selectedProperty().addListener((obv, old, newV) -> {
			if (newV) {
				parallelThread.setDisable(true);
			} else {
				parallelThread.setDisable(false);
			}
		});
		
		ObservableList<Integer> listNum 
			= FXCollections.observableArrayList(1,2,4,8,10,16,20,24,32);
		parallelThread.setItems(listNum);
		parallelThread.getSelectionModel().select(Integer.valueOf(1));
		
	}

	@FXML
	void fetchInfo(ActionEvent event) {
		if(bindItem()){
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

//		linkUrl.setText(item.getURL());

		// init category and queue
		category.setItems(Category.getCategores());
		category.selectionModelProperty()
				.get().selectedItemProperty()
				.addListener((obs, old, nw) -> {
					item.setCategory(nw.getName());
					editSavePath.setText(nw.getSaveTo()
							+ File.separator + item.getFilename());
		});
		category.getSelectionModel().select(Category.get(item.getCategory()));

		queue.setItems(Queue.getQueuesTree());
		queue.getSelectionModel().select(Queue.getQueue(item.getQueue()));

		queue.selectionModelProperty().get().selectedItemProperty()
				.addListener((obs, old, nw) -> {
					item.setQueue(nw.getName());
				});

		// init changes on saveto path
		editSavePath.textProperty().addListener((ob, old, str) -> {
			item.setSaveto(str);
			if(item.checkConfliectName()){
				str = item.getSaveto();
				editSavePath.setText(str);
			}
			item.setFilename(FilenameUtils.getName(str));
		});
		
		setupICONFile();
		
		bindLink();
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
		chooser.setInitialFileName(item.getFilename());
		chooser.setTitle("Save " + item.getFilename() + " to:");
		File file = new File(item.getSaveto());
		chooser.setInitialDirectory(file.getParentFile());
		file = chooser.showSaveDialog(null);
		if (file != null) {
			editSavePath.setText(file.getPath());
		}
	}

	@FXML
	void downLater(ActionEvent event) {
		Item2Gui gui = bindItem2Gui();
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
		
		Item2Gui gui= bindItem2Gui();
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
	
	private boolean bindItem() {
		String str = link1.getText();
		if (Utils.verifyURL(str)) {
			try {
				item = new Item(str);
				item.setReferer(referer.getText());
				
				item.isStreaming = stream.isSelected();
				if(item.isStreaming){
					item.setChunksNum(1);
				}else{
					Integer i = parallelThread.getValue();
					item.setChunksNum(i);
				}
				
				item.setState(DownState.InitDown);
				
				if (authrization.isSelected()) {
					item.setAuthrized(user.getText(), pass.getText());
				}
			} catch (Exception e) {
				message.setVisible(true);
				return false;
			}
		} else {
			message.setVisible(true);
			return false;
		}
		ReadyItem ready = new ReadyItem(item);
		item = ready.initItem();
		
		// get temp value of saveto
		saveTemp = item.getSaveto();
		
		return true;
	}

	private void bindLink() {
		link = new Link(item);
		itemLength.setText(Utils.fileLengthUnite(item.length));
	}
	
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
	
	
	/**-------------------------6-----------------------------**/

	public static void AddURL() {
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
	}

}
