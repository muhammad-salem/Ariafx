package ariafx.gui.fxml.control;

import ariafx.core.download.Download;
import ariafx.core.download.Link;
import ariafx.core.url.type.Category;
import ariafx.core.url.type.ItemStatus;
import ariafx.core.url.type.Queue;
import ariafx.gui.fxml.AriafxMainGUI;
import ariafx.gui.fxml.Item2Gui;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.gui.manager.DownList;
import ariafx.gui.manager.DownManager;
import ariafx.opt.R;
import ariafx.opt.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DisplayUrlInfo implements Initializable {

    public static URL FXML;
    private final Stage stage;
    private final Link link;
    /**
     * -------------------------0-----------------------------
     **/


    @FXML
    private ImageView iconFile;
    @FXML
    private Label itemLength;
    @FXML
    private TextField editSavePath;
    @FXML
    private AnchorPane anchor, anchorVB2;
    @FXML
    private TextField linkUrl;
    @FXML
    private ChoiceBox<Category> category;
    @FXML
    private ChoiceBox<Queue> queue;
    @FXML
    private ChoiceBox<Integer> parallelThread;

    public DisplayUrlInfo(Stage stage, Link link) {

        this.link = link;
        this.stage = stage;
        this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.setTitle("Add Link");
        if (FXML == null)
            FXML = getClass().getResource("displayUrlInfo.xml");
    }

    /**
     * -------------------------6-----------------------------
     **/

    public static void AddLink(Link link) {
        Platform.runLater(new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try {
                    Stage ui = new Stage();
                    DisplayUrlInfo info = new DisplayUrlInfo(ui, link);
                    FXMLLoader loader = new FXMLLoader(DisplayUrlInfo.FXML);
                    loader.setController(info);
                    ui.setScene(new Scene(loader.load()));
                    ui.show();
                    MovingStage.pikeToMoving(ui, info.anchor);
                } catch (Exception e) {
                    System.err.println("error in loading fxml file.\n" + DisplayUrlInfo.FXML.toString());
                }
                return null;
            }
        });

    }

    /**
     * -------------------------1-----------------------------
     **/

    @FXML
    void minimize(ActionEvent event) {
        stage.setIconified(true);
    }

    @FXML
    void close(ActionEvent event) {
        if (link != null) {
            link.getItem().clearCache();
        }
        stage.close();
    }

    /**
     * -------------------------2-----------------------------
     **/

    // anchorVB1
    void initAnchorVB1() {
        ObservableList<Integer> listNum = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 10, 16, 20, 24, 32);
        parallelThread.setItems(listNum);
        parallelThread.getSelectionModel().select(Integer.valueOf(Download.ParallelChunks));
        parallelThread.getSelectionModel().select(Integer.valueOf(Utils.guessChunksNum(link.getLength())));
    }

    /**
     * -------------------------3-----------------------------
     **/
    // anchorVB2

    void initAnchorVB2() {

        this.stage.setTitle("Fetch link Info");

        linkUrl.setText(link.getURL());
        itemLength.setText(Utils.fileLengthUnite(link.getLength()));

        // init category and queue
        category.setItems(Category.getCategores());
        category.selectionModelProperty()
                .get().selectedItemProperty()
                .addListener((obs, old, nw) -> {
                    link.setCategory(nw.getName());
                    editSavePath.setText(nw.getSaveTo()
                            + File.separator + link.getFilename());
                    link.setSaveto(editSavePath.getText());
                    if (link.checkConfliectName()) {
                        editSavePath.setText(link.getSaveto());
                    }
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
            if (link.checkConfliectName()) {
                str = link.getSaveto();
                editSavePath.setText(str);
            }
            link.setFilename(FilenameUtils.getName(str));
        });


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
            link.setSaveto(file.getPath());
            link.setFilename(FilenameUtils.getName(file.getPath()));
        }
    }

    @FXML
    void downLater(ActionEvent event) {
        Item2Gui gui = bindILink2Gui();
        if (gui == null) {
            return;
        } else {
            DownList.guis.add(gui);
        }
        stage.close();
        link.updateProgressShow();
        DownManager.IniyDownUi(link);
    }

    @FXML
    void downloadNow(ActionEvent event) {
        link.start();

        Item2Gui gui = bindILink2Gui();
        if (gui == null) {
            return;
        } else {
            DownList.guis.add(gui);
        }
        stage.close();

        DownManager.IniyDownUi(link);
    }

    /**
     * -------------------------4-----------------------------
     **/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAnchorVB1();
        initAnchorVB2();
    }

    /**
     * -------------------------5-----------------------------
     **/


    private Item2Gui bindILink2Gui() {
        Integer part = parallelThread.getValue();
        if (part == 1) link.setStreaming();
        link.setChunksNum(part);

        link.setDownState(ItemStatus.INIT_DOWNLOAD);

        link.callRange();

        Item2Gui gui = null;
        try {
            gui = new Item2Gui(link);
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
            System.err.println("error in loading fxml file.\n" + Item2Gui.FXML.toString());
        }
        R.Save_Changes_Progress();
        return gui;
    }


}
