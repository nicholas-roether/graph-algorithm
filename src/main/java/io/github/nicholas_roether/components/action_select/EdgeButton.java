package io.github.nicholas_roether.components.action_select;

import io.github.nicholas_roether.components.common.ToggleButton;
import processing.core.PApplet;

public class EdgeButton extends ToggleButton {
	public static final int Z_INDEX = 10;

	public EdgeButton(float x, float y) {
		super(Z_INDEX, x, y, 40, 40, 0xFFA0A0A0);
	}

	@Override
	protected void drawLabel(PApplet p) {
		p.stroke(100);
		p.strokeWeight(2);
		p.line(x + width * 0.2f, y + height * 0.8f, x + width * 0.8f, y + height * 0.2f);
	}
}
