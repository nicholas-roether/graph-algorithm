package io.github.nicholas_roether.draw;

import processing.core.PApplet;

public class Document extends SimpleComponent {
	private int bgColor;

	@Override
	public void init(PApplet proc) {
		bgColor = proc.color(0, 0, 0);
	}

	@Override
	public void render(PApplet proc) {
		proc.background(bgColor);
	}
}
