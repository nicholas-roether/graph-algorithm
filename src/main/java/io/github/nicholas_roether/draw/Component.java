package io.github.nicholas_roether.draw;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Component<S> implements SketchElement, PConstants {
	protected static final List<Component<?>> NO_CHILDREN = List.of();

	private final List<Component<?>> children;
	private S state;
	private boolean stateChanged = true;

	private boolean mousePressed = false;
	private boolean mouseHovered = false;

	public Component() {
		children = new ArrayList<>();
	}

	public List<Component<?>> getChildren() {
		return Collections.unmodifiableList(children);
	}

	protected abstract boolean isInBounds(float x, float y);

	protected abstract List<Component<?>> build(PApplet proc);

	protected void onMousePressed(MouseEvent event) {}

	protected void onMouseReleased(MouseEvent event) {}

	protected void onMouseClicked(MouseEvent event) {}

	protected void onMouseMoved(MouseEvent event) {}

	protected void onMouseDragged(MouseEvent event) {}

	protected void onMouseEntered(MouseEvent event) {}

	protected void onMouseExited(MouseEvent event) {}

	protected void onMouseWheel(MouseEvent event) {}

	protected void onKeyPressed(KeyEvent event) {}

	protected void onKeyReleased(KeyEvent event) {}

	protected void onKeyTyped(KeyEvent event) {}

	protected void onFocusGained() {}

	protected void onFocusLost() {}

	protected void render(PApplet applet) {}


	protected S getState() {
		return this.state;
	}

	protected void setState(S newState, boolean updateWhenEqual) {
		if (!updateWhenEqual && state.equals(newState)) return;
		stateChanged = true;
		state = newState;
	}

	protected void setState(S newState) {
		setState(newState, false);
	}

	@Override
	public final void draw(PApplet proc) {
		if (stateChanged) {
			render(proc);
			updateChildren(proc);
			stateChanged = false;
		}
	}

	private void updateChildren(PApplet proc) {
		children.clear();
		children.addAll(build(proc));
		for (Component<?> child : children)
			child.setup(proc);
		for (Component<?> child : children)
			child.draw(proc);
	}

	@Override
	public final void mousePressed(MouseEvent event) {
		if (isInBounds(event.getX(), event.getY())) {
			mousePressed = true;
			onMousePressed(event);
			for (Component<?> child : children) child.mousePressed(event);
		}
	}

	@Override
	public final void mouseReleased(MouseEvent event) {
		if (mousePressed) {
			onMouseReleased(event);
			for (Component<?> child : children) child.mouseReleased(event);
		}
	}

	@Override
	public final void mouseClicked(MouseEvent event) {
		if (isInBounds(event.getX(), event.getY())) {
			onMouseClicked(event);
			for (Component<?> child : children) child.mouseClicked(event);
		}
	}

	@Override
	public final void mouseMoved(MouseEvent event) {
		if (isInBounds(event.getX(), event.getY())) {
			if (!mouseHovered) onMouseEntered(event);
			mouseHovered = true;
			onMouseMoved(event);
			for (Component<?> child : children) child.mouseMoved(event);
		} else {
			if (mouseHovered) onMouseExited(event);
			mouseHovered = false;
		}
	}

	@Override
	public final void mouseDragged(MouseEvent event) {
		if (isInBounds(event.getX(), event.getY())) {
			onMouseDragged(event);
			for (Component<?> child : children) child.mouseDragged(event);
		}
	}

	@Override
	public final void mouseEntered(MouseEvent event) {}

	@Override
	public final void mouseExited(MouseEvent event) {}

	@Override
	public final void mouseWheel(MouseEvent event) {
		if (isInBounds(event.getX(), event.getY())) {
			onMouseWheel(event);
			for (Component<?> child : children) child.mouseDragged(event);
		}
	}

	@Override
	public final void keyPressed(KeyEvent event) {
		onKeyPressed(event);
		for (Component<?> child : children) child.keyPressed(event);
	}

	@Override
	public final void keyReleased(KeyEvent event) {
		onKeyReleased(event);
		for (Component<?> child : children) child.keyReleased(event);
	}

	@Override
	public final void keyTyped(KeyEvent event) {
		onKeyTyped(event);
		for (Component<?> child : children) child.keyTyped(event);
	}

	@Override
	public final void focusGained() {
		onFocusGained();
		for (Component<?> child : children) child.focusGained();
	}

	@Override
	public final void focusLost() {
		onFocusLost();
		for (Component<?> child : children) child.focusLost();
	}
}
