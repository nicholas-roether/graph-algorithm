package io.github.nicholas_roether.draw.bounded;

import io.github.nicholas_roether.draw.Component;
import processing.event.MouseEvent;

public abstract class BoundedComponent extends Component {
	public BoundedComponent() {
		super();
	}

	public BoundedComponent(int zIndex) {
		super(zIndex);
	}

	public abstract boolean checkInBounds(float x, float y);

	protected void mousePressedAnywhere(MouseEvent event) {}

	protected void mouseReleasedAnywhere(MouseEvent event) {}

	protected void mouseClickedAnywhere(MouseEvent event) {}

	protected void mouseMovedAnywhere(MouseEvent event) {}

	protected void mouseDraggedAnywhere(MouseEvent event) {}

	protected void mouseWheelAnywhere(MouseEvent event) {}

	protected int instructCursorAnywhere(float x, float y) {
		return NO_CURSOR_INSTRUCT;
	}

	protected void mousePressedInBounds(MouseEvent event) {}

	protected void mouseReleasedInBounds(MouseEvent event) {}

	protected void mouseClickedInBounds(MouseEvent event) {}

	protected void mouseMovedInBounds(MouseEvent event) {}

	protected void mouseDraggedInBounds(MouseEvent event) {}

	protected void mouseWheelInBounds(MouseEvent event) {}

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
