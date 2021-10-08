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
	public static final float PADDING = 7;

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
	 * The cursor position in the string.
	 */
	private int cursorIndex = 0;

	/**
	 * The point to which the selection spans from the cursor. If this is the same as {@code cursorIndex},
	 * no selection is occurring.
	 */
	private int selectionAnchor = 0;

	/**
	 * Whether the shift key is currently pressed, meaning pressing the arrow keys results in a selection.
	 */
	private boolean selecting = false;

	/**
	 * The progress within the blinking animation of the cursor. Since the animation has a period of one second,
	 * this value rises each frame from zero to one, and then starts over.
	 */
	private float cursorAnim = 0;

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
		contentWidth = width - 4 * PADDING;
		contentHeight = height - 2 * PADDING;
		this.textColor = textColor;
		this.allowedValues = Pattern.compile(allowedValues);
	}

	@Override
	public void frame() {
		/*
		If the input is focused, play the cursor animation.

		Each frame, the amount of time that has passed during that frame, in seconds, is added to the anim variable,
		modulo 1. This variable therefore counts up from 0 to 1 over and over, with a period of 1 second.
		 */
		if (focused)
			cursorAnim = (cursorAnim + 1 / p.frameRate) % 1;
	}

	@Override
	public void draw() {
		// Draw a grey, rectangular outline with a border radius of 5px.
		p.stroke(0xFFA0A0A0);
		p.rect(x, y, width, height, 5, 5, 5, 5);

		p.textSize(contentHeight * 0.7f);
		final float cursorX = x + 2 * PADDING + getOffsetToIndex(cursorIndex);
		final float cursorStartY = y + PADDING;
		final float cursorEndY = y + PADDING + contentHeight;

		// Draw the selection box if applicable.
		if (hasSelection()) {
			float selectionX = cursorX;
			if (selectionAnchor < cursorIndex)
				selectionX = x + 2 * PADDING + getOffsetToIndex(selectionAnchor);
			final float selectionWidth = p.textWidth(getSelection());
			p.strokeWeight(0);
			p.fill(0xFF3033FF);
			p.rect(selectionX, cursorStartY, selectionWidth, contentHeight);
		}

		// Draw the text.
		p.stroke(0);
		p.fill(textColor);
		p.text(value.toString(), x + 2 * PADDING, y + PADDING, contentWidth, contentHeight);

		// Draw the cursor if applicable.
		if (!hasSelection() && cursorAnim <= 0.5f) {
			p.stroke(textColor);
			p.strokeWeight(1);
			p.line(cursorX, cursorStartY, cursorX, cursorEndY);
		}
	}

	private float getOffsetToIndex(int index) {
		float offset = 0;
		if (!value.isEmpty()) {
			final String textBeforeIndex = value.substring(0, index);
			offset = p.textWidth(textBeforeIndex);
		}
		return offset;
	}

	private int getIndexForOffset(int index) {
		return 0;
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
		if (!this.focused && focused) cursorAnim = 0;
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

	/**
	 * Checks whether there is currently text selected in the input.
	 *
	 * @return {@code true} if text is selected
	 */
	public boolean hasSelection() {
		return cursorIndex != selectionAnchor;
	}

	public int getSelectionStart() {
		return Math.min(cursorIndex, selectionAnchor);
	}

	public int getSelectionEnd() {
		return Math.max(cursorIndex, selectionAnchor);
	}

	public String getSelection() {
		return value.substring(getSelectionStart(), getSelectionEnd());
	}

	@Override
	protected void mousePressedAnywhere(MouseEvent event) {
		// If the left mouse button is pressed inside the bounds of the input, focus it. If the left mouse button is
		// pressed outside the input, de-focus it.
		if (event.getButton() == LEFT)
			setFocused(checkInBounds(event.getX(), event.getY()));
	}

	private void deleteSelection() {
		value.delete(getSelectionStart(), getSelectionEnd());
		positionCursorAt(getSelectionStart(), false);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// If the input isn't focused, it isn't affected by key presses.
		if (!focused) return;
		switch (event.getKey()) {
			case ENTER -> {} // Ignore enter presses
			case BACKSPACE -> {
				// When backspace is pressed, if no text is selected, delete the last character before the cursor
				// and move the cursor back, if the cursor isn't at position 0.
				// If text is selected, delete the selected text.
				if (hasSelection()) {
					deleteSelection();
				} else if (cursorIndex > 0) {
					value.deleteCharAt(cursorIndex - 1);
					moveCursorBy(-1);
				}
			}
			case DELETE -> {
				// When delete is pressed, if no text is selected, delete the character at the cursor position,
				// if the cursor isn't at the last position.
				// If text is selected, delete the selected text.
				if (hasSelection()) {
					deleteSelection();
				} else if (cursorIndex < value.length())
					value.deleteCharAt(cursorIndex);
			}
			case CODED -> {
				switch (event.getKeyCode()) {
					case LEFT -> moveCursorBy(-1, selecting); // Move the cursor left
					case RIGHT -> moveCursorBy(1, selecting); // Move the cursor right
					case SHIFT -> selecting = true;
				}
			}
			default -> {
				// Save the current value
				final String prevValue = value.toString();
				// If text is selected, delete that text to replace it.
				deleteSelection();
				// Add the character to the value
				value.insert(cursorIndex, event.getKey());
				// Check if the new value is valid
				if (!allowedValues.matcher(value).find()) {
					// if not, reset the modifications.
					value.setLength(0);
					value.append(prevValue);
				} else {
					moveCursorBy(1); // else, keep them and advance the cursor by 1.
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == SHIFT) selecting = false;
	}

	public void positionCursorAt(int index, boolean selecting) {
		if (index < 0) index = 0;
		else if (index > value.length()) index = value.length();
		cursorIndex = index;
		cursorAnim = 0;
		if (!selecting) selectionAnchor = index;
	}

	public void positionCursorAt(int index) {
		positionCursorAt(index, false);
	}

	public void moveCursorBy(int amount, boolean selecting) {
		if (hasSelection() && !selecting) {
			selectionAnchor = cursorIndex;
			cursorAnim = 0;
		}
		else positionCursorAt(cursorIndex + amount, selecting);
	}

	public void moveCursorBy(int amount) {
		moveCursorBy(amount, false);
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
