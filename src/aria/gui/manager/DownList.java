package aria.gui.manager;

import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import aria.core.download.Download;
import aria.core.download.Link;
import aria.core.url.Item;
import aria.core.url.Url;
import aria.core.url.type.Category;
import aria.core.url.type.DownState;
import aria.core.url.type.Queue;
import aria.core.url.type.Type;
import aria.gui.fxml.Item2Gui;
import aria.gui.fxml.AriafxMainGUI;
import aria.opt.Utils;

public final class DownList {
	public static ObservableList<Link> DownloadList = FXCollections
			.observableArrayList();

	public static ObservableList<AnchorPane> AnchorList = FXCollections
			.observableArrayList();

	public static ObservableList<Item2Gui> guis = FXCollections .observableArrayList();
	
	public static boolean removeFromList(AnchorPane anchorPane) {
		int i = AnchorList.indexOf(anchorPane);
		if (i == -1)
			return false;
		Link link = DownloadList.get(i);
		if (link.isRunning())
			link.cancel();
		try {
			link.getItem().clearCache();
		} catch (Exception e) {
		}
		AnchorList.remove(anchorPane);
		return DownloadList.remove(link);
		
	}

	public static void AddGuiToList(AnchorPane pane, Link link) {
		AnchorList.add(pane);
		DownloadList.add(link);
	}

	
	public static int initGUI(Link link) {
		if (link.getItem() == null) {
			System.err.println("No item defind in download obj");
			return -1;
		}
		try {
			Item2Gui gui = new Item2Gui(link);
			FXMLLoader loader;
			if(AriafxMainGUI.isMinimal){
				loader = new FXMLLoader(Item2Gui.FXMLmin);
			}else {
				loader= new FXMLLoader(Item2Gui.FXML);
			}
			loader.setController(gui);
			AnchorPane pane = loader.load();
			AddGuiToList(pane, link);
			guis.add(gui);
			return AnchorList.indexOf(pane);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("DownList.initGUI()");
			System.err.println(link.getItem());
			System.err.println(link.getState());
			System.err.println(link.getDownState());
			
		}
		return -1;
	}

	public static int initGUI(Download download) {
		if (download.getItem() == null) {
			System.err.println("No item defind in download obj");
			return -1;
		}
		Link link = new Link(download.getItem());
		return initGUI(link);
	}
	
	public static int initGUI(Item item) {
		if (item.getURL() == null) {
			System.err.println("No item defind");
			return -1;
		}
		Download download = new Download(item);
		download.start();
		return initGUI(download);
	
	}
	
	public static int initGUI(Url url) {
		return initGUI(new Item(url));
	}
	
	public static int initGUI(String url) {
		if (!Utils.verifyURL(url)) {
			System.err.println("No URL format defind in Item obj");
			return -1;
		}
		Item item;
		try {
			item = new Item(url);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return -1;
		}

		return initGUI(item);
	}

	public static int initGUI(String url, String ref) {
		if (!Utils.verifyURL(url) ) {
			System.err.println("wrong URL or Referrer formate");
			return -1;
		}

		Item item;
		try {
			item = new Item(url, ref);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("no download added");
			return -1;
		}

		return initGUI(item);
	}

	public static int initGUIFromJson(String path) {
		return initGUI(Utils.fromJson(path, Item.class));
	}
	
	public static ObservableList<Link> downType(Type type) {
		Predicate<Link> predicate = new Predicate<Link>() {
			@Override
			public boolean test(Link l) {
				if (l.getCategory().equals(type.getName()))
					return true;
				if (l.getQueue().equals(type.getName()))
					return true;
				return false;
			}
		};
		return DownloadList.filtered(predicate);
	}

	public static ObservableList<Link> downCategory(Category category) {
		Predicate<Link> predicate = new Predicate<Link>() {
			@Override
			public boolean test(Link l) {
				if (l.getCategory().equals(category.getName()))
					return true;
				return false;
			}
		};
		return DownloadList.filtered(predicate);
	}

	public static ObservableList<Link> downQueue(Queue queue) {
		Predicate<Link> predicate = new Predicate<Link>() {

			@Override
			public boolean test(Link l) {
				if (l.getQueue().equals(queue.getName()))
					return true;
				return false;
			}
		};
		return DownloadList.filtered(predicate);
	}

	public static ObservableList<Link> downState(DownState state) {
		Predicate<Link> predicate = new Predicate<Link>() {
			@Override
			public boolean test(Link l) {
				if (l.getDownState().equals(state))
					return true;
				return false;
			}
		};
		return DownloadList.filtered(predicate);
	}

	public static ObservableList<Link> findByTitle(String name) {
		Predicate<Link> predicate = new Predicate<Link>() {
			@Override
			public boolean test(Link l) {
				String[] keys = name.toLowerCase().split(" ");
				String name = l.getFilename().toLowerCase();
				for (String key : keys) {
					if(name.contains(key))
						return true;
				}
				return false;
			}
		};
		return DownloadList.filtered(predicate);
	}

	public static ObservableList<AnchorPane> getAnchor(ObservableList<Link> list) {
		ObservableList<AnchorPane> anchorList = FXCollections
				.observableArrayList();
		for (Link l : list) {
			if (DownloadList.contains(l))
				anchorList.add(AnchorList.get(DownloadList.indexOf(l)));
		}
		return anchorList;
	}

	public static Item[] getItems() {
		Item[] items = new Item[DownloadList.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = DownloadList.get(i).item;
		}
		return items;
	}
	
	public static void saveItemsJson() {
		for (int i = 0; i < DownloadList.size(); i++) {
			DownloadList.get(i).item.toJson();
		}
	}

	public static void pauseAllDownload() {
		for (Link link : DownloadList) {
//			if (link.isRunning())
			if (link.getState() == State.RUNNING)
				link.cancel();
		}
	}

	public static void runAllPausedDownloads() {
		for (Download download : DownloadList) {
			if (download.getDownState().equals(DownState.Pause)) {
				download.reset();
				download.start();
			}
		}
	}

	

}
