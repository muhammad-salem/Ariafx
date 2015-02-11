package aria.tray.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javafx.application.Platform;

public interface MouseListenerFX extends MouseListener {
	
	/**
	 * Invoked when a mouse button has been released on a component.
	 * 
	 * @param e {@link MouseEvent} awt object.
	 */
	public void mouseReleasedFX(MouseEvent e);
	
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	public void mousePressedFX(MouseEvent e);
	
	/**
	 * Invoked when the mouse exits a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	public void mouseExitedFX(MouseEvent e);
	
	/**
	 * Invoked when the mouse enters a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	public void mouseEnteredFX(MouseEvent e);
	
	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a component.
	 * @param e {@link MouseEvent} awt object.
	 */
	public void mouseClickedFX(MouseEvent e);
	
	
	@Override
	public default void mouseReleased(MouseEvent e) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mouseReleasedFX(e);
			}
		});
	}

	@Override
	public default void mousePressed(MouseEvent e) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mousePressedFX(e);
			}
		});

	}

	@Override
	public default void mouseExited(MouseEvent e) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mouseExitedFX(e);
			}
		});
	}

	@Override
	public default void mouseEntered(MouseEvent e) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mouseEnteredFX(e);
			}
		});
	}

	@Override
	public default void mouseClicked(MouseEvent e) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mouseClickedFX(e);
			}
		});
	}

}
