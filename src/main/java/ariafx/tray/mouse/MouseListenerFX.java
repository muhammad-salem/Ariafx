package ariafx.tray.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javafx.application.Platform;

public interface MouseListenerFX extends MouseListener {
	
	/**
	 * Invoked when a mouse button has been released on a component.
	 * 
	 * @param e {@link MouseEvent} awt object.
	 */
	default void mouseReleasedFX(MouseEvent e){}
	
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	default void mousePressedFX(MouseEvent e){}
	
	/**
	 * Invoked when the mouse exits a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	default void mouseExitedFX(MouseEvent e){}
	
	/**
	 * Invoked when the mouse enters a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	default void mouseEnteredFX(MouseEvent e){}
	
	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	default void mouseClickedFX(MouseEvent e){}
	
	
	@Override
	default void mouseReleased(MouseEvent e) {
		Platform.runLater(() -> mouseReleasedFX(e));
	}

	@Override
	default void mousePressed(MouseEvent e) {
		Platform.runLater(() -> mousePressedFX(e));
	}

	@Override
	default void mouseExited(MouseEvent e) {
		Platform.runLater(() -> mouseExitedFX(e));
	}

	@Override
	default void mouseEntered(MouseEvent e) {
		Platform.runLater(() -> mouseEnteredFX(e));
	}

	@Override
	default void mouseClicked(MouseEvent e) {
		Platform.runLater(() -> mouseClickedFX(e));
	}

}
