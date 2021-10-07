package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.bounded.RectangularComponent;
import org.jetbrains.annotations.NotNull;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.regex.Pattern;

/**
 * A rudimentary text input.
 */
public class Input extends RectangularComponent {
	/**
	 * The amount of pixels between the text box and the border.
	 */
	public static final float PADDING = 5;

	/**
	 * The x-position of the top-left corner of the input.
	 */
	public final float x;

	/**
	 * The y-position of the top-left corner of the input.
	 */
	public final float y;

	/**
	 * The width of the input.
	 */
	public final float width;

	/**
	 * The height of the input.
	 */
	public final float height;

	/**
	 * The width of the text box within the input.
	 */
	public final float contentWidth;

	/**
	 * The height of the text box within the input.
	 */
	public final float contentHeight;

	/**
	 * The color the text inside the input has.
	 */
	private final int textColor;

	/**
	 * A regex pattern that all values that are typed into the input need to match.
	 */
	private final Pattern allowedValues;

	/**
	 * A string builder used each frame to build the text displayed on the screen.
	 */
	private final StringBuilder displayMessageBuilder = new StringBuilder(255);

	/**
	 * A string builder that stores the current value of the input, meaning the text that has
	 * been typed into it.
	 */
	private final StringBuilder value = new StringBuilder(255);

	/**
	 * Whether this input is currently focused, meaning key presses will
	 * cause it to update its value (i.e. it is being typed in).
	 */
	private boolean focused = false;

	/**
	 * The progress within the blinking animation of the cursor. Since the animation has a period of one second,
	 * this value rises each frame from zero to one, and then starts over.
	 */
	private float anim = 0;

	/**
	 * Constructs an {@code Input}.
	 *
	 * @param zIndex The z-index to display the input at
	 * @param x The x-position of the top-left corner of the input
	 * @param y The y-position of the top-left corner of the input
	 * @param width The width of the input
	 * @param height The height of the input
	 * @param allowedValues A regex string that all values that are typed into the input need to match
	 * @param textColor The color of the text in the input
	 */
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
		/*
		If the input is focused, play the cursor animation.

		Each frame, the amount of time that has passed during that frame, in seconds, is added to the anim variable,
		modulo 1. This variable therefore counts up from 0 to 1 over and over, with a period of 1 second.
		 */
		if (focused)
			anim = (anim + 1 / p.frameRate) % 1;
	}

	@Override
	public void draw(@NotNull Document p) {
		// Draw a grey, rectangular outline with a border radius of 5px.
		p.stroke(0xFFA0A0A0);
		p.rect(x, y, width, height, 5, 5, 5, 5);

		// Reset the displayMessageBuilder and fill it with the current value.
		displayMessageBuilder.setLength(0);
		displayMessageBuilder.append(value);

		// If the input is focused and the cursor animation is in its first half, add the cursor to the display message.
		if (focused && anim < 0.5f)
			displayMessageBuilder.append("|");

		// Draw the text.
		p.stroke(0);
		p.fill(textColor);
		p.textSize(contentHeight * 0.7f);
		p.text(displayMessageBuilder.toString(), x + PADDING, y + PADDING, contentWidth, contentHeight);
	}

	/**
	 * Checks whether this input is currently focused, meaning key presses will
	 * cause it to update its value (i.e. it is being typed in).
	 *
	 * @return {@code true} if the input is focused.
	 */
	public boolean isFocused() {
		return focused;
	}

	/**
	 * Set whether this input is focused, meaning key presses will
	 * 	 * cause it to update its value (i.e. it is being typed in).
	 *
	 * @param focused Whether this input is focused.
	 */
	public void setFocused(boolean focused) {
		if (!this.focused && focused) anim = 0;
		this.focused = focused;
	}

	/**
	 * Gets the current value of the input, meaning the text that has been typed into it.
	 *
	 * @return the current value of the input
	 */
	public String getValue() {
		return value.toString();
	}

	/**
	 * Sets the current value of the input, meaning the text that is typed into it.
	 *
	 * @param value the new value for the input
	 */
	public void setValue(String value) {
		this.value.setLength(0);
		this.value.append(value);
	}

	@Override
	protected void mousePressedAnywhere(MouseEvent event) {
		// If the left mouse button is pressed inside the bounds of the input, focus it. If the left mouse button is
		// pressed outside the input, de-focus it.
		if (event.getButton() == LEFT)
			setFocused(checkInBounds(event.getX(), event.getY()));
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// If the input is focused, it is affected by key presses.
		if (focused) {
			if (event.getKey() == ENTER) return;
			if (event.getKey() == BACKSPACE) {
				// If backspace was pressed, delete the last character of the value (if the value isn't empty).
				if (!value.isEmpty())
					value.deleteCharAt(value.length() - 1);
			} else {
				// Otherwise, append the new character to the value, check if the new value satisfies the
				// allowedValues regex, and keep the new character only if it does.
				value.append(event.getKey());
				if (!allowedValues.matcher(value).find())
					value.deleteCharAt(value.length() - 1);
			}
		}
	}

	@Override
	protected int instructCursorInBounds() {
		// The cursor should be in its TEXT form when hovering over the input.
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
