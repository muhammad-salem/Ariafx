package ariafx;

import ariafx.about.About;
import ariafx.core.download.Link;
import ariafx.gui.fxml.AriafxMainGUI;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.gui.manager.DownList;
import ariafx.gui.manager.ParameterToLink;
import ariafx.nativemessaging.Chrome;
import ariafx.nativemessaging.ChromeMSG;
import ariafx.notify.Notifier;
import ariafx.opt.AppInstance;
import ariafx.opt.Parameter;
import ariafx.opt.R;
import ariafx.opt.Setting;
import ariafx.tray.TrayUtility;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class Ariafx extends Application {


    public static Stage ui;
    public static boolean silentStartup = false;
    public static Thread shutdownHookThread;
    static String[] param;
    static FileAlterationMonitor monitor;

    public static String[] readChromeMessage() {
        String str = Chrome.readMessage();
        ChromeMSG msg = ChromeMSG.CreateMessage(str);
        return msg.toArgs();
    }

    /**
     * [--url=URL]
     * [--http-referer=REFERER]
     * [--file-name=FNAME]
     * [--user-agent=UA]
     * [--cookie=COOKIE]
     * [--cookie-file=CFILE]
     * [--input-file=UFILE]
     * [--fileSize=int]
     * [--silent]
     *
     * @return
     */
    public static void main(String[] args) throws Exception {

        if (args.length == 1
                && args[0].contains(Chrome.extensions_id)) {
            args = readChromeMessage();
        } else if (args.length == 2
                && args[1].contains(Chrome.extensions_id)) {
            args = readChromeMessage();
        }


        if (AppInstance.isAppInstanceExists()) {
            if (args.length == 1
                    && args[0].contains("--silent")) {
                return;
            } else if (args.length != 0) {
                new Parameter(args);
            }
            Platform.exit();
        } else {
            if (args.length != 0 && args[0].contains("--silent")) {
                silentStartup = true;
            }
            param = args;
            launch(args);
        }

    }

    public static void StopMonitor() {
        try {

            monitor.stop();
        } catch (Exception e) {
            System.out.println("failed to stop monitor... or it had been stopped already");
        }
    }

    public static void showUI() {
        ui.show();
        ui.setAlwaysOnTop(true);
        ui.setIconified(false);
        ui.setAlwaysOnTop(false);
    }

    public static void hideUI() {
        ui.hide();
        ui.setIconified(true);
    }

    public static void hideUITray() {
        ui.hide();
    }

    public static void Exit() {
        ui.close();
        StopMonitor();
        R.SAVE_CHANGES();
        Runtime.getRuntime().removeShutdownHook(shutdownHookThread);
        Platform.setImplicitExit(true);
        Platform.exit();
        System.exit(0);

    }

    @Override
    public void init() throws Exception {
        if (silentStartup) R.InitChanges();
    }

    public void initAfterFX() {
        Platform.runLater(() -> {
            saveStateTimeLine();
            initMonitor();
            // init tray icon
            TrayUtility.InitTray();
            Chrome.CheckNetiveMessage();
            Platform.setImplicitExit(false);
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(
                new Image(getClass().getResource("aria.png").openStream()));

        if (silentStartup) {
            initUI(stage);
            initAfterFX();
            R.ReadDownloads();
        } else {
            Notifier.NotifyNative("Lunch Ariafx", "A Smart Download Manager");
            PreLoader preloader = new PreLoader();
            preloader.start(new Stage());
            preloader.service.start();
            preloader.setOnSucceeded((e) -> {
                preloader.close();
                initUI(stage);
                initAfterFX();
                Notifier.NotifyUser("Ariafx has lunched", "A Smart Download Manager");
            });
        }
        ui = stage;

        shutdownHookThread = new Thread() {
            @Override
            public void run() {
                R.Save_Changes_Progress();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    }

    public void initUI(final Stage stage) {

        try {

            AriafxMainGUI mainGUI = new AriafxMainGUI(stage);
            FXMLLoader loader = new FXMLLoader(AriafxMainGUI.FXML);
            loader.setController(mainGUI);
            AnchorPane pane = loader.load();

            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(About.App_Name);
            stage.setScene(new Scene(pane));

            MovingStage.pikeToMoving(stage, pane);
            if (!silentStartup) {
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveStateTimeLine() {

        Timeline line = new Timeline(new KeyFrame(Duration.minutes(Setting
                .getTime_to_save()), (e) -> {

            for (Link link : DownList.DownloadList) {
                link.bindState();
            }
            R.Save_Changes_Progress();
        }));
        line.setCycleCount(Animation.INDEFINITE);
        line.play();

    }

    @Override
    public void stop() throws Exception {
        Exit();
    }

    public void initMonitor() {
        IOFileFilter fileFilter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".json"));
        IOFileFilter dirFilter = FileFilterUtils.and(
                FileFilterUtils.directoryFileFilter(),
                HiddenFileFilter.VISIBLE);

        IOFileFilter filter = FileFilterUtils.or(fileFilter, dirFilter);

        FileAlterationObserver observer =
                new FileAlterationObserver(new File(R.NewLink), filter);
        observer.addListener(new FileAlterationListener() {

            @Override
            public void onStop(FileAlterationObserver observer) {
            }

            @Override
            public void onStart(FileAlterationObserver observer) {
            }

            @Override
            public void onFileDelete(File file) {
            }

            @Override
            public void onFileCreate(File file) {
                new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() {
                                Parameter param = Parameter.fromJson(file);
                                ParameterToLink.initLink(param);
                                return null;
                            }
                        };
                    }
                }.start();
            }

            @Override
            public void onFileChange(File file) {
            }

            @Override
            public void onDirectoryDelete(File directory) {
            }

            @Override
            public void onDirectoryCreate(File directory) {
            }

            @Override
            public void onDirectoryChange(File directory) {
            }
        });

        monitor = new FileAlterationMonitor(1000, observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (param != null && param.length != 0) {
            Platform.runLater(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Parameter parameter = new Parameter();
                    parameter.setParameter(param);
                    return null;
                }
            });
        }

    }

}
