package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.util.EventSource;
import io.github.nicholas_roether.util.Notifier;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class Component implements SketchElement, PConstants {
	protected boolean mouseHover = false;
	protected boolean mouseDragging = false;
	protected Component parent = null;
	protected Document document = null;
	protected final List<Component> children;
	protected final EventSource<MouseEvent> mouseEventSource;
	protected final EventSource<KeyEvent> keyEventSource;
	protected final Notifier focusNotifier;
	protected final Notifier appNotifier;

	private boolean didSetup = false;

	public Component() {
		this.children = new ArrayList<>();
		mouseEventSource = new EventSource<>(
				"pressed",
				"released",
				"clicked",
				"dragged",
				"moved",
				"entered",
				"exited",
				"wheel"
		);
		keyEventSource = new EventSource<>(
				"pressed",
				"released",
				"typed"
		);
		focusNotifier = new Notifier("gained", "lost");
		appNotifier = new Notifier("pause", "resume");
	}

	public Component(Collection<? extends Component> children) {
		this.children = new ArrayList<>();
		mouseEventSource = new EventSource<>(
				"pressed",
				"released",
				"clicked",
				"dragged",
				"moved",
				"entered",
				"exited",
				"wheel"
		);
		keyEventSource = new EventSource<>(
				"pressed",
				"released",
				"typed"
		);
		focusNotifier = new Notifier("gained", "lost");
		appNotifier = new Notifier("pause", "resume");
		appendChildren(children);
	}

	public abstract boolean isContained(float x, float y);

	public abstract void render(PApplet proc);

	public void init(PApplet proc) {}

	public void onMousePress(MouseEvent event) {}

	public void onMouseRelease(MouseEvent event) {}

	public void onMouseClick(MouseEvent event) {}

	public void onMouseDrag(MouseEvent event) {}

	public void onMouseMove(MouseEvent event) {}

	public void onMouseEnter(MouseEvent event) {}

	public void onMouseExit(MouseEvent event) {}

	public void onMouseWheel(MouseEvent event) {}

	public void onKeyPress(KeyEvent event) {}

	public void onKeyRelease(KeyEvent event) {}

	public void onKeyType(KeyEvent event) {}

	public void onFocusGained() {}

	public void onFocusLost() {}

	public void onStart() {}

	public void onStop() {}

	@Override
	public final void setup(PApplet proc) {
		init(proc);
		onMouse("pressed", this::onMousePress);
		onMouse("released", this::onMouseRelease);
		onMouse("drag", this::onMouseDrag);
		onMouse("move", this::onMouseMove);
		onMouse("enter", this::onMouseEnter);
		onMouse("exit", this::onMouseExit);
		onMouse("wheel", this::onMouseWheel);
		onKey("pressed", this::onKeyPress);
		onKey("released", this::onKeyRelease);
		onKey("typed", this::onKeyType);
		onFocus("gained", this::onFocusGained);
		onFocus("lost", this::onFocusLost);
		for (Component child : children) {
			child.setDocument(document);
			child.setup(proc);
		}
		didSetup = true;
	}

	@Override
	public final void draw(PApplet proc) {
		render(proc);
		for (Component child : children) {
			if (!child.didSetup) {
				child.setDocument(document);
				child.setup(proc);
			};
			child.draw(proc);
		}
	}

	public final void appendChild(Component child) {
		children.add(child);
		onMouse("pressed", child::mousePressed);
		onMouse("released", child::mouseReleased);
		onMouse("clicked", child::mouseClicked);
		onMouse("dragged", child::mouseDragged);
		onMouse("moved", child::mouseMoved);
		onMouse("wheel", child::mouseWheel);
		onKey("pressed", child::keyPressed);
		onKey("released", child::keyReleased);
		onKey("typed", child::keyTyped);
		onFocus("gained", child::focusGained);
		onFocus("lost", child::focusLost);
		onApp("pause", child::pause);
		onApp("resume", child::resume);
		child.setDocument(document);
		child.setParent(parent);
	}

	public final void appendChildren(Collection<? extends Component> children) {
		for (Component child : children)
			appendChild(child);
	}

	public final void removeChild(Component child) {
		children.remove(child);
		removeMouseListener("pressed", child::mousePressed);
		removeMouseListener("released", child::mouseReleased);
		removeMouseListener("clicked", child::mouseClicked);
		removeMouseListener("dragged", child::mouseDragged);
		removeMouseListener("moved", child::mouseMoved);
		removeMouseListener("wheel", child::mouseWheel);
		removeKeyListener("pressed", child::keyPressed);
		removeKeyListener("released", child::keyReleased);
		removeKeyListener("typed", child::keyTyped);
		removeFocusListener("gained", child::focusGained);
		removeFocusListener("lost", child::focusLost);
		removeAppListener("pause", child::pause);
		removeAppListener("resume", child::resume);
		child.setDocument(null);
		child.setParent(null);
	}

	public final void removeChildren() {
		for (SketchElement child : children)
			unsubscribeChild(child);
		children.clear();
	}

	public final void replaceChildren(Collection<? extends Component> children) {
		removeChildren();
		appendChildren(children);
	}

	public final void onMouse(String event, Consumer<MouseEvent> listener) {
		mouseEventSource.on(event, listener);
	}

	public final void removeMouseListener(String event, Consumer<MouseEvent> listener) {
		mouseEventSource.removeListener(event, listener);
	}

	public final void onKey(String event, Consumer<KeyEvent> listener) {
		keyEventSource.on(event, listener);
	}

	public final void removeKeyListener(String event, Consumer<KeyEvent> listener) {
		keyEventSource.removeListener(event, listener);
	}

	public final void onFocus(String event, Runnable listener) {
		focusNotifier.on(event, listener);
	}

	public final void removeFocusListener(String event, Runnable listener) {
		focusNotifier.removeListener(event, listener);
	}

	public final void onApp(String event, Runnable listener) {
		appNotifier.on(event, listener);
	}

	public final void removeAppListener(String event, Runnable listener) {
		appNotifier.removeListener(event, listener);
	}

	@Override
	public final void mousePressed(MouseEvent event) {
		if (isContained(event.getX(), event.getY())) {
			mouseEventSource.dispatch("pressed", event);
			mouseDragging = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		mouseDragging = false;
		if (isContained(event.getX(), event.getY()))
			mouseEventSource.dispatch("released", event);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (isContained(event.getX(), event.getY()))
			mouseEventSource.dispatch("clicked", event);
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (mouseDragging)
			mouseEventSource.dispatch("dragged", event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		if (isContained(event.getX(), event.getY())) {
			mouseEventSource.dispatch("moved", event);
			if (!mouseHover) {
				mouseHover = true;
				mouseEventSource.dispatch("entered", event);
			}
		} else {
			mouseHover = false;
			mouseEventSource.dispatch("exited", event);
		}
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		if (isContained(event.getX(), event.getY()))
			mouseEventSource.dispatch("wheel", event);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		keyEventSource.dispatch("pressed", event);
	}

	@Override
	public void keyReleased(KeyEvent event) {
		keyEventSource.dispatch("released", event);
	}

	@Override
	public void keyTyped(KeyEvent event) {
		keyEventSource.dispatch("typed", event);
	}

	@Override
	public void focusGained() {
		focusNotifier.dispatch("gained");
	}

	@Override
	public void focusLost() {
		focusNotifier.dispatch("lost");
	}

	@Override
	public void pause() {
		appNotifier.dispatch("pause");
	}

	@Override
	public void resume() {
		appNotifier.dispatch("resume");
	}



	public void setParent(Component parent) {
		this.parent = parent;
	}

	public void setDocument(Document document) {
		this.document = document;
		for (Component child : children)
			child.setDocument(document);
	}

	private void unsubscribeChild(SketchElement child) {
		removeMouseListener("pressed", child::mousePressed);
		removeMouseListener("released", child::mouseReleased);
		removeMouseListener("clicked", child::mouseClicked);
		removeMouseListener("dragged", child::mouseDragged);
		removeMouseListener("moved", child::mouseMoved);
		removeMouseListener("wheel", child::mouseWheel);
		removeKeyListener("pressed", child::keyPressed);
		removeKeyListener("released", child::keyReleased);
		removeKeyListener("typed", child::keyTyped);
		removeFocusListener("gained", child::focusGained);
		removeFocusListener("lost", child::focusLost);
		removeAppListener("pause", child::pause);
		removeAppListener("resume", child::resume);
	}

	public static void makePointable(Component component) {
		component.onMouse("entered", event -> {
			if (component.document == null) {
				return;
			}
			component.document.setCursor(HAND);
		});
		component.onMouse("exited", event -> {
			if (component.document == null) {
				return;
			}
			component.document.setCursor(ARROW);
		});
	}
}
