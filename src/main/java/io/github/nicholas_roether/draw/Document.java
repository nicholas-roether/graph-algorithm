package io.github.nicholas_roether.draw;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public abstract class Document extends PApplet {
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

	public Document() {
		this.componentRegistry = new ComponentRegistry(DEFAULT_DRAW_STATE);
	}

	protected void init() {}

	@Override
	public final void setup() {
		init();
		build(componentRegistry);
		super.setup();
	}

	protected abstract void build(ComponentRegistry registry);

	protected void frame() {}

	@Override
	public final void draw() {
		frame();
		background(0);
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
}
