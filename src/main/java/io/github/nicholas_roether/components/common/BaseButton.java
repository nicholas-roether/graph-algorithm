package io.github.nicholas_roether.components.common;


import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.bounded.RectangularComponent;
import org.jetbrains.annotations.NotNull;
import processing.event.MouseEvent;

/**
 * The base class for all button components.
 *
 * @see ActionButton
 * @see ToggleButton
 */
public abstract class BaseButton extends RectangularComponent {
	/**
	 * The x-position of the top-left corner of the button.
	 */
	public final float x;

	/**
	 * The y-position of the top-left corner of the button.
	 */
	public final float y;

	/**
	 * The width of the button.
	 */
	public final float width;

	/**
	 * The height of the button.
	 */
	public final float height;

	/**
	 * The color of the button when it is in its default state.
	 */
	public final int baseColor;

	/**
	 * The color of the button while it is pressed.
	 */
	public final int pressedColor;

	/**
	 * Whether the button should be filled in with color while it is not pressed.
	 */
	public final boolean filled;

	/**
	 * Whether the button is currently pressed.
	 */
	private boolean pressed = false;

	/**
	 * Whether the mouse is currently hovering over the button.
	 */
	private boolean hover = false;

	/**
	 * Whether this button is disabled.
	 */
	private boolean disabled = false;

	/**
	 * Constructs a {@code BaseButton}.
	 *
	 * @param zIndex The z-index to display the button at.
	 * @param x The x-position of the top-left corner of the button
	 * @param y The y-position of the top-left corner of the button
	 * @param width The width of the button
	 * @param height The height of the button
	 * @param baseColor The color of the button in its default state
	 * @param pressedColor The color of the button in its pressed state
	 * @param filled Whether the button should be filled in while not pressed
	 */
	public BaseButton(int zIndex, float x, float y, float width, float height, int baseColor, int pressedColor, boolean filled) {
		super(zIndex);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.baseColor = baseColor;
		this.pressedColor = pressedColor;
		this.filled = filled;
	}

	/**
	 * Draws the contents of the button.
	 *
	 * @param p The document to draw on.
	 */
	protected abstract void drawLabel(Document p);

	/**
	 * Checks whether the button is currently pressed.
	 * @return {@code true} if the button is being pressed.
	 */
	public boolean isPressed() {
		return pressed;
	}

	/**
	 * Checks whether the mouse is currently hovering over the button.
	 *
	 * @return {@code true} if the button is being hovered over.
	 */
	public boolean isHovering() {
		return hover;
	}

	/**
	 * Checks whether the button is disabled.
	 * <br>
	 * Disabled buttons are greyed out and should no longer perform their functionality.
	 *
	 * @return {@code true} if the button is disabled.
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Enables or disables this button.
	 * <br>
	 * Disabled buttons are greyed out and should no longer perform their functionality.
	 *
	 * @param disabled Whether the button should be disabled
	 */
	public void setDisabled(boolean disabled) {
		if (!disabled) setPressed(false);
		this.disabled = disabled;
	}

	/**
	 * Sets whether the button is in its pressed state.
	 *
	 * @param pressed Whether this button is pressed
	 */
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void draw(@NotNull Document p) {
		/*
		If the button is disabled, fill it gray.
		If the button is being pressed, fill it with its pressed color.
		Otherwise, if filled is true, fill it with its base color, or if not, don't fill it at all.
		 */
		if (disabled) p.fill(150);
		else if (pressed) p.fill(pressedColor);
		else if (filled) p.fill(baseColor);
		else p.fill(0);

		if (!disabled) {
			/*
			If the button isn't disabled, add an outline to it that is derived from its main or pressed color.

			Specifically, if the button isn't being hovered over, the outline will either be its base color or its
			pressed color, depending on whether the button is in its pressed state.
			If the button is being hovered over, the outline will be the same as in the previous case, just
			a bit lighter. For details concerning the color lightening, see BaseButton.lightenColor().
			 */
			if (hover) {
				if (pressed) p.stroke(lightenColor(pressedColor));
				else p.stroke(lightenColor(baseColor));
			} else {
				if (pressed) p.stroke(pressedColor);
				else p.stroke(baseColor);
			}
		} else {
			// If the button is disabled, add a gray outline.
			p.stroke(150);
		}

		// Draw the rectangle underlying the button with an outline thickness of 2px, and a border radius of 5px.
		p.strokeWeight(2);
		p.rect(x, y, width, height, 5, 5, 5, 5);

		// Reset the draw state
		p.fill(0);
		p.stroke(0);
		p.strokeWeight(0);

		// Draw the label
		drawLabel(p);
	}

	@Override
	protected void mouseMovedAnywhere(MouseEvent event) {
		// If the mouse is within the bounds of the button, go into the hover state;
		// if not, then not.
		hover = checkInBounds(event.getX(), event.getY());
	}

	@Override
	protected int instructCursorInBounds() {
		// If the button isn't disabled, the cursor should be a hand while over it.
		if (!disabled)
			return HAND;
		return super.instructCursorInBounds();
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

	/**
	 * Lighten the given color by, if possible, 0x30.
	 *
	 * @param color The color to lighten.
	 * @return The lightened color.
	 */
	private static int lightenColor(int color) {
		// retrieve the ARGB parts of the color
		int alpha = (color & 0xFF000000) >> 24;
		int red = (color & 0x00FF0000) >> 16;
		int green = (color & 0x0000FF00) >> 8;
		int blue = (color & 0x000000FF);

		// Add 0x30, or 48 in decimal, to red, green and blue;
		// cap each at 0xFF, or 255 so that they can be properly recombined into an RGB (or ARGB) color.
		red = Math.min((red + 0x30), 0xFF);
		green = Math.min((green + 0x30), 0xFF);
		blue = Math.min((blue + 0x30), 0xFF);

		return (alpha << 24) + (red << 16) + (green << 8) + blue;
	}
}
