package ariafx.gui.fxml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.io.FileUtils;

import ariafx.Ariafx;
import ariafx.about.About;
import ariafx.core.download.Download;
import ariafx.core.download.Link;
import ariafx.core.url.Item;
import ariafx.core.url.Url;
import ariafx.core.url.type.ItemStatus;
import ariafx.core.url.type.GState;
import ariafx.core.url.type.Type;
import ariafx.gui.control.Control;
import ariafx.gui.fxml.control.AddCategory;
import ariafx.gui.fxml.control.AddQueue;
import ariafx.gui.fxml.control.FeatchURL;
import ariafx.gui.fxml.control.LinkProperties;
import ariafx.gui.manager.DownList;
import ariafx.opt.R;
import ariafx.opt.TempItems;
import ariafx.opt.Utils;

public class AriafxMainGUI implements Initializable {

	public static URL FXML;
	public Stage stage;

	public static boolean isMinimal = true;

	public AriafxMainGUI(Stage stage) {
		if (FXML == null)
			FXML = getClass().getResource("AriafxMainGUI.xml");
		this.stage = stage;
	}
	
	@FXML
	public MenuBar menu;
	
	@FXML
	public HBox hBoxHeader, hBoxFind;

	@FXML
	public ListView<AnchorPane> list;

	@FXML
	public TextField findField;

	@FXML
	public TreeView<Type> treeView;

	@FXML
	public TreeView<GState> treeState;

	@FXML
	public Label labelMessages;

	@FXML
	public ContextMenu contxtOpt, contextMenuTree;

	@FXML
	public Button btnOpt, deletButton, deletCompButton;

	@FXML
	public ImageView findIcon;

	@FXML
	public AnchorPane anchorRoot;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		menu.setUseSystemMenuBar(true);
		
		initTreeView();
		initSelectTreeProperty();
		
		initTranslateTransition();
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		list.setItems(DownList.AnchorList);
		findField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (newValue.equals(oldValue))
						return;
					if (newValue.equals("") || newValue == null) {
						findIcon.getStyleClass().removeAll("image-close",
								"image-find");
						findIcon.getStyleClass().add("image-find");

						// init list to default
						list.setItems(DownList.AnchorList);
					} else {
						findIcon.getStyleClass().removeAll("image-close",
								"image-find");
						findIcon.getStyleClass().add("image-close");
						// init searched list
						ObservableList<Link> down = DownList
								.findByTitle(newValue);
						list.setItems(DownList.getAnchor(down));

					}
				});
		
		
		findIcon.setOnMouseClicked((e) -> {
			findField.setText("");
		});

	}

	Transition transition;
	boolean goLeft = true;

	private void initTranslateTransition() {

		transition = new Transition() {
			double xStart = list.getLayoutX();
			double xEnd = treeState.getLayoutX() - 1;
			{
				setCycleDuration(Duration.millis(750));
			}

			@Override
			protected void interpolate(double frac) {
				if (!Double.isNaN(frac)) {
					double value;
					if (goLeft) {
						value = xStart - (xStart - xEnd) * frac;
						list.setLayoutX(value);

					} else {
						value = xEnd + (xStart - xEnd) * frac;
						list.setLayoutX(value);
					}
					anchorRoot.setPrefWidth(value + list.getWidth() + xEnd);
					stage.sizeToScene();
				}
			}
		};

		transition.setOnFinished((e) -> {
			goLeft = !goLeft;
		});

	}

	public static TreeItem<Type> category, queue;

	@SuppressWarnings("unchecked")
	public void initTreeView() {

		// R.LoadTreeItems();

		category = new TreeItem<>(new Type("Downloads"));
		category.setExpanded(true);
		category.getChildren().addAll(Type.categoryList);

		queue = new TreeItem<>(new Type("Queues"));
		queue.setExpanded(true);
		queue.getChildren().addAll(Type.queueList);

		TreeItem<Type> root = new TreeItem<>();
		root.setExpanded(true);
		root.getChildren().addAll(category, queue);

		if (treeView.getRoot() != null)
			treeView.getRoot().getChildren().clear();
		treeView.setRoot(root);

		initTreeContext();

		TreeItem<GState> state = new TreeItem<>();
		for (GState downState : GState.values()) {
			state.getChildren().add(new TreeItem<GState>(downState));
		}
		state.setExpanded(true);
		treeState.setRoot(state);

	}

	void initTreeContext() {
		treeView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

			@Override
			public void handle(ContextMenuEvent event) {
				Type i = treeView.getSelectionModel().getSelectedItem()
						.getValue();
				int x = contextMenuTree.getItems().size();
				if (i.id == 0) {
					// menu

					for (int j = 0; j < x; j++) {
						contextMenuTree.getItems().get(j).setVisible(false);
					}
					contextMenuTree.getItems().get(4).setVisible(true);
					contextMenuTree.getItems().get(x - 1).setVisible(true);

				} else if (i.id == 1) {
					// category
					for (int j = 0; j < 5; j++) {
						contextMenuTree.getItems().get(j).setVisible(true);
					}
					for (int j = 5; j < x; j++) {
						contextMenuTree.getItems().get(j).setVisible(false);
					}

				} else if (i.id == 2) {
					// queue
					for (int j = 0; j < 5; j++) {
						contextMenuTree.getItems().get(j).setVisible(false);

					}
					for (int j = 5; j < x; j++) {
						contextMenuTree.getItems().get(j).setVisible(true);
					}

				}

			}
		});
	}

	void initSelectTreeProperty() {
		treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treeView.selectionModelProperty().get().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {

					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {

						Type type = treeView.selectionModelProperty().get()
								.getSelectedItem().getValue();

						if (type == null || type.getName().equals("Downloads")
								|| type.getName().equals("Queues"))
							list.setItems(DownList.AnchorList);
						else {
							ObservableList<Link> obvDown = DownList
									.downType(type);
							list.setItems(DownList.getAnchor(obvDown));
						}

					}
				});

		treeState.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treeState.selectionModelProperty().get().selectedIndexProperty()
				.addListener((ob, ol, nw) -> {
					ItemStatus st = ItemStatus.values()[nw.intValue()];
					if (st.equals(ItemStatus.INIT_DOWNLOAD)) {
						list.setItems(DownList.AnchorList);
						return;
					}
					ObservableList<Link> obvDown = DownList.downState(st);
					list.setItems(DownList.getAnchor(obvDown));
				});
	}

	FileChooser chooser = new FileChooser();
	DirectoryChooser dirChooser = new DirectoryChooser();

	@FXML
	void exportToEF2IDM(ActionEvent event) {

		ObservableList<AnchorPane> list = getSelectedItems();
		if (list.isEmpty())
			return;

		chooser.setInitialDirectory(new File(R.UserHome));
		chooser.setInitialFileName("links.ef2");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("IDM Url", "ef2"));
		chooser.setTitle("Save Links to IDM (.ef2) file");
		File file = chooser.showSaveDialog(null);
		if (file == null)
			return;
		String data = "";
		for (AnchorPane anchorPane : list) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			data += link.getUrl().toIDMString() + "\n";
		}

		try {
			FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void exportToJsonFile(ActionEvent event) {
		ObservableList<AnchorPane> list = getSelectedItems();
		if (list.isEmpty())
			return;
		chooser.setInitialDirectory(new File(R.UserHome));
		chooser.setInitialFileName("links.ariafx");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("Ariafx File", ".json", ".ariafx"));
		chooser.setTitle("Export Links to file");
		File file = chooser.showSaveDialog(stage);
		if (file != null) {
			Item[] data = new Item[list.size()];
			int j = 0;
			for (AnchorPane anchorPane : list) {
				int i = DownList.AnchorList.indexOf(anchorPane);
				Link link = DownList.DownloadList.get(i);
				data[j++] = link.getItem();
			}
			TempItems tempItems = new TempItems(data);
			Utils.toJsonFile(file.getPath(), tempItems);
		}
	}


	@FXML
	void exportToTextFile(ActionEvent event) {
		ObservableList<AnchorPane> list = getSelectedItems();
		if (list.isEmpty())
			return;

		chooser.setInitialDirectory(new File(R.UserHome));
		chooser.setInitialFileName("links.txt");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("Text Links",
				".txt"));
		chooser.setTitle("Save Links to Text file");
		File file = chooser.showSaveDialog(null);
		if (file == null)
			return;
		String data = "";
		for (AnchorPane anchorPane : list) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			data += link.getUrl().getURL() + "\n";
		}

		try {
			FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void importFromEf2IDM(ActionEvent event) {
		chooser.setInitialDirectory(new File(R.UserHome));
		chooser.setInitialFileName("links.ef2");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("IDM Url", "ef2"));
		chooser.setTitle("Open Links from IDM file");
		File file = chooser.showOpenDialog(null);
		if (file == null)
			return;

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				List<Url> urls = Url.fromEf2IDMFile(file);
				for (Url url : urls) {
					System.out.println(url);
					Link link = null;
					try {
						link = new Link(url.url, url.referer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					link.setDownState(ItemStatus.INIT_DOWNLOAD);
					link.retrieveInfo();
					DownList.initGUI(link);
					link.updateProgressShow();
				}
				return null;
			}
		};

		Platform.runLater(task);

	}

	@FXML
	void importFromJsonFile(ActionEvent event) {
		chooser.setInitialDirectory(new File(R.UserHome));
		chooser.setInitialFileName("links.txt");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("Ariafx File", ".json", ".ariafx"));
		chooser.setTitle("Open Links from Ariafx (.json/.ariafx) file");
		File file = chooser.showOpenDialog(null);
		if (file == null)
			return;
		Platform.runLater(() ->{
			TempItems items = Utils.fromJson(file.getPath(), TempItems.class);
			if (Objects.isNull(items) || Objects.isNull(items.getItems()) || items.getItems().length == 0){
				return;
			}
			for (Item item : items.getItems()) {
				Download download = new Download(item);
				DownList.initGUI(download);
			}
		});
	}

	@FXML
	void importFromTextFile(ActionEvent event) {
		chooser.setInitialDirectory(new File(R.UserHome));
		chooser.setInitialFileName("links.txt");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("Text file", ".txt"));
		chooser.setTitle("Open Links from Text file");
		File file = chooser.showOpenDialog(null);
		if (file == null)
			return;
		Platform.runLater(new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				List<Url> urls = Url.fromTextFile(file);
				for (Url url : urls) {
					Link link = new Link(url.url);
					link.retrieveInfo();
					DownList.initGUI(link);
				}
				return null;
			}
		});
	}

	public ObservableList<AnchorPane> getSelectedItems() {
		return list.getSelectionModel().getSelectedItems();
	}

	@FXML
	void addNewDownload(ActionEvent event) {
		FeatchURL.AddURL();
	}

	@FXML
	void startDownload(ActionEvent event) {
		resumeDownloadFile(event);
	}

	@FXML
	void resumeDownloadFile(ActionEvent event) {
		for (AnchorPane anchorPane : getSelectedItems()) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			
			 if(link.isRunning()){
				 continue;
			 }
			
			if (link.getState() == State.READY) {
				link.start();
			} else if (link.getState() == State.FAILED) {
				link.restart();
			} else if (link.getState() == State.CANCELLED) {
				link.restart();
			} else if (link.getState() == State.SUCCEEDED) {
				if (link.getDownloaded() < link.getLength())
					link.restart();
				 else if(link.getDownState() == ItemStatus.FAILED){
					 link.restart();
				 }

			} else if (link.getState() == State.RUNNING) {
				// link.start();
			}
		}
	}

	@FXML
	void pauseAllSelected(ActionEvent event) {
		for (AnchorPane anchorPane : getSelectedItems()) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			if (link.isRunning()) {
				link.cancel();
			}
		}
	}

	@FXML
	void pauseAllDownload(ActionEvent event) {
		DownList.pauseAllDownload();
	}

	@FXML
	void deleteSelectedDownload(ActionEvent event) {
		ObservableList<AnchorPane> panes = getSelectedItems();
		for (int i = panes.size(); i >= 0; i--) {
			DownList.removeFromList(panes.get(i));
		}
	}

	@FXML
	void deleteAllComplete(ActionEvent event) {
		for (int i = DownList.DownloadList.size() - 1; i >= 0; i--) {
			Link link = DownList.DownloadList.get(i);
			if (link.getDownState().equals(ItemStatus.COMPLETE)) {
				// if (download.isRunning()) {
				// download.cancel();
				// }
				try {
					link.getItem().clearCache();
				} catch (Exception e) {
					// TODO: handle exception
				}
				DownList.AnchorList.remove(i);
				DownList.DownloadList.remove(i);
			}
		}

	}

	@FXML
	void showQueueManger(ActionEvent event) {

	}

	@FXML
	void startDownloadMainQueue(ActionEvent event) {

	}

	@FXML
	void startDownloadSecondQueue(ActionEvent event) {

	}

	@FXML
	void stopDownloadMainQueue(ActionEvent event) {

	}

	@FXML
	void stopDownloadSecondQueue(ActionEvent event) {

	}

	@FXML
	void showMinimalView(ActionEvent event) {
		MenuItem item = (MenuItem) event.getSource();
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				if (isMinimal) {
					for (Item2Gui gui : DownList.guis) {
						gui.fromMin2Item();
					}
					item.setText("Show Minimal View");
				} else {
					for (Item2Gui gui : DownList.guis) {
						gui.fromItem2Min();
					}
					item.setText("Hide Minimal View");
				}
				isMinimal = !isMinimal;
				return null;
			}
		};
		Platform.runLater(task);
	}

	@FXML
	void hideTreeView(ActionEvent event) {

		MenuItem item = (MenuItem) event.getSource();
		if (goLeft) {
			transition.play();
			item.setText("Show Category/Queue");

			hBoxHeader.getChildren().removeAll(deletButton, deletCompButton);
		} else {
			transition.play();
			item.setText("Hide Category/Queue");
			hBoxHeader.getChildren().add(4, deletButton);
			hBoxHeader.getChildren().add(5, deletCompButton);
		}

	}

	@FXML
	void showAboutWindows(ActionEvent event) {
		About.showAbout();
	}

	@FXML
	void showOptionsWindows(ActionEvent event) {
		Control.ShowControlWindow();
	}

	@FXML
	void browseSelectedCategory(ActionEvent event) {

	}

	@FXML
	void showSelectedCategoryInManger(ActionEvent event) {

	}

	@FXML
	void deleteSelectedCategory(ActionEvent event) {

	}

	@FXML
	void addNewCategory(ActionEvent event) {
		AddCategory.addCategory();
	}

	@FXML
	void startDownloadSelectedQueue(ActionEvent event) {

	}

	@FXML
	void stopDownloadSelectedQueue(ActionEvent event) {

	}

	@FXML
	void showSelectedQueueInManger(ActionEvent event) {

	}

	@FXML
	void deleteSelectedQueue(ActionEvent event) {

	}

	@FXML
	void addNewQueue(ActionEvent event) {
		AddQueue.addQueue();
	}

	@FXML
	void addGoogleDriveLink(ActionEvent event) {

	}

	@FXML
	void minimizeProgram(ActionEvent event) {
		stage.setIconified(true);
	}

	@FXML
	void closeProgram(ActionEvent event) {
		stage.close();
	}
	
	@FXML
	void exitPrograme(ActionEvent event) {
		Ariafx.Exit();
	}
	
	/*** Context Menu ***/

	@FXML
	void addLink(ActionEvent event) {
		FeatchURL.AddURL();
	}

	@FXML
	void copyURL(ActionEvent event) {
		
		final ClipboardContent content = new ClipboardContent();
		AnchorPane anchorPane = list.getSelectionModel().getSelectedItem();
		int i = DownList.AnchorList.indexOf(anchorPane);
		Link link = DownList.DownloadList.get(i);
		content.putString(link.getURL());
		Clipboard.getSystemClipboard().setContent(content);
	}

	

	@FXML
	void pauseDownload(ActionEvent event) {
		for (AnchorPane anchorPane : getSelectedItems()) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			if (link.isRunning()) {
				link.cancel();
			}
		}
		
	}

	@FXML
	void openFile(ActionEvent event) {
		for (AnchorPane anchorPane : getSelectedItems()) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			File file = new File(link.getSaveto());
			if(file.exists()){
				R.openInProcess(file.getAbsolutePath());
			}else{
				R.info("file not exists yet.");
				// show message for user
			}
		}
		
		
	}

	@FXML
	void openFolder(ActionEvent event) {
		for (AnchorPane anchorPane : getSelectedItems()) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			File file = new File(link.getSaveto());
			R.openInProcess(file.getParent());
		}
		
	}

	@FXML
	void deleteFile(ActionEvent event) {
		for (AnchorPane anchorPane : getSelectedItems()) {
			int i = DownList.AnchorList.indexOf(anchorPane);
			Link link = DownList.DownloadList.get(i);
			FileUtils.deleteQuietly(new File(link.getSaveto()));
		}
		
	}

	LinkProperties prop;
	@FXML
	void showProperties(ActionEvent event) {
		AnchorPane anchorPane = list.getSelectionModel().getSelectedItem();
		int i = DownList.AnchorList.indexOf(anchorPane);
		Link link = DownList.DownloadList.get(i);
		if(prop == null){
			prop = new LinkProperties(link);
			prop.initLink();
			prop.initLinkPropertyOnce();
		}else{
			prop.setLink(link);
			prop.initLink();
		}
		prop.show();
		
	}

}
