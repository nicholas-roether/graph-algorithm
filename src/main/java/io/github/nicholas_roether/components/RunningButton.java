package io.github.nicholas_roether.components;

import io.github.nicholas_roether.components.common.ToggleButton;
import processing.core.PImage;

public class RunningButton extends ToggleButton {
	public static int Z_INDEX = 10;
	private static PImage playIcon;
	private static PImage pauseIcon;

	public RunningButton(float x, float y) {
		super(Z_INDEX, x, y, 80, 40, 0xFF4FF226);
	}

	@Override
	protected void init() {
		if (playIcon == null)
			playIcon = p.loadImage("play_icon.png");
		if (pauseIcon == null)
			pauseIcon = p.loadImage("pause_icon.png");
	}

	@Override
	protected void drawLabel() {
		PImage icon = isPressed() ? pauseIcon : playIcon;

		p.imageMode(CENTER);
		p.image(icon, x + width / 2f, y + height / 2f);
	}
}
