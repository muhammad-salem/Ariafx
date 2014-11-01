package aria.gui.fxml.imp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MovingStage {

	public static void  pikeToMoving(Stage stage, AnchorPane pane) {
		new MovingStage(stage, pane);
	}
	
	
	boolean moving;
	double x, y;

	/***
	 * create a new Stage and its AnchorPane 
	 * then assigns them to given stage and pane
	 * 
	 * @param stage
	 * @param pane
	 */
	public MovingStage(Stage stage, AnchorPane pane) {
		moveWithMouse(stage,pane);
		pane.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
//				stage.setWidth(pane.getWidth());
				stage.setHeight(pane.getHeight());
			}
		});
		pane.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				stage.setWidth(pane.getWidth());
//				stage.setHeight(pane.getHeight());
			}
		});
	}


	/**
	 * move stage over drag mouse
	 * 
	 * @param stage
	 *            which will move.
	 * @param pane
	 *            the root parent that will drag.
	 */
	public void moveWithMouse(final Stage stage, Pane pane) {

		// moving = false;
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
					moving = true;
					x = event.getSceneX();
					y = event.getSceneY();
				}
				// System.out.println("pressed "+ x +" "+ y + " "+ moving);
			}
		});

		pane.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (moving) {
					stage.setX(event.getScreenX() - x);
					stage.setY(event.getScreenY() - y);
					pane.setCursor(Cursor.HAND);
				}
				// System.out.println("draged "+ x +" "+ y + " "+ moving);
			}
		});

		pane.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				moving = false;
				pane.setCursor(Cursor.DEFAULT);
				// System.out.println("released "+ x +" "+ y + " "+ moving);
			}
		});

	}
}
