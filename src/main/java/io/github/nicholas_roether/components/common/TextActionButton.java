package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.Document;

public class TextActionButton extends ActionButton {
	public final String title;

	private final float textSize;
	private final int textColor;

	public TextActionButton(int zIndex, Runnable action, String title, float x, float y, float width, float height, int baseColor) {
		super(zIndex, action, x, y, width, height, baseColor);
		this.title = title;
		textSize = height * 0.5f;
		textColor = getTextColor(baseColor);
	}

	@Override
	protected void drawLabel() {
		p.fill(255);
		p.textAlign(CENTER);
		p.textSize(textSize);
		if (isDisabled()) p.fill(200);
		else p.fill(textColor);
		p.text(title, x + width / 2f, y + (height + textSize * 0.7f) / 2f);
	}

	private static int getTextColor(int bgColor) {
		final int red = (bgColor & 0x00FF0000) >> 16;
		final int green = (bgColor & 0x0000FF00) >> 8;
		final int blue = bgColor & 0x000000FF;
		return red + green + blue > 382 ? 0xFF000000 : 0xFFFFFFFF;
	}
}
