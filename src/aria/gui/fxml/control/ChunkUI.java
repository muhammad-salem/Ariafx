package aria.gui.fxml.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface ChunkUI {
	
	public StringProperty stateCodeProperty();
	public IntegerProperty idProperty();
	public StringProperty sizeProperty();
	public StringProperty doneProperty();
	
	
	public default String getStateCode() {
		return stateCodeProperty().get();
	}
	public default void setStateCode(String stateCode) {
		stateCodeProperty().set(stateCode);
	}
	
	public default int getId() {
		return idProperty().get();
	}
	public default void setId(int id ) {
		idProperty().set(id);
	}
	
	public default String getSize() {
		return sizeProperty().get();
	}
	public default void setSize(String size) {
		sizeProperty().set(size);
	}
	
	public default String getDone() {
		return doneProperty().get();
	}
	public default void setDone(String done) {
		doneProperty().set(done);
	}
	
	
	

}
