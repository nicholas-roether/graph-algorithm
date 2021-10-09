package io.github.nicholas_roether.draw;

import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

/**
 * Something that can be drawn to the screen.
 */
public interface Drawable {
	/**
	 * Draws this Drawable to the screen.
	 *
	 * @param p The Document to draw to.
	 */
	void draw(@NotNull Document p);
}
