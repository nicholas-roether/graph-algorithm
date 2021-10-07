package io.github.nicholas_roether.components.common;

import processing.event.MouseEvent;

public abstract class ActionButton extends BaseButton {
	private final Runnable action;

	public ActionButton(int zIndex, Runnable action, float x, float y, float width, float height, int baseColor) {
		super(zIndex, x, y, width, height, baseColor, baseColor, true);
		this.action = action;
	}

	@Override
	protected void mousePressedInBounds(MouseEvent event) {
		if (event.getButton() == LEFT && !isDisabled()) {
			setPressed(true);
			action.run();
		}
	}

	@Override
	protected void mouseReleasedAnywhere(MouseEvent event) {
		if (event.getButton() != LEFT)
			setPressed(false);
	}
}
