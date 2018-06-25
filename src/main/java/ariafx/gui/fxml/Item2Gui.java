package ariafx.gui.fxml;

/**
 * Sample Skeleton for 'item.fxml' Controller Class
 */

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import org.apache.commons.io.FilenameUtils;



import ariafx.core.download.Link;
import ariafx.core.url.type.Category;
import ariafx.core.url.type.Queue;
import ariafx.gui.fxml.imp.ProgressStyled;
import ariafx.gui.fxml.imp.ProgressStyled.StyleProgress;
import ariafx.gui.manager.DownList;
import ariafx.gui.manager.ItemBinding;
import ariafx.opt.R;
import ariafx.opt.Utils;

public class Item2Gui implements Initializable {

	public static URL FXML;
	public static URL FXMLmin;

	public TranslateTransition showSetting, showChart, showDefault;
	private Link link;

	public Item2Gui(Link link) {
		this.link = link;
		if (FXML == null) {
			FXML = getClass().getResource("item.xml");
			FXMLmin = getClass().getResource("item-min.xml");
		}
	}

	public Link getLink() {
		return link;
	}

	public Link getDownload() {
		return link;
	}

	public void fromMin2Item() {
		fromMin2Item(this);
		updateProgressShow();
	}

	public void fromItem2Min() {
		fromItem2Min(this);
		updateProgressShow();
	}

	public void updateProgressShow() {
		if (!link.isRunning()) {
			link.reset();
			link.updateProgressShow();
		}

	}

	/*----------------------------- initialize-----------------------------*/

	@FXML
	public Button buttonChangePath, buttonSetting, buttonSped;

	@FXML
	private RadioButton stopHistoryGraph;

	@FXML
	public ImageView imgSetting, imgSped;

	@FXML
	public VBox vBox;
	@FXML
	public HBox hbox;

	@FXML
	public Label title, percent, url, state, 
		timeLeft, savePath, speed1, speed2, remining;
	  
    @FXML
    private Menu categoryMenu;
    
	@FXML
	public AnchorPane pane1, pane2, pane3, paneHBox, root;

	@FXML
	public TextField editSavePath, referrer;

	@FXML
	public ProgressStyled progress;

	@FXML
	public ChoiceBox<Category> category;

	@FXML
	public ChoiceBox<Queue> queue;

	@FXML
	public NumberAxis ax, ay;

	@FXML
	public LineChart<Integer, Double> chart;

	private AnchorPane showingPane;

	//private ObservableList<CheckMenuItem> catList = FXCollections.emptyObservableList();
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
//		for (Category cat : Category.getCategores()) {
//			CheckMenuItem menuItem = new CheckMenuItem(cat.getName());
//			catList.add(menuItem);
//			categoryMenu.getItems().add(menuItem);
//			if(link.getCategory().equals(menuItem.getText())){
//				menuItem.setSelected(true);
//			}
//			
//		}
		
		
		if (location.equals(FXMLmin)) {
			initializeMin();
			return;
		}

		hbox.setLayoutX(-600);
		showingPane = pane1;

		linkStateProperty();

		showDefault = new TranslateTransition(Duration.millis(500), hbox);
		showSetting = new TranslateTransition(Duration.millis(500), hbox);
		showChart = new TranslateTransition(Duration.millis(500), hbox);

		showChart.setCycleCount(1);
		showChart.setToX(600);

		showDefault.setCycleCount(1);
		showDefault.setToX(0);

		showSetting.setCycleCount(1);
		showSetting.setToX(-600);

		buttonSetting.setOnAction((e) -> {
			if (!showingPane.equals(pane2)) { // show setting tab
					showSetting.play();
					showingPane = pane2;
				} else if (!showingPane.equals(pane1)) { // show default tab
					showDefault.play();
					showingPane = pane1;
				}
			});

		buttonSped.setOnAction((e) -> {
			if (!showingPane.equals(pane3)) { // show speed tab
					showChart.play();
					showingPane = pane3;
				} else if (!showingPane.equals(pane1)) { // show default tab
					showDefault.play();
					showingPane = pane1;
				}
			});

		buttonChangePath.setOnAction((e) -> {
			FileChooser chooser = new FileChooser();
			chooser.setInitialFileName(link.getFilename());
			chooser.setTitle("Save " + link.getFilename() + " to:");
			File file = new File(link.getSaveto());
			chooser.setInitialDirectory(file.getParentFile());
			file = chooser.showSaveDialog(null);
			if (file != null) {
				editSavePath.setText(file.getPath());
				link.setSaveto(editSavePath.getText());
				link.setFilename(FilenameUtils.getName(link.getSaveto()));
				title.setText(link.getFilename());
			}
		});

		TranslateTransition grapStart = new TranslateTransition(
				Duration.millis(500), stopHistoryGraph);
		grapStart.setToX(-180);
		grapStart.setToY(40);
		grapStart.setAutoReverse(false);
		grapStart.setCycleCount(1);
		grapStart.setOnFinished((e) -> {
			stopHistoryGraph.setText("Restart Graph History");
		});

		TranslateTransition grapStop = new TranslateTransition(
				Duration.millis(500), stopHistoryGraph);
		grapStop.setToX(0);
		grapStop.setToY(0);
		grapStop.setAutoReverse(false);
		grapStop.setCycleCount(1);
		grapStop.setOnFinished((e) -> {
			stopHistoryGraph.setText("Stop Graph History");
		});

		stopHistoryGraph.selectedProperty().addListener((obs, old, value) -> {
			if (value) {
				grapStop.play();
				link.canCollectSpeedData();
				chart.setOpacity(1);
				link.addData(0.0);
			} else {
				grapStart.play();
				link.stopCollectSpeedData();
				chart.setOpacity(0.35);
			}
		});
		grapStart.play();

		category.selectionModelProperty().get().selectedItemProperty()
				.addListener(new ChangeListener<Category>() {

					@Override
					public void changed(
							ObservableValue<? extends Category> observable,
							Category oldValue, Category newValue) {
						link.setCategory(newValue.getName());
					}
				});

		queue.selectionModelProperty().get().selectedItemProperty()
				.addListener((obs, old, newValue) -> {
					link.setQueue(newValue.getName());
				});

		referrer.textProperty().addListener((obs, old, nw) -> {
			link.setReferer(nw);
		});
		remining.textProperty().bind(link.remainingProperty());
		ItemBinding.bindItem(link, this);

	}

	public void linkStateProperty() {

		link.stateProperty().addListener((ob, old, newv) -> {
			switch (newv) {
			case READY:
			case SCHEDULED:
				progress.setStyleClass(StyleProgress.Red);
				break;
			case CANCELLED:
				progress.setStyleClass(StyleProgress.Grey);
				timeLeft.setVisible(false);
				speed1.setVisible(false);
				remining.setVisible(false);
				break;
			case RUNNING:
				progress.setStyleClass(StyleProgress.Green);
				timeLeft.setVisible(true);
				speed1.setVisible(true);
				remining.setVisible(true);
				break;
			case SUCCEEDED: {
				if (link.getDownloaded() == link.getLength()) { // in success
					progress.setStyleClass(StyleProgress.Green);
					timeLeft.setVisible(false);
					speed1.setVisible(false);
					remining.setVisible(false);
				} else if (link.getDownloaded() > link.getLength()) { // in
																		// faild
					if (link.getItem().isUnknowLength()) {
						progress.setStyleClass(StyleProgress.Blue_Grey);
					} else {
						progress.setStyleClass(StyleProgress.Green);
					}
					timeLeft.setVisible(false);
					speed1.setVisible(false);
					remining.setVisible(false);
				} else { // still in pause

					progress.setStyleClass(StyleProgress.Grey);
				}
			}
				break;
			case FAILED:
				timeLeft.setVisible(false);
				speed1.setVisible(false);
				remining.setVisible(false);
				progress.setStyleClass(StyleProgress.Grey);
				break;

			default:
				break;
			}
		});
	}

	private void initializeMin() {
		linkStateProperty();
		buttonSetting.setOnAction((e) -> {
			fromMin2Item();
		});

		title.setText(link.getFilename());
		state.textProperty().bind(link.downStateProperty().asString());
		progress.progressProperty().bind(link.progressProperty());
		link.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (newValue == null)
					return;
				percent.setText(Utils.percent(link.getDownloaded(),
						link.getLength())
						+ " "
						+ Utils.fileLengthUnite(link.getDownloaded())
						+ " of " + Utils.fileLengthUnite(link.getLength())
						+ ((link.getRetry() == 0)? "" : " ("+ link.getRetry() + ")")
//						+ ((link.getRemining() == null )? "" : (" need :(" + link.getRemining() + ")" ) )
					);
			}
		});

		speed1.textProperty().bind(link.transferRateProperty());
		// progress.progressProperty().addListener(new ChangeListener<Number>(){
		//
		// @Override
		// public void changed(ObservableValue<? extends Number> observable,
		// Number oldValue, Number newValue) {
		// timeLeft.setText("Time Left: " +
		// Utils.calTimeLeft(link.getTimeLeft()));
		// }});
		timeLeft.textProperty().bind(link.lefttimeProperty);
		timeLeft.setAlignment(Pos.CENTER_RIGHT);

		// hide timeleft
		timeLeft.setVisible(false);
		remining.textProperty().bind(link.remainingProperty());
	}

	@FXML
	void toMin(ActionEvent event) {
		fromItem2Min();
	}
	
	
	

	/** --------------------------- static ------------------------------- **/

	public static void BindItem2Gui(Link link) {
		try {
			Item2Gui gui = new Item2Gui(link);
			FXMLLoader loader = new FXMLLoader(Item2Gui.FXML);
			loader.setController(gui);
			AnchorPane pane = loader.load();
			DownList.AddGuiToList(pane, link);
		} catch (Exception e) {
			System.err.println("error in loading fxml file.\n"
					+ Item2Gui.FXML.toString());
		}
		R.Save_Changes_Progress();
	}

	public static void BindItem2Guimin(Link link) {
		try {
			Item2Gui gui = new Item2Gui(link);
			FXMLLoader loader = new FXMLLoader(Item2Gui.FXMLmin);
			loader.setController(gui);
			AnchorPane pane = loader.load();
			DownList.AddGuiToList(pane, link);
		} catch (Exception e) {
			System.err.println("error in loading fxml file.\n"
					+ Item2Gui.FXML.toString());
		}
		R.Save_Changes_Progress();
	}

	public static void fromMin2Item(Item2Gui gui) {
		int i = DownList.DownloadList.indexOf(gui.getLink());
		try {
			FXMLLoader loader = new FXMLLoader(Item2Gui.FXML);
			loader.setController(gui);
			AnchorPane pane = loader.load();
			DownList.AnchorList.remove(i);
			DownList.AnchorList.add(i, pane);
		} catch (Exception e) {
			System.err.println("error in loading fxml file.\n"
					+ Item2Gui.FXML.toString());
		}
	}

	public static void fromItem2Min(Item2Gui gui) {
		int i = DownList.DownloadList.indexOf(gui.getLink());
		try {
			FXMLLoader loader = new FXMLLoader(Item2Gui.FXMLmin);
			loader.setController(gui);
			AnchorPane pane = loader.load();
			DownList.AnchorList.remove(i);
			DownList.AnchorList.add(i, pane);
		} catch (Exception e) {
			System.err.println("error in loading fxml file.\n"
					+ Item2Gui.FXMLmin.toString());
		}
	}

}
