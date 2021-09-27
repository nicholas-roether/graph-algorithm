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

	@Override
	public void setup() {
		document.setup(this);
		this.populate();
	}

	@Override
	public void draw() {
		document.draw(this);
	}

	@Override
	public void pause() {
		document.pause();
	}

	@Override
	public void resume() {
		document.resume();
	}

	@Override
	public void mousePressed(MouseEvent event) {
		document.mousePressed(event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		document.mouseReleased(event);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		document.mouseClicked(event);
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		document.mouseDragged(event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		document.mouseMoved(event);
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		document.mouseWheel(event);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		document.keyPressed(event);
	}

	@Override
	public void keyReleased(KeyEvent event) {
		document.keyReleased(event);
	}

	@Override
	public void keyTyped(KeyEvent event) {
		document.keyTyped(event);
	}

	@Override
	public void focusGained() {
		document.focusGained();
	}

	@Override
	public void focusLost() {
		document.focusLost();
	}
}
