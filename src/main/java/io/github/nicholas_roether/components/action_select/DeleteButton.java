package io.github.nicholas_roether.components.action_select;

import io.github.nicholas_roether.components.common.ToggleButton;
import io.github.nicholas_roether.draw.Document;
import processing.core.PImage;

public class DeleteButton extends ToggleButton {
	public static final int Z_INDEX = 10;
	private static PImage icon;

	public DeleteButton(float x, float y) {
		super(Z_INDEX, x, y, 40, 40, 0xFFA0A0A0);
	}

	@Override
	protected void init(Document p) {
		icon = p.loadImage("delete_icon.png");
	}

	@Override
	protected void drawLabel(Document p) {
		p.imageMode(CENTER);
		p.image(icon, x + width / 2, y + height / 2);
	}
}
