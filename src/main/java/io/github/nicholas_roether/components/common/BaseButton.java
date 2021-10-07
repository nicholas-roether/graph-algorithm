package io.github.nicholas_roether.components.common;


import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.bounded.RectangularComponent;
import org.jetbrains.annotations.NotNull;
import processing.event.MouseEvent;

public abstract class BaseButton extends RectangularComponent {
	public final float x;
	public final float y;
	public final float width;
	public final float height;
	public final int baseColor;
	public final int pressedColor;
	public final boolean filled;

	private boolean pressed = false;
	private boolean hover = false;
	private boolean disabled = false;

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

	protected abstract void drawLabel(Document p);

	public boolean isPressed() {
		return pressed;
	}

	public boolean isHovering() {
		return hover;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		if (!disabled) setPressed(false);
		this.disabled = disabled;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void draw(@NotNull Document p) {
		if (disabled) p.fill(150);
		else if (pressed) p.fill(pressedColor);
		else if (filled) p.fill(baseColor);
		else p.fill(0);

		if (!disabled) {
			if (hover) {
				if (pressed) p.stroke(pressedColor + 0x00303030);
				else p.stroke(baseColor + 0x00303030);
			} else {
				if (pressed) p.stroke(pressedColor);
				else p.stroke(baseColor);
			}
		} else {
			p.stroke(150);
		}

		p.strokeWeight(2);
		p.rect(x, y, width, height, 5, 5, 5, 5);
		p.fill(0);
		p.stroke(0);
		p.strokeWeight(0);
		drawLabel(p);
	}

	@Override
	protected void mouseMovedAnywhere(MouseEvent event) {
		hover = checkInBounds(event.getX(), event.getY());
	}

	@Override
	protected int instructCursorInBounds() {
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
}
