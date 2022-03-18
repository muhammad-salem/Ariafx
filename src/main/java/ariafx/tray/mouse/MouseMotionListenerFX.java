package ariafx.tray.mouse;

import javafx.application.Platform;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public interface MouseMotionListenerFX extends MouseMotionListener {

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     *
     * @param e
     */
    void mouseMovedFX(MouseEvent e);

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. MOUSE_DRAGGED
     * events will continue to be delivered to the component where the drag originated until
     * the mouse button is released (regardless of whether the mouse position is within the
     * bounds of the component).
     * Due to platform-dependent Drag&Drop implementations, MOUSE_DRAGGED events may not be
     * delivered during a native Drag&Drop operation.
     *
     * @param e
     */
    void mouseDraggedFX(MouseEvent e);

    @Override
    default void mouseMoved(MouseEvent e) {
        Platform.runLater(() -> mouseMovedFX(e));
    }

    @Override
    default void mouseDragged(MouseEvent e) {
        Platform.runLater(() -> mouseDraggedFX(e));
    }
}
