package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.SimpleComponent;
import processing.core.PApplet;

public class Label extends SimpleComponent {
	public final String message;
	public final float x;
	public final float y;

	public Label(String message, float x, float y) {
		this.message = message;
		this.x = x;
		this.y = y;
	}

	@Override
	public void render(PApplet proc) {
		proc.textSize(15);
		proc.fill(255, 255, 255);
		proc.textAlign(CENTER);
		proc.text(message, x, y);
	}
}
