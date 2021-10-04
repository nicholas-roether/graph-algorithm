package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

public class Background extends Component {
	public static final int Z_INDEX = 0;
	private boolean editing = false;

	public Background() {
		super(Z_INDEX);
	}

	@Override
	public void draw(@NotNull PApplet p) {
		if (editing) {
			p.stroke(30);
			p.strokeWeight(1);

			for (float x = 0; x < p.width; x += 30) {
				p.line(x, 0, x, p.height);
			}
			for (float y = 0; y < p.height; y += 30) {
				p.line(0, y, p.width, y);
			}
		}
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}
}
