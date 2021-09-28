package io.github.nicholas_roether.draw;

import processing.core.PApplet;

public class Document extends SimpleComponent {
	private int bgColor;
	private int cursor = ARROW;

	@Override
	public void init(PApplet proc) {
		bgColor = proc.color(0, 0, 0);
	}

	@Override
	public void render(PApplet proc) {
		proc.cursor(cursor);
		proc.background(bgColor);
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}
}
