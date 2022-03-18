package ariafx.tray.mouse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javafx.application.Platform;

public interface ActionListenerFX extends ActionListener {

	/**
	 * Invoked when an action occurs.
	 * @param e
	 */
	void actionPerformedFX(ActionEvent e);
	
	@Override
	default void actionPerformed(ActionEvent e){
		Platform.runLater(() -> actionPerformedFX(e));
	}
}
