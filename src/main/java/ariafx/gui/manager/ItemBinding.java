package ariafx.gui.manager;

import ariafx.core.download.Download;
import ariafx.core.download.Link;
import ariafx.core.url.type.Category;
import ariafx.core.url.type.Queue;
import ariafx.gui.fxml.Item2Gui;
import ariafx.gui.fxml.control.DownUi;
import ariafx.opt.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ItemBinding {

    public ItemBinding() {

    }

    public static void bindItem(Download download, Item2Gui gui) {
        //
//		gui.state.textProperty().bind(download.downStateProperty());

        //pane1
        gui.title.setText(download.item.getFilename());
        gui.state.textProperty().bind(download.downStateProperty().asString());        // change to downstate
        gui.url.setText(download.getURL());

        gui.progress.progressProperty().bind(download.progressProperty());
        download.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                if (newValue == null) return;
                long l = download.item.getLength() == -1 ?
                        download.item.getDownloaded() : download.item.getLength();
                gui.percent.setText(Utils.percent(
                        download.item.downloaded, l)
                        + " "
                        + Utils.fileLengthUnite(download.item.downloaded)
                        + " of "
                        + Utils.fileLengthUnite(l));
            }
        });

        gui.speed1.textProperty().bind(download.transferRateProperty());
        //pane3
        gui.speed2.textProperty().bind(download.transferRateProperty());

        download.canCollectSpeedData();
        download.addData(0.0);
        gui.chart.getData().add(download.series);
        gui.ax.setLowerBound(download.upperBound - 60);
        gui.ax.setUpperBound(download.upperBound);

        //pane2
        gui.editSavePath.setText(download.item.getSaveto());
        gui.referrer.setText(download.getReferer());
//		gui.queue.getSelectionModel().select(Queue.getQueue(download.item.queue));
//		gui.category.getSelectionModel().select(Category.get(download.item.category));
        gui.referrer.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                download.setReferer(newValue);
            }
        });
        gui.savePath.textProperty().bind(gui.editSavePath.textProperty());


        gui.progress.progressProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                gui.ax.setLowerBound(download.upperBound - 60);
                gui.ax.setUpperBound(download.upperBound);

//				gui.timeLeft.setText("Time Left: " 
//						+ Utils.calTimeLeft(download.item.timeLeft));
            }
        });
        gui.timeLeft.textProperty().bind(download.lefttimeProperty);

        gui.queue.setItems(Queue.getQueuesTree());
        gui.queue.getSelectionModel()
                .select(Queue.getQueue(download.item.getQueue()));
        gui.category.setItems(Category.getCategores());
        gui.category.getSelectionModel()
                .select(Category.get(download.item.getCategory()));

    }


    public static void bindItem(Link link, Item2Gui gui) {
        //
//		gui.state.textProperty().bind(download.downStateProperty());

        //pane1
        gui.title.setText(link.getFilename());
        gui.state.textProperty().bind(link.downStateProperty().asString());            // change to downstate
        gui.url.setText(link.getURL());

        gui.progress.progressProperty().bind(link.progressProperty());
        link.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                if (newValue == null) return;
                long l = link.getLength() == -1 ?
                        link.getDownloaded() : link.getLength();
                gui.percent.setText(
                        Utils.percent(link.getDownloaded(), l)
                                + " "
                                + Utils.fileLengthUnite(link.getDownloaded())
                                + " of "
                                + Utils.fileLengthUnite(l));
            }
        });

        gui.speed1.textProperty().bind(link.transferRateProperty());
        //pane3
        gui.speed2.textProperty().bind(link.transferRateProperty());

        link.stopCollectSpeedData();
        link.addData(0.0);
        gui.chart.getData().add(link.series);
        gui.ax.setLowerBound(link.upperBound - 60);
        gui.ax.setUpperBound(link.upperBound);

        //pane2
        gui.editSavePath.setText(link.getSaveto());
        gui.referrer.setText(link.getReferer());
//		gui.queue.getSelectionModel().select(Queue.getQueue(download.item.queue));
//		gui.category.getSelectionModel().select(Category.get(download.item.category));
        gui.referrer.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                link.setReferer(newValue);
            }
        });
        gui.savePath.textProperty().bind(gui.editSavePath.textProperty());


        gui.progress.progressProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                gui.ax.setLowerBound(link.upperBound - 60);
                gui.ax.setUpperBound(link.upperBound);

//				gui.timeLeft.setText("Time Left: " 
//						+ Utils.calTimeLeft(link.getTimeLeft()));
            }
        });
        gui.timeLeft.textProperty().bind(link.lefttimeProperty);
        gui.timeLeft.textProperty().bind(link.lefttimeProperty);

        gui.queue.setItems(Queue.getQueuesTree());
        gui.queue.getSelectionModel()
                .select(Queue.getQueue(link.getQueue()));
        gui.category.setItems(Category.getCategores());
        gui.category.getSelectionModel()
                .select(Category.get(link.getCategory()));

    }


    public static void bindItem(Link link, DownUi ui) {
        ui.progress.progressProperty().bind(link.progressProperty());

        ui.filename.setText(link.getFilename());
        ui.labelStatus.textProperty().bind(link.downStateProperty().asString());
        ui.labelAddress.setText(link.getURL());
        ui.labelFileSize.setText(Utils.fileLengthUnite(link.getLength()));

        ui.remining.textProperty().bind(link.remainingProperty());
        if (link.getItem().isUnknowLength()) {
            ui.labelResume.setText("You can try.");
        } else {
            ui.labelResume.setText("Yes.");
        }
        link.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                if (newValue == null) return;
                long l = link.getLength() == -1 ?
                        link.getDownloaded() : link.getLength();
                ui.labelDownloaded.setText(
                        Utils.fileLengthUnite(link.getDownloaded()) + " "
                                + Utils.percent(link.getDownloaded(), l)
                );
            }
        });


//		link.stateProperty().addListener((obv, old, value)->{
//			if(value == State.SUCCEEDED){
//				if(link.getDownloaded() == link.getLength()){
//					if(!link.getItem().isUnknowLength()){
//						ui.progress.setDoneText("Copy");
//					}
//					
//				}
//			}
//		});

        ui.labelTransferRate.textProperty().bind(link.transferRateProperty());
        ui.labelTransferRate2.textProperty().bind(link.transferRateProperty());

        link.addData(0d);
        ui.chart.getData().add(link.series);
        ui.xAxis.setLowerBound(link.upperBound - 60);
        ui.xAxis.setUpperBound(link.upperBound);

        ui.progress.progressProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                ui.xAxis.setLowerBound(link.upperBound - 60);
                ui.xAxis.setUpperBound(link.upperBound);

//				ui.labelTimeLeft.setText( Utils.calTimeLeft( link.getTimeLeft() ));
            }
        });
        ui.labelTimeLeft.textProperty().bind(link.lefttimeProperty);

        ui.textSaveTo.textProperty().bind(Bindings.concat(link.getSaveto()));


    }

}
