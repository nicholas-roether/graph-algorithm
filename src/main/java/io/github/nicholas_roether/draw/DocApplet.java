package io.github.nicholas_roether.draw;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public abstract class DocApplet extends PApplet {
	public final Document document;

	public DocApplet() {
		document = new Document();
	}

	protected abstract void populate();

	protected void frame() {}

	@Override
	public final void setup() {
		document.setup(this);
		this.populate();
	}

	@Override
	public final void draw() {
		this.frame();
		document.draw(this);
	}

	@Override
	public final void pause() {
		document.pause();
	}

	@Override
	public final void resume() {
		document.resume();
	}

	@Override
	public final void mousePressed(MouseEvent event) {
		document.mousePressed(event);
	}

	@Override
	public final void mouseReleased(MouseEvent event) {
		document.mouseReleased(event);
	}

	@Override
	public final void mouseClicked(MouseEvent event) {
		document.mouseClicked(event);
	}

	@Override
	public final void mouseDragged(MouseEvent event) {
		document.mouseDragged(event);
	}

	@Override
	public final void mouseMoved(MouseEvent event) {
		document.mouseMoved(event);
	}

	@Override
	public final void mouseWheel(MouseEvent event) {
		document.mouseWheel(event);
	}

	@Override
	public final void keyPressed(KeyEvent event) {
		document.keyPressed(event);
	}

	@Override
	public final void keyReleased(KeyEvent event) {
		document.keyReleased(event);
	}

	@Override
	public final void keyTyped(KeyEvent event) {
		document.keyTyped(event);
	}

	@Override
	public final void focusGained() {
		document.focusGained();
	}

	@Override
	public final void focusLost() {
		document.focusLost();
	}
}
