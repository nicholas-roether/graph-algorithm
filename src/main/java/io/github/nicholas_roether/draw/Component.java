package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.draw.cursor.CursorManager;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.util.Objects;

/**
 * A component is a graphical element on the screen that has the following properties:
 * <ul>
 *     <li>It has a unique id.</li>
 *     <li>
 *         It can be registered to the central {@code ComponentRegistry}, making it
 *         automatically render on every frame, off of the default rendering settings.
 *     </li>
 *     <li>It can receive window events.</li>
 *     <li>It can register its own child components to the registry.</li>
 *     <li>It has a z-index, determining it's position in the rendering order.</li>
 *     <li>
 *         It can interact with the central {@code CursorManager}, the only coordinated way
 *         of setting the cursor type.
 *     </li>
 * </ul>
 *
 * @see Element
 * @see WindowEventReceiver
 * @see ComponentRegistry
 * @see CursorManager
 */
public abstract class Component extends Element implements WindowEventReceiver {
	/**
	 * Whether this component has been initialized.
	 */
	private boolean initialized = false;

	/**
	 * The cursor type to be specified when the component doesn't want to request any cursor type for itself.
	 */
	protected static final int NO_CURSOR_INSTRUCT = CursorManager.NO_CURSOR_INSTRUCT;

	/**
	 * The last id that was assigned to a component.
	 */
	private static int idCounter = 0;

	/**
	 * The components z-index. Components will be drawn on the screen in the ascending order
	 * of their z-indices, meaning components with a higher z-index will be rendered on top.
	 */
	public final int zIndex;

	/**
	 * The unique id of this component.
	 */
	public final int id;

	public Component() {
		this.zIndex = 0;
		id = nextId();
	}

	public Component(int zIndex) {
		this.zIndex = zIndex;
		id = nextId();
	}

	/**
	 * Initialize this component.
	 *
	 * @param p The {@code PApplet} corresponding to the app window.
	 */
	public final void setup(PApplet p) {
		init(p);
		initialized = true;
	}

	/**
	 * Called every frame before components are drawn to the screen.
	 */
	public void frame(float frameRate) {}

	/**
	 * Draw this component to the screen.
	 *
	 * @param p The {@code PApplet} to be drawn to.
	 */
	public void draw(@NotNull PApplet p) {}

	/**
	 * Register this component's child components.
	 *
	 * @param registry The global component registry
	 * @param p The PApplet corresponding to the application window
	 *
	 * @see ComponentRegistry
	 */
	public void build(ComponentRegistry registry, PApplet p) {}

	/**
	 * Returns whether the component's child components should be rebuilt. This function is
	 * called on every frame.
	 *
	 * @return {@code true} if the child components should be rebuilt.
	 */
	public boolean shouldRebuild() {
		return false;
	}

	/**
	 * Gives a cursor instruction. Returns the cursor type that this component requests, or,
	 * if this component doesn't want to request a specific cursor type, returns
	 * {@code NO_CURSOR_INSTRUCT}.
	 *
	 * @param x The x-position of the mouse
	 * @param y The y-position of the mouse
	 * @return the requested cursor type
	 */
	public int instructCursor(float x, float y) {
		return NO_CURSOR_INSTRUCT;
	}

	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * A function called before the component is built, meant for the
	 * component to be initialized according data provided by the PApplet.
	 *
	 * @param p The {@code PApplet} corresponding to the app window.
	 */
	protected void init(PApplet p) {}

	/**
	 * Returns the next unique id.
	 * @return the next unique id
	 */
	private int nextId() {
		return ++idCounter;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Component component = (Component) o;
		return id == component.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
