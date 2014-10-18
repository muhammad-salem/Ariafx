package d.gui.fxml.imp;

import javafx.scene.control.ProgressBar;



public class ProgressStyled extends ProgressBar{


	public ProgressStyled(double progress) {
		super(progress);
		getStylesheets().add(getClass().getResource("progressPar.css").toExternalForm());
		getStyleClass().add(StyleProgress.DEFAULT.toString());
	}

	public ProgressStyled() {
		this(INDETERMINATE_PROGRESS);
		addStyleListener();
	}
	public ProgressStyled(StyleProgress styleProgress) {
		this();
		setStyleClass(styleProgress);
	}
	
	public void addStyleListener(){
		progressProperty().addListener((ob, old, newv)->{
			double value = newv == null ? 0 : newv.doubleValue();
			if(value > 0.2 && value < 0.4){
				setStyleClass(StyleProgress.WARNING);
			}else if(value > 0.4 && value < 0.6){
				setStyleClass(StyleProgress.INFO);
			}else if(value > 0.6 && value < 0.8){
				setStyleClass(StyleProgress.STRIPED);
			}else if(value > 0.8 && value < 1.0){
				setStyleClass(StyleProgress.SUCCESS);
			} 
		});
	}
	
	public void setStyleClass(StyleProgress style){
		getStyleClass().removeAll(StyleProgress.styleClasses);
		getStyleClass().add(style.toString());
	}

	public enum StyleProgress{
		DEFAULT,
		PRIMARY,
		STRIPED,
		DANGER,
		SUCCESS,
		INFO,
		WARNING;
		
		public static final String[] styleClasses = {
			DEFAULT.toString(), 
			PRIMARY.toString(), 
			STRIPED.toString(),
			DANGER.toString(), 
			SUCCESS.toString(), 
			INFO.toString(), 
			WARNING.toString() };
		
		@Override
		public String toString(){
				 if(this.equals(DEFAULT)) 	return "progress-default";
			else if(this.equals(PRIMARY)) 	return "progress-primary";
			else if(this.equals(STRIPED)) 	return "progress-striped";
			else if(this.equals(DANGER)) 	return "progress-danger";
			else if(this.equals(SUCCESS)) 	return "progress-success";
			else if(this.equals(INFO)) 		return "progress-info";
			else if(this.equals(WARNING)) 	return "progress-warning";
			return PRIMARY.toString();
		}
	}
	
}









