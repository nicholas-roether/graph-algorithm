package io.github.nicholas_roether.components;

import io.github.nicholas_roether.components.common.ToggleButton;
import processing.core.PApplet;
import processing.core.PImage;

public class EditingButton extends ToggleButton {
	public static int Z_INDEX = 10;
	private static PImage icon;

	public EditingButton(float x, float y, float width, float height) {
		super(Z_INDEX, x, y, width, height, 0xFF456990);
	}

	@Override
	protected void init(PApplet p) {
		if (icon == null) {
			icon = p.loadImage("edit_icon.png");
		}
	}

	@Override
	protected void drawLabel(PApplet p) {
		p.imageMode(CENTER);
		p.image(icon, x + width / 2f, y + height / 2f);
	}
}
