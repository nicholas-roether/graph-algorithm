package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.SimpleComponent;
import processing.core.PApplet;

public class Text extends SimpleComponent {
	public final String message;
	public final float x;
	public final float y;

	public Text(String message, float x, float y) {
		this.message = message;
		this.x = x;
		this.y = y;
	}

	@Override
	public void render(PApplet proc) {
		proc.text(message, x, y);
	}
}
