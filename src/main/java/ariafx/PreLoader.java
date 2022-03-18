package ariafx;

import ariafx.about.About;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.gui.fxml.imp.ProgressStyled;
import ariafx.opt.R;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Simple PreLoader Using the ProgressBar Control
 *
 * @author salem
 */
public class PreLoader extends Application {

    public Stage stage;
    AnchorPane anchor;
    Service<Void> service;
    Pre pre;

    public PreLoader() {
        initService();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.getIcons().add(new Image(getClass().getResource("aria.png").openStream()));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Initializing " + About.App_Name);

        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("pre-loader.xml"));
            pre = new Pre();
            loader.setController(pre);
            anchor = loader.load();
        } catch (Exception e) {
            System.err.println("error loading pre-loader.xml");
        }

        stage.setScene(new Scene(anchor));
        stage.show();
        MovingStage.pikeToMoving(stage, anchor);

    }

    public boolean initService() {
        service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {

                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        R.InitiDirs();
                        pre.setProgress(0.2);
                        pre.sendMessage("Creating Directories");
                        Thread.sleep(350);

                        R.ReadSetting();
                        pre.setProgress(0.44);
                        pre.sendMessage("Reading saved Setting");
                        Thread.sleep(350);

                        //R.initFileLock();
                        //AppInstance.initFileLock();    // had been run in the main thread

                        pre.setProgress(0.50);
                        pre.sendMessage("Init File Lock");
                        Thread.sleep(450);

                        R.InitNewCategores();
                        R.InitLogger();
                        pre.setProgress(0.55);
                        pre.sendMessage("Adding new Categories");
                        pre.sendMessage("Init Logger");
                        Thread.sleep(450);

                        R.InitNewQueue();
                        pre.setProgress(0.73);
                        pre.sendMessage("Adding new queues");
                        Thread.sleep(350);

                        // R.ReadDownloads(); //need fx thread
                        pre.setProgress(0.84);
                        pre.sendMessage("URLs Initialization");
                        Thread.sleep(350);

                        R.LoadTreeItems();
//						pre.setProgress(1.0);
                        pre.sendMessage("Start " + About.App_Name + "....");
                        Thread.sleep(350);

                        Platform.runLater(() -> {
                            R.ReadDownloads();
                        });
                        pre.setProgress(1.0);

                        return null;
                    }
                };
            }
        };

        return true;
    }

    public void setOnSucceeded(EventHandler<WorkerStateEvent> e) {
        service.setOnSucceeded(e);
    }

    public void close() {
        stage.close();
    }

    public class Pre implements Initializable {

        Transition transition, animation;
        @FXML
        private ProgressStyled progress;
        @FXML
        private Label title;
        @FXML
        private Text appVersion;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            sendMessage("initialization process");
            appVersion.setText(About.App_Version);
        }

        public void sendMessage(String str) {
            animation = new Transition() {

                {
                    setCycleDuration(Duration.millis(250));
                    setCycleCount(1);
                }

                @Override
                protected void interpolate(double frac) {
                    if (!Double.isNaN(frac)) {
                        // from anchorVB2
                        int length = str.length();
                        int n = Math.round(length * (float) frac);
                        title.setText(str.substring(0, n));
                    }

                }
            };
            animation.play();
        }

        public void setProgress(double value) {
            transition = new Transition() {
                final double from = progress.getProgress();
                final double inc = value - from;

                {
                    setCycleDuration(Duration.millis(350));
                    setCycleCount(1);
                }

                @Override
                protected void interpolate(double frac) {
                    double cal = from + (frac * inc);
                    progress.setProgress(cal);
                }
            };
            transition.play();
        }

    }

}
