package ariafx.gui.fxml.control;

import ariafx.core.download.Link;
import ariafx.core.url.type.Category;
import ariafx.core.url.type.Queue;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.gui.fxml.imp.ProgressStyled;
import ariafx.gui.fxml.imp.ProgressStyled.StyleProgress;
import ariafx.opt.R;
import ariafx.opt.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class LinkProperties implements Initializable {

    public static URL FXML;
    Link link;
    Stage stage;
    @FXML
    private Text name;
    @FXML
    private TextField textFieldLogin;
    @FXML
    private Text dateModified;
    @FXML
    private Label labelFileSize;
    @FXML
    private CheckBox checkBoxUseAuthorization;
    @FXML
    private TextField textFieldReferer;
    @FXML
    private ProgressStyled progressBarDownloaded;
    @FXML
    private Text dateAdd;
    @FXML
    private TextField textFieldSaveFileTo;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private TextField textFieldAddress;
    @FXML
    private Hyperlink hyperLinkReferer;
    @FXML
    private ChoiceBox<Queue> choicBoxQueue;
    @FXML
    private ChoiceBox<Category> choicBoxCatogree;

    public LinkProperties(Link link) {
        this.link = link;
        stage = new Stage(StageStyle.UNDECORATED);
        stage.setTitle(link.getFilename());

        if (FXML == null) {
            FXML = getClass().getResource("LinkProperties.xml");
        }

        FXMLLoader loader = new FXMLLoader(FXML);
        loader.setController(this);
        try {
            AnchorPane pane = loader.load();
            stage.setScene(new Scene(pane));
            MovingStage.pikeToMoving(stage, pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public void initLinkPropertyOnce() {

    }

    public void initLink() {
        name.setText(link.getFilename());
        textFieldAddress.setText(link.getURL());

        choicBoxCatogree.setItems(Category.getCategores());
        choicBoxCatogree.getSelectionModel().select(Category.get(link.getCategory()));


        choicBoxQueue.setItems(Queue.getQueuesTree());
        choicBoxQueue.getSelectionModel().select(Queue.getQueue(link.getQueue()));


        textFieldSaveFileTo.setText(link.getSaveto());

        dateAdd.setText(new Date(link.getAdded()).toString());
        dateModified.setText(new Date(link.getLastTry()).toString());

        progressBarDownloaded.progressProperty().bind(link.progressProperty());
        initProgress();
        labelFileSize.setText(Utils.percent(link.getDownloaded(),
                link.getLength())
                + " "
                + Utils.fileLengthUnite(link.getDownloaded())
                + " of " + Utils.fileLengthUnite(link.getLength())
        );

        hyperLinkReferer.setText(link.getReferer());
        textFieldReferer.setText(link.getReferer());

    }

    public void show() {
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initProgress() {

        switch (link.getState()) {
            case READY:
            case SCHEDULED:
                progressBarDownloaded.setStyleClass(StyleProgress.Red);
                break;
            case CANCELLED:
                progressBarDownloaded.setStyleClass(StyleProgress.Grey);
                break;
            case RUNNING:
                progressBarDownloaded.setStyleClass(StyleProgress.Green);
                break;
            case SUCCEEDED: {
                if (link.getDownloaded() == link.getLength()) { // in success
                    progressBarDownloaded.setStyleClass(StyleProgress.Green);
                } else if (link.getDownloaded() > link.getLength()) { // in
                    // faild
                    if (link.getItem().isUnknowLength()) {
                        progressBarDownloaded.setStyleClass(StyleProgress.Blue_Grey);
                    } else {
                        progressBarDownloaded.setStyleClass(StyleProgress.Green);
                    }

                } else { // still in pause

                    progressBarDownloaded.setStyleClass(StyleProgress.Grey);
                }
            }
            break;
            case FAILED:
                progressBarDownloaded.setStyleClass(StyleProgress.Grey);
                break;

            default:
                break;

        }

    }

    @FXML
    void changeSaveFileTo(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(link.getFilename());
        chooser.setTitle("Save " + link.getFilename() + " to:");
        File file = new File(link.getSaveto());
        chooser.setInitialDirectory(file.getParentFile());
        file = chooser.showSaveDialog(null);
        if (file != null) {
            textFieldSaveFileTo.setText(file.getAbsolutePath());
            link.setSaveto(file.getAbsolutePath());
            link.setFilename(FilenameUtils.getName(link.getSaveto()));
            stage.setTitle(link.getFilename());
        }
    }

    @FXML
    void addNewCatogree(ActionEvent event) {
        AddCategory.addCategory();
    }

    @FXML
    void addNewQueue(ActionEvent event) {
        AddQueue.addQueue();
    }

    @FXML
    void openFolder(ActionEvent event) {
        File file = new File(link.getSaveto());
        R.openInProcess(file.getParent());
    }

    @FXML
    void minimizeProgram(ActionEvent event) {
        stage.setIconified(true);
    }

    @FXML
    void closeStage(ActionEvent event) {
        stage.close();
    }

}
