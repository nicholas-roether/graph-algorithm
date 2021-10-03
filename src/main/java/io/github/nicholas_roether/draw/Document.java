package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.draw.cursor.CursorManager;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * A document lies at the root of the application, housing the actual running process as well
 * as the central component registry and the central cursor manager. It sets some window and default
 * drawing settings, applies the cursor type from the cursor manager, and passes window events to the
 * component registry.
 * <br>
 * The document also contains the functionality for drawing to the screen, and as such, is the object
 * that is passed to all {@code draw()} calls.
 */
public abstract class Document extends PApplet {
	/**
	 * The title of this document. It will appear on the top bar of the window.
	 */
	public final String title;

	/**
	 * The icon of this document. It will appear on the top left of the window.
	 */
	public final PImage icon;

	/**
	 * The width of the window.
	 */
	private final int windowWidth;

	/**
	 * The height of the window.
	 */
	private final int windowHeight;

	/**
	 * The default draw state. Resets multiple values to their default settings when applied.
	 *
	 * @see DrawState
	 */
	private static final DrawState DEFAULT_DRAW_STATE = new DrawState(p -> {
		p.colorMode(RGB, 255);
		p.fill(0, 0, 0, 0);
		p.stroke(0, 0, 0, 0);
		p.strokeWeight(1);
		p.strokeJoin(MITER);
		p.strokeCap(ROUND);
		p.textAlign(LEFT, BASELINE);
		p.textSize(12);
		p.textMode(MODEL);
		p.rectMode(CORNER);
		p.ellipseMode(DIAMETER);
		p.blendMode(BLEND);
	});

	/**
	 * The central component registry of this document.
	 *
	 * @see ComponentRegistry
	 */
	private final ComponentRegistry componentRegistry;

	/**
	 * The central cursor manager of this document.
	 *
	 * @see CursorManager
	 */
	private final CursorManager cursorManager;

	public Document(int windowWidth, int windowHeight, String title) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.title = title;
		this.icon = null;
		this.componentRegistry = new ComponentRegistry(DEFAULT_DRAW_STATE);
		this.cursorManager = new CursorManager();
	}

	public Document(int windowWidth, int windowHeight, String title, PImage icon) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.title = title;
		this.icon = icon;
		this.componentRegistry = new ComponentRegistry(DEFAULT_DRAW_STATE);
		this.cursorManager = new CursorManager();
	}

	/**
	 * A callback method that can be used to apply additional settings to the PApplet,
	 * such as for example pixelDensity.
	 */
	protected void create() {}

	/**
	 * A callback that is called once before the first frame.
	 */
	protected void init() {}

	@Override
	public void settings() {
		size(windowWidth, windowHeight);
		create();
	}

	@Override
	public final void setup() {
		surface.setTitle(title);
		if (icon != null) surface.setIcon(icon);
		init();
		build(componentRegistry);
		super.setup();
	}

	/**
	 * A callback that registers all root components to the component registry.
	 *
	 * @param registry This document's component registry
	 */
	protected abstract void build(ComponentRegistry registry);

	/**
	 * A callback that is called on every frame.
	 */
	protected void frame() {}

	@Override
	public final void draw() {
		frame();
		// Get the cursor instructions
		instructCursor();
		// Set the background to black
		background(0);
		// Style the cursor to whichever instruction takes precedence
		cursor(cursorManager.getCurrentCursor());
		// Draw all registered components
		componentRegistry.draw(this);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Pass all window events to the component registry

	@Override
	public final void mousePressed(MouseEvent event) {
		componentRegistry.mousePressed(event);
	}

	@Override
	public final void mouseReleased(MouseEvent event) {
		componentRegistry.mouseReleased(event);
	}

	@Override
	public final void mouseClicked(MouseEvent event) {
		componentRegistry.mouseClicked(event);
	}

	@Override
	public final void mouseMoved(MouseEvent event) {
		componentRegistry.mouseMoved(event);
	}

	@Override
	public final void mouseDragged(MouseEvent event) {
		componentRegistry.mouseDragged(event);
	}

	@Override
	public final void mouseEntered(MouseEvent event) {
		componentRegistry.mouseEntered(event);
	}

	@Override
	public final void mouseExited(MouseEvent event) {
		componentRegistry.mouseExited(event);
	}

	@Override
	public final void mouseWheel(MouseEvent event) {
		componentRegistry.mouseWheel(event);
	}

	@Override
	public final void keyPressed(KeyEvent event) {
		componentRegistry.keyPressed(event);
	}

	@Override
	public final void keyReleased(KeyEvent event) {
		componentRegistry.keyReleased(event);
	}

	@Override
	public final void keyTyped(KeyEvent event) {
		componentRegistry.keyTyped(event);
	}

	@Override
	public final void focusGained() {
		componentRegistry.focusGained();
	}

	@Override
	public final void focusLost() {
		componentRegistry.focusLost();
	}

	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Resets the cursor manager and gets all current instructions from the component registry.
	 */
	private void instructCursor() {
		cursorManager.reset();
		cursorManager.addInstruction(-1, ARROW); // Default instruction for the ARROW cursor
		componentRegistry.instructCursor(cursorManager, mouseX, mouseY);
	}
}
