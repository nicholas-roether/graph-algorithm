package io.github.nicholas_roether.draw;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public interface SketchElement {
	default void setup(PApplet proc) {}

	void draw(PApplet proc);

	default void pause() {}

	default void resume() {}

	default void mousePressed(MouseEvent event) {}

	default void mouseReleased(MouseEvent event) {}

	default void mouseClicked(MouseEvent event) {}

	default void mouseDragged(MouseEvent event) {}

	default void mouseMoved(MouseEvent event) {}

	default void mouseEntered(MouseEvent event) {}

	default void mouseExited(MouseEvent event) {}

	default void mouseWheel(MouseEvent event) {}

	default void keyPressed(KeyEvent event) {}

	default void keyReleased(KeyEvent event) {}

	default void keyTyped(KeyEvent event) {}

	default void focusGained() {}

	default void focusLost() {}
}
