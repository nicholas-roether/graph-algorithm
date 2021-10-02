package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.draw.cursor.CursorManager;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;

public abstract class Document extends PApplet {
	public final String title;
	public final PImage icon;
	final int windowWidth;
	final int windowHeight;

	private static final DrawState DEFAULT_DRAW_STATE = new DrawState(p -> {
		p.colorMode(RGB, 255);
		p.fill(0, 0, 0, 0);
		p.stroke(0, 0, 0, 0);
		p.strokeWeight(1);
		p.strokeJoin(MITER);
		p.strokeCap(ROUND);
		p.textAlign(CENTER);
		p.textSize(12);
		p.textLeading(14);
		p.textMode(MODEL);
		p.rectMode(CORNER);
		p.ellipseMode(DIAMETER);
		p.blendMode(BLEND);
	});

	private final ComponentRegistry componentRegistry;
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

	protected void create() {}

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

	protected abstract void build(ComponentRegistry registry);

	protected void frame() {}

	@Override
	public final void draw() {
		frame();
		instructCursor();
		background(0);
		cursor(cursorManager.getCurrentCursor());
		componentRegistry.draw(this);
	}

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

	private void instructCursor() {
		cursorManager.reset();
		cursorManager.addInstruction(-1, ARROW);
		componentRegistry.instructCursor(cursorManager, mouseX, mouseY);
	}
}
