package d.gui.manager;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import d.core.download.Link;
import d.gui.fxml.Item2Gui;
import d.gui.fxml.MainFxGet;
import d.gui.fxml.control.DownUi;
import d.opt.R;

public class DownManager {

/*---------------------------------STATIC------------------------------------*/
	
	
    static int count  = 0;
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
    
    
    public static void bindItem2Gui(Link link){
		try {
			Item2Gui gui = new Item2Gui(link);
			FXMLLoader loader ;
			if(MainFxGet.isMinimal){
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
	}
    
    

}
