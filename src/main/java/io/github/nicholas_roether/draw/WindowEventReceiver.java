package io.github.nicholas_roether.draw;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * A WindowEventReceiver houses the necessary functions to recieve window events.
 * These include:
 * <ul>
 *     <li>Mouse events like movement, clicks and mouse wheel actions</li>
 *     <li>Key events like key presses</li>
 *     <li>Focus events, when the window gains or looses focus</li>
 * </ul>
 */
public interface WindowEventReceiver {
	/**
	 * An event listener called when a mouse button is pressed down.
	 *
	 * @param event The corresponding mouse event
	 */
	default void mousePressed(MouseEvent event) {}

	/**
	 * An event listener called when a mouse button is released.
	 *
	 * @param event The corresponding mouse event
	 */
	default void mouseReleased(MouseEvent event) {}

	/**
	 * An event listener called when a mouse button is clicked.
	 *
	 * @param event The corresponding mouse event
	 */
	default void mouseClicked(MouseEvent event) {}

	/**
	 * An event listener called when the mouse is moved.
	 *
	 * @param event The corresponding mouse event
	 */
	default void mouseMoved(MouseEvent event) {}

	/**
	 * An event listener called when the mouse is dragged, meaning moved while
	 * a mouse button is held down.
	 *
	 * @param event The corresponding mouse event
	 */
	default void mouseDragged(MouseEvent event) {}

	/**
	 * An event listener called when the mouse enters the window.
	 *
	 * @param event The corresponding mouse event
	 */
	default void mouseEntered(MouseEvent event) {}

	/**
	 * An event listener called when the mouse exits the window.
	 *
	 * @param event The corresponding mouse event
	 */
	default void mouseExited(MouseEvent event) {}

	/**
	 * An event listener called when the mouse wheel is moved.
	 *
	 * @param event The corresponding mouse event.
	 */
	default void mouseWheel(MouseEvent event) {}

	/**
	 * An event listener called when a key is pressed down.
	 *
	 * @param event The corresponding key event
	 */
	default void keyPressed(KeyEvent event) {}

	/**
	 * An event listener called when a key is released.
	 *
	 * @param event The corresponding key event
	 */
	default void keyReleased(KeyEvent event) {}

	/**
	 * An event listener called when a key is typed.
	 *
	 * @param event The corresponding key event
	 */
	default void keyTyped(KeyEvent event) {}

	/**
	 * An event listener called when the window gains focus.
	 */
	default void focusGained() {}

	/**
	 * An event listener called when the window loses focus.
	 */
	default void focusLost() {}
}
