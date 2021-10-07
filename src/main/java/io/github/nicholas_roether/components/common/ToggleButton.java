package io.github.nicholas_roether.components.common;

import processing.event.MouseEvent;

public abstract class ToggleButton extends BaseButton {
	public ToggleButton(int zIndex, float x, float y, float width, float height, int baseColor) {
		super(zIndex, x, y, width, height, baseColor, baseColor, false);
	}

	@Override
	protected void mousePressedInBounds(MouseEvent event) {
		if (event.getButton() == LEFT)
			setPressed(!isPressed());
	}
}
