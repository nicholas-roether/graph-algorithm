package io.github.nicholas_roether.components.action_select;

import io.github.nicholas_roether.components.common.ToggleButton;
import io.github.nicholas_roether.draw.Document;
import processing.core.PImage;

/**
 * The delete button from the editing menu.
 */
public class DeleteButton extends ToggleButton {
	public static final int Z_INDEX = 10;
	private static PImage icon;

	public DeleteButton(float x, float y) {
		super(Z_INDEX, x, y, 40, 40, 0xFFA0A0A0);
	}

	@Override
	protected void init(Document p) {
		// load the icon if it hasn't already been loaded
		if (icon == null)
			icon = p.loadImage("delete_icon.png");
	}

	@Override
	protected void drawLabel(Document p) {
		// display the icon
		p.imageMode(CENTER);
		p.image(icon, x + width / 2, y + height / 2);
	}
}
