package ariafx.gui.fxml.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

public interface ChunkUI {

    StringProperty stateCodeProperty();

    IntegerProperty idProperty();

    StringProperty sizeProperty();

    ReadOnlyStringProperty doneProperty();


    default String getStateCode() {
        return stateCodeProperty().get();
    }

    default void setStateCode(String stateCode) {
        stateCodeProperty().set(stateCode);
    }

    default int getId() {
        return idProperty().get();
    }

    default void setId(int id) {
        idProperty().set(id);
    }

    default String getSize() {
        return sizeProperty().get();
    }

    default void setSize(String size) {
        sizeProperty().set(size);
    }


}
