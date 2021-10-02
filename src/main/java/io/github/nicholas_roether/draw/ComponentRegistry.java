package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.draw.cursor.CursorManager;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

public class ComponentRegistry implements WindowEventReceiver, Drawable {
	public static final int NO_PARENT = -1;

	private final Map<Integer, ArrayList<Component>> componentMap;
	private final DrawState initialDrawState;
	private boolean didRebuild = true;
	private List<Component> cachedComponents;

	public ComponentRegistry(DrawState initialDrawState) {
		componentMap = new HashMap<>();
		this.initialDrawState = initialDrawState;
	}

	public void register(@NotNull Collection<? extends Component> components, int id) {
		components.forEach(component -> component.build(this));
		addComponents(components, id);
	}

	public void register(Component component, int id) {
		register(List.of(component), id);
	}

	public void draw(@NotNull PApplet p) {
		checkRebuild();
		getComponents().forEach(component -> {
			resetDrawState(p);
			component.draw(p);
		});
	}

	public void instructCursor(CursorManager manager, float x, float y) {
		getComponents().forEach(component -> {
			final int cursor = component.instructCursor(x, y);
			if (cursor != CursorManager.NO_CURSOR_INSTRUCT)
				manager.addInstruction(component.zIndex, cursor);
		});
	}

	@Override
	public void mousePressed(MouseEvent event) {
		getComponents().forEach(component -> component.mousePressed(event));
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		getComponents().forEach(component -> component.mouseReleased(event));
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		getComponents().forEach(component -> component.mouseClicked(event));
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		getComponents().forEach(component -> component.mouseMoved(event));
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		getComponents().forEach(component -> component.mouseMoved(event));
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		getComponents().forEach(component -> component.mouseEntered(event));
	}

	@Override
	public void mouseExited(MouseEvent event) {
		getComponents().forEach(component -> component.mouseExited(event));
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		getComponents().forEach(component -> component.mouseWheel(event));
	}

	@Override
	public void keyPressed(KeyEvent event) {
		getComponents().forEach(component -> component.keyPressed(event));
	}

	@Override
	public void keyReleased(KeyEvent event) {
		getComponents().forEach(component -> component.keyReleased(event));
	}

	@Override
	public void keyTyped(KeyEvent event) {
		getComponents().forEach(component -> component.keyTyped(event));
	}

	@Override
	public void focusGained() {
		getComponents().forEach(WindowEventReceiver::focusGained);
	}

	@Override
	public void focusLost() {
		getComponents().forEach(WindowEventReceiver::focusLost);
	}

	private void resetDrawState(PApplet p) {
		initialDrawState.apply(p);
	}

	private void rebuild(Component parent) {
		didRebuild = true;
		componentMap.remove(parent.id);
		parent.build(this);
	}

	private void checkRebuild() {
		boolean res = false;
		for (Component component : getComponents()) {
			if (component.shouldRebuild()) {
				rebuild(component);
				res = true;
			}
		}
		if (res) checkRebuild();
	}

	private void addComponents(Collection<? extends Component> components, int parentId) {
		final ArrayList<Component> componentList = componentMap.getOrDefault(parentId, new ArrayList<>());
		componentList.addAll(components);
		componentMap.put(parentId, componentList);
		didRebuild = true;
	}

	private List<Component> getComponents() {
		if (cachedComponents == null || didRebuild) {
			final List<Component> components = new ArrayList<>();
			componentMap.values().forEach(components::addAll);
			components.sort(Comparator.comparingInt(a -> a.zIndex));
			cachedComponents = Collections.unmodifiableList(components);
			didRebuild = false;
		}
		return cachedComponents;
	}
}
