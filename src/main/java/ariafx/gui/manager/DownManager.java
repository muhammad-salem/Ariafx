package ariafx.gui.manager;

import ariafx.core.download.Link;
import ariafx.gui.fxml.AriafxMainGUI;
import ariafx.gui.fxml.Item2Gui;
import ariafx.gui.fxml.control.DownUi;
import ariafx.opt.R;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DownManager {

    /*---------------------------------STATIC------------------------------------*/

    public static int TryCount = 8;
    static int count = 0;

    public static void IniyDownUi(Link link) {
        DownUi ui = new DownUi(count++, link);
        FXMLLoader loader = new FXMLLoader(DownUi.Fxml);
        loader.setController(ui);
        try {
            AnchorPane anchorPane = loader.load();
            ui.setScene(anchorPane);
        } catch (Exception e) {
            Logger.getLogger(ui.getClass().getName()).log(Level.WARNING, "", e);
        }
        R.Save_Changes_Progress();
    }


    public static void bindItem2Gui(Link link) {
        try {
            Item2Gui gui = new Item2Gui(link);
            FXMLLoader loader;
            if (AriafxMainGUI.isMinimal) {
                loader = new FXMLLoader(Item2Gui.FXMLmin);
            } else {
                loader = new FXMLLoader(Item2Gui.FXML);
            }
            loader.setController(gui);
            AnchorPane pane = loader.load();
            DownList.AddGuiToList(pane, link);
        } catch (Exception e) {
            System.err.println("error in loading fxml file.\n"
                    + Item2Gui.FXML.toString());
        }
        R.Save_Changes_Progress();
    }

    public static void pauseLinks(Link... links) {
        for (Link link : links) {
            if (link.isRunning()) {
                link.cancel();
            }
        }
    }


    public static void ONFalidState(Link link) {
//    	link.downStateProperty().addListener(new ChangeListener<DownState>() {
//    		int tryNum = 0;
//			public void restartDownload(){
//				if(tryNum < TryCount){
//					link.restart();
//					++tryNum;
//				}
//			}
//			@Override
//			public void changed(
//					ObservableValue<? extends DownState> observable,
//					DownState oldValue, DownState newValue) {
//				if(newValue == DownState.Pause ){
//					
//					if (link.getItem().downloaded > link.getItem().length) {
//						if (link.getItem().isUnknowLength()) {
//							restartDownload();
//							if(tryNum == TryCount){
//								tryNum = 0;
//								return;
//							}
//						}
//					}
//				}
//				
//			}
//		});
        link.onFailedProperty().addListener(new ChangeListener<EventHandler<WorkerStateEvent>>() {
            int tryNum = 0;

            public void restartDownload() {
                if (tryNum < TryCount) {
                    link.restart();
                    ++tryNum;
                }
            }

            @Override
            public void changed(
                    ObservableValue<? extends EventHandler<WorkerStateEvent>> observable,
                    EventHandler<WorkerStateEvent> oldValue,
                    EventHandler<WorkerStateEvent> newValue) {

                restartDownload();
                if (tryNum == TryCount) {
                    tryNum = 0;
                    return;
                }

            }
        });
    }

    public static void ONFalidState() {
        for (Link link : DownList.DownloadList) {
            if (link.getState() == State.SUCCEEDED) {
                if (link.getItem().isCopied)
                    continue;
            }
            ONFalidState(link);
        }
    }


}
