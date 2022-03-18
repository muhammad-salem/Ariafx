package ariafx.tray.mouse;

import javafx.application.Platform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface ActionListenerFX extends ActionListener {

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    void actionPerformedFX(ActionEvent e);

    @Override
    default void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> actionPerformedFX(e));
    }
}
