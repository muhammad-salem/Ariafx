package aria.tray.mouse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javafx.application.Platform;

public interface ActionListenerFX extends ActionListener {

	/**
	 * Invoked when an action occurs.
	 * @param e
	 */
	public void actionPerformedFX(ActionEvent e);
	
	@Override
	public default void actionPerformed(ActionEvent e){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				actionPerformedFX(e);
			}
		});
	}
}
