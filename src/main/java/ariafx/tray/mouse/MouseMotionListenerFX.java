package ariafx.tray.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javafx.application.Platform;

public interface MouseMotionListenerFX extends MouseMotionListener {
	
	/**
	 * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
	 * @param e
	 */
	public void mouseMovedFX(MouseEvent e) ;
	
	/**
	 * 
	 * Invoked when a mouse button is pressed on a component and then dragged. MOUSE_DRAGGED 
	 * events will continue to be delivered to the component where the drag originated until 
	 * the mouse button is released (regardless of whether the mouse position is within the 
	 * bounds of the component).
	 * Due to platform-dependent Drag&Drop implementations, MOUSE_DRAGGED events may not be 
	 * delivered during a native Drag&Drop operation.
	 * @param e
	 */
	public void mouseDraggedFX(MouseEvent e);
	
	@Override
	public default void mouseMoved(MouseEvent e) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mouseMovedFX(e);
			}
		});
	}
	
	@Override
	public default void mouseDragged(MouseEvent e) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mouseDraggedFX(e);
			}
		});
	}
}
