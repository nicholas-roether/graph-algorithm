package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.bounded.RectangularComponent;
import org.jetbrains.annotations.NotNull;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.regex.Pattern;

public class Input extends RectangularComponent {
	public static final float PADDING = 5;

	public final float x;
	public final float y;
	public final float width;
	public final float height;
	public final float contentWidth;
	public final float contentHeight;
	private final int textColor;
	private final Pattern allowedValues;

	private final StringBuilder displayMessageBuilder = new StringBuilder(255);

	private boolean focused = false;
	private StringBuilder value = new StringBuilder(255);

	private float anim = 0;

	public Input(int zIndex, float x, float y, float width, float height, String allowedValues, int textColor) {
		super(zIndex);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		contentWidth = width - 2 * PADDING;
		contentHeight = height - 2 * PADDING;
		this.textColor = textColor;
		this.allowedValues = Pattern.compile(allowedValues);
	}

	@Override
	public void frame(Document p) {
		if (focused)
			anim = (anim + 1 / p.frameRate) % 1;
	}

	@Override
	public void draw(@NotNull Document p) {
		p.stroke(0xFFA0A0A0);
		p.rect(x, y, width, height, 5, 5, 5, 5);

		displayMessageBuilder.setLength(0);
		displayMessageBuilder.append(value);
		boolean overflow = false;
		final float prefixLength = p.textWidth("...");
		final float suffixLength = p.textWidth("|");
//		while (prefixLength + p.textWidth(displayMessageBuilder.toString()) + suffixLength > contentWidth) {
//			overflow = true;
//			displayMessageBuilder.delete(0, 1);
//		}
		if (overflow)
			displayMessageBuilder.insert(0, "...");
		if (focused && anim < 0.5f)
			displayMessageBuilder.append("|");
		p.stroke(0);
		p.fill(textColor);
		p.textSize(contentHeight * 0.7f);
		p.text(displayMessageBuilder.toString(), x + PADDING, y + PADDING, contentWidth, contentHeight);
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		if (!this.focused && focused) anim = 0;
		this.focused = focused;
	}

	public String getValue() {
		return value.toString();
	}

	public void setValue(String value) {
		this.value.setLength(0);
		this.value.append(value);
	}

	@Override
	protected void mousePressedAnywhere(MouseEvent event) {
		setFocused(checkInBounds(event.getX(), event.getY()));
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if (focused) {
			if (event.getKey() == BACKSPACE) {
				if (!value.isEmpty())
					value.deleteCharAt(value.length() - 1);
			} else {
				value.append(event.getKey());
				if (!allowedValues.matcher(value).find())
					value.deleteCharAt(value.length() - 1);
			}
		}
	}

	@Override
	protected int instructCursorInBounds() {
		return TEXT;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}
}
