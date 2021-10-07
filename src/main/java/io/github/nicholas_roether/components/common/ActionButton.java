package io.github.nicholas_roether.components.common;

import processing.event.MouseEvent;

/**
 * A button that, when clicked, calls a callback function.
 */
public abstract class ActionButton extends BaseButton {
	/**
	 * The callback that is called when the button is clicked.
	 */
	private final Runnable action;

	/**
	 * Constructs an {@code ActionButton}.
	 *
	 * @param zIndex The z-index to display the button at
	 * @param action The callback to run when the button is pressed
	 * @param x The x-position of the top-left corner of the button
	 * @param y The y-position of the top-left corner of the button
	 * @param width The width of the button
	 * @param height The height of the button
	 * @param baseColor The color of the button
	 */
	public ActionButton(int zIndex, Runnable action, float x, float y, float width, float height, int baseColor) {
		super(zIndex, x, y, width, height, baseColor, baseColor, true);
		this.action = action;
	}

	@Override
	protected void mousePressedInBounds(MouseEvent event) {
		// If the left mouse button was pressed and the button isn't disabled, display it's pressed state and run
		// the callback.
		if (event.getButton() == LEFT && !isDisabled()) {
			setPressed(true);
			action.run();
		}
	}

	@Override
	protected void mouseReleasedAnywhere(MouseEvent event) {
		// If the left mouse button is no longer pressed, stop displaying the button's pressed state.
		if (event.getButton() != LEFT)
			setPressed(false);
	}
}
