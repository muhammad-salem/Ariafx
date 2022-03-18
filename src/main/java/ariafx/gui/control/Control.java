package ariafx.gui.control;

import ariafx.core.download.ProxySetting.ProxyConfig;
import ariafx.core.download.ProxySetting.ProxyType;
import ariafx.gui.fxml.imp.MovingStage;
import ariafx.opt.R;
import ariafx.opt.Setting;
import ariafx.opt.Utils;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Control {
    public static Control control = null;
    Stage stage = new Stage(StageStyle.UNDECORATED);

    Control() {
        ShowControl();
    }

    public static void ShowControlWindow() {
        if (control == null) {
            control = new Control();
        }
        if (!control.stage.isShowing()) {
            control.stage.show();
        } else {
            control.stage.hide();
        }
    }

    public void ShowControl() {

        //ControlWin win = new ControlWin();
        ControlStage win = new ControlStage();
        FXMLLoader loader = new FXMLLoader(win.FXML);
        loader.setController(win);
        AnchorPane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(pane));
        stage.setAlwaysOnTop(true);
        stage.setTitle("Ariafx .. Control ...");
        MovingStage.pikeToMoving(stage, pane);
    }

    public class ControlStage implements Initializable {
        public URL FXML;
        String[] lines = null;
        Button browserTempButton = null;
        TranslateTransition yTrans;
        @FXML
        private VBox vbox;
        @FXML
        private Button browserChrome;
        @FXML
        private Button browserFirefox;
        @FXML
        private ImageView browserImage;
        @FXML
        private TextArea browserSupport;
        @FXML
        private Slider browserSlider;
        @FXML
        private Button defalutPathChangeButton;
        @FXML
        private TextField defalutPathEdit;
        @FXML
        private Slider autoSaveSlider;
        @FXML
        private RadioButton noProxy;
        @FXML
        private RadioButton useSystemProxySetting;
        @FXML
        private RadioButton useManualProxyConfiguration;
        @FXML
        private VBox manualVBox;
        @FXML
        private ChoiceBox<ProxyType> proxyTypeBox;
        @FXML
        private TextField remoteAddress;
        @FXML
        private TextField remotePort;
        @FXML
        private RadioButton automaticProxyConfiguration;
        @FXML
        private TextField automaticProxyConfigurationURL;
        public ControlStage() {
            if (FXML == null) {
                FXML = getClass().getResource("control.fxml");
            }
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            yTrans = new TranslateTransition(Duration.millis(500), vbox);
            ChangeListener<Number> listener = (obs, old, value) -> {
                initBrowserSliderWork(value.intValue());
            };


            browserSlider.setMin(1);
            browserSlider.setValue(1);
            browserSlider.setShowTickMarks(true);
            browserSlider.setSnapToTicks(true);
            browserSlider.valueProperty().addListener(listener);
//			browserSlider.maxProperty().addListener(listenerMax);

            browserTempButton = browserChrome;
            browserChrome.setOnAction((e) -> {
                initBrowserSliderForChrome();
            });
            initBrowserSliderForChrome();

            browserFirefox.setOnAction((e) -> {
                initBrowserSliderForFirefox();
            });

            autoSaveSlider.setValue(Setting.getTime_to_save());
            autoSaveSlider.valueProperty().addListener((obs, old, value) -> {
                Setting.setTime_to_save(value.doubleValue());
            });


            defalutPathEdit.setText(R.DefaultPath);
            defalutPathChangeButton.setOnAction((e) -> {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setInitialDirectory(new File(R.DefaultPath));
                chooser.setTitle("Select default directory:");
                File file = chooser.showDialog(stage);
                if (file == null) return;
                R.DefaultPath = file.getAbsolutePath();
                defalutPathEdit.setText(R.DefaultPath);
            });


            //remotePortHTTP.setText("8080");
            loadProxyConfig();

            //noProxy.setSelected(true);
            ToggleGroup groupProxy = new ToggleGroup();
            noProxy.setToggleGroup(groupProxy);
            useSystemProxySetting.setToggleGroup(groupProxy);
            useManualProxyConfiguration.setToggleGroup(groupProxy);
            automaticProxyConfiguration.setToggleGroup(groupProxy);

            proxyTypeBox.getItems().add(ProxyType.HTTP);
            proxyTypeBox.getItems().add(ProxyType.HTTPS);
            proxyTypeBox.getItems().add(ProxyType.SOCKS);
			proxyTypeBox.valueProperty().addListener((observable, oldValue, newValue) -> Setting.SetProxyType(newValue));


            groupProxy.selectedToggleProperty().addListener((e) -> {
                if (noProxy.isSelected()) {
                    Setting.USE_No_Proxy();
                    manualVBox.setDisable(true);
                } else if (useSystemProxySetting.isSelected()) {
                    Setting.USE_System_Proxy();
                    manualVBox.setDisable(true);
                } else if (useManualProxyConfiguration.isSelected()) {
                    //ProxySetting.Use_Manual_Proxy_Settings(remoteAddressHTTP.getText(), remotePortHTTP.getText());
                    Setting.Use_Manual_Proxy_Settings(
                            remoteAddress.getText(),
                            Integer.parseInt(remotePort.getText()),
                            proxyTypeBox.getValue()
					);
                    manualVBox.setDisable(false);
                } else if (automaticProxyConfiguration.isSelected()) {
                    Setting.Use_AutoConfiguration_Proxy(automaticProxyConfigurationURL.getText());
                    manualVBox.setDisable(true);
                }
            });


        }

        private void loadProxyConfig() {
            ProxyConfig conf = Setting.getProxySetting().getProxyConfig();
            switch (conf) {
                case NoProxy:
                    noProxy.setSelected(true);
                    manualVBox.setDisable(true);
                    break;
                case SystemProxy:
                    useSystemProxySetting.setSelected(true);
                    manualVBox.setDisable(true);
                    break;
                case AutoConfigProxy:
                    automaticProxyConfiguration.setSelected(true);
                    manualVBox.setDisable(true);
                    break;
                case ManualProxy:
                    useManualProxyConfiguration.setSelected(true);
                    manualVBox.setDisable(false);
                    ProxyType pty = Setting.GetProxyType();
                    proxyTypeBox.setValue(pty);
                    remoteAddress.setText(Setting.GetRemoteAddress());
                    remotePort.setText(String.valueOf(Setting.GetRemotePort()));

            }
        }


        public void initBrowserSliderWork(int v) {
            if ((v - 1) < lines.length)
                browserSupport.setText(lines[v - 1]);
            try {
				Image image;
                if (browserChrome.equals(browserTempButton)) {
					image = new Image(getClass().getResourceAsStream("img/linux-chrome" + v + ".png"));
                } else if (browserFirefox.equals(browserTempButton)) {
                    image = new Image(getClass().getResourceAsStream("img/linux-firefox" + v + ".png"));
                } else {
					return;
				}
				browserImage.setImage(image);
            } catch (Exception e) {

            }

        }

        public void initBrowserSliderForChrome() {
            browserSlider.setMax(4);
            browserTempButton = browserChrome;
            lines = Utils.getStringFromInputStream(this.getClass().getResourceAsStream("chrome-linux.txt")).split("\n");
            browserSlider.setValue(1);
            initBrowserSliderWork(1);
        }

        public void initBrowserSliderForFirefox() {
            browserSlider.setMax(7);
            browserTempButton = browserFirefox;
            lines = Utils.getStringFromInputStream(this.getClass().getResourceAsStream("firefox-linux.txt")).split("\n");
            browserSlider.setValue(1);
            initBrowserSliderWork(1);
        }


        @FXML
        void showBrowser(ActionEvent event) {
            yTrans.setToY(0);
            yTrans.play();
        }

        @FXML
        void showControlPanel(ActionEvent event) {
            yTrans.setToY(-500);
            yTrans.play();
        }

        @FXML
        void showProxySetting(ActionEvent event) {
            yTrans.setToY(-1000);
            yTrans.play();
        }


        private void saveProxyConfig() {

            if (noProxy.isSelected()) {
                Setting.USE_No_Proxy();
            } else if (useSystemProxySetting.isSelected()) {
                Setting.USE_System_Proxy();
            } else if (automaticProxyConfiguration.isSelected()) {
                Setting.Use_AutoConfiguration_Proxy(automaticProxyConfigurationURL.getText());
            } else if (useManualProxyConfiguration.isSelected()) {
                Setting.Use_Manual_Proxy_Settings(
                        remoteAddress.getText(),
                        Integer.parseInt(remotePort.getText()),
                        proxyTypeBox.getValue());
            }

        }


        @FXML
        void minimize(ActionEvent event) {
            stage.setIconified(true);
        }

        @FXML
        void close(ActionEvent event) {
            //save();
            saveProxyConfig();
            stage.close();
        }


        @FXML
        void reloadAutomaticProxyConfiguration(ActionEvent event) {

        }


    }


}
