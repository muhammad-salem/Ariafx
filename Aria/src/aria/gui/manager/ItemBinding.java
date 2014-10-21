package aria.gui.manager;

import aria.core.download.Download;
import aria.core.download.Link;
import aria.core.url.type.Category;
import aria.core.url.type.Queue;
import aria.gui.fxml.Item2Gui;
import aria.gui.fxml.control.DownUi;
import aria.opt.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ItemBinding {

	public ItemBinding() {
		
	}
	
	public static void bindItem(Download download, Item2Gui gui)
	{
		//
//		gui.state.textProperty().bind(download.downStateProperty());
		
		//pane1
		gui.title.setText(download.item.getFilename());
		gui.state.textProperty().bind(download.downStateProperty().asString());		// change to downstate
		gui.url.setText(download.getURL());
		
		gui.progress.progressProperty().bind(download.progressProperty());
		download.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
				if(newValue == null) return;
				gui.percent.setText(Utils.percent(
						download.item.downloaded,download.item.length)
						+ " "
						+ Utils.fileLengthUnite(download.item.downloaded)
						+ " of "
						+ Utils.fileLengthUnite(download.item.length));
			}
		});

		gui.speed1.textProperty().bind(download.transferRateProperty());
		//pane3
		gui.speed2.textProperty().bind(download.transferRateProperty());
		
		download.canCollectSpeedData();
		download.addData(0.0);
		gui.chart.getData().add(download.series);
		gui.ax.setLowerBound(download.upperBound - 60);
		gui.ax.setUpperBound(download.upperBound );
		
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
		
		
		gui.progress.progressProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				gui.ax.setLowerBound(download.upperBound - 60);
				gui.ax.setUpperBound(download.upperBound );
				
				gui.timeLeft.setText("Time Left: " 
						+ Utils.calTimeLeft(download.item.timeLeft+System.nanoTime()));
			}});
		
		gui.queue.setItems(Queue.getQueuesTree());
		gui.queue.getSelectionModel()
			.select(Queue.getQueue(download.item.getQueue()));
		gui.category.setItems(Category.getCategores());
		gui.category.getSelectionModel()
			.select(Category.get(download.item.getCategory()));
		
	}
	
	
	public static void bindItem(Link link, Item2Gui gui)
	{
		//
//		gui.state.textProperty().bind(download.downStateProperty());
		
		//pane1
		gui.title.setText(link.getFilename());
		gui.state.textProperty().bind(link.downStateProperty().asString());			// change to downstate
		gui.url.setText(link.getURL());
		
		gui.progress.progressProperty().bind(link.progressProperty());
		link.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
				if(newValue == null) return;
				gui.percent.setText(
						Utils.percent(link.getDownloaded(),link.getLength())
						+ " "
						+ Utils.fileLengthUnite(link.getDownloaded())
						+ " of "
						+ Utils.fileLengthUnite(link.getLength()));
			}
		});

		gui.speed1.textProperty().bind(link.transferRateProperty());
		//pane3
		gui.speed2.textProperty().bind(link.transferRateProperty());
		
		link.stopCollectSpeedData();
		link.addData(0.0);
		gui.chart.getData().add(link.series);
		gui.ax.setLowerBound(link.upperBound - 60);
		gui.ax.setUpperBound(link.upperBound );
		
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
		
		
		gui.progress.progressProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				gui.ax.setLowerBound(link.upperBound - 60);
				gui.ax.setUpperBound(link.upperBound );
				
				gui.timeLeft.setText("Time Left: " 
						+ Utils.calTimeLeft(link.getTimeLeft()
								+System.nanoTime()));
			}});
		
		gui.queue.setItems(Queue.getQueuesTree());
		gui.queue.getSelectionModel()
			.select(Queue.getQueue(link.getQueue()));
		gui.category.setItems(Category.getCategores());
		gui.category.getSelectionModel()
			.select(Category.get(link.getCategory()));
		
	}
	
	
	public static void bindItem(Link link, DownUi ui){
		ui.progress.progressProperty().bind(link.progressProperty());
		ui.filename.setText(link.getFilename());
		ui.labelStatus.textProperty().bind(link.downStateProperty().asString());
		ui.labelAddress.setText(link.getURL());
		ui.labelFileSize.setText(Utils.fileLengthUnite(link.getLength()));
		ui.labelResume.setText("You can try,\n this opt not suporrted yet");
		link.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
				if(newValue == null) return;
				ui.labelDownloaded.setText(
						Utils.fileLengthUnite(link.getDownloaded())+ " "
						+ Utils.percent(link.getDownloaded(),link.getLength())
				  );
			}
		});
		
		ui.labelTransferRate.textProperty().bind(link.transferRateProperty());
		ui.labelTransferRate2.textProperty().bind(link.transferRateProperty());
		
		link.addData(0d);
		ui.chart.getData().add(link.series);
		ui.xAxis.setLowerBound(link.upperBound - 60);
		ui.xAxis.setUpperBound(link.upperBound );
		
		ui.progress.progressProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				ui.xAxis.setLowerBound(link.upperBound - 60);
				ui.xAxis.setUpperBound(link.upperBound );
				
				ui.labelTimeLeft.setText("Time Left: " 
						+ Utils.calTimeLeft(link.getTimeLeft()
								+System.nanoTime()));
		 }});
		
		ui.labelSaveTo.textProperty().bind(Bindings.concat(link.getSaveto()));
		
		
	}

}
