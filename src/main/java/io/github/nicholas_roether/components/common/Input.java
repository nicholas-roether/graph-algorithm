package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.Animation;
import io.github.nicholas_roether.draw.bounded.RectangularComponent;
import io.github.nicholas_roether.utils.Utils;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
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
	 * The size of the text in the input in pixels.
	 */
	private final float textSize;

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
	 * Whether the control key is pressed.
	 */
	private boolean controlPressed = false;

	/**
	 * The blinking animation of the cursor. Since the animation has a period of one second,
	 * this value rises each frame from zero to one, and then starts over.
	 */
	private final Animation cursorAnim = new Animation(1.0, Animation.LINEAR, 0);

	/**
	 * The time at which the last mouse press occurred, for keeping track of double clicks.
	 */
	private float lastMouseClick = 0;

	/**
	 * The last text index that was clicked, for keeping track of double clicks.
	 */
	private int lastMouseIndex = -1;

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
		textSize = 0.7f * contentHeight;
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
			cursorAnim.step(1.0 / p.frameRate);
	}

	@Override
	public void draw() {
		// Draw a grey, rectangular outline with a border radius of 5px.
		p.stroke(0xFFA0A0A0);
		p.rect(x, y, width, height, 5, 5, 5, 5);

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
		p.textSize(textSize);
		p.stroke(0);
		p.fill(textColor);
		p.text(value.toString(), x + 2 * PADDING, y + PADDING, contentWidth, contentHeight);

		// Draw the cursor if applicable.
		if (focused && !hasSelection() && cursorAnim.getProgress() <= 0.5) {
			p.stroke(textColor);
			p.strokeWeight(1);
			p.line(cursorX, cursorStartY, cursorX, cursorEndY);
		}
	}

	private float getOffsetToIndex(int index) {
		p.textSize(textSize);
		float offset = 0;
		if (!value.isEmpty()) {
			final String textBeforeIndex = value.substring(0, index);
			offset = p.textWidth(textBeforeIndex);
		}
		return offset;
	}

	private int getIndexForOffset(float offset) {
		p.textSize(textSize);
		int bestIndex = -1;
		float bestDistance = 0;
		for (int i = 0; i <= value.length(); i++) {
			float distance = Math.abs(getOffsetToIndex(i) - offset);
			if (bestIndex == -1 || distance < bestDistance) {
				bestIndex = i;
				bestDistance = distance;
			} else if (distance > bestDistance) {
				break;
			}
		}
		return bestIndex;
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
		if (!this.focused && focused) cursorAnim.restart();
		else if (!focused) {
			positionCursorAt(0);
		}
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

	public void selectAll() {
		positionCursorAt(0);
		positionCursorAt(value.length(), true);
	}

	@Override
	protected void mousePressedAnywhere(MouseEvent event) {
		// If the left mouse button is pressed inside the bounds of the input, focus it. If the left mouse button is
		// pressed outside the input, de-focus it.
		if (event.getButton() == LEFT)
			setFocused(checkInBounds(event.getX(), event.getY()));
	}

	@Override
	protected void mousePressedInBounds(MouseEvent event) {
		if (event.getButton() != LEFT) return;
		final float offset = event.getX() - x - PADDING * 2;
		final int index = getIndexForOffset(offset);
		positionCursorAt(index);
		if (p.millis() - lastMouseClick <= 500 && index == lastMouseIndex)
			selectAll();
		lastMouseClick = p.millis();
		lastMouseIndex = index;
	}

	@Override
	protected void mouseDraggedAnywhere(MouseEvent event) {
		if (!focused || event.getButton() != LEFT) return;
		final float offset = event.getX() - x - PADDING * 2;
		positionCursorAt(getIndexForOffset(offset), true);
	}

	private void deleteSelection() {
		value.delete(getSelectionStart(), getSelectionEnd());
		positionCursorAt(getSelectionStart(), false);
	}

	private void copySelectionToClipboard() {
		if (!hasSelection()) return;
		final StringSelection selection = new StringSelection(getSelection());
		Utils.getClipboard().setContents(selection, selection);
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
					case CONTROL -> controlPressed = true;
				}
			}
			default -> {
				if (controlPressed) {
					switch (event.getKeyCode())  {
						// Ctrl+A (select all)
						case 65 -> selectAll();
						// Ctrl+C (copy)
						case 67 -> copySelectionToClipboard();
						// Ctrl+X (cut)
						case 88 -> {
							copySelectionToClipboard();
							deleteSelection();
						}
						// Ctrl+V (paste)
						case 86 -> {
							final String prevValue = value.toString();
							final int prevCursor = cursorIndex;
							try {
								final String clipboard = (String) Utils.getClipboard().getData(DataFlavor.stringFlavor);
								deleteSelection();
								value.insert(cursorIndex, clipboard);
								if (!allowedValues.matcher(value).find()) {
									setValue(prevValue);
									positionCursorAt(prevCursor);
								} else {
									moveCursorBy(clipboard.length());
								}
							} catch (UnsupportedFlavorException | IOException e) {
								setValue(prevValue);
								positionCursorAt(prevCursor);
							}
						}
					}
				} else {
					// Save the current value
					final String prevValue = value.toString();
					// If text is selected, delete that text to replace it.
					deleteSelection();
					// Add the character to the value
					value.insert(cursorIndex, event.getKey());
					// Check if the new value is valid
					if (!allowedValues.matcher(value).find()) {
						// if not, reset the modifications.
						setValue(prevValue);
					} else {
						moveCursorBy(1); // else, keep them and advance the cursor by 1.
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		switch (event.getKeyCode()) {
			case SHIFT -> selecting = false;
			case CONTROL -> controlPressed = false;
		}
	}

	public void positionCursorAt(int index, boolean selecting) {
		if (index < 0) index = 0;
		else if (index > value.length()) index = value.length();
		cursorIndex = index;
		cursorAnim.restart();
		if (!selecting) selectionAnchor = index;
	}

	public void positionCursorAt(int index) {
		positionCursorAt(index, false);
	}

	public void moveCursorBy(int amount, boolean selecting) {
		if (hasSelection() && !selecting) {
			if (amount < 0) positionCursorAt(getSelectionStart());
			else positionCursorAt(getSelectionEnd());
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
