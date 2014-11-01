package aria.gui.fxml.imp;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;


public class ProgressCircle extends ProgressIndicator {
	
	public ProgressCircle() {
		this(INDETERMINATE_PROGRESS);
	}
	
	public ProgressCircle(double progress) {
		super(progress);
		getStylesheets().add(getClass().getResource("ProgressIndicator.css").toExternalForm());
		getStyleClass().add("progress-circle");
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new ProgressSkin(this);
	}
	
	String doneText = "Done";
	public void setDoneText(String processName) {
		doneText = processName + "\n  Done  ";
	}
	public String getDoneText(){
		return doneText;
	}
	
	
}
