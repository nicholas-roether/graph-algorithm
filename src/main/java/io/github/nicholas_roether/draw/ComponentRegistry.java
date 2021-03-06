package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.draw.cursor.CursorManager;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

/**
 * The {@code ComponentRegistry} is responsible for keeping track of all the components
 * in the document, and the component hierarchy, meaning which components have which parent
 * components.
 * <br>
 * It also passes on window events to the registered components, as well as draw calls, and as
 * such, is responsible for keeping the component draw order.
 *
 * @see WindowEventReceiver
 * @see Drawable
 */
public class ComponentRegistry implements WindowEventReceiver {
	/**
	 * The value to use for the parent id in {@code register()} when the registered
	 * component is a root component, meaning it doesn't have a parent.
	 */
	public static final int NO_PARENT = -1;

	/**
	 * A map associating each component id with a list of their child components.
	 */
	private final Map<Integer, ArrayList<Component>> componentMap;

	/**
	 * The draw state to reset to before each render.
	 *
	 * @see DrawState
	 */
	private final DrawState initialDrawState;

	/**
	 * The PApplet that created this component registry.
	 */
	private final Document document;

	/**
	 * Whether the components were rebuilt and need to be re-sorted. True
	 * by default because the components should be sorted before the first frame.
	 */
	private boolean didRebuild = true;

	/**
	 * A cached list of the components sorted in rendering order so that they don't have
	 * to be re-sorted every frame.
	 */
	private List<Component> cachedComponents;

	public ComponentRegistry(DrawState initialDrawState, Document document) {
		componentMap = new HashMap<>();
		this.initialDrawState = initialDrawState;
		this.document = document;
	}

	/**
	 * Register the given components to the registry under the parent component with the given id.
	 * <br>
	 * Use {@code ComponentRegistry.NO_PARENT} for {@code id} when registering a root component.
	 *
	 * @param components The components to register
	 * @param id The id of the parent component
	 */
	public void register(@NotNull Collection<? extends Component> components, int id) {
		// build up all child components and let them register theirs in turn
		components.forEach(component -> {
			component.setup(document);
			component.build(this);
		});
		addComponents(components, id);
	}

	/**
	 * Register the given component to the registry under the parent component with the given id.
	 * <br>
	 * Use {@code ComponentRegistry.NO_PARENT} for {@code id} when registering a root component.
	 *
	 * @param component The component to register
	 * @param id The id of the parent component
	 */
	public void register(Component component, int id) {
		register(List.of(component), id);
	}

	public void draw() {
		// Call the frame callback for every component
		getComponents().forEach(Component::frame);
		// Check if any components need to be rebuilt
		checkRebuild();
		// Draw all components
		getComponents().forEach(component -> {
			resetDrawState(document);
			component.draw();
		});
	}

	/**
	 * Add all cursor instructions from all registered components to the given cursor manager.
	 *
	 * @param manager The cursor manager to add the instructions to
	 * @param x The mouse x-coordinate
	 * @param y The mouse y-coordinate
	 *
	 * @see CursorManager
	 */
	public void instructCursor(CursorManager manager, float x, float y) {
		getComponents().forEach(component -> {
			final int cursor = component.instructCursor(x, y);
			manager.addInstruction(component.zIndex, cursor);
		});
	}

	//------------------------------------------------------------------------------------------------------------------
	// Pass all window events to the registered components

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

	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Apply the initial draw state to the given PApplet.
	 * @param p The PApplet to apply the draw state to
	 */
	private void resetDrawState(PApplet p) {
		initialDrawState.apply(p);
	}

	/**
	 * Rebuild all children of the given parent component
	 *
	 * @param parent The parent component
	 */
	private void rebuild(Component parent) {
		remove(parent);
		parent.build(this);
	}

	/**
	 * Removes the given component and all it's children from the registry.
	 *
	 * @param component The component to remove
	 */
	private void remove(Component component) {
		// Force component re-sort on next call to getComponents()
		didRebuild = true;

		if (componentMap.get(component.id) != null)
			componentMap.get(component.id).forEach(this::remove);
		componentMap.remove(component.id);
	}

	/**
	 * Goes through all registered components and rebuilds them if necessary.
	 */
	private void checkRebuild() {
		for (Component component : getComponents()) {
			if (component.shouldRebuild()) {
				rebuild(component);
			}
		}
	}

	/**
	 * Add the given components to the component map.
	 *
	 * @param components The components to add
	 * @param parentId The id of the parent component
	 */
	private void addComponents(Collection<? extends Component> components, int parentId) {
		// Get the list of child components of the parent, or a new empty one if none are registered yet
		final ArrayList<Component> componentList = componentMap.getOrDefault(parentId, new ArrayList<>());
		// Add the new components
		componentList.addAll(components);
		componentMap.put(parentId, componentList);
		// Force component re-sort on next call to getComponents()
		didRebuild = true;
	}

	/**
	 * Returns all registered components in rendering order.
	 * @return all registered components in rendering order
	 */
	public List<Component> getComponents() {
		if (cachedComponents == null || didRebuild) {
			// If resorting is necessary, either because the cache is empty or the components were rebuilt,
			// sort them and store the sorted list in cachedComponents
			final List<Component> components = new ArrayList<>();
			componentMap.values().forEach(components::addAll);
			components.sort(Comparator.comparingInt(a -> a.zIndex));
			cachedComponents = Collections.unmodifiableList(components);
			didRebuild = false;
		}
		// Return the cached value
		return cachedComponents;
	}

	/**
	 * Returns all children of the given component.
	 *
	 * @param component The component whose children to get
	 * @return all children of the given component
	 */
	public List<Component> getChildren(Component component) {
		final List<Component> children = componentMap.get(component.id);
		if (children != null) return Collections.unmodifiableList(children);
		return List.of();
	}
}
