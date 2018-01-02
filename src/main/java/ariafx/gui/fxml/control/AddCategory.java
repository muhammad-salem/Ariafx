package ariafx.gui.fxml.control;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import ariafx.core.url.type.Category;
import ariafx.core.url.type.Type;
import ariafx.gui.fxml.AriafxMainGUI;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.opt.R;
import ariafx.opt.Setting;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AddCategory {
	
	Stage stage;
    public Stage getStage() {
		return stage;
	}
	public void setStage(Stage stage) {
		this.stage = stage;
		this.stage.setTitle("Add Category");
	}

	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField saveDir;

    @FXML
    private TextField extension;

    @FXML
    private TextField name;

    @FXML
    void browseSaveToPath(ActionEvent event) {
    	DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("create new Category");
		File file = new File(saveDir.getText());
		chooser.setInitialDirectory(file);
		file = chooser.showDialog(stage);
		if (file != null) {
			saveDir.setText(file.getPath()+ File.separator + name.getText());
		}
		chngDir = true;
		tempDir = saveDir.getText();
    }
    
    boolean checkPramter(){
    	if (name.getText().equals("")) {
			return false;
		}
    	
    	if (saveDir.getText().equals("")) {
			return false;
		}
    	
    	return true;
    }

    @FXML
    void addNewCategoryAction(ActionEvent event) {
    	if(checkPramter()){
    		try {
        		File file = new File(saveDir.getText());
            	file.mkdirs();
            	
            	Category category = new Category
            			(name.getText(), saveDir.getText(), extension.getText());
            	Category.add(category);
//            	Category.addTreeCategory(category);
            	R.LoadTreeItems();
            	Setting.updateSetting();
            	stage.close();
            	AriafxMainGUI.category.getChildren().add(new TreeItem<Type>(category));
    		} catch (Exception e) {
    			System.err.println(4);
    			Popup popup = new Popup();
    			popup.getContent().add(new Label("can't make directory"));
    			popup.show(stage);
    			e.printStackTrace();
    		}
    	}
    }

    @FXML
    void minimizePrograme(ActionEvent event) {
    	stage.setIconified(true);
    }

    @FXML
    void cancelAction(ActionEvent event) {
    	stage.close();
    }

    boolean chngDir = false;
    String tempDir = "";
    @FXML
    void initialize() {
    	saveDir.setText(R.DefaultPath);
//    	saveDir.setEditable(false);
    	
    	name.textProperty().addListener((obv, old, value)->{
    		if(chngDir) saveDir.setText(tempDir +File.separator + value);
    		else saveDir.setText(R.DefaultPath +File.separator + value);
       });
    }
    
    
    /**-------------------------------- Add Category -----------------------------------------*/
    
    public static void  addCategory() {
    	addCategory(new Stage(StageStyle.UNDECORATED));
    }
    public static void  addCategory(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(AddCategory.class.getResource("addCategory.xml"));
			AnchorPane anchorPane = loader.load();
			AddCategory category = loader.getController();
			stage.setScene(new Scene(anchorPane));
			stage.show();
			category.setStage(stage);
			MovingStage.pikeToMoving(stage, anchorPane);
		} catch (Exception e) {
			
		}
	}
    
}
