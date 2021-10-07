package io.github.nicholas_roether.draw.bounded;

import io.github.nicholas_roether.draw.Component;
import processing.event.MouseEvent;

/**
 * A component that is bounded, meaning it can determine whether
 * a certain point lies inside or outside of it.
 * <br>
 * Bounded components distinguish for example between mouse events that occur
 * inside or outside them, and supply specialized handlers for these two cases.
 */
public abstract class BoundedComponent extends Component {
	public BoundedComponent() {
		super();
	}

	public BoundedComponent(int zIndex) {
		super(zIndex);
	}

	/**
	 * Check whether a given point lies within the bounds of this component.
	 *
	 * @param x The x-coordinate of the point to check
	 * @param y The y-coordinate of the point to check
	 * @return {@code true} if the point lies within the component's bounds
	 */
	public abstract boolean checkInBounds(float x, float y);

	/**
	 * An event handler called when the mouse is pressed anywhere on the screen.
	 *
	 * @param event The mouse event corresponding to the press
	 */
	protected void mousePressedAnywhere(MouseEvent event) {}

	/**
	 * An event handler called when the mouse is released anywhere on the screen.
	 *
	 * @param event The mouse event corresponding to the release
	 */
	protected void mouseReleasedAnywhere(MouseEvent event) {}

	/**
	 * An event handler called when the mouse is clicked anywhere on the screen.
	 *
	 * @param event The mouse event corresponding to the click
	 */
	protected void mouseClickedAnywhere(MouseEvent event) {}

	/**
	 * An event handler called when the mouse is moved anywhere on the screen.
	 *
	 * @param event The mouse event corresponding to the movement
	 */
	protected void mouseMovedAnywhere(MouseEvent event) {}

	/**
	 * An event handler called when the mouse is dragged, meaning moved while
	 * a mouse button is held, anywhere on the screen.
	 *
	 * @param event The mouse event corresponding to the dragging
	 */
	protected void mouseDraggedAnywhere(MouseEvent event) {}

	/**
	 * An event handler that is called when the mouse wheel is moved with the mouse
	 * positioned anywhere on the screen.
	 *
	 * @param event The mouse event corresponding to the wheel movement.
	 */
	protected void mouseWheelAnywhere(MouseEvent event) {}

	/**
	 * Returns the cursor instruction from this component if the mouse
	 * is anywhere on the screen.
	 *
	 * @param x The x-position of the mouse
	 * @param y The y-position of the mouse
	 * @return the cursor instruction
	 */
	protected int instructCursorAnywhere(float x, float y) {
		return NO_CURSOR_INSTRUCT;
	}

	/**
	 * An event handler that is called when the mouse is pressed within the
	 * bounds of the component.
	 *
	 * @param event The mouse event corresponding to the press
	 */
	protected void mousePressedInBounds(MouseEvent event) {}

	/**
	 * An event handler that is called when the mouse is released within the
	 * bounds of the component.
	 *
	 * @param event The mouse event corresponding to the release
	 */
	protected void mouseReleasedInBounds(MouseEvent event) {}

	/**
	 * An event handler that is called when the mouse is clicked within the
	 * bounds of the component.
	 *
	 * @param event The mouse event corresponding to the click
	 */
	protected void mouseClickedInBounds(MouseEvent event) {}

	/**
	 * An event handler that is called when the mouse is moved within the
	 * bounds of the component.
	 *
	 * @param event The mouse event corresponding to the movement.
	 */
	protected void mouseMovedInBounds(MouseEvent event) {}

	/**
	 * An event handler that is called when the mouse is dragged within the
	 * bounds of the component, meaning it is moved while a mouse button is held.
	 *
	 * @param event The mouse event corresponding to the dragging
	 */
	protected void mouseDraggedInBounds(MouseEvent event) {}

	/**
	 * An event handler that is called when the mouse wheel is moved while the
	 * mouse is positioned anywhere within the bounds of the component.
	 *
	 * @param event The mouse event corresponding to the wheel movement.
	 */
	protected void mouseWheelInBounds(MouseEvent event) {}

	/**
	 * Returns the cursor instructions for when the mouse is within the bounds
	 * of the component. This will take priority over the instructions of
	 * {@code instructCursorAnywhere()}, though if this function returns
	 * {@code NO_CURSOR_INSTRUCT}, the cursor will default to the instruction
	 * for outside the component.
	 *
	 * @return The cursor instruction for the inside of the component
	 */
	protected int instructCursorInBounds() {
		return NO_CURSOR_INSTRUCT;
	}

	@Override
	public final void mousePressed(MouseEvent event) {
		mousePressedAnywhere(event);
		if (checkInBounds(event.getX(), event.getY()))
			mousePressedInBounds(event);
	}

	@Override
	public final void mouseReleased(MouseEvent event) {
		mouseReleasedAnywhere(event);
		if (checkInBounds(event.getX(), event.getY()))
			mouseReleasedInBounds(event);
	}

	@Override
	public final void mouseClicked(MouseEvent event) {
		mouseClickedAnywhere(event);
		if (checkInBounds(event.getX(), event.getY()))
			mouseClickedInBounds(event);
	}

	@Override
	public final void mouseMoved(MouseEvent event) {
		mouseMovedAnywhere(event);
		if (checkInBounds(event.getX(), event.getY()))
			mouseMovedInBounds(event);
	}

	@Override
	public final void mouseDragged(MouseEvent event) {
		mouseDraggedAnywhere(event);
		if (checkInBounds(event.getX(), event.getY()))
			mouseDraggedInBounds(event);
	}

	@Override
	public final void mouseWheel(MouseEvent event) {
		mouseWheelAnywhere(event);
		if (checkInBounds(event.getX(), event.getY()))
			mouseWheelInBounds(event);
	}

	@Override
	public final int instructCursor(float x, float y) {
		if (checkInBounds(x, y)) {
			final int cursor = instructCursorInBounds();
			if (cursor != NO_CURSOR_INSTRUCT) return cursor;
		}
		return instructCursorAnywhere(x, y);
	}
}
